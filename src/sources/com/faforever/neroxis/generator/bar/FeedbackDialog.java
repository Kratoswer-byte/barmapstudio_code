package com.faforever.neroxis.generator.bar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Finestra per segnalare un errore o proporre un miglioramento. Il programma non invia nulla da solo:
 * prepara il testo (con i dettagli tecnici, se l'utente vuole, e sempre visibili prima dell'invio) e, quando
 * l'utente preme Invia, lo spedisce al servizio di moduli indicato in {@code feedback.properties}.
 *
 * <p>L'indirizzo di posta dell'autore non sta nel programma: lo conosce solo il servizio di moduli, che
 * inoltra il messaggio. Nel programma c'e' soltanto l'indirizzo del modulo.</p>
 */
final class FeedbackDialog extends JDialog {
    private static final Color ACCENT_CYAN = new Color(0, 229, 255);
    private static final Color ACCENT_GREEN = new Color(0, 255, 136);
    private static final Color TEXT_MUTED = new Color(140, 155, 180);
    private static final Color FAIL_RED = new Color(255, 107, 107);

    private final JComboBox<String> kind = new JComboBox<>(new String[]{I18n.t("fb.kind.bug"), I18n.t("fb.kind.idea")});
    private final JTextField summary = new JTextField(34);
    private final JTextArea text = new JTextArea(8, 44);
    private final JCheckBox withDetails = new JCheckBox(I18n.t("fb.details"), true);
    private final JTextArea details = new JTextArea(6, 44);
    private final JLabel state = new JLabel(" ");
    private final String endpoint;
    private final String accessKey;
    private final String page;
    private JButton sendBtn;

    private FeedbackDialog(Window owner) {
        super(owner, I18n.t("fb.title"), ModalityType.APPLICATION_MODAL);
        Properties config = new Properties();
        try (InputStream in = Files.newInputStream(AppPaths.appDir().resolve("feedback.properties"))) {
            config.load(new java.io.InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
        endpoint = config.getProperty("feedback_endpoint", "").trim();
        accessKey = config.getProperty("feedback_key", "").trim();
        page = config.getProperty("feedback_url", "").trim();

        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBorder(new EmptyBorder(18, 22, 16, 22));
        root.add(HostMatchDialog.wrapped(I18n.t("fb.intro"), 520, null), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 0, 4, 10);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        form.add(new JLabel(I18n.t("fb.kind")), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(kind, c);
        c.gridy = 1;
        c.gridx = 0;
        c.weightx = 0;
        form.add(new JLabel(I18n.t("fb.summary")), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(summary, c);
        c.gridy = 2;
        c.gridx = 0;
        c.weightx = 0;
        form.add(new JLabel(I18n.t("fb.text")), c);
        c.gridx = 1;
        c.weightx = 1;
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.putClientProperty("JTextArea.placeholderText", I18n.t("fb.text.bug"));
        text.setToolTipText(I18n.t("fb.text.bug"));
        form.add(new JScrollPane(text), c);
        c.gridy = 3;
        c.gridx = 1;
        withDetails.addActionListener(e -> details.setEnabled(withDetails.isSelected()));
        form.add(withDetails, c);
        c.gridy = 4;
        details.setEditable(false);
        details.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        details.setText(technicalDetails());
        details.setCaretPosition(0);
        form.add(new JScrollPane(details), c);
        root.add(form, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(0, 8));
        south.add(HostMatchDialog.wrapped(endpoint.isEmpty() ? I18n.t("fb.note.manual") : I18n.t("fb.note"), 520, TEXT_MUTED), BorderLayout.NORTH);
        state.setPreferredSize(new Dimension(520, 18));
        south.add(state, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton close = new JButton(I18n.t("fb.close"));
        close.addActionListener(e -> dispose());
        JButton copy = new JButton(I18n.t("fb.copy"));
        copy.addActionListener(e -> {
            if (ready()) {
                copyReport();
                say(I18n.t("fb.copied", saveReport()), ACCENT_GREEN);
            }
        });
        buttons.add(close);
        buttons.add(copy);
        if (!page.isEmpty()) {
            JButton open = MapStudioPanel.createPrimaryButton(I18n.t("fb.page"), ACCENT_CYAN);
            open.addActionListener(e -> openPage());
            buttons.add(open);
        }
        if (!endpoint.isEmpty()) {
            sendBtn = MapStudioPanel.createPrimaryButton(I18n.t("fb.send"), ACCENT_GREEN);
            sendBtn.addActionListener(e -> send());
            buttons.add(sendBtn);
        }
        south.add(buttons, BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setLocationRelativeTo(owner);
    }

    static void open(Window owner) {
        new FeedbackDialog(owner).setVisible(true);
    }

    private void say(String message, Color color) {
        state.setText(message);
        state.setToolTipText(message);
        state.setForeground(color);
    }

    private boolean ready() {
        if (text.getText().isBlank()) {
            say(I18n.t("fb.empty"), FAIL_RED);
            return false;
        }
        return true;
    }

    private String subject() {
        String title = summary.getText().trim();
        return "[BAR Map Studio " + AppPaths.VERSION + "] " + (kind.getSelectedIndex() == 0 ? "Bug" : "Idea")
            + (title.isEmpty() ? "" : ": " + title);
    }

    private String report(boolean full) {
        StringBuilder out = new StringBuilder();
        out.append(text.getText().trim()).append("\n");
        if (withDetails.isSelected()) {
            out.append("\n----\n").append(full ? details.getText() : shortDetails());
        }
        return out.toString();
    }

    private void copyReport() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(subject() + "\n\n" + report(true)), null);
    }

    /** Salva la segnalazione tra i dati dell'utente e torna il nome del file. */
    private String saveReport() {
        try {
            Path file = AppPaths.userFolder("feedback").resolve("feedback_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");
            Files.writeString(file, subject() + "\n\n" + report(true));
            return file.toString();
        } catch (Exception e) {
            return "-";
        }
    }

    /** Spedisce la segnalazione al servizio di moduli. Se non riesce, il testo resta copiato e salvato. */
    private void send() {
        if (!ready()) {
            return;
        }
        copyReport();
        saveReport();
        String title = subject();
        String body = report(true);
        sendBtn.setEnabled(false);
        say(I18n.t("fb.sending"), TEXT_MUTED);
        new Thread(() -> {
            boolean sent = false;
            try {
                StringBuilder json = new StringBuilder("{");
                if (!accessKey.isEmpty()) {
                    json.append("\"access_key\":").append(quote(accessKey)).append(',');
                }
                json.append("\"_subject\":").append(quote(title))
                    .append(",\"subject\":").append(quote(title))
                    .append(",\"name\":").append(quote("BAR Map Studio " + AppPaths.VERSION))
                    .append(",\"_captcha\":\"false\",\"_template\":\"box\"")
                    .append(",\"message\":").append(quote(body)).append('}');
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder(URI.create(endpoint))
                    .timeout(java.time.Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json.toString(), StandardCharsets.UTF_8))
                    .build();
                java.net.http.HttpResponse<String> response = java.net.http.HttpClient.newBuilder()
                    .connectTimeout(java.time.Duration.ofSeconds(15))
                    .followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
                    .build().send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
                String answer = response.body() == null ? "" : response.body().replace(" ", "");
                sent = response.statusCode() / 100 == 2 && !answer.contains("\"success\":false") && !answer.contains("\"success\":\"false\"");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Throwable ignored) {
            }
            boolean ok = sent;
            javax.swing.SwingUtilities.invokeLater(() -> {
                sendBtn.setEnabled(true);
                if (ok) {
                    text.setText("");
                    summary.setText("");
                    say(I18n.t("fb.sent"), ACCENT_GREEN);
                } else {
                    say(I18n.t("fb.send.failed"), FAIL_RED);
                }
            });
        }, "feedback").start();
    }

    /** Testo tra virgolette secondo le regole di JSON. */
    private static String quote(String value) {
        StringBuilder out = new StringBuilder("\"");
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> { }
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        return out.append('"').toString();
    }

    private void openPage() {
        if (!ready()) {
            return;
        }
        copyReport();
        saveReport();
        try {
            java.awt.Desktop.getDesktop().browse(URI.create(page));
            say(I18n.t("fb.page.done"), ACCENT_GREEN);
        } catch (Exception e) {
            say(I18n.t("fb.page.failed", page), FAIL_RED);
        }
    }

    private static String shortDetails() {
        Path engine = AppPaths.newestEngine();
        return "BAR Map Studio " + AppPaths.VERSION + "\n"
            + System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")\n"
            + "Java " + System.getProperty("java.version") + ", " + I18n.getLanguage() + "\n"
            + "BAR engine: " + (engine == null ? "-" : engine.getFileName()) + "\n";
    }

    /** Versione, sistema e le ultime righe del registro degli errori: tutto cio' che viene allegato. */
    private static String technicalDetails() {
        StringBuilder out = new StringBuilder(shortDetails());
        try {
            Path log = AppPaths.userDir().resolve("barmap-error.log");
            if (Files.isRegularFile(log)) {
                List<String> lines = Files.readAllLines(log, StandardCharsets.UTF_8);
                out.append("\nbarmap-error.log:\n");
                for (String line : lines.subList(Math.max(0, lines.size() - 30), lines.size())) {
                    out.append(line.length() > 200 ? line.substring(0, 200) : line).append("\n");
                }
            }
        } catch (Exception ignored) {
        }
        return out.toString();
    }
}

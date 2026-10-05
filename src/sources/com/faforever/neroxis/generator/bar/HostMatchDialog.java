package com.faforever.neroxis.generator.bar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Finestra "Partita con amici": chi ospita indica il proprio nome, gli amici e l'indirizzo a cui si collegano.
 * Da qui si apre anche la finestra dello spazio cloud in cui viene caricata la mappa.
 */
final class HostMatchDialog extends JDialog {
    private static final Preferences PREFS = Preferences.userNodeForPackage(HostMatchDialog.class).node("host");
    private static final Color ACCENT_AMBER = new Color(255, 159, 28);
    private static final Color ACCENT_CYAN = new Color(0, 229, 255);
    private static final Color ACCENT_GREEN = new Color(0, 255, 136);
    private static final Color TEXT_MUTED = new Color(140, 155, 180);
    private static final Color FAIL_RED = new Color(255, 107, 107);

    /** Cio' che l'utente ha scelto. Il primo giocatore e' chi ospita. */
    record Request(List<String> players, String password, String address) {
        List<String> friends() {
            return players.subList(1, players.size());
        }
    }

    private final int maxPlayers;
    private final JTextField hostName = new JTextField(PREFS.get("name", ""), 18);
    private final JTextArea friends = new JTextArea(PREFS.get("friends", ""), 4, 18);
    private final JTextField password = new JTextField(12);
    private final JTextField address = new JTextField(PREFS.get("address", ""), 18);
    private final JLabel cloudState = new JLabel();
    private final JLabel error = new JLabel(" ");
    private Request result;

    private HostMatchDialog(Window owner, int maxPlayers) {
        super(owner, I18n.t("host.title"), ModalityType.APPLICATION_MODAL);
        this.maxPlayers = maxPlayers;

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(new EmptyBorder(18, 22, 16, 22));
        root.add(wrapped(I18n.t("host.intro", String.valueOf(maxPlayers)), 470, null), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 0, 4, 10);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;
        addRow(form, c, row++, "host.name", hostName, null);
        addRow(form, c, row++, "host.friends", new JScrollPane(friends), null);
        addRow(form, c, row++, "host.password", password, null);
        JButton detect = new JButton(I18n.t("host.detect"));
        detect.setToolTipText(I18n.t("host.detect.tip"));
        detect.addActionListener(e -> detectAddress(detect));
        addRow(form, c, row++, "host.address", address, detect);
        JButton cloud = new JButton(I18n.t("cloud.button"));
        cloud.addActionListener(e -> {
            CloudDialog.open(this);
            refreshCloudState();
        });
        addRow(form, c, row++, "host.cloud", cloudState, cloud);
        address.setToolTipText(I18n.t("host.address.tip"));
        friends.setToolTipText(I18n.t("host.friends.tip"));
        password.setToolTipText(I18n.t("host.password.tip"));
        root.add(form, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(0, 8));
        south.add(wrapped(I18n.t("host.note", String.valueOf(com.faforever.neroxis.exporter.bar.BarMapExporter.HOST_PORT)), 470, TEXT_MUTED), BorderLayout.NORTH);
        error.setForeground(FAIL_RED);
        south.add(error, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton cancel = new JButton(I18n.t("host.cancel"));
        cancel.addActionListener(e -> dispose());
        JButton start = MapStudioPanel.createPrimaryButton(I18n.t("host.start"), ACCENT_AMBER);
        start.addActionListener(e -> confirm());
        buttons.add(cancel);
        buttons.add(start);
        south.add(buttons, BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH);

        refreshCloudState();
        setContentPane(root);
        getRootPane().setDefaultButton(start);
        pack();
        setLocationRelativeTo(owner);
    }

    /** Apre la finestra e torna la scelta, o null se l'utente annulla. */
    static Request show(Window owner, int maxPlayers) {
        HostMatchDialog dialog = new HostMatchDialog(owner, maxPlayers);
        dialog.setVisible(true);
        return dialog.result;
    }

    private static void addRow(JPanel form, GridBagConstraints c, int row, String labelKey, JComponent field, JComponent extra) {
        c.gridy = row;
        c.gridx = 0;
        c.weightx = 0;
        form.add(new JLabel(I18n.t(labelKey)), c);
        c.gridx = 1;
        c.weightx = 1;
        c.gridwidth = extra == null ? 2 : 1;
        form.add(field, c);
        c.gridwidth = 1;
        if (extra != null) {
            c.gridx = 2;
            c.weightx = 0;
            form.add(extra, c);
        }
    }

    static JLabel wrapped(String text, int width, Color color) {
        JLabel label = new JLabel("<html><div style='width:" + width + "px'>" + text + "</div></html>");
        if (color != null) {
            label.setForeground(color);
        }
        return label;
    }

    private void refreshCloudState() {
        CloudStorage.Settings settings = CloudStorage.load();
        cloudState.setText(settings.complete() ? I18n.t("host.cloud.ready", settings.bucket()) : I18n.t("host.cloud.missing"));
        cloudState.setForeground(settings.complete() ? ACCENT_GREEN : FAIL_RED);
    }

    /** Chiede a un servizio pubblico con quale indirizzo questo PC esce su internet. Solo su richiesta dell'utente. */
    private void detectAddress(JButton button) {
        button.setEnabled(false);
        new Thread(() -> {
            String found = null;
            try {
                HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.ipify.org")).timeout(Duration.ofSeconds(10)).GET().build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                String body = response.body() == null ? "" : response.body().trim();
                if (response.statusCode() == 200 && JoinFileWriter.HOST.matcher(body).matches()) {
                    found = body;
                }
            } catch (Exception ignored) {
            }
            String shown = found;
            SwingUtilities.invokeLater(() -> {
                button.setEnabled(true);
                if (shown != null) {
                    address.setText(shown);
                    error.setText(" ");
                } else {
                    error.setText(I18n.t("host.detect.failed"));
                }
            });
        }, "public-address").start();
    }

    private void confirm() {
        String name = hostName.getText().trim();
        Set<String> names = new LinkedHashSet<>();
        names.add(name);
        for (String line : friends.getText().split("[\\r\\n,;]+")) {
            if (!line.isBlank()) {
                if (!names.add(line.trim())) {
                    error.setText(I18n.t("host.error.duplicate", line.trim()));
                    return;
                }
            }
        }
        for (String player : names) {
            if (!JoinFileWriter.NAME.matcher(player).matches()) {
                error.setText(I18n.t("host.error.name", player.isEmpty() ? "?" : player));
                return;
            }
        }
        if (names.size() < 2) {
            error.setText(I18n.t("host.error.nofriends"));
            return;
        }
        if (names.size() > maxPlayers) {
            error.setText(I18n.t("host.error.toomany", String.valueOf(maxPlayers)));
            return;
        }
        String pass = password.getText().trim();
        if (!JoinFileWriter.PASSWORD.matcher(pass).matches()) {
            error.setText(I18n.t("host.error.password"));
            return;
        }
        String where = address.getText().trim();
        if (!JoinFileWriter.HOST.matcher(where).matches()) {
            error.setText(I18n.t("host.error.address"));
            return;
        }
        if (!CloudStorage.load().complete()) {
            error.setText(I18n.t("host.cloud.missing"));
            return;
        }
        List<String> players = new ArrayList<>(names);
        PREFS.put("name", name);
        PREFS.put("friends", String.join("\n", players.subList(1, players.size())));
        PREFS.put("address", where);
        result = new Request(players, pass, where);
        dispose();
    }

    /** Impostazioni dello spazio cloud, con la prova del collegamento. */
    static final class CloudDialog extends JDialog {
        private final JTextField endpoint = new JTextField(30);
        private final JTextField region = new JTextField(30);
        private final JTextField bucket = new JTextField(30);
        private final JTextField accessKey = new JTextField(30);
        private final JPasswordField secretKey = new JPasswordField(30);
        private final JTextField publicUrl = new JTextField(30);
        private final JLabel state = new JLabel(" ");

        private CloudDialog(Window owner) {
            super(owner, I18n.t("cloud.title"), ModalityType.APPLICATION_MODAL);
            CloudStorage.Settings saved = CloudStorage.load();
            endpoint.setText(saved.endpoint());
            region.setText(saved.region());
            bucket.setText(saved.bucket());
            accessKey.setText(saved.accessKey());
            secretKey.setText(saved.secretKey());
            publicUrl.setText(saved.publicUrl());

            JPanel root = new JPanel(new BorderLayout(0, 12));
            root.setBorder(new EmptyBorder(18, 22, 16, 22));
            root.add(wrapped(I18n.t("cloud.intro"), 520, null), BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 0, 4, 10);
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            int row = 0;
            addRow(form, c, row++, "cloud.endpoint", endpoint, null);
            addRow(form, c, row++, "cloud.region", region, null);
            addRow(form, c, row++, "cloud.bucket", bucket, null);
            addRow(form, c, row++, "cloud.accessKey", accessKey, null);
            addRow(form, c, row++, "cloud.secretKey", secretKey, null);
            addRow(form, c, row++, "cloud.publicUrl", publicUrl, null);
            endpoint.setToolTipText(I18n.t("cloud.endpoint.tip"));
            region.setToolTipText(I18n.t("cloud.region.tip"));
            publicUrl.setToolTipText(I18n.t("cloud.publicUrl.tip"));
            root.add(form, BorderLayout.CENTER);

            JPanel south = new JPanel(new BorderLayout(0, 8));
            south.add(wrapped(I18n.t("cloud.note"), 520, TEXT_MUTED), BorderLayout.NORTH);
            state.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
            state.setPreferredSize(new Dimension(520, 36));
            south.add(state, BorderLayout.CENTER);
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            JButton test = new JButton(I18n.t("cloud.test"));
            test.addActionListener(e -> test(test));
            JButton cancel = new JButton(I18n.t("host.cancel"));
            cancel.addActionListener(e -> dispose());
            JButton save = MapStudioPanel.createPrimaryButton(I18n.t("cloud.save"), ACCENT_CYAN);
            save.addActionListener(e -> {
                CloudStorage.Settings settings = read();
                if (settings != null) {
                    CloudStorage.save(settings);
                    dispose();
                }
            });
            buttons.add(test);
            buttons.add(cancel);
            buttons.add(save);
            south.add(buttons, BorderLayout.SOUTH);
            root.add(south, BorderLayout.SOUTH);

            setContentPane(root);
            pack();
            setLocationRelativeTo(owner);
        }

        static void open(Window owner) {
            new CloudDialog(owner).setVisible(true);
        }

        /** Le impostazioni scritte nei campi, o null (con il motivo a schermo) se non sono utilizzabili. */
        private CloudStorage.Settings read() {
            CloudStorage.Settings settings = new CloudStorage.Settings(endpoint.getText().trim(), region.getText().trim(), bucket.getText().trim(),
                accessKey.getText().trim(), new String(secretKey.getPassword()).trim(), publicUrl.getText().trim());
            if (!settings.complete()) {
                status(I18n.t("cloud.error.missing"), FAIL_RED);
                return null;
            }
            if (!CloudStorage.SAFE_URL.matcher(settings.publicUrl()).matches()) {
                status(I18n.t("cloud.error.publicUrl"), FAIL_RED);
                return null;
            }
            return settings;
        }

        private void status(String text, Color color) {
            state.setText("<html><div style='width:510px'>" + text + "</div></html>");
            state.setForeground(color);
        }

        private void test(JButton button) {
            CloudStorage.Settings settings = read();
            if (settings == null) {
                return;
            }
            button.setEnabled(false);
            status(I18n.t("cloud.test.running"), TEXT_MUTED);
            new Thread(() -> {
                String failure = null;
                try {
                    CloudStorage.test(settings);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    failure = e.toString();
                } catch (Exception e) {
                    failure = e.getMessage() == null || e.getMessage().isBlank() ? e.toString() : e.getMessage();
                }
                String problem = failure;
                SwingUtilities.invokeLater(() -> {
                    button.setEnabled(true);
                    if (problem == null) {
                        status(I18n.t("cloud.test.ok"), ACCENT_GREEN);
                    } else {
                        status(I18n.t("cloud.test.failed", problem.replace("<", "&lt;")), FAIL_RED);
                    }
                });
            }, "cloud-test").start();
        }
    }
}

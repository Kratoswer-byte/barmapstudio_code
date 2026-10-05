package com.faforever.neroxis.generator.bar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Aggiornamenti tramite le "release" di un progetto GitHub.
 *
 * <p>All'avvio il programma chiede a GitHub qual e' l'ultima release del progetto indicato in
 * {@code update.properties}. Se e' piu' recente della versione in uso lo segnala; l'utente decide se
 * scaricarla. L'installer scaricato viene avviato solo se la sua impronta SHA-256 coincide con quella
 * pubblicata da GitHub per quel file; altrimenti si apre la pagina della release nel browser.</p>
 */
final class UpdateChecker {
    private static final Pattern REPO = Pattern.compile("[A-Za-z0-9_.-]{1,60}/[A-Za-z0-9_.-]{1,100}");
    private static final Color ACCENT_GREEN = new Color(0, 255, 136);
    private static final Color TEXT_MUTED = new Color(140, 155, 180);
    private static final Color FAIL_RED = new Color(255, 107, 107);

    /** Una release piu' recente della versione in uso. */
    record Release(String version, String notes, String pageUrl, String assetName, String assetUrl, String sha256, long size) {
        boolean installable() {
            return Platform.isWindows() && assetUrl != null && sha256 != null;
        }
    }

    private UpdateChecker() {}

    /** Il progetto GitHub ("utente/progetto") da cui arrivano gli aggiornamenti, o null se il controllo e' spento. */
    static String repository() {
        Properties config = new Properties();
        try (InputStream in = Files.newInputStream(AppPaths.appDir().resolve("update.properties"))) {
            config.load(new java.io.InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
        if ("false".equalsIgnoreCase(config.getProperty("update_check", "true").trim())) {
            return null;
        }
        String repo = System.getProperty("barstudio.update.repo", config.getProperty("update_repo", "")).trim();
        return REPO.matcher(repo).matches() ? repo : null;
    }

    /** Controlla in un altro thread; chiama {@code whenNewer} (nel thread dell'interfaccia) solo se c'e' una versione nuova. */
    static void checkAsync(Consumer<Release> whenNewer) {
        String repo = repository();
        if (repo == null) {
            return;
        }
        Thread thread = new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.github.com/repos/" + repo + "/releases/latest"))
                    .timeout(Duration.ofSeconds(20))
                    .header("Accept", "application/vnd.github+json")
                    .header("User-Agent", "BARMapStudio")
                    .GET().build();
                HttpResponse<String> response = client().send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    return; // nessuna release pubblicata, progetto privato o limite di richieste: si riprova al prossimo avvio
                }
                Release release = parse(response.body(), repo);
                if (release != null && isNewer(release.version(), AppPaths.VERSION)) {
                    SwingUtilities.invokeLater(() -> whenNewer.accept(release));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Throwable ignored) {
                // senza rete il programma funziona come sempre
            }
        }, "update-check");
        thread.setDaemon(true);
        thread.start();
    }

    private static HttpClient client() {
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).followRedirects(HttpClient.Redirect.NORMAL).build();
    }

    /** Legge la risposta di GitHub. Accetta solo indirizzi del progetto indicato. */
    static Release parse(String json, String repo) {
        JsonNode root = new ObjectMapper().readTree(json);
        String tag = root.path("tag_name").asString("");
        if (tag.isBlank() || root.path("draft").asBoolean(false)) {
            return null;
        }
        String base = "https://github.com/" + repo + "/";
        String page = root.path("html_url").asString("");
        if (!page.toLowerCase(Locale.ROOT).startsWith(base.toLowerCase(Locale.ROOT))) {
            page = base + "releases";
        }
        String assetName = null;
        String assetUrl = null;
        String sha256 = null;
        long size = 0;
        for (JsonNode asset : root.path("assets")) {
            String name = asset.path("name").asString("");
            String url = asset.path("browser_download_url").asString("");
            String digest = asset.path("digest").asString("");
            if (name.toLowerCase(Locale.ROOT).endsWith(".exe") && name.toLowerCase(Locale.ROOT).contains("setup")
                && name.matches("[A-Za-z0-9._-]{1,120}")
                && url.toLowerCase(Locale.ROOT).startsWith(base.toLowerCase(Locale.ROOT) + "releases/download/")) {
                assetName = name;
                assetUrl = url;
                size = asset.path("size").asLong(0);
                sha256 = digest.matches("sha256:[0-9a-fA-F]{64}") ? digest.substring(7).toLowerCase(Locale.ROOT) : null;
                break;
            }
        }
        String notes = root.path("body").asString("").replace("\r", "").trim();
        return new Release(tag.replace('-', ' ').replaceFirst("^[vV](?=\\d)", ""), notes, page, assetName, assetUrl, sha256, size);
    }

    /** Confronta i numeri di due versioni ("beta 0.2" contro "beta 0.1", "1.0" contro "beta 0.9"). */
    static boolean isNewer(String candidate, String current) {
        List<Integer> a = numbers(candidate);
        List<Integer> b = numbers(current);
        if (a.isEmpty()) {
            return false;
        }
        for (int i = 0; i < Math.max(a.size(), b.size()); i++) {
            int x = i < a.size() ? a.get(i) : 0;
            int y = i < b.size() ? b.get(i) : 0;
            if (x != y) {
                return x > y;
            }
        }
        // stessi numeri: la versione definitiva viene dopo la beta
        return current.toLowerCase(Locale.ROOT).contains("beta") && !candidate.toLowerCase(Locale.ROOT).contains("beta");
    }

    private static List<Integer> numbers(String version) {
        List<Integer> out = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\d{1,6}").matcher(version);
        while (matcher.find()) {
            out.add(Integer.parseInt(matcher.group()));
        }
        return out;
    }

    /** Finestra che presenta la nuova versione e, se l'utente vuole, la scarica e avvia l'installer. */
    static void showDialog(Window owner, Release release) {
        JDialog dialog = new JDialog(owner, I18n.t("upd.title"), java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBorder(new EmptyBorder(18, 22, 16, 22));

        JLabel headline = new JLabel(I18n.t("upd.headline", release.version(), AppPaths.VERSION));
        headline.setFont(BarMapStudio.fontCond(Font.BOLD, 16));
        headline.setForeground(ACCENT_GREEN);
        root.add(headline, BorderLayout.NORTH);

        JTextArea notes = new JTextArea(release.notes().isBlank() ? I18n.t("upd.nonotes") : release.notes(), 12, 54);
        notes.setEditable(false);
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);
        notes.setCaretPosition(0);
        root.add(new JScrollPane(notes), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(0, 8));
        JLabel state = new JLabel(release.installable()
            ? I18n.t("upd.note", String.format(Locale.ROOT, "%.0f", release.size() / 1048576.0)) : I18n.t("upd.note.page"));
        state.setForeground(TEXT_MUTED);
        south.add(state, BorderLayout.NORTH);
        JProgressBar bar = new JProgressBar(0, 1000);
        bar.setVisible(false);
        bar.setPreferredSize(new Dimension(520, 10));
        south.add(bar, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton later = new JButton(I18n.t("upd.later"));
        later.addActionListener(e -> dialog.dispose());
        JButton page = new JButton(I18n.t("upd.page"));
        page.addActionListener(e -> browse(release.pageUrl(), state));
        buttons.add(later);
        buttons.add(page);
        if (release.installable()) {
            JButton install = MapStudioPanel.createPrimaryButton(I18n.t("upd.install"), ACCENT_GREEN);
            install.addActionListener(e -> {
                install.setEnabled(false);
                later.setEnabled(false);
                bar.setVisible(true);
                state.setForeground(TEXT_MUTED);
                state.setText(I18n.t("upd.downloading"));
                new Thread(() -> download(release, bar, state, install, later), "update-download").start();
            });
            buttons.add(install);
        }
        south.add(buttons, BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private static void browse(String url, JLabel state) {
        try {
            java.awt.Desktop.getDesktop().browse(URI.create(url));
        } catch (Exception e) {
            state.setForeground(FAIL_RED);
            state.setText(url);
        }
    }

    private static void download(Release release, JProgressBar bar, JLabel state, JButton install, JButton later) {
        String problem = null;
        Path file = AppPaths.userFolder("updates").resolve(release.assetName());
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(release.assetUrl()))
                .timeout(Duration.ofMinutes(30)).header("User-Agent", "BARMapStudio").GET().build();
            HttpResponse<InputStream> response = client().send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() != 200) {
                throw new java.io.IOException("HTTP " + response.statusCode());
            }
            long total = Math.max(1, release.size());
            try (InputStream in = response.body(); OutputStream out = Files.newOutputStream(file)) {
                byte[] buffer = new byte[1 << 16];
                long done = 0;
                for (int read; (read = in.read(buffer)) > 0; ) {
                    out.write(buffer, 0, read);
                    done += read;
                    int value = (int) Math.min(1000, done * 1000 / total);
                    SwingUtilities.invokeLater(() -> bar.setValue(value));
                }
            }
            // l'installer parte solo se e' esattamente il file pubblicato nella release
            if (!release.sha256().equals(CloudStorage.sha256(file))) {
                Files.deleteIfExists(file);
                problem = I18n.t("upd.badfile");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            problem = I18n.t("upd.failed");
        } catch (Throwable e) {
            problem = I18n.t("upd.failed");
        }
        String failure = problem;
        SwingUtilities.invokeLater(() -> {
            if (failure != null) {
                state.setForeground(FAIL_RED);
                state.setText(failure);
                install.setEnabled(true);
                later.setEnabled(true);
                bar.setVisible(false);
                return;
            }
            try {
                new ProcessBuilder("cmd.exe", "/c", "start", "", file.toString()).start();
                System.exit(0);
            } catch (Exception e) {
                state.setForeground(FAIL_RED);
                state.setText(I18n.t("upd.runfailed", file.toString()));
                later.setEnabled(true);
            }
        });
    }
}

package com.faforever.neroxis.generator.bar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * La base del programma: la finestra principale, le impostazioni dell'utente, la cartella di BAR in uso
 * e l'avvio del motore del gioco. Le schede dello Studio si appoggiano a questa classe.
 */
final class StudioShell {
    private static final String SETTINGS_FILE = "settings.properties";
    private static final Color GREEN = new Color(34, 197, 94);
    private static final Color RED = new Color(239, 68, 68);

    final Path home = AppPaths.userDir();
    final JFrame frame = new JFrame();
    /** Stato di BAR mostrato nel piede: testo, colore (verde = trovato) e dettagli nel suggerimento. */
    final JLabel barStatus = new JLabel();
    private final Properties settings = new Properties();
    private Path barData;

    StudioShell() {
        loadSettings();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Path saved = null;
        String remembered = settings.getProperty("bar.data", "").trim();
        if (!remembered.isEmpty()) {
            try {
                Path path = Path.of(remembered);
                if (BarLocator.isData(path)) {
                    saved = path;
                }
            } catch (RuntimeException ignored) {
            }
        }
        if (saved == null) {
            saved = BarLocator.find(remembered.isEmpty() ? null : remembered);
        }
        barData = saved != null ? saved : BarLocator.defaultData();
        AppPaths.setBarData(barData);
        updateBarStatus();
        writeSplashPicture();
    }

    // ------------------------------------------------------------------ impostazioni

    private Path settingsPath() {
        return home.resolve(SETTINGS_FILE);
    }

    private void loadSettings() {
        Path file = settingsPath();
        try {
            if (Files.isRegularFile(file)) {
                try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    settings.load(reader);
                }
                return;
            }
            // prima versione: le impostazioni stavano in un file con un altro nome; si portano qui solo quelle dello Studio
            Path old = home.resolve("launcher.properties");
            if (Files.isRegularFile(old)) {
                Properties previous = new Properties();
                try (Reader reader = Files.newBufferedReader(old, StandardCharsets.ISO_8859_1)) {
                    previous.load(reader);
                }
                for (String key : new String[]{"bar.data", "display", "window", "hide_specs_on_start"}) {
                    if (previous.getProperty(key) != null) {
                        settings.setProperty(key, previous.getProperty(key));
                    }
                }
                saveSettings();
            }
        } catch (Exception ignored) {
        }
    }

    private void saveSettings() {
        try {
            Files.createDirectories(home);
            try (OutputStream out = Files.newOutputStream(settingsPath())) {
                settings.store(new java.io.OutputStreamWriter(out, StandardCharsets.UTF_8), "BAR Map Studio");
            }
        } catch (Exception ignored) {
        }
    }

    String setting(String key, String fallback) {
        return settings.getProperty(key, fallback);
    }

    void remember(String key, String value) {
        if (value == null) {
            settings.remove(key);
        } else {
            settings.setProperty(key, value);
        }
        saveSettings();
    }

    /** Posizione e grandezza della finestra salvate all'ultima chiusura, o null. */
    Rectangle savedWindow() {
        String[] parts = settings.getProperty("window", "").split(",");
        try {
            if (parts.length == 4) {
                return new Rectangle(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()),
                    Math.max(1280, Integer.parseInt(parts[2].trim())), Math.max(720, Integer.parseInt(parts[3].trim())));
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    void saveWindow(Rectangle bounds) {
        settings.setProperty("window", bounds.x + "," + bounds.y + "," + bounds.width + "," + bounds.height);
        saveSettings();
    }

    // ------------------------------------------------------------------ Beyond All Reason

    /** La cartella "data" di BAR in uso. */
    Path barData() {
        return barData;
    }

    boolean barFound() {
        return BarLocator.isData(barData);
    }

    private void updateBarStatus() {
        boolean found = barFound();
        String game = null;
        Path engine = null;
        if (found) {
            try {
                game = BarGameFiles.installedVersion(barData);
            } catch (RuntimeException ignored) {
            }
            engine = AppPaths.newestEngine();
        }
        String engineName = engine == null ? null : engine.getFileName().toString().replaceFirst("^recoil_", "");
        String text;
        if (!found) {
            text = I18n.t("shell.bar.missing");
        } else {
            text = (game != null ? game.replaceFirst("^Beyond All Reason ", "BAR ") : I18n.t("shell.bar.found"))
                + (engineName != null ? "  ·  " + I18n.t("shell.bar.engine", engineName) : "");
        }
        barStatus.setText("●  " + text);
        barStatus.setForeground(found ? GREEN : RED);
        barStatus.setToolTipText("<html>" + barData + (game != null ? "<br>" + game : "")
            + (engineName != null ? "<br>" + I18n.t("shell.bar.engine", engineName) : "") + "</html>");
    }

    /**
     * Fa scegliere all'utente la cartella di BAR (quella di installazione o direttamente "data").
     *
     * @return true se la cartella e' cambiata: le schede gia' aperte la usano dal prossimo avvio
     */
    boolean chooseBarFolder(Component parent) {
        Path start = barData != null && Files.isDirectory(barData) ? barData : BarLocator.browseStart();
        JFileChooser chooser = new JFileChooser(start != null ? start.toFile() : null);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // su Linux e macOS il gioco sta in cartelle nascoste (.local, .var, Library): vanno mostrate
        chooser.setFileHidingEnabled(BarLocator.isWindows());
        chooser.setDialogTitle(I18n.t("shell.bar.choose.title"));
        if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        Path picked = BarLocator.fromChoice(chooser.getSelectedFile().toPath());
        if (picked == null) {
            JOptionPane.showMessageDialog(parent, I18n.t("shell.bar.choose.wrong"), I18n.t("shell.bar.choose.title"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        boolean changed = !picked.equals(barData);
        barData = picked;
        AppPaths.setBarData(picked);
        remember("bar.data", picked.toString());
        updateBarStatus();
        if (changed) {
            JOptionPane.showMessageDialog(parent, I18n.t("shell.bar.choose.restart"), I18n.t("shell.bar.choose.title"), JOptionPane.INFORMATION_MESSAGE);
        }
        return changed;
    }

    /**
     * Avvia il motore di BAR con uno script di partita.
     *
     * @param engineVersion versione precisa del motore (per esempio "2026.07.04"), o null per la piu' recente
     */
    boolean startEngine(Path script, String engineVersion) {
        Path data = barData;
        Path wanted = engineVersion != null ? data.resolve("engine").resolve("recoil_" + engineVersion) : null;
        Path engine = wanted != null && BarLocator.hasEngine(wanted) ? wanted : AppPaths.newestEngine();
        if (engine == null) {
            JOptionPane.showMessageDialog(frame, I18n.t("host.noengine"), I18n.t("map.167"), JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            List<String> command = new ArrayList<>(List.of(engine.resolve(BarLocator.program("spring")).toString(),
                "--write-dir", data.toString(), "--isolation", script.toString()));
            new ProcessBuilder(command).directory(engine.toFile())
                .redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, I18n.t("shell.engine.failed", String.valueOf(e.getMessage())), I18n.t("map.167"), JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ------------------------------------------------------------------ immagine di avvio

    /** Prepara l'immagine mostrata da Java mentre il programma si carica (dal prossimo avvio). */
    private void writeSplashPicture() {
        Path file = home.resolve("cache").resolve("splash.png");
        if (Files.isRegularFile(file)) {
            return;
        }
        try {
            Files.createDirectories(file.getParent());
            int w = 420, h = 330;
            BufferedImage picture = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = picture.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setPaint(new GradientPaint(0, 0, new Color(19, 24, 34), 0, h, new Color(11, 14, 20)));
            g.fillRoundRect(0, 0, w, h, 22, 22);
            g.setColor(new Color(0, 229, 255, 120));
            g.drawRoundRect(0, 0, w - 1, h - 1, 22, 22);
            try (InputStream logo = StudioShell.class.getResourceAsStream("bms_icon_256.png")) {
                if (logo != null) {
                    g.drawImage(ImageIO.read(logo), (w - 120) / 2, 34, 120, 120, null);
                }
            }
            g.setColor(new Color(240, 244, 250));
            g.setFont(BarMapStudio.fontCond(Font.BOLD, 26));
            String title = "BAR MAP STUDIO";
            g.drawString(title, (w - g.getFontMetrics().stringWidth(title)) / 2, 200);
            g.setColor(new Color(140, 155, 180));
            g.setFont(BarMapStudio.font(Font.PLAIN, 14));
            String sub = "for Beyond All Reason";
            g.drawString(sub, (w - g.getFontMetrics().stringWidth(sub)) / 2, 226);
            g.setColor(new Color(0, 229, 255));
            g.setFont(BarMapStudio.font(Font.PLAIN, 13));
            String wait = "Starting...";
            g.drawString(wait, (w - g.getFontMetrics().stringWidth(wait)) / 2, 276);
            g.dispose();
            ImageIO.write(picture, "png", file.toFile());
        } catch (Exception ignored) {
        }
    }
}

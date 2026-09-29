package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/StartScreen.class */
final class StartScreen {
    private static final Pattern PROGRESS = Pattern.compile("\\[Progress]\\s+(\\d+)%");
    private static final Pattern DOWNLOAD = Pattern.compile("\\[Download] (.+?)\\s*$", 8);
    private final Supplier<Path> data;
    private final Runnable onStart;
    private final Consumer<Boolean> rememberShown;
    private final Bar launcherBar = new Bar();
    private final Bar gameBar = new Bar();
    private final JLabel launcherText = new JLabel("Looking for a newer launcher...");
    private final JLabel gameText = new JLabel("Checking Beyond All Reason...");
    private final JButton start = new JButton("Start");
    private final JCheckBox always = new JCheckBox("Show this page at every start");
    private final Timer poll = new Timer(500, e -> {
        readLog();
    });
    private boolean gameDone;
    private boolean launcherDone;
    private long logStart;
    private boolean installing;
    private boolean left;

    StartScreen(Supplier<Path> data, boolean shownAtStart, Consumer<Boolean> rememberShown, Runnable onStart) {
        this.data = data;
        this.onStart = onStart;
        this.rememberShown = rememberShown;
        this.always.setSelected(shownAtStart);
    }

    JComponent view() {
        JPanel view = new BarLauncher.BackgroundPanel(new GridBagLayout());
        JPanel card = new Card();
        card.setLayout(new BorderLayout(0, 30));
        card.setBorder(BorderFactory.createEmptyBorder(36, 44, 34, 44));
        card.setPreferredSize(new Dimension(620, 380));
        JLabel logo = new JLabel();
        try {
            InputStream stream = StartScreen.class.getResourceAsStream("eclipse.png");
            if (stream != null) {
                try {
                    BufferedImage image = ImageIO.read(stream);
                    if (image != null) {
                        logo.setIcon(new ImageIcon(smooth(image, 52)));
                    }
                } catch (Throwable th) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
        }
        JLabel title = new JLabel("ECLIPSE");
        title.setForeground(BarLauncher.ACCENT);
        title.setFont(title.getFont().deriveFont(1, 30.0f));
        JLabel subtitle = new JLabel("for Beyond All Reason");
        subtitle.setForeground(BarLauncher.MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(14.0f));
        JPanel names = new JPanel();
        names.setLayout(new BoxLayout(names, 1));
        names.setOpaque(false);
        names.add(title);
        names.add(subtitle);
        JPanel head = new JPanel(new BorderLayout(16, 0));
        head.setOpaque(false);
        head.add(logo, "West");
        head.add(names, "Center");
        card.add(head, "North");
        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows, 1));
        rows.setOpaque(false);
        rows.add(row("Launcher", this.launcherText, this.launcherBar));
        rows.add(Box.createVerticalStrut(22));
        rows.add(row("Game", this.gameText, this.gameBar));
        card.add(rows, "Center");
        this.start.setFont(this.start.getFont().deriveFont(1, 18.0f));
        this.start.setPreferredSize(new Dimension(190, 50));
        this.start.putClientProperty("JButton.buttonType", "roundRect");
        this.start.setBackground(BarLauncher.ACCENT);
        this.start.setForeground(new Color(24, 18, 8));
        this.start.setFocusPainted(false);
        this.start.addActionListener(e2 -> {
            this.onStart.run();
        });
        this.start.setEnabled(false);
        this.start.setText("Checking...");
        this.always.setOpaque(false);
        this.always.setForeground(BarLauncher.MUTED);
        this.always.addActionListener(e3 -> {
            this.rememberShown.accept(Boolean.valueOf(this.always.isSelected()));
        });
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(this.always, "West");
        bottom.add(this.start, "East");
        card.add(bottom, "South");
        GridBagConstraints middle = new GridBagConstraints();
        middle.anchor = 10;
        view.add(card, middle);
        return view;
    }

    private static Image smooth(BufferedImage image, int size) {
        BufferedImage current = image;
        int width = image.getWidth();
        int height = image.getHeight();
        while (width / 2 >= size && height / 2 >= size) {
            width /= 2;
            height /= 2;
            current = draw(current, width, height);
        }
        return draw(current, size, Math.max(1, (size * image.getHeight()) / image.getWidth()));
    }

    private static BufferedImage draw(Image image, int width, int height) {
        BufferedImage out = new BufferedImage(width, height, 2);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(image, 0, 0, width, height, (ImageObserver) null);
        g.dispose();
        return out;
    }

    private static JPanel row(String name, JLabel text, Bar bar) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, 1));
        row.setOpaque(false);
        JLabel label = new JLabel(name);
        label.setForeground(BarLauncher.TEXT);
        label.setFont(label.getFont().deriveFont(1, 14.0f));
        text.setForeground(BarLauncher.MUTED);
        text.setFont(text.getFont().deriveFont(13.0f));
        text.setHorizontalAlignment(4);
        JPanel line = new JPanel(new BorderLayout(16, 0));
        line.setOpaque(false);
        line.add(label, "West");
        line.add(text, "Center");
        line.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        bar.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        row.add(line);
        row.add(Box.createVerticalStrut(10));
        row.add(bar);
        row.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        return row;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/StartScreen$Card.class */
    private static final class Card extends JPanel {
        Card() {
            setOpaque(false);
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, new Color(27, 30, 38), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, h, new Color(19, 21, 27)));
            g.fillRoundRect(0, 0, w - 1, h - 1, 22, 22);
            g.setColor(new Color(255, 255, 255, 22));
            g.drawRoundRect(0, 0, w - 1, h - 1, 22, 22);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/StartScreen$Bar.class */
    static final class Bar extends JComponent {
        private int value;
        private float progress;
        private boolean indeterminate = true;
        private final Timer animation = new Timer(30, e -> {
            this.progress += (0.9f - this.progress) * 0.02f;
            repaint();
        });

        Bar() {
            setPreferredSize(new Dimension(100, 6));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
            setMinimumSize(new Dimension(10, 6));
            this.animation.start();
        }

        void setValue(int value) {
            this.value = Math.max(0, Math.min(100, value));
            repaint();
        }

        void setIndeterminate(boolean indeterminate) {
            if (indeterminate && !this.indeterminate) {
                this.progress = SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
            }
            this.indeterminate = indeterminate;
            if (indeterminate) {
                this.animation.start();
            } else {
                this.animation.stop();
            }
            repaint();
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g.setColor(new Color(255, 255, 255, 20));
            g.fillRoundRect(0, 0, w, h, h, h);
            g.setColor(BarLauncher.GREEN);
            if (this.indeterminate) {
                g.fillRoundRect(0, 0, Math.max(h, Math.round(w * this.progress)), h, h, h);
            } else if (this.value > 0) {
                g.fillRoundRect(0, 0, Math.max(h, (w * this.value) / 100), h, h, h);
            }
            g.dispose();
        }
    }

    void launcher(String state) {
        if ("slow".equals(state)) {
            if (!this.launcherDone) {
                this.launcherText.setText("Still looking (a new version may be downloading)...");
                return;
            }
            return;
        }
        this.launcherDone = true;
        this.launcherBar.setIndeterminate(false);
        this.launcherBar.setValue(100);
        switch (state) {
            case "current":
                this.launcherText.setText((LauncherUpdater.appStore() ? "Updates come from the App Store" : "Up to date") + " (build " + LauncherUpdater.runningCommit() + ")");
                break;
            case "ready":
                this.launcherText.setText("A new version is downloaded: it starts the next time you open Eclipse");
                break;
            case "installing":
                this.installing = true;
                this.launcherText.setText("Installing the new version, Eclipse restarts...");
                this.start.setEnabled(false);
                this.start.setText("Updating...");
                break;
            case "failed":
                this.launcherText.setText("Could not check for a new version; the launcher runs as it is");
                break;
            default:
                this.launcherDone = false;
                this.launcherBar.setIndeterminate(true);
                this.launcherText.setText("Looking for a newer launcher...");
                break;
        }
        settle();
    }

    void gameRefreshing(boolean running, String installed) {
        if (running) {
            this.gameDone = false;
            this.logStart = System.currentTimeMillis();
            this.gameBar.setIndeterminate(true);
            this.gameText.setText("Checking Beyond All Reason for updates...");
            this.poll.start();
            return;
        }
        this.poll.stop();
        this.gameDone = true;
        this.gameBar.setIndeterminate(false);
        this.gameBar.setValue(100);
        this.gameText.setText(installed != null ? "Up to date: " + installed.replaceFirst("^Beyond All Reason ", "BAR ") : "Beyond All Reason was not found; choose its folder in the launcher");
        settle();
    }

    private void readLog() {
        Path log = this.data.get().resolve("eclipse-game-refresh.log");
        if (!Files.isRegularFile(log, new LinkOption[0])) {
            return;
        }
        try {
            if (Files.getLastModifiedTime(log, new LinkOption[0]).toMillis() < this.logStart - 2000) {
                return;
            }
            String text = new String(Files.readAllBytes(log), StandardCharsets.UTF_8);
            String tail = text.length() > 6000 ? text.substring(text.length() - 6000) : text;
            Matcher download = DOWNLOAD.matcher(tail);
            String name = null;
            while (download.find()) {
                name = download.group(1).trim();
            }
            Matcher progress = PROGRESS.matcher(tail);
            int percent = -1;
            while (progress.find()) {
                percent = Integer.parseInt(progress.group(1));
            }
            if (name != null) {
                this.gameText.setText("Downloading " + name.replaceFirst("^Beyond All Reason ", "BAR ") + (percent >= 0 ? "  " + percent + "%" : ""));
            }
            if (percent >= 0) {
                this.gameBar.setIndeterminate(false);
                this.gameBar.setValue(percent);
            }
        } catch (IOException e) {
        }
    }

    boolean everythingDone() {
        return this.gameDone && this.launcherDone;
    }

    private void settle() {
        if (this.installing || !everythingDone()) {
            return;
        }
        this.start.setText("Start");
        this.start.setEnabled(true);
        if (!this.always.isSelected() && !this.left) {
            this.left = true;
            this.onStart.run();
        }
    }
}

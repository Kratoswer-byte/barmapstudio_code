package com.faforever.neroxis.generator.bar;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/Confetti.class */
final class Confetti extends JComponent {
    private static final Color[] PARTY = {new Color(240, 150, 46), new Color(204, 77, 230), new Color(108, 182, 255), BarLauncher.GREEN, new Color(255, 206, 64), new Color(236, 238, 242)};
    private static final Color[] PRIME = {new Color(255, 206, 64), new Color(240, 170, 40), new Color(255, 232, 150), new Color(108, 182, 255), new Color(64, 140, 240), new Color(236, 238, 242)};
    private final Timer timer;
    private double age;
    private final List<Piece> pieces = new ArrayList();
    private long last = System.nanoTime();

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/Confetti$Piece.class */
    private static final class Piece {
        double x;
        double y;
        double speedX;
        double speedY;
        double angle;
        double spin;
        double flutter;
        double size;
        boolean round;
        Color colour = Color.WHITE;

        private Piece() {
        }
    }

    private Confetti(int width, int height, boolean prime) {
        Random random = new Random();
        Color[] colours = prime ? PRIME : PARTY;
        int count = prime ? 260 : 130;
        for (int i = 0; i < count; i++) {
            Piece piece = new Piece();
            boolean rain = prime && i % 3 == 0;
            boolean left = i % 2 == 0;
            if (rain) {
                piece.x = random.nextDouble() * ((double) width);
                piece.y = (-20.0d) - ((random.nextDouble() * ((double) height)) * 0.5d);
                piece.speedX = (random.nextDouble() - 0.5d) * 80.0d;
                piece.speedY = 60.0d + (random.nextDouble() * 120.0d);
            } else {
                piece.x = left ? -10.0d : width + 10;
                piece.y = ((double) height) * (0.72d + (random.nextDouble() * 0.2d));
                double power = 0.75d + (random.nextDouble() * 0.55d);
                piece.speedX = ((double) (left ? 1 : -1)) * ((((double) width) * 0.28d) + (random.nextDouble() * ((double) width) * 0.32d)) * power;
                piece.speedY = (-((((double) height) * 0.9d) + (random.nextDouble() * ((double) height) * 0.55d))) * power;
            }
            piece.angle = random.nextDouble() * 3.141592653589793d * 2.0d;
            piece.spin = (random.nextDouble() - 0.5d) * 14.0d;
            piece.flutter = random.nextDouble() * 3.141592653589793d * 2.0d;
            piece.size = 7.0d + (random.nextDouble() * ((double) (prime ? 8 : 6)));
            piece.round = random.nextDouble() < 0.18d;
            piece.colour = colours[random.nextInt(colours.length)];
            this.pieces.add(piece);
        }
        setOpaque(false);
        this.timer = new Timer(16, e -> {
            step();
        });
    }

    static void burst(JFrame frame, boolean prime) {
        if (frame != null && frame.isShowing()) {
            if (System.getProperty("neroxis.launcher.screenshot") != null && System.getProperty("neroxis.launcher.confetti") == null) {
                return;
            }
            SwingUtilities.invokeLater(() -> {
                JLayeredPane layers = frame.getLayeredPane();
                Confetti confetti = new Confetti(layers.getWidth(), layers.getHeight(), prime);
                confetti.setBounds(0, 0, layers.getWidth(), layers.getHeight());
                layers.add(confetti, JLayeredPane.POPUP_LAYER);
                confetti.timer.start();
            });
        }
    }

    static BufferedImage preview(int width, int height, boolean prime, double seconds) {
        Confetti confetti = new Confetti(width, height, prime);
        confetti.setSize(width, height);
        double d = 0.0d;
        while (true) {
            double time = d;
            if (time < seconds) {
                confetti.advance(0.016666666666666666d);
                d = time + 0.016666666666666666d;
            } else {
                BufferedImage picture = new BufferedImage(width, height, 1);
                Graphics2D g = picture.createGraphics();
                g.setColor(new Color(18, 20, 25));
                g.fillRect(0, 0, width, height);
                confetti.paintComponent(g);
                g.dispose();
                return picture;
            }
        }
    }

    static List<BufferedImage> frames(int width, int height, boolean prime, int perSecond, double seconds) {
        Confetti confetti = new Confetti(width, height, prime);
        confetti.setSize(width, height);
        List<BufferedImage> result = new ArrayList<>();
        for (int frame = 0; frame < ((double) perSecond) * seconds; frame++) {
            BufferedImage picture = new BufferedImage(width, height, 2);
            Graphics2D g = picture.createGraphics();
            confetti.paintComponent(g);
            g.dispose();
            result.add(picture);
            for (int step = 0; step < 60 / perSecond; step++) {
                confetti.advance(0.016666666666666666d);
            }
        }
        return result;
    }

    private void step() {
        long now = System.nanoTime();
        double seconds = Math.min(0.05d, (now - this.last) / 1.0E9d);
        this.last = now;
        if (!advance(seconds) || this.age > 6.0d) {
            this.timer.stop();
            Container parent = getParent();
            if (parent != null) {
                parent.remove(this);
                parent.repaint();
                return;
            }
            return;
        }
        repaint();
    }

    private boolean advance(double seconds) {
        this.age += seconds;
        boolean anyLeft = false;
        for (Piece piece : this.pieces) {
            piece.speedY += 900.0d * seconds;
            piece.speedX *= Math.pow(0.35d, seconds);
            piece.speedY *= Math.pow(0.55d, seconds);
            piece.flutter += seconds * 7.0d;
            piece.x += (piece.speedX + (Math.sin(piece.flutter) * 40.0d)) * seconds;
            piece.y += piece.speedY * seconds;
            piece.angle += piece.spin * seconds;
            if (piece.y < getHeight() + 30) {
                anyLeft = true;
            }
        }
        return anyLeft;
    }

    public boolean contains(int x, int y) {
        return false;
    }

    protected void paintComponent(Graphics graphics) {
        Graphics2D g = graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform base = g.getTransform();
        float fade = (float) Math.max(0.0d, Math.min(1.0d, (6.0d - this.age) / 1.5d));
        for (Piece piece : this.pieces) {
            g.setTransform(base);
            g.translate(piece.x, piece.y);
            g.rotate(piece.angle);
            double turn = Math.abs(Math.cos(piece.flutter));
            Color colour = piece.colour;
            float shade = (float) (0.7d + (0.3d * turn));
            g.setColor(new Color(Math.round(colour.getRed() * shade), Math.round(colour.getGreen() * shade), Math.round(colour.getBlue() * shade), Math.round(255.0f * fade)));
            if (piece.round) {
                g.fill(new Ellipse2D.Double((-piece.size) / 2.0d, ((-piece.size) / 2.0d) * turn, piece.size, piece.size * turn));
            } else {
                g.fill(new Rectangle2D.Double((-piece.size) / 2.0d, (-piece.size) * 0.3d * turn, piece.size, piece.size * 0.6d * Math.max(0.15d, turn)));
            }
        }
        g.dispose();
    }
}

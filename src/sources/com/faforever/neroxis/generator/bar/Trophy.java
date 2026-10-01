package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.generator.util.serial.GeneratedMapNameEncoder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/Trophy.class */
final class Trophy {
    static final String[] NAMES = {"Sunrise", "Comet", "Nova", "Solstice", "Aurora", "Transit", "Blood Moon", "Supernova", "Diamond Ring", "Apex"};
    static final String[] GRADES = {"Bronze", "Silver", "Gold"};
    private static final Color[][] METAL = {new Color[]{new Color(240, 184, 132), new Color(184, 112, 58), new Color(96, 52, 22)}, new Color[]{new Color(248, 250, 253), new Color(172, 180, 192), new Color(84, 90, 102)}, new Color[]{new Color(255, 240, 160), new Color(236, 178, 52), new Color(132, 84, 12)}};
    static final Color[] GRADE_COLOURS = {new Color(205, GeneratedMapNameEncoder.NUM_BINS, 50), new Color(192, 199, 210), new Color(245, 196, 64)};
    private static final Color SUN = new Color(255, 206, 106);
    private static final Color SKY = new Color(11, 13, 22);
    private static final Color VELVET = new Color(84, 56, 132);
    private static final double LIGHT = Math.toRadians(-125.0d);
    private static final double[][] STONE_LEFT = {new double[]{19.0d, 84.0d}, new double[]{19.6d, 66.0d}, new double[]{19.1d, 48.0d}, new double[]{20.3d, 31.0d}, new double[]{21.2d, 25.6d}, new double[]{26.4d, 24.6d}, new double[]{32.8d, 25.8d}, new double[]{33.5d, 41.0d}, new double[]{34.3d, 57.0d}, new double[]{33.7d, 71.0d}, new double[]{34.6d, 84.0d}};
    private static final double[][] STONE_RIGHT = {new double[]{81.2d, 84.0d}, new double[]{80.5d, 69.0d}, new double[]{81.0d, 50.0d}, new double[]{79.8d, 33.0d}, new double[]{79.0d, 25.4d}, new double[]{73.8d, 24.8d}, new double[]{67.3d, 25.5d}, new double[]{66.4d, 39.0d}, new double[]{65.8d, 55.0d}, new double[]{66.3d, 70.0d}, new double[]{65.5d, 84.0d}};
    private static final double[][] LINTEL = {new double[]{12.8d, 14.6d}, new double[]{30.0d, 13.1d}, new double[]{50.0d, 13.9d}, new double[]{70.0d, 12.7d}, new double[]{87.2d, 14.3d}, new double[]{87.6d, 25.2d}, new double[]{70.0d, 26.1d}, new double[]{50.0d, 25.3d}, new double[]{30.0d, 26.3d}, new double[]{12.4d, 25.5d}};
    private static final Map<String, BufferedImage> CACHE = new ConcurrentHashMap();

    private Trophy() {
    }

    static BufferedImage image(int trophy, int grade, int size, boolean locked) {
        if (locked) {
            return CACHE.computeIfAbsent("locked-" + size, key -> {
                return locked(size);
            });
        }
        int t = Math.max(1, Math.min(10, trophy));
        int g = Math.max(0, Math.min(2, grade));
        return CACHE.computeIfAbsent(t + "-" + g + "-" + size, key2 -> {
            BufferedImage art = artwork(t, g);
            BufferedImage picture = new BufferedImage(size, size, 2);
            Graphics2D graphics = canvas(picture);
            if (art != null) {
                graphics.drawImage(art, 0, 0, size, size, (ImageObserver) null);
            } else {
                graphics.scale(((double) size) / 100.0d, ((double) size) / 100.0d);
                draw(graphics, t, g);
            }
            graphics.dispose();
            return picture;
        });
    }

    static String title(int level) {
        int index = Math.max(0, Math.min(29, level - 1));
        return NAMES[index / 3] + " " + GRADES[index % 3];
    }

    private static BufferedImage artwork(int trophy, int grade) {
        BufferedImage bufferedImage;
        String name = "trophy-" + trophy + "-" + GRADES[grade].toLowerCase() + ".png";
        try {
            Path file = Path.of(System.getProperty("neroxis.home", "."), "trophies", name);
            if (Files.isRegularFile(file, new LinkOption[0])) {
                return ImageIO.read(file.toFile());
            }
            InputStream in = Trophy.class.getResourceAsStream("/trophies/" + name);
            if (in == null) {
                bufferedImage = null;
            } else {
                try {
                    bufferedImage = ImageIO.read(in);
                } catch (Throwable th) {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            BufferedImage bufferedImage2 = bufferedImage;
            if (in != null) {
                in.close();
            }
            return bufferedImage2;
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    private static Graphics2D canvas(BufferedImage picture) {
        Graphics2D graphics = picture.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return graphics;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static BufferedImage locked(int size) {
        BufferedImage picture = new BufferedImage(size, size, 2);
        Graphics2D g = canvas(picture);
        g.scale(((double) size) / 100.0d, ((double) size) / 100.0d);
        g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 84.0f, new Color(46, 50, 60, 190), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 97.0f, new Color(18, 20, 26, 190)));
        g.fill(new RoundRectangle2D.Double(24.0d, 84.0d, 52.0d, 13.0d, 4.0d, 4.0d));
        g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 20.0f, new Color(74, 79, 92), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 72.0f, new Color(40, 43, 52)));
        g.fill(new Ellipse2D.Double(26.0d, 22.0d, 48.0d, 48.0d));
        g.setPaint(new RadialGradientPaint(new Point2D.Double(50.0d, 54.0d), 28.0f, new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new Color[]{new Color(32, 30, 44), SKY}));
        g.fill(new Ellipse2D.Double(30.0d, 26.0d, 40.0d, 40.0d));
        g.setFont(new Font("SansSerif", 1, 30));
        FontMetrics metrics = g.getFontMetrics();
        g.setColor(new Color(140, 148, 163));
        g.drawString("?", (float) (50.0d - (((double) metrics.stringWidth("?")) / 2.0d)), (float) (46.0d + (((double) (metrics.getAscent() - metrics.getDescent())) / 2.0d)));
        g.dispose();
        return picture;
    }

    private static void draw(Graphics2D g, int trophy, int grade) {
        Color[] metal = METAL[grade];
        if (trophy >= 4) {
            float strength = Math.min(1.0f, 0.25f + (trophy * 0.07f));
            g.setPaint(new RadialGradientPaint(new Point2D.Double(50.0d, 38.0d), 48.0f, new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new Color[]{alpha(GRADE_COLOURS[grade], (int) (90.0f * strength)), alpha(GRADE_COLOURS[grade], 0)}));
            g.fill(new Ellipse2D.Double(2.0d, -10.0d, 96.0d, 96.0d));
        }
        plinth(g, metal, grade);
        switch (trophy) {
            case 1:
                sunrise(g, metal);
                break;
            case 2:
                torch(g, metal);
                break;
            case 3:
                starburst(g, metal);
                break;
            case 4:
                standingStones(g, metal);
                break;
            case 5:
                crown(g, metal);
                break;
            case 6:
                shield(g, metal);
                break;
            case 7:
                obelisk(g, metal);
                break;
            case 8:
                cup(g, metal);
                break;
            case 9:
                ring(g, metal);
                break;
            default:
                wingedOrb(g, metal);
                break;
        }
    }

    private static void plinth(Graphics2D g, Color[] metal, int grade) {
        g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 84.0f, new Color(54, 58, 70), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 97.0f, new Color(18, 20, 26)));
        g.fill(new RoundRectangle2D.Double(24.0d, 84.0d, 52.0d, 13.0d, 4.0d, 4.0d));
        g.setColor(new Color(255, 255, 255, 38));
        g.setStroke(new BasicStroke(0.7f, 1, 1));
        g.draw(new Line2D.Double(26.5d, 84.7d, 73.5d, 84.7d));
        RoundRectangle2D.Double r0 = new RoundRectangle2D.Double(33.0d, 87.5d, 34.0d, 6.0d, 2.0d, 2.0d);
        g.setPaint(metalPaint(metal, 30.0d, 70.0d));
        g.fill(r0);
        bevel(g, r0, metal, 0.5d);
        for (int star = 0; star <= grade; star++) {
            star(g, 50.0d + ((((double) star) - (((double) grade) / 2.0d)) * 7.0d), 90.5d, 2.4d, new Color(30, 22, 10));
        }
    }

    private static void sunrise(Graphics2D g, Color[] metal) {
        shadow(g, 16.0d);
        for (int i = 0; i < 7; i++) {
            double length = i % 2 == 1 ? 41.0d : 31.0d;
            facetedRay(g, metal, 50.0d, 45.0d, Math.toRadians(198 + (i * 24)), 12.0d, length, 5.5d);
        }
        part(g, new Rectangle2D.Double(45.5d, 64.0d, 9.0d, 14.0d), metalPaint(metal, 45.5d, 54.5d), metal);
        part(g, new RoundRectangle2D.Double(43.0d, 71.0d, 14.0d, 3.2d, 1.6d, 1.6d), metalPaint(metal, 43.0d, 57.0d), metal);
        foot(g, metal, 77.0d, 8.0d, 15.0d);
        Path2D.Double r0 = new Path2D.Double();
        r0.moveTo(9.0d, 65.0d);
        r0.quadTo(50.0d, 55.5d, 91.0d, 65.0d);
        r0.quadTo(92.5d, 68.0d, 90.0d, 70.0d);
        r0.quadTo(50.0d, 61.0d, 10.0d, 70.0d);
        r0.quadTo(7.5d, 68.0d, 9.0d, 65.0d);
        r0.closePath();
        part(g, r0, vertical(metal[0], metal[2], 58.0d, 70.0d), metal);
        medallion(g, metal, 1, 50.0d, 45.0d, 13.0d);
    }

    private static void torch(Graphics2D g, Color[] metal) {
        shadow(g, 13.0d);
        Path2D.Double r0 = new Path2D.Double();
        r0.moveTo(44.5d, 57.0d);
        r0.lineTo(55.5d, 57.0d);
        r0.lineTo(53.0d, 80.0d);
        r0.lineTo(47.0d, 80.0d);
        r0.closePath();
        part(g, r0, metalPaint(metal, 44.5d, 55.5d), metal);
        Shape clip = g.getClip();
        g.clip(r0);
        g.setColor(alpha(metal[2], 120));
        g.setStroke(new BasicStroke(0.7f, 1, 1));
        double d = 52.0d;
        while (true) {
            double y = d;
            if (y >= 84.0d) {
                break;
            }
            g.draw(new Line2D.Double(42.0d, y, 58.0d, y + 4.5d));
            d = y + 3.6d;
        }
        g.setClip(clip);
        for (double d2 : new double[]{60.0d, 74.0d}) {
            part(g, new RoundRectangle2D.Double(43.5d, d2, 13.0d, 3.0d, 1.5d, 1.5d), metalPaint(metal, 43.5d, 56.5d), metal);
        }
        foot(g, metal, 79.0d, 6.0d, 12.0d);
        Path2D.Double r1 = new Path2D.Double();
        r1.moveTo(50.0d, 52.0d);
        r1.curveTo(33.0d, 52.0d, 24.0d, 43.0d, 26.0d, 31.0d);
        r1.curveTo(27.0d, 23.0d, 29.0d, 15.0d, 26.0d, 6.0d);
        r1.curveTo(34.0d, 12.0d, 39.0d, 16.0d, 41.0d, 21.0d);
        r1.curveTo(41.0d, 12.0d, 46.0d, 4.0d, 54.0d, -1.0d);
        r1.curveTo(55.0d, 6.0d, 58.0d, 12.0d, 58.5d, 19.0d);
        r1.curveTo(61.0d, 15.0d, 66.0d, 13.0d, 73.0d, 10.0d);
        r1.curveTo(71.0d, 17.0d, 74.0d, 23.0d, 74.0d, 31.0d);
        r1.curveTo(76.0d, 44.0d, 67.0d, 52.0d, 50.0d, 52.0d);
        r1.closePath();
        part(g, r1, vertical(metal[0], metal[1], 0.0d, 52.0d), metal);
        g.setPaint(vertical(new Color(255, 214, 140, 130), new Color(255, 110, 40, 80), 6.0d, 48.0d));
        g.fill(scaled(r1, 50.0d, 44.0d, 0.78d));
        g.setPaint(vertical(new Color(255, 252, 230, 200), new Color(255, 190, 110, 40), 14.0d, 48.0d));
        g.fill(scaled(r1, 50.0d, 46.0d, 0.52d));
        Path2D.Double r2 = new Path2D.Double();
        r2.moveTo(30.0d, 46.0d);
        r2.lineTo(70.0d, 46.0d);
        r2.curveTo(68.0d, 54.0d, 61.0d, 58.0d, 55.5d, 59.0d);
        r2.lineTo(44.5d, 59.0d);
        r2.curveTo(39.0d, 58.0d, 32.0d, 54.0d, 30.0d, 46.0d);
        r2.closePath();
        part(g, r2, metalPaint(metal, 30.0d, 70.0d), metal);
        double d3 = 36.0d;
        while (true) {
            double x = d3;
            if (x <= 64.0d) {
                ball(g, metal, x, 51.5d + (Math.abs(x - 50.0d) * 0.03d), 1.3d);
                d3 = x + 7.0d;
            } else {
                part(g, new RoundRectangle2D.Double(28.0d, 43.5d, 44.0d, 5.0d, 2.5d, 2.5d), vertical(metal[0], metal[2], 43.5d, 48.5d), metal);
                medallion(g, metal, 2, 50.0d, 28.0d, 12.0d);
                return;
            }
        }
    }

    private static void starburst(Graphics2D g, Color[] metal) {
        shadow(g, 12.0d);
        foot(g, metal, 78.0d, 5.0d, 12.0d);
        part(g, new Ellipse2D.Double(43.5d, 73.5d, 13.0d, 6.0d), metalPaint(metal, 43.5d, 56.5d), metal);
        for (int i = 0; i < 8; i++) {
            facetedRay(g, metal, 50.0d, 39.0d, (-1.5707963267948966d) + ((((((double) i) + 0.5d) * 3.141592653589793d) * 2.0d) / ((double) 8)), 20.0d, 28.0d, 1.7d);
        }
        Area compass = new Area(new Ellipse2D.Double(50.0d - 23.0d, 39.0d - 23.0d, 46.0d, 46.0d));
        compass.subtract(new Area(new Ellipse2D.Double(50.0d - 20.0d, 39.0d - 20.0d, 40.0d, 40.0d)));
        part(g, compass, metalPaint(metal, 50.0d - 23.0d, 50.0d + 23.0d), metal);
        g.setStroke(new BasicStroke(0.35f, 1, 1));
        for (int i2 = 0; i2 < 8; i2++) {
            double tip = (-1.5707963267948966d) + (((((double) i2) * 3.141592653589793d) * 2.0d) / ((double) 8));
            double length = i2 % 2 == 0 ? 37.0d : 27.0d;
            for (int side = -1; side <= 1; side += 2) {
                double valley = tip + ((((double) side) * 3.141592653589793d) / ((double) 8));
                Path2D facet = triangle(50.0d, 39.0d, 50.0d + (Math.cos(tip) * length), 39.0d + (Math.sin(tip) * length), 50.0d + (Math.cos(valley) * 14.0d), 39.0d + (Math.sin(valley) * 14.0d));
                Color colour = shade(metal, Math.cos((tip + ((((double) side) * 3.141592653589793d) / 5.0d)) - LIGHT));
                g.setColor(colour);
                g.fill(facet);
                g.draw(facet);
            }
        }
        Path2D.Double r0 = new Path2D.Double();
        for (int i3 = 0; i3 < 8 * 2; i3++) {
            double angle = (-1.5707963267948966d) + ((((double) i3) * 3.141592653589793d) / ((double) 8));
            double radius = i3 % 2 == 1 ? 14.0d : (i3 / 2) % 2 == 0 ? 37.0d : 27.0d;
            double x = 50.0d + (Math.cos(angle) * radius);
            double y = 39.0d + (Math.sin(angle) * radius);
            if (i3 == 0) {
                r0.moveTo(x, y);
            } else {
                r0.lineTo(x, y);
            }
        }
        r0.closePath();
        outline(g, r0, metal);
        medallion(g, metal, 3, 50.0d, 39.0d, 13.0d);
    }

    private static void standingStones(Graphics2D g, Color[] metal) {
        shadow(g, 34.0d);
        Shape clip = g.getClip();
        g.clip(new Rectangle2D.Double(33.0d, 25.0d, 34.0d, 59.0d));
        glow(g, 50.0d, 52.0d, 30.0d, 130);
        rays(g, 50.0d, 52.0d, 15.0d, 32.0d, 12, 1.1f);
        Path2D.Double r0 = new Path2D.Double();
        r0.moveTo(43.0d, 52.0d);
        r0.lineTo(57.0d, 52.0d);
        r0.lineTo(64.0d, 84.0d);
        r0.lineTo(36.0d, 84.0d);
        r0.closePath();
        g.setPaint(vertical(new Color(255, 222, 150, 120), new Color(255, 200, 120, 0), 52.0d, 84.0d));
        g.fill(r0);
        g.setClip(clip);
        for (double[][] outline : new double[][][]{STONE_LEFT, STONE_RIGHT}) {
            Path2D stone = polygon(outline);
            Rectangle2D box = stone.getBounds2D();
            part(g, stone, metalPaint(metal, box.getMinX(), box.getMaxX()), metal);
        }
        cracks(g, metal, new double[][]{new double[]{22.0d, 44.0d, 25.0d, 46.5d, 24.2d, 49.5d}, new double[]{30.5d, 63.0d, 28.4d, 66.5d, 29.0d, 69.0d}, new double[]{77.0d, 37.0d, 73.8d, 40.5d, 74.5d, 42.0d}, new double[]{70.2d, 59.0d, 72.4d, 62.5d, 71.2d, 66.0d}});
        Path2D lintel = polygon(LINTEL);
        part(g, lintel, vertical(metal[0], metal[2], 13.0d, 26.0d), metal);
        cracks(g, metal, new double[][]{new double[]{39.0d, 16.5d, 42.5d, 19.0d, 42.0d, 21.5d}, new double[]{63.5d, 21.8d, 66.8d, 19.8d, 69.0d, 20.6d}});
        medallion(g, metal, 4, 50.0d, 52.0d, 13.0d);
    }

    private static void crown(Graphics2D g, Color[] metal) {
        RoundRectangle2D.Double r0 = new RoundRectangle2D.Double(21.0d, 71.0d, 58.0d, 13.0d, 11.0d, 11.0d);
        g.setPaint(vertical(VELVET, VELVET.darker().darker(), 71.0d, 84.0d));
        g.fill(r0);
        g.setPaint(new RadialGradientPaint(new Point2D.Double(50.0d, 73.0d), 26.0f, new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new Color[]{new Color(255, 255, 255, 50), new Color(255, 255, 255, 0)}));
        g.fill(r0);
        for (int side = -1; side <= 1; side += 2) {
            tassel(g, metal, 50.0d + (((double) side) * 27.5d), 78.0d);
        }
        Path2D.Double r1 = new Path2D.Double();
        r1.moveTo(17.0d, 60.0d);
        r1.curveTo(17.0d, 18.0d, 83.0d, 18.0d, 83.0d, 60.0d);
        r1.closePath();
        g.setPaint(new RadialGradientPaint(new Point2D.Double(42.0d, 30.0d), 34.0f, new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new Color[]{VELVET.brighter(), new Color(30, 18, 50)}));
        g.fill(r1);
        Path2D.Double r2 = new Path2D.Double();
        r2.moveTo(17.0d, 73.0d);
        r2.lineTo(13.0d, 30.0d);
        r2.lineTo(26.0d, 46.0d);
        r2.lineTo(32.0d, 19.0d);
        r2.lineTo(41.0d, 42.0d);
        r2.lineTo(50.0d, 10.0d);
        r2.lineTo(59.0d, 42.0d);
        r2.lineTo(68.0d, 19.0d);
        r2.lineTo(74.0d, 46.0d);
        r2.lineTo(87.0d, 30.0d);
        r2.lineTo(83.0d, 73.0d);
        r2.quadTo(50.0d, 80.0d, 17.0d, 73.0d);
        r2.closePath();
        part(g, r2, metalPaint(metal, 13.0d, 87.0d), metal);
        Area band = new Area(r2);
        band.intersect(new Area(new Rectangle2D.Double(0.0d, 58.0d, 100.0d, 30.0d)));
        g.setPaint(metalPaint(metal, 8.0d, 92.0d));
        g.fill(band);
        bevel(g, band, metal, 0.9d);
        for (double[] tip : new double[][]{new double[]{13.0d, 30.0d}, new double[]{32.0d, 19.0d}, new double[]{50.0d, 10.0d}, new double[]{68.0d, 19.0d}, new double[]{87.0d, 30.0d}}) {
            ball(g, metal, tip[0], tip[1], tip[0] == 50.0d ? 4.0d : 3.4d);
        }
        for (double d : new double[]{19.5d, 25.5d, 31.5d, 37.5d, 62.5d, 68.5d, 74.5d, 80.5d}) {
            pearl(g, d, 58.0d, 1.6d);
        }
        gem(g, 26.0d, 66.0d, 3.8d, new Color(170, 150, 255), new Color(44, 26, 96));
        gem(g, 74.0d, 66.0d, 3.8d, new Color(170, 150, 255), new Color(44, 26, 96));
        gem(g, 50.0d, 67.5d, 3.2d, new Color(255, 140, 150), new Color(110, 10, 30));
        for (double x : new double[]{38.0d, 62.0d}) {
            Path2D.Double r3 = new Path2D.Double();
            r3.moveTo(x, 63.5d);
            r3.lineTo(x + 2.3d, 66.5d);
            r3.lineTo(x, 69.5d);
            r3.lineTo(x - 2.3d, 66.5d);
            r3.closePath();
            part(g, r3, vertical(metal[0], metal[1], 63.5d, 69.5d), metal);
        }
        medallion(g, metal, 5, 50.0d, 45.0d, 13.0d);
    }

    private static void shield(Graphics2D g, Color[] metal) {
        shadow(g, 16.0d);
        Path2D.Double r0 = new Path2D.Double();
        r0.moveTo(17.0d, 11.0d);
        r0.quadTo(50.0d, 2.0d, 83.0d, 11.0d);
        r0.lineTo(83.0d, 40.0d);
        r0.curveTo(83.0d, 62.0d, 68.0d, 75.0d, 50.0d, 84.0d);
        r0.curveTo(32.0d, 75.0d, 17.0d, 62.0d, 17.0d, 40.0d);
        r0.closePath();
        part(g, r0, metalPaint(metal, 17.0d, 83.0d), metal);
        Shape field = scaled(r0, 50.0d, 42.0d, 0.8d, 0.82d);
        g.setPaint(vertical(metal[1], metal[2], 10.0d, 80.0d));
        g.fill(field);
        Shape clip = g.getClip();
        g.clip(field);
        g.setStroke(new BasicStroke(1.1f, 0, 1));
        for (int i = 0; i < 24; i++) {
            double angle = ((((double) i) * 3.141592653589793d) * 2.0d) / 24.0d;
            g.setColor(i % 2 == 0 ? alpha(metal[0], 60) : alpha(metal[2], 80));
            g.draw(new Line2D.Double(50.0d + (Math.cos(angle) * 17.0d), 38.0d + (Math.sin(angle) * 17.0d), 50.0d + (Math.cos(angle) * 60.0d), 38.0d + (Math.sin(angle) * 60.0d)));
        }
        g.setPaint(new GradientPaint(44.0f, 34.0f, new Color(0, 0, 0, 0), 60.0f, 50.0f, new Color(0, 0, 0, 120)));
        g.fill(new Rectangle2D.Double(0.0d, 0.0d, 100.0d, 100.0d));
        g.setClip(clip);
        bevel(g, field, metal, -0.9d);
        outline(g, field, metal);
        for (double[] rivet : new double[][]{new double[]{23.0d, 14.0d}, new double[]{50.0d, 9.0d}, new double[]{77.0d, 14.0d}, new double[]{20.5d, 43.0d}, new double[]{79.5d, 43.0d}, new double[]{33.0d, 70.0d}, new double[]{67.0d, 70.0d}}) {
            ball(g, metal, rivet[0], rivet[1], 1.7d);
        }
        medallion(g, metal, 6, 50.0d, 38.0d, 14.0d);
    }

    private static void obelisk(Graphics2D g, Color[] metal) {
        shadow(g, 22.0d);
        part(g, new RoundRectangle2D.Double(26.0d, 79.0d, 48.0d, 5.0d, 1.5d, 1.5d), vertical(metal[1], metal[2], 79.0d, 84.0d), metal);
        part(g, new RoundRectangle2D.Double(31.0d, 74.5d, 38.0d, 5.0d, 1.5d, 1.5d), vertical(metal[0], metal[2], 74.5d, 79.5d), metal);
        Color[] dark = {mix(metal[0], SKY, 0.35d), mix(metal[1], SKY, 0.5d), mix(metal[2], SKY, 0.45d)};
        Path2D.Double r0 = new Path2D.Double();
        r0.moveTo(35.0d, 75.0d);
        r0.lineTo(40.0d, 17.0d);
        r0.lineTo(50.0d, 17.0d);
        r0.lineTo(50.0d, 75.0d);
        r0.closePath();
        Path2D.Double r1 = new Path2D.Double();
        r1.moveTo(50.0d, 75.0d);
        r1.lineTo(50.0d, 17.0d);
        r1.lineTo(60.0d, 17.0d);
        r1.lineTo(65.0d, 75.0d);
        r1.closePath();
        g.setPaint(vertical(dark[0], dark[1], 17.0d, 75.0d));
        g.fill(r0);
        g.setPaint(vertical(dark[1], dark[2], 17.0d, 75.0d));
        g.fill(r1);
        g.setColor(metal[0]);
        g.fill(triangle(40.0d, 17.0d, 50.0d, 2.0d, 50.0d, 17.0d));
        g.setColor(metal[1]);
        g.fill(triangle(50.0d, 17.0d, 50.0d, 2.0d, 60.0d, 17.0d));
        Path2D.Double r2 = new Path2D.Double();
        r2.moveTo(35.0d, 75.0d);
        r2.lineTo(40.0d, 17.0d);
        r2.lineTo(50.0d, 2.0d);
        r2.lineTo(60.0d, 17.0d);
        r2.lineTo(65.0d, 75.0d);
        r2.closePath();
        outline(g, r2, metal);
        g.setStroke(new BasicStroke(0.7f, 1, 1));
        g.setColor(alpha(metal[0], 110));
        g.draw(new Line2D.Double(40.0d, 17.0d, 60.0d, 17.0d));
        g.draw(new Line2D.Double(50.0d, 3.0d, 50.0d, 74.5d));
        g.setColor(alpha(metal[0], 70));
        g.draw(new Line2D.Double(35.8d, 74.0d, 40.6d, 18.0d));
        glyph(g, metal, 50.0d, 57.5d, 0);
        glyph(g, metal, 50.0d, 63.8d, 1);
        glyph(g, metal, 50.0d, 70.0d, 2);
        medallion(g, metal, 7, 50.0d, 38.0d, 12.5d);
    }

    private static void cup(Graphics2D g, Color[] metal) {
        shadow(g, 16.0d);
        foot(g, metal, 77.0d, 8.0d, 14.0d);
        part(g, new Rectangle2D.Double(46.0d, 64.0d, 8.0d, 14.0d), metalPaint(metal, 45.0d, 55.0d), metal);
        part(g, new Ellipse2D.Double(41.0d, 67.0d, 18.0d, 7.0d), metalPaint(metal, 41.0d, 59.0d), metal);
        laurels(g, metal, 8);
        Shape shape = new Path2D.Double();
        shape.moveTo(24.0d, 20.0d);
        shape.curveTo(8.0d, 18.0d, 8.0d, 44.0d, 30.0d, 50.0d);
        Shape shape2 = new Path2D.Double();
        shape2.moveTo(76.0d, 20.0d);
        shape2.curveTo(92.0d, 18.0d, 92.0d, 44.0d, 70.0d, 50.0d);
        for (Shape shape3 : new Path2D[]{shape, shape2}) {
            g.setStroke(new BasicStroke(5.0f, 1, 1));
            g.setColor(alpha(metal[2], 200));
            g.draw(shape3);
            g.setStroke(new BasicStroke(3.8f, 1, 1));
            g.setPaint(metalPaint(metal, 8.0d, 92.0d));
            g.draw(shape3);
            AffineTransform keep = g.getTransform();
            g.translate(-0.5d, -0.6d);
            g.setStroke(new BasicStroke(1.0f, 1, 1));
            g.setColor(new Color(255, 255, 255, 110));
            g.draw(shape3);
            g.setTransform(keep);
        }
        Path2D.Double r0 = new Path2D.Double();
        r0.moveTo(20.0d, 10.0d);
        r0.lineTo(80.0d, 10.0d);
        r0.curveTo(80.0d, 44.0d, 68.0d, 62.0d, 50.0d, 66.0d);
        r0.curveTo(32.0d, 62.0d, 20.0d, 44.0d, 20.0d, 10.0d);
        r0.closePath();
        g.setPaint(metalPaint(metal, 20.0d, 80.0d));
        g.fill(r0);
        gloss(g, r0, 18.0d, -6.0d, 24.0d, 70.0d);
        bevel(g, r0, metal, 0.9d);
        part(g, new RoundRectangle2D.Double(20.8d, 15.0d, 58.4d, 3.4d, 1.7d, 1.7d), metalPaint(metal, 20.8d, 79.2d), metal);
        double d = 25.0d;
        while (true) {
            double x = d;
            if (x <= 75.0d) {
                ball(g, metal, x, 16.7d, 0.9d);
                d = x + 5.0d;
            } else {
                part(g, new RoundRectangle2D.Double(17.0d, 7.0d, 66.0d, 6.0d, 3.0d, 3.0d), new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 7.0f, metal[0], SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 14.0f, metal[2]), metal);
                outline(g, r0, metal);
                medallion(g, metal, 8, 50.0d, 34.0d, 13.0d);
                return;
            }
        }
    }

    private static void ring(Graphics2D g, Color[] metal) {
        shadow(g, 20.0d);
        RoundRectangle2D.Double r0 = new RoundRectangle2D.Double(24.0d, 74.0d, 52.0d, 10.0d, 9.0d, 9.0d);
        g.setPaint(vertical(VELVET, VELVET.darker().darker(), 74.0d, 84.0d));
        g.fill(r0);
        g.setColor(new Color(255, 255, 255, 45));
        g.setStroke(new BasicStroke(0.7f, 1, 1));
        g.draw(new Line2D.Double(29.0d, 74.8d, 71.0d, 74.8d));
        Area band = new Area(new Ellipse2D.Double(18.0d, 47.0d, 64.0d, 35.0d));
        band.subtract(new Area(new Ellipse2D.Double(26.0d, 52.0d, 48.0d, 23.0d)));
        part(g, band, metalPaint(metal, 18.0d, 82.0d), metal);
        Area front = new Area(band);
        front.intersect(new Area(new Rectangle2D.Double(0.0d, 64.0d, 100.0d, 30.0d)));
        gloss(g, front, 22.0d, 66.0d, 56.0d, 18.0d);
        g.setColor(new Color(255, 255, 255, 130));
        g.setStroke(new BasicStroke(0.9f, 1, 1));
        g.draw(new Arc2D.Double(19.6d, 48.4d, 60.8d, 32.4d, 208.0d, 124.0d, 0));
        g.setColor(alpha(metal[2], 150));
        g.setStroke(new BasicStroke(1.2f));
        g.draw(new Arc2D.Double(26.0d, 52.0d, 48.0d, 23.0d, 0.0d, 180.0d, 0));
        Path2D.Double r1 = new Path2D.Double();
        r1.moveTo(41.0d, 53.0d);
        r1.lineTo(34.0d, 36.0d);
        r1.lineTo(66.0d, 36.0d);
        r1.lineTo(59.0d, 53.0d);
        r1.closePath();
        part(g, r1, metalPaint(metal, 34.0d, 66.0d), metal);
        for (int side = -1; side <= 1; side += 2) {
            g.setColor(alpha(SKY, 200));
            g.fill(new Ellipse2D.Double((50.0d + (((double) side) * 5.2d)) - 1.4d, 47.6d, 2.8d, 2.4d));
        }
        medallion(g, metal, 9, 50.0d, 30.0d, 15.0d);
        for (double angle : new double[]{-150.0d, -90.0d, -30.0d, 30.0d, 150.0d}) {
            AffineTransform turn = AffineTransform.getRotateInstance(Math.toRadians(angle), 50.0d, 30.0d);
            Shape prong = turn.createTransformedShape(new RoundRectangle2D.Double(63.0d, 28.4d, 5.8d, 3.2d, 3.2d, 3.2d));
            Rectangle2D box = prong.getBounds2D();
            part(g, prong, metalPaint(metal, box.getMinX(), box.getMaxX()), metal);
        }
    }

    private static void wingedOrb(Graphics2D g, Color[] metal) {
        shadow(g, 16.0d);
        for (int i = 0; i < 16; i++) {
            facetedRay(g, metal, 50.0d, 35.0d, ((((double) i) * 3.141592653589793d) * 2.0d) / 16.0d, 15.0d + 2.0d, 15.0d + 19.0d, 1.6d);
        }
        for (int i2 = 0; i2 < 16; i2++) {
            facetedRay(g, metal, 50.0d, 35.0d, (((((double) i2) + 0.5d) * 3.141592653589793d) * 2.0d) / 16.0d, 15.0d + 2.0d, 15.0d + 11.0d, 2.8d);
        }
        wings(g, metal, 50.0d, 35.0d + 4.0d, 13.0d);
        foot(g, metal, 77.0d, 7.0d, 15.0d);
        part(g, new Rectangle2D.Double(46.5d, 55.0d, 7.0d, 23.0d), metalPaint(metal, 46.5d, 53.5d), metal);
        part(g, new Ellipse2D.Double(40.5d, 63.0d, 19.0d, 7.0d), metalPaint(metal, 40.5d, 59.5d), metal);
        part(g, new Ellipse2D.Double(43.0d, 55.5d, 14.0d, 5.0d), metalPaint(metal, 43.0d, 57.0d), metal);
        Path2D.Double r0 = new Path2D.Double();
        r0.moveTo(40.0d, 47.0d);
        r0.quadTo(50.0d, 60.0d, 60.0d, 47.0d);
        r0.lineTo(56.0d, 56.0d);
        r0.lineTo(44.0d, 56.0d);
        r0.closePath();
        part(g, r0, metalPaint(metal, 40.0d, 60.0d), metal);
        AffineTransform tilt = AffineTransform.getRotateInstance(Math.toRadians(-14.0d), 50.0d, 35.0d + 2.0d);
        Area orbit = new Area(new Ellipse2D.Double(50.0d - 34.0d, (35.0d + 2.0d) - 8.5d, 68.0d, 17.0d));
        orbit.subtract(new Area(new Ellipse2D.Double(50.0d - 30.5d, (35.0d + 2.0d) - 5.5d, 61.0d, 11.0d)));
        Shape band = tilt.createTransformedShape(orbit);
        part(g, band, vertical(metal[0], metal[2], 35.0d - 12.0d, 35.0d + 16.0d), metal);
        medallion(g, metal, 10, 50.0d, 35.0d, 15.0d);
        Shape clip = g.getClip();
        g.clip(tilt.createTransformedShape(new Rectangle2D.Double(0.0d, 35.0d + 2.0d, 100.0d, 50.0d)));
        part(g, band, vertical(metal[0], metal[2], 35.0d - 12.0d, 35.0d + 16.0d), metal);
        g.setClip(clip);
        Point2D moon = tilt.transform(new Point2D.Double(50.0d + (Math.cos(Math.toRadians(38.0d)) * 32.25d), 35.0d + 2.0d + (Math.sin(Math.toRadians(38.0d)) * 7.0d)), (Point2D) null);
        moon(g, moon.getX(), moon.getY(), 2.7d);
        g.setColor(alpha(metal[0], 220));
        g.setStroke(new BasicStroke(0.6f));
        g.draw(new Ellipse2D.Double(moon.getX() - 2.7d, moon.getY() - 2.7d, 5.4d, 5.4d));
        g.setColor(new Color(255, 255, 255, 170));
        g.fill(new Ellipse2D.Double(moon.getX() - 1.6d, moon.getY() - 1.8d, 1.1d, 0.9d));
    }

    private static void medallion(Graphics2D g, Color[] metal, int trophy, double x, double y, double r) {
        g.setPaint(new LinearGradientPaint(new Point2D.Double(x - r, y - r), new Point2D.Double(x + r, y + r), new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 0.45f, 1.0f}, new Color[]{metal[0], metal[1], metal[2]}));
        g.fill(new Ellipse2D.Double((x - r) - 2.4d, (y - r) - 2.4d, (2.0d * r) + 4.8d, (2.0d * r) + 4.8d));
        g.setColor(alpha(metal[2], 200));
        g.setStroke(new BasicStroke(0.6f));
        g.draw(new Ellipse2D.Double((x - r) - 2.4d, (y - r) - 2.4d, (2.0d * r) + 4.8d, (2.0d * r) + 4.8d));
        Ellipse2D.Double r0 = new Ellipse2D.Double(x - r, y - r, 2.0d * r, 2.0d * r);
        Shape clip = g.getClip();
        AffineTransform keep = g.getTransform();
        g.setPaint(new RadialGradientPaint(new Point2D.Double(x, y + (r * 0.6d)), (float) (r * 1.6d), new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new Color[]{new Color(40, 34, 60), SKY}));
        g.fill(r0);
        g.clip(r0);
        double s = r * 0.46d;
        switch (trophy) {
            case 1:
                g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, (float) (y - r), new Color(40, 34, 70), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, (float) (y + r), new Color(230, 120, 60)));
                g.fill(r0);
                glow(g, x, y + (r * 0.35d), s * 2.2d, 150);
                sun(g, x, y + (r * 0.35d), s);
                g.setColor(new Color(16, 14, 24));
                g.fill(new Rectangle2D.Double(x - r, y + (r * 0.35d), 2.0d * r, r));
                break;
            case 2:
                glow(g, x, y + 2.0d, s * 2.0d, 170);
                Path2D.Double r1 = new Path2D.Double();
                r1.moveTo(x, y - (s * 1.7d));
                r1.curveTo(x + (s * 1.2d), y - (s * 0.4d), x + (s * 1.1d), y + s, x, y + (s * 1.2d));
                r1.curveTo(x - (s * 1.1d), y + s, x - (s * 1.2d), y - (s * 0.4d), x, y - (s * 1.7d));
                g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, (float) (y - (s * 1.7d)), new Color(255, 236, 150), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, (float) (y + (s * 1.2d)), new Color(240, 110, 40)));
                g.fill(r1);
                break;
            case 3:
                rays(g, x, y, s * 1.25d, s * 1.9d, 8, 1.3f);
                glow(g, x, y, s * 2.0d, 140);
                sun(g, x, y, s);
                break;
            case 4:
                rays(g, x, y, s * 1.2d, r, 12, 1.1f);
                rays(g, x, y, s * 1.2d, s * 1.7d, 12, 0.9f);
                glow(g, x, y, s * 2.2d, 150);
                sun(g, x, y, s);
                break;
            case 5:
                glow(g, x, y, s * 2.6d, 200);
                sun(g, x, y, s);
                g.setColor(new Color(255, 245, 210, 200));
                g.setStroke(new BasicStroke(1.2f));
                g.draw(new Ellipse2D.Double(x - (s * 1.35d), y - (s * 1.35d), s * 2.7d, s * 2.7d));
                break;
            case 6:
            case 7:
                glow(g, x, y, s * 2.2d, 150);
                sun(g, x, y, s);
                double shift = trophy == 6 ? s * 1.1d : s * 0.45d;
                moon(g, x + shift, y - (shift * 0.4d), s * 1.02d);
                break;
            default:
                corona(g, x, y, s, trophy);
                moon(g, x, y, s * 1.02d);
                if (trophy >= 9) {
                    diamond(g, x + (s * 0.72d), y - (s * 0.72d), s * (trophy == 10 ? 1.25d : 1.0d));
                }
                break;
        }
        g.setClip(clip);
        g.setTransform(keep);
        g.setColor(alpha(metal[2], 200));
        g.setStroke(new BasicStroke(0.8f));
        g.draw(r0);
    }

    private static void sun(Graphics2D g, double x, double y, double r) {
        g.setPaint(new RadialGradientPaint(new Point2D.Double(x - (r * 0.3d), y - (r * 0.3d)), (float) (r * 1.4d), new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new Color[]{new Color(255, 250, 225), SUN}));
        g.fill(new Ellipse2D.Double(x - r, y - r, 2.0d * r, 2.0d * r));
    }

    private static void moon(Graphics2D g, double x, double y, double r) {
        g.setPaint(new RadialGradientPaint(new Point2D.Double(x + (r * 0.2d), y + (r * 0.2d)), (float) r, new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new Color[]{new Color(22, 22, 30), new Color(6, 6, 10)}));
        g.fill(new Ellipse2D.Double(x - r, y - r, 2.0d * r, 2.0d * r));
    }

    private static void glow(Graphics2D g, double x, double y, double r, int strength) {
        g.setPaint(new RadialGradientPaint(new Point2D.Double(x, y), (float) r, new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 0.45f, 1.0f}, new Color[]{new Color(255, 214, 130, strength), new Color(255, 170, 80, strength / 3), new Color(255, 150, 60, 0)}));
        g.fill(new Ellipse2D.Double(x - r, y - r, 2.0d * r, 2.0d * r));
    }

    private static void corona(Graphics2D g, double x, double y, double s, int trophy) {
        glow(g, x, y, s * (trophy == 10 ? 3.0d : 2.4d), 230);
        g.setStroke(new BasicStroke(0.7f, 1, 1));
        int streamers = trophy == 10 ? 16 : 10;
        for (int i = 0; i < streamers; i++) {
            double angle = (((((double) i) * 3.141592653589793d) * 2.0d) / ((double) streamers)) + (((double) (i % 2)) * 0.12d);
            double length = s * (1.5d + (((double) (i % 3)) * 0.35d) + (trophy == 10 ? 0.4d : 0.0d));
            g.setColor(new Color(255, 236, 200, 150));
            g.draw(new Line2D.Double(x + (Math.cos(angle) * s), y + (Math.sin(angle) * s), x + (Math.cos(angle) * length), y + (Math.sin(angle) * length)));
        }
        g.setColor(new Color(255, 248, 230, 230));
        g.setStroke(new BasicStroke(1.1f));
        g.draw(new Ellipse2D.Double(x - (s * 1.08d), y - (s * 1.08d), s * 2.16d, s * 2.16d));
    }

    private static void diamond(Graphics2D g, double x, double y, double size) {
        glow(g, x, y, size * 1.6d, 255);
        g.setColor(new Color(255, 255, 250));
        g.fill(new Ellipse2D.Double(x - (size * 0.35d), y - (size * 0.35d), size * 0.7d, size * 0.7d));
        GeneralPath sparkle = new GeneralPath();
        sparkle.moveTo(x, y - (size * 1.5d));
        sparkle.lineTo(x + (size * 0.12d), y);
        sparkle.lineTo(x, y + (size * 1.5d));
        sparkle.lineTo(x - (size * 0.12d), y);
        sparkle.closePath();
        sparkle.moveTo(x - (size * 1.5d), y);
        sparkle.lineTo(x, y + (size * 0.12d));
        sparkle.lineTo(x + (size * 1.5d), y);
        sparkle.lineTo(x, y - (size * 0.12d));
        sparkle.closePath();
        g.fill(sparkle);
    }

    private static void rays(Graphics2D g, double x, double y, double from, double to, int count, float width) {
        g.setStroke(new BasicStroke(width, 1, 1));
        g.setColor(new Color(255, 214, 120, 220));
        for (int i = 0; i < count; i++) {
            double angle = ((((double) i) * 3.141592653589793d) * 2.0d) / ((double) count);
            g.draw(new Line2D.Double(x + (Math.cos(angle) * from), y + (Math.sin(angle) * from), x + (Math.cos(angle) * to), y + (Math.sin(angle) * to)));
        }
    }

    private static void facetedRay(Graphics2D g, Color[] metal, double cx, double cy, double angle, double from, double to, double half) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double bx = cx + (c * from);
        double by = cy + (s * from);
        double tx = cx + (c * to);
        double ty = cy + (s * to);
        Path2D one = triangle(bx - (s * half), by + (c * half), tx, ty, bx, by);
        Path2D other = triangle(bx, by, tx, ty, bx + (s * half), by - (c * half));
        double facing = Math.cos((angle + 1.5707963267948966d) - LIGHT);
        g.setStroke(new BasicStroke(0.3f, 1, 1));
        g.setColor(shade(metal, facing));
        g.fill(one);
        g.draw(one);
        g.setColor(shade(metal, -facing));
        g.fill(other);
        g.draw(other);
        Path2D ray = triangle(bx - (s * half), by + (c * half), tx, ty, bx + (s * half), by - (c * half));
        outline(g, ray, metal);
    }

    private static void wings(Graphics2D g, Color[] metal, double x, double y, double gap) {
        for (int side = -1; side <= 1; side += 2) {
            for (int i = 6; i >= 0; i--) {
                double t = ((double) i) / 6.0d;
                feather(g, metal, x + (((double) side) * gap), y, side, Math.toRadians((-70.0d) + (t * 72.0d)), 32.0d - (t * 13.0d), 3.3d);
            }
            for (int i2 = 3; i2 >= 0; i2--) {
                double t2 = ((double) i2) / 3.0d;
                feather(g, metal, x + (((double) side) * gap), y, side, Math.toRadians((-58.0d) + (t2 * 58.0d)), 15.5d - (t2 * 3.0d), 3.1d);
            }
        }
    }

    private static void feather(Graphics2D g, Color[] metal, double x, double y, int side, double angle, double length, double half) {
        AffineTransform keep = g.getTransform();
        g.translate(x, y);
        g.scale(side, 1.0d);
        g.rotate(angle);
        Path2D.Double r0 = new Path2D.Double();
        r0.moveTo(0.0d, 0.0d);
        r0.curveTo(length * 0.3d, (-half) * 1.25d, length * 0.78d, -half, length, 0.0d);
        r0.curveTo(length * 0.78d, half, length * 0.3d, half * 1.25d, 0.0d, 0.0d);
        r0.closePath();
        g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, alpha(metal[1], 245), (float) length, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, alpha(metal[0], 235)));
        g.fill(r0);
        g.setStroke(new BasicStroke(0.5f, 1, 1));
        g.setColor(alpha(metal[2], 160));
        g.draw(r0);
        g.setColor(alpha(metal[2], 110));
        g.draw(new Line2D.Double(1.0d, 0.0d, length * 0.9d, 0.0d));
        g.setTransform(keep);
    }

    private static void laurels(Graphics2D g, Color[] metal, int trophy) {
        int leaves = Math.min(7, 2 + (trophy / 2));
        for (int side = -1; side <= 1; side += 2) {
            Path2D.Double r0 = new Path2D.Double();
            r0.moveTo(50 + (side * 22), 95.0d);
            r0.quadTo(50 + (side * 36), 84.0d, 50 + (side * 34), 60.0d);
            g.setColor(metal[2]);
            g.setStroke(new BasicStroke(1.2f, 1, 1));
            g.draw(r0);
            for (int i = 0; i < leaves; i++) {
                double t = (((double) i) + 0.5d) / ((double) leaves);
                double u = 1.0d - t;
                double px = (u * u * ((double) (50 + (side * 22)))) + (2.0d * u * t * ((double) (50 + (side * 36)))) + (t * t * ((double) (50 + (side * 34))));
                double py = (u * u * 95.0d) + (2.0d * u * t * 84.0d) + (t * t * 60.0d);
                for (int out = -1; out <= 1; out += 2) {
                    AffineTransform keep = g.getTransform();
                    g.translate(px, py);
                    g.rotate((((double) (side * out)) * 0.75d) + (((double) side) * (0.25d - (t * 0.25d))));
                    g.setPaint(new GradientPaint(-2.5f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, metal[0], 2.5f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, metal[1]));
                    g.fill(new Ellipse2D.Double(-1.9d, -6.5d, 3.8d, 6.5d));
                    g.setColor(alpha(metal[2], 120));
                    g.setStroke(new BasicStroke(0.4f));
                    g.draw(new Line2D.Double(0.0d, -0.5d, 0.0d, -5.8d));
                    g.setTransform(keep);
                }
                if (i % 2 == 1) {
                    ball(g, metal, px - (((double) side) * 1.6d), py + 1.2d, 1.1d);
                }
            }
        }
    }

    private static void foot(Graphics2D g, Color[] metal, double top, double topHalf, double bottomHalf) {
        Path2D.Double r0 = new Path2D.Double();
        r0.moveTo(50.0d - bottomHalf, 84.0d);
        r0.lineTo(50.0d + bottomHalf, 84.0d);
        r0.lineTo(50.0d + topHalf, top);
        r0.lineTo(50.0d - topHalf, top);
        r0.closePath();
        part(g, r0, metalPaint(metal, 50.0d - bottomHalf, 50.0d + bottomHalf), metal);
    }

    private static void shadow(Graphics2D g, double halfWidth) {
        AffineTransform keep = g.getTransform();
        g.translate(50.0d, 84.5d);
        g.scale(1.0d, 0.16d);
        g.setPaint(new RadialGradientPaint(new Point2D.Double(0.0d, 0.0d), (float) halfWidth, new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new Color[]{new Color(0, 0, 0, 130), new Color(0, 0, 0, 0)}));
        g.fill(new Ellipse2D.Double(-halfWidth, -halfWidth, halfWidth * 2.0d, halfWidth * 2.0d));
        g.setTransform(keep);
    }

    private static void tassel(Graphics2D g, Color[] metal, double x, double y) {
        g.setColor(metal[1]);
        g.setStroke(new BasicStroke(0.8f, 1, 1));
        g.draw(new Line2D.Double(x, y - 2.0d, x, y + 1.5d));
        Path2D.Double r0 = new Path2D.Double();
        r0.moveTo(x, y + 1.0d);
        r0.curveTo(x + 2.4d, y + 3.0d, x + 2.2d, y + 6.5d, x, y + 7.0d);
        r0.curveTo(x - 2.2d, y + 6.5d, x - 2.4d, y + 3.0d, x, y + 1.0d);
        r0.closePath();
        part(g, r0, vertical(metal[0], metal[2], y + 1.0d, y + 7.0d), metal);
    }

    private static void glyph(Graphics2D g, Color[] metal, double x, double y, int phase) {
        Ellipse2D.Double r0 = new Ellipse2D.Double(x - 2.1d, y - 2.1d, 2.0d * 2.1d, 2.0d * 2.1d);
        g.setStroke(new BasicStroke(0.6f));
        switch (phase) {
            case 0:
                g.setColor(alpha(metal[0], 150));
                g.draw(r0);
                break;
            case 1:
                Area crescent = new Area(r0);
                crescent.subtract(new Area(new Ellipse2D.Double((x - 2.1d) + 1.3d, (y - 2.1d) - 0.3d, 2.0d * 2.1d, 2.0d * 2.1d)));
                g.setColor(alpha(metal[0], 180));
                g.fill(crescent);
                break;
            default:
                g.setColor(alpha(SKY, 210));
                g.fill(r0);
                g.setColor(alpha(metal[0], 120));
                g.setStroke(new BasicStroke(0.5f));
                g.draw(r0);
                break;
        }
    }

    private static void cracks(Graphics2D g, Color[] metal, double[][] lines) {
        g.setColor(alpha(metal[2], 130));
        g.setStroke(new BasicStroke(0.55f, 1, 1));
        for (double[] line : lines) {
            Path2D.Double r0 = new Path2D.Double();
            r0.moveTo(line[0], line[1]);
            for (int i = 2; i < line.length; i += 2) {
                r0.lineTo(line[i], line[i + 1]);
            }
            g.draw(r0);
        }
    }

    private static void ball(Graphics2D g, Color[] metal, double x, double y, double r) {
        g.setPaint(new RadialGradientPaint(new Point2D.Double(x - (r * 0.35d), y - (r * 0.35d)), (float) (r * 1.3d), new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new Color[]{metal[0], metal[2]}));
        g.fill(new Ellipse2D.Double(x - r, y - r, 2.0d * r, 2.0d * r));
    }

    private static void pearl(Graphics2D g, double x, double y, double r) {
        g.setPaint(new RadialGradientPaint(new Point2D.Double(x - (r * 0.35d), y - (r * 0.4d)), (float) (r * 1.4d), new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new Color[]{new Color(255, 253, 246), new Color(176, 168, 158)}));
        g.fill(new Ellipse2D.Double(x - r, y - r, 2.0d * r, 2.0d * r));
    }

    private static void gem(Graphics2D g, double x, double y, double r, Color light, Color dark) {
        g.setPaint(new RadialGradientPaint(new Point2D.Double(x - (r * 0.3d), y - (r * 0.3d)), (float) (r * 1.2d), new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new Color[]{light, dark}));
        g.fill(new Ellipse2D.Double(x - r, y - (r * 0.8d), 2.0d * r, 1.6d * r));
        g.setColor(new Color(255, 255, 255, 170));
        g.fill(new Ellipse2D.Double(x - (r * 0.45d), y - (r * 0.5d), r * 0.5d, r * 0.35d));
    }

    private static void part(Graphics2D g, Shape shape, Paint paint, Color[] metal) {
        g.setPaint(paint);
        g.fill(shape);
        bevel(g, shape, metal, 0.9d);
        outline(g, shape, metal);
    }

    private static void bevel(Graphics2D g, Shape shape, Color[] metal, double depth) {
        Shape clip = g.getClip();
        AffineTransform keep = g.getTransform();
        g.clip(shape);
        g.setStroke(new BasicStroke((float) (Math.abs(depth) * 1.8d), 1, 1));
        g.translate(depth, depth);
        g.setColor(new Color(255, 255, 255, 100));
        g.draw(shape);
        g.setTransform(keep);
        g.translate(-depth, -depth);
        g.setColor(alpha(metal[2], 140));
        g.draw(shape);
        g.setTransform(keep);
        g.setClip(clip);
    }

    private static void outline(Graphics2D g, Shape shape, Color[] metal) {
        g.setColor(alpha(metal[2], 170));
        g.setStroke(new BasicStroke(0.8f, 1, 1));
        g.draw(shape);
    }

    private static void gloss(Graphics2D g, Shape body, double x, double y, double w, double h) {
        Area shine = new Area(body);
        shine.intersect(new Area(new Ellipse2D.Double(x, y, w, h)));
        g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, (float) y, new Color(255, 255, 255, 90), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, (float) (y + h), new Color(255, 255, 255, 0)));
        g.fill(shine);
    }

    private static void star(Graphics2D g, double x, double y, double r, Color colour) {
        GeneralPath star = new GeneralPath();
        for (int i = 0; i < 10; i++) {
            double angle = (-1.5707963267948966d) + ((((double) i) * 3.141592653589793d) / 5.0d);
            double radius = i % 2 == 0 ? r : r * 0.45d;
            double px = x + (Math.cos(angle) * radius);
            double py = y + (Math.sin(angle) * radius);
            if (i == 0) {
                star.moveTo(px, py);
            } else {
                star.lineTo(px, py);
            }
        }
        star.closePath();
        g.setColor(colour);
        g.fill(star);
    }

    private static Path2D triangle(double x1, double y1, double x2, double y2, double x3, double y3) {
        Path2D.Double r0 = new Path2D.Double();
        r0.moveTo(x1, y1);
        r0.lineTo(x2, y2);
        r0.lineTo(x3, y3);
        r0.closePath();
        return r0;
    }

    private static Path2D polygon(double[][] points) {
        Path2D.Double r0 = new Path2D.Double();
        r0.moveTo(points[0][0], points[0][1]);
        for (int i = 1; i < points.length; i++) {
            r0.lineTo(points[i][0], points[i][1]);
        }
        r0.closePath();
        return r0;
    }

    private static Shape scaled(Shape shape, double x, double y, double factor) {
        return scaled(shape, x, y, factor, factor);
    }

    private static Shape scaled(Shape shape, double x, double y, double sx, double sy) {
        AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
        transform.scale(sx, sy);
        transform.translate(-x, -y);
        return transform.createTransformedShape(shape);
    }

    private static Color shade(Color[] metal, double facing) {
        return facing > 0.0d ? mix(metal[1], metal[0], facing) : mix(metal[1], metal[2], (-facing) * 0.8d);
    }

    private static GradientPaint vertical(Color top, Color bottom, double y0, double y1) {
        return new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, (float) y0, top, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, (float) y1, bottom);
    }

    private static LinearGradientPaint metalPaint(Color[] metal, double left, double right) {
        return new LinearGradientPaint(new Point2D.Double(left, 0.0d), new Point2D.Double(right, 0.0d), new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 0.28f, 0.5f, 0.75f, 1.0f}, new Color[]{metal[2], metal[0], metal[1], metal[0], metal[2]});
    }

    private static Color mix(Color from, Color to, double amount) {
        double t = Math.max(0.0d, Math.min(1.0d, amount));
        return new Color((int) Math.round(((double) from.getRed()) + (((double) (to.getRed() - from.getRed())) * t)), (int) Math.round(((double) from.getGreen()) + (((double) (to.getGreen() - from.getGreen())) * t)), (int) Math.round(((double) from.getBlue()) + (((double) (to.getBlue() - from.getBlue())) * t)));
    }

    private static Color alpha(Color colour, int alpha) {
        return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), Math.max(0, Math.min(255, alpha)));
    }
}

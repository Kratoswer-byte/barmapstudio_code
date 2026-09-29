package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.generator.util.serial.GeneratedMapNameEncoder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import javax.swing.JComponent;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/PrimeBadge.class */
final class PrimeBadge extends JComponent {
    static final int NONE = 0;
    static final int PRIME = 1;
    static final int LIFE = 2;
    private int level;

    PrimeBadge(int size) {
        setOpaque(false);
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
        setVisible(false);
    }

    void setLevel(int level) {
        String str;
        this.level = level;
        setVisible(level != 0);
        if (level == 2) {
            str = "Prime for life";
        } else {
            str = level == 1 ? "Prime" : null;
        }
        setToolTipText(str);
        repaint();
    }

    protected void paintComponent(Graphics graphics) {
        if (this.level == 0) {
            return;
        }
        Graphics2D g = graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        float s = Math.min(getWidth(), getHeight());
        float cx = getWidth() / 2.0f;
        float cy = getHeight() / 2.0f;
        if (this.level == 2) {
            paintGold(g, cx, cy, s);
        } else {
            paintBlue(g, cx, cy, s);
        }
        g.dispose();
    }

    private static void paintBlue(Graphics2D g, float cx, float cy, float s) {
        Color light = new Color(246, 249, 255);
        Color silver = new Color(195, 205, 220);
        Color deep = new Color(GeneratedMapNameEncoder.NUM_BINS, 139, 157);
        Color dark = new Color(70, 80, 96);
        float my = cy - (s * 0.06f);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(cx, my), s * 0.6f, new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 0.6f, 1.0f}, new Color[]{new Color(90, 150, 255, 110), new Color(90, 150, 255, 45), new Color(90, 150, 255, 0)}));
        g.fill(new Ellipse2D.Float(cx - (s * 0.6f), my - (s * 0.6f), s * 1.2f, s * 1.2f));
        float outer = s * 0.43f;
        float inner = s * 0.355f;
        for (int i = 0; i < 16; i++) {
            double a0 = ((6.283185307179586d * (((double) i) - 0.5d)) / ((double) 16)) - 1.5707963267948966d;
            double a1 = ((6.283185307179586d * ((double) i)) / ((double) 16)) - 1.5707963267948966d;
            double a2 = ((6.283185307179586d * (((double) i) + 0.5d)) / ((double) 16)) - 1.5707963267948966d;
            Point2D tip = polar(cx, my, outer, a1);
            Point2D base0 = polar(cx, my, inner, a0);
            Point2D base2 = polar(cx, my, inner, a2);
            Point2D middle = polar(cx, my, inner, a1);
            g.setColor(i % 2 == 0 ? light : silver);
            g.fill(triangle(base0, tip, middle));
            g.setColor(i % 2 == 0 ? deep : dark);
            g.fill(triangle(middle, tip, base2));
        }
        float ring = s * 0.34f;
        g.setPaint(new GradientPaint(cx, my - ring, light, cx, my + ring, deep));
        g.setStroke(new BasicStroke(Math.max(2.0f, s / 22.0f)));
        g.draw(new Ellipse2D.Float(cx - ring, my - ring, ring * 2.0f, ring * 2.0f));
        float bead = Math.max(1.2f, s / 70.0f);
        for (int i2 = 0; i2 < 32; i2++) {
            Point2D p = polar(cx, my, ring, (6.283185307179586d * ((double) i2)) / 32.0d);
            g.setColor(i2 % 2 == 0 ? light : dark);
            g.fill(new Ellipse2D.Float(((float) p.getX()) - bead, ((float) p.getY()) - bead, bead * 2.0f, bead * 2.0f));
        }
        float disc = s * 0.29f;
        g.setPaint(new RadialGradientPaint(new Point2D.Float(cx - (disc * 0.35f), my - (disc * 0.4f)), disc * 1.5f, new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new Color[]{new Color(42, 108, 240), new Color(8, 28, 92)}));
        g.fill(new Ellipse2D.Float(cx - disc, my - disc, disc * 2.0f, disc * 2.0f));
        g.setColor(silver);
        g.setStroke(new BasicStroke(Math.max(1.0f, s / 60.0f)));
        g.draw(new Ellipse2D.Float(cx - disc, my - disc, disc * 2.0f, disc * 2.0f));
        float dots = disc * 0.86f;
        float dot = Math.max(0.8f, s / 110.0f);
        g.setColor(new Color(255, 255, 255, 120));
        for (int i3 = 0; i3 < 24; i3++) {
            Point2D p2 = polar(cx, my, dots, (6.283185307179586d * ((double) i3)) / 24.0d);
            g.fill(new Ellipse2D.Float(((float) p2.getX()) - dot, ((float) p2.getY()) - dot, dot * 2.0f, dot * 2.0f));
        }
        bevelledStar(g, cx, my + (disc * 0.06f), disc * 0.74f, light, silver, dark);
        sparkle(g, cx - (s * 0.27f), my - (s * 0.27f), s * 0.05f);
        ribbon(g, cx, cy + (s * 0.4f), s * 0.72f, s * 0.14f, "PRIME", new Color(42, 108, 240), new Color(14, 46, 134), new Color(242, 246, 255));
    }

    private static void paintGold(Graphics2D g, float cx, float cy, float s) {
        Color light = new Color(255, 240, 176);
        Color gold = new Color(245, 192, 58);
        Color deep = new Color(184, 122, 16);
        Color dark = new Color(122, 76, 6);
        float my = cy - (s * 0.06f);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(cx, my), s * 0.62f, new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 0.6f, 1.0f}, new Color[]{new Color(255, 200, 80, 120), new Color(255, 190, 60, 50), new Color(255, 190, 60, 0)}));
        g.fill(new Ellipse2D.Float(cx - (s * 0.62f), my - (s * 0.62f), s * 1.24f, s * 1.24f));
        float outer = s * 0.46f;
        float inner = s * 0.35f;
        for (int i = 0; i < 24; i++) {
            double a0 = ((6.283185307179586d * (((double) i) - 0.5d)) / ((double) 24)) - 1.5707963267948966d;
            double a1 = ((6.283185307179586d * ((double) i)) / ((double) 24)) - 1.5707963267948966d;
            double a2 = ((6.283185307179586d * (((double) i) + 0.5d)) / ((double) 24)) - 1.5707963267948966d;
            Point2D tip = polar(cx, my, outer, a1);
            Point2D base0 = polar(cx, my, inner, a0);
            Point2D base2 = polar(cx, my, inner, a2);
            Point2D middle = polar(cx, my, inner, a1);
            Path2D facet = triangle(base0, tip, middle);
            g.setColor(i % 2 == 0 ? light : gold);
            g.fill(facet);
            Path2D facet2 = triangle(middle, tip, base2);
            g.setColor(i % 2 == 0 ? deep : dark);
            g.fill(facet2);
        }
        float ring = s * 0.34f;
        g.setPaint(new GradientPaint(cx, my - ring, light, cx, my + ring, deep));
        g.setStroke(new BasicStroke(Math.max(2.0f, s / 22.0f)));
        g.draw(new Ellipse2D.Float(cx - ring, my - ring, ring * 2.0f, ring * 2.0f));
        float beads = s * 0.34f;
        float bead = Math.max(1.2f, s / 70.0f);
        for (int i2 = 0; i2 < 36; i2++) {
            double angle = (6.283185307179586d * ((double) i2)) / 36.0d;
            Point2D p = polar(cx, my, beads, angle);
            g.setColor(i2 % 2 == 0 ? light : dark);
            g.fill(new Ellipse2D.Float(((float) p.getX()) - bead, ((float) p.getY()) - bead, bead * 2.0f, bead * 2.0f));
        }
        float disc = s * 0.29f;
        g.setPaint(new RadialGradientPaint(new Point2D.Float(cx - (disc * 0.35f), my - (disc * 0.4f)), disc * 1.5f, new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new Color[]{new Color(34, 44, 72), new Color(7, 9, 20)}));
        g.fill(new Ellipse2D.Float(cx - disc, my - disc, disc * 2.0f, disc * 2.0f));
        g.setColor(gold);
        g.setStroke(new BasicStroke(Math.max(1.0f, s / 60.0f)));
        g.draw(new Ellipse2D.Float(cx - disc, my - disc, disc * 2.0f, disc * 2.0f));
        bevelledStar(g, cx, my + (disc * 0.06f), disc * 0.8f, light, gold, dark);
        laurel(g, cx, my, disc * 1.18f, s, gold, deep, true);
        laurel(g, cx, my, disc * 1.18f, s, gold, deep, false);
        sparkle(g, cx - (s * 0.3f), my - (s * 0.3f), s * 0.06f);
        sparkle(g, cx + (s * 0.33f), my + (s * 0.12f), s * 0.045f);
        ribbon(g, cx, cy + (s * 0.4f), s * 0.78f, s * 0.14f, "FOR LIFE", gold, deep, new Color(58, 36, 2));
    }

    private static void laurel(Graphics2D g, float cx, float cy, float radius, float s, Color leaf, Color edge, boolean leftSide) {
        for (int i = 0; i < 7; i++) {
            double angle = 1.5707963267948966d + (((double) (leftSide ? 1 : -1)) * (0.25d + ((0.95d * ((double) i)) / ((double) (7 - 1)))));
            Point2D p = polar(cx, cy, radius, angle);
            float lw = s * 0.045f;
            float lh = s * 0.022f;
            Graphics2D leafG = g.create();
            leafG.translate(p.getX(), p.getY());
            leafG.rotate(angle + (leftSide ? -0.5d : 0.5d));
            leafG.setColor(leaf);
            leafG.fill(new Ellipse2D.Float(-lw, -lh, lw * 2.0f, lh * 2.0f));
            leafG.setColor(edge);
            leafG.setStroke(new BasicStroke(Math.max(0.8f, s / 120.0f)));
            leafG.draw(new Ellipse2D.Float(-lw, -lh, lw * 2.0f, lh * 2.0f));
            leafG.dispose();
        }
        g.setColor(edge);
        g.setStroke(new BasicStroke(Math.max(1.0f, s / 70.0f)));
        double start = Math.toDegrees(-(1.5707963267948966d + (leftSide ? 0.25d : -1.2d)));
        g.draw(new Arc2D.Float(cx - radius, cy - radius, radius * 2.0f, radius * 2.0f, (float) start, 55.0f, 0));
    }

    private static void sparkle(Graphics2D g, float x, float y, float r) {
        Path2D.Float r0 = new Path2D.Float();
        for (int i = 0; i < 8; i++) {
            double angle = (3.141592653589793d * ((double) i)) / 4.0d;
            float rr = i % 2 == 0 ? r : r * 0.28f;
            Point2D p = polar(x, y, rr, angle);
            if (i == 0) {
                r0.moveTo(p.getX(), p.getY());
            } else {
                r0.lineTo(p.getX(), p.getY());
            }
        }
        r0.closePath();
        g.setColor(new Color(255, 255, 255, 230));
        g.fill(r0);
    }

    private static void ribbon(Graphics2D g, float cx, float cy, float width, float height, String text, Color fill, Color shade, Color ink) {
        float left = cx - (width / 2.0f);
        float right = cx + (width / 2.0f);
        float top = cy - (height / 2.0f);
        float bottom = cy + (height / 2.0f);
        float notch = height * 0.45f;
        float tail = height * 0.9f;
        Path2D.Float r0 = new Path2D.Float();
        r0.moveTo(left - tail, top + (height * 0.25f));
        r0.lineTo(left + notch, top + (height * 0.25f));
        r0.lineTo(left + notch, bottom + (height * 0.25f));
        r0.lineTo(left - tail, bottom + (height * 0.25f));
        r0.lineTo((left - tail) + notch, cy + (height * 0.25f));
        r0.closePath();
        r0.moveTo(right + tail, top + (height * 0.25f));
        r0.lineTo(right - notch, top + (height * 0.25f));
        r0.lineTo(right - notch, bottom + (height * 0.25f));
        r0.lineTo(right + tail, bottom + (height * 0.25f));
        r0.lineTo((right + tail) - notch, cy + (height * 0.25f));
        r0.closePath();
        g.setColor(shade);
        g.fill(r0);
        Path2D.Float r1 = new Path2D.Float();
        r1.moveTo(left, top);
        r1.quadTo(cx, top + (height * 0.35f), right, top);
        r1.lineTo(right, bottom);
        r1.quadTo(cx, bottom + (height * 0.35f), left, bottom);
        r1.closePath();
        g.setPaint(new GradientPaint(cx, top, fill, cx, bottom, shade));
        g.fill(r1);
        g.setColor(shade.darker());
        g.setStroke(new BasicStroke(Math.max(0.8f, height / 14.0f)));
        g.draw(r1);
        Font font = g.getFont().deriveFont(1, Math.max(6.0f, height * 0.62f));
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        g.setColor(ink);
        g.drawString(text, cx - (metrics.stringWidth(text) / 2.0f), ((cy + (height * 0.22f)) + (metrics.getAscent() / 2.0f)) - (metrics.getDescent() / 2.0f));
    }

    private static void bevelledStar(Graphics2D g, float cx, float cy, float radius, Color light, Color mid, Color dark) {
        Point2D.Float r0 = new Point2D.Float(cx, cy);
        for (int i = 0; i < 5; i++) {
            double tipAngle = (-1.5707963267948966d) + ((6.283185307179586d * ((double) i)) / 5.0d);
            Point2D tip = polar(cx, cy, radius, tipAngle);
            Point2D leftIn = polar(cx, cy, radius * 0.42f, tipAngle - 0.6283185307179586d);
            Point2D rightIn = polar(cx, cy, radius * 0.42f, tipAngle + 0.6283185307179586d);
            g.setColor(light);
            g.fill(triangle(r0, leftIn, tip));
            g.setColor(mid);
            g.fill(triangle(r0, tip, rightIn));
        }
        Path2D.Float r1 = new Path2D.Float();
        for (int i2 = 0; i2 < 10; i2++) {
            double angle = (-1.5707963267948966d) + ((3.141592653589793d * ((double) i2)) / 5.0d);
            Point2D p = polar(cx, cy, i2 % 2 == 0 ? radius : radius * 0.42f, angle);
            if (i2 == 0) {
                r1.moveTo(p.getX(), p.getY());
            } else {
                r1.lineTo(p.getX(), p.getY());
            }
        }
        r1.closePath();
        g.setColor(dark);
        g.setStroke(new BasicStroke(Math.max(0.8f, radius / 18.0f), 1, 1));
        g.draw(r1);
    }

    private static Path2D triangle(Point2D a, Point2D b, Point2D c) {
        Path2D.Float r0 = new Path2D.Float();
        r0.moveTo(a.getX(), a.getY());
        r0.lineTo(b.getX(), b.getY());
        r0.lineTo(c.getX(), c.getY());
        r0.closePath();
        return r0;
    }

    private static Point2D polar(float cx, float cy, float radius, double angle) {
        return new Point2D.Float((float) (((double) cx) + (Math.cos(angle) * ((double) radius))), (float) (((double) cy) + (Math.sin(angle) * ((double) radius))));
    }
}

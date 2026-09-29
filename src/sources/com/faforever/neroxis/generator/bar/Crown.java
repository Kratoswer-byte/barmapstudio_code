package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/Crown.class */
final class Crown implements Icon {
    private final int width;
    private final int height;

    Crown(int height) {
        this.height = height;
        this.width = Math.round(height * 1.3f);
    }

    public void paintIcon(Component c, Graphics graphics, int x, int y) {
        Graphics2D g = graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.translate(x, y);
        double s = ((double) this.height) / 20.0d;
        g.scale(s, s);
        Path2D.Double body = new Path2D.Double();
        body.moveTo(3.0d, 15.0d);
        body.lineTo(1.6d, 6.2d);
        body.lineTo(7.4d, 10.4d);
        body.lineTo(13.0d, 3.2d);
        body.lineTo(18.6d, 10.4d);
        body.lineTo(24.4d, 6.2d);
        body.lineTo(23.0d, 15.0d);
        body.closePath();
        RoundRectangle2D.Double band = new RoundRectangle2D.Double(2.6d, 14.2d, 20.8d, 4.6d, 1.6d, 1.6d);
        Color dark = new Color(92, 58, 8);
        g.setColor(new Color(0, 0, 0, 90));
        g.translate(0.0d, 0.9d);
        g.fill(body);
        g.fill(band);
        g.translate(0.0d, -0.9d);
        g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 3.0f, new Color(255, 236, 150), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 16.0f, new Color(222, 150, 26)));
        g.fill(body);
        g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 14.0f, new Color(250, 206, 84), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 19.0f, new Color(176, 108, 14)));
        g.fill(band);
        g.setColor(new Color(255, 250, 220, 170));
        g.setStroke(new BasicStroke(0.8f, 1, 1));
        Path2D.Double light = new Path2D.Double();
        light.moveTo(3.4d, 8.6d);
        light.lineTo(7.4d, 11.6d);
        light.lineTo(13.0d, 4.9d);
        light.lineTo(18.6d, 11.6d);
        light.lineTo(22.6d, 8.6d);
        g.draw(light);
        g.setColor(dark);
        g.setStroke(new BasicStroke(1.1f, 1, 1));
        g.draw(body);
        g.draw(band);
        for (double[] tip : new double[][]{new double[]{1.6d, 6.2d}, new double[]{13.0d, 3.2d}, new double[]{24.4d, 6.2d}}) {
            Ellipse2D.Double pearl = new Ellipse2D.Double(tip[0] - 1.9d, tip[1] - 1.9d, 3.8d, 3.8d);
            g.setPaint(new GradientPaint((float) tip[0], ((float) tip[1]) - 2.0f, new Color(255, 250, 225), (float) tip[0], ((float) tip[1]) + 2.0f, new Color(232, 176, 60)));
            g.fill(pearl);
            g.setColor(dark);
            g.setStroke(new BasicStroke(0.9f));
            g.draw(pearl);
        }
        Path2D.Double ruby = new Path2D.Double();
        ruby.moveTo(13.0d, 14.6d);
        ruby.lineTo(15.2d, 16.5d);
        ruby.lineTo(13.0d, 18.4d);
        ruby.lineTo(10.8d, 16.5d);
        ruby.closePath();
        g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 14.6f, new Color(255, 110, 110), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 18.4f, new Color(170, 18, 30)));
        g.fill(ruby);
        g.setColor(new Color(90, 10, 16));
        g.setStroke(new BasicStroke(0.7f));
        g.draw(ruby);
        g.setColor(new Color(255, 245, 210));
        g.fill(new Ellipse2D.Double(6.2d, 15.7d, 1.7d, 1.7d));
        g.fill(new Ellipse2D.Double(18.1d, 15.7d, 1.7d, 1.7d));
        g.dispose();
    }

    public int getIconWidth() {
        return this.width;
    }

    public int getIconHeight() {
        return this.height;
    }
}

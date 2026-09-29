package com.faforever.neroxis.generator.bar;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Function;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarButtonUi.class */
final class BarButtonUi extends BasicButtonUI {
    static final Color ACTION = new Color(250, 122, 66);
    static final Color POSITIVE = new Color(13, 217, 64);
    static final Color NEGATIVE = new Color(217, 13, 64);
    static final Color OPTION = new Color(54, 135, 153);
    static final Color HIGHLIGHT = new Color(112, 118, 130);
    static final Color PLAIN = new Color(0, 0, 0);
    private final Function<AbstractButton, Color> kind;

    private BarButtonUi(Function<AbstractButton, Color> kind) {
        this.kind = kind;
    }

    static void install(AbstractButton button, Function<AbstractButton, Color> kind) {
        button.setUI(new BarButtonUi(kind));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setRolloverEnabled(true);
        button.setForeground(Color.WHITE);
        button.setCursor(Cursor.getPredefinedCursor(12));
    }

    static void install(AbstractButton button, Color kind) {
        install(button, (Function<AbstractButton, Color>) any -> {
            return kind;
        });
    }

    public void paint(Graphics graphics, JComponent component) {
        AbstractButton button = (AbstractButton) component;
        Color colour = this.kind.apply(button);
        boolean plain = colour.equals(PLAIN);
        boolean hot = button.getModel().isRollover() || button.getModel().isPressed();
        int width = button.getWidth();
        int height = button.getHeight();
        Graphics2D g = graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        float dim = button.isEnabled() ? 1.0f : 0.45f;
        RoundRectangle2D.Float body = new RoundRectangle2D.Float(0.5f, 0.5f, width - 1, height - 1, 7.0f, 7.0f);
        g.setComposite(AlphaComposite.getInstance(3, (plain ? 0.62f : hot ? 0.9f : 0.68f) * dim));
        g.setColor((plain && hot) ? new Color(60, 38, 22) : colour.equals(PLAIN) ? new Color(8, 10, 14) : darker(colour));
        g.fill(body);
        g.setClip(body);
        int reach = Math.min(9, height / 3);
        for (int i = 0; i < reach; i++) {
            float strength = 1.0f - (i / reach);
            g.setComposite(AlphaComposite.getInstance(3, (plain ? 0.05f : 0.16f) * strength * strength * dim));
            g.setColor(plain ? new Color(150, 170, 200) : brighter(colour));
            g.setStroke(new BasicStroke(1.6f));
            g.draw(new RoundRectangle2D.Float(i + 0.5f, i + 0.5f, (width - 1) - (2 * i), (height - 1) - (2 * i), Math.max(2, 7 - i), Math.max(2, 7 - i)));
        }
        if (button.getModel().isPressed()) {
            g.setComposite(AlphaComposite.getInstance(3, 0.25f));
            g.setColor(Color.BLACK);
            g.fill(body);
        }
        g.setClip((Shape) null);
        g.setComposite(AlphaComposite.getInstance(3, (hot ? 0.95f : 0.7f) * dim));
        g.setStroke(new BasicStroke(1.2f));
        g.setColor(plain ? hot ? new Color(240, 128, 59) : new Color(150, 156, 168) : brighter(colour));
        g.draw(body);
        g.dispose();
        super.paint(graphics, component);
    }

    private static Color darker(Color colour) {
        return new Color((colour.getRed() * 3) / 4, (colour.getGreen() * 3) / 4, (colour.getBlue() * 3) / 4);
    }

    private static Color brighter(Color colour) {
        return new Color(Math.min(255, colour.getRed() + 90), Math.min(255, colour.getGreen() + 90), Math.min(255, colour.getBlue() + 90));
    }
}

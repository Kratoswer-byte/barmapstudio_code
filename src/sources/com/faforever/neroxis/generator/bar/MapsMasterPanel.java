package com.faforever.neroxis.generator.bar;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MapsMasterPanel extends JPanel {

    public static final Color BG_DARK = new Color(11, 14, 20);
    public static final Color SUBNAV_BG = new Color(14, 19, 31);
    public static final Color ACCENT_CYAN = new Color(0, 229, 255);
    public static final Color TEXT_WHITE = new Color(241, 245, 249);
    public static final Color TEXT_MUTED = new Color(148, 163, 184);
    public static final Color BORDER_COLOR = new Color(30, 38, 56);

    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = new JPanel(cards);

    private final JToggleButton editorBtn;
    private final JToggleButton libraryBtn;

    public MapsMasterPanel(JComponent editorView, JComponent libraryView) {
        super(new BorderLayout());
        setBackground(BG_DARK);

        // Sub-navigation bar
        JPanel subBar = new JPanel(new BorderLayout());
        subBar.setBackground(SUBNAV_BG);
        subBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        subBar.setPreferredSize(new Dimension(100, 44));

        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        leftGroup.setOpaque(false);

        JLabel sectionLbl = new JLabel("SEZIONE:");
        sectionLbl.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        sectionLbl.setForeground(ACCENT_CYAN);
        leftGroup.add(sectionLbl);

        ButtonGroup group = new ButtonGroup();
        editorBtn = createSubTab("EDITOR & GENERATORE", "TOOLS", true);
        libraryBtn = createSubTab("LIBRERIA MAPPE", "FOLDER", false);

        group.add(editorBtn);
        group.add(libraryBtn);

        leftGroup.add(editorBtn);
        leftGroup.add(libraryBtn);
        subBar.add(leftGroup, BorderLayout.WEST);

        // Info hint on the right (responsive, auto-hides if window is narrow to prevent collision)
        JLabel hintLbl = new JLabel("Editor procedurale e gestione libreria mappe  ");
        hintLbl.setFont(BarMapStudio.font(Font.BOLD, 11));
        hintLbl.setForeground(TEXT_MUTED);
        hintLbl.setToolTipText("Editor procedurale mappa con canvas interattivo e gestione archivio mappe Beyond All Reason");
        subBar.add(hintLbl, BorderLayout.EAST);

        subBar.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                hintLbl.setVisible(subBar.getWidth() > 880);
            }
        });

        add(subBar, BorderLayout.NORTH);

        // Content Area
        cardPanel.setOpaque(false);
        cardPanel.add(editorView, "EDITOR");
        cardPanel.add(libraryView, "LIBRARY");
        add(cardPanel, BorderLayout.CENTER);

        // Actions
        editorBtn.addActionListener(e -> cards.show(cardPanel, "EDITOR"));
        libraryBtn.addActionListener(e -> cards.show(cardPanel, "LIBRARY"));
    }

    public void showEditor() {
        editorBtn.setSelected(true);
        cards.show(cardPanel, "EDITOR");
    }

    public void showLibrary() {
        libraryBtn.setSelected(true);
        cards.show(cardPanel, "LIBRARY");
    }

    private JToggleButton createSubTab(String text, String iconType, boolean selected) {
        return new SubTabButton(text, iconType, selected);
    }

    public static class SubTabButton extends JToggleButton {
        private final String iconType;

        public SubTabButton(String text, String iconType, boolean selected) {
            super(text, selected);
            this.iconType = iconType;
            setFont(BarMapStudio.fontCond(Font.BOLD, 12));
            setPreferredSize(new Dimension(215, 30));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int w = getWidth();
            int h = getHeight();

            Color fg;
            if (isSelected()) {
                g.setColor(new Color(0, 229, 255, 35));
                g.fillRoundRect(0, 0, w, h, 14, 14);
                g.setColor(ACCENT_CYAN);
                g.setStroke(new BasicStroke(1.2f));
                g.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);
                fg = ACCENT_CYAN;
            } else {
                if (getModel().isRollover()) {
                    g.setColor(new Color(255, 255, 255, 15));
                    g.fillRoundRect(0, 0, w, h, 14, 14);
                    fg = TEXT_WHITE;
                } else {
                    fg = TEXT_MUTED;
                }
            }

            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();
            int tw = fm.stringWidth(getText());
            int th = fm.getAscent();

            int iconSize = 13;
            int gap = 8;
            int totalContentW = iconSize + gap + tw;
            int startX = (w - totalContentW) / 2;
            int iconY = (h - iconSize) / 2;

            if ("TOOLS".equals(iconType)) {
                drawToolsVector(g, startX, iconY, iconSize, iconSize, fg);
            } else if ("FOLDER".equals(iconType)) {
                drawFolderVector(g, startX, iconY, iconSize, iconSize, fg);
            }

            g.setColor(fg);
            g.drawString(getText(), startX + iconSize + gap, (h + th) / 2 - 2);

            g.dispose();
        }
    }

    public static void drawToolsVector(Graphics2D g, int x, int y, int w, int h, Color c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(c);
        g2.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + 2, y + h - 2, x + w - 3, y + 3);
        g2.drawArc(x + w - 7, y, 7, 7, 45, 270);
        g2.drawLine(x + w - 2, y + h - 2, x + 3, y + 3);
        g2.drawLine(x + 1, y + 4, x + 5, y + 2);
        g2.dispose();
    }

    public static void drawFolderVector(Graphics2D g, int x, int y, int w, int h, Color c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(c);
        g2.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        java.awt.geom.Path2D.Float folder = new java.awt.geom.Path2D.Float();
        folder.moveTo(x, y + 2);
        folder.lineTo(x + w * 0.45f, y + 2);
        folder.lineTo(x + w * 0.6f, y + 4);
        folder.lineTo(x + w, y + 4);
        folder.lineTo(x + w, y + h);
        folder.lineTo(x, y + h);
        folder.closePath();
        g2.draw(folder);
        g2.drawLine(x, Math.round(y + 6), x + w, Math.round(y + 6));
        g2.dispose();
    }
}

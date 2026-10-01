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
        editorBtn = createSubTab("🛠️  EDITOR & GENERATORE", true);
        libraryBtn = createSubTab("📂  LIBRERIA MAPPE", false);

        group.add(editorBtn);
        group.add(libraryBtn);

        leftGroup.add(editorBtn);
        leftGroup.add(libraryBtn);
        subBar.add(leftGroup, BorderLayout.WEST);

        // Info hint on the right
        JLabel hintLbl = new JLabel("Crea mappe con seed e canvas, oppure gestisci quelle installate  ");
        hintLbl.setFont(BarMapStudio.font(Font.BOLD, 12));
        hintLbl.setForeground(TEXT_MUTED);
        subBar.add(hintLbl, BorderLayout.EAST);

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

    private JToggleButton createSubTab(String text, boolean selected) {
        return new SubTabButton(text, selected);
    }

    public static class SubTabButton extends JToggleButton {
        public SubTabButton(String text, boolean selected) {
            super(text, selected);
            setFont(BarMapStudio.fontCond(Font.BOLD, 12));
            setPreferredSize(new Dimension(205, 30));
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

            int w = getWidth();
            int h = getHeight();

            if (isSelected()) {
                g.setColor(new Color(0, 229, 255, 35));
                g.fillRoundRect(0, 0, w, h, 14, 14);
                g.setColor(ACCENT_CYAN);
                g.setStroke(new BasicStroke(1.2f));
                g.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);
                g.setColor(ACCENT_CYAN);
            } else {
                if (getModel().isRollover()) {
                    g.setColor(new Color(255, 255, 255, 15));
                    g.fillRoundRect(0, 0, w, h, 14, 14);
                    g.setColor(TEXT_WHITE);
                } else {
                    g.setColor(TEXT_MUTED);
                }
            }

            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();
            int tw = fm.stringWidth(getText());
            int th = fm.getAscent();
            g.drawString(getText(), (w - tw) / 2, (h + th) / 2 - 2);

            g.dispose();
        }
    }
}

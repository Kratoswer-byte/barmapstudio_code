package com.faforever.neroxis.generator.bar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Finestra interattiva di consultazione specifica e linee guida per gli asset
 * di Beyond All Reason (Engine Recoil/Spring).
 *
 * Costruita interamente con icone vettoriali native Java2D (ZERO emoji/glifi Unicode)
 * per eliminare completamente il difetto dei caratteri tofu/box vuoti [X].
 */
public class AssetSpecsDialog extends JDialog {

    private static final Color BG_DARK = new Color(9, 13, 22);
    private static final Color BG_CARD = new Color(18, 24, 38);
    private static final Color BG_INNER = new Color(25, 34, 54);
    private static final Color BORDER_COLOR = new Color(38, 50, 77);
    private static final Color ACCENT_CYAN = new Color(0, 229, 255);
    private static final Color ACCENT_AMBER = new Color(245, 158, 11);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ACCENT_RED = new Color(239, 68, 68);
    private static final Color TEXT_WHITE = new Color(241, 245, 249);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);

    private static AssetSpecsDialog instance;
    private static StudioShell lobbyRef;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(cardLayout);
    private final JButton[] tabButtons = new JButton[5];
    private final JCheckBox doNotShowCheck = new JCheckBox(I18n.t("spec.1"));
    private Point dragStart;

    public static synchronized void toggle(JFrame parent, StudioShell lobby) {
        lobbyRef = lobby;
        if (instance != null && instance.isDisplayable()) {
            if (instance.isVisible()) {
                instance.setVisible(false);
            } else {
                instance.setLocationRelativeTo(parent);
                instance.setVisible(true);
                instance.toFront();
            }
        } else {
            instance = new AssetSpecsDialog(parent, lobby);
            instance.setLocationRelativeTo(parent);
            instance.setVisible(true);
        }
    }

    public static synchronized void toggle(JFrame parent) {
        toggle(parent, lobbyRef);
    }

    public static void showOnStartup(JFrame parent, StudioShell lobby) {
        lobbyRef = lobby;
        if (lobby != null) {
            String hide = lobby.setting("hide_specs_on_start", "false");
            if ("true".equalsIgnoreCase(hide)) {
                return;
            }
        }
        toggle(parent, lobby);
    }

    public AssetSpecsDialog(JFrame parent, StudioShell lobby) {
        super(parent, I18n.t("spec.2"), false);
        lobbyRef = lobby;
        setUndecorated(true);
        setSize(820, 640);
        setMinimumSize(new Dimension(680, 500));
        setBackground(BG_DARK);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_CYAN, 1, true),
            new EmptyBorder(1, 1, 1, 1)
        ));

        // Top Draggable Header
        root.add(buildHeader(), BorderLayout.NORTH);

        // Content Area with Tabs
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(BG_DARK);
        centerWrapper.add(buildTabBar(), BorderLayout.NORTH);

        contentCards.setBackground(BG_DARK);
        contentCards.add(buildHeightmapCard(), "HEIGHTMAP");
        contentCards.add(buildTextureCard(), "TEXTURES");
        contentCards.add(buildModelsCard(), "MODELS");
        contentCards.add(buildMetalEnvCard(), "METAL_ENV");
        contentCards.add(buildCustomAssetsCard(), "CUSTOM_DIR");
        centerWrapper.add(contentCards, BorderLayout.CENTER);

        root.add(centerWrapper, BorderLayout.CENTER);

        // Bottom Action Bar with "Do Not Show Again" checkbox
        root.add(buildBottomBar(), BorderLayout.SOUTH);

        setContentPane(root);
        selectTab(0);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(14, 19, 31));
        header.setPreferredSize(new Dimension(100, 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    Point p = getLocation();
                    setLocation(p.x + e.getX() - dragStart.x, p.y + e.getY() - dragStart.y);
                }
            }
        };
        header.addMouseListener(ma);
        header.addMouseMotionListener(ma);

        // Title with Native Vector Info Badge
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        titlePanel.setOpaque(false);

        JComponent vectorInfoIcon = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                int s = 18;
                g2.setColor(new Color(0, 229, 255, 30));
                g2.fillOval(0, 3, s, s);
                g2.setColor(ACCENT_CYAN);
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawOval(0, 3, s, s);

                // Draw letter "i"
                g2.fillRect(8, 7, 2, 2);
                g2.fillRect(8, 11, 2, 6);
                g2.dispose();
            }
        };
        vectorInfoIcon.setPreferredSize(new Dimension(20, 24));
        titlePanel.add(vectorInfoIcon);

        JLabel title = new JLabel(I18n.t("spec.3"));
        title.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        title.setForeground(ACCENT_CYAN);
        titlePanel.add(title);

        header.add(titlePanel, BorderLayout.WEST);

        // Native Vector Close Button
        JButton closeBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                int w = getWidth();
                int h = getHeight();
                boolean hover = getModel().isRollover();

                if (hover) {
                    g.setColor(new Color(239, 68, 68));
                    g.fillRoundRect(4, 4, w - 8, h - 8, 6, 6);
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(TEXT_MUTED);
                }

                int cx = w / 2;
                int cy = h / 2;
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(cx - 4, cy - 4, cx + 4, cy + 4);
                g.drawLine(cx + 4, cy - 4, cx - 4, cy + 4);
                g.dispose();
            }
        };
        closeBtn.setPreferredSize(new Dimension(38, 38));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> setVisible(false));
        header.add(closeBtn, BorderLayout.EAST);

        return header;
    }

    private JPanel buildTabBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        bar.setBackground(new Color(12, 16, 26));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        String[] titles = {
            I18n.t("spec.4"),
            I18n.t("spec.5"),
            I18n.t("spec.6"),
            I18n.t("spec.7"),
            I18n.t("spec.8")
        };
        String[] targets = {"HEIGHTMAP", "TEXTURES", "MODELS", "METAL_ENV", "CUSTOM_DIR"};

        for (int i = 0; i < titles.length; i++) {
            final int idx = i;
            final String target = targets[i];
            JButton btn = new JButton(titles[i]) {
                @Override
                protected void paintComponent(Graphics graphics) {
                    Graphics2D g = (Graphics2D) graphics.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                    int w = getWidth();
                    int h = getHeight();
                    boolean active = (idx == currentTab);
                    boolean hover = getModel().isRollover();

                    g.setColor(active ? BG_CARD : (hover ? BG_INNER : new Color(16, 21, 33)));
                    g.fillRoundRect(0, 0, w, h, 6, 6);
                    g.setColor(active ? ACCENT_CYAN : (hover ? new Color(0, 229, 255, 100) : BORDER_COLOR));
                    g.drawRoundRect(0, 0, w - 1, h - 1, 6, 6);

                    Color fg = active ? ACCENT_CYAN : (hover ? TEXT_WHITE : TEXT_MUTED);

                    // Draw native vector icon for each tab
                    drawTabVectorIcon(g, idx, 10, (h - 12) / 2, fg);

                    g.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
                    g.setColor(fg);
                    FontMetrics fm = g.getFontMetrics();
                    int tw = fm.stringWidth(getText());
                    g.drawString(getText(), 26, (h + fm.getAscent()) / 2 - 2);
                    g.dispose();
                }
            };
            btn.setPreferredSize(new Dimension(150, 28));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                selectTab(idx);
                cardLayout.show(contentCards, target);
            });
            tabButtons[i] = btn;
            bar.add(btn);
        }

        return bar;
    }

    private void drawTabVectorIcon(Graphics2D g, int tabIdx, int x, int y, Color c) {
        g.setColor(c);
        g.setStroke(new BasicStroke(1.2f));
        switch (tabIdx) {
            case 0 -> { // Mountain
                g.drawLine(x, y + 10, x + 4, y + 2);
                g.drawLine(x + 4, y + 2, x + 8, y + 10);
                g.drawLine(x + 6, y + 6, x + 9, y + 1);
                g.drawLine(x + 9, y + 1, x + 12, y + 10);
                g.drawLine(x, y + 10, x + 12, y + 10);
            }
            case 1 -> { // Texture Palette/Layers
                g.drawRect(x, y + 1, 10, 4);
                g.drawRect(x + 2, y + 6, 10, 4);
            }
            case 2 -> { // 3D Model / Tree
                g.drawLine(x + 6, y, x + 6, y + 11);
                g.drawLine(x + 6, y + 2, x + 1, y + 6);
                g.drawLine(x + 6, y + 2, x + 11, y + 6);
                g.drawLine(x + 6, y + 5, x + 2, y + 9);
                g.drawLine(x + 6, y + 5, x + 10, y + 9);
            }
            case 3 -> { // Gear / Metal
                g.drawOval(x + 2, y + 1, 8, 8);
                g.fillOval(x + 5, y + 4, 2, 2);
            }
            case 4 -> { // Folder
                g.drawRect(x, y + 3, 11, 7);
                g.drawLine(x, y + 3, x + 4, y + 3);
                g.drawLine(x + 4, y + 1, x + 7, y + 1);
            }
        }
    }

    private int currentTab = 0;
    private void selectTab(int index) {
        currentTab = index;
        for (JButton b : tabButtons) {
            if (b != null) b.repaint();
        }
    }

    private JScrollPane wrapScroll(JPanel p) {
        JScrollPane sp = new JScrollPane(p);
        sp.setBorder(null);
        sp.setBackground(BG_DARK);
        sp.getViewport().setBackground(BG_DARK);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private JPanel createCardPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(16, 20, 16, 20));
        return p;
    }

    // =========================================================================
    // 1. HEIGHTMAP & RILIEVI
    // =========================================================================
    private JComponent buildHeightmapCard() {
        JPanel p = createCardPanel();

        p.add(createSectionTitle(I18n.t("spec.9")));
        p.add(Box.createVerticalStrut(10));

        p.add(createBadgeRow(
            new String[]{I18n.t("spec.10"), I18n.t("spec.11")},
            new String[]{I18n.t("spec.12"), I18n.t("spec.13")},
            new String[]{I18n.t("spec.14"), I18n.t("spec.v.formula")},
            new String[]{I18n.t("spec.15"), I18n.t("spec.16")},
            new String[]{I18n.t("spec.17"), I18n.t("spec.18")}
        ));
        p.add(Box.createVerticalStrut(14));

        p.add(createSubHeader(I18n.t("spec.19")));
        p.add(createParagraph(
            I18n.t("spec.20") +
            I18n.t("spec.21") +
            I18n.t("spec.22") +
            I18n.t("spec.23") +
            I18n.t("spec.24")
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createTipBox(
            I18n.t("spec.25") +
            I18n.t("spec.26") +
            I18n.t("spec.27")
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createWarningBox(
            I18n.t("spec.28") +
            I18n.t("spec.29") +
            I18n.t("spec.30")
        ));

        p.add(Box.createVerticalStrut(12));
        p.add(createSubHeader(I18n.t("specx.relief.title")));
        p.add(createParagraph(I18n.t("specx.relief.body")));
        p.add(Box.createVerticalStrut(12));
        p.add(createSubHeader(I18n.t("specx.import.title")));
        p.add(createParagraph(I18n.t("specx.import.body")));

        return wrapScroll(p);
    }

    // =========================================================================
    // 2. TEXTURE & TERRENO
    // =========================================================================
    private JComponent buildTextureCard() {
        JPanel p = createCardPanel();

        p.add(createSectionTitle(I18n.t("spec.31")));
        p.add(Box.createVerticalStrut(10));

        p.add(createBadgeRow(
            new String[]{I18n.t("spec.32"), I18n.t("spec.v.pow2")},
            new String[]{I18n.t("spec.33"), I18n.t("spec.34")},
            new String[]{I18n.t("spec.35"), I18n.t("spec.36")},
            new String[]{I18n.t("spec.37"), I18n.t("spec.38")},
            new String[]{"Tiling", I18n.t("spec.39")}
        ));
        p.add(Box.createVerticalStrut(14));

        p.add(createSubHeader(I18n.t("spec.40")));
        p.add(createParagraph(
            I18n.t("spec.41") +
            I18n.t("spec.42") +
            I18n.t("spec.43") +
            I18n.t("spec.44") +
            I18n.t("spec.45") +
            I18n.t("spec.46")
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createTipBox(
            I18n.t("spec.47") +
            I18n.t("spec.48") +
            I18n.t("spec.49") +
            I18n.t("spec.50") +
            I18n.t("spec.51") +
            I18n.t("spec.52")
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createSubHeader(I18n.t("spec.53")));
        p.add(createParagraph(
            I18n.t("spec.54") +
            I18n.t("spec.55") +
            I18n.t("spec.56") +
            I18n.t("spec.57") +
            I18n.t("spec.58") +
            I18n.t("spec.59") +
            I18n.t("spec.60") +
            I18n.t("spec.61") +
            I18n.t("spec.62")
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createWarningBox(
            I18n.t("spec.63") +
            I18n.t("spec.64") +
            I18n.t("spec.65")
        ));

        p.add(Box.createVerticalStrut(12));
        p.add(createSubHeader(I18n.t("specx.biome.title")));
        p.add(createParagraph(I18n.t("specx.biome.body")));

        return wrapScroll(p);
    }

    // =========================================================================
    // 3. MODELLI 3D & FLORA
    // =========================================================================
    private JComponent buildModelsCard() {
        JPanel p = createCardPanel();

        p.add(createSectionTitle(I18n.t("spec.66")));
        p.add(Box.createVerticalStrut(10));

        p.add(createBadgeRow(
            new String[]{I18n.t("spec.67"), I18n.t("spec.68")},
            new String[]{I18n.t("spec.69"), I18n.t("spec.70")},
            new String[]{I18n.t("spec.71"), I18n.t("spec.v.tris", "100 - 600")},
            new String[]{I18n.t("spec.72"), I18n.t("spec.v.tris", "1000 - 2500")},
            new String[]{I18n.t("spec.73"), I18n.t("spec.74")}
        ));
        p.add(Box.createVerticalStrut(14));

        p.add(createSubHeader(I18n.t("spec.75")));
        p.add(createParagraph(
            I18n.t("spec.76") +
            I18n.t("spec.77") +
            I18n.t("spec.78") +
            I18n.t("spec.79") +
            I18n.t("spec.80")
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createTipBox(
            I18n.t("spec.81") +
            I18n.t("spec.82") +
            I18n.t("spec.83") +
            I18n.t("spec.84") +
            I18n.t("spec.85") +
            I18n.t("spec.86") +
            I18n.t("spec.87")
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createSubHeader(I18n.t("spec.88")));
        p.add(createParagraph(
            I18n.t("spec.89") +
            I18n.t("spec.90") +
            I18n.t("spec.91") +
            I18n.t("spec.92") +
            I18n.t("spec.93") +
            I18n.t("spec.94")
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createSubHeader(I18n.t("spec.95")));
        p.add(createParagraph(
            I18n.t("spec.96") +
            I18n.t("spec.97") +
            I18n.t("spec.98")
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createTipBox(
            I18n.t("spec.99") +
            I18n.t("spec.100") +
            I18n.t("spec.101") +
            I18n.t("spec.102") +
            I18n.t("spec.103") +
            I18n.t("spec.104") +
            I18n.t("spec.105") +
            I18n.t("spec.106") +
            I18n.t("spec.107") +
            I18n.t("spec.108") +
            I18n.t("spec.109")
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createWarningBox(
            I18n.t("spec.110") +
            I18n.t("spec.111") +
            I18n.t("spec.112")
        ));

        return wrapScroll(p);
    }

    // =========================================================================
    // 4. METALLO & AMBIENTE
    // =========================================================================
    private JComponent buildMetalEnvCard() {
        JPanel p = createCardPanel();

        p.add(createSectionTitle(I18n.t("spec.113")));
        p.add(Box.createVerticalStrut(10));

        p.add(createBadgeRow(
            new String[]{I18n.t("spec.114"), I18n.t("spec.115")},
            new String[]{I18n.t("spec.116"), I18n.t("spec.117")},
            new String[]{I18n.t("spec.118"), I18n.t("spec.119")},
            new String[]{I18n.t("spec.120"), I18n.t("spec.121")},
            new String[]{I18n.t("spec.122"), I18n.t("spec.123")}
        ));
        p.add(Box.createVerticalStrut(14));

        p.add(createSubHeader(I18n.t("spec.124")));
        p.add(createParagraph(
            I18n.t("spec.125") +
            I18n.t("spec.126") +
            I18n.t("spec.127") +
            I18n.t("spec.128")
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createTipBox(
            I18n.t("spec.129") +
            I18n.t("spec.130") +
            I18n.t("spec.131")
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createTipBox(
            I18n.t("spec.132") +
            I18n.t("spec.133") +
            I18n.t("spec.134") +
            I18n.t("spec.135") +
            I18n.t("spec.136")
        ));

        p.add(Box.createVerticalStrut(12));
        p.add(createSubHeader(I18n.t("specx.options.title")));
        p.add(createParagraph(I18n.t("specx.options.body")));

        return wrapScroll(p);
    }

    // =========================================================================
    // 5. CARTELLA CUSTOM ASSETS
    // =========================================================================
    private JComponent buildCustomAssetsCard() {
        JPanel p = createCardPanel();

        p.add(createSectionTitle(I18n.t("spec.137")));
        p.add(Box.createVerticalStrut(10));

        p.add(createParagraph(
            I18n.t("spec.138") +
            I18n.t("spec.139") +
            I18n.t("spec.140") +
            I18n.t("spec.141") +
            I18n.t("spec.142") +
            I18n.t("spec.143") +
            I18n.t("spec.144") +
            I18n.t("spec.145") +
            I18n.t("spec.146")
        ));
        p.add(Box.createVerticalStrut(16));

        JButton openBtn = new JButton(I18n.t("spec.147")) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                boolean hover = getModel().isRollover();

                g.setColor(hover ? new Color(25, 34, 54) : BG_CARD);
                g.fillRoundRect(0, 0, w, h, 6, 6);
                g.setColor(hover ? ACCENT_CYAN : BORDER_COLOR);
                g.drawRoundRect(0, 0, w - 1, h - 1, 6, 6);

                // Draw folder vector icon
                drawTabVectorIcon(g, 4, 16, (h - 12) / 2, ACCENT_CYAN);

                g.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
                g.setColor(hover ? TEXT_WHITE : ACCENT_CYAN);
                FontMetrics fm = g.getFontMetrics();
                int tw = fm.stringWidth(getText());
                g.drawString(getText(), 36, (h + fm.getAscent()) / 2 - 2);
                g.dispose();
            }
        };
        openBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 36));
        openBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        openBtn.setFocusPainted(false);
        openBtn.setBorderPainted(false);
        openBtn.setContentAreaFilled(false);
        openBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openBtn.addActionListener(e -> openCustomAssetsDirectory());
        p.add(openBtn);

        p.add(Box.createVerticalStrut(12));
        p.add(createSubHeader(I18n.t("specx.defaults.title")));
        p.add(createParagraph(I18n.t("specx.defaults.body")));

        return wrapScroll(p);
    }

    private void openCustomAssetsDirectory() {
        try {
            File dir = AppPaths.userFolder("custom_assets").toFile();
            if (!dir.exists()) dir.mkdirs();
            Desktop.getDesktop().open(dir);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, I18n.t("spec.148") + ex.getMessage(), I18n.t("spec.149"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(12, 16, 26));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        bar.setPreferredSize(new Dimension(100, 42));

        // Left: "Do Not Show Again" checkbox
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        left.setOpaque(false);

        boolean currentHide = (lobbyRef != null && "true".equalsIgnoreCase(lobbyRef.setting("hide_specs_on_start", "false")));
        doNotShowCheck.setSelected(currentHide);
        doNotShowCheck.setFont(BarMapStudio.font(Font.PLAIN, 11));
        doNotShowCheck.setForeground(TEXT_WHITE);
        doNotShowCheck.setOpaque(false);
        doNotShowCheck.setFocusPainted(false);
        doNotShowCheck.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        doNotShowCheck.addActionListener(e -> {
            if (lobbyRef != null) {
                lobbyRef.remember("hide_specs_on_start", doNotShowCheck.isSelected() ? "true" : "false");
            }
        });
        left.add(doNotShowCheck);
        bar.add(left, BorderLayout.WEST);

        // Right: Open dir + Close buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 7));
        right.setOpaque(false);

        JButton openDirBtn = new JButton(I18n.t("spec.150"));
        openDirBtn.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        openDirBtn.setBackground(BG_CARD);
        openDirBtn.setForeground(ACCENT_CYAN);
        openDirBtn.setFocusPainted(false);
        openDirBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 229, 255, 100), 1, true),
            new EmptyBorder(5, 12, 5, 12)
        ));
        openDirBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openDirBtn.addActionListener(e -> openCustomAssetsDirectory());
        right.add(openDirBtn);

        JButton closeBtn = new JButton(I18n.t("spec.151"));
        closeBtn.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        closeBtn.setBackground(BG_CARD);
        closeBtn.setForeground(TEXT_WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(5, 16, 5, 16)
        ));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> setVisible(false));
        right.add(closeBtn);

        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    // =========================================================================
    // UI Helpers & Badges
    // =========================================================================
    private JLabel createSectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BarMapStudio.fontCond(Font.BOLD, 13));
        l.setForeground(ACCENT_CYAN);
        l.setAlignmentX(0f);
        return l;
    }

    private JLabel createSubHeader(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        l.setForeground(TEXT_WHITE);
        l.setAlignmentX(0f);
        return l;
    }

    private JTextArea createParagraph(String text) {
        JTextArea ta = new JTextArea(text);
        ta.setFont(BarMapStudio.font(Font.PLAIN, 12));
        ta.setForeground(TEXT_MUTED);
        ta.setBackground(BG_DARK);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setFocusable(false);
        ta.setBorder(null);
        ta.setAlignmentX(0f);
        return ta;
    }

    private JPanel createBadgeRow(String[]... badges) {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 8));
        p.setOpaque(false);
        p.setAlignmentX(0f);

        for (String[] b : badges) {
            JPanel card = new JPanel(new BorderLayout(8, 0));
            card.setBackground(BG_CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
            ));

            JLabel title = new JLabel(b[0]);
            title.setFont(BarMapStudio.fontCond(Font.BOLD, 10));
            title.setForeground(ACCENT_CYAN);

            JLabel val = new JLabel(b[1]);
            val.setFont(BarMapStudio.font(Font.BOLD, 11));
            val.setForeground(TEXT_WHITE);

            card.add(title, BorderLayout.WEST);
            card.add(val, BorderLayout.CENTER);
            p.add(card);
        }
        return p;
    }

    private JPanel createTipBox(String text) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(new Color(18, 38, 28));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_GREEN, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        p.setAlignmentX(0f);

        // Native Vector Checkmark
        JComponent vectorCheck = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.setColor(ACCENT_GREEN);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(2, 8, 6, 13);
                g2.drawLine(6, 13, 14, 2);
                g2.dispose();
            }
        };
        vectorCheck.setPreferredSize(new Dimension(16, 16));
        p.add(vectorCheck, BorderLayout.WEST);

        JTextArea ta = new JTextArea(text);
        ta.setFont(BarMapStudio.font(Font.PLAIN, 11));
        ta.setForeground(TEXT_WHITE);
        ta.setOpaque(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setFocusable(false);
        ta.setBorder(null);
        p.add(ta, BorderLayout.CENTER);

        return p;
    }

    private JPanel createWarningBox(String text) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(new Color(38, 22, 22));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_AMBER, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        p.setAlignmentX(0f);

        // Native Vector Warning Exclamation
        JComponent vectorWarn = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.setColor(ACCENT_AMBER);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Triangle
                g2.drawLine(8, 1, 1, 14);
                g2.drawLine(1, 14, 15, 14);
                g2.drawLine(15, 14, 8, 1);
                // Exclamation
                g2.fillRect(7, 5, 2, 4);
                g2.fillRect(7, 11, 2, 2);
                g2.dispose();
            }
        };
        vectorWarn.setPreferredSize(new Dimension(16, 16));
        p.add(vectorWarn, BorderLayout.WEST);

        JTextArea ta = new JTextArea(text);
        ta.setFont(BarMapStudio.font(Font.PLAIN, 11));
        ta.setForeground(TEXT_WHITE);
        ta.setOpaque(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setFocusable(false);
        ta.setBorder(null);
        p.add(ta, BorderLayout.CENTER);

        return p;
    }
}

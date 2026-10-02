package com.faforever.neroxis.generator.bar;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import java.awt.*;
import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;

public class BarMapStudio {

    public static final Color ACCENT_CYAN = new Color(0, 229, 255);
    public static final Color ACCENT_AMBER = new Color(255, 159, 28);
    public static final Color BG_DARK = new Color(11, 14, 20);
    public static final Color BG_HEADER = new Color(14, 19, 31);
    public static final Color BG_CARD = new Color(18, 24, 38);
    public static final Color BORDER_COLOR = new Color(30, 38, 56);
    public static final Color TEXT_WHITE = new Color(241, 245, 249);
    public static final Color TEXT_MUTED = new Color(148, 163, 184);

    public static final String FONT_MAIN;
    public static final String FONT_COND;

    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.util.Set<String> set = new java.util.HashSet<>(java.util.Arrays.asList(ge.getAvailableFontFamilyNames()));
        if (set.contains("Poppins")) {
            FONT_MAIN = "Poppins";
            FONT_COND = "Poppins";
        } else if (set.contains("Roboto")) {
            FONT_MAIN = "Roboto";
            FONT_COND = set.contains("Roboto Condensed") ? "Roboto Condensed" : "Roboto";
        } else {
            FONT_MAIN = "Segoe UI";
            FONT_COND = "Segoe UI";
        }
    }

    public static Font font(int style, int size) {
        return new Font(FONT_MAIN, style, size);
    }

    public static Font fontCond(int style, int size) {
        return new Font(FONT_COND, style, size);
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.ROOT);

        Font mainFont = font(Font.BOLD, 12);
        Font condFontLarge = fontCond(Font.BOLD, 13);
        Font condFontTitle = fontCond(Font.BOLD, 16);
        UIManager.put("defaultFont", mainFont);
        UIManager.put("Label.font", mainFont);
        UIManager.put("Button.font", mainFont);
        UIManager.put("ToggleButton.font", mainFont);
        UIManager.put("RadioButton.font", mainFont);
        UIManager.put("CheckBox.font", mainFont);
        UIManager.put("TextField.font", mainFont);
        UIManager.put("TextArea.font", mainFont);
        UIManager.put("ComboBox.font", mainFont);
        UIManager.put("Spinner.font", mainFont);
        UIManager.put("List.font", mainFont);
        UIManager.put("Table.font", mainFont);
        UIManager.put("TableHeader.font", condFontLarge);
        UIManager.put("TabbedPane.font", condFontLarge);
        UIManager.put("TitledBorder.font", condFontTitle);
        UIManager.put("ToolTip.font", mainFont);
        UIManager.put("MenuItem.font", mainFont);
        UIManager.put("Menu.font", mainFont);
        UIManager.put("ProgressBar.font", mainFont);

        FlatLaf.setGlobalExtraDefaults(Map.of(
            "@accentColor", "#00E5FF",
            "@background", "#0B0E14"
        ));
        UIManager.put("Button.arc", 14);
        UIManager.put("Component.arc", 12);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("ProgressBar.arc", 12);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 11);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("Button.borderWidth", 0);
        UIManager.put("Button.background", new Color(18, 24, 38));
        UIManager.put("Panel.background", BG_DARK);

        FlatLaf.setup(new FlatDarkLaf());

        SwingUtilities.invokeLater(() -> {
            try {
                BarLauncher launcher = new BarLauncher();
                Method mShow = BarLauncher.class.getDeclaredMethod("show");
                mShow.setAccessible(true);
                mShow.invoke(launcher);
                initCustomApp(launcher);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private static void initCustomApp(BarLauncher launcher) {
        try {
            Field fFrame = BarLauncher.class.getDeclaredField("frame");
            fFrame.setAccessible(true);
            JFrame frame = (JFrame) fFrame.get(launcher);
            frame.setTitle("BAR MAP");

            Field fLobby = BarLauncher.class.getDeclaredField("lobby");
            fLobby.setAccessible(true);
            LobbyUi lobby = (LobbyUi) fLobby.get(launcher);

            Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

            // Strip native Windows titlebar completely (clean frameless window)
            try {
                frame.dispose();
                frame.setUndecorated(true);
                frame.setMaximizedBounds(maxBounds);
                frame.setMinimumSize(new Dimension(850, 480));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Determine startup display mode and generous default windowed geometry
            String savedMode = lobby != null ? lobby.setting("display", "maximized") : "maximized";
            boolean startMaximized = !"window".equalsIgnoreCase(savedMode);

            Rectangle savedWin = lobby != null ? lobby.savedWindow() : null;
            int defW = Math.min(1640, Math.max(1280, maxBounds.width - 60));
            int defH = Math.min(940, Math.max(740, maxBounds.height - 50));
            int defX = maxBounds.x + (maxBounds.width - defW) / 2;
            int defY = maxBounds.y + (maxBounds.height - defH) / 2;
            Rectangle normalBounds = (savedWin != null && savedWin.width >= 1000 && savedWin.height >= 600)
                ? savedWin
                : new Rectangle(defX, defY, defW, defH);

            frame.setBounds(normalBounds);

            // Apply Application Icon
            try {
                InputStream iconStream = BarMapStudio.class.getResourceAsStream("barmap.png");
                if (iconStream != null) {
                    Image iconImg = ImageIO.read(iconStream);
                    if (iconImg != null) {
                        frame.setIconImage(iconImg);
                    }
                    iconStream.close();
                }
            } catch (Exception ignored) {}

            // Extract fields from BarLauncher
            Field fHome = BarLauncher.class.getDeclaredField("home");
            fHome.setAccessible(true);
            Path home = (Path) fHome.get(launcher);

            Field fBarFolder = BarLauncher.class.getDeclaredField("barFolder");
            fBarFolder.setAccessible(true);
            JTextField barFolderField = (JTextField) fBarFolder.get(launcher);

            Field fBarStatus = BarLauncher.class.getDeclaredField("barStatus");
            fBarStatus.setAccessible(true);
            JLabel barStatus = (JLabel) fBarStatus.get(launcher);

            Method mRefresh = BarLauncher.class.getDeclaredMethod("refreshLibrary", Path.class);
            mRefresh.setAccessible(true);

            Method mStartEngine = BarLauncher.class.getDeclaredMethod("startEngine", Path.class, String.class);
            mStartEngine.setAccessible(true);

            Method mBuildMapsView = BarLauncher.class.getDeclaredMethod("buildMapsView");
            mBuildMapsView.setAccessible(true);
            JComponent libraryView = (JComponent) mBuildMapsView.invoke(launcher);
            if (libraryView != null && libraryView.getLayout() instanceof BorderLayout bl) {
                Component center = bl.getLayoutComponent(BorderLayout.CENTER);
                Component east = bl.getLayoutComponent(BorderLayout.EAST);
                if (center != null && east != null) {
                    libraryView.removeAll();
                    JSplitPane libSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, center, east);
                    libSplit.setDividerSize(7);
                    libSplit.setContinuousLayout(true);
                    libSplit.setBorder(null);
                    libSplit.setOpaque(false);
                    libSplit.setResizeWeight(1.0);
                    libSplit.addComponentListener(new java.awt.event.ComponentAdapter() {
                        private boolean initialSet = false;
                        @Override
                        public void componentResized(java.awt.event.ComponentEvent e) {
                            if (!initialSet && libSplit.getWidth() > 0) {
                                initialSet = true;
                                libSplit.setDividerLocation(Math.max(400, libSplit.getWidth() - 360));
                            }
                        }
                    });
                    libraryView.add(libSplit, BorderLayout.CENTER);
                }
            }

            Field fUnits = BarLauncher.class.getDeclaredField("units");
            fUnits.setAccessible(true);
            Object unitsObj = fUnits.get(launcher);
            Method mUnitsView = unitsObj.getClass().getDeclaredMethod("view");
            mUnitsView.setAccessible(true);
            JComponent unitsView = (JComponent) mUnitsView.invoke(unitsObj);
            Method mEnsureLoaded = unitsObj.getClass().getDeclaredMethod("ensureLoaded");
            mEnsureLoaded.setAccessible(true);

            // Determine data folder
            Path barData = null;
            if (barFolderField != null && barFolderField.getText() != null && !barFolderField.getText().isBlank()) {
                Path p = Path.of(barFolderField.getText().trim());
                if (BarFolder.isData(p)) {
                    barData = p;
                }
            }
            if (barData == null) {
                barData = BarFolder.find(null);
            }
            if (barData == null) {
                barData = BarFolder.usual();
            }
            final Path finalBarData = barData;

            // Instantiate Map Editor Panel
            MapStudioPanel editorPanel = new MapStudioPanel(
                home,
                finalBarData,
                archive -> {
                    try {
                        mRefresh.invoke(launcher, archive);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                },
                archive -> {
                    try {
                        mStartEngine.invoke(launcher, finalBarData.resolve("_neroxis_skirmish.txt"), null);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            );

            // Build Unified MAPS Master Panel (Editor sub-tab + Library sub-tab)
            MapsMasterPanel mapsMasterPanel = new MapsMasterPanel(editorPanel, libraryView);

            // Master CardLayout for top-level tabs (MAPS, TEXTURES and UNITS)
            CardLayout masterCards = new CardLayout();
            JPanel masterViewPanel = new JPanel(masterCards);
            masterViewPanel.setOpaque(false);

            final JToggleButton[] mapsTabHolder = new JToggleButton[1];
            final JToggleButton[] texturesTabHolder = new JToggleButton[1];

            // TextureStudioPanel per generazione texture e splatting multi-materiale
            TextureStudioPanel textureStudioPanel = new TextureStudioPanel(
                home,
                finalBarData,
                (diffuseMap, splatLow, splatHigh, customHeightmap, name) -> {
                    editorPanel.applyComposedTerrain(diffuseMap, splatLow, splatHigh, customHeightmap, name);
                    masterCards.show(masterViewPanel, "MAPS");
                    if (mapsTabHolder[0] != null) mapsTabHolder[0].setSelected(true);
                }
            );

            editorPanel.setOpenTextureStudioCallback(() -> {
                textureStudioPanel.setActiveMap(editorPanel.getCurrentMap(), editorPanel.getCurrentGround(), editorPanel.getCurrentSlopeOverlay());
                masterCards.show(masterViewPanel, "TEXTURES");
                if (texturesTabHolder[0] != null) texturesTabHolder[0].setSelected(true);
            });

            editorPanel.setOnMapChangedCallback(() -> {
                textureStudioPanel.setActiveMap(editorPanel.getCurrentMap(), editorPanel.getCurrentGround(), editorPanel.getCurrentSlopeOverlay());
            });

            if (editorPanel.getCurrentMap() != null) {
                textureStudioPanel.setActiveMap(editorPanel.getCurrentMap(), editorPanel.getCurrentGround(), editorPanel.getCurrentSlopeOverlay());
            }

            masterViewPanel.add(mapsMasterPanel, "MAPS");
            masterViewPanel.add(textureStudioPanel, "TEXTURES");
            masterViewPanel.add(unitsView, "UNITS");

            // Build Brand-New Modern Header
            JPanel customHeader = buildCustomHeader(frame, launcher, lobby, masterCards, masterViewPanel, mEnsureLoaded, unitsObj, mapsTabHolder, texturesTabHolder, textureStudioPanel, editorPanel);

            // Build Slim Modern Footer with Status & Help Overlays
            JPanel customFooter = buildCustomFooter(frame, launcher, barStatus, lobby);

            // Construct Root Panel (100% full width, ZERO ONLINE DOCK!)
            JPanel customRoot = new JPanel(new BorderLayout());
            customRoot.setBackground(BG_DARK);
            customRoot.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            customRoot.add(customHeader, BorderLayout.NORTH);
            customRoot.add(masterViewPanel, BorderLayout.CENTER);
            customRoot.add(customFooter, BorderLayout.SOUTH);

            // Add edge window resizer for undecorated window
            new WindowResizer(frame, lobby);

            frame.setContentPane(customRoot);
            frame.revalidate();
            frame.repaint();

            if (startMaximized) {
                frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                frame.setBounds(maxBounds);
            } else {
                frame.setExtendedState(Frame.NORMAL);
            }

            frame.setVisible(true);

            // Mostra automaticamente la finestra delle specifiche all'avvio se non silenziata
            SwingUtilities.invokeLater(() -> AssetSpecsDialog.showOnStartup(frame, lobby));

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void toggleMaximize(JFrame frame, LobbyUi lobby) {
        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        frame.setMaximizedBounds(maxBounds);
        if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
            frame.setExtendedState(Frame.NORMAL);
            if (lobby != null) {
                lobby.remember("display", "window");
                lobby.saveWindow(frame.getBounds());
            }
        } else {
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            frame.setBounds(maxBounds);
            if (lobby != null) {
                lobby.remember("display", "maximized");
            }
        }
        frame.repaint();
    }

    private static void makeDraggable(JComponent comp, JFrame frame, Point[] dragPoint, LobbyUi lobby) {
        comp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Point p = SwingUtilities.convertPoint(comp, e.getPoint(), frame);
                    if (p.y <= 7) return; // Allow top edge resize
                    dragPoint[0] = e.getPoint();
                }
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    toggleMaximize(frame, lobby);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                dragPoint[0] = null;
                if (lobby != null && (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == 0) {
                    lobby.saveWindow(frame.getBounds());
                }
            }
        });
        comp.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragPoint[0] != null) {
                    if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                        frame.setExtendedState(Frame.NORMAL);
                        if (lobby != null) {
                            lobby.remember("display", "window");
                        }
                    }
                    Point current = e.getLocationOnScreen();
                    frame.setLocation(current.x - dragPoint[0].x, current.y - dragPoint[0].y);
                }
            }
        });
    }

    private static JPanel buildCustomHeader(
        JFrame frame,
        BarLauncher launcher,
        LobbyUi lobby,
        CardLayout masterCards,
        JPanel masterViewPanel,
        Method mEnsureLoaded,
        Object unitsObj,
        JToggleButton[] mapsTabHolder,
        JToggleButton[] texturesTabHolder,
        TextureStudioPanel textureStudioPanel,
        MapStudioPanel editorPanel
    ) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_HEADER);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(100, 56));

        // Make whole custom header draggable
        final Point[] dragPoint = new Point[1];
        makeDraggable(header, frame, dragPoint, lobby);

        // Left: Logo and Badge
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 14));
        left.setOpaque(false);
        makeDraggable(left, frame, dragPoint, lobby);

        URL logoUrl = BarMapStudio.class.getResource("barmap.png");
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            Image scaled = icon.getImage().getScaledInstance(160, 17, Image.SCALE_SMOOTH);
            left.add(new JLabel(new ImageIcon(scaled)));
        } else {
            JLabel title = new JLabel("BAR MAP");
            title.setFont(fontCond(Font.BOLD, 20));
            title.setForeground(ACCENT_CYAN);
            left.add(title);
        }

        JLabel badge = new JLabel("  STANDALONE  ");
        badge.setFont(fontCond(Font.BOLD, 10));
        badge.setForeground(TEXT_MUTED);
        badge.setBackground(new Color(22, 28, 42));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        left.add(badge);

        header.add(left, BorderLayout.WEST);

        // Center: Main Navigation Tabs (MAPS, TEXTURES and UNITS)
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        center.setOpaque(false);

        ButtonGroup navGroup = new ButtonGroup();
        JToggleButton mapsTab = createTopNavButton("MAPS", "MAP", true);
        JToggleButton texturesTab = createTopNavButton("TEXTURES", "PALETTE", false);
        JToggleButton unitsTab = createTopNavButton("UNITS", "SWORDS", false);

        if (mapsTabHolder != null && mapsTabHolder.length > 0) mapsTabHolder[0] = mapsTab;
        if (texturesTabHolder != null && texturesTabHolder.length > 0) texturesTabHolder[0] = texturesTab;

        navGroup.add(mapsTab);
        navGroup.add(texturesTab);
        navGroup.add(unitsTab);

        mapsTab.addActionListener(e -> {
            masterCards.show(masterViewPanel, "MAPS");
        });

        texturesTab.addActionListener(e -> {
            if (textureStudioPanel != null && editorPanel != null) {
                if (editorPanel.getCurrentMap() != null) {
                    textureStudioPanel.setActiveMap(editorPanel.getCurrentMap(), editorPanel.getCurrentGround(), editorPanel.getCurrentSlopeOverlay());
                }
            }
            masterCards.show(masterViewPanel, "TEXTURES");
        });

        unitsTab.addActionListener(e -> {
            try {
                mEnsureLoaded.invoke(unitsObj);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            masterCards.show(masterViewPanel, "UNITS");
        });

        center.add(mapsTab);
        center.add(texturesTab);
        center.add(unitsTab);
        header.add(center, BorderLayout.CENTER);

        // Right: Window Controls Only (Clean & Symmetrical)
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        right.setOpaque(false);

        JPanel windowBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        windowBtns.setOpaque(false);

        JButton minBtn = new WindowControlButton(WindowControlType.MIN, "Riduci a icona");
        minBtn.addActionListener(e -> frame.setExtendedState(frame.getExtendedState() | Frame.ICONIFIED));

        JButton maxBtn = new WindowControlButton(WindowControlType.MAX, "Ingrandisci / Finestra");
        maxBtn.addActionListener(e -> toggleMaximize(frame, lobby));

        JButton closeBtn = new WindowControlButton(WindowControlType.CLOSE, "Chiudi");
        closeBtn.addActionListener(e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));

        windowBtns.add(minBtn);
        windowBtns.add(maxBtn);
        windowBtns.add(closeBtn);
        right.add(windowBtns);

        header.add(right, BorderLayout.EAST);
        return header;
    }

    private static JPanel buildCustomFooter(JFrame frame, BarLauncher launcher, JLabel barStatus, LobbyUi lobby) {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_HEADER);
        footer.setPreferredSize(new Dimension(100, 24));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        // Left: Studio Branding
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 3));
        left.setOpaque(false);
        JLabel brand = new JLabel("BAR MAP STUDIO");
        brand.setFont(fontCond(Font.BOLD, 10));
        brand.setForeground(new Color(100, 116, 139));
        left.add(brand);
        footer.add(left, BorderLayout.WEST);

        // Right: BAR Status Pill Button + Asset Specs Button + Help Button
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        right.setOpaque(false);

        JButton barBtn = createBarStatusOverlayButton(launcher, barStatus);
        JButton specsBtn = createSpecsOverlayButton(frame, lobby);
        JButton helpBtn = createHelpOverlayButton();

        right.add(barBtn);
        right.add(specsBtn);
        right.add(helpBtn);
        footer.add(right, BorderLayout.EAST);

        return footer;
    }

    private static JButton createBarStatusOverlayButton(BarLauncher launcher, JLabel barStatus) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                boolean hover = getModel().isRollover();

                g.setColor(hover ? new Color(25, 34, 54) : BG_CARD);
                g.fillRoundRect(0, 0, w, h, 8, 8);
                g.setColor(hover ? ACCENT_CYAN : BORDER_COLOR);
                g.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);

                Color fg = barStatus.getForeground();
                if (fg == null || fg.equals(TEXT_WHITE)) fg = new Color(34, 197, 94);

                // Vector LED dot with glow
                g.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 60));
                g.fillOval(7, (h - 10) / 2, 10, 10);
                g.setColor(fg);
                g.fillOval(9, (h - 6) / 2, 6, 6);

                // Label
                g.setFont(fontCond(Font.BOLD, 10));
                g.setColor(hover ? TEXT_WHITE : TEXT_MUTED);
                FontMetrics fm = g.getFontMetrics();
                g.drawString("BAR", 23, (h + fm.getAscent()) / 2 - 2);
                g.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(56, 20));
        btn.setToolTipText("Dettagli connessione BAR ed Engine (Clicca per visualizzare overlay)");
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> showBarStatusOverlay(btn, launcher, barStatus));
        return btn;
    }

    private static void showBarStatusOverlay(Component anchor, BarLauncher launcher, JLabel barStatus) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(new Color(14, 19, 31));
        popup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 229, 255, 140), 1, true),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel title = new JLabel("STATO BEYOND ALL REASON");
        title.setFont(fontCond(Font.BOLD, 12));
        title.setForeground(ACCENT_CYAN);
        title.setAlignmentX(0f);
        content.add(title);
        content.add(Box.createVerticalStrut(8));

        String raw = barStatus.getText();
        if (raw == null || raw.isEmpty()) raw = "Verifica stato in corso...";
        raw = raw.replaceFirst("^[●•\u25CF]\\s*", "");

        Color fg = barStatus.getForeground();
        boolean ok = (fg != null && fg.getRed() < 100);

        JLabel statusLine = new JLabel((ok ? "✓ " : "✕ ") + raw);
        statusLine.setFont(font(Font.BOLD, 11));
        statusLine.setForeground(ok ? new Color(34, 197, 94) : new Color(239, 68, 68));
        statusLine.setAlignmentX(0f);
        content.add(statusLine);
        content.add(Box.createVerticalStrut(6));

        String tip = barStatus.getToolTipText();
        if (tip != null && !tip.isEmpty()) {
            JLabel detailsLbl = new JLabel(tip);
            detailsLbl.setFont(font(Font.PLAIN, 11));
            detailsLbl.setForeground(TEXT_MUTED);
            detailsLbl.setAlignmentX(0f);
            content.add(detailsLbl);
            content.add(Box.createVerticalStrut(8));
        }

        try {
            Field fBarFolder = BarLauncher.class.getDeclaredField("barFolder");
            fBarFolder.setAccessible(true);
            JTextField barFolder = (JTextField) fBarFolder.get(launcher);
            String path = barFolder.getText().trim();
            if (!path.isEmpty()) {
                JButton openFolderBtn = new JButton("Apri Cartella BAR");
                openFolderBtn.setFont(fontCond(Font.BOLD, 11));
                openFolderBtn.setBackground(new Color(18, 24, 38));
                openFolderBtn.setForeground(ACCENT_CYAN);
                openFolderBtn.setFocusPainted(false);
                openFolderBtn.setAlignmentX(0f);
                openFolderBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 229, 255, 80), 1, true),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                openFolderBtn.addActionListener(ev -> {
                    popup.setVisible(false);
                    try {
                        Desktop.getDesktop().open(new java.io.File(path));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                content.add(openFolderBtn);
            }
        } catch (Throwable ignored) {}

        popup.add(content);
        Dimension ps = popup.getPreferredSize();
        popup.show(anchor, anchor.getWidth() - ps.width, -ps.height - 4);
    }

    private static JButton createSpecsOverlayButton(JFrame frame, LobbyUi lobby) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                int w = getWidth();
                int h = getHeight();
                boolean hover = getModel().isRollover();

                g.setColor(hover ? new Color(25, 34, 54) : BG_CARD);
                g.fillRoundRect(0, 0, w, h, 8, 8);
                g.setColor(hover ? ACCENT_CYAN : BORDER_COLOR);
                g.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);

                Color fg = hover ? ACCENT_CYAN : TEXT_MUTED;
                g.setColor(fg);

                // Native Vector Info Circle
                int iconX = 7;
                int iconY = (h - 10) / 2;
                g.setStroke(new BasicStroke(1.2f));
                g.drawOval(iconX, iconY, 10, 10);
                g.fillRect(iconX + 4, iconY + 2, 2, 2);
                g.fillRect(iconX + 4, iconY + 5, 2, 3);

                g.setFont(fontCond(Font.BOLD, 10));
                g.drawString("SPECS", 21, (h + g.getFontMetrics().getAscent()) / 2 - 2);
                g.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(66, 20));
        btn.setToolTipText("Linee guida e specifiche tecniche per Heightmap, Texture e Modelli 3D (Clicca per aprire)");
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> AssetSpecsDialog.toggle(frame, lobby));
        return btn;
    }

    private static JButton createHelpOverlayButton() {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                boolean hover = getModel().isRollover();

                g.setColor(hover ? new Color(0, 229, 255, 40) : BG_CARD);
                g.fillRoundRect(0, 0, w, h, 8, 8);
                g.setColor(hover ? ACCENT_CYAN : BORDER_COLOR);
                g.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);

                g.setFont(fontCond(Font.BOLD, 11));
                g.setColor(hover ? ACCENT_CYAN : TEXT_MUTED);
                FontMetrics fm = g.getFontMetrics();
                int tw = fm.stringWidth("?");
                g.drawString("?", (w - tw) / 2, (h + fm.getAscent()) / 2 - 2);
                g.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(24, 20));
        btn.setToolTipText("Guida e scorciatoie da tastiera (Clicca per visualizzare overlay)");
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> showHelpOverlay(btn));
        return btn;
    }

    private static void showHelpOverlay(Component anchor) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(new Color(14, 19, 31));
        popup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 229, 255, 140), 1, true),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("SCORCIATOIE & CONTROLLI MAP STUDIO");
        title.setFont(fontCond(Font.BOLD, 12));
        title.setForeground(ACCENT_CYAN);
        title.setAlignmentX(0f);
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        String[][] shortcuts = {
            {"[ S ]", "Seleziona ed ispeziona entità"},
            {"[ M ]", "Piazza Mex (Estrattore metallo)"},
            {"[ 1 - 5 ]", "Imposta valore metallo Mex rapido"},
            {"[ G ]", "Piazza Geotermico (Fessura idrotermale)"},
            {"[ P ]", "Piazza Spawn Giocatore"},
            {"[ R ]", "Piazza Rocce da Reclaim"},
            {"[ T ]", "Piazza Alberi da Reclaim"},
            {"[ W ]", "Piazza Relitti da Reclaim"},
            {"[ E ]", "Gomma: cancella entità sotto cursore"},
            {"[ DEL ]", "Elimina entità attualmente selezionata"},
            {"[ Spazio ]", "Attiva / Disattiva modalità Simmetria"},
            {"[ X ]", "Cicla Asse Simmetria (Diag / Z / X / Quad)"},
            {"[ Ctrl+Z / Y ]", "Annulla (Undo) e Ripristina (Redo)"},
            {"[ L ]", "Attiva / Disattiva Slope Map (Pendenze)"},
            {"[ Rotellina ]", "Zoom dinamico centrato su cursore"},
            {"[ Drag Destro ]", "Sposta visuale mappa (Pan)"}
        };

        JPanel grid = new JPanel(new GridLayout(shortcuts.length, 2, 14, 4));
        grid.setOpaque(false);

        for (String[] sc : shortcuts) {
            JLabel keyLbl = new JLabel(sc[0]);
            keyLbl.setFont(fontCond(Font.BOLD, 11));
            keyLbl.setForeground(ACCENT_CYAN);
            grid.add(keyLbl);

            JLabel descLbl = new JLabel(sc[1]);
            descLbl.setFont(font(Font.PLAIN, 11));
            descLbl.setForeground(TEXT_WHITE);
            grid.add(descLbl);
        }
        grid.setAlignmentX(0f);
        panel.add(grid);

        popup.add(panel);
        Dimension ps = popup.getPreferredSize();
        popup.show(anchor, anchor.getWidth() - ps.width, -ps.height - 4);
    }

    private static JToggleButton createTopNavButton(String text, String iconType, boolean selected) {
        return new TopNavButton(text, iconType, selected);
    }

    public static class TopNavButton extends JToggleButton {
        private final String iconType;

        public TopNavButton(String text, String iconType, boolean selected) {
            super(text, selected);
            this.iconType = iconType;
            setFont(fontCond(Font.BOLD, 13));
            setPreferredSize(new Dimension(130, 34));
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
                g.setColor(new Color(0, 229, 255, 30));
                g.fillRoundRect(0, 0, w, h, 14, 14);
                g.setColor(ACCENT_CYAN);
                g.setStroke(new BasicStroke(1.4f));
                g.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);
                fg = ACCENT_CYAN;
            } else {
                if (getModel().isRollover()) {
                    g.setColor(new Color(255, 255, 255, 12));
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

            int iconSize = 14;
            int gap = 8;
            int totalContentW = iconSize + gap + tw;
            int startX = (w - totalContentW) / 2;
            int iconY = (h - iconSize) / 2;

            if ("MAP".equals(iconType)) {
                drawMapVector(g, startX, iconY, iconSize, iconSize, fg);
            } else if ("PALETTE".equals(iconType)) {
                drawPaletteVector(g, startX, iconY, iconSize, iconSize, fg);
            } else if ("SWORDS".equals(iconType)) {
                drawSwordsVector(g, startX, iconY, iconSize, iconSize, fg);
            }

            g.setColor(fg);
            g.drawString(getText(), startX + iconSize + gap, (h + th) / 2 - 2);

            g.dispose();
        }
    }

    public static void drawPaletteVector(Graphics2D g, int x, int y, int w, int h, Color c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(c);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Disegna profilo sagomato della tavolozza artistica
        java.awt.geom.Path2D.Float p = new java.awt.geom.Path2D.Float();
        p.moveTo(x + w * 0.5f, y + 1);
        p.curveTo(x + w, y + 1, x + w, y + h * 0.7f, x + w * 0.7f, y + h - 1);
        p.curveTo(x + w * 0.45f, y + h - 1, x + w * 0.4f, y + h * 0.65f, x + w * 0.25f, y + h * 0.75f);
        p.curveTo(x + 1, y + h * 0.85f, x + 1, y + 1, x + w * 0.5f, y + 1);
        p.closePath();
        g2.draw(p);
        // Macchie di colore / spot geometrici
        g2.fillOval((int)(x + w * 0.35f), (int)(y + h * 0.28f), 2, 2);
        g2.fillOval((int)(x + w * 0.65f), (int)(y + h * 0.30f), 2, 2);
        g2.fillOval((int)(x + w * 0.70f), (int)(y + h * 0.55f), 2, 2);
        g2.dispose();
    }

    public static void drawMapVector(Graphics2D g, int x, int y, int w, int h, Color c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(c);
        g2.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        float p1 = w * 0.33f;
        float p2 = w * 0.66f;
        float drop = 2.0f;

        java.awt.geom.Path2D.Float map = new java.awt.geom.Path2D.Float();
        map.moveTo(x, y + drop);
        map.lineTo(x + p1, y);
        map.lineTo(x + p2, y + drop);
        map.lineTo(x + w, y);
        map.lineTo(x + w, y + h - drop);
        map.lineTo(x + p2, y + h);
        map.lineTo(x + p1, y + h - drop);
        map.lineTo(x, y + h);
        map.closePath();
        g2.draw(map);

        g2.drawLine(Math.round(x + p1), Math.round(y), Math.round(x + p1), Math.round(y + h - drop));
        g2.drawLine(Math.round(x + p2), Math.round(y + drop), Math.round(x + p2), Math.round(y + h));
        g2.dispose();
    }

    public static void drawSwordsVector(Graphics2D g, int x, int y, int w, int h, Color c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(c);
        g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + 2, y + h - 2, x + w - 2, y + 2);
        g2.drawLine(x + w - 2, y + h - 2, x + 2, y + 2);
        g2.drawLine(x + 1, y + h - 5, x + 5, y + h - 1);
        g2.drawLine(x + w - 1, y + h - 5, x + w - 5, y + h - 1);
        g2.dispose();
    }

    public enum WindowControlType { MIN, MAX, CLOSE }

    public static class WindowControlButton extends JButton {
        private final WindowControlType type;

        public WindowControlButton(WindowControlType type, String tip) {
            super();
            this.type = type;
            setToolTipText(tip);
            setPreferredSize(new Dimension(32, 28));
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
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int w = getWidth();
            int h = getHeight();
            int cx = w / 2;
            int cy = h / 2;

            boolean hover = getModel().isRollover();
            Color iconColor;
            if (hover) {
                if (type == WindowControlType.CLOSE) {
                    g.setColor(new Color(239, 68, 68));
                    g.fillRoundRect(2, 2, w - 4, h - 4, 6, 6);
                    iconColor = Color.WHITE;
                } else {
                    g.setColor(new Color(255, 255, 255, 18));
                    g.fillRoundRect(2, 2, w - 4, h - 4, 6, 6);
                    iconColor = TEXT_WHITE;
                }
            } else {
                iconColor = TEXT_MUTED;
            }

            g.setColor(iconColor);
            g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            if (type == WindowControlType.MIN) {
                g.drawLine(cx - 5, cy + 3, cx + 5, cy + 3);
            } else if (type == WindowControlType.MAX) {
                Window win = SwingUtilities.getWindowAncestor(this);
                boolean isMax = (win instanceof Frame f) && ((f.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH);
                if (isMax) {
                    g.drawRect(cx - 3, cy - 6, 8, 8);
                    g.clearRect(cx - 6, cy - 3, 8, 8);
                    g.setColor(hover ? new Color(255, 255, 255, 25) : BG_HEADER);
                    g.fillRect(cx - 6, cy - 3, 8, 8);
                    g.setColor(iconColor);
                    g.drawRect(cx - 6, cy - 3, 8, 8);
                } else {
                    g.drawRect(cx - 5, cy - 5, 10, 10);
                }
            } else if (type == WindowControlType.CLOSE) {
                g.drawLine(cx - 4, cy - 4, cx + 4, cy + 4);
                g.drawLine(cx + 4, cy - 4, cx - 4, cy + 4);
            }

            g.dispose();
        }
    }

    public static class WindowResizer implements AWTEventListener {
        private static final int BORDER_DRAG_THICKNESS = 7;
        private final JFrame frame;
        private final LobbyUi lobby;
        private Point clickPoint;
        private Rectangle initialBounds;
        private int cursorDirection = 0;

        private static final int NORTH = 1;
        private static final int SOUTH = 2;
        private static final int WEST = 4;
        private static final int EAST = 8;

        public WindowResizer(JFrame frame, LobbyUi lobby) {
            this.frame = frame;
            this.lobby = lobby;
            Toolkit.getDefaultToolkit().addAWTEventListener(this,
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        }

        private int getDirection(Point p) {
            if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                return 0;
            }
            int dir = 0;
            int w = frame.getWidth();
            int h = frame.getHeight();
            if (p.y >= 0 && p.y <= BORDER_DRAG_THICKNESS) dir |= NORTH;
            else if (p.y >= h - BORDER_DRAG_THICKNESS && p.y <= h) dir |= SOUTH;

            if (p.x >= 0 && p.x <= BORDER_DRAG_THICKNESS) dir |= WEST;
            else if (p.x >= w - BORDER_DRAG_THICKNESS && p.x <= w) dir |= EAST;
            return dir;
        }

        private int getCursorForDirection(int dir) {
            return switch (dir) {
                case NORTH -> Cursor.N_RESIZE_CURSOR;
                case SOUTH -> Cursor.S_RESIZE_CURSOR;
                case WEST -> Cursor.W_RESIZE_CURSOR;
                case EAST -> Cursor.E_RESIZE_CURSOR;
                case NORTH | WEST -> Cursor.NW_RESIZE_CURSOR;
                case NORTH | EAST -> Cursor.NE_RESIZE_CURSOR;
                case SOUTH | WEST -> Cursor.SW_RESIZE_CURSOR;
                case SOUTH | EAST -> Cursor.SE_RESIZE_CURSOR;
                default -> Cursor.DEFAULT_CURSOR;
            };
        }

        private static boolean isResizeCursor(Cursor c) {
            if (c == null) return false;
            int t = c.getType();
            return t == Cursor.N_RESIZE_CURSOR || t == Cursor.S_RESIZE_CURSOR
                || t == Cursor.E_RESIZE_CURSOR || t == Cursor.W_RESIZE_CURSOR
                || t == Cursor.NW_RESIZE_CURSOR || t == Cursor.NE_RESIZE_CURSOR
                || t == Cursor.SW_RESIZE_CURSOR || t == Cursor.SE_RESIZE_CURSOR;
        }

        @Override
        public void eventDispatched(AWTEvent event) {
            if (!(event instanceof MouseEvent e)) return;
            Component src = e.getComponent();
            if (src == null) return;
            if (!SwingUtilities.isDescendingFrom(src, frame) && src != frame) return;

            Point p = SwingUtilities.convertPoint(src, e.getPoint(), frame);
            int dir = getDirection(p);

            switch (e.getID()) {
                case MouseEvent.MOUSE_MOVED -> {
                    if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                        if (isResizeCursor(frame.getCursor())) frame.setCursor(Cursor.getDefaultCursor());
                        return;
                    }
                    if (cursorDirection == 0) {
                        if (dir != 0) {
                            frame.setCursor(Cursor.getPredefinedCursor(getCursorForDirection(dir)));
                        } else if (isResizeCursor(frame.getCursor())) {
                            frame.setCursor(Cursor.getDefaultCursor());
                        }
                    }
                }
                case MouseEvent.MOUSE_PRESSED -> {
                    if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                        return;
                    }
                    if (SwingUtilities.isLeftMouseButton(e) && dir != 0) {
                        cursorDirection = dir;
                        clickPoint = e.getLocationOnScreen();
                        initialBounds = frame.getBounds();
                        e.consume();
                    }
                }
                case MouseEvent.MOUSE_DRAGGED -> {
                    if (cursorDirection != 0 && clickPoint != null && initialBounds != null) {
                        Point current = e.getLocationOnScreen();
                        int dx = current.x - clickPoint.x;
                        int dy = current.y - clickPoint.y;

                        int newX = initialBounds.x;
                        int newY = initialBounds.y;
                        int newW = initialBounds.width;
                        int newH = initialBounds.height;

                        int minW = 850;
                        int minH = 480;

                        if ((cursorDirection & WEST) != 0) {
                            int nw = initialBounds.width - dx;
                            if (nw >= minW) {
                                newX = initialBounds.x + dx;
                                newW = nw;
                            }
                        } else if ((cursorDirection & EAST) != 0) {
                            newW = Math.max(minW, initialBounds.width + dx);
                        }

                        if ((cursorDirection & NORTH) != 0) {
                            int nh = initialBounds.height - dy;
                            if (nh >= minH) {
                                newY = initialBounds.y + dy;
                                newH = nh;
                            }
                        } else if ((cursorDirection & SOUTH) != 0) {
                            newH = Math.max(minH, initialBounds.height + dy);
                        }

                        frame.setBounds(newX, newY, newW, newH);
                        frame.revalidate();
                        frame.repaint();
                        e.consume();
                    }
                }
                case MouseEvent.MOUSE_RELEASED -> {
                    if (cursorDirection != 0) {
                        cursorDirection = 0;
                        clickPoint = null;
                        initialBounds = null;
                        if (dir != 0) {
                            frame.setCursor(Cursor.getPredefinedCursor(getCursorForDirection(dir)));
                        } else {
                            frame.setCursor(Cursor.getDefaultCursor());
                        }
                        if (lobby != null && (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == 0) {
                            lobby.saveWindow(frame.getBounds());
                        }
                        e.consume();
                    }
                }
                case MouseEvent.MOUSE_EXITED -> {
                    if (cursorDirection == 0 && isResizeCursor(frame.getCursor())) {
                        frame.setCursor(Cursor.getDefaultCursor());
                    }
                }
            }
        }
    }
}

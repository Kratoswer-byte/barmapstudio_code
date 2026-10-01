package com.faforever.neroxis.generator.bar;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import java.awt.*;
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

    public static void main(String[] args) {
        Locale.setDefault(Locale.ROOT);

        Font boldFont = new Font("Segoe UI", Font.BOLD, 12);
        Font boldFontLarge = new Font("Segoe UI", Font.BOLD, 13);
        Font boldFontTitle = new Font("Segoe UI", Font.BOLD, 16);
        UIManager.put("defaultFont", boldFont);
        UIManager.put("Label.font", boldFont);
        UIManager.put("Button.font", boldFont);
        UIManager.put("ToggleButton.font", boldFont);
        UIManager.put("RadioButton.font", boldFont);
        UIManager.put("CheckBox.font", boldFont);
        UIManager.put("TextField.font", boldFont);
        UIManager.put("TextArea.font", boldFont);
        UIManager.put("ComboBox.font", boldFont);
        UIManager.put("Spinner.font", boldFont);
        UIManager.put("List.font", boldFont);
        UIManager.put("Table.font", boldFont);
        UIManager.put("TableHeader.font", boldFontLarge);
        UIManager.put("TabbedPane.font", boldFontLarge);
        UIManager.put("TitledBorder.font", boldFontTitle);
        UIManager.put("ToolTip.font", boldFont);
        UIManager.put("MenuItem.font", boldFont);
        UIManager.put("Menu.font", boldFont);
        UIManager.put("ProgressBar.font", boldFont);

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

            // Strip native Windows titlebar completely (clean frameless window)
            try {
                frame.dispose();
                frame.setUndecorated(true);
                Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
                frame.setMaximizedBounds(maxBounds);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

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

            // Master CardLayout for top-level tabs (MAPS and UNITS)
            CardLayout masterCards = new CardLayout();
            JPanel masterViewPanel = new JPanel(masterCards);
            masterViewPanel.setOpaque(false);
            masterViewPanel.add(mapsMasterPanel, "MAPS");
            masterViewPanel.add(unitsView, "UNITS");

            // Build Brand-New Modern Header
            JPanel customHeader = buildCustomHeader(frame, launcher, masterCards, masterViewPanel, mEnsureLoaded, unitsObj, barStatus);

            // Construct Root Panel (100% full width, ZERO ONLINE DOCK!)
            JPanel customRoot = new JPanel(new BorderLayout());
            customRoot.setBackground(BG_DARK);
            customRoot.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            customRoot.add(customHeader, BorderLayout.NORTH);
            customRoot.add(masterViewPanel, BorderLayout.CENTER);

            // Add edge window resizer for undecorated window
            WindowResizer resizer = new WindowResizer(frame);
            customRoot.addMouseListener(resizer);
            customRoot.addMouseMotionListener(resizer);

            frame.setContentPane(customRoot);
            frame.revalidate();
            frame.repaint();
            frame.setVisible(true);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void toggleMaximize(JFrame frame) {
        if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
            frame.setExtendedState(Frame.NORMAL);
        } else {
            Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
            frame.setMaximizedBounds(maxBounds);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
    }

    private static void makeDraggable(JComponent comp, JFrame frame, Point[] dragPoint) {
        comp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dragPoint[0] = e.getPoint();
                }
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    toggleMaximize(frame);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                dragPoint[0] = null;
            }
        });
        comp.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragPoint[0] != null) {
                    if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                        frame.setExtendedState(Frame.NORMAL);
                    }
                    Point current = e.getLocationOnScreen();
                    frame.setLocation(current.x - dragPoint[0].x, current.y - dragPoint[0].y);
                }
            }
        });
    }

    private static JPanel buildCustomHeader(JFrame frame, BarLauncher launcher, CardLayout masterCards, JPanel masterViewPanel, Method mEnsureLoaded, Object unitsObj, JLabel barStatus) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_HEADER);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(100, 56));

        // Make whole custom header draggable
        final Point[] dragPoint = new Point[1];
        makeDraggable(header, frame, dragPoint);

        // Left: Logo and Badge
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 14));
        left.setOpaque(false);
        makeDraggable(left, frame, dragPoint);

        URL logoUrl = BarMapStudio.class.getResource("barmap.png");
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            Image scaled = icon.getImage().getScaledInstance(160, 17, Image.SCALE_SMOOTH);
            left.add(new JLabel(new ImageIcon(scaled)));
        } else {
            JLabel title = new JLabel("BAR MAP");
            title.setFont(new Font("Segoe UI", Font.BOLD, 20));
            title.setForeground(ACCENT_CYAN);
            left.add(title);
        }

        JLabel badge = new JLabel("  STANDALONE  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(TEXT_MUTED);
        badge.setBackground(new Color(22, 28, 42));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        left.add(badge);

        header.add(left, BorderLayout.WEST);

        // Center: Main Navigation Tabs (MAPS and UNITS)
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        center.setOpaque(false);

        ButtonGroup navGroup = new ButtonGroup();
        JToggleButton mapsTab = createTopNavButton("🗺️  MAPS", true);
        JToggleButton unitsTab = createTopNavButton("⚔️  UNITS", false);

        navGroup.add(mapsTab);
        navGroup.add(unitsTab);

        mapsTab.addActionListener(e -> {
            masterCards.show(masterViewPanel, "MAPS");
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
        center.add(unitsTab);
        header.add(center, BorderLayout.CENTER);

        // Right: BAR Status & Window Controls
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        right.setOpaque(false);

        barStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        barStatus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        right.add(barStatus);

        // Window Controls
        JPanel windowBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        windowBtns.setOpaque(false);

        JButton minBtn = createWindowControlButton("–", "Riduci a icona");
        minBtn.addActionListener(e -> frame.setExtendedState(frame.getExtendedState() | Frame.ICONIFIED));

        JButton maxBtn = createWindowControlButton("□", "Ingrandisci / Finestra");
        maxBtn.addActionListener(e -> toggleMaximize(frame));

        JButton closeBtn = createWindowControlButton("✕", "Chiudi");
        closeBtn.addActionListener(e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));

        windowBtns.add(minBtn);
        windowBtns.add(maxBtn);
        windowBtns.add(closeBtn);
        right.add(windowBtns);

        header.add(right, BorderLayout.EAST);
        return header;
    }

    private static JToggleButton createTopNavButton(String text, boolean selected) {
        return new TopNavButton(text, selected);
    }

    public static class TopNavButton extends JToggleButton {
        public TopNavButton(String text, boolean selected) {
            super(text, selected);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setPreferredSize(new Dimension(125, 34));
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
                g.setColor(new Color(0, 229, 255, 30));
                g.fillRoundRect(0, 0, w, h, 14, 14);
                g.setColor(ACCENT_CYAN);
                g.setStroke(new BasicStroke(1.4f));
                g.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);
                g.setColor(ACCENT_CYAN);
            } else {
                if (getModel().isRollover()) {
                    g.setColor(new Color(255, 255, 255, 12));
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

    private static JButton createWindowControlButton(String text, String tip) {
        JButton btn = new JButton(text);
        btn.setToolTipText(tip);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(TEXT_MUTED);
        btn.setPreferredSize(new Dimension(32, 28));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(text.equals("✕") ? new Color(255, 96, 84) : TEXT_WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(TEXT_MUTED);
            }
        });
        return btn;
    }

    public static class WindowResizer extends MouseAdapter {
        private static final int BORDER_DRAG_THICKNESS = 6;
        private final JFrame frame;
        private Point clickPoint;
        private Rectangle initialBounds;
        private int cursorDirection = 0;

        private static final int NORTH = 1;
        private static final int SOUTH = 2;
        private static final int WEST = 4;
        private static final int EAST = 8;

        public WindowResizer(JFrame frame) {
            this.frame = frame;
        }

        private int getDirection(Point p, Dimension size) {
            int dir = 0;
            if (p.y <= BORDER_DRAG_THICKNESS) dir |= NORTH;
            else if (p.y >= size.height - BORDER_DRAG_THICKNESS) dir |= SOUTH;
            if (p.x <= BORDER_DRAG_THICKNESS) dir |= WEST;
            else if (p.x >= size.width - BORDER_DRAG_THICKNESS) dir |= EAST;
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

        @Override
        public void mouseMoved(MouseEvent e) {
            if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                frame.setCursor(Cursor.getDefaultCursor());
                return;
            }
            int dir = getDirection(e.getPoint(), frame.getSize());
            frame.setCursor(Cursor.getPredefinedCursor(getCursorForDirection(dir)));
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                cursorDirection = getDirection(e.getPoint(), frame.getSize());
                if (cursorDirection != 0) {
                    clickPoint = e.getLocationOnScreen();
                    initialBounds = frame.getBounds();
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            cursorDirection = 0;
            clickPoint = null;
            initialBounds = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (cursorDirection == 0 || clickPoint == null || initialBounds == null) return;
            Point current = e.getLocationOnScreen();
            int dx = current.x - clickPoint.x;
            int dy = current.y - clickPoint.y;

            int newX = initialBounds.x;
            int newY = initialBounds.y;
            int newW = initialBounds.width;
            int newH = initialBounds.height;

            Dimension min = frame.getMinimumSize();
            int minW = min != null ? min.width : 800;
            int minH = min != null ? min.height : 600;

            if ((cursorDirection & WEST) != 0) {
                int w = initialBounds.width - dx;
                if (w >= minW) {
                    newX = initialBounds.x + dx;
                    newW = w;
                }
            } else if ((cursorDirection & EAST) != 0) {
                newW = Math.max(minW, initialBounds.width + dx);
            }

            if ((cursorDirection & NORTH) != 0) {
                int h = initialBounds.height - dy;
                if (h >= minH) {
                    newY = initialBounds.y + dy;
                    newH = h;
                }
            } else if ((cursorDirection & SOUTH) != 0) {
                newH = Math.max(minH, initialBounds.height + dy);
            }

            frame.setBounds(newX, newY, newW, newH);
            frame.validate();
        }
    }
}

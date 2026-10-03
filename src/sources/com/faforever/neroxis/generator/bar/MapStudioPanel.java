package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.exporter.bar.BarExportOptions;
import com.faforever.neroxis.exporter.bar.BarMapExporter;
import com.faforever.neroxis.generator.util.serial.TerrainStyle;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Random;
import java.util.function.Consumer;

public final class MapStudioPanel extends JPanel {
    private static final Color BG_DARK = new Color(11, 14, 20);       // #0B0E14
    private static final Color CARD_BG = new Color(19, 24, 34);       // #131822
    private static final Color CARD_INNER = new Color(26, 34, 48);    // #1A2230
    private static final Color BORDER_COLOR = new Color(38, 50, 70);  // #263246
    private static final Color ACCENT_CYAN = new Color(0, 229, 255);  // #00E5FF
    private static final Color ACCENT_AMBER = new Color(255, 159, 28);// #FF9F1C
    private static final Color ACCENT_GREEN = new Color(0, 255, 136); // #00FF88
    private static final Color TEXT_MAIN = new Color(240, 244, 250);
    private static final Color TEXT_MUTED = new Color(140, 155, 180);

    public static final record MapDimension(String label, int width, int height, Symmetry recommendedSym) {
        @Override
        public String toString() {
            return label;
        }
    }

    private static final MapDimension[] MAP_DIMENSIONS = {
        new MapDimension("8x8 (256x256) - Quadrata (Diagonale X+Z)", 256, 256, Symmetry.POINT2),
        new MapDimension("12x12 (384x384) - Quadrata (Diagonale X+Z)", 384, 384, Symmetry.POINT2),
        new MapDimension("16x16 (512x512) - Quadrata Standard (Diagonale X+Z)", 512, 512, Symmetry.POINT2),
        new MapDimension("20x20 (640x640) - Quadrata Grande (Diagonale X+Z)", 640, 640, Symmetry.POINT2),
        new MapDimension("24x24 (768x768) - Quadrata Enorme (Diagonale X+Z)", 768, 768, Symmetry.POINT2),

        // Rettangolari con SPECCHIO DIAGONALE (Doppia Specchiatura Sia in X che in Z)
        new MapDimension("8x12 (256x384) - Rettangolare Diagonale (Doppia X+Z)", 256, 384, Symmetry.POINT2),
        new MapDimension("12x16 (384x512) - Rettangolare Diagonale (Doppia X+Z)", 384, 512, Symmetry.POINT2),
        new MapDimension("8x16 (256x512) - Rettangolare Diagonale (Doppia X+Z)", 256, 512, Symmetry.POINT2),
        new MapDimension("16x24 (512x768) - Rettangolare Diagonale (Doppia X+Z)", 512, 768, Symmetry.POINT2),
        new MapDimension("12x8 (384x256) - Rettangolare Diagonale (Doppia X+Z)", 384, 256, Symmetry.POINT2),
        new MapDimension("16x12 (512x384) - Rettangolare Diagonale (Doppia X+Z)", 512, 384, Symmetry.POINT2),
        new MapDimension("16x8 (512x256) - Rettangolare Diagonale (Doppia X+Z)", 512, 256, Symmetry.POINT2),
        new MapDimension("24x16 (768x512) - Rettangolare Diagonale (Doppia X+Z)", 768, 512, Symmetry.POINT2),

        // Rettangolari con SOLO SPECCHIO ASSE Z (Nord vs Sud / Sopra-Sotto)
        new MapDimension("8x12 (256x384) - Rettangolare Solo Asse Z (N-S)", 256, 384, Symmetry.Z),
        new MapDimension("12x16 (384x512) - Rettangolare Solo Asse Z (N-S)", 384, 512, Symmetry.Z),
        new MapDimension("16x24 (512x768) - Rettangolare Solo Asse Z (N-S)", 512, 768, Symmetry.Z),
        new MapDimension("12x8 (384x256) - Rettangolare Solo Asse Z (N-S)", 384, 256, Symmetry.Z),
        new MapDimension("16x12 (512x384) - Rettangolare Solo Asse Z (N-S)", 512, 384, Symmetry.Z),

        // Rettangolari con SOLO SPECCHIO ASSE X (Ovest vs Est / Sinistra-Destra)
        new MapDimension("8x12 (256x384) - Rettangolare Solo Asse X (W-E)", 256, 384, Symmetry.X),
        new MapDimension("12x16 (384x512) - Rettangolare Solo Asse X (W-E)", 384, 512, Symmetry.X),
        new MapDimension("12x8 (384x256) - Rettangolare Solo Asse X (W-E)", 384, 256, Symmetry.X),
        new MapDimension("16x12 (512x384) - Rettangolare Solo Asse X (W-E)", 512, 384, Symmetry.X),
        new MapDimension("24x16 (768x512) - Rettangolare Solo Asse X (W-E)", 768, 512, Symmetry.X),

        // Rettangolari con QUADRUPLA SPECCHIO (Sia X che Z - 4 Quadranti)
        new MapDimension("12x16 (384x512) - Rettangolare Quadrupla (4 Lati X+Z)", 384, 512, Symmetry.QUAD),
        new MapDimension("16x12 (512x384) - Rettangolare Quadrupla (4 Lati X+Z)", 512, 384, Symmetry.QUAD)
    };

    public static final record SymmetryOption(String label, Symmetry symmetry) {
        @Override
        public String toString() {
            return label;
        }
    }

    private static final SymmetryOption[] SYMMETRY_OPTIONS = {
        new SymmetryOption("Specchio Diagonale (Doppia X + Z)", Symmetry.POINT2),
        new SymmetryOption("Solo Specchio Asse Z (Nord-Sud / Y1-Y2)", Symmetry.Z),
        new SymmetryOption("Solo Specchio Asse X (Ovest-Est / X1-X2)", Symmetry.X),
        new SymmetryOption("Quadrupla Specchio (4 Quadranti Sia X che Z)", Symmetry.QUAD),
        new SymmetryOption("Nessuna Simmetria (Libera)", Symmetry.NONE)
    };

    private static final String[] TERRAIN_STYLES = {
        "VALLEY", "LAND_BRIDGE", "MOUNTAIN_RANGE", "BIG_ISLANDS", "SMALL_ISLANDS",
        "CENTER_LAKE", "FLOODED", "DROP_PLATEAU", "LITTLE_MOUNTAIN", "MULTILEVEL_LAST",
        "ONE_ISLAND", "RIVERS", "RIVERS_AND_OCEANS", "FRACTAL_LAND", "FRACTAL_PLATEAU",
        "FRACTAL_NAVY", "SETONS", "BASIC"
    };

    private static final String[] BIOMES = {
        "DESERT", "MARS", "BRIMSTONE", "EARLYAUTUMN", "FRITHEN", "MOONLIGHT",
        "PRAYER", "STONES", "SUNSET", "SYRTIS", "WINDINGRIVER", "WONDER", "CRYSTALLINE"
    };

    private final MapStudioCanvas canvas = new MapStudioCanvas();
    private final JComboBox<MapDimension> sizeCombo = new JComboBox<>(MAP_DIMENSIONS);
    private final JComboBox<String> styleCombo = new JComboBox<>(TERRAIN_STYLES);
    private final JComboBox<String> biomeCombo = new JComboBox<>(BIOMES);
    private final JComboBox<SymmetryOption> symmetryCombo = new JComboBox<>(SYMMETRY_OPTIONS);
    private final JTextField seedField = new JTextField();
    private final JButton rollSeedBtn = createGhostButton("Roll Seed");
    private final JButton genGroundBtn = createPrimaryButton("GENERA TERRENO", ACCENT_CYAN);

    // Height, Water & Slope Controls
    private final JSpinner maxHeightSpinner = new JSpinner(new SpinnerNumberModel(250.0, 20.0, 1500.0, 10.0));
    private final JSpinner waterLevelSpinner = new JSpinner(new SpinnerNumberModel(25.0, -100.0, 800.0, 5.0));
    private final JToggleButton slopeToggleBtn = new JToggleButton("Slope Map [L]", false);
    private final JSlider slopeOpacitySlider = new JSlider(10, 100, 70);
    private final JToggleButton view3DBtn = new JToggleButton("Vista 3D [V]", false);
    private MapScene3DCanvas scene3D;
    private JSplitPane centerSplit;
    private JPanel centerWrapper;
    private boolean view3DVisible = false;
    private final JButton btnRegenGround = createPrimaryButton("RIGENERA DA HEIGHTMAP", ACCENT_AMBER);
    private final JButton btnLoadHeightmap = createGhostButton("Carica Heightmap...");
    private final JButton btnLoadTexture = createGhostButton("Carica Texture Terreno...");
    private final JButton btnSmoothSlopes = createGhostButton("Leviga / Pialla Pendenze [Ramps]");

    private final java.util.Map<MapStudioCanvas.Tool, JToggleButton> toolButtons = new java.util.EnumMap<>(MapStudioCanvas.Tool.class);
    private JToggleButton symToggle;
    private final JButton axisToggleBtn = createGhostButton("Asse: Z [X]");

    // Mex Placement Metal controls
    private final JSpinner mexPlacementMetalSpinner = new JSpinner(new SpinnerNumberModel(2.0, 0.5, 10.0, 0.1));
    private final JComboBox<String> mexPlacementMetalCombo = new JComboBox<>(new String[]{
        "1.0 M (Povero)", "1.5 M", "2.0 M (Std)", "2.5 M", "3.0 M", "3.5 M (Ricco)", "4.0 M", "5.0 M (Alto)"
    });
    private final JPanel mexColorSwatch = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            float val = ((Number) mexPlacementMetalSpinner.getValue()).floatValue();
            Color fill = MapStudioCanvas.getMexFillColor(val);
            Color ring = MapStudioCanvas.getMexRingColor(val);
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int r = 11;
            g2.setColor(new Color(ring.getRed(), ring.getGreen(), ring.getBlue(), 90));
            g2.fillOval(cx - r - 3, cy - r - 3, (r + 3) * 2, (r + 3) * 2);
            g2.setColor(fill);
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            float t = Math.max(0f, Math.min(1f, (val - 1.0f) / 4.0f));
            Color outline = (t < 0.35f) ? new Color(0, 140, 180, 240) : new Color(0, 229, 255, 220);
            g2.setColor(outline);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            Color pip = (t > 0.55f) ? new Color(0, 240, 255) : new Color(11, 14, 20);
            g2.setColor(pip);
            g2.fillOval(cx - 2, cy - 2, 4, 4);
            g2.dispose();
        }
    };

    // Map Details & Settings
    private final JTextField mapNameField = new JTextField("Map_NewArena");
    private final JSpinner baseMetalSpinner = new JSpinner(new SpinnerNumberModel(2.0, 0.5, 10.0, 0.1));
    private final JSpinner maxMetalSpinner = new JSpinner(new SpinnerNumberModel(3.5, 0.5, 10.0, 0.1));
    private final JSlider reclaimSlider = new JSlider(0, 50, 10);
    private final JLabel reclaimValueLabel = new JLabel("1.0x (Standard)");

    // Counters
    private final JLabel mexCountLabel = createChip("Mex: 0", ACCENT_CYAN);
    private final JLabel geoCountLabel = createChip("Geos: 0", ACCENT_AMBER);
    private final JLabel spawnCountLabel = createChip("Spawns: 0", ACCENT_GREEN);
    private final JLabel reclaimCountLabel = createChip("Reclaim: 0", TEXT_MUTED);

    // Final Action
    private final JButton genFinalMapBtn = createPrimaryButton("GENERA MAPPA FINALE", ACCENT_GREEN);
    private final JButton genAndPlayBtn = createPrimaryButton("GENERA E GIOCA VS AI", ACCENT_AMBER);
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel statusLabel = new JLabel("Pronto per generare il terreno.");

    private final Path homeFolder;
    private final Path barDataFolder;
    private final Consumer<Path> onMapCreated;
    private final Consumer<Path> onPlaySkirmish;
    private Runnable openTextureStudioCallback;

    public void setOpenTextureStudioCallback(Runnable openTextureStudioCallback) {
        this.openTextureStudioCallback = openTextureStudioCallback;
    }

    private Runnable openObjectsStudioCallback;

    public void setOpenObjectsStudioCallback(Runnable openObjectsStudioCallback) {
        this.openObjectsStudioCallback = openObjectsStudioCallback;
        if (this.readyTray != null) {
            this.readyTray.setOnOpenStudio(openObjectsStudioCallback);
        }
    }

    private ReadyObjectsTray readyTray;
    private CardLayout rightCardsLayout;
    private JPanel rightCardsPanel;
    private JToggleButton tabMappaBtn;
    private JToggleButton tabOggettiBtn;

    private Runnable onMapChangedCallback;

    public void setOnMapChangedCallback(Runnable callback) {
        this.onMapChangedCallback = callback;
    }

    public BufferedImage getCurrentGround() {
        return canvas != null ? canvas.getGroundPreview() : null;
    }

    public BufferedImage getCurrentSlopeOverlay() {
        return canvas != null ? canvas.getSlopeOverlay() : null;
    }

    public SCMap getCurrentMap() {
        return canvas != null ? canvas.getMap() : null;
    }

    public MapStudioPanel(Path homeFolder, Path barDataFolder, Consumer<Path> onMapCreated, Consumer<Path> onPlaySkirmish) {
        this.homeFolder = homeFolder;
        this.barDataFolder = barDataFolder;
        this.onMapCreated = onMapCreated;
        this.onPlaySkirmish = onPlaySkirmish;

        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // Pre-fill seed
        rollNewSeed();

        // 1. Top HUD Toolbar
        add(buildTopToolbar(), BorderLayout.NORTH);

        // Style all Spinners across the Studio
        styleSpinner(maxHeightSpinner);
        styleSpinner(waterLevelSpinner);
        styleSpinner(baseMetalSpinner);
        styleSpinner(maxMetalSpinner);
        styleSpinner(mexPlacementMetalSpinner);

        // 2. Center Canvas Area (2D + 3D split)
        centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(6, 6, 6, 6));
        scene3D = new MapScene3DCanvas(() -> barDataFolder);
        scene3D.setMinimumSize(new Dimension(200, 100));
        canvas.setMinimumSize(new Dimension(200, 100));
        centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvas, scene3D);
        centerSplit.setDividerSize(7);
        centerSplit.setContinuousLayout(true);
        centerSplit.setBorder(null);
        centerSplit.setOpaque(false);
        centerSplit.setResizeWeight(0.6);
        centerWrapper.add(centerSplit, BorderLayout.CENTER);
        scene3D.setVisible(false);
        canvas.setHudDock(buildCanvasHudDock());

        // Ready Objects Tray nel pannello laterale destro
        readyTray = new ReadyObjectsTray(
            entity -> {
                if (entity != null) {
                    selectCustomEntityTool(entity);
                } else {
                    selectTool(MapStudioCanvas.Tool.SELECT);
                }
            },
            () -> {
                if (openObjectsStudioCallback != null) openObjectsStudioCallback.run();
            },
            () -> {
                int res = JOptionPane.showConfirmDialog(
                    this,
                    "Rimuovere tutti gli oggetti personalizzati piazzati sulla mappa?",
                    "Svuota Oggetti Piazzati",
                    JOptionPane.YES_NO_OPTION
                );
                if (res == JOptionPane.YES_OPTION) {
                    canvas.clearCustomEntities();
                }
            }
        );

        // 3. Resizable Panels Architecture with JSplitPane
        JComponent leftSidebar = buildLeftSidebar();
        JComponent rightSidebar = buildRightSidebar();

        leftSidebar.setMinimumSize(new Dimension(180, 100));
        centerWrapper.setMinimumSize(new Dimension(200, 100));
        rightSidebar.setMinimumSize(new Dimension(180, 100));

        JSplitPane leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSidebar, centerWrapper);
        leftSplit.setDividerSize(7);
        leftSplit.setContinuousLayout(true);
        leftSplit.setBorder(null);
        leftSplit.setOpaque(false);
        leftSplit.setResizeWeight(0.0);
        leftSplit.setDividerLocation(380);

        leftSplit.addComponentListener(new java.awt.event.ComponentAdapter() {
            private boolean initialSet = false;
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (!initialSet && leftSplit.getWidth() > 0) {
                    initialSet = true;
                    leftSplit.setDividerLocation(380);
                }
            }
        });

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, rightSidebar);
        mainSplit.setDividerSize(7);
        mainSplit.setContinuousLayout(true);
        mainSplit.setBorder(null);
        mainSplit.setOpaque(false);
        mainSplit.setResizeWeight(1.0);

        mainSplit.addComponentListener(new java.awt.event.ComponentAdapter() {
            private boolean initialSet = false;
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (!initialSet && mainSplit.getWidth() > 0) {
                    initialSet = true;
                    int rightW = 350;
                    int loc = Math.max(300, mainSplit.getWidth() - rightW);
                    mainSplit.setDividerLocation(loc);
                    leftSplit.setDividerLocation(380);
                }
            }
        });

        add(mainSplit, BorderLayout.CENTER);

        // Canvas change listener updates counter chips + 3D scene
        canvas.setChangeListener(() -> { updateCounters(); refreshScene3D(); });
        // Selezione 2D <-> 3D sincronizzata
        canvas.setSelectionListener(p -> { if (scene3D != null) scene3D.setSelected(p); });
        if (scene3D != null) {
            scene3D.setSelectionListener(p -> { if (p != null) canvas.setSelectedCustomEntity(p); });
            scene3D.setScaleChangeListener(() -> { updateCounters(); canvas.repaint(); });
        }

        // Synchronize Mex Metal controls
        mexPlacementMetalSpinner.addChangeListener(e -> {
            float v = ((Number) mexPlacementMetalSpinner.getValue()).floatValue();
            setPlacementMetalValue(v, false, true);
        });

        mexPlacementMetalCombo.addActionListener(e -> {
            int idx = mexPlacementMetalCombo.getSelectedIndex();
            float[] vals = {1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 5.0f};
            if (idx >= 0 && idx < vals.length) {
                setPlacementMetalValue(vals[idx], true, false);
            }
        });

        canvas.setOnMexSelected(metalVal -> {
            setPlacementMetalValue(metalVal, true, true);
        });

        // Bind Actions
        rollSeedBtn.addActionListener(e -> rollNewSeed());
        genGroundBtn.addActionListener(e -> doGenerateGround());
        genFinalMapBtn.addActionListener(e -> doGenerateFinalMap(false));
        genAndPlayBtn.addActionListener(e -> doGenerateFinalMap(true));

        btnRegenGround.addActionListener(e -> doRegenerateFromHeightmap(null));
        btnLoadHeightmap.addActionListener(e -> doLoadHeightmapFile());
        btnLoadTexture.addActionListener(e -> doLoadCustomTextureFile());
        btnSmoothSlopes.addActionListener(e -> doSmoothSlopes());

        canvas.setOnTerrainHover(info -> {
            if (info != null && !info.isEmpty()) {
                setStatus(info);
            }
        });

        sizeCombo.addActionListener(e -> {
            MapDimension sel = (MapDimension) sizeCombo.getSelectedItem();
            if (sel != null) {
                selectSymmetryInCombo(sel.recommendedSym());
                updateAxisToggleLabel(sel.recommendedSym());
                try {
                    canvas.setSymmetrySettings(new SymmetrySettings(sel.recommendedSym()));
                } catch (Exception ignored) {}
            }
        });

        symmetryCombo.addActionListener(e -> {
            SymmetryOption opt = (SymmetryOption) symmetryCombo.getSelectedItem();
            if (opt != null) {
                try {
                    canvas.setSymmetrySettings(new SymmetrySettings(opt.symmetry()));
                    updateAxisToggleLabel(opt.symmetry());
                } catch (Exception ignored) {}
            }
        });

        reclaimSlider.addChangeListener(e -> {
            float val = reclaimSlider.getValue() / 10.0f;
            reclaimValueLabel.setText(String.format("%.1fx", val));
        });

        // Initialize Window-Wide Hotkeys
        setupHotkeys();
    }

    public void selectSymmetryInCombo(Symmetry sym) {
        if (sym == null) return;
        for (int i = 0; i < symmetryCombo.getItemCount(); i++) {
            SymmetryOption opt = symmetryCombo.getItemAt(i);
            if (opt != null && opt.symmetry() == sym) {
                if (symmetryCombo.getSelectedIndex() != i) {
                    symmetryCombo.setSelectedIndex(i);
                }
                break;
            }
        }
    }

    public void updateAxisToggleLabel(Symmetry sym) {
        if (axisToggleBtn == null || sym == null) return;
        if (sym == Symmetry.POINT2 || sym == Symmetry.DIAG) {
            axisToggleBtn.setText("Asse: Diag [X]");
            axisToggleBtn.setToolTipText("Simmetria Diagonale X+Z [Tasto X]");
            axisToggleBtn.setForeground(ACCENT_CYAN);
        } else if (sym == Symmetry.Z) {
            axisToggleBtn.setText("Asse: Z [X]");
            axisToggleBtn.setToolTipText("Simmetria Speculare Asse Z (Nord-Sud) [Tasto X]");
            axisToggleBtn.setForeground(ACCENT_CYAN);
        } else if (sym == Symmetry.X) {
            axisToggleBtn.setText("Asse: X [X]");
            axisToggleBtn.setToolTipText("Simmetria Speculare Asse X (Ovest-Est) [Tasto X]");
            axisToggleBtn.setForeground(ACCENT_CYAN);
        } else if (sym == Symmetry.QUAD) {
            axisToggleBtn.setText("Asse: Quad [X]");
            axisToggleBtn.setToolTipText("Simmetria Quadrangolare 4-vie [Tasto X]");
            axisToggleBtn.setForeground(ACCENT_CYAN);
        } else {
            axisToggleBtn.setText("Asse: Libero [X]");
            axisToggleBtn.setToolTipText("Nessuna simmetria asse [Tasto X]");
            axisToggleBtn.setForeground(TEXT_MAIN);
        }
    }

    public void toggleMirrorAxis() {
        cycleSymmetryMode();
    }

    public void cycleSymmetryMode() {
        Symmetry cur = Symmetry.POINT2;
        if (canvas.getSymmetrySettings() != null) {
            cur = canvas.getSymmetrySettings().terrainSymmetry();
        }
        Symmetry next;
        if (cur == Symmetry.POINT2 || cur == Symmetry.DIAG) {
            next = Symmetry.Z;
        } else if (cur == Symmetry.Z) {
            next = Symmetry.X;
        } else if (cur == Symmetry.X) {
            next = Symmetry.QUAD;
        } else {
            next = Symmetry.POINT2;
        }
        canvas.setSymmetrySettings(new SymmetrySettings(next));
        selectSymmetryInCombo(next);
        updateAxisToggleLabel(next);
    }

    public void selectTool(MapStudioCanvas.Tool tool) {
        canvas.setTool(tool);
        JToggleButton b = toolButtons.get(tool);
        if (b != null && !b.isSelected()) {
            b.setSelected(true);
        }
        if (readyTray != null && tool != MapStudioCanvas.Tool.CUSTOM_OBJECT) {
            readyTray.clearSelection();
        }
    }

    public void selectCustomEntityTool(ObjectEntity entity) {
        canvas.setActiveCustomEntity(entity);
        for (JToggleButton b : toolButtons.values()) {
            b.setSelected(false);
        }
        switchToObjectsTab();
    }

    public void switchToObjectsTab() {
        if (rightCardsLayout != null && rightCardsPanel != null && tabOggettiBtn != null) {
            rightCardsLayout.show(rightCardsPanel, "OGGETTI");
            tabOggettiBtn.setSelected(true);
            tabOggettiBtn.setForeground(ACCENT_CYAN);
            if (tabMappaBtn != null) tabMappaBtn.setForeground(TEXT_MUTED);
        }
    }

    public void switchToMappaTab() {
        if (rightCardsLayout != null && rightCardsPanel != null && tabMappaBtn != null) {
            rightCardsLayout.show(rightCardsPanel, "MAPPA");
            tabMappaBtn.setSelected(true);
            tabMappaBtn.setForeground(ACCENT_CYAN);
            if (tabOggettiBtn != null) tabOggettiBtn.setForeground(TEXT_MUTED);
        }
    }

    private boolean isTypingInText() {
        Component fo = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        return (fo instanceof javax.swing.text.JTextComponent);
    }

    private void bindKey(int keyCode, int modifiers, String actionName, Runnable action) {
        KeyStroke stroke = KeyStroke.getKeyStroke(keyCode, modifiers);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, actionName);
        getActionMap().put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                action.run();
            }
        });
    }

    private void setupHotkeys() {
        // Tools (with text typing guard)
        bindKey(java.awt.event.KeyEvent.VK_S, 0, "tool_select", () -> { if (!isTypingInText()) selectTool(MapStudioCanvas.Tool.SELECT); });
        bindKey(java.awt.event.KeyEvent.VK_V, 0, "tool_select_v", () -> { if (!isTypingInText()) selectTool(MapStudioCanvas.Tool.SELECT); });
        bindKey(java.awt.event.KeyEvent.VK_M, 0, "tool_mex", () -> { if (!isTypingInText()) selectTool(MapStudioCanvas.Tool.MEX); });
        bindKey(java.awt.event.KeyEvent.VK_G, 0, "tool_geo", () -> { if (!isTypingInText()) selectTool(MapStudioCanvas.Tool.GEO); });
        bindKey(java.awt.event.KeyEvent.VK_P, 0, "tool_spawn", () -> { if (!isTypingInText()) selectTool(MapStudioCanvas.Tool.SPAWN); });
        bindKey(java.awt.event.KeyEvent.VK_R, 0, "tool_rock", () -> { if (!isTypingInText()) selectTool(MapStudioCanvas.Tool.RECLAIM_ROCK); });
        bindKey(java.awt.event.KeyEvent.VK_T, 0, "tool_tree", () -> { if (!isTypingInText()) selectTool(MapStudioCanvas.Tool.RECLAIM_TREE); });
        bindKey(java.awt.event.KeyEvent.VK_W, 0, "tool_wreck", () -> { if (!isTypingInText()) selectTool(MapStudioCanvas.Tool.RECLAIM_WRECK); });
        bindKey(java.awt.event.KeyEvent.VK_E, 0, "tool_erase", () -> { if (!isTypingInText()) selectTool(MapStudioCanvas.Tool.ERASE); });
        bindKey(java.awt.event.KeyEvent.VK_X, 0, "toggle_mirror_axis", () -> { if (!isTypingInText()) toggleMirrorAxis(); });

        // Deletion and Esc
        bindKey(java.awt.event.KeyEvent.VK_DELETE, 0, "del_entity", () -> { if (!isTypingInText()) canvas.deleteSelectedEntity(); });
        bindKey(java.awt.event.KeyEvent.VK_BACK_SPACE, 0, "backspace_entity", () -> { if (!isTypingInText()) canvas.deleteSelectedEntity(); });
        bindKey(java.awt.event.KeyEvent.VK_ESCAPE, 0, "escape_select", () -> selectTool(MapStudioCanvas.Tool.SELECT));

        // Symmetry toggle
        bindKey(java.awt.event.KeyEvent.VK_SPACE, 0, "toggle_sym_space", () -> {
            if (!isTypingInText() && symToggle != null) symToggle.doClick();
        });
        bindKey(java.awt.event.KeyEvent.VK_Y, 0, "toggle_sym_y", () -> {
            if (!isTypingInText() && symToggle != null) symToggle.doClick();
        });

        // Undo / Redo
        int cmd = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        bindKey(java.awt.event.KeyEvent.VK_Z, cmd, "undo", canvas::undo);
        bindKey(java.awt.event.KeyEvent.VK_Y, cmd, "redo", canvas::redo);
        bindKey(java.awt.event.KeyEvent.VK_Z, cmd | java.awt.event.KeyEvent.SHIFT_DOWN_MASK, "redo_shift", canvas::redo);

        // Zoom controls
        bindKey(java.awt.event.KeyEvent.VK_PLUS, 0, "zoom_in_plus", canvas::zoomIn);
        bindKey(java.awt.event.KeyEvent.VK_ADD, 0, "zoom_in_add", canvas::zoomIn);
        bindKey(java.awt.event.KeyEvent.VK_EQUALS, 0, "zoom_in_eq", canvas::zoomIn);
        bindKey(java.awt.event.KeyEvent.VK_MINUS, 0, "zoom_out_minus", canvas::zoomOut);
        bindKey(java.awt.event.KeyEvent.VK_SUBTRACT, 0, "zoom_out_sub", canvas::zoomOut);
        bindKey(java.awt.event.KeyEvent.VK_0, 0, "zoom_reset_0", () -> { if (!isTypingInText()) canvas.resetZoom(); });
        bindKey(java.awt.event.KeyEvent.VK_NUMPAD0, 0, "zoom_reset_num0", () -> { if (!isTypingInText()) canvas.resetZoom(); });

        // Metal presets 1..5
        bindKey(java.awt.event.KeyEvent.VK_1, 0, "mex_p1", () -> { if (!isTypingInText()) setPlacementMetalValue(1.0f, true, true); });
        bindKey(java.awt.event.KeyEvent.VK_NUMPAD1, 0, "mex_p1_np", () -> { if (!isTypingInText()) setPlacementMetalValue(1.0f, true, true); });
        bindKey(java.awt.event.KeyEvent.VK_2, 0, "mex_p2", () -> { if (!isTypingInText()) setPlacementMetalValue(2.0f, true, true); });
        bindKey(java.awt.event.KeyEvent.VK_NUMPAD2, 0, "mex_p2_np", () -> { if (!isTypingInText()) setPlacementMetalValue(2.0f, true, true); });
        bindKey(java.awt.event.KeyEvent.VK_3, 0, "mex_p3", () -> { if (!isTypingInText()) setPlacementMetalValue(3.0f, true, true); });
        bindKey(java.awt.event.KeyEvent.VK_NUMPAD3, 0, "mex_p3_np", () -> { if (!isTypingInText()) setPlacementMetalValue(3.0f, true, true); });
        bindKey(java.awt.event.KeyEvent.VK_4, 0, "mex_p4", () -> { if (!isTypingInText()) setPlacementMetalValue(4.0f, true, true); });
        bindKey(java.awt.event.KeyEvent.VK_NUMPAD4, 0, "mex_p4_np", () -> { if (!isTypingInText()) setPlacementMetalValue(4.0f, true, true); });
        bindKey(java.awt.event.KeyEvent.VK_5, 0, "mex_p5", () -> { if (!isTypingInText()) setPlacementMetalValue(5.0f, true, true); });
        // Slope Map toggle hotkey
        bindKey(java.awt.event.KeyEvent.VK_L, 0, "toggle_slope_map", () -> { if (!isTypingInText()) toggleSlopeMap(); });
        bindKey(java.awt.event.KeyEvent.VK_V, 0, "toggle_view_3d", () -> { if (!isTypingInText()) toggleView3D(); });
    }

    public void toggleView3D() { setView3DVisible(!view3DVisible); }

    public void setView3DVisible(boolean v) {
        view3DVisible = v;
        view3DBtn.setSelected(v);
        view3DBtn.setText(v ? "Vista 3D: ON [V]" : "Vista 3D [V]");
        if (scene3D != null) {
            scene3D.setVisible(v);
            if (v) {
                refreshScene3D();
                centerSplit.setDividerLocation(0.6);
            }
            centerWrapper.revalidate();
            centerWrapper.repaint();
        }
    }

    private void refreshScene3D() {
        if (scene3D == null || !view3DVisible) return;
        try {
            scene3D.setDataSupplier(() -> barDataFolder);
            scene3D.setMapAndGround(canvas.getMap(), canvas.getGroundPreview(), canvas.getCustomEntities());
            PlacedCustomEntity sel = canvas.getSelectedCustomEntity();
            if (sel != null) scene3D.setSelected(sel);
        } catch (Exception ignored) {}
    }

    public void toggleSlopeMap() {
        canvas.toggleSlopeMap();
        updateSlopeButtonState();
    }

    public void updateSlopeButtonState() {
        boolean active = canvas.isShowSlopeMap();
        slopeToggleBtn.setSelected(active);
        slopeToggleBtn.setText(active ? "Slope Map: ON [L]" : "Slope Map [L]");
        slopeToggleBtn.setForeground(active ? ACCENT_AMBER : TEXT_MAIN);
        slopeToggleBtn.setBackground(active ? new Color(45, 34, 18) : CARD_INNER);
        slopeToggleBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(active ? ACCENT_AMBER : BORDER_COLOR, 1, true),
            new EmptyBorder(2, 8, 2, 8)
        ));
    }

    private void setPlacementMetalValue(float val, boolean updateSpinner, boolean updateCombo) {
        float clamped = Math.max(0.5f, Math.min(10.0f, Math.round(val * 10f) / 10f));
        canvas.setCurrentMexMetal(clamped);
        if (updateSpinner) {
            mexPlacementMetalSpinner.setValue(Double.valueOf(clamped));
        }
        if (updateCombo) {
            if (Math.abs(clamped - 1.0f) < 0.05f) mexPlacementMetalCombo.setSelectedIndex(0);
            else if (Math.abs(clamped - 1.5f) < 0.05f) mexPlacementMetalCombo.setSelectedIndex(1);
            else if (Math.abs(clamped - 2.0f) < 0.05f) mexPlacementMetalCombo.setSelectedIndex(2);
            else if (Math.abs(clamped - 2.5f) < 0.05f) mexPlacementMetalCombo.setSelectedIndex(3);
            else if (Math.abs(clamped - 3.0f) < 0.05f) mexPlacementMetalCombo.setSelectedIndex(4);
            else if (Math.abs(clamped - 3.5f) < 0.05f) mexPlacementMetalCombo.setSelectedIndex(5);
            else if (Math.abs(clamped - 4.0f) < 0.05f) mexPlacementMetalCombo.setSelectedIndex(6);
            else if (Math.abs(clamped - 5.0f) < 0.05f) mexPlacementMetalCombo.setSelectedIndex(7);
        }
        mexColorSwatch.repaint();
    }

    private void rollNewSeed() {
        long s = Math.abs(new Random().nextLong() % 1000000000L);
        seedField.setText(String.valueOf(s));
        if (mapNameField.getText().startsWith("Map_")) {
            mapNameField.setText("Map_" + s);
        }
    }

    private JComponent buildTopToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 5));
        bar.setBackground(CARD_BG);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JLabel titleLbl = new JLabel("STRUMENTI:");
        titleLbl.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        titleLbl.setForeground(ACCENT_CYAN);
        bar.add(titleLbl);

        ButtonGroup group = new ButtonGroup();
        bar.add(createToolToggle("Sel [S]", MapStudioCanvas.Tool.SELECT, group, true));
        bar.add(createToolToggle("Mex [M]", MapStudioCanvas.Tool.MEX, group, false));

        JLabel mexMetalLbl = new JLabel("M:");
        mexMetalLbl.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        mexMetalLbl.setForeground(ACCENT_CYAN);
        bar.add(mexMetalLbl);
        mexPlacementMetalCombo.setFont(BarMapStudio.font(Font.BOLD, 11));
        mexPlacementMetalCombo.setBackground(CARD_INNER);
        mexPlacementMetalCombo.setForeground(TEXT_MAIN);
        mexPlacementMetalCombo.setSelectedIndex(2);
        mexPlacementMetalCombo.setPreferredSize(new Dimension(105, 25));
        mexPlacementMetalCombo.setToolTipText("Valore del metallo per le nuove piastrine Mex");
        bar.add(mexPlacementMetalCombo);

        bar.add(createToolToggle("Geo [G]", MapStudioCanvas.Tool.GEO, group, false));
        bar.add(createToolToggle("Spawn [P]", MapStudioCanvas.Tool.SPAWN, group, false));
        bar.add(createToolToggle("Rocce [R]", MapStudioCanvas.Tool.RECLAIM_ROCK, group, false));
        bar.add(createToolToggle("Alberi [T]", MapStudioCanvas.Tool.RECLAIM_TREE, group, false));
        bar.add(createToolToggle("Relitti [W]", MapStudioCanvas.Tool.RECLAIM_WRECK, group, false));
        bar.add(createToolToggle("Gomma [E]", MapStudioCanvas.Tool.ERASE, group, false));

        bar.add(Box.createHorizontalStrut(4));

        symToggle = new JToggleButton("Simm [Space]", true);
        styleToggleButton(symToggle);
        symToggle.setForeground(ACCENT_CYAN);
        symToggle.setToolTipText("Attiva o disattiva il disegno speculare simmetrico [Barra Spaziatrice]");
        symToggle.addActionListener(e -> {
            boolean active = symToggle.isSelected();
            symToggle.setForeground(active ? ACCENT_CYAN : TEXT_MUTED);
            canvas.setSymmetryEnabled(active);
        });
        bar.add(symToggle);

        axisToggleBtn.addActionListener(e -> toggleMirrorAxis());
        bar.add(axisToggleBtn);

        bar.add(Box.createHorizontalStrut(4));
        JButton undoBtn = createGhostButton("Annulla [^Z]");
        undoBtn.setToolTipText("Annulla ultima modifica [Ctrl+Z]");
        undoBtn.addActionListener(e -> canvas.undo());
        bar.add(undoBtn);

        JButton redoBtn = createGhostButton("Ripristina [^Y]");
        redoBtn.setToolTipText("Ripristina modifica annullata [Ctrl+Y]");
        redoBtn.addActionListener(e -> canvas.redo());
        bar.add(redoBtn);

        return bar;
    }

    private JComponent buildCanvasHudDock() {
        JPanel dock = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(11, 14, 20, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(0, 229, 255, 130));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        dock.setOpaque(false);
        dock.setBorder(new EmptyBorder(2, 6, 2, 6));
        dock.addMouseListener(new MouseAdapter() {});

        // Slope Map Toggle Button
        styleToggleButton(slopeToggleBtn);
        slopeToggleBtn.setPreferredSize(new Dimension(135, 24));
        slopeToggleBtn.setToolTipText("Attiva/Disattiva Overlay Pendenze (Slope Map) [Tasto L]");
        slopeToggleBtn.addActionListener(e -> {
            canvas.setShowSlopeMap(slopeToggleBtn.isSelected());
            updateSlopeButtonState();
        });
        dock.add(slopeToggleBtn);

        // Vista 3D toggle subito dopo Slope Map
        styleToggleButton(view3DBtn);
        view3DBtn.setPreferredSize(new Dimension(120, 24));
        view3DBtn.setToolTipText("Mostra/nascondi vista 3D proporzionata [Tasto V]");
        view3DBtn.addActionListener(e -> setView3DVisible(view3DBtn.isSelected()));
        dock.add(view3DBtn);

        // Opacity Label & Slider
        JLabel opacityLbl = new JLabel("Opacita':");
        opacityLbl.setFont(BarMapStudio.fontCond(Font.BOLD, 10));
        opacityLbl.setForeground(TEXT_MUTED);
        dock.add(opacityLbl);

        slopeOpacitySlider.setPreferredSize(new Dimension(65, 20));
        slopeOpacitySlider.setOpaque(false);
        slopeOpacitySlider.setToolTipText("Regola l'opacita' dello Slope Map Overlay");
        slopeOpacitySlider.addChangeListener(e -> canvas.setSlopeOpacity(slopeOpacitySlider.getValue() / 100.0f));
        dock.add(slopeOpacitySlider);

        // Separator
        JPanel sep = new JPanel();
        sep.setBackground(new Color(30, 38, 56));
        sep.setPreferredSize(new Dimension(1, 16));
        dock.add(sep);

        // Zoom Out [-]
        JButton zoomOut = createGhostButton("-");
        zoomOut.setFont(BarMapStudio.font(Font.BOLD, 13));
        zoomOut.setPreferredSize(new Dimension(28, 24));
        zoomOut.setToolTipText("Zoom Indietro [-]");
        zoomOut.addActionListener(e -> canvas.zoomOut());
        dock.add(zoomOut);

        // Zoom In [+]
        JButton zoomIn = createGhostButton("+");
        zoomIn.setFont(BarMapStudio.font(Font.BOLD, 13));
        zoomIn.setPreferredSize(new Dimension(28, 24));
        zoomIn.setToolTipText("Zoom Avanti [+]");
        zoomIn.addActionListener(e -> canvas.zoomIn());
        dock.add(zoomIn);

        // 1:1 Scala
        JButton zoomReset = createGhostButton("1:1 Scala");
        zoomReset.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        zoomReset.setPreferredSize(new Dimension(76, 24));
        zoomReset.setToolTipText("Ripristina Scala 1:1 [0]");
        zoomReset.addActionListener(e -> canvas.resetZoom());
        dock.add(zoomReset);

        return dock;
    }

    private JToggleButton createToolToggle(String label, MapStudioCanvas.Tool tool, ButtonGroup group, boolean select) {
        JToggleButton b = new JToggleButton(label, select);
        styleToggleButton(b);
        b.addActionListener(e -> selectTool(tool));
        group.add(b);
        toolButtons.put(tool, b);
        return b;
    }

    private void styleToggleButton(JToggleButton b) {
        b.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        b.setBackground(CARD_INNER);
        b.setForeground(TEXT_MAIN);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(3, 6, 3, 6)
        ));
    }

    private JComponent buildLeftSidebar() {
        ScrollableTrackWidthPanel side = new ScrollableTrackWidthPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(CARD_BG);
        side.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("1. GENERATORE PROCEDURALE");
        title.setFont(BarMapStudio.fontCond(Font.BOLD, 13));
        title.setForeground(ACCENT_CYAN);
        title.setAlignmentX(0f);
        side.add(title);
        side.add(Box.createVerticalStrut(10));

        side.add(createFieldRow("Dimensioni Mappa:", sizeCombo));
        side.add(Box.createVerticalStrut(6));
        side.add(createFieldRow("Stile Rilievo:", styleCombo));
        side.add(Box.createVerticalStrut(6));
        side.add(createFieldRow("Bioma / Texture:", biomeCombo));
        side.add(Box.createVerticalStrut(6));
        side.add(createFieldRow("Simmetria:", symmetryCombo));
        side.add(Box.createVerticalStrut(4));

        JButton sideAxisBtn = createGhostButton("Alterna Simmetria: Diag / X / Z [X]");
        sideAxisBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        sideAxisBtn.setAlignmentX(0f);
        sideAxisBtn.setToolTipText("Cicla tra Diagonale, Asse Z, Asse X e Quad [Tasto rapido: X]");
        sideAxisBtn.addActionListener(e -> cycleSymmetryMode());
        side.add(sideAxisBtn);
        side.add(Box.createVerticalStrut(6));

        // Seed with Roll
        JLabel seedLbl = new JLabel("Seed Terreno:");
        seedLbl.setFont(BarMapStudio.font(Font.PLAIN, 12));
        seedLbl.setForeground(TEXT_MUTED);
        seedLbl.setAlignmentX(0f);
        side.add(seedLbl);
        side.add(Box.createVerticalStrut(4));

        JPanel seedRow = new JPanel(new BorderLayout(6, 0));
        seedRow.setOpaque(false);
        seedField.setBackground(CARD_INNER);
        seedField.setForeground(TEXT_MAIN);
        seedField.setCaretColor(ACCENT_CYAN);
        seedField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        seedRow.add(seedField, BorderLayout.CENTER);
        rollSeedBtn.setPreferredSize(new Dimension(84, 28));
        seedRow.add(rollSeedBtn, BorderLayout.EAST);
        seedRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        seedRow.setAlignmentX(0f);
        side.add(seedRow);

        side.add(Box.createVerticalStrut(10));
        genGroundBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        genGroundBtn.setAlignmentX(0f);
        side.add(genGroundBtn);

        side.add(Box.createVerticalStrut(14));

        // Card 2: Rilievo & Pendenze (Heightmap)
        JPanel heightCard = new JPanel();
        heightCard.setLayout(new BoxLayout(heightCard, BoxLayout.Y_AXIS));
        heightCard.setBackground(CARD_INNER);
        heightCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        heightCard.setAlignmentX(0f);
        heightCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        JLabel heightCardTitle = new JLabel("2. RILIEVO & PENDENZE");
        heightCardTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        heightCardTitle.setForeground(ACCENT_AMBER);
        heightCardTitle.setAlignmentX(0f);
        heightCard.add(heightCardTitle);
        heightCard.add(Box.createVerticalStrut(10));

        heightCard.add(createParamRow("Quota Massima Terreno:", maxHeightSpinner, "m"));
        heightCard.add(Box.createVerticalStrut(6));
        heightCard.add(createParamRow("Livello Mare / Acqua:", waterLevelSpinner, "m"));
        heightCard.add(Box.createVerticalStrut(10));

        btnRegenGround.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnRegenGround.setAlignmentX(0f);
        btnRegenGround.setToolTipText("Ricalcola le texture, la mappa di pendenza, i gradienti e l'acqua dall'heightmap");
        heightCard.add(btnRegenGround);
        heightCard.add(Box.createVerticalStrut(6));

        btnSmoothSlopes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        btnSmoothSlopes.setAlignmentX(0f);
        btnSmoothSlopes.setToolTipText("Appiana, pialla e leviga i dislivelli impraticabili per bot e veicoli (Anti-Cliff / Ramps)");
        heightCard.add(btnSmoothSlopes);
        heightCard.add(Box.createVerticalStrut(6));

        btnLoadHeightmap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        btnLoadHeightmap.setAlignmentX(0f);
        btnLoadHeightmap.setToolTipText("Importa un file immagine (PNG 16-bit, RAW, BMP) come heightmap");
        heightCard.add(btnLoadHeightmap);
        heightCard.add(Box.createVerticalStrut(6));

        btnLoadTexture.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        btnLoadTexture.setAlignmentX(0f);
        btnLoadTexture.setToolTipText("Applica un'immagine personalizzata (PNG, JPG, BMP) come texture di superficie");
        heightCard.add(btnLoadTexture);
        heightCard.add(Box.createVerticalStrut(6));

        JButton btnOpenTexStudio = createGhostButton("Apri Texture Studio (Splatting)...");
        btnOpenTexStudio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        btnOpenTexStudio.setAlignmentX(0f);
        btnOpenTexStudio.setToolTipText("Passa al modulo Texture Studio per generare texture procedurali e maschere di splatting");
        btnOpenTexStudio.addActionListener(e -> {
            if (openTextureStudioCallback != null) openTextureStudioCallback.run();
        });
        heightCard.add(btnOpenTexStudio);

        side.add(heightCard);
        side.add(Box.createVerticalStrut(12));

        // Pulsante Specifiche & Requisiti Asset posizionato sopra alla Slope Map Info
        JButton btnLeftSpecs = createPrimaryButton("SPECIFICHE & REQUISITI ASSET", ACCENT_CYAN);
        btnLeftSpecs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btnLeftSpecs.setAlignmentX(0f);
        btnLeftSpecs.setToolTipText("Linee guida e specifiche tecniche per Heightmap 16-bit, Texture e Modelli 3D");
        btnLeftSpecs.addActionListener(e -> AssetSpecsDialog.toggle((JFrame) SwingUtilities.getWindowAncestor(this)));
        side.add(btnLeftSpecs);
        side.add(Box.createVerticalStrut(8));

        // Info box (Slope Map [Tasto L])
        JPanel infoBox = new JPanel(new BorderLayout());
        infoBox.setBackground(CARD_INNER);
        infoBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel tip = new JLabel("<html><b style='color:#00E5FF'>Slope Map [Tasto L]:</b><br/>" +
            "Visualizza in tempo reale le pendenze Recoil/BAR:<br/>" +
            "- <span style='color:#00FF88'>Verde (&lt;12 deg):</span> Veicoli & Costruibile<br/>" +
            "- <span style='color:#FFFF00'>Giallo (12-20 deg):</span> Veicoli lenti<br/>" +
            "- <span style='color:#FF9F1C'>Arancio (20-33 deg):</span> Solo Kbot / Bot<br/>" +
            "- <span style='color:#FF3333'>Rosso (&gt;33 deg):</span> Rupi Invalicabili</html>");
        tip.setFont(BarMapStudio.font(Font.PLAIN, 11));
        tip.setForeground(TEXT_MUTED);
        infoBox.add(tip, BorderLayout.CENTER);
        infoBox.setAlignmentX(0f);
        side.add(infoBox);

        side.add(Box.createVerticalStrut(12));

        ScrollableTrackWidthPanel container = new ScrollableTrackWidthPanel(new BorderLayout());
        container.setBackground(CARD_BG);
        container.add(side, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBackground(CARD_BG);
        scroll.getViewport().setBackground(CARD_BG);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        return scroll;
    }

    private void styleSidebarTabButton(JToggleButton b) {
        b.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        b.setBackground(CARD_INNER);
        b.setForeground(TEXT_MAIN);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private JComponent buildRightSidebar() {
        JPanel rightContainer = new JPanel(new BorderLayout(0, 0));
        rightContainer.setBackground(CARD_BG);

        // 1. Top Sub-Tab Navigation Bar
        JPanel tabBar = new JPanel(new GridLayout(1, 2, 4, 0));
        tabBar.setBackground(CARD_BG);
        tabBar.setBorder(new EmptyBorder(8, 10, 6, 10));

        tabMappaBtn = new JToggleButton("Dettagli Mappa", true);
        tabOggettiBtn = new JToggleButton("Oggetti (" + ReadyObjectsManager.getInstance().getReadyObjects().size() + ")", false);

        styleSidebarTabButton(tabMappaBtn);
        styleSidebarTabButton(tabOggettiBtn);
        tabMappaBtn.setForeground(ACCENT_CYAN);
        tabOggettiBtn.setForeground(TEXT_MUTED);

        ButtonGroup tabGroup = new ButtonGroup();
        tabGroup.add(tabMappaBtn);
        tabGroup.add(tabOggettiBtn);

        tabBar.add(tabMappaBtn);
        tabBar.add(tabOggettiBtn);

        // 2. CardLayout Center Container
        rightCardsLayout = new CardLayout();
        rightCardsPanel = new JPanel(rightCardsLayout);
        rightCardsPanel.setOpaque(false);

        // --- CARD 1: MAPPA (Proprieta', Mex, Reclaim, Eco, Assistenti) ---
        ScrollableTrackWidthPanel side = new ScrollableTrackWidthPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(CARD_BG);
        side.setBorder(new EmptyBorder(4, 10, 10, 10));

        // Map Name field
        JLabel nameLbl = new JLabel("Nome Mappa Personalizzato:");
        nameLbl.setFont(BarMapStudio.font(Font.PLAIN, 12));
        nameLbl.setForeground(TEXT_MUTED);
        nameLbl.setAlignmentX(0f);
        side.add(nameLbl);
        side.add(Box.createVerticalStrut(4));

        mapNameField.setBackground(CARD_INNER);
        mapNameField.setForeground(TEXT_MAIN);
        mapNameField.setCaretColor(ACCENT_CYAN);
        mapNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(6, 8, 6, 8)
        ));
        mapNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        mapNameField.setAlignmentX(0f);
        side.add(mapNameField);
        side.add(Box.createVerticalStrut(10));

        // Stats Counters in a 2x2 grid
        JPanel statGrid = new JPanel(new GridLayout(2, 2, 6, 6));
        statGrid.setOpaque(false);
        statGrid.add(mexCountLabel);
        statGrid.add(geoCountLabel);
        statGrid.add(spawnCountLabel);
        statGrid.add(reclaimCountLabel);
        statGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        statGrid.setAlignmentX(0f);
        side.add(statGrid);
        side.add(Box.createVerticalStrut(10));

        // Mex Placement Metal Card
        JPanel mexMetalCard = new JPanel();
        mexMetalCard.setLayout(new BoxLayout(mexMetalCard, BoxLayout.Y_AXIS));
        mexMetalCard.setBackground(CARD_INNER);
        mexMetalCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        mexMetalCard.setAlignmentX(0f);
        mexMetalCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel mexMetalCardTitle = new JLabel("VALORE METALLO MEX (PIASTRINE):");
        mexMetalCardTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        mexMetalCardTitle.setForeground(ACCENT_CYAN);
        mexMetalCard.add(mexMetalCardTitle);
        mexMetalCard.add(Box.createVerticalStrut(6));

        JPanel spinnerRow = new JPanel(new BorderLayout(8, 0));
        spinnerRow.setOpaque(false);
        mexPlacementMetalSpinner.setBackground(CARD_INNER);
        mexPlacementMetalSpinner.setForeground(TEXT_MAIN);
        spinnerRow.add(mexPlacementMetalSpinner, BorderLayout.CENTER);
        mexColorSwatch.setPreferredSize(new Dimension(32, 28));
        mexColorSwatch.setOpaque(false);
        spinnerRow.add(mexColorSwatch, BorderLayout.EAST);
        mexMetalCard.add(spinnerRow);
        mexMetalCard.add(Box.createVerticalStrut(6));

        JPanel presetRow = new JPanel(new GridLayout(1, 6, 4, 0));
        presetRow.setOpaque(false);
        float[] presets = {1.0f, 1.5f, 2.0f, 3.0f, 3.5f, 5.0f};
        for (float p : presets) {
            JButton pb = createGhostButton(String.format(Locale.ROOT, "%.1f", p));
            pb.setMargin(new Insets(2, 2, 2, 2));
            pb.setFont(BarMapStudio.fontCond(Font.BOLD, 10));
            pb.addActionListener(e -> setPlacementMetalValue(p, true, true));
            presetRow.add(pb);
        }
        mexMetalCard.add(presetRow);
        mexMetalCard.add(Box.createVerticalStrut(6));

        JLabel shadeHint = new JLabel("< Chiaro (Povero)          Scuro (Ricco) >");
        shadeHint.setFont(BarMapStudio.font(Font.ITALIC, 10));
        shadeHint.setForeground(TEXT_MUTED);
        shadeHint.setAlignmentX(0.5f);
        mexMetalCard.add(shadeHint);

        side.add(mexMetalCard);
        side.add(Box.createVerticalStrut(10));

        // Auto Assistants Header
        JLabel assistTitle = new JLabel("Assistenti Posizionamento Rapido:");
        assistTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        assistTitle.setForeground(TEXT_MAIN);
        assistTitle.setAlignmentX(0f);
        side.add(assistTitle);
        side.add(Box.createVerticalStrut(6));

        JButton autoMexBtn = createGhostButton("+ Auto Mex Basi (Spawn)");
        autoMexBtn.setAlignmentX(0f);
        autoMexBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        autoMexBtn.setToolTipText("Posiziona automaticamente 3 estrattori di metallo attorno a ciascuno spawn");
        autoMexBtn.addActionListener(e -> canvas.autoPlaceBaseMexes(3, ((Number) baseMetalSpinner.getValue()).floatValue()));
        side.add(autoMexBtn);
        side.add(Box.createVerticalStrut(4));

        JButton autoContestedBtn = createGhostButton("+ Auto Mex Contesi Centro");
        autoContestedBtn.setAlignmentX(0f);
        autoContestedBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        autoContestedBtn.setToolTipText("Posiziona automaticamente 6 piastrine mex contestate al centro mappa");
        autoContestedBtn.addActionListener(e -> canvas.autoPlaceContestedMexes(6, ((Number) maxMetalSpinner.getValue()).floatValue()));
        side.add(autoContestedBtn);
        side.add(Box.createVerticalStrut(4));

        JButton autoReclaimBtn = createGhostButton("+ Auto Reclaim (Rocce & Alberi)");
        autoReclaimBtn.setAlignmentX(0f);
        autoReclaimBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        autoReclaimBtn.setToolTipText("Distribuisce rocce e alberi proceduralmente in base al bioma e alla densita'");
        autoReclaimBtn.addActionListener(e -> canvas.autoSeedReclaim(reclaimSlider.getValue() / 10f));
        side.add(autoReclaimBtn);
        side.add(Box.createVerticalStrut(4));

        JPanel clearRow = new JPanel(new GridLayout(1, 2, 6, 0));
        clearRow.setOpaque(false);
        JButton clrMex = createGhostButton("Svuota Mex");
        clrMex.addActionListener(e -> canvas.clearMexes());
        JButton clrRec = createGhostButton("Svuota Reclaim");
        clrRec.addActionListener(e -> canvas.clearReclaim());
        clearRow.add(clrMex);
        clearRow.add(clrRec);
        clearRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        clearRow.setAlignmentX(0f);
        side.add(clearRow);

        side.add(Box.createVerticalStrut(12));

        // Economy
        JPanel ecoCard = new JPanel(new GridLayout(0, 2, 6, 6));
        ecoCard.setOpaque(false);
        ecoCard.add(new JLabel("Metallo Base:"));
        ecoCard.add(baseMetalSpinner);
        ecoCard.add(new JLabel("Metallo Max:"));
        ecoCard.add(maxMetalSpinner);
        ecoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        ecoCard.setAlignmentX(0f);
        side.add(ecoCard);
        side.add(Box.createVerticalStrut(8));

        side.add(createFieldRow("Densita' Reclaim:", reclaimSlider));
        reclaimValueLabel.setFont(BarMapStudio.font(Font.PLAIN, 11));
        reclaimValueLabel.setForeground(TEXT_MUTED);
        reclaimValueLabel.setAlignmentX(0f);
        side.add(reclaimValueLabel);
        side.add(Box.createVerticalStrut(10));

        ScrollableTrackWidthPanel mappaContainer = new ScrollableTrackWidthPanel(new BorderLayout());
        mappaContainer.setBackground(CARD_BG);
        mappaContainer.add(side, BorderLayout.NORTH);

        JScrollPane mappaScroll = new JScrollPane(mappaContainer);
        mappaScroll.setBackground(CARD_BG);
        mappaScroll.getViewport().setBackground(CARD_BG);
        mappaScroll.setBorder(null);
        mappaScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mappaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        mappaScroll.getVerticalScrollBar().setUnitIncrement(18);

        // --- CARD 2: OGGETTI (Dedito completamente alla palette oggetti & scenery) ---
        JPanel oggettiContainer = new JPanel(new BorderLayout());
        oggettiContainer.setBackground(CARD_BG);
        oggettiContainer.setBorder(new EmptyBorder(4, 10, 6, 10));
        if (readyTray != null) {
            oggettiContainer.add(readyTray, BorderLayout.CENTER);
        }

        rightCardsPanel.add(mappaScroll, "MAPPA");
        rightCardsPanel.add(oggettiContainer, "OGGETTI");

        tabMappaBtn.addActionListener(e -> {
            rightCardsLayout.show(rightCardsPanel, "MAPPA");
            tabMappaBtn.setForeground(ACCENT_CYAN);
            tabOggettiBtn.setForeground(TEXT_MUTED);
        });
        tabOggettiBtn.addActionListener(e -> {
            rightCardsLayout.show(rightCardsPanel, "OGGETTI");
            tabOggettiBtn.setForeground(ACCENT_CYAN);
            tabMappaBtn.setForeground(TEXT_MUTED);
        });

        ReadyObjectsManager.getInstance().addListener(list -> {
            SwingUtilities.invokeLater(() -> {
                if (tabOggettiBtn != null) {
                    tabOggettiBtn.setText("Oggetti (" + list.size() + ")");
                }
            });
        });

        // 3. Export Actions Card (Docked in basso, accessibile da entrambi i tab)
        JPanel exportCard = new JPanel();
        exportCard.setLayout(new BoxLayout(exportCard, BoxLayout.Y_AXIS));
        exportCard.setBackground(CARD_INNER);
        exportCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(8, 10, 8, 10)
        ));

        genFinalMapBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        genFinalMapBtn.setAlignmentX(0f);
        exportCard.add(genFinalMapBtn);
        exportCard.add(Box.createVerticalStrut(6));

        genAndPlayBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        genAndPlayBtn.setAlignmentX(0f);
        exportCard.add(genAndPlayBtn);
        exportCard.add(Box.createVerticalStrut(6));

        progressBar.setVisible(false);
        progressBar.setForeground(ACCENT_CYAN);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        progressBar.setAlignmentX(0f);
        exportCard.add(progressBar);
        exportCard.add(Box.createVerticalStrut(4));

        statusLabel.setFont(BarMapStudio.font(Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_MUTED);
        statusLabel.setAlignmentX(0f);
        exportCard.add(statusLabel);

        rightContainer.add(tabBar, BorderLayout.NORTH);
        rightContainer.add(rightCardsPanel, BorderLayout.CENTER);
        rightContainer.add(exportCard, BorderLayout.SOUTH);

        return rightContainer;
    }

    private void updateCounters() {
        SCMap m = canvas.getMap();
        if (m != null) {
            mexCountLabel.setText("Mex: " + m.getMexes().size());
            geoCountLabel.setText("Geos: " + m.getHydros().size());
            spawnCountLabel.setText("Spawns: " + m.getSpawns().size());
            reclaimCountLabel.setText("Reclaim: " + m.getProps().size());
        } else {
            mexCountLabel.setText("Mex: 0");
            geoCountLabel.setText("Geos: 0");
            spawnCountLabel.setText("Spawns: 0");
            reclaimCountLabel.setText("Reclaim: 0");
        }
        if (readyTray != null && canvas != null) {
            int placed = canvas.getCustomEntities() != null ? canvas.getCustomEntities().size() : 0;
            readyTray.updatePlacedCount(placed);
        }
        if (tabOggettiBtn != null) {
            int readyCount = ReadyObjectsManager.getInstance().getReadyObjects().size();
            tabOggettiBtn.setText("Oggetti (" + readyCount + ")");
        }
    }

    private void doGenerateGround() {
        MapDimension dim = (MapDimension) sizeCombo.getSelectedItem();
        if (dim == null) dim = MAP_DIMENSIONS[2];
        int width = dim.width();
        int height = dim.height();
        String styleName = (String) styleCombo.getSelectedItem();
        String biomeName = (String) biomeCombo.getSelectedItem();
        SymmetryOption symOpt = (SymmetryOption) symmetryCombo.getSelectedItem();
        Symmetry symmetry = symOpt != null ? symOpt.symmetry() : Symmetry.POINT2;
        long seed;
        try {
            seed = Long.parseLong(seedField.getText().trim());
        } catch (Exception e) {
            seed = new Random().nextLong();
        }

        TerrainStyle terrainStyle = TerrainStyle.valueOf(styleName);

        setStatus("Generazione terreno (" + width + "x" + height + ") in corso...");
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        genGroundBtn.setEnabled(false);

        long finalSeed = seed;
        new Thread(() -> {
            try {
                GroundGenerator.GroundResult res = GroundGenerator.generateGroundOnly(
                    width,
                    height,
                    terrainStyle,
                    biomeName,
                    symmetry,
                    2, // default spawns
                    2,
                    finalSeed
                );

                SwingUtilities.invokeLater(() -> {
                    canvas.setMapAndGround(res.map(), res.preview(), res.slopeOverlay(), res.symmetrySettings());
                    refreshScene3D();
                    if (res.symmetrySettings() != null) {
                        selectSymmetryInCombo(res.symmetrySettings().terrainSymmetry());
                        updateAxisToggleLabel(res.symmetrySettings().terrainSymmetry());
                    }
                    float wElev = res.map().getWaterElevation();
                    waterLevelSpinner.setValue((double) Math.round(wElev));
                    setStatus("Terreno generato! Premi [L] per visualizzare la Slope Map.");
                    progressBar.setVisible(false);
                    genGroundBtn.setEnabled(true);
                    if (onMapChangedCallback != null) {
                        onMapChangedCallback.run();
                    }
                });
            } catch (Throwable ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    setStatus("Errore generazione terreno: " + ex.getMessage());
                    progressBar.setVisible(false);
                    genGroundBtn.setEnabled(true);
                });
            }
        }, "ground-gen").start();
    }

    private void doRegenerateFromHeightmap(BufferedImage customHm) {
        doRegenerateFromHeightmap(customHm, null);
    }

    private void doRegenerateFromHeightmap(BufferedImage customHm, Runnable onDone) {
        SCMap existing = canvas.getMap();
        if (existing == null && customHm == null) {
            JOptionPane.showMessageDialog(this,
                "Prima genera un terreno iniziale o seleziona un file heightmap da importare!",
                "Nessun Terreno Presente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MapDimension dim = (MapDimension) sizeCombo.getSelectedItem();
        if (dim == null) dim = MAP_DIMENSIONS[2];
        int width = dim.width();
        int height = dim.height();
        String biomeName = (String) biomeCombo.getSelectedItem();
        SymmetryOption symOpt = (SymmetryOption) symmetryCombo.getSelectedItem();
        Symmetry symmetry = symOpt != null ? symOpt.symmetry() : Symmetry.POINT2;

        float maxHeight = ((Number) maxHeightSpinner.getValue()).floatValue();
        float waterLevel = ((Number) waterLevelSpinner.getValue()).floatValue();

        long seed;
        try {
            seed = Long.parseLong(seedField.getText().trim());
        } catch (Exception e) {
            seed = new Random().nextLong();
        }

        setStatus("Rigenerazione Ground da Heightmap (MaxH: " + (int) maxHeight + "m, Acqua: " + (int) waterLevel + "m)...");
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        btnRegenGround.setEnabled(false);
        btnLoadHeightmap.setEnabled(false);
        btnLoadTexture.setEnabled(false);
        btnSmoothSlopes.setEnabled(false);

        long finalSeed = seed;
        new Thread(() -> {
            try {
                GroundGenerator.GroundResult res = GroundGenerator.generateGroundFromHeightmap(
                    existing,
                    customHm,
                    width,
                    height,
                    maxHeight,
                    waterLevel,
                    biomeName,
                    symmetry,
                    2,
                    2,
                    finalSeed
                );

                SwingUtilities.invokeLater(() -> {
                    canvas.setMapAndGround(res.map(), res.preview(), res.slopeOverlay(), res.symmetrySettings());
                    refreshScene3D();
                    if (res.symmetrySettings() != null) {
                        selectSymmetryInCombo(res.symmetrySettings().terrainSymmetry());
                        updateAxisToggleLabel(res.symmetrySettings().terrainSymmetry());
                    }
                    setStatus("Ground rigenerato con successo da Heightmap! [L per Slope Map]");
                    progressBar.setVisible(false);
                    btnRegenGround.setEnabled(true);
                    btnLoadHeightmap.setEnabled(true);
                    btnLoadTexture.setEnabled(true);
                    btnSmoothSlopes.setEnabled(true);
                    if (onMapChangedCallback != null) {
                        onMapChangedCallback.run();
                    }
                    if (onDone != null) {
                        onDone.run();
                    }
                });
            } catch (Throwable ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    setStatus("Errore rigenerazione: " + ex.getMessage());
                    progressBar.setVisible(false);
                    btnRegenGround.setEnabled(true);
                    btnLoadHeightmap.setEnabled(true);
                    btnLoadTexture.setEnabled(true);
                    btnSmoothSlopes.setEnabled(true);
                    JOptionPane.showMessageDialog(this,
                        "Errore durante la rigenerazione da heightmap:\n" + ex.getMessage(),
                        "Errore", JOptionPane.ERROR_MESSAGE);
                });
            }
        }, "heightmap-ground-regen").start();
    }

    private void doLoadHeightmapFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleziona File Heightmap (PNG, RAW, BMP)");
        chooser.setFileFilter(new FileNameExtensionFilter("Immagini Heightmap (*.png, *.raw, *.bmp)", "png", "raw", "bmp"));
        int ret = chooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                BufferedImage loaded = ImageIO.read(f);
                if (loaded == null) {
                    byte[] bytes = Files.readAllBytes(f.toPath());
                    int side = (int) Math.round(Math.sqrt(bytes.length / 2.0));
                    if (side * side * 2 == bytes.length) {
                        loaded = new BufferedImage(side, side, BufferedImage.TYPE_USHORT_GRAY);
                        WritableRaster wr = loaded.getRaster();
                        int idx = 0;
                        int[] p = new int[1];
                        for (int y = 0; y < side; y++) {
                            for (int x = 0; x < side; x++) {
                                int b1 = bytes[idx++] & 0xFF;
                                int b2 = bytes[idx++] & 0xFF;
                                p[0] = (b2 << 8) | b1;
                                wr.setPixel(x, y, p);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Formato file non supportato o non riconosciuto.", "Errore Import", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                setStatus("Heightmap caricata: " + f.getName() + " (" + loaded.getWidth() + "x" + loaded.getHeight() + ")");
                doRegenerateFromHeightmap(loaded);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Errore durante il caricamento del file:\n" + ex.getMessage(), "Errore File", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doLoadCustomTextureFile() {
        JFileChooser chooser = new JFileChooser();
        File defaultDir = new File("custom_assets/textures");
        if (defaultDir.exists()) chooser.setCurrentDirectory(defaultDir);
        chooser.setDialogTitle("Seleziona Texture Terreno (PNG, JPG, BMP)");
        chooser.setFileFilter(new FileNameExtensionFilter("Immagini Texture (*.png, *.jpg, *.jpeg, *.bmp)", "png", "jpg", "jpeg", "bmp"));
        int ret = chooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                BufferedImage loaded = ImageIO.read(f);
                if (loaded == null) {
                    JOptionPane.showMessageDialog(this, "Formato immagine non supportato o non valido.", "Errore Import", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                SCMap currentMap = canvas.getMap();
                if (currentMap == null) {
                    JOptionPane.showMessageDialog(this, "Genera prima una mappa base su cui applicare la texture!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int targetW = currentMap.getWidth() > 0 ? currentMap.getWidth() : currentMap.getSize();
                int targetH = currentMap.getHeight() > 0 ? currentMap.getHeight() : currentMap.getSize();
                int previewDim = 512;
                int previewH = (int) Math.round(512.0 * ((double) targetH / targetW));

                BufferedImage scaled = new BufferedImage(previewDim, previewH, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = scaled.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.drawImage(loaded, 0, 0, previewDim, previewH, null);
                g.dispose();

                currentMap.setCustomTexture(loaded);
                currentMap.setPreview(scaled);
                if (canvas != null) {
                    canvas.setGroundPreview(scaled);
                } else {
                    canvas.repaint();
                }
                setStatus("Texture personalizzata applicata: " + f.getName() + " (" + loaded.getWidth() + "x" + loaded.getHeight() + " px)");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Errore durante il caricamento della texture:\n" + ex.getMessage(), "Errore File", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void applyComposedTerrain(BufferedImage diffuseMap, BufferedImage splatLow, BufferedImage splatHigh, String name) {
        applyComposedTerrain(diffuseMap, splatLow, splatHigh, null, name);
    }

    public void applyComposedTerrain(BufferedImage diffuseMap, BufferedImage splatLow, BufferedImage splatHigh, BufferedImage customHeightmap, String name) {
        SCMap currentMap = canvas != null ? canvas.getMap() : null;
        if (currentMap == null) {
            if (customHeightmap != null) {
                doRegenerateFromHeightmap(customHeightmap, () -> {
                    applyComposedTerrain(diffuseMap, splatLow, splatHigh, null, name);
                });
                return;
            }
            JOptionPane.showMessageDialog(this, "Nessuna mappa attiva trovata nell'Editor. Genera prima una mappa base su cui applicare la composizione!", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int mapW = currentMap.getWidth() > 0 ? currentMap.getWidth() : currentMap.getSize();
            int mapH = currentMap.getHeight() > 0 ? currentMap.getHeight() : currentMap.getSize();
            int previewDim = 512;
            int previewH = (int) Math.round(512.0 * ((double) mapH / mapW));

            BufferedImage scaled = new BufferedImage(previewDim, previewH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(diffuseMap, 0, 0, previewDim, previewH, null);
            g.dispose();

            currentMap.setCustomTexture(diffuseMap);
            currentMap.setPreview(scaled);
            if (canvas != null) {
                canvas.setGroundPreview(scaled);
            }

            // Se fornite, aggiorna anche le maschere interne di splatting per l'esportazione BAR
            if (splatLow != null && currentMap.getTextureMasksLow() != null) {
                BufferedImage targetLow = currentMap.getTextureMasksLow();
                Graphics2D gLow = targetLow.createGraphics();
                gLow.setComposite(AlphaComposite.Src);
                gLow.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                gLow.drawImage(splatLow, 0, 0, targetLow.getWidth(), targetLow.getHeight(), null);
                gLow.dispose();
            }
            if (splatHigh != null && currentMap.getTextureMasksHigh() != null) {
                BufferedImage targetHigh = currentMap.getTextureMasksHigh();
                Graphics2D gHigh = targetHigh.createGraphics();
                gHigh.setComposite(AlphaComposite.Src);
                gHigh.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                gHigh.drawImage(splatHigh, 0, 0, targetHigh.getWidth(), targetHigh.getHeight(), null);
                gHigh.dispose();
            }

            if (canvas != null) canvas.repaint();
            setStatus("Composizione Texture & Splatting applicata: " + name + " (" + diffuseMap.getWidth() + "x" + diffuseMap.getHeight() + " px)");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore durante l'applicazione della texture:\n" + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doSmoothSlopes() {
        SCMap m = canvas.getMap();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Nessuna mappa presente su cui levigare le pendenze!", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setStatus("Levigatura pendenze eccessive in corso...");
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        btnSmoothSlopes.setEnabled(false);

        new Thread(() -> {
            try {
                GroundGenerator.smoothSlopes(m, 0.40f, 2);
                float waterElev = m.getWaterElevation();
                BufferedImage newSlope = GroundGenerator.generateSlopeOverlayImage(m, waterElev);
                SwingUtilities.invokeLater(() -> {
                    canvas.setSlopeOverlay(newSlope);
                    canvas.setShowSlopeMap(true);
                    updateSlopeButtonState();
                    setStatus("Pendenze ripide levigate! Rupi impraticabili ridotte. [Slope Map ON]");
                    progressBar.setVisible(false);
                    btnSmoothSlopes.setEnabled(true);
                });
            } catch (Throwable ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    setStatus("Errore durante la levigatura: " + ex.getMessage());
                    progressBar.setVisible(false);
                    btnSmoothSlopes.setEnabled(true);
                });
            }
        }, "smooth-slopes").start();
    }

    private void doGenerateFinalMap(boolean launchSkirmish) {
        SCMap m = canvas.getMap();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Prima genera il terreno cliccando su 'GENERA TERRENO'!", "Nessun Terreno", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Path mapsDir = barDataFolder != null ? barDataFolder.resolve("maps") : null;
        if (mapsDir == null || !Files.isDirectory(mapsDir)) {
            JOptionPane.showMessageDialog(this, "Cartella Beyond All Reason maps non trovata in:\n" + mapsDir, "Cartella BAR non trovata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String rawName = mapNameField.getText().trim();
        if (rawName.isBlank()) {
            rawName = "Map_" + System.currentTimeMillis();
        }
        if (!rawName.startsWith("Map_") && !rawName.startsWith("Neroxis_")) {
            rawName = "Map_" + rawName;
        }
        final String finalMapName = rawName;

        setStatus("Compilazione mappa BAR in corso...");
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        genFinalMapBtn.setEnabled(false);
        genAndPlayBtn.setEnabled(false);

        float baseMetal = ((Number) baseMetalSpinner.getValue()).floatValue();
        float maxMetal = ((Number) maxMetalSpinner.getValue()).floatValue();
        float reclaimVal = reclaimSlider.getValue() / 10.0f;
        int numTeams = Math.max(2, (int) m.getSpawns().stream().mapToInt(com.faforever.neroxis.map.Spawn::getTeamID).distinct().count());

        m.getCustomEntities().clear();
        m.getCustomEntities().addAll(canvas.getCustomEntities());

        BarExportOptions options = new BarExportOptions(
            2,
            0.7f,
            baseMetal,
            Math.min(baseMetal, 2.0f),
            maxMetal,
            false,
            numTeams,
            finalMapName,
            "1.0",
            "BAR Map",
            reclaimVal,
            null,
            3,
            16,
            18
        );

        new Thread(() -> {
            try {
                Path skirmishScript = barDataFolder.resolve("_neroxis_skirmish.txt");
                Path archive = BarMapExporter.exportMap(mapsDir, m, options, skirmishScript);

                SwingUtilities.invokeLater(() -> {
                    setStatus("Mappa esportata con successo: " + archive.getFileName());
                    progressBar.setVisible(false);
                    genFinalMapBtn.setEnabled(true);
                    genAndPlayBtn.setEnabled(true);

                    if (onMapCreated != null) {
                        onMapCreated.accept(archive);
                    }
                    if (launchSkirmish && onPlaySkirmish != null) {
                        onPlaySkirmish.accept(archive);
                    }
                });
            } catch (Throwable ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    setStatus("Errore durante l'esportazione: " + ex.getMessage());
                    progressBar.setVisible(false);
                    genFinalMapBtn.setEnabled(true);
                    genAndPlayBtn.setEnabled(true);
                });
            }
        }, "final-map-export").start();
    }

    private void setStatus(String text) {
        if (text == null) text = "";
        statusLabel.setText("<html><body style='width: 250px;'>" + text.replace("<", "&lt;").replace(">", "&gt;") + "</body></html>");
        statusLabel.setToolTipText(text);
    }

    private static JPanel createFieldRow(String label, JComponent comp) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(BarMapStudio.font(Font.BOLD, 12));
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(0f);
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        comp.setAlignmentX(0f);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        p.add(comp);
        p.setAlignmentX(0f);
        return p;
    }

    private static JPanel createParamRow(String labelText, JSpinner spinner, String unit) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(BarMapStudio.font(Font.BOLD, 11));
        lbl.setForeground(TEXT_MAIN);
        lbl.setToolTipText(labelText);
        row.add(lbl, BorderLayout.CENTER);

        JPanel spinnerWrap = new JPanel(new BorderLayout(4, 0));
        spinnerWrap.setOpaque(false);
        spinner.setPreferredSize(new Dimension(68, 25));
        spinnerWrap.add(spinner, BorderLayout.CENTER);
        if (unit != null && !unit.isEmpty()) {
            JLabel uLbl = new JLabel(unit);
            uLbl.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
            uLbl.setForeground(ACCENT_CYAN);
            spinnerWrap.add(uLbl, BorderLayout.EAST);
        }
        row.add(spinnerWrap, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.setAlignmentX(0f);
        return row;
    }

    private static void styleSpinner(JSpinner spinner) {
        spinner.setFont(BarMapStudio.font(Font.BOLD, 12));
        spinner.setBackground(CARD_INNER);
        spinner.setForeground(TEXT_MAIN);
        spinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(2, 4, 2, 4)
        ));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor de) {
            JTextField tf = de.getTextField();
            tf.setBackground(CARD_INNER);
            tf.setForeground(TEXT_MAIN);
            tf.setCaretColor(ACCENT_CYAN);
            tf.setHorizontalAlignment(JTextField.RIGHT);
            tf.setFont(BarMapStudio.font(Font.BOLD, 12));
            tf.setColumns(5);
        }
    }

    private static JLabel createChip(String text, Color accent) {
        JLabel chip = new JLabel(text, SwingConstants.CENTER);
        chip.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        chip.setForeground(accent);
        chip.setBackground(CARD_INNER);
        chip.setOpaque(true);
        chip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 90), 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
        return chip;
    }

    private static JButton createPrimaryButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        b.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 35));
        b.setForeground(color);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1, true),
            new EmptyBorder(8, 14, 8, 14)
        ));
        return b;
    }

    private static JButton createGhostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        b.setBackground(CARD_INNER);
        b.setForeground(TEXT_MAIN);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(3, 6, 3, 6)
        ));
        return b;
    }

    public static class ScrollableTrackWidthPanel extends JPanel implements Scrollable {
        public ScrollableTrackWidthPanel() {
            super();
        }
        public ScrollableTrackWidthPanel(LayoutManager layout) {
            super(layout);
        }
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }
        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 18;
        }
        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 72;
        }
        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }
        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}

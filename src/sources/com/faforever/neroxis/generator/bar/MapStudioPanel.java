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
import java.io.StringWriter;
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
        new MapDimension(I18n.t("map.1"), 256, 256, Symmetry.POINT2),
        new MapDimension(I18n.t("map.2"), 384, 384, Symmetry.POINT2),
        new MapDimension(I18n.t("map.3"), 512, 512, Symmetry.POINT2),
        new MapDimension(I18n.t("map.4"), 640, 640, Symmetry.POINT2),
        new MapDimension(I18n.t("map.5"), 768, 768, Symmetry.POINT2),

        // Rettangolari con SPECCHIO DIAGONALE (Doppia Specchiatura Sia in X che in Z)
        new MapDimension(I18n.t("map.6"), 256, 384, Symmetry.POINT2),
        new MapDimension(I18n.t("map.7"), 384, 512, Symmetry.POINT2),
        new MapDimension(I18n.t("map.8"), 256, 512, Symmetry.POINT2),
        new MapDimension(I18n.t("map.9"), 512, 768, Symmetry.POINT2),
        new MapDimension(I18n.t("map.10"), 384, 256, Symmetry.POINT2),
        new MapDimension(I18n.t("map.11"), 512, 384, Symmetry.POINT2),
        new MapDimension(I18n.t("map.12"), 512, 256, Symmetry.POINT2),
        new MapDimension(I18n.t("map.13"), 768, 512, Symmetry.POINT2),

        // Rettangolari con SOLO SPECCHIO ASSE Z (Nord vs Sud / Sopra-Sotto)
        new MapDimension(I18n.t("map.14"), 256, 384, Symmetry.Z),
        new MapDimension(I18n.t("map.15"), 384, 512, Symmetry.Z),
        new MapDimension(I18n.t("map.16"), 512, 768, Symmetry.Z),
        new MapDimension(I18n.t("map.17"), 384, 256, Symmetry.Z),
        new MapDimension(I18n.t("map.18"), 512, 384, Symmetry.Z),

        // Rettangolari con SOLO SPECCHIO ASSE X (Ovest vs Est / Sinistra-Destra)
        new MapDimension(I18n.t("map.19"), 256, 384, Symmetry.X),
        new MapDimension(I18n.t("map.20"), 384, 512, Symmetry.X),
        new MapDimension(I18n.t("map.21"), 384, 256, Symmetry.X),
        new MapDimension(I18n.t("map.22"), 512, 384, Symmetry.X),
        new MapDimension(I18n.t("map.23"), 768, 512, Symmetry.X),

        // Rettangolari con QUADRUPLA SPECCHIO (Sia X che Z - 4 Quadranti)
        new MapDimension(I18n.t("map.24"), 384, 512, Symmetry.QUAD),
        new MapDimension(I18n.t("map.25"), 512, 384, Symmetry.QUAD)
    };

    public static final record SymmetryOption(String label, Symmetry symmetry) {
        @Override
        public String toString() {
            return label;
        }
    }

    private static final SymmetryOption[] SYMMETRY_OPTIONS = {
        new SymmetryOption(I18n.t("map.26"), Symmetry.POINT2),
        new SymmetryOption(I18n.t("map.27"), Symmetry.Z),
        new SymmetryOption(I18n.t("map.28"), Symmetry.X),
        new SymmetryOption(I18n.t("map.29"), Symmetry.QUAD),
        new SymmetryOption(I18n.t("map.30"), Symmetry.NONE)
    };

    private static final String[] TERRAIN_STYLES = {
        "VALLEY", "LAND_BRIDGE", "MOUNTAIN_RANGE", "BIG_ISLANDS", "SMALL_ISLANDS",
        "CENTER_LAKE", "FLOODED", "DROP_PLATEAU", "LITTLE_MOUNTAIN", "MULTILEVEL_LAST",
        "ONE_ISLAND", "RIVERS", "RIVERS_AND_OCEANS", "FRACTAL_LAND", "FRACTAL_PLATEAU",
        "FRACTAL_NAVY", "SETONS", "BASIC",
        // varianti gia' presenti nel generatore: stesso tema, ma il terreno nasce prima e le basi vengono piazzate dopo
        "BASIC_LAST", "CENTER_LAKE_LAST", "DROP_PLATEAU_LAST", "LITTLE_MOUNTAIN_LAST", "MOUNTAIN_RANGE_LAST", "VALLEY_LAST",
        // stili nuovi, disegnati da TerrainSynth (non esistono nel generatore Neroxis)
        "ARCHIPELAGO", "CANYON", "CRATER", "LANES", "MESA"
    };

    static {
        // nel menu le voci sono in ordine alfabetico
        java.util.Arrays.sort(TERRAIN_STYLES, String.CASE_INSENSITIVE_ORDER);
    }

    private static final String[] BIOMES = buildBiomeList();

    /** Biomi Neroxis + biomi ricavati dalle mappe ufficiali di BAR (voci "BAR: ..."). */
    private static String[] buildBiomeList() {
        java.util.List<String> names = new java.util.ArrayList<>(java.util.List.of(
            "DESERT", "MARS", "BRIMSTONE", "EARLYAUTUMN", "FRITHEN", "MOONLIGHT",
            "PRAYER", "STONES", "SUNSET", "SYRTIS", "WINDINGRIVER", "WONDER", "CRYSTALLINE"));
        // ordine alfabetico: prima i biomi del generatore, poi quelli ricavati dalle mappe di BAR
        names.sort(String.CASE_INSENSITIVE_ORDER);
        java.util.List<String> barNames = new java.util.ArrayList<>(com.faforever.neroxis.exporter.bar.BarBiomes.labels());
        barNames.sort(String.CASE_INSENSITIVE_ORDER);
        names.addAll(barNames);
        return names.toArray(new String[0]);
    }

    /**
     * Scrive un testo in maiuscoletto (HTML per Swing): le iniziali restano a grandezza piena, le altre
     * lettere diventano maiuscole piu' piccole. I nomi interni tipo LAND_BRIDGE diventano "Land Bridge".
     */
    static String smallCaps(String text, int fontSize) {
        String source = text;
        if (source.equals(source.toUpperCase(Locale.ROOT))) {
            // nome interno tutto maiuscolo: iniziale di ogni parola grande, resto piccolo
            StringBuilder title = new StringBuilder();
            boolean start = true;
            for (char c : source.replace('_', ' ').toCharArray()) {
                title.append(start ? c : Character.toLowerCase(c));
                start = c == ' ';
            }
            source = title.toString();
        }
        StringBuilder html = new StringBuilder("<html><nobr>");
        boolean inSmall = false;
        for (char c : source.toCharArray()) {
            boolean lower = Character.isLowerCase(c);
            if (lower != inSmall) {
                html.append(lower ? "<font size='-1'>" : "</font>");
                inSmall = lower;
            }
            char shown = lower ? Character.toUpperCase(c) : c;
            html.append(shown == '&' ? "&amp;" : shown == '<' ? "&lt;" : shown == '>' ? "&gt;" : String.valueOf(shown));
        }
        if (inSmall) {
            html.append("</font>");
        }
        return html.append("</nobr></html>").toString();
    }

    private final MapStudioCanvas canvas = new MapStudioCanvas();
    private final JComboBox<MapDimension> sizeCombo = new JComboBox<>(MAP_DIMENSIONS);
    private final JComboBox<String> styleCombo = new JComboBox<>(TERRAIN_STYLES);
    private final JComboBox<String> biomeCombo = new JComboBox<>(BIOMES);
    private final JSpinner playersPerTeamSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
    private final JComboBox<SymmetryOption> symmetryCombo = new JComboBox<>(SYMMETRY_OPTIONS);
    private final JTextField seedField = new JTextField();
    private final JButton rollSeedBtn = createGhostButton(I18n.t("map.31"));
    private final JButton genGroundBtn = createPrimaryButton(I18n.t("map.32"), ACCENT_CYAN);

    // Height, Water & Slope Controls
    private final JSpinner maxHeightSpinner = new JSpinner(new SpinnerNumberModel(250.0, 20.0, 1500.0, 10.0));
    private final JSpinner waterLevelSpinner = new JSpinner(new SpinnerNumberModel(25.0, -100.0, 800.0, 5.0));
    // Rilievo in tempo reale: i cursori rigenerano il terreno mentre li muovi
    private final JSlider maxHeightSlider = new JSlider(20, 1500, 250);
    private final JSlider waterLevelSlider = new JSlider(-100, 800, 25);
    private boolean suppressLiveRegen = false;
    // Modellazione del rilievo: i cursori lavorano sempre sull'heightmap di base, mai su quella gia' modificata
    private BufferedImage reliefBase;
    private final ReliefShaper.Params reliefParams = new ReliefShaper.Params();
    private final java.util.List<Runnable> reliefSliderResets = new java.util.ArrayList<>();
    private boolean regenRunning = false;
    private boolean regenPending = false;
    private final javax.swing.Timer liveRegenTimer = new javax.swing.Timer(120, e -> {
        if (canvas.getMap() != null) {
            doRegenerateFromHeightmap(null);
        }
    });
    private final JToggleButton slopeToggleBtn = new JToggleButton(I18n.t("map.33"), false);
    private final JSlider slopeOpacitySlider = new JSlider(10, 100, 70);
    private final JToggleButton view3DBtn = new JToggleButton(I18n.t("map.34"), false);
    private MapScene3DCanvas scene3D;
    private JSplitPane centerSplit;
    private JPanel centerWrapper;
    private boolean view3DVisible = false;
    private final JButton btnRegenGround = createPrimaryButton(I18n.t("map.35"), ACCENT_AMBER);
    private final JButton btnLoadHeightmap = createGhostButton(I18n.t("map.36"));
    private final JButton btnLoadTexture = createGhostButton(I18n.t("map.37"));
    private final JButton btnSmoothSlopes = createGhostButton(I18n.t("map.38"));

    private final java.util.Map<MapStudioCanvas.Tool, JToggleButton> toolButtons = new java.util.EnumMap<>(MapStudioCanvas.Tool.class);
    private JToggleButton symToggle;
    private final JButton axisToggleBtn = createGhostButton(I18n.t("map.39"));

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
    private final JLabel reclaimValueLabel = new JLabel(I18n.t("map.40"));

    // Counters
    private final JLabel mexCountLabel = createChip(I18n.t("map.41"), ACCENT_CYAN);
    private final JLabel geoCountLabel = createChip(I18n.t("map.42"), ACCENT_AMBER);
    private final JLabel spawnCountLabel = createChip(I18n.t("map.43"), ACCENT_GREEN);
    private final JLabel reclaimCountLabel = createChip(I18n.t("map.44"), TEXT_MUTED);

    // Final Action
    private final JButton genFinalMapBtn = createPrimaryButton(I18n.t("map.45"), ACCENT_GREEN);
    private final JButton genAndPlayBtn = createPrimaryButton(I18n.t("map.46"), ACCENT_AMBER);
    private final JButton hostMatchBtn = createPrimaryButton(
        AppPaths.FRIENDS_MATCH_ENABLED ? I18n.t("host.button") : I18n.t("host.button.soon"), AppPaths.FRIENDS_MATCH_ENABLED ? ACCENT_CYAN : TEXT_MUTED);
    /** Avvia BAR con uno script di partita e, se indicata, una versione precisa del motore. */
    private java.util.function.BiConsumer<Path, String> scriptLauncher;
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel statusLabel = new JLabel(I18n.t("map.47"));

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
                    I18n.t("map.48"),
                    I18n.t("map.49"),
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
        if (AppPaths.FRIENDS_MATCH_ENABLED) {
            hostMatchBtn.addActionListener(e -> doHostMatch());
            hostMatchBtn.setToolTipText(I18n.t("host.button.tip"));
        } else {
            // funzione in arrivo: il pulsante resta visibile e spiega cosa fara'
            hostMatchBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, I18n.t("host.soon.text"), I18n.t("host.soon.title"), JOptionPane.INFORMATION_MESSAGE));
            hostMatchBtn.setToolTipText(I18n.t("host.soon.tip"));
        }

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
            axisToggleBtn.setText(I18n.t("map.50"));
            axisToggleBtn.setToolTipText(I18n.t("map.51"));
            axisToggleBtn.setForeground(ACCENT_CYAN);
        } else if (sym == Symmetry.Z) {
            axisToggleBtn.setText(I18n.t("map.39"));
            axisToggleBtn.setToolTipText(I18n.t("map.52"));
            axisToggleBtn.setForeground(ACCENT_CYAN);
        } else if (sym == Symmetry.X) {
            axisToggleBtn.setText(I18n.t("map.53"));
            axisToggleBtn.setToolTipText(I18n.t("map.54"));
            axisToggleBtn.setForeground(ACCENT_CYAN);
        } else if (sym == Symmetry.QUAD) {
            axisToggleBtn.setText(I18n.t("map.55"));
            axisToggleBtn.setToolTipText(I18n.t("map.56"));
            axisToggleBtn.setForeground(ACCENT_CYAN);
        } else {
            axisToggleBtn.setText(I18n.t("map.57"));
            axisToggleBtn.setToolTipText(I18n.t("map.58"));
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
        view3DBtn.setText(v ? I18n.t("map.59") : I18n.t("map.34"));
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
        slopeToggleBtn.setText(active ? I18n.t("map.60") : I18n.t("map.33"));
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

        JLabel titleLbl = new JLabel(I18n.t("map.61"));
        titleLbl.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        titleLbl.setForeground(ACCENT_CYAN);
        bar.add(titleLbl);

        ButtonGroup group = new ButtonGroup();
        bar.add(createToolToggle(I18n.t("map.62"), MapStudioCanvas.Tool.SELECT, group, true));
        bar.add(createToolToggle(I18n.t("map.63"), MapStudioCanvas.Tool.MEX, group, false));

        JLabel mexMetalLbl = new JLabel("M:");
        mexMetalLbl.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        mexMetalLbl.setForeground(ACCENT_CYAN);
        bar.add(mexMetalLbl);
        mexPlacementMetalCombo.setFont(BarMapStudio.font(Font.BOLD, 11));
        mexPlacementMetalCombo.setBackground(CARD_INNER);
        mexPlacementMetalCombo.setForeground(TEXT_MAIN);
        mexPlacementMetalCombo.setSelectedIndex(2);
        mexPlacementMetalCombo.setPreferredSize(new Dimension(105, 25));
        mexPlacementMetalCombo.setToolTipText(I18n.t("map.64"));
        bar.add(mexPlacementMetalCombo);

        bar.add(createToolToggle(I18n.t("map.65"), MapStudioCanvas.Tool.GEO, group, false));
        bar.add(createToolToggle(I18n.t("map.66"), MapStudioCanvas.Tool.SPAWN, group, false));
        bar.add(createToolToggle(I18n.t("map.67"), MapStudioCanvas.Tool.RECLAIM_ROCK, group, false));
        bar.add(createToolToggle(I18n.t("map.68"), MapStudioCanvas.Tool.RECLAIM_TREE, group, false));
        bar.add(createToolToggle(I18n.t("map.69"), MapStudioCanvas.Tool.RECLAIM_WRECK, group, false));
        bar.add(createToolToggle(I18n.t("map.70"), MapStudioCanvas.Tool.ERASE, group, false));

        bar.add(Box.createHorizontalStrut(4));

        symToggle = new JToggleButton(I18n.t("map.71"), true);
        styleToggleButton(symToggle);
        symToggle.setForeground(ACCENT_CYAN);
        symToggle.setToolTipText(I18n.t("map.72"));
        symToggle.addActionListener(e -> {
            boolean active = symToggle.isSelected();
            symToggle.setForeground(active ? ACCENT_CYAN : TEXT_MUTED);
            canvas.setSymmetryEnabled(active);
        });
        bar.add(symToggle);

        axisToggleBtn.addActionListener(e -> toggleMirrorAxis());
        bar.add(axisToggleBtn);

        bar.add(Box.createHorizontalStrut(4));
        JButton undoBtn = createGhostButton(I18n.t("map.73"));
        undoBtn.setToolTipText(I18n.t("map.74"));
        undoBtn.addActionListener(e -> canvas.undo());
        bar.add(undoBtn);

        JButton redoBtn = createGhostButton(I18n.t("map.75"));
        redoBtn.setToolTipText(I18n.t("map.76"));
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
        slopeToggleBtn.setToolTipText(I18n.t("map.77"));
        slopeToggleBtn.addActionListener(e -> {
            canvas.setShowSlopeMap(slopeToggleBtn.isSelected());
            updateSlopeButtonState();
        });
        dock.add(slopeToggleBtn);

        // Vista 3D toggle subito dopo Slope Map
        styleToggleButton(view3DBtn);
        view3DBtn.setPreferredSize(new Dimension(120, 24));
        view3DBtn.setToolTipText(I18n.t("map.78"));
        view3DBtn.addActionListener(e -> setView3DVisible(view3DBtn.isSelected()));
        dock.add(view3DBtn);

        // Opacity Label & Slider
        JLabel opacityLbl = new JLabel(I18n.t("map.79"));
        opacityLbl.setFont(BarMapStudio.fontCond(Font.BOLD, 10));
        opacityLbl.setForeground(TEXT_MUTED);
        dock.add(opacityLbl);

        slopeOpacitySlider.setPreferredSize(new Dimension(65, 20));
        slopeOpacitySlider.setOpaque(false);
        slopeOpacitySlider.setToolTipText(I18n.t("map.80"));
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
        zoomOut.setToolTipText(I18n.t("map.81"));
        zoomOut.addActionListener(e -> canvas.zoomOut());
        dock.add(zoomOut);

        // Zoom In [+]
        JButton zoomIn = createGhostButton("+");
        zoomIn.setFont(BarMapStudio.font(Font.BOLD, 13));
        zoomIn.setPreferredSize(new Dimension(28, 24));
        zoomIn.setToolTipText(I18n.t("map.82"));
        zoomIn.addActionListener(e -> canvas.zoomIn());
        dock.add(zoomIn);

        // 1:1 Scala
        JButton zoomReset = createGhostButton(I18n.t("map.83"));
        zoomReset.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        zoomReset.setPreferredSize(new Dimension(76, 24));
        zoomReset.setToolTipText(I18n.t("map.84"));
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
        side.setBorder(new EmptyBorder(4, 12, 12, 12));
        // seconda scheda: tutto cio' che riguarda il rilievo
        ScrollableTrackWidthPanel reliefSide = new ScrollableTrackWidthPanel();
        reliefSide.setLayout(new BoxLayout(reliefSide, BoxLayout.Y_AXIS));
        reliefSide.setBackground(CARD_BG);
        reliefSide.setBorder(new EmptyBorder(4, 12, 12, 12));

        JLabel title = new JLabel(I18n.t("map.85"));
        title.setFont(BarMapStudio.fontCond(Font.BOLD, 13));
        title.setForeground(ACCENT_CYAN);
        title.setAlignmentX(0f);
        side.add(title);
        side.add(Box.createVerticalStrut(10));

        side.add(createFieldRow(I18n.t("map.86"), sizeCombo));
        side.add(Box.createVerticalStrut(6));
        // a ogni avvio stile, bioma e seed sono scelti a caso: niente combinazione fissa di partenza
        styleCombo.setSelectedIndex(new Random().nextInt(TERRAIN_STYLES.length));
        styleCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                Component c = super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof String name) {
                    String label = name;
                    // "X_LAST" accanto a "X" e' la sua variante: lo si dice in chiaro invece di "Last"
                    if (name.endsWith("_LAST") && java.util.Arrays.asList(TERRAIN_STYLES).contains(name.substring(0, name.length() - 5))) {
                        label = name.substring(0, name.length() - 5) + " (" + I18n.t("map.style.variant").toUpperCase(Locale.ROOT) + ")";
                    }
                    setText(smallCaps(label, getFont().getSize()));
                }
                return c;
            }
        });
        side.add(createFieldRow(I18n.t("map.87"), styleCombo));
        side.add(Box.createVerticalStrut(6));
        // i biomi BAR mostrano la descrizione tradotta; il valore resta il nome usato dal generatore
        biomeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                Object shown = value;
                if (value instanceof String name && name.startsWith("BAR: ")) {
                    shown = name + " (" + I18n.t("barbiome." + name.substring(5).toLowerCase(Locale.ROOT).replace(' ', '_')) + ")";
                }
                Component c = super.getListCellRendererComponent(list, shown, index, selected, focus);
                if (shown instanceof String label) {
                    setText(smallCaps(label, getFont().getSize()));
                }
                return c;
            }
        });
        biomeCombo.setSelectedIndex(new Random().nextInt(BIOMES.length));
        side.add(createFieldRow(I18n.t("map.88"), biomeCombo));
        side.add(Box.createVerticalStrut(6));
        side.add(createFieldRow(I18n.t("map.89"), symmetryCombo));
        side.add(Box.createVerticalStrut(6));
        playersPerTeamSpinner.setToolTipText(I18n.t("players.tooltip"));
        side.add(createFieldRow(I18n.t("players.perTeam"), playersPerTeamSpinner));
        side.add(Box.createVerticalStrut(4));

        JButton sideAxisBtn = createGhostButton(I18n.t("map.90"));
        sideAxisBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        sideAxisBtn.setAlignmentX(0f);
        sideAxisBtn.setToolTipText(I18n.t("map.91"));
        sideAxisBtn.addActionListener(e -> cycleSymmetryMode());
        side.add(sideAxisBtn);
        side.add(Box.createVerticalStrut(6));

        // Seed with Roll
        JLabel seedLbl = new JLabel(I18n.t("map.92"));
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

        JLabel heightCardTitle = new JLabel(I18n.t("map.93"));
        heightCardTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        heightCardTitle.setForeground(ACCENT_AMBER);
        heightCardTitle.setAlignmentX(0f);
        heightCard.add(heightCardTitle);
        heightCard.add(Box.createVerticalStrut(10));

        heightCard.add(createParamRow(I18n.t("map.94"), maxHeightSpinner, "m"));
        heightCard.add(Box.createVerticalStrut(2));
        heightCard.add(bindLiveSlider(maxHeightSlider, maxHeightSpinner));
        heightCard.add(Box.createVerticalStrut(6));
        heightCard.add(createParamRow(I18n.t("map.95"), waterLevelSpinner, "m"));
        heightCard.add(Box.createVerticalStrut(2));
        heightCard.add(bindLiveSlider(waterLevelSlider, waterLevelSpinner));
        liveRegenTimer.setRepeats(false);
        heightCard.add(Box.createVerticalStrut(10));

        btnRegenGround.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnRegenGround.setAlignmentX(0f);
        btnRegenGround.setToolTipText(I18n.t("map.96"));
        heightCard.add(btnRegenGround);
        heightCard.add(Box.createVerticalStrut(6));

        btnSmoothSlopes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        btnSmoothSlopes.setAlignmentX(0f);
        btnSmoothSlopes.setToolTipText(I18n.t("map.97"));
        heightCard.add(btnSmoothSlopes);
        heightCard.add(Box.createVerticalStrut(6));

        btnLoadHeightmap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        btnLoadHeightmap.setAlignmentX(0f);
        btnLoadHeightmap.setToolTipText(I18n.t("map.98"));
        heightCard.add(btnLoadHeightmap);
        heightCard.add(Box.createVerticalStrut(4));
        JButton btnImportBarMap = createGhostButton(I18n.t("import.button"));
        btnImportBarMap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        btnImportBarMap.setAlignmentX(0f);
        btnImportBarMap.setToolTipText(I18n.t("import.tooltip"));
        btnImportBarMap.addActionListener(e -> doImportBarMap());
        heightCard.add(btnImportBarMap);
        heightCard.add(Box.createVerticalStrut(6));

        btnLoadTexture.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        btnLoadTexture.setAlignmentX(0f);
        btnLoadTexture.setToolTipText(I18n.t("map.99"));
        heightCard.add(btnLoadTexture);
        heightCard.add(Box.createVerticalStrut(6));

        JButton btnOpenTexStudio = createGhostButton(I18n.t("map.100"));
        btnOpenTexStudio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        btnOpenTexStudio.setAlignmentX(0f);
        btnOpenTexStudio.setToolTipText(I18n.t("map.101"));
        btnOpenTexStudio.addActionListener(e -> {
            if (openTextureStudioCallback != null) openTextureStudioCallback.run();
        });
        heightCard.add(btnOpenTexStudio);

        reliefSide.add(heightCard);
        reliefSide.add(Box.createVerticalStrut(12));
        reliefSide.add(buildReliefShapingCard());
        reliefSide.add(Box.createVerticalStrut(12));

        // Pulsante Specifiche & Requisiti Asset posizionato sopra alla Slope Map Info
        JButton btnLeftSpecs = createPrimaryButton(I18n.t("map.102"), ACCENT_CYAN);
        btnLeftSpecs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btnLeftSpecs.setAlignmentX(0f);
        btnLeftSpecs.setToolTipText(I18n.t("map.103"));
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
        JLabel tip = new JLabel(I18n.t("map.104") +
            I18n.t("map.105") +
            I18n.t("map.106") +
            I18n.t("map.107") +
            I18n.t("map.108") +
            I18n.t("map.109"));
        tip.setFont(BarMapStudio.font(Font.PLAIN, 11));
        tip.setForeground(TEXT_MUTED);
        infoBox.add(tip, BorderLayout.CENTER);
        infoBox.setAlignmentX(0f);
        side.add(infoBox);

        side.add(Box.createVerticalStrut(12));

        // Due schede come nella barra di destra: Generatore | Rilievo
        JPanel leftContainer = new JPanel(new BorderLayout(0, 0));
        leftContainer.setBackground(CARD_BG);
        JPanel tabBar = new JPanel(new GridLayout(1, 2, 4, 0));
        tabBar.setBackground(CARD_BG);
        tabBar.setBorder(new EmptyBorder(8, 10, 6, 10));
        JToggleButton tabGeneratorBtn = new JToggleButton(I18n.t("lefttab.generator"), true);
        JToggleButton tabReliefBtn = new JToggleButton(I18n.t("lefttab.relief"), false);
        styleSidebarTabButton(tabGeneratorBtn);
        styleSidebarTabButton(tabReliefBtn);
        tabGeneratorBtn.setForeground(ACCENT_CYAN);
        tabReliefBtn.setForeground(TEXT_MUTED);
        ButtonGroup leftTabGroup = new ButtonGroup();
        leftTabGroup.add(tabGeneratorBtn);
        leftTabGroup.add(tabReliefBtn);
        tabBar.add(tabGeneratorBtn);
        tabBar.add(tabReliefBtn);

        CardLayout leftCardsLayout = new CardLayout();
        JPanel leftCardsPanel = new JPanel(leftCardsLayout);
        leftCardsPanel.setOpaque(false);
        leftCardsPanel.add(wrapSidebarScroll(side), "GENERATOR");
        leftCardsPanel.add(wrapSidebarScroll(reliefSide), "RELIEF");
        tabGeneratorBtn.addActionListener(e -> {
            leftCardsLayout.show(leftCardsPanel, "GENERATOR");
            tabGeneratorBtn.setForeground(ACCENT_CYAN);
            tabReliefBtn.setForeground(TEXT_MUTED);
        });
        tabReliefBtn.addActionListener(e -> {
            leftCardsLayout.show(leftCardsPanel, "RELIEF");
            tabReliefBtn.setForeground(ACCENT_CYAN);
            tabGeneratorBtn.setForeground(TEXT_MUTED);
        });
        leftContainer.add(tabBar, BorderLayout.NORTH);
        leftContainer.add(leftCardsPanel, BorderLayout.CENTER);
        return leftContainer;
    }

    private JScrollPane wrapSidebarScroll(JComponent content) {
        ScrollableTrackWidthPanel container = new ScrollableTrackWidthPanel(new BorderLayout());
        container.setBackground(CARD_BG);
        container.add(content, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(container);
        scroll.setBackground(CARD_BG);
        scroll.getViewport().setBackground(CARD_BG);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        return scroll;
    }

    /** Scheda "3. Modellazione": cursori che rimodellano il terreno in tempo reale partendo da quello originale. */
    private JPanel buildReliefShapingCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_INNER);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        card.setAlignmentX(0f);

        JLabel cardTitle = new JLabel(I18n.t("relief.title"));
        cardTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        cardTitle.setForeground(ACCENT_AMBER);
        cardTitle.setAlignmentX(0f);
        card.add(cardTitle);
        card.add(Box.createVerticalStrut(4));
        JLabel hint = new JLabel("<html><div style='width:210px'>" + I18n.t("relief.hint") + "</div></html>");
        hint.setFont(BarMapStudio.font(Font.PLAIN, 10));
        hint.setForeground(TEXT_MUTED);
        hint.setAlignmentX(0f);
        card.add(hint);
        card.add(Box.createVerticalStrut(8));

        card.add(createReliefSlider("relief.profile", -100, 100, value -> reliefParams.profile = value));
        card.add(createReliefSlider("relief.smooth", 0, 10, value -> reliefParams.smooth = value));
        card.add(createReliefSlider("relief.terraces", 0, 12, value -> reliefParams.terraces = value == 1 ? 2 : value));
        card.add(createReliefSlider("relief.erosion", 0, 100, value -> reliefParams.erosion = value));
        card.add(createReliefSlider("relief.detail", 0, 100, value -> reliefParams.detail = value));
        card.add(createReliefSlider("relief.border", -100, 100, value -> reliefParams.border = value));

        JButton resetBtn = createGhostButton(I18n.t("relief.reset"));
        resetBtn.setAlignmentX(0f);
        resetBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        resetBtn.setToolTipText(I18n.t("relief.reset.tooltip"));
        resetBtn.addActionListener(e -> {
            resetReliefSliders();
            if (canvas.getMap() != null) {
                liveRegenTimer.restart();
            }
        });
        card.add(Box.createVerticalStrut(4));
        card.add(resetBtn);
        return card;
    }

    private JPanel createReliefSlider(String key, int min, int max, java.util.function.IntConsumer onChange) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(0f);
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.setAlignmentX(0f);
        head.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        JLabel label = new JLabel(I18n.t(key));
        label.setFont(BarMapStudio.font(Font.PLAIN, 12));
        label.setForeground(TEXT_MAIN);
        JLabel valueLabel = new JLabel("0");
        valueLabel.setFont(BarMapStudio.font(Font.BOLD, 12));
        valueLabel.setForeground(ACCENT_CYAN);
        head.add(label, BorderLayout.WEST);
        head.add(valueLabel, BorderLayout.EAST);
        JSlider slider = new JSlider(min, max, 0);
        slider.setOpaque(false);
        slider.setAlignmentX(0f);
        slider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        slider.setToolTipText(I18n.t(key + ".tooltip"));
        slider.addChangeListener(e -> {
            valueLabel.setText(String.valueOf(slider.getValue()));
            onChange.accept(slider.getValue());
            if (!suppressLiveRegen && canvas.getMap() != null) {
                liveRegenTimer.restart();
            }
        });
        reliefSliderResets.add(() -> slider.setValue(0));
        row.add(head);
        row.add(slider);
        row.add(Box.createVerticalStrut(6));
        return row;
    }

    /** Riporta a zero tutti i cursori di modellazione senza far partire la rigenerazione. */
    private void resetReliefSliders() {
        suppressLiveRegen = true;
        try {
            for (Runnable reset : reliefSliderResets) {
                reset.run();
            }
        } finally {
            suppressLiveRegen = false;
        }
    }

    /** Il terreno e' cambiato alla radice (nuova mappa, import, heightmap caricata, levigatura): nuova base, cursori a zero. */
    private void captureReliefBase(SCMap map) {
        reliefBase = (map != null && map.getHeightmap() != null) ? ReliefShaper.copy(map.getHeightmap()) : null;
        resetReliefSliders();
        if (reliefBase != null) {
            // il campo "quota massima" deve descrivere il terreno com'e' adesso: cosi' il primo movimento
            // di un cursore rigenera lo stesso terreno invece di stirarlo fino al valore di default
            java.awt.image.Raster raster = reliefBase.getRaster();
            float scale = map.getHeightMapScale();
            float lowest = Float.MAX_VALUE;
            float highest = -Float.MAX_VALUE;
            for (int y = 0; y < raster.getHeight(); y++) {
                for (int x = 0; x < raster.getWidth(); x++) {
                    float value = raster.getSample(x, y, 0) * scale;
                    lowest = Math.min(lowest, value);
                    highest = Math.max(highest, value);
                }
            }
            float water = map.getWaterElevation();
            setQuietly(waterLevelSpinner, (double) Math.max(-100f, Math.min(800f, Math.round(water))));
            float neutral = highest - lowest + Math.min(lowest, ((Number) waterLevelSpinner.getValue()).floatValue());
            setQuietly(maxHeightSpinner, (double) Math.max(20f, Math.min(1500f, Math.round(neutral))));
        }
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

        tabMappaBtn = new JToggleButton(I18n.t("map.110"), true);
        tabOggettiBtn = new JToggleButton(I18n.t("map.111") + ReadyObjectsManager.getInstance().getReadyObjects().size() + ")", false);

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
        JLabel nameLbl = new JLabel(I18n.t("map.112"));
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

        JLabel mexMetalCardTitle = new JLabel(I18n.t("map.113"));
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
        JLabel assistTitle = new JLabel(I18n.t("map.114"));
        assistTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        assistTitle.setForeground(TEXT_MAIN);
        assistTitle.setAlignmentX(0f);
        side.add(assistTitle);
        side.add(Box.createVerticalStrut(6));

        JButton spawnRolesBtn = createGhostButton(I18n.t("roles.button"));
        spawnRolesBtn.setAlignmentX(0f);
        spawnRolesBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        spawnRolesBtn.setToolTipText(I18n.t("roles.tooltip"));
        spawnRolesBtn.addActionListener(e -> showSpawnRolesDialog());
        side.add(spawnRolesBtn);
        side.add(Box.createVerticalStrut(4));

        JButton autoMexBtn = createGhostButton(I18n.t("map.115"));
        autoMexBtn.setAlignmentX(0f);
        autoMexBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        autoMexBtn.setToolTipText(I18n.t("map.116"));
        autoMexBtn.addActionListener(e -> canvas.autoPlaceBaseMexes(3, ((Number) baseMetalSpinner.getValue()).floatValue()));
        side.add(autoMexBtn);
        side.add(Box.createVerticalStrut(4));

        JButton autoContestedBtn = createGhostButton(I18n.t("map.117"));
        autoContestedBtn.setAlignmentX(0f);
        autoContestedBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        autoContestedBtn.setToolTipText(I18n.t("map.118"));
        autoContestedBtn.addActionListener(e -> canvas.autoPlaceContestedMexes(6, ((Number) maxMetalSpinner.getValue()).floatValue()));
        side.add(autoContestedBtn);
        side.add(Box.createVerticalStrut(4));

        JButton autoReclaimBtn = createGhostButton(I18n.t("map.119"));
        autoReclaimBtn.setAlignmentX(0f);
        autoReclaimBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        autoReclaimBtn.setToolTipText(I18n.t("map.120"));
        autoReclaimBtn.addActionListener(e -> canvas.autoSeedReclaim(reclaimSlider.getValue() / 10f));
        side.add(autoReclaimBtn);
        side.add(Box.createVerticalStrut(4));

        JPanel clearRow = new JPanel(new GridLayout(1, 2, 6, 0));
        clearRow.setOpaque(false);
        JButton clrMex = createGhostButton(I18n.t("map.121"));
        clrMex.addActionListener(e -> canvas.clearMexes());
        JButton clrRec = createGhostButton(I18n.t("map.122"));
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
        ecoCard.add(new JLabel(I18n.t("map.123")));
        ecoCard.add(baseMetalSpinner);
        ecoCard.add(new JLabel(I18n.t("map.124")));
        ecoCard.add(maxMetalSpinner);
        ecoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        ecoCard.setAlignmentX(0f);
        side.add(ecoCard);
        side.add(Box.createVerticalStrut(8));

        side.add(createFieldRow(I18n.t("map.125"), reclaimSlider));
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
                    tabOggettiBtn.setText(I18n.t("map.111") + list.size() + ")");
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

        JButton mapOptionsBtn = createGhostButton(I18n.t("mapopt.button"));
        mapOptionsBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        mapOptionsBtn.setAlignmentX(0f);
        mapOptionsBtn.addActionListener(e -> showMapOptionsDialog());
        exportCard.add(mapOptionsBtn);
        exportCard.add(Box.createVerticalStrut(6));

        genFinalMapBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        genFinalMapBtn.setAlignmentX(0f);
        exportCard.add(genFinalMapBtn);
        exportCard.add(Box.createVerticalStrut(6));

        genAndPlayBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        genAndPlayBtn.setAlignmentX(0f);
        exportCard.add(genAndPlayBtn);
        exportCard.add(Box.createVerticalStrut(6));

        hostMatchBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        hostMatchBtn.setAlignmentX(0f);
        exportCard.add(hostMatchBtn);
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
            mexCountLabel.setText(I18n.t("map.126") + m.getMexes().size());
            geoCountLabel.setText(I18n.t("map.127") + m.getHydros().size());
            spawnCountLabel.setText(I18n.t("map.128") + m.getSpawns().size());
            reclaimCountLabel.setText(I18n.t("map.129") + m.getProps().size());
        } else {
            mexCountLabel.setText(I18n.t("map.41"));
            geoCountLabel.setText(I18n.t("map.42"));
            spawnCountLabel.setText(I18n.t("map.43"));
            reclaimCountLabel.setText(I18n.t("map.44"));
        }
        if (readyTray != null && canvas != null) {
            int placed = canvas.getCustomEntities() != null ? canvas.getCustomEntities().size() : 0;
            readyTray.updatePlacedCount(placed);
        }
        if (tabOggettiBtn != null) {
            int readyCount = ReadyObjectsManager.getInstance().getReadyObjects().size();
            tabOggettiBtn.setText(I18n.t("map.111") + readyCount + ")");
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

        final boolean synthStyle = TerrainSynth.supports(styleName);
        TerrainStyle terrainStyle = synthStyle ? null : TerrainStyle.valueOf(styleName);

        setStatus(I18n.t("map.130") + width + "x" + height + I18n.t("map.131"));
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        genGroundBtn.setEnabled(false);

        long finalSeed = seed;
        // due squadre: un punto di partenza per ogni giocatore
        final int spawnCount = 2 * ((Number) playersPerTeamSpinner.getValue()).intValue();
        new Thread(() -> {
            try {
                final GroundGenerator.GroundResult res;
                final TerrainSynth.Result synth;
                if (synthStyle) {
                    // stile nuovo: heightmap e basi disegnate da TerrainSynth, poi il normale percorso "da heightmap"
                    synth = TerrainSynth.generate(styleName, width, height, spawnCount, symmetry, finalSeed);
                    res = GroundGenerator.generateGroundFromHeightmap(null, synth.heightmap, width, height, synth.maxUnits, synth.waterUnits,
                        biomeName, synth.symmetry, spawnCount, 2, finalSeed);
                    res.map().getSpawns().clear();
                    for (int i = 0; i < synth.spawns.size(); i++) {
                        float[] sp = synth.spawns.get(i);
                        res.map().getSpawns().add(new com.faforever.neroxis.map.Spawn("spawn_" + i,
                            new com.faforever.neroxis.util.vector.Vector2(sp[0], sp[1]),
                            new com.faforever.neroxis.util.vector.Vector2(0, 0), (int) sp[2]));
                    }
                } else {
                    synth = null;
                    res = GroundGenerator.generateGroundOnly(
                        width,
                        height,
                        terrainStyle,
                        biomeName,
                        symmetry,
                        spawnCount,
                        2,
                        finalSeed
                    );
                }

                SwingUtilities.invokeLater(() -> {
                    canvas.setMapAndGround(res.map(), res.preview(), res.slopeOverlay(), res.symmetrySettings());
                    captureReliefBase(res.map());
                    refreshScene3D();
                    if (res.symmetrySettings() != null) {
                        selectSymmetryInCombo(res.symmetrySettings().terrainSymmetry());
                        updateAxisToggleLabel(res.symmetrySettings().terrainSymmetry());
                    }
                    float wElev = res.map().getWaterElevation();
                    setQuietly(waterLevelSpinner, (double) Math.round(wElev));
                    if (synth != null) {
                        setQuietly(maxHeightSpinner, (double) Math.min(1500f, synth.maxUnits));
                        setQuietly(waterLevelSpinner, (double) Math.max(-100f, Math.min(800f, synth.waterUnits)));
                        updateCounters();
                    }
                    setStatus(I18n.t("map.132"));
                    progressBar.setVisible(false);
                    genGroundBtn.setEnabled(true);
                    if (onMapChangedCallback != null) {
                        onMapChangedCallback.run();
                    }
                });
            } catch (Throwable ex) {
                ex.printStackTrace();
                writeGroundErrorLog(ex, width, height, styleName, biomeName, symmetry, finalSeed);
                SwingUtilities.invokeLater(() -> {
                    setStatus(I18n.t("map.133") + ex.getMessage() + I18n.t("map.134"));
                    progressBar.setVisible(false);
                    genGroundBtn.setEnabled(true);
                });
            }
        }, "ground-gen").start();
    }

    /** Scrive stack trace completo + contesto in barmap-error.log accanto al launcher per diagnosi. */
    private void writeGroundErrorLog(Throwable ex, int width, int height, String style,
                                     String biome, Symmetry symmetry, long seed) {
        try {
            java.nio.file.Path log = homeFolder != null
                ? homeFolder.resolve("barmap-error.log")
                : AppPaths.userDir().resolve("barmap-error.log");
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new java.io.PrintWriter(sw));
            String entry = "[" + new java.util.Date() + "] GENERA TERRENO "
                + width + "x" + height + " style=" + style + " biome=" + biome
                + " sym=" + symmetry + " seed=" + seed + "\n" + sw + "\n";
            java.nio.file.Files.writeString(log, entry,
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception ignored) {}
    }

    /** Lega cursore e campo numerico; ogni modifica fatta dall'utente rigenera il terreno poco dopo. */
    private JSlider bindLiveSlider(JSlider slider, JSpinner spinner) {
        slider.setOpaque(false);
        slider.setAlignmentX(0f);
        slider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        slider.addChangeListener(e -> {
            if (((Number) spinner.getValue()).intValue() != slider.getValue()) {
                spinner.setValue((double) slider.getValue());
            }
        });
        spinner.addChangeListener(e -> {
            int value = (int) Math.round(((Number) spinner.getValue()).doubleValue());
            if (slider.getValue() != value) {
                boolean previous = suppressLiveRegen;
                slider.setValue(value);
                suppressLiveRegen = previous;
            }
            if (!suppressLiveRegen && canvas.getMap() != null) {
                liveRegenTimer.restart();
            }
        });
        return slider;
    }

    /** Imposta un campo del rilievo da codice, senza far partire la rigenerazione in tempo reale. */
    private void setQuietly(JSpinner spinner, double value) {
        suppressLiveRegen = true;
        try {
            spinner.setValue(value);
        } finally {
            suppressLiveRegen = false;
        }
    }

    private static GroundGenerator.GroundResult generateFromHeightmapCall(BufferedImage shapedSource, SCMap existing, BufferedImage customHm,
            int width, int height, float maxHeight, float waterLevel, String biomeName, Symmetry symmetry, long seed) throws Exception {
        GroundGenerator.setHeightSourceOverride(shapedSource);
        try {
            return GroundGenerator.generateGroundFromHeightmap(existing, customHm, width, height, maxHeight, waterLevel, biomeName, symmetry, 2, 2, seed);
        } finally {
            GroundGenerator.setHeightSourceOverride(null);
        }
    }

    private void regenFinished() {
        regenRunning = false;
        if (regenPending) {
            regenPending = false;
            liveRegenTimer.restart();
        }
    }

    private void doRegenerateFromHeightmap(BufferedImage customHm) {
        doRegenerateFromHeightmap(customHm, null);
    }

    private void doRegenerateFromHeightmap(BufferedImage customHm, Runnable onDone) {
        if (regenRunning) {
            // una rigenerazione e' gia' in corso: riparte da sola con gli ultimi valori quando finisce
            regenPending = true;
            return;
        }
        SCMap existing = canvas.getMap();
        if (existing == null && customHm == null) {
            JOptionPane.showMessageDialog(this,
                I18n.t("map.135"),
                I18n.t("map.136"), JOptionPane.WARNING_MESSAGE);
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

        setStatus(I18n.t("map.137") + (int) maxHeight + I18n.t("map.138") + (int) waterLevel + "m)...");
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        btnRegenGround.setEnabled(false);
        btnLoadHeightmap.setEnabled(false);
        btnLoadTexture.setEnabled(false);
        btnSmoothSlopes.setEnabled(false);
        regenRunning = true;

        long finalSeed = seed;
        if (existing != null && customHm == null && reliefBase == null) {
            reliefBase = ReliefShaper.copy(existing.getHeightmap());
        }
        // cursori di modellazione: si applicano sempre alla base, mai al terreno gia' modificato
        final BufferedImage shapedSource = (existing != null && customHm == null && reliefBase != null
            && reliefBase.getWidth() == existing.getHeightmap().getWidth() && reliefBase.getHeight() == existing.getHeightmap().getHeight())
            ? ReliefShaper.apply(reliefBase, reliefParams) : null;
        new Thread(() -> {
            try {
                GroundGenerator.GroundResult res = generateFromHeightmapCall(shapedSource, existing, customHm, width, height,
                    maxHeight, waterLevel, biomeName, symmetry, finalSeed);
                if (customHm != null) {
                    SwingUtilities.invokeLater(() -> captureReliefBase(res.map()));
                }

                // quello che era legato alla mappa precedente segue la mappa rigenerata
                BufferedImage shownPreview = res.preview();
                if (existing != null && customHm == null) {
                    SCMap fresh = res.map();
                    com.faforever.neroxis.exporter.bar.SpawnRoles.carryOver(existing, fresh);
                    com.faforever.neroxis.exporter.bar.ZoneMaterials.attach(fresh, com.faforever.neroxis.exporter.bar.ZoneMaterials.get(existing));
                    com.faforever.neroxis.exporter.bar.BarBiomes.BarBiome oldBiome = com.faforever.neroxis.exporter.bar.BarBiomes.get(existing);
                    if (oldBiome != null && com.faforever.neroxis.exporter.bar.BarBiomes.get(fresh) == null) {
                        com.faforever.neroxis.exporter.bar.BarBiomes.attach(fresh, oldBiome);
                    }
                    if (existing.getCustomTexture() != null) {
                        fresh.setCustomTexture(existing.getCustomTexture());
                        if (existing.getPreview() != null) {
                            fresh.setPreview(existing.getPreview());
                            shownPreview = existing.getPreview();
                        }
                    }
                }
                BufferedImage finalShownPreview = shownPreview;
                SwingUtilities.invokeLater(() -> {
                    canvas.setMapAndGround(res.map(), finalShownPreview, res.slopeOverlay(), res.symmetrySettings());
                    refreshScene3D();
                    if (res.symmetrySettings() != null) {
                        selectSymmetryInCombo(res.symmetrySettings().terrainSymmetry());
                        updateAxisToggleLabel(res.symmetrySettings().terrainSymmetry());
                    }
                    regenFinished();
                    setStatus(I18n.t("map.139"));
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
                    regenFinished();
                    setStatus(I18n.t("map.140") + ex.getMessage());
                    progressBar.setVisible(false);
                    btnRegenGround.setEnabled(true);
                    btnLoadHeightmap.setEnabled(true);
                    btnLoadTexture.setEnabled(true);
                    btnSmoothSlopes.setEnabled(true);
                    JOptionPane.showMessageDialog(this,
                        I18n.t("map.141") + ex.getMessage(),
                        "Errore", JOptionPane.ERROR_MESSAGE);
                });
            }
        }, "heightmap-ground-regen").start();
    }

    private void doLoadHeightmapFile() {
        JFileChooser chooser = new JFileChooser();
        FileChoosers.detailed(chooser);
        chooser.setDialogTitle(I18n.t("map.142"));
        chooser.setFileFilter(new FileNameExtensionFilter(I18n.t("map.143"), "png", "raw", "bmp"));
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
                        JOptionPane.showMessageDialog(this, I18n.t("map.144"), I18n.t("map.145"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                setStatus(I18n.t("map.146") + f.getName() + " (" + loaded.getWidth() + "x" + loaded.getHeight() + ")");
                doRegenerateFromHeightmap(loaded);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, I18n.t("map.147") + ex.getMessage(), I18n.t("map.148"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Carica nell'editor una mappa BAR esistente (.sd7/.sdz): terreno, texture, spawn, metallo, geyser, luce e acqua. */
    private void doImportBarMap() {
        JFileChooser chooser = new JFileChooser();
        java.nio.file.Path data = AppPaths.barData();
        if (data != null && Files.isDirectory(data.resolve("maps"))) {
            chooser.setCurrentDirectory(data.resolve("maps").toFile());
        }
        FileChoosers.detailed(chooser);
        chooser.setDialogTitle(I18n.t("import.title"));
        chooser.setFileFilter(new FileNameExtensionFilter(I18n.t("import.filter"), "sd7", "sdz"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        importBarMap(chooser.getSelectedFile());
    }

    /** Carica nell'editor la mappa BAR indicata (usato anche dalla libreria delle mappe). */
    public void importBarMap(File file) {
        setStatus(I18n.t("import.running", file.getName()));
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        new Thread(() -> {
            try {
                BarMapImporter.Imported imp = BarMapImporter.load(file);
                // 1 unita' di quota dell'editor = 11.2 elmi; l'acqua di BAR sta a quota 0
                float maxUnits = Math.max(20f, (imp.maxHeight - imp.minHeight) / 11.2f);
                float waterUnits = imp.minHeight < 0 ? (-imp.minHeight) / 11.2f : -1000f;
                GroundGenerator.GroundResult res = GroundGenerator.generateGroundFromHeightmap(
                    null, imp.heightmap, imp.cellsW, imp.cellsH, maxUnits, waterUnits,
                    "DESERT", null, 2, 2, new Random().nextLong());
                SCMap m = res.map();
                m.getSpawns().clear();
                for (int i = 0; i < imp.spawns.size(); i++) {
                    float[] sp = imp.spawns.get(i);
                    // nelle mappe BAR gli spawn si alternano tra le due squadre
                    m.getSpawns().add(new com.faforever.neroxis.map.Spawn("spawn_" + i,
                        new com.faforever.neroxis.util.vector.Vector2(sp[0] / 16f, sp[1] / 16f),
                        new com.faforever.neroxis.util.vector.Vector2(0, 0), i % 2));
                }
                m.getMexes().clear();
                for (float[] mex : imp.mexes) {
                    m.getMexes().add(new com.faforever.neroxis.map.Marker("mex_val_2.0_" + java.util.UUID.randomUUID().toString().substring(0, 8),
                        new com.faforever.neroxis.util.vector.Vector2(mex[0] / 16f, mex[1] / 16f)));
                }
                m.getHydros().clear();
                for (float[] geo : imp.geos) {
                    m.getHydros().add(new com.faforever.neroxis.map.Marker("hydro_" + java.util.UUID.randomUUID().toString().substring(0, 8),
                        new com.faforever.neroxis.util.vector.Vector2(geo[0] / 16f, geo[1] / 16f)));
                }
                BufferedImage preview = res.preview();
                if (imp.texture != null) {
                    int previewH = (int) Math.round(512.0 * imp.cellsH / imp.cellsW);
                    BufferedImage scaled = new BufferedImage(512, previewH, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = scaled.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g.drawImage(imp.texture, 0, 0, 512, previewH, null);
                    g.dispose();
                    m.setCustomTexture(imp.texture);
                    m.setPreview(scaled);
                    preview = scaled;
                }
                com.faforever.neroxis.exporter.bar.ZoneMaterials.attach(m, null);
                com.faforever.neroxis.exporter.bar.BarBiomes.attach(m, imp.biome);
                BufferedImage finalPreview = preview;
                SwingUtilities.invokeLater(() -> {
                    canvas.setMapAndGround(m, finalPreview, res.slopeOverlay(), res.symmetrySettings());
                    captureReliefBase(m);
                    refreshScene3D();
                    setQuietly(maxHeightSpinner, (double) Math.min(1500f, maxUnits));
                    setQuietly(waterLevelSpinner, (double) Math.max(-100f, Math.min(800f, waterUnits)));
                    updateCounters();
                    setStatus(I18n.t("import.done", imp.name, imp.cellsW / 32, imp.cellsH / 32, imp.spawns.size(), imp.mexes.size(), imp.geos.size()));
                    progressBar.setVisible(false);
                    if (onMapChangedCallback != null) {
                        onMapChangedCallback.run();
                    }
                });
            } catch (Throwable ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    progressBar.setVisible(false);
                    setStatus(I18n.t("import.error", ex.getMessage()));
                    JOptionPane.showMessageDialog(this, I18n.t("import.error", ex.getMessage()), I18n.t("import.title"), JOptionPane.ERROR_MESSAGE);
                });
            }
        }, "bar-map-import").start();
    }

    private void doLoadCustomTextureFile() {
        JFileChooser chooser = new JFileChooser();
        FileChoosers.detailed(chooser);
        File defaultDir = AppPaths.userFolder("custom_assets/textures").toFile();
        if (defaultDir.exists()) chooser.setCurrentDirectory(defaultDir);
        chooser.setDialogTitle(I18n.t("map.149"));
        chooser.setFileFilter(new FileNameExtensionFilter(I18n.t("map.150"), "png", "jpg", "jpeg", "bmp"));
        int ret = chooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                BufferedImage loaded = ImageIO.read(f);
                if (loaded == null) {
                    JOptionPane.showMessageDialog(this, I18n.t("map.151"), I18n.t("map.145"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                SCMap currentMap = canvas.getMap();
                if (currentMap == null) {
                    JOptionPane.showMessageDialog(this, I18n.t("map.152"), I18n.t("map.153"), JOptionPane.WARNING_MESSAGE);
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
                // immagine unica: niente materiali di zona
                com.faforever.neroxis.exporter.bar.ZoneMaterials.attach(currentMap, null);
                currentMap.setPreview(scaled);
                if (canvas != null) {
                    canvas.setGroundPreview(scaled);
                } else {
                    canvas.repaint();
                }
                setStatus(I18n.t("map.154") + f.getName() + " (" + loaded.getWidth() + "x" + loaded.getHeight() + " px)");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, I18n.t("map.155") + ex.getMessage(), I18n.t("map.148"), JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, I18n.t("map.156"), I18n.t("map.153"), JOptionPane.WARNING_MESSAGE);
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
            // materiali di zona: l'export compone il terreno a piena risoluzione da maschere e tile
            com.faforever.neroxis.exporter.bar.ZoneMaterials.attach(currentMap, com.faforever.neroxis.exporter.bar.ZoneMaterials.takePending());
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
            setStatus(I18n.t("map.157") + name + " (" + diffuseMap.getWidth() + "x" + diffuseMap.getHeight() + " px)");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, I18n.t("map.158") + ex.getMessage(), I18n.t("map.159"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doSmoothSlopes() {
        SCMap m = canvas.getMap();
        if (m == null) {
            JOptionPane.showMessageDialog(this, I18n.t("map.160"), I18n.t("map.153"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        setStatus(I18n.t("map.161"));
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        btnSmoothSlopes.setEnabled(false);

        new Thread(() -> {
            try {
                GroundGenerator.smoothSlopes(m, 0.40f, 2);
                float waterElev = m.getWaterElevation();
                BufferedImage newSlope = GroundGenerator.generateSlopeOverlayImage(m, waterElev);
                SwingUtilities.invokeLater(() -> {
                    captureReliefBase(m);
                    canvas.setSlopeOverlay(newSlope);
                    canvas.setShowSlopeMap(true);
                    updateSlopeButtonState();
                    setStatus(I18n.t("map.162"));
                    progressBar.setVisible(false);
                    btnSmoothSlopes.setEnabled(true);
                });
            } catch (Throwable ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    setStatus(I18n.t("map.163") + ex.getMessage());
                    progressBar.setVisible(false);
                    btnSmoothSlopes.setEnabled(true);
                });
            }
        }, "smooth-slopes").start();
    }

    private void doGenerateFinalMap(boolean launchSkirmish) {
        SCMap m = canvas.getMap();
        if (m == null) {
            JOptionPane.showMessageDialog(this, I18n.t("map.164"), I18n.t("map.165"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        Path mapsDir = barDataFolder != null ? barDataFolder.resolve("maps") : null;
        if (mapsDir == null || !Files.isDirectory(mapsDir)) {
            JOptionPane.showMessageDialog(this, I18n.t("map.166") + mapsDir, I18n.t("map.167"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        String rawName = mapNameField.getText().trim();
        if (rawName.isBlank()) {
            rawName = "Map_" + System.currentTimeMillis();
        }
        final String finalMapName = rawName;

        genFinalMapBtn.setEnabled(false);
        genAndPlayBtn.setEnabled(false);

        BarExportOptions options = currentExportOptions(m, finalMapName);

        // Niente piu' messaggi nel pannello: progresso, esito e invito alla donazione stanno in una finestra al centro
        ExportDialog dialog = new ExportDialog(SwingUtilities.getWindowAncestor(this));
        final Path[] exported = new Path[1];
        dialog.log(I18n.t("export.log.start", finalMapName));
        new Thread(() -> {
            try {
                Path skirmishScript = barDataFolder.resolve("_neroxis_skirmish.txt");
                final Path archive = exportToBar(mapsDir, m, options, skirmishScript, null, java.util.List.of(), null, dialog);
                SwingUtilities.invokeLater(() -> dialog.finish(true, I18n.t("export.log.done", archive.getFileName().toString()), () -> {
                    exported[0] = archive;
                    if (onMapCreated != null) {
                        onMapCreated.accept(archive);
                    }
                    if (launchSkirmish) {
                        dialog.log(I18n.t("export.log.play"));
                    }
                }));
            } catch (Throwable ex) {
                ex.printStackTrace();
                StringWriter swx = new StringWriter();
                ex.printStackTrace(new java.io.PrintWriter(swx));
                final String entry = "[" + new java.util.Date() + "] EXPORT MAPPA " + finalMapName
                    + " entita=" + m.getCustomEntities().size() + "\n" + swx + "\n";
                try {
                    java.nio.file.Path log = homeFolder != null
                        ? homeFolder.resolve("barmap-error.log")
                        : AppPaths.userDir().resolve("barmap-error.log");
                    java.nio.file.Files.writeString(log, entry,
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                } catch (Exception ignored) {}
                String msg;
                if (ex instanceof java.nio.file.AccessDeniedException ade) {
                    msg = I18n.t("map.170") + ade.getFile() + I18n.t("map.171");
                } else {
                    msg = ex.getMessage();
                    if (msg == null || msg.isBlank()) msg = ex.toString();
                    if (msg.length() > 420) msg = msg.substring(0, 420) + "...";
                }
                final String shown = I18n.t("export.log.error", msg) + I18n.t("map.134");
                SwingUtilities.invokeLater(() -> dialog.finish(false, shown, null));
            }
        }, "final-map-export").start();
        dialog.setVisible(true); // modale: torna quando l'utente preme Conferma
        genFinalMapBtn.setEnabled(true);
        genAndPlayBtn.setEnabled(true);
        // "genera e gioca": la partita parte solo dopo la conferma
        if (launchSkirmish && exported[0] != null && onPlaySkirmish != null) {
            onPlaySkirmish.accept(exported[0]);
        }
    }

    public void setScriptLauncher(java.util.function.BiConsumer<Path, String> scriptLauncher) {
        this.scriptLauncher = scriptLauncher;
    }

    /** Le opzioni di export lette dai controlli del pannello; porta anche nella mappa gli oggetti dell'editor. */
    private BarExportOptions currentExportOptions(SCMap m, String mapName) {
        float baseMetal = ((Number) baseMetalSpinner.getValue()).floatValue();
        float maxMetal = ((Number) maxMetalSpinner.getValue()).floatValue();
        float reclaimVal = reclaimSlider.getValue() / 10.0f;
        int numTeams = Math.max(2, (int) m.getSpawns().stream().mapToInt(com.faforever.neroxis.map.Spawn::getTeamID).distinct().count());

        m.getCustomEntities().clear();
        m.getCustomEntities().addAll(canvas.getCustomEntities());

        return new BarExportOptions(
            2,
            0.7f,
            baseMetal,
            Math.min(baseMetal, 2.0f),
            maxMetal,
            false,
            numTeams,
            mapName,
            "1.0",
            "BAR Map",
            reclaimVal,
            null,
            com.faforever.neroxis.exporter.bar.MapExtraOptions.get().minWind,
            com.faforever.neroxis.exporter.bar.MapExtraOptions.get().maxWind,
            com.faforever.neroxis.exporter.bar.MapExtraOptions.get().tidal
        );
    }

    /**
     * Esporta la mappa nella cartella mappe di BAR. Se BAR e' installato in una cartella protetta, la mappa viene
     * preparata nella cartella dell'utente e copiata con una sola richiesta di amministratore, senza riavviare
     * il programma.
     *
     * @param hostScript script della partita ospitata, o null per la sola partita contro l'IA
     */
    private Path exportToBar(Path mapsDir, SCMap m, BarExportOptions options, Path skirmishScript, Path hostScript,
                             java.util.List<String> players, String password, ExportDialog dialog) throws Exception {
        try {
            return BarMapExporter.exportMap(mapsDir, m, options, skirmishScript, hostScript, players, false, password);
        } catch (java.nio.file.AccessDeniedException denied) {
            SwingUtilities.invokeLater(() -> dialog.log(I18n.t("export.log.protected")));
            Path staging = ProtectedCopy.stagingFolder();
            Path stagedSkirmish = staging.resolve(skirmishScript.getFileName());
            Path stagedHost = hostScript == null ? null : staging.resolve(hostScript.getFileName());
            Path staged = BarMapExporter.exportMap(staging, m, options, stagedSkirmish, stagedHost, players, false, password);
            Path target = mapsDir.resolve(staged.getFileName());
            java.util.Map<Path, Path> files = ProtectedCopy.pairs(staged, target, stagedSkirmish, skirmishScript);
            if (stagedHost != null) {
                files.put(stagedHost, hostScript);
            }
            if (!ProtectedCopy.copy(files, true)) {
                throw new java.io.IOException(I18n.t("export.protected.refused", staged.toString(), mapsDir.toString()));
            }
            for (Path file : files.keySet()) {
                try {
                    Files.deleteIfExists(file);
                } catch (Exception ignored) {
                }
            }
            return target;
        }
    }

    /**
     * Partita con amici che hanno solo BAR: export della mappa, caricamento nello spazio cloud dell'utente,
     * un file di ingresso per ogni amico e avvio del gioco come host.
     */
    private void doHostMatch() {
        SCMap m = canvas.getMap();
        if (m == null) {
            JOptionPane.showMessageDialog(this, I18n.t("map.164"), I18n.t("map.165"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        Path mapsDir = barDataFolder != null ? barDataFolder.resolve("maps") : null;
        if (mapsDir == null || !Files.isDirectory(mapsDir)) {
            JOptionPane.showMessageDialog(this, I18n.t("map.166") + mapsDir, I18n.t("map.167"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        Path engine = AppPaths.newestEngine();
        if (engine == null) {
            JOptionPane.showMessageDialog(this, I18n.t("host.noengine"), I18n.t("map.167"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        HostMatchDialog.Request request = HostMatchDialog.show(SwingUtilities.getWindowAncestor(this), m.getSpawnCount());
        if (request == null) {
            return;
        }
        CloudStorage.Settings cloud = CloudStorage.load();
        String rawName = mapNameField.getText().trim();
        final String finalMapName = rawName.isBlank() ? "Map_" + System.currentTimeMillis() : rawName;
        BarExportOptions options = currentExportOptions(m, finalMapName);
        String engineName = engine.getFileName().toString();

        genFinalMapBtn.setEnabled(false);
        genAndPlayBtn.setEnabled(false);
        hostMatchBtn.setEnabled(false);
        ExportDialog dialog = new ExportDialog(SwingUtilities.getWindowAncestor(this));
        final Path[] ready = new Path[2]; // script dell'host, cartella dei file di ingresso
        dialog.log(I18n.t("export.log.start", finalMapName));
        new Thread(() -> {
            try {
                Path skirmishScript = barDataFolder.resolve("_neroxis_skirmish.txt");
                Path hostScript = barDataFolder.resolve("_neroxis_host.txt");
                Path archive = exportToBar(mapsDir, m, options, skirmishScript, hostScript, request.players(), request.password(), dialog);
                String file = archive.getFileName().toString();
                String base = file.substring(0, file.lastIndexOf('.'));
                String sha = CloudStorage.sha256(archive);
                // l'impronta nel nome: una mappa rifatta con lo stesso nome non viene confusa con quella vecchia
                String key = "maps/" + base + "_" + sha.substring(0, 8) + file.substring(file.lastIndexOf('.'));
                String megabytes = String.format(Locale.ROOT, "%.1f", Files.size(archive) / 1048576.0);
                SwingUtilities.invokeLater(() -> dialog.log(I18n.t("host.log.upload", megabytes)));
                String url = CloudStorage.upload(cloud, archive, key, sha);
                SwingUtilities.invokeLater(() -> dialog.log(I18n.t("host.log.uploaded", url)));
                Path folder = AppPaths.userFolder("matches").resolve(base);
                JoinFileWriter.write(folder, new JoinFileWriter.Match(finalMapName, url, file, sha, engineName, request.address(),
                    BarMapExporter.HOST_PORT, request.password()), request.friends());
                SwingUtilities.invokeLater(() -> dialog.finish(true, I18n.t("host.log.files", folder.toString()), () -> {
                    ready[0] = hostScript;
                    ready[1] = folder;
                    if (onMapCreated != null) {
                        onMapCreated.accept(archive);
                    }
                    dialog.log(I18n.t("host.log.ready"));
                }));
            } catch (Throwable ex) {
                ex.printStackTrace();
                if (ex instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                String msg = ex.getMessage() == null || ex.getMessage().isBlank() ? ex.toString() : ex.getMessage();
                if (ex instanceof java.nio.file.AccessDeniedException ade) {
                    msg = I18n.t("map.170") + ade.getFile();
                }
                final String shown = I18n.t("export.log.error", msg.length() > 420 ? msg.substring(0, 420) + "..." : msg);
                SwingUtilities.invokeLater(() -> dialog.finish(false, shown, null));
            }
        }, "host-match").start();
        dialog.setVisible(true); // modale: torna quando l'utente preme Conferma
        genFinalMapBtn.setEnabled(true);
        genAndPlayBtn.setEnabled(true);
        hostMatchBtn.setEnabled(true);
        if (ready[0] != null) {
            try {
                java.awt.Desktop.getDesktop().open(ready[1].toFile());
            } catch (Exception ignored) {
            }
            if (scriptLauncher != null) {
                scriptLauncher.accept(ready[0], engineName.startsWith("recoil_") ? engineName.substring("recoil_".length()) : null);
            }
        }
    }

    private void setStatus(String text) {
        if (text == null) text = "";
        statusLabel.setText("<html><body style='width: 250px;'>" + text.replace("<", "&lt;").replace(">", "&gt;") + "</body></html>");
        statusLabel.setToolTipText(text);
    }

    /** Ruolo consigliato di ogni punto di partenza (i cerchi con la scritta che BAR mostra prima della partita). */
    private void showSpawnRolesDialog() {
        SCMap m = canvas.getMap();
        if (m == null || m.getSpawns().isEmpty()) {
            JOptionPane.showMessageDialog(this, I18n.t("roles.none"), I18n.t("roles.title"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        java.util.List<com.faforever.neroxis.map.Spawn> spawns = m.getSpawns();
        boolean anyRole = spawns.stream().anyMatch(s -> com.faforever.neroxis.exporter.bar.SpawnRoles.get(m, s) != null);
        if (!anyRole) {
            com.faforever.neroxis.exporter.bar.SpawnRoles.assignAllFront(m);
        }
        String[] choices = new String[com.faforever.neroxis.exporter.bar.SpawnRoles.ROLES.length];
        System.arraycopy(com.faforever.neroxis.exporter.bar.SpawnRoles.ROLES, 0, choices, 0, choices.length);
        java.util.List<JComboBox<String>> combos = new java.util.ArrayList<>();
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 4, 2, 10);
        c.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < spawns.size(); i++) {
            com.faforever.neroxis.map.Spawn s = spawns.get(i);
            c.gridy = i;
            c.gridx = 0;
            form.add(new JLabel("P" + (i + 1)), c);
            c.gridx = 1;
            form.add(new JLabel(I18n.t("roles.team", s.getTeamID() + 1)), c);
            c.gridx = 2;
            JComboBox<String> combo = new JComboBox<>(choices);
            String role = com.faforever.neroxis.exporter.bar.SpawnRoles.get(m, s);
            combo.setSelectedItem(role != null ? role : "front");
            combos.add(combo);
            form.add(combo, c);
        }
        JButton auto = new JButton(I18n.t("roles.auto"));
        auto.addActionListener(e -> {
            com.faforever.neroxis.exporter.bar.SpawnRoles.autoAssign(m);
            for (int i = 0; i < spawns.size(); i++) {
                combos.get(i).setSelectedItem(com.faforever.neroxis.exporter.bar.SpawnRoles.get(m, spawns.get(i)));
            }
        });
        JLabel note = new JLabel("<html><div style='width:320px'>" + I18n.t("roles.note") + "</div></html>");
        note.setForeground(TEXT_MUTED);
        JPanel content = new JPanel(new BorderLayout(0, 8));
        JScrollPane scroll = new JScrollPane(form);
        scroll.setPreferredSize(new Dimension(360, Math.min(420, 40 + spawns.size() * 30)));
        content.add(auto, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);
        content.add(note, BorderLayout.SOUTH);
        int answer = JOptionPane.showConfirmDialog(this, content, I18n.t("roles.title"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer == JOptionPane.OK_OPTION) {
            for (int i = 0; i < spawns.size(); i++) {
                com.faforever.neroxis.exporter.bar.SpawnRoles.set(m, spawns.get(i), (String) combos.get(i).getSelectedItem());
            }
        }
        canvas.repaint();
    }

    /**
     * Finestra mostrata mentre la mappa viene esportata: invito a sostenere il progetto (link e QR PayPal presi da
     * donation.properties / donation_qrcode.png nella cartella del programma), barra verde che dura almeno
     * 5 secondi e un piccolo log con l'esito ("mappa creata", errore...).
     */
    private final class ExportDialog extends JDialog {
        private static final int MIN_MILLIS = 5000;
        private final JProgressBar bar = new JProgressBar(0, 1000);
        private final JTextArea logArea = new JTextArea(4, 40);
        private final JButton closeBtn = createPrimaryButton(I18n.t("export.close"), ACCENT_GREEN);
        private final long startTime = System.currentTimeMillis();
        private boolean workDone = false;
        private boolean success = false;
        private Runnable afterSuccess;
        private final javax.swing.Timer ticker;

        ExportDialog(Window owner) {
            super(owner, I18n.t("export.title"), ModalityType.APPLICATION_MODAL);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

            JPanel root = new JPanel();
            root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
            root.setBorder(new EmptyBorder(18, 22, 16, 22));

            JLabel headline = new JLabel(I18n.t("export.headline"), createCoffeeIcon(26), SwingConstants.CENTER);
            headline.setHorizontalTextPosition(SwingConstants.LEFT);
            headline.setIconTextGap(10);
            headline.setFont(BarMapStudio.fontCond(Font.BOLD, 18));
            headline.setAlignmentX(0.5f);
            root.add(headline);
            root.add(Box.createVerticalStrut(6));
            JLabel sub = new JLabel("<html><div style='width:360px; text-align:center'>" + I18n.t("export.sub") + "</div></html>");
            sub.setAlignmentX(0.5f);
            root.add(sub);
            root.add(Box.createVerticalStrut(10));

            String url = donationUrl();
            BufferedImage qr = null;
            try {
                java.io.File qrFile = AppPaths.appDir().resolve("donation_qrcode.png").toFile();
                qr = qrFile.isFile() ? ImageIO.read(qrFile) : null;
            } catch (Exception ignored) {
            }
            if (qr != null) {
                int height = 200;
                int width = Math.max(1, Math.round(qr.getWidth() * (height / (float) qr.getHeight())));
                JLabel qrLabel = new JLabel(new ImageIcon(qr.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
                qrLabel.setAlignmentX(0.5f);
                root.add(qrLabel);
                JLabel qrCaption = new JLabel(I18n.t("donate.qr"));
                qrCaption.setForeground(TEXT_MUTED);
                qrCaption.setAlignmentX(0.5f);
                root.add(qrCaption);
                root.add(Box.createVerticalStrut(8));
            }
            if (url != null) {
                JButton donate = createPrimaryButton(I18n.t("donate.button"), ACCENT_CYAN);
                donate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                donate.setAlignmentX(0.5f);
                donate.addActionListener(e -> {
                    try {
                        java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
                    } catch (Exception ex) {
                        log(I18n.t("donate.error") + " " + url);
                    }
                });
                root.add(donate);
                root.add(Box.createVerticalStrut(12));
            }

            bar.setForeground(new Color(34, 197, 94));
            bar.setPreferredSize(new Dimension(420, 14));
            bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));
            bar.setAlignmentX(0.5f);
            root.add(bar);
            root.add(Box.createVerticalStrut(8));

            logArea.setEditable(false);
            logArea.setLineWrap(true);
            logArea.setWrapStyleWord(true);
            logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
            JScrollPane logScroll = new JScrollPane(logArea);
            logScroll.setAlignmentX(0.5f);
            root.add(logScroll);
            root.add(Box.createVerticalStrut(10));

            closeBtn.setEnabled(false);
            closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            closeBtn.setAlignmentX(0.5f);
            closeBtn.addActionListener(e -> dispose());
            root.add(closeBtn);

            setContentPane(root);
            pack();
            setLocationRelativeTo(owner);

            ticker = new javax.swing.Timer(40, e -> tick());
            ticker.start();
        }

        void log(String line) {
            logArea.append((logArea.getDocument().getLength() > 0 ? "\n" : "") + line);
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }

        /** L'export e' finito: la barra arriva in fondo quando sono passati anche i 5 secondi minimi. */
        void finish(boolean ok, String line, Runnable after) {
            log(line);
            workDone = true;
            success = ok;
            afterSuccess = after;
            if (!ok) {
                bar.setForeground(new Color(239, 68, 68));
            }
        }

        private void tick() {
            long elapsed = System.currentTimeMillis() - startTime;
            double timeShare = Math.min(1.0, elapsed / (double) MIN_MILLIS);
            // finche' il lavoro non e' finito la barra si ferma poco prima della fine
            bar.setValue((int) Math.round((workDone ? timeShare : Math.min(timeShare, 0.92)) * 1000));
            if (workDone && elapsed >= MIN_MILLIS) {
                ticker.stop();
                bar.setValue(1000);
                closeBtn.setEnabled(true);
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                if (success && afterSuccess != null) {
                    try {
                        afterSuccess.run();
                    } catch (RuntimeException ex) {
                        log(I18n.t("export.log.error", String.valueOf(ex.getMessage())));
                    }
                }
                // la finestra non si chiude mai da sola: resta finche' l'utente non preme Conferma
                closeBtn.requestFocusInWindow();
            }
        }
    }

    /** Link PayPal da donation.properties (chiave paypal_url), oppure null se manca. */
    private static String donationUrl() {
        try {
            java.io.File file = AppPaths.appDir().resolve("donation.properties").toFile();
            if (file.isFile()) {
                java.util.Properties props = new java.util.Properties();
                try (java.io.Reader reader = new java.io.InputStreamReader(new java.io.FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8)) {
                    props.load(reader);
                }
                String url = props.getProperty("paypal_url", "").trim();
                return url.startsWith("https://") ? url : null;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /** Tazzina di caffe' disegnata in Java2D (niente emoji: il font dell'app non le ha). */
    private static Icon createCoffeeIcon(int size) {
        return new Icon() {
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                float u = size / 24f;
                g2.setColor(new Color(245, 158, 11));
                g2.setStroke(new BasicStroke(1.8f * u, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // vapore
                for (int i = 0; i < 3; i++) {
                    java.awt.geom.Path2D.Float steam = new java.awt.geom.Path2D.Float();
                    float sx = (6.5f + i * 3.5f) * u;
                    steam.moveTo(sx, 7.5f * u);
                    steam.curveTo(sx - 1.6f * u, 5.5f * u, sx + 1.6f * u, 4.0f * u, sx, 2.0f * u);
                    g2.draw(steam);
                }
                // tazza e manico
                g2.setColor(new Color(241, 245, 249));
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(3.5f * u, 10f * u, 13f * u, 10.5f * u, 5f * u, 5f * u));
                g2.setStroke(new BasicStroke(2.0f * u));
                g2.draw(new java.awt.geom.Arc2D.Float(14f * u, 11.5f * u, 6.5f * u, 6.5f * u, -90, 180, java.awt.geom.Arc2D.OPEN));
                // caffe'
                g2.setColor(new Color(120, 72, 40));
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(5f * u, 11f * u, 10f * u, 2.6f * u, 2f * u, 2f * u));
                g2.dispose();
            }
        };
    }

    /** Opzioni scritte nella mappa esportata: nebbia di guerra, risorse iniziali, gravita', acqua, vento... */
    private void showMapOptionsDialog() {
        com.faforever.neroxis.exporter.bar.MapExtraOptions o = com.faforever.neroxis.exporter.bar.MapExtraOptions.get();
        JCheckBox noFog = new JCheckBox(I18n.t("mapopt.noFog"), o.noFogOfWar);
        JCheckBox notDeformable = new JCheckBox(I18n.t("mapopt.notDeformable"), o.notDeformable);
        JCheckBox voidWater = new JCheckBox(I18n.t("mapopt.voidWater"), o.voidWater);
        JCheckBox night = new JCheckBox(I18n.t("mapopt.night"), o.night);
        JComboBox<String> lavaMode = new JComboBox<>(new String[]{I18n.t("mapopt.lava.none"), I18n.t("mapopt.lava.lava"), I18n.t("mapopt.lava.acid")});
        lavaMode.setSelectedIndex(Math.max(0, Math.min(2, o.lavaMode)));
        JSpinner lavaLevel = new JSpinner(new SpinnerNumberModel(o.lavaLevel, -500, 2000, 5));
        JSpinner lavaDamage = new JSpinner(new SpinnerNumberModel(o.lavaDamage, 0, 10000, 10));
        JSpinner lavaTide = new JSpinner(new SpinnerNumberModel(o.lavaTide, 0, 500, 5));
        JSpinner lavaPeriod = new JSpinner(new SpinnerNumberModel(o.lavaPeriod, 10, 3600, 10));
        JSpinner startMetal = new JSpinner(new SpinnerNumberModel(o.startMetalBonus, 0, 100000, 500));
        JSpinner startEnergy = new JSpinner(new SpinnerNumberModel(o.startEnergyBonus, 0, 100000, 500));
        JSpinner gravity = new JSpinner(new SpinnerNumberModel(o.gravity, 20, 300, 10));
        JSpinner waterDamage = new JSpinner(new SpinnerNumberModel(o.waterDamage, 0, 10000, 10));
        JSpinner minWind = new JSpinner(new SpinnerNumberModel(o.minWind, 0, 50, 1));
        JSpinner maxWind = new JSpinner(new SpinnerNumberModel(o.maxWind, 0, 50, 1));
        JSpinner tidal = new JSpinner(new SpinnerNumberModel(o.tidal, 0, 50, 1));
        JSpinner extractorRadius = new JSpinner(new SpinnerNumberModel(o.extractorRadius, 40, 400, 10));
        JComboBox<String> startMode = new JComboBox<>(new String[]{I18n.t("mapopt.start.fixed"), I18n.t("mapopt.start.zones")});
        startMode.setSelectedIndex(o.startZones ? 1 : 0);
        startMode.setToolTipText(I18n.t("mapopt.start.tip"));
        JSpinner zoneMargin = new JSpinner(new SpinnerNumberModel(o.startZoneMargin, 0, 40, 1));
        zoneMargin.setToolTipText(I18n.t("mapopt.start.margin.tip"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 4, 3, 8);
        c.anchor = GridBagConstraints.WEST;
        c.gridy = 0;
        for (JCheckBox box : new JCheckBox[]{noFog, notDeformable, voidWater, night}) {
            c.gridx = 0;
            c.gridwidth = 2;
            form.add(box, c);
            c.gridy++;
        }
        Object[][] rows = {
            {"mapopt.start.mode", startMode}, {"mapopt.start.margin", zoneMargin},
            {"mapopt.startMetal", startMetal}, {"mapopt.startEnergy", startEnergy}, {"mapopt.gravity", gravity},
            {"mapopt.waterDamage", waterDamage}, {"mapopt.minWind", minWind}, {"mapopt.maxWind", maxWind},
            {"mapopt.tidal", tidal}, {"mapopt.extractorRadius", extractorRadius},
            {"mapopt.lava.mode", lavaMode}, {"mapopt.lava.level", lavaLevel}, {"mapopt.lava.damage", lavaDamage},
            {"mapopt.lava.tide", lavaTide}, {"mapopt.lava.period", lavaPeriod}};
        c.gridwidth = 1;
        for (Object[] row : rows) {
            c.gridx = 0;
            form.add(new JLabel(I18n.t((String) row[0])), c);
            c.gridx = 1;
            JComponent field = (JComponent) row[1];
            field.setPreferredSize(new Dimension(field == startMode ? 250 : field instanceof JComboBox ? 130 : 90, 24));
            form.add(field, c);
            c.gridy++;
        }
        c.gridx = 0;
        c.gridwidth = 2;
        JLabel note = new JLabel("<html><div style='width:360px'>" + I18n.t("mapopt.note") + "</div></html>");
        note.setForeground(TEXT_MUTED);
        form.add(note, c);

        int answer = JOptionPane.showConfirmDialog(this, form, I18n.t("mapopt.title"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) {
            return;
        }
        o.noFogOfWar = noFog.isSelected();
        o.notDeformable = notDeformable.isSelected();
        o.voidWater = voidWater.isSelected();
        o.night = night.isSelected();
        o.lavaMode = lavaMode.getSelectedIndex();
        o.lavaLevel = ((Number) lavaLevel.getValue()).intValue();
        o.lavaDamage = ((Number) lavaDamage.getValue()).intValue();
        o.lavaTide = ((Number) lavaTide.getValue()).intValue();
        o.lavaPeriod = ((Number) lavaPeriod.getValue()).intValue();
        o.startMetalBonus = ((Number) startMetal.getValue()).intValue();
        o.startEnergyBonus = ((Number) startEnergy.getValue()).intValue();
        o.gravity = ((Number) gravity.getValue()).intValue();
        o.waterDamage = ((Number) waterDamage.getValue()).intValue();
        o.minWind = ((Number) minWind.getValue()).intValue();
        o.maxWind = Math.max(o.minWind, ((Number) maxWind.getValue()).intValue());
        o.tidal = ((Number) tidal.getValue()).intValue();
        o.extractorRadius = ((Number) extractorRadius.getValue()).intValue();
        o.startZones = startMode.getSelectedIndex() == 1;
        o.startZoneMargin = ((Number) zoneMargin.getValue()).intValue();
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

    static JButton createPrimaryButton(String text, Color color) {
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

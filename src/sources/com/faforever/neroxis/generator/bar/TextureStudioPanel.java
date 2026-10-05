package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.exporter.bar.ZoneMaterials;
import com.faforever.neroxis.map.SCMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SplittableRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * TextureStudioPanel: Modulo intuitivo e compatto (240px) per la composizione multi-materiale,
 * con Maschere di Zona (File di Default - Metodo B), tutti gli Sliders delle Regole Terreno (Metodo A),
 * e controlli di dettaglio materiale PBR.
 */
public class TextureStudioPanel extends JPanel {

    public interface TerrainApplyCallback {
        void applyToMap(BufferedImage diffuseMap, BufferedImage splatLow, BufferedImage splatHigh, BufferedImage customHeightmap, String name);
    }

    private static final Color BG_DARK = new Color(15, 17, 23);
    private static final Color CARD_BG = new Color(22, 27, 38);
    private static final Color CARD_INNER = new Color(17, 20, 29);
    private static final Color BORDER_COLOR = new Color(38, 45, 62);
    private static final Color ACCENT_CYAN = new Color(0, 229, 255);
    private static final Color ACCENT_AMBER = new Color(255, 179, 0);
    private static final Color ACCENT_GREEN = new Color(50, 205, 50);
    private static final Color TEXT_MAIN = new Color(220, 225, 235);
    private static final Color TEXT_MUTED = new Color(130, 140, 160);

    public enum ViewMode {
        COMPOSITE(I18n.t("tex.1")),
        MAPS_GROUND(I18n.t("tex.2")),
        SPLAT_RGBA(I18n.t("tex.3")),
        CHANNEL_R(I18n.t("tex.4")),
        CHANNEL_G(I18n.t("tex.5")),
        CHANNEL_B(I18n.t("tex.6")),
        CHANNEL_A(I18n.t("tex.7")),
        NORMAL_MAP_3D(I18n.t("tex.8")),
        SEAMLESS_TILING(I18n.t("tex.9"));

        public final String label;
        ViewMode(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    public enum MaterialPreset {
        GRASS_MEADOW(I18n.t("tex.10"), 5.0f, 4, 0.45f, 1.1f, 0.8f, new Color(80, 130, 50), new Color(55, 95, 35), new Color(38, 58, 24)),
        ROCK_CLIFF(I18n.t("tex.11"), 3.5f, 5, 0.55f, 1.4f, 1.8f, new Color(125, 115, 105), new Color(85, 78, 70), new Color(45, 40, 38)),
        SAND_DUNES(I18n.t("tex.12"), 2.8f, 3, 0.35f, 0.9f, 0.6f, new Color(210, 180, 130), new Color(185, 155, 105), new Color(145, 115, 75)),
        SNOW_GLACIER(I18n.t("tex.13"), 3.0f, 4, 0.40f, 1.2f, 0.9f, new Color(240, 245, 255), new Color(205, 220, 240), new Color(160, 185, 215)),
        CRACKED_MUD(I18n.t("tex.14"), 4.2f, 4, 0.60f, 1.6f, 1.5f, new Color(140, 110, 85), new Color(105, 80, 60), new Color(50, 35, 25)),
        VOLCANIC_BASALT(I18n.t("tex.15"), 4.0f, 5, 0.58f, 1.5f, 2.0f, new Color(55, 50, 52), new Color(38, 35, 38), new Color(18, 15, 18)),
        GRAVEL_PEBBLES(I18n.t("tex.16"), 6.0f, 5, 0.50f, 1.3f, 1.4f, new Color(135, 130, 125), new Color(100, 98, 95), new Color(60, 58, 55)),
        CRYSTALLINE_ALIEN(I18n.t("tex.17"), 3.8f, 4, 0.52f, 1.3f, 1.6f, new Color(50, 170, 190), new Color(30, 110, 140), new Color(20, 50, 80));

        public final String label;
        public final float defaultScale;
        public final int defaultOctaves;
        public final float defaultPersistence;
        public final float defaultContrast;
        public final float defaultBump;
        public final Color colorLight;
        public final Color colorMid;
        public final Color colorDark;

        MaterialPreset(String label, float defaultScale, int defaultOctaves, float defaultPersistence, float defaultContrast, float defaultBump, Color cLight, Color cMid, Color cDark) {
            this.label = label;
            this.defaultScale = defaultScale;
            this.defaultOctaves = defaultOctaves;
            this.defaultPersistence = defaultPersistence;
            this.defaultContrast = defaultContrast;
            this.defaultBump = defaultBump;
            this.colorLight = cLight;
            this.colorMid = cMid;
            this.colorDark = cDark;
        }

        @Override public String toString() { return label; }
    }

    /**
     * Struttura Dati per ciascuna delle 4 Zone
     */
    public static class ZoneConfig {
        public final int id;
        public final String title;
        public final Color indicatorColor;
        public boolean useFileMask = true; // DEFAULT: METODO B (Caricamento file maschera)
        public File maskFile = null;
        public BufferedImage loadedMask = null;
        public MaterialPreset preset;
        /** true quando l'utente ha scelto un materiale per questa zona: non segue piu' i colori di MAPS. */
        public boolean presetChanged = false;
        public File customTextureFile = null;
        public BufferedImage customTexture = null;
        /** Materiale esterno completo (colore + normal + rilievo), null = preset procedurale. */
        public ZoneMaterials.Material material = null;
        /** Lato di una ripetizione sul terreno, in elmi. */
        public float tileElmos = 256.0f;
        /** Lucentezza 0..1 scritta nella mappa speculare. */
        public float gloss = 0.05f;

        public ZoneConfig(int id, String title, Color col, MaterialPreset preset) {
            this.id = id;
            this.title = title;
            this.indicatorColor = col;
            this.preset = preset;
        }
    }

    private final Path homePath;
    private final Path barDataPath;
    private final TerrainApplyCallback applyCallback;
    private final ExecutorService genPool = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "texture-gen-worker");
        t.setDaemon(true);
        return t;
    });

    // Le 4 Zone
    private final ZoneConfig zone1 = new ZoneConfig(1, I18n.t("tex.18"), new Color(239, 68, 68), MaterialPreset.GRASS_MEADOW);
    private final ZoneConfig zone2 = new ZoneConfig(2, I18n.t("tex.19"), new Color(34, 197, 94), MaterialPreset.ROCK_CLIFF);
    private final ZoneConfig zone3 = new ZoneConfig(3, I18n.t("tex.20"), new Color(59, 130, 246), MaterialPreset.SAND_DUNES);
    private final ZoneConfig zone4 = new ZoneConfig(4, I18n.t("tex.21"), new Color(234, 179, 8), MaterialPreset.SNOW_GLACIER);

    // SLIDERS METODO A (Regole Automatiche da Heightmap e Pendenze)
    private float slopeThreshold = 0.38f;     // ~22 gradi
    private float slopeFalloff = 0.15f;
    private float waterElevation = 35.0f;
    /** false per le mappe asciutte: niente fondale ne' fascia costiera. */
    private boolean waterPresent = true;
    /** Elmi in gioco per ogni unita' di quota dell'heightmap (16 elmi per cella x scala verticale 0.7 dell'export). */
    private static final float ELMOS_PER_HEIGHT_UNIT = 11.2f;
    private float beachBand = 12.0f;
    private float peakAltitude = 0.75f;
    private float noisePerturbation = 0.30f;

    // SLIDERS DETTAGLIO MATERIALI PROCEDURALI PBR
    private float matScale = 3.5f;
    private int matOctaves = 5;
    private float matPersistence = 0.55f;
    private float matContrast = 1.3f;
    private float matBump = 1.4f;
    private Color matColorLight = new Color(130, 120, 110);
    private Color matColorMid = new Color(85, 78, 70);
    private Color matColorDark = new Color(45, 40, 38);

    // Parametri Globali
    private ViewMode currentViewMode = ViewMode.COMPOSITE;
    private int currentRes = 512;
    private long currentSeed = 133742L;
    private SCMap activeMap = null;
    private BufferedImage activeGround = null;
    private BufferedImage customLoadedHeightmap = null;
    private String customLoadedHeightmapName = null;

    // Buffer Immagini
    private BufferedImage renderedDiffuse;
    private BufferedImage renderedNormal;
    private BufferedImage renderedSplatRGBA;
    private BufferedImage renderedChannelR;
    private BufferedImage renderedChannelG;
    private BufferedImage renderedChannelB;
    private BufferedImage renderedChannelA;
    private volatile BufferedImage renderedSlopeOverlay;
    // Ultima composizione, passata all'export BAR con "Applica"
    private volatile float[][] lastMasks;
    private volatile int lastMaskW;
    private volatile int lastMaskH;
    private volatile BufferedImage[] lastTiles;

    // Componenti UI
    private JComboBox<String> resCombo;
    private JComboBox<ViewMode> viewModeCombo;
    private JLabel statusLabel;
    private JLabel lblMapStatus;
    private final JToggleButton slopeToggleBtn = new JToggleButton(I18n.t("tex.22"), false);
    private final JSlider slopeOpacitySlider = new JSlider(10, 100, 70);
    private TextureCanvas canvas;
    private final Timer debounceTimer = new Timer(50, e -> triggerAsyncGeneration());

    public TextureStudioPanel(Path homePath, Path barDataPath, TerrainApplyCallback applyCallback) {
        this.homePath = homePath;
        this.barDataPath = barDataPath;
        this.applyCallback = applyCallback;

        debounceTimer.setRepeats(false);
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // Header compatta
        add(buildHeaderToolbar(), BorderLayout.NORTH);

        // Sidebar compatta (260px) + Center Canvas con SplitPane fluido
        JComponent sidebar = buildCompactSidebar();
        JComponent centerCanvas = buildCenterCanvas();

        // Stesso schema della scheda MAPS: pannello a sinistra, tela al centro, pannello a destra
        JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerCanvas, rightSidebar);
        rightSplit.setDividerSize(6);
        rightSplit.setContinuousLayout(true);
        rightSplit.setBorder(null);
        rightSplit.setOpaque(false);
        rightSplit.setResizeWeight(1.0);
        rightSidebar.setMinimumSize(new Dimension(220, 100));
        rightSidebar.setPreferredSize(new Dimension(290, 100));

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, rightSplit);
        mainSplit.setDividerLocation(280);
        mainSplit.setDividerSize(6);
        mainSplit.setContinuousLayout(true);
        mainSplit.setBorder(null);
        mainSplit.setOpaque(false);
        sidebar.setMinimumSize(new Dimension(220, 100));
        centerCanvas.setMinimumSize(new Dimension(300, 100));

        add(mainSplit, BorderLayout.CENTER);
        add(buildBottomActionBar(), BorderLayout.SOUTH);

        setupHotkeys();

        // All'avvio NON generiamo nulla: stato pulito in attesa di azione utente
        setStatus(I18n.t("tex.23"), TEXT_MUTED);
    }

    public boolean hasCustomHeightmap() {
        return customLoadedHeightmap != null;
    }

    public void setActiveMap(SCMap map) {
        setActiveMap(map, null, null);
    }

    public void setActiveMap(SCMap map, BufferedImage currentGround) {
        setActiveMap(map, currentGround, null);
    }

    public void setActiveMap(SCMap map, BufferedImage currentGround, BufferedImage currentSlope) {
        boolean isNew = (this.activeMap != map) || (this.renderedDiffuse == null);
        this.activeMap = map;
        this.activeGround = currentGround;
        if (currentSlope != null) {
            this.renderedSlopeOverlay = currentSlope;
        } else if (map != null && map.getHeightmap() != null) {
            float wElev = (map.getBiome() != null && map.getBiome().waterSettings() != null) ? map.getBiome().waterSettings().elevation() : map.getWaterElevation();
            this.renderedSlopeOverlay = GroundGenerator.generateSlopeOverlayImage(map, wElev);
        }
        if (map != null) {
            this.customLoadedHeightmap = null;
            this.customLoadedHeightmapName = null;
            if (map.getBiome() != null && map.getBiome().waterSettings() != null) {
                this.waterElevation = map.getBiome().waterSettings().elevation();
                this.waterPresent = map.getBiome().waterSettings().waterPresent();
            } else {
                this.waterElevation = map.getWaterElevation();
                this.waterPresent = true;
            }
            if (lblMapStatus != null) {
                int w = map.getWidth() > 0 ? map.getWidth() : map.getSize();
                int h = map.getHeight() > 0 ? map.getHeight() : map.getSize();
                lblMapStatus.setText(I18n.t("tex.24") + w + "x" + h + "  ");
                lblMapStatus.setForeground(ACCENT_GREEN);
            }
            if (isNew || activeMap != null) {
                triggerAsyncGeneration();
            }
        } else {
            if (customLoadedHeightmap == null) {
                this.renderedDiffuse = null;
                this.renderedNormal = null;
                this.renderedSplatRGBA = null;
                this.renderedChannelR = null;
                this.renderedChannelG = null;
                this.renderedChannelB = null;
                this.renderedChannelA = null;
                this.renderedSlopeOverlay = null;
                if (lblMapStatus != null) {
                    lblMapStatus.setText(I18n.t("tex.25"));
                    lblMapStatus.setForeground(ACCENT_AMBER);
                }
                setStatus(I18n.t("tex.26"), TEXT_MUTED);
                if (canvas != null) canvas.repaint();
            }
        }
    }

    private void doLoadHeightmap() {
        JFileChooser chooser = new JFileChooser();
        FileChoosers.detailed(chooser);
        chooser.setDialogTitle(I18n.t("tex.27"));
        chooser.setFileFilter(new FileNameExtensionFilter(I18n.t("tex.28"), "png", "raw", "bmp"));
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
                        JOptionPane.showMessageDialog(this, I18n.t("tex.29"), I18n.t("tex.30"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                this.customLoadedHeightmap = loaded;
                this.customLoadedHeightmapName = f.getName();
                this.activeMap = null;
                this.activeGround = null;
                this.renderedSlopeOverlay = null;
                if (lblMapStatus != null) {
                    lblMapStatus.setText(I18n.t("tex.31") + f.getName() + " (" + loaded.getWidth() + "x" + loaded.getHeight() + ")  ");
                    lblMapStatus.setForeground(ACCENT_GREEN);
                }
                setStatus(I18n.t("tex.32") + f.getName() + " (" + loaded.getWidth() + "x" + loaded.getHeight() + ")", ACCENT_CYAN);
                triggerAsyncGeneration();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, I18n.t("tex.33") + ex.getMessage(), I18n.t("tex.34"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JComponent buildHeaderToolbar() {
        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setBackground(CARD_BG);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        bar.setPreferredSize(new Dimension(100, 42));

        // Sinistra: Titolo e badge mappa attiva
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        left.setOpaque(false);

        JLabel lblTitle = new JLabel(I18n.t("tex.35"));
        lblTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 14));
        lblTitle.setForeground(ACCENT_CYAN);
        left.add(lblTitle);

        lblMapStatus = new JLabel(I18n.t("tex.25"));
        lblMapStatus.setFont(BarMapStudio.fontCond(Font.BOLD, 10));
        lblMapStatus.setForeground(ACCENT_AMBER);
        lblMapStatus.setBackground(CARD_INNER);
        lblMapStatus.setOpaque(true);
        lblMapStatus.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        left.add(lblMapStatus);

        bar.add(left, BorderLayout.WEST);

        // Destra: Carica Heightmap, Risoluzione, Varia Rumore e Sync
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        right.setOpaque(false);

        JButton btnLoadHm = createGhostButton(I18n.t("tex.36"), createVectorFolderIcon(12, TEXT_MAIN));
        btnLoadHm.setToolTipText(I18n.t("tex.37"));
        btnLoadHm.addActionListener(e -> doLoadHeightmap());
        right.add(btnLoadHm);

        JLabel resLbl = new JLabel(I18n.t("tex.38"));
        resLbl.setFont(BarMapStudio.font(Font.BOLD, 11));
        resLbl.setForeground(TEXT_MUTED);
        right.add(resLbl);

        resCombo = new JComboBox<>(new String[]{"512x512", "1024x1024", "2048x2048"});
        resCombo.setFont(BarMapStudio.font(Font.BOLD, 11));
        resCombo.setBackground(CARD_INNER);
        resCombo.setForeground(TEXT_MAIN);
        resCombo.setPreferredSize(new Dimension(100, 26));
        resCombo.setSelectedIndex(0);
        resCombo.addActionListener(e -> {
            int idx = resCombo.getSelectedIndex();
            currentRes = (idx == 0) ? 512 : (idx == 1) ? 1024 : 2048;
            triggerAsyncGeneration();
        });
        right.add(resCombo);

        JButton btnSeed = createGhostButton(I18n.t("tex.39"), createVectorDiceIcon(12, TEXT_MAIN));
        btnSeed.setToolTipText(I18n.t("tex.40"));
        btnSeed.addActionListener(e -> {
            currentSeed = new SplittableRandom().nextLong();
            triggerAsyncGeneration();
        });
        right.add(btnSeed);

        JButton btnSyncMap = createGhostButton(I18n.t("tex.41"));
        btnSyncMap.setToolTipText(I18n.t("tex.42"));
        btnSyncMap.addActionListener(e -> {
            // rilegge la mappa dall'editor: vale anche dopo aver caricato una heightmap esterna,
            // che altrimenti restava al posto della mappa
            if (syncWithEditor != null) {
                syncWithEditor.run();
            }
            if (activeMap != null) {
                if (activeMap.getBiome() != null && activeMap.getBiome().waterSettings() != null) {
                    waterElevation = activeMap.getBiome().waterSettings().elevation();
                    waterPresent = activeMap.getBiome().waterSettings().waterPresent();
                } else {
                    waterElevation = activeMap.getWaterElevation();
                    waterPresent = true;
                }
                if (lblMapStatus != null) {
                    int w = activeMap.getWidth() > 0 ? activeMap.getWidth() : activeMap.getSize();
                    int h = activeMap.getHeight() > 0 ? activeMap.getHeight() : activeMap.getSize();
                    lblMapStatus.setText(I18n.t("tex.24") + w + "x" + h + "  ");
                    lblMapStatus.setForeground(ACCENT_GREEN);
                }
                setStatus(I18n.t("tex.43") + activeMap.getName(), ACCENT_CYAN);
                triggerAsyncGeneration();
            } else {
                setStatus(I18n.t("tex.44"), ACCENT_AMBER);
            }
        });
        right.add(btnSyncMap);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    /**
     * Sidebar Compatta (240px-260px) con le 3 Sezioni Complete:
     * 1. MASCHERE DELLE 4 ZONE (Default Metodo B: File)
     * 2. REGOLE TERRENO & SLIDERS (Metodo A)
     * 3. PARAMETRI DETTAGLIO MATERIALI PBR
     */
    private static Component vstrut(int height) {
        JComponent c = (JComponent) Box.createVerticalStrut(height);
        c.setAlignmentX(0f);
        return c;
    }

    /**
     * Sidebar Compatta (240px-260px) con le 3 Sezioni Complete:
     * 1. MASCHERE DELLE 4 ZONE (Default Metodo B: File)
     * 2. REGOLE TERRENO & SLIDERS (Metodo A)
     * 3. PARAMETRI DETTAGLIO MATERIALI PBR
     */
    private JComponent buildCompactSidebar() {
        ScrollableTrackWidthPanel panel = newSidebarColumn();

        // --- SEZIONE 1: 4 ZONE & MASCHERE ---
        JLabel sec1Title = new JLabel(I18n.t("tex.45"));
        sec1Title.setAlignmentX(0f);
        sec1Title.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        sec1Title.setForeground(ACCENT_CYAN);
        panel.add(sec1Title);
        panel.add(vstrut(2));

        JLabel sec1Desc = new JLabel(I18n.t("tex.46"));
        sec1Desc.setAlignmentX(0f);
        sec1Desc.setFont(BarMapStudio.font(Font.PLAIN, 10));
        sec1Desc.setForeground(TEXT_MUTED);
        panel.add(sec1Desc);
        panel.add(vstrut(6));

        panel.add(buildZoneCard(zone1, I18n.t("tex.47"), I18n.t("tex.48")));
        panel.add(vstrut(6));
        panel.add(buildZoneCard(zone2, I18n.t("tex.49"), I18n.t("tex.50")));
        panel.add(vstrut(6));
        panel.add(buildZoneCard(zone3, I18n.t("tex.51"), I18n.t("tex.52")));
        panel.add(vstrut(6));
        panel.add(buildZoneCard(zone4, I18n.t("tex.53"), I18n.t("tex.54")));
        panel.add(vstrut(6));

        JButton btnMatSpecs = createGhostButton(I18n.t("tex.55"));
        btnMatSpecs.setAlignmentX(0f);
        btnMatSpecs.setFont(BarMapStudio.font(Font.BOLD, 10));
        btnMatSpecs.setToolTipText(I18n.t("tex.56"));
        btnMatSpecs.addActionListener(e -> showMaterialSpecs());
        panel.add(btnMatSpecs);
        panel.add(vstrut(14));

        JScrollPane leftScroll = wrapSidebarColumn(panel);

        // Le sezioni 2 e 3 stanno nella colonna di destra
        panel = newSidebarColumn();

        JCheckBox matchBox = new JCheckBox(I18n.t("tex.match"), matchMapColours);
        matchBox.setAlignmentX(0f);
        matchBox.setOpaque(false);
        matchBox.setFont(BarMapStudio.font(Font.BOLD, 11));
        matchBox.setForeground(TEXT_MAIN);
        matchBox.setToolTipText(I18n.t("tex.match.tip"));
        matchBox.addActionListener(e -> {
            matchMapColours = matchBox.isSelected();
            queueRegen();
        });
        panel.add(matchBox);
        JLabel matchDesc = new JLabel("<html>" + I18n.t("tex.match.desc") + "</html>");
        matchDesc.setAlignmentX(0f);
        matchDesc.setFont(BarMapStudio.font(Font.PLAIN, 10));
        matchDesc.setForeground(TEXT_MUTED);
        panel.add(matchDesc);
        panel.add(vstrut(14));

        // --- SEZIONE 2: SLIDERS & REGOLE TERRENO (METODO A) ---
        JLabel sec2Title = new JLabel(I18n.t("tex.57"));
        sec2Title.setAlignmentX(0f);
        sec2Title.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        sec2Title.setForeground(ACCENT_AMBER);
        panel.add(sec2Title);
        panel.add(vstrut(2));

        JLabel sec2Desc = new JLabel(I18n.t("tex.58"));
        sec2Desc.setAlignmentX(0f);
        sec2Desc.setFont(BarMapStudio.font(Font.PLAIN, 10));
        sec2Desc.setForeground(TEXT_MUTED);
        panel.add(sec2Desc);
        panel.add(vstrut(6));

        panel.add(createCompactSliderRow(I18n.t("tex.59"), (int)(slopeThreshold * 100), 10, 80, "%", val -> {
            slopeThreshold = val / 100.0f;
            queueRegen();
        }));
        panel.add(vstrut(6));

        panel.add(createCompactSliderRow(I18n.t("tex.60"), (int)(slopeFalloff * 100), 2, 40, "%", val -> {
            slopeFalloff = val / 100.0f;
            queueRegen();
        }));
        panel.add(vstrut(6));

        panel.add(createCompactSliderRow(I18n.t("tex.61"), (int)waterElevation, 0, 150, "m", val -> {
            waterElevation = val;
            waterPresent = true;
            queueRegen();
        }));
        panel.add(vstrut(6));

        panel.add(createCompactSliderRow(I18n.t("tex.62"), (int)beachBand, 2, 40, "m", val -> {
            beachBand = val;
            queueRegen();
        }));
        panel.add(vstrut(6));

        panel.add(createCompactSliderRow(I18n.t("tex.63"), (int)(peakAltitude * 100), 40, 95, "%", val -> {
            peakAltitude = val / 100.0f;
            queueRegen();
        }));
        panel.add(vstrut(6));

        panel.add(createCompactSliderRow(I18n.t("tex.64"), (int)(noisePerturbation * 100), 0, 70, "%", val -> {
            noisePerturbation = val / 100.0f;
            queueRegen();
        }));
        panel.add(vstrut(14));

        // --- SEZIONE 3: PARAMETRI DETTAGLIO MATERIALI PBR ---
        JLabel sec3Title = new JLabel(I18n.t("tex.65"));
        sec3Title.setAlignmentX(0f);
        sec3Title.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        sec3Title.setForeground(ACCENT_GREEN);
        panel.add(sec3Title);
        panel.add(vstrut(2));

        JLabel sec3Desc = new JLabel(I18n.t("tex.66"));
        sec3Desc.setAlignmentX(0f);
        sec3Desc.setFont(BarMapStudio.font(Font.PLAIN, 10));
        sec3Desc.setForeground(TEXT_MUTED);
        panel.add(sec3Desc);
        panel.add(vstrut(6));

        panel.add(createCompactSliderRow(I18n.t("tex.67"), (int)(matScale * 10), 10, 100, "", val -> {
            matScale = val / 10.0f;
            queueRegen();
        }));
        panel.add(vstrut(6));

        panel.add(createCompactSliderRow(I18n.t("tex.68"), matOctaves, 1, 8, "", val -> {
            matOctaves = val;
            queueRegen();
        }));
        panel.add(vstrut(6));

        panel.add(createCompactSliderRow(I18n.t("tex.69"), (int)(matPersistence * 100), 20, 80, "%", val -> {
            matPersistence = val / 100.0f;
            queueRegen();
        }));
        panel.add(vstrut(6));

        panel.add(createCompactSliderRow(I18n.t("tex.70"), (int)(matContrast * 50), 25, 120, "", val -> {
            matContrast = val / 50.0f;
            queueRegen();
        }));
        panel.add(vstrut(6));

        panel.add(createCompactSliderRow(I18n.t("tex.71"), (int)(matBump * 50), 10, 150, "", val -> {
            matBump = val / 50.0f;
            queueRegen();
        }));

        rightSidebar = wrapSidebarColumn(panel);
        return leftScroll;
    }

    private JScrollPane rightSidebar;

    private static ScrollableTrackWidthPanel newSidebarColumn() {
        ScrollableTrackWidthPanel panel = new ScrollableTrackWidthPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(0f);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private static JScrollPane wrapSidebarColumn(JPanel panel) {
        Component glue = Box.createVerticalGlue();
        if (glue instanceof JComponent jc) {
            jc.setAlignmentX(0f);
        }
        panel.add(glue);

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    /** Le zone di cui non si e' scelto il materiale prendono i colori della texture generata in MAPS. */
    private boolean matchMapColours = true;
    private float[] lastTint;
    private int lastTintW;
    private int lastTintH;

    private static boolean followsMap(ZoneConfig z) {
        return !z.presetChanged && z.customTexture == null && z.material == null;
    }

    /** Media a blocchi di un'immagine su una griglia w x h: 3 valori (r, g, b) per cella. */
    private static float[] boxAverage(BufferedImage img, int w, int h) {
        int iw = img.getWidth();
        int ih = img.getHeight();
        int[] px = img.getRGB(0, 0, iw, ih, null, 0, iw);
        float[] out = new float[w * h * 3];
        for (int cy = 0; cy < h; cy++) {
            int y0 = (cy * ih) / h;
            int y1 = Math.max(y0 + 1, ((cy + 1) * ih) / h);
            for (int cx = 0; cx < w; cx++) {
                int x0 = (cx * iw) / w;
                int x1 = Math.max(x0 + 1, ((cx + 1) * iw) / w);
                long r = 0, g = 0, b = 0;
                int n = 0;
                for (int y = y0; y < y1 && y < ih; y++) {
                    for (int x = x0; x < x1 && x < iw; x++) {
                        int c = px[(y * iw) + x];
                        r += (c >> 16) & 255;
                        g += (c >> 8) & 255;
                        b += c & 255;
                        n++;
                    }
                }
                int o = ((cy * w) + cx) * 3;
                if (n > 0) {
                    out[o] = r / (float) n;
                    out[o + 1] = g / (float) n;
                    out[o + 2] = b / (float) n;
                }
            }
        }
        return out;
    }

    /**
     * Calcola la correzione che porta i colori medi della composizione a quelli del terreno di MAPS
     * (solo dove comandano zone senza materiale scelto) e la applica all'anteprima.
     */
    private float[] matchToMapColours(BufferedImage composite, BufferedImage ground, float[][] masks, int resW, int resH, int tw, int th) {
        float[] want = boxAverage(ground, tw, th);
        float[] have = boxAverage(composite, tw, th);
        ZoneConfig[] zones = {zone1, zone2, zone3, zone4};
        float[] tint = new float[tw * th * 3];
        for (int cy = 0; cy < th; cy++) {
            for (int cx = 0; cx < tw; cx++) {
                // quanto di questa cella appartiene a zone che seguono MAPS
                int mx = Math.min(resW - 1, ((cx * resW) + (resW / 2)) / tw);
                int my = Math.min(resH - 1, ((cy * resH) + (resH / 2)) / th);
                float strength = 0.0f;
                for (int k = 0; k < 4; k++) {
                    if (followsMap(zones[k])) {
                        strength += masks[k][(my * resW) + mx];
                    }
                }
                strength = Math.max(0.0f, Math.min(1.0f, strength));
                int o = ((cy * tw) + cx) * 3;
                for (int c = 0; c < 3; c++) {
                    float ratio = Math.max(0.25f, Math.min(4.0f, (want[o + c] + 4.0f) / (have[o + c] + 4.0f)));
                    tint[o + c] = 1.0f + ((ratio - 1.0f) * strength);
                }
            }
        }
        int[] px = composite.getRGB(0, 0, resW, resH, null, 0, resW);
        for (int y = 0; y < resH; y++) {
            float fy = Math.max(0.0f, Math.min(th - 1.0f, (((y + 0.5f) / resH) * th) - 0.5f));
            int y0 = (int) fy;
            int y1 = Math.min(th - 1, y0 + 1);
            float ty = fy - y0;
            for (int x = 0; x < resW; x++) {
                float fx = Math.max(0.0f, Math.min(tw - 1.0f, (((x + 0.5f) / resW) * tw) - 0.5f));
                int x0 = (int) fx;
                int x1 = Math.min(tw - 1, x0 + 1);
                float tx = fx - x0;
                int c = px[(y * resW) + x];
                int out = 0xFF000000;
                for (int ch = 0; ch < 3; ch++) {
                    float top = (tint[(((y0 * tw) + x0) * 3) + ch] * (1.0f - tx)) + (tint[(((y0 * tw) + x1) * 3) + ch] * tx);
                    float bottom = (tint[(((y1 * tw) + x0) * 3) + ch] * (1.0f - tx)) + (tint[(((y1 * tw) + x1) * 3) + ch] * tx);
                    int shift = 16 - (ch * 8);
                    int value = Math.min(255, Math.round(((c >> shift) & 255) * ((top * (1.0f - ty)) + (bottom * ty))));
                    out |= value << shift;
                }
                px[(y * resW) + x] = out;
            }
        }
        composite.setRGB(0, 0, resW, resH, px, 0, resW);
        return tint;
    }

    /** Impostato dall'app: ricollega il pannello alla mappa aperta in questo momento nell'editor. */
    private Runnable syncWithEditor;

    public void setSyncWithEditor(Runnable syncWithEditor) {
        this.syncWithEditor = syncWithEditor;
    }

    /**
     * Scheda compatta per una singola Zona
     */
    private JPanel buildZoneCard(ZoneConfig z, String roleDesc, String autoRuleDesc) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(0f);
        card.setBackground(CARD_INNER);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 8, 6, 8)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));

        // Riga 1: Pallino + Titolo
        JPanel r1 = new JPanel(new BorderLayout(6, 0));
        r1.setAlignmentX(0f);
        r1.setOpaque(false);

        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(z.indicatorColor);
                g2.fillOval(0, 2, 8, 8);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(10, 12));
        dot.setOpaque(false);
        r1.add(dot, BorderLayout.WEST);

        JLabel title = new JLabel(z.title.toUpperCase());
        title.setFont(BarMapStudio.fontCond(Font.BOLD, 10));
        title.setForeground(TEXT_MAIN);
        r1.add(title, BorderLayout.CENTER);

        card.add(r1);
        card.add(vstrut(4));

        // Riga 2: Maschera File o Auto
        JPanel r2 = new JPanel(new BorderLayout(4, 0));
        r2.setAlignmentX(0f);
        r2.setOpaque(false);

        JLabel maskStatusBadge = new JLabel(z.maskFile != null ? z.maskFile.getName() : I18n.t("tex.72") + autoRuleDesc + ")");
        maskStatusBadge.setFont(BarMapStudio.font(Font.PLAIN, 10));
        maskStatusBadge.setForeground(z.maskFile != null ? ACCENT_GREEN : TEXT_MUTED);

        JButton btnLoadMask = createGhostButton(z.maskFile != null ? I18n.t("tex.73") : I18n.t("tex.74"));
        btnLoadMask.setPreferredSize(new Dimension(105, 22));
        btnLoadMask.setFont(BarMapStudio.font(Font.BOLD, 9));
        btnLoadMask.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser("custom_assets/textures");
            FileChoosers.detailed(chooser);
            chooser.setDialogTitle(I18n.t("tex.75") + z.title);
            chooser.setFileFilter(new FileNameExtensionFilter(I18n.t("tex.76"), "png", "jpg", "jpeg", "bmp"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = chooser.getSelectedFile();
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) {
                        z.maskFile = f;
                        z.loadedMask = img;
                        z.useFileMask = true;
                        maskStatusBadge.setText(f.getName());
                        maskStatusBadge.setForeground(ACCENT_GREEN);
                        btnLoadMask.setText(I18n.t("tex.73"));
                        queueRegen();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, I18n.t("tex.77") + ex.getMessage(), I18n.t("tex.34"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnResetAuto = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(239, 68, 68) : TEXT_MUTED);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawLine(4, 4, 10, 10);
                g2.drawLine(10, 4, 4, 10);
                g2.dispose();
            }
        };
        btnResetAuto.setPreferredSize(new Dimension(14, 14));
        btnResetAuto.setContentAreaFilled(false);
        btnResetAuto.setBorderPainted(false);
        btnResetAuto.setFocusPainted(false);
        btnResetAuto.setToolTipText(I18n.t("tex.78"));
        btnResetAuto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResetAuto.addActionListener(e -> {
            z.maskFile = null;
            z.loadedMask = null;
            z.useFileMask = false;
            maskStatusBadge.setText(I18n.t("tex.72") + autoRuleDesc + ")");
            maskStatusBadge.setForeground(TEXT_MUTED);
            btnLoadMask.setText(I18n.t("tex.74"));
            queueRegen();
        });

        JPanel maskBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        maskBtns.setOpaque(false);
        maskBtns.add(btnLoadMask);
        maskBtns.add(btnResetAuto);

        r2.add(maskStatusBadge, BorderLayout.WEST);
        r2.add(maskBtns, BorderLayout.EAST);
        card.add(r2);
        card.add(vstrut(4));

        // Riga 3: Materiale / Texture
        JPanel r3 = new JPanel(new BorderLayout(4, 0));
        r3.setAlignmentX(0f);
        r3.setOpaque(false);

        JSpinner tileSpinner = new JSpinner(new SpinnerNumberModel((double) z.tileElmos, 16.0, 2048.0, 16.0));
        JSpinner glossSpinner = new JSpinner(new SpinnerNumberModel((int) Math.round(z.gloss * 100), 0, 100, 5));
        JLabel matBadge = new JLabel(I18n.t("tex.79"));

        JComboBox<MaterialPreset> combo = new JComboBox<>(MaterialPreset.values());
        combo.setFont(BarMapStudio.font(Font.PLAIN, 10));
        combo.setBackground(CARD_BG);
        combo.setForeground(TEXT_MAIN);
        combo.setSelectedItem(z.preset);
        combo.setPreferredSize(new Dimension(140, 22));
        combo.addActionListener(e -> {
            MaterialPreset sel = (MaterialPreset) combo.getSelectedItem();
            if (sel != null) {
                z.preset = sel;
                z.presetChanged = true;
                z.customTexture = null;
                z.customTextureFile = null;
                z.material = null;
                matBadge.setText(I18n.t("tex.79"));
                matBadge.setForeground(TEXT_MUTED);
                queueRegen();
            }
        });
        r3.add(combo, BorderLayout.CENTER);

        JButton btnTexFile = createGhostButton(I18n.t("tex.80"));
        btnTexFile.setPreferredSize(new Dimension(50, 22));
        btnTexFile.setFont(BarMapStudio.font(Font.BOLD, 9));
        btnTexFile.setToolTipText(I18n.t("tex.81"));
        btnTexFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser("custom_assets/textures");
            FileChoosers.detailed(chooser);
            chooser.setDialogTitle(I18n.t("tex.82") + z.title);
            chooser.setFileFilter(new FileNameExtensionFilter(I18n.t("tex.83"), "png", "jpg", "jpeg", "bmp"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = chooser.getSelectedFile();
                    ZoneMaterials.LoadResult res = ZoneMaterials.load(f);
                    ZoneMaterials.Material m = res.material;
                    z.customTextureFile = f;
                    z.customTexture = m.diffuse;
                    z.material = m;
                    z.tileElmos = m.tileElmos;
                    z.gloss = m.gloss;
                    tileSpinner.setValue((double) m.tileElmos);
                    glossSpinner.setValue((int) Math.round(m.gloss * 100));
                    matBadge.setText(m.name + (m.normal != null ? I18n.t("tex.84") : "") + (m.height != null && m.reliefElmos > 0 ? I18n.t("tex.85") : ""));
                    matBadge.setForeground(ACCENT_GREEN);
                    queueRegen();
                    if (!res.warnings.isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                            I18n.t("tex.86") + String.join("\n- ", res.warnings)
                                + I18n.t("tex.87"),
                            "Materiale: " + m.name, JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, I18n.t("tex.88") + ex.getMessage(), I18n.t("tex.34"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        r3.add(btnTexFile, BorderLayout.EAST);

        card.add(r3);
        card.add(vstrut(4));

        // Riga 4: scala sul terreno e lucentezza del materiale
        JPanel r4 = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        r4.setAlignmentX(0f);
        r4.setOpaque(false);
        JLabel tileLbl = new JLabel(I18n.t("tex.89"));
        tileLbl.setFont(BarMapStudio.font(Font.PLAIN, 10));
        tileLbl.setForeground(TEXT_MUTED);
        tileSpinner.setFont(BarMapStudio.font(Font.PLAIN, 10));
        tileSpinner.setPreferredSize(new Dimension(62, 20));
        tileSpinner.setToolTipText(I18n.t("tex.90"));
        tileSpinner.addChangeListener(e -> {
            z.tileElmos = ((Number) tileSpinner.getValue()).floatValue();
            queueRegen();
        });
        JLabel glossLbl = new JLabel(I18n.t("tex.91"));
        glossLbl.setFont(BarMapStudio.font(Font.PLAIN, 10));
        glossLbl.setForeground(TEXT_MUTED);
        glossSpinner.setFont(BarMapStudio.font(Font.PLAIN, 10));
        glossSpinner.setPreferredSize(new Dimension(48, 20));
        glossSpinner.setToolTipText(I18n.t("tex.92"));
        glossSpinner.addChangeListener(e -> z.gloss = ((Number) glossSpinner.getValue()).intValue() / 100.0f);
        r4.add(tileLbl);
        r4.add(tileSpinner);
        r4.add(glossLbl);
        r4.add(glossSpinner);
        card.add(r4);
        card.add(vstrut(2));

        matBadge.setAlignmentX(0f);
        matBadge.setFont(BarMapStudio.font(Font.PLAIN, 9));
        matBadge.setForeground(TEXT_MUTED);
        card.add(matBadge);
        return card;
    }

    /**
     * Slider Compatto a 2 Righe:
     * Riga 1: Etichetta a sx ............ Valore a dx
     * Riga 2: Slider a tutta larghezza 100%
     */
    private JPanel createCompactSliderRow(String label, int initialValue, int min, int max, String unit, Consumer<Integer> onChange) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setAlignmentX(0f);
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        // Riga 1: Testo + Valore
        JPanel top = new JPanel(new BorderLayout());
        top.setAlignmentX(0f);
        top.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(BarMapStudio.font(Font.PLAIN, 10));
        l.setForeground(TEXT_MUTED);
        top.add(l, BorderLayout.WEST);

        JLabel valLbl = new JLabel(initialValue + unit);
        valLbl.setFont(BarMapStudio.font(Font.BOLD, 10));
        valLbl.setForeground(ACCENT_CYAN);
        top.add(valLbl, BorderLayout.EAST);

        container.add(top);

        // Riga 2: Slider 100%
        JSlider slider = new JSlider(min, max, initialValue);
        slider.setAlignmentX(0f);
        slider.setOpaque(false);
        slider.setBackground(CARD_BG);
        slider.setForeground(ACCENT_CYAN);
        slider.setPreferredSize(new Dimension(200, 18));
        slider.addChangeListener(e -> {
            valLbl.setText(slider.getValue() + unit);
            onChange.accept(slider.getValue());
        });

        container.add(slider);
        return container;
    }

    private JComponent buildCenterCanvas() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD_INNER);
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_COLOR));

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CARD_BG);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        bar.setPreferredSize(new Dimension(100, 36));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        left.setOpaque(false);

        JLabel vLbl = new JLabel(I18n.t("tex.93"));
        vLbl.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        vLbl.setForeground(ACCENT_CYAN);
        left.add(vLbl);

        viewModeCombo = new JComboBox<>(ViewMode.values());
        viewModeCombo.setFont(BarMapStudio.font(Font.BOLD, 11));
        viewModeCombo.setBackground(CARD_INNER);
        viewModeCombo.setForeground(TEXT_MAIN);
        viewModeCombo.setPreferredSize(new Dimension(220, 24));
        viewModeCombo.addActionListener(e -> {
            ViewMode vm = (ViewMode) viewModeCombo.getSelectedItem();
            if (vm != null) {
                currentViewMode = vm;
                canvas.repaint();
            }
        });
        left.add(viewModeCombo);
        bar.add(left, BorderLayout.WEST);

        p.add(bar, BorderLayout.NORTH);

        canvas = new TextureCanvas();
        canvas.setHudDock(buildCanvasHudDock());
        p.add(canvas, BorderLayout.CENTER);

        return p;
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
        slopeToggleBtn.setToolTipText(I18n.t("tex.94"));
        slopeToggleBtn.addActionListener(e -> {
            canvas.setShowSlopeMap(slopeToggleBtn.isSelected());
            updateSlopeButtonState();
        });
        dock.add(slopeToggleBtn);

        // Opacity Label & Slider
        JLabel opacityLbl = new JLabel(I18n.t("tex.95"));
        opacityLbl.setFont(BarMapStudio.fontCond(Font.BOLD, 10));
        opacityLbl.setForeground(TEXT_MUTED);
        dock.add(opacityLbl);

        slopeOpacitySlider.setPreferredSize(new Dimension(65, 20));
        slopeOpacitySlider.setOpaque(false);
        slopeOpacitySlider.setToolTipText(I18n.t("tex.96"));
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
        zoomOut.setToolTipText(I18n.t("tex.97"));
        zoomOut.addActionListener(e -> canvas.zoomOut());
        dock.add(zoomOut);

        // Zoom In [+]
        JButton zoomIn = createGhostButton("+");
        zoomIn.setFont(BarMapStudio.font(Font.BOLD, 13));
        zoomIn.setPreferredSize(new Dimension(28, 24));
        zoomIn.setToolTipText(I18n.t("tex.98"));
        zoomIn.addActionListener(e -> canvas.zoomIn());
        dock.add(zoomIn);

        // 1:1 Scala
        JButton zoomReset = createGhostButton(I18n.t("tex.99"));
        zoomReset.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        zoomReset.setPreferredSize(new Dimension(76, 24));
        zoomReset.setToolTipText(I18n.t("tex.100"));
        zoomReset.addActionListener(e -> canvas.resetZoom());
        dock.add(zoomReset);

        return dock;
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

    private boolean isTypingInText() {
        Component fo = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        return (fo instanceof javax.swing.text.JTextComponent);
    }

    private void bindKey(int keyCode, int modifiers, String actionName, Runnable action) {
        KeyStroke stroke = KeyStroke.getKeyStroke(keyCode, modifiers);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, actionName);
        getActionMap().put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    private void setupHotkeys() {
        bindKey(KeyEvent.VK_L, 0, "toggle_slope_map", () -> { if (!isTypingInText()) toggleSlopeMap(); });
        bindKey(KeyEvent.VK_PLUS, 0, "zoom_in_plus", () -> { if (!isTypingInText() && canvas != null) canvas.zoomIn(); });
        bindKey(KeyEvent.VK_ADD, 0, "zoom_in_add", () -> { if (!isTypingInText() && canvas != null) canvas.zoomIn(); });
        bindKey(KeyEvent.VK_EQUALS, 0, "zoom_in_eq", () -> { if (!isTypingInText() && canvas != null) canvas.zoomIn(); });
        bindKey(KeyEvent.VK_MINUS, 0, "zoom_out_minus", () -> { if (!isTypingInText() && canvas != null) canvas.zoomOut(); });
        bindKey(KeyEvent.VK_SUBTRACT, 0, "zoom_out_sub", () -> { if (!isTypingInText() && canvas != null) canvas.zoomOut(); });
        bindKey(KeyEvent.VK_0, 0, "zoom_reset_0", () -> { if (!isTypingInText() && canvas != null) canvas.resetZoom(); });
        bindKey(KeyEvent.VK_NUMPAD0, 0, "zoom_reset_num0", () -> { if (!isTypingInText() && canvas != null) canvas.resetZoom(); });
    }

    public void toggleSlopeMap() {
        if (canvas != null) {
            canvas.toggleSlopeMap();
            updateSlopeButtonState();
        }
    }

    public void updateSlopeButtonState() {
        if (canvas == null) return;
        boolean active = canvas.isShowSlopeMap();
        slopeToggleBtn.setSelected(active);
        slopeToggleBtn.setText(active ? I18n.t("tex.101") : I18n.t("tex.22"));
        slopeToggleBtn.setForeground(active ? ACCENT_AMBER : TEXT_MAIN);
        slopeToggleBtn.setBackground(active ? new Color(45, 34, 18) : CARD_INNER);
        slopeToggleBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(active ? ACCENT_AMBER : BORDER_COLOR, 1, true),
            new EmptyBorder(2, 8, 2, 8)
        ));
    }

    public void updateSlopeOverlay() {
        if (activeMap != null && activeMap.getHeightmap() != null) {
            float wElev = (activeMap.getBiome() != null && activeMap.getBiome().waterSettings() != null) ? activeMap.getBiome().waterSettings().elevation() : activeMap.getWaterElevation();
            this.renderedSlopeOverlay = GroundGenerator.generateSlopeOverlayImage(activeMap, wElev);
            if (canvas != null) canvas.repaint();
        }
    }

    private JComponent buildBottomActionBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(CARD_BG);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        bar.setPreferredSize(new Dimension(100, 46));
        bar.setBorder(new EmptyBorder(4, 12, 4, 12));

        statusLabel = new JLabel(I18n.t("tex.102"));
        statusLabel.setFont(BarMapStudio.font(Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_MUTED);
        bar.add(statusLabel, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton btnOpenFolder = createGhostButton(I18n.t("tex.103"));
        btnOpenFolder.addActionListener(e -> openCustomFolder());
        actions.add(btnOpenFolder);

        JButton btnSaveDiffuse = createGhostButton(I18n.t("tex.104"));
        btnSaveDiffuse.addActionListener(e -> saveCurrentTexture(false));
        actions.add(btnSaveDiffuse);

        JButton btnSaveNormal = createGhostButton(I18n.t("tex.105"));
        btnSaveNormal.addActionListener(e -> saveCurrentTexture(true));
        actions.add(btnSaveNormal);

        JButton btnApplyToMap = new JButton(I18n.t("tex.106")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 180, 210));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(ACCENT_CYAN);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnApplyToMap.setIcon(createVectorStarIcon(13, new Color(11, 14, 20)));
        btnApplyToMap.setIconTextGap(6);
        btnApplyToMap.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        btnApplyToMap.setForeground(new Color(11, 14, 20));
        btnApplyToMap.setContentAreaFilled(false);
        btnApplyToMap.setFocusPainted(false);
        btnApplyToMap.setBorder(new EmptyBorder(6, 16, 6, 16));
        btnApplyToMap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnApplyToMap.addActionListener(e -> doApplyToActiveMap());
        actions.add(btnApplyToMap);

        bar.add(actions, BorderLayout.EAST);
        return bar;
    }

    private void queueRegen() {
        debounceTimer.restart();
    }

    private void setStatus(String text, Color col) {
        SwingUtilities.invokeLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText(text);
                statusLabel.setForeground(col);
            }
        });
    }

    private Dimension computeTargetResolution() {
        int mapW = 512;
        int mapH = 512;
        if (activeMap != null) {
            mapW = activeMap.getWidth() > 0 ? activeMap.getWidth() : activeMap.getSize();
            mapH = activeMap.getHeight() > 0 ? activeMap.getHeight() : activeMap.getSize();
        } else if (customLoadedHeightmap != null) {
            mapW = customLoadedHeightmap.getWidth();
            mapH = customLoadedHeightmap.getHeight();
        }
        if (mapW <= 0) mapW = 512;
        if (mapH <= 0) mapH = 512;
        double aspect = (double) mapH / (double) mapW;
        int resW = currentRes;
        int resH = (int) Math.max(64, Math.round(currentRes * aspect));
        return new Dimension(resW, resH);
    }

    private void triggerAsyncGeneration() {
        final BufferedImage sourceHm = (activeMap != null && activeMap.getHeightmap() != null) ? activeMap.getHeightmap() : customLoadedHeightmap;
        if (sourceHm == null) {
            renderedDiffuse = null;
            renderedNormal = null;
            renderedSplatRGBA = null;
            renderedChannelR = null;
            renderedChannelG = null;
            renderedChannelB = null;
            renderedChannelA = null;
            setStatus(I18n.t("tex.107"), TEXT_MUTED);
            if (canvas != null) canvas.repaint();
            return;
        }

        setStatus(I18n.t("tex.108"), ACCENT_AMBER);
        final Dimension targetDim = computeTargetResolution();
        final int resW = targetDim.width;
        final int resH = targetDim.height;
        final long seed = currentSeed;
        // 1 cella della mappa = 16 elmi: lato di un pixel della composizione sul terreno
        final int mapCellsW = (activeMap != null)
            ? (activeMap.getWidth() > 0 ? activeMap.getWidth() : activeMap.getSize())
            : sourceHm.getWidth();
        final float elmosPerPx = Math.max(1, mapCellsW) * 16.0f / resW;

        genPool.submit(() -> {
            long t0 = System.currentTimeMillis();
            try {
                float[][] heightmap = new float[resH][resW];
                float[][] slopeMap = new float[resH][resW];
                float minH = Float.MAX_VALUE;
                float maxH = -Float.MAX_VALUE;

                float scale = (activeMap != null && activeMap.getHeightMapScale() > 0) ? activeMap.getHeightMapScale() : 0.0078125f;
                int hmW = sourceHm.getWidth();
                int hmH = sourceHm.getHeight();
                Raster r = sourceHm.getRaster();
                int[] pixelH = new int[1];

                // heightmap a 16 bit = quote reali per la scala della mappa; a 8 bit = mezzo elmo per livello.
                // (prima si decideva pixel per pixel, e le quote sotto 2 elmi venivano lette come 8 bit)
                final boolean sixteenBit = r.getSampleModel().getSampleSize(0) > 8;
                float[] hmVals = new float[hmW * hmH];
                for (int sy = 0; sy < hmH; sy++) {
                    for (int sx = 0; sx < hmW; sx++) {
                        r.getPixel(sx, sy, pixelH);
                        hmVals[sy * hmW + sx] = sixteenBit ? (pixelH[0] * scale) : (pixelH[0] * 0.5f);
                    }
                }
                // campionamento bilineare: niente gradini quando la risoluzione supera quella dell'heightmap
                for (int y = 0; y < resH; y++) {
                    float fy = Math.max(0f, Math.min(hmH - 1, (y + 0.5f) * hmH / resH - 0.5f));
                    int y0 = (int) fy;
                    int y1 = Math.min(hmH - 1, y0 + 1);
                    float ty = fy - y0;
                    for (int x = 0; x < resW; x++) {
                        float fx = Math.max(0f, Math.min(hmW - 1, (x + 0.5f) * hmW / resW - 0.5f));
                        int x0 = (int) fx;
                        int x1 = Math.min(hmW - 1, x0 + 1);
                        float tx = fx - x0;
                        float top = hmVals[y0 * hmW + x0] * (1f - tx) + hmVals[y0 * hmW + x1] * tx;
                        float bottom = hmVals[y1 * hmW + x0] * (1f - tx) + hmVals[y1 * hmW + x1] * tx;
                        float h = top * (1f - ty) + bottom * ty;
                        heightmap[y][x] = h;
                        if (h < minH) minH = h;
                        if (h > maxH) maxH = h;
                    }
                }

                for (int y = 0; y < resH; y++) {
                    int yPrev = Math.max(0, y - 1);
                    int yNext = Math.min(resH - 1, y + 1);
                    for (int x = 0; x < resW; x++) {
                        int xPrev = Math.max(0, x - 1);
                        int xNext = Math.min(resW - 1, x + 1);
                        float dx = (heightmap[y][xNext] - heightmap[y][xPrev]) * 0.5f;
                        float dy = (heightmap[yNext][x] - heightmap[yPrev][x]) * 0.5f;
                        // pendenza reale in gioco (dislivello in elmi / distanza in elmi), uguale a ogni risoluzione:
                        // 1.0 = 100% = 45 gradi. I veicoli di BAR si fermano intorno al 32%, i bot intorno al 73%.
                        slopeMap[y][x] = (float) Math.min(1.0, Math.sqrt(dx * dx + dy * dy) * ELMOS_PER_HEIGHT_UNIT / elmosPerPx);
                    }
                }

                BufferedImage slopeImg = new BufferedImage(resW, resH, BufferedImage.TYPE_INT_ARGB);
                int[] sPixels = new int[resW * resH];
                for (int y = 0; y < resH; y++) {
                    for (int x = 0; x < resW; x++) {
                        float h = heightmap[y][x];
                        float slope = slopeMap[y][x];
                        if (waterPresent && waterElevation > -900f && h < waterElevation) {
                            float depth = Math.min(1.0f, (waterElevation - h) / 30.0f);
                            int blue = (int) (180 + 75 * depth);
                            int green = (int) (140 - 40 * depth);
                            int red = (int) (14 - 10 * depth);
                            sPixels[y * resW + x] = (220 << 24) | (red << 16) | (green << 8) | blue;
                            continue;
                        }
                        int color;
                        if (slope < 0.22f) {
                            float t = Math.min(1.0f, Math.max(0f, slope / 0.22f));
                            int red = (int) (34 + t * 98);
                            int grn = (int) (197 + t * 7);
                            int blu = (int) (94 - t * 72);
                            color = (210 << 24) | (red << 16) | (grn << 8) | blu;
                        } else if (slope < 0.35f) {
                            float t = Math.min(1.0f, Math.max(0f, (slope - 0.22f) / (0.35f - 0.22f)));
                            int red = (int) (132 + t * 102);
                            int grn = (int) (204 - t * 25);
                            int blu = (int) (22 - t * 14);
                            color = (220 << 24) | (red << 16) | (grn << 8) | blu;
                        } else if (slope < 0.65f) {
                            float t = Math.min(1.0f, Math.max(0f, (slope - 0.35f) / (0.65f - 0.35f)));
                            int red = (int) (234 + t * 15);
                            int grn = (int) (179 - t * 64);
                            int blu = (int) (8 + t * 14);
                            color = (230 << 24) | (red << 16) | (grn << 8) | blu;
                        } else {
                            float t = Math.min(1.0f, Math.max(0f, (slope - 0.65f) / 0.5f));
                            int red = (int) (239 - t * 29);
                            int grn = (int) (68 - t * 38);
                            int blu = (int) (68 - t * 38);
                            color = (245 << 24) | (red << 16) | (grn << 8) | blu;
                        }
                        sPixels[y * resW + x] = color;
                    }
                }
                slopeImg.setRGB(0, 0, resW, resH, sPixels, 0, resW);
                this.renderedSlopeOverlay = slopeImg;

                float[][] maskR = computeZoneMask(zone1, heightmap, slopeMap, resW, resH, 1, seed, minH, maxH);
                float[][] maskG = computeZoneMask(zone2, heightmap, slopeMap, resW, resH, 2, seed, minH, maxH);
                float[][] maskB = computeZoneMask(zone3, heightmap, slopeMap, resW, resH, 3, seed, minH, maxH);
                float[][] maskA = computeZoneMask(zone4, heightmap, slopeMap, resW, resH, 4, seed, minH, maxH);

                for (int y = 0; y < resH; y++) {
                    for (int x = 0; x < resW; x++) {
                        float sum = maskR[y][x] + maskG[y][x] + maskB[y][x] + maskA[y][x];
                        if (sum <= 0.0001f) sum = 1.0f;
                        maskR[y][x] /= sum;
                        maskG[y][x] /= sum;
                        maskB[y][x] /= sum;
                        maskA[y][x] /= sum;
                    }
                }

                int texTileSize = 512;
                BufferedImage tex1 = getOrGenerateTexture(zone1, texTileSize, seed + 1);
                BufferedImage tex2 = getOrGenerateTexture(zone2, texTileSize, seed + 2);
                BufferedImage tex3 = getOrGenerateTexture(zone3, texTileSize, seed + 3);
                BufferedImage tex4 = getOrGenerateTexture(zone4, texTileSize, seed + 4);

                BufferedImage composite = new BufferedImage(resW, resH, BufferedImage.TYPE_INT_ARGB);
                BufferedImage splatRGBA = new BufferedImage(resW, resH, BufferedImage.TYPE_INT_ARGB);
                BufferedImage chR = new BufferedImage(resW, resH, BufferedImage.TYPE_INT_ARGB);
                BufferedImage chG = new BufferedImage(resW, resH, BufferedImage.TYPE_INT_ARGB);
                BufferedImage chB = new BufferedImage(resW, resH, BufferedImage.TYPE_INT_ARGB);
                BufferedImage chA = new BufferedImage(resW, resH, BufferedImage.TYPE_INT_ARGB);

                // i tile si ripetono in elmi (come in gioco), non in pixel della composizione
                BufferedImage[] tiles = {tex1, tex2, tex3, tex4};
                float[] tileElmos = {zone1.tileElmos, zone2.tileElmos, zone3.tileElmos, zone4.tileElmos};
                float[][] flatMasks = new float[4][resW * resH];
                for (int y = 0; y < resH; y++) {
                    float ez = (y + 0.5f) * elmosPerPx;
                    for (int x = 0; x < resW; x++) {
                        float ex = (x + 0.5f) * elmosPerPx;
                        float wR = maskR[y][x];
                        float wG = maskG[y][x];
                        float wB = maskB[y][x];
                        float wA = maskA[y][x];
                        int mi = y * resW + x;
                        flatMasks[0][mi] = wR;
                        flatMasks[1][mi] = wG;
                        flatMasks[2][mi] = wB;
                        flatMasks[3][mi] = wA;

                        int[] rgbs = new int[4];
                        for (int k = 0; k < 4; k++) {
                            BufferedImage tile = tiles[k];
                            int px = Math.floorMod((int) (ex / tileElmos[k] * tile.getWidth()), tile.getWidth());
                            int py = Math.floorMod((int) (ez / tileElmos[k] * tile.getHeight()), tile.getHeight());
                            rgbs[k] = tile.getRGB(px, py);
                        }
                        int rgb1 = rgbs[0];
                        int rgb2 = rgbs[1];
                        int rgb3 = rgbs[2];
                        int rgb4 = rgbs[3];

                        int red = (int) ((((rgb1 >> 16) & 0xFF) * wR) + (((rgb2 >> 16) & 0xFF) * wG) + (((rgb3 >> 16) & 0xFF) * wB) + (((rgb4 >> 16) & 0xFF) * wA));
                        int green = (int) ((((rgb1 >> 8) & 0xFF) * wR) + (((rgb2 >> 8) & 0xFF) * wG) + (((rgb3 >> 8) & 0xFF) * wB) + (((rgb4 >> 8) & 0xFF) * wA));
                        int blue = (int) (((rgb1 & 0xFF) * wR) + ((rgb2 & 0xFF) * wG) + ((rgb3 & 0xFF) * wB) + ((rgb4 & 0xFF) * wA));

                        red = Math.min(255, Math.max(0, red));
                        green = Math.min(255, Math.max(0, green));
                        blue = Math.min(255, Math.max(0, blue));

                        composite.setRGB(x, y, (0xFF << 24) | (red << 16) | (green << 8) | blue);

                        int sR = Math.min(255, (int)(wR * 255));
                        int sG = Math.min(255, (int)(wG * 255));
                        int sB = Math.min(255, (int)(wB * 255));
                        int sA = Math.min(255, (int)(wA * 255));
                        splatRGBA.setRGB(x, y, (0xFF << 24) | (sR << 16) | (sG << 8) | sB);

                        chR.setRGB(x, y, (0xFF << 24) | (sR << 16) | (sR << 8) | sR);
                        chG.setRGB(x, y, (0xFF << 24) | (sG << 16) | (sG << 8) | sG);
                        chB.setRGB(x, y, (0xFF << 24) | (sB << 16) | (sB << 8) | sB);
                        chA.setRGB(x, y, (0xFF << 24) | (sA << 16) | (sA << 8) | sA);
                    }
                }

                BufferedImage normal = createNormalMap(heightmap, resW, resH, matBump);

                final BufferedImage mapGround = activeGround;
                if (matchMapColours && mapGround != null && activeMap != null) {
                    int tw = Math.max(16, resW / 6);
                    int th = Math.max(16, resH / 6);
                    this.lastTint = matchToMapColours(composite, mapGround, flatMasks, resW, resH, tw, th);
                    this.lastTintW = tw;
                    this.lastTintH = th;
                } else {
                    this.lastTint = null;
                }

                this.renderedDiffuse = composite;
                this.lastMasks = flatMasks;
                this.lastMaskW = resW;
                this.lastMaskH = resH;
                this.lastTiles = tiles;
                this.renderedNormal = normal;
                this.renderedSplatRGBA = splatRGBA;
                this.renderedChannelR = chR;
                this.renderedChannelG = chG;
                this.renderedChannelB = chB;
                this.renderedChannelA = chA;

                long elapsed = System.currentTimeMillis() - t0;
                String mapName = (activeMap != null) ? activeMap.getName() : I18n.t("tex.109");
                setStatus(I18n.t("tex.110") + elapsed + " ms (" + resW + "x" + resH + " px) su " + mapName, ACCENT_GREEN);
                SwingUtilities.invokeLater(() -> canvas.repaint());

            } catch (Throwable t) {
                t.printStackTrace();
                setStatus(I18n.t("tex.111") + t.getMessage(), Color.RED);
            }
        });
    }

    private float[][] computeZoneMask(ZoneConfig z, float[][] heightmap, float[][] slopeMap, int resW, int resH, int zoneIndex, long seed, float minH, float maxH) {
        float[][] mask = new float[resH][resW];

        // METODO B: File caricato dall'utente
        if (z.useFileMask && z.loadedMask != null) {
            BufferedImage fileImg = z.loadedMask;
            int fw = fileImg.getWidth();
            int fh = fileImg.getHeight();
            for (int y = 0; y < resH; y++) {
                int sy = Math.min(fh - 1, (y * fh) / resH);
                for (int x = 0; x < resW; x++) {
                    int sx = Math.min(fw - 1, (x * fw) / resW);
                    int rgb = fileImg.getRGB(sx, sy);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    int val = (r + g + b) / 3;
                    mask[y][x] = val / 255.0f;
                }
            }
            return mask;
        }

        // METODO A: Sliders e Regole Automatiche
        float heightRange = Math.max(0.5f, maxH - minH);
        float peakThreshold = minH + peakAltitude * heightRange;

        for (int y = 0; y < resH; y++) {
            float ny = (float) y / resH;
            for (int x = 0; x < resW; x++) {
                float nx = (float) x / resW;
                float h = heightmap[y][x];
                float s = slopeMap[y][x];
                float nPerturb = (fbmPeriodic2D(nx * 8f, ny * 8f, 8, 3, 0.5f, seed + 50) - 0.5f) * noisePerturbation;

                // Le quattro zone si spartiscono il terreno senza sovrapporsi, in ordine di precedenza:
                // roccia sui pendii, poi costa e fondale, poi cime, e pianura su tutto il resto.
                float rock = smoothstep(slopeThreshold - slopeFalloff, slopeThreshold + slopeFalloff, s + nPerturb);
                float coast = 0.0f;
                // senza nemmeno un punto sotto il livello dell'acqua la mappa e' asciutta: nessuna costa
                if (waterPresent && minH < waterElevation) {
                    // la fascia e' in elmi sopra il livello dell'acqua; tutto cio' che sta sotto e' fondale
                    float aboveWater = (h - waterElevation) * ELMOS_PER_HEIGHT_UNIT + (nPerturb * beachBand);
                    coast = 1.0f - smoothstep(0f, Math.max(1f, beachBand), aboveWater);
                }
                float edge = Math.max(1.0f, heightRange * 0.06f);
                float peak = smoothstep(peakThreshold - edge, peakThreshold + edge, h + (nPerturb * heightRange * 0.15f));
                if (zoneIndex == 2) {
                    mask[y][x] = rock;
                } else if (zoneIndex == 3) {
                    mask[y][x] = coast * (1.0f - rock);
                } else if (zoneIndex == 4) {
                    mask[y][x] = peak * (1.0f - rock) * (1.0f - coast);
                } else {
                    mask[y][x] = (1.0f - peak) * (1.0f - rock) * (1.0f - coast);
                }
            }
        }
        return mask;
    }

    private BufferedImage getOrGenerateTexture(ZoneConfig z, int res, long seed) {
        if (z.customTexture != null) {
            return z.customTexture;
        }
        return generateFastMaterial(z.preset, res, seed);
    }

    private BufferedImage generateFastMaterial(MaterialPreset pset, int res, long seed) {
        BufferedImage img = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
        int basePeriod = Math.max(4, Math.round(matScale * 2.0f));
        for (int y = 0; y < res; y++) {
            float ny = (float) y / res;
            for (int x = 0; x < res; x++) {
                float nx = (float) x / res;
                float val = fbmPeriodic2D(nx * basePeriod, ny * basePeriod, basePeriod, matOctaves, matPersistence, seed);
                val = (val + 1.0f) * 0.5f;
                val = (float) Math.pow(Math.max(0f, Math.min(1f, val)), matContrast);

                Color c;
                if (val < 0.5f) {
                    c = lerpColor(pset.colorDark, pset.colorMid, val * 2.0f);
                } else {
                    c = lerpColor(pset.colorMid, pset.colorLight, (val - 0.5f) * 2.0f);
                }
                img.setRGB(x, y, c.getRGB());
            }
        }
        return img;
    }

    private BufferedImage createNormalMap(float[][] height, int resW, int resH, float bumpStrength) {
        BufferedImage normal = new BufferedImage(resW, resH, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < resH; y++) {
            int yPrev = (y - 1 + resH) % resH;
            int yNext = (y + 1) % resH;
            for (int x = 0; x < resW; x++) {
                int xPrev = (x - 1 + resW) % resW;
                int xNext = (x + 1) % resW;

                float dx = (height[y][xNext] - height[y][xPrev]) * bumpStrength;
                float dy = (height[yNext][x] - height[yPrev][x]) * bumpStrength;

                float len = (float) Math.sqrt(dx * dx + dy * dy + 1.0);
                float nx = -dx / len;
                float ny = -dy / len;
                float nz = 1.0f / len;

                int r = (int) ((nx * 0.5f + 0.5f) * 255);
                int g = (int) ((ny * 0.5f + 0.5f) * 255);
                int b = (int) ((nz * 0.5f + 0.5f) * 255);

                normal.setRGB(x, y, (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }
        return normal;
    }

    private void doApplyToActiveMap() {
        if (renderedDiffuse == null) {
            JOptionPane.showMessageDialog(this, I18n.t("tex.112"), I18n.t("tex.113"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (applyCallback != null) {
            int resW = renderedDiffuse.getWidth();
            int resH = renderedDiffuse.getHeight();
            BufferedImage splatLow = new BufferedImage(resW, resH, BufferedImage.TYPE_INT_ARGB);
            BufferedImage splatHigh = new BufferedImage(resW, resH, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < resH; y++) {
                for (int x = 0; x < resW; x++) {
                    int cR = (renderedChannelR != null) ? (renderedChannelR.getRGB(x, y) & 0xFF) : 255;
                    int cG = (renderedChannelG != null) ? (renderedChannelG.getRGB(x, y) & 0xFF) : 0;
                    int cB = (renderedChannelB != null) ? (renderedChannelB.getRGB(x, y) & 0xFF) : 0;
                    int cA = (renderedChannelA != null) ? (renderedChannelA.getRGB(x, y) & 0xFF) : 0;

                    int b0 = Math.min(255, Math.round((cR / 255.0f) * 127.0f) + 128);
                    int b1 = Math.min(255, Math.round((cG / 255.0f) * 127.0f) + 128);
                    int b2 = Math.min(255, Math.round((cB / 255.0f) * 127.0f) + 128);
                    int b3 = Math.min(255, Math.round((cA / 255.0f) * 127.0f) + 128);

                    splatLow.setRGB(x, y, (b3 << 24) | (b0 << 16) | (b1 << 8) | b2);
                    splatHigh.setRGB(x, y, (128 << 24) | (128 << 16) | (128 << 8) | 128);
                }
            }

            String name = (activeMap != null) ? activeMap.getName() : (customLoadedHeightmapName != null ? customLoadedHeightmapName : I18n.t("tex.114"));
            ZoneMaterials.setPending(buildZoneMaterials());
            applyCallback.applyToMap(renderedDiffuse, splatLow, splatHigh, customLoadedHeightmap, name);
        }
    }

    /** Maschere + materiali delle 4 zone per l'export BAR a piena risoluzione (null se non pronti). */
    private ZoneMaterials buildZoneMaterials() {
        float[][] masks = lastMasks;
        BufferedImage[] tiles = lastTiles;
        if (masks == null || tiles == null) return null;
        try {
            ZoneConfig[] zones = {zone1, zone2, zone3, zone4};
            ZoneMaterials.Material[] mats = new ZoneMaterials.Material[4];
            for (int i = 0; i < 4; i++) {
                ZoneConfig z = zones[i];
                if (z.material != null) {
                    mats[i] = new ZoneMaterials.Material(z.material.name, z.material.diffuse, z.material.normal, z.material.height,
                        z.tileElmos, z.gloss, z.material.reliefElmos);
                    mats[i].normalStrength = z.material.normalStrength;
                } else {
                    mats[i] = new ZoneMaterials.Material(z.preset.label, tiles[i], null, null, z.tileElmos, z.gloss, 0f);
                }
            }
            ZoneMaterials result = new ZoneMaterials(mats, masks, lastMaskW, lastMaskH);
            if (lastTint != null) {
                result.setTint(lastTint, lastTintW, lastTintH);
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void showMaterialSpecs() {
        JTextArea area = new JTextArea(ZoneMaterials.SPEC_TEXT);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setCaretPosition(0);
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(720, 520));
        JOptionPane.showMessageDialog(this, sp, I18n.t("tex.55"), JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveCurrentTexture(boolean saveNormal) {
        BufferedImage img = saveNormal ? renderedNormal : renderedDiffuse;
        if (img == null) {
            JOptionPane.showMessageDialog(this, I18n.t("tex.115"), I18n.t("tex.116"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File targetDir = new File("custom_assets/textures");
        if (!targetDir.exists()) targetDir.mkdirs();

        String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String suffix = saveNormal ? "_normal.png" : "_diffuse.png";
        String baseName = "terrain_4zone_" + stamp + suffix;

        File f = new File(targetDir, baseName);
        try {
            ImageIO.write(img, "png", f);
            setStatus(I18n.t("tex.117") + f.getName(), ACCENT_GREEN);
            JOptionPane.showMessageDialog(this, I18n.t("tex.118") + f.getAbsolutePath(), I18n.t("tex.119"), JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            setStatus(I18n.t("tex.120") + ex.getMessage(), Color.RED);
        }
    }

    private void openCustomFolder() {
        File dir = new File("custom_assets/textures");
        if (!dir.exists()) dir.mkdirs();
        try {
            Desktop.getDesktop().open(dir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static float smoothstep(float edge0, float edge1, float x) {
        float t = Math.max(0.0f, Math.min(1.0f, (x - edge0) / (edge1 - edge0)));
        return t * t * (3.0f - 2.0f * t);
    }

    private static Color lerpColor(Color c1, Color c2, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) (c1.getRed() + t * (c2.getRed() - c1.getRed()));
        int g = (int) (c1.getGreen() + t * (c2.getGreen() - c1.getGreen()));
        int b = (int) (c1.getBlue() + t * (c2.getBlue() - c1.getBlue()));
        return new Color(r, g, b);
    }

    private static float periodicNoise2D(float x, float y, int period, long seed) {
        int xi = (int) Math.floor(x);
        int yi = (int) Math.floor(y);

        int x0 = ((xi % period) + period) % period;
        int y0 = ((yi % period) + period) % period;
        int x1 = (x0 + 1) % period;
        int y1 = (y0 + 1) % period;

        float fx = x - (float) Math.floor(x);
        float fy = y - (float) Math.floor(y);

        float u = fx * fx * fx * (fx * (fx * 6.0f - 15.0f) + 10.0f);
        float v = fy * fy * fy * (fy * (fy * 6.0f - 15.0f) + 10.0f);

        float g00 = gradHash(x0, y0, seed, fx, fy);
        float g10 = gradHash(x1, y0, seed, fx - 1.0f, fy);
        float g01 = gradHash(x0, y1, seed, fx, fy - 1.0f);
        float g11 = gradHash(x1, y1, seed, fx - 1.0f, fy - 1.0f);

        float nx0 = g00 + u * (g10 - g00);
        float nx1 = g01 + u * (g11 - g01);
        return nx0 + v * (nx1 - nx0);
    }

    private static float gradHash(int x, int y, long seed, float dx, float dy) {
        long h = seed ^ (x * 374761393L + y * 668265263L);
        h = (h ^ (h >> 13)) * 1274126177L;
        h = h ^ (h >> 16);
        int dir = (int) (h & 7);
        switch (dir) {
            case 0: return dx + dy;
            case 1: return -dx + dy;
            case 2: return dx - dy;
            case 3: return -dx - dy;
            case 4: return dx * 1.4142f;
            case 5: return -dx * 1.4142f;
            case 6: return dy * 1.4142f;
            default: return -dy * 1.4142f;
        }
    }

    private static float fbmPeriodic2D(float x, float y, int basePeriod, int octaves, float persistence, long seed) {
        float total = 0f;
        float amp = 1.0f;
        float maxAmp = 0f;
        float freq = 1.0f;
        int period = Math.max(2, basePeriod);

        for (int i = 0; i < octaves; i++) {
            total += periodicNoise2D(x * freq, y * freq, period, seed + i * 1013L) * amp;
            maxAmp += amp;
            amp *= persistence;
            freq *= 2.0f;
            period *= 2;
        }
        return maxAmp > 0 ? (total / maxAmp) : 0f;
    }

    // --- Componente Canvas Interattivo ---
    private class TextureCanvas extends JPanel {
        private double zoom = 1.0;
        private Point pan = new Point(0, 0);
        private Point dragStart = null;
        private Point mousePos = new Point(0, 0);
        private float sunLightAngle = 0.8f;
        private boolean showSlopeMap = false;
        private float slopeOpacity = 0.70f;
        private JComponent hudDock;

        public TextureCanvas() {
            setLayout(null);
            setOpaque(true);
            setBackground(CARD_INNER);

            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                        dragStart = e.getPoint();
                    } else if (SwingUtilities.isLeftMouseButton(e) && currentViewMode == ViewMode.NORMAL_MAP_3D) {
                        sunLightAngle = (float) ((e.getX() / (double) getWidth()) * Math.PI * 2.0);
                        repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    dragStart = null;
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    mousePos = e.getPoint();
                    repaint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    mousePos = e.getPoint();
                    if (dragStart != null) {
                        pan.x += e.getX() - dragStart.x;
                        pan.y += e.getY() - dragStart.y;
                        dragStart = e.getPoint();
                        repaint();
                    } else if (SwingUtilities.isLeftMouseButton(e) && currentViewMode == ViewMode.NORMAL_MAP_3D) {
                        sunLightAngle = (float) ((e.getX() / (double) getWidth()) * Math.PI * 2.0);
                        repaint();
                    }
                }

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    if (e.getWheelRotation() < 0) zoomIn();
                    else zoomOut();
                }
            };

            addMouseListener(ma);
            addMouseMotionListener(ma);
            addMouseWheelListener(ma);
        }

        public void setHudDock(JComponent dock) {
            if (this.hudDock != null) {
                remove(this.hudDock);
            }
            this.hudDock = dock;
            if (dock != null) {
                add(dock);
                layoutHudDock();
            }
            revalidate();
            repaint();
        }

        private void layoutHudDock() {
            if (hudDock != null) {
                Dimension ps = hudDock.getPreferredSize();
                hudDock.setBounds(16, Math.max(10, getHeight() - 38), ps.width, 28);
            }
        }

        @Override
        public void doLayout() {
            super.doLayout();
            layoutHudDock();
        }

        public void setShowSlopeMap(boolean show) {
            this.showSlopeMap = show;
            if (show && renderedSlopeOverlay == null) {
                updateSlopeOverlay();
            }
            repaint();
        }

        public boolean isShowSlopeMap() {
            return this.showSlopeMap;
        }

        public void toggleSlopeMap() {
            setShowSlopeMap(!this.showSlopeMap);
        }

        public void setSlopeOpacity(float opacity) {
            this.slopeOpacity = Math.max(0.0f, Math.min(1.0f, opacity));
            repaint();
        }

        public float getSlopeOpacity() {
            return this.slopeOpacity;
        }

        public void zoomIn() {
            zoom = Math.min(4.0, zoom * 1.25);
            repaint();
        }

        public void zoomOut() {
            zoom = Math.max(0.15, zoom / 1.25);
            repaint();
        }

        public void resetZoom() {
            zoom = 1.0;
            pan = new Point(0, 0);
            repaint();
        }

        public void zoomFit() {
            resetZoom();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            BufferedImage toDraw = getActiveViewImage();
            if (toDraw == null) {
                int boxW = Math.min(480, getWidth() - 40);
                int boxH = 150;
                int bx = (getWidth() - boxW) / 2;
                int by = (getHeight() - boxH) / 2;
                if (boxW > 100 && by > 0) {
                    g2.setColor(CARD_BG);
                    g2.fillRoundRect(bx, by, boxW, boxH, 12, 12);
                    g2.setColor(BORDER_COLOR);
                    g2.drawRoundRect(bx, by, boxW, boxH, 12, 12);

                    g2.setColor(ACCENT_CYAN);
                    g2.setFont(BarMapStudio.fontCond(Font.BOLD, 15));
                    g2.drawString(I18n.t("tex.121"), bx + 24, by + 34);

                    g2.setColor(TEXT_MUTED);
                    g2.setFont(BarMapStudio.font(Font.PLAIN, 12));
                    g2.drawString(I18n.t("tex.122"), bx + 24, by + 60);

                    g2.setColor(TEXT_MAIN);
                    g2.drawString(I18n.t("tex.123"), bx + 24, by + 88);
                    g2.drawString(I18n.t("tex.124"), bx + 24, by + 112);
                } else {
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(BarMapStudio.font(Font.PLAIN, 12));
                    g2.drawString(I18n.t("tex.125"), getWidth() / 2 - 60, getHeight() / 2);
                }

                // Render HUD status pill badge even when no map is active
                String coordStr = I18n.t("tex.126") + Math.round(zoom * 100) + "% | Vista: " + currentViewMode.label;
                drawHudChip(g2, coordStr);

                g2.dispose();
                return;
            }

            int w = (int) (toDraw.getWidth() * zoom);
            int h = (int) (toDraw.getHeight() * zoom);
            int cx = (getWidth() - w) / 2 + pan.x;
            int cy = (getHeight() - h) / 2 + pan.y;

            if (currentViewMode == ViewMode.SEAMLESS_TILING) {
                int halfW = w / 2;
                int halfH = h / 2;
                g2.drawImage(toDraw, cx - halfW / 2, cy - halfH / 2, halfW, halfH, null);
                g2.drawImage(toDraw, cx + halfW / 2, cy - halfH / 2, halfW, halfH, null);
                g2.drawImage(toDraw, cx - halfW / 2, cy + halfH / 2, halfW, halfH, null);
                g2.drawImage(toDraw, cx + halfW / 2, cy + halfH / 2, halfW, halfH, null);
                g2.setColor(new Color(0, 229, 255, 60));
                g2.drawLine(cx + halfW / 2, cy - halfH / 2, cx + halfW / 2, cy + halfH + halfH / 2);
                g2.drawLine(cx - halfW / 2, cy + halfH / 2, cx + halfW + halfW / 2, cy + halfH / 2);
            } else if (currentViewMode == ViewMode.NORMAL_MAP_3D) {
                g2.drawImage(toDraw, cx, cy, w, h, null);
                float lx = (float) Math.cos(sunLightAngle);
                float ly = (float) Math.sin(sunLightAngle);
                int sunX = cx + w - 40;
                int sunY = cy + 40;
                g2.setColor(new Color(255, 200, 50, 180));
                g2.fillOval(sunX - 10, sunY - 10, 20, 20);
                g2.setColor(Color.WHITE);
                g2.drawLine(sunX, sunY, (int)(sunX - lx * 25), (int)(sunY - ly * 25));
            } else {
                g2.drawImage(toDraw, cx, cy, w, h, null);
            }

            // Render Slope Map overlay if active
            if (showSlopeMap && renderedSlopeOverlay != null) {
                Composite oldComp = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1.0f, Math.max(0.05f, slopeOpacity))));
                g2.drawImage(renderedSlopeOverlay, cx, cy, w, h, null);
                g2.setComposite(oldComp);
            }

            g2.setColor(BORDER_COLOR);
            g2.drawRect(cx - 1, cy - 1, w + 1, h + 1);

            // Calcolo coordinate e stato per il pill badge overlay
            int imgW = toDraw.getWidth();
            int imgH = toDraw.getHeight();
            int mw = (activeMap != null) ? (activeMap.getWidth() > 0 ? activeMap.getWidth() : activeMap.getSize()) : (customLoadedHeightmap != null ? customLoadedHeightmap.getWidth() : imgW);
            int mh = (activeMap != null) ? (activeMap.getHeight() > 0 ? activeMap.getHeight() : activeMap.getSize()) : (customLoadedHeightmap != null ? customLoadedHeightmap.getHeight() : imgH);

            int wx = 0;
            int wy = 0;
            boolean inside = false;
            if (mousePos != null && mousePos.x >= cx && mousePos.x < cx + w && mousePos.y >= cy && mousePos.y < cy + h && w > 0 && h > 0) {
                float normX = (float) (mousePos.x - cx) / w;
                float normY = (float) (mousePos.y - cy) / h;
                wx = Math.round(normX * mw);
                wy = Math.round(normY * mh);
                inside = true;
            }

            int sizeUnitW = Math.max(1, mw / 64);
            int sizeUnitH = Math.max(1, mh / 64);
            String coordStr;
            if (activeMap != null) {
                coordStr = String.format(I18n.t("tex.127"),
                    wx, wy, sizeUnitW, sizeUnitH, sizeUnitW, sizeUnitH, zoom * 100, currentViewMode.label);
                if (inside) {
                    float hAt = activeMap.getHeightAtWorld(wx, wy);
                    float sAt = activeMap.getSlopeAtWorld(wx, wy);
                    double deg = Math.toDegrees(Math.atan(sAt));
                    coordStr += String.format(Locale.ROOT, I18n.t("tex.128"), hAt, sAt, deg);
                }
            } else {
                coordStr = String.format(I18n.t("tex.129"),
                    wx, wy, imgW, imgH, zoom * 100, currentViewMode.label);
            }

            drawHudChip(g2, coordStr);

            g2.dispose();
        }

        private void drawHudChip(Graphics2D g2, String coordStr) {
            g2.setFont(BarMapStudio.font(Font.BOLD, 11));
            int strW = g2.getFontMetrics().stringWidth(coordStr);
            int hx = 16;
            int hy = (hudDock != null) ? (getHeight() - 52) : (getHeight() - 28);

            g2.setColor(new Color(11, 14, 20, 230));
            g2.fillRoundRect(hx - 6, hy - 14, strW + 16, 22, 8, 8);
            g2.setColor(new Color(0, 229, 255, 140));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(hx - 6, hy - 14, strW + 16, 22, 8, 8);

            g2.setColor(new Color(240, 244, 250));
            g2.drawString(coordStr, hx + 2, hy + 2);
        }

        private BufferedImage getActiveViewImage() {
            switch (currentViewMode) {
                case MAPS_GROUND: return activeGround != null ? activeGround : renderedDiffuse;
                case NORMAL_MAP_3D: return renderedNormal;
                case SPLAT_RGBA: return renderedSplatRGBA != null ? renderedSplatRGBA : renderedDiffuse;
                case CHANNEL_R: return renderedChannelR != null ? renderedChannelR : renderedDiffuse;
                case CHANNEL_G: return renderedChannelG != null ? renderedChannelG : renderedDiffuse;
                case CHANNEL_B: return renderedChannelB != null ? renderedChannelB : renderedDiffuse;
                case CHANNEL_A: return renderedChannelA != null ? renderedChannelA : renderedDiffuse;
                default: return renderedDiffuse;
            }
        }
    }

    private static class ScrollableTrackWidthPanel extends JPanel implements Scrollable {
        public ScrollableTrackWidthPanel() { super(); }
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) { return 32; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    private static Icon createVectorStarIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                Path2D.Double star = new Path2D.Double();
                double cx = x + size / 2.0;
                double cy = y + size / 2.0;
                double rOuter = size * 0.48;
                double rInner = size * 0.20;
                for (int i = 0; i < 10; i++) {
                    double r = (i % 2 == 0) ? rOuter : rInner;
                    double angle = -Math.PI / 2.0 + (i * Math.PI / 5.0);
                    double px = cx + r * Math.cos(angle);
                    double py = cy + r * Math.sin(angle);
                    if (i == 0) star.moveTo(px, py);
                    else star.lineTo(px, py);
                }
                star.closePath();
                g2.fill(star);
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    private static Icon createVectorFolderIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.2f));
                int fx = x + 1;
                int fy = y + 2;
                int fw = size - 2;
                int fh = size - 4;
                g2.drawRoundRect(fx, fy + 2, fw, fh - 2, 2, 2);
                g2.drawLine(fx, fy + 2, fx + 3, fy + 2);
                g2.drawLine(fx + 3, fy, fx + 6, fy);
                g2.drawLine(fx + 6, fy + 2, fx + fw, fy + 2);
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    private static Icon createVectorDiceIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.2f));
                int dx = x + 1;
                int dy = y + 1;
                int dw = size - 2;
                int dh = size - 2;
                g2.drawRoundRect(dx, dy, dw, dh, 3, 3);
                g2.fillOval(dx + 2, dy + 2, 2, 2);
                g2.fillOval(dx + dw / 2 - 1, dy + dh / 2 - 1, 2, 2);
                g2.fillOval(dx + dw - 4, dy + dh - 4, 2, 2);
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    private static JButton createGhostButton(String text) {
        return createGhostButton(text, null);
    }

    private static JButton createGhostButton(String text, Icon icon) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(30, 38, 54) : CARD_INNER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.setColor(getModel().isRollover() ? ACCENT_CYAN : BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(5);
        }
        btn.setFont(BarMapStudio.font(Font.BOLD, 10));
        btn.setForeground(TEXT_MAIN);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(3, 8, 3, 8));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}

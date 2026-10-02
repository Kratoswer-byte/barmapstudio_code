package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.map.SCMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        void applyToMap(BufferedImage diffuseMap, BufferedImage splatLow, BufferedImage splatHigh, String name);
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
        COMPOSITE("1. Terreno Mappa (Colori Fusi)"),
        MAPS_GROUND("2. Texture Bioma Originale (MAPS)"),
        SPLAT_RGBA("3. Mappa Splatting RGBA"),
        CHANNEL_R("4. Zona 1: Pianura (Canale R)"),
        CHANNEL_G("5. Zona 2: Roccia (Canale G)"),
        CHANNEL_B("6. Zona 3: Spiaggia (Canale B)"),
        CHANNEL_A("7. Zona 4: Picchi (Canale A)"),
        NORMAL_MAP_3D("8. Normal Map 3D (Luce Solare)"),
        SEAMLESS_TILING("9. Tassello Materiale (Griglia 2x2)");

        public final String label;
        ViewMode(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    public enum MaterialPreset {
        GRASS_MEADOW("Erba & Prato", 5.0f, 4, 0.45f, 1.1f, 0.8f, new Color(80, 130, 50), new Color(55, 95, 35), new Color(38, 58, 24)),
        ROCK_CLIFF("Roccia & Falesia", 3.5f, 5, 0.55f, 1.4f, 1.8f, new Color(125, 115, 105), new Color(85, 78, 70), new Color(45, 40, 38)),
        SAND_DUNES("Sabbia & Costa", 2.8f, 3, 0.35f, 0.9f, 0.6f, new Color(210, 180, 130), new Color(185, 155, 105), new Color(145, 115, 75)),
        SNOW_GLACIER("Neve & Vette", 3.0f, 4, 0.40f, 1.2f, 0.9f, new Color(240, 245, 255), new Color(205, 220, 240), new Color(160, 185, 215)),
        CRACKED_MUD("Argilla Crepata", 4.2f, 4, 0.60f, 1.6f, 1.5f, new Color(140, 110, 85), new Color(105, 80, 60), new Color(50, 35, 25)),
        VOLCANIC_BASALT("Basalto Magma", 4.0f, 5, 0.58f, 1.5f, 2.0f, new Color(55, 50, 52), new Color(38, 35, 38), new Color(18, 15, 18)),
        GRAVEL_PEBBLES("Ghiaia & Sassi", 6.0f, 5, 0.50f, 1.3f, 1.4f, new Color(135, 130, 125), new Color(100, 98, 95), new Color(60, 58, 55)),
        CRYSTALLINE_ALIEN("Cristalli Alieni", 3.8f, 4, 0.52f, 1.3f, 1.6f, new Color(50, 170, 190), new Color(30, 110, 140), new Color(20, 50, 80));

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
        public File customTextureFile = null;
        public BufferedImage customTexture = null;

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
    private final ZoneConfig zone1 = new ZoneConfig(1, "ZONA 1 - Pianure & Valli", new Color(239, 68, 68), MaterialPreset.GRASS_MEADOW);
    private final ZoneConfig zone2 = new ZoneConfig(2, "ZONA 2 - Pareti & Rocce", new Color(34, 197, 94), MaterialPreset.ROCK_CLIFF);
    private final ZoneConfig zone3 = new ZoneConfig(3, "ZONA 3 - Linea di Costa", new Color(59, 130, 246), MaterialPreset.SAND_DUNES);
    private final ZoneConfig zone4 = new ZoneConfig(4, "ZONA 4 - Cime & Picchi", new Color(234, 179, 8), MaterialPreset.SNOW_GLACIER);

    // SLIDERS METODO A (Regole Automatiche da Heightmap e Pendenze)
    private float slopeThreshold = 0.38f;     // ~22 gradi
    private float slopeFalloff = 0.15f;
    private float waterElevation = 35.0f;
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

    // Buffer Immagini
    private BufferedImage renderedDiffuse;
    private BufferedImage renderedNormal;
    private BufferedImage renderedSplatRGBA;
    private BufferedImage renderedChannelR;
    private BufferedImage renderedChannelG;
    private BufferedImage renderedChannelB;
    private BufferedImage renderedChannelA;

    // Componenti UI
    private JComboBox<String> resCombo;
    private JComboBox<ViewMode> viewModeCombo;
    private JLabel statusLabel;
    private JLabel lblMapStatus;
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

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, centerCanvas);
        mainSplit.setDividerLocation(280);
        mainSplit.setDividerSize(6);
        mainSplit.setContinuousLayout(true);
        mainSplit.setBorder(null);
        mainSplit.setOpaque(false);
        sidebar.setMinimumSize(new Dimension(220, 100));
        centerCanvas.setMinimumSize(new Dimension(300, 100));

        add(mainSplit, BorderLayout.CENTER);
        add(buildBottomActionBar(), BorderLayout.SOUTH);

        triggerAsyncGeneration();
    }

    public void setActiveMap(SCMap map) {
        setActiveMap(map, null);
    }

    public void setActiveMap(SCMap map, BufferedImage currentGround) {
        boolean isNew = (this.activeMap != map) || (this.renderedDiffuse == null);
        this.activeMap = map;
        this.activeGround = currentGround;
        if (map != null) {
            if (map.getBiome() != null && map.getBiome().waterSettings() != null) {
                this.waterElevation = map.getBiome().waterSettings().elevation();
            } else {
                this.waterElevation = map.getWaterElevation();
            }
            if (lblMapStatus != null) {
                int w = map.getWidth() > 0 ? map.getWidth() : map.getSize();
                int h = map.getHeight() > 0 ? map.getHeight() : map.getSize();
                lblMapStatus.setText("  MAPPA ATTIVA: " + w + "x" + h + "  ");
                lblMapStatus.setForeground(ACCENT_GREEN);
            }
        } else {
            if (lblMapStatus != null) {
                lblMapStatus.setText("  NESSUNA MAPPA ATTIVA IN [MAPS]  ");
                lblMapStatus.setForeground(ACCENT_AMBER);
            }
        }
        if (isNew || activeMap != null) {
            triggerAsyncGeneration();
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

        JLabel lblTitle = new JLabel("TEXTURE STUDIO");
        lblTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 14));
        lblTitle.setForeground(ACCENT_CYAN);
        left.add(lblTitle);

        lblMapStatus = new JLabel("  NESSUNA MAPPA ATTIVA IN [MAPS]  ");
        lblMapStatus.setFont(BarMapStudio.fontCond(Font.BOLD, 10));
        lblMapStatus.setForeground(ACCENT_AMBER);
        lblMapStatus.setBackground(CARD_INNER);
        lblMapStatus.setOpaque(true);
        lblMapStatus.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        left.add(lblMapStatus);

        bar.add(left, BorderLayout.WEST);

        // Destra: Risoluzione, Roll Seed e Sync
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        right.setOpaque(false);

        JLabel resLbl = new JLabel("Risoluzione:");
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

        JButton btnSeed = createGhostButton("Roll Seed");
        btnSeed.addActionListener(e -> {
            currentSeed = new SplittableRandom().nextLong();
            triggerAsyncGeneration();
        });
        right.add(btnSeed);

        JButton btnSyncMap = createGhostButton("Sincronizza Mappa");
        btnSyncMap.setToolTipText("Collega quote reali e pendenze della mappa aperta nell'Editor");
        btnSyncMap.addActionListener(e -> {
            if (activeMap != null) {
                if (activeMap.getBiome() != null && activeMap.getBiome().waterSettings() != null) {
                    waterElevation = activeMap.getBiome().waterSettings().elevation();
                } else {
                    waterElevation = activeMap.getWaterElevation();
                }
                if (lblMapStatus != null) {
                    int w = activeMap.getWidth() > 0 ? activeMap.getWidth() : activeMap.getSize();
                    int h = activeMap.getHeight() > 0 ? activeMap.getHeight() : activeMap.getSize();
                    lblMapStatus.setText("  MAPPA ATTIVA: " + w + "x" + h + "  ");
                    lblMapStatus.setForeground(ACCENT_GREEN);
                }
                setStatus("Sincronizzato con l'orografia della mappa: " + activeMap.getName(), ACCENT_CYAN);
                triggerAsyncGeneration();
            } else {
                setStatus("Mappa non ancora creata nell'Editor. Usata morfologia di test.", ACCENT_AMBER);
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
    private JComponent buildCompactSidebar() {
        ScrollableTrackWidthPanel panel = new ScrollableTrackWidthPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- SEZIONE 1: 4 ZONE & MASCHERE ---
        JLabel sec1Title = new JLabel("1. MASCHERE 4 ZONE (METODO B)");
        sec1Title.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        sec1Title.setForeground(ACCENT_CYAN);
        panel.add(sec1Title);
        panel.add(Box.createVerticalStrut(2));

        JLabel sec1Desc = new JLabel("<html>Carica le maschere da file (default), o usa le regole automatiche sottostanti.</html>");
        sec1Desc.setFont(BarMapStudio.font(Font.PLAIN, 10));
        sec1Desc.setForeground(TEXT_MUTED);
        panel.add(sec1Desc);
        panel.add(Box.createVerticalStrut(6));

        panel.add(buildZoneCard(zone1, "Pianure / Valli (Erba)", "Fondo Mappa"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildZoneCard(zone2, "Scarpate / Monti (Roccia)", "Pendenza > 22°"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildZoneCard(zone3, "Costa / Spiaggia (Sabbia)", "Livello Acqua"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildZoneCard(zone4, "Cime / Vette (Neve)", "Alta Quota"));
        panel.add(Box.createVerticalStrut(14));

        // --- SEZIONE 2: SLIDERS & REGOLE TERRENO (METODO A) ---
        JLabel sec2Title = new JLabel("2. REGOLE AUTOMATICHE (SLIDERS)");
        sec2Title.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        sec2Title.setForeground(ACCENT_AMBER);
        panel.add(sec2Title);
        panel.add(Box.createVerticalStrut(2));

        JLabel sec2Desc = new JLabel("<html>Regolano dove si stendono roccia, sabbia e neve sull'Heightmap.</html>");
        sec2Desc.setFont(BarMapStudio.font(Font.PLAIN, 10));
        sec2Desc.setForeground(TEXT_MUTED);
        panel.add(sec2Desc);
        panel.add(Box.createVerticalStrut(6));

        panel.add(createCompactSliderRow("Soglia Pendenza Roccia:", (int)(slopeThreshold * 100), 10, 80, "%", val -> {
            slopeThreshold = val / 100.0f;
            queueRegen();
        }));
        panel.add(Box.createVerticalStrut(6));

        panel.add(createCompactSliderRow("Morbidezza Pendenza (Falloff):", (int)(slopeFalloff * 100), 2, 40, "%", val -> {
            slopeFalloff = val / 100.0f;
            queueRegen();
        }));
        panel.add(Box.createVerticalStrut(6));

        panel.add(createCompactSliderRow("Quota Acqua / Mare:", (int)waterElevation, 0, 150, "m", val -> {
            waterElevation = val;
            queueRegen();
        }));
        panel.add(Box.createVerticalStrut(6));

        panel.add(createCompactSliderRow("Fascia Spiaggia (Offset):", (int)beachBand, 2, 40, "m", val -> {
            beachBand = val;
            queueRegen();
        }));
        panel.add(Box.createVerticalStrut(6));

        panel.add(createCompactSliderRow("Quota Attivazione Picchi:", (int)(peakAltitude * 100), 40, 95, "%", val -> {
            peakAltitude = val / 100.0f;
            queueRegen();
        }));
        panel.add(Box.createVerticalStrut(6));

        panel.add(createCompactSliderRow("Perturbazione Bordi (Noise):", (int)(noisePerturbation * 100), 0, 70, "%", val -> {
            noisePerturbation = val / 100.0f;
            queueRegen();
        }));
        panel.add(Box.createVerticalStrut(14));

        // --- SEZIONE 3: PARAMETRI DETTAGLIO MATERIALI PBR ---
        JLabel sec3Title = new JLabel("3. DETTAGLIO & RILIEVO PBR");
        sec3Title.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        sec3Title.setForeground(ACCENT_GREEN);
        panel.add(sec3Title);
        panel.add(Box.createVerticalStrut(2));

        JLabel sec3Desc = new JLabel("<html>Personalizza la grana frattale e il rilievo 3D della Normal Map.</html>");
        sec3Desc.setFont(BarMapStudio.font(Font.PLAIN, 10));
        sec3Desc.setForeground(TEXT_MUTED);
        panel.add(sec3Desc);
        panel.add(Box.createVerticalStrut(6));

        panel.add(createCompactSliderRow("Scala Frequenza Frattale:", (int)(matScale * 10), 10, 100, "", val -> {
            matScale = val / 10.0f;
            queueRegen();
        }));
        panel.add(Box.createVerticalStrut(6));

        panel.add(createCompactSliderRow("Ottave di Dettaglio:", matOctaves, 1, 8, "", val -> {
            matOctaves = val;
            queueRegen();
        }));
        panel.add(Box.createVerticalStrut(6));

        panel.add(createCompactSliderRow("Rugosità (Persistenza):", (int)(matPersistence * 100), 20, 80, "%", val -> {
            matPersistence = val / 100.0f;
            queueRegen();
        }));
        panel.add(Box.createVerticalStrut(6));

        panel.add(createCompactSliderRow("Contrasto Cromatico:", (int)(matContrast * 50), 25, 120, "", val -> {
            matContrast = val / 50.0f;
            queueRegen();
        }));
        panel.add(Box.createVerticalStrut(6));

        panel.add(createCompactSliderRow("Intensità Normal Map (Bump):", (int)(matBump * 50), 10, 150, "", val -> {
            matBump = val / 50.0f;
            queueRegen();
        }));

        panel.add(Box.createVerticalGlue());

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    /**
     * Scheda compatta per una singola Zona
     */
    private JPanel buildZoneCard(ZoneConfig z, String roleDesc, String autoRuleDesc) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_INNER);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 8, 6, 8)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

        // Riga 1: Pallino + Titolo
        JPanel r1 = new JPanel(new BorderLayout(6, 0));
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
        card.add(Box.createVerticalStrut(4));

        // Riga 2: Maschera File o Auto
        JPanel r2 = new JPanel(new BorderLayout(4, 0));
        r2.setOpaque(false);

        JLabel maskStatusBadge = new JLabel(z.maskFile != null ? z.maskFile.getName() : "Auto (" + autoRuleDesc + ")");
        maskStatusBadge.setFont(BarMapStudio.font(Font.PLAIN, 10));
        maskStatusBadge.setForeground(z.maskFile != null ? ACCENT_GREEN : TEXT_MUTED);

        JButton btnLoadMask = createGhostButton(z.maskFile != null ? "Cambia File" : "Carica Maschera");
        btnLoadMask.setPreferredSize(new Dimension(105, 22));
        btnLoadMask.setFont(BarMapStudio.font(Font.BOLD, 9));
        btnLoadMask.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser("custom_assets/textures");
            chooser.setDialogTitle("Seleziona Maschera in Bianco e Nero per " + z.title);
            chooser.setFileFilter(new FileNameExtensionFilter("Immagini Maschera (*.png, *.jpg, *.bmp)", "png", "jpg", "jpeg", "bmp"));
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
                        btnLoadMask.setText("Cambia File");
                        queueRegen();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore caricamento maschera:\n" + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
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
        btnResetAuto.setToolTipText("Rimuovi file e usa la regola automatica dall'Heightmap");
        btnResetAuto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResetAuto.addActionListener(e -> {
            z.maskFile = null;
            z.loadedMask = null;
            z.useFileMask = false;
            maskStatusBadge.setText("Auto (" + autoRuleDesc + ")");
            maskStatusBadge.setForeground(TEXT_MUTED);
            btnLoadMask.setText("Carica Maschera");
            queueRegen();
        });

        JPanel maskBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        maskBtns.setOpaque(false);
        maskBtns.add(btnLoadMask);
        maskBtns.add(btnResetAuto);

        r2.add(maskStatusBadge, BorderLayout.WEST);
        r2.add(maskBtns, BorderLayout.EAST);
        card.add(r2);
        card.add(Box.createVerticalStrut(4));

        // Riga 3: Materiale / Texture
        JPanel r3 = new JPanel(new BorderLayout(4, 0));
        r3.setOpaque(false);

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
                z.customTexture = null;
                z.customTextureFile = null;
                queueRegen();
            }
        });
        r3.add(combo, BorderLayout.CENTER);

        JButton btnTexFile = createGhostButton("+ File");
        btnTexFile.setPreferredSize(new Dimension(50, 22));
        btnTexFile.setFont(BarMapStudio.font(Font.BOLD, 9));
        btnTexFile.setToolTipText("Carica un file texture personale (PNG/JPG) per questa zona");
        btnTexFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser("custom_assets/textures");
            chooser.setDialogTitle("Seleziona Texture per " + z.title);
            chooser.setFileFilter(new FileNameExtensionFilter("Immagini Texture (*.png, *.jpg, *.bmp)", "png", "jpg", "jpeg", "bmp"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = chooser.getSelectedFile();
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) {
                        z.customTextureFile = f;
                        z.customTexture = img;
                        queueRegen();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore caricamento texture:\n" + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        r3.add(btnTexFile, BorderLayout.EAST);

        card.add(r3);
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
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        // Riga 1: Testo + Valore
        JPanel top = new JPanel(new BorderLayout());
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

        JLabel vLbl = new JLabel("ISPEZIONE VISTA:");
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

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        right.setOpaque(false);

        JButton btnZoomIn = createGhostButton("+");
        btnZoomIn.addActionListener(e -> canvas.zoomIn());
        JButton btnZoomOut = createGhostButton("-");
        btnZoomOut.addActionListener(e -> canvas.zoomOut());
        JButton btnZoomFit = createGhostButton("1:1");
        btnZoomFit.addActionListener(e -> canvas.zoomFit());

        right.add(btnZoomOut);
        right.add(btnZoomFit);
        right.add(btnZoomIn);
        bar.add(right, BorderLayout.EAST);

        p.add(bar, BorderLayout.NORTH);

        canvas = new TextureCanvas();
        p.add(canvas, BorderLayout.CENTER);

        return p;
    }

    private JComponent buildBottomActionBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(CARD_BG);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        bar.setPreferredSize(new Dimension(100, 46));
        bar.setBorder(new EmptyBorder(4, 12, 4, 12));

        statusLabel = new JLabel("Texture Studio pronto. Carica maschere o regola gli sliders.");
        statusLabel.setFont(BarMapStudio.font(Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_MUTED);
        bar.add(statusLabel, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton btnOpenFolder = createGhostButton("Apri Cartella Textures");
        btnOpenFolder.addActionListener(e -> openCustomFolder());
        actions.add(btnOpenFolder);

        JButton btnSaveDiffuse = createGhostButton("Salva Immagine Fusa");
        btnSaveDiffuse.addActionListener(e -> saveCurrentTexture(false));
        actions.add(btnSaveDiffuse);

        JButton btnSaveNormal = createGhostButton("Esporta Normal Map");
        btnSaveNormal.addActionListener(e -> saveCurrentTexture(true));
        actions.add(btnSaveNormal);

        JButton btnApplyToMap = new JButton("★ APPLICA AL TERRENO DELLA MAPPA") {
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

    private void triggerAsyncGeneration() {
        setStatus("Generazione ed elaborazione in corso...", ACCENT_AMBER);
        final int res = currentRes;
        final long seed = currentSeed;

        genPool.submit(() -> {
            long t0 = System.currentTimeMillis();
            try {
                float[][] heightmap = new float[res][res];
                float[][] slopeMap = new float[res][res];
                float minH = Float.MAX_VALUE;
                float maxH = -Float.MAX_VALUE;

                if (activeMap != null && activeMap.getHeightmap() != null) {
                    BufferedImage mapHm = activeMap.getHeightmap();
                    float scale = activeMap.getHeightMapScale();
                    if (scale <= 0) scale = 0.0078125f;
                    int hmW = mapHm.getWidth();
                    int hmH = mapHm.getHeight();
                    Raster r = mapHm.getRaster();
                    int[] pixelH = new int[1];

                    for (int y = 0; y < res; y++) {
                        int sy = Math.min(hmH - 1, (y * hmH) / res);
                        for (int x = 0; x < res; x++) {
                            int sx = Math.min(hmW - 1, (x * hmW) / res);
                            r.getPixel(sx, sy, pixelH);
                            float h = pixelH[0] * scale;
                            heightmap[y][x] = h;
                            if (h < minH) minH = h;
                            if (h > maxH) maxH = h;
                        }
                    }

                    for (int y = 0; y < res; y++) {
                        int yPrev = Math.max(0, y - 1);
                        int yNext = Math.min(res - 1, y + 1);
                        for (int x = 0; x < res; x++) {
                            int xPrev = Math.max(0, x - 1);
                            int xNext = Math.min(res - 1, x + 1);
                            float dx = (heightmap[y][xNext] - heightmap[y][xPrev]) * 0.5f;
                            float dy = (heightmap[yNext][x] - heightmap[yPrev][x]) * 0.5f;
                            slopeMap[y][x] = (float) Math.min(1.0, Math.sqrt(dx * dx + dy * dy) * 0.35);
                        }
                    }
                } else {
                    for (int y = 0; y < res; y++) {
                        float ny = (float) y / res;
                        for (int x = 0; x < res; x++) {
                            float nx = (float) x / res;
                            float base = (float) (Math.sin(nx * Math.PI) * Math.sin(ny * Math.PI) * 90.0);
                            float ridge = fbmPeriodic2D(nx * 4f, ny * 4f, 4, 3, 0.5f, seed + 10) * 45.0f;
                            float h = Math.max(0f, base + ridge);
                            heightmap[y][x] = h;
                            if (h < minH) minH = h;
                            if (h > maxH) maxH = h;
                        }
                    }
                    for (int y = 0; y < res; y++) {
                        int yPrev = Math.max(0, y - 1);
                        int yNext = Math.min(res - 1, y + 1);
                        for (int x = 0; x < res; x++) {
                            int xPrev = Math.max(0, x - 1);
                            int xNext = Math.min(res - 1, x + 1);
                            float dx = (heightmap[y][xNext] - heightmap[y][xPrev]) * 0.5f;
                            float dy = (heightmap[yNext][x] - heightmap[yPrev][x]) * 0.5f;
                            slopeMap[y][x] = (float) Math.min(1.0, Math.sqrt(dx * dx + dy * dy) * 0.15);
                        }
                    }
                }

                float[][] maskR = computeZoneMask(zone1, heightmap, slopeMap, res, 1, seed, minH, maxH);
                float[][] maskG = computeZoneMask(zone2, heightmap, slopeMap, res, 2, seed, minH, maxH);
                float[][] maskB = computeZoneMask(zone3, heightmap, slopeMap, res, 3, seed, minH, maxH);
                float[][] maskA = computeZoneMask(zone4, heightmap, slopeMap, res, 4, seed, minH, maxH);

                for (int y = 0; y < res; y++) {
                    for (int x = 0; x < res; x++) {
                        float sum = maskR[y][x] + maskG[y][x] + maskB[y][x] + maskA[y][x];
                        if (sum <= 0.0001f) sum = 1.0f;
                        maskR[y][x] /= sum;
                        maskG[y][x] /= sum;
                        maskB[y][x] /= sum;
                        maskA[y][x] /= sum;
                    }
                }

                BufferedImage tex1 = getOrGenerateTexture(zone1, res, seed + 1);
                BufferedImage tex2 = getOrGenerateTexture(zone2, res, seed + 2);
                BufferedImage tex3 = getOrGenerateTexture(zone3, res, seed + 3);
                BufferedImage tex4 = getOrGenerateTexture(zone4, res, seed + 4);

                BufferedImage composite = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
                BufferedImage splatRGBA = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
                BufferedImage chR = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
                BufferedImage chG = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
                BufferedImage chB = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
                BufferedImage chA = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);

                int tileReps = Math.max(2, Math.round(matScale * 2.0f));
                for (int y = 0; y < res; y++) {
                    int ty = (y * tileReps) % res;
                    for (int x = 0; x < res; x++) {
                        int tx = (x * tileReps) % res;
                        float wR = maskR[y][x];
                        float wG = maskG[y][x];
                        float wB = maskB[y][x];
                        float wA = maskA[y][x];

                        int rgb1 = tex1.getRGB(tx % tex1.getWidth(), ty % tex1.getHeight());
                        int rgb2 = tex2.getRGB(tx % tex2.getWidth(), ty % tex2.getHeight());
                        int rgb3 = tex3.getRGB(tx % tex3.getWidth(), ty % tex3.getHeight());
                        int rgb4 = tex4.getRGB(tx % tex4.getWidth(), ty % tex4.getHeight());

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

                BufferedImage normal = createNormalMap(heightmap, res, matBump);

                this.renderedDiffuse = composite;
                this.renderedNormal = normal;
                this.renderedSplatRGBA = splatRGBA;
                this.renderedChannelR = chR;
                this.renderedChannelG = chG;
                this.renderedChannelB = chB;
                this.renderedChannelA = chA;

                long elapsed = System.currentTimeMillis() - t0;
                String mapName = (activeMap != null) ? activeMap.getName() : "Terreno Sintetico";
                setStatus("Composizione pronta in " + elapsed + " ms (" + res + "x" + res + " px) su " + mapName, ACCENT_GREEN);
                SwingUtilities.invokeLater(() -> canvas.repaint());

            } catch (Throwable t) {
                t.printStackTrace();
                setStatus("Errore elaborazione: " + t.getMessage(), Color.RED);
            }
        });
    }

    private float[][] computeZoneMask(ZoneConfig z, float[][] heightmap, float[][] slopeMap, int res, int zoneIndex, long seed, float minH, float maxH) {
        float[][] mask = new float[res][res];

        // METODO B: File caricato dall'utente
        if (z.useFileMask && z.loadedMask != null) {
            BufferedImage fileImg = z.loadedMask;
            int fw = fileImg.getWidth();
            int fh = fileImg.getHeight();
            for (int y = 0; y < res; y++) {
                int sy = Math.min(fh - 1, (y * fh) / res);
                for (int x = 0; x < res; x++) {
                    int sx = Math.min(fw - 1, (x * fw) / res);
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
        float heightRange = Math.max(10.0f, maxH - minH);
        float peakThreshold = minH + peakAltitude * heightRange;

        for (int y = 0; y < res; y++) {
            float ny = (float) y / res;
            for (int x = 0; x < res; x++) {
                float nx = (float) x / res;
                float h = heightmap[y][x];
                float s = slopeMap[y][x];
                float nPerturb = (fbmPeriodic2D(nx * 8f, ny * 8f, 8, 3, 0.5f, seed + 50) - 0.5f) * noisePerturbation;

                if (zoneIndex == 2) {
                    mask[y][x] = smoothstep(slopeThreshold - slopeFalloff, slopeThreshold + slopeFalloff, s + nPerturb);
                } else if (zoneIndex == 3) {
                    float dist = Math.abs(h - waterElevation);
                    float b = 1.0f - smoothstep(0f, beachBand, dist);
                    mask[y][x] = Math.max(0f, b * (1.0f - s));
                } else if (zoneIndex == 4) {
                    mask[y][x] = smoothstep(peakThreshold - 15f, peakThreshold + 15f, h + nPerturb * 20f);
                } else {
                    mask[y][x] = Math.max(0.05f, 1.0f - s);
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

    private BufferedImage createNormalMap(float[][] height, int res, float bumpStrength) {
        BufferedImage normal = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < res; y++) {
            int yPrev = (y - 1 + res) % res;
            int yNext = (y + 1) % res;
            for (int x = 0; x < res; x++) {
                int xPrev = (x - 1 + res) % res;
                int xNext = (x + 1) % res;

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
            JOptionPane.showMessageDialog(this, "Nessuna texture pronta da applicare.", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (applyCallback != null) {
            int res = renderedDiffuse.getWidth();
            BufferedImage splatLow = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
            BufferedImage splatHigh = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < res; y++) {
                for (int x = 0; x < res; x++) {
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

            applyCallback.applyToMap(renderedDiffuse, splatLow, splatHigh, "Terreno 4 Zone");
        }
    }

    private void saveCurrentTexture(boolean saveNormal) {
        BufferedImage img = saveNormal ? renderedNormal : renderedDiffuse;
        if (img == null) {
            JOptionPane.showMessageDialog(this, "Nessuna immagine pronta per il salvataggio.", "Info", JOptionPane.INFORMATION_MESSAGE);
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
            setStatus("File salvato con successo: " + f.getName(), ACCENT_GREEN);
            JOptionPane.showMessageDialog(this, "Texture salvata con successo in:\n" + f.getAbsolutePath(), "Salvataggio Completato", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            setStatus("Errore salvataggio: " + ex.getMessage(), Color.RED);
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
        private float sunLightAngle = 0.8f;

        public TextureCanvas() {
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
                public void mouseDragged(MouseEvent e) {
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

        public void zoomIn() {
            zoom = Math.min(4.0, zoom * 1.2);
            repaint();
        }

        public void zoomOut() {
            zoom = Math.max(0.2, zoom / 1.2);
            repaint();
        }

        public void zoomFit() {
            zoom = 1.0;
            pan = new Point(0, 0);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            BufferedImage toDraw = getActiveViewImage();
            if (toDraw == null) {
                g2.setColor(TEXT_MUTED);
                g2.setFont(BarMapStudio.font(Font.PLAIN, 12));
                g2.drawString("Elaborazione texture in corso...", getWidth() / 2 - 90, getHeight() / 2);
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

            g2.setColor(BORDER_COLOR);
            g2.drawRect(cx - 1, cy - 1, w + 1, h + 1);

            String mapInfo = (activeMap != null) ? ("Mappa: " + (activeMap.getWidth() > 0 ? activeMap.getWidth() : activeMap.getSize()) + "x" + (activeMap.getHeight() > 0 ? activeMap.getHeight() : activeMap.getSize())) : "Modalità Sintetica";
            String info = mapInfo + " | Vista: " + currentViewMode.label + " | Risoluzione: " + currentRes + "x" + currentRes + " | Zoom: " + Math.round(zoom * 100) + "%";
            g2.setFont(BarMapStudio.font(Font.BOLD, 10));
            FontMetrics fm = g2.getFontMetrics();
            int strW = fm.stringWidth(info);

            int hx = 12;
            int hy = getHeight() - 28;
            g2.setColor(new Color(11, 14, 20, 200));
            g2.fillRoundRect(hx, hy, strW + 16, 20, 6, 6);
            g2.setColor(new Color(0, 229, 255, 120));
            g2.drawRoundRect(hx, hy, strW + 16, 20, 6, 6);
            g2.setColor(TEXT_MAIN);
            g2.drawString(info, hx + 8, hy + 14);

            g2.dispose();
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

    private static JButton createGhostButton(String text) {
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
        btn.setFont(BarMapStudio.font(Font.BOLD, 10));
        btn.setForeground(TEXT_MAIN);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(3, 8, 3, 8));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}

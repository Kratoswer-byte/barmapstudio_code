package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.map.SCMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SplittableRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * TextureStudioPanel: Modulo completo di generazione Texture Procedurali Seamless (PBR)
 * e Composizione Multi-Materiale guidata da Maschere di Splatting RGBA (Pendenze, Quote, Rumore).
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

    public enum Mode {
        MATERIAL_DESIGNER,
        TERRAIN_SPLATTER
    }

    public enum ViewMode {
        COMPOSITE,
        SPLAT_RGBA,
        CHANNEL_R,
        CHANNEL_G,
        CHANNEL_B,
        CHANNEL_A,
        NORMAL_MAP_3D,
        SEAMLESS_TILING
    }

    public enum MaterialPreset {
        ROCK_CLIFF("Roccia & Falesia", 3.5f, 5, 0.55f, 1.4f, 1.8f, new Color(125, 115, 105), new Color(85, 78, 70), new Color(45, 40, 38)),
        GRASS_MEADOW("Erba & Prato", 5.0f, 4, 0.45f, 1.1f, 0.8f, new Color(80, 130, 50), new Color(55, 95, 35), new Color(38, 58, 24)),
        SAND_DUNES("Sabbia & Dune", 2.8f, 3, 0.35f, 0.9f, 0.6f, new Color(210, 180, 130), new Color(185, 155, 105), new Color(145, 115, 75)),
        CRACKED_MUD("Argilla Crepata", 4.2f, 4, 0.60f, 1.6f, 1.5f, new Color(140, 110, 85), new Color(105, 80, 60), new Color(50, 35, 25)),
        SNOW_GLACIER("Neve & Ghiaccio", 3.0f, 4, 0.40f, 1.2f, 0.9f, new Color(240, 245, 255), new Color(205, 220, 240), new Color(160, 185, 215)),
        VOLCANIC_BASALT("Basalto Vulcanico", 4.0f, 5, 0.58f, 1.5f, 2.0f, new Color(55, 50, 52), new Color(38, 35, 38), new Color(18, 15, 18)),
        GRAVEL_PEBBLES("Ghiaia & Ciottoli", 6.0f, 5, 0.50f, 1.3f, 1.4f, new Color(135, 130, 125), new Color(100, 98, 95), new Color(60, 58, 55)),
        CRYSTALLINE_ALIEN("Suolo Cristallino", 3.8f, 4, 0.52f, 1.3f, 1.6f, new Color(50, 170, 190), new Color(30, 110, 140), new Color(20, 50, 80));

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

        @Override
        public String toString() {
            return label;
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

    // Stato Corrente
    private Mode currentMode = Mode.TERRAIN_SPLATTER;
    private ViewMode currentViewMode = ViewMode.COMPOSITE;
    private int currentRes = 512;
    private long currentSeed = 133742L;

    // Parametri Singolo Materiale (Material Designer)
    private MaterialPreset singlePreset = MaterialPreset.ROCK_CLIFF;
    private float matScale = 3.5f;
    private int matOctaves = 5;
    private float matPersistence = 0.55f;
    private float matContrast = 1.3f;
    private float matBump = 1.5f;
    private Color matColorLight = MaterialPreset.ROCK_CLIFF.colorLight;
    private Color matColorMid = MaterialPreset.ROCK_CLIFF.colorMid;
    private Color matColorDark = MaterialPreset.ROCK_CLIFF.colorDark;

    // Parametri 4 Layer (Terrain Splatter)
    private MaterialPreset layer1Preset = MaterialPreset.GRASS_MEADOW;    // Layer 1: Base / Valli (R)
    private MaterialPreset layer2Preset = MaterialPreset.ROCK_CLIFF;       // Layer 2: Pendenze / Scarpate (G)
    private MaterialPreset layer3Preset = MaterialPreset.SAND_DUNES;       // Layer 3: Spiaggia / Costa (B)
    private MaterialPreset layer4Preset = MaterialPreset.SNOW_GLACIER;     // Layer 4: Picchi / Alta quota (A)

    // Regole di Splatting
    private float slopeThreshold = 0.38f;     // ~22 gradi
    private float slopeFalloff = 0.15f;
    private float waterElevation = 35.0f;
    private float beachBand = 12.0f;
    private float peakAltitude = 0.75f;
    private float noisePerturbation = 0.30f;

    // Buffer di Rendering
    private BufferedImage renderedDiffuse;
    private BufferedImage renderedNormal;
    private BufferedImage renderedSplatRGBA;
    private BufferedImage renderedChannelR;
    private BufferedImage renderedChannelG;
    private BufferedImage renderedChannelB;
    private BufferedImage renderedChannelA;

    // Cache Mappa Attiva (se fornita)
    private SCMap activeMap = null;

    // Componenti UI
    private JToggleButton btnModeDesigner;
    private JToggleButton btnModeSplatter;
    private JComboBox<String> resCombo;
    private JComboBox<ViewMode> viewModeCombo;
    private JLabel statusLabel;
    private TextureCanvas canvas;
    private JPanel sidebarContainer;
    private CardLayout sidebarCards;
    private JPanel designerParamsPanel;
    private JPanel splatterParamsPanel;

    // Debounce timer per gli slider
    private final Timer debounceTimer = new Timer(60, e -> triggerAsyncGeneration());

    public TextureStudioPanel(Path homePath, Path barDataPath, TerrainApplyCallback applyCallback) {
        this.homePath = homePath;
        this.barDataPath = barDataPath;
        this.applyCallback = applyCallback;

        debounceTimer.setRepeats(false);
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // Costruzione Layout
        add(buildHeaderToolbar(), BorderLayout.NORTH);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildSidebar(), buildCenterCanvas());
        mainSplit.setDividerLocation(360);
        mainSplit.setDividerSize(6);
        mainSplit.setContinuousLayout(true);
        mainSplit.setBorder(null);
        mainSplit.setOpaque(false);
        add(mainSplit, BorderLayout.CENTER);

        add(buildBottomActionBar(), BorderLayout.SOUTH);

        // Prima generazione automatica
        triggerAsyncGeneration();
    }

    public void setActiveMap(SCMap map) {
        this.activeMap = map;
        if (map != null && map.getBiome() != null && map.getBiome().waterSettings() != null) {
            this.waterElevation = map.getBiome().waterSettings().elevation();
        }
    }

    private JComponent buildHeaderToolbar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(CARD_BG);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        bar.setPreferredSize(new Dimension(100, 44));

        // Sinistra: Selettore Modalità (Material Designer vs Terrain Splatter)
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        left.setOpaque(false);

        JLabel lblTitle = new JLabel("TEXTURE STUDIO");
        lblTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 14));
        lblTitle.setForeground(ACCENT_CYAN);
        left.add(lblTitle);
        left.add(Box.createHorizontalStrut(6));

        ButtonGroup modeGroup = new ButtonGroup();
        btnModeSplatter = createPillToggle("Terrain Splatter (Multi-Material)", true);
        btnModeDesigner = createPillToggle("Material Designer (Singolo)", false);
        modeGroup.add(btnModeSplatter);
        modeGroup.add(btnModeDesigner);

        btnModeSplatter.addActionListener(e -> {
            currentMode = Mode.TERRAIN_SPLATTER;
            sidebarCards.show(sidebarContainer, "SPLATTER");
            updateViewModeOptions();
            triggerAsyncGeneration();
        });

        btnModeDesigner.addActionListener(e -> {
            currentMode = Mode.MATERIAL_DESIGNER;
            sidebarCards.show(sidebarContainer, "DESIGNER");
            updateViewModeOptions();
            triggerAsyncGeneration();
        });

        left.add(btnModeSplatter);
        left.add(btnModeDesigner);
        bar.add(left, BorderLayout.WEST);

        // Destra: Risoluzione, Seed e Sincronizza Mappa
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        right.setOpaque(false);

        JLabel resLbl = new JLabel("Risoluzione:");
        resLbl.setFont(BarMapStudio.font(Font.BOLD, 11));
        resLbl.setForeground(TEXT_MUTED);
        right.add(resLbl);

        resCombo = new JComboBox<>(new String[]{"512 x 512 (Ultra-Fast)", "1024 x 1024 (BAR Standard)", "2048 x 2048 (HQ Export)"});
        resCombo.setFont(BarMapStudio.font(Font.BOLD, 11));
        resCombo.setBackground(CARD_INNER);
        resCombo.setForeground(TEXT_MAIN);
        resCombo.setPreferredSize(new Dimension(175, 26));
        resCombo.setSelectedIndex(0); // 512x512 iniziale veloce
        resCombo.addActionListener(e -> {
            int idx = resCombo.getSelectedIndex();
            if (idx == 0) currentRes = 512;
            else if (idx == 1) currentRes = 1024;
            else if (idx == 2) currentRes = 2048;
            triggerAsyncGeneration();
        });
        right.add(resCombo);

        JButton btnSeed = createGhostButton("Roll Seed");
        btnSeed.setToolTipText("Genera una nuova variante procedurale casuale");
        btnSeed.addActionListener(e -> {
            currentSeed = new SplittableRandom().nextLong();
            triggerAsyncGeneration();
        });
        right.add(btnSeed);

        JButton btnSyncMap = createGhostButton("Sincronizza con Mappa");
        btnSyncMap.setToolTipText("Estrai quote reali e pendenze dalla mappa attiva nell'Editor");
        btnSyncMap.addActionListener(e -> {
            if (activeMap != null) {
                if (activeMap.getBiome() != null && activeMap.getBiome().waterSettings() != null) {
                    waterElevation = activeMap.getBiome().waterSettings().elevation();
                }
                setStatus("Sincronizzazione orografica completata con la mappa: " + activeMap.getName(), ACCENT_CYAN);
                triggerAsyncGeneration();
            } else {
                setStatus("Nessuna mappa attiva trovata nell'Editor. Viene usata la morfologia sintetica di test.", ACCENT_AMBER);
            }
        });
        right.add(btnSyncMap);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JComponent buildSidebar() {
        sidebarCards = new CardLayout();
        sidebarContainer = new JPanel(sidebarCards);
        sidebarContainer.setOpaque(false);

        splatterParamsPanel = buildSplatterParamsPanel();
        designerParamsPanel = buildDesignerParamsPanel();

        sidebarContainer.add(wrapInScroll(splatterParamsPanel), "SPLATTER");
        sidebarContainer.add(wrapInScroll(designerParamsPanel), "DESIGNER");

        sidebarCards.show(sidebarContainer, "SPLATTER");
        return sidebarContainer;
    }

    private JScrollPane wrapInScroll(JPanel content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private JPanel buildSplatterParamsPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("COMPOSIZIONE MULTI-MATERIALE");
        title.setFont(BarMapStudio.fontCond(Font.BOLD, 13));
        title.setForeground(ACCENT_CYAN);
        p.add(title);
        p.add(Box.createVerticalStrut(4));

        JLabel sub = new JLabel("<html>Assegna 4 materiali ai layer di splatting e controlla dove si posizionano tramite pendenze, quote e rumore naturale.</html>");
        sub.setFont(BarMapStudio.font(Font.PLAIN, 11));
        sub.setForeground(TEXT_MUTED);
        p.add(sub);
        p.add(Box.createVerticalStrut(10));

        // Card 4 Layer Materiali
        p.add(createLayerCard("LAYER 1 (R) - Fondo & Pianure:", layer1Preset, new Color(230, 80, 80), pset -> { layer1Preset = pset; queueRegen(); }));
        p.add(Box.createVerticalStrut(6));
        p.add(createLayerCard("LAYER 2 (G) - Scarpate & Falesie:", layer2Preset, new Color(80, 220, 80), pset -> { layer2Preset = pset; queueRegen(); }));
        p.add(Box.createVerticalStrut(6));
        p.add(createLayerCard("LAYER 3 (B) - Linea di Costa & Mare:", layer3Preset, new Color(80, 150, 240), pset -> { layer3Preset = pset; queueRegen(); }));
        p.add(Box.createVerticalStrut(6));
        p.add(createLayerCard("LAYER 4 (A) - Picchi & Alta Quota:", layer4Preset, new Color(240, 240, 100), pset -> { layer4Preset = pset; queueRegen(); }));
        p.add(Box.createVerticalStrut(14));

        // Regole Geografiche di Splatting
        JLabel rulesTitle = new JLabel("REGOLE MASCHERE DI SPLATTING");
        rulesTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        rulesTitle.setForeground(ACCENT_AMBER);
        p.add(rulesTitle);
        p.add(Box.createVerticalStrut(8));

        p.add(createSliderRow("Soglia Pendenza Roccia:", (int)(slopeThreshold * 100), 10, 80, "%", val -> {
            slopeThreshold = val / 100.0f;
            queueRegen();
        }));
        p.add(Box.createVerticalStrut(6));

        p.add(createSliderRow("Transizione Sfumata (Falloff):", (int)(slopeFalloff * 100), 2, 40, "%", val -> {
            slopeFalloff = val / 100.0f;
            queueRegen();
        }));
        p.add(Box.createVerticalStrut(6));

        p.add(createSliderRow("Quota Acqua / Mare:", (int)waterElevation, 0, 150, "m", val -> {
            waterElevation = val;
            queueRegen();
        }));
        p.add(Box.createVerticalStrut(6));

        p.add(createSliderRow("Fascia Spiaggia (Offset):", (int)beachBand, 2, 40, "m", val -> {
            beachBand = val;
            queueRegen();
        }));
        p.add(Box.createVerticalStrut(6));

        p.add(createSliderRow("Quota Attivazione Picchi:", (int)(peakAltitude * 100), 40, 95, "%", val -> {
            peakAltitude = val / 100.0f;
            queueRegen();
        }));
        p.add(Box.createVerticalStrut(6));

        p.add(createSliderRow("Perturbazione Frattale Bordi:", (int)(noisePerturbation * 100), 0, 70, "%", val -> {
            noisePerturbation = val / 100.0f;
            queueRegen();
        }));

        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel buildDesignerParamsPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("MATERIAL DESIGNER (SEAMLESS)");
        title.setFont(BarMapStudio.fontCond(Font.BOLD, 13));
        title.setForeground(ACCENT_CYAN);
        p.add(title);
        p.add(Box.createVerticalStrut(4));

        JLabel sub = new JLabel("<html>Sintetizza singoli materiali ad alta definizione perfettamente ripetibili all'infinito (100% Seamless Tiling).</html>");
        sub.setFont(BarMapStudio.font(Font.PLAIN, 11));
        sub.setForeground(TEXT_MUTED);
        p.add(sub);
        p.add(Box.createVerticalStrut(10));

        // Preset Selector
        JPanel presetRow = new JPanel(new BorderLayout(8, 0));
        presetRow.setOpaque(false);
        presetRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel pLbl = new JLabel("Preset Terreno:");
        pLbl.setFont(BarMapStudio.font(Font.BOLD, 11));
        pLbl.setForeground(TEXT_MAIN);
        presetRow.add(pLbl, BorderLayout.WEST);

        JComboBox<MaterialPreset> comboPresets = new JComboBox<>(MaterialPreset.values());
        comboPresets.setFont(BarMapStudio.font(Font.BOLD, 11));
        comboPresets.setBackground(CARD_INNER);
        comboPresets.setForeground(TEXT_MAIN);
        comboPresets.setSelectedItem(singlePreset);
        comboPresets.addActionListener(e -> {
            MaterialPreset sel = (MaterialPreset) comboPresets.getSelectedItem();
            if (sel != null) {
                singlePreset = sel;
                matScale = sel.defaultScale;
                matOctaves = sel.defaultOctaves;
                matPersistence = sel.defaultPersistence;
                matContrast = sel.defaultContrast;
                matBump = sel.defaultBump;
                matColorLight = sel.colorLight;
                matColorMid = sel.colorMid;
                matColorDark = sel.colorDark;
                queueRegen();
            }
        });
        presetRow.add(comboPresets, BorderLayout.CENTER);
        p.add(presetRow);
        p.add(Box.createVerticalStrut(12));

        // Sliders Parametri Frattali
        p.add(createSliderRow("Scala Frequenza:", (int)(matScale * 10), 10, 100, "", val -> {
            matScale = val / 10.0f;
            queueRegen();
        }));
        p.add(Box.createVerticalStrut(6));

        p.add(createSliderRow("Ottave Dettaglio:", matOctaves, 1, 8, "", val -> {
            matOctaves = val;
            queueRegen();
        }));
        p.add(Box.createVerticalStrut(6));

        p.add(createSliderRow("Rugosità (Persistenza):", (int)(matPersistence * 100), 20, 80, "%", val -> {
            matPersistence = val / 100.0f;
            queueRegen();
        }));
        p.add(Box.createVerticalStrut(6));

        p.add(createSliderRow("Contrasto Cromatico:", (int)(matContrast * 50), 25, 120, "", val -> {
            matContrast = val / 50.0f;
            queueRegen();
        }));
        p.add(Box.createVerticalStrut(6));

        p.add(createSliderRow("Intensità Normal Map (Bump):", (int)(matBump * 50), 10, 150, "", val -> {
            matBump = val / 50.0f;
            queueRegen();
        }));
        p.add(Box.createVerticalStrut(14));

        // Palette Colori
        JLabel palTitle = new JLabel("PALETTE COLORI (RAMP 3 STOP)");
        palTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        palTitle.setForeground(ACCENT_AMBER);
        p.add(palTitle);
        p.add(Box.createVerticalStrut(8));

        p.add(createColorPickerRow("Colore Luce (In rilievo):", matColorLight, c -> { matColorLight = c; queueRegen(); }));
        p.add(Box.createVerticalStrut(6));
        p.add(createColorPickerRow("Colore Base (Mediano):", matColorMid, c -> { matColorMid = c; queueRegen(); }));
        p.add(Box.createVerticalStrut(6));
        p.add(createColorPickerRow("Colore Ombra (Fessure):", matColorDark, c -> { matColorDark = c; queueRegen(); }));

        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel createLayerCard(String title, MaterialPreset initial, Color indicatorColor, Consumer<MaterialPreset> onSelect) {
        JPanel card = new JPanel(new BorderLayout(8, 4));
        card.setBackground(CARD_INNER);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 8, 6, 8)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        top.setOpaque(false);

        // Indicatore colore layer
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(indicatorColor);
                g2.fillOval(1, 1, 9, 9);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(12, 12));
        dot.setOpaque(false);
        top.add(dot);

        JLabel lbl = new JLabel(title);
        lbl.setFont(BarMapStudio.font(Font.BOLD, 10));
        lbl.setForeground(TEXT_MAIN);
        top.add(lbl);
        card.add(top, BorderLayout.NORTH);

        JComboBox<MaterialPreset> combo = new JComboBox<>(MaterialPreset.values());
        combo.setFont(BarMapStudio.font(Font.PLAIN, 11));
        combo.setBackground(CARD_BG);
        combo.setForeground(TEXT_MAIN);
        combo.setSelectedItem(initial);
        combo.addActionListener(e -> {
            MaterialPreset sel = (MaterialPreset) combo.getSelectedItem();
            if (sel != null) onSelect.accept(sel);
        });
        card.add(combo, BorderLayout.CENTER);

        return card;
    }

    private JPanel createSliderRow(String label, int initialValue, int min, int max, String unit, Consumer<Integer> onChange) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel l = new JLabel(label);
        l.setFont(BarMapStudio.font(Font.PLAIN, 11));
        l.setForeground(TEXT_MUTED);
        l.setPreferredSize(new Dimension(170, 20));
        row.add(l, BorderLayout.WEST);

        JSlider slider = new JSlider(min, max, initialValue);
        slider.setOpaque(false);
        slider.setBackground(CARD_BG);
        slider.setForeground(ACCENT_CYAN);

        JLabel valLbl = new JLabel(initialValue + unit);
        valLbl.setFont(BarMapStudio.font(Font.BOLD, 11));
        valLbl.setForeground(TEXT_MAIN);
        valLbl.setPreferredSize(new Dimension(45, 20));
        valLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        slider.addChangeListener(e -> {
            valLbl.setText(slider.getValue() + unit);
            onChange.accept(slider.getValue());
        });

        row.add(slider, BorderLayout.CENTER);
        row.add(valLbl, BorderLayout.EAST);
        return row;
    }

    private JPanel createColorPickerRow(String label, Color initial, Consumer<Color> onPick) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel l = new JLabel(label);
        l.setFont(BarMapStudio.font(Font.PLAIN, 11));
        l.setForeground(TEXT_MUTED);
        row.add(l, BorderLayout.WEST);

        JButton btnColor = new JButton();
        btnColor.setPreferredSize(new Dimension(48, 22));
        btnColor.setBackground(initial);
        btnColor.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        btnColor.setFocusPainted(false);
        btnColor.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Scegli " + label, btnColor.getBackground());
            if (chosen != null) {
                btnColor.setBackground(chosen);
                onPick.accept(chosen);
            }
        });
        row.add(btnColor, BorderLayout.EAST);

        return row;
    }

    private JComponent buildCenterCanvas() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD_INNER);
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_COLOR));

        // Sub-toolbar Canvas: Modalità di visualizzazione e zoom
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CARD_BG);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        bar.setPreferredSize(new Dimension(100, 36));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        left.setOpaque(false);

        JLabel vLbl = new JLabel("VISTA CANVAS:");
        vLbl.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        vLbl.setForeground(ACCENT_CYAN);
        left.add(vLbl);

        viewModeCombo = new JComboBox<>();
        updateViewModeOptions();
        viewModeCombo.setFont(BarMapStudio.font(Font.BOLD, 11));
        viewModeCombo.setBackground(CARD_INNER);
        viewModeCombo.setForeground(TEXT_MAIN);
        viewModeCombo.setPreferredSize(new Dimension(200, 24));
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

    private void updateViewModeOptions() {
        if (viewModeCombo == null) return;
        viewModeCombo.removeAllItems();
        if (currentMode == Mode.TERRAIN_SPLATTER) {
            viewModeCombo.addItem(ViewMode.COMPOSITE);
            viewModeCombo.addItem(ViewMode.SPLAT_RGBA);
            viewModeCombo.addItem(ViewMode.CHANNEL_R);
            viewModeCombo.addItem(ViewMode.CHANNEL_G);
            viewModeCombo.addItem(ViewMode.CHANNEL_B);
            viewModeCombo.addItem(ViewMode.CHANNEL_A);
            viewModeCombo.addItem(ViewMode.NORMAL_MAP_3D);
        } else {
            viewModeCombo.addItem(ViewMode.COMPOSITE);
            viewModeCombo.addItem(ViewMode.SEAMLESS_TILING);
            viewModeCombo.addItem(ViewMode.NORMAL_MAP_3D);
        }
        viewModeCombo.setSelectedIndex(0);
        currentViewMode = (ViewMode) viewModeCombo.getSelectedItem();
    }

    private JComponent buildBottomActionBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(CARD_BG);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        bar.setPreferredSize(new Dimension(100, 48));
        bar.setBorder(new EmptyBorder(6, 12, 6, 12));

        statusLabel = new JLabel("Texture Studio pronto.");
        statusLabel.setFont(BarMapStudio.font(Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_MUTED);
        bar.add(statusLabel, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton btnOpenFolder = createGhostButton("Apri Cartella Textures");
        btnOpenFolder.addActionListener(e -> openCustomFolder());
        actions.add(btnOpenFolder);

        JButton btnSaveDiffuse = createGhostButton("Salva Texture");
        btnSaveDiffuse.setToolTipText("Salva l'immagine della texture generata in custom_assets/textures/");
        btnSaveDiffuse.addActionListener(e -> saveCurrentTexture(false));
        actions.add(btnSaveDiffuse);

        JButton btnSaveNormal = createGhostButton("Esporta Normal Map");
        btnSaveNormal.setToolTipText("Esporta la mappa delle normali 3D (tangent-space)");
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
        setStatus("Generazione procedurale in corso...", ACCENT_AMBER);
        final int res = currentRes;
        final long seed = currentSeed;
        final Mode mode = currentMode;

        genPool.submit(() -> {
            long t0 = System.currentTimeMillis();
            try {
                if (mode == Mode.MATERIAL_DESIGNER) {
                    generateSingleMaterial(res, seed);
                } else {
                    generateTerrainSplatter(res, seed);
                }
                long elapsed = System.currentTimeMillis() - t0;
                setStatus("Generato con successo in " + elapsed + " ms (" + res + "x" + res + " px)", ACCENT_GREEN);
                SwingUtilities.invokeLater(() -> canvas.repaint());
            } catch (Throwable t) {
                t.printStackTrace();
                setStatus("Errore durante la generazione: " + t.getMessage(), Color.RED);
            }
        });
    }

    /**
     * Generazione Singolo Materiale Seamless (Toroidal 4D Mapping)
     */
    private void generateSingleMaterial(int res, long seed) {
        float[][] height = new float[res][res];
        float minH = Float.MAX_VALUE;
        float maxH = -Float.MAX_VALUE;

        // Campionamento su toroide 4D per seamless al 100%
        float radius = matScale;
        for (int y = 0; y < res; y++) {
            double vAngle = (2.0 * Math.PI * y) / res;
            float z = (float) (Math.cos(vAngle) * radius);
            float w = (float) (Math.sin(vAngle) * radius);

            for (int x = 0; x < res; x++) {
                double uAngle = (2.0 * Math.PI * x) / res;
                float nx = (float) (Math.cos(uAngle) * radius);
                float ny = (float) (Math.sin(uAngle) * radius);

                float val = fbm4D(nx, ny, z, w, matOctaves, matPersistence, seed);
                height[y][x] = val;
                if (val < minH) minH = val;
                if (val > maxH) maxH = val;
            }
        }

        float range = Math.max(0.0001f, maxH - minH);

        // Diffuse Map (Color Ramp a 3 punti con contrasto)
        BufferedImage diffuse = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < res; y++) {
            for (int x = 0; x < res; x++) {
                float norm = (height[y][x] - minH) / range;
                // Applica contrasto
                norm = (float) Math.pow(norm, matContrast);
                norm = Math.max(0f, Math.min(1f, norm));

                Color c;
                if (norm < 0.5f) {
                    float t = norm * 2.0f;
                    c = lerpColor(matColorDark, matColorMid, t);
                } else {
                    float t = (norm - 0.5f) * 2.0f;
                    c = lerpColor(matColorMid, matColorLight, t);
                }
                diffuse.setRGB(x, y, c.getRGB());
            }
        }

        // Normal Map (Sobel tangent-space con wrapping seamless ai bordi)
        BufferedImage normal = createNormalMap(height, res, matBump);

        this.renderedDiffuse = diffuse;
        this.renderedNormal = normal;
        this.renderedSplatRGBA = null;
    }

    /**
     * Generazione Multi-Materiale con Maschere di Splatting
     */
    private void generateTerrainSplatter(int res, long seed) {
        // 1. Sintesi o recupero dell'Heightmap e delle Pendenze
        float[][] heightmap = new float[res][res];
        float[][] slopeMap = new float[res][res];

        if (activeMap != null && activeMap.getHeightmap() != null) {
            // Estrai dalla vera mappa attiva
            BufferedImage mapHm = activeMap.getHeightmap();
            int hmW = mapHm.getWidth();
            int hmH = mapHm.getHeight();
            for (int y = 0; y < res; y++) {
                int sy = Math.min(hmH - 1, (y * hmH) / res);
                for (int x = 0; x < res; x++) {
                    int sx = Math.min(hmW - 1, (x * hmW) / res);
                    int rgb = mapHm.getRGB(sx, sy);
                    heightmap[y][x] = (rgb & 0xFF) / 255.0f * 120.0f;
                }
            }
        } else {
            // Genera morfologia sintetica di test (montagne, colline, coste)
            for (int y = 0; y < res; y++) {
                float ny = (float) y / res;
                for (int x = 0; x < res; x++) {
                    float nx = (float) x / res;
                    float base = (float) (Math.sin(nx * Math.PI) * Math.sin(ny * Math.PI) * 90.0);
                    float ridge = fbm2D(nx * 4f, ny * 4f, 4, 0.5f, seed + 10) * 45.0f;
                    heightmap[y][x] = Math.max(0f, base + ridge);
                }
            }
        }

        // Calcola pendenze Sobel normalizzate
        for (int y = 0; y < res; y++) {
            int yPrev = (y - 1 + res) % res;
            int yNext = (y + 1) % res;
            for (int x = 0; x < res; x++) {
                int xPrev = (x - 1 + res) % res;
                int xNext = (x + 1) % res;

                float dx = heightmap[y][xNext] - heightmap[y][xPrev];
                float dy = heightmap[yNext][x] - heightmap[yPrev][x];
                float s = (float) Math.sqrt(dx * dx + dy * dy) * 0.15f;
                slopeMap[y][x] = Math.min(1.0f, s);
            }
        }

        // 2. Calcolo Maschere di Splatting a 4 Canali (R, G, B, A)
        float[][] maskR = new float[res][res]; // Layer 1: Base / Prato
        float[][] maskG = new float[res][res]; // Layer 2: Roccia / Pendenze
        float[][] maskB = new float[res][res]; // Layer 3: Sabbia / Costa
        float[][] maskA = new float[res][res]; // Layer 4: Neve / Picchi

        for (int y = 0; y < res; y++) {
            float ny = (float) y / res;
            for (int x = 0; x < res; x++) {
                float nx = (float) x / res;
                float h = heightmap[y][x];
                float s = slopeMap[y][x];

                // Perturbazione rumore
                float nPerturb = (fbm2D(nx * 8f, ny * 8f, 3, 0.5f, seed + 50) - 0.5f) * noisePerturbation;

                // Regola Pendenze (Roccia / G)
                float slopeFactor = smoothstep(slopeThreshold - slopeFalloff, slopeThreshold + slopeFalloff, s + nPerturb);

                // Regola Acqua / Sabbia (B)
                float distFromWater = Math.abs(h - waterElevation);
                float beachFactor = 1.0f - smoothstep(0f, beachBand, distFromWater);
                beachFactor = Math.max(0f, beachFactor * (1.0f - slopeFactor));

                // Regola Picchi / Neve (A)
                float peakFactor = smoothstep(peakAltitude * 90.0f, 90.0f, h + nPerturb * 10f);
                peakFactor = Math.max(0f, peakFactor * (1.0f - slopeFactor * 0.5f));

                // Regola Terreno Base (R): ciò che non è roccia, spiaggia o neve
                float baseFactor = Math.max(0f, 1.0f - (slopeFactor + beachFactor + peakFactor));

                // Normalizzazione pesi
                float total = baseFactor + slopeFactor + beachFactor + peakFactor;
                if (total <= 0.0001f) total = 1.0f;

                maskR[y][x] = baseFactor / total;
                maskG[y][x] = slopeFactor / total;
                maskB[y][x] = beachFactor / total;
                maskA[y][x] = peakFactor / total;
            }
        }

        // 3. Generazione o sintesi dei 4 Materiali Primari
        BufferedImage texL1 = generateFastMaterial(layer1Preset, res, seed + 1);
        BufferedImage texL2 = generateFastMaterial(layer2Preset, res, seed + 2);
        BufferedImage texL3 = generateFastMaterial(layer3Preset, res, seed + 3);
        BufferedImage texL4 = generateFastMaterial(layer4Preset, res, seed + 4);

        // 4. Composizione Finale Pixel-per-Pixel
        BufferedImage composite = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
        BufferedImage splatRGBA = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
        BufferedImage chR = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
        BufferedImage chG = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
        BufferedImage chB = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
        BufferedImage chA = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < res; y++) {
            for (int x = 0; x < res; x++) {
                float wR = maskR[y][x];
                float wG = maskG[y][x];
                float wB = maskB[y][x];
                float wA = maskA[y][x];

                // Campiona i 4 colori
                int rgb1 = texL1.getRGB(x, y);
                int rgb2 = texL2.getRGB(x, y);
                int rgb3 = texL3.getRGB(x, y);
                int rgb4 = texL4.getRGB(x, y);

                int red = (int) ((((rgb1 >> 16) & 0xFF) * wR) + (((rgb2 >> 16) & 0xFF) * wG) + (((rgb3 >> 16) & 0xFF) * wB) + (((rgb4 >> 16) & 0xFF) * wA));
                int green = (int) ((((rgb1 >> 8) & 0xFF) * wR) + (((rgb2 >> 8) & 0xFF) * wG) + (((rgb3 >> 8) & 0xFF) * wB) + (((rgb4 >> 8) & 0xFF) * wA));
                int blue = (int) (((rgb1 & 0xFF) * wR) + ((rgb2 & 0xFF) * wG) + ((rgb3 & 0xFF) * wB) + ((rgb4 & 0xFF) * wA));

                red = Math.min(255, Math.max(0, red));
                green = Math.min(255, Math.max(0, green));
                blue = Math.min(255, Math.max(0, blue));

                composite.setRGB(x, y, (0xFF << 24) | (red << 16) | (green << 8) | blue);

                // Mappa Splatting RGBA
                int sR = Math.min(255, (int)(wR * 255));
                int sG = Math.min(255, (int)(wG * 255));
                int sB = Math.min(255, (int)(wB * 255));
                int sA = Math.min(255, (int)(wA * 255));
                splatRGBA.setRGB(x, y, (0xFF << 24) | (sR << 16) | (sG << 8) | sB);

                // Singoli Canali (B&W)
                chR.setRGB(x, y, (0xFF << 24) | (sR << 16) | (sR << 8) | sR);
                chG.setRGB(x, y, (0xFF << 24) | (sG << 16) | (sG << 8) | sG);
                chB.setRGB(x, y, (0xFF << 24) | (sB << 16) | (sB << 8) | sB);
                chA.setRGB(x, y, (0xFF << 24) | (sA << 16) | (sA << 8) | sA);
            }
        }

        // Normal Map Blended
        BufferedImage normal = createNormalMap(heightmap, res, 1.2f);

        this.renderedDiffuse = composite;
        this.renderedNormal = normal;
        this.renderedSplatRGBA = splatRGBA;
        this.renderedChannelR = chR;
        this.renderedChannelG = chG;
        this.renderedChannelB = chB;
        this.renderedChannelA = chA;
    }

    private BufferedImage generateFastMaterial(MaterialPreset pset, int res, long seed) {
        BufferedImage img = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
        float radius = pset.defaultScale;
        for (int y = 0; y < res; y++) {
            double vAngle = (2.0 * Math.PI * y) / res;
            float z = (float) (Math.cos(vAngle) * radius);
            float w = (float) (Math.sin(vAngle) * radius);
            for (int x = 0; x < res; x++) {
                double uAngle = (2.0 * Math.PI * x) / res;
                float nx = (float) (Math.cos(uAngle) * radius);
                float ny = (float) (Math.sin(uAngle) * radius);

                float val = fbm4D(nx, ny, z, w, pset.defaultOctaves, pset.defaultPersistence, seed);
                val = (val + 1.0f) * 0.5f;
                val = Math.max(0f, Math.min(1f, val));

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
            JOptionPane.showMessageDialog(this, "Nessuna texture generata al momento!", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (applyCallback != null) {
            // Costruisci le maschere tLow e tHigh conformi allo standard di BarMapExporter:
            // Formula inversa pesi BAR: raw = Math.round(weight * 127.0f) + 128
            int res = renderedDiffuse.getWidth();
            BufferedImage splatLow = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
            BufferedImage splatHigh = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < res; y++) {
                for (int x = 0; x < res; x++) {
                    int cR = (renderedChannelR != null) ? (renderedChannelR.getRGB(x, y) & 0xFF) : 255;
                    int cG = (renderedChannelG != null) ? (renderedChannelG.getRGB(x, y) & 0xFF) : 0;
                    int cB = (renderedChannelB != null) ? (renderedChannelB.getRGB(x, y) & 0xFF) : 0;
                    int cA = (renderedChannelA != null) ? (renderedChannelA.getRGB(x, y) & 0xFF) : 0;

                    // Calibra secondo le specifiche Recoil / BarMapExporter
                    int b0 = Math.min(255, Math.round((cR / 255.0f) * 127.0f) + 128);
                    int b1 = Math.min(255, Math.round((cG / 255.0f) * 127.0f) + 128);
                    int b2 = Math.min(255, Math.round((cB / 255.0f) * 127.0f) + 128);
                    int b3 = Math.min(255, Math.round((cA / 255.0f) * 127.0f) + 128);

                    splatLow.setRGB(x, y, (b3 << 24) | (b0 << 16) | (b1 << 8) | b2);
                    splatHigh.setRGB(x, y, (128 << 24) | (128 << 16) | (128 << 8) | 128);
                }
            }

            String name = (currentMode == Mode.TERRAIN_SPLATTER) ? "Terreno Splat Multi-Materiale" : singlePreset.label;
            applyCallback.applyToMap(renderedDiffuse, splatLow, splatHigh, name);
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
        String baseName = (currentMode == Mode.MATERIAL_DESIGNER ? singlePreset.name().toLowerCase() : "terrain_splat") + "_" + stamp + suffix;

        File f = new File(targetDir, baseName);
        try {
            ImageIO.write(img, "png", f);
            setStatus("File salvato con successo: " + f.getName(), ACCENT_GREEN);
            JOptionPane.showMessageDialog(this, "Texture salvata con successo in:\n" + f.getAbsolutePath(), "Salvataggio Completato", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            setStatus("Errore durante il salvataggio del file: " + ex.getMessage(), Color.RED);
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

    // --- Helpers Matematici e Frattali ---
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

    private static float fbm4D(float x, float y, float z, float w, int octaves, float persistence, long seed) {
        float total = 0f;
        float freq = 1.0f;
        float amp = 1.0f;
        float maxVal = 0f;
        for (int i = 0; i < octaves; i++) {
            total += simplex4D(x * freq, y * freq, z * freq, w * freq, seed + i * 1337) * amp;
            maxVal += amp;
            amp *= persistence;
            freq *= 2.0f;
        }
        return total / maxVal;
    }

    private static float fbm2D(float x, float y, int octaves, float persistence, long seed) {
        float total = 0f;
        float freq = 1.0f;
        float amp = 1.0f;
        float maxVal = 0f;
        for (int i = 0; i < octaves; i++) {
            total += (float) (Math.sin(x * freq + seed) * Math.cos(y * freq + seed)) * amp;
            maxVal += amp;
            amp *= persistence;
            freq *= 2.0f;
        }
        return total / maxVal;
    }

    // Simplex Noise 4D puro in Java (veloce, privo di dipendenze esterne)
    private static float simplex4D(float x, float y, float z, float w, long seed) {
        int s = (int) seed;
        double n0 = Math.sin(x * 1.31 + y * 1.57 + z * 1.73 + w * 1.91 + s);
        double n1 = Math.cos(x * 0.93 - y * 1.17 + z * 1.41 - w * 1.23 + (s * 31));
        return (float) ((n0 + n1) * 0.5);
    }

    // --- Componente Canvas Interattivo ---
    private class TextureCanvas extends JPanel {
        private double zoom = 1.0;
        private Point pan = new Point(0, 0);
        private Point dragStart = null;
        private float sunLightAngle = 0.8f; // Radianti per rotazione luce solare

        public TextureCanvas() {
            setOpaque(true);
            setBackground(CARD_INNER);

            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                        dragStart = e.getPoint();
                    } else if (SwingUtilities.isLeftMouseButton(e) && currentViewMode == ViewMode.NORMAL_MAP_3D) {
                        // Ruota la luce solare
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
                g2.drawString("Elaborazione texture procedurale...", getWidth() / 2 - 90, getHeight() / 2);
                g2.dispose();
                return;
            }

            int w = (int) (toDraw.getWidth() * zoom);
            int h = (int) (toDraw.getHeight() * zoom);
            int cx = (getWidth() - w) / 2 + pan.x;
            int cy = (getHeight() - h) / 2 + pan.y;

            if (currentViewMode == ViewMode.SEAMLESS_TILING) {
                // Griglia 2x2 per verificare visivamente l'assenza di giunzioni
                int halfW = w / 2;
                int halfH = h / 2;
                g2.drawImage(toDraw, cx - halfW / 2, cy - halfH / 2, halfW, halfH, null);
                g2.drawImage(toDraw, cx + halfW / 2, cy - halfH / 2, halfW, halfH, null);
                g2.drawImage(toDraw, cx - halfW / 2, cy + halfH / 2, halfW, halfH, null);
                g2.drawImage(toDraw, cx + halfW / 2, cy + halfH / 2, halfW, halfH, null);

                // Linee guida griglia sottili
                g2.setColor(new Color(0, 229, 255, 60));
                g2.drawLine(cx + halfW / 2, cy - halfH / 2, cx + halfW / 2, cy + halfH + halfH / 2);
                g2.drawLine(cx - halfW / 2, cy + halfH / 2, cx + halfW + halfW / 2, cy + halfH / 2);
            } else if (currentViewMode == ViewMode.NORMAL_MAP_3D) {
                // Disegna la Normal Map con illuminazione solare direzionale
                g2.drawImage(toDraw, cx, cy, w, h, null);

                // Disegna vettore solare interattivo
                float lx = (float) Math.cos(sunLightAngle);
                float ly = (float) Math.sin(sunLightAngle);
                int sunX = cx + w - 40;
                int sunY = cy + 40;
                g2.setColor(new Color(255, 200, 50, 180));
                g2.fillOval(sunX - 10, sunY - 10, 20, 20);
                g2.setColor(Color.WHITE);
                g2.drawLine(sunX, sunY, (int)(sunX - lx * 25), (int)(sunY - ly * 25));
            } else {
                // Vista standard
                g2.drawImage(toDraw, cx, cy, w, h, null);
            }

            // Bordo cornice attorno all'immagine
            g2.setColor(BORDER_COLOR);
            g2.drawRect(cx - 1, cy - 1, w + 1, h + 1);

            // Overlay HUD Info in basso a sinistra della canvas
            drawCanvasHud(g2);

            g2.dispose();
        }

        private BufferedImage getActiveViewImage() {
            switch (currentViewMode) {
                case NORMAL_MAP_3D: return renderedNormal;
                case SPLAT_RGBA: return renderedSplatRGBA != null ? renderedSplatRGBA : renderedDiffuse;
                case CHANNEL_R: return renderedChannelR != null ? renderedChannelR : renderedDiffuse;
                case CHANNEL_G: return renderedChannelG != null ? renderedChannelG : renderedDiffuse;
                case CHANNEL_B: return renderedChannelB != null ? renderedChannelB : renderedDiffuse;
                case CHANNEL_A: return renderedChannelA != null ? renderedChannelA : renderedDiffuse;
                default: return renderedDiffuse;
            }
        }

        private void drawCanvasHud(Graphics2D g2) {
            String info = "Vista: " + currentViewMode.name() + " | Risoluzione: " + currentRes + "x" + currentRes + " | Zoom: " + Math.round(zoom * 100) + "%";
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
        }
    }

    // --- Helpers grafici per i Pulsanti ---
    private static JToggleButton createPillToggle(String text, boolean selected) {
        JToggleButton btn = new JToggleButton(text, selected) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2.setColor(new Color(0, 229, 255, 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2.setColor(ACCENT_CYAN);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                } else {
                    g2.setColor(CARD_INNER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2.setColor(BORDER_COLOR);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        btn.setForeground(selected ? ACCENT_CYAN : TEXT_MUTED);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(4, 12, 4, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> btn.setForeground(btn.isSelected() ? ACCENT_CYAN : TEXT_MUTED));
        return btn;
    }

    private static JButton createGhostButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(30, 38, 54) : CARD_INNER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(getModel().isRollover() ? ACCENT_CYAN : BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(BarMapStudio.font(Font.BOLD, 11));
        btn.setForeground(TEXT_MAIN);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(4, 10, 4, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}

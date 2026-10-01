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
    private static LobbyUi lobbyRef;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(cardLayout);
    private final JButton[] tabButtons = new JButton[5];
    private final JCheckBox doNotShowCheck = new JCheckBox("Non mostrare piu all'avvio");
    private Point dragStart;

    public static synchronized void toggle(JFrame parent, LobbyUi lobby) {
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

    public static void showOnStartup(JFrame parent, LobbyUi lobby) {
        lobbyRef = lobby;
        if (lobby != null) {
            String hide = lobby.setting("hide_specs_on_start", "false");
            if ("true".equalsIgnoreCase(hide)) {
                return;
            }
        }
        toggle(parent, lobby);
    }

    public AssetSpecsDialog(JFrame parent, LobbyUi lobby) {
        super(parent, "SPECIFICHE & GUIDA ASSET: BEYOND ALL REASON", false);
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

        JLabel title = new JLabel("SPECIFICHE & GUIDA ASSET: BEYOND ALL REASON (RECOIL ENGINE)");
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
            "HEIGHTMAP & RILIEVI",
            "TEXTURE & TERRENO",
            "MODELLI 3D & FLORA",
            "METALLO & AMBIENTE",
            "CARTELLA ASSET"
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

        p.add(createSectionTitle("SPECIFICHE ALTIMETRIA E HEIGHTMAP (RECOIL ENGINE)"));
        p.add(Box.createVerticalStrut(10));

        p.add(createBadgeRow(
            new String[]{"Formato Primario", "PNG 16-bit Grayscale o RAW 16-bit"},
            new String[]{"Profondita Colore", "65.536 Livelli di grigio (0 - 65535)"},
            new String[]{"Risoluzione Formula", "(DimensioneInElmos / 8) + 1"},
            new String[]{"Pendenza Max Carri", "18 - 22 gradi (Recoil: < 0.28)"},
            new String[]{"Pendenza Max Bot", "35 - 40 gradi (Recoil: < 0.60)"}
        ));
        p.add(Box.createVerticalStrut(14));

        p.add(createSubHeader("Risoluzioni Standard per BAR"));
        p.add(createParagraph(
            "> Mappa 8x8 km (1v1 / 2v2): 513 x 513 pixel (o 512x512)\n" +
            "> Mappa 16x16 km (Standard 8v8 competitivo): 1025 x 1025 pixel (o 1024x1024)\n" +
            "> Mappa 24x24 km (Ampia scala): 1537 x 1537 pixel\n" +
            "> Mappa 32x32 km (Guerra totale): 2049 x 2049 pixel\n\n" +
            "Nota: Se importi immagini con risoluzione diversa (es. 2000x2000 o 1920x1080), BAR Map Studio applica automaticamente un ricampionamento bicubico con preservazione dei dislivelli."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createTipBox(
            "PERCHE IL FORMATO 16-BIT E FONDAMENTALE:\n" +
            "Un'immagine 8-bit convenzionale dispone solo di 256 livelli discreti. Su un dislivello di 300 metri, ogni gradino misura oltre 1,17 metri, generando terrazzamenti a gradoni ('stepping/banding') che incastrano i veicoli di BAR.\n" +
            "Il 16-bit offre 65.536 quote diverse (4,5 millimetri per livello), garantendo rampe perfettamente lisce ed un pathfinding fluido per tutti i veicoli."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createWarningBox(
            "ERRORI COMUNI DA EVITARE:\n" +
            "[!] Pendenze verticali accidentali: Usa sempre lo strumento Slope Map [L] per verificare che le rampe di accesso alle basi non superino i 22 gradi per i carri.\n" +
            "[!] Picchi isolati sotto acqua: I fondali marini devono avere un fondo piatto o degradante per consentire il passaggio di hovercraft e sottomarini."
        ));

        return wrapScroll(p);
    }

    // =========================================================================
    // 2. TEXTURE & TERRENO
    // =========================================================================
    private JComponent buildTextureCard() {
        JPanel p = createCardPanel();

        p.add(createSectionTitle("SPECIFICHE TEXTURE DI SUPERFICIE E SPLATTING"));
        p.add(Box.createVerticalStrut(10));

        p.add(createBadgeRow(
            new String[]{"Risoluzione Tessere", "Potenze di 2 (1024, 2048, 4096 px)"},
            new String[]{"Formato Diffuse Globale", "DDS compresso BC1/DXT1 o PNG 24-bit"},
            new String[]{"Detail Normals (DNTS)", "TGA 32-bit non compresso (1024x1024)"},
            new String[]{"Splatting Mask", "DDS BC3/DXT5 a 4 Canali RGBA"},
            new String[]{"Tiling", "Seamless (Ripetizione continua senza cuciture)"}
        ));
        p.add(Box.createVerticalStrut(14));

        p.add(createSubHeader("I 4 Canali della Maschera di Splatting (_splats.dds)"));
        p.add(createParagraph(
            "Lo shader del terreno di Beyond All Reason fonde 4 texture di micro-dettaglio (DNTS) attraverso i canali della mappa di splatting:\n" +
            "> Canale R (Rosso): Materiale 1 (Terreno base, erba, pianura)\n" +
            "> Canale G (Verde): Materiale 2 (Pareti rocciose, falesie, scarpate verticali)\n" +
            "> Canale B (Blu): Materiale 3 (Sabbia, ghiaia, fondale marino o litorale)\n" +
            "> Canale A (Alpha): Materiale 4 (Neve, fango, crepe o polvere d'alta quota)\n\n" +
            "I valori variano da 0 a 255 e lo shader calcola automaticamente la transizione morbida tra i diversi strati."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createTipBox(
            "DETAIL NORMAL & TEXTURE (File _dnts1..4.tga):\n" +
            "Risoluzione standard: 1024x1024 pixel. I canali contengono:\n" +
            "- R (Rosso) = Vettore normale tangenziale X (nx)\n" +
            "- G (Verde) = Vettore normale tangenziale Y (ny)\n" +
            "- B (Blu) = Vettore normale tangenziale Z (nz = 1.0)\n" +
            "- Alpha (Trasparenza) = Mappa di altezza e rugosita di micro-dettaglio (High-Pass Detail Heightpass)"
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createWarningBox(
            "ERRORI COMUNI SULLE TEXTURE:\n" +
            "[!] Neri puri (#000000) o Bianchi puri (#FFFFFF): Sovrascrivono la luce solare dello shader di BAR rendendo le ombre piatte o bruciate.\n" +
            "[!] Dimensioni non potenze di 2: La GPU non puo generare mipmaps per dimensioni asimmetriche (es. 1000x1000), causando artefatti visivi da lontano."
        ));

        return wrapScroll(p);
    }

    // =========================================================================
    // 3. MODELLI 3D & FLORA
    // =========================================================================
    private JComponent buildModelsCard() {
        JPanel p = createCardPanel();

        p.add(createSectionTitle("SPECIFICHE MODELLI 3D, FLORA E RELITTI (FEATURES)"));
        p.add(Box.createVerticalStrut(10));

        p.add(createBadgeRow(
            new String[]{"Formato 3D Nativo", "Spring .s3o (Convertibile da .OBJ)"},
            new String[]{"Flora (Alberi/Arbusti)", "150 - 500 Triangoli (Max 800)"},
            new String[]{"Rocce & Massi", "100 - 600 Triangoli"},
            new String[]{"Relitti Grandi / Landmark", "1.000 - 2.500 Triangoli"},
            new String[]{"Texture Atlas", "512x512 o 1024x1024 DDS (BC1 / BC3)"}
        ));
        p.add(Box.createVerticalStrut(14));

        p.add(createSubHeader("Budget Poligonale: Perche e Cruciale in BAR"));
        p.add(createParagraph(
            "Una tipica mappa competitiva di Beyond All Reason ospita tra 5.000 e 20.000 alberi e formazioni rocciose.\n" +
            "Se un singolo albero avesse 3.000 poligoni, l'engine dovrebbe renderizzare fino a 60 MILIONI di triangoli solo per la vegetazione, provocando cali devastanti di FPS.\n" +
            "Usa geometrie low-poly con texture atlas ben pianificate e dettagli affidati alla normal map."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createTipBox(
            "SCHEMA DEFINIZIONE LUA (features/<nome_feature>.lua):\n" +
            "Ogni oggetto 3D deve avere uno script .lua di accompagnamento con i seguenti parametri chiave:\n" +
            "- name: stringa identificativa univoca\n" +
            "- object: percorso del file .s3o (es. 'albero_pino.s3o')\n" +
            "- blocking: true (ostacola il moto) / false (schiacciabile dai carri)\n" +
            "- reclaimable: true (i costruttori possono assorbire metallo/energia)\n" +
            "- energy: valore energia estraibile (es. 40-70 per alberi)\n" +
            "- metal: valore metallo estraibile (es. 100-300 per relitti)\n" +
            "- damage: punti ferita prima della distruzione/incendio\n" +
            "- footprintX / footprintZ: ingombro in caselle griglia (1 footprint = 16x16 elmos)"
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createWarningBox(
            "CONSIGLI SULLE UV E I MATERIALI:\n" +
            "[!] Piu texture separate per un solo albero: Usa un unico Texture Atlas per tronco e foglie per ridurre i draw call al minimo.\n" +
            "[!] Normali non invertite: Recoil Engine utilizza normal maps tangent space con orientamento asse Y conforme a OpenGL."
        ));

        return wrapScroll(p);
    }

    // =========================================================================
    // 4. METALLO & AMBIENTE
    // =========================================================================
    private JComponent buildMetalEnvCard() {
        JPanel p = createCardPanel();

        p.add(createSectionTitle("SPECIFICHE RISORSE (METALLO) E MAPINFO.LUA"));
        p.add(Box.createVerticalStrut(10));

        p.add(createBadgeRow(
            new String[]{"Risoluzione Metalmap", "1 Pixel ogni 16x16 Elmos (Griglia 1:16)"},
            new String[]{"Valore Metallo Mex", "0 - 255 (Standard BAR: ~180 - 255)"},
            new String[]{"Distanza Minima Mex", "48 - 64 Elmos (Evita sovrapposizione cerchi)"},
            new String[]{"Vento Standard", "Min: 4 - 6 | Max: 18 - 25"},
            new String[]{"Gravita di Gioco", "Standard Recoil: 130"}
        ));
        p.add(Box.createVerticalStrut(14));

        p.add(createSubHeader("Bilanciamento Strategico dei Punti Metallo (Mex)"));
        p.add(createParagraph(
            "> Base di Partenza: In genere 3 o 4 punti Mex (valore 1.5 - 2.0 metal/sec) difendibili entro 150 elmos dal Commander spawn.\n" +
            "> Espansione Naturale: 2 o 3 punti Mex a media distanza che incoraggiano la contesa territoriale.\n" +
            "> Punti Strategici al Centro: Spot ad alto rendimento (valore 3.0 - 5.0 metal/sec o geovent) situati in alture o gole per premiare l'aggressivita.\n" +
            "> Simmetria Assoluta: Nei match competitivi, le posizioni e il valore complessivo di metallo devono essere perfettamente speculari (Asse X, Z o Diagonale)."
        ));
        p.add(Box.createVerticalStrut(12));

        p.add(createTipBox(
            "ILLUMINAZIONE E SOLE (In mapinfo.lua):\n" +
            "- Inclinazione solare ottimale: Tra 35 e 55 gradi sull'orizzonte. Crea ombre lunghe e suggestive che valorizzano canyon e scarpate senza rendere buie le basi.\n" +
            "- Rapporto Luce/Ombra: Luce d'ambiente ~0.35-0.45 per garantire piena leggibilita visiva delle unita anche nelle gole profonde."
        ));

        return wrapScroll(p);
    }

    // =========================================================================
    // 5. CARTELLA CUSTOM ASSETS
    // =========================================================================
    private JComponent buildCustomAssetsCard() {
        JPanel p = createCardPanel();

        p.add(createSectionTitle("STRUTTURA CARTELLE PER ASSET PERSONALIZZATI"));
        p.add(Box.createVerticalStrut(10));

        p.add(createParagraph(
            "BAR Map Studio include una cartella 'custom_assets/' predisposta nella radice dell'applicazione dove puoi depositare i tuoi file personali:\n\n" +
            "[DIR] custom_assets/\n" +
            "  |-- [DIR] heightmaps/  -> Deposita qui le tue immagini PNG 16-bit o RAW di rilievo\n" +
            "  |-- [DIR] textures/    -> Deposita qui texture diffuse, normali e maschere splat\n" +
            "  `-- [DIR] features/    -> Deposita qui modelli 3D (.s3o), script (.lua) e unittextures\n\n" +
            "Come vengono utilizzati:\n" +
            "1. Le heightmap possono essere caricate direttamente dal pulsante 'Carica Heightmap...' nella barra laterale sinistra.\n" +
            "2. Le texture caricate sostituiscono in tempo reale la pavimentazione nell'anteprima e vengono impacchettate nelle tessere esportate.\n" +
            "3. I modelli e gli oggetti 3D vengono inseriti automaticamente nell'archivio .sdz autosufficiente della mappa durante l'esportazione finale per Beyond All Reason."
        ));
        p.add(Box.createVerticalStrut(16));

        JButton openBtn = new JButton("APRI CARTELLA 'custom_assets' IN WINDOWS EXPLORER") {
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

        return wrapScroll(p);
    }

    private void openCustomAssetsDirectory() {
        try {
            File dir = new File("custom_assets");
            if (!dir.exists()) dir.mkdirs();
            Desktop.getDesktop().open(dir);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Impossibile aprire la cartella custom_assets:\n" + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
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

        JButton openDirBtn = new JButton("Apri Cartella Custom Assets");
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

        JButton closeBtn = new JButton("Chiudi");
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

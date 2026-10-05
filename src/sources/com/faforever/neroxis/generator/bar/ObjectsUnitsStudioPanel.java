package com.faforever.neroxis.generator.bar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Schermata "OBJECTS & UNITS STUDIO" per BAR MAP STUDIO.
 * Layout moderno a 3 colonne conforme al tema dark-tech:
 * - Colonna 1: Catalogo completo (unita' BAR, props, animati, boss) + Importazione mesh OBJ
 * - Colonna 2: Viewport 3D interattivo Java2D con Orbit 360 deg, Pan e Zoom
 * - Colonna 3: Proprieta' entita', editor pulito per script Lua con convalidatore ANTLR4 e pulsante "INVIA ALLA MAPPA"
 */
public final class ObjectsUnitsStudioPanel extends JPanel {

    private final Supplier<Path> barDataSupplier;
    private final DefaultListModel<ObjectEntity> listModel = new DefaultListModel<>();
    private final JList<ObjectEntity> entityList = new JList<>(listModel);
    private final List<ObjectEntity> allEntities = new ArrayList<>();

    private final JTextField searchField = new JTextField();
    private String activeCategoryFilter = "TUTTI";
    private String activeFactionFilter = "ALL";
    private int activeTierFilter = 0;
    private final JLabel countLabel = new JLabel(" ");

    /** Modelli 3D delle unita' ufficiali: letti dal gioco solo quando si clicca l'unita', e tenuti solo gli ultimi. */
    private static final class OfficialView {
        final S3OParser.S3OModel source;
        final ObjModel3D model;
        final BufferedImage texture;
        OfficialView(S3OParser.S3OModel source, BufferedImage texture) { this.source = source; this.model = source.model; this.texture = texture; }
    }
    private final java.util.Map<String, OfficialView> officialViews = new java.util.LinkedHashMap<>(16, 0.75f, true) {
        @Override protected boolean removeEldestEntry(java.util.Map.Entry<String, OfficialView> e) { return size() > 8; }
    };
    private ModelTextureCache officialTextures;
    private final java.util.Map<String, BufferedImage> officialMixed = new java.util.LinkedHashMap<>(8, 0.75f, true) {
        @Override protected boolean removeEldestEntry(java.util.Map.Entry<String, BufferedImage> e) { return size() > 4; }
    };

    // Viewport Centrale
    private final Object3DCanvas canvas3D = new Object3DCanvas();
    private final JPanel centerContainer = new JPanel(new BorderLayout());
    private final JLabel preview2dLabel = new JLabel("", SwingConstants.CENTER);

    // Inspector Destra
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JSpinner footprintXSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 32, 1));
    private final JSpinner footprintZSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 32, 1));
    private final JSpinner healthSpinner = new JSpinner(new SpinnerNumberModel(500, 1, 100000000, 50));
    private final JSpinner metalSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 10000000, 10));
    private final JSpinner energySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000000, 50));
    private final JTextField descField = new JTextField();
    private final JButton cloneBtn = new JButton(I18n.t("objclone.button"));
    private final JLabel scriptHint = new JLabel(" ");
    /** false = l'editor mostra lo script di comportamento; true = il codice che modifica la definizione. */
    private boolean editingDef = false;
    private JToggleButton scriptTabBtn;
    private JToggleButton defTabBtn;
    private JButton componentsBtn;
    /** Riga sotto le schede dell'editor: dice in breve a cosa serve quella aperta. */
    private final JLabel editorHint = new JLabel();
    private final JButton deleteBtn = new JButton(I18n.t("objdel.button"));
    private final JCheckBox useScriptBox = new JCheckBox(I18n.t("objscript.use"));
    /** true mentre l'editor Lua viene riempito dal programma: quelle modifiche non sono dell'utente. */
    private boolean fillingLua = false;
    private final JSpinner sightSpinner = new JSpinner(new SpinnerNumberModel(350, 0, 10000, 50));
    /** true mentre i campi vengono riempiti dall'oggetto selezionato: in quel momento non si salva nulla. */
    private boolean fillingFields = false;
    private final JSpinner scaleSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.01, 500.0, 0.5));
    private final JLabel dimLabel = new JLabel(I18n.t("obj.1"));
    /** Ordine delle voci nel menu Schieramento. */
    private static final ObjectEntity.Owner[] OWNER_ORDER = {
        ObjectEntity.Owner.AUTO, ObjectEntity.Owner.ARMADA, ObjectEntity.Owner.CORTEX, ObjectEntity.Owner.LEGION,
        ObjectEntity.Owner.RAPTORS, ObjectEntity.Owner.SCAVENGERS, ObjectEntity.Owner.TEAM_A, ObjectEntity.Owner.TEAM_B,
        ObjectEntity.Owner.HOSTILE, ObjectEntity.Owner.NEUTRAL};
    private final JComboBox<String> ownerCombo = new JComboBox<>(new String[]{
        I18n.t("objown.auto"), I18n.t("objown.armada"), I18n.t("objown.cortex"), I18n.t("objown.legion"),
        I18n.t("objown.raptors"), I18n.t("objown.scavengers"), I18n.t("objown.teamA"), I18n.t("objown.teamB"),
        I18n.t("objown.hostile"), I18n.t("objown.neutral")});
    private final JButton tintBtn = new JButton();
    private final JButton tintResetBtn = new JButton(I18n.t("objown.colour.reset"));
    private final JLabel ownerHint = new JLabel(" ");
    private boolean fillingOwner = false;

    // Editor Lua & Validatore
    private final JTextArea luaTextArea = new JTextArea();
    private final JLabel validationStatusLabel = new JLabel(" ");
    private final JPanel validationBanner = new JPanel(new BorderLayout(8, 0));
    private final JButton sendToMapBtn = new JButton(I18n.t("obj.2"));

    private ObjectEntity selectedEntity = null;

    private boolean officialUnitsLoaded = false;
    private boolean officialUnitsLoading = false;

    public ObjectsUnitsStudioPanel(Supplier<Path> barDataSupplier) {
        this.barDataSupplier = barDataSupplier;
        setLayout(new BorderLayout());
        setBackground(BarMapStudio.BG_DARK);

        initDefaultEntities();
        initUI();
    }

    private void initDefaultEntities() {
        allEntities.addAll(ObjectEntity.loadDefaultObjects());
        refillList();
    }

    public void ensureOfficialUnitsLoaded() {
        if (officialUnitsLoaded || officialUnitsLoading) return;
        officialUnitsLoading = true;
        Thread t = new Thread(() -> {
            try {
                Path data = barDataSupplier != null ? barDataSupplier.get() : null;
                if (data == null) {
                    data = BarFolder.find(null);
                }
                if (data != null && BarFolder.isData(data)) {
                    List<Units.Unit> units = Units.read(data);
                    if (units != null && !units.isEmpty()) {
                        List<ObjectEntity> loaded = new ArrayList<>(units.size());
                        for (Units.Unit u : units) {
                            loaded.add(ObjectEntity.fromOfficialUnit(u));
                        }
                        SwingUtilities.invokeLater(() -> {
                            allEntities.addAll(loaded);
                            officialUnitsLoaded = true;
                            officialUnitsLoading = false;
                            refillList();
                        });
                        return;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            officialUnitsLoading = false;
        }, "objects-units-loader");
        t.setDaemon(true);
        t.start();
    }

    private void initUI() {
        // Layout 3 Colonne
        JPanel leftCol = buildLeftColumn();
        JPanel centerCol = buildCenterColumn();
        JPanel rightCol = buildRightColumn();

        JSplitPane splitRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerCol, rightCol);
        splitRight.setDividerSize(6);
        splitRight.setContinuousLayout(true);
        splitRight.setOpaque(false);
        splitRight.setBorder(null);
        splitRight.setResizeWeight(0.65);

        JSplitPane splitMaster = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCol, splitRight);
        splitMaster.setDividerSize(6);
        splitMaster.setContinuousLayout(true);
        splitMaster.setOpaque(false);
        splitMaster.setBorder(null);
        splitMaster.setResizeWeight(0.24);

        add(splitMaster, BorderLayout.CENTER);

        // Seleziona il primo elemento
        if (!listModel.isEmpty()) {
            entityList.setSelectedIndex(0);
        }
    }

    // =========================================================================
    // COLONNA 1: LIBRERIA & IMPORTAZIONE MESH
    // =========================================================================
    private JPanel buildLeftColumn() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 8));
        panel.setPreferredSize(new Dimension(320, 100));

        // Intestazione Colonna
        JPanel headerPanel = new JPanel(new BorderLayout(0, 6));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel(I18n.t("obj.3"));
        title.setFont(BarMapStudio.fontCond(Font.BOLD, 13));
        title.setForeground(BarMapStudio.ACCENT_CYAN);
        headerPanel.add(title, BorderLayout.NORTH);

        // Barra Ricerca
        searchField.setBackground(BarMapStudio.CARD_INNER);
        searchField.setForeground(BarMapStudio.TEXT_MAIN);
        searchField.setCaretColor(BarMapStudio.ACCENT_CYAN);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        searchField.putClientProperty("JTextField.placeholderText", I18n.t("obj.search"));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refillList(); }
            public void removeUpdate(DocumentEvent e) { refillList(); }
            public void changedUpdate(DocumentEvent e) { refillList(); }
        });
        headerPanel.add(searchField, BorderLayout.CENTER);

        // Filtri: categoria, fazione e livello tecnologico si sommano tra loro e con la ricerca
        JPanel filterBox = new JPanel();
        filterBox.setOpaque(false);
        filterBox.setLayout(new BoxLayout(filterBox, BoxLayout.Y_AXIS));

        // griglia 2x3: ogni filtro ha un terzo della colonna, cosi' il testo non viene troncato in nessuna lingua
        JPanel filters = new JPanel(new GridLayout(2, 3, 4, 4));
        filters.setOpaque(false);
        String[] cats = {"TUTTI", "BAR", "STATICI", "ANIMATI", "CUSTOM"};
        ButtonGroup filterGroup = new ButtonGroup();
        for (String cat : cats) {
            JToggleButton btn = createFilterToggle(I18n.t("objcat." + cat), cat.equals("TUTTI"), BarMapStudio.ACCENT_CYAN);
            filterGroup.add(btn);
            btn.addActionListener(e -> {
                activeCategoryFilter = cat;
                if ("BAR".equals(cat) || "TUTTI".equals(cat)) {
                    ensureOfficialUnitsLoaded();
                }
                refillList();
            });
            filters.add(btn);
        }
        filterBox.add(createFilterCaption(I18n.t("objflt.category")));
        filterBox.add(filters);

        String[] factions = {"ALL", "Armada", "Cortex", "Legion", "Other"};
        JPanel factionRow = new JPanel(new GridLayout(1, factions.length, 4, 4));
        factionRow.setOpaque(false);
        ButtonGroup factionGroup = new ButtonGroup();
        for (String faction : factions) {
            Color accent = "ALL".equals(faction) ? BarMapStudio.ACCENT_CYAN
                : "Other".equals(faction) ? new Color(180, 190, 205) : Units.factionColour(faction);
            JToggleButton btn = createFilterToggle(I18n.t("objfac." + faction), "ALL".equals(faction), accent);
            factionGroup.add(btn);
            btn.addActionListener(e -> {
                activeFactionFilter = faction;
                ensureOfficialUnitsLoaded();
                refillList();
            });
            factionRow.add(btn);
        }
        filterBox.add(createFilterCaption(I18n.t("objflt.faction")));
        filterBox.add(factionRow);

        JPanel tierRow = new JPanel(new GridLayout(1, 4, 4, 4));
        tierRow.setOpaque(false);
        ButtonGroup tierGroup = new ButtonGroup();
        for (int tier = 0; tier <= 3; tier++) {
            final int value = tier;
            JToggleButton btn = createFilterToggle(tier == 0 ? I18n.t("objcat.TUTTI") : "T" + tier, tier == 0, BarMapStudio.ACCENT_AMBER);
            tierGroup.add(btn);
            btn.addActionListener(e -> {
                activeTierFilter = value;
                ensureOfficialUnitsLoaded();
                refillList();
            });
            tierRow.add(btn);
        }
        filterBox.add(createFilterCaption(I18n.t("objflt.tier")));
        filterBox.add(tierRow);

        countLabel.setFont(BarMapStudio.font(Font.PLAIN, 10));
        countLabel.setForeground(BarMapStudio.TEXT_MUTED);
        countLabel.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 0));
        filterBox.add(countLabel);
        for (Component c : filterBox.getComponents()) {
            ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        headerPanel.add(filterBox, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Lista Elementi
        entityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entityList.setBackground(BarMapStudio.CARD_INNER);
        entityList.setFixedCellHeight(46);

        // Reusable Flyweight cell renderer (zero garbage, maximum performance)
        JPanel cell = new JPanel(new BorderLayout(6, 2));
        cell.setOpaque(true);
        cell.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        JLabel nameLbl = new JLabel();
        nameLbl.setFont(BarMapStudio.font(Font.BOLD, 12));
        JLabel subLbl = new JLabel();
        subLbl.setFont(BarMapStudio.font(Font.PLAIN, 10));
        cell.add(nameLbl, BorderLayout.NORTH);
        cell.add(subLbl, BorderLayout.SOUTH);

        entityList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (isSelected) {
                cell.setBackground(new Color(24, 38, 56));
                nameLbl.setForeground(BarMapStudio.ACCENT_CYAN);
            } else {
                cell.setBackground(index % 2 == 0 ? BarMapStudio.CARD_INNER : new Color(17, 21, 31));
                nameLbl.setForeground(BarMapStudio.TEXT_MAIN);
            }

            if (value instanceof ObjectEntity entity) {
                nameLbl.setText(entity.getName());
                Color badgeColor = getCategoryColor(entity.getCategory(), entity.getFaction());
                String sub = (entity.isClone() ? I18n.t("objclone.label", entity.getCloneOf()) : entity.getCategory().getLabel()) + " | " + entity.getFaction();
                if (entity.getTier() > 0) sub += " | T" + entity.getTier();
                subLbl.setText(sub);
                subLbl.setForeground(badgeColor);
            } else {
                nameLbl.setText("");
                subLbl.setText("");
            }
            return cell;
        });

        entityList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectEntity(entityList.getSelectedValue());
            }
        });

        JScrollPane scroll = new JScrollPane(entityList);
        scroll.setBorder(BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1));
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        panel.add(scroll, BorderLayout.CENTER);

        // Pulsante "+ IMPORTA MESH 3D"
        JButton importBtn = new JButton(I18n.t("obj.4")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(0, 160, 210));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(28, 44, 64));
                } else {
                    g2.setColor(new Color(20, 30, 44));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(BarMapStudio.ACCENT_CYAN);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        importBtn.setFont(BarMapStudio.font(Font.BOLD, 12));
        importBtn.setForeground(BarMapStudio.ACCENT_CYAN);
        importBtn.setFocusPainted(false);
        importBtn.setBorderPainted(false);
        importBtn.setContentAreaFilled(false);
        importBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        importBtn.setPreferredSize(new Dimension(100, 32));
        importBtn.addActionListener(e -> importCustomMeshDialog());
        // Copia personalizzata di un'unita' di BAR: stesso modello e comportamento, nome e valori propri
        cloneBtn.setFont(BarMapStudio.font(Font.BOLD, 11));
        cloneBtn.setForeground(BarMapStudio.ACCENT_AMBER);
        cloneBtn.setBackground(new Color(34, 28, 16));
        cloneBtn.setFocusPainted(false);
        cloneBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cloneBtn.setPreferredSize(new Dimension(100, 28));
        cloneBtn.setToolTipText(I18n.t("objclone.tip"));
        cloneBtn.addActionListener(e -> cloneSelectedOfficial());
        // Elimina: solo per gli oggetti tuoi (mesh importate e copie); le unita' di BAR sono di sola consultazione
        deleteBtn.setFont(BarMapStudio.font(Font.BOLD, 11));
        deleteBtn.setForeground(new Color(255, 110, 110));
        deleteBtn.setBackground(new Color(40, 18, 18));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBtn.setPreferredSize(new Dimension(90, 28));
        deleteBtn.setToolTipText(I18n.t("objdel.tip"));
        deleteBtn.addActionListener(e -> deleteSelectedCustom());
        entityList.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0), "deleteCustomEntity");
        entityList.getActionMap().put("deleteCustomEntity", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                deleteSelectedCustom();
            }
        });
        JPanel cloneRow = new JPanel(new BorderLayout(6, 0));
        cloneRow.setOpaque(false);
        cloneRow.add(cloneBtn, BorderLayout.CENTER);
        cloneRow.add(deleteBtn, BorderLayout.EAST);
        JPanel bottomButtons = new JPanel(new BorderLayout(0, 6));
        bottomButtons.setOpaque(false);
        bottomButtons.add(cloneRow, BorderLayout.NORTH);
        bottomButtons.add(importBtn, BorderLayout.SOUTH);
        panel.add(bottomButtons, BorderLayout.SOUTH);

        return panel;
    }

    private Color getCategoryColor(ObjectEntity.Category cat, String faction) {
        if (cat == ObjectEntity.Category.OFFICIAL_UNIT) {
            if ("Armada".equalsIgnoreCase(faction)) return new Color(74, 158, 255);
            if ("Cortex".equalsIgnoreCase(faction)) return new Color(255, 84, 72);
            if ("Legion".equalsIgnoreCase(faction)) return new Color(63, 200, 90);
            return new Color(180, 190, 205);
        }
        return switch (cat) {
            case STATIC_PROP -> new Color(50, 205, 120);
            case ANIMATED_SCENERY -> new Color(0, 210, 255);
            case BOSS_UNIT -> new Color(255, 160, 40);
            case CUSTOM_MESH -> new Color(180, 100, 255);
            default -> new Color(180, 190, 205);
        };
    }

    /** Filtro a pillola: quello attivo e' evidenziato in ciano, cosi' si vede quale categoria e' selezionata. */
    private JLabel createFilterCaption(String text) {
        JLabel caption = new JLabel(text);
        caption.setFont(BarMapStudio.fontCond(Font.BOLD, 10));
        caption.setForeground(BarMapStudio.TEXT_MUTED);
        caption.setBorder(BorderFactory.createEmptyBorder(6, 2, 3, 0));
        return caption;
    }

    private JToggleButton createFilterToggle(String text, boolean selected, Color accent) {
        JToggleButton btn = new JToggleButton(text, selected) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                Color fg;
                if (isSelected()) {
                    g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40));
                    g.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);
                    g.setColor(accent);
                    fg = accent;
                } else {
                    g.setColor(getModel().isRollover() ? new Color(255, 255, 255, 18) : BarMapStudio.CARD_INNER);
                    g.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);
                    g.setColor(BarMapStudio.BORDER_COLOR);
                    fg = getModel().isRollover() ? BarMapStudio.TEXT_MAIN : BarMapStudio.TEXT_MUTED;
                }
                g.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
                g.setFont(getFont());
                FontMetrics fm = g.getFontMetrics();
                g.setColor(fg);
                g.drawString(getText(), (w - fm.stringWidth(getText())) / 2, ((h - fm.getHeight()) / 2) + fm.getAscent());
                g.dispose();
            }
        };
        btn.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setRolloverEnabled(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 24));
        return btn;
    }

    private void refillList() {
        String filter = searchField.getText().trim().toLowerCase(Locale.ROOT);
        List<ObjectEntity> filtered = new ArrayList<>();

        for (ObjectEntity e : allEntities) {
            if (!activeCategoryFilter.equals("TUTTI")) {
                // BAR = solo le unita' originali del gioco; le copie personalizzate stanno sotto CUSTOM
                if (activeCategoryFilter.equals("BAR") && (e.getCategory() != ObjectEntity.Category.OFFICIAL_UNIT || e.isClone())) continue;
                if (activeCategoryFilter.equals("STATICI") && e.getCategory() != ObjectEntity.Category.STATIC_PROP) continue;
                if (activeCategoryFilter.equals("ANIMATI") && e.getCategory() != ObjectEntity.Category.ANIMATED_SCENERY) continue;
                // importati: tutto cio' che non e' un'unita' ufficiale di BAR (mesh importate e oggetti in dotazione)
                if (activeCategoryFilter.equals("CUSTOM") && e.getCategory() == ObjectEntity.Category.OFFICIAL_UNIT && !e.isClone()) continue;
            }

            if (!activeFactionFilter.equals("ALL")) {
                String faction = e.getFaction() == null ? "" : e.getFaction();
                boolean known = faction.equalsIgnoreCase("Armada") || faction.equalsIgnoreCase("Cortex") || faction.equalsIgnoreCase("Legion");
                // "Altro" raccoglie tutto cio' che non appartiene alle tre fazioni (neutrali, raptor, oggetti importati)
                if (activeFactionFilter.equals("Other") ? known : !faction.equalsIgnoreCase(activeFactionFilter)) continue;
            }
            if (activeTierFilter > 0) {
                if (activeTierFilter == 3 ? e.getTier() < 3 : e.getTier() != activeTierFilter) continue;
            }

            if (!filter.isEmpty()) {
                String searchTarget = (e.getId() + " " + e.getName() + " " + e.getFaction()).toLowerCase(Locale.ROOT);
                if (!searchTarget.contains(filter)) continue;
            }

            filtered.add(e);
        }

        ObjectEntity prev = selectedEntity;
        listModel.clear();
        listModel.addAll(filtered);
        countLabel.setText(I18n.t("objflt.count", filtered.size(), allEntities.size()));

        if (!listModel.isEmpty()) {
            int selIdx = (prev != null) ? filtered.indexOf(prev) : 0;
            entityList.setSelectedIndex(Math.max(0, selIdx));
        }
    }

    // =========================================================================
    // COLONNA 2: VIEWPORT 3D CENTRALE
    // =========================================================================
    private JPanel buildCenterColumn() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 4, 10, 4));

        centerContainer.setOpaque(false);
        centerContainer.add(canvas3D, BorderLayout.CENTER);

        panel.add(centerContainer, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // COLONNA 3: PROPRIETA' & EDITOR SCRIPT LUA
    // =========================================================================
    private JPanel buildRightColumn() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 12));
        panel.setPreferredSize(new Dimension(380, 100));

        // 1. Scheda Dati & Proprieta'
        JPanel propsCard = new JPanel(new GridLayout(0, 2, 8, 5));
        propsCard.setOpaque(false);
        propsCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1),
            I18n.t("obj.5"),
            0, 0,
            BarMapStudio.fontCond(Font.BOLD, 11),
            BarMapStudio.ACCENT_CYAN
        ));

        idField.setEditable(false);
        styleInputField(idField);
        styleInputField(nameField);

        propsCard.add(createPropLabel(I18n.t("obj.6")));
        propsCard.add(idField);
        propsCard.add(createPropLabel(I18n.t("obj.7")));
        propsCard.add(nameField);
        propsCard.add(createPropLabel(I18n.t("obj.8")));
        propsCard.add(footprintXSpinner);
        propsCard.add(createPropLabel(I18n.t("obj.9")));
        propsCard.add(footprintZSpinner);
        propsCard.add(createPropLabel(I18n.t("obj.10")));
        propsCard.add(healthSpinner);
        propsCard.add(createPropLabel(I18n.t("obj.11")));
        propsCard.add(metalSpinner);
        propsCard.add(createPropLabel(I18n.t("obj.12")));
        propsCard.add(energySpinner);
        propsCard.add(createPropLabel(I18n.t("objx.sight")));
        styleSpinner(sightSpinner);
        propsCard.add(sightSpinner);
        propsCard.add(createPropLabel(I18n.t("objx.desc")));
        styleInputField(descField);
        descField.setToolTipText(I18n.t("objx.desc.tip"));
        propsCard.add(descField);

        // Ogni modifica viene salvata subito nell'oggetto: vale anche per le copie gia' piazzate sulla mappa,
        // senza dover premere di nuovo "Send to map"
        DocumentListener saveText = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { saveFieldsToEntity(); }
            public void removeUpdate(DocumentEvent e) { saveFieldsToEntity(); }
            public void changedUpdate(DocumentEvent e) { saveFieldsToEntity(); }
        };
        nameField.getDocument().addDocumentListener(saveText);
        descField.getDocument().addDocumentListener(saveText);
        healthSpinner.addChangeListener(e -> saveFieldsToEntity());
        metalSpinner.addChangeListener(e -> saveFieldsToEntity());
        energySpinner.addChangeListener(e -> saveFieldsToEntity());
        sightSpinner.addChangeListener(e -> saveFieldsToEntity());

        // Scheda Scala & Dimensioni In-Game
        JPanel scaleCard = new JPanel(new BorderLayout(0, 6));
        scaleCard.setOpaque(false);
        scaleCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1),
            I18n.t("obj.13"),
            0, 0,
            BarMapStudio.fontCond(Font.BOLD, 11),
            BarMapStudio.ACCENT_CYAN
        ));

        JPanel scaleRow1 = new JPanel(new BorderLayout(8, 0));
        scaleRow1.setOpaque(false);

        JPanel spinBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        spinBox.setOpaque(false);
        spinBox.add(createPropLabel(I18n.t("obj.14")));
        scaleSpinner.setPreferredSize(new Dimension(88, 24));
        styleSpinner(scaleSpinner);
        scaleSpinner.addChangeListener(e -> {
            if (selectedEntity != null) {
                float sc = ((Number) scaleSpinner.getValue()).floatValue();
                selectedEntity.setScale(sc);
                canvas3D.setEntityScale(sc);
                updateDimensionsUI();
            }
        });
        spinBox.add(scaleSpinner);
        scaleRow1.add(spinBox, BorderLayout.WEST);

        dimLabel.setFont(BarMapStudio.font(Font.BOLD, 10));
        dimLabel.setForeground(new Color(255, 205, 50));
        dimLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        scaleRow1.add(dimLabel, BorderLayout.CENTER);

        // Pulsanti Rapidi Preset Auto-Adatta
        JPanel presetPanel = new JPanel(new GridLayout(1, 4, 4, 0));
        presetPanel.setOpaque(false);
        presetPanel.add(createPresetBtn(I18n.t("obj.15"), 45.0f));
        presetPanel.add(createPresetBtn("T1 (20m)", 20.0f));
        presetPanel.add(createPresetBtn(I18n.t("obj.16"), 60.0f));
        presetPanel.add(createPresetBtn(I18n.t("obj.17"), 35.0f));

        scaleCard.add(scaleRow1, BorderLayout.NORTH);
        scaleCard.add(presetPanel, BorderLayout.SOUTH);

        // Scheda Schieramento & Colore
        JPanel sideCard = new JPanel(new GridBagLayout());
        sideCard.setOpaque(false);
        sideCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1),
            I18n.t("objown.title"),
            0, 0,
            BarMapStudio.fontCond(Font.BOLD, 11),
            BarMapStudio.ACCENT_CYAN
        ));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 4, 2, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.gridx = 0; gc.gridy = 0;
        sideCard.add(createPropLabel(I18n.t("objown.side")), gc);
        gc.gridx = 1; gc.gridwidth = 2; gc.weightx = 1.0; gc.fill = GridBagConstraints.HORIZONTAL;
        ownerCombo.setFont(BarMapStudio.font(Font.PLAIN, 11));
        ownerCombo.setToolTipText(I18n.t("objown.side.tip"));
        ownerCombo.addActionListener(e -> {
            if (fillingOwner || selectedEntity == null) return;
            selectedEntity.setOwner(OWNER_ORDER[Math.max(0, ownerCombo.getSelectedIndex())]);
            refreshOwnerUI();
            if (selectedEntity.isClone()) {
                // il colore mostrato dipende dallo schieramento: squadra oppure colore scelto
                loadOfficialModel(selectedEntity);
            }
        });
        sideCard.add(ownerCombo, gc);
        gc.gridx = 0; gc.gridy = 1; gc.gridwidth = 1; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        sideCard.add(createPropLabel(I18n.t("objown.colour")), gc);
        gc.gridx = 1;
        tintBtn.setPreferredSize(new Dimension(88, 22));
        tintBtn.setFocusPainted(false);
        tintBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tintBtn.setToolTipText(I18n.t("objown.colour.tip"));
        tintBtn.addActionListener(e -> {
            if (selectedEntity == null) return;
            Integer current = selectedEntity.getTintRgb();
            Color chosen = JColorChooser.showDialog(this, I18n.t("objown.colour.title"), current != null ? new Color(current) : Color.WHITE);
            if (chosen != null) {
                selectedEntity.setTintRgb(chosen.getRGB() & 0xFFFFFF);
                refreshOwnerUI();
                redrawSelected();
            }
        });
        sideCard.add(tintBtn, gc);
        gc.gridx = 2; gc.weightx = 1.0;
        tintResetBtn.setFont(BarMapStudio.font(Font.BOLD, 10));
        tintResetBtn.setFocusPainted(false);
        tintResetBtn.addActionListener(e -> {
            if (selectedEntity == null) return;
            selectedEntity.setTintRgb(null);
            refreshOwnerUI();
            redrawSelected();
        });
        sideCard.add(tintResetBtn, gc);
        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 3; gc.fill = GridBagConstraints.HORIZONTAL;
        ownerHint.setFont(BarMapStudio.font(Font.PLAIN, 10));
        ownerHint.setForeground(BarMapStudio.TEXT_MUTED);
        sideCard.add(ownerHint, gc);

        JPanel lowerCards = new JPanel(new BorderLayout(0, 6));
        lowerCards.setOpaque(false);
        lowerCards.add(scaleCard, BorderLayout.NORTH);
        lowerCards.add(sideCard, BorderLayout.SOUTH);

        JPanel topWrapper = new JPanel(new BorderLayout(0, 6));
        topWrapper.setOpaque(false);
        topWrapper.add(propsCard, BorderLayout.NORTH);
        topWrapper.add(lowerCards, BorderLayout.SOUTH);

        panel.add(topWrapper, BorderLayout.NORTH);

        // 2. Editor Script Lua Spazioso e Pulito
        JPanel luaPanel = new JPanel(new BorderLayout(0, 6));
        luaPanel.setOpaque(false);
        luaPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1),
            I18n.t("obj.18"),
            0, 0,
            BarMapStudio.fontCond(Font.BOLD, 11),
            BarMapStudio.ACCENT_CYAN
        ));

        // Barra strumenti editor: Incolla / Copia / Pulisci
        JPanel luaToolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        luaToolBar.setOpaque(false);

        JButton pasteBtn = createLuaToolBtn(I18n.t("obj.19"), I18n.t("obj.20"), e -> pasteFromClipboard());
        JButton copyBtn = createLuaToolBtn(I18n.t("obj.21"), I18n.t("obj.22"), e -> copyToClipboard());
        JButton clearBtn = createLuaToolBtn(I18n.t("obj.23"), I18n.t("obj.24"), e -> luaTextArea.setText(""));

        componentsBtn = createLuaToolBtn(I18n.t("objdef.examples"), I18n.t("objdef.examples.tip"), null);
        componentsBtn.setForeground(BarMapStudio.ACCENT_AMBER);
        componentsBtn.setPreferredSize(new Dimension(120, componentsBtn.getPreferredSize().height));
        componentsBtn.addActionListener(e -> openComponentsDialog());
        componentsBtn.setVisible(false); // solo nella scheda Definition
        luaToolBar.add(componentsBtn);
        luaToolBar.add(pasteBtn);
        luaToolBar.add(copyBtn);
        luaToolBar.add(clearBtn);

        // Due schede sullo stesso editor: cosa fa l'unita' (script) e com'e' fatta (definizione)
        scriptTabBtn = createFilterToggle(I18n.t("objdef.tab.script"), true, BarMapStudio.ACCENT_CYAN);
        defTabBtn = createFilterToggle(I18n.t("objdef.tab.def"), false, BarMapStudio.ACCENT_AMBER);
        scriptTabBtn.setToolTipText(I18n.t("objdef.tab.script.tip"));
        defTabBtn.setToolTipText(I18n.t("objdef.tab.def.tip"));
        scriptTabBtn.setPreferredSize(new Dimension(110, 22));
        defTabBtn.setPreferredSize(new Dimension(110, 22));
        ButtonGroup editorTabs = new ButtonGroup();
        editorTabs.add(scriptTabBtn);
        editorTabs.add(defTabBtn);
        scriptTabBtn.addActionListener(e -> switchEditor(false));
        defTabBtn.addActionListener(e -> switchEditor(true));
        JPanel editorTabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        editorTabBar.setOpaque(false);
        editorTabBar.add(scriptTabBtn);
        editorTabBar.add(defTabBtn);
        JButton editorHelpBtn = createLuaToolBtn("?", I18n.t("objhelp.button.tip"), e -> showEditorHelp());
        editorHelpBtn.setForeground(BarMapStudio.ACCENT_CYAN);
        editorTabBar.add(editorHelpBtn);

        JPanel luaHeader = new JPanel(new BorderLayout());
        luaHeader.setOpaque(false);
        luaHeader.add(editorTabBar, BorderLayout.WEST);
        luaHeader.add(luaToolBar, BorderLayout.EAST);
        editorHint.setFont(BarMapStudio.font(Font.PLAIN, 11));
        editorHint.setForeground(new Color(140, 155, 180));
        editorHint.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));
        editorHint.setText(I18n.t("objhint.script"));
        luaHeader.add(editorHint, BorderLayout.SOUTH);
        luaPanel.add(luaHeader, BorderLayout.NORTH);

        // Area di testo con font Monospaced dark-tech
        luaTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        luaTextArea.setBackground(new Color(12, 16, 22));
        luaTextArea.setForeground(new Color(220, 235, 250));
        luaTextArea.setCaretColor(BarMapStudio.ACCENT_CYAN);
        luaTextArea.setTabSize(4);
        luaTextArea.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane luaScroll = new JScrollPane(luaTextArea);
        luaScroll.setBorder(BorderFactory.createLineBorder(new Color(30, 42, 58), 1));
        // riga informativa sopra l'editor: di chi e' lo script e quali pezzi si possono muovere
        scriptHint.setFont(BarMapStudio.font(Font.PLAIN, 10));
        scriptHint.setForeground(BarMapStudio.TEXT_MUTED);
        scriptHint.setBorder(BorderFactory.createEmptyBorder(0, 2, 4, 2));
        JPanel luaCenter = new JPanel(new BorderLayout());
        luaCenter.setOpaque(false);
        // Lo script si salva mentre lo scrivi o lo incolli, senza dover premere "Send to map". In una copia
        // personalizzata, toccare lo script significa volerlo usare: la casella si accende da sola.
        luaTextArea.getDocument().addDocumentListener(new DocumentListener() {
            private void changed() {
                ObjectEntity e = selectedEntity;
                if (fillingLua || e == null || (e.getOfficialUnit() != null && !e.isClone())) return;
                if (editingDef) {
                    e.setDefLua(luaTextArea.getText());
                    return;
                }
                e.setLuaScript(luaTextArea.getText());
                if (e.isClone() && !e.isCustomScriptEnabled()) {
                    e.setCustomScriptEnabled(true);
                    useScriptBox.setSelected(true);
                }
            }
            public void insertUpdate(DocumentEvent ev) { changed(); }
            public void removeUpdate(DocumentEvent ev) { changed(); }
            public void changedUpdate(DocumentEvent ev) { changed(); }
        });
        useScriptBox.setOpaque(false);
        useScriptBox.setFont(BarMapStudio.font(Font.BOLD, 11));
        useScriptBox.setForeground(BarMapStudio.ACCENT_AMBER);
        useScriptBox.setToolTipText(I18n.t("objscript.use.tip"));
        useScriptBox.setVisible(false);
        useScriptBox.addActionListener(e -> {
            if (selectedEntity != null && selectedEntity.isClone()) {
                selectedEntity.setCustomScriptEnabled(useScriptBox.isSelected());
            }
        });
        JPanel luaTop = new JPanel(new BorderLayout());
        luaTop.setOpaque(false);
        luaTop.add(scriptHint, BorderLayout.NORTH);
        luaTop.add(useScriptBox, BorderLayout.SOUTH);
        luaCenter.add(luaTop, BorderLayout.NORTH);
        luaCenter.add(luaScroll, BorderLayout.CENTER);
        luaPanel.add(luaCenter, BorderLayout.CENTER);

        // 3. Barra Validazione & Pulsante Convalida
        JPanel validationContainer = new JPanel(new BorderLayout(0, 6));
        validationContainer.setOpaque(false);

        JButton validateBtn = new JButton(I18n.t("obj.25")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(0, 160, 210));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(30, 48, 70));
                } else {
                    g2.setColor(new Color(20, 32, 48));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.setColor(BarMapStudio.ACCENT_CYAN);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        validateBtn.setFont(BarMapStudio.font(Font.BOLD, 11));
        validateBtn.setForeground(BarMapStudio.ACCENT_CYAN);
        validateBtn.setFocusPainted(false);
        validateBtn.setBorderPainted(false);
        validateBtn.setContentAreaFilled(false);
        validateBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        validateBtn.setPreferredSize(new Dimension(100, 28));
        validateBtn.addActionListener(e -> validateCurrentLua());

        // Banner Risultato
        validationBanner.setOpaque(true);
        validationBanner.setBackground(new Color(16, 22, 32));
        validationBanner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        validationStatusLabel.setFont(BarMapStudio.font(Font.PLAIN, 11));
        validationStatusLabel.setForeground(BarMapStudio.TEXT_MUTED);
        validationBanner.add(validationStatusLabel, BorderLayout.CENTER);

        validationContainer.add(validateBtn, BorderLayout.NORTH);
        validationContainer.add(validationBanner, BorderLayout.SOUTH);

        luaPanel.add(validationContainer, BorderLayout.SOUTH);

        // 4. Sezione Inferiore: Pulsante "INVIA ALLA MAPPA"
        JPanel bottomActionPanel = new JPanel(new BorderLayout(0, 6));
        bottomActionPanel.setOpaque(false);
        bottomActionPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        sendToMapBtn.setFont(BarMapStudio.font(Font.BOLD, 13));
        sendToMapBtn.setBackground(BarMapStudio.ACCENT_CYAN);
        sendToMapBtn.setForeground(new Color(11, 14, 20));
        sendToMapBtn.setFocusPainted(false);
        sendToMapBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendToMapBtn.setPreferredSize(new Dimension(100, 38));
        sendToMapBtn.setToolTipText(I18n.t("obj.26"));
        sendToMapBtn.addActionListener(e -> sendSelectedEntityToMap());

        bottomActionPanel.add(sendToMapBtn, BorderLayout.CENTER);

        // Assemblaggio Colonna Destra
        JPanel middleWrapper = new JPanel(new BorderLayout());
        middleWrapper.setOpaque(false);
        middleWrapper.add(luaPanel, BorderLayout.CENTER);
        middleWrapper.add(bottomActionPanel, BorderLayout.SOUTH);

        panel.add(middleWrapper, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createPropLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BarMapStudio.font(Font.PLAIN, 11));
        l.setForeground(BarMapStudio.TEXT_MUTED);
        return l;
    }

    private void styleInputField(JTextField f) {
        f.setFont(BarMapStudio.font(Font.PLAIN, 11));
        f.setBackground(BarMapStudio.CARD_INNER);
        f.setForeground(BarMapStudio.TEXT_MAIN);
        f.setCaretColor(BarMapStudio.ACCENT_CYAN);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
    }

    private void styleSpinner(JSpinner s) {
        s.setFont(BarMapStudio.font(Font.PLAIN, 11));
        JComponent ed = s.getEditor();
        if (ed instanceof JSpinner.DefaultEditor de) {
            styleInputField(de.getTextField());
        }
    }

    private JButton createLuaToolBtn(String label, String tooltip, ActionListener action) {
        JButton btn = new JButton(label);
        btn.setFont(BarMapStudio.font(Font.PLAIN, 10));
        btn.setForeground(BarMapStudio.TEXT_MUTED);
        btn.setBackground(new Color(24, 32, 44));
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(60, 20));
        btn.addActionListener(action);
        return btn;
    }

    private void pasteFromClipboard() {
        try {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (cb.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                String text = (String) cb.getData(DataFlavor.stringFlavor);
                if (text != null) {
                    luaTextArea.replaceSelection(text);
                    validateCurrentLua();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void copyToClipboard() {
        try {
            String text = luaTextArea.getText();
            StringSelection sel = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void validateCurrentLua() {
        String code = luaTextArea.getText();
        LuaValidator.ValidationResult res = LuaValidator.validate(code);

        if (res.isValid()) {
            validationBanner.setBackground(new Color(15, 45, 25));
            validationBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 160, 70), 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
            validationStatusLabel.setForeground(new Color(140, 255, 170));
            validationStatusLabel.setText(I18n.t("obj.27") + res.totalLines() + I18n.t("obj.28"));
        } else {
            validationBanner.setBackground(new Color(50, 18, 22));
            validationBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 60, 60), 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
            validationStatusLabel.setForeground(new Color(255, 160, 160));
            validationStatusLabel.setText(res.errorMessage());
        }
    }

    private void selectEntity(ObjectEntity entity) {
        if (entity == null) return;
        this.selectedEntity = entity;

        // Popola Dati
        fillingFields = true;
        try {
            descField.setText(entity.getTooltip() != null ? entity.getTooltip() : "");
            sightSpinner.setValue(entity.getSightRange());
            // il raggio visivo e la descrizione valgono solo per le unita' create dallo Studio
            // nome, descrizione e raggio visivo si possono cambiare negli oggetti dello Studio e nelle copie
            // personalizzate; le unita' di BAR originali restano com'e' nel gioco
            boolean editable = entity.getOfficialUnit() == null || entity.isClone();
            sightSpinner.setEnabled(editable);
            descField.setEnabled(editable);
            nameField.setEnabled(editable);
            nameField.setToolTipText(editable ? null : I18n.t("objx.name.official"));
            sightSpinner.setToolTipText(entity.isClone() ? I18n.t("objclone.sight.tip") : null);
            cloneBtn.setEnabled(entity.getOfficialUnit() != null);
            deleteBtn.setEnabled(entity.getOfficialUnit() == null || entity.isClone());
            idField.setText(entity.getId());
            nameField.setText(entity.getName());
            healthSpinner.setValue(entity.getHealth());
            metalSpinner.setValue(entity.getMetal());
            energySpinner.setValue(entity.getEnergy());
        } finally {
            fillingFields = false;
        }
        footprintXSpinner.setValue(entity.getFootprintX());
        footprintZSpinner.setValue(entity.getFootprintZ());
        healthSpinner.setValue(entity.getHealth());
        metalSpinner.setValue(entity.getMetal());
        energySpinner.setValue(entity.getEnergy());
        scaleSpinner.setValue((double) entity.getScale());
        updateDimensionsUI();
        refreshOwnerUI();
        refreshScriptHint(entity);

        // Popola Script Lua
        loadEditor();
        validateCurrentLua();

        // Popola Viewport 3D o Anteprima
        // le unita' di BAR passano sempre dal caricamento dedicato (texture e verso delle facce giusti),
        // anche quando la vista 3D della mappa ha gia' messo il modello nell'oggetto
        if (entity.getModel3D() != null && entity.getOfficialUnit() == null) {
            canvas3D.setEntity(entity);
            centerContainer.removeAll();
            centerContainer.add(canvas3D, BorderLayout.CENTER);
        } else if (entity.getOfficialUnit() != null) {
            // Unita' ufficiale BAR: il modello 3D viene letto dal gioco solo adesso, al clic
            loadOfficialModel(entity);
        } else {
            // Crea un modello placeholder procedurale
            ObjModel3D cube = ObjModel3D.createProceduralCube(3.0f);
            canvas3D.setModel(cube, entity.getScale());
            centerContainer.removeAll();
            centerContainer.add(canvas3D, BorderLayout.CENTER);
        }

        centerContainer.revalidate();
        centerContainer.repaint();
    }

    private void cloneSelectedOfficial() {
        ObjectEntity source = selectedEntity;
        if (source == null || source.getOfficialUnit() == null) return;
        Object answer = JOptionPane.showInputDialog(this, I18n.t("objclone.ask", source.getName()), I18n.t("objclone.title"),
            JOptionPane.PLAIN_MESSAGE, null, null, source.getName() + " II");
        if (answer == null) return;
        String name = answer.toString().trim();
        if (name.isEmpty()) return;
        // id nuovo e unico: quello dell'originale piu' un numero progressivo
        String base = source.getOfficialUnit().id().toLowerCase(Locale.ROOT);
        String id;
        int n = 1;
        do {
            id = base + "_c" + n++;
        } while (hasEntityId(id));
        ObjectEntity copy = ObjectEntity.cloneOfficial(source, id, name);
        // la copia nasce con lo script di base nell'editor, pronto da modificare: quello Lua dell'originale se
        // esiste, altrimenti una bozza Lua con il sorgente originale in fondo come riferimento
        try {
            copy.setLuaScript(OfficialModels.starterLua(source.getOfficialUnit().id(), source.getName()));
        } catch (RuntimeException ex) {
            copy.setLuaScript("");
        }
        allEntities.add(0, copy);
        activeFactionFilter = activeFactionFilter;
        selectedEntity = copy;
        refillList();
        entityList.setSelectedValue(copy, true);
    }

    /** Toglie dalla libreria l'oggetto selezionato, se e' tuo (mesh importata o copia personalizzata). */
    private void deleteSelectedCustom() {
        ObjectEntity target = selectedEntity;
        if (target == null || (target.getOfficialUnit() != null && !target.isClone())) {
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this, I18n.t("objdel.ask", target.getName()), I18n.t("objdel.title"),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) {
            return;
        }
        int index = Math.max(0, entityList.getSelectedIndex());
        allEntities.remove(target);
        ReadyObjectsManager.getInstance().removeReadyObject(target);
        selectedEntity = null;
        refillList();
        if (!listModel.isEmpty()) {
            entityList.setSelectedIndex(Math.min(index, listModel.getSize() - 1));
        }
    }

    private boolean hasEntityId(String id) {
        for (ObjectEntity e : allEntities) {
            if (e.getId().equalsIgnoreCase(id)) return true;
        }
        return false;
    }

    /** Salva nell'oggetto selezionato il testo dell'editor, nel campo della scheda aperta. */
    private void commitEditor() {
        ObjectEntity e = selectedEntity;
        if (e == null || fillingLua || (e.getOfficialUnit() != null && !e.isClone())) return;
        if (editingDef) {
            e.setDefLua(luaTextArea.getText());
        } else {
            e.setLuaScript(luaTextArea.getText());
        }
    }

    /** Riempie l'editor con il testo della scheda aperta. */
    private void loadEditor() {
        ObjectEntity e = selectedEntity;
        fillingLua = true;
        try {
            String text = e == null ? "" : (editingDef ? e.getDefLua() : e.getLuaScript());
            luaTextArea.setText(text != null ? text : "");
            luaTextArea.setCaretPosition(0);
        } finally {
            fillingLua = false;
        }
    }

    /** Finestra che spiega la differenza tra le due schede dell'editor, con un esempio per ciascuna. */
    private void showEditorHelp() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), I18n.t("objhelp.title"), java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(18, 22, 16, 22));
        String[][] parts = {
            {null, "objhelp.intro"},
            {"objhelp.b.title", "objhelp.b.text"},
            {"objhelp.d.title", "objhelp.d.text"},
            {"objhelp.rule.title", "objhelp.rule.text"},
        };
        Color[] colours = {null, BarMapStudio.ACCENT_CYAN, BarMapStudio.ACCENT_AMBER, new Color(0, 255, 136)};
        for (int i = 0; i < parts.length; i++) {
            if (parts[i][0] != null) {
                JLabel heading = new JLabel(I18n.t(parts[i][0]));
                heading.setFont(BarMapStudio.fontCond(Font.BOLD, 14));
                heading.setForeground(colours[i]);
                heading.setAlignmentX(0f);
                root.add(Box.createVerticalStrut(12));
                root.add(heading);
                root.add(Box.createVerticalStrut(4));
            }
            JLabel text = new JLabel("<html><div style='width:520px'>" + I18n.t(parts[i][1]) + "</div></html>");
            text.setFont(BarMapStudio.font(Font.PLAIN, 12));
            text.setAlignmentX(0f);
            root.add(text);
        }
        root.add(Box.createVerticalStrut(14));
        JButton close = new JButton(I18n.t("objhelp.close"));
        close.setAlignmentX(0f);
        close.addActionListener(e -> dialog.dispose());
        root.add(close);
        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(close);
        dialog.pack();
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    private void switchEditor(boolean toDefinition) {
        if (editingDef == toDefinition) return;
        commitEditor();
        editingDef = toDefinition;
        if (componentsBtn != null) {
            componentsBtn.setVisible(toDefinition);
        }
        editorHint.setText(I18n.t(toDefinition ? "objhint.def" : "objhint.script"));
        loadEditor();
        if (selectedEntity != null) {
            refreshScriptHint(selectedEntity);
        }
        validateCurrentLua();
    }

    /** Apre la finestra dei componenti: unita' di BAR, loro pezzi e impostazioni, da aggiungere alla definizione. */
    private void openComponentsDialog() {
        ObjectEntity target = selectedEntity;
        if (target == null || !editingDef || (target.getOfficialUnit() != null && !target.isClone())) return;
        ensureOfficialUnitsLoaded();
        List<Units.Unit> units = new ArrayList<>();
        for (ObjectEntity e : allEntities) {
            if (e.getOfficialUnit() != null && !e.isClone()) {
                units.add(e.getOfficialUnit());
            }
        }
        if (units.isEmpty()) {
            JOptionPane.showMessageDialog(this, I18n.t("defdlg.nounits"), I18n.t("defdlg.title"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        DefComponentDialog dialog = new DefComponentDialog(SwingUtilities.getWindowAncestor(this), units, code -> {
            // il codice va in fondo alla definizione, su righe sue
            String text = luaTextArea.getText();
            String separator = text.isEmpty() || text.endsWith("\n\n") ? "" : (text.endsWith("\n") ? "\n" : "\n\n");
            luaTextArea.append(separator + code);
            luaTextArea.setCaretPosition(luaTextArea.getDocument().getLength());
        });
        dialog.setVisible(true);
        validateCurrentLua();
    }

    /** Riga sopra l'editor Lua, e blocco dei campi per le unita' di BAR originali. */
    private void refreshScriptHint(ObjectEntity entity) {
        boolean original = entity.getOfficialUnit() != null && !entity.isClone();
        // l'unita' originale di BAR non si modifica: per cambiarla si crea una copia personalizzata
        healthSpinner.setEnabled(!original);
        metalSpinner.setEnabled(!original);
        energySpinner.setEnabled(!original);
        luaTextArea.setEditable(!original);
        useScriptBox.setVisible(entity.isClone() && !editingDef);
        useScriptBox.setSelected(entity.isClone() && entity.isCustomScriptEnabled());
        if (editingDef) {
            scriptHint.setText("<html>" + I18n.t(original ? "objdef.hint.original" : "objdef.hint") + "</html>");
            scriptHint.setToolTipText(null);
            return;
        }
        if (entity.getOfficialUnit() == null) {
            scriptHint.setText(" ");
            scriptHint.setToolTipText(null);
            return;
        }
        final String unitId = entity.getOfficialUnit().id();
        scriptHint.setText(I18n.t(original ? "objscript.original" : "objscript.clone", "..."));
        // nomi dei pezzi e dello script si leggono dal gioco in background
        Thread loader = new Thread(() -> {
            String script = OfficialModels.scriptName(unitId);
            java.util.List<String> pieces = OfficialModels.pieceNames(unitId);
            String scriptShown = script != null ? script : "-";
            SwingUtilities.invokeLater(() -> {
                if (selectedEntity != entity) return;
                scriptHint.setText("<html>" + I18n.t(original ? "objscript.original" : "objscript.clone", scriptShown)
                    + (pieces.isEmpty() ? "" : "<br>" + I18n.t("objscript.pieces", pieces.size(), String.join(", ", pieces.subList(0, Math.min(14, pieces.size()))) + (pieces.size() > 14 ? ", ..." : ""))) + "</html>");
                scriptHint.setToolTipText(pieces.isEmpty() ? null : "<html><div style='width:420px'>" + String.join(", ", pieces) + "</div></html>");
            });
        }, "script-hint-loader");
        loader.setDaemon(true);
        loader.start();
    }

    private void saveFieldsToEntity() {
        ObjectEntity e = selectedEntity;
        if (e == null || fillingFields) return;
        // le unita' di BAR originali non vengono mai modificate
        if (e.getOfficialUnit() != null && !e.isClone()) return;
        commitEditor();
        String name = nameField.getText().trim();
        if (!name.isEmpty() && !name.equals(e.getName())) {
            e.setName(name);
            entityList.repaint();
        }
        e.setTooltip(descField.getText().trim());
        e.setHealth(((Number) healthSpinner.getValue()).intValue());
        e.setMetal(((Number) metalSpinner.getValue()).intValue());
        e.setEnergy(((Number) energySpinner.getValue()).intValue());
        e.setSightRange(((Number) sightSpinner.getValue()).intValue());
    }

    private void redrawSelected() {
        if (selectedEntity == null) return;
        if (selectedEntity.getOfficialUnit() != null) {
            loadOfficialModel(selectedEntity);
        } else {
            canvas3D.setEntity(selectedEntity);
        }
    }

    /** Colore con cui disegnare un'unita' di BAR: quello scelto, se la copia ha uno schieramento libero, altrimenti quello della fazione. */
    private static Color displayColour(ObjectEntity entity) {
        if (entity.isClone() && entity.isColourFree() && entity.getTintRgb() != null) {
            return new Color(entity.getTintRgb());
        }
        return switch (entity.getOwner()) {
            case ARMADA -> Units.factionColour("Armada");
            case CORTEX -> Units.factionColour("Cortex");
            case LEGION -> Units.factionColour("Legion");
            case HOSTILE, RAPTORS, SCAVENGERS -> new Color(200, 60, 60);
            case NEUTRAL -> new Color(150, 150, 150);
            default -> Units.factionColour(entity.getFaction());
        };
    }

    /** Allinea la scheda Schieramento & Colore all'oggetto selezionato. */
    private void refreshOwnerUI() {
        ObjectEntity e = selectedEntity;
        if (e == null) return;
        boolean official = e.getOfficialUnit() != null;
        boolean feature = e.getCategory() == ObjectEntity.Category.STATIC_PROP;
        fillingOwner = true;
        try {
            int index = 0;
            for (int i = 0; i < OWNER_ORDER.length; i++) {
                if (OWNER_ORDER[i] == e.getOwner()) index = i;
            }
            ownerCombo.setSelectedIndex(index);
        } finally {
            fillingOwner = false;
        }
        // gli oggetti statici sono elementi del paesaggio: non appartengono a nessuna squadra
        ownerCombo.setEnabled(!feature);
        // colore libero: sempre per i modelli importati; per le copie di unita' BAR solo con uno schieramento
        // che non appartiene a una squadra (ostile o neutrale); mai per le unita' BAR originali
        boolean colourable = !official || (e.isClone() && e.isColourFree());
        Integer tint = colourable ? e.getTintRgb() : null;
        tintBtn.setEnabled(colourable);
        tintResetBtn.setEnabled(colourable && tint != null);
        tintBtn.setBackground(tint != null ? new Color(tint) : BarMapStudio.CARD_INNER);
        tintBtn.setText(tint != null ? String.format("#%06X", tint) : I18n.t(e.isClone() && !colourable ? "objown.colour.team" : "objown.colour.none"));
        tintBtn.setForeground(tint != null && (((tint >> 16) & 255) + ((tint >> 8) & 255) + (tint & 255)) > 380 ? Color.BLACK : BarMapStudio.TEXT_MAIN);
        String hint;
        if (feature) {
            hint = I18n.t("objown.hint.feature");
        } else if (official && !e.isClone()) {
            hint = I18n.t("objown.hint.official");
        } else if (e.isClone() && !e.isColourFree()) {
            hint = I18n.t("objown.hint.clone.team");
        } else if (e.isClone()) {
            hint = I18n.t("objown.hint.clone.free");
        } else {
            hint = switch (e.getOwner()) {
                case HOSTILE -> I18n.t("objown.hint.hostile");
                case NEUTRAL -> I18n.t("objown.hint.neutral");
                case TEAM_A, TEAM_B -> I18n.t("objown.hint.team");
                case ARMADA, CORTEX, LEGION -> I18n.t("objown.hint.faction");
                case RAPTORS, SCAVENGERS -> I18n.t("objown.hint.monsters");
                default -> I18n.t("objown.hint.auto");
            };
        }
        ownerHint.setText("<html>" + hint + "</html>");
    }

    private void updateDimensionsUI() {
        if (selectedEntity == null) return;
        dimLabel.setText(String.format(Locale.ROOT, I18n.t("obj.29"),
            selectedEntity.getScaledHeight(), selectedEntity.getScaledWidth()));
        int fx = Math.max(1, Math.round(selectedEntity.getScaledWidth() / 16.0f));
        int fz = Math.max(1, Math.round(selectedEntity.getScaledDepth() / 16.0f));
        footprintXSpinner.setValue(fx);
        footprintZSpinner.setValue(fz);
        selectedEntity.setFootprintX(fx);
        selectedEntity.setFootprintZ(fz);
    }

    private JButton createPresetBtn(String label, float targetHeight) {
        JButton b = new JButton(label);
        b.setFont(BarMapStudio.font(Font.BOLD, 9));
        b.setForeground(BarMapStudio.ACCENT_CYAN);
        b.setBackground(new Color(18, 30, 44));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1, true),
            new EmptyBorder(3, 4, 3, 4)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> {
            if (selectedEntity != null) {
                selectedEntity.autoScaleToHeight(targetHeight);
                scaleSpinner.setValue((double) selectedEntity.getScale());
                canvas3D.setEntityScale(selectedEntity.getScale());
                updateDimensionsUI();
            }
        });
        return b;
    }

    /**
     * Legge dal gioco il modello S3O e la texture dell'unita' selezionata, in background. Se il modello
     * non si trova si ripiega sull'immagine di costruzione (buildpic).
     */
    private void loadOfficialModel(ObjectEntity entity) {
        centerContainer.removeAll();
        centerContainer.add(canvas3D, BorderLayout.CENTER);
        String unitId = entity.getOfficialUnit().id();
        Color team = displayColour(entity);
        // la stessa unita' puo' essere mostrata con colori diversi (originale e copie)
        String id = unitId + "|" + team.getRGB();
        OfficialView cached = officialViews.get(id);
        if (cached != null) {
            showOfficial(entity, cached);
            return;
        }
        canvas3D.setLoading(I18n.t("obj3d.loading"));
        Thread loader = new Thread(() -> {
            OfficialView view = null;
            try {
                S3OParser.S3OModel sm = OfficialModels.loadFull(unitId);
                if (sm != null) {
                    BufferedImage tex = null;
                    if (sm.tex1 != null && !sm.tex1.isBlank()) {
                        synchronized (this) {
                            if (officialTextures == null) {
                                officialTextures = new ModelTextureCache(() -> {
                                    Path data = barDataSupplier != null ? barDataSupplier.get() : null;
                                    return data != null ? data : BarFolder.find(null);
                                });
                            }
                        }
                        // la texture con il colore di fazione e' condivisa da tutte le unita' che usano lo stesso atlante
                        String texKey = sm.tex1.toLowerCase(Locale.ROOT) + "|" + team.getRGB();
                        synchronized (officialMixed) {
                            tex = officialMixed.get(texKey);
                        }
                        if (tex == null) {
                            tex = Object3DCanvas.mixTeamColour(officialTextures.loadGameTexture(sm.tex1, 1), team);
                            if (tex != null) {
                                synchronized (officialMixed) {
                                    officialMixed.put(texKey, tex);
                                }
                            }
                        }
                    }
                    view = new OfficialView(sm, tex);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            final OfficialView result = view;
            SwingUtilities.invokeLater(() -> {
                if (result != null) {
                    officialViews.put(id, result);
                }
                if (selectedEntity != entity) {
                    return;
                }
                if (result != null) {
                    showOfficial(entity, result);
                } else {
                    loadOfficialPreview(entity.getOfficialUnit());
                    centerContainer.revalidate();
                    centerContainer.repaint();
                }
            });
        }, "official-model-loader");
        loader.setDaemon(true);
        loader.start();
    }

    private void showOfficial(ObjectEntity entity, OfficialView view) {
        canvas3D.showGameModel(entity, view.source, view.texture, entity.getScale());
        // misure reali prese dal modello del gioco
        dimLabel.setText(String.format(Locale.ROOT, I18n.t("obj.29"),
            view.model.getMaxY() * entity.getScale(), (view.model.getMaxX() - view.model.getMinX()) * entity.getScale()));
    }

    private void loadOfficialPreview(Units.Unit unit) {
        Path data = barDataSupplier != null ? barDataSupplier.get() : null;
        if (data == null) data = BarFolder.find(null);
        final Path finalData = data;

        centerContainer.removeAll();
        preview2dLabel.setIcon(null);
        preview2dLabel.setText(I18n.t("obj.30"));
        preview2dLabel.setFont(BarMapStudio.font(Font.BOLD, 13));
        preview2dLabel.setForeground(BarMapStudio.TEXT_MUTED);
        centerContainer.add(preview2dLabel, BorderLayout.CENTER);

        Thread loader = new Thread(() -> {
            BufferedImage img = Units.readPicture(finalData, unit);
            ImageIcon icon = (img != null) ? new ImageIcon(img.getScaledInstance(256, 256, Image.SCALE_SMOOTH)) : null;
            SwingUtilities.invokeLater(() -> {
                if (icon != null) {
                    preview2dLabel.setText("");
                    preview2dLabel.setIcon(icon);
                } else {
                    // Fallback a modello procedurale
                    ObjModel3D cube = ObjModel3D.createProceduralCube(2.5f);
                    canvas3D.setModel(cube);
                    centerContainer.removeAll();
                    centerContainer.add(canvas3D, BorderLayout.CENTER);
                    centerContainer.revalidate();
                    centerContainer.repaint();
                }
            });
        }, "preview-loader");
        loader.setDaemon(true);
        loader.start();
    }

    private void sendSelectedEntityToMap() {
        if (selectedEntity == null) return;

        // Salva le modifiche apportate nell'inspector
        saveFieldsToEntity();
        selectedEntity.setFootprintX((Integer) footprintXSpinner.getValue());
        selectedEntity.setFootprintZ((Integer) footprintZSpinner.getValue());
        selectedEntity.setHealth((Integer) healthSpinner.getValue());
        selectedEntity.setMetal((Integer) metalSpinner.getValue());
        selectedEntity.setEnergy((Integer) energySpinner.getValue());
        commitEditor();

        // Invia al gestore della palette pronti
        ReadyObjectsManager.getInstance().addReadyObject(selectedEntity);

        // Feedback visivo immediato sul pulsante
        String origText = sendToMapBtn.getText();
        sendToMapBtn.setText(I18n.t("obj.31"));
        sendToMapBtn.setBackground(new Color(50, 205, 120));
        Timer t = new Timer(1500, e -> {
            sendToMapBtn.setText(origText);
            sendToMapBtn.setBackground(BarMapStudio.ACCENT_CYAN);
        });
        t.setRepeats(false);
        t.start();
    }

    private void importCustomMeshDialog() {
        JFileChooser chooser = new JFileChooser();
        FileChoosers.detailed(chooser);
        chooser.setDialogTitle(I18n.t("obj.32"));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(I18n.t("obj.33"), "obj"));

        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                ObjModel3D parsed = ObjParser.parse(file);
                String baseName = file.getName().replaceAll("(?i)\\.obj$", "");
                String cleanId = "custom_" + baseName.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "_");

                ObjectEntity custom = new ObjectEntity(
                    cleanId,
                    baseName,
                    ObjectEntity.Category.CUSTOM_MESH,
                    "Custom",
                    3, 3,
                    1000, 200, 0,
                    I18n.t("obj.34") + file.getName(),
                    parsed,
                    ""
                );
                custom.setObjFilePath(file.toPath());
                // script di animazione: se accanto all'OBJ c'e' <nome>.lua viene caricato da solo
                File luaFile = new File(file.getParentFile(), baseName + ".lua");
                if (luaFile.isFile()) {
                    custom.setLuaScript(java.nio.file.Files.readString(luaFile.toPath()));
                }

                allEntities.add(0, custom);
                refillList();
                entityList.setSelectedValue(custom, true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    I18n.t("obj.35") + ex.getMessage(),
                    I18n.t("obj.36"),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

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

    // Viewport Centrale
    private final Object3DCanvas canvas3D = new Object3DCanvas();
    private final JPanel centerContainer = new JPanel(new BorderLayout());
    private final JLabel preview2dLabel = new JLabel("", SwingConstants.CENTER);

    // Inspector Destra
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JSpinner footprintXSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 32, 1));
    private final JSpinner footprintZSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 32, 1));
    private final JSpinner healthSpinner = new JSpinner(new SpinnerNumberModel(500, 1, 999999, 50));
    private final JSpinner metalSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 999999, 10));
    private final JSpinner energySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 50));

    // Editor Lua & Validatore
    private final JTextArea luaTextArea = new JTextArea();
    private final JLabel validationStatusLabel = new JLabel(" ");
    private final JPanel validationBanner = new JPanel(new BorderLayout(8, 0));
    private final JButton sendToMapBtn = new JButton("INVIA ALLA MAPPA");

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
        allEntities.add(ObjectEntity.createDefaultWindmill());
        allEntities.add(ObjectEntity.createDefaultPineTree());
        allEntities.add(ObjectEntity.createDefaultRock());
        allEntities.add(ObjectEntity.createDefaultMissileBoss());
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

        JLabel title = new JLabel("LIBRERIA ELEMENTI");
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
        searchField.putClientProperty("JTextField.placeholderText", "Cerca per nome o ID...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refillList(); }
            public void removeUpdate(DocumentEvent e) { refillList(); }
            public void changedUpdate(DocumentEvent e) { refillList(); }
        });
        headerPanel.add(searchField, BorderLayout.CENTER);

        // Filtri Categorie
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        filters.setOpaque(false);
        String[] cats = {"TUTTI", "BAR", "STATICI", "ANIMATI", "BOSS"};
        ButtonGroup filterGroup = new ButtonGroup();
        for (String cat : cats) {
            JToggleButton btn = new JToggleButton(cat, cat.equals("TUTTI"));
            filterGroup.add(btn);
            styleFilterToggle(btn);
            btn.addActionListener(e -> {
                activeCategoryFilter = cat;
                if ("BAR".equals(cat) || "TUTTI".equals(cat)) {
                    ensureOfficialUnitsLoaded();
                }
                refillList();
            });
            filters.add(btn);
        }
        headerPanel.add(filters, BorderLayout.SOUTH);

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
                String sub = entity.getCategory().getLabel() + " | " + entity.getFaction();
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
        JButton importBtn = new JButton("+ IMPORTA MESH 3D (.obj)") {
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
        panel.add(importBtn, BorderLayout.SOUTH);

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

    private void styleFilterToggle(JToggleButton btn) {
        btn.setFont(BarMapStudio.font(Font.BOLD, 10));
        btn.setForeground(BarMapStudio.TEXT_MUTED);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(54, 22));
    }

    private void refillList() {
        String filter = searchField.getText().trim().toLowerCase(Locale.ROOT);
        List<ObjectEntity> filtered = new ArrayList<>();

        for (ObjectEntity e : allEntities) {
            if (!activeCategoryFilter.equals("TUTTI")) {
                if (activeCategoryFilter.equals("BAR") && e.getCategory() != ObjectEntity.Category.OFFICIAL_UNIT) continue;
                if (activeCategoryFilter.equals("STATICI") && e.getCategory() != ObjectEntity.Category.STATIC_PROP) continue;
                if (activeCategoryFilter.equals("ANIMATI") && e.getCategory() != ObjectEntity.Category.ANIMATED_SCENERY) continue;
                if (activeCategoryFilter.equals("BOSS") && e.getCategory() != ObjectEntity.Category.BOSS_UNIT) continue;
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
        JPanel propsCard = new JPanel(new GridLayout(0, 2, 8, 6));
        propsCard.setOpaque(false);
        propsCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1),
            " DATI ENTITA' ",
            0, 0,
            BarMapStudio.fontCond(Font.BOLD, 11),
            BarMapStudio.ACCENT_CYAN
        ));

        idField.setEditable(false);
        styleInputField(idField);
        styleInputField(nameField);

        propsCard.add(createPropLabel("ID Univoco:"));
        propsCard.add(idField);
        propsCard.add(createPropLabel("Nome:"));
        propsCard.add(nameField);
        propsCard.add(createPropLabel("Footprint X (16 elmo):"));
        propsCard.add(footprintXSpinner);
        propsCard.add(createPropLabel("Footprint Z (16 elmo):"));
        propsCard.add(footprintZSpinner);
        propsCard.add(createPropLabel("Punti Vita (HP):"));
        propsCard.add(healthSpinner);
        propsCard.add(createPropLabel("Metallo:"));
        propsCard.add(metalSpinner);
        propsCard.add(createPropLabel("Energia:"));
        propsCard.add(energySpinner);

        panel.add(propsCard, BorderLayout.NORTH);

        // 2. Editor Script Lua Spazioso e Pulito
        JPanel luaPanel = new JPanel(new BorderLayout(0, 6));
        luaPanel.setOpaque(false);
        luaPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1),
            " SCRIPT LUA (ANIMAZIONE & GADGET) ",
            0, 0,
            BarMapStudio.fontCond(Font.BOLD, 11),
            BarMapStudio.ACCENT_CYAN
        ));

        // Barra strumenti editor: Incolla / Copia / Pulisci
        JPanel luaToolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        luaToolBar.setOpaque(false);

        JButton pasteBtn = createLuaToolBtn("Incolla", "Incolla codice dagli appunti di sistema", e -> pasteFromClipboard());
        JButton copyBtn = createLuaToolBtn("Copia", "Copia codice negli appunti", e -> copyToClipboard());
        JButton clearBtn = createLuaToolBtn("Pulisci", "Svuota editor", e -> luaTextArea.setText(""));

        luaToolBar.add(pasteBtn);
        luaToolBar.add(copyBtn);
        luaToolBar.add(clearBtn);

        luaPanel.add(luaToolBar, BorderLayout.NORTH);

        // Area di testo con font Monospaced dark-tech
        luaTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        luaTextArea.setBackground(new Color(12, 16, 22));
        luaTextArea.setForeground(new Color(220, 235, 250));
        luaTextArea.setCaretColor(BarMapStudio.ACCENT_CYAN);
        luaTextArea.setTabSize(4);
        luaTextArea.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane luaScroll = new JScrollPane(luaTextArea);
        luaScroll.setBorder(BorderFactory.createLineBorder(new Color(30, 42, 58), 1));
        luaPanel.add(luaScroll, BorderLayout.CENTER);

        // 3. Barra Validazione & Pulsante Convalida
        JPanel validationContainer = new JPanel(new BorderLayout(0, 6));
        validationContainer.setOpaque(false);

        JButton validateBtn = new JButton("CONVALIDA CODICE LUA") {
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
        sendToMapBtn.setToolTipText("Invia questo elemento alla palette oggetti pronti della mappa (MAPS)");
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
            validationStatusLabel.setText("Sintassi valida (" + res.totalLines() + " righe). Nessun errore.");
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
        idField.setText(entity.getId());
        nameField.setText(entity.getName());
        footprintXSpinner.setValue(entity.getFootprintX());
        footprintZSpinner.setValue(entity.getFootprintZ());
        healthSpinner.setValue(entity.getHealth());
        metalSpinner.setValue(entity.getMetal());
        energySpinner.setValue(entity.getEnergy());

        // Popola Script Lua
        luaTextArea.setText(entity.getLuaScript() != null ? entity.getLuaScript() : "");
        validateCurrentLua();

        // Popola Viewport 3D o Anteprima
        if (entity.getModel3D() != null) {
            canvas3D.setModel(entity.getModel3D());
            centerContainer.removeAll();
            centerContainer.add(canvas3D, BorderLayout.CENTER);
        } else if (entity.getOfficialUnit() != null) {
            // Unita' ufficiale BAR: carica buildpic se non ha mesh
            loadOfficialPreview(entity.getOfficialUnit());
        } else {
            // Crea un modello placeholder procedurale
            ObjModel3D cube = ObjModel3D.createProceduralCube(3.0f);
            canvas3D.setModel(cube);
            centerContainer.removeAll();
            centerContainer.add(canvas3D, BorderLayout.CENTER);
        }

        centerContainer.revalidate();
        centerContainer.repaint();
    }

    private void loadOfficialPreview(Units.Unit unit) {
        Path data = barDataSupplier != null ? barDataSupplier.get() : null;
        if (data == null) data = BarFolder.find(null);
        final Path finalData = data;

        centerContainer.removeAll();
        preview2dLabel.setIcon(null);
        preview2dLabel.setText("Caricamento anteprima BAR...");
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
        selectedEntity.setName(nameField.getText().trim());
        selectedEntity.setFootprintX((Integer) footprintXSpinner.getValue());
        selectedEntity.setFootprintZ((Integer) footprintZSpinner.getValue());
        selectedEntity.setHealth((Integer) healthSpinner.getValue());
        selectedEntity.setMetal((Integer) metalSpinner.getValue());
        selectedEntity.setEnergy((Integer) energySpinner.getValue());
        selectedEntity.setLuaScript(luaTextArea.getText());

        // Invia al gestore della palette pronti
        ReadyObjectsManager.getInstance().addReadyObject(selectedEntity);

        // Feedback visivo immediato sul pulsante
        String origText = sendToMapBtn.getText();
        sendToMapBtn.setText("AGGIUNTO AGLI OGGETTI PRONTI!");
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
        chooser.setDialogTitle("Seleziona File Modello 3D Wavefront (.obj)");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Modelli Wavefront (.obj)", "obj"));

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
                    "Mesh 3D personalizzata importata da " + file.getName(),
                    parsed,
                    ""
                );
                custom.setObjFilePath(file.toPath());

                allEntities.add(0, custom);
                refillList();
                entityList.setSelectedValue(custom, true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Errore durante l'importazione del file .obj:\n" + ex.getMessage(),
                    "Errore Importazione Mesh",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

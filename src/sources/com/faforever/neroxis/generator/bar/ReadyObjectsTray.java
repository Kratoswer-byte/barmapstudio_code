package com.faforever.neroxis.generator.bar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Pannello dedicato alla Palette degli Oggetti & Unità Pronti per il piazzamento in MAPS.
 * Permette di selezionare elementi, importare direttamente file .obj 3D,
 * aprire lo studio completo e monitorare gli oggetti piazzati.
 */
public final class ReadyObjectsTray extends JPanel {

    private final JPanel cardsPanel = new JPanel();
    private final JLabel headerTitle = new JLabel("OGGETTI & UNITA' PRONTI (0)");
    private final JButton openStudioBtn = new JButton("+ Studio 3D");
    private final JButton importObjBtn = new JButton("+ Importa .OBJ");
    private final JLabel activeStatusLabel = new JLabel("Clicca un oggetto per piazzarlo sulla mappa.");
    private final JPanel quickScaleBox = new JPanel(new BorderLayout(0, 3));
    private final JSpinner quickScaleSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.01, 500.0, 0.5));
    private final JLabel quickDimLabel = new JLabel("Alt: --");
    private final JScrollPane scrollPane;
    private final JLabel placedCountLabel = new JLabel("Piazzati: 0 oggetti");
    private final JButton clearPlacedBtn = new JButton("Svuota Piazzati");
    private final JPanel footerPanel = new JPanel(new BorderLayout(6, 0));

    private ObjectEntity activeSelectedEntity = null;
    private Consumer<ObjectEntity> onEntitySelectedCallback = null;
    private Runnable onOpenStudioCallback = null;
    private Runnable onClearPlacedCallback = null;

    public ReadyObjectsTray(Consumer<ObjectEntity> onEntitySelected) {
        this(onEntitySelected, null, null);
    }

    public ReadyObjectsTray(
        Consumer<ObjectEntity> onEntitySelected,
        Runnable onOpenStudio,
        Runnable onClearPlaced
    ) {
        this.onEntitySelectedCallback = onEntitySelected;
        this.onOpenStudioCallback = onOpenStudio;
        this.onClearPlacedCallback = onClearPlaced;

        setLayout(new BorderLayout(0, 8));
        setBackground(BarMapStudio.BG_CARD);
        setOpaque(false);

        // 1. Header con titolo, azioni veloci e banner di selezione attiva
        JPanel topBox = new JPanel();
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));
        topBox.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout(6, 0));
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        headerTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        headerTitle.setForeground(BarMapStudio.ACCENT_CYAN);
        header.add(headerTitle, BorderLayout.WEST);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        headerActions.setOpaque(false);

        importObjBtn.setFont(BarMapStudio.font(Font.BOLD, 10));
        importObjBtn.setForeground(new Color(255, 190, 70));
        importObjBtn.setBackground(new Color(36, 30, 20));
        importObjBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 100, 20), 1, true),
            new EmptyBorder(3, 7, 3, 7)
        ));
        importObjBtn.setFocusPainted(false);
        importObjBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        importObjBtn.setToolTipText("Importa un file .obj 3D Wavefront direttamente nella palette");
        importObjBtn.addActionListener(e -> importCustomObj());
        headerActions.add(importObjBtn);

        openStudioBtn.setFont(BarMapStudio.font(Font.BOLD, 10));
        openStudioBtn.setForeground(BarMapStudio.ACCENT_CYAN);
        openStudioBtn.setBackground(new Color(18, 32, 48));
        openStudioBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1, true),
            new EmptyBorder(3, 7, 3, 7)
        ));
        openStudioBtn.setFocusPainted(false);
        openStudioBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openStudioBtn.setToolTipText("Apri Objects & Units Studio per gestire tutti i parametri e script");
        openStudioBtn.addActionListener(e -> {
            if (onOpenStudioCallback != null) onOpenStudioCallback.run();
        });
        headerActions.add(openStudioBtn);

        header.add(headerActions, BorderLayout.EAST);
        topBox.add(header);
        topBox.add(Box.createVerticalStrut(4));

        // Banner di stato selezione attiva
        activeStatusLabel.setFont(BarMapStudio.font(Font.PLAIN, 10));
        activeStatusLabel.setForeground(BarMapStudio.TEXT_MUTED);
        activeStatusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(30, 42, 60), 1, true),
            new EmptyBorder(4, 6, 4, 6)
        ));
        activeStatusLabel.setOpaque(true);
        activeStatusLabel.setBackground(BarMapStudio.CARD_INNER);
        activeStatusLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        activeStatusLabel.setAlignmentX(0f);
        topBox.add(activeStatusLabel);

        // Box di Scala e Dimensioni rapide
        quickScaleBox.setOpaque(true);
        quickScaleBox.setBackground(new Color(16, 24, 34));
        quickScaleBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 56, 75), 1, true),
            new EmptyBorder(3, 5, 3, 5)
        ));
        quickScaleBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        quickScaleBox.setAlignmentX(0f);
        quickScaleBox.setVisible(false);

        JPanel row1 = new JPanel(new BorderLayout(6, 0));
        row1.setOpaque(false);
        JPanel spinWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        spinWrap.setOpaque(false);
        JLabel lblSc = new JLabel("Scala:");
        lblSc.setFont(BarMapStudio.font(Font.PLAIN, 10));
        lblSc.setForeground(BarMapStudio.TEXT_MUTED);
        spinWrap.add(lblSc);
        quickScaleSpinner.setPreferredSize(new Dimension(52, 20));
        quickScaleSpinner.setFont(BarMapStudio.font(Font.PLAIN, 10));
        spinWrap.add(quickScaleSpinner);
        row1.add(spinWrap, BorderLayout.WEST);

        quickDimLabel.setFont(BarMapStudio.font(Font.BOLD, 10));
        quickDimLabel.setForeground(new Color(255, 205, 50));
        quickDimLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        row1.add(quickDimLabel, BorderLayout.CENTER);

        JPanel presetRow = new JPanel(new GridLayout(1, 4, 3, 0));
        presetRow.setOpaque(false);
        presetRow.add(createMiniPresetBtn("45m Cmd", 45.0f));
        presetRow.add(createMiniPresetBtn("20m T1", 20.0f));
        presetRow.add(createMiniPresetBtn("60m Edif", 60.0f));
        presetRow.add(createMiniPresetBtn("35m Prop", 35.0f));

        quickScaleBox.add(row1, BorderLayout.NORTH);
        quickScaleBox.add(presetRow, BorderLayout.SOUTH);

        quickScaleSpinner.addChangeListener(e -> {
            if (syncingQuickScale) return;
            if (activeSelectedEntity != null) {
                float sc = ((Number) quickScaleSpinner.getValue()).floatValue();
                activeSelectedEntity.setScale(sc);
                refreshCards(ReadyObjectsManager.getInstance().getReadyObjects());
            }
        });

        topBox.add(Box.createVerticalStrut(3));
        topBox.add(quickScaleBox);

        add(topBox, BorderLayout.NORTH);

        // 2. Contenitore Cards Scrollabile a tutta altezza
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setOpaque(false);

        scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(30, 42, 60), 1));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Footer Tray (Piazzati sulla mappa + Tasto Svuota)
        footerPanel.setOpaque(false);
        footerPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 26));
        footerPanel.setBorder(new EmptyBorder(4, 2, 2, 2));

        placedCountLabel.setFont(BarMapStudio.font(Font.PLAIN, 10));
        placedCountLabel.setForeground(BarMapStudio.TEXT_MUTED);
        footerPanel.add(placedCountLabel, BorderLayout.CENTER);

        clearPlacedBtn.setFont(BarMapStudio.font(Font.BOLD, 10));
        clearPlacedBtn.setForeground(new Color(255, 120, 120));
        clearPlacedBtn.setBackground(new Color(40, 20, 20));
        clearPlacedBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(120, 40, 40), 1, true),
            new EmptyBorder(2, 8, 2, 8)
        ));
        clearPlacedBtn.setFocusPainted(false);
        clearPlacedBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearPlacedBtn.setToolTipText("Rimuove tutti gli oggetti personalizzati piazzati sulla mappa");
        clearPlacedBtn.addActionListener(e -> {
            if (onClearPlacedCallback != null) onClearPlacedCallback.run();
        });
        footerPanel.add(clearPlacedBtn, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);

        // Iscriviti agli aggiornamenti di ReadyObjectsManager
        ReadyObjectsManager.getInstance().addListener(this::refreshCards);
        refreshCards(ReadyObjectsManager.getInstance().getReadyObjects());
    }

    private void importCustomObj() {
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
                    "Mesh 3D importata da " + file.getName(),
                    parsed,
                    ""
                );
                custom.setObjFilePath(file.toPath());

                ReadyObjectsManager.getInstance().addReadyObject(custom);
                activeSelectedEntity = custom;
                refreshCards(ReadyObjectsManager.getInstance().getReadyObjects());
                if (onEntitySelectedCallback != null) {
                    onEntitySelectedCallback.accept(custom);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Errore durante l'importazione del file .obj:\n" + ex.getMessage(),
                    "Errore Importazione Mesh",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void setOnEntitySelected(Consumer<ObjectEntity> callback) {
        this.onEntitySelectedCallback = callback;
    }

    public void setOnOpenStudio(Runnable callback) {
        this.onOpenStudioCallback = callback;
    }

    public void setOnClearPlaced(Runnable callback) {
        this.onClearPlacedCallback = callback;
    }

    public ObjectEntity getActiveSelectedEntity() {
        return activeSelectedEntity;
    }

    public void clearSelection() {
        this.activeSelectedEntity = null;
        if (activeStatusLabel != null) {
            activeStatusLabel.setText("Clicca un oggetto per piazzarlo sulla mappa.");
            activeStatusLabel.setForeground(BarMapStudio.TEXT_MUTED);
        }
        refreshCards(ReadyObjectsManager.getInstance().getReadyObjects());
    }

    public void updatePlacedCount(int count) {
        placedCountLabel.setText("Piazzati: " + count + (count == 1 ? " oggetto" : " oggetti"));
        clearPlacedBtn.setEnabled(count > 0);
        clearPlacedBtn.setVisible(count > 0);
    }

    private boolean syncingQuickScale = false;

    private void updateQuickScaleUI() {
        ObjectEntity e = activeSelectedEntity;
        boolean show = e != null && e.getCategory() != ObjectEntity.Category.OFFICIAL_UNIT;
        quickScaleBox.setVisible(show);
        if (!show) return;
        syncingQuickScale = true;
        try {
            if (Math.abs(((Number) quickScaleSpinner.getValue()).floatValue() - e.getScale()) > 0.0001f) {
                quickScaleSpinner.setValue((double) e.getScale());
            }
        } finally {
            syncingQuickScale = false;
        }
        int fx = Math.max(1, Math.round(e.getScaledWidth() / 16.0f));
        int fz = Math.max(1, Math.round(e.getScaledDepth() / 16.0f));
        e.setFootprintX(fx);
        e.setFootprintZ(fz);
        quickDimLabel.setText(String.format(Locale.ROOT, "Alt %.1fm | Largh %.1fm", e.getScaledHeight(), e.getScaledWidth()));
        Color c = e.getScaledHeight() < 8f ? new Color(255, 110, 110) : new Color(255, 205, 50);
        quickDimLabel.setForeground(c);
        quickDimLabel.setToolTipText(e.getScaledHeight() < 8f
            ? "Attenzione: oggetto molto piccolo in gioco (Comandante = 45m)"
            : "Dimensioni reali in gioco (1 elmo ~ 1m)");
    }

    private JButton createMiniPresetBtn(String label, float targetHeight) {
        JButton b = new JButton(label);
        b.setFont(BarMapStudio.font(Font.BOLD, 9));
        b.setForeground(BarMapStudio.ACCENT_CYAN);
        b.setBackground(new Color(18, 30, 44));
        b.setBorder(BorderFactory.createLineBorder(new Color(40, 56, 75), 1, true));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setToolTipText("Scala automaticamente l'oggetto a un'altezza di " + (int) targetHeight + "m");
        b.addActionListener(ev -> {
            if (activeSelectedEntity != null) {
                activeSelectedEntity.autoScaleToHeight(targetHeight);
                refreshCards(ReadyObjectsManager.getInstance().getReadyObjects());
            }
        });
        return b;
    }

    private void refreshCards(List<ObjectEntity> list) {
        cardsPanel.removeAll();
        headerTitle.setText("OGGETTI & UNITA' PRONTI (" + list.size() + ")");

        if (activeSelectedEntity != null) {
            activeStatusLabel.setText("▶ ATTIVO: " + activeSelectedEntity.getName() + " (Piazza con Click)");
            activeStatusLabel.setForeground(BarMapStudio.ACCENT_CYAN);
        } else {
            activeStatusLabel.setText("Clicca un oggetto per piazzarlo sulla mappa.");
            activeStatusLabel.setForeground(BarMapStudio.TEXT_MUTED);
        }
        updateQuickScaleUI();

        if (list.isEmpty()) {
            JPanel emptyBox = new JPanel();
            emptyBox.setLayout(new BoxLayout(emptyBox, BoxLayout.Y_AXIS));
            emptyBox.setOpaque(false);
            emptyBox.setBorder(BorderFactory.createEmptyBorder(16, 8, 16, 8));
            emptyBox.setAlignmentX(0f);

            JLabel emptyLbl = new JLabel("Nessun oggetto pronto nella palette.");
            emptyLbl.setFont(BarMapStudio.font(Font.PLAIN, 11));
            emptyLbl.setForeground(BarMapStudio.TEXT_MUTED);
            emptyLbl.setAlignmentX(0.5f);
            emptyBox.add(emptyLbl);
            emptyBox.add(Box.createVerticalStrut(8));

            JButton gotoStudioBtn = new JButton("+ Apri Studio Oggetti");
            gotoStudioBtn.setFont(BarMapStudio.font(Font.BOLD, 10));
            gotoStudioBtn.setForeground(BarMapStudio.ACCENT_CYAN);
            gotoStudioBtn.setBackground(new Color(20, 32, 48));
            gotoStudioBtn.setBorder(BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1));
            gotoStudioBtn.setFocusPainted(false);
            gotoStudioBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            gotoStudioBtn.setAlignmentX(0.5f);
            gotoStudioBtn.addActionListener(e -> {
                if (onOpenStudioCallback != null) onOpenStudioCallback.run();
            });
            emptyBox.add(gotoStudioBtn);

            cardsPanel.add(emptyBox);
        } else {
            for (ObjectEntity entity : list) {
                cardsPanel.add(createEntityCard(entity));
                cardsPanel.add(Box.createVerticalStrut(4));
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel createEntityCard(ObjectEntity entity) {
        boolean isSelected = (activeSelectedEntity != null && activeSelectedEntity.getId().equals(entity.getId()));

        JPanel card = new JPanel(new BorderLayout(6, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected) {
                    g2.setColor(new Color(0, 110, 160, 220));
                } else {
                    g2.setColor(new Color(20, 28, 42));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(isSelected ? BarMapStudio.ACCENT_CYAN : new Color(42, 56, 78));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 6));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        card.setPreferredSize(new Dimension(220, 46));
        card.setAlignmentX(0f);

        JLabel nameLbl = new JLabel(entity.getName());
        nameLbl.setFont(BarMapStudio.font(Font.BOLD, 11));
        nameLbl.setForeground(isSelected ? Color.WHITE : BarMapStudio.TEXT_MAIN);

        JLabel catLbl = new JLabel(entity.getCategory().getLabel() + " (" + entity.getFootprintX() + "x" + entity.getFootprintZ() + ")"
            + (entity.getModel3D() != null ? String.format(Locale.ROOT, " | %.0fm x%.1f", entity.getScaledHeight(), entity.getScale()) : ""));
        catLbl.setFont(BarMapStudio.font(Font.PLAIN, 9));
        catLbl.setForeground(isSelected ? new Color(200, 240, 255) : BarMapStudio.TEXT_MUTED);

        JPanel textStack = new JPanel(new BorderLayout());
        textStack.setOpaque(false);
        textStack.add(nameLbl, BorderLayout.NORTH);
        textStack.add(catLbl, BorderLayout.SOUTH);

        card.add(textStack, BorderLayout.CENTER);

        // Pulsante Rimozione [x]
        JButton removeBtn = new JButton("x");
        removeBtn.setFont(BarMapStudio.font(Font.BOLD, 9));
        removeBtn.setForeground(BarMapStudio.TEXT_MUTED);
        removeBtn.setFocusPainted(false);
        removeBtn.setBorderPainted(false);
        removeBtn.setContentAreaFilled(false);
        removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeBtn.addActionListener(e -> {
            ReadyObjectsManager.getInstance().removeReadyObject(entity);
            if (activeSelectedEntity != null && activeSelectedEntity.getId().equals(entity.getId())) {
                activeSelectedEntity = null;
                if (onEntitySelectedCallback != null) onEntitySelectedCallback.accept(null);
            }
        });
        card.add(removeBtn, BorderLayout.EAST);

        // Click sulla card seleziona l'oggetto come tool attivo
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getSource() == removeBtn) return;
                activeSelectedEntity = isSelected ? null : entity;
                refreshCards(ReadyObjectsManager.getInstance().getReadyObjects());
                if (onEntitySelectedCallback != null) {
                    onEntitySelectedCallback.accept(activeSelectedEntity);
                }
            }
        });

        return card;
    }
}

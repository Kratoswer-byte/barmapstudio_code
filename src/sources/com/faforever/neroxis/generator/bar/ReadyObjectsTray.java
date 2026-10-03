package com.faforever.neroxis.generator.bar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Contenitore integrato nel pannello destro di MAPS (sotto Densita' Reclaim e sopra Genera Mappa).
 * Mostra l'elenco degli oggetti e unita' preparati nello Studio e pronti per essere piazzati
 * interattivamente sul canvas della mappa con supporto per selezione, rimozione e svuotamento.
 */
public final class ReadyObjectsTray extends JPanel {

    private final JPanel cardsPanel = new JPanel();
    private final JLabel headerTitle = new JLabel("OGGETTI & UNITA' PRONTI (0)");
    private final JButton openStudioBtn = new JButton("+ Studio");
    private final JButton toggleBtn = new JButton("[-]");
    private final JScrollPane scrollPane;
    private final JLabel placedCountLabel = new JLabel("Piazzati: 0");
    private final JButton clearPlacedBtn = new JButton("Svuota");
    private final JPanel footerPanel = new JPanel(new BorderLayout(6, 0));
    private boolean isCollapsed = false;

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

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BarMapStudio.CARD_INNER);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1, true),
            new EmptyBorder(8, 8, 8, 8)
        ));
        setAlignmentX(0f);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        // 1. Header Tray
        JPanel header = new JPanel(new BorderLayout(6, 0));
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        header.setAlignmentX(0f);

        headerTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        headerTitle.setForeground(BarMapStudio.ACCENT_CYAN);
        header.add(headerTitle, BorderLayout.CENTER);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        headerActions.setOpaque(false);

        openStudioBtn.setFont(BarMapStudio.font(Font.BOLD, 10));
        openStudioBtn.setForeground(BarMapStudio.ACCENT_CYAN);
        openStudioBtn.setFocusPainted(false);
        openStudioBtn.setBorderPainted(false);
        openStudioBtn.setContentAreaFilled(false);
        openStudioBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openStudioBtn.setToolTipText("Apri Objects & Units Studio");
        openStudioBtn.addActionListener(e -> {
            if (onOpenStudioCallback != null) onOpenStudioCallback.run();
        });
        headerActions.add(openStudioBtn);

        toggleBtn.setFont(BarMapStudio.font(Font.BOLD, 10));
        toggleBtn.setForeground(BarMapStudio.TEXT_MUTED);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleBtn.addActionListener(e -> toggleCollapse());
        headerActions.add(toggleBtn);

        header.add(headerActions, BorderLayout.EAST);
        add(header);
        add(Box.createVerticalStrut(6));

        // 2. Contenitore Cards Scrollabile
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setOpaque(false);

        scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(30, 42, 60), 1));
        scrollPane.setPreferredSize(new Dimension(240, 110));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        scrollPane.setAlignmentX(0f);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        add(scrollPane);

        // 3. Footer Tray (Piazzati sulla mappa + Tasto Svuota)
        footerPanel.setOpaque(false);
        footerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        footerPanel.setAlignmentX(0f);
        footerPanel.setBorder(new EmptyBorder(6, 2, 0, 2));

        placedCountLabel.setFont(BarMapStudio.font(Font.PLAIN, 10));
        placedCountLabel.setForeground(BarMapStudio.TEXT_MUTED);
        footerPanel.add(placedCountLabel, BorderLayout.CENTER);

        clearPlacedBtn.setFont(BarMapStudio.font(Font.BOLD, 9));
        clearPlacedBtn.setForeground(new Color(255, 120, 120));
        clearPlacedBtn.setFocusPainted(false);
        clearPlacedBtn.setBorderPainted(false);
        clearPlacedBtn.setContentAreaFilled(false);
        clearPlacedBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearPlacedBtn.setToolTipText("Rimuove tutti gli oggetti personalizzati piazzati sulla mappa");
        clearPlacedBtn.addActionListener(e -> {
            if (onClearPlacedCallback != null) onClearPlacedCallback.run();
        });
        footerPanel.add(clearPlacedBtn, BorderLayout.EAST);

        add(footerPanel);

        // Iscriviti agli aggiornamenti di ReadyObjectsManager
        ReadyObjectsManager.getInstance().addListener(this::refreshCards);
        refreshCards(ReadyObjectsManager.getInstance().getReadyObjects());
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
        refreshCards(ReadyObjectsManager.getInstance().getReadyObjects());
    }

    public void updatePlacedCount(int count) {
        placedCountLabel.setText("Piazzati: " + count + (count == 1 ? " oggetto" : " oggetti"));
        clearPlacedBtn.setEnabled(count > 0);
        clearPlacedBtn.setVisible(count > 0);
    }

    private void toggleCollapse() {
        isCollapsed = !isCollapsed;
        scrollPane.setVisible(!isCollapsed);
        footerPanel.setVisible(!isCollapsed);
        toggleBtn.setText(isCollapsed ? "[+]" : "[-]");
        revalidate();
        repaint();
    }

    private void refreshCards(List<ObjectEntity> list) {
        cardsPanel.removeAll();
        headerTitle.setText("OGGETTI PRONTI (" + list.size() + ")");

        if (list.isEmpty()) {
            JPanel emptyBox = new JPanel();
            emptyBox.setLayout(new BoxLayout(emptyBox, BoxLayout.Y_AXIS));
            emptyBox.setOpaque(false);
            emptyBox.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
            emptyBox.setAlignmentX(0f);

            JLabel emptyLbl = new JLabel("Nessun oggetto pronto in lista.");
            emptyLbl.setFont(BarMapStudio.font(Font.PLAIN, 10));
            emptyLbl.setForeground(BarMapStudio.TEXT_MUTED);
            emptyLbl.setAlignmentX(0.5f);
            emptyBox.add(emptyLbl);
            emptyBox.add(Box.createVerticalStrut(4));

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
        card.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 6));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        card.setPreferredSize(new Dimension(220, 44));
        card.setAlignmentX(0f);

        JLabel nameLbl = new JLabel(entity.getName());
        nameLbl.setFont(BarMapStudio.font(Font.BOLD, 11));
        nameLbl.setForeground(isSelected ? Color.WHITE : BarMapStudio.TEXT_MAIN);

        JLabel catLbl = new JLabel(entity.getCategory().getLabel() + " (" + entity.getFootprintX() + "x" + entity.getFootprintZ() + ")");
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

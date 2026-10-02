package com.faforever.neroxis.generator.bar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;

/**
 * Contenitore / Tray posizionato in basso a destra nella schermata MAPS.
 * Mostra l'elenco degli oggetti e unità preparati nello Studio e pronti
 * per essere posizionati interattivamente sul canvas della mappa.
 */
public final class ReadyObjectsTray extends JPanel {

    private final JPanel cardsPanel = new JPanel();
    private final JLabel headerTitle = new JLabel("OGGETTI PRONTI (0)");
    private final JButton toggleBtn = new JButton("[-]");
    private final JScrollPane scrollPane;
    private boolean isCollapsed = false;

    private ObjectEntity activeSelectedEntity = null;
    private Consumer<ObjectEntity> onEntitySelectedCallback = null;

    public ReadyObjectsTray(Consumer<ObjectEntity> onEntitySelected) {
        this.onEntitySelectedCallback = onEntitySelected;
        setLayout(new BorderLayout());
        setOpaque(false);

        // Header Tray
        JPanel header = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(16, 22, 32, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(40, 56, 78));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 8));
        header.setPreferredSize(new Dimension(220, 28));

        headerTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        headerTitle.setForeground(BarMapStudio.ACCENT_CYAN);
        header.add(headerTitle, BorderLayout.CENTER);

        toggleBtn.setFont(BarMapStudio.font(Font.BOLD, 10));
        toggleBtn.setForeground(BarMapStudio.TEXT_MUTED);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleBtn.addActionListener(e -> toggleCollapse());
        header.add(toggleBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Contenitore Cards
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setOpaque(false);

        scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(30, 42, 60), 1));
        scrollPane.setPreferredSize(new Dimension(230, 150));
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);

        add(scrollPane, BorderLayout.CENTER);

        // Iscriviti agli aggiornamenti di ReadyObjectsManager
        ReadyObjectsManager.getInstance().addListener(this::refreshCards);
    }

    public void setOnEntitySelected(Consumer<ObjectEntity> callback) {
        this.onEntitySelectedCallback = callback;
    }

    public ObjectEntity getActiveSelectedEntity() {
        return activeSelectedEntity;
    }

    public void clearSelection() {
        this.activeSelectedEntity = null;
        refreshCards(ReadyObjectsManager.getInstance().getReadyObjects());
    }

    private void toggleCollapse() {
        isCollapsed = !isCollapsed;
        scrollPane.setVisible(!isCollapsed);
        toggleBtn.setText(isCollapsed ? "[+]" : "[-]");
        revalidate();
        repaint();
    }

    private void refreshCards(List<ObjectEntity> list) {
        cardsPanel.removeAll();
        headerTitle.setText("OGGETTI PRONTI (" + list.size() + ")");

        if (list.isEmpty()) {
            JLabel emptyLbl = new JLabel("Nessun oggetto inviato");
            emptyLbl.setFont(BarMapStudio.font(Font.PLAIN, 10));
            emptyLbl.setForeground(BarMapStudio.TEXT_MUTED);
            emptyLbl.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            cardsPanel.add(emptyLbl);
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
                    g2.setColor(new Color(18, 25, 36, 210));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(isSelected ? BarMapStudio.ACCENT_CYAN : new Color(35, 48, 68));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 6));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setMaximumSize(new Dimension(220, 42));
        card.setPreferredSize(new Dimension(220, 42));

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

package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.exporter.PreviewGenerator;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.util.dds.DDSReader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/Units.class */
final class Units {
    private final Supplier<Path> data;
    private final DefaultListModel<Unit> model = new DefaultListModel<>();
    private final JList<Unit> list = new JList<>(this.model);
    private final JTextField search = new JTextField();
    private final Map<String, JToggleButton> factionButtons = new LinkedHashMap();
    private final JLabel status = new JLabel(" ");
    private final JLabel picture = new JLabel();
    private final JLabel name = new JLabel(" ");
    private final JLabel tooltip = new JLabel(" ");
    private final JLabel where = new JLabel(" ");
    private final JPanel numbers = new JPanel(new GridLayout(0, 2, 12, 6));
    private final Map<String, ImageIcon> pictures = new HashMap();
    private final List<Unit> compared = new ArrayList();
    private final JPanel compareGrid = new JPanel();
    private final JPanel compareHeads = new JPanel();
    private final JPanel compareRows = new JPanel();
    private final JLabel compareHint = new JLabel();
    private final JButton compareButton = new JButton("Compare");
    private final JButton clearButton = new JButton("Clear");
    private volatile List<Unit> all = List.of();
    private volatile boolean loading;
    private Path loadedFrom;
    private static final String[] FACTIONS = {"Armada", "Cortex", "Legion", "Other"};
    private static final Color ARMADA = new Color(74, 158, 255);
    private static final Color CORTEX = new Color(255, 84, 72);
    private static final Color LEGION = new Color(63, 200, 90);
    private static final String[] ROWS = {"Metal", "Energy", "Build time", "Health", "Speed"};
    private static final Pattern NUMBER = Pattern.compile("(?m)^\\s*(metalcost|buildcostmetal|energycost|buildcostenergy|buildtime|health|maxdamage|speed|maxvelocity|techlevel)\\s*=\\s*([0-9.]+)");
    private static final Pattern TEXT = Pattern.compile("(?m)^\\s*(buildpic|i18n_en_humanname|i18n_en_tooltip|subfolder|name|description)\\s*=\\s*\"([^\"]*)\"");

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/Units$Unit.class */
    static final class Unit extends Record {
        private final String id;
        private final String name;
        private final String tooltip;
        private final String faction;
        private final int tier;
        private final int metal;
        private final int energy;
        private final int buildTime;
        private final int health;
        private final float speed;
        private final String folder;
        private final String picture;

        Unit(String id, String name, String tooltip, String faction, int tier, int metal, int energy, int buildTime, int health, float speed, String folder, String picture) {
            this.id = id;
            this.name = name;
            this.tooltip = tooltip;
            this.faction = faction;
            this.tier = tier;
            this.metal = metal;
            this.energy = energy;
            this.buildTime = buildTime;
            this.health = health;
            this.speed = speed;
            this.folder = folder;
            this.picture = picture;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Unit.class), Unit.class, "id;name;tooltip;faction;tier;metal;energy;buildTime;health;speed;folder;picture", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->id:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->tooltip:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->faction:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->tier:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->metal:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->energy:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->buildTime:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->health:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->speed:F", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->folder:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->picture:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Unit.class), Unit.class, "id;name;tooltip;faction;tier;metal;energy;buildTime;health;speed;folder;picture", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->id:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->tooltip:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->faction:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->tier:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->metal:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->energy:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->buildTime:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->health:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->speed:F", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->folder:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->picture:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Unit.class, Object.class), Unit.class, "id;name;tooltip;faction;tier;metal;energy;buildTime;health;speed;folder;picture", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->id:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->tooltip:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->faction:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->tier:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->metal:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->energy:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->buildTime:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->health:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->speed:F", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->folder:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Units$Unit;->picture:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String id() {
            return this.id;
        }

        public String name() {
            return this.name;
        }

        public String tooltip() {
            return this.tooltip;
        }

        public String faction() {
            return this.faction;
        }

        public int tier() {
            return this.tier;
        }

        public int metal() {
            return this.metal;
        }

        public int energy() {
            return this.energy;
        }

        public int buildTime() {
            return this.buildTime;
        }

        public int health() {
            return this.health;
        }

        public float speed() {
            return this.speed;
        }

        public String folder() {
            return this.folder;
        }

        public String picture() {
            return this.picture;
        }

        String searchText() {
            return (this.id + " " + this.name + " " + this.tooltip + " " + this.folder).toLowerCase(Locale.ROOT);
        }
    }

    static Color factionColour(String faction) {
        switch (faction) {
            case "Armada":
                return ARMADA;
            case "Cortex":
                return CORTEX;
            case "Legion":
                return LEGION;
            default:
                return BarLauncher.TEXT;
        }
    }

    private static String hex(Color color) {
        return String.format("#%02x%02x%02x", Integer.valueOf(color.getRed()), Integer.valueOf(color.getGreen()), Integer.valueOf(color.getBlue()));
    }

    Units(Supplier<Path> data) {
        this.data = data;
    }

    JComponent view() {
        JPanel view = new BarLauncher.BackgroundPanel(new BorderLayout(16, 0));
        view.setBorder(BorderFactory.createEmptyBorder(18, 22, 16, 22));
        this.search.putClientProperty("JTextField.placeholderText", "Search units");
        this.search.getDocument().addDocumentListener(new DocumentListener(this) { // from class: com.faforever.neroxis.generator.bar.Units.1
            final /* synthetic */ Units this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void insertUpdate(DocumentEvent e) {
                this.this$0.refill();
            }

            public void removeUpdate(DocumentEvent e) {
                this.this$0.refill();
            }

            public void changedUpdate(DocumentEvent e) {
                this.this$0.refill();
            }
        });
        JPanel factions = new JPanel(new FlowLayout(0, 6, 0));
        factions.setOpaque(false);
        for (String faction : FACTIONS) {
            JToggleButton button = new JToggleButton(this, faction, !faction.equals("Other")) { // from class: com.faforever.neroxis.generator.bar.Units.2
                final /* synthetic */ Units this$0;

                {
                    Objects.requireNonNull(this);
                    this.this$0 = this;
                }

                protected void paintComponent(Graphics graphics) {
                    Graphics2D g = graphics.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    int arc = getHeight();
                    if (isSelected()) {
                        g.setColor(BarLauncher.ACCENT);
                        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
                        g.setColor(new Color(24, 18, 8));
                    } else {
                        g.setColor(BarLauncher.CARD_BORDER);
                        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
                        g.setColor(getModel().isRollover() ? BarLauncher.TEXT : BarLauncher.MUTED);
                    }
                    g.setFont(getFont());
                    FontMetrics metrics = g.getFontMetrics();
                    g.drawString(getText(), (getWidth() - metrics.stringWidth(getText())) / 2, ((getHeight() + metrics.getAscent()) - metrics.getDescent()) / 2);
                    g.dispose();
                }
            };
            button.setFont(button.getFont().deriveFont(1, 12.0f));
            button.setPreferredSize(new Dimension(Math.max(74, 18 + (8 * faction.length())), 26));
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setRolloverEnabled(true);
            button.setCursor(Cursor.getPredefinedCursor(12));
            button.addActionListener(e -> {
                refill();
            });
            this.factionButtons.put(faction, button);
            factions.add(button);
        }
        this.list.setSelectionMode(0);
        this.list.setBackground(BarLauncher.CARD);
        this.list.setFixedCellHeight(44);
        this.list.setCellRenderer(new DefaultListCellRenderer(this) { // from class: com.faforever.neroxis.generator.bar.Units.3
            final /* synthetic */ Units this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focused) {
                JLabel label = super.getListCellRendererComponent(list, value, index, selected, false);
                Unit unit = (Unit) value;
                label.setText("<html><b style='color:" + Units.hex(Units.factionColour(unit.faction())) + "'>" + Units.escape(unit.name()) + "</b> <span style='color:#8c94a3'>T" + unit.tier() + "</span><br><span style='color:#8c94a3'>" + Units.escape(unit.tooltip()) + "</span></html>");
                label.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
                return label;
            }
        });
        this.list.addListSelectionListener(e2 -> {
            if (!e2.getValueIsAdjusting()) {
                show((Unit) this.list.getSelectedValue());
            }
        });
        JScrollPane scroll = new JScrollPane(this.list);
        scroll.setBorder(BorderFactory.createLineBorder(BarLauncher.CARD_BORDER));
        scroll.getVerticalScrollBar().setUnitIncrement(44);
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(this.search, "North");
        top.add(factions, "South");
        top.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        this.status.setForeground(BarLauncher.MUTED);
        this.status.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 0));
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(columnTitle("Units"), "North");
        head.add(top, "South");
        left.add(head, "North");
        left.add(scroll, "Center");
        left.add(this.status, "South");
        left.setPreferredSize(new Dimension(380, 10));
        JPanel card = new BarLauncher.Surface(new BorderLayout(0, 0), BarLauncher.CARD, 16);
        card.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));
        this.picture.setPreferredSize(new Dimension(300, 300));
        this.picture.setMinimumSize(new Dimension(300, 300));
        this.picture.setMaximumSize(new Dimension(300, 300));
        this.picture.setHorizontalAlignment(0);
        this.picture.setOpaque(true);
        this.picture.setBackground(new Color(13, 14, 17));
        this.picture.setBorder(BorderFactory.createLineBorder(BarLauncher.CARD_BORDER));
        this.name.setForeground(BarLauncher.TEXT);
        this.name.setFont(this.name.getFont().deriveFont(1, 26.0f));
        this.tooltip.setForeground(BarLauncher.TEXT);
        this.tooltip.setFont(this.tooltip.getFont().deriveFont(16.0f));
        this.where.setForeground(BarLauncher.MUTED);
        this.numbers.setOpaque(false);
        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, 1));
        text.setOpaque(false);
        text.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        JLabel[] jLabelArr = {this.name, this.tooltip, this.where};
        int length = jLabelArr.length;
        for (int i = 0; i < length; i++) {
            JLabel jLabel = jLabelArr[i];
            jLabel.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            text.add(jLabel);
            text.add(Box.createVerticalStrut(jLabel == this.name ? 8 : 6));
        }
        JPanel cardHead = new JPanel(new BorderLayout(30, 0));
        cardHead.setOpaque(false);
        JPanel pictureBox = new JPanel(new BorderLayout());
        pictureBox.setOpaque(false);
        pictureBox.add(this.picture, "North");
        cardHead.add(pictureBox, "West");
        JPanel textBox = new JPanel(new BorderLayout());
        textBox.setOpaque(false);
        textBox.add(text, "North");
        cardHead.add(textBox, "Center");
        this.numbers.setLayout(new GridLayout(0, 2, 24, 6));
        JComponent jPanel = new JPanel(new FlowLayout(0, 0, 0));
        jPanel.setOpaque(false);
        jPanel.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        jPanel.add(this.numbers);
        this.compareButton.putClientProperty("JButton.buttonType", "roundRect");
        this.compareButton.setToolTipText("Put this unit next to others, up to four");
        this.compareButton.addActionListener(e3 -> {
            addToComparison((Unit) this.list.getSelectedValue());
        });
        JComponent jPanel2 = new JPanel(new FlowLayout(0, 0, 0));
        jPanel2.setOpaque(false);
        jPanel2.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        jPanel2.add(this.compareButton);
        for (JComponent part : new JComponent[]{jPanel, jPanel2}) {
            part.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            text.add(part);
        }
        card.add(cardHead, "Center");
        JPanel compare = new BarLauncher.Surface(new BorderLayout(0, 14), BarLauncher.CARD, 16);
        compare.setBorder(BorderFactory.createEmptyBorder(20, 30, 22, 30));
        JLabel compareTitle = new JLabel("Compare");
        compareTitle.setForeground(BarLauncher.TEXT);
        compareTitle.setFont(compareTitle.getFont().deriveFont(1, 18.0f));
        this.compareHint.setForeground(BarLauncher.MUTED);
        this.clearButton.putClientProperty("JButton.buttonType", "roundRect");
        this.clearButton.addActionListener(e4 -> {
            this.compared.clear();
            rebuildComparison();
        });
        JPanel compareHead = new JPanel(new BorderLayout(16, 0));
        compareHead.setOpaque(false);
        JPanel compareTitles = new JPanel();
        compareTitles.setLayout(new BoxLayout(compareTitles, 1));
        compareTitles.setOpaque(false);
        compareTitles.add(compareTitle);
        compareTitles.add(Box.createVerticalStrut(4));
        compareTitles.add(this.compareHint);
        compareHead.add(compareTitles, "Center");
        JPanel clearBox = new JPanel(new FlowLayout(2, 0, 0));
        clearBox.setOpaque(false);
        clearBox.add(this.clearButton);
        compareHead.add(clearBox, "East");
        compare.add(compareHead, "North");
        this.compareGrid.setOpaque(false);
        this.compareGrid.setLayout(new BoxLayout(this.compareGrid, 1));
        this.compareHeads.setOpaque(false);
        this.compareRows.setOpaque(false);
        for (JComponent part2 : new JComponent[]{this.compareHeads, this.compareRows}) {
            part2.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            this.compareGrid.add(part2);
        }
        JPanel gridRow = new JPanel(new FlowLayout(0, 0, 0));
        gridRow.setOpaque(false);
        gridRow.add(this.compareGrid);
        JScrollPane compareScroll = new JScrollPane(gridRow);
        compareScroll.setBorder(BorderFactory.createEmptyBorder());
        compareScroll.setOpaque(false);
        compareScroll.getViewport().setOpaque(false);
        compareScroll.setHorizontalScrollBarPolicy(30);
        compareScroll.getVerticalScrollBar().setUnitIncrement(20);
        compare.add(compareScroll, "Center");
        rebuildComparison();
        JPanel right = new JPanel(new BorderLayout(0, 16));
        right.setOpaque(false);
        right.add(card, "North");
        right.add(compare, "Center");
        show(null);
        view.add(left, "West");
        view.add(right, "Center");
        return view;
    }

    private static JLabel columnTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(BarLauncher.TEXT);
        label.setFont(label.getFont().deriveFont(0, 23.0f));
        label.setBorder(BorderFactory.createEmptyBorder(0, 2, 8, 0));
        return label;
    }

    void ensureLoaded() {
        Path folder = this.data.get();
        if (this.loading || folder.equals(this.loadedFrom)) {
            return;
        }
        this.loading = true;
        this.status.setText("Reading the units of the installed game...");
        Thread reader = new Thread(() -> {
            List<Unit> units = read(folder);
            SwingUtilities.invokeLater(() -> {
                this.all = units;
                this.loadedFrom = folder;
                this.loading = false;
                this.pictures.clear();
                this.status.setText(units.isEmpty() ? "No units found. Is Beyond All Reason installed in " + String.valueOf(folder) + "?" : units.size() + " units");
                refill();
            });
        }, "units");
        reader.setDaemon(true);
        reader.start();
    }

    private void refill() {
        String needle = this.search.getText().trim().toLowerCase(Locale.ROOT);
        Unit selected = (Unit) this.list.getSelectedValue();
        this.model.clear();
        int shown = 0;
        for (Unit unit : this.all) {
            JToggleButton button = this.factionButtons.get(unit.faction());
            if (button == null || button.isSelected()) {
                if (needle.isEmpty() || unit.searchText().contains(needle)) {
                    this.model.addElement(unit);
                    shown++;
                }
            }
        }
        if (!this.all.isEmpty()) {
            this.status.setText(shown == this.all.size() ? this.all.size() + " units" : shown + " of " + this.all.size() + " units");
        }
        if (selected != null && this.model.contains(selected)) {
            this.list.setSelectedValue(selected, true);
        } else if (this.model.size() > 0) {
            this.list.setSelectedIndex(0);
        }
    }

    private void show(Unit unit) {
        this.numbers.removeAll();
        if (unit == null) {
            this.picture.setIcon((Icon) null);
            this.name.setText("Pick a unit");
            this.name.setForeground(BarLauncher.TEXT);
            this.tooltip.setText("Every unit of the installed game, with its costs and numbers.");
            this.where.setText(" ");
        } else {
            this.name.setText(unit.name());
            this.name.setForeground(factionColour(unit.faction()));
            this.tooltip.setText(unit.tooltip().isEmpty() ? " " : unit.tooltip());
            this.where.setText(unit.faction() + "  ·  Tier " + unit.tier() + "  ·  " + unit.folder() + "  ·  " + unit.id());
            number("Metal", unit.metal());
            number("Energy", unit.energy());
            number("Build time", unit.buildTime());
            number("Health", unit.health());
            if (unit.speed() > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                number("Speed", Math.round(unit.speed()));
            }
            loadPicture(unit);
        }
        this.numbers.revalidate();
        this.numbers.repaint();
    }

    private void addToComparison(Unit unit) {
        if (unit == null) {
            return;
        }
        this.compared.remove(unit);
        this.compared.add(unit);
        while (this.compared.size() > 4) {
            this.compared.remove(0);
        }
        rebuildComparison();
    }

    private static int value(Unit unit, int row) {
        switch (row) {
            case 0:
                return unit.metal();
            case 1:
                return unit.energy();
            case 2:
                return unit.buildTime();
            case 3:
                return unit.health();
            default:
                return Math.round(unit.speed());
        }
    }

    private void rebuildComparison() {
        this.compareHeads.removeAll();
        this.compareRows.removeAll();
        this.clearButton.setVisible(!this.compared.isEmpty());
        this.compareButton.setText(this.compared.isEmpty() ? "Compare" : "Compare (" + this.compared.size() + ")");
        if (this.compared.isEmpty()) {
            this.compareHint.setText("Pick a unit and press Compare: up to four side by side, the best number of every row in green.");
            this.compareGrid.revalidate();
            this.compareGrid.repaint();
            return;
        }
        this.compareHint.setText(this.compared.size() == 1 ? "One unit; add another to see the differences." : this.compared.size() + " units. Green is the best of a row (lower cost and build time, higher health and speed), the percentages say how far the others are from it.");
        int columns = this.compared.size() + 1;
        this.compareHeads.setLayout(new GridLayout(1, columns, 18, 0));
        this.compareRows.setLayout(new GridLayout(0, columns, 18, 0));
        this.compareHeads.add(cell(" ", BarLauncher.MUTED, 14.0f, false));
        for (Unit unit : this.compared) {
            JPanel head = new JPanel();
            head.setLayout(new BoxLayout(head, 1));
            head.setOpaque(false);
            JComponent jLabel = new JLabel();
            jLabel.setPreferredSize(new Dimension(96, 96));
            jLabel.setMinimumSize(new Dimension(96, 96));
            jLabel.setMaximumSize(new Dimension(96, 96));
            jLabel.setOpaque(true);
            jLabel.setBackground(new Color(13, 14, 17));
            jLabel.setBorder(BorderFactory.createLineBorder(BarLauncher.CARD_BORDER));
            jLabel.setHorizontalAlignment(0);
            thumbnail(unit, jLabel);
            JLabel unitName = cell(unit.name(), factionColour(unit.faction()), 15.0f, true);
            JComponent jComponentCell = cell(unit.tooltip().isEmpty() ? " " : unit.tooltip(), BarLauncher.MUTED, 12.0f, false);
            JButton remove = new JButton("×");
            remove.putClientProperty("JButton.buttonType", "toolBarButton");
            remove.setToolTipText("Take " + unit.name() + " out of the comparison");
            remove.addActionListener(e -> {
                this.compared.remove(unit);
                rebuildComparison();
            });
            JComponent jPanel = new JPanel(new BorderLayout(6, 0));
            jPanel.setOpaque(false);
            jPanel.add(unitName, "Center");
            jPanel.add(remove, "East");
            jPanel.setMaximumSize(new Dimension(200, 30));
            JComponent[] jComponentArr = {jLabel, jPanel, jComponentCell};
            int length = jComponentArr.length;
            for (int i = 0; i < length; i++) {
                JComponent part = jComponentArr[i];
                part.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
                head.add(part);
                head.add(Box.createVerticalStrut(part == jLabel ? 8 : 2));
            }
            head.setPreferredSize(new Dimension(200, 180));
            this.compareHeads.add(head);
        }
        int row = 0;
        while (row < ROWS.length) {
            int best = Integer.MIN_VALUE;
            boolean lowerIsBetter = row < 3;
            int candidates = 0;
            for (Unit unit2 : this.compared) {
                int number = value(unit2, row);
                if (row != 4 || number > 0) {
                    candidates++;
                    best = candidates == 1 ? number : lowerIsBetter ? Math.min(best, number) : Math.max(best, number);
                }
            }
            this.compareRows.add(cell(ROWS[row], BarLauncher.MUTED, 15.0f, false));
            for (Unit unit3 : this.compared) {
                int number2 = value(unit3, row);
                boolean none = row == 4 && number2 <= 0;
                boolean winner = !none && candidates > 1 && number2 == best;
                String shown = none ? "–" : String.format(Locale.ROOT, "%,d", Integer.valueOf(number2));
                if (!none && candidates > 1 && !winner && best > 0) {
                    double difference = (((double) (number2 - best)) * 100.0d) / ((double) best);
                    shown = "<html>" + shown + " <span style='color:#8c94a3;font-size:12px;font-weight:normal'>" + (difference >= 0.0d ? "+" : "−") + Math.round(Math.abs(difference)) + "%</span></html>";
                } else if (winner) {
                    shown = "<html>" + shown + " <span style='color:#8c94a3;font-size:11px;font-weight:normal'>best</span></html>";
                }
                this.compareRows.add(cell(shown, winner ? BarLauncher.GREEN : BarLauncher.TEXT, 17.0f, true));
            }
            row++;
        }
        this.compareGrid.revalidate();
        this.compareGrid.repaint();
    }

    private static JLabel cell(String text, Color colour, float size, boolean bold) {
        JLabel label = new JLabel(text);
        label.setForeground(colour);
        label.setFont(label.getFont().deriveFont(bold ? 1 : 0, size));
        label.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        label.setPreferredSize(new Dimension(200, 30));
        return label;
    }

    private void thumbnail(Unit unit, JLabel target) {
        ImageIcon cached = this.pictures.get(unit.id());
        if (cached != null) {
            target.setIcon(new ImageIcon(cached.getImage().getScaledInstance(94, 94, 4)));
            return;
        }
        Path folder = this.data.get();
        Thread loader = new Thread(() -> {
            BufferedImage image = readPicture(folder, unit);
            if (image == null) {
                return;
            }
            ImageIcon icon = new ImageIcon(image.getScaledInstance(PreviewGenerator.PREVIEW_SIZE, PreviewGenerator.PREVIEW_SIZE, 4));
            SwingUtilities.invokeLater(() -> {
                this.pictures.put(unit.id(), icon);
                target.setIcon(new ImageIcon(icon.getImage().getScaledInstance(94, 94, 4)));
            });
        }, "unit-thumbnail");
        loader.setDaemon(true);
        loader.start();
    }

    private void number(String label, int value) {
        JLabel key = new JLabel(label);
        key.setForeground(BarLauncher.MUTED);
        key.setFont(key.getFont().deriveFont(15.0f));
        key.setPreferredSize(new Dimension(130, 22));
        JLabel number = new JLabel(String.format(Locale.ROOT, "%,d", Integer.valueOf(value)));
        number.setForeground(BarLauncher.TEXT);
        number.setFont(number.getFont().deriveFont(1, 17.0f));
        this.numbers.add(key);
        this.numbers.add(number);
    }

    private void loadPicture(Unit unit) {
        ImageIcon cached = this.pictures.get(unit.id());
        if (cached != null) {
            this.picture.setIcon(cached);
            return;
        }
        this.picture.setIcon((Icon) null);
        Path folder = this.data.get();
        Thread loader = new Thread(() -> {
            BufferedImage image = readPicture(folder, unit);
            if (image == null) {
                return;
            }
            ImageIcon icon = new ImageIcon(image.getScaledInstance(PreviewGenerator.PREVIEW_SIZE, PreviewGenerator.PREVIEW_SIZE, 4));
            SwingUtilities.invokeLater(() -> {
                this.pictures.put(unit.id(), icon);
                if (unit.equals(this.list.getSelectedValue())) {
                    this.picture.setIcon(icon);
                }
            });
        }, "unit-picture");
        loader.setDaemon(true);
        loader.start();
    }

    static BufferedImage readPicture(Path data, Unit unit) {
        byte[] bytes;
        for (String candidate : new String[]{unit.picture(), unit.id() + ".dds", unit.id() + ".png"}) {
            if (!candidate.isEmpty() && (bytes = BarInstall.readGameBytes(data, "unitpics/" + candidate)) != null) {
                try {
                    if (candidate.toLowerCase(Locale.ROOT).endsWith(".dds")) {
                        int[] pixels = DDSReader.read(bytes, DDSReader.ARGB, 0);
                        if (pixels != null) {
                            int width = DDSReader.getWidth(bytes);
                            int height = DDSReader.getHeight(bytes);
                            BufferedImage image = new BufferedImage(width, height, 2);
                            image.setRGB(0, 0, width, height, pixels, 0, width);
                            return image;
                        }
                    } else {
                        BufferedImage image2 = ImageIO.read(new ByteArrayInputStream(bytes));
                        if (image2 != null) {
                            return image2;
                        }
                    }
                } catch (IOException | RuntimeException e) {
                }
            }
        }
        return null;
    }

    static List<Unit> read(Path data) {
        String str;
        try {
            Map<String, byte[]> files = BarInstall.readGameFiles(data, file -> {
                return (file.startsWith("units/") && file.endsWith(".lua")) || file.equals("language/en/units.json");
            });
            Map<String, String> names = new LinkedHashMap<>();
            Map<String, String> tooltips = new LinkedHashMap<>();
            byte[] language = files.remove("language/en/units.json");
            if (language != null) {
                try {
                    Object obj = MiniJson.parse(new String(language, StandardCharsets.UTF_8));
                    if (obj instanceof Map) {
                        Map<?, ?> root = (Map) obj;
                        Object obj2 = root.get("units");
                        if (obj2 instanceof Map) {
                            Map<?, ?> units = (Map) obj2;
                            Object obj3 = units.get("names");
                            if (obj3 instanceof Map) {
                                Map<?, ?> map = (Map) obj3;
                                map.forEach((key, value) -> {
                                    names.put(String.valueOf(key), String.valueOf(value));
                                });
                            }
                            Object obj4 = units.get("descriptions");
                            if (obj4 instanceof Map) {
                                Map<?, ?> map2 = (Map) obj4;
                                map2.forEach((key2, value2) -> {
                                    tooltips.put(String.valueOf(key2), String.valueOf(value2));
                                });
                            }
                        }
                    }
                } catch (RuntimeException e) {
                }
            }
            List<Unit> units2 = new ArrayList<>();
            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                String path = entry.getKey();
                String id = path.substring(path.lastIndexOf(47) + 1, path.length() - 4).toLowerCase(Locale.ROOT);
                String folder = path.substring("units/".length(), Math.max("units/".length(), path.lastIndexOf(47)));
                String text = new String(entry.getValue(), StandardCharsets.UTF_8).replaceAll("(?m)--.*$", "");
                Map<String, String> values = new LinkedHashMap<>();
                Matcher matcher = NUMBER.matcher(text);
                while (matcher.find()) {
                    values.putIfAbsent(matcher.group(1).toLowerCase(Locale.ROOT), matcher.group(2));
                }
                Matcher matcher2 = TEXT.matcher(text);
                while (matcher2.find()) {
                    values.putIfAbsent(matcher2.group(1).toLowerCase(Locale.ROOT), matcher2.group(2));
                }
                if (values.containsKey("metalcost") || values.containsKey("buildcostmetal")) {
                    String unitName = names.getOrDefault(id, values.getOrDefault("i18n_en_humanname", values.getOrDefault("name", id)));
                    String unitTooltip = tooltips.getOrDefault(id, values.getOrDefault("i18n_en_tooltip", values.getOrDefault("description", "")));
                    if (id.startsWith("arm")) {
                        str = "Armada";
                    } else if (id.startsWith("cor")) {
                        str = "Cortex";
                    } else {
                        str = id.startsWith("leg") ? "Legion" : "Other";
                    }
                    String faction = str;
                    String lowerFolder = folder.toLowerCase(Locale.ROOT);
                    if (lowerFolder.contains("scav") || lowerFolder.contains("raptor") || lowerFolder.contains("other") || id.endsWith("_scav")) {
                        faction = "Other";
                    }
                    units2.add(new Unit(id, unitName, unitTooltip, faction, (int) number(values, "techlevel", 1.0f), (int) number(values, "metalcost", number(values, "buildcostmetal", SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT)), (int) number(values, "energycost", number(values, "buildcostenergy", SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT)), (int) number(values, "buildtime", SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), (int) number(values, "health", number(values, "maxdamage", SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT)), number(values, "speed", number(values, "maxvelocity", SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT)), values.getOrDefault("subfolder", folder), values.getOrDefault("buildpic", "")));
                }
            }
            units2.sort(Comparator.comparing(unit -> {
                return Integer.valueOf(factionOrder(unit.faction()));
            }).thenComparingInt((v0) -> {
                return v0.tier();
            }).thenComparing((v0) -> {
                return v0.name();
            }, String.CASE_INSENSITIVE_ORDER));
            return units2;
        } catch (RuntimeException e2) {
            return List.of();
        }
    }

    private static int factionOrder(String faction) {
        for (int i = 0; i < FACTIONS.length; i++) {
            if (FACTIONS[i].equals(faction)) {
                return i;
            }
        }
        return FACTIONS.length;
    }

    private static float number(Map<String, String> values, String key, float fallback) {
        String value = values.get(key);
        if (value == null) {
            return fallback;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}

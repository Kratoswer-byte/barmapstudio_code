package com.faforever.neroxis.generator.bar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Finestra "Componenti": elenca le unita' di BAR e, per quella scelta, i pezzi che si possono prendere
 * (armi, scudi, movimento, singoli valori). Per ogni pezzo mostra le impostazioni possibili e scrive il
 * codice da aggiungere alla scheda Definition dell'oggetto in lavorazione.
 */
public final class DefComponentDialog extends JDialog {

    /** Un pezzo dell'unita' selezionata che si puo' aggiungere alla definizione. */
    private static final class Component3 {
        final String kind;   // WEAPON, SHIELD, MOVE, VALUE
        final String name;   // nome dell'arma o della chiave
        final String label;
        final Map<String, Object> data;
        Component3(String kind, String name, String label, Map<String, Object> data) {
            this.kind = kind;
            this.name = name;
            this.label = label;
            this.data = data;
        }
        @Override public String toString() { return label; }
    }

    /** Una riga delle impostazioni: casella "cambia" + valore. */
    private static final class Setting {
        final String key;
        final JCheckBox use;
        final JSpinner value;
        Setting(String key, JCheckBox use, JSpinner value) {
            this.key = key;
            this.use = use;
            this.value = value;
        }
    }

    private static final String[] WEAPON_SETTINGS = {"range", "reloadtime", "areaofeffect", "weaponvelocity"};
    private static final String[] VALUE_KEYS = {"health", "speed", "sightdistance", "radardistance", "sonardistance", "autoheal",
        "idleautoheal", "idletime", "mass", "metalcost", "energycost", "buildtime", "workertime", "builddistance", "energymake",
        "metalmake", "energystorage", "metalstorage", "cloakcost", "mincloakdistance", "stealth", "canfly", "cruisealtitude",
        "turnrate", "maxacc", "maxdec", "maxslope", "maxwaterdepth", "seismicsignature", "radardistancejam", "explodeas", "selfdestructas"};

    private final List<BarUnits.Unit> units;
    private final Consumer<String> insert;
    private final DefaultListModel<BarUnits.Unit> unitModel = new DefaultListModel<>();
    private final JList<BarUnits.Unit> unitList = new JList<>(unitModel);
    private final DefaultListModel<Component3> componentModel = new DefaultListModel<>();
    private final JList<Component3> componentList = new JList<>(componentModel);
    private final JTextField search = new JTextField();
    private final JPanel settingsPanel = new JPanel();
    private final JTextArea preview = new JTextArea(6, 30);
    private final JLabel status = new JLabel(" ");
    private final JButton addBtn = new JButton(I18n.t("defdlg.add"));
    private String faction = "ALL";

    private final List<Setting> settings = new ArrayList<>();
    private JCheckBox autoRadius;
    private JSpinner damageValue;
    private JCheckBox damageUse;
    private JTextField valueField;

    public DefComponentDialog(Window owner, List<BarUnits.Unit> units, Consumer<String> insert) {
        super(owner, I18n.t("defdlg.title"), ModalityType.APPLICATION_MODAL);
        this.units = units;
        this.insert = insert;

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(14, 14, 12, 14));
        root.setBackground(BarMapStudio.BG_DARK);

        JLabel intro = new JLabel("<html>" + I18n.t("defdlg.intro") + "</html>");
        intro.setForeground(BarMapStudio.TEXT_MUTED);
        intro.setFont(BarMapStudio.font(Font.PLAIN, 11));
        root.add(intro, BorderLayout.NORTH);

        // --- colonna 1: unita'
        JPanel unitColumn = column(I18n.t("defdlg.units"));
        search.putClientProperty("JTextField.placeholderText", I18n.t("obj.search"));
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refillUnits(); }
            public void removeUpdate(DocumentEvent e) { refillUnits(); }
            public void changedUpdate(DocumentEvent e) { refillUnits(); }
        });
        JPanel factionRow = new JPanel(new GridLayout(1, 5, 3, 0));
        factionRow.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        for (String f : new String[]{"ALL", "Armada", "Cortex", "Legion", "Other"}) {
            JToggleButton b = new JToggleButton(I18n.t("objfac." + f), "ALL".equals(f));
            b.setFont(BarMapStudio.fontCond(Font.BOLD, 10));
            b.setMargin(new Insets(2, 2, 2, 2));
            b.setFocusPainted(false);
            b.addActionListener(e -> {
                faction = f;
                refillUnits();
            });
            group.add(b);
            factionRow.add(b);
        }
        JPanel unitTop = new JPanel(new BorderLayout(0, 5));
        unitTop.setOpaque(false);
        unitTop.add(search, BorderLayout.NORTH);
        unitTop.add(factionRow, BorderLayout.SOUTH);
        unitColumn.add(unitTop, BorderLayout.NORTH);
        unitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        unitList.setBackground(BarMapStudio.CARD_INNER);
        unitList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof BarUnits.Unit u) {
                    label.setText("<html><b>" + escape(u.name()) + "</b> <span style='color:#8c94a3'>" + escape(u.id()) + " · " + escape(u.faction()) + " · T" + u.tier() + "</span></html>");
                }
                label.setBorder(new EmptyBorder(3, 6, 3, 6));
                return label;
            }
        });
        unitList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadComponents(unitList.getSelectedValue());
            }
        });
        unitColumn.add(scroll(unitList), BorderLayout.CENTER);

        // --- colonna 2: componenti
        JPanel componentColumn = column(I18n.t("defdlg.components"));
        componentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        componentList.setBackground(BarMapStudio.CARD_INNER);
        componentList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                label.setBorder(new EmptyBorder(3, 6, 3, 6));
                return label;
            }
        });
        componentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                buildSettings(componentList.getSelectedValue());
            }
        });
        componentColumn.add(scroll(componentList), BorderLayout.CENTER);

        // --- colonna 3: impostazioni e codice
        JPanel settingsColumn = column(I18n.t("defdlg.settings"));
        settingsPanel.setOpaque(false);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        JScrollPane settingsScroll = new JScrollPane(settingsPanel);
        settingsScroll.setBorder(null);
        settingsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        settingsScroll.setOpaque(false);
        settingsScroll.getViewport().setOpaque(false);
        settingsColumn.add(settingsScroll, BorderLayout.CENTER);
        preview.setEditable(false);
        preview.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        preview.setBackground(BarMapStudio.CARD_INNER);
        preview.setForeground(BarMapStudio.TEXT_MAIN);
        preview.setLineWrap(true);
        JPanel previewBox = new JPanel(new BorderLayout(0, 4));
        previewBox.setOpaque(false);
        JLabel previewTitle = new JLabel(I18n.t("defdlg.preview"));
        previewTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 10));
        previewTitle.setForeground(BarMapStudio.TEXT_MUTED);
        previewBox.add(previewTitle, BorderLayout.NORTH);
        previewBox.add(scroll(preview), BorderLayout.CENTER);
        settingsColumn.add(previewBox, BorderLayout.SOUTH);

        JPanel columns = new JPanel(new GridLayout(1, 3, 10, 0));
        columns.setOpaque(false);
        columns.add(unitColumn);
        columns.add(componentColumn);
        columns.add(settingsColumn);
        root.add(columns, BorderLayout.CENTER);

        // --- fondo: stato e pulsanti
        status.setForeground(new Color(80, 220, 140));
        status.setFont(BarMapStudio.font(Font.BOLD, 11));
        addBtn.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        addBtn.setForeground(BarMapStudio.ACCENT_CYAN);
        addBtn.setEnabled(false);
        addBtn.addActionListener(e -> {
            String code = preview.getText();
            if (!code.isBlank()) {
                this.insert.accept(code);
                Component3 c = componentList.getSelectedValue();
                status.setText(I18n.t("defdlg.added", c != null ? c.label : ""));
            }
        });
        JButton closeBtn = new JButton(I18n.t("defdlg.close"));
        closeBtn.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(addBtn);
        buttons.add(closeBtn);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(status, BorderLayout.CENTER);
        bottom.add(buttons, BorderLayout.EAST);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
        setSize(1080, 660);
        setLocationRelativeTo(owner);
        refillUnits();
        buildSettings(null);
    }

    private static JPanel column(String title) {
        return new ColumnPanel(title);
    }

    /** Riquadro con titolo: cio' che gli si aggiunge finisce nel contenuto interno, con il suo margine. */
    private static final class ColumnPanel extends JPanel {
        private JPanel inner;

        ColumnPanel(String title) {
            super(new BorderLayout());
            setOpaque(false);
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1), title, 0, 0,
                BarMapStudio.fontCond(Font.BOLD, 11), BarMapStudio.ACCENT_CYAN));
            JPanel content = new JPanel(new BorderLayout(0, 6));
            content.setOpaque(false);
            content.setBorder(new EmptyBorder(4, 6, 6, 6));
            super.addImpl(content, BorderLayout.CENTER, -1);
            inner = content;
        }

        @Override
        protected void addImpl(Component comp, Object constraints, int index) {
            if (inner == null) {
                super.addImpl(comp, constraints, index);
            } else {
                inner.add(comp, constraints);
            }
        }
    }

    private static JScrollPane scroll(JComponent c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(BarMapStudio.BORDER_COLOR, 1));
        sp.getVerticalScrollBar().setUnitIncrement(18);
        return sp;
    }

    private static String escape(String text) {
        return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;");
    }

    private void refillUnits() {
        String filter = search.getText().trim().toLowerCase(Locale.ROOT);
        unitModel.clear();
        for (BarUnits.Unit u : units) {
            if (!"ALL".equals(faction) && !u.faction().equalsIgnoreCase(faction)) continue;
            if (!filter.isEmpty() && !(u.id() + " " + u.name()).toLowerCase(Locale.ROOT).contains(filter)) continue;
            unitModel.addElement(u);
        }
    }

    /** Legge la definizione dell'unita' dal gioco, in background, e ne elenca i componenti. */
    private void loadComponents(BarUnits.Unit unit) {
        componentModel.clear();
        buildSettings(null);
        if (unit == null) return;
        status.setText(" ");
        componentModel.addElement(new Component3("INFO", "", I18n.t("defdlg.loading"), Map.of()));
        Thread loader = new Thread(() -> {
            List<Component3> found = new ArrayList<>();
            try {
                Map<String, Object> def = LuaTableReader.unitDef(OfficialModels.unitDefText(unit.id()), unit.id());
                if (def != null) {
                    Object weapons = LuaTableReader.field(def, "weapondefs");
                    if (weapons instanceof Map<?, ?> weaponMap) {
                        for (Map.Entry<?, ?> entry : weaponMap.entrySet()) {
                            if (!(entry.getValue() instanceof Map<?, ?> raw)) continue;
                            @SuppressWarnings("unchecked") Map<String, Object> weapon = (Map<String, Object>) raw;
                            String name = String.valueOf(entry.getKey());
                            String type = text(LuaTableReader.field(weapon, "weapontype"));
                            String shown = text(LuaTableReader.field(weapon, "name"));
                            if ("shield".equalsIgnoreCase(type)) {
                                Object shield = LuaTableReader.field(weapon, "shield");
                                String radius = shield instanceof Map<?, ?> s ? text(LuaTableReader.field(cast(s), "radius")) : "?";
                                found.add(new Component3("SHIELD", name, I18n.t("defdlg.kind.shield") + ": " + name + "  (" + I18n.t("defdlg.set.radius") + " " + radius + ")", weapon));
                            } else {
                                found.add(new Component3("WEAPON", name, I18n.t("defdlg.kind.weapon") + ": " + name + (shown.isEmpty() ? "" : " - " + shown)
                                    + "  (" + type + ", " + I18n.t("defdlg.set.range") + " " + text(LuaTableReader.field(weapon, "range")) + ")", weapon));
                            }
                        }
                    }
                    String speed = text(LuaTableReader.field(def, "speed"));
                    String moveClass = text(LuaTableReader.field(def, "movementclass"));
                    if (!speed.isEmpty() || !moveClass.isEmpty() || !text(LuaTableReader.field(def, "canfly")).isEmpty()) {
                        found.add(new Component3("MOVE", "", I18n.t("defdlg.kind.move") + "  (" + (moveClass.isEmpty() ? "-" : moveClass) + ", " + I18n.t("defdlg.speed") + " " + (speed.isEmpty() ? "-" : speed) + ")", def));
                    }
                    for (String key : VALUE_KEYS) {
                        Object value = LuaTableReader.field(def, key);
                        if (value instanceof String scalar && !scalar.isEmpty()) {
                            found.add(new Component3("VALUE", key, I18n.t("defdlg.kind.value") + ": " + key + " = " + scalar, Map.of("value", scalar)));
                        }
                    }
                }
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
            SwingUtilities.invokeLater(() -> {
                if (unitList.getSelectedValue() != unit) return;
                componentModel.clear();
                if (found.isEmpty()) {
                    componentModel.addElement(new Component3("INFO", "", I18n.t("defdlg.none"), Map.of()));
                }
                for (Component3 c : found) {
                    componentModel.addElement(c);
                }
            });
        }, "def-component-loader");
        loader.setDaemon(true);
        loader.start();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> cast(Map<?, ?> map) {
        return (Map<String, Object>) map;
    }

    private static String text(Object value) {
        if (!(value instanceof String s)) return "";
        String t = s.trim();
        if (t.length() >= 2 && t.startsWith("\"") && t.endsWith("\"")) t = t.substring(1, t.length() - 1);
        return t;
    }

    private static double number(Object value, double fallback) {
        try {
            return Double.parseDouble(text(value));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    /** Costruisce il modulo delle impostazioni per il componente scelto. */
    private void buildSettings(Component3 c) {
        settingsPanel.removeAll();
        settings.clear();
        autoRadius = null;
        damageUse = null;
        damageValue = null;
        valueField = null;
        addBtn.setEnabled(c != null && !"INFO".equals(c.kind));
        if (c == null || "INFO".equals(c.kind)) {
            settingsPanel.add(note(I18n.t("defdlg.pick")));
        } else if ("WEAPON".equals(c.kind)) {
            settingsPanel.add(note(I18n.t("defdlg.help.weapon")));
            for (String key : WEAPON_SETTINGS) {
                Object value = LuaTableReader.field(c.data, key);
                if (value instanceof String) {
                    addNumberSetting(key, I18n.t("defdlg.set." + key), number(value, 0), 0, 100000, key.equals("reloadtime") ? 0.1 : 10);
                }
            }
            Object damage = LuaTableReader.field(c.data, "damage");
            if (damage instanceof Map<?, ?> d && LuaTableReader.field(cast(d), "default") instanceof String def) {
                damageUse = new JCheckBox(I18n.t("defdlg.set.damage"));
                damageValue = new JSpinner(new SpinnerNumberModel(number(def, 0), 0.0, 10000000.0, 10.0));
                addRow(damageUse, damageValue);
            }
        } else if ("SHIELD".equals(c.kind)) {
            settingsPanel.add(note(I18n.t("defdlg.help.shield")));
            Object shield = LuaTableReader.field(c.data, "shield");
            Map<String, Object> s = shield instanceof Map<?, ?> m ? cast(m) : Map.of();
            autoRadius = new JCheckBox(I18n.t("defdlg.set.autoradius"), true);
            autoRadius.setOpaque(false);
            autoRadius.setForeground(BarMapStudio.TEXT_MAIN);
            autoRadius.setAlignmentX(0f);
            autoRadius.addActionListener(e -> updatePreview());
            settingsPanel.add(autoRadius);
            addNumberSetting("radius", I18n.t("defdlg.set.radius"), number(LuaTableReader.field(s, "radius"), 300), 20, 10000, 50);
            addNumberSetting("power", I18n.t("defdlg.set.power"), number(LuaTableReader.field(s, "power"), 5000), 100, 10000000, 500);
        } else if ("MOVE".equals(c.kind)) {
            settingsPanel.add(note(I18n.t("defdlg.help.move")));
        } else if ("VALUE".equals(c.kind)) {
            settingsPanel.add(note(I18n.t("defdlg.help.value")));
            valueField = new JTextField(String.valueOf(c.data.get("value")));
            valueField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            valueField.setAlignmentX(0f);
            valueField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { updatePreview(); }
                public void removeUpdate(DocumentEvent e) { updatePreview(); }
                public void changedUpdate(DocumentEvent e) { updatePreview(); }
            });
            settingsPanel.add(valueField);
        }
        settingsPanel.add(Box.createVerticalGlue());
        settingsPanel.revalidate();
        settingsPanel.repaint();
        updatePreview();
    }

    private JLabel note(String text) {
        JLabel label = new JLabel("<html><div style='width:250px'>" + text + "</div></html>");
        label.setForeground(BarMapStudio.TEXT_MUTED);
        label.setFont(BarMapStudio.font(Font.PLAIN, 11));
        label.setBorder(new EmptyBorder(0, 0, 8, 0));
        label.setAlignmentX(0f);
        return label;
    }

    private void addNumberSetting(String key, String label, double value, double min, double max, double step) {
        JCheckBox use = new JCheckBox(label);
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(Math.max(min, Math.min(max, value)), min, max, step));
        settings.add(new Setting(key, use, spinner));
        addRow(use, spinner);
    }

    private void addRow(JCheckBox use, JSpinner spinner) {
        use.setOpaque(false);
        use.setForeground(BarMapStudio.TEXT_MAIN);
        use.setFont(BarMapStudio.font(Font.PLAIN, 11));
        use.addActionListener(e -> updatePreview());
        // toccare il valore significa volerlo cambiare
        spinner.addChangeListener(e -> {
            use.setSelected(true);
            updatePreview();
        });
        spinner.setPreferredSize(new Dimension(110, 24));
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setAlignmentX(0f);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        row.add(use, BorderLayout.CENTER);
        row.add(spinner, BorderLayout.EAST);
        settingsPanel.add(row);
        settingsPanel.add(Box.createVerticalStrut(4));
    }

    private static String fmt(Object spinnerValue) {
        double v = ((Number) spinnerValue).doubleValue();
        return v == Math.rint(v) ? String.valueOf((long) v) : String.format(Locale.ROOT, "%.3f", v).replaceAll("0+$", "");
    }

    /** Scrive nell'anteprima il codice che verra' aggiunto alla definizione. */
    private void updatePreview() {
        BarUnits.Unit unit = unitList.getSelectedValue();
        Component3 c = componentList.getSelectedValue();
        if (unit == null || c == null || "INFO".equals(c.kind)) {
            preview.setText("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("-- ").append(unit.name()).append(" (").append(unit.id()).append("): ").append(c.label.replaceAll("\\s+", " ")).append("\n");
        if ("WEAPON".equals(c.kind)) {
            List<String> overrides = new ArrayList<>();
            for (Setting s : settings) {
                if (s.use.isSelected()) overrides.add(s.key + " = " + fmt(s.value.getValue()));
            }
            if (damageUse != null && damageUse.isSelected()) {
                overrides.add("damage = { default = " + fmt(damageValue.getValue()) + " }");
            }
            sb.append("Studio.addWeapon(unitDef, \"").append(unit.id()).append("\", \"").append(c.name).append("\"");
            if (!overrides.isEmpty()) {
                sb.append(", { ").append(String.join(", ", overrides)).append(" }");
            }
            sb.append(")\n");
        } else if ("SHIELD".equals(c.kind)) {
            String radius = "Studio.modelRadius * 2";
            String power = "nil";
            for (Setting s : settings) {
                if (s.key.equals("radius") && !(autoRadius != null && autoRadius.isSelected())) radius = fmt(s.value.getValue());
                if (s.key.equals("power")) power = fmt(s.value.getValue());
            }
            sb.append("Studio.addShield(unitDef, ").append(radius).append(", ").append(power).append(", \"").append(unit.id()).append("\", \"").append(c.name).append("\")\n");
        } else if ("MOVE".equals(c.kind)) {
            sb.append("Studio.moveLike(unitDef, \"").append(unit.id()).append("\")\n");
        } else if ("VALUE".equals(c.kind)) {
            String value = valueField != null ? valueField.getText().trim() : String.valueOf(c.data.get("value"));
            sb.append("Studio.set(unitDef, \"").append(c.name).append("\", ").append(value.isEmpty() ? "nil" : value).append(")\n");
        }
        preview.setText(sb.toString());
        preview.setCaretPosition(0);
    }

    /**
     * Lettore minimo delle tabelle Lua dei file unita' di BAR: abbastanza per elencare armi e valori.
     * I valori semplici restano testo com'e' scritto nel file; le tabelle diventano mappe.
     */
    static final class LuaTableReader {
        private LuaTableReader() {}

        /** Definizione dell'unita' (tabella interna di "return { nome = { ... } }"), o null. */
        static Map<String, Object> unitDef(String source, String unitId) {
            if (source == null) return null;
            String text = stripComments(source);
            int ret = text.indexOf("return");
            int open = text.indexOf('{', Math.max(0, ret));
            if (open < 0) return null;
            Map<String, Object> root = parse(text, new int[]{open + 1});
            Object def = field(root, unitId);
            if (def instanceof Map<?, ?> m) return cast(m);
            for (Object value : root.values()) {
                if (value instanceof Map<?, ?> m) return cast(m);
            }
            return null;
        }

        static Object field(Map<String, Object> map, String key) {
            if (map == null) return null;
            for (Map.Entry<String, Object> e : map.entrySet()) {
                if (e.getKey().equalsIgnoreCase(key)) return e.getValue();
            }
            return null;
        }

        private static String stripComments(String s) {
            StringBuilder out = new StringBuilder(s.length());
            int i = 0;
            while (i < s.length()) {
                char c = s.charAt(i);
                if (c == '"' || c == '\'') {
                    int end = i + 1;
                    while (end < s.length() && s.charAt(end) != c) {
                        if (s.charAt(end) == '\\') end++;
                        end++;
                    }
                    out.append(s, i, Math.min(s.length(), end + 1));
                    i = end + 1;
                } else if (c == '-' && i + 1 < s.length() && s.charAt(i + 1) == '-') {
                    if (s.startsWith("--[[", i)) {
                        int end = s.indexOf("]]", i);
                        i = end < 0 ? s.length() : end + 2;
                    } else {
                        int end = s.indexOf('\n', i);
                        i = end < 0 ? s.length() : end;
                    }
                } else {
                    out.append(c);
                    i++;
                }
            }
            return out.toString();
        }

        /** Legge "chiave = valore, ..." fino alla graffa di chiusura; pos[0] avanza oltre la graffa. */
        private static Map<String, Object> parse(String s, int[] pos) {
            Map<String, Object> map = new LinkedHashMap<>();
            int auto = 1;
            while (pos[0] < s.length()) {
                skipSpace(s, pos);
                if (pos[0] >= s.length()) break;
                char c = s.charAt(pos[0]);
                if (c == '}') {
                    pos[0]++;
                    break;
                }
                if (c == ',' || c == ';') {
                    pos[0]++;
                    continue;
                }
                // chiave: nome, ["testo"] oppure [numero]; senza "=" e' un elemento di lista
                int start = pos[0];
                String key = null;
                if (c == '[') {
                    int close = s.indexOf(']', pos[0]);
                    if (close > 0) {
                        String inside = s.substring(pos[0] + 1, close).trim();
                        int after = close + 1;
                        int[] probe = {after};
                        skipSpace(s, probe);
                        if (probe[0] < s.length() && s.charAt(probe[0]) == '=') {
                            key = inside.replace("\"", "").replace("'", "");
                            pos[0] = probe[0] + 1;
                        }
                    }
                } else if (Character.isLetter(c) || c == '_') {
                    int end = pos[0];
                    while (end < s.length() && (Character.isLetterOrDigit(s.charAt(end)) || s.charAt(end) == '_')) end++;
                    int[] probe = {end};
                    skipSpace(s, probe);
                    if (probe[0] < s.length() && s.charAt(probe[0]) == '=' && !(probe[0] + 1 < s.length() && s.charAt(probe[0] + 1) == '=')) {
                        key = s.substring(pos[0], end);
                        pos[0] = probe[0] + 1;
                    }
                }
                if (key == null) {
                    key = String.valueOf(auto++);
                    pos[0] = start;
                }
                skipSpace(s, pos);
                if (pos[0] < s.length() && s.charAt(pos[0]) == '{') {
                    pos[0]++;
                    map.put(key, parse(s, pos));
                } else {
                    map.put(key, scalar(s, pos));
                }
            }
            return map;
        }

        /** Valore semplice: tutto fino alla virgola o alla graffa di chiusura dello stesso livello. */
        private static String scalar(String s, int[] pos) {
            int start = pos[0];
            int depth = 0;
            while (pos[0] < s.length()) {
                char c = s.charAt(pos[0]);
                if (c == '"' || c == '\'') {
                    int end = pos[0] + 1;
                    while (end < s.length() && s.charAt(end) != c) {
                        if (s.charAt(end) == '\\') end++;
                        end++;
                    }
                    pos[0] = Math.min(s.length(), end + 1);
                    continue;
                }
                if (c == '(' || c == '{') depth++;
                if (c == ')') depth--;
                if (c == '}') {
                    if (depth == 0) break;
                    depth--;
                }
                if ((c == ',' || c == ';') && depth == 0) break;
                pos[0]++;
            }
            return s.substring(start, pos[0]).trim();
        }

        private static void skipSpace(String s, int[] pos) {
            while (pos[0] < s.length() && Character.isWhitespace(s.charAt(pos[0]))) pos[0]++;
        }
    }
}

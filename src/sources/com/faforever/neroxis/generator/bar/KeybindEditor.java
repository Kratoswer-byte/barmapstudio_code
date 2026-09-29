package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/KeybindEditor.class */
final class KeybindEditor {
    private static final String[] LAYERS = {"Keys", "Shift", "Ctrl", "Alt", "Space (Meta)"};
    private static final String[][] LAYER_MODIFIERS = {new String[0], new String[]{"shift"}, new String[]{"ctrl"}, new String[]{"alt"}, new String[]{"meta"}};
    private final JFrame owner;
    private final Path storeFile;
    private JDialog dialog;
    private int layer;
    private Key selectedKey;
    private boolean fillingProfiles;
    private final List<Map<String, Object>> shipped = new ArrayList();
    private Map<String, Object> store = new LinkedHashMap();
    private final List<Action> actions = new ArrayList();
    private final Map<String, Action> actionsById = new LinkedHashMap();
    private final List<Object[]> prefixes = new ArrayList();
    private final Set<String> hidden = new TreeSet();
    private final List<Map<?, ?>> languages = new ArrayList();
    private final List<Key> keys = buildKeyboard();
    private final JComboBox<String> profileChoice = new JComboBox<>();
    private final JLabel activeLabel = new JLabel(" ");
    private final KeyboardPanel keyboard = new KeyboardPanel(this);
    private final JLabel keyTitle = new JLabel("Click a key");
    private final JPanel bindRows = new JPanel();
    private final JTextField search = new JTextField();
    private final DefaultListModel<Action> actionModel = new DefaultListModel<>();
    private final JList<Action> actionList = new JList<>(this.actionModel);
    private final JButton bindButton = new JButton("Bind to the key");
    private final JLabel status = new JLabel(" ");

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/KeybindEditor$Key.class */
    private static final class Key extends Record {
        private final String legend;
        private final double x;
        private final double y;
        private final double width;
        private final String[] tokens;

        private Key(String legend, double x, double y, double width, String... tokens) {
            this.legend = legend;
            this.x = x;
            this.y = y;
            this.width = width;
            this.tokens = tokens;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Key.class), Key.class, "legend;x;y;width;tokens", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->legend:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->x:D", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->y:D", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->width:D", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->tokens:[Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Key.class), Key.class, "legend;x;y;width;tokens", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->legend:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->x:D", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->y:D", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->width:D", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->tokens:[Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Key.class, Object.class), Key.class, "legend;x;y;width;tokens", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->legend:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->x:D", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->y:D", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->width:D", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Key;->tokens:[Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String legend() {
            return this.legend;
        }

        public double x() {
            return this.x;
        }

        public double y() {
            return this.y;
        }

        public double width() {
            return this.width;
        }

        public String[] tokens() {
            return this.tokens;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/KeybindEditor$Keyset.class */
    private static final class Keyset extends Record {
        private final Set<String> modifiers;
        private final boolean any;
        private final String key;
        private final boolean chord;

        private Keyset(Set<String> modifiers, boolean any, String key, boolean chord) {
            this.modifiers = modifiers;
            this.any = any;
            this.key = key;
            this.chord = chord;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Keyset.class), Keyset.class, "modifiers;any;key;chord", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Keyset;->modifiers:Ljava/util/Set;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Keyset;->any:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Keyset;->key:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Keyset;->chord:Z").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Keyset.class), Keyset.class, "modifiers;any;key;chord", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Keyset;->modifiers:Ljava/util/Set;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Keyset;->any:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Keyset;->key:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Keyset;->chord:Z").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Keyset.class, Object.class), Keyset.class, "modifiers;any;key;chord", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Keyset;->modifiers:Ljava/util/Set;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Keyset;->any:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Keyset;->key:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Keyset;->chord:Z").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Set<String> modifiers() {
            return this.modifiers;
        }

        public boolean any() {
            return this.any;
        }

        public String key() {
            return this.key;
        }

        public boolean chord() {
            return this.chord;
        }

        static Keyset parse(String text) {
            String[] presses = text.split(",");
            String[] parts = presses[0].trim().split("\\+");
            Set<String> modifiers = new TreeSet<>();
            boolean any = false;
            for (int i = 0; i < parts.length - 1; i++) {
                String modifier = parts[i].trim().toLowerCase(Locale.ROOT);
                if (modifier.equals("any")) {
                    any = true;
                } else if (!modifier.isEmpty()) {
                    modifiers.add(modifier);
                }
            }
            return new Keyset(modifiers, any, parts[parts.length - 1].trim().toLowerCase(Locale.ROOT), presses.length > 1);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/KeybindEditor$Action.class */
    private static final class Action extends Record {
        private final String id;
        private final String label;
        private final String category;
        private final String alwaysModifier;

        private Action(String id, String label, String category, String alwaysModifier) {
            this.id = id;
            this.label = label;
            this.category = category;
            this.alwaysModifier = alwaysModifier;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Action.class), Action.class, "id;label;category;alwaysModifier", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Action;->id:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Action;->label:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Action;->category:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Action;->alwaysModifier:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Action.class, Object.class), Action.class, "id;label;category;alwaysModifier", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Action;->id:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Action;->label:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Action;->category:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/KeybindEditor$Action;->alwaysModifier:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String id() {
            return this.id;
        }

        public String label() {
            return this.label;
        }

        public String category() {
            return this.category;
        }

        public String alwaysModifier() {
            return this.alwaysModifier;
        }

        @Override // java.lang.Record
        public String toString() {
            return this.label;
        }
    }

    KeybindEditor(JFrame owner, Path barData, String catalog, String defaults, String... texts) {
        this.owner = owner;
        String testStore = System.getProperty("neroxis.launcher.keystore");
        this.storeFile = testStore != null ? Path.of(testStore, new String[0]) : barData.resolve("LuaUI").resolve("Config").resolve("keybind_profiles.json");
        for (String text : texts) {
            if (text != null) {
                try {
                    Object obj = MiniJson.parse(text);
                    if (obj instanceof Map) {
                        Map<?, ?> file = (Map) obj;
                        this.languages.add(file);
                    }
                } catch (IllegalArgumentException e) {
                }
            }
        }
        Object obj2 = MiniJson.parse(defaults);
        if (obj2 instanceof Map) {
            Map<?, ?> file2 = (Map) obj2;
            Object obj3 = file2.get("profiles");
            if (obj3 instanceof List) {
                List<?> profiles = (List) obj3;
                for (Object profile : profiles) {
                    if (profile instanceof Map) {
                        Map<?, ?> fields = (Map) profile;
                        this.shipped.add(copy(fields));
                    }
                }
            }
        }
        readCatalog(catalog);
        readStore();
    }

    static boolean available(String catalog, String defaults) {
        return catalog != null && defaults != null && catalog.contains("\"category\"") && defaults.contains("\"profiles\"");
    }

    private static Map<String, Object> copy(Map<?, ?> source) {
        return (Map) MiniJson.parse(MiniJson.write(source));
    }

    private String translate(String key) {
        Object obj;
        for (Map<?, ?> language : this.languages) {
            Object node = language;
            for (String part : key.split("\\.")) {
                if (node instanceof Map) {
                    Map<?, ?> map = (Map) node;
                    obj = map.get(part);
                } else {
                    obj = null;
                }
                node = obj;
            }
            if (node instanceof String) {
                String text = (String) node;
                return text;
            }
        }
        return null;
    }

    private void readCatalog(String catalog) {
        String str;
        String str2;
        Object obj = MiniJson.parse(catalog);
        if (!(obj instanceof List)) {
            return;
        }
        List<?> entries = (List) obj;
        for (Object entry : entries) {
            if (entry instanceof Map) {
                Map<?, ?> fields = (Map) entry;
                Object obj2 = fields.get("hidden");
                if (obj2 instanceof List) {
                    List<?> ids = (List) obj2;
                    ids.forEach(id -> {
                        this.hidden.add(String.valueOf(id));
                    });
                }
                Object obj3 = fields.get("items");
                if (obj3 instanceof List) {
                    List<?> items = (List) obj3;
                    String categoryKey = String.valueOf(fields.get("category"));
                    String category = translate(categoryKey) != null ? translate(categoryKey) : categoryKey;
                    for (Object item : items) {
                        if (item instanceof Map) {
                            Map<?, ?> row = (Map) item;
                            Object obj4 = row.get("alwaysModifier");
                            if (obj4 instanceof String) {
                                String modifier = (String) obj4;
                                str = modifier;
                            } else {
                                str = "";
                            }
                            String always = str;
                            Object obj5 = row.get("label");
                            if (obj5 instanceof String) {
                                String key = (String) obj5;
                                str2 = key;
                            } else {
                                str2 = null;
                            }
                            String labelKey = str2;
                            Object obj6 = row.get("action");
                            if (obj6 instanceof String) {
                                String id2 = (String) obj6;
                                String label = (labelKey == null || translate(labelKey) == null) ? id2 : translate(labelKey);
                                addAction(new Action(id2, label, category, always));
                            } else {
                                Object obj7 = row.get("prefix");
                                if (obj7 instanceof String) {
                                    String prefix = (String) obj7;
                                    this.prefixes.add(new Object[]{prefix, labelKey, category, always});
                                    Object obj8 = row.get("members");
                                    if (obj8 instanceof List) {
                                        List<?> members = (List) obj8;
                                        for (Object member : members) {
                                            addAction(new Action(prefix + String.valueOf(member), prefixLabel(prefix, labelKey, String.valueOf(member)), category, always));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void addAction(Action action) {
        if (this.actionsById.putIfAbsent(action.id(), action) == null) {
            this.actions.add(action);
        }
    }

    private String prefixLabel(String prefix, String labelKey, String argument) {
        String template = labelKey != null ? translate(labelKey) : null;
        if (template == null) {
            return prefix + argument;
        }
        String[] tokens = argument.trim().split("\\s+");
        return template.replace("%{n}", argument).replace("%{row}", tokens[0]).replace("%{col}", tokens.length > 1 ? tokens[1] : "");
    }

    private String label(String actionId) {
        Action action = this.actionsById.get(actionId);
        if (action != null) {
            return action.label();
        }
        for (Object[] prefix : this.prefixes) {
            if (actionId.startsWith((String) prefix[0])) {
                return prefixLabel((String) prefix[0], (String) prefix[1], actionId.substring(((String) prefix[0]).length()));
            }
        }
        return actionId;
    }

    private void readStore() {
        this.store = new LinkedHashMap();
        try {
            if (Files.isRegularFile(this.storeFile, new LinkOption[0])) {
                Object obj = MiniJson.parse(Files.readString(this.storeFile));
                if (obj instanceof Map) {
                    Map<?, ?> file = (Map) obj;
                    this.store = copy(file);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
        }
        this.store.putIfAbsent("version", Double.valueOf(2.0d));
        if (!(this.store.get("profiles") instanceof List)) {
            this.store.put("profiles", new ArrayList());
        }
    }

    private List<Map<String, Object>> ownProfiles() {
        return (List) this.store.get("profiles");
    }

    private String activeName() {
        String str;
        Object obj = this.store.get("active");
        if (obj instanceof String) {
            String name = (String) obj;
            str = name;
        } else {
            str = null;
        }
        String active = str;
        if (active == null || find(active) == null) {
            return this.shipped.isEmpty() ? "" : String.valueOf(((Map) this.shipped.getFirst()).get("name"));
        }
        return active;
    }

    private Map<String, Object> find(String name) {
        for (Map<String, Object> profile : ownProfiles()) {
            if (name.equals(profile.get("name"))) {
                return profile;
            }
        }
        for (Map<String, Object> profile2 : this.shipped) {
            if (name.equals(profile2.get("name"))) {
                return profile2;
            }
        }
        return null;
    }

    private boolean isShipped(Map<String, Object> profile) {
        return this.shipped.contains(profile);
    }

    private Map<String, Object> shown() {
        Map<String, Object> mapFind;
        if (this.profileChoice.getSelectedItem() != null) {
            mapFind = find(plainName(String.valueOf(this.profileChoice.getSelectedItem())));
        } else {
            mapFind = null;
        }
        Map<String, Object> profile = mapFind;
        return profile != null ? profile : find(activeName());
    }

    private static String plainName(String shownName) {
        return shownName.replace("   (built in)", "");
    }

    private static List<Map<String, Object>> binds(Map<String, Object> profile) {
        if (!(profile.get("binds") instanceof List)) {
            profile.put("binds", new ArrayList());
        }
        return (List) profile.get("binds");
    }

    private Map<String, Object> editable() {
        Map<String, Object> profile = shown();
        if (!isShipped(profile)) {
            return profile;
        }
        String base = String.valueOf(profile.get("name"));
        String name = base + " (Eclipse)";
        int number = 2;
        while (find(name) != null) {
            name = base + " (Eclipse " + number + ")";
            number++;
        }
        Map<String, Object> fork = copy(profile);
        fork.remove("description");
        fork.put("name", name);
        fork.put("basedOn", base);
        ownProfiles().add(fork);
        this.store.put("active", name);
        fillProfiles(name);
        this.status.setText("\"" + base + "\" is built in: your changes go into the copy \"" + name + "\", which is now the profile of the game.");
        return fork;
    }

    private boolean save() {
        try {
            Files.createDirectories(this.storeFile.getParent(), new FileAttribute[0]);
            Path temporary = this.storeFile.resolveSibling(String.valueOf(this.storeFile.getFileName()) + ".new");
            Files.writeString(temporary, MiniJson.write(this.store), new OpenOption[0]);
            Files.move(temporary, this.storeFile, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this.dialog, "The keybinds could not be saved: " + e.getMessage());
            return false;
        }
    }

    private static List<Key> buildKeyboard() {
        String[] strArr;
        List<Key> keys = new ArrayList<>();
        keys.add(new Key("Esc", 0.0d, 0.0d, 1.0d, "esc", "escape"));
        int i = 1;
        while (i <= 12) {
            keys.add(new Key("F" + i, 1.0d + ((double) i) + (i > 4 ? 0.5d : 0.0d) + (i > 8 ? 0.5d : 0.0d), 0.0d, 1.0d, "f" + i));
            i++;
        }
        keys.add(new Key("Pause", 15.5d, 0.0d, 1.5d, "pause"));
        String[][] rows = {new String[]{"`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "="}, new String[]{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]", "\\"}, new String[]{"A", "S", "D", "F", "G", "H", "J", "K", "L", ";", "'"}, new String[]{"Z", "X", "C", "V", "B", "N", "M", ",", ".", "/"}};
        double[] offsets = {0.0d, 1.5d, 1.75d, 2.25d};
        for (int row = 0; row < rows.length; row++) {
            double x = offsets[row];
            for (String legend : rows[row]) {
                String lower = legend.toLowerCase(Locale.ROOT);
                switch (lower) {
                    case ",":
                        strArr = new String[]{"sc_comma", "sc_,", ",", "comma"};
                        break;
                    case ".":
                        strArr = new String[]{"sc_.", ".", "sc_period", "period"};
                        break;
                    case "`":
                        strArr = new String[]{"sc_`", "`", "sc_backquote", "backquote"};
                        break;
                    case "\":
                        strArr = new String[]{"sc_\\", "\\", "sc_backslash", "backslash"};
                        break;
                    default:
                        if (Character.isDigit(lower.charAt(0))) {
                            strArr = new String[]{lower, "sc_" + lower};
                            break;
                        } else {
                            strArr = new String[]{"sc_" + lower, lower};
                            break;
                        }
                        break;
                }
                String[] tokens = strArr;
                keys.add(new Key(legend, x, ((double) row) + 1.25d, 1.0d, tokens));
                x += 1.0d;
            }
        }
        keys.add(new Key("Backspace", 13.0d, 1.25d, 2.0d, "backspace"));
        keys.add(new Key("Tab", 0.0d, 2.25d, 1.5d, "tab"));
        keys.add(new Key("Caps", 0.0d, 3.25d, 1.75d, "capslock"));
        keys.add(new Key("Enter", 12.75d, 3.25d, 2.25d, "enter", "return"));
        keys.add(new Key("Shift", 0.0d, 4.25d, 2.25d, "shift"));
        keys.add(new Key("Shift", 12.25d, 4.25d, 2.75d, "shift"));
        keys.add(new Key("Ctrl", 0.0d, 5.25d, 1.5d, "ctrl"));
        keys.add(new Key("Alt", 2.75d, 5.25d, 1.5d, "alt"));
        keys.add(new Key("Space", 4.25d, 5.25d, 6.25d, "space", "sc_space"));
        keys.add(new Key("Alt", 10.5d, 5.25d, 1.5d, "alt"));
        keys.add(new Key("Ctrl", 13.5d, 5.25d, 1.5d, "ctrl"));
        String[][] navigation = {new String[]{"Ins", "insert"}, new String[]{"Home", "home"}, new String[]{"PgUp", "pageup"}, new String[]{"Del", "delete"}, new String[]{"End", "end"}, new String[]{"PgDn", "pagedown"}};
        for (int i2 = 0; i2 < navigation.length; i2++) {
            keys.add(new Key(navigation[i2][0], 15.5d + ((double) (i2 % 3)), 1.25d + ((double) (i2 / 3)), 1.0d, navigation[i2][1]));
        }
        keys.add(new Key("↑", 16.5d, 4.25d, 1.0d, "up"));
        keys.add(new Key("←", 15.5d, 5.25d, 1.0d, "left"));
        keys.add(new Key("↓", 16.5d, 5.25d, 1.0d, "down"));
        keys.add(new Key("→", 17.5d, 5.25d, 1.0d, "right"));
        String[][] pad = {new String[]{"7", "8", "9"}, new String[]{"4", "5", "6"}, new String[]{"1", "2", "3"}};
        for (int row2 = 0; row2 < 3; row2++) {
            for (int column = 0; column < 3; column++) {
                String digit = pad[row2][column];
                keys.add(new Key(digit, 19 + column, 2.25d + ((double) row2), 1.0d, "numpad" + digit, "sc_numpad" + digit));
            }
        }
        keys.add(new Key("0", 19.0d, 5.25d, 2.0d, "numpad0", "sc_numpad0"));
        keys.add(new Key(".", 21.0d, 5.25d, 1.0d, "numpad.", "sc_numpad."));
        keys.add(new Key("/", 20.0d, 1.25d, 1.0d, "numpad/", "sc_numpad/"));
        keys.add(new Key("*", 21.0d, 1.25d, 1.0d, "numpad*", "sc_numpad*"));
        keys.add(new Key("-", 22.0d, 1.25d, 1.0d, "numpad-", "sc_numpad-"));
        keys.add(new Key("+", 22.0d, 2.25d, 1.0d, "numpad+", "sc_numpad+"));
        return keys;
    }

    private static boolean matches(Key key, Keyset keyset) {
        for (String token : key.tokens()) {
            if (token.equals(keyset.key())) {
                return true;
            }
        }
        return false;
    }

    private List<Map<String, Object>> bindsOn(Key key, int wantedLayer, boolean otherLayersToo) {
        List<Map<String, Object>> result = new ArrayList<>();
        Set<String> wanted = new TreeSet<>(List.of((Object[]) LAYER_MODIFIERS[wantedLayer]));
        for (Map<String, Object> bind : binds(shown())) {
            Keyset keyset = Keyset.parse(String.valueOf(bind.get("keyset")));
            if (otherLayersToo || !this.hidden.contains(String.valueOf(bind.get("action")))) {
                if (matches(key, keyset) && (otherLayersToo || keyset.modifiers().equals(wanted) || (keyset.any() && wantedLayer == 0))) {
                    result.add(bind);
                }
            }
        }
        return result;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/KeybindEditor$KeyboardPanel.class */
    private final class KeyboardPanel extends JPanel {
        private static final double COLUMNS = 23.0d;
        private static final double ROWS = 6.25d;
        final /* synthetic */ KeybindEditor this$0;

        KeyboardPanel(final KeybindEditor this$0) {
            Objects.requireNonNull(this$0);
            this.this$0 = this$0;
            setOpaque(false);
            setPreferredSize(new Dimension(1060, 330));
            setToolTipText("");
            addMouseListener(new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.KeybindEditor.KeyboardPanel.1
                final /* synthetic */ KeyboardPanel this$1;

                {
                    Objects.requireNonNull(this);
                    this.this$1 = this;
                }

                public void mousePressed(MouseEvent event) {
                    for (Key key : this.this$1.this$0.keys) {
                        if (this.this$1.bounds(key).contains(event.getPoint())) {
                            this.this$1.this$0.selectedKey = key;
                            this.this$1.this$0.showKey();
                            this.this$1.repaint();
                            return;
                        }
                    }
                }
            });
        }

        public String getToolTipText(MouseEvent event) {
            for (Key key : this.this$0.keys) {
                if (bounds(key).contains(event.getPoint())) {
                    List<Map<String, Object>> here = this.this$0.bindsOn(key, this.this$0.layer, false);
                    if (here.isEmpty()) {
                        return null;
                    }
                    StringBuilder text = new StringBuilder("<html>");
                    for (Map<String, Object> bind : here) {
                        text.append("<b>").append(this.this$0.label(String.valueOf(bind.get("action")))).append("</b>  ").append(String.valueOf(bind.get("keyset")).replace("sc_", "")).append("<br>");
                    }
                    return text.append("</html>").toString();
                }
            }
            return null;
        }

        public Dimension getPreferredSize() {
            int width = (getParent() == null || getParent().getWidth() <= 0) ? 1060 : getParent().getWidth() - 24;
            return new Dimension(width, (int) Math.ceil((((double) width) / COLUMNS) * ROWS));
        }

        private Rectangle2D.Double bounds(Key key) {
            double unit = Math.min(((double) getWidth()) / COLUMNS, ((double) getHeight()) / ROWS);
            double left = (((double) getWidth()) - (unit * COLUMNS)) / 2.0d;
            return new Rectangle2D.Double(left + (key.x() * unit) + 2.0d, (key.y() * unit) + 2.0d, (key.width() * unit) - 4.0d, unit - 4.0d);
        }

        protected void paintComponent(Graphics graphics) {
            Color color;
            Color color2;
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Action chosen = (Action) this.this$0.actionList.getSelectedValue();
            Iterator<Key> it = this.this$0.keys.iterator();
            while (it.hasNext()) {
                Key key = it.next();
                Rectangle2D.Double box = bounds(key);
                List<Map<String, Object>> here = this.this$0.bindsOn(key, this.this$0.layer, false);
                boolean marks = chosen != null && this.this$0.bindsOn(key, this.this$0.layer, false).stream().anyMatch(bind -> {
                    return chosen.id().equals(bind.get("action"));
                });
                boolean modifier = List.of("shift", "ctrl", "alt").contains(key.tokens()[0]);
                boolean held = (modifier && List.of((Object[]) KeybindEditor.LAYER_MODIFIERS[this.this$0.layer]).contains(key.tokens()[0])) || (key.tokens()[0].equals("space") && this.this$0.layer == 4);
                if (held) {
                    color = new Color(236, 238, 242);
                } else if (key == this.this$0.selectedKey) {
                    color = new Color(70, 58, 40);
                } else {
                    color = here.isEmpty() ? new Color(30, 33, 41) : new Color(44, 48, 60);
                }
                g.setColor(color);
                g.fill(new RoundRectangle2D.Double(box.x, box.y, box.width, box.height, 8.0d, 8.0d));
                if (marks) {
                    color2 = BarLauncher.GREEN;
                } else {
                    color2 = key == this.this$0.selectedKey ? BarLauncher.ACCENT : new Color(255, 255, 255, 30);
                }
                g.setColor(color2);
                g.setStroke(new BasicStroke((marks || key == this.this$0.selectedKey) ? 2.0f : 1.0f));
                g.draw(new RoundRectangle2D.Double(box.x, box.y, box.width, box.height, 8.0d, 8.0d));
                g.setFont(getFont().deriveFont(10.5f));
                g.setColor(held ? new Color(40, 44, 54) : new Color(170, 176, 190));
                g.drawString(key.legend(), ((float) box.x) + 5.0f, ((float) box.y) + 13.0f);
                if (!here.isEmpty()) {
                    String text = this.this$0.label(String.valueOf(((Map) here.getFirst()).get("action"))).toUpperCase(Locale.ROOT);
                    float size = 10.0f;
                    g.setFont(getFont().deriveFont(1, 10.0f));
                    while (size > 7.0f && widestWord(g, text) > box.width - 8.0d) {
                        size -= 0.5f;
                        g.setFont(getFont().deriveFont(1, size));
                    }
                    g.setColor(held ? new Color(40, 44, 54) : Color.WHITE);
                    drawWrapped(g, text, box, here.size() > 1 ? "+" + (here.size() - 1) : "");
                }
            }
            g.dispose();
        }

        private int widestWord(Graphics2D g, String text) {
            int widest = 0;
            for (String word : text.split(" ")) {
                widest = Math.max(widest, g.getFontMetrics().stringWidth(word));
            }
            return widest;
        }

        private void drawWrapped(Graphics2D g, String text, Rectangle2D.Double box, String more) {
            String shown;
            FontMetrics metrics = g.getFontMetrics();
            List<String> lines = new ArrayList<>();
            StringBuilder line = new StringBuilder();
            for (String word : text.split(" ")) {
                if (line.length() > 0 && metrics.stringWidth(String.valueOf(line) + " " + word) > box.width - 8.0d) {
                    lines.add(line.toString());
                    line = new StringBuilder();
                }
                line.append(line.length() > 0 ? " " : "").append(word);
            }
            lines.add(line.toString());
            int maxLines = Math.max(1, (int) ((box.height - 20.0d) / ((double) (metrics.getHeight() - 1))));
            for (int i = 0; i < Math.min(lines.size(), maxLines); i++) {
                String strSubstring = lines.get(i);
                while (true) {
                    shown = strSubstring;
                    if (shown.length() <= 2 || metrics.stringWidth(shown) <= box.width - 6.0d) {
                        break;
                    } else {
                        strSubstring = shown.substring(0, shown.length() - 1);
                    }
                }
                g.drawString(shown, ((float) box.x) + 5.0f, (float) (box.y + 29.0d + ((double) (i * (metrics.getHeight() - 1)))));
            }
            if (!more.isEmpty()) {
                g.setColor(BarLauncher.ACCENT);
                g.drawString(more, (float) (((box.x + box.width) - ((double) metrics.stringWidth(more))) - 4.0d), ((float) box.y) + 12.0f);
            }
        }
    }

    void show(boolean modal) {
        if (this.dialog == null) {
            build(modal);
        }
        readStore();
        fillProfiles(activeName());
        refresh();
        this.dialog.setLocationRelativeTo(this.owner);
        String testBind = System.getProperty("neroxis.launcher.testbind");
        if (testBind != null && testBind.contains(":")) {
            String[] parts = testBind.split(":", 2);
            this.selectedKey = this.keys.stream().filter(key -> {
                return key.legend().equalsIgnoreCase(parts[0]);
            }).findFirst().orElse(null);
            this.actionList.setSelectedValue(this.actionsById.get(parts[1]), true);
            showKey();
            this.bindButton.doClick();
        }
        this.dialog.setVisible(true);
    }

    private void fillProfiles(String select) {
        this.fillingProfiles = true;
        this.profileChoice.removeAllItems();
        for (Map<String, Object> profile : this.shipped) {
            this.profileChoice.addItem(String.valueOf(profile.get("name")) + "   (built in)");
        }
        for (Map<String, Object> profile2 : ownProfiles()) {
            this.profileChoice.addItem(String.valueOf(profile2.get("name")));
        }
        for (int i = 0; i < this.profileChoice.getItemCount(); i++) {
            if (plainName((String) this.profileChoice.getItemAt(i)).equals(select)) {
                this.profileChoice.setSelectedIndex(i);
            }
        }
        this.fillingProfiles = false;
    }

    private void refresh() {
        String active = activeName();
        Map<String, Object> profile = shown();
        boolean isActive = profile != null && active.equals(profile.get("name"));
        this.activeLabel.setText(isActive ? "✔ The game uses this profile" : "The game uses: " + active);
        this.activeLabel.setForeground(isActive ? BarLauncher.GREEN : BarLauncher.MUTED);
        showKey();
        this.keyboard.repaint();
    }

    private String keysetFor(Key key) {
        StringBuilder text = new StringBuilder();
        for (String modifier : LAYER_MODIFIERS[this.layer]) {
            text.append(Character.toUpperCase(modifier.charAt(0))).append(modifier.substring(1)).append('+');
        }
        return String.valueOf(text) + key.tokens()[0];
    }

    private void showKey() {
        this.bindRows.removeAll();
        if (this.selectedKey == null) {
            this.keyTitle.setText("Click a key");
            this.bindButton.setEnabled(false);
        } else {
            this.keyTitle.setText(keysetFor(this.selectedKey).replace("sc_", "").toUpperCase(Locale.ROOT));
            List<Map<String, Object>> all = bindsOn(this.selectedKey, this.layer, true);
            for (Map<String, Object> bind : all) {
                String keysetText = String.valueOf(bind.get("keyset"));
                Keyset keyset = Keyset.parse(keysetText);
                JLabel text = new JLabel("<html><b>" + label(String.valueOf(bind.get("action"))) + "</b><br><span style='color:#8c94a3'>" + keysetText.replace("sc_", "") + (keyset.chord() ? "  (pressed twice)" : "") + "</span></html>");
                text.setForeground(BarLauncher.TEXT);
                JButton remove = new JButton("Remove");
                remove.setFont(remove.getFont().deriveFont(11.0f));
                remove.addActionListener(e -> {
                    Map<String, Object> profile = editable();
                    String action = String.valueOf(bind.get("action"));
                    Action known = this.actionsById.get(action);
                    binds(profile).removeIf(other -> {
                        return action.equals(other.get("action")) && (keysetText.equalsIgnoreCase(String.valueOf(other.get("keyset"))) || (known != null && known.alwaysModifier().equals("shift") && sameKeyIgnoringShift(keysetText, String.valueOf(other.get("keyset")))));
                    });
                    if (save()) {
                        this.status.setText("Removed. The game uses it from its next start.");
                    }
                    refresh();
                });
                JPanel row = new JPanel(new BorderLayout(8, 0));
                row.setOpaque(false);
                row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
                row.setMaximumSize(new Dimension(4000, 44));
                row.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
                row.add(text, "Center");
                if (this.hidden.contains(String.valueOf(bind.get("action")))) {
                    text.setForeground(BarLauncher.MUTED);
                    text.setToolTipText("A key of the engine itself, it is not changed here");
                } else {
                    row.add(remove, "East");
                }
                this.bindRows.add(row);
            }
            if (all.isEmpty()) {
                JLabel none = new JLabel("Nothing is bound to this key.");
                none.setForeground(BarLauncher.MUTED);
                none.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
                this.bindRows.add(none);
            }
            this.bindButton.setEnabled((this.actionList.getSelectedValue() == null || List.of("shift", "ctrl", "alt").contains(this.selectedKey.tokens()[0])) ? false : true);
            this.bindButton.setText("Bind to " + keysetFor(this.selectedKey).replace("sc_", "").toUpperCase(Locale.ROOT));
        }
        this.bindRows.add(Box.createVerticalGlue());
        this.bindRows.revalidate();
        this.bindRows.repaint();
    }

    private static boolean sameKeyIgnoringShift(String a, String b) {
        Keyset first = Keyset.parse(a);
        Keyset second = Keyset.parse(b);
        Set<String> firstModifiers = new TreeSet<>(first.modifiers());
        Set<String> secondModifiers = new TreeSet<>(second.modifiers());
        firstModifiers.remove("shift");
        secondModifiers.remove("shift");
        return first.key().equals(second.key()) && firstModifiers.equals(secondModifiers);
    }

    private void filterActions() {
        Action selected = (Action) this.actionList.getSelectedValue();
        String filter = this.search.getText().trim().toLowerCase(Locale.ROOT);
        this.actionModel.clear();
        for (Action action : this.actions) {
            if (filter.isEmpty() || action.label().toLowerCase(Locale.ROOT).contains(filter) || action.category().toLowerCase(Locale.ROOT).contains(filter) || action.id().contains(filter)) {
                this.actionModel.addElement(action);
            }
        }
        if (selected != null) {
            this.actionList.setSelectedValue(selected, false);
        }
    }

    private String keysOf(Action action) {
        List<String> found = new ArrayList<>();
        for (Map<String, Object> bind : binds(shown())) {
            if (action.id().equals(bind.get("action")) && found.size() < 3) {
                found.add(String.valueOf(bind.get("keyset")).replace("sc_", ""));
            }
        }
        return String.join("   ", found);
    }

    private void build(boolean modal) {
        this.dialog = new JDialog(this.owner, "Shortcut keys", modal);
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(BarLauncher.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 14, 20));
        JLabel title = new JLabel("SHORTCUT KEYS");
        title.setForeground(BarLauncher.ACCENT);
        title.setFont(title.getFont().deriveFont(1, 18.0f));
        this.profileChoice.setPreferredSize(new Dimension(280, 32));
        this.profileChoice.addActionListener(e -> {
            if (!this.fillingProfiles) {
                refresh();
            }
        });
        JButton use = new JButton("Use in the game");
        use.setToolTipText("Makes the shown profile the one the game loads at its next start");
        use.addActionListener(e2 -> {
            Map<String, Object> profile = shown();
            if (profile != null) {
                this.store.put("active", profile.get("name"));
                if (save()) {
                    this.status.setText("The game uses \"" + String.valueOf(profile.get("name")) + "\" from its next start.");
                }
                refresh();
            }
        });
        JPanel top = new JPanel(new FlowLayout(0, 10, 0));
        top.setOpaque(false);
        top.add(title);
        top.add(Box.createHorizontalStrut(14));
        top.add(this.profileChoice);
        top.add(use);
        top.add(this.activeLabel);
        JPanel layers = new JPanel(new FlowLayout(0, 6, 0));
        layers.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        int i = 0;
        while (i < LAYERS.length) {
            int index = i;
            JToggleButton button = new JToggleButton(LAYERS[i], i == 0);
            button.setFocusPainted(false);
            button.addActionListener(e3 -> {
                this.layer = index;
                refresh();
            });
            group.add(button);
            layers.add(button);
            i++;
        }
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, 1));
        north.setOpaque(false);
        top.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        layers.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        north.add(top);
        north.add(Box.createVerticalStrut(10));
        north.add(layers);
        root.add(north, "North");
        JPanel keyboardCard = new BarLauncher.Surface(new BorderLayout(), BarLauncher.CARD, 14);
        keyboardCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        keyboardCard.add(this.keyboard, "Center");
        this.keyTitle.setForeground(BarLauncher.TEXT);
        this.keyTitle.setFont(this.keyTitle.getFont().deriveFont(0, 19.0f));
        this.bindRows.setLayout(new BoxLayout(this.bindRows, 1));
        this.bindRows.setOpaque(false);
        JScrollPane bindScroll = new JScrollPane(this.bindRows);
        bindScroll.setBorder((Border) null);
        bindScroll.setOpaque(false);
        bindScroll.getViewport().setOpaque(false);
        bindScroll.setPreferredSize(new Dimension(100, 150));
        JPanel keyCard = new BarLauncher.Surface(new BorderLayout(0, 8), BarLauncher.CARD, 14);
        keyCard.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 12));
        keyCard.add(this.keyTitle, "North");
        keyCard.add(bindScroll, "Center");
        this.search.putClientProperty("JTextField.placeholderText", "Search the actions of the game...");
        this.search.putClientProperty("JTextField.showClearButton", true);
        this.search.getDocument().addDocumentListener(new DocumentListener(this) { // from class: com.faforever.neroxis.generator.bar.KeybindEditor.1
            final /* synthetic */ KeybindEditor this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void insertUpdate(DocumentEvent event) {
                this.this$0.filterActions();
            }

            public void removeUpdate(DocumentEvent event) {
                this.this$0.filterActions();
            }

            public void changedUpdate(DocumentEvent event) {
                this.this$0.filterActions();
            }
        });
        this.actionList.setSelectionMode(0);
        this.actionList.setBackground(BarLauncher.CARD);
        this.actionList.setFixedCellHeight(38);
        this.actionList.setCellRenderer(new DefaultListCellRenderer(this) { // from class: com.faforever.neroxis.generator.bar.KeybindEditor.2
            final /* synthetic */ KeybindEditor this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public Component getListCellRendererComponent(JList<?> list, Object value, int index2, boolean selected, boolean focused) {
                JLabel label = super.getListCellRendererComponent(list, value, index2, selected, false);
                Action action = (Action) value;
                String where = this.this$0.keysOf(action);
                label.setText("<html><b>" + action.label() + "</b> <span style='color:#8c94a3'>" + action.category() + "</span><br><span style='color:#F0962E'>" + (where.isEmpty() ? "not bound" : where) + "</span></html>");
                label.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 6));
                return label;
            }
        });
        this.actionList.addListSelectionListener(e4 -> {
            showKey();
            this.keyboard.repaint();
        });
        JScrollPane actionScroll = new JScrollPane(this.actionList);
        actionScroll.setBorder(BorderFactory.createLineBorder(BarLauncher.CARD_BORDER));
        this.bindButton.setBackground(BarLauncher.ACCENT);
        this.bindButton.setForeground(new Color(25, 18, 8));
        this.bindButton.setFont(this.bindButton.getFont().deriveFont(1, 13.0f));
        this.bindButton.setEnabled(false);
        this.bindButton.addActionListener(e5 -> {
            bindSelected();
        });
        JPanel actionCard = new JPanel(new BorderLayout(0, 8));
        actionCard.setOpaque(false);
        actionCard.add(this.search, "North");
        actionCard.add(actionScroll, "Center");
        actionCard.add(this.bindButton, "South");
        JPanel lower = new JPanel(new BorderLayout(12, 0));
        lower.setOpaque(false);
        keyCard.setPreferredSize(new Dimension(430, 100));
        lower.add(keyCard, "West");
        lower.add(actionCard, "Center");
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(keyboardCard, "North");
        center.add(lower, "Center");
        root.add(center, "Center");
        this.status.setForeground(BarLauncher.MUTED);
        JButton close = new JButton("Close");
        close.setPreferredSize(new Dimension(120, 36));
        close.addActionListener(e6 -> {
            this.dialog.setVisible(false);
        });
        JPanel south = new JPanel(new BorderLayout(12, 0));
        south.setOpaque(false);
        JLabel note = new JLabel("Changes are used by the game from its next start. The keys of a layer are the ones with that modifier held.");
        note.setForeground(BarLauncher.MUTED);
        JPanel texts = new JPanel();
        texts.setLayout(new BoxLayout(texts, 1));
        texts.setOpaque(false);
        texts.add(this.status);
        texts.add(note);
        south.add(texts, "Center");
        south.add(close, "East");
        root.add(south, "South");
        filterActions();
        this.dialog.setContentPane(root);
        Rectangle screen = (this.owner == null || this.owner.getGraphicsConfiguration() == null) ? new Rectangle(1600, 900) : this.owner.getGraphicsConfiguration().getBounds();
        this.dialog.setSize(Math.min(1560, screen.width - 60), Math.min(980, screen.height - 60));
    }

    private void bindSelected() {
        Action action = (Action) this.actionList.getSelectedValue();
        if (action == null || this.selectedKey == null) {
            return;
        }
        Map<String, Object> profile = editable();
        String keyset = keysetFor(this.selectedKey);
        List<String> wanted = new ArrayList<>();
        if (action.alwaysModifier().equals("any")) {
            wanted.add("Any+" + this.selectedKey.tokens()[0]);
        } else if (action.alwaysModifier().equals("shift")) {
            String bare = keyset.replace("Shift+", "");
            wanted.add(bare);
            wanted.add("Shift+" + bare);
        } else {
            wanted.add(keyset);
        }
        for (String text : wanted) {
            boolean exists = binds(profile).stream().anyMatch(bind -> {
                return action.id().equals(bind.get("action")) && text.equalsIgnoreCase(String.valueOf(bind.get("keyset")));
            });
            if (!exists) {
                Map<String, Object> bind2 = new LinkedHashMap<>();
                bind2.put("keyset", text);
                bind2.put("action", action.id());
                List<Map<String, Object>> all = binds(profile);
                int first = all.size();
                for (int i = 0; i < all.size(); i++) {
                    if (text.equalsIgnoreCase(String.valueOf(all.get(i).get("keyset")))) {
                        first = i;
                        break;
                    }
                }
                all.add(first, bind2);
            }
        }
        if (save()) {
            this.status.setText("\"" + action.label() + "\" is on " + String.join(" and ", wanted).replace("sc_", "") + ". The game uses it from its next start.");
        }
        refresh();
        this.actionList.repaint();
    }

    JComponent contentForTest() {
        return this.dialog.getContentPane();
    }
}

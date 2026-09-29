package com.faforever.neroxis.generator.bar;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/ModOptions.class */
final class ModOptions {
    private final String text;
    private int position;

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/ModOptions$Option.class */
    static final class Option extends Record {
        private final String key;
        private final String name;
        private final String description;
        private final String type;
        private final String def;
        private final String section;
        private final double min;
        private final double max;
        private final double step;
        private final Map<String, String> items;

        Option(String key, String name, String description, String type, String def, String section, double min, double max, double step, Map<String, String> items) {
            this.key = key;
            this.name = name;
            this.description = description;
            this.type = type;
            this.def = def;
            this.section = section;
            this.min = min;
            this.max = max;
            this.step = step;
            this.items = items;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Option.class), Option.class, "key;name;description;type;def;section;min;max;step;items", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->key:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->description:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->type:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->def:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->section:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->min:D", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->max:D", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->step:D", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->items:Ljava/util/Map;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Option.class), Option.class, "key;name;description;type;def;section;min;max;step;items", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->key:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->description:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->type:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->def:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->section:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->min:D", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->max:D", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->step:D", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->items:Ljava/util/Map;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Option.class, Object.class), Option.class, "key;name;description;type;def;section;min;max;step;items", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->key:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->description:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->type:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->def:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->section:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->min:D", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->max:D", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->step:D", "FIELD:Lcom/faforever/neroxis/generator/bar/ModOptions$Option;->items:Ljava/util/Map;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String key() {
            return this.key;
        }

        public String name() {
            return this.name;
        }

        public String description() {
            return this.description;
        }

        public String type() {
            return this.type;
        }

        public String def() {
            return this.def;
        }

        public String section() {
            return this.section;
        }

        public double min() {
            return this.min;
        }

        public double max() {
            return this.max;
        }

        public double step() {
            return this.step;
        }

        public Map<String, String> items() {
            return this.items;
        }
    }

    private ModOptions(String text) {
        this.text = text;
    }

    static List<Option> parse(String lua) {
        List<Option> options = new ArrayList<>();
        int start = lua.indexOf("local options");
        int start2 = start < 0 ? -1 : lua.indexOf(123, start);
        if (start2 < 0) {
            return options;
        }
        ModOptions parser = new ModOptions(lua);
        parser.position = start2;
        try {
            Object table = parser.value();
            if (!(table instanceof Map)) {
                return options;
            }
            Map<?, ?> entries = (Map) table;
            for (Object entry : entries.values()) {
                if (entry instanceof Map) {
                    Map<?, ?> fields = (Map) entry;
                    Object obj = fields.get("key");
                    if (obj instanceof String) {
                        String key = (String) obj;
                        if (!"true".equals(fields.get("hidden"))) {
                            Map<String, String> items = new LinkedHashMap<>();
                            Object obj2 = fields.get("items");
                            if (obj2 instanceof Map) {
                                Map<?, ?> list = (Map) obj2;
                                for (Object item : list.values()) {
                                    if (item instanceof Map) {
                                        Map<?, ?> itemFields = (Map) item;
                                        Object obj3 = itemFields.get("key");
                                        if (obj3 instanceof String) {
                                            String itemKey = (String) obj3;
                                            items.put(itemKey, text(itemFields.get("name"), itemKey));
                                        }
                                    }
                                    if (item instanceof String) {
                                        String plain = (String) item;
                                        items.put(plain, plain);
                                    }
                                }
                            }
                            options.add(new Option(key, text(fields.get("name"), key), text(fields.get("desc"), ""), text(fields.get("type"), "string"), text(fields.get("def"), ""), text(fields.get("section"), ""), number(fields.get("min"), 0.0d), number(fields.get("max"), 1000000.0d), number(fields.get("step"), 1.0d), items));
                        }
                    }
                }
            }
            return options;
        } catch (RuntimeException e) {
            return options;
        }
    }

    private static String text(Object value, String fallback) {
        if (value == null) {
            return fallback;
        }
        if (!(value instanceof Double)) {
            return String.valueOf(value);
        }
        Double number = (Double) value;
        return number.doubleValue() == Math.rint(number.doubleValue()) ? String.valueOf(number.longValue()) : String.valueOf(number);
    }

    private static double number(Object value, double fallback) {
        if (!(value instanceof Double)) {
            return fallback;
        }
        Double number = (Double) value;
        return number.doubleValue();
    }

    private void skipSpace() {
        while (this.position < this.text.length()) {
            char c = this.text.charAt(this.position);
            if (Character.isWhitespace(c)) {
                this.position++;
            } else if (this.text.startsWith("--[[", this.position)) {
                int end = this.text.indexOf("]]", this.position);
                this.position = end < 0 ? this.text.length() : end + 2;
            } else if (this.text.startsWith("--", this.position)) {
                int end2 = this.text.indexOf(10, this.position);
                this.position = end2 < 0 ? this.text.length() : end2;
            } else {
                return;
            }
        }
    }

    private Object value() {
        skipSpace();
        Object result = single();
        skipSpace();
        while (this.text.startsWith("..", this.position)) {
            this.position += 2;
            Object next = single();
            result = text(result, "") + text(next, "");
            skipSpace();
        }
        return result;
    }

    private Object single() {
        skipSpace();
        if (this.position >= this.text.length()) {
            throw new IllegalStateException("unexpected end");
        }
        char c = this.text.charAt(this.position);
        if (c == '{') {
            return table();
        }
        if (c == '\"' || c == '\'') {
            return quoted(c);
        }
        if (this.text.startsWith("[[", this.position)) {
            int end = this.text.indexOf("]]", this.position);
            String content = this.text.substring(this.position + 2, end < 0 ? this.text.length() : end);
            this.position = end < 0 ? this.text.length() : end + 2;
            return content;
        }
        int start = this.position;
        while (this.position < this.text.length() && ((Character.isLetterOrDigit(this.text.charAt(this.position)) || "._-+".indexOf(this.text.charAt(this.position)) >= 0) && (this.text.charAt(this.position) != '-' || this.position <= start || !this.text.startsWith("--", this.position)))) {
            this.position++;
        }
        String token = this.text.substring(start, this.position);
        if (token.isEmpty()) {
            this.position++;
            return null;
        }
        if (token.equals("true") || token.equals("false")) {
            return token;
        }
        try {
            return Double.valueOf(Double.parseDouble(token));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String quoted(char quote) {
        int code;
        StringBuilder result = new StringBuilder();
        this.position++;
        while (this.position < this.text.length() && this.text.charAt(this.position) != quote) {
            String str = this.text;
            int i = this.position;
            this.position = i + 1;
            char c = str.charAt(i);
            if (c != '\\' || this.position >= this.text.length()) {
                result.append(c);
            } else {
                String str2 = this.text;
                int i2 = this.position;
                this.position = i2 + 1;
                char escaped = str2.charAt(i2);
                if (Character.isDigit(escaped)) {
                    int iCharAt = escaped;
                    while (true) {
                        code = iCharAt - 48;
                        if (this.position >= this.text.length() || !Character.isDigit(this.text.charAt(this.position)) || code >= 100) {
                            break;
                        }
                        String str3 = this.text;
                        int i3 = this.position;
                        this.position = i3 + 1;
                        iCharAt = (code * 10) + str3.charAt(i3);
                    }
                    if (code == 255) {
                        for (int i4 = 0; i4 < 3 && this.position < this.text.length() && this.text.charAt(this.position) == '\\'; i4++) {
                            this.position++;
                            while (this.position < this.text.length() && Character.isDigit(this.text.charAt(this.position))) {
                                this.position++;
                            }
                        }
                    }
                } else if (escaped == 'n') {
                    result.append(' ');
                } else {
                    result.append(escaped);
                }
            }
        }
        this.position++;
        return result.toString();
    }

    private Map<Object, Object> table() {
        Map<Object, Object> table = new LinkedHashMap<>();
        this.position++;
        int index = 1;
        while (true) {
            skipSpace();
            if (this.position >= this.text.length()) {
                throw new IllegalStateException("unclosed table");
            }
            if (this.text.charAt(this.position) == '}') {
                this.position++;
                return table;
            }
            if (this.text.charAt(this.position) == ',' || this.text.charAt(this.position) == ';') {
                this.position++;
            } else {
                int mark = this.position;
                String key = null;
                while (this.position < this.text.length() && (Character.isLetterOrDigit(this.text.charAt(this.position)) || this.text.charAt(this.position) == '_')) {
                    this.position++;
                }
                if (this.position > mark) {
                    int afterName = this.position;
                    skipSpace();
                    if (this.position < this.text.length() && this.text.charAt(this.position) == '=' && !this.text.startsWith("==", this.position)) {
                        key = this.text.substring(mark, afterName);
                        this.position++;
                    }
                }
                if (key == null) {
                    this.position = mark;
                    int i = index;
                    index++;
                    table.put(Integer.valueOf(i), value());
                } else {
                    table.put(key, value());
                }
            }
        }
    }
}

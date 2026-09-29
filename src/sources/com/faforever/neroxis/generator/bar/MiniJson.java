package com.faforever.neroxis.generator.bar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/MiniJson.class */
final class MiniJson {
    private final String text;
    private int position;

    private MiniJson(String text) {
        this.text = text;
    }

    static Object parse(String json) {
        MiniJson parser = new MiniJson(json);
        try {
            Object value = parser.value();
            parser.space();
            return value;
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new IllegalArgumentException("not JSON at " + parser.position, e);
        }
    }

    static String write(Object value) {
        StringBuilder out = new StringBuilder();
        write(out, value, 0);
        return out.append('\n').toString();
    }

    private static void write(StringBuilder out, Object value, int depth) {
        String indent = "  ".repeat(depth + 1);
        if (value instanceof Map) {
            Map<?, ?> map = (Map) value;
            if (map.isEmpty()) {
                out.append("{}");
                return;
            }
            out.append("{\n");
            int index = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                out.append(indent);
                quote(out, String.valueOf(entry.getKey()));
                out.append(": ");
                write(out, entry.getValue(), depth + 1);
                index++;
                out.append(index < map.size() ? ",\n" : "\n");
            }
            out.append("  ".repeat(depth)).append('}');
            return;
        }
        if (value instanceof List) {
            List<?> list = (List) value;
            if (list.isEmpty()) {
                out.append("[]");
                return;
            }
            out.append("[\n");
            for (int i = 0; i < list.size(); i++) {
                out.append(indent);
                write(out, list.get(i), depth + 1);
                out.append(i + 1 < list.size() ? ",\n" : "\n");
            }
            out.append("  ".repeat(depth)).append(']');
            return;
        }
        if (value instanceof String) {
            String string = (String) value;
            quote(out, string);
            return;
        }
        if (value instanceof Double) {
            Double number = (Double) value;
            if (number.doubleValue() == Math.rint(number.doubleValue()) && Math.abs(number.doubleValue()) < 1.0E15d) {
                out.append(number.longValue());
                return;
            }
        }
        out.append(value);
    }

    private static void quote(StringBuilder out, String string) {
        out.append('\"');
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            switch (c) {
                case '\t':
                    out.append("\\t");
                    break;
                case '\n':
                    out.append("\\n");
                    break;
                case '\r':
                    out.append("\\r");
                    break;
                case '\"':
                    out.append("\\\"");
                    break;
                case '\\':
                    out.append("\\\\");
                    break;
                default:
                    if (c < ' ') {
                        out.append(String.format("\\u%04x", Integer.valueOf(c)));
                    } else {
                        out.append(c);
                    }
                    break;
            }
        }
        out.append('\"');
    }

    private void space() {
        while (this.position < this.text.length() && Character.isWhitespace(this.text.charAt(this.position))) {
            this.position++;
        }
    }

    private Object value() {
        String str;
        int i;
        String str2;
        int i2;
        space();
        char c = this.text.charAt(this.position);
        if (c == '{') {
            this.position++;
            Map<String, Object> map = new LinkedHashMap<>();
            space();
            if (this.text.charAt(this.position) == '}') {
                this.position++;
                return map;
            }
            do {
                space();
                String key = string();
                space();
                expect(':');
                map.put(key, value());
                space();
                str2 = this.text;
                i2 = this.position;
                this.position = i2 + 1;
            } while (str2.charAt(i2) != '}');
            return map;
        }
        if (c == '[') {
            this.position++;
            List<Object> list = new ArrayList<>();
            space();
            if (this.text.charAt(this.position) == ']') {
                this.position++;
                return list;
            }
            do {
                list.add(value());
                space();
                str = this.text;
                i = this.position;
                this.position = i + 1;
            } while (str.charAt(i) != ']');
            return list;
        }
        if (c == '\"') {
            return string();
        }
        int start = this.position;
        while (this.position < this.text.length() && ",}] \n\r\t".indexOf(this.text.charAt(this.position)) < 0) {
            this.position++;
        }
        String token = this.text.substring(start, this.position);
        switch (token) {
            case "true":
                return Boolean.TRUE;
            case "false":
                return Boolean.FALSE;
            case "null":
                return null;
            default:
                return Double.valueOf(Double.parseDouble(token));
        }
    }

    private void expect(char wanted) {
        String str = this.text;
        int i = this.position;
        this.position = i + 1;
        if (str.charAt(i) != wanted) {
            throw new IllegalArgumentException("expected " + wanted + " at " + (this.position - 1));
        }
    }

    private String string() {
        expect('\"');
        StringBuilder result = new StringBuilder();
        while (true) {
            String str = this.text;
            int i = this.position;
            this.position = i + 1;
            char c = str.charAt(i);
            if (c == '\"') {
                return result.toString();
            }
            if (c != '\\') {
                result.append(c);
            } else {
                String str2 = this.text;
                int i2 = this.position;
                this.position = i2 + 1;
                char escaped = str2.charAt(i2);
                switch (escaped) {
                    case 'b':
                        result.append('\b');
                        break;
                    case 'c':
                    case 'd':
                    case 'e':
                    case 'g':
                    case 'h':
                    case 'i':
                    case 'j':
                    case 'k':
                    case 'l':
                    case 'm':
                    case 'o':
                    case 'p':
                    case 'q':
                    case 's':
                    default:
                        result.append(escaped);
                        break;
                    case 'f':
                        result.append('\f');
                        break;
                    case 'n':
                        result.append('\n');
                        break;
                    case 'r':
                        result.append('\r');
                        break;
                    case 't':
                        result.append('\t');
                        break;
                    case 'u':
                        result.append((char) Integer.parseInt(this.text.substring(this.position, this.position + 4), 16));
                        this.position += 4;
                        break;
                }
            }
        }
    }
}

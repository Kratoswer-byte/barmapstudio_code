package com.faforever.neroxis.util;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/ArgumentParser.class */
public class ArgumentParser {
    public static Map<String, String> parse(String[] args) {
        Map<String, String> res = new HashMap<>();
        int i = 0;
        while (i < args.length) {
            if (!args[i].startsWith("--")) {
                throw new IllegalArgumentException("Wrong formatting of arguments. Expected: --");
            }
            String key = args[i].substring(2);
            res.put(key, null);
            if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                i++;
                res.put(key, args[i]);
            }
            i++;
        }
        return res;
    }
}

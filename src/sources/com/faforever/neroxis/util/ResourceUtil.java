package com.faforever.neroxis.util;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/ResourceUtil.class */
public class ResourceUtil {
    public static InputStream getResourceAsStream(String name) throws IOException {
        return getResourceAsStream((Class<?>) ResourceUtil.class, name);
    }

    public static InputStream getResourceAsStream(Class<?> clazz, String name) throws IOException {
        return getResourceAsStream(clazz.getModule(), name);
    }

    public static InputStream getResourceAsStream(Module module, String name) throws IOException {
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        InputStream inputStream = module.getResourceAsStream(name);
        if (inputStream != null) {
            return inputStream;
        }
        return ClassLoader.getSystemResourceAsStream(name.substring(1));
    }
}

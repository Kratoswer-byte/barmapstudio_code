package com.faforever.neroxis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Collectors;
import tools.jackson.databind.json.JsonMapper;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/FileUtil.class */
public class FileUtil {
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    public static void deleteRecursiveIfExists(Path path) {
        if (!Files.exists(path, new LinkOption[0])) {
            return;
        }
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() { // from class: com.faforever.neroxis.util.FileUtil.1
                @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readFile(String filePath) throws IOException {
        InputStream inputStream = ResourceUtil.getResourceAsStream(filePath);
        if (inputStream != null) {
            try {
                String str = (String) new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
                if (inputStream != null) {
                    inputStream.close();
                }
                return str;
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        if (inputStream != null) {
            inputStream.close();
        }
        return (String) new BufferedReader(new FileReader(Paths.get(filePath, new String[0]).toFile())).lines().collect(Collectors.joining("\n"));
    }

    public static <T> T deserialize(String str, Class<T> cls) throws IOException {
        InputStream resourceAsStream = ResourceUtil.getResourceAsStream(str);
        try {
            if (resourceAsStream == null) {
                throw new IOException("File not found: " + str);
            }
            T t = (T) deserialize(resourceAsStream, cls);
            if (resourceAsStream != null) {
                resourceAsStream.close();
            }
            return t;
        } catch (Throwable th) {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static <T> T deserialize(InputStream inputStream, Class<T> cls) {
        return (T) JSON_MAPPER.readValue(inputStream, cls);
    }

    public static <T> void serialize(String filename, T obj) throws IOException {
        serialize(new FileOutputStream(filename), obj);
    }

    public static <T> void serialize(File file, T obj) throws IOException {
        serialize(new FileOutputStream(file), obj);
    }

    public static <T> void serialize(OutputStream outputStream, T obj) {
        JSON_MAPPER.writeValue(outputStream, obj);
    }
}

package com.faforever.neroxis.generator.bar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarFolder.class */
final class BarFolder {
    private static final String[] NAMES = {"Beyond-All-Reason", "Beyond All Reason", "BAR"};

    private BarFolder() {
    }

    static boolean isData(Path folder) {
        return folder != null && Files.isDirectory(folder, new LinkOption[0]) && (Files.isDirectory(folder.resolve("engine"), new LinkOption[0]) || Files.isDirectory(folder.resolve("maps"), new LinkOption[0]) || Files.isDirectory(folder.resolve("games"), new LinkOption[0]) || Files.isDirectory(folder.resolve("rapid"), new LinkOption[0]));
    }

    static Path fromChoice(Path picked) {
        if (picked == null) {
            return null;
        }
        Path folder = picked.toAbsolutePath().normalize();
        for (Path candidate : new Path[]{folder, folder.resolve("data"), folder.getParent()}) {
            if (isData(candidate)) {
                return candidate;
            }
        }
        for (String name : NAMES) {
            if (isData(folder.resolve(name).resolve("data"))) {
                return folder.resolve(name).resolve("data");
            }
        }
        return null;
    }

    static Path find(String chosenBefore) {
        Set<Path> quick = new LinkedHashSet<>();
        add(quick, chosenBefore);
        add(quick, System.getenv("BAR_DATA"));
        quick.add(usual());
        for (Path candidate : quick) {
            if (isData(candidate)) {
                return candidate;
            }
        }
        Iterator<Path> it = candidates(chosenBefore).iterator();
        while (it.hasNext()) {
            Path data = fromChoice(it.next());
            if (data != null) {
                return data;
            }
        }
        return null;
    }

    static Path usual() {
        return Platform.defaultData();
    }

    private static List<Path> candidates(String chosenBefore) {
        Set<Path> result = new LinkedHashSet<>();
        add(result, chosenBefore);
        add(result, System.getenv("BAR_DATA"));
        result.add(usual());
        if (!Platform.WINDOWS) {
            result.addAll(Platform.linuxDataCandidates());
        }
        result.addAll(fromRegistry());
        List<String> bases = new ArrayList<>();
        for (String variable : new String[]{"ProgramFiles", "ProgramFiles(x86)", "ProgramW6432"}) {
            String value = System.getenv(variable);
            if (value != null) {
                bases.add(value);
            }
        }
        bases.add(System.getProperty("user.home"));
        bases.add(Path.of(System.getProperty("user.home"), "Games").toString());
        bases.add(Path.of(System.getProperty("user.home"), "Documents", "My Games").toString());
        for (File root : File.listRoots()) {
            bases.add(root.getPath());
            for (String folder : new String[]{"Games", "Program Files", "Program Files (x86)", "SteamLibrary", "Programs"}) {
                bases.add(new File(root, folder).getPath());
            }
        }
        for (String base : bases) {
            for (String name : NAMES) {
                result.add(Path.of(base, name, "data"));
            }
        }
        return new ArrayList(result);
    }

    private static void add(Set<Path> result, String path) {
        if (path != null && !path.isBlank()) {
            try {
                result.add(Path.of(path.trim(), new String[0]));
            } catch (RuntimeException e) {
            }
        }
    }

    private static List<Path> fromRegistry() {
        List<Path> result = new ArrayList<>();
        if (!System.getProperty("os.name", "").toLowerCase().contains("win")) {
            return result;
        }
        for (String key : new String[]{"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall", "HKLM\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall", "HKLM\\Software\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall"}) {
            try {
                Process process = new ProcessBuilder("reg", "query", key, "/s", "/f", "Beyond", "/d").redirectErrorStream(true).start();
                List<String> lines = new ArrayList<>();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                while (true) {
                    try {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        lines.add(line);
                    } catch (Throwable th) {
                        try {
                            reader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                }
                reader.close();
                process.waitFor(5L, TimeUnit.SECONDS);
                for (String line2 : lines) {
                    if (line2.startsWith("HKEY_")) {
                        result.addAll(locationsOf(line2.trim()));
                    }
                }
            } catch (IOException e) {
            } catch (InterruptedException e2) {
                Thread.currentThread().interrupt();
                return result;
            }
        }
        return result;
    }

    private static List<Path> locationsOf(String key) throws InterruptedException, IOException {
        List<Path> result = new ArrayList<>();
        Process process = new ProcessBuilder("reg", "query", key).redirectErrorStream(true).start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        while (true) {
            try {
                String line = reader.readLine();
                if (line != null) {
                    String[] parts = line.trim().split("\\s{2,}", 3);
                    if (parts.length == 3 && parts[0].matches("InstallLocation|DisplayIcon|UninstallString")) {
                        String value = parts[2].replace("\"", "").replaceAll(",\\d+$", "").trim();
                        try {
                            Path path = Path.of(value, new String[0]);
                            result.add(parts[0].equals("InstallLocation") ? path : path.getParent());
                        } catch (RuntimeException e) {
                        }
                    }
                } else {
                    reader.close();
                    process.waitFor(5L, TimeUnit.SECONDS);
                    result.removeIf(path2 -> {
                        return path2 == null;
                    });
                    return result;
                }
            } catch (Throwable th) {
                try {
                    reader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
    }
}

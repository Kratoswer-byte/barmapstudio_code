package com.faforever.neroxis.generator.bar;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/Platform.class */
final class Platform {
    static final boolean WINDOWS = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
    static final boolean MAC = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("mac");
    static final boolean LINUX = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("linux");

    private Platform() {
    }

    static boolean isWindows() {
        return WINDOWS;
    }

    static String program(String name) {
        return WINDOWS ? name + ".exe" : name;
    }

    static boolean hasEngine(Path folder) {
        Path spring = folder.resolve(program("spring"));
        if (WINDOWS) {
            return Files.isRegularFile(spring, new LinkOption[0]);
        }
        return Files.isRegularFile(spring, new LinkOption[0]) && Files.isExecutable(spring);
    }

    static String enginePackage(String version) {
        if (WINDOWS) {
            return "recoil_" + version + "_amd64-windows.7z";
        }
        String arch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);
        String cpu = (arch.contains("aarch64") || arch.contains("arm64")) ? "arm64" : "amd64";
        return "recoil_" + version + "_" + cpu + (MAC ? "-macos.7z" : "-linux.7z");
    }

    static String engineUrl(String version, String mapService) {
        return "https://github.com/beyond-all-reason/RecoilEngine/releases/download/" + version + "/" + enginePackage(version);
    }

    static boolean unpackEngine(Path archive, Path into, Path sevenZip) throws InterruptedException, IOException {
        Files.createDirectories(into, new FileAttribute[0]);
        ProcessBuilder command = new ProcessBuilder(String.valueOf(sevenZip), "x", "-y", "-o" + String.valueOf(into), archive.toString());
        Process process = command.redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
        if (process.waitFor() != 0) {
            return false;
        }
        if (!hasEngine(into)) {
            Stream<Path> entries = Files.list(into);
            try {
                List<Path> folders = entries.filter(x$0 -> {
                    return Files.isDirectory(x$0, new LinkOption[0]);
                }).toList();
                if (folders.size() == 1 && hasEngine(folders.get(0))) {
                    Path inner = folders.get(0);
                    Stream<Path> files = Files.list(inner);
                    try {
                        for (Path file : files.toList()) {
                            Files.move(file, into.resolve(file.getFileName()), new CopyOption[0]);
                        }
                        if (files != null) {
                            files.close();
                        }
                        Files.delete(inner);
                    } catch (Throwable th) {
                        if (files != null) {
                            try {
                                files.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                }
                if (entries != null) {
                    entries.close();
                }
            } catch (Throwable th3) {
                if (entries != null) {
                    try {
                        entries.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                }
                throw th3;
            }
        }
        return hasEngine(into);
    }

    static Path settingsFile(Path home) {
        Path inside = home.resolve("launcher.properties");
        if (!MAC) {
            return inside;
        }
        Path folder = Path.of(System.getProperty("user.home"), "Library", "Application Support", "BARMapStudio");
        Path outside = folder.resolve("launcher.properties");
        try {
            Files.createDirectories(folder, new FileAttribute[0]);
            if (!Files.isRegularFile(outside, new LinkOption[0]) && Files.isRegularFile(inside, new LinkOption[0])) {
                Files.copy(inside, outside, new CopyOption[0]);
            }
            return outside;
        } catch (IOException e) {
            return inside;
        }
    }

    static String startScript() {
        return WINDOWS ? "bar-map-launcher.bat" : "bar-map-launcher.sh";
    }

    static Path runtimeJava(Path home) {
        return home.resolve("runtime").resolve("bin").resolve(WINDOWS ? "javaw.exe" : "java");
    }

    static Path defaultData() {
        String override = System.getenv("BAR_DATA");
        if (override != null && !override.isBlank()) {
            return Path.of(override, new String[0]);
        }
        if (WINDOWS) {
            String localAppData = System.getenv("LOCALAPPDATA");
            Path local = Path.of(localAppData != null ? localAppData : System.getProperty("user.home"), "Programs", "Beyond-All-Reason", "data");
            if (Files.isDirectory(local)) {
                return local;
            }
            String progFiles = System.getenv("ProgramFiles");
            Path prog = Path.of(progFiles != null ? progFiles : "C:\\Program Files", "Beyond-All-Reason", "data");
            if (Files.isDirectory(prog)) {
                return prog;
            }
            Path fixedProg = Path.of("C:\\Program Files\\Beyond-All-Reason\\data");
            if (Files.isDirectory(fixedProg)) {
                return fixedProg;
            }
            return local;
        }
        if (MAC) {
            return MacOs.dataDir();
        }
        List<Path> candidates = linuxDataCandidates();
        for (Path candidate : candidates) {
            if (Files.isDirectory(candidate.resolve("engine"), new LinkOption[0]) || Files.isDirectory(candidate.resolve("games"), new LinkOption[0])) {
                return candidate;
            }
        }
        return candidates.get(0);
    }

    static List<Path> linuxDataCandidates() {
        Path home = Path.of(System.getProperty("user.home"), new String[0]);
        String state = System.getenv("XDG_STATE_HOME");
        List<Path> candidates = new ArrayList<>();
        candidates.add(((state == null || state.isBlank()) ? home.resolve(".local").resolve("state") : Path.of(state, new String[0])).resolve("Beyond-All-Reason"));
        candidates.add(home.resolve("Documents").resolve("Beyond-All-Reason"));
        candidates.add(home.resolve(".var").resolve("app").resolve("info.beyondallreason.bar").resolve(".local").resolve("state").resolve("Beyond-All-Reason"));
        candidates.add(home.resolve(".local").resolve("share").resolve("Beyond-All-Reason"));
        return candidates;
    }

    static String dataHint() {
        if (WINDOWS) {
            return "It usually is %LOCALAPPDATA%\\Programs\\Beyond-All-Reason\\data";
        }
        return MAC ? "It usually is ~/Library/Application Support/Beyond-All-Reason-mac" : "It usually is ~/.local/state/Beyond-All-Reason (the Flatpak: ~/.var/app/info.beyondallreason.bar/.local/state/Beyond-All-Reason)";
    }

    static Path sevenZip(Path data) {
        if (MAC) {
            String home = System.getProperty("neroxis.home", System.getProperty("user.dir"));
            return MacOs.sevenZip(Path.of(home, new String[0]));
        }
        if (WINDOWS) {
            Path parent = data.toAbsolutePath().getParent();
            Path bundled = parent != null ? parent.resolve("resources/app.asar.unpacked/node_modules/7zip-bin/win/x64/7za.exe") : null;
            if (bundled == null || !Files.isRegularFile(bundled, new LinkOption[0])) {
                return null;
            }
            return bundled;
        }
        String path = System.getenv("PATH");
        for (String folder : (path != null ? path : "").split(File.pathSeparator)) {
            for (String name : new String[]{"7z", "7za", "7zr", "7zz"}) {
                Path candidate = Path.of(folder.isEmpty() ? "." : folder, name);
                if (Files.isRegularFile(candidate, new LinkOption[0]) && Files.isExecutable(candidate)) {
                    return candidate;
                }
            }
        }
        for (String root : new String[]{"/app/main/resources", "/opt/Beyond-All-Reason/resources", "/usr/lib/beyond-all-reason/resources"}) {
            Path bundled2 = Path.of(root, "app.asar.unpacked/node_modules/7zip-bin/linux/x64/7za");
            if (Files.isRegularFile(bundled2, new LinkOption[0])) {
                return bundled2;
            }
        }
        return null;
    }

    static String sevenZipMissing() {
        if (WINDOWS) {
            return "the 7-Zip of the BAR launcher was not found";
        }
        return MAC ? "7zz not found in the application folder (brew install sevenzip also works)" : "no 7z found: install p7zip (for example: sudo apt install p7zip-full)";
    }
}

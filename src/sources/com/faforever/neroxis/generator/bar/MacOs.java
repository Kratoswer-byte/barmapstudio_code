package com.faforever.neroxis.generator.bar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/MacOs.class */
final class MacOs {
    static final String DATA_FOLDER = "Beyond-All-Reason-mac";
    static final String PORT_URL = "https://github.com/Vandomas/RecoilEngine-AppleSilicon/releases/latest";
    private static final String MARKER = ".eclipse-wrapper";
    static final String ENGINE_APP = "Recoil Engine.app";
    private static final String[] APP_NAMES = {"BAR Launcher.app", ENGINE_APP, "Beyond All Reason.app"};

    private MacOs() {
    }

    static boolean isMac() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("mac");
    }

    static Path bundle() {
        String bundle = System.getProperty("neroxis.bundle");
        if (bundle == null || bundle.isBlank()) {
            return null;
        }
        return Path.of(bundle, new String[0]);
    }

    static Path dataDir() {
        return Path.of(System.getProperty("user.home"), "Library", "Application Support", DATA_FOLDER);
    }

    static String enginePackage(String version) {
        return "recoil-macos-arm64-" + version + ".zip";
    }

    static Path findApp() {
        List<Path> candidates = new ArrayList<>();
        for (String override : new String[]{System.getProperty("neroxis.mac.app"), System.getenv("BAR_MAC_APP")}) {
            if (override != null && !override.isBlank()) {
                candidates.add(Path.of(override.trim(), new String[0]));
            }
        }
        Path bundle = bundle();
        if (bundle != null) {
            candidates.add(bundle.resolve("Engine").resolve(ENGINE_APP));
        }
        String home = System.getProperty("neroxis.home");
        if (home != null) {
            candidates.add(Path.of(home, "Engine", ENGINE_APP));
        }
        for (String base : new String[]{"/Applications", Path.of(System.getProperty("user.home"), "Applications").toString()}) {
            for (String name : APP_NAMES) {
                candidates.add(Path.of(base, name));
            }
        }
        for (Path candidate : candidates) {
            if (isApp(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    static boolean isApp(Path app) {
        return app != null && Files.isExecutable(app.resolve("Contents").resolve("MacOS").resolve("spring"));
    }

    static String engineVersion(Path app) {
        Path plist = app.resolve("Contents").resolve("Info.plist");
        try {
            String text = Files.readString(plist, StandardCharsets.UTF_8);
            for (String key : new String[]{"EngineVersion", "CFBundleVersion"}) {
                Matcher matcher = Pattern.compile("<key>" + key + "</key>\\s*<string>([^<]+)</string>").matcher(text);
                if (matcher.find() && matcher.group(1).matches("[0-9A-Za-z._-]{3,40}")) {
                    return matcher.group(1).trim();
                }
            }
            return null;
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    static Path sevenZip(Path home) {
        Path bundle = bundle();
        List<Path> candidates = List.of(bundle != null ? bundle.resolve("7zz") : home.resolve("7zz"), home.resolve("7zz"), home.resolve("bin").resolve("7zz"), Path.of("/opt/homebrew/bin/7zz", new String[0]), Path.of("/usr/local/bin/7zz", new String[0]), Path.of("/opt/homebrew/bin/7z", new String[0]), Path.of("/usr/local/bin/7z", new String[0]), Path.of("/opt/local/bin/7zz", new String[0]), Path.of("/opt/local/bin/7z", new String[0]));
        for (Path candidate : candidates) {
            if (Files.isExecutable(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    static String noEngine() {
        return "No BAR engine on this Mac: this Eclipse package has none inside, and none was downloaded. Get the Mac package of Eclipse again, or install the Apple Silicon port of Beyond All Reason (https://github.com/Vandomas/RecoilEngine-AppleSilicon/releases/latest).";
    }

    static String engineMissing(String version) {
        List<String> have = new ArrayList<>();
        for (Path app : apps(dataDir()).values()) {
            String installed = engineVersion(app);
            if (installed != null) {
                have.add(installed);
            }
        }
        if (have.isEmpty()) {
            return noEngine();
        }
        return "This Mac has the engine " + String.join(", ", have) + ", this room runs " + version + ", and there is no Mac build of " + version + " on the Eclipse server yet.";
    }

    static Map<String, Path> apps(Path data) {
        Map<String, Path> apps = new LinkedHashMap<>();
        Path app = findApp();
        String version = app != null ? engineVersion(app) : null;
        if (app != null && version != null) {
            apps.put(version, app);
        }
        Path engines = data.resolve("engine");
        if (Files.isDirectory(engines, new LinkOption[0])) {
            try {
                Stream<Path> folders = Files.list(engines);
                try {
                    for (Path folder : folders.sorted().toList()) {
                        Path downloaded = folder.resolve(ENGINE_APP);
                        String downloadedVersion = isApp(downloaded) ? engineVersion(downloaded) : null;
                        if (downloadedVersion != null) {
                            apps.putIfAbsent(downloadedVersion, downloaded);
                        }
                    }
                    if (folders != null) {
                        folders.close();
                    }
                } catch (Throwable th) {
                    if (folders != null) {
                        try {
                            folders.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (IOException e) {
            }
        }
        return apps;
    }

    static Path prepare(Path data) {
        if (!isMac()) {
            return null;
        }
        Map<String, Path> apps = apps(data);
        if (apps.isEmpty()) {
            return null;
        }
        Path engines = data.resolve("engine");
        Path first = null;
        try {
            Files.createDirectories(data.resolve("maps"), new FileAttribute[0]);
            if (Files.isDirectory(engines, new LinkOption[0])) {
                Stream<Path> old = Files.list(engines);
                try {
                    for (Path other : old.toList()) {
                        boolean ours = Files.isRegularFile(other.resolve(MARKER), new LinkOption[0]);
                        boolean current = apps.containsKey(other.getFileName().toString().replaceFirst("^recoil_", ""));
                        if (ours && !current) {
                            deleteTree(other);
                        }
                    }
                    if (old != null) {
                        old.close();
                    }
                } catch (Throwable th) {
                    if (old != null) {
                        try {
                            old.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            for (Map.Entry<String, Path> entry : apps.entrySet()) {
                Path folder = engines.resolve("recoil_" + entry.getKey());
                Files.createDirectories(folder, new FileAttribute[0]);
                writeScript(folder.resolve("spring"), engineScript(entry.getValue()));
                writeScript(folder.resolve("pr-downloader"), programScript(entry.getValue(), "pr-downloader"));
                Files.writeString(folder.resolve(MARKER), String.valueOf(entry.getValue()) + "\n" + entry.getKey() + "\n", StandardCharsets.UTF_8, new OpenOption[0]);
                if (first == null) {
                    first = folder;
                }
            }
            return first;
        } catch (IOException e) {
            return null;
        }
    }

    static String downloadEngine(String version, String downloadBase, Path data, Consumer<String> progress) {
        Path folder = data.resolve("engine").resolve("recoil_" + version);
        String url = downloadBase + enginePackage(version);
        Path unpacking = folder.resolveSibling(String.valueOf(folder.getFileName()) + ".installing");
        try {
            try {
                Path archive = Files.createTempFile("recoil-mac-" + version, ".zip", new FileAttribute[0]);
                HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(15L)).build();
                HttpResponse<InputStream> response = http.send(HttpRequest.newBuilder(URI.create(url)).build(), HttpResponse.BodyHandlers.ofInputStream());
                if (response.statusCode() == 404) {
                    String str = "no Mac build of the engine " + version + " on the Eclipse server yet";
                    if (archive != null) {
                        try {
                            Files.deleteIfExists(archive);
                        } catch (IOException e) {
                        }
                    }
                    deleteTree(unpacking);
                    return str;
                }
                if (response.statusCode() != 200) {
                    String str2 = "the download server answered " + response.statusCode();
                    if (archive != null) {
                        try {
                            Files.deleteIfExists(archive);
                        } catch (IOException e2) {
                        }
                    }
                    deleteTree(unpacking);
                    return str2;
                }
                long total = response.headers().firstValueAsLong("Content-Length").orElse(-1L);
                InputStream stream = (InputStream) response.body();
                try {
                    OutputStream out = Files.newOutputStream(archive, new OpenOption[0]);
                    try {
                        byte[] buffer = new byte[65536];
                        long done = 0;
                        long shown = 0;
                        while (true) {
                            int read = stream.read(buffer);
                            if (read <= 0) {
                                break;
                            }
                            out.write(buffer, 0, read);
                            done += (long) read;
                            if (done - shown > 2000000) {
                                shown = done;
                                long j = done / 1000000;
                                if (total > 0) {
                                    String str3 = " of " + (total / 1000000);
                                }
                                progress.accept("Downloading the engine " + version + " for Mac... " + j + progress + " MB");
                            }
                        }
                        if (out != null) {
                            out.close();
                        }
                        if (stream != null) {
                            stream.close();
                        }
                        progress.accept("Unpacking the engine " + version + "...");
                        deleteTree(unpacking);
                        Files.createDirectories(unpacking, new FileAttribute[0]);
                        Process tar = new ProcessBuilder("/usr/bin/ditto", "-x", "-k", archive.toString(), unpacking.toString()).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
                        Path app = unpacking.resolve(ENGINE_APP);
                        if (tar.waitFor() != 0 || !isApp(app)) {
                            if (archive != null) {
                                try {
                                    Files.deleteIfExists(archive);
                                } catch (IOException e3) {
                                }
                            }
                            deleteTree(unpacking);
                            return "unpacking failed";
                        }
                        String unpacked = engineVersion(app);
                        if (!version.equals(unpacked)) {
                            String str4 = "the package holds the engine " + unpacked + ", not " + version;
                            if (archive != null) {
                                try {
                                    Files.deleteIfExists(archive);
                                } catch (IOException e4) {
                                }
                            }
                            deleteTree(unpacking);
                            return str4;
                        }
                        deleteTree(folder);
                        Files.createDirectories(folder, new FileAttribute[0]);
                        Files.move(app, folder.resolve(ENGINE_APP), new CopyOption[0]);
                        deleteTree(unpacking);
                        prepare(data);
                        String str5 = Platform.hasEngine(folder) ? null : "the engine wrappers could not be written";
                        if (archive != null) {
                            try {
                                Files.deleteIfExists(archive);
                            } catch (IOException e5) {
                            }
                        }
                        deleteTree(unpacking);
                        return str5;
                    } catch (Throwable th) {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                        }
                    }
                    throw th3;
                }
            } catch (Throwable th5) {
                if (0 != 0) {
                    try {
                        Files.deleteIfExists(null);
                    } catch (IOException e6) {
                        throw th5;
                    }
                }
                deleteTree(unpacking);
                throw th5;
            }
        } catch (IOException | InterruptedException e7) {
            String message = e7.getMessage();
            if (0 != 0) {
                try {
                    Files.deleteIfExists(null);
                } catch (IOException e8) {
                    return message;
                }
            }
            deleteTree(unpacking);
            return message;
        }
    }

    static String engineScript(Path app) {
        String bundle = shellQuote(app.toAbsolutePath().toString());
        return "#!/bin/sh\n# Written by Eclipse: runs the Recoil engine of the Apple Silicon port with the environment its own\n# launcher sets. Rewritten at every start of Eclipse, edits are lost.\nAPP=%s\nRES=\"$APP/Contents/Resources\"\nif [ ! -x \"$APP/Contents/MacOS/spring\" ]; then\n  echo \"The engine is missing at $APP\" >&2\n  exit 127\nfi\n# OpenGL 4.6 through Mesa zink on Vulkan: KosmicKrisp needs Metal 4 (macOS 26), MoltenVK works everywhere\nKK=\"$RES/vulkan/icd.d/kosmickrisp_mesa_icd.aarch64.json\"\nMVK=\"$RES/vulkan/icd.d/moltenvk_icd.json\"\nOS_MAJOR=\"$(sw_vers -productVersion 2>/dev/null | cut -d. -f1)\"\ncase \"${BAR_VULKAN_DRIVER:-}\" in\n  kk) ICD=\"$KK\" ;;\n  mvk) ICD=\"$MVK\" ;;\n  *) if [ \"${OS_MAJOR:-0}\" -ge 26 ] && [ -f \"$KK\" ]; then ICD=\"$KK\"; else ICD=\"$MVK\"; fi ;;\nesac\n[ -f \"$ICD\" ] || ICD=\"$MVK\"\n[ -f \"$ICD\" ] || ICD=\"$KK\"\nif [ \"$ICD\" = \"$MVK\" ]; then\n  export MVK_CONFIG_USE_COMMAND_POOLING=0\n  export MVK_CONFIG_USE_METAL_ARGUMENT_BUFFERS=0\n  export MVK_EXIT_ON_DEVICE_LOSS=1\n  export MVK_CONFIG_DEBUG=1\nfi\nexport EGL_PLATFORM=surfaceless\nexport VK_ICD_FILENAMES=\"$ICD\"\nexport VK_DRIVER_FILES=\"$ICD\"\nexport GALLIUM_DRIVER=zink\nexport MESA_LOADER_DRIVER_OVERRIDE=zink\nexport MESA_GL_VERSION_OVERRIDE=4.6\nexport MVK_CONFIG_LOG_LEVEL=1\nexport DYLD_FALLBACK_LIBRARY_PATH=\"$APP/Contents/Frameworks\"\n# the engine's own content (base archives, AIs, fonts) is in the app: with --isolation the engine\n# reads the write dir and this folder, nothing else\nexport SPRING_ISOLATED=\"$RES\"\nexec \"$APP/Contents/MacOS/spring\" \"$@\"\n".formatted(bundle);
    }

    static String programScript(Path app, String program) {
        String bundle = shellQuote(app.toAbsolutePath().toString());
        return "#!/bin/sh\n# Written by Eclipse: runs %s of the Apple Silicon port. Rewritten at every start of Eclipse.\nAPP=%s\nexport DYLD_FALLBACK_LIBRARY_PATH=\"$APP/Contents/Frameworks\"\nexec \"$APP/Contents/MacOS/%s\" \"$@\"\n".formatted(program, bundle, program);
    }

    private static void writeScript(Path file, String text) throws IOException {
        if (!Files.isRegularFile(file, new LinkOption[0]) || !text.equals(Files.readString(file, StandardCharsets.UTF_8))) {
            Files.writeString(file, text, StandardCharsets.UTF_8, new OpenOption[0]);
        }
        try {
            Files.setPosixFilePermissions(file, EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_EXECUTE));
        } catch (UnsupportedOperationException e) {
        }
    }

    private static void deleteTree(Path folder) throws IOException {
        if (!Files.exists(folder, new LinkOption[0])) {
            return;
        }
        Stream<Path> tree = Files.walk(folder, new FileVisitOption[0]);
        try {
            for (Path path : tree.sorted((a, b) -> {
                return b.getNameCount() - a.getNameCount();
            }).toList()) {
                Files.deleteIfExists(path);
            }
            if (tree != null) {
                tree.close();
            }
        } catch (Throwable th) {
            if (tree != null) {
                try {
                    tree.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    private static String shellQuote(String text) {
        return "'" + text.replace("'", "'\\''") + "'";
    }
}

package com.faforever.neroxis.generator.bar;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Trova Beyond All Reason sul computer e conosce le differenze tra i sistemi operativi:
 * dove sta la cartella "data" del gioco, come si chiamano i programmi, dove trovare 7-Zip.
 */
final class BarLocator {
    private static final String OS = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
    private static final boolean WINDOWS = OS.contains("win");
    private static final boolean MAC = OS.contains("mac");
    /** Nomi che la cartella di installazione puo' avere. */
    private static final String[] FOLDER_NAMES = {"Beyond-All-Reason", "Beyond All Reason", "BAR"};

    private BarLocator() {}

    static boolean isWindows() {
        return WINDOWS;
    }

    static boolean isMac() {
        return MAC;
    }

    /** Nome del file di un programma su questo sistema ("spring" diventa "spring.exe" su Windows). */
    static String program(String name) {
        return WINDOWS ? name + ".exe" : name;
    }

    /** Vero se la cartella contiene un motore di gioco avviabile. */
    static boolean hasEngine(Path folder) {
        Path spring = folder.resolve(program("spring"));
        return Files.isRegularFile(spring) && (WINDOWS || Files.isExecutable(spring));
    }

    /** Vero se la cartella e' la "data" di BAR: quella con i motori, le mappe e i file del gioco. */
    static boolean isData(Path folder) {
        return folder != null && Files.isDirectory(folder)
            && (Files.isDirectory(folder.resolve("engine")) || Files.isDirectory(folder.resolve("maps")));
    }

    // ------------------------------------------------------------------ ricerca

    /** Le posizioni in cui BAR mette la sua cartella "data" su questo sistema, dalla piu' probabile. */
    private static List<Path> usualPlaces() {
        List<Path> places = new ArrayList<>();
        Path home = Path.of(System.getProperty("user.home"));
        if (WINDOWS) {
            String local = System.getenv("LOCALAPPDATA");
            places.add((local != null ? Path.of(local) : home.resolve("AppData").resolve("Local")).resolve("Programs").resolve("Beyond-All-Reason").resolve("data"));
            String programs = System.getenv("ProgramFiles");
            places.add(Path.of(programs != null ? programs : "C:\\Program Files", "Beyond-All-Reason", "data"));
        } else if (MAC) {
            places.add(home.resolve("Library").resolve("Application Support").resolve("Beyond-All-Reason-mac"));
            places.add(home.resolve("Library").resolve("Application Support").resolve("Beyond-All-Reason"));
        } else {
            // Il nome della cartella cambia tra le versioni del gioco (con spazi o con trattini), e il pacchetto
            // Flatpak tiene i dati nella sua cartella privata.
            Path flatpak = home.resolve(".var").resolve("app").resolve("info.beyondallreason.bar");
            for (Path base : linuxBases(home)) {
                for (String name : FOLDER_NAMES) {
                    places.add(base.resolve(name));
                    places.add(base.resolve(name).resolve("data"));
                }
            }
            places.add(flatpak.resolve("data"));
        }
        return places;
    }

    /** Le cartelle di Linux dentro cui il gioco crea la propria, dalla piu' probabile. */
    private static List<Path> linuxBases(Path home) {
        List<Path> bases = new ArrayList<>();
        Path flatpak = home.resolve(".var").resolve("app").resolve("info.beyondallreason.bar");
        for (String[] variable : new String[][]{{"XDG_STATE_HOME", ".local/state"}, {"XDG_DATA_HOME", ".local/share"}, {"XDG_CONFIG_HOME", ".config"}}) {
            String value = System.getenv(variable[0]);
            if (value != null && !value.isBlank()) {
                bases.add(Path.of(value));
            }
            bases.add(home.resolve(variable[1]));
            bases.add(flatpak.resolve(variable[1]));
        }
        bases.add(flatpak.resolve("data"));
        bases.add(home.resolve("Documents"));
        bases.add(home.resolve("Games"));
        bases.add(home);
        return bases;
    }

    /** La cartella da cui far partire la scelta a mano: su Linux quella, nascosta, in cui il gioco tiene i dati. */
    static Path browseStart() {
        Path home = Path.of(System.getProperty("user.home"));
        if (WINDOWS || MAC) {
            return null;
        }
        for (Path base : linuxBases(home)) {
            for (String name : FOLDER_NAMES) {
                if (Files.isDirectory(base.resolve(name))) {
                    return base.resolve(name);
                }
            }
        }
        Path state = home.resolve(".local").resolve("state");
        return Files.isDirectory(state) ? state : home;
    }

    /**
     * Apre una cartella nel gestore dei file. Su Linux si usa xdg-open: la via di Java funziona solo con alcuni
     * ambienti grafici.
     */
    static boolean openFolder(Path folder) {
        if (folder == null || !Files.isDirectory(folder)) {
            return false;
        }
        if (!WINDOWS) {
            try {
                new ProcessBuilder(MAC ? "open" : "xdg-open", folder.toString())
                    .redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
                return true;
            } catch (Exception ignored) {
            }
        }
        try {
            java.awt.Desktop.getDesktop().open(folder.toFile());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * La cartella "data" abituale: la variabile BAR_DATA se impostata, altrimenti la prima posizione nota che
     * esiste, altrimenti la piu' probabile (che puo' non esistere se BAR non e' installato).
     */
    static Path defaultData() {
        String override = System.getenv("BAR_DATA");
        if (override != null && !override.isBlank()) {
            return Path.of(override.trim());
        }
        List<Path> places = usualPlaces();
        for (Path place : places) {
            if (isData(place)) {
                return place;
            }
        }
        for (Path place : places) {
            if (Files.isDirectory(place)) {
                return place;
            }
        }
        return places.get(0);
    }

    /**
     * Cerca BAR: prima la cartella indicata (quella usata l'ultima volta), poi la variabile BAR_DATA e le
     * posizioni abituali, infine il registro di Windows e le cartelle tipiche di ogni disco.
     *
     * @return la cartella "data", o null se BAR non si trova
     */
    static Path find(String hint) {
        Set<Path> quick = new LinkedHashSet<>();
        add(quick, hint);
        add(quick, System.getenv("BAR_DATA"));
        quick.addAll(usualPlaces());
        for (Path candidate : quick) {
            if (isData(candidate)) {
                return candidate;
            }
        }
        Set<Path> wide = new LinkedHashSet<>(quick);
        wide.addAll(fromRegistry());
        List<Path> bases = new ArrayList<>();
        for (String variable : new String[]{"ProgramFiles", "ProgramFiles(x86)", "ProgramW6432"}) {
            String value = System.getenv(variable);
            if (value != null && !value.isBlank()) {
                bases.add(Path.of(value));
            }
        }
        Path home = Path.of(System.getProperty("user.home"));
        bases.add(home);
        bases.add(home.resolve("Games"));
        bases.add(home.resolve("Documents").resolve("My Games"));
        if (!WINDOWS && !MAC) {
            bases.addAll(linuxBases(home));
        }
        for (File root : File.listRoots()) {
            bases.add(root.toPath());
            for (String folder : new String[]{"Games", "Program Files", "Program Files (x86)", "SteamLibrary", "Programs"}) {
                bases.add(root.toPath().resolve(folder));
            }
        }
        for (Path base : bases) {
            for (String name : FOLDER_NAMES) {
                wide.add(base.resolve(name));
            }
        }
        for (Path candidate : wide) {
            Path data = fromChoice(candidate);
            if (data != null) {
                return data;
            }
        }
        return null;
    }

    /**
     * Interpreta una cartella scelta dall'utente: va bene la "data", la cartella di installazione che la
     * contiene, una sua sottocartella, oppure una cartella che contiene l'installazione.
     */
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
        for (String name : FOLDER_NAMES) {
            Path inside = folder.resolve(name).resolve("data");
            if (isData(inside)) {
                return inside;
            }
        }
        return null;
    }

    private static void add(Set<Path> paths, String text) {
        if (text != null && !text.isBlank()) {
            try {
                paths.add(Path.of(text.trim()));
            } catch (RuntimeException ignored) {
            }
        }
    }

    /** Le cartelle in cui l'installer di BAR dice a Windows di essersi installato. */
    private static List<Path> fromRegistry() {
        List<Path> found = new ArrayList<>();
        if (!WINDOWS) {
            return found;
        }
        String[] roots = {
            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
            "HKLM\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
            "HKLM\\Software\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
        };
        for (String root : roots) {
            // prima le chiavi dei programmi il cui nome contiene "Beyond", poi i valori di ciascuna
            for (String key : run("reg", "query", root, "/s", "/f", "Beyond", "/d")) {
                if (!key.startsWith("HKEY_")) {
                    continue;
                }
                for (String line : run("reg", "query", key.trim())) {
                    String[] parts = line.trim().split("\\s{2,}", 3);
                    if (parts.length == 3 && parts[0].matches("InstallLocation|DisplayIcon|UninstallString")) {
                        String value = parts[2].replace("\"", "").replaceAll(",\\d+$", "").trim();
                        try {
                            Path path = Path.of(value);
                            Path folder = parts[0].equals("InstallLocation") ? path : path.getParent();
                            if (folder != null) {
                                found.add(folder);
                            }
                        } catch (RuntimeException ignored) {
                        }
                    }
                }
            }
        }
        return found;
    }

    private static List<String> run(String... command) {
        List<String> lines = new ArrayList<>();
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    lines.add(line);
                }
            }
            process.waitFor(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception ignored) {
        }
        return lines;
    }

    // ------------------------------------------------------------------ 7-Zip

    /**
     * Il programma 7-Zip con cui aprire le mappe ufficiali (.sd7): quello che BAR porta con se' su Windows e
     * Linux, oppure uno installato nel sistema. Null se non c'e'.
     */
    static Path sevenZip(Path data) {
        if (WINDOWS) {
            Path install = data == null ? null : data.toAbsolutePath().getParent();
            Path bundled = install == null ? null : install.resolve("resources").resolve("app.asar.unpacked")
                .resolve("node_modules").resolve("7zip-bin").resolve("win").resolve("x64").resolve("7za.exe");
            return bundled != null && Files.isRegularFile(bundled) ? bundled : null;
        }
        List<String> folders = new ArrayList<>(List.of(String.valueOf(System.getenv("PATH")).split(File.pathSeparator)));
        folders.add("/opt/homebrew/bin");
        folders.add("/usr/local/bin");
        for (String folder : folders) {
            for (String name : new String[]{"7z", "7za", "7zr", "7zz"}) {
                Path candidate = Path.of(folder.isEmpty() ? "." : folder, name);
                if (Files.isRegularFile(candidate) && Files.isExecutable(candidate)) {
                    return candidate;
                }
            }
        }
        for (String root : new String[]{"/app/main/resources", "/opt/Beyond-All-Reason/resources", "/usr/lib/beyond-all-reason/resources"}) {
            Path bundled = Path.of(root, "app.asar.unpacked", "node_modules", "7zip-bin", "linux", "x64", "7za");
            if (Files.isRegularFile(bundled)) {
                return bundled;
            }
        }
        return null;
    }
}

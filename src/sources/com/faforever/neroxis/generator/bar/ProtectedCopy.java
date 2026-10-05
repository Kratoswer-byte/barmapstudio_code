package com.faforever.neroxis.generator.bar;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Copia di file in una cartella in cui l'utente non puo' scrivere (BAR installato in una cartella protetta).
 * Windows chiede una sola volta il permesso di amministratore, e solo per la copia: il programma resta
 * avviato come utente normale.
 */
final class ProtectedCopy {
    private ProtectedCopy() {}

    /** Cartella dell'utente in cui preparare i file prima di copiarli. */
    static Path stagingFolder() {
        return AppPaths.userFolder("exports");
    }

    /**
     * Copia ogni file (chiave) nella sua destinazione (valore).
     *
     * @param elevated true = con richiesta di amministratore, false = come utente normale (usato nelle prove)
     * @return true se alla fine tutte le destinazioni esistono con la stessa dimensione dell'origine
     */
    static boolean copy(Map<Path, Path> files, boolean elevated) throws IOException, InterruptedException {
        if (files.isEmpty()) {
            return true;
        }
        if (!Platform.isWindows()) {
            return false;
        }
        StringBuilder cmd = new StringBuilder("@echo off\r\nchcp 65001>nul\r\n");
        for (Map.Entry<Path, Path> file : files.entrySet()) {
            cmd.append("copy /y /b \"").append(batchPath(file.getKey())).append("\" \"")
                .append(batchPath(file.getValue())).append("\">nul\r\n");
        }
        Path script = stagingFolder().resolve("copy_to_bar.cmd");
        Files.write(script, cmd.toString().getBytes(StandardCharsets.UTF_8));
        try {
            String start = "Start-Process -FilePath '" + script.toString().replace("'", "''") + "' -Wait -WindowStyle Hidden"
                + (elevated ? " -Verb RunAs" : "");
            Process process = new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-Command", start)
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .start();
            // l'utente puo' metterci un po' a rispondere alla richiesta di Windows
            if (!process.waitFor(5, TimeUnit.MINUTES)) {
                process.destroyForcibly();
                return false;
            }
        } finally {
            Files.deleteIfExists(script);
        }
        for (Map.Entry<Path, Path> file : files.entrySet()) {
            if (!Files.isRegularFile(file.getValue()) || Files.size(file.getValue()) != Files.size(file.getKey())) {
                return false;
            }
        }
        return true;
    }

    /** Comodita' per due file. */
    static Map<Path, Path> pairs(Path from1, Path to1, Path from2, Path to2) {
        Map<Path, Path> files = new LinkedHashMap<>();
        files.put(from1, to1);
        if (from2 != null && Files.isRegularFile(from2)) {
            files.put(from2, to2);
        }
        return files;
    }

    /** In un file .cmd il segno di percentuale va raddoppiato, altrimenti viene letto come variabile. */
    private static String batchPath(Path path) {
        return path.toAbsolutePath().toString().replace("%", "%%");
    }
}

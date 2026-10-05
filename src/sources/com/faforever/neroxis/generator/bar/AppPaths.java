package com.faforever.neroxis.generator.bar;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Le cartelle del programma, uguali su qualsiasi PC e in qualsiasi posto venga installato.
 *
 * <ul>
 * <li><b>appDir</b>: dove sta il programma (sola lettura): oggetti in dotazione, icone, file della donazione.</li>
 * <li><b>userDir</b>: dove il programma scrive: impostazioni, cache, registri, materiali dell'utente.
 *     Di norma coincide con appDir; se il programma e' installato in una cartella protetta (Program Files),
 *     lo script di avvio la sposta nella cartella dati dell'utente e passa la posizione vera del programma
 *     con la proprieta' {@code barstudio.app}.</li>
 * <li><b>barData</b>: la cartella "data" di Beyond All Reason scelta dall'utente (o trovata in automatico).</li>
 * </ul>
 */
public final class AppPaths {
    private AppPaths() {}

    private static volatile Path barData;

    /** Cartella in cui il programma puo' scrivere (impostazioni, cache, registri). */
    public static Path userDir() {
        return Path.of(System.getProperty("neroxis.home", System.getProperty("user.dir"))).toAbsolutePath();
    }

    /** Cartella dei file distribuiti con il programma. */
    public static Path appDir() {
        String app = System.getProperty("barstudio.app");
        if (app != null && !app.isBlank()) {
            Path path = Path.of(app).toAbsolutePath();
            if (Files.isDirectory(path)) {
                return path;
            }
        }
        return userDir();
    }

    /** Sottocartella scrivibile dell'utente, creata se manca (es. "custom_assets/textures"). */
    public static Path userFolder(String relative) {
        Path folder = userDir().resolve(relative);
        try {
            Files.createDirectories(folder);
        } catch (Exception ignored) {
        }
        return folder;
    }

    /** Fissa la cartella dati di BAR scelta dall'utente: da quel momento la usano tutte le parti del programma. */
    public static void setBarData(Path data) {
        if (data != null) {
            barData = data;
        }
    }

    /**
     * Cartella "data" di BAR: quella scelta dall'utente se e' valida, altrimenti la variabile BAR_DATA,
     * le posizioni note e infine quella abituale del sistema. Puo' non esistere se BAR non e' installato.
     */
    public static Path barData() {
        Path chosen = barData;
        if (chosen != null && BarFolder.isData(chosen)) {
            return chosen;
        }
        try {
            Path found = BarFolder.find(chosen != null ? chosen.toString() : null);
            if (found != null) {
                barData = found;
                return found;
            }
        } catch (RuntimeException ignored) {
        }
        return chosen != null ? chosen : Platform.defaultData();
    }
}

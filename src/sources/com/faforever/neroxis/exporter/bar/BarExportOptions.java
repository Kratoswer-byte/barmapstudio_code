package com.faforever.neroxis.exporter.bar;

import java.nio.file.Path;

/**
 * Le scelte con cui una mappa viene esportata per BAR.
 *
 * @param squaresPerCell quante celle del terreno di BAR per ogni cella dell'editor (da 1 a 4)
 * @param heightScale quanto vengono moltiplicate le quote dell'editor
 * @param baseMetal metallo di un punto normale
 * @param minMetal metallo del punto piu' povero
 * @param maxMetal metallo del punto piu' ricco
 * @param uniformMetal vero per dare a tutti i punti lo stesso valore
 * @param numTeams numero di squadre (0 = ricavato dai punti di partenza)
 * @param mapName nome della mappa, o null per quello generato
 * @param mapVersion versione scritta nella mappa
 * @param author autore scritto nella mappa
 * @param reclaim quanto materiale da recuperare lasciare (1 = normale, 0 = nessuno)
 * @param featuresRepository cartella con la raccolta di alberi di BAR, o null
 * @param minWind vento minimo
 * @param maxWind vento massimo
 * @param tidal forza della marea
 */
public record BarExportOptions(int squaresPerCell, float heightScale, float baseMetal, float minMetal, float maxMetal,
                               boolean uniformMetal, int numTeams, String mapName, String mapVersion, String author,
                               float reclaim, Path featuresRepository, int minWind, int maxWind, int tidal) {

    public static final BarExportOptions DEFAULT =
        new BarExportOptions(2, 0.7f, 2.0f, 2.0f, 3.5f, false, 0, null, "1.0", "Neroxis Map Generator", 1.0f, null, 3, 16, 18);

    public BarExportOptions {
        require(squaresPerCell >= 1 && squaresPerCell <= 4, "squaresPerCell must be between 1 and 4");
        require(reclaim >= 0.0f, "reclaim must not be negative");
        require(heightScale > 0.0f, "heightScale must be positive");
        require(minMetal >= 0.1f && maxMetal >= minMetal && maxMetal <= 10.0f, "the metal range must be ordered and between 0.1 and 10");
        require(mapName == null || mapName.matches("[A-Za-z0-9][A-Za-z0-9 _.-]{1,40}"), "the map name may only contain letters, digits, spaces and _ . -");
        require(baseMetal >= minMetal && baseMetal <= maxMetal, "the base metal income must be inside the metal range");
        require(minWind >= 0 && maxWind >= minWind && maxWind <= 50, "the wind range must be ordered and between 0 and 50");
        require(tidal >= 0 && tidal <= 100, "the tidal strength must be between 0 and 100");
    }

    /** Con vento e marea abituali. */
    public BarExportOptions(int squaresPerCell, float heightScale, float baseMetal, float minMetal, float maxMetal,
                            boolean uniformMetal, int numTeams, String mapName, String mapVersion, String author,
                            float reclaim, Path featuresRepository) {
        this(squaresPerCell, heightScale, baseMetal, minMetal, maxMetal, uniformMetal, numTeams, mapName, mapVersion, author,
            reclaim, featuresRepository, 3, 16, 18);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}

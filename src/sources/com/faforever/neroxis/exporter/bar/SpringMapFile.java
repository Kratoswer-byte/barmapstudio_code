package com.faforever.neroxis.exporter.bar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Scrive i due file che descrivono una mappa per il motore: il .smf (quote, metallo, anteprima, oggetti,
 * erba) e l'intestazione del .smt (le piastrelle della texture del terreno).
 *
 * <p>Struttura del .smf: un'intestazione di 80 byte con le dimensioni e la posizione di ogni sezione, una
 * piccola tabella delle sezioni aggiuntive (qui solo l'erba), poi le sezioni una dopo l'altra. Tutti i
 * numeri sono scritti con il byte meno significativo per primo.</p>
 */
final class SpringMapFile {
    /** Lato di una cella del terreno in elmi, e pixel di texture per cella. */
    static final int SQUARE_SIZE = 8;
    static final int TEXELS_PER_SQUARE = 8;
    /** Lato di una piastrella della texture in pixel e suoi byte una volta compressa con i livelli di dettaglio. */
    static final int TILE_SIZE = 32;
    static final int TILE_BYTES = 680;
    /** L'anteprima e' sempre 1024x1024 con 9 livelli di dettaglio, compressa DXT1. */
    static final int MINIMAP_SIZE = 1024;
    static final int MINIMAP_MIP_LEVELS = 9;
    static final int MINIMAP_BYTES = 699048;

    private static final int HEADER_BYTES = 80;
    private static final int GRASS_ENTRY_BYTES = 12;
    private static final int FEATURE_BYTES = 24;

    /** Un oggetto piazzato sulla mappa: tipo (posizione nell'elenco dei nomi), punto, rotazione e scala. */
    record Feature(int type, float x, float y, float z, float rotation, float relativeSize) {}

    private SpringMapFile() {}

    static void write(OutputStream out, int mapId, int mapX, int mapY, float minHeight, float maxHeight,
                      short[] heightmap, byte[] metalMap, byte[] grassMap, byte[] minimap, String tileFileName,
                      List<String> featureTypes, List<Feature> features) throws IOException {
        int halfCells = (mapX / 2) * (mapY / 2);
        int tiles = (mapX / 4) * (mapY / 4);
        if (heightmap.length != (mapX + 1) * (mapY + 1) || metalMap.length != halfCells
            || grassMap.length != tiles || minimap.length != MINIMAP_BYTES) {
            throw new IllegalArgumentException("Map data does not match the map dimensions");
        }
        byte[] tileName = tileFileName.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream typeNames = new ByteArrayOutputStream();
        for (String type : featureTypes) {
            typeNames.write(type.getBytes(StandardCharsets.UTF_8));
            typeNames.write(0);
        }

        // posizione di ogni sezione nel file
        int heightsAt = HEADER_BYTES + GRASS_ENTRY_BYTES;
        int typeMapAt = heightsAt + heightmap.length * 2;
        int minimapAt = typeMapAt + halfCells;
        int metalAt = minimapAt + MINIMAP_BYTES;
        int tilesAt = metalAt + halfCells;
        int tileSectionBytes = 12 + tileName.length + 1 + tiles * 4;
        int featuresAt = tilesAt + tileSectionBytes;
        int featureSectionBytes = 8 + typeNames.size() + features.size() * FEATURE_BYTES;
        int grassAt = featuresAt + featureSectionBytes;

        ByteBuffer file = ByteBuffer.allocate(grassAt + tiles).order(ByteOrder.LITTLE_ENDIAN);
        file.put("spring map file\0".getBytes(StandardCharsets.US_ASCII));
        file.putInt(1);                    // versione del formato
        file.putInt(mapId);
        file.putInt(mapX);
        file.putInt(mapY);
        file.putInt(SQUARE_SIZE);
        file.putInt(TEXELS_PER_SQUARE);
        file.putInt(TILE_SIZE);
        file.putFloat(minHeight);
        file.putFloat(maxHeight);
        file.putInt(heightsAt);
        file.putInt(typeMapAt);
        file.putInt(tilesAt);
        file.putInt(minimapAt);
        file.putInt(metalAt);
        file.putInt(featuresAt);
        file.putInt(1);                    // una sezione aggiuntiva...
        file.putInt(GRASS_ENTRY_BYTES);    // ...descritta in 12 byte...
        file.putInt(1);                    // ...di tipo "erba"...
        file.putInt(grassAt);              // ...che si trova qui

        for (short height : heightmap) {
            file.putShort(height);
        }
        // la mappa dei tipi di terreno resta a zero: un solo tipo
        file.position(minimapAt);
        file.put(minimap);
        file.put(metalMap);

        file.putInt(1);                    // un solo file di piastrelle
        file.putInt(tiles);
        file.putInt(tiles);
        file.put(tileName);
        file.put((byte) 0);
        for (int tile = 0; tile < tiles; tile++) {
            file.putInt(tile);             // ogni riquadro della mappa usa la sua piastrella
        }

        file.putInt(featureTypes.size());
        file.putInt(features.size());
        file.put(typeNames.toByteArray());
        for (Feature feature : features) {
            file.putInt(feature.type());
            file.putFloat(feature.x());
            file.putFloat(feature.y());
            file.putFloat(feature.z());
            file.putFloat(feature.rotation());
            file.putFloat(feature.relativeSize());
        }
        file.put(grassMap);
        out.write(file.array());
    }

    /** L'intestazione del file .smt, a cui seguono le piastrelle. */
    static void writeTileFileHeader(OutputStream out, int tileCount) throws IOException {
        ByteBuffer header = ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN);
        header.put("spring tilefile\0".getBytes(StandardCharsets.US_ASCII));
        header.putInt(1);                  // versione del formato
        header.putInt(tileCount);
        header.putInt(TILE_SIZE);
        header.putInt(1);                  // compressione DXT1
        out.write(header.array());
    }
}

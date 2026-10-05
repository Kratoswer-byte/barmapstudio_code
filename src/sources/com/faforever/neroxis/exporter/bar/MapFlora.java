package com.faforever.neroxis.exporter.bar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * La vegetazione adatta a ogni bioma, scelta tra gli insiemi di alberi e piante che il gruppo di BAR mette
 * a disposizione per le mappe (la raccolta "mapfeatures"). Se la raccolta non e' disponibile sul computer
 * le mappe escono senza quegli alberi; fanno eccezione i cristalli, che sono gia' nel gioco.
 */
final class MapFlora {
    /** Tre taglie comuni e una rara: piccola, media (due volte), grande (due volte), molto grande. */
    private static final String[] SIZES = {"s", "m", "m", "l", "l", "xl"};

    /**
     * Un insieme di piante.
     *
     * @param definition file della raccolta che le descrive (vuoto se sono gia' nel gioco)
     * @param modelPrefix inizio del nome dei modelli 3D da copiare nella mappa
     * @param names i nomi delle varianti tra cui scegliere
     * @param perGroup quante piante mettere dove l'editor indica un gruppo
     * @param keep quota dei punti dell'editor che ricevono davvero una pianta (da 0 a 1)
     * @param fromRepository vero se i file vanno presi dalla raccolta
     */
    record PlantSet(String definition, String modelPrefix, List<String> names, int perGroup, float keep, boolean fromRepository) {}

    private MapFlora() {}

    static List<PlantSet> forBiome(String biomeName, Path repository) {
        String biome = biomeName.toLowerCase(Locale.ROOT);
        if (biome.equals("crystalline")) {
            return List.of(new PlantSet("", "", crystals("violet", "blue", "quartz"), 1, 0.3f, false));
        }
        boolean available = repository != null && Files.isDirectory(repository.resolve("features"))
            && Files.isDirectory(repository.resolve("objects3d"));
        if (!available) {
            return List.of();
        }
        return switch (biome) {
            case "frithen" -> List.of(pines(4, 1.0f, "snow", "snow", "snowgreen"));
            case "earlyautumn" -> List.of(pines(3, 1.0f, "brown", "brown", "green"), birches(3, 1.0f));
            case "windingriver", "wonder" -> List.of(birches(3, 1.0f), pines(3, 1.0f, "green"));
            case "sunset" -> List.of(pines(4, 1.0f, "green", "green", "brown"));
            case "stones" -> List.of(aleppoPines(3, 0.9f, "dark", "med", "light"));
            case "prayer" -> List.of(palms(3, 1.0f), new PlantSet("kapoktree.lua", "kapok", numbered("kapok", 4), 1, 0.5f, true));
            case "desert" -> List.of(
                new PlantSet("pedros.lua", "pedro", numbered("pedro", 6), 2, 0.6f, true),
                new PlantSet("peyotes.lua", "peyote", numbered("peyote", 4), 3, 0.6f, true),
                palms(1, 0.25f));
            case "mars", "syrtis", "moonlight", "brimstone" -> List.of(aleppoPines(1, 0.45f, "dead"));
            default -> List.of();
        };
    }

    private static PlantSet pines(int perGroup, float keep, String... colours) {
        List<String> names = new ArrayList<>();
        for (String colour : colours) {
            for (String shape : new String[]{"a", "b", "c"}) {
                for (String size : SIZES) {
                    names.add("allpinesb_ad0_" + colour + "_" + shape + "_" + size);
                }
            }
        }
        return new PlantSet("allpinesb_ad0.lua", "allpinesb_ad0", names, perGroup, keep, true);
    }

    private static PlantSet birches(int perGroup, float keep) {
        List<String> names = new ArrayList<>();
        for (int tree = 1; tree <= 8; tree++) {
            for (int variant = 1; variant <= 4; variant++) {
                names.add("euro_birch_tree_0" + tree + "_" + variant);
            }
        }
        return new PlantSet("birchtree.lua", "euro_birch", names, perGroup, keep, true);
    }

    private static PlantSet aleppoPines(int perGroup, float keep, String... colours) {
        List<String> names = new ArrayList<>();
        for (String colour : colours) {
            for (String density : new String[]{"sparse", "dense", "double"}) {
                for (String size : SIZES) {
                    names.add("ad0_aleppo2_" + density + "_" + colour + "_" + size);
                }
            }
        }
        return new PlantSet("ad0_aleppo2.lua", "ad0_aleppo2", names, perGroup, keep, true);
    }

    private static PlantSet palms(int perGroup, float keep) {
        List<String> names = new ArrayList<>();
        for (int palm = 1; palm <= 7; palm++) {
            // la versione normale due volte, quella grande una: cosi' le palme grandi sono una su tre
            names.add("ad0_senegal_" + palm);
            names.add("ad0_senegal_" + palm);
            names.add("ad0_senegal_" + palm + "_large");
        }
        return new PlantSet("ad0_senegal.lua", "ad0_senegal", names, perGroup, keep, true);
    }

    private static List<String> numbered(String prefix, int count) {
        List<String> names = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            names.add(prefix + i);
        }
        return names;
    }

    private static List<String> crystals(String... colours) {
        List<String> names = new ArrayList<>();
        for (String colour : colours) {
            for (int i = 1; i <= 3; i++) {
                names.add("pilha_crystal_" + colour + i);
            }
        }
        return names;
    }

    /**
     * I file della raccolta da copiare dentro la mappa per gli insiemi scelti: la definizione, i modelli e le
     * loro texture. Chiave: percorso dentro l'archivio della mappa; valore: file sul disco.
     */
    static TreeMap<String, Path> collectFiles(List<PlantSet> sets, Path repository) throws IOException {
        TreeMap<String, Path> files = new TreeMap<>();
        TreeMap<String, Path> textures = new TreeMap<>();
        try (Stream<Path> list = Files.list(repository.resolve("unittextures"))) {
            list.forEach(path -> textures.put(path.getFileName().toString().toLowerCase(Locale.ROOT), path));
        }
        Pattern textureReference = Pattern.compile("unittextures/([A-Za-z0-9_.()\\- ]+)");
        for (PlantSet set : sets) {
            if (!set.fromRepository()) {
                continue;
            }
            Path definition = repository.resolve("features").resolve(set.definition());
            files.put("features/" + set.definition(), definition);
            List<String> wanted = new ArrayList<>();
            Matcher matcher = textureReference.matcher(Files.readString(definition, StandardCharsets.ISO_8859_1));
            while (matcher.find()) {
                wanted.add(matcher.group(1));
            }
            List<Path> models;
            try (Stream<Path> list = Files.list(repository.resolve("objects3d"))) {
                models = list.sorted().toList();
            }
            for (Path model : models) {
                String name = model.getFileName().toString();
                String lower = name.toLowerCase(Locale.ROOT);
                if (lower.startsWith(set.modelPrefix()) && lower.endsWith(".s3o")) {
                    files.put("objects3d/" + name, model);
                    wanted.addAll(modelTextures(model));
                }
            }
            for (String textureName : wanted) {
                Path texture = textures.get(textureName.toLowerCase(Locale.ROOT));
                if (texture != null) {
                    files.put("unittextures/" + texture.getFileName(), texture);
                }
            }
        }
        return files;
    }

    /** I nomi delle due texture che un modello .s3o dichiara nella sua intestazione. */
    private static List<String> modelTextures(Path model) throws IOException {
        byte[] bytes = Files.readAllBytes(model);
        ByteBuffer header = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        List<String> names = new ArrayList<>();
        for (int slot = 0; slot < 2; slot++) {
            int start = header.getInt(44 + slot * 4);
            if (start > 0 && start < bytes.length) {
                int end = start;
                while (end < bytes.length && bytes[end] != 0) {
                    end++;
                }
                names.add(new String(bytes, start, end - start, StandardCharsets.ISO_8859_1));
            }
        }
        return names;
    }
}

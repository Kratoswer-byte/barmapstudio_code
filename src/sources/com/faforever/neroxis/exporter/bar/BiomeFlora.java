package com.faforever.neroxis.exporter.bar;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/bar/BiomeFlora.class */
final class BiomeFlora {
    static final String MAPFEATURES_COMMIT = "3b791639210dcc4f89875fce712bde8f460d38e1";

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet.class */
    static final class PlantSet extends Record {
        private final String definition;
        private final String modelPrefix;
        private final List<String> names;
        private final int perGroup;
        private final float keep;
        private final boolean fromRepository;

        PlantSet(String definition, String modelPrefix, List<String> names, int perGroup, float keep, boolean fromRepository) {
            this.definition = definition;
            this.modelPrefix = modelPrefix;
            this.names = names;
            this.perGroup = perGroup;
            this.keep = keep;
            this.fromRepository = fromRepository;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, PlantSet.class), PlantSet.class, "definition;modelPrefix;names;perGroup;keep;fromRepository", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->definition:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->modelPrefix:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->names:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->perGroup:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->keep:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->fromRepository:Z").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, PlantSet.class), PlantSet.class, "definition;modelPrefix;names;perGroup;keep;fromRepository", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->definition:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->modelPrefix:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->names:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->perGroup:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->keep:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->fromRepository:Z").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, PlantSet.class, Object.class), PlantSet.class, "definition;modelPrefix;names;perGroup;keep;fromRepository", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->definition:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->modelPrefix:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->names:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->perGroup:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->keep:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BiomeFlora$PlantSet;->fromRepository:Z").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String definition() {
            return this.definition;
        }

        public String modelPrefix() {
            return this.modelPrefix;
        }

        public List<String> names() {
            return this.names;
        }

        public int perGroup() {
            return this.perGroup;
        }

        public float keep() {
            return this.keep;
        }

        public boolean fromRepository() {
            return this.fromRepository;
        }
    }

    private BiomeFlora() {
    }

    static List<PlantSet> forBiome(String biomeName, Path featuresRepository) {
        boolean available = featuresRepository != null && Files.isDirectory(featuresRepository.resolve("features"), new LinkOption[0]) && Files.isDirectory(featuresRepository.resolve("objects3d"), new LinkOption[0]);
        String biome = biomeName.toLowerCase(Locale.ROOT);
        if (biome.equals("crystalline")) {
            return List.of(new PlantSet("", "", crystals("violet", "blue", "quartz"), 1, 0.3f, false));
        }
        if (!available) {
            return List.of();
        }
        switch (biome) {
            case "frithen":
                return List.of(pines(4, 1.0f, "snow", "snow", "snowgreen"));
            case "earlyautumn":
                return List.of(pines(3, 1.0f, "brown", "brown", "green"), birches(3, 1.0f));
            case "windingriver":
            case "wonder":
                return List.of(birches(3, 1.0f), pines(3, 1.0f, "green"));
            case "sunset":
                return List.of(pines(4, 1.0f, "green", "green", "brown"));
            case "stones":
                return List.of(aleppo(3, 0.9f, "dark", "med", "light"));
            case "prayer":
                return List.of(palms(3, 1.0f), new PlantSet("kapoktree.lua", "kapok", numbered("kapok", 4), 1, 0.5f, true));
            case "desert":
                return List.of(new PlantSet("pedros.lua", "pedro", numbered("pedro", 6), 2, 0.6f, true), new PlantSet("peyotes.lua", "peyote", numbered("peyote", 4), 3, 0.6f, true), palms(1, 0.25f));
            case "mars":
            case "syrtis":
            case "moonlight":
            case "brimstone":
                return List.of(aleppo(1, 0.45f, "dead"));
            default:
                return List.of();
        }
    }

    private static PlantSet pines(int perGroup, float keep, String... colors) {
        List<String> names = new ArrayList<>();
        for (String color : colors) {
            for (String shape : new String[]{"a", "b", "c"}) {
                for (String size : new String[]{"s", "m", "m", "l", "l", "xl"}) {
                    names.add("allpinesb_ad0_%s_%s_%s".formatted(color, shape, size));
                }
            }
        }
        return new PlantSet("allpinesb_ad0.lua", "allpinesb_ad0", names, perGroup, keep, true);
    }

    private static PlantSet birches(int perGroup, float keep) {
        List<String> names = new ArrayList<>();
        for (int tree = 1; tree <= 8; tree++) {
            for (int variant = 1; variant <= 4; variant++) {
                names.add("euro_birch_tree_0%d_%d".formatted(Integer.valueOf(tree), Integer.valueOf(variant)));
            }
        }
        return new PlantSet("birchtree.lua", "euro_birch", names, perGroup, keep, true);
    }

    private static PlantSet aleppo(int perGroup, float keep, String... colors) {
        List<String> names = new ArrayList<>();
        for (String color : colors) {
            for (String density : new String[]{"sparse", "dense", "double"}) {
                for (String size : new String[]{"s", "m", "m", "l", "l", "xl"}) {
                    names.add("ad0_aleppo2_%s_%s_%s".formatted(density, color, size));
                }
            }
        }
        return new PlantSet("ad0_aleppo2.lua", "ad0_aleppo2", names, perGroup, keep, true);
    }

    private static PlantSet palms(int perGroup, float keep) {
        List<String> names = new ArrayList<>();
        for (int palm = 1; palm <= 7; palm++) {
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

    private static List<String> crystals(String... colors) {
        List<String> names = new ArrayList<>();
        for (String color : colors) {
            for (int i = 1; i <= 3; i++) {
                names.add("pilha_crystal_%s%d".formatted(color, Integer.valueOf(i)));
            }
        }
        return names;
    }

    static TreeMap<String, Path> collectFiles(List<PlantSet> sets, Path featuresRepository) throws IOException {
        TreeMap<String, Path> files = new TreeMap<>();
        Path textureFolder = featuresRepository.resolve("unittextures");
        TreeMap<String, Path> textures = new TreeMap<>();
        Stream<Path> stream = Files.list(textureFolder);
        try {
            stream.forEach(path -> {
                textures.put(path.getFileName().toString().toLowerCase(Locale.ROOT), path);
            });
            if (stream != null) {
                stream.close();
            }
            for (PlantSet set : sets) {
                if (set.fromRepository()) {
                    Path definition = featuresRepository.resolve("features").resolve(set.definition());
                    files.put("features/" + set.definition(), definition);
                    List<String> textureNames = new ArrayList<>();
                    Matcher matcher = Pattern.compile("unittextures/([A-Za-z0-9_.()\\- ]+)").matcher(Files.readString(definition, StandardCharsets.ISO_8859_1));
                    while (matcher.find()) {
                        textureNames.add(matcher.group(1));
                    }
                    Stream<Path> stream2 = Files.list(featuresRepository.resolve("objects3d"));
                    try {
                        for (Path model : stream2.sorted().toList()) {
                            String name = model.getFileName().toString();
                            if (name.toLowerCase(Locale.ROOT).startsWith(set.modelPrefix()) && name.toLowerCase(Locale.ROOT).endsWith(".s3o")) {
                                files.put("objects3d/" + name, model);
                                textureNames.addAll(readModelTextures(model));
                            }
                        }
                        if (stream2 != null) {
                            stream2.close();
                        }
                        for (String textureName : textureNames) {
                            Path texture = textures.get(textureName.toLowerCase(Locale.ROOT));
                            if (texture != null) {
                                files.put("unittextures/" + String.valueOf(texture.getFileName()), texture);
                            }
                        }
                    } catch (Throwable th) {
                        if (stream2 != null) {
                            try {
                                stream2.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                }
            }
            return files;
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
    }

    private static List<String> readModelTextures(Path model) throws IOException {
        byte[] bytes = Files.readAllBytes(model);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        List<String> names = new ArrayList<>();
        for (int field = 0; field < 2; field++) {
            int offset = buffer.getInt(44 + (field * 4));
            if (offset > 0 && offset < bytes.length) {
                int end = offset;
                while (end < bytes.length && bytes[end] != 0) {
                    end++;
                }
                names.add(new String(bytes, offset, end - offset, StandardCharsets.ISO_8859_1));
            }
        }
        return names;
    }
}

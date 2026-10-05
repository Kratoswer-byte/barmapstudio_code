package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.biomes.Biome;
import com.faforever.neroxis.biomes.BiomeName;
import com.faforever.neroxis.biomes.Biomes;
import com.faforever.neroxis.generator.GeneratorParameters;
import com.faforever.neroxis.generator.terrain.SpawnFirstTerrainGenerator;
import com.faforever.neroxis.generator.terrain.TerrainGenerator;
import com.faforever.neroxis.generator.texture.*;
import com.faforever.neroxis.generator.util.serial.MapStyle;
import com.faforever.neroxis.generator.util.serial.PropStyle;
import com.faforever.neroxis.generator.util.serial.ResourceStyle;
import com.faforever.neroxis.generator.util.serial.TerrainStyle;
import com.faforever.neroxis.generator.util.serial.Visibility;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.placement.SpawnPlacer;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.mask.FloatMask;
import com.faforever.neroxis.util.Pipeline;
import com.faforever.neroxis.util.serial.biome.WaterSettings;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.faforever.neroxis.exporter.PreviewGenerator;
import com.faforever.neroxis.map.Spawn;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector4;
import com.faforever.neroxis.util.ImageUtil;

public final class GroundGenerator {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(
        Math.max(2, Runtime.getRuntime().availableProcessors()),
        r -> {
            Thread t = new Thread(r, "ground-gen-pool");
            t.setDaemon(true);
            return t;
        }
    );

    public record GroundResult(
        SCMap map,
        BufferedImage preview,
        SymmetrySettings symmetrySettings,
        GeneratorParameters parameters,
        long seed,
        BufferedImage slopeOverlay
    ) {
        public GroundResult(SCMap map, BufferedImage preview, SymmetrySettings symmetrySettings, GeneratorParameters parameters, long seed) {
            this(map, preview, symmetrySettings, parameters, seed, null);
        }
    }

    public static GroundResult generateGroundOnly(
        int mapSize,
        TerrainStyle terrainStyle,
        String biomeNameStr,
        Symmetry symmetry,
        int spawnCount,
        int numTeams,
        long seed
    ) throws Exception {
        return generateGroundOnly(mapSize, mapSize, terrainStyle, biomeNameStr, symmetry, spawnCount, numTeams, seed);
    }

    public static GroundResult generateGroundOnly(
        int width,
        int height,
        TerrainStyle terrainStyle,
        String biomeNameStr,
        Symmetry symmetry,
        int spawnCount,
        int numTeams,
        long seed
    ) throws Exception {
        int maxDim = Math.max(width, height);
        SplittableRandom random = new SplittableRandom(seed);
        // biomi ricavati dalle mappe BAR: forma del terreno dal bioma Neroxis di base, colori/luce/acqua dal bioma BAR
        com.faforever.neroxis.exporter.bar.BarBiomes.BarBiome barBiome = com.faforever.neroxis.exporter.bar.BarBiomes.find(biomeNameStr);
        if (barBiome != null) {
            biomeNameStr = barBiome.base;
        }
        BiomeName biomeName;
        try {
            biomeName = BiomeName.valueOf(biomeNameStr.toUpperCase());
        } catch (Exception e) {
            biomeName = BiomeName.DESERT;
        }

        Symmetry effectiveSymmetry = symmetry;
        if (effectiveSymmetry == null) {
            effectiveSymmetry = (height > width) ? Symmetry.Z : Symmetry.X;
        } else if (width != height) {
            if (effectiveSymmetry == Symmetry.DIAG) {
                effectiveSymmetry = Symmetry.POINT2;
            } else if (effectiveSymmetry != Symmetry.Z && effectiveSymmetry != Symmetry.X && effectiveSymmetry != Symmetry.POINT2 && effectiveSymmetry != Symmetry.QUAD && effectiveSymmetry != Symmetry.NONE) {
                effectiveSymmetry = (height > width) ? Symmetry.Z : Symmetry.X;
            }
        }

        SymmetrySettings symmetrySettings = new SymmetrySettings(effectiveSymmetry);
        GeneratorParameters parameters = new GeneratorParameters(
            spawnCount > 0 ? spawnCount : 2,
            maxDim,
            numTeams > 0 ? numTeams : 2,
            new MapStyle.Custom(terrainStyle, biomeName, PropStyle.BASIC, ResourceStyle.BASIC, 1.0f, 1.0f),
            symmetrySettings,
            Visibility.TOURNAMENT
        );

        SCMap map = new SCMap(maxDim, Biomes.loadBiome(biomeName));
        map.setUnexplored(false);
        map.setGeneratePreview(true);

        TerrainGenerator terrainGenerator = terrainStyle.getGeneratorSupplier().get();
        if (terrainGenerator instanceof SpawnFirstTerrainGenerator) {
            SpawnPlacer spawnPlacer = new SpawnPlacer(map, random.split());
            spawnPlacer.placeSpawns(parameters.spawnCount(), getSpawnSeparation(parameters, random), getTeamSeparation(parameters), symmetrySettings);
        }

        Pipeline.run(pipeline -> {}, () -> {
            terrainGenerator.initialize(map, random.split(), parameters, symmetrySettings);
            terrainGenerator.setupPipeline();
        });

        CompletableFuture<Void> heightFuture = CompletableFuture.runAsync(terrainGenerator::setHeightmapImage, EXECUTOR);
        CompletableFuture<Void> spawnFuture = CompletableFuture.runAsync(() -> {
            try {
                terrainGenerator.placeSpawns();
            } catch (Exception ignored) {}
        }, EXECUTOR);
        CompletableFuture.allOf(heightFuture, spawnFuture).join();

        TextureGenerator textureGenerator = createTextureGenerator(biomeName);
        Pipeline.run(pipeline -> {}, () -> {
            textureGenerator.initialize(map, random.split(), parameters, new SymmetrySettings(Symmetry.NONE), terrainGenerator);
            textureGenerator.setupPipeline();
        });

        CompletableFuture<Void> textFuture = CompletableFuture.runAsync(textureGenerator::setTextures, EXECUTOR);
        CompletableFuture<Void> decalFuture = CompletableFuture.runAsync(textureGenerator::setCompressedDecals, EXECUTOR);
        CompletableFuture.allOf(textFuture, decalFuture).join();

        textureGenerator.generatePreview();
        map.setHeights();

        BufferedImage fullPreview = map.getPreview();
        BufferedImage preview;

        if (width != height) {
            int offX = (maxDim - width) / 2;
            int offY = (maxDim - height) / 2;

            BufferedImage hm = copySubImage(map.getHeightmap(), offX, offY, width + 1, height + 1, 11);
            BufferedImage nm = copySubImage(map.getNormalMap(), offX, offY, width, height, 2);
            BufferedImage tLow = copySubImage(map.getTextureMasksLow(), offX, offY, width + 1, height + 1, 2);
            BufferedImage tHigh = copySubImage(map.getTextureMasksHigh(), offX, offY, width + 1, height + 1, 2);
            BufferedImage wm = copySubImage(map.getWaterMap(), offX / 2, offY / 2, Math.max(1, width / 2), Math.max(1, height / 2), 2);
            BufferedImage wf = copySubImage(map.getWaterFoamMap(), offX / 2, offY / 2, Math.max(1, width / 2), Math.max(1, height / 2), 10);
            BufferedImage ws = copySubImage(map.getWaterShadowMap(), offX / 2, offY / 2, Math.max(1, width / 2), Math.max(1, height / 2), 10);
            BufferedImage wd = copySubImage(map.getWaterDepthBiasMap(), offX / 2, offY / 2, Math.max(1, width / 2), Math.max(1, height / 2), 10);
            BufferedImage tt = copySubImage(map.getTerrainType(), offX, offY, width, height, 10);

            // Crop texture preview to exact rectangular aspect ratio and scale for high visual fidelity
            BufferedImage croppedPreview = null;
            if (fullPreview != null) {
                int pW = fullPreview.getWidth();
                int pH = fullPreview.getHeight();
                int px = (int) Math.round(((double) offX / maxDim) * pW);
                int py = (int) Math.round(((double) offY / maxDim) * pH);
                int pw = (int) Math.round(((double) width / maxDim) * pW);
                int ph = (int) Math.round(((double) height / maxDim) * pH);
                px = Math.max(0, Math.min(pW - 1, px));
                py = Math.max(0, Math.min(pH - 1, py));
                pw = Math.max(1, Math.min(pW - px, pw));
                ph = Math.max(1, Math.min(pH - py, ph));

                BufferedImage sub = copySubImage(fullPreview, px, py, pw, ph, BufferedImage.TYPE_INT_ARGB);
                int targetW = 512;
                int targetH = (int) Math.round(512.0 * ((double) height / width));
                croppedPreview = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = croppedPreview.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.drawImage(sub, 0, 0, targetW, targetH, null);
                g.dispose();
            }

            map.setDimensions(width, height);
            map.setCroppedImages(hm, nm, tLow, tHigh, wm, wf, ws, wd, tt, croppedPreview);
            map.setPlayableArea(new Vector4(0, 0, width, height));
            map.setHeights();

            preview = croppedPreview;

            List<Spawn> validSpawns = new ArrayList<>();
            for (Spawn s : map.getSpawns()) {
                float sx = s.getPosition().x() - offX;
                float sz = s.getPosition().z() - offY;
                if (sx >= 16 && sx <= width - 16 && sz >= 16 && sz <= height - 16) {
                    validSpawns.add(new Spawn(s.getId(), new Vector2(sx, sz), s.getNoRushOffset(), s.getTeamID()));
                }
            }
            // Il terreno nasce quadrato e viene ritagliato: le basi finite fuori dal ritaglio si perdono.
            // Se dopo il ritaglio le due squadre non hanno tutte le loro basi, si ridispongono tutte in modo
            // simmetrico dentro la mappa (prima, con piu' di un giocatore per squadra, si ricadeva sempre su 1 contro 1).
            int perTeam = Math.max(1, (spawnCount > 0 ? spawnCount : 2) / 2);
            long team0 = validSpawns.stream().filter(sp -> sp.getTeamID() == 0).count();
            long team1 = validSpawns.stream().filter(sp -> sp.getTeamID() == 1).count();
            boolean symOk = validSpawns.size() == perTeam * 2 && team0 == perTeam && team1 == perTeam;
            if (symOk && validSpawns.size() == 2) {
                Spawn s0 = validSpawns.get(0);
                Spawn s1 = validSpawns.get(1);
                if (effectiveSymmetry == Symmetry.X) {
                    symOk = Math.abs((s0.getPosition().x() + s1.getPosition().x()) - width) < 40f
                         && Math.abs(s0.getPosition().z() - s1.getPosition().z()) < 40f;
                } else if (effectiveSymmetry == Symmetry.Z) {
                    symOk = Math.abs((s0.getPosition().z() + s1.getPosition().z()) - height) < 40f
                         && Math.abs(s0.getPosition().x() - s1.getPosition().x()) < 40f;
                } else if (effectiveSymmetry == Symmetry.POINT2) {
                    symOk = Math.abs((s0.getPosition().x() + s1.getPosition().x()) - width) < 40f
                         && Math.abs((s0.getPosition().z() + s1.getPosition().z()) - height) < 40f;
                }
            }
            if (!symOk) {
                validSpawns.clear();
                validSpawns.addAll(layoutSpawns(map, width, height, effectiveSymmetry, perTeam));
            }
            map.getSpawns().clear();
            map.getSpawns().addAll(validSpawns);
        } else {
            map.setDimensions(width, height);
            preview = fullPreview;
        }

        if (preview == null) {
            preview = ImageUtil.scaleImage(map.getHeightmap(), PreviewGenerator.PREVIEW_SIZE, (int) Math.round(PreviewGenerator.PREVIEW_SIZE * ((double) height / width)));
        }
        preview = applyBarBiome(map, barBiome, preview);
        BufferedImage slopeOverlay = generateSlopeOverlayImage(map, map.getBiome().waterSettings().elevation());
        return new GroundResult(map, preview, symmetrySettings, parameters, seed, slopeOverlay);
    }

    /** Lega il bioma BAR alla mappa e ne ridisegna l'anteprima con la sua tavolozza. */
    private static BufferedImage applyBarBiome(SCMap map, com.faforever.neroxis.exporter.bar.BarBiomes.BarBiome barBiome, BufferedImage preview) {
        com.faforever.neroxis.exporter.bar.BarBiomes.attach(map, barBiome);
        if (barBiome == null) {
            return preview;
        }
        int w = preview != null ? preview.getWidth() : 512;
        int h = preview != null ? preview.getHeight() : 512;
        BufferedImage recolored = com.faforever.neroxis.exporter.bar.BarBiomes.renderPreview(map, barBiome, w, h);
        map.setPreview(recolored);
        return recolored;
    }

    /**
     * Dispone le basi di due squadre in modo simmetrico: una fila lungo il proprio lato (due file sfalsate
     * se i giocatori sono tanti). Ogni base viene spostata sul punto piu' piano e asciutto nelle vicinanze,
     * e quella avversaria e' la sua immagine secondo la simmetria della mappa.
     */
    static List<Spawn> layoutSpawns(SCMap map, int width, int height, Symmetry symmetry, int perTeam) {
        boolean point = symmetry == Symmetry.POINT2;
        boolean alongX = symmetry == Symmetry.X || (symmetry != Symmetry.Z && width >= height);
        float length = alongX ? width : height;
        float depth = alongX ? height : width;
        float spacing = Math.min(0.8f / perTeam, 0.22f);
        boolean twoRows = perTeam > 4 || (depth * spacing < 26 && perTeam > 1);
        float water = Float.NEGATIVE_INFINITY;
        if (map.getBiome() != null && map.getBiome().waterSettings() != null && map.getBiome().waterSettings().waterPresent()) {
            water = map.getBiome().waterSettings().elevation();
        }
        Raster heights = map.getHeightmap() != null ? map.getHeightmap().getRaster() : null;
        float scale = map.getHeightMapScale();
        List<Spawn> team0 = new ArrayList<>();
        List<Spawn> team1 = new ArrayList<>();
        for (int i = 0; i < perTeam; i++) {
            float a = Math.max(20f, length * 0.14f) + ((twoRows && (i % 2 == 1)) ? Math.min(30f, length * 0.07f) : 0f);
            float b = depth * (0.5f + ((i - ((perTeam - 1) / 2.0f)) * spacing));
            float x = alongX ? a : b;
            float z = alongX ? b : a;
            // punto piu' piano e asciutto entro 24 celle; un po' di preferenza per restare vicino al posto ideale
            float bestX = x;
            float bestZ = z;
            float bestScore = Float.MAX_VALUE;
            // quota piu' bassa (asciutta) della zona: le basi vanno in pianura, non in cima a un altopiano
            float lowest = Float.MAX_VALUE;
            if (heights != null) {
                for (int dz = -24; dz <= 24; dz += 4) {
                    for (int dx = -24; dx <= 24; dx += 4) {
                        int hx = Math.max(0, Math.min(heights.getWidth() - 1, Math.round(x + dx)));
                        int hz = Math.max(0, Math.min(heights.getHeight() - 1, Math.round(z + dz)));
                        float h = heights.getSample(hx, hz, 0) * scale;
                        if (h >= water + 1.0f) lowest = Math.min(lowest, h);
                    }
                }
            }
            for (int dz = -24; dz <= 24; dz += 4) {
                for (int dx = -24; dx <= 24; dx += 4) {
                    float px = x + dx;
                    float pz = z + dz;
                    if (px < 18 || pz < 18 || px > width - 18 || pz > height - 18) continue;
                    // resta nella propria meta'
                    if ((alongX ? px : pz) > length * 0.42f) continue;
                    float score = calculateSlope(map, Math.round(px), Math.round(pz)) * 40f + ((Math.abs(dx) + Math.abs(dz)) * 0.02f);
                    if (heights != null) {
                        int hx = Math.max(0, Math.min(heights.getWidth() - 1, Math.round(px)));
                        int hz = Math.max(0, Math.min(heights.getHeight() - 1, Math.round(pz)));
                        float h = heights.getSample(hx, hz, 0) * scale;
                        if (h < water + 1.0f) score += 1000f;
                        else if (lowest < Float.MAX_VALUE) score += (h - lowest) * 1.5f;
                    }
                    if (score < bestScore) {
                        bestScore = score;
                        bestX = px;
                        bestZ = pz;
                    }
                }
            }
            float mx = (alongX || point) ? width - bestX : bestX;
            float mz = (!alongX || point) ? height - bestZ : bestZ;
            team0.add(new Spawn("Spawn_" + (i * 2), new Vector2(bestX, bestZ), new Vector2(0, 0), 0));
            team1.add(new Spawn("Spawn_" + ((i * 2) + 1), new Vector2(mx, mz), new Vector2(0, 0), 1));
        }
        List<Spawn> all = new ArrayList<>(team0);
        all.addAll(team1);
        return all;
    }

    private static BufferedImage copySubImage(BufferedImage src, int x, int y, int w, int h, int imageType) {
        BufferedImage dest = new BufferedImage(w, h, imageType);
        Graphics2D g = dest.createGraphics();
        g.drawImage(src, 0, 0, w, h, x, y, x + w, y + h, null);
        g.dispose();
        return dest;
    }

    private static TextureGenerator createTextureGenerator(BiomeName biomeName) {
        return switch (biomeName) {
            case BRIMSTONE -> new BrimstoneTextureGenerator();
            case DESERT -> new DesertTextureGenerator();
            case EARLYAUTUMN -> new EarlyAutumnTextureGenerator();
            case FRITHEN -> new FrithenTextureGenerator();
            case MARS -> new MarsTextureGenerator();
            case MOONLIGHT -> new MoonlightTextureGenerator();
            case PRAYER -> new PrayerTextureGenerator();
            case STONES -> new StonesTextureGenerator();
            case SUNSET -> new SunsetTextureGenerator();
            case SYRTIS -> new SyrtisTextureGenerator();
            case WINDINGRIVER -> new WindingRiverTextureGenerator();
            case WONDER -> new WonderTextureGenerator();
            case CRYSTALLINE -> new CrystallineTextureGenerator();
            default -> new DesertTextureGenerator();
        };
    }

    private static float getSpawnSeparation(GeneratorParameters generatorParameters, SplittableRandom random) {
        int mapSize = generatorParameters.mapSize();
        int numTeams = generatorParameters.numTeams();
        int spawnCount = generatorParameters.spawnCount();
        if (numTeams < 2) {
            return (mapSize / (float) spawnCount) * 1.5f;
        }
        if (numTeams == 2) {
            return random.nextInt((mapSize / 4) - (mapSize / 16)) + (mapSize / 16.0f);
        }
        if (numTeams < 8) {
            return random.nextInt(((mapSize / 2) / numTeams) - (mapSize / 16)) + (mapSize / 16.0f);
        }
        return 0.0f;
    }

    private static int getTeamSeparation(GeneratorParameters generatorParameters) {
        int numTeams = generatorParameters.numTeams();
        int mapSize = generatorParameters.mapSize();
        if (numTeams < 2) {
            return 0;
        }
        if (numTeams == 2) {
            return mapSize / 2;
        }
        return StrictMath.min(mapSize / numTeams, 256);
    }

    public static float calculateSlope(SCMap map, int x, int z) {
        if (map == null) return 0f;
        return map.getSlopeAtWorld(x, z);
    }

    public static BufferedImage generateSlopeOverlayImage(SCMap map, float waterLevel) {
        if (map == null || map.getHeightmap() == null) return null;
        BufferedImage hm = map.getHeightmap();
        int w = map.getWidth();
        int h = map.getHeight();
        if (w <= 0) w = map.getSize();
        if (h <= 0) h = map.getSize();

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        float scale = map.getHeightMapScale();
        int hmW = hm.getWidth();
        int hmH = hm.getHeight();

        int[] pixelH = new int[1];
        Raster r = hm.getRaster();

        int[] outPixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int ym = Math.min(hmH - 1, y);
            int yUp = Math.max(0, ym - 1);
            int yDn = Math.min(hmH - 1, ym + 1);

            for (int x = 0; x < w; x++) {
                int xm = Math.min(hmW - 1, x);
                int xLt = Math.max(0, xm - 1);
                int xRt = Math.min(hmW - 1, xm + 1);

                r.getPixel(xm, ym, pixelH);
                float curHeight = pixelH[0] * scale;

                if (waterLevel > -900f && curHeight < waterLevel) {
                    float depth = Math.min(1.0f, (waterLevel - curHeight) / 30.0f);
                    int blue = (int) (180 + 75 * depth);
                    int green = (int) (140 - 40 * depth);
                    int red = (int) (14 - 10 * depth);
                    outPixels[y * w + x] = (220 << 24) | (red << 16) | (green << 8) | blue;
                    continue;
                }

                r.getPixel(xRt, ym, pixelH);
                float hR = pixelH[0] * scale;
                r.getPixel(xLt, ym, pixelH);
                float hL = pixelH[0] * scale;

                r.getPixel(xm, yDn, pixelH);
                float hD = pixelH[0] * scale;
                r.getPixel(xm, yUp, pixelH);
                float hU = pixelH[0] * scale;

                float dx = (hR - hL) / Math.max(1, xRt - xLt);
                float dz = (hD - hU) / Math.max(1, yDn - yUp);
                float slope = (float) Math.sqrt(dx * dx + dz * dz) * 0.35f;

                int color;
                if (slope < 0.22f) {
                    float t = Math.min(1.0f, Math.max(0f, slope / 0.22f));
                    int red = (int) (34 + t * 98);
                    int grn = (int) (197 + t * 7);
                    int blu = (int) (94 - t * 72);
                    color = (210 << 24) | (red << 16) | (grn << 8) | blu;
                } else if (slope < 0.35f) {
                    float t = Math.min(1.0f, Math.max(0f, (slope - 0.22f) / (0.35f - 0.22f)));
                    int red = (int) (132 + t * 102);
                    int grn = (int) (204 - t * 25);
                    int blu = (int) (22 - t * 14);
                    color = (220 << 24) | (red << 16) | (grn << 8) | blu;
                } else if (slope < 0.65f) {
                    float t = Math.min(1.0f, Math.max(0f, (slope - 0.35f) / (0.65f - 0.35f)));
                    int red = (int) (234 + t * 15);
                    int grn = (int) (179 - t * 64);
                    int blu = (int) (8 + t * 14);
                    color = (230 << 24) | (red << 16) | (grn << 8) | blu;
                } else {
                    float t = Math.min(1.0f, Math.max(0f, (slope - 0.65f) / 0.5f));
                    int red = (int) (239 - t * 29);
                    int grn = (int) (68 - t * 38);
                    int blu = (int) (68 - t * 38);
                    color = (245 << 24) | (red << 16) | (grn << 8) | blu;
                }
                outPixels[y * w + x] = color;
            }
        }
        out.setRGB(0, 0, w, h, outPixels, 0, w);
        return out;
    }

    public static void smoothSlopes(SCMap map, float maxSlopeThreshold, int passes) {
        if (map == null || map.getHeightmap() == null) return;
        BufferedImage hm = map.getHeightmap();
        int w = hm.getWidth();
        int h = hm.getHeight();
        float scale = map.getHeightMapScale();
        WritableRaster r = hm.getRaster();
        int[] pixel = new int[1];

        for (int p = 0; p < Math.max(1, passes); p++) {
            float[][] copy = new float[w][h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    r.getPixel(x, y, pixel);
                    copy[x][y] = pixel[0] * scale;
                }
            }

            for (int y = 1; y < h - 1; y++) {
                for (int x = 1; x < w - 1; x++) {
                    float dx = (copy[x + 1][y] - copy[x - 1][y]) / 2f;
                    float dz = (copy[x][y + 1] - copy[x][y - 1]) / 2f;
                    float slope = (float) Math.sqrt(dx * dx + dz * dz) * 0.35f;

                    if (maxSlopeThreshold <= 0 || slope > maxSlopeThreshold) {
                        float sum = copy[x][y] * 2.0f;
                        float count = 2.0f;
                        for (int dy = -1; dy <= 1; dy++) {
                            for (int ddx = -1; ddx <= 1; ddx++) {
                                if (dy == 0 && ddx == 0) continue;
                                sum += copy[x + ddx][y + dy];
                                count += 1.0f;
                            }
                        }
                        float smoothed = sum / count;
                        pixel[0] = Math.max(0, Math.min(65535, Math.round(smoothed / scale)));
                        r.setPixel(x, y, pixel);
                    }
                }
            }
        }
        map.setHeights();
    }

    public static void rescaleHeightsOnRaster(BufferedImage hm, float heightScale, float targetMaxHeight, float targetWaterLevel) {
        if (hm == null) return;
        int w = hm.getWidth();
        int h = hm.getHeight();
        WritableRaster wr = hm.getRaster();
        int[] px = new int[1];

        float minH = Float.MAX_VALUE;
        float maxH = -Float.MAX_VALUE;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                wr.getPixel(x, y, px);
                float val = px[0] * heightScale;
                if (val < minH) minH = val;
                if (val > maxH) maxH = val;
            }
        }

        float curRange = Math.max(1.0f, maxH - minH);
        float desiredRange = Math.max(10.0f, targetMaxHeight - Math.min(minH, targetWaterLevel));
        float factor = desiredRange / curRange;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                wr.getPixel(x, y, px);
                float val = px[0] * heightScale;
                float newVal = minH + (val - minH) * factor;
                px[0] = Math.max(0, Math.min(65535, Math.round(newVal / heightScale)));
                wr.setPixel(x, y, px);
            }
        }
    }

    public static class CustomHeightmapTerrainGenerator extends TerrainGenerator {
        public CustomHeightmapTerrainGenerator(SCMap map, FloatMask heightmap, SymmetrySettings sym, float waterElev) {
            this.map = map;
            this.heightmap = heightmap;
            this.symmetrySettings = sym;
            int size = heightmap.getSize();
            SplittableRandom r = new SplittableRandom();
            this.slope = new FloatMask(size, r.split(), sym, "slope");
            this.slope.init(((FloatMask) heightmap.copy()).supcomGradient());
            this.impassable = new BooleanMask(size, r.split(), sym, "impassable");
            this.impassable.init(this.slope, 0.7f);
            this.unbuildable = new BooleanMask(size, r.split(), sym, "unbuildable");
            this.unbuildable.init(this.slope, 0.05f);
            this.passableLand = this.heightmap.copyAsBooleanMask(waterElev);
            this.passableLand.subtract(this.impassable);
            this.passableWater = this.heightmap.copyAsBooleanMask(waterElev).invert();
            this.passable = ((BooleanMask) this.passableLand.copy()).add(this.passableWater);
            this.mexDeadZone = new BooleanMask(size, r.split(), sym, "mexDeadZone");
        }

        @Override
        public void setupPipeline() {}

        @Override
        public void placeSpawns() {}
    }

    private static final ThreadLocal<BufferedImage> HEIGHT_SOURCE_OVERRIDE = new ThreadLocal<>();

    /**
     * Heightmap da usare al posto di quella della mappa esistente nella prossima rigenerazione
     * fatta da questo thread (rilievo modellato dai cursori); null per tornare al comportamento normale.
     */
    public static void setHeightSourceOverride(BufferedImage source) {
        if (source == null) {
            HEIGHT_SOURCE_OVERRIDE.remove();
        } else {
            HEIGHT_SOURCE_OVERRIDE.set(source);
        }
    }

    public static GroundResult generateGroundFromHeightmap(
        SCMap existingMap,
        BufferedImage customHeightmap,
        int width,
        int height,
        float maxHeight,
        float waterLevel,
        String biomeNameStr,
        Symmetry symmetry,
        int spawnCount,
        int numTeams,
        long seed
    ) throws Exception {
        int maxDim = Math.max(width, height);
        SplittableRandom random = new SplittableRandom(seed);
        // biomi ricavati dalle mappe BAR: forma del terreno dal bioma Neroxis di base, colori/luce/acqua dal bioma BAR
        com.faforever.neroxis.exporter.bar.BarBiomes.BarBiome barBiome = com.faforever.neroxis.exporter.bar.BarBiomes.find(biomeNameStr);
        if (barBiome != null) {
            biomeNameStr = barBiome.base;
        }
        BiomeName biomeName;
        try {
            biomeName = BiomeName.valueOf(biomeNameStr.toUpperCase());
        } catch (Exception e) {
            biomeName = BiomeName.DESERT;
        }

        Symmetry effectiveSymmetry = symmetry;
        if (effectiveSymmetry == null) {
            effectiveSymmetry = (height > width) ? Symmetry.Z : Symmetry.X;
        } else if (width != height) {
            if (effectiveSymmetry == Symmetry.DIAG) {
                effectiveSymmetry = Symmetry.POINT2;
            } else if (effectiveSymmetry != Symmetry.Z && effectiveSymmetry != Symmetry.X && effectiveSymmetry != Symmetry.POINT2 && effectiveSymmetry != Symmetry.QUAD && effectiveSymmetry != Symmetry.NONE) {
                effectiveSymmetry = (height > width) ? Symmetry.Z : Symmetry.X;
            }
        }

        SymmetrySettings symmetrySettings = new SymmetrySettings(effectiveSymmetry);
        GeneratorParameters parameters = new GeneratorParameters(
            spawnCount > 0 ? spawnCount : 2,
            maxDim,
            numTeams > 0 ? numTeams : 2,
            new MapStyle.Custom(TerrainStyle.BASIC, biomeName, PropStyle.BASIC, ResourceStyle.BASIC, 1.0f, 1.0f),
            symmetrySettings,
            Visibility.TOURNAMENT
        );

        Biome baseBiome = Biomes.loadBiome(biomeName);
        WaterSettings oldW = baseBiome.waterSettings();
        WaterSettings newW = new WaterSettings(
            waterLevel > -500f,
            waterLevel,
            waterLevel - 15f,
            waterLevel - 30f,
            oldW.surfaceColor(),
            oldW.colorLerp(),
            oldW.refractionScale(),
            oldW.fresnelBias(),
            oldW.fresnelPower(),
            oldW.unitReflection(),
            oldW.skyReflection(),
            oldW.sunShininess(),
            oldW.sunStrength(),
            oldW.sunDirection(),
            oldW.sunColor(),
            oldW.sunReflection(),
            oldW.sunGlow(),
            oldW.texPathCubemap(),
            oldW.texPathWaterRamp(),
            oldW.waveTextures()
        );
        Biome tunedBiome = new Biome(biomeName, baseBiome.terrainMaterials(), baseBiome.propMaterials(), baseBiome.decalMaterials(), newW, baseBiome.lightingSettings());

        SCMap map = new SCMap(maxDim, tunedBiome);
        map.setUnexplored(false);
        map.setGeneratePreview(true);

        BufferedImage workingHm = new BufferedImage(maxDim + 1, maxDim + 1, BufferedImage.TYPE_USHORT_GRAY);
        float heightScale = map.getHeightMapScale();

        if (customHeightmap != null) {
            BufferedImage scaled = new BufferedImage(maxDim + 1, maxDim + 1, BufferedImage.TYPE_USHORT_GRAY);
            Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(customHeightmap, 0, 0, maxDim + 1, maxDim + 1, null);
            if (width != height) {
                // mappa rettangolare: l'heightmap va nell'area che verra' ritagliata, non stirata sul quadrato
                g.drawImage(customHeightmap, (maxDim - width) / 2, (maxDim - height) / 2, width + 1, height + 1, null);
            }
            g.dispose();

            Raster r = scaled.getRaster();
            int[] px = new int[1];
            int minRaw = Integer.MAX_VALUE;
            int maxRaw = 0;
            for (int y = 0; y <= maxDim; y++) {
                for (int x = 0; x <= maxDim; x++) {
                    r.getPixel(x, y, px);
                    if (px[0] < minRaw) minRaw = px[0];
                    if (px[0] > maxRaw) maxRaw = px[0];
                }
            }
            int rawRange = Math.max(1, maxRaw - minRaw);
            float targetRawMax = maxHeight / heightScale;

            WritableRaster wr = workingHm.getRaster();
            for (int y = 0; y <= maxDim; y++) {
                for (int x = 0; x <= maxDim; x++) {
                    r.getPixel(x, y, px);
                    float norm = (float) (px[0] - minRaw) / rawRange;
                    px[0] = Math.max(0, Math.min(65535, Math.round(norm * targetRawMax)));
                    wr.setPixel(x, y, px);
                }
            }
        } else if (existingMap != null && existingMap.getHeightmap() != null) {
            BufferedImage src = HEIGHT_SOURCE_OVERRIDE.get() != null ? HEIGHT_SOURCE_OVERRIDE.get() : existingMap.getHeightmap();
            Graphics2D g = workingHm.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(src, 0, 0, maxDim + 1, maxDim + 1, null);
            if (width != height) {
                // mappa rettangolare: il terreno esistente torna esattamente nell'area ritagliata
                g.drawImage(src, (maxDim - width) / 2, (maxDim - height) / 2, width + 1, height + 1, null);
            }
            g.dispose();

            rescaleHeightsOnRaster(workingHm, heightScale, maxHeight, waterLevel);
        }

        Graphics2D gMapHm = map.getHeightmap().createGraphics();
        gMapHm.drawImage(workingHm, 0, 0, null);
        gMapHm.dispose();

        FloatMask hmMask = new FloatMask(workingHm, random.split(), symmetrySettings, heightScale, "heightmap");
        CustomHeightmapTerrainGenerator customTerrainGen = new CustomHeightmapTerrainGenerator(map, hmMask, symmetrySettings, waterLevel);

        TextureGenerator textureGenerator = createTextureGenerator(biomeName);
        Pipeline.run(pipeline -> {}, () -> {
            textureGenerator.initialize(map, random.split(), parameters, new SymmetrySettings(Symmetry.NONE), customTerrainGen);
            textureGenerator.setupPipeline();
        });

        CompletableFuture<Void> textFuture = CompletableFuture.runAsync(textureGenerator::setTextures, EXECUTOR);
        CompletableFuture<Void> decalFuture = CompletableFuture.runAsync(textureGenerator::setCompressedDecals, EXECUTOR);
        CompletableFuture.allOf(textFuture, decalFuture).join();

        textureGenerator.generatePreview();
        map.setHeights();

        BufferedImage fullPreview = map.getPreview();
        BufferedImage preview;

        if (width != height) {
            int offX = (maxDim - width) / 2;
            int offY = (maxDim - height) / 2;

            BufferedImage hm = copySubImage(map.getHeightmap(), offX, offY, width + 1, height + 1, 11);
            BufferedImage nm = copySubImage(map.getNormalMap(), offX, offY, width, height, 2);
            BufferedImage tLow = copySubImage(map.getTextureMasksLow(), offX, offY, width + 1, height + 1, 2);
            BufferedImage tHigh = copySubImage(map.getTextureMasksHigh(), offX, offY, width + 1, height + 1, 2);
            BufferedImage wm = copySubImage(map.getWaterMap(), offX / 2, offY / 2, Math.max(1, width / 2), Math.max(1, height / 2), 2);
            BufferedImage wf = copySubImage(map.getWaterFoamMap(), offX / 2, offY / 2, Math.max(1, width / 2), Math.max(1, height / 2), 10);
            BufferedImage ws = copySubImage(map.getWaterShadowMap(), offX / 2, offY / 2, Math.max(1, width / 2), Math.max(1, height / 2), 10);
            BufferedImage wd = copySubImage(map.getWaterDepthBiasMap(), offX / 2, offY / 2, Math.max(1, width / 2), Math.max(1, height / 2), 10);
            BufferedImage tt = copySubImage(map.getTerrainType(), offX, offY, width, height, 10);

            BufferedImage croppedPreview = null;
            if (fullPreview != null) {
                int pW = fullPreview.getWidth();
                int pH = fullPreview.getHeight();
                int px = (int) Math.round(((double) offX / maxDim) * pW);
                int py = (int) Math.round(((double) offY / maxDim) * pH);
                int pw = (int) Math.round(((double) width / maxDim) * pW);
                int ph = (int) Math.round(((double) height / maxDim) * pH);
                px = Math.max(0, Math.min(pW - 1, px));
                py = Math.max(0, Math.min(pH - 1, py));
                pw = Math.max(1, Math.min(pW - px, pw));
                ph = Math.max(1, Math.min(pH - py, ph));

                BufferedImage sub = copySubImage(fullPreview, px, py, pw, ph, BufferedImage.TYPE_INT_ARGB);
                int targetW = 512;
                int targetH = (int) Math.round(512.0 * ((double) height / width));
                croppedPreview = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = croppedPreview.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.drawImage(sub, 0, 0, targetW, targetH, null);
                g2.dispose();
            }

            map.setDimensions(width, height);
            map.setCroppedImages(hm, nm, tLow, tHigh, wm, wf, ws, wd, tt, croppedPreview);
            map.setPlayableArea(new Vector4(0, 0, width, height));
            map.setHeights();
            preview = croppedPreview;
        } else {
            map.setDimensions(width, height);
            preview = fullPreview;
        }

        if (preview == null) {
            preview = ImageUtil.scaleImage(map.getHeightmap(), PreviewGenerator.PREVIEW_SIZE, (int) Math.round(PreviewGenerator.PREVIEW_SIZE * ((double) height / width)));
        }

        if (existingMap != null) {
            map.getSpawns().addAll(existingMap.getSpawns());
            map.getMexes().addAll(existingMap.getMexes());
            map.getHydros().addAll(existingMap.getHydros());
            map.getProps().addAll(existingMap.getProps());
            map.setHeights();
        }

        preview = applyBarBiome(map, barBiome, preview);
        BufferedImage slopeOverlay = generateSlopeOverlayImage(map, waterLevel);
        return new GroundResult(map, preview, symmetrySettings, parameters, seed, slopeOverlay);
    }
}

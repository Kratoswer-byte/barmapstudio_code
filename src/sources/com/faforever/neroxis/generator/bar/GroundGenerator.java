package com.faforever.neroxis.generator.bar;

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
import com.faforever.neroxis.util.Pipeline;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
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
        long seed
    ) {}

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
            boolean symOk = false;
            if (validSpawns.size() == 2) {
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
                } else {
                    symOk = true;
                }
            }
            if (validSpawns.size() < 2 || !symOk) {
                validSpawns.clear();
                if (effectiveSymmetry == Symmetry.X) {
                    validSpawns.add(new Spawn("Spawn_0", new Vector2(width * 0.18f, height / 2f), new Vector2(0, 0), 0));
                    validSpawns.add(new Spawn("Spawn_1", new Vector2(width * 0.82f, height / 2f), new Vector2(0, 0), 1));
                } else if (effectiveSymmetry == Symmetry.Z) {
                    validSpawns.add(new Spawn("Spawn_0", new Vector2(width / 2f, height * 0.18f), new Vector2(0, 0), 0));
                    validSpawns.add(new Spawn("Spawn_1", new Vector2(width / 2f, height * 0.82f), new Vector2(0, 0), 1));
                } else if (effectiveSymmetry == Symmetry.POINT2) {
                    validSpawns.add(new Spawn("Spawn_0", new Vector2(width * 0.18f, height * 0.18f), new Vector2(0, 0), 0));
                    validSpawns.add(new Spawn("Spawn_1", new Vector2(width * 0.82f, height * 0.82f), new Vector2(0, 0), 1));
                } else if (effectiveSymmetry == Symmetry.QUAD) {
                    validSpawns.add(new Spawn("Spawn_0", new Vector2(width * 0.18f, height * 0.18f), new Vector2(0, 0), 0));
                    validSpawns.add(new Spawn("Spawn_1", new Vector2(width * 0.82f, height * 0.18f), new Vector2(0, 0), 1));
                    validSpawns.add(new Spawn("Spawn_2", new Vector2(width * 0.18f, height * 0.82f), new Vector2(0, 0), 0));
                    validSpawns.add(new Spawn("Spawn_3", new Vector2(width * 0.82f, height * 0.82f), new Vector2(0, 0), 1));
                } else {
                    if (height >= width) {
                        validSpawns.add(new Spawn("Spawn_0", new Vector2(width / 2f, height * 0.18f), new Vector2(0, 0), 0));
                        validSpawns.add(new Spawn("Spawn_1", new Vector2(width / 2f, height * 0.82f), new Vector2(0, 0), 1));
                    } else {
                        validSpawns.add(new Spawn("Spawn_0", new Vector2(width * 0.18f, height / 2f), new Vector2(0, 0), 0));
                        validSpawns.add(new Spawn("Spawn_1", new Vector2(width * 0.82f, height / 2f), new Vector2(0, 0), 1));
                    }
                }
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
        return new GroundResult(map, preview, symmetrySettings, parameters, seed);
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
}

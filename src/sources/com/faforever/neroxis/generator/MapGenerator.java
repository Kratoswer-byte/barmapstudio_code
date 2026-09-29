package com.faforever.neroxis.generator;

import com.faforever.neroxis.biomes.BiomeName;
import com.faforever.neroxis.biomes.Biomes;
import com.faforever.neroxis.exporter.PreviewGenerator;
import com.faforever.neroxis.generator.prop.PropGenerator;
import com.faforever.neroxis.generator.resource.ResourceGenerator;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.generator.terrain.SpawnFirstTerrainGenerator;
import com.faforever.neroxis.generator.terrain.TerrainGenerator;
import com.faforever.neroxis.generator.texture.BrimstoneTextureGenerator;
import com.faforever.neroxis.generator.texture.CrystallineTextureGenerator;
import com.faforever.neroxis.generator.texture.DesertTextureGenerator;
import com.faforever.neroxis.generator.texture.EarlyAutumnTextureGenerator;
import com.faforever.neroxis.generator.texture.FrithenTextureGenerator;
import com.faforever.neroxis.generator.texture.MarsTextureGenerator;
import com.faforever.neroxis.generator.texture.MoonlightTextureGenerator;
import com.faforever.neroxis.generator.texture.PrayerTextureGenerator;
import com.faforever.neroxis.generator.texture.StonesTextureGenerator;
import com.faforever.neroxis.generator.texture.SunsetTextureGenerator;
import com.faforever.neroxis.generator.texture.SyrtisTextureGenerator;
import com.faforever.neroxis.generator.texture.TextureGenerator;
import com.faforever.neroxis.generator.texture.WindingRiverTextureGenerator;
import com.faforever.neroxis.generator.texture.WonderTextureGenerator;
import com.faforever.neroxis.generator.util.HasParameterConstraints;
import com.faforever.neroxis.generator.util.SpawnPlacementException;
import com.faforever.neroxis.generator.util.serial.MapStyle;
import com.faforever.neroxis.generator.util.serial.PropStyle;
import com.faforever.neroxis.generator.util.serial.ResourceStyle;
import com.faforever.neroxis.generator.util.serial.TerrainStyle;
import com.faforever.neroxis.generator.util.serial.Visibility;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.placement.SpawnPlacer;
import com.faforever.neroxis.util.DebugUtil;
import com.faforever.neroxis.util.Pipeline;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/MapGenerator.class */
public class MapGenerator {
    private static volatile Float resourceDensityOverride;
    private static volatile int mexesPerPlayerOverride;
    private static final ExecutorService PLACEMENT_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private static volatile int hydroCountOverride = -1;

    public static void setBarOverrides(Float resourceDensity, int mexesPerPlayer) {
        setBarOverrides(resourceDensity, mexesPerPlayer, -1);
    }

    public static void setBarOverrides(Float resourceDensity, int mexesPerPlayer, int hydroCount) {
        resourceDensityOverride = resourceDensity;
        mexesPerPlayerOverride = mexesPerPlayer;
        hydroCountOverride = hydroCount;
    }

    /* JADX WARN: Code duplicated, block: B:74:0x0320  */
    /* JADX WARN: Code duplicated, block: B:76:0x0326  */
    static GenerationResults generate(GeneratorParameters generatorParameters, RandomGenerator.SplittableGenerator random, boolean debug, boolean visualize) {
        TextureGenerator crystallineTextureGenerator;
        Predicate<HasParameterConstraints> constraintsMatchPredicate = hasConstraints -> {
            return hasConstraints.parameterConstraints().matches(generatorParameters);
        };
        TerrainStyle terrainStyle = generatorParameters.mapStyle().getTerrainStyleOptions().select(random, constraintsMatchPredicate);
        BiomeName biomeName = generatorParameters.mapStyle().getBiomeNameOptions().select(random);
        ResourceStyle resourceStyle = generatorParameters.mapStyle().getResourceStyleOptions().select(random, constraintsMatchPredicate);
        PropStyle propStyle = generatorParameters.mapStyle().getPropStyleOptions().select(random, constraintsMatchPredicate);
        SymmetrySettings symmetrySettings = generatorParameters.symmetrySettings();
        SCMap map = new SCMap(generatorParameters.mapSize(), Biomes.loadBiome(biomeName));
        map.setUnexplored(generatorParameters.visibility() == Visibility.UNEXPLORED);
        map.setGeneratePreview((generatorParameters.visibility() == Visibility.BLIND || map.isUnexplored()) ? false : true);
        TerrainGenerator terrainGenerator = null;
        List<Pipeline.Entry> terrainPipelineEntries = new ArrayList();
        while (map.getSpawnCount() != generatorParameters.spawnCount()) {
            try {
                TerrainGenerator attemptedTerrainGenerator = terrainStyle.getGeneratorSupplier().get();
                if (attemptedTerrainGenerator instanceof SpawnFirstTerrainGenerator) {
                    SpawnPlacer spawnPlacer = new SpawnPlacer(map, random.split());
                    DebugUtil.timedRun("com.faforever.neroxis.map.generator", "placeSpawns", () -> {
                        spawnPlacer.placeSpawns(generatorParameters.spawnCount(), getSpawnSeparation(generatorParameters, random), getTeamSeparation(generatorParameters), symmetrySettings);
                    });
                }
                terrainPipelineEntries = Pipeline.run(pipeline -> {
                    pipeline.setDebug(debug);
                    pipeline.setVisualize(visualize);
                }, () -> {
                    attemptedTerrainGenerator.initialize(map, random.split(), generatorParameters, symmetrySettings);
                    attemptedTerrainGenerator.setupPipeline();
                });
                Objects.requireNonNull(attemptedTerrainGenerator);
                CompletableFuture<Void> heightMapFuture = CompletableFuture.runAsync(attemptedTerrainGenerator::setHeightmapImage, PLACEMENT_EXECUTOR);
                Objects.requireNonNull(attemptedTerrainGenerator);
                CompletableFuture<Void> spawnFuture = CompletableFuture.runAsync(attemptedTerrainGenerator::placeSpawns, PLACEMENT_EXECUTOR);
                CompletableFuture.allOf(heightMapFuture, spawnFuture).join();
                terrainGenerator = attemptedTerrainGenerator;
            } catch (CompletionException exception) {
                switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, Throwable.class, Integer.TYPE), SpawnPlacementException.class, Throwable.class).dynamicInvoker().invoke(exception.getCause(), 0) /* invoke-custom */) {
                    case -1:
                        throw exception;
                    case 0:
                        System.out.println("Unable to place all spawns trying new generation");
                        break;
                    case 1:
                        throw exception;
                    default:
                        throw new MatchException((String) null, (Throwable) null);
                }
            }
        }
        if (terrainGenerator == null) {
            throw new IllegalStateException("Unable to generate terrain");
        }
        TerrainGenerator actualTerrainGenerator = terrainGenerator;
        switch (biomeName) {
            case BRIMSTONE:
                crystallineTextureGenerator = new BrimstoneTextureGenerator();
                break;
            case DESERT:
                crystallineTextureGenerator = new DesertTextureGenerator();
                break;
            case EARLYAUTUMN:
                crystallineTextureGenerator = new EarlyAutumnTextureGenerator();
                break;
            case FRITHEN:
                crystallineTextureGenerator = new FrithenTextureGenerator();
                break;
            case MARS:
                crystallineTextureGenerator = new MarsTextureGenerator();
                break;
            case MOONLIGHT:
                crystallineTextureGenerator = new MoonlightTextureGenerator();
                break;
            case PRAYER:
                crystallineTextureGenerator = new PrayerTextureGenerator();
                break;
            case STONES:
                crystallineTextureGenerator = new StonesTextureGenerator();
                break;
            case SUNSET:
                crystallineTextureGenerator = new SunsetTextureGenerator();
                break;
            case SYRTIS:
                crystallineTextureGenerator = new SyrtisTextureGenerator();
                break;
            case WINDINGRIVER:
                crystallineTextureGenerator = new WindingRiverTextureGenerator();
                break;
            case WONDER:
                crystallineTextureGenerator = new WonderTextureGenerator();
                break;
            case CRYSTALLINE:
                crystallineTextureGenerator = new CrystallineTextureGenerator();
                break;
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
        TextureGenerator textureGenerator = crystallineTextureGenerator;
        ResourceGenerator resourceGenerator = resourceStyle.getGeneratorSupplier().get();
        PropGenerator propGenerator = propStyle.getGeneratorSupplier().get();
        MapStyle mapStyle = generatorParameters.mapStyle();
        if (mapStyle instanceof MapStyle.Custom) {
            MapStyle.Custom custom = (MapStyle.Custom) mapStyle;
            try {
                custom.terrainStyle();
                custom.biomeName();
                custom.propStyle();
                custom.resourceStyle();
                float reclaimDensity = custom.reclaimDensity();
                if (1 != 0) {
                    float resourceDensity = custom.resourceDensity();
                    if (1 != 0) {
                        resourceGenerator.setResourceDensity(resourceDensityOverride != null ? resourceDensityOverride.floatValue() : resourceDensity);
                        propGenerator.setReclaimDensity(reclaimDensity);
                    } else if (resourceDensityOverride != null) {
                        resourceGenerator.setResourceDensity(resourceDensityOverride.floatValue());
                    }
                } else if (resourceDensityOverride != null) {
                    resourceGenerator.setResourceDensity(resourceDensityOverride.floatValue());
                }
            } catch (Throwable th) {
                throw new MatchException(th.toString(), th);
            }
        } else if (resourceDensityOverride != null) {
            resourceGenerator.setResourceDensity(resourceDensityOverride.floatValue());
        }
        if (mexesPerPlayerOverride > 0) {
            resourceGenerator.setMexesPerPlayer(mexesPerPlayerOverride);
        }
        if (hydroCountOverride >= 0) {
            resourceGenerator.setHydroCount(hydroCountOverride);
        }
        List<Pipeline.Entry> placementPipelineEntries = Pipeline.run(pipeline2 -> {
            pipeline2.setDebug(debug);
            pipeline2.setVisualize(visualize);
        }, () -> {
            textureGenerator.initialize(map, random.split(), generatorParameters, new SymmetrySettings(Symmetry.NONE), actualTerrainGenerator);
            resourceGenerator.initialize(map, random.split(), generatorParameters, symmetrySettings, actualTerrainGenerator);
            propGenerator.initialize(map, random.split(), generatorParameters, symmetrySettings, actualTerrainGenerator);
            resourceGenerator.setupPipeline();
            textureGenerator.setupPipeline();
            propGenerator.setupPipeline();
        });
        Objects.requireNonNull(textureGenerator);
        CompletableFuture<Void> textureFuture = CompletableFuture.runAsync(textureGenerator::setTextures, PLACEMENT_EXECUTOR);
        Objects.requireNonNull(textureGenerator);
        CompletableFuture<Void> normalFuture = CompletableFuture.runAsync(textureGenerator::setCompressedDecals, PLACEMENT_EXECUTOR);
        Objects.requireNonNull(resourceGenerator);
        CompletableFuture<Void> resourcesFuture = CompletableFuture.runAsync(resourceGenerator::placeResources, PLACEMENT_EXECUTOR);
        Objects.requireNonNull(textureGenerator);
        CompletableFuture<Void> decalsFuture = CompletableFuture.runAsync(textureGenerator::placeDecals, PLACEMENT_EXECUTOR);
        Objects.requireNonNull(propGenerator);
        CompletableFuture<Void> propsFuture = resourcesFuture.thenRunAsync(propGenerator::placeProps, (Executor) PLACEMENT_EXECUTOR);
        Objects.requireNonNull(propGenerator);
        CompletableFuture<Void> unitsFuture = resourcesFuture.thenRunAsync(propGenerator::placeUnits, (Executor) PLACEMENT_EXECUTOR);
        Objects.requireNonNull(textureGenerator);
        CompletableFuture<Void> previewFuture = propsFuture.thenRunAsync(textureGenerator::generatePreview, (Executor) PLACEMENT_EXECUTOR);
        CompletableFuture.allOf(textureFuture, previewFuture, resourcesFuture, decalsFuture, propsFuture, unitsFuture, normalFuture).thenRunAsync(() -> {
            Objects.requireNonNull(map);
            DebugUtil.timedRun("com.faforever.neroxis.map.generator", "setHeights", map::setHeights);
        }, (Executor) PLACEMENT_EXECUTOR).join();
        return new GenerationResults(map, symmetrySettings, terrainStyle, propStyle, biomeName, resourceStyle, terrainPipelineEntries, placementPipelineEntries);
    }

    private static float getSpawnSeparation(GeneratorParameters generatorParameters, RandomGenerator random) {
        int mapSize = generatorParameters.mapSize();
        int numTeams = generatorParameters.numTeams();
        int spawnCount = generatorParameters.spawnCount();
        if (numTeams < 2) {
            return (mapSize / spawnCount) * 1.5f;
        }
        if (numTeams == 2) {
            return random.nextInt((mapSize / 4) - (mapSize / 16)) + (mapSize / 16.0f);
        }
        if (numTeams < 8) {
            return random.nextInt(((mapSize / 2) / numTeams) - (mapSize / 16)) + (mapSize / 16.0f);
        }
        return SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
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
        return StrictMath.min(mapSize / numTeams, PreviewGenerator.PREVIEW_SIZE);
    }
}

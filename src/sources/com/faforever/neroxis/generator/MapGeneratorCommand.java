package com.faforever.neroxis.generator;

import com.faforever.neroxis.biomes.BiomeName;
import com.faforever.neroxis.cli.DebugMixin;
import com.faforever.neroxis.cli.OutputFolderMixin;
import com.faforever.neroxis.cli.VersionProvider;
import com.faforever.neroxis.cli.VisualizeMixin;
import com.faforever.neroxis.exporter.MapExporter;
import com.faforever.neroxis.exporter.SCMapExporter;
import com.faforever.neroxis.exporter.ScriptGenerator;
import com.faforever.neroxis.exporter.bar.BarExportOptions;
import com.faforever.neroxis.exporter.bar.BarMapExporter;
import com.faforever.neroxis.generator.cli.BasicOptions;
import com.faforever.neroxis.generator.cli.CustomStyleOptions;
import com.faforever.neroxis.generator.cli.GenerationOptions;
import com.faforever.neroxis.generator.cli.SpecifiedOptions;
import com.faforever.neroxis.generator.cli.StyleOptions;
import com.faforever.neroxis.generator.cli.TopLevelOptions;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.generator.util.HasParameterConstraints;
import com.faforever.neroxis.generator.util.serial.GeneratedMapNameEncoder;
import com.faforever.neroxis.generator.util.serial.GeneratorParseOutput;
import com.faforever.neroxis.generator.util.serial.MapNameParameters;
import com.faforever.neroxis.generator.util.serial.MapStyle;
import com.faforever.neroxis.generator.util.serial.PropStyle;
import com.faforever.neroxis.generator.util.serial.ResourceStyle;
import com.faforever.neroxis.generator.util.serial.TerrainStyle;
import com.faforever.neroxis.generator.util.serial.Visibility;
import com.faforever.neroxis.map.DecalGroup;
import com.faforever.neroxis.map.Marker;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.util.DebugUtil;
import com.faforever.neroxis.util.FileUtil;
import com.faforever.neroxis.util.MathUtil;
import com.faforever.neroxis.util.Range;
import com.faforever.neroxis.util.SymmetrySelector;
import com.faforever.neroxis.util.vector.Vector2;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.SwitchBootstraps;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.SplittableRandom;
import java.util.concurrent.Callable;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/MapGeneratorCommand.class */
@CommandLine.Command(name = "generate", mixinStandardHelpOptions = true, description = {"Generates a map from scratch"}, versionProvider = VersionProvider.class, usageHelpAutoWidth = true, sortOptions = false, scope = CommandLine.ScopeType.INHERIT)
public class MapGeneratorCommand implements Callable<Integer> {
    private static final WeightedOptionsWithFallback<MapStyle.Predefined> RANDOM_MAP_STYLE_OPTIONS = WeightedOptionsWithFallback.of(MapStyle.Predefined.BASIC, (WeightedOption<MapStyle.Predefined>[]) new WeightedOption[]{WeightedOption.of(MapStyle.Predefined.BASIC, 2.0f), WeightedOption.of(MapStyle.Predefined.BIG_ISLANDS, 1.0f), WeightedOption.of(MapStyle.Predefined.CENTER_LAKE, 1.0f), WeightedOption.of(MapStyle.Predefined.DROP_PLATEAU, 0.5f), WeightedOption.of(MapStyle.Predefined.FLOODED, 0.5f), WeightedOption.of(MapStyle.Predefined.HIGH_RECLAIM, 0.25f), WeightedOption.of(MapStyle.Predefined.LAND_BRIDGE, 2.0f), WeightedOption.of(MapStyle.Predefined.LITTLE_MOUNTAIN, 1.0f), WeightedOption.of(MapStyle.Predefined.LOW_MEX, 0.5f), WeightedOption.of(MapStyle.Predefined.MOUNTAIN_RANGE, 1.0f), WeightedOption.of(MapStyle.Predefined.MULTILEVEL, 1.0f), WeightedOption.of(MapStyle.Predefined.ONE_ISLAND, 1.0f), WeightedOption.of(MapStyle.Predefined.SMALL_ISLANDS, 1.0f), WeightedOption.of(MapStyle.Predefined.VALLEY, 1.0f), WeightedOption.of(MapStyle.Predefined.RIVERS, 0.25f), WeightedOption.of(MapStyle.Predefined.RIVERS_AND_OCEANS, 0.25f), WeightedOption.of(MapStyle.Predefined.FRACTAL_LAND, 1.0f), WeightedOption.of(MapStyle.Predefined.FRACTAL_PLATEAU, 0.25f), WeightedOption.of(MapStyle.Predefined.FRACTAL_NAVY, 0.75f), WeightedOption.of(MapStyle.Predefined.SETONISH, 1.0f), WeightedOption.of(MapStyle.Predefined.FORREST_SOMETHING, 0.01f)});

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(names = {"--parse"}, description = {"Only parse the options and return the parameters in json"})
    private boolean parse;

    @CommandLine.Option(names = {"--preview-path"}, order = 10000, description = {"Folder to save the map previews to"})
    private Path previewFolder;

    @CommandLine.Option(names = {"--bar"}, order = 10001, description = {"Export the map as a Beyond All Reason (Recoil engine) map archive instead of a Supreme Commander map. Use the BAR data/maps folder as --out-path"})
    private boolean bar;

    @CommandLine.Option(names = {"--bar-metal-uniform"}, order = 10007, description = {"Give every metal spot the same income like in Supreme Commander instead of varying it"})
    private boolean barMetalUniform;

    @CommandLine.Option(names = {"--bar-skirmish-script"}, order = 10008, description = {"Also write an engine start script for a skirmish against BARb AIs on the generated map to this file"})
    private Path barSkirmishScript;

    @CommandLine.Option(names = {"--bar-host-script"}, order = 10012, description = {"Also write a start script that hosts a multiplayer game on the generated map directly from this computer (UDP port 8452), see --bar-players"})
    private Path barHostScript;

    @CommandLine.Option(names = {"--bar-coop"}, order = 10014, description = {"Put the human players of the hosted game on the same team instead of spreading them over the teams"})
    private boolean barCoop;

    @CommandLine.Option(names = {"--bar-password"}, order = 10015, description = {"Password the joining players of the hosted game need"})
    private String barPassword;

    @CommandLine.Option(names = {"--bar-name"}, order = 10009, description = {"Name of the map in BAR instead of Neroxis_<code>, the version is appended"})
    private String barName;

    @CommandLine.Option(names = {"--bar-mexes"}, order = 10018, description = {"How many metal spots the map gets, 0 (few) to 1 (many), instead of what the style decides. Not part of the map name: the same map is only rebuilt with the same option"})
    private Float barMexes;

    @CommandLine.Option(names = {"--bar-start-mexes"}, order = 10019, description = {"Metal spots next to every start position, 1 to 5 (default: the generator picks 3 to 5). Not part of the map name"})
    private int barStartMexes;

    @CommandLine.Option(names = {"--bar-geos"}, order = 10023, description = {"Geothermal vents on the whole map, 0 for none (default: one for every two players; Supreme Commander maps have one per player). Rounded down to the symmetry of the map. Not part of the map name"})
    private Integer barGeos;

    @CommandLine.Option(names = {"--bar-features"}, order = 10017, description = {"Folder with a checkout of github.com/beyond-all-reason/mapfeatures. With it the maps get trees that fit the biome (pines, birches, palms, cacti, dead trees) which are copied into the map, without it fir trees"})
    private Path barFeatures;
    private final RandomGenerator random = new SplittableRandom();

    @CommandLine.ArgGroup
    private TopLevelOptions topLevelOptions = new TopLevelOptions();

    @CommandLine.Mixin
    private DebugMixin debugMixin = new DebugMixin();

    @CommandLine.Mixin
    private VisualizeMixin visualizeMixin = new VisualizeMixin();

    @CommandLine.Mixin
    private OutputFolderMixin outputFolderMixin = new OutputFolderMixin();

    @CommandLine.Option(names = {"--bar-scale"}, order = 10002, defaultValue = "2", description = {"BAR heightmap squares per supcom heightmap cell. 2 turns a 10km (512) map into a 16x16 BAR map (default: ${DEFAULT-VALUE})"})
    private int barScale = 2;

    @CommandLine.Option(names = {"--bar-height-scale"}, order = 10003, defaultValue = "0.7", description = {"Vertical scale of the BAR terrain relative to the horizontal scale. Lower values make ramps easier to climb for vehicles (default: ${DEFAULT-VALUE})"})
    private float barHeightScale = 0.7f;

    @CommandLine.Option(names = {"--bar-metal"}, order = 10004, defaultValue = "2.0", description = {"Metal income of the metal spots next to the start positions (default: ${DEFAULT-VALUE})"})
    private float barMetal = 2.0f;

    @CommandLine.Option(names = {"--bar-metal-min"}, order = 10005, defaultValue = "2.0", description = {"Lowest metal income a metal spot can have, BAR supports down to 0.5 (default: ${DEFAULT-VALUE})"})
    private float barMetalMin = 2.0f;

    @CommandLine.Option(names = {"--bar-metal-max"}, order = 10006, defaultValue = "3.5", description = {"Highest metal income a metal spot can have, BAR supports up to 10 but more than 3.5 is hard to balance (default: ${DEFAULT-VALUE})"})
    private float barMetalMax = 3.5f;

    @CommandLine.Option(names = {"--bar-players"}, order = 10013, split = ",", description = {"Names of the human players of the hosted game, the host first. The other start positions get AIs"})
    private List<String> barPlayers = List.of();

    @CommandLine.Option(names = {"--bar-version"}, order = 10010, defaultValue = "1.0", description = {"Version of a map with a custom name (default: ${DEFAULT-VALUE})"})
    private String barVersion = "1.0";

    @CommandLine.Option(names = {"--bar-author"}, order = 10011, defaultValue = "Neroxis Map Generator", description = {"Author in the map info (default: ${DEFAULT-VALUE})"})
    private String barAuthor = "Neroxis Map Generator";

    @CommandLine.Option(names = {"--bar-reclaim"}, order = 10016, defaultValue = "1.0", description = {"Amount of metal in rocks and wrecks: 1 is about 600 metal per start position which suits BAR, 0 none, 8 or more is like Supreme Commander (default: ${DEFAULT-VALUE})"})
    private float barReclaim = 1.0f;

    @CommandLine.Option(names = {"--bar-wind-min"}, order = 10020, defaultValue = "3", description = {"Lowest wind speed of the map (default: ${DEFAULT-VALUE})"})
    private int barWindMin = 3;

    @CommandLine.Option(names = {"--bar-wind-max"}, order = 10021, defaultValue = "16", description = {"Highest wind speed of the map (default: ${DEFAULT-VALUE})"})
    private int barWindMax = 16;

    @CommandLine.Option(names = {"--bar-tidal"}, order = 10022, defaultValue = "18", description = {"Tidal strength, the energy of a tidal generator (default: ${DEFAULT-VALUE})"})
    private int barTidal = 18;

    static void main(String[] args) {
        System.exit(((Integer) DebugUtil.timedRun("Execution", () -> {
            return Integer.valueOf(execute(args));
        })).intValue());
    }

    public static int execute(String[] args) {
        CommandLine commandLine = new CommandLine(new MapGeneratorCommand());
        commandLine.setAbbreviatedOptionsAllowed(true);
        commandLine.setUnmatchedArgumentsAllowed(true);
        return commandLine.execute(args);
    }

    static GenerationResults generate(MapNameParameters mapNameParameters, boolean debug, boolean visualize) {
        return generate(mapNameParameters, debug, visualize, true);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    /* JADX WARN: Code duplicated, block: B:11:0x0039  */
    /* JADX WARN: Code duplicated, block: B:62:0x0229  */
    static GenerationResults generate(MapNameParameters mapNameParameters, boolean debug, boolean visualize, boolean padToPowerOfTwo) throws MatchException {
        MapStyle mapStyle;
        SymmetrySettings symmetrySettings;
        Visibility visibility;
        SymmetrySettings symmetrySettings2;
        SplittableRandom splittableRandomCreateRandom = mapNameParameters.createRandom();
        MapNameParameters.Mode mode = mapNameParameters.mode();
        if (!(mode instanceof MapNameParameters.Casual)) {
            mapStyle = RANDOM_MAP_STYLE_OPTIONS.select(splittableRandomCreateRandom.split(), mapStyle1 -> {
                return mapStyle1.parameterConstraints().matches(mapNameParameters);
            });
        } else {
            MapNameParameters.Casual casual = (MapNameParameters.Casual) mode;
            try {
                casual.terrainSymmetry();
                MapStyle specifiedMapStyle = casual.mapStyle();
                if (specifiedMapStyle != null) {
                    mapStyle = specifiedMapStyle;
                } else {
                    mapStyle = RANDOM_MAP_STYLE_OPTIONS.select(splittableRandomCreateRandom.split(), mapStyle2 -> {
                        return mapStyle2.parameterConstraints().matches(mapNameParameters);
                    });
                }
            } catch (Throwable th) {
                throw new MatchException(th.toString(), th);
            }
        }
        String mapName = GeneratedMapNameEncoder.encode(mapNameParameters);
        MapNameParameters.Mode mode2 = mapNameParameters.mode();
        Objects.requireNonNull(mode2);
        int i = 0;
        while (true) {
            switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, MapNameParameters.Mode.class, Integer.TYPE), MapNameParameters.Casual.class).dynamicInvoker().invoke(mode2, i) /* invoke-custom */) {
                case 0:
                    MapNameParameters.Casual casual2 = (MapNameParameters.Casual) mode2;
                    Symmetry terrainSymmetry = casual2.terrainSymmetry();
                    casual2.mapStyle();
                    if (terrainSymmetry != null) {
                        symmetrySettings = SymmetrySelector.getSymmetrySettingsFromTerrainSymmetry(splittableRandomCreateRandom.split(), terrainSymmetry, mapNameParameters.spawnCount(), mapNameParameters.numTeams());
                    } else {
                        i = 1;
                    }
                    break;
                default:
                    symmetrySettings = SymmetrySelector.getSymmetrySettings(splittableRandomCreateRandom.split(), mapNameParameters.spawnCount(), mapNameParameters.numTeams());
                    break;
            }
        }
        SymmetrySettings symmetrySettings3 = symmetrySettings;
        if (mapStyle == MapStyle.Predefined.SETONISH) {
            switch (symmetrySettings3.terrainSymmetry()) {
                case ZX:
                    symmetrySettings2 = new SymmetrySettings(Symmetry.ZX, Symmetry.ZX, Symmetry.ZX);
                    break;
                case X:
                    symmetrySettings2 = new SymmetrySettings(Symmetry.X, Symmetry.X, Symmetry.X);
                    break;
                case Z:
                    symmetrySettings2 = new SymmetrySettings(Symmetry.Z, Symmetry.Z, Symmetry.Z);
                    break;
                default:
                    symmetrySettings2 = new SymmetrySettings(Symmetry.POINT2, Symmetry.XZ, Symmetry.POINT2);
                    break;
            }
            symmetrySettings3 = symmetrySettings2;
        }
        MapNameParameters.Mode mode3 = mapNameParameters.mode();
        Objects.requireNonNull(mode3);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, MapNameParameters.Mode.class, Integer.TYPE), MapNameParameters.Competitive.class).dynamicInvoker().invoke(mode3, 0) /* invoke-custom */) {
            case 0:
                MapNameParameters.Competitive competitive = (MapNameParameters.Competitive) mode3;
                competitive.generationTime();
                Visibility visibilityParameter = competitive.visibility();
                visibility = visibilityParameter;
                break;
            default:
                visibility = null;
                break;
        }
        Visibility visibility2 = visibility;
        GeneratorParameters generatorParameters = new GeneratorParameters(mapNameParameters.spawnCount(), mapNameParameters.mapSize(), mapNameParameters.numTeams(), mapStyle, symmetrySettings3, visibility2);
        long startTime = System.currentTimeMillis();
        GenerationResults generationResults = MapGenerator.generate(generatorParameters, splittableRandomCreateRandom.split(), debug && mapNameParameters.allowDebug(), visualize && mapNameParameters.allowDebug());
        SCMap map = generationResults.map();
        StringBuilder descriptionBuilder = new StringBuilder();
        MapNameParameters.Mode mode4 = mapNameParameters.mode();
        if (mode4 instanceof MapNameParameters.Competitive) {
            MapNameParameters.Competitive competitive2 = (MapNameParameters.Competitive) mode4;
            long generationTime = competitive2.generationTime();
            if (1 == 0) {
                descriptionBuilder.append("Seed: ").append(mapNameParameters.seed()).append("\n");
                descriptionBuilder.append(mapNameParameters).append("\n");
                descriptionBuilder.append("Style: ").append(mapStyle.name()).append("\n");
                descriptionBuilder.append(generationResults.settingsToString()).append("\n");
            } else {
                competitive2.visibility();
                descriptionBuilder.append(String.format("Map originally generated at %s UTC\n", DateTimeFormatter.ofPattern("HH:mm:ss dd MMM uuuu").format(Instant.ofEpochSecond(generationTime).atZone(ZoneOffset.UTC))));
            }
        } else {
            descriptionBuilder.append("Seed: ").append(mapNameParameters.seed()).append("\n");
            descriptionBuilder.append(mapNameParameters).append("\n");
            descriptionBuilder.append("Style: ").append(mapStyle.name()).append("\n");
            descriptionBuilder.append(generationResults.settingsToString()).append("\n");
        }
        if (visibility2 == Visibility.UNEXPLORED) {
            map.setCartographicContourInterval(100);
            map.setCartographicDeepWaterColor(1);
            map.setCartographicMapContourColor(1);
            map.setCartographicMapShoreColor(1);
            map.setCartographicMapLandStartColor(1);
            map.setCartographicMapLandEndColor(1);
            descriptionBuilder.append("Use with the Unexplored Maps Mod for best experience");
        }
        map.setDescription(descriptionBuilder.toString().replace("\n", "\\r\\n"));
        int mapSize = map.getSize();
        int compatibleMapSize = (int) StrictMath.pow(2.0d, StrictMath.ceil(StrictMath.log(mapSize) / StrictMath.log(2.0d)));
        Vector2 boundOffset = new Vector2(compatibleMapSize / 2.0f, compatibleMapSize / 2.0f);
        if (padToPowerOfTwo) {
            map.changeMapSize(mapSize, compatibleMapSize, boundOffset);
        }
        map.addBlank(new Marker(mapName, new Vector2(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT)));
        map.addDecalGroup(new DecalGroup(mapName, List.of()));
        map.setName(mapName);
        map.setFolderName(mapName);
        map.setFilePrefix(mapName);
        ScriptGenerator.generateScript(map);
        System.out.printf("Map generation done: %d ms\n", Long.valueOf(System.currentTimeMillis() - startTime));
        return generationResults;
    }

    private static void save(GenerationResults generationResults, MapNameParameters mapNameParameters, Path outputPath, boolean debug) {
        SCMap map = generationResults.map();
        FileUtil.deleteRecursiveIfExists(outputPath.resolve(map.getFolderName()));
        System.out.printf("Saving map to %s%n", outputPath.resolve(map.getFolderName()).toAbsolutePath());
        try {
            long startTime = System.currentTimeMillis();
            MapExporter.exportMap(outputPath, map, mapNameParameters.allowDebug());
            System.out.printf("File export done: %d ms\n", Long.valueOf(System.currentTimeMillis() - startTime));
            if (mapNameParameters.allowDebug()) {
                if (debug) {
                    long startTime2 = System.currentTimeMillis();
                    Path debugFolder = outputPath.resolve(map.getFolderName()).resolve("debug");
                    Files.createDirectory(debugFolder, new FileAttribute[0]);
                    SCMapExporter.exportSCMapString(outputPath, map.getName(), map);
                    System.out.printf("Debug export done: %d ms\n", Long.valueOf(System.currentTimeMillis() - startTime2));
                    Path path = debugFolder.resolve("pipelineMaskHashes.txt");
                    Files.deleteIfExists(path);
                    File outFile = path.toFile();
                    FileOutputStream out = new FileOutputStream(outFile);
                    try {
                        generationResults.writePipelines(out);
                        out.close();
                    } catch (Throwable th) {
                        try {
                            out.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                }
                System.out.println(mapNameParameters);
                System.out.println(generationResults.settingsToString());
            }
        } catch (IOException e) {
            System.err.println("Error while saving the map.");
            e.printStackTrace();
        }
    }

    private void saveBar(GenerationResults generationResults, Path outputPath, int numTeamsForBar) {
        try {
            long startTime = System.currentTimeMillis();
            BarExportOptions barExportOptions = new BarExportOptions(this.barScale, this.barHeightScale, this.barMetal, this.barMetalMin, this.barMetalMax, this.barMetalUniform, numTeamsForBar, this.barName, this.barVersion, this.barAuthor, this.barReclaim, this.barFeatures, Math.min(this.barWindMin, this.barWindMax), Math.max(this.barWindMin, this.barWindMax), this.barTidal);
            Path archivePath = BarMapExporter.exportMap(outputPath, generationResults.map(), barExportOptions, this.barSkirmishScript, this.barHostScript, this.barPlayers, this.barCoop, this.barPassword);
            System.out.printf("Saved BAR map to %s%n", archivePath.toAbsolutePath());
            System.out.printf("BAR export done: %d ms%n", Long.valueOf(System.currentTimeMillis() - startTime));
            System.out.println(generationResults.settingsToString());
        } catch (IOException e) {
            System.err.println("Error while saving the BAR map.");
            e.printStackTrace();
        }
    }

    @CommandLine.Command(name = "styles", aliases = {"--styles"}, description = {"Prints the map styles available"}, versionProvider = VersionProvider.class, usageHelpAutoWidth = true)
    private void printStyles() {
        System.out.println((String) Arrays.stream(MapStyle.Predefined.values()).map((v0) -> {
            return v0.toString();
        }).collect(Collectors.joining("\n")));
    }

    @CommandLine.Command(name = "terrain-styles", aliases = {"--terrain-styles"}, description = {"Prints the terrain styles available"}, versionProvider = VersionProvider.class, usageHelpAutoWidth = true)
    private void printTerrainStyles() {
        System.out.println((String) Arrays.stream(TerrainStyle.values()).map((v0) -> {
            return v0.toString();
        }).collect(Collectors.joining("\n")));
    }

    @CommandLine.Command(name = "biomes", aliases = {"--texture-styles", "--biomes"}, description = {"Prints the biomes available"}, versionProvider = VersionProvider.class, usageHelpAutoWidth = true)
    private void printBiomes() {
        System.out.println((String) Arrays.stream(BiomeName.values()).map((v0) -> {
            return v0.toString();
        }).collect(Collectors.joining("\n")));
    }

    @CommandLine.Command(name = "resource-styles", aliases = {"--resource-styles"}, description = {"Prints the resource styles available"}, versionProvider = VersionProvider.class, usageHelpAutoWidth = true)
    private void printResourceStyles() {
        System.out.println((String) Arrays.stream(ResourceStyle.values()).map((v0) -> {
            return v0.toString();
        }).collect(Collectors.joining("\n")));
    }

    @CommandLine.Command(name = "prop-styles", aliases = {"--prop-styles"}, description = {"Prints the prop styles available"}, versionProvider = VersionProvider.class, usageHelpAutoWidth = true)
    private void printPropStyles() {
        System.out.println((String) Arrays.stream(PropStyle.values()).map((v0) -> {
            return v0.toString();
        }).collect(Collectors.joining("\n")));
    }

    @CommandLine.Command(name = "symmetries", aliases = {"--symmetries"}, description = {"Prints the terrain symmetries available"}, versionProvider = VersionProvider.class, usageHelpAutoWidth = true)
    private void printSymmetries() {
        System.out.println((String) Arrays.stream(Symmetry.values()).map((v0) -> {
            return v0.toString();
        }).collect(Collectors.joining("\n")));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: picocli.CommandLine$ParameterException */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.concurrent.Callable
    public Integer call() throws Exception {
        Locale.setDefault(Locale.ROOT);
        int numToGenerate = this.topLevelOptions.getSpecifiedOptions().getNumToGenerate();
        for (int i = 0; i < numToGenerate; i++) {
            MapNameParameters mapNameParameters = createMapNameParameters();
            if (this.parse) {
                FileUtil.serialize(System.out, new GeneratorParseOutput(mapNameParameters));
                break;
            }
            if (this.bar) {
                mapNameParameters = fitToStyle(mapNameParameters);
            }
            System.out.println(GeneratedMapNameEncoder.encode(mapNameParameters));
            if (this.bar) {
                if (this.barMexes != null && (this.barMexes.floatValue() < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT || this.barMexes.floatValue() > 1.0f)) {
                    throw new CommandLine.ParameterException(this.spec.commandLine(), "--bar-mexes has to be between 0 and 1");
                }
                if (this.barStartMexes < 0 || this.barStartMexes > 5) {
                    throw new CommandLine.ParameterException(this.spec.commandLine(), "--bar-start-mexes has to be between 1 and 5");
                }
                if (this.barGeos != null && (this.barGeos.intValue() < 0 || this.barGeos.intValue() > 32)) {
                    throw new CommandLine.ParameterException(this.spec.commandLine(), "--bar-geos has to be between 0 and 32");
                }
                int geos = this.barGeos != null ? this.barGeos.intValue() : Math.max(1, mapNameParameters.spawnCount() / 2);
                MapGenerator.setBarOverrides(this.barMexes, this.barStartMexes, geos);
            }
            GenerationResults generationResults = generate(mapNameParameters, this.debugMixin.isDebug(), this.visualizeMixin.isVisualize(), !this.bar);
            if (this.bar) {
                saveBar(generationResults, this.outputFolderMixin.getOutputPath(), mapNameParameters.numTeams());
            } else {
                save(generationResults, mapNameParameters, this.outputFolderMixin.getOutputPath(), this.debugMixin.isDebug());
            }
            if (mapNameParameters.allowDebug() && this.previewFolder != null) {
                SCMapExporter.exportPreview(this.previewFolder, generationResults.map());
            }
        }
        return 0;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: picocli.CommandLine$ParameterException */
    private MapNameParameters fitToStyle(MapNameParameters parameters) throws CommandLine.ParameterException, MatchException {
        MapNameParameters.Mode mode = parameters.mode();
        if (mode instanceof MapNameParameters.Casual) {
            MapNameParameters.Casual casual = (MapNameParameters.Casual) mode;
            try {
                casual.terrainSymmetry();
                MapStyle style = casual.mapStyle();
                if (style instanceof MapStyle.Predefined) {
                    MapStyle.Predefined predefined = (MapStyle.Predefined) style;
                    ParameterConstraints constraints = predefined.parameterConstraints();
                    Range sizes = constraints.mapSizeRange();
                    Range teams = constraints.numTeamsRange();
                    Range spawns = constraints.spawnCountRange();
                    WeightedOptionsWithFallback<TerrainStyle> terrainStyleOptions = predefined.getTerrainStyleOptions();
                    if (terrainStyleOptions instanceof WeightedOptionsWithFallback.Single) {
                        TerrainStyle terrain = (TerrainStyle) ((WeightedOptionsWithFallback.Single) terrainStyleOptions).option();
                        if (terrain instanceof HasParameterConstraints) {
                            ParameterConstraints terrainConstraints = terrain.parameterConstraints();
                            sizes = Range.of(Math.max(sizes.min(), terrainConstraints.mapSizeRange().min()), Math.min(sizes.max(), terrainConstraints.mapSizeRange().max()));
                            teams = Range.of(Math.max(teams.min(), terrainConstraints.numTeamsRange().min()), Math.min(teams.max(), terrainConstraints.numTeamsRange().max()));
                        }
                    }
                    if (parameters.numTeams() != 0 && !teams.contains(parameters.numTeams())) {
                        CommandLine commandLine = this.spec.commandLine();
                        Object[] objArr = new Object[3];
                        objArr[0] = predefined.name();
                        objArr[1] = teams.min() == teams.max() ? ((int) teams.min()) + " teams" : ((int) teams.min()) + " to " + ((int) teams.max()) + " teams";
                        objArr[2] = Integer.valueOf(parameters.numTeams());
                        throw new CommandLine.ParameterException(commandLine, String.format("The style %s is made for %s, not for %d teams", objArr));
                    }
                    if (!spawns.contains(parameters.spawnCount())) {
                        throw new CommandLine.ParameterException(this.spec.commandLine(), String.format("The style %s is made for up to %d players, not for %d", predefined.name(), Integer.valueOf((int) spawns.max()), Integer.valueOf(parameters.spawnCount())));
                    }
                    if (sizes.contains(parameters.mapSize())) {
                        return parameters;
                    }
                    int fitted = ((Math.max((int) sizes.min(), Math.min((int) sizes.max(), parameters.mapSize())) + 63) / 64) * 64;
                    System.out.printf("The style %s is made for map sizes %d to %d: using %d instead of %d%n", predefined.name(), Integer.valueOf((int) sizes.min()), Integer.valueOf((int) sizes.max()), Integer.valueOf(fitted), Integer.valueOf(parameters.mapSize()));
                    return new MapNameParameters(parameters.seed(), parameters.spawnCount(), fitted, parameters.numTeams(), parameters.mode());
                }
            } catch (Throwable th) {
                throw new MatchException(th.toString(), th);
            }
        }
        return parameters;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: picocli.CommandLine$ParameterException */
    MapNameParameters createMapNameParameters() throws CommandLine.ParameterException {
        if (this.topLevelOptions.getSpecifiedMapName() != null) {
            try {
                return GeneratedMapNameEncoder.decode(this.topLevelOptions.getSpecifiedMapName());
            } catch (MapNameException e) {
                throw new CommandLine.ParameterException(this.spec.commandLine(), e.getMessage(), e);
            }
        }
        return createGeneratorParametersFromOptions(this.topLevelOptions.getSpecifiedOptions());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: picocli.CommandLine$ParameterException */
    private MapNameParameters createGeneratorParametersFromOptions(SpecifiedOptions specifiedOptions) throws CommandLine.ParameterException {
        MapNameParameters.Mode competitive;
        checkParameters(specifiedOptions);
        BasicOptions basicOptions = specifiedOptions.getBasicOptions();
        GenerationOptions generationOptions = specifiedOptions.getGenerationOptions();
        boolean competitive2 = generationOptions.getVisibilityOptions() != null;
        long seed = (competitive2 || specifiedOptions.getNumToGenerate() > 1 || generationOptions.getCasualOptions().getSeed() == null) ? this.random.nextLong() : generationOptions.getCasualOptions().getSeed().longValue();
        int spawnCount = basicOptions.getSpawnCount();
        int mapSize = basicOptions.getMapSize();
        int numTeams = basicOptions.getNumTeams();
        Symmetry terrainSymmetry = generationOptions.getCasualOptions().getTerrainSymmetry();
        Visibility visibility = (Visibility) Optional.ofNullable(generationOptions.getVisibilityOptions()).map((v0) -> {
            return v0.getVisibility();
        }).orElse(null);
        if (visibility == null) {
            competitive = new MapNameParameters.Casual(terrainSymmetry, createMapStyle(generationOptions));
        } else {
            competitive = new MapNameParameters.Competitive(Instant.now().getEpochSecond(), visibility);
        }
        MapNameParameters.Mode mode = competitive;
        return new MapNameParameters(seed, spawnCount, mapSize, numTeams, mode);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: picocli.CommandLine$ParameterException */
    private void checkParameters(SpecifiedOptions specifiedOptions) throws CommandLine.ParameterException {
        BasicOptions basicOptions = specifiedOptions.getBasicOptions();
        int numTeams = basicOptions.getNumTeams();
        int spawnCount = basicOptions.getSpawnCount();
        if (numTeams != 0 && spawnCount % numTeams != 0) {
            throw new CommandLine.ParameterException(this.spec.commandLine(), String.format("Spawn Count `%d` not a multiple of Num Teams `%d`", Integer.valueOf(spawnCount), Integer.valueOf(numTeams)));
        }
        GenerationOptions generationOptions = specifiedOptions.getGenerationOptions();
        Symmetry terrainSymmetry = generationOptions.getCasualOptions().getTerrainSymmetry();
        if (numTeams != 0 && terrainSymmetry != null && terrainSymmetry.getNumSymPoints() % numTeams != 0) {
            throw new CommandLine.ParameterException(this.spec.commandLine(), String.format("Terrain symmetry `%s` not compatible with Num Teams `%d`", terrainSymmetry, Integer.valueOf(numTeams)));
        }
    }

    private MapStyle createMapStyle(GenerationOptions generationOptions) {
        if (generationOptions.getCasualOptions().getStyleOptions() != null) {
            StyleOptions styleOptions = generationOptions.getCasualOptions().getStyleOptions();
            if (styleOptions.getCustomStyleOptions() != null) {
                return createCustomStyle(styleOptions.getCustomStyleOptions());
            }
            if (styleOptions.getPredefinedMapStyle() != null) {
                return styleOptions.getPredefinedMapStyle();
            }
            return null;
        }
        return null;
    }

    private MapStyle.Custom createCustomStyle(CustomStyleOptions customStyleOptions) {
        BiomeName biomeName;
        TerrainStyle terrainStyle;
        ResourceStyle resourceStyle;
        PropStyle propStyle;
        float reclaimDensity;
        float resourceDensity;
        if (customStyleOptions.getBiomeName() != null) {
            biomeName = customStyleOptions.getBiomeName();
        } else {
            biomeName = BiomeName.values()[this.random.nextInt(BiomeName.values().length)];
        }
        if (customStyleOptions.getTerrainStyle() != null) {
            terrainStyle = customStyleOptions.getTerrainStyle();
        } else {
            terrainStyle = TerrainStyle.values()[this.random.nextInt(TerrainStyle.values().length)];
        }
        if (customStyleOptions.getResourceStyle() != null) {
            resourceStyle = customStyleOptions.getResourceStyle();
        } else {
            resourceStyle = ResourceStyle.values()[this.random.nextInt(ResourceStyle.values().length)];
        }
        if (customStyleOptions.getPropStyle() != null) {
            propStyle = customStyleOptions.getPropStyle();
        } else {
            propStyle = PropStyle.values()[this.random.nextInt(PropStyle.values().length)];
        }
        if (customStyleOptions.getReclaimDensity() != null) {
            reclaimDensity = customStyleOptions.getReclaimDensity().floatValue();
        } else {
            reclaimDensity = MathUtil.normalizeBin(this.random.nextInt(GeneratedMapNameEncoder.NUM_BINS), GeneratedMapNameEncoder.NUM_BINS);
        }
        if (customStyleOptions.getResourceDensity() != null) {
            resourceDensity = customStyleOptions.getResourceDensity().floatValue();
        } else {
            resourceDensity = MathUtil.normalizeBin(this.random.nextInt(GeneratedMapNameEncoder.NUM_BINS), GeneratedMapNameEncoder.NUM_BINS);
        }
        return new MapStyle.Custom(terrainStyle, biomeName, propStyle, resourceStyle, reclaimDensity, resourceDensity);
    }
}

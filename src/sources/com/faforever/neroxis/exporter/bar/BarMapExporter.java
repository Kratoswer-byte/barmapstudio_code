package com.faforever.neroxis.exporter.bar;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.Army;
import com.faforever.neroxis.map.Group;
import com.faforever.neroxis.map.Marker;
import com.faforever.neroxis.map.Prop;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Spawn;
import com.faforever.neroxis.map.Unit;
import com.faforever.neroxis.util.dds.DDSHeader;
import com.faforever.neroxis.util.serial.biome.TerrainMaterials;
import com.faforever.neroxis.util.vector.Vector3;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/bar/BarMapExporter.class */
public final class BarMapExporter {
    private static final int ELMOS_PER_SQUARE = 8;
    private static final int METAL_CELL_ELMOS = 16;
    private static final int METAL_CELLS_PER_SPOT = 12;
    private static final float EXTRACTS_METAL = 0.001f;
    private static final int NOISE_SIZE = 512;
    private static final float DRY_MAP_MIN_HEIGHT = 48.0f;
    private static final float MIN_METAL_SPOT_SPACING = 112.0f;
    private static final int SPLAT_TEXELS_PER_SQUARE = 2;
    private static final float BEACH_WIDTH = 90.0f;
    private static final float WORN_GROUND_RADIUS = 120.0f;
    private static final float NOISE_DEVIATION = 0.23f;
    private static final float TARGET_LUMINANCE = 0.38f;
    private static final float TARGET_SATURATION = 0.16f;
    private static final float MAX_SATURATION = 0.3f;
    public static final int HOST_PORT = 8452;
    private static final float VEHICLE_MAX_GRADIENT = 0.48f;
    private static final float BOT_MAX_GRADIENT = 1.37f;
    private static final float MAX_WADING_DEPTH = 22.0f;
    private static final float MEX_MARK_RADIUS = 36.0f;
    private static final float GEO_MARK_RADIUS = 34.0f;
    private static final float RECLAIM_METAL_PER_PLAYER = 600.0f;
    private final SCMap map;
    private final BufferedImage customTexture;
    private BarExportOptions options;
    private final int faSize;
    private final int faWidth;
    private final int faHeight;
    private final int mapX;
    private final int mapY;
    private final int textureWidth;
    private final int textureHeight;
    private final float elmosPerCell;
    private final float waterElevation;
    private float[] faHeights;
    private final float[][] layerWeights;
    private final int layerWidth;
    private final int layerHeight;
    private final float[][] layerColors;
    private final float[] heights;
    private final float[] noise;
    private final float[] sunDirection;
    private DetailTextures.Material groundMaterial;
    private final int[] layerKinds;
    private boolean snowGround;
    private float lowGround;
    private float minHeight;
    private float maxHeight;
    public static final String GENERATED_PREFIX = "Map_";
    public static final String OLD_GENERATED_PREFIX = "Neroxis_";
    private static final String[] SMALL_WRECKS = {"armstump_dead", "corraid_dead", "armpw_dead", "corak_dead", "armflash_dead", "corgator_dead", "armham_dead", "corthud_dead"};
    private static final String[] MEDIUM_WRECKS = {"armbull_dead", "correap_dead", "armzeus_dead", "corcan_dead"};
    private static final String[] LARGE_WRECKS = {"armbull_dead", "correap_dead", "corcan_dead", "armzeus_dead"};
    private static final Map<String, Integer> WRECK_METAL = Map.ofEntries(Map.entry("armstump_dead", 152), Map.entry("corraid_dead", 167), Map.entry("armpw_dead", 29), Map.entry("corak_dead", 22), Map.entry("armflash_dead", 71), Map.entry("corgator_dead", 77), Map.entry("armham_dead", 79), Map.entry("corthud_dead", 96), Map.entry("armbull_dead", 549), Map.entry("correap_dead", 350), Map.entry("armzeus_dead", 214), Map.entry("corcan_dead", 339), Map.entry("armpship_dead", 195), Map.entry("corpship_dead", 205), Map.entry("armcrus_dead", 500), Map.entry("corcrus_dead", 500), Map.entry("armroy_dead", 440), Map.entry("corroy_dead", 480), Map.entry("armsolar_dead", 75), Map.entry("corsolar_dead", 72), Map.entry("armllt_dead", 53), Map.entry("corllt_dead", 55), Map.entry("armwin_dead", 23), Map.entry("corwin_dead", 27), Map.entry("armmstor_dead", 198), Map.entry("cormstor_dead", 208));
    private static final String[] SHIP_WRECKS = {"armpship_dead", "corpship_dead", "armcrus_dead", "corcrus_dead", "armroy_dead", "corroy_dead"};
    private static final String[] BUILDING_WRECKS = {"armsolar_dead", "corsolar_dead", "armllt_dead", "corllt_dead", "armwin_dead", "corwin_dead", "armmstor_dead", "cormstor_dead"};
    private static final List<String> T1_LAND = List.of("UEL0201", "URL0107", "UAL0201", "XSL0201");
    private static final List<String> T3_LAND = List.of("XEL0305", "UEL0303", "URL0303", "XRL0305", "UAL0303", "XSL0303");
    private final List<float[]> mexPositions = new ArrayList();
    private final List<float[]> geoPositions = new ArrayList();
    private List<BiomeFlora.PlantSet> plantSets = List.of();
    private float[] shoreDistance = new float[0];
    private float[] rockColor = {0.42f, 0.4f, TARGET_LUMINANCE};
    private float[] sandColor = {0.62f, 0.56f, 0.42f};
    private float[] dirtColor = {0.4f, 0.33f, 0.24f};
    private float highGround = 1.0f;

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/bar/BarMapExporter$ReclaimFeature.class */
    private static record ReclaimFeature(String name, float x, float z, float rotation, int metal, float order) {}

    private BarMapExporter(SCMap map, BarExportOptions options) {
        this.map = map;
        this.customTexture = map.getCustomTexture();
        this.options = options;
        this.faSize = map.getSize();
        int fW = map.getWidth();
        int fH = map.getHeight();
        this.faWidth = fW > 0 ? fW : this.faSize;
        this.faHeight = fH > 0 ? fH : this.faSize;
        this.mapX = this.faWidth * options.squaresPerCell();
        this.mapY = this.faHeight * options.squaresPerCell();
        if (this.mapX % 128 != 0 || this.mapY % 128 != 0) {
            throw new IllegalArgumentException("The map size %dx%d can not be converted to a BAR map, size * %d must be a multiple of 128".formatted(Integer.valueOf(this.faWidth), Integer.valueOf(this.faHeight), Integer.valueOf(options.squaresPerCell())));
        }
        this.textureWidth = this.mapX * 8;
        this.textureHeight = this.mapY * 8;
        this.elmosPerCell = 8 * options.squaresPerCell();
        this.waterElevation = map.getBiome().waterSettings().waterPresent() ? map.getBiome().waterSettings().elevation() : Float.NaN;
        this.faHeights = readHeights(map.getHeightmap(), map.getHeightMapScale());
        this.layerWidth = map.getTextureMasksLow().getWidth();
        this.layerHeight = map.getTextureMasksLow().getHeight();
        this.layerWeights = readLayerWeights(map.getTextureMasksLow(), map.getTextureMasksHigh());
        this.layerColors = readLayerColors();
        boolean pbr = String.valueOf(map.getBiome().name()).equalsIgnoreCase("sunset");
        this.layerKinds = pbr ? new int[]{0, 2, 0, 0, 1, 1, 0, 0} : new int[]{0, 0, 1, 1, 0, 0, 2, 2};
        this.noise = createNoise();
        this.heights = new float[(this.mapX + 1) * (this.mapY + 1)];
        Vector3 sun = map.getBiome().lightingSettings().sunDirection();
        float sunLength = (float) StrictMath.sqrt((sun.x() * sun.x()) + (sun.y() * sun.y()) + (sun.z() * sun.z()));
        this.sunDirection = sunLength > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? new float[]{sun.x() / sunLength, sun.y() / sunLength, sun.z() / sunLength} : new float[]{0.5f, 0.7f, -0.5f};
        this.sunDirection[2] = -Math.max(0.45f, Math.abs(this.sunDirection[2]));
        this.sunDirection[1] = Math.max(0.55f, Math.min(0.8f, this.sunDirection[1]));
        normalize(this.sunDirection);
    }

    public static Path exportMap(Path folderPath, SCMap map, BarExportOptions options) throws IOException {
        return new BarMapExporter(map, options).export(folderPath);
    }

    public static Path exportMap(Path folderPath, SCMap map, BarExportOptions options, Path skirmishScriptPath) throws IOException {
        return exportMap(folderPath, map, options, skirmishScriptPath, null, List.of(), false, null);
    }

    public static Path exportMap(Path folderPath, SCMap map, BarExportOptions options, Path skirmishScriptPath, Path hostScriptPath, List<String> players, boolean coop, String password) throws IOException {
        BarMapExporter exporter = new BarMapExporter(map, options);
        Path archivePath = exporter.export(folderPath);
        if (skirmishScriptPath != null) {
            writeScript(skirmishScriptPath, exporter.buildGameScript(List.of("Player"), true, false, null));
        }
        if (hostScriptPath != null) {
            if (players.isEmpty() || players.size() > map.getSpawnCount()) {
                throw new IllegalArgumentException("A hosted game needs between 1 and %d player names".formatted(Integer.valueOf(map.getSpawnCount())));
            }
            for (String player : players) {
                if (!player.matches("[A-Za-z0-9_\\[\\]]{2,24}")) {
                    throw new IllegalArgumentException("Player name '%s' may only contain letters, digits, _ [ ]".formatted(player));
                }
            }
            writeScript(hostScriptPath, exporter.buildGameScript(players, coop, true, password));
        }
        return archivePath;
    }

    private static void writeScript(Path path, String script) throws IOException {
        if (path.toAbsolutePath().getParent() != null) {
            Files.createDirectories(path.toAbsolutePath().getParent(), new FileAttribute[0]);
        }
        Files.writeString(path, script, new OpenOption[0]);
    }

    private String buildGameScript(List<String> players, boolean coop, boolean hosted, String password) {
        List<Spawn> spawns = new ArrayList<>(this.map.getSpawns());
        Map<Spawn, Integer> spawnTeams = resolveTeams(spawns);
        spawns.sort(Comparator.comparingInt(spawn -> {
            return ((Integer) spawnTeams.getOrDefault(spawn, 0)).intValue();
        }));
        List<Integer> teamIds = spawns.stream().map(spawn2 -> {
            return (Integer) spawnTeams.getOrDefault(spawn2, 0);
        }).distinct().toList();
        int[] allyTeams = new int[spawns.size()];
        for (int i = 0; i < spawns.size(); i++) {
            allyTeams[i] = teamIds.indexOf(spawnTeams.getOrDefault(spawns.get(i), 0));
        }
        int[] playerOfSlot = new int[spawns.size()];
        Arrays.fill(playerOfSlot, -1);
        for (int player = 0; player < players.size(); player++) {
            int wantedTeam = coop ? -1 : player % teamIds.size();
            int slot = -1;
            for (int i2 = 0; i2 < spawns.size() && slot < 0; i2++) {
                if (playerOfSlot[i2] < 0 && (wantedTeam < 0 || allyTeams[i2] == wantedTeam)) {
                    slot = i2;
                }
            }
            for (int i3 = 0; i3 < spawns.size() && slot < 0; i3++) {
                if (playerOfSlot[i3] < 0) {
                    slot = i3;
                }
            }
            playerOfSlot[slot] = player;
        }
        StringBuilder script = new StringBuilder();
        Object[] objArr = new Object[5];
        objArr[0] = getDisplayName();
        objArr[1] = hosted ? "" : "127.0.0.1";
        objArr[2] = Integer.valueOf(hosted ? HOST_PORT : 0);
        objArr[3] = players.getFirst();
        objArr[4] = Integer.valueOf(players.size());
        script.append("[game]\n{\n\tgametype=rapid://byar:test;\n\tmapname=%s;\n\tishost=1;\n\thostip=%s;\n\thostport=%d;\n\tmyplayername=%s;\n\tstartpostype=2;\n\tnumplayers=%d;\n\t[modoptions]\n\t{\n\t\tdraft_mode=disabled;\n\t}\n".formatted(objArr));
        int player2 = 0;
        while (player2 < players.size()) {
            int slot2 = 0;
            for (int i4 = 0; i4 < spawns.size(); i4++) {
                if (playerOfSlot[i4] == player2) {
                    slot2 = i4;
                }
            }
            Object[] objArr2 = new Object[4];
            objArr2[0] = Integer.valueOf(player2);
            objArr2[1] = players.get(player2);
            objArr2[2] = Integer.valueOf(slot2);
            objArr2[3] = (!hosted || password == null || player2 <= 0) ? "" : "\t\tpassword=" + password + ";\n";
            script.append("\t[player%d]\n\t{\n\t\tname=%s;\n\t\tteam=%d;\n\t\tspectator=0;\n%s\t}\n".formatted(objArr2));
            player2++;
        }
        int aiCount = 0;
        for (int i5 = 0; i5 < spawns.size(); i5++) {
            if (playerOfSlot[i5] < 0) {
                script.append("\t[ai%d]\n\t{\n\t\tname=BARbarian %d;\n\t\tshortname=BARb;\n\t\tversion=stable;\n\t\tteam=%d;\n\t\thost=0;\n\t}\n".formatted(Integer.valueOf(aiCount), Integer.valueOf(aiCount + 1), Integer.valueOf(i5)));
                aiCount++;
            }
            Object[] objArr3 = new Object[4];
            objArr3[0] = Integer.valueOf(i5);
            objArr3[1] = Integer.valueOf(Math.max(0, playerOfSlot[i5]));
            objArr3[2] = Integer.valueOf(allyTeams[i5]);
            objArr3[3] = i5 % 2 == 0 ? "Armada" : "Cortex";
            script.append("\t[team%d]\n\t{\n\t\tteamleader=%d;\n\t\tallyteam=%d;\n\t\tside=%s;\n\t\thandicap=0;\n\t}\n".formatted(objArr3));
        }
        for (int allyTeam = 0; allyTeam < teamIds.size(); allyTeam++) {
            float left = 1.0f;
            float top = 1.0f;
            float right = 0.0f;
            float bottom = 0.0f;
            for (int i6 = 0; i6 < spawns.size(); i6++) {
                if (allyTeams[i6] == allyTeam) {
                    float x = spawns.get(i6).getPosition().x() / this.faWidth;
                    float z = spawns.get(i6).getPosition().z() / this.faHeight;
                    left = Math.min(left, x);
                    right = Math.max(right, x);
                    top = Math.min(top, z);
                    bottom = Math.max(bottom, z);
                }
            }
            script.append(String.format(Locale.ROOT, "\t[allyteam%d]\n\t{\n\t\tnumallies=0;\n\t\tstartrectleft=%.3f;\n\t\tstartrecttop=%.3f;\n\t\tstartrectright=%.3f;\n\t\tstartrectbottom=%.3f;\n\t}\n", Integer.valueOf(allyTeam), Float.valueOf(Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, left - 0.09f)), Float.valueOf(Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, top - 0.09f)), Float.valueOf(Math.min(1.0f, right + 0.09f)), Float.valueOf(Math.min(1.0f, bottom + 0.09f))));
        }
        script.append("}\n");
        return script.toString();
    }

    private Path export(Path folderPath) throws IOException {
        Files.createDirectories(folderPath, new FileAttribute[0]);
        String archiveName = toFileName(getDisplayName());
        Path archivePath = folderPath.resolve(archiveName + ".sdz");
        buildHeights();
        this.map.getMexes().forEach(mex -> {
            this.mexPositions.add(toElmos(mex));
        });
        mergeCloseMetalSpots();
        assignMetalValues();
        float maxFound = this.options.maxMetal();
        for (float[] mex : this.mexPositions) {
            maxFound = Math.max(maxFound, mex[2]);
        }
        if (maxFound > this.options.maxMetal()) {
            this.options = new BarExportOptions(
                this.options.squaresPerCell(),
                this.options.heightScale(),
                this.options.baseMetal(),
                this.options.minMetal(),
                maxFound,
                this.options.uniformMetal(),
                this.options.numTeams(),
                this.options.mapName(),
                this.options.mapVersion(),
                this.options.author(),
                this.options.reclaim(),
                this.options.featuresRepository(),
                this.options.minWind(),
                this.options.maxWind(),
                this.options.tidal()
            );
        }
        this.map.getHydros().forEach(hydro -> {
            this.geoPositions.add(toElmos(hydro));
        });
        List<String> featureTypes = new ArrayList<>();
        List<SmfWriter.Feature> features = buildFeatures(featureTypes);
        byte[] metalMap = buildMetalMap();
        short[] smfHeights = new short[this.heights.length];
        float heightRange = Math.max(this.maxHeight - this.minHeight, 1.0f);
        for (int i = 0; i < this.heights.length; i++) {
            smfHeights[i] = (short) Math.round(((this.heights[i] - this.minHeight) / heightRange) * 65535.0f);
        }
        BufferedImage minimapImage = renderMinimap();
        byte[] minimap = compressWithMips(minimapImage, 9);
        Files.deleteIfExists(archivePath);
        ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(archivePath, new OpenOption[0])));
        try {
            zip.setLevel(6);
            zip.putNextEntry(new ZipEntry("mapinfo.lua"));
            zip.write(buildMapInfo().getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
            zip.putNextEntry(new ZipEntry("maps/" + archiveName + ".smf"));
            SmfWriter.write(zip, this.map.getName().hashCode() & Integer.MAX_VALUE, this.mapX, this.mapY, this.minHeight, this.maxHeight, smfHeights, metalMap, buildGrassMap(), minimap, archiveName + ".smt", featureTypes, features);
            zip.closeEntry();
            zip.putNextEntry(new ZipEntry("maps/" + archiveName + ".smt"));
            writeTiles(zip);
            zip.closeEntry();
            DetailTextures.Material[] materials = getSplatMaterials();
            for (int i2 = 0; i2 < materials.length; i2++) {
                zip.putNextEntry(new ZipEntry("maps/" + archiveName + "_dnts" + (i2 + 1) + ".tga"));
                zip.write(DetailTextures.createTexture(materials[i2], this.map.getName().hashCode() + (i2 * 7919)));
                zip.closeEntry();
            }
            zip.putNextEntry(new ZipEntry("maps/" + archiveName + "_splats.dds"));
            zip.write(buildSplatDistribution());
            zip.closeEntry();
            Path featuresRepository = this.options.featuresRepository();
            if (featuresRepository != null && this.plantSets.stream().anyMatch((v0) -> {
                return v0.fromRepository();
            })) {
                for (Map.Entry<String, Path> file : BiomeFlora.collectFiles(this.plantSets, featuresRepository).entrySet()) {
                    zip.putNextEntry(new ZipEntry(file.getKey()));
                    Files.copy(file.getValue(), zip);
                    zip.closeEntry();
                }
            }
            // Scrittura oggetti ed entità custom, script di animazione e gadget spawner
            writeCustomEntities(zip);

            zip.putNextEntry(new ZipEntry("maps/" + archiveName + "_normals.tga"));
            zip.write(buildNormalsTexture());
            zip.closeEntry();
            zip.putNextEntry(new ZipEntry("maps/" + archiveName + "_specular.dds"));
            zip.write(buildSpecularTexture());
            zip.closeEntry();
            zip.putNextEntry(new ZipEntry("maps/" + archiveName + "_edge.dds"));
            zip.write(buildEdgeTexture(minimapImage));
            zip.closeEntry();
            BufferedImage previewImage = renderPreview(minimapImage);
            ByteArrayOutputStream previewBytes = new ByteArrayOutputStream();
            ImageIO.write(previewImage, "png", previewBytes);
            byte[] previewPngBytes = previewBytes.toByteArray();

            zip.putNextEntry(new ZipEntry("maps/" + archiveName + "_preview.png"));
            zip.write(previewPngBytes);
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry("preview.png"));
            zip.write(previewPngBytes);
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry("minimap.png"));
            zip.write(previewPngBytes);
            zip.closeEntry();

            zip.close();
            saveLobbyMinimaps(folderPath, archiveName, getDisplayName(), previewImage, previewPngBytes);
            System.out.printf(Locale.ROOT, "BAR map: %dx%d, heights %.0f to %.0f, %d metal spots, %d geo vents, %d features%n", Integer.valueOf(this.mapX / 64), Integer.valueOf(this.mapY / 64), Float.valueOf(this.minHeight), Float.valueOf(this.maxHeight), Integer.valueOf(this.mexPositions.size()), Integer.valueOf(this.geoPositions.size()), Integer.valueOf(features.size()));
            System.out.println("Metal spot incomes: " + describeMetal());
            return archivePath;
        } catch (Throwable th) {
            try {
                zip.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private static float[] readHeights(BufferedImage heightmap, float heightMapScale) {
        int width = heightmap.getWidth();
        int height = heightmap.getHeight();
        WritableRaster raster = heightmap.getRaster();
        float[] result = new float[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result[(y * width) + x] = raster.getSample(x, y, 0) * heightMapScale;
            }
        }
        return result;
    }

    private void buildHeights() {
        int faW = this.faWidth + 1;
        int faH = this.faHeight + 1;
        this.faHeights = cleanHeights(this.faHeights, faW, faH);
        float lowest = Float.MAX_VALUE;
        for (float faHeight : this.faHeights) {
            lowest = Math.min(lowest, faHeight);
        }
        float seaLevel = Float.isNaN(this.waterElevation) ? lowest - 4.0f : this.waterElevation;
        float verticalScale = this.elmosPerCell * this.options.heightScale();
        int squaresPerCell = this.options.squaresPerCell();
        IntStream.rangeClosed(0, this.mapY).parallel().forEach(y -> {
            float fy = y / (float) squaresPerCell;
            for (int x = 0; x <= this.mapX; x++) {
                float fx = x / (float) squaresPerCell;
                this.heights[(y * (this.mapX + 1)) + x] = (sampleCubic(this.faHeights, faW, faH, fx, fy) - seaLevel) * verticalScale;
            }
        });
        sharpenCliffs();
        addRidges();
        ensureVehiclePaths();
        gradeColors();
        buildShoreDistance();
        pickRockAndSandColors();
        this.minHeight = Float.MAX_VALUE;
        this.maxHeight = -3.4028235E38f;
        for (float height : this.heights) {
            this.minHeight = Math.min(this.minHeight, height);
            this.maxHeight = Math.max(this.maxHeight, height);
        }
        if (this.minHeight >= SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT && this.minHeight < DRY_MAP_MIN_HEIGHT) {
            float offset = DRY_MAP_MIN_HEIGHT - this.minHeight;
            for (int i = 0; i < this.heights.length; i++) {
                float[] fArr = this.heights;
                int i2 = i;
                fArr[i2] = fArr[i2] + offset;
            }
            this.minHeight += offset;
            this.maxHeight += offset;
        }
        this.minHeight = (float) Math.floor(this.minHeight);
        this.maxHeight = (float) Math.ceil(this.maxHeight);
    }

    private static float[] cleanHeights(float[] heights, int width, int height) {
        float[] median = new float[heights.length];
        IntStream.range(0, height).parallel().forEach(y -> {
            float[] window = new float[9];
            for (int x = 0; x < width; x++) {
                int n = 0;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int i = n;
                        n++;
                        window[i] = heights[(clamp(y + dy, height) * width) + clamp(x + dx, width)];
                    }
                }
                Arrays.sort(window);
                float center = heights[(y * width) + x];
                median[(y * width) + x] = Math.abs(center - window[4]) > 0.35f ? window[4] : center;
            }
        });
        float[] source = median;
        for (int pass = 0; pass < 2; pass++) {
            float[] input = source;
            float[] output = new float[heights.length];
            IntStream.range(0, height).parallel().forEach(y2 -> {
                for (int x = 0; x < width; x++) {
                    float center = input[(y2 * width) + x];
                    float sum = 0.0f;
                    float weights = 0.0f;
                    for (int dy = -2; dy <= 2; dy++) {
                        for (int dx = -2; dx <= 2; dx++) {
                            float value = input[(clamp(y2 + dy, height) * width) + clamp(x + dx, width)];
                            float difference = value - center;
                            float weight = (float) StrictMath.exp(((-((dx * dx) + (dy * dy))) / 3.4f) - ((difference * difference) / 1.3f));
                            sum += value * weight;
                            weights += weight;
                        }
                    }
                    output[(y2 * width) + x] = sum / weights;
                }
            });
            source = output;
        }
        float[] input2 = source;
        float[] result = new float[heights.length];
        IntStream.range(0, height).parallel().forEach(y3 -> {
            for (int x = 0; x < width; x++) {
                float sum = 0.0f;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        sum += input2[(clamp(y3 + dy, height) * width) + clamp(x + dx, width)] * (2 - Math.abs(dx)) * (2 - Math.abs(dy));
                    }
                }
                result[(y3 * width) + x] = sum / 16.0f;
            }
        });
        return result;
    }

    private void ensureVehiclePaths() {
        List<Spawn> spawns = this.map.getSpawns();
        if (spawns.size() < 2) {
            return;
        }
        int row = this.mapX + 1;
        float[] gradient = new float[this.mapX * this.mapY];
        float[] center = new float[this.mapX * this.mapY];
        IntStream.range(0, this.mapY).parallel().forEach(z -> {
            for (int x = 0; x < this.mapX; x++) {
                float h00 = this.heights[(z * row) + x];
                float h01 = this.heights[(z * row) + x + 1];
                float h10 = this.heights[((z + 1) * row) + x];
                float h11 = this.heights[((z + 1) * row) + x + 1];
                float dx = (((h01 + h11) - h00) - h10) / 16.0f;
                float dz = (((h10 + h11) - h00) - h01) / 16.0f;
                gradient[(z * this.mapX) + x] = (float) StrictMath.sqrt((dx * dx) + (dz * dz));
                center[(z * this.mapX) + x] = (((h00 + h01) + h10) + h11) / 4.0f;
            }
        });
        if (!startsConnected(gradient, center, 1.0f, BOT_MAX_GRADIENT) || startsConnected(gradient, center, 1.0f, VEHICLE_MAX_GRADIENT)) {
            return;
        }
        for (float factor : new float[]{0.93f, 0.86f, 0.8f, 0.74f, 0.68f, 0.62f}) {
            if (startsConnected(gradient, center, factor, VEHICLE_MAX_GRADIENT)) {
                for (int i = 0; i < this.heights.length; i++) {
                    float[] fArr = this.heights;
                    int i2 = i;
                    fArr[i2] = fArr[i2] * factor;
                }
                System.out.printf(Locale.ROOT, "Flattened the terrain to %.0f%% so vehicles can reach every start position%n", Float.valueOf(factor * 100.0f));
                return;
            }
        }
    }

    private boolean startsConnected(float[] gradient, float[] center, float factor, float maxGradient) {
        int i;
        int i2;
        boolean[] visited = new boolean[gradient.length];
        int[] queue = new int[gradient.length];
        List<Spawn> spawns = this.map.getSpawns();
        int start = toSquareIndex((Spawn) spawns.getFirst());
        int head = 0;
        int tail = 0 + 1;
        queue[0] = start;
        visited[start] = true;
        while (head < tail) {
            int i3 = head;
            head++;
            int index = queue[i3];
            int x = index % this.mapX;
            int z = index / this.mapX;
            int direction = 0;
            while (direction < 4) {
                if (direction == 0) {
                    i = 1;
                } else {
                    i = direction == 1 ? -1 : 0;
                }
                int nx = x + i;
                if (direction == 2) {
                    i2 = 1;
                } else {
                    i2 = direction == 3 ? -1 : 0;
                }
                int nz = z + i2;
                if (nx >= 0 && nz >= 0 && nx < this.mapX && nz < this.mapY) {
                    int next = (nz * this.mapX) + nx;
                    if (!visited[next] && gradient[next] * factor <= maxGradient && center[next] * factor > -22.0f) {
                        visited[next] = true;
                        int i4 = tail;
                        tail++;
                        queue[i4] = next;
                    }
                }
                direction++;
            }
        }
        return spawns.stream().allMatch(spawn -> {
            return visited[toSquareIndex(spawn)];
        });
    }

    private int toSquareIndex(Spawn spawn) {
        int x = Math.max(0, Math.min(this.mapX - 1, Math.round(spawn.getPosition().x() * this.options.squaresPerCell())));
        int z = Math.max(0, Math.min(this.mapY - 1, Math.round(spawn.getPosition().z() * this.options.squaresPerCell())));
        return (z * this.mapX) + x;
    }

    private static float sampleCubic(float[] values, int width, int height, float x, float y) {
        int x1 = (int) Math.floor(x);
        int y1 = (int) Math.floor(y);
        float tx = x - x1;
        float ty = y - y1;
        if (tx == SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT && ty == SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            return values[(clamp(y1, height) * width) + clamp(x1, width)];
        }
        float result = 0.0f;
        for (int j = -1; j <= 2; j++) {
            float wy = catmullRom(j - ty);
            int row = clamp(y1 + j, height) * width;
            float rowSum = 0.0f;
            for (int i = -1; i <= 2; i++) {
                rowSum += values[row + clamp(x1 + i, width)] * catmullRom(i - tx);
            }
            result += rowSum * wy;
        }
        return result;
    }

    private static float catmullRom(float t) {
        float t2 = Math.abs(t);
        if (t2 < 1.0f) {
            return ((((1.5f * t2) * t2) * t2) - ((2.5f * t2) * t2)) + 1.0f;
        }
        if (t2 < 2.0f) {
            return ((((((-0.5f) * t2) * t2) * t2) + ((2.5f * t2) * t2)) - (4.0f * t2)) + 2.0f;
        }
        return SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
    }

    private static int clamp(int value, int size) {
        return Math.max(0, Math.min(size - 1, value));
    }

    private float heightAt(float x, float z) {
        float sx = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(this.mapX, x / 8.0f));
        float sz = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(this.mapY, z / 8.0f));
        int x0 = Math.min((int) sx, this.mapX - 1);
        int z0 = Math.min((int) sz, this.mapY - 1);
        float tx = sx - x0;
        float tz = sz - z0;
        int row = this.mapX + 1;
        float top = (this.heights[(z0 * row) + x0] * (1.0f - tx)) + (this.heights[(z0 * row) + x0 + 1] * tx);
        float bottom = (this.heights[((z0 + 1) * row) + x0] * (1.0f - tx)) + (this.heights[((z0 + 1) * row) + x0 + 1] * tx);
        return (top * (1.0f - tz)) + (bottom * tz);
    }

    private float[] toElmos(Marker marker) {
        float metal = this.options.baseMetal();
        String id = marker.getId();
        if (id != null && id.contains("_val_")) {
            try {
                int start = id.indexOf("_val_") + 5;
                int end = id.indexOf('_', start);
                String valStr = (end != -1) ? id.substring(start, end) : id.substring(start);
                metal = Float.parseFloat(valStr);
            } catch (Exception ignored) {}
        }
        return new float[]{marker.getPosition().x() * this.elmosPerCell, marker.getPosition().z() * this.elmosPerCell, metal, 1.0f};
    }

    private void mergeCloseMetalSpots() {
        int spotCount = this.mexPositions.size();
        int[] group = new int[spotCount];
        for (int i = 0; i < spotCount; i++) {
            group[i] = i;
        }
        for (int i2 = 0; i2 < spotCount; i2++) {
            for (int j = i2 + 1; j < spotCount; j++) {
                float[] other = this.mexPositions.get(j);
                if (distance(this.mexPositions.get(i2), other[0], other[1]) < MIN_METAL_SPOT_SPACING) {
                    int from = group[j];
                    int to = group[i2];
                    for (int k = 0; k < spotCount; k++) {
                        if (group[k] == from) {
                            group[k] = to;
                        }
                    }
                }
            }
        }
        List<float[]> merged = new ArrayList<>();
        for (int i3 = 0; i3 < spotCount; i3++) {
            float x = 0.0f;
            float z = 0.0f;
            float metalSum = 0.0f;
            int count = 0;
            for (int j2 = 0; j2 < spotCount; j2++) {
                if (group[j2] == i3) {
                    x += this.mexPositions.get(j2)[0];
                    z += this.mexPositions.get(j2)[1];
                    metalSum += this.mexPositions.get(j2)[2];
                    count++;
                }
            }
            if (count > 0) {
                float avgMetal = metalSum / count;
                merged.add(new float[]{Math.round((x / count) / 16.0f) * 16.0f, Math.round((z / count) / 16.0f) * 16.0f, avgMetal, count});
            }
        }
        this.mexPositions.clear();
        this.mexPositions.addAll(merged);
    }

    private void assignMetalValues() {
        boolean hasCustomMexes = false;
        for (Marker m : this.map.getMexes()) {
            if (m.getId() != null && m.getId().contains("_val_")) {
                hasCustomMexes = true;
                break;
            }
        }
        if (hasCustomMexes) {
            // Respect custom metal values chosen by user in Map Studio
            return;
        }
        if (this.options.uniformMetal() || this.mexPositions.isEmpty()) {
            for (float[] mex : this.mexPositions) {
                mex[2] = Math.min(this.options.maxMetal(), this.options.baseMetal() * (1.0f + (0.5f * (mex[3] - 1.0f))));
            }
            return;
        }
        List<float[]> spawns = this.map.getSpawns().stream().map((v1) -> {
            return toElmos(v1);
        }).toList();
        float centerX = (this.mapX * 8) / 2.0f;
        float centerZ = (this.mapY * 8) / 2.0f;
        float baseRadius = MAX_WADING_DEPTH * this.elmosPerCell;
        float crowdRadius = 30.0f * this.elmosPerCell;
        int spotCount = this.mexPositions.size();
        float[] spawnDistances = new float[spotCount];
        float[] centerDistances = new float[spotCount];
        float furthest = baseRadius + 1.0f;
        for (int i = 0; i < spotCount; i++) {
            float[] mex2 = this.mexPositions.get(i);
            float nearest = Float.MAX_VALUE;
            for (float[] spawn : spawns) {
                nearest = Math.min(nearest, distance(mex2, spawn[0], spawn[1]));
            }
            spawnDistances[i] = spawns.isEmpty() ? baseRadius * 2.0f : nearest;
            centerDistances[i] = distance(mex2, centerX, centerZ);
            furthest = Math.max(furthest, spawnDistances[i]);
        }
        int seed = this.map.getName().hashCode();
        float phaseA = ((seed & 65535) / 65535.0f) * 6.283f;
        float phaseB = (((seed >>> 16) & 65535) / 65535.0f) * 6.283f;
        float[] values = new float[spotCount];
        for (int i2 = 0; i2 < spotCount; i2++) {
            float[] mex3 = this.mexPositions.get(i2);
            float mergeBonus = 1.0f + (0.35f * (mex3[3] - 1.0f));
            if (spawnDistances[i2] <= baseRadius) {
                values[i2] = this.options.baseMetal() * mergeBonus;
            } else {
                float density = 0.0f;
                for (int j = 0; j < spotCount; j++) {
                    if (j != i2) {
                        float[] other = this.mexPositions.get(j);
                        density += Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f - (distance(mex3, other[0], other[1]) / crowdRadius));
                    }
                }
                float exposure = (spawnDistances[i2] - baseRadius) / (furthest - baseRadius);
                float random = 0.5f + (0.5f * ((float) (StrictMath.sin((centerDistances[i2] * 0.0131f) + phaseA) * StrictMath.cos((spawnDistances[i2] * 0.0173f) + phaseB))));
                float score = ((float) StrictMath.pow(exposure, 1.2000000476837158d)) * (0.55f + (0.6f * random));
                values[i2] = (this.options.minMetal() + ((this.options.maxMetal() - this.options.minMetal()) * Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, (score * (1.5f / (1.0f + (0.5f * density)))) + (0.35f * smoothStep(0.7f, 0.95f, exposure) * smoothStep(0.8f, 0.25f, density) * random))))) * mergeBonus;
            }
        }
        float tolerance = this.elmosPerCell * 1.5f;
        int[] group = new int[spotCount];
        for (int i3 = 0; i3 < spotCount; i3++) {
            group[i3] = i3;
        }
        for (int i4 = 0; i4 < spotCount; i4++) {
            for (int j2 = i4 + 1; j2 < spotCount; j2++) {
                if (group[i4] != group[j2] && this.mexPositions.get(i4)[3] == this.mexPositions.get(j2)[3] && Math.abs(spawnDistances[j2] - spawnDistances[i4]) < tolerance && Math.abs(centerDistances[j2] - centerDistances[i4]) < tolerance) {
                    int from = group[j2];
                    int to = group[i4];
                    for (int k = 0; k < spotCount; k++) {
                        if (group[k] == from) {
                            group[k] = to;
                        }
                    }
                }
            }
        }
        for (int g = 0; g < spotCount; g++) {
            float sum = 0.0f;
            int count = 0;
            for (int i5 = 0; i5 < spotCount; i5++) {
                if (group[i5] == g) {
                    sum += values[i5];
                    count++;
                }
            }
            if (count != 0) {
                float value = Math.round(Math.max(this.options.minMetal(), Math.min(this.options.maxMetal(), sum / count)) * 10.0f) / 10.0f;
                for (int i6 = 0; i6 < spotCount; i6++) {
                    if (group[i6] == g) {
                        this.mexPositions.get(i6)[2] = value;
                    }
                }
            }
        }
    }

    private static float distance(float[] position, float x, float z) {
        return (float) StrictMath.sqrt(((position[0] - x) * (position[0] - x)) + ((position[1] - z) * (position[1] - z)));
    }

    private static float smoothStep(float edge0, float edge1, float value) {
        float t = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, (value - edge0) / (edge1 - edge0)));
        return t * t * (3.0f - (2.0f * t));
    }

    private byte[] buildMetalMap() {
        int width = this.mapX / 2;
        int height = this.mapY / 2;
        byte[] metalMap = new byte[width * height];
        for (float[] mex : this.mexPositions) {
            int cx = Math.round(mex[0] / 16.0f);
            int cz = Math.round(mex[1] / 16.0f);
            int dz = -2;
            while (dz < 2) {
                int dx = -2;
                while (dx < 2) {
                    boolean corner = (dx == -2 || dx == 1) && (dz == -2 || dz == 1);
                    int x = cx + dx;
                    int z = cz + dz;
                    if (!corner && x >= 0 && z >= 0 && x < width && z < height) {
                        metalMap[(z * width) + x] = (byte) Math.max(1, Math.round((255.0f * mex[2]) / this.options.maxMetal()));
                    }
                    dx++;
                }
                dz++;
            }
        }
        return metalMap;
    }

    private String describeMetal() {
        TreeMap<Float, Integer> counts = new TreeMap<>();
        this.mexPositions.forEach(mex -> {
            counts.merge(Float.valueOf(mex[2]), 1, (v0, v1) -> {
                return Integer.sum(v0, v1);
            });
        });
        StringBuilder result = new StringBuilder();
        counts.forEach((value, count) -> {
            result.append(String.format(Locale.ROOT, "%dx %.1f, ", count, value));
        });
        return result.isEmpty() ? "none" : result.substring(0, result.length() - 2);
    }

    private float getMaxMetal() {
        return this.options.maxMetal() / 3.0600002f;
    }

    private List<SmfWriter.Feature> buildFeatures(List<String> featureTypes) {
        String str;
        int i;
        int iFloorMod;
        List<SmfWriter.Feature> features = new ArrayList<>();
        String rockSet = getRockSet();
        this.plantSets = BiomeFlora.forBiome(String.valueOf(this.map.getBiome().name()), this.options.featuresRepository());
        List<ReclaimFeature> reclaim = new ArrayList<>();
        for (float[] geo : this.geoPositions) {
            addFeature(features, featureTypes, "geovent", geo[0], geo[1], SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        }
        for (Prop prop : this.map.getProps()) {
            float x = prop.getPosition().x() * this.elmosPerCell;
            float z = prop.getPosition().z() * this.elmosPerCell;
            hash(x, z);
            String path = prop.getPath().toLowerCase(Locale.ROOT);
            if (!path.contains("tree")) {
                int rockHash = symmetricHash(x, z);
                if (prop.isBoulder()) {
                    i = 12;
                    iFloorMod = Math.floorMod(rockHash, 10);
                } else {
                    i = 1;
                    iFloorMod = Math.floorMod(rockHash, 8);
                }
                int rockIndex = i + iFloorMod;
                reclaim.add(new ReclaimFeature("rocks30_%s_%02d".formatted(rockSet, Integer.valueOf(rockIndex)), x, z, prop.getRotation(), 9 + rockIndex, ((rockHash >>> 8) & 65535) / 65536.0f));
            } else if (heightAt(x, z) >= 4.0f) {
                int groveHash = symmetricHash(x, z);
                BiomeFlora.PlantSet plants = this.plantSets.isEmpty() ? null : this.plantSets.get(Math.floorMod(groveHash >>> 3, this.plantSets.size()));
                if (plants == null || ((groveHash >>> 9) & 255) / 256.0f < plants.keep()) {
                    int groveSize = plants != null ? plants.perGroup() : 4;
                    int treeCount = path.contains("group") ? groveSize : 1;
                    int i2 = 0;
                    while (i2 < treeCount) {
                        int treeHash = hash(x + (i2 * 31.7f), z - (i2 * 17.3f));
                        float tx = i2 == 0 ? x : x + ((((treeHash & 255) / 255.0f) - 0.5f) * 5.0f * this.elmosPerCell);
                        float tz = i2 == 0 ? z : z + (((((treeHash >> 8) & 255) / 255.0f) - 0.5f) * 5.0f * this.elmosPerCell);
                        if (i2 <= 0 || (isInsideMap(tx, tz, 32.0f) && !isNearResource(tx, tz, 56.0f) && heightAt(tx, tz) >= 4.0f && slopeAt(tx, tz) <= 0.35f)) {
                            if (plants != null) {
                                str = plants.names().get(Math.floorMod((groveHash * 31) + (i2 * 7919), plants.names().size()));
                            } else {
                                str = "treetype" + ((treeHash >>> 16) % 16);
                            }
                            String tree = str;
                            addFeature(features, featureTypes, tree, tx, tz, prop.getRotation());
                        }
                        i2++;
                    }
                }
            }
        }
        for (Army army : this.map.getArmies()) {
            for (Group group : army.getGroups()) {
                for (Unit unit : group.getUnits()) {
                    float x2 = unit.getPosition().x() * this.elmosPerCell;
                    float z2 = unit.getPosition().z() * this.elmosPerCell;
                    String wreck = getWreck(unit.getType(), hash(x2, z2), heightAt(x2, z2) < -8.0f);
                    if (wreck != null && isInsideMap(x2, z2, 32.0f) && !isNearResource(x2, z2, 56.0f)) {
                        reclaim.add(new ReclaimFeature(wreck, x2, z2, unit.getRotation(), WRECK_METAL.getOrDefault(wreck, 150).intValue(), ((symmetricHash(x2, z2) >>> 8) & 65535) / 65536.0f));
                    }
                }
            }
        }
        long totalMetal = 0;
        Iterator<ReclaimFeature> it = reclaim.iterator();
        while (it.hasNext()) {
            totalMetal += (long) it.next().metal();
        }
        float budget = RECLAIM_METAL_PER_PLAYER * Math.max(2, this.map.getSpawnCount()) * this.options.reclaim();
        float keep = totalMetal > 0 ? Math.min(1.0f, budget / totalMetal) : SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
        long keptMetal = 0;
        for (ReclaimFeature feature : reclaim) {
            if (feature.order() < keep) {
                addFeature(features, featureTypes, feature.name(), feature.x(), feature.z(), feature.rotation());
                keptMetal += (long) feature.metal();
            }
        }
        // Aggiunta Features statiche personalizzate posizionate sulla mappa
        for (Object customObj : this.map.getCustomEntities()) {
            if (customObj instanceof com.faforever.neroxis.generator.bar.PlacedCustomEntity ce) {
                com.faforever.neroxis.generator.bar.ObjectEntity ent = ce.getEntity();
                if (ent.getCategory() == com.faforever.neroxis.generator.bar.ObjectEntity.Category.STATIC_PROP) {
                    float fx = ce.getPosition().x() * this.elmosPerCell;
                    float fz = ce.getPosition().y() * this.elmosPerCell;
                    addFeature(features, featureTypes, ent.getId(), fx, fz, ce.getRotation());
                }
            }
        }

        System.out.printf(Locale.ROOT, "Reclaim: %d of %d metal in rocks and wrecks kept (budget %.0f)%n", Long.valueOf(keptMetal), Long.valueOf(totalMetal), Float.valueOf(budget));
        return features;
    }

    private void writeCustomEntities(ZipOutputStream zip) throws IOException {
        Set<String> writtenEntries = new HashSet<>();
        Map<String, com.faforever.neroxis.generator.bar.ObjectEntity> uniqueEntities = new LinkedHashMap<>();
        List<com.faforever.neroxis.generator.bar.PlacedCustomEntity> toSpawn = new ArrayList<>();

        for (Object obj : this.map.getCustomEntities()) {
            if (obj instanceof com.faforever.neroxis.generator.bar.PlacedCustomEntity ce) {
                com.faforever.neroxis.generator.bar.ObjectEntity ent = ce.getEntity();
                if (ent != null) {
                    uniqueEntities.putIfAbsent(ent.getId(), ent);
                    // Solo le feature statiche senza logica inserite direttamente in .smf non necessitano di spawn runtime via gadget
                    if (ent.getCategory() != com.faforever.neroxis.generator.bar.ObjectEntity.Category.STATIC_PROP) {
                        toSpawn.add(ce);
                    }
                }
            }
        }

        // 1. Scrittura asset unici (Modelli 3D, Texture, Script Lua, Definizioni) - SENZA DUPLICATI
        for (com.faforever.neroxis.generator.bar.ObjectEntity ent : uniqueEntities.values()) {
            String entId = ent.getId();

            // A) Modello 3D .OBJ
            String objEntry = "objects3d/" + entId + ".obj";
            if (writtenEntries.add(objEntry)) {
                if (ent.getObjFilePath() != null && Files.isRegularFile(ent.getObjFilePath())) {
                    zip.putNextEntry(new ZipEntry(objEntry));
                    Files.copy(ent.getObjFilePath(), zip);
                    zip.closeEntry();
                } else if (ent.getModel3D() != null) {
                    zip.putNextEntry(new ZipEntry(objEntry));
                    zip.write(ent.getModel3D().toObjBytes(ent.getName()));
                    zip.closeEntry();
                }
            }

            // B) Texture personalizzata
            if (ent.getTexturePath() != null && Files.isRegularFile(ent.getTexturePath())) {
                String ext = ent.getTexturePath().getFileName().toString();
                String texEntry = "unittextures/" + entId + "_" + ext;
                if (writtenEntries.add(texEntry)) {
                    zip.putNextEntry(new ZipEntry(texEntry));
                    Files.copy(ent.getTexturePath(), zip);
                    zip.closeEntry();
                }
            }

            // C) Script di animazione / logica Lua
            if (ent.getLuaScript() != null && !ent.getLuaScript().isBlank()) {
                String scriptEntry = "scripts/" + entId + ".lua";
                if (writtenEntries.add(scriptEntry)) {
                    zip.putNextEntry(new ZipEntry(scriptEntry));
                    zip.write(ent.getLuaScript().getBytes(StandardCharsets.UTF_8));
                    zip.closeEntry();
                }
            }

            // D) Per entita' custom (non unita' ufficiali di BAR gia' note al gioco),
            // generiamo sia la definizione unita' (units/<id>.lua) sia feature (features/<id>.lua)
            if (ent.getCategory() != com.faforever.neroxis.generator.bar.ObjectEntity.Category.OFFICIAL_UNIT) {
                String unitDefEntry = "units/" + entId + ".lua";
                if (writtenEntries.add(unitDefEntry)) {
                    zip.putNextEntry(new ZipEntry(unitDefEntry));
                    zip.write(buildCustomUnitDefLua(ent).getBytes(StandardCharsets.UTF_8));
                    zip.closeEntry();
                }
                String featDefEntry = "features/" + entId + ".lua";
                if (writtenEntries.add(featDefEntry)) {
                    zip.putNextEntry(new ZipEntry(featDefEntry));
                    zip.write(buildCustomFeatureDefLua(ent).getBytes(StandardCharsets.UTF_8));
                    zip.closeEntry();
                }
            }
        }

        // 2. Generazione del Map Gadget per spawnare le entita' in-game
        if (!toSpawn.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("if not gadgetHandler:IsSyncedCode() then return end\n\n");
            sb.append("function gadget:GetInfo()\n");
            sb.append("    return {\n");
            sb.append("        name = \"Map Custom Entities Spawner\",\n");
            sb.append("        desc = \"Spawns units, animated scenery and custom objects placed in BAR Map Studio\",\n");
            sb.append("        author = \"BAR Map Studio\",\n");
            sb.append("        date = \"2026\",\n");
            sb.append("        license = \"GNU GPL, v2 or later\",\n");
            sb.append("        layer = 1,\n");
            sb.append("        enabled = true\n");
            sb.append("    }\n");
            sb.append("end\n\n");

            sb.append("local placedEntities = {\n");
            for (com.faforever.neroxis.generator.bar.PlacedCustomEntity ce : toSpawn) {
                com.faforever.neroxis.generator.bar.ObjectEntity ent = ce.getEntity();
                float wx = ce.getPosition().x() * this.elmosPerCell;
                float wz = ce.getPosition().y() * this.elmosPerCell;
                float rot = ce.getRotation();
                String faction = ent.getFaction() != null ? ent.getFaction() : "Neutral";
                String cat = ent.getCategory().name();

                sb.append(String.format(Locale.ROOT,
                    "    { id = \"%s\", x = %.1f, z = %.1f, rot = %.4f, faction = \"%s\", cat = \"%s\" },\n",
                    ent.getId(), wx, wz, rot, faction, cat
                ));
            }
            sb.append("}\n\n");

            sb.append("function gadget:GameStart()\n");
            sb.append("    local gaiaTeamID = Spring.GetGaiaTeamID()\n");
            sb.append("    local teamList = Spring.GetTeamList()\n");
            sb.append("    local armadaTeam = nil\n");
            sb.append("    local cortexTeam = nil\n");
            sb.append("    for _, tId in ipairs(teamList) do\n");
            sb.append("        if tId ~= gaiaTeamID then\n");
            sb.append("            local _, _, _, _, side = Spring.GetTeamInfo(tId)\n");
            sb.append("            if side and string.lower(side) == \"armada\" and not armadaTeam then armadaTeam = tId end\n");
            sb.append("            if side and string.lower(side) == \"cortex\" and not cortexTeam then cortexTeam = tId end\n");
            sb.append("        end\n");
            sb.append("    end\n");
            sb.append("    if not armadaTeam then armadaTeam = 0 end\n");
            sb.append("    if not cortexTeam then cortexTeam = 1 end\n\n");

            sb.append("    for _, e in ipairs(placedEntities) do\n");
            sb.append("        local teamID = gaiaTeamID\n");
            sb.append("        local f = string.lower(e.faction or \"\")\n");
            sb.append("        if f == \"armada\" then\n");
            sb.append("            teamID = armadaTeam\n");
            sb.append("        elseif f == \"cortex\" then\n");
            sb.append("            teamID = cortexTeam\n");
            sb.append("        elseif f == \"player0\" then\n");
            sb.append("            teamID = 0\n");
            sb.append("        elseif f == \"player1\" then\n");
            sb.append("            teamID = 1\n");
            sb.append("        else\n");
            sb.append("            teamID = gaiaTeamID\n");
            sb.append("        end\n\n");

            sb.append("        local y = Spring.GetGroundHeight(e.x, e.z)\n");
            sb.append("        local facing = \"s\"\n");
            sb.append("        if e.rot then\n");
            sb.append("            local deg = (math.deg(e.rot) % 360 + 360) % 360\n");
            sb.append("            if deg >= 45 and deg < 135 then facing = \"e\"\n");
            sb.append("            elseif deg >= 135 and deg < 225 then facing = \"n\"\n");
            sb.append("            elseif deg >= 225 and deg < 315 then facing = \"w\"\n");
            sb.append("            else facing = \"s\" end\n");
            sb.append("        end\n\n");

            sb.append("        local unitID = Spring.CreateUnit(e.id, e.x, y, e.z, facing, teamID)\n");
            sb.append("        if not unitID then\n");
            sb.append("            unitID = Spring.CreateUnit(e.id, e.x, y, e.z, facing, gaiaTeamID)\n");
            sb.append("        end\n");
            sb.append("        if not unitID then\n");
            sb.append("            Spring.CreateFeature(e.id, e.x, y, e.z, 0)\n");
            sb.append("        end\n");
            sb.append("        if unitID and teamID == gaiaTeamID then\n");
            sb.append("            Spring.SetUnitNeutral(unitID, true)\n");
            sb.append("        end\n");
            sb.append("    end\n");
            sb.append("end\n");

            zip.putNextEntry(new ZipEntry("luarules/gadgets/map_custom_spawner.lua"));
            zip.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
    }

    private String buildCustomUnitDefLua(com.faforever.neroxis.generator.bar.ObjectEntity ent) {
        StringBuilder sb = new StringBuilder();
        String id = ent.getId();
        int fx = Math.max(1, ent.getFootprintX());
        int fz = Math.max(1, ent.getFootprintZ());
        String yard = String.join(" ", java.util.Collections.nCopies(fz, "o".repeat(fx)));
        String script = (ent.getLuaScript() != null && !ent.getLuaScript().isBlank()) ? ("\"" + id + ".lua\"") : "nil";

        sb.append("local unitName = \"").append(id).append("\"\n");
        sb.append("local unitDef = {\n");
        sb.append("    name = \"").append(ent.getName() != null ? ent.getName().replace("\"", "'") : id).append("\",\n");
        sb.append("    description = \"").append(ent.getTooltip() != null ? ent.getTooltip().replace("\"", "'") : "").append("\",\n");
        sb.append("    objectName = \"objects3d/").append(id).append(".obj\",\n");
        sb.append("    maxDamage = ").append(Math.max(10, ent.getHealth())).append(",\n");
        sb.append("    footprintX = ").append(fx).append(",\n");
        sb.append("    footprintZ = ").append(fz).append(",\n");
        sb.append("    metalCost = ").append(Math.max(0, ent.getMetal())).append(",\n");
        sb.append("    energyCost = ").append(Math.max(0, ent.getEnergy())).append(",\n");
        sb.append("    reclaimable = true,\n");
        sb.append("    sightDistance = 350,\n");
        sb.append("    category = \"BUILDING NOTAIR\",\n");
        sb.append("    yardMap = \"").append(yard).append("\",\n");
        sb.append("    levelGround = false,\n");
        sb.append("    useBuildingGroundDecal = false,\n");
        if (!"nil".equals(script)) {
            sb.append("    script = ").append(script).append(",\n");
        }
        sb.append("}\n");
        sb.append("return lowerkeys({ [unitName] = unitDef })\n");
        return sb.toString();
    }

    private String buildCustomFeatureDefLua(com.faforever.neroxis.generator.bar.ObjectEntity ent) {
        StringBuilder sb = new StringBuilder();
        String id = ent.getId();
        int fx = Math.max(1, ent.getFootprintX());
        int fz = Math.max(1, ent.getFootprintZ());

        sb.append("local featureDef = {\n");
        sb.append("    name = \"").append(id).append("\",\n");
        sb.append("    description = \"").append(ent.getName() != null ? ent.getName().replace("\"", "'") : id).append("\",\n");
        sb.append("    object = \"objects3d/").append(id).append(".obj\",\n");
        sb.append("    blocking = true,\n");
        sb.append("    burnable = false,\n");
        sb.append("    reclaimable = true,\n");
        sb.append("    energy = ").append(Math.max(0, ent.getEnergy())).append(",\n");
        sb.append("    metal = ").append(Math.max(0, ent.getMetal())).append(",\n");
        sb.append("    damage = ").append(Math.max(10, ent.getHealth())).append(",\n");
        sb.append("    footprintX = ").append(fx).append(",\n");
        sb.append("    footprintZ = ").append(fz).append(",\n");
        sb.append("    hitdensity = 1000,\n");
        sb.append("    mass = 200,\n");
        sb.append("}\n");
        sb.append("return { [\"").append(id).append("\"] = featureDef }\n");
        return sb.toString();
    }

    private int symmetricHash(float x, float z) {
        float nearestSpawn = Float.MAX_VALUE;
        for (Spawn spawn : this.map.getSpawns()) {
            float dx = (spawn.getPosition().x() * this.elmosPerCell) - x;
            float dz = (spawn.getPosition().z() * this.elmosPerCell) - z;
            nearestSpawn = Math.min(nearestSpawn, (float) StrictMath.sqrt((dx * dx) + (dz * dz)));
        }
        float centerX = ((this.mapX * 8) / 2.0f) - x;
        float centerZ = ((this.mapY * 8) / 2.0f) - z;
        float center = (float) StrictMath.sqrt((centerX * centerX) + (centerZ * centerZ));
        return hash(Math.round(nearestSpawn / 24.0f), Math.round(center / 24.0f));
    }

    private void addFeature(List<SmfWriter.Feature> features, List<String> featureTypes, String type, float x, float z, float rotationRadians) {
        if (!isInsideMap(x, z, 8.0f)) {
            return;
        }
        int typeIndex = featureTypes.indexOf(type);
        if (typeIndex < 0) {
            typeIndex = featureTypes.size();
            featureTypes.add(type);
        }
        float heading = (float) (((((double) rotationRadians) / 6.283185307179586d) % 1.0d) * 65536.0d);
        if (heading > 32767.0f) {
            heading -= 65536.0f;
        } else if (heading < -32768.0f) {
            heading += 65536.0f;
        }
        features.add(new SmfWriter.Feature(typeIndex, x, heightAt(x, z), z, heading, 1.0f));
    }

    private static String getWreck(String unitType, int hash, boolean underwater) {
        String[] options;
        String type = unitType.toUpperCase(Locale.ROOT);
        char domain = type.length() > 2 ? type.charAt(2) : 'L';
        if (underwater || domain == 'S') {
            if (!underwater) {
                return null;
            }
            options = SHIP_WRECKS;
        } else {
            if (domain == 'A') {
                return null;
            }
            if (domain == 'B') {
                options = BUILDING_WRECKS;
            } else if (T1_LAND.contains(type)) {
                options = SMALL_WRECKS;
            } else if (T3_LAND.contains(type)) {
                options = LARGE_WRECKS;
            } else {
                options = MEDIUM_WRECKS;
            }
        }
        return options[Math.floorMod(hash, options.length)];
    }

    private String getRockSet() {
        String biome = String.valueOf(this.map.getBiome().name()).toLowerCase(Locale.ROOT);
        switch (biome) {
            case "desert":
            case "mars":
            case "syrtis":
            case "sunset":
                return "desert";
            case "frithen":
            case "crystalline":
                return "snow";
            case "earlyautumn":
            case "windingriver":
            case "wonder":
            case "prayer":
                return "moss";
            default:
                return "def";
        }
    }

    private boolean isInsideMap(float x, float z, float margin) {
        return x >= margin && z >= margin && x <= ((float) (this.mapX * 8)) - margin && z <= ((float) (this.mapY * 8)) - margin;
    }

    private boolean isNearResource(float x, float z, float distance) {
        float distanceSquared = distance * distance;
        for (float[] mex : this.mexPositions) {
            if (((mex[0] - x) * (mex[0] - x)) + ((mex[1] - z) * (mex[1] - z)) < distanceSquared) {
                return true;
            }
        }
        for (float[] geo : this.geoPositions) {
            if (((geo[0] - x) * (geo[0] - x)) + ((geo[1] - z) * (geo[1] - z)) < distanceSquared) {
                return true;
            }
        }
        return false;
    }

    private float slopeAt(float x, float z) {
        float dx = heightAt(x + 8.0f, z) - heightAt(x - 8.0f, z);
        float dz = heightAt(x, z + 8.0f) - heightAt(x, z - 8.0f);
        return ((float) StrictMath.sqrt((dx * dx) + (dz * dz))) / 16.0f;
    }

    private static int hash(float x, float z) {
        int h = (Float.floatToIntBits(x) * (-1640531535)) + (Float.floatToIntBits(z) * (-2048144777));
        int h2 = (h ^ (h >>> 15)) * 739982445;
        return h2 ^ (h2 >>> 12);
    }

    private static float[][] readLayerWeights(BufferedImage low, BufferedImage high) {
        int width = low.getWidth();
        int height = low.getHeight();
        if (high.getWidth() != width || high.getHeight() != height) {
            throw new IllegalArgumentException("Texture masks have different sizes: low=" + width + "x" + height + ", high=" + high.getWidth() + "x" + high.getHeight());
        }
        float[][] weights = new float[8][width * height];
        int[] pixel = new int[4];
        int image = 0;
        while (image < 2) {
            WritableRaster raster = (image == 0 ? low : high).getRaster();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    raster.getPixel(x, y, pixel);
                    for (int band = 0; band < 4; band++) {
                        int raw = pixel[band];
                        weights[(image * 4) + band][(y * width) + x] = raw > 0 ? Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, (raw - 128) / 127.0f) : SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
                    }
                }
            }
            image++;
        }
        return weights;
    }

    private float[][] readLayerColors() {
        List<String> previewColors = this.map.getBiome().terrainMaterials().previewColors();
        List<TerrainMaterials.TextureScale> textures = this.map.getBiome().terrainMaterials().textures();
        Vector3 sunColor = this.map.getBiome().lightingSettings().sunColor();
        Vector3 ambience = this.map.getBiome().lightingSettings().sunAmbience();
        float multiplier = this.map.getBiome().lightingSettings().lightingMultiplier();
        float neutralGain = (((((sunColor.x() + sunColor.y()) + sunColor.z()) / 3.0f) * 0.85f) + (((ambience.x() + ambience.y()) + ambience.z()) / 3.0f)) * multiplier;
        float[] gain = {neutralGain, neutralGain, neutralGain};
        float[][] colors = new float[9][];
        int i = 0;
        while (i < 9) {
            String hex = i < previewColors.size() ? previewColors.get(i) : "";
            boolean used = (i >= textures.size() || textures.get(i).path().isBlank() || hex.isBlank()) ? false : true;
            if (used) {
                int rgb = Integer.parseInt(hex, 16);
                float[] color = new float[3];
                color[0] = (((rgb >> 16) & 255) / 255.0f) * gain[0];
                color[1] = (((rgb >> 8) & 255) / 255.0f) * gain[1];
                color[2] = ((rgb & 255) / 255.0f) * gain[2];
                float brightest = Math.max(color[0], Math.max(color[1], color[2]));
                if (brightest > 0.92f) {
                    for (int c = 0; c < 3; c++) {
                        int i2 = c;
                        color[i2] = color[i2] * (0.92f / brightest);
                    }
                }
                colors[i] = color;
            }
            i++;
        }
        return colors;
    }

    private static float[] createNoise() {
        float[] result = new float[262144];
        IntStream.range(0, 512).parallel().forEach(y -> {
            for (int x = 0; x < 512; x++) {
                result[(y * 512) + x] = DetailTextures.tileableNoise((x + 0.5f) / 512.0f, (y + 0.5f) / 512.0f, 6, 6, 0.6f, 7919);
            }
        });
        double mean = 0.0d;
        for (float f : result) {
            mean += (double) f;
        }
        double mean2 = mean / ((double) result.length);
        double variance = 0.0d;
        for (float value : result) {
            variance += (((double) value) - mean2) * (((double) value) - mean2);
        }
        float scale = NOISE_DEVIATION / ((float) StrictMath.sqrt(variance / ((double) result.length)));
        for (int i = 0; i < result.length; i++) {
            result[i] = Math.max(-1.0f, Math.min(1.0f, ((float) (((double) result[i]) - mean2)) * scale));
        }
        if (System.getProperty("neroxis.bar.debug") != null) {
            System.out.printf(Locale.ROOT, "noise deviation before rescale %.3f%n", Float.valueOf(NOISE_DEVIATION / scale));
        }
        return result;
    }

    private static void addValueNoise(float[] target, int cells, float amplitude, int seed) {
        float[] lattice = new float[cells * cells];
        for (int i = 0; i < lattice.length; i++) {
            int h = (i + seed) * (-1640531535);
            int h2 = (h ^ (h >>> 16)) * (-2048144789);
            lattice[i] = ((h2 ^ (h2 >>> 13)) & 65535) / 65535.0f;
        }
        float scale = cells / 512.0f;
        for (int y = 0; y < 512; y++) {
            float fy = y * scale;
            int y0 = (int) fy;
            float ty = smooth(fy - y0);
            int y1 = (y0 + 1) % cells;
            for (int x = 0; x < 512; x++) {
                float fx = x * scale;
                int x0 = (int) fx;
                float tx = smooth(fx - x0);
                int x1 = (x0 + 1) % cells;
                float top = (lattice[(y0 * cells) + x0] * (1.0f - tx)) + (lattice[(y0 * cells) + x1] * tx);
                float bottom = (lattice[(y1 * cells) + x0] * (1.0f - tx)) + (lattice[(y1 * cells) + x1] * tx);
                int i2 = (y * 512) + x;
                target[i2] = target[i2] + (((top * (1.0f - ty)) + (bottom * ty)) * amplitude);
            }
        }
    }

    private static float smooth(float t) {
        return t * t * (3.0f - (2.0f * t));
    }

    private float earthPatches(float x, float z) {
        return (noiseAt((((x * 0.788f) + (z * 0.616f)) * 0.031f) + 77.0f, (((z * 0.788f) - (x * 0.616f)) * 0.031f) + 5.0f) * 0.7f) + (noiseAt((((x * 0.342f) - (z * 0.94f)) * 0.1f) + 19.0f, (((x * 0.94f) + (z * 0.342f)) * 0.1f) + 233.0f) * 0.45f);
    }

    private float noiseAt(float x, float y) {
        float fx = x - (((float) Math.floor(x / 512.0f)) * 512.0f);
        float fy = y - (((float) Math.floor(y / 512.0f)) * 512.0f);
        int x0 = ((int) fx) & 511;
        int y0 = ((int) fy) & 511;
        int x1 = (x0 + 1) & 511;
        int y1 = (y0 + 1) & 511;
        float tx = fx - ((int) fx);
        float ty = fy - ((int) fy);
        float top = (this.noise[(y0 * 512) + x0] * (1.0f - tx)) + (this.noise[(y0 * 512) + x1] * tx);
        float bottom = (this.noise[(y1 * 512) + x0] * (1.0f - tx)) + (this.noise[(y1 * 512) + x1] * tx);
        return (top * (1.0f - ty)) + (bottom * ty);
    }

    private void sharpenCliffs() {
        int row = this.mapX + 1;
        int rows = this.mapY + 1;
        float[] blurred = boxBlur(this.heights, row, rows, 2);
        float[] highest = extremeFilter(this.heights, row, rows, 3, true);
        float[] lowest = extremeFilter(this.heights, row, rows, 3, false);
        float[] result = new float[this.heights.length];
        IntStream.range(0, rows).parallel().forEach(z -> {
            for (int x = 0; x < row; x++) {
                int x0 = Math.max(0, x - 2);
                int x1 = Math.min(this.mapX, x + 2);
                int z0 = Math.max(0, z - 2);
                int z1 = Math.min(this.mapY, z + 2);
                float dx = (blurred[(z * row) + x1] - blurred[(z * row) + x0]) / ((x1 - x0) * 8);
                float dz = (blurred[(z1 * row) + x] - blurred[(z0 * row) + x]) / ((z1 - z0) * 8);
                float steep = smoothStep(0.55f, 0.95f, (float) StrictMath.sqrt((dx * dx) + (dz * dz)));
                int i = (z * row) + x;
                float height = this.heights[i];
                if (steep <= SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT || highest[i] - lowest[i] < 12.0f) {
                    result[i] = height;
                } else {
                    float position = (height - lowest[i]) / (highest[i] - lowest[i]);
                    float curved = position * position * (3.0f - (2.0f * position));
                    float target = lowest[i] + ((highest[i] - lowest[i]) * curved * curved * (3.0f - (2.0f * curved)));
                    result[i] = height + ((target - height) * steep * 0.75f);
                }
            }
        });
        float[] smoothed = boxBlur(result, row, rows, 1);
        for (int i = 0; i < this.heights.length; i++) {
            this.heights[i] = (result[i] * 0.5f) + (smoothed[i] * 0.5f);
        }
    }

    private void addRidges() {
        int row = this.mapX + 1;
        float[] blurred = boxBlur(this.heights, row, this.mapY + 1, 2);
        float[] result = new float[this.heights.length];
        IntStream.rangeClosed(0, this.mapY).parallel().forEach(z -> {
            for (int x = 0; x <= this.mapX; x++) {
                int x0 = Math.max(0, x - 2);
                int x1 = Math.min(this.mapX, x + 2);
                int z0 = Math.max(0, z - 2);
                int z1 = Math.min(this.mapY, z + 2);
                float dx = (blurred[(z * row) + x1] - blurred[(z * row) + x0]) / ((x1 - x0) * 8);
                float dz = (blurred[(z1 * row) + x] - blurred[(z0 * row) + x]) / ((z1 - z0) * 8);
                float steep = smoothStep(0.5f, 0.9f, (float) StrictMath.sqrt((dx * dx) + (dz * dz)));
                int i = (z * row) + x;
                if (steep <= SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                    result[i] = this.heights[i];
                } else {
                    float wx = x * 8;
                    float wz = z * 8;
                    float ridge = 1.0f - (Math.abs(smoothNoise(wx, wz, 110.0f, 0.788f, 0.616f, 9)) * 2.2f);
                    float fineRidge = 1.0f - (Math.abs(smoothNoise(wx, wz, 46.0f, 0.454f, -0.891f, 37)) * 2.2f);
                    float ridge2 = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, ridge);
                    float fineRidge2 = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, fineRidge);
                    result[i] = this.heights[i] + (((((ridge2 * ridge2) - 0.45f) * 14.0f) + (((fineRidge2 * fineRidge2) - 0.45f) * 5.0f)) * steep);
                }
            }
        });
        System.arraycopy(result, 0, this.heights, 0, this.heights.length);
    }

    private static float[] extremeFilter(float[] values, int width, int height, int radius, boolean maximum) {
        float[] horizontal = new float[values.length];
        float[] result = new float[values.length];
        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                float extreme = values[(y * width) + x];
                for (int d = -radius; d <= radius; d++) {
                    float value = values[(y * width) + Math.max(0, Math.min(width - 1, x + d))];
                    extreme = maximum ? Math.max(extreme, value) : Math.min(extreme, value);
                }
                horizontal[(y * width) + x] = extreme;
            }
        });
        IntStream.range(0, height).parallel().forEach(y2 -> {
            for (int x = 0; x < width; x++) {
                float extreme = horizontal[(y2 * width) + x];
                for (int d = -radius; d <= radius; d++) {
                    float value = horizontal[(Math.max(0, Math.min(height - 1, y2 + d)) * width) + x];
                    extreme = maximum ? Math.max(extreme, value) : Math.min(extreme, value);
                }
                result[(y2 * width) + x] = extreme;
            }
        });
        return result;
    }

    private static float[] boxBlur(float[] values, int width, int height, int radius) {
        float[] horizontal = new float[values.length];
        float[] result = new float[values.length];
        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                float sum = 0.0f;
                for (int d = -radius; d <= radius; d++) {
                    sum += values[(y * width) + Math.max(0, Math.min(width - 1, x + d))];
                }
                horizontal[(y * width) + x] = sum / ((radius * 2) + 1);
            }
        });
        IntStream.range(0, height).parallel().forEach(y2 -> {
            for (int x = 0; x < width; x++) {
                float sum = 0.0f;
                for (int d = -radius; d <= radius; d++) {
                    sum += horizontal[(Math.max(0, Math.min(height - 1, y2 + d)) * width) + x];
                }
                result[(y2 * width) + x] = sum / ((radius * 2) + 1);
            }
        });
        return result;
    }

    private void buildShoreDistance() {
        float[] distance = new float[this.mapX * this.mapY];
        int row = this.mapX + 1;
        boolean hasWater = false;
        for (int z = 0; z < this.mapY; z++) {
            for (int x = 0; x < this.mapX; x++) {
                float center = (((this.heights[(z * row) + x] + this.heights[((z * row) + x) + 1]) + this.heights[((z + 1) * row) + x]) + this.heights[(((z + 1) * row) + x) + 1]) / 4.0f;
                distance[(z * this.mapX) + x] = center < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT : 1000000.0f;
                hasWater |= center < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
            }
        }
        if (hasWater) {
            for (int z2 = 0; z2 < this.mapY; z2++) {
                for (int x2 = 0; x2 < this.mapX; x2++) {
                    float d = distance[(z2 * this.mapX) + x2];
                    if (x2 > 0) {
                        d = Math.min(d, distance[((z2 * this.mapX) + x2) - 1] + 8.0f);
                    }
                    if (z2 > 0) {
                        d = Math.min(d, distance[((z2 - 1) * this.mapX) + x2] + 8.0f);
                    }
                    if (x2 > 0 && z2 > 0) {
                        d = Math.min(d, distance[(((z2 - 1) * this.mapX) + x2) - 1] + 11.3136f);
                    }
                    if (x2 < this.mapX - 1 && z2 > 0) {
                        d = Math.min(d, distance[((z2 - 1) * this.mapX) + x2 + 1] + 11.3136f);
                    }
                    distance[(z2 * this.mapX) + x2] = d;
                }
            }
            for (int z3 = this.mapY - 1; z3 >= 0; z3--) {
                for (int x3 = this.mapX - 1; x3 >= 0; x3--) {
                    float d2 = distance[(z3 * this.mapX) + x3];
                    if (x3 < this.mapX - 1) {
                        d2 = Math.min(d2, distance[(z3 * this.mapX) + x3 + 1] + 8.0f);
                    }
                    if (z3 < this.mapY - 1) {
                        d2 = Math.min(d2, distance[((z3 + 1) * this.mapX) + x3] + 8.0f);
                    }
                    if (x3 < this.mapX - 1 && z3 < this.mapY - 1) {
                        d2 = Math.min(d2, distance[((z3 + 1) * this.mapX) + x3 + 1] + 11.3136f);
                    }
                    if (x3 > 0 && z3 < this.mapY - 1) {
                        d2 = Math.min(d2, distance[(((z3 + 1) * this.mapX) + x3) - 1] + 11.3136f);
                    }
                    distance[(z3 * this.mapX) + x3] = d2;
                }
            }
        }
        this.shoreDistance = distance;
    }

    private float beachAt(float x, float z, float wobble) {
        if (this.shoreDistance.length == 0) {
            return SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
        }
        float sx = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(this.mapX - 1.001f, (x / 8.0f) - 0.5f));
        float sz = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(this.mapY - 1.001f, (z / 8.0f) - 0.5f));
        int x0 = (int) sx;
        int z0 = (int) sz;
        float tx = sx - x0;
        float tz = sz - z0;
        float top = (Math.min(this.shoreDistance[(z0 * this.mapX) + x0], 1000.0f) * (1.0f - tx)) + (Math.min(this.shoreDistance[(z0 * this.mapX) + x0 + 1], 1000.0f) * tx);
        float bottom = (Math.min(this.shoreDistance[((z0 + 1) * this.mapX) + x0], 1000.0f) * (1.0f - tx)) + (Math.min(this.shoreDistance[((z0 + 1) * this.mapX) + x0 + 1], 1000.0f) * tx);
        float distance = (top * (1.0f - tz)) + (bottom * tz);
        float height = heightAt(x, z);
        float byDistance = smoothStep(BEACH_WIDTH, 31.5f, distance + (wobble * MAX_WADING_DEPTH));
        float byHeight = smoothStep(GEO_MARK_RADIUS, 16.0f, height);
        float shallow = smoothStep(-70.0f, -12.0f, height);
        return byDistance * byHeight * shallow * (1.0f - smoothStep(MAX_SATURATION, 0.6f, slopeAt(x, z)));
    }

    private void pickRockAndSandColors() {
        float[] landHeights = new float[4096];
        int landCount = 0;
        for (int gz = 0; gz < 64; gz++) {
            for (int gx = 0; gx < 64; gx++) {
                float landHeight = heightAt(((gx + 0.5f) / 64.0f) * this.mapX * 8.0f, ((gz + 0.5f) / 64.0f) * this.mapY * 8.0f);
                if (landHeight > 2.0f) {
                    int i = landCount;
                    landCount++;
                    landHeights[i] = landHeight;
                }
            }
        }
        if (landCount > 10) {
            Arrays.sort(landHeights, 0, landCount);
            this.lowGround = landHeights[landCount / 5];
            this.highGround = Math.max(this.lowGround + 40.0f, landHeights[(landCount * 9) / 10]);
        }
        float[] rock = new float[3];
        float[] ground = new float[3];
        float rockWeight = 0.0f;
        float groundWeight = 0.0f;
        for (int gz2 = 0; gz2 < 160; gz2++) {
            for (int gx2 = 0; gx2 < 160; gx2++) {
                float x = ((gx2 + 0.5f) / 160) * this.mapX * 8.0f;
                float z = ((gz2 + 0.5f) / 160) * this.mapY * 8.0f;
                if (heightAt(x, z) >= 6.0f) {
                    float slope = slopeAt(x, z);
                    float[] color = blendLayers(x, z, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, slope);
                    float steepWeight = smoothStep(0.6f, 1.1f, slope);
                    float flatWeight = 1.0f - smoothStep(0.05f, 0.2f, slope);
                    for (int c = 0; c < 3; c++) {
                        int i2 = c;
                        rock[i2] = rock[i2] + (color[c] * steepWeight);
                        int i3 = c;
                        ground[i3] = ground[i3] + (color[c] * flatWeight);
                    }
                    rockWeight += steepWeight;
                    groundWeight += flatWeight;
                }
            }
        }
        if (groundWeight > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            float[] brown = {0.47f, 0.37f, 0.25f};
            float groundLum = (((ground[0] * 0.2126f) + (ground[1] * 0.7152f)) + (ground[2] * 0.0722f)) / groundWeight;
            float groundSaturation = (Math.max(ground[0], Math.max(ground[1], ground[2])) - Math.min(ground[0], Math.min(ground[1], ground[2]))) / groundWeight;
            this.snowGround = groundLum > 0.55f && groundSaturation < 0.14f;
            float brownLum = (brown[0] * 0.2126f) + (brown[1] * 0.7152f) + (brown[2] * 0.0722f);
            for (int c2 = 0; c2 < 3; c2++) {
                this.dirtColor[c2] = (((ground[c2] / groundWeight) * 0.35f) + (brown[c2] * (groundLum / brownLum) * 0.65f)) * 0.92f;
            }
            float[] warmSand = {0.7f, 0.62f, 0.44f};
            float groundLuminance = (((ground[0] * 0.2126f) + (ground[1] * 0.7152f)) + (ground[2] * 0.0722f)) / groundWeight;
            float brightness = Math.max(0.75f, Math.min(1.15f, (groundLuminance + 0.22f) / 0.6f));
            for (int c3 = 0; c3 < 3; c3++) {
                this.sandColor[c3] = ((warmSand[c3] * 0.72f) + ((ground[c3] / groundWeight) * 0.28f)) * brightness;
            }
        }
        if (rockWeight <= 4.0f) {
            if (groundWeight > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                for (int c4 = 0; c4 < 3; c4++) {
                    this.rockColor[c4] = ((ground[c4] / groundWeight) * 0.45f) + 0.2f;
                }
                return;
            }
            return;
        }
        float luminance = (((rock[0] * 0.2126f) + (rock[1] * 0.7152f)) + (rock[2] * 0.0722f)) / rockWeight;
        float[] warmGrey = {1.04f, 1.0f, 0.94f};
        float rockLuminance = Math.max(0.24f, Math.min(TARGET_LUMINANCE, luminance));
        for (int c5 = 0; c5 < 3; c5++) {
            this.rockColor[c5] = ((rock[c5] / rockWeight) * (rockLuminance / Math.max(luminance, 0.01f)) * 0.4f) + (rockLuminance * warmGrey[c5] * 0.6f);
        }
    }

    private void gradeColors() {
        double luminanceSum = 0.0d;
        double saturationSum = 0.0d;
        int samples = 0;
        for (int gz = 0; gz < 96; gz++) {
            for (int gx = 0; gx < 96; gx++) {
                float x = ((gx + 0.5f) / 96) * this.mapX * 8.0f;
                float z = ((gz + 0.5f) / 96) * this.mapY * 8.0f;
                if (heightAt(x, z) >= 8.0f) {
                    float[] color = blendLayers(x, z, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, slopeAt(x, z));
                    luminanceSum += (double) ((color[0] * 0.2126f) + (color[1] * 0.7152f) + (color[2] * 0.0722f));
                    saturationSum += (double) (Math.max(color[0], Math.max(color[1], color[2])) - Math.min(color[0], Math.min(color[1], color[2])));
                    samples++;
                }
            }
        }
        if (samples == 0) {
            return;
        }
        float luminance = (float) (luminanceSum / ((double) samples));
        float saturation = (float) (saturationSum / ((double) samples));
        float gain = Math.max(0.85f, Math.min(2.2f, TARGET_LUMINANCE / Math.max(luminance, 0.01f)));
        float gradedSaturation = Math.max(saturation * gain, 0.01f);
        float saturationBoost = 1.0f;
        if (gradedSaturation < TARGET_SATURATION) {
            saturationBoost = Math.min(1.6f, TARGET_SATURATION / gradedSaturation);
        } else if (gradedSaturation > MAX_SATURATION) {
            saturationBoost = Math.max(0.6f, MAX_SATURATION / gradedSaturation);
        }
        for (float[] color2 : this.layerColors) {
            if (color2 != null) {
                float grey = (color2[0] * 0.2126f) + (color2[1] * 0.7152f) + (color2[2] * 0.0722f);
                for (int c = 0; c < 3; c++) {
                    float value = (grey + ((color2[c] - grey) * saturationBoost)) * gain;
                    color2[c] = Math.max(0.02f, Math.min(0.93f, value));
                }
            }
        }
        float[] warmGrey = {1.05f, 1.0f, 0.93f};
        for (int layer = 0; layer < 8; layer++) {
            float[] color3 = this.layerColors[layer + 1];
            if (color3 != null && this.layerKinds[layer] == 2) {
                float grey2 = (color3[0] * 0.2126f) + (color3[1] * 0.7152f) + (color3[2] * 0.0722f);
                for (int c2 = 0; c2 < 3; c2++) {
                    color3[c2] = (color3[c2] * MAX_SATURATION) + (grey2 * warmGrey[c2] * 0.7f);
                }
            }
        }
        System.out.printf(Locale.ROOT, "Colour grading: brightness x%.2f, saturation x%.2f%n", Float.valueOf(gain), Float.valueOf(saturationBoost));
    }

    private float[] blendLayers(float x, float z, float medium, float fine, float slope) {
        float lx = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(this.layerWidth - 1.001f, ((x / this.elmosPerCell) * (this.layerWidth - 1)) / this.faWidth));
        float lz = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(this.layerHeight - 1.001f, ((z / this.elmosPerCell) * (this.layerHeight - 1)) / this.faHeight));
        int x0 = (int) lx;
        int z0 = (int) lz;
        float tx = lx - x0;
        float tz = lz - z0;
        int i00 = (z0 * this.layerWidth) + x0;
        int i01 = i00 + 1;
        int i10 = i00 + this.layerWidth;
        int i11 = i10 + 1;
        float w00 = (1.0f - tx) * (1.0f - tz);
        float w01 = tx * (1.0f - tz);
        float w10 = (1.0f - tx) * tz;
        float w11 = tx * tz;
        float[] base = this.layerColors[0];
        float r = 0.4f;
        float g = 0.38f;
        float b = 0.3f;
        if (base != null) {
            r = base[0];
            g = base[1];
            b = base[2];
        }
        for (int layer = 0; layer < 8; layer++) {
            float[] color = this.layerColors[layer + 1];
            if (color != null) {
                float[] weights = this.layerWeights[layer];
                float weight = (weights[i00] * w00) + (weights[i01] * w01) + (weights[i10] * w10) + (weights[i11] * w11);
                if (weight > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                    int kind = this.layerKinds[layer];
                    if (kind == 1) {
                        weight *= smoothStep(0.07f, 0.26f, slope);
                    } else if (kind == 2) {
                        weight *= smoothStep(0.22f, 0.5f, slope);
                    }
                    if (weight > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                        float weight2 = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, weight + (((medium * 0.6f) + (fine * 0.25f)) * weight * (1.0f - weight) * 2.0f)));
                        r += (color[0] - r) * weight2;
                        g += (color[1] - g) * weight2;
                        b += (color[2] - b) * weight2;
                    }
                }
            }
        }
        return new float[]{r, g, b};
    }

    private int shade(float x, float z, List<float[]> nearMexes, List<float[]> nearGeos) {
        float fine = this.noise[((((int) z) & 511) * 512) + (((int) x) & 511)];
        float medium = (noiseAt((((x * 0.891f) - (z * 0.454f)) * 0.137f) + 91.0f, (((x * 0.454f) + (z * 0.891f)) * 0.137f) + 47.0f) * 0.6f) + (noiseAt((((x * 0.602f) + (z * 0.799f)) * 0.061f) + 7.0f, (((z * 0.602f) - (x * 0.799f)) * 0.061f) + 311.0f) * 0.4f);
        float coarse = (noiseAt((((x * 0.788f) + (z * 0.616f)) * 0.0113f) + 13.0f, (((z * 0.788f) - (x * 0.616f)) * 0.0113f) + 173.0f) * 0.55f) + (noiseAt((((x * 0.342f) - (z * 0.94f)) * 0.0047f) + 401.0f, (((x * 0.94f) + (z * 0.342f)) * 0.0047f) + 59.0f) * 0.45f);
        float slopeX = (heightAt(x + 6.0f, z) - heightAt(x - 6.0f, z)) / 12.0f;
        float slopeZ = (heightAt(x, z + 6.0f) - heightAt(x, z - 6.0f)) / 12.0f;
        float slopeHere = (float) StrictMath.sqrt((slopeX * slopeX) + (slopeZ * slopeZ));
        float r;
        float g;
        float b;
        if (this.customTexture != null) {
            float u = Math.max(0.0f, Math.min(1.0f, x / (float) this.textureWidth));
            float v = Math.max(0.0f, Math.min(1.0f, z / (float) this.textureHeight));
            int tw = this.customTexture.getWidth();
            int th = this.customTexture.getHeight();
            float fx = u * (tw - 1);
            float fz = v * (th - 1);
            int x0 = (int) fx;
            int y0 = (int) fz;
            int x1 = Math.min(tw - 1, x0 + 1);
            int y1 = Math.min(th - 1, y0 + 1);
            float fracX = fx - x0;
            float fracY = fz - y0;
            int c00 = this.customTexture.getRGB(x0, y0);
            int c10 = this.customTexture.getRGB(x1, y0);
            int c01 = this.customTexture.getRGB(x0, y1);
            int c11 = this.customTexture.getRGB(x1, y1);
            float r0 = (((c00 >> 16) & 255) * (1.0f - fracX)) + (((c10 >> 16) & 255) * fracX);
            float r1 = (((c01 >> 16) & 255) * (1.0f - fracX)) + (((c11 >> 16) & 255) * fracX);
            r = ((r0 * (1.0f - fracY)) + (r1 * fracY)) / 255.0f;
            float g0 = (((c00 >> 8) & 255) * (1.0f - fracX)) + (((c10 >> 8) & 255) * fracX);
            float g1 = (((c01 >> 8) & 255) * (1.0f - fracX)) + (((c11 >> 8) & 255) * fracX);
            g = ((g0 * (1.0f - fracY)) + (g1 * fracY)) / 255.0f;
            float b0 = ((c00 & 255) * (1.0f - fracX)) + ((c10 & 255) * fracX);
            float b1 = ((c01 & 255) * (1.0f - fracX)) + ((c11 & 255) * fracX);
            b = ((b0 * (1.0f - fracY)) + (b1 * fracY)) / 255.0f;
        } else {
            float[] blended = blendLayers(x, z, medium, fine, slopeHere);
            r = blended[0];
            g = blended[1];
            b = blended[2];
            float patchNoise = earthPatches(x, z);
            float earth = (smoothStep(0.1f, 0.32f, patchNoise + (medium * 0.2f)) * 0.7f) + (smoothStep(0.07f, 0.22f, slopeHere) * (1.0f - smoothStep(MAX_SATURATION, 0.5f, slopeHere)) * 0.55f);
            for (float[] mex : nearMexes) {
                float mexDistance = (float) StrictMath.sqrt(((x - mex[0]) * (x - mex[0])) + ((z - mex[1]) * (z - mex[1])));
                earth = Math.max(earth, smoothStep(95.0f, 40.0f, mexDistance + (medium * 18.0f)) * 0.6f);
            }
            float earth2 = Math.min(0.8f, earth) * (this.snowGround ? MAX_SATURATION : 1.0f);
            if (earth2 > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                float grain = 1.0f + (fine * 0.1f) + (medium * 0.08f);
                r += ((this.dirtColor[0] * grain) - r) * earth2;
                g += ((this.dirtColor[1] * grain) - g) * earth2;
                b += ((this.dirtColor[2] * grain) - b) * earth2;
            }
            float altitude = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, (heightAt(x, z) - this.lowGround) / (this.highGround - this.lowGround)));
            float dry = altitude * 0.2f;
            float groundLuminance = (r * 0.2126f) + (g * 0.7152f) + (b * 0.0722f);
            r = (r + (((groundLuminance * 1.06f) - r) * dry)) * (1.0f + (altitude * 0.07f));
            g = (g + (((groundLuminance * 1.0f) - g) * dry)) * (1.0f + (altitude * 0.07f));
            b = (b + (((groundLuminance * 0.9f) - b) * dry)) * (1.0f + (altitude * 0.07f));
            float beach = beachAt(x, z, medium);
            if (beach > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                float grain2 = 1.0f + (fine * 0.06f);
                r += ((this.sandColor[0] * grain2) - r) * beach;
                g += ((this.sandColor[1] * grain2) - g) * beach;
                b += ((this.sandColor[2] * grain2) - b) * beach;
            }
        }
        float normalLength = (float) StrictMath.sqrt((slopeX * slopeX) + (slopeZ * slopeZ) + 1.0f);
        float nx = (-slopeX) / normalLength;
        float ny = 1.0f / normalLength;
        float nz = (-slopeZ) / normalLength;
        float light = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, (nx * this.sunDirection[0]) + (ny * this.sunDirection[1]) + (nz * this.sunDirection[2]));
        float flatLight = this.sunDirection[1];
        float shading = 1.0f + ((light - flatLight) * 0.28f);
        float steepness = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, ((1.0f - ny) - 0.08f) / 0.35f));
        float shading2 = shading * (1.0f - (steepness * 0.08f));
        float height = heightAt(x, z);
        float curvature = ((((heightAt(x + 14.0f, z) + heightAt(x - 14.0f, z)) + heightAt(x, z + 14.0f)) + heightAt(x, z - 14.0f)) - (4.0f * height)) / 14.0f;
        float shading3 = shading2 * (1.0f - Math.max(-0.1f, Math.min(0.2f, curvature * TARGET_SATURATION)));
        float r4;
        float g4;
        float b4;
        if (this.customTexture != null) {
            r4 = r * shading3;
            g4 = g * shading3;
            b4 = b * shading3;
        } else {
            float r2 = r;
            float g2 = g;
            float b2 = b;
            float rockFace = smoothStep(MAX_SATURATION, 0.62f, slopeHere + (medium * 0.05f));
            if (rockFace > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                float gradientLength = slopeHere + 1.0E-4f;
                float alongContour = ((x * (-slopeZ)) + (z * slopeX)) / gradientLength;
                float streaks = (noiseAt((alongContour * 0.42f) + 31.0f, (height * 0.035f) + 7.0f) * 0.65f) + (noiseAt((alongContour * 0.17f) + 113.0f, (height * 0.02f) + 59.0f) * 0.35f);
                float faceBrightness = 1.0f + (streaks * 0.22f);
                r2 += ((this.rockColor[0] * faceBrightness) - r2) * rockFace * 0.85f;
                g2 += ((this.rockColor[1] * faceBrightness) - g2) * rockFace * 0.85f;
                b2 += ((this.rockColor[2] * faceBrightness) - b2) * rockFace * 0.85f;
                shading3 = Math.max(shading3 * (1.0f - (Math.max(-0.07f, Math.min(0.2f, curvature * 0.22f)) * rockFace)), 0.78f);
            }
            float drift = coarse * (1.0f - steepness);
            float r3 = r2 * (1.0f + (drift * TARGET_SATURATION));
            float g3 = g2 * (1.0f + (drift * 0.05f));
            float b3 = b2 * (1.0f - (drift * TARGET_SATURATION));
            if (steepness > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                float band = (((float) StrictMath.sin((height * 0.11f) + (medium * 2.5f) + (coarse * 4.0f))) * 0.6f) + (((float) StrictMath.sin((height * 0.37f) + (medium * 4.0f))) * 0.4f);
                float strata = band * steepness;
                r3 *= 1.0f + (strata * 0.08f);
                g3 *= 1.0f + (strata * 0.065f);
                b3 *= 1.0f + (strata * 0.045f);
            }
            float detail = 1.0f + (fine * (0.08f + (0.04f * steepness))) + (medium * 0.17f) + (coarse * 0.14f);
            float brightness = shading3 * detail;
            r4 = r3 * brightness;
            g4 = g3 * brightness;
            b4 = b3 * brightness;
        }
        if (height < 12.0f) {
            float wet = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, (5.0f - height) / 14.0f));
            float depth = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, (-height) / 220.0f));
            float wetDarken = 1.0f - (wet * 0.22f);
            r4 *= wetDarken * (1.0f - (depth * 0.55f));
            g4 *= wetDarken * (1.0f - (depth * 0.4f));
            b4 *= wetDarken * (1.0f - (depth * 0.25f));
        }
        for (float[] mex2 : nearMexes) {
            float dx = x - mex2[0];
            float dz = z - mex2[1];
            float richness = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, (mex2[2] - this.options.baseMetal()) / 2.0f));
            float distance = ((float) StrictMath.sqrt((dx * dx) + (dz * dz))) / (MEX_MARK_RADIUS * (0.8f + (0.2f * richness)));
            if (distance < 1.0f) {
                float edge = distance + (medium * 0.18f);
                float alpha = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, (1.0f - edge) / MAX_SATURATION)) * (0.7f + (0.25f * richness));
                float metal = 0.36f + (TARGET_SATURATION * richness) + (fine * 0.1f) + (medium * 0.08f);
                float speck = fine > 0.45f ? 0.25f : SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
                r4 += (((metal * 0.92f) + speck) - r4) * alpha;
                g4 += (((metal * 0.95f) + speck) - g4) * alpha;
                b4 += (((metal * 1.04f) + speck) - b4) * alpha;
            }
        }
        for (float[] geo : nearGeos) {
            float dx2 = x - geo[0];
            float dz2 = z - geo[1];
            float distance2 = ((float) StrictMath.sqrt((dx2 * dx2) + (dz2 * dz2))) / GEO_MARK_RADIUS;
            if (distance2 < 1.0f) {
                float edge2 = distance2 + (medium * 0.25f);
                float alpha2 = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, (1.0f - edge2) / 0.6f));
                float alpha3 = alpha2 * alpha2;
                r4 += (0.1f - r4) * alpha3 * 0.9f;
                g4 += (0.07f - g4) * alpha3 * 0.9f;
                b4 += (0.05f - b4) * alpha3 * 0.9f;
            }
        }
        int ri = Math.max(0, Math.min(255, Math.round(r4 * 255.0f)));
        int gi = Math.max(0, Math.min(255, Math.round(g4 * 255.0f)));
        int bi = Math.max(0, Math.min(255, Math.round(b4 * 255.0f)));
        return (ri << 16) | (gi << 8) | bi;
    }

    private DetailTextures.Material[] getSplatMaterials() {
        return new DetailTextures.Material[]{DetailTextures.Material.ROCK, DetailTextures.Material.GRAVEL, getGroundMaterial(), DetailTextures.Material.SEABED};
    }

    private DetailTextures.Material getGroundMaterial() {
        DetailTextures.Material result;
        DetailTextures.Material cached = this.groundMaterial;
        if (cached != null) {
            return cached;
        }
        float r = 0.0f;
        float g = 0.0f;
        float b = 0.0f;
        int samples = 0;
        for (Spawn spawn : this.map.getSpawns()) {
            float[] position = toElmos(spawn);
            for (int i = 0; i < 9; i++) {
                int rgb = shade(position[0] + (((i % 3) - 1) * 90), position[1] + (((i / 3) - 1) * 90), List.of(), List.of());
                r += (rgb >> 16) & 255;
                g += (rgb >> 8) & 255;
                b += rgb & 255;
                samples++;
            }
        }
        if (samples == 0) {
            result = DetailTextures.Material.DUST;
        } else if (g > r * 1.06f && g > b * 1.12f) {
            result = DetailTextures.Material.GRASS;
        } else if (Math.min(r, Math.min(g, b)) / samples > 150.0f && Math.max(r, Math.max(g, b)) < Math.min(r, Math.min(g, b)) * 1.25f) {
            result = DetailTextures.Material.SNOW;
        } else if (r > b * 1.3f) {
            result = DetailTextures.Material.DUST;
        } else {
            result = DetailTextures.Material.CRACKED;
        }
        this.groundMaterial = result;
        return result;
    }

    private byte[] buildSplatDistribution() {
        int width = this.mapX * 2;
        int height = this.mapY * 2;
        float texelSize = 4.0f;
        int[] argb = new int[width * height];
        IntStream.range(0, height).parallel().forEach(y -> {
            float z = (y + 0.5f) * texelSize;
            for (int px = 0; px < width; px++) {
                float x = (px + 0.5f) * texelSize;
                float slope = slopeAt(x, z);
                float terrainHeight = heightAt(x, z);
                float patches = earthPatches(x, z);
                float rock = smoothStep(0.2f, VEHICLE_MAX_GRADIENT, slope);
                float sand = Math.max(smoothStep(5.0f, -10.0f, terrainHeight + (patches * 3.0f)), beachAt(x, z, patches));
                float gravel = Math.min(1.0f, smoothStep(0.06f, 0.2f, slope) + (smoothStep(0.1f, 0.32f, patches) * 0.7f));
                float rest = (1.0f - rock) * (1.0f - sand);
                int red = Math.round(rock * 255.0f);
                int green = Math.round(rest * gravel * 255.0f);
                int blue = Math.round(rest * (1.0f - gravel) * 255.0f);
                int alpha = Math.round((1.0f - rock) * sand * 255.0f);
                argb[(y * width) + px] = (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        });
        return Dxt1Encoder.createDxt5Dds(argb, width, height);
    }

    private List<float[]> filterByRows(List<float[]> positions, float minZ, float maxZ, float radius) {
        List<float[]> result = new ArrayList<>();
        for (float[] position : positions) {
            if (position[1] + radius >= minZ && position[1] - radius <= maxZ) {
                result.add(position);
            }
        }
        return result;
    }

    private static List<float[]> filterByColumn(List<float[]> positions, float x, float radius) {
        if (positions.isEmpty()) {
            return positions;
        }
        List<float[]> result = List.of();
        for (float[] position : positions) {
            if (Math.abs(position[0] - x) <= radius) {
                if (result.isEmpty()) {
                    result = new ArrayList();
                }
                result.add(position);
            }
        }
        return result;
    }

    private void writeTiles(OutputStream out) throws IOException {
        int tilesX = this.textureWidth / 32;
        int tilesY = this.textureHeight / 32;
        SmfWriter.writeSmtHeader(out, tilesX * tilesY);
        int tileSize = 32;
        int i = 0;
        while (true) {
            int chunkStart = i;
            if (chunkStart < tilesY) {
                int end = Math.min(tilesY, chunkStart + 64);
                List<byte[]> rows = IntStream.range(chunkStart, end).parallel().mapToObj(tileY -> {
                    byte[] rowBytes = new byte[tilesX * 680];
                    float minZ = tileY * tileSize;
                    List<float[]> rowMexes = filterByRows(this.mexPositions, minZ, minZ + tileSize, WORN_GROUND_RADIUS);
                    List<float[]> rowGeos = filterByRows(this.geoPositions, minZ, minZ + tileSize, GEO_MARK_RADIUS);
                    int[] tile = new int[tileSize * tileSize];
                    for (int tileX = 0; tileX < tilesX; tileX++) {
                        float centerX = (tileX * tileSize) + (tileSize / 2.0f);
                        List<float[]> tileMexes = filterByColumn(rowMexes, centerX, WORN_GROUND_RADIUS + tileSize);
                        List<float[]> tileGeos = filterByColumn(rowGeos, centerX, GEO_MARK_RADIUS + tileSize);
                        for (int y = 0; y < tileSize; y++) {
                            for (int x = 0; x < tileSize; x++) {
                                tile[(y * tileSize) + x] = shade((tileX * tileSize) + x + 0.5f, minZ + y + 0.5f, tileMexes, tileGeos);
                            }
                        }
                        int offset = tileX * 680;
                        int[] mip = tile;
                        for (int size = tileSize; size >= 4; size /= 2) {
                            offset += Dxt1Encoder.compress(mip, size, size, rowBytes, offset);
                            if (size > 4) {
                                mip = Dxt1Encoder.downsample(mip, size, size);
                            }
                        }
                    }
                    return rowBytes;
                }).toList();
                for (byte[] row : rows) {
                    out.write(row);
                }
                i = chunkStart + 64;
            } else {
                return;
            }
        }
    }

    private BufferedImage renderPreview(BufferedImage minimapImage) {
        int width = minimapImage.getWidth();
        int height = minimapImage.getHeight();
        int[] pixels = minimapImage.getRGB(0, 0, width, height, (int[]) null, 0, width);
        float scaleX = this.textureWidth / (float) width;
        float scaleZ = this.textureHeight / (float) height;
        float lightX = -0.6f;
        float lightY = 0.64f;
        float lightZ = -0.48f;
        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                float wx = (x + 0.5f) * scaleX;
                float wz = (y + 0.5f) * scaleZ;
                float slopeX = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, heightAt(wx + scaleX, wz)) - Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, heightAt(wx - scaleX, wz));
                float slopeZ = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, heightAt(wx, wz + scaleZ)) - Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, heightAt(wx, wz - scaleZ));
                float nx = -slopeX;
                float ny = 2.0f * scaleX;
                float nz = -slopeZ;
                float length = (float) StrictMath.sqrt((nx * nx) + (ny * ny) + (nz * nz));
                float light = (((nx * lightX) + (ny * lightY)) + (nz * lightZ)) / length;
                float factor = Math.max(0.5f, Math.min(1.45f, 1.0f + ((light - lightY) * 1.7f)));
                int rgb = pixels[(y * width) + x];
                int r = Math.min(255, Math.round(((rgb >> 16) & 255) * factor));
                int g = Math.min(255, Math.round(((rgb >> 8) & 255) * factor));
                int b = Math.min(255, Math.round((rgb & 255) * factor));
                pixels[(y * width) + x] = (r << 16) | (g << 8) | b;
            }
        });
        BufferedImage preview = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        preview.setRGB(0, 0, width, height, pixels, 0, width);
        return preview;
    }

    private BufferedImage renderMinimap() {
        int size = DDSHeader.CUBEMAP_POSITIVEX_FLAG;
        BufferedImage image = new BufferedImage(DDSHeader.CUBEMAP_POSITIVEX_FLAG, DDSHeader.CUBEMAP_POSITIVEX_FLAG, 1);
        float scaleX = this.textureWidth / DDSHeader.CUBEMAP_POSITIVEX_FLAG;
        float scaleZ = this.textureHeight / DDSHeader.CUBEMAP_POSITIVEX_FLAG;
        int[] pixels = new int[DDSHeader.CUBEMAP_POSITIVEX_FLAG * DDSHeader.CUBEMAP_POSITIVEX_FLAG];
        IntStream.range(0, DDSHeader.CUBEMAP_POSITIVEX_FLAG).parallel().forEach(y -> {
            for (int x = 0; x < size; x++) {
                int rgb = shade((x + 0.5f) * scaleX, (y + 0.5f) * scaleZ, this.mexPositions, this.geoPositions);
                float height = heightAt((x + 0.5f) * scaleX, (y + 0.5f) * scaleZ);
                if (height < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                    float depth = Math.min(1.0f, (-height) / 160.0f);
                    float keep = 0.55f - (depth * MAX_SATURATION);
                    int r = (int) ((((rgb >> 16) & 255) * keep) + (30.0f * (1.0f - keep)));
                    int g = (int) ((((rgb >> 8) & 255) * keep) + (80.0f * (1.0f - keep)));
                    int b = (int) (((rgb & 255) * keep) + (125.0f * (1.0f - keep)));
                    rgb = (r << 16) | (g << 8) | b;
                }
                pixels[(y * size) + x] = rgb;
            }
        });
        image.setRGB(0, 0, DDSHeader.CUBEMAP_POSITIVEX_FLAG, DDSHeader.CUBEMAP_POSITIVEX_FLAG, pixels, 0, DDSHeader.CUBEMAP_POSITIVEX_FLAG);
        return image;
    }

    private byte[] buildGrassMap() {
        int width = this.mapX / 4;
        int height = this.mapY / 4;
        byte[] grass = new byte[width * height];
        if (getSplatMaterials()[2] != DetailTextures.Material.GRASS) {
            return grass;
        }
        List<float[]> spawns = this.map.getSpawns().stream().map((v1) -> {
            return toElmos(v1);
        }).toList();
        IntStream.range(0, height).parallel().forEach(gz -> {
            float z = (gz + 0.5f) * 4.0f * 8.0f;
            for (int gx = 0; gx < width; gx++) {
                float x = (gx + 0.5f) * 4.0f * 8.0f;
                float patches = noiseAt((((x * 0.788f) + (z * 0.616f)) * 0.017f) + 211.0f, (((z * 0.788f) - (x * 0.616f)) * 0.017f) + 97.0f);
                float gravelPatches = earthPatches(x, z);
                boolean nearSpawn = false;
                Iterator i$ = spawns.iterator();
                while (i$.hasNext()) {
                    float[] spawn = (float[]) i$.next();
                    nearSpawn |= distance(spawn, x, z) < 260.0f;
                }
                if (gravelPatches < 0.26f && heightAt(x, z) > 4.0f && slopeAt(x, z) < 0.12f && beachAt(x, z, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) < 0.25f && !nearSpawn && !isNearResource(x, z, BEACH_WIDTH)) {
                    float size = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, (patches + 1.0f) / 2.0f)) * (1.0f - (Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, gravelPatches) * 3.0f));
                    grass[(gz * width) + gx] = (byte) (70 + Math.round(size * 185.0f));
                }
            }
        });
        return grass;
    }

    private byte[] buildNormalsTexture() {
        int sizeX = Math.min(DDSHeader.CUBEMAP_NEGATIVEX_FLAG, this.mapX * 2);
        int sizeY = Math.min(DDSHeader.CUBEMAP_NEGATIVEX_FLAG, this.mapY * 2);
        float texelSizeX = (this.mapX * 8.0f) / sizeX;
        float texelSizeZ = (this.mapY * 8.0f) / sizeY;
        int[] argb = new int[sizeX * sizeY];
        IntStream.range(0, sizeY).parallel().forEach(py -> {
            float z = (py + 0.5f) * texelSizeZ;
            for (int px = 0; px < sizeX; px++) {
                float x = (px + 0.5f) * texelSizeX;
                float slope = Math.min(1.0f, slopeAt(x, z) / 0.5f);
                float strength = 0.5f + (slope * 2.4f);
                float dx = ((relief(x + texelSizeX, z) - relief(x - texelSizeX, z)) / (2.0f * texelSizeX)) * strength;
                float dz = ((relief(x, z + texelSizeZ) - relief(x, z - texelSizeZ)) / (2.0f * texelSizeZ)) * strength;
                float length = (float) StrictMath.sqrt((dx * dx) + (dz * dz) + 1.0f);
                int red = toByte((((-dx) / length) * 0.5f) + 0.5f);
                int green = toByte((((-dz) / length) * 0.5f) + 0.5f);
                int blue = toByte(((1.0f / length) * 0.5f) + 0.5f);
                int alpha = toByte(heightAt(x, z) < -20.0f ? 0.25f : 0.45f + (slope * MAX_SATURATION));
                argb[(py * sizeX) + px] = (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        });
        return DetailTextures.encodeTga(argb, sizeX, sizeY);
    }

    private float relief(float x, float z) {
        return (smoothNoise(x, z, 24.0f, 0.891f, -0.454f, 17) * 2.2f) + (smoothNoise(x, z, 55.0f, 0.602f, 0.799f, 131) * 4.2f);
    }

    private static float smoothNoise(float x, float z, float wavelength, float cos, float sin, int seed) {
        float domain = wavelength * 64.0f;
        float u = ((x * cos) - (z * sin)) / domain;
        float v = ((x * sin) + (z * cos)) / domain;
        return (DetailTextures.tileableNoise(u - ((float) Math.floor(u)), v - ((float) Math.floor(v)), 64, 2, 0.5f, seed) * 2.0f) - 1.0f;
    }

    private byte[] buildSpecularTexture() {
        int sizeX = this.mapX;
        int sizeY = this.mapY;
        float texelSize = 8.0f;
        boolean snow = getSplatMaterials()[2] == DetailTextures.Material.SNOW;
        int[] argb = new int[sizeX * sizeY];
        IntStream.range(0, sizeY).parallel().forEach(py -> {
            float z = (py + 0.5f) * texelSize;
            for (int px = 0; px < sizeX; px++) {
                float x = (px + 0.5f) * texelSize;
                float height = heightAt(x, z);
                float rock = smoothStep(0.2f, VEHICLE_MAX_GRADIENT, slopeAt(x, z));
                float wet = smoothStep(16.0f, -4.0f, height);
                float variation = noiseAt((x * 0.11f) + 3.0f, (z * 0.11f) + 29.0f) * 0.02f;
                float specular = (snow ? 0.13f : 0.05f) + (rock * 0.05f) + (wet * TARGET_SATURATION) + variation;
                int red = toByte(specular * (1.0f - (wet * 0.15f)));
                int green = toByte(specular);
                int blue = toByte(specular * (1.0f + (wet * 0.2f)));
                int alpha = toByte(0.35f + (wet * 0.4f) + (rock * 0.1f));
                argb[(py * sizeX) + px] = (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        });
        return Dxt1Encoder.createDxt5Dds(argb, sizeX, sizeY);
    }

    private static int toByte(float value) {
        return Math.max(0, Math.min(255, Math.round(value * 255.0f)));
    }

    private byte[] buildEdgeTexture(BufferedImage minimapImage) {
        int width = minimapImage.getWidth();
        int height = minimapImage.getHeight();
        int[] pixels = minimapImage.getRGB(0, 0, width, height, (int[]) null, 0, width);
        for (int i = 0; i < pixels.length; i++) {
            int r = (pixels[i] >> 16) & 255;
            int g = (pixels[i] >> 8) & 255;
            int b = pixels[i] & 255;
            float grey = (r * MAX_SATURATION) + (g * 0.59f) + (b * 0.11f);
            int nr = Math.round((grey + ((r - grey) * 0.35f)) * 0.5f);
            int ng = Math.round((grey + ((g - grey) * 0.35f)) * 0.5f);
            int nb = Math.round((grey + ((b - grey) * 0.35f)) * 0.5f);
            pixels[i] = (-16777216) | (nr << 16) | (ng << 8) | nb;
        }
        return Dxt1Encoder.createDxt5Dds(pixels, width, height);
    }

    private static byte[] compressWithMips(BufferedImage image, int levels) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = image.getRGB(0, 0, width, height, (int[]) null, 0, width);
        for (int i = 0; i < pixels.length; i++) {
            int i2 = i;
            pixels[i2] = pixels[i2] & 16777215;
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        for (int level = 0; level < levels; level++) {
            byte[] compressed = new byte[Math.max(1, width / 4) * Math.max(1, height / 4) * 8];
            Dxt1Encoder.compress(pixels, width, height, compressed, 0);
            result.writeBytes(compressed);
            if (level < levels - 1) {
                pixels = Dxt1Encoder.downsample(pixels, width, height);
                width /= 2;
                height /= 2;
            }
        }
        return result.toByteArray();
    }

    private String buildMapInfo() {
        Vector3 sunColor = this.map.getBiome().lightingSettings().sunColor();
        float sunMax = Math.max(sunColor.x(), Math.max(sunColor.y(), Math.max(sunColor.z(), EXTRACTS_METAL)));
        float[] sun = {sunColor.x() / sunMax, sunColor.y() / sunMax, sunColor.z() / sunMax};
        Vector3 fog = this.map.getBiome().lightingSettings().fogColor();
        float[] fogColor = {clampColor(fog.x()), clampColor(fog.y()), clampColor(fog.z())};
        List<Spawn> spawns = new ArrayList<>(this.map.getSpawns());
        Map<Spawn, Integer> spawnTeams = resolveTeams(spawns);
        spawns.sort(Comparator.comparingInt(spawn -> {
            return ((Integer) spawnTeams.getOrDefault(spawn, 0)).intValue();
        }));
        StringBuilder teams = new StringBuilder();
        for (int i = 0; i < spawns.size(); i++) {
            float[] position = toElmos(spawns.get(i));
            teams.append("\t\t[%d] = {startPos = {x = %d, z = %d}},\n".formatted(Integer.valueOf(i), Integer.valueOf(Math.round(position[0])), Integer.valueOf(Math.round(position[1]))));
        }
        String description = "Generated by the Neroxis Map Generator for BAR. %d players, %d metal spots, %d geo vents. %s Regenerate with --map-name %s".formatted(Integer.valueOf(spawns.size()), Integer.valueOf(this.mexPositions.size()), Integer.valueOf(this.geoPositions.size()), describeTeams(spawns, spawnTeams), this.map.getName());
        Locale locale = Locale.ROOT;
        Object[] objArr = new Object[24];
        objArr[0] = getDisplayName();
        objArr[1] = getDisplayName();
        objArr[2] = description;
        objArr[3] = toFileName(getDisplayName());
        objArr[4] = Float.valueOf(getMaxMetal());
        objArr[5] = Float.valueOf(this.minHeight);
        objArr[6] = Float.valueOf(this.maxHeight);
        objArr[7] = Float.valueOf(fogColor[0]);
        objArr[8] = Float.valueOf(fogColor[1]);
        objArr[9] = Float.valueOf(fogColor[2]);
        objArr[10] = Float.valueOf(sun[0]);
        objArr[11] = Float.valueOf(sun[1]);
        objArr[12] = Float.valueOf(sun[2]);
        objArr[13] = Float.valueOf(this.sunDirection[0]);
        objArr[14] = Float.valueOf(this.sunDirection[1]);
        objArr[15] = Float.valueOf(this.sunDirection[2]);
        objArr[16] = Float.valueOf(sun[0] * 0.95f);
        objArr[17] = Float.valueOf(sun[1] * 0.95f);
        objArr[18] = Float.valueOf(sun[2] * 0.95f);
        objArr[19] = Float.valueOf(sun[0] * 0.85f);
        objArr[20] = Float.valueOf(sun[1] * 0.85f);
        objArr[21] = Float.valueOf(sun[2] * 0.85f);
        objArr[22] = this.minHeight < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? "true" : "false";
        objArr[23] = teams;
        String info = String.format(locale, "-- Generated by the Neroxis Map Generator (BAR export)\nlocal mapinfo = {\n\tname = \"%s\",\n\tshortname = \"%s\",\n\tdescription = \"%s\",\n\tauthor = \"@AUTHOR@\",\n\tversion = \"@VERSION@\",\n\tmapfile = \"maps/%s.smf\",\n\tmodtype = 3,\n\tdepend = {\"Map Helper v1\"},\n\n\tmaphardness = 300,\n\tnotDeformable = false,\n\tgravity = 100,\n\ttidalStrength = @TIDAL@,\n\tmaxMetal = %.4f,\n\textractorRadius = 100,\n\tvoidWater = false,\n\tvoidGround = false,\n\tautoShowMetal = true,\n\n\tsmf = {\n\t\tminheight = %.1f,\n\t\tmaxheight = %.1f,\n\t\tsmtFileName0 = \"maps/@BASE@.smt\",\n\t\tminimapTex = \"maps/@BASE@_preview.png\",\n\t},\n\n\tresources = {\n\t\tgrassShadingTex = \"@BASE@_edge.dds\",\n\t\tdetailNormalTex = \"@BASE@_normals.tga\",\n\t\tspecularTex = \"@BASE@_specular.dds\",\n\t\tsplatDetailNormalDiffuseAlpha = 1,\n\t\tsplatDetailNormalTex1 = \"@BASE@_dnts1.tga\",\n\t\tsplatDetailNormalTex2 = \"@BASE@_dnts2.tga\",\n\t\tsplatDetailNormalTex3 = \"@BASE@_dnts3.tga\",\n\t\tsplatDetailNormalTex4 = \"@BASE@_dnts4.tga\",\n\t\tsplatDistrTex = \"@BASE@_splats.dds\",\n\t},\n\n\tsplats = {\n\t\ttexScales = {0.0045, 0.0071, 0.0062, 0.0058},\n\t\ttexMults = {0.6, 0.7, 0.65, 0.6},\n\t},\n\n\tatmosphere = {\n\t\tminWind = @MINWIND@,\n\t\tmaxWind = @MAXWIND@,\n\t\tfogStart = 0.6,\n\t\tfogEnd = 1.0,\n\t\tfogColor = {%.3f, %.3f, %.3f},\n\t\tskyColor = {0.42, 0.55, 0.72},\n\t\tsunColor = {%.3f, %.3f, %.3f},\n\t\tcloudColor = {0.9, 0.9, 0.9},\n\t\tcloudDensity = 0.4,\n\t},\n\n\tlighting = {\n\t\tsunDir = {%.3f, %.3f, %.3f},\n\t\tgroundAmbientColor = {0.52, 0.52, 0.55},\n\t\tgroundDiffuseColor = {%.3f, %.3f, %.3f},\n\t\tgroundSpecularColor = {0.05, 0.05, 0.05},\n\t\tgroundShadowDensity = 0.75,\n\t\tunitAmbientColor = {0.6, 0.6, 0.6},\n\t\tunitDiffuseColor = {%.3f, %.3f, %.3f},\n\t\tunitSpecularColor = {0.8, 0.8, 0.8},\n\t\tunitShadowDensity = 0.75,\n\t\tspecularExponent = 100.0,\n\t},\n\n\twater = {\n\t\tdamage = 0,\n\t\tabsorb = {0.06, 0.025, 0.012},\n\t\tbaseColor = {0.75, 0.9, 0.95},\n\t\tminColor = {0.05, 0.12, 0.2},\n\t\tsurfaceColor = {0.6, 0.75, 0.85},\n\t\tsurfaceAlpha = 0.25,\n\t\tplaneColor = {0.02, 0.1, 0.16},\n\t\tfresnelMin = 0.2,\n\t\tfresnelMax = 0.8,\n\t\tfresnelPower = 4.0,\n\t\tshoreWaves = true,\n\t\tforceRendering = false,\n\t\thasWaterPlane = %s,\n\t},\n\n\tteams = {\n%s\t},\n\n\tterrainTypes = {\n\t\t[0] = {\n\t\t\tname = \"Default\",\n\t\t\thardness = 1.0,\n\t\t\treceiveTracks = true,\n\t\t\tmoveSpeeds = {\n\t\t\t\ttank = 1.0,\n\t\t\t\tkbot = 1.0,\n\t\t\t\thover = 1.0,\n\t\t\t\tship = 1.0,\n\t\t\t},\n\t\t},\n\t},\n\n\tgrass = {\n\t\tbladeWaveScale = 1.0,\n\t\tbladeWidth = 0.4,\n\t\tbladeHeight = 4.0,\n\t\tbladeAngle = 1.57,\n\t\tbladeColor = {0.55, 0.6, 0.3},\n\t},\n\n\tcustom = {\n\t\tgenerator = \"neroxis\",\n\t\t-- the grass shading texture is used to darken the map edge extension, the grass\n\t\t-- takes its colour from the minimap instead\n\t\tgrassconfig = {\n\t\t\tmapGrassColorModTex = \"$minimap\",\n\t\t\tgrassMinSize = 0.6,\n\t\t\tgrassMaxSize = 1.2,\n\t\t\tgrassShaderParams = {\n\t\t\t\tMAPCOLORFACTOR = 0.5,\n\t\t\t\tMAPCOLORBASE = 0.9,\n\t\t\t},\n\t\t},\n\t},\n}\n\nreturn mapinfo\n", objArr).replace("@BASE@", toFileName(getDisplayName())).replace("@AUTHOR@", this.options.author()).replace("@VERSION@", this.options.mapName() != null ? this.options.mapVersion() : "") + "\n--[[skirmish\n" + buildGameScript(List.of("Player"), true, false, null) + "]]\n";
        return info.replace("@TIDAL@", String.valueOf(this.options.tidal())).replace("@MINWIND@", String.valueOf(this.options.minWind())).replace("@MAXWIND@", String.valueOf(this.options.maxWind()));
    }

    private Map<Spawn, Integer> resolveTeams(List<Spawn> spawns) {
        Map<Spawn, Integer> teams = new IdentityHashMap<>();
        spawns.forEach(spawn -> {
            teams.put(spawn, Integer.valueOf(spawn.getTeamID()));
        });
        int numTeams = this.options.numTeams();
        long distinctIds = spawns.stream().map((v0) -> {
            return v0.getTeamID();
        }).distinct().count();
        if (numTeams < 2 || distinctIds == numTeams || spawns.size() % numTeams != 0) {
            return teams;
        }
        float centerX = this.faWidth / 2.0f;
        float centerZ = this.faHeight / 2.0f;
        if (numTeams == 2) {
            float[][] directions = {new float[]{1.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT}, new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new float[]{0.7071f, 0.7071f}, new float[]{0.7071f, -0.7071f}};
            float bestGap = 0.0f;
            float[] best = null;
            for (float[] direction : directions) {
                int positive = 0;
                float gap = Float.MAX_VALUE;
                for (Spawn spawn2 : spawns) {
                    float projection = ((spawn2.getPosition().x() - centerX) * direction[0]) + ((spawn2.getPosition().z() - centerZ) * direction[1]);
                    positive += projection > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? 1 : 0;
                    gap = Math.min(gap, Math.abs(projection));
                }
                if (positive * 2 == spawns.size() && gap > bestGap) {
                    bestGap = gap;
                    best = direction;
                }
            }
            if (best != null) {
                for (Spawn spawn3 : spawns) {
                    teams.put(spawn3, Integer.valueOf(((spawn3.getPosition().x() - centerX) * best[0]) + ((spawn3.getPosition().z() - centerZ) * best[1]) > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? 1 : 0));
                }
            }
            return teams;
        }
        List<Spawn> byAngle = new ArrayList<>(spawns);
        byAngle.sort(Comparator.comparingDouble(spawn4 -> {
            return StrictMath.atan2(spawn4.getPosition().z() - centerZ, spawn4.getPosition().x() - centerX);
        }));
        int teamSize = spawns.size() / numTeams;
        for (int i = 0; i < byAngle.size(); i++) {
            teams.put(byAngle.get(i), Integer.valueOf(i / teamSize));
        }
        return teams;
    }

    private String describeTeams(List<Spawn> spawns, Map<Spawn, Integer> teams) {
        List<Integer> teamIds = teams.values().stream().distinct().sorted().toList();
        if (teamIds.size() < 2 || teamIds.size() > 4) {
            return "";
        }
        StringBuilder result = new StringBuilder("Start areas:");
        Iterator<Integer> it = teamIds.iterator();
        while (it.hasNext()) {
            int teamId = it.next().intValue();
            float x = 0.0f;
            float z = 0.0f;
            int count = 0;
            for (Spawn spawn : spawns) {
                if (teams.getOrDefault(spawn, -1).intValue() == teamId) {
                    x += spawn.getPosition().x() / this.faWidth;
                    z += spawn.getPosition().z() / this.faHeight;
                    count++;
                }
            }
            float x2 = x / count;
            float z2 = z / count;
            String vertical = z2 < 0.4f ? "north" : z2 > 0.6f ? "south" : "";
            String horizontal = x2 < 0.4f ? "west" : x2 > 0.6f ? "east" : "";
            String area = (vertical + " " + horizontal).trim();
            Object[] objArr = new Object[2];
            objArr[0] = Integer.valueOf(teamIds.indexOf(Integer.valueOf(teamId)) + 1);
            objArr[1] = area.isEmpty() ? "center" : area;
            result.append(" team %d %s,".formatted(objArr));
        }
        result.setLength(result.length() - 1);
        return String.valueOf(result) + ".";
    }

    private static float clampColor(float value) {
        return Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, value));
    }

    private static void normalize(float[] vector) {
        float length = (float) StrictMath.sqrt((vector[0] * vector[0]) + (vector[1] * vector[1]) + (vector[2] * vector[2]));
        for (int i = 0; i < 3; i++) {
            int i2 = i;
            vector[i2] = vector[i2] / length;
        }
    }

    private String getDisplayName() {
        int versionEnd;
        String customName = this.options.mapName();
        if (customName != null) {
            return customName + " " + this.options.mapVersion();
        }
        String name = this.map.getName();
        if (!name.startsWith("neroxis_map_generator_") || (versionEnd = name.indexOf(95, "neroxis_map_generator_".length())) <= 0 || versionEnd >= name.length() - 1) {
            return name.isBlank() ? "Map_map" : name;
        }
        return "Map_" + name.substring(versionEnd + 1);
    }

    private static void saveLobbyMinimaps(Path folderPath, String archiveName, String displayName, BufferedImage previewImage, byte[] previewPngBytes) {
        Set<Path> dataDirs = new LinkedHashSet<>();
        if (folderPath != null) {
            Path parent = folderPath.getParent();
            if (parent != null && "maps".equalsIgnoreCase(folderPath.getFileName().toString())) {
                dataDirs.add(parent);
            }
        }
        String progFiles = System.getenv("ProgramFiles");
        if (progFiles != null && !progFiles.isBlank()) {
            dataDirs.add(Path.of(progFiles, "Beyond-All-Reason", "data"));
        }
        dataDirs.add(Path.of("C:\\Program Files\\Beyond-All-Reason\\data"));
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null && !localAppData.isBlank()) {
            dataDirs.add(Path.of(localAppData, "Programs", "Beyond-All-Reason", "data"));
        }

        String chobbyName = displayName.replace(" ", "_");
        ByteArrayOutputStream jpgBytes = new ByteArrayOutputStream();
        byte[] previewJpgBytes = null;
        try {
            ImageIO.write(previewImage, "jpg", jpgBytes);
            previewJpgBytes = jpgBytes.toByteArray();
        } catch (Throwable ignored) {
        }

        for (Path dataDir : dataDirs) {
            if (!Files.isDirectory(dataDir)) {
                continue;
            }
            try {
                Path thumbDir = dataDir.resolve("LuaMenu/configs/gameconfig/byar/minimapthumbnail");
                Path overrideDir = dataDir.resolve("LuaMenu/configs/gameconfig/byar/minimapoverride");
                Path imagesMinimapsDir = dataDir.resolve("LuaMenu/Images/Minimaps");

                Files.createDirectories(thumbDir);
                Files.createDirectories(overrideDir);
                Files.createDirectories(imagesMinimapsDir);

                String[] names = {chobbyName, archiveName, displayName};
                for (String n : names) {
                    Files.write(thumbDir.resolve(n + ".png"), previewPngBytes);
                    if (previewJpgBytes != null) {
                        Files.write(overrideDir.resolve(n + ".jpg"), previewJpgBytes);
                        Files.write(imagesMinimapsDir.resolve(n + ".jpg"), previewJpgBytes);
                    }
                    Files.write(imagesMinimapsDir.resolve(n + ".png"), previewPngBytes);
                }
            } catch (Throwable t) {
                System.err.println("Warning: Could not save lobby minimap preview: " + t.getMessage());
            }
        }
    }

    private static String toFileName(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_.-]", "_");
    }
}

package com.faforever.neroxis.exporter;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.importer.MapImporter;
import com.faforever.neroxis.importer.SaveImporter;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.mask.FloatMask;
import com.faforever.neroxis.mask.Vector4Mask;
import com.faforever.neroxis.util.ArgumentParser;
import com.faforever.neroxis.util.ImageUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.SplittableRandom;
import java.util.stream.Stream;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/MapImageExporter.class */
public class MapImageExporter {
    public static boolean DEBUG = false;
    private boolean writeLayer0;
    private boolean writeLayer1;
    private boolean writeLayer2;
    private boolean writeLayer3;
    private boolean writeLayer4;
    private boolean writeLayer5;
    private boolean writeLayer6;
    private boolean writeLayer7;
    private boolean writeLayer8;
    private boolean writeLayerh;
    private Path inMapPath = Path.of(".", new String[0]);
    private String writeImagesPath = "";
    private SymmetrySettings symmetrySettings = new SymmetrySettings(Symmetry.NONE, Symmetry.NONE, Symmetry.NONE);

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    void main(String[] args) throws IOException, MatchException {
        Locale.setDefault(Locale.ROOT);
        MapImageExporter mapImageExporter = new MapImageExporter();
        mapImageExporter.interpretArguments(args);
        System.out.println("Creating map image files at " + String.valueOf(mapImageExporter.inMapPath));
        SCMap map = mapImageExporter.importMap();
        mapImageExporter.writeMapImages(map);
    }

    public void interpretArguments(String[] args) {
        interpretArguments(ArgumentParser.parse(args));
    }

    private void interpretArguments(Map<String, String> arguments) {
        if (arguments.containsKey("help")) {
            System.out.println("map-image-writer usage:\n--help                 produce help message\n--in-folder-path arg   required, set the input folder for the map - the images will appear in this folder\n--create-only arg      optional, create only arg limits which layers have PNG images created from them (0, 1, 2, 3, 4, 5, 6, 7, 8, h)\n - ie: to create all available PNG images except the one for layer 7, use: --create-only 01234568h\n - options 0 - 8 will create PNG's of the corresponding texture layers, and option h will create a PNG of the heightmap--debug                optional, turn on debugging options\n");
            System.exit(0);
        }
        if (arguments.containsKey("debug")) {
            DEBUG = true;
        }
        if (!arguments.containsKey("in-folder-path")) {
            System.out.println("Input Folder not Specified");
            System.exit(1);
        }
        this.inMapPath = Paths.get((String) Objects.requireNonNull(arguments.get("in-folder-path")), new String[0]);
        this.writeImagesPath = (String) Objects.requireNonNull(arguments.get("in-folder-path"));
        this.symmetrySettings = new SymmetrySettings(Symmetry.NONE, Symmetry.NONE, Symmetry.NONE);
        boolean writeOnlySelectImages = arguments.containsKey("create-only");
        if (writeOnlySelectImages) {
            String whichImages = arguments.get("create-only");
            if (whichImages != null) {
                this.writeLayer0 = whichImages.contains("0");
                this.writeLayer1 = whichImages.contains("1");
                this.writeLayer2 = whichImages.contains("2");
                this.writeLayer3 = whichImages.contains("3");
                this.writeLayer4 = whichImages.contains("4");
                this.writeLayer5 = whichImages.contains("5");
                this.writeLayer6 = whichImages.contains("6");
                this.writeLayer7 = whichImages.contains("7");
                this.writeLayer8 = whichImages.contains("8");
                this.writeLayerh = whichImages.contains("h");
                return;
            }
            return;
        }
        this.writeLayer0 = true;
        this.writeLayer1 = true;
        this.writeLayer2 = true;
        this.writeLayer3 = true;
        this.writeLayer4 = true;
        this.writeLayer5 = true;
        this.writeLayer6 = true;
        this.writeLayer7 = true;
        this.writeLayer8 = true;
        this.writeLayerh = true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public SCMap importMap() throws MatchException {
        try {
            File dir = this.inMapPath.toFile();
            File[] mapFiles = (File[]) Objects.requireNonNull(dir.listFiles((file, filename) -> {
                return filename.endsWith(".scmap");
            }));
            if (mapFiles.length == 0) {
                throw new IOException("No scmap file in map folder");
            }
            SCMap map = MapImporter.importMap(this.inMapPath);
            SaveImporter.importSave(this.inMapPath, map);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error while importing the map.");
            throw new RuntimeException(e);
        }
    }

    public void writeMapImages(SCMap map) throws IOException {
        SplittableRandom splittableRandom = new SplittableRandom();
        FloatMask heightmapBase = new FloatMask(map.getHeightmap(), splittableRandom.split(), this.symmetrySettings, map.getHeightMapScale(), "heightmapBase");
        FloatMask[] textureMasksLow = new Vector4Mask(map.getTextureMasksLow(), splittableRandom.split(), this.symmetrySettings, 1.0f, "TextureMasksLow").subtractScalar(128.0f).divideScalar(127.0f).clampComponentMin(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT).clampComponentMax(1.0f).splitComponentMasks();
        FloatMask[] textureMasksHigh = new Vector4Mask(map.getTextureMasksHigh(), splittableRandom.split(), this.symmetrySettings, 1.0f, "TextureMasksHigh").subtractScalar(128.0f).divideScalar(127.0f).clampComponentMin(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT).clampComponentMax(1.0f).splitComponentMasks();
        if (this.writeLayerh) {
            ImageUtil.writePNGFromMask(heightmapBase, 1.0f, Paths.get(this.writeImagesPath, new String[0]).resolve("Heightmap.png"));
        }
        if (this.writeLayer1) {
            ImageUtil.writePNGFromMask(textureMasksLow[0], 255.0f, Paths.get(this.writeImagesPath, new String[0]).resolve("Layer 1.png"));
        }
        if (this.writeLayer2) {
            ImageUtil.writePNGFromMask(textureMasksLow[1], 255.0f, Paths.get(this.writeImagesPath, new String[0]).resolve("Layer 2.png"));
        }
        if (this.writeLayer3) {
            ImageUtil.writePNGFromMask(textureMasksLow[2], 255.0f, Paths.get(this.writeImagesPath, new String[0]).resolve("Layer 3.png"));
        }
        if (this.writeLayer4) {
            ImageUtil.writePNGFromMask(textureMasksLow[3], 255.0f, Paths.get(this.writeImagesPath, new String[0]).resolve("Layer 4.png"));
        }
        if (this.writeLayer5) {
            ImageUtil.writePNGFromMask(textureMasksHigh[0], 255.0f, Paths.get(this.writeImagesPath, new String[0]).resolve("Layer 5.png"));
        }
        if (this.writeLayer6) {
            ImageUtil.writePNGFromMask(textureMasksHigh[1], 255.0f, Paths.get(this.writeImagesPath, new String[0]).resolve("Layer 6.png"));
        }
        if (this.writeLayer7) {
            ImageUtil.writePNGFromMask(textureMasksHigh[2], 255.0f, Paths.get(this.writeImagesPath, new String[0]).resolve("Layer 7.png"));
        }
        if (this.writeLayer8) {
            ImageUtil.writePNGFromMask(textureMasksHigh[3], 255.0f, Paths.get(this.writeImagesPath, new String[0]).resolve("Layer 8.png"));
        }
        if (this.writeLayer0) {
            int mapImageSize = textureMasksLow[0].getSize();
            FloatMask oldLayer0 = new FloatMask(mapImageSize, splittableRandom.split(), this.symmetrySettings, "oldLayer0");
            oldLayer0.init(new BooleanMask(mapImageSize, splittableRandom.split(), this.symmetrySettings, "oldLayer0Inverted").invert(), Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), Float.valueOf(1.0f));
            Stream stream = Arrays.stream(textureMasksHigh);
            Objects.requireNonNull(oldLayer0);
            stream.forEach(oldLayer0::subtract);
            Stream stream2 = Arrays.stream(textureMasksLow);
            Objects.requireNonNull(oldLayer0);
            stream2.forEach(oldLayer0::subtract);
            oldLayer0.clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT));
            ImageUtil.writePNGFromMask(oldLayer0, 255.0f, Paths.get(this.writeImagesPath, new String[0]).resolve("Layer 0.png"));
        }
    }
}

package com.faforever.neroxis.exporter;

import com.faforever.neroxis.biomes.Biome;
import com.faforever.neroxis.util.FileUtil;
import com.faforever.neroxis.util.dds.DDSReader;
import com.faforever.neroxis.util.serial.biome.TerrainMaterials;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/BiomeExporter.class */
public class BiomeExporter {
    public static String filename = "";

    public static void exportBiome(Path envDir, Path folderPath, String biomeName, Biome biome) throws IOException {
        Files.createDirectories(folderPath.resolve(biomeName), new FileAttribute[0]);
        filename = folderPath.resolve(biomeName).resolve("Light.scmlighting").toString();
        FileUtil.serialize(filename, biome.lightingSettings());
        filename = folderPath.resolve(biomeName).resolve("WaterSettings.scmwtr").toString();
        FileUtil.serialize(filename, biome.waterSettings());
        List<String> previewColors = new ArrayList<>();
        TerrainMaterials oldTerrainMaterials = biome.terrainMaterials();
        List<TerrainMaterials.TextureScale> textures = oldTerrainMaterials.textures();
        for (int i = 0; i < 9; i++) {
            if (!textures.get(i).path().isBlank()) {
                Color c = getTexturePreviewColor(envDir, textures.get(i).path());
                if (c != null) {
                    previewColors.add(String.format("#%02x%02x%02x", Integer.valueOf(c.getRed()), Integer.valueOf(c.getGreen()), Integer.valueOf(c.getBlue())));
                } else {
                    previewColors.add("");
                }
            } else {
                previewColors.add("");
            }
        }
        TerrainMaterials newTerrainMaterials = new TerrainMaterials(oldTerrainMaterials.cubeMaps(), oldTerrainMaterials.textures(), oldTerrainMaterials.normals(), previewColors, oldTerrainMaterials.terrainTypes());
        filename = folderPath.resolve(biomeName).resolve("materials.json").toString();
        FileUtil.serialize(filename, newTerrainMaterials);
    }

    public static Color getTexturePreviewColor(Path envDir, String texturePath) throws IOException {
        File file = Paths.get(envDir.toString(), texturePath).toFile();
        InputStream inputStream = new FileInputStream(file);
        try {
            int[] pixels = DDSReader.read(inputStream.readAllBytes(), DDSReader.ARGB, 0);
            inputStream.close();
            if (pixels == null) {
                return null;
            }
            float avgR = 0.0f;
            float avgG = 0.0f;
            float avgB = 0.0f;
            for (int pixel : pixels) {
                Color pixColor = new Color(pixel);
                avgR += pixColor.getRed();
                avgG += pixColor.getGreen();
                avgB += pixColor.getBlue();
            }
            return new Color(((int) avgR) / pixels.length, ((int) avgG) / pixels.length, ((int) avgB) / pixels.length);
        } catch (Throwable th) {
            try {
                inputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }
}

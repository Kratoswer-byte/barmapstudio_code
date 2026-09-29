package com.faforever.neroxis.biomes;

import com.faforever.neroxis.util.FileUtil;
import com.faforever.neroxis.util.serial.biome.DecalMaterials;
import com.faforever.neroxis.util.serial.biome.LightingSettings;
import com.faforever.neroxis.util.serial.biome.PropMaterials;
import com.faforever.neroxis.util.serial.biome.TerrainMaterials;
import com.faforever.neroxis.util.serial.biome.WaterSettings;
import java.io.IOException;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/biomes/Biomes.class */
public class Biomes {
    private static final String CUSTOM_BIOMES_DIR = "/custom_biome/";

    public static Biome loadBiome(BiomeName biomeName) {
        String folderPath = "/custom_biome/" + biomeName.getFolderName() + "/";
        try {
            TerrainMaterials terrainMaterials = (TerrainMaterials) FileUtil.deserialize(folderPath + "materials.json", TerrainMaterials.class);
            try {
                PropMaterials propMaterials = (PropMaterials) FileUtil.deserialize(folderPath + "props.json", PropMaterials.class);
                try {
                    DecalMaterials decalMaterials = (DecalMaterials) FileUtil.deserialize(folderPath + "decals.json", DecalMaterials.class);
                    try {
                        WaterSettings waterSettings = (WaterSettings) FileUtil.deserialize(folderPath + "WaterSettings.scmwtr", WaterSettings.class);
                        try {
                            LightingSettings lightingSettings = (LightingSettings) FileUtil.deserialize(folderPath + "Light.scmlighting", LightingSettings.class);
                            return new Biome(biomeName, terrainMaterials, propMaterials, decalMaterials, waterSettings, lightingSettings);
                        } catch (IOException e) {
                            throw new RuntimeException(String.format("An error occurred while loading %s LightingSettings\n", folderPath), e);
                        }
                    } catch (IOException e2) {
                        throw new RuntimeException(String.format("An error occurred while loading %s WaterSettings\n", folderPath), e2);
                    }
                } catch (IOException e3) {
                    throw new RuntimeException(String.format("An error occurred while loading %sdecals.json\n", folderPath), e3);
                }
            } catch (IOException e4) {
                throw new RuntimeException(String.format("An error occurred while loading %sprops.json\n", folderPath), e4);
            }
        } catch (IOException e5) {
            throw new RuntimeException(String.format("An error occurred while loading %smaterials.json\n", folderPath), e5);
        }
    }
}

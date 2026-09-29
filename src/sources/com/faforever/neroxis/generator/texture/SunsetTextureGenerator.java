package com.faforever.neroxis.generator.texture;

import com.faforever.neroxis.biomes.Biome;
import com.faforever.neroxis.biomes.BiomeName;
import com.faforever.neroxis.biomes.Biomes;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/texture/SunsetTextureGenerator.class */
public class SunsetTextureGenerator extends PbrTextureGenerator {
    @Override // com.faforever.neroxis.generator.texture.TextureGenerator
    public Biome loadBiome() {
        return Biomes.loadBiome(BiomeName.SUNSET);
    }
}

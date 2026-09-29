package com.faforever.neroxis.util.serial.biome;

import com.faforever.neroxis.map.CubeMap;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/serial/biome/TerrainMaterials.class */
public final class TerrainMaterials extends Record {
    private final List<CubeMap> cubeMaps;
    private final List<TextureScale> textures;
    private final List<TextureScale> normals;
    private final List<String> previewColors;
    private final List<Integer> terrainTypes;
    public static final int TERRAIN_TEXTURE_COUNT = 10;
    public static final int TERRAIN_NORMAL_COUNT = 9;

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, TerrainMaterials.class), TerrainMaterials.class, "cubeMaps;textures;normals;previewColors;terrainTypes", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->cubeMaps:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->textures:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->normals:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->previewColors:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->terrainTypes:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, TerrainMaterials.class), TerrainMaterials.class, "cubeMaps;textures;normals;previewColors;terrainTypes", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->cubeMaps:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->textures:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->normals:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->previewColors:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->terrainTypes:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, TerrainMaterials.class, Object.class), TerrainMaterials.class, "cubeMaps;textures;normals;previewColors;terrainTypes", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->cubeMaps:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->textures:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->normals:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->previewColors:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;->terrainTypes:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public List<CubeMap> cubeMaps() {
        return this.cubeMaps;
    }

    public List<TextureScale> textures() {
        return this.textures;
    }

    public List<TextureScale> normals() {
        return this.normals;
    }

    public List<String> previewColors() {
        return this.previewColors;
    }

    public List<Integer> terrainTypes() {
        return this.terrainTypes;
    }

    public TerrainMaterials(List<CubeMap> cubeMaps, List<TextureScale> textures, List<TextureScale> normals, List<String> previewColors, List<Integer> terrainTypes) {
        List<CubeMap> cubeMaps2 = List.copyOf(cubeMaps);
        List<Integer> terrainTypes2 = List.copyOf(terrainTypes);
        List<TextureScale> textures2 = List.copyOf(textures);
        List<TextureScale> normals2 = List.copyOf(normals);
        List<String> previewColors2 = List.copyOf(previewColors);
        if (textures2.size() != 10) {
            throw new IllegalArgumentException("Texture paths does not have 10 items");
        }
        if (normals2.size() == 9) {
            this.cubeMaps = cubeMaps2;
            this.textures = textures2;
            this.normals = normals2;
            this.previewColors = previewColors2;
            this.terrainTypes = terrainTypes2;
            return;
        }
        throw new IllegalArgumentException("Normal paths does not have 9 items");
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/serial/biome/TerrainMaterials$TextureScale.class */
    public static final class TextureScale extends Record {
        private final String path;
        private final float scale;

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, TextureScale.class), TextureScale.class, "path;scale", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials$TextureScale;->path:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials$TextureScale;->scale:F").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, TextureScale.class), TextureScale.class, "path;scale", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials$TextureScale;->path:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials$TextureScale;->scale:F").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, TextureScale.class, Object.class), TextureScale.class, "path;scale", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials$TextureScale;->path:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials$TextureScale;->scale:F").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String path() {
            return this.path;
        }

        public float scale() {
            return this.scale;
        }

        public TextureScale(String path, float scale) {
            Objects.requireNonNull(path);
            this.path = path;
            this.scale = scale;
        }
    }
}

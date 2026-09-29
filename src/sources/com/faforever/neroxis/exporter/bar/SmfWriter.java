package com.faforever.neroxis.exporter.bar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/bar/SmfWriter.class */
final class SmfWriter {
    static final int SQUARE_SIZE = 8;
    static final int TEXELS_PER_SQUARE = 8;
    static final int TILE_SIZE = 32;
    static final int TILE_BYTES = 680;
    static final int MINIMAP_SIZE = 1024;
    static final int MINIMAP_MIP_LEVELS = 9;
    static final int MINIMAP_BYTES = 699048;
    private static final int HEADER_SIZE = 80;
    private static final int GRASS_HEADER_SIZE = 12;

    private SmfWriter() {
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/bar/SmfWriter$Feature.class */
    static final class Feature extends Record {
        private final int type;
        private final float x;
        private final float y;
        private final float z;
        private final float rotation;
        private final float relativeSize;

        Feature(int type, float x, float y, float z, float rotation, float relativeSize) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.z = z;
            this.rotation = rotation;
            this.relativeSize = relativeSize;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Feature.class), Feature.class, "type;x;y;z;rotation;relativeSize", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->type:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->x:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->y:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->z:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->rotation:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->relativeSize:F").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Feature.class), Feature.class, "type;x;y;z;rotation;relativeSize", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->type:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->x:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->y:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->z:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->rotation:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->relativeSize:F").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Feature.class, Object.class), Feature.class, "type;x;y;z;rotation;relativeSize", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->type:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->x:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->y:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->z:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->rotation:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/SmfWriter$Feature;->relativeSize:F").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public int type() {
            return this.type;
        }

        public float x() {
            return this.x;
        }

        public float y() {
            return this.y;
        }

        public float z() {
            return this.z;
        }

        public float rotation() {
            return this.rotation;
        }

        public float relativeSize() {
            return this.relativeSize;
        }
    }

    static void write(OutputStream out, int mapId, int mapX, int mapY, float minHeight, float maxHeight, short[] heightmap, byte[] metalMap, byte[] grassMap, byte[] minimap, String smtFileName, List<String> featureTypes, List<Feature> features) throws IOException {
        int halfCells = (mapX / 2) * (mapY / 2);
        int tileCount = (mapX / 4) * (mapY / 4);
        if (heightmap.length != (mapX + 1) * (mapY + 1) || metalMap.length != halfCells || grassMap.length != tileCount || minimap.length != MINIMAP_BYTES) {
            throw new IllegalArgumentException("Map data does not match the map dimensions");
        }
        byte[] smtName = smtFileName.getBytes(StandardCharsets.UTF_8);
        int tileSectionSize = 12 + smtName.length + 1 + (tileCount * 4);
        ByteArrayOutputStream featureBytes = new ByteArrayOutputStream();
        for (String featureType : featureTypes) {
            featureBytes.write(featureType.getBytes(StandardCharsets.UTF_8));
            featureBytes.write(0);
        }
        int featureSectionSize = 8 + featureBytes.size() + (features.size() * 24);
        int typeMapPtr = 92 + (heightmap.length * 2);
        int minimapPtr = typeMapPtr + halfCells;
        int metalMapPtr = minimapPtr + MINIMAP_BYTES;
        int tilesPtr = metalMapPtr + halfCells;
        int featurePtr = tilesPtr + tileSectionSize;
        int grassPtr = featurePtr + featureSectionSize;
        int grassSize = (mapX / 4) * (mapY / 4);
        ByteBuffer buffer = ByteBuffer.allocate(grassPtr + grassSize).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put("spring map file\u0000".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(1);
        buffer.putInt(mapId);
        buffer.putInt(mapX);
        buffer.putInt(mapY);
        buffer.putInt(8);
        buffer.putInt(8);
        buffer.putInt(32);
        buffer.putFloat(minHeight);
        buffer.putFloat(maxHeight);
        buffer.putInt(92);
        buffer.putInt(typeMapPtr);
        buffer.putInt(tilesPtr);
        buffer.putInt(minimapPtr);
        buffer.putInt(metalMapPtr);
        buffer.putInt(featurePtr);
        buffer.putInt(1);
        buffer.putInt(12);
        buffer.putInt(1);
        buffer.putInt(grassPtr);
        for (short height : heightmap) {
            buffer.putShort(height);
        }
        buffer.position(minimapPtr);
        buffer.put(minimap);
        buffer.put(metalMap);
        buffer.putInt(1);
        buffer.putInt(tileCount);
        buffer.putInt(tileCount);
        buffer.put(smtName);
        buffer.put((byte) 0);
        for (int i = 0; i < tileCount; i++) {
            buffer.putInt(i);
        }
        buffer.putInt(featureTypes.size());
        buffer.putInt(features.size());
        buffer.put(featureBytes.toByteArray());
        for (Feature feature : features) {
            buffer.putInt(feature.type());
            buffer.putFloat(feature.x());
            buffer.putFloat(feature.y());
            buffer.putFloat(feature.z());
            buffer.putFloat(feature.rotation());
            buffer.putFloat(feature.relativeSize());
        }
        buffer.put(grassMap);
        out.write(buffer.array());
    }

    static void writeSmtHeader(OutputStream out, int tileCount) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put("spring tilefile\u0000".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(1);
        buffer.putInt(tileCount);
        buffer.putInt(32);
        buffer.putInt(1);
        out.write(buffer.array());
    }
}

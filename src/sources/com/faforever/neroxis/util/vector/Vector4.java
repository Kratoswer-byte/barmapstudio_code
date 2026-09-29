package com.faforever.neroxis.util.vector;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/vector/Vector4.class */
public final class Vector4 extends Record implements Vector<Vector4> {
    private final float x;
    private final float y;
    private final float z;
    private final float w;

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Vector4.class), Vector4.class, "x;y;z;w", "FIELD:Lcom/faforever/neroxis/util/vector/Vector4;->x:F", "FIELD:Lcom/faforever/neroxis/util/vector/Vector4;->y:F", "FIELD:Lcom/faforever/neroxis/util/vector/Vector4;->z:F", "FIELD:Lcom/faforever/neroxis/util/vector/Vector4;->w:F").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Vector4.class, Object.class), Vector4.class, "x;y;z;w", "FIELD:Lcom/faforever/neroxis/util/vector/Vector4;->x:F", "FIELD:Lcom/faforever/neroxis/util/vector/Vector4;->y:F", "FIELD:Lcom/faforever/neroxis/util/vector/Vector4;->z:F", "FIELD:Lcom/faforever/neroxis/util/vector/Vector4;->w:F").dynamicInvoker().invoke(this, o) /* invoke-custom */;
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

    public float w() {
        return this.w;
    }

    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4() {
        this(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
    }

    private Vector4(float... values) {
        this(values[0], values[1], values[2], values[3]);
    }

    @Override // com.faforever.neroxis.util.vector.Vector
    public float get(int i) {
        switch (i) {
            case 0:
                return x();
            case 1:
                return y();
            case 2:
                return z();
            case 3:
                return w();
            default:
                throw new UnsupportedOperationException("Unsupported component: " + i);
        }
    }

    @Override // com.faforever.neroxis.util.vector.Vector
    public Vector4 transform(Vector.Transformer transformer) {
        return new Vector4(transformer.transform(0, x()), transformer.transform(1, y()), transformer.transform(2, z()), transformer.transform(3, w()));
    }

    @Override // com.faforever.neroxis.util.vector.Vector
    public int getDimension() {
        return 4;
    }

    @Override // com.faforever.neroxis.util.vector.Vector
    public float[] toArray() {
        return new float[]{x(), y(), z(), w()};
    }

    @Override // java.lang.Record
    public String toString() {
        float[] values = toArray();
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = String.format("%9f", Float.valueOf(get(i)));
        }
        return Arrays.toString(strings).replace("[", "").replace("]", "");
    }
}

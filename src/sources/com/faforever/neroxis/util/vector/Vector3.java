package com.faforever.neroxis.util.vector;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.LinkedHashSet;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/vector/Vector3.class */
public final class Vector3 extends Record implements Vector<Vector3> {
    private final float x;
    private final float y;
    private final float z;

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Vector3.class), Vector3.class, "x;y;z", "FIELD:Lcom/faforever/neroxis/util/vector/Vector3;->x:F", "FIELD:Lcom/faforever/neroxis/util/vector/Vector3;->y:F", "FIELD:Lcom/faforever/neroxis/util/vector/Vector3;->z:F").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Vector3.class, Object.class), Vector3.class, "x;y;z", "FIELD:Lcom/faforever/neroxis/util/vector/Vector3;->x:F", "FIELD:Lcom/faforever/neroxis/util/vector/Vector3;->y:F", "FIELD:Lcom/faforever/neroxis/util/vector/Vector3;->z:F").dynamicInvoker().invoke(this, o) /* invoke-custom */;
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

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3() {
        this(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
    }

    private Vector3(float... values) {
        this(values[0], values[1], values[2]);
    }

    public Vector3(Vector2 other) {
        this(other.x(), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, other.y());
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
            default:
                throw new UnsupportedOperationException("Unsupported component: " + i);
        }
    }

    @Override // com.faforever.neroxis.util.vector.Vector
    public Vector3 transform(Vector.Transformer transformer) {
        return new Vector3(transformer.transform(0, x()), transformer.transform(1, y()), transformer.transform(2, z()));
    }

    @Override // com.faforever.neroxis.util.vector.Vector
    public int getDimension() {
        return 3;
    }

    @Override // com.faforever.neroxis.util.vector.Vector
    public float[] toArray() {
        return new float[]{x(), y(), z()};
    }

    public Vector3 cross(Vector3 other) {
        float x = x();
        float oX = other.x();
        float y = y();
        float oY = other.y();
        float z = z();
        float oZ = other.z();
        float newX = (y * oZ) - (z * oY);
        float newY = (z * oX) - (x * oZ);
        float newZ = (x * oY) - (y * oX);
        return new Vector3(newX, newY, newZ);
    }

    public Vector3 rotateXZ(float angle) {
        float oldX = x();
        float oldZ = z();
        float cos = (float) StrictMath.cos(angle);
        float sin = (float) StrictMath.sin(angle);
        return new Vector3((oldX * cos) - (oldZ * sin), y(), (oldX * sin) + (oldZ * cos));
    }

    public float getXZDistance(Vector2 location) {
        return getXZDistance(new Vector3(location));
    }

    public float getXZDistance(Vector3 location) {
        float dx = x() - location.x();
        float dz = z() - location.z();
        return (float) StrictMath.sqrt((dx * dx) + (dz * dz));
    }

    public LinkedHashSet<Vector2> getXZLine(Vector3 location) {
        LinkedHashSet<Vector2> line = new LinkedHashSet<>();
        Vector2 currentPoint = new Vector2(this);
        Vector2 targetPoint = new Vector2(location);
        while (currentPoint.getDistance(targetPoint) > 1.0f) {
            line.add(currentPoint);
            float angle = currentPoint.angleTo(location);
            currentPoint = new Vector2(StrictMath.round(((double) currentPoint.x()) + StrictMath.cos(angle)), StrictMath.round(((double) currentPoint.y()) + StrictMath.sin(angle)));
        }
        return line;
    }

    public float getAzimuth() {
        return (float) StrictMath.atan2(z(), x());
    }

    public float getElevation() {
        return (float) StrictMath.atan2(y(), StrictMath.sqrt((x() * x()) + (z() * z())));
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

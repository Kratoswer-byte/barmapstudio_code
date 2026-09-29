package com.faforever.neroxis.util.vector;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.Symmetry;
import java.awt.Dimension;
import java.awt.Point;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.LinkedHashSet;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/vector/Vector2.class */
public final class Vector2 extends Record implements Vector<Vector2> {
    private final float x;
    private final float y;

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Vector2.class), Vector2.class, "x;y", "FIELD:Lcom/faforever/neroxis/util/vector/Vector2;->x:F", "FIELD:Lcom/faforever/neroxis/util/vector/Vector2;->y:F").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Vector2.class, Object.class), Vector2.class, "x;y", "FIELD:Lcom/faforever/neroxis/util/vector/Vector2;->x:F", "FIELD:Lcom/faforever/neroxis/util/vector/Vector2;->y:F").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2() {
        this(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
    }

    private Vector2(float... values) {
        this(values[0], values[1]);
    }

    public Vector2(Vector3 location) {
        this(location.x(), location.z());
    }

    public Vector2(Dimension other) {
        this((float) other.getWidth(), (float) other.getHeight());
    }

    public Vector2(Point other) {
        this((float) other.getX(), (float) other.getY());
    }

    @Override // com.faforever.neroxis.util.vector.Vector
    public float get(int i) {
        switch (i) {
            case 0:
                return x();
            case 1:
                return y();
            default:
                throw new UnsupportedOperationException("Unsupported component: " + i);
        }
    }

    @Override // com.faforever.neroxis.util.vector.Vector
    public Vector2 transform(Vector.Transformer transformer) {
        return new Vector2(transformer.transform(0, x()), transformer.transform(1, y()));
    }

    @Override // com.faforever.neroxis.util.vector.Vector
    public int getDimension() {
        return 2;
    }

    @Override // com.faforever.neroxis.util.vector.Vector
    public float[] toArray() {
        return new float[]{x(), y()};
    }

    public float angleTo(Vector3 location) {
        return angleTo(new Vector2(location));
    }

    public float angleTo(Vector2 location) {
        float dx = location.x() - x();
        float dy = location.y() - y();
        return (float) StrictMath.atan2(dy, dx);
    }

    public LinkedHashSet<Vector2> getLine(Vector2 location) {
        LinkedHashSet<Vector2> line = new LinkedHashSet<>();
        Vector2 vector2 = this;
        while (true) {
            Vector2 currentPoint = vector2;
            if (currentPoint.getDistance(location) > 1.0f) {
                line.add(currentPoint);
                float angle = currentPoint.angleTo(location);
                vector2 = new Vector2((float) (((double) currentPoint.x()) + StrictMath.cos(angle)), (float) (((double) currentPoint.y()) + StrictMath.sin(angle)));
            } else {
                return line;
            }
        }
    }

    public Vector2 addPolar(float angle, float magnitude) {
        return add((float) (((double) magnitude) * StrictMath.cos(angle)), (float) (((double) magnitude) * StrictMath.sin(angle)));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public Vector2 flip(Vector2 center, Symmetry symmetry) throws MatchException {
        switch (symmetry) {
            case X:
                return new Vector2((2.0f * center.x()) - x(), this.y);
            case Z:
                return new Vector2(this.x, (2.0f * center.y()) - y());
            case XZ:
            case ZX:
            case POINT2:
                return new Vector2((2.0f * center.x()) - x(), (2.0f * center.y()) - y());
            case POINT3:
            case NONE:
            case DIAG:
            case QUAD:
            case POINT16:
            case POINT15:
            case POINT14:
            case POINT13:
            case POINT12:
            case POINT11:
            case POINT10:
            case POINT9:
            case POINT8:
            case POINT7:
            case POINT6:
            case POINT5:
            case POINT4:
                return this;
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }

    public Vector2 rotate(float angle) {
        float oldX = x();
        float oldY = y();
        float cos = (float) StrictMath.cos(angle);
        float sin = (float) StrictMath.sin(angle);
        return new Vector2((oldX * cos) - (oldY * sin), (oldX * sin) + (oldY * cos));
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

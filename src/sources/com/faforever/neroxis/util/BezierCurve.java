package com.faforever.neroxis.util;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.util.vector.Vector2;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/BezierCurve.class */
public class BezierCurve {
    private static final Map<Integer, Integer[]> COEFFICIENTS_MAP = new HashMap();
    protected final Vector2[] controlPoints;
    protected final Integer[] coefficients;
    private final int order;

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BezierCurve)) {
            return false;
        }
        BezierCurve other = (BezierCurve) o;
        return other.canEqual(this) && getOrder() == other.getOrder() && Arrays.deepEquals(this.controlPoints, other.controlPoints) && Arrays.deepEquals(this.coefficients, other.coefficients);
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof BezierCurve;
    }

    @Generated
    public int hashCode() {
        int result = (1 * 59) + getOrder();
        return (((result * 59) + Arrays.deepHashCode(this.controlPoints)) * 59) + Arrays.deepHashCode(this.coefficients);
    }

    @Generated
    public int getOrder() {
        return this.order;
    }

    public BezierCurve(int order, RandomGenerator random) {
        this(new Vector2[order + 1]);
        this.controlPoints[0] = new Vector2(-0.5f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        this.controlPoints[order] = new Vector2(0.5f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        for (int i = 1; i < order; i++) {
            this.controlPoints[i] = new Vector2().randomize(random, -1.0f, 1.0f);
        }
    }

    public BezierCurve(Vector2... controlPoints) {
        this.controlPoints = controlPoints;
        this.order = controlPoints.length - 1;
        this.coefficients = COEFFICIENTS_MAP.computeIfAbsent(Integer.valueOf(this.order), (v0) -> {
            return computeCoefficients(v0);
        });
    }

    private static Integer[] computeCoefficients(int order) {
        int numControlPoints = order + 1;
        Integer[] coefficients = new Integer[numControlPoints];
        for (int i = 0; i < numControlPoints; i++) {
            coefficients[i] = Integer.valueOf(MathUtil.binomialCoefficient(order, i));
        }
        return coefficients;
    }

    public Vector2 getPoint(float t) {
        if (t < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT || t > 1.0f) {
            throw new IllegalArgumentException("t must be between 0 and 1");
        }
        Vector2 pointOnCurve = new Vector2(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        for (int i = 0; i < this.controlPoints.length; i++) {
            pointOnCurve = pointOnCurve.add(this.controlPoints[i].multiply((float) (((double) this.coefficients[i].intValue()) * StrictMath.pow(1.0f - t, this.order - i) * StrictMath.pow(t, i))));
        }
        return pointOnCurve;
    }

    public BezierCurve transformTo(Vector2 newStart, Vector2 newEnd) {
        Vector2 currentStart = getStart();
        Vector2 currentEnd = getEnd();
        float distanceRatio = newStart.getDistance(newEnd) / currentStart.getDistance(currentEnd);
        float angleDifference = newStart.angleTo(newEnd) - currentStart.angleTo(currentEnd);
        Vector2 positionDifference = newStart.subtract(currentStart);
        return rotate(angleDifference).scale(distanceRatio).translate(positionDifference);
    }

    public Vector2 getStart() {
        return this.controlPoints[0];
    }

    public Vector2 getEnd() {
        return this.controlPoints[this.controlPoints.length - 1];
    }

    private BezierCurve translate(Vector2 translationVector) {
        return translate(translationVector.x(), translationVector.y());
    }

    private BezierCurve translate(float xTranslation, float yTranslation) {
        Vector2[] newControlPoints = new Vector2[this.controlPoints.length];
        for (int i = 1; i < this.controlPoints.length; i++) {
            newControlPoints[i] = this.controlPoints[i].add(xTranslation, yTranslation);
        }
        return new BezierCurve(newControlPoints);
    }

    private BezierCurve scale(float factor) {
        Vector2[] newControlPoints = new Vector2[this.controlPoints.length];
        Vector2 origin = this.controlPoints[0];
        newControlPoints[0] = origin;
        for (int i = 1; i < this.controlPoints.length; i++) {
            newControlPoints[i] = this.controlPoints[i].subtract(origin).multiply(factor).add(origin);
        }
        return new BezierCurve(newControlPoints);
    }

    private BezierCurve rotate(float angle) {
        Vector2[] newControlPoints = new Vector2[this.controlPoints.length];
        Vector2 origin = this.controlPoints[0];
        newControlPoints[0] = origin;
        for (int i = 1; i < this.controlPoints.length; i++) {
            newControlPoints[i] = this.controlPoints[i].subtract(origin).rotate(angle).add(origin);
        }
        return new BezierCurve(newControlPoints);
    }

    public String toString() {
        String[] strings = new String[this.controlPoints.length];
        for (int i = 0; i < this.controlPoints.length; i++) {
            strings[i] = "(" + this.controlPoints[i].toString() + ")";
        }
        return Arrays.toString(strings).replace("[", "").replace("]", "");
    }
}

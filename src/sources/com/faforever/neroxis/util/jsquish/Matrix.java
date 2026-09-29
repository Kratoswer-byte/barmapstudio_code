package com.faforever.neroxis.util.jsquish;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import java.util.Arrays;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/jsquish/Matrix.class */
final class Matrix {
    private static final float FLT_EPSILON = 1.0E-5f;
    private final float[] values;

    Matrix() {
        this.values = new float[6];
    }

    Matrix(float a) {
        this.values = new float[6];
        Arrays.fill(this.values, a);
    }

    static Matrix computeWeightedCovariance(final ColourSet m_colours, Matrix covariance) {
        int count = m_colours.getCount();
        Vec[] points = m_colours.getPoints();
        float[] weights = m_colours.getWeights();
        Vec centroid = new Vec();
        Vec a = new Vec();
        Vec b = new Vec();
        float total = 0.0f;
        for (int i = 0; i < count; i++) {
            total += weights[i];
            centroid.add(a.set(points[i]).mul(weights[i]));
        }
        centroid.div(total);
        if (covariance == null) {
            covariance = new Matrix();
        } else {
            Arrays.fill(covariance.values, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        }
        float[] values = covariance.values;
        for (int i2 = 0; i2 < count; i2++) {
            a.set(points[i2]).sub(centroid);
            b.set(a).mul(weights[i2]);
            values[0] = values[0] + (a.x() * b.x());
            values[1] = values[1] + (a.x() * b.y());
            values[2] = values[2] + (a.x() * b.z());
            values[3] = values[3] + (a.y() * b.y());
            values[4] = values[4] + (a.y() * b.z());
            values[5] = values[5] + (a.z() * b.z());
        }
        return covariance;
    }

    static Vec computePrincipleComponent(final Matrix matrix) {
        float rt;
        float[] m = matrix.values;
        float c0 = (((((m[0] * m[3]) * m[5]) + (((2.0f * m[1]) * m[2]) * m[4])) - ((m[0] * m[4]) * m[4])) - ((m[3] * m[2]) * m[2])) - ((m[5] * m[1]) * m[1]);
        float c1 = (((((m[0] * m[3]) + (m[0] * m[5])) + (m[3] * m[5])) - (m[1] * m[1])) - (m[2] * m[2])) - (m[4] * m[4]);
        float c2 = m[0] + m[3] + m[5];
        float a = c1 - ((0.33333334f * c2) * c2);
        float b = (((((-0.074074075f) * c2) * c2) * c2) + ((0.33333334f * c1) * c2)) - c0;
        float Q = (0.25f * b * b) + (0.037037037f * a * a * a);
        if (FLT_EPSILON < Q) {
            return new Vec(1.0f);
        }
        if (Q < -1.0E-5f) {
            float theta = (float) Math.atan2(Math.sqrt(-Q), (-0.5f) * b);
            float rho = (float) Math.sqrt(((0.25f * b) * b) - Q);
            float rt2 = (float) Math.pow(rho, 0.3333333432674408d);
            float ct = (float) Math.cos(theta / 3.0f);
            float st = (float) Math.sin(theta / 3.0f);
            float l1 = (0.33333334f * c2) + (2.0f * rt2 * ct);
            float l2 = (0.33333334f * c2) - (rt2 * (ct + (((float) Math.sqrt(3.0d)) * st)));
            float l3 = (0.33333334f * c2) - (rt2 * (ct - (((float) Math.sqrt(3.0d)) * st)));
            if (Math.abs(l2) > Math.abs(l1)) {
                l1 = l2;
            }
            if (Math.abs(l3) > Math.abs(l1)) {
                l1 = l3;
            }
            return getMultiplicity1Evector(matrix, l1);
        }
        if (b < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            rt = (float) (-Math.pow((-0.5f) * b, 0.3333333432674408d));
        } else {
            rt = (float) Math.pow(0.5f * b, 0.3333333432674408d);
        }
        float l4 = (0.33333334f * c2) + rt;
        float l5 = (0.33333334f * c2) - (2.0f * rt);
        if (Math.abs(l4) > Math.abs(l5)) {
            return getMultiplicity2Evector(matrix, l4);
        }
        return getMultiplicity1Evector(matrix, l5);
    }

    private static Vec getMultiplicity1Evector(final Matrix matrix, final float evalue) {
        float[] values = matrix.values;
        float[] m = {values[0] - evalue, values[1], values[2], values[3] - evalue, values[4], values[5] - evalue};
        float[] u = {(m[3] * m[5]) - (m[4] * m[4]), (m[2] * m[4]) - (m[1] * m[5]), (m[1] * m[4]) - (m[2] * m[3]), (m[0] * m[5]) - (m[2] * m[2]), (m[1] * m[2]) - (m[4] * m[0]), (m[0] * m[3]) - (m[1] * m[1])};
        float mc = Math.abs(u[0]);
        int mi = 0;
        for (int i = 1; i < 6; i++) {
            float c = Math.abs(u[i]);
            if (c > mc) {
                mc = c;
                mi = i;
            }
        }
        switch (mi) {
            case 0:
                return new Vec(u[0], u[1], u[2]);
            case 1:
            case 3:
                return new Vec(u[1], u[3], u[4]);
            case 2:
            default:
                return new Vec(u[2], u[4], u[5]);
        }
    }

    private static Vec getMultiplicity2Evector(final Matrix matrix, final float evalue) {
        float[] values = matrix.values;
        float[] m = {values[0] - evalue, values[1], values[2], values[3] - evalue, values[4], values[5] - evalue};
        float mc = Math.abs(m[0]);
        int mi = 0;
        for (int i = 1; i < 6; i++) {
            float c = Math.abs(m[i]);
            if (c > mc) {
                mc = c;
                mi = i;
            }
        }
        switch (mi) {
            case 0:
            case 1:
                return new Vec(-m[1], m[0], SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            case 2:
                return new Vec(m[2], SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, -m[0]);
            case 3:
            case 4:
                return new Vec(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, -m[4], m[3]);
            default:
                return new Vec(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, -m[5], m[4]);
        }
    }

    float get(final int index) {
        return this.values[index];
    }
}

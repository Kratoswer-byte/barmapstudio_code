package com.faforever.neroxis.util;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/MathUtil.class */
public class MathUtil {
    public static float discretePercentage(float percent, int numBins) {
        return binPercentage(percent, numBins) / numBins;
    }

    public static int binPercentage(float percent, int numBins) {
        return StrictMath.max(StrictMath.min(StrictMath.round(percent * numBins), numBins), 0);
    }

    public static float normalizeBin(int bin, int numBins) {
        return bin / numBins;
    }

    public static float smootherStep(float val0, float val1, float weight) {
        return ((val1 - val0) * ((weight * ((weight * 6.0f) - 15.0f)) + 10.0f) * weight * weight * weight) + val0;
    }

    public static int binomialCoefficient(int n, int k) {
        float coefficient = 1.0f;
        for (int i = 1; i <= k; i++) {
            coefficient *= ((n + 1) - i) / i;
        }
        return (int) coefficient;
    }

    public static float log2(float val) {
        return (float) (StrictMath.log(val) / StrictMath.log(2.0d));
    }

    public static int clamp(int value, int min, int max) {
        return StrictMath.max(StrictMath.min(value, max), min);
    }
}

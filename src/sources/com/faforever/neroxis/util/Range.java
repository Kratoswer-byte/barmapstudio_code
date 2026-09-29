package com.faforever.neroxis.util;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/Range.class */
public final class Range extends Record {
    private final float min;
    private final float max;

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Range.class), Range.class, "min;max", "FIELD:Lcom/faforever/neroxis/util/Range;->min:F", "FIELD:Lcom/faforever/neroxis/util/Range;->max:F").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Range.class), Range.class, "min;max", "FIELD:Lcom/faforever/neroxis/util/Range;->min:F", "FIELD:Lcom/faforever/neroxis/util/Range;->max:F").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Range.class, Object.class), Range.class, "min;max", "FIELD:Lcom/faforever/neroxis/util/Range;->min:F", "FIELD:Lcom/faforever/neroxis/util/Range;->max:F").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public float min() {
        return this.min;
    }

    public float max() {
        return this.max;
    }

    public Range(float min, float max) {
        if (max >= min) {
            this.min = min;
            this.max = max;
            return;
        }
        throw new IllegalArgumentException(String.format("Max %f greater than Min %f", Float.valueOf(max), Float.valueOf(min)));
    }

    public static Range of(float min, float max) {
        return new Range(min, max);
    }

    public boolean contains(float value) {
        return value >= this.min && value <= this.max;
    }

    public float normalize(float value) {
        return StrictMath.max(StrictMath.min((value - this.min) / (this.max - this.min), 1.0f), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
    }

    public float map(float value) {
        return StrictMath.max(StrictMath.min((value * (this.max - this.min)) + this.min, this.max), this.min);
    }

    public float getRandomFloat(RandomGenerator random) {
        return (random.nextFloat() * (this.max - this.min)) + this.min;
    }
}

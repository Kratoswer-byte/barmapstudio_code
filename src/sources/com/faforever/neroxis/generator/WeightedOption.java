package com.faforever.neroxis.generator;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Objects;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/WeightedOption.class */
public final class WeightedOption<T> extends Record {
    private final T option;
    private final float weight;

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, WeightedOption.class), WeightedOption.class, "option;weight", "FIELD:Lcom/faforever/neroxis/generator/WeightedOption;->option:Ljava/lang/Object;", "FIELD:Lcom/faforever/neroxis/generator/WeightedOption;->weight:F").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, WeightedOption.class), WeightedOption.class, "option;weight", "FIELD:Lcom/faforever/neroxis/generator/WeightedOption;->option:Ljava/lang/Object;", "FIELD:Lcom/faforever/neroxis/generator/WeightedOption;->weight:F").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, WeightedOption.class, Object.class), WeightedOption.class, "option;weight", "FIELD:Lcom/faforever/neroxis/generator/WeightedOption;->option:Ljava/lang/Object;", "FIELD:Lcom/faforever/neroxis/generator/WeightedOption;->weight:F").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public T option() {
        return this.option;
    }

    public float weight() {
        return this.weight;
    }

    public WeightedOption(T option, float weight) {
        Objects.requireNonNull(option, "Option cannot be null");
        if (weight > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            this.option = option;
            this.weight = weight;
            return;
        }
        throw new IllegalArgumentException("Weight must be greater than 0");
    }

    public static <T> WeightedOption<T> of(T option, float weight) {
        return new WeightedOption<>(option, weight);
    }
}

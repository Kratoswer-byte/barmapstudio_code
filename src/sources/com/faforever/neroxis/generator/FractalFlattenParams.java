package com.faforever.neroxis.generator;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/FractalFlattenParams.class */
public final class FractalFlattenParams extends Record {
    private final Float minHeight;
    private final Float maxHeight;
    private final Float destinationMinHeight;
    private final Float destinationMaxHeight;
    private final Float slope;
    private final Integer edgeBlur;
    private final Boolean hasRamps;
    private final Float rampPercentage;
    private final Boolean spawnable;
    private final Integer spawnMaskDeflate;

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/FractalFlattenParams$FractalFlattenParamsBuilder.class */
    @Generated
    public static class FractalFlattenParamsBuilder {

        @Generated
        private Float minHeight;

        @Generated
        private Float maxHeight;

        @Generated
        private Float destinationMinHeight;

        @Generated
        private Float destinationMaxHeight;

        @Generated
        private Float slope;

        @Generated
        private Integer edgeBlur;

        @Generated
        private Boolean hasRamps;

        @Generated
        private Float rampPercentage;

        @Generated
        private Boolean spawnable;

        @Generated
        private Integer spawnMaskDeflate;

        @Generated
        FractalFlattenParamsBuilder() {
        }

        @Generated
        public FractalFlattenParamsBuilder minHeight(final Float minHeight) {
            this.minHeight = minHeight;
            return this;
        }

        @Generated
        public FractalFlattenParamsBuilder maxHeight(final Float maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }

        @Generated
        public FractalFlattenParamsBuilder destinationMinHeight(final Float destinationMinHeight) {
            this.destinationMinHeight = destinationMinHeight;
            return this;
        }

        @Generated
        public FractalFlattenParamsBuilder destinationMaxHeight(final Float destinationMaxHeight) {
            this.destinationMaxHeight = destinationMaxHeight;
            return this;
        }

        @Generated
        public FractalFlattenParamsBuilder slope(final Float slope) {
            this.slope = slope;
            return this;
        }

        @Generated
        public FractalFlattenParamsBuilder edgeBlur(final Integer edgeBlur) {
            this.edgeBlur = edgeBlur;
            return this;
        }

        @Generated
        public FractalFlattenParamsBuilder hasRamps(final Boolean hasRamps) {
            this.hasRamps = hasRamps;
            return this;
        }

        @Generated
        public FractalFlattenParamsBuilder rampPercentage(final Float rampPercentage) {
            this.rampPercentage = rampPercentage;
            return this;
        }

        @Generated
        public FractalFlattenParamsBuilder spawnable(final Boolean spawnable) {
            this.spawnable = spawnable;
            return this;
        }

        @Generated
        public FractalFlattenParamsBuilder spawnMaskDeflate(final Integer spawnMaskDeflate) {
            this.spawnMaskDeflate = spawnMaskDeflate;
            return this;
        }

        @Generated
        public FractalFlattenParams build() {
            return new FractalFlattenParams(this.minHeight, this.maxHeight, this.destinationMinHeight, this.destinationMaxHeight, this.slope, this.edgeBlur, this.hasRamps, this.rampPercentage, this.spawnable, this.spawnMaskDeflate);
        }

        @Generated
        public String toString() {
            return "FractalFlattenParams.FractalFlattenParamsBuilder(minHeight=" + this.minHeight + ", maxHeight=" + this.maxHeight + ", destinationMinHeight=" + this.destinationMinHeight + ", destinationMaxHeight=" + this.destinationMaxHeight + ", slope=" + this.slope + ", edgeBlur=" + this.edgeBlur + ", hasRamps=" + this.hasRamps + ", rampPercentage=" + this.rampPercentage + ", spawnable=" + this.spawnable + ", spawnMaskDeflate=" + this.spawnMaskDeflate + ")";
        }
    }

    @Generated
    public static FractalFlattenParamsBuilder builder() {
        return new FractalFlattenParamsBuilder();
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, FractalFlattenParams.class), FractalFlattenParams.class, "minHeight;maxHeight;destinationMinHeight;destinationMaxHeight;slope;edgeBlur;hasRamps;rampPercentage;spawnable;spawnMaskDeflate", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->minHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->maxHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->destinationMinHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->destinationMaxHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->slope:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->edgeBlur:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->hasRamps:Ljava/lang/Boolean;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->rampPercentage:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->spawnable:Ljava/lang/Boolean;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->spawnMaskDeflate:Ljava/lang/Integer;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, FractalFlattenParams.class), FractalFlattenParams.class, "minHeight;maxHeight;destinationMinHeight;destinationMaxHeight;slope;edgeBlur;hasRamps;rampPercentage;spawnable;spawnMaskDeflate", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->minHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->maxHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->destinationMinHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->destinationMaxHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->slope:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->edgeBlur:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->hasRamps:Ljava/lang/Boolean;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->rampPercentage:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->spawnable:Ljava/lang/Boolean;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->spawnMaskDeflate:Ljava/lang/Integer;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, FractalFlattenParams.class, Object.class), FractalFlattenParams.class, "minHeight;maxHeight;destinationMinHeight;destinationMaxHeight;slope;edgeBlur;hasRamps;rampPercentage;spawnable;spawnMaskDeflate", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->minHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->maxHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->destinationMinHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->destinationMaxHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->slope:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->edgeBlur:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->hasRamps:Ljava/lang/Boolean;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->rampPercentage:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->spawnable:Ljava/lang/Boolean;", "FIELD:Lcom/faforever/neroxis/generator/FractalFlattenParams;->spawnMaskDeflate:Ljava/lang/Integer;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public Float minHeight() {
        return this.minHeight;
    }

    public Float maxHeight() {
        return this.maxHeight;
    }

    public Float destinationMinHeight() {
        return this.destinationMinHeight;
    }

    public Float destinationMaxHeight() {
        return this.destinationMaxHeight;
    }

    public Float slope() {
        return this.slope;
    }

    public Integer edgeBlur() {
        return this.edgeBlur;
    }

    public Boolean hasRamps() {
        return this.hasRamps;
    }

    public Float rampPercentage() {
        return this.rampPercentage;
    }

    public Boolean spawnable() {
        return this.spawnable;
    }

    public Integer spawnMaskDeflate() {
        return this.spawnMaskDeflate;
    }

    public FractalFlattenParams(Float minHeight, Float maxHeight, Float destinationMinHeight, Float destinationMaxHeight, Float slope, Integer edgeBlur, Boolean hasRamps, Float rampPercentage, Boolean spawnable, Integer spawnMaskDeflate) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.destinationMinHeight = destinationMinHeight;
        this.destinationMaxHeight = destinationMaxHeight;
        this.slope = slope;
        this.edgeBlur = edgeBlur;
        this.hasRamps = hasRamps;
        this.rampPercentage = rampPercentage;
        this.spawnable = spawnable;
        this.spawnMaskDeflate = spawnMaskDeflate;
    }
}

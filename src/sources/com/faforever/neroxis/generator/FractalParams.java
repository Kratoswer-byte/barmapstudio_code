package com.faforever.neroxis.generator;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/FractalParams.class */
public final class FractalParams extends Record {
    private final Float waterHeight;
    private final FractalWaterMasks fractalWaterMask;
    private final Integer noiseMapBlurAmount;
    private final Integer noiseSmallestDetail;
    private final Float noiseOctaveMultiplier;
    private final Float noiseExpMultiplier;
    private final Integer teamSeparation;
    private final Integer spawnMaskDeflate;
    private final Float clampMapHeight;
    private final List<FractalFlattenParams> fractalFlattenParams;

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/FractalParams$FractalParamsBuilder.class */
    @Generated
    public static class FractalParamsBuilder {

        @Generated
        private Float waterHeight;

        @Generated
        private FractalWaterMasks fractalWaterMask;

        @Generated
        private Integer noiseMapBlurAmount;

        @Generated
        private Integer noiseSmallestDetail;

        @Generated
        private Float noiseOctaveMultiplier;

        @Generated
        private Float noiseExpMultiplier;

        @Generated
        private Integer teamSeparation;

        @Generated
        private Integer spawnMaskDeflate;

        @Generated
        private Float clampMapHeight;

        @Generated
        private List<FractalFlattenParams> fractalFlattenParams;

        @Generated
        FractalParamsBuilder() {
        }

        @Generated
        public FractalParamsBuilder waterHeight(final Float waterHeight) {
            this.waterHeight = waterHeight;
            return this;
        }

        @Generated
        public FractalParamsBuilder fractalWaterMask(final FractalWaterMasks fractalWaterMask) {
            this.fractalWaterMask = fractalWaterMask;
            return this;
        }

        @Generated
        public FractalParamsBuilder noiseMapBlurAmount(final Integer noiseMapBlurAmount) {
            this.noiseMapBlurAmount = noiseMapBlurAmount;
            return this;
        }

        @Generated
        public FractalParamsBuilder noiseSmallestDetail(final Integer noiseSmallestDetail) {
            this.noiseSmallestDetail = noiseSmallestDetail;
            return this;
        }

        @Generated
        public FractalParamsBuilder noiseOctaveMultiplier(final Float noiseOctaveMultiplier) {
            this.noiseOctaveMultiplier = noiseOctaveMultiplier;
            return this;
        }

        @Generated
        public FractalParamsBuilder noiseExpMultiplier(final Float noiseExpMultiplier) {
            this.noiseExpMultiplier = noiseExpMultiplier;
            return this;
        }

        @Generated
        public FractalParamsBuilder teamSeparation(final Integer teamSeparation) {
            this.teamSeparation = teamSeparation;
            return this;
        }

        @Generated
        public FractalParamsBuilder spawnMaskDeflate(final Integer spawnMaskDeflate) {
            this.spawnMaskDeflate = spawnMaskDeflate;
            return this;
        }

        @Generated
        public FractalParamsBuilder clampMapHeight(final Float clampMapHeight) {
            this.clampMapHeight = clampMapHeight;
            return this;
        }

        @Generated
        public FractalParamsBuilder fractalFlattenParams(final List<FractalFlattenParams> fractalFlattenParams) {
            this.fractalFlattenParams = fractalFlattenParams;
            return this;
        }

        @Generated
        public FractalParams build() {
            return new FractalParams(this.waterHeight, this.fractalWaterMask, this.noiseMapBlurAmount, this.noiseSmallestDetail, this.noiseOctaveMultiplier, this.noiseExpMultiplier, this.teamSeparation, this.spawnMaskDeflate, this.clampMapHeight, this.fractalFlattenParams);
        }

        @Generated
        public String toString() {
            return "FractalParams.FractalParamsBuilder(waterHeight=" + this.waterHeight + ", fractalWaterMask=" + String.valueOf(this.fractalWaterMask) + ", noiseMapBlurAmount=" + this.noiseMapBlurAmount + ", noiseSmallestDetail=" + this.noiseSmallestDetail + ", noiseOctaveMultiplier=" + this.noiseOctaveMultiplier + ", noiseExpMultiplier=" + this.noiseExpMultiplier + ", teamSeparation=" + this.teamSeparation + ", spawnMaskDeflate=" + this.spawnMaskDeflate + ", clampMapHeight=" + this.clampMapHeight + ", fractalFlattenParams=" + String.valueOf(this.fractalFlattenParams) + ")";
        }
    }

    @Generated
    public static FractalParamsBuilder builder() {
        return new FractalParamsBuilder();
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, FractalParams.class), FractalParams.class, "waterHeight;fractalWaterMask;noiseMapBlurAmount;noiseSmallestDetail;noiseOctaveMultiplier;noiseExpMultiplier;teamSeparation;spawnMaskDeflate;clampMapHeight;fractalFlattenParams", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->waterHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->fractalWaterMask:Lcom/faforever/neroxis/generator/FractalWaterMasks;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->noiseMapBlurAmount:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->noiseSmallestDetail:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->noiseOctaveMultiplier:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->noiseExpMultiplier:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->teamSeparation:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->spawnMaskDeflate:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->clampMapHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->fractalFlattenParams:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, FractalParams.class), FractalParams.class, "waterHeight;fractalWaterMask;noiseMapBlurAmount;noiseSmallestDetail;noiseOctaveMultiplier;noiseExpMultiplier;teamSeparation;spawnMaskDeflate;clampMapHeight;fractalFlattenParams", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->waterHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->fractalWaterMask:Lcom/faforever/neroxis/generator/FractalWaterMasks;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->noiseMapBlurAmount:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->noiseSmallestDetail:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->noiseOctaveMultiplier:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->noiseExpMultiplier:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->teamSeparation:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->spawnMaskDeflate:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->clampMapHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->fractalFlattenParams:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, FractalParams.class, Object.class), FractalParams.class, "waterHeight;fractalWaterMask;noiseMapBlurAmount;noiseSmallestDetail;noiseOctaveMultiplier;noiseExpMultiplier;teamSeparation;spawnMaskDeflate;clampMapHeight;fractalFlattenParams", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->waterHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->fractalWaterMask:Lcom/faforever/neroxis/generator/FractalWaterMasks;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->noiseMapBlurAmount:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->noiseSmallestDetail:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->noiseOctaveMultiplier:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->noiseExpMultiplier:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->teamSeparation:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->spawnMaskDeflate:Ljava/lang/Integer;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->clampMapHeight:Ljava/lang/Float;", "FIELD:Lcom/faforever/neroxis/generator/FractalParams;->fractalFlattenParams:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public Float waterHeight() {
        return this.waterHeight;
    }

    public FractalWaterMasks fractalWaterMask() {
        return this.fractalWaterMask;
    }

    public Integer noiseMapBlurAmount() {
        return this.noiseMapBlurAmount;
    }

    public Integer noiseSmallestDetail() {
        return this.noiseSmallestDetail;
    }

    public Float noiseOctaveMultiplier() {
        return this.noiseOctaveMultiplier;
    }

    public Float noiseExpMultiplier() {
        return this.noiseExpMultiplier;
    }

    public Integer teamSeparation() {
        return this.teamSeparation;
    }

    public Integer spawnMaskDeflate() {
        return this.spawnMaskDeflate;
    }

    public Float clampMapHeight() {
        return this.clampMapHeight;
    }

    public List<FractalFlattenParams> fractalFlattenParams() {
        return this.fractalFlattenParams;
    }

    public FractalParams(Float waterHeight, FractalWaterMasks fractalWaterMask, Integer noiseMapBlurAmount, Integer noiseSmallestDetail, Float noiseOctaveMultiplier, Float noiseExpMultiplier, Integer teamSeparation, Integer spawnMaskDeflate, Float clampMapHeight, List<FractalFlattenParams> fractalFlattenParams) {
        this.waterHeight = waterHeight;
        this.fractalWaterMask = fractalWaterMask;
        this.noiseMapBlurAmount = noiseMapBlurAmount;
        this.noiseSmallestDetail = noiseSmallestDetail;
        this.noiseOctaveMultiplier = noiseOctaveMultiplier;
        this.noiseExpMultiplier = noiseExpMultiplier;
        this.teamSeparation = teamSeparation;
        this.spawnMaskDeflate = spawnMaskDeflate;
        this.clampMapHeight = clampMapHeight;
        this.fractalFlattenParams = fractalFlattenParams;
    }
}

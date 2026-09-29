package com.faforever.neroxis.generator.util.serial;

import com.faforever.neroxis.generator.ParameterConstraints;
import com.faforever.neroxis.generator.resource.BasicResourceGenerator;
import com.faforever.neroxis.generator.resource.HighMexLandLowMexWaterResourceGenerator;
import com.faforever.neroxis.generator.resource.LowMexResourceGenerator;
import com.faforever.neroxis.generator.resource.OneHydroFourMexResourceGenerator;
import com.faforever.neroxis.generator.resource.OneHydroPerSpawnResourceGenerator;
import com.faforever.neroxis.generator.resource.ResourceGenerator;
import com.faforever.neroxis.generator.resource.WaterMexResourceGenerator;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.generator.util.HasParameterConstraints;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.function.Supplier;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/util/serial/ResourceStyle.class */
public enum ResourceStyle implements HasParameterConstraints {
    BASIC(BasicResourceGenerator::new, ParameterConstraints.ANY),
    LOW_MEX(LowMexResourceGenerator::new, ParameterConstraints.builder().mapSizes(384.0f, 768.0f).spawnCount(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 4.0f).build()),
    WATER_MEX(WaterMexResourceGenerator::new, ParameterConstraints.ANY),
    HI_MEX_LAND_LOW_MEX_WATER(HighMexLandLowMexWaterResourceGenerator::new, ParameterConstraints.ANY),
    ONE_HYDRO_NO_MEX(OneHydroPerSpawnResourceGenerator::new, ParameterConstraints.ANY),
    ONE_HYDRO_FOUR_MEX(OneHydroFourMexResourceGenerator::new, ParameterConstraints.ANY);

    private final Supplier<ResourceGenerator> generatorSupplier;
    private final ParameterConstraints parameterConstraints;

    @Generated
    ResourceStyle(final Supplier generatorSupplier, final ParameterConstraints parameterConstraints) {
        this.generatorSupplier = generatorSupplier;
        this.parameterConstraints = parameterConstraints;
    }

    @Generated
    public Supplier<ResourceGenerator> getGeneratorSupplier() {
        return this.generatorSupplier;
    }

    @Generated
    public ParameterConstraints getParameterConstraints() {
        return this.parameterConstraints;
    }

    @Override // com.faforever.neroxis.generator.util.HasParameterConstraints
    @JsonIgnore
    public ParameterConstraints parameterConstraints() {
        return this.parameterConstraints;
    }
}

package com.faforever.neroxis.generator.util.serial;

import com.faforever.neroxis.biomes.BiomeName;
import com.faforever.neroxis.generator.ParameterConstraints;
import com.faforever.neroxis.generator.WeightedOption;
import com.faforever.neroxis.generator.WeightedOptionsWithFallback;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.generator.util.HasParameterConstraints;
import com.faforever.neroxis.util.MathUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/util/serial/MapStyle.class */
public interface MapStyle {
    String name();

    WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions();

    WeightedOptionsWithFallback<BiomeName> getBiomeNameOptions();

    WeightedOptionsWithFallback<ResourceStyle> getResourceStyleOptions();

    WeightedOptionsWithFallback<PropStyle> getPropStyleOptions();

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/util/serial/MapStyle$Predefined.class */
    public enum Predefined implements MapStyle, HasParameterConstraints {
        BASIC(ParameterConstraints.ANY) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.1
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 0.1f), WeightedOption.of(PropStyle.ENEMY_CIV, 0.5f), WeightedOption.of(PropStyle.HIGH_RECLAIM, 0.25f), WeightedOption.of(PropStyle.LARGE_BATTLE, 0.5f), WeightedOption.of(PropStyle.NAVY_WRECKS, 2.0f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        BIG_ISLANDS(ParameterConstraints.builder().mapSizes(768.0f, 1024.0f).build()) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.2
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.BIG_ISLANDS, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 0.1f), WeightedOption.of(PropStyle.ENEMY_CIV, 0.5f), WeightedOption.of(PropStyle.NAVY_WRECKS, 2.0f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        CENTER_LAKE(ParameterConstraints.builder().mapSizes(384.0f, 1024.0f).build()) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.3
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.CENTER_LAKE_LAST, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 0.1f), WeightedOption.of(PropStyle.ENEMY_CIV, 0.5f), WeightedOption.of(PropStyle.NAVY_WRECKS, 2.0f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        DROP_PLATEAU(ParameterConstraints.ANY) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.4
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.DROP_PLATEAU_LAST, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 0.1f), WeightedOption.of(PropStyle.ENEMY_CIV, 0.5f), WeightedOption.of(PropStyle.HIGH_RECLAIM, 0.5f), WeightedOption.of(PropStyle.LARGE_BATTLE, 0.5f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        FLOODED(ParameterConstraints.builder().mapSizes(384.0f, 1024.0f).build()) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.5
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.FLOODED, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<ResourceStyle> getResourceStyleOptions() {
                return WeightedOptionsWithFallback.of(ResourceStyle.WATER_MEX, (WeightedOption<ResourceStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.NAVY_WRECKS, 2.0f)});
            }
        },
        HIGH_RECLAIM(ParameterConstraints.ANY) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.6
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.BASIC_LAST, (WeightedOption<TerrainStyle>[]) new WeightedOption[]{WeightedOption.of(TerrainStyle.DROP_PLATEAU_LAST, 1.0f), WeightedOption.of(TerrainStyle.MOUNTAIN_RANGE_LAST, 1.0f), WeightedOption.of(TerrainStyle.LITTLE_MOUNTAIN_LAST, 1.0f), WeightedOption.of(TerrainStyle.VALLEY_LAST, 1.0f)});
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.HIGH_RECLAIM, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.HIGH_RECLAIM, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 1.0f)});
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<BiomeName> getBiomeNameOptions() {
                return WeightedOptionsWithFallback.of(BiomeName.DESERT, (WeightedOption<BiomeName>[]) new WeightedOption[]{WeightedOption.of(BiomeName.DESERT, 1.0f), WeightedOption.of(BiomeName.FRITHEN, 1.0f), WeightedOption.of(BiomeName.MOONLIGHT, 1.0f), WeightedOption.of(BiomeName.SUNSET, 1.0f), WeightedOption.of(BiomeName.WONDER, 1.0f)});
            }
        },
        LAND_BRIDGE(ParameterConstraints.builder().mapSizes(768.0f, 1024.0f).numTeams(2.0f, 4.0f).build()) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.7
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.LAND_BRIDGE, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.LARGE_BATTLE, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BOULDER_FIELD, 0.1f), WeightedOption.of(PropStyle.LARGE_BATTLE, 0.5f), WeightedOption.of(PropStyle.NAVY_WRECKS, 2.0f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        LITTLE_MOUNTAIN(ParameterConstraints.ANY) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.8
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.LITTLE_MOUNTAIN_LAST, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 0.1f), WeightedOption.of(PropStyle.ENEMY_CIV, 0.5f), WeightedOption.of(PropStyle.HIGH_RECLAIM, 0.5f), WeightedOption.of(PropStyle.LARGE_BATTLE, 0.5f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        LOW_MEX(ParameterConstraints.builder().mapSizes(256.0f, 640.0f).spawnCount(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 4.0f).numTeams(2.0f, 2.0f).build()) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.9
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<ResourceStyle> getResourceStyleOptions() {
                return WeightedOptionsWithFallback.of(ResourceStyle.LOW_MEX, (WeightedOption<ResourceStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.BASIC_LAST, (WeightedOption<TerrainStyle>[]) new WeightedOption[]{WeightedOption.of(TerrainStyle.BASIC_LAST, 1.0f), WeightedOption.of(TerrainStyle.ONE_ISLAND, 1.0f), WeightedOption.of(TerrainStyle.LITTLE_MOUNTAIN_LAST, 1.0f), WeightedOption.of(TerrainStyle.VALLEY_LAST, 1.0f)});
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 0.1f), WeightedOption.of(PropStyle.ENEMY_CIV, 0.5f), WeightedOption.of(PropStyle.HIGH_RECLAIM, 0.25f), WeightedOption.of(PropStyle.LARGE_BATTLE, 0.5f), WeightedOption.of(PropStyle.NAVY_WRECKS, 2.0f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        MOUNTAIN_RANGE(ParameterConstraints.builder().mapSizes(256.0f, 640.0f).build()) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.10
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.MOUNTAIN_RANGE_LAST, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 0.1f), WeightedOption.of(PropStyle.ENEMY_CIV, 0.5f), WeightedOption.of(PropStyle.HIGH_RECLAIM, 0.25f), WeightedOption.of(PropStyle.LARGE_BATTLE, 0.5f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        MULTILEVEL(ParameterConstraints.ANY) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.11
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.MULTILEVEL_LAST, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 1.0f), WeightedOption.of(PropStyle.ENEMY_CIV, 0.5f), WeightedOption.of(PropStyle.HIGH_RECLAIM, 0.25f), WeightedOption.of(PropStyle.LARGE_BATTLE, 0.5f), WeightedOption.of(PropStyle.SMALL_BATTLE, 0.5f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f)});
            }
        },
        ONE_ISLAND(ParameterConstraints.builder().mapSizes(384.0f, 1024.0f).build()) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.12
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.ONE_ISLAND, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 0.1f), WeightedOption.of(PropStyle.NAVY_WRECKS, 2.0f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        SMALL_ISLANDS(ParameterConstraints.builder().mapSizes(768.0f, 1024.0f).build()) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.13
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.SMALL_ISLANDS, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 0.1f), WeightedOption.of(PropStyle.NAVY_WRECKS, 2.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        VALLEY(ParameterConstraints.builder().mapSizes(384.0f, 1024.0f).build()) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.14
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.VALLEY_LAST, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 0.1f), WeightedOption.of(PropStyle.ENEMY_CIV, 0.5f), WeightedOption.of(PropStyle.HIGH_RECLAIM, 0.5f), WeightedOption.of(PropStyle.LARGE_BATTLE, 0.5f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        RIVERS(ParameterConstraints.ANY) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.15
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.RIVERS, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 0.1f), WeightedOption.of(PropStyle.ENEMY_CIV, 0.5f), WeightedOption.of(PropStyle.HIGH_RECLAIM, 0.25f), WeightedOption.of(PropStyle.LARGE_BATTLE, 0.5f), WeightedOption.of(PropStyle.NAVY_WRECKS, 2.0f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        RIVERS_AND_OCEANS(ParameterConstraints.ANY) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.16
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<ResourceStyle> getResourceStyleOptions() {
                return WeightedOptionsWithFallback.of(ResourceStyle.HI_MEX_LAND_LOW_MEX_WATER, (WeightedOption<ResourceStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.RIVERS_AND_OCEANS, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 0.1f), WeightedOption.of(PropStyle.ENEMY_CIV, 0.5f), WeightedOption.of(PropStyle.HIGH_RECLAIM, 0.25f), WeightedOption.of(PropStyle.LARGE_BATTLE, 0.5f), WeightedOption.of(PropStyle.NAVY_WRECKS, 2.0f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        FRACTAL_LAND(ParameterConstraints.ANY) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.17
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.FRACTAL_LAND, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 0.5f), WeightedOption.of(PropStyle.ENEMY_CIV, 0.5f), WeightedOption.of(PropStyle.HIGH_RECLAIM, 0.25f), WeightedOption.of(PropStyle.LARGE_BATTLE, 0.5f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        FRACTAL_PLATEAU(ParameterConstraints.ANY) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.18
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.FRACTAL_PLATEAU, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 0.5f), WeightedOption.of(PropStyle.ENEMY_CIV, 0.5f), WeightedOption.of(PropStyle.HIGH_RECLAIM, 0.5f), WeightedOption.of(PropStyle.LARGE_BATTLE, 0.5f), WeightedOption.of(PropStyle.NEUTRAL_CIV, 1.0f), WeightedOption.of(PropStyle.ROCK_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 1.0f)});
            }
        },
        FRACTAL_NAVY(ParameterConstraints.ANY) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.19
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<ResourceStyle> getResourceStyleOptions() {
                return WeightedOptionsWithFallback.of(ResourceStyle.HI_MEX_LAND_LOW_MEX_WATER, (WeightedOption<ResourceStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.FRACTAL_NAVY, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.NAVY_WRECKS, 3.0f), WeightedOption.of(PropStyle.BOULDER_FIELD, 2.0f), WeightedOption.of(PropStyle.HIGH_RECLAIM, 1.0f), WeightedOption.of(PropStyle.LARGE_BATTLE, 0.5f), WeightedOption.of(PropStyle.ROCK_FIELD, 0.5f), WeightedOption.of(PropStyle.SMALL_BATTLE, 0.5f)});
            }
        },
        SETONISH(ParameterConstraints.builder().mapSizes(512.0f, 1024.0f).numTeams(2.0f, 2.0f).build()) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.20
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.SETONS, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[]{WeightedOption.of(PropStyle.BASIC, 1.0f), WeightedOption.of(PropStyle.LARGE_BATTLE, 0.5f), WeightedOption.of(PropStyle.BOULDER_FIELD, 1.0f), WeightedOption.of(PropStyle.SMALL_BATTLE, 2.0f)});
            }
        },
        FORREST_SOMETHING(ParameterConstraints.ANY) { // from class: com.faforever.neroxis.generator.util.serial.MapStyle.Predefined.21
            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
                return WeightedOptionsWithFallback.of(PropStyle.FORREST_SOMETHING, (WeightedOption<PropStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<ResourceStyle> getResourceStyleOptions() {
                return WeightedOptionsWithFallback.of(ResourceStyle.ONE_HYDRO_NO_MEX, (WeightedOption<ResourceStyle>[]) new WeightedOption[0]);
            }

            @Override // com.faforever.neroxis.generator.util.serial.MapStyle.Predefined, com.faforever.neroxis.generator.util.serial.MapStyle
            public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
                return WeightedOptionsWithFallback.of(TerrainStyle.BASIC_LAST, (WeightedOption<TerrainStyle>[]) new WeightedOption[]{WeightedOption.of(TerrainStyle.BASIC_LAST, 1.0f), WeightedOption.of(TerrainStyle.BIG_ISLANDS, 1.0f), WeightedOption.of(TerrainStyle.CENTER_LAKE_LAST, 1.0f), WeightedOption.of(TerrainStyle.DROP_PLATEAU_LAST, 1.0f), WeightedOption.of(TerrainStyle.FRACTAL_LAND, 1.0f), WeightedOption.of(TerrainStyle.FRACTAL_PLATEAU, 1.0f), WeightedOption.of(TerrainStyle.LAND_BRIDGE, 1.0f), WeightedOption.of(TerrainStyle.LITTLE_MOUNTAIN_LAST, 1.0f), WeightedOption.of(TerrainStyle.MOUNTAIN_RANGE_LAST, 1.0f), WeightedOption.of(TerrainStyle.MULTILEVEL_LAST, 1.0f), WeightedOption.of(TerrainStyle.ONE_ISLAND, 1.0f), WeightedOption.of(TerrainStyle.RIVERS, 1.0f), WeightedOption.of(TerrainStyle.VALLEY_LAST, 1.0f)});
            }
        };

        private final ParameterConstraints parameterConstraints;

        @Generated
        Predefined(final ParameterConstraints parameterConstraints) {
            this.parameterConstraints = parameterConstraints;
        }

        @Override // com.faforever.neroxis.generator.util.HasParameterConstraints
        @JsonIgnore
        public ParameterConstraints parameterConstraints() {
            return this.parameterConstraints;
        }

        @Override // com.faforever.neroxis.generator.util.serial.MapStyle
        @JsonIgnore
        public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
            return WeightedOptionsWithFallback.of(TerrainStyle.BASIC_LAST, (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
        }

        @Override // com.faforever.neroxis.generator.util.serial.MapStyle
        @JsonIgnore
        public WeightedOptionsWithFallback<BiomeName> getBiomeNameOptions() {
            return WeightedOptionsWithFallback.of(BiomeName.BRIMSTONE, (WeightedOption<BiomeName>[]) new WeightedOption[]{WeightedOption.of(BiomeName.BRIMSTONE, 1.0f), WeightedOption.of(BiomeName.DESERT, 1.0f), WeightedOption.of(BiomeName.EARLYAUTUMN, 1.0f), WeightedOption.of(BiomeName.FRITHEN, 1.0f), WeightedOption.of(BiomeName.MARS, 1.0f), WeightedOption.of(BiomeName.PRAYER, 1.0f), WeightedOption.of(BiomeName.STONES, 1.0f), WeightedOption.of(BiomeName.SUNSET, 1.0f), WeightedOption.of(BiomeName.WINDINGRIVER, 1.0f), WeightedOption.of(BiomeName.WONDER, 1.0f), WeightedOption.of(BiomeName.CRYSTALLINE, 1.0f)});
        }

        @Override // com.faforever.neroxis.generator.util.serial.MapStyle
        @JsonIgnore
        public WeightedOptionsWithFallback<ResourceStyle> getResourceStyleOptions() {
            return WeightedOptionsWithFallback.of(ResourceStyle.BASIC, (WeightedOption<ResourceStyle>[]) new WeightedOption[0]);
        }

        @Override // com.faforever.neroxis.generator.util.serial.MapStyle
        @JsonIgnore
        public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
            return WeightedOptionsWithFallback.of(PropStyle.BASIC, (WeightedOption<PropStyle>[]) new WeightedOption[0]);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/util/serial/MapStyle$Custom.class */
    public static final class Custom extends Record implements MapStyle {
        private final TerrainStyle terrainStyle;
        private final BiomeName biomeName;
        private final PropStyle propStyle;
        private final ResourceStyle resourceStyle;
        private final float reclaimDensity;
        private final float resourceDensity;

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Custom.class), Custom.class, "terrainStyle;biomeName;propStyle;resourceStyle;reclaimDensity;resourceDensity", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->terrainStyle:Lcom/faforever/neroxis/generator/util/serial/TerrainStyle;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->biomeName:Lcom/faforever/neroxis/biomes/BiomeName;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->propStyle:Lcom/faforever/neroxis/generator/util/serial/PropStyle;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->resourceStyle:Lcom/faforever/neroxis/generator/util/serial/ResourceStyle;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->reclaimDensity:F", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->resourceDensity:F").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Custom.class), Custom.class, "terrainStyle;biomeName;propStyle;resourceStyle;reclaimDensity;resourceDensity", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->terrainStyle:Lcom/faforever/neroxis/generator/util/serial/TerrainStyle;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->biomeName:Lcom/faforever/neroxis/biomes/BiomeName;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->propStyle:Lcom/faforever/neroxis/generator/util/serial/PropStyle;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->resourceStyle:Lcom/faforever/neroxis/generator/util/serial/ResourceStyle;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->reclaimDensity:F", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->resourceDensity:F").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Custom.class, Object.class), Custom.class, "terrainStyle;biomeName;propStyle;resourceStyle;reclaimDensity;resourceDensity", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->terrainStyle:Lcom/faforever/neroxis/generator/util/serial/TerrainStyle;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->biomeName:Lcom/faforever/neroxis/biomes/BiomeName;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->propStyle:Lcom/faforever/neroxis/generator/util/serial/PropStyle;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->resourceStyle:Lcom/faforever/neroxis/generator/util/serial/ResourceStyle;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->reclaimDensity:F", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapStyle$Custom;->resourceDensity:F").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public TerrainStyle terrainStyle() {
            return this.terrainStyle;
        }

        public BiomeName biomeName() {
            return this.biomeName;
        }

        public PropStyle propStyle() {
            return this.propStyle;
        }

        public ResourceStyle resourceStyle() {
            return this.resourceStyle;
        }

        public float reclaimDensity() {
            return this.reclaimDensity;
        }

        public float resourceDensity() {
            return this.resourceDensity;
        }

        public Custom(TerrainStyle terrainStyle, BiomeName biomeName, PropStyle propStyle, ResourceStyle resourceStyle, float reclaimDensity, float resourceDensity) {
            float reclaimDensity2 = MathUtil.discretePercentage(reclaimDensity, GeneratedMapNameEncoder.NUM_BINS);
            float resourceDensity2 = MathUtil.discretePercentage(resourceDensity, GeneratedMapNameEncoder.NUM_BINS);
            this.terrainStyle = terrainStyle;
            this.biomeName = biomeName;
            this.propStyle = propStyle;
            this.resourceStyle = resourceStyle;
            this.reclaimDensity = reclaimDensity2;
            this.resourceDensity = resourceDensity2;
        }

        @Override // com.faforever.neroxis.generator.util.serial.MapStyle
        public String name() {
            return "Custom";
        }

        @Override // com.faforever.neroxis.generator.util.serial.MapStyle
        @JsonIgnore
        public WeightedOptionsWithFallback<TerrainStyle> getTerrainStyleOptions() {
            return WeightedOptionsWithFallback.of(terrainStyle(), (WeightedOption<TerrainStyle>[]) new WeightedOption[0]);
        }

        @Override // com.faforever.neroxis.generator.util.serial.MapStyle
        @JsonIgnore
        public WeightedOptionsWithFallback<BiomeName> getBiomeNameOptions() {
            return WeightedOptionsWithFallback.of(biomeName(), (WeightedOption<BiomeName>[]) new WeightedOption[0]);
        }

        @Override // com.faforever.neroxis.generator.util.serial.MapStyle
        @JsonIgnore
        public WeightedOptionsWithFallback<ResourceStyle> getResourceStyleOptions() {
            return WeightedOptionsWithFallback.of(resourceStyle(), (WeightedOption<ResourceStyle>[]) new WeightedOption[0]);
        }

        @Override // com.faforever.neroxis.generator.util.serial.MapStyle
        @JsonIgnore
        public WeightedOptionsWithFallback<PropStyle> getPropStyleOptions() {
            return WeightedOptionsWithFallback.of(propStyle(), (WeightedOption<PropStyle>[]) new WeightedOption[0]);
        }
    }
}

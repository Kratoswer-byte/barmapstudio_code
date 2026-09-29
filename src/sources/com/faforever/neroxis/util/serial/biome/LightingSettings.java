package com.faforever.neroxis.util.serial.biome;

import com.faforever.neroxis.util.vector.Vector3;
import com.faforever.neroxis.util.vector.Vector4;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Objects;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/serial/biome/LightingSettings.class */
public final class LightingSettings extends Record {
    private final float lightingMultiplier;
    private final Vector3 sunDirection;
    private final Vector3 sunAmbience;
    private final Vector3 sunColor;
    private final Vector3 shadowFillColor;
    private final Vector4 specularColor;
    private final float bloom;
    private final Vector3 fogColor;
    private final float fogStart;
    private final float fogEnd;

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, LightingSettings.class), LightingSettings.class, "lightingMultiplier;sunDirection;sunAmbience;sunColor;shadowFillColor;specularColor;bloom;fogColor;fogStart;fogEnd", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->lightingMultiplier:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->sunDirection:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->sunAmbience:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->sunColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->shadowFillColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->specularColor:Lcom/faforever/neroxis/util/vector/Vector4;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->bloom:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->fogColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->fogStart:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->fogEnd:F").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, LightingSettings.class), LightingSettings.class, "lightingMultiplier;sunDirection;sunAmbience;sunColor;shadowFillColor;specularColor;bloom;fogColor;fogStart;fogEnd", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->lightingMultiplier:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->sunDirection:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->sunAmbience:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->sunColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->shadowFillColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->specularColor:Lcom/faforever/neroxis/util/vector/Vector4;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->bloom:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->fogColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->fogStart:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->fogEnd:F").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, LightingSettings.class, Object.class), LightingSettings.class, "lightingMultiplier;sunDirection;sunAmbience;sunColor;shadowFillColor;specularColor;bloom;fogColor;fogStart;fogEnd", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->lightingMultiplier:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->sunDirection:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->sunAmbience:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->sunColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->shadowFillColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->specularColor:Lcom/faforever/neroxis/util/vector/Vector4;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->bloom:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->fogColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->fogStart:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;->fogEnd:F").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public float lightingMultiplier() {
        return this.lightingMultiplier;
    }

    public Vector3 sunDirection() {
        return this.sunDirection;
    }

    public Vector3 sunAmbience() {
        return this.sunAmbience;
    }

    public Vector3 sunColor() {
        return this.sunColor;
    }

    public Vector3 shadowFillColor() {
        return this.shadowFillColor;
    }

    public Vector4 specularColor() {
        return this.specularColor;
    }

    public float bloom() {
        return this.bloom;
    }

    public Vector3 fogColor() {
        return this.fogColor;
    }

    public float fogStart() {
        return this.fogStart;
    }

    public float fogEnd() {
        return this.fogEnd;
    }

    public LightingSettings(float lightingMultiplier, Vector3 sunDirection, Vector3 sunAmbience, Vector3 sunColor, Vector3 shadowFillColor, Vector4 specularColor, float bloom, Vector3 fogColor, float fogStart, float fogEnd) {
        Objects.requireNonNull(sunDirection);
        Objects.requireNonNull(sunAmbience);
        Objects.requireNonNull(sunColor);
        Objects.requireNonNull(shadowFillColor);
        Objects.requireNonNull(specularColor);
        Objects.requireNonNull(fogColor);
        this.lightingMultiplier = lightingMultiplier;
        this.sunDirection = sunDirection;
        this.sunAmbience = sunAmbience;
        this.sunColor = sunColor;
        this.shadowFillColor = shadowFillColor;
        this.specularColor = specularColor;
        this.bloom = bloom;
        this.fogColor = fogColor;
        this.fogStart = fogStart;
        this.fogEnd = fogEnd;
    }
}

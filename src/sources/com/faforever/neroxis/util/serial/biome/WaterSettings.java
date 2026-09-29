package com.faforever.neroxis.util.serial.biome;

import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/serial/biome/WaterSettings.class */
public final class WaterSettings extends Record {
    private final boolean waterPresent;
    private final float elevation;
    private final float elevationDeep;
    private final float elevationAbyss;
    private final Vector3 surfaceColor;
    private final Vector2 colorLerp;
    private final float refractionScale;
    private final float fresnelBias;
    private final float fresnelPower;
    private final float unitReflection;
    private final float skyReflection;
    private final float sunShininess;
    private final float sunStrength;
    private final Vector3 sunDirection;
    private final Vector3 sunColor;
    private final float sunReflection;
    private final float sunGlow;
    private final String texPathCubemap;
    private final String texPathWaterRamp;
    private final List<WaveTexture> waveTextures;

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, WaterSettings.class), WaterSettings.class, "waterPresent;elevation;elevationDeep;elevationAbyss;surfaceColor;colorLerp;refractionScale;fresnelBias;fresnelPower;unitReflection;skyReflection;sunShininess;sunStrength;sunDirection;sunColor;sunReflection;sunGlow;texPathCubemap;texPathWaterRamp;waveTextures", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->waterPresent:Z", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->elevation:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->elevationDeep:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->elevationAbyss:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->surfaceColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->colorLerp:Lcom/faforever/neroxis/util/vector/Vector2;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->refractionScale:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->fresnelBias:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->fresnelPower:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->unitReflection:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->skyReflection:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunShininess:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunStrength:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunDirection:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunReflection:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunGlow:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->texPathCubemap:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->texPathWaterRamp:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->waveTextures:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, WaterSettings.class), WaterSettings.class, "waterPresent;elevation;elevationDeep;elevationAbyss;surfaceColor;colorLerp;refractionScale;fresnelBias;fresnelPower;unitReflection;skyReflection;sunShininess;sunStrength;sunDirection;sunColor;sunReflection;sunGlow;texPathCubemap;texPathWaterRamp;waveTextures", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->waterPresent:Z", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->elevation:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->elevationDeep:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->elevationAbyss:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->surfaceColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->colorLerp:Lcom/faforever/neroxis/util/vector/Vector2;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->refractionScale:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->fresnelBias:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->fresnelPower:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->unitReflection:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->skyReflection:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunShininess:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunStrength:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunDirection:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunReflection:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunGlow:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->texPathCubemap:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->texPathWaterRamp:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->waveTextures:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, WaterSettings.class, Object.class), WaterSettings.class, "waterPresent;elevation;elevationDeep;elevationAbyss;surfaceColor;colorLerp;refractionScale;fresnelBias;fresnelPower;unitReflection;skyReflection;sunShininess;sunStrength;sunDirection;sunColor;sunReflection;sunGlow;texPathCubemap;texPathWaterRamp;waveTextures", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->waterPresent:Z", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->elevation:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->elevationDeep:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->elevationAbyss:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->surfaceColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->colorLerp:Lcom/faforever/neroxis/util/vector/Vector2;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->refractionScale:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->fresnelBias:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->fresnelPower:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->unitReflection:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->skyReflection:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunShininess:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunStrength:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunDirection:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunColor:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunReflection:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->sunGlow:F", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->texPathCubemap:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->texPathWaterRamp:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;->waveTextures:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public boolean waterPresent() {
        return this.waterPresent;
    }

    public float elevation() {
        return this.elevation;
    }

    public float elevationDeep() {
        return this.elevationDeep;
    }

    public float elevationAbyss() {
        return this.elevationAbyss;
    }

    public Vector3 surfaceColor() {
        return this.surfaceColor;
    }

    public Vector2 colorLerp() {
        return this.colorLerp;
    }

    public float refractionScale() {
        return this.refractionScale;
    }

    public float fresnelBias() {
        return this.fresnelBias;
    }

    public float fresnelPower() {
        return this.fresnelPower;
    }

    public float unitReflection() {
        return this.unitReflection;
    }

    public float skyReflection() {
        return this.skyReflection;
    }

    public float sunShininess() {
        return this.sunShininess;
    }

    public float sunStrength() {
        return this.sunStrength;
    }

    public Vector3 sunDirection() {
        return this.sunDirection;
    }

    public Vector3 sunColor() {
        return this.sunColor;
    }

    public float sunReflection() {
        return this.sunReflection;
    }

    public float sunGlow() {
        return this.sunGlow;
    }

    public String texPathCubemap() {
        return this.texPathCubemap;
    }

    public String texPathWaterRamp() {
        return this.texPathWaterRamp;
    }

    public List<WaveTexture> waveTextures() {
        return this.waveTextures;
    }

    public WaterSettings(boolean waterPresent, float elevation, float elevationDeep, float elevationAbyss, Vector3 surfaceColor, Vector2 colorLerp, float refractionScale, float fresnelBias, float fresnelPower, float unitReflection, float skyReflection, float sunShininess, float sunStrength, Vector3 sunDirection, Vector3 sunColor, float sunReflection, float sunGlow, String texPathCubemap, String texPathWaterRamp, List<WaveTexture> waveTextures) {
        Objects.requireNonNull(surfaceColor);
        Objects.requireNonNull(colorLerp);
        Objects.requireNonNull(sunDirection);
        Objects.requireNonNull(sunColor);
        Objects.requireNonNull(texPathCubemap);
        Objects.requireNonNull(texPathWaterRamp);
        List<WaveTexture> waveTextures2 = List.copyOf(waveTextures);
        if (waveTextures2.size() == 4) {
            this.waterPresent = waterPresent;
            this.elevation = elevation;
            this.elevationDeep = elevationDeep;
            this.elevationAbyss = elevationAbyss;
            this.surfaceColor = surfaceColor;
            this.colorLerp = colorLerp;
            this.refractionScale = refractionScale;
            this.fresnelBias = fresnelBias;
            this.fresnelPower = fresnelPower;
            this.unitReflection = unitReflection;
            this.skyReflection = skyReflection;
            this.sunShininess = sunShininess;
            this.sunStrength = sunStrength;
            this.sunDirection = sunDirection;
            this.sunColor = sunColor;
            this.sunReflection = sunReflection;
            this.sunGlow = sunGlow;
            this.texPathCubemap = texPathCubemap;
            this.texPathWaterRamp = texPathWaterRamp;
            this.waveTextures = waveTextures2;
            return;
        }
        throw new IllegalArgumentException("Number of wave textures must be 4");
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/serial/biome/WaterSettings$WaveTexture.class */
    public static final class WaveTexture extends Record {
        private final Vector2 normalMovement;
        private final String texPath;
        private final float normalRepeat;

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, WaveTexture.class), WaveTexture.class, "normalMovement;texPath;normalRepeat", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings$WaveTexture;->normalMovement:Lcom/faforever/neroxis/util/vector/Vector2;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings$WaveTexture;->texPath:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings$WaveTexture;->normalRepeat:F").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, WaveTexture.class), WaveTexture.class, "normalMovement;texPath;normalRepeat", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings$WaveTexture;->normalMovement:Lcom/faforever/neroxis/util/vector/Vector2;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings$WaveTexture;->texPath:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings$WaveTexture;->normalRepeat:F").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, WaveTexture.class, Object.class), WaveTexture.class, "normalMovement;texPath;normalRepeat", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings$WaveTexture;->normalMovement:Lcom/faforever/neroxis/util/vector/Vector2;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings$WaveTexture;->texPath:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/WaterSettings$WaveTexture;->normalRepeat:F").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Vector2 normalMovement() {
            return this.normalMovement;
        }

        public String texPath() {
            return this.texPath;
        }

        public float normalRepeat() {
            return this.normalRepeat;
        }

        public WaveTexture(Vector2 normalMovement, String texPath, float normalRepeat) {
            Objects.requireNonNull(normalMovement);
            Objects.requireNonNull(texPath);
            this.normalMovement = normalMovement;
            this.texPath = texPath;
            this.normalRepeat = normalRepeat;
        }
    }
}

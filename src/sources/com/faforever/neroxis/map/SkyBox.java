package com.faforever.neroxis.map;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import com.faforever.neroxis.util.vector.Vector4;
import java.awt.Color;
import java.util.Arrays;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/SkyBox.class */
public class SkyBox {
    private float horizonHeight = -42.5f;
    private float scale = 1171.5814f;
    private float subHeight = 1.256637f;
    private int subDivAx = 16;
    private int subDivHeight = 6;
    private float zenithHeight = 293.50708f;
    private Vector3 horizonColor = new Vector3(0.6485937f, 0.8204687f, 0.84f);
    private Vector3 zenithColor = new Vector3(0.21999997f, 0.40999997f, 0.71999997f);
    private Vector3 position = new Vector3(256.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 256.0f);
    private float decalGlowMultiplier = 0.1f;
    private String albedo = "/textures/environment/Decal_test_Albedo001.dds";
    private String glow = "/textures/environment/Decal_test_Glow001.dds";
    private Planet[] Planets = {new Planet()};
    private Color midRgbColor = new Color(0);
    private float cirrusMultiplier = 1.8f;
    private Vector3 cirrusColor = new Vector3(1.16f, 1.16f, 1.23f);
    private String cirrusTexture = "/textures/environment/cirrus000.dds";
    private Cirrus[] cirrusLayers = {new Cirrus()};
    private float clouds7 = SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;

    @Generated
    public SkyBox() {
    }

    @Generated
    public void setHorizonHeight(final float horizonHeight) {
        this.horizonHeight = horizonHeight;
    }

    @Generated
    public void setScale(final float scale) {
        this.scale = scale;
    }

    @Generated
    public void setSubHeight(final float subHeight) {
        this.subHeight = subHeight;
    }

    @Generated
    public void setSubDivAx(final int subDivAx) {
        this.subDivAx = subDivAx;
    }

    @Generated
    public void setSubDivHeight(final int subDivHeight) {
        this.subDivHeight = subDivHeight;
    }

    @Generated
    public void setZenithHeight(final float zenithHeight) {
        this.zenithHeight = zenithHeight;
    }

    @Generated
    public void setHorizonColor(final Vector3 horizonColor) {
        this.horizonColor = horizonColor;
    }

    @Generated
    public void setZenithColor(final Vector3 zenithColor) {
        this.zenithColor = zenithColor;
    }

    @Generated
    public void setPosition(final Vector3 position) {
        this.position = position;
    }

    @Generated
    public void setDecalGlowMultiplier(final float decalGlowMultiplier) {
        this.decalGlowMultiplier = decalGlowMultiplier;
    }

    @Generated
    public void setAlbedo(final String albedo) {
        this.albedo = albedo;
    }

    @Generated
    public void setGlow(final String glow) {
        this.glow = glow;
    }

    @Generated
    public void setPlanets(final Planet[] Planets) {
        this.Planets = Planets;
    }

    @Generated
    public void setMidRgbColor(final Color midRgbColor) {
        this.midRgbColor = midRgbColor;
    }

    @Generated
    public void setCirrusMultiplier(final float cirrusMultiplier) {
        this.cirrusMultiplier = cirrusMultiplier;
    }

    @Generated
    public void setCirrusColor(final Vector3 cirrusColor) {
        this.cirrusColor = cirrusColor;
    }

    @Generated
    public void setCirrusTexture(final String cirrusTexture) {
        this.cirrusTexture = cirrusTexture;
    }

    @Generated
    public void setCirrusLayers(final Cirrus[] cirrusLayers) {
        this.cirrusLayers = cirrusLayers;
    }

    @Generated
    public void setClouds7(final float clouds7) {
        this.clouds7 = clouds7;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SkyBox)) {
            return false;
        }
        SkyBox other = (SkyBox) o;
        if (!other.canEqual(this) || Float.compare(getHorizonHeight(), other.getHorizonHeight()) != 0 || Float.compare(getScale(), other.getScale()) != 0 || Float.compare(getSubHeight(), other.getSubHeight()) != 0 || getSubDivAx() != other.getSubDivAx() || getSubDivHeight() != other.getSubDivHeight() || Float.compare(getZenithHeight(), other.getZenithHeight()) != 0 || Float.compare(getDecalGlowMultiplier(), other.getDecalGlowMultiplier()) != 0 || Float.compare(getCirrusMultiplier(), other.getCirrusMultiplier()) != 0 || Float.compare(getClouds7(), other.getClouds7()) != 0) {
            return false;
        }
        Object this$horizonColor = getHorizonColor();
        Object other$horizonColor = other.getHorizonColor();
        if (this$horizonColor == null) {
            if (other$horizonColor != null) {
                return false;
            }
        } else if (!this$horizonColor.equals(other$horizonColor)) {
            return false;
        }
        Object this$zenithColor = getZenithColor();
        Object other$zenithColor = other.getZenithColor();
        if (this$zenithColor == null) {
            if (other$zenithColor != null) {
                return false;
            }
        } else if (!this$zenithColor.equals(other$zenithColor)) {
            return false;
        }
        Object this$position = getPosition();
        Object other$position = other.getPosition();
        if (this$position == null) {
            if (other$position != null) {
                return false;
            }
        } else if (!this$position.equals(other$position)) {
            return false;
        }
        Object this$albedo = getAlbedo();
        Object other$albedo = other.getAlbedo();
        if (this$albedo == null) {
            if (other$albedo != null) {
                return false;
            }
        } else if (!this$albedo.equals(other$albedo)) {
            return false;
        }
        Object this$glow = getGlow();
        Object other$glow = other.getGlow();
        if (this$glow == null) {
            if (other$glow != null) {
                return false;
            }
        } else if (!this$glow.equals(other$glow)) {
            return false;
        }
        if (!Arrays.deepEquals(getPlanets(), other.getPlanets())) {
            return false;
        }
        Object this$midRgbColor = getMidRgbColor();
        Object other$midRgbColor = other.getMidRgbColor();
        if (this$midRgbColor == null) {
            if (other$midRgbColor != null) {
                return false;
            }
        } else if (!this$midRgbColor.equals(other$midRgbColor)) {
            return false;
        }
        Object this$cirrusColor = getCirrusColor();
        Object other$cirrusColor = other.getCirrusColor();
        if (this$cirrusColor == null) {
            if (other$cirrusColor != null) {
                return false;
            }
        } else if (!this$cirrusColor.equals(other$cirrusColor)) {
            return false;
        }
        Object this$cirrusTexture = getCirrusTexture();
        Object other$cirrusTexture = other.getCirrusTexture();
        if (this$cirrusTexture == null) {
            if (other$cirrusTexture != null) {
                return false;
            }
        } else if (!this$cirrusTexture.equals(other$cirrusTexture)) {
            return false;
        }
        return Arrays.deepEquals(getCirrusLayers(), other.getCirrusLayers());
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof SkyBox;
    }

    @Generated
    public int hashCode() {
        int result = (1 * 59) + Float.floatToIntBits(getHorizonHeight());
        int result2 = (((((((((((((((result * 59) + Float.floatToIntBits(getScale())) * 59) + Float.floatToIntBits(getSubHeight())) * 59) + getSubDivAx()) * 59) + getSubDivHeight()) * 59) + Float.floatToIntBits(getZenithHeight())) * 59) + Float.floatToIntBits(getDecalGlowMultiplier())) * 59) + Float.floatToIntBits(getCirrusMultiplier())) * 59) + Float.floatToIntBits(getClouds7());
        Object $horizonColor = getHorizonColor();
        int result3 = (result2 * 59) + ($horizonColor == null ? 43 : $horizonColor.hashCode());
        Object $zenithColor = getZenithColor();
        int result4 = (result3 * 59) + ($zenithColor == null ? 43 : $zenithColor.hashCode());
        Object $position = getPosition();
        int result5 = (result4 * 59) + ($position == null ? 43 : $position.hashCode());
        Object $albedo = getAlbedo();
        int result6 = (result5 * 59) + ($albedo == null ? 43 : $albedo.hashCode());
        Object $glow = getGlow();
        int result7 = (((result6 * 59) + ($glow == null ? 43 : $glow.hashCode())) * 59) + Arrays.deepHashCode(getPlanets());
        Object $midRgbColor = getMidRgbColor();
        int result8 = (result7 * 59) + ($midRgbColor == null ? 43 : $midRgbColor.hashCode());
        Object $cirrusColor = getCirrusColor();
        int result9 = (result8 * 59) + ($cirrusColor == null ? 43 : $cirrusColor.hashCode());
        Object $cirrusTexture = getCirrusTexture();
        return (((result9 * 59) + ($cirrusTexture == null ? 43 : $cirrusTexture.hashCode())) * 59) + Arrays.deepHashCode(getCirrusLayers());
    }

    @Generated
    public String toString() {
        return "SkyBox(horizonHeight=" + getHorizonHeight() + ", scale=" + getScale() + ", subHeight=" + getSubHeight() + ", subDivAx=" + getSubDivAx() + ", subDivHeight=" + getSubDivHeight() + ", zenithHeight=" + getZenithHeight() + ", horizonColor=" + String.valueOf(getHorizonColor()) + ", zenithColor=" + String.valueOf(getZenithColor()) + ", position=" + String.valueOf(getPosition()) + ", decalGlowMultiplier=" + getDecalGlowMultiplier() + ", albedo=" + getAlbedo() + ", glow=" + getGlow() + ", Planets=" + Arrays.deepToString(getPlanets()) + ", midRgbColor=" + String.valueOf(getMidRgbColor()) + ", cirrusMultiplier=" + getCirrusMultiplier() + ", cirrusColor=" + String.valueOf(getCirrusColor()) + ", cirrusTexture=" + getCirrusTexture() + ", cirrusLayers=" + Arrays.deepToString(getCirrusLayers()) + ", clouds7=" + getClouds7() + ")";
    }

    @Generated
    public float getHorizonHeight() {
        return this.horizonHeight;
    }

    @Generated
    public float getScale() {
        return this.scale;
    }

    @Generated
    public float getSubHeight() {
        return this.subHeight;
    }

    @Generated
    public int getSubDivAx() {
        return this.subDivAx;
    }

    @Generated
    public int getSubDivHeight() {
        return this.subDivHeight;
    }

    @Generated
    public float getZenithHeight() {
        return this.zenithHeight;
    }

    @Generated
    public Vector3 getHorizonColor() {
        return this.horizonColor;
    }

    @Generated
    public Vector3 getZenithColor() {
        return this.zenithColor;
    }

    @Generated
    public Vector3 getPosition() {
        return this.position;
    }

    @Generated
    public float getDecalGlowMultiplier() {
        return this.decalGlowMultiplier;
    }

    @Generated
    public String getAlbedo() {
        return this.albedo;
    }

    @Generated
    public String getGlow() {
        return this.glow;
    }

    @Generated
    public Planet[] getPlanets() {
        return this.Planets;
    }

    @Generated
    public Color getMidRgbColor() {
        return this.midRgbColor;
    }

    @Generated
    public float getCirrusMultiplier() {
        return this.cirrusMultiplier;
    }

    @Generated
    public Vector3 getCirrusColor() {
        return this.cirrusColor;
    }

    @Generated
    public String getCirrusTexture() {
        return this.cirrusTexture;
    }

    @Generated
    public Cirrus[] getCirrusLayers() {
        return this.cirrusLayers;
    }

    @Generated
    public float getClouds7() {
        return this.clouds7;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/SkyBox$Planet.class */
    public static class Planet {
        private Vector3 position = new Vector3(2190.5806f, 570.7427f, -1020.0027f);
        private float rotation = -1.5847589f;
        private Vector2 scale = new Vector2(183.0f, 183.0f);
        private Vector4 uv = new Vector4(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 0.5f, 0.5f, 0.5f);

        @Generated
        public Planet() {
        }

        @Generated
        public void setPosition(final Vector3 position) {
            this.position = position;
        }

        @Generated
        public void setRotation(final float rotation) {
            this.rotation = rotation;
        }

        @Generated
        public void setScale(final Vector2 scale) {
            this.scale = scale;
        }

        @Generated
        public void setUv(final Vector4 uv) {
            this.uv = uv;
        }

        @Generated
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Planet)) {
                return false;
            }
            Planet other = (Planet) o;
            if (!other.canEqual(this) || Float.compare(getRotation(), other.getRotation()) != 0) {
                return false;
            }
            Object this$position = getPosition();
            Object other$position = other.getPosition();
            if (this$position == null) {
                if (other$position != null) {
                    return false;
                }
            } else if (!this$position.equals(other$position)) {
                return false;
            }
            Object this$scale = getScale();
            Object other$scale = other.getScale();
            if (this$scale == null) {
                if (other$scale != null) {
                    return false;
                }
            } else if (!this$scale.equals(other$scale)) {
                return false;
            }
            Object this$uv = getUv();
            Object other$uv = other.getUv();
            if (this$uv == null) {
                return other$uv == null;
            }
            return this$uv.equals(other$uv);
        }

        @Generated
        protected boolean canEqual(final Object other) {
            return other instanceof Planet;
        }

        @Generated
        public int hashCode() {
            int result = (1 * 59) + Float.floatToIntBits(getRotation());
            Object $position = getPosition();
            int result2 = (result * 59) + ($position == null ? 43 : $position.hashCode());
            Object $scale = getScale();
            int result3 = (result2 * 59) + ($scale == null ? 43 : $scale.hashCode());
            Object $uv = getUv();
            return (result3 * 59) + ($uv == null ? 43 : $uv.hashCode());
        }

        @Generated
        public String toString() {
            return "SkyBox.Planet(position=" + String.valueOf(getPosition()) + ", rotation=" + getRotation() + ", scale=" + String.valueOf(getScale()) + ", uv=" + String.valueOf(getUv()) + ")";
        }

        @Generated
        public Vector3 getPosition() {
            return this.position;
        }

        @Generated
        public float getRotation() {
            return this.rotation;
        }

        @Generated
        public Vector2 getScale() {
            return this.scale;
        }

        @Generated
        public Vector4 getUv() {
            return this.uv;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/SkyBox$Cirrus.class */
    public static class Cirrus {
        private Vector2 frequency = new Vector2(1.0E-4f, 1.0E-4f);
        private float speed = 7.7999997f;
        private Vector2 direction = new Vector2(0.4320857f, -0.9018325f);

        @Generated
        public Cirrus() {
        }

        @Generated
        public void setFrequency(final Vector2 frequency) {
            this.frequency = frequency;
        }

        @Generated
        public void setSpeed(final float speed) {
            this.speed = speed;
        }

        @Generated
        public void setDirection(final Vector2 direction) {
            this.direction = direction;
        }

        @Generated
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Cirrus)) {
                return false;
            }
            Cirrus other = (Cirrus) o;
            if (!other.canEqual(this) || Float.compare(getSpeed(), other.getSpeed()) != 0) {
                return false;
            }
            Object this$frequency = getFrequency();
            Object other$frequency = other.getFrequency();
            if (this$frequency == null) {
                if (other$frequency != null) {
                    return false;
                }
            } else if (!this$frequency.equals(other$frequency)) {
                return false;
            }
            Object this$direction = getDirection();
            Object other$direction = other.getDirection();
            if (this$direction == null) {
                return other$direction == null;
            }
            return this$direction.equals(other$direction);
        }

        @Generated
        protected boolean canEqual(final Object other) {
            return other instanceof Cirrus;
        }

        @Generated
        public int hashCode() {
            int result = (1 * 59) + Float.floatToIntBits(getSpeed());
            Object $frequency = getFrequency();
            int result2 = (result * 59) + ($frequency == null ? 43 : $frequency.hashCode());
            Object $direction = getDirection();
            return (result2 * 59) + ($direction == null ? 43 : $direction.hashCode());
        }

        @Generated
        public String toString() {
            return "SkyBox.Cirrus(frequency=" + String.valueOf(getFrequency()) + ", speed=" + getSpeed() + ", direction=" + String.valueOf(getDirection()) + ")";
        }

        @Generated
        public Vector2 getFrequency() {
            return this.frequency;
        }

        @Generated
        public float getSpeed() {
            return this.speed;
        }

        @Generated
        public Vector2 getDirection() {
            return this.direction;
        }
    }
}

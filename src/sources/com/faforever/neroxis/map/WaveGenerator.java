package com.faforever.neroxis.map;

import com.faforever.neroxis.util.vector.Vector3;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/WaveGenerator.class */
public final class WaveGenerator extends PositionedObject {
    private String textureName;
    private String rampName;
    private float rotation;
    private Vector3 velocity;
    private float lifeTimeFirst;
    private float lifeTimeSecond;
    private float periodFirst;
    private float periodSecond;
    private float scaleFirst;
    private float scaleSecond;
    private float frameCount;
    private float frameRateFirst;
    private float frameRateSecond;
    private float stripCount;

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WaveGenerator)) {
            return false;
        }
        WaveGenerator other = (WaveGenerator) o;
        if (!other.canEqual(this) || !super.equals(o) || Float.compare(getRotation(), other.getRotation()) != 0 || Float.compare(getLifeTimeFirst(), other.getLifeTimeFirst()) != 0 || Float.compare(getLifeTimeSecond(), other.getLifeTimeSecond()) != 0 || Float.compare(getPeriodFirst(), other.getPeriodFirst()) != 0 || Float.compare(getPeriodSecond(), other.getPeriodSecond()) != 0 || Float.compare(getScaleFirst(), other.getScaleFirst()) != 0 || Float.compare(getScaleSecond(), other.getScaleSecond()) != 0 || Float.compare(getFrameCount(), other.getFrameCount()) != 0 || Float.compare(getFrameRateFirst(), other.getFrameRateFirst()) != 0 || Float.compare(getFrameRateSecond(), other.getFrameRateSecond()) != 0 || Float.compare(getStripCount(), other.getStripCount()) != 0) {
            return false;
        }
        Object this$textureName = getTextureName();
        Object other$textureName = other.getTextureName();
        if (this$textureName == null) {
            if (other$textureName != null) {
                return false;
            }
        } else if (!this$textureName.equals(other$textureName)) {
            return false;
        }
        Object this$rampName = getRampName();
        Object other$rampName = other.getRampName();
        if (this$rampName == null) {
            if (other$rampName != null) {
                return false;
            }
        } else if (!this$rampName.equals(other$rampName)) {
            return false;
        }
        Object this$velocity = getVelocity();
        Object other$velocity = other.getVelocity();
        if (this$velocity == null) {
            return other$velocity == null;
        }
        return this$velocity.equals(other$velocity);
    }

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof WaveGenerator;
    }

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    public int hashCode() {
        int result = super.hashCode();
        int result2 = (((((((((((((((((((((result * 59) + Float.floatToIntBits(getRotation())) * 59) + Float.floatToIntBits(getLifeTimeFirst())) * 59) + Float.floatToIntBits(getLifeTimeSecond())) * 59) + Float.floatToIntBits(getPeriodFirst())) * 59) + Float.floatToIntBits(getPeriodSecond())) * 59) + Float.floatToIntBits(getScaleFirst())) * 59) + Float.floatToIntBits(getScaleSecond())) * 59) + Float.floatToIntBits(getFrameCount())) * 59) + Float.floatToIntBits(getFrameRateFirst())) * 59) + Float.floatToIntBits(getFrameRateSecond())) * 59) + Float.floatToIntBits(getStripCount());
        Object $textureName = getTextureName();
        int result3 = (result2 * 59) + ($textureName == null ? 43 : $textureName.hashCode());
        Object $rampName = getRampName();
        int result4 = (result3 * 59) + ($rampName == null ? 43 : $rampName.hashCode());
        Object $velocity = getVelocity();
        return (result4 * 59) + ($velocity == null ? 43 : $velocity.hashCode());
    }

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    public String toString() {
        return "WaveGenerator(super=" + super.toString() + ", textureName=" + getTextureName() + ", rampName=" + getRampName() + ", rotation=" + getRotation() + ", velocity=" + String.valueOf(getVelocity()) + ", lifeTimeFirst=" + getLifeTimeFirst() + ", lifeTimeSecond=" + getLifeTimeSecond() + ", periodFirst=" + getPeriodFirst() + ", periodSecond=" + getPeriodSecond() + ", scaleFirst=" + getScaleFirst() + ", scaleSecond=" + getScaleSecond() + ", frameCount=" + getFrameCount() + ", frameRateFirst=" + getFrameRateFirst() + ", frameRateSecond=" + getFrameRateSecond() + ", stripCount=" + getStripCount() + ")";
    }

    @Generated
    public void setTextureName(final String textureName) {
        this.textureName = textureName;
    }

    @Generated
    public void setRampName(final String rampName) {
        this.rampName = rampName;
    }

    @Generated
    public void setRotation(final float rotation) {
        this.rotation = rotation;
    }

    @Generated
    public void setVelocity(final Vector3 velocity) {
        this.velocity = velocity;
    }

    @Generated
    public void setLifeTimeFirst(final float lifeTimeFirst) {
        this.lifeTimeFirst = lifeTimeFirst;
    }

    @Generated
    public void setLifeTimeSecond(final float lifeTimeSecond) {
        this.lifeTimeSecond = lifeTimeSecond;
    }

    @Generated
    public void setPeriodFirst(final float periodFirst) {
        this.periodFirst = periodFirst;
    }

    @Generated
    public void setPeriodSecond(final float periodSecond) {
        this.periodSecond = periodSecond;
    }

    @Generated
    public void setScaleFirst(final float scaleFirst) {
        this.scaleFirst = scaleFirst;
    }

    @Generated
    public void setScaleSecond(final float scaleSecond) {
        this.scaleSecond = scaleSecond;
    }

    @Generated
    public void setFrameCount(final float frameCount) {
        this.frameCount = frameCount;
    }

    @Generated
    public void setFrameRateFirst(final float frameRateFirst) {
        this.frameRateFirst = frameRateFirst;
    }

    @Generated
    public void setFrameRateSecond(final float frameRateSecond) {
        this.frameRateSecond = frameRateSecond;
    }

    @Generated
    public void setStripCount(final float stripCount) {
        this.stripCount = stripCount;
    }

    @Generated
    public String getTextureName() {
        return this.textureName;
    }

    @Generated
    public String getRampName() {
        return this.rampName;
    }

    @Generated
    public float getRotation() {
        return this.rotation;
    }

    @Generated
    public Vector3 getVelocity() {
        return this.velocity;
    }

    @Generated
    public float getLifeTimeFirst() {
        return this.lifeTimeFirst;
    }

    @Generated
    public float getLifeTimeSecond() {
        return this.lifeTimeSecond;
    }

    @Generated
    public float getPeriodFirst() {
        return this.periodFirst;
    }

    @Generated
    public float getPeriodSecond() {
        return this.periodSecond;
    }

    @Generated
    public float getScaleFirst() {
        return this.scaleFirst;
    }

    @Generated
    public float getScaleSecond() {
        return this.scaleSecond;
    }

    @Generated
    public float getFrameCount() {
        return this.frameCount;
    }

    @Generated
    public float getFrameRateFirst() {
        return this.frameRateFirst;
    }

    @Generated
    public float getFrameRateSecond() {
        return this.frameRateSecond;
    }

    @Generated
    public float getStripCount() {
        return this.stripCount;
    }

    public WaveGenerator(String textureName, String rampName, Vector3 position, float rotation, Vector3 velocity) {
        super(position);
        this.textureName = textureName;
        this.rampName = rampName;
        this.rotation = rotation;
        this.velocity = velocity;
    }
}

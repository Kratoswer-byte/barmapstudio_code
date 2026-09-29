package com.faforever.neroxis.map;

import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import java.util.Locale;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/Decal.class */
public final class Decal extends PositionedObject {
    private String path;
    private Vector3 rotation;
    private Vector3 scale;
    private DecalType type;
    private float cutOffLOD;

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Decal)) {
            return false;
        }
        Decal other = (Decal) o;
        if (!other.canEqual(this) || !super.equals(o) || Float.compare(getCutOffLOD(), other.getCutOffLOD()) != 0) {
            return false;
        }
        Object this$path = getPath();
        Object other$path = other.getPath();
        if (this$path == null) {
            if (other$path != null) {
                return false;
            }
        } else if (!this$path.equals(other$path)) {
            return false;
        }
        Object this$rotation = getRotation();
        Object other$rotation = other.getRotation();
        if (this$rotation == null) {
            if (other$rotation != null) {
                return false;
            }
        } else if (!this$rotation.equals(other$rotation)) {
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
        Object this$type = getType();
        Object other$type = other.getType();
        if (this$type == null) {
            return other$type == null;
        }
        return this$type.equals(other$type);
    }

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof Decal;
    }

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    public int hashCode() {
        int result = super.hashCode();
        int result2 = (result * 59) + Float.floatToIntBits(getCutOffLOD());
        Object $path = getPath();
        int result3 = (result2 * 59) + ($path == null ? 43 : $path.hashCode());
        Object $rotation = getRotation();
        int result4 = (result3 * 59) + ($rotation == null ? 43 : $rotation.hashCode());
        Object $scale = getScale();
        int result5 = (result4 * 59) + ($scale == null ? 43 : $scale.hashCode());
        Object $type = getType();
        return (result5 * 59) + ($type == null ? 43 : $type.hashCode());
    }

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    public String toString() {
        return "Decal(super=" + super.toString() + ", path=" + getPath() + ", rotation=" + String.valueOf(getRotation()) + ", scale=" + String.valueOf(getScale()) + ", type=" + String.valueOf(getType()) + ", cutOffLOD=" + getCutOffLOD() + ")";
    }

    @Generated
    public void setPath(final String path) {
        this.path = path;
    }

    @Generated
    public void setRotation(final Vector3 rotation) {
        this.rotation = rotation;
    }

    @Generated
    public void setScale(final Vector3 scale) {
        this.scale = scale;
    }

    @Generated
    public void setType(final DecalType type) {
        this.type = type;
    }

    @Generated
    public void setCutOffLOD(final float cutOffLOD) {
        this.cutOffLOD = cutOffLOD;
    }

    @Generated
    public String getPath() {
        return this.path;
    }

    @Generated
    public Vector3 getRotation() {
        return this.rotation;
    }

    @Generated
    public Vector3 getScale() {
        return this.scale;
    }

    @Generated
    public DecalType getType() {
        return this.type;
    }

    @Generated
    public float getCutOffLOD() {
        return this.cutOffLOD;
    }

    public Decal(String path, Vector2 position, Vector3 rotation, float scale, float cutOffLOD) {
        this(path, new Vector3(position), rotation, scale, cutOffLOD);
    }

    public Decal(String path, Vector3 position, Vector3 rotation, float scale, float cutOffLOD) {
        this(path, position, rotation, new Vector3(scale, scale, scale), cutOffLOD);
    }

    public Decal(String path, Vector3 position, Vector3 rotation, Vector3 scale, float cutOffLOD) {
        super(position);
        this.path = path;
        if (path.toLowerCase(Locale.ROOT).contains("normal")) {
            this.type = DecalType.Known.NORMALS;
        } else {
            this.type = DecalType.Known.ALBEDO;
        }
        this.rotation = rotation;
        this.scale = scale;
        this.cutOffLOD = cutOffLOD;
    }

    public Decal(String path, Vector3 position, Vector3 rotation, Vector3 scale, float cutOffLOD, DecalType type) {
        super(position);
        this.path = path;
        this.type = type;
        this.rotation = rotation;
        this.scale = scale;
        this.cutOffLOD = cutOffLOD;
    }
}

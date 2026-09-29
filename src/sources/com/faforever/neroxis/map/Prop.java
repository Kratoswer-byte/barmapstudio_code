package com.faforever.neroxis.map;

import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/Prop.class */
public final class Prop extends PositionedObject {
    private String path;
    private float rotation;
    private boolean isBoulder;

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Prop)) {
            return false;
        }
        Prop other = (Prop) o;
        if (!other.canEqual(this) || !super.equals(o) || Float.compare(getRotation(), other.getRotation()) != 0 || isBoulder() != other.isBoulder()) {
            return false;
        }
        Object this$path = getPath();
        Object other$path = other.getPath();
        if (this$path == null) {
            return other$path == null;
        }
        return this$path.equals(other$path);
    }

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof Prop;
    }

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    public int hashCode() {
        int result = super.hashCode();
        int result2 = (((result * 59) + Float.floatToIntBits(getRotation())) * 59) + (isBoulder() ? 79 : 97);
        Object $path = getPath();
        return (result2 * 59) + ($path == null ? 43 : $path.hashCode());
    }

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    public String toString() {
        return "Prop(super=" + super.toString() + ", path=" + getPath() + ", rotation=" + getRotation() + ", isBoulder=" + isBoulder() + ")";
    }

    @Generated
    public void setPath(final String path) {
        this.path = path;
    }

    @Generated
    public void setRotation(final float rotation) {
        this.rotation = rotation;
    }

    @Generated
    public void setBoulder(final boolean isBoulder) {
        this.isBoulder = isBoulder;
    }

    @Generated
    public String getPath() {
        return this.path;
    }

    @Generated
    public float getRotation() {
        return this.rotation;
    }

    @Generated
    public boolean isBoulder() {
        return this.isBoulder;
    }

    public Prop(String path, Vector2 position, float rotation, boolean isBoulder) {
        this(path, new Vector3(position), rotation, isBoulder);
    }

    public Prop(String path, Vector3 position, float rotation, boolean isBoulder) {
        super(position);
        this.path = path;
        this.rotation = rotation;
        this.isBoulder = isBoulder;
    }
}

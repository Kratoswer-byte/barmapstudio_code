package com.faforever.neroxis.map;

import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/Unit.class */
public final class Unit extends Marker {
    private String type;
    private float rotation;

    @Override // com.faforever.neroxis.map.Marker, com.faforever.neroxis.map.PositionedObject
    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Unit)) {
            return false;
        }
        Unit other = (Unit) o;
        if (!other.canEqual(this) || !super.equals(o) || Float.compare(getRotation(), other.getRotation()) != 0) {
            return false;
        }
        Object this$type = getType();
        Object other$type = other.getType();
        if (this$type == null) {
            return other$type == null;
        }
        return this$type.equals(other$type);
    }

    @Override // com.faforever.neroxis.map.Marker, com.faforever.neroxis.map.PositionedObject
    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof Unit;
    }

    @Override // com.faforever.neroxis.map.Marker, com.faforever.neroxis.map.PositionedObject
    @Generated
    public int hashCode() {
        int result = super.hashCode();
        int result2 = (result * 59) + Float.floatToIntBits(getRotation());
        Object $type = getType();
        return (result2 * 59) + ($type == null ? 43 : $type.hashCode());
    }

    @Override // com.faforever.neroxis.map.Marker, com.faforever.neroxis.map.PositionedObject
    @Generated
    public String toString() {
        return "Unit(super=" + super.toString() + ", type=" + getType() + ", rotation=" + getRotation() + ")";
    }

    @Generated
    public void setType(final String type) {
        this.type = type;
    }

    @Generated
    public void setRotation(final float rotation) {
        this.rotation = rotation;
    }

    @Generated
    public String getType() {
        return this.type;
    }

    @Generated
    public float getRotation() {
        return this.rotation;
    }

    public Unit(String id, String type, Vector2 position, float rotation) {
        this(id, type, new Vector3(position), rotation);
    }

    public Unit(String id, String type, Vector3 position, float rotation) {
        super(id, position);
        this.type = type;
        this.rotation = rotation;
    }
}

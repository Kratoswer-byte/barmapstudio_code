package com.faforever.neroxis.map;

import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/PositionedObject.class */
public abstract class PositionedObject {
    protected Vector3 position;

    @Generated
    public void setPosition(final Vector3 position) {
        this.position = position;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PositionedObject)) {
            return false;
        }
        PositionedObject other = (PositionedObject) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Object this$position = getPosition();
        Object other$position = other.getPosition();
        if (this$position == null) {
            return other$position == null;
        }
        return this$position.equals(other$position);
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof PositionedObject;
    }

    @Generated
    public int hashCode() {
        Object $position = getPosition();
        int result = (1 * 59) + ($position == null ? 43 : $position.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "PositionedObject(position=" + String.valueOf(getPosition()) + ")";
    }

    @Generated
    public PositionedObject(final Vector3 position) {
        this.position = position;
    }

    @Generated
    public Vector3 getPosition() {
        return this.position;
    }

    protected PositionedObject(Vector2 position) {
        this.position = new Vector3(position);
    }
}

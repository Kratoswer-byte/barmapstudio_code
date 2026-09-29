package com.faforever.neroxis.map;

import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/Marker.class */
public class Marker extends PositionedObject {
    private String id;

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Marker)) {
            return false;
        }
        Marker other = (Marker) o;
        if (!other.canEqual(this) || !super.equals(o)) {
            return false;
        }
        Object this$id = getId();
        Object other$id = other.getId();
        if (this$id == null) {
            return other$id == null;
        }
        return this$id.equals(other$id);
    }

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof Marker;
    }

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    public int hashCode() {
        int result = super.hashCode();
        Object $id = getId();
        return (result * 59) + ($id == null ? 43 : $id.hashCode());
    }

    @Override // com.faforever.neroxis.map.PositionedObject
    @Generated
    public String toString() {
        return "Marker(super=" + super.toString() + ", id=" + getId() + ")";
    }

    @Generated
    public void setId(final String id) {
        this.id = id;
    }

    @Generated
    public String getId() {
        return this.id;
    }

    public Marker(String id, Vector3 position) {
        super(position);
        this.id = id;
    }

    public Marker(String id, Vector2 position) {
        super(position);
        this.id = id;
    }
}

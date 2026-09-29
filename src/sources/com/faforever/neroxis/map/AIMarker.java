package com.faforever.neroxis.map;

import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.SequencedSet;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/AIMarker.class */
public final class AIMarker extends Marker {
    private final SequencedSet<String> neighbors;

    @Override // com.faforever.neroxis.map.Marker, com.faforever.neroxis.map.PositionedObject
    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AIMarker)) {
            return false;
        }
        AIMarker other = (AIMarker) o;
        if (!other.canEqual(this) || !super.equals(o)) {
            return false;
        }
        Object this$neighbors = getNeighbors();
        Object other$neighbors = other.getNeighbors();
        if (this$neighbors == null) {
            return other$neighbors == null;
        }
        return this$neighbors.equals(other$neighbors);
    }

    @Override // com.faforever.neroxis.map.Marker, com.faforever.neroxis.map.PositionedObject
    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof AIMarker;
    }

    @Override // com.faforever.neroxis.map.Marker, com.faforever.neroxis.map.PositionedObject
    @Generated
    public int hashCode() {
        int result = super.hashCode();
        Object $neighbors = getNeighbors();
        return (result * 59) + ($neighbors == null ? 43 : $neighbors.hashCode());
    }

    @Override // com.faforever.neroxis.map.Marker, com.faforever.neroxis.map.PositionedObject
    @Generated
    public String toString() {
        return "AIMarker(super=" + super.toString() + ", neighbors=" + String.valueOf(getNeighbors()) + ")";
    }

    @Generated
    public SequencedSet<String> getNeighbors() {
        return this.neighbors;
    }

    public AIMarker(String id, Vector2 position) {
        this(id, new Vector3(position), (SequencedSet<String>) new LinkedHashSet());
    }

    public AIMarker(String id, Vector2 position, SequencedSet<String> neighbors) {
        this(id, new Vector3(position), neighbors);
    }

    public AIMarker(String id, Vector3 position, SequencedSet<String> neighbors) {
        super(id, position);
        this.neighbors = neighbors;
    }

    public int getNeighborCount() {
        return this.neighbors.size();
    }

    public void addNeighbor(String id) {
        this.neighbors.add(id);
    }

    public void addNeighbors(Collection<? extends String> ids) {
        this.neighbors.addAll(ids);
    }
}

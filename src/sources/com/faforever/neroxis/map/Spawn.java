package com.faforever.neroxis.map;

import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/Spawn.class */
public final class Spawn extends Marker {
    private Vector2 noRushOffset;
    private int teamID;

    @Override // com.faforever.neroxis.map.Marker, com.faforever.neroxis.map.PositionedObject
    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Spawn)) {
            return false;
        }
        Spawn other = (Spawn) o;
        if (!other.canEqual(this) || !super.equals(o) || getTeamID() != other.getTeamID()) {
            return false;
        }
        Object this$noRushOffset = getNoRushOffset();
        Object other$noRushOffset = other.getNoRushOffset();
        if (this$noRushOffset == null) {
            return other$noRushOffset == null;
        }
        return this$noRushOffset.equals(other$noRushOffset);
    }

    @Override // com.faforever.neroxis.map.Marker, com.faforever.neroxis.map.PositionedObject
    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof Spawn;
    }

    @Override // com.faforever.neroxis.map.Marker, com.faforever.neroxis.map.PositionedObject
    @Generated
    public int hashCode() {
        int result = super.hashCode();
        int result2 = (result * 59) + getTeamID();
        Object $noRushOffset = getNoRushOffset();
        return (result2 * 59) + ($noRushOffset == null ? 43 : $noRushOffset.hashCode());
    }

    @Override // com.faforever.neroxis.map.Marker, com.faforever.neroxis.map.PositionedObject
    @Generated
    public String toString() {
        return "Spawn(super=" + super.toString() + ", noRushOffset=" + String.valueOf(getNoRushOffset()) + ", teamID=" + getTeamID() + ")";
    }

    @Generated
    public void setNoRushOffset(final Vector2 noRushOffset) {
        this.noRushOffset = noRushOffset;
    }

    @Generated
    public void setTeamID(final int teamID) {
        this.teamID = teamID;
    }

    @Generated
    public Vector2 getNoRushOffset() {
        return this.noRushOffset;
    }

    @Generated
    public int getTeamID() {
        return this.teamID;
    }

    public Spawn(String id, Vector2 position, Vector2 noRushOffset, int teamID) {
        this(id, new Vector3(position), noRushOffset, teamID);
    }

    public Spawn(String id, Vector3 position, Vector2 noRushOffset, int teamID) {
        super(id, position);
        this.noRushOffset = noRushOffset;
        this.teamID = teamID;
    }
}

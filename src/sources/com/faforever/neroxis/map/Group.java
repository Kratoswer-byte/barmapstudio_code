package com.faforever.neroxis.map;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/Group.class */
public class Group {
    private final String id;
    private final List<Unit> units = new ArrayList();

    @Generated
    public Group(final String id) {
        this.id = id;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Group)) {
            return false;
        }
        Group other = (Group) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Object this$id = getId();
        Object other$id = other.getId();
        if (this$id == null) {
            return other$id == null;
        }
        return this$id.equals(other$id);
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof Group;
    }

    @Generated
    public int hashCode() {
        Object $id = getId();
        int result = (1 * 59) + ($id == null ? 43 : $id.hashCode());
        return result;
    }

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public List<Unit> getUnits() {
        return this.units;
    }

    public Unit getUnit(String id) {
        return this.units.stream().filter(unit -> {
            return unit.getId().equals(id);
        }).findFirst().orElse(null);
    }

    public Unit getUnit(int i) {
        return this.units.get(i);
    }

    public void addUnit(Unit unit) {
        this.units.add(unit);
    }

    public int getUnitCount() {
        return this.units.size();
    }
}

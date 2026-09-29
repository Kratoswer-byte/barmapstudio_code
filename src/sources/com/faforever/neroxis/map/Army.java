package com.faforever.neroxis.map;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/Army.class */
public class Army {
    private final String id;
    private final List<Group> groups = new ArrayList();

    @Generated
    public Army(final String id) {
        this.id = id;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Army)) {
            return false;
        }
        Army other = (Army) o;
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
        return other instanceof Army;
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
    public List<Group> getGroups() {
        return this.groups;
    }

    public Group getGroup(String id) {
        return this.groups.stream().filter(group -> {
            return group.getId().equals(id);
        }).findFirst().orElse(null);
    }

    public Group getGroup(int i) {
        return this.groups.get(i);
    }

    public void addGroup(Group group) {
        this.groups.add(group);
    }

    public int getGroupCount() {
        return this.groups.size();
    }

    public int getNumUnits() {
        return this.groups.stream().mapToInt((v0) -> {
            return v0.getUnitCount();
        }).sum();
    }
}

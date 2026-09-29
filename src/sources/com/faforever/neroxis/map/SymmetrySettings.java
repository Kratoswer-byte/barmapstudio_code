package com.faforever.neroxis.map;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/SymmetrySettings.class */
public final class SymmetrySettings extends Record {
    private final Symmetry terrainSymmetry;
    private final Symmetry teamSymmetry;
    private final Symmetry spawnSymmetry;

    public SymmetrySettings(Symmetry terrainSymmetry, Symmetry teamSymmetry, Symmetry spawnSymmetry) {
        this.terrainSymmetry = terrainSymmetry;
        this.teamSymmetry = teamSymmetry;
        this.spawnSymmetry = spawnSymmetry;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, SymmetrySettings.class), SymmetrySettings.class, "terrainSymmetry;teamSymmetry;spawnSymmetry", "FIELD:Lcom/faforever/neroxis/map/SymmetrySettings;->terrainSymmetry:Lcom/faforever/neroxis/map/Symmetry;", "FIELD:Lcom/faforever/neroxis/map/SymmetrySettings;->teamSymmetry:Lcom/faforever/neroxis/map/Symmetry;", "FIELD:Lcom/faforever/neroxis/map/SymmetrySettings;->spawnSymmetry:Lcom/faforever/neroxis/map/Symmetry;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, SymmetrySettings.class), SymmetrySettings.class, "terrainSymmetry;teamSymmetry;spawnSymmetry", "FIELD:Lcom/faforever/neroxis/map/SymmetrySettings;->terrainSymmetry:Lcom/faforever/neroxis/map/Symmetry;", "FIELD:Lcom/faforever/neroxis/map/SymmetrySettings;->teamSymmetry:Lcom/faforever/neroxis/map/Symmetry;", "FIELD:Lcom/faforever/neroxis/map/SymmetrySettings;->spawnSymmetry:Lcom/faforever/neroxis/map/Symmetry;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, SymmetrySettings.class, Object.class), SymmetrySettings.class, "terrainSymmetry;teamSymmetry;spawnSymmetry", "FIELD:Lcom/faforever/neroxis/map/SymmetrySettings;->terrainSymmetry:Lcom/faforever/neroxis/map/Symmetry;", "FIELD:Lcom/faforever/neroxis/map/SymmetrySettings;->teamSymmetry:Lcom/faforever/neroxis/map/Symmetry;", "FIELD:Lcom/faforever/neroxis/map/SymmetrySettings;->spawnSymmetry:Lcom/faforever/neroxis/map/Symmetry;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public Symmetry terrainSymmetry() {
        return this.terrainSymmetry;
    }

    public Symmetry teamSymmetry() {
        return this.teamSymmetry;
    }

    public Symmetry spawnSymmetry() {
        return this.spawnSymmetry;
    }

    public SymmetrySettings(Symmetry symmetry) {
        this(symmetry, symmetry, symmetry);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public Symmetry getSymmetry(SymmetryType symmetryType) throws MatchException {
        switch (symmetryType) {
            case TEAM:
                return this.teamSymmetry;
            case TERRAIN:
                return this.terrainSymmetry;
            case SPAWN:
                return this.spawnSymmetry;
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }
}

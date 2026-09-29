package com.faforever.neroxis.bases;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.Army;
import com.faforever.neroxis.map.Group;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.Unit;
import com.faforever.neroxis.util.vector.Vector2;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.LinkedHashSet;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/bases/BaseTemplate.class */
public final class BaseTemplate extends Record {
    private final Vector2 center;
    private final SequencedMap<String, SequencedSet<Vector2>> units;

    public BaseTemplate(Vector2 center, SequencedMap<String, SequencedSet<Vector2>> units) {
        this.center = center;
        this.units = units;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, BaseTemplate.class), BaseTemplate.class, "center;units", "FIELD:Lcom/faforever/neroxis/bases/BaseTemplate;->center:Lcom/faforever/neroxis/util/vector/Vector2;", "FIELD:Lcom/faforever/neroxis/bases/BaseTemplate;->units:Ljava/util/SequencedMap;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, BaseTemplate.class), BaseTemplate.class, "center;units", "FIELD:Lcom/faforever/neroxis/bases/BaseTemplate;->center:Lcom/faforever/neroxis/util/vector/Vector2;", "FIELD:Lcom/faforever/neroxis/bases/BaseTemplate;->units:Ljava/util/SequencedMap;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, BaseTemplate.class, Object.class), BaseTemplate.class, "center;units", "FIELD:Lcom/faforever/neroxis/bases/BaseTemplate;->center:Lcom/faforever/neroxis/util/vector/Vector2;", "FIELD:Lcom/faforever/neroxis/bases/BaseTemplate;->units:Ljava/util/SequencedMap;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public Vector2 center() {
        return this.center;
    }

    public SequencedMap<String, SequencedSet<Vector2>> units() {
        return this.units;
    }

    public void addUnits(Army army, Group group) {
        units().forEach((name, positions) -> {
            positions.forEach(position -> {
                group.addUnit(new Unit(String.format("%s %s Unit %d", army.getId(), group.getId(), Integer.valueOf(group.getUnitCount())), name, position.add(this.center), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT));
            });
        });
    }

    public void flip(Symmetry symmetry) {
        this.units.entrySet().forEach(entry -> {
            entry.setValue((SequencedSet) ((SequencedSet) entry.getValue()).stream().map(position -> {
                return position.flip(new Vector2(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), symmetry);
            }).collect(Collectors.toCollection(LinkedHashSet::new)));
        });
    }
}

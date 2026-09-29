package com.faforever.neroxis.util.serial.biome;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.List;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/serial/biome/PropMaterials.class */
public final class PropMaterials extends Record {
    private final List<String> treeGroups;
    private final List<String> rocks;
    private final List<String> boulders;

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, PropMaterials.class), PropMaterials.class, "treeGroups;rocks;boulders", "FIELD:Lcom/faforever/neroxis/util/serial/biome/PropMaterials;->treeGroups:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/PropMaterials;->rocks:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/PropMaterials;->boulders:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, PropMaterials.class), PropMaterials.class, "treeGroups;rocks;boulders", "FIELD:Lcom/faforever/neroxis/util/serial/biome/PropMaterials;->treeGroups:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/PropMaterials;->rocks:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/PropMaterials;->boulders:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, PropMaterials.class, Object.class), PropMaterials.class, "treeGroups;rocks;boulders", "FIELD:Lcom/faforever/neroxis/util/serial/biome/PropMaterials;->treeGroups:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/PropMaterials;->rocks:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/PropMaterials;->boulders:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public List<String> treeGroups() {
        return this.treeGroups;
    }

    public List<String> rocks() {
        return this.rocks;
    }

    public List<String> boulders() {
        return this.boulders;
    }

    public PropMaterials(List<String> treeGroups, List<String> rocks, List<String> boulders) {
        List<String> treeGroups2 = List.copyOf(treeGroups);
        List<String> rocks2 = List.copyOf(rocks);
        List<String> boulders2 = List.copyOf(boulders);
        this.treeGroups = treeGroups2;
        this.rocks = rocks2;
        this.boulders = boulders2;
    }
}

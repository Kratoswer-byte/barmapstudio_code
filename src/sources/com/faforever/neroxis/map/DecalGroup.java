package com.faforever.neroxis.map;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.List;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/DecalGroup.class */
public final class DecalGroup extends Record {
    private final String name;
    private final List<Integer> data;

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, DecalGroup.class), DecalGroup.class, "name;data", "FIELD:Lcom/faforever/neroxis/map/DecalGroup;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/map/DecalGroup;->data:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, DecalGroup.class), DecalGroup.class, "name;data", "FIELD:Lcom/faforever/neroxis/map/DecalGroup;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/map/DecalGroup;->data:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, DecalGroup.class, Object.class), DecalGroup.class, "name;data", "FIELD:Lcom/faforever/neroxis/map/DecalGroup;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/map/DecalGroup;->data:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public String name() {
        return this.name;
    }

    public List<Integer> data() {
        return this.data;
    }

    public DecalGroup(String name, List<Integer> data) {
        List<Integer> data2 = List.copyOf(data);
        this.name = name;
        this.data = data2;
    }
}

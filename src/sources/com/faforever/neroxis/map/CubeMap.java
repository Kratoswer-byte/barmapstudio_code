package com.faforever.neroxis.map;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/CubeMap.class */
public final class CubeMap extends Record {
    private final String name;
    private final String path;

    public CubeMap(String name, String path) {
        this.name = name;
        this.path = path;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, CubeMap.class), CubeMap.class, "name;path", "FIELD:Lcom/faforever/neroxis/map/CubeMap;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/map/CubeMap;->path:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, CubeMap.class), CubeMap.class, "name;path", "FIELD:Lcom/faforever/neroxis/map/CubeMap;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/map/CubeMap;->path:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, CubeMap.class, Object.class), CubeMap.class, "name;path", "FIELD:Lcom/faforever/neroxis/map/CubeMap;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/map/CubeMap;->path:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public String name() {
        return this.name;
    }

    public String path() {
        return this.path;
    }
}

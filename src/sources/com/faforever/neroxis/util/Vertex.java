package com.faforever.neroxis.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/Vertex.class */
public final class Vertex extends Record {
    private final int x;
    private final int y;

    public Vertex(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Vertex.class), Vertex.class, "x;y", "FIELD:Lcom/faforever/neroxis/util/Vertex;->x:I", "FIELD:Lcom/faforever/neroxis/util/Vertex;->y:I").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Vertex.class), Vertex.class, "x;y", "FIELD:Lcom/faforever/neroxis/util/Vertex;->x:I", "FIELD:Lcom/faforever/neroxis/util/Vertex;->y:I").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Vertex.class, Object.class), Vertex.class, "x;y", "FIELD:Lcom/faforever/neroxis/util/Vertex;->x:I", "FIELD:Lcom/faforever/neroxis/util/Vertex;->y:I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }
}

package com.faforever.neroxis.util.serial.biome;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.List;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/serial/biome/DecalMaterials.class */
public final class DecalMaterials extends Record {
    private final List<String> fieldNormals;
    private final List<String> fieldAlbedos;
    private final List<String> mountainNormals;
    private final List<String> mountainAlbedos;
    private final List<String> slopeNormals;
    private final List<String> slopeAlbedos;

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, DecalMaterials.class), DecalMaterials.class, "fieldNormals;fieldAlbedos;mountainNormals;mountainAlbedos;slopeNormals;slopeAlbedos", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->fieldNormals:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->fieldAlbedos:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->mountainNormals:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->mountainAlbedos:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->slopeNormals:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->slopeAlbedos:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, DecalMaterials.class), DecalMaterials.class, "fieldNormals;fieldAlbedos;mountainNormals;mountainAlbedos;slopeNormals;slopeAlbedos", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->fieldNormals:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->fieldAlbedos:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->mountainNormals:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->mountainAlbedos:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->slopeNormals:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->slopeAlbedos:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, DecalMaterials.class, Object.class), DecalMaterials.class, "fieldNormals;fieldAlbedos;mountainNormals;mountainAlbedos;slopeNormals;slopeAlbedos", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->fieldNormals:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->fieldAlbedos:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->mountainNormals:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->mountainAlbedos:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->slopeNormals:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;->slopeAlbedos:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public List<String> fieldNormals() {
        return this.fieldNormals;
    }

    public List<String> fieldAlbedos() {
        return this.fieldAlbedos;
    }

    public List<String> mountainNormals() {
        return this.mountainNormals;
    }

    public List<String> mountainAlbedos() {
        return this.mountainAlbedos;
    }

    public List<String> slopeNormals() {
        return this.slopeNormals;
    }

    public List<String> slopeAlbedos() {
        return this.slopeAlbedos;
    }

    public DecalMaterials(List<String> fieldNormals, List<String> fieldAlbedos, List<String> mountainNormals, List<String> mountainAlbedos, List<String> slopeNormals, List<String> slopeAlbedos) {
        List<String> fieldNormals2 = List.copyOf(fieldNormals);
        List<String> fieldAlbedos2 = List.copyOf(fieldAlbedos);
        List<String> mountainNormals2 = List.copyOf(mountainNormals);
        List<String> mountainAlbedos2 = List.copyOf(mountainAlbedos);
        List<String> slopeNormals2 = List.copyOf(slopeNormals);
        List<String> slopeAlbedos2 = List.copyOf(slopeAlbedos);
        this.fieldNormals = fieldNormals2;
        this.fieldAlbedos = fieldAlbedos2;
        this.mountainNormals = mountainNormals2;
        this.mountainAlbedos = mountainAlbedos2;
        this.slopeNormals = slopeNormals2;
        this.slopeAlbedos = slopeAlbedos2;
    }
}

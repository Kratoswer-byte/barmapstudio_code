package com.faforever.neroxis.util.serial.biome;

import com.faforever.neroxis.util.vector.Vector3;
import com.faforever.neroxis.util.vector.Vector4;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/serial/biome/SCUnitSet.class */
public final class SCUnitSet extends Record {

    @JsonProperty("Units")
    private final List<SCUnit> units;

    @JsonProperty("Center")
    private final Vector3 center;

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, SCUnitSet.class), SCUnitSet.class, "units;center", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet;->units:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet;->center:Lcom/faforever/neroxis/util/vector/Vector3;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, SCUnitSet.class), SCUnitSet.class, "units;center", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet;->units:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet;->center:Lcom/faforever/neroxis/util/vector/Vector3;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, SCUnitSet.class, Object.class), SCUnitSet.class, "units;center", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet;->units:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet;->center:Lcom/faforever/neroxis/util/vector/Vector3;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    @JsonProperty("Units")
    public List<SCUnit> units() {
        return this.units;
    }

    @JsonProperty("Center")
    public Vector3 center() {
        return this.center;
    }

    public SCUnitSet(@JsonProperty("Units") List<SCUnit> units, @JsonProperty("Center") Vector3 center) {
        Objects.requireNonNull(center);
        this.units = List.copyOf(units);
        this.center = center;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit.class */
    public static final class SCUnit extends Record {
        private final String ID;
        private final Vector3 pos;
        private final Vector4 rot;
        private final String orders;
        private final String platoon;

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, SCUnit.class), SCUnit.class, "ID;pos;rot;orders;platoon", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->ID:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->pos:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->rot:Lcom/faforever/neroxis/util/vector/Vector4;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->orders:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->platoon:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, SCUnit.class), SCUnit.class, "ID;pos;rot;orders;platoon", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->ID:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->pos:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->rot:Lcom/faforever/neroxis/util/vector/Vector4;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->orders:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->platoon:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, SCUnit.class, Object.class), SCUnit.class, "ID;pos;rot;orders;platoon", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->ID:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->pos:Lcom/faforever/neroxis/util/vector/Vector3;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->rot:Lcom/faforever/neroxis/util/vector/Vector4;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->orders:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/util/serial/biome/SCUnitSet$SCUnit;->platoon:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String ID() {
            return this.ID;
        }

        public Vector3 pos() {
            return this.pos;
        }

        public Vector4 rot() {
            return this.rot;
        }

        public String orders() {
            return this.orders;
        }

        public String platoon() {
            return this.platoon;
        }

        public SCUnit(String ID, Vector3 pos, Vector4 rot, String orders, String platoon) {
            Objects.requireNonNull(ID);
            Objects.requireNonNull(pos);
            Objects.requireNonNull(rot);
            Objects.requireNonNull(orders);
            Objects.requireNonNull(platoon);
            this.ID = ID;
            this.pos = pos;
            this.rot = rot;
            this.orders = orders;
            this.platoon = platoon;
        }
    }
}

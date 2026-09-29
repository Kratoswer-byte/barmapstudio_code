package com.faforever.neroxis.map;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/DecalType.class */
public interface DecalType {
    int typeNum();

    static DecalType of(int typeNum) {
        switch (typeNum) {
            case 1:
                return Known.ALBEDO;
            case 2:
                return Known.NORMALS;
            case 3:
                return Known.WATER_MASK;
            case 4:
                return Known.WATER_ALBEDO;
            case 5:
                return Known.WATER_NORMALS;
            case 6:
                return Known.GLOW;
            case 7:
                return Known.ALPHA_NORMALS;
            case 8:
                return Known.GLOW_MASK;
            default:
                return new Unknown(typeNum);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/DecalType$Known.class */
    public enum Known implements DecalType {
        ALBEDO(1),
        NORMALS(2),
        WATER_MASK(3),
        WATER_ALBEDO(4),
        WATER_NORMALS(5),
        GLOW(6),
        ALPHA_NORMALS(7),
        GLOW_MASK(8);

        private final int typeNum;

        @Generated
        Known(final int typeNum) {
            this.typeNum = typeNum;
        }

        @Override // com.faforever.neroxis.map.DecalType
        public int typeNum() {
            return this.typeNum;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/DecalType$Unknown.class */
    public static final class Unknown extends Record implements DecalType {
        private final int typeNum;

        public Unknown(int typeNum) {
            this.typeNum = typeNum;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Unknown.class), Unknown.class, "typeNum", "FIELD:Lcom/faforever/neroxis/map/DecalType$Unknown;->typeNum:I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Unknown.class), Unknown.class, "typeNum", "FIELD:Lcom/faforever/neroxis/map/DecalType$Unknown;->typeNum:I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Unknown.class, Object.class), Unknown.class, "typeNum", "FIELD:Lcom/faforever/neroxis/map/DecalType$Unknown;->typeNum:I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        @Override // com.faforever.neroxis.map.DecalType
        public int typeNum() {
            return this.typeNum;
        }
    }
}

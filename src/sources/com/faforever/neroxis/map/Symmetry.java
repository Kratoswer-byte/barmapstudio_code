package com.faforever.neroxis.map;

import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/Symmetry.class */
public enum Symmetry {
    POINT2(true, 2),
    POINT3(false, 3),
    POINT4(true, 4),
    POINT5(false, 5),
    POINT6(false, 6),
    POINT7(false, 7),
    POINT8(false, 8),
    POINT9(false, 9),
    POINT10(false, 10),
    POINT11(false, 11),
    POINT12(false, 12),
    POINT13(false, 13),
    POINT14(false, 14),
    POINT15(false, 15),
    POINT16(false, 16),
    XZ(true, 2),
    ZX(true, 2),
    X(true, 2),
    Z(true, 2),
    QUAD(true, 4),
    DIAG(true, 4),
    NONE(true, 1);

    private final boolean perfectSymmetry;
    private final int numSymPoints;

    @Generated
    Symmetry(final boolean perfectSymmetry, final int numSymPoints) {
        this.perfectSymmetry = perfectSymmetry;
        this.numSymPoints = numSymPoints;
    }

    @Generated
    public boolean isPerfectSymmetry() {
        return this.perfectSymmetry;
    }

    @Generated
    public int getNumSymPoints() {
        return this.numSymPoints;
    }
}

package com.faforever.neroxis.util.functional;

import com.faforever.neroxis.util.vector.Vector2;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/functional/SymmetryRegionBoundsChecker.class */
public interface SymmetryRegionBoundsChecker {
    boolean inBounds(int x, int y);

    default boolean inBounds(Vector2 location) {
        return inBounds((int) location.x(), (int) location.y());
    }
}

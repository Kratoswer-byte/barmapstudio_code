package com.faforever.neroxis.generator.util;

import com.faforever.neroxis.generator.ParameterConstraints;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/util/HasParameterConstraints.class */
public interface HasParameterConstraints {
    default ParameterConstraints parameterConstraints() {
        return ParameterConstraints.ANY;
    }
}

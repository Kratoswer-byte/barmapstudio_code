package com.faforever.neroxis.generator.cli;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.generator.util.serial.GeneratedMapNameEncoder;
import com.faforever.neroxis.util.MathUtil;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/cli/BinnedDensityConverter.class */
public class BinnedDensityConverter implements CommandLine.ITypeConverter<Float> {
    /* JADX INFO: Thrown type has an unknown type hierarchy: picocli.CommandLine$TypeConversionException */
    /* JADX INFO: renamed from: convert, reason: merged with bridge method [inline-methods] */
    public Float m38convert(String value) throws CommandLine.TypeConversionException {
        float percent = Float.parseFloat(value);
        if (percent < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT || percent > 1.0f) {
            throw new CommandLine.TypeConversionException(String.format("Must be between 0 and 1 but was `%f`", Float.valueOf(percent)));
        }
        return Float.valueOf(MathUtil.discretePercentage(percent, GeneratedMapNameEncoder.NUM_BINS));
    }
}

package com.faforever.neroxis.cli;

import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/cli/PowerOfTwoMapSizeConverter.class */
public class PowerOfTwoMapSizeConverter extends MapSizeConverter {
    /* JADX INFO: Thrown type has an unknown type hierarchy: picocli.CommandLine$TypeConversionException */
    @Override // com.faforever.neroxis.cli.MapSizeConverter
    /* JADX INFO: renamed from: convert */
    public Integer mo5convert(String rawValue) throws CommandLine.TypeConversionException {
        int value = super.mo5convert(rawValue).intValue();
        if ((StrictMath.log(value) / StrictMath.log(2.0d)) % 1.0d != 0.0d) {
            throw new CommandLine.TypeConversionException(String.format("Size must be a power of 2 but is %d", Integer.valueOf(value)));
        }
        return Integer.valueOf(value);
    }
}

package com.faforever.neroxis.cli;

import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/cli/MapSizeConverter.class */
public class MapSizeConverter implements CommandLine.ITypeConverter<Integer> {
    @Override // 
    /* JADX INFO: renamed from: convert, reason: merged with bridge method [inline-methods] */
    public Integer mo5convert(String rawValue) {
        if (rawValue.endsWith("km")) {
            String kmString = rawValue.replace("km", "");
            float kmValue = Float.parseFloat(kmString);
            return Integer.valueOf((int) (((double) kmValue) * 51.2d));
        }
        return Integer.valueOf(Integer.parseInt(rawValue));
    }
}

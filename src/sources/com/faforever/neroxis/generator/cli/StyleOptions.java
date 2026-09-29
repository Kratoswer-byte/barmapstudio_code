package com.faforever.neroxis.generator.cli;

import com.faforever.neroxis.generator.util.serial.MapStyle;
import lombok.Generated;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/cli/StyleOptions.class */
public class StyleOptions {

    @CommandLine.ArgGroup(heading = "Options to create a custom map style%n", exclusive = false)
    private CustomStyleOptions customStyleOptions;
    private MapStyle.Predefined predefinedMapStyle;

    @Generated
    public void setCustomStyleOptions(final CustomStyleOptions customStyleOptions) {
        this.customStyleOptions = customStyleOptions;
    }

    @Generated
    public CustomStyleOptions getCustomStyleOptions() {
        return this.customStyleOptions;
    }

    @Generated
    public MapStyle.Predefined getPredefinedMapStyle() {
        return this.predefinedMapStyle;
    }

    @CommandLine.Option(names = {"--style"}, order = 50, description = {"Style for the generated map. Values: ${COMPLETION-CANDIDATES}"})
    public void setPredefinedMapStyle(MapStyle.Predefined predefinedMapStyle) {
        if (this.predefinedMapStyle != null) {
            throw new IllegalStateException("Map style is already set");
        }
        this.predefinedMapStyle = predefinedMapStyle;
    }
}

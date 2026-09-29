package com.faforever.neroxis.generator.util.serial;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Objects;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/util/serial/GeneratorParseOutput.class */
public final class GeneratorParseOutput extends Record {
    private final MapNameParameters parameters;
    private final String mapName;

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, GeneratorParseOutput.class), GeneratorParseOutput.class, "parameters;mapName", "FIELD:Lcom/faforever/neroxis/generator/util/serial/GeneratorParseOutput;->parameters:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/GeneratorParseOutput;->mapName:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, GeneratorParseOutput.class), GeneratorParseOutput.class, "parameters;mapName", "FIELD:Lcom/faforever/neroxis/generator/util/serial/GeneratorParseOutput;->parameters:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/GeneratorParseOutput;->mapName:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, GeneratorParseOutput.class, Object.class), GeneratorParseOutput.class, "parameters;mapName", "FIELD:Lcom/faforever/neroxis/generator/util/serial/GeneratorParseOutput;->parameters:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/GeneratorParseOutput;->mapName:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public MapNameParameters parameters() {
        return this.parameters;
    }

    public String mapName() {
        return this.mapName;
    }

    public GeneratorParseOutput(MapNameParameters parameters) {
        this(parameters, GeneratedMapNameEncoder.encode(parameters));
    }

    public GeneratorParseOutput(MapNameParameters parameters, String mapName) {
        if (Objects.equals(GeneratedMapNameEncoder.encode(parameters), mapName)) {
            this.parameters = parameters;
            this.mapName = mapName;
            return;
        }
        throw new IllegalArgumentException("Map name does not match parameters");
    }
}

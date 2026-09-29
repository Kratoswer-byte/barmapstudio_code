package com.faforever.neroxis.generator.bar;

import java.awt.Color;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.List;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/PlayerColors.class */
final class PlayerColors {
    static final List<Named> FAF = List.of((Object[]) new Named[]{new Named("Cybran red", new Color(232, 10, 10)), new Named("Dark red", new Color(144, 20, 39)), new Named("Nomads orange", new Color(255, 135, 62)), new Named("Brown", new Color(183, 101, 24)), new Named("Sera golden", new Color(167, 150, 2)), new Named("Yellow", new Color(250, 250, 0)), new Named("Order green", new Color(159, 216, 2)), new Named("Mid green", new Color(64, 191, 64)), new Named("Green", new Color(46, 139, 87)), new Named("Olive", new Color(47, 79, 79)), new Named("Blue", new Color(67, 110, 238)), new Named("UEF blue", new Color(41, 41, 225)), new Named("Dark purple", new Color(95, 1, 167)), new Named("Purple", new Color(145, 97, 255)), new Named("Aqua", new Color(102, 255, 204)), new Named("White", new Color(255, 255, 255)), new Named("Grey", new Color(97, 109, 126)), new Named("Pink", new Color(255, 136, 255)), new Named("Fuchsia", new Color(255, 50, 255))});
    static final int DEFAULT = 16744192;

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/PlayerColors$Named.class */
    static final class Named extends Record {
        private final String name;
        private final Color color;

        Named(String name, Color color) {
            this.name = name;
            this.color = color;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Named.class), Named.class, "name;color", "FIELD:Lcom/faforever/neroxis/generator/bar/PlayerColors$Named;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/PlayerColors$Named;->color:Ljava/awt/Color;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Named.class), Named.class, "name;color", "FIELD:Lcom/faforever/neroxis/generator/bar/PlayerColors$Named;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/PlayerColors$Named;->color:Ljava/awt/Color;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Named.class, Object.class), Named.class, "name;color", "FIELD:Lcom/faforever/neroxis/generator/bar/PlayerColors$Named;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/PlayerColors$Named;->color:Ljava/awt/Color;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String name() {
            return this.name;
        }

        public Color color() {
            return this.color;
        }

        String hex() {
            return String.format("#%02x%02x%02x", Integer.valueOf(this.color.getRed()), Integer.valueOf(this.color.getGreen()), Integer.valueOf(this.color.getBlue()));
        }
    }

    private PlayerColors() {
    }

    static int toProtocol(Color color) {
        return (color.getBlue() << 16) | (color.getGreen() << 8) | color.getRed();
    }

    static Color fromProtocol(int value) {
        return new Color(value & 255, (value >> 8) & 255, (value >> 16) & 255);
    }

    static String hex(int protocolValue) {
        Color color = fromProtocol(protocolValue);
        return String.format("#%02x%02x%02x", Integer.valueOf(color.getRed()), Integer.valueOf(color.getGreen()), Integer.valueOf(color.getBlue()));
    }

    static String describe(int protocolValue) {
        Color color = fromProtocol(protocolValue);
        for (Named named : FAF) {
            if (named.color().equals(color)) {
                return named.name();
            }
        }
        return hex(protocolValue);
    }
}

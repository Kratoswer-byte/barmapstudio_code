package com.faforever.neroxis.util;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/EndianSwapper.class */
public class EndianSwapper {
    public static short swap(short value) {
        int b1 = value & 255;
        int b2 = (value >> 8) & 255;
        return (short) ((b1 << 8) | b2);
    }

    public static float swap(float value) {
        int intValue = Float.floatToRawIntBits(value);
        return Float.intBitsToFloat(swap(intValue));
    }

    public static int swap(int value) {
        int b1 = value & 255;
        int b2 = (value >> 8) & 255;
        int b3 = (value >> 16) & 255;
        int b4 = (value >> 24) & 255;
        return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }
}

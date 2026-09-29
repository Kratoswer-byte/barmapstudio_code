package com.faforever.neroxis.util.dds;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/dds/DDSReader.class */
public final class DDSReader {
    private static final int DXT1 = 1146639409;
    private static final int DXT2 = 1146639410;
    private static final int DXT3 = 1146639411;
    private static final int DXT4 = 1146639412;
    private static final int DXT5 = 1146639413;
    private static final int A1R5G5B5 = 65538;
    private static final int X1R5G5B5 = 131074;
    private static final int A4R4G4B4 = 196610;
    private static final int X4R4G4B4 = 262146;
    private static final int R5G6B5 = 327682;
    private static final int R8G8B8 = 65539;
    private static final int A8B8G8R8 = 65540;
    private static final int X8B8G8R8 = 131076;
    private static final int A8R8G8B8 = 196612;
    private static final int X8R8G8B8 = 262148;
    public static final Order ARGB = new Order(16, 8, 0, 24);
    public static final Order ABGR = new Order(0, 8, 16, 24);
    private static final int[] A1R5G5B5_MASKS = {31744, 992, 31, DDSHeader.CUBEMAP_NEGATIVEZ_FLAG};
    private static final int[] X1R5G5B5_MASKS = {31744, 992, 31, 0};
    private static final int[] A4R4G4B4_MASKS = {3840, 240, 15, 61440};
    private static final int[] X4R4G4B4_MASKS = {3840, 240, 15, 0};
    private static final int[] R5G6B5_MASKS = {63488, 2016, 31, 0};
    private static final int[] R8G8B8_MASKS = {16711680, 65280, 255, 0};
    private static final int[] A8B8G8R8_MASKS = {255, 65280, 16711680, -16777216};
    private static final int[] X8B8G8R8_MASKS = {255, 65280, 16711680, 0};
    private static final int[] A8R8G8B8_MASKS = {16711680, 65280, 255, -16777216};
    private static final int[] X8R8G8B8_MASKS = {16711680, 65280, 255, 0};
    private static final int[] BIT5 = {0, 8, 16, 25, 33, 41, 49, 58, 66, 74, 82, 90, 99, 107, 115, 123, 132, 140, 148, 156, 165, 173, 181, 189, 197, 206, 214, 222, 230, 239, 247, 255};
    private static final int[] BIT6 = {0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 45, 49, 53, 57, 61, 65, 69, 73, 77, 81, 85, 89, 93, 97, 101, 105, 109, 113, 117, 121, 125, 130, 134, 138, 142, 146, 150, 154, 158, 162, 166, 170, 174, 178, 182, 186, 190, 194, 198, 202, 206, 210, 215, 219, 223, 227, 231, 235, 239, 243, 247, 251, 255};

    private DDSReader() {
    }

    public static int getHeight(byte[] buffer) {
        return (buffer[12] & 255) | ((buffer[13] & 255) << 8) | ((buffer[14] & 255) << 16) | ((buffer[15] & 255) << 24);
    }

    public static int getWidth(byte[] buffer) {
        return (buffer[16] & 255) | ((buffer[17] & 255) << 8) | ((buffer[18] & 255) << 16) | ((buffer[19] & 255) << 24);
    }

    public static int getMipmap(byte[] buffer) {
        return (buffer[28] & 255) | ((buffer[29] & 255) << 8) | ((buffer[30] & 255) << 16) | ((buffer[31] & 255) << 24);
    }

    public static int getPixelFormatFlags(byte[] buffer) {
        return (buffer[80] & 255) | ((buffer[81] & 255) << 8) | ((buffer[82] & 255) << 16) | ((buffer[83] & 255) << 24);
    }

    public static int getFourCC(byte[] buffer) {
        return ((buffer[84] & 255) << 24) | ((buffer[85] & 255) << 16) | ((buffer[86] & 255) << 8) | (buffer[87] & 255);
    }

    public static int getBitCount(byte[] buffer) {
        return (buffer[88] & 255) | ((buffer[89] & 255) << 8) | ((buffer[90] & 255) << 16) | ((buffer[91] & 255) << 24);
    }

    public static int getRedMask(byte[] buffer) {
        return (buffer[92] & 255) | ((buffer[93] & 255) << 8) | ((buffer[94] & 255) << 16) | ((buffer[95] & 255) << 24);
    }

    public static int getGreenMask(byte[] buffer) {
        return (buffer[96] & 255) | ((buffer[97] & 255) << 8) | ((buffer[98] & 255) << 16) | ((buffer[99] & 255) << 24);
    }

    public static int[] read(byte[] buffer, Order order, int mipmapLevel) {
        int width = getWidth(buffer);
        int height = getHeight(buffer);
        int mipmap = getMipmap(buffer);
        Integer type = getType(buffer);
        if (type == null) {
            return null;
        }
        int offset = 128;
        if (mipmapLevel > 0 && mipmapLevel < mipmap) {
            for (int i = 0; i < mipmapLevel; i++) {
                switch (type.intValue()) {
                    case A1R5G5B5 /* 65538 */:
                    case R8G8B8 /* 65539 */:
                    case A8B8G8R8 /* 65540 */:
                    case X1R5G5B5 /* 131074 */:
                    case X8B8G8R8 /* 131076 */:
                    case A4R4G4B4 /* 196610 */:
                    case A8R8G8B8 /* 196612 */:
                    case X4R4G4B4 /* 262146 */:
                    case X8R8G8B8 /* 262148 */:
                    case R5G6B5 /* 327682 */:
                        offset += (type.intValue() & 255) * width * height;
                        break;
                    case DXT1 /* 1146639409 */:
                        offset += 8 * ((width + 3) / 4) * ((height + 3) / 4);
                        break;
                    case DXT2 /* 1146639410 */:
                    case DXT3 /* 1146639411 */:
                    case DXT4 /* 1146639412 */:
                    case DXT5 /* 1146639413 */:
                        offset += 16 * ((width + 3) / 4) * ((height + 3) / 4);
                        break;
                }
                width /= 2;
                height /= 2;
            }
            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }
        }
        switch (type.intValue()) {
            case A1R5G5B5 /* 65538 */:
                return readA1R5G5B5(width, height, offset, buffer, order);
            case R8G8B8 /* 65539 */:
                return readR8G8B8(width, height, offset, buffer, order);
            case A8B8G8R8 /* 65540 */:
                return readA8B8G8R8(width, height, offset, buffer, order);
            case X1R5G5B5 /* 131074 */:
                return readX1R5G5B5(width, height, offset, buffer, order);
            case X8B8G8R8 /* 131076 */:
                return readX8B8G8R8(width, height, offset, buffer, order);
            case A4R4G4B4 /* 196610 */:
                return readA4R4G4B4(width, height, offset, buffer, order);
            case A8R8G8B8 /* 196612 */:
                return readA8R8G8B8(width, height, offset, buffer, order);
            case X4R4G4B4 /* 262146 */:
                return readX4R4G4B4(width, height, offset, buffer, order);
            case X8R8G8B8 /* 262148 */:
                return readX8R8G8B8(width, height, offset, buffer, order);
            case R5G6B5 /* 327682 */:
                return readR5G6B5(width, height, offset, buffer, order);
            case DXT1 /* 1146639409 */:
                return decodeDXT1(width, height, offset, buffer, order);
            case DXT2 /* 1146639410 */:
                return decodeDXT2(width, height, offset, buffer, order);
            case DXT3 /* 1146639411 */:
                return decodeDXT3(width, height, offset, buffer, order);
            case DXT4 /* 1146639412 */:
                return decodeDXT4(width, height, offset, buffer, order);
            case DXT5 /* 1146639413 */:
                return decodeDXT5(width, height, offset, buffer, order);
            default:
                return null;
        }
    }

    private static Integer getType(byte[] buffer) {
        Integer type = null;
        int flags = getPixelFormatFlags(buffer);
        if ((flags & 4) != 0) {
            type = Integer.valueOf(getFourCC(buffer));
        } else if ((flags & 64) != 0) {
            int bitCount = getBitCount(buffer);
            int redMask = getRedMask(buffer);
            int greenMask = getGreenMask(buffer);
            int blueMask = getBlueMask(buffer);
            int alphaMask = (flags & 1) != 0 ? getAlphaMask(buffer) : 0;
            switch (bitCount) {
                case 16:
                    if (redMask == A1R5G5B5_MASKS[0] && greenMask == A1R5G5B5_MASKS[1] && blueMask == A1R5G5B5_MASKS[2] && alphaMask == A1R5G5B5_MASKS[3]) {
                        type = Integer.valueOf(A1R5G5B5);
                    } else if (redMask == X1R5G5B5_MASKS[0] && greenMask == X1R5G5B5_MASKS[1] && blueMask == X1R5G5B5_MASKS[2] && alphaMask == X1R5G5B5_MASKS[3]) {
                        type = Integer.valueOf(X1R5G5B5);
                    } else if (redMask == A4R4G4B4_MASKS[0] && greenMask == A4R4G4B4_MASKS[1] && blueMask == A4R4G4B4_MASKS[2] && alphaMask == A4R4G4B4_MASKS[3]) {
                        type = Integer.valueOf(A4R4G4B4);
                    } else if (redMask == X4R4G4B4_MASKS[0] && greenMask == X4R4G4B4_MASKS[1] && blueMask == X4R4G4B4_MASKS[2] && alphaMask == X4R4G4B4_MASKS[3]) {
                        type = Integer.valueOf(X4R4G4B4);
                    } else if (redMask == R5G6B5_MASKS[0] && greenMask == R5G6B5_MASKS[1] && blueMask == R5G6B5_MASKS[2] && alphaMask == R5G6B5_MASKS[3]) {
                        type = Integer.valueOf(R5G6B5);
                    }
                    break;
                case 24:
                    if (redMask == R8G8B8_MASKS[0] && greenMask == R8G8B8_MASKS[1] && blueMask == R8G8B8_MASKS[2] && alphaMask == R8G8B8_MASKS[3]) {
                        type = Integer.valueOf(R8G8B8);
                    }
                    break;
                case 32:
                    if (redMask == A8B8G8R8_MASKS[0] && greenMask == A8B8G8R8_MASKS[1] && blueMask == A8B8G8R8_MASKS[2] && alphaMask == A8B8G8R8_MASKS[3]) {
                        type = Integer.valueOf(A8B8G8R8);
                    } else if (redMask == X8B8G8R8_MASKS[0] && greenMask == X8B8G8R8_MASKS[1] && blueMask == X8B8G8R8_MASKS[2] && alphaMask == X8B8G8R8_MASKS[3]) {
                        type = Integer.valueOf(X8B8G8R8);
                    } else if (redMask == A8R8G8B8_MASKS[0] && greenMask == A8R8G8B8_MASKS[1] && blueMask == A8R8G8B8_MASKS[2] && alphaMask == A8R8G8B8_MASKS[3]) {
                        type = Integer.valueOf(A8R8G8B8);
                    } else if (redMask == X8R8G8B8_MASKS[0] && greenMask == X8R8G8B8_MASKS[1] && blueMask == X8R8G8B8_MASKS[2] && alphaMask == X8R8G8B8_MASKS[3]) {
                        type = Integer.valueOf(X8R8G8B8);
                    }
                    break;
            }
        }
        return type;
    }

    public static int getBlueMask(byte[] buffer) {
        return (buffer[100] & 255) | ((buffer[101] & 255) << 8) | ((buffer[102] & 255) << 16) | ((buffer[103] & 255) << 24);
    }

    public static int getAlphaMask(byte[] buffer) {
        return (buffer[104] & 255) | ((buffer[105] & 255) << 8) | ((buffer[106] & 255) << 16) | ((buffer[107] & 255) << 24);
    }

    private static int[] decodeDXT1(int width, int height, int offset, byte[] buffer, Order order) {
        int[] pixels = new int[width * height];
        int index = offset;
        int w = (width + 3) / 4;
        int h = (height + 3) / 4;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int c0 = (buffer[index] & 255) | ((buffer[index + 1] & 255) << 8);
                int index2 = index + 2;
                int c1 = (buffer[index2] & 255) | ((buffer[index2 + 1] & 255) << 8);
                index = index2 + 2;
                for (int k = 0; k < 4 && (4 * i) + k < height; k++) {
                    int t0 = buffer[index] & 3;
                    int t1 = (buffer[index] & 12) >> 2;
                    int t2 = (buffer[index] & 48) >> 4;
                    int i2 = index;
                    index++;
                    int t3 = (buffer[i2] & 192) >> 6;
                    pixels[(4 * width * i) + (4 * j) + (width * k)] = getDXTColor(c0, c1, 255, t0, order);
                    if ((4 * j) + 1 < width) {
                        pixels[(4 * width * i) + (4 * j) + (width * k) + 1] = getDXTColor(c0, c1, 255, t1, order);
                        if ((4 * j) + 2 < width) {
                            pixels[(4 * width * i) + (4 * j) + (width * k) + 2] = getDXTColor(c0, c1, 255, t2, order);
                            if ((4 * j) + 3 < width) {
                                pixels[(4 * width * i) + (4 * j) + (width * k) + 3] = getDXTColor(c0, c1, 255, t3, order);
                            }
                        }
                    }
                }
            }
        }
        return pixels;
    }

    private static int[] decodeDXT2(int width, int height, int offset, byte[] buffer, Order order) {
        return decodeDXT3(width, height, offset, buffer, order);
    }

    private static int[] decodeDXT3(int width, int height, int offset, byte[] buffer, Order order) {
        int index = offset;
        int w = (width + 3) / 4;
        int h = (height + 3) / 4;
        int[] pixels = new int[width * height];
        int[] alphaTable = new int[16];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                for (int k = 0; k < 4; k++) {
                    int i2 = index;
                    int index2 = index + 1;
                    int a0 = buffer[i2] & 255;
                    index = index2 + 1;
                    int a1 = buffer[index2] & 255;
                    alphaTable[4 * k] = 17 * ((a0 & 240) >> 4);
                    alphaTable[(4 * k) + 1] = 17 * (a0 & 15);
                    alphaTable[(4 * k) + 2] = 17 * ((a1 & 240) >> 4);
                    alphaTable[(4 * k) + 3] = 17 * (a1 & 15);
                }
                int c0 = (buffer[index] & 255) | ((buffer[index + 1] & 255) << 8);
                int index3 = index + 2;
                int c1 = (buffer[index3] & 255) | ((buffer[index3 + 1] & 255) << 8);
                index = index3 + 2;
                for (int k2 = 0; k2 < 4 && (4 * i) + k2 < height; k2++) {
                    int t0 = buffer[index] & 3;
                    int t1 = (buffer[index] & 12) >> 2;
                    int t2 = (buffer[index] & 48) >> 4;
                    int i3 = index;
                    index++;
                    int t3 = (buffer[i3] & 192) >> 6;
                    pixels[(4 * width * i) + (4 * j) + (width * k2)] = getDXTColor(c0, c1, alphaTable[4 * k2], t0, order);
                    if ((4 * j) + 1 < width) {
                        pixels[(4 * width * i) + (4 * j) + (width * k2) + 1] = getDXTColor(c0, c1, alphaTable[(4 * k2) + 1], t1, order);
                        if ((4 * j) + 2 < width) {
                            pixels[(4 * width * i) + (4 * j) + (width * k2) + 2] = getDXTColor(c0, c1, alphaTable[(4 * k2) + 2], t2, order);
                            if ((4 * j) + 3 < width) {
                                pixels[(4 * width * i) + (4 * j) + (width * k2) + 3] = getDXTColor(c0, c1, alphaTable[(4 * k2) + 3], t3, order);
                            }
                        }
                    }
                }
            }
        }
        return pixels;
    }

    private static int[] decodeDXT4(int width, int height, int offset, byte[] buffer, Order order) {
        return decodeDXT5(width, height, offset, buffer, order);
    }

    private static int[] decodeDXT5(int width, int height, int offset, byte[] buffer, Order order) {
        int index = offset;
        int w = (width + 3) / 4;
        int h = (height + 3) / 4;
        int[] pixels = new int[width * height];
        int[] alphaTable = new int[16];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int i2 = index;
                int index2 = index + 1;
                int a0 = buffer[i2] & 255;
                int index3 = index2 + 1;
                int a1 = buffer[index2] & 255;
                int b0 = (buffer[index3] & 255) | ((buffer[index3 + 1] & 255) << 8) | ((buffer[index3 + 2] & 255) << 16);
                int index4 = index3 + 3;
                int b1 = (buffer[index4] & 255) | ((buffer[index4 + 1] & 255) << 8) | ((buffer[index4 + 2] & 255) << 16);
                int index5 = index4 + 3;
                alphaTable[0] = b0 & 7;
                alphaTable[1] = (b0 >> 3) & 7;
                alphaTable[2] = (b0 >> 6) & 7;
                alphaTable[3] = (b0 >> 9) & 7;
                alphaTable[4] = (b0 >> 12) & 7;
                alphaTable[5] = (b0 >> 15) & 7;
                alphaTable[6] = (b0 >> 18) & 7;
                alphaTable[7] = (b0 >> 21) & 7;
                alphaTable[8] = b1 & 7;
                alphaTable[9] = (b1 >> 3) & 7;
                alphaTable[10] = (b1 >> 6) & 7;
                alphaTable[11] = (b1 >> 9) & 7;
                alphaTable[12] = (b1 >> 12) & 7;
                alphaTable[13] = (b1 >> 15) & 7;
                alphaTable[14] = (b1 >> 18) & 7;
                alphaTable[15] = (b1 >> 21) & 7;
                int c0 = (buffer[index5] & 255) | ((buffer[index5 + 1] & 255) << 8);
                int index6 = index5 + 2;
                int c1 = (buffer[index6] & 255) | ((buffer[index6 + 1] & 255) << 8);
                index = index6 + 2;
                for (int k = 0; k < 4 && (4 * i) + k < height; k++) {
                    int t0 = buffer[index] & 3;
                    int t1 = (buffer[index] & 12) >> 2;
                    int t2 = (buffer[index] & 48) >> 4;
                    int i3 = index;
                    index++;
                    int t3 = (buffer[i3] & 192) >> 6;
                    pixels[(4 * width * i) + (4 * j) + (width * k)] = getDXTColor(c0, c1, getDXT5Alpha(a0, a1, alphaTable[4 * k]), t0, order);
                    if ((4 * j) + 1 < width) {
                        pixels[(4 * width * i) + (4 * j) + (width * k) + 1] = getDXTColor(c0, c1, getDXT5Alpha(a0, a1, alphaTable[(4 * k) + 1]), t1, order);
                        if ((4 * j) + 2 < width) {
                            pixels[(4 * width * i) + (4 * j) + (width * k) + 2] = getDXTColor(c0, c1, getDXT5Alpha(a0, a1, alphaTable[(4 * k) + 2]), t2, order);
                            if ((4 * j) + 3 < width) {
                                pixels[(4 * width * i) + (4 * j) + (width * k) + 3] = getDXTColor(c0, c1, getDXT5Alpha(a0, a1, alphaTable[(4 * k) + 3]), t3, order);
                            }
                        }
                    }
                }
            }
        }
        return pixels;
    }

    private static int getDXT5Alpha(int a0, int a1, int t) {
        if (a0 > a1) {
            switch (t) {
                case 0:
                    return a0;
                case 1:
                    return a1;
                case 2:
                    return ((6 * a0) + a1) / 7;
                case 3:
                    return ((5 * a0) + (2 * a1)) / 7;
                case 4:
                    return ((4 * a0) + (3 * a1)) / 7;
                case 5:
                    return ((3 * a0) + (4 * a1)) / 7;
                case 6:
                    return ((2 * a0) + (5 * a1)) / 7;
                case 7:
                    return (a0 + (6 * a1)) / 7;
                default:
                    return 0;
            }
        }
        switch (t) {
            case 0:
                return a0;
            case 1:
                return a1;
            case 2:
                return ((4 * a0) + a1) / 5;
            case 3:
                return ((3 * a0) + (2 * a1)) / 5;
            case 4:
                return ((2 * a0) + (3 * a1)) / 5;
            case 5:
                return (a0 + (4 * a1)) / 5;
            case 6:
                return 0;
            case 7:
                return 255;
            default:
                return 0;
        }
    }

    private static int[] readA1R5G5B5(int width, int height, int offset, byte[] buffer, Order order) {
        int index = offset;
        int[] pixels = new int[width * height];
        for (int i = 0; i < height * width; i++) {
            int rgba = (buffer[index] & 255) | ((buffer[index + 1] & 255) << 8);
            index += 2;
            int r = BIT5[(rgba & A1R5G5B5_MASKS[0]) >> 10];
            int g = BIT5[(rgba & A1R5G5B5_MASKS[1]) >> 5];
            int b = BIT5[rgba & A1R5G5B5_MASKS[2]];
            int a = 255 * ((rgba & A1R5G5B5_MASKS[3]) >> 15);
            pixels[i] = (a << order.alphaShift) | (r << order.redShift) | (g << order.greenShift) | (b << order.blueShift);
        }
        return pixels;
    }

    private static int[] readX1R5G5B5(int width, int height, int offset, byte[] buffer, Order order) {
        int index = offset;
        int[] pixels = new int[width * height];
        for (int i = 0; i < height * width; i++) {
            int rgba = (buffer[index] & 255) | ((buffer[index + 1] & 255) << 8);
            index += 2;
            int r = BIT5[(rgba & X1R5G5B5_MASKS[0]) >> 10];
            int g = BIT5[(rgba & X1R5G5B5_MASKS[1]) >> 5];
            int b = BIT5[rgba & X1R5G5B5_MASKS[2]];
            pixels[i] = (255 << order.alphaShift) | (r << order.redShift) | (g << order.greenShift) | (b << order.blueShift);
        }
        return pixels;
    }

    private static int[] readA4R4G4B4(int width, int height, int offset, byte[] buffer, Order order) {
        int index = offset;
        int[] pixels = new int[width * height];
        for (int i = 0; i < height * width; i++) {
            int rgba = (buffer[index] & 255) | ((buffer[index + 1] & 255) << 8);
            index += 2;
            int r = 17 * ((rgba & A4R4G4B4_MASKS[0]) >> 8);
            int g = 17 * ((rgba & A4R4G4B4_MASKS[1]) >> 4);
            int b = 17 * (rgba & A4R4G4B4_MASKS[2]);
            int a = 17 * ((rgba & A4R4G4B4_MASKS[3]) >> 12);
            pixels[i] = (a << order.alphaShift) | (r << order.redShift) | (g << order.greenShift) | (b << order.blueShift);
        }
        return pixels;
    }

    private static int[] readX4R4G4B4(int width, int height, int offset, byte[] buffer, Order order) {
        int index = offset;
        int[] pixels = new int[width * height];
        for (int i = 0; i < height * width; i++) {
            int rgba = (buffer[index] & 255) | ((buffer[index + 1] & 255) << 8);
            index += 2;
            int r = 17 * ((rgba & A4R4G4B4_MASKS[0]) >> 8);
            int g = 17 * ((rgba & A4R4G4B4_MASKS[1]) >> 4);
            int b = 17 * (rgba & A4R4G4B4_MASKS[2]);
            pixels[i] = (255 << order.alphaShift) | (r << order.redShift) | (g << order.greenShift) | (b << order.blueShift);
        }
        return pixels;
    }

    private static int[] readR5G6B5(int width, int height, int offset, byte[] buffer, Order order) {
        int index = offset;
        int[] pixels = new int[width * height];
        for (int i = 0; i < height * width; i++) {
            int rgba = (buffer[index] & 255) | ((buffer[index + 1] & 255) << 8);
            index += 2;
            int r = BIT5[(rgba & R5G6B5_MASKS[0]) >> 11];
            int g = BIT6[(rgba & R5G6B5_MASKS[1]) >> 5];
            int b = BIT5[rgba & R5G6B5_MASKS[2]];
            pixels[i] = (255 << order.alphaShift) | (r << order.redShift) | (g << order.greenShift) | (b << order.blueShift);
        }
        return pixels;
    }

    private static int[] readR8G8B8(int width, int height, int offset, byte[] buffer, Order order) {
        int index = offset;
        int[] pixels = new int[width * height];
        for (int i = 0; i < height * width; i++) {
            int i2 = index;
            int index2 = index + 1;
            int b = buffer[i2] & 255;
            int index3 = index2 + 1;
            int g = buffer[index2] & 255;
            index = index3 + 1;
            int r = buffer[index3] & 255;
            pixels[i] = (255 << order.alphaShift) | (r << order.redShift) | (g << order.greenShift) | (b << order.blueShift);
        }
        return pixels;
    }

    private static int[] readA8B8G8R8(int width, int height, int offset, byte[] buffer, Order order) {
        int index = offset;
        int[] pixels = new int[width * height];
        for (int i = 0; i < height * width; i++) {
            int i2 = index;
            int index2 = index + 1;
            int r = buffer[i2] & 255;
            int index3 = index2 + 1;
            int g = buffer[index2] & 255;
            int index4 = index3 + 1;
            int b = buffer[index3] & 255;
            index = index4 + 1;
            int a = buffer[index4] & 255;
            pixels[i] = (a << order.alphaShift) | (r << order.redShift) | (g << order.greenShift) | (b << order.blueShift);
        }
        return pixels;
    }

    private static int[] readX8B8G8R8(int width, int height, int offset, byte[] buffer, Order order) {
        int index = offset;
        int[] pixels = new int[width * height];
        for (int i = 0; i < height * width; i++) {
            int i2 = index;
            int index2 = index + 1;
            int r = buffer[i2] & 255;
            int index3 = index2 + 1;
            int g = buffer[index2] & 255;
            int b = buffer[index3] & 255;
            index = index3 + 1 + 1;
            pixels[i] = (255 << order.alphaShift) | (r << order.redShift) | (g << order.greenShift) | (b << order.blueShift);
        }
        return pixels;
    }

    private static int[] readA8R8G8B8(int width, int height, int offset, byte[] buffer, Order order) {
        int index = offset;
        int[] pixels = new int[width * height];
        for (int i = 0; i < height * width; i++) {
            int i2 = index;
            int index2 = index + 1;
            int b = buffer[i2] & 255;
            int index3 = index2 + 1;
            int g = buffer[index2] & 255;
            int index4 = index3 + 1;
            int r = buffer[index3] & 255;
            index = index4 + 1;
            int a = buffer[index4] & 255;
            pixels[i] = (a << order.alphaShift) | (r << order.redShift) | (g << order.greenShift) | (b << order.blueShift);
        }
        return pixels;
    }

    private static int getDXTColor(int c0, int c1, int a, int t, Order order) {
        switch (t) {
            case 0:
                return getDXTColor1(c0, a, order);
            case 1:
                return getDXTColor1(c1, a, order);
            case 2:
                return c0 > c1 ? getDXTColor2_1(c0, c1, a, order) : getDXTColor1_1(c0, c1, a, order);
            case 3:
                if (c0 > c1) {
                    return getDXTColor2_1(c1, c0, a, order);
                }
                return 0;
            default:
                return 0;
        }
    }

    private static int getDXTColor2_1(int c0, int c1, int a, Order order) {
        int r = ((2 * BIT5[(c0 & 64512) >> 11]) + BIT5[(c1 & 64512) >> 11]) / 3;
        int g = ((2 * BIT6[(c0 & 2016) >> 5]) + BIT6[(c1 & 2016) >> 5]) / 3;
        int b = ((2 * BIT5[c0 & 31]) + BIT5[c1 & 31]) / 3;
        return (a << order.alphaShift) | (r << order.redShift) | (g << order.greenShift) | (b << order.blueShift);
    }

    private static int getDXTColor1_1(int c0, int c1, int a, Order order) {
        int r = (BIT5[(c0 & 64512) >> 11] + BIT5[(c1 & 64512) >> 11]) / 2;
        int g = (BIT6[(c0 & 2016) >> 5] + BIT6[(c1 & 2016) >> 5]) / 2;
        int b = (BIT5[c0 & 31] + BIT5[c1 & 31]) / 2;
        return (a << order.alphaShift) | (r << order.redShift) | (g << order.greenShift) | (b << order.blueShift);
    }

    private static int getDXTColor1(int c, int a, Order order) {
        int r = BIT5[(c & 64512) >> 11];
        int g = BIT6[(c & 2016) >> 5];
        int b = BIT5[c & 31];
        return (a << order.alphaShift) | (r << order.redShift) | (g << order.greenShift) | (b << order.blueShift);
    }

    private static int[] readX8R8G8B8(int width, int height, int offset, byte[] buffer, Order order) {
        int index = offset;
        int[] pixels = new int[width * height];
        for (int i = 0; i < height * width; i++) {
            int i2 = index;
            int index2 = index + 1;
            int b = buffer[i2] & 255;
            int index3 = index2 + 1;
            int g = buffer[index2] & 255;
            int r = buffer[index3] & 255;
            index = index3 + 1 + 1;
            pixels[i] = (255 << order.alphaShift) | (r << order.redShift) | (g << order.greenShift) | (b << order.blueShift);
        }
        return pixels;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/dds/DDSReader$Order.class */
    public static final class Order {
        public int redShift;
        public int greenShift;
        public int blueShift;
        public int alphaShift;

        private Order(int redShift, int greenShift, int blueShift, int alphaShift) {
            this.redShift = redShift;
            this.greenShift = greenShift;
            this.blueShift = blueShift;
            this.alphaShift = alphaShift;
        }
    }
}

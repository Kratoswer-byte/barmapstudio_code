package com.faforever.neroxis.util.jsquish;

import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/jsquish/Squish.class */
public final class Squish {

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/jsquish/Squish$CompressionMethod.class */
    public enum CompressionMethod {
        CLUSTER_FIT { // from class: com.faforever.neroxis.util.jsquish.Squish.CompressionMethod.1
            @Override // com.faforever.neroxis.util.jsquish.Squish.CompressionMethod
            CompressorColourFit getCompressor(final ColourSet colours, final CompressionType type, final CompressionMetric metric) {
                return new CompressorCluster(colours, type, metric);
            }
        },
        RANGE_FIT { // from class: com.faforever.neroxis.util.jsquish.Squish.CompressionMethod.2
            @Override // com.faforever.neroxis.util.jsquish.Squish.CompressionMethod
            CompressorColourFit getCompressor(final ColourSet colours, final CompressionType type, final CompressionMetric metric) {
                return new CompressorRange(colours, type, metric);
            }
        };

        abstract CompressorColourFit getCompressor(ColourSet colours, CompressionType type, CompressionMetric metric);
    }

    private Squish() {
    }

    public static byte[] compressImage(final byte[] rgba, final int width, final int height, final byte[] blocks, final CompressionType type) {
        return compressImage(rgba, width, height, blocks, type, CompressionMethod.CLUSTER_FIT, CompressionMetric.PERCEPTUAL, false);
    }

    public static byte[] compressImage(final byte[] rgba, final int width, final int height, byte[] blocks, final CompressionType type, final CompressionMethod method, final CompressionMetric metric, final boolean weightAlpha) {
        byte[] blocks2 = checkCompressInput(rgba, width, height, blocks, type);
        byte[] sourceRGBA = new byte[64];
        int targetBlock = 0;
        for (int y = 0; y < height; y += 4) {
            for (int x = 0; x < width; x += 4) {
                int targetPixel = 0;
                int mask = 0;
                for (int py = 0; py < 4; py++) {
                    int sy = y + py;
                    for (int px = 0; px < 4; px++) {
                        int sx = x + px;
                        if (sx < width && sy < height) {
                            int sourcePixel = 4 * ((width * sy) + sx);
                            for (int i = 0; i < 4; i++) {
                                int i2 = targetPixel;
                                targetPixel++;
                                int i3 = sourcePixel;
                                sourcePixel++;
                                sourceRGBA[i2] = rgba[i3];
                            }
                            mask |= 1 << ((4 * py) + px);
                        } else {
                            targetPixel += 4;
                        }
                    }
                }
                compress(sourceRGBA, mask, blocks2, targetBlock, type, method, metric, weightAlpha);
                targetBlock += type.blockSize;
            }
        }
        return blocks2;
    }

    private static byte[] checkCompressInput(final byte[] rgba, final int width, final int height, byte[] blocks, final CompressionType type) {
        int storageSize = getStorageRequirements(width, height, type);
        if (rgba == null || rgba.length < width * height * 4) {
            throw new IllegalArgumentException("Invalid source image data specified.");
        }
        if (blocks == null || blocks.length < storageSize) {
            blocks = new byte[storageSize];
        }
        return blocks;
    }

    public static int getStorageRequirements(final int width, final int height, final CompressionType type) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid image dimensions specified: " + width + " x " + height);
        }
        int blockcount = ((width + 3) / 4) * ((height + 3) / 4);
        return blockcount * type.blockSize;
    }

    private static void compress(final byte[] rgba, final int mask, final byte[] block, final int offset, final CompressionType type, final CompressionMethod method, final CompressionMetric metric, final boolean weightAlpha) {
        CompressorColourFit fit;
        int colourBlock = offset + type.blockOffset;
        ColourSet colours = new ColourSet();
        colours.init(rgba, mask, type, weightAlpha);
        if (colours.getCount() == 1) {
            fit = new CompressorSingleColour(colours, type);
        } else {
            fit = method.getCompressor(colours, type, metric);
        }
        fit.compress(block, colourBlock);
        if (type == CompressionType.DXT3) {
            AlphaBlock.compressAlphaDxt3(rgba, mask, block, offset);
        } else if (type == CompressionType.DXT5) {
            AlphaBlock.compressAlphaDxt5(rgba, mask, block, offset);
        }
    }

    public static byte[] compressImage(final byte[] rgba, final int width, final int height, final byte[] blocks, final CompressionType type, final CompressionMethod method) {
        return compressImage(rgba, width, height, blocks, type, method, CompressionMetric.PERCEPTUAL, false);
    }

    public static byte[] decompressImage(byte[] rgba, final int width, final int height, final byte[] blocks, final CompressionType type) {
        byte[] rgba2 = checkDecompressInput(rgba, width, height, blocks, type);
        byte[] targetRGBA = new byte[64];
        int sourceBlock = 0;
        for (int y = 0; y < height; y += 4) {
            for (int x = 0; x < width; x += 4) {
                decompress(targetRGBA, blocks, sourceBlock, type);
                int sourcePixel = 0;
                for (int py = 0; py < 4; py++) {
                    for (int px = 0; px < 4; px++) {
                        int sx = x + px;
                        int sy = y + py;
                        if (sx < width && sy < height) {
                            int targetPixel = 4 * ((width * sy) + sx);
                            for (int i = 0; i < 4; i++) {
                                int i2 = targetPixel;
                                targetPixel++;
                                int i3 = sourcePixel;
                                sourcePixel++;
                                rgba2[i2] = targetRGBA[i3];
                            }
                        } else {
                            sourcePixel += 4;
                        }
                    }
                }
                sourceBlock += type.blockSize;
            }
        }
        return rgba2;
    }

    private static byte[] checkDecompressInput(byte[] rgba, final int width, final int height, final byte[] blocks, final CompressionType type) {
        int storageSize = getStorageRequirements(width, height, type);
        if (blocks == null || blocks.length < storageSize) {
            throw new IllegalArgumentException("Invalid source image data specified.");
        }
        if (rgba == null || rgba.length < width * height * 4) {
            rgba = new byte[width * height * 4];
        }
        return rgba;
    }

    private static void decompress(final byte[] rgba, final byte[] block, final int offset, final CompressionType type) {
        int colourBlockOffset = offset + type.blockOffset;
        ColourBlock.decompressColour(rgba, block, colourBlockOffset, type == CompressionType.DXT1);
        if (type == CompressionType.DXT3) {
            AlphaBlock.decompressAlphaDxt3(rgba, block, offset);
        } else if (type == CompressionType.DXT5) {
            AlphaBlock.decompressAlphaDxt5(rgba, block, offset);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/jsquish/Squish$CompressionType.class */
    public enum CompressionType {
        DXT1(8),
        DXT3(16),
        DXT5(16);

        public final int blockSize;
        public final int blockOffset;

        @Generated
        public int getBlockSize() {
            return this.blockSize;
        }

        @Generated
        public int getBlockOffset() {
            return this.blockOffset;
        }

        CompressionType(final int blockSize) {
            this.blockSize = blockSize;
            this.blockOffset = blockSize - 8;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/jsquish/Squish$CompressionMetric.class */
    public enum CompressionMetric {
        PERCEPTUAL(0.2126f, 0.7152f, 0.0722f),
        UNIFORM(1.0f, 1.0f, 1.0f);

        public final float r;
        public final float g;
        public final float b;

        CompressionMetric(final float r, final float g, final float b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public float dot(final float x, final float y, final float z) {
            return (this.r * x) + (this.g * y) + (this.b * z);
        }
    }
}

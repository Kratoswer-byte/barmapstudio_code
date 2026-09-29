package com.faforever.neroxis.util;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.mask.FloatMask;
import com.faforever.neroxis.mask.NormalMask;
import com.faforever.neroxis.mask.Vector4Mask;
import com.faforever.neroxis.util.dds.DDSHeader;
import com.faforever.neroxis.util.jsquish.Squish;
import com.faforever.neroxis.util.serial.biome.LightingSettings;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import com.faforever.neroxis.util.vector.Vector4;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Hashtable;
import javax.imageio.ImageIO;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/ImageUtil.class */
public class ImageUtil {
    public static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
        if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
            return false;
        }
        int width = imgA.getWidth();
        int height = imgA.getHeight();
        int[] pixelsA = imgA.getRaster().getPixels(0, 0, width, height, (int[]) null);
        int[] pixelsB = imgB.getRaster().getPixels(0, 0, width, height, (int[]) null);
        return Arrays.equals(pixelsA, pixelsB);
    }

    public static int[] getImagePixels(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        return image.getRaster().getPixels(0, 0, width, height, (int[]) null);
    }

    public static BufferedImage readImage(String resource) throws IOException {
        InputStream inputStream = ResourceUtil.getResourceAsStream(resource);
        try {
            if (inputStream == null) {
                throw new IOException("File not found: " + resource);
            }
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
            return bufferedImage;
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static BufferedImage scaleImage(BufferedImage image, int width, int height) {
        int width2 = StrictMath.max(width, 1);
        int height2 = StrictMath.max(height, 1);
        BufferedImage imageScaled = new BufferedImage(width2, height2, image.getType());
        AffineTransform at = new AffineTransform();
        at.scale(((double) width2) / ((double) image.getWidth()), ((double) height2) / ((double) image.getHeight()));
        AffineTransformOp scaleOp = new AffineTransformOp(at, 2);
        scaleOp.filter(image, imageScaled);
        return imageScaled;
    }

    public static BufferedImage rotateImage(BufferedImage image, float radians) {
        BufferedImage imageScaled = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        AffineTransform at = new AffineTransform();
        at.rotate(radians, ((double) image.getWidth()) / 2.0d, ((double) image.getHeight()) / 2.0d);
        AffineTransformOp rotateOp = new AffineTransformOp(at, 2);
        rotateOp.filter(image, imageScaled);
        return imageScaled;
    }

    public static BufferedImage insertImageIntoNewImageOfSize(BufferedImage image, int width, int height, Vector2 locToInsertTopLeft) {
        BufferedImage newImage = new BufferedImage(width, height, image.getType());
        WritableRaster newImageRaster = newImage.getRaster();
        Raster imageRaster = image.getData();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int newX = x + ((int) locToInsertTopLeft.x());
                int newY = y + ((int) locToInsertTopLeft.y());
                if (inImageBounds(newX, newY, newImage)) {
                    newImageRaster.setPixel(newX, newY, imageRaster.getPixel(x, y, new int[image.getColorModel().getNumComponents()]));
                }
            }
        }
        return newImage;
    }

    public static boolean inImageBounds(int x, int y, BufferedImage image) {
        return x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight();
    }

    public static void writeNormalDDS(NormalMask imageMask, Path path) throws IOException {
        int size = imageMask.getSize();
        int length = size * size * 4;
        ByteBuffer imageBytes = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Vector3 value = imageMask.get(x, y);
                byte xV = (byte) StrictMath.min(StrictMath.max((128.0f * value.x()) + 128.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                byte yV = (byte) StrictMath.min(StrictMath.max((128.0f * (1.0f - value.y())) + 127.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                byte zV = (byte) StrictMath.min(StrictMath.max((128.0f * value.z()) + 128.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                imageBytes.put(yV);
                imageBytes.put(zV);
                imageBytes.put((byte) 0);
                imageBytes.put(xV);
            }
        }
        DDSHeader ddsHeader = new DDSHeader();
        ddsHeader.setWidth(imageMask.getSize());
        ddsHeader.setHeight(imageMask.getSize());
        ddsHeader.setFourCC("DXT5");
        byte[] compressedData = Squish.compressImage(imageBytes.array(), ddsHeader.getWidth(), ddsHeader.getHeight(), null, Squish.CompressionType.DXT5);
        Files.write(path, ddsHeader.toBytes(), StandardOpenOption.CREATE);
        Files.write(path, compressedData, StandardOpenOption.APPEND);
    }

    public static void writeCompressedDDS(BufferedImage image, Path path) throws IOException {
        int size = image.getHeight();
        int length = size * size * 4;
        Raster imageRaster = image.getData();
        ByteBuffer imageBytes = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int[] values = imageRaster.getPixel(x, y, new int[4]);
                for (int val : values) {
                    imageBytes.put((byte) val);
                }
            }
        }
        DDSHeader ddsHeader = new DDSHeader();
        ddsHeader.setWidth(size);
        ddsHeader.setHeight(size);
        ddsHeader.setFourCC("DXT5");
        byte[] compressedData = Squish.compressImage(imageBytes.array(), ddsHeader.getWidth(), ddsHeader.getHeight(), null, Squish.CompressionType.DXT5);
        Files.deleteIfExists(path);
        Files.write(path, ddsHeader.toBytes(), StandardOpenOption.CREATE);
        Files.write(path, compressedData, StandardOpenOption.APPEND);
    }

    public static void writeRawDDS(BufferedImage image, Path path) throws IOException {
        int size = image.getHeight();
        int length = size * size * 4;
        Raster imageRaster = image.getData();
        ByteBuffer imageBytes = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int[] values = imageRaster.getPixel(x, y, new int[4]);
                imageBytes.put((byte) values[2]);
                imageBytes.put((byte) values[1]);
                imageBytes.put((byte) values[0]);
                imageBytes.put((byte) values[3]);
            }
        }
        DDSHeader ddsHeader = new DDSHeader();
        ddsHeader.setWidth(size);
        ddsHeader.setHeight(size);
        ddsHeader.setRGBBitCount(32);
        ddsHeader.setBBitMask(255);
        ddsHeader.setGBitMask(65280);
        ddsHeader.setRBitMask(16711680);
        ddsHeader.setABitMask(-16777216);
        Files.deleteIfExists(path);
        Files.write(path, ddsHeader.toBytes(), StandardOpenOption.CREATE);
        Files.write(path, imageBytes.array(), StandardOpenOption.APPEND);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static BufferedImage getMapInfoTexture(FloatMask waterDepth, FloatMask shadowMask) {
        FloatMask waterDepthCopy = (FloatMask) ((FloatMask) waterDepth.copy()).resample(shadowMask.getSize());
        int size = shadowMask.getSize();
        BufferedImage image = new BufferedImage(size, size, 2);
        WritableRaster imageRaster = image.getRaster();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int xV = (byte) StrictMath.min(StrictMath.max(waterDepthCopy.get(x, y).floatValue() * 255.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                int wV = (byte) StrictMath.min(StrictMath.max(shadowMask.get(x, y).floatValue() * 255.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                imageRaster.setPixel(x, y, new int[]{xV, -1, -1, wV});
            }
        }
        return image;
    }

    public static BufferedImage getMapNormalTexture(NormalMask normalMask) {
        int size = normalMask.getSize();
        BufferedImage image = new BufferedImage(size, size, 2);
        WritableRaster imageRaster = image.getRaster();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Vector3 normalValue = normalMask.get(x, y);
                int xV = (byte) StrictMath.min(StrictMath.max((128.0f * normalValue.x()) + 127.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                int zV = (byte) StrictMath.min(StrictMath.max((128.0f * normalValue.z()) + 127.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                imageRaster.setPixel(x, y, new int[]{zV, zV, zV, xV});
            }
        }
        return image;
    }

    public static byte[] compressNormal(NormalMask mask) {
        int size = mask.getSize();
        int length = size * size * 4;
        ByteBuffer imageByteBuffer = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Vector3 value = mask.get(x, y);
                int xV = (byte) StrictMath.min(StrictMath.max((128.0f * value.x()) + 128.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                int yV = (byte) StrictMath.min(StrictMath.max(255.0f * (1.0f - value.y()), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                int zV = (byte) StrictMath.min(StrictMath.max((128.0f * value.z()) + 128.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                imageByteBuffer.put((byte) yV);
                imageByteBuffer.put((byte) zV);
                imageByteBuffer.put((byte) 0);
                imageByteBuffer.put((byte) xV);
            }
        }
        return getCompressedDDSImageBytes(size, imageByteBuffer);
    }

    public static byte[] compressShadow(FloatMask mask, LightingSettings lightingSettings) {
        int size = mask.getSize();
        int length = size * size * 4;
        Vector3 shadowFillColor = lightingSettings.shadowFillColor().add(lightingSettings.sunAmbience()).divide(4.0f);
        float opacityScale = lightingSettings.lightingMultiplier() / 4.0f;
        ByteBuffer imageByteBuffer = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float value = mask.get(x, y).floatValue();
                int r = (byte) StrictMath.min(StrictMath.max(shadowFillColor.x() * 128.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                int g = (byte) StrictMath.min(StrictMath.max(shadowFillColor.y() * 128.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                int b = (byte) StrictMath.min(StrictMath.max(shadowFillColor.z() * 128.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                int a = (byte) StrictMath.min(StrictMath.max((1.0f - value) * opacityScale * 255.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                imageByteBuffer.put((byte) r);
                imageByteBuffer.put((byte) g);
                imageByteBuffer.put((byte) b);
                imageByteBuffer.put((byte) a);
            }
        }
        return getCompressedDDSImageBytes(size, imageByteBuffer);
    }

    public static BufferedImage normalToARGB(NormalMask mask) {
        int size = mask.getSize();
        BufferedImage image = new BufferedImage(size, size, 2);
        WritableRaster imageRaster = image.getRaster();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Vector3 value = mask.get(x, y);
                int xV = (byte) StrictMath.min(StrictMath.max((128.0f * value.x()) + 128.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                int yV = (byte) StrictMath.min(StrictMath.max(255.0f * (1.0f - value.y()), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                int zV = (byte) StrictMath.min(StrictMath.max((128.0f * value.z()) + 128.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                imageRaster.setPixel(x, y, new int[]{yV, zV, 0, xV});
            }
        }
        return image;
    }

    public static byte[] compressVector4(Vector4Mask mask) {
        int size = mask.getSize();
        int length = size * size * 4;
        ByteBuffer imageByteBuffer = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Vector4 value = mask.get(x, y);
                int xV = (byte) StrictMath.min(StrictMath.max(value.x(), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                int yV = (byte) StrictMath.min(StrictMath.max(value.y(), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                int zV = (byte) StrictMath.min(StrictMath.max(value.z(), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                int wV = (byte) StrictMath.min(StrictMath.max(value.w(), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
                imageByteBuffer.put((byte) xV);
                imageByteBuffer.put((byte) yV);
                imageByteBuffer.put((byte) zV);
                imageByteBuffer.put((byte) wV);
            }
        }
        return getCompressedDDSImageBytes(size, imageByteBuffer);
    }

    private static byte[] getCompressedDDSImageBytes(int size, ByteBuffer imageByteBuffer) {
        DDSHeader ddsHeader = new DDSHeader();
        ddsHeader.setWidth(size);
        ddsHeader.setHeight(size);
        ddsHeader.setFourCC("DXT5");
        ddsHeader.toBytes();
        byte[] headerBytes = ddsHeader.toBytes();
        byte[] imageBytes = Squish.compressImage(imageByteBuffer.array(), ddsHeader.getWidth(), ddsHeader.getHeight(), null, Squish.CompressionType.DXT5, Squish.CompressionMethod.RANGE_FIT);
        int headerLength = headerBytes.length;
        int imageLength = imageBytes.length;
        byte[] allBytes = Arrays.copyOf(headerBytes, headerLength + imageLength);
        System.arraycopy(imageBytes, 0, allBytes, headerLength, imageLength);
        return allBytes;
    }

    public static void writeAutoScaledPNGFromMasks(FloatMask redMask, FloatMask greenMask, FloatMask blueMask, Path path) throws IOException {
        float scaleMultiplier = 255.0f / StrictMath.max(StrictMath.max(redMask.getMax().floatValue(), greenMask.getMax().floatValue()), blueMask.getMax().floatValue());
        writePNGFromMasks(redMask, greenMask, blueMask, scaleMultiplier, path);
    }

    public static void writePNGFromMasks(FloatMask redMask, FloatMask greenMask, FloatMask blueMask, float scaleMultiplier, Path path) throws IOException {
        int size = redMask.getSize();
        if (size != greenMask.getSize() || size != blueMask.getSize()) {
            throw new IllegalArgumentException("Masks not the same size: redMask is " + redMask.getSize() + ", greenMask is " + greenMask.getSize() + " and blueMask is " + blueMask.getSize());
        }
        int length = size * size * 3;
        int index = 0;
        byte[] byteArray = new byte[length];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                byte redByte = (byte) Float.valueOf(redMask.get(x, y).floatValue() * scaleMultiplier).intValue();
                byte greenByte = (byte) Float.valueOf(greenMask.get(x, y).floatValue() * scaleMultiplier).intValue();
                byte blueByte = (byte) Float.valueOf(blueMask.get(x, y).floatValue() * scaleMultiplier).intValue();
                byteArray[index] = redByte;
                int index2 = index + 1;
                byteArray[index2] = greenByte;
                int index3 = index2 + 1;
                byteArray[index3] = blueByte;
                index = index3 + 1;
            }
        }
        WritableRaster raster = Raster.createInterleavedRaster(new DataBufferByte(byteArray, byteArray.length), size, size, 3 * size, 3, new int[]{0, 1, 2}, new Point(0, 0));
        BufferedImage image = new BufferedImage(new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, 3, 0), raster, true, (Hashtable) null);
        ImageIO.write(image, "png", path.toFile());
        System.out.println("PNG created at " + String.valueOf(path));
    }

    public static void writePNGFromMask(FloatMask mask, float scaleMultiplier, Path path) throws IOException {
        writePNGFromMasks(mask, mask, mask, scaleMultiplier, path);
    }

    public static void writeAutoScaledPNGFromMask(FloatMask mask, Path path) throws IOException {
        float scaleMultiplier = 255.0f / mask.getMax().floatValue();
        writePNGFromMasks(mask, mask, mask, scaleMultiplier, path);
    }

    public static boolean inImageBounds(Vector2 position, BufferedImage image) {
        return inImageBounds((int) position.x(), (int) position.y(), image);
    }
}

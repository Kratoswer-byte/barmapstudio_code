package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.util.dds.DDSReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.imageio.ImageIO;

/**
 * Cache texture modelli: DDS BAR (mip2 1024) + immagini OBJ (map_Kd) + TGA.
 * Off-EDT, LRU semplice, mai crash se gioco assente.
 */
public final class ModelTextureCache {
    private static final int MAX = 24;
    private final Map<String, BufferedImage> cache = new LinkedHashMap<>(32, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<String, BufferedImage> e) { return size() > MAX; }
    };
    private Supplier<Path> dataSupplier;

    public ModelTextureCache(Supplier<Path> dataSupplier) { this.dataSupplier = dataSupplier; }
    public void setDataSupplier(Supplier<Path> s) { this.dataSupplier = s; }

    public synchronized BufferedImage get(String key) { return cache.get(key); }
    public synchronized void put(String key, BufferedImage img) {
        if (key != null && img != null) cache.put(key, img);
    }

    /** Carica unittextures/<name> dal gioco (DDS/TGA/PNG). */
    public BufferedImage loadGameTexture(String name) {
        if (name == null || name.isBlank()) return null;
        String key = "game:" + name.toLowerCase();
        BufferedImage c = get(key);
        if (c != null) return c;
        if (dataSupplier == null) return null;
        Path data;
        try { data = dataSupplier.get(); } catch (Exception ex) { return null; }
        if (data == null) return null;
        String[] cands = {name, "unittextures/" + name, "unittextures/" + base(name) + ".dds"};
        for (String cand : cands) {
            byte[] bytes = BarInstall.readGameBytes(data, cand);
            if (bytes == null || bytes.length < 128) continue;
            BufferedImage img = decodeGameBytes(bytes);
            if (img != null) { put(key, img); return img; }
        }
        return null;
    }

    private static String base(String n) {
        int s = Math.max(n.lastIndexOf('/'), n.lastIndexOf('\\'));
        String b = s >= 0 ? n.substring(s + 1) : n;
        int d = b.lastIndexOf('.');
        return d > 0 ? b.substring(0, d) : b;
    }

    /** DDS via DDSReader (mip2) oppure TGA/PNG raw. */
    public static BufferedImage decodeGameBytes(byte[] bytes) {
        if (bytes == null || bytes.length < 16) return null;
        boolean isDDS = bytes.length > 4 && bytes[0] == 'D' && bytes[1] == 'D' && bytes[2] == 'S' && bytes[3] == ' ';
        if (isDDS) {
            try {
                int mips = DDSReader.getMipmap(bytes);
                int level = Math.min(2, Math.max(0, mips - 1));
                int[] px = DDSReader.read(bytes, DDSReader.ARGB, level);
                if (px == null) px = DDSReader.read(bytes, DDSReader.ARGB, 0);
                if (px == null) return null;
                int w = DDSReader.getWidth(bytes) >> level;
                int h = DDSReader.getHeight(bytes) >> level;
                if (w <= 0) w = (int) Math.sqrt(px.length);
                if (h <= 0) h = w;
                if (w * h != px.length) {
                    int s = (int) Math.sqrt(px.length);
                    w = s; h = s;
                }
                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                img.setRGB(0, 0, w, h, px, 0, w);
                return img;
            } catch (Exception ex) { return null; }
        }
        // TGA check: footer "TRUEVISION-XFILE" oppure header plausibile
        try {
            BufferedImage tga = readTga(bytes);
            if (tga != null) return tga;
        } catch (Exception ignored) {}
        try {
            return ImageIO.read(new java.io.ByteArrayInputStream(bytes));
        } catch (Exception ex) { return null; }
    }

    /** Mini-reader TGA 24/32bit non compresso + RLE. */
    public static BufferedImage readTga(byte[] d) {
        if (d == null || d.length < 18) return null;
        int idLen = d[0] & 255, cmap = d[1] & 255, type = d[2] & 255;
        if (cmap != 0) return null;
        if (type != 2 && type != 10) return null;
        int w = (d[12] & 255) | ((d[13] & 255) << 8);
        int h = (d[14] & 255) | ((d[15] & 255) << 8);
        int bpp = d[16] & 255;
        if (w <= 0 || h <= 0 || w > 8192 || h > 8192) return null;
        if (bpp != 24 && bpp != 32) return null;
        boolean topLeft = (d[17] & 0x20) != 0;
        int off = 18 + idLen;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int[] px = new int[w * h];
        try {
            if (type == 2) {
                int bpc = bpp / 8;
                for (int y = 0; y < h; y++) {
                    int dy = topLeft ? y : (h - 1 - y);
                    for (int x = 0; x < w; x++) {
                        int b = d[off++] & 255, g = d[off++] & 255, r = d[off++] & 255;
                        int a = bpc == 4 ? (d[off++] & 255) : 255;
                        px[dy * w + x] = (a << 24) | (r << 16) | (g << 8) | b;
                    }
                }
            } else {
                int bpc = bpp / 8, p = 0;
                while (p < w * h) {
                    int pkt = d[off++] & 255;
                    int count = (pkt & 0x7F) + 1;
                    if ((pkt & 0x80) != 0) {
                        int b = d[off++] & 255, g = d[off++] & 255, r = d[off++] & 255;
                        int a = bpc == 4 ? (d[off++] & 255) : 255;
                        int c = (a << 24) | (r << 16) | (g << 8) | b;
                        for (int k = 0; k < count && p < w * h; k++) px[p++] = c;
                    } else {
                        for (int k = 0; k < count && p < w * h; k++) {
                            int b = d[off++] & 255, g = d[off++] & 255, r = d[off++] & 255;
                            int a = bpc == 4 ? (d[off++] & 255) : 255;
                            px[p++] = (a << 24) | (r << 16) | (g << 8) | b;
                        }
                    }
                }
                if (!topLeft) {
                    int[] flip = new int[w * h];
                    for (int y = 0; y < h; y++) System.arraycopy(px, y * w, flip, (h - 1 - y) * w, w);
                    px = flip;
                }
            }
        } catch (Exception ex) { return null; }
        img.setRGB(0, 0, w, h, px, 0, w);
        return img;
    }

    /** Carica immagine da file (PNG/JPG/BMP via ImageIO, TGA via reader). */
    public BufferedImage loadFileImage(File f) {
        if (f == null || !f.isFile()) return null;
        String key = "file:" + f.getAbsolutePath().toLowerCase();
        BufferedImage c = get(key);
        if (c != null) return c;
        try {
            String n = f.getName().toLowerCase();
            BufferedImage img;
            if (n.endsWith(".tga")) img = readTga(java.nio.file.Files.readAllBytes(f.toPath()));
            else img = ImageIO.read(f);
            if (img != null) put(key, img);
            return img;
        } catch (Exception ex) { return null; }
    }
}

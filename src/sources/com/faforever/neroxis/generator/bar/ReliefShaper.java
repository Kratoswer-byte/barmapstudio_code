package com.faforever.neroxis.generator.bar;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Modellazione del rilievo in tempo reale: parte sempre dall'heightmap di base (quella generata,
 * importata o caricata) e applica i cursori del pannello, cosi' gli effetti non si accumulano
 * e riportando un cursore a zero si torna al terreno originale.
 */
public final class ReliefShaper {

    /** Valori dei cursori; tutti a zero = terreno invariato. */
    public static final class Params {
        /** -100..100: negativo = pianure piu' ampie e cime piu' aguzze, positivo = altopiani e valli strette. */
        public int profile;
        /** 0..10: raggio della levigatura generale. */
        public int smooth;
        /** 0 = spente, 2..12 = numero di gradoni. */
        public int terraces;
        /** 0..100: forza dell'erosione (materiale che scivola dai pendii ripidi). */
        public int erosion;
        /** 0..100: piccole irregolarita' aggiunte al terreno. */
        public int detail;
        /** -100..100: abbassa o alza il bordo della mappa. */
        public int border;

        public boolean isNeutral() {
            return profile == 0 && smooth == 0 && terraces == 0 && erosion == 0 && detail == 0 && border == 0;
        }
    }

    private ReliefShaper() {}

    public static BufferedImage copy(BufferedImage src) {
        if (src == null) return null;
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_USHORT_GRAY);
        out.getRaster().setRect(src.getRaster());
        return out;
    }

    /** Restituisce una nuova heightmap a 16 bit con i cursori applicati alla base. */
    public static BufferedImage apply(BufferedImage base, Params p) {
        if (base == null || p == null || p.isNeutral()) {
            return base;
        }
        int w = base.getWidth();
        int h = base.getHeight();
        Raster in = base.getRaster();
        float[] v = new float[w * h];
        int lo = 65535;
        int hi = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int raw = in.getSample(x, y, 0);
                lo = Math.min(lo, raw);
                hi = Math.max(hi, raw);
            }
        }
        float range = Math.max(1, hi - lo);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                v[(y * w) + x] = (in.getSample(x, y, 0) - lo) / range;
            }
        }

        if (p.profile != 0) {
            // esponente > 1 schiaccia le quote basse (pianure ampie), < 1 le alza (altopiani)
            double exponent = Math.pow(2.0, -p.profile / 50.0);
            for (int i = 0; i < v.length; i++) {
                v[i] = (float) Math.pow(v[i], exponent);
            }
        }
        if (p.terraces >= 2) {
            int levels = p.terraces;
            for (int i = 0; i < v.length; i++) {
                float t = v[i] * levels;
                float floor = (float) Math.floor(t);
                // gradino netto ma con una rampa stretta al centro, per non creare muri verticali
                float ramp = smooth(0.32f, 0.68f, t - floor);
                v[i] = Math.min(1.0f, (floor + ramp) / levels);
            }
        }
        if (p.erosion > 0) {
            erode(v, w, h, Math.round(p.erosion * 0.3f), 0.012f - (p.erosion * 0.00008f));
        }
        if (p.smooth > 0) {
            v = boxBlur(boxBlur(v, w, h, p.smooth), w, h, p.smooth);
        }
        if (p.detail > 0) {
            float amplitude = p.detail * 0.00025f;
            boolean square = w == h;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    // rumore mediato sulle posizioni speculari: il dettaglio non rompe la simmetria della mappa
                    float n = noise(x, y) + noise(w - 1 - x, y) + noise(x, h - 1 - y) + noise(w - 1 - x, h - 1 - y);
                    int count = 4;
                    if (square) {
                        n += noise(y, x) + noise(h - 1 - y, x) + noise(y, w - 1 - x) + noise(h - 1 - y, w - 1 - x);
                        count = 8;
                    }
                    v[(y * w) + x] += ((n / count) - 0.5f) * 2.0f * amplitude;
                }
            }
        }
        if (p.border != 0) {
            float band = Math.max(6.0f, Math.min(w, h) * 0.12f);
            float amount = p.border / 100.0f * 0.6f;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    float edge = Math.min(Math.min(x, w - 1 - x), Math.min(y, h - 1 - y));
                    if (edge < band) {
                        float f = 1.0f - smooth(0.0f, band, edge);
                        v[(y * w) + x] += amount * f;
                    }
                }
            }
        }

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_USHORT_GRAY);
        WritableRaster raster = out.getRaster();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float value = Math.max(0.0f, Math.min(1.0f, v[(y * w) + x]));
                raster.setSample(x, y, 0, Math.round(lo + (value * range)));
            }
        }
        return out;
    }

    /** Erosione termica: dove il pendio supera la soglia, parte del materiale scivola sul vicino piu' basso. */
    private static void erode(float[] v, int w, int h, int iterations, float talus) {
        float[] delta = new float[v.length];
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};
        for (int it = 0; it < iterations; it++) {
            java.util.Arrays.fill(delta, 0.0f);
            for (int y = 1; y < h - 1; y++) {
                for (int x = 1; x < w - 1; x++) {
                    int i = (y * w) + x;
                    float best = 0.0f;
                    int target = -1;
                    for (int k = 0; k < 4; k++) {
                        int n = ((y + dy[k]) * w) + x + dx[k];
                        float d = v[i] - v[n];
                        if (d > best) {
                            best = d;
                            target = n;
                        }
                    }
                    if (target >= 0 && best > talus) {
                        float moved = (best - talus) * 0.35f;
                        delta[i] -= moved;
                        delta[target] += moved;
                    }
                }
            }
            for (int i = 0; i < v.length; i++) {
                v[i] += delta[i];
            }
        }
    }

    private static float[] boxBlur(float[] v, int w, int h, int radius) {
        float[] tmp = new float[v.length];
        float[] out = new float[v.length];
        for (int y = 0; y < h; y++) {
            float acc = 0.0f;
            int count = 0;
            for (int x = -radius; x <= radius; x++) {
                if (x >= 0 && x < w) { acc += v[(y * w) + x]; count++; }
            }
            for (int x = 0; x < w; x++) {
                tmp[(y * w) + x] = acc / count;
                int add = x + radius + 1;
                int remove = x - radius;
                if (add < w) { acc += v[(y * w) + add]; count++; }
                if (remove >= 0) { acc -= v[(y * w) + remove]; count--; }
            }
        }
        for (int x = 0; x < w; x++) {
            float acc = 0.0f;
            int count = 0;
            for (int y = -radius; y <= radius; y++) {
                if (y >= 0 && y < h) { acc += tmp[(y * w) + x]; count++; }
            }
            for (int y = 0; y < h; y++) {
                out[(y * w) + x] = acc / count;
                int add = y + radius + 1;
                int remove = y - radius;
                if (add < h) { acc += tmp[(add * w) + x]; count++; }
                if (remove >= 0) { acc -= tmp[(remove * w) + x]; count--; }
            }
        }
        return out;
    }

    /** Rumore a due ottave, 0..1, interpolato in modo morbido. */
    private static float noise(int x, int y) {
        return (valueNoise(x / 9.0f, y / 9.0f) * 0.65f) + (valueNoise(x / 3.5f, y / 3.5f) * 0.35f);
    }

    private static float valueNoise(float x, float y) {
        int x0 = (int) Math.floor(x);
        int y0 = (int) Math.floor(y);
        float tx = x - x0;
        float ty = y - y0;
        tx = tx * tx * (3.0f - (2.0f * tx));
        ty = ty * ty * (3.0f - (2.0f * ty));
        float top = hash(x0, y0) + ((hash(x0 + 1, y0) - hash(x0, y0)) * tx);
        float bottom = hash(x0, y0 + 1) + ((hash(x0 + 1, y0 + 1) - hash(x0, y0 + 1)) * tx);
        return top + ((bottom - top) * ty);
    }

    private static float hash(int x, int y) {
        int n = (x * 374761393) + (y * 668265263);
        n = (n ^ (n >>> 13)) * 1274126177;
        return ((n ^ (n >>> 16)) & 0xFFFF) / 65535.0f;
    }

    private static float smooth(float edge0, float edge1, float value) {
        float t = Math.max(0.0f, Math.min(1.0f, (value - edge0) / (edge1 - edge0)));
        return t * t * (3.0f - (2.0f * t));
    }
}

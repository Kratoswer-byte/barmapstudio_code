package com.faforever.neroxis.exporter.bar;

import com.faforever.neroxis.map.SCMap;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Biomi ricavati dalle mappe ufficiali di BAR: tavolozza del terreno (misurata confrontando la
 * texture colore con pendenza e quota), luce, atmosfera e acqua (lette da mapinfo.lua).
 * I dati sono generati dall'analisi delle mappe installate; contengono solo colori e numeri,
 * nessuna texture degli autori.
 */
public final class BarBiomes {

    public static final class BarBiome {
        public final String label;
        /** Mappa BAR di origine e suo autore. */
        public final String source;
        public final String author;
        /** Bioma Neroxis usato per generare forma e maschere del terreno. */
        public final String base;
        // tavolozza (alcune voci possono mancare nella mappa di origine)
        public final float[] ground, high, slope, rock, sand, seabed;
        // luce
        public final float[] sunDir, groundAmbient, groundDiffuse, groundSpecular, unitAmbient, unitDiffuse, unitSpecular;
        public final float shadowDensity;
        // atmosfera
        public final float[] fogColor, skyColor, sunColor, cloudColor;
        public final float fogStart, fogEnd;
        // acqua
        public final float[] waterAbsorb, waterBase, waterMin, waterSurface, waterPlane;
        public final float waterSurfaceAlpha;

        public BarBiome(String label, String source, String author, String base,
                 float[][] palette, float[][] light, float shadowDensity,
                 float[][] atmosphere, float fogStart, float fogEnd,
                 float[][] water, float waterSurfaceAlpha) {
            this.label = label;
            this.source = source;
            this.author = author;
            this.base = base;
            this.ground = palette[0];
            this.rock = palette[3] != null ? palette[3] : palette[2];
            this.high = palette[1] != null ? palette[1] : this.ground;
            this.slope = palette[2] != null ? palette[2] : mix(this.ground, this.rock, 0.5f);
            this.sand = palette[4];
            this.seabed = palette[5];
            this.sunDir = light[0];
            this.groundAmbient = light[1];
            this.groundDiffuse = light[2];
            this.groundSpecular = light[3];
            this.unitAmbient = light[4];
            this.unitDiffuse = light[5];
            this.unitSpecular = light[6];
            this.shadowDensity = shadowDensity;
            this.fogColor = atmosphere[0];
            this.skyColor = atmosphere[1];
            this.sunColor = atmosphere[2];
            this.cloudColor = atmosphere[3];
            this.fogStart = fogStart;
            this.fogEnd = fogEnd;
            this.waterAbsorb = water[0];
            this.waterBase = water[1];
            this.waterMin = water[2];
            this.waterSurface = water[3];
            this.waterPlane = water[4];
            this.waterSurfaceAlpha = waterSurfaceAlpha;
        }
    }

    private static float[] f(float r, float g, float b) {
        return new float[]{r, g, b};
    }

    static float[] mix(float[] a, float[] b, float t) {
        return new float[]{a[0] + ((b[0] - a[0]) * t), a[1] + ((b[1] - a[1]) * t), a[2] + ((b[2] - a[2]) * t)};
    }

    private static final List<BarBiome> ALL = List.of(
/*@ENTRIES@*/
    );

    /** Voci per il menu "Bioma / Texture". */
    public static List<String> labels() {
        List<String> labels = new ArrayList<>();
        for (BarBiome biome : ALL) {
            labels.add(biome.label);
        }
        return labels;
    }

    public static BarBiome find(String label) {
        if (label == null) {
            return null;
        }
        for (BarBiome biome : ALL) {
            if (biome.label.equalsIgnoreCase(label)) {
                return biome;
            }
        }
        return null;
    }

    private static WeakReference<SCMap> attachedMap = new WeakReference<>(null);
    private static BarBiome attached;

    /** Lega il bioma BAR alla mappa appena generata (null = bioma Neroxis normale). */
    public static synchronized void attach(SCMap map, BarBiome biome) {
        attachedMap = new WeakReference<>(map);
        attached = biome;
    }

    public static synchronized BarBiome get(SCMap map) {
        return map != null && attachedMap.get() == map ? attached : null;
    }

    /**
     * Anteprima 2D del terreno con i colori del bioma: stesse regole dell'export
     * (pianura, quota, pendii, roccia, riva, fondale) piu' un'ombreggiatura dal sole.
     */
    public static BufferedImage renderPreview(SCMap map, BarBiome biome, int outW, int outH) {
        BufferedImage hm = map.getHeightmap();
        int hw = hm.getWidth();
        int hh = hm.getHeight();
        Raster raster = hm.getRaster();
        float scale = map.getHeightMapScale();
        boolean water = map.getBiome() != null && map.getBiome().waterSettings() != null && map.getBiome().waterSettings().waterPresent();
        float sea = water ? map.getBiome().waterSettings().elevation() : -1000.0f;
        float[] heights = new float[hw * hh];
        float lowest = Float.MAX_VALUE;
        float highest = -Float.MAX_VALUE;
        for (int y = 0; y < hh; y++) {
            for (int x = 0; x < hw; x++) {
                // 1 unita' di quota = 11.2 elmi (16 elmi per cella x 0.7), come nell'export
                float h = ((raster.getSample(x, y, 0) * scale) - (water ? sea : 0.0f)) * 11.2f;
                heights[(y * hw) + x] = h;
                lowest = Math.min(lowest, h);
                highest = Math.max(highest, h);
            }
        }
        if (lowest >= 0.0f) {
            // nessun punto sotto il livello del mare: mappa asciutta, come la tratta l'export
            water = false;
        }
        float landLow = Math.max(lowest, water ? 10.0f : lowest);
        float landRange = Math.max(40.0f, highest - landLow);
        float[] sun = biome.sunDir != null ? biome.sunDir : new float[]{0.5f, 0.7f, -0.5f};
        float sunLen = (float) Math.sqrt((sun[0] * sun[0]) + (sun[1] * sun[1]) + (sun[2] * sun[2]));
        float sx = sun[0] / sunLen;
        float sy = Math.max(0.45f, sun[1] / sunLen);
        float sz = sun[2] / sunLen;
        float[] surface = biome.waterSurface != null ? biome.waterSurface : new float[]{0.3f, 0.45f, 0.6f};
        BufferedImage out = new BufferedImage(outW, outH, BufferedImage.TYPE_INT_ARGB);
        for (int py = 0; py < outH; py++) {
            int y = Math.min(hh - 2, Math.max(1, (py * hh) / outH));
            for (int px = 0; px < outW; px++) {
                int x = Math.min(hw - 2, Math.max(1, (px * hw) / outW));
                float h = heights[(y * hw) + x];
                float dx = (heights[(y * hw) + x + 1] - heights[(y * hw) + x - 1]) / 32.0f;
                float dz = (heights[((y + 1) * hw) + x] - heights[((y - 1) * hw) + x]) / 32.0f;
                float slope = (float) Math.sqrt((dx * dx) + (dz * dz));
                float altitude = clamp((h - landLow) / landRange);
                float[] c = mix(biome.ground, biome.high, smooth(0.45f, 0.9f, altitude));
                c = mix(c, biome.slope, smooth(0.08f, 0.26f, slope));
                c = mix(c, biome.rock, smooth(0.3f, 0.6f, slope));
                if (water) {
                    if (biome.sand != null) {
                        c = mix(c, biome.sand, (1.0f - smooth(4.0f, 22.0f, Math.abs(h - 4.0f))) * (1.0f - smooth(0.15f, 0.4f, slope)));
                    }
                    if (h < 0.0f) {
                        float depth = smooth(0.0f, 60.0f, -h);
                        if (biome.seabed != null) {
                            c = mix(c, biome.seabed, depth);
                        }
                        c = mix(c, surface, 0.25f + (0.45f * depth));
                    }
                }
                float len = (float) Math.sqrt((dx * dx) + (dz * dz) + 1.0f);
                float light = Math.max(0.0f, ((-dx * sx) + sy + (-dz * sz)) / len);
                float shade = 1.0f + ((light - sy) * 0.9f);
                int r = Math.round(clamp(c[0] * shade) * 255.0f);
                int g = Math.round(clamp(c[1] * shade) * 255.0f);
                int b = Math.round(clamp(c[2] * shade) * 255.0f);
                out.setRGB(px, py, 0xFF000000 | (r << 16) | (g << 8) | b);
            }
        }
        return out;
    }

    private static float clamp(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    private static float smooth(float edge0, float edge1, float value) {
        float t = clamp((value - edge0) / (edge1 - edge0));
        return t * t * (3.0f - (2.0f * t));
    }

    private BarBiomes() {
    }
}

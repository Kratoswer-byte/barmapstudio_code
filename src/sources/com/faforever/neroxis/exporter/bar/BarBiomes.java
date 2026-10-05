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
        new BarBiome("BAR: Incandescence", "Incandescence Remake", "IceXuick, original from qray, TheMooseIsLoose", "DESERT",
            new float[][]{f(0.693f, 0.570f, 0.149f), f(0.587f, 0.505f, 0.448f), f(0.487f, 0.407f, 0.309f), f(0.308f, 0.265f, 0.232f), null, null},
            new float[][]{f(0.318f, 0.607f, -0.624f), f(0.550f, 0.450f, 0.450f), f(0.970f, 0.950f, 0.920f), null, f(0.660f, 0.660f, 0.700f), f(0.990f, 0.995f, 0.950f), f(0.800f, 0.600f, 0.600f)}, 1.100f,
            new float[][]{f(0.430f, 0.290f, 0.180f), f(0.429f, 0.580f, 0.640f), f(1.000f, 0.920f, 0.780f), f(0.900f, 0.900f, 0.900f)}, 0.550f, 0.880f,
            new float[][]{f(0.600f, 0.300f, 0.300f), f(1.000f, 0.700f, 0.100f), f(0.300f, 0.200f, 0.000f), f(1.000f, 0.800f, 0.100f), f(1.000f, 0.800f, 0.100f)}, 0.300f),
        new BarBiome("BAR: Hyperion Shale", "Hyperion Shale", "Moose", "FRITHEN",
            new float[][]{f(0.483f, 0.501f, 0.850f), f(0.435f, 0.518f, 0.774f), f(0.542f, 0.506f, 0.804f), f(0.473f, 0.433f, 0.679f), null, f(0.362f, 0.511f, 0.703f)},
            new float[][]{f(0.500f, 1.000f, -0.700f), f(0.560f, 0.550f, 0.550f), f(0.600f, 0.600f, 0.650f), f(0.500f, 0.500f, 0.500f), f(0.600f, 0.600f, 0.600f), f(1.000f, 0.940f, 0.920f), f(0.900f, 0.830f, 0.820f)}, 0.750f,
            new float[][]{f(0.400f, 0.240f, 0.420f), f(0.429f, 0.580f, 0.640f), f(1.000f, 0.920f, 0.780f), f(0.900f, 0.900f, 0.900f)}, 0.480f, 0.890f,
            new float[][]{f(0.060f, 0.004f, 0.002f), f(0.600f, 0.820f, 0.850f), f(0.100f, 0.300f, 0.250f), f(0.220f, 0.170f, 0.410f), f(0.400f, 0.240f, 0.420f)}, 0.100f),
        new BarBiome("BAR: Canis River", "Canis River v1.4", "Phalange", "DESERT",
            new float[][]{f(0.915f, 0.812f, 0.636f), f(0.658f, 0.535f, 0.393f), f(0.803f, 0.711f, 0.550f), f(0.444f, 0.376f, 0.286f), null, f(0.560f, 0.485f, 0.351f)},
            new float[][]{f(0.330f, 0.670f, -0.900f), f(0.500f, 0.500f, 0.500f), f(0.500f, 0.500f, 0.500f), f(0.600f, 0.600f, 0.600f), f(0.600f, 0.600f, 0.600f), f(1.000f, 0.940f, 0.920f), f(0.900f, 0.830f, 0.820f)}, 0.790f,
            new float[][]{f(0.750f, 0.750f, 0.800f), f(0.429f, 0.580f, 0.640f), f(0.900f, 0.800f, 0.800f), f(0.900f, 0.900f, 0.900f)}, 0.800f, 1.500f,
            new float[][]{f(0.050f, 0.050f, 0.010f), f(0.300f, 0.600f, 0.600f), f(0.000f, 0.300f, 0.300f), f(0.670f, 0.800f, 1.000f), null}, 0.200f),
        new BarBiome("BAR: Glacier Pass", "Glacier Pass", "IceXuick", "DESERT",
            new float[][]{f(0.647f, 0.647f, 0.647f), f(0.735f, 0.734f, 0.734f), f(0.508f, 0.502f, 0.496f), f(0.172f, 0.146f, 0.126f), null, null},
            new float[][]{f(-1.300f, 0.970f, -0.620f), f(0.468f, 0.470f, 0.550f), f(1.000f, 1.000f, 1.000f), f(0.632f, 0.677f, 0.800f), f(0.590f, 0.569f, 0.549f), f(0.985f, 0.920f, 1.000f), f(0.550f, 0.600f, 0.850f)}, 0.900f,
            new float[][]{f(0.853f, 0.884f, 0.980f), f(0.429f, 0.580f, 0.640f), f(0.907f, 0.840f, 1.000f), f(0.900f, 0.900f, 0.900f)}, 0.420f, 0.790f,
            new float[][]{f(0.004f, 0.004f, 0.002f), f(0.400f, 0.600f, 0.800f), f(0.100f, 0.100f, 0.230f), f(0.650f, 0.700f, 0.750f), null}, 0.100f),
        new BarBiome("BAR: All That Glitters", "All That Glitters v2.2.3", "Nikuksis", "DESERT",
            new float[][]{f(0.637f, 0.470f, 0.439f), f(0.613f, 0.647f, 0.586f), f(0.637f, 0.434f, 0.372f), f(0.594f, 0.412f, 0.359f), null, null},
            new float[][]{f(0.500f, 0.400f, -0.500f), f(0.300f, 0.300f, 0.300f), f(1.000f, 1.000f, 1.000f), f(0.950f, 0.950f, 0.950f), f(0.570f, 0.569f, 0.569f), f(1.000f, 0.985f, 0.920f), f(0.800f, 0.600f, 0.600f)}, 0.700f,
            new float[][]{f(0.650f, 0.600f, 0.500f), f(0.700f, 0.600f, 0.400f), f(0.600f, 0.300f, 0.200f), f(0.700f, 0.500f, 0.400f)}, 0.500f, 1.000f,
            new float[][]{f(0.007f, 0.006f, 0.002f), f(0.200f, 0.380f, 0.500f), f(0.002f, 0.004f, 0.005f), f(0.250f, 0.400f, 0.450f), f(0.130f, 0.220f, 0.250f)}, 0.600f),
        new BarBiome("BAR: Evad River", "Evad River Confluence_V2", "NOiZE", "FRITHEN",
            new float[][]{f(0.397f, 0.484f, 0.041f), f(0.491f, 0.556f, 0.082f), f(0.255f, 0.276f, 0.049f), f(0.305f, 0.334f, 0.032f), f(0.236f, 0.385f, 0.010f), f(0.380f, 0.332f, 0.241f)},
            new float[][]{f(0.800f, 0.800f, -0.700f), f(0.400f, 0.400f, 0.400f), f(1.000f, 1.000f, 1.000f), null, f(0.560f, 0.560f, 0.600f), f(0.950f, 0.955f, 0.900f), f(0.800f, 0.600f, 0.600f)}, 0.750f,
            new float[][]{f(0.800f, 0.800f, 0.500f), f(0.429f, 0.580f, 0.940f), f(1.000f, 0.920f, 0.780f), f(0.900f, 0.900f, 0.900f)}, 0.800f, 1.000f,
            new float[][]{f(0.050f, 0.005f, 0.001f), f(0.300f, 0.500f, 0.500f), f(0.000f, 0.300f, 0.300f), f(0.670f, 0.800f, 1.000f), f(0.300f, 0.500f, 0.500f)}, 0.200f),
        new BarBiome("BAR: Melting Glacier", "Melting Glacier v1.1.2", "Nikuksis", "FRITHEN",
            new float[][]{f(0.691f, 0.728f, 0.817f), f(0.740f, 0.754f, 0.840f), f(0.578f, 0.679f, 0.772f), f(0.515f, 0.620f, 0.697f), f(0.430f, 0.705f, 0.818f), f(0.156f, 0.277f, 0.355f)},
            new float[][]{f(-0.640f, 0.660f, -0.570f), f(0.560f, 0.550f, 0.550f), f(0.600f, 0.600f, 0.650f), f(0.600f, 0.500f, 0.500f), f(0.570f, 0.569f, 0.569f), f(1.000f, 0.985f, 0.920f), f(0.800f, 0.600f, 0.600f)}, 0.700f,
            new float[][]{f(0.630f, 0.660f, 0.690f), f(0.429f, 0.580f, 0.640f), f(1.000f, 0.920f, 0.780f), f(0.900f, 0.900f, 0.900f)}, 0.500f, 1.000f,
            new float[][]{f(0.011f, 0.011f, 0.015f), f(0.500f, 0.680f, 0.680f), f(0.022f, 0.004f, 0.035f), f(0.500f, 0.600f, 0.650f), f(0.130f, 0.220f, 0.250f)}, 0.200f),
        new BarBiome("BAR: Red River", "Red River Estuary v1.1", "Nikuksis", "FRITHEN",
            new float[][]{f(0.424f, 0.245f, 0.194f), f(0.422f, 0.238f, 0.181f), f(0.368f, 0.190f, 0.136f), f(0.337f, 0.166f, 0.117f), f(0.383f, 0.204f, 0.152f), f(0.323f, 0.193f, 0.155f)},
            new float[][]{f(-0.640f, 0.660f, -0.570f), f(0.650f, 0.650f, 0.650f), f(1.000f, 1.000f, 1.000f), f(0.600f, 0.500f, 0.500f), f(0.450f, 0.450f, 0.450f), f(0.750f, 0.750f, 0.700f), f(0.800f, 0.600f, 0.600f)}, 0.700f,
            new float[][]{f(0.540f, 0.210f, 0.140f), f(0.429f, 0.580f, 0.640f), f(1.000f, 0.920f, 0.780f), f(0.900f, 0.900f, 0.900f)}, 0.250f, 0.850f,
            new float[][]{f(0.008f, 0.120f, 0.016f), f(0.660f, 0.450f, 0.920f), f(0.012f, 0.010f, 0.031f), f(0.220f, 0.170f, 0.410f), null}, 0.100f),
        new BarBiome("BAR: High Noon", "High Noon Remake", "Phalange", "FRITHEN",
            new float[][]{f(0.870f, 0.620f, 0.417f), f(0.843f, 0.543f, 0.386f), f(0.814f, 0.592f, 0.425f), f(0.457f, 0.352f, 0.303f), f(0.978f, 0.768f, 0.590f), f(0.871f, 0.685f, 0.538f)},
            new float[][]{f(0.480f, 1.180f, -0.700f), f(0.500f, 0.500f, 0.500f), f(0.850f, 0.850f, 0.850f), f(0.600f, 0.600f, 0.600f), f(0.600f, 0.600f, 0.600f), f(1.000f, 0.940f, 0.920f), f(0.900f, 0.830f, 0.820f)}, 0.790f,
            new float[][]{f(0.370f, 0.180f, 0.010f), f(0.500f, 0.500f, 0.500f), f(1.000f, 1.000f, 1.000f), f(0.900f, 0.900f, 0.900f)}, 0.800f, 1.200f,
            new float[][]{f(0.060f, 0.004f, 0.002f), f(0.600f, 0.820f, 0.850f), f(0.100f, 0.300f, 0.350f), f(0.600f, 0.800f, 0.900f), f(0.600f, 0.800f, 0.850f)}, 0.080f),
        new BarBiome("BAR: Faster Than Light", "Faster Than Light", "ShaunJS", "FRITHEN",
            new float[][]{f(0.176f, 0.214f, 0.290f), f(0.353f, 0.365f, 0.387f), f(0.246f, 0.257f, 0.301f), f(0.146f, 0.155f, 0.190f), null, f(0.060f, 0.047f, 0.109f)},
            new float[][]{f(0.800f, 1.000f, -0.700f), f(0.600f, 0.600f, 0.600f), f(0.900f, 0.900f, 0.900f), f(0.096f, 0.105f, 0.096f), f(0.750f, 0.750f, 0.750f), f(1.000f, 1.000f, 1.000f), f(0.400f, 0.400f, 0.500f)}, 0.750f,
            new float[][]{f(0.100f, 0.100f, 0.400f), f(0.429f, 0.580f, 0.640f), f(0.780f, 0.920f, 1.000f), f(0.900f, 0.900f, 0.900f)}, 0.800f, 1.000f,
            new float[][]{f(0.080f, 0.010f, 0.003f), f(0.200f, 1.200f, 0.700f), f(0.000f, 0.200f, 0.200f), f(0.670f, 0.990f, 1.000f), null}, 0.100f),
        new BarBiome("BAR: DSD", "DSD 8 Way", "IceXuick", "FRITHEN",
            new float[][]{f(0.468f, 0.440f, 0.246f), f(0.516f, 0.460f, 0.367f), f(0.556f, 0.516f, 0.383f), f(0.463f, 0.434f, 0.372f), null, f(0.671f, 0.617f, 0.499f)},
            new float[][]{f(0.403f, 0.506f, -0.791f), f(0.570f, 0.500f, 0.500f), f(0.990f, 0.940f, 0.940f), null, f(0.660f, 0.660f, 0.700f), f(0.990f, 0.995f, 0.950f), f(0.800f, 0.600f, 0.600f)}, 0.900f,
            new float[][]{f(0.800f, 0.620f, 0.450f), f(0.429f, 0.580f, 0.640f), f(1.000f, 0.920f, 0.780f), f(0.900f, 0.900f, 0.900f)}, 0.590f, 0.950f,
            new float[][]{f(0.007f, 0.006f, 0.002f), f(0.550f, 0.400f, 0.150f), f(0.100f, 0.070f, 0.010f), f(0.550f, 0.400f, 0.150f), null}, 0.300f),
        new BarBiome("BAR: Ascendancy", "Ascendancy v2.2", "Nikuksis", "FRITHEN",
            new float[][]{f(0.453f, 0.476f, 0.585f), f(0.768f, 0.790f, 0.901f), f(0.500f, 0.519f, 0.621f), f(0.419f, 0.431f, 0.500f), f(0.196f, 0.221f, 0.324f), f(0.211f, 0.236f, 0.342f)},
            new float[][]{f(-0.640f, 0.850f, -0.570f), f(0.560f, 0.550f, 0.550f), f(0.600f, 0.600f, 0.650f), f(0.500f, 0.500f, 0.500f), f(0.570f, 0.569f, 0.569f), f(1.000f, 0.985f, 0.920f), f(0.800f, 0.600f, 0.600f)}, 0.700f,
            new float[][]{f(0.630f, 0.660f, 0.690f), f(0.429f, 0.580f, 0.640f), f(1.000f, 0.920f, 0.780f), f(0.900f, 0.900f, 0.900f)}, 0.500f, 1.000f,
            new float[][]{f(0.011f, 0.011f, 0.015f), f(0.500f, 0.680f, 0.680f), f(0.022f, 0.004f, 0.035f), f(0.500f, 0.600f, 0.650f), f(0.130f, 0.220f, 0.250f)}, 0.200f),
        new BarBiome("BAR: Esker Creek", "Esker Creek", "RebelNode", "DESERT",
            new float[][]{f(0.237f, 0.402f, 0.213f), f(0.293f, 0.484f, 0.257f), f(0.305f, 0.450f, 0.301f), f(0.309f, 0.337f, 0.372f), f(0.766f, 0.720f, 0.560f), null},
            new float[][]{f(0.318f, 0.607f, -0.624f), f(0.550f, 0.510f, 0.510f), f(0.650f, 0.650f, 0.650f), f(0.500f, 0.500f, 0.500f), f(0.580f, 0.560f, 0.560f), f(1.000f, 0.965f, 0.940f), f(0.800f, 0.600f, 0.600f)}, 0.900f,
            new float[][]{f(0.150f, 0.132f, 0.126f), f(0.429f, 0.580f, 0.640f), f(1.000f, 0.900f, 0.810f), f(0.900f, 0.900f, 0.900f)}, 1.000f, 1.200f,
            new float[][]{f(0.050f, 0.005f, 0.001f), f(0.300f, 0.500f, 0.500f), f(0.000f, 0.300f, 0.300f), f(0.670f, 0.800f, 1.000f), null}, 0.100f),
        new BarBiome("BAR: Sphagnum Bog", "Sphagnum Bog v1.2.1", "Nikuksis", "FRITHEN",
            new float[][]{f(0.241f, 0.323f, 0.036f), f(0.255f, 0.318f, 0.039f), f(0.217f, 0.272f, 0.065f), f(0.176f, 0.208f, 0.077f), f(0.258f, 0.308f, 0.077f), null},
            new float[][]{f(-0.640f, 0.660f, -0.570f), f(0.560f, 0.550f, 0.550f), f(1.000f, 1.000f, 1.000f), f(0.600f, 0.500f, 0.500f), f(0.570f, 0.569f, 0.569f), f(1.000f, 0.985f, 0.920f), f(0.800f, 0.600f, 0.600f)}, 0.700f,
            new float[][]{f(0.630f, 0.660f, 0.690f), f(0.429f, 0.580f, 0.640f), f(1.000f, 0.920f, 0.780f), f(0.900f, 0.900f, 0.900f)}, 0.500f, 1.000f,
            new float[][]{f(0.090f, 0.010f, 0.090f), f(0.350f, 0.850f, 0.450f), f(0.015f, 0.045f, 0.022f), f(0.015f, 0.045f, 0.022f), null}, 0.100f),
        new BarBiome("BAR: Ring Atoll", "Ring Atoll Remake v2.0", "Nikuksis", "FRITHEN",
            new float[][]{f(0.764f, 0.678f, 0.615f), f(0.769f, 0.685f, 0.622f), f(0.707f, 0.595f, 0.524f), f(0.633f, 0.497f, 0.422f), f(0.696f, 0.522f, 0.480f), f(0.579f, 0.490f, 0.430f)},
            new float[][]{f(0.500f, 0.400f, -0.500f), f(0.550f, 0.550f, 0.550f), f(0.500f, 0.500f, 0.500f), f(0.750f, 0.750f, 0.750f), f(0.570f, 0.569f, 0.569f), f(1.000f, 0.985f, 0.920f), f(0.800f, 0.600f, 0.600f)}, 0.700f,
            new float[][]{f(0.650f, 0.600f, 0.500f), f(0.700f, 0.600f, 0.400f), f(0.600f, 0.300f, 0.200f), f(0.700f, 0.500f, 0.400f)}, 0.500f, 1.000f,
            new float[][]{f(0.600f, 0.040f, 0.020f), f(0.400f, 0.850f, 0.950f), f(0.050f, 0.150f, 0.250f), f(0.100f, 0.300f, 0.400f), f(0.000f, 0.150f, 0.150f)}, 0.200f),
        new BarBiome("BAR: Titan", "Titan", "SirArtturi, DNTS Update by The_Yak", "DESERT",
            new float[][]{f(0.679f, 0.430f, 0.322f), f(0.718f, 0.588f, 0.487f), f(0.546f, 0.363f, 0.273f), f(0.313f, 0.194f, 0.131f), f(0.590f, 0.432f, 0.335f), f(0.483f, 0.613f, 0.404f)},
            new float[][]{f(0.400f, 0.195f, -0.200f), f(0.800f, 0.700f, 0.700f), f(1.100f, 1.000f, 1.000f), null, f(0.800f, 0.600f, 0.600f), f(1.000f, 0.800f, 0.800f), null}, 0.900f,
            new float[][]{f(0.800f, 0.800f, 0.500f), f(0.080f, 0.130f, 0.700f), f(1.000f, 0.750f, 0.750f), f(0.900f, 0.900f, 0.900f)}, 1.000f, 1.200f,
            new float[][]{f(1.000f, 0.500f, 0.000f), f(0.400f, 0.780f, 0.300f), f(0.300f, 0.560f, 0.430f), f(0.450f, 0.800f, 0.450f), f(0.020f, 0.050f, 0.010f)}, Float.NaN),
        new BarBiome("BAR: Lavender Bender", "Lavender Bender", "Johannes", "FRITHEN",
            new float[][]{f(0.618f, 0.543f, 0.669f), f(0.575f, 0.495f, 0.521f), f(0.550f, 0.449f, 0.515f), f(0.414f, 0.338f, 0.389f), f(0.647f, 0.422f, 0.435f), f(0.444f, 0.342f, 0.376f)},
            new float[][]{f(-0.300f, 0.450f, -0.500f), f(0.700f, 0.700f, 0.700f), f(0.680f, 0.680f, 0.680f), null, f(0.550f, 0.550f, 0.700f), f(0.660f, 0.660f, 0.750f), null}, 0.750f,
            new float[][]{f(0.800f, 0.600f, 0.500f), f(0.200f, 0.250f, 0.050f), f(1.000f, 0.700f, 0.700f), f(0.950f, 0.850f, 0.750f)}, 0.800f, 1.000f,
            new float[][]{f(0.004f, 0.003f, 0.002f), f(0.670f, 0.730f, 0.950f), f(0.100f, 0.100f, 0.200f), f(0.850f, 0.750f, 1.000f), null}, 0.100f),
        new BarBiome("BAR: Cape Violet", "Cape_Violet_V1", "[teh]Beherith (mysterme@gmail.com)", "FRITHEN",
            new float[][]{f(0.350f, 0.235f, 0.486f), f(0.552f, 0.531f, 0.602f), f(0.445f, 0.358f, 0.564f), f(0.332f, 0.300f, 0.379f), f(0.363f, 0.258f, 0.416f), f(0.312f, 0.255f, 0.371f)},
            new float[][]{f(0.800f, 1.000f, -0.700f), f(0.500f, 0.500f, 0.500f), f(0.990f, 0.990f, 0.950f), null, f(0.560f, 0.560f, 0.600f), f(0.950f, 0.955f, 0.900f), f(0.800f, 0.600f, 0.600f)}, 0.750f,
            new float[][]{f(0.800f, 0.800f, 0.500f), f(0.429f, 0.580f, 0.640f), f(1.000f, 0.920f, 0.780f), f(0.900f, 0.900f, 0.900f)}, 0.800f, 1.000f,
            new float[][]{f(0.080f, 0.010f, 0.003f), f(0.200f, 1.200f, 0.700f), f(0.000f, 0.200f, 0.200f), f(0.670f, 0.990f, 1.000f), f(0.000f, 0.150f, 0.150f)}, 0.100f),
        new BarBiome("BAR: Aurelia", "Aurelia", "Johannes", "FRITHEN",
            new float[][]{f(0.633f, 0.318f, 0.215f), f(0.519f, 0.425f, 0.414f), f(0.550f, 0.350f, 0.323f), f(0.405f, 0.289f, 0.286f), f(0.567f, 0.332f, 0.286f), f(0.543f, 0.395f, 0.391f)},
            new float[][]{f(-0.300f, 0.350f, -0.500f), f(0.710f, 0.710f, 0.710f), f(0.490f, 0.590f, 0.590f), null, f(0.550f, 0.550f, 0.700f), f(0.660f, 0.660f, 0.710f), null}, 0.770f,
            new float[][]{f(0.800f, 0.600f, 0.500f), f(0.200f, 0.250f, 0.050f), f(1.000f, 0.700f, 0.700f), f(0.950f, 0.850f, 0.750f)}, 0.800f, 1.000f,
            new float[][]{f(0.004f, 0.003f, 0.002f), f(0.670f, 0.730f, 0.950f), f(0.100f, 0.100f, 0.200f), f(0.850f, 0.750f, 1.000f), null}, 0.100f),
        new BarBiome("BAR: Baryon Tar Lake", "Baryon Tar Lake v1.1", "Nikuksis", "DESERT",
            new float[][]{f(0.230f, 0.154f, 0.059f), f(0.289f, 0.215f, 0.058f), f(0.437f, 0.241f, 0.092f), f(0.313f, 0.169f, 0.080f), null, null},
            new float[][]{f(0.700f, 0.600f, -0.500f), f(0.750f, 0.750f, 0.750f), f(1.000f, 1.000f, 1.000f), f(0.700f, 0.700f, 0.700f), f(0.700f, 0.700f, 0.700f), f(1.000f, 0.985f, 0.920f), f(0.800f, 0.600f, 0.600f)}, 0.700f,
            new float[][]{f(0.630f, 0.660f, 0.690f), f(0.429f, 0.580f, 0.640f), f(1.000f, 0.920f, 0.780f), f(0.900f, 0.900f, 0.900f)}, 0.500f, 1.000f,
            new float[][]{f(0.001f, 0.001f, 0.001f), f(0.100f, 0.100f, 0.100f), f(0.000f, 0.000f, 0.000f), f(0.010f, 0.010f, 0.010f), f(0.010f, 0.010f, 0.010f)}, 0.300f)
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

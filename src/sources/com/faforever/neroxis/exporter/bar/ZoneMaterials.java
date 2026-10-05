package com.faforever.neroxis.exporter.bar;

import com.faforever.neroxis.generator.bar.I18n;
import com.faforever.neroxis.map.SCMap;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.imageio.ImageIO;

/**
 * Materiali delle 4 zone del pannello Texture, passati all'export BAR.
 * L'export compone il colore a piena risoluzione (1 px per elmo) dalle maschere e dai tile,
 * scrive le maschere in _splats.dds e un DNTS per zona, e usa lucentezza e rilievo del materiale.
 */
public final class ZoneMaterials {

    /** Specifiche dei materiali esterni, mostrate nel pannello Texture e nella finestra Specifiche. */
    public static final String SPEC_TEXT =
        I18n.t("zm.1") +
        "\n" +
        I18n.t("zm.2") +
        I18n.t("zm.3") +
        "\n" +
        I18n.t("zm.4") +
        I18n.t("zm.5") +
        I18n.t("zm.6") +
        I18n.t("zm.7") +
        "\n" +
        I18n.t("zm.8") +
        I18n.t("zm.9") +
        I18n.t("zm.10") +
        I18n.t("zm.11") +
        I18n.t("zm.12") +
        "\n" +
        I18n.t("zm.13") +
        I18n.t("zm.14") +
        I18n.t("zm.15") +
        I18n.t("zm.16") +
        I18n.t("zm.17") +
        I18n.t("zm.18") +
        I18n.t("zm.19") +
        I18n.t("zm.20") +
        I18n.t("zm.21") +
        "\n" +
        I18n.t("zm.22") +
        I18n.t("zm.23") +
        I18n.t("zm.24") +
        I18n.t("zm.25") +
        "                      (0 = nessuno, max 64)\n" +
        I18n.t("zm.26") +
        "\n" +
        I18n.t("zm.27") +
        I18n.t("zm.28") +
        I18n.t("zm.29") +
        I18n.t("zm.30") +
        I18n.t("zm.31");

    /** Un materiale: tile ripetibili + parametri. */
    public static final class Material {
        public final String name;
        public final BufferedImage diffuse;
        public final BufferedImage normal;
        public final BufferedImage height;
        public float tileElmos;
        public float gloss;
        public float reliefElmos;
        /** Moltiplicatore della pendenza della normal map (1 = come nel file). */
        public float normalStrength = 2.0f;

        public Material(String name, BufferedImage diffuse, BufferedImage normal, BufferedImage height,
                        float tileElmos, float gloss, float reliefElmos) {
            this.name = name;
            this.diffuse = diffuse;
            this.normal = normal;
            this.height = height;
            this.tileElmos = clamp(tileElmos, 16.0f, 2048.0f);
            this.gloss = clamp(gloss, 0.0f, 1.0f);
            this.reliefElmos = clamp(reliefElmos, 0.0f, 64.0f);
        }
    }

    /** Esito del caricamento: materiale + avvisi sulle specifiche non rispettate. */
    public static final class LoadResult {
        public final Material material;
        public final List<String> warnings;

        LoadResult(Material material, List<String> warnings) {
            this.material = material;
            this.warnings = warnings;
        }
    }

    private static final String[] COLOR_SUFFIXES = {"_Color", "_BaseColor", "_Albedo", "_Diffuse", "_diff", "_col"};
    private static final String[] NORMAL_SUFFIXES = {"_normal", "_NormalGL", "_nor_gl", "_n"};
    private static final String[] NORMAL_DX_SUFFIXES = {"_NormalDX", "_nor_dx"};
    private static final String[] HEIGHT_SUFFIXES = {"_height", "_Displacement", "_disp", "_h"};
    private static final String[] ROUGHNESS_SUFFIXES = {"_Roughness", "_rough"};
    private static final String[] IMAGE_EXTENSIONS = {"png", "jpg", "jpeg", "bmp"};

    /**
     * Carica un materiale esterno a partire dal file del colore (vedi SPEC_TEXT).
     * Riconosce anche i pacchetti PBR standard (ambientCG, Poly Haven): _Color, _NormalGL,
     * _NormalDX, _Displacement, _Roughness.
     */
    public static LoadResult load(File diffuseFile) throws Exception {
        List<String> warnings = new ArrayList<>();
        File dir = diffuseFile.getAbsoluteFile().getParentFile();
        String base = stripExtension(diffuseFile.getName());
        boolean hasColorSuffix = false;
        for (String suffix : COLOR_SUFFIXES) {
            if (base.toLowerCase(Locale.ROOT).endsWith(suffix.toLowerCase(Locale.ROOT))) {
                base = base.substring(0, base.length() - suffix.length());
                hasColorSuffix = true;
                break;
            }
        }
        if (!hasColorSuffix) {
            // file scelto senza suffisso (es. l'anteprima del pacchetto): se esiste <base>*_Color uso quello
            File packColor = findPackColor(dir, base);
            if (packColor != null) {
                warnings.add(diffuseFile.getName() + I18n.t("zm.32") + packColor.getName() + I18n.t("zm.33"));
                diffuseFile = packColor;
                base = stripExtension(packColor.getName());
                for (String suffix : COLOR_SUFFIXES) {
                    if (base.toLowerCase(Locale.ROOT).endsWith(suffix.toLowerCase(Locale.ROOT))) {
                        base = base.substring(0, base.length() - suffix.length());
                        break;
                    }
                }
            }
        }
        BufferedImage diffuse = ImageIO.read(diffuseFile);
        if (diffuse == null) {
            throw new IllegalArgumentException("Formato immagine non supportato: " + diffuseFile.getName());
        }
        checkTile(diffuse, diffuseFile.getName(), warnings);
        BufferedImage normal = readSibling(dir, base, NORMAL_SUFFIXES, warnings);
        if (normal == null) {
            BufferedImage dx = readSibling(dir, base, NORMAL_DX_SUFFIXES, warnings);
            if (dx != null) {
                // DirectX ha il verde verso il basso: lo inverto per la convenzione OpenGL
                normal = new BufferedImage(dx.getWidth(), dx.getHeight(), BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < dx.getHeight(); y++) {
                    for (int x = 0; x < dx.getWidth(); x++) {
                        int rgb = dx.getRGB(x, y);
                        normal.setRGB(x, y, (rgb & 0xFFFF00FF) | ((255 - ((rgb >> 8) & 255)) << 8));
                    }
                }
            }
        }
        BufferedImage height = readSibling(dir, base, HEIGHT_SUFFIXES, warnings);
        float tile = 128.0f;
        float gloss = 0.05f;
        float relief = 0.0f;
        float normalStrength = 2.0f;
        BufferedImage roughness = readSibling(dir, base, ROUGHNESS_SUFFIXES, warnings);
        if (roughness != null) {
            // lucentezza stimata dalla mappa di ruvidita' del pacchetto (ruvido = opaco)
            int[] px = pixels(resize(roughness, 64, 64));
            float mean = 0.0f;
            for (int p : px) {
                mean += luminance(p);
            }
            mean /= px.length;
            gloss = clamp((1.0f - mean) * (1.0f - mean) * 0.5f, 0.0f, 1.0f);
        }
        File props = new File(dir, base + ".properties");
        if (props.isFile()) {
            Properties p = new Properties();
            try (InputStream in = new FileInputStream(props)) {
                p.load(in);
            }
            tile = number(p, "tile_elmos", tile, warnings);
            gloss = number(p, "gloss", gloss, warnings);
            relief = number(p, "relief_elmos", relief, warnings);
            normalStrength = number(p, "normal_strength", normalStrength, warnings);
        }
        if (relief > 0.0f && height == null) {
            warnings.add(I18n.t("zm.34") + base + I18n.t("zm.35"));
        }
        if (relief > 0.0f && tile < 64.0f) {
            warnings.add(I18n.t("zm.36"));
        }
        Material material = new Material(base, diffuse, normal, height, tile, gloss, relief);
        material.normalStrength = clamp(normalStrength, 0.0f, 8.0f);
        return new LoadResult(material, warnings);
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(0, dot) : fileName;
    }

    private static File findPackColor(File dir, String base) {
        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }
        String prefix = base.toLowerCase(Locale.ROOT);
        for (File f : files) {
            String name = stripExtension(f.getName()).toLowerCase(Locale.ROOT);
            if (!f.isFile() || !name.startsWith(prefix) || name.equals(prefix)) {
                continue;
            }
            for (String suffix : COLOR_SUFFIXES) {
                if (name.endsWith(suffix.toLowerCase(Locale.ROOT))) {
                    return f;
                }
            }
        }
        return null;
    }

    private static BufferedImage readSibling(File dir, String base, String[] suffixes, List<String> warnings) {
        for (String suffix : suffixes) {
            for (String ext : IMAGE_EXTENSIONS) {
                File f = new File(dir, base + suffix + "." + ext);
                if (!f.isFile()) {
                    continue;
                }
                try {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) {
                        checkTile(img, f.getName(), warnings);
                        return img;
                    }
                } catch (Exception ex) {
                    warnings.add(I18n.t("zm.37") + f.getName() + ": " + ex.getMessage());
                }
            }
        }
        return null;
    }

    private static void checkTile(BufferedImage img, String name, List<String> warnings) {
        int w = img.getWidth();
        int h = img.getHeight();
        if (w != h) {
            warnings.add(name + I18n.t("zm.38") + w + "x" + h + I18n.t("zm.39"));
        }
        if (Integer.bitCount(w) != 1 || Integer.bitCount(h) != 1) {
            warnings.add(name + I18n.t("zm.40") + w + "x" + h + ").");
        }
        if (Math.max(w, h) > 2048) {
            warnings.add(name + I18n.t("zm.41"));
        }
    }

    private static float number(Properties p, String key, float fallback, List<String> warnings) {
        String value = p.getProperty(key);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Float.parseFloat(value.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            warnings.add("Parametro " + key + I18n.t("zm.42") + value + "\".");
            return fallback;
        }
    }

    // ---------------------------------------------------------------------------------------
    // Strato pronto per l'export: maschera della zona + tile ricampionati alla scala di gioco
    // ---------------------------------------------------------------------------------------

    static final class Layer {
        final Material material;
        final float[] mask;
        final int maskW;
        final int maskH;
        private final int[] color;
        private final int colorSize;
        private final float[] relief;
        private final int reliefSize;

        Layer(Material material, float[] mask, int maskW, int maskH) {
            this.material = material;
            this.mask = mask;
            this.maskW = maskW;
            this.maskH = maskH;
            // il colore in gioco ha 1 px per elmo: riduco il tile a quella densita' (niente moire')
            int size = Math.min(powerOfTwoAtMost(Math.round(material.tileElmos)), powerOfTwoAtMost(material.diffuse.getWidth()));
            this.colorSize = Math.max(4, size);
            this.color = pixels(resize(material.diffuse, this.colorSize, this.colorSize));
            if (material.height != null) {
                // i colori PBR sono piatti (la trama sta in normal e rilievo): scurisco le cavita' e
                // schiarisco le creste dal rilievo, cosi' la trama si legge anche nel colore della mappa
                int[] hp = pixels(resize(material.height, this.colorSize, this.colorSize));
                float mean = 0.0f;
                for (int h : hp) {
                    mean += luminance(h);
                }
                mean /= hp.length;
                for (int i = 0; i < this.color.length; i++) {
                    float f = clamp(1.0f + ((luminance(hp[i]) - mean) * 1.8f), 0.5f, 1.4f);
                    int c = this.color[i];
                    int brightest = Math.max((c >> 16) & 255, Math.max((c >> 8) & 255, c & 255));
                    if (brightest * f > 255.0f) {
                        // un canale saturato sposterebbe la tinta (arancione -> giallo)
                        f = 255.0f / brightest;
                    }
                    this.color[i] = (Math.min(255, Math.round(((c >> 16) & 255) * f)) << 16) | (Math.min(255, Math.round(((c >> 8) & 255) * f)) << 8) | Math.min(255, Math.round((c & 255) * f));
                }
            }
            if (material.height != null && material.reliefElmos > 0.0f && material.tileElmos >= 64.0f) {
                // il terreno ha 1 punto ogni 8 elmi
                this.reliefSize = Math.max(4, powerOfTwoAtMost(Math.round(material.tileElmos / 8.0f)));
                int[] px = pixels(resize(material.height, this.reliefSize, this.reliefSize));
                this.relief = new float[px.length];
                float lo = Float.MAX_VALUE;
                float hi = -Float.MAX_VALUE;
                for (int i = 0; i < px.length; i++) {
                    this.relief[i] = luminance(px[i]);
                    lo = Math.min(lo, this.relief[i]);
                    hi = Math.max(hi, this.relief[i]);
                }
                // contrasto pieno: relief_elmos e' il dislivello reale tra il punto piu' basso e il piu' alto
                float range = Math.max(1.0E-3f, hi - lo);
                for (int i = 0; i < px.length; i++) {
                    this.relief[i] = ((this.relief[i] - lo) / range) - 0.5f;
                }
            } else {
                this.reliefSize = 0;
                this.relief = null;
            }
        }

        float maskAt(float u, float v) {
            float fx = clamp(u, 0.0f, 1.0f) * (this.maskW - 1);
            float fy = clamp(v, 0.0f, 1.0f) * (this.maskH - 1);
            int x0 = (int) fx;
            int y0 = (int) fy;
            int x1 = Math.min(this.maskW - 1, x0 + 1);
            int y1 = Math.min(this.maskH - 1, y0 + 1);
            float tx = fx - x0;
            float ty = fy - y0;
            float top = (this.mask[(y0 * this.maskW) + x0] * (1.0f - tx)) + (this.mask[(y0 * this.maskW) + x1] * tx);
            float bottom = (this.mask[(y1 * this.maskW) + x0] * (1.0f - tx)) + (this.mask[(y1 * this.maskW) + x1] * tx);
            return (top * (1.0f - ty)) + (bottom * ty);
        }

        /** Somma in rgb[0..2] il colore del tile in (x, z) elmi, pesato. */
        void addColor(float x, float z, float weight, float[] rgb) {
            float fx = (x / this.material.tileElmos) * this.colorSize;
            float fy = (z / this.material.tileElmos) * this.colorSize;
            int mask = this.colorSize - 1;
            int ix = (int) Math.floor(fx);
            int iy = (int) Math.floor(fy);
            float tx = fx - ix;
            float ty = fy - iy;
            int x0 = ix & mask;
            int y0 = iy & mask;
            int x1 = (ix + 1) & mask;
            int y1 = (iy + 1) & mask;
            int c00 = this.color[(y0 * this.colorSize) + x0];
            int c10 = this.color[(y0 * this.colorSize) + x1];
            int c01 = this.color[(y1 * this.colorSize) + x0];
            int c11 = this.color[(y1 * this.colorSize) + x1];
            float w00 = (1.0f - tx) * (1.0f - ty);
            float w10 = tx * (1.0f - ty);
            float w01 = (1.0f - tx) * ty;
            float w11 = tx * ty;
            float scale = weight / 255.0f;
            rgb[0] += ((((c00 >> 16) & 255) * w00) + (((c10 >> 16) & 255) * w10) + (((c01 >> 16) & 255) * w01) + (((c11 >> 16) & 255) * w11)) * scale;
            rgb[1] += ((((c00 >> 8) & 255) * w00) + (((c10 >> 8) & 255) * w10) + (((c01 >> 8) & 255) * w01) + (((c11 >> 8) & 255) * w11)) * scale;
            rgb[2] += (((c00 & 255) * w00) + ((c10 & 255) * w10) + ((c01 & 255) * w01) + ((c11 & 255) * w11)) * scale;
        }

        boolean hasRelief() {
            return this.relief != null;
        }

        /** Spostamento di quota in elmi del rilievo in (x, z) elmi. */
        float reliefAt(float x, float z) {
            float fx = (x / this.material.tileElmos) * this.reliefSize;
            float fy = (z / this.material.tileElmos) * this.reliefSize;
            int mask = this.reliefSize - 1;
            int ix = (int) Math.floor(fx);
            int iy = (int) Math.floor(fy);
            float tx = fx - ix;
            float ty = fy - iy;
            int x0 = ix & mask;
            int y0 = iy & mask;
            int x1 = (ix + 1) & mask;
            int y1 = (iy + 1) & mask;
            float top = (this.relief[(y0 * this.reliefSize) + x0] * (1.0f - tx)) + (this.relief[(y0 * this.reliefSize) + x1] * tx);
            float bottom = (this.relief[(y1 * this.reliefSize) + x0] * (1.0f - tx)) + (this.relief[(y1 * this.reliefSize) + x1] * tx);
            return ((top * (1.0f - ty)) + (bottom * ty)) * this.material.reliefElmos;
        }

        /**
         * Texture di dettaglio (DNTS) 1024x1024: RGB = normal map, alpha = dettaglio di luminosita'.
         * La normal map viene dal file se presente, altrimenti dal rilievo o dalla luminosita' del colore.
         */
        byte[] createDetailTexture() {
            int size = GroundDetail.SIZE;
            int mask = size - 1;
            int[] diffusePx = pixels(resize(this.material.diffuse, size, size));
            float[] lum = new float[size * size];
            for (int i = 0; i < lum.length; i++) {
                lum[i] = luminance(diffusePx[i]);
            }
            int[] normalPx = this.material.normal != null ? pixels(resize(this.material.normal, size, size)) : null;
            float[] heightField = lum;
            if (normalPx == null && this.material.height != null) {
                int[] hp = pixels(resize(this.material.height, size, size));
                heightField = new float[hp.length];
                for (int i = 0; i < hp.length; i++) {
                    heightField[i] = luminance(hp[i]);
                }
            }
            float[] blurred = boxBlurWrap(lum, size, 20);
            double sum = 0.0;
            for (int i = 0; i < lum.length; i++) {
                float d = lum[i] - blurred[i];
                sum += d * d;
            }
            float deviation = (float) Math.sqrt(sum / lum.length);
            float detailScale = deviation > 1.0E-5f ? 0.045f / deviation : 0.0f;
            float gradientScale = 0.0f;
            if (normalPx == null) {
                double gradSum = 0.0;
                for (int y = 0; y < size; y++) {
                    for (int x = 0; x < size; x++) {
                        float gx = heightField[(y * size) + ((x + 1) & mask)] - heightField[(y * size) + ((x - 1) & mask)];
                        float gy = heightField[(((y + 1) & mask) * size) + x] - heightField[(((y - 1) & mask) * size) + x];
                        gradSum += (gx * gx) + (gy * gy);
                    }
                }
                float gradDeviation = (float) Math.sqrt(gradSum / (2.0 * size * size));
                gradientScale = gradDeviation > 1.0E-6f ? 0.22f / gradDeviation : 0.0f;
            }
            int[] argb = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    int i = (y * size) + x;
                    int rgb;
                    if (normalPx != null) {
                        // pendenza della normal map amplificata: in gioco la luce ambiente alta la appiattisce
                        float nx = ((((normalPx[i] >> 16) & 255) / 255.0f) * 2.0f) - 1.0f;
                        float ny = ((((normalPx[i] >> 8) & 255) / 255.0f) * 2.0f) - 1.0f;
                        float nz = Math.max(0.05f, (((normalPx[i] & 255) / 255.0f) * 2.0f) - 1.0f);
                        nx *= this.material.normalStrength;
                        ny *= this.material.normalStrength;
                        float length = (float) Math.sqrt((nx * nx) + (ny * ny) + (nz * nz));
                        rgb = (toByte(((nx / length) * 0.5f) + 0.5f) << 16) | (toByte(((ny / length) * 0.5f) + 0.5f) << 8) | toByte(((nz / length) * 0.5f) + 0.5f);
                    } else {
                        float gx = heightField[(y * size) + ((x + 1) & mask)] - heightField[(y * size) + ((x - 1) & mask)];
                        float gy = heightField[(((y + 1) & mask) * size) + x] - heightField[(((y - 1) & mask) * size) + x];
                        float nx = -clamp(gx * gradientScale, -2.5f, 2.5f);
                        float ny = -clamp(gy * gradientScale, -2.5f, 2.5f);
                        float length = (float) Math.sqrt((nx * nx) + (ny * ny) + 1.0f);
                        rgb = (toByte(((nx / length) * 0.5f) + 0.5f) << 16) | (toByte(((ny / length) * 0.5f) + 0.5f) << 8) | toByte(((1.0f / length) * 0.5f) + 0.5f);
                    }
                    int alpha = toByte(0.5f + clamp((lum[i] - blurred[i]) * detailScale, -0.22f, 0.22f));
                    argb[i] = (alpha << 24) | rgb;
                }
            }
            return GroundDetail.encodeTga(argb, size, size);
        }
    }

    // ---------------------------------------------------------------------------------------
    // Insieme delle 4 zone e aggancio alla mappa
    // ---------------------------------------------------------------------------------------

    private static final float[] JITTER_SIGN = {1.0f, -1.0f, 0.6f, -0.6f};

    final Layer[] layers;

    /**
     * @param materials 4 materiali (zone 1..4 = canali R, G, B, A dello splat)
     * @param masks     4 maschere 0..1, ciascuna maskW*maskH, gia' normalizzate (somma 1)
     */
    public ZoneMaterials(Material[] materials, float[][] masks, int maskW, int maskH) {
        this.layers = new Layer[4];
        for (int i = 0; i < 4; i++) {
            this.layers[i] = new Layer(materials[i], masks[i], maskW, maskH);
        }
    }

    /** Correzione di colore a bassa frequenza (3 valori per cella), null = nessuna. Vedi setTint. */
    private float[] tint;
    private int tintW;
    private int tintH;

    /**
     * Griglia di moltiplicatori RGB stesa su tutta la mappa: porta il colore medio di ogni area a quello
     * della texture generata in MAPS, lasciando la trama dei materiali. 1 = nessun cambiamento.
     */
    public void setTint(float[] tint, int w, int h) {
        if (tint != null && tint.length == w * h * 3) {
            this.tint = tint;
            this.tintW = w;
            this.tintH = h;
        }
    }

    /** Moltiplica rgb per la correzione in (u, v) 0..1, interpolata. */
    void applyTint(float u, float v, float[] rgb) {
        if (this.tint == null) {
            return;
        }
        float fx = Math.max(0.0f, Math.min(this.tintW - 1.0f, (u * this.tintW) - 0.5f));
        float fy = Math.max(0.0f, Math.min(this.tintH - 1.0f, (v * this.tintH) - 0.5f));
        int x0 = (int) fx;
        int y0 = (int) fy;
        int x1 = Math.min(this.tintW - 1, x0 + 1);
        int y1 = Math.min(this.tintH - 1, y0 + 1);
        float tx = fx - x0;
        float ty = fy - y0;
        for (int c = 0; c < 3; c++) {
            float top = (this.tint[(((y0 * this.tintW) + x0) * 3) + c] * (1.0f - tx)) + (this.tint[(((y0 * this.tintW) + x1) * 3) + c] * tx);
            float bottom = (this.tint[(((y1 * this.tintW) + x0) * 3) + c] * (1.0f - tx)) + (this.tint[(((y1 * this.tintW) + x1) * 3) + c] * tx);
            rgb[c] *= (top * (1.0f - ty)) + (bottom * ty);
        }
    }

    /**
     * Pesi delle 4 zone in (x, z) elmi. jitter (circa -1..1) sposta i bordi con il rumore
     * dell'export e il contrasto li rende netti invece che "fangosi".
     */
    void weights(float u, float v, float jitter, float[] out) {
        float sum = 0.0f;
        for (int i = 0; i < 4; i++) {
            float w = this.layers[i].maskAt(u, v);
            w += jitter * JITTER_SIGN[i] * 1.2f * w * (1.0f - w);
            w = Math.max(0.0f, w);
            w = w * w;
            out[i] = w;
            sum += w;
        }
        if (sum <= 1.0E-5f) {
            out[0] = 1.0f;
            out[1] = 0.0f;
            out[2] = 0.0f;
            out[3] = 0.0f;
            return;
        }
        for (int i = 0; i < 4; i++) {
            out[i] /= sum;
        }
    }

    boolean hasRelief() {
        for (Layer layer : this.layers) {
            if (layer.hasRelief()) {
                return true;
            }
        }
        return false;
    }

    String describe() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            Material m = this.layers[i].material;
            sb.append(String.format(Locale.ROOT, "%s%s (tile %.0f, gloss %.2f%s)", i > 0 ? ", " : "", m.name, m.tileElmos, m.gloss,
                this.layers[i].hasRelief() ? String.format(Locale.ROOT, ", rilievo %.0f", m.reliefElmos) : ""));
        }
        return sb.toString();
    }

    private static WeakReference<SCMap> attachedMap = new WeakReference<>(null);
    private static ZoneMaterials attached;
    private static ZoneMaterials pending;

    /** Il pannello Texture deposita qui l'insieme prima di chiamare "Applica". */
    public static synchronized void setPending(ZoneMaterials materials) {
        pending = materials;
    }

    public static synchronized ZoneMaterials takePending() {
        ZoneMaterials result = pending;
        pending = null;
        return result;
    }

    /** Lega i materiali alla mappa attiva (null = torna al comportamento senza zone). */
    public static synchronized void attach(SCMap map, ZoneMaterials materials) {
        attachedMap = new WeakReference<>(map);
        attached = materials;
    }

    public static synchronized ZoneMaterials get(SCMap map) {
        return map != null && attachedMap.get() == map ? attached : null;
    }

    // ---------------------------------------------------------------------------------------
    // Utilita' immagini
    // ---------------------------------------------------------------------------------------

    /** Ridimensiona con dimezzamenti successivi (media per area), poi bilineare all'ultimo passo. */
    static BufferedImage resize(BufferedImage src, int w, int h) {
        BufferedImage current = src;
        int cw = src.getWidth();
        int ch = src.getHeight();
        while (cw / 2 >= w && ch / 2 >= h) {
            cw /= 2;
            ch /= 2;
            current = scale(current, cw, ch);
        }
        if (cw != w || ch != h || current.getType() != BufferedImage.TYPE_INT_ARGB) {
            current = scale(current, w, h);
        }
        return current;
    }

    private static BufferedImage scale(BufferedImage src, int w, int h) {
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return out;
    }

    private static int[] pixels(BufferedImage img) {
        int[] px = new int[img.getWidth() * img.getHeight()];
        img.getRGB(0, 0, img.getWidth(), img.getHeight(), px, 0, img.getWidth());
        return px;
    }

    private static float luminance(int rgb) {
        return ((((rgb >> 16) & 255) * 0.2126f) + (((rgb >> 8) & 255) * 0.7152f) + ((rgb & 255) * 0.0722f)) / 255.0f;
    }

    private static float[] boxBlurWrap(float[] values, int size, int radius) {
        int mask = size - 1;
        float norm = 1.0f / ((2 * radius) + 1);
        float[] tmp = new float[values.length];
        for (int y = 0; y < size; y++) {
            float acc = 0.0f;
            for (int k = -radius; k <= radius; k++) {
                acc += values[(y * size) + (k & mask)];
            }
            for (int x = 0; x < size; x++) {
                tmp[(y * size) + x] = acc * norm;
                acc += values[(y * size) + ((x + radius + 1) & mask)] - values[(y * size) + ((x - radius) & mask)];
            }
        }
        float[] out = new float[values.length];
        for (int x = 0; x < size; x++) {
            float acc = 0.0f;
            for (int k = -radius; k <= radius; k++) {
                acc += tmp[((k & mask) * size) + x];
            }
            for (int y = 0; y < size; y++) {
                out[(y * size) + x] = acc * norm;
                acc += tmp[(((y + radius + 1) & mask) * size) + x] - tmp[(((y - radius) & mask) * size) + x];
            }
        }
        return out;
    }

    private static int powerOfTwoAtMost(int value) {
        return Integer.highestOneBit(Math.max(1, value));
    }

    private static int toByte(float value) {
        return Math.max(0, Math.min(255, Math.round(value * 255.0f)));
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}

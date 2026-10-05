package com.faforever.neroxis.generator.bar;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Rappresentazione geometrica 3D per rendering e ispezione in BAR MAP STUDIO.
 * Include vertici, facce, normali, coordinate UV, materiali MTL, baking palette texture
 * ed esportazione nativa in formato binario S3O con supporto completo alla scala e team-color.
 */
public final class ObjModel3D {
    private final float[] vertices; // x, y, z appiattiti: v[i*3], v[i*3+1], v[i*3+2]
    private final int[][] faces;     // indici dei vertici per ogni faccia
    private final float[][] faceNormals; // nx, ny, nz per ogni faccia
    private final int[] faceColors;  // colore RGB per faccia (0xRRGGBB)
    private final float[][] faceUVs; // u, v per ciascun vertice di ogni faccia
    private final Map<String, float[]> materialColors;
    private final String[] faceMaterials;
    private BufferedImage bakedTexture;

    private final float minX, maxX, minY, maxY, minZ, maxZ;
    private final float centerX, centerY, centerZ;
    private final float maxDimension;

    public ObjModel3D(float[] vertices, int[][] faces, float[][] faceNormals) {
        this(vertices, faces, faceNormals, null, null, null, null);
    }

    public ObjModel3D(float[] vertices, int[][] faces, float[][] faceNormals,
                      int[] faceColors, float[][] faceUVs,
                      Map<String, float[]> materialColors, String[] faceMaterials) {
        this.vertices = vertices != null ? vertices : new float[0];
        this.faces = faces != null ? faces : new int[0][0];

        // Calcolo Bounding Box AABB
        float minX = Float.POSITIVE_INFINITY, maxX = Float.NEGATIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY, maxZ = Float.NEGATIVE_INFINITY;

        int vertCount = this.vertices.length / 3;
        if (vertCount == 0) {
            minX = maxX = minY = maxY = minZ = maxZ = 0;
        } else {
            for (int i = 0; i < vertCount; i++) {
                float x = this.vertices[i * 3];
                float y = this.vertices[i * 3 + 1];
                float z = this.vertices[i * 3 + 2];
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
                if (z < minZ) minZ = z;
                if (z > maxZ) maxZ = z;
            }
        }

        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;

        this.centerX = (minX + maxX) / 2.0f;
        this.centerY = (minY + maxY) / 2.0f;
        this.centerZ = (minZ + maxZ) / 2.0f;

        float dimX = maxX - minX;
        float dimY = maxY - minY;
        float dimZ = maxZ - minZ;
        this.maxDimension = Math.max(Math.max(dimX, dimY), Math.max(dimZ, 0.0001f));

        // Calcolo o normalizzazione normali di faccia
        if (faceNormals != null && faceNormals.length == this.faces.length) {
            this.faceNormals = faceNormals;
        } else {
            this.faceNormals = new float[this.faces.length][3];
            for (int f = 0; f < this.faces.length; f++) {
                int[] face = this.faces[f];
                if (face.length >= 3) {
                    int i0 = face[0];
                    int i1 = face[1];
                    int i2 = face[2];

                    float ax = this.vertices[i1 * 3] - this.vertices[i0 * 3];
                    float ay = this.vertices[i1 * 3 + 1] - this.vertices[i0 * 3 + 1];
                    float az = this.vertices[i1 * 3 + 2] - this.vertices[i0 * 3 + 2];

                    float bx = this.vertices[i2 * 3] - this.vertices[i0 * 3];
                    float by = this.vertices[i2 * 3 + 1] - this.vertices[i0 * 3 + 1];
                    float bz = this.vertices[i2 * 3 + 2] - this.vertices[i0 * 3 + 2];

                    // Prodotto vettoriale (A x B)
                    float nx = ay * bz - az * by;
                    float ny = az * bx - ax * bz;
                    float nz = ax * by - ay * bx;

                    float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
                    if (len > 0.00001f) {
                        this.faceNormals[f][0] = nx / len;
                        this.faceNormals[f][1] = ny / len;
                        this.faceNormals[f][2] = nz / len;
                    } else {
                        this.faceNormals[f][0] = 0;
                        this.faceNormals[f][1] = 1;
                        this.faceNormals[f][2] = 0;
                    }
                }
            }
        }

        // Colori faccia
        if (faceColors != null && faceColors.length == this.faces.length) {
            this.faceColors = faceColors;
        } else {
            this.faceColors = new int[this.faces.length];
            Arrays.fill(this.faceColors, 0x508CBE);
        }

        // UV coordinate e materiali
        this.faceUVs = (faceUVs != null && faceUVs.length == this.faces.length) ? faceUVs : new float[this.faces.length][];
        this.materialColors = materialColors;
        this.faceMaterials = faceMaterials;
        // Copia delle UV originali (vt): serve per ripristinarle solo sui materiali CON texture
        this.baseUVs = new float[this.faces.length][];
        for (int f = 0; f < this.faces.length; f++) {
            float[] src = (f < this.faceUVs.length) ? this.faceUVs[f] : null;
            this.baseUVs[f] = (src != null) ? java.util.Arrays.copyOf(src, src.length) : null;
        }

        // Bake automatico palette se presenti materiali MTL
        if (materialColors != null && !materialColors.isEmpty()) {
            bakeTexturePalette();
        }
    }

    /** UV vt originali del parser (possono essere null per faccia). */
    private final float[][] baseUVs;

    private void bakeTexturePalette() {
        if (materialColors == null || materialColors.isEmpty()) return;

        List<String> mtlList = new ArrayList<>(materialColors.keySet());
        int numMtls = mtlList.size();
        // Celle larghe e larghezza potenza di 2: con le mipmap del motore i colori vicini non si mescolano
        int cellW = 32;
        int imgW = 64;
        while (imgW < numMtls * cellW) imgW <<= 1;
        int imgH = 16;

        BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
        Map<String, Float> uCenters = new HashMap<>();

        for (int k = 0; k < numMtls; k++) {
            String mtl = mtlList.get(k);
            float[] rgb = materialColors.get(mtl);
            int r = Math.max(0, Math.min(255, Math.round(rgb[0] * 255.0f)));
            int g = Math.max(0, Math.min(255, Math.round(rgb[1] * 255.0f)));
            int b = Math.max(0, Math.min(255, Math.round(rgb[2] * 255.0f)));
            int argb = (0xFF << 24) | (r << 16) | (g << 8) | b;

            int startX = k * cellW;
            int endX = (k + 1) * cellW;
            for (int x = startX; x < endX; x++) {
                for (int y = 0; y < imgH; y++) {
                    img.setRGB(x, y, argb);
                }
            }
            float uCenter = (startX + cellW * 0.5f) / (float) imgW;
            uCenters.put(mtl, uCenter);
        }

        for (int x = numMtls * cellW; x < imgW; x++) {
            for (int y = 0; y < imgH; y++) {
                img.setRGB(x, y, 0xFF808080);
            }
        }

        this.bakedTexture = img;

        // Regola UV: colore Kd -> cella palette (vale per TUTTE le facce del materiale,
        // anche se hanno vt: senza immagine le vt non hanno senso). Le vt originali
        // restano in baseUVs e vengono ripristinate da setMaterialDiffuse per i
        // materiali che hanno davvero una texture (map_Kd).
        if (faceMaterials != null) {
            for (int f = 0; f < faces.length; f++) {
                if (f < faceMaterials.length && faceMaterials[f] != null) {
                    String mtl = faceMaterials[f];
                    if (uCenters.containsKey(mtl)) {
                        float u = uCenters.get(mtl);
                        float v = 0.5f;
                        int vCount = faces[f].length;
                        float[] uvs = new float[vCount * 2];
                        for (int vi = 0; vi < vCount; vi++) {
                            uvs[vi * 2] = u;
                            uvs[vi * 2 + 1] = v;
                        }
                        this.faceUVs[f] = uvs;
                    }
                }
            }
        }
    }

    public float[] getVertices() { return vertices; }
    public int[][] getFaces() { return faces; }
    public float[][] getFaceNormals() { return faceNormals; }
    public int getVertexCount() { return vertices.length / 3; }
    public int getFaceCount() { return faces.length; }

    public int getFaceColor(int faceIndex) {
        if (faceColors != null && faceIndex >= 0 && faceIndex < faceColors.length) {
            return faceColors[faceIndex];
        }
        return 0x508CBE;
    }

    public BufferedImage getBakedTexture() { return bakedTexture; }
    public void setBakedTexture(BufferedImage tex) { this.bakedTexture = tex; }
    public float[][] getFaceUVs() { return faceUVs; }
    public String[] getFaceMaterials() { return faceMaterials; }
    public Map<String, float[]> getMaterialColors() { return materialColors; }
    public int[] getFaceColors() { return faceColors; }

    /** Materiale con texture diffusa opzionale (map_Kd) + colore Kd fallback. */
    public static final class Material {
        public final String name;
        public final int kdRgb;
        public BufferedImage diffuse;
        public Material(String name, int kdRgb, BufferedImage diffuse) {
            this.name = name; this.kdRgb = kdRgb; this.diffuse = diffuse;
        }
    }
    private final Map<String, Material> materials = new HashMap<>();
    public Map<String, Material> getMaterials() { return materials; }
    public void setMaterialDiffuse(String name, BufferedImage img) {
        if (name == null) return;
        Material m = materials.get(name);
        if (m == null) {
            float[] rgb = materialColors != null ? materialColors.get(name) : null;
            int kd = 0x508CBE;
            if (rgb != null) {
                int r = Math.max(0, Math.min(255, Math.round(rgb[0] * 255f)));
                int g = Math.max(0, Math.min(255, Math.round(rgb[1] * 255f)));
                int b = Math.max(0, Math.min(255, Math.round(rgb[2] * 255f)));
                kd = (r << 16) | (g << 8) | b;
            }
            m = new Material(name, kd, img);
            materials.put(name, m);
        } else m.diffuse = img;
        // Con una texture reale valgono le UV vt originali; senza texture resta la cella palette.
        if (img != null) restoreBaseUVsFor(name);
    }

    /** Ripristina le UV vt originali per tutte le facce del materiale (usato quando arriva map_Kd). */
    private void restoreBaseUVsFor(String materialName) {
        if (materialName == null || faceMaterials == null || baseUVs == null) return;
        for (int f = 0; f < faces.length; f++) {
            if (f >= faceMaterials.length || !materialName.equals(faceMaterials[f])) continue;
            float[] base = (f < baseUVs.length) ? baseUVs[f] : null;
            if (base != null && base.length == faces[f].length * 2) {
                faceUVs[f] = java.util.Arrays.copyOf(base, base.length);
            }
        }
    }

    /** true se almeno un materiale ha una texture diffusa reale. */
    public boolean hasAnyDiffuse() {
        for (Material m : materials.values()) if (m.diffuse != null) return true;
        return false;
    }

    /**
     * UV originali del file (vt) per faccia. A differenza di getFaceUVs() non vengono
     * sostituite dalle celle della palette Kd: servono all'atlante di esportazione,
     * perche' una faccia con map_Kd deve campionare l'immagine, non il colore Kd.
     */
    public float[][] getBaseUVs() { return baseUVs; }

    /** Ritorna la prima texture diffusa disponibile (o null). */
    public BufferedImage firstDiffuse() {
        for (Material m : materials.values()) if (m.diffuse != null) return m.diffuse;
        return null;
    }

    public float getMinX() { return minX; }
    public float getMaxX() { return maxX; }
    public float getMinY() { return minY; }
    public float getMaxY() { return maxY; }
    public float getMinZ() { return minZ; }
    public float getMaxZ() { return maxZ; }

    public float getCenterX() { return centerX; }
    public float getCenterY() { return centerY; }
    public float getCenterZ() { return centerZ; }
    public float getMaxDimension() { return maxDimension; }

    /**
     * Genera un modello procedurale di Mulino a Vento con torre e 4 pale per test immediato.
     */
    public static ObjModel3D createProceduralWindmill() {
        List<Float> vList = new ArrayList<>();
        List<int[]> fList = new ArrayList<>();

        java.util.function.Function<float[], Integer> addV = (coords) -> {
            int idx = vList.size() / 3;
            vList.add(coords[0]);
            vList.add(coords[1]);
            vList.add(coords[2]);
            return idx;
        };

        // Base Torre Ottagonale
        int segs = 8;
        float rBase = 2.0f;
        float rTop = 1.2f;
        float hTower = 5.0f;

        int[] baseIndices = new int[segs];
        int[] topIndices = new int[segs];

        for (int i = 0; i < segs; i++) {
            double ang = 2.0 * Math.PI * i / segs;
            float cos = (float) Math.cos(ang);
            float sin = (float) Math.sin(ang);
            baseIndices[i] = addV.apply(new float[]{rBase * cos, 0.0f, rBase * sin});
            topIndices[i] = addV.apply(new float[]{rTop * cos, hTower, rTop * sin});
        }

        // Facce laterali della torre
        for (int i = 0; i < segs; i++) {
            int next = (i + 1) % segs;
            fList.add(new int[]{baseIndices[i], baseIndices[next], topIndices[next], topIndices[i]});
        }

        // Tetto conico a punta
        int apexIdx = addV.apply(new float[]{0.0f, hTower + 1.8f, 0.0f});
        for (int i = 0; i < segs; i++) {
            int next = (i + 1) % segs;
            fList.add(new int[]{topIndices[i], topIndices[next], apexIdx});
        }

        // Mozzo del rotore frontale (a Z positiva)
        float hubY = hTower - 0.4f;
        float hubZ = rTop + 0.4f;
        int hubCenterIdx = addV.apply(new float[]{0.0f, hubY, hubZ});

        // 4 Pale a croce
        float bladeLen = 4.2f;
        float bladeHalfW = 0.35f;

        for (int b = 0; b < 4; b++) {
            double ang = b * (Math.PI / 2.0);
            float cos = (float) Math.cos(ang);
            float sin = (float) Math.sin(ang);
            float perpCos = -sin;
            float perpSin = cos;

            int v0 = addV.apply(new float[]{perpCos * bladeHalfW, hubY + perpSin * bladeHalfW, hubZ});
            int v1 = addV.apply(new float[]{-perpCos * bladeHalfW, hubY - perpSin * bladeHalfW, hubZ});
            int v2 = addV.apply(new float[]{cos * bladeLen - perpCos * bladeHalfW, hubY + sin * bladeLen - perpSin * bladeHalfW, hubZ});
            int v3 = addV.apply(new float[]{cos * bladeLen + perpCos * bladeHalfW, hubY + sin * bladeLen + perpSin * bladeHalfW, hubZ});

            fList.add(new int[]{v0, v1, v2, v3});
        }

        float[] vArr = new float[vList.size()];
        for (int i = 0; i < vList.size(); i++) vArr[i] = vList.get(i);
        int[][] fArr = fList.toArray(new int[0][]);

        return new ObjModel3D(vArr, fArr, null);
    }

    public static ObjModel3D createProceduralCube(float size) {
        float h = size / 2.0f;
        float[] v = {
            -h, 0, -h,  h, 0, -h,  h, size, -h, -h, size, -h, // retro
            -h, 0,  h,  h, 0,  h,  h, size,  h, -h, size,  h  // fronte
        };
        int[][] f = {
            {0, 1, 2, 3}, // retro
            {5, 4, 7, 6}, // fronte
            {4, 0, 3, 7}, // sinistra
            {1, 5, 6, 2}, // destra
            {3, 2, 6, 7}, // sopra
            {4, 5, 1, 0}  // sotto
        };
        return new ObjModel3D(v, f, null);
    }

    public byte[] toObjBytes(String modelName) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Wavefront OBJ generato da BAR Map Studio\n");
        sb.append("o ").append(modelName != null && !modelName.isBlank() ? modelName.replaceAll("\\s+", "_") : "Model3D").append("\n");
        int vertCount = vertices.length / 3;
        for (int i = 0; i < vertCount; i++) {
            sb.append(String.format(Locale.ROOT, "v %.4f %.4f %.4f\n", vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2]));
        }
        for (float[] fn : faceNormals) {
            sb.append(String.format(Locale.ROOT, "vn %.4f %.4f %.4f\n", fn[0], fn[1], fn[2]));
        }
        for (int f = 0; f < faces.length; f++) {
            int[] face = faces[f];
            if (face.length >= 3) {
                sb.append("f");
                int normIdx = (f < faceNormals.length) ? (f + 1) : 1;
                for (int vIdx : face) {
                    sb.append(" ").append(vIdx + 1).append("//").append(normIdx);
                }
                sb.append("\n");
            }
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] toS3OBytes(String modelName) {
        return toS3OBytes(modelName, "default_mat.tga", "", 1.0f);
    }

    public byte[] toS3OBytes(String modelName, String texture1, String texture2) {
        return toS3OBytes(modelName, texture1, texture2, 1.0f);
    }

    /** Nome del pezzo per faccia (dagli "o"/"g" dell'OBJ); null = modello a pezzo unico. */
    private String[] facePieces;

    public String[] getFacePieces() { return facePieces; }

    public void setFacePieces(String[] facePieces) {
        this.facePieces = (facePieces != null && facePieces.length == this.faces.length) ? facePieces : null;
    }

    /** Nomi dei pezzi nell'ordine in cui compaiono (vuoto = pezzo unico "base"). */
    public List<String> getPieceNames() {
        List<String> names = new ArrayList<>();
        if (facePieces != null) {
            for (String name : facePieces) {
                if (name != null && !names.contains(name)) names.add(name);
            }
        }
        return names;
    }

    /** Pezzo dell'S3O in costruzione: vertici gia' scalati, in coordinate modello. */
    private static final class S3OPiece {
        final String name;
        final List<float[]> verts = new ArrayList<>(); // pos3, normale3, uv2
        float ox, oy, oz; // perno, relativo al pezzo padre
        final List<S3OPiece> children = new ArrayList<>();
        int offset;

        S3OPiece(String name) { this.name = name; }

        int byteSize() {
            return 52 + name.length() + 1 + (children.size() * 4) + (verts.size() * 32) + (verts.size() * 4);
        }
    }

    /**
     * Esporta la geometria del modello 3D in formato binario nativo S3O (Spring 3D Object).
     * Include scalatura, centratura orizzontale, appoggio alla base (Y=0) e coordinate UV.
     * Se l'OBJ ha piu' oggetti ("o"/"g") ognuno diventa un pezzo animabile: la radice e' "body"
     * (o "base", o il pezzo piu' grande) e gli altri sono suoi figli, con il perno nel punto
     * di attacco al corpo. Senza oggetti il modello resta un pezzo unico "base".
     */
    public byte[] toS3OBytes(String modelName, String texture1, String texture2, float scale) {
        float effectiveScale = Math.max(0.001f, scale);
        byte[] tex1Bytes = (texture1 != null && !texture1.isBlank() ? texture1 : "default_mat.tga").getBytes(StandardCharsets.US_ASCII);
        byte[] tex2Bytes = (texture2 != null ? texture2 : "").getBytes(StandardCharsets.US_ASCII);

        float rangeX = Math.max(maxX - minX, 0.001f);
        float rangeZ = Math.max(maxZ - minZ, 0.001f);

        // 1. Scelta della radice
        List<String> names = getPieceNames();
        String rootName = "base";
        if (names.size() > 1) {
            if (names.contains("body")) {
                rootName = "body";
            } else if (!names.contains("base")) {
                int best = -1;
                for (String name : names) {
                    int count = 0;
                    for (String fp : facePieces) if (name.equals(fp)) count++;
                    if (count > best) { best = count; rootName = name; }
                }
            }
        } else if (names.size() == 1) {
            rootName = names.get(0);
        }
        S3OPiece root = new S3OPiece(rootName);
        Map<String, S3OPiece> pieces = new java.util.LinkedHashMap<>();
        pieces.put(rootName, root);

        // 2. Triangolazione: ogni triangolo va nel suo pezzo (vertici non condivisi, normale di faccia)
        for (int f = 0; f < faces.length; f++) {
            int[] face = faces[f];
            if (face.length < 3) continue;
            float[] fn = (f < faceNormals.length && faceNormals[f] != null) ? faceNormals[f] : new float[]{0, 1, 0};
            float[] uvs = (faceUVs != null && f < faceUVs.length) ? faceUVs[f] : null;
            if (uvs != null && uvs.length < face.length * 2) uvs = null;
            String pieceName = (names.size() > 1 && facePieces[f] != null) ? facePieces[f] : rootName;
            S3OPiece piece = pieces.get(pieceName);
            if (piece == null) {
                piece = new S3OPiece(pieceName);
                pieces.put(pieceName, piece);
                root.children.add(piece);
            }
            for (int i = 1; i < face.length - 1; i++) {
                int[] corners = {0, i, i + 1};
                for (int corner : corners) {
                    int idx = face[corner];
                    float origX = (idx * 3 < vertices.length) ? vertices[idx * 3] : 0f;
                    float origY = (idx * 3 + 1 < vertices.length) ? vertices[idx * 3 + 1] : 0f;
                    float origZ = (idx * 3 + 2 < vertices.length) ? vertices[idx * 3 + 2] : 0f;
                    float u, v;
                    if (uvs != null) {
                        u = uvs[corner * 2];
                        v = uvs[corner * 2 + 1];
                    } else {
                        u = (origX - minX) / rangeX;
                        v = (origZ - minZ) / rangeZ;
                    }
                    // Scalatura, centratura orizzontale e appoggio al suolo Y=0
                    piece.verts.add(new float[]{
                        (origX - centerX) * effectiveScale, (origY - minY) * effectiveScale, (origZ - centerZ) * effectiveScale,
                        fn[0], fn[1], fn[2], u, v});
                }
            }
        }

        // 3. Perno dei pezzi figli: il punto di attacco, cioe' la media dei vertici piu' vicini al centro del corpo
        if (!root.children.isEmpty() && !root.verts.isEmpty()) {
            float[] lo = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
            float[] hi = {-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
            for (float[] vert : root.verts) {
                for (int k = 0; k < 3; k++) {
                    lo[k] = Math.min(lo[k], vert[k]);
                    hi[k] = Math.max(hi[k], vert[k]);
                }
            }
            float cx = (lo[0] + hi[0]) * 0.5f, cy = (lo[1] + hi[1]) * 0.5f, cz = (lo[2] + hi[2]) * 0.5f;
            for (S3OPiece child : root.children) {
                int n = child.verts.size();
                if (n == 0) continue;
                float[] dist = new float[n];
                for (int i = 0; i < n; i++) {
                    float[] vert = child.verts.get(i);
                    float dx = vert[0] - cx, dy = vert[1] - cy, dz = vert[2] - cz;
                    dist[i] = (dx * dx) + (dy * dy) + (dz * dz);
                }
                float[] sorted = dist.clone();
                Arrays.sort(sorted);
                float threshold = sorted[Math.min(n - 1, Math.max(2, n / 8))];
                float px = 0, py = 0, pz = 0;
                int count = 0;
                for (int i = 0; i < n; i++) {
                    if (dist[i] <= threshold) {
                        float[] vert = child.verts.get(i);
                        px += vert[0]; py += vert[1]; pz += vert[2];
                        count++;
                    }
                }
                child.ox = px / count; child.oy = py / count; child.oz = pz / count;
                for (float[] vert : child.verts) {
                    vert[0] -= child.ox; vert[1] -= child.oy; vert[2] -= child.oz;
                }
            }
        }

        // 4. Disposizione nel file: header, nomi texture, poi i pezzi uno dopo l'altro
        int tex1Offset = 52;
        int tex2Offset = tex1Offset + tex1Bytes.length + 1;
        int cursor = tex2Offset + tex2Bytes.length + 1;
        List<S3OPiece> ordered = new ArrayList<>();
        ordered.add(root);
        ordered.addAll(root.children);
        for (S3OPiece piece : ordered) {
            piece.offset = cursor;
            cursor += piece.byteSize();
        }

        ByteBuffer buf = ByteBuffer.allocate(cursor);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        float scaledRadius = Math.max(1.0f, (maxDimension * 0.5f) * effectiveScale);
        float scaledHeight = Math.max(1.0f, (maxY - minY) * effectiveScale);

        // Header S3O (52 bytes)
        buf.put(new byte[]{'S', 'p', 'r', 'i', 'n', 'g', ' ', 'u', 'n', 'i', 't', 0});
        buf.putInt(0); // version 0
        buf.putFloat(scaledRadius);
        buf.putFloat(scaledHeight);
        buf.putFloat(0.0f); // midx centrato
        buf.putFloat(scaledHeight * 0.5f); // midy
        buf.putFloat(0.0f); // midz centrato
        buf.putInt(root.offset);
        buf.putInt(0); // collisionDataOffset
        buf.putInt(tex1Offset);
        buf.putInt(tex2Offset);
        buf.put(tex1Bytes);
        buf.put((byte) 0);
        buf.put(tex2Bytes);
        buf.put((byte) 0);

        for (S3OPiece piece : ordered) {
            byte[] nameBytes = piece.name.getBytes(StandardCharsets.US_ASCII);
            int nameOffset = piece.offset + 52;
            int childrenOffset = nameOffset + nameBytes.length + 1;
            int verticesOffset = childrenOffset + (piece.children.size() * 4);
            int indicesOffset = verticesOffset + (piece.verts.size() * 32);
            // Piece header (52 bytes)
            buf.putInt(nameOffset);
            buf.putInt(piece.children.size());
            buf.putInt(childrenOffset);
            buf.putInt(piece.verts.size());
            buf.putInt(verticesOffset);
            buf.putInt(0); // vertexType
            buf.putInt(0); // primitiveType (0 = triangles)
            buf.putInt(piece.verts.size()); // vertexTableSize (indici)
            buf.putInt(indicesOffset);
            buf.putInt(0); // collisionDataOffset
            buf.putFloat(piece.ox);
            buf.putFloat(piece.oy);
            buf.putFloat(piece.oz);
            buf.put(nameBytes);
            buf.put((byte) 0);
            for (S3OPiece child : piece.children) {
                buf.putInt(child.offset);
            }
            // Vertices (32 bytes each: 3f pos, 3f normal, 2f UV)
            for (float[] vert : piece.verts) {
                for (float value : vert) buf.putFloat(value);
            }
            for (int i = 0; i < piece.verts.size(); i++) {
                buf.putInt(i);
            }
        }
        return buf.array();
    }

    /**
     * Converte un BufferedImage in un array di byte formato TGA a 32-bit (BGRA) non compresso,
     * compatibile al 100% con il motore Spring/BAR. Alpha = 255 su ogni pixel: adatto a tex2
     * (alpha = opacità). Per tex1 usare {@link #imageToTgaBytes(BufferedImage, int)} con alpha 0.
     */
    public static byte[] imageToTgaBytes(BufferedImage img) {
        return imageToTgaBytes(img, 255);
    }

    /**
     * Come sopra, ma scrive l'alpha indicato su ogni pixel. In tex1 l'alpha è la maschera del
     * colore squadra (BAR: albedo = mix(tex1.rgb, teamColor, tex1.a)): 0 = colori originali,
     * 255 = modello interamente tinto col colore squadra.
     */
    public static byte[] imageToTgaBytes(BufferedImage img, int alpha) {
        if (img == null) return null;
        int w = img.getWidth();
        int h = img.getHeight();
        byte[] tga = new byte[18 + w * h * 4];
        tga[2] = 2; // uncompressed true-color
        tga[12] = (byte) (w & 0xFF);
        tga[13] = (byte) ((w >> 8) & 0xFF);
        tga[14] = (byte) (h & 0xFF);
        tga[15] = (byte) ((h >> 8) & 0xFF);
        tga[16] = 32; // 32 bits per pixel (BGRA)
        tga[17] = 8;  // 8 bits alpha

        int offset = 18;
        // TGA standard bottom-to-top
        for (int y = h - 1; y >= 0; y--) {
            for (int x = 0; x < w; x++) {
                int argb = img.getRGB(x, y);
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                tga[offset++] = (byte) b;
                tga[offset++] = (byte) g;
                tga[offset++] = (byte) r;
                tga[offset++] = (byte) alpha;
            }
        }
        return tga;
    }
}

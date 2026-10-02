package com.faforever.neroxis.generator.bar;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresentazione geometrica 3D per rendering e ispezione in BAR MAP STUDIO.
 * Include vertici, facce triangolari/quadrangolari, normali e AABB per auto-centratura.
 */
public final class ObjModel3D {
    private final float[] vertices; // x, y, z appiattiti: v[i*3], v[i*3+1], v[i*3+2]
    private final int[][] faces;     // indici dei vertici per ogni faccia
    private final float[][] faceNormals; // nx, ny, nz per ogni faccia
    private final float minX, maxX, minY, maxY, minZ, maxZ;
    private final float centerX, centerY, centerZ;
    private final float maxDimension;

    public ObjModel3D(float[] vertices, int[][] faces, float[][] faceNormals) {
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
    }

    public float[] getVertices() { return vertices; }
    public int[][] getFaces() { return faces; }
    public float[][] getFaceNormals() { return faceNormals; }
    public int getVertexCount() { return vertices.length / 3; }
    public int getFaceCount() { return faces.length; }

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

        // Helper per aggiungere vertice
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
            baseIndices[i] = addV.apply(new float[]{cos * rBase, 0.0f, sin * rBase});
            topIndices[i] = addV.apply(new float[]{cos * rTop, hTower, sin * rTop});
        }

        // Facce laterali torre
        for (int i = 0; i < segs; i++) {
            int next = (i + 1) % segs;
            fList.add(new int[]{baseIndices[i], next < segs ? baseIndices[next] : baseIndices[0], topIndices[next], topIndices[i]});
        }

        // Tetto a cupola / cono
        int apex = addV.apply(new float[]{0.0f, hTower + 1.6f, 0.0f});
        for (int i = 0; i < segs; i++) {
            int next = (i + 1) % segs;
            fList.add(new int[]{topIndices[i], topIndices[next], apex});
        }

        // Mozzo pale (sporgente sull'asse Z)
        int hubCenter = addV.apply(new float[]{0.0f, hTower + 0.6f, rTop + 0.5f});

        // 4 Pale rotanti a croce
        float bladeLen = 3.6f;
        float bladeWidth = 0.45f;
        for (int b = 0; b < 4; b++) {
            double bAng = b * (Math.PI / 2.0);
            float cos = (float) Math.cos(bAng);
            float sin = (float) Math.sin(bAng);
            float px = -sin * bladeWidth;
            float py = cos * bladeWidth;

            int p0 = hubCenter;
            int p1 = addV.apply(new float[]{cos * bladeLen, hTower + 0.6f + sin * bladeLen, rTop + 0.5f});
            int p2 = addV.apply(new float[]{cos * bladeLen + px, hTower + 0.6f + sin * bladeLen + py, rTop + 0.45f});
            int p3 = addV.apply(new float[]{px * 0.4f, hTower + 0.6f + py * 0.4f, rTop + 0.48f});

            fList.add(new int[]{p0, p1, p2, p3});
        }

        float[] vArr = new float[vList.size()];
        for (int i = 0; i < vList.size(); i++) vArr[i] = vList.get(i);
        int[][] fArr = fList.toArray(new int[0][]);

        return new ObjModel3D(vArr, fArr, null);
    }

    /**
     * Genera un modello procedurale di Cubo Tech per test o placeholder.
     */
    public static ObjModel3D createProceduralCube(float size) {
        float h = size / 2.0f;
        float[] v = {
            -h, -h, -h,  h, -h, -h,  h,  h, -h, -h,  h, -h, // retro
            -h, -h,  h,  h, -h,  h,  h,  h,  h, -h,  h,  h  // fronte
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
}

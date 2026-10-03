package com.faforever.neroxis.generator.bar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser Wavefront .obj e .mtl in Java puro, ultra-rapido e a zero dipendenze esterne.
 * Supporta facce poligonali, coordinate texture vt, normali vn, materiali multipli usemtl,
 * ed estrazione colori diffusi Kd da file .mtl associati.
 */
public final class ObjParser {

    private ObjParser() {}

    public static ObjModel3D parse(Path path) throws IOException {
        return parse(path.toFile());
    }

    public static ObjModel3D parse(File file) throws IOException {
        File parentDir = file.getParentFile();
        try (InputStream in = new FileInputStream(file)) {
            return parse(in, parentDir);
        }
    }

    public static ObjModel3D parse(InputStream in) throws IOException {
        return parse(in, null);
    }

    public static ObjModel3D parse(InputStream in, File parentDir) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        List<Float> vertices = new ArrayList<>(2048);
        List<Float> normals = new ArrayList<>(2048);
        List<Float> uvs = new ArrayList<>(2048);

        List<int[]> faces = new ArrayList<>(2048);
        List<float[]> faceNormals = new ArrayList<>(2048);
        List<float[]> faceUVs = new ArrayList<>(2048);
        List<String> faceMaterials = new ArrayList<>(2048);
        List<Integer> faceColors = new ArrayList<>(2048);

        Map<String, float[]> materialColors = new LinkedHashMap<>();
        String currentMaterial = null;

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (line.startsWith("mtllib ")) {
                String mtlFileName = line.substring(7).trim();
                if (parentDir != null) {
                    File mtlFile = new File(parentDir, mtlFileName);
                    if (mtlFile.exists() && mtlFile.isFile()) {
                        parseMtl(mtlFile, materialColors);
                    }
                }
            } else if (line.startsWith("usemtl ")) {
                currentMaterial = line.substring(7).trim();
            } else if (line.startsWith("v ")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 4) {
                    try {
                        vertices.add(Float.parseFloat(parts[1]));
                        vertices.add(Float.parseFloat(parts[2]));
                        vertices.add(Float.parseFloat(parts[3]));
                    } catch (NumberFormatException ignored) {}
                }
            } else if (line.startsWith("vn ")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 4) {
                    try {
                        normals.add(Float.parseFloat(parts[1]));
                        normals.add(Float.parseFloat(parts[2]));
                        normals.add(Float.parseFloat(parts[3]));
                    } catch (NumberFormatException ignored) {}
                }
            } else if (line.startsWith("vt ")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    try {
                        uvs.add(Float.parseFloat(parts[1]));
                        uvs.add(Float.parseFloat(parts[2]));
                    } catch (NumberFormatException ignored) {}
                }
            } else if (line.startsWith("f ")) {
                String[] parts = line.split("\\s+");
                if (parts.length < 4) continue; // Almeno 3 vertici

                int vertCountTotal = vertices.size() / 3;
                int normCountTotal = normals.size() / 3;
                int uvCountTotal = uvs.size() / 2;

                int[] faceIndices = new int[parts.length - 1];
                int[] faceNormIndices = new int[parts.length - 1];
                int[] faceUvIndices = new int[parts.length - 1];
                boolean hasNormals = true;
                boolean hasUVs = true;

                for (int i = 1; i < parts.length; i++) {
                    String part = parts[i];
                    String[] sub = part.split("/");

                    // Indice vertice (1-based o relativo)
                    int vIdx = Integer.parseInt(sub[0]);
                    if (vIdx < 0) {
                        vIdx = vertCountTotal + vIdx;
                    } else {
                        vIdx = vIdx - 1;
                    }
                    faceIndices[i - 1] = Math.max(0, Math.min(vertCountTotal - 1, vIdx));

                    // Indice UV (sub[1])
                    if (sub.length >= 2 && !sub[1].isEmpty()) {
                        int tIdx = Integer.parseInt(sub[1]);
                        if (tIdx < 0) {
                            tIdx = uvCountTotal + tIdx;
                        } else {
                            tIdx = tIdx - 1;
                        }
                        faceUvIndices[i - 1] = Math.max(0, Math.min(uvCountTotal - 1, tIdx));
                    } else {
                        hasUVs = false;
                    }

                    // Indice normale (sub[2])
                    if (sub.length >= 3 && !sub[2].isEmpty()) {
                        int nIdx = Integer.parseInt(sub[2]);
                        if (nIdx < 0) {
                            nIdx = normCountTotal + nIdx;
                        } else {
                            nIdx = nIdx - 1;
                        }
                        faceNormIndices[i - 1] = Math.max(0, Math.min(normCountTotal - 1, nIdx));
                    } else {
                        hasNormals = false;
                    }
                }

                // Risolvi colore per faccia
                int faceColor = 0x508CBE; // Default tech blue
                if (currentMaterial != null && materialColors.containsKey(currentMaterial)) {
                    float[] rgb = materialColors.get(currentMaterial);
                    int r = Math.max(0, Math.min(255, Math.round(rgb[0] * 255.0f)));
                    int g = Math.max(0, Math.min(255, Math.round(rgb[1] * 255.0f)));
                    int b = Math.max(0, Math.min(255, Math.round(rgb[2] * 255.0f)));
                    faceColor = (r << 16) | (g << 8) | b;
                }

                // Se la faccia ha 3 o 4 vertici, la teniamo direttamente
                if (faceIndices.length <= 4) {
                    faces.add(faceIndices);
                    faceMaterials.add(currentMaterial);
                    faceColors.add(faceColor);

                    if (hasNormals && faceNormIndices.length > 0 && normCountTotal > 0) {
                        int n0 = faceNormIndices[0];
                        faceNormals.add(new float[]{
                            normals.get(n0 * 3),
                            normals.get(n0 * 3 + 1),
                            normals.get(n0 * 3 + 2)
                        });
                    } else {
                        faceNormals.add(new float[]{0, 1, 0});
                    }

                    if (hasUVs && uvCountTotal > 0) {
                        float[] uvsForFace = new float[faceIndices.length * 2];
                        for (int vi = 0; vi < faceIndices.length; vi++) {
                            int ui = faceUvIndices[vi];
                            uvsForFace[vi * 2] = uvs.get(ui * 2);
                            uvsForFace[vi * 2 + 1] = uvs.get(ui * 2 + 1);
                        }
                        faceUVs.add(uvsForFace);
                    } else {
                        faceUVs.add(null);
                    }
                } else {
                    // Triangolazione per poligoni con più di 4 vertici (fan triangulation)
                    for (int i = 1; i < faceIndices.length - 1; i++) {
                        faces.add(new int[]{faceIndices[0], faceIndices[i], faceIndices[i + 1]});
                        faceMaterials.add(currentMaterial);
                        faceColors.add(faceColor);

                        if (hasNormals && normCountTotal > 0) {
                            int n0 = faceNormIndices[0];
                            faceNormals.add(new float[]{
                                normals.get(n0 * 3),
                                normals.get(n0 * 3 + 1),
                                normals.get(n0 * 3 + 2)
                            });
                        } else {
                            faceNormals.add(new float[]{0, 1, 0});
                        }

                        if (hasUVs && uvCountTotal > 0) {
                            int u0 = faceUvIndices[0];
                            int u1 = faceUvIndices[i];
                            int u2 = faceUvIndices[i + 1];
                            faceUVs.add(new float[]{
                                uvs.get(u0 * 2), uvs.get(u0 * 2 + 1),
                                uvs.get(u1 * 2), uvs.get(u1 * 2 + 1),
                                uvs.get(u2 * 2), uvs.get(u2 * 2 + 1)
                            });
                        } else {
                            faceUVs.add(null);
                        }
                    }
                }
            }
        }

        float[] vArr = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            vArr[i] = vertices.get(i);
        }

        int[][] fArr = faces.toArray(new int[0][]);
        float[][] fnArr = faceNormals.toArray(new float[0][]);
        int[] fcArr = new int[faceColors.size()];
        for (int i = 0; i < faceColors.size(); i++) {
            fcArr[i] = faceColors.get(i);
        }
        float[][] fUvArr = faceUVs.toArray(new float[0][]);
        String[] fmArr = faceMaterials.toArray(new String[0]);

        return new ObjModel3D(vArr, fArr, fnArr, fcArr, fUvArr, materialColors, fmArr);
    }

    private static void parseMtl(File mtlFile, Map<String, float[]> materialColors) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(mtlFile), StandardCharsets.UTF_8))) {
            String line;
            String curMtl = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (line.startsWith("newmtl ")) {
                    curMtl = line.substring(7).trim();
                } else if (line.startsWith("Kd ") && curMtl != null) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 4) {
                        try {
                            float r = Math.max(0.0f, Math.min(1.0f, Float.parseFloat(parts[1])));
                            float g = Math.max(0.0f, Math.min(1.0f, Float.parseFloat(parts[2])));
                            float b = Math.max(0.0f, Math.min(1.0f, Float.parseFloat(parts[3])));
                            materialColors.put(curMtl, new float[]{r, g, b});
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Impossibile leggere file MTL " + mtlFile.getName() + ": " + ex.getMessage());
        }
    }
}

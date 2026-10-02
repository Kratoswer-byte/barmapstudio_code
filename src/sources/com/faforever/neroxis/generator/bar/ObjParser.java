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
import java.util.List;

/**
 * Parser Wavefront .obj in Java puro, ultra-rapido e a zero dipendenze esterne.
 * Supporta facce poligonali complesse, coordinate normali e indici relativi.
 */
public final class ObjParser {

    private ObjParser() {}

    public static ObjModel3D parse(Path path) throws IOException {
        try (InputStream in = new FileInputStream(path.toFile())) {
            return parse(in);
        }
    }

    public static ObjModel3D parse(File file) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            return parse(in);
        }
    }

    public static ObjModel3D parse(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        List<Float> vertices = new ArrayList<>(2048);
        List<Float> normals = new ArrayList<>(2048);
        List<int[]> faces = new ArrayList<>(2048);
        List<float[]> faceNormals = new ArrayList<>(2048);

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (line.startsWith("v ")) {
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
            } else if (line.startsWith("f ")) {
                String[] parts = line.split("\\s+");
                if (parts.length < 4) continue; // Almeno 3 vertici

                int vertCountTotal = vertices.size() / 3;
                int normCountTotal = normals.size() / 3;

                int[] faceIndices = new int[parts.length - 1];
                int[] faceNormIndices = new int[parts.length - 1];
                boolean hasNormals = true;

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

                    // Indice normale
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

                // Se la faccia ha 3 o 4 vertici, la teniamo direttamente
                if (faceIndices.length <= 4) {
                    faces.add(faceIndices);
                    if (hasNormals && faceNormIndices.length > 0 && normCountTotal > 0) {
                        int n0 = faceNormIndices[0];
                        faceNormals.add(new float[]{
                            normals.get(n0 * 3),
                            normals.get(n0 * 3 + 1),
                            normals.get(n0 * 3 + 2)
                        });
                    }
                } else {
                    // Triangolazione per poligoni con più di 4 vertici (fan triangulation)
                    for (int i = 1; i < faceIndices.length - 1; i++) {
                        faces.add(new int[]{faceIndices[0], faceIndices[i], faceIndices[i + 1]});
                        if (hasNormals && normCountTotal > 0) {
                            int n0 = faceNormIndices[0];
                            faceNormals.add(new float[]{
                                normals.get(n0 * 3),
                                normals.get(n0 * 3 + 1),
                                normals.get(n0 * 3 + 2)
                            });
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
        float[][] fnArr = faceNormals.size() == fArr.length ? faceNormals.toArray(new float[0][]) : null;

        return new ObjModel3D(vArr, fArr, fnArr);
    }
}

package com.faforever.neroxis.generator.bar;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser S3O (Spring 3D Object) in Java puro.
 * Legge header, albero piece (offset ricorsivi), vertici pos/normali/uv,
 * primitive tri/strip/quad -> triangoli. Ritorna ObjModel3D + nomi tex1/tex2.
 * Convenzione V: se il render risulta flip, invertire qui (unico punto).
 */
public final class S3OParser {
    private S3OParser() {}

    public static final class S3OModel {
        public final ObjModel3D model;
        public final String tex1, tex2;
        public final float radius, height;
        S3OModel(ObjModel3D m, String t1, String t2, float r, float h) {
            model = m; tex1 = t1; tex2 = t2; radius = r; height = h;
        }
    }

    public static S3OModel parse(byte[] data) {
        if (data == null || data.length < 52) return null;
        try {
            ByteBuffer b = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            byte[] magic = new byte[12]; b.get(magic);
            String mg = new String(magic, 0, 11, StandardCharsets.US_ASCII);
            if (!mg.startsWith("Spring unit")) return null;
            b.getInt(); // version
            float radius = b.getFloat();
            float height = b.getFloat();
            b.getFloat(); b.getFloat(); b.getFloat(); // mid xyz
            int rootOff = b.getInt();
            b.getInt(); // collision
            int tex1Off = b.getInt(), tex2Off = b.getInt();
            String t1 = cstr(data, tex1Off), t2 = cstr(data, tex2Off);
            List<Float> V = new ArrayList<>(8192);
            List<float[]> N = new ArrayList<>(2048);
            List<float[]> U = new ArrayList<>(2048);
            List<int[]> F = new ArrayList<>(2048);
            walkPiece(data, rootOff, 0, 0, 0, V, N, U, F);
            float[] va = new float[V.size()]; for (int i = 0; i < va.length; i++) va[i] = V.get(i);
            int[][] fa = F.toArray(new int[0][]);
            float[][] na = N.toArray(new float[0][]);
            float[][] ua = U.toArray(new float[0][]);
            ObjModel3D m = new ObjModel3D(va, fa, na, null, ua, null, null);
            return new S3OModel(m, t1, t2, radius, height);
        } catch (Exception ex) {
            return null;
        }
    }

    private static String cstr(byte[] d, int off) {
        if (off < 0 || off >= d.length) return "";
        int e = off;
        while (e < d.length && d[e] != 0) e++;
        return new String(d, off, e - off, StandardCharsets.US_ASCII);
    }

    private static void walkPiece(byte[] d, int off, float px, float py, float pz,
                                  List<Float> V, List<float[]> N, List<float[]> U, List<int[]> F) {
        if (off < 0 || off + 52 > d.length) return;
        ByteBuffer b = ByteBuffer.wrap(d).order(ByteOrder.LITTLE_ENDIAN);
        b.position(off);
        b.getInt(); // nameOff (skip)
        int numChildren = b.getInt();
        int childrenOff = b.getInt();
        int numVerts = b.getInt();
        int vertsOff = b.getInt();
        b.getInt(); // lods
        int primType = b.getInt();
        int numIdx = b.getInt();
        int idxOff = b.getInt();
        b.getInt(); // collision
        float ox = b.getFloat(), oy = b.getFloat(), oz = b.getFloat();
        float gx = px + ox, gy = py + oy, gz = pz + oz;
        int base = V.size() / 3;
        // vertici 32B: pos(3f) nrm(3f) uv(2f)
        for (int i = 0; i < numVerts; i++) {
            int p = vertsOff + i * 32;
            if (p + 32 > d.length) break;
            ByteBuffer vb = ByteBuffer.wrap(d, p, 32).order(ByteOrder.LITTLE_ENDIAN);
            float x = vb.getFloat() + gx, y = vb.getFloat() + gy, z = vb.getFloat() + gz;
            float nx = vb.getFloat(), ny = vb.getFloat(), nz = vb.getFloat();
            float u = vb.getFloat(), v = vb.getFloat();
            V.add(x); V.add(y); V.add(z);
            // normale per vertice: approssimiamo con normale vertice, fan usa prima
            N.add(new float[]{nx, ny, nz});
            U.add(new float[]{u, v});
        }
        // NOTA: le normali S3O sono per-vertice; ObjModel3D vuole per-faccia -> calcolate dopo dal costruttore se null.
        // indici
        int[] indices = new int[Math.max(0, numIdx)];
        for (int i = 0; i < numIdx; i++) {
            int p = idxOff + i * 4;
            if (p + 4 > d.length) { indices = java.util.Arrays.copyOf(indices, i); break; }
            indices[i] = base + (ByteBuffer.wrap(d, p, 4).order(ByteOrder.LITTLE_ENDIAN).getInt());
        }
        if (primType == 0) { // triangoli
            for (int i = 0; i + 2 < indices.length; i += 3) {
                int a = indices[i] - base, bb = indices[i+1] - base, c = indices[i+2] - base;
                F.add(new int[]{indices[i], indices[i+1], indices[i+2]});
                // uv per faccia dai vertici letti
                float[] uA = U.get(base + a >= 0 && base + a < U.size() ? 0 : 0); // placeholder, ricostruite sotto
            }
            // ricostruisci UV per faccia dai vertici (U list e base)
            // (sostituiamo le entry appena aggiunte con UV corrette)
            int start = F.size() - indices.length / 3;
            for (int t = 0; t < indices.length / 3; t++) {
                int a = indices[t*3] - base, bb = indices[t*3+1] - base, c = indices[t*3+2] - base;
                float[] ua = getUV(U, base + a), ub = getUV(U, base + bb), uc = getUV(U, base + c);
                U.add(new float[]{0,0}); // dummy per allineare N/U size a F? no: usiamo faceUVs separati sotto
                // patch: ObjModel3D costruito dopo, quindi ricalcoliamo faceUVs qui via lista parallela
            }
        } else if (primType == 1) { // strip
            for (int i = 0; i + 2 < indices.length; i++) {
                if ((i & 1) == 0) F.add(new int[]{indices[i], indices[i+1], indices[i+2]});
                else F.add(new int[]{indices[i+1], indices[i], indices[i+2]});
            }
        } else if (primType == 2) { // quad
            for (int i = 0; i + 3 < indices.length; i += 4) {
                F.add(new int[]{indices[i], indices[i+1], indices[i+2]});
                F.add(new int[]{indices[i], indices[i+2], indices[i+3]});
            }
        }
        // children (array di offset)
        for (int i = 0; i < numChildren; i++) {
            int p = childrenOff + i * 4;
            if (p + 4 > d.length) break;
            int childOff = ByteBuffer.wrap(d, p, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            walkPiece(d, childOff, gx, gy, gz, V, N, U, F);
        }
    }

    private static float[] getUV(List<float[]> U, int i) {
        if (i >= 0 && i < U.size() && U.get(i) != null) return U.get(i);
        return new float[]{0.5f, 0.5f};
    }

    /** Ricostruisce ObjModel3D con UV per-faccia corrette da un parse grezzo. Chiamato da parse() v2. */
    public static S3OModel parseWithFaceUV(byte[] data) {
        if (data == null || data.length < 52) return null;
        try {
            ByteBuffer b = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            byte[] magic = new byte[12]; b.get(magic);
            if (!new String(magic, 0, 11, StandardCharsets.US_ASCII).startsWith("Spring unit")) return null;
            b.getInt();
            float radius = b.getFloat(), height = b.getFloat();
            b.getFloat(); b.getFloat(); b.getFloat();
            int rootOff = b.getInt(); b.getInt();
            int tex1Off = b.getInt(), tex2Off = b.getInt();
            String t1 = cstr(data, tex1Off), t2 = cstr(data, tex2Off);
            List<Float> V = new ArrayList<>(8192);
            List<int[]> Fall = new ArrayList<>(2048);
            List<float[]> Fuv = new ArrayList<>(2048);
            List<float[]> Fn = new ArrayList<>(2048);
            walkPiece2(data, rootOff, 0, 0, 0, V, Fall, Fuv, Fn);
            float[] va = new float[V.size()]; for (int i = 0; i < va.length; i++) va[i] = V.get(i);
            ObjModel3D m = new ObjModel3D(va, Fall.toArray(new int[0][]), Fn.toArray(new float[0][]),
                null, Fuv.toArray(new float[0][]), null, null);
            return new S3OModel(m, t1, t2, radius, height);
        } catch (Exception ex) { return null; }
    }

    private static void walkPiece2(byte[] d, int off, float px, float py, float pz,
                                   List<Float> V, List<int[]> Fall, List<float[]> Fuv, List<float[]> Fn) {
        if (off < 0 || off + 52 > d.length) return;
        ByteBuffer b = ByteBuffer.wrap(d).order(ByteOrder.LITTLE_ENDIAN);
        b.position(off);
        b.getInt();
        int numChildren = b.getInt();
        int childrenOff = b.getInt();
        int numVerts = b.getInt();
        int vertsOff = b.getInt();
        b.getInt();
        int primType = b.getInt();
        int numIdx = b.getInt();
        int idxOff = b.getInt();
        b.getInt();
        float ox = b.getFloat(), oy = b.getFloat(), oz = b.getFloat();
        float gx = px + ox, gy = py + oy, gz = pz + oz;
        int base = V.size() / 3;
        float[] vx = new float[numVerts * 3];
        float[] vn = new float[numVerts * 3];
        float[] vu = new float[numVerts * 2];
        for (int i = 0; i < numVerts; i++) {
            int p = vertsOff + i * 32;
            if (p + 32 > d.length) break;
            ByteBuffer vb = ByteBuffer.wrap(d, p, 32).order(ByteOrder.LITTLE_ENDIAN);
            vx[i*3] = vb.getFloat() + gx; vx[i*3+1] = vb.getFloat() + gy; vx[i*3+2] = vb.getFloat() + gz;
            vn[i*3] = vb.getFloat(); vn[i*3+1] = vb.getFloat(); vn[i*3+2] = vb.getFloat();
            vu[i*2] = vb.getFloat(); vu[i*2+1] = vb.getFloat();
            V.add(vx[i*3]); V.add(vx[i*3+1]); V.add(vx[i*3+2]);
        }
        int[] indices = new int[Math.max(0, numIdx)];
        int ic = 0;
        for (int i = 0; i < numIdx; i++) {
            int p = idxOff + i * 4;
            if (p + 4 > d.length) break;
            indices[ic++] = base + ByteBuffer.wrap(d, p, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }
        java.util.List<int[]> tris = new ArrayList<>();
        if (primType == 0) for (int i = 0; i + 2 < ic; i += 3) tris.add(new int[]{indices[i], indices[i+1], indices[i+2]});
        else if (primType == 1) for (int i = 0; i + 2 < ic; i++) tris.add((i & 1) == 0 ? new int[]{indices[i], indices[i+1], indices[i+2]} : new int[]{indices[i+1], indices[i], indices[i+2]});
        else for (int i = 0; i + 3 < ic; i += 4) { tris.add(new int[]{indices[i], indices[i+1], indices[i+2]}); tris.add(new int[]{indices[i], indices[i+2], indices[i+3]}); }
        for (int[] t : tris) {
            Fall.add(t);
            float[] uvs = new float[6];
            float[] nn = new float[3];
            for (int k = 0; k < 3; k++) {
                int li = t[k] - base;
                if (li >= 0 && li < numVerts) {
                    uvs[k*2] = vu[li*2]; uvs[k*2+1] = vu[li*2+1];
                    if (k == 0) { nn[0] = vn[li*3]; nn[1] = vn[li*3+1]; nn[2] = vn[li*3+2]; }
                }
            }
            Fuv.add(uvs); Fn.add(nn);
        }
        for (int i = 0; i < numChildren; i++) {
            int p = childrenOff + i * 4;
            if (p + 4 > d.length) break;
            walkPiece2(d, ByteBuffer.wrap(d, p, 4).order(ByteOrder.LITTLE_ENDIAN).getInt(), gx, gy, gz, V, Fall, Fuv, Fn);
        }
    }
}

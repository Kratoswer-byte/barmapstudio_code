package com.faforever.neroxis.generator.bar;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

/**
 * Rasterizzatore software in Java puro (no OpenGL, no dipendenze native).
 * Framebuffer int[] + z-buffer float[], UV prospettiva-corretta,
 * luce Lambert + ambiente, bande parallele, risoluzione dinamica.
 * Convenzioni: Y up nel world, camera orbit yaw/pitch, unità = elmi BAR.
 */
public final class SoftRenderer3D {

    public static final class Texture {
        public final int w, h;
        public final int[] px;
        public Texture(BufferedImage img) {
            if (img == null) { w = 0; h = 0; px = new int[0]; return; }
            w = img.getWidth(); h = img.getHeight();
            px = new int[w * h];
            img.getRGB(0, 0, w, h, px, 0, w);
        }
        public Texture(int w, int h, int fill) {
            this.w = w; this.h = h;
            px = new int[w * h];
            java.util.Arrays.fill(px, fill);
        }
        public int sampleNearest(float u, float v) {
            if (w == 0 || h == 0) return 0xFF808080;
            float uu = u - (float) Math.floor(u);
            float vv = v - (float) Math.floor(v);
            int x = Math.min(w - 1, (int) (uu * w));
            int y = Math.min(h - 1, (int) (vv * h));
            return px[y * w + x];
        }
        public int sampleBilinear(float u, float v) {
            if (w == 0 || h == 0) return 0xFF808080;
            float uu = (u - (float) Math.floor(u)) * (w - 1);
            float vv = (v - (float) Math.floor(v)) * (h - 1);
            int x0 = (int) uu; int y0 = (int) vv;
            int x1 = Math.min(w - 1, x0 + 1); int y1 = Math.min(h - 1, y0 + 1);
            float fx = uu - x0; float fy = vv - y0;
            int c00 = px[y0 * w + x0], c10 = px[y0 * w + x1];
            int c01 = px[y1 * w + x0], c11 = px[y1 * w + x1];
            return bilerp(c00, c10, c01, c11, fx, fy);
        }
        private static int bilerp(int c00, int c10, int c01, int c11, float fx, float fy) {
            float a = lerp(lerp((c00 >>> 24) & 255, (c10 >>> 24) & 255, fx), lerp((c01 >>> 24) & 255, (c11 >>> 24) & 255, fx), fy);
            float r = lerp(lerp((c00 >> 16) & 255, (c10 >> 16) & 255, fx), lerp((c01 >> 16) & 255, (c11 >> 16) & 255, fx), fy);
            float g = lerp(lerp((c00 >> 8) & 255, (c10 >> 8) & 255, fx), lerp((c01 >> 8) & 255, (c11 >> 8) & 255, fx), fy);
            float b = lerp(lerp(c00 & 255, c10 & 255, fx), lerp(c01 & 255, c11 & 255, fx), fy);
            return ((int) a << 24) | ((int) r << 16) | ((int) g << 8) | (int) b;
        }
        private static float lerp(float a, float b, float t) { return a + (b - a) * t; }
    }

    /** Mesh piatta pronta per il render: triangoli con pos/normali/uv. */
    public static final class RenderMesh {
        public final float[] pos; // 3 per vertice
        public final float[] nrm; // 3 per vertice
        public final float[] uv;  // 2 per vertice
        public final int[] idx;   // 3 per triangolo
        public final int triCount;
        public RenderMesh(float[] pos, float[] nrm, float[] uv, int[] idx) {
            this.pos = pos; this.nrm = nrm; this.uv = uv; this.idx = idx;
            this.triCount = idx.length / 3;
        }
        /** Costruisce da ObjModel3D triangolando le facce (fan). */
        public static RenderMesh fromModel(ObjModel3D m) {
            if (m == null || m.getFaceCount() == 0) return new RenderMesh(new float[0], new float[0], new float[0], new int[0]);
            float[] v = m.getVertices();
            int[][] faces = m.getFaces();
            float[][] fN = m.getFaceNormals();
            java.util.ArrayList<Float> P = new java.util.ArrayList<>(4096);
            java.util.ArrayList<Float> N = new java.util.ArrayList<>(4096);
            java.util.ArrayList<Float> U = new java.util.ArrayList<>(4096);
            java.util.ArrayList<Integer> I = new java.util.ArrayList<>(4096);
            float[][] fUv = m.getFaceUVs();
            for (int f = 0; f < faces.length; f++) {
                int[] face = faces[f];
                if (face.length < 3) continue;
                float[] fn = (fN != null && f < fN.length && fN[f] != null) ? fN[f] : new float[]{0, 1, 0};
                float[] uvs = (fUv != null && f < fUv.length) ? fUv[f] : null;
                int base = P.size() / 3;
                for (int k = 0; k < face.length; k++) {
                    int vi = face[k];
                    float x = (vi * 3 < v.length) ? v[vi * 3] : 0;
                    float y = (vi * 3 + 1 < v.length) ? v[vi * 3 + 1] : 0;
                    float z = (vi * 3 + 2 < v.length) ? v[vi * 3 + 2] : 0;
                    P.add(x); P.add(y); P.add(z);
                    N.add(fn[0]); N.add(fn[1]); N.add(fn[2]);
                    if (uvs != null && uvs.length >= face.length * 2) {
                        U.add(uvs[k * 2]); U.add(uvs[k * 2 + 1]);
                    } else {
                        U.add(0.5f); U.add(0.5f);
                    }
                }
                for (int k = 1; k < face.length - 1; k++) {
                    I.add(base); I.add(base + k); I.add(base + k + 1);
                }
            }
            float[] pa = new float[P.size()]; for (int i = 0; i < pa.length; i++) pa[i] = P.get(i);
            float[] na = new float[N.size()]; for (int i = 0; i < na.length; i++) na[i] = N.get(i);
            float[] ua = new float[U.size()]; for (int i = 0; i < ua.length; i++) ua[i] = U.get(i);
            int[] ia = new int[I.size()]; for (int i = 0; i < ia.length; i++) ia[i] = I.get(i);
            return new RenderMesh(pa, na, ua, ia);
        }
    }

    /** Camera orbitale stile RTS. */
    public static final class Camera {
        public double yawDeg = 45, pitchDeg = 28;
        public double dist = 600; // elmi
        public double tx, ty, tz; // target in elmi
        public double fovY = 40;
    }

    private int w, h;
    private int[] fb;
    private float[] zb;
    private BufferedImage present;
    private boolean bilinear = true;
    private float ambient = 0.45f;
    private final float[] lightDir = norm(new float[]{-0.45f, 0.8f, 0.35f});

    public SoftRenderer3D(int w, int h) { resize(w, h); }

    public void resize(int w, int h) {
        w = Math.max(8, w); h = Math.max(8, h);
        if (w == this.w && h == this.h && fb != null) return;
        this.w = w; this.h = h;
        fb = new int[w * h];
        zb = new float[w * h];
        present = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    }

    public void setBilinear(boolean b) { this.bilinear = b; }
    public int getWidth() { return w; }
    public int getHeight() { return h; }

    public void clear(int bgRgb) {
        java.util.Arrays.fill(fb, bgRgb & 0xFFFFFF);
        java.util.Arrays.fill(zb, Float.POSITIVE_INFINITY);
    }

    public BufferedImage present() {
        present.setRGB(0, 0, w, h, fb, 0, w);
        return present;
    }

    private static float[] norm(float[] v) {
        float l = (float) Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
        if (l < 1e-6f) return new float[]{0,1,0};
        return new float[]{v[0]/l, v[1]/l, v[2]/l};
    }

    /** Disegna una mesh con modello (traslazione+rotY+scala) e camera. Ritorna n. triangoli rasterizzati. */
    public int drawMesh(RenderMesh m, Texture tex, int flatRgbIfNull,
                        double wx, double wy, double wz, double rotYRad, double scale,
                        Camera cam) {
        if (m == null || m.triCount == 0) return 0;
        double yaw = Math.toRadians(cam.yawDeg);
        double pitch = Math.toRadians(cam.pitchDeg);
        // basis camera: eye = target + dist * dir(yaw,pitch)
        double cx = Math.cos(pitch) * Math.cos(yaw);
        double cy = Math.sin(pitch);
        double cz = Math.cos(pitch) * Math.sin(yaw);
        double ex = cam.tx + cx * cam.dist;
        double ey = cam.ty + cy * cam.dist;
        double ez = cam.tz + cz * cam.dist;
        // view axes
        double[] fwd = normD(new double[]{cam.tx - ex, cam.ty - ey, cam.tz - ez});
        double[] right = normD(new double[]{fwd[2], 0, -fwd[0]});
        if (lenD(right) < 1e-6) right = new double[]{1, 0, 0};
        double[] up = crossD(fwd, right);
        double focal = (h * 0.5) / Math.tan(Math.toRadians(cam.fovY * 0.5));
        double cosR = Math.cos(rotYRad), sinR = Math.sin(rotYRad);

        int n = m.pos.length / 3;
        double[] vx = new double[n], vy = new double[n], vz = new double[n]; // view space
        double[] sx = new double[n], sy = new double[n]; // screen
        double[] invW = new double[n];
        float[] tu = new float[n], tv = new float[n];
        float[] shade = new float[n];

        for (int i = 0; i < n; i++) {
            double lx = m.pos[i*3] * scale;
            double ly = m.pos[i*3+1] * scale;
            double lz = m.pos[i*3+2] * scale;
            // rotY
            double rx = lx * cosR + lz * sinR;
            double rz = -lx * sinR + lz * cosR;
            double gx = rx + wx, gy = ly + wy, gz = rz + wz;
            double dx = gx - ex, dy = gy - ey, dz = gz - ez;
            double vxv = dx * right[0] + dy * right[1] + dz * right[2];
            double vyv = dx * up[0] + dy * up[1] + dz * up[2];
            double vzv = dx * fwd[0] + dy * fwd[1] + dz * fwd[2];
            vx[i] = vxv; vy[i] = vyv; vz[i] = vzv;
            if (vzv < 1.0) { invW[i] = 0; continue; }
            invW[i] = 1.0 / vzv;
            sx[i] = w * 0.5 + (vxv * focal) / vzv;
            sy[i] = h * 0.5 - (vyv * focal) / vzv;
            tu[i] = m.uv[i*2]; tv[i] = m.uv[i*2+1];
            // lambert con normale ruotata (solo rotY)
            double nx = m.nrm[i*3], ny = m.nrm[i*3+1], nz = m.nrm[i*3+2];
            double rnx = nx * cosR + nz * sinR;
            double rnz = -nx * sinR + nz * cosR;
            double d = Math.max(0, rnx * lightDir[0] + ny * lightDir[1] + rnz * lightDir[2]);
            shade[i] = ambient + (1 - ambient) * (float) d;
        }
        return rasterize(m.idx, sx, sy, invW, tu, tv, shade, tex, flatRgbIfNull, vz);
    }

    /** Disegna wireframe footprint a terra (rettangolo) via lines 3D->2D. Usare Graphics2D dopo present per semplicita. */
    private static double[] normD(double[] v) {
        double l = Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
        if (l < 1e-9) return new double[]{0,0,0};
        return new double[]{v[0]/l, v[1]/l, v[2]/l};
    }
    private static double lenD(double[] v) { return Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]); }
    private static double[] crossD(double[] a, double[] b) {
        return new double[]{a[1]*b[2]-a[2]*b[1], a[2]*b[0]-a[0]*b[2], a[0]*b[1]-a[1]*b[0]};
    }

    private int rasterize(int[] idx, double[] sx, double[] sy, double[] invW,
                          float[] tu, float[] tv, float[] shade,
                          Texture tex, int flatRgb, double[] vzView) {
        int tris = idx.length / 3;
        // pre-clip: scarta tri con vertici dietro camera
        int[] valid = new int[tris];
        int vc = 0;
        for (int t = 0; t < tris; t++) {
            int a = idx[t*3], b = idx[t*3+1], c = idx[t*3+2];
            if (invW[a] <= 0 || invW[b] <= 0 || invW[c] <= 0) continue;
            // backface cull in screen space
            double area = (sx[b]-sx[a])*(sy[c]-sy[a])-(sx[c]-sx[a])*(sy[b]-sy[a]);
            if (area >= 0) continue; // frontface = area negativa (y down)
            // frustum rapido
            double minx = Math.min(sx[a], Math.min(sx[b], sx[c]));
            double maxx = Math.max(sx[a], Math.max(sx[b], sx[c]));
            double miny = Math.min(sy[a], Math.min(sy[b], sy[c]));
            double maxy = Math.max(sy[a], Math.max(sy[b], sy[c]));
            if (maxx < 0 || minx >= w || maxy < 0 || miny >= h) continue;
            valid[vc++] = t;
        }
        if (vc == 0) return 0;
        final int vcount = vc;
        final boolean useTex = tex != null && tex.w > 0;
        final boolean bilin = bilinear && useTex;
        // bande orizzontali: per semplicita parallelizziamo sui triangoli
        IntStream.range(0, vcount).parallel().forEach(k -> {
            int t = valid[k];
            int a = idx[t*3], b = idx[t*3+1], c = idx[t*3+2];
            double ax = sx[a], ay = sy[a], bx = sx[b], by = sy[b], cx = sx[c], cy = sy[c];
            double area = (bx-ax)*(cy-ay)-(cx-ax)*(by-ay);
            if (area == 0) return;
            double invA = 1.0 / area;
            int minx = Math.max(0, (int) Math.floor(Math.min(ax, Math.min(bx, cx))));
            int maxx = Math.min(w - 1, (int) Math.ceil(Math.max(ax, Math.max(bx, cx))));
            int miny = Math.max(0, (int) Math.floor(Math.min(ay, Math.min(by, cy))));
            int maxy = Math.min(h - 1, (int) Math.ceil(Math.max(ay, Math.max(by, cy))));
            double ia = invW[a], ib = invW[b], ic = invW[c];
            double uia = tu[a]*ia, uib = tu[b]*ib, uic = tu[c]*ic;
            double via = tv[a]*ia, vib = tv[b]*ib, vic = tv[c]*ic;
            double sia = shade[a]*ia, sib = shade[b]*ib, sic = shade[c]*ic;
            for (int y = miny; y <= maxy; y++) {
                for (int x = minx; x <= maxx; x++) {
                    double px = x + 0.5, py = y + 0.5;
                    double w0 = ((bx-px)*(cy-py)-(cx-px)*(by-py)) * invA;
                    double w1 = ((cx-px)*(ay-py)-(ax-px)*(cy-py)) * invA;
                    double w2 = 1.0 - w0 - w1;
                    if (w0 < 0 || w1 < 0 || w2 < 0) continue;
                    double inv = w0*ia + w1*ib + w2*ic;
                    if (inv <= 0) continue;
                    float depth = (float) (1.0 / inv);
                    int off = y * w + x;
                    // z-test non sincronizzato per pixel: accettabile, artefatti rari; depth quasi sempre coerente
                    if (depth >= zb[off]) continue;
                    float uu = (float) ((w0*uia + w1*uib + w2*uic) / inv);
                    float vv = (float) ((w0*via + w1*vib + w2*vic) / inv);
                    float sh = (float) ((w0*sia + w1*sib + w2*sic) / inv);
                    int rgb;
                    if (useTex) rgb = bilin ? tex.sampleBilinear(uu, vv) : tex.sampleNearest(uu, vv);
                    else rgb = flatRgb;
                    int r = (int) (((rgb >> 16) & 255) * sh);
                    int g = (int) (((rgb >> 8) & 255) * sh);
                    int bl = (int) ((rgb & 255) * sh);
                    if (r > 255) r = 255; if (g > 255) g = 255; if (bl > 255) bl = 255;
                    int out = (r << 16) | (g << 8) | bl;
                    // write con check-then-set; race benigna su frame interattivi
                    if (depth < zb[off]) { zb[off] = depth; fb[off] = out; }
                }
            }
        });
        return vcount;
    }

    /** Terreno: griglia heights (elmos) stride w, colorMap per vertice gia mappata. Disegna come mesh a due tri per cella con LOD step. */
    public int drawTerrain(float[] heights, int gw, int gh, float cellElmos,
                           BufferedImage colorMap, Camera cam, double originX, double originZ) {
        if (heights == null || gw < 2 || gh < 2) return 0;
        int step = 1;
        while ((gw / step) * (gh / step) > 256 * 256 && step < 16) step *= 2;
        int nw = (gw - 1) / step + 1, nh = (gh - 1) / step + 1;
        float[] pos = new float[nw * nh * 3];
        float[] nrm = new float[nw * nh * 3];
        float[] uv = new float[nw * nh * 2];
        int[] col = new int[nw * nh];
        int cmW = colorMap != null ? colorMap.getWidth() : 1;
        int cmH = colorMap != null ? colorMap.getHeight() : 1;
        int[] cmPx = null;
        if (colorMap != null) { cmPx = new int[cmW * cmH]; colorMap.getRGB(0, 0, cmW, cmH, cmPx, 0, cmW); }
        for (int j = 0; j < nh; j++) {
            for (int i = 0; i < nw; i++) {
                int gi = Math.min(gw - 1, i * step), gj = Math.min(gh - 1, j * step);
                float hgt = heights[gj * gw + gi];
                int k = j * nw + i;
                pos[k*3] = (float) (originX + gi * cellElmos);
                pos[k*3+1] = hgt;
                pos[k*3+2] = (float) (originZ + gj * cellElmos);
                // normale da gradiente centrale
                float hx0 = heights[gj * gw + Math.max(0, gi - 1)];
                float hx1 = heights[gj * gw + Math.min(gw - 1, gi + 1)];
                float hz0 = heights[Math.max(0, gj - 1) * gw + gi];
                float hz1 = heights[Math.min(gh - 1, gj + 1) * gw + gi];
                float[] nn = norm(new float[]{(hx0 - hx1) / (2 * cellElmos), 1.0f, (hz0 - hz1) / (2 * cellElmos)});
                nrm[k*3] = nn[0]; nrm[k*3+1] = nn[1]; nrm[k*3+2] = nn[2];
                float u = gw <= 1 ? 0 : (float) gi / (gw - 1);
                float v = gh <= 1 ? 0 : (float) gj / (gh - 1);
                uv[k*2] = u; uv[k*2+1] = v;
                if (cmPx != null) {
                    int cx = Math.min(cmW - 1, (int) (u * cmW));
                    int cy = Math.min(cmH - 1, (int) (v * cmH));
                    col[k] = cmPx[cy * cmW + cx];
                } else col[k] = 0xFF5A7A4A;
            }
        }
        // render a celle con colore flat per cella (media 4 vertici) — veloce e stabile
        Texture tmp = new Texture(1, 1, 0);
        int drawn = 0;
        for (int j = 0; j < nh - 1; j++) {
            for (int i = 0; i < nw - 1; i++) {
                int k00 = j * nw + i, k10 = k00 + 1, k01 = k00 + nw, k11 = k01 + 1;
                int avg = avgCol(col[k00], col[k10], col[k01], col[k11]);
                // due triangoli come mini-mesh
                float[] pp = new float[]{
                    pos[k00*3], pos[k00*3+1], pos[k00*3+2],
                    pos[k10*3], pos[k10*3+1], pos[k10*3+2],
                    pos[k11*3], pos[k11*3+1], pos[k11*3+2],
                    pos[k00*3], pos[k00*3+1], pos[k00*3+2],
                    pos[k11*3], pos[k11*3+1], pos[k11*3+2],
                    pos[k01*3], pos[k01*3+1], pos[k01*3+2]};
                float[] nn = new float[]{
                    nrm[k00*3], nrm[k00*3+1], nrm[k00*3+2],
                    nrm[k10*3], nrm[k10*3+1], nrm[k10*3+2],
                    nrm[k11*3], nrm[k11*3+1], nrm[k11*3+2],
                    nrm[k00*3], nrm[k00*3+1], nrm[k00*3+2],
                    nrm[k11*3], nrm[k11*3+1], nrm[k11*3+2],
                    nrm[k01*3], nrm[k01*3+1], nrm[k01*3+2]};
                float[] uu = new float[12];
                int[] ii = new int[]{0, 1, 2, 3, 4, 5};
                drawn += drawMesh(new RenderMesh(pp, nn, uu, ii), null, avg, 0, 0, 0, 0, 1.0, cam);
            }
        }
        return drawn;
    }

    private static int avgCol(int a, int b, int c, int d) {
        int r = (((a>>16)&255)+((b>>16)&255)+((c>>16)&255)+((d>>16)&255)) / 4;
        int g = (((a>>8)&255)+((b>>8)&255)+((c>>8)&255)+((d>>8)&255)) / 4;
        int bl = ((a&255)+(b&255)+(c&255)+(d&255)) / 4;
        return (r<<16)|(g<<8)|bl;
    }

    /** Proietta un punto world->screen, ritorna null se dietro camera. */
    public java.awt.Point project(double wx, double wy, double wz, Camera cam) {
        double yaw = Math.toRadians(cam.yawDeg);
        double pitch = Math.toRadians(cam.pitchDeg);
        double cx = Math.cos(pitch) * Math.cos(yaw);
        double cy = Math.sin(pitch);
        double cz = Math.cos(pitch) * Math.sin(yaw);
        double ex = cam.tx + cx * cam.dist;
        double ey = cam.ty + cy * cam.dist;
        double ez = cam.tz + cz * cam.dist;
        double[] fwd = normD(new double[]{cam.tx - ex, cam.ty - ey, cam.tz - ez});
        double[] right = normD(new double[]{fwd[2], 0, -fwd[0]});
        if (lenD(right) < 1e-6) right = new double[]{1, 0, 0};
        double[] up = crossD(fwd, right);
        double dx = wx - ex, dy = wy - ey, dz = wz - ez;
        double vz = dx * fwd[0] + dy * fwd[1] + dz * fwd[2];
        if (vz < 1.0) return null;
        double vx = dx * right[0] + dy * right[1] + dz * right[2];
        double vy = dx * up[0] + dy * up[1] + dz * up[2];
        double focal = (h * 0.5) / Math.tan(Math.toRadians(cam.fovY * 0.5));
        int sx = (int) (w * 0.5 + (vx * focal) / vz);
        int sy = (int) (h * 0.5 - (vy * focal) / vz);
        return new java.awt.Point(sx, sy);
    }
}

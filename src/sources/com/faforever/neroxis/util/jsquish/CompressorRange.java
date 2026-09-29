package com.faforever.neroxis.util.jsquish;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/jsquish/CompressorRange.class */
final class CompressorRange extends CompressorColourFit {
    private final int[] closest;
    private final int[] indices;
    private final Vec[] codes;
    private final Squish.CompressionMetric metric;
    private final Vec start;
    private final Vec end;
    private float bestError;

    CompressorRange(final ColourSet colours, final Squish.CompressionType type, final Squish.CompressionMetric metric) {
        super(colours, type);
        this.closest = new int[16];
        this.indices = new int[16];
        this.codes = new Vec[]{new Vec(), new Vec(), new Vec(), new Vec()};
        this.start = new Vec();
        this.end = new Vec();
        this.metric = metric;
        this.bestError = Float.MAX_VALUE;
        int count = this.colours.getCount();
        Vec[] points = this.colours.getPoints();
        Matrix covariance = Matrix.computeWeightedCovariance(colours, new Matrix());
        Vec principle = Matrix.computePrincipleComponent(covariance);
        if (count > 0) {
            float fX = points[0].x();
            float bX = fX;
            float aX = fX;
            float fY = points[0].y();
            float bY = fY;
            float aY = fY;
            float fZ = points[0].z();
            float bZ = fZ;
            float aZ = fZ;
            float fDot = points[0].dot(principle);
            float max = fDot;
            float min = fDot;
            for (int i = 1; i < count; i++) {
                Vec p = points[i];
                float val = p.dot(principle);
                if (val < min) {
                    aX = p.x();
                    aY = p.y();
                    aZ = p.z();
                    min = val;
                } else if (val > max) {
                    bX = p.x();
                    bY = p.y();
                    bZ = p.z();
                    max = val;
                }
            }
            this.start.set(clamp(aX, 31.0f, 0.032258064f), clamp(aY, 63.0f, 0.015873017f), clamp(aZ, 31.0f, 0.032258064f));
            this.end.set(clamp(bX, 31.0f, 0.032258064f), clamp(bY, 63.0f, 0.015873017f), clamp(bZ, 31.0f, 0.032258064f));
        }
    }

    @Override // com.faforever.neroxis.util.jsquish.CompressorColourFit
    void compress3(final byte[] block, final int offset) {
        int count = this.colours.getCount();
        Vec[] points = this.colours.getPoints();
        Vec v = new Vec();
        this.codes[0].set(this.start);
        this.codes[1].set(this.end);
        this.codes[2].set(this.start).add(this.end).mul(0.5f);
        float error = 0.0f;
        for (int i = 0; i < count; i++) {
            Vec p = points[i];
            float dist = Float.MAX_VALUE;
            int index = 0;
            for (int j = 0; j < 3; j++) {
                Vec c = this.codes[j];
                v.set((p.x() - c.x()) * this.metric.r, (p.y() - c.y()) * this.metric.g, (p.z() - c.z()) * this.metric.b);
                float d = v.lengthSQ();
                if (d < dist) {
                    dist = d;
                    index = j;
                }
            }
            this.closest[i] = index;
            error += dist;
        }
        if (error < this.bestError) {
            this.colours.remapIndices(this.closest, this.indices);
            ColourBlock.writeColourBlock3(this.start, this.end, this.indices, block, offset);
            this.bestError = error;
        }
    }

    @Override // com.faforever.neroxis.util.jsquish.CompressorColourFit
    void compress4(final byte[] block, final int offset) {
        int count = this.colours.getCount();
        Vec[] points = this.colours.getPoints();
        Vec v = new Vec();
        this.codes[0].set(this.start);
        this.codes[1].set(this.end);
        this.codes[2].set(0.6666667f).mul(this.start).add(v.set(0.33333334f).mul(this.end));
        this.codes[3].set(0.33333334f).mul(this.start).add(v.set(0.6666667f).mul(this.end));
        float error = 0.0f;
        for (int i = 0; i < count; i++) {
            Vec p = points[i];
            float dist = Float.MAX_VALUE;
            int index = 0;
            for (int j = 0; j < 4; j++) {
                Vec c = this.codes[j];
                v.set((p.x() - c.x()) * this.metric.r, (p.y() - c.y()) * this.metric.g, (p.z() - c.z()) * this.metric.b);
                float d = v.lengthSQ();
                if (d < dist) {
                    dist = d;
                    index = j;
                }
            }
            this.closest[i] = index;
            error += dist;
        }
        if (error < this.bestError) {
            this.colours.remapIndices(this.closest, this.indices);
            ColourBlock.writeColourBlock4(this.start, this.end, this.indices, block, offset);
            this.bestError = error;
        }
    }
}

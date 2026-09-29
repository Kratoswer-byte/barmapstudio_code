package com.faforever.neroxis.util.jsquish;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/jsquish/CompressorCluster.class */
final class CompressorCluster extends CompressorColourFit {
    private static final int MAX_ITERATIONS = 8;
    private static final float TWO_THIRDS = 0.6666667f;
    private static final float ONE_THIRD = 0.33333334f;
    private static final float HALF = 0.5f;
    private static final float ZERO = 0.0f;
    private final float[] dps;
    private final float[] weighted;
    private final float[] weights;
    private final int[] indices;
    private final int[] bestIndices;
    private final float[] alpha;
    private final float[] beta;
    private final int[] unordered;
    private final Vec xxSum;
    private final int[] orders;
    private final Vec principle;
    private final Squish.CompressionMetric metric;
    private float bestError;

    CompressorCluster(final ColourSet colours, final Squish.CompressionType type, final Squish.CompressionMetric metric) {
        super(colours, type);
        this.dps = new float[16];
        this.weighted = new float[48];
        this.weights = new float[16];
        this.indices = new int[16];
        this.bestIndices = new int[16];
        this.alpha = new float[16];
        this.beta = new float[16];
        this.unordered = new int[16];
        this.xxSum = new Vec();
        this.orders = new int[128];
        this.bestError = Float.MAX_VALUE;
        this.metric = metric;
        Matrix covariance = Matrix.computeWeightedCovariance(colours, new Matrix());
        this.principle = Matrix.computePrincipleComponent(covariance);
    }

    @Override // com.faforever.neroxis.util.jsquish.CompressorColourFit
    void compress3(final byte[] block, final int offset) {
        int count = this.colours.getCount();
        Vec bestStart = new Vec(0.0f);
        Vec bestEnd = new Vec(0.0f);
        float currentError = this.bestError;
        Vec a = new Vec();
        Vec b = new Vec();
        constructOrdering(this.principle, 0);
        int bestIteration = 0;
        int iteration = 0;
        do {
            for (int m = 0; m < count; m++) {
                this.indices[m] = 0;
                this.alpha[m] = this.weights[m];
                this.beta[m] = 0.0f;
            }
            for (int i = count; i >= 0; i--) {
                for (int m2 = i; m2 < count; m2++) {
                    this.indices[m2] = 2;
                    float f = HALF * this.weights[m2];
                    this.beta[m2] = f;
                    this.alpha[m2] = f;
                }
                for (int j = count; j >= i; j--) {
                    if (j < count) {
                        this.indices[j] = 1;
                        this.alpha[j] = 0.0f;
                        this.beta[j] = this.weights[j];
                    }
                    float error = solveLeastSquares(a, b);
                    if (error < currentError) {
                        bestStart.set(a);
                        bestEnd.set(b);
                        System.arraycopy(this.indices, 0, this.bestIndices, 0, 16);
                        currentError = error;
                        bestIteration = iteration;
                    }
                }
            }
            if (bestIteration != iteration) {
                break;
            }
            iteration++;
            if (iteration == 8) {
                break;
            }
        } while (constructOrdering(a.set(bestEnd).sub(bestStart), iteration));
        if (currentError < this.bestError) {
            int order = 16 * bestIteration;
            for (int i2 = 0; i2 < count; i2++) {
                this.unordered[this.orders[order + i2]] = this.bestIndices[i2];
            }
            this.colours.remapIndices(this.unordered, this.bestIndices);
            ColourBlock.writeColourBlock3(bestStart, bestEnd, this.bestIndices, block, offset);
            this.bestError = currentError;
        }
    }

    @Override // com.faforever.neroxis.util.jsquish.CompressorColourFit
    void compress4(final byte[] block, final int offset) {
        int count = this.colours.getCount();
        Vec bestStart = new Vec(0.0f);
        Vec bestEnd = new Vec(0.0f);
        float currentError = this.bestError;
        Vec start = new Vec();
        Vec end = new Vec();
        constructOrdering(this.principle, 0);
        int bestIteration = 0;
        int iteration = 0;
        do {
            for (int m = 0; m < count; m++) {
                this.indices[m] = 0;
                this.alpha[m] = this.weights[m];
                this.beta[m] = 0.0f;
            }
            for (int i = count; i >= 0; i--) {
                for (int m2 = i; m2 < count; m2++) {
                    this.indices[m2] = 2;
                    this.alpha[m2] = TWO_THIRDS * this.weights[m2];
                    this.beta[m2] = ONE_THIRD * this.weights[m2];
                }
                for (int j = count; j >= i; j--) {
                    for (int m3 = j; m3 < count; m3++) {
                        this.indices[m3] = 3;
                        this.alpha[m3] = ONE_THIRD * this.weights[m3];
                        this.beta[m3] = TWO_THIRDS * this.weights[m3];
                    }
                    for (int k = count; k >= j; k--) {
                        if (k < count) {
                            this.indices[k] = 1;
                            this.alpha[k] = 0.0f;
                            this.beta[k] = this.weights[k];
                        }
                        float error = solveLeastSquares(start, end);
                        if (error < currentError) {
                            bestStart.set(start);
                            bestEnd.set(end);
                            System.arraycopy(this.indices, 0, this.bestIndices, 0, 16);
                            currentError = error;
                            bestIteration = iteration;
                        }
                    }
                }
            }
            if (bestIteration != iteration) {
                break;
            }
            iteration++;
            if (iteration == 8) {
                break;
            }
        } while (constructOrdering(start.set(bestEnd).sub(bestStart), iteration));
        if (currentError < this.bestError) {
            int order = 16 * bestIteration;
            for (int i2 = 0; i2 < count; i2++) {
                this.unordered[this.orders[order + i2]] = this.bestIndices[i2];
            }
            this.colours.remapIndices(this.unordered, this.bestIndices);
            ColourBlock.writeColourBlock4(bestStart, bestEnd, this.bestIndices, block, offset);
            this.bestError = currentError;
        }
    }

    private boolean constructOrdering(final Vec axis, final int iteration) {
        int count = this.colours.getCount();
        Vec[] values = this.colours.getPoints();
        int order = 16 * iteration;
        for (int i = 0; i < count; i++) {
            this.dps[i] = values[i].dot(axis);
            this.orders[order + i] = i;
        }
        for (int i2 = 0; i2 < count; i2++) {
            for (int j = i2; j > 0 && this.dps[j] < this.dps[j - 1]; j--) {
                float tmpF = this.dps[j];
                this.dps[j] = this.dps[j - 1];
                this.dps[j - 1] = tmpF;
                int tmpI = this.orders[order + j];
                this.orders[order + j] = this.orders[(order + j) - 1];
                this.orders[(order + j) - 1] = tmpI;
            }
        }
        for (int it = 0; it < iteration; it++) {
            int prev = 16 * it;
            boolean same = true;
            for (int i3 = 0; i3 < count; i3++) {
                if (this.orders[order + i3] != this.orders[prev + i3]) {
                    same = false;
                    break;
                }
            }
            if (same) {
                return false;
            }
        }
        Vec[] points = this.colours.getPoints();
        float[] cWeights = this.colours.getWeights();
        this.xxSum.set(0.0f);
        int i4 = 0;
        int j2 = 0;
        while (i4 < count) {
            int p = this.orders[order + i4];
            float weight = cWeights[p];
            Vec point = points[p];
            this.weights[i4] = weight;
            float wX = weight * point.x();
            float wY = weight * point.y();
            float wZ = weight * point.z();
            this.xxSum.add(wX * wX, wY * wY, wZ * wZ);
            this.weighted[j2] = wX;
            this.weighted[j2 + 1] = wY;
            this.weighted[j2 + 2] = wZ;
            i4++;
            j2 += 3;
        }
        return true;
    }

    private float solveLeastSquares(final Vec start, final Vec end) {
        float aX;
        float aY;
        float aZ;
        float bX;
        float bY;
        float bZ;
        int count = this.colours.getCount();
        float alpha2_sum = 0.0f;
        float beta2_sum = 0.0f;
        float alphabeta_sum = 0.0f;
        float alphax_sumX = 0.0f;
        float alphax_sumY = 0.0f;
        float alphax_sumZ = 0.0f;
        float betax_sumX = 0.0f;
        float betax_sumY = 0.0f;
        float betax_sumZ = 0.0f;
        int i = 0;
        int j = 0;
        while (i < count) {
            float a = this.alpha[i];
            float b = this.beta[i];
            alpha2_sum += a * a;
            beta2_sum += b * b;
            alphabeta_sum += a * b;
            alphax_sumX += this.weighted[j] * a;
            alphax_sumY += this.weighted[j + 1] * a;
            alphax_sumZ += this.weighted[j + 2] * a;
            betax_sumX += this.weighted[j] * b;
            betax_sumY += this.weighted[j + 1] * b;
            betax_sumZ += this.weighted[j + 2] * b;
            i++;
            j += 3;
        }
        if (beta2_sum == 0.0f) {
            float rcp = 1.0f / alpha2_sum;
            aX = alphax_sumX * rcp;
            aY = alphax_sumY * rcp;
            aZ = alphax_sumZ * rcp;
            bZ = 0.0f;
            bY = 0.0f;
            bX = 0.0f;
        } else if (alpha2_sum == 0.0f) {
            float rcp2 = 1.0f / beta2_sum;
            aZ = 0.0f;
            aY = 0.0f;
            aX = 0.0f;
            bX = betax_sumX * rcp2;
            bY = betax_sumY * rcp2;
            bZ = betax_sumZ * rcp2;
        } else {
            float rcp3 = 1.0f / ((alpha2_sum * beta2_sum) - (alphabeta_sum * alphabeta_sum));
            if (rcp3 == Float.POSITIVE_INFINITY) {
                return Float.MAX_VALUE;
            }
            aX = ((alphax_sumX * beta2_sum) - (betax_sumX * alphabeta_sum)) * rcp3;
            aY = ((alphax_sumY * beta2_sum) - (betax_sumY * alphabeta_sum)) * rcp3;
            aZ = ((alphax_sumZ * beta2_sum) - (betax_sumZ * alphabeta_sum)) * rcp3;
            bX = ((betax_sumX * alpha2_sum) - (alphax_sumX * alphabeta_sum)) * rcp3;
            bY = ((betax_sumY * alpha2_sum) - (alphax_sumY * alphabeta_sum)) * rcp3;
            bZ = ((betax_sumZ * alpha2_sum) - (alphax_sumZ * alphabeta_sum)) * rcp3;
        }
        float aX2 = clamp(aX, 31.0f, 0.032258064f);
        float aY2 = clamp(aY, 63.0f, 0.015873017f);
        float aZ2 = clamp(aZ, 31.0f, 0.032258064f);
        start.set(aX2, aY2, aZ2);
        float bX2 = clamp(bX, 31.0f, 0.032258064f);
        float bY2 = clamp(bY, 63.0f, 0.015873017f);
        float bZ2 = clamp(bZ, 31.0f, 0.032258064f);
        end.set(bX2, bY2, bZ2);
        float eX = (aX2 * aX2 * alpha2_sum) + (bX2 * bX2 * beta2_sum) + this.xxSum.x() + (2.0f * ((((aX2 * bX2) * alphabeta_sum) - (aX2 * alphax_sumX)) - (bX2 * betax_sumX)));
        float eY = (aY2 * aY2 * alpha2_sum) + (bY2 * bY2 * beta2_sum) + this.xxSum.y() + (2.0f * ((((aY2 * bY2) * alphabeta_sum) - (aY2 * alphax_sumY)) - (bY2 * betax_sumY)));
        float eZ = (aZ2 * aZ2 * alpha2_sum) + (bZ2 * bZ2 * beta2_sum) + this.xxSum.z() + (2.0f * ((((aZ2 * bZ2) * alphabeta_sum) - (aZ2 * alphax_sumZ)) - (bZ2 * betax_sumZ)));
        return this.metric.dot(eX, eY, eZ);
    }
}

package com.faforever.neroxis.util.jsquish;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/jsquish/CompressorSingleColour.class */
final class CompressorSingleColour extends CompressorColourFit {
    private final int[] indices;
    private final int[][][][] lookups;
    private final int[][] sources;
    private final Vec start;
    private final Vec end;
    private final int[] index;
    private int bestError;
    private final int[] colour;

    CompressorSingleColour(final ColourSet colours, final Squish.CompressionType type) {
        super(colours, type);
        this.indices = new int[16];
        this.lookups = new int[3][][][];
        this.sources = new int[3][];
        this.start = new Vec();
        this.end = new Vec();
        this.index = new int[1];
        this.colour = new int[3];
        Vec colour = colours.getPoints()[0];
        this.colour[0] = Math.round(255.0f * colour.x());
        this.colour[1] = Math.round(255.0f * colour.y());
        this.colour[2] = Math.round(255.0f * colour.z());
        this.bestError = Integer.MAX_VALUE;
    }

    @Override // com.faforever.neroxis.util.jsquish.CompressorColourFit
    void compress3(final byte[] block, final int offset) {
        this.lookups[0] = SingleColourLookup3.LOOKUP_5_3;
        this.lookups[1] = SingleColourLookup3.LOOKUP_6_3;
        this.lookups[2] = SingleColourLookup3.LOOKUP_5_3;
        int error = computeEndPoints(3, this.lookups);
        if (error < this.bestError) {
            this.colours.remapIndices(this.index, this.indices);
            ColourBlock.writeColourBlock3(this.start, this.end, this.indices, block, offset);
            this.bestError = error;
        }
    }

    @Override // com.faforever.neroxis.util.jsquish.CompressorColourFit
    void compress4(final byte[] block, final int offset) {
        this.lookups[0] = SingleColourLookup4.LOOKUP_5_4;
        this.lookups[1] = SingleColourLookup4.LOOKUP_6_4;
        this.lookups[2] = SingleColourLookup4.LOOKUP_5_4;
        int error = computeEndPoints(4, this.lookups);
        if (error < this.bestError) {
            this.colours.remapIndices(this.index, this.indices);
            ColourBlock.writeColourBlock4(this.start, this.end, this.indices, block, offset);
            this.bestError = error;
        }
    }

    private int computeEndPoints(final int count, final int[][][][] lookups) {
        for (int index = 0; index < count; index++) {
            int error = 0;
            for (int channel = 0; channel < 3; channel++) {
                int[][][] lookup = lookups[channel];
                int target = this.colour[channel];
                this.sources[channel] = lookup[target][index];
                int diff = this.sources[channel][2];
                error += diff * diff;
            }
            if (error < this.bestError) {
                this.start.set(this.sources[0][0] * 0.032258064f, this.sources[1][0] * 0.015873017f, this.sources[2][0] * 0.032258064f);
                this.end.set(this.sources[0][1] * 0.032258064f, this.sources[1][1] * 0.015873017f, this.sources[2][1] * 0.032258064f);
                this.index[0] = index;
                this.bestError = error;
            }
        }
        return this.bestError;
    }
}

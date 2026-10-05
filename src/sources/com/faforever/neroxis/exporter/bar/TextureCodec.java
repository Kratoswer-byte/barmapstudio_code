package com.faforever.neroxis.exporter.bar;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Compressione delle immagini nei formati che il motore di BAR legge: blocchi DXT1 (colore) e DXT5
 * (colore piu' trasparenza), e riduzione a meta' per i livelli di dettaglio.
 *
 * <p>Ogni blocco copre 4x4 pixel. In DXT1 si salvano due colori a 16 bit e, per ogni pixel, due bit che
 * scelgono tra i due colori e le due sfumature intermedie. I due colori sono gli estremi del blocco,
 * avvicinati di un sedicesimo per non lasciare i pixel piu' numerosi troppo lontani da una sfumatura.</p>
 */
final class TextureCodec {
    private static final int BLOCK = 4;
    private static final int DXT1_BLOCK_BYTES = 8;
    private static final int DDS_HEADER_BYTES = 128;

    private TextureCodec() {}

    /**
     * Comprime un'immagine RGB in blocchi DXT1.
     *
     * @param out dove scrivere, a partire da {@code outOffset}
     * @return il numero di byte scritti
     */
    static int compressDxt1(int[] rgb, int width, int height, byte[] out, int outOffset) {
        int[] block = new int[BLOCK * BLOCK];
        int position = outOffset;
        for (int top = 0; top < height; top += BLOCK) {
            for (int left = 0; left < width; left += BLOCK) {
                readBlock(rgb, width, left, top, block);
                writeColourBlock(block, out, position);
                position += DXT1_BLOCK_BYTES;
            }
        }
        return position - outOffset;
    }

    /** Un file .dds completo in formato DXT5, senza livelli di dettaglio. */
    static byte[] dxt5File(int[] argb, int width, int height) {
        int dataBytes = (width / BLOCK) * (height / BLOCK) * 2 * DXT1_BLOCK_BYTES;
        ByteBuffer header = ByteBuffer.allocate(DDS_HEADER_BYTES + dataBytes).order(ByteOrder.LITTLE_ENDIAN);
        header.put(new byte[]{'D', 'D', 'S', ' '});
        header.putInt(124);            // lunghezza dell'intestazione
        header.putInt(0x81007);        // campi presenti: dimensioni, formato, dimensione dei dati
        header.putInt(height);
        header.putInt(width);
        header.putInt(dataBytes);
        header.position(76);
        header.putInt(32);             // lunghezza della descrizione del formato
        header.putInt(4);              // il formato e' indicato dal codice di quattro lettere
        header.put(new byte[]{'D', 'X', 'T', '5'});
        header.position(108);
        header.putInt(0x1000);         // e' una texture
        byte[] out = header.array();
        int[] block = new int[BLOCK * BLOCK];
        int position = DDS_HEADER_BYTES;
        for (int top = 0; top < height; top += BLOCK) {
            for (int left = 0; left < width; left += BLOCK) {
                readBlock(argb, width, left, top, block);
                writeAlphaBlock(block, out, position);
                writeColourBlock(block, out, position + DXT1_BLOCK_BYTES);
                position += 2 * DXT1_BLOCK_BYTES;
            }
        }
        return out;
    }

    /** L'immagine a meta' larghezza e meta' altezza: ogni pixel e' la media arrotondata di quattro. */
    static int[] halve(int[] rgb, int width, int height) {
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        int[] half = new int[halfWidth * halfHeight];
        for (int y = 0; y < halfHeight; y++) {
            for (int x = 0; x < halfWidth; x++) {
                int first = y * 2 * width + x * 2;
                int a = rgb[first];
                int b = rgb[first + 1];
                int c = rgb[first + width];
                int d = rgb[first + width + 1];
                int red = (channel(a, 16) + channel(b, 16) + channel(c, 16) + channel(d, 16) + 2) >> 2;
                int green = (channel(a, 8) + channel(b, 8) + channel(c, 8) + channel(d, 8) + 2) >> 2;
                int blue = (channel(a, 0) + channel(b, 0) + channel(c, 0) + channel(d, 0) + 2) >> 2;
                half[y * halfWidth + x] = (red << 16) | (green << 8) | blue;
            }
        }
        return half;
    }

    private static int channel(int colour, int shift) {
        return (colour >> shift) & 0xFF;
    }

    private static void readBlock(int[] pixels, int width, int left, int top, int[] block) {
        for (int row = 0; row < BLOCK; row++) {
            System.arraycopy(pixels, (top + row) * width + left, block, row * BLOCK, BLOCK);
        }
    }

    private static void writeColourBlock(int[] block, byte[] out, int offset) {
        int lowRed = 255, lowGreen = 255, lowBlue = 255;
        int highRed = 0, highGreen = 0, highBlue = 0;
        for (int pixel : block) {
            int red = channel(pixel, 16);
            int green = channel(pixel, 8);
            int blue = channel(pixel, 0);
            lowRed = Math.min(lowRed, red);
            lowGreen = Math.min(lowGreen, green);
            lowBlue = Math.min(lowBlue, blue);
            highRed = Math.max(highRed, red);
            highGreen = Math.max(highGreen, green);
            highBlue = Math.max(highBlue, blue);
        }
        int insetRed = (highRed - lowRed) >> 4;
        int insetGreen = (highGreen - lowGreen) >> 4;
        int insetBlue = (highBlue - lowBlue) >> 4;
        int first = pack565(highRed - insetRed, highGreen - insetGreen, highBlue - insetBlue);
        int second = pack565(lowRed + insetRed, lowGreen + insetGreen, lowBlue + insetBlue);
        if (first < second) {
            int swap = first;
            first = second;
            second = swap;
        }
        long choices = 0;
        if (first != second) {
            // ogni pixel viene proiettato sulla linea tra i due colori: la posizione decide la sfumatura
            int red1 = widen5(first >> 11), green1 = widen6((first >> 5) & 63), blue1 = widen5(first & 31);
            int red2 = widen5(second >> 11), green2 = widen6((second >> 5) & 63), blue2 = widen5(second & 31);
            int dRed = red1 - red2;
            int dGreen = green1 - green2;
            int dBlue = blue1 - blue2;
            int span = dRed * dRed + dGreen * dGreen + dBlue * dBlue;
            for (int i = block.length - 1; i >= 0; i--) {
                int pixel = block[i];
                int along = (channel(pixel, 16) - red2) * dRed + (channel(pixel, 8) - green2) * dGreen + (channel(pixel, 0) - blue2) * dBlue;
                int choice;
                if (along * 6 < span) {
                    choice = 1;            // il secondo colore
                } else if (along * 2 < span) {
                    choice = 3;            // un terzo verso il primo
                } else if (along * 6 < span * 5) {
                    choice = 2;            // due terzi verso il primo
                } else {
                    choice = 0;            // il primo colore
                }
                choices = (choices << 2) | choice;
            }
        }
        out[offset] = (byte) first;
        out[offset + 1] = (byte) (first >> 8);
        out[offset + 2] = (byte) second;
        out[offset + 3] = (byte) (second >> 8);
        out[offset + 4] = (byte) choices;
        out[offset + 5] = (byte) (choices >> 8);
        out[offset + 6] = (byte) (choices >> 16);
        out[offset + 7] = (byte) (choices >> 24);
    }

    /** Trasparenza di un blocco DXT5: i due estremi e, per ogni pixel, tre bit che scelgono tra otto livelli. */
    private static void writeAlphaBlock(int[] block, byte[] out, int offset) {
        int lowest = 255;
        int highest = 0;
        for (int pixel : block) {
            int alpha = pixel >>> 24;
            lowest = Math.min(lowest, alpha);
            highest = Math.max(highest, alpha);
        }
        out[offset] = (byte) highest;
        out[offset + 1] = (byte) lowest;
        long choices = 0;
        if (highest > lowest) {
            int range = highest - lowest;
            for (int i = block.length - 1; i >= 0; i--) {
                int step = ((highest - (block[i] >>> 24)) * 7 + range / 2) / range;
                // nel formato i livelli 0 e 1 sono gli estremi, da 2 a 7 quelli intermedi
                int choice = step == 0 ? 0 : step == 7 ? 1 : step + 1;
                choices = (choices << 3) | choice;
            }
        }
        for (int i = 0; i < 6; i++) {
            out[offset + 2 + i] = (byte) (choices >> (8 * i));
        }
    }

    /** Da tre valori a 8 bit a un colore a 16 bit (5 rosso, 6 verde, 5 blu), arrotondando. */
    private static int pack565(int red, int green, int blue) {
        return ((red * 31 + 127) / 255 << 11) | ((green * 63 + 127) / 255 << 5) | ((blue * 31 + 127) / 255);
    }

    private static int widen5(int value) {
        return (value << 3) | (value >> 2);
    }

    private static int widen6(int value) {
        return (value << 2) | (value >> 4);
    }
}

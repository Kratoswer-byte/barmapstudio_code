package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.map.Symmetry;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Stili di terreno aggiuntivi, pensati per BAR, che non esistono nel generatore Neroxis: Canyon,
 * Crater, Archipelago, Lanes e Mesa. Ogni stile disegna direttamente una heightmap simmetrica e le
 * posizioni di partenza; texture e resto della mappa passano poi dal normale percorso "da heightmap".
 *
 * Le quote sono in unita' dell'editor (1 unita' = 11,2 elmi in gioco) e le distanze in celle (16 elmi).
 * Il terreno si calcola per la meta' della squadra 0 e si specchia (o si ruota di 180 gradi) per l'altra.
 */
public final class TerrainSynth {

    public static final String[] STYLES = {"ARCHIPELAGO", "CANYON", "CRATER", "LANES", "MESA"};

    private TerrainSynth() {}

    public static boolean supports(String style) {
        for (String s : STYLES) {
            if (s.equals(style)) return true;
        }
        return false;
    }

    public static final class Result {
        /** Heightmap a 16 bit, (larghezza+1) x (altezza+1) celle, normalizzata su tutto l'intervallo. */
        public BufferedImage heightmap;
        /** Dislivello totale in unita' dell'editor. */
        public float maxUnits;
        /** Livello dell'acqua in unita' sopra il punto piu' basso, oppure -1000 se la mappa e' asciutta. */
        public float waterUnits;
        public Symmetry symmetry;
        /** {x, z, squadra} in celle; le squadre si alternano come nelle mappe di BAR. */
        public final List<float[]> spawns = new ArrayList<>();
    }

    /** Terreno di una meta' mappa: a = distanza dal proprio bordo (0..length/2), b = posizione laterale (0..depth). */
    private abstract static class Field {
        double length;
        double depth;
        double[][] spawns;
        long seed;
        /** Quota a cui vengono spianate le basi. */
        float spawnLevel;
        /** Livello dell'acqua, NaN = mappa asciutta. */
        float seaLevel = Float.NaN;
        /** true = le due meta' sono ruotate di 180 gradi invece che specchiate. */
        boolean point;

        /**
         * Quanto deformare le forme in questo punto (0..1). Con la simmetria a rotazione la deformazione si
         * spegne verso la linea di mezzo: li' le due meta' devono combaciare, altrimenti gole e creste si spezzano.
         */
        double warp(double a) {
            return point ? smooth(0.0, 70.0, (length / 2.0) - a) : 1.0;
        }

        abstract void setup(Random rng);

        abstract float at(double a, double b);

        double spawnDistance(double a, double b) {
            double best = Double.MAX_VALUE;
            for (double[] s : spawns) {
                best = Math.min(best, Math.hypot(a - s[0], b - s[1]));
            }
            return best;
        }

        double fbm(double x, double y, double scale, int octaves, int channel) {
            double sum = 0;
            double amp = 1;
            double total = 0;
            double f = 1.0 / scale;
            for (int o = 0; o < octaves; o++) {
                sum += valueNoise(x * f, y * f, seed + (channel * 7919L) + (o * 104729L)) * amp;
                total += amp;
                amp *= 0.5;
                f *= 2.0;
            }
            return sum / total;
        }
    }

    public static Result generate(String style, int width, int height, int spawnCount, Symmetry requested, long seed) {
        Symmetry symmetry = requested;
        if (symmetry != Symmetry.X && symmetry != Symmetry.Z && symmetry != Symmetry.POINT2) {
            symmetry = height > width ? Symmetry.Z : Symmetry.X;
        }
        final boolean point = symmetry == Symmetry.POINT2;
        // asse lungo cui si fronteggiano le squadre
        final boolean alongX = symmetry == Symmetry.X || (point && width >= height);
        final double length = alongX ? width : height;
        final double depth = alongX ? height : width;

        Field field = switch (style) {
            case "CANYON" -> new Canyon();
            case "CRATER" -> new Crater();
            case "ARCHIPELAGO" -> new Archipelago();
            case "LANES" -> new Lanes();
            default -> new Mesa();
        };
        field.length = length;
        field.depth = depth;
        field.seed = seed;
        field.point = point;

        // basi della squadra 0: una fila lungo il proprio bordo, su due file sfalsate se sono tante
        int perTeam = Math.max(1, spawnCount / 2);
        double baseA = Math.max(22.0, length * 0.13);
        double spacing = Math.min(0.8 / perTeam, 0.22);
        boolean twoRows = perTeam > 4 || (depth * spacing < 26 && perTeam > 1);
        field.spawns = new double[perTeam][];
        for (int i = 0; i < perTeam; i++) {
            double b = depth * (0.5 + ((i - ((perTeam - 1) / 2.0)) * spacing));
            double a = baseA + ((twoRows && (i % 2 == 1)) ? Math.min(30.0, length * 0.07) : 0.0);
            field.spawns[i] = new double[]{a, b};
        }
        field.setup(new Random(seed));

        final int w1 = width + 1;
        final int h1 = height + 1;
        final float[] values = new float[w1 * h1];
        final Field f = field;
        IntStream.range(0, h1).parallel().forEach(y -> {
            for (int x = 0; x < w1; x++) {
                double a = alongX ? x : y;
                double b = alongX ? y : x;
                if (a > length / 2.0) {
                    a = length - a;
                    if (point) {
                        b = depth - b;
                    }
                }
                double h = f.at(a, b);
                if (point) {
                    // vicino alla linea di mezzo le due meta' ruotate devono coincidere: si media con il punto opposto
                    double seam = smooth(length / 2.0 - 26.0, length / 2.0, a);
                    if (seam > 0) {
                        h += ((f.at(a, depth - b) + h) * 0.5 - h) * seam;
                    }
                }
                // piazzola piana attorno alle basi
                double flat = smooth(15.0, 30.0, f.spawnDistance(a, b));
                h = f.spawnLevel + ((h - f.spawnLevel) * flat);
                values[(y * w1) + x] = (float) h;
            }
        });

        float min = Float.MAX_VALUE;
        float max = -Float.MAX_VALUE;
        for (float v : values) {
            min = Math.min(min, v);
            max = Math.max(max, v);
        }
        float range = Math.max(0.001f, max - min);

        Result result = new Result();
        result.symmetry = symmetry;
        result.heightmap = new BufferedImage(w1, h1, BufferedImage.TYPE_USHORT_GRAY);
        WritableRaster raster = result.heightmap.getRaster();
        for (int y = 0; y < h1; y++) {
            for (int x = 0; x < w1; x++) {
                raster.setSample(x, y, 0, Math.round(((values[(y * w1) + x] - min) / range) * 65535.0f));
            }
        }
        // l'editor non scende sotto 20 unita' di dislivello: se servisse, il terreno viene stirato in proporzione
        result.maxUnits = Math.max(20.0f, range);
        result.waterUnits = Float.isNaN(field.seaLevel) ? -1000.0f : (field.seaLevel - min) * (result.maxUnits / range);

        for (int i = 0; i < perTeam; i++) {
            double a = field.spawns[i][0];
            double b = field.spawns[i][1];
            result.spawns.add(alongX ? new float[]{(float) a, (float) b, 0} : new float[]{(float) b, (float) a, 0});
            double a2 = length - a;
            double b2 = point ? depth - b : b;
            result.spawns.add(alongX ? new float[]{(float) a2, (float) b2, 1} : new float[]{(float) b2, (float) a2, 1});
        }
        return result;
    }

    // ------------------------------------------------------------------ CANYON

    /** Altopiano tagliato da gole strette: le basi stanno sul fondo, i passaggi verso il centro sono obbligati. */
    private static final class Canyon extends Field {
        private final List<double[]> segments = new ArrayList<>(); // x1, y1, x2, y2, mezza larghezza
        private final List<double[]> ramps = new ArrayList<>();
        private static final double TOP = 11.0;

        @Override
        void setup(Random rng) {
            spawnLevel = 0.0f;
            double baseA = spawns[0][0];
            double[] lanes = depth >= 420 ? new double[]{0.2, 0.5, 0.8} : new double[]{0.28, 0.72};
            double first = lanes[0] * depth;
            double last = lanes[lanes.length - 1] * depth;
            double lo = first;
            double hi = last;
            for (double[] s : spawns) {
                lo = Math.min(lo, s[1]);
                hi = Math.max(hi, s[1]);
            }
            // gola di fondo che unisce le basi e l'imbocco delle corsie
            segments.add(new double[]{baseA, lo, baseA, hi, 13.0});
            for (double lane : lanes) {
                double b = lane * depth;
                double bend = b + ((rng.nextDouble() - 0.5) * depth * 0.10);
                segments.add(new double[]{baseA, b, length * 0.31, bend, 10.0});
                segments.add(new double[]{length * 0.31, bend, length * 0.5, b, 10.0});
                // rampa per salire sull'altopiano, a meta' corsia
                ramps.add(new double[]{length * (0.38 + (rng.nextDouble() * 0.06)), bend + ((rng.nextBoolean() ? 1 : -1) * 22.0)});
            }
            // collegamento trasversale tra le corsie: al centro oppure a un terzo
            double crossA = rng.nextBoolean() ? length * 0.5 : length * 0.36;
            segments.add(new double[]{crossA, first, crossA, last, 9.0});
        }

        @Override
        float at(double a, double b) {
            double wa = a + ((fbm(a, b, 140, 3, 1) - 0.5) * 84.0 * warp(a));
            double wb = b + ((fbm(a, b, 140, 3, 2) - 0.5) * 84.0 * warp(a));
            double d = Double.MAX_VALUE;
            for (double[] s : segments) {
                double width = s[4] + ((fbm(a, b, 55, 2, 3) - 0.5) * 14.0);
                d = Math.min(d, segmentDistance(wa, wb, s[0], s[1], s[2], s[3]) - width);
            }
            double wall = 3.5;
            for (double[] r : ramps) {
                wall = Math.max(wall, 3.5 + (22.0 * (1.0 - smooth(12.0, 26.0, Math.hypot(a - r[0], b - r[1])))));
            }
            double rise = smooth(0.0, wall, d);
            double top = TOP + (fbm(a, b, 40, 3, 4) * 2.5);
            // lontano dalle gole l'altopiano si alza in creste
            double ridged = 1.0 - Math.abs((fbm(a, b, 70, 3, 5) * 2.0) - 1.0);
            top += 9.0 * ridged * ridged * smooth(24.0, 70.0, d);
            double floor = fbm(a, b, 30, 2, 6) * 0.8;
            return (float) (floor + ((top - floor) * rise));
        }
    }

    // ------------------------------------------------------------------ LANES

    /** Tre corsie separate da creste rocciose, con varchi verso il centro: lo schema delle mappe competitive. */
    private static final class Lanes extends Field {
        private double innerGap;
        private boolean hasInnerGap;

        @Override
        void setup(Random rng) {
            spawnLevel = 2.5f;
            hasInnerGap = rng.nextBoolean();
            innerGap = length * (0.33 + (rng.nextDouble() * 0.05));
        }

        @Override
        float at(double a, double b) {
            double wb = b + ((fbm(a, b, 110, 3, 1) - 0.5) * 76.0 * warp(a));
            double floor = 1.0 + (fbm(a, b, 60, 3, 2) * 3.0);
            double half = 10.0 + (fbm(a, b, 45, 2, 3) * 12.0);
            double d = Math.min(Math.abs(wb - (depth / 3.0)), Math.abs(wb - (depth * 2.0 / 3.0))) - half;
            // le creste partono dopo la zona delle basi e si interrompono nei varchi
            double present = smooth(length * 0.19, length * 0.24, a);
            present *= smooth(10.0, 16.0, Math.abs(a - (length * 0.5)));
            if (hasInnerGap) {
                present *= smooth(9.0, 15.0, Math.abs(a - innerGap));
            }
            double crest = 14.0 + (fbm(a, b, 28, 3, 4) * 8.0);
            double rise = 1.0 - smooth(0.0, 4.0, d);
            double h = floor + ((crest - floor) * rise * present);
            // massi sparsi nelle corsie e una cornice di rilievi lungo i due lati della mappa
            h += 4.0 * smooth(0.70, 0.80, fbm(a, b, 16, 2, 7));
            double side = Math.min(b, depth - b) + ((fbm(a, b, 50, 2, 8) - 0.5) * 34.0);
            h = Math.max(h, floor + ((crest - floor) * (1.0 - smooth(4.0, 20.0, side))));
            return (float) h;
        }
    }

    // ------------------------------------------------------------------ MESA

    /** Tante piattaforme rialzate a pareti ripide, ognuna raggiungibile da una rampa. */
    private static final class Mesa extends Field {
        private final List<double[]> mesas = new ArrayList<>(); // a, b, raggio, quota, angolo rampa, seme forma, gradone

        @Override
        void setup(Random rng) {
            spawnLevel = 1.0f;
            double maxRadius = Math.max(16.0, Math.min(34.0, Math.min(length, depth) * 0.085));
            int target = (int) Math.min(60, Math.max(4, ((length / 2.0) * depth) / (78.0 * 78.0)));
            for (int attempt = 0; attempt < 4000 && mesas.size() < target; attempt++) {
                double r = 13.0 + (rng.nextDouble() * (maxRadius - 13.0));
                double a = 8.0 + (rng.nextDouble() * ((length / 2.0) - 8.0 - r - 8.0));
                double b = r + 8.0 + (rng.nextDouble() * (depth - (2.0 * (r + 8.0))));
                if (a < 8.0 || b < r + 8.0 || spawnDistance(a, b) < r + 38.0) {
                    continue;
                }
                boolean free = true;
                for (double[] m : mesas) {
                    if (Math.hypot(a - m[0], b - m[1]) < r + m[2] + 20.0) {
                        free = false;
                        break;
                    }
                }
                if (!free) {
                    continue;
                }
                double level = rng.nextDouble() < 0.3 ? 15.0 : 9.5;
                boolean tiered = r > 22.0 && rng.nextDouble() < 0.5;
                mesas.add(new double[]{a, b, r, level, rng.nextDouble() * Math.PI * 2.0, rng.nextDouble() * 1000.0, tiered ? 1 : 0});
            }
        }

        @Override
        float at(double a, double b) {
            double ground = fbm(a, b, 55, 3, 1) * 2.5;
            double h = ground;
            for (double[] m : mesas) {
                double dx = a - m[0];
                double dy = b - m[1];
                double dist = Math.hypot(dx, dy);
                if (dist > (m[2] * 1.4) + 50.0) {
                    continue;
                }
                double angle = Math.atan2(dy, dx);
                double edge = m[2] * (0.86 + (0.3 * fbm(Math.cos(angle) * 40.0 + m[5], Math.sin(angle) * 40.0 + m[5], 30, 2, 2)));
                double delta = Math.abs(Math.atan2(Math.sin(angle - m[4]), Math.cos(angle - m[4])));
                // nel settore della rampa la parete diventa un pendio lungo, percorribile dai veicoli
                double wall = 3.5 + ((m[3] * 2.5) * (1.0 - smooth(0.45, 0.85, delta)));
                double top = m[3] + (fbm(a, b, 26, 2, 3) * 1.2);
                double value = ground + ((top - ground) * (1.0 - smooth(0.0, wall, dist - edge)));
                if (m[6] > 0) {
                    // secondo gradone al centro
                    value += 7.0 * (1.0 - smooth(0.0, 3.5, dist - (edge * 0.45)));
                }
                h = Math.max(h, value);
            }
            return (float) h;
        }
    }

    // ------------------------------------------------------------------ CRATER

    /** Un grande cratere al centro, chiuso da un bordo roccioso con pochi valichi, e crateri minori attorno. */
    private static final class Crater extends Field {
        private double radius;
        private boolean diagonalPasses;
        private final List<double[]> small = new ArrayList<>();

        @Override
        void setup(Random rng) {
            spawnLevel = 6.5f;
            radius = Math.min(length, depth) * 0.27;
            diagonalPasses = rng.nextDouble() < 0.3;
            int target = (int) Math.min(14, Math.max(2, ((length / 2.0) * depth) / (140.0 * 140.0)));
            for (int attempt = 0; attempt < 2000 && small.size() < target; attempt++) {
                double r = 10.0 + (rng.nextDouble() * 9.0);
                double a = r + 6.0 + (rng.nextDouble() * ((length / 2.0) - (2.0 * (r + 6.0))));
                double b = r + 6.0 + (rng.nextDouble() * (depth - (2.0 * (r + 6.0))));
                if (spawnDistance(a, b) < r + 36.0 || Math.hypot(a - (length / 2.0), b - (depth / 2.0)) < radius + r + 34.0) {
                    continue;
                }
                boolean free = true;
                for (double[] c : small) {
                    if (Math.hypot(a - c[0], b - c[1]) < r + c[2] + 16.0) {
                        free = false;
                        break;
                    }
                }
                if (free) {
                    small.add(new double[]{a, b, r});
                }
            }
        }

        @Override
        float at(double a, double b) {
            double dx = (length / 2.0) - a;
            double dy = b - (depth / 2.0);
            double r = Math.hypot(dx, dy);
            double theta = Math.atan2(dy, dx);
            double rim = radius * (0.84 + (0.32 * fbm(Math.cos(theta) * 60.0, Math.sin(theta) * 60.0, 40, 2, 1)));
            double outside = 6.0 + (fbm(a, b, 60, 3, 2) * 3.0);
            double h = outside;
            for (double[] c : small) {
                double d = Math.hypot(a - c[0], b - c[1]);
                if (d < c[2] * 2.2) {
                    double bowl = 1.0 - smooth(c[2] * 0.55, c[2], d);
                    double lip = Math.max(0.0, 1.0 - (Math.abs(d - c[2]) / (c[2] * 0.45)));
                    h += (lip * lip * 3.5) - (bowl * 3.5);
                }
            }
            // dentro il cratere: fondo basso e un rilievo centrale
            double inside = 0.5 + (fbm(a, b, 45, 2, 3) * 1.5);
            inside += 5.0 * (1.0 - smooth(radius * 0.16, radius * 0.16 + 16.0, r));
            double blend = smooth(rim - 26.0, rim - 4.0, r);
            h = inside + ((h - inside) * blend);
            // bordo: parete ripida verso l'interno, pendio piu' dolce verso l'esterno
            double offset = r - rim;
            double crest = offset < 0 ? 1.0 - smooth(0.0, 6.0, -offset - 4.0) : 1.0 - smooth(0.0, 20.0, offset - 4.0);
            // valichi: verso ogni base, sulla linea di mezzo e, a volte, in diagonale
            double pass = passFactor(theta, 0.0, r);
            pass *= passFactor(theta, Math.PI / 2.0, r) * passFactor(theta, -Math.PI / 2.0, r);
            if (diagonalPasses) {
                pass *= passFactor(theta, Math.PI / 4.0, r) * passFactor(theta, -Math.PI / 4.0, r);
            }
            double crestHeight = 11.0 + (fbm(a, b, 34, 3, 4) * 12.0);
            return (float) (h + ((crestHeight - h) * crest * pass));
        }

        private static double passFactor(double theta, double centre, double r) {
            double delta = Math.abs(Math.atan2(Math.sin(theta - centre), Math.cos(theta - centre)));
            return smooth(11.0, 18.0, delta * r);
        }
    }

    // ------------------------------------------------------------------ ARCHIPELAGO

    /** Isole disposte ad anello attorno a un mare centrale, unite da secche guadabili. */
    private static final class Archipelago extends Field {
        private static final double SEA = 12.0;
        private final List<double[]> islands = new ArrayList<>(); // a1, b1, a2, b2, raggio, quota, seme
        private final List<double[]> bars = new ArrayList<>();

        @Override
        void setup(Random rng) {
            seaLevel = (float) SEA;
            spawnLevel = (float) (SEA + 5.0);
            double lo = depth / 2.0;
            double hi = depth / 2.0;
            double baseA = spawns[0][0];
            for (double[] s : spawns) {
                lo = Math.min(lo, s[1]);
                hi = Math.max(hi, s[1]);
                baseA = Math.max(baseA, s[0]);
            }
            double unit = Math.max(16.0, Math.min(40.0, Math.min(length, depth) * 0.07));
            // isola di casa: una striscia lunga quanto la fila delle basi
            double homeA = (spawns[0][0] + baseA) / 2.0;
            islands.add(new double[]{homeA, lo, homeA, hi, unit + 26.0, SEA + 5.0, rng.nextDouble() * 1000.0});
            double ringA = length * 0.30;
            double ringB = depth * 0.34;
            double phi = Math.toRadians(48.0 + (rng.nextDouble() * 10.0));
            double[][] ring = {
                {(length / 2.0) - (ringA * Math.cos(phi)), (depth / 2.0) - (ringB * Math.sin(phi))},
                {(length / 2.0) - (ringA * Math.cos(phi)), (depth / 2.0) + (ringB * Math.sin(phi))},
                {length / 2.0, (depth / 2.0) - ringB},
                {length / 2.0, (depth / 2.0) + ringB}};
            for (double[] c : ring) {
                double r = unit * (0.9 + (rng.nextDouble() * 0.5));
                islands.add(new double[]{c[0], c[1], c[0], c[1], r, SEA + 4.0 + (rng.nextDouble() * 5.0), rng.nextDouble() * 1000.0});
            }
            // secche: casa -> isole laterali -> isole sulla linea di mezzo
            bars.add(new double[]{homeA, lo, ring[0][0], ring[0][1]});
            bars.add(new double[]{homeA, hi, ring[1][0], ring[1][1]});
            bars.add(new double[]{ring[0][0], ring[0][1], ring[2][0], ring[2][1]});
            bars.add(new double[]{ring[1][0], ring[1][1], ring[3][0], ring[3][1]});
        }

        @Override
        float at(double a0, double b0) {
            // coste irregolari: le coordinate vengono deformate prima di misurare la distanza dalle isole
            double a = a0 + ((fbm(a0, b0, 110, 3, 5) - 0.5) * 56.0 * warp(a0));
            double b = b0 + ((fbm(a0, b0, 110, 3, 6) - 0.5) * 56.0 * warp(a0));
            double shore = Double.MAX_VALUE;
            double top = SEA + 4.0;
            for (double[] i : islands) {
                double dx = a - ((i[0] + i[2]) / 2.0);
                double dy = b - ((i[1] + i[3]) / 2.0);
                double angle = Math.atan2(dy, dx);
                double r = i[4] * (0.82 + (0.36 * fbm(Math.cos(angle) * 50.0 + i[6], Math.sin(angle) * 50.0 + i[6], 34, 2, 1)));
                double d = segmentDistance(a, b, i[0], i[1], i[2], i[3]) - r;
                if (d < shore) {
                    shore = d;
                    top = i[5];
                }
            }
            if (shore < 0) {
                // spiaggia dolce, poi l'interno dell'isola
                double inland = smooth(0.0, 16.0, -shore);
                return (float) (SEA + 0.6 + ((top - SEA - 0.6) * inland) + (fbm(a, b, 30, 3, 2) * 1.6 * inland));
            }
            double seabed = 0.5 + (fbm(a, b, 70, 3, 3) * 3.0);
            double h = Math.max(seabed, SEA - 0.6 - (0.2 * shore));
            for (double[] bar : bars) {
                double d = segmentDistance(a, b, bar[0], bar[1], bar[2], bar[3]) + ((fbm(a, b, 40, 2, 4) - 0.5) * 8.0);
                // secca a circa 10 elmi di profondita': guadabile dalla maggior parte delle unita' di terra
                h = Math.max(h, seabed + (((SEA - 0.9) - seabed) * (1.0 - smooth(6.0, 13.0, d))));
            }
            return (float) h;
        }
    }

    // ------------------------------------------------------------------ strumenti

    private static double smooth(double edge0, double edge1, double x) {
        double t = Math.max(0.0, Math.min(1.0, (x - edge0) / (edge1 - edge0)));
        return t * t * (3.0 - (2.0 * t));
    }

    private static double segmentDistance(double px, double py, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double len = (dx * dx) + (dy * dy);
        double t = len < 1e-9 ? 0.0 : Math.max(0.0, Math.min(1.0, (((px - x1) * dx) + ((py - y1) * dy)) / len));
        return Math.hypot(px - (x1 + (dx * t)), py - (y1 + (dy * t)));
    }

    private static double valueNoise(double x, double y, long seed) {
        long x0 = (long) Math.floor(x);
        long y0 = (long) Math.floor(y);
        double tx = x - x0;
        double ty = y - y0;
        tx = tx * tx * (3.0 - (2.0 * tx));
        ty = ty * ty * (3.0 - (2.0 * ty));
        double top = hash(x0, y0, seed) + ((hash(x0 + 1, y0, seed) - hash(x0, y0, seed)) * tx);
        double bottom = hash(x0, y0 + 1, seed) + ((hash(x0 + 1, y0 + 1, seed) - hash(x0, y0 + 1, seed)) * tx);
        return top + ((bottom - top) * ty);
    }

    private static double hash(long x, long y, long seed) {
        long n = (x * 0x9E3779B97F4A7C15L) ^ (y * 0xC2B2AE3D27D4EB4FL) ^ (seed * 0x165667B19E3779F9L);
        n ^= n >>> 31;
        n *= 0xBF58476D1CE4E5B9L;
        n ^= n >>> 29;
        return ((n >>> 11) & 0xFFFFF) / (double) 0xFFFFF;
    }
}

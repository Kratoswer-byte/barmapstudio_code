package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/StartBoxShapes.class */
final class StartBoxShapes {
    static final String OPTION = "mapmetadata_startbox_override";
    static final String SQUARE = "square";
    static final String TRIANGLE = "triangle";

    private StartBoxShapes() {
    }

    static float[][] triangle(float[] box) {
        float left = box[0];
        float top = box[1];
        float right = box[2];
        float bottom = box[3];
        float centerX = (left + right) / 2.0f;
        float centerY = (top + bottom) / 2.0f;
        boolean west = left <= 0.02f;
        boolean east = right >= 0.98f;
        boolean north = top <= 0.02f;
        boolean south = bottom >= 0.98f;
        if ((west || east) && ((north || south) && ((!west || !east) && (!north || !south)))) {
            float cornerX = west ? left : right;
            float cornerY = north ? top : bottom;
            float[][] fArr = new float[3][];
            fArr[0] = new float[]{cornerX, cornerY};
            float[] fArr2 = new float[2];
            fArr2[0] = west ? right : left;
            fArr2[1] = cornerY;
            fArr[1] = fArr2;
            float[] fArr3 = new float[2];
            fArr3[0] = cornerX;
            fArr3[1] = north ? bottom : top;
            fArr[2] = fArr3;
            return fArr;
        }
        float dx = centerX - 0.5f;
        float dy = centerY - 0.5f;
        if (Math.abs(dx) >= Math.abs(dy)) {
            float baseX = dx <= SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? left : right;
            float tipX = dx <= SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? right : left;
            return new float[][]{new float[]{baseX, top}, new float[]{baseX, bottom}, new float[]{tipX, centerY}};
        }
        float baseY = dy <= SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? top : bottom;
        float tipY = dy <= SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? bottom : top;
        return new float[][]{new float[]{left, baseY}, new float[]{right, baseY}, new float[]{centerX, tipY}};
    }

    static Map<Integer, float[][]> shapes(Map<Integer, float[]> boxes, String shape) {
        Map<Integer, float[][]> result = new TreeMap<>();
        for (Map.Entry<Integer, float[]> box : new TreeMap(boxes).entrySet()) {
            float[] edges = box.getValue();
            result.put(box.getKey(), TRIANGLE.equals(shape) ? triangle(edges) : new float[][]{new float[]{edges[0], edges[1]}, new float[]{edges[2], edges[3]}});
        }
        return result;
    }

    static String encode(Map<Integer, float[][]> polygons, int maxPlayersPerBox) {
        StringBuilder json = new StringBuilder("{\"startboxes\":[");
        int boxIndex = 0;
        for (float[][] polygon : polygons.values()) {
            int i = boxIndex;
            boxIndex++;
            json.append(i > 0 ? ",{\"poly\":[" : "{\"poly\":[");
            int i2 = 0;
            while (i2 < polygon.length) {
                json.append(i2 > 0 ? ",{\"x\":" : "{\"x\":").append(number(polygon[i2][0])).append(",\"y\":").append(number(polygon[i2][1])).append('}');
                i2++;
            }
            json.append("]}");
        }
        json.append("],\"maxPlayersPerStartbox\":").append(Math.max(1, Math.min(16, maxPlayersPerBox))).append('}');
        byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
        Deflater deflater = new Deflater(9);
        deflater.setInput(bytes);
        deflater.finish();
        byte[] buffer = new byte[bytes.length + 64];
        int length = deflater.deflate(buffer);
        deflater.end();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(Arrays.copyOf(buffer, length));
    }

    private static String number(float value) {
        double scaled = ((double) Math.round(Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, value)) * 2000.0f)) / 10.0d;
        return scaled == Math.rint(scaled) ? String.valueOf((int) scaled) : String.valueOf(scaled);
    }

    static Map<Integer, float[][]> decode(String value) {
        String json;
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            byte[] raw = Base64.getUrlDecoder().decode(value.trim().replace('+', '-').replace('/', '_').replaceAll("=+$", ""));
            try {
                Inflater inflater = new Inflater();
                inflater.setInput(raw);
                byte[] buffer = new byte[65536];
                int length = inflater.inflate(buffer);
                inflater.end();
                json = new String(buffer, 0, length, StandardCharsets.UTF_8);
            } catch (Exception e) {
                json = new String(raw, StandardCharsets.UTF_8);
            }
            Object obj = MiniJson.parse(json);
            if (obj instanceof Map) {
                Map<?, ?> arrangement = (Map) obj;
                Object obj2 = arrangement.get("startboxes");
                if (!(obj2 instanceof List)) {
                    return null;
                }
                List<?> boxes = (List) obj2;
                Map<Integer, float[][]> result = new TreeMap<>();
                int index = 0;
                for (Object box : boxes) {
                    if (box instanceof Map) {
                        Map<?, ?> entry = (Map) box;
                        Object obj3 = entry.get("poly");
                        if (obj3 instanceof List) {
                            List<?> points = (List) obj3;
                            if (points.size() >= 2) {
                                float[][] polygon = new float[points.size()][];
                                for (int i = 0; i < polygon.length; i++) {
                                    Object obj4 = points.get(i);
                                    if (!(obj4 instanceof Map)) {
                                        return null;
                                    }
                                    Map<?, ?> point = (Map) obj4;
                                    Object obj5 = point.get("x");
                                    if (obj5 instanceof Number) {
                                        Number x = (Number) obj5;
                                        Object obj6 = point.get("y");
                                        if (!(obj6 instanceof Number)) {
                                            return null;
                                        }
                                        Number y = (Number) obj6;
                                        polygon[i] = new float[]{x.floatValue() / 200.0f, y.floatValue() / 200.0f};
                                    } else {
                                        return null;
                                    }
                                }
                                result.put(Integer.valueOf(index), polygon);
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    }
                    index++;
                }
                return result;
            }
            return null;
        } catch (RuntimeException e2) {
            return null;
        }
    }
}

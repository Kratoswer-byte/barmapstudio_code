package com.faforever.neroxis.map.placement;

import com.faforever.neroxis.map.AIMarker;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.util.vector.Vector2;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/placement/AIMarkerPlacer.class */
public class AIMarkerPlacer {
    public static void placeAIMarkers(BooleanMask passable, List<AIMarker> markers, String nameFormat) {
        LinkedHashSet<Vector2> rawCoordinates = new LinkedHashSet<>(passable.getSpacedCoordinatesEqualTo(true, 32.0f, 8));
        rawCoordinates.addAll(passable.copyAsDistanceField().copyAsLocalMaximums(Float.valueOf(8.0f), Float.valueOf(passable.getSize())).getSpacedCoordinatesEqualTo(true, 16.0f, 4));
        LinkedHashSet<Vector2> unusedCoordinates = new LinkedHashSet<>();
        rawCoordinates.forEach(location -> {
            if (!unusedCoordinates.contains(location)) {
                if (!passable.inTeam(location, false)) {
                    unusedCoordinates.add(location);
                } else {
                    rawCoordinates.forEach(location1 -> {
                        if (location != location1 && location.getDistance(location1) < 64.0f) {
                            LinkedHashSet<Vector2> lineCoordinates = location.getLine(location1);
                            boolean connected = !lineCoordinates.removeIf(loc -> {
                                return (passable.inBounds(loc) && ((Boolean) passable.get(loc)).booleanValue()) ? false : true;
                            });
                            if (connected) {
                                unusedCoordinates.add(location1);
                            }
                        }
                    });
                }
            }
        });
        rawCoordinates.removeAll(unusedCoordinates);
        List<Vector2> coordinates = rawCoordinates.stream().map((v0) -> {
            return v0.roundToNearestHalfPoint();
        }).toList();
        for (int i = 0; i < coordinates.size(); i++) {
            int index = i;
            Vector2 coordinate = coordinates.get(i);
            AIMarker aiMarker = new AIMarker(String.format(nameFormat, Integer.valueOf(index)), coordinate, (SequencedSet<String>) new LinkedHashSet());
            markers.add(aiMarker);
            List<Vector2> symmetryPoints = passable.getSymmetryPoints(aiMarker.getPosition(), SymmetryType.SPAWN);
            symmetryPoints.forEach(symmetryPoint -> {
                markers.add(new AIMarker(String.format(nameFormat + "s%d", Integer.valueOf(index), Integer.valueOf(symmetryPoints.indexOf(symmetryPoint))), symmetryPoint, (SequencedSet<String>) new LinkedHashSet()));
            });
        }
        markers.forEach(aiMarker2 -> {
            markers.forEach(aiMarker1 -> {
                if (aiMarker2 != aiMarker1 && aiMarker2.getPosition().getXZDistance(aiMarker1.getPosition()) <= 128.0f) {
                    LinkedHashSet<Vector2> lineCoordinates = aiMarker2.getPosition().getXZLine(aiMarker1.getPosition());
                    boolean connected = !lineCoordinates.removeIf(location2 -> {
                        return (passable.inBounds(location2) && ((Boolean) passable.get(location2)).booleanValue()) ? false : true;
                    });
                    if (connected) {
                        aiMarker2.addNeighbor(aiMarker1.getId());
                    }
                }
            });
        });
    }

    public static void placeAirAIMarkers(SCMap map) {
        float airMarkerConnectionDistance = ((float) StrictMath.sqrt(64.0f * 64.0f * 2.0f)) + 1.0f;
        List<Vector2> airCoordinates = new BooleanMask(map.getSize() + 1, (RandomGenerator.SplittableGenerator) null, new SymmetrySettings(Symmetry.NONE)).getSpacedCoordinates(64.0f, ((int) 64.0f) / 8).stream().map((v0) -> {
            return v0.roundToNearestHalfPoint();
        }).toList();
        for (int i = 0; i < airCoordinates.size(); i++) {
            Vector2 location = airCoordinates.get(i);
            map.addAirMarker(new AIMarker(String.format("AirPN%d", Integer.valueOf(i)), location, (SequencedSet<String>) new LinkedHashSet()));
        }
        map.getAirAIMarkers().forEach(aiMarker -> {
            map.getAirAIMarkers().forEach(aiMarker1 -> {
                if (aiMarker != aiMarker1 && aiMarker.getPosition().getXZDistance(aiMarker1.getPosition()) < airMarkerConnectionDistance) {
                    aiMarker.addNeighbor(aiMarker1.getId());
                }
            });
        });
    }
}

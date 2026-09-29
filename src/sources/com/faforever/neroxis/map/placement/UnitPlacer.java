package com.faforever.neroxis.map.placement;

import com.faforever.neroxis.bases.BaseTemplate;
import com.faforever.neroxis.bases.BaseTemplateLoader;
import com.faforever.neroxis.map.Army;
import com.faforever.neroxis.map.Group;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.map.Unit;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.util.vector.Vector2;
import java.io.IOException;
import java.util.List;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/placement/UnitPlacer.class */
public class UnitPlacer {
    public static final String[] T1_Land = {"UEL0201", "URL0107", "UAL0201", "XSL0201"};
    public static final String[] T2_Land = {"DRL0204", "URL0202", "DEL0204", "UEL0202", "UAL0202", "XAL0203", "XSL0203", "XSL0202"};
    public static final String[] T3_Land = {"XEL0305", "UEL0303", "URL0303", "XRL0305", "UAL0303", "XSL0303"};
    public static final String[] T2_Navy = {"URS0201"};
    public static final String[] Navy_Factory = {"ZAB9503", "ZEB9503", "ZRB9503", "ZSB9503"};
    public static final String[] MEDIUM_ENEMY = {"/base_template/UEFMedium.lua", "/base_template/AeonMedium.lua"};
    public static final String[] MEDIUM_RECLAIM = {"/base_template/CybranMediumReclaim.lua", "/base_template/UEFMediumReclaim.lua", "/base_template/CybranBrick.lua", "/base_template/CybranMantisBase.lua", "/base_template/RadarAndPDBase.lua", "/base_template/UEFPegenBase.lua", "/base_template/UEFTitans.lua"};
    public static final int MAX_UNIT_COUNT = 800;
    private final RandomGenerator.SplittableGenerator random;

    public UnitPlacer(RandomGenerator.SplittableGenerator random) {
        this.random = random.split();
    }

    public void placeBases(BooleanMask spawnMask, String[] templates, Army army, Group group, float separation) throws IOException {
        if (templates.length > 0) {
            String templateFile = templates[this.random.nextInt(templates.length)];
            if (!spawnMask.getSymmetrySettings().spawnSymmetry().isPerfectSymmetry()) {
                spawnMask.limitToCenteredCircle(spawnMask.getSize() / 2.0f);
            }
            spawnMask.limitToSymmetryRegion();
            SequencedMap<String, SequencedSet<Vector2>> units = BaseTemplateLoader.loadUnits(templateFile);
            int numUnitsInTemplate = units.values().stream().mapToInt((v0) -> {
                return v0.size();
            }).sum();
            List<Vector2> coordinates = spawnMask.getRandomCoordinates(separation).stream().limit((MAX_UNIT_COUNT - army.getNumUnits()) / numUnitsInTemplate).map((v0) -> {
                return v0.roundToNearestHalfPoint();
            }).toList();
            for (Vector2 location : coordinates) {
                BaseTemplate base = new BaseTemplate(location, units);
                base.addUnits(army, group);
                List<Vector2> symmetryPoints = spawnMask.getSymmetryPoints(location, SymmetryType.SPAWN).stream().map((v0) -> {
                    return v0.roundToNearestHalfPoint();
                }).toList();
                symmetryPoints.forEach(symmetryPoint -> {
                    BaseTemplate symBase = new BaseTemplate(symmetryPoint, base.units());
                    if (!spawnMask.inTeam(symmetryPoint, false)) {
                        symBase.flip(spawnMask.getSymmetrySettings().spawnSymmetry());
                    }
                    symBase.addUnits(army, group);
                });
            }
        }
    }

    public void placeUnits(BooleanMask spawnMask, String[] types, Army army, Group group, float separation) {
        placeUnits(spawnMask, types, army, group, separation, separation);
    }

    public void placeUnits(BooleanMask spawnMask, String[] types, Army army, Group group, float minSeparation, float maxSeparation) {
        if (types != null && types.length > 0) {
            spawnMask.limitToSymmetryRegion();
            List<Vector2> coordinates = spawnMask.getRandomCoordinates(minSeparation, maxSeparation).stream().limit((MAX_UNIT_COUNT - army.getNumUnits()) / spawnMask.getSymmetrySettings().spawnSymmetry().getNumSymPoints()).map((v0) -> {
                return v0.roundToNearestHalfPoint();
            }).toList();
            String type = types[this.random.nextInt(types.length)];
            float rot = this.random.nextFloat() * 3.14159f;
            for (Vector2 location : coordinates) {
                int groupID = group.getUnitCount();
                Unit unit = new Unit(String.format("%s %s Unit %d", army.getId(), group.getId(), Integer.valueOf(groupID)), type, location, rot);
                group.addUnit(unit);
                List<Vector2> symmetryPoints = spawnMask.getSymmetryPoints(unit.getPosition(), SymmetryType.SPAWN).stream().map((v0) -> {
                    return v0.roundToNearestHalfPoint();
                }).toList();
                List<Float> symmetryRotation = spawnMask.getSymmetryRotations(unit.getRotation());
                for (int i = 0; i < symmetryPoints.size(); i++) {
                    group.addUnit(new Unit(String.format("%s %s Unit %d sym %s", army.getId(), group.getId(), Integer.valueOf(groupID), Integer.valueOf(i)), type, symmetryPoints.get(i), symmetryRotation.get(i).floatValue()));
                }
            }
        }
    }
}

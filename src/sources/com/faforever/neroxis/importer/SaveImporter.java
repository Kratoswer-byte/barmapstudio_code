package com.faforever.neroxis.importer;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.lua.Lua;
import com.faforever.neroxis.map.AIMarker;
import com.faforever.neroxis.map.Army;
import com.faforever.neroxis.map.Group;
import com.faforever.neroxis.map.Marker;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Spawn;
import com.faforever.neroxis.map.Unit;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import com.faforever.neroxis.util.vector.Vector4;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.SwitchBootstraps;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/importer/SaveImporter.class */
public class SaveImporter {
    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static void importSave(Path folderPath, SCMap map) throws IOException, MatchException {
        Stream<Path> paths = Files.list(folderPath);
        try {
            Path savePath = paths.filter(file -> {
                return file.getFileName().toString().endsWith("_save.lua");
            }).findFirst().orElseThrow(() -> {
                return new IllegalStateException("No save file in map folder");
            });
            if (paths != null) {
                paths.close();
            }
            InputStream inputStream = Files.newInputStream(savePath, new OpenOption[0]);
            try {
                Stream<? extends Lua.Statement> stream = Lua.parse(inputStream).statements().stream();
                Class<Lua.Statement.Assignment> cls = Lua.Statement.Assignment.class;
                Objects.requireNonNull(Lua.Statement.Assignment.class);
                Stream<? extends Lua.Statement> streamFilter = stream.filter((v1) -> {
                    return r1.isInstance(v1);
                });
                Class<Lua.Statement.Assignment> cls2 = Lua.Statement.Assignment.class;
                Objects.requireNonNull(Lua.Statement.Assignment.class);
                Stream map2 = streamFilter.map((v1) -> {
                    return r1.cast(v1);
                }).filter(SaveImporter::isScenarioAssignment).map((v0) -> {
                    return v0.values();
                }).map((v0) -> {
                    return v0.getFirst();
                });
                Class<Lua.Value.Table> cls3 = Lua.Value.Table.class;
                Objects.requireNonNull(Lua.Value.Table.class);
                Lua.Value.Table luaScenario = (Lua.Value.Table) map2.map((v1) -> {
                    return r1.cast(v1);
                }).findFirst().orElseThrow();
                if (inputStream != null) {
                    inputStream.close();
                }
                importPlayableArea(map, luaScenario);
                importMarkers(map, luaScenario);
                importArmies(map, luaScenario);
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (Throwable th3) {
            if (paths != null) {
                try {
                    paths.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
            }
            throw th3;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private static void importPlayableArea(SCMap map, Lua.Value.Table luaScenario) throws MatchException {
        Lua.Expression areasExpression = luaScenario.get("Areas");
        if (areasExpression == null) {
            return;
        }
        if (!(areasExpression instanceof Lua.Value.Table)) {
            throw new IllegalStateException("Invalid Areas expression %s".formatted(areasExpression));
        }
        Lua.Value.Table areasTable = (Lua.Value.Table) areasExpression;
        Lua.Expression area1 = areasTable.get("AREA_1");
        if (area1 == null) {
            return;
        }
        Lua.Expression expression = areasTable.get("AREA_1");
        if (!(expression instanceof Lua.Value.Table)) {
            throw new IllegalStateException("Invalid Areas expression %s".formatted(areasExpression));
        }
        Lua.Value.Table area1Table = (Lua.Value.Table) expression;
        Lua.Value.Table rectangleTable = extractTableFromExpression(area1Table.get("rectangle"));
        Lua.Expression expression2 = rectangleTable.get((Number) 1);
        if (expression2 instanceof Lua.Value.Num) {
            try {
                double x0 = ((Lua.Value.Num) expression2).value();
                if (1 != 0) {
                    Lua.Expression expression3 = rectangleTable.get((Number) 2);
                    if (expression3 instanceof Lua.Value.Num) {
                        double y0 = ((Lua.Value.Num) expression3).value();
                        if (1 != 0) {
                            Lua.Expression expression4 = rectangleTable.get((Number) 3);
                            if (expression4 instanceof Lua.Value.Num) {
                                double x1 = ((Lua.Value.Num) expression4).value();
                                if (1 != 0) {
                                    Lua.Expression expression5 = rectangleTable.get((Number) 4);
                                    if (expression5 instanceof Lua.Value.Num) {
                                        double y1 = ((Lua.Value.Num) expression5).value();
                                        if (1 != 0) {
                                            map.setPlayableArea(new Vector4((float) x0, (float) y0, (float) x1, (float) y1));
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                throw new MatchException(th.toString(), th);
            }
        }
        throw new IllegalStateException("Invalid rectangle value %s".formatted(rectangleTable));
    }

    private static void importMarkers(SCMap map, Lua.Value.Table luaScenario) {
        Lua.Expression masterChainExpression = luaScenario.get("MasterChain");
        if (masterChainExpression == null) {
            return;
        }
        if (masterChainExpression instanceof Lua.Value.Table) {
            Lua.Value.Table masterChainTable = (Lua.Value.Table) masterChainExpression;
            Lua.Expression expression = masterChainTable.get("_MASTERCHAIN_");
            if (expression instanceof Lua.Value.Table) {
                Lua.Value.Table masterChainSubTable = (Lua.Value.Table) expression;
                Lua.Expression expression2 = masterChainSubTable.get("Markers");
                if (expression2 instanceof Lua.Value.Table) {
                    Lua.Value.Table markersTable = (Lua.Value.Table) expression2;
                    markersTable.forEach((key, value) -> {
                        if (!(key instanceof Lua.Value.Str)) {
                            throw new IllegalStateException("Invalid marker id %s".formatted(key));
                        }
                        Lua.Value.Str $b$0 = (Lua.Value.Str) key;
                        try {
                            String patt1$temp = $b$0.value();
                            if (!(value instanceof Lua.Value.Table)) {
                                throw new IllegalStateException("Invalid marker value %s".formatted(value));
                            }
                            Lua.Value.Table markerTable = (Lua.Value.Table) value;
                            addMarker(markerTable, patt1$temp, map);
                        } catch (Throwable th) {
                            throw new MatchException(th.toString(), th);
                        }
                    });
                    return;
                }
            }
        }
        throw new IllegalStateException("Invalid master chain expression %s".formatted(masterChainExpression));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private static void addMarker(Lua.Value.Table markerTable, String id, SCMap map) throws MatchException {
        String type = extractStringFromExpression(markerTable.get("type"));
        Lua.Value.Table positionTable = extractTableFromExpression(markerTable.get("position"));
        Lua.Expression expression = positionTable.get((Number) 1);
        if (expression instanceof Lua.Value.Num) {
            try {
                double x = ((Lua.Value.Num) expression).value();
                if (1 != 0) {
                    Lua.Expression expression2 = positionTable.get((Number) 2);
                    if (expression2 instanceof Lua.Value.Num) {
                        double y = ((Lua.Value.Num) expression2).value();
                        if (1 != 0) {
                            Lua.Expression expression3 = positionTable.get((Number) 3);
                            if (expression3 instanceof Lua.Value.Num) {
                                double z = ((Lua.Value.Num) expression3).value();
                                if (1 != 0) {
                                    Vector3 location = new Vector3((float) x, (float) y, (float) z);
                                    switch (type) {
                                        case "Mass":
                                            map.addMex(new Marker(id, location));
                                            return;
                                        case "Hydrocarbon":
                                            map.addHydro(new Marker(id, location));
                                            return;
                                        case "Blank Marker":
                                            if (id.contains("ARMY")) {
                                                map.addSpawn(new Spawn(id, location, new Vector2(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 0));
                                                return;
                                            } else {
                                                map.addBlank(new Marker(id, location));
                                                return;
                                            }
                                        case "Air Path Node":
                                            String adjacentTo = extractStringFromExpression(markerTable.get("adjacentTo"));
                                            SequencedSet<String> neighbors = (SequencedSet) Arrays.stream(adjacentTo.split(" ")).filter(s -> {
                                                return !s.isEmpty();
                                            }).collect(Collectors.toCollection(LinkedHashSet::new));
                                            map.addAirMarker(new AIMarker(id, location, neighbors));
                                            return;
                                        case "Amphibious Path Node":
                                            String adjacentTo2 = extractStringFromExpression(markerTable.get("adjacentTo"));
                                            SequencedSet<String> neighbors2 = (SequencedSet) Arrays.stream(adjacentTo2.split(" ")).filter(s2 -> {
                                                return !s2.isEmpty();
                                            }).collect(Collectors.toCollection(LinkedHashSet::new));
                                            map.addAmphibiousMarker(new AIMarker(id, location, neighbors2));
                                            return;
                                        case "Water Path Node":
                                            String adjacentTo3 = extractStringFromExpression(markerTable.get("adjacentTo"));
                                            SequencedSet<String> neighbors3 = (SequencedSet) Arrays.stream(adjacentTo3.split(" ")).filter(s3 -> {
                                                return !s3.isEmpty();
                                            }).collect(Collectors.toCollection(LinkedHashSet::new));
                                            map.addNavyMarker(new AIMarker(id, location, neighbors3));
                                            return;
                                        case "Land Path Node":
                                            String adjacentTo4 = extractStringFromExpression(markerTable.get("adjacentTo"));
                                            SequencedSet<String> neighbors4 = (SequencedSet) Arrays.stream(adjacentTo4.split(" ")).filter(s4 -> {
                                                return !s4.isEmpty();
                                            }).collect(Collectors.toCollection(LinkedHashSet::new));
                                            map.addLandMarker(new AIMarker(id, location, neighbors4));
                                            return;
                                        case "Rally Point":
                                            map.addRallyMarker(new AIMarker(id, location, (SequencedSet<String>) new LinkedHashSet()));
                                            return;
                                        case "Expansion Area":
                                            map.addExpansionMarker(new AIMarker(id, location, (SequencedSet<String>) new LinkedHashSet()));
                                            return;
                                        case "Large Expansion Area":
                                            map.addLargeExpansionMarker(new AIMarker(id, location, (SequencedSet<String>) new LinkedHashSet()));
                                            return;
                                        case "Naval Area":
                                            map.addNavalAreaMarker(new AIMarker(id, location, (SequencedSet<String>) new LinkedHashSet()));
                                            return;
                                        case "Naval Rally Point":
                                            map.addNavyRallyMarker(new AIMarker(id, location, (SequencedSet<String>) new LinkedHashSet()));
                                            return;
                                        default:
                                            return;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                throw new MatchException(th.toString(), th);
            }
        }
        throw new IllegalArgumentException("Invalid position table %s".formatted(markerTable.get("position")));
    }

    private static void importArmies(SCMap map, Lua.Value.Table luaScenario) {
        Lua.Expression armiesExpression = luaScenario.get("Armies");
        if (armiesExpression == null) {
            return;
        }
        if (!(armiesExpression instanceof Lua.Value.Table)) {
            throw new IllegalStateException("Invalid armies expression %s".formatted(armiesExpression));
        }
        Lua.Value.Table armiesTable = (Lua.Value.Table) armiesExpression;
        armiesTable.forEach((key, value) -> {
            if (!(key instanceof Lua.Value.Str)) {
                throw new IllegalStateException("Invalid army id %s".formatted(key));
            }
            Lua.Value.Str $b$0 = (Lua.Value.Str) key;
            try {
                String patt1$temp = $b$0.value();
                if (!(value instanceof Lua.Value.Table)) {
                    throw new IllegalStateException("Invalid army value %s".formatted(value));
                }
                Lua.Value.Table armyTable = (Lua.Value.Table) value;
                addArmy(armyTable, patt1$temp, map);
            } catch (Throwable th) {
                throw new MatchException(th.toString(), th);
            }
        });
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private static void addArmy(Lua.Value.Table armyTable, String armyName, SCMap map) throws MatchException {
        Army army = new Army(armyName);
        map.addArmy(army);
        Lua.Expression unitsExpression = armyTable.get("Units");
        if (unitsExpression == null) {
            return;
        }
        Lua.Value.Table unitsTable = extractTableFromExpression(unitsExpression);
        Lua.Expression groupsExpression = unitsTable.get("Units");
        if (groupsExpression == null) {
            return;
        }
        Lua.Value.Table groupsTable = extractTableFromExpression(groupsExpression);
        groupsTable.forEach((key, value) -> {
            if (!(key instanceof Lua.Value.Str)) {
                throw new IllegalStateException("Invalid group id %s".formatted(key));
            }
            Lua.Value.Str $b$0 = (Lua.Value.Str) key;
            try {
                String patt1$temp = $b$0.value();
                Lua.Value.Table groupTable = extractTableFromExpression(value);
                addGroup(groupTable, patt1$temp, army);
            } catch (Throwable th) {
                throw new MatchException(th.toString(), th);
            }
        });
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private static void addGroup(Lua.Value.Table groupTable, String groupName, Army army) throws MatchException {
        Group group = new Group(groupName);
        army.addGroup(group);
        Lua.Expression unitsExpression = groupTable.get("Units");
        if (unitsExpression == null) {
            return;
        }
        Lua.Value.Table unitsTable = extractTableFromExpression(unitsExpression);
        unitsTable.forEach((key, value) -> {
            if (!(key instanceof Lua.Value.Str)) {
                throw new IllegalStateException("Invalid unit id %s".formatted(key));
            }
            Lua.Value.Str $b$0 = (Lua.Value.Str) key;
            try {
                String patt1$temp = $b$0.value();
                if (!(value instanceof Lua.Value.Table)) {
                    throw new IllegalStateException("Invalid unit value %s".formatted(value));
                }
                Lua.Value.Table unitTable = (Lua.Value.Table) value;
                Lua.Expression patt3$temp = unitTable.get("type");
                if (!(patt3$temp instanceof Lua.Value.Str)) {
                    throw new IllegalStateException("Invalid unit type %s".formatted(unitTable.get("type")));
                }
                Lua.Value.Str $b$2 = (Lua.Value.Str) patt3$temp;
                String patt4$temp = $b$2.value();
                Lua.Value.Table positionTable = extractTableFromExpression(unitTable.get("Position"));
                Lua.Expression patt6$temp = positionTable.get((Number) 1);
                if (patt6$temp instanceof Lua.Value.Num) {
                    Lua.Value.Num $b$5 = (Lua.Value.Num) patt6$temp;
                    double patt7$temp = $b$5.value();
                    if (1 != 0) {
                        Lua.Expression patt9$temp = positionTable.get((Number) 2);
                        if (patt9$temp instanceof Lua.Value.Num) {
                            Lua.Value.Num $b$8 = (Lua.Value.Num) patt9$temp;
                            double patt10$temp = $b$8.value();
                            if (1 != 0) {
                                Lua.Expression patt12$temp = positionTable.get((Number) 3);
                                if (patt12$temp instanceof Lua.Value.Num) {
                                    Lua.Value.Num $b$11 = (Lua.Value.Num) patt12$temp;
                                    double patt13$temp = $b$11.value();
                                    if (1 != 0) {
                                        Lua.Value.Table orientationTable = extractTableFromExpression(unitTable.get("Orientation"));
                                        Lua.Expression patt15$temp = orientationTable.get((Number) 2);
                                        if (patt15$temp instanceof Lua.Value.Num) {
                                            Lua.Value.Num $b$14 = (Lua.Value.Num) patt15$temp;
                                            double patt16$temp = $b$14.value();
                                            if (1 != 0) {
                                                Vector3 location = new Vector3((float) patt7$temp, (float) patt10$temp, (float) patt13$temp);
                                                group.addUnit(new Unit(patt1$temp, patt4$temp, location, (float) patt16$temp));
                                                return;
                                            }
                                        }
                                        throw new IllegalArgumentException("Invalid orientation table %s".formatted(unitTable.get("position")));
                                    }
                                }
                            }
                        }
                    }
                }
                throw new IllegalArgumentException("Invalid position table %s".formatted(unitTable.get("position")));
            } catch (Throwable th) {
                throw new MatchException(th.toString(), th);
            }
        });
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    /* JADX WARN: Switch 'out' block B:3:0x0004 for B:4:0x000b already processed. Defaulting to fallback option. */
    private static Lua.Value.Table extractTableFromExpression(Lua.Expression expression) throws MatchException {
        int i = 0;
        while (true) {
            switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, Lua.Expression.class, Integer.TYPE), Lua.FunctionCall.Direct.class, Lua.Value.Table.class).dynamicInvoker().invoke(expression, i) /* invoke-custom */) {
                case -1:
                default:
                    throw new IllegalArgumentException("Could not extract table from expression %s".formatted(expression));
                case 0:
                    Lua.FunctionCall.Direct direct = (Lua.FunctionCall.Direct) expression;
                    try {
                        Lua.Variable variableReceiver = direct.receiver();
                        int i2 = 0;
                        while (true) {
                            switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, Lua.Variable.class, Integer.TYPE), Lua.Variable.Named.class, Lua.Variable.Named.class, Lua.Variable.Named.class).dynamicInvoker().invoke(variableReceiver, i2) /* invoke-custom */) {
                                case 0:
                                    Lua.Variable.Named named = (Lua.Variable.Named) variableReceiver;
                                    List<? extends Lua.Expression> arguments = direct.arguments();
                                    String name = named.name();
                                    List<? extends Lua.MemberAccessor> memberAccessors = named.memberAccessors();
                                    if (memberAccessors.isEmpty() && name.equals("RECTANGLE") && arguments.size() == 4) {
                                        return convertArgumentsToIndexedTable(arguments);
                                    }
                                    i2 = 1;
                                    break;
                                case 1:
                                    Lua.Variable.Named named2 = (Lua.Variable.Named) variableReceiver;
                                    List<? extends Lua.Expression> arguments2 = direct.arguments();
                                    String name2 = named2.name();
                                    List<? extends Lua.MemberAccessor> memberAccessors2 = named2.memberAccessors();
                                    if (memberAccessors2.isEmpty() && name2.equals("VECTOR3") && arguments2.size() == 3) {
                                        return convertArgumentsToIndexedTable(arguments2);
                                    }
                                    i2 = 2;
                                    break;
                                case 2:
                                    Lua.Variable.Named named3 = (Lua.Variable.Named) variableReceiver;
                                    List<? extends Lua.Expression> arguments3 = direct.arguments();
                                    String name3 = named3.name();
                                    List<? extends Lua.MemberAccessor> memberAccessors3 = named3.memberAccessors();
                                    if (memberAccessors3.isEmpty() && name3.equals("GROUP") && arguments3.size() == 1) {
                                        Object first = arguments3.getFirst();
                                        if (first instanceof Lua.Value.Table) {
                                            Lua.Value.Table table = (Lua.Value.Table) first;
                                            return table;
                                        }
                                    }
                                    i2 = 3;
                                    break;
                            }
                        }
                        i = 1;
                    } catch (Throwable th) {
                        throw new MatchException(th.toString(), th);
                    }
                    break;
                case 1:
                    Lua.Value.Table table2 = (Lua.Value.Table) expression;
                    return table2;
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private static String extractStringFromExpression(Lua.Expression expression) throws MatchException {
        String str;
        int i = 0;
        while (true) {
            switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, Lua.Expression.class, Integer.TYPE), Lua.FunctionCall.Direct.class, Lua.Value.Str.class).dynamicInvoker().invoke(expression, i) /* invoke-custom */) {
                case -1:
                default:
                    throw new IllegalArgumentException("Could not extract string from expression %s".formatted(expression));
                case 0:
                    Lua.FunctionCall.Direct direct = (Lua.FunctionCall.Direct) expression;
                    try {
                        Lua.Variable variableReceiver = direct.receiver();
                        if (variableReceiver instanceof Lua.Variable.Named) {
                            Lua.Variable.Named named = (Lua.Variable.Named) variableReceiver;
                            List<? extends Lua.Expression> arguments = direct.arguments();
                            String name = named.name();
                            List<? extends Lua.MemberAccessor> memberAccessors = named.memberAccessors();
                            if (memberAccessors.isEmpty() && name.equals("STRING") && arguments.size() == 1) {
                                Object first = arguments.getFirst();
                                if (first instanceof Lua.Value.Str) {
                                    String value = ((Lua.Value.Str) first).value();
                                    str = value;
                                    break;
                                }
                            }
                        }
                        i = 1;
                    } catch (Throwable th) {
                        throw new MatchException(th.toString(), th);
                    }
                    break;
                case 1:
                    String value2 = ((Lua.Value.Str) expression).value();
                    str = value2;
                    break;
            }
        }
        return str;
    }

    private static Lua.Value.Table convertArgumentsToIndexedTable(List<? extends Lua.Expression> arguments) {
        Map<Lua.Value.Num, Lua.Expression> contents = new HashMap<>();
        int index = 1;
        for (Lua.Expression argument : arguments) {
            int i = index;
            index++;
            contents.put(new Lua.Value.Num(i), argument);
        }
        return new Lua.Value.Table(contents);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private static boolean isScenarioAssignment(Lua.Statement.Assignment assignment) throws MatchException {
        if (assignment.targets().size() != 1) {
            return false;
        }
        Object first = assignment.targets().getFirst();
        if (first instanceof Lua.Variable.Named) {
            Lua.Variable.Named named = (Lua.Variable.Named) first;
            try {
                String name = named.name();
                List<? extends Lua.MemberAccessor> memberAccessors = named.memberAccessors();
                if (!name.equals("Scenario") || !memberAccessors.isEmpty()) {
                    return false;
                }
                List<? extends Lua.Expression> values = assignment.values();
                if (values.size() != 1) {
                    return false;
                }
                return values.getFirst() instanceof Lua.Value.Table;
            } catch (Throwable th) {
                throw new MatchException(th.toString(), th);
            }
        }
        return false;
    }
}

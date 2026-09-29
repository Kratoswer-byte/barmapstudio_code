package com.faforever.neroxis.importer;

import com.faforever.neroxis.lua.Lua;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.util.vector.Vector2;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.SwitchBootstraps;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/importer/ScenarioImporter.class */
public class ScenarioImporter {
    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static void importScenario(Path folderPath, SCMap map) throws IOException, MatchException {
        Stream<Path> paths = Files.list(folderPath);
        try {
            Path scenarioPath = paths.filter(file -> {
                return file.getFileName().toString().endsWith("_scenario.lua");
            }).findFirst().orElseThrow(() -> {
                return new IllegalStateException("No scenario file in map folder");
            });
            if (paths != null) {
                paths.close();
            }
            InputStream inputStream = Files.newInputStream(scenarioPath, new OpenOption[0]);
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
                }).filter(ScenarioImporter::isScenarioInfoAssignment).map((v0) -> {
                    return v0.values();
                }).map((v0) -> {
                    return v0.getFirst();
                });
                Class<Lua.Value.Table> cls3 = Lua.Value.Table.class;
                Objects.requireNonNull(Lua.Value.Table.class);
                Lua.Value.Table luaScenarioInfo = (Lua.Value.Table) map2.map((v1) -> {
                    return r1.cast(v1);
                }).findFirst().orElseThrow();
                if (inputStream != null) {
                    inputStream.close();
                }
                Lua.Expression expression = luaScenarioInfo.get("name");
                if (!(expression instanceof Lua.Value.Str)) {
                    throw new IllegalArgumentException("ScenarioInfo.name is not a string");
                }
                try {
                    String name = ((Lua.Value.Str) expression).value();
                    map.setName(name);
                    Lua.Expression expression2 = luaScenarioInfo.get("description");
                    if (!(expression2 instanceof Lua.Value.Str)) {
                        throw new IllegalArgumentException("ScenarioInfo.description is not a string");
                    }
                    String description = ((Lua.Value.Str) expression2).value();
                    map.setDescription(description);
                    Lua.Expression expression3 = luaScenarioInfo.get("norushradius");
                    if (expression3 instanceof Lua.Value.Num) {
                        double noRushRadius = ((Lua.Value.Num) expression3).value();
                        if (1 != 0) {
                            map.setNoRushRadius((float) noRushRadius);
                            map.getSpawns().forEach(spawn -> {
                                Lua.Expression patt1$temp = luaScenarioInfo.get("norushoffsetX_" + spawn.getId());
                                if (patt1$temp instanceof Lua.Value.Num) {
                                    Lua.Value.Num $b$0 = (Lua.Value.Num) patt1$temp;
                                    try {
                                        double patt2$temp = $b$0.value();
                                        if (1 != 0) {
                                            Lua.Expression patt4$temp = luaScenarioInfo.get("norushoffsetY_" + spawn.getId());
                                            if (patt4$temp instanceof Lua.Value.Num) {
                                                Lua.Value.Num $b$3 = (Lua.Value.Num) patt4$temp;
                                                double patt5$temp = $b$3.value();
                                                if (1 != 0) {
                                                    spawn.setNoRushOffset(new Vector2((float) patt2$temp, (float) patt5$temp));
                                                }
                                            }
                                        }
                                    } catch (Throwable th) {
                                        throw new MatchException(th.toString(), th);
                                    }
                                }
                            });
                            Lua.Expression expression4 = luaScenarioInfo.get("Configurations");
                            if (expression4 instanceof Lua.Value.Table) {
                                Lua.Value.Table configurations = (Lua.Value.Table) expression4;
                                Lua.Expression expression5 = configurations.get("standard");
                                if (expression5 instanceof Lua.Value.Table) {
                                    Lua.Value.Table standardTable = (Lua.Value.Table) expression5;
                                    Lua.Expression expression6 = standardTable.get("teams");
                                    if (expression6 instanceof Lua.Value.Table) {
                                        Lua.Value.Table teamsTable = (Lua.Value.Table) expression6;
                                        Stream<? extends Lua.Expression> stream2 = teamsTable.contents().values().stream();
                                        Class<Lua.Value.Table> cls4 = Lua.Value.Table.class;
                                        Objects.requireNonNull(Lua.Value.Table.class);
                                        Stream<? extends Lua.Expression> streamFilter2 = stream2.filter((v1) -> {
                                            return r1.isInstance(v1);
                                        });
                                        Class<Lua.Value.Table> cls5 = Lua.Value.Table.class;
                                        Objects.requireNonNull(Lua.Value.Table.class);
                                        Stream map3 = streamFilter2.map((v1) -> {
                                            return r1.cast(v1);
                                        }).filter(table -> {
                                            Lua.Expression patt1$temp = table.get("name");
                                            if (patt1$temp instanceof Lua.Value.Str) {
                                                Lua.Value.Str $b$0 = (Lua.Value.Str) patt1$temp;
                                                try {
                                                    String patt2$temp = $b$0.value();
                                                    if ("FFA".equals(patt2$temp)) {
                                                        return true;
                                                    }
                                                } catch (Throwable th) {
                                                    throw new MatchException(th.toString(), th);
                                                }
                                            }
                                            return false;
                                        }).map(table2 -> {
                                            return table2.get("armies");
                                        });
                                        Class<Lua.Value.Table> cls6 = Lua.Value.Table.class;
                                        Objects.requireNonNull(Lua.Value.Table.class);
                                        Stream streamFilter3 = map3.filter((v1) -> {
                                            return r1.isInstance(v1);
                                        });
                                        Class<Lua.Value.Table> cls7 = Lua.Value.Table.class;
                                        Objects.requireNonNull(Lua.Value.Table.class);
                                        streamFilter3.map((v1) -> {
                                            return r1.cast(v1);
                                        }).findFirst().ifPresent(armiesTable -> {
                                            int numArmies = armiesTable.contents().size();
                                            List<String> armyOrder = IntStream.range(0, numArmies).mapToObj(i -> {
                                                Lua.Expression selector1$temp = armiesTable.get(Integer.valueOf(i + 1));
                                                switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, Lua.Expression.class, Integer.TYPE), Lua.Value.Str.class, Lua.Expression.class).dynamicInvoker().invoke(selector1$temp, 0) /* invoke-custom */) {
                                                    case -1:
                                                        throw new IllegalStateException("Count out of range");
                                                    case 0:
                                                        Lua.Value.Str $b$0 = (Lua.Value.Str) selector1$temp;
                                                        try {
                                                            String patt3$temp = $b$0.value();
                                                            return patt3$temp;
                                                        } catch (Throwable th) {
                                                            throw new MatchException(th.toString(), th);
                                                        }
                                                    default:
                                                        throw new IllegalStateException("Army is not a string");
                                                }
                                            }).toList();
                                            map.setArmyOrder(armyOrder);
                                        });
                                        return;
                                    }
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                    }
                    throw new IllegalArgumentException("ScenarioInfo.norushradius is not a number");
                } catch (Throwable th) {
                    throw new MatchException(th.toString(), th);
                }
            } catch (Throwable th2) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        } catch (Throwable th4) {
            if (paths != null) {
                try {
                    paths.close();
                } catch (Throwable th5) {
                    th4.addSuppressed(th5);
                }
            }
            throw th4;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private static boolean isScenarioInfoAssignment(Lua.Statement.Assignment assignment) throws MatchException {
        if (assignment.targets().size() != 1) {
            return false;
        }
        Object first = assignment.targets().getFirst();
        if (first instanceof Lua.Variable.Named) {
            Lua.Variable.Named named = (Lua.Variable.Named) first;
            try {
                String name = named.name();
                List<? extends Lua.MemberAccessor> memberAccessors = named.memberAccessors();
                if (!name.equals("ScenarioInfo") || !memberAccessors.isEmpty()) {
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

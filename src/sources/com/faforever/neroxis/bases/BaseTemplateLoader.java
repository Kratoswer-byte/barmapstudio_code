package com.faforever.neroxis.bases;

import com.faforever.neroxis.lua.Lua;
import com.faforever.neroxis.util.FileUtil;
import com.faforever.neroxis.util.ResourceUtil;
import com.faforever.neroxis.util.serial.biome.SCUnitSet;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.SwitchBootstraps;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/bases/BaseTemplateLoader.class */
public class BaseTemplateLoader {
    public static final Comparator<Vector2> VECTOR_COMPARATOR = Comparator.comparing((v0) -> {
        return v0.x();
    }).thenComparing((v0) -> {
        return v0.y();
    });
    private static final Comparator<Map.Entry<String, Vector2>> UNIT_ENTRY_COMPARATOR = Map.Entry.comparingByKey().thenComparing((v0) -> {
        return v0.getValue();
    }, VECTOR_COMPARATOR);

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/bases/BaseTemplateLoader$TemplateType.class */
    public enum TemplateType {
        SCUNITS,
        LUA
    }

    public static SequencedMap<String, SequencedSet<Vector2>> loadUnits(String path) throws IOException {
        InputStream inputStream = (InputStream) Objects.requireNonNull(ResourceUtil.getResourceAsStream(path));
        try {
            if (path.endsWith(".lua")) {
                SequencedMap<String, SequencedSet<Vector2>> sequencedMapLoadUnitsFromLua = loadUnitsFromLua(inputStream);
                if (inputStream != null) {
                    inputStream.close();
                }
                return sequencedMapLoadUnitsFromLua;
            }
            if (path.endsWith(".scunits")) {
                SequencedMap<String, SequencedSet<Vector2>> sequencedMapLoadUnitsFromSCUnits = loadUnitsFromSCUnits(inputStream);
                if (inputStream != null) {
                    inputStream.close();
                }
                return sequencedMapLoadUnitsFromSCUnits;
            }
            throw new IllegalArgumentException("File format not valid");
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
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static SequencedMap<String, SequencedSet<Vector2>> loadUnits(InputStream inputStream, TemplateType type) throws IOException, MatchException {
        switch (type) {
            case SCUNITS:
                return loadUnitsFromSCUnits(inputStream);
            case LUA:
                return loadUnitsFromLua(inputStream);
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }

    private static SequencedMap<String, SequencedSet<Vector2>> loadUnitsFromLua(InputStream inputStream) throws IOException {
        Stream<? extends Lua.Statement> stream = Lua.parse(inputStream).statements().stream();
        Class<Lua.Statement.Assignment> cls = Lua.Statement.Assignment.class;
        Objects.requireNonNull(Lua.Statement.Assignment.class);
        Stream<? extends Lua.Statement> streamFilter = stream.filter((v1) -> {
            return r1.isInstance(v1);
        });
        Class<Lua.Statement.Assignment> cls2 = Lua.Statement.Assignment.class;
        Objects.requireNonNull(Lua.Statement.Assignment.class);
        Stream map = streamFilter.map((v1) -> {
            return r1.cast(v1);
        }).filter(BaseTemplateLoader::isUnitsAssignment).map((v0) -> {
            return v0.values();
        }).map((v0) -> {
            return v0.getFirst();
        });
        Class<Lua.Value.Table> cls3 = Lua.Value.Table.class;
        Objects.requireNonNull(Lua.Value.Table.class);
        Lua.Value.Table luaUnits = (Lua.Value.Table) map.map((v1) -> {
            return r1.cast(v1);
        }).findFirst().orElseThrow();
        return (SequencedMap) luaUnits.contents().entrySet().stream().map(BaseTemplateLoader::extractUnitPositionEntry).sorted(UNIT_ENTRY_COMPARATOR).collect(Collectors.groupingBy((v0) -> {
            return v0.getKey();
        }, LinkedHashMap::new, Collectors.mapping((v0) -> {
            return v0.getValue();
        }, Collectors.toCollection(LinkedHashSet::new))));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private static boolean isUnitsAssignment(Lua.Statement.Assignment assignment) throws MatchException {
        if (assignment.targets().size() != 1) {
            return false;
        }
        Object first = assignment.targets().getFirst();
        if (first instanceof Lua.Variable.Named) {
            Lua.Variable.Named named = (Lua.Variable.Named) first;
            try {
                String name = named.name();
                List<? extends Lua.MemberAccessor> memberAccessors = named.memberAccessors();
                if (!name.equals("Units") || !memberAccessors.isEmpty()) {
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

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private static Map.Entry<String, Vector2> extractUnitPositionEntry(Map.Entry<? extends Lua.Expression, ? extends Lua.Expression> entry) throws MatchException {
        Lua.Expression key = entry.getKey();
        if (!(key instanceof Lua.Value.Str)) {
            throw new IllegalArgumentException("Key must be a string, got: %s".formatted(entry.getKey()));
        }
        try {
            String keyValue = ((Lua.Value.Str) key).value();
            Lua.Expression value = entry.getValue();
            if (!(value instanceof Lua.Value.Table)) {
                throw new IllegalArgumentException("Value must be a table for unit %s, got: %s".formatted(keyValue, entry.getValue()));
            }
            Lua.Value.Table table = (Lua.Value.Table) value;
            Lua.Expression expression = table.get("type");
            if (!(expression instanceof Lua.Value.Str)) {
                throw new IllegalArgumentException("type must be a String for unit %s, got: %s".formatted(keyValue, table.get("type")));
            }
            String type = ((Lua.Value.Str) expression).value();
            Vector2 position = extractPosition(keyValue, table);
            return Map.entry(type, position);
        } catch (Throwable th) {
            throw new MatchException(th.toString(), th);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private static Vector2 extractPosition(String keyValue, Lua.Value.Table table) throws MatchException {
        Lua.Expression expression = table.get("Position");
        if (!(expression instanceof Lua.Value.Table)) {
            throw new IllegalArgumentException("Position must be a table for unit %s, got: %s".formatted(keyValue, table.get("Position")));
        }
        Lua.Value.Table positionTable = (Lua.Value.Table) expression;
        double x = extractNumber(positionTable.get((Number) 1));
        double y = extractNumber(positionTable.get((Number) 3));
        return new Vector2((float) x, (float) y);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private static double extractNumber(Lua.Expression expression) throws MatchException {
        int i = 0;
        while (true) {
            switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, Lua.Expression.class, Integer.TYPE), Lua.Value.Num.class, Lua.UnaryOperator.Negate.class).dynamicInvoker().invoke(expression, i) /* invoke-custom */) {
                case -1:
                default:
                    throw new IllegalArgumentException("Expression must be a number got %s".formatted(expression));
                case 0:
                    try {
                        double value = ((Lua.Value.Num) expression).value();
                        if (1 != 0) {
                            return value;
                        }
                        i = 1;
                    } catch (Throwable th) {
                        throw new MatchException(th.toString(), th);
                    }
                    break;
                case 1:
                    Lua.Expression expression2 = ((Lua.UnaryOperator.Negate) expression).expression();
                    if (expression2 instanceof Lua.Value.Num) {
                        double value2 = ((Lua.Value.Num) expression2).value();
                        if (1 != 0) {
                            return -value2;
                        }
                    }
                    i = 2;
                    break;
            }
        }
    }

    private static SequencedMap<String, SequencedSet<Vector2>> loadUnitsFromSCUnits(InputStream inputStream) throws IOException {
        SCUnitSet scUnitSet = (SCUnitSet) FileUtil.deserialize(inputStream, SCUnitSet.class);
        Vector3 center = scUnitSet.center();
        return (SequencedMap) scUnitSet.units().stream().map(unit -> {
            return Map.entry(unit.ID(), new Vector2(unit.pos().subtract(center).multiply(10.0f).round(2)));
        }).sorted(UNIT_ENTRY_COMPARATOR).collect(Collectors.groupingBy((v0) -> {
            return v0.getKey();
        }, LinkedHashMap::new, Collectors.mapping((v0) -> {
            return v0.getValue();
        }, Collectors.toCollection(LinkedHashSet::new))));
    }
}

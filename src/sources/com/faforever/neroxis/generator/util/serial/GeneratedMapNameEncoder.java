package com.faforever.neroxis.generator.util.serial;

import com.faforever.neroxis.biomes.BiomeName;
import com.faforever.neroxis.cli.VersionProvider;
import com.faforever.neroxis.generator.MapNameException;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.util.MathUtil;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.SwitchBootstraps;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.Objects;
import org.apache.commons.codec.CodecPolicy;
import org.apache.commons.codec.binary.Base32;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/util/serial/GeneratedMapNameEncoder.class */
public class GeneratedMapNameEncoder {
    public static final int NUM_BINS = 127;
    private static final Base32 ENCODER = Base32.builder().setLineLength(0).setPadding((byte) 61).setDecodingPolicy(CodecPolicy.LENIENT).get();
    private static final String VERSION = new VersionProvider().getVersion()[0];

    static String encodeFromBytes(byte[] bytes) {
        return ENCODER.encodeAsString(bytes).replace("=", "").toLowerCase(Locale.ROOT);
    }

    static byte[] decodeToBytes(String encoded) {
        return ENCODER.decode(encoded);
    }

    public static MapNameParameters decode(String mapName) {
        String[] nameArgs = verifyMapName(mapName);
        long seed = ByteBuffer.wrap(decodeToBytes(nameArgs[4])).getLong();
        int spawnCount = 6;
        int mapSize = 512;
        int numTeams = 2;
        MapNameParameters.Mode mode = new MapNameParameters.Casual(null, null);
        if (nameArgs.length >= 6) {
            String optionString = nameArgs[5];
            byte[] optionBytes = decodeToBytes(optionString);
            if (optionBytes.length > 0) {
                spawnCount = optionBytes[0];
            }
            if (optionBytes.length > 1) {
                mapSize = optionBytes[1] * 64;
            }
            if (optionBytes.length > 2) {
                numTeams = optionBytes[2];
            }
            if (optionBytes.length == 4 && nameArgs.length >= 7) {
                long generationTime = ByteBuffer.wrap(decodeToBytes(nameArgs[6])).getLong();
                Visibility visibility = Visibility.values()[optionBytes[3]];
                mode = new MapNameParameters.Competitive(generationTime, visibility);
            } else if (optionBytes.length > 3) {
                byte terrainSymmetryOption = optionBytes[3];
                Symmetry terrainSymmetry = terrainSymmetryOption < 0 ? null : Symmetry.values()[terrainSymmetryOption];
                MapStyle mapStyle = null;
                if (optionBytes.length == 5) {
                    mapStyle = MapStyle.Predefined.values()[optionBytes[4]];
                } else if (optionBytes.length == 10) {
                    BiomeName biomeName = BiomeName.values()[optionBytes[4]];
                    TerrainStyle terrainStyle = TerrainStyle.values()[optionBytes[5]];
                    ResourceStyle resourceStyle = ResourceStyle.values()[optionBytes[6]];
                    PropStyle propStyle = PropStyle.values()[optionBytes[7]];
                    float reclaimDensity = MathUtil.normalizeBin(optionBytes[8], NUM_BINS);
                    float resourceDensity = MathUtil.normalizeBin(optionBytes[9], NUM_BINS);
                    mapStyle = new MapStyle.Custom(terrainStyle, biomeName, propStyle, resourceStyle, reclaimDensity, resourceDensity);
                }
                mode = new MapNameParameters.Casual(terrainSymmetry, mapStyle);
            }
        }
        return new MapNameParameters(seed, spawnCount, mapSize, numTeams, mode);
    }

    private static String[] verifyMapName(String mapName) {
        if (!mapName.startsWith("neroxis_map_generator")) {
            throw new MapNameException(String.format("Map name `%s` is not a generated map", mapName));
        }
        String[] nameArgs = mapName.split("_");
        if (nameArgs.length < 4) {
            throw new MapNameException(String.format("Map name `%s` does not specify a version", mapName));
        }
        String version = nameArgs[3];
        if (!VERSION.equals(version)) {
            throw new MapNameException(String.format("Version for `%s` does not match this generator version", mapName));
        }
        if (nameArgs.length < 5) {
            throw new MapNameException(String.format("Map name `%s` does not specify a seed", mapName));
        }
        return nameArgs;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static String encode(MapNameParameters mapNameParameters) throws MatchException {
        String strEncodeFromBytes;
        ByteBuffer seedBuffer = ByteBuffer.allocate(8);
        seedBuffer.putLong(mapNameParameters.seed());
        String seedString = encodeFromBytes(seedBuffer.array());
        byte spawnOption = (byte) mapNameParameters.spawnCount();
        byte mapSizeOption = (byte) (mapNameParameters.mapSize() / 64);
        byte numTeamsOption = (byte) mapNameParameters.numTeams();
        MapNameParameters.Mode mode = mapNameParameters.mode();
        Objects.requireNonNull(mode);
        int i = 0;
        while (true) {
            switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, MapNameParameters.Mode.class, Integer.TYPE), MapNameParameters.Competitive.class, MapNameParameters.Casual.class).dynamicInvoker().invoke(mode, i) /* invoke-custom */) {
                case 0:
                    MapNameParameters.Competitive competitive = (MapNameParameters.Competitive) mode;
                    try {
                        long generationTime = competitive.generationTime();
                        if (1 != 0) {
                            Visibility visibility = competitive.visibility();
                            byte[] optionArray = {spawnOption, mapSizeOption, numTeamsOption, (byte) visibility.ordinal()};
                            String timeString = encodeFromBytes(ByteBuffer.allocate(8).putLong(generationTime).array());
                            strEncodeFromBytes = encodeFromBytes(optionArray) + "_" + timeString;
                            String optionString = strEncodeFromBytes;
                            return "neroxis_map_generator_%s_%s_%s".formatted(VERSION, seedString, optionString).toLowerCase(Locale.ROOT);
                        }
                        i = 1;
                    } catch (Throwable th) {
                        throw new MatchException(th.toString(), th);
                    }
                    break;
                case 1:
                    MapNameParameters.Casual casual = (MapNameParameters.Casual) mode;
                    Symmetry terrainSymmetry = casual.terrainSymmetry();
                    MapStyle mapStyle = casual.mapStyle();
                    byte terrainSymmetryOption = terrainSymmetry == null ? (byte) -1 : (byte) terrainSymmetry.ordinal();
                    switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, MapStyle.class, Integer.TYPE), MapStyle.Predefined.class, MapStyle.Custom.class).dynamicInvoker().invoke(mapStyle, 0) /* invoke-custom */) {
                        case -1:
                            byte[] optionArray2 = terrainSymmetryOption < 0 ? new byte[]{spawnOption, mapSizeOption, numTeamsOption} : new byte[]{spawnOption, mapSizeOption, numTeamsOption, terrainSymmetryOption};
                            strEncodeFromBytes = encodeFromBytes(optionArray2);
                            break;
                        case 0:
                            MapStyle.Predefined predefinedMapStyle = (MapStyle.Predefined) mapStyle;
                            byte[] optionArray3 = {spawnOption, mapSizeOption, numTeamsOption, terrainSymmetryOption, (byte) predefinedMapStyle.ordinal()};
                            strEncodeFromBytes = encodeFromBytes(optionArray3);
                            break;
                        case 1:
                            MapStyle.Custom customMapStyle = (MapStyle.Custom) mapStyle;
                            byte BiomeNameOption = (byte) customMapStyle.biomeName().ordinal();
                            byte terrainStyleOption = (byte) customMapStyle.terrainStyle().ordinal();
                            byte resourceStyleOption = (byte) customMapStyle.resourceStyle().ordinal();
                            byte propStyleOption = (byte) customMapStyle.propStyle().ordinal();
                            byte reclaimDensityOption = (byte) MathUtil.binPercentage(customMapStyle.reclaimDensity(), NUM_BINS);
                            byte resourceDensityOption = (byte) MathUtil.binPercentage(customMapStyle.resourceDensity(), NUM_BINS);
                            byte[] optionArray4 = {spawnOption, mapSizeOption, numTeamsOption, terrainSymmetryOption, BiomeNameOption, terrainStyleOption, resourceStyleOption, propStyleOption, reclaimDensityOption, resourceDensityOption};
                            strEncodeFromBytes = encodeFromBytes(optionArray4);
                            break;
                        default:
                            throw new MatchException((String) null, (Throwable) null);
                    }
                    String optionString2 = strEncodeFromBytes;
                    return "neroxis_map_generator_%s_%s_%s".formatted(VERSION, seedString, optionString2).toLowerCase(Locale.ROOT);
                default:
                    throw new MatchException((String) null, (Throwable) null);
            }
        }
    }
}

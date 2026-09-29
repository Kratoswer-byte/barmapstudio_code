# Neroxis Map Generator for Beyond All Reason

A fork of the [FAF Neroxis Map Generator](https://github.com/FAForever/Neroxis-Map-Generator) that can export
its maps for [Beyond All Reason](https://www.beyondallreason.info/) (Recoil engine) instead of Supreme Commander.
The terrain, symmetry, spawn, resource and prop generation is the original Neroxis code, the `--bar` switch
replaces the export.

## Usage

**`bar-map-launcher.bat` opens the launcher window** (like the map generator dialog of the FAF client): players per
team, teams, map size, style or a custom mix of terrain / biome / reclaim style / densities, symmetry, metal
incomes, reclaim, biome trees, optional map name and seed. It shows the preview of every generated map, keeps a
library of them and starts a skirmish against AIs on the selected one. The start script of a map is stored as a
comment in its map info so library maps start without regenerating.

The command line works too:


Release zip: unzip anywhere and double click `generate-bar-map.bat`, Java is included.
From source: run `build-bar-generator.bat` once (and after code changes).

Without arguments `generate-bar-map.bat` shows a menu: game format (1v1 up to 8v8, multiple teams, FFA), map size
(with a recommendation for the format), map style and metal richness. It generates the map and offers to start
a skirmish against BARb AIs on it right away, with start boxes for every team.

With arguments they go straight to the generator:

```
generate-bar-map.bat --spawn-count 8 --num-teams 2 --map-size 640     4v4 on 20x20
generate-bar-map.bat --style BIG_ISLANDS --seed 42
generate-bar-map.bat --map-name neroxis_map_generator_...             regenerate a map from its name
```

The map is written as `Neroxis_<code>.sdz` into `%LOCALAPPDATA%\Programs\Beyond-All-Reason\data\maps` (set the
`BAR_DATA` environment variable to the `data` folder of another install). Restart BAR, the map is then listed in
the map selection of skirmish and hosted games. Other players need the same file to join, or generate it
themselves from the same `--map-name`: the export is deterministic, the files are byte identical.
See [BAR-INTEGRATION.md](BAR-INTEGRATION.md) for what `!nextmap` style generation in public lobbies would need.

### Playing with friends without the BAR servers

`host-game.bat` asks for the player names, generates a map and hosts the game directly from your computer (UDP
port 8452, forward it on your router or use a VPN like Tailscale). It prints the line your friends run:
`join-game.bat <map name> <your ip> <their name> [password or -] [generator options]`. Options that change the map
(like `--bar-reclaim`) have to be the same for everybody, `host-game.bat` prints them as part of the line. `join-game.bat` generates the identical map from its
name and connects, nothing has to be downloaded. Everybody needs the same version of this generator. The game does
not show up in the BAR lobby.

`play-last-map.bat` starts the skirmish of the last generated map again (`data\_neroxis_skirmish.txt`,
written by `--bar-skirmish-script`). The start scripts switch BAR's draft spawn order off, its default makes a
single player wait for a turn that never comes.

Map sizes: supcom sizes feel one step bigger in BAR, a 16x16 map plays like a 2v2 / 3v3 map. `--map-size` has to
be a multiple of 64.

| Game | `--spawn-count` | `--map-size` | BAR size |
| --- | --- | --- | --- |
| 1v1 | 2 | 256 - 384 | 8x8 - 12x12 |
| 2v2 | 4 | 384 - 512 | 12x12 - 16x16 |
| 3v3 / 4v4 | 6 / 8 | 512 - 640 | 16x16 - 20x20 |
| 8v8 | 16 | 768 - 1024 | 24x24 - 32x32 |

Every original option works (`--help`, `styles`, `biomes`, `--terrain-style`, `--biome`, `--terrain-symmetry`,
`--reclaim-density`, `--resource-density`, ...). Additional BAR options:

| Option | Default | |
| --- | --- | --- |
| `--bar` | | export a BAR map, `--out-path` is the maps folder |
| `--bar-scale` | 2 | heightmap squares per supcom cell. 2 keeps the unit scale: `--map-size 256/512/768/1024` become 8x8/16x16/24x24/32x32 maps |
| `--bar-height-scale` | 0.7 | vertical scale. BAR vehicles climb up to 27 degrees, supcom units 35, 0.7 keeps every generated ramp usable for vehicles while cliffs stay cliffs (bots get over some of the lower ones) |
| `--bar-metal` | 2.0 | income of the metal spots next to a start position |
| `--bar-metal-min` / `--bar-metal-max` | 2.0 / 3.5 | income range of all other spots, BAR supports 0.5 to 10 but more than 3.5 is hard to balance |
| `--bar-metal-uniform` | | every spot gets `--bar-metal` like in supcom |
| `--bar-skirmish-script` | | also write an engine start script (player + BARb AIs, team start boxes) to this file |
| `--bar-features` | | folder with a checkout of beyond-all-reason/mapfeatures for biome trees, the launchers set it |
| `--bar-reclaim` | 1.0 | amount of metal in rocks and wrecks. Supcom maps are full of reclaim (a generated 12x12 map had 21000 metal in rocks alone, single experimental wrecks are worth up to 14000 in BAR), BAR maps have little: 1 is a budget of 600 metal per start position, 0 none, 8 or more is supcom like. Mirrored rocks and wrecks are kept or dropped together, experimental wrecks are never placed |
| `--bar-host-script`, `--bar-players`, `--bar-coop`, `--bar-password` | | host a multiplayer game without the BAR servers, see below |

## What is converted how

| Supreme Commander | Beyond All Reason |
| --- | --- |
| heightmap | cleaned (median + edge preserving bilateral filter, the cell sized noise of supcom heightmaps turns into visible bumps at BAR scale) and bicubic upscaled heightmap, water level moved to 0. Ground that is too steep for vehicles is steepened into crisp cliff faces. If the start positions are connected for bots but not for vehicles the terrain is flattened in small steps until vehicles get through |
| texture layer masks + biome | one baked diffuse texture (8 texels per square, DXT1 tiles) with procedural detail, baked lighting, shore line and resource markings, and the minimap |
| (nothing, supcom tiles its texture set at runtime) | four procedural tileable detail textures (`*_dnts1-4.tga`: rock, gravel, the ground of the biome - grass, snow, dust or cracked earth - and sea floor; normal map in rgb, brightness detail in alpha) and a DXT5 splat distribution map that places them by slope and height. These give the ground its structure up close |
| (nothing) | rock faces painted from the real slope with erosion streaks along the fall line, a sand beach that follows the distance to the water (the low lands are only a few elmos above the water, a height based beach would cover them), colours graded towards the brightness and saturation of hand made BAR maps |
| (nothing) | the slope and rock layers of the supcom textures are blurred over hundreds of elmos, they only show where the BAR terrain really slopes and rock is neutral grey instead of the bluish rock of many biomes. Ground that is too steep for vehicles gets ridges and gullies |
| mass points | metal spots in the metal map. Spots closer than 112 elmos are merged into one richer spot as BAR would fuse them |
| hydrocarbons | geothermal vents |
| tree groups | trees that fit the biome: snowy pines, birches and brown pines, palms and kapok trees, cacti, aleppo pines, dead trees on the red worlds, crystals. BAR only ships fir trees, the others come from the feature sets the BAR team certified for maps ([beyond-all-reason/mapfeatures](https://github.com/beyond-all-reason/mapfeatures)). They are not part of this generator: the launchers download that repository once (pinned commit, 37 MB) and the export copies the sets a map uses into the map (0.6 - 10 MB). Without it (`--bar-features` not set) maps get BAR's fir trees. Mirrored groves get the same plants |
| rocks / boulders | `rocks30_*` features that ship with BAR, reclaimable, mirrored rocks have the same size |
| wrecks and civilian bases | BAR unit wrecks (tanks, bots, ships under water, buildings) |
| spawns | `teams` start positions, ordered team by team as BAR lobbies fill ally teams one after another. Neroxis only knows symmetry slots, the spawns are grouped into the requested number of teams by the widest gap (for example west vs east) |

Metal income: supcom mass points are all equal, BAR spots are not. Spots next to a start position keep the base
income, the income of the others grows with the distance to the nearest start position, shrinks in crowded
clusters and lone contested spots are the richest. Only distances that are identical for mirrored spots are used
so both teams always get the same income.

Start boxes are configured in the lobby, not in the map. The map description says where the teams start
(for example "team 1 north west, team 2 south east"), pick the matching boxes or use fixed start positions.

## Code

* `shared/.../exporter/bar/BarMapExporter.java` conversion and texture rendering
* `shared/.../exporter/bar/SmfWriter.java` smf / smt file format
* `shared/.../exporter/bar/DetailTextures.java` procedural detail (DNTS) textures
* `shared/.../exporter/bar/Dxt1Encoder.java` DXT1 / DXT5 texture compression
* `generator/.../MapGeneratorCommand.java` the `--bar*` options
* `generator/src/test/.../BarExportTest.java`

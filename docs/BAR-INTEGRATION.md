# Generated maps in BAR multiplayer lobbies

Goal: after a game somebody calls a vote (like `!nextmap`) and the next map is a freshly generated one.

## Why a local tool can not do this on its own

Public BAR lobbies are hosted by the BAR team's SPADS autohosts. The autohost and every player need the map
archive, clients download maps from BAR's map server, and the engine refuses to start when the archive checksums
of the players differ. A map that only exists on one PC can therefore only be used in games where everybody
copied the same `.sdz` by hand.

## How FAF solved it, and what this generator already provides

In FAF the map name *is* the map: `neroxis_map_generator_<version>_<encoded parameters>` contains the seed,
player count, teams, size and style. The host only announces the name and every client generates the map locally.

The BAR export keeps that property:

* `generate-bar-map.bat --map-name neroxis_map_generator_<version>_<code>` recreates a map from its name. The
  name is stored in the description of every generated map (`Regenerate with --map-name ...`), the BAR map
  name `Neroxis_<code>` is the same code without the version.
* The export is deterministic: the same name gives byte identical `smf`, `smt`, texture and `mapinfo.lua` files
  (verified across JVM launches and thread counts, `StrictMath` is used like in the rest of Neroxis), so the
  engine's archive checksum matches between players who generated the map themselves.
* Generation takes 2-4 seconds for a 16x16 map, the self contained release needs no Java install.

## What is needed on the BAR side

1. **Autohost (SPADS plugin)**: a command such as `!genmap [players] [teams] [size] [style]` or a
   `!nextmap generated` rotation entry that runs the generator CLI on the host, puts the archive in the
   autohost's maps folder, reloads the archive list and sets the map.
2. **Clients** - one of:
   * *Download*: the autohost uploads the archive (15-40 MB) to a location the lobby's map downloader knows.
     Simple, no client change besides an additional download source.
   * *Generate locally* (the FAF way): the lobby recognises `Neroxis_<code>` map names and runs the bundled
     generator instead of downloading. Needs the generator (about 50 MB with its runtime) shipped with the
     launcher and the generator version pinned per lobby so everybody produces the same bytes.
3. **Start boxes**: the autohost has no saved boxes for a new map. The exporter already computes ally team
   boxes from the generated start positions (see `--bar-skirmish-script`), the plugin can set the same boxes
   with `!addbox`.

Until then generated maps work in skirmish, in games hosted from your own lobby where all players have the file,
and in private games between friends who generate the map from the same name.

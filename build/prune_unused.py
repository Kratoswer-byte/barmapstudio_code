"""Toglie da launcher.jar le classi ereditate che il programma non usa piu'.

Parte da BarMapStudio (il punto di ingresso) e dalle classi del generatore Neroxis, segue tutti i
riferimenti tra classi scritti nei file .class e tiene solo cio' che e' raggiungibile dentro i due
pacchetti dello Studio (generator/bar ed exporter/bar). Il resto - lobby, replay, notizie, trofei,
vecchio avvio - viene tolto, insieme alle risorse che servivano solo a quelle parti.

Uso:  python build/prune_unused.py <launcher.jar>           elenca cosa toglierebbe
      python build/prune_unused.py <launcher.jar> --apply   riscrive il jar (salva prima .bak_preprune)
"""
import re
import shutil
import struct
import sys
import zipfile

PACKAGES = ("com/faforever/neroxis/generator/bar/", "com/faforever/neroxis/exporter/bar/")
ROOTS = ("com/faforever/neroxis/generator/bar/BarMapStudio",)
# risorse usate solo dalle parti tolte
DEAD_RESOURCES = re.compile(r"^(com/faforever/neroxis/generator/bar/(barmapstudio-background\.jpg|lobby-art-\d+\.jpg|"
                            r"mapgen-sigil-\d+\.jpg|barmap_return_to_lobby\.lua|barmap_own_colour\.lua|build-info\.properties|"
                            r"barmap\.(png|ico))|trophies/.*)$")
NAME = re.compile(rb"com/faforever/neroxis/(?:generator|exporter)/bar/[A-Za-z0-9_$]+")


def references(data):
    """Tutti i nomi di classi dei due pacchetti citati nella tabella delle costanti di un file .class."""
    found = set()
    if data[:4] != b"\xca\xfe\xba\xbe":
        return found
    count = struct.unpack(">H", data[8:10])[0]
    pos = 10
    index = 1
    while index < count:
        tag = data[pos]
        if tag == 1:
            length = struct.unpack(">H", data[pos + 1:pos + 3])[0]
            for match in NAME.finditer(data[pos + 3:pos + 3 + length]):
                found.add(match.group().decode())
            pos += 3 + length
        else:
            pos += {3: 5, 4: 5, 5: 9, 6: 9, 7: 3, 8: 3, 9: 5, 10: 5, 11: 5, 12: 5, 15: 4, 16: 3, 17: 5, 18: 5, 19: 3, 20: 3}[tag]
            if tag in (5, 6):
                index += 1
        index += 1
    return found


def main():
    jar = sys.argv[1]
    apply = "--apply" in sys.argv
    source = zipfile.ZipFile(jar)
    classes = {}
    outside = set()
    for info in source.infolist():
        if not info.filename.endswith(".class"):
            continue
        name = info.filename[:-6]
        refs = references(source.read(info.filename)) if "faforever" in info.filename else set()
        if info.filename.startswith(PACKAGES):
            classes[name] = refs
        else:
            outside |= refs
    # una classe interna tiene viva quella che la contiene, e viceversa
    reachable = set()
    todo = [root for root in ROOTS if root in classes] + [name for name in outside if name in classes]
    while todo:
        name = todo.pop()
        if name in reachable or name not in classes:
            continue
        reachable.add(name)
        todo.extend(classes[name])
        outer = name.split("$")[0]
        todo.append(outer)
        todo.extend(other for other in classes if other.startswith(name + "$"))
    dead = sorted(set(classes) - reachable)
    dead_resources = sorted(info.filename for info in source.infolist() if DEAD_RESOURCES.match(info.filename) and not info.is_dir())
    tops = sorted({name.rsplit("/", 1)[1].split("$")[0] for name in dead})
    kept_tops = sorted({name.rsplit("/", 1)[1].split("$")[0] for name in reachable})
    size = sum(source.getinfo(name + ".class").file_size for name in dead) + sum(source.getinfo(name).file_size for name in dead_resources)
    print("classi tenute: %d (%d con le interne)" % (len(kept_tops), len(reachable)))
    print("classi da togliere: %d (%d con le interne): %s" % (len(tops), len(dead), " ".join(tops)))
    print("risorse da togliere: %d" % len(dead_resources))
    print("spazio liberato: %.1f MB" % (size / 1048576.0))
    if apply:
        gone = {name + ".class" for name in dead} | set(dead_resources)
        shutil.copyfile(jar, jar + ".bak_preprune")
        target = zipfile.ZipFile(jar + ".tmp", "w", zipfile.ZIP_DEFLATED)
        for info in source.infolist():
            if info.filename in gone:
                continue
            target.writestr(info, source.read(info.filename), zipfile.ZIP_STORED if info.compress_type == zipfile.ZIP_STORED else zipfile.ZIP_DEFLATED)
        target.close()
        source.close()
        shutil.move(jar + ".tmp", jar)
        print("jar riscritto")
    else:
        source.close()


if __name__ == "__main__":
    main()

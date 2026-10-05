"""Toglie da launcher.jar i riferimenti al vecchio launcher da cui il programma deriva.

Le classi BarLauncher, LobbyUi, LobbyClient e InstallReport non si possono ricompilare dai sorgenti, quindi
dentro il jar restano quelle originali, con l'indirizzo del server del vecchio launcher scritto come valore
predefinito. Questo script riscrive quelle costanti direttamente nei file .class (tabella delle costanti),
senza toccare il codice:

  - indirizzi del vecchio server  -> 127.0.0.1 (nessuna richiesta esce dal PC)
  - nome del vecchio launcher     -> nomi neutri
  - toglie la copia di InstallReport.class finita per errore nella radice del jar

Uso:  python build/clean_legacy_refs.py <launcher.jar>        riscrive il jar (salva prima una copia .bak_preclean)
      python build/clean_legacy_refs.py <launcher.jar> --check  elenca soltanto cosa trova
"""
import shutil
import struct
import sys
import zipfile

REPLACEMENTS = [
    (b"eclipseforeverbeyond.com", b"127.0.0.1"),
    (b"51.81.81.166", b"127.0.0.1"),
    (b"Eclipse-launcher", b"BARMapStudio"),
    (b"Eclipse.lnk", b"BARMapStudio-old.lnk"),
    (b"Eclipse", b"BARMapStudio"),
    (b"eclipse", b"general"),
]
# solo il codice del programma: le librerie (ICU, Jackson...) citano Eclipse per altri motivi
PREFIX = "com/faforever/neroxis/"
STRAY = {"InstallReport.class"}


def patch_class(data):
    """Riscrive le costanti di testo di un file .class. Torna (nuovi byte, elenco delle modifiche)."""
    if data[:4] != b"\xca\xfe\xba\xbe":
        return data, []
    count = struct.unpack(">H", data[8:10])[0]
    out = bytearray(data[:10])
    pos = 10
    changes = []
    index = 1
    while index < count:
        tag = data[pos]
        if tag == 1:  # Utf8
            length = struct.unpack(">H", data[pos + 1:pos + 3])[0]
            text = data[pos + 3:pos + 3 + length]
            new = text
            for old, replacement in REPLACEMENTS:
                new = new.replace(old, replacement)
            if new != text:
                changes.append((text.decode("utf-8", "replace"), new.decode("utf-8", "replace")))
            out += bytes([1]) + struct.pack(">H", len(new)) + new
            pos += 3 + length
        else:
            size = {3: 5, 4: 5, 5: 9, 6: 9, 7: 3, 8: 3, 9: 5, 10: 5, 11: 5, 12: 5, 15: 4, 16: 3, 17: 5, 18: 5, 19: 3, 20: 3}[tag]
            out += data[pos:pos + size]
            pos += size
            if tag in (5, 6):  # long e double occupano due posti
                index += 1
        index += 1
    out += data[pos:]
    return bytes(out), changes


def main():
    jar = sys.argv[1]
    check = "--check" in sys.argv
    found = 0
    source = zipfile.ZipFile(jar)
    target = None
    if not check:
        shutil.copyfile(jar, jar + ".bak_preclean")
        target = zipfile.ZipFile(jar + ".tmp", "w", zipfile.ZIP_DEFLATED)
    for info in source.infolist():
        data = source.read(info.filename)
        if info.filename in STRAY:
            print("tolto dalla radice:", info.filename)
            found += 1
            continue
        if info.filename.startswith(PREFIX) and info.filename.endswith(".class"):
            data, changes = patch_class(data)
            for old, new in changes:
                found += 1
                print("%s: %s  ->  %s" % (info.filename.rsplit("/", 1)[1], old[:90], new[:90]))
        if target is not None:
            target.writestr(info, data, zipfile.ZIP_STORED if info.compress_type == zipfile.ZIP_STORED else zipfile.ZIP_DEFLATED)
    source.close()
    if target is not None:
        target.close()
        shutil.move(jar + ".tmp", jar)
    print("riferimenti trovati:", found)


if __name__ == "__main__":
    main()

"""Analizza le mappe BAR installate: tavolozza del terreno (da SMT + SMF) e luce/atmosfera/acqua (da mapinfo.lua)."""
import json, os, re, shutil, struct, subprocess, sys, math

SEVEN = r"C:\Program Files\Beyond-All-Reason\resources\app.asar.unpacked\node_modules\7zip-bin\win\x64\7za.exe"
MAPS = r"C:\Program Files\Beyond-All-Reason\data\maps"
HERE = os.path.dirname(os.path.abspath(__file__))
TMP = os.path.join(HERE, "tmp")
OUT = os.path.join(HERE, "results.jsonl")


def run(args):
    return subprocess.run(args, capture_output=True)


def list_archive(path):
    out = run([SEVEN, "l", "-slt", path]).stdout.decode("utf-8", "replace")
    return [m.group(1).strip() for m in re.finditer(r"^Path = (.+)$", out, re.M)][1:]


def rgb565(c):
    return ((c >> 11) & 31) * 255 / 31, ((c >> 5) & 63) * 255 / 63, (c & 31) * 255 / 31


def block_avg(b):
    c0, c1, idx = struct.unpack("<HHI", b)
    p0, p1 = rgb565(c0), rgb565(c1)
    if c0 > c1:
        pal = [p0, p1, tuple((2 * a + b_) / 3 for a, b_ in zip(p0, p1)), tuple((a + 2 * b_) / 3 for a, b_ in zip(p0, p1))]
    else:
        pal = [p0, p1, tuple((a + b_) / 2 for a, b_ in zip(p0, p1)), (0, 0, 0)]
    r = g = bl = 0.0
    for i in range(16):
        p = pal[(idx >> (2 * i)) & 3]
        r += p[0]; g += p[1]; bl += p[2]
    return r / 16, g / 16, bl / 16


def lua_number_list(text, key):
    m = re.search(r"\b" + key + r"\s*=\s*\{([^{}]*)\}", text, re.I)
    if not m:
        return None
    nums = re.findall(r"-?\d+\.?\d*(?:[eE]-?\d+)?", m.group(1))
    return [float(n) for n in nums] if nums else None


def lua_number(text, key):
    m = re.search(r"\b" + key + r"\s*=\s*(-?\d+\.?\d*)", text, re.I)
    return float(m.group(1)) if m else None


def lua_block(text, key):
    m = re.search(r"\b" + key + r"\s*=\s*\{", text, re.I)
    if not m:
        return ""
    depth, i = 1, m.end()
    while i < len(text) and depth:
        depth += {"{": 1, "}": -1}.get(text[i], 0)
        i += 1
    return text[m.end():i - 1]


def strip_comments(text):
    text = re.sub(r"--\[\[.*?\]\]", "", text, flags=re.S)
    return re.sub(r"--[^\n]*", "", text)


def analyze(archive):
    name = os.path.basename(archive)
    files = list_archive(archive)
    smf = next((f for f in files if f.lower().endswith(".smf")), None)
    info = next((f for f in files if f.lower() == "mapinfo.lua"), None)
    if not smf or not info:
        return {"file": name, "error": "manca smf o mapinfo.lua"}
    shutil.rmtree(TMP, ignore_errors=True)
    os.makedirs(TMP)
    smts = [f for f in files if f.lower().endswith(".smt")]
    run([SEVEN, "e", "-y", "-o" + TMP, archive, smf, info] + smts)
    info_text = strip_comments(open(os.path.join(TMP, "mapinfo.lua"), encoding="utf-8", errors="replace").read())
    data = open(os.path.join(TMP, os.path.basename(smf)), "rb").read()
    if data[:15] != b"spring map file":
        return {"file": name, "error": "smf non valido"}
    (version, mapid, mapx, mapy, square, texel, tilesize, minh, maxh, hptr, typeptr, tilesptr, miniptr, metalptr, featptr, nextra) = struct.unpack_from("<IIIIIIIffIIIIIII", data, 16)
    smf_block = lua_block(info_text, "smf")
    mh = lua_number(smf_block, "minheight")
    xh = lua_number(smf_block, "maxheight")
    if mh is not None: minh = mh
    if xh is not None: maxh = xh
    # tile files
    num_files, num_tiles = struct.unpack_from("<II", data, tilesptr)
    pos = tilesptr + 8
    tile_files = []
    for _ in range(num_files):
        n = struct.unpack_from("<I", data, pos)[0]
        pos += 4
        end = data.index(b"\0", pos)
        tile_files.append((n, data[pos:end].decode("latin1")))
        pos = end + 1
    cw, ch = mapx // 4, mapy // 4
    index = struct.unpack_from("<%dI" % (cw * ch), data, pos)
    # colore medio per tile (mip 4x4, ultimo blocco di ogni record da 680 byte)
    tile_colors = []
    for n, fname in tile_files:
        path = os.path.join(TMP, os.path.basename(fname))
        if not os.path.isfile(path):
            return {"file": name, "error": "manca smt " + fname}
        smt = open(path, "rb").read()
        for i in range(n):
            off = 32 + i * 680 + 672
            tile_colors.append(block_avg(smt[off:off + 8]))
    hw = mapx + 1
    scale = (maxh - minh) / 65535.0

    def height(cx, cz):  # centro della cella tile (4 quadrati)
        x = min(mapx, cx * 4 + 2); z = min(mapy, cz * 4 + 2)
        return minh + struct.unpack_from("<H", data, hptr + 2 * (z * hw + x))[0] * scale

    classes = {k: [] for k in ("seabed", "sand", "ground", "high", "slope", "rock")}
    hs = {}
    step = max(1, int(math.sqrt(cw * ch / 40000.0)))
    land = []
    for cz in range(1, ch - 1, step):
        for cx in range(1, cw - 1, step):
            h = height(cx, cz)
            hs[(cx, cz)] = h
            if h > 15: land.append(h)
    land.sort()
    median = land[len(land) // 2] if land else 0
    top = land[int(len(land) * 0.85)] if land else 0
    for (cx, cz), h in hs.items():
        ti = index[cz * cw + cx]
        if ti >= len(tile_colors):
            continue
        c = tile_colors[ti]
        dx = (height(cx + 1, cz) - height(cx - 1, cz)) / 64.0
        dz = (height(cx, cz + 1) - height(cx, cz - 1)) / 64.0
        slope = math.sqrt(dx * dx + dz * dz)
        if h < -15:
            classes["seabed"].append(c)
        elif h < 14 and slope < 0.12 and minh < -5:
            classes["sand"].append(c)
        elif slope > 0.55:
            classes["rock"].append(c)
        elif slope > 0.2:
            classes["slope"].append(c)
        elif slope < 0.1 and h >= 14:
            classes["high" if h > top else "ground"].append(c)

    def mean(lst):
        if len(lst) < 12:
            return None
        return [round(sum(c[k] for c in lst) / len(lst) / 255.0, 3) for k in range(3)]

    res = {"file": name, "mapx": mapx, "mapy": mapy, "minh": minh, "maxh": maxh,
           "counts": {k: len(v) for k, v in classes.items()},
           "palette": {k: mean(v) for k, v in classes.items()}}
    light = lua_block(info_text, "lighting")
    atmo = lua_block(info_text, "atmosphere")
    water = lua_block(info_text, "water")
    res["lighting"] = {k: lua_number_list(light, k) for k in ("sunDir", "groundAmbientColor", "groundDiffuseColor", "groundSpecularColor", "unitAmbientColor", "unitDiffuseColor", "unitSpecularColor")}
    res["lighting"]["groundShadowDensity"] = lua_number(light, "groundShadowDensity")
    res["atmosphere"] = {k: lua_number_list(atmo, k) for k in ("fogColor", "skyColor", "sunColor", "cloudColor")}
    for k in ("fogStart", "fogEnd", "cloudDensity"):
        res["atmosphere"][k] = lua_number(atmo, k)
    res["water"] = {k: lua_number_list(water, k) for k in ("absorb", "baseColor", "minColor", "surfaceColor", "planeColor", "diffuseColor", "specularColor")}
    for k in ("surfaceAlpha", "ambientFactor", "diffuseFactor", "specularFactor", "specularPower", "fresnelMin", "fresnelMax", "fresnelPower", "reflectionDistortion", "blurBase", "blurExponent", "perlinStartFreq", "perlinLacunarity", "perlinAmplitude", "numTiles", "foamDistortion"):
        res["water"][k] = lua_number(water, k)
    m = re.search(r"\bname\s*=\s*[\"']([^\"']+)[\"']", info_text)
    res["name"] = m.group(1) if m else name
    m = re.search(r"\bauthor\s*=\s*[\"']([^\"']*)[\"']", info_text)
    res["author"] = m.group(1) if m else ""
    return res


def latest_versions():
    groups = {}
    for f in sorted(os.listdir(MAPS)):
        if not f.lower().endswith(".sd7"):
            continue
        base = re.sub(r"[_ -]?v?\d+(\.\d+)*[a-z]?\.sd7$", "", f.lower())
        full = os.path.join(MAPS, f)
        if base not in groups or os.path.getmtime(full) > os.path.getmtime(groups[base]):
            groups[base] = full
    return sorted(groups.values())


if __name__ == "__main__":
    if len(sys.argv) > 1:
        targets = [os.path.join(MAPS, a) for a in sys.argv[1:]]
    else:
        targets = latest_versions()
    done = set()
    if os.path.isfile(OUT) and len(sys.argv) == 1:
        done = {json.loads(l)["file"] for l in open(OUT, encoding="utf-8") if l.strip()}
    for i, t in enumerate(targets):
        if os.path.basename(t) in done:
            continue
        try:
            r = analyze(t)
        except Exception as ex:
            r = {"file": os.path.basename(t), "error": repr(ex)}
        if len(sys.argv) > 1:
            print(json.dumps(r, indent=1))
        else:
            with open(OUT, "a", encoding="utf-8") as fh:
                fh.write(json.dumps(r) + "\n")
            print(i + 1, len(targets), r["file"], r.get("error", "ok"), flush=True)
    shutil.rmtree(TMP, ignore_errors=True)

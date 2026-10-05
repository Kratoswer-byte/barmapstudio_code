"""Genera BarBiomes.java dai biomi selezionati."""
import json, re, sys, os

HERE = os.path.dirname(os.path.abspath(__file__))
sel = json.load(open(os.path.join(HERE, 'selected.json')))
SKIP = {'Downs_of_Destruction_V2', 'TMA20X', 'Devil', 'Sand Crowns'}
LABELS = {
    'Incandescence Remake': 'Incandescence',
    'Hyperion Shale': 'Hyperion Shale',
    'Canis River v1.4': 'Canis River',
    'Glacier Pass': 'Glacier Pass',
    'All That Glitters v2.2.3': 'All That Glitters',
    'Evad River Confluence_V2': 'Evad River',
    'Melting Glacier v1.1.2': 'Melting Glacier',
    'Red River Estuary v1.1': 'Red River',
    'High Noon Remake': 'High Noon',
    'Faster Than Light': 'Faster Than Light',
    'DSD 8 Way': 'DSD',
    'Ascendancy v2.2': 'Ascendancy',
    'Esker Creek': 'Esker Creek',
    'Sphagnum Bog v1.2.1': 'Sphagnum Bog',
    'Ring Atoll Remake v2.0': 'Ring Atoll',
    'Titan': 'Titan',
    'Lavender Bender': 'Lavender Bender',
    'Cape_Violet_V1': 'Cape Violet',
    'Aurelia': 'Aurelia',
    'Baryon Tar Lake v1.1': 'Baryon Tar Lake',
}


def arr(v, n=3):
    if not v or len(v) < n:
        return 'null'
    return 'f(' + ', '.join('%.3ff' % x for x in v[:n]) + ')'


def num(v):
    return 'Float.NaN' if v is None else '%.3ff' % v


entries = []
for r in sel:
    if r['name'] in SKIP:
        continue
    p, L, A, W = r['palette'], r['lighting'], r['atmosphere'], r['water']
    total = sum(r['counts'].values()) or 1
    wet = (r['counts']['seabed'] + r['counts']['sand']) / total > 0.04
    label = 'BAR: ' + LABELS.get(r['name'], r['name'])
    author = re.sub(r'[^\x20-\x7e]', '', r['author']).replace('\\', '').replace('"', "'")
    source = re.sub(r'[^\x20-\x7e]', '', r['name']).replace('"', "'")
    entries.append('''        new BarBiome("%s", "%s", "%s", "%s",
            new float[][]{%s, %s, %s, %s, %s, %s},
            new float[][]{%s, %s, %s, %s, %s, %s, %s}, %s,
            new float[][]{%s, %s, %s, %s}, %s, %s,
            new float[][]{%s, %s, %s, %s, %s}, %s)''' % (
        label, source, author, 'FRITHEN' if wet else 'DESERT',
        arr(p['ground']), arr(p['high']), arr(p['slope']), arr(p['rock']), arr(p['sand']), arr(p['seabed']),
        arr(L['sunDir']), arr(L['groundAmbientColor']), arr(L['groundDiffuseColor']), arr(L['groundSpecularColor']),
        arr(L['unitAmbientColor']), arr(L['unitDiffuseColor']), arr(L['unitSpecularColor']), num(L['groundShadowDensity']),
        arr(A['fogColor']), arr(A['skyColor']), arr(A['sunColor']), arr(A['cloudColor']), num(A['fogStart']), num(A['fogEnd']),
        arr(W['absorb']), arr(W['baseColor']), arr(W['minColor']), arr(W['surfaceColor']), arr(W['planeColor']), num(W['surfaceAlpha'])))

template = open(os.path.join(HERE, 'BarBiomes.template.java'), encoding='utf-8').read()
out = template.replace('/*@ENTRIES@*/', ',\n'.join(entries))
open(sys.argv[1], 'w', encoding='utf-8', newline='\n').write(out)
print(len(entries), 'biomi')

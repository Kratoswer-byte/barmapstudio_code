import json, math, colorsys
rows=[json.loads(l) for l in open('results.jsonl',encoding='utf-8') if l.strip()]
ok=[]
for r in rows:
    if 'error' in r: continue
    p=r['palette']
    if not p['ground'] and p['high']: p['ground']=p['high']
    if not p['ground']: continue
    if not p['rock']: p['rock']=p['slope']
    if not p['rock']: continue
    L=r['lighting']
    if not (L['sunDir'] and L['groundAmbientColor'] and L['groundDiffuseColor']): continue
    ok.append(r)
print(len(rows),'analizzate,',len(ok),'complete')
def feat(r):
    g=r['palette']['ground']; k=r['palette']['rock']
    h,l,s=colorsys.rgb_to_hls(*g)
    return (g[0],g[1],g[2],k[0]*0.5,k[1]*0.5,k[2]*0.5)
def d(a,b): return math.dist(feat(a),feat(b))
# farthest point sampling a partire dalla mappa piu' satura
ok.sort(key=lambda r:-(max(r['palette']['ground'])-min(r['palette']['ground'])))
sel=[ok[0]]
while len(sel)<24:
    best=max((r for r in ok if r not in sel), key=lambda r:min(d(r,s) for s in sel))
    sel.append(best)
json.dump(sel,open('selected.json','w'),indent=1)
from PIL import Image, ImageDraw
W=1100; H=34*len(sel)+30
im=Image.new('RGB',(W,H),(20,22,28)); dr=ImageDraw.Draw(im)
keys=['ground','high','slope','rock','sand','seabed']
dr.text((10,6),'mappa'+' '*46+'  '.join(keys)+'   sole  ambiente  acqua',fill=(200,200,200))
for i,r in enumerate(sel):
    y=28+i*34
    dr.text((10,y+8),r['name'][:40],fill=(230,230,230))
    for j,k in enumerate(keys):
        c=r['palette'][k]
        x=330+j*70
        if c: dr.rectangle((x,y,x+62,y+28),fill=tuple(int(v*255) for v in c))
        else: dr.rectangle((x,y,x+62,y+28),outline=(80,80,80))
    for j,c in enumerate([r['lighting']['groundDiffuseColor'],r['lighting']['groundAmbientColor'],r['water'].get('surfaceColor') or r['water'].get('baseColor')]):
        x=770+j*70
        if c: dr.rectangle((x,y,x+62,y+28),fill=tuple(max(0,min(255,int(v*255))) for v in c[:3]))
        else: dr.rectangle((x,y,x+62,y+28),outline=(80,80,80))
im.save('selected.png')
for r in sel: print(r['name'],'|',r['file'],'|',r['author'])

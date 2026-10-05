"""Unisce i testi estratti (italiano) e le traduzioni nei file messages_<lingua>.properties.

uso: build.py <cartella i18n dei sorgenti> <prefisso>
Legge strings_<prefisso>.json (italiano) e tr_<prefisso>.tsv (numero|en|fr|de).
"""
import json, os, re, sys

HERE = os.path.dirname(os.path.abspath(__file__))
target, prefix = sys.argv[1], sys.argv[2]
it = json.load(open(os.path.join(HERE, 'strings_%s.json' % prefix), encoding='utf-8'))
tr = {'en': {}, 'fr': {}, 'de': {}}
path = os.path.join(HERE, 'tr_%s.tsv' % prefix)
bad = []
if os.path.isfile(path):
    for line in open(path, encoding='utf-8').read().split('\n'):
        if not line.strip():
            continue
        parts = line.split('|')
        rest = parts[1:]
        if len(rest) % 3 != 0:
            bad.append(parts[0])
            continue
        n = len(rest) // 3   # le traduzioni possono contenere "|": tre gruppi di uguale lunghezza
        key = '%s.%s' % (prefix, parts[0])
        for i, lang in enumerate(('en', 'fr', 'de')):
            tr[lang][key] = '|'.join(rest[i * n:(i + 1) * n]).replace('\\n', '\n')

# etichette composte da termini ricorrenti (es. dimensioni mappa): sostituzione parola per parola
TERMS = {
    'en': [('Quadrata Standard', 'Standard Square'), ('Quadrata Grande', 'Large Square'), ('Quadrata Enorme', 'Huge Square'), ('Quadrata', 'Square'),
           ('Rettangolare Diagonale', 'Rectangular Diagonal'), ('Rettangolare Solo Asse', 'Rectangular, Axis'), ('Rettangolare Quadrupla', 'Rectangular Quad'),
           ('Diagonale X+Z', 'Diagonal X+Z'), ('Doppia X+Z', 'Double X+Z'), ('4 Lati X+Z', '4 Sides X+Z')],
    'fr': [('Quadrata Standard', 'Carrée standard'), ('Quadrata Grande', 'Grande carrée'), ('Quadrata Enorme', 'Carrée énorme'), ('Quadrata', 'Carrée'),
           ('Rettangolare Diagonale', 'Rectangulaire diagonale'), ('Rettangolare Solo Asse', 'Rectangulaire, axe'), ('Rettangolare Quadrupla', 'Rectangulaire quadruple'),
           ('Diagonale X+Z', 'Diagonale X+Z'), ('Doppia X+Z', 'Double X+Z'), ('4 Lati X+Z', '4 côtés X+Z'), ('W-E', 'O-E')],
    'de': [('Quadrata Standard', 'Quadratisch Standard'), ('Quadrata Grande', 'Quadratisch groß'), ('Quadrata Enorme', 'Quadratisch riesig'), ('Quadrata', 'Quadratisch'),
           ('Rettangolare Diagonale', 'Rechteckig diagonal'), ('Rettangolare Solo Asse', 'Rechteckig, Achse'), ('Rettangolare Quadrupla', 'Rechteckig vierfach'),
           ('Diagonale X+Z', 'Diagonal X+Z'), ('Doppia X+Z', 'Doppelt X+Z'), ('4 Lati X+Z', '4 Seiten X+Z'), ('W-E', 'W-O')],
}
missing = []
for key, text in it.items():
    for lang in tr:
        if key in tr[lang]:
            continue
        converted = text
        for a, b in TERMS[lang]:
            converted = converted.replace(a, b)
        if converted == text and re.search(r'[a-zà-ù]{4,}', text):
            missing.append((lang, key))
        tr[lang][key] = converted
tr['it'] = dict(it)
# gli spazi ai bordi contano (i testi vengono concatenati): riallineo ogni traduzione all'italiano
for lang in ('en', 'fr', 'de'):
    for key, text in it.items():
        value = tr[lang][key]
        lead = text[:len(text) - len(text.lstrip(' '))]
        trail = text[len(text.rstrip(' ')):]
        tr[lang][key] = lead + value.strip(' ') + trail if value.strip(' ') else value
        # stessi a-capo finali dell'italiano (paragrafi delle guide)
        value = tr[lang][key]
        tr[lang][key] = value.rstrip('\n') + text[len(text.rstrip('\n')):]


def escape(value):
    value = value.replace('\\', '\\\\').replace('\n', '\\n').replace('\t', '\\t')
    if value.startswith(' '):
        value = '\\' + value
    return value


for lang in ('en', 'it', 'fr', 'de'):
    p = os.path.join(target, 'messages_%s.properties' % lang)
    lines = [l for l in open(p, encoding='utf-8').read().split('\n') if not l.startswith(prefix + '.') and l.strip() != '# --- ' + prefix]
    while lines and not lines[-1].strip():
        lines.pop()
    lines.append('# --- ' + prefix)
    for key in it:
        lines.append(key + '=' + escape(tr[lang][key]))
    open(p, 'w', encoding='utf-8', newline='\n').write('\n'.join(lines) + '\n')
print(len(it), 'chiavi; righe scartate:', bad, '; non tradotte:', sorted(set(k for _, k in missing))[:40])

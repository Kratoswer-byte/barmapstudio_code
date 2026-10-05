"""Estrae i testi dell'interfaccia dai sorgenti Java e li sostituisce con I18n.t("chiave").

uso: extract.py <cartella generator/bar> <prefisso> <File1.java> [File2.java ...] [--apply]
Senza --apply stampa solo l'elenco (chiave<TAB>testo). Con --apply riscrive i sorgenti e
aggiunge i testi italiani a strings_<prefisso>.json.
"""
import json, os, re, sys

HERE = os.path.dirname(os.path.abspath(__file__))
STR = re.compile(r'"((?:[^"\\\n]|\\.)*)"')
UI_CALLS = ('new JLabel(', 'new JButton(', 'new JCheckBox(', 'new JToggleButton(', 'new JRadioButton(', 'new JMenuItem(',
            'createGhostButton(', 'createPrimaryButton(', 'createToolToggle(', 'createChip(', 'createFieldRow(', 'createPropLabel(',
            'createPresetBtn(', 'createCompactSliderRow(', 'createSectionTitle(', 'createSubHeader(', 'createParagraph(',
            'createTipBox(', 'createWarningBox(', 'createLuaToolBtn(', 'setToolTipText(', 'setText(', 'setStatus(',
            'setDialogTitle(', 'showMessageDialog(', 'showConfirmDialog(', 'createTitledBorder(', 'setTitle(', 'drawTag(',
            'new MapDimension(', 'new SymmetryOption(', 'FileNameExtensionFilter(', 'buildZoneCard(', 'new ZoneConfig(')
SKIP_LINE = re.compile(r'^\s*(case\s|import\s|package\s|@|//|\*|/\*)')
SKIP_CTX = ('I18n.t(', '.equals(', 'equalsIgnoreCase(', '.setting(', 'getProperty(', 'System.getProperty', 'new Font(', 'bindKey(',
            'Thread(', '.startsWith(', '.endsWith(', '.contains(', '.replace(', '.replaceAll(', '.split(', '.matches(',
            'Pattern.compile', 'ImageIO.write', 'valueOf(', 'System.err', 'System.out', 'printStackTrace', 'new Color(',
            'putClientProperty', 'getActionMap', 'getInputMap', '.resolve(', 'Path.of(', 'new File(', 'exportMap(',
            'new BarExportOptions', 'writeGroundErrorLog', 'Exception(', 'new Marker(', 'new Spawn(', 'new Prop(', 'sb.append(')
WORD = re.compile(r"[A-Za-zÀ-ÿ']{3,}")


def unescape(lit):
    return bytes(lit, 'utf-8').decode('unicode_escape').encode('latin-1', 'ignore').decode('utf-8', 'ignore') if '\\' in lit else lit


def java_unescape(lit):
    out, i = [], 0
    while i < len(lit):
        c = lit[i]
        if c == '\\' and i + 1 < len(lit):
            n = lit[i + 1]
            if n == 'n': out.append('\n')
            elif n == 't': out.append('\t')
            elif n == 'u':
                out.append(chr(int(lit[i + 2:i + 6], 16))); i += 4
            else: out.append(n)
            i += 2
        else:
            out.append(c); i += 1
    return ''.join(out)


def is_ui(text, line, start):
    words = WORD.findall(text)
    if not words:
        return False
    if re.fullmatch(r'[a-z0-9_.\-/]+', text):          # identificatori, chiavi, percorsi
        return False
    if text.startswith(('/', '#', 'http', 'custom_', 'mex_', 'hydro_', 'spawn_')):
        return False
    before = line[:start]
    if before.rstrip().endswith('I18n.t('):               # gia' tradotto
        return False
    if before.rstrip().endswith(('.equals(', 'equalsIgnoreCase(')):   # confronto logico, non testo a schermo
        return False
    if text.startswith(('--', 'local ', '    ', 'function ')) or 'function script' in text:   # codice Lua dei modelli
        return False
    if text in ('Map_', 'BAR Map') or 'GENERA TERRENO ' in text and text.startswith(']') or text.startswith('] EXPORT'):
        return False
    if not WORD.findall(re.sub(r'<[^>]*>', ' ', text)):   # solo markup HTML
        return False
    in_ui_call = any(call in before for call in UI_CALLS)
    if any(ctx in before[max(0, len(before) - 60):] for ctx in SKIP_CTX) and not in_ui_call:
        return False
    if in_ui_call:
        return True
    # fuori da una chiamata nota: serve una frase (spazio + almeno due parole) o un'etichetta maiuscola
    if ' ' in text.strip() and len(words) >= 2:
        return True
    return False


def main():
    args = [a for a in sys.argv[1:] if a != '--apply']
    apply = '--apply' in sys.argv
    base, prefix, files = args[0], args[1], args[2:]
    store_path = os.path.join(HERE, 'strings_%s.json' % prefix)
    store = json.load(open(store_path, encoding='utf-8')) if os.path.isfile(store_path) else {}
    by_text = {v: k for k, v in store.items()}
    for name in files:
        path = os.path.join(base, name)
        src = open(path, encoding='utf-8', newline='').read()
        nl = '\r\n' if '\r\n' in src else '\n'
        out_lines = []
        for line in src.split(nl):
            if SKIP_LINE.match(line) or 'static final String' in line and 'I18n' not in line and '[]' not in line and False:
                out_lines.append(line)
                continue
            pieces, last = [], 0
            for m in STR.finditer(line):
                text = java_unescape(m.group(1))
                if not is_ui(text, line, m.start()):
                    continue
                key = by_text.get(text)
                if key is None:
                    key = '%s.%d' % (prefix, len(store) + 1)
                    store[key] = text
                    by_text[text] = key
                pieces.append(line[last:m.start()] + 'I18n.t("%s")' % key)
                last = m.end()
            out_lines.append(''.join(pieces) + line[last:] if pieces else line)
        if apply:
            open(path, 'w', encoding='utf-8', newline='').write(nl.join(out_lines))
    if apply:
        json.dump(store, open(store_path, 'w', encoding='utf-8'), ensure_ascii=False, indent=0)
    else:
        for k, v in store.items():
            print(k + '\t' + v.replace('\n', '\\n'))
    print(len(store), 'testi', file=sys.stderr)


main()

"""Audit canonical Gen 3 forms; require complete animation, not portraits alone."""
import json
import sys
from pathlib import Path
from import_spritecollab_forms import collect, make_one

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'only_tham_khao/SpriteCollab-master'
OUT = ROOT / 'outputs/gen3-forms-audit'
CANONICAL = {'Mega', 'Mega_Z', 'Galar', 'Primal', 'Sunny', 'Rainy', 'Snowy', 'Purple', 'Attack', 'Defense', 'Speed'}

def main():
    tracker = json.loads((SOURCE / 'tracker.json').read_text(encoding='utf-8'))
    rows = []
    for dex in range(252, 387):
        entry = tracker.get('%04d' % dex, {})
        for fid, form in entry.get('subgroups', {}).items():
            if fid == '0000':
                continue
            name = form.get('name', '')
            rel = '%04d/%s' % (dex, fid)
            row = {'dex': dex, 'pokemon': entry.get('name', ''), 'form': name, 'path': rel, 'complete': False, 'shiny': False, 'status': 'Excluded: alternate art/cutscene/recolour'}
            if name in CANONICAL:
                try:
                    collect(SOURCE / 'sprite' / rel)
                    row['complete'] = True
                    row['status'] = 'Complete normal animation'
                    try:
                        collect(SOURCE / 'sprite' / rel / '0001')
                        row['shiny'] = True
                    except Exception:
                        pass
                except Exception as error:
                    row['status'] = 'Skipped: ' + str(error)
            rows.append(row)
    OUT.mkdir(parents=True, exist_ok=True)
    (OUT / 'audit.json').write_text(json.dumps(rows, ensure_ascii=False, indent=2), encoding='utf-8')
    for row in rows:
        if row['form'] in CANONICAL:
            print(row['dex'], row['pokemon'], row['form'], row['complete'], row['shiny'], row['status'])
    if '--import' in sys.argv:
        credits = []
        for row in rows:
            if not row['complete']:
                continue
            rel = row['path']
            mega = row['form'] == 'Mega'
            stem = str(row['dex']) if mega else rel.replace('/', '-')
            make_one(SOURCE / 'sprite', SOURCE / 'portrait', rel, stem, ROOT / ('res/megaraw' if mega else 'res/formraw'), ROOT / ('res/megaav' if mega else 'res/formav'))
            credits.append(rel + ' ' + row['form'])
            for kind in ('sprite', 'portrait'):
                file = SOURCE / kind / rel / 'credits.txt'
                if file.exists():
                    credits.append(kind + '\n' + file.read_text(encoding='utf-8'))
        (ROOT / 'res/credits/Gen3-forms.txt').write_text('Source: https://github.com/PMDCollab/SpriteCollab\nChanges: PACR packing, resized portraits or sprite-derived avatars.\n\n' + '\n\n'.join(credits), encoding='utf-8')

if __name__ == '__main__':
    main()

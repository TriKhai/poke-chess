"""Inventory every SpriteCollab form without silently enabling unreviewed art.

Completeness means the six runtime actions (documented Idle fallbacks) in eight
directions. Inventory is not a declaration of canonical gameplay eligibility.
"""
import json
from pathlib import Path
from import_spritecollab_forms import collect

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'only_tham_khao/SpriteCollab-master'
OUT = ROOT / 'outputs/all-gen-audit'

def generation(dex):
    return next((i + 1 for i, limit in enumerate((151,251,386,493,649,721,809,905,1025)) if dex <= limit), 0)

def main():
    tracker = json.loads((SOURCE / 'tracker.json').read_text(encoding='utf-8'))
    rows = []
    def walk(dex, pokemon, groups, prefix, labels):
        for fid, form in groups.items():
            path = prefix + '/' + fid
            names = labels + ([form.get('name', '')] if form.get('name') else [])
            # Palette and gender children are inventoried separately by the parent.
            if fid != '0000' and names and not any(n in ('Shiny', 'Female', 'Male') for n in names):
                row = dict(dex=dex, generation=generation(dex), pokemon=pokemon,
                           form='/'.join(names), path=path, complete=False, shiny=False,
                           portrait=(SOURCE / 'portrait' / path / 'Normal.png').exists())
                try:
                    collect(SOURCE / 'sprite' / path)
                    row['complete'] = True
                    row['reason'] = 'Six usable actions, eight directions'
                except Exception as error:
                    row['reason'] = str(error).replace(str(SOURCE), 'SpriteCollab')
                try:
                    collect(SOURCE / 'sprite' / path / '0001')
                    row['shiny'] = True
                except Exception:
                    pass
                rows.append(row)
            if len(path.split('/')) < 3:
                walk(dex, pokemon, form.get('subgroups', {}), path, names)
    for key, entry in tracker.items():
        dex = int(key)
        if 1 <= dex <= 1025:
            walk(dex, entry.get('name', ''), entry.get('subgroups', {}), key, [])
    OUT.mkdir(parents=True, exist_ok=True)
    (OUT / 'forms.json').write_text(json.dumps(rows, ensure_ascii=False, indent=2), encoding='utf-8')
    summary = []
    for gen in range(1, 10):
        group = [r for r in rows if r['generation'] == gen]
        summary.append(dict(generation=gen, candidates=len(group), complete=sum(r['complete'] for r in group),
                            portrait_only=sum(r['portrait'] and not r['complete'] for r in group)))
    (OUT / 'summary.json').write_text(json.dumps(summary, indent=2), encoding='utf-8')
    print(json.dumps(summary, indent=2))

if __name__ == '__main__':
    main()

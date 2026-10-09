"""Audit supplied form sprites, excluding recolours/beta/cutscene alternatives."""
import json
from pathlib import Path
from import_spritecollab_forms import collect, make_one

ROOT=Path('only_tham_khao/SpriteCollab-master')
OUT=Path('outputs/gen2-forms-audit')
OUT.mkdir(parents=True,exist_ok=True)
tracker=json.loads((ROOT/'tracker.json').read_text(encoding='utf-8'))
rows=[]
for dex in [144,145,146]+list(range(152,252)):
    entry=tracker.get('%04d'%dex,{})
    for fid,form in entry.get('subgroups',{}).items():
        if fid=='0000':continue
        name=form.get('name','')
        rel='%04d/%s'%(dex,fid)
        playable=name in ('Mega','Galar','Hisui','Paldea','Spiky','Shadow') or dex==201
        status='Excluded: cosmetic/cutscene/beta or alternate illustration'
        if playable:
            try:
                collect(ROOT/'sprite'/rel)
                status='Complete normal animation'
                try:collect(ROOT/'sprite'/rel/'0001');status+=' + shiny'
                except Exception:status+='; shiny unavailable'
            except Exception as e:status='Skipped: '+str(e)
        rows.append({'dex':dex,'pokemon':entry.get('name',''),'form':name,'path':rel,'playable':playable,'status':status})
(OUT/'audit.json').write_text(json.dumps(rows,ensure_ascii=False,indent=2),encoding='utf-8')
(OUT/'audit.md').write_text('# Supplied Gen 2 forms and Galar birds\n\n| Dex | Pokemon | Form | Audit |\n|---|---|---|---|\n'+'\n'.join('| %s | %s | %s | %s |'%(r['dex'],r['pokemon'],r['form'],r['status']) for r in rows),encoding='utf-8')
for r in rows:
    if r['playable']:print(r['dex'],r['pokemon'],r['form'],r['status'])
if '--import' in __import__('sys').argv:
    for r in rows:
        if not r['playable'] or not r['status'].startswith('Complete'):continue
        dex=r['dex'];rel=r['path'];stem=str(dex) if r['form']=='Mega' else rel.replace('/','-')
        raw=Path('res/megaraw' if r['form']=='Mega' else 'res/formraw')
        av=Path('res/megaav' if r['form']=='Mega' else 'res/formav')
        make_one(ROOT/'sprite',ROOT/'portrait',rel,stem,raw,av)
        credits=Path('res/credits/Gen2-forms.txt')
        with credits.open('a',encoding='utf-8') as f:
            f.write('\n'+rel+' '+r['form']+'\n')
            for kind in ('sprite','portrait'):
                c=ROOT/kind/rel/'credits.txt'
                if c.exists():f.write(kind+'\n'+c.read_text(encoding='utf-8')+'\n')

#!/usr/bin/env python3
"""Package source-backed Primal, Origin, Paradox and Zygarde form atlases."""
import json,struct,sys
from pathlib import Path
from PIL import Image
ACTIONS=("Idle","Walk","Attack","Hop","Hurt","Pose")
ATTACKS=("Shock","Punch","Shoot","Attack","Strike","Kick","SpAttack","Ricochet","Slam","Charge","Emit","Rotate")
FORMS=("0382-0001","0383-0001","0487-0001","1009","0718","0718-0001","0718-0002","0386-0001","0386-0002","0386-0003","0720-0001","0800-0003")
def key(item):
    try:return int(item[0].split('/')[-1])
    except ValueError:return item[0]
def main():
    if len(sys.argv)!=5:raise SystemExit('usage: make_special_form_atlas.py ATLAS_ROOT PORTRAIT_ROOT RAW_OUT AVATAR_OUT')
    root,portraits,out,av=map(Path,sys.argv[1:]);out.mkdir(parents=True,exist_ok=True);av.mkdir(parents=True,exist_ok=True)
    for d in (out,av):
        for p in d.iterdir():
            if p.is_file():p.unlink()
    for stem in FORMS:
        data=json.loads((root/(stem+'.json')).read_text(encoding='utf-8'));texture=data['textures'][0]
        source=root/texture['image'];Image.open(source).save(out/(stem+'.png'),optimize=True)
        for palette,suffix in (('Normal',''),('Shiny','-shiny')):
            clips={}
            for fr in texture['frames']:
                parts=fr['filename'].split('/')
                if len(parts)<5 or parts[0]!=palette or parts[2]!='Anim':continue
                # The upstream 100% Zygarde sheet appends 10% frames under the
                # same Normal/Shiny animation namespace.  Those small frames
                # use a 40px-wide source canvas; keeping them makes the 100%
                # animation suddenly alternate with the 10% model.
                if stem=='0718-0002' and fr['sourceSize']['w']<=40:continue
                clips.setdefault((parts[1],int(parts[3])),[]).append((fr['filename'],fr))
            if not clips:continue
            attack=next((a for a in ATTACKS if any((a,d) in clips for d in range(8))),'Idle')
            with (out/(stem+suffix+'.dat')).open('wb') as stream:
                stream.write(b'PACR');stream.write(struct.pack('>B',6))
                for action in ('Idle','Walk',attack,'Hop','Hurt','Pose'):
                    for direction in range(8):
                        fs=sorted(clips.get((action,direction),[]),key=key) or sorted(clips.get(('Idle',direction),[]),key=key);stream.write(struct.pack('>H',len(fs)))
                        for _,fr in fs:
                            b,p,s=fr['frame'],fr['spriteSourceSize'],fr['sourceSize'];vals=(b['x'],b['y'],b['w'],b['h'],p['x'],p['y'],s['w'],s['h']);stream.write(struct.pack('>8HB',*(vals+(1 if fr.get('rotated') else 0,))))
        parts=stem.split('-');portrait=portraits/parts[0]
        if len(parts)>1:portrait=portrait/parts[1]
        with Image.open(portrait/'Normal.png') as im:im.convert('RGBA').resize((32,32),Image.Resampling.NEAREST).save(av/(stem+'.png'),optimize=True)
        shiny=(portrait/'0001'/'Normal.png') if len(parts)>1 else (portrait/'0000'/'0001'/'Normal.png')
        if shiny.exists():
            with Image.open(shiny) as im:im.convert('RGBA').resize((32,32),Image.Resampling.NEAREST).save(av/(stem+'-shiny.png'),optimize=True)
    print('generated',len(FORMS),'special form atlases')
if __name__=='__main__':main()

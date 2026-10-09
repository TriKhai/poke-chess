#!/usr/bin/env python3
"""Package source-backed Mega form atlases and portraits used by v1.5.5."""
import json,shutil,struct,sys
from pathlib import Path
from PIL import Image
ACTIONS=("Idle","Walk","Attack","Hop","Hurt","Pose")
ATTACKS=("Shock","Punch","Shoot","Attack","Strike","Kick","SpAttack","Ricochet","Slam","Charge","Emit","Rotate")
DEX=(208,229,302,310,323,354,384,428) # Altaria/Abomasnow have portraits but no Mega animation atlas.
def key(item):
    try:return int(item[0].split('/')[-1])
    except ValueError:return item[0]
def main():
    if len(sys.argv)!=5:raise SystemExit('usage: make_mega_atlas.py ATLAS_ROOT PORTRAIT_ROOT RAW_OUT AVATAR_OUT')
    root,portraits,out,av=map(Path,sys.argv[1:]);out.mkdir(parents=True,exist_ok=True);av.mkdir(parents=True,exist_ok=True)
    for d in (out,av):
        for p in d.iterdir():
            if p.is_file():p.unlink()
    made=0
    for dex in DEX:
        stem=f'{dex:04d}-0001';jp=root/(stem+'.json');data=json.loads(jp.read_text(encoding='utf-8'));texture=data['textures'][0]
        source=root/texture['image'];shutil.copyfile(source,out/(str(dex)+'.png'))
        for palette,suffix in (('Normal',''),('Shiny','-shiny')):
            clips={}
            for frame in texture['frames']:
                parts=frame['filename'].split('/')
                if len(parts)<5 or parts[0]!=palette or parts[2]!='Anim':continue
                clips.setdefault((parts[1],int(parts[3])),[]).append((frame['filename'],frame))
            if not clips:continue
            attack=next((a for a in ATTACKS if any((a,d) in clips for d in range(8))),'Idle')
            with (out/(str(dex)+suffix+'.dat')).open('wb') as stream:
                stream.write(b'PACR');stream.write(struct.pack('>B',6))
                for action in ('Idle','Walk',attack,'Hop','Hurt','Pose'):
                    for direction in range(8):
                        fs=sorted(clips.get((action,direction),[]),key=key) or sorted(clips.get(('Idle',direction),[]),key=key);stream.write(struct.pack('>H',len(fs)))
                        for _,fr in fs:
                            b,p,s=fr['frame'],fr['spriteSourceSize'],fr['sourceSize'];vals=(b['x'],b['y'],b['w'],b['h'],p['x'],p['y'],s['w'],s['h']);stream.write(struct.pack('>8HB',*(vals+(1 if fr.get('rotated') else 0,))))
        portrait=portraits/f'{dex:04d}'/'0001'/'Normal.png'
        with Image.open(portrait) as im:im.convert('RGBA').resize((32,32),Image.Resampling.NEAREST).save(av/(str(dex)+'.png'),optimize=True)
        shiny=portraits/f'{dex:04d}'/'0001'/'0001'/'Normal.png'
        if shiny.exists():
            with Image.open(shiny) as im:im.convert('RGBA').resize((32,32),Image.Resampling.NEAREST).save(av/(str(dex)+'-shiny.png'),optimize=True)
        made+=1
    print('generated',made,'Mega animation atlases and portraits')
if __name__=='__main__':main()

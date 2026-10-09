#!/usr/bin/env python3
"""Build Shiny frame indexes and portraits for Gen 4-9 CollectionDex entries.

The normal /dexraw PNG already contains both Normal and Shiny frames, so this
only writes compact coordinate tables plus 32px portraits.
"""
import json, re, struct, sys
from pathlib import Path
from PIL import Image

ACTIONS=("Idle","Walk","Attack","Hop","Hurt","Pose")
ATTACKS=("Shock","Punch","Shoot","Attack","Strike","Kick","SpAttack","Ricochet","Slam","Charge","Emit","Rotate")

def numeric(item):
    try:return int(item[0].split('/')[-1])
    except ValueError:return item[0]

def main():
    if len(sys.argv)!=6:raise SystemExit("usage: make_collection_shiny.py CollectionDex.java ATLAS_ROOT PORTRAIT_ROOT DAT_OUT AVATAR_OUT")
    java,root,portraits,dat_out,av_out=map(Path,sys.argv[1:]);dat_out.mkdir(parents=True,exist_ok=True);av_out.mkdir(parents=True,exist_ok=True)
    for out in (dat_out,av_out):
        for p in out.iterdir():
            if p.is_file():p.unlink()
    text=java.read_text(encoding='utf-8');match=re.search(r'public static final int\[\] DEX=\{([^}]*)\}',text)
    if not match:raise SystemExit('CollectionDex DEX list not found')
    dexes=[int(x) for x in match.group(1).split(',') if x.strip()]
    made=avatars=0
    for dex in dexes:
        key=f'{dex:04d}';jp=root/(key+'.json')
        if not jp.exists():continue
        data=json.loads(jp.read_text(encoding='utf-8'));clips={}
        for texture in data.get('textures',[]):
            for frame in texture.get('frames',[]):
                parts=frame['filename'].split('/')
                if len(parts)<5 or parts[0]!='Shiny' or parts[2]!='Anim':continue
                clips.setdefault((parts[1],int(parts[3])),[]).append((frame['filename'],frame))
        if clips:
            attack=next((a for a in ATTACKS if any((a,d) in clips for d in range(8))),'Idle')
            with (dat_out/(str(dex)+'.dat')).open('wb') as stream:
                stream.write(b'PACR');stream.write(struct.pack('>B',len(ACTIONS)))
                for action in ('Idle','Walk',attack,'Hop','Hurt','Pose'):
                    for direction in range(8):
                        frames=sorted(clips.get((action,direction),[]),key=numeric) or sorted(clips.get(('Idle',direction),[]),key=numeric)
                        stream.write(struct.pack('>H',len(frames)))
                        for _,fr in frames:
                            box,pos,src=fr['frame'],fr['spriteSourceSize'],fr['sourceSize'];vals=(box['x'],box['y'],box['w'],box['h'],pos['x'],pos['y'],src['w'],src['h'])
                            stream.write(struct.pack('>8HB',*(vals+(1 if fr.get('rotated') else 0,))))
            made+=1
        portrait=portraits/key/'0000'/'0001'/'Normal.png'
        if portrait.exists():
            with Image.open(portrait) as im:im.convert('RGBA').resize((32,32),Image.Resampling.NEAREST).save(av_out/(str(dex)+'.png'),optimize=True)
            avatars+=1
    print('generated',made,'Gen 4-9 Shiny indexes and',avatars,'avatars')

if __name__=='__main__':main()

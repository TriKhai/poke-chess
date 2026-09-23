#!/usr/bin/env python3
"""Convert original attack/status frame folders into compact MIDP strips."""
import os, sys
from pathlib import Path
from PIL import Image

STATUS = ["ARMOR_BREAK","BLINDED","BURN","CHARM","CONFUSION","CURSE",
          "FATIGUE","FLINCH","FREEZE","LOCKED","PARALYSIS","POISON",
          "PROTECT","SILENCE","SLEEP","WOUND"]
TYPES = ["NORMAL","FIRE","WATER","GRASS","ELECTRIC","ROCK","PSYCHIC",
         "FIGHTING","FLYING","DRAGON","GHOST","BUG","POISON","GROUND",
         "ICE","DARK","STEEL","FAIRY","AMORPHOUS","AQUATIC","ARTIFICIAL",
         "BABY","FIELD","FLORA","FOSSIL","GOURMET","HUMAN","LIGHT",
         "MONSTER","SOUND","WILD"]

def fit(path, size):
    im=Image.open(path).convert("RGBA")
    box=im.getchannel("A").getbbox()
    if box: im=im.crop(box)
    scale=min(float(size)/im.width,float(size)/im.height)
    w=max(1,int(im.width*scale)); h=max(1,int(im.height*scale))
    im=im.resize((w,h),Image.Resampling.NEAREST)
    out=Image.new("RGBA",(size,size),(0,0,0,0))
    out.alpha_composite(im,((size-w)//2,(size-h)//2))
    return out

def strip(folder, output, size):
    files=sorted(Path(folder).glob("*.png"))
    if not files: return False
    chosen=[files[(i*(len(files)-1))//3] for i in range(4)]
    out=Image.new("RGBA",(size*4,size),(0,0,0,0))
    for i,p in enumerate(chosen): out.alpha_composite(fit(p,size),(i*size,0))
    out.save(output,optimize=True)
    return True

def main():
    if len(sys.argv)!=5:
        raise SystemExit("usage: make_combat_fx.py STATUS_TPS ATTACK_TPS STATUS_OUT ATTACK_OUT")
    sr,ar,so,ao=sys.argv[1:]
    os.makedirs(so,exist_ok=True); os.makedirs(ao,exist_ok=True)
    for i,name in enumerate(STATUS):
        if not strip(Path(sr,name),Path(so,str(i)+".png"),16):
            raise RuntimeError("missing status "+name)
    for i,name in enumerate(TYPES):
        root=Path(ar,name)
        # Source has no projectile set for every extended synergy. Fall back to Normal.
        for kind,sub in enumerate(("melee","range","hit")):
            folder=root/sub
            if not folder.exists(): folder=Path(ar,"NORMAL",sub)
            strip(folder,Path(ao,"%d_%d.png"%(i,kind)),16)
    print("generated",len(STATUS),"status strips and attack strips for",len(TYPES),"types")

if __name__=="__main__": main()

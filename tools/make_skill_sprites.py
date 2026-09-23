#!/usr/bin/env python3
"""Build nine compact 4-frame skill strips from the original web assets."""
import os, sys
from pathlib import Path
from PIL import Image

SIZE = 32
SKILLS = ["SLASH", "RECOVER", "EXPLOSION", "BULK_UP", "THUNDER",
          "DISCHARGE", "DEFENSE_CURL", "ABSORB", "HYPER_BEAM_CHARGE"]

def fit(im):
    im = im.convert("RGBA")
    box = im.getchannel("A").getbbox()
    if box: im = im.crop(box)
    scale = min(float(SIZE) / im.width, float(SIZE) / im.height)
    w=max(1,int(im.width*scale)); h=max(1,int(im.height*scale))
    im=im.resize((w,h),Image.Resampling.NEAREST)
    out=Image.new("RGBA",(SIZE,SIZE),(0,0,0,0))
    out.alpha_composite(im,((SIZE-w)//2,(SIZE-h)//2))
    return out

def main():
    if len(sys.argv)!=3: raise SystemExit("usage: make_skill_sprites.py ABILITIES_TPS OUTPUT_DIR")
    root,out=sys.argv[1],sys.argv[2]; os.makedirs(out,exist_ok=True)
    for i,name in enumerate(SKILLS):
        files=sorted(Path(root,name).glob("*.png"))
        if not files: raise RuntimeError("missing "+name)
        chosen=[files[(k*(len(files)-1))//3] for k in range(4)]
        strip=Image.new("RGBA",(SIZE*4,SIZE),(0,0,0,0))
        for k,p in enumerate(chosen): strip.alpha_composite(fit(Image.open(p)),(k*SIZE,0))
        strip.save(os.path.join(out,str(i)+".png"),optimize=True)
        print(i,name)
if __name__=="__main__": main()

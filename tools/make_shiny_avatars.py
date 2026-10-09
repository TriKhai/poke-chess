#!/usr/bin/env python3
"""Build compact 32x32 Shiny portraits for Gen 1-3."""
import os,sys
from PIL import Image
def main():
    if len(sys.argv)!=3:raise SystemExit("usage: make_shiny_avatars.py PORTRAIT_ROOT OUTPUT_DIR")
    root,out=sys.argv[1:];os.makedirs(out,exist_ok=True);made=0
    for dex in range(1,387):
        src=os.path.join(root,"%04d"%dex,"0000","0001","Normal.png")
        if not os.path.exists(src):continue
        im=Image.open(src).convert("RGBA")
        if im.size!=(32,32):im=im.resize((32,32),Image.Resampling.NEAREST)
        im.save(os.path.join(out,"%d.png"%(dex-1)),optimize=True);made+=1
    print("generated",made,"Shiny avatars")
if __name__=="__main__":main()

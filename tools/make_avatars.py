#!/usr/bin/env python3
"""Convert official 40x40 Normal portraits into 32x32 MIDP UI avatars."""
import io,os,sys,zipfile
from PIL import Image

DEX = list(range(1,387))

def main():
    if len(sys.argv)!=3: raise SystemExit("usage: make_avatars.py PORTRAITS_ROOT OUTPUT_DIR")
    root,out=sys.argv[1],sys.argv[2]; os.makedirs(out,exist_ok=True)
    archive=zipfile.ZipFile(root) if zipfile.is_zipfile(root) else None
    for sp,dex in enumerate(DEX):
        rel="%04d/Normal.png"%dex
        if archive:
            raw=archive.read("portraits/"+rel)
            im=Image.open(io.BytesIO(raw))
            src=root+":"+rel
        else:
            src=os.path.join(root,rel)
            im=Image.open(src)
        im=im.convert("RGBA").resize((32,32),Image.Resampling.NEAREST)
        im.save(os.path.join(out,str(sp)+".png"),optimize=True)
        print("%3d <- %s"%(sp,src))
    if archive: archive.close()
if __name__=="__main__": main()

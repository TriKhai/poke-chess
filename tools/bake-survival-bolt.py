"""Postprocess generated Sprite Forge frames, never draw replacement artwork."""
from PIL import Image
from pathlib import Path
import re
root=Path(__file__).resolve().parent.parent
frames=sorted((root/'assets/survival-bolt').glob('projectile-*.png'))
if len(frames)!=8: raise ValueError('Expected eight processed frames')
sheet=Image.new('RGBA',(32*8,32*8))
for d in range(8):
    for f,p in enumerate(frames):
        im=Image.open(p).convert('RGBA').rotate(-90+d*45,resample=Image.Resampling.BICUBIC)
        sheet.paste(im,(f*32,d*32))
sheet.save(root/'res/fx/survival-bolt.png',optimize=True)

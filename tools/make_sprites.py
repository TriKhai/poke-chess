#!/usr/bin/env python3
"""
Tao res/sp/<index>.png tu thu muc assets cua pokemonAutoChess.

  python tools/make_sprites.py <duong dan toi assets> [--mode idle|portrait]

  --mode idle      (mac dinh) cat khung "Normal/Walk/Anim/0/0000" trong atlas
                   app/public/src/assets/pokemons/<dex>.png/.json -> sprite trong suot
  --mode portrait  dung assets/portraits/<dex>/Normal.png (40x40, nen dac)

<index> la thu tu loai trong src/pac/Data.java (0..40), KHONG phai so Pokedex.
Can:  pip install pillow
"""
import json, os, sys
from PIL import Image

# (index trong Data.java) -> so Pokedex
DEX = [16, 17, 18, 19, 20, 129, 130, 4, 5, 6, 7, 8, 9, 1, 2, 3, 172, 25, 26,
       74, 75, 76, 63, 64, 65, 66, 67, 68, 92, 93, 94, 147, 148, 149,
       246, 247, 248, 150, 145, 146, 144]
FRAME = "Normal/Walk/Anim/0/0000"          # tu the dung, huong nhin xuong
FRAME_OVERRIDE = {74: "Normal/Idle/Anim/0/0000"}   # Geodude: khung Walk chi la 1 vet det


def idle_sprite(assets, dex):
    base = os.path.join(assets, "pokemons", "%04d" % dex)
    atlas = json.load(open(base + ".json"))
    sheet = Image.open(base + ".png").convert("RGBA")
    for tex in atlas["textures"]:
        for f in tex["frames"]:
            if f["filename"] != FRAME_OVERRIDE.get(dex, FRAME):
                continue
            r = f["frame"]
            if f.get("rotated"):
                im = sheet.crop((r["x"], r["y"], r["x"] + r["h"], r["y"] + r["w"])).rotate(90, expand=True)
            else:
                im = sheet.crop((r["x"], r["y"], r["x"] + r["w"], r["y"] + r["h"]))
            return im
    raise SystemExit("khong thay khung %s trong %s.json" % (FRAME_OVERRIDE.get(dex, FRAME), base))


def square(im):
    """cat sat vien, dua vao o vuong, can giua ngang, sat day."""
    box = im.getbbox()
    if box:
        im = im.crop(box)
    s = max(im.size)
    out = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    out.paste(im, ((s - im.width) // 2, s - im.height))
    return out


def main():
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    mode = "idle"
    if "--mode" in sys.argv:
        mode = sys.argv[sys.argv.index("--mode") + 1]
        args = [a for a in args if a != mode]
    if not args:
        print(__doc__)
        return
    assets = args[0]
    out_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "res", "sp")
    os.makedirs(out_dir, exist_ok=True)
    for i, dex in enumerate(DEX):
        if mode == "portrait":
            im = Image.open(os.path.join(assets, "portraits", "%04d" % dex, "Normal.png")).convert("RGBA")
        else:
            im = square(idle_sprite(assets, dex))
        im.save(os.path.join(out_dir, "%d.png" % i), optimize=True)
        print(i, dex, im.size)


if __name__ == "__main__":
    main()

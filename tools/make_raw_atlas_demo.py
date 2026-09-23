#!/usr/bin/env python3
"""Package selected original TexturePacker atlases without cutting their frames.

The PNG is copied byte-for-byte.  A small binary index replaces JSON on CLDC and
stores frame rectangles, source canvas sizes and offsets for six complete clips.
"""
import json
import os
import shutil
import struct
import sys

DEMO = tuple(range(1, 387))  # complete Generations 1-3
ACTIONS = ("Idle", "Walk", "Attack", "Hop", "Hurt", "Pose")
ATTACKS = ("Shock", "Punch", "Shoot", "Attack", "Strike", "Kick",
           "SpAttack", "Ricochet", "Slam", "Charge", "Emit", "Rotate")

def numeric_key(item):
    name = item[0].split('/')[-1]
    try: return int(name)
    except ValueError: return name

def main():
    if len(sys.argv) != 3:
        raise SystemExit("usage: make_raw_atlas_demo.py POKEMON_ATLAS_DIR OUTPUT_DIR")
    root, out = sys.argv[1:]
    os.makedirs(out, exist_ok=True)
    for dex in DEMO:
        key = "%04d" % dex
        data = json.load(open(os.path.join(root, key + ".json")))
        if len(data["textures"]) != 1:
            raise RuntimeError("demo expects one texture for " + key)
        texture = data["textures"][0]
        shutil.copyfile(os.path.join(root, texture["image"]),
                        os.path.join(out, "%d.png" % (dex - 1)))
        clips = {}
        for frame in texture["frames"]:
            parts = frame["filename"].split('/')
            if len(parts) < 5 or parts[0] != "Normal" or parts[2] != "Anim":
                continue
            clips.setdefault((parts[1], int(parts[3])), []).append((frame["filename"], frame))
        attack = next((name for name in ATTACKS
                       if any((name, direction) in clips for direction in range(8))), "Idle")
        resolved = ("Idle", "Walk", attack, "Hop", "Hurt", "Pose")
        with open(os.path.join(out, "%d.dat" % (dex - 1)), "wb") as stream:
            stream.write(b"PACR")
            stream.write(struct.pack(">B", len(ACTIONS)))
            total = 0
            for action in resolved:
                for direction in range(8):
                    frames = sorted(clips.get((action, direction), []), key=numeric_key)
                    if not frames:
                        frames = sorted(clips.get(("Idle", direction), []), key=numeric_key)
                    stream.write(struct.pack(">H", len(frames)))
                    total += len(frames)
                    for _, fr in frames:
                        box, pos, src = fr["frame"], fr["spriteSourceSize"], fr["sourceSize"]
                        values = (box["x"], box["y"], box["w"], box["h"],
                                  pos["x"], pos["y"], src["w"], src["h"])
                        stream.write(struct.pack(">8HB", *(values + (1 if fr.get("rotated") else 0,))))
        print("#%s raw atlas: %d indexed frames, attack=%s" % (key, total, attack))

if __name__ == "__main__":
    main()

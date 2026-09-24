#!/usr/bin/env python3
"""Package original Gen 4-9 atlases for the collection-only Full build."""
import csv
import json
import shutil
import struct
import sys
from pathlib import Path

ACTIONS = ("Idle", "Walk", "Attack", "Hop", "Hurt", "Pose")
ATTACKS = ("Shock", "Punch", "Shoot", "Attack", "Strike", "Kick",
           "SpAttack", "Ricochet", "Slam", "Charge", "Emit", "Rotate")

def numeric_key(item):
    name = item[0].split("/")[-1]
    try:
        return int(name)
    except ValueError:
        return name

def main():
    if len(sys.argv) != 5:
        raise SystemExit("usage: make_collection_raw_atlas.py CSV ATLAS_ROOT PORTRAIT_ROOT OUT")
    csv_path, root, portraits, out = map(Path, sys.argv[1:])
    out.mkdir(parents=True, exist_ok=True)
    for old in out.iterdir():
        if old.is_file():
            old.unlink()

    dexes = set()
    with csv_path.open(encoding="utf-8-sig", newline="") as stream:
        for row in csv.DictReader(stream):
            idx = row["Index"]
            if len(idx) == 4 and idx.isdigit() and 387 <= int(idx) <= 1025:
                if (portraits / idx / "Normal.png").exists():
                    dexes.add(int(idx))

    made = 0
    for dex in sorted(dexes):
        key = "%04d" % dex
        json_path = root / (key + ".json")
        png_path = root / (key + ".png")
        if not json_path.exists() or not png_path.exists():
            continue
        data = json.loads(json_path.read_text(encoding="utf-8"))
        if len(data["textures"]) != 1:
            continue
        texture = data["textures"][0]
        source_png = root / texture["image"]
        if not source_png.exists():
            source_png = png_path
        shutil.copyfile(source_png, out / ("%d.png" % dex))
        clips = {}
        for frame in texture["frames"]:
            parts = frame["filename"].split("/")
            if len(parts) < 5 or parts[0] != "Normal" or parts[2] != "Anim":
                continue
            clips.setdefault((parts[1], int(parts[3])), []).append((frame["filename"], frame))
        attack = next((name for name in ATTACKS
                       if any((name, direction) in clips for direction in range(8))), "Idle")
        resolved = ("Idle", "Walk", attack, "Hop", "Hurt", "Pose")
        with (out / ("%d.dat" % dex)).open("wb") as stream:
            stream.write(b"PACR")
            stream.write(struct.pack(">B", len(ACTIONS)))
            for action in resolved:
                for direction in range(8):
                    frames = sorted(clips.get((action, direction), []), key=numeric_key)
                    if not frames:
                        frames = sorted(clips.get(("Idle", direction), []), key=numeric_key)
                    stream.write(struct.pack(">H", len(frames)))
                    for _, fr in frames:
                        box, pos, src = fr["frame"], fr["spriteSourceSize"], fr["sourceSize"]
                        values = (box["x"], box["y"], box["w"], box["h"],
                                  pos["x"], pos["y"], src["w"], src["h"])
                        stream.write(struct.pack(">8HB", *(values + (1 if fr.get("rotated") else 0,))))
        made += 1
    print("generated", made, "collection animation atlases")

if __name__ == "__main__":
    main()

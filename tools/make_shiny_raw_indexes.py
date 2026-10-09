#!/usr/bin/env python3
"""Build CLDC indexes for Shiny frames already embedded in Gen 1-3 atlases.

The PNG is not copied: /raw/<sp>.png already contains Normal and Shiny frames.
"""
import json, os, struct, sys

ACTIONS=("Idle","Walk","Attack","Hop","Hurt","Pose")
ATTACKS=("Shock","Punch","Shoot","Attack","Strike","Kick","SpAttack","Ricochet","Slam","Charge","Emit","Rotate")

def key(item):
    name=item[0].split('/')[-1]
    try:return int(name)
    except ValueError:return name

def main():
    if len(sys.argv)!=3:raise SystemExit("usage: make_shiny_raw_indexes.py POKEMON_ATLAS_DIR OUTPUT_DIR")
    root,out=sys.argv[1:];os.makedirs(out,exist_ok=True);made=0
    for dex in range(1,387):
        path=os.path.join(root,"%04d.json"%dex)
        data=json.load(open(path,encoding="utf-8"));clips={}
        for texture in data["textures"]:
            for frame in texture["frames"]:
                parts=frame["filename"].split('/')
                if len(parts)<5 or parts[0]!="Shiny" or parts[2]!="Anim":continue
                clips.setdefault((parts[1],int(parts[3])),[]).append((frame["filename"],frame))
        if not clips:continue
        attack=next((name for name in ATTACKS if any((name,d) in clips for d in range(8))),"Idle")
        resolved=("Idle","Walk",attack,"Hop","Hurt","Pose")
        with open(os.path.join(out,"%d.dat"%(dex-1)),"wb") as stream:
            stream.write(b"PACR");stream.write(struct.pack(">B",len(ACTIONS)))
            for action in resolved:
                for direction in range(8):
                    frames=sorted(clips.get((action,direction),[]),key=key)
                    if not frames:frames=sorted(clips.get(("Idle",direction),[]),key=key)
                    stream.write(struct.pack(">H",len(frames)))
                    for _,fr in frames:
                        box,pos,src=fr["frame"],fr["spriteSourceSize"],fr["sourceSize"]
                        vals=(box["x"],box["y"],box["w"],box["h"],pos["x"],pos["y"],src["w"],src["h"])
                        stream.write(struct.pack(">8HB",*(vals+(1 if fr.get("rotated") else 0,))))
        made+=1
    print("generated",made,"Shiny indexes")
if __name__=="__main__":main()

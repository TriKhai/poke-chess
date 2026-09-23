#!/usr/bin/env python3
"""Convert the original Pokemon multi-atlases into tiny MIDP battle strips.

Output is one 96-frame PNG per species: 8 directions x
(4 idle + 4 attack + 4 victory). Frame dimensions retain each atlas' natural
sourceSize ratio, with evolution-tier height floors for readable progression.
"""
import csv
import json
import os
import sys
from PIL import Image

# pac.Data is now national-dex order: Bulbasaur (#1) through Deoxys (#386).
DEX = list(range(1, 387))
ATTACK_NAMES = ("Attack", "Strike", "Punch", "Kick", "Shoot", "SpAttack",
                "Ricochet", "Slam", "Charge", "Emit", "Rotate", "Shock")
BASE_SCALE = 1.00
MIN_FRAME = 20
MAX_FRAME = 72
MIN_HEIGHT_BY_TIER = [0, 36, 48, 60]
SIDE_PAD = 6
TOP_PAD = 6
BOTTOM_PAD = 2

def frames_for(root, dex):
    key = "%04d" % dex
    with open(os.path.join(root, key + ".json"), "r") as f:
        data = json.load(f)
    sheets = {}
    result = []
    for texture in data["textures"]:
        image_name = texture["image"]
        if image_name not in sheets:
            sheets[image_name] = Image.open(os.path.join(root, image_name)).convert("RGBA")
        sheet = sheets[image_name]
        for fr in texture["frames"]:
            parts = fr["filename"].split("/")
            if len(parts) < 5 or parts[0] != "Normal" or parts[2] != "Anim":
                continue
            result.append((parts[1], int(parts[3]), parts[4], sheet, fr))
    return result

def source_canvas(sheet, fr, natural_w, natural_h):
    src = fr["sourceSize"]
    pos = fr["spriteSourceSize"]
    box = fr["frame"]
    crop = sheet.crop((box["x"], box["y"], box["x"] + box["w"], box["y"] + box["h"]))
    if fr.get("rotated"):
        crop = crop.transpose(Image.Transpose.ROTATE_90)
    canvas = Image.new("RGBA", (src["w"], src["h"]), (0,0,0,0))
    canvas.alpha_composite(crop, (pos["x"], pos["y"]))
    master = Image.new("RGBA", (natural_w, natural_h), (0,0,0,0))
    master.alpha_composite(canvas, ((natural_w-src["w"])//2, natural_h-src["h"]))
    return master

def rebuild(sheet, fr, body_w, body_h, natural_w, natural_h):
    """Preserve the atlas source canvas and its bottom-centre anchor.

    Never alpha-crop an animation frame: effects, wings and lunges are often far
    outside the visible body in other frames.  A species uses one scale for every
    direction/action, exactly like the original atlas renderer.
    """
    canvas = source_canvas(sheet, fr, natural_w, natural_h)
    canvas = canvas.resize((body_w, body_h), Image.Resampling.NEAREST)
    frame_w = body_w + SIDE_PAD * 2
    frame_h = body_h + TOP_PAD + BOTTOM_PAD
    out = Image.new("RGBA", (frame_w, frame_h), (0,0,0,0))
    out.alpha_composite(canvas, (SIDE_PAD, TOP_PAD))
    return out

def pick(seq, action, direction):
    candidates = [x for x in seq if x[0] == action and x[1] == direction]
    candidates.sort(key=lambda x: x[2])
    if not candidates:
        return []
    # Sample the whole clip rather than taking four adjacent near-identical frames.
    n = len(candidates)
    return [candidates[(i * (n-1)) // 3] for i in range(4)]

def main():
    if len(sys.argv) not in (3, 4, 5):
        raise SystemExit("usage: make_anim_sprites.py ASSETS_POKEMONS OUTPUT_DIR [pokemons-data.csv] [VisualSize.java]")
    root, out_dir = sys.argv[1], sys.argv[2]
    static_dir = os.path.join(os.path.dirname(out_dir), "sp")
    sizes = []
    tiers = {d: 1 for d in DEX}
    if len(sys.argv) >= 4:
        for row in csv.DictReader(open(sys.argv[3], encoding="utf-8")):
            if row["Index"].isdigit() and int(row["Index"]) in tiers:
                tiers[int(row["Index"])] = max(1, min(3, int(row["Tier"])))
    os.makedirs(out_dir, exist_ok=True)
    os.makedirs(static_dir, exist_ok=True)
    for sp, dex in enumerate(DEX):
        seq = frames_for(root, dex)
        idle_frames = [x for x in seq if x[0] == "Idle"]
        # All actions participate in the master canvas. Attack/hop frames may use
        # a larger sourceSize than idle; using idle alone clipped those frames.
        natural_w = max(x[4]["sourceSize"]["w"] for x in seq)
        natural_h = max(x[4]["sourceSize"]["h"] for x in seq)
        scale = BASE_SCALE
        if max(natural_w, natural_h) * scale > MAX_FRAME:
            scale = float(MAX_FRAME) / max(natural_w, natural_h)
        body_w = max(MIN_FRAME, int(natural_w * scale + .5))
        body_h = max(MIN_FRAME, int(natural_h * scale + .5))
        # The original combat tier follows most evolution lines and makes later
        # forms visually stronger. Natural giants may already exceed this floor.
        min_h = MIN_HEIGHT_BY_TIER[tiers[dex]]
        if body_h < min_h:
            grow = float(min_h) / body_h
            body_w = int(body_w * grow + .5)
            body_h = min_h
        if max(body_w, body_h) > MAX_FRAME:
            shrink = float(MAX_FRAME) / max(body_w, body_h)
            body_w = max(MIN_FRAME, int(body_w * shrink + .5))
            body_h = max(MIN_FRAME, int(body_h * shrink + .5))
        frame_w = body_w + SIDE_PAD * 2
        frame_h = body_h + TOP_PAD + BOTTOM_PAD
        sizes.append((frame_w, frame_h))
        chosen = []
        for direction in range(8):
            idle = pick(seq, "Idle", direction)
            attack = []
            for name in ATTACK_NAMES:
                attack = pick(seq, name, direction)
                if attack: break
            if not idle: idle = pick(seq, "Walk", direction) or pick(seq, "Hop", direction)
            if not attack: attack = idle
            victory = pick(seq, "Hop", direction) or pick(seq, "Pose", direction) or idle
            chosen += idle + attack + victory
        if len(chosen) != 96:
            raise RuntimeError("missing directional frames for %04d" % dex)
        strip = Image.new("RGBA", (frame_w * 96, frame_h), (0,0,0,0))
        for i, (_, _, _, sheet, fr) in enumerate(chosen):
            strip.alpha_composite(rebuild(sheet, fr, body_w, body_h, natural_w, natural_h), (i * frame_w, 0))
        # Keep full RGBA. Quantizing these tiny pixel sprites makes their antialiased
        # transparency look grey/blurred on several MIDP implementations.
        strip.save(os.path.join(out_dir, "%d.png" % sp), optimize=True)
        # World, collection and catch screens use a single crisp idle frame.
        rebuild(chosen[0][3], chosen[0][4], body_w, body_h, natural_w, natural_h).save(
            os.path.join(static_dir, "%d.png" % sp), optimize=True)
        print("%2d <- #%04d (%dx%d)" % (sp, dex, frame_w, frame_h))
    if len(sys.argv) == 5:
        java = ['package pac;\n\n/** Generated from original atlas proportions plus evolution-size floors; do not edit. */\npublic final class VisualSize {\n    private VisualSize() {}\n']
        java.append('    public static final int[] W={'+','.join(str(w) for w,h in sizes)+'};\n')
        java.append('    public static final int[] H={'+','.join(str(h) for w,h in sizes)+'};\n}\n')
        with open(sys.argv[4], 'w', encoding='utf-8', newline='\n') as f: f.write(''.join(java))

if __name__ == "__main__":
    main()

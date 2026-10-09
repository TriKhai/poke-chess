#!/usr/bin/env python3
"""Import complete PMD SpriteCollab forms into the compact PACR atlases.

Only explicitly reviewed forms are listed below. Portrait-only/incomplete forms
are deliberately excluded. The generated PNG stores normal and shiny frames;
the DAT files point into the same sheet.
"""
import hashlib, shutil, struct, sys, xml.etree.ElementTree as ET
from pathlib import Path
from PIL import Image

ACTIONS = ("Idle", "Walk", "Attack", "Hop", "Hurt", "Pose")
ATTACKS = ("Attack", "Strike", "Punch", "Kick", "Shoot", "SpAttack",
           "Ricochet", "Slam", "Charge", "Emit", "Rotate", "Shock")
MEGAS = {
    "6": "0006/0001",       # Mega Charizard X (Y is portrait-only)
    "65": "0065/0001",     # Mega Alakazam
    "94": "0094/0001",     # Mega Gengar
    "115": "0115/0001",    # Mega Kangaskhan
    "142": "0142/0001",    # Mega Aerodactyl
    "150": "0150/0002",    # Mega Mewtwo Y (X is portrait-only)
}
VARIANTS = {
    "0020-0001": "0020/0001", "0026-0001": "0026/0001",
    "0028-0001": "0028/0001", "0038-0001": "0038/0001",
    "0051-0001": "0051/0001", "0053-0001": "0053/0001",
    "0059-0001": "0059/0001", "0076-0001": "0076/0001",
    "0078-0001": "0078/0001", "0080-0001": "0080/0001",
    "0089-0001": "0089/0001", "0101-0001": "0101/0001",
    "0103-0001": "0103/0001", "0105-0001": "0105/0001",
    "0110-0001": "0110/0001", "0122-0001": "0122/0001",
    "0157-0001": "0157/0001", "0199-0001": "0199/0001",
    "0503-0001": "0503/0001", "0549-0001": "0549/0001",
    "0571-0001": "0571/0001", "0628-0001": "0628/0001",
    "0706-0001": "0706/0001", "0713-0001": "0713/0001",
    "0724-0001": "0724/0001",
}

def animation_info(folder):
    root = ET.parse(folder / "AnimData.xml").getroot()
    result = {}
    for anim in root.findall("./Anims/Anim"):
        name = anim.findtext("Name")
        copy = anim.findtext("CopyOf")
        if copy:
            result[name] = (copy, 0, 0, 0)
            continue
        fw = int(anim.findtext("FrameWidth")); fh = int(anim.findtext("FrameHeight"))
        count = len(anim.findall("./Durations/Duration"))
        result[name] = (name, fw, fh, count)
    return result

def resolve_action(folder, info, requested):
    names = ATTACKS if requested == "Attack" else (("Pose", "Hop", "Idle") if requested == "Pose" else (requested, "Idle"))
    for name in names:
        if name not in info: continue
        source, fw, fh, count = info[name]
        if fw == 0 and source in info: source, fw, fh, count = info[source]
        image = folder / (source + "-Anim.png")
        if image.exists() and fw > 0 and fh > 0 and count > 0: return source, fw, fh, count, image
    raise RuntimeError("missing %s in %s" % (requested, folder))

def sampled(count):
    return list(range(count))

def collect(folder):
    info = animation_info(folder)
    clips = []
    for requested in ACTIONS:
        source, fw, fh, count, image_path = resolve_action(folder, info, requested)
        sheet = Image.open(image_path).convert("RGBA")
        if sheet.width < fw*count or sheet.height < fh*8:
            raise RuntimeError("missing 8-direction animation: %s" % image_path)
        action_dirs = []
        for direction in range(8):
            frames = []
            for frame in sampled(count):
                cell = sheet.crop((frame*fw, direction*fh, (frame+1)*fw, (direction+1)*fh))
                box = cell.getbbox()
                if not box: raise RuntimeError("empty frame %s direction %d frame %d" % (image_path,direction,frame))
                frames.append((cell.crop(box), box[0], box[1], fw, fh))
            action_dirs.append(frames)
        clips.append(action_dirs)
    return clips

def pack(normal, shiny):
    all_frames = []
    for palette in (normal, shiny):
        if palette is None: continue
        for action in palette:
            for direction in action:
                for frame in direction: all_frames.append(frame)
    width = 1024; x = y = row_h = 0; placed = []; unique = []; positions = {}
    for frame in all_frames:
        image = frame[0]
        signature = (image.size, hashlib.sha256(image.tobytes()).digest())
        if signature in positions:
            px, py = positions[signature]
            placed.append((px, py, frame))
            continue
        if x + image.width > width: x = 0; y += row_h + 1; row_h = 0
        positions[signature] = (x, y)
        placed.append((x, y, frame)); unique.append((x, y, frame))
        x += image.width + 1; row_h = max(row_h, image.height)
    height = y + row_h
    atlas = Image.new("RGBA", (width, max(1, height)), (0,0,0,0))
    # Composite once: repeated semi-transparent frames must not gain opacity.
    for px, py, frame in unique: atlas.alpha_composite(frame[0], (px, py))
    cursor = 0; metadata = []
    for palette in (normal, shiny):
        if palette is None: metadata.append(None); continue
        out = []
        for action in palette:
            dirs = []
            for direction in action:
                frames = []
                for _ in direction:
                    px, py, frame = placed[cursor]; cursor += 1
                    im, ox, oy, sw, sh = frame
                    frames.append((px, py, im.width, im.height, ox, oy, sw, sh))
                dirs.append(frames)
            out.append(dirs)
        metadata.append(out)
    return atlas, metadata[0], metadata[1]

def write_dat(path, clips):
    if clips is None: return
    with path.open("wb") as stream:
        stream.write(b"PACR"); stream.write(struct.pack(">B", 6))
        for action in clips:
            for direction in action:
                stream.write(struct.pack(">H", len(direction)))
                for frame in direction: stream.write(struct.pack(">8HB", *(frame + (0,))))

def avatar_from(folder, normal, output):
    portrait = folder
    # sprite and portrait roots mirror the same form path.
    candidate = portrait / "Normal.png"
    if candidate.exists(): image = Image.open(candidate).convert("RGBA")
    else:
        frame = normal[0][0][0][0]
        image = frame.copy()
    box = image.getbbox()
    if box: image = image.crop(box)
    scale = min(30.0/max(1,image.width), 30.0/max(1,image.height))
    image = image.resize((max(1,int(image.width*scale)),max(1,int(image.height*scale))),Image.Resampling.NEAREST)
    canvas = Image.new("RGBA",(32,32),(0,0,0,0)); canvas.alpha_composite(image,((32-image.width)//2,32-image.height))
    canvas.save(output,optimize=True)

def make_one(sprite_root, portrait_root, rel, stem, raw_out, av_out):
    folder = sprite_root / Path(rel)
    if not (folder / "AnimData.xml").exists(): raise RuntimeError("missing animation: "+rel)
    normal = collect(folder)
    shiny_folder = folder / "0001"
    # A partial shiny sheet must not prevent importing a complete normal form.
    shiny = None
    if (shiny_folder / "AnimData.xml").exists():
        try: shiny = collect(shiny_folder)
        except (RuntimeError, ValueError, OSError, ET.ParseError): pass
    atlas, normal_meta, shiny_meta = pack(normal, shiny)
    atlas.save(raw_out/(stem+".png"),optimize=True)
    write_dat(raw_out/(stem+".dat"),normal_meta)
    if shiny_meta is not None: write_dat(raw_out/(stem+"-shiny.dat"),shiny_meta)
    portrait_folder = portrait_root / Path(rel)
    avatar_from(portrait_folder if (portrait_folder/"Normal.png").exists() else folder, normal, av_out/(stem+".png"))
    shiny_portrait = portrait_folder/"0001"/"Normal.png"
    if shiny is not None: avatar_from(portrait_folder/"0001" if shiny_portrait.exists() else shiny_folder, shiny, av_out/(stem+"-shiny.png"))

def main():
    if len(sys.argv) != 4: raise SystemExit("usage: import_spritecollab_forms.py SPRITECOLLAB_ROOT MEGA_RAW_OUT FORM_RAW_OUT")
    root, mega_raw, form_raw = map(Path,sys.argv[1:])
    mega_av = mega_raw.parent/"megaav"; form_av = form_raw.parent/"formav"
    for d in (mega_raw,mega_av,form_raw,form_av): d.mkdir(parents=True,exist_ok=True)
    for stem, rel in MEGAS.items(): make_one(root/"sprite",root/"portrait",rel,stem,mega_raw,mega_av)
    for stem, rel in VARIANTS.items(): make_one(root/"sprite",root/"portrait",rel,stem,form_raw,form_av)
    credits_dir=mega_raw.parent/"credits";credits_dir.mkdir(parents=True,exist_ok=True)
    shutil.copyfile(root/"LICENSE.md",credits_dir/"SpriteCollab-LICENSE.md")
    credits=["Source: https://github.com/PMDCollab/SpriteCollab", "Imported from the locally supplied SpriteCollab-master.", "Changes: packed/cropped animation frames into PACR atlases; resized portraits and generated sprite avatars when portraits were absent."]
    for stem,rel in list(MEGAS.items())+list(VARIANTS.items()):
        for kind in ("sprite","portrait"):
            for suffix in ("","/0001"):
                file=root/kind/(rel+suffix)/"credits.txt"
                if file.exists():credits.extend(["\n"+kind+" "+rel+suffix,file.read_text(encoding="utf-8")])
    (credits_dir/"SpriteCollab-forms.txt").write_text("\n".join(credits),encoding="utf-8")
    print("Imported",len(MEGAS),"complete Gen-1 Mega forms and",len(VARIANTS),"regional evolution forms")

if __name__ == "__main__": main()

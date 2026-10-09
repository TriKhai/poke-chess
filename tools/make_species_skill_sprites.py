#!/usr/bin/env python3
"""Build one original-asset ability strip for every battle Pokemon."""
import csv
import hashlib
import io
import os
import re
import sys
from pathlib import Path
from PIL import Image

SIZE = 32
FRAMES = 8

def norm(value):
    return re.sub(r"[^A-Z0-9]+", "_", value.upper()).strip("_")

def fit(image):
    image = image.convert("RGBA")
    bounds = image.getchannel("A").getbbox()
    if bounds:
        image = image.crop(bounds)
    scale = min(float(SIZE) / image.width, float(SIZE) / image.height)
    width = max(1, int(image.width * scale))
    height = max(1, int(image.height * scale))
    image = image.resize((width, height), Image.Resampling.NEAREST)
    output = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    output.alpha_composite(image, ((SIZE - width) // 2, (SIZE - height) // 2))
    return output

def main():
    if len(sys.argv) != 6:
        raise SystemExit("usage: make_species_skill_sprites.py ABILITIES_TPS pokemons-data.csv PORTRAITS_ROOT OUTPUT_DIR JAVA_OUT")
    root, csv_path, portraits_root, output_dir, java_out = sys.argv[1:]
    os.makedirs(output_dir, exist_ok=True)
    for old in Path(output_dir).glob("*.png"):
        old.unlink()
    dirs = {p.name: p for p in Path(root).iterdir() if p.is_dir()}
    by_dex = {}
    for row in csv.DictReader(open(csv_path, encoding="utf-8-sig")):
        if row["Index"].isdigit():
            dex = int(row["Index"])
            if 1 <= dex <= 1025 and dex not in by_dex:
                by_dex[dex] = row
    core = [by_dex[d] for d in range(1, 387) if d in by_dex]
    available = [by_dex[d] for d in range(387, 1026) if d in by_dex and
                 (Path(portraits_root) / ("%04d" % d) / "Normal.png").exists()]
    gen4 = [r for r in available if int(r["Index"]) <= 493]
    later_legend = [r for r in available if int(r["Index"]) >= 494 and r["Category"] == "LEGENDARY"]
    later_regular = [r for r in available if int(r["Index"]) >= 494 and r["Category"] != "LEGENDARY"]
    # Mirrors Data initialization: core, all Gen 4, later Legendaries, then other Gen 5-9.
    rows = core + gen4 + later_legend + later_regular
    fallback_by_type = {
        "FIRE": "FIRE_BLAST", "WATER": "HYDRO_PUMP", "ELECTRIC": "DISCHARGE",
        "GRASS": "MAGICAL_LEAF", "FLORA": "MAGICAL_LEAF", "ICE": "BLIZZARD",
        "POISON": "ACID_SPRAY", "PSYCHIC": "PSYCHIC", "GHOST": "SHADOW_BALL",
        "DRAGON": "DRAGON_PULSE", "FIGHTING": "CLOSE_COMBAT", "FLYING": "AIR_SLASH",
        "ROCK": "ANCIENT_POWER", "GROUND": "LANDS_WRATH", "STEEL": "FLASH_CANNON",
        "BUG": "BUG_BUZZ", "DARK": "DARK_PULSE", "FAIRY": "FAIRY_WIND",
        "NORMAL": "HYPER_BEAM_CHARGE", "SOUND": "HYPER_VOICE"
    }
    exact = 0
    shared = {}
    mapping = []
    for species, row in enumerate(rows):
        ability = norm(row["Ability"])
        chosen = ability
        if chosen not in dirs:
            chosen = fallback_by_type.get(norm(row["Type 1"]), "BALL")
        if chosen not in dirs:
            chosen = "BALL"
        else:
            exact += int(chosen == ability)
        files = sorted(dirs[chosen].rglob("*.png"))
        if not files:
            raise RuntimeError("missing frames for " + chosen)
        strip = Image.new("RGBA", (SIZE * FRAMES, SIZE), (0, 0, 0, 0))
        for frame in range(FRAMES):
            source = files[(frame * (len(files) - 1)) // (FRAMES - 1)]
            strip.alpha_composite(fit(Image.open(source)), (frame * SIZE, 0))
        digest = hashlib.sha256(strip.tobytes()).digest()
        effect = shared.get(digest)
        if effect is None:
            effect = species
            shared[digest] = effect
            strip.save(os.path.join(output_dir, "%d.png" % effect), optimize=True)
        mapping.append(effect)
    code = """package pac;

/** Generated mapping from species id to a deduplicated skill-effect strip. */
public final class SkillFxData {
    private SkillFxData(){}
    public static final short[] FX={%s};
    public static int effect(int sp){return sp>=0&&sp<FX.length?FX[sp]:-1;}
}
""" % ",".join(str(value) for value in mapping)
    Path(java_out).write_text(code, encoding="utf-8", newline="\n")
    print("generated %d species mappings -> %d unique strips; %d use exact original ability folders" %
          (len(rows), len(shared), exact))

if __name__ == "__main__":
    main()

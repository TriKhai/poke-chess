#!/usr/bin/env python3
"""Build one original-asset ability strip for every Gen 1-3 Pokemon."""
import csv
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
    if len(sys.argv) != 4:
        raise SystemExit("usage: make_species_skill_sprites.py ABILITIES_TPS pokemons-data.csv OUTPUT_DIR")
    root, csv_path, output_dir = sys.argv[1:]
    os.makedirs(output_dir, exist_ok=True)
    dirs = {p.name: p for p in Path(root).iterdir() if p.is_dir()}
    rows = [r for r in csv.DictReader(open(csv_path, encoding="utf-8"))
            if r["Index"].isdigit() and 1 <= int(r["Index"]) <= 386]
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
    for row in rows:
        species = int(row["Index"]) - 1
        ability = norm(row["Ability"])
        chosen = ability
        if chosen not in dirs:
            chosen = fallback_by_type.get(norm(row["Type 1"]), "BALL")
        if chosen not in dirs:
            chosen = "BALL"
        else:
            exact += int(chosen == ability)
        files = sorted(dirs[chosen].glob("*.png"))
        if not files:
            raise RuntimeError("missing frames for " + chosen)
        strip = Image.new("RGBA", (SIZE * FRAMES, SIZE), (0, 0, 0, 0))
        for frame in range(FRAMES):
            source = files[(frame * (len(files) - 1)) // (FRAMES - 1)]
            strip.alpha_composite(fit(Image.open(source)), (frame * SIZE, 0))
        strip.save(os.path.join(output_dir, "%d.png" % species), optimize=True)
    print("generated %d species strips; %d use exact original ability folders" % (len(rows), exact))

if __name__ == "__main__":
    main()

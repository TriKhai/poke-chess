#!/usr/bin/env python3
"""Generate collection-only Gen 4-9 metadata and 32px portraits from the reference source."""
import csv
import os
import shutil
import sys
from pathlib import Path
from PIL import Image

TYPE_IDS = {
    name: i for i, name in enumerate((
        "NORMAL FIRE WATER GRASS ELECTRIC ROCK PSYCHIC FIGHTING FLYING DRAGON "
        "GHOST BUG POISON GROUND ICE DARK STEEL FAIRY AMORPHOUS AQUATIC "
        "ARTIFICIAL BABY FIELD FLORA FOSSIL GOURMET HUMAN LIGHT MONSTER SOUND WILD"
    ).split())
}

SPECIAL = {
    "MIME_JR": "Mime Jr.", "MR_RIME": "Mr. Rime", "PORYGON_Z": "Porygon-Z",
    "HO_OH": "Ho-Oh", "TYPE_NULL": "Type: Null", "SIRFETCHD": "Sirfetch'd",
    "FARFETCHD": "Farfetch'd", "FLABEBE": "Flabébé", "WO_CHIEN": "Wo-Chien",
    "CHIEN_PAO": "Chien-Pao", "TING_LU": "Ting-Lu", "CHI_YU": "Chi-Yu"
}

def display_name(raw):
    if raw in SPECIAL:
        return SPECIAL[raw]
    return " ".join(word.capitalize() for word in raw.split("_"))

def generation(dex):
    limits = (151, 251, 386, 493, 649, 721, 809, 905, 1025)
    for i, end in enumerate(limits):
        if dex <= end:
            return i + 1
    return 9

def java_string(value):
    return '"' + value.replace("\\", "\\\\").replace('"', '\\"') + '"'

def main():
    if len(sys.argv) != 5:
        raise SystemExit("usage: generate_collection_dex.py CSV PORTRAITS_ROOT JAVA_OUT RES_OUT")
    csv_path, portrait_root, java_out, res_out = map(Path, sys.argv[1:])
    by_dex = {}
    with csv_path.open(encoding="utf-8-sig", newline="") as stream:
        for row in csv.DictReader(stream):
            idx = row["Index"]
            if not (len(idx) == 4 and idx.isdigit()):
                continue
            dex = int(idx)
            if 387 <= dex <= 1025 and dex not in by_dex:
                portrait = portrait_root / idx / "Normal.png"
                if portrait.exists():
                    by_dex[dex] = row

    rows = [(dex, by_dex[dex]) for dex in sorted(by_dex)]
    java_out.parent.mkdir(parents=True, exist_ok=True)
    res_out.mkdir(parents=True, exist_ok=True)
    for old in res_out.glob("*.png"):
        old.unlink()

    for dex, row in rows:
        source = portrait_root / ("%04d" % dex) / "Normal.png"
        with Image.open(source) as image:
            image.convert("RGBA").resize((32, 32), Image.Resampling.NEAREST).save(
                res_out / ("%d.png" % dex), optimize=True
            )

    def values(expr):
        return ",".join(str(expr(dex, row)) for dex, row in rows)

    names = ",".join(java_string(display_name(row["Name"])) for _, row in rows)
    categories = ",".join(java_string(row["Category"].replace("_", " ").title()) for _, row in rows)
    code = """package pac;

/** Generated collection-only National Dex entries. Never used by shop, AI or Battle. */
public final class CollectionDex {
    private CollectionDex(){}
    public static final int COUNT=%d;
    public static final int[] DEX={%s};
    public static final byte[] GEN={%s};
    public static final byte[] T1={%s};
    public static final byte[] T2={%s};
    public static final String[] NAME={%s};
    public static final String[] CATEGORY={%s};
    public static int firstOfGen(int gen){for(int i=0;i<COUNT;i++)if(GEN[i]==gen)return i;return -1;}
    public static int countGen(int gen){int n=0;for(int i=0;i<COUNT;i++)if(GEN[i]==gen)n++;return n;}
}
""" % (
        len(rows),
        values(lambda dex, row: dex),
        values(lambda dex, row: generation(dex)),
        values(lambda dex, row: TYPE_IDS.get(row["Type 1"], 0)),
        values(lambda dex, row: TYPE_IDS.get(row["Type 2"], -1) if row["Type 2"] else -1),
        names, categories
    )
    java_out.write_text(code, encoding="utf-8", newline="\n")
    counts = {g: sum(1 for dex, _ in rows if generation(dex) == g) for g in range(4, 10)}
    print("generated", len(rows), "collection-only entries", counts)

if __name__ == "__main__":
    main()

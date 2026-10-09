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
COST = {"COMMON":1,"UNCOMMON":2,"RARE":3,"EPIC":4,"ULTRA":5,
        "UNIQUE":5,"LEGENDARY":5,"SPECIAL":2,"HATCH":2}
CATEGORY_ID = {"COMMON":0,"UNCOMMON":1,"RARE":2,"EPIC":3,"ULTRA":4,
               "UNIQUE":5,"LEGENDARY":6,"SPECIAL":7,"HATCH":1}

def ability_id(row):
    types={row["Type 1"],row["Type 2"],row["Type 3"],row["Type 4"]}
    value=row["Ability"]
    if any(x in value for x in ("HEAL","RECOVER","WISH","SYNTHESIS","LIFE_DEW")): return 1
    if any(x in value for x in ("EXPLOSION","BLAST","ERUPTION","QUAKE","SURF")): return 2
    if any(x in value for x in ("DANCE","SONG","SCENT","HOWL","CHEER")): return 3
    if any(x in value for x in ("STUN","PARAL","SLEEP","HYPNO","SPORE")): return 4
    if "ELECTRIC" in types: return 5
    if any(x in types for x in ("ROCK","STEEL","GROUND","FOSSIL")): return 6
    if any(x in value for x in ("DRAIN","ABSORB","LEECH","DREAM_EATER")): return 7
    if row["Category"] in ("LEGENDARY","UNIQUE") or int(row["Tier"])>=3: return 8
    return 0

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
    all_rows=[]
    with csv_path.open(encoding="utf-8-sig", newline="") as stream:
        for row in csv.DictReader(stream):
            idx = row["Index"]
            if not (len(idx) == 4 and idx.isdigit()):
                continue
            all_rows.append(row)
            dex = int(idx)
            if 387 <= dex <= 1025 and dex not in by_dex:
                portrait = portrait_root / idx / "Normal.png"
                if portrait.exists():
                    by_dex[dex] = row

    rows = [(dex, by_dex[dex]) for dex in sorted(by_dex)]
    family_dex={}
    for row in all_rows:
        family_dex.setdefault(row["Family"],int(row["Index"]))
    # Tier also represents rarity/form rank upstream. Normalize each actual
    # family so singleton legendaries are stage 1 instead of showing 3 dots.
    family_rows={}
    for row in all_rows:
        family_rows.setdefault(row["Family"],[]).append(row)
    evolution_stage={}
    for members in family_rows.values():
        first=min(int(member["Tier"]) for member in members)
        for member in members:
            evolution_stage[int(member["Index"])]=max(1,min(3,int(member["Tier"])-first+1))
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
    battle = """package pac;

/** Generated gameplay data for CollectionDex entries. Split out for the CLDC 64K method limit. */
public final class LaterBattleData {
    private LaterBattleData(){}
    public static final byte[] COST={%s};
    public static final byte[] CATEGORY={%s};
    public static final short[] HP={%s},ATK={%s},DEF={%s},SDEF={%s},SPEED={%s},MANA={%s};
    public static final byte[] RANGE={%s},TIER={%s},ABILITY={%s};
    public static final short[] FAMILY_DEX={%s};
    public static final String[] SKILL={%s};
}
""" % (
        values(lambda dex,row:COST.get(row["Category"],2)),
        values(lambda dex,row:CATEGORY_ID.get(row["Category"],1)),
        values(lambda dex,row:max(30,int(row["HP"]))),
        values(lambda dex,row:max(2,int(row["Attack"]))),
        values(lambda dex,row:max(0,int(row["Defense"]))),
        values(lambda dex,row:max(0,int(row["Special Defense"]))),
        values(lambda dex,row:int(row["Speed"])),
        values(lambda dex,row:max(30,min(120,int(row["Max PP"])))),
        values(lambda dex,row:max(1,min(4,int(row["Attack Range"])))),
        values(lambda dex,row:evolution_stage.get(dex,1)),
        values(lambda dex,row:ability_id(row)),
        values(lambda dex,row:family_dex.get(row["Family"],dex)),
        ",".join(java_string(display_name(row["Ability"])) for _,row in rows)
    )
    (java_out.parent / "LaterBattleData.java").write_text(battle,encoding="utf-8",newline="\n")
    counts = {g: sum(1 for dex, _ in rows if generation(dex) == g) for g in range(4, 10)}
    print("generated", len(rows), "collection-only entries", counts)

if __name__ == "__main__":
    main()

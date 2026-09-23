#!/usr/bin/env python3
"""Generate the CLDC-safe 386-species roster from the original app source."""
import csv
import os
import re
import sys

TYPES = ["NORMAL", "FIRE", "WATER", "GRASS", "ELECTRIC", "ROCK", "PSYCHIC",
         "FIGHTING", "FLYING", "DRAGON", "GHOST", "BUG", "POISON", "GROUND",
         "ICE", "DARK", "STEEL", "FAIRY", "AMORPHOUS", "AQUATIC", "ARTIFICIAL",
         "BABY", "FIELD", "FLORA", "FOSSIL", "GOURMET", "HUMAN", "LIGHT",
         "MONSTER", "SOUND", "WILD"]
COLORS = [0xA8A878,0xF08030,0x6890F0,0x78C850,0xF8D030,0xB8A038,0xF85888,
          0xC03028,0xA890F0,0x7038F8,0x705898,0xA8B820,0xA040A0,0xE0C068,
          0x98D8D8,0x705848,0xB8B8D0,0xEE99AC,0x806080,0x4098A8,0x8899AA,
          0xFFB0C8,0xB09070,0x70B850,0x9C8060,0xE8A050,0xC09070,0xF8E870,
          0x907858,0xC080D0,0x789060]
SHORT = {"ELECTRIC":"ELE", "PSYCHIC":"PSY", "FIGHTING":"FIG", "FLYING":"FLY",
         "DRAGON":"DRA", "GHOST":"GHO", "NORMAL":"NOR", "WATER":"WAT",
         "GRASS":"GRA", "GROUND":"GRO", "POISON":"POI", "ARTIFICIAL":"ART",
         "AMORPHOUS":"AMO", "AQUATIC":"AQU", "MONSTER":"MON", "GOURMET":"GOU"}
COST = {"COMMON":1,"UNCOMMON":2,"RARE":3,"EPIC":4,"ULTRA":5,
        "UNIQUE":5,"LEGENDARY":5,"SPECIAL":2}
CATEGORY = {"COMMON":0,"UNCOMMON":1,"RARE":2,"EPIC":3,"ULTRA":4,
            "UNIQUE":5,"LEGENDARY":6,"SPECIAL":7}

def display(n):
    special={"MR_MIME":"Mr. Mime","MIME_JR":"Mime Jr.","FARFETCH_D":"Farfetch'd",
             "HO_OH":"Ho-Oh","NIDORAN_F":"Nidoran F","NIDORAN_M":"Nidoran M"}
    return special.get(n, " ".join(x.capitalize() for x in n.split("_")))

def parse_evolutions(ts):
    text=open(ts,encoding="utf-8").read()
    class_to_pkm={c:p for p,c in re.findall(r"\[Pkm\.([A-Z0-9_]+)\]:\s*([A-Za-z0-9_]+)",text)}
    blocks={}
    marks=list(re.finditer(r"export class\s+([A-Za-z0-9_]+)\s+extends Pokemon\s*\{",text))
    for i,m in enumerate(marks):
        blocks[m.group(1)]=text[m.end():marks[i+1].start() if i+1<len(marks) else len(text)]
    out={}
    for cls,pkm in class_to_pkm.items():
        body=blocks.get(cls,"")
        one=re.search(r"\bevolution\s*=\s*Pkm\.([A-Z0-9_]+)",body)
        many=re.search(r"\bevolutions\s*=\s*\[([^]]+)\]",body,re.S)
        if one: out[pkm]=one.group(1)
        elif many:
            vals=re.findall(r"Pkm\.([A-Z0-9_]+)",many.group(1))
            if vals: out[pkm]=vals[0]
    return out

def ability(row):
    types={row["Type 1"],row["Type 2"],row["Type 3"],row["Type 4"]}
    a=row["Ability"]
    if any(x in a for x in ("HEAL","RECOVER","WISH","SYNTHESIS","LIFE_DEW")): return 1
    if any(x in a for x in ("EXPLOSION","BLAST","ERUPTION","QUAKE","SURF")): return 2
    if any(x in a for x in ("DANCE","SONG","SCENT","HOWL","CHEER")): return 3
    if any(x in a for x in ("STUN","PARAL","SLEEP","HYPNO","SPORE")): return 4
    if "ELECTRIC" in types: return 5
    if any(x in types for x in ("ROCK","STEEL","GROUND","FOSSIL")): return 6
    if any(x in a for x in ("DRAIN","ABSORB","LEECH","DREAM_EATER")): return 7
    if row["Category"] in ("LEGENDARY","UNIQUE") or int(row["Tier"])>=3: return 8
    return 0

def main():
    if len(sys.argv)!=4:
        raise SystemExit("usage: generate_gen123_roster.py pokemons-data.csv pokemon.ts Data.java")
    csv_path,ts_path,out_path=sys.argv[1:]
    rows=[r for r in csv.DictReader(open(csv_path,encoding="utf-8"))
          if r["Index"].isdigit() and 1<=int(r["Index"])<=386]
    rows.sort(key=lambda r:int(r["Index"]))
    if len(rows)!=386: raise RuntimeError("expected 386 standard Pokemon")
    by_name={r["Name"]:int(r["Index"])-1 for r in rows}
    ev=parse_evolutions(ts_path)
    links=[]
    for src,dst in ev.items():
        if src in by_name and dst in by_name: links.append((by_name[src],by_name[dst]))
    targets={b for a,b in links}
    h=[]
    h.append('''package pac;

/** Generated Gen 1-3 roster. Source: app/models/precomputed/pokemons-data.csv. */
public final class Data {
    private Data() {}
''')
    consts=[]
    for i,t in enumerate(TYPES): consts.append("T_"+("ELEC" if t=="ELECTRIC" else "PSY" if t=="PSYCHIC" else "FIGHT" if t=="FIGHTING" else "FLY" if t=="FLYING" else t)+" = "+str(i))
    h.append("    public static final int "+", ".join(consts)+";\n")
    h.append("    public static final int NT = %d;\n"%len(TYPES))
    h.append("    public static final String[] TNAME = {"+",".join('"'+t.title()+'"' for t in TYPES)+"};\n")
    h.append("    public static final String[] TSHORT = {"+",".join('"'+SHORT.get(t,t[:3])+'"' for t in TYPES)+"};\n")
    h.append("    public static final int[] TCOL = {"+",".join("0x%06X"%c for c in COLORS)+"};\n")
    syn=["HP +12/25/45%","ATK +15/35/60%","Start mana +20/40/70","Regen 2/4/7% HP/s","Atk speed +15/30/50%","DEF +2/5/9","Skill dmg +30/60/120%","Crit 15/30/50%","Dodge 10/20/35%","ATK+HP +10/25/50%","Lifesteal 10/20/35%"]
    syn += ["HP+ATK +8/16/28%"]*(len(TYPES)-len(syn))
    h.append("    public static final String[] SYN_TEXT = {"+",".join('"'+x+'"' for x in syn)+"};\n")
    h.append('''    public static final String[] ABIL_NAME = {"Power Strike","Heal Pulse","Blast","Rally","Paralyze","Chain Bolt","Harden","Drain","Hyper Beam"};
    public static final String[] ABIL_DESC = {"Hits target for 250% ATK.","Heals the weakest ally.","Blast around target, 180% ATK.","Nearby allies gain +30% ATK.","Stuns target 1.2s, 120% ATK.","Zaps 3 foes for 140% ATK.","Gains a shield of 35% max HP.","Hits 200% ATK and heals self.","Massive 380% ATK beam."};
    public static final String[] ZNAME = {"Meadow","Lake","Cave","Haunted"};
    public static final String[] RARITY = {"","Common","Uncommon","Rare","Epic","Legend"};
    public static final String[] CATEGORY_NAME = {"Common","Uncommon","Rare","Epic","Ultra","Unique","Legendary","Special"};
    public static final int[] CATCH_W = {0,460,360,270,190,130};
    public static final int[] WILD_W = {0,20,12,6,3,1};
    public static final int[] POOL_COPIES = {0,22,18,14,10,6};
    public static final int MAX_LEVEL=9;
    public static final int[] XP_NEED={0,2,2,6,10,20,32,50,66,9999};
    public static final int[][] ODDS={{100,0,0,0,0},{100,0,0,0,0},{100,0,0,0,0},{70,30,0,0,0},{55,35,10,0,0},{40,35,20,5,0},{30,35,25,10,0},{20,30,30,15,5},{15,25,30,20,10},{10,20,30,25,15}};
    public static final int MAX=386;
    public static int N=0;
    public static final String[] name=new String[MAX],skillName=new String[MAX];
    public static final int[] t1=new int[MAX],t2=new int[MAX],cost=new int[MAX],category=new int[MAX],hp=new int[MAX],atk=new int[MAX],def=new int[MAX],speDef=new int[MAX],speed=new int[MAX],range=new int[MAX],cd=new int[MAX],mana=new int[MAX],abil=new int[MAX],evo=new int[MAX],fam=new int[MAX],tier=new int[MAX],stage=new int[MAX],zones=new int[MAX];
    public static int MEWTWO=149;
    static int add(String nm,String sk,int a,int b,int c,int cat,int h,int at,int df,int sd,int spd,int rg,int cool,int mn,int ab,int st,int zmask){int i=N++;name[i]=nm;skillName[i]=sk;t1[i]=a;t2[i]=b;cost[i]=c;category[i]=cat;hp[i]=h;atk[i]=at;def[i]=df;speDef[i]=sd;speed[i]=spd;range[i]=rg;cd[i]=cool;mana[i]=mn;abil[i]=ab;evo[i]=-1;fam[i]=i;tier[i]=1;stage[i]=st;zones[i]=zmask;return i;}
    static void link(int from,int to){evo[from]=to;fam[to]=fam[from];tier[to]=tier[from]+1;cost[to]=cost[from];zones[to]=0;}
    static { init1(); init2(); init3(); initLinks(); }
''')
    chunks=[rows[:130],rows[130:260],rows[260:]]
    for ci,chunk in enumerate(chunks,1):
        h.append("    private static void init%d(){\n"%ci)
        for r in chunk:
            idx=int(r["Index"])-1
            # Kecleon is intentionally typeless in the online source because Color
            # Change assigns it at runtime; Normal is its safe offline baseline.
            ty=[r["Type 1"] or "NORMAL",r["Type 2"]]
            a=TYPES.index(ty[0]); b=TYPES.index(ty[1]) if ty[1] else -1
            cat=COST[r["Category"]]
            rarity=CATEGORY[r["Category"]]
            hp=max(30,int(r["HP"])); at=max(2,int(r["Attack"])); df=max(0,int(r["Defense"])); sd=max(0,int(r["Special Defense"]))
            rg=max(1,min(4,int(r["Attack Range"]))); speed=int(r["Speed"])
            cool=max(2,min(7,8-speed//14)); mn=max(30,min(120,int(r["Max PP"])))
            st=max(1,min(3,int(r["Tier"])))
            p=ty[0]
            z=2 if p in ("WATER","AQUATIC","ICE") else 4 if p in ("ROCK","GROUND","STEEL","FOSSIL") else 8 if p in ("GHOST","DARK","PSYCHIC","AMORPHOUS") else 1
            sk=display(r["Ability"])
            h.append('        add("%s","%s",%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d); // #%03d\n'%(display(r["Name"]),sk,a,b,cat,rarity,hp,at,df,sd,speed,rg,cool,mn,ability(r),st,z,idx+1))
        h.append("    }\n")
    h.append("    private static void initLinks(){\n")
    for a,b in links: h.append("        link(%d,%d);\n"%(a,b))
    h.append('''    }
    public static int copies(int sp){int t=tier[sp];return t==1?1:(t==2?3:9);}
    public static int sellValue(int sp){int v=cost[sp]*copies(sp);if(tier[sp]>1)v--;return v;}
    public static boolean isBase(int sp){return fam[sp]==sp;}
    public static boolean famHasType(int sp,int type){int s=fam[sp];while(s>=0){if(t1[s]==type||t2[s]==type)return true;s=evo[s];}return false;}
    public static int countFamilies(){int n=0;for(int i=0;i<N;i++)if(fam[i]==i)n++;return n;}
    public static int synLevel(int count){if(count>=6)return 3;if(count>=4)return 2;if(count>=2)return 1;return 0;}
    public static int visualWidth(int sp){return VisualSize.W[sp];}
    public static int visualHeight(int sp){return VisualSize.H[sp];}
    public static int visualSize(int sp){return Math.max(VisualSize.W[sp],VisualSize.H[sp]);}
    public static int pickWild(int zone,Rng r){int total=0,bit=1<<zone;for(int i=0;i<N;i++)if((zones[i]&bit)!=0)total+=WILD_W[cost[i]];if(total<=0)return-1;int roll=r.nextInt(total);for(int i=0;i<N;i++)if((zones[i]&bit)!=0){roll-=WILD_W[cost[i]];if(roll<0)return i;}return-1;}
}
''')
    os.makedirs(os.path.dirname(out_path),exist_ok=True)
    open(out_path,"w",encoding="utf-8",newline="\n").write("".join(h))
    print("generated",len(rows),"species and",len(links),"evolution links ->",out_path)

if __name__=="__main__": main()

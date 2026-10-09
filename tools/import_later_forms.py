"""Curated Gen 4-9 gameplay forms; never import alternate art/cutscenes.

Generated catalog IDs append to the existing 1..79 save IDs. Re-running with
new resources retains previously assigned IDs via catalog.json.
"""
import csv
import json
import sys
from pathlib import Path
from import_spritecollab_forms import make_one

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'only_tham_khao/SpriteCollab-master'
OUT = ROOT / 'outputs/all-gen-audit'
CURATED = {
19:'Alola',27:'Alola',37:'Alola',50:'Alola',52:'Alola Galar',58:'Hisui',74:'Alola',75:'Alola',77:'Galar',79:'Galar',83:'Galar',88:'Alola',100:'Hisui',128:'Paldea Paldea_Blaze Paldea_Aqua',
412:'Sand Trash',413:'Sand Trash',421:'Sunshine',422:'East',423:'East',
479:'Heat Wash Frost Fan Mow',483:'Origin',484:'Origin',492:'Sky',
550:'Blue White',554:'Galar',555:'Zen Galar Galar_Zen',562:'Galar',570:'Hisui',
585:'Summer Autumn Winter',586:'Summer Autumn Winter',641:'Therian',642:'Therian',645:'Therian',
646:'Black White',647:'Resolute',648:'Pirouette',649:'Douse Shock Burn Chill',
666:'Icy_Snow Polar Tundra Continental Garden Elegant Modern Marine Archipelago High_Plains Sandstorm River Monsoon Savannah Sun Ocean Jungle Fancy Pokeball',
669:'Yellow Orange Blue White',670:'Yellow Orange Blue White Eternal',671:'Yellow Orange Blue White',
676:'Heart Star Diamond Debutante Matron Dandy La_Reine Kabuki Pharaoh',681:'Blade',705:'Hisui',
710:'Small Large Super',711:'Small Large Super',716:'Neutral',
741:'Pom_Pom Pa_U Sensu',745:'Midnight Dusk',746:'School',774:'Red Orange Yellow Green Blue Indigo Violet',778:'Busted',801:'Original',
845:'Gulping Gorging',849:'Lowkey',875:'Noice',877:'Hangry',888:'Crowned_Sword',889:'Crowned_Shield',890:'Eternamax',
892:'Rapid_Strike',893:'Dada',898:'Ice_Rider Shadow_Rider',901:'Bloodmoon',905:'Therian',
925:'Three',931:'Blue Yellow White',964:'Hero',978:'Stretchy Droopy',982:'Three',999:'Roaming',1007:'Limited',1008:'Low_Power',
1017:'Wellspring Hearthflame Cornerstone Teal_Mask Wellspring_Mask Hearthflame_Mask Cornerstone_Mask',1024:'Terastal Stellar',
}
TYPES = 'Bug Dark Dragon Electric Fighting Fire Flying Ghost Grass Ground Ice Poison Psychic Rock Steel Water Fairy'.split()
CURATED[493] = CURATED[773] = ' '.join(TYPES)
REGIONAL = {'Galar','Hisui','Alola','Paldea','Paldea_Blaze','Paldea_Aqua'}
BATTLE = {'Sunshine','Zen','Galar_Zen','Therian','Black','White','Pirouette','Eternal','Blade','School','Crowned_Sword','Crowned_Shield','Eternamax','Ice_Rider','Shadow_Rider','Hero','Terastal','Stellar','Origin'}
# No palette/garment animation is a distinct combat form of Alcremie.
def eligible(row):
    dex, name = row['dex'], row['form']
    return len(row['path'].split('/')) == 2 and (row['generation'] >= 4 or dex==80 or dex in CURATED) and (
        name == 'Mega' or name in CURATED.get(dex,'').split() or
        dex == 869 and ('Sweet' in name or name == 'Gigantamax'))

def main():
    inventory = json.loads((OUT / 'forms.json').read_text(encoding='utf-8'))
    selected = [r for r in inventory if eligible(r)]
    (OUT/'reviewed-forms.json').write_text(json.dumps(selected,ensure_ascii=False,indent=2),encoding='utf-8')
    metadata=list(csv.DictReader((OUT/'reference/pokemon_species.csv').open(encoding='utf-8')))
    (OUT/'species.json').write_text(json.dumps(metadata,ensure_ascii=False,indent=2),encoding='utf-8')
    old = json.loads((OUT / 'catalog.json').read_text(encoding='utf-8')) if (OUT / 'catalog.json').exists() else []
    ids = {r['path']:r['id'] for r in old}; next_id = max([79]+list(ids.values()))+1
    species = {int(r['id']):r for r in csv.DictReader((OUT/'reference/pokemon_species.csv').open(encoding='utf-8'))}
    poke = list(csv.DictReader((OUT/'reference/pokemon.csv').open(encoding='utf-8')))
    poke_by_name = {r['identifier']:int(r['id']) for r in poke}
    poke_types = {}
    type_names = {int(r['id']):r['identifier'] for r in csv.DictReader((OUT/'reference/types.csv').open(encoding='utf-8'))}
    game_types = {'normal':0,'fire':1,'water':2,'grass':3,'electric':4,'rock':5,'psychic':6,'fighting':7,'flying':8,'dragon':9,'ghost':10,'bug':11,'poison':12,'ground':13,'ice':14,'dark':15,'steel':16,'fairy':17}
    for r in csv.DictReader((OUT/'reference/pokemon_types.csv').open(encoding='utf-8')):
        poke_types.setdefault(int(r['pokemon_id']),{})[int(r['slot'])]=game_types.get(type_names[int(r['type_id'])],-1)
    catalog=[]; megas=[]; credits=[]
    for row in selected:
        if not row['complete']: continue
        dex,name,rel=row['dex'],row['form'],row['path']
        mega=name=='Mega';stem=str(dex) if mega else rel.replace('/','-')
        for directory in ('res/megaraw','res/megaav','res/formraw','res/formav'): (ROOT/directory).mkdir(parents=True,exist_ok=True)
        if '--catalog-only' not in sys.argv and not ('--missing-only' in sys.argv and (ROOT/('res/megaraw' if mega else 'res/formraw')/(stem+'.dat')).exists()):
            make_one(SOURCE/'sprite',SOURCE/'portrait',rel,stem,ROOT/('res/megaraw' if mega else 'res/formraw'),ROOT/('res/megaav' if mega else 'res/formav'))
        suffix={'Galar':'galar','Hisui':'hisui','Crowned_Sword':'crowned','Crowned_Shield':'crowned','Eternamax':'eternamax','Lowkey':'low-key','Pirouette':'pirouette','Sky':'sky','Bloodmoon':'bloodmoon','Rapid_Strike':'rapid-strike','Three':'family-of-three' if dex==925 else 'three-segment'}.get(name,name.lower().replace('_','-'))
        pid=poke_by_name.get(species[dex]['identifier']+'-'+('mega' if mega else suffix),dex)
        typ=poke_types.get(pid,poke_types.get(dex,{}));t1=typ.get(1,0);t2=typ.get(2,-1)
        if dex in (493,773) and name in TYPES:t1=game_types[name.lower()];t2=-1
        if dex==479:t1=4;t2={'Heat':1,'Wash':2,'Frost':14,'Fan':8,'Mow':3}[name]
        if dex==555 and name=='Galar':t1=14;t2=-1
        if dex==128:t1=7;t2={'Paldea':-1,'Paldea_Blaze':1,'Paldea_Aqua':2}[name]
        if dex==649:t1=11;t2=16 # Drives change Techno Blast, not Genesect's type.
        if dex==1017:t1=3;t2={'Wellspring':2,'Hearthflame':1,'Cornerstone':5}.get(name.replace('_Mask',''),-1)
        record=dict(row,key=stem,type1=t1,type2=t2)
        if mega:megas.append(record)
        else:
            if rel not in ids:ids[rel]=next_id;next_id+=1
            record['id']=ids[rel];record['regional']=name in REGIONAL or dex==550 and name=='White'
            record['upgrade']=not record['regional'] and (name in BATTLE or name=='Gigantamax' or dex==1017 and name.endswith('_Mask'))
            record['choice']=not record['upgrade'] and (record['regional'] or bool(species[dex]['evolves_from_species_id']))
            catalog.append(record)
        for kind in ('sprite','portrait'):
            for suffix in ('','/0001'):
                file=SOURCE/kind/(rel+suffix)/'credits.txt'
                if file.exists():credits.append(kind+' '+rel+suffix+'\n'+file.read_text(encoding='utf-8'))
    catalog.sort(key=lambda r:r['id'])
    (OUT/'catalog.json').write_text(json.dumps(catalog,ensure_ascii=False,indent=2),encoding='utf-8')
    (OUT/'later-megas.json').write_text(json.dumps(megas,ensure_ascii=False,indent=2),encoding='utf-8')
    (ROOT/'res/credits/Gen4-9-forms.txt').write_text('Source: https://github.com/PMDCollab/SpriteCollab\nChanges: PACR packing; resized or sprite-derived avatars.\n\n'+'\n\n'.join(credits),encoding='utf-8')
    def arr(field):return ','.join(json.dumps(r[field],ensure_ascii=False) if isinstance(r[field],str) else str(int(r[field])) for r in catalog)
    java='''package pac;
/** Generated complete resource-backed Gen 4-9 forms. IDs are append-only. */
public final class LaterFormData {
    private LaterFormData(){}
    public static final int MAX_FORM=%d;
    private static final int[] ID={%s},DEX={%s},T1={%s},T2={%s},REGIONAL={%s},UPGRADE={%s},CHOICE={%s},SHINY={%s};
    private static final String[] KEY={%s},NAME={%s},LABEL={%s};
    private static int index(int f){for(int i=0;i<ID.length;i++)if(ID[i]==f)return i;return -1;}
    public static boolean contains(int f){return index(f)>=0;}
    public static String key(int f){int i=index(f);return i<0?"":KEY[i];}
    public static String name(int f){int i=index(f);return i<0?"":NAME[i];}
    public static boolean regional(int f){int i=index(f);return i>=0&&REGIONAL[i]!=0;}
    public static boolean upgrade(int f){int i=index(f);return i>=0&&UPGRADE[i]!=0;}
    public static boolean shiny(int f){int i=index(f);return i>=0&&SHINY[i]!=0;}
    public static int type1(int sp,int f){int i=index(f);return i<0?Data.t1[sp]:T1[i];}
    public static int type2(int sp,int f){int i=index(f);return i<0?Data.t2[sp]:T2[i];}
    public static int first(int sp){int dex=Data.nationalDex(sp);for(int i=0;i<ID.length;i++)if(DEX[i]==dex)return ID[i];return 0;}
    public static int regionalFor(int sp){int dex=Data.nationalDex(sp);for(int i=0;i<ID.length;i++)if(DEX[i]==dex&&REGIONAL[i]!=0)return ID[i];return 0;}
    public static int choiceCount(int sp){int n=0,dex=Data.nationalDex(sp);for(int i=0;i<ID.length;i++)if(DEX[i]==dex&&CHOICE[i]!=0)n++;return n;}
    public static int choice(int sp,int n){int dex=Data.nationalDex(sp);for(int i=0;i<ID.length;i++)if(DEX[i]==dex&&CHOICE[i]!=0&&n--==0)return ID[i];return 0;}
    public static int next(int sp,int f){int dex=Data.nationalDex(sp),start=index(f);for(int i=start+1;i<ID.length;i++)if(DEX[i]==dex)return ID[i];return 0;}
    public static boolean marker(int sp){int dex=Data.nationalDex(sp);for(int i=0;i<ID.length;i++)if(DEX[i]==dex&&UPGRADE[i]!=0)return true;return false;}
    public static boolean hasEvolution(int sp){int d=Data.nationalDex(sp);return d==550||d==562;}
    public static int evolved(int sp,int f){int i=index(f),d=Data.nationalDex(sp),target=0;if(regional(f)&&d==562)target=867;if(regional(f)&&d==550)target=902;if(i>=0&&LABEL[i].equals("Galar")&&d==52)target=863;if(regional(f)&&d==83)target=865;if(target!=0){int s=Data.speciesForDex(target);return s>=0?s:sp;}return Data.evo[sp]>=0?Data.evo[sp]:sp;}
    public static int evolutionForm(int next,int f){int old=index(f),dex=Data.nationalDex(next);if(old<0)return 0;for(int i=0;i<ID.length;i++)if(DEX[i]==dex&&LABEL[i].equals(LABEL[old]))return ID[i];return REGIONAL[old]!=0?EvolutionVariantData.formFor(next):0;}
    public static int region(int sp,int f){int i=index(f);if(i>=0&&REGIONAL[i]!=0)return LABEL[i].equals("Galar")?30001:LABEL[i].equals("White")?30003:LABEL[i].equals("Alola")?30004:LABEL[i].startsWith("Paldea")?40000+f:30002;int d=Data.nationalDex(sp);if(f==19||f==24||f==29||f==31||f==32||f==33||f==34||f==35||f==36||f==37||f==42||f==43||d==903||d==904)return 30002;if(f==13||f==14||f==15||f==16||f==17||f==18||f==20||f==23||f==25||f==26)return 30004;if(f==21||f==22||f==27||f==28||f==30||f==38||f==39||f==40||f==44)return 30001;if(f==41||d==980)return 30005;if(d==867||d==863||d==865||d==866||d==864)return 30001;if(d==902)return 30003;return 0;}
    public static int synergyBranch(int sp,int f){int r=region(sp,f);return r==0?0:r+100*CanonicalEvolutionData.synergyBranch(sp);}
}
''' % (max([79]+[r['id'] for r in catalog]),arr('id'),arr('dex'),arr('type1'),arr('type2'),arr('regional'),arr('upgrade'),arr('choice'),arr('shiny'),arr('key'),','.join(json.dumps(r['pokemon']+' '+r['form'].replace('_',' '),ensure_ascii=False) for r in catalog),arr('form'))
    (ROOT/'src/pac/LaterFormData.java').write_text(java,encoding='utf-8')
    print('Imported',len(catalog),'forms and',len(megas),'Megas; skipped',sum(not r['complete'] for r in selected),'incomplete candidates')

if __name__=='__main__':main()

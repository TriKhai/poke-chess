package pac;

/** Generated Gen 1-3 roster. Source: app/models/precomputed/pokemons-data.csv. */
public final class Data {
    private Data() {}
    public static final int T_NORMAL = 0, T_FIRE = 1, T_WATER = 2, T_GRASS = 3, T_ELEC = 4, T_ROCK = 5, T_PSY = 6, T_FIGHT = 7, T_FLY = 8, T_DRAGON = 9, T_GHOST = 10, T_BUG = 11, T_POISON = 12, T_GROUND = 13, T_ICE = 14, T_DARK = 15, T_STEEL = 16, T_FAIRY = 17, T_AMORPHOUS = 18, T_AQUATIC = 19, T_ARTIFICIAL = 20, T_BABY = 21, T_FIELD = 22, T_FLORA = 23, T_FOSSIL = 24, T_GOURMET = 25, T_HUMAN = 26, T_LIGHT = 27, T_MONSTER = 28, T_SOUND = 29, T_WILD = 30;
    public static final int NT = 31;
    public static final String[] TNAME = {"Normal","Fire","Water","Grass","Electric","Rock","Psychic","Fighting","Flying","Dragon","Ghost","Bug","Poison","Ground","Ice","Dark","Steel","Fairy","Amorphous","Aquatic","Artificial","Baby","Field","Flora","Fossil","Gourmet","Human","Light","Monster","Sound","Wild"};
    public static final String[] TSHORT = {"NOR","FIR","WAT","GRA","ELE","ROC","PSY","FIG","FLY","DRA","GHO","BUG","POI","GRO","ICE","DAR","STE","FAI","AMO","AQU","ART","BAB","FIE","FLO","FOS","GOU","HUM","LIG","MON","SOU","WIL"};
    public static final int[] TCOL = {0xA8A878,0xF08030,0x6890F0,0x78C850,0xF8D030,0xB8A038,0xF85888,0xC03028,0xA890F0,0x7038F8,0x705898,0xA8B820,0xA040A0,0xE0C068,0x98D8D8,0x705848,0xB8B8D0,0xEE99AC,0x806080,0x4098A8,0x8899AA,0xFFB0C8,0xB09070,0x70B850,0x9C8060,0xE8A050,0xC09070,0xF8E870,0x907858,0xC080D0,0x789060};
    public static final String[] SYN_TEXT = {"HP +12/25/45%","ATK +15/35/60%","Start mana +20/40/70","Regen 2/4/7% HP/s","Atk speed +15/30/50%","DEF +2/5/9","Skill dmg +30/60/120%","Crit 15/30/50%","Dodge 10/20/35%","ATK+HP +10/25/50%","Lifesteal 10/20/35%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%","HP+ATK +8/16/28%"};
    public static final String[] ABIL_NAME = {"Power Strike","Heal Pulse","Blast","Rally","Paralyze","Chain Bolt","Harden","Drain","Hyper Beam"};
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
    public static final int CORE_N=386;
    /** Highest generation enabled for gameplay in this build. Raised per v1.5.7 letter release. */
    public static final int BATTLE_MAX_GEN=9;
    public static final int MAX=1024;
    public static int N=0;
    public static final String[] name=new String[MAX],skillName=new String[MAX];
    public static final int[] t1=new int[MAX],t2=new int[MAX],cost=new int[MAX],category=new int[MAX],hp=new int[MAX],atk=new int[MAX],def=new int[MAX],speDef=new int[MAX],speed=new int[MAX],range=new int[MAX],cd=new int[MAX],mana=new int[MAX],abil=new int[MAX],evo=new int[MAX],fam=new int[MAX],tier=new int[MAX],stage=new int[MAX],zones=new int[MAX],laterDex=new int[MAX];
    public static int MEWTWO=149;
    static int add(String nm,String sk,int a,int b,int c,int cat,int h,int at,int df,int sd,int spd,int rg,int cool,int mn,int ab,int st,int zmask){int i=N++;name[i]=nm;skillName[i]=sk;t1[i]=a;t2[i]=b;cost[i]=c;category[i]=cat;hp[i]=h;atk[i]=at;def[i]=df;speDef[i]=sd;speed[i]=spd;range[i]=rg;cd[i]=cool;mana[i]=mn;abil[i]=ab;evo[i]=-1;fam[i]=i;tier[i]=1;stage[i]=st;zones[i]=zmask;return i;}
    static void link(int from,int to){evo[from]=to;fam[to]=fam[from];tier[to]=tier[from]+1;cost[to]=cost[from];zones[to]=0;}
    static { init1(); init2(); init3(); initLinks(); initGen4Battle(); initLaterLegendaries(); initLaterBattle(); linkLaterFamilies(); CanonicalEvolutionData.init(); initBranchLinks(); }
    private static void initBranchLinks(){
        link(60,61);branch(60,185);branch(43,181);
        link(132,133);int[] eevee={134,135,136,196,197,470,471,700};for(int i=0;i<eevee.length;i++){int sp=speciesByDex(eevee[i]);if(sp>=0)branch(132,sp);}
        link(78,79);branch(78,198);
        link(235,105);branch(235,106);branch(235,236);
        link(122,211); // Scyther -> Scizor was missing from the core evolution links.
        link(264,265);branch(264,267);link(267,268);
        link(280,281);branch(280,speciesByDex(475));
        link(360,361);branch(360,speciesByDex(478));
        link(365,366);branch(365,367);
        branch(289,291); // Shedinja is an explicit option instead of an extra free unit.
        link(speciesByDex(406),314);link(314,speciesByDex(407));
        link(speciesByDex(433),357);
        int ob=speciesByDex(862);fam[ob]=fam[262];tier[ob]=3;cost[ob]=cost[262];zones[ob]=0;
    }
    private static void branch(int from,int to){fam[to]=fam[from];tier[to]=tier[from]+1;cost[to]=cost[from];zones[to]=0;}
    private static void initGen4Battle(){for(int i=0;i<CollectionDex.COUNT;i++)if(CollectionDex.GEN[i]==4)addCollectionEntry(i);}
    private static void addCollectionEntry(int i){int dex=CollectionDex.DEX[i],speed=LaterBattleData.SPEED[i],sp=add(CollectionDex.NAME[i],LaterBattleData.SKILL[i],CollectionDex.T1[i],CollectionDex.T2[i],LaterBattleData.COST[i],LaterBattleData.CATEGORY[i],LaterBattleData.HP[i],LaterBattleData.ATK[i],LaterBattleData.DEF[i],LaterBattleData.SDEF[i],speed,LaterBattleData.RANGE[i],Math.max(2,Math.min(7,8-speed/14)),LaterBattleData.MANA[i],LaterBattleData.ABILITY[i],LaterBattleData.TIER[i],0);laterDex[sp]=dex;tier[sp]=LaterBattleData.TIER[i];}
    private static int collectionCategory(String s){if("Common".equals(s))return 0;if("Uncommon".equals(s))return 1;if("Rare".equals(s))return 2;if("Epic".equals(s))return 3;if("Ultra".equals(s))return 4;if("Legendary".equals(s))return 6;return 5;}
    private static void initLaterLegendaries(){
        for(int i=0;i<CollectionDex.COUNT;i++)if(CollectionDex.GEN[i]>4&&"Legendary".equals(CollectionDex.CATEGORY[i])){
            addCollectionEntry(i);
        }
    }
    private static void initLaterBattle(){for(int i=0;i<CollectionDex.COUNT;i++)if(CollectionDex.GEN[i]>=5&&CollectionDex.GEN[i]<=BATTLE_MAX_GEN&&!"Legendary".equals(CollectionDex.CATEGORY[i]))addCollectionEntry(i);}
    private static int speciesByDex(int dex){if(dex>=1&&dex<=CORE_N)return dex-1;for(int i=CORE_N;i<N;i++)if(laterDex[i]==dex)return i;return-1;}
    private static int collectionByDex(int dex){for(int i=0;i<CollectionDex.COUNT;i++)if(CollectionDex.DEX[i]==dex)return i;return-1;}
    private static int familyDexOf(int sp){if(sp<CORE_N)return fam[sp]+1;int ci=collectionByDex(laterDex[sp]);return ci>=0?LaterBattleData.FAMILY_DEX[ci]:laterDex[sp];}
    private static void linkLaterFamilies(){for(int sp=CORE_N;sp<N;sp++){int ci=collectionByDex(laterDex[sp]);if(ci<0)continue;int fd=LaterBattleData.FAMILY_DEX[ci],base=speciesByDex(fd);if(base>=0)fam[sp]=fam[base];int want=tier[sp]-1;if(want<1)continue;int prev=-1;for(int q=0;q<N;q++)if(q!=sp&&tier[q]==want&&fam[q]==fam[sp]){prev=q;break;}if(prev>=0)evo[prev]=sp;}}
    private static String legendSkill(int type){
        if(type==T_FIRE)return "Legend Flame";if(type==T_WATER)return "Origin Pulse";
        if(type==T_ELEC)return "Thunder Storm";if(type==T_PSY)return "Astral Burst";
        if(type==T_DRAGON)return "Dragon Force";if(type==T_GHOST)return "Phantom Rift";
        if(type==T_ICE)return "Absolute Zero";if(type==T_STEEL)return "Titan Crash";
        return "Legendary Power";
    }
    public static int nationalDex(int sp){
        return sp<CORE_N?sp+1:(sp>=0&&sp<N?laterDex[sp]:0);
    }
    public static int generation(int sp){int dex=nationalDex(sp);return dex<=151?1:(dex<=251?2:(dex<=386?3:(dex<=493?4:(dex<=649?5:(dex<=721?6:(dex<=809?7:(dex<=905?8:9)))))));}
    public static int collectionIndex(int sp){int dex=nationalDex(sp);for(int i=0;i<CollectionDex.COUNT;i++)if(CollectionDex.DEX[i]==dex)return i;return-1;}
    public static int speciesForDex(int dex){return speciesByDex(dex);}
    private static void init1(){
        add("Bulbasaur","Magical Leaf",3,12,3,2,80,5,4,4,51,2,5,70,0,1,1); // #001
        add("Ivysaur","Magical Leaf",3,12,3,2,130,9,8,8,51,2,5,70,0,2,1); // #002
        add("Venusaur","Magical Leaf",3,12,3,2,240,17,12,12,51,2,5,70,8,3,1); // #003
        add("Charmander","Blast Burn",9,1,1,0,60,4,3,3,57,1,4,100,2,1,1); // #004
        add("Charmeleon","Blast Burn",9,1,1,0,120,8,4,4,57,1,4,100,2,2,1); // #005
        add("Charizard","Blast Burn",9,1,1,0,220,18,5,5,57,1,4,100,2,3,1); // #006
        add("Squirtle","Hydro Pump",2,22,1,0,60,5,2,2,50,3,5,100,0,1,2); // #007
        add("Wartortle","Hydro Pump",2,22,1,0,120,9,3,3,50,3,5,100,0,2,2); // #008
        add("Blastoise","Hydro Pump",2,22,1,0,200,20,4,4,50,3,5,100,8,3,2); // #009
        add("Caterpie","String Shot",11,18,1,0,60,5,2,2,47,2,5,100,0,1,1); // #010
        add("Metapod","String Shot",11,18,1,0,110,9,6,2,47,2,5,100,0,2,1); // #011
        add("Butterfree","String Shot",11,8,1,0,180,16,4,6,47,2,5,100,8,3,1); // #012
        add("Weedle","Bug Buzz",11,12,1,0,60,5,4,4,49,1,5,100,0,1,1); // #013
        add("Kakuna","Bug Buzz",11,12,1,0,110,10,6,4,35,1,6,100,0,2,1); // #014
        add("Beedrill","Bug Buzz",11,12,1,0,170,17,4,4,49,1,5,100,8,3,1); // #015
        add("Pidgey","Hurricane",0,8,1,0,60,4,2,2,57,2,4,100,0,1,1); // #016
        add("Pidgeotto","Hurricane",0,8,1,0,110,8,4,4,57,2,4,100,0,2,1); // #017
        add("Pidgeot","Hurricane",0,8,1,0,200,16,6,6,57,2,4,100,8,3,1); // #018
        add("Rattata","Agility",30,0,1,0,50,4,2,2,56,1,4,100,0,1,1); // #019
        add("Raticate","Agility",30,0,1,0,110,9,4,4,56,1,4,100,0,2,1); // #020
        add("Spearow","Peck",30,8,1,0,50,4,2,2,57,1,4,100,0,1,1); // #021
        add("Fearow","Peck",30,8,1,0,120,8,4,4,57,1,4,100,0,2,1); // #022
        add("Ekans","Coil",12,15,2,1,60,8,4,4,51,1,5,90,0,1,1); // #023
        add("Arbok","Coil",12,15,2,1,130,17,8,8,51,1,5,90,0,2,1); // #024
        add("Pikachu","Nuzzle",4,17,1,0,120,8,4,6,54,1,5,100,5,2,1); // #025
        add("Raichu","Nuzzle",4,17,1,0,220,17,7,10,54,1,5,100,5,3,1); // #026
        add("Sandshrew","Rollout",13,0,2,1,90,5,6,3,46,1,5,80,6,1,4); // #027
        add("Sandslash","Rollout",13,0,2,1,180,13,10,5,46,1,5,80,6,2,4); // #028
        add("Nidoranf","Venoshock",12,22,2,1,70,5,4,4,49,1,5,90,6,1,1); // #029
        add("Nidorina","Venoshock",12,22,2,1,130,10,6,6,49,1,5,90,6,2,1); // #030
        add("Nidoqueen","Venoshock",12,22,2,1,230,20,8,8,49,1,5,90,6,3,1); // #031
        add("Nidoranm","Horn Attack",12,22,2,1,70,5,4,4,52,1,5,90,6,1,1); // #032
        add("Nidorino","Horn Attack",12,22,2,1,140,9,4,4,52,1,5,90,6,2,1); // #033
        add("Nidoking","Horn Attack",12,22,2,1,250,20,6,6,52,1,5,90,6,3,1); // #034
        add("Clefairy","Metronome",17,0,2,1,150,11,6,6,44,1,5,100,0,2,1); // #035
        add("Clefable","Metronome",17,0,2,1,220,18,8,8,44,1,5,80,8,3,1); // #036
        add("Vulpix","Fire Spin",1,6,3,2,75,6,4,4,57,2,4,90,0,1,1); // #037
        add("Ninetales","Fire Spin",1,6,3,2,170,18,6,10,57,2,4,90,0,2,1); // #038
        add("Jigglypuff","Sing",0,17,2,1,120,10,4,4,39,2,6,90,0,2,1); // #039
        add("Wigglytuff","Sing",0,17,2,1,250,19,6,6,39,2,6,90,8,3,1); // #040
        add("Zubat","Leech Life",12,8,1,0,50,4,2,2,67,2,4,100,7,1,1); // #041
        add("Golbat","Leech Life",12,8,1,0,100,7,2,2,67,2,4,100,7,2,1); // #042
        add("Oddish","Stun Spore",23,12,2,7,80,8,4,4,41,1,6,80,4,1,1); // #043
        add("Gloom","Stun Spore",23,12,2,7,150,16,6,6,41,1,6,80,4,2,1); // #044
        add("Vileplume","Stun Spore",23,12,2,7,250,24,8,8,41,1,6,80,4,3,1); // #045
        add("Paras","Absorb",11,12,3,2,90,7,4,4,35,1,6,110,7,1,1); // #046
        add("Parasect","Absorb",11,12,3,2,180,18,6,6,35,1,6,110,7,2,1); // #047
        add("Venonat","Bug Buzz",11,8,2,1,50,6,4,4,54,1,5,80,0,1,1); // #048
        add("Venomoth","Bug Buzz",11,8,2,1,130,10,6,6,54,2,5,80,0,2,1); // #049
        add("Diglett","Dig",13,0,2,1,75,5,6,4,63,1,4,50,6,1,4); // #050
        add("Dugtrio","Dig",13,0,2,1,160,12,10,8,63,1,4,50,6,2,4); // #051
        add("Meowth","Payday",0,22,3,2,80,7,6,6,62,1,4,80,0,1,1); // #052
        add("Persian","Payday",0,22,3,2,200,17,6,6,62,1,4,80,0,2,1); // #053
        add("Psyduck","Psyshock",2,6,2,1,75,7,4,4,52,1,5,100,0,1,2); // #054
        add("Golduck","Psyshock",2,6,2,1,170,14,8,8,52,1,5,100,0,2,2); // #055
        add("Mankey","Thrash",30,7,4,3,120,7,6,4,54,1,5,90,0,1,1); // #056
        add("Primeape","Thrash",30,7,4,3,240,20,12,4,54,1,5,90,0,2,1); // #057
        add("Growlithe","Fire Fang",1,22,2,1,75,5,4,4,55,1,5,80,0,1,1); // #058
        add("Arcanine","Fire Fang",1,22,2,1,140,13,10,10,55,1,5,80,0,2,1); // #059
        add("Poliwag","Soak",2,19,1,0,65,5,2,2,54,2,5,100,0,1,2); // #060
        add("Poliwhirl","Soak",2,19,1,0,120,8,2,2,54,2,5,100,0,2,2); // #061
        add("Poliwrath","Crabhammer",2,19,1,0,220,17,6,6,54,1,5,100,8,3,2); // #062
        add("Abra","Teleport",6,26,3,2,90,4,4,8,63,4,4,50,0,1,8); // #063
        add("Kadabra","Teleport",6,26,3,2,130,8,6,10,63,4,4,50,0,2,8); // #064
        add("Alakazam","Teleport",6,26,3,2,230,19,8,16,63,4,4,50,8,3,8); // #065
        add("Machop","Guillotine",7,26,2,1,70,6,6,6,43,1,5,100,0,1,1); // #066
        add("Machoke","Guillotine",7,26,2,1,130,12,8,8,43,1,5,100,0,2,1); // #067
        add("Machamp","Guillotine",7,26,2,1,220,23,10,10,43,1,5,100,8,3,1); // #068
        add("Bellsprout","Ingrain",23,3,2,7,60,6,3,3,47,1,5,100,0,1,1); // #069
        add("Weepinbell","Ingrain",23,3,2,7,130,12,5,5,47,1,5,100,0,2,1); // #070
        add("Victreebel","Ingrain",23,3,2,7,200,20,7,7,47,1,5,100,8,3,1); // #071
        add("Tentacool","Toxic",2,19,2,1,65,4,4,8,57,1,4,85,0,1,2); // #072
        add("Tentacruel","Toxic",2,19,2,1,150,9,6,14,57,1,4,85,0,2,2); // #073
        add("Geodude","Rock Slide",13,5,1,0,70,4,4,4,39,1,6,100,6,1,4); // #074
        add("Graveler","Rock Slide",13,5,1,0,120,10,8,8,39,1,6,100,6,2,4); // #075
        add("Golem","Rock Slide",13,5,1,0,200,17,12,12,39,1,6,100,6,3,4); // #076
        add("Ponyta","Flame Charge",1,22,4,3,90,11,6,6,59,1,4,60,0,1,1); // #077
        add("Rapidash","Flame Charge",1,22,4,3,220,21,10,10,59,1,4,60,0,2,1); // #078
        add("Slowpoke","Yawn",6,2,2,1,80,7,6,4,35,1,6,100,0,1,8); // #079
        add("Slowbro","Yawn",6,2,2,1,160,12,10,6,35,1,6,100,0,2,8); // #080
        add("Magnemite","Magnet Bomb",4,16,2,1,80,5,2,2,44,2,5,100,5,1,1); // #081
        add("Magneton","Magnet Bomb",4,16,2,1,150,9,2,2,44,2,5,100,5,2,1); // #082
        add("Farfetch'd","Razor Wind",8,25,5,5,200,20,8,8,50,1,5,60,8,3,1); // #083
        add("Doduo","Drill Peck",30,8,4,3,90,10,6,4,60,1,4,85,0,1,1); // #084
        add("Dodrio","Drill Peck",30,8,4,3,185,24,10,6,60,1,4,85,0,2,1); // #085
        add("Seel","Aurora Beam",14,19,2,1,80,7,8,8,47,1,5,100,0,1,2); // #086
        add("Dewgong","Aurora Beam",14,19,2,1,170,16,8,8,47,1,5,100,0,2,2); // #087
        add("Grimer","Sludge",12,28,2,1,80,4,6,4,41,1,6,100,0,1,1); // #088
        add("Muk","Sludge",12,28,2,1,170,9,12,8,41,1,6,100,0,2,1); // #089
        add("Shellder","Shell Smash",2,14,2,1,70,5,10,4,47,1,5,110,6,1,2); // #090
        add("Cloyster","Shell Smash",2,14,2,1,150,11,16,4,47,1,5,110,6,2,2); // #091
        add("Gastly","Nightmare",10,12,5,4,90,12,6,6,60,2,4,90,0,1,8); // #092
        add("Haunter","Nightmare",10,12,5,4,180,22,8,6,60,2,4,90,0,2,8); // #093
        add("Gengar","Nightmare",10,12,5,4,350,35,10,6,60,2,4,90,8,3,8); // #094
        add("Onix","Iron Tail",5,13,4,3,100,7,20,8,35,1,6,100,6,1,4); // #095
        add("Drowzee","Dream Eater",6,26,4,3,100,7,4,6,46,2,5,100,7,1,8); // #096
        add("Hypno","Dream Eater",6,26,4,3,250,14,8,12,46,2,5,100,7,2,8); // #097
        add("Krabby","Vise Grip",2,0,2,1,80,6,8,2,49,1,5,100,0,1,2); // #098
        add("Kingler","Vise Grip",2,0,2,1,150,16,14,4,49,1,5,100,0,2,2); // #099
        add("Voltorb","Explosion",4,20,2,1,60,7,2,2,73,1,3,100,2,1,1); // #100
        add("Electrode","Explosion",4,20,2,1,150,14,6,6,73,1,3,100,2,2,1); // #101
        add("Exeggcute","Egg Bomb",23,6,4,3,110,9,6,6,38,3,6,100,0,1,1); // #102
        add("Exeggutor","Egg Bomb",23,6,4,3,300,22,10,10,38,1,6,100,0,2,1); // #103
        add("Cubone","Bonemerang",13,10,4,3,110,11,8,8,36,1,6,80,6,1,4); // #104
        add("Marowak","Bonemerang",13,10,4,3,250,22,12,10,36,1,6,80,6,2,4); // #105
        add("Hitmonlee","Mawashi Geri",7,26,5,5,200,24,6,6,56,1,4,100,8,3,1); // #106
        add("Hitmonchan","Mega Punch",7,26,5,5,200,22,12,12,49,1,5,100,8,3,1); // #107
        add("Lickitung","Lick",30,0,2,1,70,5,4,4,41,1,6,90,0,1,1); // #108
        add("Koffing","Smog",12,20,2,1,65,5,8,4,44,1,5,60,0,1,1); // #109
        add("Weezing","Smog",12,20,2,1,155,10,12,7,44,1,5,60,0,2,1); // #110
        add("Rhyhorn","Horn Drill",13,28,5,4,120,14,12,4,38,1,6,120,6,1,4); // #111
        add("Rhydon","Horn Drill",13,28,5,4,240,23,20,6,38,1,6,120,6,2,4); // #112
        add("Chansey","Soft Boiled",0,17,5,4,300,21,12,20,43,1,5,120,0,2,1); // #113
        add("Tangela","Vine Whip",3,28,2,1,100,4,8,4,41,1,6,100,6,1,1); // #114
        add("Kangaskhan","Dizzy Punch",7,0,5,5,200,15,8,8,54,1,5,100,8,3,1); // #115
        add("Horsea","Whirlpool",9,2,2,1,70,6,2,2,52,2,5,100,0,1,1); // #116
        add("Seadra","Whirlpool",9,2,2,1,140,11,2,2,52,2,5,100,0,2,1); // #117
        add("Goldeen","Waterfall",2,0,3,2,90,8,4,4,47,1,5,100,0,1,2); // #118
        add("Seaking","Waterfall",2,0,3,2,250,16,8,8,47,1,5,100,0,2,2); // #119
        add("Staryu","Psybeam",2,6,3,2,80,6,4,6,62,2,4,100,0,1,2); // #120
        add("Starmie","Psybeam",2,6,3,2,180,17,8,12,62,2,4,100,0,2,2); // #121
        add("Mr. Mime","Mimic",17,6,2,1,150,11,4,7,54,2,5,80,0,2,1); // #122
        add("Scyther","X Scissor",11,8,5,5,170,17,10,10,59,1,4,80,8,3,1); // #123
        add("Jynx","Lovely Kiss",14,6,2,1,130,11,6,8,61,2,4,70,0,2,2); // #124
        add("Electabuzz","Shockwave",4,20,4,3,190,15,12,12,55,1,5,100,5,2,1); // #125
        add("Magmar","Flamethrower",1,26,3,2,140,15,4,6,52,2,5,80,0,2,1); // #126
        add("Pinsir","Guillotine",30,11,5,5,190,20,6,6,52,1,5,100,8,3,1); // #127
        add("Tauros","Raging Bull",30,0,5,5,200,16,10,4,60,1,4,100,8,3,1); // #128
        add("Magikarp","Splash",2,-1,2,7,30,2,2,2,51,1,5,50,0,1,2); // #129
        add("Gyarados","Hydro Pump",9,2,2,7,300,28,10,2,51,1,5,100,8,3,1); // #130
    }
    private static void init2(){
        add("Lapras","Dive",19,14,5,5,225,12,5,9,38,1,6,120,8,3,2); // #131
        add("Ditto","Transform",18,-1,2,7,50,5,2,2,40,1,6,30,0,1,8); // #132
        add("Eevee","Happy Hour",0,22,2,7,60,5,5,3,43,1,5,100,0,1,1); // #133
        add("Vaporeon","Happy Hour",2,22,2,7,150,12,6,4,43,1,5,100,0,2,2); // #134
        add("Jolteon","Happy Hour",4,22,2,7,150,8,6,4,83,1,3,120,5,2,1); // #135
        add("Flareon","Happy Hour",1,22,2,7,150,12,6,4,43,1,5,100,0,2,1); // #136
        add("Porygon","Tri Attack",0,20,5,4,100,13,12,12,54,2,5,100,0,1,1); // #137
        add("Omanyte","Rock Tomb",24,2,2,1,70,6,4,6,43,2,5,90,6,1,4); // #138
        add("Omastar","Rock Tomb",24,2,2,1,150,14,6,8,43,2,5,90,6,2,4); // #139
        add("Kabuto","Protect",24,2,3,2,80,8,6,6,51,1,5,80,6,1,4); // #140
        add("Kabutops","Slashing Claw",24,2,3,2,190,22,8,8,51,1,5,80,6,2,4); // #141
        add("Aerodactyl","Rock Slide",5,8,5,5,200,14,6,6,67,1,4,80,6,3,4); // #142
        add("Snorlax","Body Slam",0,25,4,3,300,21,6,6,35,1,6,100,0,2,1); // #143
        add("Articuno","Blizzard",14,8,5,6,300,28,6,6,52,2,5,120,8,3,2); // #144
        add("Zapdos","Thunder",4,8,5,6,300,27,6,6,57,2,4,110,5,3,1); // #145
        add("Moltres","Overheat",1,8,5,6,300,28,6,6,54,2,5,100,8,3,1); // #146
        add("Dratini","Twister",9,8,4,3,90,7,8,8,51,1,5,100,0,1,1); // #147
        add("Dragonair","Twister",9,8,4,3,150,15,13,13,51,1,5,100,0,2,1); // #148
        add("Dragonite","Twister",9,8,4,3,290,28,20,20,51,1,5,100,8,3,1); // #149
        add("Mewtwo","Psystrike",6,28,5,6,200,25,10,10,67,3,4,110,8,3,8); // #150
        add("Mew","Teleport",6,18,5,6,200,27,10,10,64,4,4,50,8,3,8); // #151
        add("Chikorita","Sweet Scent",23,3,2,7,70,6,2,2,51,2,5,100,3,1,1); // #152
        add("Bayleef","Sweet Scent",23,3,2,7,140,10,4,4,51,2,5,100,3,2,1); // #153
        add("Meganium","Sweet Scent",23,3,2,7,220,22,6,6,51,2,5,100,3,3,1); // #154
        add("Cyndaquil","Wheel Of Fire",1,22,2,1,70,7,2,2,51,2,5,100,0,1,1); // #155
        add("Quilava","Wheel Of Fire",1,22,2,1,120,12,4,4,51,2,5,100,0,2,1); // #156
        add("Typhlosion","Wheel Of Fire",1,22,2,1,230,24,6,6,51,2,5,100,8,3,1); // #157
        add("Totodile","Crunch",2,28,3,2,75,7,4,4,50,1,5,100,0,1,2); // #158
        add("Croconaw","Crunch",2,28,3,2,130,14,6,6,50,1,5,100,0,2,2); // #159
        add("Feraligatr","Crunch",2,28,3,2,240,27,10,10,50,1,5,100,8,3,2); // #160
        add("Sentret","Helping Hand",13,0,3,2,80,6,6,6,54,1,5,100,6,1,4); // #161
        add("Furret","Helping Hand",13,0,3,2,200,15,8,8,54,1,5,80,6,2,4); // #162
        add("Hoothoot","Hypnosis",0,8,2,1,75,5,4,6,47,1,5,90,4,1,1); // #163
        add("Noctowl","Hypnosis",0,8,2,1,170,10,6,10,47,1,5,90,4,2,1); // #164
        add("Ledyba","Mach Punch",11,7,2,1,60,5,2,4,55,1,5,100,0,1,1); // #165
        add("Ledian","Mach Punch",11,7,2,1,130,9,4,10,55,1,5,100,0,2,1); // #166
        add("Spinarak","String Shot",30,11,2,1,60,6,2,2,38,2,6,70,0,1,1); // #167
        add("Ariados","String Shot",30,11,2,1,150,15,4,4,38,2,6,70,0,2,1); // #168
        add("Crobat","Leech Life",12,8,1,0,200,15,2,2,67,2,4,100,7,3,1); // #169
        add("Chinchou","Electro Ball",2,4,2,1,60,7,4,6,43,2,5,100,5,1,2); // #170
        add("Lanturn","Electro Ball",2,4,2,1,130,16,6,10,43,2,5,100,5,2,2); // #171
        add("Pichu","Nuzzle",4,17,1,0,60,5,1,2,54,1,5,100,5,1,1); // #172
        add("Cleffa","Metronome",17,0,2,1,70,5,2,2,44,1,5,100,0,1,1); // #173
        add("Igglybuff","Sing",0,17,2,1,65,5,2,2,39,2,6,90,0,1,1); // #174
        add("Togepi","Wish",17,0,3,2,80,5,2,2,51,2,5,100,1,1,1); // #175
        add("Togetic","Wish",17,0,3,2,150,10,2,2,51,2,5,100,1,2,1); // #176
        add("Natu","Magic Bounce",6,8,3,2,90,5,4,4,60,2,4,70,0,1,8); // #177
        add("Xatu","Magic Bounce",6,8,3,2,200,15,7,7,60,2,4,70,0,2,8); // #178
        add("Mareep","Thunder Shock",4,22,1,0,60,5,2,2,43,2,5,100,5,1,1); // #179
        add("Flaffy","Thunder Shock",4,22,1,0,110,9,2,2,43,2,5,100,5,2,1); // #180
        add("Ampharos","Thunder Shock",4,22,1,0,220,18,2,2,43,2,5,100,5,3,1); // #181
        add("Bellossom","Petal Blizzard",23,29,2,7,300,30,10,10,41,1,6,30,8,3,1); // #182
        add("Marill","Play Rough",2,17,1,0,110,9,4,4,41,2,6,100,0,2,2); // #183
        add("Azumarill","Play Rough",2,17,1,0,200,21,6,6,41,2,6,100,8,3,2); // #184
        add("Sudowoodo","Wood Hammer",5,23,4,3,250,22,12,6,35,1,6,100,6,2,4); // #185
        add("Politoed","Soak",2,19,1,0,220,17,2,2,54,2,5,90,8,3,2); // #186
        add("Hoppip","Acrobatics",8,23,2,7,50,5,2,2,60,3,4,100,0,1,1); // #187
        add("Skiploom","Acrobatics",8,23,2,7,100,10,3,4,60,3,4,100,0,2,1); // #188
        add("Jumpluff","Acrobatics",8,23,2,7,150,15,4,6,60,3,4,100,8,3,1); // #189
        add("Aipom","Tickle",30,0,2,1,70,6,4,4,62,1,4,80,0,1,1); // #190
        add("Sunkern","Solar Beam",3,27,3,2,80,9,6,6,35,2,6,100,0,1,1); // #191
        add("Sunflora","Solar Beam",3,27,3,2,160,20,10,10,35,2,6,80,0,2,1); // #192
        add("Yanma","Aerial Ace",11,24,3,2,70,8,2,2,55,2,5,100,6,1,1); // #193
        add("Wooper","Mud Shot",19,13,3,2,80,8,10,6,31,1,6,100,6,1,2); // #194
        add("Quagsire","Mud Shot",19,13,3,2,190,21,15,10,31,1,6,100,6,2,2); // #195
        add("Espeon","Happy Hour",6,22,2,7,150,8,6,4,70,1,3,100,0,2,8); // #196
        add("Umbreon","Happy Hour",15,22,2,7,150,12,6,4,43,1,5,100,0,2,8); // #197
        add("Murkrow","Foul Play",15,8,4,3,110,9,12,6,48,1,5,70,0,1,8); // #198
        add("Slowking","Wise Yawn",6,2,2,1,160,11,8,6,35,3,6,110,0,2,8); // #199
        add("Misdreavus","Night Shade",10,17,4,3,80,6,4,6,59,3,4,100,0,1,8); // #200
        add("Unown A","Hidden Power A",6,-1,2,7,100,2,2,2,40,4,6,90,0,1,8); // #201
        add("Wobbuffet","Counter",6,18,3,2,280,20,8,8,36,1,6,100,0,2,8); // #202
        add("Girafarig","Twin Beam",0,6,4,3,90,11,4,4,39,1,6,100,0,1,1); // #203
        add("Pineco","Explosion",11,16,2,1,75,5,10,6,38,1,6,120,2,1,1); // #204
        add("Forretress","Explosion",11,16,2,1,200,9,14,6,38,1,6,120,2,2,1); // #205
        add("Dunsparce","Hyper Drill",0,13,5,5,220,15,4,4,35,1,6,100,6,3,1); // #206
        add("Gligar","Poison Jab",13,12,4,3,100,11,6,3,55,1,5,100,6,1,4); // #207
        add("Steelix","Iron Tail",5,13,4,3,200,13,40,10,35,1,6,100,6,2,4); // #208
        add("Snubull","Roar",17,22,4,3,115,13,8,4,39,1,6,70,0,1,1); // #209
        add("Granbull","Roar",17,22,4,3,265,30,12,6,39,1,6,70,0,2,1); // #210
        add("Qwilfish","Stockpile",2,12,5,5,150,13,15,6,52,1,5,80,8,3,2); // #211
        add("Scizor","Bullet Punch",11,16,5,5,170,22,14,10,42,1,5,80,6,3,1); // #212
        add("Shuckle","Bide",11,5,5,5,150,5,40,40,27,1,7,50,6,3,1); // #213
        add("Heracross","Close Combat",11,7,5,5,190,21,6,6,52,1,5,100,8,3,1); // #214
        add("Sneasel","Slashing Claw",14,15,4,3,85,8,2,6,65,1,4,40,0,1,2); // #215
        add("Teddiursa","Fury Swipes",30,13,5,4,150,13,8,6,41,1,6,100,6,1,1); // #216
        add("Ursaring","Fury Swipes",30,13,5,4,280,24,12,10,41,1,6,100,6,2,1); // #217
        add("Slugma","Lava Plume",1,5,3,2,70,7,8,6,35,1,6,100,6,1,1); // #218
        add("Magcargo","Lava Plume",1,5,3,2,180,16,16,10,35,1,6,100,6,2,1); // #219
        add("Swinub","Icicle Crash",13,14,1,0,65,4,5,3,51,1,5,100,6,1,4); // #220
        add("Piloswine","Icicle Crash",13,14,1,0,120,8,8,6,51,1,5,100,6,2,4); // #221
        add("Corsola","Recover",5,19,4,3,125,9,2,8,35,1,6,100,1,1,4); // #222
        add("Remoraid","Aqua Jet",30,2,2,7,60,13,4,2,39,1,6,80,0,1,1); // #223
        add("Octillery","Octazooka",30,2,2,7,150,26,6,6,39,3,6,80,0,2,1); // #224
        add("Delibird","Present",14,8,5,5,200,19,10,10,49,1,5,100,8,3,2); // #225
        add("Mantine","Bounce",2,8,5,5,230,12,8,16,47,2,5,100,8,3,2); // #226
        add("Skarmory","Roar",16,8,5,5,190,18,16,8,47,1,5,80,6,3,4); // #227
        add("Houndour","Beat Up",1,15,4,3,90,7,6,10,55,1,5,110,0,1,1); // #228
        add("Houndoom","Beat Up",1,15,4,3,175,24,10,14,55,1,5,110,0,2,1); // #229
        add("Kingdra","Whirlpool",9,2,2,1,250,21,4,4,52,2,5,100,8,3,1); // #230
        add("Phanpy","Rapid Spin",30,13,3,2,80,5,8,4,41,1,6,100,6,1,1); // #231
        add("Donphan","Rapid Spin",30,13,3,2,180,10,12,8,41,1,6,100,6,2,1); // #232
        add("Porygon 2","Tri Attack",0,20,5,4,200,23,16,16,54,2,5,80,0,2,1); // #233
        add("Stantler","Psyshield Bash",30,6,5,5,180,19,6,6,52,1,5,100,8,3,1); // #234
        add("Smeargle","Sketch",0,26,5,5,250,19,6,6,49,3,5,100,8,3,1); // #235
        add("Tyrogue","Mach Punch",7,26,5,5,150,11,6,6,36,1,6,100,8,2,1); // #236
        add("Hitmontop","Triple Kick",7,26,5,5,200,22,10,10,45,1,5,80,8,3,1); // #237
        add("Smoochum","Lovely Kiss",14,6,2,1,60,5,2,3,61,2,4,70,0,1,2); // #238
        add("Elekid","Shockwave",4,20,4,3,110,5,8,8,55,1,5,100,5,1,1); // #239
        add("Magby","Flamethrower",1,26,3,2,80,6,2,4,52,2,5,80,0,1,1); // #240
        add("Miltank","Rollout",0,25,5,5,200,13,10,10,57,1,4,100,8,3,1); // #241
        add("Blissey","Soft Boiled",0,17,5,4,480,26,20,30,43,1,5,120,8,3,1); // #242
        add("Raikou","Volt Switch",30,4,5,6,300,26,10,10,62,1,4,120,5,3,1); // #243
        add("Entei","Flame Charge",30,1,5,6,300,27,10,10,57,1,4,120,8,3,1); // #244
        add("Suicune","Aqua Jet",30,2,5,6,300,28,10,10,52,1,5,100,8,3,1); // #245
        add("Larvitar","Bite",15,28,3,2,75,7,5,4,39,1,6,90,6,1,8); // #246
        add("Pupitar","Bite",15,28,3,2,130,14,9,8,39,1,6,90,6,2,8); // #247
        add("Tyranitar","Bite",15,28,3,2,210,28,12,10,39,1,6,90,6,3,8); // #248
        add("Lugia","Sky Attack",19,8,5,6,300,26,12,12,60,1,4,80,8,3,2); // #249
        add("Ho-Oh","Fire Blast",1,8,5,6,300,30,6,6,50,2,5,100,2,3,1); // #250
        add("Celebi","Time Travel",3,6,5,6,250,25,10,10,57,3,4,100,8,3,1); // #251
        add("Treecko","Leaf Blade",3,28,2,1,70,4,6,6,63,1,4,100,0,1,1); // #252
        add("Grovyle","Leaf Blade",3,28,2,1,120,12,6,6,63,1,4,100,0,2,1); // #253
        add("Sceptile","Leaf Blade",3,28,2,1,210,23,10,10,63,1,4,100,8,3,1); // #254
        add("Torchic","Blaze Kick",1,7,3,2,80,6,6,6,51,1,5,100,0,1,1); // #255
        add("Combusken","Blaze Kick",1,7,3,2,150,11,8,8,51,1,5,100,0,2,1); // #256
        add("Blaziken","Blaze Kick",1,7,3,2,240,23,10,10,51,1,5,100,8,3,1); // #257
        add("Mudkip","Mud Bubble",2,13,1,0,65,5,4,4,44,1,5,60,6,1,2); // #258
        add("Marshtomp","Mud Bubble",2,13,1,0,130,9,6,6,44,1,5,60,6,2,2); // #259
        add("Swampert","Mud Bubble",2,13,1,0,200,20,10,10,44,1,5,60,6,3,2); // #260
    }
    private static void init3(){
        add("Poochyena","Growl",30,15,3,2,80,9,4,4,47,1,5,75,0,1,1); // #261
        add("Mightyena","Growl",30,15,3,2,190,19,8,8,47,1,5,75,0,2,1); // #262
        add("Zigzagoon","Slash",30,22,3,2,80,7,8,4,57,1,4,50,0,1,1); // #263
        add("Linoone","Slash",30,22,3,2,180,19,12,8,57,1,4,50,0,2,1); // #264
        add("Wurmple","Entangling Thread",11,-1,4,3,110,12,6,6,31,1,6,90,0,1,1); // #265
        add("Silcoon","Spiky Shield",11,0,4,3,200,23,12,12,20,1,7,100,0,2,1); // #266
        add("Beautifly","Silver Wind",11,0,4,3,250,35,12,12,42,1,5,60,8,3,1); // #267
        add("Cascoon","Spiky Shield",11,12,4,3,200,23,12,12,20,1,7,100,0,2,1); // #268
        add("Dustox","Poison Powder",11,12,4,3,250,35,12,12,42,1,5,60,8,3,1); // #269
        add("Lotad","Torment",3,19,3,2,60,6,2,2,47,3,5,80,0,1,1); // #270
        add("Lombre","Torment",3,19,3,2,110,12,4,4,47,3,5,80,0,2,1); // #271
        add("Ludicolo","Torment",3,19,3,2,220,22,6,6,47,3,5,80,8,3,1); // #272
        add("Seedot","Razor Leaf",3,15,1,0,60,6,4,4,51,1,5,100,0,1,1); // #273
        add("Nuzleaf","Razor Leaf",3,15,1,0,120,9,6,6,51,1,5,100,0,2,1); // #274
        add("Shiftry","Razor Leaf",3,15,1,0,200,21,8,8,51,1,5,100,8,3,1); // #275
        add("Taillow","Air Slash",30,8,3,2,70,7,6,5,80,1,3,100,0,1,1); // #276
        add("Swellow","Air Slash",30,8,3,2,170,16,11,9,80,1,3,100,0,2,1); // #277
        add("Wingull","Whirlwind",2,8,4,3,90,10,10,6,46,2,5,70,0,1,2); // #278
        add("Pelipper","Whirlwind",2,8,4,3,200,24,14,8,46,2,5,70,0,2,2); // #279
        add("Ralts","Future Sight",6,17,4,3,90,6,4,8,51,3,5,100,0,1,8); // #280
        add("Kirlia","Future Sight",6,17,4,3,130,15,6,10,51,3,5,100,0,2,8); // #281
        add("Gardevoir","Future Sight",6,17,4,3,230,26,8,16,51,3,5,100,8,3,8); // #282
        add("Surskit","Aqua Jet",11,19,3,2,70,6,4,5,51,2,5,100,0,1,1); // #283
        add("Masquerain","Silver Wind",11,19,3,2,150,14,6,7,51,2,5,100,0,2,1); // #284
        add("Shroomish","Leech Seed",3,7,2,1,70,7,4,4,47,1,5,85,7,1,1); // #285
        add("Breloom","Leech Seed",3,7,2,1,170,18,6,6,47,1,5,85,7,2,1); // #286
        add("Slakoth","Slack Off",0,22,4,3,130,5,10,8,57,1,4,100,0,1,1); // #287
        add("Vigoroth","Slack Off",0,22,4,3,220,16,10,8,57,1,4,100,0,2,1); // #288
        add("Slaking","Slack Off",0,22,4,3,380,30,14,10,57,1,4,100,8,3,1); // #289
        add("Nincada","Wonder Guard",11,-1,4,3,130,10,10,4,76,1,3,110,0,1,1); // #290
        add("Ninjask","Aerial Ace",11,8,4,3,160,14,10,4,76,1,3,100,0,2,1); // #291
        add("Shedinja","Wonder Guard",11,10,4,3,100,14,0,0,38,1,6,100,0,2,1); // #292
        add("Whismur","Uproar",0,29,3,2,90,6,2,2,47,2,5,90,0,1,1); // #293
        add("Loudred","Uproar",0,29,3,2,150,14,4,4,47,2,5,90,0,2,1); // #294
        add("Exploud","Uproar",0,29,3,2,300,24,6,6,47,2,5,90,8,3,1); // #295
        add("Makuhita","Dynamic Punch",7,28,3,2,80,8,6,6,41,1,6,100,0,1,1); // #296
        add("Hariyama","Dynamic Punch",7,28,3,2,200,23,10,10,41,1,6,80,0,2,1); // #297
        add("Azurill","Play Rough",2,17,1,0,50,5,2,2,41,2,6,100,0,1,2); // #298
        add("Nosepass","Magnet Rise",5,16,2,1,70,5,6,6,38,2,6,100,6,1,4); // #299
        add("Skitty","Disarming Voice",0,17,2,1,65,6,3,2,32,1,6,100,0,1,1); // #300
        add("Delcatty","Disarming Voice",0,17,2,1,160,14,5,3,32,1,6,100,0,2,1); // #301
        add("Sableye","Knock Off",15,10,5,5,200,13,8,8,41,1,6,100,8,3,8); // #302
        add("Mawile","Play Rough",16,17,5,5,180,18,12,12,47,1,5,80,6,3,4); // #303
        add("Aron","Heavy Slam",16,28,1,0,60,4,4,4,41,1,6,100,6,1,4); // #304
        add("Lairon","Heavy Slam",16,28,1,0,100,9,8,6,41,1,6,100,6,2,4); // #305
        add("Aggron","Heavy Slam",16,28,1,0,170,20,12,10,41,1,6,100,6,3,4); // #306
        add("Meditite","Meditate",6,26,4,3,80,9,8,8,51,2,5,100,0,1,8); // #307
        add("Medicham","Meditate",6,26,4,3,160,16,12,12,51,2,5,100,0,2,8); // #308
        add("Electrike","Thunder Fang",4,22,3,2,80,6,6,6,70,1,3,100,5,1,1); // #309
        add("Manectric","Thunder Fang",4,22,3,2,160,14,8,8,70,1,3,100,5,2,1); // #310
        add("Plusle","Link Cable",4,22,5,5,140,12,6,6,61,1,4,70,5,3,1); // #311
        add("Minun","Link Cable",4,22,5,5,140,12,6,6,61,1,4,70,5,3,1); // #312
        add("Volbeat","Tail Glow",11,27,5,5,130,12,4,4,52,1,5,90,8,3,1); // #313
        add("Illumise","Struggle Bug",11,27,5,5,130,12,4,4,52,1,5,90,8,3,1); // #314
        add("Roselia","Petal Dance",3,12,4,3,130,15,4,4,54,3,5,100,3,2,1); // #315
        add("Gulpin","Swallow",12,25,4,3,120,7,4,4,43,1,5,60,0,1,1); // #316
        add("Swalot","Swallow",12,25,4,3,280,15,8,8,43,1,5,60,0,2,1); // #317
        add("Carvanha","Bite",2,15,3,2,85,11,2,4,55,1,5,80,0,1,2); // #318
        add("Sharpedo","Bite",2,15,3,2,170,26,4,6,55,1,5,80,0,2,2); // #319
        add("Wailmer","Dive",2,29,4,3,180,6,4,4,44,1,5,100,0,1,2); // #320
        add("Wailord","Dive",2,29,4,3,400,11,6,6,44,1,5,100,0,2,2); // #321
        add("Numel","Eruption",1,22,4,3,120,10,6,6,38,1,6,120,2,1,1); // #322
        add("Camerupt","Eruption",1,22,4,3,210,15,14,14,38,1,6,120,2,2,1); // #323
        add("Torkoal","Smoke Screen",1,13,5,5,200,17,16,4,31,1,6,110,6,3,1); // #324
        add("Spoink","Bounce",6,22,3,2,100,5,8,12,51,1,5,100,0,1,8); // #325
        add("Grumpig","Bounce",6,22,3,2,240,9,12,20,51,1,5,100,0,2,8); // #326
        add("Spinda","Teeter Dance",0,25,5,5,200,20,10,10,44,1,5,100,3,3,1); // #327
        add("Trapinch","Dragon Tail",11,13,3,2,70,7,2,2,57,1,4,100,6,1,1); // #328
        add("Vibrava","Dragon Tail",9,11,3,2,120,12,5,5,57,1,4,100,6,2,1); // #329
        add("Flygon","Dragon Tail",9,11,3,2,190,23,8,8,57,1,4,100,6,3,1); // #330
        add("Cacnea","Heal Block",3,15,3,2,85,7,6,2,43,1,5,70,1,1,1); // #331
        add("Cacturne","Heal Block",3,15,3,2,180,21,12,4,43,1,5,70,1,2,1); // #332
        add("Swablu","Hyper Voice",17,29,4,3,100,9,6,6,51,2,5,100,0,1,1); // #333
        add("Altaria","Hyper Voice",9,17,4,3,170,15,8,8,51,2,5,100,0,2,1); // #334
        add("Zangoose","Facade",30,0,5,5,250,18,4,4,54,1,5,100,8,3,1); // #335
        add("Seviper","Venoshock",12,28,5,5,180,22,8,4,46,1,5,70,8,3,1); // #336
        add("Lunatone","Cosmic Power Moon",5,6,5,5,200,20,4,4,47,2,5,80,6,3,4); // #337
        add("Solrock","Cosmic Power Sun",5,1,5,5,200,20,4,4,47,2,5,80,6,3,4); // #338
        add("Barboach","Fissure",2,13,4,3,120,9,6,8,44,1,5,90,6,1,2); // #339
        add("Whiscash","Fissure",2,13,4,3,250,22,8,10,44,1,5,90,6,2,2); // #340
        add("Corphish","Crabhammer",19,15,2,1,85,6,6,4,43,1,5,100,0,1,2); // #341
        add("Crawdaunt","Crabhammer",19,15,2,1,160,16,10,6,43,1,5,100,0,2,2); // #342
        add("Baltoy","Confusion",13,6,4,3,80,8,8,8,49,2,5,70,6,1,4); // #343
        add("Claydol","Confusion",13,6,4,3,180,15,12,12,49,2,5,70,6,2,4); // #344
        add("Lileep","Leech Seed",24,3,3,2,70,7,4,4,39,2,6,100,6,1,4); // #345
        add("Cradily","Leech Seed",24,3,3,2,150,22,6,6,39,2,6,100,6,2,4); // #346
        add("Anorith","Harden",24,11,2,1,60,6,4,2,39,1,6,80,6,1,4); // #347
        add("Armaldo","Harden",24,11,2,1,130,16,6,4,39,1,6,80,6,2,4); // #348
        add("Feebas","Splash",2,-1,2,7,60,5,4,8,51,1,5,100,0,1,2); // #349
        add("Milotic","Attract",9,17,2,7,300,15,8,14,51,2,5,80,8,3,1); // #350
        add("Castform","Forecast",20,18,5,5,180,18,6,6,47,2,5,80,8,3,1); // #351
        add("Kecleon","Camouflage",0,-1,5,5,200,22,6,6,38,1,6,100,8,3,1); // #352
        add("Shuppet","Shadow Clone",10,20,4,3,100,10,4,6,46,1,5,120,0,1,8); // #353
        add("Banette","Shadow Clone",10,20,4,3,180,20,6,8,46,1,5,120,0,2,8); // #354
        add("Duskull","Shadow Ball",15,10,2,1,70,6,4,4,39,2,6,100,0,1,8); // #355
        add("Dusclops","Shadow Ball",15,10,2,1,140,12,7,7,39,2,6,100,0,2,8); // #356
        add("Tropius","Growth",3,25,5,5,200,17,8,8,41,1,6,100,8,3,1); // #357
        add("Chimecho","Echo",29,6,5,5,200,15,8,9,46,2,5,80,8,3,1); // #358
        add("Absol","Night Slash",30,15,5,5,200,19,10,10,49,1,5,100,8,3,1); // #359
        add("Wynaut","Counter",6,21,3,2,110,8,4,4,36,1,6,100,0,1,8); // #360
        add("Snorunt","Icy Wind",10,14,4,3,90,9,4,4,60,3,4,100,0,1,8); // #361
        add("Glalie","Icy Wind",10,14,4,3,170,17,6,6,60,3,4,90,0,2,8); // #362
        add("Spheal","Ice Ball",19,14,2,1,80,6,6,4,46,1,5,90,0,1,2); // #363
        add("Sealeo","Ice Ball",19,14,2,1,150,12,6,4,46,1,5,90,0,2,2); // #364
        add("Walrein","Ice Ball",19,14,2,1,300,24,6,6,46,1,5,90,8,3,2); // #365
        add("Clamperl","Iron Defense",24,2,4,3,100,8,10,5,35,1,6,80,6,1,4); // #366
        add("Huntail","Cavernous Chomp",24,2,4,3,140,30,10,6,35,1,6,80,6,2,4); // #367
        add("Gorebyss","Aqua Ring",24,2,4,3,200,18,10,6,35,2,6,80,6,2,4); // #368
        add("Relicanth","Rock Tomb",5,2,5,5,220,13,14,6,43,1,5,100,6,3,4); // #369
        add("Luvdisc","Charm",2,19,5,5,150,14,6,10,56,3,4,60,8,3,2); // #370
        add("Bagon","Dragon Claw",9,28,2,1,70,5,6,6,57,1,4,100,0,1,1); // #371
        add("Shelgon","Dragon Claw",9,28,2,1,130,11,10,6,57,1,4,100,0,2,1); // #372
        add("Salamence","Dragon Claw",9,28,2,1,210,20,10,6,57,1,4,100,8,3,1); // #373
        add("Beldum","Meteor Mash",6,16,4,3,110,5,12,8,47,1,5,100,6,1,8); // #374
        add("Metang","Meteor Mash",6,16,4,3,190,9,18,12,47,1,5,100,6,2,8); // #375
        add("Metagross","Meteor Mash",6,16,4,3,320,20,24,16,47,1,5,100,6,3,8); // #376
        add("Regirock","Stealth Rocks",5,26,5,6,300,25,20,15,41,1,6,100,6,3,4); // #377
        add("Regice","Hail",14,26,5,6,300,25,15,20,41,1,6,100,6,3,2); // #378
        add("Registeel","Iron Head",16,26,5,6,300,25,15,15,41,1,6,100,6,3,4); // #379
        add("Latias","Mist Ball",9,6,5,5,120,9,4,4,60,3,4,100,8,3,1); // #380
        add("Latios","Luster Purge",9,6,5,5,120,9,4,4,60,3,4,100,8,3,1); // #381
        add("Kyogre","Origin Pulse",2,28,5,6,300,18,6,6,54,3,5,100,8,3,2); // #382
        add("Groudon","Precipice Blades",13,28,5,6,300,20,10,6,54,1,5,100,6,3,4); // #383
        add("Rayquaza","Draco Meteor",9,8,5,6,300,27,10,10,55,1,5,120,8,3,1); // #384
        add("Jirachi","Doom Desire",16,6,5,6,220,27,10,10,57,3,4,80,6,3,4); // #385
        add("Deoxys","Psycho Boost",6,18,5,6,250,20,10,10,73,1,3,100,8,3,8); // #386
    }
    private static void initLinks(){
        link(0,1);
        link(1,2);
        link(3,4);
        link(4,5);
        link(6,7);
        link(7,8);
        link(78,79);
        link(73,74);
        link(74,75);
        link(297,182);
        link(182,183);
        link(40,41);
        link(41,168);
        link(178,179);
        link(179,180);
        link(172,34);
        link(34,35);
        link(173,38);
        link(38,39);
        link(9,10);
        link(10,11);
        link(12,13);
        link(13,14);
        link(15,16);
        link(16,17);
        link(186,187);
        link(187,188);
        link(272,273);
        link(273,274);
        link(151,152);
        link(152,153);
        link(154,155);
        link(155,156);
        link(157,158);
        link(158,159);
        link(251,252);
        link(252,253);
        link(254,255);
        link(255,256);
        link(257,258);
        link(258,259);
        link(28,29);
        link(29,30);
        link(31,32);
        link(32,33);
        link(171,24);
        link(24,25);
        link(65,66);
        link(66,67);
        link(115,116);
        link(116,229);
        link(327,328);
        link(328,329);
        link(362,363);
        link(363,364);
        link(303,304);
        link(304,305);
        link(80,81);
        link(110,111);
        link(174,175);
        link(354,355);
        link(269,270);
        link(270,271);
        link(59,60);
        link(60,185);
        link(62,63);
        link(63,64);
        link(91,92);
        link(92,93);
        link(146,147);
        link(147,148);
        link(245,246);
        link(246,247);
        link(286,287);
        link(287,288);
        link(279,280);
        link(280,281);
        link(370,371);
        link(371,372);
        link(373,374);
        link(374,375);
        link(238,124);
        link(239,125);
        link(57,58);
        link(94,207);
        link(128,129);
        link(18,19);
        link(20,21);
        link(132,133);
        link(306,307);
        link(321,322);
        link(68,69);
        link(69,70);
        link(219,220);
        link(360,361);
        link(332,333);
        link(317,318);
        link(51,52);
        link(42,43);
        link(43,44);
        link(346,347);
        link(344,345);
        link(137,138);
        link(139,140);
        link(136,232);
        link(308,309);
        link(352,353);
        link(103,104);
        link(292,293);
        link(293,294);
        link(340,341);
        link(295,296);
        link(76,77);
        link(289,290);
        link(330,331);
        link(112,241);
        link(227,228);
        link(365,366);
        link(237,123);
        link(47,48);
        link(99,100);
        link(217,218);
        link(169,170);
        link(260,261);
        link(284,285);
        link(71,72);
        link(208,209);
        link(36,37);
        link(319,320);
        link(26,27);
        link(49,50);
        link(235,236);
        link(87,88);
        link(203,204);
        link(85,86);
        link(22,23);
        link(162,163);
        link(359,201);
        link(264,265);
        link(265,266);
        link(267,268);
        link(45,46);
        link(55,56);
        link(190,191);
        link(176,177);
        link(89,90);
        link(160,161);
        link(108,109);
        link(119,120);
        link(342,343);
        link(338,339);
        link(83,84);
        link(277,278);
        link(101,102);
        link(262,263);
        link(348,349);
        link(215,216);
        link(166,167);
        link(95,96);
        link(193,194);
        link(53,54);
        link(230,231);
        link(324,325);
        link(117,118);
        link(222,223);
        link(97,98);
        link(315,316);
        link(299,300);
        link(282,283);
        link(164,165);
        link(275,276);
    }
    public static int copies(int sp){int t=tier[sp];return t==1?1:(t==2?3:9);}
    public static int sellValue(int sp){int v=cost[sp]*copies(sp);if(tier[sp]>1)v--;return v;}
    public static boolean isBase(int sp){return fam[sp]==sp;}
    public static boolean famHasType(int sp,int type){int s=fam[sp];while(s>=0){if(t1[s]==type||t2[s]==type)return true;s=evo[s];}return false;}
    public static int countFamilies(){int n=0;for(int i=0;i<N;i++)if(fam[i]==i)n++;return n;}
    public static int synLevel(int count){return SynergyEffects.tier(T_FIRE,count);}
    public static int synLevel(int type,int count){return SynergyEffects.tier(type,count);}
    /** Collection-atlas combatants use the same visible footprint as core raw sprites. */
    public static int visualWidth(int sp){return sp<CORE_N?VisualSize.W[sp]:48;}
    public static int visualHeight(int sp){return sp<CORE_N?VisualSize.H[sp]:56;}
    public static int visualSize(int sp){return Math.max(visualWidth(sp),visualHeight(sp));}
    public static int pickWild(int zone,Rng r){int total=0,bit=1<<zone;for(int i=0;i<N;i++)if((zones[i]&bit)!=0)total+=WILD_W[cost[i]];if(total<=0)return-1;int roll=r.nextInt(total);for(int i=0;i<N;i++)if((zones[i]&bit)!=0){roll-=WILD_W[cost[i]];if(roll<0)return i;}return-1;}
}

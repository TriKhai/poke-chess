package pac;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.microedition.rms.RecordStore;

/** Persistent progress (RecordStore). Collection is stored per base species. */
public final class Save {
    private Save() {}

    private static final String STORE = "pacsave1";
    private static final int VERSION = 14;

    public static int balls = 15;
    /** Premium Explore/Gacha balls. 1 silver = 10 normal; 1 gold = 100 normal. */
    public static int silverBalls = 0, goldBalls = 0;
    public static int best = 0;
    public static int runs = 0;
    public static int wins = 0;
    public static int caught = 0;
    public static boolean[] unlocked = new boolean[Data.N];
    public static boolean cheatMode = false;
    /** -1 not chosen, 0 Khổ dăm, 1 Vạch đích (developer tools). */
    public static int playPath = -1;
    /** 0 Vietnamese (default), 1 English. */
    public static int language = Lang.VI;
    /** 0 = legacy smooth 16 FPS, 1 = battery 10 FPS, 2 = medium 20 FPS, 3 = high 25 FPS. */
    public static int performance = 0;
    /** Explore progression: chosen lead and three real-time camp slots. */
    public static int hero=-1;
    public static int[] camp={-1,-1,-1};
    public static long campStart=0;
    /** Per-slot lock gate and reward clock. lockUntil>0 means the slot is committed. */
    public static long[] campLockUntil={0,0,0},campClaimAt={0,0,0};
    /** Personal profile. Empty name means it has never been confirmed. */
    public static String profileName="";
    /** National Dex number of the displayed partner Pokemon. */
    public static int profileAvatarDex=1;
    /** Bit 0 Normal, bit 1 Thirty rounds, bit 2 Generation 1. */
    public static int modeCleared=0;
    /** Collection-only ownership for Gen 4-9 and the Gacha pity meter. */
    public static boolean[] dexUnlocked=new boolean[CollectionDex.COUNT];
    public static int gachaPoints=0;
    /** Explore type streaks and the current five-minute Legendary outbreak. */
    public static int[] exploreTypeCaught=new int[Data.NT];
    public static int exploreLegendType=-1;
    public static long exploreLegendUntil=0;
    public static int exploreLegendSpecies=-1;
    public static int exploreLegendGen=0;
    /** v1.6.4a: per-map quest progress and persistent Explore statistics. */
    public static int[] exploreQuestCaught=new int[Data.BATTLE_MAX_GEN*Data.NT];
    public static int[] exploreMapCaught=new int[Data.BATTLE_MAX_GEN],exploreMapDefeated=new int[Data.BATTLE_MAX_GEN],exploreMapMissed=new int[Data.BATTLE_MAX_GEN],exploreMapLegends=new int[Data.BATTLE_MAX_GEN];
    public static int[] exploreSpeciesCaught=new int[Data.MAX];
    /** Three milestone bits per Gen map: 25%, 50%, 100% unique species. */
    public static int[] exploreMapRewardMask=new int[Data.BATTLE_MAX_GEN];
    public static int frameDelay(){switch(performance){case 1:return 100;case 2:return 50;case 3:return 40;default:return 60;}}
    public static int targetFps(){switch(performance){case 1:return 10;case 2:return 20;case 3:return 25;default:return 16;}}

    public static void enableCheat() {
        cheatMode = true;
        balls = 9999;
        for (int i = 0; i < Data.N; i++) if (Data.isBase(i)) unlocked[i] = true;
        for (int i = 0; i < dexUnlocked.length; i++) dexUnlocked[i] = true;
        save();
    }

    public static void reset() {
        exploreLegendGen=0;
        balls = 15; silverBalls=0;goldBalls=0;best = 0; runs = 0; wins = 0; caught = 0; performance = 0;hero=-1;campStart=0;camp=new int[]{-1,-1,-1};campLockUntil=new long[]{0,0,0};campClaimAt=new long[]{0,0,0};profileName="";profileAvatarDex=1;modeCleared=0;dexUnlocked=new boolean[CollectionDex.COUNT];gachaPoints=0;exploreTypeCaught=new int[Data.NT];exploreLegendType=-1;exploreLegendUntil=0;exploreLegendSpecies=-1;exploreQuestCaught=new int[Data.BATTLE_MAX_GEN*Data.NT];exploreMapCaught=new int[Data.BATTLE_MAX_GEN];exploreMapDefeated=new int[Data.BATTLE_MAX_GEN];exploreMapMissed=new int[Data.BATTLE_MAX_GEN];exploreMapLegends=new int[Data.BATTLE_MAX_GEN];exploreSpeciesCaught=new int[Data.MAX];exploreMapRewardMask=new int[Data.BATTLE_MAX_GEN];
        unlocked = new boolean[Data.N];
        // starter families
        unlocked[Data.fam[find("Charmander")]] = true;
        unlocked[Data.fam[find("Squirtle")]] = true;
        unlocked[Data.fam[find("Bulbasaur")]] = true;
    }

    /** Erase progression while keeping only language and device FPS preference. */
    public static void resetProgress(){
        int lang=language,perf=performance;reset();language=lang;performance=perf;
        playPath=-1;cheatMode=false;
    }

    private static int find(String n) {
        for (int i = 0; i < Data.N; i++) if (Data.name[i].equals(n)) return i;
        return 0;
    }

    public static boolean has(int sp) {
        return unlocked[Data.fam[sp]];
    }

    public static String displayName(){return profileName.length()==0?Lang.t("Kẻ vô danh","Nameless"):profileName;}
    public static boolean setProfileNameOnce(String value){
        if(profileName.length()!=0||value==null)return false;
        value=value.trim();if(value.length()==0)return false;
        if(value.length()>16)value=value.substring(0,16);
        profileName=value;save();return true;
    }
    public static boolean ownsDex(int dex){if(dex>=1&&dex<=Data.CORE_N)return has(dex-1);int di=collectionIndex(dex);return di>=0&&dexUnlocked[di];}
    public static boolean chooseProfileAvatar(int dex){if(!ownsDex(dex)||(dex<=Data.CORE_N&&inCamp(dex-1)))return false;profileAvatarDex=dex;save();return true;}
    public static boolean unlockFamily(int sp){int f=Data.fam[sp];boolean fresh=!unlocked[f];unlocked[f]=true;for(int i=0;i<Data.N;i++)if(Data.fam[i]==f&&Data.nationalDex(i)>Data.CORE_N){int di=collectionIndex(Data.nationalDex(i));if(di>=0)dexUnlocked[di]=true;}return fresh;}
    /** Camp retired; legacy slots remain readable but never lock owned Pokemon. */
    public static boolean inCamp(int sp){return false;}
    public static boolean usable(int sp){return sp>=0&&sp<Data.N&&has(sp)&&!inCamp(sp);}
    public static boolean unlockDex(int dex){if(dex<=Data.CORE_N)return unlockFamily(dex-1);int di=collectionIndex(dex);if(di<0)return false;boolean fresh=!dexUnlocked[di];dexUnlocked[di]=true;return fresh;}

    public static int familiesUnlocked() {
        int n = 0;
        for (int i = 0; i < Data.N; i++) if (Data.fam[i] == i && unlocked[i]) n++;
        return n;
    }

    public static void load() {
        reset();
        RecordStore rs = null;
        try {
            rs = RecordStore.openRecordStore(STORE, true);
            if (rs.getNumRecords() > 0) {
                byte[] b = rs.getRecord(1);
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));
                int ver = in.readInt();
                if (ver >= 1 && ver <= VERSION) {
                    balls = in.readInt();
                    best = in.readInt();
                    runs = in.readInt();
                    wins = in.readInt();
                    caught = in.readInt();
                    int n = readLength(in);
                    boolean[] u = new boolean[Data.N];
                    for (int i = 0; i < n; i++) {
                        boolean v = in.readBoolean();
                        if (i < Data.N) u[i] = v;
                    }
                    unlocked = u;
                    language = ver >= 2 ? in.readInt() : Lang.VI;
                    if (language != Lang.EN) language = Lang.VI;
                    playPath = ver >= 3 ? in.readInt() : -1;
                    performance = ver >= 4 ? in.readInt() : 0;
                    if (performance < 0 || performance > 3) performance = 0;
                    if(ver>=5){hero=in.readInt();for(int i=0;i<3;i++)camp[i]=in.readInt();campStart=in.readLong();}
                    if(ver>=6){profileName=in.readUTF();profileAvatarDex=in.readInt();}
                    if(ver>=7)modeCleared=in.readInt();
                    if(ver>=8){int dn=readLength(in);for(int i=0;i<dn;i++){boolean v=in.readBoolean();if(i<dexUnlocked.length)dexUnlocked[i]=v;}gachaPoints=in.readInt();if(gachaPoints<0)gachaPoints=0;if(gachaPoints>99)gachaPoints=99;}
                    if(ver>=9){for(int i=0;i<3;i++)campLockUntil[i]=in.readLong();for(int i=0;i<3;i++)campClaimAt[i]=in.readLong();}
                    else if(campStart>0)for(int i=0;i<3;i++)if(camp[i]>=0){campLockUntil[i]=campStart+3600L;campClaimAt[i]=campStart;}
                    if(ver>=10){int tn=readLength(in);for(int i=0;i<tn;i++){int v=in.readInt();if(i<exploreTypeCaught.length)exploreTypeCaught[i]=Math.max(0,v);}exploreLegendType=in.readInt();exploreLegendUntil=in.readLong();if(exploreLegendType<-1||exploreLegendType>=Data.NT){exploreLegendType=-1;exploreLegendUntil=0;}}
                    if(ver>=11){exploreLegendSpecies=in.readInt();if(exploreLegendSpecies<-1||exploreLegendSpecies>=Data.N)exploreLegendSpecies=-1;}
                    if(ver>=12){readInts(in,exploreQuestCaught);readInts(in,exploreMapCaught);readInts(in,exploreMapDefeated);readInts(in,exploreMapMissed);readInts(in,exploreMapLegends);readInts(in,exploreSpeciesCaught);}
                    else for(int t=0;t<Data.NT;t++)exploreQuestCaught[t]=exploreTypeCaught[t];
                    if(ver>=13){silverBalls=Math.max(0,in.readInt());goldBalls=Math.max(0,in.readInt());readInts(in,exploreMapRewardMask);}
                    exploreLegendGen=ver>=14?in.readInt():(exploreLegendSpecies>=0?Data.generation(exploreLegendSpecies):0);
                    if(exploreLegendGen<0||exploreLegendGen>Data.BATTLE_MAX_GEN)ExploreRules.clearLegendary();
                    if(profileName==null)profileName="";
                    if(!ownsDex(profileAvatarDex))profileAvatarDex=firstOwnedDex();
                    cheatMode = playPath == 1;
                    if(cheatMode){for(int i=0;i<Data.N;i++)if(Data.isBase(i))unlocked[i]=true;for(int i=0;i<dexUnlocked.length;i++)dexUnlocked[i]=true;}
                }
            }
        } catch (Exception e) {
            reset();
        } finally {
            if (rs != null) {
                try { rs.closeRecordStore(); } catch (Exception e) { }
            }
        }
        HistoryStore.load();
    }

    public static void save() {
        RecordStore rs = null;
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bo);
            out.writeInt(VERSION);
            out.writeInt(balls);
            out.writeInt(best);
            out.writeInt(runs);
            out.writeInt(wins);
            out.writeInt(caught);
            out.writeInt(Data.N);
            for (int i = 0; i < Data.N; i++) out.writeBoolean(unlocked[i]);
            out.writeInt(language);
            out.writeInt(playPath);
            out.writeInt(performance);
            out.writeInt(hero);for(int i=0;i<3;i++)out.writeInt(camp[i]);out.writeLong(campStart);
            out.writeUTF(profileName);out.writeInt(profileAvatarDex);
            out.writeInt(modeCleared);
            out.writeInt(dexUnlocked.length);for(int i=0;i<dexUnlocked.length;i++)out.writeBoolean(dexUnlocked[i]);out.writeInt(gachaPoints);
            for(int i=0;i<3;i++)out.writeLong(campLockUntil[i]);for(int i=0;i<3;i++)out.writeLong(campClaimAt[i]);
            out.writeInt(exploreTypeCaught.length);for(int i=0;i<exploreTypeCaught.length;i++)out.writeInt(exploreTypeCaught[i]);out.writeInt(exploreLegendType);out.writeLong(exploreLegendUntil);out.writeInt(exploreLegendSpecies);writeInts(out,exploreQuestCaught);writeInts(out,exploreMapCaught);writeInts(out,exploreMapDefeated);writeInts(out,exploreMapMissed);writeInts(out,exploreMapLegends);writeInts(out,exploreSpeciesCaught);out.writeInt(silverBalls);out.writeInt(goldBalls);writeInts(out,exploreMapRewardMask);
            out.writeInt(exploreLegendGen);
            out.flush();
            byte[] b = bo.toByteArray();
            rs = RecordStore.openRecordStore(STORE, true);
            if (rs.getNumRecords() == 0) rs.addRecord(b, 0, b.length);
            else rs.setRecord(1, b, 0, b.length);
        } catch (Exception e) {
            // storage unavailable: ignore, game still works
        } finally {
            if (rs != null) {
                try { rs.closeRecordStore(); } catch (Exception e) { }
            }
        }
    }

    static int readLength(DataInputStream in)throws java.io.IOException{int n=in.readInt();if(n<0||n>4096)throw new java.io.IOException("invalid save array length");return n;}
    private static int firstOwnedDex(){for(int i=0;i<Data.N;i++)if(has(i))return Data.nationalDex(i);return 1;}
    private static int collectionIndex(int dex){for(int i=0;i<CollectionDex.COUNT;i++)if(CollectionDex.DEX[i]==dex)return i;return -1;}
    private static void writeInts(DataOutputStream out,int[] a)throws java.io.IOException{out.writeInt(a.length);for(int i=0;i<a.length;i++)out.writeInt(a[i]);}
    private static void readInts(DataInputStream in,int[] a)throws java.io.IOException{int n=readLength(in);for(int i=0;i<n;i++){int v=in.readInt();if(i<a.length)a[i]=Math.max(0,v);}}
}

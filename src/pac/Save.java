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
    private static final int VERSION = 8;

    public static int balls = 15;
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
    /** Personal profile. Empty name means it has never been confirmed. */
    public static String profileName="";
    /** National Dex number of the displayed partner Pokemon. */
    public static int profileAvatarDex=1;
    /** Bit 0 Normal, bit 1 Thirty rounds, bit 2 Generation 1. */
    public static int modeCleared=0;
    /** Collection-only ownership for Gen 4-9 and the Gacha pity meter. */
    public static boolean[] dexUnlocked=new boolean[CollectionDex.COUNT];
    public static int gachaPoints=0;
    public static int frameDelay(){switch(performance){case 1:return 100;case 2:return 50;case 3:return 40;default:return 60;}}
    public static int targetFps(){switch(performance){case 1:return 10;case 2:return 20;case 3:return 25;default:return 16;}}

    public static void enableCheat() {
        cheatMode = true;
        balls = 9999;
        for (int i = 0; i < Data.N; i++) if (Data.isBase(i)) unlocked[i] = true;
        save();
    }

    public static void reset() {
        balls = 15; best = 0; runs = 0; wins = 0; caught = 0; performance = 0;hero=-1;campStart=0;camp=new int[]{-1,-1,-1};profileName="";profileAvatarDex=1;modeCleared=0;dexUnlocked=new boolean[CollectionDex.COUNT];gachaPoints=0;
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
    public static boolean ownsDex(int dex){if(dex>=1&&dex<=Data.N)return has(dex-1);int di=collectionIndex(dex);return di>=0&&dexUnlocked[di];}
    public static boolean chooseProfileAvatar(int dex){if(!ownsDex(dex))return false;profileAvatarDex=dex;save();return true;}
    public static boolean unlockFamily(int sp){int f=Data.fam[sp];boolean fresh=!unlocked[f];unlocked[f]=true;return fresh;}
    public static boolean unlockDex(int dex){if(dex<=Data.N)return unlockFamily(dex-1);int di=collectionIndex(dex);if(di<0)return false;boolean fresh=!dexUnlocked[di];dexUnlocked[di]=true;return fresh;}

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
                    int n = in.readInt();
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
                    if(ver>=8){int dn=in.readInt();for(int i=0;i<dn;i++){boolean v=in.readBoolean();if(i<dexUnlocked.length)dexUnlocked[i]=v;}gachaPoints=in.readInt();if(gachaPoints<0)gachaPoints=0;if(gachaPoints>99)gachaPoints=99;}
                    if(profileName==null)profileName="";
                    if(!ownsDex(profileAvatarDex))profileAvatarDex=firstOwnedDex();
                    cheatMode = playPath == 1;
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

    private static int firstOwnedDex(){for(int i=0;i<Data.N;i++)if(has(i))return i+1;return 1;}
    private static int collectionIndex(int dex){for(int i=0;i<CollectionDex.COUNT;i++)if(CollectionDex.DEX[i]==dex)return i;return -1;}
}

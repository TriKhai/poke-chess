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
    private static final int VERSION = 3;

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

    public static void enableCheat() {
        cheatMode = true;
        balls = 9999;
        for (int i = 0; i < Data.N; i++) if (Data.isBase(i)) unlocked[i] = true;
        save();
    }

    public static void reset() {
        balls = 15; best = 0; runs = 0; wins = 0; caught = 0;
        unlocked = new boolean[Data.N];
        // starter families
        unlocked[Data.fam[find("Charmander")]] = true;
        unlocked[Data.fam[find("Squirtle")]] = true;
        unlocked[Data.fam[find("Bulbasaur")]] = true;
    }

    private static int find(String n) {
        for (int i = 0; i < Data.N; i++) if (Data.name[i].equals(n)) return i;
        return 0;
    }

    public static boolean has(int sp) {
        return unlocked[Data.fam[sp]];
    }

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
}

package pac;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.microedition.rms.RecordStore;

/** Versioned run snapshot kept separate from the legacy progression save. */
public final class RunStorage {
    private RunStorage(){}
    private static final String STORE="pacrun1";
    private static final int VERSION=2;

    public static boolean has(){RecordStore rs=null;try{rs=RecordStore.openRecordStore(STORE,false);return rs.getNumRecords()>0;}catch(Exception e){return false;}finally{close(rs);}}
    public static void clear(){try{RecordStore.deleteRecordStore(STORE);}catch(Exception e){}}

    private static void ints(DataOutputStream out,int[] a)throws Exception{out.writeInt(a.length);for(int i=0;i<a.length;i++)out.writeInt(a[i]);}
    private static void readInts(DataInputStream in,int[] a)throws Exception{int n=in.readInt();if(n<0||n>100000)throw new Exception("bad array");for(int i=0;i<n;i++){int v=in.readInt();if(i<a.length)a[i]=v;}}

    static byte[] encode(Run r){try{
        ByteArrayOutputStream bo=new ByteArrayOutputStream();DataOutputStream out=new DataOutputStream(bo);
        out.writeInt(VERSION);out.writeInt(r.mode);out.writeInt(r.rng.state());out.writeInt(r.round);out.writeInt(r.hp);out.writeInt(r.gold);out.writeInt(r.level);out.writeInt(r.xp);out.writeInt(r.streak);out.writeBoolean(r.shopLocked);
        ints(out,r.board);ints(out,r.bench);ints(out,r.shop);ints(out,r.pool);ints(out,r.equip);ints(out,r.inventory);ints(out,r.enemy);ints(out,r.enemyEquip);
        out.writeInt(r.enemyScale);out.writeUTF(r.enemyName==null?"":r.enemyName);ints(out,r.resultPath);out.writeInt(r.resultCount);
        out.writeInt(r.draftStage);out.writeInt(r.poolType);ints(out,r.poolTypes);out.flush();return bo.toByteArray();
    }catch(Exception e){return null;}}

    static Run decode(byte[] b){try{DataInputStream in=new DataInputStream(new ByteArrayInputStream(b));int version=in.readInt();if(version<1||version>VERSION)return null;
        int mode=in.readInt(),rngState=in.readInt();Run r=new Run(1,mode);r.round=in.readInt();r.hp=in.readInt();r.gold=in.readInt();r.level=in.readInt();r.xp=in.readInt();r.streak=in.readInt();r.shopLocked=in.readBoolean();
        readInts(in,r.board);readInts(in,r.bench);readInts(in,r.shop);readInts(in,r.pool);readInts(in,r.equip);readInts(in,r.inventory);readInts(in,r.enemy);readInts(in,r.enemyEquip);
        r.enemyScale=in.readInt();r.enemyName=in.readUTF();readInts(in,r.resultPath);r.resultCount=in.readInt();if(r.resultCount<0||r.resultCount>r.resultPath.length)throw new Exception("bad results");
        if(version>=2){r.draftStage=in.readInt();r.poolType=in.readInt();readInts(in,r.poolTypes);}else{r.draftStage=2;r.poolType=-1;}
        r.rng.restore(rngState);return r;
    }catch(Exception e){return null;}}

    public static void save(Run r){if(r==null||r.over){clear();return;}RecordStore rs=null;try{byte[] b=encode(r);if(b==null)return;rs=RecordStore.openRecordStore(STORE,true);if(rs.getNumRecords()==0)rs.addRecord(b,0,b.length);else rs.setRecord(1,b,0,b.length);}catch(Exception e){}finally{close(rs);}}
    public static Run load(){RecordStore rs=null;try{rs=RecordStore.openRecordStore(STORE,false);Run r=decode(rs.getRecord(1));if(r==null)clear();return r;}catch(Exception e){clear();return null;}finally{close(rs);}}
    private static void close(RecordStore rs){if(rs!=null)try{rs.closeRecordStore();}catch(Exception e){}}
}

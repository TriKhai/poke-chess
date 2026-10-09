package pac;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.microedition.rms.RecordStore;

/** Last five completed runs: outcome graph, final formation and held items. */
public final class HistoryStore {
    private HistoryStore(){}
    private static final String STORE="pachist1";private static final int VERSION=3,MAX=5;
    public static int[][] forms=new int[MAX][9],flags=new int[MAX][9];
    public static int count;public static int[] mode=new int[MAX],round=new int[MAX],hp=new int[MAX],gold=new int[MAX],resultCount=new int[MAX];
    public static boolean[] victory=new boolean[MAX];public static int[][] path=new int[MAX][40],team=new int[MAX][9],items=new int[MAX][27];
    static{reset();}
    private static void reset(){count=0;for(int h=0;h<MAX;h++){for(int i=0;i<9;i++){team[h][i]=-1;forms[h][i]=0;flags[h][i]=0;}for(int i=0;i<40;i++)path[h][i]=0;for(int i=0;i<27;i++)items[h][i]=-1;}}
    public static void clear(){try{RecordStore.deleteRecordStore(STORE);}catch(Exception e){}reset();}
    public static void add(Run r){for(int h=Math.min(count,MAX-1);h>0;h--){mode[h]=mode[h-1];round[h]=round[h-1];hp[h]=hp[h-1];gold[h]=gold[h-1];victory[h]=victory[h-1];resultCount[h]=resultCount[h-1];copy(path[h-1],path[h]);copy(team[h-1],team[h]);copy(items[h-1],items[h]);copy(forms[h-1],forms[h]);copy(flags[h-1],flags[h]);}
        mode[0]=r.mode;round[0]=r.round;hp[0]=r.hp;gold[0]=r.gold;victory[0]=r.victory;resultCount[0]=r.resultCount;copy(r.resultPath,path[0]);for(int i=0;i<9;i++){team[0][i]=-1;forms[0][i]=0;flags[0][i]=0;}for(int i=0;i<27;i++)items[0][i]=-1;
        int slot=0;for(int p=0;p<Run.BOARD&&slot<9;p++)if(r.board[p]>=0){team[0][slot]=r.board[p];forms[0][slot]=r.specialFormAt(p);flags[0][slot]=(r.isShiny(p)?1:0)|(r.isMega(p)?2:0);for(int s=0;s<3;s++)items[0][slot*3+s]=r.equip[p*3+s];slot++;}if(count<MAX)count++;save();}
    private static void copy(int[] a,int[] b){for(int i=0;i<b.length;i++)b[i]=i<a.length?a[i]:0;}
    public static void load(){reset();RecordStore rs=null;try{rs=RecordStore.openRecordStore(STORE,false);decode(rs.getRecord(1));}catch(Exception e){reset();}finally{close(rs);}}
    static void decode(byte[] bytes)throws java.io.IOException{reset();try{DataInputStream in=new DataInputStream(new ByteArrayInputStream(bytes));int version=in.readInt();if(version<1||version>VERSION)throw new java.io.IOException("history version");int oldPath=version==1?30:40;count=in.readInt();if(count<0||count>MAX)throw new java.io.IOException("history count");for(int h=0;h<count;h++){mode[h]=in.readInt();round[h]=in.readInt();hp[h]=in.readInt();gold[h]=in.readInt();victory[h]=in.readBoolean();resultCount[h]=in.readInt();if(resultCount[h]<0||resultCount[h]>oldPath)throw new java.io.IOException("history path");for(int i=0;i<oldPath;i++)path[h][i]=in.readInt();for(int i=0;i<9;i++){team[h][i]=in.readInt();if(team[h][i]<-1||team[h][i]>=Data.N)throw new java.io.IOException("history species");}for(int i=0;i<27;i++){items[h][i]=in.readInt();if(items[h][i]<-1||items[h][i]>=ItemData.ID.length)throw new java.io.IOException("history item");}if(version>=3)for(int i=0;i<9;i++){forms[h][i]=in.readInt();flags[h][i]=in.readInt();if(forms[h][i]<0||forms[h][i]>SpecialFormData.MAX_FORM||flags[h][i]<0||flags[h][i]>3)throw new java.io.IOException("history form");}}}catch(java.io.IOException e){reset();throw e;}}
    static byte[] encode()throws java.io.IOException{ByteArrayOutputStream bo=new ByteArrayOutputStream();DataOutputStream out=new DataOutputStream(bo);out.writeInt(VERSION);out.writeInt(count);for(int h=0;h<count;h++){out.writeInt(mode[h]);out.writeInt(round[h]);out.writeInt(hp[h]);out.writeInt(gold[h]);out.writeBoolean(victory[h]);out.writeInt(resultCount[h]);for(int i=0;i<40;i++)out.writeInt(path[h][i]);for(int i=0;i<9;i++)out.writeInt(team[h][i]);for(int i=0;i<27;i++)out.writeInt(items[h][i]);for(int i=0;i<9;i++){out.writeInt(forms[h][i]);out.writeInt(flags[h][i]);}}out.flush();return bo.toByteArray();}
    public static void save(){RecordStore rs=null;try{byte[] b=encode();rs=RecordStore.openRecordStore(STORE,true);if(rs.getNumRecords()==0)rs.addRecord(b,0,b.length);else rs.setRecord(1,b,0,b.length);}catch(Exception e){}finally{close(rs);}}
    private static void close(RecordStore rs){if(rs!=null)try{rs.closeRecordStore();}catch(Exception e){}}
}

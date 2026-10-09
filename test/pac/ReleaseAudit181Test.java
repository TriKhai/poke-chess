package pac;
import java.io.*;
/** Release regressions: history identity, compatibility, malformed records and map isolation. */
public final class ReleaseAudit181Test {
 private static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 public static void run(){try{
  HistoryStore.count=1;for(int i=0;i<9;i++){HistoryStore.team[0][i]=-1;HistoryStore.forms[0][i]=0;HistoryStore.flags[0][i]=0;}for(int i=0;i<27;i++)HistoryStore.items[0][i]=-1;HistoryStore.resultCount[0]=0;
  HistoryStore.team[0][0]=5;HistoryStore.flags[0][0]=3;HistoryStore.team[0][1]=0;HistoryStore.forms[0][1]=1;byte[] bytes=HistoryStore.encode();HistoryStore.decode(bytes);check(HistoryStore.flags[0][0]==3&&HistoryStore.forms[0][1]==1,"history lost transformed identity");
  byte[] old=new byte[bytes.length-72];System.arraycopy(bytes,0,old,0,old.length);old[3]=2;HistoryStore.decode(old);check(HistoryStore.count==1&&HistoryStore.flags[0][0]==0&&HistoryStore.forms[0][1]==0,"old history migration");
  boolean rejected=false;try{HistoryStore.decode(new byte[]{0,0,0,3,0,0,0,6});}catch(IOException e){rejected=true;}check(rejected&&HistoryStore.count==0,"corrupt history retained partial state");
  Save.reset();int sp=-1;for(int i=0;i<Data.N;i++)if(Data.isBase(i)&&Data.generation(i)==1&&Data.category[i]==6){sp=i;break;}check(sp>=0,"no Gen1 legendary");Save.exploreLegendType=Data.t1[sp];Save.exploreLegendGen=1;Save.exploreLegendSpecies=sp;Save.exploreLegendUntil=1000;
  check(ExploreRules.legendaryActive(1,100)&&!ExploreRules.legendaryActive(2,100),"outbreak leaked into another map");check(ExploreRules.pickLegendary(2,new Rng(7))==-1&&Save.exploreLegendSpecies==sp&&Save.exploreLegendGen==1,"switching Gen changed active outbreak");check(!ExploreRules.legendaryActive(1,1000)&&Save.exploreLegendGen==0,"expired outbreak retained Gen");Save.reset();
  int[] badLengths={-1,4097,2147483647};for(int i=0;i<badLengths.length;i++){ByteArrayOutputStream bo=new ByteArrayOutputStream();DataOutputStream out=new DataOutputStream(bo);out.writeInt(badLengths[i]);boolean bad=false;try{Save.readLength(new DataInputStream(new ByteArrayInputStream(bo.toByteArray())));}catch(IOException e){bad=true;}check(bad,"unbounded save array length");}
  System.out.println("ReleaseAudit181Test OK: history forms/Shiny/Mega, v2 migration, corrupt records, bounded save lengths, Gen-bound outbreak");
 }catch(IOException e){throw new RuntimeException(e.toString());}}
}

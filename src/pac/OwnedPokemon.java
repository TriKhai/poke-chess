package pac;
/** Shared compact roster queries for Camp and Farm pickers. */
public final class OwnedPokemon{
 private OwnedPokemon(){}
 public static boolean eligible(int sp,int allow){return sp>=0&&sp<Data.N&&Data.isBase(sp)&&Save.has(sp)&&(!Save.inCamp(sp)||(allow>=0&&Data.fam[sp]==Data.fam[allow]));}
 /** Builds one stable picker snapshot instead of rescanning the dex per visible cell. */
 public static int fill(int[] out,int allow){int n=0;for(int i=0;i<Data.N&&n<out.length;i++)if(eligible(i,allow))out[n++]=i;return n;}
 public static int count(int allow){int n=0;for(int i=0;i<Data.N;i++)if(eligible(i,allow))n++;return n;}
 public static int at(int index,int allow){for(int i=0;i<Data.N;i++)if(eligible(i,allow)&&index--==0)return i;return 0;}
}

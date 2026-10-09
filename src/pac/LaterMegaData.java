package pac;

/** Complete Gen 4-9 Mega resources audited in the supplied SpriteCollab tree. */
public final class LaterMegaData {
    private LaterMegaData(){}
    public static final int[] DEX={448,475,491,530,560,604,670,691,701,718,719,780,807,978,80};
    private static final int[] T1={7,6,15,13,15,4,17,12,7,9,5,0,4,9,2};
    private static final int[] T2={16,7,-1,16,7,-1,-1,9,8,13,17,9,-1,2,6};
    public static int index(int sp){int dex=Data.nationalDex(sp);for(int i=0;i<DEX.length;i++)if(DEX[i]==dex)return i;return -1;}
    public static int type1(int sp){int i=index(sp);return i<0?Data.t1[sp]:T1[i];}
    public static int type2(int sp){int i=index(sp);return i<0?Data.t2[sp]:T2[i];}
    public static boolean shiny(int sp){return index(sp)>=0&&Data.nationalDex(sp)!=491;}
}

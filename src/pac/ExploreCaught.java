package pac;

/** Stable species-sorted map catch list; duplicates are displayed as counts. */
public final class ExploreCaught {
 private ExploreCaught(){}
 public static int count(int gen){int n=0;for(int sp=0;sp<Data.N;sp++)if(Data.generation(sp)==gen&&Save.exploreSpeciesCaught[sp]>0)n++;return n;}
 public static int at(int gen,int rank){if(rank<0)return-1;for(int sp=0;sp<Data.N;sp++)if(Data.generation(sp)==gen&&Save.exploreSpeciesCaught[sp]>0&&rank--==0)return sp;return-1;}
}

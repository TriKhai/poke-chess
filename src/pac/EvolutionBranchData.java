package pac;

/** Species branches reuse the regional choice popup, without pretending to be forms. */
public final class EvolutionBranchData {
    private EvolutionBranchData(){}
    public static int alternate(int sp){int d=Data.nationalDex(sp);return d==45?181:d==62?185:-1;}
    private static final int[] EEVEE={134,135,136,196,197,470,471,700};
    private static final int[] SLOW={80,199};
    private static final int[] HITMON={106,107,237};
    private static final int[] WURMPLE={266,268},KIRLIA={282,475},SNORUNT={362,478},CLAMPERL={367,368},NINCADA={291,292};
    private static int[] branches(int sp){int d=Data.nationalDex(sp);return d==134?EEVEE:d==80?SLOW:d==106?HITMON:d==266?WURMPLE:d==282?KIRLIA:d==362?SNORUNT:d==367?CLAMPERL:d==291?NINCADA:CanonicalEvolutionData.branches(sp);}
    public static int species(int dex){for(int i=0;i<Data.N;i++)if(Data.nationalDex(i)==dex)return i;return -1;}
    public static int count(int sp){int[] b=branches(sp);if(b==null)return alternate(sp)>=0?2:EvolutionVariantData.choiceCount(sp);int n=0;for(int i=0;i<b.length;i++){int s=species(b[i]);if(s>=0)n+=EvolutionVariantData.choiceCount(s);}return n;}
    public static int choiceSpecies(int sp,int choice){int[] b=branches(sp);if(b==null)return choice==1&&alternate(sp)>=0?alternate(sp):sp;for(int i=0;i<b.length;i++){int s=species(b[i]);if(s<0)continue;int n=EvolutionVariantData.choiceCount(s);if(choice<n)return s;choice-=n;}return sp;}
    public static int choiceForm(int sp,int choice){int[] b=branches(sp);if(b==null)return alternate(sp)>=0?0:EvolutionVariantData.choiceForm(sp,choice);for(int i=0;i<b.length;i++){int s=species(b[i]);if(s<0)continue;int n=EvolutionVariantData.choiceCount(s);if(choice<n)return EvolutionVariantData.choiceForm(s,choice);choice-=n;}return 0;}
    public static boolean available(int sp){return count(sp)>1;}
    /** Base species with a regional counterpart choose before purchase, not by merging triples. */
    public static boolean purchaseChoice(int sp){return sp>=0&&Data.isBase(sp)&&EvolutionVariantData.standalone(sp);}
    public static int defaultNext(int sp){int d=Data.nationalDex(sp);return d==44?44:d==61?61:Data.evo[sp];}
    public static int synergyBranch(int sp){int d=Data.nationalDex(sp);if(d==182)return 10001;if(d==186)return 10002;if(d==199)return 10003;if(d==292||d==107||d==237||d==268||d==269||d==475||d==478||d==368)return 10000+(d==269?268:d);for(int i=1;i<EEVEE.length;i++)if(d==EEVEE[i])return 10000+d;return CanonicalEvolutionData.synergyBranch(sp);}
}

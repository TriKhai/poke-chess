package pac;

/** Regional final forms chosen immediately after a three-copy evolution. */
public final class EvolutionVariantData{
    private EvolutionVariantData(){}
    private static final int[] DEX={20,26,28,38,51,53,59,76,78,80,89,101,103,105,110,122,157,199,503,549,571,628,706,713,724,144,145,146,194,211,215,222};
    private static final int[] FORM={SpecialFormData.ALOLA_RATICATE,SpecialFormData.ALOLA_RAICHU,SpecialFormData.ALOLA_SANDSLASH,SpecialFormData.ALOLA_NINETALES,SpecialFormData.ALOLA_DUGTRIO,SpecialFormData.ALOLA_PERSIAN,SpecialFormData.HISUI_ARCANINE,SpecialFormData.ALOLA_GOLEM,SpecialFormData.GALAR_RAPIDASH,SpecialFormData.GALAR_SLOWBRO,SpecialFormData.ALOLA_MUK,SpecialFormData.HISUI_ELECTRODE,SpecialFormData.ALOLA_EXEGGUTOR,SpecialFormData.ALOLA_MAROWAK,SpecialFormData.GALAR_WEEZING,SpecialFormData.GALAR_MR_MIME,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44};
    public static int formFor(int sp){int d=Data.nationalDex(sp);if(d==263)return 74;if(d==264)return 75;for(int i=0;i<DEX.length;i++)if(DEX[i]==d)return FORM[i];return LaterFormData.regionalFor(sp);}
    public static int choiceCount(int sp){int f=formFor(sp),n=1+(f>0?1:0);for(int i=0;i<LaterFormData.choiceCount(sp);i++)if(LaterFormData.choice(sp,i)!=f)n++;return n;}
    public static int choiceForm(int sp,int n){if(n==0)return 0;int f=formFor(sp);if(f>0){if(n==1)return f;n--;}for(int i=0;i<LaterFormData.choiceCount(sp);i++){int next=LaterFormData.choice(sp,i);if(next==f)continue;if(--n==0)return next;}return 0;}
    public static boolean validChoiceForm(int sp,int f){for(int i=0;i<choiceCount(sp);i++)if(choiceForm(sp,i)==f)return true;return false;}
    public static boolean available(int sp){return formFor(sp)!=0;}
    /** Standalone species without a configured predecessor can still unlock their regional form. */
    public static boolean standalone(int sp){if(!available(sp))return false;for(int i=0;i<Data.N;i++)if(Data.evo[i]==sp)return false;return true;}
    private static final String[] KEYS={"0020-0001","0026-0001","0028-0001","0038-0001","0051-0001","0053-0001","0059-0001","0076-0001","0078-0001","0080-0001","0089-0001","0101-0001","0103-0001","0105-0001","0110-0001","0122-0001","0157-0001","0199-0001","0503-0001","0549-0001","0571-0001","0628-0001","0706-0001","0713-0001","0724-0001","0144-0001","0145-0001","0146-0001","0194-0002","0211-0001","0215-0001","0222-0001"};
    private static final String[] NAMES={"Raticate Alola","Raichu Alola","Sandslash Alola","Ninetales Alola","Dugtrio Alola","Persian Alola","Arcanine Hisui","Golem Alola","Rapidash Galar","Slowbro Galar","Muk Alola","Electrode Hisui","Exeggutor Alola","Marowak Alola","Weezing Galar","Mr. Mime Galar","Typhlosion Hisui","Slowking Galar","Samurott Hisui","Lilligant Hisui","Zoroark Hisui","Braviary Hisui","Goodra Hisui","Avalugg Hisui","Decidueye Hisui","Articuno Galar","Zapdos Galar","Moltres Galar","Wooper Paldea","Qwilfish Hisui","Sneasel Hisui","Corsola Galar"};
    private static final int[] T1={15,4,14,14,13,15,1,5,6,12,12,4,3,1,12,14,1,12,2,3,0,6,16,14,3,6,7,15,12,15,7,10};
    private static final int[] T2={0,6,16,17,16,-1,5,4,17,6,15,3,9,10,17,6,10,6,15,7,10,8,9,5,7,8,8,8,13,12,12,-1};
    public static String key(int form){return LaterFormData.contains(form)?LaterFormData.key(form):GenThreeFormData.regional(form)?GenThreeFormData.key(form):KEYS[form-13];}
    public static String name(int form){return LaterFormData.contains(form)?LaterFormData.name(form):GenThreeFormData.regional(form)?GenThreeFormData.name(form):NAMES[form-13];}
    public static int type1(int sp,int form){return LaterFormData.contains(form)?LaterFormData.type1(sp,form):GenThreeFormData.contains(form)?GenThreeFormData.type1(sp,form):SpecialFormData.evolutionVariant(form)?T1[form-13]:Data.t1[sp];}
    public static int type2(int sp,int form){return LaterFormData.contains(form)?LaterFormData.type2(sp,form):form==SpecialFormData.PRIMAL_GROUDON?Data.T_FIRE:GenThreeFormData.contains(form)?GenThreeFormData.type2(sp,form):SpecialFormData.evolutionVariant(form)?T2[form-13]:Data.t2[sp];}
}

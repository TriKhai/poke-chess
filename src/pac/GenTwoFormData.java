package pac;

/** Cosmetic/source-backed item forms: these never add another synergy identity. */
public final class GenTwoFormData {
    private GenTwoFormData(){}
    public static final int SPIKY_PICHU=45,SHADOW_LUGIA=46,UNOWN_B=47,UNOWN_QUESTION=73;
    public static boolean cosmetic(int form){return form>=45&&form<=73;}
    public static String key(int form){if(form==45)return "0172-0001";if(form==46)return "0249-0001";int n=form-46;return "0201-"+(n<10?"000":"00")+n;}
    public static String name(int form){if(form==45)return "Pichu Spiky";if(form==46)return "Shadow Lugia";return "Unown "+(form==72?"!":form==73?"?":""+(char)('B'+form-47));}
    public static int first(int sp){int d=Data.nationalDex(sp);return d==172?45:d==249?46:d==201?47:0;}
    public static int next(int sp,int current){int first=first(sp);if(first==0)return 0;if(Data.nationalDex(sp)==201)return current>=47&&current<73?current+1:47;return current==0?first:0;}
    public static int evolved(int sp,int form){if(LaterFormData.contains(form))return LaterFormData.evolved(sp,form);if(GenThreeFormData.regional(form))return GenThreeFormData.evolved(sp,form);int dex=form==41?980:form==42?904:form==43?903:form==44?864:0;if(dex==0)return Data.evo[sp]>=0?Data.evo[sp]:sp;for(int i=0;i<Data.N;i++)if(Data.nationalDex(i)==dex)return i;return sp;}
}

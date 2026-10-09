package pac;

/** Complete Gen 3 regional/weather/cosmetic animations; stable save IDs. */
public final class GenThreeFormData {
    private GenThreeFormData(){}
    public static boolean regional(int form){return form==74||form==75;}
    public static boolean contains(int form){return form>=74&&form<=79;}
    public static String key(int form){return form==74?"0263-0001":form==75?"0264-0001":form==76?"0351-0001":form==77?"0351-0002":form==78?"0351-0003":"0352-0001";}
    public static String name(int form){return form==74?"Zigzagoon Galar":form==75?"Linoone Galar":form==76?"Castform Sunny":form==77?"Castform Rainy":form==78?"Castform Snowy":"Kecleon Purple";}
    public static int first(int sp){int d=Data.nationalDex(sp);return d==351?76:d==352?79:0;}
    public static int next(int sp,int current){return Data.nationalDex(sp)==351?(current>=76&&current<78?current+1:76):current==0?first(sp):0;}
    public static int type1(int sp,int form){return regional(form)?Data.T_DARK:form==76?Data.T_FIRE:form==77?Data.T_WATER:form==78?Data.T_ICE:Data.t1[sp];}
    public static int type2(int sp,int form){return regional(form)?Data.T_NORMAL:form>=76&&form<=78?-1:Data.t2[sp];}
    public static int evolved(int sp,int form){return Data.speciesForDex(form==74?264:form==75?862:Data.nationalDex(sp));}
}

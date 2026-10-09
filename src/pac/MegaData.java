package pac;

/** Source-backed Mega forms available in the current Full package. */
public final class MegaData{
    private MegaData(){}
    public static final int PRICE=25,UNLOCK_ROUND=15;
    /** Only forms with a complete on-disk animation set are exposed. */
    private static final int[] DEX={6,65,94,115,142,150,208,229,302,310,323,354,384,428,227,248,254,282,303,308,319,334,358,359,362,380,381,448,475,491,530,560,604,670,691,701,718,719,780,807,978,80};
    private static final int[] HP={300,230,380,330,280,360,300,250,250,240,300,260,380,260,300,380};
    private static final int[] ATK={30,32,40,28,30,34,18,32,18,24,22,28,32,25,28,34};
    private static final int[] DEF={14,8,12,18,14,12,50,14,14,12,20,10,14,12,30,24};
    private static final int[] SDEF={12,16,14,12,10,18,22,16,14,12,18,12,14,12,22,20};
    private static final int[] SPEED={70,75,70,60,85,85,40,65,50,80,45,60,70,75,65,55};
    private static final int[] MANA={100,90,90,100,90,80,100,110,100,100,110,100,100,80,100,110};
    private static final int[] RANGE={2,4,2,1,1,3,1,1,1,1,1,1,2,1,2,1};
    public static String name(){return Lang.t("Đá Mega","Mega Stone");}
    public static String desc(){return Lang.t("Mega hóa đến hết run, không yêu cầu Tỏa sáng. Giữ nguyên Tỏa sáng nếu đã có.","Mega Evolves for the run without requiring Shiny. Existing Shiny is preserved.");}
    public static int form(int sp){int dex=Data.nationalDex(sp);for(int i=0;i<DEX.length;i++)if(DEX[i]==dex)return i;return-1;}
    public static boolean available(int sp){return form(sp)>=0;}
    /** These four supplied Mega sets have no Shiny animation/portrait source yet. Bonuses are unaffected. */
    public static boolean shinyVisual(int sp){int dex=Data.nationalDex(sp);return available(sp)&&dex!=227&&dex!=248&&dex!=308&&dex!=491;}
    /** New forms use game-scale bonuses, not main-series base stats. */
    private static int stat(int sp,int[] values,int delta,int minimum){int i=form(sp);if(i<values.length)return values[i];int base=values==HP?Data.hp[sp]:values==ATK?Data.atk[sp]:values==DEF?Data.def[sp]:values==SDEF?Data.speDef[sp]:values==SPEED?Data.speed[sp]:values==MANA?Data.mana[sp]:Data.range[sp];return Math.max(minimum,base+delta);}
    public static boolean requiresShiny(int sp){return false;}
    public static int dex(int sp){return Data.nationalDex(sp);}
    public static int type1(int sp){if(LaterMegaData.index(sp)>=0)return LaterMegaData.type1(sp);return Data.nationalDex(sp)==6?Data.T_FIRE:Data.t1[sp];}
    public static int type2(int sp){if(LaterMegaData.index(sp)>=0)return LaterMegaData.type2(sp);int d=Data.nationalDex(sp);return d==6||d==254?Data.T_DRAGON:d==334?Data.T_FAIRY:d==358?Data.T_STEEL:Data.t2[sp];}
    public static String formName(int sp){int d=Data.nationalDex(sp);if(d==6)return"Mega Charizard X";if(d==150)return"Mega Mewtwo Y";return available(sp)?"Mega "+Data.name[sp]:Data.name[sp];}
    public static String detail(int sp){int i=form(sp);if(i<0)return Lang.t("Pokémon chưa thể Mega hóa.","Pokemon cannot Mega Evolve yet.");return formName(sp)+"\nHP "+stat(sp,HP,100,260)+"  "+Lang.t("Công ","ATK ")+stat(sp,ATK,8,20)+"  "+Lang.t("Thủ ","DEF ")+stat(sp,DEF,4,8)+"\n"+Lang.t("Kháng ","RES ")+stat(sp,SDEF,4,8)+"  "+Lang.t("Tốc ","SPD ")+stat(sp,SPEED,10,50)+"  "+Lang.t("Tầm ","RNG ")+stat(sp,RANGE,0,1)+"\n"+(requiresShiny(sp)?Lang.t("Điều kiện: Pokémon đã Tỏa sáng.","Requires: Shiny Pokemon."):Lang.t("Không yêu cầu Tỏa sáng.","Shiny is not required."));}
    /** Apply the Mega base-stat delta so held items, fruit boosts and synergies remain intact. */
    public static void apply(Unit u){int i=form(u.sp);if(i<0)return;u.mega=true;u.maxHp+=stat(u.sp,HP,100,260)-Data.hp[u.sp];u.hp=u.prevHp=u.maxHp;u.atk+=stat(u.sp,ATK,8,20)-Data.atk[u.sp];u.def+=stat(u.sp,DEF,4,8)-Data.def[u.sp];u.speDef+=stat(u.sp,SDEF,4,8)-Data.speDef[u.sp];u.speed+=stat(u.sp,SPEED,10,50)-Data.speed[u.sp];u.maxMana+=stat(u.sp,MANA,0,80)-Data.mana[u.sp];if(u.mana>u.maxMana)u.mana=u.maxMana;u.range+=stat(u.sp,RANGE,0,1)-Data.range[u.sp];u.cd=CombatRules.cooldownTicks(1000,u.speed);}
}

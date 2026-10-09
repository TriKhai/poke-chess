package pac;

/** Run-only stones that add one point to a synergy; they are not held equipment. */
public final class SynergyStoneData{
    private SynergyStoneData(){}
    private static final String[] ICON_ID={
        "NORMAL_GEM","FIRE_GEM","WATER_GEM","GRASS_GEM","ELECTRIC_GEM","ROCK_GEM",
        "PSYCHIC_GEM","FIGHTING_GEM","FLYING_GEM","DRAGON_GEM","GHOST_GEM","BUG_GEM",
        "POISON_GEM","GROUND_GEM","ICE_GEM","DARK_GEM","STEEL_GEM","FAIRY_GEM",
        "AMORPHOUS_GEM","AQUATIC_GEM","ARTIFICIAL_GEM","DAWN_STONE","FIELD_GEM",
        "FLORA_GEM","FOSSIL_GEM","GOURMET_GEM","HUMAN_GEM","LIGHT_GEM","MONSTER_GEM",
        "SOUND_GEM","WILD_GEM"
    };
    public static final int[] ICON=new int[Data.NT];
    static{for(int i=0;i<Data.NT;i++)ICON[i]=ItemData.indexOf(ICON_ID[i]);}
    public static int price(int round){return 5+((Math.max(1,round)-1)/5)*5;}
    public static String name(int type){return Lang.t("Đá ","Stone of ")+Lang.typeName(type);}
    public static int nextPriceRound(int round){return ((Math.max(1,round)-1)/5+1)*5+1;}
    public static String desc(int type){return Lang.t("Kích hoạt ngay +1 điểm cộng hưởng hệ ","Immediately grants +1 ")+Lang.typeName(type)+Lang.t(" trong toàn bộ run. Không chiếm ô trang bị."," synergy for the run. Uses no equipment slot.");}
}

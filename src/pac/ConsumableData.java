package pac;

/** Berry consumables used only by the current run, never as held equipment. */
public final class ConsumableData{
    private ConsumableData(){}
    public static final int AGUAV=0,APICOT=1,ASPEAR=2,BABIRI=3,CHERI=4,CHESTO=5,GANLON=6,JABOCA=7,LANSAT=8,LEPPA=9,LIECHI=10,LUM=11,ORAN=12,PECHA=13,PERSIM=14,PETAYA=15,RAWST=16,ROWAP=17,SALAC=18,SITRUS=19,GOLDEN_RAZZ=20,GOLDEN_NANAB=21,GOLDEN_PINAP=22,NANAB=23,ULTIMATE=24,SHINY_CHARM=25,MEGA_STONE=26,MEMORY_DISC=27,ZYGARDE_CUBE=28,COUNT=29;
    private static final String[] ID={"AGUAV_BERRY","APICOT_BERRY","ASPEAR_BERRY","BABIRI_BERRY","CHERI_BERRY","CHESTO_BERRY","GANLON_BERRY","JABOCA_BERRY","LANSAT_BERRY","LEPPA_BERRY","LIECHI_BERRY","LUM_BERRY","ORAN_BERRY","PECHA_BERRY","PERSIM_BERRY","PETAYA_BERRY","RAWST_BERRY","ROWAP_BERRY","SALAC_BERRY","SITRUS_BERRY","GOLDEN_RAZZ_BERRY","GOLDEN_NANAB_BERRY","GOLDEN_PINAP_BERRY","NANAB_BERRY","BERRIES","SHINY_STONE","MEGA_STONE","MEMORY_DISCS","ZYGARDE_CUBE"};
    public static final int[] ICON=new int[COUNT];
    public static final int[] PRICE={7,5,7,10,5,7,5,10,10,7,7,10,5,5,7,5,7,7,10,7,10,10,10,7,50,15,25,25,20};
    public static final int[] HP={80,0,0,0,0,0,0,0,0,0,0,0,50,50,0,0,0,0,0,100,0,0,0,0,150,0,0,0,0};
    public static final int[] ATK={0,0,0,0,6,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,4,15,0,0,0,0};
    public static final int[] DEF={0,0,0,0,0,0,5,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,10,0,0,0,0};
    public static final int[] SPDEF={0,5,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,10,0,0,0,0};
    public static final int[] SPEED={0,0,10,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10,10,0,0,0,0};
    public static final int[] MANA={0,0,0,0,0,20,0,0,0,25,0,0,0,0,0,0,0,0,0,0,0,0,0,0,30,0,0,0,0};
    public static final int[] AP={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,15,0,0,0,0,0,0,0,0,15,0,0,0,0};
    public static final int[] RANGE={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0};
    public static final int[] SHIELD={0,0,0,100,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    public static final int[] HP_PCT={0,0,0,0,0,0,0,0,0,0,0,5,0,0,0,0,0,0,0,0,5,0,0,0,0,0,0,0,0};
    public static final int[] ATK_PCT={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5,0,0,0,0,0,0,0};
    public static final int[] DEF_PCT={0,0,0,5,0,0,0,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    public static final int[] SPEED_PCT={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5,0,0,0,0,0,0,0,0,0,0};
    public static final int[] AP_PCT={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5,0,0,0,0,0,0};
    public static final int[] CRIT_PCT={0,0,0,0,0,0,0,0,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    static{for(int i=0;i<COUNT;i++)ICON[i]=ItemData.indexOf(ID[i]);ICON[ULTIMATE]=ICON[CHERI];}
    public static String name(int id){if(id==MEGA_STONE)return MegaData.name();if(id==MEMORY_DISC)return Lang.t("Thẻ nhớ biến đổi","Memory Disc");if(id==ZYGARDE_CUBE)return"Zygarde Cube";return id==SHINY_CHARM?Lang.t("Đá Shiny","Shiny Stone"):(id>=0&&id<COUNT?ItemData.name(ICON[id]):"?");}
    public static boolean randomEligible(int id,int round){if(id<0||id>=ULTIMATE||id==CHERI||id==GOLDEN_RAZZ)return false;int p=PRICE[id];return p==5||(p==7&&round>=5)||(p==10&&round>=15);}
    public static boolean isFruitItem(int itemId){for(int i=0;i<=ULTIMATE;i++)if(ICON[i]==itemId)return true;return false;}
    public static String group(int id){return id>=SHINY_CHARM?"GEN":PRICE[id]+Lang.t(" vàng"," gold");}
    public static String desc(int id){if(id<0||id>=COUNT)return"";if(id>=SHINY_CHARM)return effect(id);StringBuffer b=new StringBuffer();boolean more=false;more=add(b,more,"HP",HP[id]);more=add(b,more,Lang.t("Công","ATK"),ATK[id]);more=add(b,more,Lang.t("Thủ","DEF"),DEF[id]);more=add(b,more,Lang.t("Kháng","SP.DEF"),SPDEF[id]);more=add(b,more,Lang.t("Tốc","SPD"),SPEED[id]);more=add(b,more,"AP",AP[id]);more=add(b,more,Lang.t("Năng lượng đầu","Starting energy"),MANA[id]);more=add(b,more,Lang.t("Tầm","Range"),RANGE[id]);more=add(b,more,Lang.t("Khiên","Shield"),SHIELD[id]);more=addPct(b,more,"HP",HP_PCT[id]);more=addPct(b,more,Lang.t("Công","ATK"),ATK_PCT[id]);more=addPct(b,more,Lang.t("Thủ","DEF"),DEF_PCT[id]);more=addPct(b,more,Lang.t("Tốc","SPD"),SPEED_PCT[id]);more=addPct(b,more,"AP",AP_PCT[id]);addPct(b,more,Lang.t("Chí mạng","Crit"),CRIT_PCT[id]);b.append(". ").append(effect(id));return b.toString();}
    public static String effect(int id){
        if(id==SHINY_CHARM)return ShinyData.desc();if(id==MEGA_STONE)return MegaData.desc();if(id==MEMORY_DISC)return SpecialFormData.memoryDesc();if(id==ZYGARDE_CUBE)return SpecialFormData.cubeDesc();
        if(id==AGUAV)return Lang.t("Dưới 50% HP: hồi 40 HP một lần.","Below 50% HP: heal 40 once.");
        if(id==ASPEAR)return Lang.t("Miễn Đóng băng.","Immune to Freeze.");if(id==CHESTO)return Lang.t("Miễn Ngủ.","Immune to Sleep.");
        if(id==PECHA)return Lang.t("Miễn Độc.","Immune to Poison.");if(id==PERSIM)return Lang.t("Miễn Hỗn loạn.","Immune to Confusion.");if(id==RAWST)return Lang.t("Miễn Bỏng.","Immune to Burn.");
        if(id==BABIRI)return Lang.t("Mỗi trận tạo 100 Máu ảo.","Gain 100 shield each battle.");if(id==JABOCA)return Lang.t("Phản 20 sát thương chuẩn khi bị đánh thường.","Reflect 20 true damage from basic attacks.");
        if(id==LANSAT)return Lang.t("Đòn chí mạng đầu tiên cộng 60 sát thương.","First critical hit gains 60 damage.");if(id==LEPPA)return Lang.t("Sau tuyệt kỹ đầu tiên hồi 25 năng lượng.","Refund 25 energy after the first ability.");
        if(id==LIECHI)return Lang.t("Dưới 35% HP: cộng thêm 4 Công.","Below 35% HP: gain 4 ATK.");if(id==LUM)return Lang.t("Xóa debuff lần đầu.","Cleanse the first debuff.");
        if(id==ROWAP)return Lang.t("Chặn kỹ năng đầu tiên.","Block the first ability hit.");if(id==SALAC)return Lang.t("Dưới 35% HP: lập tức sẵn sàng hành động.","Below 35% HP: act immediately.");
        if(id==SITRUS)return Lang.t("Dưới 50% HP: hồi 60 HP một lần.","Below 50% HP: heal 60 once.");if(id==NANAB||id==GOLDEN_NANAB)return Lang.t("Mỗi hạ gục nhận thêm 1 vàng sau trận.","Each kill grants 1 post-battle gold.");
        if(id==GOLDEN_RAZZ)return Lang.t("Mỗi trận tạo Máu ảo bằng 10% HP tối đa.","Gain shield equal to 10% max HP each battle.");if(id==GOLDEN_PINAP)return Lang.t("Sau tuyệt kỹ đầu tiên hồi 30 năng lượng.","Refund 30 energy after the first ability.");
        if(id==ULTIMATE)return Lang.t("Không có nội tại bổ sung.","No additional passive.");
        return "";
    }
    private static boolean add(StringBuffer b,boolean more,String label,int value){if(value==0)return more;if(more)b.append(", ");b.append(label).append(" +").append(value);return true;}
    private static boolean addPct(StringBuffer b,boolean more,String label,int value){if(value==0)return more;if(more)b.append(", ");b.append(label).append(" +").append(value).append('%');return true;}
}

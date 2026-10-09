package pac;

/** Run-only Shiny upgrade. Kept independent from held equipment and synergies. */
public final class ShinyData {
    private ShinyData(){}
    public static final int PRICE=15, UNLOCK_ROUND=10;
    public static final int ICON=ItemData.indexOf("SHINY_CHARM");

    public static String name(){return Lang.t("Đá Shiny","Shiny Stone");}
    public static String desc(){return Lang.t(
        "Đổi sang dạng Tỏa sáng đến hết run: +50 HP, +5 Công/Thủ/Kháng/Tốc, +1 Tầm và +25% uy lực tuyệt kỹ. Nhận thêm một nội tại nhỏ theo vai trò.",
        "Turns Shiny for the run: +50 HP, +5 Attack/Defense/Sp.Def/Speed, +1 Range and +25% ultimate power. Also gains a small role passive.");}

    /** Available only when the source-backed Shiny animation table is packaged. */
    public static boolean available(int sp){if(sp<0||sp>=Data.N)return false;if(sp<Data.CORE_N)return true;int di=Data.collectionIndex(sp);return di>=0&&CollectionAtlas.hasShiny(di);}

    public static int role(int sp){
        if(Data.range[sp]>1)return 2;                 // ranged
        if(Data.mana[sp]>=100)return 1;              // caster
        if(Data.hp[sp]+Data.def[sp]*8>=260)return 0; // tank
        if(Data.speed[sp]>=65)return 3;              // speed
        return 4;                                    // fighter
    }
    public static String roleName(int sp){
        switch(role(sp)){
            case 0:return Lang.t("Hộ Thể: khiên đầu trận bằng 10% HP.","Guard: starts with a shield equal to 10% HP.");
            case 1:return Lang.t("Tụ Năng: bắt đầu với thêm 15 năng lượng.","Focus: starts with 15 extra energy.");
            case 2:return Lang.t("Xạ Kích: thêm 5% chí mạng.","Marksman: gains 5% critical chance.");
            case 3:return Lang.t("Thần Tốc: thêm 10 Tốc độ.","Haste: gains 10 Speed.");
            default:return Lang.t("Cường Công: thêm 10% Công.","Power: gains 10% Attack.");
        }
    }
    public static void apply(Unit u){
        u.shiny=true;u.maxHp+=50;u.hp=u.prevHp=u.maxHp;u.atk+=5;u.def+=5;u.speDef+=5;u.speed+=5;u.range+=1;u.skillBonus+=25;
        switch(role(u.sp)){case 0:int sh=Math.max(1,u.maxHp/10);u.shield+=sh;u.shieldDone+=sh;break;case 1:u.mana=Math.min(u.maxMana,u.mana+15);break;case 2:u.crit+=5;break;case 3:u.speed+=10;break;default:u.atk+=Math.max(1,Data.atk[u.sp]/10);break;}
        u.cd=CombatRules.cooldownTicks(1000,u.speed);
    }
}

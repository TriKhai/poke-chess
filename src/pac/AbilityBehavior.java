package pac;

/** Source-name driven secondary ability behavior for the offline roster. */
public final class AbilityBehavior {
    private AbilityBehavior(){}
    private static boolean has(String s,String token){return s.indexOf(token)>=0;}

    public static String description(int sp){
        String n=Data.skillName[sp].toUpperCase(),extra="";
        if(has(n,"AGILITY")||has(n,"AQUA STEP")||has(n,"FLAME CHARGE"))extra=Lang.t("Sau khi dùng: +20 tốc đánh.","After casting: +20 speed.");
        else if(has(n,"COIL")||has(n,"BULK UP")||has(n,"COSMIC POWER"))extra=Lang.t("Tăng ATK, DEF và SP.DEF cho bản thân.","Raises the user's ATK, DEF and SP.DEF.");
        else if(has(n,"RECOVER")||has(n,"ROOST")||has(n,"SYNTHESIS")||has(n,"MOONLIGHT"))extra=Lang.t("Hồi 35% HP tối đa.","Restores 35% max HP.");
        else if(has(n,"AROMATHERAPY")||has(n,"HEAL BELL")||has(n,"PURIFY"))extra=Lang.t("Xóa trạng thái xấu cho cả đội.","Cleanses negative statuses from the team.");
        else if(has(n,"PROTECT")||has(n,"DETECT")||has(n,"KING SHIELD")||has(n,"OBSTRUCT"))extra=Lang.t("Bảo vệ bản thân trong 2 giây.","Protects the user for 2 seconds.");
        else if(has(n,"PAYDAY"))extra=Lang.t("Tạo thêm 1 vàng thưởng sau trận.","Generates 1 post-battle gold.");
        else if(has(n,"SING")||has(n,"GRASS WHISTLE"))extra=Lang.t("Gây Ngủ cho nhóm địch quanh mục tiêu.","Puts enemies around the target to Sleep.");
        else if(has(n,"POISON GAS")||has(n,"POISON POWDER"))extra=Lang.t("Gây Độc cho nhóm địch quanh mục tiêu.","Poisons enemies around the target.");
        String base=Lang.abilityDesc(Data.abil[sp]);return extra.length()==0?base:base+" "+extra;
    }

    public static void apply(Battle b,Unit caster,Unit target){
        String n=Data.skillName[caster.sp].toUpperCase();
        if(has(n,"AGILITY")||has(n,"AQUA STEP")||has(n,"FLAME CHARGE"))caster.speed+=20;
        if(has(n,"COIL")||has(n,"BULK UP")||has(n,"COSMIC POWER")){caster.atk+=Math.max(1,caster.atk/5);caster.def+=3;caster.speDef+=3;}
        if(has(n,"ACID ARMOR")||has(n,"DEFENSE CURL")||has(n,"IRON DEFENSE"))caster.def+=6;
        if(has(n,"NASTY PLOT")||has(n,"CALM MIND")||has(n,"QUIVER DANCE"))caster.skillBonus+=30;
        if(has(n,"RECOVER")||has(n,"ROOST")||has(n,"SYNTHESIS")||has(n,"MOONLIGHT")||has(n,"MORNING SUN")||has(n,"SLACK OFF"))b.itemHeal(caster,caster,Math.max(1,caster.maxHp*35/100));
        if(has(n,"AQUA RING")||has(n,"INGRAIN"))caster.regen+=3;
        if(has(n,"PROTECT")||has(n,"DETECT")||has(n,"KING SHIELD")||has(n,"OBSTRUCT")||has(n,"BANEFUL BUNKER"))caster.status.protect=Math.max(caster.status.protect,20);
        if(has(n,"TELEPORT")||has(n,"DIG")||has(n,"FLY"))b.relocateAway(caster);
        if(has(n,"PAYDAY")&&caster.side==0)b.itemGold++;
        if(has(n,"AROMATHERAPY")||has(n,"HEAL BELL")||has(n,"PURIFY")||has(n,"LUNAR BLESSING")){
            for(int i=0;i<b.n;i++){Unit a=b.units[i];if(a.alive&&a.side==caster.side)a.status.clearNegative();}
        }
        if(has(n,"AURORA VEIL")||has(n,"REFLECT")){
            for(int i=0;i<b.n;i++){Unit a=b.units[i];if(a.alive&&a.side==caster.side){int sh=Math.max(1,a.maxHp/8);a.shield+=sh;a.shieldDone+=sh;}}
        }
        if(has(n,"GROWL")||has(n,"CHARM"))for(int i=0;i<b.n;i++){Unit e=b.units[i];if(e.alive&&e.side!=caster.side&&b.distance(caster,e)<=2)e.atk=Math.max(1,e.atk*80/100);}
        if(has(n,"SING")||has(n,"GRASS WHISTLE"))for(int i=0;i<b.n;i++){Unit e=b.units[i];if(e.alive&&e.side!=caster.side&&b.distance(target,e)<=1)e.status.sleep=Math.max(e.status.sleep,20);}
        if(has(n,"POISON GAS")||has(n,"POISON POWDER"))for(int i=0;i<b.n;i++){Unit e=b.units[i];if(e.alive&&e.side!=caster.side&&b.distance(target,e)<=1)e.status.poison=Math.max(e.status.poison,40);}
        if(has(n,"FLASH")||has(n,"SAND SPIT"))for(int i=0;i<b.n;i++){Unit e=b.units[i];if(e.alive&&e.side!=caster.side&&b.distance(target,e)<=1)e.status.blinded=Math.max(e.status.blinded,30);}
        if(has(n,"HAZE")){for(int i=0;i<b.n;i++)if(b.units[i].alive)b.units[i].status.clearNegative();}
    }
}

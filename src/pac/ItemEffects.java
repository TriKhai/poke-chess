package pac;

/**
 * CLDC-safe held-item dispatcher.  Battle owns timing and damage resolution;
 * this class only reacts to explicit combat events, mirroring the source game's
 * OnSimulationStart/Periodic/OnMove/OnAttack/OnDamage/OnCast/OnKill/OnDeath hooks.
 */
public final class ItemEffects {
    private ItemEffects() {}

    private static final int SOUL_DEW=id("SOUL_DEW"), UPGRADE=id("UPGRADE"), REAPER_CLOTH=id("REAPER_CLOTH");
    private static final int POKEMONOMICON=id("POKEMONOMICON"), SHELL_BELL=id("SHELL_BELL"), AIR_BALLOON=id("AIR_BALLOON");
    private static final int AQUA_EGG=id("AQUA_EGG"), BLUE_ORB=id("BLUE_ORB"), SCOPE_LENS=id("SCOPE_LENS");
    private static final int STAR_DUST=id("STAR_DUST"), GREEN_ORB=id("GREEN_ORB"), DEEP_SEA_TOOTH=id("DEEP_SEA_TOOTH");
    private static final int SMOKE_BALL=id("SMOKE_BALL"), RAZOR_FANG=id("RAZOR_FANG"), PROTECTIVE_PADS=id("PROTECTIVE_PADS");
    private static final int LOADED_DICE=id("LOADED_DICE"), MUSCLE_BAND=id("MUSCLE_BAND"), STICKY_BARB=id("STICKY_BARB");
    private static final int KINGS_ROCK=id("KINGS_ROCK"), FLAME_ORB=id("FLAME_ORB"), AMULET_COIN=id("AMULET_COIN");
    private static final int MAX_REVIVE=id("MAX_REVIVE"), ROCKY_HELMET=id("ROCKY_HELMET"), RUNNING_SHOES=id("RUNNING_SHOES");
    private static final int MACH_RIBBON=id("MACH_RIBBON"), EXPLOSIVE_BAND=id("EXPLOSIVE_BAND"), EFFICIENT_BANDANNA=id("EFFICIENT_BANDANNA");
    private static final int LUCKY_RIBBON=id("LUCKY_RIBBON"), WIDE_LENS=id("WIDE_LENS"), ELECTIRIZER=id("ELECTIRIZER");
    private static final int SPELL_TAG=id("SPELL_TAG"), BLACK_BELT=id("BLACK_BELT");

    private static int id(String s){return ItemData.indexOf(s);}
    public static boolean has(Unit u,int id){for(int i=0;i<3;i++)if(u.items[i]==id)return true;return false;}
    private static void consume(Unit u,int id){for(int i=0;i<3;i++)if(u.items[i]==id){u.items[i]=-1;return;}}
    private static void mana(Unit u,int n){if(u.maxMana<=0)return;u.mana+=n;if(u.mana<0)u.mana=0;if(u.mana>u.maxMana)u.mana=u.maxMana;}
    private static void shield(Unit u,int n){if(n<=0)return;u.shield+=n;u.shieldDone+=n;}

    public static void onStart(Battle b,Unit u){
        u.critPower=150;
        if(has(u,REAPER_CLOTH)||has(u,RAZOR_FANG))u.critPower+=50;
        if(has(u,AIR_BALLOON))u.dodge+=10;
        if(has(u,LUCKY_RIBBON))u.dodge+=15;
        if(has(u,WIDE_LENS))u.range+=2;
        if(has(u,KINGS_ROCK))shield(u,u.maxHp/5);
        if(has(u,FLAME_ORB)){u.atk+=Math.max(1,Data.atk[u.sp]);u.status.burn=3000;}
        if(has(u,EFFICIENT_BANDANNA))for(int i=0;i<b.n;i++){Unit a=b.units[i];if(a.side==u.side&&a.y==u.y&&Math.abs(a.x-u.x)<=1){a.maxMana=Math.max(1,a.maxMana*85/100);if(a.mana>a.maxMana)a.mana=a.maxMana;}}
    }

    public static void onTick(Battle b,Unit u){
        if(has(u,SOUL_DEW)&&++u.itemSoulClock>=10){u.itemSoulClock=0;u.skillBonus+=5;mana(u,5);}
        if(has(u,GREEN_ORB)&&++u.itemGreenClock>=20){u.itemGreenClock=0;for(int i=0;i<b.n;i++){Unit a=b.units[i];if(a.alive&&a.side==u.side&&b.distance(u,a)<=1)b.itemHeal(u,a,Math.max(1,a.maxHp/20));}}
        if(has(u,MACH_RIBBON)&&++u.itemRibbonClock>=30){u.itemRibbonClock=0;u.speed+=20;}
    }

    public static void onMove(Unit u){if(has(u,RUNNING_SHOES))u.speed+=5;}

    public static void onBasicAttack(Battle b,Unit u,Unit t,int dealt,boolean crit,boolean killed){
        u.itemAttackCount++;
        if(has(u,UPGRADE))u.speed+=5;
        if(has(u,DEEP_SEA_TOOTH))mana(u,killed?20:5);
        if(has(u,BLACK_BELT)&&crit)shield(u,(dealt+2)/3);
        if(has(u,SCOPE_LENS)&&crit){int steal=Math.min(10,t.mana);mana(t,-steal);mana(u,steal);}
        if(has(u,ELECTIRIZER)&&u.itemAttackCount%3==0&&t.alive)t.status.paralysis=Math.max(t.status.paralysis,20);
        if(has(u,RAZOR_FANG)&&t.alive)t.status.armorBreak=Math.max(t.status.armorBreak,20);
        if(has(u,BLUE_ORB)&&u.itemAttackCount%3==0){int left=2;for(int i=0;i<b.n&&left>0;i++){Unit e=b.units[i];if(e.alive&&e.side!=u.side&&e!=t){b.itemDamage(u,e,10,Battle.ITEM_SPECIAL);mana(e,-15);left--;}}}
        if(has(u,LOADED_DICE)&&dealt>0&&b.itemChance(50)){Unit e=b.lowestAdjacentEnemy(u,t);if(e!=null)b.itemDamage(u,e,Math.max(1,dealt*3/4),Battle.ITEM_PHYSICAL);}
    }

    public static void onDamageDealt(Battle b,Unit src,Unit tgt,int dealt,int type){
        if(dealt<=0)return;
        if(has(src,SHELL_BELL))b.itemHeal(src,src,(dealt+2)/3);
        if(has(src,POKEMONOMICON)&&type==Battle.ITEM_SPECIAL&&tgt.alive){tgt.status.burn=Math.max(tgt.status.burn,30);tgt.speDef=Math.max(0,tgt.speDef-1);}
    }

    public static void onDamageReceived(Battle b,Unit u,Unit attacker,int dealt,int blocked,int type,boolean basic,boolean crit,int shieldBefore){
        if(has(u,MUSCLE_BAND)&&dealt>0&&u.itemDamageCount<20){u.itemDamageCount++;if((u.itemDamageCount&1)==0){u.atk++;u.def+=2;u.speed+=5;}}
        if(basic&&attacker!=null&&b.distance(u,attacker)==1&&!has(attacker,PROTECTIVE_PADS)){
            if(has(u,STICKY_BARB)){b.itemDamage(u,attacker,3+u.def*15/100,Battle.ITEM_TRUE);attacker.status.wound=Math.max(attacker.status.wound,30);}
            if(has(u,ROCKY_HELMET)&&type!=Battle.ITEM_TRUE)b.itemDamage(u,attacker,Math.max(1,dealt/4),Battle.ITEM_TRUE);
        }
        if(has(u,SMOKE_BALL)&&!u.itemSmokeUsed&&u.alive&&u.hp*100<u.maxHp*40){u.itemSmokeUsed=true;consume(u,SMOKE_BALL);shield(u,50);b.relocateAway(u);}
        if(has(u,EXPLOSIVE_BAND)&&!u.itemShieldBurstUsed&&shieldBefore>0&&u.shield==0){u.itemShieldBurstUsed=true;consume(u,EXPLOSIVE_BAND);for(int i=0;i<b.n;i++){Unit e=b.units[i];if(e.alive&&e.side!=u.side&&b.distance(u,e)<=1)b.itemDamage(u,e,Math.max(1,u.shieldDone/2),Battle.ITEM_SPECIAL);}}
    }

    public static void onCast(Battle b,Unit u){
        u.itemCastCount++;
        if(has(u,AQUA_EGG))mana(u,Math.max(1,u.maxMana/5+u.itemCastCount*2));
        if(has(u,STAR_DUST))shield(u,Math.max(1,u.maxMana/2));
    }

    public static void onKill(Battle b,Unit killer,Unit dead){if(killer!=null&&has(killer,AMULET_COIN)){killer.itemKillCount++;if(killer.side==0)b.itemGold++;}}

    /** @return true when death was replaced by a one-time resurrection. */
    public static boolean onDeath(Battle b,Unit u,Unit killer){
        if(has(u,MAX_REVIVE)&&!u.itemReviveUsed){u.itemReviveUsed=true;consume(u,MAX_REVIVE);u.hp=Math.max(1,u.maxHp/2);u.alive=true;shield(u,u.maxHp/5);return true;}
        if(has(u,SPELL_TAG)&&killer!=null)killer.status.silence=Math.max(killer.status.silence,100);
        return false;
    }

    public static String passiveDesc(int i){
        if(i==SOUL_DEW)return Lang.t("Mỗi 1 giây: +5 AP và +5 MP.","Every 1s: +5 AP and +5 MP.");
        if(i==SHELL_BELL)return Lang.t("Hồi 33% sát thương gây ra.","Heal for 33% of damage dealt.");
        if(i==UPGRADE)return Lang.t("Mỗi đòn đánh: +5 tốc đánh.","Each basic attack grants +5 speed.");
        if(i==REAPER_CLOTH)return Lang.t("Kỹ năng có thể chí mạng; +50% sát thương chí mạng.","Abilities can crit; gain +50% critical power.");
        if(i==POKEMONOMICON)return Lang.t("Sát thương kỹ năng gây Bỏng 3 giây và -1 SP.DEF.","Ability damage burns for 3s and removes 1 SP.DEF.");
        if(i==BLUE_ORB)return Lang.t("Mỗi đòn thứ 3 nảy 10 sát thương lên 2 địch và đốt 15 MP.","Every 3rd attack chains 10 damage to 2 enemies and burns 15 MP.");
        if(i==GREEN_ORB)return Lang.t("Mỗi 2 giây hồi 5% HP cho đồng minh cạnh bên.","Every 2s heals adjacent allies for 5% HP.");
        if(i==DEEP_SEA_TOOTH)return Lang.t("Đánh thường nhận 5 MP; hạ mục tiêu nhận tổng 20 MP.","Basic attacks grant 5 MP; 20 MP on kill.");
        if(i==SCOPE_LENS)return Lang.t("Đòn chí mạng cướp tối đa 10 MP của mục tiêu.","Critical attacks steal up to 10 MP.");
        if(i==SMOKE_BALL)return Lang.t("Dưới 40% HP: dùng một lần, nhận 50 khiên và thoát ra xa.","Below 40% HP: consume for 50 shield and escape.");
        if(i==LOADED_DICE)return Lang.t("50% nảy 75% sát thương đánh thường sang địch cạnh mục tiêu.","50% chance to bounce 75% basic damage to an adjacent enemy.");
        if(i==MUSCLE_BAND)return Lang.t("Mỗi 2 lần nhận sát thương (tối đa 10 cộng dồn): +1 ATK, +2 DEF, +5 SPD.","Every 2 hits taken (10 stacks max): +1 ATK, +2 DEF, +5 SPD.");
        if(i==STICKY_BARB)return Lang.t("Bị đánh cận chiến: phản sát thương chuẩn và gây Vết thương 3 giây.","Melee attackers take true damage and Wound for 3s.");
        if(i==PROTECTIVE_PADS)return Lang.t("Miễn phản sát thương khi tiếp xúc.","Prevents contact retaliation damage.");
        if(i==RAZOR_FANG)return Lang.t("Đòn đánh gây Phá giáp 2 giây; +50% sát thương chí mạng.","Attacks apply Armor Break for 2s; +50% critical power.");
        if(i==BLACK_BELT)return Lang.t("Đòn chí mạng cho khiên bằng 33% sát thương.","Critical attacks grant shield equal to 33% damage.");
        if(i==KINGS_ROCK)return Lang.t("Đầu trận nhận khiên bằng 20% HP tối đa.","Gain a 20% max-HP shield at battle start.");
        if(i==FLAME_ORB)return Lang.t("Đầu trận tự Bỏng nhưng cộng thêm ATK gốc.","Start Burned but gain base ATK again.");
        if(i==MAX_REVIVE)return Lang.t("Tử trận lần đầu: hồi sinh 50% HP và 20% khiên.","First death: revive with 50% HP and 20% shield.");
        if(i==ROCKY_HELMET)return Lang.t("Phản 25% sát thương đánh cận chiến dưới dạng sát thương chuẩn.","Reflect 25% melee damage as true damage.");
        if(i==ELECTIRIZER)return Lang.t("Mỗi đòn đánh thứ 3 gây Tê liệt 2 giây.","Every 3rd attack Paralyses for 2s.");
        if(i==AQUA_EGG)return Lang.t("Sau khi dùng chiêu, hồi lại MP theo MP tối đa.","Regain MP after casting.");
        if(i==STAR_DUST)return Lang.t("Sau khi dùng chiêu, nhận khiên bằng 50% MP tối đa.","After casting, gain shield equal to 50% max MP.");
        if(i==AIR_BALLOON)return Lang.t("+10% né và miễn ảnh hưởng địa hình.","Gain 10% dodge and ignore board hazards.");
        if(i==WIDE_LENS)return Lang.t("Tăng 2 ô tầm đánh.","Gain 2 attack range.");
        if(i==LUCKY_RIBBON)return Lang.t("Đầu trận +15% né.","Gain 15% dodge at battle start.");
        if(i==RUNNING_SHOES)return Lang.t("Mỗi ô di chuyển: +5 tốc đánh.","Each cell moved grants +5 speed.");
        if(i==MACH_RIBBON)return Lang.t("Mỗi 3 giây: +20 tốc đánh.","Every 3s: +20 speed.");
        if(i==EFFICIENT_BANDANNA)return Lang.t("Đầu trận giảm 15% MP tối đa của bản thân và đồng minh cùng hàng cạnh bên.","At start, reduce max MP by 15% for self and adjacent row allies.");
        if(i==AMULET_COIN)return Lang.t("Mỗi lần hạ gục: +1 vàng thưởng sau trận.","Each kill grants +1 post-battle gold.");
        if(i==EXPLOSIVE_BAND)return Lang.t("Khi khiên cạn lần đầu, nổ 50% tổng khiên đã nhận vào địch cạnh bên.","When shield first breaks, explode for 50% of total shield gained.");
        return "";
    }
}

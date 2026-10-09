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
        else if(has(n,"REVIVAL BLESSING"))extra=Lang.t("Hồi sinh đồng minh đã ngã với 35% HP.","Revives a fallen ally with 35% HP.");
        else if(has(n,"LIFE DEW")||has(n,"JUNGLE HEALING")||has(n,"HEAL ORDER")||has(n,"FLORAL HEALING"))extra=Lang.t("Hồi 15% HP tối đa cho toàn đội.","Restores 15% max HP to the team.");
        else if(has(n,"AURORA VEIL")||has(n,"LIGHT SCREEN")||has(n,"REFLECT"))extra=Lang.t("Tạo Khiên bằng 15% HP tối đa cho toàn đội.","Shields the team for 15% max HP.");
        else if(has(n,"TAILWIND"))extra=Lang.t("Toàn đội nhận +15 Tốc độ.","The team gains +15 Speed.");
        else if(has(n,"SWORDS DANCE")||has(n,"DRAGON DANCE")||has(n,"VICTORY DANCE")||has(n,"WORK UP"))extra=Lang.t("Tăng Công; các điệu múa còn tăng Tốc độ.","Raises Attack; dances also raise Speed.");
        else if(has(n,"SHELL SMASH")||has(n,"NO RETREAT")||has(n,"CLANGOROUS SOUL"))extra=Lang.t("Tăng Công, Tốc độ và uy lực kỹ năng.","Raises Attack, Speed and ability power.");
        else if(has(n,"AQUA JET")||has(n,"AQUA STEP")||has(n,"QUICK ATTACK")||has(n,"EXTREME SPEED")||has(n,"BULLET PUNCH")||has(n,"ACCELEROCK"))extra=Lang.t("Lướt đến ô trống gần mục tiêu.","Dashes to an open cell near the target.");
        else if(has(n,"BONEMERANG")||has(n,"DOUBLE IRON BASH")||has(n,"ARM THRUST")||has(n,"FURY SWIPES"))extra=Lang.t("Gây thêm một đòn phụ bằng 60% Công.","Deals an extra hit for 60% Attack.");
        else if(has(n,"SALT CURE"))extra=Lang.t("Đồng minh gần nhận Khiên; địch Nước, Thép hoặc Ma bị Bỏng.","Shields nearby allies; burns nearby Water, Steel or Ghost enemies.");
        else if(has(n,"FUTURE SIGHT")||has(n,"DOOM DESIRE"))extra=Lang.t("Đánh dấu mục tiêu và phát nổ sau 2 giây.","Marks the target and explodes after 2 seconds.");
        else if(has(n,"CEASELESS EDGE")||has(n,"STONE AXE")||has(n,"SPIKES")||has(n,"STEALTH ROCK"))extra=Lang.t("Đặt bẫy tại ô mục tiêu, gây sát thương và Vết thương.","Places a hazard that damages and Wounds its victim.");
        else if(has(n,"CIRCLE THROW")||has(n,"WHIRLWIND")||has(n,"ROAR"))extra=Lang.t("Hất mục tiêu ra ô trống xa nhất.","Knocks the target to the farthest open cell.");
        else if(has(n,"MAGNET PULL")||has(n,"VINE WHIP")||has(n,"POWER WHIP"))extra=Lang.t("Kéo mục tiêu đến ô trống gần người dùng.","Pulls the target toward the user.");
        else if(has(n,"ALLY SWITCH"))extra=Lang.t("Đổi vị trí với mục tiêu.","Swaps position with the target.");
        else if(has(n,"GUILLOTINE")||has(n,"FISSURE")||has(n,"HORN DRILL")||has(n,"SHEER COLD"))extra=Lang.t("Kết liễu mục tiêu còn không quá 35% HP.","Executes a target at 35% HP or lower.");
        else if(has(n,"METRONOME"))extra=Lang.t("Kích hoạt ngẫu nhiên sát thương, hồi máu, khiên hoặc buff.","Randomly triggers damage, healing, shielding or a buff.");
        else if(has(n,"ASSIST"))extra=Lang.t("Dùng lại kỹ năng hợp lệ của một đồng minh.","Repeats an eligible ally ability.");
        else if(has(n,"MIMIC")||has(n,"COPYCAT")||has(n,"KNOWLEDGE THIEF"))extra=Lang.t("Sao chép và thi triển kỹ năng của mục tiêu.","Copies and casts the target's ability.");
        else if(has(n,"ENCORE"))extra=Lang.t("Lặp lại kỹ năng gần nhất của đội.","Repeats the team's most recent ability.");
        else if(has(n,"SKILL SWAP"))extra=Lang.t("Sao chép kỹ năng mục tiêu đến hết trận.","Copies the target's ability for the rest of battle.");
        else if(has(n,"TRANSFORM"))extra=Lang.t("Sao chép hình dạng và chỉ số chiến đấu của mục tiêu.","Copies the target's form and combat stats.");
        else if(has(n,"SUBSTITUTE")||has(n,"DOUBLE TEAM")||has(n,"SHADOW CLONE")||has(n,"SHED TAIL"))extra=Lang.t("Tạo một bản sao có 35% HP và 50% Công.","Creates a clone with 35% HP and 50% Attack.");
        String base=Lang.abilityDesc(Data.abil[sp]);if(extra.length()>0)base=base+" "+extra;String passive=PokemonPassive.description(sp);return passive.length()==0?base:base+" "+passive;
    }

    public static void apply(Battle b,Unit caster,Unit target){
        String n=Data.skillName[b.effectiveSkillSp(caster)].toUpperCase();
        if(has(n,"AGILITY")||has(n,"AQUA STEP")||has(n,"FLAME CHARGE"))caster.speed+=20;
        if(has(n,"COIL")||has(n,"BULK UP")||has(n,"COSMIC POWER")){caster.atk+=Math.max(1,caster.atk/5);caster.def+=3;caster.speDef+=3;}
        if(has(n,"ACID ARMOR")||has(n,"DEFENSE CURL")||has(n,"IRON DEFENSE"))caster.def+=6;
        if(has(n,"NASTY PLOT")||has(n,"CALM MIND")||has(n,"QUIVER DANCE"))caster.skillBonus+=30;
        if(has(n,"SWORDS DANCE")||has(n,"DRAGON DANCE")||has(n,"VICTORY DANCE")||has(n,"WORK UP")){caster.atk+=Math.max(1,caster.atk/4);if(has(n,"DANCE"))caster.speed+=15;}
        if(has(n,"SHELL SMASH")||has(n,"NO RETREAT")||has(n,"CLANGOROUS SOUL")){caster.atk+=Math.max(1,caster.atk/4);caster.speed+=20;caster.skillBonus+=30;}
        if(has(n,"RECOVER")||has(n,"ROOST")||has(n,"SYNTHESIS")||has(n,"MOONLIGHT")||has(n,"MORNING SUN")||has(n,"SLACK OFF"))b.itemHeal(caster,caster,Math.max(1,caster.maxHp*35/100));
        if(has(n,"AQUA RING")||has(n,"INGRAIN"))caster.regen+=3;
        if(has(n,"PROTECT")||has(n,"DETECT")||has(n,"KING SHIELD")||has(n,"OBSTRUCT")||has(n,"BANEFUL BUNKER"))caster.status.apply(CombatStatus.PROTECT,20);
        if(has(n,"SAFEGUARD"))for(int i=0;i<b.n;i++){Unit a=b.units[i];if(a.alive&&a.side==caster.side)a.status.apply(CombatStatus.SAFEGUARD,40);}
        if(has(n,"RAGE"))caster.status.apply(CombatStatus.RAGE,40);
        if(has(n,"ELECTRIC TERRAIN"))field(b,caster,CombatStatus.ELECTRIC_FIELD);
        if(has(n,"MISTY TERRAIN"))field(b,caster,CombatStatus.FAIRY_FIELD);
        if(has(n,"GRASSY TERRAIN"))field(b,caster,CombatStatus.GRASS_FIELD);
        if(has(n,"PSYCHIC TERRAIN"))field(b,caster,CombatStatus.PSYCHIC_FIELD);
        if(has(n,"TELEPORT")||has(n,"DIG")||has(n,"FLY"))b.relocateAway(caster);
        if(has(n,"AQUA JET")||has(n,"AQUA STEP")||has(n,"QUICK ATTACK")||has(n,"EXTREME SPEED")||has(n,"BULLET PUNCH")||has(n,"ACCELEROCK"))b.relocateNear(caster,target);
        if(has(n,"PAYDAY")&&caster.side==0)b.itemGold++;
        if(has(n,"AROMATHERAPY")||has(n,"HEAL BELL")||has(n,"PURIFY")||has(n,"LUNAR BLESSING")){
            for(int i=0;i<b.n;i++){Unit a=b.units[i];if(a.alive&&a.side==caster.side)a.status.clearNegative();}
        }
        if(has(n,"LIFE DEW")||has(n,"JUNGLE HEALING")||has(n,"HEAL ORDER")||has(n,"FLORAL HEALING"))for(int i=0;i<b.n;i++){Unit a=b.units[i];if(a.alive&&a.side==caster.side)b.itemHeal(caster,a,Math.max(1,a.maxHp*15/100));}
        if(has(n,"REVIVAL BLESSING"))b.reviveAlly(caster);
        if(has(n,"AURORA VEIL")||has(n,"REFLECT")||has(n,"LIGHT SCREEN")){
            for(int i=0;i<b.n;i++){Unit a=b.units[i];if(a.alive&&a.side==caster.side)b.abilityShield(caster,a,Math.max(1,a.maxHp*15/100));}
        }
        if(has(n,"TAILWIND"))for(int i=0;i<b.n;i++){Unit a=b.units[i];if(a.alive&&a.side==caster.side)a.speed+=15;}
        if(has(n,"GROWL")||has(n,"CHARM"))for(int i=0;i<b.n;i++){Unit e=b.units[i];if(e.alive&&e.side!=caster.side&&b.distance(caster,e)<=2)e.atk=Math.max(1,e.atk*80/100);}
        if(has(n,"SING")||has(n,"GRASS WHISTLE"))for(int i=0;i<b.n;i++){Unit e=b.units[i];if(e.alive&&e.side!=caster.side&&b.distance(target,e)<=1)e.status.apply(CombatStatus.SLEEP,20);}
        if(has(n,"POISON GAS")||has(n,"POISON POWDER"))for(int i=0;i<b.n;i++){Unit e=b.units[i];if(e.alive&&e.side!=caster.side&&b.distance(target,e)<=1)e.status.apply(CombatStatus.POISON,40);}
        if(has(n,"FLASH")||has(n,"SAND SPIT"))for(int i=0;i<b.n;i++){Unit e=b.units[i];if(e.alive&&e.side!=caster.side&&b.distance(target,e)<=1)e.status.apply(CombatStatus.BLINDED,30);}
        if(has(n,"HAZE")){for(int i=0;i<b.n;i++)if(b.units[i].alive)b.units[i].status.clearNegative();}
        if(target!=null&&target.alive&&(has(n,"BONEMERANG")||has(n,"DOUBLE IRON BASH")||has(n,"ARM THRUST")||has(n,"FURY SWIPES")))b.itemDamage(caster,target,Math.max(1,caster.atk*60/100),Battle.ITEM_PHYSICAL);
        if(has(n,"SALT CURE"))for(int i=0;i<b.n;i++){Unit a=b.units[i];if(!a.alive||b.distance(caster,a)>2)continue;if(a.side==caster.side){a.status.clearNegative();b.abilityShield(caster,a,Math.max(1,a.maxHp/10));}else if(a.primaryType()==Data.T_WATER||a.secondaryType()==Data.T_WATER||a.primaryType()==Data.T_STEEL||a.secondaryType()==Data.T_STEEL||a.primaryType()==Data.T_GHOST||a.secondaryType()==Data.T_GHOST)a.status.apply(CombatStatus.BURN,50);}
        if(target!=null&&target.alive&&(has(n,"FUTURE SIGHT")||has(n,"DOOM DESIRE")))b.scheduleAbility(caster,target,Math.max(1,caster.atk*(has(n,"DOOM DESIRE")?2:1)),20,has(n,"FUTURE SIGHT")?1:0);
        if(target!=null&&(has(n,"CEASELESS EDGE")||has(n,"STONE AXE")||has(n,"SPIKES")||has(n,"STEALTH ROCK")||has(n,"TOXIC SPIKES")))b.placeHazard(caster,target.x,target.y,Math.max(1,caster.atk/2),50);
        if(target!=null&&(has(n,"CIRCLE THROW")||has(n,"WHIRLWIND")||has(n,"ROAR")))b.pushAway(caster,target);
        if(target!=null&&(has(n,"MAGNET PULL")||has(n,"VINE WHIP")||has(n,"POWER WHIP")))b.pullNear(caster,target);
        if(target!=null&&has(n,"ALLY SWITCH"))b.swapUnits(caster,target);
        if(target!=null&&target.alive&&(has(n,"GUILLOTINE")||has(n,"FISSURE")||has(n,"HORN DRILL")||has(n,"SHEER COLD"))&&target.hp*100<=target.maxHp*35)b.itemDamage(caster,target,target.hp+target.shield+1,Battle.ITEM_TRUE);
        if(has(n,"METRONOME")){if(b.itemChance(25))b.itemHeal(caster,caster,Math.max(1,caster.maxHp/3));else if(b.itemChance(33))b.abilityShield(caster,caster,Math.max(1,caster.maxHp/4));else if(target!=null&&target.alive&&b.itemChance(50))b.itemDamage(caster,target,Math.max(1,caster.atk*2),Battle.ITEM_SPECIAL);else{caster.atk+=5;caster.speed+=10;caster.skillBonus+=20;}}
        if(target!=null&&target.alive&&has(n,"TRANSFORM")){caster.sp=target.sp;caster.atk=target.atk;caster.def=target.def;caster.speDef=target.speDef;caster.speed=target.speed;caster.range=target.range;caster.maxMana=target.maxMana;}
        if(has(n,"SUBSTITUTE")||has(n,"DOUBLE TEAM")||has(n,"SHADOW CLONE")||has(n,"SHED TAIL"))b.summonClone(caster);
        if(has(n,"ASSIST")){int copied=b.assistSkill(caster);if(copied>=0)b.repeatAbility(caster,target,copied);}
        if(has(n,"MIMIC")||has(n,"COPYCAT")||has(n,"KNOWLEDGE THIEF")){if(target!=null)b.repeatAbility(caster,target,b.effectiveSkillSp(target));}
        if(has(n,"ENCORE")){int copied=b.lastSkill(caster.side);if(copied>=0&&copied!=b.effectiveSkillSp(caster))b.repeatAbility(caster,target,copied);}
        if(has(n,"SKILL SWAP")&&target!=null&&target.alive){caster.copiedSkillSp=b.effectiveSkillSp(target);caster.maxMana=Data.mana[caster.copiedSkillSp];caster.mana=0;b.repeatAbility(caster,target,caster.copiedSkillSp);}
    }
    private static void field(Battle b,Unit caster,int status){for(int i=0;i<b.n;i++){Unit a=b.units[i];if(a.alive&&a.side==caster.side)a.status.apply(status,60);}}
}

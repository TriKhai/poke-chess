package pac;

/** Timed status container. Durations are deterministic simulation ticks. */
public final class CombatStatus {
    public static final int NONE=-1, ARMOR_BREAK=0, BLINDED=1, BURN=2, CHARM=3,
            CONFUSION=4, CURSE=5, FATIGUE=6, FLINCH=7, FREEZE=8, LOCKED=9,
            PARALYSIS=10, POISON=11, PROTECT=12, SILENCE=13, SLEEP=14, WOUND=15,
            POSSESSED=16, RESURRECTION=17, SAFEGUARD=18, RAGE=19, POKERUS=20,
            ELECTRIC_FIELD=21, FAIRY_FIELD=22, GRASS_FIELD=23, PSYCHIC_FIELD=24, COUNT=25;
    public int stun, paralysis, burn, poison, freeze, sleep;
    public int confusion, charm, curse, fatigue, flinch, locked, wound, blinded, armorBreak, silence, protect;
    public int possessed,resurrection,safeguard,rage,pokerus,electricField,fairyField,grassField,psychicField;
    private int dotClock;

    public boolean blocksAction() { return stun > 0 || freeze > 0 || sleep > 0 || flinch > 0; }
    public boolean blocksMove() { return locked > 0; }
    public int effectiveSpeed(int base) {
        if(paralysis>0)base=Math.max(1,base/2);
        if(fatigue>0)base=Math.max(1,base*2/3);
        if(rage>0)base=base*3/2;
        if(electricField>0)base=base*6/5;
        return base;
    }

    public boolean negativeImmune(){return safeguard>0;}
    public boolean positive(int id){return id==PROTECT||id==RESURRECTION||id==SAFEGUARD||id==RAGE||id==POKERUS||id>=ELECTRIC_FIELD;}
    /** Apply by documented status id, extending duration and respecting Safeguard. */
    public boolean apply(int id,int duration){
        if(duration<=0)return false;
        if(!positive(id)&&negativeImmune())return false;
        switch(id){
        case ARMOR_BREAK:armorBreak=Math.max(armorBreak,duration);break;case BLINDED:blinded=Math.max(blinded,duration);break;
        case BURN:burn=Math.max(burn,duration);break;case CHARM:charm=Math.max(charm,duration);break;
        case CONFUSION:confusion=Math.max(confusion,duration);break;case CURSE:curse=Math.max(curse,duration);break;
        case FATIGUE:fatigue=Math.max(fatigue,duration);break;case FLINCH:flinch=Math.max(flinch,duration);break;
        case FREEZE:freeze=Math.max(freeze,duration);break;case LOCKED:locked=Math.max(locked,duration);break;
        case PARALYSIS:paralysis=Math.max(paralysis,duration);break;case POISON:poison=Math.max(poison,duration);break;
        case PROTECT:protect=Math.max(protect,duration);break;case SILENCE:silence=Math.max(silence,duration);break;
        case SLEEP:sleep=Math.max(sleep,duration);break;case WOUND:wound=Math.max(wound,duration);break;
        case POSSESSED:possessed=Math.max(possessed,duration);break;case RESURRECTION:resurrection=Math.max(resurrection,duration);break;
        case SAFEGUARD:safeguard=Math.max(safeguard,duration);clearNegative();break;case RAGE:rage=Math.max(rage,duration);break;
        case POKERUS:pokerus=Math.max(pokerus,duration);break;case ELECTRIC_FIELD:electricField=Math.max(electricField,duration);break;
        case FAIRY_FIELD:fairyField=Math.max(fairyField,duration);break;case GRASS_FIELD:grassField=Math.max(grassField,duration);break;
        case PSYCHIC_FIELD:psychicField=Math.max(psychicField,duration);break;default:return false;}
        return true;
    }

    public boolean hasNegative(){return stun>0||paralysis>0||burn>0||poison>0||freeze>0||sleep>0||confusion>0||charm>0||possessed>0||curse>0||fatigue>0||flinch>0||locked>0||wound>0||blinded>0||armorBreak>0||silence>0;}
    public void clearNegative(){stun=paralysis=burn=poison=freeze=sleep=confusion=charm=possessed=curse=fatigue=flinch=locked=wound=blinded=armorBreak=silence=0;}
    public void clearPositive(){protect=resurrection=safeguard=rage=pokerus=electricField=fairyField=grassField=psychicField=0;}

    public void update(Battle battle, Unit unit) {
        if (stun > 0) stun--;
        if (paralysis > 0) paralysis--;
        if (burn > 0) burn--;
        if (poison > 0) poison--;
        if (freeze > 0) freeze--;
        if (sleep > 0) sleep--;
        if (confusion > 0) confusion--;
        if (charm > 0) charm--;
        if (curse > 0) curse--;
        if (fatigue > 0) fatigue--;
        if (flinch > 0) flinch--;
        if (locked > 0) locked--;
        if (wound > 0) wound--;
        if (blinded > 0) blinded--;
        if (armorBreak > 0) armorBreak--;
        if (silence > 0) silence--;
        if (protect > 0) protect--;
        if(possessed>0)possessed--;if(resurrection>0)resurrection--;if(safeguard>0)safeguard--;
        if(rage>0)rage--;if(pokerus>0)pokerus--;if(electricField>0)electricField--;
        if(fairyField>0)fairyField--;if(grassField>0)grassField--;if(psychicField>0)psychicField--;
        dotClock++;
        if (dotClock >= 10) {
            dotClock = 0;
            if (burn > 0) battle.statusDamage(unit, Math.max(1, unit.maxHp / 25));
            if (poison > 0 && unit.alive) battle.statusDamage(unit, Math.max(1, unit.maxHp / 20));
            if (curse > 0 && unit.alive) battle.statusDamage(unit, Math.max(1, unit.maxHp / 16));
            if(grassField>0&&unit.alive)battle.itemHeal(unit,unit,Math.max(1,unit.maxHp/25));
            if(pokerus>0&&unit.alive){unit.atk++;unit.skillBonus+=10;}
        }
    }

    public int visual() {
        if (freeze>0) return FREEZE; if (sleep>0) return SLEEP;
        if (paralysis>0) return PARALYSIS; if (burn>0) return BURN;
        if (poison>0) return POISON; if (curse>0) return CURSE; if(fatigue>0)return FATIGUE;
        if(flinch>0)return FLINCH;if(locked>0)return LOCKED;if (confusion>0) return CONFUSION;
        if (charm>0) return CHARM; if (wound>0) return WOUND;
        if (blinded>0) return BLINDED; if (armorBreak>0) return ARMOR_BREAK;
        if (silence>0) return SILENCE; if (protect>0) return PROTECT;
        if(possessed>0)return POSSESSED;if(resurrection>0)return RESURRECTION;if(safeguard>0)return SAFEGUARD;
        if(rage>0)return RAGE;if(pokerus>0)return POKERUS;if(electricField>0)return ELECTRIC_FIELD;
        if(fairyField>0)return FAIRY_FIELD;if(grassField>0)return GRASS_FIELD;if(psychicField>0)return PSYCHIC_FIELD;
        return NONE;
    }

    /** Return the nth simultaneous status so the renderer can layer buff/debuff VFX. */
    public int visualAt(int slot) {
        int n=0;
        if(armorBreak>0){if(n++==slot)return ARMOR_BREAK;} if(blinded>0){if(n++==slot)return BLINDED;}
        if(burn>0){if(n++==slot)return BURN;} if(charm>0){if(n++==slot)return CHARM;}
        if(confusion>0){if(n++==slot)return CONFUSION;} if(freeze>0){if(n++==slot)return FREEZE;}
        if(curse>0){if(n++==slot)return CURSE;} if(fatigue>0){if(n++==slot)return FATIGUE;}
        if(flinch>0){if(n++==slot)return FLINCH;} if(locked>0){if(n++==slot)return LOCKED;}
        if(paralysis>0){if(n++==slot)return PARALYSIS;} if(poison>0){if(n++==slot)return POISON;}
        if(protect>0){if(n++==slot)return PROTECT;} if(silence>0){if(n++==slot)return SILENCE;}
        if(sleep>0){if(n++==slot)return SLEEP;} if(wound>0){if(n++==slot)return WOUND;}
        if(possessed>0){if(n++==slot)return POSSESSED;}if(resurrection>0){if(n++==slot)return RESURRECTION;}
        if(safeguard>0){if(n++==slot)return SAFEGUARD;}if(rage>0){if(n++==slot)return RAGE;}
        if(pokerus>0){if(n++==slot)return POKERUS;}if(electricField>0){if(n++==slot)return ELECTRIC_FIELD;}
        if(fairyField>0){if(n++==slot)return FAIRY_FIELD;}if(grassField>0){if(n++==slot)return GRASS_FIELD;}
        if(psychicField>0){if(n++==slot)return PSYCHIC_FIELD;}
        return NONE;
    }

    public String label() {
        int v=visual();
        if(v==FREEZE)return "FREEZE"; if(v==SLEEP)return "SLEEP";
        if(v==PARALYSIS)return "PARALYZE"; if(v==BURN)return "BURN";
        if(v==POISON)return "POISON"; if(v==CURSE)return "CURSE";if(v==FATIGUE)return "FATIGUE";
        if(v==FLINCH)return "FLINCH";if(v==LOCKED)return "LOCKED";if(v==CONFUSION)return "CONFUSE";
        if(v==CHARM)return "CHARM"; if(v==WOUND)return "WOUND";
        if(v==BLINDED)return "BLIND"; if(v==ARMOR_BREAK)return "ARMOR-";
        if(v==SILENCE)return "SILENCE"; if(v==PROTECT)return "PROTECT";
        if(v==POSSESSED)return "POSSESSED";if(v==RESURRECTION)return "REVIVE";if(v==SAFEGUARD)return "SAFEGUARD";
        if(v==RAGE)return "RAGE";if(v==POKERUS)return "POKERUS";if(v==ELECTRIC_FIELD)return "ELECTRIC";
        if(v==FAIRY_FIELD)return "FAIRY";if(v==GRASS_FIELD)return "GRASS";if(v==PSYCHIC_FIELD)return "PSYCHIC";
        return "";
    }
}

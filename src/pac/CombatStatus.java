package pac;

/** Timed status container. Durations are deterministic simulation ticks. */
public final class CombatStatus {
    public static final int NONE=-1, ARMOR_BREAK=0, BLINDED=1, BURN=2, CHARM=3,
            CONFUSION=4, CURSE=5, FATIGUE=6, FLINCH=7, FREEZE=8, LOCKED=9,
            PARALYSIS=10, POISON=11, PROTECT=12, SILENCE=13, SLEEP=14, WOUND=15;
    public int stun, paralysis, burn, poison, freeze, sleep;
    public int confusion, charm, wound, blinded, armorBreak, silence, protect;
    private int dotClock;

    public boolean blocksAction() { return stun > 0 || freeze > 0 || sleep > 0 || charm > 0; }
    public int effectiveSpeed(int base) { return paralysis > 0 ? Math.max(1, base / 2) : base; }

    public void update(Battle battle, Unit unit) {
        if (stun > 0) stun--;
        if (paralysis > 0) paralysis--;
        if (burn > 0) burn--;
        if (poison > 0) poison--;
        if (freeze > 0) freeze--;
        if (sleep > 0) sleep--;
        if (confusion > 0) confusion--;
        if (charm > 0) charm--;
        if (wound > 0) wound--;
        if (blinded > 0) blinded--;
        if (armorBreak > 0) armorBreak--;
        if (silence > 0) silence--;
        if (protect > 0) protect--;
        dotClock++;
        if (dotClock >= 10) {
            dotClock = 0;
            if (burn > 0) battle.statusDamage(unit, Math.max(1, unit.maxHp / 25));
            if (poison > 0 && unit.alive) battle.statusDamage(unit, Math.max(1, unit.maxHp / 20));
        }
    }

    public int visual() {
        if (freeze>0) return FREEZE; if (sleep>0) return SLEEP;
        if (paralysis>0) return PARALYSIS; if (burn>0) return BURN;
        if (poison>0) return POISON; if (confusion>0) return CONFUSION;
        if (charm>0) return CHARM; if (wound>0) return WOUND;
        if (blinded>0) return BLINDED; if (armorBreak>0) return ARMOR_BREAK;
        if (silence>0) return SILENCE; if (protect>0) return PROTECT;
        return NONE;
    }

    /** Return the nth simultaneous status so the renderer can layer buff/debuff VFX. */
    public int visualAt(int slot) {
        int n=0;
        if(armorBreak>0){if(n++==slot)return ARMOR_BREAK;} if(blinded>0){if(n++==slot)return BLINDED;}
        if(burn>0){if(n++==slot)return BURN;} if(charm>0){if(n++==slot)return CHARM;}
        if(confusion>0){if(n++==slot)return CONFUSION;} if(freeze>0){if(n++==slot)return FREEZE;}
        if(paralysis>0){if(n++==slot)return PARALYSIS;} if(poison>0){if(n++==slot)return POISON;}
        if(protect>0){if(n++==slot)return PROTECT;} if(silence>0){if(n++==slot)return SILENCE;}
        if(sleep>0){if(n++==slot)return SLEEP;} if(wound>0){if(n++==slot)return WOUND;}
        return NONE;
    }

    public String label() {
        int v=visual();
        if(v==FREEZE)return "FREEZE"; if(v==SLEEP)return "SLEEP";
        if(v==PARALYSIS)return "PARALYZE"; if(v==BURN)return "BURN";
        if(v==POISON)return "POISON"; if(v==CONFUSION)return "CONFUSE";
        if(v==CHARM)return "CHARM"; if(v==WOUND)return "WOUND";
        if(v==BLINDED)return "BLIND"; if(v==ARMOR_BREAK)return "ARMOR-";
        if(v==SILENCE)return "SILENCE"; if(v==PROTECT)return "PROTECT";
        return "";
    }
}

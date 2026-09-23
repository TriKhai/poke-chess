package pac;

/** Compact secondary-effect bridge using original ability names from app.zip. */
public final class SkillEffects {
    private SkillEffects() {}

    public static void apply(Unit caster, Unit target) {
        String name = Data.skillName[caster.sp].toUpperCase();
        if (target != null && target.alive) {
        if (has(name, "TOXIC") || has(name, "POISON") || has(name, "SLUDGE") || has(name, "VENOM")) {
            target.status.poison = Math.max(target.status.poison, 50);
        } if (has(name, "BLIZZARD") || has(name, "FREEZE") || has(name, "ICE BEAM") || has(name, "ICY")) {
            target.status.freeze = Math.max(target.status.freeze, 12);
        } if (has(name, "SLEEP") || has(name, "HYPNO") || has(name, "SPORE") || has(name, "YAWN") || has(name,"SING")) {
            target.status.sleep = Math.max(target.status.sleep, 20);
        } if (has(name, "THUNDER") || has(name, "VOLT") || has(name, "PARAL") || has(name, "ELECTR") || has(name,"NUZZLE") || has(name,"STUN SPORE") || has(name,"LICK")) {
            target.status.paralysis = Math.max(target.status.paralysis, 40);
        } if (has(name, "FIRE") || has(name, "FLAME") || has(name, "BURN") || has(name, "BLAZE")) {
            target.status.burn = Math.max(target.status.burn, 40);
        } if (has(name, "ATTRACT") || has(name, "CHARM") || has(name, "SWEET KISS") || has(name,"PLAY ROUGH")) {
            target.status.charm = Math.max(target.status.charm, 20);
        } if (has(name, "CONFUS") || has(name, "DYNAMIC PUNCH") || has(name, "CHATTER") || has(name,"LICK")) {
            target.status.confusion = Math.max(target.status.confusion, 30);
        } if (has(name, "WOUND") || has(name, "CEASELESS EDGE") || has(name, "BARB BARRAGE") || has(name,"HEAL BLOCK")) {
            target.status.wound = Math.max(target.status.wound, 40);
        } if (has(name, "FLASH") || has(name, "MUD SLAP") || has(name, "SAND ATTACK") || has(name,"OCTAZOOKA")) {
            target.status.blinded = Math.max(target.status.blinded, 30);
        } if (has(name, "ACID SPRAY") || has(name, "CRUSH CLAW") || has(name, "FAKE TEARS") || has(name,"ROCK SMASH") || has(name,"HORN ATTACK")) {
            target.status.armorBreak = Math.max(target.status.armorBreak, 30);
        } if (has(name, "SILENCE") || has(name, "DISABLE") || has(name, "THROAT CHOP") || has(name,"GRUDGE")) {
            target.status.silence = Math.max(target.status.silence, 30);
        }
        if(has(name,"CURSE")||has(name,"NIGHTMARE")||has(name,"LAST RESPECTS"))target.status.curse=Math.max(target.status.curse,50);
        if(has(name,"HYPER BEAM")||has(name,"GIGATON")||has(name,"ROCK WRECKER"))caster.status.fatigue=Math.max(caster.status.fatigue,40);
        if(has(name,"BITE")||has(name,"HEADBUTT")||has(name,"AIR SLASH"))target.status.flinch=Math.max(target.status.flinch,20);
        if(has(name,"LOCK")||has(name,"ANCHOR SHOT")||has(name,"MAGNET BOMB"))target.status.locked=Math.max(target.status.locked,30);
        }
        if (has(name, "PROTECT") || has(name, "BANEFUL BUNKER"))
            caster.status.protect = Math.max(caster.status.protect, 15);
    }

    private static boolean has(String value, String token) { return value.indexOf(token) >= 0; }
}

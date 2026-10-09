package pac;

/** Compact secondary-effect bridge using original ability names from app.zip. */
public final class SkillEffects {
    private SkillEffects() {}

    public static void apply(Unit caster, Unit target) {
        apply(caster,target,caster.sp);
    }
    public static void apply(Unit caster, Unit target,int skillSp) {
        String name = Data.skillName[skillSp].toUpperCase();
        if (target != null && target.alive) {
        if (has(name, "TOXIC") || has(name, "POISON") || has(name, "SLUDGE") || has(name, "VENOM")) {
            target.status.apply(CombatStatus.POISON,50);
        } if (has(name, "BLIZZARD") || has(name, "FREEZE") || has(name, "ICE BEAM") || has(name, "ICY")) {
            target.status.apply(CombatStatus.FREEZE,12);
        } if (has(name, "SLEEP") || has(name, "HYPNO") || has(name, "SPORE") || has(name, "YAWN") || has(name,"SING")) {
            if(!PokemonPassive.immune(target,CombatStatus.SLEEP))target.status.apply(CombatStatus.SLEEP,20);
        } if (has(name, "THUNDER") || has(name, "VOLT") || has(name, "PARAL") || has(name, "ELECTR") || has(name,"NUZZLE") || has(name,"STUN SPORE") || has(name,"LICK")) {
            target.status.apply(CombatStatus.PARALYSIS,40);
        } if (has(name, "FIRE") || has(name, "FLAME") || has(name, "BURN") || has(name, "BLAZE")) {
            target.status.apply(CombatStatus.BURN,40);
        } if (has(name, "ATTRACT") || has(name, "CHARM") || has(name, "SWEET KISS") || has(name,"PLAY ROUGH")) {
            target.status.apply(CombatStatus.CHARM,20);
        } if (has(name, "CONFUS") || has(name, "DYNAMIC PUNCH") || has(name, "CHATTER") || has(name,"LICK")) {
            target.status.apply(CombatStatus.CONFUSION,30);
        } if (has(name, "WOUND") || has(name, "CEASELESS EDGE") || has(name, "BARB BARRAGE") || has(name,"HEAL BLOCK")) {
            target.status.apply(CombatStatus.WOUND,40);
        } if (has(name, "FLASH") || has(name, "MUD SLAP") || has(name, "SAND ATTACK") || has(name,"OCTAZOOKA")) {
            target.status.apply(CombatStatus.BLINDED,30);
        } if (has(name, "ACID SPRAY") || has(name, "CRUSH CLAW") || has(name, "FAKE TEARS") || has(name,"ROCK SMASH") || has(name,"HORN ATTACK")) {
            target.status.apply(CombatStatus.ARMOR_BREAK,30);
        } if (has(name, "SILENCE") || has(name, "DISABLE") || has(name, "THROAT CHOP") || has(name,"GRUDGE")) {
            target.status.apply(CombatStatus.SILENCE,30);
        }
        if(has(name,"CURSE")||has(name,"NIGHTMARE")||has(name,"LAST RESPECTS"))target.status.apply(CombatStatus.CURSE,50);
        if(has(name,"HYPER BEAM")||has(name,"GIGATON")||has(name,"ROCK WRECKER"))caster.status.apply(CombatStatus.FATIGUE,40);
        if(has(name,"BITE")||has(name,"HEADBUTT")||has(name,"AIR SLASH"))target.status.apply(CombatStatus.FLINCH,20);
        if(has(name,"LOCK")||has(name,"ANCHOR SHOT")||has(name,"MAGNET BOMB"))target.status.apply(CombatStatus.LOCKED,30);
        }
        if (has(name, "PROTECT") || has(name, "BANEFUL BUNKER"))
            caster.status.apply(CombatStatus.PROTECT,15);
    }

    private static boolean has(String value, String token) { return value.indexOf(token) >= 0; }
}

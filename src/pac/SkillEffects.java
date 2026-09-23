package pac;

/** Compact secondary-effect bridge using original ability names from app.zip. */
public final class SkillEffects {
    private SkillEffects() {}

    public static void apply(Unit caster, Unit target) {
        if (target == null || !target.alive) return;
        String name = Data.skillName[caster.sp].toUpperCase();
        if (has(name, "TOXIC") || has(name, "POISON") || has(name, "SLUDGE") || has(name, "VENOM")) {
            target.status.poison = Math.max(target.status.poison, 50);
        } else if (has(name, "BLIZZARD") || has(name, "FREEZE") || has(name, "ICE BEAM") || has(name, "ICY")) {
            target.status.freeze = Math.max(target.status.freeze, 12);
        } else if (has(name, "SLEEP") || has(name, "HYPNO") || has(name, "SPORE") || has(name, "YAWN")) {
            target.status.sleep = Math.max(target.status.sleep, 20);
        } else if (has(name, "THUNDER") || has(name, "VOLT") || has(name, "PARAL") || has(name, "ELECTR")) {
            target.status.paralysis = Math.max(target.status.paralysis, 40);
        } else if (has(name, "FIRE") || has(name, "FLAME") || has(name, "BURN") || has(name, "BLAZE")) {
            target.status.burn = Math.max(target.status.burn, 40);
        } else if (has(name, "ATTRACT") || has(name, "CHARM") || has(name, "SWEET KISS")) {
            target.status.charm = Math.max(target.status.charm, 20);
        } else if (has(name, "CONFUS") || has(name, "DYNAMIC PUNCH") || has(name, "CHATTER")) {
            target.status.confusion = Math.max(target.status.confusion, 30);
        } else if (has(name, "WOUND") || has(name, "CEASELESS EDGE") || has(name, "BARB BARRAGE")) {
            target.status.wound = Math.max(target.status.wound, 40);
        } else if (has(name, "FLASH") || has(name, "MUD SLAP") || has(name, "SAND ATTACK")) {
            target.status.blinded = Math.max(target.status.blinded, 30);
        } else if (has(name, "ACID SPRAY") || has(name, "CRUSH CLAW") || has(name, "FAKE TEARS")) {
            target.status.armorBreak = Math.max(target.status.armorBreak, 30);
        } else if (has(name, "SILENCE") || has(name, "DISABLE") || has(name, "THROAT CHOP")) {
            target.status.silence = Math.max(target.status.silence, 30);
        }
        if (has(name, "PROTECT") || has(name, "BANEFUL BUNKER"))
            caster.status.protect = Math.max(caster.status.protect, 15);
    }

    private static boolean has(String value, String token) { return value.indexOf(token) >= 0; }
}

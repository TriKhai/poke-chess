package pac;

/** Shared combat formulas ported from app/config/game/battle.ts. */
public final class CombatRules {
    private CombatRules() {}

    public static final int TICK_MS = 100;
    public static final int ON_ATTACK_MANA = 5;
    public static final int ON_DAMAGE_MANA = 4;
    public static final int DEFAULT_CRIT_CHANCE = 10;
    public static final int CRIT_POWER_PERCENT = 200;
    /** Hard cap for the combat speed stat. Further permanent or battle buffs have no effect. */
    public static final int MAX_SPEED = 120;
    public static final int FIGHT_DURATION_TICKS = 45000 / TICK_MS;
    public static final int HARD_LIMIT_TICKS = 65000 / TICK_MS;

    /** Original formula: duration = base / (0.4 + speed * 0.007). */
    public static int cooldownTicks(int baseMs, int speed) {
        speed = cappedSpeed(speed);
        int denominator1000 = 400 + speed * 7;
        int ms = (baseMs * 1000 + denominator1000 / 2) / denominator1000;
        int ticks = (ms + TICK_MS - 1) / TICK_MS;
        return ticks < 1 ? 1 : ticks;
    }

    public static int cappedSpeed(int speed) {
        if (speed < 1) return 1;
        return speed > MAX_SPEED ? MAX_SPEED : speed;
    }

    public static int physicalDamage(int raw, int defense) {
        return reducedDamage(raw, defense);
    }

    public static int specialDamage(int raw, int specialDefense) {
        return reducedDamage(raw, specialDefense);
    }

    /** Original ARMOR_FACTOR is 0.05: damage / (1 + 0.05 * armor). */
    private static int reducedDamage(int raw, int armor) {
        if (raw <= 0) return 0;
        if (armor < 0) armor = 0;
        int value = raw * 20 / (20 + armor);
        return value < 1 ? 1 : value;
    }
}

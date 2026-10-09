package pac;

/** A unit fighting on the battle grid. */
public final class Unit {
    public static final int MOVING = 0, ATTACKING = 1, CASTING = 2, IDLE = 3, DEAD = 4;
    public int sp, side;
    public int type1=-1,type2=-1;
    public int primaryType(){return type1<0?Data.t1[sp]:type1;}
    public int secondaryType(){return type1<0?Data.t2[sp]:type2;}
    public int x, y, px, py;
    /** Consecutive failed path attempts; used to abandon a blocked target. */
    public int stuck;
    /** Source-parity cell travel duration/progress, in deterministic 100ms ticks. */
    public int moveTicks, moveLeft;
    public int hp, prevHp, maxHp, atk, def, speDef, speed, range, cd, cdLeft, mana, prevMana, maxMana;
    public int stun, shield;
    public int dodge, crit, lifesteal, skillBonus, regen;
    public int critPower, basicCount, damageBlockEvery, damageHitCount, flyCharges;
    /** Per-battle counters/flags owned by ItemEffects. */
    public int itemAttackCount, itemDamageCount, itemCastCount, itemKillCount, itemSoulClock, itemGreenClock, itemRibbonClock;
    public boolean itemSmokeUsed, itemReviveUsed, itemShieldBurstUsed;
    /** Run Berry passives active for this combat. */
    public int fruitMask;
    /** Run-only Shiny form; visual marker plus ShinyData combat bonuses. */
    public boolean shiny;
    public boolean mega;
    /** Primal, Origin, Ancient Paradox or Zygarde form selected for this run. */
    public int specialForm;
    public boolean fruitReviveUsed, fruitSkillBlocked;
    public int fruitUsedMask;
    public boolean passiveTree;
    public int passiveAttackBonus;
    public int passiveTimer;
    /** Runtime-only copied ability source used by Skill Swap. */
    public int copiedSkillSp=-1;
    public int[] synTier = new int[Data.NT];
    /** Live combat statistics used by the persistent team inspector. */
    public int damageDealt, damageTaken, damageBlocked, healingDone, shieldDone;
    /** Original held-item ids, copied into combat for the team inspector. */
    public int[] items = { -1, -1, -1 };
    public boolean alive = true;
    public int state = MOVING;
    public final CombatStatus status = new CombatStatus();
    public int hit;      // ticks of hit flash left
    public int cast;     // ticks of cast flash left
    /** short visual-only attack lunge; target cell is kept even if the target dies */
    public int attack, attackX, attackY;
    public int facing; // PMD atlas direction: 0 down, 2 right, 4 up, 6 left
    public Unit target;
}

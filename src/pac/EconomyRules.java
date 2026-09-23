package pac;

/** Deterministic offline economy rules, kept separate for regression testing. */
public final class EconomyRules {
    private EconomyRules(){}
    public static final int BASE_INCOME=5, INTEREST_CAP=5;
    public static int interest(int gold){return Math.min(INTEREST_CAP,Math.max(0,gold/10));}
    public static int streakBonus(int streak){int s=Math.abs(streak);return s>=6?3:(s>=4?2:(s>=2?1:0));}
    public static int victoryBonus(boolean won){return won?1:0;}
    public static int income(int gold,int streak,boolean won,int itemGold){return BASE_INCOME+interest(gold)+streakBonus(streak)+victoryBonus(won)+Math.max(0,itemGold);}
    public static int playerDamage(int round,int enemyTierSum){return 2+round/2+enemyTierSum;}
}

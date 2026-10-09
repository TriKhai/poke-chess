package pac;

/** Arena-only tuning; never modifies chess stats or the player's base stats. */
public final class SurvivalBalance {
 private SurvivalBalance(){}
 public static final int ULTIMATE_MS=30000,DASH_MANA=20;
 public static int killGold(int n,boolean boss){int w=wave(n);return boss?30+Math.min(70,w*2):1+Math.min(4,w/5);}
 private static int wave(int n){return Math.max(1,Math.min(10000,n));}
 public static int enemyHp(int species,int n,int role){int w=wave(n),late=Math.max(0,w-10);long hp=24+Data.hp[species]/3+w*5+late*2;if(w>=10)hp=hp*(200L+late*15L)/100;hp=Math.min(1000000L,hp);return role==3?(int)hp*3:role==2?Math.max(15,(int)hp*65/100):(int)hp;}
 public static int enemyAtk(int species,int n){int w=wave(n),late=Math.max(0,w-10);long power=Math.max(2,Data.atk[species]/3)+w/4+late/6;if(w>=10)power=power*(170L+late*10L)/100;return (int)Math.min(1000000L,power);}
 public static int killXp(int n,boolean boss){return boss?Math.min(30,12+wave(n)/5):3;}
 public static int bossCooldown(int type,int n){int base=type==Data.T_ELEC?2800:3400;return Math.max(2200,base-Math.min(60,wave(n))*10);}
 public static int bossWarning(int type){return type==Data.T_ELEC?800:type==Data.T_ICE||type==Data.T_WATER?1000:900;}
 public static int spawnInterval(int n){return Math.max(320,850-wave(n)*25);}
}

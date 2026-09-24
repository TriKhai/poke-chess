package pac;
/** Pure progression/filter rules shared by Farm, Gacha and Camp. */
public final class ExploreRules{
 private ExploreRules(){}public static int gen(int sp){return sp<151?1:(sp<251?2:3);}public static int hourlyBalls(int sp){return sp<0?0:Math.max(1,Data.category[sp]+1);}public static boolean matches(int sp,int kind,int value){if(!Data.isBase(sp))return false;if(kind==0)return gen(sp)==value;if(kind==1)return Data.t1[sp]==value||Data.t2[sp]==value;return Data.category[sp]==value;}
 public static int pick(Rng r,int kind,int value){int count=0;for(int i=0;i<Data.N;i++)if(matches(i,kind,value))count++;if(count==0)return -1;int q=r.nextInt(count);for(int i=0;i<Data.N;i++)if(matches(i,kind,value)&&q--==0)return i;return -1;}
 public static int pickAnyBase(Rng r){int count=0;for(int i=0;i<Data.N;i++)if(Data.isBase(i)&&Data.category[i]!=6)count++;if(count==0)return -1;int q=r.nextInt(count);for(int i=0;i<Data.N;i++)if(Data.isBase(i)&&Data.category[i]!=6&&q--==0)return i;return -1;}
 public static boolean farmLegendaryRound(int enemyNumber){return enemyNumber>0&&enemyNumber%10==0;}
 public static int farmReward(int kills){return Math.max(0,kills)/5;}
 public static boolean campLocked(int slot){return slot>=0&&slot<3&&Save.camp[slot]>=0&&Save.campLockUntil[slot]>0;}
 public static long campUnlockRemaining(int slot,long now){if(!campLocked(slot))return 0;long left=Save.campLockUntil[slot]-now;return left>0?left:0;}
 public static int campRate(){int rate=0;for(int i=0;i<3;i++)if(campLocked(i))rate+=hourlyBalls(Save.camp[i]);return rate;}
 public static int campSlotReward(int slot,long now){if(!campLocked(slot)||Save.campClaimAt[slot]<=0||now<=Save.campClaimAt[slot])return 0;long hours=(now-Save.campClaimAt[slot])/3600L;if(hours>24)hours=24;return (int)hours*hourlyBalls(Save.camp[slot]);}
 public static int campReward(long now){int reward=0;for(int i=0;i<3;i++)reward+=campSlotReward(i,now);return reward;}
 public static int claimCamp(long now){int reward=0;for(int i=0;i<3;i++){int got=campSlotReward(i,now);if(got<=0)continue;long hours=(now-Save.campClaimAt[i])/3600L;if(hours>24){hours=24;Save.campClaimAt[i]=now;}else Save.campClaimAt[i]+=hours*3600L;reward+=got;}if(reward>0)Save.balls+=reward;Save.campStart=now;return reward;}
}

package pac;
/** Pure progression/filter rules shared by Farm, Gacha and Camp. */
public final class ExploreRules{
 private ExploreRules(){}public static int gen(int sp){return sp<151?1:(sp<251?2:3);}public static int hourlyBalls(int sp){return sp<0?0:Math.max(1,Data.category[sp]+1);}public static boolean matches(int sp,int kind,int value){if(!Data.isBase(sp))return false;if(kind==0)return gen(sp)==value;if(kind==1)return Data.t1[sp]==value||Data.t2[sp]==value;return Data.category[sp]==value;}
 public static int pick(Rng r,int kind,int value){int count=0;for(int i=0;i<Data.N;i++)if(matches(i,kind,value))count++;if(count==0)return -1;int q=r.nextInt(count);for(int i=0;i<Data.N;i++)if(matches(i,kind,value)&&q--==0)return i;return -1;}
 public static int pickAnyBase(Rng r){int count=0;for(int i=0;i<Data.N;i++)if(Data.isBase(i)&&Data.category[i]!=6)count++;if(count==0)return -1;int q=r.nextInt(count);for(int i=0;i<Data.N;i++)if(Data.isBase(i)&&Data.category[i]!=6&&q--==0)return i;return -1;}
 public static boolean farmLegendaryRound(int enemyNumber){return enemyNumber>0&&enemyNumber%10==0;}
 public static int farmReward(int kills){return Math.max(0,kills)/5;}
 public static int campReward(long now){long start=Save.campStart;if(start<=0||now<=start)return 0;long hours=(now-start)/3600L;if(hours>24)hours=24;int rate=0;for(int i=0;i<Save.camp.length;i++)rate+=hourlyBalls(Save.camp[i]);return (int)hours*rate;}
 public static int claimCamp(long now){int reward=campReward(now);if(reward>0)Save.balls+=reward;Save.campStart=now;return reward;}
}

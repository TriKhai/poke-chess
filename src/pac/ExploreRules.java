package pac;
/** Pure progression/filter rules shared by Farm, Gacha and Camp. */
public final class ExploreRules{
 private ExploreRules(){}public static int gen(int sp){return Data.generation(sp);}public static int hourlyBalls(int sp){return sp<0?0:Math.max(1,Data.category[sp]+1);}public static boolean matches(int sp,int kind,int value){if(!Data.isBase(sp))return false;if(kind==0)return gen(sp)==value;if(kind==1)return Data.t1[sp]==value||Data.t2[sp]==value;return Data.category[sp]==value;}
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
 public static void recordCatch(int sp,long now){recordQuestProgress(sp,now);}
 public static void recordDefeat(int sp,long now){recordQuestProgress(sp,now);}
 public static int questProgress(int gen,int type){if(gen<1||gen>Data.BATTLE_MAX_GEN||type<0||type>=Data.NT)return 0;return Save.exploreQuestCaught[(gen-1)*Data.NT+type]%10;}
 private static void recordQuestProgress(int sp,long now){int gen=gen(sp),a=Data.t1[sp],b=Data.t2[sp];progressType(gen,a,now);if(b!=a)progressType(gen,b,now);}
 private static void progressType(int gen,int type,long now){if(gen<1||gen>Data.BATTLE_MAX_GEN||type<0||type>=Data.NT||legendaryForType(gen,type)<0)return;int at=(gen-1)*Data.NT+type;Save.exploreQuestCaught[at]++;Save.exploreTypeCaught[type]++;if(Save.exploreQuestCaught[at]%10==0&&!legendaryActive(now)){Save.exploreLegendType=type;Save.exploreLegendUntil=now+300;Save.exploreLegendSpecies=-1;Save.exploreLegendGen=gen;}}
 public static int uniqueCaught(int gen){int n=0;for(int sp=0;sp<Data.N;sp++)if(ExploreRules.gen(sp)==gen&&Save.exploreSpeciesCaught[sp]>0)n++;return n;}
 public static int mapSpeciesCount(int gen){int n=0;for(int sp=0;sp<Data.N;sp++)if(Data.isBase(sp)&&ExploreRules.gen(sp)==gen)n++;return n;}
 /** Awards newly crossed map milestones and returns their bit mask (1/2/4). */
 public static int mapCompletionGold(int gen){return gen<1?0:(gen>Data.BATTLE_MAX_GEN?3:gen);}
 public static int claimMapMilestones(int gen){if(gen<1||gen>Data.BATTLE_MAX_GEN)return 0;int total=mapSpeciesCount(gen),got=uniqueCaught(gen),old=Save.exploreMapRewardMask[gen-1],now=old;if(total<=0)return 0;if(got*4>=total)now|=1;if(got*2>=total)now|=2;if(got>=total)now|=4;int fresh=now&~old;if((fresh&1)!=0)Save.silverBalls++;if((fresh&2)!=0)Save.silverBalls+=2;if((fresh&4)!=0)Save.goldBalls+=mapCompletionGold(gen);Save.exploreMapRewardMask[gen-1]=now;return fresh;}
 public static int tripReward(int caught,int defeated,int legends){return Math.max(0,caught/2+defeated/3+legends*3);}
 public static boolean legendaryActive(long now){if(Save.exploreLegendType<0||now>=Save.exploreLegendUntil){if(now>=Save.exploreLegendUntil)clearLegendary();return false;}return true;}
 public static boolean legendaryActive(int gen,long now){return legendaryActive(now)&&(Save.exploreLegendGen==0||Save.exploreLegendGen==gen);}
 public static int pickLegendary(int gen,Rng rng){if(Save.exploreLegendGen!=0&&Save.exploreLegendGen!=gen)return-1;Save.exploreLegendGen=gen;int saved=Save.exploreLegendSpecies,t=Save.exploreLegendType;if(saved>=0&&saved<Data.N&&Data.generation(saved)==gen&&Data.category[saved]==6&&(Data.t1[saved]==t||Data.t2[saved]==t))return saved;int n=0;for(int sp=0;sp<Data.N;sp++)if(Data.isBase(sp)&&Data.generation(sp)==gen&&Data.category[sp]==6&&(Data.t1[sp]==t||Data.t2[sp]==t))n++;if(n==0)return-1;int q=rng.nextInt(n);for(int sp=0;sp<Data.N;sp++)if(Data.isBase(sp)&&Data.generation(sp)==gen&&Data.category[sp]==6&&(Data.t1[sp]==t||Data.t2[sp]==t)&&q--==0)return sp;return-1;}
 public static void clearLegendary(){Save.exploreLegendType=-1;Save.exploreLegendUntil=0;Save.exploreLegendSpecies=-1;Save.exploreLegendGen=0;}
 public static int legendaryForType(int gen,int type){for(int sp=0;sp<Data.N;sp++)if(Data.isBase(sp)&&Data.generation(sp)==gen&&Data.category[sp]==6&&(Data.t1[sp]==type||Data.t2[sp]==type))return sp;return-1;}
 public static int legendaryCountForType(int gen,int type){int n=0;for(int sp=0;sp<Data.N;sp++)if(Data.isBase(sp)&&Data.generation(sp)==gen&&Data.category[sp]==6&&(Data.t1[sp]==type||Data.t2[sp]==type))n++;return n;}
 public static int legendaryAt(int gen,int type,int rank){for(int sp=0;sp<Data.N;sp++)if(Data.isBase(sp)&&Data.generation(sp)==gen&&Data.category[sp]==6&&(Data.t1[sp]==type||Data.t2[sp]==type)&&rank--==0)return sp;return-1;}
 public static int questTypeAt(int gen,int rank){for(int t=0;t<18;t++)if(legendaryForType(gen,t)>=0&&rank--==0)return t;return-1;}
 public static int questCount(int gen){int n=0;for(int t=0;t<18;t++)if(legendaryForType(gen,t)>=0)n++;return n;}
}

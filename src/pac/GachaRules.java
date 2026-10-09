package pac;

import java.util.Calendar;

/** Daily Gacha pool and 100-point non-duplicate Legendary pity rules. */
public final class GachaRules{
    private GachaRules(){}
    /** 0=Gen1-3, 1=Gen4-6, 2=Gen7-9, 3=all. */
    public static int groupForDay(int day){if(day==Calendar.SUNDAY)return 3;if(day==Calendar.MONDAY||day==Calendar.TUESDAY)return 0;if(day==Calendar.WEDNESDAY||day==Calendar.THURSDAY)return 1;return 2;}
    public static int todayGroup(){return groupForDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));}
    public static boolean allowsGen(int group,int gen){return group==3||(group==0&&gen<=3)||(group==1&&gen>=4&&gen<=6)||(group==2&&gen>=7);}
    public static String scheduleName(int group){return group==0?"GEN 1-2-3":(group==1?"GEN 4-5-6":(group==2?"GEN 7-8-9":Lang.t("TẤT CẢ GEN","ALL GENERATIONS")));}
    public static int generationOfDex(int dex){if(dex<=151)return 1;if(dex<=251)return 2;if(dex<=386)return 3;for(int i=0;i<CollectionDex.COUNT;i++)if(CollectionDex.DEX[i]==dex)return CollectionDex.GEN[i];return 1;}
    public static boolean legendary(int dex){if(dex<=Data.CORE_N)return Data.category[dex-1]==6;for(int i=0;i<CollectionDex.COUNT;i++)if(CollectionDex.DEX[i]==dex)return "Legendary".equals(CollectionDex.CATEGORY[i]);return false;}
    private static int weight(int dex){if(legendary(dex))return 1;if(dex<=Data.CORE_N){int c=Data.category[dex-1];return c>=5?2:(c>=3?5:(c>=2?10:20));}for(int i=0;i<CollectionDex.COUNT;i++)if(CollectionDex.DEX[i]==dex){String c=CollectionDex.CATEGORY[i];return "Unique".equals(c)||"Special".equals(c)?2:("Ultra".equals(c)||"Epic".equals(c)?5:("Rare".equals(c)?10:20));}return 1;}
    public static int pick(Rng rng,int group,boolean pity){
        int total=0;for(int i=1;i<=Data.CORE_N;i++)if(allowsGen(group,generationOfDex(i))&&(!pity||legendary(i))&&(!pity||!Save.ownsDex(i)))total+=pity?1:weight(i);
        for(int i=0;i<CollectionDex.COUNT;i++){int d=CollectionDex.DEX[i];if(allowsGen(group,CollectionDex.GEN[i])&&(!pity||legendary(d))&&(!pity||!Save.ownsDex(d)))total+=pity?1:weight(d);}
        if(total==0&&pity){for(int i=1;i<=Data.CORE_N;i++)if(legendary(i)&&!Save.ownsDex(i))total++;for(int i=0;i<CollectionDex.COUNT;i++)if(legendary(CollectionDex.DEX[i])&&!Save.ownsDex(CollectionDex.DEX[i]))total++;group=3;}
        if(total==0)return 1;int q=rng.nextInt(total);
        for(int i=1;i<=Data.CORE_N;i++)if(allowsGen(group,generationOfDex(i))&&(!pity||legendary(i))&&(!pity||!Save.ownsDex(i))){q-=pity?1:weight(i);if(q<0)return i;}
        for(int i=0;i<CollectionDex.COUNT;i++){int d=CollectionDex.DEX[i];if(allowsGen(group,CollectionDex.GEN[i])&&(!pity||legendary(d))&&(!pity||!Save.ownsDex(d))){q-=pity?1:weight(d);if(q<0)return d;}}
        return 1;
    }
    private static boolean featured(int dex,int[] list){if(list==null)return false;for(int i=0;i<list.length;i++)if(list[i]==dex)return true;return false;}
    /** Daily draw: every Legendary, including an early one, is restricted to today's cards. */
    public static int pickFeatured(Rng rng,int group,boolean pity,int[] list){
        int total=0;
        if(pity){for(int i=0;i<list.length;i++)if(list[i]>0&&!Save.ownsDex(list[i]))total++;if(total==0)for(int i=0;i<list.length;i++)if(list[i]>0)total++;if(total==0)return 1;int q=rng.nextInt(total);boolean needNew=false;for(int i=0;i<list.length;i++)if(list[i]>0&&!Save.ownsDex(list[i]))needNew=true;for(int i=0;i<list.length;i++)if(list[i]>0&&(!needNew||!Save.ownsDex(list[i]))&&q--==0)return list[i];return list[0];}
        for(int d=1;d<=Data.CORE_N;d++)if(allowsGen(group,generationOfDex(d))&&(!legendary(d)||featured(d,list)))total+=weight(d);
        for(int i=0;i<CollectionDex.COUNT;i++){int d=CollectionDex.DEX[i];if(allowsGen(group,CollectionDex.GEN[i])&&(!legendary(d)||featured(d,list)))total+=weight(d);}
        if(total<=0)return 1;int q=rng.nextInt(total);
        for(int d=1;d<=Data.CORE_N;d++)if(allowsGen(group,generationOfDex(d))&&(!legendary(d)||featured(d,list))){q-=weight(d);if(q<0)return d;}
        for(int i=0;i<CollectionDex.COUNT;i++){int d=CollectionDex.DEX[i];if(allowsGen(group,CollectionDex.GEN[i])&&(!legendary(d)||featured(d,list))){q-=weight(d);if(q<0)return d;}}
        return 1;
    }
    public static int[] featuredLegendaries(int group,int max){int[] out=new int[max];for(int i=0;i<max;i++)out[i]=-1;int n=0;for(int d=1;d<=Data.CORE_N&&n<max;d++)if(allowsGen(group,generationOfDex(d))&&legendary(d))out[n++]=d;for(int i=0;i<CollectionDex.COUNT&&n<max;i++){int d=CollectionDex.DEX[i];if(allowsGen(group,CollectionDex.GEN[i])&&legendary(d))out[n++]=d;}return out;}
    public static int[] randomFeaturedLegendaries(int group,int count,Rng rng){
        int total=0;for(int d=1;d<=Data.CORE_N;d++)if(allowsGen(group,generationOfDex(d))&&legendary(d))total++;for(int i=0;i<CollectionDex.COUNT;i++)if(allowsGen(group,CollectionDex.GEN[i])&&legendary(CollectionDex.DEX[i]))total++;
        if(count>total)count=total;int[] pool=new int[total];int n=0;for(int d=1;d<=Data.CORE_N;d++)if(allowsGen(group,generationOfDex(d))&&legendary(d))pool[n++]=d;for(int i=0;i<CollectionDex.COUNT;i++){int d=CollectionDex.DEX[i];if(allowsGen(group,CollectionDex.GEN[i])&&legendary(d))pool[n++]=d;}
        for(int i=0;i<count;i++){int p=i+rng.nextInt(total-i),v=pool[i];pool[i]=pool[p];pool[p]=v;}int[] out=new int[count];for(int i=0;i<count;i++)out[i]=pool[i];return out;
    }
    /** Stable for the whole local calendar day; changes after midnight with the weekday pool. */
    public static int[] dailyFeaturedLegendaries(int group,int count){Calendar c=Calendar.getInstance();int key=c.get(Calendar.YEAR)*372+c.get(Calendar.MONTH)*31+c.get(Calendar.DATE);return featuredForDay(group,count,key);}
    public static int[] featuredForDay(int group,int count,int dayKey){return randomFeaturedLegendaries(group,count,new Rng(dayKey*37+group*1009+0x47414348));}
}

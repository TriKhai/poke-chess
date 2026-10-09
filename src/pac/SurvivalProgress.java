package pac;

/** Arena-only progression, independent from owned copies and autochess tiers. */
public final class SurvivalProgress {
 private SurvivalProgress(){}
 public static boolean legendary(int sp){return Data.category[sp]==6;}
 private static int previous(int sp){for(int i=0;i<Data.N;i++)if(i!=sp&&Data.evo[i]==sp)return i;return -1;}
 public static int startSpecies(int selected){if(legendary(selected))return selected;int s=selected;for(int n=0;n<Data.N;n++){int p=previous(s);if(p<0){int base=Data.fam[s];if(base>=0&&base<Data.N&&base!=s){s=base;continue;}break;}s=p;}return s;}
 public static int nextSpecies(int current,int selected){if(current!=selected){int s=selected;for(int n=0;n<Data.N;n++){int p=previous(s);if(p==current)return s;if(p<0)break;s=p;}if(Data.fam[current]==Data.fam[selected]&&Data.tier[selected]>Data.tier[current]){if(Data.tier[selected]==Data.tier[current]+1)return selected;for(int i=0;i<Data.N;i++)if(Data.fam[i]==Data.fam[selected]&&Data.tier[i]==Data.tier[current]+1)return i;}}return Data.evo[current]>=0?Data.evo[current]:current;}
 public static int speciesAt(int selected,int level){int s=startSpecies(selected);if(legendary(selected))return s;int steps=level>=10?2:level>=5?1:0;while(steps-->0){int next=nextSpecies(s,selected);if(next<0)break;s=next;}return s;}
 public static int skillTier(int level){return level>=15?4:level>=10?3:level>=5?2:1;}
 public static int effect(int type){if(type==Data.T_FIRE||type==Data.T_GRASS||type==Data.T_POISON||type==Data.T_FLORA)return 1;if(type==Data.T_WATER||type==Data.T_AQUATIC||type==Data.T_DRAGON||type==Data.T_PSY||type==Data.T_GHOST)return 2;if(type==Data.T_ELEC)return 3;if(type==Data.T_ICE||type==Data.T_FAIRY)return 4;if(type==Data.T_ROCK||type==Data.T_STEEL||type==Data.T_FIGHT||type==Data.T_GROUND||type==Data.T_FOSSIL)return 5;return 2;}
}

package pac;

public final class SurvivalEvolutionTest {
 private static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 private static int dex(int n){return Data.speciesForDex(n);}
 private static void level(SurvivalRun r,int target){while(r.level<target){if(r.elementPending)r.chooseElement(0);while(r.pendingLevels>0)check(r.choose(0),"buff selection blocked");r.gainXp(r.xpNeeded());}}
 private static void chooseSpecies(SurvivalRun r,int species){for(int i=0;i<r.variantCount();i++)if(r.variantSpecies(i)==species&&r.variantForm(i)==0){check(r.chooseVariant(i),"branch confirmation");return;}throw new RuntimeException("branch missing");}
 public static void run(){
  SurvivalRun r=new SurvivalRun(dex(43),178);level(r,5);if(r.variantPending>0)r.chooseVariant(0);level(r,10);check(r.variantPending>0,"Oddish branches missing");chooseSpecies(r,dex(182));check(r.sp==dex(182),"Bellossom selected");
  r=new SurvivalRun(dex(60),179);level(r,5);if(r.variantPending>0)r.chooseVariant(0);level(r,10);chooseSpecies(r,dex(186));check(r.sp==dex(186),"Politoed selected");
  r=new SurvivalRun(dex(700),180);level(r,5);check(r.variantCount()>=8,"all Eevee options");chooseSpecies(r,dex(136));level(r,10);check(r.sp==dex(136),"chosen Eevee branch overwritten");
  r=new SurvivalRun(dex(263),181);level(r,5);check(r.chooseVariant(1)&&r.form==75,"Galar Linoone");level(r,10);check(r.sp==dex(862),"regional evolution froze");
  r=new SurvivalRun(dex(79),182);level(r,5);int count=r.variantCount();for(int i=0;i<count;i++){SurvivalRun c=new SurvivalRun(dex(79),183+i);level(c,5);check(c.chooseVariant(i)&&c.type1()>=0,"Slowpoke choice");}
  r=new SurvivalRun(dex(26),184);r.level=9;r.gainXp(r.xpNeeded());r.chosenSkillType=Data.T_NORMAL;check(r.chooseVariant(true)&&r.attackType()==r.type1(),"stale element after form");
  check(!r.chooseVariant(-1)&&!r.chooseVariant(100),"invalid choice");
  System.out.println("SurvivalEvolutionTest OK: Oddish/Poliwag, 8 Eevee branches, regional continuation, all Slowpoke choices and element refresh");
 }
}

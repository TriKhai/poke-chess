package pac;

public final class SurvivalVolleyTest {
 private static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 private static int rays(SurvivalRun r){int n=0;for(int i=0;i<SurvivalRun.MAX_RAYS;i++)if(r.rayLife[i]>0&&!r.rayEnemy[i])n++;return n;}
 private static boolean buff(SurvivalRun r,int id){r.pendingLevels=1;r.choices[0]=id;return r.choose(0);}
 public static void run(){
  SurvivalRun r=new SurvivalRun(3,177);
  check(r.shotCount==1&&r.burstCount==1,"default shot");
  for(int i=0;i<4;i++)check(buff(r,SurvivalRun.MULTISHOT),"fan upgrade");
  check(!buff(r,SurvivalRun.MULTISHOT)&&r.shotCount==5,"fan cap");
  for(int i=0;i<2;i++)check(buff(r,SurvivalRun.BURST),"burst upgrade");
  check(!buff(r,SurvivalRun.BURST)&&r.burstCount==3,"burst cap");
  r.pendingLevels=0;r.level=15;int mana=r.mana;r.dir=2;check(r.attack()&&rays(r)==5,"first volley");
  int positive=0,negative=0;for(int i=0;i<5;i++){if(r.rayVY[i]>0)positive++;if(r.rayVY[i]<0)negative++;}
  check(positive==2&&negative==2,"fan symmetry");
  r.pendingLevels=1;r.update(240,0,0);check(rays(r)==5,"modal fired burst");r.pendingLevels=0;
  r.update(240,1,0);check(rays(r)==15&&r.mana==mana,"moving burst or mana duplication");
  check(!r.attack(),"cooldown overlap");r.finish();r.update(240,0,0);check(rays(r)==0,"ended burst");
  r=new SurvivalRun(3,178);int old=r.attackRadius();for(int i=0;i<3;i++)check(buff(r,SurvivalRun.REACH),"range buff");
  check(r.attackRadius()>old&&r.range<=6&&!buff(r,SurvivalRun.REACH),"range cap");
  for(int n=0;n<200;n++){r.gainXp(r.xpNeeded());if(r.elementPending)r.chooseElement(0);if(r.variantPending>0)r.chooseVariant(false);for(int i=0;i<3;i++)check(r.choices[i]!=SurvivalRun.REACH,"capped range offered");while(r.pendingLevels>0)r.choose(0);}
  r=new SurvivalRun(3,179);for(int i=0;i<SurvivalRun.MAX_RAYS;i++)r.rayLife[i]=1000;
  check(!r.attack()&&r.normalCd==0,"full pool consumed attack");
  r=new SurvivalRun(3,180);check(r.shotCount==1&&r.burstCount==1,"retry retained buffs");
  System.out.println("SurvivalVolleyTest OK: stacked fan/burst, caps, moving fire, pause, pool and retry");
 }
}

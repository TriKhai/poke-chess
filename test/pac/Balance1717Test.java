package pac;

/** Deterministic arena balance checks, not emulator playtesting. */
public final class Balance1717Test {
 private static void check(boolean b,String message){if(!b)throw new RuntimeException(message);}
 private static void enemy(SurvivalRun r,int role){r.enemySp[0]=0;r.ex[0]=r.prevEx[0]=r.x+20*256;r.ey[0]=r.prevEy[0]=r.y;r.ehp[0]=r.emax[0]=1;r.ecool[0]=10000;r.enemyRole[0]=role;r.edamage[0]=8;r.target=0;}
 public static void run(){
  for(int wave=1;wave<=500;wave++){check(SurvivalBalance.enemyHp(0,wave,0)>=SurvivalBalance.enemyHp(0,wave-1,0),"nonmonotonic enemy health");check(SurvivalBalance.enemyAtk(0,wave)>=SurvivalBalance.enemyAtk(0,wave-1),"nonmonotonic enemy attack");check(SurvivalBalance.spawnInterval(wave)>=320,"spawn cadence unbounded");check(SurvivalBalance.bossCooldown(Data.T_ELEC,wave)>=2200,"boss attack spam");check(SurvivalBalance.killXp(wave,true)>SurvivalBalance.killXp(wave,false)&&SurvivalBalance.killXp(wave,true)<=30,"boss XP tuning");}
  check(SurvivalBalance.enemyHp(0,Integer.MAX_VALUE,3)>0&&SurvivalBalance.enemyAtk(0,Integer.MAX_VALUE)>0,"late wave overflow");
  SurvivalRun regular=new SurvivalRun(3,17170),boss=new SurvivalRun(3,17170);regular.wave=boss.wave=5;enemy(regular,1);enemy(boss,3);regular.attack();boss.attack();regular.update(20,0,0);boss.update(20,0,0);check(regular.kills==1&&boss.kills==1&&boss.level>regular.level,"boss kill not rewarding extra XP");
  int shinyCount=0,megaCount=0;
  for(int sp=0;sp<Data.CORE_N;sp++){
   SurvivalRun r=new SurvivalRun(sp,17171);r.sp=sp;r.level=15;
   if(MegaData.available(sp)){r.pendingLevels=1;r.choices[0]=SurvivalRun.MEGA;check(r.choose(0),"Mega rejected");megaCount++;}
   if(ShinyData.available(sp)){r.pendingLevels=1;r.choices[0]=SurvivalRun.SHINY;check(r.choose(0)&&r.hp==r.maxHp&&r.mana==r.maxMana,"Shiny stats/refill failed");check(r.ultimatePower()==CombatRules.specialDamage(r.attackPower()*(2+r.skillTier())*125/100,0),"Shiny ultimate percentage mismatch");check(r.specialDefense>Data.speDef[sp],"Shiny resistance unused");if(ShinyData.role(sp)==0){check(r.shield==r.maxHp/10,"Shiny tank shield missing");int shield=r.shield;r.pendingLevels=1;r.choices[0]=SurvivalRun.POWER;check(r.choose(0)&&r.shield==shield,"ordinary buff recharged Shiny shield");}if(ShinyData.role(sp)==2)check(r.criticalChance==5,"Shiny ranged critical missing");shinyCount++;}
  }
  for(int sp=0;sp<Data.CORE_N;sp++)if(ShinyData.role(sp)==0){SurvivalRun r=new SurvivalRun(sp,17172);r.sp=sp;r.level=12;r.pendingLevels=1;r.choices[0]=SurvivalRun.SHINY;check(r.choose(0),"tank setup failed");enemy(r,3);r.warning[0]=20;r.warnX[0]=r.x;r.warnY[0]=r.y;int hp=r.hp,shield=r.shield;r.update(20,0,0);check(r.shield<shield&&r.hp==hp,"shield failed to absorb boss spell");break;}
  SurvivalRun paused=new SurvivalRun(3,17173);paused.mana=paused.maxMana;enemy(paused,1);paused.ehp[0]=10000;check(paused.ultimate(),"cooldown setup failed");paused.pendingLevels=1;paused.update(250,0,0);check(paused.skillCd==30000,"choice menu consumed cooldown");paused.pendingLevels=0;paused.buffTime[3]=100000;for(int i=0;i<120;i++)paused.update(250,0,0);check(paused.skillCd==0,"30-second ultimate never ready");
  System.out.println("Balance1717Test OK: 500-wave curves, boss XP/cadence, "+shinyCount+" Shiny / "+megaCount+" Mega checks, shields, resistance, critical and 30s cooldown");
 }
}

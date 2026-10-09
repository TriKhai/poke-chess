package pac;

/** Pure-logic regression tests for the four optional chess modes. */
public final class ExtraChessTest {
 private static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 private static Battle result(Run r,boolean won){Battle b=r.makeBattle();b.over=true;b.winner=won?0:1;return b;}
 private static void starter(Run r){int[] types=new int[9];r.typePackageChoices(types);r.choosePoolTypes(types,0);int[] picks=new int[3];r.starterChoices(picks);check(picks[0]>=0,"starter missing");r.chooseStarter(picks[0]);}
 public static void run(){
  for(int seed=1;seed<=30;seed++){
   Run mono=new Run(seed,Run.MODE_MONOTYPE);starter(mono);for(int t=0;t<mono.poolTypes.length;t++)check(mono.poolTypes[t]==mono.poolType,"not a single type");for(int i=0;i<Data.N;i++)if(mono.pool[i]>0)check(Data.t1[i]==mono.poolType||Data.t2[i]==mono.poolType,"wrong type in pool");for(int roll=0;roll<20;roll++){mono.rollShop();for(int i=0;i<5;i++)if(mono.shop[i]>=0)check(mono.matchesChosenPool(mono.shop[i]),"wrong shop type");}
   Run fixed=new Run(seed,Run.MODE_RANDOM_TEAM);check(fixed.boardCount()==9&&fixed.level==9,"fixed team not nine");int gold=fixed.gold;check(!fixed.reroll()&&!fixed.buy(0)&&!fixed.sell(8)&&fixed.gold==gold,"fixed-team economy bypass");for(int i=0;i<5;i++)check(fixed.shop[i]<0,"fixed-team shop populated");Run fixedLoad=RunStorage.decode(RunStorage.encode(fixed));check(fixedLoad!=null&&fixedLoad.boardCount()==9,"fixed-team save lost roster");
  }
  Run tower=new Run(70,Run.MODE_TOWER);starter(tower);tower.board[8]=tower.bench[0];tower.bench[0]=-1;tower.round=5;tower.genEnemy();tower.applyResult(result(tower,true));check(tower.towerPending,"tower buff not offered");check(tower.chooseTowerBuff(0)&&tower.modeHpPct==15&&!tower.chooseTowerBuff(1),"tower buff not exactly once");tower.towerPending=true;tower.chooseTowerBuff(1);tower.towerPending=true;tower.chooseTowerBuff(2);tower.nextRound();Run loaded=RunStorage.decode(RunStorage.encode(tower));check(loaded!=null&&loaded.modeHpPct==15&&loaded.modeAtkPct==10&&loaded.modeArmor==3,"tower buffs not saved");
  for(int mode=Run.MODE_TOWER;mode<=Run.MODE_BOSS_RUSH;mode++){Run r=new Run(80+mode,mode);if(r.usesDraft())starter(r);for(int round=1;round<=r.maxRound();round++){r.round=round;r.genEnemy();int enemies=0;for(int i=0;i<Run.BOARD;i++){check(r.enemy[i]<Data.N,"invalid enemy species");if(r.enemy[i]>=0)enemies++;}check(enemies>0,"empty enemies");if(mode==Run.MODE_BOSS_RUSH)check(r.isBoss()&&StageRoad.boss(round,mode)&&enemies>=2,"boss guards missing");}check(RunStorage.decode(RunStorage.encode(r))!=null,"new mode not resumable");r.applyResult(result(r,true));check(r.over&&r.victory,"new mode never wins");}
  Run boss=new Run(99,Run.MODE_BOSS_RUSH);starter(boss);boss.hp=60;boss.applyResult(result(boss,true));check(boss.hp==70,"boss rest heal missing");boss.applyResult(result(boss,false));check(boss.hp<70&&!boss.over,"boss loss health rule");
  System.out.println("ExtraChessTest OK: four modes, 30 seeded fixed teams/monotype pools, shop restrictions, tower buffs/save, every floor/boss and HP rules");
 }
}

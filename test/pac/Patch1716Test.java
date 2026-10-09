package pac;

/** Regression coverage for 1.7.16; no device UI/FPS claims. */
public final class Patch1716Test {
 private static void check(boolean b,String message){if(!b)throw new RuntimeException(message);}
 public static void run(){
  int[][] sizes={{176,208},{240,320},{320,240},{360,640},{640,360}};
  for(int n=0;n<sizes.length;n++)for(int fh=10;fh<=16;fh+=2){int w=sizes[n][0],h=sizes[n][1],top=ExtraChessRules.menuTop(fh),gap=ExtraChessRules.menuGap(h,fh);for(int row=0;row<4;row++){int y=top+row*gap;check(ExtraChessRules.menuHit(w/2,y-3,w,h,fh,false)==row&&ExtraChessRules.menuHit(w/2,y+fh+2,w,h,fh,false)==row,"challenge row hit mismatch");}for(int row=0;row<2;row++)check(ExtraChessRules.menuHit(w/2,h/2+row*(fh+10)-3,w,h,fh,true)==row,"replace-save row hit mismatch");check(ExtraChessRules.menuDescriptionY(h,fh)>=top+3*gap+fh+6,"mode description overlaps rows");check(ExtraChessRules.menuHit(-1,top,w,h,fh,false)==-1,"outside tap selected mode");}
  Run tower=new Run(17160,Run.MODE_TOWER);tower.draftStage=2;tower.round=5;tower.towerPending=true;Run resumed=RunStorage.decode(RunStorage.encode(tower));check(resumed!=null&&resumed.lastWon&&resumed.towerPending,"pending tower save lost victory");resumed.nextRound();check(resumed.round==5,"pending buff bypassed");check(resumed.chooseTowerBuff(0),"resumed buff failed");resumed.nextRound();check(resumed.round==6&&resumed.modeHpPct==15,"resumed floor repeated / buff lost");
  Run corrupt=new Run(17161,Run.MODE_NORMAL);corrupt.board[0]=Data.N;check(RunStorage.decode(RunStorage.encode(corrupt))==null,"invalid species accepted");corrupt.board[0]=-1;corrupt.inventory[0]=-1;check(RunStorage.decode(RunStorage.encode(corrupt))==null,"negative bag accepted");
  for(int sp=0;sp<Data.N;sp++)if(SpecialFormData.zygarde(sp)){SurvivalRun r=new SurvivalRun(sp,17162);r.sp=sp;r.level=15;r.form=SpecialFormData.ZYGARDE_100;r.pendingLevels=1;r.choices[0]=SurvivalRun.MEGA;check(r.canMega()&&r.choose(0)&&r.mega&&r.form==0&&r.transformFx>0,"arena Zygarde 100 Mega blocked");break;}
  System.out.println("Patch1716Test OK: menu hit/draw geometry, pending tower restore, invalid saves and arena Zygarde Mega");
 }
}

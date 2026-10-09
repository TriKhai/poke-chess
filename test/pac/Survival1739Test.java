package pac;
/** Shop/economy regression checks. Desktop simulation, not emulator FPS. */
public final class Survival1739Test {
 private static void check(boolean ok,String why){if(!ok)throw new RuntimeException(why);}
 private static void inventory(SurvivalRun r){for(int part=0;part<SurvivalShop.PARTS;part++){int worn=0;for(int s=0;s<6;s++)if(r.shop.slots[s]==SurvivalShop.GEARS+part)worn++;check(r.shop.parts[part]>=worn&&r.shop.parts[part]>=0,"component count below worn count");}for(int g=0;g<SurvivalShop.GEARS;g++)check(r.shop.bag[g]>=0,"negative gear bag");check(r.gold>=0,"negative gold");}
 public static void run(){
  for(int gear=0;gear<SurvivalShop.GEARS;gear++)for(int free=0;free<=6;free++){
   SurvivalRun r=new SurvivalRun(0,1739);r.gold=10000;
   for(int s=0;s<6-free;s++){r.shop.parts[5]++;r.shop.slots[s]=SurvivalShop.GEARS+5;}
   r.shopRebuild();int before=r.gold;
   check(r.shop.buyAndEquip(r,SurvivalShop.GEARS+SurvivalShop.A[gear]),"first staged component");
   check(r.shop.buyAndEquip(r,SurvivalShop.GEARS+SurvivalShop.B[gear]),"second staged component");
   check(r.shop.buyAndEquip(r,gear)&&before-r.gold==SurvivalShop.gearPrice(gear),"staged total differs from direct price");
   inventory(r);check(r.shop.has(gear)||r.shop.bag[gear]==1,"purchase lost with full slots");
   check(r.shop.sell(r,gear,-1),"catalogue must sell owned or worn gear");inventory(r);
   check(r.gold==before-SurvivalShop.gearPrice(gear)+SurvivalShop.gearPrice(gear)/2,"resale payout mismatch");
   check(!r.shop.sell(r,gear,-1),"sold gear twice");
  }
  SurvivalRun r=new SurvivalRun(0,1740);r.gold=15;r.shop.plan(0);
  check(r.shop.quickPurchase(r)&&r.shop.parts[0]==1&&r.gold==0,"first quick component");
  r.gold=15;check(r.shop.quickPurchase(r)&&r.shop.parts[0]==2,"duplicate recipe second component");
  r.gold=15;check(!r.shop.quickBuyAvailable(r),"quick buy extra ingredient");
  r.gold=SurvivalShop.upgradeFee(0);check(r.shop.quickPurchase(r)&&r.shop.has(0)&&r.shop.plannedGear==-1,"quick final upgrade");
  r=new SurvivalRun(0,1741);r.gold=10000;for(int s=0;s<6;s++){r.shop.parts[0]++;r.shop.slots[s]=SurvivalShop.GEARS;}
  check(r.shop.buyAndEquip(r,1)&&r.shop.bag[1]==1,"full slots must keep finished item");
  check(r.shop.sell(r,SurvivalShop.GEARS,0)&&r.shop.slots[0]==1&&r.shop.bag[1]==0,"sale must fill vacant slot from bag");inventory(r);
  r.shop.plan(2);r.shop.bag[2]=1;check(!r.shop.quickBuyAvailable(r),"quick offer duplicates stored finished item");
  r=new SurvivalRun(0,1742);int baseAtk=r.atk;r.gold=15;r.shop.buyAndEquip(r,SurvivalShop.GEARS);r.gold=r.shop.remainingPrice(0)-1;int atk=r.atk,gold=r.gold;check(!r.shop.purchaseUpgrade(r,0)&&r.atk==atk&&r.gold==gold&&r.shop.parts[0]==1,"failed upgrade changed worn stats or inventory");r.gold++;check(r.shop.purchaseUpgrade(r,0)&&r.atk==baseAtk&&r.shop.parts[0]==0,"consumed worn component stats lingered before auto-equip");
  for(int w=1;w<=1000;w++){check(SurvivalBalance.killGold(w,false)>=1&&SurvivalBalance.killGold(w,false)<=5,"normal gold unbounded");check(SurvivalBalance.killGold(w,true)>=SurvivalBalance.killGold(w,false)&&SurvivalBalance.killGold(w,true)<=100,"boss gold unbounded");check(SurvivalBalance.enemyHp(0,w,0)>=SurvivalBalance.enemyHp(0,w-1,0),"enemy health curve");}
  check(SurvivalShop.gearPrice(16)>SurvivalShop.gearPrice(4)&&SurvivalShop.gearPrice(14)>SurvivalShop.gearPrice(0),"utility cheaper than resurrection/guard");
  System.out.println("Survival1739Test OK: 126 saturated-slot recipes, quick-buy duplicates, resale conservation, bag refill and bounded economy");
 }
}

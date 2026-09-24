package pac;

/** Desktop-only smoke tests. Not packaged in the MIDlet JAR. */
public final class CombatSmokeTest {
    private static void check(boolean ok, String message) {
        if (!ok) throw new RuntimeException(message);
    }

    public static void main(String[] args) {
        int[][] screens={{128,160},{176,208},{176,220},{240,320},{320,240}};
        for(int s=0;s<screens.length;s++){
            int w=screens[s][0],h=screens[s][1],m=UiLayout.margin(w,h),cw=UiLayout.contentWidth(w,h);
            check(m>=0&&cw>0&&m*2+cw<=w,"responsive content outside screen "+w+"x"+h);
            check(UiLayout.popupWidth(w,174)<=w&&UiLayout.popupHeight(h,220)<=h,"responsive popup overflow "+w+"x"+h);
            int rows=UiLayout.visibleRows(h,20,20,24),first=UiLayout.firstVisible(8,9,rows);
            check(rows>=1&&first>=0&&first+rows>=9,"responsive scroll window mismatch "+w+"x"+h);
        }
        check(UiLayout.profile(128,160)==UiLayout.COMPACT,"128x160 must be compact");
        check(UiLayout.profile(176,208)==UiLayout.STANDARD,"176x208 must be standard");
        check(UiLayout.profile(240,320)==UiLayout.LARGE&&UiLayout.profile(320,240)==UiLayout.LARGE,"large/landscape profile mismatch");
        check(MapData.COUNT==143,"original DungeonPMDO map catalog mismatch");
        Rng exploreRng=new Rng(137);int gachaPick=ExploreRules.pick(exploreRng,0,1);
        check(gachaPick>=0&&Data.isBase(gachaPick)&&ExploreRules.gen(gachaPick)==1,"Gen-filtered gacha mismatch");
        check(GachaRules.groupForDay(java.util.Calendar.MONDAY)==0&&GachaRules.groupForDay(java.util.Calendar.THURSDAY)==1&&GachaRules.groupForDay(java.util.Calendar.SATURDAY)==2&&GachaRules.groupForDay(java.util.Calendar.SUNDAY)==3,"daily Gacha schedule mismatch");
        check(GachaRules.allowsGen(0,3)&&!GachaRules.allowsGen(0,4)&&GachaRules.allowsGen(1,5)&&GachaRules.allowsGen(2,9)&&GachaRules.allowsGen(3,1),"daily generation pool mismatch");
        int pityDex=GachaRules.pick(new Rng(138),3,true);check(GachaRules.legendary(pityDex)&&!Save.ownsDex(pityDex),"Gacha pity must select a new Legendary");
        int[] featured=GachaRules.randomFeaturedLegendaries(3,3,new Rng(139));check(featured.length==3&&GachaRules.legendary(featured[0])&&GachaRules.legendary(featured[1])&&GachaRules.legendary(featured[2]),"random featured Legendary list mismatch");
        check(featured[0]!=featured[1]&&featured[0]!=featured[2]&&featured[1]!=featured[2],"featured Legendaries must be unique");
        int[] dailyA=GachaRules.featuredForDay(1,5,2026270),dailyB=GachaRules.featuredForDay(1,5,2026270),dailyC=GachaRules.featuredForDay(1,5,2026271);
        for(int i=0;i<5;i++)check(dailyA[i]==dailyB[i],"daily featured list changed within one day");
        boolean dayChanged=false;for(int i=0;i<5;i++)if(dailyA[i]!=dailyC[i])dayChanged=true;check(dayChanged,"daily featured list did not reset next day");
        check(Save.unlockDex(387)&&Save.ownsDex(387)&&!Save.unlockDex(387),"collection-only ownership mismatch");
        Save.dexUnlocked=new boolean[CollectionDex.COUNT];
        int oldCamp0=Save.camp[0],oldCamp1=Save.camp[1],oldCamp2=Save.camp[2];long oldCampStart=Save.campStart;
        Save.camp[0]=0;Save.camp[1]=-1;Save.camp[2]=-1;Save.campStart=1000;
        check(ExploreRules.campReward(1000+7200)==ExploreRules.hourlyBalls(0)*2,"offline camp reward mismatch");
        check(ExploreRules.campReward(1000+30L*3600)==ExploreRules.hourlyBalls(0)*24,"camp reward cap mismatch");
        int oldBalls=Save.balls;int claimed=ExploreRules.claimCamp(1000+7200);
        check(claimed==ExploreRules.hourlyBalls(0)*2&&Save.balls==oldBalls+claimed&&Save.campStart==8200,"camp claim transaction mismatch");
        Save.balls=oldBalls;
        Save.camp[0]=oldCamp0;Save.camp[1]=oldCamp1;Save.camp[2]=oldCamp2;Save.campStart=oldCampStart;
        check(CombatStatus.COUNT==25,"documented status count mismatch");
        CombatStatus parity=new CombatStatus();parity.apply(CombatStatus.BURN,20);parity.apply(CombatStatus.SAFEGUARD,15);
        check(parity.burn==0&&parity.safeguard==15,"Safeguard must cleanse negatives");
        check(!parity.apply(CombatStatus.POISON,20)&&parity.poison==0,"Safeguard must block negatives");
        parity.apply(CombatStatus.RAGE,10);check(parity.effectiveSpeed(60)==90,"Rage speed mismatch");
        parity.clearPositive();parity.apply(CombatStatus.ELECTRIC_FIELD,10);check(parity.effectiveSpeed(50)==60,"Electric Field speed mismatch");
        check(parity.positive(CombatStatus.RESURRECTION)&&!parity.positive(CombatStatus.POSSESSED),"status polarity mismatch");
        int[] cadence={100,60,50,40};
        for(int c=0;c<cadence.length;c++){
            FixedStepClock clock=new FixedStepClock(100);int elapsed=0,steps=0;
            while(elapsed<6000){int dt=Math.min(cadence[c],6000-elapsed);clock.add(dt,1);elapsed+=dt;while(clock.ready()){clock.consume();steps++;}}
            check(steps==60&&clock.remainder()==0,"render cadence changed simulation at "+cadence[c]+"ms");
        }
        check(FixedStepClock.smooth256(0)==0&&FixedStepClock.smooth256(128)==128&&FixedStepClock.smooth256(256)==256,"smooth interpolation endpoints mismatch");
        check(Save.targetFps()==16,"default target FPS mismatch");
        check(Data.N == 386, "roster must contain Gen 1-3");
        check(CollectionDex.COUNT==558,"collection-only Gen 4-9 roster mismatch");
        check(CollectionDex.countGen(4)==107&&CollectionDex.countGen(5)==135&&CollectionDex.countGen(6)==68,
              "collection Gen 4-6 classification mismatch");
        check(CollectionDex.countGen(7)==80&&CollectionDex.countGen(8)==83&&CollectionDex.countGen(9)==85,
              "collection Gen 7-9 classification mismatch");
        check(Data.speed[0] == 51, "Bulbasaur speed must come from source CSV");
        check(Data.speDef[0] == 4, "Bulbasaur special defense must come from source CSV");
        check("Magical Leaf".equals(Data.skillName[0]), "original ability name missing");
        check(CombatRules.physicalDamage(100, 20) == 50, "armor formula mismatch");
        check(CombatRules.specialDamage(100, 5) == 80, "special defense formula mismatch");
        int[][] expectedThresholds={
          {3,5,7,9},{2,4,6,8},{3,6,9,0},{3,5,7,9},{3,5,7,0},{2,4,6,0},{3,5,7,0},{2,4,6,8},
          {2,4,6,8},{3,5,7,0},{2,4,6,8},{2,4,6,8},{3,5,7,0},{2,4,6,8},{2,4,6,8},{3,5,7,0},
          {2,4,6,8},{2,4,6,8},{3,5,7,0},{2,4,6,8},{2,4,6,0},{3,5,7,0},{3,6,9,0},{3,4,5,6},
          {2,4,6,0},{3,4,5,0},{2,4,6,0},{2,3,4,5},{2,4,6,8},{2,4,6,0},{2,4,6,9}
        };
        check(expectedThresholds.length==Data.NT,"all 31 synergies need thresholds");
        for(int type=0;type<Data.NT;type++)for(int tier=0;tier<4;tier++)
            check(SynergyEffects.threshold(type,tier)==expectedThresholds[type][tier],"threshold mismatch type "+type+" tier "+tier);
        check(SynergyEffects.tier(Data.T_NORMAL,2)==0&&SynergyEffects.tier(Data.T_NORMAL,3)==1,"Normal threshold mismatch");
        check(SynergyEffects.tier(Data.T_LIGHT,5)==4,"Light fourth tier mismatch");
        check(SynergyEffects.tier(Data.T_WATER,9)==3,"Water third tier mismatch");
        int evolvedBulbasaur=Data.evo[0];
        int[] uniquePlayer=new int[24],uniqueEnemy=new int[24];
        for(int i=0;i<24;i++){uniquePlayer[i]=-1;uniqueEnemy[i]=-1;}
        uniquePlayer[0]=0;uniquePlayer[1]=0;uniquePlayer[2]=evolvedBulbasaur;uniqueEnemy[0]=3;
        Battle uniqueFamilyBattle=new Battle(uniquePlayer,uniqueEnemy,100,new Rng(71));
        int[] uniqueCounts=new int[Data.NT];uniqueFamilyBattle.countSyn(0,uniqueCounts);
        check(uniqueCounts[Data.T_GRASS]==1,"copies/evolution stages must count as one synergy family");
        Unit statusUnit=new Unit();statusUnit.maxHp=statusUnit.hp=160;statusUnit.alive=true;
        statusUnit.status.curse=11;statusUnit.status.fatigue=5;statusUnit.status.flinch=3;statusUnit.status.locked=4;
        check(statusUnit.status.effectiveSpeed(60)==40,"Fatigue speed reduction missing");
        check(statusUnit.status.blocksAction()&&statusUnit.status.blocksMove(),"Flinch/Locked behavior missing");
        for(int i=0;i<10;i++)statusUnit.status.update(uniqueFamilyBattle,statusUnit);
        check(statusUnit.hp==150,"Curse periodic damage mismatch");
        check(statusUnit.status.visualAt(0)!=CombatStatus.NONE,"new status visuals missing");
        statusUnit.status.clearNegative();check(!statusUnit.status.hasNegative(),"status cleanse incomplete");
        Unit agilityUser=new Unit();agilityUser.sp=18;agilityUser.speed=Data.speed[18];agilityUser.alive=true;
        AbilityBehavior.apply(uniqueFamilyBattle,agilityUser,uniqueFamilyBattle.units[uniqueFamilyBattle.n-1]);
        check(agilityUser.speed==Data.speed[18]+20,"Agility behavior missing");
        check(AbilityBehavior.description(18).length()>Lang.abilityDesc(Data.abil[18]).length(),"specific ability description missing");
        int heracross=-1,zangoose=-1,vigoroth=-1,sudowoodo=-1;
        for(int i=0;i<Data.N;i++){if("Heracross".equals(Data.name[i]))heracross=i;if("Zangoose".equals(Data.name[i]))zangoose=i;if("Vigoroth".equals(Data.name[i]))vigoroth=i;if("Sudowoodo".equals(Data.name[i]))sudowoodo=i;}
        check(heracross>=0&&zangoose>=0&&vigoroth>=0&&sudowoodo>=0,"passive fixtures missing");
        Unit passiveUnit=new Unit();passiveUnit.sp=heracross;passiveUnit.atk=20;passiveUnit.status.burn=20;PokemonPassive.onTick(uniqueFamilyBattle,passiveUnit);
        check(passiveUnit.atk==25&&passiveUnit.passiveAttackBonus==5,"Guts passive mismatch");
        passiveUnit.status.burn=0;PokemonPassive.onTick(uniqueFamilyBattle,passiveUnit);check(passiveUnit.atk==20,"Guts removal mismatch");
        passiveUnit.sp=vigoroth;check(PokemonPassive.immune(passiveUnit,CombatStatus.SLEEP),"Vigoroth sleep immunity missing");
        passiveUnit.sp=sudowoodo;PokemonPassive.onStart(passiveUnit);check(PokemonPassive.blocksMove(passiveUnit),"Sudowoodo tree passive missing");
        check(PokemonPassive.description(heracross).length()>20&&PokemonPassive.description(zangoose).length()>20,"passive descriptions missing");
        int lick=-1;for(int i=0;i<Data.N;i++)if("Lick".equals(Data.skillName[i])){lick=i;break;}
        check(lick>=0,"Lick fixture missing");Unit lickUser=new Unit();lickUser.sp=lick;Unit lickTarget=new Unit();lickTarget.alive=true;
        SkillEffects.apply(lickUser,lickTarget);check(lickTarget.status.confusion>0&&lickTarget.status.paralysis>0,"multi-status move behavior missing");
        for(int language=Lang.VI;language<=Lang.EN;language++){
            Save.language=language;
            for(int type=0;type<Data.NT;type++)
                check(Lang.synergyLongDesc(type).length()>30,"missing detailed synergy description "+type+" language "+language);
        }
        Save.language=Lang.VI;

        Save.reset();
        int oldPerformance=Save.performance;
        Save.performance=1;check(Save.frameDelay()==100,"10 FPS performance mode mismatch");
        check(Save.targetFps()==10,"10 FPS target label mismatch");
        Save.performance=0;check(Save.frameDelay()==60,"legacy 16 FPS performance mode mismatch");
        check(Save.targetFps()==16,"16 FPS target label mismatch");
        Save.performance=2;check(Save.frameDelay()==50,"20 FPS performance mode mismatch");
        check(Save.targetFps()==20,"20 FPS target label mismatch");
        Save.performance=3;check(Save.frameDelay()==40,"25 FPS performance mode mismatch");
        check(Save.targetFps()==25,"25 FPS target label mismatch");
        Save.performance=oldPerformance;
        Save.language=Lang.EN;Save.performance=3;Save.playPath=1;Save.cheatMode=true;Save.balls=999;
        Save.resetProgress();check(Save.language==Lang.EN&&Save.performance==3,"start-over must keep device settings");
        check(Save.playPath==-1&&!Save.cheatMode&&Save.balls==15,"start-over did not erase progression");
        check("Nameless".equals(Save.displayName()),"default profile name mismatch");
        check(!Save.chooseProfileAvatar(25),"locked Pokemon must not become profile avatar");
        check(Save.chooseProfileAvatar(1)&&Save.profileAvatarDex==1,"owned profile avatar selection failed");
        check(Save.setProfileNameOnce("Kdic")&&"Kdic".equals(Save.profileName),"first profile name confirmation failed");
        check(!Save.setProfileNameOnce("Renamed")&&"Kdic".equals(Save.profileName),"profile name must only be set once");
        Save.camp[0]=0;
        check(Save.inCamp(0)&&Save.inCamp(Data.evo[0]),"camp lock must cover the full evolution family");
        check(!Save.usable(0)&&!OwnedPokemon.eligible(0,-1),"camped Pokemon must be unavailable outside Camp");
        check(!Save.chooseProfileAvatar(1),"camped family must not become the profile avatar");
        Run campLocked=new Run(554,Run.MODE_NORMAL);
        check(campLocked.pool[0]==0,"camped family leaked into a new Battle pool");
        Save.camp[0]=-1;
        check(Save.usable(0)&&OwnedPokemon.eligible(0,-1),"removing a Camp slot must restore availability");
        check(ExploreRules.farmReward(4)==0&&ExploreRules.farmReward(5)==1&&ExploreRules.farmReward(12)==2,"Farm 5-KO reward/remainder rule mismatch");
        check(!ExploreRules.farmLegendaryRound(9)&&ExploreRules.farmLegendaryRound(10)&&ExploreRules.farmLegendaryRound(20),"Farm Legendary cadence mismatch");
        int normalFarmEnemy=ExploreRules.pickAnyBase(new Rng(555));
        check(normalFarmEnemy>=0&&Data.isBase(normalFarmEnemy)&&Data.category[normalFarmEnemy]!=6,"Farm normal enemy picker mismatch");
        Save.modeCleared=0;
        int normalFirst=ProgressionRules.finishReward(Run.MODE_NORMAL,20,true);
        check(normalFirst==33,"Normal first-clear reward mismatch");
        ProgressionRules.recordClear(Run.MODE_NORMAL);
        check(ProgressionRules.cleared(Run.MODE_NORMAL)&&ProgressionRules.finishReward(Run.MODE_NORMAL,20,true)==23,"repeat clear reward mismatch");
        check(ProgressionRules.finishReward(Run.MODE_THIRTY,30,true)==61,"30-round first-clear reward mismatch");
        check(ProgressionRules.objective(Run.MODE_GEN1).length()>30,"mode objective help missing");
        Save.language=Lang.VI;Save.performance=oldPerformance;
        Run sandbox = new Run(123, true);
        sandbox.shop[0] = 0;
        check(sandbox.buy(0), "unlimited mode must allow buying");
        check(sandbox.gold == 9999, "unlimited buy changed gold");
        check(sandbox.reroll() && sandbox.gold == 9999, "unlimited reroll changed gold");
        check(sandbox.buyXp() && sandbox.gold == 9999, "unlimited XP changed gold");

        int spoon=ItemData.indexOf("TWISTED_SPOON"),charcoal=ItemData.indexOf("CHARCOAL");
        int book=ItemData.indexOf("POKEMONOMICON");
        check(ItemData.crafted(spoon,charcoal)==book,"original ItemRecipe mismatch");
        sandbox.giveItem(spoon);sandbox.giveItem(charcoal);
        check(sandbox.craftItems(spoon,charcoal),"manual component craft failed");
        check(sandbox.itemCount(book)==1,"crafted item missing from inventory");
        check(sandbox.equipDirect(Run.BOARD,book),"crafted item equip failed");
        check(sandbox.itemAt(Run.BOARD,0)==book,"crafted item not held");
        check(sandbox.move(Run.BOARD,0),"equipped Pokemon move failed");
        check(sandbox.itemAt(0,0)==book,"item did not follow moved Pokemon");
        Battle itemBattle=sandbox.makeBattle();
        Unit equipped=null;for(int i=0;i<itemBattle.n;i++)if(itemBattle.units[i].side==0){equipped=itemBattle.units[i];break;}
        check(equipped!=null,"equipped unit missing from battle");
        check(equipped.atk==Data.atk[0]+3,"ItemStats ATK not applied");
        check(equipped.skillBonus==30,"ItemStats AP not applied");
        check(ItemData.desc(book).indexOf("Nội tại")>=0,"ported item passive missing from Collection description");

        int king=ItemData.indexOf("KINGS_ROCK"),upgrade=ItemData.indexOf("UPGRADE"),revive=ItemData.indexOf("MAX_REVIVE");
        int[] triggerEquip=new int[24*3];for(int i=0;i<triggerEquip.length;i++)triggerEquip[i]=-1;
        triggerEquip[0]=king;triggerEquip[1]=upgrade;triggerEquip[2]=revive;
        Battle triggerBattle=new Battle(uniquePlayer,uniqueEnemy,100,new Rng(72),triggerEquip);
        Unit triggerUnit=null,triggerEnemy=null;for(int i=0;i<triggerBattle.n;i++){if(triggerBattle.units[i].side==0&&triggerUnit==null)triggerUnit=triggerBattle.units[i];if(triggerBattle.units[i].side==1)triggerEnemy=triggerBattle.units[i];}
        check(triggerUnit!=null&&triggerEnemy!=null,"item trigger fixture missing units");
        check(triggerUnit.shield>=triggerUnit.maxHp/5,"King's Rock start trigger missing");
        int speedBefore=triggerUnit.speed;ItemEffects.onBasicAttack(triggerBattle,triggerUnit,triggerEnemy,10,false,false);
        check(triggerUnit.speed==speedBefore+5,"Upgrade attack trigger missing");
        triggerUnit.hp=0;check(ItemEffects.onDeath(triggerBattle,triggerUnit,triggerEnemy),"Max Revive death trigger missing");
        check(triggerUnit.hp==triggerUnit.maxHp/2&&triggerUnit.itemReviveUsed,"Max Revive state mismatch");
        triggerUnit.items[0]=ItemData.indexOf("AMULET_COIN");ItemEffects.onKill(triggerBattle,triggerUnit,triggerEnemy);
        check(triggerBattle.itemGold==1,"Amulet Coin kill reward missing");
        int[] onePlayer=new int[24],oneEnemy=new int[24],oneEquip=new int[24*3];
        for(int recipe=0;recipe<ItemData.recipeCount();recipe++){
            for(int i=0;i<24;i++){onePlayer[i]=-1;oneEnemy[i]=-1;}for(int i=0;i<oneEquip.length;i++)oneEquip[i]=-1;
            onePlayer[0]=0;oneEnemy[0]=3;oneEquip[0]=ItemData.recipeOutputAt(recipe);
            Battle itemStress=new Battle(onePlayer,oneEnemy,100,new Rng(900+recipe),oneEquip);
            for(int i=0;i<250&&!itemStress.over;i++)itemStress.step();
            check(itemStress.tick>0,"crafted item battle did not run: "+ItemData.ID[oneEquip[0]]);
        }

        Run direct = new Run(321, Run.MODE_NORMAL);
        direct.set(Run.BOARD, 0);
        direct.giveItem(spoon); direct.giveItem(charcoal);
        check(direct.equipCombined(Run.BOARD,spoon,charcoal),"craft-and-equip failed");
        check(direct.itemAt(Run.BOARD,0)==book,"craft-and-equip result mismatch");
        int beforeSell=direct.itemCount(book);
        check(direct.sell(Run.BOARD),"selling equipped Pokemon failed");
        check(direct.itemCount(book)==beforeSell+1,"sold Pokemon did not return item");

        Run merge = new Run(456, Run.MODE_NORMAL);
        merge.set(0,0); merge.set(1,0); merge.set(Run.BOARD,0);
        merge.giveItem(spoon); merge.giveItem(charcoal); merge.giveItem(book);
        check(merge.equipDirect(0,spoon),"merge item 1 equip failed");
        check(merge.equipDirect(1,charcoal),"merge item 2 equip failed");
        check(merge.equipDirect(Run.BOARD,book),"merge item 3 equip failed");
        merge.mergeAll();
        check(merge.count(Data.evo[0])==1,"three copies did not evolve");
        int evolved=-1;for(int p=0;p<Run.BOARD+Run.BENCH;p++)if(merge.get(p)==Data.evo[0]){evolved=p;break;}
        check(evolved>=0,"evolved Pokemon missing");
        check(merge.itemAt(evolved,0)>=0&&merge.itemAt(evolved,1)>=0&&merge.itemAt(evolved,2)>=0,
              "merge did not preserve three items");

        Run locked = new Run(789, Run.MODE_NORMAL);
        int[] heldShop=new int[5];for(int i=0;i<5;i++)heldShop[i]=locked.shop[i];
        locked.toggleShopLock(); locked.nextRound();
        for(int i=0;i<5;i++)check(locked.shop[i]==heldShop[i],"shop lock did not preserve slot "+i);
        check(!locked.shopLocked,"shop lock must expire after one round");
        check(new Run(1,Run.MODE_NORMAL).maxRound()==20,"normal mode round limit mismatch");
        check(new Run(1,Run.MODE_THIRTY).maxRound()==30,"thirty-round mode limit mismatch");
        check(new Run(1,Run.MODE_GEN1).maxRound()==30,"Gen 1 mode round limit mismatch");
        check(EconomyRules.interest(59)==5,"interest cap mismatch");
        check(EconomyRules.streakBonus(-6)==3&&EconomyRules.streakBonus(1)==0,"streak economy mismatch");
        check(EconomyRules.income(20,4,true,2)==12,"income breakdown mismatch");
        check(EconomyRules.playerDamage(8,5)==11,"player damage formula mismatch");
        Run pve=new Run(990,Run.MODE_NORMAL);pve.round=8;pve.genEnemy();
        int gyarados=-1;for(int i=0;i<Data.N;i++)if("Gyarados".equals(Data.name[i])){gyarados=i;break;}
        check(pve.enemy[2*8+4]==gyarados,"source Gyarados formation mismatch");
        check(pve.enemyEquip[(2*8+4)*3]==ItemData.indexOf("KINGS_ROCK"),"boss held item missing");
        Battle pveBattle=pve.makeBattle();Unit bossUnit=null;for(int i=0;i<pveBattle.n;i++)if(pveBattle.units[i].side==1)bossUnit=pveBattle.units[i];
        check(bossUnit!=null&&bossUnit.shield>=bossUnit.maxHp/5,"enemy held item passive missing");
        pve.round=20;pve.genEnemy();int legends=0;for(int i=0;i<Run.BOARD;i++)if(pve.enemy[i]>=0)legends++;
        check(legends==3&&pve.enemy[2*8+2]>=0&&pve.enemy[2*8+4]>=0&&pve.enemy[2*8+6]>=0,"legendary birds formation mismatch");
        Run snapshot=new Run(1201,Run.MODE_THIRTY);snapshot.round=7;snapshot.hp=73;snapshot.gold=42;snapshot.level=5;snapshot.xp=9;snapshot.streak=-3;
        snapshot.board[4]=25;snapshot.bench[2]=7;snapshot.shop[1]=18;snapshot.equip[4*3]=ItemData.indexOf("SHELL_BELL");snapshot.resultPath[0]=1;snapshot.resultPath[1]=0;snapshot.resultCount=2;
        int expectedRng=snapshot.rng.state();byte[] snapshotBytes=RunStorage.encode(snapshot);Run restored=RunStorage.decode(snapshotBytes);
        check(restored!=null&&restored.mode==Run.MODE_THIRTY&&restored.round==7&&restored.hp==73&&restored.gold==42,"run snapshot scalar mismatch");
        check(restored.board[4]==25&&restored.bench[2]==7&&restored.shop[1]==18&&restored.equip[12]==ItemData.indexOf("SHELL_BELL"),"run snapshot arrays mismatch");
        check(restored.resultCount==2&&restored.resultPath[1]==0&&restored.rng.state()==expectedRng,"run snapshot graph/RNG mismatch");RunStorage.clear();
        snapshot.over=true;snapshot.victory=false;HistoryStore.add(snapshot);check(HistoryStore.count>0&&HistoryStore.round[0]==7,"history entry missing");
        check(HistoryStore.team[0][0]==25&&HistoryStore.items[0][0]==ItemData.indexOf("SHELL_BELL"),"history final team/items mismatch");
        HistoryStore.clear();check(HistoryStore.count==0,"history clear failed");

        int[] player = new int[24];
        int[] enemy = new int[24];
        for (int i = 0; i < 24; i++) { player[i] = -1; enemy[i] = -1; }
        player[0] = 0;
        enemy[0] = 3;
        Battle battle = new Battle(player, enemy, 100, new Rng(7));
        for (int i = 0; i < 800 && !battle.over; i++) battle.step();
        check(battle.over, "battle did not terminate");
        check(battle.tick <= Battle.HARD_LIMIT, "battle exceeded hard limit");

        for (int seed = 0; seed < 100; seed++) {
            for (int i = 0; i < 24; i++) { player[i] = -1; enemy[i] = -1; }
            for (int i = 0; i < 6; i++) {
                player[i] = (seed * 17 + i * 31) % Data.N;
                enemy[i] = (seed * 43 + i * 19 + 7) % Data.N;
            }
            Battle stress = new Battle(player, enemy, 85 + seed % 31, new Rng(seed + 99));
            for (int i = 0; i <= Battle.HARD_LIMIT && !stress.over; i++) stress.step();
            check(stress.over, "stress battle did not terminate at seed " + seed);
        }

        System.out.println("CombatSmokeTest OK: synergies, items, abilities/statuses, economy, PvE/bosses, save/resume/history, Camp/Farm rules, "+ItemData.recipeCount()+" item battles and 101 base battles");
    }
}

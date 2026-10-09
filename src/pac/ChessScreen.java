package pac;

import javax.microedition.lcdui.Graphics;

/** Mode 2: offline auto chess run against AI teams, using unlocked (collected) families only. */
public final class ChessScreen extends Screen {
    static final int PREP = 0, BATTLE = 1, RESULT = 2, OVER = 3;
    static final int TICK_MS = CombatRules.TICK_MS;
    static final String[] BTN = { "XP", "Roll", "More", "Syn", "GO" };

    private final Run run;
    private final boolean unlimitedGold;
    private final boolean labMode;
    private int state = PREP;

    // cursor: 0 board, 1 bench, 2 shop, 3 buttons, 4 item reserve, 5 battle team
    private int zone = 2, col = 0, row = 0;
    private int held = -1;
    private int evolutionChoiceSel=0;
    private int pendingPurchaseSlot=-1,purchaseForm=0;
    private int choiceSpecies(){return pendingPurchaseSlot>=0?run.shop[pendingPurchaseSlot]:run.get(run.pendingEvolutionPos);}
    private int choiceCount(){return pendingPurchaseSlot>=0?EvolutionVariantData.choiceCount(choiceSpecies()):EvolutionBranchData.count(choiceSpecies());}
    /** Animation frame captured once when a formation unit is picked up. */
    private int heldFrame = -1;

    private Battle bt;
    private final FixedStepClock simClock=new FixedStepClock(TICK_MS);
    private int endT = 0, speed = 1;
    private boolean endReady = false;
    private boolean battleEndVisualsCleared=false;
    private String toast = "";
    private int toastT = 0;
    private boolean showSyn = false;
    private int synCursor = 0;
    private boolean showMore = false;
    private int moreSel = 0;
    private boolean showPool=false,showStoneStats=false,showItemShop=false,itemShopTargeting=false,itemShopAction=false,itemShopDetail=false;
    private int itemShopSel=0,itemShopTab=0,itemShopTarget=0,itemShopActionSel=0,itemShopUseId=-1,poolCursor=0,poolScroll=0,stoneStatsCursor=0;
    private boolean showItems = false;
    private int itemSel = 0, itemTarget = -1;
    private boolean showConsumables=false,consumableTargeting=false;
    private int consumableSel=0,consumableTarget=0;
    private int dockItemCursor=0,dockItemScroll=0,dockConsumableCursor=0,dockConsumableScroll=0,dockTeamCursor=0,pendingConsumable=-1;
    private int dockMode=0,dockSynCursor=0,prepHoverType=-1;
    private int pendingItemA=-1,pendingItemB=-1,pendingMade=-1,dockDetailItem=-1;
    private boolean dockDetail=false;
    private boolean itemActionMenu=false;
    private int itemAction=0;
    private boolean petActionMenu=false,dockPetDetail=false;
    private int petAction=0,dockPetPos=-1,dockPetDetailScroll=0;
    private int petDetailTab=0,petEquipRow=0,petEquipCursor=0;
    private int petFormsBottom=0,fruitBagCursor=0;
    private final Unit previewStats=new Unit();
    private boolean showCraft=false;
    private int craftTab=0,craftFocus=0,craftItemCursor=0,craftItemScroll=0;
    private int craftA=-1,craftB=-1,craftRecipeCursor=0,craftRecipeScroll=0;
    private int craftFinishedCursor=0,craftButton=0;
    private boolean detailReturnCraft=false;
    private boolean quitArm = false;
    private boolean warned = false;
    private int reward = 0;
    private boolean finished = false;
    private int visualTime = 0;
    private int watch = 0;
    private boolean rosterDetail = false;
    private boolean refreshItemsAsk=false;
    private int refreshItemsChoice=0;
    /** Shared vertical choice card; first use is the five-round crafted-item reward. */
    private boolean rewardChoice=false;
    private int rewardChoiceSel=0;
    private int rewardChoiceScroll=0;
    private int choiceTouchX,choiceTouchY,choiceTouchW,choiceTouchH,choiceTouchTop,choiceTouchView;
    private final int[] choiceTouchRows={0,0,0};
    private final int[] rewardChoiceIds={-1,-1,-1};
    /** Wrapped choice text is immutable while a popup is open; cache it to avoid per-frame garbage. */
    private final int[] choiceRowCache={42,42,42};
    private final String[][] choiceLinesA=new String[3][],choiceLinesB=new String[3][];
    private int choiceLayoutW=-1,choiceLayoutKind=-1;
    private final int[] poolChoiceTypes={-1,-1,-1,-1,-1,-1,-1,-1,-1};
    private int rewardChoiceKind=0,postChoiceMask=0;
    private boolean typeChoiceAction=false,typeChoiceDetail=false;
    private int typeChoiceActionSel=0;
    private static final int CH_ITEM=1,CH_TYPE=2,CH_STARTER=3,CH_ADD=4,CH_UNIQUE=5,CH_LEGEND=6,CH_TOWER=7;
    /** 1: formation is sucked in, 2: allies deploy onto the battle board. */
    private int transitionPhase=0,transitionT=0;
    private static final int TRANSITION_MS=900;
    private int rosterSide = 0, rosterSel = 0, statMode = 0, battleSynCursor = 0;
    private boolean battleMore=false;private int battleMoreSel=0;private boolean showBoardHealth=true;
    /** True while the arrow-key cursor is on the combat-stat tabs. */
    private boolean statFocus = false;
    private final int[] spriteBounds = new int[4];
    /** Reused by paint/input paths to avoid garbage-collector spikes on CLDC phones. */
    private final int[] synCounts = new int[Data.NT];
    /** Short source-style blue/green burst when a purchase lands on the bench. */
    private int benchSpawnSlot=-1,benchSpawnT=0;
    private int evolutionFxPos=-1,evolutionFxTier=0,evolutionFxT=0;
    private int shinyFxPos=-1,shinyFxT=0,megaFxPos=-1,megaFxT=0;
    private int sellGoldX,sellGoldY,sellGoldValue,sellGoldT=0;
    /** HUD feedback for every gold/XP mutation; kept visual-only and out of RMS. */
    private int hudGoldValue=0,hudGoldT=0,hudXpValue=0,hudXpT=0,levelUpFxT=0;
    private int seenGold=0,seenXp=0,seenLevel=0;
    private boolean hudValuesReady=false;
    private int legendaryBuySlot=-1,legendaryBuyT=0;
    private final int[] buyBenchBefore=new int[Run.BENCH];
    /** Precomputed once: drawing evolution dots must never scan the full roster per pet/frame. */
    private static final int[] FAMILY_MAX_TIER=new int[Data.N];
    private static final boolean[] FAMILY_SPECIAL=new boolean[Data.N];
    private static final int[] FAMILY_TIER_SP=new int[Data.N*4];
    private static final int[][] FAMILY_MEMBERS=new int[Data.N][];
    static{
        int[] members=new int[Data.N];for(int i=0;i<Data.N;i++)members[Data.fam[i]]++;
        for(int i=0;i<Data.N;i++)FAMILY_MEMBERS[i]=new int[members[i]];
        for(int i=0;i<Data.N;i++)members[i]=0;
        for(int tier=1;tier<=3;tier++)for(int i=0;i<Data.N;i++)if(Data.tier[i]==tier){int f=Data.fam[i];FAMILY_MEMBERS[f][members[f]++]=i;}
        for(int i=0;i<FAMILY_TIER_SP.length;i++)FAMILY_TIER_SP[i]=-1;
        for(int i=0;i<Data.N;i++){
            int f=Data.fam[i],tier=Data.tier[i];
            if(f>=0&&f<FAMILY_MAX_TIER.length&&tier>FAMILY_MAX_TIER[f])FAMILY_MAX_TIER[f]=tier;
            if(f>=0&&f<Data.N&&(MegaData.available(i)||SpecialFormData.markerAvailable(i)))FAMILY_SPECIAL[f]=true;
            if(f>=0&&f<Data.N&&tier>=1&&tier<=3&&EvolutionBranchData.synergyBranch(i)==0)FAMILY_TIER_SP[f*4+tier]=i;
        }
    }

    // layout (recomputed every frame)
    private int cell, hudH, boardY, benchY, shopY, shopH, btnY, btnH, infoY, cw,boardX;private boolean prepLandscape;

    public ChessScreen(Game g) {
        this(g, false);
    }

    public ChessScreen(Game g, boolean unlimited) {
        this(g,unlimited?Run.MODE_UNLIMITED:Run.MODE_NORMAL);
    }

    public ChessScreen(Game g,int mode) {
        super(g);
        labMode=false;
        unlimitedGold = mode==Run.MODE_UNLIMITED;
        run = new Run((int) System.currentTimeMillis(),mode);
        ensureDraftChoice();
        RunStorage.save(run);
    }

    /** Isolated generation lab: never overwrites resume/history/progression. */
    public ChessScreen(Game g,int generation,int starter){super(g);labMode=true;run=new Run((int)System.currentTimeMillis(),Run.MODE_TEST,generation);unlimitedGold=true;run.level=9;run.xp=0;run.gold=9999;run.lockTestShop(starter);if(starter>=0){run.bench[0]=starter;if(SpecialFormData.zygarde(starter))run.specialForm[Run.BOARD]=SpecialFormData.ZYGARDE_10;}for(int i=0;i<ConsumableData.COUNT;i++)run.consumables[i]=3;}

    public ChessScreen(Game g,Run resumed){super(g);labMode=false;run=resumed;unlimitedGold=run.mode==Run.MODE_UNLIMITED;state=PREP;ensureDraftChoice();}
    /** Run snapshots are written only when a preparation round begins. */
    public void saveResume(){}
    private void saveRun(){if(!labMode)RunStorage.save(run);}

    private void say(String s) {
        toast = s;
        toastT = 1500;
    }

    // ---- update ----------------------------------------------------------

    public void update(int dt) {
        visualTime += dt;
        if(!hudValuesReady){seenGold=run.gold;seenXp=run.xp;seenLevel=run.level;hudValuesReady=true;}
        else{
            if(run.gold!=seenGold){hudGoldValue=run.gold-seenGold;hudGoldT=1200;seenGold=run.gold;}
            if(run.xp!=seenXp||run.level!=seenLevel){int gained=run.xp-seenXp;for(int lv=seenLevel;lv<run.level&&lv<Data.MAX_LEVEL;lv++)gained+=Data.XP_NEED[lv];if(gained>0){hudXpValue=gained;hudXpT=1200;}if(run.level>seenLevel)levelUpFxT=1100;seenXp=run.xp;seenLevel=run.level;}
        }
        if(benchSpawnT>0){benchSpawnT-=dt;if(benchSpawnT<=0){benchSpawnT=0;benchSpawnSlot=-1;}}
        if(evolutionFxT>0){evolutionFxT-=dt;if(evolutionFxT<=0){evolutionFxT=0;evolutionFxPos=-1;Art.clearEvolutionFx();}}
        if(shinyFxT>0){shinyFxT-=dt;if(shinyFxT<=0){shinyFxT=0;shinyFxPos=-1;}}
        if(sellGoldT>0){sellGoldT-=dt;if(sellGoldT<0)sellGoldT=0;}
        if(hudGoldT>0){hudGoldT-=dt;if(hudGoldT<0)hudGoldT=0;}
        if(hudXpT>0){hudXpT-=dt;if(hudXpT<0)hudXpT=0;}
        if(levelUpFxT>0){levelUpFxT-=dt;if(levelUpFxT<0)levelUpFxT=0;}
        if(megaFxT>0){megaFxT-=dt;if(megaFxT<0)megaFxT=0;}
        if(legendaryBuyT>0){legendaryBuyT-=dt;if(legendaryBuyT<=0){legendaryBuyT=0;legendaryBuySlot=-1;}}
        if (toastT > 0) toastT -= dt;
        if(transitionPhase>0){
            transitionT+=dt;
            if(transitionT>=TRANSITION_MS){
                if(transitionPhase==1){state=BATTLE;transitionPhase=2;transitionT=0;simClock.reset();}
                else{transitionPhase=0;transitionT=0;simClock.reset();}
            }
            return;
        }
        if (state == BATTLE && !rosterDetail) {
            if (!bt.over) {
                simClock.add(dt,speed);
                while (simClock.ready() && !bt.over) {
                    simClock.consume();
                    bt.step();
                }
                if(bt.over&&!battleEndVisualsCleared){
                    bt.clearTransientVisuals();battleEndVisualsCleared=true;
                }
            } else {
                endT += dt;
                if (endT >= 900) endReady=true;
            }
        }
    }

    private void endBattle() {
        run.applyResult(bt);
        if (run.over) finishRun();
        state = RESULT;
    }

    private void finishRun() {
        if (finished) return;
        finished = true;
        if(labMode){reward=0;return;}
        reward = ProgressionRules.finishReward(run.mode,run.round,run.victory);
        Save.balls += reward;
        Save.runs++;
        if (run.victory) {Save.wins++;ProgressionRules.recordClear(run.mode);}
        if (run.round > Save.best) Save.best = run.round;
        Save.save();
        HistoryStore.add(run);
        RunStorage.clear();
    }

    // ---- input -----------------------------------------------------------

    public void key(int k) {
        if(transitionPhase>0)return;
        if(pendingPurchaseSlot>=0||run.hasEvolutionChoice()){
            int n=choiceCount();
            if(k==Game.K_LEFT||k==Game.K_UP)evolutionChoiceSel=(evolutionChoiceSel+n-1)%n;
            else if(k==Game.K_RIGHT||k==Game.K_DOWN)evolutionChoiceSel=(evolutionChoiceSel+1)%n;
            else if(pendingPurchaseSlot>=0&&(k==Game.K_0||k==Game.K_SOFT2||k==Game.K_STAR)){pendingPurchaseSlot=-1;evolutionChoiceSel=0;resetTouchChoice();}
            else if(k==Game.K_FIRE||k==Game.K_SOFT1){
                if(pendingPurchaseSlot>=0){col=pendingPurchaseSlot;zone=2;purchaseForm=EvolutionVariantData.choiceForm(run.shop[col],evolutionChoiceSel);pendingPurchaseSlot=-1;completePurchase();purchaseForm=0;}
                else{int p=run.pendingEvolutionPos;run.chooseEvolution(evolutionChoiceSel);evolutionFxPos=p;evolutionFxTier=Data.tier[run.get(p)];evolutionFxT=840;}
                evolutionChoiceSel=0;resetTouchChoice();saveRun();
            }
            return;
        }
        if(rewardChoice){keyRewardChoice(k);return;}
        if(refreshItemsAsk){keyRefreshItems(k);return;}
        if(showPool){keyPoolInfo(k);return;}
        if(showStoneStats){keyStoneStats(k);return;}
        if(showItemShop){keyItemShop(k);return;}
        if(showConsumables){keyConsumableBag(k);return;}
        if(showMore){keyMore(k);return;}
        if(showCraft){keyCraft(k);return;}
        if(dockPetDetail){keyPetDetail(k);return;}
        if(petActionMenu){keyPetAction(k);return;}
        if(itemActionMenu){keyItemAction(k);return;}
        if(dockDetail){keyDockDetail(k);return;}
        if(showItems){keyItems(k);return;}
        if (showSyn) { keySyn(k); return; }
        switch (state) {
            case PREP: keyPrep(k); break;
            case BATTLE: keyBattle(k); break;
            case RESULT:
                if (k == Game.K_FIRE || k == Game.K_SOFT1) {
                    beginPostRoundChoices();
                }
                break;
            default:
                if (k == Game.K_FIRE || k == Game.K_SOFT1 || k == Game.K_SOFT2) {
                    game.setScreen(new MenuScreen(game));
                }
                break;
        }
    }

    private void continueNextRound(){run.nextRound();state=PREP;warned=false;held=-1;heldFrame=-1;saveRun();if(run.mode==Run.MODE_TOWER){say(ExtraChessRules.towerRule(run.round));toastT=4000;}}

    private void afterResultChoice(){
        if(run.over)state=OVER;
        else if(run.shouldOfferNineItemRefresh()){refreshItemsAsk=true;refreshItemsChoice=0;}
        else continueNextRound();
    }

    private void ensureDraftChoice(){if(run.towerPending){postChoiceMask=StageRoad.itemReward(run.round)?StageRoad.REWARD_ITEM:0;for(int i=0;i<3;i++)rewardChoiceIds[i]=i;openChoice(CH_TOWER);resetTouchChoice();}else if(run.draftStage==0)openTypeChoice();else if(run.draftStage==1)openStarterChoice();}

    private void openChoice(int kind){rewardChoiceKind=kind;rewardChoiceSel=0;rewardChoiceScroll=0;choiceLayoutW=-1;choiceLayoutKind=-1;resetTouchChoice();typeChoiceAction=false;typeChoiceDetail=false;rewardChoice=true;}
    private void openTypeChoice(){run.typePackageChoices(poolChoiceTypes);for(int i=0;i<3;i++)rewardChoiceIds[i]=i;openChoice(CH_TYPE);}
    private void openStarterChoice(){run.starterChoices(rewardChoiceIds);openChoice(CH_STARTER);}
    private void openRewardChoice(){run.craftedChoices(rewardChoiceIds);openChoice(CH_ITEM);}

    private void beginPostRoundChoices(){
        postChoiceMask=ExtraChessRules.mode(run.mode)?(StageRoad.itemReward(run.round)?StageRoad.REWARD_ITEM:0):StageRoad.rewardMask(run.round);if(run.towerPending)postChoiceMask|=16;
        if(!run.lastWon){afterResultChoice();return;}
        openNextPostChoice();
    }

    private void openNextPostChoice(){
        if((postChoiceMask&16)!=0){postChoiceMask&=~16;for(int i=0;i<3;i++)rewardChoiceIds[i]=i;openChoice(CH_TOWER);return;}
        if((postChoiceMask&StageRoad.REWARD_ITEM)!=0){postChoiceMask&=~StageRoad.REWARD_ITEM;openRewardChoice();return;}
        int cat=-1,kind=0;
        if((postChoiceMask&StageRoad.REWARD_FAMILY)!=0){postChoiceMask&=~StageRoad.REWARD_FAMILY;cat=run.mode==Run.MODE_LEGEND?6:(run.round==5?1:(run.round==8?2:3));kind=run.mode==Run.MODE_LEGEND?CH_LEGEND:CH_ADD;}
        else if((postChoiceMask&StageRoad.REWARD_UNIQUE)!=0){postChoiceMask&=~StageRoad.REWARD_UNIQUE;cat=run.mode==Run.MODE_LEGEND?6:5;kind=run.mode==Run.MODE_LEGEND?CH_LEGEND:CH_UNIQUE;}
        else if((postChoiceMask&StageRoad.REWARD_LEGEND)!=0){postChoiceMask&=~StageRoad.REWARD_LEGEND;cat=6;kind=CH_LEGEND;}
        if(kind!=0){run.pokemonChoices(rewardChoiceIds,cat,-1,false);openChoice(kind);return;}
        afterResultChoice();
    }

    private void keyRewardChoice(int k){
        if(typeChoiceDetail){if(k==Game.K_FIRE||k==Game.K_SOFT1||k==Game.K_SOFT2||k==Game.K_0||k==Game.K_POUND)typeChoiceDetail=false;return;}
        if(typeChoiceAction){
            if(k==Game.K_UP||k==Game.K_LEFT)typeChoiceActionSel=0;
            else if(k==Game.K_DOWN||k==Game.K_RIGHT)typeChoiceActionSel=1;
            else if(k==Game.K_0||k==Game.K_SOFT2||k==Game.K_POUND)typeChoiceAction=false;
            else if(k==Game.K_FIRE||k==Game.K_SOFT1){
                if(typeChoiceActionSel==0){typeChoiceAction=false;if(rewardChoiceKind==CH_TYPE)confirmTypeChoice();else confirmStarterChoice();}
                else{typeChoiceAction=false;if(rewardChoiceKind==CH_TYPE)typeChoiceDetail=true;}
            }
            return;
        }
        if(k==Game.K_UP||k==Game.K_LEFT)rewardChoiceSel=(rewardChoiceSel+2)%3;
        else if(k==Game.K_DOWN||k==Game.K_RIGHT)rewardChoiceSel=(rewardChoiceSel+1)%3;
        else if(k==Game.K_FIRE||k==Game.K_SOFT1){
            int id=rewardChoiceIds[rewardChoiceSel];
            if(id<0){say(Lang.t("Lựa chọn này không hợp lệ","This choice is unavailable"));return;}
            if(rewardChoiceKind==CH_TYPE||rewardChoiceKind==CH_STARTER){typeChoiceAction=true;typeChoiceActionSel=0;return;}
            if(rewardChoiceKind==CH_TOWER){if(!run.chooseTowerBuff(id))return;}
            else if(rewardChoiceKind==CH_ITEM){run.giveItem(id);run.lastItem=id;}
            else{run.chooseAdditional(id);say(Lang.t("Đã nhận ","Received ")+Data.name[id]);}
            rewardChoice=false;openNextPostChoice();
        }
    }

    private void confirmTypeChoice(){int id=rewardChoiceIds[rewardChoiceSel];run.choosePoolTypes(poolChoiceTypes,id*3);saveRun();openStarterChoice();}
    private void confirmStarterChoice(){int id=rewardChoiceIds[rewardChoiceSel];if(id<0){say(Lang.t("Lựa chọn này không hợp lệ","This choice is unavailable"));return;}run.chooseStarter(id);rewardChoice=false;saveRun();say(Lang.t("Đã chọn ","Selected ")+Data.name[id]);if(run.mode==Run.MODE_TOWER){say(ExtraChessRules.towerRule(run.round));toastT=4000;}}

    public boolean pointer(int px,int py){
        touchDragY=py;touchDragX=px;
        if(pendingPurchaseSlot>=0||run.hasEvolutionChoice()){int fh=Art.fh,w=Math.min(game.W-12,250),h=TouchLayout.evolutionHeight(game.H,fh),x=(game.W-w)/2,y=(game.H-h)/2,top=y+fh+16,rowH=TouchLayout.evolutionRow(game.H,fh);if(py>=y+h-fh-8&&py<y+h&&px>=x&&px<x+w){if(choiceCount()>2){evolutionChoiceSel=(evolutionChoiceSel+(px<x+w/2?choiceCount()-1:1))%choiceCount();resetTouchChoice();}else if(pendingPurchaseSlot>=0)key(Game.K_SOFT2);return true;}int pick=TouchLayout.rowAt(px,py,x+6,top,w-12,rowH,rowH-4,2);if(pick>=0){pick+=(evolutionChoiceSel/2)*2;if(pick>=choiceCount())return true;evolutionChoiceSel=pick;if(confirmTouch(100+pick))key(Game.K_FIRE);}return true;}
        if(refreshItemsAsk){int fh=Art.fh,w=Math.min(game.W-16,174),h=fh*6+14,x=(game.W-w)/2,y=(game.H-h)/2,by=y+fh*3+8,bw=(w-15)/2;for(int i=0;i<2;i++){int bx=x+5+i*(bw+5);if(px>=bx&&px<bx+bw&&py>=by&&py<by+fh+6){boolean same=refreshItemsChoice==i;refreshItemsChoice=i;if(i==1||same)key(Game.K_FIRE);}}return true;}
        if(state==BATTLE&&!rewardChoice&&!run.hasEvolutionChoice())return touchBattle(px,py);
        int fh=Art.fh,W=game.W,H=game.H;
        if(showSyn){fillSynCounts(synCounts);int rows=countPresentSyn(synCounts),h=Math.min(H,rows==0?fh*4+12:16*rows+fh*2+10),y=(H-h)/2;int pick=touchRow(px,py,7,y+fh+4,W-14,16,rows);if(pick>=0&&py<y+h-fh)synCursor=pick;else if(py>=y+h-fh||py<y||py>=y+h)key(Game.K_0);return true;}
        if(dockDetail){if(py>=H-fh*2)key(Game.K_0);return true;}
        if(showItems){int top=fh*2+38,rows=Math.max(1,(H-top-fh*2-5)/27),rank=0;for(int i=0;i<ownedItemCount();i++)if(ownedItemAt(i)==itemSel)rank=i;int first=rank>=rows?rank-rows+1:0,pick=touchRow(px,py,7,top,W-14,27,rows),id=ownedItemAt(first+pick);if(pick>=0&&id>=0){boolean same=itemSel==id;itemSel=id;if(same){key(Game.K_FIRE);saveRun();}}else if(py>=H-fh-8)key(Game.K_0);return true;}
        if(showConsumables){
            if(consumableTargeting){touchConsumableTarget(px,py,false);return true;}
            int rows=Math.max(1,(H-(fh+12)-fh*5)/28),rank=0;for(int i=0;i<ownedConsumableCount();i++)if(ownedConsumableAt(i)==consumableSel)rank=i;int first=rank>=rows?rank-rows+1:0,pick=touchRow(px,py,7,fh+12,W-14,28,rows),id=ownedConsumableAt(first+pick);
            if(pick>=0&&id>=0){boolean same=consumableSel==id;consumableSel=id;if(same)key(Game.K_FIRE);}else if(py>=H-fh-8)key(Game.K_0);return true;
        }
        if(showItemShop){if(itemShopTargeting){touchConsumableTarget(px,py,true);return true;}if(itemShopDetail){int h=Math.min(H-30,fh*10+18),y=(H-h)/2;if(py>=y+h-fh-5&&py<y+h)key(Game.K_0);return true;}if(itemShopAction){int w=Math.min(156,W-18),h=fh*5+12,pick=touchRow(px,py,(W-w)/2+5,(H-h)/2+fh+7,w-10,fh+2,3);if(pick>=0){itemShopActionSel=pick;if(confirmTouch(500+pick))key(Game.K_FIRE);}return true;}return touchItemShop(px,py);}
        if(showMore){int w=Math.min(182,W-18),h=Math.min(H-8,fh*10+25),step=Math.max(fh+1,(h-fh-9)/8),pick=touchRow(px,py,(W-w)/2+5,(H-h)/2+fh+6,w-10,step,8);if(pick>=0){moreSel=pick;if(confirmTouch(200+pick))key(Game.K_FIRE);}return true;}
        if(petActionMenu||itemActionMenu){int w=Math.min(132,W-16),h=fh*4+8,pick=touchRow(px,py,(W-w)/2+4,(H-h)/2+fh+5,w-8,fh+2,2);if(pick>=0){if(petActionMenu)petAction=pick;else itemAction=pick;if(confirmTouch((petActionMenu?300:400)+pick))key(Game.K_FIRE);}return true;}
        if(dockPetDetail){
            if(py>=43&&py<43+fh+5&&px>=6&&px<W-6){petDetailTab=Math.min(2,(px-6)/Math.max(1,(W-12)/3));petEquipRow=-1;petEquipCursor=fruitBagCursor=dockPetDetailScroll=0;return true;}
            int baseY=43+fh+10;
            if(petDetailTab==1){boolean compact=H<220;int rowH=compact?38:fh+42,base=compact?70:12,visible=Math.max(1,(W-base-12)/38);for(int r=0;r<2;r++){int top=baseY+r*rowH+(compact?0:fh+4),cursor=petEquipRow==r?petEquipCursor:0,start=Math.max(0,cursor-visible+1),col=(px-base)/38,rank=start+col,n=r==0?3:ownedItemCount();if(r==1&&py>=top&&py<top+34&&(px<10||px>=W-10)){petEquipRow=1;petEquipCursor=Math.max(0,Math.min(Math.max(0,n-1),cursor+(px<W/2?-visible:visible)));return true;}if(px>=base&&col<visible&&rank<n&&py>=top&&py<top+30){boolean same=petEquipRow==r&&petEquipCursor==rank;petEquipRow=r;petEquipCursor=rank;if(same)key(Game.K_FIRE);return true;}}}
            if(petDetailTab==2){int step=(W-24)/6,top=baseY+fh+3;for(int r=0;r<3;r++){int iy=top+r*29+(r==2?fh+3:0),c=(px-12)/Math.max(1,step);if(r==2&&py>=iy-2&&py<iy+28&&(px<10||px>=W-10)){petEquipRow=2;fruitBagCursor=Math.max(0,Math.min(Math.max(0,ownedConsumableCount()-1),fruitBagCursor+(px<W/2?-6:6)));return true;}if(px>=12&&c<6&&py>=iy-2&&py<iy+26){int rank=r==2?(fruitBagCursor/6)*6+c:r*6+c;boolean same=petEquipRow==r&&(r==2?fruitBagCursor:petEquipCursor)==rank;petEquipRow=r;if(r==2)fruitBagCursor=rank;else petEquipCursor=rank;if(same&&r==2)key(Game.K_FIRE);return true;}}}
            if(py>=H-fh-8)key(Game.K_0);return true;
        }
        if(showCraft){if(py>=4&&py<fh+9){craftTab=px<W/2?0:1;craftFocus=0;return true;}return touchCraft(px,py);}
        if(!rewardChoice){
            if(state==PREP&&!refreshItemsAsk&&!showPool&&!showStoneStats&&!showItemShop&&!showConsumables&&!showMore&&!showCraft&&!dockPetDetail&&!petActionMenu&&!itemActionMenu&&!dockDetail&&!showItems&&!showSyn){
                layout();int z=-1,c=0,r=0;
                if(touchPrepDock(px,py))return true;
                if(px>=boardX&&px<boardX+8*cell&&py>=boardY&&py<boardY+3*cell){z=0;c=(px-boardX)/cell;r=(py-boardY)/cell;}
                else if(px>=boardX&&px<boardX+8*cell&&py>=benchY&&py<benchY+cell){z=1;c=(px-boardX)/cell;}
                else if(px>=0&&px<5*cw&&py>=shopY&&py<shopY+shopH){z=2;c=px/cw;}
                else if(px>=0&&px<5*cw&&py>=btnY&&py<btnY+btnH){z=3;c=px/cw;}
                if(z>=0){boolean confirm=zone==z&&col==c&&row==r;zone=z;col=c;row=r;if(z<=1&&(pendingItemA>=0||pendingConsumable>=0)){int p=pos();if(run.get(p)>=0){if(pendingConsumable>=0){boolean ok=run.useConsumable(p,pendingConsumable);say(run.msg);if(ok){saveRun();pendingConsumable=-1;megaFxPos=p;megaFxT=900;}}else{dockPetPos=p;equipPendingToDockPet();saveRun();}}}else if(z==3||confirm||held>=0)key(Game.K_FIRE);}
                return true;
            }
            return false;
        }
        if(typeChoiceDetail){typeChoiceDetail=false;return true;}
        if(typeChoiceAction){int h=fh*4+16,y=(game.H-h)/2,by=y+fh+9,step=fh+7,pick=py>=by&&py<by+step?0:(py>=by+step&&py<by+step*2?1:-1);if(pick>=0){typeChoiceActionSel=pick;if(confirmTouch(900+pick))keyRewardChoice(Game.K_FIRE);}return true;}
        if(px<choiceTouchX||px>=choiceTouchX+choiceTouchW||py<choiceTouchTop||py>=choiceTouchTop+choiceTouchView)return true;
        int cy=py-choiceTouchTop+rewardChoiceScroll,at=0;
        for(int i=0;i<3;i++){int bottom=at+choiceTouchRows[i];if(cy>=at&&cy<bottom){rewardChoiceSel=i;if(confirmTouch(1000+i))keyRewardChoice(Game.K_FIRE);return true;}at=bottom;}
        return true;
    }

    private int touchRow(int px,int py,int x,int y,int w,int step,int count){return TouchLayout.rowAt(px,py,x,y,w,step,step,count);}
    private boolean touchPrepDock(int x,int y){int W=game.W;if(y<infoY||y>=infoY+53)return false;
        if(y<infoY+25){
            if(dockMode==1){fillSynCounts(synCounts);int total=countPresentSyn(synCounts),visible=Math.max(1,(W-24)/25),first=Math.max(0,Math.min(dockSynCursor-visible+1,total-visible));if(x<12)dockSynCursor=Math.max(0,dockSynCursor-1);else if(x>=W-12)dockSynCursor=Math.min(Math.max(0,total-1),dockSynCursor+1);else{int rank=first+(x-12)/25;if(rank<total)dockSynCursor=rank;}zone=4;return true;}
            int visible=8,step=Math.max(1,Math.min(W-24,200)/8),left=(W-step*8)/2,total=dockMode==0?ownedItemCount():ownedConsumableCount();
            if(x<left||x>=left+step*8){int d=x<left?-1:1;if(dockMode==0)dockItemCursor=Math.max(0,Math.min(total-1,dockItemCursor+d));else dockConsumableCursor=Math.max(0,Math.min(total-1,dockConsumableCursor+d));zone=4;return true;}
            int rank=(dockMode==0?dockItemScroll:dockConsumableScroll)+(x-left)/step;if(rank>=total)return true;
            boolean same=zone==4&&(dockMode==0?dockItemCursor:dockConsumableCursor)==rank;zone=4;if(dockMode==0)dockItemCursor=rank;else dockConsumableCursor=rank;if(same)key(Game.K_FIRE);return true;
        }
        if(y>=infoY+29){int n=boardUnitCount(),step=Math.max(20,Math.min(25,(W-8)/Math.max(1,n))),left=(W-step*n)/2;if(x>=left&&x<left+step*n){int rank=(x-left)/step;boolean same=zone==5&&dockTeamCursor==rank;zone=5;dockTeamCursor=rank;if(pendingItemA>=0||pendingConsumable>=0){key(Game.K_FIRE);saveRun();}else if(same)key(Game.K_POUND);}return true;}return true;
    }
    private void touchConsumableTarget(int px,int py,boolean shop){int fh=Art.fh,w=Math.min(game.W-14,230),h=fh*(shop?8:7)+12,x=(game.W-w)/2,y=(game.H-h)/2,cell=Math.max(1,(w-12)/8),top=y+fh+8;
        if(py>=y+h-fh-5){key(Game.K_0);return;}if(px<x+6||px>=x+6+cell*8||py<top)return;int row=(py-top)/(fh+7),p=row*8+(px-x-6)/cell;if(row<4&&p<Run.BOARD+Run.BENCH&&(py-top)%(fh+7)<18&&run.get(p)>=0){if(shop)itemShopTarget=p;else consumableTarget=p;key(Game.K_FIRE);}
    }
    private boolean touchItemShop(int x,int y){int W=game.W,H=game.H,fh=Art.fh,tw=Math.max(1,(W-8)/3);if(x>=4&&x<W-4&&y>=fh+6&&y<fh*2+11){itemShopTab=Math.min(2,(x-4)/tw);itemShopSel=0;return true;}int count=itemShopCount(),top=fh*2+14,visible=Math.max(1,(H-top-fh*4)/28),first=Math.max(0,Math.min(itemShopSel-visible/2,count-visible)),pick=touchRow(x,y,7,top,W-14,28,visible);if(pick>=0&&first+pick<count){itemShopSel=first+pick;if(confirmTouch(600+itemShopTab*100+itemShopSel))key(Game.K_FIRE);}else if(y>=H-fh-8)key(Game.K_0);return true;}
    private int touchDragY,touchDragX;
    private boolean touchCraft(int x,int y){int W=game.W,H=game.H,fh=Art.fh,top=fh+13;
        if(craftTab==0&&y>=top&&y<top+24){int sx=W/2-60;if(x>=sx&&x<sx+24)craftA=-1;else if(x>=sx+43&&x<sx+67)craftB=-1;return true;}
        if(craftTab==1){int cols=Math.max(1,(W-16)/28),rows=Math.max(1,(H-fh-18-fh*3)/28),col=(x-8)/28,row=(y-top)/28,rank=(craftRecipeScroll+row)*cols+col;if(x>=8&&col<cols&&y>=top&&row<rows&&rank<craftCatalogCount()){boolean same=craftRecipeCursor==rank;craftRecipeCursor=rank;if(confirmTouch(2000+rank))key(Game.K_POUND);}else if(y>=H-fh-8)key(Game.K_0);return true;}
        int by=H-fh*2-13,bw=Math.max(1,(W-16)/3);if(x>=8&&x<W-8&&y>=by&&y<by+fh+5){craftFocus=2;craftButton=Math.min(2,(x-8)/bw);key(Game.K_FIRE);return true;}
        boolean compact=H<220;int visible=Math.max(1,(W-(compact?74:20))/28),base=compact?64:10;
        for(int r=0;r<2;r++){int iy=top+28+r*(compact?28:fh+30)+(compact?0:fh+2),cursor=r==0?craftItemCursor:craftFinishedCursor,start=Math.max(0,cursor-visible+1),col=(x-base)/28,rank=start+col;if(x>=base&&col<visible&&y>=iy&&y<iy+24&&rank<craftOwnedCount(r==0)){boolean same=craftFocus==r&&cursor==rank;craftFocus=r;if(r==0)craftItemCursor=rank;else craftFinishedCursor=rank;if(confirmTouch(3000+r*1000+rank)){if(r==0)key(Game.K_FIRE);else{dockDetailItem=craftOwnedAt(false,rank);detailReturnCraft=true;dockDetail=true;showCraft=false;}}return true;}}
        return true;
    }
    public void pointerDrag(int x,int y){int dx=x-touchDragX,dy=y-touchDragY;if(Math.abs(dx)>=18&&Math.abs(dx)>Math.abs(dy)){touchDragX=x;touchDragY=y;int k=dx<0?Game.K_RIGHT:Game.K_LEFT;if(showCraft||dockPetDetail)key(k);else if(state==PREP&&zone==4&&!showMore&&!showItemShop&&!showConsumables&&!showSyn)key(k);return;}if(Math.abs(dy)<18)return;touchDragY=y;int k=dy<0?Game.K_DOWN:Game.K_UP;if(showConsumables&&!consumableTargeting||showItemShop&&!itemShopAction&&!itemShopDetail&&!itemShopTargeting||showSyn||showCraft||dockPetDetail)key(k);else if(state==BATTLE&&statMode==3&&!rosterDetail&&!battleMore)moveBattleSyn(k==Game.K_DOWN?1:-1);}
    private void keyRefreshItems(int k){
        if(k==Game.K_LEFT||k==Game.K_UP)refreshItemsChoice=0;
        else if(k==Game.K_RIGHT||k==Game.K_DOWN)refreshItemsChoice=1;
        else if(k==Game.K_FIRE||k==Game.K_SOFT1){run.refreshNineItems(refreshItemsChoice==0);refreshItemsAsk=false;continueNextRound();}
    }

    private void keyBattle(int k) {
        if(Save.playPath==1&&k==Game.K_POUND&&!bt.over){bt.forcePlayerVictory();endReady=true;endBattle();return;}
        if(bt.over){
            if(endReady&&(k==Game.K_FIRE||k==Game.K_SOFT1||k==Game.K_9))endBattle();
            if(k==Game.K_FIRE||k==Game.K_SOFT1||k==Game.K_9)return;
        }
        if (rosterDetail) { keyRoster(k); return; }
        if(battleMore){
            if(k==Game.K_UP||k==Game.K_DOWN||k==Game.K_LEFT||k==Game.K_RIGHT)battleMoreSel=1-battleMoreSel;else if(k==Game.K_FIRE||k==Game.K_SOFT1){if(battleMoreSel==1)showBoardHealth=!showBoardHealth;else{statMode=5;battleMore=false;statFocus=false;}}
            else if(k==Game.K_0||k==Game.K_SOFT2||k==Game.K_POUND)battleMore=false;
            return;
        }
        int count=rosterCount(rosterSide);boolean wideBattle=UiLayout.landscape(game.W,game.H);
        if (k == Game.K_FIRE && statFocus&&wideBattle) statFocus=false;
        else if (k == Game.K_FIRE && count>0 && (statMode<3||statMode==4)) rosterDetail=true;
        else if (k == Game.K_STAR) speed = speed == 1 ? 2 : (speed == 2 ? 4 : 1);
        else if (k == Game.K_0){battleMore=true;battleMoreSel=0;}
        else if (k == Game.K_1) statMode=(statMode+5)%6;
        else if (k == Game.K_3) statMode=(statMode+1)%6;
        else if (k == Game.K_7) {rosterSide=1-rosterSide;rosterSel=0;battleSynCursor=0;}
        else if (wideBattle&&statFocus&&k==Game.K_LEFT){if(statMode%3==0)statFocus=false;else statMode--;}
        else if (wideBattle&&statFocus&&k==Game.K_RIGHT){if(statMode%3<2)statMode++;}
        else if (wideBattle&&statFocus&&k==Game.K_UP){if(statMode>=3)statMode-=3;else{rosterSide=1-rosterSide;rosterSel=0;battleSynCursor=0;}}
        else if (wideBattle&&statFocus&&k==Game.K_DOWN){if(statMode<3)statMode+=3;}
        else if (statFocus && k == Game.K_LEFT) statMode=(statMode+5)%6;
        else if (statFocus && k == Game.K_RIGHT) statMode=(statMode+1)%6;
        else if (statFocus && k == Game.K_DOWN) statFocus=false;
        else if (statFocus && k == Game.K_UP) {rosterSide=1-rosterSide;rosterSel=0;battleSynCursor=0;}
        else if (!wideBattle&&statMode==3 && (k==Game.K_LEFT||k==Game.K_UP)) moveBattleSyn(-1);
        else if (!wideBattle&&statMode==3 && (k==Game.K_RIGHT||k==Game.K_DOWN)) moveBattleSyn(1);
        else if (k == Game.K_LEFT && rosterSel>0) rosterSel--;
        else if (wideBattle&&k==Game.K_RIGHT&&(rosterSel%3==2||rosterSel+1>=count))statFocus=true;
        else if (k == Game.K_RIGHT && rosterSel+1<count) rosterSel++;
        else if (k == Game.K_UP && rosterSel>=3) rosterSel-=3;
        else if (k == Game.K_UP&&!wideBattle) statFocus=true;
        else if (k == Game.K_DOWN && rosterSel+3<count) rosterSel+=3;
        else if (k == Game.K_9 || k == Game.K_SOFT1) {
            int guard = 0;
            while (!bt.over && guard++ < 1000) bt.step();
        }
        syncWatchToRoster();
    }

    private void keySyn(int k){
        int n=presentSynCount();
        if(k==Game.K_0||k==Game.K_POUND||k==Game.K_STAR||k==Game.K_SOFT2||k==Game.K_FIRE||k==Game.K_SOFT1){showSyn=false;return;}
        if(n<=0)return;
        if(k==Game.K_UP||k==Game.K_LEFT)synCursor--;
        else if(k==Game.K_DOWN||k==Game.K_RIGHT)synCursor++;
        if(synCursor<0)synCursor=n-1;if(synCursor>=n)synCursor=0;
    }

    private void keyMore(int k){
        if(k==Game.K_UP||k==Game.K_LEFT){moreSel--;if(moreSel<0)moreSel=7;}
        else if(k==Game.K_DOWN||k==Game.K_RIGHT){moreSel++;if(moreSel>7)moreSel=0;}
        else if(k==Game.K_0||k==Game.K_POUND||k==Game.K_SOFT2)showMore=false;
        else if(k==Game.K_FIRE||k==Game.K_SOFT1){
            showMore=false;
            if(moreSel==0){run.toggleShopLock();say(run.msg);}
            else if(moreSel==1){showCraft=true;craftTab=0;craftFocus=0;craftItemCursor=craftItemScroll=0;craftA=craftB=-1;craftFinishedCursor=craftButton=0;}
            else if(moreSel==2){dockMode=0;zone=4;}
            else if(moreSel==3){dockMode=2;zone=4;pendingConsumable=-1;}
            else if(moreSel==4){dockMode=1;zone=4;dockSynCursor=0;}
            else if(moreSel==5){showPool=true;poolCursor=poolScroll=0;}
            else if(moreSel==6){showStoneStats=true;stoneStatsCursor=0;}
            else{showItemShop=true;itemShopSel=0;itemShopTab=0;itemShopTargeting=false;itemShopAction=false;itemShopDetail=false;itemShopUseId=-1;}
        }
    }

    private int boughtStoneTypeCount(){int n=0;for(int t=0;t<Data.NT;t++)if(run.synergyStoneBonus[t]>0)n++;return n;}
    private int boughtStoneTypeAt(int rank){for(int t=0,n=0;t<Data.NT;t++)if(run.synergyStoneBonus[t]>0&&n++==rank)return t;return-1;}
    private void keyStoneStats(int k){int n=boughtStoneTypeCount();if(k==Game.K_0||k==Game.K_POUND||k==Game.K_SOFT2||k==Game.K_FIRE||k==Game.K_SOFT1){showStoneStats=false;return;}if(k==Game.K_UP||k==Game.K_LEFT)stoneStatsCursor--;else if(k==Game.K_DOWN||k==Game.K_RIGHT)stoneStatsCursor++;if(n<=0)stoneStatsCursor=0;else{if(stoneStatsCursor<0)stoneStatsCursor=n-1;if(stoneStatsCursor>=n)stoneStatsCursor=0;}}

    private int nextOwnedConsumable(int from,int dir){for(int n=0;n<ConsumableData.COUNT;n++){from=(from+dir+ConsumableData.COUNT)%ConsumableData.COUNT;if(run.consumableCount(from)>0)return from;}return 0;}
    private int ownedConsumableCount(){int n=0;for(int id=0;id<ConsumableData.COUNT;id++)if(run.consumableCount(id)>0)n++;return n;}
    private int ownedConsumableAt(int rank){for(int id=0,n=0;id<ConsumableData.COUNT;id++)if(run.consumableCount(id)>0){if(n==rank)return id;n++;}return -1;}
    private void keyConsumableBag(int k){
        if(consumableTargeting){if(k==Game.K_0||k==Game.K_SOFT2){consumableTargeting=false;return;}if(k==Game.K_LEFT||k==Game.K_UP)consumableTarget=nextShopTarget(consumableTarget,-1);else if(k==Game.K_RIGHT||k==Game.K_DOWN)consumableTarget=nextShopTarget(consumableTarget,1);else if(k==Game.K_FIRE||k==Game.K_SOFT1){int used=consumableSel;boolean ok=run.useConsumable(consumableTarget,used);say(run.msg);if(ok){if(used==ConsumableData.SHINY_CHARM){shinyFxPos=consumableTarget;shinyFxT=900;}else if(used>=ConsumableData.MEGA_STONE){megaFxPos=consumableTarget;megaFxT=1000;if(run.isShiny(consumableTarget)){shinyFxPos=consumableTarget;shinyFxT=900;}}RunStorage.save(run);consumableTargeting=false;if(run.consumableCount(used)<=0)consumableSel=nextOwnedConsumable(used,1);}}return;}
        if(k==Game.K_0||k==Game.K_POUND||k==Game.K_SOFT2){showConsumables=false;return;}
        if(k==Game.K_UP||k==Game.K_LEFT)consumableSel=nextOwnedConsumable(consumableSel,-1);else if(k==Game.K_DOWN||k==Game.K_RIGHT)consumableSel=nextOwnedConsumable(consumableSel,1);else if(k==Game.K_FIRE||k==Game.K_SOFT1){if(ownedConsumableCount()<=0){say(Lang.t("Túi vật phẩm trống","Consumable bag is empty"));return;}consumableTarget=nextShopTarget(-1,1);if(run.get(consumableTarget)<0){say(Lang.t("Đội chưa có Pokémon","No Pokemon in the team"));return;}consumableTargeting=true;}
    }

    private int itemShopCount(){return itemShopTab==0?run.fruitShop.length:(itemShopTab==1?run.stoneShop.length:4);}
    private int shopTypeAt(int slot){return itemShopTab==1?run.stoneShop[slot]:-1;}
    private int shopIconAt(int slot){if(itemShopTab==2){int[] ids={ConsumableData.SHINY_CHARM,ConsumableData.MEGA_STONE,ConsumableData.MEMORY_DISC,ConsumableData.ZYGARDE_CUBE};return ConsumableData.ICON[ids[slot]];}int t=shopTypeAt(slot);return t>=0?SynergyStoneData.ICON[t]:ConsumableData.ICON[run.fruitShop[slot]];}
    private int genItemAt(int slot){return slot==0?ConsumableData.SHINY_CHARM:(slot==1?ConsumableData.MEGA_STONE:(slot==2?ConsumableData.MEMORY_DISC:ConsumableData.ZYGARDE_CUBE));}
    private int shopPriceAt(int slot){if(itemShopTab==2){int id=genItemAt(slot);return id==ConsumableData.SHINY_CHARM?ShinyData.PRICE:(id==ConsumableData.MEGA_STONE?MegaData.PRICE:ConsumableData.PRICE[id]);}return itemShopTab==1?SynergyStoneData.price(run.round):ConsumableData.PRICE[run.fruitShop[slot]];}
    private String shopNameAt(int slot){if(itemShopTab==2)return ConsumableData.name(genItemAt(slot));int t=shopTypeAt(slot);return t>=0?SynergyStoneData.name(t):ConsumableData.name(run.fruitShop[slot]);}
    private String shopDescAt(int slot){if(itemShopTab==2)return ConsumableData.effect(genItemAt(slot));int t=shopTypeAt(slot);return t>=0?SynergyStoneData.desc(t):ConsumableData.desc(run.fruitShop[slot]);}
    private boolean shopSoldAt(int slot){if(itemShopTab==2)return slot==0?run.shinyBoughtRound==run.round:(slot==1?run.megaBoughtRound==run.round:(slot==2?run.memoryBoughtRound==run.round:run.cubeBoughtRound==run.round));return itemShopTab==1?run.stoneBoughtRound==run.round:run.fruitBought(slot);}
    private int nextShopTarget(int from,int dir){for(int n=0;n<Run.BOARD+Run.BENCH;n++){from=(from+dir+Run.BOARD+Run.BENCH)%(Run.BOARD+Run.BENCH);if(run.get(from)>=0)return from;}return 0;}
    private int poolFamilyCount(){int n=0;for(int i=0;i<Data.N;i++)if(run.pool[i]>0)n++;return n;}
    private int poolFamilyAt(int rank){for(int i=0,n=0;i<Data.N;i++)if(run.pool[i]>0&&n++==rank)return i;return-1;}
    private void keyPoolInfo(int k){
        if(k==Game.K_FIRE||k==Game.K_0||k==Game.K_POUND||k==Game.K_SOFT1||k==Game.K_SOFT2){showPool=false;return;}
        int count=poolFamilyCount(),cols=5;if(count<=0)return;
        if(k==Game.K_LEFT)poolCursor--;else if(k==Game.K_RIGHT)poolCursor++;else if(k==Game.K_UP)poolCursor-=cols;else if(k==Game.K_DOWN)poolCursor+=cols;
        if(poolCursor<0)poolCursor=0;if(poolCursor>=count)poolCursor=count-1;
    }
    private void keyItemShop(int k){
        if(itemShopDetail){if(k==Game.K_0||k==Game.K_FIRE||k==Game.K_SOFT1||k==Game.K_SOFT2)itemShopDetail=false;return;}
        if(itemShopTargeting){if(k==Game.K_0||k==Game.K_SOFT2){itemShopTargeting=false;return;}if(k==Game.K_LEFT||k==Game.K_UP)itemShopTarget=nextShopTarget(itemShopTarget,-1);else if(k==Game.K_RIGHT||k==Game.K_DOWN)itemShopTarget=nextShopTarget(itemShopTarget,1);else if(k==Game.K_FIRE||k==Game.K_SOFT1){int used=itemShopUseId;boolean ok=run.useConsumable(itemShopTarget,used);say(run.msg);if(ok){if(used==ConsumableData.SHINY_CHARM){shinyFxPos=itemShopTarget;shinyFxT=900;}else if(used>=ConsumableData.MEGA_STONE){megaFxPos=itemShopTarget;megaFxT=1000;if(run.isShiny(itemShopTarget)){shinyFxPos=itemShopTarget;shinyFxT=900;}}RunStorage.save(run);itemShopTargeting=false;}}return;}
        if(itemShopAction){if(k==Game.K_0||k==Game.K_SOFT2){itemShopAction=false;return;}if(k==Game.K_UP||k==Game.K_LEFT){itemShopActionSel--;if(itemShopActionSel<0)itemShopActionSel=2;}else if(k==Game.K_DOWN||k==Game.K_RIGHT){itemShopActionSel++;if(itemShopActionSel>2)itemShopActionSel=0;}else if(k==Game.K_FIRE||k==Game.K_SOFT1){if(itemShopActionSel==0){boolean ok;if(itemShopTab==2)ok=itemShopSel==0?run.buyShinyCharm():(itemShopSel==1?run.buyMegaStone():(itemShopSel==2?run.buyMemoryDisc():run.buyZygardeCube()));else ok=itemShopTab==1?run.buyStone(itemShopSel):run.buyFruit(itemShopSel);say(run.msg);if(ok)RunStorage.save(run);itemShopAction=false;}else if(itemShopActionSel==1)itemShopDetail=true;else itemShopAction=false;}return;}
        if(k==Game.K_0||k==Game.K_SOFT2){showItemShop=false;return;}
        if(k==Game.K_1||k==Game.K_LEFT){itemShopTab=(itemShopTab+2)%3;itemShopSel=0;return;}if(k==Game.K_3||k==Game.K_RIGHT){itemShopTab=(itemShopTab+1)%3;itemShopSel=0;return;}
        int count=itemShopCount();
        if(k==Game.K_UP){if(count>0){itemShopSel--;if(itemShopSel<0)itemShopSel=count-1;}}
        else if(k==Game.K_DOWN){if(count>0){itemShopSel++;if(itemShopSel>=count)itemShopSel=0;}}
        else if(k==Game.K_POUND&&itemShopTab==0){say(Lang.t("Hãy dùng trong More > Túi vật phẩm","Use it from More > Consumable bag"));}
        else if(k==Game.K_9&&itemShopTab==0){if(run.rerollFruitShop()){RunStorage.save(run);say(run.msg);}else say(run.msg);}
        else if(k==Game.K_FIRE||k==Game.K_SOFT1){itemShopAction=true;itemShopActionSel=0;}
    }

    private void keyRoster(int k) {
        if(k==Game.K_FIRE||k==Game.K_SOFT2||k==Game.K_0)rosterDetail=false;
    }

    private int rosterCount(int side) {
        int n=0;if(bt!=null)for(int i=0;i<bt.n;i++)if(bt.units[i].side==side)n++;
        return n;
    }

    private int rosterAlive(int side) {
        int n=0;if(bt!=null)for(int i=0;i<bt.n;i++)if(bt.units[i].side==side&&bt.units[i].alive)n++;
        return n;
    }

    private Unit rosterUnit(int side,int slot) {
        if(bt==null)return null;
        for(int i=0,n=0;i<bt.n;i++)if(bt.units[i].side==side){if(n==slot)return bt.units[i];n++;}
        return null;
    }

    private void syncWatchToRoster(){
        Unit selected=rosterUnit(rosterSide,rosterSel);if(selected==null||bt==null)return;
        for(int i=0;i<bt.n;i++)if(bt.units[i]==selected){watch=i;return;}
    }

    private void cycleWatch(int dir) {
        if (bt == null || bt.n == 0) return;
        for (int tries = 0; tries < bt.n; tries++) {
            watch = (watch + dir + bt.n) % bt.n;
            if (bt.units[watch].alive) return;
        }
    }

    private int pos() {
        if (zone == 0) return row * 8 + col;
        if (zone == 1) return Run.BOARD + col;
        return -1;
    }

    private void moveCursor(int dx, int dy) {
        if(dx!=0&&zone==4){
            if(dockMode==0){int n=ownedItemCount();if(n>0){dockItemCursor+=dx;if(dockItemCursor<0)dockItemCursor=0;if(dockItemCursor>=n)dockItemCursor=n-1;}}
            else if(dockMode==2){int n=ownedConsumableCount();if(n>0){dockConsumableCursor+=dx;if(dockConsumableCursor<0)dockConsumableCursor=0;if(dockConsumableCursor>=n)dockConsumableCursor=n-1;}}
            else{int n=presentSynCount();if(n>0){dockSynCursor+=dx;if(dockSynCursor<0)dockSynCursor=0;if(dockSynCursor>=n)dockSynCursor=n-1;}}return;
        }
        if(dx!=0&&zone==5){
            int n=boardUnitCount();if(n>0){dockTeamCursor+=dx;if(dockTeamCursor<0)dockTeamCursor=0;if(dockTeamCursor>=n)dockTeamCursor=n-1;}return;
        }
        if (dy != 0) {
            if (zone == 0) {
                if (dy < 0) { if (row > 0) row--; }
                else if (row < 2) row++;
                else zone = 1;
            } else if (zone == 1) {
                if (dy < 0) { zone = 0; row = 2; } else zone = 2;
            } else if (zone == 2) {
                zone = dy < 0 ? 1 : 3;
            } else if(zone==3){
                zone=dy<0?2:4;
            } else if(zone==4){
                zone=dy<0?3:5;
            } else if(zone==5&&dy<0){
                zone=4;
            }
            if ((zone==2||zone==3) && col > 4) col = 4;
        } else {
            int max = zone >= 2 ? 4 : 7;
            col += dx;
            if (col < 0) col = 0;
            if (col > max) col = max;
        }
    }

    private void keyPrep(int k) {
        if (k != Game.K_SOFT2 && k != Game.K_0) quitArm = false;
        switch (k) {
            case Game.K_UP: moveCursor(0, -1); break;
            case Game.K_DOWN: moveCursor(0, 1); break;
            case Game.K_LEFT: moveCursor(-1, 0); break;
            case Game.K_RIGHT: moveCursor(1, 0); break;
            case Game.K_FIRE: act(); break;
            case Game.K_1: doReroll(); break;
            case Game.K_3: doXp(); break;
            case Game.K_7: doSell(); break;
            case Game.K_9:
            case Game.K_SOFT1:
                doFight();
                break;
            case Game.K_STAR: openSyn();break;
            case Game.K_POUND:
                if(zone<=1&&run.get(pos())>=0)openPetDetail(pos());
                else if(zone==5&&boardPosAt(dockTeamCursor)>=0)openPetDetail(boardPosAt(dockTeamCursor));
                else if(zone==4&&dockMode==0){dockDetailItem=ownedItemAt(dockItemCursor);if(dockDetailItem>=0)dockDetail=true;}
                else say(Lang.t("Chọn Pokemon rồi nhấn #","Select a Pokemon then press #"));
                break;
            case Game.K_SOFT2:
            case Game.K_0:
                if (!quitArm) {
                    quitArm = true;
                    say(Lang.t("Nhấn quay lại lần nữa để bỏ lượt chơi", "Press back again to abandon run"));
                } else {
                    run.over = true;
                    finishRun();
                    state = OVER;
                }
                break;
            default: break;
        }
    }

    private int nextOwned(int from,int dir){
        int n=ItemData.count(),p=from;
        for(int i=0;i<n;i++){p=(p+dir+n)%n;if(run.itemCount(p)>0)return p;}
        return 0;
    }

    private int ownedItemCount(){int n=0;for(int id=0;id<ItemData.count();id++)if(run.itemCount(id)>0)n++;return n;}
    private int ownedItemAt(int rank){for(int id=0,n=0;id<ItemData.count();id++)if(run.itemCount(id)>0){if(n==rank)return id;n++;}return -1;}
    private int craftCatalogCount(){int n=0;for(int id=0;id<ItemData.count();id++)if(ItemData.isComponent(id))n++;for(int r=0;r<ItemData.recipeCount();r++){int id=ItemData.recipeOutputAt(r);if(!ItemData.isComponent(id))n++;}return n;}
    private int craftCatalogAt(int rank){for(int id=0,n=0;id<ItemData.count();id++)if(ItemData.isComponent(id)){if(n==rank)return id;n++;}for(int r=0,n=componentCount();r<ItemData.recipeCount();r++){int id=ItemData.recipeOutputAt(r);if(!ItemData.isComponent(id)){if(n==rank)return id;n++;}}return -1;}
    private int componentCount(){int n=0;for(int id=0;id<ItemData.count();id++)if(ItemData.isComponent(id))n++;return n;}
    private int craftOwnedCount(boolean parts){int n=0;for(int id=0;id<ItemData.count();id++)if(run.itemCount(id)>0&&ItemData.isComponent(id)==parts)n++;return n;}
    private int craftOwnedAt(boolean parts,int rank){for(int id=0;id<ItemData.count();id++)if(run.itemCount(id)>0&&ItemData.isComponent(id)==parts&&rank--==0)return id;return -1;}
    private int boardUnitCount(){int n=0;for(int p=0;p<Run.BOARD;p++)if(run.get(p)>=0)n++;return n;}
    private int boardPosAt(int rank){for(int p=0,n=0;p<Run.BOARD;p++)if(run.get(p)>=0){if(n==rank)return p;n++;}return -1;}

    private void keyDockDetail(int k){
        if(k==Game.K_FIRE||k==Game.K_0||k==Game.K_POUND||k==Game.K_SOFT1||k==Game.K_SOFT2){dockDetail=false;if(detailReturnCraft){detailReturnCraft=false;showCraft=true;}}
    }

    private void keyItemAction(int k){
        if(k==Game.K_UP||k==Game.K_LEFT)itemAction=0;
        else if(k==Game.K_DOWN||k==Game.K_RIGHT)itemAction=1;
        else if(k==Game.K_0||k==Game.K_POUND||k==Game.K_SOFT2)itemActionMenu=false;
        else if(k==Game.K_FIRE||k==Game.K_SOFT1){
            itemActionMenu=false;
            if(itemAction==0)selectDockItem(dockDetailItem);else dockDetail=true;
        }
    }

    private void keyCraft(int k){
        if(k==Game.K_0||k==Game.K_SOFT2){showCraft=false;return;}
        if(k==Game.K_1||k==Game.K_3){craftTab=1-craftTab;craftFocus=0;return;}
        if(craftTab==1){int n=craftCatalogCount(),cols=Math.max(1,(game.W-16)/28);if(k==Game.K_LEFT)craftRecipeCursor=Math.max(0,craftRecipeCursor-1);else if(k==Game.K_RIGHT)craftRecipeCursor=Math.min(n-1,craftRecipeCursor+1);else if(k==Game.K_UP)craftRecipeCursor=Math.max(0,craftRecipeCursor-cols);else if(k==Game.K_DOWN)craftRecipeCursor=Math.min(n-1,craftRecipeCursor+cols);else if(k==Game.K_POUND){dockDetailItem=craftCatalogAt(craftRecipeCursor);detailReturnCraft=true;dockDetail=true;showCraft=false;}return;}
        if(craftFocus<0)craftFocus=0;
        if(k==Game.K_UP){craftFocus=Math.max(0,craftFocus-1);return;}if(k==Game.K_DOWN){craftFocus=Math.min(2,craftFocus+1);return;}
        if(craftFocus==2){if(k==Game.K_LEFT)craftButton=(craftButton+2)%3;else if(k==Game.K_RIGHT)craftButton=(craftButton+1)%3;else if(k==Game.K_FIRE||k==Game.K_SOFT1){if(craftButton==2)showCraft=false;else if(craftButton==1){craftA=craftB=-1;}else if(craftA>=0&&craftB>=0){boolean ok=run.craftItems(craftA,craftB);say(run.msg);if(ok){craftA=craftB=-1;craftItemCursor=0;saveRun();}}else say(Lang.t("Chọn hai mảnh trước","Choose two components first"));}return;}
        boolean parts=craftFocus==0;int n=craftOwnedCount(parts),cursor=parts?craftItemCursor:craftFinishedCursor;
        if(k==Game.K_LEFT)cursor=Math.max(0,cursor-1);else if(k==Game.K_RIGHT)cursor=Math.min(Math.max(0,n-1),cursor+1);
        if(parts)craftItemCursor=cursor;else craftFinishedCursor=cursor;
        int id=craftOwnedAt(parts,cursor);if(id<0)return;
        if(k==Game.K_POUND){dockDetailItem=id;detailReturnCraft=true;dockDetail=true;showCraft=false;return;}
        if(k==Game.K_FIRE||k==Game.K_SOFT1){if(!parts){say(Lang.t("Đồ hoàn chỉnh không thể ghép tiếp","Finished items cannot be combined"));return;}int used=(craftA==id?1:0)+(craftB==id?1:0);if(used>=run.itemCount(id)){if(craftB==id)craftB=-1;else craftA=-1;}else if(craftA<0)craftA=id;else if(craftB<0)craftB=id;else{craftA=id;craftB=-1;}}
    }

    private void selectDockItem(int id){
        if(id<0||run.itemCount(id)<=0)return;
        // Crafting is reserved for the next dedicated Item flow.
        pendingItemA=id;pendingItemB=-1;pendingMade=-1;pendingConsumable=-1;
        say(Lang.t("Đã cầm ","Holding ")+ItemData.name(id));
    }

    private void openPetDetail(int p){dockPetPos=p;fruitBagCursor=petDetailTab=petEquipRow=petEquipCursor=dockPetDetailScroll=0;dockPetDetail=true;}
    private int eatenFruitCount(){return run.boostUses[dockPetPos];}
    private int eatenFruitAt(int rank){return run.eatenFruitAt(dockPetPos,rank);}
    private void keyPetDetail(int k){
        if(k==Game.K_0||k==Game.K_POUND||k==Game.K_SOFT2){dockPetDetail=false;return;}
        if(k==Game.K_1||k==Game.K_3){petDetailTab=(petDetailTab+(k==Game.K_1?2:1))%3;petEquipCursor=dockPetDetailScroll=0;petEquipRow=-1;return;}
        if(k==Game.K_7||k==Game.K_9){dockPetDetailScroll=Math.max(0,dockPetDetailScroll+(k==Game.K_7?-18:18));return;}
        if(petEquipRow<0){if(k==Game.K_LEFT||k==Game.K_RIGHT){petDetailTab=(petDetailTab+(k==Game.K_LEFT?2:1))%3;petEquipCursor=dockPetDetailScroll=0;}else if(k==Game.K_DOWN||k==Game.K_FIRE||k==Game.K_SOFT1){petEquipRow=0;petEquipCursor=0;}return;}
        if(petDetailTab==0){if(k==Game.K_UP){if(dockPetDetailScroll==0)petEquipRow=-1;else dockPetDetailScroll=Math.max(0,dockPetDetailScroll-18);}else if(k==Game.K_DOWN)dockPetDetailScroll+=18;return;}
        if(petDetailTab==2){if(petEquipRow==2){int total=ownedConsumableCount();if(k==Game.K_UP){petEquipCursor=6+fruitBagCursor%6;petEquipRow=1;}else if(k==Game.K_LEFT)fruitBagCursor=Math.max(0,fruitBagCursor-1);else if(k==Game.K_RIGHT)fruitBagCursor=Math.min(Math.max(5,total-1),fruitBagCursor+1);else if(k==Game.K_FIRE||k==Game.K_SOFT1){int id=ownedConsumableAt(fruitBagCursor);if(id>=0){boolean ok=run.useConsumable(dockPetPos,id);say(run.msg);if(ok){saveRun();fruitBagCursor=Math.min(fruitBagCursor,Math.max(0,ownedConsumableCount()-1));if(id==ConsumableData.SHINY_CHARM){shinyFxPos=dockPetPos;shinyFxT=900;}else if(id>=ConsumableData.MEGA_STONE){megaFxPos=dockPetPos;megaFxT=1000;}}}}}else{if(k==Game.K_UP){if(petEquipCursor<6)petEquipRow=-1;else petEquipCursor-=6;}else if(k==Game.K_DOWN){if(petEquipCursor<6)petEquipCursor+=6;else{petEquipRow=2;fruitBagCursor=petEquipCursor%6;}}else if(k==Game.K_LEFT)petEquipCursor=Math.max(0,petEquipCursor-1);else if(k==Game.K_RIGHT)petEquipCursor=Math.min(11,petEquipCursor+1);if(petEquipRow>=0&&petEquipRow<2)petEquipRow=petEquipCursor/6;}dockPetDetailScroll=0;return;}
        if(k==Game.K_UP){if(petEquipRow==0)petEquipRow=-1;else{petEquipRow=0;petEquipCursor=Math.min(2,petEquipCursor);}dockPetDetailScroll=0;return;}
        if(k==Game.K_DOWN){petEquipRow=1;petEquipCursor=Math.min(petEquipCursor,Math.max(0,ownedItemCount()-1));dockPetDetailScroll=0;return;}
        int n=petEquipRow==0?3:ownedItemCount();if(k==Game.K_LEFT)petEquipCursor=Math.max(0,petEquipCursor-1);else if(k==Game.K_RIGHT)petEquipCursor=Math.min(Math.max(0,n-1),petEquipCursor+1);
        else if(k==Game.K_FIRE||k==Game.K_SOFT1){boolean ok=petEquipRow==0?run.unequip(dockPetPos,petEquipCursor):run.equipDirect(dockPetPos,ownedItemAt(petEquipCursor));say(run.msg);if(ok){saveRun();if(petEquipRow==1)petEquipCursor=Math.min(petEquipCursor,Math.max(0,ownedItemCount()-1));}}dockPetDetailScroll=0;
    }
    private void keyPetAction(int k){
        if(k==Game.K_UP||k==Game.K_LEFT)petAction=0;
        else if(k==Game.K_DOWN||k==Game.K_RIGHT)petAction=1;
        else if(k==Game.K_0||k==Game.K_POUND||k==Game.K_SOFT2)petActionMenu=false;
        else if(k==Game.K_FIRE||k==Game.K_SOFT1){petActionMenu=false;if(petAction==0){dockPetDetailScroll=0;dockPetDetail=true;}else equipPendingToDockPet();}
    }

    private void equipPendingToDockPet(){
        if(pendingItemA<0){say(Lang.t("Chưa chọn trang bị","No item selected"));return;}
        boolean ok=run.equipDirect(dockPetPos,pendingItemA);say(run.msg);
        if(ok){pendingItemA=pendingItemB=pendingMade=-1;int n=ownedItemCount();if(dockItemCursor>=n)dockItemCursor=Math.max(0,n-1);}
    }

    private void openItemBag(int p){
        if(p<0||run.get(p)<0){say(Lang.t("Hãy chọn Pokemon trước","Select a unit first"));return;}
        itemTarget=p;
        itemSel=nextOwned(-1,1);
        showItems=true;
    }

    private void keyItems(int k){
        if(k==Game.K_UP||k==Game.K_LEFT)itemSel=nextOwned(itemSel,-1);
        else if(k==Game.K_DOWN||k==Game.K_RIGHT)itemSel=nextOwned(itemSel,1);
        else if(k==Game.K_FIRE||k==Game.K_SOFT1){
            if(run.itemCount(itemSel)>0){boolean ok=run.equipItem(itemTarget,itemSel);say(run.msg);if(ok&&run.itemCount(itemSel)<=0)itemSel=nextOwned(itemSel,1);}
            else say(Lang.t("Túi trang bị trống","Equipment bag is empty"));
        } else if(k==Game.K_POUND||k==Game.K_0||k==Game.K_SOFT2)showItems=false;
    }

    private void act() {
        if (zone <= 1) {
            int p = pos();
            if (held < 0) {
                if (run.get(p) >= 0) {held=p;heldFrame=visualTime/90+p;} else say(Lang.t("Ô trống", "Empty slot"));
            } else if (p == held) {
                held = -1;heldFrame=-1;
            } else {
                if (!run.move(held, p)) say(run.msg);
                held = -1;heldFrame=-1;
            }
        } else if (zone == 2) {
            if (held >= 0) doSell();
            else{
                int sp=run.shop[col];
                if(EvolutionBranchData.purchaseChoice(sp)){pendingPurchaseSlot=col;evolutionChoiceSel=0;resetTouchChoice();}
                else completePurchase();
            }
        } else if(zone==3) {
            switch (col) {
                case 0: doXp(); break;
                case 1: doReroll(); break;
                case 2: showMore=true;moreSel=0;break;
                case 3: openSyn();break;
                default: doFight(); break;
            }
        } else if(zone==4){
            if(dockMode==0){int id=ownedItemAt(dockItemCursor);if(id>=0){selectDockItem(id);zone=5;}else say(Lang.t("Kho trang bị trống","Item reserve is empty"));}
            else if(dockMode==2){int id=ownedConsumableAt(dockConsumableCursor);if(id>=0){pendingConsumable=id;pendingItemA=pendingItemB=pendingMade=-1;zone=5;say(Lang.t("Chọn Pokémon để dùng ","Choose a Pokemon for ")+ConsumableData.name(id));}else say(Lang.t("Túi vật phẩm trống","Consumable bag is empty"));}
        } else if(zone==5){
            int p=boardPosAt(dockTeamCursor);
            if(p>=0&&dockMode==2&&pendingConsumable>=0){int used=pendingConsumable;boolean ok=run.useConsumable(p,used);say(run.msg);if(ok){if(used==ConsumableData.SHINY_CHARM){shinyFxPos=p;shinyFxT=900;}else if(used>=ConsumableData.MEGA_STONE){megaFxPos=p;megaFxT=1000;if(run.isShiny(p)){shinyFxPos=p;shinyFxT=900;}}RunStorage.save(run);if(run.consumableCount(used)<=0){pendingConsumable=-1;int n=ownedConsumableCount();if(dockConsumableCursor>=n)dockConsumableCursor=Math.max(0,n-1);}zone=4;}}
            else if(p>=0){dockPetPos=p;if(pendingItemA>=0)equipPendingToDockPet();else say(Lang.t("#: chi tiết · chọn đồ để trang bị","#: details · select an item to equip"));}
        }
    }

    private void doReroll() {
        if (!run.reroll()) say(run.msg);
    }

    private void openSyn(){if(presentSynCount()<=0)say(Lang.t("Chưa có Pokémon trên bàn","No Pokémon on board"));else{showSyn=true;synCursor=0;}}

    private void doXp() {
        if (!run.buyXp()) say(run.msg);
    }

    private void doSell() {
        int p = held >= 0 ? held : (zone <= 1 ? pos() : -1);
        if (p < 0 || run.get(p) < 0) { say(Lang.t("Hãy chọn Pokemon trước", "Select a unit first")); return; }
        int sp = run.get(p);
        int v = Data.sellValue(sp);
        int bx=boardX;
        sellGoldX=bx+(p<Run.BOARD?p%8:p-Run.BOARD)*cell+cell/2;
        sellGoldY=(p<Run.BOARD?boardY+(p/8)*cell:benchY)+cell/2;
        sellGoldValue=v;sellGoldT=850;
        run.sell(p);
        held = -1;heldFrame=-1;
        say(Lang.t("Đã bán ", "Sold ") + Data.name[sp] + " +" + v + "g");
    }

    private void doFight() {
        int bc = run.boardCount();
        if (bc == 0) { say(Lang.t("Hãy đưa Pokemon lên bàn!", "Put a unit on the board!")); return; }
        if (!warned && bc < run.level) {
            boolean spare = false;
            for (int i = 0; i < Run.BENCH; i++) if (run.bench[i] >= 0) spare = true;
            if (spare || run.gold >= 2) {
                warned = true;
                say(Lang.t("Còn ô triển khai! Nhấn GO lần nữa", "Free board slots! GO again to fight"));
                return;
            }
        }
        held = -1;heldFrame=-1;
        bt = run.makeBattle();
        watch = 0;
        for (int i = 0; i < bt.n; i++) if (bt.units[i].side == 0) { watch = i; break; }
        simClock.reset();
        endT = 0;
        endReady = false;
        battleEndVisualsCleared=false;
        transitionPhase=1;
        transitionT=0;
    }

    // ---- layout ----------------------------------------------------------

    private void layout() {
        int W = game.W, H = game.H, fh = Art.fh;
        prepLandscape=UiLayout.landscape(W,H);
        cell = prepLandscape?Math.min(48,(W-Math.max(112,W*28/100)-8)/8):W/8;
        while (true) {
            hudH = fh * 2 + 4;
            shopH = cell + fh + 2;
            btnH = fh + 4;
            // Reserve two item/team rows plus the original information panel.
            int need = hudH + 3 * cell + 2 + cell + 2 + shopH + 2 + btnH + 2 + 55 + (prepLandscape?0:fh*3);
            if (need <= H || cell <= 10) break;
            cell--;
        }
        boardX=prepLandscape?4:(W-8*cell)/2;
        boardY = hudH;
        benchY = boardY + 3 * cell + 2;
        shopY = benchY + cell + 2;
        btnY = shopY + shopH + 2;
        infoY = btnY + btnH + 2;
        cw = W / 5;
    }

    // ---- painting --------------------------------------------------------

    public void paint(Graphics g) {
        // DirectGraphics keeps clip state between frames on several J2ME emulators.
        // Always restore the full canvas so a closed choice popup cannot leave trails.
        g.setClip(0,0,game.W,game.H);
        // Choice dialogs cover gameplay and do not need the animated board,
        // shop, units and selection outline to be repainted underneath.  This
        // avoids decoding/drawing several large Gen 4-9 atlases every frame.
        if(rewardChoice){
            g.setColor(0x101830);g.fillRect(0,0,game.W,game.H);
            paintRewardChoice(g);return;
        }
        if (state == PREP) paintPrep(g);
        else {
            if (bt != null) paintBattle(g);
            else {
                g.setColor(0x14182A);
                g.fillRect(0, 0, game.W, game.H);
            }
            if (state == RESULT) paintResult(g);
            else if (state == OVER) paintOver(g);
        }
        if (showSyn) paintSyn(g);
        if (showMore) paintMore(g);
        if (showPool) paintPoolInfo(g);
        if (showStoneStats) paintStoneStats(g);
        if (showItemShop) paintItemShop(g);
        if (showConsumables) paintConsumableBag(g);
        if (showItems) paintItemBag(g);
        if (showCraft) paintCraft(g);
        if (dockDetail) paintDockDetail(g);
        if (itemActionMenu) paintItemActionMenu(g);
        if (dockPetDetail) paintDockPetDetail(g);
        if (petActionMenu) paintPetActionMenu(g);
        if (state==BATTLE&&rosterDetail) paintRosterDetail(g);
        if(refreshItemsAsk)paintRefreshItemsAsk(g);
        if(pendingPurchaseSlot>=0||run.hasEvolutionChoice())paintEvolutionChoice(g);
    }

    private void completePurchase(){
                int boughtSp=run.shop[col],landed=-1;
                for(int i=0;i<Run.BENCH;i++)buyBenchBefore[i]=run.bench[i];
                if(!run.buy(col,purchaseForm))say(run.msg);
                else{
                    for(int i=0;i<Run.BENCH;i++)if(run.bench[i]>=0&&run.bench[i]!=buyBenchBefore[i]){
                        landed=i;benchSpawnSlot=i;benchSpawnT=620;break;
                    }
                    if(boughtSp>=0&&Data.category[boughtSp]==6&&landed>=0){
                        legendaryBuySlot=landed;legendaryBuyT=760;
                    }
                    if(run.mergeEventPos>=0){
                        evolutionFxPos=run.mergeEventPos;evolutionFxTier=run.mergeEventTier;evolutionFxT=840;
                        benchSpawnT=0;benchSpawnSlot=-1;
                    }
                }
    }

    private void paintEvolutionChoice(Graphics g){
        int sp=choiceSpecies(),p=run.pendingEvolutionPos,n=choiceCount(),first=(evolutionChoiceSel/2)*2;
        boolean shiny=pendingPurchaseSlot<0&&run.isShiny(p);
        int w=Math.min(game.W-12,250),h=TouchLayout.evolutionHeight(game.H,Art.fh),x=(game.W-w)/2,y=(game.H-h)/2;
        Art.box(g,x,y,w,h,0x111B30,0xFFE060);
        Art.textBC(g,Lang.t("CHỌN DẠNG","CHOOSE FORM")+" "+(evolutionChoiceSel+1)+"/"+n,game.W/2,y+7,0xFFE060);
        int top=y+Art.fh+16,cw=w-12,rowH=TouchLayout.evolutionRow(game.H,Art.fh);
        for(int row=0;row<2&&first+row<n;row++){
            int i=first+row,species=pendingPurchaseSlot>=0?sp:EvolutionBranchData.choiceSpecies(sp,i);
            int form=pendingPurchaseSlot>=0?EvolutionVariantData.choiceForm(sp,i):EvolutionBranchData.choiceForm(sp,i);
            int cx=x+6,cy=top+row*rowH;
            g.setColor(i==evolutionChoiceSel?0x304D75:0x1B2940);g.fillRect(cx,cy,cw,rowH-4);
            g.setColor(i==evolutionChoiceSel?0xFFE060:0x51647A);g.drawRect(cx,cy,cw-1,rowH-5);
            PetAvatar.draw(g,species,form,false,shiny,cx+5,cy+(rowH-4-32)/2,32);
            int tx=cx+44,ty=cy+6;String name=form==0?Data.name[species]:SpecialFormData.name(form);
            g.setClip(tx,ty,cw-49,Art.fh+2);
            if(i==evolutionChoiceSel)drawMarqueeBold(g,name,tx,ty,cw-49,0xFFFFFF);else Art.textB(g,name,tx,ty,0xFFFFFF);
            g.setClip(0,0,game.W,game.H);
            int t1=EvolutionVariantData.type1(species,form),t2=EvolutionVariantData.type2(species,form);
            Art.typeIcon(g,t1,tx,ty+Art.fh+4);if(t2>=0)Art.typeIcon(g,t2,tx+20,ty+Art.fh+4);
        }
        Art.textSmallC(g,n>2?Lang.t("< Trước   2/8: chọn   Sau >","< Previous   2/8: choose   Next >"):pendingPurchaseSlot>=0?Lang.t("5: mua   0: hủy / chạm để hủy","5: buy   0: cancel / tap to cancel"):Lang.t("2/8: chọn   5: xác nhận","2/8: choose   5: confirm"),game.W/2,y+h-Art.fh-5,0x80D8FF);
    }

    private void drawUnit(Graphics g, int sp, int x, int y, int size) {
        Art.sprite(g, sp, x, y, size);
        drawEvolutionDots(g,sp,x+2,y+size-3);
    }

    private void paintPrep(Graphics g) {
        layout();
        int W = game.W, H = game.H, fh = Art.fh;
        g.setColor(0x1A2230);
        g.fillRect(0, 0, W, H);

        // HUD
        String roundText=Lang.t("Vòng ","Round ")+run.round+(run.endlessMode()?" / " + Lang.t("Vô tận","Endless"):"/"+run.maxRound());
        String levelText="Lv"+run.level+" "+(run.level>=Data.MAX_LEVEL?"MAX":run.xp+"/"+Data.XP_NEED[run.level])+"  "+run.boardCount()+"/"+run.level;
        Art.text(g,roundText,3,1,0xFFFFFF);
        Art.uiIcon(g,1,W-43,0);Art.textR(g,""+run.hp,W-3,1,run.hp>40?0x80FF80:0xFF6060);
        Art.uiIcon(g,7,2,fh+1);Art.text(g,levelText,18,fh+2,0xB0C8FF);
        String money=unlimitedGold?"INF":""+run.gold;int moneyX=W-3-Art.textWidth(money)-9;
        Art.textB(g,"$",moneyX,fh+1,0xFFD030);Art.textR(g,money,W-3,fh+2,0xFFD030);
        int prepLeft=Math.max(3+Art.textWidth(roundText),18+Art.textWidth(levelText))+3;
        int prepRight=Math.min(W-44,moneyX-3),prepAvail=prepRight-prepLeft;
        int prepCount=prepLandscape?3:(prepAvail>=82?3:2),prepTotal=prepCount*24+(prepCount-1)*5;
        int prepX=prepLandscape?Math.max(prepLeft,W-49-prepTotal):(prepAvail>=prepTotal?prepLeft+(prepAvail-prepTotal)/2:(W-prepTotal)/2);
        StageRoad.draw(g,prepX,2,run.round,run.endlessMode()?run.round+2:run.maxRound(),24,prepCount,run);

        int bx = boardX;
        prepHoverType=-1;if(dockMode==1&&zone==4){fillSynCounts(synCounts);prepHoverType=presentSynTypeAt(dockSynCursor,synCounts);}
        // Pass 1: paint every formation cell. Units must not be interleaved with
        // these fills: a later cell used to erase wings/tails overflowing from
        // an earlier cell (most visible in column zero and on large birds).
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 8; c++) {
                int x = bx + c * cell, y = boardY + r * cell;
                g.setColor(((r + c) & 1) == 0 ? 0x2F4A36 : 0x2A4230);
                g.fillRect(x, y, cell, cell);
            }
        }
        for (int c = 0; c < 8; c++) {
            int x = bx + c * cell, y = benchY;
            g.setColor(((c) & 1) == 0 ? 0x4A3A28 : 0x42341F);
            g.fillRect(x, y, cell, cell);
        }
        if(sellGoldT>0){int age=850-sellGoldT,fy=sellGoldY-age/24;Art.textBC(g,"+"+sellGoldValue+"$",sellGoldX,fy,age<620?0xFFD030:0xFFF2A0);}
        if(benchSpawnT>0&&benchSpawnSlot>=0){
            int fx=bx+benchSpawnSlot*cell+(cell-32)/2,fy=benchY+cell-30;
            Art.spawnFx(g,fx,fy,620-benchSpawnT);
        }
        if(evolutionFxT>0&&evolutionFxPos>=0){
            int ex,ey;
            if(evolutionFxPos<Run.BOARD){ex=bx+(evolutionFxPos%8)*cell;ey=boardY+(evolutionFxPos/8)*cell;}
            else{ex=bx+(evolutionFxPos-Run.BOARD)*cell;ey=benchY;}
            Art.evolutionFx(g,evolutionFxTier,ex+(cell-56)/2,ey+cell-51,840-evolutionFxT);
        }
        if(shinyFxT>0&&shinyFxPos>=0){
            int sx,sy;if(shinyFxPos<Run.BOARD){sx=bx+(shinyFxPos%8)*cell;sy=boardY+(shinyFxPos/8)*cell;}else{sx=bx+(shinyFxPos-Run.BOARD)*cell;sy=benchY;}
            Art.shinyTransformFx(g,sx+cell/2,sy+cell/2,900-shinyFxT);
        }
        if(megaFxT>0&&megaFxPos>=0){
            int mx,my;if(megaFxPos<Run.BOARD){mx=bx+(megaFxPos%8)*cell;my=boardY+(megaFxPos/8)*cell;}else{mx=bx+(megaFxPos-Run.BOARD)*cell;my=benchY;}
            Art.megaTransformFx(g,mx+cell/2,my+cell/2,1000-megaFxT);
        }
        // Pass 2: draw all units after all backgrounds. Lower rows are painted
        // later, giving large sprites a stable natural depth order.
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 8; c++) {
                int sp = run.board[r * 8 + c];
                if (sp >= 0) {
                    int p=r*8+c,x=bx+c*cell,y=boardY+r*cell;
                    if(transitionPhase==1){
                        int q=transitionUnitProgress();
                        int tx=boardX+4*cell-cell/2,ty=boardY-cell/2;
                        x=x+(tx-x)*q/256;y=y+(ty-y)*q/256;
                        if(q<244)drawSetupUnit(g,sp,x,y,cell,p);
                    }else{
                        drawSetupUnit(g,sp,x,y,cell,p);
                        drawEquippedItems(g,p,x,y,cell);
                    }
                }
            }
        }
        for (int c = 0; c < 8; c++) {
            int sp = run.bench[c];
            if (sp >= 0) {
                int p=Run.BOARD+c,x=bx+c*cell;
                drawSetupUnit(g,sp,x,benchY,cell,24+c);
                drawEquippedItems(g,p,x,benchY,cell);
            }
        }
        // Thin one-pixel source marker; the old double border covered small units.
        if (held >= 0) {
            int hx, hy;
            if (held < Run.BOARD) { hx = bx + (held % 8) * cell; hy = boardY + (held / 8) * cell; }
            else { hx = bx + (held - Run.BOARD) * cell; hy = benchY; }
            g.setColor(0x80D8FF);g.drawRect(hx,hy,cell-1,cell-1);
            int selected=run.get(held);
            if(selected>=0)Art.formationSelection(g,selected,hx,hy,cell,game.W,heldFrame,0xFFFFFF,run.isMega(held),run.specialFormAt(held),run.isShiny(held));
        }
        // shop
        for (int i = 0; i < 5; i++) {
            int x = i * cw + 1, y = shopY;
            int card=run.shop[i]>=0?Art.rarityColor(Data.category[run.shop[i]]):0x2A2C48;
            g.setColor(run.shop[i]>=0?Art.dark(card):card);
            g.fillRect(x, y, cw - 2, shopH);
            if(run.shop[i]>=0){g.setColor(card);g.fillRect(x,y,cw-2,2);}
            int sp = run.shop[i];
            if (sp >= 0) {
                if(SpecialFormData.zygarde(sp))SpecialFormAtlas.avatar(g,SpecialFormData.ZYGARDE_10,x+(cw-32)/2,y,false,false);else Art.avatar(g, sp, x + (cw - 34) / 2, y);
                boolean afford = run.gold >= Run.shopCost(sp);
                Art.textB(g,"$",x+2,y+shopH-fh-1,afford?0xFFD030:0x805040);
                Art.text(g, "" + Run.shopCost(sp), x + 11, y + shopH - fh - 1, afford ? 0xFFD030 : 0x805040);
                int own = run.count(sp);
                if (own > 0) Art.textR(g, "x" + own, x + cw - 4, y + 1, own >= 2 ? 0x80FF80 : 0xB0C8FF);
            } else {
                Art.textC(g, "-", x + cw / 2, y + shopH / 2 - fh / 2, 0x606070);
            }
        }
        // buttons
        for (int i = 0; i < 5; i++) {
            int x = i * cw + 1;
            g.setColor(i == 4 ? 0xC59112 : (i<2?0x238BCB:0x465875));
            g.fillRect(x, btnY, cw - 2, btnH);
            if(i==0)Art.uiIcon(g,7,x+2,btnY+1); else if(i==1)drawRollIconYellow(g,x+2,btnY+1);
            String label=i==0?"XP":(i==1?Lang.t("Đổi","Roll"):(i==2?"More":(i==3?Lang.t("Hệ","Syn"):BTN[i])));
            Art.textC(g, label, x + (cw - 2) / 2+(i<2?4:0), btnY + 2, 0xFFFFFF);
            if(i==2&&run.shopLocked){g.setColor(0x80FF80);g.fillRect(x+2,btnY+2,3,3);}
        }
        // cursor
        int cx, cy, cwid, chei;
        if (zone == 0) { cx = bx + col * cell; cy = boardY + row * cell; cwid = cell; chei = cell; }
        else if (zone == 1) { cx = bx + col * cell; cy = benchY; cwid = cell; chei = cell; }
        else if (zone == 2) { cx = col * cw + 1; cy = shopY; cwid = cw - 2; chei = shopH; }
        else { cx = col * cw + 1; cy = btnY; cwid = cw - 2; chei = btnH; }
        if(zone<=3){
            g.setColor(0xFFE040);
            g.drawRect(cx, cy, cwid - 1, chei - 1);
        }
        if (held >= 0 && zone <= 1) {
            int hs=run.get(held);
            // The drag preview must use the same raw-atlas frame, canvas and
            // bottom anchor as board/bench units. Art.sprite() is the small
            // static collection icon and made a held Gen-1 Pokemon collapse.
            if(hs>=0){
                drawSetupUnit(g,hs,cx,cy,cell,held+37);
                Art.formationSelection(g,hs,cx,cy,cell,game.W,heldFrame,0xFFFFFF,run.isMega(held),run.specialFormAt(held),run.isShiny(held));
            }
        }

        if(prepLandscape)paintPrepLandscapeInfo(g,boardX+8*cell+3,boardY,W-(boardX+8*cell+6),benchY+cell-boardY);
        paintPrepDock(g,infoY,53);
        if(!prepLandscape)paintPrepInfo(g,infoY+55,H-(infoY+55));
        if(transitionPhase==1)GachaFx.transitionPortal(g,boardX+4*cell,boardY,visualTime,transitionRadius(cell));
        if(legendaryBuyT>0&&legendaryBuySlot>=0)
            drawLegendaryBuyFx(g,bx+legendaryBuySlot*cell+cell/2,benchY+cell/2,W,H,760-legendaryBuyT);
        paintHudFeedback(g);
    }

    /** Top-most resource feedback; board cells used to repaint over the old text. */
    private void paintHudFeedback(Graphics g){
        int W=game.W,y=hudH+3;
        if(hudXpT>0){String s="+"+hudXpValue+" XP";int w=Art.textWidth(s)+8;g.setColor(0x102318);g.fillRect(3,y,w,Art.fh+4);g.setColor(0x50E878);g.drawRect(3,y,w-1,Art.fh+3);Art.text(g,s,7,y+2,0x90FF98);}
        if(hudGoldT>0){String s=(hudGoldValue>0?"+":"")+hudGoldValue+"$";int w=Art.textWidth(s)+8,x=W-w-3;g.setColor(hudGoldValue<0?0x2A1515:0x2A2410);g.fillRect(x,y,w,Art.fh+4);g.setColor(hudGoldValue<0?0xFF7058:0xFFD030);g.drawRect(x,y,w-1,Art.fh+3);Art.text(g,s,x+4,y+2,hudGoldValue<0?0xFF9078:0xFFE060);}
        if(levelUpFxT>0){int age=1100-levelUpFxT;for(int i=0;i<6;i++){int t=age-i*55;if(t<0)continue;int px=18+i*7,py=hudH-2+t/18;if(py>hudH+cell)continue;g.setColor((i&1)==0?0x50FF70:0xB0FF80);g.fillRect(px,py,2+(i&1),2+(i&1));}}
    }

    /** Compact inspector occupying the otherwise unused right side in wide setup mode. */
    private void paintPrepLandscapeInfo(Graphics g,int x,int y,int w,int h){
        if(w<72||h<40)return;g.setColor(0x101827);g.fillRect(x,y,w,h);g.setColor(0x40506A);g.drawRect(x,y,w-1,h-1);
        if(toastT>0){
            Art.textBC(g,Lang.t("THÔNG BÁO","NOTICE"),x+w/2,y+8,0xFFD060);
            String[] note=Art.wrap(toast,w-18,5);int yy=y+(h-note.length*Art.fh)/2;
            for(int i=0;i<note.length;i++){Art.textSmallC(g,note[i],x+w/2,yy,0xFFE090);yy+=Art.fh;}
            return;
        }
        int p=-1,sp=-1;boolean shop=false;if(zone<=1){p=pos();sp=run.get(p);}else if(zone==2){sp=run.shop[col];shop=true;}else if(zone==5){p=boardPosAt(dockTeamCursor);if(p>=0)sp=run.get(p);}
        int bodyH=h;
        if(sp>=0)drawInfoAt(g,sp,x+3,y+3,w-6,Math.max(2,(bodyH-6)/Art.fh),shop,p);
        else if(zone==3){String[] n={Lang.t("NÂNG CẤP","LEVEL UP"),Lang.t("ĐỔI SHOP","REROLL"),"MORE",Lang.t("CỘNG HƯỞNG","SYNERGY"),Lang.t("BẮT ĐẦU","START")};String[] d={Lang.t("Mua 4 XP với giá 4 vàng.","Buy 4 XP for 4 gold."),Lang.t("Đổi năm Pokémon mới trong shop với giá 2 vàng.","Roll five new shop Pokémon for 2 gold."),Lang.t("Mở khóa shop, túi đồ, ghép đồ và cửa hàng vật phẩm.","Open shop lock, bags, crafting and item shop."),Lang.t("Xem các mốc cộng hưởng hệ đang kích hoạt.","View active type synergy thresholds."),Lang.t("Bắt đầu vòng chiến đấu hiện tại.","Start the current battle round.")};Art.textBC(g,n[col],x+w/2,y+8,0xFFD060);Art.para(g,d[col],x+7,y+Art.fh+13,w-14,0xC8D8E8,Math.max(2,(bodyH-Art.fh*2-18)/Art.fh));}
        else if(zone==4&&dockMode==0&&ownedItemCount()>0){int id=ownedItemAt(dockItemCursor);Art.itemIcon(g,id,x+6,y+6);drawMarqueeBold(g,ItemData.name(id),x+35,y+7,w-42,0xFFFFFF);Art.para(g,ItemData.desc(id),x+6,y+34,w-12,0xB8C8D8,Math.max(1,(bodyH-38)/Art.fh));}
        else if(zone==4&&dockMode==2&&ownedConsumableCount()>0){int id=ownedConsumableAt(dockConsumableCursor);drawConsumableIcon(g,id,x+6,y+6,false);drawMarqueeBold(g,ConsumableData.name(id),x+35,y+7,w-42,0xFFFFFF);Art.para(g,ConsumableData.desc(id),x+6,y+34,w-12,0xB8C8D8,Math.max(1,(bodyH-38)/Art.fh));}
        else if(zone==4&&dockMode==1){int[] cnt=synCounts;fillSynCounts(cnt);int t=presentSynTypeAt(dockSynCursor,cnt);if(t>=0){Art.typeIcon(g,t,x+7,y+7);Art.textB(g,Lang.typeName(t),x+30,y+8,0xFFFFFF);Art.para(g,Lang.synergyLongDesc(t),x+7,y+31,w-14,0xB8C8D8,Math.max(2,(bodyH-35)/Art.fh));}}
        else Art.textBC(g,Lang.t("THÔNG TIN","INFORMATION"),x+w/2,y+bodyH/2-Art.fh/2,0x8090A8);
    }

    private void drawSetupUnit(Graphics g, int sp, int x, int y, int size, int phase) {
        int pos=phase>=37?phase-37:phase;boolean changing=(shinyFxT>0&&pos==shinyFxPos)||(megaFxT>0&&pos==megaFxPos);
        boolean typeLocked=prepHoverType>=0&&(run.type1At(pos)==prepHoverType||run.type2At(pos)==prepHoverType);
        int frame = held>=0&&(phase==held||phase==held+37)?heldFrame:((typeLocked||changing)?0:visualTime/90+phase);
        Art.formationSprite(g,sp,x,y,size,size,game.W,frame,run.isShiny(pos),run.isMega(pos),run.specialFormAt(pos));
        if(run.isShiny(pos))Art.shinyGlints(g,x+2,y+2,size-4,size-5,visualTime+pos*37);
        drawFormationDots(g,sp,pos,x+2,y+size-3);
    }

    /** Two mirrored full-screen light fronts emitted by a purchased Legendary. */
    private void drawLegendaryBuyFx(Graphics g,int cx,int cy,int w,int h,int age){
        int p=age*256/760;if(p>256)p=256;
        int reach=(Math.max(w,h)+24)*p/256;
        int pulse=(p<128?p:256-p);if(pulse<0)pulse=0;
        int col=pulse>70?0xFFFFFF:(pulse>30?0x80E8FF:0x4098E8);
        for(int side=-1;side<=1;side+=2){
            int x=cx+side*reach;
            g.setColor(0x2868B8);g.drawLine(x-1,0,x-1,h);
            g.setColor(col);g.drawLine(x,0,x,h);
            g.setColor(0x60D8FF);g.drawLine(x+1,0,x+1,h);
            int slant=8+p/24;
            g.drawLine(x-side*slant,0,x+side*slant,h);
        }
        int rw=reach*2,rh=Math.max(8,reach);
        g.setColor(col);g.drawArc(cx-reach,cy-rh/2,rw,rh,0,360);
        if(p<72){
            int r=4+p/6;g.setColor(0xFFFFFF);
            g.drawLine(cx-r,cy,cx+r,cy);g.drawLine(cx,cy-r,cx,cy+r);
        }
    }

    private int familyMaxTier(int sp){int f=Data.fam[sp],max=f>=0&&f<FAMILY_MAX_TIER.length?FAMILY_MAX_TIER[f]:1;return max>0?max:1;}

    private void drawEvolutionDots(Graphics g,int sp,int x,int y){drawPetMarkers(g,sp,false,false,x,y);}
    private void drawPetMarkers(Graphics g,int sp,boolean mega,boolean special,int x,int y){int max=familyMaxTier(sp),tier=Data.tier[sp],family=Data.fam[sp];for(int i=1;i<=max;i++){g.setColor(0x172030);g.fillRect(x,y-1,5,5);g.setColor(i<=tier?0x60DF70:0xFFE060);g.fillRect(x+1,y,3,3);x+=6;}if(mega||special||(family>=0&&family<FAMILY_SPECIAL.length&&FAMILY_SPECIAL[family])){g.setColor(0x172030);g.fillRect(x,y-1,5,5);g.setColor(mega||special?0x50D8FF:0xF05050);g.fillRect(x+1,y,3,3);}}
    private void drawFormationDots(Graphics g,int sp,int pos,int x,int y){int form=run.specialFormAt(pos);drawPetMarkers(g,sp,run.isMega(pos),form>=1&&form<=12,x,y);}

    private void drawEvolutionChain(Graphics g,int sp,int x,int y){
        int f=Data.fam[sp],max=familyMaxTier(sp),px=x;
        Art.textSmall(g,Lang.t("Tiến hóa:","Evolution:"),px,y+5,0x90A8C8);px+=Art.smallWidth(Lang.t("Tiến hóa:","Evolution:"))+4;
        for(int tier=1;tier<=max;tier++){
            int evo=f>=0&&f<Data.N&&tier<=3?FAMILY_TIER_SP[f*4+tier]:-1;
            if(evo<0)continue;
            g.setColor(evo==sp?0x405B72:0xD8DEDF);g.fillRect(px,y,22,22);Art.avatarDock(g,evo,px,y);
            g.setColor(evo==sp?0xFFE060:0x6E7988);g.drawRect(px,y,21,21);if(evo==sp)g.drawRect(px+1,y+1,19,19);
            px+=25;
        }
    }

    private void drawRollIconYellow(Graphics g,int x,int y){
        g.setColor(0xFFD030);g.drawArc(x+1,y+1,11,11,30,285);g.fillTriangle(x+10,y,x+14,y+3,x+9,y+5);
    }

    /** Original-like vertical circular sockets beside a formation Pokemon. */
    private void drawEquippedItems(Graphics g,int p,int x,int y,int size){
        int n=0;
        for(int s=0;s<3;s++)if(run.itemAt(p,s)>=0)n++;
        if(n==0)return;
        int px=x+size-9,py=y+(size-n*9)/2;
        for(int s=0;s<3;s++){
            int id=run.itemAt(p,s);
            if(id>=0){
                g.setColor(0x202735);g.fillArc(px,py,10,10,0,360);
                g.setColor(0x77849A);g.drawArc(px,py,9,9,0,360);
                Art.itemIconTiny(g,id,px+1,py+1);py+=9;
            }
        }
    }

    /** Permanent reserve + deployed-team dock below the option buttons. */
    private void paintPrepDock(Graphics g,int y,int h){
        int W=game.W,fh=Art.fh;
        g.setColor(0x111827);g.fillRect(0,y,W,h);
        int sy=y+25,trackW=W-24;
        if(dockMode==0){
            int total=ownedItemCount(),visible=8,groupW=Math.min(W-24,visible*25),step=Math.max(1,groupW/visible),diam=Math.min(24,step-1),itemsX=(W-step*visible)/2;
            if(dockItemCursor>=total)dockItemCursor=Math.max(0,total-1);
            if(dockItemCursor<dockItemScroll)dockItemScroll=dockItemCursor;
            if(dockItemCursor>=dockItemScroll+visible)dockItemScroll=dockItemCursor-visible+1;
            if(total<=visible)dockItemScroll=0;
            Art.textR(g,"<",Math.max(8,itemsX-3),y+5,total>visible&&dockItemScroll>0?0xFFFFFF:0x586478);Art.text(g,">",Math.min(W-8,itemsX+step*visible+3),y+5,total>visible&&dockItemScroll+visible<total?0xFFFFFF:0x586478);
            for(int i=0;i<visible;i++){
                int rank=dockItemScroll+i,id=ownedItemAt(rank),x=itemsX+i*step+(step-diam)/2,iy=y+(24-diam)/2;
                boolean focus=id>=0&&rank==dockItemCursor&&zone==4,picked=id>=0&&(id==pendingItemA||id==pendingItemB);int ring=focus?0xFFE060:(picked?0x50D8FF:0x718096);
                g.setColor(0x202735);g.fillArc(x,iy,diam,diam,0,360);
                if(id>=0){if(diam>=20)Art.itemIcon(g,id,x+(diam-24)/2,iy+(diam-24)/2);else Art.itemIconTiny(g,id,x+(diam-8)/2,iy+(diam-8)/2);if(diam>=20){int q=run.itemCount(id);String qs="x"+q;Art.textSmall(g,qs,x+2,iy+diam-10,0x101827);Art.textSmall(g,qs,x+1,iy+diam-11,ring);}}
                g.setColor(ring);g.drawArc(x,iy,diam-1,diam-1,0,360);if(focus||picked)g.drawArc(x+1,iy+1,diam-3,diam-3,0,360);
            }
            int trackX=itemsX,barW=step*visible;g.setColor(0x303B50);g.fillRect(trackX,sy,barW,2);if(total>0){int thumb=Math.max(8,barW*Math.min(visible,total)/total);int max=barW-thumb;int tx=trackX+(total<=visible?0:max*dockItemScroll/Math.max(1,total-visible));g.setColor(0x70A8FF);g.fillRect(tx,sy,thumb,2);}
        }else if(dockMode==2){
            int total=ownedConsumableCount(),visible=8,groupW=Math.min(W-24,visible*25),step=Math.max(1,groupW/visible),diam=Math.min(24,step-1),itemsX=(W-step*visible)/2;
            if(dockConsumableCursor>=total)dockConsumableCursor=Math.max(0,total-1);if(dockConsumableCursor<dockConsumableScroll)dockConsumableScroll=dockConsumableCursor;if(dockConsumableCursor>=dockConsumableScroll+visible)dockConsumableScroll=dockConsumableCursor-visible+1;if(total<=visible)dockConsumableScroll=0;
            Art.textR(g,"<",Math.max(8,itemsX-3),y+5,total>visible&&dockConsumableScroll>0?0xFFFFFF:0x586478);Art.text(g,">",Math.min(W-8,itemsX+step*visible+3),y+5,total>visible&&dockConsumableScroll+visible<total?0xFFFFFF:0x586478);
            for(int i=0;i<visible;i++){int rank=dockConsumableScroll+i,id=ownedConsumableAt(rank),x=itemsX+i*step+(step-diam)/2,iy=y+(24-diam)/2;boolean focus=id>=0&&rank==dockConsumableCursor&&zone==4,picked=id>=0&&id==pendingConsumable;int ring=focus?0xFFE060:(picked?0x50D8FF:0x718096);g.setColor(0x202735);g.fillArc(x,iy,diam,diam,0,360);if(id>=0){drawConsumableIcon(g,id,x+(diam-(diam>=20?24:8))/2,iy+(diam-(diam>=20?24:8))/2,diam<20);int qty=run.consumableCount(id),qtyCol=qty>=3?0xFFD840:(qty==2?0x70F0A0:0xFFFFFF);String qs="x"+qty;Art.textSmall(g,qs,x+2,iy+diam-10,0x080C14);Art.textSmall(g,qs,x+1,iy+diam-11,qtyCol);}g.setColor(ring);g.drawArc(x,iy,diam-1,diam-1,0,360);if(focus||picked)g.drawArc(x+1,iy+1,diam-3,diam-3,0,360);}
            int barW=step*visible;g.setColor(0x303B50);g.fillRect(itemsX,sy,barW,2);if(total>0){int thumb=Math.max(8,barW*Math.min(visible,total)/total);int max=barW-thumb;int tx=itemsX+(total<=visible?0:max*dockConsumableScroll/Math.max(1,total-visible));g.setColor(0x70A8FF);g.fillRect(tx,sy,thumb,2);}
        }else{
            int[] cnt=synCounts;fillSynCounts(cnt);int total=countPresentSyn(cnt),visible=Math.max(1,(W-24)/25);if(dockSynCursor>=total)dockSynCursor=Math.max(0,total-1);
            Art.text(g,"<",2,y+5,dockSynCursor>0?0xFFFFFF:0x586478);Art.textR(g,">",W-2,y+5,dockSynCursor+1<total?0xFFFFFF:0x586478);
            int first=Math.max(0,Math.min(dockSynCursor-visible+1,total-visible));
            for(int i=0;i<visible&&first+i<total;i++){int rank=first+i,t=presentSynTypeAt(rank,cnt),x=12+i*25,lv=Data.synLevel(t,cnt[t]);g.setColor(lv>0?Art.dark(Data.TCOL[t]):0x202735);g.fillArc(x,y,24,24,0,360);Art.typeIcon(g,t,x+4,y+4);g.setColor(rank==dockSynCursor&&zone==4?0xFFE060:(lv>0?Data.TCOL[t]:0x596578));g.drawArc(x,y,23,23,0,360);if(rank==dockSynCursor&&zone==4)g.drawArc(x+1,y+1,21,21,0,360);Art.textSmallR(g,""+cnt[t],x+23,y+14,lv>0?0xFFFFFF:0xA8B5C8);}
            g.setColor(0x303B50);g.fillRect(12,sy,trackW,2);if(zone==4&&total>0)paintDockSynHighlight(g,presentSynTypeAt(dockSynCursor,cnt));
        }
        int ty=y+29,n=boardUnitCount();
        if(dockTeamCursor>=n)dockTeamCursor=Math.max(0,n-1);
        int step=Math.max(20,Math.min(25,(W-8)/Math.max(1,n))),start=(W-step*n)/2;
        for(int i=0;i<n;i++){
            int p=boardPosAt(i),sp=run.get(p),x=start+i*step;
            g.setColor((i&1)==0?0xF2F4F3:0xD9DEDF);g.fillRect(x,ty,22,22);
            if(run.specialFormAt(p)!=0)SpecialFormAtlas.avatar(g,run.specialFormAt(p),x+1,ty+1,true,run.isShiny(p));else if(run.isMega(p))Art.avatarMiniMega(g,sp,x+1,ty+1,run.isShiny(p));else if(run.isShiny(p))Art.avatarMiniShiny(g,sp,x+1,ty+1);else Art.avatarDock(g,sp,x,ty);
            int equipped=0;for(int s=0;s<3;s++)if(run.itemAt(p,s)>=0)equipped++;if(equipped>0)Art.textSmall(g,"x"+equipped,x+1,ty+13,0xFFFFFF);
            boolean teamFocus=i==dockTeamCursor&&zone==5;g.setColor(teamFocus?0x40E8FF:0x596878);g.drawRect(x,ty,21,21);if(teamFocus){g.drawRect(x+1,ty+1,19,19);g.setColor(0xFFFFFF);g.fillRect(x,ty,3,3);g.fillRect(x+19,ty,3,3);}
        }
        if(n==0)Art.textSmallC(g,Lang.t("Đưa Pokemon lên sân để trang bị","Deploy Pokemon to equip"),W/2,ty+5,0x8090A8);
        if(pendingItemA>=0){int id=pendingMade>=0?pendingMade:pendingItemA;Art.itemIconTiny(g,id,W-11,ty+1);}if(pendingConsumable>=0)drawConsumableIcon(g,pendingConsumable,W-11,ty+1,true);
    }

    /** Restored legacy information area below the new reserve/team dock. */
    private void paintPrepInfo(Graphics g,int y,int h){
        int fh=Art.fh,avail=h/fh,sp=-1,infoPos=-1;boolean shop=false;
        if(h<=0)return;
        if(toastT>0){Art.textSmall(g,toast,3,y,0xFFB070);y+=fh;avail--;}
        if(zone<=1){infoPos=pos();sp=run.get(infoPos);}
        else if(zone==2){sp=run.shop[col];shop=true;}
        else if(zone==5){infoPos=boardPosAt(dockTeamCursor);if(infoPos>=0)sp=run.get(infoPos);}
        if(sp>=0&&avail>=2)drawInfo(g,sp,y,avail,shop,infoPos);
        else if(zone==4&&dockMode==0&&ownedItemCount()>0&&avail>0){int id=ownedItemAt(dockItemCursor);Art.textSmall(g,ItemData.name(id)+" x"+run.itemCount(id),3,y,0xD8E0F0);if(avail>1)Art.para(g,ItemData.desc(id),3,y+fh,game.W-6,0xB0D0FF,avail-1);}
        else if(zone==4&&dockMode==2&&ownedConsumableCount()>0&&avail>0){int id=ownedConsumableAt(dockConsumableCursor);Art.textSmall(g,ConsumableData.name(id)+" x"+run.consumableCount(id),3,y,0xFFD060);if(avail>1)Art.para(g,ConsumableData.desc(id),3,y+fh,game.W-6,0xD8E8FF,avail-1);}
        else if(zone==4&&dockMode==1&&avail>0){int[] cnt=synCounts;fillSynCounts(cnt);int t=presentSynTypeAt(dockSynCursor,cnt);if(t>=0){int lv=Data.synLevel(t,cnt[t]),stone=run.synergyStoneBonus[t],base=cnt[t]-stone;String mark=SynergyEffects.marks(t,cnt[t]);Art.textSmall(g,Lang.typeName(t)+"  "+base+(stone>0?" +"+stone:"")+"  "+mark,3,y,lv>0?0xFFD060:0xA8B5C8);if(avail>1)Art.para(g,Lang.synergyLongDesc(t),3,y+fh,game.W-6,lv>0?0xD8E8FF:0x8090A8,avail-1);}}
        else if(zone==3&&avail>0){String[] d={Lang.t("Mua 4 XP (4 vàng)","Buy 4 XP (4 gold)"),Lang.t("Đổi shop (2 vàng)","Reroll shop (2 gold)"),Lang.t("Khóa shop / Vật phẩm","Lock shop / Items"),Lang.t("Xem cộng hưởng (*)","Show synergies (*)"),Lang.t("Bắt đầu chiến đấu! (9)","Start the fight! (9)")};Art.textSmall(g,d[col],3,y,0xD8E0F0);}
    }

    private void paintItemActionMenu(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,w=Math.min(132,W-16),h=fh*4+8,x=(W-w)/2,y=(H-h)/2;
        Art.box(g,x,y,w,h,0x101830,0xFFD030);
        Art.textBC(g,ItemData.name(dockDetailItem),W/2,y+3,0xFFD060);
        String[] a={Lang.t("Trang bị","Equip"),Lang.t("Chi tiết","Details")};
        for(int i=0;i<2;i++){int ry=y+fh+5+i*(fh+2);if(i==itemAction){g.setColor(0x405273);g.fillRect(x+4,ry,w-8,fh+1);}Art.textC(g,a[i],W/2,ry,i==itemAction?0xFFFFFF:0xA8B5C8);}
    }

    private void paintMore(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,w=Math.min(182,W-18),h=Math.min(H-8,fh*10+25),x=(W-w)/2,y=(H-h)/2;
        Art.box(g,x,y,w,h,0x101830,0xFFD030);
        Art.textBC(g,"MORE",W/2,y+3,0xFFD030);
        String lock=run.shopLocked?Lang.t("Mở khóa shop","Unlock shop"):Lang.t("Khóa shop vòng sau","Lock next shop");
        String[] a={lock,Lang.t("Ghép đồ","Craft items"),Lang.t("Túi trang bị","Equipment bag"),Lang.t("Túi vật phẩm","Consumable bag"),Lang.t("Hệ cộng hưởng","Synergy dock"),Lang.t("Pool hệ đã chọn","Selected type pool"),Lang.t("Đá hệ đã mua","Bought stones"),Lang.t("Cửa hàng vật phẩm","Item shop")};
        int step=Math.max(fh+1,(h-fh-9)/8);for(int i=0;i<8;i++){int ry=y+fh+6+i*step;if(i==moreSel){g.setColor(0x405273);g.fillRect(x+5,ry,w-10,fh+1);}Art.textC(g,a[i],W/2,ry,i==moreSel?0xFFFFFF:0xA8B5C8);}
    }

    private void paintStoneStats(Graphics g){int W=game.W,H=game.H,fh=Art.fh;Art.box(g,4,4,W-8,H-8,0x101830,0xFFFFFF);Art.textBC(g,Lang.t("ĐÁ HỆ ĐÃ MUA","BOUGHT SYNERGY STONES"),W/2,8,0xFFD030);int count=boughtStoneTypeCount(),top=fh+13,rowH=22,footer=fh*3,rows=Math.max(1,(H-top-footer)/rowH),first=stoneStatsCursor-rows/2;if(first<0)first=0;if(first+rows>count)first=count-rows;if(first<0)first=0;for(int r=0;r<rows;r++){int rank=first+r;if(rank>=count)break;int t=boughtStoneTypeAt(rank),y=top+r*rowH;if(rank==stoneStatsCursor){g.setColor(0x405273);g.fillRect(7,y,W-14,rowH-1);g.setColor(0xFFFFFF);g.drawRect(7,y,W-15,rowH-2);}Art.itemIcon(g,SynergyStoneData.ICON[t],9,y-1);Art.textB(g,Lang.typeName(t),37,y+1,rank==stoneStatsCursor?0xFFFFFF:0xC8D8E8);Art.textR(g,"+"+run.synergyStoneBonus[t],W-10,y+4,0x80E8A0);}if(count==0)Art.textC(g,Lang.t("Chưa mua đá hệ nào","No synergy stones bought"),W/2,H/2,0x90A0B8);int total=0;for(int t=0;t<Data.NT;t++)total+=run.synergyStoneBonus[t];Art.textSmallC(g,Lang.t("Tổng điểm từ đá: ","Total stone points: ")+total,W/2,H-fh*2-4,0x80E8A0);Art.textSmallC(g,Lang.t("2/8: xem   FIRE/0: đóng","2/8: browse   FIRE/0: close"),W/2,H-fh-4,0x8090B0);}

    private void paintPoolInfo(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh;Art.box(g,3,3,W-6,H-6,0x101830,0xFFD030);Art.textBC(g,Lang.t("POOL HỆ ĐÃ CHỌN","SELECTED TYPE POOL"),W/2,7,0xFFD030);
        int typeY=fh+9,typeStep=fh+7;if(run.poolType>=0)for(int i=0;i<3;i++){int t=run.poolTypes[i],ry=typeY+i*typeStep;if(t<0)continue;Art.typeIcon(g,t,7,ry-2);String detail=Lang.typeName(t)+" - "+Lang.synergyDesc(t);while(detail.length()>4&&Art.smallWidth(detail)>W-32)detail=detail.substring(0,detail.length()-1);Art.textSmall(g,detail,28,ry+2,0xD8E8FF);}
        int listTitleY=typeY+3*typeStep+1;Art.textBC(g,Lang.t("DANH SÁCH POKÉMON","POKEMON LIST"),W/2,listTitleY,0xFFD060);
        int count=poolFamilyCount(),cols=5,cellW=(W-8)/cols,av=cellW>=36&&H>=220?32:20,cellH=av+7,gridY=listTitleY+fh+3,footer=fh*3+5,rows=Math.max(1,(H-gridY-footer)/cellH),visible=rows*cols;
        if(poolCursor<poolScroll)poolScroll=(poolCursor/cols)*cols;if(poolCursor>=poolScroll+visible)poolScroll=(poolCursor/cols-rows+1)*cols;if(poolScroll<0)poolScroll=0;
        for(int slot=0;slot<visible;slot++){int rank=poolScroll+slot;if(rank>=count)break;int sp=poolFamilyAt(rank),c=slot%cols,r=slot/cols,x=4+c*cellW,y=gridY+r*cellH;g.setColor(((r+c)&1)==0?0x202C3F:0x1B2638);g.fillRect(x,y,cellW-1,cellH-1);if(av==32)Art.avatar(g,sp,x+(cellW-32)/2,y+2);else Art.avatarMini(g,sp,x+(cellW-20)/2,y+2);if(rank==poolCursor){g.setColor(0xFFE060);g.drawRect(x,y,cellW-2,cellH-2);}}
        int selected=poolFamilyAt(poolCursor);String info=selected>=0?Data.name[selected]+"  x"+run.pool[selected]:Lang.t("Pool trống","Empty pool");Art.textC(g,info,W/2,H-fh*2-5,0xFFFFFF);Art.textSmallC(g,Lang.t("Mũi tên: xem   FIRE/0: đóng","Arrows: browse   FIRE/0: close"),W/2,H-fh-5,0x8090B0);
    }

    private void paintItemShop(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh;Art.box(g,3,3,W-6,H-6,0x101827,0xFFD030);Art.textBC(g,Lang.t("CỬA HÀNG VẬT PHẨM","ITEM SHOP"),W/2,5,0xFFD030);Art.textR(g,"$"+run.gold,W-8,5,0xFFE040);
        String[] tabs={Lang.t("TRÁI CÂY","FRUIT"),Lang.t("ĐÁ HỆ","STONES"),"GEN"};int tw=(W-8)/3;for(int i=0;i<3;i++){int x=4+i*tw;g.setColor(i==itemShopTab?0x405273:0x253147);g.fillRect(x,fh+6,tw-1,fh+5);Art.textSmallC(g,tabs[i],x+tw/2,fh+8,i==itemShopTab?0xFFFFFF:0x8996AA);}
        int count=itemShopCount(),rowH=28,top=fh*2+14,visible=Math.max(1,(H-top-fh*4)/rowH),first=itemShopSel-visible/2;if(first<0)first=0;if(first+visible>count)first=count-visible;if(first<0)first=0;
        for(int r=0;r<visible&&count>0;r++){int q=first+r;if(q>=count)break;int price=shopPriceAt(q),y=top+r*rowH;boolean sold=shopSoldAt(q);int st=shopTypeAt(q);String sub=itemShopTab==2?((q==0&&run.round<ShinyData.UNLOCK_ROUND)?Lang.t("Mở từ vòng 10","Unlocks at round 10"):((q>0&&run.round<15)?Lang.t("Mở từ vòng 15","Unlocks at round 15"):Lang.t("Vào Túi vật phẩm","Added to Consumable bag"))):(itemShopTab==1?Lang.t("Đã kích +","Active +")+run.synergyStoneBonus[st]:ConsumableData.group(run.fruitShop[q])+"  "+Lang.t("Kho x","Owned x")+run.consumableCount(run.fruitShop[q]));if(q==itemShopSel){g.setColor(0x405273);g.fillRect(7,y,W-14,rowH-1);g.setColor(0xFFE060);g.drawRect(7,y,W-15,rowH-2);}drawShopIcon(g,q,9,y+2,sold);Art.textB(g,shopNameAt(q),36,y+2,sold?0x687080:(q==itemShopSel?0xFFFFFF:0xC8D0E0));Art.textSmall(g,sub,36,y+fh+2,sold?0x687080:0x80A8C8);Art.textR(g,sold?Lang.t("ĐÃ MUA","SOLD"):"$"+price,W-10,y+7,sold?0x8090A0:0xFFE040);}
        if(count>0&&toastT<=0){String detail=shopDescAt(itemShopSel);if(itemShopTab==1){int nr=SynergyStoneData.nextPriceRound(run.round);detail=Lang.t("Giá hiện tại $","Current $")+SynergyStoneData.price(run.round)+Lang.t("; từ vòng ","; from round ")+nr+": $"+SynergyStoneData.price(nr)+". "+detail;}Art.para(g,detail,8,H-fh*4-5,W-16,0xAFC0D8,3);}else if(toastT>0)Art.textSmallC(g,toast,W/2,H-fh*4-5,0xFFB070);
        String help=itemShopTab==0?Lang.t("FIRE chọn  9 reset $1  trái/phải đổi tab","FIRE select  9 reset $1  left/right tabs"):(itemShopTab==1?Lang.t("FIRE chọn  trái/phải đổi tab  0 đóng","FIRE select  left/right tabs  0 close"):Lang.t("FIRE chọn  trái/phải đổi tab  0 đóng","FIRE select  left/right tabs  0 close"));Art.textSmallC(g,help,W/2,H-fh-5,0x8090B0);if(itemShopAction)paintFruitAction(g);if(itemShopDetail)paintFruitDetail(g);if(itemShopTargeting&&itemShopTab==2)paintShinyTarget(g);
    }

    private void drawShopIcon(Graphics g,int slot,int x,int y,boolean gray){if(itemShopTab==2&&slot==1){Art.megaStoneIcon(g,x,y,false);return;}int icon=shopIconAt(slot);if(gray)Art.itemIconGray(g,icon,x,y);else Art.itemIcon(g,icon,x,y);}
    private void drawConsumableIcon(Graphics g,int id,int x,int y,boolean tiny){if(id==ConsumableData.MEGA_STONE)Art.megaStoneIcon(g,x,y,tiny);else if(tiny)Art.itemIconTiny(g,ConsumableData.ICON[id],x,y);else Art.itemIcon(g,ConsumableData.ICON[id],x,y);}

    private void paintFruitAction(Graphics g){int W=game.W,H=game.H,fh=Art.fh,w=Math.min(156,W-18),h=fh*5+12,x=(W-w)/2,y=(H-h)/2;Art.box(g,x,y,w,h,0x101830,0xFFD030);Art.textBC(g,shopNameAt(itemShopSel),W/2,y+4,0xFFD060);String[] a={Lang.t("Mua - $","Buy - $")+shopPriceAt(itemShopSel),Lang.t("Xem chi tiết","View details"),Lang.t("Đóng","Close")};for(int i=0;i<3;i++){int ry=y+fh+7+i*(fh+2);if(i==itemShopActionSel){g.setColor(0x405273);g.fillRect(x+5,ry,w-10,fh+1);}Art.textC(g,a[i],W/2,ry,i==itemShopActionSel?0xFFFFFF:0xA8B5C8);}}

    private void paintFruitDetail(Graphics g){int W=game.W,H=game.H,fh=Art.fh,w=Math.min(226,W-16),h=Math.min(H-30,fh*10+18),x=(W-w)/2,y=(H-h)/2;Art.box(g,x,y,w,h,0x101830,0x70D8FF);drawShopIcon(g,itemShopSel,x+7,y+7,false);Art.textB(g,shopNameAt(itemShopSel),x+36,y+6,0xFFD060);Art.textSmall(g,"$"+shopPriceAt(itemShopSel),x+36,y+fh+7,0xA8C8E8);Art.para(g,shopDescAt(itemShopSel),x+8,y+fh*3,w-16,0xE0E8F0,6);Art.textSmallC(g,Lang.t("FIRE/0 đóng","FIRE/0 close"),W/2,y+h-fh-5,0x8090B0);}

    private void paintConsumableTarget(Graphics g){int W=game.W,H=game.H,fh=Art.fh,w=Math.min(W-14,230),h=fh*7+12,x=(W-w)/2,y=(H-h)/2;Art.box(g,x,y,w,h,0x101830,0x70D8FF);Art.textBC(g,Lang.t("DÙNG ","USE ")+ConsumableData.name(itemShopUseId),W/2,y+4,0xFFD060);int cols=8,cell=(w-12)/cols;for(int p=0;p<Run.BOARD+Run.BENCH;p++)if(run.get(p)>=0){int c=p%cols,r=p/cols,px=x+6+c*cell,py=y+fh+8+r*(fh+7);PetAvatar.slot(g,run,p,px,py,16);if(p==itemShopTarget){g.setColor(0xFFE060);g.drawRect(px-1,py-1,17,17);}}int sp=run.get(itemShopTarget);String s=sp>=0?Data.name[sp]+"  "+run.boostUses[itemShopTarget]+"/12":"";Art.textSmallC(g,s,W/2,y+h-fh*2,0xFFFFFF);Art.textSmallC(g,Lang.t("FIRE dùng   0 hủy","FIRE use   0 cancel"),W/2,y+h-fh-5,0x8090B0);}

    private void paintShinyTarget(Graphics g){int W=game.W,H=game.H,fh=Art.fh,w=Math.min(W-14,230),h=fh*8+12,x=(W-w)/2,y=(H-h)/2;Art.box(g,x,y,w,h,0x101830,0xD060FF);Art.textBC(g,Lang.t("CHỌN POKÉMON SHINY","CHOOSE SHINY POKEMON"),W/2,y+4,0xFFF060);int cols=8,cell=(w-12)/cols;for(int p=0;p<Run.BOARD+Run.BENCH;p++)if(run.get(p)>=0){int c=p%cols,r=p/cols,px=x+6+c*cell,py=y+fh+8+r*(fh+7);PetAvatar.slot(g,run,p,px,py,16);if(run.isShiny(p)){g.setColor(0xD060FF);g.fillRect(px+12,py,3,3);}if(p==itemShopTarget){g.setColor(0xFFFFFF);g.drawRect(px-1,py-1,17,17);}}int sp=run.get(itemShopTarget);if(sp>=0){Art.textSmallC(g,(run.isShiny(itemShopTarget)?"* ":"")+Data.name[sp],W/2,y+h-fh*3,run.isShiny(itemShopTarget)?0xD060FF:0xFFFFFF);Art.textSmallC(g,ShinyData.roleName(sp),W/2,y+h-fh*2,0xA8D8FF);}Art.textSmallC(g,Lang.t("FIRE xác nhận   0 hủy","FIRE confirm   0 cancel"),W/2,y+h-fh-5,0x8090B0);}

    private void paintConsumableBag(Graphics g){int W=game.W,H=game.H,fh=Art.fh;Art.box(g,4,4,W-8,H-8,0x101830,0x70D8FF);Art.textBC(g,Lang.t("TÚI VẬT PHẨM","CONSUMABLE BAG"),W/2,8,0xFFD030);int top=fh+12,rowH=28,footer=fh*5,rows=Math.max(1,(H-top-footer)/rowH),total=ownedConsumableCount(),rank=0;for(int id=0,n=0;id<ConsumableData.COUNT;id++)if(run.consumableCount(id)>0){if(id==consumableSel)rank=n;n++;}int first=rank>=rows?rank-rows+1:0,seen=0,shown=0;for(int id=0;id<ConsumableData.COUNT&&shown<rows;id++)if(run.consumableCount(id)>0){if(seen++<first)continue;int y=top+shown*rowH;if(id==consumableSel){g.setColor(0x405273);g.fillRect(7,y,W-14,rowH-1);g.setColor(0xFFE060);g.drawRect(7,y,W-15,rowH-2);}drawConsumableIcon(g,id,9,y+2,false);Art.textB(g,ConsumableData.name(id),36,y+2,id==consumableSel?0xFFFFFF:0xC8D0E0);Art.textSmall(g,"x"+run.consumableCount(id)+"  "+ConsumableData.group(id),36,y+fh+2,0x80A8C8);shown++;}if(total==0)Art.textC(g,Lang.t("Túi vật phẩm trống","Consumable bag is empty"),W/2,H/2,0x9098A8);else Art.para(g,ConsumableData.desc(consumableSel),8,H-footer+3,W-16,0xB8C8D8,3);Art.textSmallC(g,Lang.t("FIRE dùng lên Pokémon   #/0 đóng","FIRE use on Pokemon   #/0 close"),W/2,H-fh-6,0x8090B0);if(consumableTargeting){itemShopUseId=consumableSel;itemShopTarget=consumableTarget;paintConsumableTarget(g);}}

    private void paintPetActionMenu(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,sp=dockPetPos>=0?run.get(dockPetPos):-1,w=Math.min(132,W-16),h=fh*4+8,x=(W-w)/2,y=(H-h)/2;
        Art.box(g,x,y,w,h,0x101830,0xFFD030);
        Art.textBC(g,sp>=0?Data.name[sp]:"Pokemon",W/2,y+3,0xFFD060);
        String[] a={Lang.t("Chi tiết","Details"),Lang.t("Mặc vào","Equip")};
        for(int i=0;i<2;i++){int ry=y+fh+5+i*(fh+2);if(i==petAction){g.setColor(0x405273);g.fillRect(x+4,ry,w-8,fh+1);}int tc=i==1&&pendingItemA<0?0x626C7C:(i==petAction?0xFFFFFF:0xA8B5C8);Art.textC(g,a[i],W/2,ry,tc);}
    }

    private void paintCraft(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh;
        Art.box(g,2,2,W-4,H-4,0x101827,0xFFD030);
        String[] tabs={Lang.t("GHÉP ĐỒ","CRAFT"),Lang.t("CÔNG THỨC","RECIPES")};
        for(int i=0;i<2;i++){int x=i*W/2,w=i==1?W-x:W/2;g.setColor(i==craftTab?0x405273:0x27344A);g.fillRect(x+3,4,w-6,fh+5);if(i==craftTab){g.setColor(0xFFE060);g.fillRect(x+3,fh+7,w-6,2);if(craftFocus<0)g.drawRect(x+3,4,w-7,fh+4);}Art.textC(g,tabs[i],x+w/2,6,i==craftTab?0xFFFFFF:0xA8B5C8);}
        if(craftTab==0)paintCraftBuild(g,fh+13,H-fh-18);else paintRecipeGuide(g,fh+13,H-fh-18);
        Art.textSmallC(g,Lang.t("2/8: hàng  4/6: chọn  5: dùng  #: chi tiết","Up/down: row  Left/right: select  5: use  #: details"),W/2,H-fh-4,0x8090B0);
    }

    private void paintCraftBuild(Graphics g,int y,int h){
        int W=game.W,H=game.H,fh=Art.fh,out=craftA>=0&&craftB>=0?ItemData.crafted(craftA,craftB):-1;
        int sx=W/2-60;drawCraftSlot(g,craftA,sx,y);Art.textSmall(g,"+",sx+28,y+7,0xFFFFFF);drawCraftSlot(g,craftB,sx+43,y);Art.textSmall(g,"=",sx+72,y+7,0xFFFFFF);drawCraftSlot(g,out,sx+87,y);
        boolean compact=H<220;int rowY=y+(compact?28:28),visible=Math.max(1,(W-(compact?74:20))/28);
        for(int row=0;row<2;row++){boolean parts=row==0;int count=craftOwnedCount(parts),cursor=parts?craftItemCursor:craftFinishedCursor;cursor=Math.min(cursor,Math.max(0,count-1));if(parts)craftItemCursor=cursor;else craftFinishedCursor=cursor;int start=Math.max(0,cursor-visible+1),iy=rowY+row*(compact?28:fh+30);Art.textSmall(g,parts?Lang.t(compact?"Mảnh":"MẢNH GHÉP","Parts"):Lang.t(compact?"Đồ":"ĐỒ HOÀN CHỈNH","Items"),10,iy,parts?0x90C8FF:0xFFD060);if(!compact)iy+=fh+2;for(int i=0;i<visible;i++){int rank=start+i,id=craftOwnedAt(parts,rank);if(id<0)break;int x=(compact?64:10)+i*28;drawCraftSlot(g,id,x,iy);Art.textSmall(g,"x"+run.itemCount(id),x+1,iy+13,0x70E8FF);if(craftFocus==row&&rank==cursor){g.setColor(0xFFFFFF);g.drawRect(x-1,iy-1,25,25);}}}
        int by=H-fh*2-13,iy=rowY+2*(compact?28:fh+30),id=craftFocus==0?craftOwnedAt(true,craftItemCursor):craftFocus==1?craftOwnedAt(false,craftFinishedCursor):out;
        if(by>iy+fh){Art.box(g,8,iy,W-16,by-iy-4,0x18263C,0x507DA3);if(id>=0){Art.textSmall(g,ItemData.name(id),12,iy+3,0xFFD060);Art.para(g,ItemData.desc(id),12,iy+fh+6,W-24,0xB0D0FF,Math.max(1,(by-iy-fh-12)/fh));}}
        String[] labels={Lang.t("GHÉP","CRAFT"),Lang.t("LÀM MỚI","RESET"),Lang.t("ĐÓNG","CLOSE")};for(int i=0;i<3;i++){int x=8+i*(W-16)/3,w=(W-16)/3-3;Art.box(g,x,by,w,fh+5,craftFocus==2&&craftButton==i?0x305090:0x27344A,craftFocus==2&&craftButton==i?0xFFFFFF:0x507DA3);Art.textSmallC(g,labels[i],x+w/2,by+2,0xFFFFFF);}
    }

    private boolean hasCraftMaterials(int a,int b){return a>=0&&b>=0&&run.itemCount(a)>=(a==b?2:1)&&run.itemCount(b)>=(a==b?2:1);}

    private void drawCraftSlot(Graphics g,int id,int x,int y){
        g.setColor(0x202735);g.fillArc(x,y,24,24,0,360);g.setColor(id>=0?0x77849A:0x465064);g.drawArc(x,y,23,23,0,360);if(id>=0)Art.itemIcon(g,id,x,y);
    }

    private void paintRecipeGuide(Graphics g,int y,int h){
        int W=game.W,fh=Art.fh,n=craftCatalogCount();if(n<=0)return;
        int cols=Math.max(1,(W-16)/28),rows=Math.max(1,(h-fh*3)/28),visible=cols*rows;
        if(craftRecipeCursor>=n)craftRecipeCursor=n-1;if(craftRecipeCursor<0)craftRecipeCursor=0;
        int cursorRow=craftRecipeCursor/cols;
        if(cursorRow<craftRecipeScroll)craftRecipeScroll=cursorRow;
        if(cursorRow>=craftRecipeScroll+rows)craftRecipeScroll=cursorRow-rows+1;
        int maxScroll=Math.max(0,(n+cols-1)/cols-rows);if(craftRecipeScroll>maxScroll)craftRecipeScroll=maxScroll;
        int first=craftRecipeScroll*cols;
        for(int i=0;i<visible;i++){
            int rank=first+i;if(rank>=n)break;int id=craftCatalogAt(rank),c=i%cols,r=i/cols,x=8+c*28,iy=y+r*28;
            g.setColor(ItemData.isComponent(id)?0x33445D:0x202735);g.fillArc(x,iy,24,24,0,360);Art.itemIcon(g,id,x,iy);
            g.setColor(rank==craftRecipeCursor&&craftFocus==0?0xFFE060:(ItemData.isComponent(id)?0x70B8FF:0x657086));g.drawArc(x,iy,23,23,0,360);
            if(rank==craftRecipeCursor&&craftFocus==0)g.drawArc(x+1,iy+1,21,21,0,360);
        }
        int id=craftCatalogAt(craftRecipeCursor),fy=y+rows*28+1;
        Art.textSmallC(g,ItemData.name(id),W/2,fy,ItemData.isComponent(id)?0x90C8FF:0xFFD060);
        Art.textSmallC(g,(craftRecipeCursor+1)+"/"+n+Lang.t("  FIRE: chi tiết","  FIRE: details"),W/2,fy+fh,0x90A8C0);
    }

    private void circleItem(Graphics g,int id,int x,int y,boolean selected){g.setColor(0x202735);g.fillArc(x-2,y-2,36,36,0,360);if(id>=0)Art.itemIconLarge(g,id,x,y);g.setColor(selected?0xFFFFFF:0x657086);g.drawArc(x-2,y-2,35,35,0,360);}
    private void petInfoBox(Graphics g,String title,String text,int y){int W=game.W,H=game.H,fh=Art.fh,bottom=H-fh-16,h=Math.max(1,bottom-y);Art.box(g,8,y,W-16,h,0x18263C,0x507DA3);if(h<fh+6)return;drawMarqueeBold(g,title,12,y+4,W-24,0xFFD060);int top=y+fh+8;if(bottom<=top)return;g.setClip(12,top,W-24,bottom-top-2);String[] lines=Art.wrap(text,W-24,150);int max=Math.max(0,lines.length*fh-(bottom-top-2));if(dockPetDetailScroll>max)dockPetDetailScroll=max;for(int i=0;i<lines.length;i++)Art.textSmall(g,lines[i],12,top+i*fh-dockPetDetailScroll,0xB0D0FF);g.setClip(0,0,W,H);}
    private void inventoryScroll(Graphics g,int y,int start,int visible,int total,int base,int size){g.setColor(start>0?0x70D8FF:0x46536A);g.fillTriangle(2,y+size/2,8,y+size/2-5,8,y+size/2+5);g.setColor(start+visible<total?0x70D8FF:0x46536A);g.fillTriangle(game.W-2,y+size/2,game.W-8,y+size/2-5,game.W-8,y+size/2+5);int track=Math.max(1,game.W-base-12),thumb=total<=visible?track:Math.max(6,track*visible/total),max=Math.max(1,total-visible),offset=Math.min(start,max)*(track-thumb)/max;g.setColor(0x35465E);g.fillRect(base,y+size+2,track,2);g.setColor(total>visible?0x70D8FF:0x657086);g.fillRect(base+offset,y+size+2,thumb,2);}
    private void paintPetEquipment(Graphics g,int p,int y){int W=game.W,fh=Art.fh;boolean compact=game.H<220;int rowH=compact?38:fh+42;
        for(int row=0;row<2;row++){int iy=y+row*rowH,base=compact?70:12;if(compact)Art.textSmall(g,row==0?Lang.t("Đang đeo","Equipped"):Lang.t("Trong túi","Inventory"),10,iy+7,0x90C8FF);else{Art.textSmall(g,row==0?Lang.t("Đang đeo · 5: tháo","Equipped · 5: remove"):Lang.t("Trong túi · 5: đeo","Inventory · 5: equip"),10,iy,0x90C8FF);iy+=fh+4;}int visible=Math.max(1,(W-base-12)/38),n=row==0?3:ownedItemCount(),cursor=petEquipRow==row?petEquipCursor:0,start=Math.max(0,cursor-visible+1);for(int i=0;i<visible&&start+i<n;i++){int rank=start+i,id=row==0?run.itemAt(p,rank):ownedItemAt(rank),x=base+i*38;circleItem(g,id,x,iy,petEquipRow==row&&rank==petEquipCursor);if(row==1)Art.textSmall(g,"x"+run.itemCount(id),x+1,iy+22,0x70E8FF);}if(row==1)inventoryScroll(g,iy,start,visible,n,base,34);}
        int id=petEquipRow==0?run.itemAt(p,Math.min(2,petEquipCursor)):petEquipRow==1?ownedItemAt(petEquipCursor):-1;petInfoBox(g,id>=0?ItemData.name(id):Lang.t("Trang bị","Equipment"),id>=0?ItemData.desc(id):Lang.t("Di chuyển xuống để chọn slot hoặc đồ trong túi.","Move down to select a slot or inventory item."),y+2*rowH+4);
    }
    private void paintPetFruit(Graphics g,int p,int y){int W=game.W,fh=Art.fh,step=(W-24)/6,rowH=29;
        Art.textSmall(g,Lang.t("Đã ăn","Consumed")+" "+eatenFruitCount()+"/12",10,y,0x70D8FF);int top=y+fh+3;
        for(int row=0;row<3;row++){int iy=top+row*rowH;if(row==2){Art.textSmall(g,Lang.t("Trong túi · 5: dùng","Inventory · 5: use"),10,iy,0x70D8FF);iy+=fh+3;}int first=row==2?(fruitBagCursor/6)*6:row*6;for(int col=0;col<6;col++){int rank=first+col,id=row==2?ownedConsumableAt(rank):eatenFruitAt(rank),x=12+col*step+(step-24)/2;boolean selected=petEquipRow==row&&(row==2?rank==fruitBagCursor:rank==petEquipCursor);if(id>=0)drawConsumableIcon(g,id,x,iy,false);g.setColor(selected?0xFFFFFF:0x657086);g.drawArc(x-2,iy-2,27,27,0,360);if(row==2&&id>=0)Art.textSmall(g,"x"+run.consumableCount(id),x,iy+14,0x70E8FF);}if(row==2)inventoryScroll(g,iy,first,6,ownedConsumableCount(),12,26);}
        int id=petEquipRow==2?ownedConsumableAt(fruitBagCursor):petEquipRow>=0?eatenFruitAt(petEquipCursor):-1;petInfoBox(g,id>=0?ConsumableData.name(id):Lang.t("Thông tin trái","Fruit information"),id>=0?ConsumableData.desc(id):Lang.t("Ô trống. Chọn trái trong túi để dùng.","Empty slot. Select an inventory fruit to use."),top+3*rowH+fh+5);
    }
    private void paintPetForms(Graphics g,int p,int x,int y,int width){int sp=run.get(p),cols=Math.max(1,width/25),n=0,f=Data.fam[sp];Art.textSmall(g,Lang.t("Các dạng","Forms"),x,y,0x70D8FF);y+=Art.fh+3;
        for(int rank=0;f>=0&&f<Data.N&&rank<FAMILY_MEMBERS[f].length;rank++){int s=FAMILY_MEMBERS[f][rank];if(s<0)continue;int ax=x+(n%cols)*25,ay=y+(n/cols)*25;Art.avatarMini(g,s,ax,ay);if(s==sp&&!run.isMega(p)&&run.specialFormAt(p)==0){g.setColor(0xFFFFFF);g.drawRect(ax-1,ay-1,21,21);}n++;
            int form=EvolutionVariantData.formFor(s);if(form>0){ax=x+(n%cols)*25;ay=y+(n/cols)*25;SpecialFormAtlas.avatar(g,form,ax,ay,true,false);if(form==run.specialFormAt(p)){g.setColor(0xFFFFFF);g.drawRect(ax-1,ay-1,21,21);}n++;}
            if(MegaData.available(s)){ax=x+(n%cols)*25;ay=y+(n/cols)*25;Art.avatarMiniMega(g,s,ax,ay,false);if(s==sp&&run.isMega(p)){g.setColor(0xFFFFFF);g.drawRect(ax-1,ay-1,21,21);}n++;}}
        int form=SpecialFormData.memoryForm(sp);if(Data.nationalDex(sp)==201&&run.specialFormAt(p)>=47)form=run.specialFormAt(p);int count=SpecialFormData.zygarde(sp)?3:form==SpecialFormData.DEOXYS_ATTACK||form==76?3:form>0&&!SpecialFormData.evolutionVariant(form)?1:0;for(int i=0;i<count;i++){int id=SpecialFormData.zygarde(sp)?(i==0?SpecialFormData.ZYGARDE_10:i==1?SpecialFormData.ZYGARDE_50:SpecialFormData.ZYGARDE_100):form+i,ax=x+(n%cols)*25,ay=y+(n/cols)*25;SpecialFormAtlas.avatar(g,id,ax,ay,true,false);if(id==run.specialFormAt(p)){g.setColor(0xFFFFFF);g.drawRect(ax-1,ay-1,21,21);}n++;}
        petFormsBottom=y+((n+cols-1)/cols)*25;
    }
    private void paintDockPetDetail(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,p=dockPetPos,sp=p>=0?run.get(p):-1;if(sp<0){dockPetDetail=false;return;}Art.box(g,4,4,W-8,H-8,0x101830,0xFFD030);
        if(run.specialFormAt(p)!=0)SpecialFormAtlas.avatar(g,run.specialFormAt(p),8,9,false,run.isShiny(p));else if(run.isMega(p))Art.avatarMega(g,sp,8,9,run.isShiny(p));else if(run.isShiny(p))Art.avatarShiny(g,sp,8,9);else Art.avatar(g,sp,8,9);drawMarqueeBold(g,run.nameAt(p)+(run.isShiny(p)?Lang.t(" [Tỏa sáng]"," [Shiny]"):"")+" +"+Data.sellValue(sp)+"$",45,9,W-53,0xFFFFFF);Art.typeIcon(g,run.type1At(p),45,9+fh);if(run.type2At(p)>=0)Art.typeIcon(g,run.type2At(p),61,9+fh);
        int tabY=43;String[] tabs={Lang.t("Chi tiết","Details"),Lang.t("Trang bị","Equipment"),Lang.t("Trái đã ăn","Fruit")};for(int i=0;i<3;i++){int x=6+i*(W-12)/3,w=(W-12)/3;Art.box(g,x,tabY,w,fh+5,i==petDetailTab?0x305090:0x27344A,petEquipRow<0&&i==petDetailTab?0xFFFFFF:0x27344A);Art.textSmallC(g,tabs[i],x+w/2,tabY+2,i==petDetailTab?0xFFFFFF:0x90A8C8);}
        int y=tabY+fh+10;if(petDetailTab==1)paintPetEquipment(g,p,y);else if(petDetailTab==2)paintPetFruit(g,p,y);else{run.previewUnit(p,previewStats);int split=Math.max(105,W/2);g.setClip(8,y,split-16,fh*3+3);Art.textSmall(g,Lang.t("Máu: ","HP: ")+previewStats.maxHp+Lang.t(" Công: "," ATK: ")+previewStats.atk,8,y,0xD8E0F0);Art.textSmall(g,Lang.t("Thủ: ","DEF: ")+previewStats.def+Lang.t(" Kháng: "," RES: ")+previewStats.speDef,8,y+fh,0xD8E0F0);Art.textSmall(g,Lang.t("Tốc: ","SPD: ")+previewStats.speed+Lang.t(" Tầm: "," RNG: ")+previewStats.range,8,y+fh*2,0xD8E0F0);g.setClip(0,0,W,H);paintPetForms(g,p,split,y,W-split-12);int bottom=Math.max(y+fh*3+4,petFormsBottom);String text=Lang.t("Năng lượng: ","Energy: ")+previewStats.mana+"/"+previewStats.maxMana+"  AP: "+previewStats.skillBonus+"\n"+AbilityBehavior.description(sp);if(run.isShiny(p))text+="\n"+ShinyData.desc();if(run.isMega(p))text+="\n"+MegaData.detail(sp);text+="\n"+Lang.t("Chỉ số trước trận, chưa cộng hưởng đội hình.","Preparation stats, before team synergies.");petInfoBox(g,Lang.moveName(Data.skillName[sp]),text,bottom+3);}
        Art.textSmallC(g,Lang.t("2/8: hàng  4/6: chọn  7/9: cuộn  #: đóng","Arrows: select  7/9: scroll  #: close"),W/2,H-fh-6,0x8090B0);
    }

    private void paintDockDetail(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,id=dockDetailItem;
        Art.box(g,4,4,W-8,H-8,0x101830,0xFFD030);
        Art.itemIconLarge(g,id,9,9);Art.textB(g,ItemData.name(id),47,9,0xFFD060);
        Art.textSmall(g,ItemData.kind(id)+"  x"+run.itemCount(id),47,9+fh,0x91A8C8);
        int y=47,recipeY=H-fh*3-72,lines=Math.max(2,(recipeY-y-2)/fh);
        Art.para(g,ItemData.desc(id),8,y,W-16,0xE0E8F0,lines);
        paintItemRecipeHint(g,id,recipeY);
        Art.textSmallC(g,Lang.t("FIRE / 0: đóng","FIRE / 0: close"),W/2,H-fh-6,0xA8B8D0);
    }

    private void paintItemRecipeHint(Graphics g,int id,int y){
        int W=game.W,a=ItemData.RECIPE_A[id],b=ItemData.RECIPE_B[id],n=ItemData.outputCount(id);
        if(a>=0&&b>=0){
            Art.textSmall(g,Lang.t("Ghép từ:","Crafted from:"),8,y,0xFFD060);y+=11;
            Art.itemIcon(g,a,8,y);Art.text(g,"+",34,y+7,0xFFFFFF);Art.itemIcon(g,b,46,y);
            Art.textSmall(g,ItemData.name(a)+" + "+ItemData.name(b),74,y+6,0xB8C8D8);
        }else if(n>0){
            int max=Math.min(n,(W-16)/28);
            Art.textSmall(g,Lang.t("Ghép với:","Combine with:")+" "+n,8,y,0x90C8FF);y+=11;
            for(int i=0;i<max;i++){int out=ItemData.outputAt(id,i),partner=ItemData.RECIPE_A[out]==id?ItemData.RECIPE_B[out]:ItemData.RECIPE_A[out];Art.itemIcon(g,partner,8+i*28,y);}y+=28;
            Art.textSmall(g,Lang.t("Thành:","Creates:"),8,y,0xFFD060);y+=11;
            for(int i=0;i<max;i++){int out=ItemData.outputAt(id,i);Art.itemIcon(g,out,8+i*28,y);}
        }else Art.textSmall(g,Lang.t("Không có công thức ghép","No crafting recipe"),8,y,0x77849A);
    }

    private void drawInfo(Graphics g, int sp, int y, int lines, boolean shop, int p) {
        int W = game.W, fh = Art.fh;
        int contentX=39;
        if(!shop&&p>=0&&run.specialFormAt(p)!=0)SpecialFormAtlas.avatar(g,run.specialFormAt(p),3,y+1,false,run.isShiny(p));else if(!shop&&p>=0&&run.isMega(p))Art.avatarMega(g,sp,3,y+1,run.isShiny(p));else if(!shop&&p>=0&&run.isShiny(p))Art.avatarShiny(g,sp,3,y+1);else Art.avatar(g,sp,3,y+1);
        String displayName=(shop&&SpecialFormData.zygarde(sp)?SpecialFormData.name(SpecialFormData.ZYGARDE_10):(!shop&&p>=0&&run.isMega(p)?MegaData.formName(sp):(!shop&&p>=0?run.nameAt(p):Data.name[sp])))+(!shop&&p>=0&&run.isShiny(p)?Lang.t(" [Tỏa sáng]"," [Shiny]"):"");
        Art.textB(g, displayName, contentX, y+1, 0xFFFFFF);
        if (shop) {
            String price="$"+Run.shopCost(sp);
            Art.textB(g,price,W-3-Art.boldWidth(price),y+1,0xFFD030);
        }
        y+=fh+3;int x=contentX;Art.textSmall(g,Lang.t("Hệ:","Type:"),x,y+3,0x90A8C8);x+=Art.smallWidth(Lang.t("Hệ:","Type:"))+3;
        Art.typeIcon(g,(!shop&&p>=0?run.type1At(p):Data.t1[sp]),x,y);x+=18;if((!shop&&p>=0?run.type2At(p):Data.t2[sp])>=0)Art.typeIcon(g,(!shop&&p>=0?run.type2At(p):Data.t2[sp]),x,y);
        drawEvolutionTop(g,sp,W-3,y);
        y += 20;
        lines--;
        if (lines <= 0) return;
        int hp=Data.hp[sp],atk=Data.atk[sp],def=Data.def[sp],range=Data.range[sp];if(!shop&&p>=0){run.previewUnit(p,previewStats);hp=previewStats.maxHp;atk=previewStats.atk;def=previewStats.def;range=previewStats.range;}
        Art.textSmall(g,"HP: "+hp+Lang.t(" Công: "," ATK: ")+atk+Lang.t(" Thủ: "," DEF: ")+def+Lang.t(" Tầm: "," RNG: ")+range,contentX,y,!shop&&p>=0&&run.boostUses[p]>0?0x80E8A0:0xB0D0FF);
        y += fh+2;
        lines--;
        if (lines <= 0) return;
        // Shiny is intentionally kept on the name row; full details live in the detail panel.
    }

    /** Same hover information as portrait mode, constrained to a landscape side column. */
    private void drawInfoAt(Graphics g,int sp,int x0,int y,int width,int lines,boolean shop,int p){
        int fh=Art.fh,contentX=x0+36;
        if(!shop&&p>=0&&run.specialFormAt(p)!=0)SpecialFormAtlas.avatar(g,run.specialFormAt(p),x0,y+1,false,run.isShiny(p));else if(!shop&&p>=0&&run.isMega(p))Art.avatarMega(g,sp,x0,y+1,run.isShiny(p));else if(!shop&&p>=0&&run.isShiny(p))Art.avatarShiny(g,sp,x0,y+1);else Art.avatar(g,sp,x0,y+1);
        String displayName=(shop&&SpecialFormData.zygarde(sp)?SpecialFormData.name(SpecialFormData.ZYGARDE_10):(!shop&&p>=0&&run.isMega(p)?MegaData.formName(sp):(!shop&&p>=0?run.nameAt(p):Data.name[sp])))+(!shop&&p>=0&&run.isShiny(p)?Lang.t(" [Tỏa sáng]"," [Shiny]"):"");
        drawMarqueeBold(g,displayName,contentX,y+1,Math.max(20,width-39),0xFFFFFF);drawEvolutionIconsFrom(g,sp,contentX,y+fh+3);y+=fh+22;lines--;if(lines<=0)return;
        int hp=Data.hp[sp],atk=Data.atk[sp],def=Data.def[sp],sdef=Data.speDef[sp],range=Data.range[sp];if(!shop&&p>=0){run.previewUnit(p,previewStats);hp=previewStats.maxHp;atk=previewStats.atk;def=previewStats.def;range=previewStats.range;sdef=previewStats.speDef;}
        int statCol=!shop&&p>=0&&run.boostUses[p]>0?0x80E8A0:0xB0D0FF,ch=Art.compactHeight(),statX=x0+18;Art.typeIcon(g,(!shop&&p>=0?run.type1At(p):Data.t1[sp]),x0,y);if((!shop&&p>=0?run.type2At(p):Data.t2[sp])>=0){Art.typeIcon(g,(!shop&&p>=0?run.type2At(p):Data.t2[sp]),x0+18,y);statX=x0+36;}Art.textCompact(g,Lang.t("Máu: ","HP: ")+hp+Lang.t("  Công: ","  ATK: ")+atk,statX,y+4,statCol);y+=18;if(--lines<=0)return;Art.textCompact(g,Lang.t("Thủ: ","DEF: ")+def+Lang.t(" Kháng: "," SDEF: ")+sdef+Lang.t(" Tầm: "," RNG: ")+range,x0,y,0xB0D0FF);y+=ch+3;if(--lines<=0||shop||p<0)return;
        for(int s=0;s<3;s++){int ix=x0+s*29,id=run.itemAt(p,s);g.setColor(0x202735);g.fillArc(ix,y,24,24,0,360);if(id>=0)Art.itemIcon(g,id,ix,y);g.setColor(id>=0?0x80D8FF:0x596878);g.drawArc(ix,y,23,23,0,360);}
    }

    private void drawEvolutionIcons(Graphics g,int sp,int right,int y){int f=Data.fam[sp],max=familyMaxTier(sp),count=0;for(int tier=1;tier<=max;tier++)if(f>=0&&f<Data.N&&tier<=3&&FAMILY_TIER_SP[f*4+tier]>=0)count++;int px=right-count*18;for(int tier=1;tier<=max;tier++){int evo=f>=0&&f<Data.N&&tier<=3?FAMILY_TIER_SP[f*4+tier]:-1;if(evo<0)continue;Art.avatarTiny(g,evo,px,y);g.setColor(evo==sp?0xFFE060:0x657086);g.drawRect(px,y,15,15);px+=18;}}
    private void drawEvolutionIconsFrom(Graphics g,int sp,int x,int y){int f=Data.fam[sp],max=familyMaxTier(sp),px=x;for(int tier=1;tier<=max;tier++){int evo=f>=0&&f<Data.N&&tier<=3?FAMILY_TIER_SP[f*4+tier]:-1;if(evo<0)continue;Art.avatarTiny(g,evo,px,y);g.setColor(evo==sp?0xFFE060:0x657086);g.drawRect(px,y,15,15);px+=18;}}

    private void drawMarqueeBold(Graphics g,String text,int x,int y,int w,int col){int tw=Art.boldWidth(text);if(tw<=w){Art.textB(g,text,x,y,col);return;}int ox=g.getClipX(),oy=g.getClipY(),ow=g.getClipWidth(),oh=g.getClipHeight(),gap=18,span=tw+gap,shift=(visualTime/45)%span;g.setClip(x,y,w,Art.fh+3);Art.textB(g,text,x-shift,y,col);Art.textB(g,text,x-shift+span,y,col);g.setClip(ox,oy,ow,oh);}

    private void drawEvolutionTop(Graphics g,int sp,int right,int y){
        int f=Data.fam[sp],max=familyMaxTier(sp),count=0;for(int tier=1;tier<=max;tier++)if(f>=0&&f<Data.N&&tier<=3&&FAMILY_TIER_SP[f*4+tier]>=0)count++;
        int px=right-count*18,labelX=px-Art.smallWidth(Lang.t("Tiến hóa:","Evo:"))-3;Art.textSmall(g,Lang.t("Tiến hóa:","Evo:"),labelX,y+3,0x90A8C8);
        for(int tier=1;tier<=max;tier++){int evo=f>=0&&f<Data.N&&tier<=3?FAMILY_TIER_SP[f*4+tier]:-1;if(evo<0)continue;Art.avatarTiny(g,evo,px,y);g.setColor(evo==sp?0xFFE060:0x657086);g.drawRect(px,y,15,15);px+=18;}
    }

    private void drawEvolutionMini(Graphics g,int sp,int x,int y){
        int f=Data.fam[sp],max=familyMaxTier(sp),px=x;
        Art.textSmall(g,Lang.t("Tiến hóa:","Evolution:"),px,y+5,0x90A8C8);px+=Art.smallWidth(Lang.t("Tiến hóa:","Evolution:"))+4;
        for(int tier=1;tier<=max;tier++){int evo=f>=0&&f<Data.N&&tier<=3?FAMILY_TIER_SP[f*4+tier]:-1;if(evo<0)continue;g.setColor(evo==sp?0x405B72:0xD9DEDF);g.fillRect(px,y,20,20);Art.avatarMini(g,evo,px,y);g.setColor(evo==sp?0xFFE060:0x657086);g.drawRect(px,y,19,19);px+=23;}
    }

    private void fillSynCounts(int[] cnt){
        for(int i=0;i<Data.NT;i++)cnt[i]=0;
        if(bt!=null&&state!=PREP)bt.countSyn(0,cnt);
        else run.countBoardSyn(cnt);
        for(int i=0;i<Data.NT;i++)cnt[i]+=run.synergyStoneBonus[i];
    }

    private void paintDockSynHighlight(Graphics g,int type){
        if(type<0)return;int bx=boardX;
        for(int p=0;p<Run.BOARD;p++){int sp=run.board[p];if(sp>=0&&(run.type1At(p)==type||run.type2At(p)==type)){int x=bx+(p%8)*cell,y=boardY+(p/8)*cell;Art.formationSelection(g,sp,x,y,cell,game.W,0,0xFFFFFF,run.isMega(p),run.specialFormAt(p),run.isShiny(p));}}
    }

    private int countPresentSyn(int[] cnt){int n=0;for(int t=0;t<Data.NT;t++)if(cnt[t]>0)n++;return n;}
    private int presentSynCount(){fillSynCounts(synCounts);return countPresentSyn(synCounts);}
    private int presentSynTypeAt(int rank,int[] cnt){for(int t=0,n=0;t<Data.NT;t++)if(cnt[t]>0){if(n==rank)return t;n++;}return -1;}

    private void paintSyn(Graphics g) {
        int W = game.W, H = game.H, fh = Art.fh;
        int[] cnt = synCounts;
        fillSynCounts(cnt);
        int rows = 0;
        for (int t = 0; t < Data.NT; t++) if (cnt[t] > 0) rows++;
        if(synCursor>=rows)synCursor=Math.max(0,rows-1);
        int selected=presentSynTypeAt(synCursor,cnt);
        if(state==PREP&&selected>=0){int bx=boardX;for(int p=0;p<Run.BOARD;p++){int sp=run.board[p];if(sp>=0&&(run.type1At(p)==selected||run.type2At(p)==selected)){int x=bx+(p%8)*cell,y=boardY+(p/8)*cell;Art.formationSelection(g,sp,x,y,cell,W,0,0xFFFFFF,run.isMega(p),run.specialFormAt(p),run.isShiny(p));}}}
        int rowH=16;
        int h = rows==0 ? fh*4+12 : rowH * rows + fh * 2 + 10;
        if (h > H) h = H;
        int y0 = (H - h) / 2;
        Art.box(g, 4, y0, W - 8, h, 0x101830, 0xFFD030);
        Art.textBC(g, Lang.t("Cộng hưởng", "Synergies"), W / 2, y0 + 3, 0xFFD030);
        int y = y0 + fh + 5;
        if (rows == 0) Art.textC(g, Lang.t("Chưa có Pokemon trên bàn", "No units on board"), W / 2, y0+fh+9, 0xB0B0C0);
        int ri=0;
        for (int t = 0; t < Data.NT; t++) {
            if (cnt[t] == 0) continue;
            int lv = Data.synLevel(t,cnt[t]);
            if(ri==synCursor){g.setColor(0x405273);g.fillRect(7,y-1,W-14,rowH);}
            int x = 8;
            Art.typeIcon(g,t,x,y);x+=20;
            int stone=run.synergyStoneBonus[t],base=cnt[t]-stone;String head=base+(stone>0?"+"+stone:"")+"  "+SynergyEffects.marks(t,cnt[t]);
            Art.textSmall(g,head,x,y+2,lv>0?0xFFD060:0x8894A8);
            int dx=x+Art.smallWidth(head)+7;
            if(dx<W-8)Art.textSmall(g,Lang.synergyDesc(t),dx,y+2,lv>0?0xFFFFFF:0x687588);
            y += rowH;
            ri++;
            if (y > y0 + h - fh) break;
        }
        Art.textSmallC(g,rows==0?Lang.t("Chưa có Pokémon trên bàn","No Pokémon on board"):Lang.t("Mỗi hệ có mốc kích hoạt riêng","Each type has its own thresholds"),W/2,y0+h-fh-2,0x8090B0);
    }

    private void paintItemBag(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh;
        Art.box(g,4,4,W-8,H-8,0x101830,0xFFD030);
        Art.textBC(g,Lang.t("TÚI TRANG BỊ","EQUIPMENT BAG"),W/2,8,0xFFD030);
        int sp=itemTarget>=0?run.get(itemTarget):-1;
        if(sp>=0)Art.textC(g,Data.name[sp],W/2,fh+9,0xFFFFFF);
        int ey=fh*2+10,ex=8;
        for(int s=0;s<3;s++){
            int id=itemTarget>=0?run.itemAt(itemTarget,s):-1;
            if(id>=0){Art.itemIcon(g,id,ex,ey);ex+=27;}
            else{g.setColor(0x344158);g.drawRect(ex,ey,23,23);ex+=27;}
        }
        int y=ey+28,rows=(H-y-fh*2-5)/27;if(rows<1)rows=1;
        int rank=0,total=0;for(int id=0;id<ItemData.count();id++)if(run.itemCount(id)>0){if(id==itemSel)rank=total;total++;}
        int first=rank>=rows?rank-rows+1:0,owned=0,shown=0;
        for(int id=0;id<ItemData.count()&&shown<rows;id++)if(run.itemCount(id)>0){
            if(owned++<first)continue;
            if(id==itemSel){g.setColor(0x405273);g.fillRect(7,y,W-14,27);}
            Art.itemIcon(g,id,9,y+1);Art.textB(g,ItemData.name(id),36,y+2,id==itemSel?0xFFFFFF:0xC8D0E0);
            Art.textSmall(g,"x"+run.itemCount(id)+"  "+ItemData.kind(id),36,y+fh+2,0x80A8C8);
            y+=27;shown++;
        }
        if(shown==0)Art.textC(g,Lang.t("Túi trang bị trống","Equipment bag is empty"),W/2,y+8,0x9098A8);
        Art.textSmallC(g,Lang.t("FIRE: trang bị/ghép   #: đóng","FIRE: equip/craft   #: close"),W/2,H-fh-6,0x8090B0);
    }

    private final int[] battleTouch=new int[32*5];private int battleTouchCount;
    private void battleHit(int x,int y,int w,int h,int action){if(battleTouchCount>=32||w<=0||h<=0)return;int p=battleTouchCount++*5;battleTouch[p]=x;battleTouch[p+1]=y;battleTouch[p+2]=w;battleTouch[p+3]=h;battleTouch[p+4]=action;}
    private boolean touchBattle(int x,int y){
        if(rosterDetail){if(y>=game.H-Art.fh*2)key(Game.K_0);return true;}
        for(int i=battleTouchCount-1;i>=0;i--){int p=i*5;if(x<battleTouch[p]||y<battleTouch[p+1]||x>=battleTouch[p]+battleTouch[p+2]||y>=battleTouch[p+1]+battleTouch[p+3])continue;int a=battleTouch[p+4];
            if(a>=50&&a<50+Data.NT){battleSynCursor=a-50;}
            else if(a>=30&&a<32){battleMoreSel=a-30;keyBattle(Game.K_FIRE);}
            else if(a==40){battleMore=false;}
            else if(a>=20&&a<29){int s=a-20;if(s<rosterCount(rosterSide)){boolean same=rosterSel==s&&!statFocus;rosterSel=s;statFocus=false;syncWatchToRoster();if(same&&(statMode<3||statMode==4))rosterDetail=true;}}
            else if(a>=10&&a<16){statMode=a-10;statFocus=false;if(statMode==5){battleMore=true;battleMoreSel=0;}else battleMore=false;}
            else if(a<2){rosterSide=a;rosterSel=battleSynCursor=0;statFocus=false;syncWatchToRoster();}
            return true;
        }return true;
    }
    private void paintBattle(Graphics g) {
        battleTouchCount=0;
        int W = game.W, H = game.H, fh = Art.fh;
        g.setColor(0x0E141C);
        g.fillRect(0, 0, W, H);
        int top = fh * 2 + 4;boolean wide=UiLayout.landscape(W,H);
        int panelH = H >= 280 ? 144 : 116;
        int arenaW=wide?W*3/5:W;
        int cs = wide?Math.min(arenaW/8,(H-top-5)/6):Math.min(W/8,(H-top-panelH-3)/6);
        int bx = wide?0:(W-cs*8)/2, by = top;

        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 8; c++) {
                boolean a = ((r + c) & 1) == 0;
                int col = r < 3 ? (a ? 0x4C2C2C : 0x563232) : (a ? 0x25354F : 0x2C3E5E);
                g.setColor(col);
                g.fillRect(bx + c * cs, by + r * cs, cs, cs);
            }
        }
        g.setColor(0x808080);
        g.drawLine(bx, by + 3 * cs, bx + 8 * cs, by + 3 * cs);

        // Persistent cell effects are below Pokemon, matching the web client's board layer.
        for(int k=0;k<Battle.MAXBOARDFX;k++){
            if(bt.boardFxTtl[k]<=0)continue;
            int fx=bx+bt.boardFxX[k]*cs+(cs-32)/2;
            int fy=by+bt.boardFxY[k]*cs+(cs-32)/2+4;
            Art.speciesSkillSprite(g,bt.boardFxSp[k],fx,fy,(visualTime/90+k)&7);
        }

        if(transitionPhase==2)GachaFx.transitionPortal(g,bx+4*cs,by+6*cs,visualTime,transitionRadius(cs));

        int linearFrac = bt.over ? 256 : simClock.fraction256();
        int frac = FixedStepClock.smooth256(linearFrac);
        if (frac > 256) frac = 256;
        for (int i = 0; i < bt.n; i++) {
            Unit u = bt.units[i];
            if (!u.alive) continue;
            int moveFrac=256;
            if(u.moveLeft>0&&u.moveTicks>0){
                moveFrac=((u.moveTicks-u.moveLeft)*256+frac)/u.moveTicks;
                if(moveFrac>256)moveFrac=256;
            }
            int ux = bx + (u.px * 256 + (u.x - u.px) * moveFrac) * cs / 256;
            int uy = by + (u.py * 256 + (u.y - u.py) * moveFrac) * cs / 256;
            if(transitionPhase==2&&u.side==0){
                int q=transitionUnitProgress();
                int sx=bx+4*cs-cs/2,sy=by+6*cs-cs/2;
                ux=sx+(ux-sx)*q/256;uy=sy+(uy-sy)*q/256;
            }
            // The web game uses directional walk/attack clips. On MIDP we retain the
            // readable motion language with interpolation, idle bob and a short lunge.
            int bob = (((visualTime/90) + i) & 3) == 0 ? -1 : 0;
            if (u.attack > 0) {
                int power = u.attack == 3 ? 3 : (u.attack == 2 ? 2 : 1);
                int dx = u.attackX - u.x, dy = u.attackY - u.y;
                if (dx < 0) dx = -1; else if (dx > 0) dx = 1;
                if (dy < 0) dy = -1; else if (dy > 0) dy = 1;
                ux += dx * power;
                uy += dy * power;
            }
            int sprY = uy + 2 + bob;
            if (u.cast > 0) {
                int rr = cs / 2 + (3 - u.cast) * 2;
                g.setColor(Art.light(Data.TCOL[u.primaryType()]));
                g.drawArc(ux + cs / 2 - rr, uy + cs / 2 - rr, rr * 2, rr * 2, 0, 360);
            }
            int animFrame = visualTime / 70 + i;
            int vw = Data.visualWidth(u.sp), vh = Data.visualHeight(u.sp);
            int ax = ux + (cs - vw) / 2;
            int ay = sprY + cs - vh - 2;
            if(ax<0)ax=0;
            if(ax+vw>W)ax=W-vw;
            int animState = u.hit > 0 ? RawAtlas.HURT :
                (u.attack > 0 || u.cast > 0 ? RawAtlas.ATTACK :
                ((bt.over && bt.winner == u.side) ? RawAtlas.VICTORY :
                (u.state == Unit.MOVING ? RawAtlas.WALK : RawAtlas.IDLE)));
            // Winners stop facing their last target and celebrate toward the viewer.
            int renderFacing=(bt.over&&bt.winner==u.side)?7:u.facing; // 7 = DOWN-LEFT
            boolean raw=u.specialForm!=0?SpecialFormAtlas.bounds(u.specialForm,ax,ay,vw,vh,animState,renderFacing,animFrame,spriteBounds,u.shiny):(u.mega?MegaAtlas.bounds(u.sp,ax,ay,vw,vh,animState,renderFacing,animFrame,spriteBounds,u.shiny):(u.sp>=Data.CORE_N?CollectionAtlas.bounds(Data.collectionIndex(u.sp),ax,ay,vw,vh,animState,renderFacing,animFrame,spriteBounds,u.shiny):RawAtlas.bounds(u.sp,ax,ay,vw,vh,animState,renderFacing,animFrame,spriteBounds,u.shiny)));
            int bodyX=raw?spriteBounds[0]:ax,bodyY=raw?spriteBounds[1]:ay;
            int bodyW=raw?spriteBounds[2]:vw,bodyH=raw?spriteBounds[3]:vh;
            Art.battleSprite(g, u.sp, ax, ay, animFrame, animState, renderFacing,u.shiny,u.mega,u.specialForm);
            if(u.shiny)Art.shinyGlints(g,bodyX-1,bodyY-1,bodyW+2,bodyH+2,visualTime+i*37);
            // Status icons are intentionally kept off the board. The upcoming
            // bottom inspector owns status names/timers so Pokemon stay readable.
            // team marker
            drawPetMarkers(g,u.sp,u.mega,u.specialForm>=1&&u.specialForm<=12,bodyX,bodyY+bodyH-3);
            // Smooth web-style bars: interpolate values between 200ms logic ticks.
            int shownHp = u.alive ? u.prevHp + (u.hp - u.prevHp) * frac / 256 : 0;
            int shownMana = u.prevMana + (u.mana - u.prevMana) * frac / 256;
            int barY=bodyY+bodyH+1;
            int barCx=bodyX+bodyW/2;
            if(barCx<18)barCx=18;if(barCx>W-18)barCx=W-18;
            if(showBoardHealth)Art.battleBar(g,barCx,barY,shownHp,u.maxHp,u.shield,shownMana,u.maxMana,u.side==0);
            if (u.hit > 0) {
                g.setColor(0xFFFFFF);
                g.drawRect(bodyX-1,bodyY-1,bodyW+1,bodyH+1);
            }
            if (u.cast > 0) {
                g.setColor(0xD060FF);
                g.drawRect(bodyX-2,bodyY-2,bodyW+3,bodyH+3);
            }
            if (u.stun > 0) {
                g.setColor(0xFFE040);
                g.fillRect(ux + cs / 2 - 2, uy + 3, 4, 2);
            } else if (u.status.freeze > 0 || u.status.sleep > 0 || u.status.paralysis > 0) {
                g.setColor(u.status.freeze > 0 ? 0x80E8FF : (u.status.sleep > 0 ? 0xD090F0 : 0xFFE040));
                g.fillArc(ux + cs / 2 - 2, uy + 2, 5, 5, 0, 360);
            } else if (u.status.burn > 0 || u.status.poison > 0) {
                g.setColor(u.status.burn > 0 ? 0xFF7030 : 0xB050D0);
                g.fillRect(ux + cs / 2 - 1, uy + 2, 3, 4);
            }
            if (i == watch) {
                g.setColor(0xFFE060);
                g.drawRect(bodyX-3,bodyY-3,bodyW+5,bodyH+barY-bodyY-bodyH+8);
            }
        }
        for (int k = 0; k < Battle.MAXSKILLFX; k++) {
            if (bt.skillFxTtl[k] <= 0) continue;
            int sf = bt.skillFxAge[k] * 2 + (linearFrac * 2 / 256);
            int fx = bx + bt.skillFxX[k] * cs + (cs - 32) / 2;
            int fy = by + bt.skillFxY[k] * cs + (cs - 32) / 2;
            // A cast must remain legible even when a device cannot decode the
            // original PNG. The ring is also the wind-up/impact silhouette used
            // underneath the source animation, not a status icon.
            int age = bt.skillFxAge[k];
            int rr = 5 + (age < 6 ? age * 2 : (11 - age) * 2);
            if (rr < 3) rr = 3;
            g.setColor(Art.light(Data.TCOL[Data.t1[bt.skillFxId[k]]]));
            g.drawArc(fx + 16 - rr, fy + 16 - rr, rr * 2, rr * 2, 0, 360);
            g.drawArc(fx + 17 - rr, fy + 16 - rr, rr * 2, rr * 2, 0, 360);
            Art.speciesSkillSprite(g, bt.skillFxId[k], fx, fy, sf);
        }
        // Projectiles are simulated visually between logical ticks. This makes ranged
        // attacks and skills legible without changing deterministic combat timing.
        for (int k = 0; k < Battle.MAXSHOT; k++) {
            if (bt.shotTtl[k] <= 0) continue;
            int age = 4 - bt.shotTtl[k];
            int p = age * 256 + frac / 4;
            if (p > 256) p = 256;
            int sx = bx + bt.shotX0[k] * cs + cs / 2;
            int sy = by + bt.shotY0[k] * cs + cs / 2;
            int ex = bx + bt.shotX1[k] * cs + cs / 2;
            int ey = by + bt.shotY1[k] * cs + cs / 2;
            int px = sx + (ex - sx) * p / 256;
            int py = sy + (ey - sy) * p / 256;
            g.setColor(Art.dark(bt.shotCol[k]));
            g.drawLine(sx, sy, px, py);
            int attackKind=bt.shotKind[k]==0?1:2;
            boolean drawn=Art.attackSprite(g,bt.shotType[k],attackKind,px,py,age);
            g.setColor(bt.shotCol[k]);
            int rr = bt.shotKind[k] == 1 ? 3 : 2;
            if(!drawn)g.fillArc(px - rr, py - rr, rr * 2 + 1, rr * 2 + 1, 0, 360);
            if (bt.shotKind[k] == 1) {
                g.setColor(0xFFFFFF);
                g.fillRect(px, py, 1, 1);
            }
        }
        for (int k = 0; k < Battle.MAXFX; k++) {
            if (bt.fxTtl[k] <= 0) continue;
            String s = bt.fxType[k] == 4 ? "KO" : (bt.fxType[k] == 2 ? "miss" : (bt.fxType[k] == 1 ? "+" : "") + bt.fxVal[k]);
            int fx = bx + bt.fxX[k] * cs + cs / 2 - Art.textWidth(s) / 2;
            int fy = by + bt.fxY[k] * cs - (4 - bt.fxTtl[k]) * 3;
            Art.text(g, s, fx, fy, bt.fxCol[k]);
        }

        // HUD
        String battleTitle="R"+run.round+"  vs "+run.enemyName;
        String perf="x"+speed+" "+game.actualFps+"/"+Save.targetFps()+"FPS"+(bt.tick>Battle.SUDDEN_DEATH?" SUDDEN":"");
        int battleCount=3;
        int battleRight=StageRoad.draw(g,3,2,run.round,run.endlessMode()?run.round+2:run.maxRound(),24,battleCount,run);
        Art.textR(g,fitHud(battleTitle,W-battleRight-6),W-3,1,0xFFFFFF);
        Art.textR(g,perf,W-3,fh+2,0xFFD030);
        if(wide)paintBattleLandscapePanel(g,bx+8*cs,top,by+6*cs);else paintBattleStats(g,by+6*cs+2,H-(by+6*cs+2));
        if(bt.over&&endReady){
            int ph=fh+6,py=H-ph;
            g.setColor(0x101830);g.fillRect(0,py,W,ph);g.setColor(bt.winner==0?0x40E060:0xFF6060);g.drawLine(0,py,W,py);
            Art.textBC(g,bt.winner==0?Lang.t("THẮNG - FIRE xem thống kê","VICTORY - FIRE for stats"):Lang.t("BẠI - FIRE xem thống kê","DEFEAT - FIRE for stats"),W/2,py+3,0xFFFFFF);
        }
    }

    private String fitHud(String value,int width){
        if(Art.textWidth(value)<=width)return value;
        String tail="..";int n=value.length();
        while(n>1&&Art.textWidth(value.substring(0,n)+tail)>width)n--;
        return value.substring(0,n)+tail;
    }

    /** Wide battle layout: board/info above, controls/3x3 progress roster below. */
    private void paintBattleLandscapePanel(Graphics g,int boardRight,int top,int boardBottom){
        int W=game.W,H=game.H,fh=Art.fh,x=boardRight+1,w=W-x,infoH=boardBottom-top;
        g.setColor(0x111A29);g.fillRect(x,top,w,infoH);g.setColor(0x40506A);g.drawRect(x,top,w-1,infoH-1);
        if(battleMore){paintBattleMore(g,top,infoH,boardBottom-Art.fh);return;}Unit selected=rosterUnit(rosterSide,rosterSel);if(selected==null&&bt!=null&&bt.n>0)selected=bt.units[watch];
        if(selected!=null){
            int ax=x+5,ay=top+6;PetAvatar.unit(g,selected,ax,ay,32);
            int nx=x+40;String name=selected.specialForm!=0?SpecialFormData.name(selected.specialForm):(selected.mega?MegaData.formName(selected.sp):Data.name[selected.sp]);if(selected.shiny)name+=Lang.t(" [Tỏa sáng]"," [Shiny]");drawMarqueeBold(g,name,nx,top+6,Math.max(20,W-nx-4),selected.alive?0xFFFFFF:0x888898);
            int barY=top+fh+10,barW=Math.max(24,w-47);Art.bar(g,nx,barY,barW,5,Math.max(0,selected.hp),Math.max(1,selected.maxHp),selected.side==0?0x76C442:0xE76E55);Art.textSmallR(g,Math.max(0,selected.hp)+"/"+selected.maxHp,W-4,barY+7,0xB8E8C0);
            Art.bar(g,nx,barY+fh+8,barW,4,selected.mana,Math.max(1,selected.maxMana),0x209CEE);Art.textSmallR(g,selected.mana+"/"+selected.maxMana,W-4,barY+fh+13,0x80C8FF);
            int iy=top+54;Art.typeIcon(g,selected.primaryType(),x+5,iy);if(selected.secondaryType()>=0)Art.typeIcon(g,selected.secondaryType(),x+23,iy);Art.textCompact(g,Lang.t("Công: ","ATK: ")+selected.atk+Lang.t("  Thủ: ","  DEF: ")+selected.def,x+42,iy+3,0xD8E0F0);Art.textCompact(g,Lang.t("Kháng: ","SDEF: ")+selected.speDef+Lang.t("  Tầm: ","  RNG: ")+selected.range,x+5,iy+19,0xB8C8D8);
            int itemY=iy+34;for(int s=0;s<3;s++){int ix=x+5+s*22,id=selected.items[s];g.setColor(0x202735);g.fillArc(ix,itemY,18,18,0,360);if(id>=0)Art.itemIconTiny(g,id,ix+5,itemY+5);g.setColor(id>=0?0x80D8FF:0x596878);g.drawArc(ix,itemY,17,17,0,360);}
        }else Art.textBC(g,Lang.t("THÔNG TIN","INFORMATION"),x+w/2,top+infoH/2-fh/2,0x8090A8);

        int y=boardBottom+1,h=H-y,rosterW=boardRight,optionX=rosterW+1,optionW=W-optionX;g.setColor(0x111A29);g.fillRect(0,y,W,h);g.setColor(0x40506A);g.drawLine(0,y,W,y);g.drawLine(rosterW,y,rosterW,H);
        int sideH=fh+3,half=optionW/2;for(int s=0;s<2;s++){int tx=optionX+s*half,tw=s==1?W-tx:half-1;g.setColor(s==rosterSide?0x49617E:0x29374D);g.fillRect(tx,y+1,tw,sideH);battleHit(tx,y+1,tw,sideH,s);if(s==rosterSide){g.setColor(s==0?0x76C442:0xE76E55);g.fillRect(tx,y+sideH-1,tw,2);}Art.textSmallC(g,(s==0?Lang.t("TA","ALLY"):Lang.t("ĐỊCH","ENEMY"))+" "+rosterAlive(s)+"/"+rosterCount(s),tx+tw/2,y+2,0xFFFFFF);}
        String[] modes={"DMG","HP","MP",Lang.t("Hệ","TYPE"),"Item","More"};int my=y+sideH+3,mw=Math.max(1,optionW/3),mh=Math.max(fh+3,(h-sideH-fh-8)/2);for(int i=0;i<6;i++){int tx=optionX+(i%3)*mw,ty=my+(i/3)*mh,tw=i%3==2?W-tx:mw-1;g.setColor(i==statMode?0xC59112:0x35455F);g.fillRect(tx,ty,tw,mh-1);battleHit(tx,ty,tw,mh-1,10+i);if(i==statMode&&statFocus){g.setColor(0xFFFFFF);g.drawRect(tx,ty,tw-1,mh-2);}Art.textSmallC(g,modes[i],tx+tw/2,ty+(mh-fh)/2,i==statMode?0xFFFFFF:0xA8B5C8);}
        Art.textSmallC(g,battleMore?Lang.t("FIRE: DEV   0: đóng","FIRE: DEV   0: close"):Lang.t("FIRE: chi tiết   *: tốc độ","FIRE: details   *: speed"),optionX+optionW/2,H-fh-1,0x687890);

        int count=rosterCount(rosterSide);if(rosterSel>=count)rosterSel=Math.max(0,count-1);int cw=Math.max(1,rosterW/3),ch=Math.max(1,h/3),max=1;for(int i=0;i<count;i++){int v=statValue(rosterUnit(rosterSide,i),statMode);if(v>max)max=v;}
        for(int slot=0;slot<9;slot++){int c=slot%3,r=slot/3,tx=c*cw,ty=y+r*ch,tw=c==2?rosterW-tx:cw;g.setColor(((r+c)&1)==0?0x202C3F:0x1B2638);g.fillRect(tx,ty,tw-1,ch-1);battleHit(tx,ty,tw-1,ch-1,20+slot);Unit u=rosterUnit(rosterSide,slot);if(u==null)continue;int avY=ty+(ch-20)/2;if(avY<ty)avY=ty;PetAvatar.unit(g,u,tx+2,avY,20);if(statMode==4){int ix=tx+24,iy=ty+(ch-14)/2;for(int s=0;s<3;s++){g.setColor(u.items[s]>=0?0x263448:0x172131);g.fillArc(ix+s*14,iy,12,12,0,360);if(u.items[s]>=0)Art.itemIconTiny(g,u.items[s],ix+2+s*14,iy+2);g.setColor(u.items[s]>=0?0x80D8FF:0x4B586C);g.drawArc(ix+s*14,iy,11,11,0,360);}}else if(statMode==3){Art.typeIcon(g,u.primaryType(),tx+24,ty+(ch-16)/2);if(u.secondaryType()>=0)Art.typeIcon(g,u.secondaryType(),tx+42,ty+(ch-16)/2);}else{int value=statValue(u,statMode),barMax=max;if(statMode==1)barMax=Math.max(1,u.maxHp);else if(statMode==2)barMax=Math.max(1,u.maxMana);Art.textSmall(g,""+value,tx+24,ty+3,u.alive?0xFFFFFF:0x888898);Art.bar(g,tx+24,ty+ch-6,Math.max(4,tw-28),5,value,barMax,statColor(statMode,rosterSide));}if(slot==rosterSel&&!statFocus){g.setColor(0x60E878);g.drawRect(tx+1,ty+1,tw-3,ch-3);}}
    }

    private void paintBattleInfo(Graphics g, int y, int h) {
        int W = game.W, fh = Art.fh;
        g.setColor(0x090D14); g.fillRect(0, y, W, h);
        g.setColor(0x40506A); g.drawLine(0, y, W, y);
        if (bt == null || bt.n == 0) return;
        Unit u = bt.units[watch];
        String team = u.side == 0 ? Lang.t("TA","YOU") : Lang.t("ĐỊCH","FOE");
        int teamCol = u.side == 0 ? 0x70B8FF : 0xFF7070;
        Art.textB(g, u.specialForm!=0?SpecialFormData.name(u.specialForm):Data.name[u.sp], 3, y + 2, 0xFFFFFF);
        Art.textR(g, team, W - 3, y + 2, teamCol);
        String hp = "HP " + u.hp + "/" + u.maxHp;
        String mp = u.maxMana > 0 ? "  MP " + u.mana + "/" + u.maxMana : "";
        Art.text(g, hp + mp, 3, y + fh + 2, 0xB8E8C0);
        String st = "AT " + u.atk + " DF " + u.def + "/" + u.speDef + "  " + Lang.moveName(Data.skillName[u.sp]);
        if (u.stun > 0) st += Lang.t(" CHOÁNG"," STUN");
        else if (u.status.visual() >= 0) st += " " + Lang.statusName(u.status.visual());
        else if (u.shield > 0) st += Lang.t(" KHIÊN"," SHIELD");
        else if (u.cast > 0) st += Lang.t(" TUNG CHIÊU"," CAST");
        else if (!u.alive) st += " KO";
        Art.textSmall(g, st, 3, y + fh * 2 + 2, u.stun > 0 ? 0xFFE050 : 0xA8B8D0);
        Art.textR(g, Lang.t("FIRE đội  * tốc độ","FIRE teams  * speed"), W - 3, y + fh * 2 + 2, 0x687890);
    }

    /** Persistent panel below the board: visible side/mode buttons plus 3x3 mini roster. */
    private void paintBattleStats(Graphics g,int y,int h) {
        int W=game.W,fh=Art.fh;
        g.setColor(0x111A29);g.fillRect(0,y,W,h);
        g.setColor(0x40506A);g.drawLine(0,y,W,y);
        int sideH=Math.max(13,fh),half=W/2;
        for(int s=0;s<2;s++){
            int x=s*half,w=s==1?W-half:half-1;
            g.setColor(s==rosterSide?0x49617E:0x29374D);g.fillRect(x,y+1,w,sideH);battleHit(x,y+1,w,sideH,s);
            if(s==rosterSide){g.setColor(s==0?0x76C442:0xE76E55);g.fillRect(x,y+sideH-1,w,2);}
            String sideName=s==0?Lang.t("TA","ALLY"):Lang.t("ĐỊCH","ENEMY");
            Art.textC(g,sideName+" "+rosterAlive(s)+"/"+rosterCount(s),x+w/2,y+1,0xFFFFFF);
        }
        String[] modes={"DMG","HP","MP",Lang.t("Hệ","TYPE"),"Item","More"};
        int modeY=y+sideH+2,mh=Math.max(13,fh),mw=W/6;
        for(int i=0;i<6;i++){
            int x=i*mw,w=i==5?W-x:mw-1;
            g.setColor(i==statMode?0xC59112:0x35455F);g.fillRect(x,modeY,w,mh);battleHit(x,modeY,w,mh,10+i);
            if(i==statMode&&statFocus){g.setColor(0xFFFFFF);g.drawRect(x,modeY,w-1,mh-1);}
            Art.textSmallC(g,modes[i],x+w/2,modeY+1,i==statMode?0xFFFFFF:0xA8B5C8);
        }
        int footer=Math.max(13,fh+1),gridY=modeY+mh+2,gridH=h-(gridY-y)-footer;
        if(battleMore){paintBattleMore(g,gridY,gridH,y+h-footer);return;}
        if(statMode==3){paintBattleSynergies(g,gridY,gridH,y+h-footer,footer);return;}
        if(statMode==5){paintBattleDevInfo(g,gridY,gridH,y+h-footer);return;}
        int cw=W/3,ch=Math.max(1,gridH/3),count=rosterCount(rosterSide);
        if(rosterSel>=count)rosterSel=Math.max(0,count-1);
        int max=1;
        for(int i=0;i<count;i++){Unit u=rosterUnit(rosterSide,i);int v=statValue(u,statMode);if(v>max)max=v;}
        for(int slot=0;slot<9;slot++){
            int c=slot%3,r=slot/3,x=c*cw,cy=gridY+r*ch,w=c==2?W-x:cw;battleHit(x,cy,w-1,ch-1,20+slot);
            g.setColor(((r+c)&1)==0?0x202C3F:0x1B2638);g.fillRect(x,cy,w-1,ch-1);
            Unit u=rosterUnit(rosterSide,slot);if(u==null)continue;
            int value=statValue(u,statMode),avY=cy+(ch-20)/2;if(avY<cy)avY=cy;
            if(u.alive)Art.avatarMini(g,u.sp,x+2,avY);else Art.avatarMiniGray(g,u.sp,x+2,avY);
            if(statMode==4){
                int ix=x+24,iy=cy+(ch-14)/2;
                for(int s=0;s<3;s++){g.setColor(u.items[s]>=0?0x263448:0x172131);g.fillArc(ix+s*16,iy,14,14,0,360);if(u.items[s]>=0)Art.itemIconTiny(g,u.items[s],ix+3+s*16,iy+3);g.setColor(u.items[s]>=0?0x80D8FF:0x4B586C);g.drawArc(ix+s*16,iy,13,13,0,360);}
            }else Art.textSmall(g,(statMode==1?"HP ":statMode==2?"MP ":"")+value,x+24,cy+9,u.alive?0xFFFFFF:0x888898);
            int barX=x+24,barY=cy+ch-5,barW=w-28;if(barW<4)barW=4;
            int col=statColor(statMode,rosterSide);
            int barMax=max;
            if(statMode==1)barMax=Math.max(1,u.maxHp);
            else if(statMode==2)barMax=Math.max(1,u.maxMana);
            if(statMode!=4)Art.bar(g,barX,barY,barW,5,value,barMax,col);
            // One thin inset outline keeps the progress bar readable.
            if(slot==rosterSel&&!statFocus){g.setColor(0x60E878);g.drawRect(x+1,cy+1,w-3,ch-3);}
        }
        int total=0;for(int i=0;i<count;i++)total+=statValue(rosterUnit(rosterSide,i),statMode);
        Art.textB(g,statMode==4?Lang.t("FIRE: xem trang bị","FIRE: item details"):Lang.t("Tổng: ","Total: ")+total,3,y+h-footer+1,statColor(statMode,rosterSide));
    }

    private void paintBattleMore(Graphics g,int y,int h,int footerY){int W=game.W,fh=Art.fh;g.setColor(0x182438);g.fillRect(0,y,W,h);battleHit(0,footerY,W,Math.max(1,game.H-footerY),40);Art.textCompact(g,"MORE",8,y+3,0xFFD060);int row=Math.max(14,Math.min(fh+6,(h-18)/2));for(int i=0;i<2;i++){int by=y+16+i*row;g.setColor(i==battleMoreSel?0x405273:0x24344B);g.fillRect(8,by,W-16,row-2);battleHit(8,by,W-16,row-2,30+i);if(i==battleMoreSel){g.setColor(0xFFFFFF);g.drawRect(8,by,W-17,row-3);}String label=i==0?Lang.t("INFO DEV - chỉ số realtime","DEV INFO - realtime stats"):showBoardHealth?Lang.t("Ẩn thanh máu trên sân","Hide board health bars"):Lang.t("Hiện thanh máu trên sân","Show board health bars");panelBattleLabel(g,label,12,by+3,W-24);}Art.textSmall(g,Lang.t("2/8 chọn   FIRE xác nhận   0 đóng","2/8 select   FIRE confirm   0 close"),3,footerY+1,0xA8B5C8);}
    private void panelBattleLabel(Graphics g,String s,int x,int y,int w){g.setClip(x,y,w,Art.compactHeight()+1);Art.textCompact(g,s,x,y,0xFFFFFF);g.setClip(0,0,game.W,game.H);}

    private void paintBattleDevInfo(Graphics g,int y,int h,int footerY){
        int W=game.W,fh=Art.fh;Unit u=rosterUnit(rosterSide,rosterSel);g.setColor(0x121D2D);g.fillRect(0,y,W,h);
        if(u==null){Art.textSmallC(g,Lang.t("Không có pet đang chọn","No selected unit"),W/2,y+3,0x8090A8);return;}
        PetAvatar.unit(g,u,3,y+2,20);Art.textB(g,(u.specialForm!=0?SpecialFormData.name(u.specialForm):(u.mega?MegaData.formName(u.sp):Data.name[u.sp]))+(u.shiny?Lang.t(" [Tỏa sáng]"," [Shiny]"):"")+"  T"+Data.tier[u.sp],27,y+2,u.alive?0xFFFFFF:0x888898);
        Art.textSmall(g,"HP "+u.hp+"/"+u.maxHp+"  MP "+u.mana+"/"+u.maxMana+"  SH "+u.shield,27,y+fh+3,0xB8E8C0);
        Art.textSmall(g,"ATK "+u.atk+"  DEF "+u.def+"  SDEF "+u.speDef+"  SPD "+u.speed,3,y+fh*2+4,0xD8E0F0);
        Art.textSmall(g,"CRIT "+u.crit+"  DODGE "+u.dodge+"  AP "+u.skillBonus+"  REGEN "+u.regen,3,y+fh*3+5,0xD8E0F0);
        String types=Lang.typeName(u.primaryType())+(u.secondaryType()>=0?"/"+Lang.typeName(u.secondaryType()):"");
        Art.textSmall(g,types+"  "+Lang.moveName(Data.skillName[u.sp]),3,y+fh*4+6,u.shiny?0xFFF060:0xFFD060);
        Art.textSmall(g,Lang.t("0 More  |  mũi tên chọn pet","0 More  |  arrows select unit"),3,footerY+1,0x8090A8);
    }

    private int statValue(Unit u,int mode){
        if(u==null)return 0;
        switch(mode){case 0:return u.damageDealt;case 1:return u.alive?Math.max(0,u.hp):0;
            case 2:return u.mana;default:return 0;}
    }

    private int statColor(int mode,int side){
        switch(mode){case 0:return 0xE76E55;case 1:return side==0?0x76C442:0xE76E55;
            case 2:return 0x209CEE;case 3:return 0xFFE060;default:return 0x687890;}
    }

    private void fillBattleSynCounts(int side,int[] cnt){
        for(int t=0;t<Data.NT;t++)cnt[t]=0;
        if(bt!=null)bt.countSyn(side,cnt);
    }

    private int battleSynCount(){
        int[] cnt=synCounts;fillBattleSynCounts(rosterSide,cnt);int n=0;
        for(int t=0;t<Data.NT;t++)if(cnt[t]>0)n++;
        return n;
    }

    private int battleSynType(int rank){
        int[] cnt=synCounts;fillBattleSynCounts(rosterSide,cnt);int n=0;
        for(int t=0;t<Data.NT;t++)if(cnt[t]>0){if(n==rank)return t;n++;}
        return -1;
    }

    private void moveBattleSyn(int delta){
        int n=battleSynCount();if(n<=0){battleSynCursor=0;return;}
        battleSynCursor=(battleSynCursor+delta+n)%n;
    }

    /** Compact scrolling type picker and a fixed 3x3 matching-Pokémon grid. */
    private void paintBattleSynergies(Graphics g,int y,int h,int footerY,int footerH){
        int W=game.W,leftW=Math.max(42,W/4),rightX=leftW+1,rightW=W-rightX;
        int[] cnt=synCounts;fillBattleSynCounts(rosterSide,cnt);
        int n=countPresentSyn(cnt);if(n<=0){battleSynCursor=0;}else if(battleSynCursor>=n)battleSynCursor=n-1;
        int type=n>0?presentSynTypeAt(battleSynCursor,cnt):-1;
        g.setColor(0x172235);g.fillRect(0,y,leftW,h);
        g.setColor(0x1B2638);g.fillRect(rightX,y,rightW,h);
        g.setColor(0x40506A);g.drawLine(leftW,y,leftW,y+h-1);
        int cell=20,visible=Math.max(1,h/cell),start=0;
        if(battleSynCursor>=visible)start=battleSynCursor-visible+1;
        int rank=0;
        for(int t=0;t<Data.NT;t++)if(cnt[t]>0){
            if(rank>=start&&rank<start+visible){
                int local=rank-start,x=3,cy=y+local*cell;battleHit(x,cy,leftW-6,cell,50+rank);
                g.setColor(rank==battleSynCursor?0x304C3D:0x202D41);g.fillRect(x,cy+1,leftW-6,cell-2);
                if(rank==battleSynCursor){g.setColor(0x60E878);g.drawRect(x,cy+1,leftW-6,cell-2);}
                Art.typeIcon(g,t,x+2,cy+2);Art.textSmallR(g,""+cnt[t],leftW-5,cy+5,rank==battleSynCursor?0xFFFFFF:0xA8B5C8);
            }
            rank++;
        }
        if(n==0)Art.textSmallC(g,Lang.t("Chưa có hệ","No types"),leftW/2,y+4,0x687890);
        int matches=0;for(int i=0;i<rosterCount(rosterSide);i++){Unit u=rosterUnit(rosterSide,i);if(u!=null&&type>=0&&(u.primaryType()==type||u.secondaryType()==type))matches++;}
        int cardCols=3,cardRows=3,cardW=Math.max(1,rightW/cardCols),cardH=Math.max(1,h/cardRows),slot=0;
        for(int box=0;box<9;box++){int c=box%3,r=box/3,x=rightX+c*cardW,cy=y+r*cardH,w=c==2?W-x:cardW;g.setColor(((r+c)&1)==0?0x243248:0x202C3F);g.fillRect(x,cy,w-1,cardH-1);}
        for(int i=0;i<rosterCount(rosterSide);i++){
            Unit u=rosterUnit(rosterSide,i);if(u==null||type<0||(u.primaryType()!=type&&u.secondaryType()!=type))continue;
            if(slot>=9)break;
            int c=slot%cardCols,r=slot/cardCols,x=rightX+c*cardW,cy=y+r*cardH,w=c==cardCols-1?W-x:cardW;
            int ay=cy+Math.max(1,(cardH-20)/2);
            PetAvatar.unit(g,u,x+2,ay,20);
            int tx=x+24;
            Art.typeIcon(g,u.primaryType(),tx,cy+2);
            if(u.secondaryType()>=0)Art.typeIcon(g,u.secondaryType(),tx,cy+Math.max(17,cardH-18));
            slot++;
        }
        String info=type<0?Lang.t("Chưa có Pokémon thuộc hệ nào","No Pokémon types"):Lang.typeName(type)+"  "+cnt[type]+"  "+Lang.synergyDesc(type);
        Art.textSmall(g,info,3,footerY+1,0xD8E0F0);
    }

    private void paintRosterDetail(Graphics g) {
        int W=game.W,H=game.H,fh=Art.fh;
        Unit u=rosterUnit(rosterSide,rosterSel);
        g.setColor(0x0C1420);g.fillRect(0,0,W,H);
        if(u==null){rosterDetail=false;return;}
        int rarity=Art.rarityColor(Data.category[u.sp]);
        Art.textB(g,(u.specialForm!=0?SpecialFormData.name(u.specialForm):(u.mega?MegaData.formName(u.sp):Data.name[u.sp]))+(u.shiny?Lang.t(" [Tỏa sáng]"," [Shiny]"):""),4,3,0xFFFFFF);
        Art.textR(g,u.side==0?Lang.t("ĐỘI MÌNH","MY TEAM"):Lang.t("ĐỘI ĐỊCH","ENEMY"),W-4,3,u.side==0?0x76C442:0xE76E55);
        g.setColor(rarity);g.fillRect(0,fh+5,W,2);
        int ay=fh+11;PetAvatar.unit(g,u,5,ay,32);
        String typeLabel=Lang.t("Hệ:","Type:");Art.textSmall(g,typeLabel,42,ay+4,0x90A8C8);int typeX=42+Art.smallWidth(typeLabel)+3;Art.typeIcon(g,u.primaryType(),typeX,ay+1);
        if(u.secondaryType()>=0)Art.typeIcon(g,u.secondaryType(),typeX+18,ay+1);
        Art.textSmall(g,Lang.t("Máu: ","HP: ")+Math.max(0,u.hp)+"/"+u.maxHp+Lang.t("  Năng lượng: ","  PP: ")+u.mana+"/"+u.maxMana,42,ay+18,0xB8E8C0);
        int y=ay+36;
        Art.textSmall(g,Lang.t("Công: ","ATK: ")+u.atk+Lang.t("  Thủ: ","  DEF: ")+u.def+Lang.t("  Kháng: ","  SP.DEF: ")+u.speDef,5,y,0xD8E0F0);y+=fh;
        Art.textSmall(g,Lang.t("Tốc: ","SPD: ")+u.speed+Lang.t("  Tầm: ","  RNG: ")+u.range+Lang.t("  Hồi chiêu: ","  CD: ")+u.cd,5,y,0xD8E0F0);y+=fh;
        Art.textSmall(g,Lang.t("Khiên: ","Shield: ")+u.shield+Lang.t("  Chí mạng: ","  Crit: ")+u.crit+"%"+Lang.t("  Né: ","  Dodge: ")+u.dodge+"%",5,y,0xD8E0F0);y+=fh+2;
        Art.textSmall(g,Lang.t("Sát thương: ","DMG: ")+u.damageDealt+Lang.t("  Nhận: ","  Taken: ")+u.damageTaken+Lang.t("  Chặn: ","  Block: ")+u.damageBlocked,5,y,0xFFC0A0);y+=fh;
        Art.textSmall(g,Lang.t("Hồi: ","Heal: ")+u.healingDone+Lang.t("  Khiên tạo: ","  Shield made: ")+u.shieldDone,5,y,0xA0E8B0);y+=fh+2;
        if(u.shiny){y=Art.para(g,ShinyData.desc(),5,y,W-10,0xFFF060,2)+1;}
        if(u.mega){y=Art.para(g,MegaData.detail(u.sp),5,y,W-10,0x70E8FF,2)+1;}
        drawEvolutionChain(g,u.sp,5,y);y+=27;
        Art.textB(g,Lang.moveName(Data.skillName[u.sp]),5,y,0xA0FFA0);y+=fh;
        y=Art.para(g,AbilityBehavior.description(u.sp),5,y,W-10,0xA0D8A0,2)+2;
        int first=u.status.visual();
        Art.textB(g,Lang.t("Trạng thái:","Status:"),5,y,0xFF6868);y+=fh;
        if(first<0&&u.stun<=0){Art.text(g,Lang.t("Bình thường","Normal"),5,y,0xFF9090);y+=fh;}
        else {
            int sx=5;
            if(u.stun>0){Art.text(g,Lang.t("Choáng","Stun"),sx,y,0xFF7070);sx+=Art.textWidth(Lang.t("Choáng","Stun"))+5;}
            for(int n=0;n<4;n++){
                int st=u.status.visualAt(n);if(st<0)break;
                Art.statusSprite(g,st,sx,y-1,visualTime/90);sx+=18;
            }
            String labels=statusLabels(u);if(labels.length()>0)Art.textSmall(g,labels,5,y+17,0xFF9090);
            y+=fh+18;
        }
        paintBattleEquipment(g,u,5,y);
        Art.textC(g,Lang.t("FIRE / 0: quay lại danh sách","FIRE / 0: back to roster"),W/2,H-fh-2,0x8090B0);
    }

    private void paintBattleEquipment(Graphics g,Unit u,int x,int y){
        paintEquipmentBlock(g,x,y,u.items[0],u.items[1],u.items[2]);
    }

    private void paintEquipmentBlock(Graphics g,int x,int y,int a,int b,int c){
        int W=game.W,fh=Art.fh;
        Art.textB(g,Lang.t("Trang bị:","Equipment:"),x,y,0xFFD030);y+=fh+1;
        for(int s=0;s<3;s++){
            int px=x+s*27,id=s==0?a:(s==1?b:c);g.setColor(0x202735);g.fillArc(px,y,24,24,0,360);
            g.setColor(id>=0?0xFFE060:0x536176);g.drawArc(px,y,23,23,0,360);
            if(id>=0)Art.itemIcon(g,id,px,y);
        }
        y+=26;
        String bonus=itemBonusSummary(a,b,c);if(bonus.length()==0)bonus=Lang.t("Không có chỉ số trang bị","No equipment bonuses");
        Art.para(g,bonus,x,y,W-x-5,0xFFE39A,2);
        y+=Art.fh*2;
        for(int s=0;s<3;s++){int id=s==0?a:(s==1?b:c);if(id<0)continue;String passive=ItemEffects.passiveDesc(id);if(passive.length()==0)passive=ItemData.desc(id);String line=ItemData.name(id)+": "+passive;while(line.length()>6&&Art.smallWidth(line)>W-x-6)line=line.substring(0,line.length()-1);Art.textSmall(g,line,x,y,0xB8E8FF);y+=fh;}
    }

    private String itemBonusSummary(int a,int b,int c){
        int hp=0,atk=0,def=0,sd=0,spd=0,mp=0,shield=0,ap=0,crit=0;
        for(int s=0;s<3;s++){int id=s==0?a:(s==1?b:c);if(id<0)continue;hp+=ItemData.HP[id];atk+=ItemData.ATK[id];def+=ItemData.DEF[id];sd+=ItemData.SPE_DEF[id];spd+=ItemData.SPEED[id];mp+=ItemData.MP[id];shield+=ItemData.SHIELD[id];ap+=ItemData.AP[id];crit+=ItemData.CRIT[id];}
        StringBuffer out=new StringBuffer();
        if(atk!=0)out.append(Lang.t("Công +","ATK +")).append(atk);
        if(def!=0){if(out.length()>0)out.append("  ");out.append(Lang.t("Thủ +","DEF +")).append(def);}
        if(sd!=0){if(out.length()>0)out.append("  ");out.append(Lang.t("Kháng +","SP.DEF +")).append(sd);}
        if(hp!=0){if(out.length()>0)out.append("  ");out.append("HP +").append(hp);}
        if(mp!=0){if(out.length()>0)out.append("  ");out.append("PP +").append(mp);}
        if(spd!=0){if(out.length()>0)out.append("  ");out.append(Lang.t("Tốc +","SPD +")).append(spd);}
        if(shield!=0){if(out.length()>0)out.append("  ");out.append(Lang.t("Khiên +","Shield +")).append(shield);}
        if(ap!=0){if(out.length()>0)out.append("  ");out.append("AP +").append(ap);}
        if(crit!=0){if(out.length()>0)out.append("  ");out.append(Lang.t("Chí mạng +","Crit +")).append(crit).append("%");}
        return out.toString();
    }

    private String statusLabels(Unit u) {
        StringBuffer b=new StringBuffer();
        for(int n=0;n<6;n++){
            int st=u.status.visualAt(n);if(st<0)break;
            if(b.length()>0)b.append(", ");b.append(Lang.statusName(st));
        }
        return b.toString();
    }

    private void paintResult(Graphics g) {
        int W = game.W, H = game.H, fh = Art.fh;
        g.setColor(0x101827);g.fillRect(0,0,W,H);
        Art.textBC(g,Lang.t("THÔNG SỐ TRẬN ĐẤU","BATTLE STATISTICS"),W/2,2,0xFFD030);
        Art.textSmallC(g,run.lastWon?Lang.t("THẮNG","VICTORY"):Lang.t("BẠI","DEFEAT"),W/2,fh+2,run.lastWon?0x60FF80:0xFF6060);
        Art.textSmallC(g,Lang.t("SÁT THƯƠNG TRẬN ĐẤU","BATTLE DAMAGE"),W/2,fh*2+2,0xD8E0F0);
        int pc=rosterCount(0),ec=rosterCount(1),rows=Math.max(pc,ec);
        int totalP=0,totalE=0,max=1;
        for(int i=0;i<pc;i++){int v=rosterUnit(0,i).damageDealt;totalP+=v;if(v>max)max=v;}
        for(int i=0;i<ec;i++){int v=rosterUnit(1,i).damageDealt;totalE+=v;if(v>max)max=v;}
        int headY=fh*3+4;
        Art.textSmallC(g,Lang.t("TA ","ALLY ")+totalP,W/4,headY,0x76C442);
        Art.textSmallC(g,Lang.t("ĐỊCH ","ENEMY ")+totalE,W*3/4,headY,0xE76E55);
        int top=headY+fh+2,footer=fh*5+7,rowH=rows>0?(H-top-footer)/rows:20;
        if(rowH>24)rowH=24;if(rowH<13)rowH=13;
        int av=rowH>=21?20:16,half=W/2,barW=half-av-8;if(barW<12)barW=12;
        for(int r=0;r<rows;r++){
            int y=top+r*rowH;Unit p=rosterUnit(0,r),e=rosterUnit(1,r);
            if((r&1)==0){g.setColor(0x172235);g.fillRect(0,y,W,rowH-1);}
            if(p!=null){
                PetAvatar.draw(g,p.sp,p.specialForm,p.mega,p.shiny,2,y+(rowH-av)/2,av);
                Art.textSmall(g,""+p.damageDealt,av+5,y,0xD8F0D8);
                drawResultItems(g,p,half-29,y+1);
                Art.bar(g,av+5,y+Math.min(fh,rowH-4),barW,4,p.damageDealt,max,0x76C442);
            }
            if(e!=null){
                PetAvatar.draw(g,e.sp,e.specialForm,e.mega,e.shiny,W-av-2,y+(rowH-av)/2,av);
                Art.textSmallR(g,""+e.damageDealt,W-av-5,y,0xFFD0C8);
                drawResultItems(g,e,half+2,y+1);
                Art.barReverse(g,half+3,y+Math.min(fh,rowH-4),barW,4,e.damageDealt,max,0xE76E55);
            }
        }
        int fy=H-footer+2;
        Art.textSmallC(g,"XP +"+run.lastXp+Lang.t("   Pet còn ","   Survivors ")+run.lastSurvivors,W/2,fy,0xB8E8C0);
        Art.textSmallC(g,Lang.t("Tổng vàng +","Total gold +")+run.lastGold,W/2,fy+fh,0xFFD060);
        String goldParts=Lang.t("Gốc ","Base ")+run.lastBaseGold+Lang.t("  Lãi ","  Interest ")+run.lastInterest+
                Lang.t("  Chuỗi ","  Streak ")+run.lastStreakGold+Lang.t("  Thắng ","  Win ")+run.lastVictoryGold+
                (run.lastItemGold>0?Lang.t("  Đồ ","  Item ")+run.lastItemGold:"");
        Art.textSmallC(g,goldParts,W/2,fy+fh*2,0xC8B878);
        if(run.lastItem>=0)Art.textSmallC(g,Lang.t("Vật phẩm: ","Item: ")+ItemData.name(run.lastItem),W/2,fy+fh*3,0x80D8FF);
        String next=run.over?Lang.t("FIRE: xem tổng kết","FIRE: final summary"):
            (run.lastWon?Lang.t("FIRE: tiếp tục","FIRE: continue"):Lang.t("FIRE: đấu lại vòng này","FIRE: retry this round"));
        Art.textSmallC(g,next,W/2,H-fh-2,0x8090B0);
    }

    private void drawResultItems(Graphics g,Unit u,int x,int y){for(int s=0;s<3;s++){int id=u.items[s],px=x+s*9;g.setColor(id>=0?0x263448:0x172131);g.fillArc(px,y,8,8,0,360);g.setColor(id>=0?0x80D8FF:0x4B586C);g.drawArc(px,y,7,7,0,360);if(id>=0)Art.itemIconTiny(g,id,px, y);}}

    private int transitionUnitProgress(){
        int q=(transitionT-130)*256/590;
        if(q<0)q=0;if(q>256)q=256;return q;
    }

    private int transitionRadius(int base){
        int max=base+8;if(max<22)max=22;
        if(transitionT<220)return 2+(max-2)*transitionT/220;
        if(transitionT>690)return max*(TRANSITION_MS-transitionT)/(TRANSITION_MS-690);
        return max;
    }

    /** Reusable row-based choice presentation for items, starters, pools and Legendaries. */
    private void paintRewardChoice(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,w=Math.min(W-12,286),textW=w-55;
        ensureChoiceLayout(textW);int[] rh=choiceRowCache;
        int content=rh[0]+rh[1]+rh[2],chrome=fh*2+17,h=content+chrome;if(h>H-8)h=H-8;
        int x=(W-w)/2,y=(H-h)/2,top=y+fh+8,footer=fh+6,view=h-(top-y)-footer;
        choiceTouchX=x;choiceTouchY=y;choiceTouchW=w;choiceTouchH=h;choiceTouchTop=top;choiceTouchView=view;
        for(int i=0;i<3;i++)choiceTouchRows[i]=rh[i];
        int selectedTop=rewardChoiceSel==0?0:(rewardChoiceSel==1?rh[0]:rh[0]+rh[1]);
        int selectedBottom=selectedTop+rh[rewardChoiceSel];
        if(selectedTop<rewardChoiceScroll)rewardChoiceScroll=selectedTop;
        if(selectedBottom>rewardChoiceScroll+view)rewardChoiceScroll=selectedBottom-view;
        int maxScroll=Math.max(0,content-view);if(rewardChoiceScroll>maxScroll)rewardChoiceScroll=maxScroll;if(rewardChoiceScroll<0)rewardChoiceScroll=0;
        Art.box(g,x,y,w,h,0x101830,0xFFD030);
        String title=rewardChoiceKind==CH_TOWER?Lang.t("CHỌN BUFF TOÀN ĐỘI","CHOOSE TEAM BUFF"):rewardChoiceKind==CH_TYPE?Lang.t("CHỌN POOL HỆ","CHOOSE TYPE POOL"):
            (rewardChoiceKind==CH_STARTER?(run.mode==Run.MODE_LEGEND?Lang.t("CHỌN THẦN THÚ KHỞI ĐẦU","CHOOSE A LEGENDARY STARTER"):Lang.t("CHỌN POKÉMON KHỞI ĐẦU","CHOOSE A STARTER")):
            (rewardChoiceKind==CH_ITEM?Lang.t("CHỌN 1 TRANG BỊ MIỄN PHÍ","CHOOSE 1 FREE ITEM"):
            (rewardChoiceKind==CH_ADD?Lang.t("CHỌN FAMILY BỔ SUNG","CHOOSE AN EXTRA FAMILY"):
            (rewardChoiceKind==CH_UNIQUE?Lang.t("CHỌN UNIQUE","CHOOSE A UNIQUE"):Lang.t("CHỌN LEGENDARY","CHOOSE A LEGENDARY")))));
        Art.textBC(g,title,W/2,y+3,0xFFD030);
        int oldX=g.getClipX(),oldY=g.getClipY(),oldW=g.getClipWidth(),oldH=g.getClipHeight();
        g.setClip(x+3,top,w-6,view);
        int ry=top-rewardChoiceScroll;
        for(int i=0;i<3;i++){
            int rowH=rh[i],id=rewardChoiceIds[i];
            g.setColor(i==rewardChoiceSel?0x405273:((i&1)==0?0x202B40:0x192338));
            g.fillRect(x+3,ry,w-6,rowH-2);
            if(i==rewardChoiceSel){g.setColor(0xFFE060);g.drawRect(x+3,ry,w-7,rowH-3);}
            if(rewardChoiceKind==CH_TOWER){Art.textB(g,ExtraChessRules.buffName(id),x+7,ry+8,0xFFFFFF);Art.textSmall(g,Lang.t("Áp dụng đến hết run","Applies for the entire run"),x+7,ry+Art.fh+12,0x9FB5CE);}
            else if(rewardChoiceKind==CH_ITEM){
                Art.itemIcon(g,id,x+7,ry+5);
                Art.textB(g,ItemData.name(id),x+36,ry+2,i==rewardChoiceSel?0xFFFFFF:0xD4DCE8);
                drawChoiceLines(g,choiceLinesA[i],x+36,ry+fh+2,0x9FB5CE);
            }else if(rewardChoiceKind==CH_TYPE){
                int px=x+7;
                for(int t=0;t<(run.mode==Run.MODE_MONOTYPE?1:3);t++){int type=poolChoiceTypes[i*3+t];Art.typeIcon(g,type,px,ry+3);px+=18;}
                int a=poolChoiceTypes[i*3],b=poolChoiceTypes[i*3+1],c=poolChoiceTypes[i*3+2];
                Art.textB(g,run.mode==Run.MODE_MONOTYPE?Lang.typeName(a):Lang.typeName(a)+" / "+Lang.typeName(b)+" / "+Lang.typeName(c),x+62,ry+3,i==rewardChoiceSel?0xFFFFFF:0xD4DCE8);
                Art.textSmall(g,Lang.t("FIRE: mở tùy chọn","FIRE: open actions"),x+7,ry+fh+4,0x9FB5CE);
            }else{
                if(id>=0)Art.avatar(g,id,x+5,ry+5);
                String name=id>=0?Data.name[id]:"-";
                Art.textB(g,name+"  ["+rarityVi(id)+"]",x+43,ry+1,i==rewardChoiceSel?0xFFFFFF:0xD4DCE8);
                if(id>=0){
                    Art.typeIcon(g,Data.t1[id],x+43,ry+fh+1);Art.typeIcon(g,Data.t2[id],x+59,ry+fh+1);
                    Art.textSmall(g,Lang.typeName(Data.t1[id])+" / "+Lang.typeName(Data.t2[id]),x+77,ry+fh+3,0x9FC5E0);
                    int py=drawChoiceLines(g,choiceLinesA[i],x+43,ry+fh*2+3,0xD5DEE8);
                    drawChoiceLines(g,choiceLinesB[i],x+43,py,0xA0FFA0);
                }
            }
            ry+=rowH;
        }
        g.setClip(oldX,oldY,oldW,oldH);
        String hint=rewardChoiceKind==CH_TYPE?Lang.t("↑↓ chọn  •  FIRE mở tùy chọn","↑↓ choose  •  FIRE actions"):Lang.t("↑↓ chọn  •  FIRE xác nhận","↑↓ choose  •  FIRE confirm");
        Art.textSmallC(g,hint,W/2,y+h-fh-3,0x90A8C8);
        if(typeChoiceAction)paintTypeChoiceAction(g);
        if(typeChoiceDetail)paintTypeChoiceDetail(g);
    }

    private void ensureChoiceLayout(int textW){
        if(choiceLayoutW==textW&&choiceLayoutKind==rewardChoiceKind)return;choiceLayoutW=textW;choiceLayoutKind=rewardChoiceKind;
        for(int i=0;i<3;i++){int id=rewardChoiceIds[i],lines=1;choiceLinesA[i]=choiceLinesB[i]=null;
            if(rewardChoiceKind==CH_TOWER)lines=3;
            else if(rewardChoiceKind==CH_ITEM){choiceLinesA[i]=Art.wrap("• "+ItemData.desc(id),textW,20);lines+=choiceLinesA[i].length;}
            else if(rewardChoiceKind==CH_TYPE)lines=2;
            else if(id>=0){String stats="• HP: "+Data.hp[id]+Lang.t("  Công: ","  ATK: ")+Data.atk[id]+Lang.t("  Thủ: ","  DEF: ")+Data.def[id]+Lang.t("  Kháng: ","  RES: ")+Data.speDef[id]+Lang.t("  Tốc: ","  SPD: ")+Data.speed[id];String skill="• "+Lang.t("Tuyệt kỹ: ","Ultimate: ")+Lang.moveName(Data.skillName[id])+" - "+AbilityBehavior.description(id);choiceLinesA[i]=Art.wrap(stats,textW,20);choiceLinesB[i]=Art.wrap(skill,textW,20);lines=2+choiceLinesA[i].length+choiceLinesB[i].length;}
            int h=lines*Art.fh+8;choiceRowCache[i]=h<42?42:h;
        }
    }

    private int drawChoiceLines(Graphics g,String[] lines,int x,int y,int color){if(lines==null)return y;for(int i=0;i<lines.length;i++){Art.textSmall(g,lines[i],x,y,color);y+=Art.fh;}return y;}

    private void paintTypeChoiceAction(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,w=Math.min(174,W-20),h=fh*4+16,x=(W-w)/2,y=(H-h)/2;
        Art.box(g,x,y,w,h,0x101830,0xFFD030);
        Art.textBC(g,rewardChoiceKind==CH_STARTER?Lang.t("POKÉMON ĐÃ CHỌN","SELECTED POKÉMON"):Lang.t("POOL ĐÃ CHỌN","SELECTED POOL"),W/2,y+4,0xFFD030);
        String[] a=rewardChoiceKind==CH_STARTER?new String[]{Lang.t("CHỌN","SELECT"),Lang.t("ĐÓNG","CLOSE")}:new String[]{Lang.t("CHỌN","SELECT"),Lang.t("CHI TIẾT","DETAILS")};int by=y+fh+9;
        for(int i=0;i<2;i++){g.setColor(i==typeChoiceActionSel?0x405273:0x202B40);g.fillRect(x+6,by+i*(fh+7),w-12,fh+5);if(i==typeChoiceActionSel){g.setColor(0xFFE060);g.drawRect(x+6,by+i*(fh+7),w-13,fh+4);}Art.textC(g,a[i],W/2,by+i*(fh+7)+2,0xFFFFFF);}
    }

    private void paintTypeChoiceDetail(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,w=Math.min(W-12,286),x=(W-w)/2,y=5,h=H-10;
        Art.box(g,x,y,w,h,0x101830,0xFFD030);Art.textBC(g,Lang.t("CHI TIẾT POOL HỆ","TYPE POOL DETAILS"),W/2,y+4,0xFFD030);
        int yy=y+fh+9,base=rewardChoiceSel*3;
        for(int i=0;i<3;i++){
            int type=poolChoiceTypes[base+i];Art.typeIcon(g,type,x+7,yy);Art.textB(g,Lang.typeName(type),x+27,yy+1,0xFFFFFF);yy+=fh+5;
            yy=Art.para(g,"• "+Lang.synergyLongDesc(type),x+9,yy,w-18,0xB8CDE4,20)+4;
        }
        Art.textSmallC(g,Lang.t("FIRE/0: quay lại","FIRE/0: back"),W/2,y+h-fh-3,0x8090B0);
    }

    private String rarityVi(int sp){
        if(sp<0)return "";if(Save.language==Lang.EN)return Data.CATEGORY_NAME[Data.category[sp]];
        String[] n={"Phổ biến","Ít gặp","Hiếm","Sử thi","Siêu hiếm","Độc nhất","Huyền thoại","Đặc biệt"};
        return n[Data.category[sp]];
    }

    private void paintRefreshItemsAsk(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,w=Math.min(W-16,174),h=fh*6+14,x=(W-w)/2,y=(H-h)/2;
        Art.box(g,x,y,w,h,0x101830,0xFFD030);
        Art.textBC(g,Lang.t("9 MẢNH VÒNG MỚI","9 NEW COMPONENTS"),W/2,y+4,0xFFD030);
        Art.textSmallC(g,Lang.t("Có bỏ toàn bộ vật phẩm cũ?","Discard all old items?"),W/2,y+fh+8,0xD8E0F0);
        String[] a={Lang.t("BỎ ĐỒ CŨ","DISCARD"),Lang.t("GIỮ ĐỒ CŨ","KEEP")};
        int by=y+fh*3+8,bw=(w-15)/2;
        for(int i=0;i<2;i++){int bx=x+5+i*(bw+5);g.setColor(i==refreshItemsChoice?0x405273:0x29374D);g.fillRect(bx,by,bw,fh+6);if(i==refreshItemsChoice){g.setColor(0xFFE060);g.drawRect(bx,by,bw-1,fh+5);}Art.textSmallC(g,a[i],bx+bw/2,by+3,i==refreshItemsChoice?0xFFFFFF:0xA8B5C8);}
        Art.textSmallC(g,Lang.t("FIRE xác nhận","FIRE confirm"),W/2,y+h-fh-3,0x8090B0);
    }

    private void paintOver(Graphics g) {
        int W = game.W, H = game.H, fh = Art.fh;
        g.setColor(0x101830);
        g.fillRect(0, 0, W, H);
        int y = 3;
        Art.textBC(g, run.victory ? Lang.t("NHÀ VÔ ĐỊCH!","CHAMPION!") : Lang.t("KẾT THÚC","RUN OVER"), W / 2, y, run.victory ? 0xFFD030 : 0xFF6060);
        y+=fh+3;
        Art.textSmallC(g,Lang.t("Vòng ","Round ")+run.round+(run.endlessMode()?" / "+Lang.t("Vô tận","Endless"):"/"+run.maxRound())+"   HP "+run.hp+"   "+Lang.t("Vàng ","Gold ")+run.gold,W/2,y,0xE0E8FF);
        y+=fh+2;HistoryScreen.drawGraph(g,run.resultPath,run.resultCount,4,y,W-8,38);y+=41;
        Art.textB(g,Lang.t("ĐỘI HÌNH CUỐI","FINAL TEAM"),4,y,0x80D8FF);y+=fh+2;
        int n=run.boardCount(),slot=0,per=H<250?7:5,step=H<250?23:34;
        for(int i=0;i<Run.BOARD;i++)if(run.board[i]>=0){
            int x=3+(slot%per)*step,ay=y+(slot/per)*step;
            PetAvatar.draw(g,run.board[i],run.specialFormAt(i),run.isMega(i),run.isShiny(i),x,ay,H<250?20:32);slot++;
        }
        y+=((n+per-1)/per)*step;
        int[] cnt=synCounts;fillSynCounts(cnt);
        Art.textB(g,Lang.t("CỘNG HƯỞNG","SYNERGIES"),4,y,0x80D8FF);y+=fh+1;
        int sx=4,sy=y;
        for(int t=0;t<Data.NT;t++)if(Data.synLevel(t,cnt[t])>0){
            if(sx+34>W){sx=4;sy+=17;}Art.typeIcon(g,t,sx,sy);Art.textSmall(g,""+cnt[t],sx+16,sy+2,0xFFD060);sx+=34;
        }
        int infoY=Math.max(sy+18,H-fh*3-2);
        BallArt.amount(g,BallArt.NORMAL,reward,8,infoY,0xFFB070);Art.textSmallR(g,Lang.t("Tốt nhất: ","Best: ")+Save.best,W-8,infoY,0xFFB070);
        Art.textC(g,Lang.t("FIRE: về menu","FIRE: menu"),W/2,H-fh-2,0x8090B0);
    }
}

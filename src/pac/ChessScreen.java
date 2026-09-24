package pac;

import javax.microedition.lcdui.Graphics;

/** Mode 2: offline auto chess run against AI teams, using unlocked (collected) families only. */
public final class ChessScreen extends Screen {
    static final int PREP = 0, BATTLE = 1, RESULT = 2, OVER = 3;
    static final int TICK_MS = CombatRules.TICK_MS;
    static final String[] BTN = { "XP", "Roll", "More", "Syn", "GO" };

    private final Run run;
    private final boolean unlimitedGold;
    private int state = PREP;

    // cursor: 0 board, 1 bench, 2 shop, 3 buttons, 4 item reserve, 5 battle team
    private int zone = 2, col = 0, row = 0;
    private int held = -1;

    private Battle bt;
    private final FixedStepClock simClock=new FixedStepClock(TICK_MS);
    private int endT = 0, speed = 1;
    private boolean endReady = false;
    private String toast = "";
    private int toastT = 0;
    private boolean showSyn = false;
    private int synCursor = 0;
    private boolean showMore = false;
    private int moreSel = 0;
    private boolean showItems = false;
    private int itemSel = 0, itemTarget = -1;
    private int dockItemCursor=0,dockItemScroll=0,dockTeamCursor=0;
    private int dockMode=0,dockSynCursor=0;
    private int pendingItemA=-1,pendingItemB=-1,pendingMade=-1,dockDetailItem=-1;
    private boolean dockDetail=false;
    private boolean itemActionMenu=false;
    private int itemAction=0;
    private boolean petActionMenu=false,dockPetDetail=false;
    private int petAction=0,dockPetPos=-1;
    private boolean showCraft=false;
    private int craftTab=0,craftFocus=0,craftItemCursor=0,craftItemScroll=0;
    private int craftA=-1,craftB=-1,craftRecipeCursor=0,craftRecipeScroll=0;
    private boolean detailReturnCraft=false;
    private boolean quitArm = false;
    private boolean warned = false;
    private int reward = 0;
    private boolean finished = false;
    private int visualTime = 0;
    private int saveClock = 0;
    private int watch = 0;
    private boolean rosterDetail = false;
    private boolean refreshItemsAsk=false;
    private int refreshItemsChoice=0;
    private int rosterSide = 0, rosterSel = 0, statMode = 0, battleSynCursor = 0;
    private boolean battleMore=false;
    /** True while the arrow-key cursor is on the combat-stat tabs. */
    private boolean statFocus = false;
    private final int[] spriteBounds = new int[4];
    /** Reused by paint/input paths to avoid garbage-collector spikes on CLDC phones. */
    private final int[] synCounts = new int[Data.NT];
    private final boolean[] synFamilies = new boolean[Data.N];

    // layout (recomputed every frame)
    private int cell, hudH, boardY, benchY, shopY, shopH, btnY, btnH, infoY, cw;

    public ChessScreen(Game g) {
        this(g, false);
    }

    public ChessScreen(Game g, boolean unlimited) {
        this(g,unlimited?Run.MODE_UNLIMITED:Run.MODE_NORMAL);
    }

    public ChessScreen(Game g,int mode) {
        super(g);
        unlimitedGold = mode==Run.MODE_UNLIMITED;
        run = new Run((int) System.currentTimeMillis(),mode);
        RunStorage.save(run);
    }

    public ChessScreen(Game g,Run resumed){super(g);run=resumed;unlimitedGold=run.mode==Run.MODE_UNLIMITED;state=PREP;}
    public void saveResume(){if(!finished&&state==PREP)RunStorage.save(run);}

    private void say(String s) {
        toast = s;
        toastT = 2200;
    }

    // ---- update ----------------------------------------------------------

    public void update(int dt) {
        visualTime += dt;
        if(state==PREP){saveClock+=dt;if(saveClock>=3000){saveClock=0;RunStorage.save(run);}}
        if (toastT > 0) toastT -= dt;
        if (state == BATTLE && !rosterDetail) {
            if (!bt.over) {
                simClock.add(dt,speed);
                while (simClock.ready() && !bt.over) {
                    simClock.consume();
                    bt.step();
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
        reward = 2 + (run.round - 1) / 2 + (run.victory ? 10 : 0);
        Save.balls += reward;
        Save.runs++;
        if (run.victory) Save.wins++;
        if (run.round > Save.best) Save.best = run.round;
        Save.save();
        HistoryStore.add(run);
        RunStorage.clear();
    }

    // ---- input -----------------------------------------------------------

    public void key(int k) {
        if(refreshItemsAsk){keyRefreshItems(k);return;}
        if(showMore){keyMore(k);return;}
        if(showCraft){keyCraft(k);return;}
        if(dockPetDetail){if(k==Game.K_FIRE||k==Game.K_0||k==Game.K_POUND||k==Game.K_SOFT1||k==Game.K_SOFT2)dockPetDetail=false;return;}
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
                    if (run.over) state = OVER;
                    else if(run.mode==Run.MODE_THIRTY){refreshItemsAsk=true;refreshItemsChoice=0;}
                    else continueNextRound();
                }
                break;
            default:
                if (k == Game.K_FIRE || k == Game.K_SOFT1 || k == Game.K_SOFT2) {
                    game.setScreen(new MenuScreen(game));
                }
                break;
        }
    }

    private void continueNextRound(){run.nextRound();state=PREP;warned=false;held=-1;RunStorage.save(run);}

    private void keyRefreshItems(int k){
        if(k==Game.K_LEFT||k==Game.K_UP)refreshItemsChoice=0;
        else if(k==Game.K_RIGHT||k==Game.K_DOWN)refreshItemsChoice=1;
        else if(k==Game.K_FIRE||k==Game.K_SOFT1){run.refreshNineItems(refreshItemsChoice==0);refreshItemsAsk=false;continueNextRound();}
    }

    private void keyBattle(int k) {
        if(bt.over){
            if(endReady&&(k==Game.K_FIRE||k==Game.K_SOFT1||k==Game.K_9))endBattle();
            if(k==Game.K_FIRE||k==Game.K_SOFT1||k==Game.K_9)return;
        }
        if (rosterDetail) { keyRoster(k); return; }
        if(battleMore){
            if(k==Game.K_FIRE||k==Game.K_SOFT1){statMode=5;battleMore=false;statFocus=false;}
            else if(k==Game.K_0||k==Game.K_SOFT2||k==Game.K_POUND)battleMore=false;
            return;
        }
        int count=rosterCount(rosterSide);
        if (k == Game.K_FIRE && count>0 && (statMode<3||statMode==4)) rosterDetail=true;
        else if (k == Game.K_STAR) speed = speed == 1 ? 2 : (speed == 2 ? 4 : 1);
        else if (k == Game.K_0) battleMore=true;
        else if (k == Game.K_1) statMode=(statMode+5)%6;
        else if (k == Game.K_3) statMode=(statMode+1)%6;
        else if (k == Game.K_7) {rosterSide=1-rosterSide;rosterSel=0;battleSynCursor=0;}
        else if (statFocus && k == Game.K_LEFT) statMode=(statMode+5)%6;
        else if (statFocus && k == Game.K_RIGHT) statMode=(statMode+1)%6;
        else if (statFocus && k == Game.K_DOWN) statFocus=false;
        else if (statFocus && k == Game.K_UP) {rosterSide=1-rosterSide;rosterSel=0;battleSynCursor=0;}
        else if (statMode==3 && (k==Game.K_LEFT||k==Game.K_UP)) moveBattleSyn(-1);
        else if (statMode==3 && (k==Game.K_RIGHT||k==Game.K_DOWN)) moveBattleSyn(1);
        else if (k == Game.K_LEFT && rosterSel>0) rosterSel--;
        else if (k == Game.K_RIGHT && rosterSel+1<count) rosterSel++;
        else if (k == Game.K_UP && rosterSel>=3) rosterSel-=3;
        else if (k == Game.K_UP) statFocus=true;
        else if (k == Game.K_DOWN && rosterSel+3<count) rosterSel+=3;
        else if (k == Game.K_9 || k == Game.K_SOFT1) {
            int guard = 0;
            while (!bt.over && guard++ < 1000) bt.step();
        }
        syncWatchToRoster();
    }

    private void keySyn(int k){
        int n=presentSynCount();
        if(k==Game.K_0||k==Game.K_POUND||k==Game.K_SOFT2||k==Game.K_FIRE||k==Game.K_SOFT1){showSyn=false;return;}
        if(n<=0)return;
        if(k==Game.K_UP||k==Game.K_LEFT)synCursor--;
        else if(k==Game.K_DOWN||k==Game.K_RIGHT)synCursor++;
        if(synCursor<0)synCursor=n-1;if(synCursor>=n)synCursor=0;
    }

    private void keyMore(int k){
        if(k==Game.K_UP||k==Game.K_LEFT){moreSel--;if(moreSel<0)moreSel=3;}
        else if(k==Game.K_DOWN||k==Game.K_RIGHT){moreSel++;if(moreSel>3)moreSel=0;}
        else if(k==Game.K_0||k==Game.K_POUND||k==Game.K_SOFT2)showMore=false;
        else if(k==Game.K_FIRE||k==Game.K_SOFT1){
            showMore=false;
            if(moreSel==0){run.toggleShopLock();say(run.msg);}
            else if(moreSel==1){showCraft=true;craftTab=0;craftFocus=-1;craftItemCursor=craftItemScroll=0;craftA=craftB=-1;}
            else{dockMode=moreSel==2?0:1;zone=4;if(dockMode==1)dockSynCursor=0;}
        }
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
                if(zone<=1&&run.get(pos())>=0)openItemBag(pos());
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
        if(k==Game.K_0||k==Game.K_POUND||k==Game.K_SOFT2){showCraft=false;return;}
        if(k==Game.K_1){craftTab=0;craftFocus=-1;return;}
        if(k==Game.K_3){craftTab=1;craftFocus=-1;return;}
        if(craftFocus<0){
            if(k==Game.K_LEFT){craftTab=0;return;}
            if(k==Game.K_RIGHT){craftTab=1;return;}
            if(k==Game.K_DOWN||k==Game.K_FIRE){craftFocus=0;return;}
            return;
        }
        if(craftTab==1){
            int n=craftCatalogCount(),cols=Math.max(1,(game.W-16)/28);
            if(k==Game.K_LEFT&&craftRecipeCursor>0)craftRecipeCursor--;
            else if(k==Game.K_RIGHT&&craftRecipeCursor+1<n)craftRecipeCursor++;
            else if(k==Game.K_UP){if(craftRecipeCursor<cols)craftFocus=-1;else craftRecipeCursor-=cols;}
            else if(k==Game.K_DOWN&&craftRecipeCursor+cols<n)craftRecipeCursor+=cols;
            else if(k==Game.K_FIRE||k==Game.K_SOFT1){int id=craftCatalogAt(craftRecipeCursor);if(id>=0){dockDetailItem=id;detailReturnCraft=true;dockDetail=true;showCraft=false;}}
            return;
        }
        if(k==Game.K_UP){if(craftFocus==0)craftFocus=-1;else craftFocus=0;return;}
        if(k==Game.K_DOWN){if(craftFocus==0)craftFocus=1;return;}
        if(craftFocus>=1&&craftFocus<=3&&(k==Game.K_LEFT||k==Game.K_RIGHT)){
            craftFocus+=k==Game.K_LEFT?-1:1;if(craftFocus<1)craftFocus=3;if(craftFocus>3)craftFocus=1;return;
        }
        if(craftFocus==0&&(k==Game.K_LEFT||k==Game.K_RIGHT)){
            int n=ownedItemCount();if(n>0){craftItemCursor+=k==Game.K_LEFT?-1:1;if(craftItemCursor<0)craftItemCursor=0;if(craftItemCursor>=n)craftItemCursor=n-1;}return;
        }
        if(k==Game.K_FIRE||k==Game.K_SOFT1){
            if(craftFocus==0){
                int id=ownedItemAt(craftItemCursor);if(id<0){say(Lang.t("Kho trống","Inventory empty"));return;}
                int used=(craftA==id?1:0)+(craftB==id?1:0),owned=run.itemCount(id);
                if(used>=owned){
                    if(craftB==id)craftB=-1;else if(craftA==id)craftA=-1;
                    say(Lang.t("Đã lấy ra: ","Removed: ")+ItemData.name(id));
                }else if(craftA<0){craftA=id;say(Lang.t("Nguyên liệu A: ","Ingredient A: ")+ItemData.name(id));}
                else if(craftB<0){craftB=id;int out=ItemData.crafted(craftA,craftB);say(out>=0?Lang.t("Kết quả: ","Result: ")+ItemData.name(out):Lang.t("Không có công thức","No recipe"));}
                else{craftA=id;craftB=-1;say(Lang.t("Chọn lại nguyên liệu A","Ingredient A replaced"));}
            }else if(craftFocus==1){
                if(craftA<0||craftB<0){say(Lang.t("Hãy chọn đủ hai nguyên liệu","Choose two ingredients"));return;}
                boolean ok=run.craftItems(craftA,craftB);say(run.msg);
                if(ok){craftA=craftB=-1;int n=ownedItemCount();if(craftItemCursor>=n)craftItemCursor=Math.max(0,n-1);craftFocus=0;}
            }else if(craftFocus==2){
                craftA=craftB=-1;craftFocus=0;
                say(Lang.t("Đã làm mới ô ghép","Craft slots cleared"));
            }else{
                showCraft=false;
            }
        }
    }

    private void selectDockItem(int id){
        if(id<0||run.itemCount(id)<=0)return;
        // Crafting is reserved for the next dedicated Item flow.
        pendingItemA=id;pendingItemB=-1;pendingMade=-1;
        say(Lang.t("Đã cầm ","Holding ")+ItemData.name(id));
    }

    private void keyPetAction(int k){
        if(k==Game.K_UP||k==Game.K_LEFT)petAction=0;
        else if(k==Game.K_DOWN||k==Game.K_RIGHT)petAction=1;
        else if(k==Game.K_0||k==Game.K_POUND||k==Game.K_SOFT2)petActionMenu=false;
        else if(k==Game.K_FIRE||k==Game.K_SOFT1){petActionMenu=false;if(petAction==0)dockPetDetail=true;else equipPendingToDockPet();}
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
            else say(Lang.t("Túi vật phẩm trống","Item bag is empty"));
        } else if(k==Game.K_POUND||k==Game.K_0||k==Game.K_SOFT2)showItems=false;
    }

    private void act() {
        if (zone <= 1) {
            int p = pos();
            if (held < 0) {
                if (run.get(p) >= 0) held = p; else say(Lang.t("Ô trống", "Empty slot"));
            } else if (p == held) {
                held = -1;
            } else {
                if (!run.move(held, p)) say(run.msg);
                held = -1;
            }
        } else if (zone == 2) {
            if (held >= 0) doSell();
            else if (!run.buy(col)) say(run.msg);
        } else if(zone==3) {
            switch (col) {
                case 0: doXp(); break;
                case 1: doReroll(); break;
                case 2: showMore=true;moreSel=0;break;
                case 3: openSyn();break;
                default: doFight(); break;
            }
        } else if(zone==4){
            if(dockMode==0){int id=ownedItemAt(dockItemCursor);if(id>=0){dockDetailItem=id;itemAction=0;itemActionMenu=true;}else say(Lang.t("Kho trang bị trống","Item reserve is empty"));}
        } else if(zone==5){
            int p=boardPosAt(dockTeamCursor);
            if(p>=0){dockPetPos=p;petAction=0;petActionMenu=true;}
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
        run.sell(p);
        held = -1;
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
        held = -1;
        bt = run.makeBattle();
        watch = 0;
        for (int i = 0; i < bt.n; i++) if (bt.units[i].side == 0) { watch = i; break; }
        simClock.reset();
        endT = 0;
        endReady = false;
        state = BATTLE;
    }

    // ---- layout ----------------------------------------------------------

    private void layout() {
        int W = game.W, H = game.H, fh = Art.fh;
        cell = W / 8;
        while (true) {
            hudH = fh * 2 + 4;
            shopH = cell + fh + 2;
            btnH = fh + 4;
            // Reserve two item/team rows plus the original information panel.
            int need = hudH + 3 * cell + 2 + cell + 2 + shopH + 2 + btnH + 2 + 55 + fh * 3;
            if (need <= H || cell <= 10) break;
            cell--;
        }
        boardY = hudH;
        benchY = boardY + 3 * cell + 2;
        shopY = benchY + cell + 2;
        btnY = shopY + shopH + 2;
        infoY = btnY + btnH + 2;
        cw = W / 5;
    }

    // ---- painting --------------------------------------------------------

    public void paint(Graphics g) {
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
        if (showItems) paintItemBag(g);
        if (showCraft) paintCraft(g);
        if (dockDetail) paintDockDetail(g);
        if (itemActionMenu) paintItemActionMenu(g);
        if (dockPetDetail) paintDockPetDetail(g);
        if (petActionMenu) paintPetActionMenu(g);
        if (state==BATTLE&&rosterDetail) paintRosterDetail(g);
        if(refreshItemsAsk)paintRefreshItemsAsk(g);
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
        Art.text(g, Lang.t("Vòng ", "Round ") + run.round + "/" + run.maxRound(), 3, 1, 0xFFFFFF);
        Art.uiIcon(g,1,W-43,0); Art.textR(g,""+run.hp,W-3,1,run.hp>40?0x80FF80:0xFF6060);
        Art.uiIcon(g,7,2,fh+1);
        Art.text(g, "Lv" + run.level + " " + (run.level >= Data.MAX_LEVEL ? "MAX" : run.xp + "/" + Data.XP_NEED[run.level])
                + "  " + run.boardCount() + "/" + run.level, 18, fh + 2, 0xB0C8FF);
        String money=unlimitedGold?"INF":""+run.gold;int moneyX=W-3-Art.textWidth(money)-9;
        Art.textB(g,"$",moneyX,fh+1,0xFFD030);Art.textR(g,money,W-3,fh+2,0xFFD030);

        int bx = (W - 8 * cell) / 2;
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
        // Pass 2: draw all units after all backgrounds. Lower rows are painted
        // later, giving large sprites a stable natural depth order.
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 8; c++) {
                int sp = run.board[r * 8 + c];
                if (sp >= 0) {
                    int p=r*8+c,x=bx+c*cell,y=boardY+r*cell;
                    drawSetupUnit(g,sp,x,y,cell,p);
                    drawEquippedItems(g,p,x,y,cell);
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
        // held marker
        if (held >= 0) {
            int hx, hy;
            if (held < Run.BOARD) { hx = bx + (held % 8) * cell; hy = boardY + (held / 8) * cell; }
            else { hx = bx + (held - Run.BOARD) * cell; hy = benchY; }
            g.setColor(0x40A0FF);
            g.drawRect(hx, hy, cell - 1, cell - 1);
            g.drawRect(hx + 1, hy + 1, cell - 3, cell - 3);
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
                Art.avatar(g, sp, x + (cw - 34) / 2, y);
                boolean afford = run.gold >= Data.cost[sp];
                Art.textB(g,"$",x+2,y+shopH-fh-1,afford?0xFFD030:0x805040);
                Art.text(g, "" + Data.cost[sp], x + 11, y + shopH - fh - 1, afford ? 0xFFD030 : 0x805040);
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
            g.drawRect(cx + 1, cy + 1, cwid - 3, chei - 3);
        }
        if (held >= 0 && zone <= 1) {
            int hs = run.get(held);
            // The drag preview must use the same raw-atlas frame, canvas and
            // bottom anchor as board/bench units. Art.sprite() is the small
            // static collection icon and made a held Gen-1 Pokemon collapse.
            if (hs >= 0) drawSetupUnit(g, hs, cx, cy, cell, held + 37);
        }

        paintPrepDock(g,infoY,53);
        paintPrepInfo(g,infoY+55,H-(infoY+55));
    }

    private void drawSetupUnit(Graphics g, int sp, int x, int y, int size, int phase) {
        int frame = visualTime / 90 + phase;
        Art.formationSprite(g,sp,x,y,size,size,game.W,frame);
        drawEvolutionDots(g,sp,x+2,y+size-3);
    }

    private int familyMaxTier(int sp){int max=1,f=Data.fam[sp];for(int i=0;i<Data.N;i++)if(Data.fam[i]==f&&Data.tier[i]>max)max=Data.tier[i];return max;}

    private void drawEvolutionDots(Graphics g,int sp,int x,int y){
        int cur=Data.tier[sp],max=familyMaxTier(sp);
        for(int i=0;i<max;i++){
            g.setColor(0x172030);g.fillRect(x+i*6,y-1,5,5);
            g.setColor(i<cur?0x62E66C:0xFFD030);g.fillRect(x+1+i*6,y,3,3);
        }
    }

    private void drawEvolutionChain(Graphics g,int sp,int x,int y){
        int f=Data.fam[sp],max=familyMaxTier(sp),px=x;
        Art.textSmall(g,Lang.t("Tiến hóa:","Evolution:"),px,y+5,0x90A8C8);px+=Art.smallWidth(Lang.t("Tiến hóa:","Evolution:"))+4;
        for(int tier=1;tier<=max;tier++){
            int evo=-1;for(int i=0;i<Data.N;i++)if(Data.fam[i]==f&&Data.tier[i]==tier){evo=i;break;}
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
            int total=ownedItemCount(),visible=Math.max(1,(W-24)/25);
            if(dockItemCursor>=total)dockItemCursor=Math.max(0,total-1);
            if(dockItemCursor<dockItemScroll)dockItemScroll=dockItemCursor;
            if(dockItemCursor>=dockItemScroll+visible)dockItemScroll=dockItemCursor-visible+1;
            if(total<=visible)dockItemScroll=0;
            Art.text(g,"<",2,y+5,total>visible&&dockItemScroll>0?0xFFFFFF:0x586478);Art.textR(g,">",W-2,y+5,total>visible&&dockItemScroll+visible<total?0xFFFFFF:0x586478);
            for(int i=0;i<visible;i++){
                int rank=dockItemScroll+i,id=ownedItemAt(rank),x=12+i*25;
                boolean focus=id>=0&&rank==dockItemCursor&&zone==4,picked=id>=0&&(id==pendingItemA||id==pendingItemB);
                g.setColor(0x202735);g.fillArc(x,y,24,24,0,360);
                if(id>=0){Art.itemIcon(g,id,x,y);int q=run.itemCount(id);String qs="x"+q;Art.textSmall(g,qs,x+2,y+14,0x101827);Art.textSmall(g,qs,x+1,y+13,q==1?0xFFE080:0x7FE8FF);}
                g.setColor(focus?0xFFE060:(picked?0x50D8FF:0x4B586C));g.drawArc(x,y,23,23,0,360);if(focus||picked)g.drawArc(x+1,y+1,21,21,0,360);
            }
            g.setColor(0x303B50);g.fillRect(12,sy,trackW,2);if(total>0){int thumb=Math.max(8,trackW*Math.min(visible,total)/total);int max=trackW-thumb;int tx=12+(total<=visible?0:max*dockItemScroll/Math.max(1,total-visible));g.setColor(0x70A8FF);g.fillRect(tx,sy,thumb,2);}
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
            Art.avatarDock(g,sp,x,ty);
            for(int s=0;s<3;s++){int id=run.itemAt(p,s);if(id>=0)Art.itemIconTiny(g,id,x+s*7,ty+14);}
            g.setColor(i==dockTeamCursor&&zone==5?0xFFE060:0x7C8790);g.drawRect(x,ty,21,21);
        }
        if(n==0)Art.textSmallC(g,Lang.t("Đưa Pokemon lên sân để trang bị","Deploy Pokemon to equip"),W/2,ty+5,0x8090A8);
        if(pendingItemA>=0){int id=pendingMade>=0?pendingMade:pendingItemA;Art.itemIconTiny(g,id,W-11,ty+1);}
    }

    /** Restored legacy information area below the new reserve/team dock. */
    private void paintPrepInfo(Graphics g,int y,int h){
        int fh=Art.fh,avail=h/fh,sp=-1;boolean shop=false;
        if(h<=0)return;
        if(toastT>0){Art.textSmall(g,toast,3,y,0xFFB070);y+=fh;avail--;}
        if(zone<=1)sp=run.get(pos());
        else if(zone==2){sp=run.shop[col];shop=true;}
        else if(zone==5){int p=boardPosAt(dockTeamCursor);if(p>=0)sp=run.get(p);}
        if(sp>=0&&avail>=2)drawInfo(g,sp,y,avail,shop);
        else if(zone==4&&dockMode==0&&ownedItemCount()>0&&avail>0){int id=ownedItemAt(dockItemCursor);Art.textSmall(g,ItemData.name(id)+" x"+run.itemCount(id),3,y,0xD8E0F0);}
        else if(zone==4&&dockMode==1&&avail>0){int[] cnt=synCounts;fillSynCounts(cnt);int t=presentSynTypeAt(dockSynCursor,cnt);if(t>=0){int lv=Data.synLevel(t,cnt[t]);String mark=SynergyEffects.marks(t,cnt[t]);Art.textSmall(g,Lang.typeName(t)+"  "+cnt[t]+"  "+mark,3,y,lv>0?0xFFD060:0xA8B5C8);if(avail>1)Art.para(g,Lang.synergyLongDesc(t),3,y+fh,game.W-6,lv>0?0xD8E8FF:0x8090A8,avail-1);}}
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
        int W=game.W,H=game.H,fh=Art.fh,w=Math.min(158,W-18),h=fh*6+16,x=(W-w)/2,y=(H-h)/2;
        Art.box(g,x,y,w,h,0x101830,0xFFD030);
        Art.textBC(g,"MORE",W/2,y+3,0xFFD030);
        String lock=run.shopLocked?Lang.t("Mở khóa shop","Unlock shop"):Lang.t("Khóa shop vòng sau","Lock next shop");
        String[] a={lock,Lang.t("Ghép đồ","Craft items"),Lang.t("Túi vật phẩm","Item bag"),Lang.t("Hệ cộng hưởng","Synergy dock")};
        for(int i=0;i<4;i++){int ry=y+fh+6+i*(fh+3);if(i==moreSel){g.setColor(0x405273);g.fillRect(x+5,ry,w-10,fh+2);}Art.textC(g,a[i],W/2,ry+1,i==moreSel?0xFFFFFF:0xA8B5C8);}
    }

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
        Art.textSmallC(g,Lang.t("1/3 đổi tab","1/3 tabs"),W/2,H-fh-4,0x8090B0);
    }

    private void paintCraftBuild(Graphics g,int y,int h){
        int W=game.W,fh=Art.fh,recipe=craftA>=0&&craftB>=0?ItemData.crafted(craftA,craftB):-1;
        int out=recipe>=0&&hasCraftMaterials(craftA,craftB)?recipe:-1;
        int sx=W/2-54;
        drawCraftSlot(g,craftA,sx,y);Art.textC(g,"+",sx+34,y+7,0xFFFFFF);
        drawCraftSlot(g,craftB,sx+48,y);Art.textC(g,"=",sx+82,y+7,0xFFFFFF);
        drawCraftSlot(g,out,sx+96,y);
        if(craftA>=0&&craftB>=0)Art.textSmallC(g,ItemData.name(craftA)+" + "+ItemData.name(craftB),W/2,y+27,0xB8C8D8);
        else if(craftA>=0)Art.textSmallC(g,ItemData.name(craftA)+" + ?",W/2,y+27,0xB8C8D8);
        if(out>=0)Art.textSmallC(g,"= "+ItemData.name(out),W/2,y+27+fh,0xFFD060);
        int iy=y+fh*3+20,total=ownedItemCount(),visible=Math.max(1,(W-16)/27);
        if(craftItemCursor>=total)craftItemCursor=Math.max(0,total-1);
        if(craftItemCursor<craftItemScroll)craftItemScroll=craftItemCursor;
        if(craftItemCursor>=craftItemScroll+visible)craftItemScroll=craftItemCursor-visible+1;
        if(total<=visible)craftItemScroll=0;
        for(int i=0;i<visible;i++){
            int rank=craftItemScroll+i,id=ownedItemAt(rank),x=8+i*27;
            g.setColor(0x202735);g.fillArc(x,iy,24,24,0,360);if(id>=0){Art.itemIcon(g,id,x,iy);int q=run.itemCount(id);String qs="x"+q;Art.textSmall(g,qs,x+2,iy+14,0x101827);Art.textSmall(g,qs,x+1,iy+13,q==1?0xFFE080:0x7FE8FF);}
            g.setColor(craftFocus==0&&rank==craftItemCursor?0xFFE060:0x4B586C);g.drawArc(x,iy,23,23,0,360);
        }
        int by=iy+31,gap=3,bw=(W-20-gap*2)/3,bx=7,bx2=bx+bw+gap,bx3=bx2+bw+gap;boolean ready=out>=0&&hasCraftMaterials(craftA,craftB),canReset=craftA>=0||craftB>=0;
        g.setColor(craftFocus==1?(ready?0xC59112:0x51472F):0x35455F);g.fillRect(bx,by,bw,fh+6);
        if(craftFocus==1){g.setColor(0xFFE060);g.drawRect(bx,by,bw-1,fh+5);}
        Art.textSmallC(g,Lang.t("GHÉP","CRAFT"),bx+bw/2,by+3,ready?0xFFFFFF:0x777B82);
        g.setColor(craftFocus==2?(canReset?0x596D8D:0x454D5B):0x35455F);g.fillRect(bx2,by,bw,fh+6);
        if(craftFocus==2){g.setColor(0xFFE060);g.drawRect(bx2,by,bw-1,fh+5);}
        Art.textSmallC(g,Lang.t("LÀM MỚI","RESET"),bx2+bw/2,by+3,canReset?0xFFFFFF:0x777B82);
        g.setColor(craftFocus==3?0x596D8D:0x35455F);g.fillRect(bx3,by,bw,fh+6);
        if(craftFocus==3){g.setColor(0xFFE060);g.drawRect(bx3,by,bw-1,fh+5);}
        Art.textSmallC(g,Lang.t("ĐÓNG","CLOSE"),bx3+bw/2,by+3,craftFocus==3?0xFFFFFF:0xC8D0E0);
        if(out<0&&craftA>=0&&craftB>=0)Art.textSmallC(g,recipe>=0?Lang.t("Không đủ số lượng","Not enough copies"):Lang.t("Hai món không ghép được","No matching recipe"),W/2,by+fh+10,0xFF7070);
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

    private void paintDockPetDetail(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,p=dockPetPos,sp=p>=0?run.get(p):-1;if(sp<0){dockPetDetail=false;return;}
        Art.box(g,4,4,W-8,H-8,0x101830,0xFFD030);
        Art.avatar(g,sp,8,9);Art.textB(g,Data.name[sp],45,9,0xFFFFFF);
        Art.typeIcon(g,Data.t1[sp],45,9+fh);if(Data.t2[sp]>=0)Art.typeIcon(g,Data.t2[sp],61,9+fh);
        int y=48;
        Art.textSmall(g,"HP: "+Data.hp[sp]+Lang.t("  Công: ","  ATK: ")+Data.atk[sp]+Lang.t("  Thủ: ","  DEF: ")+Data.def[sp]+Lang.t("  Kháng: ","  SP.DEF: ")+Data.speDef[sp],8,y,0xD8E0F0);y+=fh;
        Art.textSmall(g,"PP: "+Data.mana[sp]+Lang.t("  Tốc: ","  SPD: ")+Data.speed[sp]+Lang.t("  Tầm: ","  RNG: ")+Data.range[sp]+Lang.t("  Hồi chiêu: ","  CD: ")+Data.cd[sp],8,y,0xD8E0F0);y+=fh+2;
        drawEvolutionChain(g,sp,8,y);y+=27;
        Art.textB(g,Lang.moveName(Data.skillName[sp]),8,y,0xA0FFA0);y+=fh;
        y=Art.para(g,AbilityBehavior.description(sp),8,y,W-16,0xA0D8A0,2)+3;
        paintEquipmentBlock(g,8,y,run.itemAt(p,0),run.itemAt(p,1),run.itemAt(p,2));
        Art.textSmallC(g,Lang.t("FIRE / 0: đóng","FIRE / 0: close"),W/2,H-fh-6,0x8090B0);
    }

    private void paintDockDetail(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,id=dockDetailItem;
        Art.box(g,4,4,W-8,H-8,0x101830,0xFFD030);
        Art.itemIcon(g,id,9,9);Art.textB(g,ItemData.name(id),38,9,0xFFD060);
        Art.textSmall(g,ItemData.kind(id)+"  x"+run.itemCount(id),38,9+fh,0x91A8C8);
        int y=39,recipeY=H-fh*3-60,lines=Math.max(2,(recipeY-y-2)/fh);
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
            int max=Math.min(n,(W-16)/17);
            Art.textSmall(g,Lang.t("Ghép với:","Combine with:")+" "+n,8,y,0x90C8FF);y+=11;
            for(int i=0;i<max;i++){int out=ItemData.outputAt(id,i),partner=ItemData.RECIPE_A[out]==id?ItemData.RECIPE_B[out]:ItemData.RECIPE_A[out];Art.itemIconMedium(g,partner,8+i*17,y);}y+=18;
            Art.textSmall(g,Lang.t("Thành:","Creates:"),8,y,0xFFD060);y+=11;
            for(int i=0;i<max;i++){int out=ItemData.outputAt(id,i);Art.itemIconMedium(g,out,8+i*17,y);}
        }else Art.textSmall(g,Lang.t("Không có công thức ghép","No crafting recipe"),8,y,0x77849A);
    }

    private void drawInfo(Graphics g, int sp, int y, int lines, boolean shop) {
        int W = game.W, fh = Art.fh;
        int contentX=39;
        Art.avatar(g,sp,3,y+1);
        Art.textB(g, Data.name[sp], contentX, y+1, 0xFFFFFF);
        if (shop) {
            String price="$"+Data.cost[sp];
            Art.textB(g,price,W-3-Art.boldWidth(price),y+1,0xFFD030);
        }
        int x=contentX+Art.boldWidth(Data.name[sp])+5;
        int priceLeft=shop?W-8-Art.boldWidth("$"+Data.cost[sp]):W;
        if(x+16>priceLeft)x=priceLeft-37;
        Art.typeIcon(g,Data.t1[sp],x,y);x+=19;
        if (Data.t2[sp] >= 0){Art.typeIcon(g,Data.t2[sp],x,y);x+=19;}
        y += fh+5;
        lines--;
        if (lines <= 0) return;
        Art.textSmall(g,"HP: "+Data.hp[sp]+Lang.t(" Công: "," ATK: ")+Data.atk[sp]+Lang.t(" Thủ: "," DEF: ")+Data.def[sp]+Lang.t(" Tầm: "," RNG: ")+Data.range[sp],contentX,y,0xB0D0FF);
        y += fh+2;
        lines--;
        if (lines <= 0) return;
        drawEvolutionMini(g,sp,contentX,y);
    }

    private void drawEvolutionMini(Graphics g,int sp,int x,int y){
        int f=Data.fam[sp],max=familyMaxTier(sp),px=x;
        Art.textSmall(g,Lang.t("Tiến hóa:","Evolution:"),px,y+5,0x90A8C8);px+=Art.smallWidth(Lang.t("Tiến hóa:","Evolution:"))+4;
        for(int tier=1;tier<=max;tier++){int evo=-1;for(int i=0;i<Data.N;i++)if(Data.fam[i]==f&&Data.tier[i]==tier){evo=i;break;}if(evo<0)continue;g.setColor(evo==sp?0x405B72:0xD9DEDF);g.fillRect(px,y,20,20);Art.avatarMini(g,evo,px,y);g.setColor(evo==sp?0xFFE060:0x657086);g.drawRect(px,y,19,19);px+=23;}
    }

    private void fillSynCounts(int[] cnt){
        for(int i=0;i<Data.NT;i++)cnt[i]=0;
        if(bt!=null&&state!=PREP)bt.countSyn(0,cnt);
        else{for(int i=0;i<Data.N;i++)synFamilies[i]=false;for(int i=0;i<Run.BOARD;i++){int sp=run.board[i];if(sp<0)continue;int family=Data.fam[sp];if(synFamilies[family])continue;synFamilies[family]=true;cnt[Data.t1[sp]]++;if(Data.t2[sp]>=0)cnt[Data.t2[sp]]++;}}
    }

    private void paintDockSynHighlight(Graphics g,int type){
        if(type<0)return;int bx=(game.W-8*cell)/2;
        for(int p=0;p<Run.BOARD;p++){int sp=run.board[p];if(sp>=0&&(Data.t1[sp]==type||Data.t2[sp]==type)){int x=bx+(p%8)*cell,y=boardY+(p/8)*cell;g.setColor(0xFFE060);g.drawRect(x,y,cell-1,cell-1);g.drawRect(x+1,y+1,cell-3,cell-3);}}
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
        if(state==PREP&&selected>=0){int bx=(W-8*cell)/2;for(int p=0;p<Run.BOARD;p++){int sp=run.board[p];if(sp>=0&&(Data.t1[sp]==selected||Data.t2[sp]==selected)){int x=bx+(p%8)*cell,y=boardY+(p/8)*cell;g.setColor(0xFFE060);g.drawRect(x,y,cell-1,cell-1);g.drawRect(x+1,y+1,cell-3,cell-3);}}}
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
            String head=SynergyEffects.marks(t,cnt[t]);
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
        Art.textBC(g,Lang.t("TÚI VẬT PHẨM","ITEM BAG"),W/2,8,0xFFD030);
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
        if(shown==0)Art.textC(g,Lang.t("Túi vật phẩm trống","Item bag is empty"),W/2,y+8,0x9098A8);
        Art.textSmallC(g,Lang.t("FIRE: trang bị/ghép   #: đóng","FIRE: equip/craft   #: close"),W/2,H-fh-6,0x8090B0);
    }

    private void paintBattle(Graphics g) {
        int W = game.W, H = game.H, fh = Art.fh;
        g.setColor(0x0E141C);
        g.fillRect(0, 0, W, H);
        int top = fh * 2 + 4;
        int panelH = H >= 280 ? 144 : 116;
        int cs = Math.min(W / 8, (H - top - panelH - 3) / 6);
        int bx = (W - cs * 8) / 2, by = top;

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
            // The web game uses directional walk/attack clips. On MIDP we retain the
            // readable motion language with interpolation, idle bob and a short lunge.
            int bob = ((bt.tick + i) & 3) == 0 ? -1 : 0;
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
                g.setColor(Art.light(Data.TCOL[Data.t1[u.sp]]));
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
            boolean raw=RawAtlas.bounds(u.sp,ax,ay,vw,vh,animState,u.facing,animFrame,spriteBounds);
            int bodyX=raw?spriteBounds[0]:ax,bodyY=raw?spriteBounds[1]:ay;
            int bodyW=raw?spriteBounds[2]:vw,bodyH=raw?spriteBounds[3]:vh;
            Art.battleSprite(g, u.sp, ax, ay, animFrame, animState, u.facing);
            // Status icons are intentionally kept off the board. The upcoming
            // bottom inspector owns status names/timers so Pokemon stay readable.
            // team marker
            g.setColor(u.side == 0 ? 0x4090FF : 0xFF5050);
            g.fillRect(bodyX, bodyY + bodyH - 3, 3, 3);
            // Smooth web-style bars: interpolate values between 200ms logic ticks.
            int shownHp = u.alive ? u.prevHp + (u.hp - u.prevHp) * frac / 256 : 0;
            int shownMana = u.prevMana + (u.mana - u.prevMana) * frac / 256;
            int barY=bodyY+bodyH+1;
            int barCx=bodyX+bodyW/2;
            if(barCx<18)barCx=18;if(barCx>W-18)barCx=W-18;
            Art.battleBar(g,barCx,barY,shownHp,u.maxHp,u.shield,shownMana,u.maxMana,u.side==0);
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
        Art.text(g, "R" + run.round + "  vs " + run.enemyName, 3, 1, 0xFFFFFF);
        Art.text(g, Lang.t("Ta ", "You ") + bt.alive(0) + Lang.t("  Địch ", "  Foe ") + bt.alive(1), 3, fh + 2, 0xB0C8FF);
        Art.textR(g, "x" + speed + " " + game.actualFps + "/" + Save.targetFps() + "FPS" + (bt.tick > Battle.SUDDEN_DEATH ? " SUDDEN" : ""), W - 3, fh + 2, 0xFFD030);
        paintBattleStats(g, by + 6 * cs + 2, H-(by+6*cs+2));
        if(bt.over&&endReady){
            int ph=fh+6,py=H-ph;
            g.setColor(0x101830);g.fillRect(0,py,W,ph);g.setColor(bt.winner==0?0x40E060:0xFF6060);g.drawLine(0,py,W,py);
            Art.textBC(g,bt.winner==0?Lang.t("THẮNG - FIRE xem thống kê","VICTORY - FIRE for stats"):Lang.t("BẠI - FIRE xem thống kê","DEFEAT - FIRE for stats"),W/2,py+3,0xFFFFFF);
        }
    }

    private void paintBattleInfo(Graphics g, int y, int h) {
        int W = game.W, fh = Art.fh;
        g.setColor(0x090D14); g.fillRect(0, y, W, h);
        g.setColor(0x40506A); g.drawLine(0, y, W, y);
        if (bt == null || bt.n == 0) return;
        Unit u = bt.units[watch];
        String team = u.side == 0 ? Lang.t("TA","YOU") : Lang.t("ĐỊCH","FOE");
        int teamCol = u.side == 0 ? 0x70B8FF : 0xFF7070;
        Art.textB(g, Data.name[u.sp], 3, y + 2, 0xFFFFFF);
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
            g.setColor(s==rosterSide?0x49617E:0x29374D);g.fillRect(x,y+1,w,sideH);
            if(s==rosterSide){g.setColor(s==0?0x76C442:0xE76E55);g.fillRect(x,y+sideH-1,w,2);}
            String sideName=s==0?Lang.t("TA","ALLY"):Lang.t("ĐỊCH","ENEMY");
            Art.textC(g,sideName+" "+rosterAlive(s)+"/"+rosterCount(s),x+w/2,y+1,0xFFFFFF);
        }
        String[] modes={"DMG","HP","MP",Lang.t("Hệ","TYPE"),"Item","More"};
        int modeY=y+sideH+2,mh=Math.max(13,fh),mw=W/6;
        for(int i=0;i<6;i++){
            int x=i*mw,w=i==5?W-x:mw-1;
            g.setColor(i==statMode?0xC59112:0x35455F);g.fillRect(x,modeY,w,mh);
            if(i==statMode){g.setColor(statFocus?0xFFFFFF:0xFFE060);g.drawRect(x,modeY,w-1,mh-1);}
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
            int c=slot%3,r=slot/3,x=c*cw,cy=gridY+r*ch,w=c==2?W-x:cw;
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
            if(slot==rosterSel){g.setColor(0x60E878);g.drawRect(x+1,cy+1,w-3,ch-3);}
        }
        int total=0;for(int i=0;i<count;i++)total+=statValue(rosterUnit(rosterSide,i),statMode);
        Art.textB(g,statMode==4?Lang.t("FIRE: xem trang bị","FIRE: item details"):Lang.t("Tổng: ","Total: ")+total,3,y+h-footer+1,statColor(statMode,rosterSide));
    }

    private void paintBattleMore(Graphics g,int y,int h,int footerY){
        int W=game.W,fh=Art.fh;g.setColor(0x182438);g.fillRect(0,y,W,h);
        Art.textBC(g,Lang.t("MORE - CHẾ ĐỘ DEV","MORE - DEV MODES"),W/2,y+3,0xFFD060);
        int by=y+fh+7,bh=fh+6;g.setColor(0x405273);g.fillRect(8,by,W-16,bh);g.setColor(0x60E878);g.drawRect(8,by,W-17,bh-1);
        Art.textC(g,Lang.t("INFO DEV - thông số pet realtime","DEV INFO - realtime unit stats"),W/2,by+3,0xFFFFFF);
        Art.textSmall(g,Lang.t("FIRE chọn   0 đóng","FIRE select   0 close"),3,footerY+1,0xA8B5C8);
    }

    private void paintBattleDevInfo(Graphics g,int y,int h,int footerY){
        int W=game.W,fh=Art.fh;Unit u=rosterUnit(rosterSide,rosterSel);g.setColor(0x121D2D);g.fillRect(0,y,W,h);
        if(u==null){Art.textSmallC(g,Lang.t("Không có pet đang chọn","No selected unit"),W/2,y+3,0x8090A8);return;}
        Art.avatarMini(g,u.sp,3,y+2);Art.textB(g,Data.name[u.sp]+"  T"+Data.tier[u.sp],27,y+2,u.alive?0xFFFFFF:0x888898);
        Art.textSmall(g,"HP "+u.hp+"/"+u.maxHp+"  MP "+u.mana+"/"+u.maxMana+"  SH "+u.shield,27,y+fh+3,0xB8E8C0);
        Art.textSmall(g,"ATK "+u.atk+"  DEF "+u.def+"  SDEF "+u.speDef+"  SPD "+u.speed,3,y+fh*2+4,0xD8E0F0);
        Art.textSmall(g,"CRIT "+u.crit+"  DODGE "+u.dodge+"  AP "+u.skillBonus+"  REGEN "+u.regen,3,y+fh*3+5,0xD8E0F0);
        String types=Lang.typeName(Data.t1[u.sp])+(Data.t2[u.sp]>=0?"/"+Lang.typeName(Data.t2[u.sp]):"");
        Art.textSmall(g,types+"  "+Lang.moveName(Data.skillName[u.sp]),3,y+fh*4+6,0xFFD060);
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
                int local=rank-start,x=3,cy=y+local*cell;
                g.setColor(rank==battleSynCursor?0x304C3D:0x202D41);g.fillRect(x,cy+1,leftW-6,cell-2);
                if(rank==battleSynCursor){g.setColor(0x60E878);g.drawRect(x,cy+1,leftW-6,cell-2);}
                Art.typeIcon(g,t,x+2,cy+2);Art.textSmallR(g,""+cnt[t],leftW-5,cy+5,rank==battleSynCursor?0xFFFFFF:0xA8B5C8);
            }
            rank++;
        }
        if(n==0)Art.textSmallC(g,Lang.t("Chưa có hệ","No types"),leftW/2,y+4,0x687890);
        int matches=0;for(int i=0;i<rosterCount(rosterSide);i++){Unit u=rosterUnit(rosterSide,i);if(u!=null&&type>=0&&(Data.t1[u.sp]==type||Data.t2[u.sp]==type))matches++;}
        int cardCols=3,cardRows=3,cardW=Math.max(1,rightW/cardCols),cardH=Math.max(1,h/cardRows),slot=0;
        for(int box=0;box<9;box++){int c=box%3,r=box/3,x=rightX+c*cardW,cy=y+r*cardH,w=c==2?W-x:cardW;g.setColor(((r+c)&1)==0?0x243248:0x202C3F);g.fillRect(x,cy,w-1,cardH-1);}
        for(int i=0;i<rosterCount(rosterSide);i++){
            Unit u=rosterUnit(rosterSide,i);if(u==null||type<0||(Data.t1[u.sp]!=type&&Data.t2[u.sp]!=type))continue;
            if(slot>=9)break;
            int c=slot%cardCols,r=slot/cardCols,x=rightX+c*cardW,cy=y+r*cardH,w=c==cardCols-1?W-x:cardW;
            int ay=cy+Math.max(1,(cardH-20)/2);
            if(u.alive)Art.avatarMini(g,u.sp,x+2,ay);else Art.avatarMiniGray(g,u.sp,x+2,ay);
            int tx=x+24;
            Art.typeIcon(g,Data.t1[u.sp],tx,cy+2);
            if(Data.t2[u.sp]>=0)Art.typeIcon(g,Data.t2[u.sp],tx,cy+Math.max(17,cardH-18));
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
        Art.textB(g,Data.name[u.sp],4,3,0xFFFFFF);
        Art.textR(g,u.side==0?Lang.t("ĐỘI MÌNH","MY TEAM"):Lang.t("ĐỘI ĐỊCH","ENEMY"),W-4,3,u.side==0?0x76C442:0xE76E55);
        g.setColor(rarity);g.fillRect(0,fh+5,W,2);
        int ay=fh+11;Art.avatar(g,u.sp,5,ay);
        Art.typeIcon(g,Data.t1[u.sp],42,ay+1);
        if(Data.t2[u.sp]>=0)Art.typeIcon(g,Data.t2[u.sp],58,ay+1);
        Art.textSmall(g,"HP "+Math.max(0,u.hp)+"/"+u.maxHp+"  PP "+u.mana+"/"+u.maxMana,42,ay+18,0xB8E8C0);
        int y=ay+36;
        Art.textSmall(g,Lang.t("Công: ","ATK: ")+u.atk+Lang.t("  Thủ: ","  DEF: ")+u.def+Lang.t("  Kháng: ","  SP.DEF: ")+u.speDef,5,y,0xD8E0F0);y+=fh;
        Art.textSmall(g,Lang.t("Tốc: ","SPD: ")+u.speed+Lang.t("  Tầm: ","  RNG: ")+u.range+Lang.t("  Hồi chiêu: ","  CD: ")+u.cd,5,y,0xD8E0F0);y+=fh;
        Art.textSmall(g,Lang.t("Khiên: ","Shield: ")+u.shield+Lang.t("  Chí mạng: ","  Crit: ")+u.crit+"%"+Lang.t("  Né: ","  Dodge: ")+u.dodge+"%",5,y,0xD8E0F0);y+=fh+2;
        Art.textSmall(g,Lang.t("Sát thương: ","DMG: ")+u.damageDealt+Lang.t("  Nhận: ","  Taken: ")+u.damageTaken+Lang.t("  Chặn: ","  Block: ")+u.damageBlocked,5,y,0xFFC0A0);y+=fh;
        Art.textSmall(g,Lang.t("Hồi: ","Heal: ")+u.healingDone+Lang.t("  Khiên tạo: ","  Shield made: ")+u.shieldDone,5,y,0xA0E8B0);y+=fh+2;
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
                if(av==20)Art.avatarMini(g,p.sp,2,y+(rowH-20)/2);else Art.avatarTiny(g,p.sp,2,y+(rowH-16)/2);
                Art.textSmall(g,""+p.damageDealt,av+5,y,0xD8F0D8);
                drawResultItems(g,p,half-29,y+1);
                Art.bar(g,av+5,y+Math.min(fh,rowH-4),barW,4,p.damageDealt,max,0x76C442);
            }
            if(e!=null){
                if(av==20)Art.avatarMini(g,e.sp,W-22,y+(rowH-20)/2);else Art.avatarTiny(g,e.sp,W-18,y+(rowH-16)/2);
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
        Art.textSmallC(g,run.over?Lang.t("FIRE: xem tổng kết","FIRE: final summary"):Lang.t("FIRE: tiếp tục","FIRE: continue"),W/2,H-fh-2,0x8090B0);
    }

    private void drawResultItems(Graphics g,Unit u,int x,int y){for(int s=0;s<3;s++){int id=u.items[s],px=x+s*9;g.setColor(id>=0?0x263448:0x172131);g.fillArc(px,y,8,8,0,360);g.setColor(id>=0?0x80D8FF:0x4B586C);g.drawArc(px,y,7,7,0,360);if(id>=0)Art.itemIconTiny(g,id,px, y);}}

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
        Art.textSmallC(g,Lang.t("Vòng ","Round ")+run.round+"/"+run.maxRound()+"   HP "+run.hp+"   "+Lang.t("Vàng ","Gold ")+run.gold,W/2,y,0xE0E8FF);
        y+=fh+2;HistoryScreen.drawGraph(g,run.resultPath,run.resultCount,4,y,W-8,38);y+=41;
        Art.textB(g,Lang.t("ĐỘI HÌNH CUỐI","FINAL TEAM"),4,y,0x80D8FF);y+=fh+2;
        int n=run.boardCount(),slot=0,per=H<250?7:5,step=H<250?23:34;
        for(int i=0;i<Run.BOARD;i++)if(run.board[i]>=0){
            int x=3+(slot%per)*step,ay=y+(slot/per)*step;
            if(H<250)Art.avatarMini(g,run.board[i],x,ay);else Art.avatar(g,run.board[i],x,ay);slot++;
        }
        y+=((n+per-1)/per)*step;
        int[] cnt=synCounts;fillSynCounts(cnt);
        Art.textB(g,Lang.t("CỘNG HƯỞNG","SYNERGIES"),4,y,0x80D8FF);y+=fh+1;
        int sx=4,sy=y;
        for(int t=0;t<Data.NT;t++)if(Data.synLevel(t,cnt[t])>0){
            if(sx+34>W){sx=4;sy+=17;}Art.typeIcon(g,t,sx,sy);Art.textSmall(g,""+cnt[t],sx+16,sy+2,0xFFD060);sx+=34;
        }
        int infoY=Math.max(sy+18,H-fh*3-2);
        Art.textSmallC(g,Lang.t("Thưởng: +","Reward: +")+reward+" Poke Balls   "+Lang.t("Tốt nhất: ","Best: ")+Save.best,W/2,infoY,0xFFB070);
        Art.textC(g,Lang.t("FIRE: về menu","FIRE: menu"),W/2,H-fh-2,0x8090B0);
    }
}

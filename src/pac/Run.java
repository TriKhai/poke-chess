package pac;

/**
 * One offline auto chess run (player vs. AI). Pure logic, no MIDP classes.
 * Slot codes: 0..23 = board (row*8+col, row 0 = front), 24..31 = bench.
 */
public final class Run {
    public static final int MAX_ROUND = 20;
    public static final int MODE_NORMAL=0,MODE_UNLIMITED=1,MODE_THIRTY=2,MODE_GEN1=3;
    public static final int BOARD = 24, BENCH = 8;

    public final Rng rng;
    public int round = 1;
    public int hp = 100;
    public int gold = 6;
    public int level = 2;
    public int xp = 0;
    public int streak = 0;          // >0 win streak, <0 lose streak
    public int[] board = new int[BOARD];
    public int[] bench = new int[BENCH];
    public int[] shop = new int[5];
    public int[] pool = new int[Data.N];
    /** Three held-item slots per board/bench position, matching the original cap. */
    public int[] equip = new int[(BOARD + BENCH) * 3];
    /** Player item inventory; indices match ItemData.ID. */
    public int[] inventory = new int[ItemData.ID.length];
    /** Last merge result for preparation-screen visuals; not persisted. */
    public int mergeEventPos=-1,mergeEventTier=0,mergeEventSp=-1;
    public int lastItem = -1;
    public final boolean unlimitedGold;
    public final int mode;
    public String msg = "";
    /** Preserve the current shop for exactly the next round. */
    public boolean shopLocked = false;

    // enemy for the current round
    public int[] enemy = new int[BOARD];
    /** Three source-backed held-item slots for every enemy board cell. */
    public int[] enemyEquip = new int[BOARD*3];
    public int enemyScale = 100;
    public String enemyName = "";

    // last result
    public boolean lastWon;
    public int lastDamage, lastGold, lastSurvivors, lastXp;
    public int lastBaseGold,lastInterest,lastStreakGold,lastVictoryGold,lastItemGold;
    public boolean over, victory;
    /** Cumulative graph point after each completed round: win +1, loss -1. */
    public int[] resultPath=new int[30];
    public int resultCount=0;

    private static final String[] NAMES = { "Youngster Joey", "Lass Amy", "Hiker Tom", "Swimmer Kai",
            "Sage Ren", "Rocker Vic", "Scout Mia", "Ace Dan", "Ranger Sue", "Elder Gus", "Camper Lou", "Tamer Bo" };

    public Run(int seed) {
        this(seed, MODE_NORMAL);
    }

    public Run(int seed, boolean unlimited) {
        this(seed,unlimited?MODE_UNLIMITED:MODE_NORMAL);
    }

    public Run(int seed,int runMode) {
        mode=runMode;
        unlimitedGold = runMode==MODE_UNLIMITED;
        if (unlimitedGold) gold = 9999;
        rng = new Rng(seed);
        for (int i = 0; i < BOARD; i++) board[i] = -1;
        for (int i = 0; i < BENCH; i++) bench[i] = -1;
        for (int i = 0; i < equip.length; i++) equip[i] = -1;
        for (int i = 0; i < enemyEquip.length; i++) enemyEquip[i] = -1;
        for (int i = 0; i < 5; i++) shop[i] = -1;
        for (int i = 0; i < Data.N; i++) {
            if (Data.isBase(i) && Save.unlocked[i] && !Save.inCamp(i) && (mode!=MODE_GEN1||i<151)) pool[i] = Data.POOL_COPIES[Data.cost[i]];
        }
        rollShop();
        if(mode==MODE_THIRTY)refreshNineItems(false);
        genEnemy();
    }

    public int maxRound(){return mode>=MODE_THIRTY?30:MAX_ROUND;}

    public void refreshNineItems(boolean discardOld){
        if(discardOld)for(int i=0;i<inventory.length;i++)inventory[i]=0;
        int[] basic={5,2,8,6,7,3,4,1,0};
        for(int i=0;i<9;i++)giveItem(basic[rng.nextInt(basic.length)]);
        msg=Lang.t("Đã nhận 9 mảnh trang bị mới","Received 9 new components");
    }

    // ---- slots -----------------------------------------------------------

    public int get(int p) {
        return p < BOARD ? board[p] : bench[p - BOARD];
    }

    public void set(int p, int v) {
        if (p < BOARD) board[p] = v; else bench[p - BOARD] = v;
    }

    public int itemAt(int p,int slot){return equip[p*3+slot];}
    public int itemCount(int id){return id>=0&&id<inventory.length?inventory[id]:0;}
    public void giveItem(int id){if(id>=0&&id<inventory.length)inventory[id]++;}

    private void swapItems(int a,int b){
        for(int s=0;s<3;s++){int t=equip[a*3+s];equip[a*3+s]=equip[b*3+s];equip[b*3+s]=t;}
    }

    private void clearItems(int p,boolean returnToBag){
        for(int s=0;s<3;s++){int k=p*3+s,id=equip[k];if(returnToBag&&id>=0)giveItem(id);equip[k]=-1;}
    }

    /** Legacy entry now equips directly; automatic combining is intentionally disabled. */
    public boolean equipItem(int p,int id){
        return equipDirect(p,id);
    }

    /** Equip exactly the chosen inventory item; no implicit recipe is triggered. */
    public boolean equipDirect(int p,int id){
        if(p<0||p>=BOARD+BENCH||get(p)<0){msg=Lang.t("Hãy chọn Pokemon","Select a Pokemon");return false;}
        if(id<0||id>=inventory.length||inventory[id]<=0){msg=Lang.t("Không có vật phẩm","Item unavailable");return false;}
        for(int s=0;s<3;s++)if(itemAt(p,s)<0){equip[p*3+s]=id;inventory[id]--;msg=Lang.t("Đã trang bị ","Equipped ")+ItemData.name(id);return true;}
        msg=Lang.t("Pokemon đã đủ 3 vật phẩm","Pokemon already has 3 items");return false;
    }

    /** Craft in inventory from the exact unordered ItemRecipe pair. */
    public boolean craftItems(int a,int b){
        int made=ItemData.crafted(a,b);
        if(made<0){msg=Lang.t("Hai món này không có công thức","These items have no recipe");return false;}
        if(a<0||b<0||a>=inventory.length||b>=inventory.length){msg=Lang.t("Nguyên liệu không hợp lệ","Invalid ingredients");return false;}
        int needA=a==b?2:1;
        if(inventory[a]<needA||inventory[b]<(a==b?2:1)){msg=Lang.t("Không đủ nguyên liệu","Not enough components");return false;}
        inventory[a]--;inventory[b]--;inventory[made]++;
        lastItem=made;msg=Lang.t("Đã ghép: ","Crafted: ")+ItemData.name(made);return true;
    }

    /** Reserved implementation for the future dedicated Item crafting flow; currently not called. */
    public boolean equipCombined(int p,int a,int b){
        int made=ItemData.crafted(a,b),need=a==b?2:1;
        if(made<0){msg=Lang.t("Hai món này không ghép được","These items do not combine");return false;}
        if(a<0||b<0||inventory[a]<need||inventory[b]<(a==b?need:1)){msg=Lang.t("Không đủ nguyên liệu","Not enough components");return false;}
        if(p<0||p>=BOARD+BENCH||get(p)<0){msg=Lang.t("Hãy chọn Pokemon","Select a Pokemon");return false;}
        int slot=-1;for(int s=0;s<3;s++)if(itemAt(p,s)<0){slot=s;break;}
        if(slot<0){msg=Lang.t("Pokemon đã đủ 3 vật phẩm","Pokemon already has 3 items");return false;}
        inventory[a]--;inventory[b]--;equip[p*3+slot]=made;
        msg=Lang.t("Đã ghép và trang bị: ","Crafted and equipped: ")+ItemData.name(made);return true;
    }

    public int boardCount() {
        int c = 0;
        for (int i = 0; i < BOARD; i++) if (board[i] >= 0) c++;
        return c;
    }

    public int count(int sp) {
        int c = 0;
        for (int i = 0; i < BOARD + BENCH; i++) if (get(i) == sp) c++;
        return c;
    }

    private int freeBench() {
        for (int i = 0; i < BENCH; i++) if (bench[i] < 0) return BOARD + i;
        return -1;
    }

    // ---- shop ------------------------------------------------------------

    private void returnShopToPool() {
        for (int i = 0; i < 5; i++) {
            if (shop[i] >= 0) { pool[shop[i]]++; shop[i] = -1; }
        }
    }

    public void rollShop() {
        returnShopToPool();
        for (int i = 0; i < 5; i++) shop[i] = drawFromPool();
    }

    private int drawFromPool() {
        int roll = rng.nextInt(100);
        int c = 0, acc = 0;
        int[] odds = Data.ODDS[level];
        for (int k = 0; k < 5; k++) {
            acc += odds[k];
            if (roll < acc) { c = k + 1; break; }
        }
        if (c == 0) c = 1;
        // search nearest cost that still has stock: c, c-1, c+1, c-2, ...
        for (int off = 0; off < 5; off++) {
            int lo = c - off, hi = c + off;
            if (lo >= 1) { int sp = pick(lo); if (sp >= 0) return sp; }
            if (off > 0 && hi <= 5) { int sp = pick(hi); if (sp >= 0) return sp; }
        }
        return -1;
    }

    private int pick(int cost) {
        int total = 0;
        for (int i = 0; i < Data.N; i++) if (Data.cost[i] == cost && pool[i] > 0 && Data.isBase(i)) total += pool[i];
        if (total <= 0) return -1;
        int roll = rng.nextInt(total);
        for (int i = 0; i < Data.N; i++) {
            if (Data.cost[i] == cost && pool[i] > 0 && Data.isBase(i)) {
                roll -= pool[i];
                if (roll < 0) { pool[i]--; return i; }
            }
        }
        return -1;
    }

    public boolean reroll() {
        if (gold < 2) { msg = Lang.t("Cần 2 vàng để đổi shop", "Need 2 gold to reroll"); return false; }
        if (!unlimitedGold) gold -= 2;
        rollShop();
        return true;
    }

    public void toggleShopLock(){
        shopLocked=!shopLocked;
        msg=shopLocked?Lang.t("Đã khóa shop cho vòng sau","Shop locked for next round"):Lang.t("Đã mở khóa shop","Shop unlocked");
    }

    public boolean buy(int slot) {
        mergeEventPos=-1;mergeEventTier=0;mergeEventSp=-1;
        int sp = shop[slot];
        if (sp < 0) { msg = Lang.t("Đã bán hết", "Sold out"); return false; }
        int c = Data.cost[sp];
        if (gold < c) { msg = Lang.t("Không đủ vàng", "Not enough gold"); return false; }
        if (!addUnit(sp)) { msg = Lang.t("Hàng chờ đã đầy", "Bench is full"); return false; }
        if (!unlimitedGold) gold -= c;
        shop[slot] = -1;
        return true;
    }

    public boolean buyXp() {
        if (level >= Data.MAX_LEVEL) { msg = Lang.t("Đã đạt cấp tối đa", "Max level"); return false; }
        if (gold < 4) { msg = Lang.t("Cần 4 vàng để mua XP", "Need 4 gold for XP"); return false; }
        if (!unlimitedGold) gold -= 4;
        gainXp(4);
        return true;
    }

    private void gainXp(int amount) {
        xp += amount;
        while (level < Data.MAX_LEVEL && xp >= Data.XP_NEED[level]) {
            xp -= Data.XP_NEED[level];
            level++;
        }
        if (level >= Data.MAX_LEVEL) xp = 0;
    }

    // ---- units -----------------------------------------------------------

    /** puts a new unit on the bench and merges triples. false when there is no room. */
    private boolean addUnit(int sp) {
        int slot = freeBench();
        if (slot < 0) {
            // bench full: only OK when this unit completes a triple
            if (Data.evo[sp] < 0 || count(sp) < 2) return false;
            mergeCopies(sp,2);
            mergeAll();
            return true;
        }
        set(slot, sp);
        mergeAll();
        return true;
    }

    /** evolves any species that has 3 copies (board + bench), cascading. */
    public void mergeAll() {
        boolean again = true;
        while (again) {
            again = false;
            for (int sp = 0; sp < Data.N; sp++) {
                if (Data.evo[sp] < 0) continue;
                if (count(sp) >= 3) {
                    mergeCopies(sp,3);
                    again = true;
                    break;
                }
            }
        }
    }

    /** Merge copies and preserve up to three held items; overflow returns to inventory. */
    private void mergeCopies(int sp,int needed){
        int first=-1,removed=0,n=0;int[] kept=new int[9];
        for(int i=0;i<kept.length;i++)kept[i]=-1;
        for(int p=0;p<BOARD+BENCH&&removed<needed;p++)if(get(p)==sp){
            if(first<0)first=p;
            for(int s=0;s<3;s++){int id=itemAt(p,s);if(id>=0&&n<kept.length)kept[n++]=id;}
            clearItems(p,false);set(p,-1);removed++;
        }
        int evolved=Data.evo[sp];set(first,evolved);
        mergeEventPos=first;mergeEventTier=Data.tier[evolved];mergeEventSp=evolved;
        for(int i=0;i<n;i++){if(i<3)equip[first*3+i]=kept[i];else giveItem(kept[i]);}
    }

    public boolean move(int a, int b) {
        int ua = get(a), ub = get(b);
        if (ua < 0) return false;
        boolean aBoard = a < BOARD, bBoard = b < BOARD;
        if (bBoard && !aBoard && ub < 0 && boardCount() >= level) {
            msg = Lang.t("Bàn đấu đã đầy (cấp ", "Board full (level ") + level + ")";
            return false;
        }
        set(a, ub);
        set(b, ua);
        swapItems(a,b);
        return true;
    }

    public boolean sell(int p) {
        int sp = get(p);
        if (sp < 0) return false;
        if (!unlimitedGold) gold += Data.sellValue(sp);
        pool[Data.fam[sp]] += Data.copies(sp);
        clearItems(p,true);
        set(p, -1);
        return true;
    }

    // ---- rounds ----------------------------------------------------------

    public boolean isBoss() {
        if(mode>=MODE_THIRTY)return round%5==0;
        return round == 8 || round == 12 || round == 16 || round == 20;
    }

    public Battle makeBattle() {
        return new Battle(board, enemy, enemyScale, rng, equip,enemyEquip);
    }

    /** applies the outcome of a finished battle to the run. */
    public void applyResult(Battle b) {
        lastWon = (b.winner == 0);
        int previous=resultCount>0?resultPath[resultCount-1]:0;
        if(resultCount<resultPath.length)resultPath[resultCount++]=previous+(lastWon?1:-1);
        lastSurvivors = b.alive(0);
        lastDamage = 0;
        lastGold = 0;
        lastBaseGold=lastInterest=lastStreakGold=lastVictoryGold=lastItemGold=0;
        lastItem = -1;
        lastXp = 2;
        gainXp(lastXp);
        if (lastWon) {
            streak = streak > 0 ? streak + 1 : 1;
            if (round >= maxRound()) { over = true; victory = true;if(mode==MODE_GEN1)unlockGen1(); }
        } else {
            streak = streak < 0 ? streak - 1 : -1;
            lastDamage = EconomyRules.playerDamage(round,b.aliveTierSum(1));
            hp -= lastDamage;
            if (hp <= 0) { hp = 0; over = true; }
            if (round >= maxRound()) over = true; // the final boss ends the run either way
        }
        if (!over) {
            lastBaseGold=EconomyRules.BASE_INCOME;lastInterest=EconomyRules.interest(gold);
            lastStreakGold=EconomyRules.streakBonus(streak);lastVictoryGold=EconomyRules.victoryBonus(lastWon);lastItemGold=b.itemGold;
            lastGold=EconomyRules.income(gold,streak,lastWon,b.itemGold);
        }
        if(mode==MODE_GEN1&&lastWon&&rng.pct(45)){
            int[] basic={5,2,8,6,7,3,4,1};lastItem=basic[rng.nextInt(basic.length)];giveItem(lastItem);
        }else if(mode<MODE_THIRTY&&lastWon&&(round<=3||isBoss())){
            int[] basic={5,2,8,6,7,3,4,1};
            lastItem=basic[rng.nextInt(basic.length)];giveItem(lastItem);
        }
    }

    private void unlockGen1(){for(int i=0;i<151&&i<Data.N;i++)Save.unlocked[Data.fam[i]]=true;Save.save();}

    /** moves to the next round: pays income, gives xp, refreshes the shop. */
    public void nextRound() {
        if (!unlimitedGold) gold += lastGold;
        else gold = 9999;
        round++;
        if(shopLocked)shopLocked=false;else rollShop();
        genEnemy();
    }

    // ---- enemy generation ------------------------------------------------

    private int pickEnemySp(int maxCost, int theme) {
        for (int tries = 0; tries < 40; tries++) {
            int c;
            int r = rng.nextInt(100);
            if (r < 45) c = maxCost;
            else if (r < 80) c = maxCost - 1;
            else c = maxCost - 1 - rng.nextInt(2);
            if (c < 1) c = 1;
            int sp = rng.nextInt(Data.N);
            if (!Data.isBase(sp) || Data.cost[sp] != c || (mode==MODE_GEN1&&sp>=151)) continue;
            if (theme >= 0 && !Data.famHasType(sp, theme)) continue;
            return sp;
        }
        for (int i = 0; i < 200; i++) {
            int sp = rng.nextInt(Data.N);
            if (Data.isBase(sp) && Data.cost[sp] <= maxCost && (mode!=MODE_GEN1||sp<151)) return sp;
        }
        return 0;
    }

    // difficulty tuning
    public static int EVO1 = 30, EVO2 = 35, EVO3 = 30, SCALE_BASE = 80, SCALE_STEP = 3;

    private static final int[] COL_ORDER = { 3, 4, 2, 5, 1, 6, 0, 7 };

    public void genEnemy() {
        int r = round;
        for (int i = 0; i < BOARD; i++) enemy[i] = -1;
        for (int i = 0; i < enemyEquip.length; i++) enemyEquip[i] = -1;
        boolean boss = isBoss();
        int n;
        if(mode==MODE_GEN1&&boss){gen1Boss(r);return;}
        if(EnemyFormation.apply(this))return;
        if (r == 1) n = 1;
        else if (r <= 3) n = 2;
        else n = 2 + (r * 2) / 5;
        if (boss) n++;
        if(mode==MODE_THIRTY&&boss)n=1;
        if (n > 9) n = 9;
        int maxCost = r < 4 ? 1 : (r < 8 ? 2 : (r < 11 ? 3 : (r < 18 ? 4 : 5)));
        int theme = -1;
        if (r == 8) theme = Data.T_ROCK;
        else if (r == 12) theme = Data.T_WATER;
        else if (r == 16) theme = Data.T_PSY;
        else if (r == 20) theme = Data.T_DRAGON;

        int[] list = new int[n];
        for (int k = 0; k < n; k++) {
            int sp = pickEnemySp(maxCost, theme);
            int steps = 0;
            if (r >= 6 && rng.pct(EVO1)) steps++;
            if (r >= 11 && rng.pct(EVO2)) steps++;
            if (r >= 16 && rng.pct(EVO3)) steps++;
            while (steps > 0 && Data.evo[sp] >= 0) { sp = Data.evo[sp]; steps--; }
            list[k] = sp;
        }
        if (r == maxRound()) list[0] = Data.MEWTWO;

        for (int k = 0; k < n; k++) {
            int sp = list[k];
            int row = Data.range[sp] - 1;
            if (row > 2) row = 2;
            if (row < 0) row = 0;
            boolean placed = false;
            for (int tryRow = 0; tryRow < 3 && !placed; tryRow++) {
                int rr = (row + tryRow) % 3;
                for (int c = 0; c < 8; c++) {
                    int idx = rr * 8 + COL_ORDER[c];
                    if (enemy[idx] < 0) { enemy[idx] = sp; placed = true; break; }
                }
            }
        }

        if(mode==MODE_THIRTY&&boss){enemyScale=150+r*4;enemyName=Lang.t("BOSS KHỔNG LỒ","GIANT BOSS");}
        else if (r <= 3) { enemyScale = 75 + r * 5; enemyName = "Wild Pokemon"; }
        else {
            enemyScale = SCALE_BASE + r * SCALE_STEP + (boss ? 15 : 0);
            if (r == 8) enemyName = "Boss: Rock Tamer";
            else if (r == 12) enemyName = "Boss: Tide Master";
            else if (r == 16) enemyName = "Boss: Mind Sage";
            else if (r == 20) enemyName = "CHAMPION";
            else enemyName = NAMES[rng.nextInt(NAMES.length)];
        }
    }

    private int findSp(String name){for(int i=0;i<Data.N;i++)if(Data.name[i].equals(name))return i;return 0;}

    private void gen1Boss(int r){
        int sp=findSp(r==5?"Magikarp":r==10?"Moltres":r==15?"Articuno":r==20?"Zapdos":r==25?"Mewtwo":"Ho-Oh");
        int copies=r==25?2:1;for(int i=0;i<copies;i++)enemy[1*8+COL_ORDER[i]]=sp;
        enemyScale=125+r*4;enemyName="BOSS: "+Data.name[sp]+(copies>1?" x2":"");
    }
}

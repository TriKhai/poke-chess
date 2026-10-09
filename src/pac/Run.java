package pac;

/**
 * One offline auto chess run (player vs. AI). Pure logic, no MIDP classes.
 * Slot codes: 0..23 = board (row*8+col, row 0 = front), 24..31 = bench.
 */
public final class Run {
    public static final int MAX_ROUND = 40;
    public static final int MODE_NORMAL=0,MODE_UNLIMITED=1,MODE_THIRTY=2,MODE_GEN1=3,MODE_LEGEND=4,MODE_GEN2=5,MODE_GEN3=6,MODE_GEN4=7,MODE_GEN5=8,MODE_GEN6=9,MODE_GEN7=10,MODE_GEN8=11,MODE_GEN9=12,MODE_ENDLESS=13,MODE_TEST=14,MODE_TOWER=15,MODE_MONOTYPE=16,MODE_RANDOM_TEAM=17,MODE_BOSS_RUSH=18;
    public int modeHpPct,modeAtkPct,modeArmor;public boolean towerPending;
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
    /** 0 choose synergy, 1 choose starter, 2 normal preparation unlocked. */
    public int draftStage;
    public int poolType=-1;
    public int[] poolTypes={-1,-1,-1};
    /** Three held-item slots per board/bench position, matching the original cap. */
    public int[] equip = new int[(BOARD + BENCH) * 3];
    /** Player item inventory; indices match ItemData.ID. */
    public int[] inventory = new int[ItemData.ID.length];
    /** Run-only consumables and per-slot permanent growth. */
    public int[] consumables=new int[ConsumableData.COUNT];
    public int[] boostHp=new int[BOARD+BENCH],boostAtk=new int[BOARD+BENCH],boostDef=new int[BOARD+BENCH],boostSpeDef=new int[BOARD+BENCH];
    public int[] boostSpeed=new int[BOARD+BENCH],boostMana=new int[BOARD+BENCH],boostAp=new int[BOARD+BENCH],boostUses=new int[BOARD+BENCH];
    public int[] boostRange=new int[BOARD+BENCH],boostShield=new int[BOARD+BENCH],boostHpPct=new int[BOARD+BENCH],boostAtkPct=new int[BOARD+BENCH],boostDefPct=new int[BOARD+BENCH],boostSpeedPct=new int[BOARD+BENCH],boostApPct=new int[BOARD+BENCH],boostCritPct=new int[BOARD+BENCH],fruitMask=new int[BOARD+BENCH];
    public int[] fruitShop={-1,-1,-1,-1,-1,-1};
    /** Five distinct synergy stones offered each round; bought stones apply immediately. */
    public int[] stoneShop={-1,-1,-1,-1,-1};
    public int[] synergyStoneBonus=new int[Data.NT];
    public int stoneBoughtRound=-1;
    /** 1 when the formation/bench slot has been upgraded with a Shiny Charm. */
    public int[] shiny=new int[BOARD+BENCH];
    public int shinyBoughtRound=-1;
    public int[] mega=new int[BOARD+BENCH];
    public int megaBoughtRound=-1;
    public int[] specialForm=new int[BOARD+BENCH],zygardeCells=new int[BOARD+BENCH];
    public int memoryBoughtRound=-1,cubeBoughtRound=-1;
    public int ultimateBoughtRound=-1;
    public int fruitBoughtMask=0,fruitBoughtRound=-1;
    /** Last merge result for preparation-screen visuals; not persisted. */
    public int mergeEventPos=-1,mergeEventTier=0,mergeEventSp=-1;
    public int lastItem = -1;
    public final boolean unlimitedGold;
    public final int mode;
    /** Non-persistent developer lab filter; zero in every normal/save run. */
    public final int testGeneration;
    public int testSpecies=-1;
    public int pendingEvolutionPos=-1;
    public int[] eatenFruits=new int[(BOARD+BENCH)*12];
    public int eatenFruitAt(int p,int slot){return slot<0||slot>=12?-1:eatenFruits[p*12+slot]-1;}
    public void migrateFruitHistory(){for(int p=0;p<BOARD+BENCH;p++){int slot=0;for(int id=0;id<ConsumableData.SHINY_CHARM&&slot<12;id++)if((fruitMask[p]&(1<<id))!=0)eatenFruits[p*12+slot++]=id+1;}}
    public boolean hasEvolutionChoice(){return pendingEvolutionPos>=0&&get(pendingEvolutionPos)>=0;}
    public boolean chooseEvolution(boolean variant){return chooseEvolution(variant?1:0);}
    public boolean chooseEvolution(int choice){if(!hasEvolutionChoice())return false;int p=pendingEvolutionPos,sp=get(p);if(choice<0||choice>=EvolutionBranchData.count(sp))return false;int next=EvolutionBranchData.choiceSpecies(sp,choice),form=EvolutionBranchData.choiceForm(sp,choice);if(next!=sp){set(p,next);mega[p]=0;}specialForm[p]=form;pendingEvolutionPos=-1;mergeEventPos=p;mergeEventTier=Data.tier[get(p)];mergeEventSp=get(p);mergeAll();return true;}
    public int type1At(int p){int sp=get(p);return sp<0?-1:isMega(p)?MegaData.type1(sp):EvolutionVariantData.type1(sp,specialFormAt(p));}
    public int type2At(int p){int sp=get(p);return sp<0?-1:isMega(p)?MegaData.type2(sp):EvolutionVariantData.type2(sp,specialFormAt(p));}
    /** Board-only counts, before purchased synergy stones. */
    public void countBoardSyn(int[] cnt){for(int t=0;t<Data.NT;t++)cnt[t]=0;for(int p=0;p<BOARD;p++){int sp=board[p];if(sp<0||supersededBoardForm(p))continue;int t1=type1At(p),t2=type2At(p);cnt[t1]++;if(t2>=0&&t2!=t1)cnt[t2]++;}}
    private boolean supersededBoardForm(int p){int sp=board[p],form=mega[p]!=0?-1:specialForm[p];for(int q=0;q<BOARD;q++)if(board[q]>=0&&SynergyEffects.supersedes(board[q],mega[q]!=0?-1:specialForm[q],q,sp,form,p))return true;return false;}
    public void lockTestShop(int sp){if(!testMode()||sp<0||sp>=Data.N||!Data.isBase(sp))return;testSpecies=sp;for(int i=0;i<pool.length;i++)pool[i]=0;pool[sp]=999;for(int i=0;i<shop.length;i++)shop[i]=-1;rollShop();}
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
    public int[] resultPath=new int[40];
    public int resultCount=0;

    private static final String[] NAMES = { "Youngster Joey", "Lass Amy", "Hiker Tom", "Swimmer Kai",
            "Sage Ren", "Rocker Vic", "Scout Mia", "Ace Dan", "Ranger Sue", "Elder Gus", "Camper Lou", "Tamer Bo" };

    public Run(int seed) {
        this(seed, MODE_NORMAL);
    }

    public Run(int seed, boolean unlimited) {
        this(seed,unlimited?MODE_UNLIMITED:MODE_NORMAL);
    }

    public Run(int seed,int runMode) {this(seed,runMode,0);}

    public Run(int seed,int runMode,int labGeneration) {
        mode=runMode;
        testGeneration=runMode==MODE_TEST?Math.max(1,Math.min(Data.BATTLE_MAX_GEN,labGeneration)):0;
        unlimitedGold = runMode==MODE_UNLIMITED||runMode==MODE_TEST;
        if (unlimitedGold) gold = 9999;
        rng = new Rng(seed);
        for (int i = 0; i < BOARD; i++) board[i] = -1;
        for (int i = 0; i < BENCH; i++) bench[i] = -1;
        for (int i = 0; i < equip.length; i++) equip[i] = -1;
        for (int i = 0; i < enemyEquip.length; i++) enemyEquip[i] = -1;
        for (int i = 0; i < 5; i++) shop[i] = -1;
        draftStage=usesDraft()?0:2;
        if(mode==MODE_LEGEND){
            for(int i=0;i<Data.N;i++)if(eligibleBase(i)&&Data.category[i]==6)pool[i]=Data.POOL_COPIES[Data.cost[i]];
            draftStage=1;
        }else if(!usesDraft()){
            for (int i = 0; i < Data.N; i++)if(eligibleBase(i)&&Data.category[i]<5)
                pool[i] = Data.POOL_COPIES[Data.cost[i]];
            rollShop();
        }
        if(mode==MODE_THIRTY)refreshNineItems(false);
        if(mode==MODE_RANDOM_TEAM)setupRandomTeam();
        genEnemy();
        rollFruitShop();
        rollStoneShop();
    }

    public boolean usesDraft(){return mode==MODE_NORMAL||mode==MODE_UNLIMITED||(generationMode()&&!testMode())||endlessMode()||mode==MODE_TOWER||mode==MODE_MONOTYPE||mode==MODE_BOSS_RUSH;}
    private void setupRandomTeam(){level=9;gold=20;for(int k=0;k<9;k++){int start=rng.nextInt(Data.N),sp=-1;for(int i=0;i<Data.N;i++){int s=(start+i)%Data.N;if(!autoChessEligible(s)||Data.category[s]>=5)continue;boolean dup=false;for(int p=0;p<BOARD;p++)if(board[p]>=0&&Data.fam[board[p]]==Data.fam[s])dup=true;if(!dup){sp=s;break;}}if(sp>=0){if(Data.evo[sp]>=0)sp=Data.evo[sp];board[k<8?8+COL_ORDER[k]:3]=sp;}}for(int i=0;i<9;i++)giveItem(randomComponent());for(int i=0;i<shop.length;i++)shop[i]=-1;}
    public boolean chooseTowerBuff(int choice){if(mode!=MODE_TOWER||!towerPending||choice<0||choice>2)return false;if(choice==0)modeHpPct+=15;else if(choice==1)modeAtkPct+=10;else modeArmor+=3;towerPending=false;return true;}
    public boolean testMode(){return mode==MODE_TEST;}
    /** Auto Chess roster is independent from Collection ownership and Camp locks. */
    boolean autoChessEligible(int sp){
        if(!Data.isBase(sp))return false;
        // Walking Wake is an Ancient Paradox transformation in this mode, not a rollable unit.
        if(Data.nationalDex(sp)==1009)return false;
        if(mode==MODE_LEGEND)return Data.category[sp]==6;
        if(testMode())return Data.generation(sp)==testGeneration;
        if(generationMode())return Data.generation(sp)<=playerGenerationLimit();
        if(mode==MODE_UNLIMITED||mode==MODE_ENDLESS||ExtraChessRules.mode(mode))return Data.generation(sp)<=Data.BATTLE_MAX_GEN;
        return sp<Data.CORE_N;
    }
    private boolean eligibleBase(int sp){return autoChessEligible(sp);}

    /** Three source-style portal packages, each displaying three synergy symbols. */
    public void typePackageChoices(int[] out){
        for(int i=0;i<out.length;i++){
            int t,guard=0;boolean dup;int common;
            do{t=rng.nextInt(Data.NT);dup=false;common=0;for(int j=0;j<i;j++)if(out[j]==t)dup=true;
                for(int s=0;s<Data.N;s++)if(eligibleBase(s)&&Data.category[s]==0&&Data.famHasType(s,t))common++;
            }while((dup||common<1)&&++guard<300);
            out[i]=t;
        }
    }

    public void choosePoolTypes(int[] types,int offset){
        for(int i=0;i<3;i++)poolTypes[i]=types[offset+(mode==MODE_MONOTYPE?0:i)];poolType=poolTypes[0];draftStage=1;
    }

    public void starterChoices(int[] out){
        if(mode==MODE_LEGEND){pokemonChoices(out,6,-1,false);return;}
        for(int k=0;k<out.length;k++){
            int type=poolTypes[k]>=0?poolTypes[k]:poolType,count=0;
            for(int i=0;i<Data.N;i++)if(choiceCandidate(i,0,type,true,out,k))count++;
            if(count==0){out[k]=-1;continue;}int q=rng.nextInt(count);
            for(int i=0;i<Data.N;i++)if(choiceCandidate(i,0,type,true,out,k)&&q--==0){out[k]=i;break;}
        }
    }

    /** Completes stage-zero draft, builds the restricted shop pool, gives starter, then opens shop. */
    public void chooseStarter(int starter){
        if(mode==MODE_LEGEND){addUnit(starter);draftStage=2;rollShop();return;}
        buildTypedPool(starter);addUnit(starter);draftStage=2;rollShop();
    }

    private void buildTypedPool(int starter){
        for(int i=0;i<pool.length;i++)pool[i]=0;
        for(int i=0;i<Data.N;i++)if(eligibleBase(i)&&Data.category[i]<5&&matchesChosenPool(i))pool[i]=Data.POOL_COPIES[Data.cost[i]];
    }

    public boolean matchesChosenPool(int sp){if(poolType<0)return true;if(mode==MODE_MONOTYPE)return Data.t1[sp]==poolType||Data.t2[sp]==poolType;for(int i=0;i<poolTypes.length;i++)if(poolTypes[i]>=0&&Data.famHasType(sp,poolTypes[i]))return true;return false;}

    private int pickFamilies(boolean[] chosen,int wanted,int kind,int st1,int st2,boolean[] bridgeType){
        int added=0;
        while(added<wanted){
            int count=0;
            for(int i=0;i<Data.N;i++)if(!chosen[i]&&eligibleBase(i)&&Data.category[i]<5&&poolCandidate(i,kind,st1,st2,bridgeType))count++;
            if(count==0)break;int q=rng.nextInt(count);
            for(int i=0;i<Data.N;i++)if(!chosen[i]&&eligibleBase(i)&&Data.category[i]<5&&poolCandidate(i,kind,st1,st2,bridgeType)&&q--==0){chosen[i]=true;added++;break;}
        }
        return added;
    }

    private boolean poolCandidate(int sp,int kind,int st1,int st2,boolean[] bridgeType){
        boolean direct=Data.famHasType(sp,st1)||Data.famHasType(sp,st2);
        if(kind==0)return direct;
        if(kind==1){int a=Data.t1[sp],b=Data.t2[sp];return !direct&&((a>=0&&a<Data.NT&&bridgeType[a])||(b>=0&&b<Data.NT&&bridgeType[b]));}
        return true;
    }

    /** Three distinct eligible base families. type=-1 means no synergy filter. */
    public void pokemonChoices(int[] out,int category,int type,boolean requireType){
        for(int k=0;k<out.length;k++){
            int count=0;
            for(int i=0;i<Data.N;i++)if(choiceCandidate(i,category,type,requireType,out,k))count++;
            if(count==0){out[k]=-1;continue;}int q=rng.nextInt(count);
            for(int i=0;i<Data.N;i++)if(choiceCandidate(i,category,type,requireType,out,k)&&q--==0){out[k]=i;break;}
        }
    }

    private boolean choiceCandidate(int sp,int category,int type,boolean requireType,int[] out,int used){
        if(!eligibleBase(sp)||!Data.isBase(sp)||Data.tier[sp]!=1||Data.category[sp]!=category)return false;
        if(poolType>=0&&!matchesChosenPool(sp))return false;
        if(requireType&&!Data.famHasType(sp,type))return false;
        for(int i=0;i<used;i++)if(out[i]==sp)return false;
        return true;
    }

    /** Additional picks enter the normal shop pool and also grant one free copy. */
    public void chooseAdditional(int sp){
        if(mode==MODE_RANDOM_TEAM||mode==MODE_MONOTYPE&&!matchesChosenPool(sp))return;
        if(sp<0)return;if(Data.category[sp]<5||(mode==MODE_LEGEND&&Data.category[sp]==6))pool[sp]+=Data.POOL_COPIES[Data.cost[sp]];
        if(!addUnit(sp)&&!unlimitedGold)gold+=Data.sellValue(sp);
    }

    private int randomBaseCategory(int category){
        int n=0;for(int i=0;i<Data.N;i++)if(eligibleBase(i)&&Data.category[i]==category)n++;
        if(n==0)return -1;int q=rng.nextInt(n);for(int i=0;i<Data.N;i++)if(eligibleBase(i)&&Data.category[i]==category&&q--==0)return i;return -1;
    }

    public boolean generationMode(){return (mode>=MODE_GEN1&&mode!=MODE_LEGEND&&mode<=MODE_GEN9)||mode==MODE_TEST;}
    public boolean endlessMode(){return mode==MODE_ENDLESS;}
    public int enemyGeneration(){if(ExtraChessRules.mode(mode))return 1+((round-1)/(mode==MODE_BOSS_RUSH?1:5))%Data.BATTLE_MAX_GEN;if(testMode())return testGeneration;if(endlessMode())return 1+((round-1)/5)%Data.BATTLE_MAX_GEN;return mode==MODE_GEN1?1:(mode==MODE_GEN2?2:(mode==MODE_GEN3?3:(mode==MODE_GEN4?4:(mode==MODE_GEN5?5:(mode==MODE_GEN6?6:(mode==MODE_GEN7?7:(mode==MODE_GEN8?8:(mode==MODE_GEN9?9:0))))))));}
    public int playerGenerationLimit(){if(testMode())return testGeneration;if(endlessMode())return Data.BATTLE_MAX_GEN;int gen=enemyGeneration();return gen>0&&gen<3?3:gen;}
    public int maxRound(){if(mode==MODE_TOWER||mode==MODE_RANDOM_TEAM)return 30;if(mode==MODE_BOSS_RUSH)return 20;return endlessMode()?Integer.MAX_VALUE:(mode==MODE_THIRTY||generationMode()?30:MAX_ROUND);}

    /** The 30-round inventory refresh is a victory reward, never a loss reward. */
    public boolean shouldOfferNineItemRefresh(){return mode==MODE_THIRTY&&lastWon&&!over;}

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

    private void swapBoosts(int a,int b){for(int j=0;j<12;j++){int t=eatenFruits[a*12+j];eatenFruits[a*12+j]=eatenFruits[b*12+j];eatenFruits[b*12+j]=t;}int[][] all={boostHp,boostAtk,boostDef,boostSpeDef,boostSpeed,boostMana,boostAp,boostUses,boostRange,boostShield,boostHpPct,boostAtkPct,boostDefPct,boostSpeedPct,boostApPct,boostCritPct,fruitMask,shiny,mega,specialForm,zygardeCells};for(int i=0;i<all.length;i++){int t=all[i][a];all[i][a]=all[i][b];all[i][b]=t;}}
    private void clearBoosts(int p){for(int j=0;j<12;j++)eatenFruits[p*12+j]=0;int[][] all={boostHp,boostAtk,boostDef,boostSpeDef,boostSpeed,boostMana,boostAp,boostUses,boostRange,boostShield,boostHpPct,boostAtkPct,boostDefPct,boostSpeedPct,boostApPct,boostCritPct,fruitMask,shiny,mega,specialForm,zygardeCells};for(int i=0;i<all.length;i++)all[i][p]=0;}
    public boolean isShiny(int p){return p>=0&&p<shiny.length&&shiny[p]!=0;}
    public boolean isMega(int p){return p>=0&&p<mega.length&&mega[p]!=0;}
    public int specialFormAt(int p){return p>=0&&p<specialForm.length?specialForm[p]:0;}
    public static int shopCost(int sp){return sp>=0&&Data.nationalDex(sp)==718?5:Data.cost[sp];}
    public String nameAt(int p){int sp=get(p),form=specialFormAt(p);return form!=0?SpecialFormData.name(form):(sp>=0?Data.name[sp]:"");}
    public boolean buyShinyCharm(){
        if(round<ShinyData.UNLOCK_ROUND){msg=Lang.t("Đá Shiny mở từ vòng 10","Shiny Stone unlocks at round 10");return false;}
        if(shinyBoughtRound==round){msg=Lang.t("Mỗi vòng chỉ mua được 1 Đá Shiny","Only one Shiny Stone may be bought each round");return false;}
        if(gold<ShinyData.PRICE&&!unlimitedGold){msg=Lang.t("Không đủ vàng","Not enough gold");return false;}
        if(!unlimitedGold)gold-=ShinyData.PRICE;giveConsumable(ConsumableData.SHINY_CHARM);shinyBoughtRound=round;
        msg=Lang.t("Đá Shiny đã vào Túi vật phẩm","Shiny Stone was added to the Consumable bag");return true;
    }
    public boolean buyMegaStone(){if(round<MegaData.UNLOCK_ROUND){msg=Lang.t("Đá Mega mở từ vòng 15","Mega Stone unlocks at round 15");return false;}if(megaBoughtRound==round){msg=Lang.t("Mỗi vòng chỉ mua được 1 Đá Mega","Only one Mega Stone may be bought each round");return false;}if(gold<MegaData.PRICE&&!unlimitedGold){msg=Lang.t("Không đủ vàng","Not enough gold");return false;}if(!unlimitedGold)gold-=MegaData.PRICE;giveConsumable(ConsumableData.MEGA_STONE);megaBoughtRound=round;msg=Lang.t("Đá Mega đã vào Túi vật phẩm","Mega Stone was added to the Consumable bag");return true;}
    public boolean buyMemoryDisc(){if(round<SpecialFormData.UNLOCK_ROUND){msg=Lang.t("Mở từ vòng 15","Unlocks at round 15");return false;}if(memoryBoughtRound==round){msg=Lang.t("Mỗi vòng chỉ mua được 1 Thẻ nhớ","Only one Memory Disc per round");return false;}if(gold<SpecialFormData.MEMORY_PRICE&&!unlimitedGold){msg=Lang.t("Không đủ vàng","Not enough gold");return false;}if(!unlimitedGold)gold-=SpecialFormData.MEMORY_PRICE;giveConsumable(ConsumableData.MEMORY_DISC);memoryBoughtRound=round;msg=Lang.t("Thẻ nhớ đã vào Túi vật phẩm","Memory Disc added to bag");return true;}
    public boolean buyZygardeCube(){if(round<SpecialFormData.UNLOCK_ROUND){msg=Lang.t("Mở từ vòng 15","Unlocks at round 15");return false;}if(cubeBoughtRound==round){msg=Lang.t("Mỗi vòng chỉ mua được 1 Zygarde Cube","Only one Zygarde Cube may be bought each round");return false;}if(gold<SpecialFormData.CUBE_PRICE&&!unlimitedGold){msg=Lang.t("Không đủ vàng","Not enough gold");return false;}if(!unlimitedGold)gold-=SpecialFormData.CUBE_PRICE;giveConsumable(ConsumableData.ZYGARDE_CUBE);cubeBoughtRound=round;msg="Zygarde Cube "+Lang.t("đã vào Túi vật phẩm (-$10)","added to bag (-$10)");return true;}
    public int consumableCount(int id){return id>=0&&id<consumables.length?consumables[id]:0;}
    public void giveConsumable(int id){if(id>=0&&id<consumables.length)consumables[id]++;}
    public boolean useConsumable(int p,int id){
        if(p<0||p>=BOARD+BENCH||get(p)<0){msg=Lang.t("Hãy chọn Pokémon","Select a Pokemon");return false;}
        if(id<0||id>=consumables.length||consumables[id]<=0){msg=Lang.t("Không có vật phẩm này","Consumable unavailable");return false;}
        if(id==ConsumableData.SHINY_CHARM){
            int sp=get(p);if(!ShinyData.available(sp)){msg=Lang.t("Pokémon này chưa có tài nguyên Tỏa sáng","This Pokemon has no Shiny resources yet");return false;}
            if(shiny[p]!=0){msg=Lang.t("Pokémon này đã Tỏa sáng","This Pokemon is already Shiny");return false;}
            shiny[p]=1;consumables[id]--;msg=Data.name[sp]+Lang.t(" đã chuyển sang dạng Tỏa sáng!"," became Shiny!");return true;
        }
        if(id==ConsumableData.MEGA_STONE){int sp=get(p);if(MegaData.requiresShiny(sp)&&!isShiny(p)){msg=Lang.t("Pokémon phải Tỏa sáng trước khi Mega hóa","Pokemon must be Shiny before Mega Evolution");return false;}if(!MegaData.available(sp)){msg=Lang.t("Pokémon này chưa có animation Mega trong source","This Pokemon has no source-backed Mega animation");return false;}if(mega[p]!=0||(specialForm[p]!=0&&!(SpecialFormData.zygarde(sp)&&specialForm[p]==SpecialFormData.ZYGARDE_100))){msg=Lang.t("Pokémon đã có dạng biến đổi","Pokemon already has a transformed form");return false;}mega[p]=1;specialForm[p]=0;consumables[id]--;msg=MegaData.formName(sp)+"!";return true;}
        if(id==ConsumableData.MEMORY_DISC){int sp=get(p),form=SpecialFormData.nextMemoryForm(sp,specialForm[p]);if(form==0&&!LaterFormData.contains(specialForm[p])){msg=SpecialFormData.memoryAvailable(sp)?Lang.t("Pokémon đã có dạng biến đổi","Pokemon already has a transformed form"):Lang.t("Pokémon này chưa có dạng dùng Thẻ nhớ","No Memory Disc form for this Pokemon");return false;}if(mega[p]!=0){msg=Lang.t("Pokémon đã Mega hóa","Pokemon is already Mega Evolved");return false;}specialForm[p]=form;consumables[id]--;msg=Data.name[sp]+" -> "+(form==0?Data.name[sp]:SpecialFormData.name(form));return true;}
        if(id==ConsumableData.ZYGARDE_CUBE){int sp=get(p);if(!SpecialFormData.zygarde(sp)){msg=Lang.t("Chỉ dùng cho Zygarde","Only usable on Zygarde");return false;}if(mega[p]!=0||specialForm[p]==SpecialFormData.ZYGARDE_100){msg=Lang.t("Zygarde đã đạt dạng 100%","Zygarde already reached 100%");return false;}if(specialForm[p]==SpecialFormData.ZYGARDE_10)specialForm[p]=SpecialFormData.ZYGARDE_50;else if(specialForm[p]==SpecialFormData.ZYGARDE_50)specialForm[p]=SpecialFormData.ZYGARDE_100;else{msg=Lang.t("Dạng Zygarde không hợp lệ","Invalid Zygarde form");return false;}consumables[id]--;msg=Lang.t("Đã nâng thành ","Upgraded to ")+SpecialFormData.name(specialForm[p]);return true;}
        if(boostUses[p]>=12){msg=Lang.t("Pokémon đã đạt giới hạn 12 trái","Pokemon reached the 12-fruit limit");return false;}
        boostHp[p]+=ConsumableData.HP[id];boostAtk[p]+=ConsumableData.ATK[id];boostDef[p]+=ConsumableData.DEF[id];boostSpeDef[p]+=ConsumableData.SPDEF[id];boostSpeed[p]+=ConsumableData.SPEED[id];boostMana[p]+=ConsumableData.MANA[id];boostAp[p]+=ConsumableData.AP[id];boostRange[p]+=ConsumableData.RANGE[id];boostShield[p]+=ConsumableData.SHIELD[id];boostHpPct[p]+=ConsumableData.HP_PCT[id];boostAtkPct[p]+=ConsumableData.ATK_PCT[id];boostDefPct[p]+=ConsumableData.DEF_PCT[id];boostSpeedPct[p]+=ConsumableData.SPEED_PCT[id];boostApPct[p]+=ConsumableData.AP_PCT[id];boostCritPct[p]+=ConsumableData.CRIT_PCT[id];fruitMask[p]|=1<<id;if(boostRange[p]>2&&id!=ConsumableData.ULTIMATE)boostRange[p]=2;int sp=get(p),speedBase=sp>=0?Data.speed[sp]:0,speedPct=speedBase*boostSpeedPct[p]/100,speedRoom=Math.max(0,CombatRules.MAX_SPEED-speedBase-speedPct);if(boostSpeed[p]>speedRoom)boostSpeed[p]=speedRoom;
        eatenFruits[p*12+boostUses[p]]=id+1;consumables[id]--;boostUses[p]++;msg=Lang.t("Đã dùng ","Used ")+ConsumableData.name(id)+" ("+boostUses[p]+"/12)";return true;
    }

    public void rollFruitShop(){fruitBoughtMask=0;fruitBoughtRound=round;int randomSlots=round>=20?5:6;for(int s=0;s<randomSlots;s++){int id;do{id=rng.nextInt(ConsumableData.ULTIMATE);}while(!ConsumableData.randomEligible(id,round));fruitShop[s]=id;}if(round>=20)fruitShop[5]=ConsumableData.ULTIMATE;}
    public void rollStoneShop(){for(int s=0;s<stoneShop.length;s++){int type,guard=0;boolean dup;do{type=rng.nextInt(Data.NT);dup=false;for(int i=0;i<s;i++)if(stoneShop[i]==type)dup=true;}while(dup&&++guard<100);stoneShop[s]=type;}}
    public boolean rerollFruitShop(){int price=1;if(!unlimitedGold&&gold<price){msg=Lang.t("Không đủ 1 vàng để reset shop","Need 1 gold to reset shop");return false;}if(!unlimitedGold)gold-=price;rollFruitShop();msg=Lang.t("Đã reset shop vật phẩm (-1 vàng)","Item shop reset (-1 gold)");return true;}
    public boolean fruitBought(int slot){return slot>=0&&slot<fruitShop.length&&fruitBoughtRound==round&&(fruitBoughtMask&(1<<slot))!=0;}
    public boolean buyFruit(int slot){if(slot<0||slot>=fruitShop.length||fruitShop[slot]<0){msg=Lang.t("Ô chưa có trái","No fruit in this slot");return false;}int id=fruitShop[slot];if(fruitBought(slot)){msg=Lang.t("Ô trái này đã mua","This fruit slot was already bought");return false;}int price=ConsumableData.PRICE[id];if(gold<price&&!unlimitedGold){msg=Lang.t("Không đủ vàng","Not enough gold");return false;}if(!unlimitedGold)gold-=price;giveConsumable(id);if(fruitBoughtRound!=round){fruitBoughtRound=round;fruitBoughtMask=0;}fruitBoughtMask|=1<<slot;if(id==ConsumableData.ULTIMATE)ultimateBoughtRound=round;msg=Lang.t("Đã mua ","Bought ")+ConsumableData.name(id);return true;}
    public boolean buyStone(int slot){if(slot<0||slot>=stoneShop.length||stoneShop[slot]<0){msg=Lang.t("Ô chưa có đá","No stone in this slot");return false;}if(stoneBoughtRound==round){msg=Lang.t("Mỗi vòng chỉ mua được 1 đá hệ","Only one synergy stone may be bought each round");return false;}int type=stoneShop[slot];int price=SynergyStoneData.price(round);if(gold<price&&!unlimitedGold){msg=Lang.t("Không đủ vàng","Not enough gold");return false;}if(!unlimitedGold)gold-=price;synergyStoneBonus[type]++;stoneBoughtRound=round;msg=Lang.t("Đã kích +1 hệ ","Granted +1 ")+Lang.typeName(type);return true;}

    private void clearItems(int p,boolean returnToBag){
        for(int s=0;s<3;s++){int k=p*3+s,id=equip[k];if(returnToBag&&id>=0)giveItem(id);equip[k]=-1;}
    }

    /** Legacy entry now equips directly; automatic combining is intentionally disabled. */
    public boolean equipItem(int p,int id){
        return equipDirect(p,id);
    }

    /** Equip exactly the chosen inventory item; no implicit recipe is triggered. */
    public boolean unequip(int p,int slot){if(p<0||p>=BOARD+BENCH||get(p)<0||slot<0||slot>=3)return false;int id=itemAt(p,slot);if(id<0)return false;inventory[id]++;equip[p*3+slot]=-1;msg=Lang.t("Đã tháo ","Unequipped ")+ItemData.name(id);return true;}
    /** Preparation stats: upgrades and held stat bonuses, without team synergy/combat triggers. */
    public void previewUnit(int p,Unit u){int sp=get(p);u.sp=sp;u.maxHp=Data.hp[sp]+boostHp[p];u.atk=Data.atk[sp]+boostAtk[p];u.def=Data.def[sp]+boostDef[p];u.speDef=Data.speDef[sp]+boostSpeDef[p];u.speed=Data.speed[sp]+boostSpeed[p];u.range=Data.range[sp];u.maxMana=Data.mana[sp];u.mana=boostMana[p];u.skillBonus=boostAp[p];u.shield=u.shieldDone=u.crit=0;u.mega=u.shiny=false;u.specialForm=0;u.type1=type1At(p);u.type2=type2At(p);for(int s=0;s<3;s++){int id=itemAt(p,s);u.items[s]=id;if(id<0)continue;u.maxHp+=ItemData.HP[id];u.atk+=ItemData.ATK[id];u.def+=ItemData.DEF[id];u.speDef+=ItemData.SPE_DEF[id];u.speed+=ItemData.SPEED[id];u.mana+=ItemData.MP[id];u.skillBonus+=ItemData.AP[id];u.crit+=ItemData.CRIT[id];u.shield+=ItemData.SHIELD[id];u.shieldDone+=ItemData.SHIELD[id];}applyUpgrades(u,p);ExtraChessRules.applyUnit(this,u);u.hp=u.prevHp=u.maxHp;}
    public boolean equipDirect(int p,int id){
        if(p<0||p>=BOARD+BENCH||get(p)<0){msg=Lang.t("Hãy chọn Pokemon","Select a Pokemon");return false;}
        if(id<0||id>=inventory.length||inventory[id]<=0){msg=Lang.t("Không có vật phẩm","Item unavailable");return false;}
        if(ConsumableData.isFruitItem(id)){msg=Lang.t("Trái cây chỉ được dùng, không thể trang bị","Fruit is consumed and cannot be equipped");return false;}
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
        if(mode==MODE_RANDOM_TEAM){for(int i=0;i<shop.length;i++)shop[i]=-1;return;}
        returnShopToPool();
        if(testMode()&&testSpecies>=0){for(int i=0;i<shop.length;i++)shop[i]=testSpecies;return;}
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
        if(mode==MODE_RANDOM_TEAM){msg=Lang.t("Mode đội cố định không roll Pokémon","Fixed-team mode has no Pokemon rerolls");return false;}
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
        return buy(slot,0);
    }
    public boolean buy(int slot,int form) {
        if(mode==MODE_RANDOM_TEAM)return false;
        if(slot<0||slot>=shop.length)return false;
        mergeEventPos=-1;mergeEventTier=0;mergeEventSp=-1;
        int sp = shop[slot];
        if (sp < 0) { msg = Lang.t("Đã bán hết", "Sold out"); return false; }
        if(!Data.isBase(sp)){msg=Lang.t("Dạng tiến hóa không bán riêng","Evolutions are not sold separately");return false;}
        int c = shopCost(sp);
        if (gold < c) { msg = Lang.t("Không đủ vàng", "Not enough gold"); return false; }
        if(form!=0&&(!EvolutionBranchData.purchaseChoice(sp)||!EvolutionVariantData.validChoiceForm(sp,form)))return false;
        if (!addUnit(sp,form)) { msg = Lang.t("Hàng chờ đã đầy", "Bench is full"); return false; }
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
        return addUnit(sp,0);
    }
    private boolean addUnit(int sp,int form) {
        int slot = freeBench();
        if (slot < 0) {
            // bench full: only OK when this unit completes a triple
            if(hasEvolutionChoice()||!canMergeSpecies(sp)||mergeableCount(sp,form)<2)return false;
            mergeCopies(sp,2,form);
            mergeAll();
            return true;
        }
        set(slot, sp);
        specialForm[slot]=form;
        if(SpecialFormData.zygarde(sp))specialForm[slot]=SpecialFormData.ZYGARDE_10;
        mergeAll();
        return true;
    }

    /** evolves any species that has 3 copies (board + bench), cascading. */
    public void mergeAll() {
        if(hasEvolutionChoice())return;
        boolean again = true;
        while (again) {
            again = false;
            for (int sp = 0; sp < Data.N; sp++) {
                if (!canMergeSpecies(sp)) continue;
                int selectedForm=-1;for(int p=0;p<BOARD+BENCH;p++)if(get(p)==sp&&mergeableCount(sp,specialForm[p])>=3){selectedForm=specialForm[p];break;}
                if (selectedForm >= 0) {
                    mergeCopies(sp,3,selectedForm);
                    if(hasEvolutionChoice())return;
                    again = true;
                    break;
                }
            }
        }
    }

    /** Merge copies; every held item returns to reserve so the evolved unit starts empty. */
    private boolean canMergeSpecies(int sp){return Data.evo[sp]>=0||LaterFormData.hasEvolution(sp)||EvolutionVariantData.standalone(sp)||GenThreeFormData.regional(EvolutionVariantData.formFor(sp));}
    private boolean mergeableAt(int p,int sp){int form=specialForm[p];if(form>=41&&form<=44||GenThreeFormData.regional(form)||LaterFormData.regional(form))return get(p)==sp&&GenTwoFormData.evolved(sp,form)!=sp;if(form==0&&EvolutionVariantData.formFor(sp)==75&&Data.evo[sp]<0)return false;return get(p)==sp&&(Data.evo[sp]>=0||(form==0&&mega[p]==0&&!EvolutionBranchData.purchaseChoice(sp)));}
    private int mergeableCount(int sp,int form){int n=0;for(int p=0;p<BOARD+BENCH;p++)if(mergeableAt(p,sp)&&specialForm[p]==form)n++;return n;}
    private void mergeCopies(int sp,int needed,int form){
        int first=-1,removed=0,historyUses=-1;int[] history=new int[12];int[] best=new int[17];boolean keepShiny=false,keepMega=false;int keepForm=0,keepCells=0;
        for(int p=0;p<BOARD+BENCH&&removed<needed;p++)if(mergeableAt(p,sp)&&specialForm[p]==form){
            if(first<0)first=p;
            if(boostUses[p]>historyUses){historyUses=boostUses[p];for(int j=0;j<12;j++)history[j]=eatenFruits[p*12+j];}
            int[] v={boostHp[p],boostAtk[p],boostDef[p],boostSpeDef[p],boostSpeed[p],boostMana[p],boostAp[p],boostUses[p],boostRange[p],boostShield[p],boostHpPct[p],boostAtkPct[p],boostDefPct[p],boostSpeedPct[p],boostApPct[p],boostCritPct[p],fruitMask[p]};for(int i=0;i<best.length-1;i++)if(v[i]>best[i])best[i]=v[i];best[16]|=v[16];
            if(shiny[p]!=0)keepShiny=true;if(mega[p]!=0)keepMega=true;if(specialForm[p]!=0)keepForm=specialForm[p];if(zygardeCells[p]>keepCells)keepCells=zygardeCells[p];clearItems(p,true);clearBoosts(p);set(p,-1);removed++;
        }
        int evolved=keepForm==0?EvolutionBranchData.defaultNext(sp):GenTwoFormData.evolved(sp,keepForm);if(evolved<0)evolved=sp;if(((keepForm>=41&&keepForm<=44)||GenTwoFormData.cosmetic(keepForm))&&evolved!=sp)keepForm=0;if(keepForm==75&&Data.nationalDex(evolved)==862)keepForm=0;set(first,evolved);boostHp[first]=best[0];boostAtk[first]=best[1];boostDef[first]=best[2];boostSpeDef[first]=best[3];boostSpeed[first]=best[4];boostMana[first]=best[5];boostAp[first]=best[6];boostUses[first]=best[7];boostRange[first]=best[8];boostShield[first]=best[9];boostHpPct[first]=best[10];boostAtkPct[first]=best[11];boostDefPct[first]=best[12];boostSpeedPct[first]=best[13];boostApPct[first]=best[14];boostCritPct[first]=best[15];fruitMask[first]=best[16];shiny[first]=keepShiny?1:0;mega[first]=keepMega&&MegaData.available(evolved)?1:0;specialForm[first]=LaterFormData.contains(keepForm)?LaterFormData.evolutionForm(evolved,keepForm):SpecialFormData.evolutionVariant(keepForm)?EvolutionVariantData.formFor(evolved):keepForm;zygardeCells[first]=keepCells;
        mergeEventPos=first;mergeEventTier=Data.tier[evolved];mergeEventSp=evolved;
        for(int j=0;j<12;j++)eatenFruits[first*12+j]=history[j];
        if(EvolutionBranchData.available(evolved)&&(keepForm==0||LaterFormData.contains(keepForm)&&(!LaterFormData.regional(keepForm)||CanonicalEvolutionData.branches(evolved)!=null)))pendingEvolutionPos=first;
    }

    /** One component is awarded after every completed round. */
    private int randomComponent(){
        int count=0;for(int i=0;i<ItemData.count();i++)if(ItemData.isComponent(i))count++;
        int rank=rng.nextInt(count);
        for(int i=0;i<ItemData.count();i++)if(ItemData.isComponent(i)&&rank--==0)return i;
        return 0;
    }

    /** Three distinct completed items for the five-round reward popup. */
    public void craftedChoices(int[] out){
        int total=ItemData.recipeCount();
        for(int i=0;i<out.length;i++){
            int id,guard=0;boolean duplicate;
            do{ id=ItemData.recipeOutputAt(rng.nextInt(total));duplicate=false;
                for(int j=0;j<i;j++)if(out[j]==id)duplicate=true;
            }while(duplicate&&++guard<40);
            out[i]=id;
        }
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
        swapBoosts(a,b);
        if(pendingEvolutionPos==a)pendingEvolutionPos=b;else if(pendingEvolutionPos==b)pendingEvolutionPos=a;
        return true;
    }

    public boolean sell(int p) {
        if(mode==MODE_RANDOM_TEAM){msg=Lang.t("Không bán Pokémon đội cố định","Fixed-team Pokemon cannot be sold");return false;}
        int sp = get(p);
        if (sp < 0) return false;
        if (!unlimitedGold) gold += Data.sellValue(sp);
        int family=Data.fam[sp];
        if(Data.category[family]<5||(mode==MODE_LEGEND&&Data.category[family]==6))pool[family] += Data.copies(sp);
        clearItems(p,true);
        clearBoosts(p);
        set(p, -1);
        if(pendingEvolutionPos==p)pendingEvolutionPos=-1;
        return true;
    }

    // ---- rounds ----------------------------------------------------------

    public boolean isBoss() {
        return mode==MODE_BOSS_RUSH||StageRoad.boss(round,mode);
    }

    private void applyUpgrades(Unit u,int p){u.maxHp+=Data.hp[u.sp]*boostHpPct[p]/100;u.hp=u.prevHp=u.maxHp;u.atk+=Data.atk[u.sp]*boostAtkPct[p]/100;u.def+=Data.def[u.sp]*boostDefPct[p]/100;u.speed+=Data.speed[u.sp]*boostSpeedPct[p]/100;u.skillBonus+=Math.max(0,Data.atk[u.sp]*boostApPct[p]/100);u.crit+=boostCritPct[p];u.range+=boostRange[p];u.shield+=boostShield[p];u.shieldDone+=boostShield[p];u.fruitMask=fruitMask[p];if((fruitMask[p]&(1<<ConsumableData.GOLDEN_RAZZ))!=0){int q=u.maxHp/10;u.shield+=q;u.shieldDone+=q;}if(mega[p]!=0)MegaData.apply(u);else SpecialFormData.apply(u,specialForm[p]);if(shiny[p]!=0)ShinyData.apply(u);u.speed=CombatRules.cappedSpeed(u.speed);if(u.mana>u.maxMana)u.mana=u.maxMana;u.cd=CombatRules.cooldownTicks(1000,u.speed);}
    public Battle makeBattle() {
        int[] battleForms=new int[BOARD];for(int p=0;p<BOARD;p++)battleForms[p]=mega[p]!=0?-1:specialForm[p];
        Battle b=new Battle(board,enemy,enemyScale,rng,equip,enemyEquip,boostHp,boostAtk,boostDef,boostSpeDef,boostSpeed,boostMana,boostAp,synergyStoneBonus,battleForms);
        for(int i=0;i<b.n;i++){Unit u=b.units[i];if(u.side!=0)continue;int p=(u.y-3)*8+u.x;if(p>=0&&p<BOARD)applyUpgrades(u,p);}
        ExtraChessRules.apply(this,b);return b;
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
        lastXp = lastWon?2:0;
        if(lastXp>0)gainXp(lastXp);
        if (lastWon) {
            streak = streak > 0 ? streak + 1 : 1;
            if (!endlessMode()&&round >= maxRound()) { over = true; victory = true; }
        } else {
            streak = streak < 0 ? streak - 1 : -1;
            lastDamage = EconomyRules.playerDamage(round,b.aliveTierSum(1));
            hp -= lastDamage;
            if (hp <= 0) {if(hp<0)hp=0;over = true;}
        }
        if (!over) {
            lastBaseGold=mode==MODE_LEGEND?(lastWon?10:5):EconomyRules.BASE_INCOME;lastInterest=EconomyRules.interest(gold);
            lastStreakGold=EconomyRules.streakBonus(streak);lastVictoryGold=EconomyRules.victoryBonus(lastWon);lastItemGold=b.itemGold;
            lastGold=lastBaseGold+lastInterest+lastStreakGold+lastVictoryGold+Math.max(0,lastItemGold);
        }
        if(lastWon){lastItem=randomComponent();giveItem(lastItem);if(mode==MODE_TOWER&&round%5==0&&!over)towerPending=true;if(mode==MODE_BOSS_RUSH&&!over){hp=Math.min(100,hp+10);giveItem(randomComponent());}}
    }

    /** moves to the next round: pays income, gives xp, refreshes the shop. */
    public void nextRound() {
        if(towerPending)return;
        if (!unlimitedGold) gold += lastGold;
        else gold = 9999;
        if(lastWon)round++;
        if(shopLocked)shopLocked=false;else rollShop();
        if(lastWon){rollFruitShop();rollStoneShop();}
        genEnemy();
    }

    // ---- enemy generation ------------------------------------------------

    private int pickEnemySp(int maxCost, int theme) {
        int gen=enemyGeneration();
        for (int tries = 0; tries < 40; tries++) {
            int c;
            int r = rng.nextInt(100);
            if (r < 45) c = maxCost;
            else if (r < 80) c = maxCost - 1;
            else c = maxCost - 1 - rng.nextInt(2);
            if (c < 1) c = 1;
            int sp = rng.nextInt(Data.N);
            if (!Data.isBase(sp) || Data.cost[sp] != c || (gen>0&&Data.generation(sp)!=gen) || (gen==0&&sp>=Data.CORE_N)) continue;
            if (theme >= 0 && !Data.famHasType(sp, theme)) continue;
            return sp;
        }
        int start=rng.nextInt(Data.N);for(int i=0;i<Data.N;i++){int sp=(start+i)%Data.N;if(Data.isBase(sp)&&Data.cost[sp]<=maxCost&&(gen>0?Data.generation(sp)==gen:sp<Data.CORE_N))return sp;}
        if(gen>0)for(int i=0;i<Data.N;i++){int sp=(start+i)%Data.N;if(Data.isBase(sp)&&Data.generation(sp)==gen)return sp;}
        return 0;
    }

    // difficulty tuning
    public static int EVO1 = 30, EVO2 = 35, EVO3 = 30, SCALE_BASE = 80, SCALE_STEP = 3;

    private static final int[] COL_ORDER = { 3, 4, 2, 5, 1, 6, 0, 7 };

    public void genEnemy() {
        int r = round,dr=endlessMode()?Math.min(round,1000):round;
        for (int i = 0; i < BOARD; i++) enemy[i] = -1;
        for (int i = 0; i < enemyEquip.length; i++) enemyEquip[i] = -1;
        boolean boss = isBoss();
        int n;
        if(mode==MODE_BOSS_RUSH){genGenerationBoss(r);int guards=Math.min(6,1+r/4);for(int k=0;k<guards;k++)enemy[16+COL_ORDER[k]]=pickEnemySp(Math.min(5,1+r/4),-1);enemyScale=115+r*8;return;}
        if(mode==MODE_LEGEND){genLegendEnemy(r,boss);return;}
        if((generationMode()||endlessMode()||mode==MODE_TOWER)&&boss){genGenerationBoss(dr);return;}
        if(!generationMode()&&!endlessMode()&&!ExtraChessRules.mode(mode)&&EnemyFormation.apply(this))return;
        if (dr == 1) n = 1;
        else if (dr <= 3) n = 2;
        else n = 2 + (dr * 2) / 5;
        if (boss) n++;
        if(mode==MODE_THIRTY&&boss)n=1;
        if (n > 9) n = 9;
        int maxCost = dr < 4 ? 1 : (dr < 8 ? 2 : (dr < 11 ? 3 : (dr < 18 ? 4 : 5)));
        int theme = -1;
        if (r == 8) theme = Data.T_ROCK;
        else if (r == 12) theme = Data.T_WATER;
        else if (r == 16) theme = Data.T_PSY;
        else if (r == 20) theme = Data.T_DRAGON;

        int[] list = new int[n];
        for (int k = 0; k < n; k++) {
            int sp = pickEnemySp(maxCost, theme);
            int steps = 0;
            if (dr >= 6 && rng.pct(EVO1)) steps++;
            if (dr >= 11 && rng.pct(EVO2)) steps++;
            if (dr >= 16 && rng.pct(EVO3)) steps++;
            while (steps > 0 && Data.evo[sp] >= 0) {int next=Data.evo[sp];if(enemyGeneration()>0&&Data.generation(next)!=enemyGeneration())break;sp=next;steps--;}
            list[k] = sp;
        }
        if (r == maxRound()&&enemyGeneration()==0) list[0] = Data.MEWTWO;

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
        else if (dr <= 3) { enemyScale = 75 + dr * 5; enemyName = "Wild Pokemon"; }
        else {
            enemyScale = SCALE_BASE + dr * SCALE_STEP + (boss ? 15 : 0);
            if (r == 8) enemyName = "Boss: Rock Tamer";
            else if (r == 12) enemyName = "Boss: Tide Master";
            else if (r == 16) enemyName = "Boss: Mind Sage";
            else if (r == 20) enemyName = "CHAMPION";
            else enemyName = NAMES[rng.nextInt(NAMES.length)];
        }
    }

    private void genLegendEnemy(int r,boolean boss){
        int n=r<=2?1:(2+r/6);if(boss)n++;if(n>8)n=8;
        for(int k=0;k<n;k++){
            int sp=randomBaseCategory(6);if(sp<0)sp=Data.MEWTWO;
            int row=Data.range[sp]-1;if(row<0)row=0;if(row>2)row=2;
            for(int tr=0;tr<3;tr++){int rr=(row+tr)%3;boolean placed=false;for(int c=0;c<8;c++){int p=rr*8+COL_ORDER[c];if(enemy[p]<0){enemy[p]=sp;placed=true;break;}}if(placed)break;}
        }
        enemyScale=85+r*3+(boss?18:0);
        enemyName=boss?Lang.t("BOSS THẦN THÚ","LEGENDARY BOSS"):Lang.t("ĐỘI THẦN THÚ","LEGENDARY TEAM");
    }

    private int findSp(String name){for(int i=0;i<Data.N;i++)if(Data.name[i].equals(name))return i;return 0;}

    private void genGenerationBoss(int r){
        int sp=generationBossSpecies(r);
        int copies=r%30==25?2:1;for(int i=0;i<copies;i++)enemy[1*8+COL_ORDER[i]]=sp;
        enemyScale=125+r*4;enemyName="BOSS: "+Data.name[sp]+(copies>1?" x2":"");
    }
    public int generationBossSpecies(int r){
        int gen=enemyGeneration(),sp=-1;if(gen<=0)return-1;int stage=((r-1)%30)+1;
        if(gen==1)sp=findSp(stage==5?"Magikarp":stage==10?"Moltres":stage==15?"Articuno":stage==20?"Zapdos":stage==25?"Mewtwo":"Mew");
        else{int rank=stage/5,count=0;for(int i=0;i<Data.N;i++)if(Data.generation(i)==gen&&Data.category[i]==6){count++;if(count==rank)sp=i;}if(sp<0)for(int i=Data.N-1;i>=0;i--)if(Data.generation(i)==gen&&Data.isBase(i)){sp=i;break;}}
        return sp<0?0:sp;
    }
}

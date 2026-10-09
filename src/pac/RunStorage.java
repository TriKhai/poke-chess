package pac;
// Gen 2 extends valid form IDs without changing existing save field layout.

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.microedition.rms.RecordStore;

/** Versioned run snapshot kept separate from the legacy progression save. */
public final class RunStorage {
    private RunStorage(){}
    private static final String STORE="pacrun1";
    private static final int VERSION=13;

    public static boolean has(){RecordStore rs=null;try{rs=RecordStore.openRecordStore(STORE,false);return rs.getNumRecords()>0;}catch(Exception e){return false;}finally{close(rs);}}
    public static void clear(){try{RecordStore.deleteRecordStore(STORE);}catch(Exception e){}}

    private static void ints(DataOutputStream out,int[] a)throws Exception{out.writeInt(a.length);for(int i=0;i<a.length;i++)out.writeInt(a[i]);}
    private static void readInts(DataInputStream in,int[] a)throws Exception{int n=in.readInt();if(n<0||n>100000)throw new Exception("bad array");for(int i=0;i<n;i++){int v=in.readInt();if(i<a.length)a[i]=v;}}

    static byte[] encode(Run r){try{
        ByteArrayOutputStream bo=new ByteArrayOutputStream();DataOutputStream out=new DataOutputStream(bo);
        out.writeInt(VERSION);out.writeInt(r.mode);out.writeInt(r.rng.state());out.writeInt(r.round);out.writeInt(r.hp);out.writeInt(r.gold);out.writeInt(r.level);out.writeInt(r.xp);out.writeInt(r.streak);out.writeBoolean(r.shopLocked);
        ints(out,r.board);ints(out,r.bench);ints(out,r.shop);ints(out,r.pool);ints(out,r.equip);ints(out,r.inventory);ints(out,r.enemy);ints(out,r.enemyEquip);
        out.writeInt(r.enemyScale);out.writeUTF(r.enemyName==null?"":r.enemyName);ints(out,r.resultPath);out.writeInt(r.resultCount);
        out.writeInt(r.draftStage);out.writeInt(r.poolType);ints(out,r.poolTypes);
        ints(out,r.consumables);ints(out,r.boostHp);ints(out,r.boostAtk);ints(out,r.boostDef);ints(out,r.boostSpeDef);ints(out,r.boostSpeed);ints(out,r.boostMana);ints(out,r.boostAp);ints(out,r.boostUses);
        ints(out,r.boostRange);ints(out,r.boostShield);ints(out,r.boostHpPct);ints(out,r.boostAtkPct);ints(out,r.boostDefPct);ints(out,r.boostSpeedPct);ints(out,r.boostApPct);ints(out,r.boostCritPct);ints(out,r.fruitMask);ints(out,r.fruitShop);out.writeInt(r.ultimateBoughtRound);out.writeInt(r.fruitBoughtMask);out.writeInt(r.fruitBoughtRound);ints(out,r.stoneShop);ints(out,r.synergyStoneBonus);out.writeInt(r.stoneBoughtRound);ints(out,r.shiny);out.writeInt(r.shinyBoughtRound);ints(out,r.mega);out.writeInt(r.megaBoughtRound);ints(out,r.specialForm);ints(out,r.zygardeCells);out.writeInt(r.memoryBoughtRound);out.writeInt(r.cubeBoughtRound);out.writeInt(r.pendingEvolutionPos);ints(out,r.eatenFruits);out.writeInt(r.modeHpPct);out.writeInt(r.modeAtkPct);out.writeInt(r.modeArmor);out.writeBoolean(r.towerPending);out.flush();return bo.toByteArray();
    }catch(Exception e){return null;}}

    private static boolean speciesValid(int[] a){for(int i=0;i<a.length;i++)if(a[i]<-1||a[i]>=Data.N)return false;return true;}
    private static boolean nonnegative(int[] a){for(int i=0;i<a.length;i++)if(a[i]<0)return false;return true;}
    static Run decode(byte[] b){try{DataInputStream in=new DataInputStream(new ByteArrayInputStream(b));int version=in.readInt();if(version<1||version>VERSION)return null;
        int mode=in.readInt(),rngState=in.readInt();if(mode<Run.MODE_NORMAL||mode>Run.MODE_BOSS_RUSH||mode==Run.MODE_TEST)return null;Run r=new Run(1,mode);r.round=in.readInt();r.hp=in.readInt();r.gold=in.readInt();r.level=in.readInt();r.xp=in.readInt();r.streak=in.readInt();r.shopLocked=in.readBoolean();
        if(r.round<1||r.round>r.maxRound()||r.hp<1||r.gold<0||r.level<1||r.level>Data.MAX_LEVEL||r.xp<0)return null;
        readInts(in,r.board);readInts(in,r.bench);readInts(in,r.shop);readInts(in,r.pool);readInts(in,r.equip);readInts(in,r.inventory);readInts(in,r.enemy);readInts(in,r.enemyEquip);
        r.enemyScale=in.readInt();r.enemyName=in.readUTF();readInts(in,r.resultPath);r.resultCount=in.readInt();if(r.resultCount<0||r.resultCount>r.resultPath.length)throw new Exception("bad results");
        if(version>=2){r.draftStage=in.readInt();r.poolType=in.readInt();readInts(in,r.poolTypes);if(r.draftStage<0||r.draftStage>2||r.poolType<-1||r.poolType>=Data.NT)return null;for(int i=0;i<r.poolTypes.length;i++)if(r.poolTypes[i]<-1||r.poolTypes[i]>=Data.NT)return null;}else{r.draftStage=2;r.poolType=-1;}
        if(version>=3){readInts(in,r.consumables);readInts(in,r.boostHp);readInts(in,r.boostAtk);readInts(in,r.boostDef);readInts(in,r.boostSpeDef);readInts(in,r.boostSpeed);readInts(in,r.boostMana);readInts(in,r.boostAp);readInts(in,r.boostUses);}
        if(version>=4){readInts(in,r.boostRange);readInts(in,r.boostShield);readInts(in,r.boostHpPct);readInts(in,r.boostAtkPct);readInts(in,r.boostDefPct);readInts(in,r.boostSpeedPct);readInts(in,r.boostApPct);readInts(in,r.boostCritPct);readInts(in,r.fruitMask);readInts(in,r.fruitShop);r.ultimateBoughtRound=in.readInt();if(version>=5){r.fruitBoughtMask=in.readInt();r.fruitBoughtRound=in.readInt();}if(version>=6){readInts(in,r.stoneShop);readInts(in,r.synergyStoneBonus);r.stoneBoughtRound=in.readInt();for(int i=0;i<r.synergyStoneBonus.length;i++)if(r.synergyStoneBonus[i]<0)r.synergyStoneBonus[i]=0;}else r.rollStoneShop();if(version>=7){readInts(in,r.shiny);r.shinyBoughtRound=in.readInt();for(int i=0;i<r.shiny.length;i++)r.shiny[i]=r.shiny[i]==0?0:1;}}else{r.rollFruitShop();r.rollStoneShop();}
        if(version>=9){readInts(in,r.mega);r.megaBoughtRound=in.readInt();for(int i=0;i<r.mega.length;i++)r.mega[i]=r.mega[i]==0?0:1;}
        if(version>=10){readInts(in,r.specialForm);readInts(in,r.zygardeCells);r.memoryBoughtRound=in.readInt();r.cubeBoughtRound=in.readInt();for(int i=0;i<r.specialForm.length;i++){if(r.specialForm[i]<0||r.specialForm[i]>SpecialFormData.MAX_FORM)r.specialForm[i]=0;if(r.zygardeCells[i]<0)r.zygardeCells[i]=0;if(r.zygardeCells[i]>95)r.zygardeCells[i]=95;}}
        if(version>=11){r.pendingEvolutionPos=in.readInt();if(r.pendingEvolutionPos<-1||r.pendingEvolutionPos>=Run.BOARD+Run.BENCH)return null;if(r.hasEvolutionChoice()&&!EvolutionBranchData.available(r.get(r.pendingEvolutionPos)))return null;}if(version<8){r.fruitBoughtMask=0;r.fruitBoughtRound=r.round;}
        // v1.5.5b migration: standalone transformed species cannot remain in a saved shop/pool.
        if(version>=12){readInts(in,r.eatenFruits);for(int i=0;i<r.eatenFruits.length;i++)if(r.eatenFruits[i]<0||r.eatenFruits[i]>ConsumableData.SHINY_CHARM)return null;}else r.migrateFruitHistory();
        if(version>=13){r.modeHpPct=in.readInt();r.modeAtkPct=in.readInt();r.modeArmor=in.readInt();r.towerPending=in.readBoolean();if(r.modeHpPct<0||r.modeHpPct>150||r.modeAtkPct<0||r.modeAtkPct>100||r.modeArmor<0||r.modeArmor>30)return null;}
        if(!speciesValid(r.board)||!speciesValid(r.bench)||!speciesValid(r.shop)||!speciesValid(r.enemy)||!nonnegative(r.pool)||!nonnegative(r.inventory)||!nonnegative(r.consumables))return null;
        for(int i=0;i<r.equip.length;i++)if(r.equip[i]<-1||r.equip[i]>=ItemData.ID.length)return null;
        for(int i=0;i<r.enemyEquip.length;i++)if(r.enemyEquip[i]<-1||r.enemyEquip[i]>=ItemData.ID.length)return null;
        if(r.towerPending){if(r.mode!=Run.MODE_TOWER||r.round%5!=0||r.round>=r.maxRound()||r.draftStage!=2)return null;r.lastWon=true;}
        for(int p=0;p<Run.BOARD+Run.BENCH;p++)if(r.get(p)>=0&&r.isMega(p)){if(!MegaData.available(r.get(p)))return null;r.specialForm[p]=0;}
        for(int i=0;i<Data.N;i++)if(Data.nationalDex(i)==1009||!Data.isBase(i))r.pool[i]=0;
        boolean refill=false;for(int i=0;i<r.shop.length;i++)if(r.shop[i]>=0&&(Data.nationalDex(r.shop[i])==1009||!Data.isBase(r.shop[i]))){r.shop[i]=-1;refill=true;}
        for(int p=0;p<Run.BOARD+Run.BENCH;p++)if(r.get(p)>=0&&SpecialFormData.zygarde(r.get(p))&&!r.isMega(p)&&r.specialForm[p]==SpecialFormData.NONE)r.specialForm[p]=SpecialFormData.ZYGARDE_10;
        if(refill&&r.draftStage==2)r.rollShop();
        r.rng.restore(rngState);r.mergeAll();return r;
    }catch(Exception e){return null;}}

    public static void save(Run r){if(r!=null&&r.testMode())return;if(r==null||r.over){clear();return;}RecordStore rs=null;try{byte[] b=encode(r);if(b==null)return;rs=RecordStore.openRecordStore(STORE,true);if(rs.getNumRecords()==0)rs.addRecord(b,0,b.length);else rs.setRecord(1,b,0,b.length);}catch(Exception e){}finally{close(rs);}}
    public static Run load(){RecordStore rs=null;try{rs=RecordStore.openRecordStore(STORE,false);Run r=decode(rs.getRecord(1));if(r==null)clear();return r;}catch(Exception e){clear();return null;}finally{close(rs);}}
    private static void close(RecordStore rs){if(rs!=null)try{rs.closeRecordStore();}catch(Exception e){}}
}

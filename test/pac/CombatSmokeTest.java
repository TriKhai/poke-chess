package pac;

/** Desktop-only smoke tests. Not packaged in the MIDlet JAR. */
public final class CombatSmokeTest {
    private static void check(boolean ok, String message) {
        if (!ok) throw new RuntimeException(message);
    }

    public static void main(String[] args) {
        check(Data.N == 386, "roster must contain Gen 1-3");
        check(Data.speed[0] == 51, "Bulbasaur speed must come from source CSV");
        check(Data.speDef[0] == 4, "Bulbasaur special defense must come from source CSV");
        check("Magical Leaf".equals(Data.skillName[0]), "original ability name missing");
        check(CombatRules.physicalDamage(100, 20) == 50, "armor formula mismatch");
        check(CombatRules.specialDamage(100, 5) == 80, "special defense formula mismatch");

        Save.reset();
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

        System.out.println("CombatSmokeTest OK: roster, items, merge, shop lock, modes and 101 battles");
    }
}

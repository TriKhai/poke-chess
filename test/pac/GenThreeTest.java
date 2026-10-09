package pac;

public final class GenThreeTest {
    private static void check(boolean ok,String text){if(!ok)throw new RuntimeException(text);}
    private static int sp(int dex){return Data.speciesForDex(dex);}
    public static void run(){
        int[] before={252,253,255,256,258,259,263,265,266,268,280,281,290,299,315,355,356,361,366,406,433};
        int[] after={253,254,256,257,259,260,264,266,267,269,281,282,291,476,407,356,477,362,367,315,358};
        for(int i=0;i<before.length;i++)check(Data.evo[sp(before[i])]==sp(after[i]),"Gen3 independent edge missing "+before[i]+" -> "+after[i]);
        int routes=0;
        for(int s=0;s<Data.N;s++)if(Data.generation(s)==3&&Data.evo[s]>=0){
            int next=EvolutionBranchData.defaultNext(s),choices=EvolutionBranchData.count(next);
            for(int c=0;c<choices;c++)for(int split=0;split<2;split++){
                Run r=new Run(20000+s,Run.MODE_UNLIMITED);r.bench[0]=r.bench[1]=s;if(split==0)r.bench[2]=s;else r.board[0]=s;
                int p=split==0?24:0;r.shiny[p]=1;r.boostHp[p]=17;r.mergeAll();
                check(r.get(p)==next,"Gen3 triple failed "+Data.name[s]);
                r=RunStorage.decode(RunStorage.encode(r));check(r!=null,"Gen3 pending save failed");
                if(r.hasEvolutionChoice())check(r.chooseEvolution(c),"Gen3 branch failed");
                check(r.get(p)==EvolutionBranchData.choiceSpecies(next,c)&&r.specialFormAt(p)==EvolutionBranchData.choiceForm(next,c)&&r.shiny[p]==1&&r.boostHp[p]==17,"Gen3 branch state lost "+Data.name[s]);routes++;
            }
            if(Data.isBase(s)){
                Run full=new Run(21000+s,Run.MODE_UNLIMITED);for(int p=0;p<Run.BENCH;p++)full.bench[p]=Data.MEWTWO;full.bench[0]=full.bench[1]=s;full.shop[0]=s;
                check(full.buy(0)&&full.get(24)==next,"Gen3 full bench buy failed "+Data.name[s]);
            }
        }
        int[] hidden={268,269,368,475,478,292,862};for(int i=0;i<hidden.length;i++)check(!Data.isBase(sp(hidden[i])),"branch sold separately "+hidden[i]);
        for(int full=0;full<2;full++){
            Run r=new Run(22000+full,Run.MODE_UNLIMITED);int z=sp(263);if(full!=0)for(int p=0;p<Run.BENCH;p++)r.bench[p]=Data.MEWTWO;
            r.bench[0]=r.bench[1]=z;r.specialForm[24]=r.specialForm[25]=74;r.shop[0]=z;
            check(r.buy(0,74)&&r.get(24)==sp(264)&&r.specialFormAt(24)==75,"Galar Zigzagoon merge failed");
            r.bench[1]=r.bench[2]=sp(264);r.specialForm[25]=r.specialForm[26]=75;r.shiny[24]=1;r.mergeAll();
            check(r.get(24)==sp(862)&&r.specialFormAt(24)==0&&r.shiny[24]==1,"Galar Linoone -> Obstagoon failed");
            r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.get(24)==sp(862),"Obstagoon save failed");
        }
        Run mixed=new Run(22500,Run.MODE_UNLIMITED);mixed.bench[0]=mixed.bench[1]=mixed.bench[2]=sp(263);mixed.specialForm[26]=74;mixed.mergeAll();check(mixed.get(24)==sp(263)&&mixed.get(25)==sp(263)&&mixed.get(26)==sp(263),"mixed Galar/normal copies merged");
        Run syn=new Run(22501,Run.MODE_UNLIMITED);syn.board[0]=sp(263);syn.specialForm[0]=74;syn.board[1]=sp(264);syn.specialForm[1]=75;syn.board[2]=sp(862);int[] counts=new int[Data.NT];syn.countBoardSyn(counts);check(counts[Data.T_DARK]==1&&syn.makeBattle().synP[Data.T_DARK]==1,"Galar evolution chain doubled synergy");
        Run purple=new Run(22502,Run.MODE_UNLIMITED);purple.board[0]=sp(352);purple.shiny[0]=1;purple.giveConsumable(ConsumableData.MEMORY_DISC);check(purple.useConsumable(0,ConsumableData.MEMORY_DISC),"Kecleon item failed");purple=RunStorage.decode(RunStorage.encode(purple));check(purple!=null&&purple.specialFormAt(0)==79&&purple.isShiny(0),"Kecleon save/shiny failed");
        int[] mega={254,282,302,303,308,310,319,323,334,354,358,359,362,380,381,384};
        for(int i=0;i<mega.length;i++)for(int shiny=0;shiny<2;shiny++){
            int s=sp(mega[i]);check(MegaData.available(s),"Gen3 Mega missing");Run r=new Run(23000+i,Run.MODE_UNLIMITED);r.board[0]=s;r.shiny[0]=shiny;r.boostHp[0]=17;r.giveConsumable(ConsumableData.MEGA_STONE);
            check(r.useConsumable(0,ConsumableData.MEGA_STONE),"Gen3 Mega item failed");r=RunStorage.decode(RunStorage.encode(r));Unit u=new Unit();r.previewUnit(0,u);
            check(r.isMega(0)&&u.maxHp>=Data.hp[s]+17&&r.shiny[0]==shiny&&u.atk>=Data.atk[s],"Gen3 Mega stats/save failed");
        }
        int[] missing={257,260,306,373,376};for(int i=0;i<missing.length;i++)check(!MegaData.available(sp(missing[i])),"incomplete Mega enabled");
        Run primal=new Run(23500,Run.MODE_UNLIMITED);primal.board[0]=sp(383);primal.giveConsumable(ConsumableData.MEMORY_DISC);check(primal.useConsumable(0,ConsumableData.MEMORY_DISC)&&primal.type2At(0)==Data.T_FIRE&&primal.makeBattle().synP[Data.T_FIRE]==1,"Primal Groudon Fire type/synergy failed");
        Run nin=new Run(23501,Run.MODE_UNLIMITED);nin.board[0]=sp(291);nin.board[1]=sp(292);nin.countBoardSyn(counts);check(counts[Data.T_BUG]==2,"Ninjask/Shedinja branches deduped incorrectly");
        Run weather=new Run(24000,Run.MODE_UNLIMITED);weather.board[0]=sp(351);int[] types={Data.T_FIRE,Data.T_WATER,Data.T_ICE,Data.T_FIRE};
        for(int i=0;i<4;i++){weather.giveConsumable(ConsumableData.MEMORY_DISC);check(weather.useConsumable(0,ConsumableData.MEMORY_DISC)&&weather.type1At(0)==types[i],"Castform cycle/type failed");weather=RunStorage.decode(RunStorage.encode(weather));check(weather!=null,"Castform save failed");}
        System.out.println("Gen3Audit OK: "+routes+" merge/branch/split/save routes, full bench, Galar chain, 16 Megas x shiny, weather and resource exclusions");
    }
}

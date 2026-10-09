package pac;

/** Desktop regression matrix: ordinary/regional triples, choices and item forms. */
public final class AllGenerationTest {
    private static void check(boolean ok,String text){if(!ok)throw new RuntimeException(text);}
    private static int sp(int dex){return Data.speciesForDex(dex);}
    public static void run(){
        int[] parent={387,388,390,391,393,394,438,439,440,446,458,495,496,498,499,501,502,519,520,532,533,602,603,650,651,653,654,656,657,679,680,704,705,722,723,725,726,728,729,789,790,808,810,811,813,814,816,817,848,891,906,907,909,910,912,913,935,960,961,974,996,997};
        int[] child ={388,389,391,392,394,395,185,122,113,143,226,496,497,499,500,502,503,520,521,533,534,603,604,651,652,654,655,657,658,680,681,705,706,723,724,726,727,729,730,790,791,809,811,812,814,815,817,818,849,892,907,908,910,911,913,914,936,961,962,975,997,998};
        for(int i=0;i<parent.length;i++)if(sp(parent[i])>=0&&sp(child[i])>=0)check(Data.evo[sp(parent[i])]==sp(child[i]),"Canonical edge missing "+parent[i]+" -> "+child[i]);
        int routes=0,forms=0,mega=0;
        for(int s=0;s<Data.N;s++)if(Data.evo[s]>=0){
            int next=EvolutionBranchData.defaultNext(s),n=EvolutionBranchData.count(next);
            for(int c=0;c<n;c++)for(int split=0;split<2;split++){
                Run r=new Run(30000+s,Run.MODE_UNLIMITED);r.bench[0]=r.bench[1]=s;if(split==0)r.bench[2]=s;else r.board[0]=s;
                int p=split==0?24:0;r.shiny[p]=1;r.boostHp[p]=17;r.giveItem(0);r.mergeAll();
                check(r.get(p)==next,"Triple failed "+Data.name[s]);r=RunStorage.decode(RunStorage.encode(r));check(r!=null,"Pending save failed");
                if(r.hasEvolutionChoice())check(r.chooseEvolution(c),"Choice failed "+Data.name[s]);
                check(r.get(p)==EvolutionBranchData.choiceSpecies(next,c)&&r.specialFormAt(p)==EvolutionBranchData.choiceForm(next,c)&&r.isShiny(p)&&r.boostHp[p]==17,"Merge state lost "+Data.name[s]+" option "+c);
                r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.get(p)==EvolutionBranchData.choiceSpecies(next,c),"Completed save failed");routes++;
            }
            if(Data.isBase(s)){
                Run r=new Run(31000+s,Run.MODE_UNLIMITED);for(int p=0;p<Run.BENCH;p++)r.bench[p]=Data.MEWTWO;r.bench[0]=r.bench[1]=s;r.shop[0]=s;
                check(r.buy(0)&&r.get(24)==next,"Full bench merge-buy failed "+Data.name[s]);
            }
        }
        for(int s=0;s<Data.N;s++){
            for(int f=80;f<=SpecialFormData.MAX_FORM;f++)if(LaterFormData.contains(f)&&LaterFormData.key(f).startsWith(pad(Data.nationalDex(s))+"-")){
                Run r=new Run(32000+s,Run.MODE_UNLIMITED);r.board[0]=s;r.shiny[0]=1;r.boostHp[0]=17;
                int current=0,steps=0;while(current!=f&&steps++<80){r.giveConsumable(ConsumableData.MEMORY_DISC);check(r.useConsumable(0,ConsumableData.MEMORY_DISC),"Memory unavailable "+Data.name[s]);current=r.specialFormAt(0);}
                check(current==f,"Memory cycle unreachable "+LaterFormData.name(f));r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.specialFormAt(0)==f&&r.isShiny(0),"Form save failed");
                check(r.type1At(0)==LaterFormData.type1(s,f)&&r.type2At(0)==LaterFormData.type2(s,f),"Form type mismatch");
                Unit preview=new Unit();r.previewUnit(0,preview);if(LaterFormData.upgrade(f))check(preview.maxHp>Data.hp[s]+17&&preview.atk>Data.atk[s],"Special upgrade has no stat bonus");
                Battle b=r.makeBattle();check(b.units[0].primaryType()==r.type1At(0)&&b.units[0].secondaryType()==r.type2At(0),"Combat form type mismatch");
                Run syn=new Run(33000+s,Run.MODE_UNLIMITED);syn.board[0]=syn.board[1]=s;syn.specialForm[1]=f;int[] counts=new int[Data.NT];syn.countBoardSyn(counts);
                if(!LaterFormData.regional(f))for(int t=0;t<Data.NT;t++)check(counts[t]<=1,"Same form species doubled synergy "+LaterFormData.name(f));forms++;
            }
            if(LaterMegaData.index(s)>=0)for(int shiny=0;shiny<2;shiny++){
                Run r=new Run(34000+s,Run.MODE_UNLIMITED);r.board[0]=s;r.shiny[0]=shiny;r.boostHp[0]=17;r.giveConsumable(ConsumableData.MEGA_STONE);
                check(r.useConsumable(0,ConsumableData.MEGA_STONE),"Mega item failed "+Data.name[s]);r=RunStorage.decode(RunStorage.encode(r));Unit u=new Unit();r.previewUnit(0,u);
                check(r.isMega(0)&&u.maxHp>Data.hp[s]+17&&u.atk>Data.atk[s]&&r.shiny[0]==shiny,"Mega stats/save failed");mega++;
            }
        }
        int[] regionalOnly={862,864,867,902,903,904,980};for(int i=0;i<regionalOnly.length;i++){int s=sp(regionalOnly[i]);if(s>=0){check(!Data.isBase(s),"Regional child sold as base");for(int p=0;p<Data.N;p++)check(Data.evo[p]!=s,"Regional child on ordinary route");}}
        int[] regionalParents={550,554,562,570,705,19,27,37,50,52,58,74,75,77,79,100};for(int i=0;i<regionalParents.length;i++){
            int s=sp(regionalParents[i]),form=LaterFormData.regionalFor(s),next=LaterFormData.evolved(s,form);if(form==0||next<0)continue;
            Run r=new Run(35000+s,Run.MODE_UNLIMITED);r.bench[0]=r.bench[1]=r.bench[2]=s;r.specialForm[24]=r.specialForm[25]=r.specialForm[26]=form;r.mergeAll();
            check(r.get(24)==next&&r.specialFormAt(24)==LaterFormData.evolutionForm(next,form),"Regional chain failed "+Data.name[s]);
            if(regionalParents[i]==570)check(r.specialFormAt(24)==33&&r.type1At(24)==Data.T_NORMAL&&r.type2At(24)==Data.T_GHOST,"Hisui Zorua lost regional form at Zoroark");
            if(regionalParents[i]==705)check(r.specialFormAt(24)==35&&r.type1At(24)==Data.T_STEEL&&r.type2At(24)==Data.T_DRAGON,"Hisui Sliggoo lost regional form at Goodra");
            Run syn=new Run(35500+s,Run.MODE_UNLIMITED);syn.board[0]=s;syn.specialForm[0]=form;syn.board[1]=next;syn.specialForm[1]=r.specialFormAt(24);int[] counts=new int[Data.NT];syn.countBoardSyn(counts);for(int t=0;t<Data.NT;t++)check(counts[t]<=1,"Regional chain synergy doubled "+Data.name[s]);
            Run mixed=new Run(36000+s,Run.MODE_UNLIMITED);mixed.bench[0]=mixed.bench[1]=mixed.bench[2]=s;mixed.specialForm[26]=form;mixed.mergeAll();check(mixed.get(24)==s&&mixed.get(25)==s&&mixed.get(26)==s,"Mixed regional triple merged");
        }
        Run cycle=new Run(37001,Run.MODE_UNLIMITED);cycle.board[0]=sp(479);int current=0,seen=0;do{cycle.giveConsumable(ConsumableData.MEMORY_DISC);check(cycle.useConsumable(0,ConsumableData.MEMORY_DISC),"Rotom cycle/reset failed");current=cycle.specialFormAt(0);seen++;}while(current!=0&&seen<10);check(seen==6&&cycle.type1At(0)==Data.t1[sp(479)],"Rotom did not return to ordinary form");
        int tauros=sp(128);check(EvolutionVariantData.choiceCount(tauros)==4,"Tauros breeds missing purchase choices");for(int c=0;c<4;c++){Run r=new Run(37010+c,Run.MODE_UNLIMITED);r.shop[0]=tauros;int f=EvolutionVariantData.choiceForm(tauros,c);check(r.buy(0,f)&&r.specialFormAt(24)==f,"Tauros purchase failed");r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.specialFormAt(24)==f,"Tauros purchase save failed");}
        int meowth=sp(52),galar=0;for(int i=0;i<LaterFormData.choiceCount(meowth);i++){int f=LaterFormData.choice(meowth,i);if(LaterFormData.name(f).indexOf("Galar")>=0)galar=f;}check(galar>0,"Galar Meowth missing");Run noTarget=new Run(37020,Run.MODE_UNLIMITED);noTarget.bench[0]=noTarget.bench[1]=noTarget.bench[2]=meowth;noTarget.specialForm[24]=noTarget.specialForm[25]=noTarget.specialForm[26]=galar;noTarget.mergeAll();check(noTarget.get(24)==meowth&&noTarget.get(25)==meowth&&noTarget.get(26)==meowth,"Galar Meowth became ordinary/Alola Persian");
        Run slowBranches=new Run(37030,Run.MODE_UNLIMITED);slowBranches.board[0]=sp(79);slowBranches.specialForm[0]=LaterFormData.regionalFor(sp(79));slowBranches.board[1]=sp(80);slowBranches.specialForm[1]=22;slowBranches.board[2]=sp(199);slowBranches.specialForm[2]=30;int[] slowCounts=new int[Data.NT];slowBranches.countBoardSyn(slowCounts);check(slowCounts[Data.T_PSY]==2&&slowCounts[Data.T_POISON]==2,"Galar Slowbro/Slowking branches not distinct or parent not superseded");
        int burmy=sp(412),sand=LaterFormData.first(burmy);Run cloak=new Run(37002,Run.MODE_UNLIMITED);cloak.bench[0]=cloak.bench[1]=cloak.bench[2]=burmy;cloak.specialForm[24]=cloak.specialForm[25]=cloak.specialForm[26]=sand;cloak.mergeAll();check(cloak.hasEvolutionChoice(),"Burmy cloak blocked Mothim branch");boolean mothim=false;for(int c=0;c<EvolutionBranchData.count(cloak.get(24));c++)if(EvolutionBranchData.choiceSpecies(cloak.get(24),c)==sp(414))mothim=true;check(mothim,"Mothim missing from cloak branch popup");
        System.out.println("AllGenerationAudit OK: "+routes+" merge/choice/board/save routes; "+forms+" item forms; "+mega+" Mega/shiny routes; canonical edges, full benches, regional exclusions");
    }
    private static String pad(int n){return n<10?"000"+n:n<100?"00"+n:n<1000?"0"+n:""+n;}
}

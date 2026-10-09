package pac;

/** Desktop-only smoke tests. Not packaged in the MIDlet JAR. */
public final class CombatSmokeTest {
    private static void check(boolean ok, String message) {
        if (!ok) throw new RuntimeException(message);
    }
    private static int speciesNamed(String name){for(int i=0;i<Data.N;i++)if(name.equals(Data.name[i]))return i;return -1;}
    private static int speciesSkill(String skill){for(int i=0;i<Data.N;i++)if(skill.equalsIgnoreCase(Data.skillName[i]))return i;return -1;}
    private static Unit unit(Battle battle,int sp){for(int i=0;i<battle.n;i++)if(battle.units[i].sp==sp)return battle.units[i];return null;}

    private static void checkRegionalEvolution(){
        int pikachu=speciesNamed("Pikachu"),raichu=speciesNamed("Raichu");
        Run r=new Run(1901,Run.MODE_UNLIMITED);r.draftStage=2;r.bench[0]=r.bench[1]=r.bench[2]=pikachu;r.shiny[24]=1;r.boostHp[24]=15;r.mergeAll();
        check(r.hasEvolutionChoice()&&r.get(r.pendingEvolutionPos)==raichu,"regional choice was not offered after merging");
        Run resumed=RunStorage.decode(RunStorage.encode(r));check(resumed!=null&&resumed.hasEvolutionChoice(),"pending evolution did not survive save/resume");
        check(resumed.chooseEvolution(true),"regional evolution confirmation failed");int p=24;check(resumed.specialFormAt(p)==SpecialFormData.ALOLA_RAICHU&&resumed.isShiny(p)&&resumed.boostHp[p]==15,"regional choice lost shiny/fruit");
        check(resumed.move(p,0),"regional form move failed");Battle b=resumed.makeBattle();Unit u=unit(b,raichu);check(u!=null&&u.primaryType()==Data.T_ELEC&&u.secondaryType()==Data.T_PSY&&b.synP[Data.T_PSY]>0,"regional combat types/synergies mismatch");
        Run saved=RunStorage.decode(RunStorage.encode(resumed));check(saved!=null&&saved.specialFormAt(0)==SpecialFormData.ALOLA_RAICHU,"regional form save roundtrip failed");
        Run normal=new Run(1902,Run.MODE_UNLIMITED);normal.bench[0]=normal.bench[1]=normal.bench[2]=pikachu;normal.mergeAll();check(normal.chooseEvolution(false)&&normal.specialFormAt(24)==0,"normal evolution branch failed");
        int starter=speciesNamed("Charmander");Run lab=new Run(1903,Run.MODE_TEST,1);lab.lockTestShop(starter);for(int i=0;i<30;i++){lab.rollShop();for(int j=0;j<5;j++)check(lab.shop[j]==starter,"lab shop escaped selected Pokemon");}
        int[] supported={6,65,94,115,142,150};for(int i=0;i<supported.length;i++)check(MegaData.available(supported[i]-1),"complete Gen-1 Mega missing");
        check(!MegaData.available(2)&&!MegaData.available(8),"portrait-only Mega was enabled");
    }
    private static void checkGenOneForms(){
        int regionalCount=0,megaCount=0;
        for(int sp=0;sp<Data.N;sp++)if(Data.nationalDex(sp)<=151){
            if(EvolutionVariantData.available(sp)){
                regionalCount++;int pre=-1;for(int j=0;j<Data.N;j++)if(Data.evo[j]==sp){pre=j;break;}
                if(pre<0){check(EvolutionVariantData.standalone(sp),"no evolution route for "+Data.name[sp]);pre=sp;}
                for(int shiny=0;shiny<2;shiny++)for(int variant=0;variant<2;variant++){
                    Run r=new Run(1700+sp,Run.MODE_UNLIMITED);
                    if(EvolutionBranchData.purchaseChoice(sp)){r.shop[0]=sp;check(r.buy(0,variant==0?0:EvolutionVariantData.formFor(sp)),"pre-summon purchase failed");r.shiny[24]=shiny;r.boostHp[24]=15;r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&!r.hasEvolutionChoice(),"purchase incorrectly opened merge popup");}
                    else{r.bench[0]=r.bench[1]=r.bench[2]=pre;r.shiny[24]=shiny;r.boostHp[24]=15;r.mergeAll();check(r.hasEvolutionChoice()&&r.get(r.pendingEvolutionPos)==sp,"missing form popup: "+Data.name[sp]);r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.chooseEvolution(variant!=0),"choice resume failed: "+Data.name[sp]);}
                    int form=variant==0?0:EvolutionVariantData.formFor(sp);check(r.specialFormAt(24)==form&&r.shiny[24]==shiny&&r.boostHp[24]==15,"form lost boosts: "+Data.name[sp]);
                    check(r.move(24,0),"form move failed");Battle b=r.makeBattle();Unit u=unit(b,sp);check(u!=null&&u.primaryType()==EvolutionVariantData.type1(sp,form)&&u.secondaryType()==EvolutionVariantData.type2(sp,form),"wrong form battle types: "+Data.name[sp]);
                    r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.specialFormAt(0)==form,"form save failed");
                }
            }
            if(MegaData.available(sp)){megaCount++;for(int shiny=0;shiny<2;shiny++){Run r=new Run(1800+sp,Run.MODE_UNLIMITED);r.board[0]=sp;r.shiny[0]=shiny;r.giveConsumable(ConsumableData.MEGA_STONE);check(r.useConsumable(0,ConsumableData.MEGA_STONE)&&r.shiny[0]==shiny,"Mega failed: "+Data.name[sp]);r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.isMega(0),"Mega save failed");Unit u=unit(r.makeBattle(),sp);check(u!=null&&u.mega&&u.shiny==(shiny!=0),"Mega battle form failed");}}
        }
        check(regionalCount==32&&megaCount==7,"Gen 1 form coverage changed");System.out.println("Gen1Forms OK: 32 regional routes x normal/variant x shiny, 7 Megas x shiny; saves and battle types");
        Run charizard=new Run(17000,Run.MODE_UNLIMITED);int sp=speciesNamed("Charizard");charizard.board[0]=sp;charizard.giveConsumable(ConsumableData.MEGA_STONE);check(charizard.useConsumable(0,ConsumableData.MEGA_STONE),"Charizard Mega use failed");Battle b=charizard.makeBattle();Unit u=unit(b,sp);check(u.primaryType()==Data.T_FIRE&&u.secondaryType()==Data.T_DRAGON&&b.synP[Data.T_DRAGON]==1&&charizard.type2At(0)==Data.T_DRAGON,"Mega X Dragon synergy/info mismatch");
    }
    private static void checkUpgradePreview(){
        int sp=speciesNamed("Steelix"),id=ItemData.indexOf("MYSTIC_WATER");Run r=new Run(17100,Run.MODE_UNLIMITED);r.board[0]=sp;r.boostHp[0]=15;r.boostAtk[0]=3;r.boostMana[0]=1000;r.inventory[id]=1;check(r.equipDirect(0,id),"preview item equip failed");
        Unit preview=new Unit();for(int mega=0;mega<2;mega++)for(int shiny=0;shiny<2;shiny++){r.mega[0]=mega;r.shiny[0]=shiny;r.previewUnit(0,preview);Unit actual=unit(r.makeBattle(),sp);check(preview.maxHp==actual.maxHp&&preview.atk==actual.atk&&preview.def==actual.def&&preview.speDef==actual.speDef&&preview.speed==actual.speed&&preview.range==actual.range&&preview.skillBonus==actual.skillBonus,"upgrade preview differs from combat");check(preview.mana<=preview.maxMana,"preview starting mana exceeded max");}
        int copies=r.itemCount(id);check(r.unequip(0,0)&&r.itemCount(id)==copies+1&&r.itemAt(0,0)<0,"unequip lost item");check(!r.unequip(0,0)&&r.itemCount(id)==copies+1&&!r.unequip(-1,0),"invalid unequip duplicated item");r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.itemCount(id)==copies+1&&r.itemAt(0,0)<0,"unequip save mismatch");System.out.println("UpgradePreview OK: normal/Shiny/Mega combinations, held bonuses, mana cap, equip/unequip/save");
    }
    private static void check172Stability(){
        int mr=speciesNamed("Mr. Mime");Run r=new Run(17201,Run.MODE_UNLIMITED);r.board[0]=mr;r.board[1]=mr;r.specialForm[0]=r.specialForm[1]=SpecialFormData.GALAR_MR_MIME;r.bench[0]=mr;r.mergeAll();check(r.board[0]==mr&&r.board[1]==mr&&r.bench[0]==mr&&!r.hasEvolutionChoice(),"transformed standalone copies merged");
        r=new Run(17202,Run.MODE_UNLIMITED);int pikachu=speciesNamed("Pikachu");r.bench[0]=r.bench[1]=r.bench[2]=pikachu;r.mergeAll();check(r.hasEvolutionChoice()&&r.move(24,0)&&r.pendingEvolutionPos==0,"pending evolution did not follow move");r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.chooseEvolution(true)&&r.specialFormAt(0)==SpecialFormData.ALOLA_RAICHU,"moved evolution failed to resume");
        r=new Run(17203,Run.MODE_UNLIMITED);r.bench[0]=r.bench[1]=r.bench[2]=pikachu;r.mergeAll();check(r.sell(24)&&r.pendingEvolutionPos==-1,"sold evolution left stale popup");
        int checked=0;Unit preview=new Unit();for(int sp=0;sp<Data.N;sp++)if(Data.nationalDex(sp)<=151){
            for(int shiny=0;shiny<2;shiny++)for(int mega=0;mega<(MegaData.available(sp)?2:1);mega++){
                r=new Run(17210+sp,Run.MODE_UNLIMITED);r.board[0]=sp;r.shiny[0]=shiny;r.mega[0]=mega;r.consumables[ConsumableData.ORAN]=2;check(r.useConsumable(0,ConsumableData.ORAN)&&r.useConsumable(0,ConsumableData.ORAN),"Gen1 fruit use failed");r.previewUnit(0,preview);Unit actual=unit(r.makeBattle(),sp);
                check(preview.maxHp==actual.maxHp&&preview.atk==actual.atk&&preview.def==actual.def&&preview.speDef==actual.speDef&&preview.speed==actual.speed&&preview.range==actual.range,"Gen1 upgrade preview mismatch "+Data.name[sp]);
                check(preview.maxHp>0&&preview.atk>0&&preview.speed<=CombatRules.MAX_SPEED&&preview.mana<=preview.maxMana,"Gen1 invalid upgraded stats");
                if(mega!=0)check(preview.maxHp>=Data.hp[sp]+100&&preview.atk>=Data.atk[sp]&&preview.range>=Data.range[sp],"Gen1 Mega downgraded stats: "+Data.name[sp]);
                r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.move(0,1)&&r.eatenFruitAt(1,1)==ConsumableData.ORAN&&r.shiny[1]==shiny&&r.mega[1]==mega,"Gen1 upgraded save/move lost state");checked++;
            }
        }System.out.println("v1.7.2 stability OK: "+checked+" Gen1 upgrade combinations, duplicate fruit/save/move, standalone forms and pending evolution");
    }
    private static void checkFormSynergies(){
        checkGenTwoAddedForms();
        for(int sp=0;sp<Data.N;sp++)if(MegaData.available(sp)||(SpecialFormData.memoryAvailable(sp)&&!SpecialFormData.evolutionVariant(SpecialFormData.memoryForm(sp)))||SpecialFormData.zygarde(sp)){
            for(int order=0;order<2;order++){
                Run pair=new Run(17400+sp,Run.MODE_UNLIMITED);pair.board[0]=sp;pair.board[1]=sp;
                if(MegaData.available(sp))pair.mega[order]=1;else pair.specialForm[order]=SpecialFormData.zygarde(sp)?SpecialFormData.ZYGARDE_100:SpecialFormData.memoryForm(sp);
                int[] counts=new int[Data.NT];pair.countBoardSyn(counts);Battle combat=pair.makeBattle();
                int a=pair.type1At(order),b=pair.type2At(order);
                for(int t=0;t<Data.NT;t++){int want=t==a||t==b?1:0;check(counts[t]==want&&combat.synP[t]==want,"special upgrade doubled synergy: "+Data.name[sp]+" order "+order);}
            }
        }
        int[] expected=new int[Data.NT],prep=new int[Data.NT];int routes=0;
        for(int sp=0;sp<Data.N;sp++)if(EvolutionVariantData.available(sp)){
            int form=EvolutionVariantData.formFor(sp),pre=-1;for(int j=0;j<Data.N;j++)if(Data.evo[j]==sp){pre=j;break;}
            for(int order=0;order<2;order++){
                Run r=new Run(17120+sp,Run.MODE_UNLIMITED);r.board[0]=sp;r.board[1]=sp;r.specialForm[order]=form;
                r.board[2]=sp;r.specialForm[2]=form;r.shiny[2]=1;r.board[3]=sp;r.shiny[3]=1;if(pre>=0)r.board[4]=pre;
                for(int t=0;t<Data.NT;t++)expected[t]=0;expected[Data.t1[sp]]++;if(Data.t2[sp]>=0&&Data.t2[sp]!=Data.t1[sp])expected[Data.t2[sp]]++;
                int a=EvolutionVariantData.type1(sp,form),b=EvolutionVariantData.type2(sp,form);expected[a]++;if(b>=0&&b!=a)expected[b]++;
                r.countBoardSyn(prep);Battle battle=r.makeBattle();for(int t=0;t<Data.NT;t++)check(prep[t]==expected[t]&&battle.synP[t]==expected[t],"form synergy count mismatch: "+Data.name[sp]+" type "+t+" order "+order);
                int[] recounted=new int[Data.NT];battle.countSyn(0,recounted);for(int t=0;t<Data.NT;t++)check(recounted[t]==expected[t],"battle form recount changed");
                r=RunStorage.decode(RunStorage.encode(r));check(r!=null,"form synergy save failed");r.countBoardSyn(prep);for(int t=0;t<Data.NT;t++)check(prep[t]==expected[t],"saved form synergy mismatch");
            }routes++;
        }
        int raichu=speciesNamed("Raichu"),abra=speciesNamed("Abra"),drowzee=speciesNamed("Drowzee");Run r=new Run(17125,Run.MODE_UNLIMITED);r.board[0]=speciesNamed("Pikachu");r.board[1]=raichu;r.board[2]=raichu;r.specialForm[2]=SpecialFormData.ALOLA_RAICHU;r.board[3]=abra;r.board[4]=drowzee;
        Battle battle=r.makeBattle();check(battle.synP[Data.T_PSY]==3&&battle.synP[Data.T_ELEC]==2,"mixed evolution branches lost new type");
        boolean boosted=false;for(int i=0;i<battle.n;i++)if(battle.units[i].specialForm==SpecialFormData.ALOLA_RAICHU)boosted=battle.units[i].synTier[Data.T_PSY]>0&&battle.units[i].skillBonus>=50;check(boosted,"regional type did not receive active synergy bonus");
        System.out.println("FormSynergies OK: "+routes+" routes, normal/variant duplicates, shiny, highest tier, order independence, save and active bonus");
    }
    private static void checkGenTwoAddedForms(){
        int[] dex={144,145,146,194,211,215,222,172,249,201};
        for(int k=0;k<dex.length;k++){int sp=-1;for(int i=0;i<Data.N;i++)if(Data.nationalDex(i)==dex[k]){sp=i;break;}check(sp>=0,"new form species missing");
            for(int shiny=0;shiny<2;shiny++){Run r=new Run(17500+k,Run.MODE_UNLIMITED);r.board[0]=sp;r.shiny[0]=shiny;r.boostHp[0]=15;r.giveConsumable(ConsumableData.MEMORY_DISC);check(r.useConsumable(0,ConsumableData.MEMORY_DISC),"memory route missing "+dex[k]);int form=r.specialFormAt(0);check(form==SpecialFormData.memoryForm(sp),"wrong memory form");
                r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.specialFormAt(0)==form&&r.shiny[0]==shiny&&r.boostHp[0]==15,"new form save/boost loss");Unit u=unit(r.makeBattle(),sp);check(u!=null&&u.specialForm==form&&u.maxHp>=Data.hp[sp]+15,"new form stats lost");check(u.primaryType()==r.type1At(0)&&u.secondaryType()==r.type2At(0),"new form types differ between prep/battle");
                if(dex[k]==201){for(int n=0;n<27;n++){r.giveConsumable(ConsumableData.MEMORY_DISC);check(r.useConsumable(0,ConsumableData.MEMORY_DISC),"Unown cycle blocked");}check(r.specialFormAt(0)==47,"Unown cycle wrap failed");}
            }
        }
        int[] megaDex={208,227,229,248};for(int k=0;k<megaDex.length;k++){int sp=-1;for(int i=0;i<Data.N;i++)if(Data.nationalDex(i)==megaDex[k])sp=i;check(sp>=0&&MegaData.available(sp),"Gen2 Mega missing");Run r=new Run(17550+k,Run.MODE_UNLIMITED);r.board[0]=sp;r.giveConsumable(ConsumableData.MEGA_STONE);check(r.useConsumable(0,ConsumableData.MEGA_STONE)&&unit(r.makeBattle(),sp).mega,"Gen2 Mega activation failed");}
        for(int form=41;form<=44;form++){int d=form==41?194:form==42?211:form==43?215:222,sp=-1;for(int i=0;i<Data.N;i++)if(Data.nationalDex(i)==d)sp=i;Run r=new Run(17580+form,Run.MODE_UNLIMITED);r.bench[0]=r.bench[1]=r.bench[2]=sp;for(int p=24;p<27;p++)r.specialForm[p]=form;r.mergeAll();int evolved=GenTwoFormData.evolved(sp,form);check(r.get(24)==evolved,"regional evolution jumped to normal route");check(evolved==sp||r.specialFormAt(24)==0,"regional form id leaked to evolved species");Run mixed=new Run(17600+form,Run.MODE_UNLIMITED);mixed.bench[0]=mixed.bench[1]=mixed.bench[2]=sp;mixed.specialForm[24]=form;mixed.mergeAll();check(mixed.get(24)==sp&&mixed.get(25)==sp&&mixed.get(26)==sp,"normal and regional copies merged together");}
        System.out.println("Gen2Forms OK: memory routes, birds, Unown cycle, stats, types, shiny/save and four Megas");
    }
    private static void checkGenOneRosterAndBranches(){
        int checked=0;
        for(int sp=0;sp<Data.N;sp++)if(Data.nationalDex(sp)>=1&&Data.nationalDex(sp)<=151){
            for(int shiny=0;shiny<2;shiny++){Run r=new Run(17700+sp,Run.MODE_UNLIMITED);r.board[0]=sp;r.shiny[0]=shiny;r.boostHp[0]=15;
                r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.board[0]==sp&&r.shiny[0]==shiny,"Gen1 save species/shiny loss");Unit u=unit(r.makeBattle(),sp);check(u!=null&&u.maxHp>=Data.hp[sp]+15&&u.shiny==(shiny!=0),"Gen1 base battle/boost loss");String path=PetAvatar.path(sp,0,false,shiny!=0);check(path.equals((shiny!=0?"/shinyav/":"/av/")+sp+".png"),"Gen1 summary avatar path mismatch");
                if(MegaData.available(sp)){r.giveConsumable(ConsumableData.MEGA_STONE);check(r.useConsumable(0,ConsumableData.MEGA_STONE),"Gen1 Mega activation failed");u=unit(r.makeBattle(),sp);check(u.mega&&PetAvatar.path(sp,0,true,shiny!=0).indexOf("/megaav/")==0,"Gen1 Mega result avatar route mismatch");}
                int form=EvolutionVariantData.formFor(sp);if(form>0){r.mega[0]=0;r.specialForm[0]=form;r=RunStorage.decode(RunStorage.encode(r));u=unit(r.makeBattle(),sp);check(u.specialForm==form&&u.primaryType()==EvolutionVariantData.type1(sp,form)&&u.secondaryType()==EvolutionVariantData.type2(sp,form),"Gen1 regional state/type loss");check(PetAvatar.path(sp,form,false,shiny!=0).indexOf(SpecialFormData.key(form))>=0,"Gen1 regional result avatar route mismatch");}
            }checked++;
        }
        check(checked==151,"Gen1 roster audit missed species");
        int[] middle={43,60},normal={44,61},alternate={181,185};
        for(int branch=0;branch<2;branch++)for(int choice=0;choice<2;choice++)for(int shiny=0;shiny<2;shiny++){
            Run r=new Run(17800+branch,Run.MODE_UNLIMITED);r.bench[0]=r.bench[1]=r.bench[2]=middle[branch];r.shiny[24]=shiny;r.boostHp[24]=15;r.mergeAll();check(r.hasEvolutionChoice()&&r.get(24)==normal[branch],"branch popup not offered");r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.hasEvolutionChoice()&&r.chooseEvolution(choice==1),"branch pending save/confirmation failed");int want=choice==1?alternate[branch]:normal[branch];check(r.get(24)==want&&r.specialFormAt(24)==0&&r.shiny[24]==shiny&&r.boostHp[24]==15,"branch lost species/shiny/boost");check(r.move(24,0),"branch move failed");r=RunStorage.decode(RunStorage.encode(r));Unit u=unit(r.makeBattle(),want);check(u!=null&&u.primaryType()==Data.t1[want]&&u.secondaryType()==Data.t2[want],"branch stats/types did not switch");
        }
        for(int branch=0;branch<2;branch++){Run r=new Run(17900+branch,Run.MODE_UNLIMITED);r.board[0]=middle[branch];r.board[1]=normal[branch];r.board[2]=alternate[branch];int[] cnt=new int[Data.NT];r.countBoardSyn(cnt);int t=Data.t1[normal[branch]];check(cnt[t]==2&&r.makeBattle().synP[t]==2,"branch synergy mismatch "+branch+" prep="+cnt[t]+" battle="+r.makeBattle().synP[t]+" tiers="+Data.tier[middle[branch]]+"/"+Data.tier[normal[branch]]+"/"+Data.tier[alternate[branch]]+" families="+Data.fam[middle[branch]]+"/"+Data.fam[normal[branch]]+"/"+Data.fam[alternate[branch]]);}
        System.out.println("Gen1Audit OK: 151 species x normal/shiny, supported Mega/regional summary routes, both species branches");
    }
    private static final class TouchProbe extends Screen{
        TouchProbe(){super(null);}
        public void update(int dt){}
        public void key(int k){}
        public void paint(javax.microedition.lcdui.Graphics g){}
        boolean tap(int choice){return confirmTouch(choice);}
    }
    private static void checkMergeAudit(){
        int[] preDex={82,108,112,114,125,126,176,193,207,221,233},nextDex={462,463,464,465,466,467,468,469,472,473,474};
        for(int k=0;k<preDex.length;k++){
            int pre=Data.speciesForDex(preDex[k]),next=Data.speciesForDex(nextDex[k]);
            check(pre>=0&&next>=0&&Data.evo[pre]==next,"independent evolution route missing: "+preDex[k]+" -> "+nextDex[k]);
            for(int split=0;split<2;split++){
                Run r=new Run(18900+k,Run.MODE_UNLIMITED);r.bench[0]=r.bench[1]=pre;if(split==0)r.bench[2]=pre;else r.board[0]=pre;r.shiny[24]=1;r.boostHp[24]=23;r.mergeAll();
                int p=split==0?24:0;check(r.get(p)==next&&r.shiny[p]==1&&r.boostHp[p]==23,"three-copy cross-generation merge failed");
                r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.get(p)==next,"cross-generation save failed");
            }
        }
        Run shopMagby=new Run(19001,Run.MODE_UNLIMITED);int magby=Data.speciesForDex(240);for(int i=0;i<9;i++){shopMagby.shop[0]=magby;check(shopMagby.buy(0),"Magby purchase failed");}check(shopMagby.get(24)==Data.speciesForDex(467),"nine Magby did not cascade into Magmortar");
        Run oldMagmar=new Run(19002,Run.MODE_UNLIMITED);oldMagmar.bench[0]=oldMagmar.bench[1]=oldMagmar.bench[2]=Data.speciesForDex(126);oldMagmar=RunStorage.decode(RunStorage.encode(oldMagmar));check(oldMagmar!=null&&oldMagmar.get(24)==Data.speciesForDex(467),"existing three Magmar did not repair on load");
        int[] noMarker={172,25,26,144,145,146,194,211,215,222,201,249};for(int k=0;k<noMarker.length;k++)check(!SpecialFormData.markerAvailable(Data.speciesForDex(noMarker[k])),"regional/cosmetic form got special marker");
        check(SpecialFormData.markerAvailable(Data.speciesForDex(245))&&SpecialFormData.markerAvailable(Data.speciesForDex(382)),"real special upgrade marker missing");
        check(Data.evo[122]==211&&Data.fam[211]==Data.fam[122]&&!Data.isBase(211),"Scyther/Scizor route missing");
        int tested=0;
        for(int sp=0;sp<Data.N;sp++)if(Data.generation(sp)<=2&&Data.evo[sp]>=0){
            for(int full=0;full<2;full++){
                Run r=new Run(18400+sp,Run.MODE_UNLIMITED);r.gold=100;
                if(full==0){r.bench[0]=r.bench[1]=r.bench[2]=sp;r.shiny[24]=1;r.boostHp[24]=19;r.mergeAll();}
                else{for(int i=0;i<Run.BENCH;i++)r.bench[i]=Data.MEWTWO;r.bench[0]=r.bench[1]=sp;r.shiny[24]=1;r.boostHp[24]=19;r.shop[0]=sp;if(Data.isBase(sp))check(r.buy(0),"full bench prevented base merge: "+Data.name[sp]);else continue;}
                int want=EvolutionBranchData.defaultNext(sp);
                check(r.get(24)==want&&r.get(25)<0&&r.shiny[24]==1&&r.boostHp[24]==19,"merge failed/lost state: "+Data.name[sp]);
                r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.get(24)==want,"merge save failed");
                if(r.hasEvolutionChoice())check(r.chooseEvolution(0),"merge default option failed");
                tested++;
            }
        }
        int[] forms={41,42,43,44},dex={194,211,215,222};
        for(int k=0;k<forms.length;k++){
            int sp=Data.speciesForDex(dex[k]),form=forms[k];Run r=new Run(18800+k,Run.MODE_UNLIMITED);
            for(int i=0;i<Run.BENCH;i++)r.bench[i]=Data.MEWTWO;
            r.bench[0]=r.bench[1]=sp;r.specialForm[24]=r.specialForm[25]=form;r.shop[0]=sp;
            check(r.buy(0,form)&&r.get(24)==GenTwoFormData.evolved(sp,form)&&r.get(25)<0,"full bench regional merge failed");
        }
        System.out.println("MergeAudit OK: "+tested+" Gen1/2 routes, normal/full bench, shiny/boost/save and four regional full-bench routes");
    }
    private static void checkModeTouch(){
        int[] widths={176,240,360,640},heights={220,320,480,360};
        for(int d=0;d<widths.length;d++)for(int n=5;n<=8;n++){
            int W=widths[d],H=heights[d],fh=14,gap=UiLayout.compact(W,H)?fh+4:fh+10,top=Math.max(fh*2+2,H/2-n*gap/2)-3;
            for(int i=0;i<n;i++)check(TouchLayout.rowAt(W/2,top+i*gap+fh/2,W/8,top,W*3/4,gap,fh+6,n)==i,"mode touch row mismatch");
            check(TouchLayout.rowAt(W/8-1,top+3,W/8,top,W*3/4,gap,fh+6,n)<0,"mode touch outside selected");
            check(TouchLayout.rowAt(W/2,top-1,W/8,top,W*3/4,gap,fh+6,n)<0,"mode touch header selected");
        }
        TouchProbe p=new TouchProbe();check(!p.tap(0)&&p.tap(0),"initial highlighted option activated on first tap");
        check(!p.tap(1)&&!p.tap(2)&&p.tap(2),"changing hovered option confirmed wrong row");
        check(!p.tap(100),"replace save activated immediately");p.resetTouchChoice();check(!p.tap(100)&&p.tap(100),"replace save two-tap confirmation failed");
        System.out.println("ModeTouch OK: 5-8 menu rows across four sizes; hover/confirm and modal reset");
    }
    private static void checkTouchPopupGeometry(){
        int[] widths={176,240,320,512,640},heights={208,320,360,240,360};
        for(int i=0;i<widths.length;i++){int W=widths[i],H=heights[i],fh=14,w=Math.min(W-12,250),h=TouchLayout.evolutionHeight(H,fh),x=(W-w)/2+6,y=(H-h)/2+fh+16,row=TouchLayout.evolutionRow(H,fh);
            check(TouchLayout.rowAt(x+2,y+2,x,y,w-12,row,row-4,2)==0,"touch normal evolution misses");
            check(TouchLayout.rowAt(x+w-14,y+row+2,x,y,w-12,row,row-4,2)==1,"touch regional evolution misses");
            check(TouchLayout.rowAt(x+2,y+row-1,x,y,w-12,row,row-4,2)==-1,"touch evolution gap selects a form");
            check(TouchLayout.rowAt(x-1,y+2,x,y,w-12,row,row-4,2)==-1,"touch outside popup selects a form");
        }System.out.println("TouchLayout OK: portrait/landscape evolution cards, gaps and outside bounds");
    }
    private static void checkExpandedEvolutionChoices(){
        int[] from={44,61,133,79,236},defaults={45,62,134,80,106},counts={2,2,8,4,3};
        int checked=0;
        for(int k=0;k<from.length;k++){
            int pre=EvolutionBranchData.species(from[k]),base=EvolutionBranchData.species(defaults[k]);
            check(EvolutionBranchData.count(base)==counts[k],"branch count mismatch "+from[k]);
            for(int c=0;c<counts[k];c++){
                int expected=EvolutionBranchData.choiceSpecies(base,c),form=EvolutionBranchData.choiceForm(base,c);
                check(!Data.isBase(expected),"evolved branch leaked as base: "+Data.name[expected]);
                Run r=new Run(18000+k*10+c,Run.MODE_UNLIMITED);
                r.bench[0]=r.bench[1]=r.bench[2]=pre;r.shiny[24]=1;r.boostHp[24]=17;r.mergeAll();
                check(r.hasEvolutionChoice()&&r.get(24)==base,"wrong branch landing "+from[k]);
                r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.chooseEvolution(c),"branch confirmation/save failed");
                check(r.get(24)==expected&&r.specialFormAt(24)==form&&r.shiny[24]==1&&r.boostHp[24]==17,"branch lost state");
                r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.get(24)==expected&&r.specialFormAt(24)==form,"chosen branch save failed");
                checked++;
            }
        }
        int[] stones={26,28,31,34,36,38,40,45,59,62,71,91,103,121,182,192};
        for(int i=0;i<stones.length;i++){int sp=EvolutionBranchData.species(stones[i]);check(sp>=0&&!Data.isBase(sp),"stone evolution in shop: "+stones[i]);}
        for(int d=144;d<=146;d++){
            int sp=EvolutionBranchData.species(d),form=EvolutionVariantData.formFor(sp);
            check(EvolutionBranchData.purchaseChoice(sp),"bird missing pre-purchase choice");
            for(int c=0;c<2;c++){
                Run r=new Run(18100+d+c,Run.MODE_UNLIMITED);r.shop[0]=sp;int gold=r.gold;
                check(r.buy(0,c==0?0:form)&&r.get(24)==sp&&r.specialFormAt(24)==(c==0?0:form)&&r.shop[0]<0,"bird purchase failed");
                check(r.gold==gold&&!r.hasEvolutionChoice(),"bird purchase charged unlimited gold or opened merge");
                r=RunStorage.decode(RunStorage.encode(r));check(r!=null&&r.specialFormAt(24)==(c==0?0:form),"bird purchase save failed");
            }
            Run full=new Run(18200+d,Run.MODE_THIRTY);full.gold=100;full.shop[0]=sp;for(int i=0;i<Run.BENCH;i++)full.bench[i]=sp;
            check(!full.buy(0,form)&&full.gold==100&&full.shop[0]==sp&&!full.hasEvolutionChoice(),"failed purchase consumed resources");
            full.mergeAll();check(!full.hasEvolutionChoice()&&full.bench[1]==sp,"standalone bird merged");
        }
        Run legacy=new Run(18300,Run.MODE_UNLIMITED);legacy.shop[0]=EvolutionBranchData.species(135);legacy.pool[legacy.shop[0]]=20;
        legacy=RunStorage.decode(RunStorage.encode(legacy));check(legacy!=null&&legacy.pool[EvolutionBranchData.species(135)]==0,"legacy evolved pool survived");
        for(int i=0;i<legacy.shop.length;i++)check(legacy.shop[i]<0||Data.isBase(legacy.shop[i]),"legacy evolved shop survived");
        System.out.println("ExpandedEvolution OK: "+checked+" choices, stone-family shop filters, bird pre-purchase/save/full-bench transactions");
    }
    public static void main(String[] args) {
        checkMergeAudit();
        GenThreeTest.run();
        AllGenerationTest.run();
        checkModeTouch();
        checkExpandedEvolutionChoices();
        checkGenOneRosterAndBranches();
        checkTouchPopupGeometry();
        SurvivalSmokeTest.run();
        SurvivalSystemTest.run();
        Survival174Test.run();
        SurvivalVolleyTest.run();
        SurvivalEvolutionTest.run();
        BallExchangeTest.run();
        BallExchangeTest.checkIcons();
        ReleasePolishTest.run();
        ExtraChessTest.run();
        Patch1716Test.run();
        Balance1717Test.run();
        Explore181Test.run();
        AvatarRouteTest.run();
        ReleaseAudit181Test.run();
        MusicRoutingTest.run();
        MidiResourceTest.run();
        check172Stability();
        checkFormSynergies();
        checkRegionalEvolution();
        checkGenOneForms();
        checkUpgradePreview();
        Run fruitRun=new Run(9,Run.MODE_NORMAL);fruitRun.board[0]=0;fruitRun.consumables[ConsumableData.ORAN]=2;
        check(fruitRun.useConsumable(0,ConsumableData.ORAN)&&fruitRun.useConsumable(0,ConsumableData.ORAN),"duplicate fruit consumption failed");
        check(fruitRun.eatenFruitAt(0,0)==ConsumableData.ORAN&&fruitRun.eatenFruitAt(0,1)==ConsumableData.ORAN&&fruitRun.eatenFruitAt(0,2)==-1,"duplicate fruit slots missing");
        check(fruitRun.move(0,1),"fruit history move failed");Run fruitSaved=RunStorage.decode(RunStorage.encode(fruitRun));
        check(fruitSaved!=null&&fruitSaved.eatenFruitAt(1,0)==ConsumableData.ORAN&&fruitSaved.eatenFruitAt(1,1)==ConsumableData.ORAN&&fruitSaved.eatenFruitAt(0,0)==-1,"fruit history save/move mismatch");
        int[][] screens={{128,160},{176,208},{176,220},{240,320},{320,240},{400,240},{480,320}};
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
        check(!UiLayout.landscape(240,320)&&UiLayout.landscape(320,240)&&UiLayout.landscape(400,240)&&UiLayout.landscape(480,320),"responsive landscape threshold mismatch");
        for(int s=0;s<screens.length;s++)check(StartupNoticeScreen.visibleLines(screens[s][1],12)>=6,"startup notice has too few readable lines at "+screens[s][0]+"x"+screens[s][1]);
        Rng exploreRng=new Rng(137);int gachaPick=ExploreRules.pick(exploreRng,0,1);
        check(gachaPick>=0&&Data.isBase(gachaPick)&&ExploreRules.gen(gachaPick)==1,"Gen-filtered gacha mismatch");
        check(GachaRules.groupForDay(java.util.Calendar.MONDAY)==0&&GachaRules.groupForDay(java.util.Calendar.THURSDAY)==1&&GachaRules.groupForDay(java.util.Calendar.SATURDAY)==2&&GachaRules.groupForDay(java.util.Calendar.SUNDAY)==3,"daily Gacha schedule mismatch");
        check(GachaRules.allowsGen(0,3)&&!GachaRules.allowsGen(0,4)&&GachaRules.allowsGen(1,5)&&GachaRules.allowsGen(2,9)&&GachaRules.allowsGen(3,1),"daily generation pool mismatch");
        int pityDex=GachaRules.pick(new Rng(138),3,true);check(GachaRules.legendary(pityDex)&&!Save.ownsDex(pityDex),"Gacha pity must select a new Legendary");
        int[] featured=GachaRules.randomFeaturedLegendaries(3,3,new Rng(139));check(featured.length==3&&GachaRules.legendary(featured[0])&&GachaRules.legendary(featured[1])&&GachaRules.legendary(featured[2]),"random featured Legendary list mismatch");
        for(int roll=0;roll<500;roll++){int d=GachaRules.pickFeatured(new Rng(5000+roll),3,false,featured);if(GachaRules.legendary(d))check(d==featured[0]||d==featured[1]||d==featured[2],"early Legendary escaped today's featured pool");}
        int featuredPity=GachaRules.pickFeatured(new Rng(140),3,true,featured);check(featuredPity==featured[0]||featuredPity==featured[1]||featuredPity==featured[2],"pity Legendary escaped today's featured pool");
        check(featured[0]!=featured[1]&&featured[0]!=featured[2]&&featured[1]!=featured[2],"featured Legendaries must be unique");
        int[] dailyA=GachaRules.featuredForDay(1,5,2026270),dailyB=GachaRules.featuredForDay(1,5,2026270),dailyC=GachaRules.featuredForDay(1,5,2026271);
        for(int i=0;i<5;i++)check(dailyA[i]==dailyB[i],"daily featured list changed within one day");
        boolean dayChanged=false;for(int i=0;i<5;i++)if(dailyA[i]!=dailyC[i])dayChanged=true;check(dayChanged,"daily featured list did not reset next day");
        check(Save.unlockDex(387)&&Save.ownsDex(387)&&!Save.unlockDex(387),"collection-only ownership mismatch");
        Save.dexUnlocked=new boolean[CollectionDex.COUNT];
        int oldCamp0=Save.camp[0],oldCamp1=Save.camp[1],oldCamp2=Save.camp[2];long oldCampStart=Save.campStart;long oldLock=Save.campLockUntil[0],oldClaim=Save.campClaimAt[0];
        Save.camp[0]=0;Save.camp[1]=-1;Save.camp[2]=-1;Save.campStart=1000;Save.campLockUntil[0]=4600;Save.campClaimAt[0]=1000;
        check(ExploreRules.campReward(1000+7200)==ExploreRules.hourlyBalls(0)*2,"offline camp reward mismatch");
        check(ExploreRules.campReward(1000+30L*3600)==ExploreRules.hourlyBalls(0)*24,"camp reward cap mismatch");
        int oldBalls=Save.balls;int claimed=ExploreRules.claimCamp(1000+7200);
        check(claimed==ExploreRules.hourlyBalls(0)*2&&Save.balls==oldBalls+claimed&&Save.campStart==8200,"camp claim transaction mismatch");
        Save.balls=oldBalls;
        Save.camp[0]=oldCamp0;Save.camp[1]=oldCamp1;Save.camp[2]=oldCamp2;Save.campStart=oldCampStart;Save.campLockUntil[0]=oldLock;Save.campClaimAt[0]=oldClaim;
        check(CombatStatus.COUNT==25,"documented status count mismatch");
        CombatStatus parity=new CombatStatus();parity.apply(CombatStatus.BURN,20);parity.apply(CombatStatus.SAFEGUARD,15);
        check(parity.burn==0&&parity.safeguard==15,"Safeguard must cleanse negatives");
        check(!parity.apply(CombatStatus.POISON,20)&&parity.poison==0,"Safeguard must block negatives");
        parity.apply(CombatStatus.RAGE,10);check(parity.effectiveSpeed(60)==90,"Rage speed mismatch");
        parity.clearPositive();parity.apply(CombatStatus.ELECTRIC_FIELD,10);check(parity.effectiveSpeed(50)==60,"Electric Field speed mismatch");
        check(parity.positive(CombatStatus.RESURRECTION)&&!parity.positive(CombatStatus.POSSESSED),"status polarity mismatch");
        int mimikyu=speciesNamed("Mimikyu"),mudsdale=speciesNamed("Mudsdale"),regigigas=speciesNamed("Regigigas");
        check(mimikyu>=0&&mudsdale>=0&&regigigas>=0,"Gen 4-7 passive species missing");
        int[] passiveUs=new int[24],passiveFoe=new int[24];for(int i=0;i<24;i++){passiveUs[i]=-1;passiveFoe[i]=-1;}passiveUs[0]=mimikyu;passiveUs[1]=mudsdale;passiveUs[2]=regigigas;passiveFoe[0]=0;
        Battle passiveBattle=new Battle(passiveUs,passiveFoe,100,new Rng(1578));Unit mimicUnit=unit(passiveBattle,mimikyu),mudUnit=unit(passiveBattle,mudsdale),regiUnit=unit(passiveBattle,regigigas);
        check(mimicUnit!=null&&mimicUnit.shield>=mimicUnit.maxHp/4,"Mimikyu Disguise passive missing");
        int mudDef=mudUnit.def;PokemonPassive.onDamaged(passiveBattle,mudUnit);check(mudUnit.def==mudDef+1,"Mudsdale Stamina passive missing");
        check(regiUnit.passiveTimer==50&&!PokemonPassive.description(regigigas).equals(""),"Regigigas Slow Start passive missing");

        int reflect=speciesSkill("Reflect"),aquaStep=speciesSkill("Aqua Step"),saltCure=speciesSkill("Salt Cure"),bonemerang=speciesSkill("Bonemerang");
        check(reflect>=0&&aquaStep>=0&&saltCure>=0&&bonemerang>=0,"v1.5.8 representative skills missing");
        int[] skillUs=new int[24],skillFoe=new int[24];for(int i=0;i<24;i++){skillUs[i]=-1;skillFoe[i]=-1;}skillUs[0]=reflect;skillUs[1]=aquaStep;skillUs[2]=saltCure;skillUs[3]=bonemerang;skillUs[4]=0;skillFoe[0]=7;
        Battle skillBattle=new Battle(skillUs,skillFoe,100,new Rng(1580));Unit reflectUnit=unit(skillBattle,reflect),stepUnit=unit(skillBattle,aquaStep),saltUnit=unit(skillBattle,saltCure),boneUnit=unit(skillBattle,bonemerang),skillTarget=unit(skillBattle,7),skillAlly=unit(skillBattle,0);
        int allyShield=skillAlly.shield;AbilityBehavior.apply(skillBattle,reflectUnit,skillTarget);check(skillAlly.shield>allyShield,"team shield skill behavior missing");
        int beforeDistance=skillBattle.distance(stepUnit,skillTarget);AbilityBehavior.apply(skillBattle,stepUnit,skillTarget);check(skillBattle.distance(stepUnit,skillTarget)<=beforeDistance,"dash skill did not approach target");
        skillTarget.x=saltUnit.x;skillTarget.y=saltUnit.y;AbilityBehavior.apply(skillBattle,saltUnit,skillTarget);check(skillTarget.status.burn>0,"Salt Cure type burn missing");
        int targetHp=skillTarget.hp;AbilityBehavior.apply(skillBattle,boneUnit,skillTarget);check(skillTarget.hp<targetHp,"multi-hit extra damage missing");
        skillAlly.alive=false;skillAlly.hp=0;check(skillBattle.reviveAlly(reflectUnit)&&skillAlly.alive&&skillAlly.hp>0,"Revival Blessing helper mismatch");

        int future=speciesSkill("Future Sight"),spikes=speciesSkill("Spikes"),roar=speciesSkill("Roar"),guillotine=speciesSkill("Guillotine"),transform=speciesSkill("Transform"),shadowClone=speciesSkill("Shadow Clone");
        check(future>=0&&spikes>=0&&roar>=0&&guillotine>=0&&transform>=0&&shadowClone>=0,"v1.5.9 representative skills missing");
        int[] advancedUs=new int[24],advancedFoe=new int[24];for(int i=0;i<24;i++){advancedUs[i]=-1;advancedFoe[i]=-1;}advancedUs[0]=future;advancedUs[1]=spikes;advancedUs[2]=roar;advancedUs[3]=guillotine;advancedUs[4]=transform;advancedUs[5]=shadowClone;advancedFoe[0]=7;
        Battle advanced=new Battle(advancedUs,advancedFoe,100,new Rng(1590));Unit futureUnit=unit(advanced,future),spikeUnit=unit(advanced,spikes),roarUnit=unit(advanced,roar),executeUnit=unit(advanced,guillotine),dittoUnit=unit(advanced,transform),cloneUnit=unit(advanced,shadowClone),advancedTarget=unit(advanced,7);
        for(int i=0;i<advanced.n;i++)advanced.units[i].status.stun=100;
        int delayedHp=advancedTarget.hp;AbilityBehavior.apply(advanced,futureUnit,advancedTarget);for(int i=0;i<20;i++)advanced.step();check(advancedTarget.hp<delayedHp,"delayed Future Sight damage missing");
        int hazardHp=advancedTarget.hp;AbilityBehavior.apply(advanced,spikeUnit,advancedTarget);for(int i=0;i<10;i++)advanced.step();check(advancedTarget.hp<hazardHp&&advancedTarget.status.wound>0,"persistent hazard damage/status missing");
        int oldDistance=advanced.distance(roarUnit,advancedTarget);AbilityBehavior.apply(advanced,roarUnit,advancedTarget);check(advanced.distance(roarUnit,advancedTarget)>=oldDistance,"knockback behavior missing");
        int copiedSp=advancedTarget.sp;AbilityBehavior.apply(advanced,dittoUnit,advancedTarget);check(dittoUnit.sp==copiedSp&&dittoUnit.atk==advancedTarget.atk,"Transform stat/form copy missing");
        int oldN=advanced.n;AbilityBehavior.apply(advanced,cloneUnit,advancedTarget);check(advanced.n==oldN+1,"Shadow Clone summon missing");
        advancedTarget.hp=Math.max(1,advancedTarget.maxHp*30/100);AbilityBehavior.apply(advanced,executeUnit,advancedTarget);check(!advancedTarget.alive,"execute threshold behavior missing");

        int mimic=speciesSkill("Mimic"),aoe=-1;for(int i=0;i<Data.N;i++)if(Data.abil[i]==2){aoe=i;break;}check(mimic>=0&&aoe>=0,"v1.6.0 copy/AoE species missing");
        int[] aiUs=new int[24],aiFoe=new int[24];for(int i=0;i<24;i++){aiUs[i]=-1;aiFoe[i]=-1;}aiUs[0]=aoe;aiUs[1]=guillotine;aiUs[2]=mimic;aiFoe[0]=7;aiFoe[1]=8;aiFoe[2]=9;
        Battle aiBattle=new Battle(aiUs,aiFoe,100,new Rng(1600));Unit aoeUnit=unit(aiBattle,aoe),aiExecute=unit(aiBattle,guillotine),mimicUnit2=unit(aiBattle,mimic),foeA=unit(aiBattle,7),foeB=unit(aiBattle,8),foeC=unit(aiBattle,9);
        foeA.x=2;foeA.y=1;foeB.x=3;foeB.y=1;foeC.x=7;foeC.y=0;Unit aoeChoice=aiBattle.smartAbilityTarget(aoeUnit,foeC);check(aoeChoice==foeA||aoeChoice==foeB,"AoE AI did not choose the largest enemy cluster");
        foeA.hp=foeA.maxHp*80/100;foeB.hp=foeB.maxHp*20/100;foeC.hp=foeC.maxHp*60/100;check(aiBattle.smartAbilityTarget(aiExecute,foeA)==foeB,"execute AI did not choose lowest HP ratio");
        int mimicHp=foeA.hp;AbilityBehavior.apply(aiBattle,mimicUnit2,foeA);check(foeA.hp<mimicHp,"Mimic did not repeat target ability effect");
        mimicUnit2.copiedSkillSp=aoe;check(aiBattle.effectiveSkillSp(mimicUnit2)==aoe,"persistent copied skill source missing");
        int[] cadence={100,60,50,40};
        for(int c=0;c<cadence.length;c++){
            FixedStepClock clock=new FixedStepClock(100);int elapsed=0,steps=0;
            while(elapsed<6000){int dt=Math.min(cadence[c],6000-elapsed);clock.add(dt,1);elapsed+=dt;while(clock.ready()){clock.consume();steps++;}}
            check(steps==60&&clock.remainder()==0,"render cadence changed simulation at "+cadence[c]+"ms");
        }
        check(FixedStepClock.smooth256(0)==0&&FixedStepClock.smooth256(128)==128&&FixedStepClock.smooth256(256)==256,"smooth interpolation endpoints mismatch");
        check(CombatRules.cappedSpeed(119)==119&&CombatRules.cappedSpeed(120)==120&&CombatRules.cappedSpeed(999)==120,"combat speed cap mismatch");
        check(Save.targetFps()==16,"default target FPS mismatch");
        check(Data.CORE_N == 386, "core roster must contain Gen 1-3");
        check(Data.N >= 493, "battle roster must include all 107 Gen 4 Pokemon");
        int gen4Battle=0;for(int i=0;i<Data.N;i++)if(Data.generation(i)==4)gen4Battle++;
        check(gen4Battle==107,"Gen 4 battle roster mismatch");
        int rosterLegends=0;for(int i=0;i<Data.N;i++)if(Data.category[i]==6)rosterLegends++;
        check(rosterLegends==81, "Legendary War must contain 81 legendaries");
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
        check(!Save.inCamp(0)&&!Save.inCamp(Data.evo[0]),"retired camp must not lock legacy families");
        check(Save.usable(0)&&OwnedPokemon.eligible(0,-1),"legacy camp blocked owned Pokemon");
        check(Save.chooseProfileAvatar(1),"legacy camp blocked profile avatar");
        Run campLocked=new Run(554,Run.MODE_NORMAL);
        Save.camp[0]=-1;
        Run campReleased=new Run(554,Run.MODE_NORMAL);
        for(int i=0;i<Data.N;i++)check(campLocked.pool[i]==campReleased.pool[i],"legacy camp altered Battle pool");
        check(Save.usable(0)&&OwnedPokemon.eligible(0,-1),"removing a Camp slot must restore availability");
        check(ExploreRules.farmReward(4)==0&&ExploreRules.farmReward(5)==1&&ExploreRules.farmReward(12)==2,"Farm 5-KO reward/remainder rule mismatch");
        check(!ExploreRules.farmLegendaryRound(9)&&ExploreRules.farmLegendaryRound(10)&&ExploreRules.farmLegendaryRound(20),"Farm Legendary cadence mismatch");
        int normalFarmEnemy=ExploreRules.pickAnyBase(new Rng(555));
        check(normalFarmEnemy>=0&&Data.isBase(normalFarmEnemy)&&Data.category[normalFarmEnemy]!=6,"Farm normal enemy picker mismatch");
        Save.modeCleared=0;
        check(ProgressionRules.generationUnlocked(1)&&!ProgressionRules.generationUnlocked(2),"Gen 1 must be the only initially unlocked generation");
        ProgressionRules.recordClear(Run.MODE_GEN1);check(ProgressionRules.generationUnlocked(2)&&!ProgressionRules.generationUnlocked(3),"Gen 1 clear must unlock Gen 2 only");
        ProgressionRules.recordClear(Run.MODE_GEN2);check(ProgressionRules.generationUnlocked(3)&&!ProgressionRules.generationUnlocked(4),"Gen 2 clear must unlock Gen 3 only");
        ProgressionRules.recordClear(Run.MODE_GEN3);check(ProgressionRules.generationUnlocked(4),"Gen 3 clear must unlock Gen 4");
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
        itemBattle.forcePlayerVictory();check(itemBattle.over&&itemBattle.winner==0&&itemBattle.alive(1)==0,"cheat force-win battle state mismatch");
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
        check(merge.mergeEventPos>=0&&merge.mergeEventTier==2&&merge.mergeEventSp==Data.evo[0],
              "tier-2 evolution visual event missing");
        int evolved=-1;for(int p=0;p<Run.BOARD+Run.BENCH;p++)if(merge.get(p)==Data.evo[0]){evolved=p;break;}
        check(evolved>=0,"evolved Pokemon missing");
        check(merge.itemAt(evolved,0)<0&&merge.itemAt(evolved,1)<0&&merge.itemAt(evolved,2)<0,
              "evolved Pokemon must start with empty equipment");
        check(merge.itemCount(spoon)==1&&merge.itemCount(charcoal)==1&&merge.itemCount(book)==1,
              "merge did not return all equipment to reserve");
        int[] freeChoices={-1,-1,-1};merge.craftedChoices(freeChoices);
        check(freeChoices[0]>=0&&freeChoices[1]>=0&&freeChoices[2]>=0,
              "five-round crafted choices missing");
        check(freeChoices[0]!=freeChoices[1]&&freeChoices[0]!=freeChoices[2]&&freeChoices[1]!=freeChoices[2],
              "five-round crafted choices must be distinct");
        check(!ItemData.isComponent(freeChoices[0])&&!ItemData.isComponent(freeChoices[1])&&!ItemData.isComponent(freeChoices[2]),
              "five-round reward offered a component instead of a crafted item");

        Run locked = new Run(789, Run.MODE_NORMAL);
        int[] heldShop=new int[5];for(int i=0;i<5;i++)heldShop[i]=locked.shop[i];
        locked.toggleShopLock(); locked.nextRound();
        for(int i=0;i<5;i++)check(locked.shop[i]==heldShop[i],"shop lock did not preserve slot "+i);
        check(!locked.shopLocked,"shop lock must expire after one round");
        check(new Run(1,Run.MODE_NORMAL).maxRound()==40,"normal mode round limit mismatch");
        check(new Run(1,Run.MODE_THIRTY).maxRound()==30,"thirty-round mode limit mismatch");
        check(new Run(1,Run.MODE_GEN1).maxRound()==30,"Gen 1 mode round limit mismatch");
        check(new Run(1,Run.MODE_GEN2).maxRound()==30&&new Run(1,Run.MODE_GEN3).maxRound()==30&&new Run(1,Run.MODE_GEN4).maxRound()==30,"Gen 2-4 round limit mismatch");
        check(new Run(1,Run.MODE_ENDLESS).maxRound()==Integer.MAX_VALUE,"Endless mode must not have a terminal round");
        Run endlessRoster=new Run(7,Run.MODE_ENDLESS);check(endlessRoster.usesDraft()&&endlessRoster.playerGenerationLimit()==Data.BATTLE_MAX_GEN,"Endless draft/generation setup mismatch");
        check(new Run(2,Run.MODE_GEN1).autoChessEligible(151)&&new Run(2,Run.MODE_GEN1).autoChessEligible(251)&&!new Run(2,Run.MODE_GEN1).autoChessEligible(386),"Gen 1 player roster must use Gen 1-3");
        check(new Run(2,Run.MODE_GEN2).autoChessEligible(251)&&!new Run(2,Run.MODE_GEN2).autoChessEligible(386),"Gen 2 player roster must use Gen 1-3");
        check(new Run(2,Run.MODE_GEN3).autoChessEligible(251)&&!new Run(2,Run.MODE_GEN3).autoChessEligible(386),"Gen 3 player roster must use Gen 1-3");
        check(new Run(2,Run.MODE_GEN4).autoChessEligible(386),"Gen 4 player roster must include Gen 4");
        int[] generationModes={Run.MODE_GEN1,Run.MODE_GEN2,Run.MODE_GEN3,Run.MODE_GEN4};
        for(int gm=0;gm<generationModes.length;gm++){Run gr=new Run(6000+gm,generationModes[gm]);check(gr.draftStage==0,"generation run must begin with type-pool choice");int[] gp=new int[9],gs={-1,-1,-1};gr.typePackageChoices(gp);gr.choosePoolTypes(gp,0);gr.starterChoices(gs);check(gs[0]>=0&&Data.generation(gs[0])<=gr.playerGenerationLimit(),"generation starter exceeds unlocked roster");gr.chooseStarter(gs[0]);check(gr.draftStage==2&&gr.count(gs[0])==1,"generation starter draft failed");for(int sp=0;sp<Data.N;sp++)if(gr.pool[sp]>0)check(Data.generation(sp)<=gr.playerGenerationLimit(),"generation shop pool exceeds cumulative roster");}
        int isolatedSp=0,isolatedFam=Data.fam[isolatedSp],savedCampSlot=Save.camp[0];boolean oldOwned=Save.unlocked[isolatedFam];Save.unlocked[isolatedFam]=false;Save.camp[0]=isolatedSp;
        Run isolatedRun=new Run(1776,Run.MODE_NORMAL);check(isolatedRun.autoChessEligible(isolatedSp),"Collection/Camp state leaked into Auto Chess roster");isolatedRun.chooseAdditional(isolatedSp);check(isolatedRun.count(isolatedSp)==1,"Auto Chess could not grant a Collection-locked or camped Pokemon");check(!Save.unlocked[isolatedFam],"Auto Chess grant incorrectly unlocked Collection");Save.camp[0]=savedCampSlot;Save.unlocked[isolatedFam]=oldOwned;

        Run draft=new Run(2468,Run.MODE_NORMAL);
        check(draft.draftStage==0,"40-round run must begin with type-pool choice");
        for(int i=0;i<draft.shop.length;i++)check(draft.shop[i]<0,"shop opened before starter draft");
        int[] typePick={-1,-1,-1,-1,-1,-1,-1,-1,-1};draft.typePackageChoices(typePick);
        for(int i=0;i<typePick.length;i++)for(int j=0;j<i;j++)check(typePick[i]!=typePick[j],"type-pool symbols must be distinct");
        draft.choosePoolTypes(typePick,0);
        int[] starters={-1,-1,-1};draft.starterChoices(starters);
        for(int i=0;i<3;i++)check(starters[i]>=0&&Data.category[starters[i]]==0&&Data.famHasType(starters[i],typePick[i]),"starter does not match selected type-pool path");
        draft.chooseStarter(starters[0]);
        check(draft.draftStage==2&&draft.count(starters[0])==1,"free starter was not granted");
        int activeFamilies=0;for(int i=0;i<Data.N;i++)if(draft.pool[i]>0)activeFamilies++;
        check(activeFamilies>0,"selected-type family pool is empty");
        for(int sp=0;sp<Data.N;sp++)if(draft.pool[sp]>0)check(draft.matchesChosenPool(sp),"shop pool contains a family outside selected types");
        for(int i=0;i<draft.shop.length;i++)if(draft.shop[i]>=0)check(Data.category[draft.shop[i]]<5,"Unique/Legendary leaked into normal shop");
        int[] uniquePick={-1,-1,-1};draft.pokemonChoices(uniquePick,5,-1,false);
        check(uniquePick[0]>=0,"Unique milestone has no proposition");int uniqueStock=draft.pool[uniquePick[0]];
        draft.chooseAdditional(uniquePick[0]);
        check(draft.count(uniquePick[0])==1&&draft.pool[uniquePick[0]]==uniqueStock,"Unique must be free but stay outside normal shop pool");

        Run retry=new Run(9753,Run.MODE_NORMAL);retry.round=5;int itemTotal=0;
        for(int i=0;i<ItemData.count();i++)itemTotal+=retry.itemCount(i);
        int[] retryUs=new int[24],retryFoe=new int[24];for(int i=0;i<24;i++){retryUs[i]=-1;retryFoe[i]=-1;}retryUs[0]=0;retryFoe[0]=3;
        Battle lostRound=new Battle(retryUs,retryFoe,100,new Rng(77));lostRound.winner=1;lostRound.over=true;
        int savedClears=Save.modeCleared;Save.modeCleared=0;check(!ProgressionRules.endlessUnlocked(),"Endless unlocked before Gen9 clear");ProgressionRules.recordClear(Run.MODE_GEN8);check(!ProgressionRules.endlessUnlocked(),"Gen8 incorrectly unlocks Endless");ProgressionRules.recordClear(Run.MODE_GEN9);check(ProgressionRules.endlessUnlocked(),"Gen9 does not unlock Endless");Save.modeCleared=savedClears;
        Run endlessHp=new Run(9755,Run.MODE_ENDLESS);endlessHp.applyResult(lostRound);check(endlessHp.hp<100&&endlessHp.hp>0&&!endlessHp.over,"Endless ends on first loss");endlessHp.hp=1;endlessHp.applyResult(lostRound);check(endlessHp.over&&endlessHp.hp==0,"Endless does not end at zero HP");
        retry.applyResult(lostRound);
        int afterLossItems=0;for(int i=0;i<ItemData.count();i++)afterLossItems+=retry.itemCount(i);
        check(!retry.lastWon&&retry.lastXp==0&&retry.lastItem<0&&afterLossItems==itemTotal,"lost round granted XP or an item reward");
        retry.nextRound();check(retry.round==5,"lost round advanced to the next stage");
        retry.round=40;retry.hp=100;retry.over=false;retry.applyResult(lostRound);
        check(!retry.over,"final stage loss must retry while HP remains");

        Run thirtyLoss=new Run(9754,Run.MODE_THIRTY);thirtyLoss.round=5;thirtyLoss.applyResult(lostRound);
        check(!thirtyLoss.shouldOfferNineItemRefresh(),"30-round loss offered the nine-item victory refresh");
        Battle wonRound=new Battle(retryUs,retryFoe,100,new Rng(78));wonRound.winner=0;wonRound.over=true;
        Run thirtyWin=new Run(9755,Run.MODE_THIRTY);thirtyWin.round=5;thirtyWin.applyResult(wonRound);
        check(thirtyWin.shouldOfferNineItemRefresh(),"30-round victory did not offer the nine-item refresh");
        thirtyWin.round=thirtyWin.maxRound();thirtyWin.over=false;thirtyWin.applyResult(wonRound);
        check(thirtyWin.over&&!thirtyWin.shouldOfferNineItemRefresh(),"finished 30-round run offered an extra inventory refresh");

        Run fullGift=new Run(8642,Run.MODE_THIRTY);for(int p=0;p<Run.BOARD+Run.BENCH;p++)fullGift.set(p,p%40);
        int giftSp=Data.MEWTWO,beforeGiftGold=fullGift.gold;fullGift.chooseAdditional(giftSp);
        check(fullGift.gold==beforeGiftGold+Data.sellValue(giftSp),"full bench gift without merge was not converted to gold");
        Run mergeGift=new Run(8643,Run.MODE_THIRTY);for(int p=0;p<Run.BOARD+Run.BENCH;p++)mergeGift.set(p,20+p%20);mergeGift.set(0,0);mergeGift.set(1,0);
        int beforeMergeGold=mergeGift.gold;mergeGift.chooseAdditional(0);
        check(mergeGift.count(Data.evo[0])==1&&mergeGift.gold==beforeMergeGold,"full bench gift did not evolve before gold conversion");

        Run growth=new Run(8644,Run.MODE_THIRTY);growth.set(0,0);
        for(int id=0;id<ConsumableData.COUNT;id++)if(id!=ConsumableData.MEGA_STONE)check(ConsumableData.ICON[id]>=0,"fruit catalogue icon missing: "+id);
        int[] growthFruit={ConsumableData.ORAN,ConsumableData.CHERI,ConsumableData.GANLON,ConsumableData.APICOT,ConsumableData.PETAYA,ConsumableData.ASPEAR,ConsumableData.CHESTO};
        for(int q=0;q<growthFruit.length;q++){int id=growthFruit[q];growth.giveConsumable(id);check(growth.useConsumable(0,id),"fruit consumable could not be used: "+id);}
        check(growth.boostHp[0]==50&&growth.boostAtk[0]==6&&growth.boostDef[0]==5&&growth.boostSpeDef[0]==5&&growth.boostAp[0]==15&&growth.boostSpeed[0]==10&&growth.boostMana[0]==20,"fruit growth stats mismatch");
        growth.move(0,1);check(growth.get(1)==0&&growth.boostHp[1]==50&&growth.boostUses[1]==7&&growth.boostHp[0]==0,"growth did not follow moved Pokemon");
        Battle growthBattle=growth.makeBattle();Unit grown=null;for(int i=0;i<growthBattle.n;i++)if(growthBattle.units[i].side==0)grown=growthBattle.units[i];
        check(grown!=null&&grown.maxHp==Data.hp[0]+50&&grown.atk==Data.atk[0]+6&&grown.def==Data.def[0]+5&&grown.speDef==Data.speDef[0]+5&&grown.skillBonus==15&&grown.speed==Data.speed[0]+10&&grown.mana==20,"growth was not applied to Battle unit");
        Run repeatFruit=new Run(8644,Run.MODE_THIRTY);repeatFruit.board[0]=0;repeatFruit.giveConsumable(ConsumableData.ORAN);repeatFruit.giveConsumable(ConsumableData.ORAN);check(repeatFruit.useConsumable(0,ConsumableData.ORAN)&&repeatFruit.useConsumable(0,ConsumableData.ORAN)&&repeatFruit.boostHp[0]==ConsumableData.HP[ConsumableData.ORAN]*2&&repeatFruit.boostUses[0]==2,"duplicate fruit did not stack");for(int i=0;i<5;i++)repeatFruit.giveConsumable(ConsumableData.LEPPA);for(int i=0;i<5;i++)check(repeatFruit.useConsumable(0,ConsumableData.LEPPA),"starting energy fruit unexpectedly capped");check(repeatFruit.boostMana[0]==ConsumableData.MANA[ConsumableData.LEPPA]*5,"same energy fruit did not stack");repeatFruit.giveConsumable(ConsumableData.CHESTO);repeatFruit.giveConsumable(ConsumableData.GOLDEN_PINAP);check(repeatFruit.useConsumable(0,ConsumableData.CHESTO)&&repeatFruit.useConsumable(0,ConsumableData.GOLDEN_PINAP)&&repeatFruit.boostMana[0]>ConsumableData.MANA[ConsumableData.LEPPA]*5,"different starting-energy fruits should accumulate");Battle repeatBattle=repeatFruit.makeBattle();Unit repeatUnit=null;for(int i=0;i<repeatBattle.n;i++)if(repeatBattle.units[i].side==0)repeatUnit=repeatBattle.units[i];check(repeatUnit!=null&&repeatUnit.mana<=repeatUnit.maxMana,"starting energy was not capped on battle entry");
        Run ultimateShop=new Run(8645,Run.MODE_THIRTY);ultimateShop.gold=999;ultimateShop.round=19;ultimateShop.rollFruitShop();for(int i=0;i<6;i++)check(ultimateShop.fruitShop[i]!=ConsumableData.ULTIMATE,"Ultimate Fruit appeared before round 20");ultimateShop.round=20;ultimateShop.rollFruitShop();check(ultimateShop.fruitShop[5]==ConsumableData.ULTIMATE,"Ultimate Fruit is not fixed in round 20 shop");check(ConsumableData.ICON[ConsumableData.ULTIMATE]==ConsumableData.ICON[ConsumableData.CHERI]&&ConsumableData.name(ConsumableData.ULTIMATE).equals(ConsumableData.name(ConsumableData.CHERI)),"50-gold Cheri identity mismatch");check(!ConsumableData.randomEligible(ConsumableData.CHERI,40),"legacy Cheri option still appears in random shop");check(ultimateShop.buyFruit(5),"Ultimate Fruit first purchase failed");check(!ultimateShop.buyFruit(5),"Ultimate Fruit could be bought twice in one round");ultimateShop.round=21;ultimateShop.rollFruitShop();check(ultimateShop.buyFruit(5),"Ultimate Fruit did not return next round");
        Run onceShop=new Run(8646,Run.MODE_THIRTY);onceShop.gold=100;onceShop.fruitShop[0]=ConsumableData.ORAN;onceShop.fruitShop[1]=ConsumableData.ORAN;check(onceShop.buyFruit(0)&&!onceShop.buyFruit(0)&&onceShop.buyFruit(1),"duplicate fruit slots were not independently purchasable");int resetGold=onceShop.gold;check(onceShop.rerollFruitShop()&&onceShop.gold==resetGold-1,"fruit shop reset must cost exactly one gold");onceShop.fruitShop[0]=ConsumableData.ORAN;check(onceShop.buyFruit(0),"shop reset did not create a fresh purchase turn");onceShop.round++;onceShop.rollFruitShop();check(onceShop.buyFruit(0),"fruit did not become purchasable in the next round");
        int oranItem=ConsumableData.ICON[ConsumableData.ORAN];growth.giveItem(oranItem);check(!growth.equipDirect(1,oranItem)&&growth.itemCount(oranItem)==1,"fruit was incorrectly accepted as held equipment");
        check(SynergyStoneData.price(1)==5&&SynergyStoneData.price(5)==5&&SynergyStoneData.price(6)==10&&SynergyStoneData.price(40)==40,"synergy stone price curve mismatch");
        Run stoneRun=new Run(8647,Run.MODE_THIRTY);stoneRun.gold=100;stoneRun.board[0]=0;boolean[] stoneSeen=new boolean[Data.NT];for(int i=0;i<stoneRun.stoneShop.length;i++){int type=stoneRun.stoneShop[i];check(type>=0&&type<Data.NT&&!stoneSeen[type]&&SynergyStoneData.ICON[type]>=0,"invalid/duplicate synergy stone offer");stoneSeen[type]=true;}
        int boughtType=stoneRun.stoneShop[0],stoneGold=stoneRun.gold;check(stoneRun.buyStone(0)&&stoneRun.gold==stoneGold-5&&stoneRun.synergyStoneBonus[boughtType]==1,"synergy stone purchase mismatch");check(!stoneRun.buyStone(1),"more than one synergy stone was bought in a round");Battle stoneBattle=stoneRun.makeBattle();check(stoneBattle.synP[boughtType]>=1,"synergy stone bonus was not applied to battle");for(int rr=2;rr<=4;rr++){stoneRun.round=rr;stoneRun.stoneShop[0]=boughtType;check(stoneRun.buyStone(0),"synergy stone incorrectly capped");}check(stoneRun.synergyStoneBonus[boughtType]==4,"synergy stone should stack beyond +3");
        Run shinyRun=new Run(8648,Run.MODE_THIRTY);shinyRun.board[0]=0;shinyRun.gold=100;shinyRun.round=9;check(!shinyRun.buyShinyCharm(),"Shiny Stone unlocked before round 10");shinyRun.round=10;int shinyGold=shinyRun.gold;check(shinyRun.buyShinyCharm()&&shinyRun.gold==shinyGold-ShinyData.PRICE&&shinyRun.consumableCount(ConsumableData.SHINY_CHARM)==1&&!shinyRun.isShiny(0),"Shiny Stone was not sent to consumable bag");check(!shinyRun.buyShinyCharm(),"more than one Shiny Stone was bought in a round");check(shinyRun.useConsumable(0,ConsumableData.SHINY_CHARM)&&shinyRun.isShiny(0)&&shinyRun.consumableCount(ConsumableData.SHINY_CHARM)==0,"Shiny Stone use mismatch");Battle shinyBattle=shinyRun.makeBattle();Unit shinyUnit=null;for(int i=0;i<shinyBattle.n;i++)if(shinyBattle.units[i].side==0)shinyUnit=shinyBattle.units[i];check(shinyUnit!=null&&shinyUnit.shiny&&shinyUnit.maxHp==Data.hp[0]+50&&shinyUnit.atk==Data.atk[0]+5&&shinyUnit.def==Data.def[0]+5&&shinyUnit.speDef==Data.speDef[0]+5&&shinyUnit.speed==CombatRules.cappedSpeed(Data.speed[0]+5)&&shinyUnit.range==Data.range[0]+1&&shinyUnit.skillBonus>=25&&shinyUnit.crit>=5,"balanced Shiny bonuses missing");
        int megaSp=-1;for(int i=0;i<Data.N;i++)if(Data.nationalDex(i)==208){megaSp=i;break;}check(megaSp>=0,"Mega Steelix species missing");Run megaRun=new Run(8649,Run.MODE_THIRTY);megaRun.board[0]=megaSp;megaRun.gold=100;megaRun.round=14;check(!megaRun.buyMegaStone(),"Mega Stone unlocked before round 15");megaRun.round=15;check(megaRun.buyMegaStone()&&megaRun.useConsumable(0,ConsumableData.MEGA_STONE)&&megaRun.consumableCount(ConsumableData.MEGA_STONE)==0&&!megaRun.isShiny(0),"non-Shiny Pokemon should be allowed to Mega Evolve");megaRun.shiny[0]=1;check(megaRun.isMega(0),"Mega Stone use mismatch");Battle megaBattle=megaRun.makeBattle();Unit megaUnit=null;for(int i=0;i<megaBattle.n;i++)if(megaBattle.units[i].side==0)megaUnit=megaBattle.units[i];check(megaUnit!=null&&megaUnit.mega&&megaUnit.shiny&&megaUnit.maxHp==350&&megaUnit.atk==23&&megaUnit.def==55&&megaUnit.speDef==27&&megaUnit.speed==45&&megaUnit.range==2&&megaUnit.skillBonus==25,"balanced Shiny Mega stats mismatch");
        int ray=-1,zyg=-1;for(int i=0;i<Data.N;i++){if(Data.nationalDex(i)==384)ray=i;if(Data.nationalDex(i)==718)zyg=i;}check(ray>=0&&zyg>=0,"Rayquaza/Zygarde missing");Run formRun=new Run(8650,Run.MODE_THIRTY);formRun.round=15;formRun.gold=100;formRun.board[0]=ray;formRun.giveConsumable(ConsumableData.MEGA_STONE);check(formRun.useConsumable(0,ConsumableData.MEGA_STONE)&&formRun.isMega(0),"Rayquaza should Mega Evolve without Shiny");formRun.board[1]=zyg;formRun.specialForm[1]=SpecialFormData.ZYGARDE_10;check(formRun.buyZygardeCube()&&!formRun.buyZygardeCube(),"Zygarde Cube purchase must be once per round");check(formRun.useConsumable(1,ConsumableData.ZYGARDE_CUBE)&&formRun.specialForm[1]==SpecialFormData.ZYGARDE_50,"Zygarde 10 to 50 failed");formRun.round++;check(formRun.buyZygardeCube()&&formRun.useConsumable(1,ConsumableData.ZYGARDE_CUBE)&&formRun.specialForm[1]==SpecialFormData.ZYGARDE_100,"Zygarde 50 to 100 failed");
        formRun.giveConsumable(ConsumableData.MEGA_STONE);formRun.shiny[1]=1;check(formRun.useConsumable(1,ConsumableData.MEGA_STONE)&&formRun.isMega(1)&&formRun.specialForm[1]==0&&formRun.isShiny(1),"Zygarde 100 to Mega failed");int stones=formRun.consumableCount(ConsumableData.MEGA_STONE);check(!formRun.useConsumable(1,ConsumableData.MEGA_STONE)&&formRun.consumableCount(ConsumableData.MEGA_STONE)==stones,"duplicate Mega charged item");Run zygResume=RunStorage.decode(RunStorage.encode(formRun));check(zygResume.isMega(1)&&zygResume.specialForm[1]==0&&zygResume.isShiny(1),"Mega Zygarde save restored old form");Unit zygPreview=new Unit();zygResume.previewUnit(1,zygPreview);check(zygPreview.mega&&zygPreview.shiny&&zygPreview.specialForm==0,"Mega Zygarde preview mismatch");formRun.board[2]=zyg;formRun.specialForm[2]=SpecialFormData.ZYGARDE_50;formRun.giveConsumable(ConsumableData.MEGA_STONE);check(!formRun.useConsumable(2,ConsumableData.MEGA_STONE)&&!formRun.isMega(2),"Zygarde skipped 100 percent requirement");
        int deoxys=-1,hoopa=-1,necrozma=-1;for(int i=0;i<Data.N;i++){int dex=Data.nationalDex(i);if(dex==386)deoxys=i;else if(dex==720)hoopa=i;else if(dex==800)necrozma=i;}check(deoxys>=0&&hoopa>=0&&necrozma>=0,"Deoxys/Hoopa/Necrozma missing");check(Data.tier[deoxys]==1&&Data.evo[deoxys]<0&&Data.tier[hoopa]==1&&Data.evo[hoopa]<0&&Data.tier[necrozma]==1&&Data.evo[necrozma]<0,"standalone special forms must not show evolution dots");Run alternateForms=new Run(8651,Run.MODE_THIRTY);alternateForms.board[0]=deoxys;for(int i=0;i<3;i++)alternateForms.giveConsumable(ConsumableData.MEMORY_DISC);check(alternateForms.useConsumable(0,ConsumableData.MEMORY_DISC)&&alternateForms.specialForm[0]==SpecialFormData.DEOXYS_ATTACK,"Deoxys Attack form failed");check(alternateForms.useConsumable(0,ConsumableData.MEMORY_DISC)&&alternateForms.specialForm[0]==SpecialFormData.DEOXYS_DEFENSE,"Deoxys Defense form failed");check(alternateForms.useConsumable(0,ConsumableData.MEMORY_DISC)&&alternateForms.specialForm[0]==SpecialFormData.DEOXYS_SPEED,"Deoxys Speed form failed");alternateForms.board[1]=hoopa;alternateForms.board[2]=necrozma;alternateForms.giveConsumable(ConsumableData.MEMORY_DISC);alternateForms.giveConsumable(ConsumableData.MEMORY_DISC);check(alternateForms.useConsumable(1,ConsumableData.MEMORY_DISC)&&alternateForms.specialForm[1]==SpecialFormData.HOOPA_UNBOUND,"Hoopa Unbound failed");check(alternateForms.useConsumable(2,ConsumableData.MEMORY_DISC)&&alternateForms.specialForm[2]==SpecialFormData.ULTRA_NECROZMA,"Ultra Necrozma failed");

        Run legendaryRun=new Run(13579,Run.MODE_LEGEND);check(legendaryRun.draftStage==1,"Legendary War must skip type draft and request a starter");
        check(Game.combinedDirection(1|4)==Game.K_1&&Game.combinedDirection(1|8)==Game.K_3&&Game.combinedDirection(2|4)==Game.K_7&&Game.combinedDirection(2|8)==Game.K_9,"held joystick/keypad diagonal mapping mismatch");check(Game.combinedDirection(1|2)==Game.K_NONE&&Game.combinedDirection(4|8)==Game.K_NONE,"opposite held directions must cancel");
        for(int gen=1;gen<=Data.BATTLE_MAX_GEN;gen++){Run lab=new Run(14000+gen,Run.MODE_TEST,gen);check(lab.testMode()&&lab.testGeneration==gen&&lab.unlimitedGold&&lab.draftStage==2,"generation lab setup mismatch");for(int i=0;i<lab.shop.length;i++)if(lab.shop[i]>=0)check(Data.generation(lab.shop[i])==gen,"generation lab shop leaked another Gen");for(int i=0;i<lab.enemy.length;i++)if(lab.enemy[i]>=0)check(Data.generation(lab.enemy[i])==gen,"generation lab enemy leaked another Gen");}
        check(ExploreBattleRules.typePct(Data.T_FIRE,Data.T_GRASS,-1)==200&&ExploreBattleRules.typePct(Data.T_ELEC,Data.T_GROUND,-1)==0&&ExploreBattleRules.typePct(Data.T_WATER,Data.T_FIRE,-1)==200,"Explore type chart mismatch");int catchSp=0;for(int i=0;i<Data.CORE_N;i++)if(Data.isBase(i)&&Data.category[i]>=3){catchSp=i;break;}int fullChance=ExploreBattleRules.catchChance(catchSp,Data.hp[catchSp],Data.hp[catchSp]),lowChance=ExploreBattleRules.catchChance(catchSp,Data.hp[catchSp]/5,Data.hp[catchSp]);check(lowChance>=fullChance+20&&lowChance<=95,"low-HP capture bonus mismatch");for(int sp=0;sp<Data.CORE_N;sp++)if(Data.isBase(sp)){int full=ExploreBattleRules.catchChance(sp,Data.hp[sp],Data.hp[sp]),half=ExploreBattleRules.catchChance(sp,Data.hp[sp]/2,Data.hp[sp]),red=ExploreBattleRules.catchChance(sp,Data.hp[sp]/5,Data.hp[sp]);check(full>0&&full<=75&&half>=full&&red>=half&&red<=95,"Explore capture curve invalid for "+sp);int normalDmg=ExploreBattleRules.damage(sp,catchSp,false),skillDmg=ExploreBattleRules.damage(sp,catchSp,true);check(normalDmg>=1&&normalDmg<=Math.max(2,Data.hp[catchSp]*32/100),"Explore normal damage cap invalid");check(skillDmg>=normalDmg&&skillDmg<=Math.max(2,Data.hp[catchSp]*45/100),"Explore skill damage cap invalid");}
        int mapW=56,mapH=44;int[] mapSig=new int[4];for(int gen=1;gen<=3;gen++){byte[] mt=new byte[mapW*mapH],mz=new byte[mapW*mapH];ExploreMapLayout.build(mt,mz,mapW,mapH,gen);int walk=0,zones=0,sig=1;boolean[] seenZ=new boolean[4];for(int i=0;i<mt.length;i++){if(ExploreMapLayout.walkable(mt,mapW,mapH,i%mapW,i/mapW))walk++;if(mz[i]>=0&&mz[i]<4)seenZ[mz[i]]=true;sig=sig*31+mt[i]*5+mz[i];}for(int i=0;i<4;i++)if(seenZ[i])zones++;check(ExploreMapLayout.walkable(mt,mapW,mapH,mapW/2,mapH/2),"Explore Gen "+gen+" spawn blocked");for(int y=1;y<mapH-1;y++)for(int x=1;x<mapW-1;x++)if(ExploreMapLayout.walkable(mt,mapW,mapH,x,y))for(int dy=-1;dy<=1;dy++)for(int dx=-1;dx<=1;dx++)if(dx!=0||dy!=0){boolean expected=ExploreMapLayout.walkable(mt,mapW,mapH,x+dx,y+dy)&&(dx==0||dy==0||ExploreMapLayout.walkable(mt,mapW,mapH,x+dx,y)&&ExploreMapLayout.walkable(mt,mapW,mapH,x,y+dy));check(ExploreMapLayout.canStep(mt,mapW,mapH,x,y,dx,dy)==expected,"Explore collision/diagonal mismatch");}check(walk>mapW*mapH/2&&zones==4,"Explore Gen "+gen+" layout coverage invalid");mapSig[gen]=sig;}check(mapSig[1]!=mapSig[2]&&mapSig[1]!=mapSig[3]&&mapSig[2]!=mapSig[3],"Explore Gen layouts are not distinct");
        int legendCatch=-1;for(int i=0;i<Data.CORE_N;i++)if(Data.isBase(i)&&Data.category[i]==6){legendCatch=i;break;}check(legendCatch>=0&&ExploreBattleRules.catchChance(legendCatch,Data.hp[legendCatch]/5,Data.hp[legendCatch])>=65,"Legendary low-HP catch chance is too low");int questType=Data.T_PSY,questSp=-1,questGen=1;for(int i=0;i<Data.CORE_N;i++)if(Data.isBase(i)&&Data.category[i]!=6&&(Data.t1[i]==questType||Data.t2[i]==questType)&&ExploreRules.legendaryForType(ExploreRules.gen(i),questType)>=0){questSp=i;questGen=ExploreRules.gen(i);break;}int qi=(questGen-1)*Data.NT+questType,oldQuest=Save.exploreQuestCaught[qi];Save.exploreQuestCaught[qi]=9;ExploreRules.clearLegendary();ExploreRules.recordDefeat(questSp,1000);check(ExploreRules.questProgress(questGen,questType)==0&&Save.exploreLegendType==questType&&Save.exploreLegendUntil==1300,"Explore 10/10 outbreak trigger mismatch");ExploreRules.clearLegendary();Save.exploreQuestCaught[qi]=oldQuest;check(ExploreRules.tripReward(4,6,1)==7&&ExploreRules.tripReward(0,0,0)==0,"Explore trip reward mismatch");check(BallArt.pity(BallArt.NORMAL)==1&&BallArt.pity(BallArt.SILVER)==15&&BallArt.pity(BallArt.GOLD)==100,"Explore ball pity values mismatch");check(ExploreRules.mapCompletionGold(1)==1&&ExploreRules.mapCompletionGold(2)==2&&ExploreRules.mapCompletionGold(3)==3,"Explore regional completion rewards mismatch");
        for(int i=0;i<legendaryRun.shop.length;i++)check(legendaryRun.shop[i]<0,"Legendary shop opened before starter choice");
        int[] legendaryStarters={-1,-1,-1};legendaryRun.starterChoices(legendaryStarters);
        check(legendaryStarters[0]!=legendaryStarters[1]&&legendaryStarters[0]!=legendaryStarters[2]&&legendaryStarters[1]!=legendaryStarters[2],"Legendary starter choices must be distinct");
        for(int i=0;i<3;i++)check(legendaryStarters[i]>=0&&Data.category[legendaryStarters[i]]==6,"opening proposition is not Legendary");
        legendaryRun.chooseStarter(legendaryStarters[0]);
        int legendaryOwned=0;for(int p=0;p<Run.BOARD+Run.BENCH;p++)if(legendaryRun.get(p)>=0){check(Data.category[legendaryRun.get(p)]==6,"Legendary War starter is not Legendary");legendaryOwned++;}
        check(legendaryOwned==1,"Legendary War must grant exactly one opening Pokemon");
        for(int i=0;i<legendaryRun.shop.length;i++)if(legendaryRun.shop[i]>=0)check(Data.category[legendaryRun.shop[i]]==6,"non-Legendary leaked into Legendary shop");
        for(int i=0;i<legendaryRun.enemy.length;i++)if(legendaryRun.enemy[i]>=0)check(Data.category[Data.fam[legendaryRun.enemy[i]]]==6,"non-Legendary enemy in Legendary War");
        int[] econUs=new int[24],econFoe=new int[24];for(int i=0;i<24;i++){econUs[i]=-1;econFoe[i]=-1;}econUs[0]=0;econFoe[0]=3;
        Battle legendWin=new Battle(econUs,econFoe,100,new Rng(91));legendWin.winner=0;legendWin.over=true;
        Run legendWinRun=new Run(91,Run.MODE_LEGEND);legendWinRun.applyResult(legendWin);
        check(legendWinRun.lastBaseGold==10&&legendWinRun.lastVictoryGold==1&&legendWinRun.lastGold==11,"Legendary win income must be 10 base + 1 victory without interest");
        Battle legendLoss=new Battle(econUs,econFoe,100,new Rng(92));legendLoss.winner=1;legendLoss.over=true;
        Run legendLossRun=new Run(92,Run.MODE_LEGEND);legendLossRun.applyResult(legendLoss);
        check(legendLossRun.lastBaseGold==5&&legendLossRun.lastVictoryGold==0&&legendLossRun.lastGold==5,"Legendary loss income must be 5 base without interest");
        check(EconomyRules.interest(59)==5,"interest cap mismatch");
        check(EconomyRules.streakBonus(-6)==3&&EconomyRules.streakBonus(1)==0,"streak economy mismatch");
        check(EconomyRules.income(20,4,true,2)==12,"income breakdown mismatch");
        check(EconomyRules.playerDamage(8,5)==11,"player damage formula mismatch");
        Run pve=new Run(990,Run.MODE_NORMAL);pve.round=9;pve.genEnemy();
        int gyarados=-1;for(int i=0;i<Data.N;i++)if("Gyarados".equals(Data.name[i])){gyarados=i;break;}
        check(pve.enemy[2*8+4]!=gyarados,"Gyarados boss appeared one round early");
        pve.round=10;pve.genEnemy();check(pve.enemy[2*8+4]==gyarados,"round-10 Gyarados formation mismatch");
        check(pve.enemyEquip[(2*8+4)*3]==ItemData.indexOf("KINGS_ROCK"),"boss held item missing");
        Battle pveBattle=pve.makeBattle();Unit bossUnit=null;for(int i=0;i<pveBattle.n;i++)if(pveBattle.units[i].side==1)bossUnit=pveBattle.units[i];
        check(bossUnit!=null&&bossUnit.shield>=bossUnit.maxHp/5,"enemy held item passive missing");
        pve.round=24;pve.genEnemy();int legends=0;for(int i=0;i<Run.BOARD;i++)if(pve.enemy[i]>=0)legends++;
        check(legends==3&&pve.enemy[2*8+2]>=0&&pve.enemy[2*8+4]>=0&&pve.enemy[2*8+6]>=0,"legendary birds formation mismatch");
        check(!StageRoad.boss(9)&&StageRoad.boss(10)&&StageRoad.boss(40)&&!StageRoad.boss(39),"40-stage boss road mismatch");
        check(StageRoad.kind(1,Run.MODE_NORMAL)==StageRoad.KIND_BATTLE,"normal stage icon mismatch");
        check(StageRoad.kind(5,Run.MODE_NORMAL)==StageRoad.KIND_ITEM&&StageRoad.hasReward(5),"item reward stage icon mismatch");
        check(StageRoad.kind(8,Run.MODE_NORMAL)==StageRoad.KIND_POKEMON&&StageRoad.hasReward(8),"Pokemon reward stage icon mismatch");
        check(StageRoad.kind(10,Run.MODE_NORMAL)==StageRoad.KIND_BOSS&&StageRoad.hasReward(10),"reward Boss stage icon mismatch");
        check(StageRoad.kind(14,Run.MODE_NORMAL)==StageRoad.KIND_BOSS&&!StageRoad.hasReward(14),"plain Boss stage icon mismatch");
        check(StageRoad.rewardMask(5)==(StageRoad.REWARD_ITEM|StageRoad.REWARD_FAMILY),"round-5 reward mask mismatch");
        check(StageRoad.rewardMask(8)==StageRoad.REWARD_FAMILY,"round-8 reward mask mismatch");
        check(StageRoad.rewardMask(10)==(StageRoad.REWARD_ITEM|StageRoad.REWARD_UNIQUE),"round-10 reward mask mismatch");
        check(StageRoad.rewardMask(20)==(StageRoad.REWARD_ITEM|StageRoad.REWARD_LEGEND),"round-20 reward mask mismatch");
        check(StageRoad.rewardMask(40)==0,"final round must not open a milestone choice");
        check(StageRoad.boss(5,Run.MODE_THIRTY)&&StageRoad.boss(5,Run.MODE_GEN1)&&StageRoad.boss(5,Run.MODE_GEN4)&&!StageRoad.boss(5,Run.MODE_NORMAL),"mode-specific round-5 Boss mismatch");
        check(StageRoad.boss(14,Run.MODE_LEGEND)&&StageRoad.kind(14,Run.MODE_LEGEND)==StageRoad.KIND_BOSS,"Legendary Boss icon mismatch");
        int[] roadModes={Run.MODE_NORMAL,Run.MODE_UNLIMITED,Run.MODE_THIRTY,Run.MODE_GEN1,Run.MODE_LEGEND,Run.MODE_GEN2,Run.MODE_GEN3,Run.MODE_GEN4,Run.MODE_GEN5,Run.MODE_GEN6,Run.MODE_GEN7,Run.MODE_GEN8,Run.MODE_GEN9};
        for(int mi=0;mi<roadModes.length;mi++){Run road=new Run(31000+mi,roadModes[mi]);for(int rr=1;rr<=road.maxRound();rr++){road.round=rr;road.genEnemy();int enemies=0;for(int p=0;p<Run.BOARD;p++)if(road.enemy[p]>=0){int sp=road.enemy[p];check(sp<Data.N,"invalid enemy species at mode "+roadModes[mi]+" round "+rr);if(road.generationMode())check(Data.generation(sp)==road.enemyGeneration(),"wrong-generation enemy at mode "+roadModes[mi]+" round "+rr);if(roadModes[mi]==Run.MODE_LEGEND)check(Data.category[Data.fam[sp]]==6,"non-Legendary enemy at round "+rr);enemies++;}check(enemies>0&&road.enemyScale>0&&road.enemyName.length()>0,"empty stage at mode "+roadModes[mi]+" round "+rr);check(road.isBoss()==StageRoad.boss(rr,roadModes[mi]),"Boss rule drift at mode "+roadModes[mi]+" round "+rr);}}
        check(Data.BATTLE_MAX_GEN>=4&&Data.BATTLE_MAX_GEN<=9,"v1.5.7 roster generation cap invalid");
        int[] expectedLater={0,0,0,0,107,135,68,80,83,85};
        for(int gen=4;gen<=Data.BATTLE_MAX_GEN;gen++){int found=0;for(int sp=0;sp<Data.N;sp++)if(Data.generation(sp)==gen)found++;check(found==expectedLater[gen],"Gen "+gen+" roster count mismatch: "+found);}
        int gen9=-1;for(int sp=0;sp<Data.N;sp++)if(Data.generation(sp)==Data.BATTLE_MAX_GEN){gen9=sp;break;}check(gen9>=0,"latest generation roster missing");
        Run normalScope=new Run(41000,Run.MODE_NORMAL),unlimitedScope=new Run(41001,Run.MODE_UNLIMITED),endlessScope=new Run(41002,Run.MODE_ENDLESS);
        check(!normalScope.autoChessEligible(gen9),"later generation leaked into legacy Normal mode");
        check(unlimitedScope.autoChessEligible(gen9)&&endlessScope.autoChessEligible(gen9),"latest generation missing from Unlimited/Endless roster");
        check(endlessScope.maxRound()==Integer.MAX_VALUE&&StageRoad.boss(5,Run.MODE_ENDLESS),"Endless round/boss rules mismatch");
        check(GachaScreen.spinClock(0)==0&&GachaScreen.spinClock(700)==1750&&GachaScreen.spinClock(1500)==3750,"Gacha acceleration curve anchors mismatch");
        check(GachaScreen.spinClock(300)-GachaScreen.spinClock(200)<GachaScreen.spinClock(700)-GachaScreen.spinClock(600),"Gacha must accelerate");
        check(GachaScreen.spinClock(900)-GachaScreen.spinClock(800)>GachaScreen.spinClock(1500)-GachaScreen.spinClock(1400),"Gacha must slow back down");
        for(int seed=0;seed<500;seed++){Run firstPick=new Run(20000+seed,Run.MODE_NORMAL);int[] packs=new int[9],firstChoices={-1,-1,-1};firstPick.typePackageChoices(packs);firstPick.choosePoolTypes(packs,0);firstPick.starterChoices(firstChoices);check(firstChoices[0]>=0,"first starter option invalid at seed "+seed);firstPick.chooseStarter(firstChoices[0]);check(firstPick.draftStage==2&&firstPick.count(firstChoices[0])==1,"first starter option failed at seed "+seed);for(int sp=0;sp<Data.N;sp++)if(firstPick.pool[sp]>0)check(firstPick.matchesChosenPool(sp),"seeded shop pool escaped selected types");}
        Run snapshot=new Run(1201,Run.MODE_THIRTY);snapshot.round=7;snapshot.hp=73;snapshot.gold=42;snapshot.level=5;snapshot.xp=9;snapshot.streak=-3;
        snapshot.board[4]=25;snapshot.bench[2]=7;snapshot.shop[1]=18;snapshot.equip[4*3]=ItemData.indexOf("SHELL_BELL");snapshot.resultPath[0]=1;snapshot.resultPath[1]=0;snapshot.resultCount=2;
        int expectedRng=snapshot.rng.state();byte[] snapshotBytes=RunStorage.encode(snapshot);Run restored=RunStorage.decode(snapshotBytes);
        check(restored!=null&&restored.mode==Run.MODE_THIRTY&&restored.round==7&&restored.hp==73&&restored.gold==42,"run snapshot scalar mismatch");
        check(restored.board[4]==25&&restored.bench[2]==7&&restored.shop[1]==18&&restored.equip[12]==ItemData.indexOf("SHELL_BELL"),"run snapshot arrays mismatch");
        check(restored.resultCount==2&&restored.resultPath[1]==0&&restored.rng.state()==expectedRng,"run snapshot graph/RNG mismatch");RunStorage.clear();
        growth.giveConsumable(ConsumableData.CHERI);byte[] growthBytes=RunStorage.encode(growth);Run growthRestored=RunStorage.decode(growthBytes);
        check(growthRestored!=null&&growthRestored.consumableCount(ConsumableData.CHERI)==1&&growthRestored.boostHp[1]==50&&growthRestored.boostUses[1]==7,"v1.5 fruit save/resume mismatch");
        byte[] stoneBytes=RunStorage.encode(stoneRun);Run stoneRestored=RunStorage.decode(stoneBytes);check(stoneRestored!=null&&stoneRestored.synergyStoneBonus[boughtType]==4&&stoneRestored.stoneBoughtRound==4,"v1.5.1 synergy stone save/resume mismatch");
        byte[] shinyBytes=RunStorage.encode(shinyRun);Run shinyRestored=RunStorage.decode(shinyBytes);check(shinyRestored!=null&&shinyRestored.isShiny(0)&&shinyRestored.shinyBoughtRound==10,"v1.5.3 Shiny save/resume mismatch");
        byte[] megaBytes=RunStorage.encode(megaRun);Run megaRestored=RunStorage.decode(megaBytes);check(megaRestored!=null&&megaRestored.isMega(0)&&megaRestored.megaBoughtRound==15,"v1.5.5 Mega save/resume mismatch");
        for(int old=1;old<=6;old++){byte[] compatible=new byte[stoneBytes.length];System.arraycopy(stoneBytes,0,compatible,0,stoneBytes.length);compatible[3]=(byte)old;check(RunStorage.decode(compatible)!=null,"legacy run schema v"+old+" no longer loads");}
        check(RunStorage.decode(null)==null,"null run save must be rejected");
        byte[] truncated=new byte[snapshotBytes.length/2];System.arraycopy(snapshotBytes,0,truncated,0,truncated.length);
        check(RunStorage.decode(truncated)==null,"truncated run save must be rejected");
        byte[] wrongVersion=new byte[snapshotBytes.length];System.arraycopy(snapshotBytes,0,wrongVersion,0,snapshotBytes.length);
        wrongVersion[3]=99;check(RunStorage.decode(wrongVersion)==null,"unknown run save version must be rejected");
        byte[] wrongMode=new byte[snapshotBytes.length];System.arraycopy(snapshotBytes,0,wrongMode,0,snapshotBytes.length);
        wrongMode[4]=0;wrongMode[5]=0;wrongMode[6]=0;wrongMode[7]=99;
        check(RunStorage.decode(wrongMode)==null,"unknown run mode must be rejected");
        Run badRound=new Run(1202,Run.MODE_NORMAL);badRound.round=41;
        check(RunStorage.decode(RunStorage.encode(badRound))==null,"out-of-range run round must be rejected");
        Run badDraft=new Run(1203,Run.MODE_NORMAL);badDraft.draftStage=7;
        check(RunStorage.decode(RunStorage.encode(badDraft))==null,"out-of-range draft stage must be rejected");
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
        battle.fxTtl[0]=3;battle.shotTtl[0]=3;battle.skillFxTtl[0]=3;battle.boardFxTtl[0]=3;
        battle.units[0].hit=2;battle.units[0].cast=2;battle.units[0].attack=2;battle.units[0].moveLeft=2;
        battle.clearTransientVisuals();
        check(battle.fxTtl[0]==0&&battle.shotTtl[0]==0&&battle.skillFxTtl[0]==0&&battle.boardFxTtl[0]==0,
              "battle-end effects were not cleared");
        check(battle.units[0].hit==0&&battle.units[0].cast==0&&battle.units[0].attack==0&&battle.units[0].moveLeft==0,
              "battle-end unit animation state was not cleared");

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
        for(int seed=0;seed<25;seed++){
            Battle a=new Battle(player,enemy,100,new Rng(7000+seed));
            Battle b=new Battle(player,enemy,100,new Rng(7000+seed));
            while(!a.over&&!b.over){a.step();b.step();check(a.tick==b.tick&&a.over==b.over,"deterministic battle diverged at seed "+seed);}
            check(a.over==b.over&&a.winner==b.winner&&a.tick==b.tick,"deterministic result mismatch at seed "+seed);
        }

        System.out.println("CombatSmokeTest OK: responsive matrix, corrupt saves, deterministic combat, synergies, items, abilities/statuses, economy, PvE/bosses, save/resume/history, Camp/Farm rules, "+ItemData.recipeCount()+" item battles and 126 base battles");
    }
}

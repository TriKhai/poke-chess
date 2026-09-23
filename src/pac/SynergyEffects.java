package pac;

/** Original thresholds plus CLDC-safe offline combat synergy effects. */
public final class SynergyEffects {
    private SynergyEffects() {}
    private static final int[][] TH={
      {3,5,7,9},{2,4,6,8},{3,6,9,0},{3,5,7,9},{3,5,7,0},{2,4,6,0},{3,5,7,0},{2,4,6,8},
      {2,4,6,8},{3,5,7,0},{2,4,6,8},{2,4,6,8},{3,5,7,0},{2,4,6,8},{2,4,6,8},{3,5,7,0},
      {2,4,6,8},{2,4,6,8},{3,5,7,0},{2,4,6,8},{2,4,6,0},{3,5,7,0},{3,6,9,0},{3,4,5,6},
      {2,4,6,0},{3,4,5,0},{2,4,6,0},{2,3,4,5},{2,4,6,8},{2,4,6,0},{2,4,6,9}
    };
    public static int tier(int type,int count){if(type<0||type>=TH.length)return 0;int lv=0;for(int i=0;i<4;i++)if(TH[type][i]>0&&count>=TH[type][i])lv=i+1;return lv;}
    public static int threshold(int type,int i){return type>=0&&type<TH.length&&i>=0&&i<4?TH[type][i]:0;}
    public static String marks(int type,int count){int lv=tier(type,count);StringBuffer b=new StringBuffer();for(int i=0;i<4&&TH[type][i]>0;i++){if(i>0)b.append(' ');if(lv==i+1)b.append('(');b.append(TH[type][i]);if(lv==i+1)b.append(')');}return b.toString();}
    private static boolean has(Unit u,int t){return Data.t1[u.sp]==t||Data.t2[u.sp]==t;}
    private static int itemCount(Unit u){int n=0;for(int i=0;i<3;i++)if(u.items[i]>=0)n++;return n;}
    private static void hp(Unit u,int amount){u.maxHp=Math.max(1,u.maxHp+amount);u.hp=u.maxHp;}
    private static void hpPct(Unit u,int p){hp(u,u.maxHp*p/100);}
    private static void atkPct(Unit u,int p){u.atk+=u.atk*p/100;}

    public static void apply(Battle b,int side,int[] count){
        int active=0,dragonStars=0;for(int t=0;t<Data.NT;t++)if(tier(t,count[t])>0)active++;
        for(int i=0;i<b.n;i++){Unit u=b.units[i];if(u.side==side&&has(u,Data.T_DRAGON))dragonStars+=Data.tier[u.sp];}
        for(int i=0;i<b.n;i++){Unit u=b.units[i];if(u.side!=side)continue;for(int t=0;t<Data.NT;t++){int lv=tier(t,count[t]);if(lv>0&&has(u,t)){u.synTier[t]=lv;applyOne(u,t,lv,active,dragonStars);}}}
        ghostCurses(b,side,tier(Data.T_GHOST,count[Data.T_GHOST]));
    }
    private static void applyOne(Unit u,int t,int lv,int active,int dragonStars){
        switch(t){
        case Data.T_NORMAL:{int[] v={0,15,35,60,90};u.shield+=v[lv];u.shieldDone+=v[lv];break;}
        case Data.T_FIRE:break;
        case Data.T_WATER:u.mana+=15*lv;if(u.maxMana>0)u.mana=Math.min(u.mana,u.maxMana);break;
        case Data.T_GRASS:u.regen+=2*lv;break;
        case Data.T_ELEC:break;
        case Data.T_ROCK:{int[] v={0,10,25,50};u.def+=v[lv];break;}
        case Data.T_PSY:{int[] v={0,50,100,150};u.skillBonus+=v[lv];break;}
        case Data.T_FIGHT:u.damageBlockEvery=10;break;
        case Data.T_FLY:u.flyCharges=lv<3?1:2;break;
        case Data.T_DRAGON:if(lv>=2){u.shield+=dragonStars*5;u.shieldDone+=dragonStars*5;}if(lv>=3){u.skillBonus+=dragonStars;u.speed+=dragonStars;}break;
        case Data.T_GHOST:u.dodge+=15;break;
        case Data.T_BUG:hpPct(u,5*lv);break;
        case Data.T_POISON:break;
        case Data.T_GROUND:u.def+=lv;u.speDef+=lv;if(lv>=3)u.atk+=lv==3?5:8;break;
        case Data.T_ICE:{int[] v={0,4,12,25,50};u.speDef+=v[lv];break;}
        case Data.T_DARK:{int[] c={0,30,40,50},p={0,40,60,100};u.crit+=c[lv];u.critPower+=p[lv];break;}
        case Data.T_STEEL:u.def+=3;break;
        case Data.T_FAIRY:u.status.protect=Math.max(u.status.protect,lv*5);if(lv==4)u.crit+=5;break;
        case Data.T_AMORPHOUS:{int[] s={0,1,3,5},h={0,3,6,10};u.speed+=s[lv]*active;hp(u,h[lv]*active);break;}
        case Data.T_AQUATIC:u.speed+=10*lv;u.regen+=lv;break;
        case Data.T_ARTIFICIAL:{int n=itemCount(u);if(n>0&&lv>=2){int p=(lv==2?5:10)*n;atkPct(u,p);u.skillBonus+=(lv==2?5:10)*n;int sh=u.maxHp*p/100;u.shield+=sh;u.shieldDone+=sh;}break;}
        case Data.T_BABY:u.dodge+=5*lv;break;
        case Data.T_FIELD:break;
        case Data.T_FLORA:u.regen+=lv;u.skillBonus+=10*lv;break;
        case Data.T_FOSSIL:u.def+=3*lv;u.atk+=2*lv;break;
        case Data.T_GOURMET:hpPct(u,8*lv);u.regen+=lv;break;
        case Data.T_HUMAN:{int[] v={0,25,35,50};u.lifesteal+=v[lv];break;}
        case Data.T_LIGHT:atkPct(u,20);u.skillBonus+=20;if(lv>=3){u.def+=u.def/2;u.speDef+=u.speDef/2;}if(lv>=4){u.shield+=100;u.shieldDone+=100;}break;
        case Data.T_MONSTER:break;
        case Data.T_SOUND:break;
        case Data.T_WILD:u.speed+=lv==1?20:40;if(lv>=3)atkPct(u,40);break;
        }
        u.cd=Math.max(2,CombatRules.cooldownTicks(1000,u.speed));
    }
    private static void ghostCurses(Battle b,int side,int lv){if(lv==0)return;Unit hd=null,ha=null,hp=null,strong=null;for(int i=0;i<b.n;i++){Unit u=b.units[i];if(u.side==side)continue;if(hd==null||u.def+u.speDef>hd.def+hd.speDef)hd=u;if(ha==null||u.atk>ha.atk)ha=u;if(hp==null||u.skillBonus>hp.skillBonus)hp=u;if(strong==null||u.maxHp+u.atk*5>strong.maxHp+strong.atk*5)strong=u;}if(hd!=null){hd.def=Math.max(0,hd.def-5);hd.speDef=Math.max(0,hd.speDef-5);}if(lv>=2&&ha!=null){ha.atk=ha.atk*80/100;ha.status.paralysis=Battle.HARD_LIMIT;}if(lv>=3&&hp!=null){hp.skillBonus-=30;hp.status.silence=30;}if(lv>=4&&strong!=null)strong.status.wound=80;}
    public static int critPower(Unit u){return CombatRules.CRIT_POWER_PERCENT+u.critPower;}
    public static int extraAttacks(Unit u){int lv=u.synTier[Data.T_ELEC];return (lv==1&&u.basicCount%4==0)||(lv>=2&&u.basicCount%3==0)?2:0;}
    public static void onBasicAttack(Unit u,Unit target,Rng rng){u.basicCount++;int fire=u.synTier[Data.T_FIRE];if(fire>=2)u.atk+=fire-1;int p=u.synTier[Data.T_POISON],chance=p==1?30:(p==2?60:(p>=3?100:0));if(chance>0&&rng.pct(chance))target.status.poison=Math.max(target.status.poison,40);if(u.synTier[Data.T_WILD]>0&&rng.pct(25))target.status.wound=Math.max(target.status.wound,30);}
    public static void onCast(Battle b,Unit caster){int lv=caster.synTier[Data.T_SOUND];if(lv==0)return;int atk=lv==1?2:1,spd=lv>=2?5:0,pp=lv>=3?3:0;for(int i=0;i<b.n;i++){Unit u=b.units[i];if(u.alive&&u.side==caster.side){u.atk+=atk;u.speed+=spd;if(u.maxMana>0)u.mana=Math.min(u.maxMana,u.mana+pp);}}}
    public static void onKill(Battle b,Unit killer,Unit dead){if(killer!=null){int lv=killer.synTier[Data.T_MONSTER];if(lv>0){int[] a={0,3,6,10,10},ap={0,10,20,30,30},h={0,20,40,60,60};killer.atk+=a[lv];killer.skillBonus+=ap[lv];hp(killer,dead.maxHp*h[lv]/100);}}int f=dead.synTier[Data.T_FIELD];if(f>0){int[] heal={0,30,40,50},speed={0,15,20,25};for(int i=0;i<b.n;i++){Unit u=b.units[i];if(u.alive&&u.side==dead.side&&has(u,Data.T_FIELD)){u.hp=Math.min(u.maxHp,u.hp+heal[f]);u.speed+=speed[f];}}}}
}

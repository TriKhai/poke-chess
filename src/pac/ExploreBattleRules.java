package pac;

/** Capture and type-effectiveness rules used only by Explore encounters. */
public final class ExploreBattleRules{
    private ExploreBattleRules(){}
    public static int catchChance(int sp,int hp,int maxHp){int[] base={0,55,45,35,27,20,14,8};int cat=Data.category[sp],chance=cat==6?22:base[Math.min(base.length-1,cat+1)];int pct=maxHp<=0?0:Math.max(0,Math.min(100,hp*100/maxHp));chance+=(100-pct)*30/100;if(pct<=20)chance+=20;if(chance>95)chance=95;return chance;}
    public static int damage(int attacker,int defender,boolean skill){int pct=typePct(Data.t1[attacker],Data.t1[defender],Data.t2[defender]);int raw=Data.atk[attacker]*(skill?135:100)/100,dmg=Math.max(1,raw*100/(100+Data.def[defender]*6)*pct/100);int cap=Math.max(2,Data.hp[defender]*(skill?45:32)/100);return Math.min(dmg,cap);}
    public static int typePct(int attack,int d1,int d2){int p=one(attack,d1);if(d2>=0&&d2<18)p=p*one(attack,d2)/100;return p;}
    private static int one(int a,int d){if(a<0||a>=18||d<0||d>=18)return 100;switch(a){
        case Data.T_NORMAL:return d==Data.T_ROCK||d==Data.T_STEEL?50:(d==Data.T_GHOST?0:100);
        case Data.T_FIRE:return d==Data.T_GRASS||d==Data.T_ICE||d==Data.T_BUG||d==Data.T_STEEL?200:(d==Data.T_FIRE||d==Data.T_WATER||d==Data.T_ROCK||d==Data.T_DRAGON?50:100);
        case Data.T_WATER:return d==Data.T_FIRE||d==Data.T_ROCK||d==Data.T_GROUND?200:(d==Data.T_WATER||d==Data.T_GRASS||d==Data.T_DRAGON?50:100);
        case Data.T_GRASS:return d==Data.T_WATER||d==Data.T_ROCK||d==Data.T_GROUND?200:(d==Data.T_FIRE||d==Data.T_GRASS||d==Data.T_POISON||d==Data.T_FLY||d==Data.T_BUG||d==Data.T_DRAGON||d==Data.T_STEEL?50:100);
        case Data.T_ELEC:return d==Data.T_WATER||d==Data.T_FLY?200:(d==Data.T_ELEC||d==Data.T_GRASS||d==Data.T_DRAGON?50:(d==Data.T_GROUND?0:100));
        case Data.T_ROCK:return d==Data.T_FIRE||d==Data.T_ICE||d==Data.T_FLY||d==Data.T_BUG?200:(d==Data.T_FIGHT||d==Data.T_GROUND||d==Data.T_STEEL?50:100);
        case Data.T_PSY:return d==Data.T_FIGHT||d==Data.T_POISON?200:(d==Data.T_PSY||d==Data.T_STEEL?50:(d==Data.T_DARK?0:100));
        case Data.T_FIGHT:return d==Data.T_NORMAL||d==Data.T_ROCK||d==Data.T_ICE||d==Data.T_DARK||d==Data.T_STEEL?200:(d==Data.T_POISON||d==Data.T_FLY||d==Data.T_PSY||d==Data.T_BUG||d==Data.T_FAIRY?50:(d==Data.T_GHOST?0:100));
        case Data.T_FLY:return d==Data.T_GRASS||d==Data.T_FIGHT||d==Data.T_BUG?200:(d==Data.T_ELEC||d==Data.T_ROCK||d==Data.T_STEEL?50:100);
        case Data.T_DRAGON:return d==Data.T_DRAGON?200:(d==Data.T_STEEL?50:(d==Data.T_FAIRY?0:100));
        case Data.T_GHOST:return d==Data.T_PSY||d==Data.T_GHOST?200:(d==Data.T_DARK?50:(d==Data.T_NORMAL?0:100));
        case Data.T_BUG:return d==Data.T_GRASS||d==Data.T_PSY||d==Data.T_DARK?200:(d==Data.T_FIRE||d==Data.T_FIGHT||d==Data.T_POISON||d==Data.T_FLY||d==Data.T_GHOST||d==Data.T_STEEL||d==Data.T_FAIRY?50:100);
        case Data.T_POISON:return d==Data.T_GRASS||d==Data.T_FAIRY?200:(d==Data.T_POISON||d==Data.T_GROUND||d==Data.T_ROCK||d==Data.T_GHOST?50:(d==Data.T_STEEL?0:100));
        case Data.T_GROUND:return d==Data.T_FIRE||d==Data.T_ELEC||d==Data.T_POISON||d==Data.T_ROCK||d==Data.T_STEEL?200:(d==Data.T_GRASS||d==Data.T_BUG?50:(d==Data.T_FLY?0:100));
        case Data.T_ICE:return d==Data.T_GRASS||d==Data.T_GROUND||d==Data.T_FLY||d==Data.T_DRAGON?200:(d==Data.T_FIRE||d==Data.T_WATER||d==Data.T_ICE||d==Data.T_STEEL?50:100);
        case Data.T_DARK:return d==Data.T_PSY||d==Data.T_GHOST?200:(d==Data.T_FIGHT||d==Data.T_DARK||d==Data.T_FAIRY?50:100);
        case Data.T_STEEL:return d==Data.T_ICE||d==Data.T_ROCK||d==Data.T_FAIRY?200:(d==Data.T_FIRE||d==Data.T_WATER||d==Data.T_ELEC||d==Data.T_STEEL?50:100);
        case Data.T_FAIRY:return d==Data.T_FIGHT||d==Data.T_DRAGON||d==Data.T_DARK?200:(d==Data.T_FIRE||d==Data.T_POISON||d==Data.T_STEEL?50:100);default:return 100;}}
}

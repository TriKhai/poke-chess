package pac;

/** Fixed PvE encounters adapted from the source game's PVEStages table. */
public final class EnemyFormation {
    private EnemyFormation(){}
    private static int sp(String name){for(int i=0;i<Data.N;i++)if(Data.name[i].equals(name))return i;return 0;}
    private static int item(String id){return ItemData.indexOf(id);}
    private static void put(Run r,String name,int x,int y,int a,int b){
        int p=y*8+x;r.enemy[p]=sp(name);if(a>=0)r.enemyEquip[p*3]=a;if(b>=0)r.enemyEquip[p*3+1]=b;
    }
    private static void put(Run r,String name,int x,int y){put(r,name,x,y,-1,-1);}

    /** Returns true when this round has a source-backed scripted encounter. */
    public static boolean apply(Run r){
        if(r.mode!=Run.MODE_NORMAL)return false;
        int k=r.round;
        if(k==1){put(r,"Magikarp",3,1);put(r,"Magikarp",5,1);r.enemyScale=80;r.enemyName="Wild Magikarp";return true;}
        if(k==2){put(r,"Rattata",3,1);put(r,"Rattata",5,1);r.enemyScale=85;r.enemyName="Wild Rattata";return true;}
        if(k==3){put(r,"Spearow",3,1);put(r,"Spearow",5,1);put(r,"Spearow",4,2);r.enemyScale=90;r.enemyName="Wild Spearow";return true;}
        if(k==8){put(r,"Gyarados",4,2,item("KINGS_ROCK"),-1);r.enemyScale=125;r.enemyName="BOSS: Gyarados";return true;}
        if(k==12){put(r,"Mewtwo",0,1,item("METAL_COAT"),-1);put(r,"Mew",7,1,item("DEEP_SEA_TOOTH"),-1);r.enemyScale=135;r.enemyName="BOSS: Mewtwo & Mew";return true;}
        if(k==16){put(r,"Lugia",3,1,item("STAR_PIECE"),-1);put(r,"Ho-Oh",5,1,item("SACRED_ASH"),-1);r.enemyScale=150;r.enemyName="BOSS: Tower Duo";return true;}
        if(k==20){put(r,"Zapdos",2,2,item("XRAY_VISION"),item("BLUE_ORB"));put(r,"Moltres",4,2,item("SOUL_DEW"),item("POKEMONOMICON"));put(r,"Articuno",6,2,item("AQUA_EGG"),item("STAR_DUST"));r.enemyScale=165;r.enemyName="CHAMPION: Legendary Birds";return true;}
        return false;
    }
}

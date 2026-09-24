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
        if(k==10){put(r,"Gyarados",4,2,item("KINGS_ROCK"),-1);r.enemyScale=125;r.enemyName="BOSS: Gyarados";return true;}
        if(k==14){put(r,"Mewtwo",0,1,item("METAL_COAT"),-1);put(r,"Mew",7,1,item("DEEP_SEA_TOOTH"),-1);r.enemyScale=135;r.enemyName="BOSS: Mewtwo & Mew";return true;}
        if(k==19){put(r,"Lugia",3,1,item("STAR_PIECE"),-1);put(r,"Ho-Oh",5,1,item("SACRED_ASH"),-1);r.enemyScale=150;r.enemyName="BOSS: Tower Duo";return true;}
        if(k==24){put(r,"Zapdos",2,2,item("XRAY_VISION"),item("BLUE_ORB"));put(r,"Moltres",4,2,item("SOUL_DEW"),item("POKEMONOMICON"));put(r,"Articuno",6,2,item("AQUA_EGG"),item("STAR_DUST"));r.enemyScale=165;r.enemyName="BOSS: Legendary Birds";return true;}
        if(k==28){put(r,"Entei",2,2);put(r,"Raikou",4,2);put(r,"Suicune",6,2);r.enemyScale=180;r.enemyName="BOSS: Legendary Beasts";return true;}
        if(k==32){put(r,"Kyogre",2,2);put(r,"Rayquaza",4,2);put(r,"Groudon",6,2);r.enemyScale=195;r.enemyName="BOSS: Super Ancients";return true;}
        if(k==36){put(r,"Regice",2,2);put(r,"Regirock",4,2);put(r,"Registeel",6,2);r.enemyScale=210;r.enemyName="BOSS: Legendary Giants";return true;}
        if(k==40){put(r,"Lugia",1,2);put(r,"Mewtwo",3,1);put(r,"Rayquaza",4,2);put(r,"Ho-Oh",6,2);r.enemyScale=230;r.enemyName="FINAL: Arceus Trial";return true;}
        return false;
    }
}

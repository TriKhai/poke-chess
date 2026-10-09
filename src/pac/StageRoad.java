package pac;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Three-step 40-stage road used by preparation and battle HUDs. */
public final class StageRoad {
    private StageRoad(){}
    public static final int KIND_BATTLE=0,KIND_ITEM=1,KIND_POKEMON=2,KIND_BOSS=3,CATALOG_COUNT=4;
    public static final int REWARD_ITEM=1,REWARD_FAMILY=2,REWARD_UNIQUE=4,REWARD_LEGEND=8;
    private static final int[] BOSS_ROUND={10,14,19,24,28,32,36,40};
    private static final Image[] BOSS=new Image[BOSS_ROUND.length];
    private static final Image[] BOSS_DIM=new Image[BOSS_ROUND.length];
    private static final boolean[] TRIED=new boolean[BOSS_ROUND.length];

    public static boolean boss(int round){return bossIndex(round)>=0;}
    public static boolean boss(int round,int mode){if(mode==Run.MODE_BOSS_RUSH)return true;return mode==Run.MODE_TOWER||mode==Run.MODE_THIRTY||mode==Run.MODE_ENDLESS||ProgressionRules.isGeneration(mode)?round%5==0:boss(round);}
    public static boolean itemReward(int round){return round>=5&&round<=35&&round%5==0;}
    public static boolean pokemonReward(int round){return round==5||round==8||round==10||round==11||round==20;}
    public static boolean hasReward(int round){return itemReward(round)||pokemonReward(round);}
    public static int rewardMask(int round){int mask=0;if(itemReward(round))mask|=REWARD_ITEM;if(round==5||round==8||round==11)mask|=REWARD_FAMILY;if(round==10)mask|=REWARD_UNIQUE;if(round==20)mask|=REWARD_LEGEND;return mask;}
    public static int kind(int round,int mode){if(boss(round,mode))return KIND_BOSS;if(itemReward(round))return KIND_ITEM;if(pokemonReward(round))return KIND_POKEMON;return KIND_BATTLE;}
    private static int bossIndex(int round){for(int i=0;i<BOSS_ROUND.length;i++)if(BOSS_ROUND[i]==round)return i;return-1;}

    private static Image bossImage(int round,boolean dim){
        int i=bossIndex(round);if(i<0)return null;
        if(!TRIED[i]){
            TRIED[i]=true;
            try{
                BOSS[i]=Image.createImage("/stage/"+round+".png");
                int w=BOSS[i].getWidth(),h=BOSS[i].getHeight(),n=w*h;
                int[] px=new int[n];BOSS[i].getRGB(px,0,w,0,0,w,h);
                for(int p=0;p<n;p++){int c=px[p],a=c&0xFF000000;px[p]=a|(((c>>16)&255)*3/7<<16)|(((c>>8)&255)*3/7<<8)|(c&255)*3/7;}
                BOSS_DIM[i]=Image.createRGBImage(px,w,h,true);
            }catch(Exception e){BOSS[i]=BOSS_DIM[i]=null;}
        }
        return dim?BOSS_DIM[i]:BOSS[i];
    }

    /** Draw current stage and the next two. Returns the right edge used. */
    public static int draw(Graphics g,int x,int y,int current,int max,int size){
        return draw(g,x,y,current,max,size,3);
    }

    /** Same road with a responsive maximum number of visible steps. */
    public static int draw(Graphics g,int x,int y,int current,int max,int size,int visible){
        return draw(g,x,y,current,max,size,visible,Run.MODE_NORMAL);
    }

    public static int draw(Graphics g,int x,int y,int current,int max,int size,int visible,int mode){
        return drawInternal(g,x,y,current,max,size,visible,mode,null);
    }
    public static int draw(Graphics g,int x,int y,int current,int max,int size,int visible,Run run){
        return drawInternal(g,x,y,current,max,size,visible,run.mode,run);
    }
    private static int drawInternal(Graphics g,int x,int y,int current,int max,int size,int visible,int mode,Run run){
        int n=Math.min(visible,max-current+1);if(n<=0)return x;
        int gap=5,total=n*size+(n-1)*gap;
        for(int i=0;i<n;i++){
            int round=current+i,sx=x+i*(size+gap);boolean now=i==0;
            if(i>0){g.setColor(0x566173);g.drawLine(sx-gap,y+size/2,sx-1,y+size/2);}
            g.setColor(now?0x367FC1:0x252C3B);g.fillRect(sx,y,size,size);
            boolean bossRound=boss(round,mode);Image boss=mode==Run.MODE_NORMAL||mode==Run.MODE_UNLIMITED?bossImage(round,!now):null;
            if(boss!=null){
                int ox=(size-boss.getWidth())/2,oy=(size-boss.getHeight())/2;
                g.drawImage(boss,sx+ox,y+oy,Graphics.TOP|Graphics.LEFT);
                if(hasReward(round))drawRewardBadge(g,sx,y,size,now);
            }else{int kind=kind(round,mode);if(bossRound&&run!=null&&(run.generationMode()||run.endlessMode())){int sp=run.generationBossSpecies(round);if(now)Art.avatarMini(g,sp,sx+(size-20)/2,y+(size-20)/2);else Art.avatarMiniGray(g,sp,sx+(size-20)/2,y+(size-20)/2);if(hasReward(round))drawRewardBadge(g,sx,y,size,now);}else if(kind==KIND_ITEM)drawItemReward(g,sx,y,size,now);else if(kind==KIND_POKEMON)drawPokemonReward(g,sx,y,size,now);else if(bossRound){drawBattle(g,sx,y,size,now);drawBossCrown(g,sx,y,size);if(hasReward(round))drawRewardBadge(g,sx,y,size,now);}else drawBattle(g,sx,y,size,now);}
            g.setColor(now?0xFFFFFF:0x687386);g.drawRect(sx,y,size-1,size-1);
            if(now){g.drawRect(sx+1,y+1,size-3,size-3);}
            String label=""+round;
            Art.textSmallR(g,label,sx+size-2,y+size-Art.fh,now?0xFFFFFF:0x9AA4B4);
        }
        return x+total;
    }

    private static void drawBattle(Graphics g,int x,int y,int s,boolean bright){
        int c=bright?0xE8F4FF:0x697586,cx=x+s/2,cy=y+s/2-1,r=Math.max(5,s/4);
        g.setColor(c);
        for(int d=-1;d<=1;d++){
            g.drawLine(cx-r+d,cy-r,cx+r+d,cy+r);
            g.drawLine(cx+r+d,cy-r,cx-r+d,cy+r);
        }
        g.fillTriangle(cx-r-2,cy-r-2,cx-r+4,cy-r,cx-r,cy-r+4);
        g.fillTriangle(cx+r+2,cy-r-2,cx+r-4,cy-r,cx+r,cy-r+4);
    }

    private static void drawItemReward(Graphics g,int x,int y,int s,boolean bright){
        int c=bright?0xFFD34E:0x776B45,cx=x+s/2,cy=y+s/2;
        g.setColor(c);g.fillRect(cx-7,cy-4,15,10);g.drawRect(cx-6,cy-8,13,4);g.setColor(bright?0xFFF2A0:0x92855D);g.drawRect(cx-7,cy-4,14,9);g.fillRect(cx-1,cy-4,3,5);
    }

    private static void drawPokemonReward(Graphics g,int x,int y,int s,boolean bright){
        int c=bright?0xF4F7FF:0x737B89,cx=x+s/2,cy=y+s/2,r=Math.max(6,s/4);
        g.setColor(c);g.fillArc(cx-r,cy-r,r*2,r*2,0,360);g.setColor(bright?0xD94A4A:0x704D55);g.fillArc(cx-r+1,cy-r+1,r*2-2,r-1,0,180);g.setColor(bright?0x273448:0x4B5260);g.drawLine(cx-r,cy,cx+r,cy);g.fillArc(cx-3,cy-3,7,7,0,360);g.setColor(c);g.fillArc(cx-1,cy-1,3,3,0,360);
    }

    private static void drawRewardBadge(Graphics g,int x,int y,int s,boolean bright){int c=bright?0xFFD030:0x756B46;g.setColor(0x182030);g.fillArc(x+s-9,y+s-9,9,9,0,360);g.setColor(c);g.fillRect(x+s-7,y+s-6,5,4);g.drawRect(x+s-7,y+s-8,4,2);}

    /** Draws one icon for the Collection legend. */
    public static void drawCatalogIcon(Graphics g,int kind,int x,int y,int size){g.setColor(0x263448);g.fillRect(x,y,size,size);if(kind==KIND_ITEM)drawItemReward(g,x,y,size,true);else if(kind==KIND_POKEMON)drawPokemonReward(g,x,y,size,true);else if(kind==KIND_BOSS){drawBattle(g,x,y,size,true);drawBossCrown(g,x,y,size);}else drawBattle(g,x,y,size,true);g.setColor(0xFFFFFF);g.drawRect(x,y,size-1,size-1);}
    private static void drawBossCrown(Graphics g,int x,int y,int s){int cx=x+s/2;g.setColor(0xFFD030);g.fillTriangle(cx-7,y+6,cx-3,y+11,cx,y+5);g.fillTriangle(cx,y+5,cx+3,y+11,cx+7,y+6);g.fillRect(cx-7,y+10,15,3);}
    public static String catalogName(int kind){switch(kind){case KIND_ITEM:return Lang.t("Vòng quà trang bị","Item reward stage");case KIND_POKEMON:return Lang.t("Vòng quà Pokémon","Pokémon reward stage");case KIND_BOSS:return Lang.t("Ải Boss","Boss stage");default:return Lang.t("Trận đấu thường","Normal battle");}}
    public static String catalogDesc(int kind){switch(kind){case KIND_ITEM:return Lang.t("Thắng vòng này được chọn 1 trong 3 trang bị hoàn chỉnh. Xuất hiện tại vòng 5, 10, 15, 20, 25, 30 và 35.","Win to choose 1 of 3 completed items. Appears at rounds 5, 10, 15, 20, 25, 30 and 35.");case KIND_POKEMON:return Lang.t("Thắng để chọn thêm family/Pokémon: vòng 5, 8, 11; Unique ở vòng 10 và Legendary ở vòng 20.","Win to choose an extra family/Pokémon at 5, 8 and 11; Unique at 10 and Legendary at 20.");case KIND_BOSS:return Lang.t("Đội địch đặc biệt và khó hơn. Normal dùng avatar Boss; mode khác dùng vương miện. Boss có quà kèm huy hiệu vàng.","A stronger special team. Normal uses a Boss portrait; other modes use a crown. Reward Bosses show a gold badge.");default:return Lang.t("Trận PvE thường. Thắng mới sang vòng kế; thua nhận vàng thua và đấu lại. Mỗi trận thắng vẫn rơi 1 mảnh đồ; icon quà chỉ đánh dấu quà chọn đặc biệt.","A normal PvE battle. Win to advance; lose to receive loss gold and retry. Every win still drops a component; reward icons mark special choice rewards.");}}
}

package pac;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Three-step 40-stage road used by preparation and battle HUDs. */
public final class StageRoad {
    private StageRoad(){}
    private static final int[] BOSS_ROUND={9,14,19,24,28,32,36,40};
    private static final Image[] BOSS=new Image[BOSS_ROUND.length];
    private static final Image[] BOSS_DIM=new Image[BOSS_ROUND.length];
    private static final boolean[] TRIED=new boolean[BOSS_ROUND.length];

    public static boolean boss(int round){return bossIndex(round)>=0;}
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
        int n=Math.min(3,max-current+1);if(n<=0)return x;
        int gap=5,total=n*size+(n-1)*gap;
        for(int i=0;i<n;i++){
            int round=current+i,sx=x+i*(size+gap);boolean now=i==0;
            if(i>0){g.setColor(0x566173);g.drawLine(sx-gap,y+size/2,sx-1,y+size/2);}
            g.setColor(now?0x367FC1:0x252C3B);g.fillRect(sx,y,size,size);
            Image boss=bossImage(round,!now);
            if(boss!=null){
                int ox=(size-boss.getWidth())/2,oy=(size-boss.getHeight())/2;
                g.drawImage(boss,sx+ox,y+oy,Graphics.TOP|Graphics.LEFT);
            }else drawBattle(g,sx,y,size,now);
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
}

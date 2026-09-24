package pac;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Original eight-frame portal strip used by the Gacha vortex. */
public final class GachaFx{
    private static Image portal,farm;private static boolean tried,farmTried;
    private static final int[] TRANSITION_COL={0x6A28A8,0xB84CE8,0x58C8FF};
    private GachaFx(){}
    public static void portal(Graphics g,int cx,int cy,int clock){if(!tried){tried=true;try{portal=Image.createImage("/fx/gacha_portal.png");}catch(Exception e){portal=null;}}if(portal!=null)g.drawRegion(portal,(clock/90&7)*52,0,52,52,0,cx-26,cy-26,Graphics.TOP|Graphics.LEFT);else{g.setColor(0x503080);g.fillArc(cx-22,cy-22,44,44,0,360);g.setColor(0x080510);g.fillArc(cx-13,cy-13,26,26,0,360);}}
    public static void farmPortal(Graphics g,int cx,int cy,int clock,int shrink){if(!farmTried){farmTried=true;try{farm=Image.createImage("/fx/farm_portal.png");}catch(Exception e){farm=null;}}if(shrink<0)shrink=0;if(shrink>3)shrink=3;if(farm!=null)g.drawRegion(farm,(clock/90&7)*52,shrink*52,52,52,0,cx-26,cy-26,Graphics.TOP|Graphics.LEFT);else portal(g,cx,cy,clock);}
    /** Scalable lightweight vortex for scene transitions; avoids allocating scaled Images. */
    public static void transitionPortal(Graphics g,int cx,int cy,int clock,int radius){
        if(radius<2)return;
        g.setColor(0x05030B);g.fillArc(cx-radius,cy-radius,radius*2,radius*2,0,360);
        int turn=(clock/12)%360;
        for(int i=0;i<3;i++){
            int r=radius-i*4;if(r<3)continue;
            g.setColor(TRANSITION_COL[i]);g.drawArc(cx-r,cy-r,r*2,r*2,turn+i*92,145);
            g.drawArc(cx-r,cy-r,r*2,r*2,turn+190+i*73,80);
        }
        int core=radius/2;if(core<2)core=2;
        g.setColor(0x000000);g.fillArc(cx-core,cy-core,core*2,core*2,0,360);
        g.setColor(0xE8C8FF);g.drawArc(cx-radius+1,cy-radius+1,radius*2-2,radius*2-2,turn,55);
    }
}

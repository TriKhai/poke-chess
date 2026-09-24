package pac;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Original eight-frame portal strip used by the Gacha vortex. */
public final class GachaFx{
    private static Image portal,farm;private static boolean tried,farmTried;
    private GachaFx(){}
    public static void portal(Graphics g,int cx,int cy,int clock){if(!tried){tried=true;try{portal=Image.createImage("/fx/gacha_portal.png");}catch(Exception e){portal=null;}}if(portal!=null)g.drawRegion(portal,(clock/90&7)*52,0,52,52,0,cx-26,cy-26,Graphics.TOP|Graphics.LEFT);else{g.setColor(0x503080);g.fillArc(cx-22,cy-22,44,44,0,360);g.setColor(0x080510);g.fillArc(cx-13,cy-13,26,26,0,360);}}
    public static void farmPortal(Graphics g,int cx,int cy,int clock,int shrink){if(!farmTried){farmTried=true;try{farm=Image.createImage("/fx/farm_portal.png");}catch(Exception e){farm=null;}}if(shrink<0)shrink=0;if(shrink>3)shrink=3;if(farm!=null)g.drawRegion(farm,(clock/90&7)*52,shrink*52,52,52,0,cx-26,cy-26,Graphics.TOP|Graphics.LEFT);else portal(g,cx,cy,clock);}
    /** Transition uses the same Gacha artwork, with its four pre-scaled rows. */
    public static void transitionPortal(Graphics g,int cx,int cy,int clock,int radius){
        if(radius<2)return;
        int shrink=radius<9?3:(radius<15?2:(radius<22?1:0));
        farmPortal(g,cx,cy,clock,shrink);
    }
}

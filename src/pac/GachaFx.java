package pac;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Original eight-frame portal strip used by the Gacha vortex. */
public final class GachaFx{
    private static Image portal;private static boolean tried;
    private GachaFx(){}
    public static void portal(Graphics g,int cx,int cy,int clock){if(!tried){tried=true;try{portal=Image.createImage("/fx/gacha_portal.png");}catch(Exception e){portal=null;}}if(portal!=null)g.drawRegion(portal,(clock/90&7)*52,0,52,52,0,cx-26,cy-26,Graphics.TOP|Graphics.LEFT);else{g.setColor(0x503080);g.fillArc(cx-22,cy-22,44,44,0,360);g.setColor(0x080510);g.fillArc(cx-13,cy-13,26,26,0,360);}}
}

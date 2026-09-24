package pac;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
/** Fixed TinyMeadow source preview used as a lightweight Camp/Farm background. */
public final class CampMap{
 private static Image image;private static boolean tried;private CampMap(){}
 public static void draw(Graphics g,int x,int y,int w,int h){if(!tried){tried=true;try{image=Image.createImage("/fx/camp_grass.png");}catch(Exception e){image=null;}}g.setColor(0x315C32);g.fillRect(x,y,w,h);if(image==null)return;int sx=image.getWidth()>w?(image.getWidth()-w)/2:0,sy=image.getHeight()>h?(image.getHeight()-h)/2:0,sw=Math.min(w,image.getWidth()),sh=Math.min(h,image.getHeight());g.drawRegion(image,sx,sy,sw,sh,0,x+(w-sw)/2,y+(h-sh)/2,Graphics.TOP|Graphics.LEFT);}
}

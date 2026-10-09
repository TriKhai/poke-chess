package pac;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Camera-aligned generated tile terrain; render only visible cells. */
public final class SurvivalMap {
 private static Image tiles;private static boolean tried;
 private SurvivalMap(){}
 public static void draw(Graphics g,int x,int y,int w,int h,int cameraX,int cameraY){
  if(!tried){tried=true;try{tiles=Image.createImage("/fx/survival-terrain.png");}catch(Exception e){Game.lastError="Survival terrain missing";}}
  g.setClip(x,y,w,h);g.setColor(0x739D4B);g.fillRect(x,y,w,h);if(tiles!=null){int firstX=cameraX/32,firstY=cameraY/32,lastX=Math.min(19,(cameraX+w-1)/32),lastY=Math.min(14,(cameraY+h-1)/32);for(int row=firstY;row<=lastY;row++)for(int col=firstX;col<=lastX;col++){int id=SurvivalMapData.tile(col,row);g.drawRegion(tiles,(id%4)*32,(id/4)*32,32,32,0,x+col*32-cameraX,y+row*32-cameraY,Graphics.TOP|Graphics.LEFT);}}
 }
}

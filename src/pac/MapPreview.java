package pac;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
/** Loads one Full-build map preview at a time and scales it to the current screen. */
public final class MapPreview{
 private MapPreview(){}private static int cached=-1,cw,ch;private static Image image;private static boolean available;
 public static void clear(){cached=-1;image=null;available=false;}
 private static void load(int id,int w,int h){if(id==cached&&w==cw&&h==ch)return;cached=id;cw=w;ch=h;image=null;available=false;try{Image src=Image.createImage("/maps/"+MapData.ID[id]+"-preview.png");int sw=src.getWidth(),sh=src.getHeight(),tw=w,th=sh*tw/sw;if(th>h){th=h;tw=sw*th/sh;}if(tw<1||th<1)return;int[] in=new int[sw*sh],out=new int[tw*th];src.getRGB(in,0,sw,0,0,sw,sh);for(int y=0;y<th;y++){int sy=y*sh/th;for(int x=0;x<tw;x++)out[y*tw+x]=in[sy*sw+x*sw/tw];}image=Image.createRGBImage(out,tw,th,false);available=true;}catch(Exception e){image=null;}}
 public static boolean draw(Graphics g,int id,int x,int y,int w,int h){load(id,w,h);if(image==null)return false;g.drawImage(image,x+(w-image.getWidth())/2,y+(h-image.getHeight())/2,Graphics.TOP|Graphics.LEFT);return true;}
 public static boolean available(){return available;}
}

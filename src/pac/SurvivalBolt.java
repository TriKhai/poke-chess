package pac;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
/** Single grayscale atlas and reusable tint buffer, no per-projectile allocation. */
final class SurvivalBolt {
 private static int[] source;private static boolean tried;private static final int[] pixels=new int[1024];
 static void draw(Graphics g,int type,int direction,int age,int x,int y){
  if(!tried){tried=true;try{Image im=Image.createImage("/fx/survival-bolt.png");source=new int[65536];im.getRGB(source,0,256,0,0,256,256);}catch(Exception e){}}
  if(source==null)return;int col=type<0?0xFFFFFF:Data.TCOL[type],r=(col>>16)&255,b=col&255,gr=(col>>8)&255,ox=((age/55)%8)*32,oy=direction*32;
  for(int yy=0;yy<32;yy++)for(int xx=0;xx<32;xx++){int p=source[(oy+yy)*256+ox+xx],v=(((p>>16)&255)+((p>>8)&255)+(p&255))/3,white=Math.max(0,v-210)*255/45;pixels[yy*32+xx]=(p&0xFF000000)|((r+(255-r)*white/255)<<16)|((gr+(255-gr)*white/255)<<8)|(b+(255-b)*white/255);}
  g.drawRGB(pixels,0,32,x-16,y-16,32,32,true);
 }
}

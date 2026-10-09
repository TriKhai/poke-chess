package pac;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Original skill frames, uniformly fitted to the damage diameter. Reusable buffers. */
final class SurvivalSkillFx {
 private static final int[] source=new int[8192],pixels=new int[16384];
 private static Image canvas;private static int species=-1,left,top,width,height;
 static void draw(Graphics g,int sp,int cx,int cy,int radius,int frame){
  if(sp!=species){try{if(canvas==null)canvas=Image.createImage(32,256);Graphics cg=canvas.getGraphics();cg.setColor(0xFF00FF);cg.fillRect(0,0,32,256);for(int f=0;f<8;f++)Art.speciesSkillSprite(cg,sp,0,f*32,f);canvas.getRGB(source,0,32,0,0,32,256);int right=-1,bottom=-1;left=top=32;for(int f=0;f<8;f++)for(int y=0;y<32;y++)for(int x=0;x<32;x++){int p=f*1024+y*32+x,c=source[p];if((c&0xFFFFFF)==0xFF00FF||(c>>>24)==0){source[p]=0;continue;}if(x<left)left=x;if(y<top)top=y;if(x>right)right=x;if(y>bottom)bottom=y;}width=right-left+1;height=bottom-top+1;species=sp;}catch(Exception e){species=-1;return;}}
  if(width<=0||height<=0)return;int size=Math.max(2,Math.min(128,radius*2)),scale=Math.min(size*256/width,size*256/height),w=Math.max(1,width*scale/256),h=Math.max(1,height*scale/256),ox=(size-w)/2,oy=(size-h)/2;
  for(int i=0;i<size*size;i++)pixels[i]=0;int offset=(frame&7)*1024;for(int y=0;y<h;y++)for(int x=0;x<w;x++)pixels[(y+oy)*size+x+ox]=source[offset+(top+y*height/h)*32+left+x*width/w];
  g.drawRGB(pixels,0,size,cx-size/2,cy-size/2,size,size,true);
 }
}

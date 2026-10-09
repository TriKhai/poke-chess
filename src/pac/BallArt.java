package pac;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Shared temporary ball icons. Replace the three PNG files without touching game logic. */
public final class BallArt{
 public static final int NORMAL=0,SILVER=1,GOLD=2;
 private static final String[] PATH={"/balls/normal.png","/balls/silver.png","/balls/gold.png"};
 private static Image[] img=new Image[3];private static boolean tried;
 private BallArt(){}
 /** Private-use glyphs consumed by PixelFont, never shown as text labels. */
 public static String symbol(int type){return type==SILVER?"\uE001":type==GOLD?"\uE002":"\uE000";}
 public static int symbolType(char c){return c>='\uE000'&&c<='\uE002'?c-'\uE000':-1;}
 private static final Image[] mini=new Image[3];
 public static void small(Graphics g,int type,int x,int y){if(type<0||type>2)return;load();if(mini[type]==null&&img[type]!=null){try{int w=img[type].getWidth(),h=img[type].getHeight();int[] src=new int[w*h],dst=new int[144];img[type].getRGB(src,0,w,0,0,w,h);for(int yy=0;yy<12;yy++)for(int xx=0;xx<12;xx++)dst[yy*12+xx]=src[yy*h/12*w+xx*w/12];mini[type]=Image.createRGBImage(dst,12,12,true);}catch(Exception e){}}if(mini[type]!=null)g.drawImage(mini[type],x,y,Graphics.TOP|Graphics.LEFT);else{g.setColor(type==GOLD?0xFFD030:type==SILVER?0xD8E0EC:0xF04040);g.fillArc(x,y,12,12,0,360);g.setColor(0x202838);g.drawLine(x,y+6,x+11,y+6);}}
 public static void amount(Graphics g,int type,int count,int x,int y,int color){small(g,type,x,y);Art.textSmall(g,""+count,x+16,y,color);}
 public static void wallet(Graphics g,int x,int y,int width){int cell=width/3,cx=g.getClipX(),cy=g.getClipY(),cw=g.getClipWidth(),ch=g.getClipHeight();for(int i=0;i<3;i++){g.setClip(cx,cy,cw,ch);g.clipRect(x+i*cell,y,cell,14);amount(g,i,count(i),x+i*cell,y,0xFFFFFF);}g.setClip(cx,cy,cw,ch);}
 private static void load(){if(tried)return;tried=true;for(int i=0;i<3;i++)try{img[i]=Image.createImage(PATH[i]);}catch(Exception e){img[i]=null;}}
 public static int count(int type){return type==SILVER?Save.silverBalls:(type==GOLD?Save.goldBalls:Save.balls);}
 public static String name(int type){return symbol(type);}
 public static int pity(int type){return type==SILVER?15:(type==GOLD?100:1);}
 public static void draw(Graphics g,int type,int x,int y){load();if(type>=0&&type<3&&img[type]!=null){g.drawImage(img[type],x,y,Graphics.TOP|Graphics.LEFT);return;}int c=type==GOLD?0xFFD030:(type==SILVER?0xD8E0EC:0xF04040);g.setColor(c);g.fillArc(x+4,y+4,24,24,0,360);g.setColor(0x202838);g.drawLine(x+4,y+16,x+27,y+16);g.fillArc(x+13,y+13,7,7,0,360);}
}

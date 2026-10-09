package pac;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Form-aware portraits for battle/result views; cache includes every visual state. */
public final class PetAvatar {
 private PetAvatar(){}
 private static final String[] keys=new String[32];private static Image[] images;private static int next;
 public static String path(int sp,int form,boolean mega,boolean shiny){if(form!=0)return "/formav/"+SpecialFormData.key(form)+(shiny?"-shiny":"")+".png";if(mega)return "/megaav/"+Data.nationalDex(sp)+(shiny?"-shiny":"")+".png";return sp<Data.CORE_N?(shiny?"/shinyav/":"/av/")+sp+".png":(shiny?"/shinydex/":"/dex/")+Data.nationalDex(sp)+".png";}
 public static String cacheKey(int sp,int form,boolean mega,boolean shiny,int size,boolean gray){return path(sp,form,mega,shiny)+"@"+size+(gray?":gray":":color");}
 static int grayPixel(int argb){int v=((argb>>16&255)*30+(argb>>8&255)*59+(argb&255)*11)/100;return(argb&0xFF000000)|(v<<16)|(v<<8)|v;}
 public static void unit(Graphics g,Unit u,int x,int y,int size){if(u!=null)draw(g,u.sp,u.specialForm,u.mega,u.shiny,x,y,size,!u.alive);}
 public static void slot(Graphics g,Run r,int p,int x,int y,int size){int sp=r.get(p);if(sp>=0)draw(g,sp,r.specialFormAt(p),r.isMega(p),r.isShiny(p),x,y,size);}
 public static void draw(Graphics g,int sp,int form,boolean mega,boolean shiny,int x,int y,int size){draw(g,sp,form,mega,shiny,x,y,size,false);}
 public static void draw(Graphics g,int sp,int form,boolean mega,boolean shiny,int x,int y,int size,boolean gray){
  if(sp<0||sp>=Data.N||size<=0)return;
  if(images==null)images=new Image[32];
  String key=cacheKey(sp,form,mega,shiny,size,gray);for(int i=0;i<keys.length;i++)if(key.equals(keys[i])&&images[i]!=null){g.drawImage(images[i],x,y,Graphics.TOP|Graphics.LEFT);return;}
  try{Image source;try{source=Image.createImage(path(sp,form,mega,shiny));}catch(Exception e){try{source=Image.createImage(path(sp,form,mega,false));}catch(Exception missing){source=Image.createImage(path(sp,0,false,shiny));}}
   int sw=source.getWidth(),sh=source.getHeight();int[] src=new int[sw*sh],dst=new int[size*size];source.getRGB(src,0,sw,0,0,sw,sh);
   for(int yy=0;yy<size;yy++)for(int xx=0;xx<size;xx++){int argb=src[(yy*sh/size)*sw+xx*sw/size];dst[yy*size+xx]=gray?grayPixel(argb):argb;}
   Image im=Image.createRGBImage(dst,size,size,true);keys[next]=key;images[next]=im;next=(next+1)%keys.length;g.drawImage(im,x,y,Graphics.TOP|Graphics.LEFT);
  }catch(Exception e){if(gray)Art.avatarMiniGray(g,sp,x,y);else if(size<20)Art.avatarTiny(g,sp,x,y);else if(size<32)Art.avatarMini(g,sp,x,y);else Art.avatar(g,sp,x,y);}
 }
}

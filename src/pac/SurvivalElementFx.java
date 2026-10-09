package pac;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
/** Existing authored animation selected by element, tinted with a reusable buffer. */
final class SurvivalElementFx {
 private static final int[][] sheets=new int[Data.NT][];private static final boolean[] tried=new boolean[Data.NT];private static final int[] pixels=new int[1024];
 static void draw(Graphics g,int type,int age,int x,int y){if(type<0){SurvivalBolt.draw(g,-1,4,age,x,y);return;}if(!tried[type]){tried[type]=true;int species=-1;for(int i=0;i<Data.CORE_N;i++)if(Data.t1[i]==type&&SkillFxData.effect(i)>=0){species=i;break;}if(species<0)for(int i=0;i<Data.N;i++)if(Data.t1[i]==type&&SkillFxData.effect(i)>=0){species=i;break;}if(species>=0)try{Image im=Image.createImage("/sfx/"+SkillFxData.effect(species)+".png");sheets[type]=new int[8192];im.getRGB(sheets[type],0,256,0,0,256,32);}catch(Exception e){}}
 if(sheets[type]==null){SurvivalBolt.draw(g,type,4,age,x,y);return;}int col=Data.TCOL[type],r=(col>>16)&255,gr=(col>>8)&255,b=col&255,frame=(age/75)%8;for(int yy=0;yy<32;yy++)for(int xx=0;xx<32;xx++){int p=sheets[type][yy*256+frame*32+xx],v=Math.max((p>>16)&255,Math.max((p>>8)&255,p&255)),white=Math.max(0,v-235)*255/20;pixels[yy*32+xx]=(p&0xFF000000)|((r+(255-r)*white/255)<<16)|((gr+(255-gr)*white/255)<<8)|(b+(255-b)*white/255);}g.drawRGB(pixels,0,32,x-16,y-16,32,32,true);
 }
}

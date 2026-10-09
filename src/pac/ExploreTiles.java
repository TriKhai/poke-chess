package pac;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Small cached generated tilesets; art never determines collision. */
public final class ExploreTiles {
 private ExploreTiles(){}private static final Image[] atlas=new Image[3];private static final boolean[] tried=new boolean[3];private static int currentGen=-1;
 public static boolean draw(Graphics g,int gen,int tile,int x,int y,int size){int family=gen>=4?gen:0;if(currentGen!=family){currentGen=family;for(int i=0;i<3;i++){atlas[i]=null;tried[i]=false;}}int index=size==20?0:size==16?1:2;if(!tried[index]){tried[index]=true;try{atlas[index]=Image.createImage("/map/explore-"+(family==0?"":"gen"+family+"-")+size+".png");}catch(Exception e){Game.lastError="Explore tiles missing";}}if(atlas[index]==null)return false;g.drawRegion(atlas[index],tile%4*size,tile/4*size,size,size,0,x,y,Graphics.TOP|Graphics.LEFT);return true;}
}

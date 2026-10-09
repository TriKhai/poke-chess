package pac;
/** Shared paint/hit geometry for direct Gacha touch controls. */
public final class GachaControls {
 private GachaControls(){}
 public static int ballLeft(int width){return width/2;}
 public static int ballWidth(int width){return (width-ballLeft(width)-4)/3;}
 public static int ballAt(int x,int y,int width,int fontHeight){int left=ballLeft(width),cell=ballWidth(width);if(cell<=0||x<left||x>=left+cell*3||y<fontHeight+3||y>=fontHeight+17)return -1;return (x-left)/cell;}
 public static int actionAt(int x,int y,int width,int height,int fontHeight){if(width<=0||x<0||x>=width||y<height-fontHeight-7||y>=height)return -1;return Math.min(3,((x+1)*4-1)/width);}
}

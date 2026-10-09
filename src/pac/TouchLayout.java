package pac;

/** Pure hit geometry shared by popup rendering and pointer handling. */
public final class TouchLayout {
    private TouchLayout(){}
    public static int evolutionHeight(int height,int fh){return Math.min(height-12,fh*3+112);}
    public static int evolutionRow(int height,int fh){return (evolutionHeight(height,fh)-fh*2-26)/2;}
    public static int rowAt(int px,int py,int x,int y,int width,int step,int paintedHeight,int count){
        if(width<=0||step<=0||paintedHeight<=0||px<x||px>=x+width||py<y)return -1;
        int offset=py-y,row=offset/step;
        return row<count&&offset%step<paintedHeight?row:-1;
    }
}

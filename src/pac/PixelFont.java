package pac;

import java.io.DataInputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Compact Vietnamese 7px bitmap font adapted from the NRO reference renderer. */
public final class PixelFont {
    private static final String CHARS=" 0123456789+-*='_?.,<>/[]{}!@#$%^&*():aáàảãạâấầẩẫậăắằẳẵặbcdđeéèẻẽẹêếềểễệfghiíìỉĩịjklmnoóòỏõọôốồổỗộơớờởỡợpqrstuúùủũụưứừửữựvxyýỳỷỹỵzwAÁÀẢÃẠĂẰẮẲẴẶÂẤẦẨẪẬBCDĐEÉÈẺẼẸÊẾỀỂỄỆFGHIÍÌỈĨỊJKLMNOÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢPQRSTUÚÙỦŨỤƯỨỪỬỮỰVXYÝỲỶỸỴZW";
    private static final int TL=Graphics.TOP|Graphics.LEFT;
    private final int[][] glyph;
    private final Image source;
    private final int[] sourcePixels;
    private final Image[] colorCache=new Image[6];
    private final int[] cacheColor={-1,-1,-1,-1,-1,-1};
    private final int[] cacheAge=new int[6];
    private int age=1,sourceW,sourceH;
    private final int spacing;
    private int height=8;

    public PixelFont(String base,boolean bold){
        spacing=bold?0:0;
        glyph=loadMetrics("/font/"+base);
        source=load("/font/"+base+"_white.png");
        if(source!=null){sourceW=source.getWidth();sourceH=source.getHeight();sourcePixels=new int[sourceW*sourceH];source.getRGB(sourcePixels,0,sourceW,0,0,sourceW,sourceH);}
        else sourcePixels=null;
        // The first glyph is SPACE and is only 1px high. Using it as the global
        // line height collapses every menu row. Use the tallest visible glyph.
        height=1;
        for(int i=0;i<glyph.length;i++)if(glyph[i][3]>height)height=glyph[i][3];
    }

    private Image load(String path){try{return Image.createImage(path);}catch(Exception e){return null;}}
    private int[][] loadMetrics(String path){
        DataInputStream in=null;
        try{
            in=new DataInputStream(getClass().getResourceAsStream(path));int n=in.readShort();int[][] a=new int[n][4];
            for(int i=0;i<n;i++)for(int j=0;j<4;j++)a[i][j]=in.readShort();return a;
        }catch(Exception e){return new int[][]{{0,0,3,8}};}
        finally{try{if(in!=null)in.close();}catch(Exception e){}}
    }

    public int height(){return height;}
    public int width(String s){int w=0;for(int i=0;i<s.length();i++){int k=CHARS.indexOf(s.charAt(i));if(k<0||k>=glyph.length)k=0;w+=glyph[k][2]+spacing;}return w;}
    private Image colored(int color){
        color&=0xFFFFFF;
        for(int i=0;i<colorCache.length;i++)if(cacheColor[i]==color&&colorCache[i]!=null){cacheAge[i]=age++;return colorCache[i];}
        if(sourcePixels==null)return source;
        int slot=0;for(int i=1;i<colorCache.length;i++)if(colorCache[i]==null||cacheAge[i]<cacheAge[slot])slot=i;
        try{
            int[] px=new int[sourcePixels.length];
            for(int i=0;i<px.length;i++)px[i]=(sourcePixels[i]&0xFF000000)|color;
            colorCache[slot]=Image.createRGBImage(px,sourceW,sourceH,true);cacheColor[slot]=color;cacheAge[slot]=age++;
            return colorCache[slot];
        }catch(Exception e){return source;}
    }
    public void draw(Graphics g,String s,int x,int y,int color){
        Image im=colored(color);if(im==null)return;
        for(int i=0;i<s.length();i++){
            int k=CHARS.indexOf(s.charAt(i));if(k<0||k>=glyph.length)k=0;int[] q=glyph[k];
            if(q[2]>0&&q[3]>0)g.drawRegion(im,q[0],q[1],q[2],q[3],0,x,y,TL);
            x+=q[2]+spacing;
        }
    }
}

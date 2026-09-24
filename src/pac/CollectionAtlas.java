package pac;

import java.io.DataInputStream;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Full-build animation loader for collection-only Gen 4-9 Pokemon. */
public final class CollectionAtlas {
    private static final int TRANS_NONE=0,TRANS_ROT270=6;
    private static final CollectionAtlas[] CACHE=new CollectionAtlas[CollectionDex.COUNT];
    private static final boolean[] TRIED=new boolean[CollectionDex.COUNT];
    private Image sheet;
    private short[][][] frames=new short[6][8][];
    private byte[][][] rotated=new byte[6][8][];

    private CollectionAtlas(int index)throws Exception{
        int dex=CollectionDex.DEX[index];
        sheet=Image.createImage("/dexraw/"+dex+".png");
        InputStream input=getClass().getResourceAsStream("/dexraw/"+dex+".dat");
        if(input==null)throw new Exception("missing collection atlas");
        DataInputStream in=new DataInputStream(input);
        if(in.readInt()!=0x50414352)throw new Exception("bad collection atlas");
        int actions=in.readUnsignedByte();
        for(int a=0;a<actions;a++)for(int d=0;d<8;d++){
            int count=in.readUnsignedShort();short[] values=new short[count*8];byte[] turns=new byte[count];
            for(int i=0;i<count;i++){int p=i*8;for(int n=0;n<8;n++)values[p+n]=(short)in.readUnsignedShort();turns[i]=in.readByte();}
            frames[a][d]=values;rotated[a][d]=turns;
        }
        in.close();
    }

    private static CollectionAtlas get(int index){
        if(index<0||index>=CACHE.length)return null;
        if(!TRIED[index]){TRIED[index]=true;try{CACHE[index]=new CollectionAtlas(index);}catch(Exception e){CACHE[index]=null;}}
        return CACHE[index];
    }

    public static boolean draw(Graphics g,int index,int x,int y,int boxW,int boxH,int action,int direction,int clock){
        CollectionAtlas atlas=get(index);if(atlas==null)return false;
        if(action<0||action>=6)action=0;direction&=7;
        short[] clip=atlas.frames[action][direction];if(clip==null||clip.length==0)return false;
        int count=clip.length/8,frame=(clock/2)%count,p=frame*8;
        int sx=clip[p]&65535,sy=clip[p+1]&65535,sw=clip[p+2]&65535,sh=clip[p+3]&65535;
        int ox=clip[p+4]&65535,oy=clip[p+5]&65535,sourceW=clip[p+6]&65535,sourceH=clip[p+7]&65535;
        int transform=atlas.rotated[action][direction][frame]!=0?TRANS_ROT270:TRANS_NONE;
        int dx=x+(boxW-sourceW)/2+ox,dy=y+boxH-sourceH+oy;
        g.drawRegion(atlas.sheet,sx,sy,sw,sh,transform,dx,dy,Graphics.TOP|Graphics.LEFT);
        return true;
    }
}

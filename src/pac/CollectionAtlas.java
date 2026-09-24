package pac;

import java.io.DataInputStream;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Full-build animation loader for collection-only Gen 4-9 Pokemon. */
public final class CollectionAtlas {
    private static final int TRANS_NONE=0,TRANS_ROT270=6;
    private static final CollectionAtlas[] CACHE=new CollectionAtlas[CollectionDex.COUNT];
    private static final boolean[] FAILED=new boolean[CollectionDex.COUNT];
    private static final int[] AGE=new int[CollectionDex.COUNT];
    private static final int MAX_CACHE=6;
    private static int clock;
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
        if(CACHE[index]!=null){AGE[index]=++clock;return CACHE[index];}
        if(FAILED[index])return null;
        int count=0,old=-1,oldAge=Integer.MAX_VALUE;
        for(int i=0;i<CACHE.length;i++)if(CACHE[i]!=null){
            count++;if(AGE[i]<oldAge){oldAge=AGE[i];old=i;}
        }
        if(count>=MAX_CACHE&&old>=0){CACHE[old]=null;AGE[old]=0;}
        try{CACHE[index]=new CollectionAtlas(index);AGE[index]=++clock;}
        catch(Exception e){FAILED[index]=true;CACHE[index]=null;}
        return CACHE[index];
    }

    public static boolean draw(Graphics g,int index,int x,int y,int boxW,int boxH,int action,int direction,int clock){
        CollectionAtlas atlas=get(index);if(atlas==null)return false;
        if(action<0||action>=6)action=0;direction&=7;
        int requested=action;
        if(action==0){
            short[] idle=atlas.frames[0][direction],walk=atlas.frames[1][direction];
            if((idle==null||idle.length<=8)&&walk!=null&&walk.length>8)action=1;
        }
        short[] clip=atlas.frames[action][direction];if(clip==null||clip.length==0)return false;
        int count=clip.length/8,frame=(clock/(requested==0&&action==1?3:2))%count,p=frame*8;
        int sx=clip[p]&65535,sy=clip[p+1]&65535,sw=clip[p+2]&65535,sh=clip[p+3]&65535;
        int ox=clip[p+4]&65535,oy=clip[p+5]&65535,sourceW=clip[p+6]&65535,sourceH=clip[p+7]&65535;
        int transform=atlas.rotated[action][direction][frame]!=0?TRANS_ROT270:TRANS_NONE;
        int dx=x+(boxW-sourceW)/2+ox,dy=y+boxH-sourceH+oy;
        g.drawRegion(atlas.sheet,sx,sy,sw,sh,transform,dx,dy,Graphics.TOP|Graphics.LEFT);
        return true;
    }
}

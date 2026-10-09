package pac;

import java.io.DataInputStream;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Direct renderer for selected original TexturePacker sheets (v1.0.9a demo). */
public final class RawAtlas {
    // MIDP 2.0 Sprite transform constants, kept local so desktop smoke-test
    // stubs do not need the optional lcdui.game.Sprite class.
    private static final int TRANS_NONE=0, TRANS_ROT270=6;
    public static final int IDLE=0, WALK=1, ATTACK=2, VICTORY=3, HURT=4, POSE=5;
    private static final int RAW_COUNT=Data.N;
    private static final RawAtlas[] CACHE=new RawAtlas[RAW_COUNT];
    private static final RawAtlas[] SHINY_CACHE=new RawAtlas[RAW_COUNT];
    private static final boolean[] FAILED=new boolean[RAW_COUNT];
    private static final boolean[] SHINY_FAILED=new boolean[RAW_COUNT];
    private static final int[] AGE=new int[RAW_COUNT];
    /** Smooth profile: enough decoded atlases for a full formation without churn. */
    private static final int MAX_CACHE=24;
    private static int clock;
    private static int outlineSp=-1,outlineDir=-1,outlineCount=0;
    private static Image[] outlineFrames;
    private static final Image[] fixedOutline=new Image[RAW_COUNT];
    private static final int[] fixedOutlineColor=new int[RAW_COUNT];
    private Image sheet;
    /** action/direction -> packed x,y,w,h,offsetX,offsetY,sourceW,sourceH */
    private short[][][] frames=new short[6][8][];
    private byte[][][] rotated=new byte[6][8][];

    private RawAtlas(int sp,boolean shiny,Image sharedSheet) throws Exception {
        sheet=sharedSheet!=null?sharedSheet:Image.createImage("/raw/"+sp+".png");
        InputStream input=getClass().getResourceAsStream((shiny?"/shinyraw/":"/raw/")+sp+".dat");
        if(input==null)throw new Exception("missing raw atlas index");
        DataInputStream in=new DataInputStream(input);
        if(in.readInt()!=0x50414352)throw new Exception("bad raw atlas index");
        int actions=in.readUnsignedByte();
        for(int a=0;a<actions;a++)for(int d=0;d<8;d++){
            int count=in.readUnsignedShort();
            short[] values=new short[count*8]; byte[] turns=new byte[count];
            for(int i=0;i<count;i++){
                int p=i*8;
                for(int n=0;n<8;n++)values[p+n]=(short)in.readUnsignedShort();
                turns[i]=in.readByte();
            }
            frames[a][d]=values; rotated[a][d]=turns;
        }
        in.close();
    }

    private static RawAtlas get(int sp){return get(sp,false);}
    private static RawAtlas get(int sp,boolean shiny){
        if(sp<0||sp>=RAW_COUNT)return null;
        RawAtlas[] cache=shiny?SHINY_CACHE:CACHE;boolean[] failed=shiny?SHINY_FAILED:FAILED;
        if(cache[sp]!=null){AGE[sp]=++clock;return cache[sp];}
        // Lite intentionally omits /raw. Remember that miss so every paint does
        // not throw another exception and reopen the same absent resource.
        if(failed[sp])return null;
        int count=0,old=-1,oldAge=Integer.MAX_VALUE;boolean oldShiny=false;
        for(int i=0;i<RAW_COUNT;i++){
            if(CACHE[i]!=null){count++;if(AGE[i]<oldAge){oldAge=AGE[i];old=i;oldShiny=false;}}
            if(SHINY_CACHE[i]!=null){count++;if(AGE[i]<oldAge){oldAge=AGE[i];old=i;oldShiny=true;}}
        }
        if(count>=MAX_CACHE&&old>=0){if(oldShiny)SHINY_CACHE[old]=null;else CACHE[old]=null;AGE[old]=0;}
        try{Image shared=null;if(shiny){RawAtlas normal=get(sp,false);if(normal!=null)shared=normal.sheet;}cache[sp]=new RawAtlas(sp,shiny,shared);AGE[sp]=++clock;}
        catch(Exception e){failed[sp]=true;return null;}
        return cache[sp];
    }

    public static boolean hasShiny(int sp){return get(sp,true)!=null;}

    /** Draw one untouched atlas region using the JSON source-canvas anchor. */
    public static boolean draw(Graphics g,int sp,int x,int y,int boxW,int boxH,
                               int action,int direction,int clock){
        return draw(g,sp,x,y,boxW,boxH,action,direction,clock,false);
    }
    public static boolean draw(Graphics g,int sp,int x,int y,int boxW,int boxH,
                               int action,int direction,int clock,boolean shiny){
        RawAtlas atlas=get(sp,shiny); if(atlas==null&&shiny)atlas=get(sp,false);if(atlas==null)return false;
        if(action<0||action>=6)action=IDLE;
        direction&=7;
        short[] clip=atlas.frames[action][direction];
        if(clip==null||clip.length==0)return false;
        int count=clip.length/8;
        int frame=(clock/2)%count;
        int p=frame*8;
        int sx=clip[p]&65535,sy=clip[p+1]&65535;
        int sw=clip[p+2]&65535,sh=clip[p+3]&65535;
        int ox=clip[p+4]&65535,oy=clip[p+5]&65535;
        int sourceW=clip[p+6]&65535,sourceH=clip[p+7]&65535;
        int transform=atlas.rotated[action][direction][frame]!=0?TRANS_ROT270:TRANS_NONE;
        // sourceSize is the stable logical canvas. spriteSourceSize restores the
        // exact original offset; no cropped frame is ever written to another PNG.
        int dx=x+(boxW-sourceW)/2+ox;
        int dy=y+boxH-sourceH+oy;
        g.drawRegion(atlas.sheet,sx,sy,sw,sh,transform,dx,dy,Graphics.TOP|Graphics.LEFT);
        return true;
    }

    /** Returns the exact visible frame rectangle produced by draw(). */
    public static boolean bounds(int sp,int x,int y,int boxW,int boxH,
                                 int action,int direction,int clock,int[] out){
        return bounds(sp,x,y,boxW,boxH,action,direction,clock,out,false);
    }
    public static boolean bounds(int sp,int x,int y,int boxW,int boxH,
                                 int action,int direction,int clock,int[] out,boolean shiny){
        RawAtlas atlas=get(sp,shiny);if(atlas==null&&shiny)atlas=get(sp,false);if(atlas==null)return false;
        if(action<0||action>=6)action=IDLE;
        direction&=7;
        short[] clip=atlas.frames[action][direction];
        if(clip==null||clip.length==0)return false;
        int count=clip.length/8,frame=(clock/2)%count,p=frame*8;
        int sw=clip[p+2]&65535,sh=clip[p+3]&65535;
        int ox=clip[p+4]&65535,oy=clip[p+5]&65535;
        int sourceW=clip[p+6]&65535,sourceH=clip[p+7]&65535;
        boolean turn=atlas.rotated[action][direction][frame]!=0;
        out[0]=x+(boxW-sourceW)/2+ox;
        out[1]=y+boxH-sourceH+oy;
        out[2]=turn?sh:sw;
        out[3]=turn?sw:sh;
        return true;
    }

    /** Stable formation renderer: centres visible pixels, not transparent sourceSize. */
    public static boolean drawFormation(Graphics g,int sp,int x,int y,int boxW,int boxH,
                                        int screenW,int direction,int clock){
        return drawFormation(g,sp,x,y,boxW,boxH,screenW,direction,clock,false);
    }
    public static boolean drawFormation(Graphics g,int sp,int x,int y,int boxW,int boxH,
                                        int screenW,int direction,int clock,boolean shiny){
        RawAtlas atlas=get(sp,shiny);if(atlas==null&&shiny)atlas=get(sp,false);if(atlas==null)return false;
        direction&=7;
        short[] clip=atlas.frames[IDLE][direction];
        if(clip==null||clip.length==0)return false;
        int count=clip.length/8,frame=(clock/2)%count,p=frame*8;
        int sx=clip[p]&65535,sy=clip[p+1]&65535;
        int sw=clip[p+2]&65535,sh=clip[p+3]&65535;
        boolean turn=atlas.rotated[IDLE][direction][frame]!=0;
        int visibleW=turn?sh:sw,visibleH=turn?sw:sh;
        int dx=x+(boxW-visibleW)/2;
        if(dx<0)dx=0;
        if(dx+visibleW>screenW)dx=screenW-visibleW;
        int dy=y+boxH-visibleH-2;
        g.drawRegion(atlas.sheet,sx,sy,sw,sh,turn?TRANS_ROT270:TRANS_NONE,
                     dx,dy,Graphics.TOP|Graphics.LEFT);
        return true;
    }

    /** One-pixel silhouette from the exact transformed formation frame. */
    public static boolean drawFormationOutline(Graphics g,int sp,int x,int y,int boxW,int boxH,
                                               int screenW,int direction,int clock,int color){
        RawAtlas atlas=get(sp);if(atlas==null)return false;
        direction&=7;short[] clip=atlas.frames[IDLE][direction];if(clip==null||clip.length==0)return false;
        int count=clip.length/8,frame=(clock/2)%count,p=frame*8;
        int sx=clip[p]&65535,sy=clip[p+1]&65535,sw=clip[p+2]&65535,sh=clip[p+3]&65535;
        boolean turn=atlas.rotated[IDLE][direction][frame]!=0;
        int visibleW=turn?sh:sw,visibleH=turn?sw:sh;
        int dx=x+(boxW-visibleW)/2;if(dx<0)dx=0;if(dx+visibleW>screenW)dx=screenW-visibleW;
        int dy=y+boxH-visibleH-2;
        if(clock==0&&fixedOutline[sp]!=null&&fixedOutlineColor[sp]==color){g.drawImage(fixedOutline[sp],dx-2,dy-2,Graphics.TOP|Graphics.LEFT);return true;}
        if(outlineSp!=sp||outlineDir!=direction||outlineCount!=count){
            outlineSp=sp;outlineDir=direction;outlineCount=count;outlineFrames=new Image[count];
        }
        if(outlineFrames[frame]==null)try{
            Image part=Image.createImage(atlas.sheet,sx,sy,sw,sh,turn?TRANS_ROT270:TRANS_NONE);
            int ow=visibleW+4,oh=visibleH+4,argb=0xFF000000|(color&0xFFFFFF);
            int[] src=new int[visibleW*visibleH],out=new int[ow*oh];
            part.getRGB(src,0,visibleW,0,0,visibleW,visibleH);
            for(int yy=0;yy<visibleH;yy++)for(int xx=0;xx<visibleW;xx++)if((src[yy*visibleW+xx]>>>24)!=0){
                int cx=xx+2,cy=yy+2;
                for(int oy=-1;oy<=1;oy++)for(int ox=-1;ox<=1;ox++)out[(cy+oy)*ow+cx+ox]=argb;
            }
            for(int yy=0;yy<visibleH;yy++)for(int xx=0;xx<visibleW;xx++)if((src[yy*visibleW+xx]>>>24)!=0)out[(yy+2)*ow+xx+2]=0;
            outlineFrames[frame]=Image.createRGBImage(out,ow,oh,true);
        }catch(Exception e){return false;}
        if(clock==0){fixedOutline[sp]=outlineFrames[frame];fixedOutlineColor[sp]=color;}
        g.drawImage(outlineFrames[frame],dx-2,dy-2,Graphics.TOP|Graphics.LEFT);
        return true;
    }

}

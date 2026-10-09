package pac;

import java.io.DataInputStream;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Full-build animation loader for collection-only Gen 4-9 Pokemon. */
public final class CollectionAtlas {
    private static final int TRANS_NONE=0,TRANS_ROT270=6;
    private static final CollectionAtlas[] CACHE=new CollectionAtlas[CollectionDex.COUNT];
    private static final CollectionAtlas[] SHINY_CACHE=new CollectionAtlas[CollectionDex.COUNT];
    private static final boolean[] FAILED=new boolean[CollectionDex.COUNT];
    private static final boolean[] SHINY_FAILED=new boolean[CollectionDex.COUNT];
    private static final int[] AGE=new int[CollectionDex.COUNT];
    private static final int[] SHINY_AGE=new int[CollectionDex.COUNT];
    private static final byte[] HAS_SHINY=new byte[CollectionDex.COUNT];
    /** Preparation can show 9 board + 8 bench species; avoid cyclic PNG decode churn. */
    private static final int MAX_CACHE=18;
    private static int clock;
    private Image sheet;
    private short[][][] frames=new short[6][8][];
    private byte[][][] rotated=new byte[6][8][];
    private Image fitted;
    private int fittedAction=-1,fittedDirection=-1,fittedFrame=-1,fittedW=-1,fittedH=-1;
    private Image[] formationOutlineFrames;
    private int formationOutlineDirection=-1,formationOutlineCount=0,formationOutlineColor=-1;

    /** Lunala's source attack clip contains its red eclipse frames; keep the normal palette in-game. */
    private static int safeAction(int index,int action){return index>=0&&index<CollectionDex.COUNT&&CollectionDex.DEX[index]==792&&action==2?1:action;}

    private CollectionAtlas(int index)throws Exception{this(index,false,null);}
    private CollectionAtlas(int index,boolean shiny,Image sharedSheet)throws Exception{
        int dex=CollectionDex.DEX[index];
        sheet=sharedSheet!=null?sharedSheet:Image.createImage("/dexraw/"+dex+".png");
        InputStream input=getClass().getResourceAsStream((shiny?"/dexshiny/":"/dexraw/")+dex+".dat");
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

    private static CollectionAtlas get(int index){return get(index,false);}
    private static CollectionAtlas get(int index,boolean shiny){
        if(index<0||index>=CACHE.length)return null;
        CollectionAtlas[] cache=shiny?SHINY_CACHE:CACHE;boolean[] failed=shiny?SHINY_FAILED:FAILED;int[] ages=shiny?SHINY_AGE:AGE;
        if(cache[index]!=null){ages[index]=++clock;return cache[index];}
        if(failed[index])return null;
        int count=0,old=-1,oldAge=Integer.MAX_VALUE;
        boolean oldShiny=false;
        for(int i=0;i<CACHE.length;i++){if(CACHE[i]!=null){count++;if(AGE[i]<oldAge){oldAge=AGE[i];old=i;oldShiny=false;}}if(SHINY_CACHE[i]!=null){count++;if(SHINY_AGE[i]<oldAge){oldAge=SHINY_AGE[i];old=i;oldShiny=true;}}}
        if(count>=MAX_CACHE&&old>=0){if(oldShiny){SHINY_CACHE[old]=null;SHINY_AGE[old]=0;}else{CACHE[old]=null;AGE[old]=0;}}
        try{
            if(shiny){CollectionAtlas normal=get(index,false);if(normal==null)throw new Exception("missing normal atlas");cache[index]=new CollectionAtlas(index,true,normal.sheet);}
            else cache[index]=new CollectionAtlas(index);
            ages[index]=++clock;
        }
        catch(Exception e){failed[index]=true;cache[index]=null;}
        return cache[index];
    }

    public static boolean hasShiny(int index){if(index<0||index>=HAS_SHINY.length)return false;if(HAS_SHINY[index]==0){InputStream in=null;try{in=CollectionAtlas.class.getResourceAsStream("/dexshiny/"+CollectionDex.DEX[index]+".dat");HAS_SHINY[index]=(byte)(in==null?1:2);}catch(Exception e){HAS_SHINY[index]=1;}finally{try{if(in!=null)in.close();}catch(Exception e){}}}return HAS_SHINY[index]==2;}

    public static boolean draw(Graphics g,int index,int x,int y,int boxW,int boxH,int action,int direction,int clock){
        return draw(g,index,x,y,boxW,boxH,action,direction,clock,false);
    }
    public static boolean draw(Graphics g,int index,int x,int y,int boxW,int boxH,int action,int direction,int clock,boolean shiny){
        CollectionAtlas atlas=get(index,shiny);if(atlas==null&&shiny)atlas=get(index);if(atlas==null)return false;
        action=safeAction(index,action);
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

    private Image fitted(int action,int direction,int clock,int boxW,int boxH){
        if(action<0||action>=6)action=0;direction&=7;
        short[] clip=frames[action][direction];if(clip==null||clip.length==0)return null;
        int count=clip.length/8,frame=(clock/2)%count;
        if(fitted!=null&&fittedAction==action&&fittedDirection==direction&&fittedFrame==frame&&fittedW==boxW&&fittedH==boxH)return fitted;
        int p=frame*8,sx=clip[p]&65535,sy=clip[p+1]&65535,sw=clip[p+2]&65535,sh=clip[p+3]&65535;
        int ox=clip[p+4]&65535,oy=clip[p+5]&65535,sourceW=clip[p+6]&65535,sourceH=clip[p+7]&65535;
        int transform=rotated[action][direction][frame]!=0?TRANS_ROT270:TRANS_NONE;
        try{
            Image canvas=Image.createImage(sourceW,sourceH);Graphics cg=canvas.getGraphics();cg.setColor(0xFF00FF);cg.fillRect(0,0,sourceW,sourceH);
            cg.drawRegion(sheet,sx,sy,sw,sh,transform,ox,oy,Graphics.TOP|Graphics.LEFT);
            int[] src=new int[sourceW*sourceH],out=new int[boxW*boxH];canvas.getRGB(src,0,sourceW,0,0,sourceW,sourceH);
            int left=sourceW,top=sourceH,right=-1,bottom=-1;
            for(int yy=0;yy<sourceH;yy++)for(int xx=0;xx<sourceW;xx++){int c=src[yy*sourceW+xx];if((c>>>24)!=0&&(c&0xFFFFFF)!=0xFF00FF){if(xx<left)left=xx;if(xx>right)right=xx;if(yy<top)top=yy;if(yy>bottom)bottom=yy;}}
            if(right<left||bottom<top)return null;
            int visibleW=right-left+1,visibleH=bottom-top+1,targetW=Math.max(1,boxW-6),targetH=Math.max(1,boxH-4);
            int scale=Math.min(targetW*256/visibleW,targetH*256/visibleH);
            int dw=Math.max(1,visibleW*scale/256),dh=Math.max(1,visibleH*scale/256),dx=(boxW-dw)/2,dy=boxH-dh-1;
            for(int yy=0;yy<dh;yy++)for(int xx=0;xx<dw;xx++){
                int c=src[(top+yy*visibleH/dh)*sourceW+left+xx*visibleW/dw];
                if((c>>>24)!=0&&(c&0xFFFFFF)!=0xFF00FF)out[(dy+yy)*boxW+dx+xx]=c;
            }
            fitted=Image.createRGBImage(out,boxW,boxH,true);fittedAction=action;fittedDirection=direction;fittedFrame=frame;fittedW=boxW;fittedH=boxH;
            return fitted;
        }catch(Exception e){return null;}
    }

    public static boolean drawFitted(Graphics g,int index,int x,int y,int boxW,int boxH,int action,int direction,int clock){
        CollectionAtlas atlas=get(index);if(atlas==null)return false;action=safeAction(index,action);Image im=atlas.fitted(action,direction,clock,boxW,boxH);if(im==null)return false;g.drawImage(im,x,y,Graphics.TOP|Graphics.LEFT);return true;
    }

    public static boolean drawFittedOutline(Graphics g,int index,int x,int y,int boxW,int boxH,int action,int direction,int clock,int color){
        CollectionAtlas atlas=get(index);if(atlas==null)return false;Image im=atlas.fitted(action,direction,clock,boxW,boxH);if(im==null)return false;
        try{int[] src=new int[boxW*boxH],out=new int[boxW*boxH];im.getRGB(src,0,boxW,0,0,boxW,boxH);int argb=0xFF000000|(color&0xFFFFFF);
            for(int yy=0;yy<boxH;yy++)for(int xx=0;xx<boxW;xx++){int p=yy*boxW+xx;if((src[p]>>>24)!=0)continue;boolean edge=false;for(int yd=-1;yd<=1&&!edge;yd++)for(int xd=-1;xd<=1;xd++){int nx=xx+xd,ny=yy+yd;if(nx>=0&&nx<boxW&&ny>=0&&ny<boxH&&(src[ny*boxW+nx]>>>24)!=0){edge=true;break;}}if(edge)out[p]=argb;}
            g.drawImage(Image.createRGBImage(out,boxW,boxH,true),x,y,Graphics.TOP|Graphics.LEFT);return true;
        }catch(Exception e){return false;}
    }

    public static boolean bounds(int index,int x,int y,int boxW,int boxH,int action,int direction,int clock,int[] out){
        return bounds(index,x,y,boxW,boxH,action,direction,clock,out,false);
    }
    public static boolean bounds(int index,int x,int y,int boxW,int boxH,int action,int direction,int clock,int[] out,boolean shiny){
        CollectionAtlas atlas=get(index,shiny);if(atlas==null&&shiny)atlas=get(index);if(atlas==null)return false;action=safeAction(index,action);if(action<0||action>=6)action=0;direction&=7;
        short[] clip=atlas.frames[action][direction];if(clip==null||clip.length==0)return false;int frame=(clock/2)%(clip.length/8),p=frame*8;
        int sw=clip[p+2]&65535,sh=clip[p+3]&65535,ox=clip[p+4]&65535,oy=clip[p+5]&65535,sourceW=clip[p+6]&65535,sourceH=clip[p+7]&65535;
        boolean turn=atlas.rotated[action][direction][frame]!=0;out[0]=x+(boxW-sourceW)/2+ox;out[1]=y+boxH-sourceH+oy;out[2]=turn?sh:sw;out[3]=turn?sw:sh;return true;
    }

    public static boolean drawFormation(Graphics g,int index,int x,int y,int boxW,int boxH,int screenW,int direction,int clock){
        return drawFormation(g,index,x,y,boxW,boxH,screenW,direction,clock,false);
    }
    public static boolean drawFormation(Graphics g,int index,int x,int y,int boxW,int boxH,int screenW,int direction,int clock,boolean shiny){
        CollectionAtlas atlas=get(index,shiny);if(atlas==null&&shiny)atlas=get(index);if(atlas==null)return false;direction&=7;short[] clip=atlas.frames[0][direction];if(clip==null||clip.length==0)return false;
        int frame=(clock/2)%(clip.length/8),p=frame*8,sx=clip[p]&65535,sy=clip[p+1]&65535,sw=clip[p+2]&65535,sh=clip[p+3]&65535;boolean turn=atlas.rotated[0][direction][frame]!=0;
        int visibleW=turn?sh:sw,visibleH=turn?sw:sh;
        // A 3x3 footprint uses cell 8 (bottom-middle) as its occupied tile:
        // centre the visible frame horizontally and anchor its visible feet.
        int dx=x+(boxW-visibleW)/2,dy=y+boxH-visibleH-2;
        g.drawRegion(atlas.sheet,sx,sy,sw,sh,turn?TRANS_ROT270:TRANS_NONE,dx,dy,Graphics.TOP|Graphics.LEFT);return true;
    }

    public static boolean drawFormationOutline(Graphics g,int index,int x,int y,int boxW,int boxH,int screenW,int direction,int clock,int color){
        CollectionAtlas atlas=get(index);if(atlas==null)return false;direction&=7;short[] clip=atlas.frames[0][direction];if(clip==null||clip.length==0)return false;
        int frame=(clock/2)%(clip.length/8),p=frame*8,sx=clip[p]&65535,sy=clip[p+1]&65535,sw=clip[p+2]&65535,sh=clip[p+3]&65535;boolean turn=atlas.rotated[0][direction][frame]!=0;
        int visibleW=turn?sh:sw,visibleH=turn?sw:sh;
        int dx=x+(boxW-visibleW)/2,dy=y+boxH-visibleH-2;
        int count=clip.length/8;
        if(atlas.formationOutlineFrames==null||atlas.formationOutlineDirection!=direction||atlas.formationOutlineCount!=count||atlas.formationOutlineColor!=color){
            atlas.formationOutlineFrames=new Image[count];atlas.formationOutlineDirection=direction;atlas.formationOutlineCount=count;atlas.formationOutlineColor=color;
        }
        if(atlas.formationOutlineFrames[frame]==null)try{Image part=Image.createImage(atlas.sheet,sx,sy,sw,sh,turn?TRANS_ROT270:TRANS_NONE);int ow=visibleW+4,oh=visibleH+4,argb=0xFF000000|(color&0xFFFFFF);int[] src=new int[visibleW*visibleH],out=new int[ow*oh];part.getRGB(src,0,visibleW,0,0,visibleW,visibleH);
            for(int yy=0;yy<visibleH;yy++)for(int xx=0;xx<visibleW;xx++)if((src[yy*visibleW+xx]>>>24)!=0){int cx=xx+2,cy=yy+2;for(int yd=-1;yd<=1;yd++)for(int xd=-1;xd<=1;xd++)out[(cy+yd)*ow+cx+xd]=argb;}
            for(int yy=0;yy<visibleH;yy++)for(int xx=0;xx<visibleW;xx++)if((src[yy*visibleW+xx]>>>24)!=0)out[(yy+2)*ow+xx+2]=0;
            atlas.formationOutlineFrames[frame]=Image.createRGBImage(out,ow,oh,true);
        }catch(Exception e){return false;}
        g.drawImage(atlas.formationOutlineFrames[frame],dx-2,dy-2,Graphics.TOP|Graphics.LEFT);return true;
    }
}

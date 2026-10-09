package pac;
import javax.microedition.lcdui.Graphics;

/** Isolated generation laboratory; a chosen base species locks the shop. */
public final class GenerationTestScreen extends Screen{
    private int generation=1,cursor=0,firstRow=0;
    private boolean choosingPokemon=false;
    public GenerationTestScreen(Game game){super(game);}
    public void update(int dt){}
    private int count(){int n=0;for(int sp=0;sp<Data.N;sp++)if(candidate(sp))n++;return n;}
    private boolean candidate(int sp){return Data.generation(sp)==generation&&Data.isBase(sp)&&Data.nationalDex(sp)!=1009;}
    private int speciesAt(int rank){for(int sp=0;sp<Data.N;sp++)if(candidate(sp)&&rank--==0)return sp;return-1;}
    private int cols(){return Math.max(1,(game.W-12)/34);}
    private int top(){return Art.fh+8;}
    private int detailY(){return game.H-Math.max(Art.fh*5+23,80);}
    private int visibleRows(){return Math.max(1,(detailY()-top())/34);}
    public void key(int key){
        if(!choosingPokemon){if(key==Game.K_LEFT||key==Game.K_UP)generation=generation==1?9:generation-1;else if(key==Game.K_RIGHT||key==Game.K_DOWN)generation=generation==9?1:generation+1;else if(key==Game.K_FIRE||key==Game.K_SOFT1){choosingPokemon=true;cursor=firstRow=0;}else if(key==Game.K_0||key==Game.K_SOFT2)game.setScreen(new ChessModeScreen(game));return;}
        int n=count(),c=cols();if(n==0)return;
        if(key==Game.K_LEFT)cursor=Math.max(0,cursor-1);else if(key==Game.K_RIGHT)cursor=Math.min(n-1,cursor+1);else if(key==Game.K_UP)cursor=Math.max(0,cursor-c);else if(key==Game.K_DOWN)cursor=Math.min(n-1,cursor+c);else if(key==Game.K_0||key==Game.K_SOFT2)choosingPokemon=false;else if(key==Game.K_FIRE||key==Game.K_SOFT1)game.setScreen(new ChessScreen(game,generation,speciesAt(cursor)));
    }
    public boolean pointer(int x,int y){if(!choosingPokemon){int gap=Art.fh+8,visible=Math.min(9,Math.max(1,(game.H-Art.fh*7)/gap)),start=Math.max(0,Math.min(9-visible,generation-1-visible/2)),rank=(y-Art.fh*3+3)/gap;if(y>=Art.fh*3-3&&rank>=0&&rank<visible){int next=start+rank+1;if(next==generation){choosingPokemon=true;cursor=firstRow=0;}else generation=next;return true;}return false;}if(y>=top()&&y<detailY()){int col=(x-6)/34,row=(y-top())/34,rank=(firstRow+row)*cols()+col;if(x>=6&&col<cols()&&rank<count()){cursor=rank;return true;}}return false;}
    private void paintGenerations(Graphics g){int W=game.W,H=game.H,fh=Art.fh,gap=fh+8,visible=Math.min(9,Math.max(1,(H-fh*7)/gap)),start=Math.max(0,Math.min(9-visible,generation-1-visible/2));for(int i=0;i<visible;i++){int gen=start+i+1,y=fh*3+i*gap;if(gen==generation)Art.box(g,W/8,y-3,W*3/4,fh+6,0x305090,0xFFD030);Art.textC(g,Lang.t("THẾ HỆ ","GENERATION ")+gen,W/2,y,0xFFFFFF);}Art.para(g,Lang.t("Shop chỉ roll Pokémon đã chọn. Vàng vô hạn, cấp 9, có sẵn đồ biến đổi.","Shop rolls only the selected Pokemon. Infinite gold, level 9, transformation items."),8,H-fh*4-5,W-16,0x70D8FF,3);Art.textSmallC(g,Lang.t("2/8: chọn   5: tiếp   0: về","Up/down: select   5: next   0: back"),W/2,H-fh-3,0x8090B0);}
    private void paintForms(Graphics g,int sp,int x,int y,int width){int ox=g.getClipX(),oy=g.getClipY(),ow=g.getClipWidth(),oh=g.getClipHeight();g.clipRect(x-1,y-1,width+2,35);int n=0,last=sp;boolean mini=width<168;int size=mini?20:32,cell=size+2;for(int p=sp,depth=0;p>=0&&depth<3;p=EvolutionBranchData.defaultNext(p),depth++){int px=x+n*cell+(cell-size)/2;if(mini)Art.avatarMini(g,p,px,y);else Art.avatar(g,p,px,y);if(p==sp){g.setColor(0xFFFFFF);g.drawRect(px-1,y-1,size+1,size+1);}n++;last=p;}int alternate=EvolutionBranchData.alternate(last);if(n<5&&alternate>=0){int px=x+n++*cell+(cell-size)/2;PetAvatar.draw(g,alternate,0,false,false,px,y,size);}if(n<5&&MegaData.available(last)){int px=x+n++*cell+(cell-size)/2;if(mini)Art.avatarMiniMega(g,last,px,y,false);else Art.avatarMega(g,last,px,y,false);}int form=EvolutionVariantData.formFor(last);if(form<=0&&SpecialFormData.memoryAvailable(last))form=SpecialFormData.memoryForm(last);if(n<5&&form>0){SpecialFormAtlas.avatar(g,form,x+n*cell+(cell-size)/2,y,mini,false);}g.setClip(ox,oy,ow,oh);}
    public void paint(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh;g.setColor(0x101827);g.fillRect(0,0,W,H);if(!choosingPokemon)Art.textBC(g,Lang.t("PHÒNG KIỂM THỬ","AUTO CHESS LAB"),W/2,5,0xFFD030);
        if(!choosingPokemon){paintGenerations(g);return;}
        int n=count(),c=cols(),rows=visibleRows(),row=cursor/c;if(row<firstRow)firstRow=row;if(row>=firstRow+rows)firstRow=row-rows+1;
        String title=Lang.t("PHÒNG KIỂM THỬ","AUTO CHESS LAB"),genText="  Gen "+generation+"  "+(cursor+1)+"/"+n;int titleW=Art.smallWidth(title),headerX=(W-titleW-Art.smallWidth(genText))/2;Art.textSmall(g,title,headerX,5,0xFFD030);Art.textSmall(g,genText,headerX+titleW,5,0x70D8FF);
        int oldX=g.getClipX(),oldY=g.getClipY(),oldW=g.getClipWidth(),oldH=g.getClipHeight();g.setClip(0,top(),W,Math.max(0,detailY()-top()));
        for(int i=0;i<c*rows;i++){int rank=firstRow*c+i;if(rank>=n)break;int sp=speciesAt(rank),x=6+(i%c)*34,y=top()+(i/c)*34;Art.avatar(g,sp,x,y);if(rank==cursor){g.setColor(0xFFE060);g.drawRect(x,y,32,32);}}
        g.setClip(oldX,oldY,oldW,oldH);int sp=speciesAt(cursor),dy=detailY();Art.box(g,5,dy,W-10,H-dy-fh-5,0x18263C,0x507DA3);
        if(sp>=0){int split=W>=240?W/2-18:W-11;Art.textSmall(g,Data.name[sp]+" #"+Data.nationalDex(sp),11,dy+4,0xFFFFFF);Art.typeIcon(g,Data.t1[sp],11,dy+fh+6);if(Data.t2[sp]>=0)Art.typeIcon(g,Data.t2[sp],29,dy+fh+6);Art.textSmall(g,Lang.t("Máu: ","HP: ")+Data.hp[sp]+Lang.t(" Công: "," ATK: ")+Data.atk[sp],50,dy+fh+7,0xB0D0FF);Art.textSmall(g,Lang.t("Thủ: ","DEF: ")+Data.def[sp]+Lang.t(" Kháng: "," RES: ")+Data.speDef[sp],11,dy+fh*2+12,0xB0D0FF);Art.textSmall(g,Lang.t("Tốc: ","SPD: ")+Data.speed[sp]+Lang.t(" Tầm: "," RNG: ")+Data.range[sp],11,dy+fh*3+14,0xB0D0FF);if(W>=240){Art.textSmall(g,Lang.t("Các dạng","Forms"),split,dy+4,0x70D8FF);paintForms(g,sp,split,dy+fh+5,W-split-10);}else paintForms(g,sp,11,dy+fh*2+9,W-22);}
        Art.textSmallC(g,Lang.t("2/4/6/8: chọn   5: bắt đầu   0: về","Arrows: choose   5: start   0: back"),W/2,H-fh-2,0x8090B0);
    }
}

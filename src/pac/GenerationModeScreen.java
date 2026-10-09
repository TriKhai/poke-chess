package pac;

import javax.microedition.lcdui.Graphics;

/** Sequential generation challenges plus fixed-memory Endless Survival. */
public final class GenerationModeScreen extends Screen{
    private int selected;
    private final boolean hasResume;
    private final int savedRound;
    private boolean replaceAsk;
    private int replaceChoice,pendingMode;
    private String msg="";
    private int msgT;

    public GenerationModeScreen(Game game){super(game);Run r=RunStorage.load();hasResume=r!=null;savedRound=hasResume?r.round:0;}
    public void update(int dt){if(msgT>0)msgT-=dt;}
    public void key(int key){
        if(replaceAsk){
            if(key==Game.K_LEFT||key==Game.K_UP)replaceChoice=0;else if(key==Game.K_RIGHT||key==Game.K_DOWN)replaceChoice=1;
            else if(key==Game.K_0||key==Game.K_SOFT2)replaceAsk=false;
            else if(key==Game.K_FIRE||key==Game.K_SOFT1){if(replaceChoice==0)replaceAsk=false;else{RunStorage.clear();game.setScreen(new ChessScreen(game,pendingMode));}}return;
        }
        int count=Data.BATTLE_MAX_GEN+1;
        if(key==Game.K_UP||key==Game.K_LEFT)selected=(selected+count-1)%count;else if(key==Game.K_DOWN||key==Game.K_RIGHT)selected=(selected+1)%count;
        else if(key==Game.K_0||key==Game.K_SOFT2)game.setScreen(new ChessModeScreen(game));
        else if(key==Game.K_FIRE||key==Game.K_SOFT1){int gen=selected+1;if(selected==Data.BATTLE_MAX_GEN&&!ProgressionRules.endlessUnlocked()){msg=Lang.t("Hãy thắng Thế hệ 9 trước.","Clear Generation 9 first.");msgT=1800;return;}if(selected<Data.BATTLE_MAX_GEN&&!ProgressionRules.generationUnlocked(gen)){msg=Lang.t("Hãy thắng Thế hệ "+(gen-1)+" trước.","Clear Generation "+(gen-1)+" first.");msgT=1800;return;}pendingMode=selected==Data.BATTLE_MAX_GEN?Run.MODE_ENDLESS:modeForGen(gen);if(hasResume){replaceAsk=true;replaceChoice=0;}else game.setScreen(new ChessScreen(game,pendingMode));}
    }
    public void paint(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh;g.setColor(0x101827);g.fillRect(0,0,W,H);Art.textBC(g,Lang.t("THẾ HỆ POKÉMON","POKEMON GENERATIONS"),W/2,12,0xFFD030);
        int total=Data.BATTLE_MAX_GEN+1,gap=fh+8,visible=Math.max(3,(H-fh*9)/gap);if(visible>total)visible=total;int top=selected-visible/2;if(top<0)top=0;if(top+visible>total)top=total-visible;int y=fh*3;
        for(int i=top;i<top+visible;i++){boolean endless=i==Data.BATTLE_MAX_GEN;int gen=i+1,yy=y+(i-top)*gap;boolean open=endless?ProgressionRules.endlessUnlocked():ProgressionRules.generationUnlocked(gen);if(i==selected){g.setColor(0x305090);g.fillRect(W/8,yy-3,W*3/4,fh+6);g.setColor(open?0xFFD030:0x707888);g.drawRect(W/8,yy-3,W*3/4-1,fh+5);}String s=endless?Lang.t("SINH TỒN VÔ TẬN","ENDLESS SURVIVAL")+(open?"":Lang.t("  [KHÓA]","  [LOCKED]")):Lang.t("THẾ HỆ ","GENERATION ")+gen+(open?(ProgressionRules.cleared(modeForGen(gen))?Lang.t("  [ĐÃ THẮNG]","  [CLEARED]"):""):Lang.t("  [KHÓA]","  [LOCKED]"));Art.textC(g,s,W/2,yy,open?0xFFFFFF:0x68758A);}
        String note=selected==Data.BATTLE_MAX_GEN?ProgressionRules.objective(Run.MODE_ENDLESS):(ProgressionRules.generationUnlocked(selected+1)?ProgressionRules.objective(modeForGen(selected+1)):Lang.t("Thắng Thế hệ "+selected+" để mở khóa.","Clear Generation "+selected+" to unlock."));
        Art.para(g,note,8,H-fh*5,W-16,0xAFC0D8,3);if(msgT>0)Art.textC(g,msg,W/2,H-fh*2-3,0xFF8060);else Art.textC(g,Lang.t("FIRE: chọn   0: về","FIRE: select   0: back"),W/2,H-fh-3,0x8090B0);if(replaceAsk)paintAsk(g);
    }
    private int modeForGen(int gen){return ProgressionRules.modeForGeneration(gen);}
    private void paintAsk(Graphics g){int W=game.W,H=game.H,fh=Art.fh,w=Math.min(W-16,210),h=fh*6+16,x=(W-w)/2,y=(H-h)/2;Art.box(g,x,y,w,h,0x101830,0xFF7050);Art.textBC(g,Lang.t("XÓA SAVE ĐANG CHƠI?","REPLACE CURRENT SAVE?"),W/2,y+4,0xFF8060);Art.para(g,Lang.t("Chơi mới sẽ xóa save vòng ","New run deletes round ")+savedRound+".",x+8,y+fh+8,w-16,0xD8E0F0,3);String[] a={Lang.t("HỦY","CANCEL"),Lang.t("XÓA & CHƠI","REPLACE")};int by=y+h-fh*2-7,bw=(w-18)/2;for(int i=0;i<2;i++){int bx=x+6+i*(bw+6);g.setColor(i==replaceChoice?0x405273:0x263348);g.fillRect(bx,by,bw,fh+6);if(i==replaceChoice){g.setColor(0xFFE060);g.drawRect(bx,by,bw-1,fh+5);}Art.textSmallC(g,a[i],bx+bw/2,by+3,0xFFFFFF);}}
}

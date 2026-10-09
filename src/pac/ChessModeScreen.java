package pac;

import javax.microedition.lcdui.Graphics;

/** Small run-mode chooser shown before creating an Auto Chess run. */
public final class ChessModeScreen extends Screen {
    private int selected;
    private final boolean hasResume;
    private final int savedRound;
    private boolean replaceAsk;
    private int replaceChoice,pendingMode;

    public ChessModeScreen(Game game){super(game);Run r=RunStorage.load();hasResume=r!=null;savedRound=hasResume?r.round:0;}

    public void update(int dt){}

    private int optionCount(){return (Save.cheatMode?7:5)+(hasResume?1:0)+1;}
    private int optionGap(){return UiLayout.compact(game.W,game.H)?Art.fh+4:Art.fh+10;}
    private int optionTop(){return Math.max(Art.fh*2+2,game.H/2-optionCount()*optionGap()/2);}

    public boolean pointer(int px,int py){
        int W=game.W,H=game.H,fh=Art.fh;
        if(replaceAsk){
            int w=Math.min(W-16,210),h=fh*6+16,x=(W-w)/2,y=(H-h)/2,by=y+h-fh*2-7,bw=(w-18)/2;
            for(int i=0;i<2;i++){int bx=x+6+i*(bw+6);if(px>=bx&&px<bx+bw&&py>=by&&py<by+fh+6){replaceChoice=i;if(confirmTouch(100+i))key(Game.K_FIRE);return true;}}
            return true;
        }
        int pick=TouchLayout.rowAt(px,py,W/8,optionTop()-3,W*3/4,optionGap(),fh+6,optionCount());
        if(pick>=0){selected=pick;if(confirmTouch(pick))key(Game.K_FIRE);return true;}
        if(py>=H-fh-6&&py<H&&px>=0&&px<W){key(px<W/2?Game.K_FIRE:Game.K_0);return true;}
        resetTouchChoice();
        return true;
    }

    public void key(int key){
        resetTouchChoice();
        if(replaceAsk){
            if(key==Game.K_LEFT||key==Game.K_UP)replaceChoice=0;
            else if(key==Game.K_RIGHT||key==Game.K_DOWN)replaceChoice=1;
            else if(key==Game.K_0||key==Game.K_SOFT2)replaceAsk=false;
            else if(key==Game.K_FIRE||key==Game.K_SOFT1){if(replaceChoice==0)replaceAsk=false;else{RunStorage.clear();game.setScreen(new ChessScreen(game,pendingMode));}}
            return;
        }
        int modes=Save.cheatMode?7:5,n=modes+(hasResume?1:0)+1;
        if(key==Game.K_UP||key==Game.K_LEFT)selected=(selected+n-1)%n;
        else if(key==Game.K_DOWN||key==Game.K_RIGHT)selected=(selected+1)%n;
        else if(key==Game.K_FIRE||key==Game.K_SOFT1){
            if(selected<modes){int picked=modeAt(selected);if(picked==-1){game.setScreen(new GenerationModeScreen(game));return;}if(picked==-3){game.setScreen(new ExtraChessModeScreen(game));return;}if(picked==-2){game.setScreen(new GenerationTestScreen(game));return;}pendingMode=picked;if(hasResume){replaceAsk=true;replaceChoice=0;}else game.setScreen(new ChessScreen(game,pendingMode));}
            else if(hasResume&&selected==modes){Run r=RunStorage.load();game.setScreen(r==null?new ChessScreen(game,Run.MODE_NORMAL):new ChessScreen(game,r));}
            else game.setScreen(new HistoryScreen(game));
        }
        else if(key==Game.K_SOFT2||key==Game.K_0)game.setScreen(new MenuScreen(game));
    }

    private int modeAt(int index){
        if(Save.cheatMode){int[] m={Run.MODE_NORMAL,Run.MODE_LEGEND,Run.MODE_UNLIMITED,Run.MODE_THIRTY,-1,-2,-3};return m[index];}
        int[] m={Run.MODE_NORMAL,Run.MODE_LEGEND,Run.MODE_THIRTY,-1,-3};return m[index];
    }

    public void paint(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh;
        g.setColor(0x101827);g.fillRect(0,0,W,H);
        Art.textBC(g,Lang.t("CHỌN CHẾ ĐỘ", "AUTO CHESS MODE"),W/2,12,0xFFD030);
        String[] base=Save.cheatMode?
            new String[]{Lang.t("CHƠI THƯỜNG","NORMAL RUN"),Lang.t("THẦN THÚ ĐẠI CHIẾN","LEGENDARY WAR"),Lang.t("VÀNG VÔ HẠN","UNLIMITED GOLD"),Lang.t("ĐẠI CHIẾN TRANG BỊ","ITEM WAR"),Lang.t("THẾ HỆ POKÉMON","POKEMON GENERATIONS"),Lang.t("KIỂM THỬ THẾ HỆ","GENERATION LAB"),Lang.t("CỜ THỬ THÁCH","CHESS CHALLENGES")}:
            new String[]{Lang.t("CHƠI THƯỜNG","NORMAL RUN"),Lang.t("THẦN THÚ ĐẠI CHIẾN","LEGENDARY WAR"),Lang.t("ĐẠI CHIẾN TRANG BỊ","ITEM WAR"),Lang.t("THẾ HỆ POKÉMON","POKEMON GENERATIONS"),Lang.t("CỜ THỬ THÁCH","CHESS CHALLENGES")};
        int extra=(hasResume?1:0)+1;String[] mode=new String[base.length+extra];for(int i=0;i<base.length;i++)mode[i]=base[i];int q=base.length;if(hasResume)mode[q++]=Lang.t("TIẾP TỤC VÒNG ","RESUME ROUND ")+savedRound;mode[q]=Lang.t("LỊCH SỬ ĐẤU","BATTLE HISTORY");
        boolean compact=UiLayout.compact(W,H);int gap=optionGap();
        int y=optionTop();
        for(int i=0;i<mode.length;i++){
            if(i==selected){
                g.setColor(0x305090);g.fillRect(W/8,y+i*gap-3,W*3/4,fh+6);
                g.setColor(0xFFD030);g.drawRect(W/8,y+i*gap-3,W*3/4-1,fh+5);
            }
            Art.textC(g,mode[i],W/2,y+i*gap,i==selected?0xFFFFFF:0xAFC0D8);
        }
        int modes=base.length;String note;if(selected>=modes)note=hasResume&&selected==modes?Lang.t("Khôi phục đội hình, shop, đồ và kinh tế","Restore team, shop, items and economy"):Lang.t("Xem kết quả và 9 Pokemon cuối","View results and final 9 Pokemon");else if(modeAt(selected)==-3)note=Lang.t("Tháp, Đơn hệ, Đội ngẫu nhiên và Boss Rush.","Tower, Monotype, Random Team and Boss Rush.");else if(modeAt(selected)==-1)note=Lang.t("Chọn Gen 1-"+Data.BATTLE_MAX_GEN+" hoặc Sinh Tồn Vô Tận.","Choose Gen 1-"+Data.BATTLE_MAX_GEN+" or Endless Survival.");else if(modeAt(selected)==-2)note=Lang.t("Chọn Gen và Pokémon, không ghi save hay thành tích.","Choose a Gen and Pokemon without saving progress.");else note=ProgressionRules.objective(modeAt(selected));
        if(!compact)Art.para(g,note,8,H-fh*4,W-16,0x70D8FF,2);
        Art.textC(g,Lang.t("FIRE: bắt đầu   0: về","FIRE: start   0: back"),W/2,H-fh-3,0x8090B0);
        if(replaceAsk)paintReplaceAsk(g);
    }

    private void paintReplaceAsk(Graphics g){int W=game.W,H=game.H,fh=Art.fh,w=Math.min(W-16,210),h=fh*6+16,x=(W-w)/2,y=(H-h)/2;Art.box(g,x,y,w,h,0x101830,0xFF7050);Art.textBC(g,Lang.t("XÓA SAVE ĐANG CHƠI?","REPLACE CURRENT SAVE?"),W/2,y+4,0xFF8060);Art.para(g,Lang.t("Bắt đầu run mới sẽ xóa save vòng ","Starting a new run deletes round ")+savedRound+Lang.t(". Bạn vẫn có thể Hủy để tiếp tục save cũ.",". Cancel to keep the old save."),x+8,y+fh+8,w-16,0xD8E0F0,3);String[] a={Lang.t("HỦY","CANCEL"),Lang.t("XÓA & CHƠI MỚI","REPLACE")};int by=y+h-fh*2-7,bw=(w-18)/2;for(int i=0;i<2;i++){int bx=x+6+i*(bw+6);g.setColor(i==replaceChoice?0x405273:0x263348);g.fillRect(bx,by,bw,fh+6);if(i==replaceChoice){g.setColor(0xFFE060);g.drawRect(bx,by,bw-1,fh+5);}Art.textSmallC(g,a[i],bx+bw/2,by+3,0xFFFFFF);}}

}

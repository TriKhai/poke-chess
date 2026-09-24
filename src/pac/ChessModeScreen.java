package pac;

import javax.microedition.lcdui.Graphics;

/** Small run-mode chooser shown before creating an Auto Chess run. */
public final class ChessModeScreen extends Screen {
    private int selected;
    private final boolean hasResume;
    private final int savedRound;

    public ChessModeScreen(Game game){super(game);hasResume=RunStorage.has();Run r=hasResume?RunStorage.load():null;savedRound=r==null?0:r.round;}

    public void update(int dt){}

    public void key(int key){
        int modes=Save.cheatMode?4:3,n=modes+(hasResume?1:0)+1;
        if(key==Game.K_UP||key==Game.K_LEFT)selected=(selected+n-1)%n;
        else if(key==Game.K_DOWN||key==Game.K_RIGHT)selected=(selected+1)%n;
        else if(key==Game.K_FIRE||key==Game.K_SOFT1){
            if(selected<modes){RunStorage.clear();game.setScreen(new ChessScreen(game,modeAt(selected)));}
            else if(hasResume&&selected==modes){Run r=RunStorage.load();game.setScreen(r==null?new ChessScreen(game,Run.MODE_NORMAL):new ChessScreen(game,r));}
            else game.setScreen(new HistoryScreen(game));
        }
        else if(key==Game.K_SOFT2||key==Game.K_0)game.setScreen(new MenuScreen(game));
    }

    private int modeAt(int index){
        if(Save.cheatMode)return index;
        return index==0?Run.MODE_NORMAL:(index==1?Run.MODE_THIRTY:Run.MODE_GEN1);
    }

    public void paint(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh;
        g.setColor(0x101827);g.fillRect(0,0,W,H);
        Art.textBC(g,Lang.t("CHỌN CHẾ ĐỘ", "AUTO CHESS MODE"),W/2,12,0xFFD030);
        String[] base=Save.cheatMode?
            new String[]{Lang.t("CHƠI THƯỜNG","NORMAL RUN"),Lang.t("VÀNG VÔ HẠN","UNLIMITED GOLD"),Lang.t("30 VÒNG - 9 MẢNH","30 ROUNDS - 9 ITEMS"),Lang.t("THẾ HỆ 1","GENERATION 1")}:
            new String[]{Lang.t("CHƠI THƯỜNG","NORMAL RUN"),Lang.t("30 VÒNG - 9 MẢNH","30 ROUNDS - 9 ITEMS"),Lang.t("THẾ HỆ 1","GENERATION 1")};
        int extra=(hasResume?1:0)+1;String[] mode=new String[base.length+extra];for(int i=0;i<base.length;i++)mode[i]=base[i];int q=base.length;if(hasResume)mode[q++]=Lang.t("TIẾP TỤC VÒNG ","RESUME ROUND ")+savedRound;mode[q]=Lang.t("LỊCH SỬ ĐẤU","BATTLE HISTORY");
        int y=Math.max(fh*3,H/2-(mode.length*(fh+10))/2);
        for(int i=0;i<mode.length;i++){
            if(i==selected){
                g.setColor(0x305090);g.fillRect(W/8,y+i*(fh+10)-3,W*3/4,fh+6);
                g.setColor(0xFFD030);g.drawRect(W/8,y+i*(fh+10)-3,W*3/4-1,fh+5);
            }
            Art.textC(g,mode[i],W/2,y+i*(fh+10),i==selected?0xFFFFFF:0xAFC0D8);
        }
        int modes=base.length;String note;if(selected>=modes)note=hasResume&&selected==modes?Lang.t("Khôi phục đội hình, shop, đồ và kinh tế","Restore team, shop, items and economy"):Lang.t("Xem kết quả và 9 Pokemon cuối","View results and final 9 Pokemon");else{int actual=modeAt(selected);note=actual==Run.MODE_UNLIMITED?Lang.t("Roll, mua và XP miễn phí để test","Free roll, buy and XP for testing"):actual==Run.MODE_THIRTY?Lang.t("9 mảnh mỗi vòng, boss mỗi 5 vòng","9 components each round, boss every 5 rounds"):actual==Run.MODE_GEN1?Lang.t("Chỉ Gen 1, boss cố định mỗi 5 vòng","Gen 1 only, fixed boss every 5 rounds"):Lang.t("Kinh tế và phần thưởng tiêu chuẩn","Standard economy and rewards");}
        Art.textSmallC(g,note,W/2,H-fh*3,0x90A0B8);
        Art.textC(g,Lang.t("FIRE: bắt đầu   0: về","FIRE: start   0: back"),W/2,H-fh-3,0x8090B0);
    }

}

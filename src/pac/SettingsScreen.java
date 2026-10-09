package pac;

import javax.microedition.lcdui.Graphics;

/** Persistent player settings. */
public final class SettingsScreen extends Screen {
    private int selected;
    private boolean confirmReset;

    public SettingsScreen(Game game) { super(game); }

    public void update(int dt) { }
    private int rowGap(){return UiLayout.compact(game.W,game.H)?Art.fh+5:Art.fh*2+8;}
    private int rowTop(){return UiLayout.compact(game.W,game.H)?Art.fh*3:Math.max(Art.fh*3,game.H/3-rowGap()/2);}
    public boolean pointer(int x,int y){if(confirmReset)return false;int gap=rowGap(),top=rowTop(),index=(y-top+3)/gap;if(x>=8&&x<game.W-8&&y>=top-3&&index<5&&(y-top+3)%gap<Art.fh+6){selected=index;if(confirmTouch(selected))key(Game.K_FIRE);return true;}if(y>=game.H-Art.fh-8){key(Game.K_0);return true;}return true;}

    public void key(int key) {
        if(key!=Game.K_FIRE&&key!=Game.K_SOFT1)resetTouchChoice();
        if(confirmReset){
            if(key==Game.K_FIRE||key==Game.K_SOFT1){Save.resetProgress();Save.save();RunStorage.clear();HistoryStore.clear();confirmReset=false;selected=0;}
            else if(key==Game.K_0||key==Game.K_SOFT2)confirmReset=false;
            return;
        }
        if (key == Game.K_UP || key == Game.K_DOWN)
            selected = (selected + (key==Game.K_UP?4:1)) % 5;
        else if (key == Game.K_LEFT || key == Game.K_RIGHT || key == Game.K_FIRE || key == Game.K_SOFT1) {
            if (selected == 0) Lang.toggle();
            else if (selected == 1) { nextPerformance();Save.save(); }
            else if(selected==2)Music.toggle();
            else if(selected==3)confirmReset=true;
            else game.setScreen(new MenuScreen(game));
        } else if (key == Game.K_SOFT2 || key == Game.K_0)
            game.setScreen(new MenuScreen(game));
    }

    public void paint(Graphics g) {
        int W = game.W, H = game.H, fh = Art.fh;
        g.setColor(0x101827); g.fillRect(0, 0, W, H);
        Art.textBC(g, Lang.t("CÀI ĐẶT", "SETTINGS"), W / 2, 10, 0xFFD030);

        boolean compact=UiLayout.compact(W,H);int gap=rowGap();
        int y = rowTop();
        drawRow(g, 0, y, Lang.t("Ngôn ngữ", "Language"),
                Save.language == Lang.VI ? "Tiếng Việt" : "English");
        drawRow(g, 1, y + gap, Lang.t("Hiệu năng", "Performance"),
                performanceName());
        drawRow(g, 2, y + gap*2, Lang.t("Nhạc nền", "Music"),!Music.enabled()?Lang.t("Tắt","Off"):Music.lastError.length()>0?Lang.t("Lỗi phát","Error"):Lang.t("Bật","On"));
        drawRow(g, 3, y + gap*3, Lang.t("Chơi lại từ đầu", "Start over"), "");
        drawRow(g, 4, y + gap*4, Lang.t("Quay lại", "Back"), "");

        if(!compact)Art.textC(g, selected == 0
                ? Lang.t("TRÁI/PHẢI hoặc FIRE để đổi", "LEFT/RIGHT or FIRE to change")
                : Lang.t("FIRE để về menu", "FIRE to return"),
                W / 2, H - fh * 2 - 4, 0x90A0B8);
        Art.textC(g, Lang.t("Thiết lập được tự động lưu", "Settings are saved automatically"),
                W / 2, H - fh - 2, 0x708098);
        if(confirmReset){
            int bw=UiLayout.popupWidth(W,W-16),bh=UiLayout.popupHeight(H,fh*5+8),bx=(W-bw)/2,by=(H-bh)/2;
            Art.box(g,bx,by,bw,bh,0x181B28,0xFF6058);
            Art.textBC(g,Lang.t("XÓA TIẾN ĐỘ?","ERASE PROGRESS?"),W/2,by+4,0xFF7068);
            Art.textSmallC(g,Lang.t("Xóa run, lịch sử, bộ sưu tập","Erase run, history, collection"),W/2,by+fh+6,0xE0D0D0);
            Art.textSmallC(g,Lang.t("FIRE: xác nhận   0: hủy","FIRE: confirm   0: cancel"),W/2,by+fh*3+5,0xFFD060);
        }
    }

    private void nextPerformance(){
        if(Save.performance==1)Save.performance=0;
        else if(Save.performance==0)Save.performance=2;
        else if(Save.performance==2)Save.performance=3;
        else Save.performance=1;
    }

    private String performanceName(){
        switch(Save.performance){
            case 1:return Lang.t("10 FPS Pin","10 FPS Battery");
            case 2:return Lang.t("20 FPS TB","20 FPS Medium");
            case 3:return Lang.t("25 FPS Cao","25 FPS High");
            default:return Lang.t("16 FPS Mượt","16 FPS Smooth");
        }
    }

    private void drawRow(Graphics g, int index, int y, String name, String value) {
        int W = game.W, fh = Art.fh;
        if (selected == index) {
            g.setColor(0x305090); g.fillRect(8, y - 3, W - 16, fh + 6);
            g.setColor(0xFFD030); g.drawRect(8, y - 3, W - 17, fh + 5);
        }
        Art.text(g, name, 14, y, selected == index ? 0xFFFFFF : 0xB0C0E0);
        if (value.length() > 0) {
            Art.textR(g, "< " + value + " >", W - 14, y,
                    selected == index ? 0xFFE070 : 0x90A8C8);
        }
    }
}

package pac;

import javax.microedition.lcdui.Graphics;

/** Reserved community/about page; final copy will be supplied for v1.3.5. */
public final class AboutScreen extends Screen {
    public AboutScreen(Game g){super(g);}
    public void update(int dt){}
    public void key(int k){if(k==Game.K_FIRE||k==Game.K_SOFT1||k==Game.K_SOFT2||k==Game.K_0)game.setScreen(new MenuScreen(game));}
    public void paint(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh;g.setColor(0x101827);g.fillRect(0,0,W,H);
        Art.textBC(g,Lang.t("VỀ GAME","ABOUT"),W/2,8,0xFFD030);
        Art.textC(g,"POKE AUTO CHESS ME",W/2,fh*3,0xFFFFFF);
        Art.textSmallC(g,Lang.t("Trang dành cho đôi lời gửi tới","A page reserved for a message to"),W/2,fh*5,0xB8C8E0);
        Art.textSmallC(g,Lang.t("cộng đồng và người chơi.","the community and players."),W/2,fh*6,0xB8C8E0);
        Art.textSmallC(g,Lang.t("Nội dung sẽ cập nhật ở v1.3.5.","Final text arrives in v1.3.5."),W/2,fh*8,0xFFE080);
        Art.textC(g,Lang.t("FIRE / 0: quay lại","FIRE / 0: back"),W/2,H-fh-3,0x8090B0);
    }
}

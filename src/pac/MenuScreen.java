package pac;

import javax.microedition.lcdui.Graphics;

public final class MenuScreen extends Screen {
    private int sel = 0;
    private int t = 0;

    public MenuScreen(Game g) {
        super(g);
    }

    public void update(int dt) {
        t += dt;
    }

    public void key(int k) {
        switch (k) {
            case Game.K_UP: sel = (sel + Lang.menu().length - 1) % Lang.menu().length; break;
            case Game.K_DOWN: sel = (sel + 1) % Lang.menu().length; break;
            case Game.K_FIRE:
            case Game.K_SOFT1:
                choose();
                break;
            default: break;
        }
    }

    private void choose() {
        switch (sel) {
            case 0: game.setScreen(new ExploreHubScreen(game)); break;
            case 1: game.setScreen(new ChessModeScreen(game)); break;
            case 2: game.setScreen(new CollectionScreen(game)); break;
            case 3: game.setScreen(new SettingsScreen(game)); break;
            case 4: game.setScreen(new HelpScreen(game)); break;
            case 5: game.setScreen(new AboutScreen(game)); break;
            default: game.quit(); break;
        }
    }

    public void paint(Graphics g) {
        int W = game.W, H = game.H, fh = Art.fh;
        for (int i = 0; i < 8; i++) {
            g.setColor(0x102040 + (i * 0x000A08));
            g.fillRect(0, H * i / 8, W, H / 8 + 1);
        }
        Art.textBC(g, "POKE AUTO CHESS", W / 2, 6, 0xFFD030);
        Art.textC(g, "J2ME Edition", W / 2, 6 + fh + 1, 0xA0C0FF);

        boolean compact=UiLayout.compact(W,H);
        // Compact phones reserve vertical space for every menu entry.
        int size = W / 5;
        int shown = 0;
        int y0 = 6 + fh * 2 + (compact?2:10);
        for (int i = 0; !compact && i < Data.N && shown < 5; i++) {
            if (Data.isBase(i) && Save.unlocked[i]) {
                int bob = ((t / 200 + shown) & 1) == 0 ? 0 : 2;
                Art.formationSprite(g,i,shown*size,y0+bob,size,42,W,t/90+shown);
                shown++;
            }
        }

        int y = compact?y0+fh:y0+48;
        String[] items = Lang.menu();
        int gap=compact?fh+1:fh+2;
        for (int i = 0; i < items.length; i++) {
            boolean s = i == sel;
            if (s) {
                g.setColor(0x305090);
                g.fillRect(W / 8, y - 1, W * 3 / 4, fh + 2);
                g.setColor(0xFFD030);
                g.drawRect(W / 8, y - 1, W * 3 / 4 - 1, fh + 1);
            }
            Art.textC(g, items[i], W / 2, y, s ? 0xFFFFFF : 0xB0C0E0);
            y += gap;
        }
        y = H - fh * 3 - 3;
        if(compact){
            Art.textSmallC(g,Lang.t("Bóng ","Balls ")+Save.balls+Lang.t("  Bộ sưu tập ","  Collection ")+Save.familiesUnlocked()+"/"+Data.countFamilies(),W/2,y+fh,0xC0FFC0);
        }else{
            Art.textC(g, Lang.t("Bóng: ", "Poke Balls: ") + Save.balls, W / 2, y, 0xFF8080);
            Art.textC(g, Lang.t("Bộ sưu tập ", "Collection ") + Save.familiesUnlocked() + "/" + Data.countFamilies()
                    + Lang.t("   Vòng cao nhất ", "   Best round ") + Save.best, W / 2, y + fh, 0xC0FFC0);
        }
        Art.textSmallR(g,"(c) 2026 Kdic",W-3,H-fh-1,0x68788E);
    }
}

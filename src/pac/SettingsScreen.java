package pac;

import javax.microedition.lcdui.Graphics;

/** Persistent player settings. */
public final class SettingsScreen extends Screen {
    private int selected;

    public SettingsScreen(Game game) { super(game); }

    public void update(int dt) { }

    public void key(int key) {
        if (key == Game.K_UP || key == Game.K_DOWN)
            selected = (selected + 1) % 3;
        else if (key == Game.K_LEFT || key == Game.K_RIGHT || key == Game.K_FIRE || key == Game.K_SOFT1) {
            if (selected == 0) Lang.toggle();
            else if (selected == 1) { Save.performance=1-Save.performance;Save.save(); }
            else game.setScreen(new MenuScreen(game));
        } else if (key == Game.K_SOFT2 || key == Game.K_0)
            game.setScreen(new MenuScreen(game));
    }

    public void paint(Graphics g) {
        int W = game.W, H = game.H, fh = Art.fh;
        g.setColor(0x101827); g.fillRect(0, 0, W, H);
        Art.textBC(g, Lang.t("CÀI ĐẶT", "SETTINGS"), W / 2, 10, 0xFFD030);

        int y = H / 3;
        drawRow(g, 0, y, Lang.t("Ngôn ngữ", "Language"),
                Save.language == Lang.VI ? "Tiếng Việt" : "English");
        drawRow(g, 1, y + fh * 2 + 8, Lang.t("Hiệu năng", "Performance"),
                Save.performance==0?Lang.t("Mượt","Smooth"):Lang.t("Tiết kiệm","Battery"));
        drawRow(g, 2, y + (fh * 2 + 8)*2, Lang.t("Quay lại", "Back"), "");

        Art.textC(g, selected == 0
                ? Lang.t("TRÁI/PHẢI hoặc FIRE để đổi", "LEFT/RIGHT or FIRE to change")
                : Lang.t("FIRE để về menu", "FIRE to return"),
                W / 2, H - fh * 2 - 4, 0x90A0B8);
        Art.textC(g, Lang.t("Thiết lập được tự động lưu", "Settings are saved automatically"),
                W / 2, H - fh - 2, 0x708098);
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

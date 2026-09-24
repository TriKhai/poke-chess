package pac;

import javax.microedition.lcdui.Graphics;

public final class HelpScreen extends Screen {
    private String[] lines;
    private int scroll = 0;
    private int wrappedWidth=-1;

    public HelpScreen(Game g) {
        super(g);
    }

    public void update(int dt) { }

    public void key(int k) {
        switch (k) {
            case Game.K_UP: if (scroll > 0) scroll--; break;
            case Game.K_DOWN: scroll++; break;
            case Game.K_SOFT2: case Game.K_0: case Game.K_FIRE: case Game.K_SOFT1:
                game.setScreen(new MenuScreen(game));
                break;
            default: break;
        }
    }

    public void paint(Graphics g) {
        int W = game.W, H = game.H, fh = Art.fh;
        g.setColor(0x102040);
        g.fillRect(0, 0, W, H);
        if (lines == null||wrappedWidth!=W) {
            wrappedWidth=W;
            String text = Lang.help();
            // wrap paragraph by paragraph
            java.util.Vector v = new java.util.Vector();
            int start = 0;
            while (start <= text.length()) {
                int nl = text.indexOf('\n', start);
                if (nl < 0) nl = text.length();
                String[] w = Art.wrap(text.substring(start, nl), W - 8, 60);
                for (int i = 0; i < w.length; i++) v.addElement(w[i]);
                v.addElement("");
                start = nl + 1;
            }
            lines = new String[v.size()];
            v.copyInto(lines);
        }
        Art.textBC(g, Lang.t("HƯỚNG DẪN","HELP"), W / 2, 2, 0xFFD030);
        int visible = UiLayout.visibleRows(H,fh+4,fh+2,fh);
        if (scroll > lines.length - visible) scroll = Math.max(0, lines.length - visible);
        int y = fh + 4;
        for (int i = scroll; i < lines.length && i < scroll + visible; i++) {
            Art.text(g, lines[i], 4, y, 0xE0E8FF);
            y += fh;
        }
        Art.textC(g, Lang.t("Lên/Xuống cuộn - FIRE về","Up/Down scroll - FIRE back"), W / 2, H - fh - 1, 0x8090B0);
    }
}

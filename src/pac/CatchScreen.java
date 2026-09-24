package pac;

import javax.microedition.lcdui.Graphics;

/** Timing mini-game: press FIRE while the marker is inside the green zone to catch the wild Pokemon. */
public final class CatchScreen extends Screen {
    private final WorldScreen world;
    private final int sp, wx, wy;
    private final Rng rng;

    private int m = 0, dir = 1;          // marker position 0..1000
    private int zc, zw;                  // zone centre / width in per-mille
    private int state = 0;               // 0 aiming, 1 showing result
    private int timer = 0;
    private int fails = 0;
    private boolean caught = false, fled = false;
    private String msg = "";
    private int t = 0;

    public CatchScreen(Game g, WorldScreen w, int species, int x, int y) {
        super(g);
        world = w;
        sp = species;
        wx = x;
        wy = y;
        rng = new Rng((int) System.currentTimeMillis() ^ (species * 7919));
        zw = Data.CATCH_W[Data.cost[sp]];
        newZone();
    }

    private void newZone() {
        zc = zw / 2 + rng.nextInt(1000 - zw + 1);
        m = 0;
        dir = 1;
    }

    private int sweepMs() {
        return 1000 - 100 * Data.cost[sp];
    }

    public void update(int dt) {
        t += dt;
        if (state == 0) {
            m += dir * dt * 1000 / sweepMs();
            if (m >= 1000) { m = 1000; dir = -1; }
            if (m <= 0) { m = 0; dir = 1; }
        } else {
            timer -= dt;
            if (timer <= 0) {
                if (caught || fled) {
                    world.encounterDone(wx, wy, true, msg);
                    game.setScreen(world);
                } else {
                    state = 0;
                    newZone();
                }
            }
        }
    }

    public void key(int k) {
        if (state == 1) return;
        if (k == Game.K_SOFT2 || k == Game.K_0) {
            world.encounterDone(wx, wy, false, Lang.t("Bạn đã rời đi an toàn.", "You got away safely."));
            game.setScreen(world);
            return;
        }
        if (k != Game.K_FIRE) return;
        if (Save.balls <= 0) {
            world.encounterDone(wx, wy, false, Lang.t("Đã hết Bóng!", "Out of Poke Balls!"));
            game.setScreen(world);
            return;
        }
        Save.balls--;
        int d = m - zc;
        if (d < 0) d = -d;
        if (d <= zw / 2) {
            boolean isNew = !Save.has(sp);
            Save.caught++;
            Save.unlockFamily(sp);
            if (isNew) {
                msg = Lang.t("Đã bắt ", "Caught ") + Data.name[sp] + Lang.t("! MỚI trong bộ sưu tập!", "! NEW in collection!");
            } else {
                Save.balls++;
                msg = Lang.t("Đã bắt ", "Caught ") + Data.name[sp] + Lang.t("! (trùng, hoàn lại bóng)", "! (duplicate, ball refunded)");
            }
            Save.save();
            caught = true;
            timer = 1600;
        } else if (rng.pct(8 + fails * 8 + Data.cost[sp] * 2)) {
            fled = true;
            msg = Data.name[sp] + Lang.t(" đã chạy mất!", " fled!");
            timer = 1200;
        } else {
            fails++;
            msg = Lang.t("Trượt!", "Missed!");
            timer = 550;
        }
        state = 1;
    }

    public void paint(Graphics g) {
        int W = game.W, H = game.H, fh = Art.fh;
        for (int i = 0; i < 10; i++) {
            g.setColor(0x203860 + i * 0x020304);
            g.fillRect(0, H * i / 10, W, H / 10 + 1);
        }
        g.setColor(0x4A7A3A);
        g.fillRect(0, H * 6 / 10, W, H - H * 6 / 10);

        int sz = Math.min(W, H) * 2 / 5;
        int sx = W / 2 - sz / 2, sy = H / 6;
        int bob = state == 0 ? ((t / 250) & 1) * 2 : 0;
        if (state == 1 && caught) {
            int d = sz / 2;
            drawBall(g, W / 2 - d / 2, sy + sz / 2, d);
        } else if (!(state == 1 && fled)) {
            Art.sprite(g, sp, sx, sy - bob, sz);
        }

        Art.textBC(g, Data.name[sp], W / 2, 4, 0xFFFFFF);
        Art.textC(g, Data.RARITY[Data.cost[sp]] + (Save.has(sp) ? "" : Lang.t("  - MỚI!", "  - NEW!")), W / 2, 4 + fh, Save.has(sp) ? 0xB0C0E0 : 0xFFE040);

        int bx = W / 10, bw = W * 8 / 10, bh = fh + 6;
        int by = H * 7 / 10;
        g.setColor(0x000000);
        g.fillRect(bx - 1, by - 1, bw + 2, bh + 2);
        g.setColor(0x404858);
        g.fillRect(bx, by, bw, bh);
        int zx = bx + (zc - zw / 2) * bw / 1000;
        int zwp = zw * bw / 1000;
        g.setColor(0x30D040);
        g.fillRect(zx, by, zwp, bh);
        int mx = bx + m * bw / 1000;
        g.setColor(0xFFFFFF);
        g.fillRect(mx - 1, by - 3, 3, bh + 6);

        Art.textC(g, "Poke Balls: " + Save.balls, W / 2, by + bh + 6, 0xFF9090);
        if (state == 0) Art.textC(g, "FIRE: throw   0: run", W / 2, H - fh - 3, 0xE0E8FF);
        else Art.textBC(g, msg, W / 2, H - fh * 2 - 6, caught ? 0xFFE040 : 0xFFFFFF);
    }

    private static void drawBall(Graphics g, int x, int y, int d) {
        g.setColor(0xE03030);
        g.fillArc(x, y, d, d, 0, 180);
        g.setColor(0xF8F8F8);
        g.fillArc(x, y, d, d, 180, 180);
        g.setColor(0x000000);
        g.drawArc(x, y, d, d, 0, 360);
        g.drawLine(x, y + d / 2, x + d, y + d / 2);
        g.setColor(0xF8F8F8);
        g.fillArc(x + d / 2 - d / 8, y + d / 2 - d / 8, d / 4, d / 4, 0, 360);
        g.setColor(0x000000);
        g.drawArc(x + d / 2 - d / 8, y + d / 2 - d / 8, d / 4, d / 4, 0, 360);
    }
}

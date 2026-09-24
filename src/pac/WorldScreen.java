package pac;

import javax.microedition.lcdui.Graphics;

/**
 * Mode 1: top-down 2D map. Wild Pokemon are released onto the map and wander around;
 * walk next to one and press FIRE to try to catch it.
 */
public final class WorldScreen extends Screen {
    static final int MW = 56, MH = 44, MAXW = 7;
    static final int T_GRASS = 0, T_SAND = 1, T_ROCK = 2, T_DARK = 3, T_WATER = 4, T_TREE = 5,
            T_BOULDER = 6, T_DEAD = 7;

    private final byte[] tile = new byte[MW * MH];
    private final byte[] zone = new byte[MW * MH];
    private int px = MW / 2, py = MH / 2;

    private final int[] wsp = new int[MAXW];
    private final int[] wx = new int[MAXW];
    private final int[] wy = new int[MAXW];
    private final int[] wttl = new int[MAXW];
    private int wn = 0;

    private final Rng rng;
    private int acc = 0, tick = 0, time = 0,playerDir=0,playerWalk=0;
    private String hud = "";
    private int hudT = 0;

    public WorldScreen(Game g) {
        super(g);
        rng = new Rng((int) System.currentTimeMillis());
        gen();
        for (int i = 0; i < 4; i++) spawn(true);
    }

    // ---- map generation --------------------------------------------------

    private int baseTile(int z) {
        return z == 2 ? T_ROCK : (z == 3 ? T_DARK : T_GRASS);
    }

    private void gen() {
        int[] sx = { 12, 43, 12, 43 };
        int[] sy = { 10, 10, 33, 33 };
        int[] zid = { 0, 1, 2, 3 };
        for (int i = 3; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int t = zid[i]; zid[i] = zid[j]; zid[j] = t;
        }
        int lakeSeed = 0;
        for (int k = 0; k < 4; k++) if (zid[k] == 1) lakeSeed = k;

        for (int y = 0; y < MH; y++) {
            for (int x = 0; x < MW; x++) {
                int best = 0, bd = 0x7FFFFFFF;
                for (int k = 0; k < 4; k++) {
                    int dx = x - sx[k], dy = y - sy[k];
                    int d = dx * dx + dy * dy + ((x * (k + 3) + y * (k + 5)) & 15) * 6;
                    if (d < bd) { bd = d; best = k; }
                }
                int z = zid[best];
                int i = y * MW + x;
                zone[i] = (byte) z;
                int t = baseTile(z);
                int r = rng.nextInt(100);
                if (z == 0 && r < 8) t = T_TREE;
                else if (z == 1 && r < 3) t = T_TREE;
                else if (z == 2 && r < 8) t = T_BOULDER;
                else if (z == 3 && r < 8) t = T_DEAD;
                tile[i] = (byte) t;
            }
        }
        // lakes
        for (int b = 0; b < 3; b++) {
            int cx = sx[lakeSeed] + rng.nextInt(13) - 6;
            int cy = sy[lakeSeed] + rng.nextInt(9) - 4;
            int rx = 3 + rng.nextInt(4), ry = 2 + rng.nextInt(3);
            for (int y = cy - ry - 2; y <= cy + ry + 2; y++) {
                for (int x = cx - rx - 2; x <= cx + rx + 2; x++) {
                    if (x < 1 || y < 1 || x >= MW - 1 || y >= MH - 1) continue;
                    int dx = x - cx, dy = y - cy;
                    int v = dx * dx * 100 / (rx * rx) + dy * dy * 100 / (ry * ry);
                    int i = y * MW + x;
                    if (v <= 100) tile[i] = (byte) T_WATER;
                    else if (v <= 200 && tile[i] != T_WATER) tile[i] = (byte) T_SAND;
                }
            }
        }
        for (int x = 0; x < MW; x++) { tile[x] = T_TREE; tile[(MH - 1) * MW + x] = T_TREE; }
        for (int y = 0; y < MH; y++) { tile[y * MW] = T_TREE; tile[y * MW + MW - 1] = T_TREE; }
        for (int dy = -3; dy <= 3; dy++) {
            for (int dx = -3; dx <= 3; dx++) {
                int i = (py + dy) * MW + (px + dx);
                tile[i] = (byte) baseTile(zone[i]);
            }
        }
    }

    private boolean walkable(int x, int y) {
        if (x < 0 || y < 0 || x >= MW || y >= MH) return false;
        return tile[y * MW + x] <= T_DARK;
    }

    private int wildAt(int x, int y) {
        for (int i = 0; i < wn; i++) if (wx[i] == x && wy[i] == y) return i;
        return -1;
    }

    // ---- wild pokemon ----------------------------------------------------

    private void spawn(boolean near) {
        if (wn >= MAXW) return;
        for (int tries = 0; tries < 12; tries++) {
            int r = near ? 5 : 8;
            int x = px + rng.nextInt(2 * r + 1) - r;
            int y = py + rng.nextInt(2 * r + 1) - r;
            if (Math.abs(x - px) < 2 && Math.abs(y - py) < 2) continue;
            if (!walkable(x, y) || wildAt(x, y) >= 0) continue;
            int sp = Data.pickWild(zone[y * MW + x], rng);
            if (sp < 0) continue;
            wsp[wn] = sp; wx[wn] = x; wy[wn] = y; wttl[wn] = 250 + rng.nextInt(200);
            wn++;
            return;
        }
    }

    private void removeWild(int i) {
        wn--;
        wsp[i] = wsp[wn]; wx[i] = wx[wn]; wy[i] = wy[wn]; wttl[i] = wttl[wn];
    }

    private void simStep() {
        tick++;
        for (int i = wn - 1; i >= 0; i--) {
            wttl[i]--;
            int dist = Math.max(Math.abs(wx[i] - px), Math.abs(wy[i] - py));
            if (wttl[i] <= 0 || dist > 22) { removeWild(i); continue; }
            if (rng.pct(18)) {
                int dx = rng.nextInt(3) - 1, dy = rng.nextInt(3) - 1;
                if (rng.pct(50)) dy = 0; else dx = 0;
                int nx = wx[i] + dx, ny = wy[i] + dy;
                if ((dx != 0 || dy != 0) && walkable(nx, ny) && wildAt(nx, ny) < 0 && !(nx == px && ny == py)) {
                    wx[i] = nx; wy[i] = ny;
                }
            }
        }
        if (wn < MAXW && rng.pct(10)) spawn(false);
        if (tick % 300 == 0 && Save.balls < 5) {
            Save.balls++;
            say(Lang.t("+1 Bóng", "+1 Poke Ball"));
        }
    }

    private void say(String s) {
        hud = s;
        hudT = 2500;
    }

    // ---- interaction -----------------------------------------------------

    public void update(int dt) {
        time += dt;
        if(playerWalk>0)playerWalk-=dt;
        if (hudT > 0) hudT -= dt;
        acc += dt;
        while (acc >= 200) {
            acc -= 200;
            simStep();
        }
    }

    private int nearestAdjacent() {
        int best = -1, bd = 99;
        for (int i = 0; i < wn; i++) {
            int d = Math.max(Math.abs(wx[i] - px), Math.abs(wy[i] - py));
            if (d <= 1 && d < bd) { bd = d; best = i; }
        }
        return best;
    }

    private void tryMove(int dx, int dy) {
        int nx = px + dx, ny = py + dy;
        if (walkable(nx, ny) && wildAt(nx, ny) < 0) {
            px = nx; py = ny;playerDir=dx>0?2:(dx<0?6:(dy<0?4:0));playerWalk=260;
        }
    }

    public void key(int k) {
        switch (k) {
            case Game.K_UP: tryMove(0, -1); break;
            case Game.K_DOWN: tryMove(0, 1); break;
            case Game.K_LEFT: tryMove(-1, 0); break;
            case Game.K_RIGHT: tryMove(1, 0); break;
            case Game.K_FIRE: {
                int w = nearestAdjacent();
                if (w < 0) say(Lang.t("Không có gì để bắt. Hãy đến gần hơn!", "Nothing to catch here. Get closer!"));
                else if (Save.balls <= 0) say(Lang.t("Hết Bóng! Hãy chơi Auto Chess để nhận thêm.", "No Poke Balls! Play Auto Chess to earn some."));
                else game.setScreen(new CatchScreen(game, this, wsp[w], wx[w], wy[w]));
                break;
            }
            case Game.K_SOFT2:
            case Game.K_0:
                Save.save();
                game.setScreen(new MenuScreen(game));
                break;
            case Game.K_1: say(Lang.t("Khu vực: ", "Zone: ") + Data.ZNAME[zone[py * MW + px]]); break;
            default: break;
        }
    }

    /** called by CatchScreen when the encounter is over. */
    public void encounterDone(int x, int y, boolean remove, String msg) {
        if (remove) {
            int i = wildAt(x, y);
            if (i >= 0) removeWild(i);
        }
        if (msg != null) say(msg);
    }

    // ---- rendering -------------------------------------------------------

    private void drawTile(Graphics g, int tx, int ty, int sx, int sy, int ts) {
        int t = tile[ty * MW + tx];
        int h = (tx * 7 + ty * 13) & 7;
        boolean alt = ((tx + ty) & 1) == 0;
        int base;
        switch (t) {
            case T_SAND: base = alt ? 0xE2CA84 : 0xDCC27C; break;
            case T_ROCK: base = alt ? 0x9C9C9C : 0x929292; break;
            case T_DARK: base = alt ? 0x54446A : 0x4A3B5E; break;
            case T_WATER: base = 0x3C78D8; break;
            case T_BOULDER: base = alt ? 0x9C9C9C : 0x929292; break;
            case T_DEAD: base = alt ? 0x54446A : 0x4A3B5E; break;
            default: base = alt ? 0x5AB250 : 0x52AA48; break;
        }
        g.setColor(base);
        g.fillRect(sx, sy, ts, ts);
        switch (t) {
            case T_GRASS:
                if (h == 0 || h == 3) {
                    g.setColor(0x3E8A38);
                    g.fillRect(sx + ts / 4, sy + ts / 2, 2, ts / 5 + 1);
                    g.fillRect(sx + ts / 4 + 3, sy + ts / 2 + 1, 2, ts / 5);
                }
                break;
            case T_SAND:
                if (h < 2) { g.setColor(0xC8AE64); g.fillRect(sx + h * ts / 3 + 2, sy + ts / 2, 2, 2); }
                break;
            case T_ROCK:
                if (h < 2) { g.setColor(0x787878); g.drawLine(sx + 2, sy + ts / 3, sx + ts / 2, sy + ts / 2); }
                break;
            case T_DARK:
                if (h < 2) { g.setColor(0x382C48); g.fillRect(sx + ts / 3, sy + ts / 3, 3, 2); }
                break;
            case T_WATER:
                g.setColor(0x78B0F0);
                if ((((tx + ty) + time / 500) & 1) == 0) g.drawLine(sx + 2, sy + ts / 2, sx + ts - 3, sy + ts / 2);
                else g.drawLine(sx + 4, sy + ts / 3, sx + ts - 5, sy + ts / 3);
                break;
            case T_TREE:
                g.setColor(0x6A4020);
                g.fillRect(sx + ts * 2 / 5, sy + ts / 2, ts / 5 + 1, ts / 2);
                g.setColor(0x2A7A30);
                g.fillArc(sx + 1, sy, ts - 2, ts * 2 / 3 + 2, 0, 360);
                g.setColor(0x38A040);
                g.fillArc(sx + ts / 4, sy + 1, ts / 3, ts / 3, 0, 360);
                break;
            case T_BOULDER:
                g.setColor(0x6E6E6E);
                g.fillArc(sx + 2, sy + ts / 4, ts - 4, ts * 3 / 4 - 1, 0, 360);
                g.setColor(0xA8A8A8);
                g.fillArc(sx + ts / 3, sy + ts / 3, ts / 4, ts / 5, 0, 360);
                break;
            case T_DEAD:
                g.setColor(0x2A1E1A);
                g.drawLine(sx + ts / 2, sy + ts - 2, sx + ts / 2, sy + 3);
                g.drawLine(sx + ts / 2, sy + ts / 2, sx + 3, sy + 3);
                g.drawLine(sx + ts / 2, sy + ts / 3, sx + ts - 3, sy + 4);
                break;
            default:
                break;
        }
    }

    public void paint(Graphics g) {
        int W = game.W, H = game.H, fh = Art.fh;
        int ts = W >= 200 ? 20 : (W >= 150 ? 16 : 12);
        int camX = px * ts + ts / 2 - W / 2;
        int camY = py * ts + ts / 2 - H / 2;
        if (camX > MW * ts - W) camX = MW * ts - W;
        if (camY > MH * ts - H) camY = MH * ts - H;
        if (camX < 0) camX = 0;
        if (camY < 0) camY = 0;

        g.setColor(0x000000);
        g.fillRect(0, 0, W, H);
        int tx0 = camX / ts, ty0 = camY / ts;
        int tx1 = (camX + W) / ts, ty1 = (camY + H) / ts;
        for (int ty = ty0; ty <= ty1 && ty < MH; ty++) {
            for (int tx = tx0; tx <= tx1 && tx < MW; tx++) {
                drawTile(g, tx, ty, tx * ts - camX, ty * ts - camY, ts);
            }
        }

        int near = nearestAdjacent();
        for (int i = 0; i < wn; i++) {
            int sx = wx[i] * ts - camX, sy = wy[i] * ts - camY;
            if (sx < -ts || sy < -ts || sx > W || sy > H) continue;
            int bob = (((time / 300) + i) & 1);
            // Raw atlas idle frame: unlike the old padded static thumbnail this
            // keeps the visible body readable at world-tile scale.
            Art.formationSprite(g,wsp[i],sx,sy-bob,ts,ts,W,time/90+i);
            if (!Save.has(wsp[i])) Art.text(g, "!", sx + ts / 2 - 2, sy - fh + 2, 0xFFE040);
            if (i == near) {
                g.setColor(0xFFFFFF);
                g.drawRect(sx, sy, ts - 1, ts - 1);
            }
        }

        // Profile Pokemon is the player's exploration actor.
        int sx = px * ts - camX, sy = py * ts - camY;
        drawProfilePokemon(g,sx,sy,ts);

        // HUD
        g.setColor(0x101820);
        g.fillRect(0, 0, W, fh + 2);
        Art.text(g, "Balls " + Save.balls, 3, 1, 0xFF9090);
        String zn = Data.ZNAME[zone[py * MW + px]];
        Art.textR(g, zn, W - 3, 1, 0xB0F0B0);
        g.setColor(0x101820);
        g.fillRect(0, H - fh - 2, W, fh + 2);
        String bottom;
        if (hudT > 0) bottom = hud;
        else if (near >= 0) bottom = Data.name[wsp[near]] + " (" + Data.RARITY[Data.cost[wsp[near]]] + ") FIRE: catch";
        else bottom = "Find wild Pokemon!   0: menu";
        Art.text(g, bottom, 3, H - fh - 1, near >= 0 && hudT <= 0 ? 0xFFE060 : 0xE0E8FF);
    }
    private void drawProfilePokemon(Graphics g,int x,int y,int size){int dex=Save.profileAvatarDex,action=playerWalk>0?RawAtlas.WALK:RawAtlas.IDLE,box=Math.max(32,size+12),bx=x-(box-size)/2,by=y-(box-size);if(dex<=Data.N){int sp=dex-1;if(!RawAtlas.draw(g,sp,bx,by,box,box,action,playerDir,time/90))Art.sprite(g,sp,x,y,size);}else{int di=-1;for(int i=0;i<CollectionDex.COUNT;i++)if(CollectionDex.DEX[i]==dex){di=i;break;}if(di<0||!CollectionAtlas.draw(g,di,bx,by,box,box,action,playerDir,time/90))if(di>=0)Art.dexAvatar(g,di,x+(size-32)/2,y+(size-32)/2);}}
}

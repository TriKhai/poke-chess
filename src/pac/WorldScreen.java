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
    private final ExploreMotion motion=new ExploreMotion(px,py);
    private int moveKey,moveT;private boolean touchMoving;private int touchX,touchY;
    private final int mapGen;

    private final int[] wsp = new int[MAXW];
    private final int[] wx = new int[MAXW];
    private final int[] wy = new int[MAXW];
    private final int[] wttl = new int[MAXW];
    private final int[] walkBounds=new int[4];
    private int wn = 0;

    private final Rng rng;
    private int acc = 0, tick = 0, time = 0,playerDir=0;
    private String hud = "";private boolean legendNotice,legendPopup,legendSpawned,showQuest,questDetail;private int legendSp=-1,legendWaveT,questCursor,questDetailTop;
    private int tripCaught,tripDefeated,tripMissed,tripLegends;private int questTab,caughtCursor;
    private int hudT = 0;private int hudBall=-1,hudBallCount;

    public WorldScreen(Game g) {this(g,1);}
    public void onShow(){super.onShow();touchMoving=false;moveT=0;moveKey=Game.K_NONE;motion.reset();game.resetArenaInput();}
    public WorldScreen(Game g,int generation) {
        super(g);
        mapGen=Math.max(1,Math.min(Data.BATTLE_MAX_GEN,generation));
        rng = new Rng((int) System.currentTimeMillis());
        gen();
        for (int i = 0; i < 4; i++) spawn(true);
        if(ExploreRules.legendaryActive(mapGen,System.currentTimeMillis()/1000L))spawnLegendary();
    }

    // ---- map generation --------------------------------------------------

    private int baseTile(int z) {
        return z == 2 ? T_ROCK : (z == 3 ? T_DARK : T_GRASS);
    }

    private void gen() {
        ExploreMapLayout.build(tile,zone,MW,MH,mapGen);
    }

    private boolean walkable(int x, int y) {
        return ExploreMapLayout.walkable(tile,MW,MH,x,y);
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
            int sp=pickMapWild(zone[y*MW+x]);
            if (sp < 0) continue;
            wsp[wn] = sp; wx[wn] = x; wy[wn] = y; wttl[wn] = 250 + rng.nextInt(200);
            wn++;
            return;
        }
    }

    private void spawnLegendary(){if(legendSpawned)return;if(wn>=MAXW)removeWild(wn-1);int sp=ExploreRules.pickLegendary(mapGen,rng);if(sp<0)return;Save.exploreLegendSpecies=sp;legendSp=sp;for(int tries=0;tries<100;tries++){int x=px+rng.nextInt(21)-10,y=py+rng.nextInt(17)-8;if(Math.abs(x-px)<3&&Math.abs(y-py)<3)continue;if(!walkable(x,y)||wildAt(x,y)>=0)continue;wsp[wn]=sp;wx[wn]=x;wy[wn]=y;wttl[wn]=0x3FFFFFFF;wn++;legendSpawned=true;Save.save();return;}}
    private boolean isLegend(int i){return i>=0&&i<wn&&legendSp>=0&&wsp[i]==legendSp;}
    private void removeLegendary(){for(int i=wn-1;i>=0;i--)if(isLegend(i))removeWild(i);legendSpawned=false;legendSp=-1;}
    private int pickMapWild(int z){int n=0;for(int sp=0;sp<Data.N;sp++)if(Data.isBase(sp)&&Data.generation(sp)==mapGen&&Data.category[sp]!=6&&zoneMatch(sp,z))n++;if(n==0)return -1;int q=rng.nextInt(n);for(int sp=0;sp<Data.N;sp++)if(Data.isBase(sp)&&Data.generation(sp)==mapGen&&Data.category[sp]!=6&&zoneMatch(sp,z)&&q--==0)return sp;return -1;}
    private boolean zoneMatch(int sp,int z){int t=Data.t1[sp];if(z==1)return t==Data.T_WATER||t==Data.T_AQUATIC||Data.t2[sp]==Data.T_WATER;if(z==2)return t==Data.T_ROCK||t==Data.T_GROUND||t==Data.T_FOSSIL;if(z==3)return t==Data.T_GHOST||t==Data.T_DARK||t==Data.T_PSY;return true;}

    private void removeWild(int i) {
        wn--;
        wsp[i] = wsp[wn]; wx[i] = wx[wn]; wy[i] = wy[wn]; wttl[i] = wttl[wn];
    }

    private void simStep() {
        tick++;
        for (int i = wn - 1; i >= 0; i--) {
            wttl[i]--;
            int dist = Math.max(Math.abs(wx[i] - px), Math.abs(wy[i] - py));
            if (!isLegend(i)&&(wttl[i] <= 0 || dist > 22)) { removeWild(i); continue; }
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
            ballNotice("+",BallArt.NORMAL,1);
        }
    }

    private void say(String s) {
        hud = s;hudBall=-1;
        hudT = 2500;
    }

    // ---- interaction -----------------------------------------------------

    public void update(int dt) {
        time += dt;
        if(legendWaveT>0)legendWaveT=Math.max(0,legendWaveT-dt);
        if(showQuest||legendPopup){touchMoving=false;moveT=0;motion.reset();return;}
        int key=game.heldDirection();if(key==Game.K_NONE&&(touchMoving||moveT>0))key=moveKey;
        moveT=Math.max(0,moveT-dt);motion.update(dt,dx(key),dy(key),tile,MW,MH,wx,wy,wn);
        px=motion.x/256;py=motion.y/256;playerDir=motion.dir;
        if (hudT > 0) hudT -= dt;
        acc += dt;
        while (acc >= 200) {
            acc -= 200;
            simStep();
        }
        long now=System.currentTimeMillis()/1000L;boolean active=ExploreRules.legendaryActive(mapGen,now);if(active){if(!legendSpawned)spawnLegendary();if(!legendNotice){legendNotice=true;legendPopup=true;legendWaveT=1800;say(Lang.t("Thần thú hệ ","Legendary ")+Data.TNAME[Save.exploreLegendType]+Lang.t(" đã xuất hiện! Còn 5 phút."," appeared! Five minutes remain."));}}else{if(legendSpawned)removeLegendary();legendNotice=false;}
    }

    private int nearestAdjacent() {
        int best = -1, bd = 99;
        for (int i = 0; i < wn; i++) {
            int d = Math.max(Math.abs(wx[i] - px), Math.abs(wy[i] - py));
            if (d <= 1 && d < bd) { bd = d; best = i; }
        }
        return best;
    }

    private void tryMove(int dx,int dy){moveKey=dy<0?(dx<0?Game.K_1:dx>0?Game.K_3:Game.K_UP):dy>0?(dx<0?Game.K_7:dx>0?Game.K_9:Game.K_DOWN):dx<0?Game.K_LEFT:Game.K_RIGHT;moveT=game.heldDirection()==Game.K_NONE?120:0;}
    private static int dx(int k){return k==Game.K_LEFT||k==Game.K_1||k==Game.K_7?-1:k==Game.K_RIGHT||k==Game.K_3||k==Game.K_9?1:0;}
    private static int dy(int k){return k==Game.K_UP||k==Game.K_1||k==Game.K_3?-1:k==Game.K_DOWN||k==Game.K_7||k==Game.K_9?1:0;}
    private int direction(int dx,int dy){if(dy>0){if(dx>0)return 1;if(dx<0)return 7;return 0;}if(dy<0){if(dx>0)return 3;if(dx<0)return 5;return 4;}return dx>0?2:6;}

    public void key(int k) {
        if(legendPopup){if(k==Game.K_FIRE||k==Game.K_SOFT1)legendPopup=false;return;}
        if(showQuest){if(questDetail){int type=ExploreRules.questTypeAt(mapGen,questCursor),ln=ExploreRules.legendaryCountForType(mapGen,type);if(k==Game.K_UP)questDetailTop=Math.max(0,questDetailTop-1);else if(k==Game.K_DOWN)questDetailTop=Math.min(Math.max(0,ln-1),questDetailTop+1);else if(k==Game.K_FIRE||k==Game.K_0||k==Game.K_SOFT1||k==Game.K_SOFT2){questDetail=false;resetTouchChoice();}return;}
            if(k==Game.K_0||k==Game.K_SOFT2||k==Game.K_STAR){showQuest=false;game.resetArenaInput();touchMoving=false;moveT=0;resetTouchChoice();return;}
            if(k==Game.K_LEFT||k==Game.K_RIGHT){questTab=1-questTab;resetTouchChoice();return;}
            int n=questTab==0?ExploreRules.questCount(mapGen):ExploreCaught.count(mapGen),cursor=questTab==0?questCursor:caughtCursor;
            if(k==Game.K_UP)cursor=Math.max(0,cursor-1);else if(k==Game.K_DOWN)cursor=Math.min(Math.max(0,n-1),cursor+1);else if((k==Game.K_FIRE||k==Game.K_SOFT1)&&questTab==0&&n>0){questDetail=true;questDetailTop=0;resetTouchChoice();}
            if(questTab==0)questCursor=cursor;else caughtCursor=cursor;return;}
        if(k==Game.K_STAR){showQuest=true;questTab=questCursor=caughtCursor=0;touchMoving=false;moveT=0;game.resetArenaInput();motion.reset();resetTouchChoice();return;}
        switch (k) {
            case Game.K_UP: tryMove(0, -1); break;
            case Game.K_DOWN: tryMove(0, 1); break;
            case Game.K_LEFT: tryMove(-1, 0); break;
            case Game.K_RIGHT: tryMove(1, 0); break;
            case Game.K_1: tryMove(-1,-1); break;
            case Game.K_3: tryMove(1,-1); break;
            case Game.K_7: tryMove(-1,1); break;
            case Game.K_9: tryMove(1,1); break;
            case Game.K_FIRE: {
                int w = nearestAdjacent();
                if (w < 0) say(Lang.t("Không có gì để bắt. Hãy đến gần hơn!", "Nothing to catch here. Get closer!"));
                else if (Save.balls <= 0) say(Lang.t("Hết \uE000! Hãy chơi Auto Chess để nhận thêm.", "No \uE000! Play Auto Chess to earn some."));
                else game.setScreen(new ExploreCatchBattleScreen(game, this, wsp[w], wx[w], wy[w]));
                break;
            }
            case Game.K_SOFT2:
            case Game.K_0:
                int reward=ExploreRules.tripReward(tripCaught,tripDefeated,tripLegends);Save.balls+=reward;Save.save();
                game.setScreen(new ExploreSummaryScreen(game,mapGen,tripCaught,tripDefeated,tripMissed,tripLegends,reward));
                break;
            default: break;
        }
    }

    /** called by CatchScreen when the encounter is over. */
    public void encounterDone(int x, int y, boolean remove, String msg) {
        int i=wildAt(x,y);boolean wasLegend=isLegend(i);
        if(remove||wasLegend)if(i>=0)removeWild(i);
        if(wasLegend){legendSpawned=false;legendPopup=false;legendWaveT=0;ExploreRules.clearLegendary();Save.save();}
        if (msg != null) say(msg);
    }
    public void recordCatch(int sp){tripCaught++;Save.exploreMapCaught[mapGen-1]++;if(sp>=0&&sp<Save.exploreSpeciesCaught.length)Save.exploreSpeciesCaught[sp]++;if(Data.category[sp]==6){tripLegends++;Save.exploreMapLegends[mapGen-1]++;}int milestone=ExploreRules.claimMapMilestones(mapGen);if((milestone&4)!=0)ballNotice(Lang.t("Hoàn thành map: +","Map complete: +"),BallArt.GOLD,ExploreRules.mapCompletionGold(mapGen));else if((milestone&2)!=0)ballNotice(Lang.t("Đạt 50%: +","50% complete: +"),BallArt.SILVER,2);else if((milestone&1)!=0)ballNotice(Lang.t("Đạt 25%: +","25% complete: +"),BallArt.SILVER,1);}
    private void ballNotice(String label,int type,int count){say(label);hudBall=type;hudBallCount=count;}
    public void recordDefeat(int sp){tripDefeated++;Save.exploreMapDefeated[mapGen-1]++;}
    public void recordMiss(){tripMissed++;Save.exploreMapMissed[mapGen-1]++;}
    public void legendaryUnlocked(){removeLegendary();int sp=ExploreRules.pickLegendary(mapGen,rng);Save.exploreLegendSpecies=sp;legendNotice=true;legendPopup=true;legendWaveT=1800;spawnLegendary();Save.save();}

    // ---- rendering -------------------------------------------------------

    private void drawTile(Graphics g, int tx, int ty, int sx, int sy, int ts) {
        int t = tile[ty * MW + tx];
        if(ExploreTiles.draw(g,mapGen,t,sx,sy,ts))return;
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
        int actorWorldX=motion.renderX()*ts/256,actorWorldY=motion.renderY()*ts/256;
        int camX = actorWorldX - W / 2;
        int camY = actorWorldY - H / 2;
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
        int sx = actorWorldX-camX, sy = actorWorldY-camY;
        drawProfilePokemon(g,sx,sy,ts);

        // HUD
        g.setColor(0x101820);
        g.fillRect(0, 0, W, fh*2 + 5);
        BallArt.wallet(g,3,1,Math.min(W-6,210));
        String zn = ExploreMapLayout.name(mapGen);
        Art.textSmallC(g, zn, W/2, fh+3, 0xB0F0B0);
        g.setColor(0x101820);
        g.fillRect(0, H - fh*2 - 5, W, fh*2 + 5);
        String bottom;
        if (hudT > 0) bottom = hud;
        else if (near >= 0) bottom = Data.name[wsp[near]] + " (" + Data.RARITY[Data.cost[wsp[near]]] + ") FIRE: catch";
        else bottom = Lang.t("Giữ hướng/kéo để đi; đến gần Pokémon","Hold/drag to move; approach Pokemon");
        Art.text(g, bottom, 3, H - fh*2 - 2, near >= 0 && hudT <= 0 ? 0xFFE060 : 0xE0E8FF);if(hudT>0&&hudBall>=0)BallArt.amount(g,hudBall,hudBallCount,Math.min(W-48,Art.font.stringWidth(bottom)+8),H-fh*2-2,0xFFE080);
        Art.textSmallC(g,Lang.t("*: Nhật ký","*: Journal"),W/6,H-fh-1,0x80D8FF);Art.textSmallC(g,Lang.t("5: Thu phục","5: Catch"),W/2,H-fh-1,0xFFE080);Art.textSmallC(g,Lang.t("0: Về","0: Back"),W*5/6,H-fh-1,0xB8C8DD);
        if(showQuest)paintQuest(g);if(legendPopup||legendWaveT>0)paintLegendArrival(g);
    }
    private void paintLegendArrival(Graphics g){int W=game.W,H=game.H,fh=Art.fh,cx=W/2,cy=H/2,p=1800-Math.max(0,legendWaveT);for(int i=0;i<3;i++){int q=p-i*170;if(q<0)continue;int rx=10+q/5,ry=6+q/10;g.setColor(i==0?0xFFFFFF:(i==1?0xFFD050:0x70D8FF));g.drawArc(cx-rx,cy-ry,rx*2,ry*2,0,360);g.drawArc(cx-rx-1,cy-ry,rx*2+2,ry*2,0,360);}if(!legendPopup)return;int w=Math.min(W-28,200),h=fh*6+14,x=(W-w)/2,y=(H-h)/2,type=Save.exploreLegendType,sp=Save.exploreLegendSpecies;Art.box(g,x,y,w,h,0x101827,0xFFD030);Art.textBC(g,Lang.t("THẦN THÚ XUẤT HIỆN!","LEGENDARY APPEARED!"),W/2,y+5,0xFFF080);if(sp>=0){Art.avatar(g,sp,x+8,y+fh+10);Art.textB(g,Data.name[sp],x+47,y+fh+12,0xFFFFFF);Art.typeIcon(g,type,x+47,y+fh*2+11);}Art.textSmallC(g,Lang.t("Xuất hiện ngẫu nhiên trong 5 phút","Roams this map for 5 minutes"),W/2,y+fh*4,0xC8D8E8);Art.textSmallC(g,Lang.t("Nhấn 5 để tiếp tục","Press 5 to continue"),W/2,y+h-fh-5,0xFFE080);}
    private int journalWidth(){return Math.min(game.W-12,300);}private int journalHeight(){return game.H-12;}private int journalTop(){return 6;}private int journalRow(){return questTab==0?Math.max(21,Art.fh+5):32;}private int journalListY(){return journalTop()+Art.fh*2+16;}private int journalVisible(){return Math.max(1,(journalHeight()-Art.fh*3-24)/journalRow());}
    public boolean pointer(int x,int y){
        if(legendPopup){key(Game.K_FIRE);return true;}
        if(showQuest){int w=journalWidth(),left=(game.W-w)/2,top=journalTop(),fh=Art.fh;
            if(questDetail){if(y>=game.H/2)key(Game.K_DOWN);else key(Game.K_UP);if(y>=game.H-fh*2)key(Game.K_0);return true;}
            if(y>=top+fh+7&&y<top+fh*2+11&&x>=left+5&&x<left+w-5){questTab=x<game.W/2?0:1;resetTouchChoice();return true;}
            if(y>=top+journalHeight()-fh-7){key(Game.K_0);return true;}
            int n=questTab==0?ExploreRules.questCount(mapGen):ExploreCaught.count(mapGen),cursor=questTab==0?questCursor:caughtCursor,visible=journalVisible(),first=Math.max(0,cursor-visible+1);
            if(x>=left+w-14&&y>=journalListY()&&y<top+journalHeight()-fh-7){key(y<game.H/2?Game.K_UP:Game.K_DOWN);return true;}
            int row=TouchLayout.rowAt(x,y,left+5,journalListY(),w-20,journalRow(),journalRow()-2,visible),pick=first+row;
            if(row>=0&&pick<n){if(questTab==0){questCursor=pick;if(confirmTouch(800+pick))key(Game.K_FIRE);}else caughtCursor=pick;}return true;
        }
        if(y>=game.H-Art.fh-3){key(x<game.W/3?Game.K_STAR:x<game.W*2/3?Game.K_FIRE:Game.K_0);return true;}
        if(y<Art.fh*2+5)return true;touchMoving=true;touchX=x;touchY=y;int ax=x-game.W/2,ay=y-game.H/2;moveKey=ay<-10?(ax<-10?Game.K_1:ax>10?Game.K_3:Game.K_UP):ay>10?(ax<-10?Game.K_7:ax>10?Game.K_9:Game.K_DOWN):ax<0?Game.K_LEFT:Game.K_RIGHT;return true;
    }
    public void pointerDrag(int x,int y){if(!touchMoving)return;int ax=x-touchX,ay=y-touchY;if(Math.abs(ax)<8&&Math.abs(ay)<8)return;moveKey=ay<-8?(ax<-8?Game.K_1:ax>8?Game.K_3:Game.K_UP):ay>8?(ax<-8?Game.K_7:ax>8?Game.K_9:Game.K_DOWN):ax<0?Game.K_LEFT:Game.K_RIGHT;}
    public void pointerRelease(int x,int y){touchMoving=false;moveT=0;moveKey=Game.K_NONE;motion.reset();}
    private void paintQuest(Graphics g){int W=game.W,fh=Art.fh,w=journalWidth(),h=journalHeight(),x=(W-w)/2,y=journalTop(),rowH=journalRow(),n=questTab==0?ExploreRules.questCount(mapGen):ExploreCaught.count(mapGen),visible=journalVisible();
        Art.box(g,x,y,w,h,0x101827,0xFFD030);Art.textSmallC(g,Lang.t("NHẬT KÝ THU PHỤC - GEN ","CATCH JOURNAL - GEN ")+mapGen,W/2,y+4,0xFFD030);
        for(int tab=0;tab<2;tab++){int tx=x+5+tab*(w-10)/2,tw=(w-10)/2;g.setColor(tab==questTab?0x34568A:0x202D43);g.fillRect(tx,y+fh+7,tw,fh+4);Art.textSmallC(g,tab==0?Lang.t("Nhiệm vụ","Quests"):Lang.t("Pokémon đã bắt","Caught Pokemon"),tx+tw/2,y+fh+9,tab==questTab?0xFFFFFF:0x9FAFC5);}
        int cursor=questTab==0?questCursor:caughtCursor;if(cursor>=n)cursor=Math.max(0,n-1);if(questTab==0)questCursor=cursor;else caughtCursor=cursor;int first=Math.max(0,cursor-visible+1);long now=System.currentTimeMillis()/1000L;
        if(n==0)Art.textSmallC(g,Lang.t("Chưa bắt Pokémon nào trong map này.","No Pokemon caught on this map yet."),W/2,journalListY()+9,0xA8BACF);
        for(int row=0;row<visible&&first+row<n;row++){int i=first+row,yy=journalListY()+row*rowH;g.setColor(i==cursor?0x304C78:0x17253B);g.fillRect(x+5,yy,w-20,rowH-2);
            if(questTab==1){int sp=ExploreCaught.at(mapGen,i);Art.avatarMini(g,sp,x+9,yy+4);Art.textSmall(g,Data.name[sp],x+34,yy+2,0xFFFFFF);Art.typeIcon(g,Data.t1[sp],x+34,yy+14);if(Data.t2[sp]>=0)Art.typeIcon(g,Data.t2[sp],x+51,yy+14);Art.textSmallR(g,"x"+Save.exploreSpeciesCaught[sp],x+w-20,yy+8,0x90E8A0);}
            else{int type=ExploreRules.questTypeAt(mapGen,i);boolean active=Save.exploreLegendType==type&&ExploreRules.legendaryActive(mapGen,now);int p=active?10:ExploreRules.questProgress(mapGen,type);Art.typeIcon(g,type,x+8,yy+2);Art.textSmall(g,Lang.typeName(type),x+28,yy+4,0xFFFFFF);int bw=Math.max(30,w-122),bx=x+w-bw-19;Art.bar(g,bx,yy+8,bw,7,p,10,active?0xFFD030:Data.TCOL[type]);Art.textSmallR(g,active?countdown(Math.max(0,Save.exploreLegendUntil-now)):p+"/10",x+w-20,yy+3,0xFFFFFF);}
        }
        if(n>visible){int track=visible*rowH,thumb=Math.max(8,track*visible/n),offset=(track-thumb)*first/Math.max(1,n-visible);g.setColor(0x30415B);g.fillRect(x+w-10,journalListY(),3,track);g.setColor(0x80D8FF);g.fillRect(x+w-10,journalListY()+offset,3,thumb);Art.textSmall(g,"^",x+w-13,journalListY()-10,0x80D8FF);Art.textSmall(g,"v",x+w-13,journalListY()+track-5,0x80D8FF);}
        Art.textSmallC(g,questTab==0?Lang.t("4/6: tab  2/8: chọn  5: xem  0: đóng","4/6: tab  2/8: select  5: view  0: close"):Lang.t("4/6: tab  2/8: cuộn  0: đóng","4/6: tab  2/8: scroll  0: close"),W/2,y+h-fh-3,0x8090B0);if(questDetail)paintQuestDetail(g);
    }
    private String countdown(long seconds){long m=seconds/60,s=seconds%60;return m+":"+(s<10?"0":"")+s;}
    private void paintQuestDetail(Graphics g){int W=game.W,H=game.H,fh=Art.fh,type=ExploreRules.questTypeAt(mapGen,questCursor),n=ExploreRules.legendaryCountForType(mapGen,type),w=Math.min(W-12,220),h=Math.min(H-12,fh*10+24),x=(W-w)/2,y=(H-h)/2,rowH=23,listY=y+fh+9,footerH=fh*4+9,visible=Math.max(1,(h-footerH-(listY-y))/rowH);if(questDetailTop>Math.max(0,n-visible))questDetailTop=Math.max(0,n-visible);Art.box(g,x,y,w,h,0x111A2C,Data.TCOL[type]);Art.textBC(g,Lang.t("THẦN THÚ CÓ THỂ XUẤT HIỆN","POSSIBLE LEGENDARIES"),W/2,y+4,0xFFE060);for(int i=0;i<visible&&questDetailTop+i<n;i++){int sp=ExploreRules.legendaryAt(mapGen,type,questDetailTop+i),yy=listY+i*rowH;g.setColor((i&1)==0?0x1B2940:0x172338);g.fillRect(x+5,yy,w-10,rowH-1);Art.avatarMini(g,sp,x+8,yy+1);Art.textB(g,Data.name[sp],x+31,yy+2,0xFFFFFF);Art.typeIcon(g,Data.t1[sp],x+31,yy+fh+1);if(Data.t2[sp]>=0)Art.typeIcon(g,Data.t2[sp],x+48,yy+fh+1);}int fy=y+h-footerH,p=(Save.exploreLegendType==type&&ExploreRules.legendaryActive(mapGen,System.currentTimeMillis()/1000L))?10:ExploreRules.questProgress(mapGen,type);g.setColor(0x0D1625);g.fillRect(x+5,fy,w-10,footerH-5);Art.textSmallC(g,Lang.t("Bắt hoặc hạ 10 Pokémon hệ ","Catch or defeat 10 ")+Data.TNAME[type],W/2,fy+3,0xD0D8E8);Art.textSmallC(g,Lang.t("Thần thú xuất hiện ngẫu nhiên trong 5 phút","Legendary roams randomly for 5 minutes"),W/2,fy+fh+3,0xA8B8D0);Art.textSmallC(g,p+"/10   "+Lang.t("Lên/xuống: cuộn","Up/down: scroll"),W/2,fy+fh*2+3,0x90E8A0);Art.textSmallC(g,Lang.t("FIRE/0: đóng","FIRE/0: close"),W/2,fy+fh*3+3,0xFFE080);}
    /** Same directional atlases, ground anchor and distance-based walk cadence as Uprising. */
    static int profileSpecies(){int sp=Data.speciesForDex(Save.profileAvatarDex);if(sp>=0&&Save.usable(sp))return sp;if(Save.hero>=0&&Save.usable(Save.hero))return Save.hero;for(int i=0;i<Data.N;i++)if(Save.usable(i))return i;return 0;}
    private void drawProfilePokemon(Graphics g,int x,int y,int size){int sp=profileSpecies(),action=motion.moving?RawAtlas.WALK:RawAtlas.IDLE,phase=motion.animationClock(size),box=36,di=Data.collectionIndex(sp);boolean later=sp>=Data.CORE_N;boolean bounded=later?CollectionAtlas.bounds(di,0,0,box,box,action,playerDir,phase,walkBounds):RawAtlas.bounds(sp,0,0,box,box,action,playerDir,phase,walkBounds);int sw=bounded?Math.max(12,Math.min(24,walkBounds[2])):16;g.setColor(0x345032);g.fillArc(x-sw/2,y-2,sw,5,0,360);int ax=bounded?x-walkBounds[0]-walkBounds[2]/2:x-box/2,ay=bounded?y-walkBounds[1]-walkBounds[3]:y-32;boolean drawn=later?CollectionAtlas.draw(g,di,ax,ay,box,box,action,playerDir,phase):RawAtlas.draw(g,sp,ax,ay,box,box,action,playerDir,phase);if(!drawn)Art.sprite(g,sp,x-12,y-24,24);}
}


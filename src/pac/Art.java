package pac;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Drawing helpers. Creatures are drawn procedurally (shape + colours from their types) so the
 * jar stays tiny. If /sp/<index>.png exists inside the jar it is used instead (index = species
 * index in Data, 0..40), scaled at run time to whatever size the screen needs. Made by
 * tools/make_sprites.py.
 */
public final class Art {
    private Art() {}

    public static Font font, fontB, fontSmall;
    private static PixelFont pixel,pixelB;
    public static int fh = 12;
    private static Image[] png = new Image[Data.N];
    private static boolean[] tried = new boolean[Data.N];
    /** cache of the source image scaled to the size it was last drawn at */
    private static Image[] scl = new Image[Data.N];
    private static int[] sclS = new int[Data.N];
    private static Image[] skill = new Image[Data.ABIL_NAME.length];
    private static boolean[] skillTried = new boolean[Data.ABIL_NAME.length];
    private static Image[] status = new Image[16];
    private static boolean[] statusTried = new boolean[16];
    private static Image[] speciesSkill = new Image[Data.MAX];
    private static boolean[] speciesSkillTried = new boolean[Data.MAX];
    private static Image[] typeIcon = new Image[Data.NT];
    private static boolean[] typeIconTried = new boolean[Data.NT];
    private static Image[] uiIcon = new Image[12];
    private static boolean[] uiIconTried = new boolean[12];
    private static Image[][] attackFx = new Image[Data.NT][3];
    private static boolean[][] attackFxTried = new boolean[Data.NT][3];
    private static Image[] avatar = new Image[Data.N];
    private static boolean[] avatarTried = new boolean[Data.N];
    private static Image[] avatarMini = new Image[Data.N];
    private static Image[] avatarMiniGray = new Image[Data.N];
    private static Image[] avatarTiny = new Image[Data.N];
    private static Image[] avatarDock = new Image[Data.N];
    private static Image[] avatarHistory = new Image[Data.N];
    private static Image[] dexAvatar = new Image[CollectionDex.COUNT];
    private static boolean[] dexAvatarTried = new boolean[CollectionDex.COUNT];
    private static Image[] itemIcon = new Image[ItemData.ID.length];
    private static boolean[] itemIconTried = new boolean[ItemData.ID.length];
    private static Image[] itemIconTiny = new Image[ItemData.ID.length];
    private static boolean[] itemIconTinyTried = new boolean[ItemData.ID.length];
    private static Image[] itemIconMedium = new Image[ItemData.ID.length];
    private static boolean[] itemIconMediumTried = new boolean[ItemData.ID.length];
    private static final int[] RARITY_COLOR={0xA0A0A0,0x3BC95E,0x41BFCC,0x927FFF,0xE53B3B,0xFFFFFF,0xE6CB49,0xE58EE5};
    private static final int TL = Graphics.TOP | Graphics.LEFT;

    public static void init() {
        font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        fontB = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
        fontSmall = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        // NRO menu text uses the 7b face: visibly larger/thicker than tahoma_7,
        // while still much more compact than the emulator's MIDP system font.
        pixel=new PixelFont("tahoma_7b",true);
        pixelB=pixel;
        fh = pixel.height();
    }

    public static int dark(int c) {
        return (c >> 1) & 0x7F7F7F;
    }

    public static int light(int c) {
        int r = (c >> 16) & 255, g = (c >> 8) & 255, b = c & 255;
        r += (255 - r) / 2; g += (255 - g) / 2; b += (255 - b) / 2;
        return (r << 16) | (g << 8) | b;
    }

    /** Rarity palette copied from app.zip public/src/style/colors.css. */
    public static int rarityColor(int category) {
        if(category<0||category>=RARITY_COLOR.length)return RARITY_COLOR[0];
        return RARITY_COLOR[category];
    }

    // ---- text ------------------------------------------------------------

    public static void text(Graphics g, String s, int x, int y, int col) {
        pixel.draw(g,s,x,y,col);
    }

    public static void textB(Graphics g, String s, int x, int y, int col) {
        pixelB.draw(g,s,x,y,col);
    }

    public static void textC(Graphics g, String s, int cx, int y, int col) {
        int w = textWidth(s);
        text(g, s, cx - w / 2, y, col);
    }

    public static void textBC(Graphics g, String s, int cx, int y, int col) {
        int w = boldWidth(s);
        textB(g, s, cx - w / 2, y, col);
    }

    public static void textR(Graphics g, String s, int rx, int y, int col) {
        text(g, s, rx - textWidth(s), y, col);
    }

    /** Compact unshadowed text for dense stat panels, matching NRO-style information rows. */
    public static void textSmall(Graphics g, String s, int x, int y, int col) {
        pixel.draw(g,s,x,y,col);
    }

    public static void textSmallC(Graphics g, String s, int cx, int y, int col) {
        textSmall(g,s,cx-smallWidth(s)/2,y,col);
    }

    public static void textSmallR(Graphics g, String s, int rx, int y, int col) {
        textSmall(g,s,rx-smallWidth(s),y,col);
    }

    public static int textWidth(String s){return pixel!=null?pixel.width(s):font.stringWidth(s);}
    public static int boldWidth(String s){return pixelB!=null?pixelB.width(s):fontB.stringWidth(s);}
    public static int smallWidth(String s){return textWidth(s);}

    /** word-wraps s into at most maxLines lines that fit maxW pixels. */
    public static String[] wrap(String s, int maxW, int maxLines) {
        String[] out = new String[maxLines];
        int n = 0;
        StringBuffer line = new StringBuffer();
        int i = 0, len = s.length();
        while (i <= len && n < maxLines) {
            int j = s.indexOf(' ', i);
            if (j < 0) j = len;
            String word = s.substring(i, j);
            String cand = line.length() == 0 ? word : line.toString() + " " + word;
            if (line.length() == 0 || textWidth(cand) <= maxW) {
                line.setLength(0);
                line.append(cand);
            } else {
                out[n++] = line.toString();
                line.setLength(0);
                line.append(word);
            }
            i = j + 1;
        }
        if (line.length() > 0 && n < maxLines) out[n++] = line.toString();
        String[] r = new String[n];
        System.arraycopy(out, 0, r, 0, n);
        return r;
    }

    /** draws wrapped text, returns y after the last line. */
    public static int para(Graphics g, String s, int x, int y, int w, int col, int maxLines) {
        String[] l = wrap(s, w, maxLines);
        for (int i = 0; i < l.length; i++) {
            text(g, l[i], x, y, col);
            y += fh;
        }
        return y;
    }

    // ---- shapes ----------------------------------------------------------

    public static void bar(Graphics g, int x, int y, int w, int h, int val, int max, int col) {
        g.setColor(0x000000);
        g.fillRect(x, y, w, h);
        if (max <= 0) return;
        int fw = (w - 2) * val / max;
        if (val > 0 && fw < 1) fw = 1;
        if (h > 2) {
            g.setColor(col);
            g.fillRect(x + 1, y + 1, fw, h - 2);
        } else {
            g.setColor(col);
            g.fillRect(x, y, w * val / max, h);
        }
    }

    /** Progress bar whose fill grows from right to left. */
    public static void barReverse(Graphics g, int x, int y, int w, int h, int val, int max, int col) {
        g.setColor(0x000000);
        g.fillRect(x, y, w, h);
        if (max <= 0) return;
        int fw = (w - 2) * val / max;
        if (val > 0 && fw < 1) fw = 1;
        if (h > 2) {
            g.setColor(col);
            g.fillRect(x + w - 1 - fw, y + 1, fw, h - 2);
        } else {
            fw = w * val / max;
            g.setColor(col);
            g.fillRect(x + w - fw, y, fw, h);
        }
    }

    public static void box(Graphics g, int x, int y, int w, int h, int fill, int border) {
        g.setColor(fill);
        g.fillRect(x, y, w, h);
        g.setColor(border);
        g.drawRect(x, y, w - 1, h - 1);
    }

    public static int chip(Graphics g, int type, int x, int y) {
        String s = Lang.typeShort(type);
        int w = textWidth(s) + 4;
        g.setColor(Data.TCOL[type]);
        g.fillRect(x, y, w, fh);
        g.setColor(0x000000);
        pixel.draw(g,s,x+2,y,0x000000);
        return w;
    }

    /** Fixed 35px source-style HP/PP bar, scaled from the web client's 70px bar. */
    public static void battleBar(Graphics g, int cx, int y, int hp, int maxHp,
                                 int shield, int pp, int maxPp, boolean ally) {
        final int w=35, inner=w-2, hpH=5;
        int x=cx-w/2;
        g.setColor(0x000000); g.fillRect(x,y,w,maxPp>0?10:7);
        g.setColor(0x303030); g.fillRect(x+1,y+1,inner,hpH);
        if(maxHp>0&&hp>0){
            int total=Math.max(maxHp,hp+shield);
            int hpW=inner*hp/total;
            g.setColor(ally?0x76C442:0xE76E55); g.fillRect(x+1,y+1,hpW,hpH);
            if(shield>0){
                int sw=inner*shield/total;if(sw>inner-hpW)sw=inner-hpW;
                g.setColor(0xE0E0E0);g.fillRect(x+1+hpW,y+1,sw,hpH);
            }
            g.setColor(0x303030);
            for(int v=25;v<total;v+=25){int sx=x+1+inner*v/total;g.drawLine(sx,y+1,sx,y+4);}
        }
        if(maxPp>0){
            g.setColor(0x282828);g.fillRect(x+1,y+7,inner,2);
            g.setColor(0x209CEE);g.fillRect(x+1,y+7,inner*pp/maxPp,2);
        }
    }

    // ---- creatures -------------------------------------------------------

    private static Image loadPng(int sp) {
        if (!tried[sp]) {
            tried[sp] = true;
            try {
                png[sp] = Image.createImage("/sp/" + sp + ".png");
            } catch (Exception e) {
                png[sp] = null;
            }
        }
        return png[sp];
    }

    /**
     * the bundled sprite for sp scaled to s x s (square, transparent, bottom aligned by the
     * converter), or null when there is no png. Box filter when shrinking, nearest when growing.
     */
    private static Image scaled(int sp, int s) {
        if (s < 4) return null;
        Image im = loadPng(sp);
        if (im == null) return null;
        int w = im.getWidth(), h = im.getHeight();
        if (w == s && h == s) return im;
        if (scl[sp] != null && sclS[sp] == s) return scl[sp];
        try {
            int[] src = new int[w * h];
            im.getRGB(src, 0, w, 0, 0, w, h);
            int[] dst = new int[s * s];
            int tw=s,th=s;
            if(w>h)th=Math.max(1,s*h/w); else tw=Math.max(1,s*w/h);
            int ox=(s-tw)/2,oy=s-th;
            for (int y = 0; y < th; y++) {
                int y0 = y * h / th;
                int y1 = (y + 1) * h / th;
                if (y1 <= y0) y1 = y0 + 1;
                for (int x = 0; x < tw; x++) {
                    int x0 = x * w / tw;
                    int x1 = (x + 1) * w / tw;
                    if (x1 <= x0) x1 = x0 + 1;
                    int n = 0, sa = 0, sr = 0, sg = 0, sb = 0;
                    for (int yy = y0; yy < y1; yy++) {
                        for (int xx = x0; xx < x1; xx++) {
                            int c = src[yy * w + xx];
                            int a = (c >>> 24) & 0xFF;
                            n++;
                            sa += a;
                            sr += ((c >> 16) & 0xFF) * a;
                            sg += ((c >> 8) & 0xFF) * a;
                            sb += (c & 0xFF) * a;
                        }
                    }
                    int o = 0;
                    if (sa > 0) {
                        int a = sa / n;
                        // keep edges crisp: half-covered pixels become opaque
                        a = a >= 96 ? 255 : 0;
                        o = (a << 24) | ((sr / sa) << 16) | ((sg / sa) << 8) | (sb / sa);
                    }
                    dst[(y+oy) * s + x+ox] = o;
                }
            }
            scl[sp] = Image.createRGBImage(dst, s, s, true);
            sclS[sp] = s;
            return scl[sp];
        } catch (Exception e) {
            return null;
        }
    }

    /** draws species sp inside the square (x, y, s, s). */
    public static void sprite(Graphics g, int sp, int x, int y, int s) {
        Image im = scaled(sp, s);
        if (im != null) {
            g.drawImage(im, x, y, TL);
            return;
        }
        int a = Data.t1[sp], b = Data.t2[sp];
        int c1 = Data.TCOL[a];
        int c2 = b >= 0 ? Data.TCOL[b] : light(c1);
        int dk = dark(c1);
        int tr = Data.tier[sp];
        int cx = x + s / 2;
        int bw = s * (5 + tr) / 10;
        int bh = bw * 9 / 10;
        int bx = cx - bw / 2;
        int by = y + s - bh - 1;
        int hw = s * 4 / 10 + tr * s / 20;
        int hh = hw * 9 / 10;
        int hx = cx - hw / 2;
        int hy = by - hh / 3;
        int style = sp % 4;
        boolean flying = a == Data.T_FLY || b == Data.T_FLY;

        if (flying) {
            g.setColor(c2);
            g.fillTriangle(bx + bw / 5, by + bh / 2, bx - s / 6, by + bh / 8, bx + bw / 5, by + bh * 4 / 5);
            g.fillTriangle(bx + bw * 4 / 5, by + bh / 2, bx + bw + s / 6, by + bh / 8, bx + bw * 4 / 5, by + bh * 4 / 5);
        }
        if (a == Data.T_DRAGON || a == Data.T_FIRE || style == 1) {
            g.setColor(dk);
            g.fillTriangle(bx + bw - s / 10, by + bh * 2 / 3, bx + bw + s / 5, by + bh / 3, bx + bw - s / 10, by + bh - 1);
        }
        // body
        g.setColor(c1);
        g.fillArc(bx, by, bw, bh, 0, 360);
        g.setColor(dk);
        g.drawArc(bx, by, bw, bh, 0, 360);
        if (s >= 14) {
            g.setColor(c2);
            g.fillArc(cx - bw / 4, by + bh / 3, bw / 2, bh / 2, 0, 360);
        }
        // ears / horn / crest
        g.setColor(style == 1 ? light(c2) : c1);
        if (style == 0) {
            g.fillTriangle(hx, hy + hh / 4, hx + hw / 4, hy - hh / 3, hx + hw / 2 - 1, hy + hh / 6);
            g.fillTriangle(hx + hw, hy + hh / 4, hx + hw * 3 / 4, hy - hh / 3, hx + hw / 2 + 1, hy + hh / 6);
        } else if (style == 1) {
            g.fillTriangle(cx - hw / 6, hy + hh / 5, cx, hy - hh / 2, cx + hw / 6, hy + hh / 5);
        } else if (style == 2) {
            int er = Math.max(2, hw / 4);
            g.fillArc(hx - 1, hy - er / 2, er, er, 0, 360);
            g.fillArc(hx + hw - er + 1, hy - er / 2, er, er, 0, 360);
        } else {
            g.setColor(c2);
            int sw = Math.max(1, s / 12);
            g.fillRect(cx - sw / 2, hy - s / 8, sw, s / 8 + 1);
            g.fillRect(cx - sw / 2 - sw * 2, hy - s / 12, sw, s / 12 + 1);
            g.fillRect(cx - sw / 2 + sw * 2, hy - s / 12, sw, s / 12 + 1);
        }
        // head
        g.setColor(c1);
        g.fillArc(hx, hy, hw, hh, 0, 360);
        g.setColor(dk);
        g.drawArc(hx, hy, hw, hh, 0, 360);
        // eyes
        int ex = Math.max(1, hw / 6);
        int ey = hy + hh / 3;
        g.setColor(0xFFFFFF);
        g.fillRect(cx - hw / 4 - ex / 2, ey, ex + 1, ex + 1);
        g.fillRect(cx + hw / 4 - ex / 2, ey, ex + 1, ex + 1);
        if (ex >= 2) {
            g.setColor(0x000000);
            g.fillRect(cx - hw / 4, ey + 1, ex / 2 + 1, ex / 2 + 1);
            g.fillRect(cx + hw / 4, ey + 1, ex / 2 + 1, ex / 2 + 1);
        }
        if (a == Data.T_GHOST) {
            g.setColor(0xFF4040);
            g.fillRect(cx - hw / 6, hy + hh * 2 / 3, hw / 3 + 1, Math.max(1, s / 16));
        }
    }

    /** Draws a stage-sized frame. state: 0 idle, 1 attack, 2 victory/hop. */
    public static void battleSprite(Graphics g, int sp, int x, int y, int frame, int state, int direction) {
        if (RawAtlas.draw(g, sp, x, y, Data.visualWidth(sp), Data.visualHeight(sp),
                          state, direction, frame)) return;
        int w = Data.visualWidth(sp), h = Data.visualHeight(sp);
        int s = Math.max(w, h);
        sprite(g, sp, x + (w - s) / 2, y + h - s, s);
    }

    /** Board/bench placement preview with visible-body centring for raw atlases. */
    public static void formationSprite(Graphics g,int sp,int x,int y,int w,int h,int screenW,int frame){
        // Direction 7 is DOWN-LEFT in the original atlas direction table.
        if(RawAtlas.drawFormation(g,sp,x,y,w,h,screenW,7,frame))return;
        int vw=Data.visualWidth(sp),vh=Data.visualHeight(sp);
        int px=x+(w-vw)/2;
        if(px<0)px=0;
        if(px+vw>screenW)px=screenW-vw;
        battleSprite(g,sp,px,y+h-vh-2,frame,RawAtlas.IDLE,7);
    }

    public static void skillSprite(Graphics g, int ability, int x, int y, int frame) {
        if (!skillTried[ability]) {
            skillTried[ability] = true;
            try { skill[ability] = Image.createImage("/fx/" + ability + ".png"); }
            catch (Exception e) { skill[ability] = null; }
        }
        if (skill[ability] != null)
            g.drawRegion(skill[ability], (frame & 3) * 32, 0, 32, 32, 0, x, y, TL);
    }

    /** Eight-frame ability animation selected by Pokemon, sourced from app.zip. */
    public static void speciesSkillSprite(Graphics g, int sp, int x, int y, int frame) {
        if (sp < 0 || sp >= Data.MAX) return;
        if (!speciesSkillTried[sp]) {
            speciesSkillTried[sp] = true;
            try { speciesSkill[sp] = Image.createImage("/sfx/" + sp + ".png"); }
            catch (Exception e) { speciesSkill[sp] = null; }
        }
        if (speciesSkill[sp] != null)
            g.drawRegion(speciesSkill[sp], (frame & 7) * 32, 0, 32, 32, 0, x, y, TL);
        else skillSprite(g, Data.abil[sp], x, y, frame);
    }

    public static void typeIcon(Graphics g,int type,int x,int y){
        if(type<0||type>=Data.NT)return;
        if(!typeIconTried[type]){
            typeIconTried[type]=true;
            try{typeIcon[type]=Image.createImage("/type/"+Data.TNAME[type].toUpperCase()+".png");}
            catch(Exception e){typeIcon[type]=null;}
        }
        if(typeIcon[type]!=null)g.drawImage(typeIcon[type],x,y,TL);
        else{g.setColor(Data.TCOL[type]);g.fillArc(x,y,14,14,0,360);}
    }

    /** UI ids: 0 gold, 1 heart, 2 ball, 3 reroll, 4/5 lock, 6 dex, 7 xp, 8 atk, 9 def, 10 hp, 11 shield. */
    public static void uiIcon(Graphics g,int id,int x,int y){
        if(id<0||id>=uiIcon.length)return;
        if(!uiIconTried[id]){
            uiIconTried[id]=true;
            try{uiIcon[id]=Image.createImage("/ui/"+id+".png");}catch(Exception e){uiIcon[id]=null;}
        }
        if(uiIcon[id]!=null){g.drawImage(uiIcon[id],x,y,TL);return;}
        g.setColor(id==0?0xFFD22E:id==3?0x38A8E8:0xD8E0F0);
        if(id==0){g.fillArc(x+2,y+2,10,10,0,360);g.setColor(0x8A5A00);g.drawArc(x+2,y+2,10,10,0,360);}
        else if(id==3){g.drawArc(x+1,y+1,11,11,35,285);g.fillTriangle(x+10,y,x+14,y+3,x+9,y+5);}
        else{g.drawRect(x+2,y+2,9,9);}
    }

    public static void itemIcon(Graphics g,int id,int x,int y){
        if(id<0||id>=itemIcon.length)return;
        if(!itemIconTried[id]){
            itemIconTried[id]=true;
            try{
                Image src=Image.createImage("/item/"+ItemData.ID[id]+".png");
                int sw=src.getWidth(),sh=src.getHeight(),s=24;
                if(sw==s&&sh==s)itemIcon[id]=src;
                else{
                    int[] in=new int[sw*sh],out=new int[s*s];src.getRGB(in,0,sw,0,0,sw,sh);
                    for(int yy=0;yy<s;yy++)for(int xx=0;xx<s;xx++)out[yy*s+xx]=in[(yy*sh/s)*sw+xx*sw/s];
                    itemIcon[id]=Image.createRGBImage(out,s,s,true);
                }
            }catch(Exception e){itemIcon[id]=null;}
        }
        if(itemIcon[id]!=null)g.drawImage(itemIcon[id],x,y,TL);
        else{g.setColor(0x506078);g.fillRect(x,y,24,24);textC(g,"?",x+12,y+7,0xFFFFFF);}
    }

    /** Eight-pixel nearest-neighbour icon used on formation units and compact cards. */
    public static void itemIconTiny(Graphics g,int id,int x,int y){
        if(id<0||id>=itemIconTiny.length)return;
        if(!itemIconTinyTried[id]){
            itemIconTinyTried[id]=true;
            try{
                Image src=Image.createImage("/item/"+ItemData.ID[id]+".png");
                int sw=src.getWidth(),sh=src.getHeight(),s=8;
                int[] in=new int[sw*sh],out=new int[s*s];src.getRGB(in,0,sw,0,0,sw,sh);
                for(int yy=0;yy<s;yy++)for(int xx=0;xx<s;xx++)out[yy*s+xx]=in[(yy*sh/s)*sw+xx*sw/s];
                itemIconTiny[id]=Image.createRGBImage(out,s,s,true);
            }catch(Exception e){itemIconTiny[id]=null;}
        }
        if(itemIconTiny[id]!=null)g.drawImage(itemIconTiny[id],x,y,TL);
        else{g.setColor(0xFFD060);g.fillRect(x+1,y+1,6,6);}
    }

    /** Sixteen-pixel nearest-neighbour icon used by the item recipe preview. */
    public static void itemIconMedium(Graphics g,int id,int x,int y){
        if(id<0||id>=itemIconMedium.length)return;
        if(!itemIconMediumTried[id]){
            itemIconMediumTried[id]=true;
            try{
                Image src=Image.createImage("/item/"+ItemData.ID[id]+".png");
                int sw=src.getWidth(),sh=src.getHeight(),s=16;
                int[] in=new int[sw*sh],out=new int[s*s];src.getRGB(in,0,sw,0,0,sw,sh);
                for(int yy=0;yy<s;yy++)for(int xx=0;xx<s;xx++)out[yy*s+xx]=in[(yy*sh/s)*sw+xx*sw/s];
                itemIconMedium[id]=Image.createRGBImage(out,s,s,true);
            }catch(Exception e){itemIconMedium[id]=null;}
        }
        if(itemIconMedium[id]!=null)g.drawImage(itemIconMedium[id],x,y,TL);
        else{g.setColor(0xFFD060);g.fillRect(x+1,y+1,14,14);}
    }

    public static void statusSprite(Graphics g, int id, int x, int y, int frame) {
        if (id < 0 || id >= status.length) return;
        if (!statusTried[id]) {
            statusTried[id]=true;
            try { status[id]=Image.createImage("/status/"+id+".png"); } catch(Exception e) { status[id]=null; }
        }
        if (status[id]!=null) g.drawRegion(status[id],(frame&3)*16,0,16,16,0,x,y,TL);
    }

    public static boolean attackSprite(Graphics g,int type,int kind,int x,int y,int frame) {
        if (type<0 || type>=Data.NT || kind<0 || kind>2) return false;
        if (!attackFxTried[type][kind]) {
            attackFxTried[type][kind]=true;
            try { attackFx[type][kind]=Image.createImage("/attack/"+type+"_"+kind+".png"); }
            catch(Exception e) { attackFx[type][kind]=null; }
        }
        Image im=attackFx[type][kind];
        if(im==null)return false;
        g.drawRegion(im,(frame&3)*16,0,16,16,0,x-8,y-8,TL);
        return true;
    }

    public static void avatar(Graphics g, int sp, int x, int y) {
        if (!avatarTried[sp]) {
            avatarTried[sp] = true;
            try { avatar[sp] = Image.createImage("/av/" + sp + ".png"); }
            catch (Exception e) { avatar[sp] = null; }
        }
        if (avatar[sp] != null) g.drawImage(avatar[sp], x, y, TL);
        else sprite(g, sp, x, y, 32);
    }

    /** 20px cached portrait used by the compact 3x3 combat statistics panel. */
    public static void avatarMini(Graphics g,int sp,int x,int y){
        if(avatarMini[sp]==null){
            if(!avatarTried[sp]){
                avatarTried[sp]=true;
                try{avatar[sp]=Image.createImage("/av/"+sp+".png");}catch(Exception e){avatar[sp]=null;}
            }
            Image im=avatar[sp];
            if(im!=null)try{
                int sw=im.getWidth(),sh=im.getHeight(),s=20;
                int[] src=new int[sw*sh],dst=new int[s*s];
                im.getRGB(src,0,sw,0,0,sw,sh);
                for(int yy=0;yy<s;yy++)for(int xx=0;xx<s;xx++)dst[yy*s+xx]=src[(yy*sh/s)*sw+xx*sw/s];
                avatarMini[sp]=Image.createRGBImage(dst,s,s,true);
            }catch(Exception e){avatarMini[sp]=null;}
        }
        if(avatarMini[sp]!=null)g.drawImage(avatarMini[sp],x,y,TL);else sprite(g,sp,x,y,20);
    }

    /** 22px portrait for the preparation equipment target row. */
    public static void avatarDock(Graphics g,int sp,int x,int y){
        if(avatarDock[sp]==null){
            if(!avatarTried[sp]){avatarTried[sp]=true;try{avatar[sp]=Image.createImage("/av/"+sp+".png");}catch(Exception e){avatar[sp]=null;}}
            Image im=avatar[sp];
            if(im!=null)try{
                int sw=im.getWidth(),sh=im.getHeight(),s=22;int[] src=new int[sw*sh],dst=new int[s*s];
                im.getRGB(src,0,sw,0,0,sw,sh);
                for(int yy=0;yy<s;yy++)for(int xx=0;xx<s;xx++)dst[yy*s+xx]=src[(yy*sh/s)*sw+xx*sw/s];
                avatarDock[sp]=Image.createRGBImage(dst,s,s,true);
            }catch(Exception e){avatarDock[sp]=null;}
        }
        if(avatarDock[sp]!=null)g.drawImage(avatarDock[sp],x,y,TL);else sprite(g,sp,x,y,22);
    }

    /** 24px portrait used by history rows on screens wide enough for nine slots. */
    public static void avatarHistory(Graphics g,int sp,int x,int y){
        if(avatarHistory[sp]==null){
            if(!avatarTried[sp]){avatarTried[sp]=true;try{avatar[sp]=Image.createImage("/av/"+sp+".png");}catch(Exception e){avatar[sp]=null;}}
            Image im=avatar[sp];
            if(im!=null)try{
                int sw=im.getWidth(),sh=im.getHeight(),s=24;int[] src=new int[sw*sh],dst=new int[s*s];
                im.getRGB(src,0,sw,0,0,sw,sh);
                for(int yy=0;yy<s;yy++)for(int xx=0;xx<s;xx++)dst[yy*s+xx]=src[(yy*sh/s)*sw+xx*sw/s];
                avatarHistory[sp]=Image.createRGBImage(dst,s,s,true);
            }catch(Exception e){avatarHistory[sp]=null;}
        }
        if(avatarHistory[sp]!=null)g.drawImage(avatarHistory[sp],x,y,TL);else sprite(g,sp,x,y,24);
    }

    /** Collection-only portrait for Gen 4-9; index belongs to CollectionDex, not Battle Data. */
    public static void dexAvatar(Graphics g,int index,int x,int y){
        if(index<0||index>=CollectionDex.COUNT)return;
        if(!dexAvatarTried[index]){
            dexAvatarTried[index]=true;
            try{dexAvatar[index]=Image.createImage("/dex/"+CollectionDex.DEX[index]+".png");}
            catch(Exception e){dexAvatar[index]=null;}
        }
        if(dexAvatar[index]!=null)g.drawImage(dexAvatar[index],x,y,TL);
        else{g.setColor(0x40506A);g.fillRect(x,y,32,32);textC(g,"?",x+16,y+9,0xD8E0F0);}
    }

    /** Gray 20px portrait for defeated units; generated once and then cached. */
    public static void avatarMiniGray(Graphics g,int sp,int x,int y){
        if(avatarMini[sp]==null)avatarMini(g,sp,-100,-100);
        if(avatarMiniGray[sp]==null&&avatarMini[sp]!=null)try{
            int s=20;int[] px=new int[s*s];avatarMini[sp].getRGB(px,0,s,0,0,s,s);
            for(int i=0;i<px.length;i++){
                int a=px[i]&0xFF000000,r=(px[i]>>16)&255,gg=(px[i]>>8)&255,b=px[i]&255;
                int v=(r*30+gg*59+b*11)/100;px[i]=a|(v<<16)|(v<<8)|v;
            }
            avatarMiniGray[sp]=Image.createRGBImage(px,s,s,true);
        }catch(Exception e){avatarMiniGray[sp]=null;}
        if(avatarMiniGray[sp]!=null)g.drawImage(avatarMiniGray[sp],x,y,TL);
        else if(avatarMini[sp]!=null)g.drawImage(avatarMini[sp],x,y,TL);
        else sprite(g,sp,x,y,20);
    }

    /** 16px portrait for dense nine-row post-battle comparisons. */
    public static void avatarTiny(Graphics g,int sp,int x,int y){
        if(avatarTiny[sp]==null){
            if(!avatarTried[sp]){
                avatarTried[sp]=true;
                try{avatar[sp]=Image.createImage("/av/"+sp+".png");}catch(Exception e){avatar[sp]=null;}
            }
            Image im=avatar[sp];
            if(im!=null)try{
                int sw=im.getWidth(),sh=im.getHeight(),s=16;
                int[] src=new int[sw*sh],dst=new int[s*s];im.getRGB(src,0,sw,0,0,sw,sh);
                for(int yy=0;yy<s;yy++)for(int xx=0;xx<s;xx++)dst[yy*s+xx]=src[(yy*sh/s)*sw+xx*sw/s];
                avatarTiny[sp]=Image.createRGBImage(dst,s,s,true);
            }catch(Exception e){avatarTiny[sp]=null;}
        }
        if(avatarTiny[sp]!=null)g.drawImage(avatarTiny[sp],x,y,TL);else sprite(g,sp,x,y,16);
    }
}

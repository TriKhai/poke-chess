package pac;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

/** Main canvas: game loop, key mapping, screen switching. */
public final class Game extends GameCanvas implements Runnable {
    public static final int K_NONE = 0, K_UP = 1, K_DOWN = 2, K_LEFT = 3, K_RIGHT = 4, K_FIRE = 5,
            K_SOFT1 = 6, K_SOFT2 = 7, K_STAR = 8, K_POUND = 9,
            K_0 = 10, K_1 = 11, K_3 = 13, K_7 = 17, K_9 = 19;

    /** last exception message from the game loop (debug aid, shown on screen). */
    public static String lastError = null;

    private final PacMidlet midlet;
    private Screen screen;
    private volatile boolean running;
    private Thread thread;
    public int W = 176, H = 208;
    private Graphics gfx;
    private boolean sizeDirty = true;
    /** Measured render diagnostics, updated once per second without allocations. */
    public int actualFps=0,lastFrameMs=0;

    private final int[] queue = new int[16];
    private int qHead = 0, qTail = 0;
    /** Held directions are sampled by the game loop so devices without keyRepeated still move. */
    private volatile int heldDirections;
    public static final int K_DASH_BASE=40;private final SurvivalTap arenaTaps=new SurvivalTap();public void resetArenaInput(){heldDirections=0;arenaTaps.reset();}
    private long nextHeldMove;

    public Game(PacMidlet m) {
        super(false);
        midlet = m;
        setFullScreenMode(true);
    }

    public void setScreen(Screen s) {
        heldDirections=0;arenaTaps.reset();
        screen = s;
        s.onShow();
        Music.screen(s);
    }

    public void quit() {
        midlet.quit();
    }

    public void requestProfileName(){midlet.requestProfileName();}

    public boolean releaseIdentityOk(){return midlet.releaseIdentityOk();}

    public void openMain(){if(Save.playPath<0)setScreen(new PlayPathScreen(this));else setScreen(new MenuScreen(this));}

    public synchronized void start() {
        if (running) return;
        Music.pause(false);
        Thread next = new Thread(this);
        thread = next;
        running = true;
        next.start();
    }

    public synchronized void stop() {
        running = false;
        Music.pause(true);
    }
    protected void hideNotify(){Music.pause(true);}
    protected void showNotify(){Music.pause(false);}

    public void persist(){Screen s=screen;if(s instanceof ChessScreen)((ChessScreen)s).saveResume();}

    protected void sizeChanged(int w, int h) {
        sizeDirty = true;
    }

    // ---- input ---------------------------------------------------------

    private synchronized void push(int k) {
        if (k == K_NONE) return;
        int next = (qTail + 1) % queue.length;
        if (next == qHead) return;
        queue[qTail] = k;
        qTail = next;
    }

    private synchronized int pop() {
        if (qHead == qTail) return K_NONE;
        int k = queue[qHead];
        qHead = (qHead + 1) % queue.length;
        return k;
    }

    private int map(int kc) {
        switch (kc) {
            case KEY_NUM0: return K_0;
            case KEY_NUM1: return K_1;
            case KEY_NUM2: return K_UP;
            case KEY_NUM3: return K_3;
            case KEY_NUM4: return K_LEFT;
            case KEY_NUM5: return K_FIRE;
            case KEY_NUM6: return K_RIGHT;
            case KEY_NUM7: return K_7;
            case KEY_NUM8: return K_DOWN;
            case KEY_NUM9: return K_9;
            case KEY_STAR: return K_STAR;
            case KEY_POUND: return K_POUND;
            case -6: case -21: return K_SOFT1;
            case -7: case -22: return K_SOFT2;
            default: break;
        }
        int a;
        try {
            a = getGameAction(kc);
        } catch (IllegalArgumentException e) {
            return K_NONE;
        }
        switch (a) {
            case UP: return K_UP;
            case DOWN: return K_DOWN;
            case LEFT: return K_LEFT;
            case RIGHT: return K_RIGHT;
            case FIRE: return K_FIRE;
            default: return K_NONE;
        }
    }

    private static int directionBit(int k){return k==K_UP?1:(k==K_DOWN?2:(k==K_LEFT?4:(k==K_RIGHT?8:0)));}
    private static int diagonalBit(int k){return k==K_1?16:k==K_3?32:k==K_7?64:k==K_9?128:0;}
    /** Continuous movement for the arena; does not depend on keyRepeated. */
    public int heldDirection(){return combinedHeldDirection(heldDirections);}
    static int combinedHeldDirection(int mask){int base=mask&15;if((mask&16)!=0)base|=5;if((mask&32)!=0)base|=9;if((mask&64)!=0)base|=6;if((mask&128)!=0)base|=10;return combinedDirection(base);}

    /** Package-visible for input regression tests. Opposite directions cancel. */
    static int combinedDirection(int mask){int v=((mask&1)!=0?1:0)-((mask&2)!=0?1:0),h=((mask&4)!=0?1:0)-((mask&8)!=0?1:0);if(v>0)return h>0?K_1:(h<0?K_3:K_UP);if(v<0)return h>0?K_7:(h<0?K_9:K_DOWN);return h>0?K_LEFT:(h<0?K_RIGHT:K_NONE);}

    protected void keyPressed(int kc) {
        int k=map(kc),bit=directionBit(k)|diagonalBit(k);
        if(bit!=0){boolean fresh=(heldDirections&bit)==0;heldDirections|=bit;int held=heldDirection();if(fresh&&screen instanceof SurvivalScreen&&arenaTaps.press(held,System.currentTimeMillis()))push(K_DASH_BASE+held);push(held);nextHeldMove=System.currentTimeMillis()+125;}
        else push(k);
    }

    protected void keyRepeated(int kc) {
        int k=map(kc);if((directionBit(k)|diagonalBit(k))==0)push(k);
    }

    protected void keyReleased(int kc){int k=map(kc),bit=directionBit(k)|diagonalBit(k);if(bit!=0)heldDirections&=~bit;}

    /** touch fallback: screen acts as a big d-pad, bottom-left = back. */
    protected void pointerPressed(int x, int y) {
        if(screen!=null&&screen.pointer(x,y))return;
        int cx = W / 2, cy = H / 2;
        int dx = x - cx, dy = y - cy;
        if (x < W / 5 && y > H * 4 / 5) { push(K_SOFT2); return; }
        if (x > W * 4 / 5 && y > H * 4 / 5) { push(K_9); return; }
        if (Math.abs(dx) < W / 6 && Math.abs(dy) < H / 6) { push(K_FIRE); return; }
        if (Math.abs(dx) > Math.abs(dy)) push(dx < 0 ? K_LEFT : K_RIGHT);
        else push(dy < 0 ? K_UP : K_DOWN);
    }

    // ---- loop ----------------------------------------------------------
    protected void pointerDragged(int x,int y){if(screen!=null)screen.pointerDrag(x,y);}
    protected void pointerReleased(int x,int y){if(screen!=null)screen.pointerRelease(x,y);}

    public void run() {
        Thread owner=Thread.currentThread();
        long last = System.currentTimeMillis();
        long fpsStart=last;int fpsFrames=0;
        // A quick pause/resume may start a replacement before the old phone thread
        // leaves sleep. Ownership prevents both loops from updating the same screen.
        while (running&&thread==owner) {
            long now = System.currentTimeMillis();
            int dt = (int) (now - last);
            last = now;
            if (dt > 250) dt = 250;
            if (dt < 0) dt = 0;
            try {
                if (sizeDirty || gfx == null) {
                    W = getWidth();
                    H = getHeight();
                    gfx = getGraphics();
                    sizeDirty = false;
                }
                Screen s = screen;
                int k = pop();
                while (k != K_NONE) {
                    s.key(k);
                    s = screen;
                    k = pop();
                }
                s.update(dt);
                s.paint(gfx);
                flushGraphics();
                fpsFrames++;
                long fpsNow=System.currentTimeMillis();
                if(fpsNow-fpsStart>=1000){actualFps=(int)(fpsFrames*1000/(fpsNow-fpsStart));fpsFrames=0;fpsStart=fpsNow;}
            } catch (Exception e) {
                // keep the loop alive; a broken frame should not kill the game
                lastError = e.toString();
            }
            long spent = System.currentTimeMillis() - now;
            lastFrameMs=(int)spent;
            // Real phones can trade animation cadence for lower CPU/battery usage.
            long wait = Save.frameDelay() - spent;
            try {
                Thread.sleep(wait > 5 ? wait : 5);
            } catch (InterruptedException e) { }
        }
        synchronized(this){if(thread==owner)thread=null;}
    }
}

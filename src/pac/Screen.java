package pac;

import javax.microedition.lcdui.Graphics;

/** Base class for all game screens. */
public abstract class Screen {
    protected final Game game;

    protected Screen(Game g) {
        game = g;
    }

    public void onShow() { }

    public abstract void update(int dt);

    public abstract void paint(Graphics g);

    /** logical key, see Game.K_* */
    public abstract void key(int k);

    /** Optional direct touch/click handling before the global d-pad fallback. */
    public boolean pointer(int x,int y){return false;}
}

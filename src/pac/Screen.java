package pac;

import javax.microedition.lcdui.Graphics;

/** Base class for all game screens. */
public abstract class Screen {
    protected final Game game;
    private int touchChoice=-1;
    /** First tap highlights; only a second tap on the same option activates it. */
    protected boolean confirmTouch(int choice){if(touchChoice==choice){touchChoice=-1;return true;}touchChoice=choice;return false;}
    public void resetTouchChoice(){touchChoice=-1;}

    protected Screen(Game g) {
        game = g;
    }

    public void onShow() { resetTouchChoice(); }

    public abstract void update(int dt);

    public abstract void paint(Graphics g);

    /** logical key, see Game.K_* */
    public abstract void key(int k);

    /** Optional direct touch/click handling before the global d-pad fallback. */
    public boolean pointer(int x,int y){return false;}
    public void pointerDrag(int x,int y) { }
    public void pointerRelease(int x,int y) { }
}

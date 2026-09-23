package pac;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public final class PacMidlet extends MIDlet {
    private Game game;

    public PacMidlet() { }

    protected void startApp() {
        if (game == null) {
            Save.load();
            Art.init();
            game = new Game(this);
            if (Save.playPath < 0) game.setScreen(new PlayPathScreen(game));
            else game.setScreen(new MenuScreen(game));
        }
        Display.getDisplay(this).setCurrent(game);
        game.start();
    }

    protected void pauseApp() {
        if (game != null) {game.persist();game.stop();}
    }

    protected void destroyApp(boolean unconditional) {
        if (game != null) {game.persist();game.stop();}
        Save.save();
    }

    public void quit() {
        destroyApp(true);
        notifyDestroyed();
    }
}

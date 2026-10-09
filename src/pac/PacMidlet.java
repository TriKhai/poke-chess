package pac;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

public final class PacMidlet extends MIDlet {
    private Game game;

    public PacMidlet() { }

    protected void startApp() {
        if (game == null) {
            Save.load();
            Art.init();
            game = new Game(this);
            game.setScreen(new StartupNoticeScreen(game));
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

    public boolean releaseIdentityOk(){String vendor=getAppProperty("MIDlet-Vendor");return "Kdic".equals(vendor);}

    public void requestProfileName(){
        if(Save.profileName.length()!=0)return;
        game.stop();
        final TextBox box=new TextBox(Lang.t("Đặt tên nhân vật","Choose your name"),"",16,TextField.ANY);
        final Command ok=new Command(Lang.t("Xác nhận","Confirm"),Command.OK,1);
        final Command cancel=new Command(Lang.t("Hủy","Cancel"),Command.CANCEL,2);
        box.addCommand(ok);box.addCommand(cancel);
        box.setCommandListener(new CommandListener(){public void commandAction(Command c,Displayable d){
            if(c==ok)Save.setProfileNameOnce(box.getString());
            Display.getDisplay(PacMidlet.this).setCurrent(game);game.start();
        }});
        Display.getDisplay(this).setCurrent(box);
    }
}

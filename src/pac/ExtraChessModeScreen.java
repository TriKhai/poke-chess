package pac;
import javax.microedition.lcdui.Graphics;

/** Scroll-free four-mode picker; reuses the normal replace-save confirmation. */
public final class ExtraChessModeScreen extends Screen {
 private int selected;private boolean ask;private int confirm;
 public ExtraChessModeScreen(Game g){super(g);}
 public void update(int dt){}
 public void key(int k){if(k!=Game.K_FIRE&&k!=Game.K_SOFT1)resetTouchChoice();if(k==Game.K_0||k==Game.K_SOFT2){if(ask){ask=false;resetTouchChoice();}else game.setScreen(new ChessModeScreen(game));return;}if(k==Game.K_UP||k==Game.K_LEFT){if(ask)confirm=0;else selected=(selected+3)%4;}else if(k==Game.K_DOWN||k==Game.K_RIGHT){if(ask)confirm=1;else selected=(selected+1)%4;}else if(k==Game.K_FIRE||k==Game.K_SOFT1){if(ask){if(confirm==1){RunStorage.clear();game.setScreen(new ChessScreen(game,Run.MODE_TOWER+selected));}else{ask=false;resetTouchChoice();}}else if(RunStorage.has()){ask=true;confirm=0;resetTouchChoice();}else game.setScreen(new ChessScreen(game,Run.MODE_TOWER+selected));}}
 private int top(){return ExtraChessRules.menuTop(Art.fh);}private int gap(){return ask?Art.fh+10:ExtraChessRules.menuGap(game.H,Art.fh);}
 public boolean pointer(int x,int y){int index=ExtraChessRules.menuHit(x,y,game.W,game.H,Art.fh,ask);if(index>=0){if(ask)confirm=index;else selected=index;if(confirmTouch(index))key(Game.K_FIRE);}else if(y>=game.H-Art.fh-7)key(Game.K_0);return true;}
 public void paint(Graphics g){int W=game.W,H=game.H,fh=Art.fh;g.setColor(0x101827);g.fillRect(0,0,W,H);Art.textBC(g,Lang.t("CỜ THỬ THÁCH","CHESS CHALLENGES"),W/2,8,0xFFD030);if(ask){Art.para(g,Lang.t("Chơi mới sẽ xóa run đang lưu.","New game replaces the saved run."),8,H/2-fh*3,W-16,0xFF9080,3);for(int i=0;i<2;i++){int y=H/2+i*gap();if(i==confirm){g.setColor(0x305090);g.fillRect(W/8,y-3,W*3/4,fh+6);}Art.textC(g,i==0?Lang.t("HỦY","CANCEL"):Lang.t("XÓA & CHƠI","REPLACE & PLAY"),W/2,y,0xFFFFFF);}}else{for(int i=0;i<4;i++){int y=top()+i*gap();if(i==selected){g.setColor(0x305090);g.fillRect(W/8,y-3,W*3/4,fh+6);}Art.textC(g,ExtraChessRules.name(Run.MODE_TOWER+i),W/2,y,i==selected?0xFFFFFF:0xAFC0D8);}Art.para(g,ExtraChessRules.objective(Run.MODE_TOWER+selected),8,ExtraChessRules.menuDescriptionY(H,fh),W-16,0x70D8FF,Math.max(1,(H-fh*2-ExtraChessRules.menuDescriptionY(H,fh))/fh));}Art.textSmallC(g,Lang.t("5: chọn  0: về","5: select  0: back"),W/2,H-fh-3,0x8090B0);}
}

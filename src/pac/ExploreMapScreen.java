package pac;
import javax.microedition.lcdui.Graphics;
/** All nine generations; temporary terrain layouts remain replaceable. */
public final class ExploreMapScreen extends Screen{
 private int sel,top;
 public ExploreMapScreen(Game g){super(g);}
 public void update(int dt){}
 private int start(){return Art.fh*2+8;}
 private int row(){return Art.fh*3+8;}
 private int rows(){return Math.max(1,(game.H-start()-Art.fh*2-8)/row());}
 private void scroll(){if(sel<top)top=sel;if(sel>=top+rows())top=sel-rows()+1;}
 public void key(int k){if(k!=Game.K_FIRE&&k!=Game.K_SOFT1)resetTouchChoice();if(k==Game.K_UP||k==Game.K_LEFT)sel=(sel+8)%9;else if(k==Game.K_DOWN||k==Game.K_RIGHT)sel=(sel+1)%9;else if(k==Game.K_STAR)game.setScreen(new ExploreDexScreen(game,sel+1));else if(k==Game.K_POUND)game.setScreen(new ExploreBallScreen(game));else if(k==Game.K_FIRE||k==Game.K_SOFT1)game.setScreen(new WorldScreen(game,sel+1));else if(k==Game.K_0||k==Game.K_SOFT2)game.setScreen(new ExploreHubScreen(game));scroll();}
 public boolean pointer(int x,int y){if(y>=game.H-Art.fh*2-8){if(x<game.W/3)key(Game.K_STAR);else if(x<game.W*2/3)key(Game.K_POUND);else key(Game.K_0);return true;}if(y<start())return true;if(x>=game.W-14){key(y<start()+rows()*row()/2?Game.K_UP:Game.K_DOWN);return true;}int i=top+(y-start())/row();if(i>=9||i>=top+rows())return true;sel=i;if(confirmTouch(700+i))key(Game.K_FIRE);scroll();return true;}
 public void paint(Graphics g){scroll();int W=game.W,H=game.H,fh=Art.fh;g.setColor(0x101827);g.fillRect(0,0,W,H);Art.textBC(g,Lang.t("CHỌN KHU THU PHỤC","SELECT CAPTURE AREA"),W/2,5,0xFFD030);for(int r=0;r<rows()&&top+r<9;r++){int i=top+r,y=start()+r*row();Art.box(g,6,y,W-20,row()-3,i==sel?0x305090:0x1B2940,i==sel?0xFFD030:0x40506A);Art.textB(g,ExploreMapLayout.region(i+1)+" - GEN "+(i+1),12,y+3,0xFFFFFF);Art.textSmall(g,Lang.t("Đã bắt ","Caught ")+Save.exploreMapCaught[i]+Lang.t(" • Loài "," • Species ")+ExploreRules.uniqueCaught(i+1)+"/"+ExploreRules.mapSpeciesCount(i+1),12,y+fh+4,0x90D8A0);Art.textSmall(g,Lang.t("Hạ ","KO ")+Save.exploreMapDefeated[i]+Lang.t(" • Thần thú "," • Legendary ")+Save.exploreMapLegends[i],12,y+fh*2+4,0xFFE080);}int track=rows()*row();g.setColor(0x30405A);g.fillRect(W-10,start(),4,track);g.setColor(0x80D8FF);g.fillRect(W-10,start()+top*track/9,4,Math.max(5,Math.min(9,rows())*track/9));Art.textSmallC(g,Lang.t("*: loài   #: túi   0: về","*: species   #: bag   0: back"),W/2,H-fh-4,0x8090B0);}
}

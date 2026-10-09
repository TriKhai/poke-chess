package pac;
import javax.microedition.lcdui.Graphics;
/** Icon wallet and the existing normal-ball upgrade routes. */
public final class ExploreBallScreen extends Screen {
 private int sel;private String msg="";private final boolean fromHub;
 public ExploreBallScreen(Game g){this(g,false);}
 public ExploreBallScreen(Game g,boolean hub){super(g);fromHub=hub;}
 public void update(int dt){}
 private int top(){return Art.fh*4+24;}
 private int row(){return Math.max(42,Math.min(58,(game.H-top()-Art.fh*4)/2));}
 public void key(int k){if(k==Game.K_UP||k==Game.K_LEFT||k==Game.K_DOWN||k==Game.K_RIGHT){sel=1-sel;resetTouchChoice();}else if(k==Game.K_0||k==Game.K_SOFT2){if(fromHub)game.setScreen(new ExploreHubScreen(game));else game.setScreen(new ExploreMapScreen(game));}else if(k==Game.K_FIRE||k==Game.K_SOFT1){if(BallExchange.exchange(sel)){Save.save();msg=Lang.t("Đổi thành công","Exchange complete");}else msg=Lang.t("Không đủ số lượng hoặc túi đã đầy","Insufficient balance or wallet full");}}
 public boolean pointer(int x,int y){if(y>=game.H-Art.fh-10){key(Game.K_0);return true;}int index=(y-top())/row();if(x>=10&&x<game.W-10&&y>=top()&&index<2&&(y-top())%row()<row()-6){sel=index;if(confirmTouch(sel))key(Game.K_FIRE);}return true;}
 public void paint(Graphics g){int W=game.W,H=game.H;g.setColor(0x101827);g.fillRect(0,0,W,H);Art.textBC(g,Lang.t("ĐỔI \uE000","\uE000 EXCHANGE"),W/2,8,0xFFD030);BallArt.wallet(g,10,Art.fh*2+8,W-20);for(int i=0;i<2;i++){int y=top()+i*row();Art.box(g,10,y,W-20,row()-6,i==sel?0x305090:0x1B2940,i==sel?0xFFD030:0x40506A);int cx=W/2;BallArt.draw(g,BallArt.NORMAL,cx-91,y+3);Art.textB(g,"x"+BallExchange.cost(i),cx-55,y+12,0xFFFFFF);Art.textB(g,">",cx-4,y+12,0x70D8FF);BallArt.draw(g,BallExchange.target(i),cx+17,y+3);Art.textB(g,"x1",cx+56,y+12,0xFFFFFF);}Art.para(g,msg,12,H-Art.fh*4,W-24,0x70D8FF,2);Art.textSmallC(g,Lang.t("2/8: chọn  5: đổi  0: về","2/8: select  5: exchange  0: back"),W/2,H-Art.fh-3,0x8090B0);}
}

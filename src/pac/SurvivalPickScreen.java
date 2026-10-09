package pac;
import javax.microedition.lcdui.Graphics;

/** Owned playable species picker; never changes the profile avatar. */
public final class SurvivalPickScreen extends Screen {
 private final int[] pets=new int[Data.N];private int count,sel,top;
 public SurvivalPickScreen(Game g){super(g);for(int i=0;i<Data.N;i++)if(Save.ownsDex(Data.nationalDex(i))&&!Save.inCamp(i)){boolean duplicate=false;for(int j=0;j<count;j++)if(Data.nationalDex(pets[j])==Data.nationalDex(i))duplicate=true;if(!duplicate){pets[count]=i;if(Data.nationalDex(i)==Save.profileAvatarDex)sel=count;count++;}}}
 public void update(int dt){}
 private int columns(){return Math.max(3,Math.min(8,game.W/38));}
 public void key(int k){int c=columns();if(k==Game.K_0||k==Game.K_SOFT2){game.setScreen(new ExploreHubScreen(game));return;}if(count==0)return;if(k==Game.K_LEFT)sel=Math.max(0,sel-1);else if(k==Game.K_RIGHT)sel=Math.min(count-1,sel+1);else if(k==Game.K_UP)sel=Math.max(0,sel-c);else if(k==Game.K_DOWN)sel=Math.min(count-1,sel+c);else if(k==Game.K_FIRE||k==Game.K_SOFT1)game.setScreen(new SurvivalScreen(game,pets[sel]));}
 public boolean pointer(int x,int y){int rows=rows(),c=columns(),cell=game.W/c,sy=Art.fh*2+8,index=(top+(y-sy)/40)*c+x/cell;if(y>=sy&&y<sy+rows*40&&index>=0&&index<count){sel=index;if(confirmTouch(sel))key(Game.K_FIRE);}else if(y>=game.H-Art.fh-8)key(Game.K_0);return true;}
 private int rows(){return Math.max(1,(game.H-Art.fh*7-20)/40);}
 public void paint(Graphics g){int W=game.W,H=game.H,fh=Art.fh,c=columns(),cell=W/c,rows=rows(),sy=fh*2+8;g.setColor(0x101827);g.fillRect(0,0,W,H);Art.textBC(g,Lang.t("CHỌN POKEMON NỔI LOẠN","CHOOSE UPRISING POKEMON"),W/2,4,0xFFE060);Art.textSmallC(g,Lang.t("Pet đã sở hữu · không đổi Hồ sơ","Owned pets · profile unchanged"),W/2,fh+6,0x80D8FF);if(count==0){Art.para(g,Lang.t("Chưa có pet khả dụng. Hãy thu thập Pokémon hoặc nhận pet từ bãi.","No playable pet. Collect Pokemon or recall a camped pet."),8,sy,W-16,0xB0D0EE,4);return;}int row=sel/c;if(row<top)top=row;if(row>=top+rows)top=row-rows+1;for(int r=0;r<rows;r++)for(int col=0;col<c;col++){int i=(top+r)*c+col;if(i>=count)continue;int x=col*cell,y=sy+r*40;Art.avatar(g,pets[i],x+(cell-32)/2,y+3);if(i==sel){g.setColor(0xFFFFFF);g.drawRect(x+1,y,cell-3,38);}}int s=pets[sel],base=SurvivalProgress.startSpecies(s),fy=H-fh*4-12;g.setColor(0x20304A);g.fillRect(4,fy,W-8,fh*3+8);Art.textSmall(g,Data.name[s]+" #"+Data.nationalDex(s),8,fy+3,0xFFFFFF);Art.textSmall(g,Lang.t("Bắt đầu: ","Starts: ")+Data.name[base],8,fy+fh+4,0x80D8FF);Art.textSmall(g,Lang.t("Chiêu I/II/III/IV: Lv 1/5/10/15","Skill I/II/III/IV: Lv 1/5/10/15"),8,fy+fh*2+5,0xFFD070);Art.textSmallC(g,Lang.t("2/4/6/8: chọn · 5: vào · R/0: về","2/4/6/8: select · 5: enter · R/0: back"),W/2,H-fh-2,0x80D8FF);}
}

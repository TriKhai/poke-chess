package pac;
import javax.microedition.lcdui.Graphics;
/** Lightweight active farm battle using one chosen collection Pokémon. */
public final class FarmScreen extends Screen{
 private int hero,enemy,hp,ehp,acc,kills,msgT;private final Rng rng=new Rng((int)System.currentTimeMillis());public FarmScreen(Game g){super(g);hero=Save.hero>=0&&Save.has(Save.hero)?Save.hero:next(-1,1);spawn();}
 private int next(int from,int d){for(int n=1;n<=Data.N;n++){int i=(from+d*n+Data.N*2)%Data.N;if(Data.isBase(i)&&Save.has(i))return i;}return 0;}private void spawn(){enemy=ExploreRules.pick(rng,rng.nextInt(3),rng.nextInt(3)+1);if(enemy<0)enemy=0;hp=Data.hp[hero];ehp=Data.hp[enemy];acc=0;}
 public void update(int dt){acc+=dt;if(acc>=800){acc-=800;ehp-=Math.max(1,Data.atk[hero]);hp-=Math.max(1,Data.atk[enemy]/2);check();}if(msgT>0)msgT-=dt;}
 private void check(){if(ehp<=0){int gain=Math.max(1,Data.category[enemy]/2+1);Save.balls+=gain;kills++;Save.save();spawn();msgT=900;}else if(hp<=0){spawn();msgT=900;}}
 public void key(int k){if(k==Game.K_LEFT||k==Game.K_UP){hero=next(hero,-1);Save.hero=hero;Save.save();spawn();}else if(k==Game.K_RIGHT||k==Game.K_DOWN){hero=next(hero,1);Save.hero=hero;Save.save();spawn();}else if(k==Game.K_FIRE||k==Game.K_SOFT1){ehp-=Math.max(3,Data.atk[hero]*2+Data.abil[hero]*2);check();}else if(k==Game.K_0||k==Game.K_SOFT2)game.setScreen(new ExploreHubScreen(game));}
 public void paint(Graphics g){int W=game.W,H=game.H,fh=Art.fh;g.setColor(0x285B35);g.fillRect(0,0,W,H);for(int x=0;x<W;x+=16){g.setColor((x&16)==0?0x397B43:0x33713D);g.fillRect(x,H/2,16,H/2);}Art.textBC(g,Lang.t("FARM PET - Bóng ","PET FARM - Balls ")+Save.balls,W/2,3,0xFFD030);Art.formationSprite(g,enemy,W-72,H/3-10,64,64,W,acc/90);Art.formationSprite(g,hero,8,H*2/3-32,64,64,W,acc/90);Art.text(g,Data.name[hero]+" "+Math.max(0,hp)+"/"+Data.hp[hero],3,H-fh*3,0xA0E8FF);Art.textR(g,Data.name[enemy]+" "+Math.max(0,ehp)+"/"+Data.hp[enemy],W-3,fh*2,0xFFB0A0);Art.textSmallC(g,Lang.t("4/6 đổi pet  FIRE dùng skill  0 về","4/6 hero  FIRE skill  0 back"),W/2,H-fh-2,0xFFFFFF);}
}

package pac;
import javax.microedition.lcdui.Graphics;
/** TinyMeadow camp with three exclusive offline gathering slots. */
public final class CampScreen extends Screen{
 private int slot,pick,pickTop,time;private boolean popup;private String msg="";private int msgT;
 private final Rng rng=new Rng((int)System.currentTimeMillis());
 private final int[] actorSp={-2,-2,-2},ax=new int[3],ay=new int[3],tx=new int[3],ty=new int[3],dir=new int[3],pause=new int[3],moveBank=new int[3];
 public CampScreen(Game g){super(g);}
 public void update(int dt){time+=dt;if(msgT>0)msgT-=dt;for(int i=0;i<3;i++)updateActor(i,dt);}
 private int sceneY(){return Art.fh+5;}private int sceneH(){int h=game.H-sceneY()-(Art.fh*5+8);return Math.max(70,h);}
 private void placeActor(int i){int sy=sceneY(),sh=sceneH();ax[i]=Math.max(4,game.W*(i+1)/4-24);ay[i]=sy+Math.max(8,sh/2-12)+(i&1)*12;actorSp[i]=Save.camp[i];chooseTarget(i);}
 private void chooseTarget(int i){int sy=sceneY(),sh=sceneH(),maxX=Math.max(5,game.W-53),maxY=Math.max(sy+8,sy+sh-43);tx[i]=4+rng.nextInt(maxX-3);ty[i]=sy+8+rng.nextInt(Math.max(1,maxY-sy-7));dir[i]=face(tx[i]-ax[i],ty[i]-ay[i]);}
 private static int face(int dx,int dy){if(Math.abs(dx)>Math.abs(dy)*2)return dx>=0?2:6;if(Math.abs(dy)>Math.abs(dx)*2)return dy>=0?0:4;if(dx>=0)return dy>=0?1:3;return dy>=0?7:5;}
 private void updateActor(int i,int dt){if(Save.camp[i]<0){actorSp[i]=-2;return;}if(actorSp[i]!=Save.camp[i])placeActor(i);if(pause[i]>0){pause[i]-=dt;return;}moveBank[i]+=dt;int steps=moveBank[i]/70;moveBank[i]%=70;while(steps-->0){if(ax[i]<tx[i])ax[i]++;else if(ax[i]>tx[i])ax[i]--;if(ay[i]<ty[i])ay[i]++;else if(ay[i]>ty[i])ay[i]--;if(ax[i]==tx[i]&&ay[i]==ty[i]){pause[i]=500+rng.nextInt(1300);chooseTarget(i);break;}}dir[i]=face(tx[i]-ax[i],ty[i]-ay[i]);}
 public void key(int k){
  if(popup){keyPopup(k);return;}
  if(k==Game.K_LEFT)slot=(slot+2)%3;else if(k==Game.K_RIGHT)slot=(slot+1)%3;
  else if(k==Game.K_FIRE||k==Game.K_SOFT1){popup=true;pick=0;pickTop=0;}
  else if(k==Game.K_3){int reward=ExploreRules.claimCamp(System.currentTimeMillis()/1000L);Save.save();msg=reward>0?Lang.t("Đã nhận ","Claimed ")+reward+Lang.t(" Bóng"," Balls"):Lang.t("Chưa đủ một giờ","Less than one hour");msgT=1400;}
  else if(k==Game.K_7&&Save.camp[slot]>=0){long now=System.currentTimeMillis()/1000L;ExploreRules.claimCamp(now);Save.camp[slot]=-1;Save.campStart=now;Save.save();}
  else if(k==Game.K_0||k==Game.K_SOFT2)game.setScreen(new ExploreHubScreen(game));
 }
 private void keyPopup(int k){int allow=Save.camp[slot],count=OwnedPokemon.count(allow),cols=Math.max(3,game.W/38);if(k==Game.K_LEFT&&pick>0)pick--;else if(k==Game.K_RIGHT&&pick<count-1)pick++;else if(k==Game.K_UP&&pick>=cols)pick-=cols;else if(k==Game.K_DOWN&&pick+cols<count)pick+=cols;else if((k==Game.K_FIRE||k==Game.K_SOFT1)&&count>0){long now=System.currentTimeMillis()/1000L;if(Save.campStart>0)ExploreRules.claimCamp(now);Save.camp[slot]=OwnedPokemon.at(pick,allow);Save.campStart=now;Save.save();popup=false;}else if(k==Game.K_0||k==Game.K_SOFT2)popup=false;}
 public void paint(Graphics g){int W=game.W,H=game.H,fh=Art.fh,sceneY=fh+5,footer=fh*5+8,sceneH=H-sceneY-footer;if(sceneH<70)sceneH=70;CampMap.draw(g,0,sceneY,W,sceneH);g.setColor(0x0D1720);g.fillRect(0,0,W,sceneY);Art.textBC(g,Lang.t("BÃI POKÉMON","POKÉMON CAMP"),W/2,2,0xFFD030);
  for(int i=0;i<3;i++){int cx=W*(i+1)/4,emptyY=sceneY+sceneH-48;if(Save.camp[i]>=0){if(actorSp[i]!=Save.camp[i])placeActor(i);int action=pause[i]>0?(pause[i]>1000?RawAtlas.POSE:RawAtlas.IDLE):RawAtlas.WALK;if(i==slot){g.setColor(0xFFE060);g.drawRect(ax[i]-2,ay[i]-3,51,44);}int sp=Save.camp[i];if(!RawAtlas.draw(g,sp,ax[i],ay[i],48,40,action,dir[i],time/90+i))Art.sprite(g,sp,ax[i]+8,ay[i]+4,32);}else{if(i==slot){g.setColor(0xFFE060);g.drawRect(cx-20,emptyY-3,39,39);}g.setColor(0x243A2C);g.fillRect(cx-16,emptyY+2,32,32);Art.textBC(g,"+",cx,emptyY+9,0xD8F0C0);}}
  int fy=sceneY+sceneH;g.setColor(0x101B22);g.fillRect(0,fy,W,H-fy);int reward=ExploreRules.campReward(System.currentTimeMillis()/1000L);Art.textSmallC(g,msgT>0?msg:Lang.t("Ô ","Slot ")+(slot+1)+"   "+Lang.t("Có thể nhận: ","Claimable: ")+reward,W/2,fy+2,msgT>0?0xFFFFFF:0xFFE080);paintCampSlots(g,fy+fh+2);Art.textSmallC(g,Lang.t("FIRE chọn pet  3 nhận  7 gỡ  0 về","FIRE choose  3 claim  7 remove  0 back"),W/2,H-fh-2,0xD0E0D0);if(popup)paintPopup(g);
 }
 private void paintCampSlots(Graphics g,int y){int W=game.W,cell=W/3;for(int i=0;i<3;i++){int cx=i*cell+cell/2,x=cx-16;if(i==slot){g.setColor(0xFFE060);g.drawRect(x-3,y-3,37,37);}if(Save.camp[i]>=0)Art.avatar(g,Save.camp[i],x,y);else{g.setColor(0x243448);g.fillRect(x,y,32,32);Art.textBC(g,"+",cx,y+8,0x9EB5C8);}Art.textSmallC(g,""+(i+1),cx,y+31,i==slot?0xFFE060:0x8090A0);}}
 private void paintPopup(Graphics g){int W=game.W,H=game.H,fh=Art.fh,bx=6,by=fh*2,bw=W-12,bh=H-fh*4,allow=Save.camp[slot],count=OwnedPokemon.count(allow),cols=Math.max(3,bw/38),cell=bw/cols,row=38,rows=(bh-fh*3)/row;if(rows<1)rows=1;int pr=pick/cols;if(pr<pickTop)pickTop=pr;if(pr>=pickTop+rows)pickTop=pr-rows+1;Art.box(g,bx,by,bw,bh,0x111A27,0x80D8FF);Art.textBC(g,Lang.t("CHỌN POKÉMON TREO","CHOOSE CAMP POKÉMON"),W/2,by+4,0xFFD060);int y0=by+fh+8;for(int r=0;r<rows;r++)for(int c=0;c<cols;c++){int i=(pickTop+r)*cols+c;if(i>=count)continue;int sp=OwnedPokemon.at(i,allow),x=bx+c*cell+(cell-32)/2,y=y0+r*row;if(i==pick){g.setColor(0xFFE060);g.drawRect(x-2,y-2,35,35);}Art.avatar(g,sp,x,y);}if(count>0){int sp=OwnedPokemon.at(pick,allow);Art.textSmallC(g,Data.name[sp]+Lang.t("  - khóa khỏi các mode","  - locked from all modes"),W/2,by+bh-fh*2,0xA0E8FF);}else Art.textSmallC(g,Lang.t("Không còn Pokémon khả dụng","No available Pokémon"),W/2,by+bh-fh*2,0xFF9090);Art.textSmallC(g,Lang.t("FIRE chọn   0 hủy","FIRE choose   0 cancel"),W/2,by+bh-fh,0x8090B0);}
}

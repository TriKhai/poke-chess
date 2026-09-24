package pac;
import javax.microedition.lcdui.Graphics;
/** TinyMeadow camp with three exclusive offline gathering slots. */
public final class CampScreen extends Screen{
 private int slot,pick,pickTop,time;private boolean popup;private String msg="";private int msgT;
 public CampScreen(Game g){super(g);}
 public void update(int dt){time+=dt;if(msgT>0)msgT-=dt;}
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
  for(int i=0;i<3;i++){int cx=W*(i+1)/4,y=sceneY+sceneH-48;if(i==slot){g.setColor(0xFFE060);g.drawRect(cx-25,y-4,50,48);}if(Save.camp[i]>=0){int sp=Save.camp[i];if(!RawAtlas.draw(g,sp,cx-24,y,48,40,RawAtlas.WALK,(i*2)&7,time/90+i))Art.sprite(g,sp,cx-16,y+4,32);Art.textSmallC(g,Data.name[sp],cx,y+39,0xFFFFFF);}else{g.setColor(0x243A2C);g.fillRect(cx-16,y+5,32,32);Art.textBC(g,"+",cx,y+12,0xD8F0C0);}}
  int fy=sceneY+sceneH;g.setColor(0x101B22);g.fillRect(0,fy,W,H-fy);int reward=ExploreRules.campReward(System.currentTimeMillis()/1000L);Art.textSmallC(g,Lang.t("Ô ","Slot ")+(slot+1)+"   "+Lang.t("Có thể nhận: ","Claimable: ")+reward,W/2,fy+3,0xFFE080);Art.textSmallC(g,Lang.t("FIRE chọn pet  3 nhận  7 gỡ  0 về","FIRE choose  3 claim  7 remove  0 back"),W/2,H-fh-2,0xD0E0D0);if(msgT>0)Art.textBC(g,msg,W/2,fy+fh+4,0xFFFFFF);if(popup)paintPopup(g);
 }
 private void paintPopup(Graphics g){int W=game.W,H=game.H,fh=Art.fh,bx=6,by=fh*2,bw=W-12,bh=H-fh*4,allow=Save.camp[slot],count=OwnedPokemon.count(allow),cols=Math.max(3,bw/38),cell=bw/cols,row=38,rows=(bh-fh*3)/row;if(rows<1)rows=1;int pr=pick/cols;if(pr<pickTop)pickTop=pr;if(pr>=pickTop+rows)pickTop=pr-rows+1;Art.box(g,bx,by,bw,bh,0x111A27,0x80D8FF);Art.textBC(g,Lang.t("CHỌN POKÉMON TREO","CHOOSE CAMP POKÉMON"),W/2,by+4,0xFFD060);int y0=by+fh+8;for(int r=0;r<rows;r++)for(int c=0;c<cols;c++){int i=(pickTop+r)*cols+c;if(i>=count)continue;int sp=OwnedPokemon.at(i,allow),x=bx+c*cell+(cell-32)/2,y=y0+r*row;if(i==pick){g.setColor(0xFFE060);g.drawRect(x-2,y-2,35,35);}Art.avatar(g,sp,x,y);}if(count>0){int sp=OwnedPokemon.at(pick,allow);Art.textSmallC(g,Data.name[sp]+Lang.t("  - khóa khỏi các mode","  - locked from all modes"),W/2,by+bh-fh*2,0xA0E8FF);}else Art.textSmallC(g,Lang.t("Không còn Pokémon khả dụng","No available Pokémon"),W/2,by+bh-fh*2,0xFF9090);Art.textSmallC(g,Lang.t("FIRE chọn   0 hủy","FIRE choose   0 cancel"),W/2,by+bh-fh,0x8090B0);}
}

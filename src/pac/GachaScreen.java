package pac;

import javax.microedition.lcdui.Graphics;

/** Daily all-in-one Gacha with vortex animation and a 100-point Legendary pity. */
public final class GachaScreen extends Screen{
    /** Eight positions exactly 45 degrees apart around the vortex. */
    private static final int[] OX={0,31,44,31,0,-31,-44,-31},OY={-44,-31,0,31,44,31,0,-31};
    private final Rng rng=new Rng((int)System.currentTimeMillis());
    private final int group;
    private final int[] featured;
    private int state=0,time=0,lastDex=-1;private boolean lastNew,lastPity,help,autoRoll;private String msg="";
    public GachaScreen(Game g){super(g);group=GachaRules.todayGroup();featured=GachaRules.dailyFeaturedLegendaries(group,5);}
    public void update(int dt){if(help)return;time+=dt;if(state==1&&time>=2200)resolve();else if(autoRoll&&state==2&&time>=650)startRoll();}
    public void key(int k){
        if(help){if(k==Game.K_STAR||k==Game.K_0||k==Game.K_SOFT2||k==Game.K_FIRE||k==Game.K_SOFT1)help=false;return;}
        if(k==Game.K_STAR){help=true;return;}
        if(k==Game.K_POUND){autoRoll=!autoRoll;if(autoRoll&&state!=1)startRoll();else if(!autoRoll)msg=Lang.t("Đã tắt tự động quay","Auto-roll stopped");return;}
        if(state==3){if(k==Game.K_FIRE||k==Game.K_SOFT1){state=2;time=0;}return;}
        if(k==Game.K_0||k==Game.K_SOFT2){if(state!=1)game.setScreen(new ExploreHubScreen(game));return;}
        if((k==Game.K_FIRE||k==Game.K_SOFT1)&&state!=1)startRoll();
    }
    private void startRoll(){if(state==1)return;if(Save.balls<=0){autoRoll=false;msg=Lang.t("Hết Bóng - đã dừng tự động quay","Out of Balls - auto-roll stopped");state=2;return;}Save.balls--;Save.save();state=1;time=0;lastDex=-1;msg="";}
    private void resolve(){
        int candidate=GachaRules.pickFeatured(rng,group,false,featured);boolean candidateNew=!Save.ownsDex(candidate);int gain=candidateNew?1:2;
        lastPity=Save.gachaPoints+gain>=100;lastDex=lastPity?GachaRules.pickFeatured(rng,group,true,featured):candidate;
        lastNew=Save.unlockDex(lastDex);Save.caught++;
        if(GachaRules.legendary(lastDex))Save.gachaPoints=0;else Save.gachaPoints+=lastNew?1:2;
        if(Save.gachaPoints>99)Save.gachaPoints=99;
        Save.save();state=GachaRules.legendary(lastDex)?3:2;time=0;
        msg=Lang.t("CHÚC MỪNG! Bạn nhận được ","CONGRATULATIONS! You got ")+name(lastDex);
    }
    private String name(int dex){if(dex<=Data.N)return Data.name[dex-1];int di=dexIndex(dex);return di>=0?CollectionDex.NAME[di]:"#"+dex;}
    private int dexIndex(int dex){for(int i=0;i<CollectionDex.COUNT;i++)if(CollectionDex.DEX[i]==dex)return i;return -1;}
    private void drawPokemon(Graphics g,int dex,int x,int y,int w,int h,int action,int clock){if(dex<=Data.N){if(!RawAtlas.draw(g,dex-1,x,y,w,h,action,7,clock))Art.sprite(g,dex-1,x,y,Math.min(w,h));}else{int di=dexIndex(dex);if(di<0||!CollectionAtlas.draw(g,di,x,y,w,h,action,7,clock))Art.dexAvatar(g,di,x+(w-32)/2,y+(h-32)/2);}}
    private void drawAvatar(Graphics g,int dex,int x,int y){if(dex<=Data.N)Art.avatar(g,dex-1,x,y);else{int di=dexIndex(dex);if(di>=0)Art.dexAvatar(g,di,x,y);}}
    public void paint(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,cx=W/2;boolean compact=UiLayout.compact(W,H);int barY=fh*3+6;int cy=compact?barY+42:Math.max(barY+58,H/2);
        g.setColor(0x0B0B20);g.fillRect(0,0,W,H);for(int y=0;y<H;y+=12){g.setColor(0x10132A+(y&24)*0x010101);g.fillRect(0,y,W,12);}
        Art.textBC(g,Lang.t("GACHA HỐ ĐEN","BLACK HOLE GACHA"),cx,3,0xFFD030);
        Art.textSmallC(g,GachaRules.scheduleName(group)+"  "+Lang.t("Bóng ","Balls ")+Save.balls,cx,fh+5,0x80D8FF);
        Art.textSmallC(g,Lang.t("Mốc Huyền thoại: ","Legendary pity: ")+Save.gachaPoints+"/100",cx,fh*2+5,0xFFE070);
        int bw=W-32;if(bw>176)bw=176;Art.bar(g,cx-bw/2,barY,bw,7,Save.gachaPoints,100,Save.gachaPoints>=75?0xFFD030:0x9058D8);
        int phase=state==1?time:0;
        if(state!=2||lastDex<0){
            int base=compact?30:42,radius=phase>1400?Math.max(2,base-(phase-1400)*(base-2)/800):base;
            int shift=state==1?(time/110)%OX.length:0;
            for(int i=0;i<OX.length;i++){int p=(i+shift)%OX.length,type=(i+(state==1?time/180*OX.length:0))%Data.NT,x=cx+OX[p]*radius/44,y=cy+OY[p]*radius/44;g.setColor(0xFFFFFF);g.fillArc(x-10,y-10,20,20,0,360);g.setColor(Data.TCOL[type]);g.drawArc(x-10,y-10,19,19,0,360);g.drawArc(x-9,y-9,17,17,0,360);Art.typeIcon(g,type,x-8,y-8);}
            // Draw portal last so the center remains a visible black hole while icons are swallowed.
            GachaFx.portal(g,cx,cy,time);
            // Ho-Oh fills the empty upper-left space without touching the type ring.
            if(!compact&&W>=220)drawPokemon(g,250,5,cy-31,52,44,RawAtlas.WALK,time/90+3);
        }else{
            GachaFx.portal(g,cx,cy,time);
            int pw=compact?56:76,ph=compact?52:72;drawPokemon(g,lastDex,cx-pw/2,cy-ph/2,pw,ph,RawAtlas.WALK,time/90);
            Art.textBC(g,name(lastDex),cx,cy+39,GachaRules.legendary(lastDex)?0xFFD030:0xFFFFFF);
            Art.textSmallC(g,lastPity?Lang.t("MỐC 100 - HUYỀN THOẠI KHÔNG TRÙNG","100 PITY - NEW LEGENDARY"):(lastNew?Lang.t("Pokémon mới  +1 điểm","New Pokémon  +1 point"):Lang.t("Pokémon trùng  +2 điểm","Duplicate  +2 points")),cx,cy+fh+40,lastNew?0x80FF90:0xFFC070);
        }
        int ly=H-fh*2-32;
        if(state!=2&&!compact){Art.textSmallC(g,Lang.t("HUYỀN THOẠI HÔM NAY","TODAY'S LEGENDARIES"),cx,ly-fh-6,0xA890F0);int step=34,start=cx-((featured.length-1)*step+32)/2;for(int i=0;i<featured.length;i++)drawAvatar(g,featured[i],start+i*step,ly);}
        // Walking partners decorate both lower corners without entering the reward pool.
        int a=group==0?25:(group==1?387:(group==2?722:25)),b=group==0?150:(group==1?493:(group==2?810:387));
        if(!compact&&W>=220){drawPokemon(g,a,2,H-fh*4-24,40,36,RawAtlas.WALK,time/90);drawPokemon(g,b,W-42,H-fh*4-24,40,36,RawAtlas.WALK,time/90+2);}
        if(msg.length()>0)Art.textSmallC(g,msg,cx,H-fh*3-2,0xFFE070);
        Art.textSmallC(g,state==1?Lang.t("Các hệ đang bị hút vào...","Types are being pulled in..."):Lang.t("FIRE quay  * hướng dẫn  # tự động","FIRE draw  * help  # auto"),cx,H-fh-1,autoRoll?0x80FF90:0xD0D8F0);if(state==3)paintLegendReveal(g,cx,cy);else if(help)paintHelp(g);
    }
    private void paintLegendReveal(Graphics g,int cx,int cy){int W=game.W,H=game.H,fh=Art.fh,max=W+H,r=Math.min(max,time*max/700);if(r>=max-1){g.setColor(0xFFFFFF);g.fillRect(0,0,W,H);}else{g.setColor(0xFFFFFF);g.fillArc(cx-r,cy-r,r*2,r*2,0,360);}if(time<260)return;int rise=Math.min(12,(time-260)/35),pw=W<180?76:96,ph=W<180?70:88,py=H/2-ph/2-rise;drawPokemon(g,lastDex,cx-pw/2,py,pw,ph,RawAtlas.VICTORY,time/90);Art.textBC(g,name(lastDex),cx,py+ph+2,0x8A5A00);Art.textSmallC(g,Lang.t("HUYỀN THOẠI!","LEGENDARY!"),cx,Math.max(5,py-fh-4),0xC08000);Art.textSmallC(g,Lang.t("Nhấn 5 để tiếp tục","Press 5 to continue"),cx,H-fh-3,0x303848);}
    private void paintHelp(Graphics g){int W=game.W,H=game.H,fh=Art.fh,m=8,w=W-16,h=H-24,x=8,y=12;Art.box(g,x,y,w,h,0x101526,0xFFD060);Art.textBC(g,Lang.t("HƯỚNG DẪN GACHA","GACHA GUIDE"),W/2,y+4,0xFFD030);String s=Lang.t("Mỗi lượt tốn 1 Bóng. Pool đổi theo lịch Gen ghi trên màn hình. Chỉ 5 Pokémon Huyền thoại hiển thị hôm nay có thể xuất hiện, kể cả rơi sớm và mốc 100. Pokémon mới: +1 điểm. Pokémon trùng: +2 điểm, không hoàn Bóng. Đủ 100 điểm ưu tiên Huyền thoại hôm nay chưa sở hữu. Trúng Huyền thoại sớm đưa điểm về 0. Phím # bật/tắt tự động quay.","Each draw costs 1 Ball. The Gen pool follows the shown schedule. Only today's 5 displayed Legendaries can appear, both early and at pity 100. New Pokémon: +1 point. Duplicate: +2 points, no refund. Pity prioritizes an unowned featured Legendary. An early Legendary resets points. Press # to toggle auto-roll.");String[] lines=Art.wrap(s,w-2*m,30);int yy=y+fh+9;for(int i=0;i<lines.length&&yy<y+h-fh*2;i++){Art.textSmall(g,lines[i],x+m,yy,0xD8E4F2);yy+=fh;}Art.textSmallC(g,Lang.t("*/FIRE/0: đóng","*/FIRE/0: close"),W/2,y+h-fh-2,0x80A8D0);}
}

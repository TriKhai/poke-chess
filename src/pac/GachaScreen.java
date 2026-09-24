package pac;

import javax.microedition.lcdui.Graphics;

/** Daily all-in-one Gacha with vortex animation and a 100-point Legendary pity. */
public final class GachaScreen extends Screen{
    private static final int[] OX={0,18,26,18,0,-18,-26,-18},OY={-26,-18,0,18,26,18,0,-18};
    private final Rng rng=new Rng((int)System.currentTimeMillis());
    private final int group=GachaRules.todayGroup();
    private int state=0,time=0,lastDex=-1;private boolean lastNew,lastPity;private String msg="";
    public GachaScreen(Game g){super(g);}
    public void update(int dt){time+=dt;if(state==1&&time>=2200)resolve();}
    public void key(int k){
        if(k==Game.K_0||k==Game.K_SOFT2){if(state!=1)game.setScreen(new ExploreHubScreen(game));return;}
        if((k==Game.K_FIRE||k==Game.K_SOFT1)&&state!=1){if(Save.balls<=0){msg=Lang.t("Không đủ Bóng!","Not enough Balls!");state=2;return;}Save.balls--;Save.save();state=1;time=0;lastDex=-1;msg="";}
    }
    private void resolve(){
        int candidate=GachaRules.pick(rng,group,false);boolean candidateNew=!Save.ownsDex(candidate);int gain=candidateNew?1:2;
        lastPity=Save.gachaPoints+gain>=100;lastDex=lastPity?GachaRules.pick(rng,group,true):candidate;
        lastNew=Save.unlockDex(lastDex);Save.caught++;
        if(GachaRules.legendary(lastDex))Save.gachaPoints=0;else Save.gachaPoints+=lastNew?1:2;
        if(Save.gachaPoints>99)Save.gachaPoints=99;
        Save.save();state=2;time=0;
        msg=Lang.t("CHÚC MỪNG! Bạn nhận được ","CONGRATULATIONS! You got ")+name(lastDex);
    }
    private String name(int dex){if(dex<=Data.N)return Data.name[dex-1];int di=dexIndex(dex);return di>=0?CollectionDex.NAME[di]:"#"+dex;}
    private int dexIndex(int dex){for(int i=0;i<CollectionDex.COUNT;i++)if(CollectionDex.DEX[i]==dex)return i;return -1;}
    private void drawPokemon(Graphics g,int dex,int x,int y,int w,int h,int action,int clock){if(dex<=Data.N){if(!RawAtlas.draw(g,dex-1,x,y,w,h,action,7,clock))Art.sprite(g,dex-1,x,y,Math.min(w,h));}else{int di=dexIndex(dex);if(di<0||!CollectionAtlas.draw(g,di,x,y,w,h,action,7,clock))Art.dexAvatar(g,di,x+(w-32)/2,y+(h-32)/2);}}
    private void drawAvatar(Graphics g,int dex,int x,int y){if(dex<=Data.N)Art.avatarMini(g,dex-1,x,y);else{int di=dexIndex(dex);if(di>=0)Art.dexAvatar(g,di,x-6,y-6);}}
    public void paint(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,cx=W/2;boolean compact=UiLayout.compact(W,H);int cy=compact?fh*5:Math.max(fh*6,H/2-12);
        g.setColor(0x0B0B20);g.fillRect(0,0,W,H);for(int y=0;y<H;y+=12){g.setColor(0x10132A+(y&24)*0x010101);g.fillRect(0,y,W,12);}
        Art.textBC(g,Lang.t("GACHA HỐ ĐEN","BLACK HOLE GACHA"),cx,3,0xFFD030);
        Art.textSmallC(g,GachaRules.scheduleName(group)+"  "+Lang.t("Bóng ","Balls ")+Save.balls,cx,fh+5,0x80D8FF);
        Art.textSmallC(g,Lang.t("Mốc Huyền thoại: ","Legendary pity: ")+Save.gachaPoints+"/100",cx,fh*2+5,0xFFE070);
        int phase=state==1?time:0;GachaFx.portal(g,cx,cy,time);
        if(state!=2||lastDex<0){
            int radius=phase>1400?Math.max(2,26-(phase-1400)*24/800):26;
            int shift=state==1?(time/90)&7:0;
            for(int i=0;i<8;i++){int p=(i+shift)&7,type=(i+(state==1?time/180*8:0))%Data.NT,x=cx+OX[p]*radius/26,y=cy+OY[p]*radius/26;g.setColor(Data.TCOL[type]);g.fillArc(x-8,y-8,16,16,0,360);g.setColor(0xFFFFFF);g.drawArc(x-8,y-8,15,15,0,360);Art.textSmallC(g,Data.TSHORT[type],x,y-4,0x101018);}
        }else{
            int pw=compact?56:76,ph=compact?52:72;drawPokemon(g,lastDex,cx-pw/2,cy-ph/2,pw,ph,RawAtlas.WALK,time/90);
            Art.textBC(g,name(lastDex),cx,cy+39,GachaRules.legendary(lastDex)?0xFFD030:0xFFFFFF);
            Art.textSmallC(g,lastPity?Lang.t("MỐC 100 - HUYỀN THOẠI KHÔNG TRÙNG","100 PITY - NEW LEGENDARY"):(lastNew?Lang.t("Pokémon mới  +1 điểm","New Pokémon  +1 point"):Lang.t("Pokémon trùng  +2 điểm","Duplicate  +2 points")),cx,cy+fh+40,lastNew?0x80FF90:0xFFC070);
        }
        int[] legends=GachaRules.featuredLegendaries(group,compact?3:4);int ly=compact?H-fh*4:H-fh*5-22;
        if(state!=2){Art.textSmallC(g,Lang.t("HUYỀN THOẠI HÔM NAY","TODAY'S LEGENDARIES"),cx,ly-fh,0xA890F0);int shown=0,step=compact?32:34,start=cx-(legends.length*step)/2;for(int i=0;i<legends.length;i++)if(legends[i]>0){drawAvatar(g,legends[i],start+shown*step,ly);shown++;}}
        // Walking partners decorate both lower corners without entering the reward pool.
        int a=group==0?25:(group==1?387:(group==2?722:25)),b=group==0?150:(group==1?493:(group==2?810:387));
        if(!compact&&W>=220){drawPokemon(g,a,2,H-fh*4-24,40,36,RawAtlas.WALK,time/90);drawPokemon(g,b,W-42,H-fh*4-24,40,36,RawAtlas.WALK,time/90+2);}
        if(msg.length()>0)Art.textSmallC(g,msg,cx,H-fh*3-2,0xFFE070);
        Art.textSmallC(g,state==1?Lang.t("Các hệ đang bị hút vào...","Types are being pulled in..."):Lang.t("FIRE: quay 1 Bóng   0: về","FIRE: draw 1 Ball   0: back"),cx,H-fh-1,0xD0D8F0);
    }
}

package pac;

import javax.microedition.lcdui.Graphics;

/** End-of-trip results shown when the player voluntarily leaves an Explore map. */
public final class ExploreSummaryScreen extends Screen{
    private final int gen,caught,defeated,missed,legends,reward;
    public ExploreSummaryScreen(Game g,int generation,int c,int d,int m,int l,int r){super(g);gen=generation;caught=c;defeated=d;missed=m;legends=l;reward=r;}
    public void update(int dt){}
    public void key(int k){if(k==Game.K_FIRE||k==Game.K_SOFT1||k==Game.K_0||k==Game.K_SOFT2)game.setScreen(new ExploreMapScreen(game));}
    public void paint(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,w=Math.min(W-14,UiLayout.landscape(W,H)?330:220),h=Math.min(H-16,fh*12+24),x=(W-w)/2,y=(H-h)/2;
        g.setColor(0x090E18);g.fillRect(0,0,W,H);Art.box(g,x,y,w,h,0x111A2C,0xFFD030);
        Art.textBC(g,Lang.t("TỔNG KẾT KHÁM PHÁ","EXPLORE SUMMARY"),W/2,y+6,0xFFD030);
        Art.textSmallC(g,ExploreMapLayout.name(gen),W/2,y+fh+8,0xB0F0B0);
        int yy=y+fh*3;row(g,Lang.t("Đã bắt","Caught"),caught,x,w,yy);yy+=fh+3;
        row(g,Lang.t("Đã hạ","Defeated"),defeated,x,w,yy);yy+=fh+3;
        row(g,Lang.t("Bắt hụt","Failed throws"),missed,x,w,yy);yy+=fh+3;
        row(g,Lang.t("Thần thú bắt được","Legendaries caught"),legends,x,w,yy);yy+=fh+5;
        g.setColor(0x31415A);g.drawLine(x+8,yy,x+w-9,yy);yy+=5;
        Art.textB(g,Lang.t("Thưởng chuyến đi","Trip reward"),x+10,yy,0xFFFFFF);BallArt.amount(g,BallArt.NORMAL,reward,x+w-55,yy,0x80E8FF);yy+=fh+5;
        Art.textSmallC(g,Lang.t("Tổng map: ","Map total: ")+Save.exploreMapCaught[gen-1]+Lang.t(" bắt • "," caught • ")+ExploreRules.uniqueCaught(gen)+"/"+ExploreRules.mapSpeciesCount(gen)+Lang.t(" loài"," species"),W/2,yy,0x90D8A0);
        Art.textSmallC(g,Lang.t("Nhấn 5 để tiếp tục","Press 5 to continue"),W/2,y+h-fh-6,0xFFE080);
    }
    private void row(Graphics g,String label,int value,int x,int w,int y){Art.textSmall(g,label,x+10,y,0xB8C8D8);Art.textR(g,""+value,x+w-10,y,0xFFFFFF);}
}

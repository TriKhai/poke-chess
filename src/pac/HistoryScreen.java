package pac;

import javax.microedition.lcdui.Graphics;

/** Browse the last five completed Auto Chess runs. */
public final class HistoryScreen extends Screen {
    private int selected;
    public HistoryScreen(Game g){super(g);}
    public void update(int dt){}
    public void key(int k){if(k==Game.K_UP||k==Game.K_LEFT){if(selected>0)selected--;}else if(k==Game.K_DOWN||k==Game.K_RIGHT){if(selected+1<HistoryStore.count)selected++;}else if(k==Game.K_0||k==Game.K_SOFT2||k==Game.K_FIRE||k==Game.K_SOFT1)game.setScreen(new ChessModeScreen(game));}
    private String mode(int m){return m==Run.MODE_THIRTY?Lang.t("30 vòng","30 rounds"):m==Run.MODE_GEN1?Lang.t("Thế hệ 1","Gen 1"):m==Run.MODE_UNLIMITED?"DEV":Lang.t("Thường","Normal");}
    public void paint(Graphics g){int W=game.W,H=game.H,fh=Art.fh;g.setColor(0x101827);g.fillRect(0,0,W,H);Art.textBC(g,Lang.t("LỊCH SỬ ĐẤU","BATTLE HISTORY"),W/2,3,0xFFD030);
        if(HistoryStore.count==0){Art.textC(g,Lang.t("Chưa có lượt chơi đã kết thúc","No completed runs yet"),W/2,H/2,0x90A0B8);Art.textC(g,"0: "+Lang.t("về","back"),W/2,H-fh-3,0x8090B0);return;}
        int top=fh+5,av=W>=216?24:20,rowH=av+fh+8,visible=Math.max(1,(H-top-fh-4)/rowH),first=Math.max(0,selected-visible+1);
        for(int h=first;h<HistoryStore.count&&h<first+visible;h++){
            int y=top+(h-first)*rowH,color=HistoryStore.victory[h]?0xFFD060:0xFF7070;
            g.setColor(h==selected?0x263B55:0x182438);g.fillRect(3,y,W-6,rowH-2);
            g.setColor(h==selected?0xFFD060:0x40506A);g.drawRect(3,y,W-7,rowH-3);
            String result=HistoryStore.victory[h]?Lang.t("Nhà Vô Địch","Champion"):Lang.t("Kẻ bại trận","Defeated");
            Art.textSmall(g,result+" - "+mode(HistoryStore.mode[h])+" [HP:"+HistoryStore.hp[h]+", $:"+HistoryStore.gold[h]+"]",6,y+2,color);
            int start=3,step=(W-6-av)/8,ay=y+fh+3;
            for(int i=0;i<9;i++){int sp=HistoryStore.team[h][i],ax=start+i*step;g.setColor(sp>=0?0xD9DEDF:0x222D3D);g.fillRect(ax,ay,av,av);if(sp>=0){if(av==24)Art.avatarHistory(g,sp,ax,ay);else Art.avatarMini(g,sp,ax,ay);}g.setColor(sp>=0?0x526780:0x303B4D);g.drawRect(ax,ay,av-1,av-1);}
        }
        Art.textSmallC(g,Lang.t("2/8: chọn   0: về","2/8: select   0: back"),W/2,H-fh-2,0x8090B0);
    }
    static void drawGraph(Graphics g,int[] data,int n,int x,int y,int w,int h){
        g.setColor(0x182438);g.fillRect(x,y,w,h);g.setColor(0x526780);g.drawRect(x,y,w-1,h-1);
        if(n<=0)return;
        int min=-1,max=1;
        for(int i=0;i<n;i++){if(data[i]<min)min=data[i];if(data[i]>max)max=data[i];}
        int span=max-min;if(span<1)span=1;
        int zero=graphY(0,min,span,y,h);
        g.setColor(0x40506A);g.drawLine(x+2,zero,x+w-3,zero);
        for(int i=1;i<4;i++){int gx=x+2+i*(w-5)/4;g.setColor(0x29384D);g.drawLine(gx,y+2,gx,y+h-3);}
        int px=x+2,py=graphY(data[0],min,span,y,h);
        if(n==1){g.setColor(0x60E878);g.fillArc(px-2,py-2,5,5,0,360);return;}
        final int steps=8;
        for(int i=0;i<n-1;i++){
            int p0=data[i>0?i-1:i],p1=data[i],p2=data[i+1],p3=data[i+2<n?i+2:i+1];
            int x1=x+2+i*(w-5)/(n-1),x2=x+2+(i+1)*(w-5)/(n-1);
            int color=p2>=p1?0x60E878:0xFF6060;
            for(int s=1;s<=steps;s++){
                int t=s*256/steps,t2=t*t/256,t3=t2*t/256;
                int value=(2*p1+(-p0+p2)*t/256+(2*p0-5*p1+4*p2-p3)*t2/256+(-p0+3*p1-3*p2+p3)*t3/256)/2;
                if(value<min)value=min;if(value>max)value=max;
                int nx=x1+(x2-x1)*s/steps,ny=graphY(value,min,span,y,h);
                thickLine(g,px,py,nx,ny,color);px=nx;py=ny;
            }
            g.setColor(color);g.fillArc(px-1,py-1,3,3,0,360);
        }
        g.setColor(0xFFF0A0);g.fillArc(px-2,py-2,5,5,0,360);
    }
    private static int graphY(int value,int min,int span,int y,int h){return y+h-3-(value-min)*(h-6)/span;}
    private static void thickLine(Graphics g,int x1,int y1,int x2,int y2,int color){
        g.setColor(0x243044);g.drawLine(x1,y1+1,x2,y2+1);
        g.setColor(color);g.drawLine(x1,y1,x2,y2);g.drawLine(x1+1,y1,x2+1,y2);
    }
}

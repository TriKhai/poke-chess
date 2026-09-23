package pac;

import javax.microedition.lcdui.Graphics;

/** Browse the last five completed Auto Chess runs. */
public final class HistoryScreen extends Screen {
    private int selected;
    public HistoryScreen(Game g){super(g);}
    public void update(int dt){}
    public void key(int k){if(k==Game.K_UP||k==Game.K_LEFT){if(selected>0)selected--;}else if(k==Game.K_DOWN||k==Game.K_RIGHT){if(selected+1<HistoryStore.count)selected++;}else if(k==Game.K_0||k==Game.K_SOFT2||k==Game.K_FIRE||k==Game.K_SOFT1)game.setScreen(new ChessModeScreen(game));}
    private String mode(int m){return m==Run.MODE_THIRTY?"30":m==Run.MODE_GEN1?"GEN1":m==Run.MODE_UNLIMITED?"DEV":"NORMAL";}
    public void paint(Graphics g){int W=game.W,H=game.H,fh=Art.fh;g.setColor(0x101827);g.fillRect(0,0,W,H);Art.textBC(g,Lang.t("LỊCH SỬ ĐẤU","BATTLE HISTORY"),W/2,3,0xFFD030);
        if(HistoryStore.count==0){Art.textC(g,Lang.t("Chưa có lượt chơi đã kết thúc","No completed runs yet"),W/2,H/2,0x90A0B8);Art.textC(g,"0: "+Lang.t("về","back"),W/2,H-fh-3,0x8090B0);return;}
        int y=fh+5;String summary=(selected+1)+"/"+HistoryStore.count+"  "+mode(HistoryStore.mode[selected])+"  "+Lang.t("Vòng ","Round ")+HistoryStore.round[selected]+"  "+(HistoryStore.victory[selected]?Lang.t("THẮNG","WIN"):Lang.t("THUA","LOSS"));Art.textSmallC(g,summary,W/2,y,HistoryStore.victory[selected]?0x60E878:0xFF7070);y+=fh+2;
        drawGraph(g,HistoryStore.path[selected],HistoryStore.resultCount[selected],4,y,W-8,38);y+=41;
        Art.textSmallC(g,"HP "+HistoryStore.hp[selected]+"  "+Lang.t("Vàng ","Gold ")+HistoryStore.gold[selected],W/2,y,0xFFD060);y+=fh+2;
        int gap=2,cw=(W-8-gap*2)/3,ch=Math.max(27,(H-y-fh-5)/3);for(int i=0;i<9;i++){int cx=4+(i%3)*(cw+gap),cy=y+(i/3)*ch,sp=HistoryStore.team[selected][i];g.setColor(0x182438);g.fillRect(cx,cy,cw,ch-2);g.setColor(sp>=0?0x526780:0x303B4D);g.drawRect(cx,cy,cw-1,ch-3);if(sp<0)continue;Art.avatarTiny(g,sp,cx+2,cy+2);Art.textSmall(g,Data.name[sp],cx+20,cy+2,0xD8E0F0);for(int s=0;s<3;s++){int id=HistoryStore.items[selected][i*3+s],ix=cx+20+s*10,iy=cy+14;g.setColor(id>=0?0x263448:0x172131);g.fillArc(ix,iy,9,9,0,360);g.setColor(id>=0?0x80D8FF:0x4B586C);g.drawArc(ix,iy,8,8,0,360);if(id>=0)Art.itemIconTiny(g,id,ix+1,iy+1);}}
        Art.textSmallC(g,Lang.t("</>: đổi lượt   0: về","</>: change run   0: back"),W/2,H-fh-2,0x8090B0);
    }
    static void drawGraph(Graphics g,int[] data,int n,int x,int y,int w,int h){g.setColor(0x182438);g.fillRect(x,y,w,h);g.setColor(0x526780);g.drawRect(x,y,w-1,h-1);int zero=y+h/2;g.setColor(0x40506A);g.drawLine(x+2,zero,x+w-3,zero);if(n<=0)return;int min=-1,max=1;for(int i=0;i<n;i++){if(data[i]<min)min=data[i];if(data[i]>max)max=data[i];}int span=max-min;if(span<1)span=1;int px=x+2,py=y+h-3-(data[0]-min)*(h-6)/span;for(int i=1;i<n;i++){int nx=x+2+i*(w-5)/Math.max(1,n-1),ny=y+h-3-(data[i]-min)*(h-6)/span;g.setColor(data[i]>=data[i-1]?0x60E878:0xFF6060);g.drawLine(px,py,nx,ny);px=nx;py=ny;}g.fillArc(px-2,py-2,5,5,0,360);}
}

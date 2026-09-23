package pac;

import javax.microedition.lcdui.Graphics;

/** One-time, RMS-persisted choice between normal progression and developer tools. */
public final class PlayPathScreen extends Screen {
    private int selected=0;

    public PlayPathScreen(Game g){super(g);}

    public void update(int dt){}

    public void key(int k){
        if(k==Game.K_UP||k==Game.K_LEFT)selected=0;
        else if(k==Game.K_DOWN||k==Game.K_RIGHT)selected=1;
        else if(k==Game.K_FIRE||k==Game.K_SOFT1){
            Save.playPath=selected;
            if(selected==1)Save.enableCheat();else{Save.cheatMode=false;Save.save();}
            game.setScreen(new MenuScreen(game));
        }
    }

    public void paint(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh;
        g.setColor(0x101827);g.fillRect(0,0,W,H);
        Art.textBC(g,"BẠN THEO HỆ NÀO?",W/2,12,0xFFE060);
        Art.textSmallC(g,"Chỉ được chọn một lần",W/2,12+fh+4,0xA8B5C8);
        String[] title={"KHỔ DĂM","VẠCH ĐÍCH"};
        String[] desc={"Cày chay toàn bộ nội dung","Mở công cụ cheat cho dev"};
        int bw=Math.min(W-24,190),bh=fh*3+8,x=(W-bw)/2,y=H/2-bh-5;
        for(int i=0;i<2;i++){
            int by=y+i*(bh+8);g.setColor(i==selected?0x304C3D:0x202C3F);g.fillRect(x,by,bw,bh);
            g.setColor(i==selected?0x60E878:0x4B586C);g.drawRect(x,by,bw-1,bh-1);
            Art.textBC(g,title[i],W/2,by+4,i==selected?0xFFFFFF:0xA8B5C8);
            Art.textSmallC(g,desc[i],W/2,by+fh+8,i==selected?0xC8F0D0:0x78869A);
        }
        Art.textSmallC(g,"FIRE: xác nhận",W/2,H-fh-6,0x8090B0);
    }
}

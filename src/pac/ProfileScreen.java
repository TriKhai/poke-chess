package pac;

import javax.microedition.lcdui.Graphics;

/** Personal trainer card and owned-Pokemon avatar picker. */
public final class ProfileScreen extends Screen {
    private int tab=0,sel=0,top=0,anim=0;
    public ProfileScreen(Game g){super(g);}
    public void update(int dt){anim+=dt;}

    public void key(int k){
        int cols=columns(),count=count();
        if(k==Game.K_LEFT){if(sel%cols>0)sel--;else changeTab(-1);}
        else if(k==Game.K_RIGHT){if(sel%cols<cols-1&&sel+1<count)sel++;else changeTab(1);}
        else if(k==Game.K_UP){if(sel>=cols)sel-=cols;else changeTab(-1);}
        else if(k==Game.K_DOWN){if(sel+cols<count)sel+=cols;}
        else if(k==Game.K_1)changeTab(-1);
        else if(k==Game.K_3||k==Game.K_STAR)changeTab(1);
        else if(k==Game.K_FIRE||k==Game.K_SOFT1){int dex=dexAt(sel);if(Save.ownsDex(dex))Save.chooseProfileAvatar(dex);}
        else if(k==Game.K_POUND&&Save.profileName.length()==0)game.requestProfileName();
        else if(k==Game.K_0||k==Game.K_SOFT2)game.setScreen(new MenuScreen(game));
    }

    private void changeTab(int d){tab=(tab+d+10)%10;sel=0;top=0;}
    private int columns(){int c=game.W/38;return c<3?3:(c>6?6:c);}
    private int count(){if(tab==0)return Data.N+CollectionDex.COUNT;if(tab<=3)return tab==1?151:(tab==2?100:135);return CollectionDex.countGen(tab);}
    private int dexAt(int local){
        if(tab==0)return local<Data.N?local+1:CollectionDex.DEX[local-Data.N];
        if(tab<=3)return (tab==1?1:(tab==2?152:252))+local;
        int first=CollectionDex.firstOfGen(tab);return first<0?1:CollectionDex.DEX[first+local];
    }
    private int dexCollectionIndex(int dex){for(int i=0;i<CollectionDex.COUNT;i++)if(CollectionDex.DEX[i]==dex)return i;return -1;}

    public void paint(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh;
        g.setColor(0x101827);g.fillRect(0,0,W,H);
        Art.textBC(g,Lang.t("HỒ SƠ CÁ NHÂN","MY PROFILE"),W/2,2,0xFFD030);
        int modelY=fh+5,dex=Save.profileAvatarDex,groupX=W/2-50;drawProfileAvatar(g,dex,groupX,modelY+8);
        if(dex<=Data.N){int sp=dex-1,vw=Data.visualWidth(sp),vh=Data.visualHeight(sp);if(!RawAtlas.draw(g,sp,groupX+36,modelY,64,48,RawAtlas.WALK,7,anim/90))Art.battleSprite(g,sp,groupX+68-vw/2,modelY,anim/90,RawAtlas.WALK,7);modelY+=Math.max(vh,42);}
        else{int di=dexCollectionIndex(dex);if(di>=0&&!CollectionAtlas.draw(g,di,groupX+36,modelY,64,48,RawAtlas.WALK,7,anim/90))Art.dexAvatar(g,di,groupX+52,modelY+8);modelY+=50;}
        Art.textBC(g,Save.displayName(),W/2,modelY,0xFFFFFF);modelY+=fh+1;
        if(Save.profileName.length()==0)Art.textSmallC(g,Lang.t("#: đặt tên một lần","#: set name once"),W/2,modelY,0x80D8FF);
        else Art.textSmallC(g,Lang.t("Tên đã được xác nhận","Name confirmed"),W/2,modelY,0x708098);
        String medals=(ProgressionRules.cleared(Run.MODE_NORMAL)?"N ":"- ")+(ProgressionRules.cleared(Run.MODE_THIRTY)?"30 ":"-- ")+(ProgressionRules.cleared(Run.MODE_GEN1)?"G1":"--");
        Art.textSmallR(g,medals,W-3,modelY,0xFFD060);
        modelY+=fh+3;

        String tabName=tab==0?Lang.t("TẤT CẢ","ALL"):"GEN "+tab;
        g.setColor(0x293A58);g.fillRect(0,modelY,W,fh+5);
        Art.textBC(g,"<  "+tabName+"  >",W/2,modelY+2,0xFFD060);modelY+=fh+7;
        int cols=columns(),cell=W/cols,row=38,rows=(H-modelY-fh*2)/row;if(rows<1)rows=1;
        int firstRow=sel/cols;if(firstRow<top)top=firstRow;if(firstRow>=top+rows)top=firstRow-rows+1;
        int count=count();
        for(int r=0;r<rows;r++)for(int c=0;c<cols;c++){
            int i=(top+r)*cols+c;if(i>=count)continue;int x=c*cell,y=modelY+r*row,d=dexAt(i);boolean owned=Save.ownsDex(d),hover=i==sel;
            if(hover){g.setColor(0xFFF060);g.drawRect(x+1,y+1,cell-3,35);g.drawRect(x+2,y+2,cell-5,33);}
            else if(d==Save.profileAvatarDex){g.setColor(0x60D8FF);g.drawRect(x+2,y+2,cell-5,33);}
            if(owned){if(d<=Data.N)Art.avatar(g,d-1,x+(cell-32)/2,y+3);else Art.dexAvatar(g,dexCollectionIndex(d),x+(cell-32)/2,y+3);}
            else{g.setColor(0x253148);g.fillRect(x+(cell-30)/2,y+4,30,30);Art.textBC(g,"?",x+cell/2,y+11,0x7C879A);}
        }
        int current=dexAt(sel);String label=Save.ownsDex(current)?(current<=Data.N?(Save.inCamp(current-1)?Lang.t("Đang ở Bãi Pokémon","In Pokémon Camp"):Data.name[current-1]):"#"+current):Lang.t("Chưa sở hữu","Not owned");
        Art.textSmallC(g,label+"  #"+current,W/2,H-fh*2,Save.ownsDex(current)?0xFFFFFF:0x788398);
        Art.textSmallC(g,Lang.t("1/3: Gen  FIRE: chọn  0: về","1/3: Gen  FIRE: choose  0: back"),W/2,H-fh-1,0x80A8D0);
    }
    private void drawProfileAvatar(Graphics g,int dex,int x,int y){g.setColor(0x26354D);g.fillRect(x-2,y-2,36,36);g.setColor(0x60D8FF);g.drawRect(x-2,y-2,35,35);if(dex<=Data.N)Art.avatar(g,dex-1,x,y);else{int di=dexCollectionIndex(dex);if(di>=0)Art.dexAvatar(g,di,x,y);}}
}

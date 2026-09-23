package pac;

import javax.microedition.lcdui.Graphics;

/** Pokedex-like list of every species; locked families are hidden. */
public final class CollectionScreen extends Screen {
    private int sel = 0, top = 0;
    private int tab=0,typeSel=0,itemSel=0;
    private int itemDescScroll=0;
    private boolean detail = false;
    private boolean itemDetail = false;
    private int animTime = 0, previewState = 0, previewDir = 0;
    private int previewStatus = 0, previewAttackKind = 1;
    private static final String[] CLIP = { "IDLE", "ATTACK", "VICTORY" };
    private static final String[] STATUS = { "ARMOR BREAK", "BLINDED", "BURN", "CHARM",
            "CONFUSION", "CURSE", "FATIGUE", "FLINCH", "FREEZE", "LOCKED",
            "PARALYSIS", "POISON", "PROTECT", "SILENCE", "SLEEP", "WOUND" };
    private static final String[] DIR = { "DOWN", "DOWN-R", "RIGHT", "UP-R", "UP", "UP-L", "LEFT", "DOWN-L" };

    public CollectionScreen(Game g) {
        super(g);
    }

    public void update(int dt) { animTime += dt; }

    public void key(int k) {
        if(itemDetail){
            if(k==Game.K_FIRE||k==Game.K_SOFT1||k==Game.K_SOFT2||k==Game.K_0)itemDetail=false;
            return;
        }
        if (detail) {
            switch (k) {
                case Game.K_LEFT: previewDir = (previewDir + 7) & 7; break;
                case Game.K_RIGHT: previewDir = (previewDir + 1) & 7; break;
                case Game.K_UP: previewState = (previewState + 2) % 3; break;
                case Game.K_DOWN: previewState = (previewState + 1) % 3; break;
                case Game.K_1: previewStatus = (previewStatus + STATUS.length - 1) % STATUS.length; break;
                case Game.K_3: case Game.K_STAR: previewStatus = (previewStatus + 1) % STATUS.length; break;
                case Game.K_7: previewAttackKind = (previewAttackKind + 2) % 3; break;
                case Game.K_9: previewAttackKind = (previewAttackKind + 1) % 3; break;
                case Game.K_FIRE: case Game.K_SOFT1: case Game.K_SOFT2: case Game.K_0:
                    detail = false; break;
                default: break;
            }
            return;
        }
        switch (k) {
            case Game.K_LEFT: tab=(tab+2)%3; top=0; itemDescScroll=0; break;
            case Game.K_RIGHT: tab=(tab+1)%3; top=0; itemDescScroll=0; break;
            case Game.K_UP:
                if(tab==0&&sel>0)sel--; else if(tab==1&&typeSel>0)typeSel--; else if(tab==2&&itemSel>0){itemSel--;itemDescScroll=0;}
                break;
            case Game.K_DOWN:
                if(tab==0&&sel<Data.N-1)sel++; else if(tab==1&&typeSel<Data.NT-1)typeSel++; else if(tab==2&&itemSel<ItemData.count()-1){itemSel++;itemDescScroll=0;}
                break;
            case Game.K_1: if(tab==2&&itemDescScroll>0)itemDescScroll--; break;
            case Game.K_3: if(tab==2)itemDescScroll++; break;
            case Game.K_FIRE: case Game.K_SOFT1:
                if (tab==0&&Save.has(sel)) { detail = true; animTime = 0; }
                else if(tab==2)itemDetail=true;
                break;
            case Game.K_SOFT2: case Game.K_0:
                game.setScreen(new MenuScreen(game));
                break;
            default: break;
        }
    }

    public void paint(Graphics g) {
        if(itemDetail){paintItemDetail(g);return;}
        if (detail) { paintDetail(g); return; }
        int W = game.W, H = game.H, fh = Art.fh;
        g.setColor(0x182030);
        g.fillRect(0, 0, W, H);
        paintTabs(g);
        if(tab==1){paintTypes(g);return;}
        if(tab==2){paintItems(g);return;}
        Art.textB(g, Lang.t("Bộ sưu tập ", "Collection ") + Save.familiesUnlocked() + "/" + Data.countFamilies(), 4, fh+7, 0xFFD030);

        int rowH = 34;
        int detailMinH = fh * 5 + 6;
        int listY = fh * 2 + 11;
        int rows = (H - listY - detailMinH) / rowH;
        if (rows < 1) rows = 1;
        if (sel < top) top = sel;
        if (sel >= top + rows) top = sel - rows + 1;

        for (int r = 0; r < rows && top + r < Data.N; r++) {
            int i = top + r;
            int y = listY + r * rowH;
            boolean has = Save.has(i);
            if (i == sel) {
                g.setColor(0x305090);
                g.fillRect(0, y, W, rowH);
            }
            if (has) Art.avatar(g,i,2,y+1);
            else Art.text(g, "?", 2 + rowH / 3, y + (rowH - fh) / 2, 0x707080);
            String nm = has ? Data.name[i] : "???";
            Art.text(g, "#" + (i + 1) + " " + nm, 38, y + (rowH - fh) / 2, has ? 0xFFFFFF : 0x808090);
            if (has) {
                Art.textR(g, Data.CATEGORY_NAME[Data.category[i]], W - 3, y + (rowH - fh) / 2, 0xB0C0E0);
            }
        }

        int dy = listY + rows * rowH;
        int detailH = H - dy;
        g.setColor(0x0C1420);
        g.fillRect(0, dy, W, detailH);
        if (Save.has(sel)) {
            Art.avatar(g, sel, 3, dy + 2);
            int x = 38;
            Art.typeIcon(g,Data.t1[sel],x,dy+1);x+=16;
            if (Data.t2[sel] >= 0) Art.typeIcon(g,Data.t2[sel],x,dy+1);
            Art.textSmall(g, "HP " + Data.hp[sel] + " AT " + Data.atk[sel] + " DF " + Data.def[sel]
                    + "/" + Data.speDef[sel] + " SP " + Data.speed[sel], 38, dy + fh + 3, 0xE0E8FF);
            int yy = Art.para(g, Lang.moveName(Data.skillName[sel]) + ": " + AbilityBehavior.description(sel),
                    38, dy + fh * 2 + 3, W - 42, 0xA0FFA0, 2);
            String evo = Data.evo[sel] >= 0 ? Lang.t("Tiến hóa: ", "Evolves: ") + Data.name[Data.evo[sel]] : Lang.t("Dạng cuối", "Final form");
            Art.text(g, evo, 4, yy, 0xC0C0C0);
        } else {
            Art.text(g, Lang.t("Chưa được khám phá.", "Not discovered yet."), 4, dy + 3, 0x9090A0);
            Art.text(g, Lang.t("Hãy bắt trong chế độ Khám phá!", "Catch it in Explore mode!"), 4, dy + fh + 3, 0x9090A0);
        }
        Art.textR(g, Lang.t("FIRE chi tiết", "FIRE detail"), W - 3, fh+7, 0x80A8D0);
    }

    private void paintTabs(Graphics g){
        int W=game.W,w=W/3,h=Art.fh+5;
        for(int i=0;i<3;i++){
            g.setColor(i==tab?0x60708A:0x303B52);g.fillRect(i*w,0,i==2?W-i*w:w-1,h);
            if(i==tab){g.setColor(0xFFD030);g.fillRect(i*w,h-2,i==2?W-i*w:w-1,2);}
            String title=i==0?"POKEMON":(i==1?Lang.t("HỆ","TYPES"):Lang.t("VẬT PHẨM","ITEMS"));
            Art.textC(g,title,i*w+(i==2?W-i*w:w)/2,2,i==tab?0xFFFFFF:0xA8B0C0);
        }
    }

    private void paintTypes(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,y0=fh+8,row=Math.max(18,fh+5),rows=(H-y0-fh*4-6)/row;
        if(rows<1)rows=1;if(typeSel<top)top=typeSel;if(typeSel>=top+rows)top=typeSel-rows+1;
        for(int r=0;r<rows&&top+r<Data.NT;r++){
            int t=top+r,y=y0+r*row;if(t==typeSel){g.setColor(0x405273);g.fillRect(0,y,W,row);}
            Art.typeIcon(g,t,3,y+1);Art.textB(g,Lang.typeName(t),22,y+2,0xFFFFFF);
            Art.textR(g,SynergyEffects.marks(t,0),W-3,y+2,0xFFD060);
        }
        int py=y0+rows*row;Art.box(g,2,py,W-4,H-py-2,0x101827,Data.TCOL[typeSel]);
        Art.typeIcon(g,typeSel,6,py+4);Art.textB(g,Lang.typeName(typeSel),25,py+4,Data.TCOL[typeSel]);
        Art.para(g,Lang.synergyLongDesc(typeSel),6,py+fh+5,W-12,0xE0E8F0,4);
    }

    private void paintItems(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh,y0=fh+8,row=27,detailMinH=fh*7+8;
        int rows=(H-y0-detailMinH)/row;if(rows<1)rows=1;
        if(itemSel<top)top=itemSel;if(itemSel>=top+rows)top=itemSel-rows+1;
        for(int r=0;r<rows&&top+r<ItemData.count();r++){
            int i=top+r,y=y0+r*row;if(i==itemSel){g.setColor(0x405273);g.fillRect(0,y,W,row-1);}
            Art.itemIcon(g,i,2,y+1);Art.textB(g,ItemData.name(i),30,y+3,i==itemSel?0xFFFFFF:0xC8D0E0);
            Art.textSmall(g,ItemData.ID[i],30,y+fh+3,0x7088A0);
        }
        int py=y0+rows*row,detailH=H-py;Art.box(g,2,py,W-4,detailH,0x101827,0x526780);
        Art.itemIcon(g,itemSel,5,py+4);Art.textB(g,ItemData.name(itemSel),34,py+4,0xFFD060);
        Art.textSmall(g,ItemData.kind(itemSel),34,py+fh+4,0x80D8FF);
        int textY=py+fh*2+4,visible=(H-textY-fh-3)/fh;if(visible<1)visible=1;
        String[] wrapped=Art.wrap(ItemData.desc(itemSel),W-14,64);
        int maxScroll=Math.max(0,wrapped.length-visible);if(itemDescScroll>maxScroll)itemDescScroll=maxScroll;
        for(int line=0;line<visible&&itemDescScroll+line<wrapped.length;line++)Art.textSmall(g,wrapped[itemDescScroll+line],5,textY+line*fh,0xD8E0F0);
        if(maxScroll>0){
            Art.textSmallR(g,Lang.t("1/3 cuộn ","1/3 scroll ")+(itemDescScroll+1)+"/"+(maxScroll+1),W-5,H-fh-2,0x80A8D0);
            int barH=Math.max(4,(H-textY)*visible/wrapped.length),barY=textY+(H-textY-barH)*itemDescScroll/Math.max(1,maxScroll);
            g.setColor(0x607898);g.fillRect(W-3,barY,2,barH);
        }
        Art.textR(g,(itemSel+1)+"/"+ItemData.count(),W-5,py+4,0x8090A8);
    }

    private void paintItemDetail(Graphics g){
        int W=game.W,H=game.H,fh=Art.fh;
        g.setColor(0x111827);g.fillRect(0,0,W,H);
        Art.box(g,3,3,W-6,H-6,0x101830,0xFFD030);
        Art.textBC(g,Lang.t("CHI TIẾT VẬT PHẨM","ITEM DETAILS"),W/2,7,0xFFD030);
        int y=fh+12;
        Art.itemIcon(g,itemSel,8,y);
        Art.textB(g,ItemData.name(itemSel),36,y,0xFFFFFF);
        Art.textSmall(g,ItemData.ID[itemSel],36,y+fh,0x8798B0);
        y+=29;
        g.setColor(0x40506A);g.drawLine(7,y,W-8,y);y+=5;
        Art.textB(g,ItemData.kind(itemSel),8,y,0x80D8FF);y+=fh+4;
        int lines=(H-y-fh*2-8)/fh;if(lines<1)lines=1;
        Art.para(g,ItemData.desc(itemSel),8,y,W-16,0xE0E8F0,lines);
        Art.textSmallR(g,(itemSel+1)+"/"+ItemData.count(),W-8,fh+12,0x8090A8);
        Art.textC(g,Lang.t("FIRE / 0: đóng","FIRE / 0: close"),W/2,H-fh-5,0x8090B0);
    }

    private void paintDetail(Graphics g) {
        int W=game.W,H=game.H,fh=Art.fh,frame=animTime/70;
        g.setColor(0x08111E); g.fillRect(0,0,W,H);
        Art.textB(g,"#"+(sel+1)+" "+Data.name[sel],4,2,0xFFFFFF);
        Art.textR(g,Data.CATEGORY_NAME[Data.category[sel]],W-3,2,0xFFD060);

        int chipY=fh+5,x=4;
        Art.typeIcon(g,Data.t1[sel],x,chipY);x+=16;
        if(Data.t2[sel]>=0)Art.typeIcon(g,Data.t2[sel],x,chipY);

        int vw=Data.visualWidth(sel),vh=Data.visualHeight(sel),size=Math.max(vw,vh),px=W/2-vw/2,py=chipY+fh+3;
        int rawState=previewState==1?RawAtlas.ATTACK:
            (previewState==2?RawAtlas.VICTORY:RawAtlas.IDLE);
        Art.battleSprite(g,sel,px,py,frame,rawState,previewDir);
        Art.statusSprite(g,previewStatus,px-19,py+vh/2-8,frame);
        Art.speciesSkillSprite(g,sel,px+vw+3,py+vh/2-16,frame);
        Art.attackSprite(g,Data.t1[sel],previewAttackKind,W/2,py+vh+12,frame);

        int y=py+vh+22;
        Art.textC(g,CLIP[previewState]+"  "+DIR[previewDir],W/2,y,0x80D8FF); y+=fh;
        Art.textC(g,"STATUS: "+STATUS[previewStatus],W/2,y,0xFFC070); y+=fh;
        String kind=previewAttackKind==0?"MELEE":(previewAttackKind==1?"RANGE":"HIT");
        Art.textC(g,Lang.t("HIỆU ỨNG: ","ATTACK FX: ")+Lang.typeName(Data.t1[sel])+" "+kind,W/2,y,0xA0E8A0); y+=fh;

        Art.textSmall(g,"HP "+Data.hp[sel]+"  ATK "+Data.atk[sel]+"  DEF "+Data.def[sel]+"/"+Data.speDef[sel],4,y,0xE0E8FF); y+=fh;
        Art.textSmall(g,"SPD "+Data.speed[sel]+"  RNG "+Data.range[sel]+"  PP "+Data.mana[sel],4,y,0xE0E8FF); y+=fh;
        Art.text(g,"Stage "+Data.stage[sel]+"  Tier "+Data.tier[sel]+"  Cost "+Data.cost[sel],4,y,0xC0C8D8); y+=fh;
        Art.para(g,Lang.moveName(Data.skillName[sel])+": "+AbilityBehavior.description(sel),4,y,W-8,0xA0FFA0,1);

        Art.textC(g,"< > direction   2/8 clip",W/2,H-fh*2-2,0x8090B0);
        Art.textC(g,"1/3 status  7/9 attack  FIRE back",W/2,H-fh-1,0x8090B0);
    }
}

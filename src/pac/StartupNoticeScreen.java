package pac;

import javax.microedition.lcdui.Graphics;

/** First-launch notice. Text is decoded at runtime so it is not stored plainly in the JAR. */
public final class StartupNoticeScreen extends Screen{
    private static final byte[] NOTICE={
        -56,121,72,116,123,24,-35,45,85,55,111,-43,-99,-55,17,80,-83,-105,1,31,125,-74,20,-84,-12,-10,-107,78,53,-7,-107,-110,11,75,-69,-41,110,77,-120,-1,37,-88,9,4,-19,25,33,102,-93,-102,-39,69,56,28,-59,-21,-49,101,5,23,68,-71,112,107,-69,48,79,-73,55,-105,-77,98,121,-116,-119,30,61,71,-48,18,59,-68,7,63,-7,118,22,-38,16,53,-91,43,-100,-14,69,83,-18,17,23,-58,-113,33,-19,82,37,-121,60,34,-125,-38,-30,11,115,-3,119,-120,-7,-125,-80,-122,-52,71,-122,-14,107,-126,69,109,104,16,-6,-20,88,54,95,78,78,-24,121,123,-106,-85,34,96,64,-15,74,83,7,-42,21,35,-12,-108,78,-118,54,82,127,-40,40,110,-33,77,75,-85,-52,-92,41,-12,-99,90,120,99,104,-8,-111,-57,25,-68,88,-75,-70,-25,36,100,-34,-100,-112,-30,81,-28,-45,-110,-56,58,-2,-40,61,-5,60,-40,-52,-128,-60,-119,-39,69,33,66,40,-1,-99,-74,112,-105,44,-70,126,-64,-100,-1,30,21,72,34,126,74,81,103,99,6,42,-35,-115,99,46,27,-99,-125,-124,123,48,78,-94,45,107,7,72,4,62,-94,36,66,21,-103,-37,27,-17,23,28,21,-34,-49,90,106,56,-6,-122,-103,-73,51,-51,-72,114,32,-119,46,26,-4,31,-100,88,-116,-5,18,-68,-82,17,68,-23,-61,30,44,-60,62,28,80,-22,-69,-23,-24,53,5,29,-18,-104,-96,38,104,-31,40,-48,-99,76,-10,56,-27,-14,27,-29,-10,107,-43,51,-17,-28,5,-82,90,-23,-118,81,-80,-96,-41,-127,-91,41,-43,49,-98,-33,-75,120,-29,3,99,-114,-122,16,91,-19,47,-82,-84,5,-31,-45,61,-2,13,-66,-72,2,-117,74,-71,27,48,-113,-1,-40,28,-123,-79,50,62,-121,-62
    };
    private static final int[] KEY={0x4B,0x64,0x69,0x63,0x50,0x41,0x43,0x31};
    private String[] lines;private int width=-1,scroll;
    public StartupNoticeScreen(Game g){super(g);}
    public void update(int dt){}
    public boolean pointer(int x,int y){if(x>=0&&x<game.W&&y>=0&&y<game.H)game.openMain();return true;}
    static int visibleLines(int height,int fontHeight){return Math.max(1,(height-fontHeight*3-20)/fontHeight);}
    public void key(int k){if(k==Game.K_UP&&scroll>0)scroll--;else if(k==Game.K_DOWN&&lines!=null&&scroll<lines.length-1)scroll++;else if(k==Game.K_FIRE)game.openMain();}
    private static String decode(){byte[] out=new byte[233];int state=0x5A;for(int i=0;i<out.length;i++){state=(state*73+41+i)&255;out[i]=(byte)((NOTICE[i]&255)^KEY[i%KEY.length]^state);}try{return new String(out,"UTF-8");}catch(Exception e){return "Kdic - Poke Auto Chess\n\nPress 5 to continue";}}
    public void paint(Graphics g){int W=game.W,H=game.H,fh=Art.fh;g.setColor(0x0B1221);g.fillRect(0,0,W,H);g.setColor(0x182844);g.fillRect(3,3,W-6,H-6);g.setColor(game.releaseIdentityOk()?0xFFD030:0xFF5048);g.drawRect(3,3,W-7,H-7);Art.textBC(g,Brand.name(),W/2,7,0xFFD030);Art.textSmallC(g,Lang.t("PHÁT TRIỂN BỞI KDIC","DEVELOPED BY KDIC"),W/2,7+fh,0x80D8FF);int textW=W-16;if(lines==null||width!=textW){lines=Art.wrap(decode()+Lang.t("\n\nTrò chơi do Kdic phát triển.","\n\nGame developed by Kdic."),textW,80);width=textW;scroll=0;}int top=fh*2+14,visible=visibleLines(H-fh,fh);if(scroll>Math.max(0,lines.length-visible))scroll=Math.max(0,lines.length-visible);int y=top;for(int i=scroll;i<lines.length&&i<scroll+visible;i++){Art.textSmall(g,lines[i],8,y,0xD8E4F2);y+=fh;}if(!game.releaseIdentityOk())Art.textSmallC(g,Lang.t("CẢNH BÁO: Vendor/JAR đã bị chỉnh sửa","WARNING: Vendor/JAR was modified"),W/2,H-fh*2-4,0xFF6058);Art.textBC(g,Lang.t("CHẠM / 5 ĐỂ TIẾP TỤC","TAP / 5 TO CONTINUE"),W/2,H-fh-3,0xFFFFFF);}
}

package pac;

/** Nine authored tile layouts; generated art is independent from navigation. */
public final class ExploreMapLayout{
 private ExploreMapLayout(){}
 public static String name(int gen){return region(gen)+" - Gen "+gen;}
 public static boolean walkable(byte[] tile,int w,int h,int x,int y){if(x<0||y<0||x>=w||y>=h)return false;int t=tile[y*w+x];return t==WorldScreen.T_GRASS||t==WorldScreen.T_SAND||t==WorldScreen.T_ROCK||t==WorldScreen.T_DARK;}
 public static boolean canStep(byte[] tile,int w,int h,int x,int y,int dx,int dy){int nx=x+dx,ny=y+dy;if(!walkable(tile,w,h,nx,ny))return false;return dx==0||dy==0||(walkable(tile,w,h,x+dx,y)&&walkable(tile,w,h,x,y+dy));}
 public static String region(int gen){String[] names={"Kanto","Johto","Hoenn","Sinnoh","Unova","Kalos","Alola","Galar","Paldea"};return names[Math.max(0,Math.min(8,gen-1))];}
 public static void build(byte[] tile,byte[] zone,int w,int h,int gen){
  for(int y=0;y<h;y++)for(int x=0;x<w;x++){int i=y*w+x;tile[i]=WorldScreen.T_GRASS;zone[i]=(byte)(gen>=4?0:(x<w/2?(y<h/2?0:3):(y<h/2?2:0)));}
  if(gen>=4){
   if(gen==4)sinnoh(tile,zone,w,h);else if(gen==5)unova(tile,zone,w,h);else if(gen==6)kalos(tile,zone,w,h);else if(gen==7)alola(tile,zone,w,h);else if(gen==8)galar(tile,zone,w,h);else paldea(tile,zone,w,h);
   ground(tile,zone);border(tile,w,h);if(gen==7){for(int x=0;x<w;x++){set(tile,w,x,0,WorldScreen.T_WATER);set(tile,w,x,h-1,WorldScreen.T_WATER);}for(int y=0;y<h;y++){set(tile,w,0,y,WorldScreen.T_WATER);set(tile,w,w-1,y,WorldScreen.T_WATER);}}clearLanding(tile,zone,w,h);return;
  }
  if(gen==1)kanto(tile,zone,w,h);else if(gen==2)johto(tile,zone,w,h);else hoenn(tile,zone,w,h);
  // Ground material follows encounter zones; blockers remain separate tile identities.
  for(int i=0;i<tile.length;i++)if(tile[i]==WorldScreen.T_GRASS){if(zone[i]==2)tile[i]=WorldScreen.T_ROCK;else if(zone[i]==3)tile[i]=WorldScreen.T_DARK;}
  // A connected walking loop links the three route styles without crossing lakes.
  for(int x=3;x<w-3;x++)for(int dy=0;dy<2;dy++){path(tile,w,h,x,5+dy);path(tile,w,h,x,h-7+dy);}
  for(int y=5;y<h-5;y++)for(int dx=0;dx<2;dx++){path(tile,w,h,5+dx,y);path(tile,w,h,w-7+dx,y);}
  border(tile,w,h);clearStart(tile,zone,w,h);
 }
 private static void path(byte[] t,int w,int h,int x,int y){if(x>0&&y>0&&x<w-1&&y<h-1&&t[y*w+x]!=WorldScreen.T_WATER)t[y*w+x]=WorldScreen.T_SAND;}
 private static void ground(byte[] t,byte[] z){for(int i=0;i<t.length;i++)if(t[i]==WorldScreen.T_GRASS){if(z[i]==2)t[i]=WorldScreen.T_ROCK;else if(z[i]==3)t[i]=WorldScreen.T_DARK;}}
 private static void area(byte[] z,int w,int h,int x0,int y0,int x1,int y1,int value){for(int y=Math.max(1,y0);y<Math.min(h-1,y1);y++)for(int x=Math.max(1,x0);x<Math.min(w-1,x1);x++)z[y*w+x]=(byte)value;}
 private static void zoneOval(byte[] z,int w,int h,int cx,int cy,int rx,int ry,int value){for(int y=Math.max(1,cy-ry);y<Math.min(h-1,cy+ry+1);y++)for(int x=Math.max(1,cx-rx);x<Math.min(w-1,cx+rx+1);x++){int dx=x-cx,dy=y-cy;if(dx*dx*100/(rx*rx)+dy*dy*100/(ry*ry)<=100)z[y*w+x]=(byte)value;}}
 private static void clearLanding(byte[] t,byte[] z,int w,int h){int cx=w/2,cy=h/2;for(int y=cy-3;y<=cy+3;y++)for(int x=cx-3;x<=cx+3;x++)if(!walkable(t,w,h,x,y)){set(t,w,x,y,WorldScreen.T_GRASS);z[y*w+x]=0;}}
 /** Explicit two-cell walking corridors; water crossings become navigable paving. */
 private static void trail(byte[] t,int w,int h,int x0,int y0,int x1,int y1){int steps=Math.max(Math.abs(x1-x0),Math.abs(y1-y0));for(int i=0;i<=steps;i++){int x=steps==0?x0:x0+(x1-x0)*i/steps,y=steps==0?y0:y0+(y1-y0)*i/steps;for(int yy=y;yy<=y+1;yy++)for(int xx=x;xx<=x+1;xx++)if(xx>0&&yy>0&&xx<w-1&&yy<h-1)set(t,w,xx,yy,WorldScreen.T_SAND);}}
 private static void props(byte[] t,int w,int h,int x0,int y0,int x1,int y1,int spacing,int value){for(int y=y0;y<y1;y+=spacing)for(int x=x0;x<x1;x+=spacing)if(x>0&&y>0&&x<w-1&&y<h-1&&t[y*w+x]!=WorldScreen.T_WATER)set(t,w,x,y,value);}
 private static void sinnoh(byte[] t,byte[] z,int w,int h){
  // Alpine ridge splits the east; two passes and a glacial lake connect its terraces.
  for(int y=1;y<h-1;y++)area(z,w,h,33+(y%12)/4,y,w-1,y+1,2);zoneOval(z,w,h,11,35,15,10,3);
  pond(t,z,w,h,45,9,6,4);pond(t,z,w,h,10,33,4,3);
  for(int y=4;y<h-4;y++)for(int x=36;x<=37;x++)if(y<17||y>19&&y<30||y>32)set(t,w,x,y,WorldScreen.T_BOULDER);
  props(t,w,h,5,5,24,18,4,WorldScreen.T_TREE);props(t,w,h,4,30,25,40,5,WorldScreen.T_DEAD);
  trail(t,w,h,6,23,28,22);trail(t,w,h,28,22,37,18);trail(t,w,h,37,18,49,20);trail(t,w,h,28,22,37,31);trail(t,w,h,37,31,49,34);trail(t,w,h,28,22,24,8);
 }
 private static void unova(byte[] t,byte[] z,int w,int h){
  // Long western canal, three bridges, paved eastern plaza and abandoned southern garden.
  area(z,w,h,31,2,w-1,29,2);zoneOval(z,w,h,36,38,18,11,3);
  for(int y=2;y<h-2;y++)for(int x=16;x<=18;x++){set(t,w,x,y,WorldScreen.T_WATER);z[y*w+x]=1;}area(z,w,h,15,2,20,h-2,1);
  props(t,w,h,4,5,13,39,5,WorldScreen.T_TREE);props(t,w,h,36,6,51,19,5,WorldScreen.T_BOULDER);props(t,w,h,27,32,50,40,5,WorldScreen.T_DEAD);
  trail(t,w,h,6,12,28,12);trail(t,w,h,6,22,48,22);trail(t,w,h,6,33,28,33);trail(t,w,h,27,12,27,33);trail(t,w,h,42,6,42,36);
 }
 private static void kalos(byte[] t,byte[] z,int w,int h){
  // Symmetric garden promenade, lavender south-west and small south-eastern river pools.
  area(z,w,h,35,3,52,19,2);zoneOval(z,w,h,13,36,14,10,3);
  pond(t,z,w,h,44,32,7,4);pond(t,z,w,h,10,9,4,3);
  for(int x=15;x<=40;x+=3){set(t,w,x,9,WorldScreen.T_TREE);set(t,w,x,33,WorldScreen.T_TREE);}for(int y=12;y<=30;y+=3){set(t,w,14,y,WorldScreen.T_TREE);set(t,w,41,y,WorldScreen.T_TREE);}
  props(t,w,h,4,29,24,40,5,WorldScreen.T_DEAD);props(t,w,h,36,5,51,17,6,WorldScreen.T_BOULDER);
  trail(t,w,h,17,12,38,12);trail(t,w,h,38,12,38,30);trail(t,w,h,38,30,17,30);trail(t,w,h,17,30,17,12);trail(t,w,h,5,22,50,22);trail(t,w,h,28,4,28,39);
 }
 private static void alola(byte[] t,byte[] z,int w,int h){
  // Lagoon bays, continuous sandy coast and a volcanic headland; broad tropical interior.
  zoneOval(z,w,h,46,8,15,12,2);zoneOval(z,w,h,14,35,12,11,3);
  pond(t,z,w,h,3,12,7,8);pond(t,z,w,h,49,36,9,6);pond(t,z,w,h,17,7,4,3);
  props(t,w,h,13,12,29,19,4,WorldScreen.T_TREE);props(t,w,h,35,4,51,18,4,WorldScreen.T_BOULDER);props(t,w,h,6,28,22,40,5,WorldScreen.T_DEAD);
  trail(t,w,h,11,22,29,22);trail(t,w,h,29,22,46,22);trail(t,w,h,29,22,33,9);trail(t,w,h,29,22,28,36);trail(t,w,h,11,22,15,35);trail(t,w,h,28,36,38,36);
 }
 private static void galar(byte[] t,byte[] z,int w,int h){
  // Winding eastern stream with two crossings, standing-stone garden and heather moor.
  zoneOval(z,w,h,14,10,12,9,2);zoneOval(z,w,h,27,39,28,13,3);
  for(int y=2;y<h-2;y++){int cx=40+(y<15?-3:y<29?0:3);for(int x=cx;x<=cx+2;x++){set(t,w,x,y,WorldScreen.T_WATER);z[y*w+x]=1;}area(z,w,h,cx-1,y,cx+4,y+1,1);}
  int[] sx={10,14,19,21,19,14,9,7},sy={5,4,6,10,14,16,14,9};for(int i=0;i<sx.length;i++)set(t,w,sx[i],sy[i],WorldScreen.T_BOULDER);
  props(t,w,h,5,30,35,40,5,WorldScreen.T_DEAD);props(t,w,h,28,5,35,18,4,WorldScreen.T_TREE);
  trail(t,w,h,6,22,28,22);trail(t,w,h,28,22,32,15);trail(t,w,h,32,15,50,15);trail(t,w,h,28,22,32,32);trail(t,w,h,32,32,51,32);trail(t,w,h,28,22,15,10);
 }
 private static void paldea(byte[] t,byte[] z,int w,int h){
  // Western crystal lake, olive orchards and two ochre canyon ridges with staggered passes.
  for(int y=1;y<29;y++)area(z,w,h,34-y/9,y,w-1,y+1,2);zoneOval(z,w,h,45,39,17,11,3);
  pond(t,z,w,h,10,15,6,5);pond(t,z,w,h,45,35,5,3);
  props(t,w,h,6,28,24,40,4,WorldScreen.T_TREE);props(t,w,h,34,31,42,40,5,WorldScreen.T_DEAD);
  for(int x=33;x<w-3;x++){if(x<39||x>42)set(t,w,x,9,WorldScreen.T_BOULDER);if(x<46||x>49)set(t,w,x,17,WorldScreen.T_BOULDER);}
  trail(t,w,h,19,23,28,22);trail(t,w,h,28,22,40,10);trail(t,w,h,40,10,48,5);trail(t,w,h,28,22,48,23);trail(t,w,h,48,23,47,14);trail(t,w,h,28,22,28,36);trail(t,w,h,28,36,38,36);trail(t,w,h,19,23,12,26);
 }
 private static void kanto(byte[] t,byte[] z,int w,int h){
  // Tiny Meadow: broad central lawn, western ponds and an eastern rocky terrace.
  pond(t,z,w,h,8,9,5,4);pond(t,z,w,h,47,34,6,4);pond(t,z,w,h,7,35,3,3);
  for(int y=4;y<h-4;y++)for(int x=w*2/3;x<w-3;x++)if(((x*5+y*3)&7)==0){set(t,w,x,y,WorldScreen.T_BOULDER);z[y*w+x]=2;}
  for(int x=3;x<w-3;x++){set(t,w,x,h/2,WorldScreen.T_SAND);set(t,w,x,h/2+1,WorldScreen.T_SAND);}
  for(int y=3;y<h-3;y++){set(t,w,w/2,y,WorldScreen.T_GRASS);set(t,w,w/2+1,y,WorldScreen.T_GRASS);}
  for(int y=h*2/3;y<h-3;y++)for(int x=3;x<w/3;x++)if(((x+y*3)&6)==0)set(t,w,x,y,WorldScreen.T_DEAD);
 }
 private static void johto(byte[] t,byte[] z,int w,int h){
  // Forest Path: dense groves separated by readable horizontal/vertical corridors.
  for(int y=2;y<h-2;y++)for(int x=2;x<w-2;x++)if((x%9<=1||y%10<=1)&&x!=w/2&&x!=w/2+1)set(t,w,x,y,WorldScreen.T_TREE);
  for(int y=2;y<h-2;y++){if(y!=9&&y!=10&&y!=22&&y!=23&&y!=34&&y!=35){set(t,w,10,y,WorldScreen.T_WATER);set(t,w,11,y,WorldScreen.T_WATER);z[y*w+10]=z[y*w+11]=1;}else{set(t,w,10,y,WorldScreen.T_SAND);set(t,w,11,y,WorldScreen.T_SAND);z[y*w+10]=z[y*w+11]=1;}z[y*w+9]=z[y*w+12]=1;}
  for(int x=2;x<w-2;x++){set(t,w,x,22,WorldScreen.T_GRASS);set(t,w,x,23,WorldScreen.T_GRASS);}
  for(int y=2;y<h-2;y++){set(t,w,w/2,y,WorldScreen.T_GRASS);set(t,w,w/2+1,y,WorldScreen.T_GRASS);}
  for(int y=27;y<h-3;y++)for(int x=14;x<25;x++){z[y*w+x]=3;if(((x+y)&5)==0)set(t,w,x,y,WorldScreen.T_DEAD);}
 }
 private static void hoenn(byte[] t,byte[] z,int w,int h){
  // Lush Prairie: bright open route, flower-like brush islands and several lakes.
  pond(t,z,w,h,9,10,5,3);pond(t,z,w,h,44,9,7,3);pond(t,z,w,h,46,34,4,5);
  for(int x=2;x<w-2;x++){int y=h/2+(x-w/2)/8;set(t,w,x,y,WorldScreen.T_SAND);if(y+1<h-1)set(t,w,x,y+1,WorldScreen.T_SAND);}
  for(int y=4;y<h-4;y++)for(int x=4;x<w-4;x++)if(((x*11+y*7)%47)<2&&Math.abs(x-w/2)>5)set(t,w,x,y,WorldScreen.T_TREE);
  for(int y=h/2+5;y<h-3;y++)for(int x=3;x<w/3;x++){z[y*w+x]=2;if(((x*3+y)&9)==0)set(t,w,x,y,WorldScreen.T_BOULDER);}
  for(int y=3;y<h/3;y++)for(int x=w/3;x<w*2/3;x++)z[y*w+x]=3;
 }
 private static void pond(byte[] t,byte[] z,int w,int h,int cx,int cy,int rx,int ry){for(int y=cy-ry-1;y<=cy+ry+1;y++)for(int x=cx-rx-1;x<=cx+rx+1;x++){if(x<=0||y<=0||x>=w-1||y>=h-1)continue;int dx=x-cx,dy=y-cy,v=dx*dx*100/(rx*rx)+dy*dy*100/(ry*ry),i=y*w+x;if(v<=100){t[i]=WorldScreen.T_WATER;z[i]=1;}else if(v<=165){t[i]=WorldScreen.T_SAND;z[i]=1;}}}
 private static void border(byte[] t,int w,int h){for(int x=0;x<w;x++){set(t,w,x,0,WorldScreen.T_TREE);set(t,w,x,h-1,WorldScreen.T_TREE);}for(int y=0;y<h;y++){set(t,w,0,y,WorldScreen.T_TREE);set(t,w,w-1,y,WorldScreen.T_TREE);}}
 private static void clearStart(byte[] t,byte[] z,int w,int h){int cx=w/2,cy=h/2;for(int y=cy-3;y<=cy+3;y++)for(int x=cx-3;x<=cx+3;x++){set(t,w,x,y,WorldScreen.T_GRASS);z[y*w+x]=0;}}
 private static void set(byte[] t,int w,int x,int y,int v){t[y*w+x]=(byte)v;}
}

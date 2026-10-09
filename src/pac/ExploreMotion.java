package pac;

/** Fixed-step free movement in 1/256 tile coordinates; no per-frame allocations. */
public final class ExploreMotion {
 public int x,y,dir,idleAge;public boolean moving;public long travel;private int bank,prevX,prevY;
 public ExploreMotion(int tileX,int tileY){prevX=x=tileX*256+128;prevY=y=tileY*256+128;}
 public void reset(){bank=0;moving=false;idleAge=0;prevX=x;prevY=y;}
 public int renderX(){return SurvivalAnimation.interpolate(prevX,x,bank);}
 public int renderY(){return SurvivalAnimation.interpolate(prevY,y,bank);}
 public int animationClock(int size){return moving?(int)(travel*size/(6*256)*2):SurvivalAnimation.clock(RawAtlas.IDLE,idleAge,0);}
 public void update(int dt,int dx,int dy,byte[] tiles,int w,int h,int[] wx,int[] wy,int count){if(dt<=0)return;bank+=Math.min(250,dt);if(bank<20)return;moving=false;while(bank>=20){bank-=20;int step=20,ox=x,oy=y;prevX=x;prevY=y;if(dx!=0&&dy!=0)step=step*181/256;int nx=x+dx*step,ny=y+dy*step;if(clear(nx,y,tiles,w,h,wx,wy,count))x=nx;if(clear(x,ny,tiles,w,h,wx,wy,count))y=ny;int ax=Math.abs(x-ox),ay=Math.abs(y-oy);if(ax!=0||ay!=0){moving=true;idleAge=0;travel+=Math.max(ax,ay)+Math.min(ax,ay)*3/8;dir=dy>0?(dx>0?1:dx<0?7:0):dy<0?(dx>0?3:dx<0?5:4):dx>0?2:6;}else idleAge=Math.min(1000000000,idleAge+20);}}
 private boolean clear(int px,int py,byte[] tiles,int w,int h,int[] wx,int[] wy,int count){int radius=72;for(int iy=-1;iy<=1;iy+=2)for(int ix=-1;ix<=1;ix+=2){int tx=(px+ix*radius)/256,ty=(py+iy*radius)/256;if(px+ix*radius<0||py+iy*radius<0||!ExploreMapLayout.walkable(tiles,w,h,tx,ty))return false;}for(int i=0;i<count;i++){int ax=Math.abs(px-(wx[i]*256+128)),ay=Math.abs(py-(wy[i]*256+128));if(ax<144&&ay<144)return false;}return true;}
}

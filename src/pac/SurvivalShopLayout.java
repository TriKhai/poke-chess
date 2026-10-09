package pac;
/** Shared painting and touch geometry, including 320x240 landscape. */
public final class SurvivalShopLayout {
 private SurvivalShopLayout(){}
 public static final int TOP=44,FOOT=24;
 public static int statsTop(){return 110;}
 public static int statsTop(int h){return Math.min(124,Math.max(110,h-FOOT-14));}
 public static int statsRow(int h){return Math.min(24,Math.max(14,(h-FOOT-statsTop(h))/5));}
 public static int statsVisible(int h){return Math.max(1,(h-FOOT-statsTop(h))/statsRow(h));}
 public static int statsFirst(int index,int h){return UiLayout.firstVisible(index,5,statsVisible(h));}
 public static int hitStat(int x,int y,int w,int h,int selected){if(x<4||x>=w-4||y<statsTop(h)||y>=h-FOOT)return-1;int offset=(y-statsTop(h))/statsRow(h),index=statsFirst(selected,h)+offset;return offset<statsVisible(h)&&index<5&&(y-statsTop(h))%statsRow(h)<statsRow(h)-2?index:-1;}
 public static int marqueeOffset(int textWidth,int width,int elapsed){int overflow=Math.max(0,textWidth-width);if(overflow==0)return 0;int phase=elapsed%(overflow*35+2400);return Math.min(overflow,Math.max(0,(phase-1200)/35));}
 public static int listWidth(int w,int h){return w>h?w*48/100:w;}
 public static int listEnd(int w,int h){return w>h?h-24:h-92;}
 public static int listRows(int w,int h){return Math.max(1,(listEnd(w,h)-TOP)/36);}
 public static int cell(int w,int h){return Math.min(40,Math.max(18,Math.min((w-36)/6-3,(h-TOP-FOOT-80)/3)));}
 public static int slotY(){return TOP;}
 public static int partsY(int w,int h){return h-FOOT-cell(w,h)*2-20;}
 public static int gearsY(int w,int h){return h-FOOT-cell(w,h)-6;}
 public static int detailY(int w,int h){return slotY()+cell(w,h)+4;}
 public static int detailH(int w,int h){return Math.max(16,partsY(w,h)-detailY(w,h)-14);}
 public static int gridX(int w,int h,int col){int size=cell(w,h);return (w-(size*6+15))/2+col*(size+3);}
 public static int bagVisible(int w,int h){return Math.max(1,(w-28+4)/(cell(w,h)+4));}
 public static int bagX(int w,int h,int col){int size=cell(w,h),n=bagVisible(w,h);return (w-(size*n+(n-1)*4))/2+col*(size+4);}
 public static int ownedCount(int[] quantities){int n=0;for(int i=0;i<quantities.length;i++)if(quantities[i]>0)n++;return n;}
 public static int ownedAt(int[] quantities,int position){if(position<0)return-1;for(int i=0;i<quantities.length;i++)if(quantities[i]>0){if(position==0)return i;position--;}return-1;}
 public static int hitGrid(int x,int y,int w,int h,int row){int yy=row==0?slotY():row==1?partsY(w,h):gearsY(w,h),size=cell(w,h);if(y<yy||y>=yy+size)return-1;for(int c=0;c<6;c++)if(x>=gridX(w,h,c)&&x<gridX(w,h,c)+size)return c;return-1;}
 public static int recipe(int a,int b){if(a<0||b<0||a>=SurvivalShop.PARTS||b>=SurvivalShop.PARTS)return-1;for(int i=0;i<SurvivalShop.GEARS;i++)if(SurvivalShop.A[i]==a&&SurvivalShop.B[i]==b||SurvivalShop.A[i]==b&&SurvivalShop.B[i]==a)return i;int item=ItemData.crafted(SurvivalShop.ICON[a],SurvivalShop.ICON[b]);for(int i=0;i<SurvivalShop.GEARS;i++)if(SurvivalShop.gearIcon(i)==item)return i;return-1;}
}

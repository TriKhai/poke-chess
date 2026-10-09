package pac;

/** Pure responsive layout helpers; safe to exercise in desktop regression tests. */
public final class UiLayout {
    public static final int COMPACT=0, STANDARD=1, LARGE=2;
    private UiLayout(){}

    public static int profile(int w,int h){
        if(w<176||h<208)return COMPACT;
        if(w>=240||h>=300)return LARGE;
        return STANDARD;
    }
    public static boolean compact(int w,int h){return profile(w,h)==COMPACT;}
    public static boolean large(int w,int h){return profile(w,h)==LARGE;}
    /** Wide mode gets a dedicated composition; ordinary near-square phones keep portrait layout. */
    public static boolean landscape(int w,int h){return w>=240&&w>h+32;}
    public static int margin(int w,int h){return compact(w,h)?3:(large(w,h)?8:5);}
    public static int contentWidth(int w,int h){int m=margin(w,h);return Math.max(1,w-m*2);}
    public static int clamp(int value,int lo,int hi){if(hi<lo)return lo;return value<lo?lo:(value>hi?hi:value);}
    public static int visibleRows(int height,int top,int bottom,int rowHeight){
        return Math.max(1,(height-Math.max(0,top)-Math.max(0,bottom))/Math.max(1,rowHeight));
    }
    public static int firstVisible(int selected,int total,int visible){
        if(total<=visible)return 0;
        int first=selected-visible+1;
        return clamp(first,0,total-visible);
    }
    public static int popupWidth(int width,int preferred){
        int m=width<176?4:8;return Math.max(1,Math.min(preferred,width-m*2));
    }
    public static int popupHeight(int height,int preferred){
        int m=height<208?4:8;return Math.max(1,Math.min(preferred,height-m*2));
    }
}

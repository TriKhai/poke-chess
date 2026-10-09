package pac;

/** Isolated arena cadence: no change to autochess or existing atlas renderers. */
public final class SurvivalAnimation {
 private SurvivalAnimation(){}
 public static int interpolate(int previous,int current,int remainder){return previous+(current-previous)*Math.max(0,Math.min(20,remainder))/20;}
 public static int clock(int action,int age,int travel){if(action==RawAtlas.IDLE)return Math.max(0,age)/240*2;if(action==RawAtlas.WALK)return travel/(6*256)*2;return Math.max(0,age)/65*2;}
}

package pac;
/** Called on fresh direction presses only, never on key-repeat events. */
public final class SurvivalTap {
 private int last;private long at;
 public void reset(){last=0;at=0;}
 public boolean press(int direction,long now){if(direction==0)return false;boolean twice=last==direction&&now>=at&&now-at<=260;last=twice?0:direction;at=now;return twice;}
}

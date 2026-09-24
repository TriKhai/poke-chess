package pac;

/** Fixed simulation clock independent from render cadence. No per-frame allocation. */
public final class FixedStepClock {
    private final int stepMs;
    private int accumulator;
    public FixedStepClock(int step){stepMs=Math.max(1,step);}
    public void reset(){accumulator=0;}
    public void add(int realMs,int speed){
        if(realMs<0)realMs=0;if(realMs>250)realMs=250;
        accumulator+=realMs*Math.max(1,speed);
    }
    public boolean ready(){return accumulator>=stepMs;}
    public void consume(){if(accumulator>=stepMs)accumulator-=stepMs;}
    public int remainder(){return accumulator;}
    public int fraction256(){int f=accumulator*256/stepMs;return f>256?256:f;}
    /** Integer smoothstep: same endpoints, softer motion between deterministic ticks. */
    public static int smooth256(int f){
        if(f<=0)return 0;if(f>=256)return 256;
        return f*f*(768-2*f)/65536;
    }
}

package pac;

/** Tiny xorshift RNG (CLDC's java.util.Random lacks nextInt(int)). */
public final class Rng {
    private int s;

    public Rng(int seed) {
        s = (seed == 0) ? 0x2545F491 : seed;
        for (int i = 0; i < 4; i++) next();
    }

    public int next() {
        s ^= s << 13;
        s ^= s >>> 17;
        s ^= s << 5;
        return s;
    }

    /** 0 .. n-1 (n must be > 0). */
    public int nextInt(int n) {
        return (next() >>> 1) % n;
    }

    public boolean pct(int p) {
        return nextInt(100) < p;
    }
}

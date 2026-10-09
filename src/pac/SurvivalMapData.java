package pac;

/** Generated from data/survival-map.json; art and walk geometry are independent. */
public final class SurvivalMapData {
    private SurvivalMapData(){}
    public static final int TILE=32,COLS=20,ROWS=15;
    public static final String[] GROUND={
        "23010201045010102323",
        "30100101054010010232",
        "21010201045010100123",
        "01010101454010101010",
        "01010104450101010101",
        "10101444550101441010",
        "44444545676745454444",
        "55545454767654545555",
        "01010104676701010101",
        "10101010454010101010",
        "01010101045401010101",
        "10101010104540101010",
        "23010101014540100123",
        "32101010105410101032",
        "23230101014501013232"
    };
    public static int tile(int x,int y){return GROUND[y].charAt(x)-48;}
    public static boolean walkable(int x,int y){return x>=12&&y>=12&&x<=628&&y<=468;}
}

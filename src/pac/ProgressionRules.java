package pac;

/** Mode goals and one-time offline progression rewards. */
public final class ProgressionRules{
    private ProgressionRules(){}
    public static int clearBit(int mode){return mode==Run.MODE_NORMAL?1:(mode==Run.MODE_THIRTY?2:(mode==Run.MODE_GEN1?4:0));}
    public static boolean cleared(int mode){int b=clearBit(mode);return b!=0&&(Save.modeCleared&b)!=0;}
    public static int finishReward(int mode,int round,boolean victory){
        int reward=2+(round-1)/2;
        if(victory){reward+=mode==Run.MODE_THIRTY?25:(mode==Run.MODE_GEN1?20:12);int b=clearBit(mode);if(b!=0&&(Save.modeCleared&b)==0)reward+=mode==Run.MODE_THIRTY?20:10;}
        return reward;
    }
    public static void recordClear(int mode){int b=clearBit(mode);if(b!=0)Save.modeCleared|=b;}
    public static String objective(int mode){
        if(mode==Run.MODE_THIRTY)return Lang.t("Sống sót 30 vòng; nhận 9 mảnh mỗi vòng và hạ boss mỗi 5 vòng.","Survive 30 rounds, receive 9 components each round and defeat a boss every 5 rounds.");
        if(mode==Run.MODE_GEN1)return Lang.t("Dùng duy nhất Gen 1, vượt 30 vòng để mở toàn bộ family Gen 1.","Use only Gen 1 and clear 30 rounds to unlock every Gen 1 family.");
        if(mode==Run.MODE_UNLIMITED)return Lang.t("Chế độ thử nghiệm: vàng và thao tác kinh tế không giới hạn.","Testing mode with unlimited gold and economy actions.");
        return Lang.t("Vượt 20 vòng với kinh tế tiêu chuẩn; chiến thắng nhận thưởng Bóng.","Clear 20 rounds with standard economy; victory grants a Ball reward.");
    }
}

package pac;

/** Mode goals and one-time offline progression rewards. */
public final class ProgressionRules{
    private ProgressionRules(){}
    public static int clearBit(int mode){if(mode==Run.MODE_NORMAL)return 1;if(mode==Run.MODE_THIRTY)return 2;if(mode==Run.MODE_LEGEND)return 8;int gen=generationForMode(mode);return gen>0?1<<(gen+1):0;}
    public static boolean cleared(int mode){int b=clearBit(mode);return b!=0&&(Save.modeCleared&b)!=0;}
    public static int finishReward(int mode,int round,boolean victory){
        int reward=2+(round-1)/2;
        if(victory){reward+=mode==Run.MODE_THIRTY?25:(isGeneration(mode)?20:12);int b=clearBit(mode);if(b!=0&&(Save.modeCleared&b)==0)reward+=mode==Run.MODE_THIRTY?20:10;}
        return reward;
    }
    public static void recordClear(int mode){int b=clearBit(mode);if(b!=0)Save.modeCleared|=b;}
    public static boolean isGeneration(int mode){return mode>=Run.MODE_GEN1&&mode!=Run.MODE_LEGEND&&mode<=Run.MODE_GEN9;}
    public static int generationForMode(int mode){if(mode==Run.MODE_GEN1)return 1;if(mode==Run.MODE_GEN2)return 2;if(mode==Run.MODE_GEN3)return 3;if(mode==Run.MODE_GEN4)return 4;if(mode==Run.MODE_GEN5)return 5;if(mode==Run.MODE_GEN6)return 6;if(mode==Run.MODE_GEN7)return 7;if(mode==Run.MODE_GEN8)return 8;if(mode==Run.MODE_GEN9)return 9;return 0;}
    public static int modeForGeneration(int gen){int[] mode={0,Run.MODE_GEN1,Run.MODE_GEN2,Run.MODE_GEN3,Run.MODE_GEN4,Run.MODE_GEN5,Run.MODE_GEN6,Run.MODE_GEN7,Run.MODE_GEN8,Run.MODE_GEN9};return gen>=1&&gen<=9?mode[gen]:Run.MODE_GEN1;}
    public static boolean generationUnlocked(int gen){return gen==1||(gen>=2&&gen<=Data.BATTLE_MAX_GEN&&cleared(modeForGeneration(gen-1)));}
    public static boolean endlessUnlocked(){return cleared(Run.MODE_GEN9);}
    public static String objective(int mode){
        if(ExtraChessRules.mode(mode))return ExtraChessRules.objective(mode);
        if(mode==Run.MODE_THIRTY)return Lang.t("Sống sót 30 vòng; nhận 9 mảnh mỗi vòng và hạ boss mỗi 5 vòng.","Survive 30 rounds, receive 9 components each round and defeat a boss every 5 rounds.");
        if(isGeneration(mode)){int gen=generationForMode(mode),limit=gen<3?3:gen;return Lang.t("Chọn pool hệ và starter. Địch chỉ dùng Gen "+gen+"; phe ta dùng Pokémon Gen 1-"+limit+". Vượt 30 vòng để mở thế hệ kế tiếp.","Choose a type pool and starter. Enemies only use Gen "+gen+"; your roster includes Gen 1-"+limit+". Clear 30 rounds to unlock the next generation.");}
        if(mode==Run.MODE_ENDLESS)return Lang.t("Sinh Tồn Vô Tận: dùng roster Gen 1-"+Data.BATTLE_MAX_GEN+", địch đổi thế hệ mỗi 5 vòng. Bắt đầu 100 máu; thua trừ máu, hết máu mới kết thúc. Mở sau khi thắng Thế hệ 9.","Endless Survival: use the Gen 1-"+Data.BATTLE_MAX_GEN+" roster; enemies rotate generation every 5 rounds. Start with 100 HP; losses cost HP and the run ends at zero HP. Unlocks after clearing Generation 9.");
        if(mode==Run.MODE_LEGEND)return Lang.t("Thần Thú Đại Chiến: starter, shop, quà Pokémon và toàn bộ đối thủ đều là Legendary.","Legendary War: starter, shop, Pokémon rewards and every enemy are Legendary.");
        if(mode==Run.MODE_UNLIMITED)return Lang.t("Chế độ thử nghiệm: vàng và thao tác kinh tế không giới hạn.","Testing mode with unlimited gold and economy actions.");
        return Lang.t("Vượt 40 ải với kinh tế tiêu chuẩn; thắng mới nhận mảnh và quà mốc, thua nhận vàng rồi đấu lại.","Clear 40 stages with standard economy; only wins grant components and milestone rewards, while losses grant gold and retry the same stage.");
    }
}

package pac;

/** Lightweight bilingual text selector. Pokemon and move names remain canonical. */
public final class Lang {
    private Lang() {}
    private static String[] moveMap;
    private static String[] moveMapMore;
    private static String[] synergyLongVi;
    private static String[] synergyLongEn;

    public static final int VI = 0;
    public static final int EN = 1;

    public static String t(String vi, String en) {
        return Save.language == EN ? en : vi;
    }

    public static void toggle() {
        Save.language = Save.language == VI ? EN : VI;
        Save.save();
    }

    public static String[] menu() {
        return Save.language == EN
            ? new String[] { "Explore & Catch", "Auto Chess Run", "Collection", "My Profile", "Settings", "Help", "About", "Exit" }
            : new String[] { "Khám phá và bắt cóc Pokémon", "Chơi AutoChess", "Bộ sưu tập", "Hồ sơ cá nhân", "Cài đặt", "Hướng dẫn", "Về game", "Thoát" };
    }

    private static final String[] TYPE_VI = {"Thường","Lửa","Nước","Cỏ","Điện","Đá","Tâm linh","Chiến đấu","Bay","Rồng","Ma","Bọ","Độc","Đất","Băng","Bóng tối","Thép","Tiên","Vô định hình","Thủy sinh","Nhân tạo","Trẻ nhỏ","Đồng cỏ","Thực vật","Hóa thạch","Ẩm thực","Con người","Ánh sáng","Quái thú","Âm thanh","Hoang dã"};
    private static final String[] TYPE_SHORT_VI = {"THƯ","LỬ","NƯỚ","CỎ","ĐIỆ","ĐÁ","TL","ĐẤU","BAY","RỒ","MA","BỌ","ĐỘC","ĐẤT","BĂN","TỐI","THÉ","TIÊ","VĐH","THỦ","NT","NHỎ","ĐỒN","TV","HT","ẨM","NGƯ","SÁG","QT","ÂM","HD"};
    private static final String[] ABILITY_VI = {"Cú Đánh Uy Lực","Xung Hồi Phục","Bùng Nổ","Hiệu Triệu","Tê Liệt","Sét Dây Chuyền","Cứng Hóa","Hút Sinh Lực","Tia Siêu Cấp"};
    private static final String[] ABILITY_DESC_VI = {"Đánh mục tiêu bằng 250% ATK.","Hồi máu cho đồng minh yếu nhất.","Nổ quanh mục tiêu, gây 180% ATK.","Đồng minh gần đó nhận +30% ATK.","Làm choáng 1,2 giây và gây 120% ATK.","Giật sét 3 kẻ địch, gây 140% ATK.","Nhận lá chắn bằng 35% HP tối đa.","Gây 200% ATK và tự hồi máu.","Bắn tia lớn gây 380% ATK."};
    private static final String[] STATUS_EN={"Armor Break","Blinded","Burn","Charm","Confusion","Curse","Fatigue","Flinch","Freeze","Locked","Paralysis","Poison","Protect","Silence","Sleep","Wound","Possessed","Resurrection","Safeguard","Rage","Pokerus","Electric Field","Fairy Field","Grass Field","Psychic Field"};
    private static final String[] STATUS_VI={"Phá giáp","Mù","Bỏng","Mê hoặc","Hoang mang","Nguyền rủa","Mệt mỏi","Nao núng","Đóng băng","Khóa","Tê liệt","Nhiễm độc","Bảo vệ","Câm lặng","Ngủ","Vết thương","Nhập hồn","Hồi sinh","Hộ vệ","Cuồng nộ","Pokerus","Điện trường","Tiên trường","Thảo trường","Tâm linh trường"};

    public static String typeName(int type) {
        return Save.language == EN ? Data.TNAME[type] : TYPE_VI[type];
    }

    public static String typeShort(int type) {
        return Save.language == EN ? Data.TSHORT[type] : TYPE_SHORT_VI[type];
    }

    public static String abilityName(int ability) {
        return Save.language == EN ? Data.ABIL_NAME[ability] : ABILITY_VI[ability];
    }

    public static String abilityDesc(int ability) {
        return Save.language == EN ? Data.ABIL_DESC[ability] : ABILITY_DESC_VI[ability];
    }

    public static String statusName(int status) {
        if(status<0||status>=STATUS_EN.length)return t("Bình thường","Normal");
        return Save.language==EN?STATUS_EN[status]:STATUS_VI[status];
    }

    /** Compact descriptions for the source-backed offline synergy rules. */
    public static String synergyDesc(int type) {
        switch(type) {
            case Data.T_NORMAL:return t("Khiên 15/35/60/90","Shield 15/35/60/90");
            case Data.T_FIRE:return t("Đánh tăng ATK 0/1/2/3","Hits grant ATK 0/1/2/3");
            case Data.T_WATER:return t("MP đầu +15/30/45","Start MP +15/30/45");
            case Data.T_GRASS:return t("Hồi HP 2/4/6/8%","Regen 2/4/6/8% HP");
            case Data.T_ELEC:return t("Tam kích mỗi 4/3/3 đòn","Triple hit every 4/3/3 attacks");
            case Data.T_ROCK:return t("DEF +10/25/50","DEF +10/25/50");
            case Data.T_PSY:return t("Skill +50/100/150%","Skill +50/100/150%");
            case Data.T_FIGHT:return t("Phản kích mỗi 10 lần đỡ","Counter every 10 blocks");
            case Data.T_FLY:return t("Thoát hiểm 1/1/2/2 lần","Escape 1/1/2/2 times");
            case Data.T_DRAGON:return t("Khiên; cấp 3 tăng Skill/Tốc","Shield; tier 3 grants AP/Speed");
            case Data.T_GHOST:return t("Né 15% và nguyền đối thủ","15% dodge and enemy curses");
            case Data.T_BUG:return t("HP +5/10/15/20%","HP +5/10/15/20%");
            case Data.T_POISON:return t("Độc 30/60/100% khi đánh","30/60/100% poison on hit");
            case Data.T_GROUND:return t("Tăng DEF/SpeDef và ATK","Gain DEF/SpeDef and ATK");
            case Data.T_ICE:return t("SpeDef +4/12/25/50","SpeDef +4/12/25/50");
            case Data.T_DARK:return t("Tăng chí mạng và sát thương","Crit chance and crit power");
            case Data.T_STEEL:return t("DEF +3","DEF +3");
            case Data.T_FAIRY:return t("Bảo hộ đầu trận","Opening protection");
            case Data.T_AMORPHOUS:return t("Tốc/HP theo số hệ kích hoạt","Speed/HP per active synergy");
            case Data.T_AQUATIC:return t("Tăng tốc và hồi phục","Speed and regeneration");
            case Data.T_ARTIFICIAL:return t("Trang bị tăng ATK/Skill/Khiên","Items grant ATK/AP/Shield");
            case Data.T_BABY:return t("Né +5/10/15%","Dodge +5/10/15%");
            case Data.T_FIELD:return t("Chết hồi máu/tốc đồng minh","Death heals/speeds Field allies");
            case Data.T_FLORA:return t("Hồi phục và Skill","Regeneration and AP");
            case Data.T_FOSSIL:return t("ATK và DEF cổ đại","Ancient ATK and DEF");
            case Data.T_GOURMET:return t("Tăng HP và hồi phục","HP and regeneration");
            case Data.T_HUMAN:return t("Hút máu 25/35/50%","Lifesteal 25/35/50%");
            case Data.T_LIGHT:return t("ATK/Skill; cấp cao hộ thể","ATK/AP; high tiers protect");
            case Data.T_MONSTER:return t("Hạ địch tăng ATK/Skill/HP","Kills grant ATK/AP/HP");
            case Data.T_SOUND:return t("Dùng chiêu buff cả đội","Casting buffs the team");
            case Data.T_WILD:return t("Tốc, ATK và gây Vết thương","Speed, ATK and Wound");
            default:return "";
        }
    }

    /** Full explanation shared by the preparation bench and Collection. */
    public static String synergyLongDesc(int type) {
        if(type<0||type>=Data.NT)return "";
        if(synergyLongVi==null)synergyLongVi=new String[]{
          "Mốc 3/5/7/9. Pokemon Normal nhận 15/35/60/90 Khiên khi bắt đầu trận, giúp chịu đòn mở màn.",
          "Mốc 2/4/6/8. Mỗi đòn đánh thường của Pokemon Fire tăng vĩnh viễn 0/1/2/3 ATK trong trận hiện tại.",
          "Mốc 3/6/9. Pokemon Water bắt đầu trận với thêm 15/30/45 MP, nhưng không vượt quá MP tối đa.",
          "Mốc 3/5/7/9. Pokemon Grass hồi 2/4/6/8% HP tối đa mỗi giây khi còn sống.",
          "Mốc 3/5/7. Pokemon Electric tung thêm 2 đòn sau mỗi đòn thứ 4/3/3; đòn phụ không tích thêm MP.",
          "Mốc 2/4/6. Pokemon Rock nhận thêm 10/25/50 DEF trong suốt trận.",
          "Mốc 3/5/7. Pokemon Psychic nhận 50/100/150% sức mạnh kỹ năng, tăng sát thương, hồi máu và Khiên từ chiêu.",
          "Mốc 2/4/6/8. Pokemon Fighting tích số lần đỡ đòn; mỗi 10 lần sẽ kích hoạt phản kích cận chiến.",
          "Mốc 2/4/6/8. Pokemon Flying có 1/1/2/2 lần thoát hiểm khi HP thấp để tránh bị dồn sát thương.",
          "Mốc 3/5/7. Tổng bậc tiến hóa Dragon quyết định Khiên; mốc cuối còn cộng cùng lượng Tốc và sức mạnh kỹ năng.",
          "Mốc 2/4/6/8. Ghost nhận 15% Né và lần lượt nguyền địch mạnh: giảm thủ, ATK, Skill rồi gây Vết thương.",
          "Mốc 2/4/6/8. Luật offline tăng 5/10/15/20% HP cho Bug, thay cho cơ chế sinh bầy vào hàng chờ online.",
          "Mốc 3/5/7. Đòn đánh thường của Pokemon Poison có 30/60/100% gây Độc trong 4 giây.",
          "Mốc 2/4/6/8. Ground tăng DEF và SpeDef theo cấp; cấp cao còn nhận thêm ATK như hiệu ứng đào sâu.",
          "Mốc 2/4/6/8. Pokemon Ice nhận thêm 4/12/25/50 SpeDef để chống sát thương kỹ năng.",
          "Mốc 3/5/7. Dark nhận 30/40/50% chí mạng và tăng 40/60/100% sức mạnh đòn chí mạng.",
          "Mốc 2/4/6/8. Pokemon Steel nhận thêm 3 DEF; tương tác trang bị Steel sẽ mở rộng ở bản item passive.",
          "Mốc 2/4/6/8. Fairy được Bảo hộ ngắn lúc mở trận; mốc cuối nhận thêm 5% chí mạng.",
          "Mốc 3/5/7. Mỗi hệ đang kích hoạt cho Amorphous thêm Tốc 1/3/5 và HP 3/6/10.",
          "Mốc 2/4/6/8. Luật offline cho Aquatic thêm 10/20/30/40 Tốc và hồi 1/2/3/4% HP mỗi giây.",
          "Mốc 2/4/6. Từ mốc 2, mỗi trang bị của Artificial tăng ATK, Skill và Khiên; mốc 3 cho gấp đôi.",
          "Mốc 3/5/7. Luật offline cho Baby thêm 5/10/15% Né, thay cho phần thưởng trứng giữa các vòng online.",
          "Mốc 3/6/9. Khi một Pokemon Field bị hạ, đồng minh Field hồi 30/40/50 HP và tăng 15/20/25 Tốc.",
          "Mốc 3/4/5/6. Luật offline cho Flora hồi 1/2/3/4% HP mỗi giây và thêm 10/20/30/40 sức mạnh kỹ năng.",
          "Mốc 2/4/6. Luật offline cho Fossil thêm 3/6/9 DEF và 2/4/6 ATK, mô phỏng sức mạnh cổ đại.",
          "Mốc 3/4/5. Luật offline cho Gourmet thêm 8/16/24% HP tối đa và hồi 1/2/3% HP mỗi giây.",
          "Mốc 2/4/6. Human hồi máu bằng 25/35/50% sát thương thật sự gây ra, không tính sát thương tự gây.",
          "Mốc 2/3/4/5. Light tăng 20% ATK và 20 Skill; cấp cao tăng 50% hai loại thủ rồi thêm 100 Khiên.",
          "Mốc 2/4/6/8. Mỗi lần Monster hạ địch tăng ATK, Skill và HP tối đa dựa trên HP của mục tiêu.",
          "Mốc 2/4/6. Mỗi lần Sound tung chiêu, cả đội nhận ATK; cấp cao còn thêm Tốc và 3 MP.",
          "Mốc 2/4/6/9. Wild tăng Tốc; từ mốc 3 thêm 40% ATK. Đòn thường có 25% gây Vết thương."
        };
        if(synergyLongEn==null)synergyLongEn=new String[]{
          "At 3/5/7/9, Normal Pokemon start combat with 15/35/60/90 Shield for opening durability.",
          "At 2/4/6/8, every Fire basic attack permanently grants 0/1/2/3 ATK for the current battle.",
          "At 3/6/9, Water Pokemon begin with 15/30/45 extra MP, capped at their maximum MP.",
          "At 3/5/7/9, living Grass Pokemon regenerate 2/4/6/8% maximum HP each second.",
          "At 3/5/7, Electric Pokemon add two attacks after every 4th/3rd/3rd hit; extra hits grant no MP.",
          "At 2/4/6, Rock Pokemon gain 10/25/50 DEF for the battle.",
          "At 3/5/7, Psychic Pokemon gain 50/100/150% ability power for spell damage, healing and shields.",
          "At 2/4/6/8, Fighting Pokemon count blocked hits and trigger a melee counter every 10 blocks.",
          "At 2/4/6/8, Flying Pokemon gain 1/1/2/2 low-HP escape charges to avoid focused damage.",
          "At 3/5/7, total Dragon evolution stars determine Shield; the final tier also grants Speed and ability power.",
          "At 2/4/6/8, Ghosts gain 15% Dodge and progressively curse enemy defenses, ATK, ability power and healing.",
          "At 2/4/6/8, the offline rule grants Bug Pokemon 5/10/15/20% max HP instead of online bench swarms.",
          "At 3/5/7, Poison basic attacks have a 30/60/100% chance to Poison for 4 seconds.",
          "At 2/4/6/8, Ground Pokemon gain DEF and SpeDef; higher tiers also gain ATK from digging.",
          "At 2/4/6/8, Ice Pokemon gain 4/12/25/50 SpeDef against ability damage.",
          "At 3/5/7, Dark Pokemon gain 30/40/50% critical chance and 40/60/100% extra critical power.",
          "At 2/4/6/8, Steel Pokemon gain 3 DEF; Steel item interactions expand with item passives.",
          "At 2/4/6/8, Fairy Pokemon receive brief opening protection; the final tier adds 5% critical chance.",
          "At 3/5/7, each active synergy grants Amorphous Pokemon 1/3/5 Speed and 3/6/10 HP.",
          "At 2/4/6/8, the offline Aquatic rule grants 10/20/30/40 Speed and 1/2/3/4% HP regeneration.",
          "At 2/4/6, tier 2 makes each held item grant Artificial ATK, ability power and Shield; tier 3 doubles it.",
          "At 3/5/7, the offline Baby rule grants 5/10/15% Dodge instead of online between-round egg rewards.",
          "At 3/6/9, a fallen Field Pokemon heals Field allies for 30/40/50 HP and grants 15/20/25 Speed.",
          "At 3/4/5/6, the offline Flora rule grants 1/2/3/4% HP regeneration and 10/20/30/40 ability power.",
          "At 2/4/6, the offline Fossil rule grants 3/6/9 DEF and 2/4/6 ATK as accumulated ancient power.",
          "At 3/4/5, the offline Gourmet rule grants 8/16/24% max HP and 1/2/3% HP regeneration.",
          "At 2/4/6, Human Pokemon heal for 25/35/50% of damage actually dealt, excluding self-damage.",
          "At 2/3/4/5, Light grants 20% ATK and 20 ability power; high tiers add 50% defenses then 100 Shield.",
          "At 2/4/6/8, every Monster kill grants ATK, ability power and max HP based on the victim.",
          "At 2/4/6, every Sound ability cast buffs team ATK; higher tiers also grant Speed and 3 MP.",
          "At 2/4/6/9, Wild gains Speed; tier 3 adds 40% ATK. Basic attacks have 25% chance to Wound."
        };
        return Save.language==EN?synergyLongEn[type]:synergyLongVi[type];
    }

    /** Vietnamese battle-facing move names; English data remains untouched for combat rules. */
    public static String moveName(String en) {
        if (Save.language == EN) return en;
        if (moveMap == null) moveMap = new String[] {
            "Magical Leaf=Lá Ma Thuật","Blast Burn=Thiêu Đốt Bùng Nổ","Hydro Pump=Vòi Rồng Nước","String Shot=Bắn Tơ","Bug Buzz=Tiếng Rền Côn Trùng","Hurricane=Bão Tố","Agility=Né Nhanh","Peck=Mổ","Coil=Cuộn Tròn","Nuzzle=Cọ Điện","Rollout=Lăn Tròn","Venoshock=Sốc Độc","Horn Attack=Húc Sừng","Metronome=Nhịp Điệu","Fire Spin=Lốc Lửa","Sing=Ca Hát","Leech Life=Hút Sinh Lực","Stun Spore=Bào Tử Tê Liệt","Absorb=Hấp Thụ","Dig=Đào Hang","Payday=Ngày Lương","Psyshock=Sốc Tâm Linh","Thrash=Cuồng Kích","Fire Fang=Nanh Lửa","Soak=Ngâm Nước","Crabhammer=Búa Cua","Teleport=Dịch Chuyển","Guillotine=Đoạn Đầu","Ingrain=Bén Rễ","Toxic=Kịch Độc","Rock Slide=Sạt Đá","Flame Charge=Xung Kích Lửa","Yawn=Ngáp","Thunder Shock=Sốc Điện","Thunder Fang=Nanh Sét","Thunder=Sấm Sét","Vine Whip=Roi Mây","Razor Leaf=Lá Dao","Solar Beam=Tia Mặt Trời","Poison Powder=Bột Độc","Sleep Powder=Bột Ngủ","Confusion=Hoang Mang","Psychic=Tâm Linh","Quick Attack=Tấn Công Nhanh","Hyper Beam=Tia Siêu Cấp","Body Slam=Ép Thân","Bite=Cắn","Crunch=Nghiền Nát","Flamethrower=Súng Phun Lửa","Waterfall=Thác Nước","Blizzard=Bão Tuyết","Ice Beam=Tia Băng","Shadow Ball=Cầu Bóng Tối","Dragon Claw=Vuốt Rồng","Dragon Tail=Đuôi Rồng","Aerial Ace=Át Chủ Bài Trên Không","Air Slash=Chém Gió","Aqua Jet=Phản Lực Nước","Aqua Ring=Vòng Nước","Aurora Beam=Tia Cực Quang","Close Combat=Cận Chiến","Draco Meteor=Thiên Thạch Rồng","Dream Eater=Ăn Giấc Mơ","Dynamic Punch=Cú Đấm Bùng Nổ","Electro Ball=Cầu Điện","Explosion=Tự Nổ","Future Sight=Tiên Tri","Heal Block=Khóa Hồi Phục","Helping Hand=Trợ Giúp","Hidden Power A=Sức Mạnh Ẩn A","Iron Defense=Phòng Thủ Sắt","Iron Head=Đầu Sắt","Iron Tail=Đuôi Sắt","Leaf Blade=Kiếm Lá","Leech Seed=Hạt Hút Máu","Mach Punch=Đấm Siêu Tốc","Mega Punch=Siêu Cú Đấm","Meteor Mash=Đấm Thiên Thạch","Night Slash=Chém Đêm","Petal Dance=Vũ Điệu Cánh Hoa","Poison Jab=Đâm Độc","Protect=Bảo Vệ","Recover=Hồi Phục","Shadow Clone=Phân Thân Bóng Tối","Shell Smash=Phá Vỏ","Sky Attack=Không Kích","Soft Boiled=Trứng Hồi Phục","Splash=Vẫy Nước","Stealth Rocks=Đá Tàng Hình","Sweet Scent=Hương Thơm","Tri Attack=Tam Kích","Triple Kick=Tam Cước","Volt Switch=Đổi Điện","Whirlpool=Xoáy Nước","Whirlwind=Lốc Xoáy","Wish=Điều Ước","Wood Hammer=Búa Gỗ","X Scissor=Kéo Chữ X"
        };
        String[] map = moveMap;
        for (int i = 0; i < map.length; i++) {
            int p = map[i].indexOf('=');
            if (en.equals(map[i].substring(0, p))) return map[i].substring(p + 1);
        }
        if (moveMapMore == null) moveMapMore = new String[] {
            "Acrobatics=Nhào Lộn","Attract=Quyến Rũ","Beat Up=Đánh Hội Đồng","Bide=Nhẫn Nhịn","Blaze Kick=Cước Lửa","Bonemerang=Phi Xương","Bounce=Bật Nhảy","Bullet Punch=Đấm Đạn","Camouflage=Ngụy Trang","Cavernous Chomp=Cú Ngoạm Hang Sâu","Charm=Mê Hoặc","Cosmic Power Moon=Sức Mạnh Mặt Trăng","Cosmic Power Sun=Sức Mạnh Mặt Trời","Counter=Phản Đòn","Disarming Voice=Tiếng Hát Mê Hoặc","Dive=Lặn","Dizzy Punch=Đấm Choáng","Doom Desire=Ước Nguyện Diệt Vong","Drill Peck=Mổ Khoan","Echo=Vang Âm","Egg Bomb=Bom Trứng","Entangling Thread=Tơ Trói Buộc","Eruption=Phun Trào","Facade=Đòn Nghịch Cảnh","Fire Blast=Đại Hỏa Cầu","Fissure=Nứt Đất","Forecast=Dự Báo","Foul Play=Chơi Xấu","Fury Swipes=Vuốt Liên Hoàn","Growl=Gầm Gừ","Growth=Tăng Trưởng","Hail=Mưa Đá","Happy Hour=Giờ Vui Vẻ","Harden=Cứng Hóa","Heavy Slam=Ép Nặng","Horn Drill=Khoan Sừng","Hyper Drill=Siêu Khoan","Hyper Voice=Siêu Thanh","Hypnosis=Thôi Miên","Ice Ball=Cầu Băng","Icicle Crash=Mưa Cọc Băng","Icy Wind=Gió Băng","Knock Off=Đánh Rơi","Lava Plume=Cột Dung Nham","Lick=Liếm","Link Cable=Cáp Liên Kết","Lovely Kiss=Nụ Hôn Ngọt Ngào","Luster Purge=Thanh Tẩy Rực Sáng","Magic Bounce=Phản Phép","Magnet Bomb=Bom Nam Châm","Magnet Rise=Từ Trường Bay","Mawashi Geri=Đá Vòng Cầu","Meditate=Thiền Định","Mimic=Bắt Chước","Mist Ball=Cầu Sương Mù","Mud Bubble=Bong Bóng Bùn","Mud Shot=Bắn Bùn","Night Shade=Bóng Đêm","Nightmare=Ác Mộng","Octazooka=Pháo Bạch Tuộc","Origin Pulse=Xung Khởi Nguyên","Overheat=Quá Nhiệt","Petal Blizzard=Bão Cánh Hoa","Play Rough=Chơi Thô Bạo","Precipice Blades=Lưỡi Kiếm Vực Sâu","Present=Quà Tặng","Psybeam=Tia Tâm Linh","Psycho Boost=Bùng Nổ Tâm Linh","Psyshield Bash=Khiên Tâm Linh","Psystrike=Đột Kích Tâm Linh","Raging Bull=Bò Tót Cuồng Nộ","Rapid Spin=Xoay Nhanh","Razor Wind=Gió Dao","Roar=Gầm","Rock Tomb=Mộ Đá","Shockwave=Sóng Điện","Silver Wind=Gió Bạc","Sketch=Phác Họa","Slack Off=Nghỉ Ngơi","Slash=Chém","Slashing Claw=Vuốt Chém","Sludge=Bùn Độc","Smog=Khói Độc","Smoke Screen=Màn Khói","Spiky Shield=Khiên Gai","Stockpile=Tích Trữ","Struggle Bug=Bọ Vùng Vẫy","Swallow=Nuốt","Tail Glow=Đuôi Phát Sáng","Teeter Dance=Vũ Điệu Lảo Đảo","Tickle=Cù","Time Travel=Du Hành Thời Gian","Torment=Hành Hạ","Transform=Biến Hình","Twin Beam=Tia Song Sinh","Twister=Lốc Xoáy","Uproar=Gào Thét","Vise Grip=Kẹp Chặt","Wheel Of Fire=Bánh Xe Lửa","Wise Yawn=Cơn Ngáp Thông Thái","Wonder Guard=Hộ Vệ Kỳ Diệu"
        };
        String[] more = moveMapMore;
        for (int i = 0; i < more.length; i++) {
            int p = more[i].indexOf('=');
            if (en.equals(more[i].substring(0, p))) return more[i].substring(p + 1);
        }
        return en;
    }

    public static String help() {
        if (Save.language == EN) return
            "MODE 1 - EXPLORE: Walk with the d-pad (2/4/6/8). Wild Pokemon roam the map. Stand next to one and press FIRE (5) to throw a ball, then press FIRE when the marker is inside the green zone. Rarer Pokemon have a smaller zone. Catching a Pokemon unlocks its whole evolution family for Auto Chess. Balls regenerate slowly; Auto Chess pays balls too.\n"
          + "MODE 2 - AUTO CHESS: Buy Pokemon from the shop, move them to the board (cursor + FIRE), then press GO. Units fight automatically. 3 identical Pokemon merge and evolve. Units of the same type grant synergy bonuses. Board size equals your level. Interest: +1 gold per 10 saved (max 5). Survive 20 rounds.\n"
          + "KEYS: 1 Reroll, 3 Buy XP, 7 Sell, 9 Fight, * Synergies, # Item bag on selected Pokemon, 0/right softkey Back. In battle the 3x3 panel is always visible: 1/3 changes metric, 7 changes side, arrows select, FIRE opens details, * changes speed and 9 skips.";
        return
            "CHẾ ĐỘ 1 - KHÁM PHÁ VÀ BẮT CÓC POKÉMON: Di chuyển bằng phím hướng (2/4/6/8). Pokémon hoang dã đi lại trên bản đồ. Đứng cạnh chúng và nhấn FIRE (5) để ném bóng, sau đó nhấn FIRE khi kim nằm trong vùng xanh. Pokémon càng hiếm thì vùng xanh càng nhỏ. Bắt được Pokémon sẽ mở cả chuỗi tiến hóa trong Auto Chess. Bóng hồi chậm theo thời gian và cũng là phần thưởng Auto Chess.\n"
          + "CHẾ ĐỘ 2 - AUTO CHESS: Mua Pokemon trong cửa hàng, đưa lên bàn bằng con trỏ + FIRE rồi nhấn GO. Các đơn vị tự chiến đấu. 3 Pokemon giống nhau sẽ hợp nhất và tiến hóa. Pokemon cùng hệ tạo cộng hưởng. Số ô được triển khai bằng cấp người chơi. Mỗi 10 vàng giữ lại nhận thêm 1 vàng lãi, tối đa 5. Hãy sống sót qua 20 vòng.\n"
          + "PHÍM: 1 Đổi shop, 3 Mua XP, 7 Bán, 9 Chiến đấu, * Cộng hưởng, # mở túi đồ trên Pokemon đang chọn, 0/phím phải Quay lại. Trong trận, bảng 3x3 luôn hiện: 1/3 đổi chỉ số, 7 đổi phe, phím hướng chọn pet, FIRE xem chi tiết, * đổi tốc độ và 9 bỏ qua.";
    }
}

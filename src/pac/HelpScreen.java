package pac;

import javax.microedition.lcdui.Graphics;

public final class HelpScreen extends Screen {
    private String[] lines;
    private int scroll = 0;
    private int wrappedWidth=-1;
    private int page=0;

    public HelpScreen(Game g) {
        super(g);
    }

    public void update(int dt) { }

    public void key(int k) {
        switch (k) {
            case Game.K_UP: if (scroll > 0) scroll--; break;
            case Game.K_DOWN: scroll++; break;
            case Game.K_LEFT: page=(page+3)%4;lines=null;scroll=0;break;
            case Game.K_RIGHT: page=(page+1)%4;lines=null;scroll=0;break;
            case Game.K_SOFT2: case Game.K_0: case Game.K_FIRE: case Game.K_SOFT1:
                game.setScreen(new MenuScreen(game));
                break;
            default: break;
        }
    }

    public void paint(Graphics g) {
        int W = game.W, H = game.H, fh = Art.fh;
        g.setColor(0x102040);
        g.fillRect(0, 0, W, H);
        if (lines == null||wrappedWidth!=W) {
            wrappedWidth=W;
            String text = pageText();
            // wrap paragraph by paragraph
            java.util.Vector v = new java.util.Vector();
            int start = 0;
            while (start <= text.length()) {
                int nl = text.indexOf('\n', start);
                if (nl < 0) nl = text.length();
                String[] w = Art.wrap(text.substring(start, nl), W - 8, 60);
                for (int i = 0; i < w.length; i++) v.addElement(w[i]);
                v.addElement("");
                start = nl + 1;
            }
            lines = new String[v.size()];
            v.copyInto(lines);
        }
        String[] title={Lang.t("ĐIỀU KHIỂN","CONTROLS"),Lang.t("CHỈ SỐ","STATS"),"STATUS",Lang.t("CHẾ ĐỘ","MODES")};
        Art.textBC(g, "< "+title[page]+" >", W / 2, 2, 0xFFD030);
        int visible = UiLayout.visibleRows(H,fh+4,fh+2,fh);
        if (scroll > lines.length - visible) scroll = Math.max(0, lines.length - visible);
        int y = fh + 4;
        for (int i = scroll; i < lines.length && i < scroll + visible; i++) {
            Art.text(g, lines[i], 4, y, 0xE0E8FF);
            y += fh;
        }
        Art.textSmallC(g, Lang.t("4/6 đổi trang  2/8 cuộn  FIRE về","4/6 page  2/8 scroll  FIRE back"), W / 2, H - fh - 1, 0x8090B0);
    }

    private String pageText(){
        if(page==0)return Lang.help();
        if(page==1)return Lang.t("HP: sinh lực; về 0 thì Pokémon bị hạ.\nMP/PP: năng lượng; đầy thì dùng kỹ năng.\nATK: sát thương vật lý cơ bản.\nDEF: giảm sát thương vật lý.\nSP.DEF: giảm sát thương kỹ năng.\nSPEED: giảm thời gian giữa các đòn đánh.\nRANGE: khoảng cách có thể tấn công.\nLEVEL/XP: số Pokémon tối đa trên sân và tỉ lệ shop.\nVÀNG/LÃI: mỗi 10 vàng nhận +1, tối đa +5.","HP: health; at zero the Pokemon is knocked out.\nMP/PP: energy; casts an ability when full.\nATK: basic physical damage.\nDEF: reduces physical damage.\nSP.DEF: reduces ability damage.\nSPEED: shortens time between attacks.\nRANGE: attack distance.\nLEVEL/XP: board limit and shop odds.\nGOLD/INTEREST: +1 per 10 saved, up to +5.");
        if(page==2)return Lang.t("STATUS XẤU: Bỏng/Độc/Nguyền gây mất HP; Mù làm hụt đòn; Phá giáp giảm DEF; Câm lặng khóa skill; Ngủ/Đóng băng/Nao núng ngăn hành động; Tê liệt/Mệt mỏi giảm tốc; Vết thương giảm hồi máu.\nSTATUS TỐT: Bảo vệ chặn sát thương; Hộ vệ xóa và chặn debuff; Cuồng nộ tăng tốc; Hồi sinh cho sống lại một lần.\nFIELD: Điện tăng tốc, Cỏ hồi HP, Tiên bảo hộ, Tâm linh tăng sức mạnh kỹ năng. Xem đủ 25 status và animation trong Bộ sưu tập > STATUS.","DEBUFFS: Burn/Poison/Curse drain HP; Blind causes misses; Armor Break lowers defenses; Silence blocks skills; Sleep/Freeze/Flinch block actions; Paralysis/Fatigue slow; Wound reduces healing.\nBUFFS: Protect blocks damage; Safeguard cleanses and prevents debuffs; Rage raises Speed; Resurrection revives once.\nFIELDS: Electric raises Speed, Grass heals, Fairy protects, Psychic boosts abilities. View all 25 statuses and animations in Collection > STATUS.");
        return ProgressionRules.objective(Run.MODE_NORMAL)+"\n\n"+ProgressionRules.objective(Run.MODE_THIRTY)+"\n\n"+ProgressionRules.objective(Run.MODE_GEN1)+"\n\n"+Lang.t("KHÁM PHÁ mở khóa family. FARM kiếm Bóng bằng chiến đấu. GACHA lọc theo Gen/Hệ/Độ hiếm; quay trùng được hoàn Bóng. BÃI tích Bóng theo giờ, tối đa 24 giờ.","EXPLORE unlocks families. FARM earns Balls through battles. GACHA filters by Gen/Type/Rarity; duplicates refund the Ball. CAMP gathers hourly Balls for up to 24 hours.");
    }
}

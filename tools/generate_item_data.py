#!/usr/bin/env python3
"""Generate the compact J2ME item catalogue from the original app.zip sources."""
import re
import sys
import zipfile
from pathlib import Path

APP = Path(sys.argv[1])
OUT = Path(sys.argv[2])

with zipfile.ZipFile(APP) as z:
    enum_src = z.read("app/types/enum/Item.ts").decode("utf-8")
    stats_src = z.read("app/config/game/items.ts").decode("utf-8")

ids = re.findall(r'^  ([A-Z0-9_]+) = "\1",?\r?$', enum_src, re.M)

stats = {}
stat_maps = {}
for item, body in re.findall(r'\[Item\.([A-Z0-9_]+)\]:\s*\{([^}]*)\}', stats_src, re.S):
    vals = []
    sm = {}
    for stat, value in re.findall(r'\[Stat\.([A-Z_]+)\]:\s*(-?\d+)', body):
        sm[stat] = int(value)
        label = {"SPE_DEF":"SP.DEF", "CRIT_CHANCE":"CRIT", "CRIT_POWER":"CRIT DMG",
                 "SPEED":"SPD", "SHIELD":"SHIELD", "RANGE":"RANGE", "LUCK":"LUCK",
                 "PP":"MP"}.get(stat, stat)
        vals.append(label + (" " if value.startswith("-") else " +") + value)
    stats[item] = ", ".join(vals)
    stat_maps[item] = sm

recipes = {}
recipe_block = re.search(r'export const ItemRecipe.*?= \{(.*?)\n\}', enum_src, re.S)
if recipe_block:
    for item, a, b in re.findall(r'\[Item\.([A-Z0-9_]+)\]:\s*\[Item\.([A-Z0-9_]+),\s*Item\.([A-Z0-9_]+)\]', recipe_block.group(1)):
        recipes[item] = (a, b)

exact_vi = {
    "MIRACLE_SEED":"Hạt Kỳ Diệu", "MYSTIC_WATER":"Nước Huyền Bí",
    "HEART_SCALE":"Vảy Tim", "NEVER_MELT_ICE":"Băng Vĩnh Cửu",
    "CHARCOAL":"Than Củi", "MAGNET":"Nam Châm", "BLACK_GLASSES":"Kính Đen",
    "TWISTED_SPOON":"Thìa Xoắn", "FOSSIL_STONE":"Đá Hóa Thạch",
    "SILK_SCARF":"Khăn Lụa", "RARE_CANDY":"Kẹo Hiếm",
    "EXP_SHARE":"Chia Sẻ Kinh Nghiệm", "LEFTOVERS":"Thức Ăn Thừa",
    "KINGS_ROCK":"Đá Vương Giả", "METAL_COAT":"Áo Kim Loại",
    "REAPER_CLOTH":"Vải Tử Thần", "CHOICE_SPECS":"Kính Lựa Chọn",
    "SHELL_BELL":"Chuông Vỏ Sò", "LIGHT_BALL":"Cầu Ánh Sáng",
    "POKE_DOLL":"Búp Bê Pokémon", "LUCKY_EGG":"Trứng May Mắn",
    "AMULET_COIN":"Đồng Xu Hộ Mệnh", "ROCKY_HELMET":"Mũ Đá",
    "ASSAULT_VEST":"Áo Khoác Xung Kích", "MUSCLE_BAND":"Đai Cơ Bắp"
}

words = {
    "FIRE":"Lửa", "WATER":"Nước", "GRASS":"Cỏ", "ELECTRIC":"Điện",
    "ICE":"Băng", "DARK":"Bóng Tối", "LIGHT":"Ánh Sáng", "PSYCHIC":"Tâm Linh",
    "FIGHTING":"Chiến Đấu", "POISON":"Độc", "GROUND":"Đất", "ROCK":"Đá",
    "STEEL":"Thép", "FLYING":"Bay", "GHOST":"Ma", "DRAGON":"Rồng",
    "FAIRY":"Tiên", "BUG":"Bọ", "NORMAL":"Thường", "FOSSIL":"Hóa Thạch",
    "STONE":"Đá", "GEM":"Ngọc", "MEMORY":"Bộ Nhớ", "BERRY":"Quả",
    "GIFT":"Quà", "BOX":"Hộp", "TICKET":"Vé", "BAND":"Đai",
    "RIBBON":"Ruy Băng", "WAND":"Đũa", "MASK":"Mặt Nạ", "ORB":"Cầu",
    "SWEET":"Kẹo", "FLAVOR":"Vị", "RED":"Đỏ", "BLUE":"Xanh Lam",
    "GREEN":"Xanh Lá", "GOLD":"Vàng", "GOLDEN":"Vàng", "BLACK":"Đen",
    "WHITE":"Trắng", "PINK":"Hồng", "PURPLE":"Tím", "YELLOW":"Vàng",
    "OLD":"Cũ", "BIG":"Lớn", "SMALL":"Nhỏ", "RARE":"Hiếm",
    "LEGENDARY":"Huyền Thoại", "COMMON":"Thường", "UNCOMMON":"Không Phổ Biến",
    "EPIC":"Sử Thi", "UNIQUE":"Độc Nhất", "ULTRA":"Siêu Cấp"
}

def en_name(item):
    return " ".join(w.capitalize() if w not in ("TM", "HP", "XP") else w for w in item.split("_"))

def vi_name(item):
    if item in exact_vi:
        return exact_vi[item]
    return " ".join(words.get(w, w.capitalize()) for w in item.split("_"))

def category(item, vi):
    if item.startswith("TM_"): return "Máy dạy kỹ năng." if vi else "Technical machine that teaches a move."
    if item.endswith("_MEMORY") or item == "MEMORY_DISCS": return "Đĩa bộ nhớ dùng cho cơ chế đổi hệ." if vi else "Memory disc used by type-changing mechanics."
    if item.endswith("_GEM"): return "Ngọc cộng hưởng, đại diện cho một hệ Pokémon." if vi else "Synergy gem representing a Pokémon type."
    if item.endswith("_BERRY"): return "Quả mọng có hiệu ứng hỗ trợ hoặc hồi phục." if vi else "Berry with a support or recovery effect."
    if item.endswith("_WAND"): return "Đũa phép tạo hiệu ứng đặc biệt trong thám hiểm." if vi else "Wand with a special exploration effect."
    if item.endswith("_STONE") or item.endswith("_ROCK"): return "Đá dùng cho tiến hóa, thời tiết hoặc chế tạo." if vi else "Stone used for evolution, weather or crafting."
    if item.endswith("_GIFT") or item.endswith("_BUNDLE") or item.endswith("_BOX"): return "Gói phần thưởng; mở để nhận vật phẩm." if vi else "Reward container that grants items when opened."
    if item.endswith("_TICKET") or "MISSION_ORDER" in item: return "Vật phẩm nhiệm vụ hoặc vé sử dụng tại thị trấn." if vi else "Quest item or ticket used in town."
    if item.endswith("_SCARF") or item.endswith("_BAND") or item.endswith("_BELT") or item.endswith("_RIBBON") or item.endswith("_BANDANNA"):
        return "Trang bị đeo cho Pokémon trong trận." if vi else "Held equipment for a Pokémon in battle."
    if item.endswith("_SWEET") or item.endswith("_FLAVOR") or item in ("SANDWICH","CURRY","TEA","BERRY_JUICE","FRUIT_JUICE","MOOMOO_MILK","HONEY","POFFIN"):
        return "Nguyên liệu hoặc món ăn dùng trong chế độ thám hiểm." if vi else "Food or ingredient used during exploration."
    if item.endswith("_NECTAR"): return "Mật hoa dùng để đổi dạng Pokémon tương ứng." if vi else "Nectar used by the matching form-change mechanic."
    return ""

def desc(item, vi):
    parts = []
    if stats.get(item): parts.append(("Chỉ số: " if vi else "Stats: ") + stats[item] + ".")
    if item in recipes:
        a, b = recipes[item]
        parts.append(("Ghép: " if vi else "Recipe: ") + (vi_name(a) if vi else en_name(a)) + " + " + (vi_name(b) if vi else en_name(b)) + ".")
    group = category(item, vi)
    if group: parts.append(group)
    if not parts:
        parts.append("Gói app.zip không kèm bản mô tả ngôn ngữ cho vật phẩm này." if vi else "This app.zip does not include the localized description for this item.")
    return " ".join(parts)

def j(s):
    return '"' + s.replace('\\', '\\\\').replace('"', '\\"') + '"'

def array(name, vals):
    lines = ["    public static final String[] " + name + "={"]
    for i in range(0, len(vals), 5):
        lines.append("        " + ",".join(j(v) for v in vals[i:i+5]) + ("," if i+5 < len(vals) else ""))
    lines.append("    };")
    return "\n".join(lines)

src = """package pac;

/** Full item catalogue generated from app.zip Item enum, stats and recipes. */
public final class ItemData {
    private ItemData() {}
%s
%s
%s
%s
%s
%s
%s
%s
%s
%s
%s
%s
%s
%s
%s
%s
    public static int count(){return ID.length;}
    public static String name(int i){return Save.language==Lang.EN?EN[i]:VI[i];}
    public static String desc(int i){return Save.language==Lang.EN?DESC_EN[i]:DESC_VI[i];}
    public static int indexOf(String id){for(int i=0;i<ID.length;i++)if(ID[i].equals(id))return i;return -1;}
    public static boolean isComponent(int i){String s=ID[i];return s.equals("MIRACLE_SEED")||s.equals("MYSTIC_WATER")||s.equals("HEART_SCALE")||s.equals("NEVER_MELT_ICE")||s.equals("CHARCOAL")||s.equals("MAGNET")||s.equals("BLACK_GLASSES")||s.equals("TWISTED_SPOON")||s.equals("FOSSIL_STONE")||s.equals("SILK_SCARF");}
    public static int crafted(int a,int b){for(int i=0;i<ID.length;i++)if((RECIPE_A[i]==a&&RECIPE_B[i]==b)||(RECIPE_A[i]==b&&RECIPE_B[i]==a))return i;return -1;}
    public static String kind(int i){
        String s=ID[i];
        if(s.indexOf("_BERRY")>=0)return Lang.t("QUẢ MỌNG","BERRY");
        if(s.indexOf("_MEMORY")>=0||s.equals("MEMORY_DISCS"))return Lang.t("ĐĨA BỘ NHỚ","MEMORY");
        if(s.indexOf("_GEM")>=0)return Lang.t("NGỌC HỆ","SYNERGY GEM");
        if(s.indexOf("TM_")==0)return Lang.t("MÁY KỸ NĂNG","TECHNICAL MACHINE");
        if(s.indexOf("_WAND")>=0)return Lang.t("ĐŨA PHÉP","WAND");
        if(s.indexOf("_STONE")>=0||s.indexOf("_ROCK")>=0)return Lang.t("ĐÁ / TIẾN HÓA","STONE / EVOLUTION");
        if(s.indexOf("_GIFT")>=0||s.indexOf("_BOX")>=0||s.indexOf("_BUNDLE")>=0)return Lang.t("GÓI PHẦN THƯỞNG","REWARD");
        if(s.indexOf("_TICKET")>=0||s.indexOf("MISSION_ORDER")>=0)return Lang.t("NHIỆM VỤ / VÉ","QUEST / TICKET");
        return Lang.t("VẬT PHẨM","ITEM");
    }
}
""" % (array("ID", ids), array("EN", [en_name(x) for x in ids]),
       array("VI", [vi_name(x) for x in ids]), array("DESC_EN", [desc(x, False) for x in ids]),
       array("DESC_VI", [desc(x, True) for x in ids]),
       "    public static final int[] HP={"+",".join(str(stat_maps.get(x,{}).get("HP",0)) for x in ids)+"};",
       "    public static final int[] ATK={"+",".join(str(stat_maps.get(x,{}).get("ATK",0)) for x in ids)+"};",
       "    public static final int[] DEF={"+",".join(str(stat_maps.get(x,{}).get("DEF",0)) for x in ids)+"};",
       "    public static final int[] SPE_DEF={"+",".join(str(stat_maps.get(x,{}).get("SPE_DEF",0)) for x in ids)+"};",
       "    public static final int[] SPEED={"+",".join(str(stat_maps.get(x,{}).get("SPEED",0)) for x in ids)+"};",
       "    public static final int[] MP={"+",".join(str(stat_maps.get(x,{}).get("PP",0)) for x in ids)+"};",
       "    public static final int[] AP={"+",".join(str(stat_maps.get(x,{}).get("AP",0)) for x in ids)+"};",
       "    public static final int[] CRIT={"+",".join(str(stat_maps.get(x,{}).get("CRIT_CHANCE",0)) for x in ids)+"};",
       "    public static final int[] SHIELD={"+",".join(str(stat_maps.get(x,{}).get("SHIELD",0)) for x in ids)+"};",
       "    public static final int[] RECIPE_A={"+",".join(str(ids.index(recipes[x][0])) if x in recipes else "-1" for x in ids)+"};",
       "    public static final int[] RECIPE_B={"+",".join(str(ids.index(recipes[x][1])) if x in recipes else "-1" for x in ids)+"};")
OUT.write_text(src, encoding="utf-8")
print("generated", len(ids), "items ->", OUT)

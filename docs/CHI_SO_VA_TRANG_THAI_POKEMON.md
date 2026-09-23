# Chỉ số và trạng thái Pokémon

Tài liệu này mô tả các chữ viết tắt, thống kê trận đấu và trạng thái đang được sử dụng trong Poke Auto Chess ME v1.2.8.

## 1. Chỉ số cơ bản

| Viết tắt | Tên đầy đủ | Giải thích |
|---|---|---|
| `HP` | Hit Points | Máu hiện tại/tối đa. HP về 0 thì Pokémon bị hạ. |
| `MP` / `PP` | Mana / Power Points | Năng lượng dùng kỹ năng. Đủ thanh thì Pokémon tung chiêu. |
| `ATK` | Attack | Sức tấn công, dùng cho đánh thường và nhiều kỹ năng. |
| `DEF` | Defense | Phòng thủ vật lý. |
| `SP.DEF` | Special Defense | Phòng thủ trước sát thương kỹ năng. |
| `SPD` | Speed | Tốc độ di chuyển và tốc độ tấn công. |
| `RNG` | Range | Tầm đánh tính theo số ô. |
| `CD` | Cooldown | Thời gian chờ giữa hai lần tấn công. |
| `CRIT` | Critical Chance | Tỷ lệ gây đòn chí mạng. |
| `DODGE` | Dodge | Tỷ lệ né đòn đánh thường. |
| `SHIELD` | Shield | Khiên hấp thụ sát thương trước HP. |
| `REGEN` | Regeneration | Tỷ lệ hồi HP định kỳ. |
| `LIFESTEAL` | Lifesteal | Hồi HP theo phần trăm sát thương gây ra. |
| `SKILL` / `AP` | Skill Power | Phần trăm tăng sức mạnh kỹ năng. |

### Công thức liên quan

- Sát thương vật lý được giảm bởi `DEF`.
- Sát thương kỹ năng được giảm bởi `SP.DEF`.
- Công thức giảm sát thương: `sát thương thực = sát thương gốc × 20 / (20 + phòng thủ)`.
- Chí mạng gây `200%` sát thương.
- Đánh thường nhận `5 MP`.
- Bị đánh nhận `4 MP`.
- Một tick mô phỏng bằng `100 ms`.

## 2. Thống kê Battle Stats

| Nút | Giải thích |
|---|---|
| `DMG` | Tổng sát thương Pokémon đã gây trong trận. |
| `Khiên` | Tổng lượng khiên Pokémon đã tạo. |
| `Chặn` | Tổng sát thương đã được khiên hấp thụ. |
| `Hồi` | Tổng HP đã hồi thực tế; không tính phần hồi vượt HP tối đa. |
| `HP` | Máu hiện tại. Pokémon đã bị hạ hiển thị 0. |
| `MP` | Năng lượng hiện tại so với mức cần để dùng kỹ năng. |

Các nhãn bổ sung:

| Nhãn | Giải thích |
|---|---|
| `Nhận` | Sát thương thực tế đã nhận sau khi trừ khiên. |
| `Khiên tạo` | Tổng lượng khiên đã tạo cho bản thân hoặc đồng minh. |
| `TA` | Đội người chơi. |
| `ĐỊCH` | Đội đối thủ. |
| `KO` | Pokémon đã bị hạ. |

## 3. Trạng thái đang hoạt động

| Trạng thái | Tiếng Anh | Hiệu ứng hiện tại |
|---|---|---|
| Choáng | Stun | Không thể di chuyển, đánh thường hoặc tung kỹ năng. Trạng thái này được lưu riêng, không nằm trong bộ 16 icon. |
| Phá giáp | Armor Break | Giảm `DEF` và `SP.DEF` còn một nửa. |
| Mù | Blinded | Đánh thường có 50% khả năng trượt. |
| Bỏng | Burn | Mỗi giây mất khoảng 4% HP tối đa. |
| Mê hoặc | Charm | Đổi mục tiêu sang Pokémon đồng minh gần nhất trong thời gian hiệu lực. |
| Hoang mang | Confusion | Mỗi lượt hành động có 25% khả năng tự nhận 5% HP tối đa và mất lượt. |
| Nguyền rủa | Curse | Mỗi giây mất khoảng 6,25% HP tối đa. |
| Mệt mỏi | Fatigue | Tốc độ hiệu dụng còn khoảng hai phần ba. |
| Nao núng | Flinch | Không thể hành động trong thời gian hiệu lực. |
| Khóa | Locked | Không thể di chuyển nhưng vẫn có thể đánh hoặc tung chiêu nếu mục tiêu trong tầm. |
| Đóng băng | Freeze | Không thể hành động. |
| Tê liệt | Paralysis | Tốc độ hiệu dụng giảm còn một nửa. |
| Nhiễm độc | Poison | Mỗi giây mất khoảng 5% HP tối đa. |
| Bảo vệ | Protect | Chặn hoàn toàn sát thương trong thời gian hiệu lực. |
| Câm lặng | Silence | Không thể tung kỹ năng nhưng vẫn đánh thường được. |
| Ngủ | Sleep | Không thể hành động. |
| Vết thương | Wound | Lượng hồi máu nhận được giảm 50%. |

## 4. Behavior kỹ năng

- `Data.abil` tiếp tục quyết định mẫu sát thương chính của kỹ năng.
- `AbilityBehavior` đọc tên move gốc để áp dụng hành vi phụ như buff, hồi phục,
  Protect, dịch chuyển, cleanse, khiên đội, vàng Payday và trạng thái diện rộng.
- `SkillEffects` ánh xạ move sang một hoặc nhiều trạng thái; ví dụ Lick có thể gây
  đồng thời Hoang mang và Tê liệt.

## 5. Tóm tắt mức độ hoàn thiện

- Danh sách 16 trạng thái đều có state, thời lượng, icon và hành vi gameplay.
- Một Pokémon có thể mang và hiển thị đồng thời nhiều trạng thái.
- `Stun` hoạt động đầy đủ nhưng được quản lý riêng ngoài danh sách 16 icon.

## 6. Nguồn mã liên quan

- `src/pac/Unit.java`: chỉ số hiện tại của từng Pokémon.
- `src/pac/CombatStatus.java`: thời lượng và xử lý trạng thái.
- `src/pac/SkillEffects.java`: ánh xạ chiêu thức sang hiệu ứng phụ.
- `src/pac/AbilityBehavior.java`: hành vi đặc thù theo tên move.
- `src/pac/Battle.java`: công thức sát thương, hồi máu, khiên và hành động.
- `src/pac/CombatRules.java`: hằng số MP, chí mạng, tốc độ và giảm sát thương.
- `src/pac/ChessScreen.java`: bảng Battle Stats và bảng chi tiết Pokémon.

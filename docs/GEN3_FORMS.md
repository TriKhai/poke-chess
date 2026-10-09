# Gen 3 — v1.7.5

Audit 08/10/2026 từ SpriteCollab-master do chủ project cung cấp. Không thêm dạng chỉ có portrait. Tool kiểm tra 6 clip, 8 hướng, kích thước sheet và frame không rỗng; các action phụ được fallback theo importer hiện có.

## Đã thêm

- 11 Mega mới: Sceptile, Gardevoir, Mawile, Medicham, Sharpedo, Altaria, Chimecho, Absol, Glalie, Latias, Latios.
- 5 Mega Gen 3 đã có được rà lại tài nguyên: Sableye, Manectric, Camerupt, Banette, Rayquaza. Tổng cộng 16 Mega Gen 3.
- Medicham Mega chưa có animation shiny riêng: giữ trạng thái shiny/chỉ số và dùng animation Mega thường.
- Zigzagoon Galar chọn trước khi mua. Ghép 3 → Linoone Galar; ghép 3 Linoone Galar → Obstagoon. Không ghép lẫn dạng thường/Galar; không bán riêng Linoone/Obstagoon.
- Castform Sunny/Rainy/Snowy: Thẻ nhớ quay vòng ba dạng, hệ theo dạng. Kecleon Purple: Thẻ nhớ, không cộng hưởng riêng, chưa có shiny animation riêng.
- Primal Kyogre/Groudon và Deoxys Attack/Defense/Speed đã có: nhập lại animation normal/shiny đã qua audit.

## Nhánh và sửa ghép

- Wurmple chọn Silcoon/Cascoon, sau đó lên Beautifly/Dustox.
- Kirlia chọn Gardevoir/Gallade; Snorunt chọn Glalie/Froslass; Clamperl chọn Huntail/Gorebyss.
- Nincada chọn Ninjask/Shedinja. Đây là cơ chế game chuyển thể: không tạo thêm Shedinja miễn phí.
- Sửa họ/đường Budew → Roselia → Roserade, Chingling → Chimecho; dạng tiến hóa không bán riêng.
- Sửa bộ lọc merge để Linoone Galar có thể lên Obstagoon dù nhánh Linoone thường đã kết thúc.
- Cộng hưởng chỉ tính bậc cao nhất của chuỗi Galar; dạng thường và Galar vẫn là hai nhánh. Mega/weather/cosmetic không tạo thêm danh tính cùng loài.
- Sửa hệ Lửa của Primal Groudon trong bảng thông tin, combat và cộng hưởng.
- Các Mega mới dùng bonus cân bằng theo thang chỉ số của game; không phải chỉ số base của game Pokémon gốc.

## Chưa thêm

Mega Blaziken, Swampert, Aggron, Salamence, Metagross và Absol Z thiếu bộ animation hoàn chỉnh trong nguồn hiện tại. Các alternate art, recolour và cutscene không được coi là dạng gameplay.

## Kiểm thử

GenThreeTest kiểm tra 138 trường hợp ghép/chọn nhánh/sân-hàng chờ/save, mua khi hàng chờ đầy, chuỗi Galar, 16 Mega x normal/shiny, weather, loại dạng thiếu resource. Bộ kiểm thử Gen 1/2 vẫn chạy cùng.

Kiểm thử tự động không xác nhận chất lượng hình ảnh/cảm ứng trên giả lập. Chủ project cần thử tay popup và các animation mới.

Nguồn đối chiếu Mega Chimecho: https://www.pokemon.com/uk/pokedex/chimecho . Credits asset giữ trong res/credits/Gen3-forms.txt và giấy phép SpriteCollab hiện có.

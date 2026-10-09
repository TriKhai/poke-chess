# Nguồn và ghi công

Danh sách người vẽ theo từng bộ asset hiện có trong game: [CREDITS_ASSETS.md](CREDITS_ASSETS.md). Tên được tra từ credits nguồn và bảng tên SpriteCollab; phần chưa xác minh được đánh dấu riêng.

## Nhạc nền (thông tin bổ sung do chủ project cung cấp)

- Auto Chess: **Littleroot Town (Remix) - Pokemon: Ruby, Sapphire & Emerald**.
  Composed by: Go Ichinose, Morikazu Aoki, Junichi Masuda, Hitomi Sato.
  Tác giả bản remix chưa được xác minh.
- Khám phá: **Hyper Potions - Littleroot Town (Pokemon Ruby & Sapphire)**.
  Remix: Hyper Potions; nhạc gốc Littleroot Town: Go Ichinose.
- Menu: Tình Bạn Vĩnh Cửu — Lương Bằng Quang; thể hiện Cindy V.
- Game đóng gói MP3 mono 48 kbps; thông tin ghi công không xác nhận quyền phân phối nhạc.

Tileset thu phục mới (1.7.18): tạo bằng công cụ imagegen tích hợp;
nguồn và prompt ở assets/tilesets/explore-v2 và explore-gen4 đến explore-gen9,
bố cục/va chạm ở data/explore-map-v2.json. Sáu Gen mới có art và geometry riêng.

Trạng thái: bản ghi nguồn đang rà soát, chưa xác nhận quyền phát hành toàn bộ game.
Tài liệu này bổ sung CREDITS.txt, không thay thế các điều kiện giấy phép của chủ sở hữu.

## Pokémon Auto Chess

Nguồn: https://github.com/keldaanCommunity/pokemonAutoChess

Script generate_gen123_roster.py lấy roster từ source tham khảo;
generate_item_data.py lấy enum, stats và recipes từ app.zip;
generate_collection_dex.py lấy dữ liệu/portrait các thế hệ sau.
Các script make_* chuyển đổi atlas, portrait và hiệu ứng sang dạng nhỏ cho Java ME.
Code upstream có GPL-3.0. Phạm vi phần chuyển thể trong game cần đối chiếu trước khi chọn LICENSE.

## PMDCollab / SpriteCollab

Nguồn: https://github.com/PMDCollab/SpriteCollab

Các dạng nhập trực tiếp được ghi trong res/credits/SpriteCollab-forms.txt và
res/credits/Gen2-forms.txt; bản giấy phép upstream được lưu ở
res/credits/SpriteCollab-LICENSE.md.
Thay đổi: cắt/đóng gói frame vào PACR, tạo DAT, đổi kích thước portrait,
tạo avatar từ sprite khi thiếu portrait. Những thay đổi này không đổi chủ sở hữu nguồn.

Credits hiện còn định danh Discord, nhãn PMDCollab_1 và Unspecified.
Cần giữ nguyên bản ghi và xác minh tác giả/điều kiện của từng asset sử dụng;
không suy diễn mọi asset trong repo đều có cùng giấy phép.

## Nguồn khác chưa hoàn tất

- Font tahoma_7/tahoma_7b: chủ project xác nhận tự làm; ghi nhận theo thông tin chủ project cung cấp.
- Artwork item: chủ project (Kdic) xác nhận tự làm; ghi nhận theo thông tin chủ project cung cấp.
- Hiệu ứng và map: các bộ reference assets; cần kiểm tra theo từng nhóm trong docs/licensing/ASSET_SUMMARY.md.
- Các hình được tạo mới: phải ghi nhận nguồn tạo và tách khỏi hình bên thứ ba; hiện chưa xác minh từng file.

Tên, nhân vật và hình ảnh Pokémon liên quan đến các chủ sở hữu quyền tương ứng.
Không có xác nhận dự án được các chủ sở hữu Pokémon tài trợ hoặc cấp phép.
Thông báo fan project và ghi công không thay thế quyền sử dụng.

# Gen 1–9 — v1.7.6

## Phạm vi thực tế

Roster hiện tại giữ nguyên 944 Pokémon và toàn bộ species/save IDs cũ. Không
thêm 81 Pokémon gốc vắng trong metadata gameplay hiện tại bằng chỉ số tự đoán.
Excel liệt kê đủ National Dex #1–1025, mỗi gen một sheet, đánh dấu rõ các dòng
chưa có trong game. Vì vậy đây không phải tuyên bố có đủ mọi Pokémon/dạng.

Gen 4–9 và các dạng cấp trước Gen 1 rà bổ sung: 226 bộ form được đóng gói; 222 có Pokémon gốc trong roster nên mở
được trong gameplay. Crowned Zamazenta, Roaming Gimmighoul, Low Power Miraidon,
Terastal Terapagos mới có tài nguyên form; Pokémon gốc chưa nằm trong roster.
14 Mega mới mở được: Lucario, Gallade, Darkrai, Excadrill, Scrafty, Eelektross,
Floette, Dragalge, Hawlucha, Zygarde, Diancie, Drampa, Zeraora, Tatsugiri.
52 ứng viên được xét nhưng thiếu animation đầy đủ, không bật. Gen 1 bổ sung
Mega Slowbro normal/shiny và 16 dạng cấp trước/Tauros Paldea. Alola Grimer thiếu
animation nên không bật; Galar Meowth/Farfetch'd không chuyển nhầm thành Persian
khi Perrserker/Sirfetch'd chưa có trong roster. Popup mua có thể nhiều lựa chọn.
Các Mega cũ có tài nguyên trong game được giữ lại dù bản SpriteCollab hiện tại
chưa hoàn thiện animation tương ứng.

Không nhập Alternate, Altcolor, Cutscene, Beta, Skytemple, bộ phận cơ thể,
Rotom Phone/Drone/Dex, Starmobile hay ảnh đơn không có animation. Các phối màu
chính thức như Vivillon/Alcremie nằm trong danh sách xét riêng.

## Tiến hóa và hệ

Topology dựa trên [CSV PokeAPI](https://github.com/PokeAPI/pokeapi/tree/master/data/v2/csv):
475 cạnh tiến hóa thường, 9 con cần bố/mẹ biến thể không được nối vào tuyến
thường. Điều kiện đá hệ, giới tính, trao đổi, độ thân thiện… được quy đổi thành
ghép 3 và popup lựa chọn của game; không mô phỏng đầy đủ luật game gốc.

Nhánh Scizor/Kleavor, Wormadam/Mothim, Froslass/Glalie, Gallade/Gardevoir,
Solgaleo/Lunala, Flapple/Appletun/Dipplin… không bị ghi đè bởi suy đoán cùng họ.
Các tuyến baby Gen 4 nối đúng về Sudowoodo, Mr. Mime, Chansey, Snorlax, Mantine.
Không bán riêng dạng đã tiến hóa.

Galar Darumaka/Yamask, Hisui Zorua/Sliggoo giữ đúng dạng khi tiến hóa;
White-striped Basculin đi Basculegion. Ba bản khác dạng không tự ghép lẫn.
Burmy có cloak vẫn được chọn Mothim; nhánh Hisui không cộng hưởng trùng giữa
cấp trước và cấp sau. Mega/item/cosmetic giữ cùng identity; nhánh vùng miền
khác nhau có identity riêng. Genesect drive đổi dạng, không đổi hệ Bug/Steel.

Thẻ nhớ đổi qua các dạng đủ tài nguyên; dạng mới sau cuối danh sách có thể trở
về thường. Popup tiến hóa hỗ trợ nhiều lựa chọn, phân trang hai thẻ.
Mega dùng Đá Mega, không cần shiny. Dạng không có shiny animation giữ cờ shiny
và dùng animation thường, không tạo ảnh shiny giả.

Các dạng nâng cấp item mới dùng bonus theo thang game: HP/ATK +20% base
(tối thiểu +10 HP/+2 ATK), DEF/RES +2, tốc +5. Cosmetic/regional chỉ đổi hình/hệ.
Đây là cân bằng của game, không phải base-stat chính thức. Các cơ chế như weather,
stance, fusion, Dynamax và battle-only activation được tạm thích nghi qua Thẻ nhớ,
chưa mô phỏng điều kiện/ability riêng của từng dạng trong game gốc.

## Tài nguyên và kiểm thử

Animation đủ nghĩa là sáu action dùng được, mỗi action tám hướng, không có frame
rỗng. Walk/Hop/Hurt/Pose có thể dùng fallback Idle theo bộ đóng gói hiện có.
Không đồng nghĩa sáu action độc lập đều do tác giả vẽ. Shiny được kiểm tra riêng.

- `tools/audit_all_gen_forms.py`: kiểm kê cả tree, chưa tự bật các ứng viên.
- `tools/import_later_forms.py`: whitelist, import và catalog form ID append-only.
- `tools/generate_canonical_evolution.py`: topology dựa trên reference cached.
- `test/pac/AllGenerationTest.java`: mọi route runtime, cả hai vị trí ghép,
  từng lựa chọn, boost/shiny, save pending/completed, hàng chờ đầy, item,
  hệ combat, cộng hưởng, Mega và regional chains.
- `ReleaseArtifactTest`: PACR header, sáu action × tám hướng, frame dimensions,
  giới hạn tọa độ trong PNG, EOF, avatar normal/shiny khi có, credits, JAD/JAR version và size.
- Atlas mới gộp các frame có pixel hoàn toàn giống nhau (giữ offset/palette/
  alpha), giảm diện tích giải mã PNG. `tools/test_spritecollab_pack.py` kiểm
  tra các bất biến này; không phải đo FPS trên điện thoại.
  Ví dụ cùng chiều rộng 1024 px: Wishiwashi School giảm chiều cao 5172 → 625,
  Black Kyurem 4874 → 1273, Eternamax Eternatus 3926 → 958.

Nguồn form: [SpriteCollab](https://github.com/PMDCollab/SpriteCollab).
Credits normal và shiny đi cùng bản Full trong `res/credits/Gen4-9-forms.txt`.
Reference URL/hash nằm trong `outputs/all-gen-audit/reference/sources.json`;
audit chi tiết và Excel có bản sao trước khi cập nhật trong `outputs/`.

Kiểm thử desktop/build không thay thế thao tác cảm ứng/quan sát animation trên
giả lập hoặc điện thoại. Chưa xác nhận FPS và UI thật trên thiết bị.

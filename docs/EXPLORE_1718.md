# Thu phục Pokémon — 1.7.18

Mở đủ khu Gen 1–9 (Kanto đến Paldea), danh sách cuộn và chạm hai lần để vào. Gen 4–9 có sáu tileset vẽ mới bằng công cụ imagegen tích hợp và sáu bố cục riêng, không dùng lại map Gen 1–3. Danh sách Pokémon, nhiệm vụ, bộ sưu tập và tiến độ thưởng lọc riêng từng Gen. Mở rộng mảng lưu có độ dài trong dữ liệu nên vẫn đọc được tiến độ Gen 1–3 cũ. Pokémon hạng 5 cũng xuất hiện thường để không làm mốc hoàn thành bất khả thi.

## Sáu map mới

- Gen 4 / Sinnoh: núi lạnh, hai đèo qua sườn đá, hồ núi và rừng thông.
- Gen 5 / Unova: kênh dài với ba cầu, quảng trường lát đá và vườn bỏ hoang.
- Gen 6 / Kalos: lối dạo hình vòng, vườn hoa, cánh đồng lavender và hồ nhỏ.
- Gen 7 / Alola: vịnh nhiệt đới, bờ cát, rừng cọ và mũi đất núi lửa; rìa map là nước.
- Gen 8 / Galar: suối uốn khúc với hai cầu, vòng đá cổ và đồng hoa heather.
- Gen 9 / Paldea: hồ trong xanh, vườn olive, hai dải hẻm núi đất đỏ có lối xuyên qua.

Mỗi map dùng atlas 20/16/12px; 18 PNG của sáu Gen tổng 36.7KB trước nén JAR. Chỉ cache ba cỡ ảnh của khu hiện tại, giải phóng tham chiếu khi đổi khu; không load sáu ảnh nguồn lớn vào game. Ảnh nguồn/prompt: `assets/tilesets/explore-gen4` đến `explore-gen9`, mỗi thư mục có `source.png` và `source.prompt.txt`. Pipeline: tile_mode / tilemap / tile-object layers + scene hooks / tile_collision / project-native. Dữ liệu va chạm không suy ra từ pixel.

Ảnh tổng quan QA: `assets/tilesets/explore-v2/gen-4-9-overview.png`; preview từng map và camera có bóng ở thư mục tileset tương ứng. Đây là ảnh ghép từ tile/data để kiểm tra, không phải ảnh chụp giả lập.

- Nhân vật là Pokémon trong hồ sơ, không phải quả bóng. Di chuyển tự do tám hướng bằng phím giữ hoặc cảm ứng giữ/kéo; mô phỏng bước 20ms và nội suy vị trí bằng cùng helper với Pokémon Nổi Loạn. WALK theo quãng đường thực, IDLE khi dừng hoặc bị chặn; hướng 8 phía, chân sprite bám đúng vị trí trên map. Gen 4–9 dùng CollectionAtlas, Gen 1–3 dùng RawAtlas.
- Va chạm dùng bán kính bóng với cây/đá/nước và Pokémon đang đứng; chạy chéo được chuẩn hóa tốc độ. Khi mở nhật ký, giao chiến, đổi màn hình hoặc nhả cảm ứng, xóa trạng thái di chuyển để không bị chạy tiếp.
- Giữ nguyên cơ chế 1v1 giảm HP để thu phục. Cảm ứng popup chọn Bắt ngay/Tiếp tục đánh dùng chạm chọn, chạm lại xác nhận; popup kết quả chạm để tiếp tục. Bóng đã dùng được lưu ngay khi ném.
- Nhật ký mở bằng * hoặc nút dưới map. Hai tab: Nhiệm vụ / Pokémon đã bắt. 4/6 đổi tab, 2/8 cuộn; chạm trực tiếp tab để đổi. Tab đã bắt hiện avatar, tên, hệ và số lần bắt cho map Gen đang chơi, gồm dữ liệu đã lưu từ các chuyến trước.
- Tileset được vẽ bằng imagegen; ba atlas 20/16/12px tổng khoảng 6.2KB. Map Gen 1–3 thêm vòng đường đi, nền khác nhau theo vùng và sửa vùng nước Johto để có bờ sinh Pokémon hệ Nước. Dữ liệu va chạm/spawn/zone và ảnh vẽ tách riêng; chỉ render tile trong camera.
- Nguồn: assets/tilesets/explore-v2/source.png; prompt cùng thư mục. Chuẩn hóa và ghép preview: tools/prepare_explore_tiles.py. Dữ liệu map: data/explore-map-v2.json (xuất từ ExploreMapLayout, phải tái xuất khi đổi bố cục).
- Sau khi compile, dùng `python tools/prepare_explore_tiles.py --refresh-layout --java "C:/Program Files (x86)/Java/jdk1.8.0_503/bin/java.exe"` để cập nhật JSON từ Java, giữ provenance và ghép lại preview. Script chỉ crop/resize/quantize art đã vẽ và xuất dữ liệu, không tự vẽ artwork.
- Về game có tên đầy đủ hai bản Littleroot và nhóm nhạc sĩ theo thông tin người dùng cung cấp; không thay đổi âm thanh hoặc khẳng định đã có quyền phân phối.

Kiểm tra tự động: bước thời gian, đường chéo, vật cản, không đi xuyên Pokémon, chín map kết nối các vùng, chín bố cục khác nhau, lọc Gen và số lần bắt trùng, đóng gói đủ ba cỡ tileset của từng Gen mới. Cảm ứng thực tế và FPS/âm thanh vẫn cần test trên giả lập.

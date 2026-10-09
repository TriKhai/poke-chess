# Thủ Lính — v1.7.3

**Cập nhật đang dùng: v1.7.4.** Xem [luật và thay đổi v1.7.4](SURVIVAL_174.md)
cho AoE, boss, vai địch, double-tap lướt, idle và HUD mới. Các ghi chú về tuyệt
kỹ một mục tiêu/XP trong cột bên dưới là mô tả nền v1.7.3, đã được thay thế.

Vào **Khám Phá → Thủ Lính → Chọn pet**. Có màn avatar riêng cho các Pokémon đã
sở hữu, không đang ở bãi và có dữ liệu chiến đấu. Không đổi avatar Hồ sơ hay pet
Farm. Chọn dạng cuối vẫn bắt đầu bằng dạng đầu trong họ; giữ nhánh đã chọn như
Vaporeon. Huyền thoại giữ loài gốc. Chế độ Cờ và dữ liệu Gen 1 không đổi.

## Điều khiển

- Giữ 2/4/6/8 hoặc joystick để đi; kết hợp hướng hoặc 1/3/7/9 để đi chéo.
- 5/FIRE: bắn mục tiêu đang ghim; lần đầu tự ghim địch gần nhất trong tầm.
  Không có địch thì bắn theo hướng nhìn. Có thể giữ hướng di chuyển và bắn cùng lúc.
- *: đổi mục tiêu. Mục tiêu có vòng đỏ và thanh avatar/tên/Máu riêng trên HUD.
  Giữ ghim khi địch ra ngoài tầm; tia không bay vô hạn. Địch chết tự ghim con kế
  tiếp trong tầm. Không còn thanh máu nhỏ trên tất cả địch.
- #: tuyệt kỹ đánh một địch đang ghim khi đầy MP và trong tầm; hiệu ứng của loài
  hiện ở điểm trúng trên địch, không ở người chơi. Giữ điểm hiệu ứng sau khi địch
  chết/đổi ghim. Buff tăng số mục tiêu để phát triển sau, chưa triển khai.
  MP tăng theo thời gian/đánh/bị đánh.
- R (phím mềm phải)/0: menu tạm dừng với Tiếp tục/Kết thúc lượt; kết thúc có xác
  nhận trước khi tổng kết. Trong menu, 2/8 chọn, 5 xác nhận, R/0 tiếp tục.
- Khi lên cấp: 2/8 chọn, 5 xác nhận. Trận dừng hoàn toàn trong lúc chọn.

## Luật lượt chơi

Địch xuất hiện từ bốn biên của sân cỏ 640 × 480 theo từng đợt. Camera theo người
chơi, radar hiển thị địch ngoài khung nhìn. Mỗi đợt tăng số lượng và độ khó; đợt
kế tiếp bắt đầu sau khi hạ hết địch và chờ ngắn. Giới hạn 28 địch hoạt động.

Hạ địch nhận XP. Mỗi lần lên cấp hồi đầy Máu/MP, xóa hồi đánh/chiêu và tạm dừng
chọn một trong ba buff hữu ích. Tiến hóa tự động ở cấp 5/10, giữ buff; đến loài
có biến thể hiện chọn dạng thường/biến thể với avatar và hệ. Mega/Nguyên thủy
mở từ cấp 15, không yêu cầu Shiny. Shiny mở từ cấp 12 và giữ Mega đã chọn.
Không đổi sở hữu, Hồ sơ hay nhiệm vụ hệ Thu Thập. Tất cả nâng cấp chỉ trong lượt.

Chiêu I/II/III/IV mở ở Lv 1/5/10/15 kể cả loài không tiến hóa và huyền thoại.
Tia tăng độ dày, bán kính va chạm, tầm và sát thương; tuyệt kỹ tăng bán kính và
sát thương. Từ chiêu III: Lửa/Cỏ/Độc gây DoT 2 giây, Nước/Dragon/Psychic/Ghost
xuyên tối đa hai mục tiêu thêm, Điện nảy một lần trong 65 px, Băng/Fairy làm
chậm 1,8 giây, Đá/Thép/Đấu/Đất đẩy nhẹ; các hệ còn lại dùng tia xuyên. Hệ dùng
đúng dữ liệu game, không tự sửa hệ loài (ví dụ Charmander có Dragon là hệ đầu).
Mega/dạng đặc biệt có tia xuyên và tầm dùng tuyệt kỹ xa thêm 25 px; tuyệt kỹ
hiện chỉ đánh một mục tiêu. Shiny dùng chỉ số Shiny hiện có và lõi tia vàng nhạt. Đây là các nhóm
kỹ năng dùng chung, không phải bộ chiêu độc quyền hoàn chỉnh cho từng loài.

Địch bị hạ có 18% cơ hội rơi quả; tối đa 8 quả, tự mất sau 12 giây. Nhặt trong
18 px: chạy +30%/8 giây, công +25%/8 giây, bắn +20%/6 giây (hồi tối thiểu
250 ms), bất tử/3 giây. Bất tử chiếm 6% số quả rơi. Cùng loại làm mới thời gian,
không cộng dồn. Icon và số giây trên map; bộ đếm dừng khi menu/chọn buff/biến thể.

Khi Pokémon gục hoặc xác nhận kết thúc: Ball thường = `KO / 5` (lấy phần nguyên)
+ số đợt đã hoàn thành. Ball được cộng một lần ở tổng kết. Chơi lại bắt đầu từ
Pet ban đầu đã chọn, không giữ buff/đạn/ghim mục tiêu. Đóng ứng dụng không lưu lượt
và không tự nhận Ball; hãy kết thúc lượt trước khi thoát.

## Giới hạn bản đầu

Tuyệt kỹ là đòn vùng chủ động nâng theo cấp chiêu, kèm hiệu ứng loài; chưa chuyển
toàn bộ nội tại/tuyệt kỹ riêng từ autochess. Map dùng tile cỏ/đất/đá tự tạo bằng
image generation; sân vẫn mở, không có vật cản giả. Cân bằng trải nghiệm và
giao diện cần test thêm trên giả lập.

## Map và animation

Dữ liệu map: `data/survival-map.json`; nguồn prompt: `assets/map/survival-terrain.prompt.txt`.
Atlas trong JAR: `res/fx/survival-terrain.png`, 128 × 64, tám tile 32 × 32.
Preview được ghép từ đúng atlas và dữ liệu tile, không phải ảnh nền bake.
Player spawn, bốn vùng sinh địch, vùng đi lại và camera được khai báo độc lập.

Thủ Lính nội suy vị trí giữa các bước mô phỏng 20 ms. Walk đổi frame theo quãng
đường thực sự di chuyển; idle giữ frame đứng; đánh/bị đánh dùng tuổi hiệu ứng.
Không thay đổi tốc độ animation của autochess hoặc nguyên bản Gen 1 đang test.

Mô phỏng dùng pool cố định 28 địch/72 tia/8 quả, không thêm bitmap mới cho quả
hoặc tia. Va chạm kiểm tra tối đa 72 × 28 mỗi bước 20 ms; nảy một lần và xuyên
hai lần thêm có mask chống đánh lại cùng địch. Chưa có đo FPS/thẩm định giao diện
trực tiếp trên giả lập; cần người chơi kiểm tra cảm giác điều khiển và cân bằng.

## HUD nhỏ chia đôi

Header chỉ có nút Bắn/Chiêu/Ghim/Dừng, số đợt/KO và radar. Bảng dưới chia đôi:
trái là avatar/tên/hệ/Máu/MP/Lv/cấp chiêu/XP của ta; phải là avatar/tên/hệ/Máu/
Công/Thủ của địch ghim. Dùng font compact 7 px, clip tên trong từng cột, thanh
máu nằm dưới số. Chạm bảng thông tin không phát lệnh di chuyển; nút cảm ứng
theo vị trí mới trên header. Không thay đổi font của các mode khác.

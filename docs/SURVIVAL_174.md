# Thủ Lính — v1.7.4

- `*` đổi ghim, `R/0` tạm dừng, `5` bắn, `#` tuyệt kỹ; giữ đủ 8 hướng.
- Nhấn hai lần cùng hướng, có nhả phím, trong 260 ms để lướt tức thời 64 px.
  Chéo được chuẩn hóa, không vượt biên. Hồi lướt 900 ms, né sát thương 160 ms.
  Phím repeat/giữ không kích hoạt lướt; cảm ứng nhận hai lần chạm hướng.
- Trả thanh máu trên đầu mọi địch; dày 6 px. Thanh chi tiết dày 7 px, có vạch
  mỗi 50 HP (gộp theo bội 50 nếu quá nhiều vạch so với chiều rộng). XP là dải 4 px
  chạy toàn bộ mép trên bảng dưới, không còn thanh XP trong cột của ta.
- Idle phát khung đứng chậm 240 ms/frame, không dùng walk. Đi reset đồng hồ idle.
  Đánh/bị đánh có tuổi animation riêng; không đổi animation chế độ Cờ.
- Tia thường dài hơn, dày thêm 2 px và có lõi sáng, không tăng hitbox chỉ vì art.
- Tuyệt kỹ có vùng tác động tại địch ghim, bán kính 32/36/40/44 px theo cấp chiêu;
  mọi địch trong vùng đều trúng. Không giới hạn một con trong vùng.
- Buff từ cấp 5 có thể tăng số vùng 1 → 2 → 3. Chọn vùng bổ sung tại địch ngoài
  vùng cũ, trong tầm thi triển; vùng phụ 65% sát thương. Mỗi địch chỉ trúng một
  lần mỗi lần thi triển, kể cả các vùng chồng nhau. Vị trí/radius/loài hiệu ứng
  được chốt lúc dùng, không thay đổi khi người chơi tiến hóa hoặc địch chết.
- Hạ gục có 550 ms hiệu ứng mua pet từ Cờ phát ngược; pool 12 điểm hiệu ứng.
  Người chơi gục có 650 ms chuyển cảnh rồi mới tổng kết. Retry tạo toàn bộ state
  mới; không lưu buff, lướt, đạn, warning hay xác/hiệu ứng của lượt trước.
- Địch: bắn xa giữ khoảng cách 85 px; cận chiến tiến tới và đánh trong 24 px;
  địch nhanh di chuyển 58 px/s nhưng chỉ 65% HP. Boss là con cuối mỗi đợt thứ 5,
  HP x3, báo vòng 34 px tại vị trí người chơi 700 ms rồi đánh; có thể chạy/lướt né.
- Chỉnh áp lực HP từ +7 xuống +6/đợt, sát thương tăng wave/3 thay vì wave/2;
  tuyệt kỹ hồi 2,2 giây. Phần thưởng vẫn KO/5 + đợt, thêm 1 Ball/5 đợt hoàn thành.

## Kiểm thử và giới hạn

Pool cố định: 28 địch, 72 tia, 8 quả, 12 hiệu ứng chết, tối đa 3 vùng tuyệt kỹ.
Không tạo ảnh mới theo frame; dùng lại hiệu ứng spawn đã có trong JAR.
`Survival174Test` kiểm thử AoE, vùng phụ/hit-mask, lướt/biên/hồi chiêu, boss/né,
idle và hiệu ứng chết; stress 10.000 bước với đủ 28 địch và 72 tia.
Thời gian stress chỉ là mô phỏng trên JVM desktop, không phải FPS giả lập và
không bao gồm tải/vẽ atlas. Chưa thẩm định giao diện hoặc FPS trên giả lập.

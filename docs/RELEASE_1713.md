# 1.7.12–1.7.13: ổn định và cân bằng

Hai mốc được gộp trong bản bàn giao 1.7.13; không thêm shop.

## Mốc 1.7.12

- Gacha có ba ô chọn loại bóng bằng icon và hàng thao tác Quay / Giúp / Auto / Về. Chạm thao tác lần đầu để chọn, lần nữa để xác nhận. Khi đang quay không đổi loại bóng hoặc rời màn.
- Vùng chạm và vẽ dùng cùng hình học, kiểm thử độ rộng 176, 240, 241, 320, 360 và 640.
- Xóa lựa chọn chạm cũ khi mở lại màn, điều hướng bằng phím và bắt đầu lượt Thủ Lính mới.
- Ví bóng cắt nội dung theo từng ô, tránh số dư dài đè ô bên cạnh; giữ nguyên số dư trong save.
- Chặn buff tốc khi đã đạt trần và chặn chọn buff trước khi hoàn thành chọn hệ.
- Sửa đạn đứng yên nếu mục tiêu trùng vị trí người chơi.
- Giữ định dạng save, tỷ lệ đổi bóng và cơ chế nhận thưởng đúng một lần. Kiểm thử hồi quy lưu/đọc hồ sơ, chơi lại và nhận thưởng trong bộ test chung.

## Mốc 1.7.13

- Tối đa 5 tia × 3 lượt; tia chính 100%, tia phụ 60%. Lượt liên xạ sau dùng 85% sát thương so với lượt đầu.
- Hồi bắn cơ bản ít nhất 400 ms, cộng 80 ms mỗi lượt liên xạ bổ sung; trái tăng tốc bắn vẫn có hiệu lực và trần 400 ms.
- Pool 72 viên chia 56 cho người chơi, 16 cho địch. Không tăng kích thước hoặc cấp phát đạn mỗi tick; tránh một phe chiếm hết khả năng bắn của phe kia.
- Máu quái: 24 + máu loài/3 + đợt×5 + max(0, đợt−10)×2. Quái nhanh 65% máu; boss ×3.
- Công quái: max(2, công loài/3) + đợt/4 + max(0, đợt−10)/6. Các phép chia là số nguyên.
- Boss và thêm lính mỗi 5 đợt giữ nguyên; tối đa 28 quái sống cùng lúc.
- Roll buff chỉ duyệt danh sách cố định, không lặp thử ngẫu nhiên vô hạn khi nhiều buff đã đạt trần.

## Kiểm thử và giới hạn xác nhận

ReleasePolishTest kiểm tra vùng chạm, trạng thái xác nhận, hai phần pool đạn, sát thương/hồi liên xạ, độ khó tăng theo đợt và hai bài 10.000 tick: bắn tối đa, rồi 28 quái + 72 viên cùng tồn tại. Đây là mô phỏng desktop, không phải FPS trên thiết bị.

Trước khi gọi bản ổn định, cần test tay:

1. Gacha: chạm từng icon, Quay/Auto/Giúp/Về, hết bóng; không trừ hai lần khi chạm nhanh.
2. Đổi bóng, đóng và mở game, xác nhận đủ ba số dư.
3. Thủ Lính: đi và bắn, ghim, ulti vùng, dash, chọn nhánh; kiểm tra HUD ở màn hình thực tế.
4. Chơi tới boss đợt 5/10; cảm nhận độ khó và kiểm tra FPS khi đông quái.
5. Chết/kết thúc, nhận thưởng một lần, chơi lại: buff/đạn/đợt phải trở về đầu.

Chưa chạy kiểm tra hình ảnh/cảm ứng hoặc FPS trên giả lập/điện thoại. Cân bằng là bản đầu để tiếp tục điều chỉnh từ kết quả chơi thật.

# Thủ Lính: tia đánh thường (1.7.3)

- Phím 5 phóng tia màu hệ chính, hướng tới địch gần nhất trong tầm; không có địch thì bắn theo hướng đang nhìn. Không hiện thông báo ngoài tầm.
- Tia địch giữ hướng lúc bắn, bay với tốc độ 180 px/s; chỉ gây sát thương khi chạm người chơi. Có thể né, tia hết hạn hoặc ra khỏi map sẽ bị thu hồi.
- Tia người chơi bay 320 px/s, hết hạn theo tầm đánh. Chạm địch đầu tiên mới gây sát thương.
- Pool tối đa 72 tia, cập nhật bước cố định 20 ms; tạm dừng cùng màn chọn buff. Miễn sát thương ngắn sau khi trúng đòn vẫn giữ để tránh mất toàn bộ máu do nhiều tia chồng nhau.
- Kết thúc lượt dọn sạch tia. Chơi lại tạo mô phỏng mới và xóa thông báo, bộ đếm hiệu ứng, con trỏ và lệnh di chuyển tạm của màn hình.
- Kiểm thử tự động: bắn không gây sát thương tức thì, tia trúng/miss, dọn tia cuối lượt và trạng thái mô phỏng mới. Chưa kiểm thử trực tiếp trên giả lập.

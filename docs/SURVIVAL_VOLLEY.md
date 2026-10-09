# Thủ Lính 1.7.7 — Buff đạn

- Mỗi lần lên cấp vẫn chọn một trong ba buff. Đa tia và Liên xạ được đưa vào nhóm buff thường.
- Đa tia: từ 1 tới 5 tia hình quạt quanh hướng ghim. Tia chính 100% sát thương, tia phụ 60%.
- Liên xạ: từ 1 tới 3 lượt, cách nhau 120 ms, giữ hướng ngắm của lần bắn đầu; đạn xuất phát từ vị trí hiện tại nên vẫn vừa đi vừa bắn.
- Hai buff kết hợp: tối đa 15 viên mỗi lần đánh. Năng lượng chỉ nhận một lần, không nhân theo số viên.
- Tầm đánh vẫn tăng qua buff: tối đa ba lần +1, chỉ số tầm tối đa 6. Đạt giới hạn thì không được đề xuất tiếp.
- Đạn dùng chung pool 72 ô cố định. Pool đầy thì không tạo thêm; lần bắn thất bại không tiêu hồi chiêu.
- Popup nâng cấp và tạm dừng đóng băng các lượt bắn nối tiếp; kết thúc run hủy chúng. Lượt chơi mới bắt đầu với một tia, một lượt.

Kiểm thử tự động nằm trong SurvivalVolleyTest. Chưa xác nhận FPS và cảm ứng trên giả lập.

# 1.7.14 — Thử nhạc nền MIDI

## Bản thử menu MP3 mono 48 kbps

Menu hiện phát `res/music/menu.mp3` bằng MMAPI `audio/mpeg`: nén từ MP3 64 kbps do người dùng cung cấp, mono 22050 Hz / 48 kbps, giữ cả bài 150.77 giây, 905213 byte, bỏ ảnh bìa. MIDI menu cũ không đóng gói trong Full JAR. Hai bài Littleroot vẫn là MIDI. Các ghi chú MIDI menu phía dưới là lịch sử thử nghiệm, không phải cấu hình hiện tại. Chưa xác nhận hỗ trợ MP3 hoặc FPS trên giả lập/điện thoại; lỗi phát không ngăn gameplay và hiển thị trong Cài đặt.

## Cập nhật bản thử ngày 2026-10-09

Nhạc menu đã thay bằng MIDI do người dùng tự convert (file nguồn không có đuôi trong `only_tham_khao/music`), giữ nguyên dữ liệu: 148156 byte, 18515 nốt, tối đa 26 nốt đồng thời, khoảng 149 giây. Không chạy lại converter hoặc giảm nốt. Hai bài Littleroot vẫn giữ bản MIDI cũ. Các thông tin bốn giọng và Basic Pitch bên dưới chỉ còn áp dụng cho hai bài Littleroot; nhạc menu cần kiểm tra âm thanh/hiệu năng trên giả lập thực tế.

| Phạm vi | File trong game | Nguồn do người dùng cung cấp |
|---|---|---|
| Vào game, menu và các trang menu phụ | res/music/menu.mid | Tình Bạn Vĩnh Cửu — Cindy V, 43QBA4OD1x4 |
| Auto Chess, chọn mode/thế hệ/kiểm thử, lịch sử đấu | res/music/autochess.mid | Littleroot Town (Remix), c4qHrexNRS8 |
| Toàn bộ Khám phá: bản đồ, bắt Pokémon, Gacha, Đổi bóng, Thủ Lính, tổng kết | res/music/explore.mid | Hyper Potions — Littleroot Town, jV3w2PZn8z4 |

Các MP3 gốc không bị thay đổi và không đưa vào JAR. MIDI là bản suy luận nốt tự động bằng [Spotify Basic Pitch](https://github.com/spotify/basic-pitch), không phải bản thu remix giữ nguyên âm sắc. Dùng GM piano, tối đa bốn nốt đồng thời, không giữ giọng hát/trống/pitch bend. Sai nốt hoặc thiếu bè có thể xảy ra; cần nghe thử.

## Phát nhạc

- Một worker xử lý tải, prefetch và phát; không xử lý audio trong update/paint.
- Chỉ một MMAPI Player và một input stream sống; lặp vô hạn.
- Chuyển màn cùng nhóm không khởi động lại nhạc.
- Đổi nhóm, tắt nhạc hoặc đưa app ra nền sẽ đóng player/stream. Quay lại sau pause phát lại từ đầu.
- Âm lượng mặc định 35%; Cài đặt có Nhạc nền bật/tắt, lưu riêng vào RMS pac_music để không đổi cấu trúc save game.
- Thiết bị không hỗ trợ audio/midi sẽ tiếp tục chơi không nhạc; Cài đặt hiển thị Lỗi phát.

## Kiểm tra

MusicRoutingTest kiểm tra ánh xạ màn; MidiResourceTest đọc MIDI thực, đếm nốt, kiểm tra kết thúc nốt và tối đa bốn giọng. Kiểm thử desktop không mở bộ tổng hợp âm thanh Java ME.

Chưa xác nhận chất lượng nghe, MMAPI thực hoặc FPS trên giả lập/điện thoại. Không suy ra “không lag” chỉ từ dung lượng MIDI nhỏ.

Test tay cùng thiết bị, cùng cài đặt FPS:

1. Bật nhạc, vào menu → Auto Chess → Khám phá/Gacha/Thủ Lính. Kiểm tra đúng bài, không chồng nhạc.
2. Chuyển các màn trong cùng chế độ: nhạc không bắt đầu lại. Ra nền/quay lại và thoát app: không còn tiếng sau thoát.
3. Thủ Lính lúc đông quái, build 5 tia × 3 lượt: ghi FPS khi bật và tắt nhạc trong Cài đặt.
4. So sánh độ khựng lúc đổi nhóm nhạc, độ phản hồi nút và RAM nếu giả lập có đo.
5. Nghe thử cả ba bản MIDI. Nếu bản dựng không đạt, thay bằng MIDI được soạn/chỉnh tay; kiến trúc phát không cần đổi.

Tạo lại: môi trường .music-runtime (không push Git), tools/convert_game_music.py. Báo cáo nguồn/sha256/dung lượng/số nốt ở outputs/music-20261009/report.json.

Đây là bản thử cá nhân. Chưa xác nhận quyền phân phối các bài nhạc; việc chuyển MIDI không tự cấp quyền đưa nhạc vào release công khai.

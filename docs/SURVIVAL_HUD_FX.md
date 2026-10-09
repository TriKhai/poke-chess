# Thủ Lính — HUD và tia đánh thường

HUD của mình: avatar sát trái, cấp dưới avatar. Ba hàng: tên + hệ và bốn icon chung bên phải; máu; năng lượng. Tên dài chạy trong vùng cắt riêng, icon hệ không bị đè. Sáu ô trang bị trống và minimap bên phải; bỏ nhãn Đồ và bảng thông tin địch, giữ thanh máu trên nhân vật địch. Header chỉ còn đợt/KO; R/0 vẫn tạm dừng. XP chạy trên viền bảng.

Cấp 1: tia trắng chung. Lên cấp 2: chọn một trong các hệ đang có (một hệ chỉ một lựa chọn), tạm dừng mô phỏng rồi tiếp tục chọn buff. Hệ chiêu được giữ trong lượt chơi, không đổi chỉ vì tiến hóa. Tuyệt kỹ dùng sprite có sẵn được chọn theo hệ và tô lại màu hệ, không lấy chiêu loài gây lệch màu. Chưa vẽ mới toàn bộ bộ hiệu ứng riêng cho mỗi hệ.

Dash thành công tốn 20 năng lượng, thất bại không trừ. Dash và bắn có bóng trắng lấy từ hình Pokémon hiện tại, mờ dần; dash thêm vệt sáng và vòng lóe, bắn thêm cung sáng. Bộ đệm bóng tái sử dụng. Chạm icon dash lướt theo hướng đang nhìn; phím nhấn đúp hướng giữ nguyên.

Tia đánh thường dùng Sprite Forge trong only_tham_khao/skill_paint, tạo ảnh bằng imagegen tích hợp: 8 khung anime năng lượng, xử lý magenta bằng script của skill, xoay tám hướng bằng tools/bake-survival-bolt.py. Atlas 256×256 dùng chung, đổi màu theo hệ qua bộ đệm 32×32 tái sử dụng, không tạo Image theo từng tia. Vị trí tia nội suy giữa các bước mô phỏng 20ms; không thay sát thương hay tầm đánh.

Nguồn và prompt: assets/survival-bolt. Tài nguyên runtime: res/fx/survival-bolt.png. Kiểm tra processor: 8/8 khung hợp lệ, không chạm biên, không ô rỗng. Cần kiểm tra trực tiếp trên KEmulator/thiết bị để xác nhận bố cục và FPS.

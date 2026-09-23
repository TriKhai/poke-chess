# Item Core v1.2.0

## Nguồn dữ liệu

- ID vật phẩm: `app/types/enum/Item.ts`.
- Chỉ số: `app/config/game/items.ts` (`ItemStats`).
- Công thức: `app/types/enum/Item.ts` (`ItemRecipe`).
- Cơ chế hiệu ứng: `app/core/effects/items.ts`.

## Cơ chế đã triển khai

- Mỗi Pokémon có tối đa ba ô trang bị.
- Nhấn `#` khi con trỏ đang chọn Pokémon trên bàn hoặc bench để mở túi.
- Hoặc nhấc Pokémon bằng `FIRE`, di chuyển xuống nút `Item`, rồi nhấn `FIRE`.
- Pokémon đã trang bị hiện dải icon vật phẩm nhỏ trực tiếp trên bàn/bench.
- Bán Pokémon bằng cách nhấc nó lên ô shop; phím `7` vẫn là lối tắt bán nhanh.

## Giao diện v1.2.1

- Kho trang bị luôn nằm dưới hàng nút; Trái/Phải đổi món, thanh phía dưới báo vị trí cuộn.
- `FIRE` trên món mở thông tin đầy đủ; `FIRE` lần nữa xác nhận cầm món.
- Chọn hai nguyên liệu hợp lệ để tạo món ghép đang chờ, sau đó đi xuống hàng avatar.
- `FIRE` trên avatar Pokémon đang ra trận mới trừ nguyên liệu và trang bị kết quả.
- Nếu chỉ chọn một món, thao tác trên avatar sẽ trang bị trực tiếp món đó.
- Từ v1.2.1a, `FIRE` trên món mở hai lựa chọn `Trang bị` và `Chi tiết`.
- Các socket item là vòng tròn tối; chỉ con trỏ hoặc món đang cầm mới phát sáng.
- Bảng thông tin Pokémon/shop cũ được giữ lại bên dưới hai hàng mới.

## Thay đổi v1.2.1b

- Tạm khóa ghép tự động; chọn món khác chỉ thay item đang cầm.
- `FIRE` trên thẻ Pokémon mở `Chi tiết / Mặc vào`.
- `Mặc vào` chỉ trang bị trực tiếp một món đã chọn; chưa chọn món sẽ không trừ kho.
- Phần ghép vật phẩm được dành cho luồng nút `Item` tiếp theo.
- Thẻ Pokémon là ô vuông nền trắng/xám trắng, dùng avatar 22px.

## Cơ chế ghép v1.2.2

- Nút `Item` mở hai tab `GHÉP ĐỒ` và `CÔNG THỨC`.
- Tab ghép chọn lần lượt nguyên liệu A và B từ kho, rồi xem trước kết quả bằng icon.
- Xuống nút `GHÉP` và nhấn `FIRE` mới trừ nguyên liệu; đóng màn hình trước đó không mất đồ.
- Cặp nguyên liệu không phân biệt thứ tự và tra trực tiếp `ItemRecipe` gốc.
- Kết quả ghép được thêm lại vào kho, không tự động mặc cho Pokémon.
- Tab công thức hiển thị `A + B = kết quả` bằng icon, kèm tên và mô tả.
- Chi tiết item hiển thị `Ghép từ` hoặc `Ghép ra` cùng icon liên quan.

## Giao diện công thức v1.2.2a

- Tab `CÔNG THỨC` là ma trận icon gồm nguyên liệu cơ bản và toàn bộ đồ ghép.
- Bốn phím hướng di chuyển con trỏ; `Lên` ở hàng đầu quay về thanh tab.
- `Trái/Phải` trên thanh tab đổi tab, bên cạnh phím tắt `1/3`.
- `FIRE` mở chi tiết icon và khi đóng sẽ quay lại đúng vị trí ma trận.
- Chi tiết nguyên liệu có hai hàng icon `Ghép với` và `Thành` tương ứng.
- Dùng phím hướng chọn vật phẩm, `FIRE` để trang bị hoặc ghép, `#`/`0` để đóng.
- Nếu thả một nguyên liệu lên Pokémon đang giữ nguyên liệu tương thích, hai món được thay bằng kết quả trong `ItemRecipe`.
- Trang bị đi theo Pokémon khi đổi vị trí.
- Khi ba Pokémon hợp nhất, tối đa ba trang bị được giữ; phần dư trở về túi.
- Khi bán Pokémon, trang bị trở về túi.
- Thắng các vòng PvE đầu (1–3) hoặc boss (8/12/16/20) nhận một nguyên liệu cơ bản.

## Chỉ số được áp chính xác

- HP, ATK, DEF, SP.DEF và Speed cộng thẳng theo `ItemStats`.
- PP được dùng làm MP khởi đầu.
- AP tăng phần trăm sức mạnh kỹ năng.
- CRIT tăng tỷ lệ chí mạng.
- SHIELD tạo khiên khởi đầu.

## Phạm vi chưa kích hoạt

Các hiệu ứng sự kiện phức tạp như phản sát thương, nảy đòn, hồi sinh,
đổi mục tiêu, miễn trạng thái hoặc kích hoạt khi hạ gục vẫn cần được port
từng hiệu ứng từ `effects/items.ts`. v1.2.0 không tự chế hiệu ứng thay thế.
# Cập nhật giao diện ghép đồ v1.2.2b

- Icon ở các hàng **Ghép với** và **Thành** dùng kích thước 16×16 để đọc rõ trên màn hình nhỏ.
- Mở màn **Ghép đồ** luôn bắt đầu bằng hai ô nguyên liệu trống, tránh giữ món từ lần mở trước.
- Nút **Làm mới** nằm cạnh **Ghép**. Khi đang chọn hàng nút, dùng Trái/Phải để đổi nút và FIRE để xác nhận.
- **Làm mới** chỉ hủy lựa chọn trong hai ô, không trừ hoặc tạo thêm vật phẩm trong kho.

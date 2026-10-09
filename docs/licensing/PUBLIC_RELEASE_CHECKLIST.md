# Chuẩn bị public GitHub

Hiện tại chưa đạt bước xác nhận quyền phát hành toàn bộ source và JAR.
Repo private có thể phục vụ quản lý dự án, nhưng không tự giải quyết quyền sử dụng.

- [ ] Đối chiếu source tham khảo với code chuyển thể, đặc biệt roster/item/recipes/synergies; xác định phần thuộc GPL.
- [ ] Xác nhận phiên bản và tác giả upstream; bổ sung copyright, thông báo thay đổi và LICENSE đúng phạm vi.
- [ ] Đối chiếu từng resource trong asset-inventory.json với file/frame nguồn thực sự.
- [ ] Giữ license và credits của từng asset, giải quyết Unspecified/PMDCollab_1 và định danh tác giả chưa rõ.
- [ ] Xác minh hoặc thay font, item, effect, map và icon chưa rõ quyền.
- [ ] Đảm bảo source/build scripts tương ứng với JAR được phát hành; build thử từ checkout sạch.
- [ ] Soát secrets và dữ liệu cá nhân trong file đang theo dõi lẫn lịch sử Git; không dán giá trị bí mật vào báo cáo.
- [ ] Không upload emulator, SDK/JDK, save RMS, log, zip nguồn tham khảo, build cũ hoặc nguyên thư mục only_tham_khao.
- [ ] Nếu từng commit file không muốn công khai: .gitignore không xóa chúng khỏi lịch sử; xử lý lịch sử là bước riêng cần thống nhất.
- [ ] Sau khi hoàn tất rà soát quyền, tạo tag/release với JAR/JAD, source tương ứng và release notes.

## Ba quyết định cần chủ project xác nhận

1. Font và bộ item: chủ project xác nhận tự làm; ghi nhận theo thông tin chủ project cung cấp.
2. Có đồng ý thay những tài nguyên chưa chứng minh được quyền phân phối không?
3. Dự định phân phối miễn phí hay có quảng cáo, bán game, bán vật phẩm hoặc hình thức thu tiền khác?

Không tự coi thu phí/quảng cáo/donation là phù hợp CC BY-NC; cần đánh giá cách sử dụng cụ thể.
Chưa thực hiện upload, đổi visibility, sửa lịch sử Git hoặc gán giấy phép toàn project.

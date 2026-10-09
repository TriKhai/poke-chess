# Chuẩn bị phát hành tại máy

Không có thao tác upload hoặc tạo repo trong quy trình này. Phiên bản hiện tại là 1.7.18; gói thử phát hành tại `releases/1.7.18-rc1`.

1. Hoàn tất checklist quyền trong `docs/licensing/PUBLIC_RELEASE_CHECKLIST.md`. Không chọn giấy phép bao trùm tài nguyên bên thứ ba khi chưa xác định phạm vi.
2. Giữ `src`, `res`, `test`, build scripts, credits và tài liệu cần thiết. Không đưa emulator, SDK, nguồn tham khảo, outputs hoặc build cũ lên repo. `.gitignore` chỉ tác động file chưa được theo dõi, không xóa file khỏi lịch sử.
3. Kiểm tra diff để không bỏ sót thay đổi hoặc vô tình loại tài nguyên runtime. Không chạy các script `patch_*` lịch sử để build.
4. Build `clean release` theo README. Kiểm tra version trong manifest/JAD và kích thước JAR khớp JAD.
5. Test tay theo `docs/GEN1_RETEST.md` và `docs/MERGE_BUGFIX_20261007.md`: ghép ba con, đội hình/bench đầy, nhánh tiến hóa, chỉ báo dạng, avatar kết quả, lưu/tiếp tục và cảm ứng.
6. Kiểm tra sinh tồn: di chuyển/bắn, đổi mục tiêu, chiêu diện rộng, dash/mana, popup lên cấp, tạm dừng/chơi lại và tổng kết.
7. Khi sẵn sàng đăng: dùng đúng source đã build cùng `PokeChess-Full.jar/.jad`, release notes, credits và thông tin giấy phép. Tạo tag chỉ sau khi chọn phiên bản chính thức.

Chưa thực hiện soát toàn bộ lịch sử Git/secrets, kiểm thử UI trực tiếp hoặc xác nhận pháp lý. Những việc này không được coi là đã hoàn tất chỉ vì build thành công.

# POKE CHESS — Bảo bối thần kỳ

Game Pokémon offline dành cho Java ME. Dự án fan-made, miễn phí, phi thương mại — dành cho người hâm mộ, được làm bởi người hâm mộ.

## Quyền Pokémon thuộc The Pokémon Company và các chủ sở hữu liên quan. Dự án có thể ngừng phát triển hoặc phát hành theo yêu cầu của chủ sở hữu quyền.

Không liên kết chính thức, không được tài trợ và không tuyên bố được các chủ sở hữu Pokémon cấp phép. Quyền đối với sprite, artwork, nhạc và code bên thứ ba thuộc từng tác giả/chủ sở hữu tương ứng.

**Phiên bản hiện tại:** 1.7.18 — bản thử phát hành RC1, cần kiểm thử thêm trên thiết bị.

**Chơi game:** tải `PokeChess-Full.jar` và `PokeChess-Full.jad` cùng phiên bản tại [Releases](https://github.com/TriKhai/poke-chess/releases) khi bản phát hành được đăng; chạy bằng thiết bị hoặc giả lập Java ME/MIDP 2.0.

**Source:** [TriKhai/poke-chess](https://github.com/TriKhai/poke-chess).

**Thực hiện:** Kdic, với sự đóng góp của Cộng đồng Game Java Việt Nam.

**Nguồn tham khảo/chuyển thể:** [Pokémon Auto Chess — keldaanCommunity](https://github.com/keldaanCommunity/pokemonAutoChess).

**Ghi công nghệ sĩ:** [Danh sách tác giả theo từng bộ tài nguyên](CREDITS_ASSETS.md).

---

## Phạm vi Pokémon và tài nguyên

Đợt rà Gen 1–9: topology tiến hóa cụ thể, dạng có animation cho Gen 4–9,
kiểm thử ghép/chọn nhánh và workbook 9 sheet. Phạm vi và tài nguyên còn thiếu:
[ALL_GEN_FORMS.md](docs/ALL_GEN_FORMS.md).
Project đang được chuẩn bị để chia sẻ source; chưa xác nhận xong quyền phát hành toàn bộ tài nguyên.

## Chơi game

Dùng `PokeChess-Full.jar` và file `.jad` cùng phiên bản trên thiết bị hoặc giả lập hỗ trợ Java ME/MIDP 2.0. Bản Full chứa animation và tài nguyên đầy đủ.

- Auto Chess: mua Pokémon, ghép ba bản tương thích để nâng bậc, chọn nhánh tiến hóa khi có lựa chọn, trang bị và xây đội hình cộng hưởng.
- Khám phá: bộ sưu tập, hồ sơ và các hoạt động khám phá; mode sinh tồn cho phép điều khiển Pokémon, đánh thường, dùng chiêu và chọn buff.
- Kiểm thử thế hệ: chọn Pokémon để giới hạn shop roll, phục vụ kiểm tra Gen 1–2 và các dạng được hỗ trợ.

Dùng phím điều hướng/2–4–6–8 và FIRE/5; xem hướng dẫn ngay trên từng màn hình. Với danh sách option cảm ứng: chạm để chọn, chạm lại để xác nhận. Trong sinh tồn, `*` đổi mục tiêu, `0` mở tạm dừng; chiêu dùng `#`.

## Build trên Windows

Cần JDK 8, Java ME SDK có API CLDC/MIDP và công cụ preverify, cùng Apache Ant. Không kèm JDK, SDK hoặc giả lập trong source.

Ví dụ PowerShell (đổi đường dẫn theo máy):

```powershell
$env:JAVA_HOME = 'C:\Program Files (x86)\Java\jdk1.8.0_503'
$env:KVEM_HOME = 'C:\Java_ME_platform_SDK_3.0'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
& "$env:KVEM_HOME\toolbar\java2\ant\bin\ant.bat" clean release
```

Hoặc chỉnh đường dẫn trong `build-poke.bat` rồi chạy. `build.properties` có các override API/preverify nếu tự dò không thành công.

Kết quả:

- `build/dist/PokeChess-Full.jar`
- `build/dist/PokeChess-Full.jad`

Target `release` chạy kiểm thử combat và kiểm tra gói phát hành. Kiểm thử tự động không thay thế việc kiểm tra cảm ứng và hiển thị trên giả lập/thiết bị thật.

## Cấu trúc source

| Thư mục/file | Nội dung |
| --- | --- |
| `src/pac/` | Code game Java ME |
| `res/` | Tài nguyên runtime và credits; cần giữ để build |
| `test/` | Kiểm thử Java |
| `tools/` | Công cụ tạo/chuyển đổi tài nguyên; một số cần nguồn tham khảo không nằm trong repo |
| `docs/` | Phạm vi Pokémon, chỉ số/trạng thái, nguồn chuyển thể và thông tin quyền tài nguyên |
| `build.xml`, `build.properties`, `build-poke.bat` | Build bằng Ant |

Build game sử dụng tài nguyên đã bake trong `res/`, không cần chạy lại mọi script tạo asset.
`README.txt` và `CHANGELOG.txt` giữ thông tin lịch sử; hướng dẫn hiện tại là tài liệu này.

## Nguồn và giấy phép

POKE CHESS / Bảo bối thần kỳ phiên bản JAR là dự án fan-made do **Kdic** thực hiện, với sự đóng góp của **Cộng đồng Game Java Việt Nam**, dựa trên mã nguồn mở Pokémon Auto Chess của **keldaanCommunity**.

Pokémon, tên nhân vật và các nhãn hiệu liên quan thuộc về **The Pokémon Company và các chủ sở hữu quyền tương ứng**. Dự án không liên kết chính thức, không được tài trợ và không tuyên bố được các chủ sở hữu Pokémon cấp phép. Artwork, sprite, nhạc và mã nguồn bên thứ ba vẫn thuộc về tác giả/chủ sở hữu của từng tài nguyên.

Phiên bản game này được làm để chia sẻ **miễn phí, phi thương mại**, không nhằm mua bán game hoặc tài nguyên bên thứ ba. Thông báo này không thay thế hay sửa đổi các giấy phép của mã nguồn và tài nguyên gốc. Dự án có thể ngừng phát triển hoặc phát hành theo yêu cầu của chủ sở hữu quyền.

Cảm ơn cộng đồng, các nghệ sĩ và tất cả người chơi đã góp phần vào dự án!

Xem [người vẽ từng bộ tài nguyên](CREDITS_ASSETS.md), [CREDITS.md](CREDITS.md), [CREDITS.txt](CREDITS.txt), [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md) và [checklist phát hành](docs/licensing/PUBLIC_RELEASE_CHECKLIST.md).

Chưa gán LICENSE chung: cần xác định phạm vi code chuyển thể và quyền của từng nhóm tài nguyên. Không coi việc game miễn phí hoặc có credits là xác nhận quyền phân phối. Các giấy phép/credits bên thứ ba đã lưu trong `res/credits/` phải được giữ nguyên.

## Chuẩn bị phiên bản mới

Build sạch bằng `clean release`, kiểm thử tay cảm ứng/hiển thị, lưu/tiếp tục, ghép/tiến hóa và các mode chơi; kiểm tra FPS/âm thanh trên thiết bị đích. Dùng JAR/JAD làm release assets, không commit build hay giả lập vào source. Chủ project tự tạo repo, tag và đăng GitHub Releases; đánh dấu pre-release nếu chưa hoàn tất kiểm thử trên thiết bị.

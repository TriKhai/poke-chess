"""Generate attribution for current packaged assets from local credit records.

Names are resolved without publishing artist emails. Path matches are evidence,
not proof that every historical contributor drew the exact imported frame.
"""
import json
import re
from collections import defaultdict
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'only_tham_khao/SpriteCollab-master'

def name_map():
    names = {}
    for line in (SOURCE / 'credit_names.txt').read_text(encoding='utf-8-sig').splitlines()[1:]:
        fields = line.split('\t')
        if len(fields) >= 2 and fields[0].strip():
            names[fields[1].strip()] = fields[0].strip()
    return names

def records(path, portrait, names):
    result = set()
    if not path.is_file():
        return result
    for line in path.read_text(encoding='utf-8-sig', errors='replace').splitlines():
        fields = line.split('\t')
        if len(fields) < 4:
            continue
        if portrait and len(fields) >= 5 and 'Normal' not in fields[4].split(','):
            continue
        author = fields[1].strip()
        name = names.get(author, author)
        if name.startswith('<@'):
            name = 'Discord ID ' + re.sub(r'\D', '', name) + ' (chưa có tên)'
        result.add((name, fields[3].strip()))
    return result

def main():
    names = name_map()
    imported = set()
    for file in (ROOT / 'res/credits').glob('*forms.txt'):
        for line in file.read_text(encoding='utf-8', errors='replace').splitlines():
            if re.match(r'^(sprite|portrait) \d', line):
                imported.add(tuple(line.split()))
    mega = {'150': '0150/0002'}
    catalog = ROOT / 'outputs/all-gen-audit/catalog.json'
    form_paths = {r['key']: r['path'] for r in json.loads(catalog.read_text(encoding='utf-8'))} if catalog.exists() else {}
    rows = defaultdict(list)
    other = defaultdict(list)
    portrait_groups = {'av', 'shinyav', 'dex', 'shinydex', 'formav', 'megaav'}
    sprite_groups = {'sp', 'raw', 'shinyraw', 'dexraw', 'dexshiny', 'formraw', 'megaraw'}
    for file in sorted((ROOT / 'res').rglob('*')):
        if not file.is_file():
            continue
        group = file.relative_to(ROOT / 'res').parts[0]
        if group not in portrait_groups | sprite_groups:
            other[group].append(file.relative_to(ROOT).as_posix())
            continue
        stem = file.stem
        shiny = group in {'shinyav', 'shinyraw', 'shinydex', 'dexshiny'} or stem.endswith('-shiny')
        stem = stem.removesuffix('-shiny')
        dex = int(stem.split('-')[0])
        if group in {'av', 'shinyav', 'sp', 'raw', 'shinyraw'}:
            dex += 1
        key = f'{dex:04d}'
        if group.startswith('mega'):
            key = mega.get(str(dex), key + '/0001')
        elif group.startswith('form'):
            key = form_paths.get(stem, stem.replace('-', '/'))
        if shiny:
            key += '/0001' if '/' in key else '/0000/0001'
        tree = 'portrait' if group in portrait_groups else 'sprite'
        rows[(dex, key, tree)].append(file.relative_to(ROOT).as_posix())
    lines = ['# Ghi công tài nguyên — POKE CHESS / Bảo bối thần kỳ', '',
        'Danh sách lập từ các file hiện có trong `res/`, ngày 09/10/2026. Không liệt kê map cũ đã bỏ khỏi gói game.', '',
        'Tên nghệ sĩ được tra từ `credit_names.txt` của bản SpriteCollab do chủ project cung cấp. Không công khai email liên hệ. ID chưa có tên được giữ rõ, không tự đoán.', '',
        '**Phạm vi bằng chứng:** credit portrait chỉ lấy dòng có biểu cảm Normal; sprite giữ người đóng góp trong credits của bộ animation. Ánh xạ theo đường dẫn/metadata, chưa kiểm chứng từng pixel/frame với mọi bản nguồn. Người đóng góp lịch sử không đồng nghĩa tác giả mọi frame hiện dùng. Ghi công không thay thế giấy phép phân phối.', '',
        '## Nguồn và thay đổi', '',
        '- Pokémon Auto Chess: https://github.com/keldaanCommunity/pokemonAutoChess — nguồn atlas/portrait và dữ liệu tham khảo.',
        '- PMDCollab / SpriteCollab: https://github.com/PMDCollab/SpriteCollab — nghệ sĩ được liệt kê dưới đây; giấy phép nguồn lưu trong `res/credits/SpriteCollab-LICENSE.md`.',
        '- Chuyển đổi trong project: cắt/resize portrait, đóng gói animation PACR/DAT, tạo avatar từ sprite khi thiếu portrait. Không chuyển quyền tác giả artwork gốc.',
        '- Nhãn giấy phép giữ nguyên từ nguồn: CC_BY-NC_4, PMDCollab_1, PMDCollab_2, Unspecified… Không suy diễn Unspecified thành tài nguyên tự do.', '',
        '## Người vẽ portrait và sprite/animation theo bộ asset', '',
        '| Dex / đường dẫn nguồn | Loại | Tên người đóng góp — nhãn nguồn | Bằng chứng | File trong game |',
        '| --- | --- | --- | --- | --- |']
    all_names = set()
    unresolved = 0
    for (dex, key, tree), files in sorted(rows.items()):
        explicit = (tree, key) in imported
        cp = SOURCE / tree / key / 'credits.txt'
        # Base portraits were converted from the supplied upstream portrait tree.
        supplied = ROOT / 'only_tham_khao/assets/portraits' / key / 'credits.txt'
        if tree == 'portrait' and not explicit and supplied.exists():
            cp = supplied
        rec = records(cp, tree == 'portrait', names)
        if not rec:
            unresolved += len(files)
        all_names.update(n for n, _ in rec)
        authors = '; '.join(n + ' — ' + license for n, license in sorted(rec)) or '**Chưa tìm được tác giả trong credits nguồn**'
        evidence = ('Bản import có credits đi kèm' if explicit else 'Ứng viên theo đường dẫn; cần đối chiếu bản nhập') if rec else 'Chưa đủ bằng chứng'
        location = f'{tree}/{key}'
        authors = authors.replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;').replace('|', '\\|')
        lines.append('| #' + str(dex) + ' `' + location + '` | ' + tree + ' | ' + authors + ' | ' + evidence + ' | ' + ', '.join('`' + f + '`' for f in files) + ' |')
    lines += ['', '## Các nhóm tài nguyên khác', '',
        '| Nhóm / file | Ghi công và trạng thái nguồn |', '| --- | --- |']
    descriptions = {
        'font': 'Kdic / chủ project — chủ project xác nhận tự làm bitmap font.',
        'item': 'Kdic / chủ project — chủ project xác nhận tự làm artwork item; chuyển đổi thành PNG nhỏ cho Java ME.',
        'map': 'Tileset/bố cục Thu phục và Pokémon Nổi Loạn: tạo trong quá trình phát triển với công cụ imagegen tích hợp; không gán tên nghệ sĩ PMDCollab. Prompt/source trong assets/tilesets và assets/map.',
        'music': 'Xem phần nhạc dưới đây; tên nhạc không phải bằng chứng có quyền phân phối.',
        'credits': 'Bản ghi tác giả và giấy phép nguồn, giữ nguyên người đóng góp được nêu trong từng file.',
    }
    for group, files in sorted(other.items()):
        description = descriptions.get(group, '**Chưa xác định được tên người vẽ từ tài liệu nguồn hiện có.** Không gán toàn bộ cho Kdic hoặc PMDCollab. Cần đối chiếu riêng; xem script chuyển đổi và inventory nguồn.')
        lines.append('| ' + ', '.join('`' + f + '`' for f in files) + ' | ' + description + ' |')
    lines += ['', '## Nhạc nền', '',
        '- Menu: **Tình Bạn Vĩnh Cửu** — thể hiện **Cindy V**, sáng tác **Lương Bằng Quang** (thông tin chủ project cung cấp).',
        '- Auto Chess: **Littleroot Town (Remix) - Pokemon: Ruby, Sapphire & Emerald**. Thông tin sáng tác do chủ project cung cấp: **Go Ichinose, Morikazu Aoki, Junichi Masuda, Hitomi Sato**. Tác giả bản remix chưa xác minh; danh sách này không khẳng định cả bốn cùng sáng tác riêng bài Littleroot Town.',
        '- Khám phá: **Hyper Potions - Littleroot Town (Pokemon Ruby & Sapphire)** — remix **Hyper Potions**; nhạc gốc Littleroot Town **Go Ichinose**.',
        '- MP3 đã nén mono 48 kbps. Những bản MIDI thử nghiệm không được coi là artwork/nhạc tự sáng tác.', '',
        '## Danh sách tên tra được', '', ', '.join(sorted(all_names)).replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;'), '',
        'Tên/nhân vật Pokémon thuộc các chủ sở hữu quyền tương ứng. Đây là fan project, không tuyên bố được cấp phép hoặc tài trợ.', '',
        f'Tổng kiểm kê: {sum(map(len, rows.values())) + sum(map(len, other.values()))} file; {len(rows)} bộ sprite/portrait; {unresolved} file sprite/portrait chưa có dòng tác giả phù hợp. Các nhóm khác chưa rõ tên được đánh dấu riêng.', '']
    output = ROOT / 'CREDITS_ASSETS.md'
    output.write_text('\n'.join(lines), encoding='utf-8')
    print(f'{output.name}: {len(rows)} sets, {len(all_names)} credit names/IDs, {unresolved} unresolved Pokemon files')

if __name__ == '__main__':
    main()

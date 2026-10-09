"""Read-only provenance inventory; credit evidence is NOT redistribution approval."""
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
GROUPS = {
    'sp': ('Pokemon atlases', 'tools/make_sprites.py'),
    'raw': ('Pokemon atlases', 'tools/make_raw_atlas_demo.py'),
    'shinyraw': ('Pokemon shiny atlases', 'tools/make_shiny_raw_indexes.py'),
    'av': ('Pokemon portraits', 'tools/make_avatars.py'),
    'shinyav': ('Pokemon shiny portraits', 'tools/make_shiny_avatars.py'),
    'dex': ('Later-generation portraits', 'tools/generate_collection_dex.py'),
    'shinydex': ('Later-generation shiny portraits', 'tools/make_collection_shiny.py'),
    'dexraw': ('Later-generation atlases', 'tools/make_collection_raw_atlas.py'),
    'dexshiny': ('Later-generation shiny atlases', 'tools/make_collection_shiny.py'),
    'megaav': ('Mega portraits, mixed legacy/SpriteCollab imports', 'tools/make_mega_atlas.py; tools/import_spritecollab_forms.py'),
    'megaraw': ('Mega atlases, mixed legacy/SpriteCollab imports', 'tools/make_mega_atlas.py; tools/import_spritecollab_forms.py'),
    'formav': ('Special/regional/cosmetic portraits, mixed imports', 'tools/make_special_form_atlas.py; tools/import_spritecollab_forms.py'),
    'formraw': ('Special/regional/cosmetic atlases, mixed imports', 'tools/make_special_form_atlas.py; tools/import_spritecollab_forms.py'),
    'font': ('Project-owner-created bitmap font (owner confirmation; not independently verified)', 'CREDITS.txt; src/pac/Art.java'),
    'item': ('Owner-supplied item artwork; rights unresolved', 'CREDITS.txt; README.txt'),
    'sfx': ('Visual ability strips, not automatically audio', 'tools/make_species_skill_sprites.py; tools/make_skill_sprites.py'),
    'attack': ('Attack frames from reference assets', 'tools/make_combat_fx.py'),
    'status': ('Status frames from reference assets', 'tools/make_combat_fx.py'),
    'fx': ('Mixed reference effects/maps and other generated assets', 'tools/make_gacha_portal.ps1; tools/make_camp_map.ps1'),
    'maps': ('Map assets; exact authors/license unresolved', 'README.txt'),
    'credits': ('Existing license and attribution records', 'res/credits'),
}

def candidates(group, stem):
    base = stem.replace('-shiny', '')
    if not base.split('-')[0].isdigit():
        return []
    d = int(base.split('-')[0])
    if group in ('sp', 'raw', 'av', 'shinyav', 'shinyraw'):
        d += 1
    key = f'{d:04d}'
    if '-' in base:
        key += '/' + base.split('-')[1]
    if group.startswith('mega'):
        key += '/0001'
    shiny = group in ('shinyav', 'shinyraw', 'shinydex', 'dexshiny') or '-shiny' in stem
    if shiny:
        key += '/0001'
    paths = []
    for tree in ('portrait', 'sprite'):
        paths.append(ROOT / 'only_tham_khao/SpriteCollab-master' / tree / key / 'credits.txt')
    paths.append(ROOT / 'only_tham_khao/assets/portraits' / key / 'credits.txt')
    return [p for p in paths if p.is_file()]

def main():
    records, totals = [], {}
    for file in sorted((ROOT / 'res').rglob('*')):
        if not file.is_file():
            continue
        rel = file.relative_to(ROOT).as_posix()
        group = file.relative_to(ROOT / 'res').parts[0]
        source, evidence = GROUPS.get(group, ('Exact source/license unresolved', 'No source mapping established'))
        credit_paths = candidates(group, file.stem) if group in GROUPS and group not in ('font', 'item', 'sfx', 'attack', 'status', 'fx', 'maps', 'credits') else []
        credits = []
        for cp in credit_paths:
            for line in cp.read_text(encoding='utf-8', errors='replace').splitlines():
                fields = line.split('\t')
                if len(fields) >= 4:
                    credits.append({'author': fields[1], 'licenseLabel': fields[3], 'record': line, 'source': cp.relative_to(ROOT).as_posix()})
        record = {'file': rel, 'bytes': file.stat().st_size, 'sha256': hashlib.sha256(file.read_bytes()).hexdigest(), 'group': group, 'sourceHint': source, 'evidence': evidence, 'candidateCredits': credits, 'creditMatch': 'path-based candidate only; not verified against imported frame bytes', 'redistribution': 'UNVERIFIED'}
        records.append(record)
        totals[group] = totals.get(group, 0) + 1
    out = ROOT / 'docs/licensing'
    out.mkdir(parents=True, exist_ok=True)
    (out / 'asset-inventory.json').write_text(json.dumps({'asOf': '2026-10-07', 'scope': 'all current res files; candidate source matches do not grant permissions', 'files': records}, ensure_ascii=False, indent=2), encoding='utf-8')
    labels = sorted({c['licenseLabel'] for r in records for c in r['candidateCredits']})
    lines = ['# Kiểm kê tài nguyên', '', 'Ngày rà soát: 07/10/2026. Đây là bằng chứng nguồn, không phải xác nhận quyền phân phối.', '', f'Tổng: {len(records)} file trong res. Mọi file vẫn cần xác minh quyền; không tự gán MIT hoặc CC cho cả thư mục.', '', '| Nhóm | Số file | Nguồn/bằng chứng |', '| --- | ---: | --- |']
    for g, n in sorted(totals.items()):
        source, evidence = GROUPS.get(g, ('Chưa xác định', 'Chưa có ánh xạ'))
        lines.append(f'| {g} | {n} | {source}; {evidence} |')
    lines += ['', 'Nhãn giấy phép tìm thấy trong credits ứng viên: ' + ', '.join(labels), '', 'Xem asset-inventory.json để tra từng file, hash, tác giả và dòng credits ứng viên.', 'Ánh xạ chỉ theo đường dẫn/dex, chưa xác nhận frame nhập thuộc đúng phiên bản/tác giả đó.', 'Không có bản ghi credits không đồng nghĩa file không có bản quyền.']
    (out / 'ASSET_SUMMARY.md').write_text('\n'.join(lines) + '\n', encoding='utf-8')
    print(f'Inventoried {len(records)} resource files across {len(totals)} groups; {len(labels)} candidate license labels. No files approved automatically.')

if __name__ == '__main__':
    main()

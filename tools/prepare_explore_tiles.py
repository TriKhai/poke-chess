"""Normalize generated tiles, not procedural artwork, for Java ME and QA."""
from pathlib import Path
from PIL import Image, ImageDraw
import json
import argparse
import subprocess

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('--refresh-layout', action='store_true', help='Export canonical Java tile/zone geometry after a successful compile')
parser.add_argument('--java', default='java')
args = parser.parse_args()
if args.refresh_layout:
    grid_path = root / 'data/explore-map-v2.json'
    meta = json.loads(grid_path.read_text(encoding='utf-8-sig'))
    result = subprocess.run([args.java, '-cp', 'build/testclasses;build/classes;test/stubs', 'pac.Explore181Test'], cwd=root, capture_output=True, text=True, check=True)
    exported = json.loads(result.stdout)
    for region in exported['regions']:
        old = next((r for r in meta['regions'] if r['generation'] == region['generation']), None)
        if old is None:
            old = dict(region)
            meta['regions'].append(old)
        old['tiles'], old['zones'] = region['tiles'], region['zones']
    grid_path.write_text(json.dumps(meta, separators=(',', ':'))+'\n', encoding='utf-8')
for gen in (0, 4, 5, 6, 7, 8, 9):
    folder = 'explore-v2' if gen == 0 else f'explore-gen{gen}'
    source = Image.open(root / f'assets/tilesets/{folder}/source.png').convert('RGB')
    assert abs(source.width / source.height - 2) < 0.05, 'atlas must be four by two cells'
    assert (root / f'assets/tilesets/{folder}/source.prompt.txt').exists()
    for size in (20, 16, 12):
        atlas = Image.new('RGB', (size * 4, size * 2))
        for row in range(2):
            for col in range(4):
                bounds = (round(col*source.width/4), round(row*source.height/2), round((col+1)*source.width/4), round((row+1)*source.height/2))
                tile = source.crop(bounds).resize((size, size), Image.Resampling.BOX)
                atlas.paste(tile, (col*size, row*size))
        prefix = 'explore-' if gen == 0 else f'explore-gen{gen}-'
        dest = root / f'res/map/{prefix}{size}.png'
        dest.parent.mkdir(parents=True, exist_ok=True)
        atlas.quantize(colors=96).save(dest, optimize=True)
        check = Image.open(dest)
        assert check.size == (size*4, size*2)
        print(dest.name, dest.stat().st_size, 'bytes')
grid_path = root / 'data/explore-map-v2.json'
if grid_path.exists():
    meta = json.loads(grid_path.read_text(encoding='utf-8-sig'))
    contact = Image.new('RGB', (1008, 576), '#101827')
    labels = ['Sinnoh', 'Unova', 'Kalos', 'Alola', 'Galar', 'Paldea']
    for region in meta['regions']:
        gen = region['generation']
        tiles = Image.open(root / ('res/map/explore-20.png' if gen < 4 else f'res/map/explore-gen{gen}-20.png')).convert('RGB')
        folder = root / ('assets/tilesets/explore-v2' if gen < 4 else f'assets/tilesets/explore-gen{gen}')
        image = Image.new('RGB', (meta['columns']*20, meta['rows']*20))
        for y, row in enumerate(region['tiles']):
            for x, tile in enumerate(row):
                image.paste(tiles.crop((tile%4*20, tile//4*20, tile%4*20+20, tile//4*20+20)), (x*20, y*20))
        image.save(folder / f'gen-{gen}-preview.png')
        if gen >= 4:
            ix, iy = (gen-4) % 3 * 336, (gen-4) // 3 * 288
            contact.paste(image.resize((336, 264), Image.Resampling.NEAREST), (ix, iy+24))
            ImageDraw.Draw(contact).text((ix+8, iy+6), f'GEN {gen} - {labels[gen-4]}', fill='#FFFFFF')
        center = image.crop((meta['columns']*10-120, meta['rows']*10-100, meta['columns']*10+120, meta['rows']*10+100)).convert('RGBA')
        # Actor-in-place QA reuses an actual packaged icon; no generated actor artwork.
        actor = Image.open(root / 'res/balls/normal.png').convert('RGBA')
        actor.thumbnail((12, 12), Image.Resampling.NEAREST)
        center.alpha_composite(actor, (120-actor.width//2, 100-actor.height//2))
        center.resize((480, 400), Image.Resampling.NEAREST).save(folder / f'gen-{gen}-gameplay-preview.png')
    contact.save(root / 'assets/tilesets/explore-v2/gen-4-9-overview.png')

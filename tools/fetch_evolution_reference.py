"""Cache upstream primary evolution data with hashes for reproducible audits."""
import hashlib
import json
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'outputs/all-gen-audit/reference'
BASE = 'https://raw.githubusercontent.com/PokeAPI/pokeapi/master/data/v2/csv/'

def main():
    OUT.mkdir(parents=True, exist_ok=True)
    manifest = []
    for name in ('pokemon_species.csv', 'pokemon.csv', 'pokemon_types.csv', 'types.csv', 'pokemon_forms.csv'):
        data = urllib.request.urlopen(BASE + name, timeout=60).read()
        (OUT / name).write_bytes(data)
        manifest.append(dict(file=name, source=BASE + name, sha256=hashlib.sha256(data).hexdigest()))
    (OUT / 'sources.json').write_text(json.dumps(manifest, indent=2), encoding='utf-8')
    print('Cached', len(manifest), 'primary reference files')

if __name__ == '__main__':
    main()

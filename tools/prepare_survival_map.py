"""Normalize generated terrain art and assemble metadata-backed QA previews."""
import json,sys
from pathlib import Path
from PIL import Image

root=Path(__file__).resolve().parents[1]
meta=json.loads((root/'data/survival-map.json').read_text(encoding='utf-8'))
source=Image.open(sys.argv[1]).convert('RGB')
raw=root/'assets/map/survival-terrain-source.png'
source.save(raw)
tile_size=meta['tileSize'];atlas=Image.new('RGB',(tile_size*4,tile_size*2))
for row in range(2):
    for col in range(4):
        box=(round(col*source.width/4),round(row*source.height/2),round((col+1)*source.width/4),round((row+1)*source.height/2))
        tile=source.crop(box).resize((tile_size,tile_size),Image.Resampling.NEAREST)
        atlas.paste(tile,(col*tile_size,row*tile_size))
runtime=root/meta['tileset'];runtime.parent.mkdir(parents=True,exist_ok=True)
atlas.quantize(colors=64).save(runtime,optimize=True)
atlas=Image.open(runtime).convert('RGB')
grid=meta['ground'];assert len(grid)==meta['rows'] and all(len(row)==meta['columns'] for row in grid)
assert all(c in '01234567' for row in grid for c in row)
world_w=meta.get('worldWidth',meta['columns']*tile_size)
world_h=meta.get('worldHeight',meta['rows']*tile_size)
assert meta['sceneHooks']['playerSpawn']=={'x':world_w//2,'y':world_h//2}
preview=Image.new('RGB',(world_w,world_h))
for y in range(world_h//tile_size):
    for x in range(world_w//tile_size):
        c=grid[y%len(grid)][x%len(grid[0])]
        i=int(c);preview.paste(atlas.crop(((i%4)*32,(i//4)*32,(i%4+1)*32,(i//4+1)*32)),(x*32,y*32))
preview.save(root/'assets/map/survival-preview.png')
actor=Image.open(root/'res/sp/3.png').convert('RGBA')
cx,cy=world_w//2,world_h//2
play=preview.crop((cx-88,cy-64,cx+88,cy+66)).convert('RGBA');play.alpha_composite(actor,(88-actor.width//2,64-actor.height))
play.resize((528,390),Image.Resampling.NEAREST).save(root/'assets/map/survival-gameplay-preview.png')
rows=',\n'.join('        "'+row+'"' for row in grid)
java='package pac;\n\n/** Generated from data/survival-map.json; art and walk geometry are independent. */\npublic final class SurvivalMapData {\n    private SurvivalMapData(){}\n    public static final int TILE=32,COLS='+str(world_w//tile_size)+',ROWS='+str(world_h//tile_size)+';\n    public static final String[] GROUND={\n'+rows+'\n    };\n    public static int tile(int x,int y){return GROUND[y%GROUND.length].charAt(x%GROUND[0].length())-48;}\n    public static boolean walkable(int x,int y){return x>=12&&y>=12&&x<=SurvivalRun.WIDTH-12&&y<=SurvivalRun.HEIGHT-12;}\n}\n'
target=root/'src/pac/SurvivalMapData.java'
if target.exists():
    old=target.read_text(encoding='utf-8').replace('\r\n','\n')
    patch='*** Begin Patch\n*** Update File: '+str(target)+'\n@@\n'+'\n'.join('-'+x for x in old.splitlines())+'\n'+'\n'.join('+'+x for x in java.splitlines())+'\n*** End Patch'
else:
    patch='*** Begin Patch\n*** Add File: '+str(target)+'\n'+'\n'.join('+'+x for x in java.splitlines())+'\n*** End Patch'
print(patch)

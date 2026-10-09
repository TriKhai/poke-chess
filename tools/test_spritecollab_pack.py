"""Mechanical atlas dedup regression; no game assets are modified by this test."""
from PIL import Image
from import_spritecollab_forms import pack

red = Image.new('RGBA',(4,4),(255,0,0,128))
blue = Image.new('RGBA',(4,4),(0,0,255,128))
normal = [[[(red,1,2,8,8),(red,3,4,8,8)] for _ in range(8)] for _ in range(6)]
shiny = [[[(blue,1,2,8,8)] for _ in range(8)] for _ in range(6)]
sheet, meta, shine = pack(normal,shiny)
assert sheet.height == 4
assert meta[0][0][0][:4] == meta[5][7][1][:4]
assert meta[0][0][0][4:] == (1,2,8,8)
assert meta[0][0][1][4:] == (3,4,8,8)
assert meta[0][0][0][:2] != shine[0][0][0][:2]
assert sheet.getpixel(meta[0][0][0][:2]) == (255,0,0,128)
assert sheet.getpixel(shine[0][0][0][:2]) == (0,0,255,128)
print('AtlasPack OK: exact-pixel dedup, palette separation, offsets and alpha preserved')

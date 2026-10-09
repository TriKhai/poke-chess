"""Validate imported PACR indexes and generate visual contact sheets."""
import json, struct
from pathlib import Path
from PIL import Image,ImageDraw
from import_spritecollab_forms import MEGAS,VARIANTS

root=Path(__file__).resolve().parents[1]
output=root/"outputs"/"spritecollab-forms-audit"
output.mkdir(parents=True,exist_ok=True)
report=[]
for kind,forms in (("mega",MEGAS),("form",VARIANTS)):
    cards=[]
    for stem,rel in forms.items():
        raw=root/"res"/("megaraw" if kind=="mega" else "formraw")
        image=Image.open(raw/(stem+".png")).convert("RGBA")
        card=Image.new("RGBA",(320,150),(23,35,54,255));draw=ImageDraw.Draw(card)
        draw.text((6,4),kind+" "+stem+" / "+rel,fill="white")
        for suffix,row in (("",0),("-shiny",1)):
            file=raw/(stem+suffix+".dat")
            if not file.exists():continue
            data=file.read_bytes();assert data[:5]==b"PACR\x06"
            pos=5;idle=[];total=0
            for action in range(6):
                for direction in range(8):
                    count=struct.unpack_from(">H",data,pos)[0];pos+=2
                    assert count>0,(stem,suffix,action,direction)
                    total+=count
                    for index in range(count):
                        vals=struct.unpack_from(">8HB",data,pos);pos+=17
                        x,y,w,h,ox,oy,sw,sh,rot=vals
                        assert x+w<=image.width and y+h<=image.height
                        assert image.crop((x,y,x+w,y+h)).getbbox()
                        if action==0 and index==0:idle.append(image.crop((x,y,x+w,y+h)))
            assert pos==len(data)
            for direction,frame in enumerate(idle):
                frame.thumbnail((36,52),Image.Resampling.NEAREST)
                card.alpha_composite(frame,(direction*39+(39-frame.width)//2,22+row*62+52-frame.height))
            report.append({"kind":kind,"stem":stem,"palette":suffix or "normal","frameCount":total,"sheetSize":image.size})
        cards.append(card)
    rows=(len(cards)+2)//3;sheet=Image.new("RGBA",(960,rows*150),(23,35,54,255))
    for i,card in enumerate(cards):sheet.alpha_composite(card,((i%3)*320,(i//3)*150))
    sheet.convert("RGB").save(output/(kind+"-preview.png"))
(output/"audit.json").write_text(json.dumps(report,indent=2),encoding="utf-8")
print("Validated",len(report),"palette indexes with nonempty frames in all 6 x 8 clips")

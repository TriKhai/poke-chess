# Build an 8x52px J2ME strip from the original TexturePacker portal atlas.
Add-Type -AssemblyName System.Drawing
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$src = Join-Path $root 'only_tham_khao\assets\environment'
$json = Get-Content -Raw -LiteralPath (Join-Path $src 'portal.json') | ConvertFrom-Json
$atlas = [System.Drawing.Bitmap]::new((Join-Path $src 'portal.png'))
$strip = [System.Drawing.Bitmap]::new(416,52,[System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$gfx = [System.Drawing.Graphics]::FromImage($strip)
$gfx.Clear([System.Drawing.Color]::Transparent)
foreach($entry in $json.textures[0].frames){
    $i=[int]$entry.filename;if($i -lt 0 -or $i -ge 8){continue}
    $f=$entry.frame;$dst=[System.Drawing.Rectangle]::new($i*52+[int]((52-$f.w)/2),[int]((52-$f.h)/2),$f.w,$f.h)
    $srcRect=[System.Drawing.Rectangle]::new($f.x,$f.y,$f.w,$f.h)
    $gfx.DrawImage($atlas,$dst,$srcRect,[System.Drawing.GraphicsUnit]::Pixel)
}
$out=Join-Path $root 'res\fx\gacha_portal.png'
$strip.Save($out,[System.Drawing.Imaging.ImageFormat]::Png)
$farm=[System.Drawing.Bitmap]::new(416,208,[System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$fg=[System.Drawing.Graphics]::FromImage($farm);$fg.Clear([System.Drawing.Color]::Transparent);$fg.InterpolationMode=[System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
$sizes=@(52,40,28,16);for($row=0;$row -lt 4;$row++){for($i=0;$i -lt 8;$i++){$s=$sizes[$row];$fg.DrawImage($strip,[System.Drawing.Rectangle]::new($i*52+(52-$s)/2,$row*52+(52-$s)/2,$s,$s),[System.Drawing.Rectangle]::new($i*52,0,52,52),[System.Drawing.GraphicsUnit]::Pixel)}}
$farmOut=Join-Path $root 'res\fx\farm_portal.png';$farm.Save($farmOut,[System.Drawing.Imaging.ImageFormat]::Png)
$fg.Dispose();$farm.Dispose();$gfx.Dispose();$strip.Dispose();$atlas.Dispose()
Get-Item -LiteralPath $out | Select-Object FullName,Length

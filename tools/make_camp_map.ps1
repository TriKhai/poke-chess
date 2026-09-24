# Downscale the original TinyMeadow preview for the fixed J2ME Camp/Farm scene.
Add-Type -AssemblyName System.Drawing
$root=Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$src=Join-Path $root 'only_tham_khao\assets\maps\TinyMeadow-preview.png'
$out=Join-Path $root 'res\fx\camp_grass.png'
$im=[System.Drawing.Bitmap]::new($src)
$dst=[System.Drawing.Bitmap]::new(240,180,[System.Drawing.Imaging.PixelFormat]::Format24bppRgb)
$g=[System.Drawing.Graphics]::FromImage($dst);$g.InterpolationMode=[System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor;$g.PixelOffsetMode=[System.Drawing.Drawing2D.PixelOffsetMode]::Half
$g.DrawImage($im,[System.Drawing.Rectangle]::new(0,0,240,180),[System.Drawing.Rectangle]::new(0,0,$im.Width,$im.Height),[System.Drawing.GraphicsUnit]::Pixel)
$dst.Save($out,[System.Drawing.Imaging.ImageFormat]::Png);$g.Dispose();$dst.Dispose();$im.Dispose();Get-Item $out|Select-Object FullName,Length

Add-Type -AssemblyName System.Drawing
$projectRoot = Split-Path $PSScriptRoot -Parent
$source = [System.Drawing.Image]::FromFile((Join-Path $projectRoot 'output/imagegen/world-clock-launcher.png'))
function Save-LauncherBitmap([string]$path, [int]$size, [int]$margin) {
    $bitmap = New-Object System.Drawing.Bitmap($size, $size)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.Clear([System.Drawing.ColorTranslator]::FromHtml('#03164A'))
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $graphics.DrawImage($source, $margin, $margin, ($size - 2 * $margin), ($size - 2 * $margin))
        $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    } finally { $graphics.Dispose(); $bitmap.Dispose() }
}
try {
    foreach ($density in @{mdpi=48;hdpi=72;xhdpi=96;xxhdpi=144;xxxhdpi=192}.GetEnumerator()) {
        $directory = Join-Path $projectRoot "app/src/main/res/mipmap-$($density.Key)"
        foreach ($name in @('ic_launcher', 'ic_launcher_round')) {
            Save-LauncherBitmap (Join-Path $directory "$name.png") $density.Value 0
            $oldFile = Join-Path $directory "$name.webp"
            if (Test-Path -LiteralPath $oldFile) { Remove-Item -LiteralPath $oldFile }
        }
    }
    Save-LauncherBitmap (Join-Path $projectRoot 'app/src/main/res/drawable-nodpi/world_clock_launcher.png') 432 40
} finally { $source.Dispose() }

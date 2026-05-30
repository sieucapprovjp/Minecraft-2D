param(
    [string]$ToolsRoot = (Join-Path $PSScriptRoot "..\assets\tools"),
    [switch]$Apply
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$tierOrder = @("wood", "stone", "iron", "gold", "diamond", "netherite", "copper")

function Get-ToolType {
    param([string]$FileName)

    $n = $FileName.ToLower()
    if ($n -match "pickaxe|pixkaxe|stone-pickaxe") { return "pickaxe" }
    if ($n -match "sword") { return "sword" }
    if ($n -match "shovel") { return "shovel" }
    if ($n -match "hoe") { return "hoe" }
    if ($n -match "axe") { return "axe" }
    return $null
}

if (-not (Test-Path -LiteralPath $ToolsRoot)) {
    throw "Tools root not found: $ToolsRoot"
}

$planned = @()

foreach ($tier in $tierOrder) {
    $tierPath = Join-Path $ToolsRoot $tier
    if (-not (Test-Path -LiteralPath $tierPath)) { continue }

    $files = @(Get-ChildItem -LiteralPath $tierPath -File | Sort-Object Name)
    if ($files.Count -eq 0) { continue }

    $grouped = @{}
    foreach ($f in $files) {
        $toolType = Get-ToolType -FileName $f.Name
        if ($null -eq $toolType) {
            Write-Host "SKIP (unknown type): $($f.FullName)"
            continue
        }

        if (-not $grouped.ContainsKey($toolType)) {
            $grouped[$toolType] = @()
        }
        $grouped[$toolType] += $f
    }

    foreach ($toolType in $grouped.Keys) {
        $toolFiles = @($grouped[$toolType] | Sort-Object Name)
        for ($i = 0; $i -lt $toolFiles.Count; $i++) {
            $src = $toolFiles[$i]
            $index = $i + 1
            $newBase = if ($index -eq 1) {
                "${tier}_${toolType}"
            } else {
                "${tier}_${toolType}_v$index"
            }

            $targetName = "$newBase$($src.Extension.ToLower())"
            $targetPath = Join-Path $tierPath $targetName

            if ($src.FullName -ieq $targetPath) { continue }

            $planned += [PSCustomObject]@{
                Tier       = $tier
                SourcePath = $src.FullName
                SourceName = $src.Name
                TargetPath = $targetPath
                TargetName = $targetName
            }
        }
    }
}

if ($planned.Count -eq 0) {
    Write-Host "No rename changes needed."
    exit 0
}

$dupTargets = @($planned | Group-Object TargetPath | Where-Object { $_.Count -gt 1 })
if ($dupTargets.Count -gt 0) {
    Write-Host "Duplicate target names detected."
    foreach ($d in $dupTargets) {
        Write-Host " - $($d.Name)"
    }
    throw "Rename aborted due to duplicate target names."
}

$sourcesSet = @{}
foreach ($p in $planned) { $sourcesSet[$p.SourcePath.ToLower()] = $true }

foreach ($p in $planned) {
    if ((Test-Path -LiteralPath $p.TargetPath) -and (-not $sourcesSet.ContainsKey($p.TargetPath.ToLower()))) {
        throw "Target already exists and is not part of rename set: $($p.TargetPath)"
    }
}

Write-Host "Planned renames ($($planned.Count))"
foreach ($p in $planned | Sort-Object Tier, SourceName) {
    Write-Host "[$($p.Tier)] $($p.SourceName) -> $($p.TargetName)"
}

if (-not $Apply) {
    Write-Host "`nDry run only. Re-run with -Apply to perform rename."
    exit 0
}

$phase1 = @()
foreach ($p in $planned) {
    $tmp = "$($p.SourcePath).__renametmp__"
    if (Test-Path -LiteralPath $tmp) {
        Remove-Item -LiteralPath $tmp -Force
    }
    Rename-Item -LiteralPath $p.SourcePath -NewName ([IO.Path]::GetFileName($tmp))
    $phase1 += [PSCustomObject]@{
        TempPath   = $tmp
        TargetPath = $p.TargetPath
    }
}

foreach ($r in $phase1) {
    Rename-Item -LiteralPath $r.TempPath -NewName ([IO.Path]::GetFileName($r.TargetPath))
}

Write-Host "`nRename complete."

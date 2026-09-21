$ErrorActionPreference = "Stop"
$key = "C:\Users\tlsrl\emotionMap\emotionMap\dev-key2.pem"
$host_ = "ubuntu@43.201.18.125"

$files = @(
    "2026-09-15-add-posts-status.sql",
    "2026-09-13-split-posts-and-comments.sql"
)

foreach ($f in $files) {
    Write-Host "=== $f ==="
    ssh -i $key $host_ "sudo mysql emotionMap < /home/ubuntu/migrations/$f"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "FAILED at $f"
        exit 1
    }
    Write-Host "--- $f OK ---"
}

Write-Host "ALL_MIGRATIONS_OK"

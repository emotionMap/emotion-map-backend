$ErrorActionPreference = "Stop"
Set-Location "C:\Users\tlsrl\intellliJ\emotion_app"

Write-Host "=== gradlew clean bootJar ==="
& ".\gradlew.bat" clean bootJar
if ($LASTEXITCODE -ne 0) { Write-Host "BUILD_FAILED"; exit 1 }
Write-Host "BUILD_OK"

$key = "C:\Users\tlsrl\emotionMap\emotionMap\dev-key2.pem"
$host_ = "ubuntu@43.201.18.125"
$jar = Get-ChildItem "build\libs\*.jar" | Select-Object -First 1

Write-Host "=== scp upload ($($jar.Name)) ==="
scp -i $key $jar.FullName "${host_}:/home/ubuntu/emotion_app-0.0.1-SNAPSHOT.jar.new"
if ($LASTEXITCODE -ne 0) { Write-Host "UPLOAD_FAILED"; exit 1 }
Write-Host "UPLOAD_OK"

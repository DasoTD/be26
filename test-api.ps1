# Test Register Endpoint
Write-Host "Testing Register Endpoint..." -ForegroundColor Green
$registerBody = @{
    username = "john_doe"
    password = "securePassword123"
    email = "john@example.com"
    balance = 0
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $registerBody -ContentType "application/json" -ErrorAction Stop
    Write-Host "Register Response:" -ForegroundColor Green
    $registerResponse | ConvertTo-Json | Write-Host
    $userId = $registerResponse.id
} catch {
    Write-Host "Register Error: $_" -ForegroundColor Red
}

Write-Host "`nTesting Login Endpoint..." -ForegroundColor Green
$loginBody = @{
    username = "john_doe"
    password = "securePassword123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json" -ErrorAction Stop
    Write-Host "Login Response (JWT Token):" -ForegroundColor Green
    Write-Host $loginResponse
} catch {
    Write-Host "Login Error: $_" -ForegroundColor Red
}

Write-Host "`nTesting Get User Details..." -ForegroundColor Green
try {
    $userResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/userDetails?username=john_doe" -Method GET -ContentType "application/json" -ErrorAction Stop
    Write-Host "User Details Response:" -ForegroundColor Green
    $userResponse | ConvertTo-Json | Write-Host
} catch {
    Write-Host "Get User Error: $_" -ForegroundColor Red
}

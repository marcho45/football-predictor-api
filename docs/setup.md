# Setup Lokal

## Prasyarat
- JDK 17+ (Amper mengunduh toolchain bila perlu)
- IntelliJ IDEA
- (M2) PostgreSQL 15+ atau Docker Desktop
- (M3) Akun RapidAPI + API key API-Football

## Menjalankan server
```
kotlin.bat run         # Windows
./kotlin run           # macOS/Linux
```
Cek: buka `http://localhost:8080/` lalu `http://localhost:8080/api/teams`.

## Test & build
```
kotlin.bat test
kotlin.bat build
```

## Android Emulator
Emulator mengakses komputer host lewat `http://10.0.2.2:8080/`, bukan `localhost`.
HP fisik: pakai IP LAN komputer (mis. `http://192.168.x.x:8080/`) dan satu jaringan Wi-Fi yang sama.
Untuk HTTP (bukan HTTPS) Android perlu `usesCleartextTraffic` / network security config pada mode debug.

## Environment variable (mulai M2)
Buat `.env.example` (di-commit) dan `.env` (JANGAN di-commit, masukkan ke `.gitignore`):
```
DB_URL=jdbc:postgresql://localhost:5432/football
DB_USER=postgres
DB_PASSWORD=change_me
RAPIDAPI_KEY=change_me
```

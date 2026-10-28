# Build Spec — Football Predictor API (Backend)

Sumber kebenaran proyek. Jika ada konflik dengan dokumen lain, dokumen ini menang.

## 1. Tujuan
Backend REST API (Ktor/Kotlin) yang menyuplai statistik kekuatan tim EPL ke aplikasi Android.
Android memasukkan data itu ke model TFLite (on-device) untuk menghitung peluang Menang/Seri/Kalah dan estimasi skor.
Proyek portofolio **Full-stack Kotlin** tanpa BaaS (Firebase/Supabase), untuk menunjukkan arsitektur, ML on-device, dan database relasional.

## 2. Scope
**In scope:** endpoint daftar tim, health check, sumber data mock lalu PostgreSQL, sinkronisasi terjadwal dari API-Football, unit/integration test.
**Out of scope (versi ini):** login/user, admin dashboard, WebSocket, menjalankan model ML di server (ML ada di Android), deploy produksi.

## 3. Tech stack
| Area | Pilihan |
|---|---|
| Bahasa/Framework | Kotlin, Ktor Server 3.x, Netty |
| Serialisasi | kotlinx.serialization (JSON) |
| Build | JetBrains Amper (`module.yaml`) + `libs.versions.toml`; CLI: `kotlin` / `kotlin.bat` |
| Database (M2) | PostgreSQL + JetBrains Exposed |
| Sumber data eksternal (M3) | API-Football via RapidAPI (Ktor Client) |
| Scheduler (M3) | Coroutine loop / library scheduler (diputuskan di M3, catat di `decisions.md`) |
| Test | Ktor `testApplication` + kotlin.test |

## 4. Peta milestone
| ID | Nama | Status |
|---|---|---|
| M0 | Bootstrap proyek & dokumentasi AI | Done |
| M1 | MVP Lokal — mock JSON | **In Progress** |
| M2 | PostgreSQL + Exposed | Pending |
| M3 | Sinkronisasi terjadwal API-Football | Pending |

Aturan: satu milestone aktif pada satu waktu. Milestone baru hanya dibuka setelah milestone sebelumnya **Done + di-approve**.

## 5. Kontrak API (jangan diubah sembarangan)
### `GET /`
`200` teks: `Football Predictor API is running!`

### `GET /api/teams`
`200` `application/json`
```json
[
  { "id": "ARS", "name": "Arsenal", "attackStrength": 2.1, "defenseStrength": 0.8 }
]
```
| Field | Tipe | Keterangan |
|---|---|---|
| id | String | Kode 3 huruf unik (ARS, MCI, LIV) |
| name | String | Nama tim |
| attackStrength | Float | Rata-rata kekuatan serangan (>= 0) |
| defenseStrength | Float | Rata-rata kekuatan pertahanan (>= 0) |

**Skala metrik:**
- `attackStrength` = gol dicetak per laga tim ÷ rata-rata liga. 1.0 = rata-rata; makin tinggi makin tajam.
- `defenseStrength` = gol kebobolan per laga tim ÷ rata-rata liga. 1.0 = rata-rata; **makin rendah makin kuat**.
- Rentang wajar: 0.4 – 2.0.

Error: `404` jika sumber data tidak ditemukan; `500` untuk error tak terduga (body JSON `{ "error": "..." }`).

Rencana (M2+, tidak boleh dibuat sebelum dibuka): `GET /api/teams/{id}`.

## 6. Aturan kualitas
- Gate sebelum lapor: build hijau + test hijau.
- Setiap endpoint punya minimal 1 test (happy path) dan 1 test error.
- Tidak ada secret di repo; sediakan `.env.example` bila memakai env.

## 7. Jangan dilakukan
Lihat `AGENTS.md` §3. Aturan di sana berlaku untuk seluruh milestone.

## 8. Glosarium
| Indonesia | Kode |
|---|---|
| Tim | `Team` |
| Kekuatan serangan | `attackStrength` |
| Kekuatan pertahanan | `defenseStrength` |
| Tuan rumah / Tamu | `home` / `away` |
| Peluang menang / seri / kalah | `homeWin` / `draw` / `awayWin` (dihitung di Android) |
| Data tiruan | `mock` |

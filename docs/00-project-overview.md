# 00 — Project Overview: Football Predictor (Full-stack Kotlin)

> Dokumen induk untuk seluruh proyek (ML + Backend + Android).
> **Salin file ini apa adanya ke repo Android Studio** (`docs/00-project-overview.md`) supaya AI di kedua repo punya konteks yang sama.
> Bagian yang bertanda **[Usulan]** adalah keputusan yang belum final; putuskan saat milestone terkait dibuka, lalu catat di `decisions.md`.

---

## 1. Ringkasan
**Football Predictor** adalah aplikasi Android yang memprediksi hasil pertandingan Liga Inggris (EPL): peluang **Tuan Rumah Menang / Seri / Tamu Menang** (dalam %) dan **estimasi skor akhir**.

Input prediksi berasal dari metrik kekuatan tim (serangan & pertahanan) yang disuplai backend Ktor. Perhitungan prediksi dilakukan **di dalam HP** oleh model TensorFlow Lite, sehingga prediksi bisa berjalan tanpa internet setelah data tim diambil.

Proyek ini adalah **proyek portofolio** untuk menunjukkan kemampuan end-to-end dengan satu bahasa (Kotlin): UI deklaratif, arsitektur bersih, REST API, database relasional, integrasi API eksternal, dan machine learning on-device. Proyek ini sengaja **tidak memakai BaaS** (Firebase/Supabase).

## 2. Tujuan
| Jenis | Tujuan |
|---|---|
| Produk | User memilih 2 tim dan langsung melihat peluang menang/seri/kalah + skor prediksi |
| Belajar | Menguasai Clean Architecture + MVVM, Ktor, Exposed + PostgreSQL, TFLite |
| Portofolio | Repo rapi, dokumentasi lengkap, diagram arsitektur, screenshot/demo, test otomatis |
| Proses | Menerapkan praktik SE (scope management, milestone, quality gate, changelog) dan memakai AI agent secara terkontrol |

## 3. Scope
**In scope**
- Memilih tim tuan rumah dan tamu (dropdown/searchable)
- Menampilkan peluang menang/seri/kalah, skor prediksi, dan kekuatan tim
- Backend yang menyuplai daftar tim + metrik (mock -> database -> data nyata)
- Model ML (dilatih di Python, dijalankan di Android via TFLite)
- Penanganan loading, error, dan kondisi tanpa internet

**Out of scope (versi ini)**
- Login/akun user, profil, favorit
- Live score, notifikasi, taruhan/odds
- Menjalankan ML di server
- Rilis ke Play Store
- Liga selain EPL

## 4. Komponen sistem
Proyek terdiri dari **tiga bagian** yang bisa dikerjakan terpisah:

| Bagian | Fungsi | Teknologi | Lokasi |
|---|---|---|---|
| **ML Pipeline** | Melatih model prediksi dan mengekspor `.tflite` | Python, pandas, scikit-learn/TensorFlow | folder `ml/` **[Usulan]** di repo Android atau repo terpisah |
| **Backend** | Menyuplai data tim lewat REST API | Kotlin, Ktor 3 (Netty), kotlinx.serialization, (Fase 2) PostgreSQL + Exposed | repo `football-predictor-api` |
| **Android App** | UI, state, memanggil API, menjalankan TFLite | Kotlin, Jetpack Compose, MVVM + Clean Architecture, Retrofit, TFLite | repo `football-predictor-android` (Android Studio) |

## 5. Arsitektur tingkat tinggi

```
                   ┌─────────────────────────── ANDROID APP ───────────────────────────┐
                   │  Compose UI ──events──> ViewModel ──> UseCase ──> Repository       │
                   │      ^                      │                      │       │       │
                   │      └────── UiState ───────┘                      │       │       │
                   │                                          Retrofit (remote) TFLite  │
                   └──────────────────────────────────────────────┬─────────────┬──────┘
                                                                  │ HTTP/JSON   │ model.tflite
                                                                  v             │ (di assets)
                                       ┌────────── KTOR BACKEND ──────────┐     │
                                       │ Route -> Service -> Repository   │     │
                                       │ Fase 1: mock_epl_stats.json      │     │
                                       │ Fase 2: PostgreSQL (Exposed)     │     │
                                       │          ^                       │     │
                                       │   Cron Job <── API-Football      │     │
                                       └──────────────────────────────────┘     │
                                                                                │
                   ┌────────────── ML PIPELINE (offline, di laptop) ────────────┘
                   │ dataset -> pandas -> training -> evaluasi -> konversi .tflite
                   └───────────────────────────────────────────────────────────────
```

### Prinsip utama
1. **Server menyuplai data, klien menghitung AI.** Pemisahan tegas.
2. **Dependency mengarah ke dalam.** UI -> Domain <- Data. Domain tidak mengenal Android, Retrofit, atau TFLite.
3. **Satu sumber kebenaran untuk kontrak.** Format JSON dan input/output model didefinisikan di dokumen ini dan `build-spec.md`; perubahan wajib disetujui.
4. **Ganti implementasi tanpa merombak UI.** Mock -> database di backend, atau TFLite -> API di Android, cukup mengganti implementasi di balik interface.

## 6. Alur data (satu prediksi)
1. User memilih tim tuan rumah dan tamu lalu menekan **Predict**.
2. Compose mengirim event ke `PredictionViewModel`.
3. ViewModel mengubah state menjadi `Loading` dan memanggil `GetMatchPredictionUseCase(homeId, awayId)`.
4. Use case meminta `TeamRepository` dan `MatchPredictor` (interface di domain).
5. `TeamRepositoryImpl` memanggil Retrofit `GET /api/teams` (atau memakai cache bila sudah ada) -> mendapat `attackStrength` dan `defenseStrength` kedua tim.
6. `TflitePredictor` memasukkan 4 angka ke model -> mendapat probabilitas dan ekspektasi gol.
7. Use case mengembalikan `MatchPrediction` (model domain).
8. ViewModel mengubah state menjadi `Success(prediction)` dan Compose menampilkannya.

> Catatan terhadap diagram Eraser: panah Retrofit -> TFLite tidak boleh langsung. Semua lewat ViewModel/UseCase.

## 7. Kontrak API (backend <-> Android)
`GET /api/teams` -> `200 application/json`
```json
[
  { "id": "ARS", "name": "Arsenal", "attackStrength": 2.1, "defenseStrength": 0.8 },
  { "id": "MCI", "name": "Manchester City", "attackStrength": 2.5, "defenseStrength": 0.9 }
]
```
Android mengubah DTO ini menjadi `Team` (domain) lewat mapper. Detail lengkap: `docs/build-spec.md` §5.

## 8. Komponen ML

### 8.1 Pipeline (Python, offline)
1. **Data** — dataset historis EPL (mis. Kaggle / football-data.co.uk): tanggal, tim, gol, hasil.
2. **Feature engineering** — hitung attack/defense strength tiap tim (rata-rata gol dicetak/kebobolan relatif terhadap rata-rata liga), bisa ditambah faktor kandang.
3. **Training** — hasil (H/D/A) sebagai klasifikasi, gol sebagai regresi.
4. **Evaluasi** — log-loss/akurasi untuk hasil, MAE untuk gol, dibandingkan dengan baseline.
5. **Ekspor** — konversi ke `football_predictor.tflite`, simpan di `app/src/main/assets/`.

### 8.2 Kontrak model (harus sama antara Python dan Android)
| | Spesifikasi |
|---|---|
| Input (`float32[1,4]`) | `[homeAttack, homeDefense, awayAttack, awayDefense]` |
| Output **[Usulan]** | `float32[1,5]`: `[pHomeWin, pDraw, pAwayWin, expHomeGoals, expAwayGoals]` |
| Aturan | `pHomeWin + pDraw + pAwayWin ≈ 1.0` (softmax); gol ≥ 0 |
| Skor tampilan | `round(expGoals)` per tim |

Alternatif bila satu model multi-output menyulitkan: dua model `.tflite` (klasifikasi + regresi). Putuskan saat milestone ML dibuka.

### 8.3 Baseline pembanding **[Usulan]**
Buat model **Poisson** sederhana (rumus statistik, tanpa ML) sebagai pembanding. Test di Android memastikan output TFLite tidak menyimpang jauh dari baseline pada beberapa pasangan tim. Ini juga memberi cerita bagus di portofolio.

### 8.4 Catatan jujur
Prediksi sepak bola secara inheren tidak pasti. Aplikasi ini adalah demo teknik, bukan alat taruhan. Cantumkan disclaimer di aplikasi dan README.

## 9. Backend (ringkas)
Detail: `architecture.md`, `build-spec.md`, `plan/`.

| Milestone | Isi |
|---|---|
| M0 Bootstrap | Proyek Ktor + dokumen AI |
| M1 MVP Lokal | `GET /api/teams` dari `mock_epl_stats.json` |
| M2 Database | PostgreSQL + Exposed, endpoint membaca DB |
| M3 Sinkronisasi | Cron job mingguan dari API-Football via RapidAPI |

Layering backend: `Route -> Service -> Repository(interface) -> Mock/Db`.

Build system backend: **JetBrains Amper** (`module.yaml`, perintah `kotlin build|test|run`). Proyek Android memakai **Gradle** (standar Android Studio).

## 10. Android App (rencana detail)

### 10.1 Stack
| Kebutuhan | Pilihan |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Arsitektur | MVVM + Clean Architecture (presentation / domain / data) |
| Async | Kotlin Coroutines + Flow (`StateFlow` untuk UI state) |
| Network | Retrofit + OkHttp + kotlinx.serialization converter |
| ML | TensorFlow Lite (`org.tensorflow:tensorflow-lite`) |
| DI **[Usulan]** | Hilt (standar industri) atau Koin (lebih ringan); pilih satu saat A0 |
| Navigasi | Navigation Compose (bila lebih dari satu layar) |
| Test | JUnit, MockK, Turbine (Flow), Compose UI test |

### 10.2 Layer dan tanggung jawab
| Layer | Isi | Boleh bergantung pada |
|---|---|---|
| **Domain** | `Team`, `MatchPrediction`, interface `TeamRepository` & `MatchPredictor`, use case | Kotlin murni saja (tanpa `android.*`) |
| **Data** | `TeamApi` (Retrofit), DTO, mapper, `TeamRepositoryImpl`, `TflitePredictor` | Domain, library jaringan/ML |
| **Presentation** | Composable, `PredictionViewModel`, `PredictionUiState`, theme | Domain |

Arah dependency: `presentation -> domain <- data`.

### 10.3 Struktur folder
```
com.portfolio.footballpredictor/
├── di/                                  # modul DI
├── domain/
│   ├── model/        Team.kt, MatchPrediction.kt
│   ├── repository/   TeamRepository.kt
│   ├── predictor/    MatchPredictor.kt            # interface; implementasi = TFLite
│   └── usecase/      GetTeamsUseCase.kt, GetMatchPredictionUseCase.kt
├── data/
│   ├── remote/       TeamApi.kt, dto/TeamDto.kt
│   ├── mapper/       TeamMapper.kt
│   ├── ml/           TflitePredictor.kt           # memuat assets/football_predictor.tflite
│   └── repository/   TeamRepositoryImpl.kt
└── presentation/
    ├── theme/
    ├── components/   TeamDropdown.kt, ProbabilityBar.kt, ResultCard.kt
    └── prediction/   PredictionScreen.kt, PredictionViewModel.kt, PredictionUiState.kt
app/src/main/assets/football_predictor.tflite
```

### 10.4 Model domain
```kotlin
data class Team(val id: String, val name: String, val attackStrength: Float, val defenseStrength: Float)

data class MatchPrediction(
    val homeTeam: Team, val awayTeam: Team,
    val homeWinProbability: Float, val drawProbability: Float, val awayWinProbability: Float,
    val predictedHomeGoals: Int, val predictedAwayGoals: Int
)
```

### 10.5 State UI
```kotlin
data class PredictionUiState(
    val teams: List<Team> = emptyList(),
    val selectedHome: Team? = null,
    val selectedAway: Team? = null,
    val result: PredictionResult = PredictionResult.Idle
)
sealed interface PredictionResult {
    data object Idle : PredictionResult
    data object Loading : PredictionResult
    data class Success(val prediction: MatchPrediction) : PredictionResult
    data class Error(val message: String) : PredictionResult
}
```
Aturan: Composable hanya **menampilkan state** dan **mengirim event**. Tidak ada kalkulasi atau pemanggilan jaringan di Composable.

### 10.6 Layar utama (wireframe teks)
```
┌──────────────────────────────┐
│ Football Predictor           │
│ [ Home team  ▼ ]  vs  [ Away team ▼ ] │
│        [  Predict  ]         │
│ ┌──────────────────────────┐ │
│ │   Arsenal  2 - 1  Man City│ │  <- skor prediksi besar
│ │  Home 48% | Draw 27% | Away 25% │
│ │  ███████░░░░░ (bar/ring) │ │
│ │  Attack/Defense tiap tim │ │
│ └──────────────────────────┘ │
│ Disclaimer: hanya demo       │
└──────────────────────────────┘
```
Validasi: tim tuan rumah ≠ tim tamu; tombol Predict nonaktif jika belum lengkap.

### 10.7 Penanganan error & offline
- Gagal jaringan: tampilkan pesan + tombol coba lagi.
- Data tim sempat berhasil dimuat: simpan **cache di memori** (A-awal); persistensi Room **[Usulan, opsional]** agar prediksi tetap bisa jalan offline.
- Model gagal dimuat: error jelas, jangan crash.

### 10.8 Milestone Android **[Usulan]**
| ID | Isi |
|---|---|
| A0 | Setup proyek Compose, DI, theme, struktur paket, dokumen AI |
| A1 | Domain layer + unit test (model, use case) |
| A2 | Data layer: Retrofit ke backend M1 + mapper + repository |
| A3 | UI: layar prediksi dengan **predictor palsu** (fake) dulu |
| A4 | Integrasi `TflitePredictor` + model `.tflite` |
| A5 | Polish: animasi, error state, cache, test UI |
| A6 | README, screenshot/demo, diagram, release APK |

Strategi: bangun UI dengan `FakeMatchPredictor` dulu, agar UI dan model ML bisa dikerjakan paralel.

## 11. Strategi pengujian
| Lapisan | Alat | Yang diuji |
|---|---|---|
| Backend | Ktor `testApplication` | status, bentuk JSON, error |
| Domain | JUnit + MockK | use case, validasi probabilitas (0–1, jumlah ≈ 1) |
| Data | MockWebServer | parsing DTO, mapper, error HTTP |
| ML | JUnit (instrumented) | output model valid, mendekati baseline Poisson |
| ViewModel | Turbine | transisi Idle -> Loading -> Success/Error |
| UI | Compose test | dropdown, tombol, tampilan hasil |

## 12. Alur kerja dengan AI agent
- Setiap repo punya `AGENTS.md`, `docs/build-spec.md`, `docs/plan/*`, `docs/progress.md`.
- Satu milestone aktif; AI mengerjakan satu slice, menjalankan gate, mencatat progress, lapor, menunggu approval.
- Keputusan arsitektur baru dicatat di `docs/decisions.md`.
- Android Gate: `./gradlew assembleDebug testDebugUnitTest lint`.

## 13. Struktur repo
```
football-predictor-api/        # backend (repo ini)
football-predictor-android/    # Android Studio
  └── ml/ (atau repo football-predictor-ml)   # notebook/skrip Python + dataset + model
```

## 14. Risiko dan mitigasi
| Risiko | Mitigasi |
|---|---|
| Kuota gratis API-Football habis | Fase 1 pakai mock; Fase 2 cache di DB + sinkron mingguan |
| Akurasi model rendah | Bandingkan dengan baseline Poisson; jelaskan batasan di README |
| Format input/output TFLite tidak cocok | Kontrak §8.2 dikunci; test integrasi di A4 |
| Emulator tidak bisa akses `localhost` | Pakai `10.0.2.2` (lihat `setup.md`) |
| Scope melebar | Out of scope §3 + satu milestone aktif |
| AI mengubah hal di luar tugas | `AGENTS.md` + gate + approval per slice |

## 15. Checklist portofolio
- [ ] README: deskripsi, screenshot/GIF, diagram arsitektur (Eraser), cara menjalankan
- [ ] Diagram alur data dan layering
- [ ] Penjelasan keputusan (`decisions.md`)
- [ ] Test hijau + badge CI (GitHub Actions) **[opsional]**
- [ ] Disclaimer prediksi
- [ ] Release APK di GitHub

## 16. Glosarium
| Istilah | Arti |
|---|---|
| EPL | English Premier League |
| Attack/Defense strength | Kekuatan serangan/pertahanan relatif terhadap rata-rata liga |
| TFLite | Format model ringan TensorFlow untuk perangkat mobile |
| MVVM | Model-View-ViewModel |
| Clean Architecture | Pemisahan layer domain/data/presentation dengan dependency ke arah domain |
| DTO | Data Transfer Object: bentuk data dari/ke API |
| Use case | Satu aksi bisnis, mis. mendapatkan prediksi pertandingan |
| Vertical slice | Membangun fitur tipis dari ujung ke ujung dulu (mock -> nyata) |
| ORM | Penerjemah objek Kotlin <-> tabel SQL (Exposed) |
| Baseline | Model sederhana sebagai pembanding (Poisson) |

# Progress & Changelog

**[Instruksi untuk AI]** Setiap selesai satu tugas, tambahkan entri di **bagian atas** daftar (terbaru dulu) dengan format:

```
### YYYY-MM-DD — [Mx.y] Judul singkat
- Yang diubah: <file-file>
- Ringkasan: <1-3 kalimat>
- Gate: build ✅/❌ · test ✅/❌
- Catatan/keputusan: <opsional>
```

---

### 2026-10-30 — [M1e] Suite pengujian otomatis (ServerTest, TeamsApiTest, MockTeamRepositoryTest)
- Yang diubah: test/ServerTest.kt, test/TeamsApiTest.kt, test/MockTeamRepositoryTest.kt, src/Routing.kt, docs/plan/m1-mock-api.md
- Ringkasan: Menambahkan suite pengujian komprehensif (14 test) mencakup root endpoint, routing /api/teams (happy path, jumlah >= 6 tim, validasi nilai metrik), pemetaan HTTP 404/500, serta pengujian ketat validasi integritas data MockTeamRepository.
- Gate: build ✅ · test ✅ (14 passed)
- Catatan/keputusan: Dikerjakan manual oleh pengguna, diverifikasi AI. Slice M1f ditunda sampai aplikasi Android siap.

### 2026-10-30 — [M1d] TeamService dan endpoint GET /api/teams
- Yang diubah: src/service/TeamService.kt, src/Routing.kt, src/repository/TeamRepository.kt, src/repository/MockTeamRepository.kt, docs/plan/m1-mock-api.md
- Ringkasan: Menambahkan TeamService dan endpoint GET /api/teams dengan respons JSON serta penanganan status HTTP 404 (FileNotFoundException) dan 500 (Exception) berformat JSON ErrorResponse, serta memperbarui respons GET / menjadi "Football Predictor API is running!".
- Gate: build ✅ · test ✅
- Catatan/keputusan: Dikerjakan manual oleh pengguna, diverifikasi AI.

### 2026-10-30 — [M1c] TeamRepository dan MockTeamRepository
- Yang diubah: src/repository/TeamRepository.kt, src/repository/MockTeamRepository.kt, docs/plan/m1-mock-api.md
- Ringkasan: Membuat interface TeamRepository dan implementasi MockTeamRepository yang memuat data dari mock_epl_stats.json di classpath menggunakan parser Json ketat dan memvalidasi keunikan id serta nilai attack/defense strength >= 0.
- Gate: build ✅ · test ✅
- Catatan/keputusan: MockTeamRepository mendukung parsing instance Json ketat (tanpa isLenient) dan melempar IllegalArgumentException dengan id tim bila terjadi duplikasi atau nilai negatif, serta FileNotFoundException jika resource tidak ditemukan.

### 2026-10-29 — [chore] rename package
- Yang diubah: src/main.kt, src/Routing.kt, src/Serialization.kt, src/model/Team.kt, test/ServerTest.kt, resources/application.yaml
- Ringkasan: Mengubah package dari com.example menjadi com.portfolio.footballpredictor di seluruh file Kotlin dan konfigurasi application.yaml.
- Gate: build ✅ · test ✅
- Catatan/keputusan: Server dijalankan dengan kotlin.bat run dan endpoint http://localhost:8080/ terbukti merespons status 200 "Hello, World!".

### 2026-10-29 — [M1b] Team data class dan konfigurasi ContentNegotiation JSON
- Yang diubah: src/model/Team.kt, src/Serialization.kt, docs/plan/m1-mock-api.md
- Ringkasan: Membuat data class Team dengan anotasi @Serializable sesuai kontrak build-spec §5 dan memastikan plugin ContentNegotiation terkonfigurasi dengan opsi kotlinx.serialization JSON.
- Gate: build ✅ · test ✅
- Catatan/keputusan: Struktur model diletakkan pada package com.example.model mengikuti target project-structure.md.

### 2026-10-29 — [M1a] Pembuatan mock EPL stats JSON
- Yang diubah: resources/mock_epl_stats.json, docs/plan/m1-mock-api.md
- Ringkasan: Menambahkan file mock_epl_stats.json berisi 6 tim EPL sesuai kontrak build-spec §5 dengan kode 3 huruf unik dan metrik attack/defense strength dalam rentang wajar (0.4 - 2.0).
- Gate: build ✅ · test ✅
- Catatan/keputusan: Normalisasi line endings kotlin.bat ke CRLF agar kompatibel dengan command prompt Windows.

### 2026-10-29 — [M0] Inisialisasi proyek
- Yang diubah: struktur Ktor (main.kt, Routing.kt, Serialization.kt), AGENTS.md, docs/*
- Ringkasan: Proyek `football-predictor-api` dibuat dengan Ktor + Netty + build system Amper. Dokumentasi pengendali AI ditambahkan.
- Gate: build ✅ · test ✅ (isi sesuai kondisi nyata)

# M1 — MVP Lokal (Mock API)

Spec: `build-spec.md` §5. Status: **In Progress**

Tujuan: `GET /api/teams` mengembalikan data dari `mock_epl_stats.json`, siap ditarik Retrofit di Android.
Tidak boleh: database, internet, API eksternal.

| Slice | Isi | Status |
|---|---|---|
| M1a | `resources/mock_epl_stats.json` — minimal 6 tim EPL (id, name, attackStrength, defenseStrength) | [x] |
| M1b | `Team` data class `@Serializable` + konfigurasi ContentNegotiation JSON | [ ] |
| M1c | `TeamRepository` (interface) + `MockTeamRepository` yang membaca JSON dari resources | [ ] |
| M1d | `TeamService` + route `GET /api/teams` (200 JSON, 404/500 sesuai kontrak) | [ ] |
| M1e | Test: happy path `/api/teams` (jumlah & field), error saat file hilang | [ ] |
| M1f | Uji dari Android Emulator (`http://10.0.2.2:8080/api/teams`) lalu dokumentasikan di `setup.md` | [ ] |

## Aturan khusus M1
- Validasi data: `attackStrength` dan `defenseStrength` >= 0, `id` unik. Data mock yang melanggar harus ditolak saat dimuat.
- Route hanya memanggil `TeamService`; membaca file hanya di repository.
- Baca JSON dengan `kotlinx.serialization` (decode ke `List<Team>`), jangan kirim string mentah.

## Acceptance
1. `kotlin build` & `kotlin test` hijau (Windows: `kotlin.bat`).
2. `curl http://localhost:8080/api/teams` menampilkan array tim sesuai kontrak.
3. `progress.md` terisi untuk setiap slice.
4. Pengguna menyatakan **Approved** -> status menjadi Done, M2 dibuka.

## Instruksi untuk AI sekarang
Mulai dari **M1a**. Kerjakan satu slice, jalankan gate, catat di `progress.md`, lapor, tunggu YES.

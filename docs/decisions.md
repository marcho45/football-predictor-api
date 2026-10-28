# Decision Log (ADR ringkas)

Format: ID · tanggal · keputusan · alasan. Tambahkan di bawah, jangan hapus entri lama.

| ID | Tanggal | Keputusan | Alasan |
|---|---|---|---|
| D1 | 2026-10-29 | Tidak memakai Firebase/Supabase | Tujuan proyek: menguasai backend Kotlin end-to-end (routing, ORM, scheduler) |
| D2 | 2026-10-29 | Ktor 3.x + Netty + build system Amper (hasil Ktor Project Generator) | Satu bahasa (Kotlin) untuk client & server |
| D3 | 2026-10-29 | REST, bukan WebSocket | Data statistik diminta sesekali, tidak butuh koneksi dua arah permanen |
| D4 | 2026-10-29 | ML (TFLite) berjalan di Android | Prediksi luring, server tetap ringan |
| D5 | 2026-10-29 | Dua fase: mock JSON dulu, DB kemudian (vertical slice) | Hemat kuota API-Football saat mengembangkan UI & ML |
| D6 | 2026-10-29 | Repository interface sejak M1 | Pergantian mock -> PostgreSQL tanpa mengubah route |

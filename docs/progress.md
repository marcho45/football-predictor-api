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

### 2026-10-29 — [M1a] Pembuatan mock EPL stats JSON
- Yang diubah: resources/mock_epl_stats.json, docs/plan/m1-mock-api.md
- Ringkasan: Menambahkan file mock_epl_stats.json berisi 6 tim EPL sesuai kontrak build-spec §5 dengan kode 3 huruf unik dan metrik attack/defense strength dalam rentang wajar (0.4 - 2.0).
- Gate: build ✅ · test ✅
- Catatan/keputusan: Normalisasi line endings kotlin.bat ke CRLF agar kompatibel dengan command prompt Windows.

### 2026-10-29 — [M0] Inisialisasi proyek
- Yang diubah: struktur Ktor (main.kt, Routing.kt, Serialization.kt), AGENTS.md, docs/*
- Ringkasan: Proyek `football-predictor-api` dibuat dengan Ktor + Netty + build system Amper. Dokumentasi pengendali AI ditambahkan.
- Gate: build ✅ · test ✅ (isi sesuai kondisi nyata)

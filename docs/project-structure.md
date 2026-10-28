# Struktur Proyek (kondisi nyata + target)

Struktur di bawah mengikuti proyek hasil generator Ktor kamu saat ini. Jika letak file berbeda, sesuaikan dokumen ini, lalu catat di `progress.md`.

```
football-predictor-api/
├── AGENTS.md                  # aturan AI (Antigravity); salin ke .clinerules bila pakai Cline
├── .clinerules                # (opsional) aturan untuk Cline/Roo
├── README.md
├── module.yaml                # konfigurasi modul & dependency (Amper)
├── libs.versions.toml         # katalog versi library
├── kotlin / kotlin.bat        # wrapper Amper (jalankan build/run/test)
├── docs/
│   ├── 00-project-overview.md # gambaran seluruh proyek (ML+backend+Android)
│   ├── build-spec.md          # sumber kebenaran backend
│   ├── architecture.md
│   ├── decisions.md
│   ├── roadmap.md
│   ├── setup.md
│   ├── project-structure.md   # file ini
│   ├── progress.md            # changelog yang diisi AI
│   ├── problems.md            # bug/masalah terbuka
│   └── plan/
│       ├── m0-bootstrap.md
│       ├── m1-mock-api.md
│       ├── m2-database.md
│       └── m3-sync-job.md
├── resources/
│   ├── application.yaml       # port, host, modul
│   ├── logback.xml
│   └── mock_epl_stats.json    # dibuat di M1 (data mock)
├── src/
│   ├── main.kt                # entry point Netty
│   ├── Routing.kt             # endpoint
│   └── Serialization.kt       # ContentNegotiation + JSON
└── test/
    └── ServerTest.kt
```

## Target setelah M1 (tambahan file, bukan pemindahan besar)
```
src/
├── model/Team.kt                      # @Serializable data class
├── repository/TeamRepository.kt       # interface
├── repository/MockTeamRepository.kt   # baca mock_epl_stats.json
└── service/TeamService.kt
```
(Jika proyek memakai `src/main/kotlin/<package>/`, ikuti struktur itu dan perbarui dokumen ini.)

## Aturan penempatan
- File Kotlin baru -> `src/` sesuai layer. Aset statis & config -> `resources/`.
- Dokumentasi -> `docs/`. Jangan taruh dokumen di root selain `README.md` dan `AGENTS.md`.

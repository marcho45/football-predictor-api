# Arsitektur Sistem

## Gambaran besar
Server hanya **menyuplai data**. Logika AI (TFLite) berjalan di Android. Keduanya dipisah tegas.

```
[Android: Compose UI] -> [ViewModel] -> [Retrofit] --GET /api/teams--> [Ktor]
                              |                                            |
                              v                                  Fase 1: mock_epl_stats.json
                        [TFLite Model]                           Fase 2: PostgreSQL (Exposed)
                              |                                            ^
                              v                                            |
                      probabilitas & skor                     Fase 2: Cron Job <- API-Football
```

## Fase 1 — MVP Lokal (M1)
Android -> Ktor -> baca `mock_epl_stats.json` -> JSON -> Android. Tidak ada internet, tidak ada database.

## Fase 2 — Produksi (M2, M3)
1. Cron Job mingguan memanggil API-Football (RapidAPI).
2. Data disimpan/diperbarui lewat Exposed ke PostgreSQL.
3. `GET /api/teams` membaca dari database, bukan file.

## Layering backend (target mulai M1, rapi sejak awal)
| Layer | Tanggung jawab | Contoh |
|---|---|---|
| Route | HTTP saja: terima request, panggil service, kirim respons | `Routing.kt` |
| Service | Aturan bisnis | `TeamService` |
| Repository | Sumber data (interface) | `TeamRepository` -> `MockTeamRepository` (M1), `DbTeamRepository` (M2) |
| Model | Data class `@Serializable` | `Team` |

Tujuan: saat M2, cukup mengganti implementasi repository; route dan kontrak API tidak berubah.

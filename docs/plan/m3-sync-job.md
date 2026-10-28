# M3 — Sinkronisasi Terjadwal API-Football

Status: **Pending** — JANGAN dikerjakan sebelum M2 Approved.

Rencana kasar:
- Ktor Client memanggil API-Football (RapidAPI) memakai `RAPIDAPI_KEY` dari env
- Hitung `attackStrength` / `defenseStrength` dari statistik nyata (rumus didokumentasikan di `decisions.md`)
- Scheduler mingguan -> upsert ke PostgreSQL via Exposed
- Penanganan batas kuota, retry, dan logging

Acceptance (draft): data tim di DB terupdate otomatis tiap minggu tanpa campur tangan manual.

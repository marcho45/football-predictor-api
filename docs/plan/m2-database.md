# M2 — PostgreSQL + Exposed

Status: **Pending** — JANGAN dikerjakan sebelum M1 Approved dan file ini dilengkapi bersama pengguna.

Rencana kasar (akan dirinci saat dibuka):
- Koneksi PostgreSQL (env var), Exposed, migrasi skema
- Tabel `teams` (id, name, attack_strength, defense_strength, updated_at)
- `DbTeamRepository` menggantikan `MockTeamRepository`
- Seed awal dari `mock_epl_stats.json`
- Test repository (database test / Testcontainers atau H2 sesuai keputusan di `decisions.md`)

Acceptance (draft): `GET /api/teams` identik dengan kontrak M1 namun datanya dari database.

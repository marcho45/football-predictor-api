# AGENTS.md — Aturan Kerja AI (football-predictor-api)

> File ini dibaca otomatis oleh Antigravity (dan agent lain seperti Cline/Roo/Claude Code).
> Jika memakai Cline/Roo, salin isi file ini ke `.clinerules`.

## 0. Wajib baca sebelum menulis kode (urutan)
1. `docs/00-project-overview.md` — gambaran seluruh proyek (ML + backend + Android).
2. `docs/build-spec.md` — sumber kebenaran proyek (scope, kontrak API, "Jangan dilakukan").
3. `../../../Users/paulu/Downloads/backend-docs/backend-docs/docs/plan` — buka file milestone yang berstatus **In Progress**. Hanya itu yang boleh dikerjakan.
4. `docs/project-structure.md` — sebelum membuat/memindah file.
5. `docs/progress.md` — lihat apa yang sudah dikerjakan terakhir.

## 1. Alur kerja per tugas
1. Baca tugas di file milestone aktif. Kerjakan **satu slice/tugas saja**.
2. Jalankan gate dengan Amper: `kotlin build` lalu `kotlin test` (Windows: `kotlin.bat build`, `kotlin.bat test`; macOS/Linux: `./kotlin ...`). Harus hijau.
3. Catat di `docs/progress.md` (format di file itu) **SEBELUM** melapor.
4. Update status checkbox di file milestone.
5. Lapor ringkas ke pengguna: file yang berubah, hasil gate, lalu **berhenti dan tunggu YES/NO**.
6. Jangan lompat ke slice/milestone berikutnya tanpa persetujuan.

## 2. Aturan kode
- Stack: Kotlin, Ktor 3.x (Netty), build system **JetBrains Amper** (`module.yaml`, bukan Gradle), kotlinx.serialization.
- Pisahkan: routing (HTTP) · logika bisnis (service) · akses data (repository). Route tidak boleh berisi logika berat.
- Semua respons JSON memakai `@Serializable` data class, bukan string mentah.
- Tidak boleh hardcode API key/kredensial. Pakai environment variable atau `application.yaml` dengan `${ENV_VAR}`.
- Komentar singkat berbahasa Indonesia pada fungsi publik. Nama kode (class/fungsi/variabel) berbahasa Inggris.
- Jangan menambah dependency tanpa izin; sebutkan alasannya di laporan. Dependency ditambahkan di `module.yaml` (versi di `libs.versions.toml`), bukan `build.gradle.kts`.
- Ubah seminimal mungkin. Jangan refactor file yang tidak terkait tugas.

## 3. Jangan dilakukan
- Jangan mengerjakan milestone berstatus **Pending**.
- Jangan mengubah kontrak API (`docs/build-spec.md` §5) tanpa persetujuan — Android bergantung padanya.
- Jangan menyentuh database/PostgreSQL/Exposed sebelum M2 dibuka.
- Jangan commit file `.env` atau secret.
- Jangan menghapus atau menulis ulang dokumen di `../../../Users/paulu/Downloads/backend-docs/backend-docs/docs` selain yang diminta tugas.
- Jangan menebak: jika spesifikasi ambigu, **tanya pengguna**.

## 4. Konvensi commit
`feat(m1): ...` · `fix(m1): ...` · `docs: ...` · `chore: ...` · `test(m1): ...`

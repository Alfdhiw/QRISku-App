# Qrisku App — v0.2

Aplikasi Native Android (Kotlin + Jetpack Compose) untuk mendeteksi notifikasi pembayaran DANA Bisnis, mencatat nominal secara lokal, dan membacakannya memakai Text-to-Speech Bahasa Indonesia offline.

## Menjalankan aplikasi

Buka folder project ini di Android Studio, lalu sync Gradle. Project memakai AGP 9.2.0, Kotlin Compose 2.2.10, Gradle Wrapper 9.4.1, compile SDK 37, target SDK 36, dan minimum Android 8 (API 26). Lokasi SDK dikonfigurasi di `local.properties` yang tidak masuk Git.

```powershell
.\gradlew.bat :app:assembleDebug
```

APK debug: `app/build/outputs/apk/debug/app-debug.apk`.

Ikuti pengenalan → akses notifikasi Android → Uji Suara → Selesai. **Nanti Saja** membuka Beranda dengan konfigurasi belum aktif. Bacakan Otomatis diaktifkan setelah izin diberikan dan contoh selesai dibacakan; kontrolnya tersedia di Beranda dan Pengaturan.

## Layar dan state

- **Beranda:** izin belum diberikan, listener belum terhubung, pemantauan aktif, atau suara nonaktif. Notifikasi terakhir berasal dari penyimpanan.
- **Riwayat Notifikasi:** terbaru di atas, dimuat per 100 item; detail, Putar Ulang Suara, dan Buka DANA.
- **Pengaturan:** suara otomatis, kecepatan Lambat/Normal/Cepat, volume media perangkat, uji suara, izin, bantuan baterai, dan hapus riwayat dengan konfirmasi.
- **TTS:** menunggu, sedang dibacakan, selesai, gagal, atau terhenti. **Sudah dibacakan** hanya dicatat setelah callback selesai Android.
- **Error:** petunjuk jika voice Indonesia offline tidak tersedia, inisialisasi gagal, antrean penuh, atau penyimpanan tidak dapat diakses.

Qrisku membacakan informasi notifikasi. Aplikasi tidak memproses pembayaran atau memverifikasi settlement dana secara independen; pengguna tetap memeriksa DANA untuk konfirmasi transaksi.

## Sumber dan penyimpanan

Format awal yang diterima tetap ketat:

- Package asli: `id.dana`.
- Judul: `Pembayaran Masuk`.
- Isi: `Rp29.000 diterima DANA Bisnis.`.

Transfer, format lain, nominal nol, aplikasi lain, dan group summary diabaikan. SQLite menyimpan nominal, waktu deteksi/posting, package sumber, status suara, serta hash identitas event untuk deduplikasi. Raw notification key, isi mentah, judul, dan identitas pembayar tidak disimpan. Dua event berbeda dengan nominal sama tetap terpisah.

Preferensi suara dan penyelesaian onboarding disimpan di SharedPreferences. Listener dan UI memakai satu `AppController`/`VoiceEngine` per proses. Menutup Activity tidak mematikan suara listener. Setelah proses dimulai ulang, ucapan yang masih antre/berjalan ditandai **Pembacaan terhenti**, tanpa replay otomatis.

Riwayat disimpan sampai dihapus pengguna. Retensi 30 hari dalam PRD masih berupa usulan dan belum diaktifkan. Cache teknis deduplikasi dibatasi 4.096 event dan dipertahankan setelah hapus riwayat. Callback terlambat tidak bisa membuat kembali record yang dihapus. Backup cloud dan transfer data Android dikecualikan.

Tidak ada backend atau permission INTERNET. Engine hanya memilih voice Indonesia yang tersedia lokal dan tidak membutuhkan jaringan. Pasang paket suara lewat pengaturan TTS perangkat jika belum tersedia.

## Acuan implementasi

[Penyelarasan PRD dan UI](docs/design/qrisku-prd-alignment.md) menetapkan token, konten, dan pemetaan state. [Katalog Stitch](docs/design/stitch/15423157619100262672/README.md) menyimpan referensi aslinya.

[Laporan verifikasi v0.2](docs/design/verification/2026-10-09/README.md) berisi hasil 23 tests, lint, smoke test native UI, dan batas pengujian.

Struktur: `QriskuApplication` → `AppController` → repository SQLite/preferensi + engine suara; layar Compose ada di `ui/`. Parser dan pelafalan tetap memakai aturan PoC.

## Pengujian

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

Instrumented tests memakai database serta preferensi khusus. Jalankan hanya di emulator/perangkat uji: Gradle dapat memasang ulang atau menghapus APK setelah pengujian, sehingga data aplikasi pada perangkat itu dapat hilang. Jangan gunakan perangkat harian yang memiliki riwayat penting.

Smoke test tanpa Android SDK, bila `kotlinc` tersedia:

```sh
kotlinc app/src/main/java/id/qrisku/app/PaymentParser.kt app/src/main/java/id/qrisku/app/IndonesianNumberWords.kt app/src/main/java/id/qrisku/app/data/AppModels.kt tests/ParserSmokeTest.kt -include-runtime -d parser-tests.jar
java -jar parser-tests.jar
```

Jika sertifikat repositori sudah dipercaya Windows tetapi Java lokal gagal memvalidasinya, gunakan trust store Windows untuk sesi build dengan validasi TLS tetap aktif:

```powershell
.\gradlew.bat "-Djavax.net.ssl.trustStoreType=Windows-ROOT" "-Djavax.net.ssl.trustStore=NONE" :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

## Validasi perangkat nyata

Uji akses ditolak/dicabut, dua pembayaran nominal sama, update notifikasi, layar mati, app tidak terbuka, volume nol, TTS offline tidak tersedia, dan suara otomatis dimatikan saat ada antrean. Notifikasi yang tidak diterima ketika listener terputus tidak dikejar ulang.

Jangan memasukkan PIN, OTP, kredensial, atau isi notifikasi aplikasi lain ke Logcat maupun laporan masalah. Pengujian emulator memakai event sintetis dan tidak membuktikan keandalan notifikasi DANA pada perangkat pedagang.

# Qrisku App — v0.2.1

Aplikasi Native Android (Kotlin + Jetpack Compose) untuk mendeteksi notifikasi pembayaran DANA Bisnis, mencatat nominal secara lokal, dan membacakannya memakai Text-to-Speech Bahasa Indonesia offline.

## Menjalankan aplikasi

Clone repository, lalu pilih **Open** di Android Studio dan buka folder root yang berisi `settings.gradle.kts`. Tidak perlu membuat project Empty Activity baru. Repository sudah menyertakan Gradle Wrapper (`gradlew`, `gradlew.bat`, dan `gradle/wrapper/`) dengan versi Gradle yang ditetapkan untuk proyek ini.

| Komponen | Versi |
| --- | --- |
| Android Gradle Plugin (AGP) | 9.2.0 |
| Gradle Wrapper | 9.4.1 |
| JDK untuk Gradle | 17 atau versi kompatibel yang disertakan Android Studio |
| Compile SDK | Android API 37 |
| Target SDK | Android API 36 |
| Minimum Android | Android 8 / API 26 |
| SDK Build Tools | 36.0.0 |
| Kotlin Compose plugin | 2.2.10 |
| Compose BOM | 2026.09.00 |

AGP 9.2.0 memerlukan Gradle minimal 9.4.1. Lihat [kompatibilitas resmi AGP](https://developer.android.com/build/releases/agp-9-2-0-release-notes). Wrapper menetapkan versi tersebut dan memverifikasi checksum unduhan Gradle.

JAR wrapper juga memakai versi 9.4.1; checksum-nya dapat dibandingkan dengan [checksum resmi Gradle](https://gradle.org/release-checksums/). Perbarui JAR, kedua script launcher, dan properties sebagai satu paket saat mengganti versi wrapper.

Di **Settings → Build, Execution, Deployment → Build Tools → Gradle**, pilih distribusi Gradle dari **Wrapper** jika pilihan tersebut tersedia. Pilih **Gradle JDK** yang kompatibel (JDK 17 atau JDK bawaan Android Studio yang didukung Gradle 9.4.1). Instal Android SDK Platform 37 dan SDK Build Tools 36.0.0 melalui SDK Manager, lalu pilih **File → Sync Project with Gradle Files**. Sinkronisasi pertama memerlukan koneksi internet untuk mengunduh Gradle dan dependencies.

Untuk memeriksa versi dan membuat APK debug dari terminal di root repository:

```powershell
.\gradlew.bat --version
.\gradlew.bat :app:assembleDebug
```

Di Linux/macOS, gunakan `./gradlew --version` dan `./gradlew :app:assembleDebug`. Terminal memerlukan `JAVA_HOME` atau `java` pada `PATH`; lokasi SDK dapat ditetapkan melalui `local.properties` yang dibuat Android Studio. APK debug berada di `app/build/outputs/apk/debug/app-debug.apk` setelah build berhasil.

### Jika sebelumnya muncul error Gradle 9.3.0

Ambil perubahan terbaru dari branch `master`. Pastikan `gradle/wrapper/gradle-wrapper.properties` berisi:

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-9.4.1-bin.zip
distributionSha256Sum=2ab2958f2a1e51120c326cad6f385153bb11ee93b3c216c5fccebfdfbb7ec6cb
```

Kemudian lakukan **Sync Project with Gradle Files**. Jika mengedit wrapper lokal yang sudah ada, sesuaikan juga `distributionSha256Sum` bila properti tersebut tersedia agar checksum tidak masih mengacu pada Gradle 9.3.0.

### Pengaturan pertama

APK debug: `app/build/outputs/apk/debug/app-debug.apk`.

Ikuti pengenalan → akses notifikasi Android → Uji Suara → Selesai. **Nanti Saja** membuka Beranda dengan konfigurasi belum aktif. Bacakan Otomatis diaktifkan setelah izin diberikan dan contoh selesai dibacakan; kontrolnya tersedia di Beranda dan Pengaturan.

### Jika akses notifikasi diblokir Android

Pesan **Aplikasi tidak diberi akses** / **Dikontrol oleh Setelan Terbatas** adalah perlindungan Android 13+, bukan permission runtime yang dapat diberikan otomatis oleh QRISku. Lanjutkan hanya jika Anda percaya sumber APK. Lihat [panduan resmi setelan terbatas Android](https://support.google.com/android/answer/12623953?hl=id).

1. Tekan **Akses diblokir Android?** pada onboarding/Beranda/Pengaturan, atau **Bantuan → Panduan Akses Notifikasi**.
2. Pilih **Buka Info Aplikasi → ⋮/Lainnya → Izinkan setelan terbatas**. Selesaikan persetujuan Android jika diminta.
3. **Xiaomi/HyperOS:** jika Info Aplikasi biasa hanya menampilkan “Info apl” dan “Berbagi”, gunakan **Daftar Aplikasi Android → Qrisku App → Lainnya → Izinkan setelan terbatas**. Jalur ini diverifikasi pada Xiaomi Android 15; pada perangkat lain nama/menu bisa berbeda.
4. Kembali ke QRISku, pilih **Aktifkan Akses/Periksa Akses Notifikasi**, lalu izinkan akses untuk QRISku melalui layar Android.
5. Kembali dan pastikan **Pemantauan aktif**, lalu Uji Suara. Aktifkan **Bacakan Otomatis** jika ingin pembayaran dibacakan; pembaruan APK tidak mengubah pilihan suara sebelumnya.

Tidak perlu mematikan Play Protect, memberi izin lewat ADB, atau menghapus/memasang ulang aplikasi. Perangkat yang dikelola organisasi mungkin memerlukan persetujuan administrator. QRISku tidak mendeteksi status setelan terbatas lewat API tersembunyi dan tidak mengklaim pemantauan aktif sebelum izin serta koneksi listener benar-benar tersedia.

### Jika izin aktif tetapi pemantauan perlu diperiksa

QRISku meminta Android menyambungkan ulang listener yang sudah diizinkan, saat aplikasi dibuka atau layanan terputus, dengan jeda retry 30 detik. Ini tidak memberikan izin baru atau menjamin vendor selalu mengizinkan background service. Status aktif tetap menunggu callback koneksi.

Pada Xiaomi/HyperOS, buka **Setelan → Aplikasi → Mulai otomatis di latar belakang** (sebagian versi melalui **Perizinan → Mulai otomatis**) dan izinkan **QRISku saja**. Tanpa pengaturan ini, perangkat uji menolak bind ulang setelah pembaruan APK dengan pesan `AutStart Unable to bind notification listener service`. Mulai Otomatis dapat menambah penggunaan baterai; lihat [panduan resmi Xiaomi](https://www.mi.com/global/support/faq/details/KA-497677/). Nama/letak menu dapat berbeda. Beranda dan Pengaturan menyediakan petunjuk saat izin sudah ada tetapi layanan belum terhubung.

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

Untuk `BigTextStyle`, teks lengkap yang tidak kosong diprioritaskan daripada preview pendek/kosong; parser tetap memakai pola ketat yang sama. Ketukan manual berulang pada Uji Suara/Putar Ulang ditolak di antrean yang sedang sibuk, tetapi pembayaran otomatis tetap dapat mengantre.

Preferensi suara dan penyelesaian onboarding disimpan di SharedPreferences. Listener dan UI memakai satu `AppController`/`VoiceEngine` per proses. Menutup Activity tidak mematikan suara listener. Setelah proses dimulai ulang, ucapan yang masih antre/berjalan ditandai **Pembacaan terhenti**, tanpa replay otomatis.

Riwayat disimpan sampai dihapus pengguna. Retensi 30 hari dalam PRD masih berupa usulan dan belum diaktifkan. Cache teknis deduplikasi dibatasi 4.096 event dan dipertahankan setelah hapus riwayat. Callback terlambat tidak bisa membuat kembali record yang dihapus. Backup cloud dan transfer data Android dikecualikan.

Tidak ada backend atau permission INTERNET. Engine hanya memilih voice Indonesia yang tersedia lokal dan tidak membutuhkan jaringan. Pasang paket suara lewat pengaturan TTS perangkat jika belum tersedia.

`BIND_NOTIFICATION_LISTENER_SERVICE` adalah izin layanan khusus melalui Settings Android, bukan dialog runtime `POST_NOTIFICATIONS`.

## Kredensial dan signing

Jangan commit private key, keystore signing, token, password, atau data notifikasi pengguna. `.gitignore` mengabaikan file environment, konfigurasi signing lokal, private key/keystore beserta backup, file kredensial umum, APK/AAB, dan log. Contoh `.env.example`, `.env.sample`, dan `.env.template` boleh dilacak **hanya dengan placeholder**, bukan nilai rahasia. Gunakan direktori `secrets/` yang diabaikan untuk file rahasia dengan nama lain.

Simpan konfigurasi signing lokal di `keystore.properties` atau `signing.properties` dan password di environment/Gradle user home, bukan `gradle.properties` repository atau `build.gradle.kts`. Aturan ignore tidak menambahkan konfigurasi release signing ke aplikasi; jika diperlukan, integrasi tersebut harus dibuat terpisah. `gradle.properties`, Gradle Wrapper, dan JSON referensi desain tetap dilacak agar clone tetap dapat dibangun. Jangan memasukkan kredensial server ke source/resources Android: isi APK dapat diperiksa meskipun file asalnya diabaikan Git.

Sebelum commit, periksa `git status --short`, `git diff --cached --name-only`, dan `git ls-files -ci --exclude-standard`. Perintah terakhir menunjukkan file yang sudah dilacak tetapi sekarang cocok dengan aturan ignore. Gunakan `git check-ignore -v --no-index -- path/ke/file` untuk memeriksa aturan yang berlaku; jangan gunakan `git add -f` untuk file rahasia.

`.gitignore` tidak memeriksa isi file dan tidak menghapus file yang sudah dilacak maupun riwayat commit; lihat [dokumentasi Git](https://git-scm.com/docs/gitignore). Jika kredensial pernah ter-commit, cabut/rotasi dahulu, lalu koordinasikan penghapusan dari index dan riwayat. Menghapus file lokal atau menambahkan aturan ignore saja tidak cukup. Secret scanning dan push protection perlu disiapkan terpisah sebagai lapisan perlindungan tambahan.

## Acuan implementasi

[Penyelarasan PRD dan UI](docs/design/qrisku-prd-alignment.md) menetapkan token, konten, dan pemetaan state. [Katalog Stitch](docs/design/stitch/15423157619100262672/README.md) menyimpan referensi aslinya.

[Laporan verifikasi v0.2](docs/design/verification/2026-10-09/README.md) berisi hasil 23 tests, lint, smoke test native UI, dan batas pengujian.

[Laporan perbaikan v0.2.1](docs/design/verification/2026-10-11/README.md) mencatat diagnosis setelan terbatas/autostart HyperOS, pemulihan melalui persetujuan Android, 42 tests, serta batas pengujian DANA nyata.

Struktur: `QriskuApplication` → `AppController` → repository SQLite/preferensi + engine suara; layar Compose ada di `ui/`. Parser dan pelafalan tetap memakai aturan PoC.

## Pengujian

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

Instrumented tests memakai database serta preferensi khusus. Jalankan hanya di emulator/perangkat uji: Gradle dapat memasang ulang atau menghapus APK setelah pengujian, sehingga data aplikasi pada perangkat itu dapat hilang. Jangan gunakan perangkat harian yang memiliki riwayat penting.

Untuk HP USB milik pengguna, build test APK tanpa menjalankan `connectedDebugAndroidTest`, pastikan sertifikat APK sama dengan aplikasi terpasang, lalu pasang sebagai **update**, bukan uninstall:

```powershell
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest
adb -s SERIAL install -r app/build/outputs/apk/debug/app-debug.apk
adb -s SERIAL install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb -s SERIAL shell am instrument -w id.qrisku.app.test/androidx.test.runner.AndroidJUnitRunner
```

Ganti `SERIAL` dengan perangkat yang disetujui pemiliknya. Test repository/controller memakai namespace database/preferensi UUID, bukan data pengguna. Jangan mengatasi signature mismatch dengan uninstall. Pengujian langsung ini tidak menghapus APK utama setelah selesai; test APK dapat dihapus terpisah (`id.qrisku.app.test`, bukan `id.qrisku.app`).

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

1. Jalankan aplikasi di perangkat uji yang memiliki DANA dengan DANA Bisnis aktif.
2. Aktifkan akses notifikasi melalui Qrisku, lalu kembali dan periksa status pemantauan.
3. Tekan **Uji Suara** untuk mengecek voice Bahasa Indonesia offline, volume media, dan output audio.
4. Pada pengujian pembayaran yang disetujui pemilik akun, amati suara, **Notifikasi terakhir**, serta **Riwayat Notifikasi**. Konfirmasi status pembayaran di DANA.
5. Untuk pemeriksaan koneksi layanan, gunakan filter Logcat `Qrisku`; jangan mencatat isi notifikasi mentah.

Uji akses ditolak/dicabut, dua pembayaran nominal sama, update notifikasi, layar mati, app tidak terbuka, volume nol, TTS offline tidak tersedia, dan suara otomatis dimatikan saat ada antrean. Notifikasi yang tidak diterima ketika listener terputus tidak dikejar ulang.

Jangan memasukkan PIN, OTP, kredensial, atau isi notifikasi aplikasi lain ke Logcat maupun laporan masalah. Pengujian emulator memakai event sintetis dan tidak membuktikan keandalan notifikasi DANA pada perangkat pedagang.

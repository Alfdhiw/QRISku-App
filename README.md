# Qrisku App — Proof of Concept v0.1

Prototipe Native Android (Kotlin + Jetpack Compose) untuk mendeteksi notifikasi pembayaran DANA Bisnis, mengambil nominal, dan membacakannya menggunakan TextToSpeech. Bukan alat verifikasi finansial.

## Cara menjalankan di Android Studio

Clone repository, lalu pilih **Open** di Android Studio dan buka folder root yang berisi `settings.gradle.kts`. Tidak perlu membuat project Empty Activity baru. Repository sudah menyertakan Gradle Wrapper (`gradlew`, `gradlew.bat`, dan `gradle/wrapper/`) dengan versi Gradle yang ditetapkan untuk proyek ini.

| Komponen | Versi |
| --- | --- |
| Android Gradle Plugin (AGP) | 9.2.0 |
| Gradle Wrapper | 9.4.1 |
| JDK untuk Gradle | 17 atau versi kompatibel yang disertakan Android Studio |
| Compile SDK | Android API 37 |
| SDK Build Tools | 36.0.0 |
| Kotlin Compose plugin | 2.2.10 |
| Compose BOM | 2026.09.00 |

AGP 9.2.0 memerlukan Gradle minimal 9.4.1. Lihat [kompatibilitas resmi AGP](https://developer.android.com/build/releases/agp-9-2-0-release-notes). Wrapper menetapkan versi tersebut dan memverifikasi checksum unduhan Gradle.

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

### Pengujian PoC di perangkat

1. Jalankan aplikasi di smartphone asli yang memiliki aplikasi DANA Bisnis dalam DANA.
2. Buka tombol **Buka Pengaturan Akses Notifikasi**, aktifkan akses Qrisku, lalu kembali.
3. Tekan **Tes Suara** untuk mengecek engine TTS Bahasa Indonesia, volume media, dan pilihan output audio.
4. Biarkan Qrisku terpasang dan layanan notifikasi aktif. Lakukan transaksi QRIS DANA Bisnis sungguhan dengan nominal kecil di perangkat tersebut.
5. Amati suara yang keluar dan baris **Peristiwa terakhir**. Lihat **Logcat** dengan filter `QriskuPoC` untuk log non-sensitif.
6. Ulangi dengan dua pembayaran nominal sama, notifikasi berbeda, layar mati, lalu kondisi app tidak terbuka.

## Format yang diizinkan

- Package: `id.dana`
- Judul: `Pembayaran Masuk`
- Pesan: `Rp29.000 diterima DANA Bisnis.`

Pola hanya mencocokkan sampel yang diberikan. Jika format notifikasi berubah, parser perlu diperbarui. Jangan memperlebar pencocokan tanpa pengujian agar transfer atau notifikasi lain tidak dibacakan sebagai pembayaran sukses.

## Batasan v0.1

- Menggunakan notifikasi Android, bukan webhook/API DANA. Bunyi suara bukan bukti dana pasti sudah masuk.
- Duplikasi dikelola sementara di memori per callback (`key`, `postTime`, `title`, `body`); tidak menjamin tepat-sekali pada pembaruan notifikasi, restart, atau beberapa peristiwa dengan metadata sama.
- Pembayaran saat Notification Listener tidak terhubung dapat terlewat. PoC belum mengejar notifikasi lama supaya tidak membacakan pembayaran lama saat pertama diaktifkan.
- TTS mungkin gagal jika paket suara Bahasa Indonesia tidak tersedia; cek pengaturan Text-to-Speech Android.
- Jangan memasukkan PIN, OTP, data akun, dan isi notifikasi aplikasi lain ke Logcat atau laporan masalah. Kode tidak mengunggah data ke server.
- `BIND_NOTIFICATION_LISTENER_SERVICE` ialah izin layanan khusus melalui Settings, **bukan** dialog runtime `POST_NOTIFICATIONS`.
- Build APK belum diverifikasi di lingkungan perbaikan ini: unduhan distribusi Gradle terhalang akses jaringan dan Android SDK tidak tersedia. Sintaks script wrapper, checksum resmi JAR, integritas JAR, dan kesesuaian versi Gradle/AGP sudah diperiksa.

## Tes logic Kotlin tanpa Android SDK

Dari root paket:

```sh
kotlinc app/src/main/java/id/qrisku/app/PaymentParser.kt app/src/main/java/id/qrisku/app/IndonesianNumberWords.kt app/src/main/java/id/qrisku/app/NotificationDeduplicator.kt tests/ParserSmokeTest.kt -include-runtime -d parser-tests.jar
java -jar parser-tests.jar
```

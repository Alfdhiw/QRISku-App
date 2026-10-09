# Qrisku App — Proof of Concept v0.1

Prototipe Native Android (Kotlin + Jetpack Compose) untuk mendeteksi notifikasi pembayaran DANA Bisnis, mengambil nominal, dan membacakannya menggunakan TextToSpeech. Bukan alat verifikasi finansial.

## Cara menjalankan di Android Studio

**Paling mudah:** Buat project **Empty Activity** baru di Android Studio dengan package `id.qrisku.app`, Kotlin, minimum API 26. Salin folder `app/src/main` dari paket ini ke project tersebut (replace file yang relevan). Pastikan dependencies `activity-compose` dan `material3` tersedia di `app/build.gradle.kts`; gunakan `app/build.gradle.kts` dari paket ini hanya jika konfigurasi Gradle project sesuai.

Paket ini juga berisi file Gradle untuk referensi mandiri (AGP 9.2.0, compile SDK 37, Compose BOM 2026.09.00). **Tidak menyertakan Gradle Wrapper**, jadi bukan project siap-build satu klik dari ZIP. Menggunakan proyek baru yang dibuat oleh Android Studio lebih aman agar wrapper dan toolchain otomatis benar.

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
- Pengujian build APK tidak dilakukan di lingkungan pembuatan berkas ini karena Android SDK/Gradle tidak tersedia.

## Tes logic Kotlin tanpa Android SDK

Dari root paket:

```sh
kotlinc app/src/main/java/id/qrisku/app/PaymentParser.kt app/src/main/java/id/qrisku/app/IndonesianNumberWords.kt app/src/main/java/id/qrisku/app/NotificationDeduplicator.kt tests/ParserSmokeTest.kt -include-runtime -d parser-tests.jar
java -jar parser-tests.jar
```

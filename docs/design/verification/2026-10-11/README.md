# Verifikasi perbaikan QRISku v0.2.1 — 11 Oktober 2026

## Perangkat dan batas pengujian

HP USB milik pengguna: Xiaomi 23021RAA2Y (topaz), Android 15 / API 35, HyperOS V816. APK awal `id.qrisku.app` v0.2.0 (versionCode 2); pembaruan v0.2.1 (versionCode 3), min API 26, target API 36.

Pengguna menyetujui pengujian akses notifikasi melalui persetujuan Android dan, secara terpisah, mengizinkan Mulai Otomatis di Latar Belakang untuk QRISku saja. Tidak memakai perintah untuk memaksa grant/app-op, tidak menonaktifkan Play Protect, tidak uninstall aplikasi utama, tidak menghapus riwayat, tidak membuka akun DANA, dan tidak melakukan pembayaran. Teks notifikasi pribadi tidak diekspor ke laporan atau Logcat.

## Diagnosis dan pemulihan izin

1. Sebelum perbaikan, pemeriksaan read-only app-op QRISku menunjukkan `ACCESS_RESTRICTED_SETTINGS: ignore`. Halaman listener QRISku menampilkan **Dikontrol oleh Setelan Terbatas**; menyentuh izin memunculkan **Aplikasi tidak diberi akses** beserta peringatan info pribadi/keuangan.
2. Deep link Info Aplikasi pada HyperOS membuka halaman Xiaomi; menu Lainnya di sana hanya berisi **Info apl** dan **Berbagi**. Memperbarui APK dengan `install -r` saja tidak menghilangkan pembatasan.
3. Public Settings action `ACTION_APPLICATION_SETTINGS` membuka daftar aplikasi Android yang berbeda. **Qrisku App → Lainnya** pada halaman ini menyediakan **Izinkan setelan terbatas**. Setelah persetujuan pada perangkat, app-op menjadi `allow`.
4. Tombol akses menuju detail listener QRISku. Android menampilkan persetujuan **Berbahaya** dengan hitung mundur; setelah persetujuan perangkat, izin listener aktif. Logcat khusus tag QRISku mencatat **Notification listener terhubung** dan Beranda menjadi **Pemantauan aktif · Suara nonaktif**.
5. Onboarding tetap selesai, volume media tetap 60%, riwayat kosong tetap kosong. Bacakan Otomatis awalnya mati; di akhir pengujian preferensi perangkat sudah aktif dan tetap dipertahankan saat update. Test controller tidak memakai preferensi pengguna. Izin bukan diberikan oleh patch/manifest secara diam-diam.
6. Pengujian update/instrumentasi menghentikan proses aplikasi dan memunculkan **Pemantauan perlu diperiksa** meskipun izin masih ada. Log sistem khusus QRISku menunjukkan `AutStart Unable to bind notification listener service`: pembatasan startup HyperOS yang terpisah dari setelan terbatas.
7. Melalui **Setelan → Aplikasi → Mulai otomatis di latar belakang**, switch **Qrisku App** ditemukan mati, lalu diaktifkan sesuai persetujuan pengguna. Tidak mengubah switch aplikasi lain atau menonaktifkan penghematan baterai global.
8. Setelah patch sambung ulang, update APK, dan instrumentasi terakhir, membuka QRISku memulihkan callback koneksi serta status **Pemantauan aktif**, dengan Bacakan Otomatis aktif. Layanan juga tetap terdaftar ketika Activity dibawa ke background melalui Home; ini hanya smoke check singkat, bukan uji layar mati/berjam-jam.

Petunjuk umum berasal dari [panduan resmi Android tentang setelan terbatas](https://support.google.com/android/answer/12623953?hl=id). Jalur alternatif HyperOS didasarkan pada observasi perangkat di atas, bukan jaminan untuk semua vendor/versi OS. [Detail Settings listener](https://developer.android.com/reference/android/provider/Settings#ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS) tersedia sejak API 30; aplikasi memakai fallback public Settings untuk versi lama atau screen vendor yang tidak tersedia.

[Android requestRebind](https://developer.android.com/reference/android/service/notification/NotificationListenerService#requestRebind(android.content.ComponentName)) tidak memberikan akses kepada listener yang belum disetujui pengguna. [Panduan Xiaomi autostart](https://www.mi.com/global/support/faq/details/KA-497677/) mendokumentasikan pengaturan startup aplikasi dan dampak terhadap sumber daya/baterai. Pada perangkat ini menu tersedia langsung di halaman Aplikasi.

## Perubahan yang diperiksa

- **Alur izin:** bantuan Android 13+ pada onboarding, Beranda, Pengaturan, dan Bantuan; Info Aplikasi serta daftar aplikasi Android; penjelasan privasi dan langkah HyperOS. Tidak membaca/mengubah app-op melalui API tersembunyi.
- **Pembacaan notifikasi:** `NotificationContent` memprioritaskan teks lengkap `EXTRA_BIG_TEXT` yang tidak kosong. Sebelumnya preview kosong/terpotong dipakai walaupun pesan lengkap tersedia. Fallback preview tetap ada, parser/package filter tetap ketat.
- **Antrean TTS:** admission manual diperiksa di dalam Handler, bukan hanya state controller sebelum enqueue. Ketukan cepat uji/putar ulang tidak menambah permintaan ganda; pembayaran otomatis tetap dapat mengantre.
- **Listener setelah restart/update:** `requestRebind()` melalui API resmi setelah izin tersedia, retry terbatas 30 detik, dan panduan autostart untuk penolakan vendor. Percobaan bind tidak dianggap sama dengan koneksi berhasil.
- **Dialog:** bantuan, panduan izin, dan konfirmasi hapus memiliki konten scrollable. Pemeriksaan font 200% seluruh dialog belum diotomatisasi pada HP ini; tidak mengubah font sistem pengguna untuk mengujinya.

## Hasil build dan tests

Build:

```powershell
.\gradlew.bat "-Djavax.net.ssl.trustStoreType=Windows-ROOT" "-Djavax.net.ssl.trustStore=NONE" :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug --console=plain
```

| Pemeriksaan | Hasil |
| --- | --- |
| APK debug / test APK | Build berhasil |
| Unit test | 26 lulus: parser 3, state/identity 11, notification content 4, speech admission 3, reconnect policy 5 |
| Instrumented test pada HP USB | 16 lulus: repository 7, controller 4, Settings intents/permission reader 5 |
| Total tests | 42 lulus |
| Lint | 0 errors, 6 warnings tentang versi target/dependensi; tidak menaikkan dependensi di patch ini |
| Uji Suara fisik | State sedang dibacakan → selesai melalui callback TTS; tidak membuat riwayat pembayaran |
| Izin + koneksi layanan fisik | Aktif setelah persetujuan Android |
| Update/restart proses + foreground ulang | Listener kembali terhubung setelah autostart QRISku diizinkan |
| Update tanpa uninstall | Signature sama, data/pilihan pengguna dipertahankan |

Instrumen dijalankan langsung dengan `adb shell am instrument`, **bukan** `connectedDebugAndroidTest`, agar Gradle tidak menghapus APK utama/data setelah tes. Repository/controller test memakai database dan preferences UUID yang terisolasi dari namespace pengguna. APK debug awal dan pembaruan memiliki sertifikat SHA-256 sama: `107c8ad1fe1686aa23477aada89dc72092a3a311aeebe57270f7414997a73122`.

APK sementara `id.qrisku.app.test` dihapus setelah pengujian; dapat dibangun/dipasang ulang dari source test. APK utama v0.2.1 tetap terpasang. Pemeriksaan akhir Pengaturan menunjukkan **Akses sudah diberikan**, **Layanan pemantauan terhubung**, dan **Notifikasi tersimpan: 0**. HP dikembalikan ke Beranda dengan **Pemantauan aktif**.

Bukti UI tanpa data transaksi nyata: [Beranda aktif dan uji suara selesai](home-active-tts-complete.png), [panduan izin HyperOS setelah digulir](restricted-settings-guide.png).

## Dampak terhadap project

- `MainActivity.kt` dan `NotificationAccessSettings.kt`: aksi Settings terarah, fallback vendor, dan pemulihan melalui daftar aplikasi. Tidak mengubah exported component, target SDK, atau manifest permission.
- `AppController.kt`, `DeviceActions`, layar Compose: capability bantuan setelan terbatas mengikuti SDK; izin dibaca melalui API NotificationManager untuk komponen sendiri pada API 27+, fallback Secure Settings pada API 26. Status pemantauan tetap memerlukan callback koneksi. Tombol bantuan tidak mengklaim bahwa OS pasti memblokir izin.
- `ListenerReconnectPolicy.kt`, Activity/listener: retry monotonic 30 detik pada foreground/disconnection, tidak saat izin ditolak atau layanan sudah terhubung. Race pencabutan izin ditangani tanpa crash; request rebind tidak menulis permission.
- `QriskuNotificationListener.kt`, `NotificationContent.kt`: hanya pemilihan teks; parser nominal, deduplikasi, dan data minimal tetap sama. Teks mentah tidak disimpan.
- `VoiceEngine.kt`, `SpeechAdmission.kt`: guard antrean manual; status terminal, timeout, antrean otomatis, dan callback repository tidak diubah.
- SQLite schema tetap v1; tidak ada migration, backend/API baru, perubahan retensi, atau penghapusan data. Versi APK, README, penyelarasan PRD, dan regression tests diperbarui.

## Yang belum dibuktikan

Notifikasi pembayaran DANA Bisnis nyata, perangkat terkunci/layar mati, ketahanan baterai HyperOS, dan variasi format DANA belum diuji end-to-end. Unit test teks lengkap memakai pesan sintetis, bukan pembayaran nyata. Callback TTS selesai membuktikan penyelesaian engine, bukan konfirmasi bahwa pengguna mendengar audio dari speaker yang diharapkan. Matriks API 26/30 diuji melalui intent construction, belum pada dua perangkat OS tersebut.

## Next Recommended Updates

1. **High — Validasi DANA nyata dan background HyperOS.** Area: `QriskuNotificationListener.kt`, `VoiceEngine.kt`, dan QA perangkat. Alasan: izin terhubung belum membuktikan format notifikasi aktual atau keandalan saat layar mati. Risiko: pembayaran tidak tercatat/dibacakan meski status pemantauan aktif. Pengujian transaksi memerlukan persetujuan pemilik akun.
2. **Medium — Otomatisasi UI pemulihan izin dan dialog font besar.** Area: `ui/`, `NotificationAccessSettings.kt`, dan Android UI tests. Alasan: jalur vendor berbeda dan panduan panjang dapat bergeser pada font 200%/layar pendek. Risiko: regresi tombol pemulihan, teks persetujuan, atau dialog yang sulit digunakan.

Rekomendasi lanjutan belum diimplementasikan.

# Verifikasi QRISku App v0.2

Tanggal: 9 Oktober 2026. Emulator khusus `QRISku_Check`, Android 17/API 37.1, density 480 dpi. Pengujian tidak memakai perangkat atau data pedagang.

## Build dan pengujian otomatis

- `assembleDebug`, `testDebugUnitTest`, `lintDebug`, dan `connectedDebugAndroidTest`: **BUILD SUCCESSFUL**.
- **14 unit tests**: parser/pelafalan, state pemantauan, transisi pembacaan, identitas event, dan dua event nominal sama.
- **9 instrumented tests**: SQLite/paging/pemulihan/penghapusan/deduplikasi, filter sumber dan format, preferensi, serta callback listener sebelum izin terbaca. Nol gagal, error, atau skipped.
- **Lint: 0 error, 7 warning**. Warning terbatas pada target SDK dan ketersediaan versi Gradle/dependensi yang lebih baru; upgrade tidak termasuk tugas ini.
- `git diff --check`: bersih. Seluruh 28 checksum referensi Stitch tetap cocok; ilustrasi runtime merupakan salinan asli.

APK: `app/build/outputs/apk/debug/app-debug.apk`, versionName `0.2.0`, 12.238.459 byte. SHA-256:

```text
f8b190c9cc0bde8ae5bfd0b170203cc7434bcbc66b2866d33c5c90fd97475761
```

## Pemeriksaan native UI

Pemeriksaan manual mencakup lebar 320dp, 360dp, dan 412dp; font sistem 100%, 130%, dan 200% pada layar kecil. Konten dapat digulir, label navigasi tetap ada, dan kartu pemantauan menjadi konten pertama Beranda. Pada font di atas 150%, subtitle dekoratif header disembunyikan untuk memberi ruang bagi informasi utama.

Alur yang diamati: onboarding dan Nanti Saja, Beranda tanpa izin, pemberian akses/koneksi listener, suara otomatis nonaktif/aktif, perpindahan ketiga menu, riwayat kosong, preferensi suara di Pengaturan, serta error suara offline. Toggle Pengaturan tersimpan di SharedPreferences dan terbaca di Beranda. Logcat tidak menunjukkan crash aplikasi pada sesi smoke test.

Cuplikan layar aplikasi berjalan:

- [Beranda tanpa akses, 360dp](home-denied-360.png)
- [Pemantauan aktif dengan suara nonaktif, 360dp](home-active-muted-360.png)
- [Beranda dengan font 200%, 320dp](home-320-font200.png)

## Kontras token

Perhitungan luminansi sRGB pasangan warna teks aktif yang digunakan, bukan audit lengkap TalkBack/usability:

| Pasangan | Rasio |
| --- | --- |
| Primary / putih | 5,17:1 |
| Primary Dark / Primary Soft | 5,49:1 |
| Text / Background | 17,06:1 |
| Secondary Text / putih | 7,58:1 |
| Warning / Warning Soft | 4,51:1 |
| Error / Error Soft | 5,30:1 |
| Success / Success Soft | 4,57:1 |

## Batas verifikasi

DANA tidak terpasang di emulator dan voice Indonesia offline belum tersedia. UI memperlihatkan petunjuk pemasangan; pembacaan audio sukses, notifikasi DANA asli, layar mati, perilaku OEM/baterai, dan minimum API 26 belum divalidasi pada perangkat nyata. Tests menggunakan event sintetis, bukan pembayaran sungguhan. Pemeriksaan UI manual bukan suite Compose UI otomatis; detail berisi notifikasi nyata belum diuji end-to-end.

Gunakan emulator/perangkat khusus untuk `connectedDebugAndroidTest`: Gradle dapat menghapus APK dan data aplikasinya sesudah pengujian. Rekomendasi lanjutan ada di [penyelarasan PRD](../../qrisku-prd-alignment.md#next-recommended-updates).

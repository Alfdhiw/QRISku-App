# QRISku App — Referensi desain Stitch

Ekspor dari proyek [PRD UI Component Design](https://stitch.withgoogle.com/projects/15423157619100262672), ID `15423157619100262672`, pada 9 Oktober 2026 (Asia/Jakarta).

Implementasi Android v0.2 kini mengikuti [acuan PRD dan pemetaan state aktual](../../qrisku-prd-alignment.md). Katalog ini menyimpan arsip asli Stitch; catatan di bawah merekam keadaan project saat ekspor, sebelum implementasi tersebut.

Semua **12 item yang diminta** tersedia: **9 HTML, 2 SVG, 1 PRD Markdown, dan 11 gambar pratinjau**. Tiga gambar tambahan yang dirujuk HTML juga diunduh. File sumber dipertahankan sesuai keluaran Stitch.

## Katalog

| No. | Item | Screen ID | Kode / dokumen | Gambar (ukuran aktual) |
| --- | --- | --- | --- | --- |
| 1 | PRD_Desain_UI_UX_Qrisku_App_v1.0.md | `15369102763386810496` | [PRD_Desain_UI_UX_Qrisku_App_v1.0.md](01-prd/PRD_Desain_UI_UX_Qrisku_App_v1.0.md) | Tidak disediakan (dokumen Markdown) |
| 2 | Ilustrasi Onboarding Qrisku | `394fa65df03247b789c16b2cb68c7c32` | [source.svg](02-onboarding-illustration/source.svg) | [800 × 600](02-onboarding-illustration/preview.png) |
| 3 | S-04 Onboarding Uji Suara TTS | `96b95e0a4d5f4bdfa6532a5d102d85af` | [source.html](03-s04-tts-test/source.html) | [780 × 1994](03-s04-tts-test/preview.png) |
| 4 | S-02 & S-03 Onboarding & Izin Notifikasi | `3c9f591d923240ca81ea9aad09f44423` | [source.html](04-s02-s03-onboarding-permissions/source.html) | [780 × 1768](04-s02-s03-onboarding-permissions/preview.png) |
| 5 | S-05 Beranda (Pemantauan Aktif) | `2cd0af32e68a4f399b51e33bb7ef233b` | [source.html](05-s05-home/source.html) | [780 × 2400](05-s05-home/preview.png) |
| 6 | S-06 Riwayat Notifikasi | `4c4102cd6fa749479290dbfde1f0f84a` | [source.html](06-s06-history/source.html) | [780 × 2940](06-s06-history/preview.png) |
| 7 | S-07 Pengaturan Suara & Izin | `27d046adce1a42579939450deffb0d94` | [source.html](07-s07-settings/source.html) | [780 × 3656](07-s07-settings/preview.png) |
| 8 | S-06 Detail Notifikasi (Bottom Sheet) | `c494f7b7fd5f43229733b3ad45765b8f` | [source.html](08-s06-notification-detail-a/source.html) | [780 × 1768](08-s06-notification-detail-a/preview.png) |
| 9 | S-06 Detail Notifikasi (Bottom Sheet) | `e3922dbff2324d6ba1010a221ba8b2c3` | [source.html](09-s06-notification-detail-b/source.html) | [780 × 1768](09-s06-notification-detail-b/preview.png) |
| 10 | S-07 Dialog Konfirmasi Hapus Riwayat | `26ae97e8f4b247f2b36968c8d97760c5` | [source.html](10-s07-delete-history-dialog/source.html) | [585 × 2742](10-s07-delete-history-dialog/preview.jpg) |
| 11 | Logo Resmi Qrisku App | `a7b57c96c1fb4e08b4c895eacc62cc16` | [source.svg](11-app-logo/source.svg) | [480 × 480](11-app-logo/preview.png) |
| 12 | S-01 Splash Screen | `55dae19ea25a4b97a8b0e119f33c0baa` | [source.html](12-s01-splash/source.html) | [780 × 1768](12-s01-splash/preview.png) |

Dokumen PRD tidak memiliki screenshot dari Stitch. Item yang sebelumnya bernama “Generating Image...” dan “Generating Screen...” sudah selesai menjadi **Logo Resmi Qrisku App** dan **S-01 Splash Screen**. Kedua varian detail notifikasi disimpan terpisah karena screen ID dan kontennya berbeda.

## Design system, metadata, dan gambar pendukung

- [DESIGN.md](DESIGN.md): design system dari metadata proyek, disimpan tanpa mengubah instruksinya.
- [project.json](project.json): metadata proyek dan penempatan screen yang diberikan Stitch.
- [manifest.json](manifest.json): pemetaan judul, ID, file lokal, URL sumber, tipe MIME, dan ukuran gambar aktual.
- [checksums.json](checksums.json): hasil verifikasi format, ukuran file, serta checksum SHA-256 untuk 28 file sumber/gambar/metadata.
- [Logo header yang digunakan HTML](assets/header-logo.png).
- [Ilustrasi onboarding yang digunakan HTML](assets/onboarding-illustration.png).
- [Logo splash yang digunakan HTML](assets/splash-logo.png).

## Unduh dan baca referensi

File diunduh dengan `curl.exe -f -L`. Gambar menggunakan akhiran URL Google FIFE `=s0` untuk mengambil resolusi asli yang tersedia, karena URL tanpa akhiran menghasilkan thumbnail maksimal sekitar 512 piksel. Ukuran aktual dapat berbeda dari ukuran screen dalam metadata; misalnya screenshot dialog hapus riwayat tersedia sebagai JPEG 585 × 2742, sementara metadata screen menyebut 780 × 3656. Gambar asli dipertahankan tanpa diperbesar atau dikonversi.

Contoh mengunduh ulang ke lokasi baru menggunakan URL di manifest:

```powershell
curl.exe -f -L --output "salinan-source.html" "<source.downloadUrl dari manifest.json>"
curl.exe -f -L --output "salinan-preview.png" "<screenshot.downloadUrl dari manifest.json>"
```

HTML memakai Tailwind CDN, Google Fonts/Material Symbols, dan URL gambar eksternal. Salinan gambar eksternal tersedia pada folder `assets/`; HTML sumber tetap mempertahankan URL aslinya sehingga pratinjau HTML membutuhkan koneksi internet. SVG logo dan ilustrasi bisa dibaca sebagai sumber vektor.

Ekspor ini menjadi referensi untuk aplikasi Android Kotlin + Jetpack Compose. Tombol, switch, data transaksi contoh, tanggal, nama warung, profil, status Bluetooth, volume, dan label pada HTML merupakan mockup; implementasi aplikasi tetap perlu menghubungkannya ke state Android yang sebenarnya.

## Catatan dampak saat ekspor awal

Perubahan hanya menambahkan referensi desain pada `docs/design/stitch/15423157619100262672/`. Source Android, parser pembayaran, layanan notifikasi, konfigurasi Gradle, dan test yang sudah ada tidak diubah.

Saat membaca desain dan source project, ditemukan hal berikut untuk tahap implementasi:

- Detail notifikasi menampilkan package `com.dana.id`, sedangkan `QriskuNotificationListener.kt` memfilter `id.dana`. Package yang ditampilkan dan target membuka DANA perlu mengikuti identitas yang benar di implementasi.
- PRD menyebut primary `#2563EB`, background `#F8FAFC`, dan margin 20dp. HTML memakai primary `#004AC6`, surface `#FAF8FF`, dan gutter 16px. `DESIGN.md` juga memuat token hasil generator serta pedoman brand dengan nilai berbeda. Acuan token Compose perlu ditetapkan sebelum migrasi UI.
- Mockup menampilkan “Sudah Dibacakan”, “Tersinkron”, dan kesiapan layanan. `PocState.kt` saat ini hanya menyimpan `lastEvent` di memori; `VoiceEngine.kt` memeriksa hasil antrean TTS tetapi belum menyediakan status selesai/error ucapan ke UI, dan listener belum mengekspos status koneksi sebagai state UI.
- Dua detail S-06 memiliki hasil visual berbeda: varian A menampilkan sebagian isi dan area kosong; varian B menampilkan bottom sheet lengkap dengan latar redup. Keduanya tetap disimpan sebagai ekspor asli untuk dipilih dan ditinjau.

## Next Recommended Updates

1. **High — Selaraskan detail S-06 dan design tokens dengan PRD.** Area: PRD, `DESIGN.md`, HTML S-06, dan tema Compose saat diimplementasikan. Alasan: package DANA, label sinkronisasi, warna, dan spacing berbeda. Risiko jika tidak dilakukan: target aplikasi salah, klaim status yang tidak didukung, dan tampilan tidak konsisten.
2. **High — Petakan UI baru ke state listener, hasil TTS, preferensi suara, dan riwayat tersimpan.** Area: `MainActivity.kt`, `PocState.kt`, `QriskuNotificationListener.kt`, `VoiceEngine.kt`, serta penyimpanan lokal yang akan dibutuhkan. Alasan: mockup memerlukan state dan riwayat yang belum tersedia pada PoC. Risiko jika tidak dilakukan: indikator aktif/sukses keliru dan riwayat hilang saat proses dimulai ulang.
3. **Medium — Tetapkan varian detail final dan verifikasi tata letak Android.** Area: kedua desain S-06 dan komponen bottom sheet Compose saat diimplementasikan. Alasan: ekspor A terpotong, sementara PRD meminta dukungan layar 320/360/412dp dan font besar. Risiko jika tidak dilakukan: konten penting atau tombol tidak terlihat pada perangkat kecil.

Rekomendasi 1 dan 2 sudah ditangani pada implementasi Android v0.2; lihat acuan penyelarasan di atas. Pengujian visual lebih lanjut masih relevan. Source dan checksum ekspor asli tetap dipertahankan.


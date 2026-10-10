# Penyelarasan PRD dan implementasi QRISku

PRD v1.0 menjadi acuan UI Native Android v0.2. HTML, SVG, gambar, `DESIGN.md`, dan metadata ekspor Stitch tetap disimpan sebagai referensi asli. Tema aplikasi dan dokumen ini menetapkan pilihan ketika token generator berbeda dengan PRD.

## Token implementasi

| Token | Nilai PRD / implementasi | Area |
| --- | --- | --- |
| Primary / Primary Dark / Soft | `#2563EB` / `#1D4ED8` / `#DBEAFE` | Tombol, ikon, navigasi, sorotan |
| Background / Surface | `#F8FAFC` / `#FFFFFF` | Halaman / kartu dan sheet |
| Text / Secondary | `#0F172A` / `#475569` | Angka, judul / keterangan |
| Border | `#E2E8F0` | Kartu dan pemisah |
| Success / Warning / Error | `#15803D` / `#B45309` / `#B91C1C` | Feedback disertai teks/ikon |
| Font | Sans-serif sistem Android | Tanpa font jarak jauh |
| Margin / radius kartu / radius tombol | 20dp / 18dp / 14dp | Komponen reusable |
| Tombol / target sentuh | Minimal 52dp / 48dp | CTA dan kontrol |
| Judul / nominal / isi / pendukung | 24sp / 34sp / 16sp / 14sp | Mengikuti font scaling Android |

Implementasi: `ui/theme/QriskuTheme.kt`, `ui/components/`, dan resource tema/drawable Android.

## Konten yang diselaraskan

- Package DANA dipusatkan dalam `PaymentSource.PACKAGE = "id.dana"` untuk filter listener dan membuka DANA. Package `com.dana.id` dari mockup tidak dipakai.
- Label **Riwayat Notifikasi** dan **Notifikasi DANA Bisnis terdeteksi** mengikuti PRD.
- Label “Tersinkron”, Bluetooth contoh, profil/Pro, nama warung/pelanggan, referensi transaksi, tanggal statis, dan ringkasan pemasukan tidak masuk UI.
- Bottom sheet menampilkan data minimal tersimpan, kalimat suara dari nominal, dan catatan sumber notifikasi. Tidak merekonstruksi contoh sebagai “teks asli”.
- Splash memakai AndroidX SplashScreen tanpa delay buatan. Onboarding memakai ilustrasi Stitch dengan label contoh.
- Kartu pemantauan menjadi konten pertama Beranda. Sapaan opsional dihilangkan agar tidak mendominasi status pada font besar. Subtitle dekoratif header disembunyikan pada font di atas 150%; label navigasi tetap tersedia dan dapat membungkus.

## Pemetaan state dan aksi

| UI / aksi | Sumber state / tindakan |
| --- | --- |
| Akses notifikasi | `Settings.Secure`, saat resume dan layar aktif |
| Pemantauan aktif | Izin diberikan dan `onListenerConnected()` diterima |
| Perlu diperiksa | Izin ada, listener belum/tidak lagi terhubung |
| Bacakan Otomatis | SharedPreferences; saat mati riwayat tetap dicatat |
| Volume media | Persentase `AudioManager.STREAM_MUSIC` |
| Menunggu / sedang dibacakan | Antrean TTS / `onStart()` |
| Sudah dibacakan | `UtteranceProgressListener.onDone()` |
| Gagal / terhenti | Error/timeout atau penghentian/pemulihan proses |
| Terakhir / daftar | SQLite; 100 item per halaman |
| Putar ulang | Permintaan manual; status pembacaan awal dipertahankan |
| Hapus riwayat | Konfirmasi, hapus row, preferensi dipertahankan |
| Buka DANA | Launch intent `id.dana`; pesan jika tidak tersedia |
| Uji Suara | Permintaan contoh terpisah; tidak membuat riwayat pembayaran |

Dokumentasi Android: [callback TTS](https://developer.android.com/reference/android/speech/tts/UtteranceProgressListener), [koneksi listener](https://developer.android.com/reference/android/service/notification/NotificationListenerService), [kebutuhan jaringan voice](https://developer.android.com/reference/android/speech/tts/Voice), dan [state Compose](https://developer.android.com/develop/ui/compose/state).

## Penyimpanan dan konkurensi

SQLite `qrisku_notifications.db`, schema v1. PoC sebelumnya tidak memiliki riwayat disk yang perlu dimigrasikan. Operasi database memakai dispatcher IO dan mutex. Insert history serta marker deduplikasi atomik dalam satu transaksi. Hash identitas berasal dari package, notification key, postTime, judul, dan isi; hanya hash yang disimpan.

Status terminal tidak dapat ditimpa callback terlambat, dan callback tidak membuat row baru. Setelah restart, QUEUED/SPEAKING menjadi INTERRUPTED tanpa replay otomatis. Cache marker teknis dibatasi 4.096 item; riwayat baru dihapus atas tindakan pengguna, tanpa retensi waktu otomatis.

Riwayat yang masih tersimpan juga menolak event duplikat setelah marker cache-nya terhapus. Observasi koneksi listener dipisahkan dari pembacaan izin: callback yang tiba sebelum setting izin tersedia tidak hilang pada refresh berikutnya. Status aktif tetap mensyaratkan keduanya.

Engine memilih voice Indonesia offline, memakai audio media, serta membatasi antrean dan timeout. Listener dan UI berbagi engine per proses, sehingga perubahan Activity tidak menghentikan layanan suara.

## Dampak perubahan

`PocState` dan deduplikasi memori digantikan repository/state bersama. Parser dan pelafalan dipertahankan. Manifest memperbarui identitas, Application, splash, visibilitas DANA, serta pengecualian backup. Tidak menambahkan permission INTERNET atau dialog izin notifikasi runtime.

Dependensi lifecycle Compose, coroutine, splash, dan pengujian ditambahkan. README dan smoke test diperbarui. Tests mencakup aturan status, parser, SQLite/pemulihan/deduplikasi/paging/penghapusan, filter sumber, preferensi, dan race callback terhadap izin.

Hasil build, 23 tests, pemeriksaan UI, kontras token, dan batas validasi dicatat dalam [laporan verifikasi](verification/2026-10-09/README.md).

## Next Recommended Updates

1. **High — Uji listener/TTS dengan DANA Bisnis pada perangkat pedagang.** Area: listener, `VoiceEngine.kt`, dan pengujian perangkat. Alasan: format aktual, layar mati, baterai, dan callback engine berbeda antarperangkat. Risiko: notifikasi terlewat atau suara tidak terdengar.
2. **Medium — Putuskan retensi riwayat.** Area: `NotificationRepository.kt`, Pengaturan, dan dokumentasi privasi. Alasan: 30 hari masih usulan, saat ini hapus manual. Risiko: database bertambah terus pada penggunaan lama.
3. **Medium — Tambahkan uji UI otomatis untuk font besar dan detail panjang.** Area: `ui/`, tests Compose, dan bottom sheet. Alasan: seluruh navigasi dan state izin/error berubah. Risiko: regresi tampilan atau CTA terpotong.

Rekomendasi lanjutan belum diimplementasikan.

# PRODUCT REQUIREMENTS DOCUMENT (PRD)
## Desain UI/UX Qrisku App — Fase 2

| Informasi | Keterangan |
|---|---|
| Produk | Qrisku App — Payment Voice Assistant |
| Versi dokumen | 1.0 (Draft untuk review) |
| Fase | 2 — Desain UI/UX Custom |
| Platform | Native Android (Kotlin + Jetpack Compose) |
| Target pengguna | Pedagang UMKM, warung sembako, toko kelontong, dan usaha kecil |
| Sumber data awal | Notifikasi pembayaran masuk dari DANA Bisnis melalui aplikasi DANA |
| Tema visual | Modern Minimalist, biru-putih, ramah berbagai usia |
| Status produk | Spesifikasi desain; keandalan listener masih perlu validasi di perangkat nyata |

---

## 1. Ringkasan Produk

Qrisku App adalah asisten notifikasi suara yang membantu pedagang mengetahui nominal pembayaran QRIS yang diterima melalui DANA Bisnis. Dengan izin akses notifikasi Android, Qrisku membaca notifikasi yang sesuai pola pembayaran masuk, mengekstrak nominal, lalu membacakannya menggunakan Text-to-Speech (TTS).

**Prinsip penting:** Qrisku **tidak** memproses pembayaran, tidak menerbitkan QRIS, tidak terhubung langsung ke server pembayaran DANA pada MVP, dan **tidak memverifikasi settlement dana secara independen**. Riwayat dalam aplikasi adalah **riwayat notifikasi yang terdeteksi**, bukan buku besar atau bukti pembayaran resmi. Untuk konfirmasi transaksi, pengguna tetap merujuk aplikasi DANA.

Contoh notifikasi nyata yang menjadi acuan awal:
- **Judul:** `Pembayaran Masuk`
- **Isi:** `Rp29.000 diterima DANA Bisnis.`
- **Contoh suara:** “Pembayaran masuk sebesar dua puluh sembilan ribu rupiah.”

## 2. Masalah, Tujuan, dan Indikator Keberhasilan

### 2.1 Masalah pengguna
- Pedagang sering melayani pembeli sambil melakukan aktivitas lain sehingga tidak praktis membuka aplikasi DANA berkali-kali.
- Pedagang perlu mengetahui nominal yang disebutkan dalam notifikasi secara cepat tanpa menatap layar.
- Pengguna dari berbagai kalangan usia memerlukan aplikasi sederhana dengan istilah nonteknis.

### 2.2 Tujuan desain
1. Pengguna mengetahui kondisi pemantauan notifikasi dalam **sekitar 3 detik** saat membuka Beranda.
2. Pengguna baru mampu menyelesaikan pemberian izin dan menguji suara dengan panduan yang jelas.
3. Nominal notifikasi terakhir mudah terbaca dan terdengar.
4. Pengaturan suara dan riwayat dapat diakses melalui navigasi yang konsisten.
5. Desain tetap dapat digunakan pada perangkat Android dengan ukuran layar dan pengaturan font berbeda.

### 2.3 Indikator keberhasilan UX (target pengujian, bukan hasil terukur)
- ≥ 90% partisipan uji dapat menemukan status pemantauan tanpa bantuan.
- ≥ 85% partisipan uji dapat menyelesaikan onboarding dengan bantuan minimal.
- ≥ 90% partisipan uji dapat menemukan tombol **Uji Suara**.
- Tidak ada kekeliruan kritis dalam membedakan **notifikasi terdeteksi** dari **transaksi terverifikasi** pada sesi usability testing.

## 3. Pengguna Sasaran

| Persona | Kebutuhan | Implikasi desain |
|---|---|---|
| Pemilik warung 45–60 tahun, penggunaan smartphone dasar | Mengetahui suara asisten aktif dan nominal yang masuk | Teks besar, label eksplisit, area sentuh lebar, sedikit menu |
| Kasir/pedagang 20–40 tahun dengan ritme transaksi cepat | Mengaktifkan suara, melihat notifikasi terbaru, memeriksa jika suara tidak terdengar | Ringkasan status, aksi uji suara, daftar ringkas, troubleshooting |

**Prinsip UX:** satu tugas utama per layar, bahasa Indonesia sederhana, tindakan penting terlihat jelas, tanpa mengandalkan warna saja, dan tidak menggunakan grafik/fitur finansial yang tidak dibutuhkan.

## 4. Ruang Lingkup Produk

### 4.1 Wajib untuk MVP (P0)
- Splash screen ringan dan identitas merek Qrisku.
- Onboarding manfaat aplikasi dan penjelasan izin akses notifikasi.
- Alur menuju pengaturan Android untuk memberi **akses notifikasi** (`NotificationListenerService`) dan pemeriksaan status izin saat kembali.
- Panduan dan aksi **Uji Suara** untuk TTS.
- Beranda: status pemantauan, status suara otomatis, notifikasi terakhir, tombol uji suara.
- Riwayat notifikasi yang cocok dengan pola DANA Bisnis (nominal, tanggal/jam, sumber, status pembacaan).
- Pengaturan: suara otomatis aktif/nonaktif, kecepatan bicara, volume/panduan volume sesuai kapabilitas Android, uji suara, status izin, hapus riwayat.
- Keadaan UI: belum diatur, siap memantau, suara nonaktif, listener bermasalah, belum ada riwayat, TTS tidak tersedia.
- Navigasi bawah: **Beranda · Riwayat · Pengaturan**.
- Informasi privasi dan penegasan bahwa Qrisku hanya menyuarakan informasi notifikasi, bukan memverifikasi pembayaran.

### 4.2 Peningkatan setelah MVP (P1)
- Pilihan beberapa suara dan template kalimat yang didukung mesin TTS perangkat.
- Rentang waktu senyap/jam operasional.
- Filter dan pencarian riwayat.
- Mode gelap, tutorial interaktif, opsi ukuran teks tambahan.
- Dukungan penyedia QRIS lain setelah format notifikasinya teruji.

### 4.3 Di luar lingkup Fase 2/MVP
- Login, registrasi, server backend, integrasi API pembayaran resmi, cek saldo DANA.
- Pembuatan QRIS, refund, pembayaran keluar, laporan omset resmi atau rekonsiliasi keuangan.
- Klaim bahwa notifikasi membuktikan dana telah diterima secara terverifikasi.
- OCR layar, Accessibility Service untuk membaca aplikasi lain, atau akses akun DANA.

## 5. Information Architecture (Sitemap)

```text
Qrisku App
├── Splash (saat peluncuran)
├── Onboarding (instalasi pertama / pengaturan belum selesai)
│   ├── Pengenalan manfaat
│   ├── Penjelasan privasi & izin notifikasi
│   └── Uji suara & selesai
└── Aplikasi Utama
    ├── Beranda
    │   ├── Status pemantauan
    │   ├── Suara otomatis (aktif/nonaktif)
    │   ├── Uji suara
    │   └── Notifikasi terakhir
    ├── Riwayat
    │   ├── Daftar notifikasi
    │   └── Detail notifikasi (bottom sheet)
    └── Pengaturan
        ├── Preferensi suara
        ├── Izin & bantuan pemantauan
        ├── Privasi / hapus riwayat
        └── Tentang Qrisku
```

## 6. Spesifikasi Layar

### S-01 — Splash Screen
- **Tujuan:** menampilkan logo dan identitas singkat.
- **Isi:** logo Qrisku, nama aplikasi, latar putih dengan aksen biru.
- **Perilaku:** gunakan Android SplashScreen API; jangan memaksakan animasi atau delay panjang. Langsung navigasi ke onboarding atau Beranda berdasarkan kondisi pengaturan.

### S-02 — Onboarding: Pengenalan
- **Judul:** “Pembayaran masuk, langsung terdengar.”
- **Deskripsi:** “Qrisku membantu membacakan notifikasi pembayaran dari DANA Bisnis.”
- **Ilustrasi:** satu ilustrasi sederhana smartphone/notifikasi dan gelombang suara.
- **CTA:** **Mulai Pengaturan**.
- **Catatan:** tidak membutuhkan akun.

### S-03 — Onboarding: Akses Notifikasi
- **Judul:** “Izinkan Qrisku membaca notifikasi.”
- **Penjelasan:** “Android akan meminta akses notifikasi. Qrisku hanya memproses notifikasi DANA Bisnis yang sesuai pola pembayaran masuk. Informasi diproses secara lokal.”
- **Peringatan jujur:** Android memberikan akses yang secara teknis dapat mencakup notifikasi dari aplikasi lain; Qrisku berkomitmen mengabaikannya, bukan mengklaim izin Android hanya berlaku untuk DANA.
- **CTA utama:** **Buka Pengaturan Akses**.
- **CTA sekunder:** **Nanti Saja** (pengguna kembali ke Beranda dengan status belum aktif).
- **Setelah kembali ke aplikasi:** periksa apakah izin benar-benar diberikan; jangan menganggapnya sukses hanya karena halaman Android dibuka.

### S-04 — Onboarding: Uji Suara
- **Judul:** “Coba suara Qrisku.”
- **Deskripsi:** “Pastikan volume media terdengar.”
- **CTA:** **Uji Suara** → TTS membacakan kalimat contoh; tombol dapat digunakan berulang.
- **Status:** tampilkan instruksi jika TTS tidak tersedia / tidak bisa digunakan.
- **CTA akhir:** **Selesai**. Jika izin notifikasi belum diberikan, tetap tampilkan peringatan konfigurasi.

### S-05 — Beranda (halaman prioritas tertinggi)
**Susunan vertikal:**
1. App bar: logo Qrisku dan tombol bantuan.
2. Sapaan singkat: “Halo, siap jualan hari ini?” (opsional, tidak dominan).
3. **Kartu status pemantauan (paling menonjol):** ikon + teks, deskripsi, langkah perbaikan bila perlu.
4. **Kontrol suara otomatis:** label **Bacakan Otomatis** + switch; saat nonaktif, notifikasi tetap dapat dicatat tetapi tidak disuarakan.
5. **Tombol Uji Suara** dengan ikon speaker dan area sentuh besar.
6. **Notifikasi Terakhir:** contoh `Rp29.000`, waktu dan label **Notifikasi DANA Bisnis terdeteksi**; jika kosong tampilkan empty state.
7. Bottom navigation tiga menu.

**Copy status yang disetujui:**
- Izin belum ada: “Akses notifikasi belum aktif” → **Aktifkan Akses**.
- Izin ada dan listener terhubung: “Pemantauan aktif” → “Siap mendeteksi notifikasi DANA Bisnis.”
- Suara otomatis dimatikan: “Pemantauan aktif · Suara nonaktif”.
- Listener tidak terhubung / pengaturan bermasalah: “Pemantauan perlu diperiksa” → **Periksa Pengaturan**.

**Batas klaim:** status **Pemantauan aktif** hanya menunjukkan kesiapan layanan yang dapat diamati aplikasi; bukan koneksi resmi ke DANA atau jaminan semua pembayaran akan terdeteksi.

### S-06 — Riwayat
- **Judul:** “Riwayat Notifikasi” (jangan gunakan “Riwayat Transaksi” untuk MVP).
- **Setiap item:** nominal besar, tanggal/jam, label sumber **DANA Bisnis**, label status **Sudah dibacakan** / **Suara nonaktif** / **Gagal dibacakan**.
- **Urutan:** terbaru di atas.
- **Detail:** bottom sheet dengan nominal, waktu notifikasi terdeteksi, status pembacaan dan catatan: “Data ini berasal dari notifikasi perangkat. Periksa DANA untuk konfirmasi transaksi.”
- **Empty state:** “Belum ada notifikasi pembayaran yang terdeteksi.”
- **Larangan desain:** hindari menampilkan **Total Pemasukan** seolah data lengkap dan resmi.

### S-07 — Pengaturan
Kelompok pengaturan:
1. **Suara Asisten:** switch **Bacakan Otomatis**, kecepatan bicara (Lambat / Normal / Cepat), tombol **Uji Suara**, pengaturan volume media (dengan penjelasan kontrol volume perangkat).
2. **Pemantauan:** status akses notifikasi, tautan **Periksa Akses Notifikasi**, bantuan jika notifikasi tidak terbaca atau aplikasi dibatasi baterai.
3. **Data & Privasi:** penjelasan data lokal, tombol **Hapus Riwayat** dengan dialog konfirmasi.
4. **Informasi:** tentang Qrisku dan penafian sumber notifikasi.

## 7. Alur Pengguna Utama

### Flow A — Pengguna pertama kali
`Buka aplikasi → Splash → Pengenalan → Penjelasan izin → Pengaturan Android → Kembali & verifikasi izin → Uji Suara → Beranda`

**Percabangan:** jika izin ditolak, pengguna boleh masuk Beranda tetapi kartu status menampilkan tindakan **Aktifkan Akses**; jangan menampilkan status aktif palsu.

### Flow B — Menerima notifikasi pembayaran
`DANA memunculkan notifikasi → Listener menerima event → Validasi aplikasi/format → Ekstraksi nominal → Catat riwayat → Jika Bacakan Otomatis aktif, masukkan ke antrean TTS → Perbarui Beranda`

**Percabangan:** notifikasi tak cocok, tak terbaca, atau berduplikasi diabaikan atau ditangani sesuai logika engine; jangan menampilkan sebagai pembayaran valid. Dua pembayaran berbeda dengan nominal sama tidak boleh disatukan hanya karena nilainya sama.

### Flow C — Suara tidak terdengar
`Beranda → Uji Suara → Jika gagal: tampilkan petunjuk cek volume media, TTS perangkat, mode senyap/pengaturan audio yang relevan → Coba lagi`.

### Flow D — Meninjau informasi masuk
`Beranda → Riwayat → Pilih item → Detail notifikasi → Tutup detail`.

## 8. Design System

### 8.1 Palet warna

| Token | Hex | Penggunaan |
|---|---|---|
| Primary / Blue 600 | `#2563EB` | Tombol utama, ikon aktif, navigasi aktif |
| Primary Dark / Blue 700 | `#1D4ED8` | Variasi pressed/kontras |
| Primary Soft / Blue 100 | `#DBEAFE` | Sorotan ringan dan latar informasi |
| Background | `#F8FAFC` | Latar halaman |
| Surface | `#FFFFFF` | Kartu, sheet, dialog |
| Text Primary | `#0F172A` | Judul/angka utama |
| Text Secondary | `#475569` | Deskripsi dan metadata |
| Border | `#E2E8F0` | Garis pemisah/kartu |
| Success | `#15803D` | Kesiapan/aksi positif (bukan verifikasi dana) |
| Warning | `#B45309` | Masalah pengaturan nonkritis |
| Error | `#B91C1C` | Gagal dan tindakan yang butuh perhatian |

**Gaya:** dominan putih, biru sebagai aksen tindakan, radius membulat, garis batas lembut, tanpa gradient berat, tanpa efek glassmorphism sebagai dasar tampilan.

### 8.2 Tipografi
- Font utama: **Roboto / sans-serif sistem Android**, mudah dibaca dan tersedia stabil di perangkat.
- Judul halaman: **24sp, semibold**.
- Nominal utama: **30–34sp, bold**, tabular figures jika tersedia.
- Isi utama: **16sp, regular**.
- Teks pendukung: **14sp**; hindari teks kritis di bawah 14sp.
- Label tombol: **16sp, semibold**.
- Hormati pengaturan ukuran font dan *font scaling* Android; komponen harus bisa membesar tanpa memotong pesan penting.

### 8.3 Tata letak dan komponen
- Grid spacing berbasis **8dp**; jarak umum 8/16/24dp.
- Margin horizontal layar: **20dp**, adaptif di perangkat kecil.
- Radius kartu **16–20dp**, tombol **12–16dp**.
- Tinggi tombol utama **minimal 52dp**.
- Area sentuh interaktif **minimal 48×48dp**.
- Bottom navigation: tiga menu + ikon dan teks, aktif ditandai ikon/warna/label.
- Hindari interaksi yang hanya bisa dilakukan dengan *swipe* atau ikon tanpa label.

### 8.4 Komponen reusable di Jetpack Compose
`QriskuTopBar`, `MonitoringStatusCard`, `AutoVoiceToggle`, `TestVoiceButton`, `LatestNotificationCard`, `NotificationHistoryItem`, `NotificationDetailSheet`, `PermissionActionCard`, `EmptyState`, `ErrorState`, `SettingsGroup`, `ConfirmDeleteDialog`, dan `QriskuBottomBar`.

Seluruh komponen memiliki varian **default, pressed, disabled, loading (jika relevan), dan error**; komponen status juga memiliki varian **aktif, tidak aktif, dan perlu perhatian**.

## 9. Panduan Konten (Microcopy)

| Kondisi | Teks yang digunakan | Hindari |
|---|---|---|
| Pemantauan siap | “Pemantauan aktif” | “Terhubung ke server DANA” |
| Pesan masuk | “Notifikasi DANA Bisnis terdeteksi” | “Dana dijamin sudah masuk” |
| Data daftar | “Riwayat Notifikasi” | “Buku kas resmi” |
| Izin belum aktif | “Aktifkan akses notifikasi” | “Aktifkan NotificationListenerService” |
| Tes audio | “Uji Suara” | “Initialize TTS” |
| Suara dimatikan | “Suara otomatis nonaktif” | “Aplikasi mati” |
| Belum ada data | “Belum ada notifikasi yang terdeteksi” | “Belum ada pembayaran hari ini” |

## 10. Aksesibilitas dan Usability

1. Target kontras mengikuti **WCAG AA**: minimal **4,5:1** untuk teks normal dan **3:1** untuk teks besar (termasuk uji pada kombinasi warna sebenarnya).
2. Semua ikon penting memiliki label teks atau *contentDescription* untuk pembaca layar.
3. Prioritaskan ukuran sentuh minimal **48dp**, cukup jarak antartombol.
4. Jangan mengandalkan warna hijau/merah saja; selalu sertai ikon dan kalimat status.
5. Mendukung rotasi/ukuran layar yang realistis atau mempertahankan tata letak aman saat perangkat berubah.
6. Uji pada lebar **320dp, 360dp, 412dp**, serta font sistem besar; scroll vertikal tidak boleh memotong tombol penting.
7. Gunakan animasi singkat (±150–250ms) dan hormati pengaturan pengurangan animasi perangkat jika tersedia.
8. Jangan menggunakan suara otomatis yang sulit dihentikan saat pengguna mematikan **Bacakan Otomatis**.

## 11. Privasi, Kepercayaan, dan Keamanan UX

- Sebelum meminta izin, jelaskan dengan lugas alasan kebutuhan akses notifikasi dan ruang lingkup teknis izin Android.
- MVP memproses data **di perangkat**, tanpa mengunggah konten notifikasi ke server Qrisku.
- Filter berdasarkan identitas *package* aplikasi DANA yang diverifikasi dalam PoC; jangan percaya teks nama pengirim semata.
- Jangan menyimpan isi mentah notifikasi dari aplikasi lain.
- Simpan seminimal mungkin untuk riwayat: nominal, waktu deteksi, sumber, dan status suara; hindari identitas pihak pembayar yang tidak dibutuhkan.
- Riwayat dapat dihapus melalui dialog konfirmasi. Usulan retensi default **30 hari** memerlukan persetujuan produk sebelum implementasi.
- Tidak meminta PIN, OTP, kata sandi, kredensial DANA, maupun izin Accessibility Service.
- Tambahkan penjelasan: “Qrisku membacakan notifikasi yang muncul di perangkat. Untuk memastikan status pembayaran, periksa aplikasi DANA.”

## 12. State dan Penanganan Kasus Khusus

| Keadaan | Respons UI |
|---|---|
| Izin belum diberikan | Kartu peringatan + tombol **Aktifkan Akses** |
| Izin dicabut setelah aktif | Status berubah menjadi **Akses notifikasi belum aktif**; tombol perbaikan |
| Listener belum terhubung walau izin ada | **Pemantauan perlu diperiksa**, petunjuk langkah teknis sederhana |
| Belum ada notifikasi cocok | Empty state informatif, tanpa nominal/total palsu |
| Suara otomatis dimatikan | Status monitoring tetap jelas, riwayat masih dapat dicatat |
| Mesin TTS gagal / bahasa tidak tersedia | Pesan **Suara belum siap**, petunjuk periksa pengaturan suara perangkat |
| Perangkat membatasi aktivitas aplikasi | Panduan pemecahan masalah, hindari klaim solusi pasti |
| Dua notifikasi nominal sama | Item tetap terpisah jika merupakan event berbeda |
| Ada pembaruan notifikasi yang sama | Hindari membacakan berulang; jangan deduplikasi hanya dengan nominal |
| Tidak ada internet | Fitur lokal tetap ditampilkan; jangan menjamin notifikasi DANA akan tiba |
| Data bukan pembayaran QRIS sesuai pola | Jangan ditampilkan sebagai pembayaran DANA Bisnis terdeteksi |

## 13. Kriteria Penerimaan (Acceptance Criteria) Desain

**AC-01 — Navigasi:** terdapat tiga menu bawah dengan label **Beranda, Riwayat, Pengaturan** dan posisi konsisten.

**AC-02 — Prioritas Beranda:** status pemantauan terlihat sebelum daftar notifikasi tanpa perlu interaksi tambahan pada layar Android umum.

**AC-03 — Status jujur:** mockup memperlihatkan minimal empat kondisi: belum diberi izin, pemantauan aktif, suara nonaktif, dan pemantauan bermasalah.

**AC-04 — Permission flow:** pengguna memahami mengapa izin diperlukan dan memiliki jalan kembali setelah menolak; desain tidak mengklaim izin telah berhasil sebelum diperiksa.

**AC-05 — Riwayat:** daftar menyebut **notifikasi**, bukan transaksi tervalidasi; tidak ada ringkasan omset resmi.

**AC-06 — Suara:** tersedia tindakan **Uji Suara** di Beranda dan Pengaturan serta *state* bila uji suara gagal.

**AC-07 — Aksesibilitas:** ukuran sentuh dan tipografi memenuhi pedoman di §10; kontras diverifikasi pada komponen final.

**AC-08 — Responsif:** layout diuji setidaknya pada 320dp, 360dp, 412dp dan font yang diperbesar tanpa kehilangan CTA penting.

**AC-09 — Data kosong/error:** seluruh halaman yang memiliki data atau ketergantungan izin menampilkan empty state dan error state yang bisa ditindaklanjuti.

**AC-10 — Implementasi:** setiap layar memiliki spesifikasi komponen, state, nama aksi, dan contoh microcopy agar dapat diimplementasikan di Jetpack Compose tanpa menebak perilaku.

## 14. Handoff untuk Desain dan Development

### 14.1 Deliverables Fase 2
- Design tokens: warna, tipografi, spacing, radius, iconography.
- Sitemap dan user flow onboarding, notifikasi masuk, troubleshooting.
- Wireframe low-fidelity dan UI high-fidelity seluruh layar S-01 s.d. S-07.
- Varian UI untuk setiap state penting di §12.
- Prototype klik dari onboarding → Beranda → Riwayat → Pengaturan.
- Komponen reusable dengan penamaan yang konsisten.
- Catatan implementasi Jetpack Compose: layout, state, event, navigasi, dan aksesibilitas.
- Uji pemahaman desain minimal dengan satu pedagang yang kurang terbiasa memakai smartphone dan satu pengguna aktif smartphone.

### 14.2 Struktur kode UI yang disarankan

```text
ui/
├── theme/             # Color, Type, Shape, Spacing
├── components/        # Komponen Compose reusable
├── navigation/        # NavHost dan rute
├── onboarding/        # Intro, izin, uji suara
├── home/              # Beranda dan state monitoring
├── history/           # Daftar dan detail notifikasi
├── settings/          # Pengaturan audio, izin, privasi
└── state/             # UI state dan event
```

### 14.3 Dependensi antar-fase
- Desain dapat dibuat menggunakan data tiruan yang jelas diberi label **contoh**.
- Implementasi pembacaan notifikasi di perangkat nyata tetap membutuhkan hasil PoC: **package name DANA**, struktur `Notification.EXTRA_TITLE`/`EXTRA_TEXT`, variasi format nominal, dan perilaku notifikasi yang diperbarui.
- Fase 2 **tidak dianggap memvalidasi keberhasilan deteksi pembayaran**; validasi tersebut menjadi kriteria teknis terpisah.

## 15. Hal yang Perlu Diputuskan Sebelum Desain Final

1. Apakah suara otomatis aktif secara default **setelah** izin dan tes suara selesai? **Rekomendasi:** ya, tampilkan kontrol on/off yang jelas.
2. Berapa lama riwayat lokal disimpan? **Usulan:** 30 hari dan tombol hapus semua, menunggu persetujuan.
3. Apakah pengguna bisa memilih suara berbeda pada MVP? **Rekomendasi:** cukup suara TTS Bahasa Indonesia default; variasi suara masuk P1.
4. Apakah dukungan mode gelap penting di rilis awal? **Rekomendasi:** fokus mode terang biru-putih untuk mempercepat penyelesaian, tanpa menghalangi dukungan berikutnya.

---

**Keputusan desain inti:** Qrisku adalah **asisten pembaca notifikasi pembayaran**, bukan dompet digital atau aplikasi verifikasi pembayaran. Prioritas UI adalah **status pemantauan yang jelas, suara yang mudah diuji, dan riwayat notifikasi yang tidak menyesatkan**.

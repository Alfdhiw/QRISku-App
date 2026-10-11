package id.qrisku.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RestrictedSettingsDialog(onDismiss: () -> Unit, onApplicationInfo: () -> Unit,
                             onApplicationList: () -> Unit,
                             onPermission: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Akses diblokir Android?") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Jika muncul “Aplikasi tidak diberi akses” atau “Dikontrol oleh Setelan Terbatas”, " +
                    "Android 13 atau lebih baru dapat membatasi akses aplikasi yang dipasang lewat APK.")
                Text("1. Buka Info Aplikasi QRISku melalui tombol di bawah.\n\n" +
                    "2. Buka menu ⋮ (Lainnya), lalu pilih Izinkan setelan terbatas. " +
                    "Selesaikan konfirmasi/PIN/biometrik di Android jika diminta.\n\n" +
                    "3. Kembali ke QRISku, buka Akses Notifikasi dan aktifkan izin untuk QRISku.")
                Text("Pada Xiaomi/HyperOS, Info Aplikasi biasa mungkin tidak menampilkan menu tersebut. " +
                    "Gunakan Daftar Aplikasi Android di bawah → pilih Qrisku App → Lainnya → " +
                    "Izinkan setelan terbatas. Nama/letak menu dapat berbeda pada HP lain.")
                SecondaryAction("Daftar Aplikasi Android", onApplicationList)
                Text("Jika kedua jalur tidak menyediakan menu atau perangkat dikelola kantor, " +
                    "hubungi pengelola perangkat. QRISku tidak dapat membuka pembatasan ini sendiri.")
                Text("Lanjutkan hanya jika Anda percaya sumber APK. Akses ini secara teknis dapat " +
                    "membaca notifikasi pribadi dan keuangan dari aplikasi lain. QRISku hanya memproses " +
                    "notifikasi DANA Bisnis yang sesuai, secara lokal. Tidak perlu menonaktifkan Play Protect.",
                    style = MaterialTheme.typography.bodyMedium)
                SecondaryAction("Buka Akses Notifikasi", onPermission)
            }
        },
        confirmButton = { TextButton(onClick = onApplicationInfo) { Text("Buka Info Aplikasi") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Nanti") } })
}

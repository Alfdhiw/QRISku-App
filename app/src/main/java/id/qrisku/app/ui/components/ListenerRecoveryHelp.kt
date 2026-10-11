package id.qrisku.app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ListenerRecoveryHelp(onAndroidSettings: () -> Unit) {
    Text("QRISku mencoba menyambungkan kembali layanan yang sudah diizinkan. " +
        "Jika tetap terputus, periksa izin mulai/latar belakang aplikasi.\n\n" +
        "Xiaomi/HyperOS: Setelan → Aplikasi → Mulai otomatis di latar belakang " +
        "(atau Perizinan → Mulai otomatis), lalu izinkan QRISku saja. " +
        "Nama menu dapat berbeda di tiap HP. Pengaturan ini dapat menambah penggunaan baterai.",
        style = MaterialTheme.typography.bodyMedium)
    SecondaryAction("Buka Pengaturan Android", onAndroidSettings)
}

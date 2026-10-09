package id.qrisku.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Status sementara di memori; tidak menyimpan data pembayaran ke disk. */
object PocState {
    var lastEvent by mutableStateOf("Belum ada notifikasi DANA Bisnis yang diproses.")
}

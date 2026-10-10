package id.qrisku.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import id.qrisku.app.R
import id.qrisku.app.data.*
import id.qrisku.app.ui.theme.QriskuColors
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun amountLabel(amount: Long): String =
    "Rp" + NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(amount)

fun timeLabel(time: Long): String =
    DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", Locale.forLanguageTag("id-ID"))
        .withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(time))

@Composable
fun AppIcon(@DrawableRes resource: Int, modifier: Modifier = Modifier, description: String? = null,
            tint: Color = QriskuColors.Primary) {
    Icon(painterResource(resource), description, modifier = modifier.size(24.dp), tint = tint)
}

@Composable
fun QriskuTopBar(onHelp: () -> Unit) {
    Surface(color = Color.White, shadowElevation = 1.dp) {
        Row(Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Image(painterResource(R.drawable.qrisku_logo), null, Modifier.size(44.dp))
            Column(Modifier.weight(1f)) {
                Text("Qrisku", style = MaterialTheme.typography.titleLarge)
                if (LocalDensity.current.fontScale <= 1.5f) {
                    Text("Asisten notifikasi suara", style = MaterialTheme.typography.bodyMedium,
                        color = QriskuColors.SecondaryText)
                }
            }
            IconButton(onClick = onHelp, modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)) {
                AppIcon(R.drawable.ic_help, description = "Bantuan", tint = QriskuColors.SecondaryText)
            }
        }
    }
}

@Composable
fun QriskuCard(modifier: Modifier = Modifier, color: Color = Color.White,
               content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
        color = color, border = BorderStroke(1.dp, QriskuColors.Border)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
fun PrimaryAction(label: String, onClick: () -> Unit, enabled: Boolean = true,
                  @DrawableRes icon: Int? = null) {
    Button(onClick = onClick, enabled = enabled,
        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)) {
        if (icon != null) {
            AppIcon(icon, tint = Color.White)
            Spacer(Modifier.width(8.dp))
        }
        Text(label)
    }
}

@Composable
fun SecondaryAction(label: String, onClick: () -> Unit, enabled: Boolean = true) {
    OutlinedButton(onClick = onClick, enabled = enabled,
        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)) {
        Text(label)
    }
}

@Composable
fun InfoNotice(text: String, warning: Boolean = false) {
    QriskuCard(color = if (warning) QriskuColors.WarningSoft else QriskuColors.PrimarySoft) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppIcon(if (warning) R.drawable.ic_warning else R.drawable.ic_info,
                tint = if (warning) QriskuColors.Warning else QriskuColors.PrimaryDark)
            Text(text, style = MaterialTheme.typography.bodyMedium,
                color = if (warning) QriskuColors.Warning else QriskuColors.Text)
        }
    }
}

@Composable
fun PlaybackBadge(status: PlaybackStatus) {
    val foreground = when (status) {
        PlaybackStatus.SPOKEN -> QriskuColors.Success
        PlaybackStatus.FAILED -> QriskuColors.Error
        PlaybackStatus.INTERRUPTED -> QriskuColors.Warning
        PlaybackStatus.MUTED -> QriskuColors.SecondaryText
        else -> QriskuColors.PrimaryDark
    }
    val background = when (status) {
        PlaybackStatus.SPOKEN -> QriskuColors.SuccessSoft
        PlaybackStatus.FAILED -> QriskuColors.ErrorSoft
        PlaybackStatus.INTERRUPTED -> QriskuColors.WarningSoft
        PlaybackStatus.MUTED -> QriskuColors.Background
        else -> QriskuColors.PrimarySoft
    }
    Surface(color = background, shape = RoundedCornerShape(50)) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            if (status == PlaybackStatus.SPOKEN) AppIcon(R.drawable.ic_check, tint = foreground, modifier = Modifier.size(18.dp))
            Text(status.label, style = MaterialTheme.typography.labelMedium, color = foreground)
        }
    }
}

@Composable
fun AutoVoiceToggle(automatic: Boolean, onChange: (Boolean) -> Unit) {
    QriskuCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppIcon(R.drawable.ic_speaker)
            Column(Modifier.weight(1f)) {
                Text("Bacakan Otomatis", style = MaterialTheme.typography.titleMedium)
                Text(if (automatic) "Notifikasi yang cocok akan dibacakan."
                    else "Notifikasi tetap dicatat tanpa suara.", style = MaterialTheme.typography.bodyMedium,
                    color = QriskuColors.SecondaryText)
            }
            Switch(checked = automatic, onCheckedChange = onChange,
                modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .semantics { contentDescription = "Bacakan Otomatis" })
        }
    }
}

@Composable
fun VoiceTestCard(state: SpeechState, mediaVolume: Int, onTest: () -> Unit,
                  onStop: () -> Unit, onRetry: () -> Unit, onSettings: () -> Unit) {
    QriskuCard(color = QriskuColors.PrimarySoft) {
        Text("Uji suara asisten", style = MaterialTheme.typography.titleLarge)
        Text("Pastikan volume media terdengar.", style = MaterialTheme.typography.bodyLarge)
        if (state.engine == EngineStatus.INITIALIZING) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                Text("Menyiapkan suara…", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Text("Volume media perangkat: $mediaVolume%", style = MaterialTheme.typography.bodyMedium)
        if (mediaVolume == 0) Text("Volume media masih nol. Naikkan volume sebelum menguji suara.",
            style = MaterialTheme.typography.bodyMedium, color = QriskuColors.Warning)
        state.testStatus?.let {
            Text(when (it) {
                PlaybackStatus.SPOKEN -> "Contoh selesai dibacakan. Pastikan suaranya terdengar."
                PlaybackStatus.FAILED -> "Uji suara gagal. Periksa pengaturan suara perangkat."
                PlaybackStatus.INTERRUPTED -> "Uji suara dihentikan."
                else -> it.label
            }, style = MaterialTheme.typography.bodyMedium)
        }
        state.error?.let { Text(it, color = QriskuColors.Error, style = MaterialTheme.typography.bodyMedium) }
        PrimaryAction(if (state.busy) "Suara sedang diproses…" else "Uji Suara",
            onTest, enabled = state.engine == EngineStatus.READY && !state.busy, icon = R.drawable.ic_speaker)
        if (state.busy) SecondaryAction("Hentikan Suara", onStop)
        if (state.engine == EngineStatus.UNAVAILABLE) {
            SecondaryAction("Coba Lagi", onRetry)
            SecondaryAction("Pengaturan Suara Perangkat", onSettings)
        }
    }
}

@Composable
fun EmptyState(title: String, description: String) {
    QriskuCard {
        AppIcon(R.drawable.ic_notification, modifier = Modifier.size(40.dp))
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(description, style = MaterialTheme.typography.bodyLarge, color = QriskuColors.SecondaryText)
    }
}

@Composable
fun StorageError(message: String, onRetry: () -> Unit) {
    QriskuCard(color = QriskuColors.ErrorSoft) {
        Text("Riwayat perlu diperiksa", style = MaterialTheme.typography.titleMedium, color = QriskuColors.Error)
        Text(message, style = MaterialTheme.typography.bodyMedium)
        SecondaryAction("Coba Muat Riwayat", onRetry)
    }
}

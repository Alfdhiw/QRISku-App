package id.qrisku.app.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import id.qrisku.app.IndonesianNumberWords
import id.qrisku.app.R
import id.qrisku.app.data.*
import id.qrisku.app.ui.components.*
import id.qrisku.app.ui.theme.QriskuColors

@Composable
fun HistoryScreen(history: HistoryState, onSelect: (String) -> Unit,
                  onRetry: () -> Unit, onLoadMore: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Riwayat Notifikasi", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Notifikasi yang terdeteksi di perangkat ini.",
                style = MaterialTheme.typography.bodyLarge, color = QriskuColors.SecondaryText)
        }
        history.error?.let { item { StorageError(it, onRetry) } }
        if (history.loading) item { CircularProgressIndicator() }
        else if (history.records.isEmpty()) item {
            EmptyState("Belum ada notifikasi pembayaran",
                "Notifikasi DANA Bisnis yang sesuai akan muncul di sini, terbaru di atas.")
        }
        items(history.records, key = { it.id }) { record ->
            NotificationHistoryItem(record) { onSelect(record.id) }
        }
        if (history.records.size < history.totalCount) item {
            SecondaryAction("Muat Notifikasi Lainnya", onLoadMore)
        }
        item { InfoNotice("Riwayat ini berasal dari notifikasi perangkat, bukan buku kas resmi. " +
            "Periksa DANA untuk konfirmasi transaksi.") }
    }
}

@Composable
fun NotificationHistoryItem(record: NotificationRecord, onClick: () -> Unit) {
    Surface(Modifier.fillMaxWidth().clickable(onClick = onClick).semantics(mergeDescendants = true) {},
        color = QriskuColors.Surface, shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, QriskuColors.Border)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                AppIcon(R.drawable.ic_notification)
                Text(PaymentSource.LABEL, style = MaterialTheme.typography.titleMedium)
            }
            Text(amountLabel(record.amount), style = MaterialTheme.typography.headlineMedium)
            Text(timeLabel(record.detectedAt), style = MaterialTheme.typography.bodyMedium,
                color = QriskuColors.SecondaryText)
            PlaybackBadge(record.playbackStatus)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailSheet(record: NotificationRecord, speech: SpeechState,
                            onDismiss: () -> Unit, onReplay: () -> Unit, onDana: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = QriskuColors.Surface,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Rincian Notifikasi", style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f))
                IconButton(onClick = onDismiss, modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)) {
                    Icon(painterResource(R.drawable.ic_close), "Tutup rincian")
                }
            }
            QriskuCard(color = QriskuColors.Background) {
                Text(PaymentSource.LABEL, style = MaterialTheme.typography.titleMedium, color = QriskuColors.PrimaryDark)
                Text("Nominal terdeteksi", style = MaterialTheme.typography.bodyMedium, color = QriskuColors.SecondaryText)
                Text(amountLabel(record.amount), style = MaterialTheme.typography.headlineLarge)
                Text("Terdeteksi: " + timeLabel(record.detectedAt), style = MaterialTheme.typography.bodyMedium)
                PlaybackBadge(record.playbackStatus)
            }
            QriskuCard {
                Text("Kalimat suara", style = MaterialTheme.typography.titleMedium)
                Text("Pembayaran masuk sebesar " + IndonesianNumberWords.spell(record.amount) + " rupiah.",
                    style = MaterialTheme.typography.bodyLarge)
            }
            PrimaryAction("Putar Ulang Suara", onReplay,
                enabled = speech.engine == EngineStatus.READY && !speech.busy, icon = R.drawable.ic_speaker)
            speech.replayStatus?.takeIf { speech.replayRecordId == record.id }?.let {
                Text(if (it == PlaybackStatus.SPOKEN) "Putar ulang selesai dibacakan." else it.label,
                    style = MaterialTheme.typography.bodyMedium)
            }
            speech.error?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = QriskuColors.Error) }
            SecondaryAction("Buka DANA", onDana)
            InfoNotice("Data ini berasal dari notifikasi perangkat. Periksa DANA untuk konfirmasi transaksi.")
        }
    }
}

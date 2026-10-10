package id.qrisku.app.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class NotificationRepository(
    context: Context,
    scope: CoroutineScope,
    databaseName: String = "qrisku_notifications.db"
) {
    private val database = HistoryDatabase(context.applicationContext, databaseName)
    private val mutex = Mutex()
    private var recovered = false
    private var visibleLimit = 100
    private val mutableState = MutableStateFlow(HistoryState())
    val state = mutableState.asStateFlow()

    init { scope.launch { reload() } }

    suspend fun reload() = access { publish(it) }

    suspend fun loadMore() = access {
        visibleLimit += 100
        publish(it)
    }

    /** Insert history and deduplication marker atomically before attempting speech. */
    suspend fun record(record: NotificationRecord): Boolean? = access { db ->
        var inserted = false
        db.transaction {
            val marker = ContentValues().apply {
                put("id", record.id)
                put("detected_at", record.detectedAt)
            }
            val newMarker = db.insertWithOnConflict("seen_notifications", null, marker,
                SQLiteDatabase.CONFLICT_IGNORE) != -1L
            if (newMarker) {
                val values = ContentValues().apply {
                    put("id", record.id)
                    put("amount", record.amount)
                    put("detected_at", record.detectedAt)
                    put("posted_at", record.postedAt)
                    put("source_package", record.sourcePackage)
                    put("playback_status", record.playbackStatus.name)
                }
                // History still deduplicates an old event after its bounded marker was evicted.
                inserted = db.insertWithOnConflict("notifications", null, values,
                    SQLiteDatabase.CONFLICT_IGNORE) != -1L
                // Bound only the technical deduplication cache, not the user's history.
                db.execSQL("DELETE FROM seen_notifications WHERE id NOT IN " +
                    "(SELECT id FROM seen_notifications ORDER BY detected_at DESC, rowid DESC LIMIT 4096)")
            }
        }
        publish(db)
        inserted
    }

    suspend fun updatePlayback(id: String, status: PlaybackStatus) = access { db ->
        db.query("notifications", arrayOf("playback_status"), "id = ?", arrayOf(id),
            null, null, null).use { cursor ->
            if (cursor.moveToFirst()) {
                val previous = PlaybackStatus.valueOf(cursor.getString(0))
                if (PlaybackRules.canTransition(previous, status)) {
                    val values = ContentValues().apply { put("playback_status", status.name) }
                    db.update("notifications", values, "id = ?", arrayOf(id))
                }
            }
        }
        // A late TTS callback updates existing rows only; it cannot resurrect deleted history.
        publish(db)
    }

    suspend fun clearHistory() = access { db ->
        db.delete("notifications", null, null)
        visibleLimit = 100
        publish(db)
    }

    suspend fun close() = withContext(Dispatchers.IO) {
        mutex.withLock { database.close() }
    }

    private suspend fun <T> access(block: (SQLiteDatabase) -> T): T? = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                val db = database.writableDatabase
                if (!recovered) {
                    db.execSQL("UPDATE notifications SET playback_status = ? " +
                        "WHERE playback_status IN (?, ?)", arrayOf(PlaybackStatus.INTERRUPTED.name,
                        PlaybackStatus.QUEUED.name, PlaybackStatus.SPEAKING.name))
                    recovered = true
                }
                block(db)
            } catch (_: android.database.SQLException) {
                mutableState.value = mutableState.value.copy(loading = false,
                    error = "Riwayat belum dapat disimpan atau dibaca. Periksa ruang penyimpanan lalu coba lagi.")
                null
            }
        }
    }

    private fun publish(db: SQLiteDatabase) {
        val records = mutableListOf<NotificationRecord>()
        db.query("notifications", null, null, null, null, null,
            "detected_at DESC, rowid DESC", visibleLimit.toString()).use { cursor ->
            while (cursor.moveToNext()) {
                records += NotificationRecord(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    amount = cursor.getLong(cursor.getColumnIndexOrThrow("amount")),
                    detectedAt = cursor.getLong(cursor.getColumnIndexOrThrow("detected_at")),
                    postedAt = cursor.getLong(cursor.getColumnIndexOrThrow("posted_at")),
                    sourcePackage = cursor.getString(cursor.getColumnIndexOrThrow("source_package")),
                    playbackStatus = PlaybackStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("playback_status")))
                )
            }
        }
        val count = db.rawQuery("SELECT COUNT(*) FROM notifications", null).use {
            it.moveToFirst()
            it.getInt(0)
        }
        mutableState.value = HistoryState(records, count, loading = false)
    }

    private class HistoryDatabase(context: Context, name: String) : SQLiteOpenHelper(context, name, null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE notifications (id TEXT PRIMARY KEY NOT NULL, " +
                "amount INTEGER NOT NULL CHECK(amount > 0), detected_at INTEGER NOT NULL, " +
                "posted_at INTEGER NOT NULL, source_package TEXT NOT NULL, playback_status TEXT NOT NULL)")
            db.execSQL("CREATE INDEX notification_time ON notifications(detected_at DESC)")
            db.execSQL("CREATE TABLE seen_notifications (id TEXT PRIMARY KEY NOT NULL, detected_at INTEGER NOT NULL)")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            throw android.database.SQLException("A non-destructive migration is required for schema $newVersion")
        }
    }
}

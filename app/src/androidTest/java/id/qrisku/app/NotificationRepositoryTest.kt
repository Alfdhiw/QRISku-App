package id.qrisku.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import id.qrisku.app.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class NotificationRepositoryTest {
    private lateinit var context: Context
    private lateinit var name: String
    private lateinit var scope: CoroutineScope
    private lateinit var repository: NotificationRepository

    @Before fun prepare() = runBlocking {
        context = ApplicationProvider.getApplicationContext()
        name = "qrisku_test_" + UUID.randomUUID().toString() + ".db"
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        repository = NotificationRepository(context, scope, name)
        repository.reload()
        Unit
    }

    @After fun clean() = runBlocking {
        scope.cancel()
        repository.close()
        context.deleteDatabase(name)
        Unit
    }

    @Test fun recordsMutedNotificationsAndOrdersNewestFirst() = runBlocking {
        assertEquals(true, repository.record(record("first", 100, PlaybackStatus.MUTED)))
        assertEquals(true, repository.record(record("second", 200, PlaybackStatus.MUTED)))
        assertEquals(listOf("second", "first"), repository.state.value.records.map { it.id })
        assertEquals(PlaybackStatus.MUTED, repository.state.value.records.first().playbackStatus)
    }

    @Test fun duplicatesAreRejectedButEqualAmountsRemainSeparate() = runBlocking {
        val first = record("first", 100, PlaybackStatus.MUTED)
        assertEquals(true, repository.record(first))
        assertEquals(false, repository.record(first))
        assertEquals(true, repository.record(record("second", 101, PlaybackStatus.MUTED)))
        assertEquals(2, repository.state.value.totalCount)
    }

    @Test fun completedCallbackPersistsAndCannotRegress() = runBlocking {
        repository.record(record("first", 100, PlaybackStatus.QUEUED))
        repository.updatePlayback("first", PlaybackStatus.SPEAKING)
        assertEquals(PlaybackStatus.SPEAKING, repository.state.value.records.single().playbackStatus)
        repository.updatePlayback("first", PlaybackStatus.SPOKEN)
        repository.updatePlayback("first", PlaybackStatus.SPEAKING)
        assertEquals(PlaybackStatus.SPOKEN, repository.state.value.records.single().playbackStatus)
    }

    @Test fun existingHistoryDeduplicatesAfterCacheMarkerEviction() = runBlocking {
        val item = record("first", 100, PlaybackStatus.MUTED)
        repository.record(item)
        context.openOrCreateDatabase(name, Context.MODE_PRIVATE, null).use {
            it.delete("seen_notifications", "id = ?", arrayOf(item.id))
        }
        assertEquals(false, repository.record(item))
        assertEquals(1, repository.state.value.totalCount)
        assertNull(repository.state.value.error)
    }

    @Test fun restartKeepsHistoryAndMarksIncompleteSpeechInterrupted() = runBlocking {
        val first = record("queued", 100, PlaybackStatus.QUEUED)
        repository.record(first)
        repository.record(record("complete", 101, PlaybackStatus.QUEUED))
        repository.updatePlayback("complete", PlaybackStatus.SPOKEN)
        scope.cancel()
        repository.close()
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        repository = NotificationRepository(context, scope, name)
        repository.reload()
        assertEquals(2, repository.state.value.totalCount)
        assertEquals(PlaybackStatus.INTERRUPTED, repository.state.value.records.first { it.id == "queued" }.playbackStatus)
        assertEquals(PlaybackStatus.SPOKEN, repository.state.value.records.first { it.id == "complete" }.playbackStatus)
        assertEquals(false, repository.record(first))
    }

    @Test fun deletionDoesNotAllowLateCallbacksToResurrectRows() = runBlocking {
        val item = record("first", 100, PlaybackStatus.QUEUED)
        repository.record(item)
        repository.clearHistory()
        repository.updatePlayback("first", PlaybackStatus.SPOKEN)
        assertTrue(repository.state.value.records.isEmpty())
        assertEquals(0, repository.state.value.totalCount)
        assertEquals(false, repository.record(item))
    }

    @Test fun historyLoadsInPagesWithoutDiscardingOlderRecords() = runBlocking {
        repeat(105) { repository.record(record("record-$it", it.toLong(), PlaybackStatus.MUTED)) }
        assertEquals(100, repository.state.value.records.size)
        assertEquals(105, repository.state.value.totalCount)
        repository.loadMore()
        assertEquals(105, repository.state.value.records.size)
    }

    private fun record(id: String, time: Long, status: PlaybackStatus) =
        NotificationRecord(id, 29_000, time, time, playbackStatus = status)
}

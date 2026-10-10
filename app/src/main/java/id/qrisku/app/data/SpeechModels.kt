package id.qrisku.app.data

enum class EngineStatus { INITIALIZING, READY, UNAVAILABLE }
enum class SpeechKind { AUTOMATIC, TEST, REPLAY }

data class SpeechRequest(
    val id: String,
    val message: String,
    val kind: SpeechKind,
    val recordId: String? = null
)

data class SpeechState(
    val engine: EngineStatus = EngineStatus.INITIALIZING,
    val testStatus: PlaybackStatus? = null,
    val replayStatus: PlaybackStatus? = null,
    val replayRecordId: String? = null,
    val busy: Boolean = false,
    val error: String? = null
)

package id.qrisku.app

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.media.AudioAttributes
import android.speech.tts.UtteranceProgressListener
import id.qrisku.app.data.EngineStatus
import id.qrisku.app.data.PlaybackStatus
import id.qrisku.app.data.SpeechKind
import id.qrisku.app.data.SpeechRequest
import id.qrisku.app.data.SpeechState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.ArrayDeque
import java.util.Locale

/** One process-wide, offline TTS queue, shared by UI tests, replays, and the listener. */
class VoiceEngine(
    private val context: Context,
    private val onPlayback: (SpeechRequest, PlaybackStatus) -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private val pending = ArrayDeque<SpeechRequest>()
    private var current: SpeechRequest? = null
    private var tts: TextToSpeech? = null
    private var released = false
    private var generation = 0
    private var rate = 1f
    private val mutableState = MutableStateFlow(SpeechState())
    val state = mutableState.asStateFlow()
    private var speechTimeout: Runnable? = null
    private var initializationTimeout: Runnable? = null

    init { initialize() }

    private fun initialize() {
        generation++
        val attempt = generation
        mutableState.value = mutableState.value.copy(engine = EngineStatus.INITIALIZING, error = null)
        initializationTimeout = Runnable {
            if (attempt == generation && mutableState.value.engine == EngineStatus.INITIALIZING) {
                failEngine("Mesin suara belum merespons. Periksa pengaturan suara lalu coba lagi.")
            }
        }.also { handler.postDelayed(it, 20_000) }
        tts = TextToSpeech(context.applicationContext) { result ->
            handler.post {
                if (released || attempt != generation || mutableState.value.engine != EngineStatus.INITIALIZING) return@post
                initializationTimeout?.let(handler::removeCallbacks)
                if (result != TextToSpeech.SUCCESS) {
                    failEngine("Suara belum siap. Periksa mesin Text-to-Speech di pengaturan perangkat.")
                    return@post
                }
                val engine = tts ?: return@post
                val availability = engine.setLanguage(Locale.forLanguageTag("id-ID"))
                if (availability == TextToSpeech.LANG_MISSING_DATA ||
                    availability == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    failEngine("Suara Bahasa Indonesia belum tersedia. Pasang paket suara di pengaturan perangkat.")
                    return@post
                }
                val offlineVoice = engine.voices?.filter {
                    it.locale.language == Locale.forLanguageTag("id-ID").language &&
                        !it.isNetworkConnectionRequired &&
                        !it.features.orEmpty().contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)
                }?.sortedWith(compareByDescending<android.speech.tts.Voice> { it.quality }
                    .thenBy { it.name })?.firstOrNull()
                if (offlineVoice == null || engine.setVoice(offlineVoice) != TextToSpeech.SUCCESS) {
                    failEngine("Suara Bahasa Indonesia offline belum tersedia. Pasang paket suara lokal lalu coba lagi.")
                    return@post
                }
                engine.setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build())
                engine.setSpeechRate(rate)
                engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) = deliver(utteranceId, PlaybackStatus.SPEAKING)
                    override fun onDone(utteranceId: String?) = deliver(utteranceId, PlaybackStatus.SPOKEN)
                    @Deprecated("Android legacy callback")
                    override fun onError(utteranceId: String?) = deliver(utteranceId, PlaybackStatus.FAILED)
                    override fun onError(utteranceId: String?, errorCode: Int) = deliver(utteranceId, PlaybackStatus.FAILED)
                    override fun onStop(utteranceId: String?, interrupted: Boolean) =
                        deliver(utteranceId, PlaybackStatus.INTERRUPTED)
                })
                mutableState.value = mutableState.value.copy(engine = EngineStatus.READY, error = null)
                pump()
            }
        }
    }

    fun speak(request: SpeechRequest) {
        handler.post {
            if (released || mutableState.value.engine == EngineStatus.UNAVAILABLE) {
                publish(request, PlaybackStatus.FAILED)
                return@post
            }
            if (pending.size >= 64) {
                publish(request, PlaybackStatus.FAILED)
                mutableState.value = mutableState.value.copy(error = "Antrean suara penuh. Periksa volume dan mesin suara.")
                return@post
            }
            pending.addLast(request)
            publish(request, PlaybackStatus.QUEUED)
            pump()
        }
    }

    fun setSpeechRate(value: Float) {
        handler.post {
            rate = value.coerceIn(0.8f, 1.2f)
            tts?.setSpeechRate(rate)
        }
    }

    private fun pump() {
        if (released || current != null || mutableState.value.engine != EngineStatus.READY) return
        val request = pending.pollFirst() ?: run {
            mutableState.value = mutableState.value.copy(busy = false)
            return
        }
        current = request
        tts?.setSpeechRate(rate)
        val result = tts?.speak(request.message, TextToSpeech.QUEUE_FLUSH, null, request.id)
        if (result != TextToSpeech.SUCCESS) {
            current = null
            publish(request, PlaybackStatus.FAILED)
            pump()
        } else {
            speechTimeout = Runnable {
                if (current?.id == request.id) {
                    failEngine("Pembacaan suara tidak selesai. Periksa mesin suara lalu coba lagi.")
                }
            }.also { handler.postDelayed(it, 45_000) }
        }
    }

    private fun deliver(id: String?, status: PlaybackStatus) {
        handler.post {
            val request = current?.takeIf { it.id == id } ?: return@post
            if (status == PlaybackStatus.SPEAKING) {
                publish(request, status)
            } else {
                speechTimeout?.let(handler::removeCallbacks)
                current = null
                publish(request, status)
                pump()
            }
        }
    }

    fun stopAutomatic() = stop(automaticOnly = true)
    fun stopAll() = stop(automaticOnly = false)

    private fun stop(automaticOnly: Boolean) {
        handler.post {
            val cancelled = pending.filter { !automaticOnly || it.kind == SpeechKind.AUTOMATIC }
            pending.removeAll(cancelled.toSet())
            cancelled.forEach { publish(it, PlaybackStatus.INTERRUPTED) }
            current?.takeIf { !automaticOnly || it.kind == SpeechKind.AUTOMATIC }?.let {
                current = null
                speechTimeout?.let(handler::removeCallbacks)
                tts?.stop()
                publish(it, PlaybackStatus.INTERRUPTED)
            }
            pump()
        }
    }

    fun retry() {
        handler.post {
            if (released || mutableState.value.engine != EngineStatus.UNAVAILABLE) return@post
            tts?.shutdown()
            tts = null
            initialize()
        }
    }

    private fun failEngine(message: String) {
        initializationTimeout?.let(handler::removeCallbacks)
        speechTimeout?.let(handler::removeCallbacks)
        val stopped = listOfNotNull(current) + pending.toList()
        current = null
        pending.clear()
        tts?.stop()
        stopped.forEach { publish(it, PlaybackStatus.FAILED) }
        mutableState.value = mutableState.value.copy(engine = EngineStatus.UNAVAILABLE, busy = false, error = message)
    }

    private fun publish(request: SpeechRequest, status: PlaybackStatus) {
        mutableState.value = mutableState.value.copy(
            testStatus = if (request.kind == SpeechKind.TEST) status else mutableState.value.testStatus,
            replayStatus = if (request.kind == SpeechKind.REPLAY) status else mutableState.value.replayStatus,
            replayRecordId = if (request.kind == SpeechKind.REPLAY) request.recordId else mutableState.value.replayRecordId,
            busy = current != null || pending.isNotEmpty(),
            error = if (status == PlaybackStatus.FAILED) "Suara gagal dibacakan. Periksa volume dan pengaturan suara."
                else if (status == PlaybackStatus.SPOKEN) null else mutableState.value.error
        )
        onPlayback(request, status)
    }

    fun shutdown() {
        handler.post {
            if (released) return@post
            failEngine("Mesin suara dihentikan.")
            released = true
            tts?.shutdown()
            tts = null
        }
    }
}

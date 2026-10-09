package id.qrisku.app

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.ArrayDeque
import java.util.Locale
import java.util.UUID

/** Menunggu TTS siap dan menambahkan suara ke antrean. */
class VoiceEngine(context: Context) {
    private val handler = Handler(Looper.getMainLooper())
    private val pending = ArrayDeque<String>()
    private var tts: TextToSpeech? = null
    private var isReady = false
    private var released = false

    init {
        tts = TextToSpeech(context.applicationContext) { result ->
            handler.post {
                if (released) return@post
                if (result != TextToSpeech.SUCCESS) {
                    Log.e("QriskuPoC", "Inisialisasi TTS gagal")
                    return@post
                }
                val availability = tts?.setLanguage(Locale.forLanguageTag("id-ID"))
                if (availability == TextToSpeech.LANG_MISSING_DATA ||
                    availability == TextToSpeech.LANG_NOT_SUPPORTED || availability == null
                ) {
                    Log.e("QriskuPoC", "Bahasa Indonesia tidak tersedia di engine TTS")
                    return@post
                }
                isReady = true
                while (pending.isNotEmpty()) speakNow(pending.removeFirst())
            }
        }
    }

    fun speak(message: String) {
        handler.post {
            if (released) return@post
            if (!isReady) pending.addLast(message) else speakNow(message)
        }
    }

    private fun speakNow(message: String) {
        val result = tts?.speak(message, TextToSpeech.QUEUE_ADD, null, UUID.randomUUID().toString())
        if (result != TextToSpeech.SUCCESS) Log.e("QriskuPoC", "Gagal mengantrikan ucapan")
    }

    fun shutdown() {
        handler.post {
            if (released) return@post
            released = true
            pending.clear()
            tts?.stop()
            tts?.shutdown()
            tts = null
        }
    }
}

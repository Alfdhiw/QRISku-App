package id.qrisku.app.data

object SpeechAdmission {
    fun accepts(kind: SpeechKind, busy: Boolean): Boolean = kind == SpeechKind.AUTOMATIC || !busy
}

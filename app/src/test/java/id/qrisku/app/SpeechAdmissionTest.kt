package id.qrisku.app

import id.qrisku.app.data.SpeechAdmission
import id.qrisku.app.data.SpeechKind
import org.junit.Assert.*
import org.junit.Test

class SpeechAdmissionTest {
    @Test fun manualRequestsAreRejectedWhileAnySpeechIsPending() {
        assertFalse(SpeechAdmission.accepts(SpeechKind.TEST, busy = true))
        assertFalse(SpeechAdmission.accepts(SpeechKind.REPLAY, busy = true))
    }

    @Test fun manualRequestsAreAcceptedWhenIdle() {
        assertTrue(SpeechAdmission.accepts(SpeechKind.TEST, busy = false))
        assertTrue(SpeechAdmission.accepts(SpeechKind.REPLAY, busy = false))
    }

    @Test fun incomingPaymentsStillQueueWhileSpeechIsBusy() {
        assertTrue(SpeechAdmission.accepts(SpeechKind.AUTOMATIC, busy = true))
        assertTrue(SpeechAdmission.accepts(SpeechKind.AUTOMATIC, busy = false))
    }
}

package id.qrisku.app

import id.qrisku.app.data.*
import org.junit.Assert.*
import org.junit.Test

class NotificationStateTest {
    @Test fun permissionIsRequiredEvenWithStaleConnectedState() {
        assertEquals(MonitoringStatus.PERMISSION_REQUIRED, monitoringStatus(false, true, true))
    }

    @Test fun permissionAloneDoesNotClaimMonitoringIsActive() {
        assertEquals(MonitoringStatus.NEEDS_ATTENTION, monitoringStatus(true, false, true))
    }

    @Test fun disablingAutomaticVoiceKeepsMonitoringActive() {
        assertEquals(MonitoringStatus.ACTIVE_MUTED, monitoringStatus(true, true, false))
    }

    @Test fun connectedListenerAndPermissionEnableMonitoring() {
        assertEquals(MonitoringStatus.ACTIVE, monitoringStatus(true, true, true))
    }

    @Test fun queuedSpeechIsNotCompletedSpeech() {
        assertNotEquals(PlaybackStatus.SPOKEN, PlaybackStatus.QUEUED)
        assertTrue(PlaybackRules.canTransition(PlaybackStatus.QUEUED, PlaybackStatus.SPEAKING))
        assertTrue(PlaybackRules.canTransition(PlaybackStatus.SPEAKING, PlaybackStatus.SPOKEN))
    }

    @Test fun terminalPlaybackCannotBeOverwrittenByLateCallbacks() {
        listOf(PlaybackStatus.SPOKEN, PlaybackStatus.FAILED, PlaybackStatus.MUTED,
            PlaybackStatus.INTERRUPTED).forEach { terminal ->
            assertFalse(PlaybackRules.canTransition(terminal, PlaybackStatus.SPEAKING))
            assertFalse(PlaybackRules.canTransition(terminal, PlaybackStatus.SPOKEN))
        }
    }

    @Test fun failedAndStoppedUtterancesDoNotBecomeSpoken() {
        assertTrue(PlaybackRules.canTransition(PlaybackStatus.QUEUED, PlaybackStatus.FAILED))
        assertTrue(PlaybackRules.canTransition(PlaybackStatus.SPEAKING, PlaybackStatus.INTERRUPTED))
        assertFalse(PlaybackRules.canTransition(PlaybackStatus.SPEAKING, PlaybackStatus.QUEUED))
    }

    @Test fun interruptedProcessDoesNotResumeOldSpeechAsNewPayment() {
        assertEquals(PlaybackStatus.INTERRUPTED, PlaybackRules.afterRestart(PlaybackStatus.QUEUED))
        assertEquals(PlaybackStatus.INTERRUPTED, PlaybackRules.afterRestart(PlaybackStatus.SPEAKING))
        assertEquals(PlaybackStatus.SPOKEN, PlaybackRules.afterRestart(PlaybackStatus.SPOKEN))
    }

    @Test fun identicalNotificationHasStableNonSensitiveIdentity() {
        val id = identity("key", 123)
        assertEquals(id, identity("key", 123))
        assertEquals(64, id.length)
        assertTrue(id.matches(Regex("[0-9a-f]{64}")))
        assertFalse(id.contains("29.000"))
    }

    @Test fun separatePaymentsWithSameAmountRemainSeparate() {
        assertNotEquals(identity("key-a", 123), identity("key-b", 123))
        assertNotEquals(identity("key", 123), identity("key", 124))
    }

    @Test fun fieldSeparatorsPreventAmbiguousIdentityConcatenation() {
        assertNotEquals(NotificationIdentity.create("a", "bc", 123, "d", "e"),
            NotificationIdentity.create("ab", "c", 123, "d", "e"))
    }

    private fun identity(key: String, time: Long) = NotificationIdentity.create(PaymentSource.PACKAGE,
        key, time, "Pembayaran Masuk", "Rp29.000 diterima DANA Bisnis.")
}

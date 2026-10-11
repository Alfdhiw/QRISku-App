package id.qrisku.app

import id.qrisku.app.data.ListenerReconnectPolicy
import org.junit.Assert.*
import org.junit.Test

class ListenerReconnectPolicyTest {
    @Test fun neverRequestsWithoutPermission() {
        assertFalse(ListenerReconnectPolicy().shouldRequest(false, false, 0))
    }

    @Test fun neverRebindsAConnectedListener() {
        assertFalse(ListenerReconnectPolicy().shouldRequest(true, true, 0))
    }

    @Test fun disconnectedListenerRequestsImmediatelyThenThrottles() {
        val policy = ListenerReconnectPolicy()
        assertTrue(policy.shouldRequest(true, false, 0))
        assertFalse(policy.shouldRequest(true, false, 2_000))
        assertFalse(policy.shouldRequest(true, false, 29_999))
        assertTrue(policy.shouldRequest(true, false, 30_000))
    }

    @Test fun newPermissionGrantCanRecoverWithoutWaitingForOldAttempt() {
        val policy = ListenerReconnectPolicy()
        assertTrue(policy.shouldRequest(true, false, 0))
        assertFalse(policy.shouldRequest(false, false, 1_000))
        assertTrue(policy.shouldRequest(true, false, 2_000))
    }

    @Test fun reconnectionResetsBackoffForTheNextDisconnection() {
        val policy = ListenerReconnectPolicy()
        assertTrue(policy.shouldRequest(true, false, 0))
        assertFalse(policy.shouldRequest(true, true, 1_000))
        assertTrue(policy.shouldRequest(true, false, 2_000))
    }
}

package id.qrisku.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import id.qrisku.app.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class AppControllerTest {
    @Test fun listenerCallbackSurvivesDelayedPermissionVisibility() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "qrisku_connection_test_" + UUID.randomUUID().toString()
        var access = false
        val controller = AppController(context, "$name.db", name, notificationAccessReader = { access })
        try {
            controller.listenerConnected(true)
            assertFalse(controller.device.value.notificationAccess)
            assertFalse(controller.device.value.listenerConnected)
            access = true
            controller.refreshDevice()
            assertTrue(controller.device.value.listenerConnected)
            access = false
            controller.refreshDevice()
            assertFalse(controller.device.value.listenerConnected)
            controller.listenerConnected(false)
            access = true
            controller.refreshDevice()
            assertFalse(controller.device.value.listenerConnected)
        } finally {
            controller.close()
            context.deleteDatabase("$name.db")
            context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear().commit()
        }
    }

    @Test fun notificationPipelineFiltersSourceAndFormatAndPersistsWhenMuted() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "qrisku_controller_test_" + UUID.randomUUID().toString()
        val controller = AppController(context, "$name.db", name)
        try {
            controller.setAutomatic(false)
            controller.notificationReceived("com.dana.id", "wrong-source", 1, "Pembayaran Masuk", "Rp29.000 diterima DANA Bisnis.")
            controller.notificationReceived(PaymentSource.PACKAGE, "wrong-format", 2, "Transfer Berhasil", "Rp29.000 diterima DANA Bisnis.")
            controller.notificationReceived(PaymentSource.PACKAGE, "first", 3, "Pembayaran Masuk", "Rp29.000 diterima DANA Bisnis.")
            withTimeout(5_000) { controller.history.state.first { it.totalCount == 1 } }
            assertEquals(29_000L, controller.history.state.value.records.single().amount)
            assertEquals(PlaybackStatus.MUTED, controller.history.state.value.records.single().playbackStatus)
            controller.notificationReceived(PaymentSource.PACKAGE, "first", 3, "Pembayaran Masuk", "Rp29.000 diterima DANA Bisnis.")
            controller.history.reload()
            assertEquals(1, controller.history.state.value.totalCount)
            controller.setSpeechRate(1.2f)
            controller.preferences.completeOnboarding(false)
            val restored = PreferencesRepository(context, name).state.value
            assertFalse(restored.automatic)
            assertTrue(restored.onboardingComplete)
            assertEquals(1.2f, restored.speechRate, 0f)
        } finally {
            controller.close()
            context.deleteDatabase("$name.db")
            context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear().commit()
        }
    }
}

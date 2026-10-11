package id.qrisku.app

import android.content.ComponentName
import android.content.Context
import android.app.NotificationManager
import android.os.Build
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationAccessSettingsTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test fun permissionReaderMatchesAndroidApprovalForOurOwnListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            assertEquals(context.getSystemService(NotificationManager::class.java)
                .isNotificationListenerAccessGranted(ComponentName(context, QriskuNotificationListener::class.java)),
                NotificationAccessSettings.isGranted(context))
        } else {
            val granted = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
                .orEmpty().split(':').any {
                    ComponentName.unflattenFromString(it) == ComponentName(context, QriskuNotificationListener::class.java)
                }
            assertEquals(granted, NotificationAccessSettings.isGranted(context))
        }
    }

    @Test fun supportedAndroidOpensOnlyOurListenerFirst() {
        val intents = NotificationAccessSettings.permissionScreens(context, sdk = 30)
        assertEquals(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS, intents[0].action)
        assertEquals(ComponentName(context, QriskuNotificationListener::class.java).flattenToString(),
            intents[0].getStringExtra(Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME))
        assertEquals(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS, intents[1].action)
        assertEquals("package:${context.packageName}", intents[2].data.toString())
    }

    @Test fun androidEightUsesGeneralSettingsWithApplicationInfoFallback() {
        val intents = NotificationAccessSettings.permissionScreens(context, sdk = 26)
        assertEquals(2, intents.size)
        assertEquals(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS, intents[0].action)
        assertEquals(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, intents[1].action)
        assertEquals("package:${context.packageName}", intents[1].data.toString())
    }

    @Test fun recoveryInfoScreenTargetsOnlyOurPackage() {
        val intent = NotificationAccessSettings.applicationInfo(context.packageName)
        assertEquals(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, intent.action)
        assertEquals("package:${context.packageName}", intent.data.toString())
    }

    @Test fun hyperOsRecoveryOffersPublicApplicationListScreens() {
        val screens = NotificationAccessSettings.applicationListScreens()
        assertEquals(Settings.ACTION_APPLICATION_SETTINGS, screens[0].action)
        assertEquals(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS, screens[1].action)
        assertTrue(screens.all { it.component == null })
    }
}

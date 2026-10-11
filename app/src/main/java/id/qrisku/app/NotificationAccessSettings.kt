package id.qrisku.app

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/** Public Settings screens only: restricted settings still require the user's OS consent. */
object NotificationAccessSettings {
    fun applicationInfo(packageName: String) = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null))

    // On HyperOS the package details deep link can hide the restricted-settings menu,
    // while Android's application list exposes the normal, user-confirmed recovery flow.
    fun applicationListScreens() = listOf(Intent(Settings.ACTION_APPLICATION_SETTINGS),
        Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS))

    fun isGranted(context: Context): Boolean {
        val listener = ComponentName(context, QriskuNotificationListener::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            context.getSystemService(NotificationManager::class.java)
                .isNotificationListenerAccessGranted(listener)
        } else {
            Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
                .orEmpty().split(':').any { ComponentName.unflattenFromString(it) == listener }
        }
    }

    // These are inlined string constants, used only when the SDK guard allows the detail screen.
    // An explicit sdk also lets regression tests verify the API 26 fallback without an emulator.
    @SuppressLint("InlinedApi")
    fun permissionScreens(context: Context, sdk: Int = Build.VERSION.SDK_INT): List<Intent> {
        val detail = if (sdk >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS).putExtra(
                Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
                ComponentName(context, QriskuNotificationListener::class.java).flattenToString())
        } else null
        return listOfNotNull(detail, Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
            applicationInfo(context.packageName))
    }
}

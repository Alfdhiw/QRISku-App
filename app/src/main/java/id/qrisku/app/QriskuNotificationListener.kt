package id.qrisku.app

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import id.qrisku.app.data.PaymentSource

class QriskuNotificationListener : NotificationListenerService() {
    private val controller get() = (application as QriskuApplication).controller

    override fun onListenerConnected() {
        super.onListenerConnected()
        controller.listenerConnected(true)
        Log.i("Qrisku", "Notification listener terhubung")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Cek package asli, bukan ikon/nama tampilan yang mudah ditiru.
        if (sbn.packageName != PaymentSource.PACKAGE) return
        if (sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) return

        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim()
        val body = (extras.getCharSequence(Notification.EXTRA_TEXT)
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT))?.toString()?.trim()
        controller.notificationReceived(sbn.packageName, sbn.key, sbn.postTime, title, body)
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        controller.listenerConnected(false)
    }

    override fun onDestroy() {
        controller.listenerConnected(false)
        super.onDestroy()
    }
}

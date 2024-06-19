package com.d479.xpenses

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.d479.xpenses.managers.LocationManager
import com.d479.xpenses.models.Category
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.Item
import com.d479.xpenses.models.User
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class XpensesApp : Application() {

    companion object {
        lateinit var realm: Realm
        const val CHANNEL_ID = "invoice_channel_id"
    }

    override fun onCreate() {
        super.onCreate()
        realm = Realm.open(
            configuration = RealmConfiguration.create(
                schema = setOf(
                    Item::class,
                    User::class,
                    Invoice::class,
                    Category::class
                )
            )
        )
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Invoice Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        LocationManager.initialize(this)
    }
}

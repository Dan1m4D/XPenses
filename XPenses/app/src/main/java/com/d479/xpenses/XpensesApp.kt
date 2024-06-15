package com.d479.xpenses

import android.app.Application
import com.d479.xpenses.models.Category
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.Item
import com.d479.xpenses.models.User
import com.d479.xpenses.signIn.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class XpensesApp: Application() {

    companion object {
        lateinit var realm: Realm
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
    }
}
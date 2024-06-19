package com.d479.xpenses.models

import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.util.Date

class Invoice: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var title: String = ""
    var date: RealmInstant = RealmInstant.now()
    var latitude: Double = 40.638076
    var longitude: Double = -8.653603
    var categoryId: ObjectId = ObjectId()
    var total: Double = 0.0
    var items: RealmList<Item> = realmListOf()
    var user: User? = null
}
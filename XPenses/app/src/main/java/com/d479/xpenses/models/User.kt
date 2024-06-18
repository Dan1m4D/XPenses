package com.d479.xpenses.models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class User() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var uid: String = ""
    var name: String = ""
    var photoUrl: String = ""
    var invoices: RealmList<Invoice> = realmListOf()
    var timestamp: RealmInstant = RealmInstant.now()
}
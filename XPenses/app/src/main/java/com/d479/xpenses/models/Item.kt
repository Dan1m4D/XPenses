package com.d479.xpenses.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Item: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var name: String = ""
    var price: Double = 0.0 
    var qty: Int = 1
}

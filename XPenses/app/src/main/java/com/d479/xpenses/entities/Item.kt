package com.d479.xpenses.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name:String,
    val price:Double,
    val quantity:Int
)

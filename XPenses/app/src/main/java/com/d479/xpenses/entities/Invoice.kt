package com.d479.xpenses.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Invoice(
    @PrimaryKey(autoGenerate = true)
    val id:String? = null,
    val date:String,
    val local:String,
    val commercialName:String,
    val category: Categories,
    val total:Double,
    val items:List<Item>,
    val user: User
)

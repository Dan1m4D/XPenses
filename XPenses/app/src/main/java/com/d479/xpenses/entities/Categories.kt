package com.d479.xpenses.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Categories(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = 0,
    val name: String,
    val color: String
)
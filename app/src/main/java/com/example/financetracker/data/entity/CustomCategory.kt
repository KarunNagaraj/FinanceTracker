package com.example.financetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="custom_categories")
data class CustomCategory(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val categoryName: String
)

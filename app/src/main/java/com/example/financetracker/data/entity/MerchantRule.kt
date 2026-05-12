package com.example.financetracker.data.entity



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "merchant_rules")
data class MerchantRule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val merchantPattern: String, // e.g., "ZEPTO"
    val category: String         // e.g., "Groceries"
)
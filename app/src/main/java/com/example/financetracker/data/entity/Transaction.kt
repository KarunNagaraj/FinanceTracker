package com.example.financetracker.data.entity



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rawDescription: String,
    val amount: Double,
    val timestamp: Long,
    val type: String, // "CREDIT" or "DEBIT"
    val category: String, // e.g., "Food", "Transport"
    val source: String, // "SMS", "CSV", or "MANUAL"
    val isManuallyCorrected: Boolean = false
)
package com.example.financetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_budgets")
data class MonthlyBudget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monthYear: String, // Format: "MM-YYYY"
    val totalBudgetLimit: Double,
    val incomeTarget: Double
)
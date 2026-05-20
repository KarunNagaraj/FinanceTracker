package com.example.financetracker.utils

import com.example.financetracker.data.repository.TransactionRepository
import java.util.Calendar

class TransactionClassifier(private val repository: TransactionRepository) {

    // LEVEL 2: Global Dictionary
    private val globalKeywords = mapOf(
        "SWIGGY" to "Food & Dining",
        "ZOMATO" to "Food & Dining",
        "ZEPTO" to "Groceries",
        "BLINKIT" to "Groceries",
        "IOCL" to "Fuel",
        "HPCL" to "Fuel",
        "UBER" to "Transport",
        "OLA" to "Transport",
        "AMAZON" to "Shopping",
        "D2D" to "Groceries"
    )

    suspend fun categorizeTransaction(merchant: String, amount: Double, timestamp: Long, type: String): String {
        val upperMerchant = merchant.uppercase()
        if (type=="CREDIT"){
            return "Income"
        }

        // LEVEL 1: User Memory (Highest Priority)
        val userRules = repository.getAllRules()
        for (rule in userRules) {
            if (upperMerchant.contains(rule.merchantPattern.uppercase())) {
                return rule.category
            }
        }

        // LEVEL 2: Global Dictionary
        for ((keyword, category) in globalKeywords) {
            if (upperMerchant.contains(keyword)) {
                return category
            }
        }

        // LEVEL 3: Contextual Heuristics
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY

        if (amount < 100.0) return "Petty"
        if (isWeekend && hourOfDay in 18..23 && amount > 500.0) return "Dining / Weekend"

        // LEVEL 4: Fallback
        return "Uncategorized"
    }
}
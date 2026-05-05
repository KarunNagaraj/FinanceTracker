package com.example.financetracker.utils



import android.util.Log
import com.example.financetracker.data.entity.Transaction
import java.util.regex.Pattern

object SmsParser {

    // Looks for "Rs.", "Rs", or "INR" followed by numbers and decimals
    private val amountRegex = Pattern.compile("(?i)(?:rs\\.?|inr)\\s*([0-9,]+\\.[0-9]{2}|[0-9,]+)")

    // Looks for text after "to", "at", or "info" to guess the merchant
    private val merchantRegex = Pattern.compile("(?i)(?:to|at|info\\/)\\s+([A-Za-z0-9\\s@*]+?)(?=\\.|\\n| UPI| REF)")

    fun parseMessage(messageBody: String, timestamp: Long): Transaction? {
        val lowercaseMsg = messageBody.lowercase()

        // 1. Is this actually a bank transaction?
        val isDebit = lowercaseMsg.contains("debited") || lowercaseMsg.contains("spent") || lowercaseMsg.contains("sent")
        val isCredit = lowercaseMsg.contains("credited") || lowercaseMsg.contains("received")

        if (!isDebit && !isCredit) {
            Log.d("SmsParser", "Ignored: Not a transaction message")
            return null
        }

        // 2. Extract the Amount
        val amountMatcher = amountRegex.matcher(messageBody)
        val amount = if (amountMatcher.find()) {
            amountMatcher.group(1)?.replace(",", "")?.toDoubleOrNull() ?: 0.0
        } else {
            Log.d("SmsParser", "Ignored: Could not find amount")
            return null
        }

        // 3. Extract the Merchant Name
        val merchantMatcher = merchantRegex.matcher(messageBody)
        val merchant = if (merchantMatcher.find()) {
            merchantMatcher.group(1)?.trim() ?: "Unknown Merchant"
        } else {
            "Unknown Merchant"
        }

        // 4. Return the beautifully parsed database object!
        return Transaction(
            rawDescription = merchant,
            amount = amount,
            timestamp = timestamp,
            type = if (isDebit) "DEBIT" else "CREDIT",
            category = "Uncategorized", // Our AI (Phase 5) will replace this!
            source = "SMS"
        )
    }
}
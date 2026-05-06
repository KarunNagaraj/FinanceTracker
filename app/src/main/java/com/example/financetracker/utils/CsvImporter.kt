package com.example.financetracker.utils



import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.financetracker.data.entity.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Locale

object CsvImporter {

    suspend fun parseAndImport(context: Context, uri: Uri, classifier: TransactionClassifier): List<Transaction> {
        return withContext(Dispatchers.IO) {
            val transactions = mutableListOf<Transaction>()
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val reader = BufferedReader(InputStreamReader(inputStream!!))

                // Matches the "01/04/26" format in your bank statement
                val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

                var line: String?
                var isTableStarted = false
                var lineNumber = 0 // 1. Let's track the line number

                while (reader.readLine().also { line = it } != null) {
                    lineNumber++

                    // 2. X-Ray the raw text the app is receiving
                    if (lineNumber < 25) { // Just log the first 25 lines so we don't spam the console
                        Log.d("CSV_DEBUG", "Line $lineNumber RAW: $line")
                    }

                    if (!isTableStarted) {
                        if (line!!.contains("Date") && line!!.contains("Narration")) {
                            isTableStarted = true
                            // 3. Confirm the trigger actually woke up
                            Log.d("CSV_DEBUG", "WAKE UP: Found Headers on line $lineNumber!")
                        }
                        continue
                    }

                    if (line!!.isBlank() || line!!.startsWith("*")) {
                        continue
                    }

                    val tokens = line!!.split(",")

                    // 4. X-Ray the columns. If size is 1, we know the commas are missing!
                    Log.d("CSV_DEBUG", "Row Data -> Column Count: ${tokens.size} | Content: $tokens")

                    // HDFC format has at least 6 columns (up to Deposit Amt.)
                    if (tokens.size >= 6) {
                        val dateString = tokens[0].trim()
                        val merchant = tokens[1].trim()

                        // In this bank format, Withdrawal is Col 4, Deposit is Col 5
                        val withdrawalStr = tokens[4].trim()
                        val depositStr = tokens[5].trim()

                        // Convert "15/04/26" to a computer-readable Unix Timestamp
                        val timestamp = try {
                            dateFormat.parse(dateString)?.time ?: System.currentTimeMillis()
                        } catch (e: Exception) {
                            System.currentTimeMillis()
                        }

                        // Figure out if it was money in or money out
                        var amount = 0.0
                        var type = "DEBIT"

                        if (withdrawalStr.isNotEmpty()) {
                            amount = withdrawalStr.toDoubleOrNull() ?: 0.0
                            type = "DEBIT"
                        } else if (depositStr.isNotEmpty()) {
                            amount = depositStr.toDoubleOrNull() ?: 0.0
                            type = "CREDIT"
                        }

                        // 4. The Brain: Only process it if there is a valid amount
                        if (amount > 0.0 && merchant.isNotEmpty()) {
                            // Ask the ML model to categorize "ZEPTO" or "SWIGGY"
                            val smartCategory = classifier.predictCategory(merchant)

                            transactions.add(
                                Transaction(
                                    rawDescription = merchant,
                                    amount = amount,
                                    timestamp = timestamp,
                                    type = type,
                                    category = smartCategory,
                                    source = "CSV"
                                )
                            )
                        }
                    }
                }
                reader.close()
            } catch (e: Exception) {
                Log.e("CsvImporter", "Error reading CSV", e)
            }

            return@withContext transactions
        }
    }
}
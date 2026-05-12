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
/*Open CSV
   ↓
Read each row
   ↓
Ignore garbage rows
   ↓
Extract columns
   ↓
Convert text → usable values
   ↓
Build Transaction objects
   ↓
Return list*/

object CsvImporter {

    private const val TAG = "CsvImporter"
    private const val DEBUG_TAG = "CSV_DEBUG"

    suspend fun parseAndImport(
        context: Context,
        uri: Uri,
        classifier: TransactionClassifier
    ): List<Transaction> {

        return withContext(Dispatchers.IO) {

            val transactions = mutableListOf<Transaction>()

            try {
                val inputStream = context.contentResolver.openInputStream(uri)

                if (inputStream == null) {
                    Log.e(TAG, "Could not open input stream")
                    return@withContext transactions
                }

                val reader = BufferedReader(InputStreamReader(inputStream))

                val dateFormat = SimpleDateFormat(
                    "dd/MM/yy",
                    Locale.getDefault()
                )

                var isTableStarted = false
                var lineNumber = 0

                while (true) {

                    val line = reader.readLine() ?: break
                    lineNumber++

                    // Log first few raw lines for debugging
                    if (lineNumber < 25) {
                        Log.d(DEBUG_TAG, "Line $lineNumber RAW: $line")
                    }

                    // Wait until table headers are found
                    if (!isTableStarted) {

                        val hasHeaders =
                            line.contains("Date") &&
                                    line.contains("Narration")

                        if (hasHeaders) {
                            isTableStarted = true
                            Log.d(
                                DEBUG_TAG,
                                "Found headers on line $lineNumber"
                            )
                        }

                        continue
                    }

                    // Skip empty or footer lines
                    if (line.isBlank() || line.startsWith("*")) {
                        continue
                    }

                    val tokens = line.split(",")

                    Log.d(
                        DEBUG_TAG,
                        "Column Count: ${tokens.size} | Content: $tokens"
                    )

                    // Expected minimum columns:
                    // Date, Narration, Ref No, Value Date,
                    // Withdrawal, Deposit
                    if (tokens.size < 6) {
                        continue
                    }

                    val dateString = tokens[0].trim()
                    val merchant = tokens[1].trim()

                    val withdrawalString = tokens[4].trim()
                    val depositString = tokens[5].trim()

                    val timestamp = parseTimestamp(
                        dateString,
                        dateFormat
                    )

                    val transactionData = extractAmountAndType(
                        withdrawalString,
                        depositString
                    )

                    val amount = transactionData.first
                    val type = transactionData.second

                    // Ignore invalid rows
                    if (amount <= 0.0 || merchant.isEmpty()) {
                        continue
                    }

                    val smartCategory = classifier.categorizeTransaction(merchant, amount, timestamp)

                    val transaction = Transaction(
                        rawDescription = merchant,
                        amount = amount,
                        timestamp = timestamp,
                        type = type,
                        category = smartCategory,
                        source = "CSV"
                    )

                    transactions.add(transaction)
                }

                reader.close()

            } catch (e: Exception) {
                Log.e(TAG, "Error reading CSV", e)
            }

            transactions
        }
    }

    private fun parseTimestamp(
        dateString: String,
        dateFormat: SimpleDateFormat
    ): Long {

        return try {
            dateFormat.parse(dateString)?.time
                ?: System.currentTimeMillis()

        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    private fun extractAmountAndType(
        withdrawalString: String,
        depositString: String
    ): Pair<Double, String> {

        if (withdrawalString.isNotEmpty()) {

            val amount =
                withdrawalString.toDoubleOrNull() ?: 0.0

            return Pair(amount, "DEBIT")
        }

        if (depositString.isNotEmpty()) {

            val amount =
                depositString.toDoubleOrNull() ?: 0.0

            return Pair(amount, "CREDIT")
        }

        return Pair(0.0, "DEBIT")
    }
}
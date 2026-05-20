package com.example.financetracker.utils



import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.financetracker.data.AppDatabase
import com.example.financetracker.data.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//Android BroadcastReceiver is a System Entry Point. It is an invisible, background trigger that operates completely outside of your app's normal UI flow.
// It doesn't have a screen, and it doesn't have a ViewModel. Therefore, it is forced to act as the "Orchestrator"—it has to wake up, call the parser (SmsParser), and then call the database (Repository) itself
// because there is no other layer available to do it.
class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Double-check we are actually receiving an SMS
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {

            // Extract the messages from the intent
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            // Grab our database instance
            val database = AppDatabase.getDatabase(context)
            val repository = TransactionRepository(database.transactionDao(), database.merchantRuleDao(), database.customCategoryDao())
            val classifier = TransactionClassifier(repository)

            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    for (sms in messages) {
                        val body = sms.displayMessageBody
                        val timestamp = sms.timestampMillis

                        // Send it to the Regex parser
                        val parsedData = SmsParser.parseMessage(body, timestamp)

                        if (parsedData != null) {
                            // 2. Ask the ML model to categorize the merchant!
                            val smartCategory = classifier.categorizeTransaction(
                                merchant = parsedData.rawDescription,
                                amount = parsedData.amount,
                                timestamp = parsedData.timestamp,
                                type = parsedData.type
                            )

                            // 3. Create the final transaction with the smart category
                            val finalTransaction = parsedData.copy(category = smartCategory)

                            repository.insertTransaction(finalTransaction)
                            Log.d("SmsReceiver", "Saved: ${finalTransaction.rawDescription} as $smartCategory")
                        }
                    }} catch (e: Exception) {
                    Log.e("SmsReceiver", "Error parsing SMS", e)
                } finally {
                    // Tell Android we are done, so it can put the receiver back to sleep
                    pendingResult.finish()
                }
            }
        }
    }
}
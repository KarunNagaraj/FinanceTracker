package com.example.financetracker.utils


import android.content.Context
import android.util.Log
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier
import java.io.IOException

class TransactionClassifier(private val context: Context) {

    private var classifier: NLClassifier? = null

    init {
        setupModel()
    }

    private fun setupModel() {
        try {
            // This looks inside your app's 'assets' folder for the model
            classifier = NLClassifier.createFromFile(context, "model.tflite")
            Log.d("ML_Engine", "TFLite Model loaded successfully!")
        } catch (e: IOException) {
            Log.e("ML_Engine", "Failed to load TFLite model. Using fallback logic.", e)
            classifier = null
        }
    }

    fun predictCategory(rawDescription: String): String {
        // If the model is loaded, use it!
        classifier?.let { model ->
            val results = model.classify(rawDescription)
            // TFLite returns a list of probabilities. We want the one with the highest score.
            val topCategory = results.maxByOrNull { it.score }
            if (topCategory != null && topCategory.score > 0.5f) { // 50% confidence threshold
                return topCategory.label
            }
        }

        // --- FALLBACK LOGIC ---
        // If you haven't put a model.tflite file in your assets folder yet,
        // or the AI is unsure, we use a basic keyword fallback so your app still works today.
        val text = rawDescription.lowercase()
        return when {
            text.contains("zomato") || text.contains("swiggy") || text.contains("starbucks") -> "Food & Dining"
            text.contains("amazon") || text.contains("flipkart") || text.contains("myntra") -> "Shopping"
            text.contains("uber") || text.contains("ola") || text.contains("irctc") -> "Transport"
            text.contains("netflix") || text.contains("spotify") || text.contains("prime") -> "Entertainment"
            text.contains("jio") || text.contains("airtel") || text.contains("bescom") -> "Bills & Utilities"
            else -> "Other"
        }
    }
}
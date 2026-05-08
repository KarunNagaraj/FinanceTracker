package com.example.financetracker.utils

import android.content.Context

import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.channels.FileChannel

class TransactionClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null
    private var labels = listOf<String>()

    init {
        try {
            // 1. Read the labels.txt file so we know what the output numbers mean
            labels = context.assets.open("labels.txt").bufferedReader().readLines()

            // 2. Load the custom_brain.tflite file into memory
            val assetFileDescriptor = context.assets.openFd("custom_brain.tflite")
            val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
            val fileChannel = fileInputStream.channel
            val modelBuffer = fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.declaredLength
            )

            // 3. Fire up the TensorFlow engine
            val options = Interpreter.Options()
            interpreter = Interpreter(modelBuffer, options)

        } catch (e: Exception) {
            e.printStackTrace()
            // If the model fails to load, it will just safely fallback to "Other"
        }
    }

    fun predictCategory(merchant: String): String {
        if (interpreter == null || labels.isEmpty()) return "Other"

        // The model expects an Array of Strings as the input
        val input = arrayOf(merchant.uppercase())

        // The model outputs a 2D Array of probabilities (e.g. [0.01, 0.95, 0.04...])
        // The size of the output perfectly matches the number of categories in labels.txt
        val output = Array(1) { FloatArray(labels.size) }

        try {
            // 4. Run the data through the neural network!
            interpreter?.run(input, output)

            val probabilities = output[0]
            var highestProbability = 0f
            var winningIndex = -1

            // 5. Loop through the results to find the highest percentage
            for (i in probabilities.indices) {
                if (probabilities[i] > highestProbability) {
                    highestProbability = probabilities[i]
                    winningIndex = i
                }
            }

            // 6. If the AI is at least 30% confident, return the category.
            // Otherwise, it's too confusing, so dump it in "Other"
            if (winningIndex != -1 && highestProbability > 0.3f) {
                return labels[winningIndex]
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "Other"
    }
}
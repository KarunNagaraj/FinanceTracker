package com.example.financetracker.ui.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financetracker.data.entity.MerchantRule
import com.example.financetracker.data.entity.Transaction
import com.example.financetracker.data.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TransactionsViewModel(private val repository: TransactionRepository) : ViewModel() {

    // 1. A live stream of all transactions to feed the UI list
    val allTransactions: Flow<List<Transaction>> = repository.allTransactionsFlow

    // 2. The function triggered when the user clicks "Save" on the Bottom Sheet
    fun saveCategorization(transaction: Transaction, newCategory: String, alwaysRemember: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Instantly update the specific transaction the user tapped
                val updatedTransaction = transaction.copy(
                    category = newCategory,
                    isManuallyCorrected = true
                )
                repository.updateTransaction(updatedTransaction)

                // 2. If the checkbox was checked, do the Magic Trick!
                if (alwaysRemember) {
                    val newRule = MerchantRule(
                        merchantPattern = transaction.rawDescription,
                        category = newCategory
                    )
                    repository.insertRule(newRule)

                    repository.updatePastUncategorized(
                        merchantName = transaction.rawDescription,
                        newCategory = newCategory
                    )
                }
            } catch (e: Exception) {
                // If anything fails, print it to Logcat so we can see why it didn't update!
                e.printStackTrace()
            }
        }
    }
}

// Boilerplate factory to help Android build the ViewModel
class TransactionsViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

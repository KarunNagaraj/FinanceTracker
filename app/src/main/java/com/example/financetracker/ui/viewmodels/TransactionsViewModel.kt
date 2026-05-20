package com.example.financetracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financetracker.data.entity.CustomCategory
import com.example.financetracker.data.entity.MerchantRule
import com.example.financetracker.data.entity.Transaction
import com.example.financetracker.data.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionsViewModel(private val repository: TransactionRepository) : ViewModel() {

    // 1. The live stream of transactions
    val allTransactions: Flow<List<Transaction>> = repository.allTransactionsFlow

    // 2. The Un-deletable Base Categories
    val baseCategories = listOf(
        "Food & Dining", "Groceries", "Transport", "Fuel",
        "Shopping", "Entertainment", "Bills & Utilities", "Rent", "Health", "Other"
    )

    // 3. The Dynamic Combined List
    // We map the database Flow. Every time the DB changes, we extract the names and add them to the base list!
    val availableCategories = repository.allCustomCategoriesFlow.map { customList ->
        baseCategories + customList.map { it.categoryName }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), baseCategories)

    // --- Save Logic ---
    fun saveCategorization(transaction: Transaction, newCategory: String, alwaysRemember: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedTransaction = transaction.copy(category = newCategory, isManuallyCorrected = true)
                repository.updateTransaction(updatedTransaction)

                if (alwaysRemember) {
                    repository.insertRule(MerchantRule(merchantPattern = transaction.rawDescription, category = newCategory))
                    repository.updatePastUncategorized(transaction.rawDescription, newCategory)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- Custom Category Logic ---
    fun addNewCategory(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val cleanName = name.trim()
            // Don't add blank names or duplicates of our base list
            if (cleanName.isNotEmpty() && !baseCategories.contains(cleanName)) {
                repository.insertCustomCategory(CustomCategory(categoryName = cleanName))
            }
        }
    }

    fun deleteCustomCategory(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCustomCategoryByName(name)
        }
    }
}

class TransactionsViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
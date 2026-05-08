package com.example.financetracker.data.repository

import com.example.financetracker.data.dao.CategoryTotal
import com.example.financetracker.data.dao.TransactionDao
import com.example.financetracker.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactionsFlow: Flow<List<Transaction>> = transactionDao.getAllTransactionsFlow()
    val totalExpensesFlow: Flow<Double> = transactionDao.getTotalExpensesFlow()
    val totalIncomeFlow: Flow<Double> = transactionDao.getTotalIncomeFlow()

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }
    // NEW: Expose the grouped data
    fun getSpendingByCategory(): Flow<List<CategoryTotal>> {
        return transactionDao.getSpendingByCategory()
    }
}
package com.example.financetracker.data.repository

import com.example.financetracker.data.dao.CategoryTotal
import com.example.financetracker.data.dao.CustomCategoryDao
import com.example.financetracker.data.dao.MerchantRuleDao
import com.example.financetracker.data.dao.TransactionDao
import com.example.financetracker.data.entity.CustomCategory
import com.example.financetracker.data.entity.MerchantRule
import com.example.financetracker.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao,private val merchantRuleDao: MerchantRuleDao, private val customCategoryDao: CustomCategoryDao) {

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

    // NEW: Expose the bulk update
    suspend fun updatePastUncategorized(merchantName: String, newCategory: String) {
        transactionDao.updatePastUncategorized(merchantName, newCategory)
    }
    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }
    suspend fun insertRule(rule: MerchantRule) {
        merchantRuleDao.insertRule(rule)
    }

    suspend fun getAllRules(): List<MerchantRule> {
        return merchantRuleDao.getAllRules()
    }

    suspend fun insertCustomCategory(category: CustomCategory) {
        customCategoryDao.insertCustomCategory(category)
    }
    suspend fun deleteCustomCategory(category: CustomCategory) {
        customCategoryDao.deleteCustomCategory(category)
    }
    suspend fun deleteCustomCategoryByName(name: String) {
        customCategoryDao.deleteCustomCategoryByName(name)
    }
    val allCustomCategoriesFlow: Flow<List<CustomCategory>> = customCategoryDao.getAllCustomCategories()


}
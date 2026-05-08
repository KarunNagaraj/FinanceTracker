package com.example.financetracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financetracker.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)// functions are suspend because ?

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    // Gets all transactions ordered by newest first, constantly updating the UI
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<Transaction>>

    // COALESCE guarantees a 0.0 is returned if the table is empty,
    // preventing the Flow<Double?> KSP2 compiler crash.
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'DEBIT'")
    fun getTotalExpensesFlow(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'CREDIT'")
    fun getTotalIncomeFlow(): Flow<Double>
    // NEW: The Aggregation Query for the Chart
    @Query("SELECT category, SUM(amount) as totalAmount FROM transactions WHERE type = 'DEBIT' GROUP BY category ORDER BY totalAmount DESC")
    fun getSpendingByCategory(): Flow<List<CategoryTotal>>
}

// NEW: A simple container to hold the grouped result
data class CategoryTotal(
    val category: String,
    val totalAmount: Double
)

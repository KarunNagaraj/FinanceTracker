package com.example.financetracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.financetracker.data.entity.CustomCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomCategoryDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomCategory(category: CustomCategory)

    @Delete
    suspend fun deleteCustomCategory(category: CustomCategory)

    @Query("SELECT * FROM custom_categories ORDER BY categoryName ASC")
    fun getAllCustomCategories(): Flow<List<CustomCategory>>
    // Delete by name (much easier for the UI!)
    @Query("DELETE FROM custom_categories WHERE categoryName = :name")
    suspend fun deleteCustomCategoryByName(name: String)

}
package com.example.financetracker.data.dao



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.financetracker.data.entity.MerchantRule

@Dao
interface MerchantRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: MerchantRule)

    // Checks if the incoming merchant string contains any of our saved rules
    @Query("SELECT * FROM merchant_rules")
    suspend fun getAllRules(): List<MerchantRule>
}
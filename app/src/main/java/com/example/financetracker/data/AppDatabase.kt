package com.example.financetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.financetracker.data.dao.MerchantRuleDao
import com.example.financetracker.data.dao.TransactionDao
import com.example.financetracker.data.entity.MerchantRule
import com.example.financetracker.data.entity.Transaction

@Database(
    entities = [
        Transaction::class,
        MerchantRule::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    abstract fun merchantRuleDao(): MerchantRuleDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            // Return existing database if already created
            if (INSTANCE != null) {
                return INSTANCE!!
            }

            synchronized(this) { //synchronized since many threads may try to create instances at the same time

                // Double-check after entering synchronized block
                if (INSTANCE == null) {

                    val database = Room.databaseBuilder(
                        context.applicationContext, // exists for the lifetime of the app, not just a screen
                        AppDatabase::class.java,
                        "finance_database"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = database
                }
            }

            return INSTANCE!!
        }
    }
}
/*
    APP DATABASE FLOW (ROOM DATABASE)

    PURPOSE:
    This class acts as the central database holder for the app.
    It connects:
    - Entities (tables)
    - DAOs (database operations)
    - The actual SQLite database

    ------------------------------------------------------------
    HIGH LEVEL FLOW
    ------------------------------------------------------------

    App Code
        ↓
    AppDatabase.getDatabase(context)
        ↓
    Returns SINGLE shared database instance
        ↓
    Database provides DAO objects
        ↓
    DAO functions are called
        ↓
    Room converts DAO calls into SQL queries
        ↓
    SQLite database is updated/read

    ------------------------------------------------------------
    MAIN COMPONENTS
    ------------------------------------------------------------

    1. ENTITIES
       Defined in @Database(...)

       entities = [
           Transaction::class,
           MerchantRule::class
       ]

       These are the database tables.

       Example:
       Transaction entity
           ↓
       Creates "transactions" table in SQLite.

    ------------------------------------------------------------

    2. DAO FUNCTIONS

       abstract fun transactionDao(): TransactionDao
       abstract fun merchantRuleDao(): MerchantRuleDao

       These functions are NOT manually implemented.

       Room automatically generates their implementations
       during app build time.

       Example generated idea:

           override fun transactionDao(): TransactionDao {
               return TransactionDao_Impl()
           }

       DAO objects contain actual database operations:
       - insert
       - delete
       - update
       - query

    ------------------------------------------------------------

    3. SINGLETON DATABASE INSTANCE

       private var INSTANCE: AppDatabase? = null

       Only ONE database object should exist in the app.

       Why:
       - avoids memory waste
       - avoids multiple DB connections
       - avoids threading issues

    ------------------------------------------------------------

    4. @Volatile

       Ensures all threads always see latest INSTANCE value.

       Important because multiple threads may access database.

    ------------------------------------------------------------

    5. getDatabase(context)

       Main access point for database.

       FLOW:

       Step 1:
       Check if database already exists.

           if (INSTANCE != null)

       If yes:
           return existing database immediately.

       --------------------------------------------------------

       Step 2:
       If database does not exist:

           synchronized(this)

       Locks block so only ONE thread can create database.

       Prevents race conditions.

       --------------------------------------------------------

       Step 3:
       Double-check INSTANCE again inside synchronized block.

       Another thread may have already created database while
       current thread was waiting.

       --------------------------------------------------------

       Step 4:
       Build Room database.

           Room.databaseBuilder(...).build()

       This creates actual SQLite-backed Room database.

       --------------------------------------------------------

       Step 5:
       Store database in INSTANCE.

       Future calls reuse same object.

       --------------------------------------------------------

       Step 6:
       Return database instance.

        RUNTIME USAGE FLOW

        UI / Screen
        (Activity, Fragment, Compose Screen)
            ↓
        ViewModel
            ↓
        Repository
            ↓
        AppDatabase
            ↓
        DAO Object
            ↓
        Room ORM
            ↓
        SQLite Database

        ------------------------------------------------------------

        EXAMPLE FLOW

        User adds transaction from UI
            ↓
        ViewModel receives request
            ↓
        ViewModel calls:

            repository.insert(transaction)

            ↓

        Repository accesses database DAO:

            database.transactionDao()

            ↓

        Repository calls DAO function:

            transactionDao.insert(transaction)

            ↓

        Room automatically converts DAO call into SQL

            ↓

        SQLite executes query and stores data locally

    ------------------------------------------------------------
    IMPORTANT CONCEPT
    ------------------------------------------------------------

    This file mostly DECLARES database structure and access rules.

    Room automatically GENERATES:
    - SQL handling
    - DAO implementations
    - object mapping
    - database connection code

*/
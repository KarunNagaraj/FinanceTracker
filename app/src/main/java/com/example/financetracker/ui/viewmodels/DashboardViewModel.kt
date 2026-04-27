package com.example.financetracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financetracker.data.entity.Transaction
import com.example.financetracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: TransactionRepository) : ViewModel() {

    val totalExpenses: StateFlow<Double> = repository.totalExpensesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    //statein is used to convert flow into state flow. Its paramters are first where state flow is tied to the viewmodel,
    //meaning if viewmodel gets destroyed, it gets destroyed. Second paramter means that expose the data only while on the ui page
    // and if we leave that particular page using it then stop giving the data but wait 5 seconds before you do
    //lastly is just the inital value before the flow starts
    val totalIncome: StateFlow<Double> = repository.totalIncomeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    // purpose of stateflow and state in is:
    // current cached latest value - since Flow on its own may not have the data immediately ready
    //shared collection - multiple modules can access total expenses and total income without starting a new flow for each
    //immediate UI readiness
    //stable screen state model
    // technically using only flow could work but this is best practice apparently

    // Temporary function just to test if the UI reacts!
    fun addDummyTransaction() {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    rawDescription = "Test Coffee",
                    amount = 150.0,
                    timestamp = System.currentTimeMillis(),
                    type = "DEBIT",
                    category = "Food",
                    source = "MANUAL"
                )
            )
        }
    }
}

// Factory to tell Android how to build our ViewModel
class DashboardViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
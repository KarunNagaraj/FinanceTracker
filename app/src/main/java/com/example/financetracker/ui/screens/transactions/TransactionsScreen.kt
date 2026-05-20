package com.example.financetracker.ui.screens.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.data.entity.Transaction
import com.example.financetracker.ui.screens.transactions.components.CategorizeBottomSheet
import com.example.financetracker.ui.screens.transactions.components.TransactionRow
import com.example.financetracker.ui.theme.*
import com.example.financetracker.ui.viewmodels.TransactionsViewModel

// Avoid repeating raw string literals everywhere
private const val UNCATEGORIZED = "Uncategorized"

/**
 * Main entry point for the Transactions Screen.
 * Handles high-level state management and layout coordination.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel
) {
    // Convert Flow<List<Transaction>> into Compose state
    val transactions by viewModel
        .allTransactions
        .collectAsState(initial = emptyList())

    // Convert Flow<List<String>> into Compose state for dynamic categories
    val dynamicCategories by viewModel
        .availableCategories
        .collectAsState()

    // Stores currently selected transaction for categorization
    var selectedTransaction by remember {
        mutableStateOf<Transaction?>(null)
    }

    // Material3 bottom sheet state
    val sheetState = rememberModalBottomSheetState()

    // Main UI Layout
    Scaffold(
        topBar = { ScreenHeader() },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            TransactionsList(
                transactions = transactions,
                onTransactionClick = { transaction ->
                    // Only allow categorization for uncategorized transactions
                    if (transaction.category == UNCATEGORIZED) {
                        selectedTransaction = transaction
                    }
                }
            )
        }
    }

    // Modal UI: Categorization Bottom Sheet
    // Shown only when a transaction is selected
    if (selectedTransaction != null) {
        CategorizeBottomSheet(
            transaction = selectedTransaction!!,
            categories = dynamicCategories,
            baseCategories = viewModel.baseCategories,
            sheetState = sheetState,
            onDismiss = {
                selectedTransaction = null
            },
            onSave = { category, alwaysRemember ->
                viewModel.saveCategorization(
                    selectedTransaction!!,
                    category,
                    alwaysRemember
                )
                selectedTransaction = null
            },
            onAddCategory = { newCategory ->
                viewModel.addNewCategory(newCategory)
            },
            onDeleteCategory = { categoryToDelete ->
                viewModel.deleteCustomCategory(categoryToDelete)
            }
        )
    }
}

/**
 * Renders the top header section of the screen.
 */
@Composable
private fun ScreenHeader() {
    Text(
        text = "Transactions",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = BrandBlue,
        modifier = Modifier.padding(16.dp)
    )
}

/**
 * Renders a scrollable list of transactions.
 * Uses LazyColumn for optimized rendering of large datasets.
 */
@Composable
private fun TransactionsList(
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(
            items = transactions,
            key = { it.id } // Use ID for better performance during list updates
        ) { transaction ->
            TransactionRow(
                transaction = transaction,
                onClick = { onTransactionClick(transaction) }
            )
        }
    }
}

package com.example.financetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.data.entity.Transaction
import com.example.financetracker.ui.theme.*
import com.example.financetracker.ui.viewmodels.TransactionsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: TransactionsViewModel) {
    // 1. Observe the live list of transactions from the database
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())

    // 2. State variables to control the Bottom Sheet
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Column(modifier = Modifier.fillMaxSize().background(BackgroundLight).padding(16.dp)) {
        Text("Transactions", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
        Spacer(modifier = Modifier.height(16.dp))

        // 3. The List of Transactions
        LazyColumn {
            items(transactions) { transaction ->
                TransactionRow(
                    transaction = transaction,
                    onClick = {
                        // When tapped, remember which one was tapped and open the sheet
                        if (transaction.category == "Uncategorized") {
                            selectedTransaction = transaction
                            showBottomSheet = true
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    // 4. The Bottom Sheet UI (Only shows if showBottomSheet is true)
    if (showBottomSheet && selectedTransaction != null) {
        CategorizeBottomSheet(
            transaction = selectedTransaction!!,
            sheetState = sheetState,
            onDismiss = { showBottomSheet = false },
            onSave = { newCategory, alwaysRemember ->
                // Send the user's decision to the Brain (ViewModel)
                viewModel.saveCategorization(selectedTransaction!!, newCategory, alwaysRemember)
                showBottomSheet = false
            }
        )
    }
}

// ... keep your main TransactionsScreen composable the same ...

@Composable
fun TransactionRow(transaction: Transaction, onClick: () -> Unit) {
    val isUncategorized = transaction.category == "Uncategorized"
    val backgroundColor = if (isUncategorized) ExpenseRed.copy(alpha = 0.1f) else CardWhite

    val dateFormatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateString = dateFormatter.format(Date(transaction.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // FIX 1: Added weight(1f) to force this column to give up space to the amount
            Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                // Added maxLines so insanely long names get truncated with "..."
                Text(transaction.rawDescription, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                Text(transaction.category, fontSize = 12.sp, color = if (isUncategorized) ExpenseRed else TextSecondary)
                Text(dateString, fontSize = 10.sp, color = TextSecondary)
            }

            // FIX 1: Explicitly tell the amount it is never allowed to wrap lines
            Text(
                text = if (transaction.type == "CREDIT") "+₹%,.2f".format(transaction.amount) else "-₹%,.2f".format(transaction.amount),
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == "CREDIT") IncomeGreen else ExpenseRed,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorizeBottomSheet(
    transaction: Transaction,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (String, Boolean) -> Unit
) {
    val availableCategories = listOf("Food & Dining", "Groceries", "Transport", "Fuel", "Shopping", "Entertainment", "Bills & Utilities", "Rent", "Health", "Other")

    var selectedCategory by remember { mutableStateOf(availableCategories[0]) }
    var alwaysRemember by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardWhite
    ) {
        // FIX 2: Added .verticalScroll(rememberScrollState()) so smaller screens don't crush the bottom items!

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Categorize Transaction", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
                    Text("Merchant: ${transaction.rawDescription}", color = TextSecondary, modifier = Modifier.padding(bottom = 16.dp))

                    availableCategories.forEach { category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { selectedCategory = category }.padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = (selectedCategory == category),
                                onClick = { selectedCategory = category }
                            )
                            Text(category, modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // The Checkbox (This is your "Random" tickbox from the screenshot!)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = alwaysRemember,
                            onCheckedChange = { alwaysRemember = it }
                        )
                        Text("Always categorize as $selectedCategory", fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onSave(selectedCategory, alwaysRemember) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                    ) {
                        Text("Save & Apply", color = CardWhite)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
    }
}
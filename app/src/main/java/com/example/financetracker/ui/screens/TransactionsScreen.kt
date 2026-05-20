package com.example.financetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financetracker.data.entity.Transaction
import com.example.financetracker.ui.theme.*
import com.example.financetracker.ui.viewmodels.TransactionsViewModel
import java.text.SimpleDateFormat
import java.util.*

// Avoid repeating raw string literals everywhere
private const val UNCATEGORIZED = "Uncategorized"

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

    // Stores currently selected transaction
    var selectedTransaction by remember {
        mutableStateOf<Transaction?>(null)
    }

    // Bottom sheet should exist only if a transaction is selected
    val isBottomSheetVisible = selectedTransaction != null

    // Material3 bottom sheet state
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {

        ScreenHeader()

        Spacer(modifier = Modifier.height(16.dp))

        TransactionsList(
            transactions = transactions,

            // Only uncategorized transactions are clickable
            onTransactionClick = { transaction ->

                if (transaction.category == UNCATEGORIZED) {
                    selectedTransaction = transaction
                }
            }
        )
    }

    // Compose creates/removes this UI depending on state
    if (isBottomSheetVisible) {

        CategorizeBottomSheet(
            transaction = selectedTransaction!!,
            categories = dynamicCategories,
            baseCategories = viewModel.baseCategories,
            sheetState = sheetState,

            // Closing sheet = resetting state
            onDismiss = {
                selectedTransaction = null
            },

            // Receives category selection from bottom sheet
            onSave = { category, alwaysRemember ->

                // ViewModel handles database + merchant rules
                viewModel.saveCategorization(
                    selectedTransaction!!,
                    category,
                    alwaysRemember
                )

                // Close sheet after saving
                selectedTransaction = null
            },

            // Pass the Add/Delete actions to the ViewModel
            onAddCategory = { newCategory ->
                viewModel.addNewCategory(newCategory)
            },
            onDeleteCategory = { categoryToDelete ->
                viewModel.deleteCustomCategory(categoryToDelete)
            }
        )
    }
}

@Composable
private fun ScreenHeader() {

    Text(
        text = "Transactions",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = BrandBlue
    )
}

@Composable
private fun TransactionsList(
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit
) {

    // LazyColumn renders only visible items for performance
    LazyColumn {

        items(transactions) { transaction ->

            TransactionRow(
                transaction = transaction,

                // Bubble click event upward to parent
                onClick = {
                    onTransactionClick(transaction)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TransactionRow(
    transaction: Transaction,
    onClick: () -> Unit
) {

    // Used to determine warning styling
    val isUncategorized =
        transaction.category == UNCATEGORIZED

    // Highlight uncategorized transactions
    val backgroundColor =
        if (isUncategorized) {
            ExpenseRed.copy(alpha = 0.1f)
        } else {
            CardWhite
        }

    val categoryColor =
        if (isUncategorized) {
            ExpenseRed
        } else {
            TextSecondary
        }

    // Credits are green, debits are red
    val amountColor =
        if (transaction.type == "CREDIT") {
            IncomeGreen
        } else {
            ExpenseRed
        }

    // Format amount for the ui rendering
    val formattedAmount =
        if (transaction.type == "CREDIT") {
            "+₹%,.2f".format(transaction.amount)
        } else {
            "-₹%,.2f".format(transaction.amount)
        }

    // Cache formatted date unless timestamp changes
    val formattedDate = remember(transaction.timestamp) {
        SimpleDateFormat(
            "dd MMM, hh:mm a",
            Locale.getDefault()
        ).format(Date(transaction.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Left side = description/category/date
            TransactionInfoSection(
                transaction = transaction,
                categoryColor = categoryColor,
                formattedDate = formattedDate
            )

            // Right side = amount
            TransactionAmount(
                amount = formattedAmount,
                color = amountColor
            )
        }
    }
}

@Composable
private fun RowScope.TransactionInfoSection(
    transaction: Transaction,
    categoryColor: Color,
    formattedDate: String
) {

    Column(
        modifier = Modifier

            // Takes remaining horizontal space inside Row
            .weight(1f)

            .padding(end = 16.dp)
    ) {

        Text(
            text = transaction.rawDescription,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,

            // Prevent long names from breaking layout
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = transaction.category,
            fontSize = 12.sp,
            color = categoryColor
        )

        Text(
            text = formattedDate,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun TransactionAmount(
    amount: String,
    color: Color
) {

    Text(
        text = amount,
        fontWeight = FontWeight.Bold,
        color = color,

        // Prevent amount from wrapping to next line
        maxLines = 1
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorizeBottomSheet(
    transaction: Transaction,
    categories: List<String>,
    baseCategories: List<String>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (String, Boolean) -> Unit,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (String) -> Unit
) {

    // Currently selected category, defaults to first item safely
    var selectedCategory by remember {
        mutableStateOf(categories.firstOrNull() ?: "Other")
    }

    // Controls merchant memory checkbox
    var alwaysRemember by remember {
        mutableStateOf(true)
    }

    // Holds the text for the new category input field
    var newCategoryText by remember {
        mutableStateOf("")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardWhite
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)

                // Allows scrolling on smaller screens
                .verticalScroll(rememberScrollState())
        ) {

            BottomSheetHeader(transaction)

            // Dynamic Radio button category selection UI
            CategorySelector(
                categories = categories,
                baseCategories = baseCategories,
                selectedCategory = selectedCategory,

                // Updates selected category state
                onCategorySelected = {
                    selectedCategory = it
                },

                // Handles deletion and resets selection if they delete the active one
                onDeleteCategory = { categoryToDelete ->
                    onDeleteCategory(categoryToDelete)
                    if (selectedCategory == categoryToDelete) {
                        selectedCategory = "Other"
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // The New Category Text Field & Button
            NewCategoryInput(
                value = newCategoryText,
                onValueChange = { newCategoryText = it },
                onAddClick = {
                    if (newCategoryText.isNotBlank()) {
                        onAddCategory(newCategoryText)
                        selectedCategory = newCategoryText.trim()
                        newCategoryText = ""
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BackgroundLight)
            Spacer(modifier = Modifier.height(16.dp))

            RememberRuleCheckbox(
                selectedCategory = selectedCategory,
                checked = alwaysRemember,

                // Updates checkbox state
                onCheckedChange = {
                    alwaysRemember = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SaveButton(
                onClick = {

                    // Send selected values upward
                    onSave(selectedCategory, alwaysRemember)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BottomSheetHeader(
    transaction: Transaction
) {

    Text(
        text = "Categorize Transaction",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = BrandBlue,
        modifier = Modifier.padding(top = 8.dp)
    )

    Text(
        text = "Merchant: ${transaction.rawDescription}",
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun CategorySelector(
    categories: List<String>,
    baseCategories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onDeleteCategory: (String) -> Unit
) {

    // Generate one row per dynamic category
    categories.forEach { category ->

        Row(
            modifier = Modifier
                .fillMaxWidth()

                // Entire row is clickable
                .clickable {
                    onCategorySelected(category)
                }

                .padding(vertical = 4.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            RadioButton(
                selected = selectedCategory == category,

                // Radio button updates same state
                onClick = {
                    onCategorySelected(category)
                }
            )

            Text(
                text = category,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f) // Pushes the trash can to the far right
            )

            // Only show the trash can for user-created categories
            if (!baseCategories.contains(category)) {
                IconButton(
                    onClick = { onDeleteCategory(category) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Custom Category",
                        tint = ExpenseRed
                    )
                }
            }
        }
    }
}

@Composable
private fun NewCategoryInput(
    value: String,
    onValueChange: (String) -> Unit,
    onAddClick: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("New Custom Category") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPurple
            )
        ) {
            Text("Add", color = CardWhite)
        }
    }
}

@Composable
private fun RememberRuleCheckbox(
    selectedCategory: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        // Text updates automatically when category changes
        Text(
            text = "Always categorize as $selectedCategory",
            fontSize = 14.sp
        )
    }
}

@Composable
private fun SaveButton(
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,

        modifier = Modifier.fillMaxWidth(),

        colors = ButtonDefaults.buttonColors(
            containerColor = BrandBlue
        )
    ) {

        Text(
            text = "Save & Apply",
            color = CardWhite
        )
    }
}
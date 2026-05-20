package com.example.financetracker.ui.screens.transactions.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.financetracker.data.entity.Transaction
import com.example.financetracker.ui.theme.*
/* The main thing to understand in this code is state hoisting.
The main canvas of this screen starts with ModalBottomSheet which then calls a column then within the column we have 5 main functions
BottomSheetHeader, CategorySelector, NewCategoryInput, RememberRuleCheckbox, and SaveButton.
BottomSheet header is your normal function that just draws some UI
The state hoisting happens in newcategoryInput and categorySelector.
In the function call for newcategoryinput, onValueChange and onAddCategory are functions defined inside the parameter but called by the body
Yes, you have to understand that the function call itself has another functions definition with a name(lambda) which uses a variable defined in its outer scope
The function that is defined in the parameter is called by the function body.
The reason Compose leans on it so heavily is that it's the only clean way to let child UI components affect parent state without giving them direct access to variables they shouldn't own.
The parent stays in control of its own state — it just hands down a key that the child can use to knock on the door.
Effectively UI is being drawn by the function body only but the state management is done in the function parameter.
*  */
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
                baseCategories = baseCategories, //variable or list being used by the function body
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
                onValueChange = { newCategoryText = it }, //variable being modified by the body, inside a lambda. Effectively onValueChange and onAddCategory are functions defined inside the parameter but called by the body
                onAddCategory = { //function defined here and called in the body
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

            @Suppress("DEPRECATION")
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
    onAddCategory: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange, //changing the variable value in the parameter by calling the lambda defined in the parameter
            label = { Text("New Custom Category") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onAddCategory,
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

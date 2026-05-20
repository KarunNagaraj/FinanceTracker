package com.example.financetracker.ui.screens.settings

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.financetracker.data.repository.TransactionRepository
import com.example.financetracker.ui.screens.settings.components.ImportCard
import com.example.financetracker.ui.screens.settings.components.PermissionCard
import com.example.financetracker.ui.screens.settings.components.SettingsHeader
import com.example.financetracker.utils.CsvImporter
import com.example.financetracker.utils.TransactionClassifier
import kotlinx.coroutines.launch

/**
 * Main entry point for the Settings Screen.
 * Manages app permissions and data import workflows.
 */
@Composable
fun SettingsScreen(
    repository: TransactionRepository,
    classifier: TransactionClassifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State Management for Permissions
    var hasSmsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, 
                Manifest.permission.RECEIVE_SMS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission Request Handler
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasSmsPermission = permissions[Manifest.permission.RECEIVE_SMS] == true
        }
    )

    // CSV File Picker Handler
    val csvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                Toast.makeText(context, "Analyzing CSV...", Toast.LENGTH_SHORT).show()

                // Logic Extraction: Business logic handled by CsvImporter and Classifier
                val transactions = CsvImporter.parseAndImport(context, uri, classifier)

                // Save results to database
                transactions.forEach { repository.insertTransaction(it) }

                Toast.makeText(
                    context, 
                    "Imported ${transactions.size} transactions!", 
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SettingsHeader()

        // Data Management Section
        ImportCard(
            onClick = {
                csvPickerLauncher.launch("*/*")
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Permissions Section
        PermissionCard(
            hasSmsPermission = hasSmsPermission,
            onCheckedChange = { checked ->
                if (checked) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.RECEIVE_SMS, 
                            Manifest.permission.READ_SMS
                        )
                    )
                }
            }
        )
    }
}

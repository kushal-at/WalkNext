package com.walknxt.app.presentation.settings

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.walknxt.app.data.preferences.PreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val useImperial by viewModel.useImperial.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val dailyGoal by viewModel.dailyStepGoal.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Fitness Goals
        Text(text = "Fitness Goals", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        
        ListItem(
            headlineContent = { Text("Daily Step Goal") },
            supportingContent = { Text("$dailyGoal steps") },
            modifier = Modifier.clickable { showGoalDialog = true }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Appearance
        Text(text = "Appearance", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        ListItem(
            headlineContent = { Text("Theme") },
            supportingContent = { 
                Text(
                    when(themeMode) {
                        PreferencesManager.THEME_LIGHT -> "Light"
                        PreferencesManager.THEME_DARK -> "Dark"
                        else -> "System Default"
                    }
                ) 
            },
            modifier = Modifier.clickable { showThemeDialog = true }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Units
        Text(text = "Units", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        ListItem(
            headlineContent = { Text("Distance Unit") },
            supportingContent = { Text(if (useImperial) "Miles (mi)" else "Kilometers (km)") },
            modifier = Modifier.clickable { viewModel.toggleUnits() }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Privacy
        Text(text = "Privacy", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        ListItem(
            headlineContent = { Text("Export Data") },
            supportingContent = { Text("Save your walks offline") },
            modifier = Modifier.clickable { 
                coroutineScope.launch {
                    val uri = viewModel.generateExportUri()
                    if (uri != null) {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, "Export WalkNxt Data"))
                    }
                }
            }
        )
        ListItem(
            headlineContent = { Text("Delete All History", color = MaterialTheme.colorScheme.error) },
            supportingContent = { Text("Permanently erase your data") },
            modifier = Modifier.clickable { showDeleteConfirm = true }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }

    // --- Modals & Dialogs ---
    
    if (showGoalDialog) {
        var tempGoal by remember { mutableStateOf(dailyGoal.toString()) }
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Daily Step Goal") },
            text = {
                OutlinedTextField(
                    value = tempGoal,
                    onValueChange = { tempGoal = it.filter { char -> char.isDigit() } },
                    label = { Text("Steps") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { 
                    val goal = tempGoal.toIntOrNull() ?: 10000
                    viewModel.setDailyStepGoal(goal)
                    showGoalDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Select Theme") },
            text = {
                Column {
                    val options = listOf(
                        PreferencesManager.THEME_SYSTEM to "System Default",
                        PreferencesManager.THEME_LIGHT to "Light",
                        PreferencesManager.THEME_DARK to "Dark"
                    )
                    options.forEach { (value, label) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setTheme(value)
                                    showThemeDialog = false
                                }
                                .padding(16.dp)
                        ) {
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete All Data?") },
            text = { Text("This will permanently erase all your walking history and cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllHistory()
                        showDeleteConfirm = false
                    }
                ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

}

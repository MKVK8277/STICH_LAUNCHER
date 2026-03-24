package com.vinodk.launcher.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onThemeChange: (ThemeMode) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleFocusGate: (Boolean) -> Unit,
    onToggleUsageLimits: (Boolean) -> Unit,
    onToggleScheduling: (Boolean) -> Unit,
    onToggleReadingMode: (Boolean) -> Unit,
    onBatchNotificationsChange: (Int) -> Unit,
    onDefaultDailyLimitChange: (Int) -> Unit,
    onResetDefaults: () -> Unit,
    onExportSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        item {
            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Theme section
        item {
            SettingsSectionHeader(title = "APPEARANCE")
        }

        item {
            ThemeSelector(
                currentTheme = uiState.themeMode,
                onThemeSelected = onThemeChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        item {
            SettingsToggle(
                label = "Dark Mode",
                description = "Reduce eye strain with dark theme",
                checked = uiState.isDarkMode,
                onCheckedChange = onToggleDarkMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }

        // Focus & Usage section
        item {
            SettingsSectionHeader(title = "FOCUS & USAGE")
        }

        item {
            SettingsToggle(
                label = "Focus Gate",
                description = "Require confirmation for low-priority apps",
                checked = uiState.enableFocusGate,
                onCheckedChange = onToggleFocusGate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        item {
            SettingsToggle(
                label = "Usage Limits",
                description = "Track and limit daily app usage",
                checked = uiState.enableUsageLimits,
                onCheckedChange = onToggleUsageLimits,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        item {
            SettingsToggle(
                label = "Time-Based Scheduling",
                description = "Block apps during specific hours",
                checked = uiState.enableScheduling,
                onCheckedChange = onToggleScheduling,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }

        // Reading Mode section
        item {
            SettingsSectionHeader(title = "READING MODE")
        }

        item {
            SettingsToggle(
                label = "Reading Mode",
                description = "Distraction-free reading interface",
                checked = uiState.enableReadingMode,
                onCheckedChange = onToggleReadingMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }

        // Notifications section
        item {
            SettingsSectionHeader(title = "NOTIFICATIONS")
        }

        item {
            SettingsSlider(
                label = "Batch Notifications",
                description = "Check notifications every X minutes",
                value = uiState.batchNotificationsMinutes.toFloat(),
                onValueChange = { onBatchNotificationsChange(it.toInt()) },
                valueRange = 30f..240f,
                steps = 3,
                displayValue = "${uiState.batchNotificationsMinutes}m",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }

        // Usage section
        item {
            SettingsSectionHeader(title = "USAGE DEFAULTS")
        }

        item {
            SettingsSlider(
                label = "Default Daily Limit",
                description = "Suggested screen time limit per app",
                value = uiState.defaultDailyLimitMinutes.toFloat(),
                onValueChange = { onDefaultDailyLimitChange(it.toInt()) },
                valueRange = 30f..480f,
                steps = 15,
                displayValue = uiState.defaultDailyLimitMinutes.formatMinutes(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }

        // Actions section
        item {
            SettingsSectionHeader(title = "ACTIONS")
        }

        item {
            SettingsButton(
                label = "Export Settings",
                description = "Save your configuration as JSON",
                onClick = onExportSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
        }

        item {
            SettingsButton(
                label = "Reset to Defaults",
                description = "Restore all settings to factory defaults",
                onClick = onResetDefaults,
                isDestructive = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )
        }

        // Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Stitch Launcher v1.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Digital wellbeing focused",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.secondary,
        modifier = modifier.padding(bottom = 16.dp, top = 8.dp)
    )
}

@Composable
private fun ThemeSelector(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Color Theme",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ThemeOption(
                name = "Paper",
                theme = ThemeMode.PAPER_WHITE,
                isSelected = currentTheme == ThemeMode.PAPER_WHITE,
                backgroundColor = MaterialTheme.colorScheme.surface,
                onClick = { onThemeSelected(ThemeMode.PAPER_WHITE) },
                modifier = Modifier.weight(1f)
            )

            ThemeOption(
                name = "Sepia",
                theme = ThemeMode.SEPIA,
                isSelected = currentTheme == ThemeMode.SEPIA,
                backgroundColor = androidx.compose.ui.graphics.Color(0xFFE8DCC8),
                onClick = { onThemeSelected(ThemeMode.SEPIA) },
                modifier = Modifier.weight(1f)
            )

            ThemeOption(
                name = "Charcoal",
                theme = ThemeMode.CHARCOAL,
                isSelected = currentTheme == ThemeMode.CHARCOAL,
                backgroundColor = androidx.compose.ui.graphics.Color(0xFF2A2A2A),
                onClick = { onThemeSelected(ThemeMode.CHARCOAL) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ThemeOption(
    name: String,
    theme: ThemeMode,
    isSelected: Boolean,
    backgroundColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = MaterialTheme.shapes.medium,
        border = if (isSelected) {
            androidx.compose.foundation.border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            )
        } else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = if (theme == ThemeMode.CHARCOAL) 
                    androidx.compose.ui.graphics.Color.White 
                else 
                    MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun SettingsToggle(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
private fun SettingsSlider(
    label: String,
    description: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    displayValue: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Text(
                    text = displayValue,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SettingsButton(
    label: String,
    description: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDestructive)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

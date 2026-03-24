package com.vinodk.launcher.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vinodk.launcher.data.model.AppInfo
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onSearchQueryChange: (String) -> Unit,
    onAppLaunch: (AppInfo) -> Unit,
    onAppLongPress: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTime = remember { mutableStateOf("") }
    val currentDate = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val now = System.currentTimeMillis()
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
            currentTime.value = timeFormat.format(Date(now))
            currentDate.value = dateFormat.format(Date(now))
            kotlinx.coroutines.delay(60_000) // Update every minute
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header with time and date
        Text(
            text = currentTime.value,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1
        )

        Text(
            text = "Good Morning",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = currentDate.value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search bar
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Pinned apps (quick access)
        if (uiState.pinnedApps.isNotEmpty()) {
            Text(
                text = "PINNED",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            uiState.pinnedApps.forEach { app ->
                AppListItem(
                    app = app,
                    onClick = { onAppLaunch(app) },
                    onLongClick = { onAppLongPress(app) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // App list based on search or sections
        if (uiState.searchQuery.isNotEmpty()) {
            LazyColumn {
                items(uiState.filteredApps) { app ->
                    AppListItem(
                        app = app,
                        onClick = { onAppLaunch(app) },
                        onLongClick = { onAppLongPress(app) }
                    )
                }
            }
        } else {
            LazyColumn {
                // Essential apps
                if (uiState.essentialApps.isNotEmpty()) {
                    item {
                        Text(
                            text = "ESSENTIAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 12.dp, top = 12.dp)
                        )
                    }
                    items(uiState.essentialApps) { app ->
                        AppListItem(
                            app = app,
                            onClick = { onAppLaunch(app) },
                            onLongClick = { onAppLongPress(app) }
                        )
                    }
                }

                // Normal priority apps
                if (uiState.normalApps.isNotEmpty()) {
                    item {
                        Text(
                            text = "APPS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 12.dp, top = 12.dp)
                        )
                    }
                    items(uiState.normalApps) { app ->
                        AppListItem(
                            app = app,
                            onClick = { onAppLaunch(app) },
                            onLongClick = { onAppLongPress(app) }
                        )
                    }
                }

                // Low priority apps
                if (uiState.lowPriorityApps.isNotEmpty()) {
                    item {
                        Text(
                            text = "LOW PRIORITY",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 12.dp, top = 12.dp)
                        )
                    }
                    items(uiState.lowPriorityApps) { app ->
                        AppListItem(
                            app = app,
                            onClick = { onAppLaunch(app) },
                            onLongClick = { onAppLongPress(app) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppListItem(
    app: AppInfo,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.surface,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = app.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (app.usageMinutesToday > 0) {
                Text(
                    text = "${app.usageMinutesToday}m",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.height(48.dp),
        placeholder = { Text("Search apps...") },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

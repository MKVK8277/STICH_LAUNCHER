package com.vinodk.launcher.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

sealed class NavItem(
    val icon: String,
    val label: String,
    val description: String
) {
    object Home : NavItem("⌂", "Home", "App launcher")
    object ReadingMode : NavItem("📖", "Reading", "Distraction-free mode")
    object UsageTimers : NavItem("⏱", "Timers", "Track usage")
    object Settings : NavItem("⚙", "Settings", "Preferences")
}

@Composable
fun QuickAccessBar(
    currentNav: NavItem,
    onNavigate: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val items = listOf(
            NavItem.Home,
            NavItem.ReadingMode,
            NavItem.UsageTimers,
            NavItem.Settings
        )

        items.forEach { item ->
            QuickAccessItem(
                item = item,
                isSelected = item::class == currentNav::class,
                onClick = { onNavigate(item) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickAccessItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = item.icon,
            style = MaterialTheme.typography.headlineMedium,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FloatingActionMenu(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onNavigate: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        if (isExpanded) {
            FloatingMenuButton(
                icon = "📖",
                label = "Reading",
                onClick = {
                    onNavigate(NavItem.ReadingMode)
                    onToggle()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            FloatingMenuButton(
                icon = "⏱",
                label = "Usage",
                onClick = {
                    onNavigate(NavItem.UsageTimers)
                    onToggle()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            FloatingMenuButton(
                icon = "⚙",
                label = "Settings",
                onClick = {
                    onNavigate(NavItem.Settings)
                    onToggle()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        FloatingMenuButton(
            icon = if (isExpanded) "✕" else "≡",
            label = if (isExpanded) "Close" else "Menu",
            onClick = onToggle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            isPrimary = true
        )
    }
}

@Composable
private fun FloatingMenuButton(
    icon: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = icon,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

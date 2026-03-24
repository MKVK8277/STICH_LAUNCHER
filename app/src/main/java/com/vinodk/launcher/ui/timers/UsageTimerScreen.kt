package com.vinodk.launcher.ui.timers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun UsageTimerScreen(
    uiState: UsageTimerUiState,
    onUpdateLimit: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Text(
            text = "USAGE TRACKER",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Daily summary
        DailySummaryCard(
            totalUsedMinutes = uiState.totalUsedToday,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        Text(
            text = "TODAY'S APPS",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // App timers list
        LazyColumn {
            items(uiState.activeTimers) { timer ->
                AppTimerCard(
                    timer = timer,
                    onUpdateLimit = { newLimit ->
                        onUpdateLimit(timer.packageName, newLimit)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }
        }

        // Alerts section
        if (uiState.timerAlerts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "ALERTS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(uiState.timerAlerts.take(3)) { alert ->
                    AlertCard(
                        alert = alert,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DailySummaryCard(
    totalUsedMinutes: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Screen Time",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = totalUsedMinutes.formatMinutes(),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Updated in real-time",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun AppTimerCard(
    timer: AppTimerInfo,
    onUpdateLimit: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val usagePercent = timer.usedMinutesToday.toFloat() / timer.dailyLimitMinutes.coerceAtLeast(1)
    val isExceeded = timer.usedMinutesToday > timer.dailyLimitMinutes
    val progressColor = when {
        isExceeded -> MaterialTheme.colorScheme.error
        usagePercent > 0.8f -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.secondary
    }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with app name and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = timer.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (timer.isActive) "Currently open" else "Closed",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Time badge
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = timer.usedMinutesToday.formatMinutes(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.background, shape = MaterialTheme.shapes.extraSmall)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(usagePercent.coerceAtMost(1f))
                        .background(progressColor, shape = MaterialTheme.shapes.extraSmall)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Used: ${timer.usedMinutesToday}/${timer.dailyLimitMinutes} min",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                Text(
                    text = "${(usagePercent * 100).toInt().coerceAtMost(100)}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = progressColor
                )
            }

            if (isExceeded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠ Daily limit exceeded",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AlertCard(
    alert: TimerAlert,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (alert.severity) {
        AlertSeverity.INFO -> MaterialTheme.colorScheme.surface
        AlertSeverity.WARNING -> Color(0xFFFFF8E1)  // Light yellow
        AlertSeverity.CRITICAL -> Color(0xFFFFEBEE) // Light red
    }

    val textColor = when (alert.severity) {
        AlertSeverity.INFO -> MaterialTheme.colorScheme.onBackground
        AlertSeverity.WARNING -> Color(0xFFF57F17)  // Dark amber
        AlertSeverity.CRITICAL -> MaterialTheme.colorScheme.error
    }

    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon for severity
            Text(
                text = when (alert.severity) {
                    AlertSeverity.INFO -> "ℹ"
                    AlertSeverity.WARNING -> "⚠"
                    AlertSeverity.CRITICAL -> "✕"
                },
                color = textColor,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = alert.message,
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

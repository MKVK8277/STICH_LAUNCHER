package com.vinodk.launcher.ui.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vinodk.launcher.data.model.AppAccessRequest

@Composable
fun FocusGateOverlay(
    request: AppAccessRequest?,
    onAllow: () -> Unit,
    onDeny: () -> Unit,
    onDelay5Min: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (request == null) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight(),
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = request.label,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = when (request.reason) {
                        com.vinodk.launcher.data.model.AccessReason.DAILY_LIMIT_EXCEEDED ->
                            "You've reached your daily limit for this app."
                        com.vinodk.launcher.data.model.AccessReason.NOT_IN_SCHEDULE ->
                            "This app is not available at this time."
                        com.vinodk.launcher.data.model.AccessReason.GATED_BY_FOCUS ->
                            "This is a low-priority app. Take a moment to reconsider?"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDeny,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("Not Now", style = MaterialTheme.typography.labelMedium)
                    }

                    Button(
                        onClick = onDelay5Min,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("Delay 5m", style = MaterialTheme.typography.labelMedium)
                    }

                    Button(
                        onClick = onAllow,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Open", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

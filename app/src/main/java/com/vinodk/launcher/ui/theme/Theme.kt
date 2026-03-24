package com.vinodk.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// E-Ink inspired color palette
object LauncherColors {
    // Paper White theme
    val PaperWhite = Color(0xFFF5F5F0)
    val EInkBlack = Color(0xFF2E2E2E)
    val MutedGray = Color(0xFFB0B0B0)
    val SepiaAccent = Color(0xFFA67C52)
    val SoftHighlight = Color(0xFFEDE6D6)
    
    // Charcoal theme
    val Charcoal = Color(0xFF1F1F1F)
    val CharcoalText = Color(0xFFE8E8E8)
    
    // Functional
    val Success = Color(0xFF6B8E23)  // Muted green
    val Warning = Color(0xFF8B7355)  // Muted brown
    val Error = Color(0xFF8B4445)    // Muted red
}

private val LightColors = lightColorScheme(
    primary = LauncherColors.SepiaAccent,
    onPrimary = LauncherColors.PaperWhite,
    primaryContainer = LauncherColors.SoftHighlight,
    onPrimaryContainer = LauncherColors.EInkBlack,
    
    secondary = LauncherColors.MutedGray,
    onSecondary = LauncherColors.PaperWhite,
    
    background = LauncherColors.PaperWhite,
    onBackground = LauncherColors.EInkBlack,
    
    surface = LauncherColors.SoftHighlight,
    onSurface = LauncherColors.EInkBlack,
    
    error = LauncherColors.Error,
    onError = LauncherColors.PaperWhite,
)

private val DarkColors = darkColorScheme(
    primary = LauncherColors.SepiaAccent,
    onPrimary = LauncherColors.Charcoal,
    primaryContainer = Color(0xFF4A4A4A),
    onPrimaryContainer = LauncherColors.CharcoalText,
    
    secondary = LauncherColors.MutedGray,
    onSecondary = LauncherColors.Charcoal,
    
    background = LauncherColors.Charcoal,
    onBackground = LauncherColors.CharcoalText,
    
    surface = Color(0xFF2A2A2A),
    onSurface = LauncherColors.CharcoalText,
    
    error = LauncherColors.Error,
    onError = LauncherColors.Charcoal,
)

@Composable
fun LauncherTheme(
    isDarkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkMode) DarkColors else LightColors
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = LauncherTypography,
        content = content
    )
}

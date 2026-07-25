package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val QuantumDarkColorScheme = darkColorScheme(
    primary = QuantumCyan,
    onPrimary = ObsidianBlack,
    primaryContainer = CardSlate,
    onPrimaryContainer = QuantumCyan,
    secondary = TacticalEmerald,
    onSecondary = ObsidianBlack,
    secondaryContainer = CardSlate,
    onSecondaryContainer = TacticalEmerald,
    tertiary = WarningAmber,
    error = AlertCrimson,
    background = ObsidianBlack,
    onBackground = TextPrimary,
    surface = DarkSlate,
    onSurface = TextPrimary,
    surfaceVariant = CardSlate,
    onSurfaceVariant = TextSecondary,
    outline = BorderSlate
)

@Composable
fun QuantumMessengerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = QuantumDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    QuantumMessengerTheme(content = content)
}


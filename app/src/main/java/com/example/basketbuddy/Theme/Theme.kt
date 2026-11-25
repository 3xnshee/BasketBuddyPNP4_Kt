package com.example.basketbuddy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light mode color scheme
private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    secondary = SecondaryGreen,
    onSecondary = Color.White,
    primaryContainer = LightGreen,
    onPrimaryContainer = PrimaryText,
    background = AppBackground,
    onBackground = PrimaryText,
    surface = AppSurface,
    onSurface = PrimaryText,
    onSurfaceVariant = SecondaryText,
    error = ErrorRed,
    onError = Color.White
)

// Dark mode color scheme
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryGreen,
    onPrimary = DarkBackground,
    secondary = DarkSecondaryGreen,
    onSecondary = DarkBackground,
    primaryContainer = DarkGreenContainer,
    onPrimaryContainer = DarkPrimaryText,
    background = DarkBackground,
    onBackground = DarkPrimaryText,
    surface = DarkSurface,
    onSurface = DarkPrimaryText,
    onSurfaceVariant = DarkSecondaryText,
    error = DarkErrorRed,
    onError = DarkBackground
)

// Main Basket Buddy application theme
@Composable
fun BasketBuddyTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {

    // Select the appropriate color scheme
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    // Apply application colors and typography
    MaterialTheme(
        colorScheme = colorScheme,
        typography = BasketBuddyTypography,
        content = content
    )
}
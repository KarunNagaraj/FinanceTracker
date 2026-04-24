package com.example.financetracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrandBlue,
    secondary = BrandPurple,
    tertiary = SavingsBlue,
    background = BackgroundLight,
    surface = CardWhite,
    onPrimary = CardWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    secondary = BrandPurple,
    tertiary = SavingsBlue,
    background = BackgroundLight,
    surface = CardWhite,
    onPrimary = CardWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun FinanceTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to false so our brand colors aren't overridden by Android 12+ wallpaper themes
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        // This relies on Type.kt still being intact from your initial project creation.
        // If "Typography" is highlighted red, simply delete this line.
        typography = Typography,
        content = content
    )
}
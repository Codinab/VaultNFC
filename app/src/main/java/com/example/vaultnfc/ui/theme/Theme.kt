package com.example.vaultnfc.ui.theme

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vaultnfc.ui.viewmodel.SettingsViewModel

private val DarkColorScheme = darkColorScheme(
    primary = RedDarkEnd, // Maintain the primary color for consistency
    secondary = BlackLabel, // Use a dark color for backgrounds
    tertiary = WhiteEnd, // Use a light color for text to ensure readability
    background = BlackLabel, // Use a darker shade for cards and fields background
    onBackground = BlackBackground,
    outline = Color.Gray
)

private val LightColorScheme = lightColorScheme(
    primary = RedEnd,
    secondary = WhiteEnd,
    tertiary = BlackEnd,
    background = LightRed,
    onBackground = Color.White,
    outline = Color.Gray
)

@Composable
fun BoxTest() {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun VaultNFCTheme(
    content: @Composable () -> Unit,
) {
    val settingsViewModel: SettingsViewModel = viewModel()
    val darkTheme by settingsViewModel.darkThemeEnabled.collectAsState(initial = true)  // Default to dark theme

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
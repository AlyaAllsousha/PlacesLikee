package com.example.placeslikee.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreen,
    onPrimary = Color.White,
    primaryContainer = EmeraldGreen.copy(alpha = 0.12f),
    onPrimaryContainer = EmeraldGreen,

    secondary = Terracotta,
    onSecondary = Color.White,
    secondaryContainer = Terracotta.copy(alpha = 0.12f),
    onSecondaryContainer = Terracotta,

    tertiary = EmeraldGreen.copy(alpha = 0.8f),
    onTertiary = Color.White,

    background = WarmPaper,
    onBackground = TextHighEmphasisLight,

    surface = Color.White,  // Карточки постов - чистый белый
    onSurface = TextHighEmphasisLight,
    surfaceVariant = WarmPaper,
    onSurfaceVariant = TextMediumEmphasisLight,

    error = ErrorLight,
    onError = Color.White,
    errorContainer = ErrorLight.copy(alpha = 0.12f),
    onErrorContainer = ErrorLight,

    outline = TextMediumEmphasisLight,
    outlineVariant = DividerLight.copy(alpha = 0.5f)
)
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)
private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreenLight,
    onPrimary = NightBackground,
    primaryContainer = EmeraldGreenLight.copy(alpha = 0.20f),
    onPrimaryContainer = EmeraldGreenLight,

    secondary = TerracottaLight,
    onSecondary = NightBackground,
    secondaryContainer = TerracottaLight.copy(alpha = 0.20f),
    onSecondaryContainer = TerracottaLight,

    tertiary = EmeraldGreenLight.copy(alpha = 0.8f),
    onTertiary = NightBackground,

    background = NightBackground,
    onBackground = TextHighEmphasisDark,

    surface = NightSurface,
    onSurface = TextHighEmphasisDark,
    surfaceVariant = NightBackground,
    onSurfaceVariant = TextMediumEmphasisDark,

    error = ErrorDark,
    onError = NightBackground,
    errorContainer = ErrorDark.copy(alpha = 0.20f),
    onErrorContainer = ErrorDark,

    outline = TextMediumEmphasisDark,
    outlineVariant = DividerDark.copy(alpha = 0.5f)
)

@Composable
fun PlacesLikeeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
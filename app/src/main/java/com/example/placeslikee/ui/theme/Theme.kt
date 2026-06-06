package com.example.placeslikee.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = OceanicTurquoise,
    onPrimary = Color.White,
    primaryContainer = OceanicTurquoise.copy(alpha = 0.12f),
    onPrimaryContainer = OceanicTurquoise,

    secondary = SunsetCoral,
    onSecondary = Color.White,
    secondaryContainer = SunsetCoral.copy(alpha = 0.12f),
    onSecondaryContainer = SunsetCoral,

    tertiary = OceanicTurquoise.copy(alpha = 0.8f),
    onTertiary = Color.White,

    background = CloudyWhite,
    onBackground = TextHighEmphasisLight,

    surface = Color.White,
    onSurface = TextHighEmphasisLight,
    surfaceVariant = CloudyWhite,
    onSurfaceVariant = TextMediumEmphasisLight,

    error = ErrorLight,
    onError = Color.White,
    errorContainer = ErrorLight.copy(alpha = 0.12f),
    onErrorContainer = ErrorLight,


    outline = TextLowEmphasisLight,
    outlineVariant = TextLowEmphasisLight.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = GlowingTurquoise,
    onPrimary = NightGraphite,
    primaryContainer = GlowingTurquoise.copy(alpha = 0.20f),
    onPrimaryContainer = GlowingTurquoise,

    secondary = SoftSalmon,
    onSecondary = NightGraphite,
    secondaryContainer = SoftSalmon.copy(alpha = 0.20f),
    onSecondaryContainer = SoftSalmon,

    tertiary = GlowingTurquoise.copy(alpha = 0.8f),
    onTertiary = NightGraphite,

    background = NightGraphite,
    onBackground = TextHighEmphasisDark,

    surface = CardGraphite,
    onSurface = TextHighEmphasisDark,
    surfaceVariant = NightGraphite,
    onSurfaceVariant = TextMediumEmphasisDark,

    error = ErrorDark,
    onError = NightGraphite,
    errorContainer = ErrorDark.copy(alpha = 0.20f),
    onErrorContainer = ErrorDark,

    outline = TextLowEmphasisDark,
    outlineVariant = TextLowEmphasisDark.copy(alpha = 0.5f)
)


@Composable
fun PlacesLikeeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }


        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
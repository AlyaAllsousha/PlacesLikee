// Theme.kt

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
    // ── Primary: тёплый янтарь ──────────────────────────────────────────────
    primary = Amber,
    onPrimary = Color.White,
    primaryContainer = Amber.copy(alpha = 0.15f),
    onPrimaryContainer = Color(0xFF4A2800),     // тёмно-коричневый для читаемости

    // ── Secondary: терракота ─────────────────────────────────────────────────
    secondary = Terracotta,
    onSecondary = Color.White,
    secondaryContainer = Terracotta.copy(alpha = 0.12f),
    onSecondaryContainer = Color(0xFF3D0A00),

    // ── Tertiary: тёплая охра (дополнительный акцент) ────────────────────────
    tertiary = Color(0xFFB5803A),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB5803A).copy(alpha = 0.12f),
    onTertiaryContainer = Color(0xFF3A2000),

    // ── Фон и поверхности ────────────────────────────────────────────────────
    background = Parchment,
    onBackground = TextHighEmphasisLight,

    surface = Color.White,
    onSurface = TextHighEmphasisLight,
    surfaceVariant = Color(0xFFF2EAE0),         // тёплый бежевый для вариантов
    onSurfaceVariant = TextMediumEmphasisLight,

    // ── Ошибки и границы ─────────────────────────────────────────────────────
    error = ErrorLight,
    onError = Color.White,
    errorContainer = ErrorLight.copy(alpha = 0.12f),
    onErrorContainer = ErrorLight,

    outline = TextMediumEmphasisLight,
    outlineVariant = DividerLight
)

private val DarkColorScheme = darkColorScheme(
    // ── Primary: янтарь (немного теплее для тёмного фона) ───────────────────
    primary = AmberLight,
    onPrimary = Color(0xFF2E1500),
    primaryContainer = Color(0xFF4A2800),
    onPrimaryContainer = Color(0xFFFFD9A8),     // кремово-оранжевый

    // ── Secondary: мягкая терракота ──────────────────────────────────────────
    secondary = TerracottaLight,
    onSecondary = Color(0xFF2B0A00),
    secondaryContainer = Color(0xFF4A1800),
    onSecondaryContainer = Color(0xFFFFB4A0),

    // ── Tertiary ─────────────────────────────────────────────────────────────
    tertiary = Color(0xFFC99050),
    onTertiary = Color(0xFF2E1800),
    tertiaryContainer = Color(0xFF452C00),
    onTertiaryContainer = Color(0xFFFFDDB0),

    // ── Фон и поверхности ────────────────────────────────────────────────────
    background = NightBackground,
    onBackground = TextHighEmphasisDark,

    surface = NightSurface,
    onSurface = TextHighEmphasisDark,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = TextMediumEmphasisDark,

    // ── Ошибки и границы ─────────────────────────────────────────────────────
    error = ErrorDark,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = TextMediumEmphasisDark,
    outlineVariant = DividerDark
)

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
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
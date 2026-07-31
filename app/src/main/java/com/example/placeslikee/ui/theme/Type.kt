package com.example.placeslikee.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.placeslikee.R // ваш пакет

// Если используете Inter, оставляем так.
// Если скачаете Manrope (идеально для Premium Nature), просто переименуйте файлы в R.font.manrope_...
val PremiumFontFamily = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semi_bold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold)
)

val Typography = Typography(

    displayMedium = TextStyle(
        fontFamily = PremiumFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = (-1).sp
    ),

    // ЗАГОЛОВКИ ЭКРАНОВ (Например: "Лента", "Карта", "Профиль")
    headlineMedium = TextStyle(
        fontFamily = PremiumFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.5).sp
    ),

    //  ИМЕНА ПОЛЬЗОВАТЕЛЕЙ И НАЗВАНИЯ МЕСТ В ЛЕНТЕ
    titleMedium = TextStyle(
        fontFamily = PremiumFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),

    //  ОСНОВНОЙ ТЕКСТ ПОСТА (Отзывы, описания маршрутов)
    bodyLarge = TextStyle(
        fontFamily = PremiumFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),

    // ВТОРОСТЕПЕННЫЙ ТЕКСТ (Например: Короткие комментарии)
    bodyMedium = TextStyle(
        fontFamily = PremiumFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),

    // МЕЛКИЕ ДЕТАЛИ И КНОПКИ
    labelMedium = TextStyle(
        fontFamily = PremiumFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),

    //  КНОПКИ
    labelLarge = TextStyle(
        fontFamily = PremiumFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)
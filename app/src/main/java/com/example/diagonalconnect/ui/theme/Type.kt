package com.example.diagonalconnect.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Sans = FontFamily.SansSerif

val Typography = Typography(
    displaySmall = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Light, fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 6.sp),
    headlineMedium = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Light, fontSize = 26.sp, lineHeight = 34.sp, letterSpacing = 4.sp),
    headlineSmall = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Normal, fontSize = 21.sp, lineHeight = 28.sp, letterSpacing = 2.sp),
    titleLarge = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Medium, fontSize = 19.sp, lineHeight = 26.sp, letterSpacing = 1.sp),
    titleMedium = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = 1.2.sp),
    titleSmall = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 2.sp),
    bodyLarge = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.3.sp),
    bodyMedium = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp, letterSpacing = 0.25.sp),
    bodySmall = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp, letterSpacing = 0.3.sp),
    labelLarge = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 2.5.sp),
    labelMedium = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 2.sp),
    labelSmall = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 1.8.sp),
)

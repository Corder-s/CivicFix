package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.ui.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFB923C),           // Vibrant High-Contrast Orange
    onPrimary = Color(0xFF0F172A),         // Deep Dark Gray
    primaryContainer = Color(0xFFC2410C),  // Warm Orange Container
    onPrimaryContainer = Color(0xFFFFF2E8),
    secondary = Color(0xFFF97316),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF7C2D12),
    onSecondaryContainer = Color(0xFFFFEDD5),
    tertiary = Color(0xFFFBBF24),
    onTertiary = Color(0xFF78350F),
    background = Color(0xFF0F172A),        // Deep Slate Dark Canvas (#0F172A)
    surface = Color(0xFF1E293B),           // Elevated Charcoal Card Surface (#1E293B)
    surfaceVariant = Color(0xFF334155),    // Crisp Card Secondary (#334155)
    onBackground = Color(0xFFFFFFFF),      // Crisp White
    onSurface = Color(0xFFFFFFFF),         // Crisp White
    onSurfaceVariant = Color(0xFFE2E8F0),  // Light Gray for secondary text
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155)
)

private val LightColorScheme = lightColorScheme(
    primary = CivicOrangePrimary,          // #FF6B00 Vibrant Civic Orange
    onPrimary = Color.White,
    primaryContainer = CivicOrangeContainer,
    onPrimaryContainer = CivicOrangeDark,
    secondary = CivicOrangeDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF7ED),
    onSecondaryContainer = CivicOrangeDark,
    tertiary = CivicAmber,
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = CivicDarkGray,
    onSurface = CivicDarkGray,
    onSurfaceVariant = CivicSlate600,
    outline = CivicSlate200,
)

@Composable
fun CivicFixTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

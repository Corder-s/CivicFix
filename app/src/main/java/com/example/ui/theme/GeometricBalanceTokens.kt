package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Geometric Balance Design System Tokens
 * Centralized design tokens for consistent spacing, radii, typography, and color variables.
 */
object GeometricBalanceTokens {

    // --- Color Variables ---
    object Colors {
        // Deep Indigo Navy Brand
        val NavyDark = Color(0xFF1A237E)
        val NavyPrimary = Color(0xFF283593)
        val NavyLight = Color(0xFF3F51B5)
        val NavyContainer = Color(0xFFEEF2FF)
        val NavyBorder = Color(0xFFC7D2FE)

        // Emerald / Forest Civic Green
        val GreenPrimary = Color(0xFF2E7D32)
        val GreenLight = Color(0xFF4CAF50)
        val GreenContainer = Color(0xFFE8F5E9)
        val GreenDark = Color(0xFF1B5E20)

        // Accent Amber
        val Amber = Color(0xFFF59E0B)
        val AmberContainer = Color(0xFFFEF3C7)
        val AmberText = Color(0xFFB45309)
        val AmberDark = Color(0xFFD97706)

        // Urgent Red / Danger
        val Red = Color(0xFFDC2626)
        val RedContainer = Color(0xFFFEE2E2)
        val RedText = Color(0xFF991B1B)
        val RedDark = Color(0xFFB91C1C)

        // Neutral Slate Scales
        val Slate50 = Color(0xFFF8FAFC)
        val Slate100 = Color(0xFFF1F5F9)
        val Slate200 = Color(0xFFE2E8F0)
        val Slate300 = Color(0xFFCBD5E1)
        val Slate400 = Color(0xFF94A3B8)
        val Slate500 = Color(0xFF64748B)
        val Slate600 = Color(0xFF475569)
        val Slate700 = Color(0xFF334155)
        val Slate800 = Color(0xFF1E293B)
        val Slate900 = Color(0xFF0F172A)
    }

    // --- Spacing Grid System ---
    object Spacing {
        val xxs: Dp = 2.dp
        val xs: Dp = 4.dp
        val sm: Dp = 8.dp
        val md: Dp = 12.dp
        val lg: Dp = 16.dp
        val xl: Dp = 20.dp
        val xxl: Dp = 24.dp
        val xxxl: Dp = 32.dp
    }

    // --- Shape & Corner Radius Tokens ---
    object Radii {
        val xs = RoundedCornerShape(4.dp)
        val sm = RoundedCornerShape(8.dp)
        val md = RoundedCornerShape(10.dp)
        val lg = RoundedCornerShape(14.dp)
        val xl = RoundedCornerShape(18.dp)
        val xxl = RoundedCornerShape(22.dp)
        val pill = RoundedCornerShape(999.dp)
    }

    // --- Elevation Tokens ---
    object Elevation {
        val none: Dp = 0.dp
        val low: Dp = 1.dp
        val defaultElevation: Dp = 2.dp
        val hover: Dp = 4.dp
        val modal: Dp = 8.dp
        val floating: Dp = 12.dp
    }

    // --- Typography Scale ---
    object Typography {
        val displayLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            letterSpacing = (-0.5).sp
        )

        val titleLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            letterSpacing = (-0.2).sp
        )

        val titleMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 22.sp
        )

        val bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        val bodySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        val labelMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        val labelSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.2.sp
        )
    }
}

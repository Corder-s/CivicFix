package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicAmberContainer
import com.example.ui.theme.CivicAmberText
import com.example.ui.theme.CivicDarkGray
import com.example.ui.theme.CivicGreenContainer
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenLight
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyBorder
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyLight
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicOrangeDark
import com.example.ui.theme.CivicOrangeLight
import com.example.ui.theme.CivicOrangePrimary
import com.example.ui.theme.CivicRed
import com.example.ui.theme.CivicRedContainer
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate300
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate50
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900

// Palette tokens matching the Orange + White + Dark Gray design system
val CivicOrangeSubmit = Color(0xFFFF6B00)
val CivicOrangeSubmitHover = Color(0xFFEA580C)
val CivicWarmAmbientGlow = Color(0xFFFFF2E8)
val WarmAmbientGlow = Color(0xFFFFF2E8)
val CardBackgroundCanvas = Color(0xFFFFFFFF)
val SoftInputBackground = Color(0xFFF1F5F9)
val SoftInputDarkBackground = Color(0xFF1E293B)

/**
 * Aesthetic Split-Screen Authentication Layout matching the Crextio / CivicFix reference design.
 * Features:
 * - Brand pill tag at top left ("CivicFix")
 * - Rounded pill input controls with soft placeholders
 * - Warm yellow "Submit" pill action button
 * - Apple and Google pill social sign-in buttons
 * - Hero collaborative team image with floating glassmorphism widgets:
 *    1. "Task Review With Team" (yellow pill card)
 *    2. Frosted glass calendar timeline strip
 *    3. "Daily Meeting" white card with attendee avatars
 *    4. Floating member portraits
 *    5. Top-right close button (X)
 */
@Composable
fun AuthLayout(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    isAdminTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0)) // Neutral slate background canvas
    ) {
        val isWide = maxWidth >= 760.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isWide) 24.dp else 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Main Rounded Modal Card Container
            Surface(
                shape = RoundedCornerShape(if (isWide) 32.dp else 24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 1040.dp)
            ) {
                if (isWide) {
                    // Wide / Desktop / Tablet Side-by-Side View
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(680.dp)
                    ) {
                        // Left Form Side (48%)
                        Box(
                            modifier = Modifier
                                .weight(0.95f)
                                .fillMaxHeight()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            WarmAmbientGlow.copy(alpha = 0.9f),
                                            Color(0xFFFFFDF5),
                                            Color.White
                                        ),
                                        center = Offset(0f, 680f),
                                        radius = 650f
                                    )
                                )
                                .padding(32.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    // Brand pill top-left
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = Color.White,
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            shadowElevation = 1.dp
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isAdminTheme) CivicAmber else CivicGreenPrimary)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (isAdminTheme) "CivicAdmin" else "CivicFix",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CivicSlate900
                                                )
                                            }
                                        }

                                        if (onBackClick != null) {
                                            IconButton(
                                                onClick = onBackClick,
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = "Back",
                                                    tint = CivicSlate600,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Header Text
                                    Text(
                                        text = title,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CivicSlate900,
                                        letterSpacing = (-0.5).sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = subtitle,
                                        fontSize = 13.sp,
                                        color = CivicSlate600
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Form Content
                                    content()
                                }
                            }
                        }

                        // Right Hero Side (52%)
                        Box(
                            modifier = Modifier
                                .weight(1.05f)
                                .fillMaxHeight()
                                .padding(12.dp)
                        ) {
                            HeroCollaborativeTeamCard(
                                isAdminTheme = isAdminTheme,
                                onCloseClick = onBackClick,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    // Mobile Vertical View
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color(0xFFFFFDF5),
                                        WarmAmbientGlow.copy(alpha = 0.6f)
                                    )
                                )
                            )
                    ) {
                        // Hero Header on Mobile
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .padding(8.dp)
                        ) {
                            HeroCollaborativeTeamCard(
                                isAdminTheme = isAdminTheme,
                                onCloseClick = onBackClick,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Form Section below
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Brand tag
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isAdminTheme) CivicAmber else CivicGreenPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAdminTheme) "CivicAdmin" else "CivicFix",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CivicSlate900
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = title,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CivicSlate900
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = subtitle,
                                fontSize = 12.5.sp,
                                color = CivicSlate600
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            content()
                        }
                    }
                }
            }
        }
    }
}

/**
 * Collaborative Team Hero Card with Glassmorphism Overlays matching reference image
 */
@Composable
fun HeroCollaborativeTeamCard(
    isAdminTheme: Boolean = false,
    onCloseClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(CivicDarkGray)
    ) {
        // High-Quality Civic & Community Hero Photo
        Image(
            painter = painterResource(id = R.drawable.civic_community_hero_1787055793969),
            contentDescription = "Civic Community Issue Reporting and City Improvement",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle dark gradient vignette for readable widgets
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f)
                        )
                    )
                )
        )

        // Top Right Close Button
        if (onCloseClick != null) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(36.dp)
                    .clickable { onCloseClick() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = CivicSlate800,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Floating Top Orange Badge: "Clean City Initiative • Active Ward Resolution"
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = CivicOrangePrimary,
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isAdminTheme) "Municipal Command Center" else "Community Improvement Hub",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                    Text(
                        text = "Smart Public Services & Faster Resolution",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Floating Team Avatars overlay
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp),
            verticalArrangement = Arrangement.spacedBy((-8).dp)
        ) {
            TeamAvatarBubble(initials = "AL", name = "Amélie", isOnline = true)
            TeamAvatarBubble(initials = "RK", name = "Rahul", isOnline = true)
            TeamAvatarBubble(initials = "PS", name = "Priya", isOnline = false)
        }

        // Middle Frosted Glass Calendar Timeline Strip
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.Black.copy(alpha = 0.45f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp)
                .fillMaxWidth(0.88f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val days = listOf("Sun" to "22", "Mon" to "23", "Tue" to "24", "Wed" to "25", "Thu" to "26", "Fri" to "27", "Sat" to "28")
                days.forEachIndexed { index, (day, date) ->
                    val isSelected = index == 3
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color.White.copy(alpha = 0.25f) else Color.Transparent)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = day,
                            fontSize = 9.sp,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = date,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Bottom Left White Meeting Card: "Daily Meeting • 12:00pm-01:00pm"
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 6.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp)
                .widthIn(max = 210.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isAdminTheme) "Daily Ward Briefing" else "Daily Meeting",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate900
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFB300))
                    )
                }
                Text(
                    text = "12:00pm-01:00pm",
                    fontSize = 10.5.sp,
                    color = CivicSlate600
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Attendee Avatars Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-6).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AttendeeSmallBubble(color = CivicNavyPrimary, text = "A")
                    AttendeeSmallBubble(color = CivicGreenPrimary, text = "R")
                    AttendeeSmallBubble(color = CivicAmber, text = "P")
                    AttendeeSmallBubble(color = Color(0xFF7C3AED), text = "S")
                }
            }
        }
    }
}

@Composable
private fun TeamAvatarBubble(initials: String, name: String, isOnline: Boolean) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .shadow(4.dp, CircleShape)
    ) {
        Surface(
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(2.dp, if (isOnline) CivicGreenLight else Color.White),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(CivicSlate800)
            ) {
                Text(
                    text = initials,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        if (isOnline) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(CivicGreenLight)
                    .border(1.5.dp, Color.White, CircleShape)
            )
        }
    }
}

@Composable
private fun AttendeeSmallBubble(color: Color, text: String) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.5.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

/**
 * Reusable Pill Text Input for Auth Forms (as in reference image)
 */
@Composable
fun AuthInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector? = null,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: (() -> Unit)? = null,
    isAdminTheme: Boolean = false,
    testTag: String = "auth_input"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = CivicSlate600
        )
        Spacer(modifier = Modifier.height(5.dp))

        Surface(
            shape = RoundedCornerShape(50),
            color = SoftInputBackground,
            border = BorderStroke(
                1.dp,
                if (errorMessage != null) CivicRed else Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (errorMessage != null) CivicRed else CivicSlate400,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = placeholder,
                            fontSize = 13.5.sp,
                            color = CivicSlate400
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
                    keyboardActions = KeyboardActions(
                        onNext = { onImeAction?.invoke() },
                        onDone = { onImeAction?.invoke() }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = CivicSlate900,
                        unfocusedTextColor = CivicSlate900
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag(testTag)
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = CivicRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

/**
 * Reusable Pill Password Input with Visibility Toggle (as in reference image)
 */
@Composable
fun PasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: (() -> Unit)? = null,
    showStrength: Boolean = false,
    isAdminTheme: Boolean = false,
    testTag: String = "auth_password_input"
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = CivicSlate600
        )
        Spacer(modifier = Modifier.height(5.dp))

        Surface(
            shape = RoundedCornerShape(50),
            color = SoftInputBackground,
            border = BorderStroke(
                1.dp,
                if (errorMessage != null) CivicRed else Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (errorMessage != null) CivicRed else CivicSlate400,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = placeholder,
                            fontSize = 13.5.sp,
                            color = CivicSlate400
                        )
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
                    keyboardActions = KeyboardActions(
                        onDone = { onImeAction?.invoke() },
                        onNext = { onImeAction?.invoke() }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = CivicSlate900,
                        unfocusedTextColor = CivicSlate900
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag(testTag)
                )

                IconButton(
                    onClick = { isPasswordVisible = !isPasswordVisible },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isPasswordVisible) "Hide" else "Show",
                        tint = CivicSlate400,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = CivicRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        if (showStrength && value.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            PasswordStrengthIndicator(password = value)
        }
    }
}

/**
 * Reusable Auth Primary Action Button (Warm Yellow Pill Button matching reference image)
 */
@Composable
fun AuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    loadingText: String = "Please wait...",
    isEnabled: Boolean = true,
    isAdminTheme: Boolean = false,
    testTag: String = "auth_action_button"
) {
    val buttonColor = if (isAdminTheme) CivicDarkGray else CivicOrangePrimary
    val contentColor = Color.White

    Surface(
        onClick = onClick,
        enabled = isEnabled && !isLoading,
        shape = RoundedCornerShape(50),
        color = buttonColor,
        shadowElevation = 3.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag(testTag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 2.5.dp,
                        color = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = loadingText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            } else {
                Text(
                    text = text,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}

/**
 * Side-by-side Apple and Google Social Auth Buttons (as in reference image)
 */
@Composable
fun SocialPillAuthRow(
    onAppleClick: () -> Unit,
    onGoogleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Apple Button
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 1.dp,
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clickable { onAppleClick() }
                .testTag("social_apple_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate900
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Apple",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CivicSlate800
                )
            }
        }

        // Google Button
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 1.dp,
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clickable { onGoogleClick() }
                .testTag("social_google_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "G",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF4285F4)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Google",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CivicSlate800
                )
            }
        }
    }
}

/**
 * Reusable "OR" Divider
 */
@Composable
fun SocialDivider(
    text: String = "OR",
    modifier: Modifier = Modifier,
    isAdminTheme: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color(0xFFE2E8F0)
        )
        Text(
            text = text,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = CivicSlate400,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color(0xFFE2E8F0)
        )
    }
}

/**
 * Real-time Password Strength Indicator & Criteria Checklist
 */
@Composable
fun PasswordStrengthIndicator(
    password: String,
    modifier: Modifier = Modifier
) {
    val hasMinLength = password.length >= 8
    val hasUppercase = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }

    val strengthScore = (if (hasMinLength) 1 else 0) + (if (hasUppercase) 1 else 0) + (if (hasDigit) 1 else 0)

    val (strengthLabel, strengthColor) = when (strengthScore) {
        3 -> "Strong password" to CivicGreenPrimary
        2 -> "Medium strength" to CivicAmber
        else -> "Weak password" to CivicRed
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password Strength",
                fontSize = 11.sp,
                color = CivicSlate600
            )
            Text(
                text = strengthLabel,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = strengthColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 1..3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (i <= strengthScore) strengthColor else CivicSlate200)
                )
            }
        }
    }
}

/**
 * Logout Confirmation Modal (Dialog)
 */
@Composable
fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirmLogout: () -> Unit,
    isLoggingOut: Boolean = false
) {
    AlertDialog(
        onDismissRequest = { if (!isLoggingOut) onDismiss() },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        icon = {
            Surface(
                shape = CircleShape,
                color = CivicRedContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Sign Out",
                        tint = CivicRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = "Sign out of CivicFix?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CivicNavyDark
            )
        },
        text = {
            Text(
                text = "Are you sure you want to sign out? You will need to sign in again to file grievances and track resolution progress.",
                fontSize = 13.5.sp,
                color = CivicSlate600,
                lineHeight = 19.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmLogout,
                enabled = !isLoggingOut,
                colors = ButtonDefaults.buttonColors(containerColor = CivicRed),
                shape = RoundedCornerShape(50),
                modifier = Modifier.testTag("modal_confirm_signout_button")
            ) {
                if (isLoggingOut) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Signing out...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text("Sign Out", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isLoggingOut,
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, CivicSlate200),
                modifier = Modifier.testTag("modal_cancel_signout_button")
            ) {
                Text("Cancel", fontSize = 13.sp, color = CivicSlate800)
            }
        }
    )
}

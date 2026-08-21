package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.CivicStrings
import com.example.data.localization.civicString
import com.example.ui.AppPlatformMode
import com.example.ui.theme.CivicDarkGray
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicOrangeDark
import com.example.ui.theme.CivicOrangeLight
import com.example.ui.theme.CivicOrangePrimary
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Shield

/**
 * Circular Shrinking Floating AI Agent Element
 * - Shrunk by default into a sleek circular FAB (CircleShape, 54.dp) with vibrant gradient and AI sparkle icon when idle.
 * - Dynamically switches context between AI Road Safety and CivicFix mode.
 * - When tapped, smoothly expands into quick action chips and full AI chat panel.
 * - Clicking outside the panel automatically closes/shrinks it.
 */
@Composable
fun GlobalFloatingAiButton(
    bottomPadding: Dp = 80.dp,
    platformMode: AppPlatformMode = AppPlatformMode.AI_ROAD_SAFETY,
    onOpenAiChat: () -> Unit,
    onQuickAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "ai_idle_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val primaryColor = if (platformMode == AppPlatformMode.AI_ROAD_SAFETY) CivicOrangePrimary else CivicNavyPrimary
    val gradientColors = if (platformMode == AppPlatformMode.AI_ROAD_SAFETY) {
        listOf(CivicOrangePrimary, CivicOrangeDark, CivicNavyDark)
    } else {
        listOf(CivicNavyPrimary, CivicNavyDark, CivicDarkGray)
    }

    // Full screen overlay when expanded to detect outside clicks and close auto-magically
    if (isExpanded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isExpanded = false
                }
        )
    }

    Box(
        modifier = modifier
            .padding(end = 16.dp, bottom = bottomPadding)
            .testTag("floating_ai_container")
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Expanded Popup Menu (Opens smoothly when user taps the circular button)
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(spring()) + scaleIn(spring(), initialScale = 0.75f),
                exit = fadeOut(spring()) + scaleOut(spring(), targetScale = 0.75f)
            ) {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .padding(bottom = 6.dp)
                        .shadow(16.dp, RoundedCornerShape(22.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            // Consume clicks inside card so it doesn't dismiss
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Header row with Close button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = primaryColor,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (platformMode == AppPlatformMode.AI_ROAD_SAFETY) Icons.Default.Shield else Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = if (platformMode == AppPlatformMode.AI_ROAD_SAFETY) "Road Safety AI" else "CivicFix AI",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CivicDarkGray
                                    )
                                    Text(
                                        text = if (platformMode == AppPlatformMode.AI_ROAD_SAFETY) "Route Safety & Transit Advisor" else "Grievance & Resolution Guide",
                                        fontSize = 10.sp,
                                        color = CivicSlate600,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Shrink / Close Button
                            IconButton(
                                onClick = { isExpanded = false },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Shrink AI Agent",
                                    tint = CivicSlate400,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        if (platformMode == AppPlatformMode.AI_ROAD_SAFETY) {
                            // Road Safety Actions
                            AiCircleActionRow(
                                icon = Icons.Default.Navigation,
                                iconColor = CivicOrangePrimary,
                                title = "Plan Safest Route",
                                subtitle = "AI flood bypass & safety score",
                                onClick = {
                                    isExpanded = false
                                    onQuickAction("plan_route")
                                    onOpenAiChat()
                                }
                            )

                            AiCircleActionRow(
                                icon = Icons.Default.DirectionsBus,
                                iconColor = Color(0xFF16A34A),
                                title = "Bus & Transit Schedules",
                                subtitle = "Route frequency & crowd prediction",
                                onClick = {
                                    isExpanded = false
                                    onQuickAction("bus_routes")
                                    onOpenAiChat()
                                }
                            )

                            AiCircleActionRow(
                                icon = Icons.Default.Shield,
                                iconColor = Color(0xFF4F46E5),
                                title = "Weather & Flood Risks",
                                subtitle = "Live waterlogging alerts",
                                onClick = {
                                    isExpanded = false
                                    onQuickAction("weather_risk")
                                    onOpenAiChat()
                                }
                            )
                        } else {
                            // CivicFix Actions
                            AiCircleActionRow(
                                icon = Icons.Default.Report,
                                iconColor = CivicNavyPrimary,
                                title = civicString(CivicStrings.AI_ACTION_REPORT),
                                subtitle = civicString(CivicStrings.AI_ACTION_REPORT_DESC),
                                onClick = {
                                    isExpanded = false
                                    onQuickAction("report_issue")
                                    onOpenAiChat()
                                }
                            )

                            AiCircleActionRow(
                                icon = Icons.Default.TrackChanges,
                                iconColor = Color(0xFF16A34A),
                                title = civicString(CivicStrings.AI_ACTION_TRACK),
                                subtitle = civicString(CivicStrings.AI_ACTION_TRACK_DESC),
                                onClick = {
                                    isExpanded = false
                                    onQuickAction("track_report")
                                    onOpenAiChat()
                                }
                            )

                            AiCircleActionRow(
                                icon = Icons.Default.LocationSearching,
                                iconColor = Color(0xFF4F46E5),
                                title = civicString(CivicStrings.AI_ACTION_NEARBY),
                                subtitle = civicString(CivicStrings.AI_ACTION_NEARBY_DESC),
                                onClick = {
                                    isExpanded = false
                                    onQuickAction("find_similar")
                                    onOpenAiChat()
                                }
                            )
                        }

                        // Full Chat Button
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = primaryColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isExpanded = false
                                    onOpenAiChat()
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (platformMode == AppPlatformMode.AI_ROAD_SAFETY) "Ask Road Safety AI" else "Ask CivicFix AI",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Circular Shrunk Floating Button (Always CircleShape)
            FloatingActionButton(
                onClick = {
                    isExpanded = !isExpanded
                },
                shape = CircleShape,
                containerColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .size(54.dp)
                    .scale(if (!isExpanded) pulseScale else 1f)
                    .testTag("floating_ai_assistant_btn")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        )
                ) {
                    if (isExpanded) {
                        // When open, show Close X icon
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close AI",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        // When shrunk / idle, show AI Sparkle icon with clean white tint
                        Icon(
                            imageVector = if (platformMode == AppPlatformMode.AI_ROAD_SAFETY) Icons.Default.Shield else Icons.Default.AutoAwesome,
                            contentDescription = "AI Agent",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Online indicator dot
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E))
                            .shadow(2.dp, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun AiCircleActionRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CivicSlate100,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = iconColor.copy(alpha = 0.15f),
                modifier = Modifier.size(30.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate800
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = CivicSlate600
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = CivicSlate400,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

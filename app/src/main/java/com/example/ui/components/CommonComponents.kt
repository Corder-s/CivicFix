package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CivicIssue
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.data.models.IssueStatus
import com.example.data.models.UserRole
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicAmberContainer
import com.example.ui.theme.CivicAmberText
import com.example.ui.theme.CivicGreenContainer
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenLight
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyBorder
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyLight
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicOrangeBorder
import com.example.ui.theme.CivicOrangeContainer
import com.example.ui.theme.CivicOrangeDark
import com.example.ui.theme.CivicOrangeLight
import com.example.ui.theme.CivicOrangePrimary
import com.example.ui.theme.CivicRed
import com.example.ui.theme.CivicRedContainer
import com.example.ui.theme.CivicRedText
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Geometric Balance Button Variants & Sizes
 */
enum class CivicButtonVariant {
    PRIMARY,        // Deep Civic Navy
    PRIMARY_GREEN,  // Forest Civic Green
    PRIMARY_ORANGE, // Road Safety Vibrant Orange
    SECONDARY,      // Crisp Slate Container
    SECONDARY_OUTLINE, // Secondary Border Outline
    OUTLINED,       // High-contrast geometric border
    TONAL_GREEN,    // Soft green container with deep green text
    TONAL_AMBER,    // Amber container for Municipal Administration
    DANGER          // Urgent red for removal / escalation
}

enum class CivicButtonSize {
    SMALL,   // 36dp height, compact for cards and list items
    MEDIUM,  // 48dp height, standard Material 3 touch target
    LARGE    // 54dp height, prominent hero actions
}

/**
 * Custom Reusable CivicButton Component based on Geometric Balance Theme
 * - Responsive touch target (min 48dp for MEDIUM/LARGE, with proper padding)
 * - Geometric shape tokens and crisp border highlights
 * - Supports loading states, leading/trailing icons, and responsive scaling
 */
@Composable
fun CivicButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: CivicButtonVariant = CivicButtonVariant.PRIMARY,
    size: CivicButtonSize = CivicButtonSize.MEDIUM,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    testTag: String? = null
) {
    val (minHeight: Dp, fontSize, cornerRadius, horizontalPadding, iconSize) = when (size) {
        CivicButtonSize.SMALL -> Tuple5(36.dp, 12.sp, 8.dp, 12.dp, 14.dp)
        CivicButtonSize.MEDIUM -> Tuple5(48.dp, 14.sp, 12.dp, 18.dp, 18.dp)
        CivicButtonSize.LARGE -> Tuple5(54.dp, 15.sp, 14.dp, 24.dp, 20.dp)
    }

    val (containerColor, contentColor, borderStroke) = when (variant) {
        CivicButtonVariant.PRIMARY -> Triple(
            if (enabled) CivicNavyDark else CivicSlate200,
            if (enabled) Color.White else CivicSlate400,
            null
        )
        CivicButtonVariant.PRIMARY_GREEN -> Triple(
            if (enabled) CivicGreenPrimary else CivicSlate200,
            if (enabled) Color.White else CivicSlate400,
            null
        )
        CivicButtonVariant.PRIMARY_ORANGE -> Triple(
            if (enabled) CivicOrangePrimary else CivicSlate200,
            if (enabled) Color.White else CivicSlate400,
            null
        )
        CivicButtonVariant.SECONDARY -> Triple(
            if (enabled) CivicSlate100 else CivicSlate100,
            if (enabled) CivicSlate800 else CivicSlate400,
            BorderStroke(1.dp, CivicSlate200)
        )
        CivicButtonVariant.SECONDARY_OUTLINE -> Triple(
            Color.Transparent,
            if (enabled) CivicSlate800 else CivicSlate400,
            BorderStroke(1.dp, if (enabled) CivicSlate400 else CivicSlate200)
        )
        CivicButtonVariant.OUTLINED -> Triple(
            Color.Transparent,
            if (enabled) CivicNavyDark else CivicSlate400,
            BorderStroke(1.5.dp, if (enabled) CivicNavyPrimary else CivicSlate200)
        )
        CivicButtonVariant.TONAL_GREEN -> Triple(
            if (enabled) CivicGreenContainer else CivicSlate100,
            if (enabled) CivicGreenDark else CivicSlate400,
            BorderStroke(1.dp, CivicGreenLight.copy(alpha = 0.5f))
        )
        CivicButtonVariant.TONAL_AMBER -> Triple(
            if (enabled) CivicAmber else CivicSlate200,
            if (enabled) Color.White else CivicSlate400,
            null
        )
        CivicButtonVariant.DANGER -> Triple(
            if (enabled) CivicRed else CivicSlate200,
            if (enabled) Color.White else CivicSlate400,
            null
        )
    }

    val buttonModifier = modifier
        .defaultMinSize(minHeight = minHeight)
        .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)

    Surface(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(cornerRadius),
        color = containerColor,
        contentColor = contentColor,
        border = borderStroke,
        shadowElevation = if (enabled && variant in listOf(CivicButtonVariant.PRIMARY, CivicButtonVariant.PRIMARY_GREEN, CivicButtonVariant.TONAL_AMBER)) 2.dp else 0.dp,
        modifier = buttonModifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 6.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = contentColor,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(iconSize + 2.dp)
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (leadingIcon != null) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(iconSize)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = text,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        textAlign = TextAlign.Center
                    )

                    if (trailingIcon != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(iconSize)
                        )
                    }
                }
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)

/**
 * Custom Reusable IssueCard Component based on Geometric Balance Theme
 * Fully responsive across Phone (<600dp) and Tablet (>=600dp) sizes.
 */
@Composable
fun IssueCard(
    issue: CivicIssue,
    onIssueClick: () -> Unit,
    onUpvoteClick: () -> Unit,
    modifier: Modifier = Modifier,
    showFullDetails: Boolean = false
) {
    val catColor = getCategoryColor(issue.category)
    val catIcon = getCategoryIcon(issue.category)
    val formattedDate = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(issue.reportedTimestamp))

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isWide = maxWidth >= 500.dp

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, CivicSlate200),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onIssueClick() }
                .testTag("issue_card_${issue.id}")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isWide) 18.dp else 14.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Geometric Square Category Icon container
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = catColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, catColor.copy(alpha = 0.25f)),
                        modifier = Modifier.size(if (isWide) 52.dp else 44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = catIcon,
                                contentDescription = issue.category.displayName,
                                tint = catColor,
                                modifier = Modifier.size(if (isWide) 26.dp else 22.dp)
                            )
                        }
                    }

                    // Title + Subtitle details
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = issue.title,
                                fontSize = if (isWide) 16.sp else 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicSlate900,
                                maxLines = if (showFullDetails) 2 else 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            CivicStatusBadge(status = issue.status)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = CivicSlate400,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${issue.location} • ${formattedDate}",
                                fontSize = 11.sp,
                                color = CivicSlate600,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Grievance Description Snippet
                Text(
                    text = issue.description,
                    fontSize = 12.sp,
                    color = CivicSlate800,
                    maxLines = if (showFullDetails) 4 else 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Footer Metadata & Responsive Upvote Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Department & Priority Badges
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CivicNavyContainer
                        ) {
                            Text(
                                text = issue.assignedDepartment.displayName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicNavyPrimary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }

                        CivicPriorityBadge(priority = issue.priority)
                    }

                    // Upvote Pill / Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (issue.hasUserUpvoted) CivicNavyDark else CivicSlate100,
                        border = BorderStroke(1.dp, if (issue.hasUserUpvoted) CivicNavyDark else CivicSlate200),
                        modifier = Modifier
                            .clickable { onUpvoteClick() }
                            .testTag("upvote_button_${issue.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = if (issue.hasUserUpvoted) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                contentDescription = "Upvote",
                                tint = if (issue.hasUserUpvoted) Color.White else CivicSlate600,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${issue.upvotes}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (issue.hasUserUpvoted) Color.White else CivicSlate800
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Premium Segmented Switcher for "AI Road Safety" (Primary) <-> "CivicFix" (Connected Grievances)
 */
@Composable
fun PlatformModeSwitcher(
    currentMode: com.example.ui.AppPlatformMode,
    onModeSelect: (com.example.ui.AppPlatformMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("app_platform_mode_switcher")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Option 1: 🛡️ AI Road Safety
            val isSafety = currentMode == com.example.ui.AppPlatformMode.AI_ROAD_SAFETY
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSafety) CivicOrangePrimary else Color.Transparent,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onModeSelect(com.example.ui.AppPlatformMode.AI_ROAD_SAFETY) }
                    .testTag("switch_mode_ai_road_safety")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🛡️",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI Road Safety",
                        fontSize = 12.sp,
                        fontWeight = if (isSafety) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSafety) Color.White else Color.White.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
            }

            // Option 2: 🏙️ CivicFix
            val isCivic = currentMode == com.example.ui.AppPlatformMode.CIVIC_FIX
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isCivic) CivicOrangePrimary else Color.Transparent,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onModeSelect(com.example.ui.AppPlatformMode.CIVIC_FIX) }
                    .testTag("switch_mode_civic_fix")
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🏙️",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CivicFix",
                        fontSize = 12.sp,
                        fontWeight = if (isCivic) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCivic) Color.White else Color.White.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Geometric Top Navigation Bar with AI Road Safety / CivicFix Mode Switcher
 */
@Composable
fun CivicTopBar(
    currentRole: UserRole,
    unreadNotifCount: Int,
    onNotificationClick: () -> Unit,
    onToggleRoleClick: () -> Unit,
    onAiChatClick: () -> Unit = {},
    selectedLanguage: com.example.data.localization.AppLanguage = com.example.data.localization.AppLanguage.EN,
    onLanguageClick: () -> Unit = {},
    userName: String? = "Rahul Sharma",
    platformMode: com.example.ui.AppPlatformMode = com.example.ui.AppPlatformMode.AI_ROAD_SAFETY,
    onPlatformModeChange: (com.example.ui.AppPlatformMode) -> Unit = {}
) {
    Surface(
        color = CivicNavyDark, // Dark Charcoal #0F172A
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 16.dp)
        ) {
            // Top Row: Geometric Logo + Brand Title + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Geometric Logo + Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CivicOrangePrimary,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (platformMode == com.example.ui.AppPlatformMode.AI_ROAD_SAFETY) Icons.Default.Shield else Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CivicFix Mobility",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = Color.White,
                                letterSpacing = (-0.3).sp
                            )
                        }
                        Text(
                            text = if (platformMode == com.example.ui.AppPlatformMode.AI_ROAD_SAFETY) "AI Road Safety Engine" else "Civic Redressal Module",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CivicOrangeLight
                        )
                    }
                }

                // Action controls: Language, Role toggle & Notification Bell
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Language Chip
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier.clickable { onLanguageClick() }
                    ) {
                        Text(
                            text = selectedLanguage.nativeName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        )
                    }

                    // Notification Bell
                    BadgedBox(
                        badge = {
                            if (unreadNotifCount > 0) {
                                Badge(
                                    containerColor = CivicOrangePrimary,
                                    contentColor = Color.White,
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Text("$unreadNotifCount", fontSize = 9.sp)
                                }
                            }
                        }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier
                                .size(34.dp)
                                .clickable { onNotificationClick() }
                                .testTag("notifications_icon_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // If currently logged in as Admin, show a subtle officer indicator
                    if (currentRole == UserRole.ADMIN) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CivicAmber.copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, CivicAmber),
                            modifier = Modifier.clickable { onToggleRoleClick() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = CivicAmber,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Officer Mode",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CivicAmber
                                )
                            }
                        }
                    }
                }
            }

            // APP SWITCHER SEGMENTED CONTROL (prominently near top)
            if (currentRole == UserRole.CITIZEN) {
                Spacer(modifier = Modifier.height(12.dp))
                PlatformModeSwitcher(
                    currentMode = platformMode,
                    onModeSelect = onPlatformModeChange
                )
            }
        }
    }
}

/**
 * Geometric Status Pill
 */
@Composable
fun CivicStatusBadge(status: IssueStatus) {
    val (bgColor, textColor, label) = when (status) {
        IssueStatus.PENDING -> Triple(CivicAmberContainer, CivicAmberText, "NEW / PENDING")
        IssueStatus.IN_PROGRESS -> Triple(CivicNavyContainer, CivicNavyPrimary, "ACTIVE")
        IssueStatus.RESOLVED -> Triple(CivicGreenContainer, CivicGreenDark, "RESOLVED")
        IssueStatus.REJECTED -> Triple(CivicRedContainer, CivicRedText, "CLOSED")
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
        )
    }
}

@Composable
fun CivicPriorityBadge(priority: IssuePriority) {
    val (color, label) = when (priority) {
        IssuePriority.HIGH -> Pair(CivicRed, "HIGH")
        IssuePriority.MEDIUM -> Pair(CivicAmber, "MEDIUM")
        IssuePriority.LOW -> Pair(CivicGreenPrimary, "LOW")
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

fun getCategoryIcon(category: IssueCategory): ImageVector {
    return when (category) {
        IssueCategory.GARBAGE -> Icons.Default.Delete
        IssueCategory.ROADS -> Icons.Default.Traffic
        IssueCategory.WATER -> Icons.Default.WaterDrop
        IssueCategory.ELECTRICITY -> Icons.Default.ElectricBolt
        IssueCategory.DRAINAGE -> Icons.Default.Waves
        IssueCategory.STREETLIGHT -> Icons.Default.Lightbulb
        IssueCategory.PUBLIC_PROPERTY -> Icons.Default.AccountBalance
        IssueCategory.OTHER -> Icons.Default.MoreHoriz
    }
}

fun getCategoryColor(category: IssueCategory): Color {
    return when (category) {
        IssueCategory.GARBAGE -> Color(0xFF2E7D32)
        IssueCategory.ROADS -> Color(0xFFE65100)
        IssueCategory.WATER -> Color(0xFF0288D1)
        IssueCategory.ELECTRICITY -> Color(0xFFF57F17)
        IssueCategory.DRAINAGE -> Color(0xFF00897B)
        IssueCategory.STREETLIGHT -> Color(0xFF3949AB)
        IssueCategory.PUBLIC_PROPERTY -> Color(0xFF7B1FA2)
        IssueCategory.OTHER -> CivicSlate600
    }
}

/**
 * Geometric Balance Metric Card
 * Implements bg-white p-4 rounded-2xl border border-slate-100 shadow-sm
 */
@Composable
fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, CivicSlate200),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Squircle Geometric Icon container
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = accentColor.copy(alpha = 0.12f),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Large Bold Value
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CivicSlate900,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Uppercase Subtitle
            Text(
                text = title.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CivicSlate600,
                letterSpacing = 0.6.sp
            )
        }
    }
}

/**
 * Geometric Balance Issue Card
 * Implements clean geometric container with squircle icon, clean tags, and upvote button
 */
@Composable
fun IssueCardItem(
    issue: CivicIssue,
    onIssueClick: () -> Unit,
    onUpvoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IssueCard(
        issue = issue,
        onIssueClick = onIssueClick,
        onUpvoteClick = onUpvoteClick,
        modifier = modifier
    )
}

/**
 * Geometric Stepper
 */
@Composable
fun StatusTimelineStepper(status: IssueStatus) {
    val steps = listOf(
        Pair("Reported", IssueStatus.PENDING),
        Pair("Under Review", IssueStatus.IN_PROGRESS),
        Pair("In Progress", IssueStatus.IN_PROGRESS),
        Pair("Resolved", IssueStatus.RESOLVED)
    )

    val currentStepIndex = when (status) {
        IssueStatus.PENDING -> 0
        IssueStatus.IN_PROGRESS -> 2
        IssueStatus.RESOLVED -> 3
        IssueStatus.REJECTED -> -1
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CivicSlate200),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RESOLUTION PROGRESS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate800,
                    letterSpacing = 0.8.sp
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CivicNavyContainer
                ) {
                    Text(
                        text = "LIVE TRACKING",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CivicNavyPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (status == IssueStatus.REJECTED) {
                Surface(
                    color = CivicRedContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = null,
                            tint = CivicRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ticket dismissed as non-actionable or duplicate.",
                            color = CivicRedText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    steps.forEachIndexed { index, (label, _) ->
                        val isCompleted = index <= currentStepIndex
                        val isCurrent = index == currentStepIndex

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = when {
                                    isCurrent -> CivicGreenPrimary
                                    isCompleted -> CivicNavyDark
                                    else -> CivicSlate200
                                },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 11.sp,
                                            color = CivicSlate600,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) CivicGreenDark else if (isCompleted) CivicSlate900 else CivicSlate400,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

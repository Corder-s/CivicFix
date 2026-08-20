package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.CivicStrings
import com.example.data.localization.civicString
import com.example.data.localization.localizedName
import com.example.data.models.CivicIssue
import com.example.data.models.IssueCategory
import com.example.data.models.IssueStatus
import com.example.ui.components.CivicButton
import com.example.ui.components.CivicButtonSize
import com.example.ui.components.CivicButtonVariant
import com.example.ui.components.HomeAiProblemSolver
import com.example.ui.components.IssueCard
import com.example.ui.components.IssueCardItem
import com.example.ui.components.StatMetricCard
import com.example.ui.components.getCategoryColor
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenLight
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyLight
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900

@Composable
fun PublicHomeScreen(
    issues: List<CivicIssue>,
    onReportClick: () -> Unit,
    onExploreClick: () -> Unit,
    onCategoryClick: (IssueCategory) -> Unit,
    onIssueClick: (String) -> Unit,
    onUpvoteClick: (CivicIssue) -> Unit,
    onAiHelpClick: () -> Unit = {},
    onPlanJourneyClick: () -> Unit = {}
) {
    val totalCount = issues.size
    val resolvedCount = issues.count { it.status == IssueStatus.RESOLVED }
    val inProgressCount = issues.count { it.status == IssueStatus.IN_PROGRESS }
    val pendingCount = issues.count { it.status == IssueStatus.PENDING }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicSlate100)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        // Section: Overview with LIVE DATA pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = civicString(CivicStrings.OVERVIEW).uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CivicSlate800,
                letterSpacing = 1.sp
            )
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CivicNavyContainer
            ) {
                Text(
                    text = civicString(CivicStrings.LIVE_DATA_BADGE).uppercase(),
                    color = CivicNavyLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Geometric 2x2 Metric Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMetricCard(
                title = civicString(CivicStrings.STAT_PENDING),
                value = String.format("%02d", pendingCount),
                icon = Icons.Default.HourglassEmpty,
                accentColor = CivicAmber,
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = civicString(CivicStrings.STAT_RESOLVED),
                value = String.format("%02d", resolvedCount),
                icon = Icons.Default.CheckCircle,
                accentColor = CivicGreenPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMetricCard(
                title = civicString(CivicStrings.STAT_IN_PROGRESS),
                value = String.format("%02d", inProgressCount),
                icon = Icons.Default.Speed,
                accentColor = CivicNavyLight,
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = civicString(CivicStrings.STAT_TOTAL_REPORTS),
                value = String.format("%02d", totalCount),
                icon = Icons.Default.Report,
                accentColor = CivicNavyPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Geometric Balance CivicButton (REPORT NEW ISSUE)
        CivicButton(
            text = civicString(CivicStrings.REPORT_ISSUE).uppercase(),
            onClick = onReportClick,
            variant = CivicButtonVariant.PRIMARY_GREEN,
            size = CivicButtonSize.LARGE,
            leadingIcon = Icons.Default.Add,
            modifier = Modifier.fillMaxWidth(),
            testTag = "report_new_issue_button"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // AI Safe Journey Planner Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CivicNavyDark,
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPlanJourneyClick() }
                .testTag("home_journey_planner_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CivicGreenPrimary,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Safe Journey",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PLAN SAFE JOURNEY",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CivicGreenPrimary
                        ) {
                            Text(
                                text = "AI",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "Real-time hazard-aware transit, bus routes & rain scores",
                        fontSize = 11.sp,
                        color = CivicSlate200,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open Journey Planner",
                    tint = CivicGreenLight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Problem Categories Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = civicString(CivicStrings.QUICK_CATEGORIES).uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CivicSlate800,
                letterSpacing = 1.sp
            )
            Text(
                text = civicString(CivicStrings.VIEW_ALL).uppercase(),
                fontSize = 11.sp,
                color = CivicNavyLight,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onExploreClick() }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Category Squircles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IssueCategory.values().forEach { category ->
                val color = getCategoryColor(category)
                val icon = getCategoryIcon(category)
                val count = issues.count { it.category == category }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, CivicSlate200),
                    modifier = Modifier
                        .clickable { onCategoryClick(category) }
                        .padding(vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = color.copy(alpha = 0.12f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = category.localizedName(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicSlate900
                            )
                            Text(
                                text = "$count active",
                                fontSize = 10.sp,
                                color = CivicSlate400
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Reports Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = civicString(CivicStrings.LIVE_GRIEVANCES).uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CivicSlate800,
                letterSpacing = 1.sp
            )
            Text(
                text = civicString(CivicStrings.VIEW_ALL).uppercase(),
                fontSize = 11.sp,
                color = CivicNavyLight,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onExploreClick() }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Recent Complaints List
        issues.take(4).forEach { issue ->
            IssueCardItem(
                issue = issue,
                onIssueClick = { onIssueClick(issue.id) },
                onUpvoteClick = { onUpvoteClick(issue) },
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

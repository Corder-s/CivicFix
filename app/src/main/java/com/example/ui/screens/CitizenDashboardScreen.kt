package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CivicIssue
import com.example.data.models.IssueStatus
import com.example.data.models.User
import com.example.ui.components.IssueCardItem
import com.example.ui.components.StatMetricCard
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicGreenContainer
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenLight
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyLight
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicRed
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900

@Composable
fun CitizenDashboardScreen(
    user: User?,
    issues: List<CivicIssue>,
    onReportClick: () -> Unit,
    onViewMyIssues: () -> Unit,
    onExploreCommunity: () -> Unit,
    onIssueClick: (String) -> Unit,
    onUpvoteClick: (CivicIssue) -> Unit
) {
    val myEmail = user?.email ?: "rahul.sharma@example.com"
    val myIssues = issues.filter { it.reportedByEmail == myEmail }

    val totalReported = myIssues.size
    val pendingCount = myIssues.count { it.status == IssueStatus.PENDING }
    val inProgressCount = myIssues.count { it.status == IssueStatus.IN_PROGRESS }
    val resolvedCount = myIssues.count { it.status == IssueStatus.RESOLVED }

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
                text = "OVERVIEW",
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
                    text = "MY STATS",
                    color = CivicNavyLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Geometric 2x2 Grid (Pending / Resolved)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMetricCard(
                title = "Pending",
                value = String.format("%02d", pendingCount),
                icon = Icons.Default.HourglassEmpty,
                accentColor = CivicAmber,
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = "Resolved",
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
                title = "Active",
                value = String.format("%02d", inProgressCount),
                icon = Icons.Default.Speed,
                accentColor = CivicNavyLight,
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = "Total Tickets",
                value = String.format("%02d", totalReported),
                icon = Icons.Default.Description,
                accentColor = CivicNavyPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Primary Action Button (Geometric #2E7D32 with rounded-2xl)
        Button(
            onClick = onReportClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = CivicGreenPrimary,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = CivicGreenDark.copy(alpha = 0.3f))
                .testTag("dashboard_report_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "REPORT NEW ISSUE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Reports Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT REPORTS",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CivicSlate800,
                letterSpacing = 1.sp
            )
            Text(
                text = "VIEW ALL (${totalReported})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CivicNavyLight,
                modifier = Modifier.clickable { onViewMyIssues() }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (myIssues.isEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CivicSlate200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CivicNavyContainer,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = CivicNavyPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No tickets reported yet",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Report civic issues in your area to track resolution.",
                        fontSize = 11.sp,
                        color = CivicSlate600
                    )
                }
            }
        } else {
            myIssues.take(3).forEach { issue ->
                IssueCardItem(
                    issue = issue,
                    onIssueClick = { onIssueClick(issue.id) },
                    onUpvoteClick = { onUpvoteClick(issue) },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Community Resolved Highlights
        Text(
            text = "RECENTLY RESOLVED IN YOUR WARD",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CivicSlate800,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        issues.filter { it.status == IssueStatus.RESOLVED }.take(2).forEach { resolvedIssue ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CivicSlate200),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clickable { onIssueClick(resolvedIssue.id) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CivicGreenContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CivicGreenDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = resolvedIssue.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicSlate900,
                            maxLines = 1
                        )
                        Text(
                            text = "${resolvedIssue.location} • Resolved by ${resolvedIssue.assignedDepartment.displayName}",
                            fontSize = 11.sp,
                            color = CivicSlate600,
                            maxLines = 1
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = CivicSlate400,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

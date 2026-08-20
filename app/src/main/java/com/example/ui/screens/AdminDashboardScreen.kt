package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CivicIssue
import com.example.data.models.Department
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.data.models.IssueStatus
import com.example.data.models.User
import com.example.data.services.CivicMobilityScore
import com.example.data.services.PredictiveHazard
import com.example.ui.components.CivicPriorityBadge
import com.example.ui.components.CivicStatusBadge
import com.example.ui.components.StatMetricCard
import com.example.ui.components.getCategoryColor
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicGreenContainer
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyLight
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicOrangeContainer
import com.example.ui.theme.CivicOrangeDark
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

@Composable
fun AdminDashboardScreen(
    adminUser: User?,
    issues: List<CivicIssue>,
    citizens: List<User>,
    predictiveHazards: List<PredictiveHazard> = emptyList(),
    civicMobilityScores: List<CivicMobilityScore> = emptyList(),
    onManageIssues: () -> Unit,
    onManageUsers: () -> Unit,
    onIssueClick: (String) -> Unit
) {
    val totalIssues = issues.size
    val pendingCount = issues.count { it.status == IssueStatus.PENDING }
    val inProgressCount = issues.count { it.status == IssueStatus.IN_PROGRESS }
    val resolvedCount = issues.count { it.status == IssueStatus.RESOLVED }
    val highPriorityCount = issues.count { it.priority == IssuePriority.HIGH && it.status != IssueStatus.RESOLVED }
    val totalCitizens = citizens.size

    val resolutionRate = if (totalIssues > 0) (resolvedCount.toFloat() / totalIssues * 100).toInt() else 0

    // Mobility metrics calculation
    val activeHazardsCount = predictiveHazards.size
    val highRiskRoadsCount = predictiveHazards.count { it.riskLevel == "HIGH" }
    val weatherIncidentsCount = predictiveHazards.count { it.hazardType.contains("Flood", ignoreCase = true) || it.hazardType.contains("Water", ignoreCase = true) }
    val affectedBusRoutes = predictiveHazards.flatMap { it.affectedTransitRoutes }.distinct()

    val averageMobilityScore = if (civicMobilityScores.isNotEmpty()) {
        civicMobilityScores.map { it.overallScore }.average().toInt()
    } else 78

    var selectedWardIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicSlate100)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Admin Command Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CivicNavyDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CivicAmber,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Municipal Operations Command",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Officer: ${adminUser?.name ?: "Priya Verma (Zonal Head)"}",
                                fontSize = 11.sp,
                                color = CivicSlate400
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CivicGreenContainer
                    ) {
                        Text(
                            text = "$resolutionRate% Resolved",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicGreenDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 🚦 LIVE CIVIC MOBILITY & INFRASTRUCTURE ANALYTICS
        // ==========================================
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("mobility_analytics_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Traffic,
                            contentDescription = null,
                            tint = CivicNavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "City Mobility & Hazard Analytics",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicSlate900
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (averageMobilityScore >= 75) CivicGreenContainer else CivicOrangeContainer
                    ) {
                        Text(
                            text = "City Score: $averageMobilityScore/100",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (averageMobilityScore >= 75) CivicGreenDark else CivicOrangeDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mobility 4-Stat Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatMetricCard(
                        title = "Active Hazards",
                        value = "$activeHazardsCount",
                        icon = Icons.Default.Warning,
                        accentColor = CivicRed,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = "High-Risk Roads",
                        value = "$highRiskRoadsCount",
                        icon = Icons.Default.Speed,
                        accentColor = CivicOrangePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = "Affected Buses",
                        value = "${affectedBusRoutes.size}",
                        icon = Icons.Default.DirectionsBus,
                        accentColor = CivicNavyLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = "Weather Events",
                        value = "$weatherIncidentsCount",
                        icon = Icons.Default.Thunderstorm,
                        accentColor = Color(0xFF0288D1),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Risk Distribution Chart (High / Moderate / Safe)
                Text(
                    text = "Urban Road Risk Distribution",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate800
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                ) {
                    Box(modifier = Modifier.weight(0.18f).background(CivicRed))
                    Box(modifier = Modifier.weight(0.28f).background(CivicAmber))
                    Box(modifier = Modifier.weight(0.54f).background(CivicGreenPrimary))
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("🔴 High Risk (18%)", fontSize = 10.sp, color = CivicRed, fontWeight = FontWeight.Bold)
                    Text("🟡 Moderate (28%)", fontSize = 10.sp, color = Color(0xFFD97706), fontWeight = FontWeight.Bold)
                    Text("🟢 Safe Roads (54%)", fontSize = 10.sp, color = CivicGreenDark, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 🏛️ CIVIC MOBILITY SCORES BY WARD
        // ==========================================
        if (civicMobilityScores.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ward Mobility Health Index",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate900
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Ward Selector Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        civicMobilityScores.forEachIndexed { index, wardScore ->
                            FilterChip(
                                selected = selectedWardIndex == index,
                                onClick = { selectedWardIndex = index },
                                label = { Text(wardScore.wardName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CivicNavyDark,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val selectedWard = civicMobilityScores.getOrNull(selectedWardIndex) ?: civicMobilityScores.first()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedWard.wardName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = CivicNavyDark
                            )
                            Text(
                                text = "Composite Urban Mobility Index",
                                fontSize = 11.sp,
                                color = CivicSlate600
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedWard.overallScore >= 80) CivicGreenContainer else CivicAmber.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${selectedWard.overallScore}/100",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = if (selectedWard.overallScore >= 80) CivicGreenDark else Color(0xFFD97706),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Ward Sub-metrics Progress Bars
                    WardMetricBar(label = "Road Surface Quality", score = selectedWard.roadQualityScore, color = CivicGreenPrimary)
                    WardMetricBar(label = "Monsoon Drainage & Flood Safety", score = selectedWard.floodSafetyScore, color = if (selectedWard.floodSafetyScore < 70) CivicRed else CivicAmber)
                    WardMetricBar(label = "Public Transit & Bus Connectivity", score = selectedWard.publicTransitScore, color = CivicNavyPrimary)
                    WardMetricBar(label = "Street Lighting & Night Safety", score = selectedWard.lightingSecurityScore, color = Color(0xFF673AB7))
                    WardMetricBar(label = "Grievance Resolution Velocity", score = selectedWard.issueResolutionSpeed, color = Color(0xFF00897B))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // ==========================================
        // ⚠️ HIGH RISK ROADS & PROBLEM AREAS
        // ==========================================
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = CivicRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "High-Risk Roads & Urban Hotspots",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate900
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                predictiveHazards.take(4).forEach { hazard ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (hazard.riskLevel == "HIGH") CivicRedContainer.copy(alpha = 0.5f) else CivicOrangeContainer.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = hazard.roadName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CivicSlate900
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (hazard.riskLevel == "HIGH") CivicRed else CivicAmber
                                ) {
                                    Text(
                                        text = "${hazard.riskScore}/100 Risk",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${hazard.hazardType} • Impact: ${hazard.commuterImpact}",
                                fontSize = 11.sp,
                                color = CivicSlate800,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Action: ${hazard.recommendedMitigation}",
                                fontSize = 11.sp,
                                color = CivicNavyDark
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 6-Card Municipal Stats Row 1
        Text(
            text = "Grievance Redressal Metrics",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CivicSlate900
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatMetricCard(
                title = "Total Complaints",
                value = "$totalIssues",
                icon = Icons.Default.Report,
                accentColor = CivicNavyPrimary,
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = "Pending Action",
                value = "$pendingCount",
                icon = Icons.Default.HourglassEmpty,
                accentColor = CivicAmber,
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = "In Progress",
                value = "$inProgressCount",
                icon = Icons.Default.Speed,
                accentColor = CivicNavyLight,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatMetricCard(
                title = "Resolved Tickets",
                value = "$resolvedCount",
                icon = Icons.Default.CheckCircle,
                accentColor = CivicGreenPrimary,
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = "Urgent Alerts",
                value = "$highPriorityCount",
                icon = Icons.Default.PriorityHigh,
                accentColor = CivicRed,
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = "Registered Users",
                value = "$totalCitizens",
                icon = Icons.Default.Group,
                accentColor = CivicSlate800,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Category Breakdown Chart Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Grievance Breakdown by Civic Category",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate900
                )
                Spacer(modifier = Modifier.height(14.dp))

                IssueCategory.values().forEach { cat ->
                    val catIssues = issues.count { it.category == cat }
                    val fraction = if (totalIssues > 0) catIssues.toFloat() / totalIssues else 0f
                    val color = getCategoryColor(cat)
                    val icon = getCategoryIcon(cat)

                    Column(modifier = Modifier.padding(bottom = 10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(cat.displayName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CivicSlate800)
                            }
                            Text("$catIssues complaints (${(fraction * 100).toInt()}%)", fontSize = 11.sp, color = CivicSlate600)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { fraction },
                            color = color,
                            trackColor = CivicSlate100,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // High Priority Urgent Queue
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = CivicRed, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "High Priority Redressal Queue",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate900
                )
            }
            Text(
                text = "Manage All",
                fontSize = 12.sp,
                color = CivicNavyLight,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onManageIssues() }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        val urgentIssues = issues.filter { it.priority == IssuePriority.HIGH && it.status != IssueStatus.RESOLVED }

        if (urgentIssues.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CivicGreenContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CivicGreenDark, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("No urgent complaints pending. Great job!", color = CivicGreenDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            urgentIssues.take(3).forEach { issue ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { onIssueClick(issue.id) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(issue.id, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CivicNavyPrimary)
                            CivicStatusBadge(status = issue.status)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(issue.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CivicSlate900)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${issue.location} • ${issue.upvotes} Citizens Impacted",
                            fontSize = 11.sp,
                            color = CivicSlate600
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onManageIssues,
                colors = ButtonDefaults.buttonColors(containerColor = CivicNavyPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("admin_manage_issues_button")
            ) {
                Icon(Icons.Default.Report, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("All Complaints", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onManageUsers,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Group, contentDescription = null, tint = CivicNavyPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Manage Users", fontWeight = FontWeight.Bold, color = CivicNavyPrimary)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun WardMetricBar(
    label: String,
    score: Int,
    color: Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 11.sp, color = CivicSlate800, fontWeight = FontWeight.Medium)
            Text("$score%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            color = color,
            trackColor = CivicSlate100,
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
        )
    }
}

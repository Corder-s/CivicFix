package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicAmberContainer
import com.example.ui.theme.CivicAmberText
import com.example.ui.theme.CivicGreenContainer
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenLight
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyLight
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicRed
import com.example.ui.theme.CivicRedContainer
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Data class representing an AI Problem Diagnosis result.
 */
data class AiProblemDiagnosis(
    val problemTitle: String,
    val identifiedCategory: IssueCategory,
    val estimatedPriority: IssuePriority,
    val responsibleDepartment: String,
    val slaTimeline: String,
    val aiSolutionText: String,
    val actionableGuidance: List<String>,
    val helplineNumber: String = "1913"
)

/**
 * Interactive AI Problem Solver Agent Widget embedded right on the Home Page.
 * Allows citizens to describe civic problems, receive real-time AI diagnosis & triage,
 * and auto-draft complaints directly into the municipal pipeline with 1 tap.
 */
@Composable
fun HomeAiProblemSolver(
    onReportIssueWithDraft: (title: String, description: String, category: IssueCategory, priority: IssuePriority) -> Unit,
    onOpenFullAiChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var problemQuery by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var diagnosisResult by remember { mutableStateOf<AiProblemDiagnosis?>(null) }

    fun solveProblem(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        isAnalyzing = true
        coroutineScope.launch {
            delay(650) // Realistic AI synthesis latency
            diagnosisResult = diagnoseCivicProblem(trimmed)
            isAnalyzing = false
        }
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_ai_problem_solver_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row with Glowing AI Pill Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Agent",
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CivicFix AI Problem Solver",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicNavyDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Text(
                                    text = "LIVE AGENT",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF15803D),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Instant diagnosis, SLA routing & automatic grievance filing",
                            fontSize = 11.5.sp,
                            color = CivicSlate600
                        )
                    }
                }

                // Expand Full Chat Button
                IconButton(
                    onClick = onOpenFullAiChat,
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color(0xFFF1F5F9), CircleShape)
                        .testTag("open_full_ai_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "Full Chat",
                        tint = CivicNavyDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Prompt Problem Chips (One-tap issue selectors)
            val quickChips = listOf(
                "🚧 Deep Pothole on Road",
                "💡 Broken Street Light",
                "💧 Pipeline Burst & Leak",
                "🗑️ Overflowing Waste Dump",
                "⚡ Power Line Sparks",
                "🌳 Fallen Tree Branch"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickChips.forEach { chipText ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .clickable {
                                problemQuery = chipText
                                solveProblem(chipText)
                            }
                    ) {
                        Text(
                            text = chipText,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = CivicNavyDark,
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Problem Input Text Box with Sunny Action Button
            OutlinedTextField(
                value = problemQuery,
                onValueChange = { problemQuery = it },
                placeholder = {
                    Text(
                        text = "Describe your civic issue (e.g. Broken water pipe near 5th Ave)",
                        fontSize = 13.sp,
                        color = CivicSlate400
                    )
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        if (problemQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    problemQuery = ""
                                    diagnosisResult = null
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = CivicSlate400,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Submit Query Button
                        Surface(
                            shape = CircleShape,
                            color = if (problemQuery.isNotBlank() && !isAnalyzing) Color(0xFFFBBF24) else Color(0xFFE2E8F0),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable(enabled = problemQuery.isNotBlank() && !isAnalyzing) {
                                    solveProblem(problemQuery)
                                }
                                .testTag("solve_problem_submit_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isAnalyzing) {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                        color = CivicNavyDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Diagnose",
                                        tint = if (problemQuery.isNotBlank()) Color(0xFF1E293B) else CivicSlate400,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { solveProblem(problemQuery) }),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFBBF24),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color(0xFFFFFDF8),
                    unfocusedContainerColor = Color(0xFFF8FAFC),
                    focusedTextColor = CivicSlate900,
                    unfocusedTextColor = CivicSlate900
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_problem_query_input")
            )

            // Real-time AI Diagnosis and Resolution Result Panel
            AnimatedVisibility(
                visible = diagnosisResult != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                diagnosisResult?.let { diag ->
                    Column(
                        modifier = Modifier
                            .padding(top = 14.dp)
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        // AI Diagnosis Title & Category Tag
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🤖 AI Diagnosis & Resolution Plan",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicNavyDark
                            )

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = when (diag.estimatedPriority) {
                                    IssuePriority.HIGH -> CivicRedContainer
                                    IssuePriority.MEDIUM -> CivicAmberContainer
                                    else -> CivicGreenContainer
                                }
                            ) {
                                Text(
                                    text = "${diag.estimatedPriority.name} PRIORITY",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when (diag.estimatedPriority) {
                                        IssuePriority.HIGH -> CivicRed
                                        IssuePriority.MEDIUM -> CivicAmberText
                                        else -> CivicGreenDark
                                    },
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Routing Metadata Grid (Dept & SLA)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = CivicNavyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Column {
                                        Text(text = "Authority", fontSize = 9.5.sp, color = CivicSlate400)
                                        Text(
                                            text = diag.responsibleDepartment,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CivicNavyDark,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Column {
                                        Text(text = "Expected SLA", fontSize = 9.5.sp, color = CivicSlate400)
                                        Text(
                                            text = diag.slaTimeline,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // AI Explanation text
                        Text(
                            text = diag.aiSolutionText,
                            fontSize = 12.5.sp,
                            color = CivicSlate800,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Actionable Bullet Items
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            diag.actionableGuidance.forEach { step ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = CivicGreenPrimary,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .padding(top = 1.dp)
                                    )
                                    Text(
                                        text = step,
                                        fontSize = 11.5.sp,
                                        color = CivicSlate600,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 1-Tap Auto-Draft & File Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onReportIssueWithDraft(
                                        diag.problemTitle,
                                        diag.aiSolutionText,
                                        diag.identifiedCategory,
                                        diag.estimatedPriority
                                    )
                                    Toast.makeText(context, "Opening pre-filled grievance report...", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24)),
                                modifier = Modifier
                                    .weight(1.4f)
                                    .height(42.dp)
                                    .testTag("auto_draft_issue_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = Color(0xFF1E293B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Auto-Draft & File",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                            }

                            // Emergency Helpline Button
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "Calling Municipal Helpline: ${diag.helplineNumber}", Toast.LENGTH_LONG).show()
                                },
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("call_helpline_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = CivicNavyDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Call ${diag.helplineNumber}",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CivicNavyDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Heuristic & Knowledge Engine for Instant Civic Problem Triage
 */
private fun diagnoseCivicProblem(query: String): AiProblemDiagnosis {
    val q = query.lowercase()

    return when {
        q.contains("pothole") || q.contains("road") || q.contains("tarmac") || q.contains("crater") || q.contains("footpath") -> {
            AiProblemDiagnosis(
                problemTitle = if (query.length > 40) query.take(40) + "..." else "Road Surface Damage & Pothole Hazard",
                identifiedCategory = IssueCategory.ROADS,
                estimatedPriority = IssuePriority.HIGH,
                responsibleDepartment = "Public Works Dept (PWD)",
                slaTimeline = "24 to 48 Hours",
                aiSolutionText = "Road defect detected on transit corridor. PWD rapid-patch crew is notified to dispatch asphalt mixing units to prevent vehicle damage and accidents.",
                actionableGuidance = listOf(
                    "High traffic risk: Place caution markers if safe to do so.",
                    "Capture clear photos showing approximate width and depth.",
                    "Submit GPS coordinates for accurate dispatch."
                )
            )
        }
        q.contains("water") || q.contains("pipe") || q.contains("leak") || q.contains("drain") || q.contains("flood") || q.contains("sewage") -> {
            AiProblemDiagnosis(
                problemTitle = if (query.length > 40) query.take(40) + "..." else "Water Main Supply Leakage / Pipeline Break",
                identifiedCategory = IssueCategory.WATER,
                estimatedPriority = IssuePriority.HIGH,
                responsibleDepartment = "Municipal Water Board",
                slaTimeline = "12 to 24 Hours",
                aiSolutionText = "Potable water supply or drainage leakage identified. Valve control operations will isolate the burst section to restore standard water pressure.",
                actionableGuidance = listOf(
                    "Avoid touching water near electrical poles or transformers.",
                    "Nearby households advised to store reserve water during repair.",
                    "File complaint with landmark for quickest valve shutdown."
                )
            )
        }
        q.contains("light") || q.contains("lamp") || q.contains("dark") || q.contains("bulb") || q.contains("pole") -> {
            AiProblemDiagnosis(
                problemTitle = if (query.length > 40) query.take(40) + "..." else "Non-Functional Street Luminaire / Dark Zone",
                identifiedCategory = IssueCategory.STREETLIGHT,
                estimatedPriority = IssuePriority.MEDIUM,
                responsibleDepartment = "Electrical Maintenance Wing",
                slaTimeline = "24 to 72 Hours",
                aiSolutionText = "Streetlight failure causing low nighttime visibility. The zonal electrical maintenance team will inspect circuit breaker panels and replace LED fixtures.",
                actionableGuidance = listOf(
                    "Include the pole number stenciled on the mast if visible.",
                    "Indicate if multiple consecutive streetlights are out of order.",
                    "Pedestrians advised to use alternative illuminated routes."
                )
            )
        }
        q.contains("waste") || q.contains("garbage") || q.contains("trash") || q.contains("dump") || q.contains("bin") || q.contains("smell") -> {
            AiProblemDiagnosis(
                problemTitle = if (query.length > 40) query.take(40) + "..." else "Illegal Waste Dumping & Bin Overflow",
                identifiedCategory = IssueCategory.GARBAGE,
                estimatedPriority = IssuePriority.MEDIUM,
                responsibleDepartment = "Solid Waste Management (SWM)",
                slaTimeline = "24 Hours SLA",
                aiSolutionText = "Accumulated municipal solid waste identified. Sanitation compactor truck will be routed to clear the site and sanitize the perimeter.",
                actionableGuidance = listOf(
                    "Avoid burning waste which violates clean air ordinances.",
                    "Commercial dumpers will be checked by municipal ward marshals.",
                    "File with landmark for prioritized dumper truck routing."
                )
            )
        }
        q.contains("power") || q.contains("spark") || q.contains("wire") || q.contains("transformer") || q.contains("electric") -> {
            AiProblemDiagnosis(
                problemTitle = if (query.length > 40) query.take(40) + "..." else "Exposed Electrical Hazard / Power Fault",
                identifiedCategory = IssueCategory.ELECTRICITY,
                estimatedPriority = IssuePriority.HIGH,
                responsibleDepartment = "Electricity Distribution Board",
                slaTimeline = "2 to 6 Hours Emergency",
                aiSolutionText = "Urgent electrical fault. A fast-response emergency line team is alerted to cut power supply to hazardous loose wires and repair circuit breakers.",
                actionableGuidance = listOf(
                    "🚨 MAINTAIN AT LEAST 10 METERS SAFE DISTANCE.",
                    "Do not attempt to touch loose wires with wooden sticks.",
                    "Alert nearby residents and call emergency helpline 1912."
                )
            )
        }
        else -> {
            AiProblemDiagnosis(
                problemTitle = if (query.length > 40) query.take(40) + "..." else "Civic Community Grievance",
                identifiedCategory = IssueCategory.OTHER,
                estimatedPriority = IssuePriority.MEDIUM,
                responsibleDepartment = "Municipal Ward Office",
                slaTimeline = "48 to 72 Hours",
                aiSolutionText = "Civic inquiry recorded. CivicFix AI has routed this ticket to your local Municipal Ward Officer for automated inspection and redressal.",
                actionableGuidance = listOf(
                    "Attach a photo or precise location for faster response.",
                    "Track ticket status and receive live officer notifications."
                )
            )
        }
    }
}

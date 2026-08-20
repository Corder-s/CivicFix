package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicGreenPrimary

data class HelpFaqItem(
    val category: String,
    val question: String,
    val answer: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpAndFeedbackDialog(
    initialTab: Int = 0,
    onDismiss: () -> Unit,
    onSubmitFeedback: (type: String, subject: String, message: String, rating: Int) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var isSubmitted by remember { mutableStateOf(false) }

    val faqs = remember {
        listOf(
            HelpFaqItem(
                category = "Reporting Issues",
                question = "How do I report a civic problem?",
                answer = "Tap the '+ Report Issue' button from the home screen or navigation bar. Choose the relevant category (Roads, Water, Waste, etc.), add an accurate landmark or address, take clear photos, and select the estimated urgency. You'll receive a unique tracking Complaint ID (e.g. CIV-2026-0081).",
                icon = Icons.Default.Report
            ),
            HelpFaqItem(
                category = "Tracking Complaints",
                question = "How do I check the progress of my complaint?",
                answer = "Navigate to 'My Reports' in your profile or use the CivicFix AI Assistant to search your Complaint ID. You will see real-time updates: 'Pending' (waiting assignment), 'In Progress' (crew on site), 'Resolved' (verified repair), or 'Rejected' with official officer notes.",
                icon = Icons.Default.TrackChanges
            ),
            HelpFaqItem(
                category = "Community Upvotes",
                question = "What does upvoting an issue do?",
                answer = "Upvoting signals collective citizen impact to municipal ward officers. Highly upvoted community grievances automatically gain higher priority ranking on the Admin Triage Board, expediting budget and work order allocation.",
                icon = Icons.Default.ThumbUp
            ),
            HelpFaqItem(
                category = "AI Assistant",
                question = "What can CivicFix AI help me with?",
                answer = "CivicFix AI is your 24/7 civic copilot. It diagnoses issues from conversational descriptions in English, Hindi, and Hinglish, auto-drafts complaints, routes tickets to the correct department (PWD, Water Board, Electricity), and provides instant emergency helplines.",
                icon = Icons.Default.AutoAwesome
            ),
            HelpFaqItem(
                category = "Language Settings",
                question = "How do I switch the application language?",
                answer = "Tap the language pill in the top app bar or go to Profile & Settings → App Language. You can choose from 13 Indian languages (Hindi, Bengali, Telugu, Marathi, Tamil, Gujarati, Kannada, Malayalam, Punjabi, Odia, Assamese, Urdu). Your choice is saved automatically.",
                icon = Icons.Default.Language
            ),
            HelpFaqItem(
                category = "Account Management",
                question = "How do I update my profile or phone number?",
                answer = "Open 'Profile & Settings', tap 'Edit Profile Details' or 'Change Password'. You can also switch between Citizen and Municipal Admin Officer demo roles at any time.",
                icon = Icons.Default.ManageAccounts
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (selectedTab == 0) Icons.Default.HelpOutline else Icons.Default.Feedback,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (selectedTab == 0) "Help Center" else "Send Feedback",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
            ) {
                // Top Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("FAQs & Guide", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Send Feedback", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedTab == 0) {
                    // Help Center FAQs List
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        items(faqs) { item ->
                            var isExpanded by remember { mutableStateOf(false) }
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isExpanded = !isExpanded }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = item.icon,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = item.question,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    AnimatedVisibility(visible = isExpanded) {
                                        Column(modifier = Modifier.padding(top = 8.dp, start = 36.dp)) {
                                            Text(
                                                text = item.answer,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                lineHeight = 17.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Send Feedback Form
                    if (isSubmitted) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Submitted",
                                tint = CivicGreenPrimary,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Feedback Received!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Thank you for helping us improve CivicFix. Your suggestions are shared directly with the municipal digital governance team.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Done")
                            }
                        }
                    } else {
                        var feedbackType by remember { mutableStateOf("Bug Report") }
                        var subject by remember { mutableStateOf("") }
                        var message by remember { mutableStateOf("") }
                        var rating by remember { mutableIntStateOf(5) }
                        var dropdownExpanded by remember { mutableStateOf(false) }

                        val feedbackTypes = listOf("Bug Report", "Feature Request", "Civic Suggestion", "App Experience", "Municipal Coordination")

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            item {
                                Text(
                                    text = "Feedback Category",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                ExposedDropdownMenuBox(
                                    expanded = dropdownExpanded,
                                    onExpandedChange = { dropdownExpanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = feedbackType,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                        )
                                    )
                                    ExposedDropdownMenu(
                                        expanded = dropdownExpanded,
                                        onDismissRequest = { dropdownExpanded = false }
                                    ) {
                                        feedbackTypes.forEach { type ->
                                            DropdownMenuItem(
                                                text = { Text(type) },
                                                onClick = {
                                                    feedbackType = type
                                                    dropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = "Rate Experience (Optional)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    (1..5).forEach { starIndex ->
                                        Icon(
                                            imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "Star $starIndex",
                                            tint = CivicAmber,
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clickable { rating = starIndex }
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when (rating) {
                                            5 -> "Excellent"
                                            4 -> "Very Good"
                                            3 -> "Average"
                                            2 -> "Needs Work"
                                            else -> "Poor"
                                        },
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            item {
                                OutlinedTextField(
                                    value = subject,
                                    onValueChange = { subject = it },
                                    label = { Text("Subject") },
                                    placeholder = { Text("Short summary of your feedback") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = message,
                                    onValueChange = { message = it },
                                    label = { Text("Message") },
                                    placeholder = { Text("Describe the issue or share your suggestion in detail...") },
                                    minLines = 3,
                                    maxLines = 5,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            item {
                                Button(
                                    onClick = {
                                        if (message.isNotBlank() || subject.isNotBlank()) {
                                            onSubmitFeedback(
                                                feedbackType,
                                                subject.ifBlank { "App Feedback" },
                                                message.ifBlank { "General rating feedback" },
                                                rating
                                            )
                                            isSubmitted = true
                                        }
                                    },
                                    enabled = subject.isNotBlank() || message.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("btn_submit_feedback")
                                ) {
                                    Text("Submit Feedback", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isSubmitted) {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}

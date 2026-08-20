package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CivicIssue
import com.example.data.models.IssueStatus
import com.example.data.models.User
import com.example.ui.components.IssueCardItem
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyIssuesScreen(
    user: User?,
    issues: List<CivicIssue>,
    onReportClick: () -> Unit,
    onIssueClick: (String) -> Unit,
    onUpvoteClick: (CivicIssue) -> Unit
) {
    val myEmail = user?.email ?: "rahul.sharma@example.com"
    val myIssues = issues.filter { it.reportedByEmail == myEmail }

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<IssueStatus?>(null) }

    val filtered = myIssues.filter { issue ->
        val matchesSearch = searchQuery.isBlank() ||
                issue.title.contains(searchQuery, ignoreCase = true) ||
                issue.id.contains(searchQuery, ignoreCase = true) ||
                issue.location.contains(searchQuery, ignoreCase = true)

        val matchesStatus = selectedStatus == null || issue.status == selectedStatus

        matchesSearch && matchesStatus
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicSlate100)
    ) {
        // Search & Filter Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Grievances (${myIssues.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate900
                    )

                    Button(
                        onClick = onReportClick,
                        colors = ButtonDefaults.buttonColors(containerColor = CivicGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Report", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search my reports by keyword or ID...", fontSize = 13.sp, color = CivicSlate400) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = CivicSlate400)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null, tint = CivicSlate400)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CivicNavyPrimary,
                        unfocusedBorderColor = CivicSlate200,
                        focusedContainerColor = CivicSlate100,
                        unfocusedContainerColor = CivicSlate100
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { selectedStatus = null },
                        label = { Text("All", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CivicNavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                    IssueStatus.values().forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = if (selectedStatus == status) null else status },
                            label = { Text(status.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CivicNavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = CivicSlate400,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (myIssues.isEmpty()) "No complaints filed yet" else "No complaints match filter",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (myIssues.isEmpty()) "Report problems in your area to get fast municipal action." else "Try adjusting search or status filters.",
                        fontSize = 12.sp,
                        color = CivicSlate600
                    )
                    if (myIssues.isEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onReportClick,
                            colors = ButtonDefaults.buttonColors(containerColor = CivicGreenPrimary)
                        ) {
                            Text("File a Complaint")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { issue ->
                    IssueCardItem(
                        issue = issue,
                        onIssueClick = { onIssueClick(issue.id) },
                        onUpvoteClick = { onUpvoteClick(issue) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

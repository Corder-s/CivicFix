package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import com.example.data.models.Department
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.data.models.IssueStatus
import com.example.ui.components.CivicPriorityBadge
import com.example.ui.components.CivicStatusBadge
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicRed
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminIssueManagementScreen(
    issues: List<CivicIssue>,
    onIssueClick: (String) -> Unit,
    onAdminUpdate: (
        issue: CivicIssue,
        newStatus: IssueStatus,
        newDept: Department,
        newPriority: IssuePriority,
        officialResponse: String?,
        officerName: String?
    ) -> Unit,
    onDeleteIssue: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<IssueStatus?>(null) }
    var selectedCategory by remember { mutableStateOf<IssueCategory?>(null) }
    var selectedPriority by remember { mutableStateOf<IssuePriority?>(null) }

    var triageModalIssue by remember { mutableStateOf<CivicIssue?>(null) }
    var deleteConfirmIssueId by remember { mutableStateOf<String?>(null) }

    val filteredIssues = issues.filter { issue ->
        val matchesSearch = searchQuery.isBlank() ||
                issue.title.contains(searchQuery, ignoreCase = true) ||
                issue.id.contains(searchQuery, ignoreCase = true) ||
                issue.location.contains(searchQuery, ignoreCase = true) ||
                issue.reportedByName.contains(searchQuery, ignoreCase = true)

        val matchesStatus = selectedStatus == null || issue.status == selectedStatus
        val matchesCategory = selectedCategory == null || issue.category == selectedCategory
        val matchesPriority = selectedPriority == null || issue.priority == selectedPriority

        matchesSearch && matchesStatus && matchesCategory && matchesPriority
    }

    // Modal for quick status triage update
    if (triageModalIssue != null) {
        val cur = triageModalIssue!!
        var modalStatus by remember { mutableStateOf(cur.status) }
        var modalDept by remember { mutableStateOf(cur.assignedDepartment) }
        var modalPriority by remember { mutableStateOf(cur.priority) }
        var modalResponse by remember { mutableStateOf(cur.authorityResponse ?: "") }

        var showStatusDropdown by remember { mutableStateOf(false) }
        var showDeptDropdown by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { triageModalIssue = null },
            title = {
                Text(
                    text = "Triage Complaint: ${cur.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = cur.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = CivicSlate900
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Status:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Box {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CivicSlate100,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showStatusDropdown = true }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(modalStatus.displayName, fontWeight = FontWeight.Bold)
                                CivicStatusBadge(status = modalStatus)
                            }
                        }

                        DropdownMenu(
                            expanded = showStatusDropdown,
                            onDismissRequest = { showStatusDropdown = false }
                        ) {
                            IssueStatus.values().forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st.displayName) },
                                    onClick = {
                                        modalStatus = st
                                        showStatusDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Assign Department:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Box {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CivicSlate100,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDeptDropdown = true }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(modalDept.displayName, modifier = Modifier.padding(10.dp), fontSize = 13.sp)
                        }

                        DropdownMenu(
                            expanded = showDeptDropdown,
                            onDismissRequest = { showDeptDropdown = false }
                        ) {
                            Department.values().forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d.displayName) },
                                    onClick = {
                                        modalDept = d
                                        showDeptDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Official Authority Response:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = modalResponse,
                        onValueChange = { modalResponse = it },
                        placeholder = { Text("Enter official redressal note / crew action...", fontSize = 12.sp) },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAdminUpdate(
                            cur,
                            modalStatus,
                            modalDept,
                            modalPriority,
                            modalResponse,
                            "Zonal Officer Priya Verma"
                        )
                        triageModalIssue = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivicNavyPrimary)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { triageModalIssue = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (deleteConfirmIssueId != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirmIssueId = null },
            title = { Text("Delete Complaint Record?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete ticket $deleteConfirmIssueId?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteIssue(deleteConfirmIssueId!!)
                        deleteConfirmIssueId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivicRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { deleteConfirmIssueId = null }) {
                    Text("Cancel")
                }
            }
        )
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
                Text(
                    text = "Municipal Grievance Registry",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate900
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by ticket ID, citizen, area, keyword...", fontSize = 13.sp, color = CivicSlate400) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CivicSlate400) },
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

                // Status Pills Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { selectedStatus = null },
                        label = { Text("All (${issues.size})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CivicNavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                    IssueStatus.values().forEach { status ->
                        val count = issues.count { it.status == status }
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = if (selectedStatus == status) null else status },
                            label = { Text("${status.displayName} ($count)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CivicNavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Issue List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredIssues, key = { it.id }) { issue ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onIssueClick(issue.id) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = issue.id,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CivicNavyPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${issue.category.displayName}",
                                    fontSize = 11.sp,
                                    color = CivicSlate600
                                )
                            }
                            CivicStatusBadge(status = issue.status)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = issue.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicSlate900
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Citizen: ${issue.reportedByName} • ${issue.location}",
                            fontSize = 11.sp,
                            color = CivicSlate600
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
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
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CivicGreenPrimary,
                                    modifier = Modifier.clickable { triageModalIssue = issue }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Quick Triage", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CivicRed.copy(alpha = 0.15f),
                                    modifier = Modifier.clickable { deleteConfirmIssueId = issue.id }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CivicRed, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

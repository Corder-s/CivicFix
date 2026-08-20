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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.data.models.CivicIssue
import com.example.data.models.Department
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.data.models.IssueStatus
import com.example.data.models.UserRole
import com.example.ui.components.CivicButton
import com.example.ui.components.CivicButtonSize
import com.example.ui.components.CivicButtonVariant
import com.example.ui.components.CivicPriorityBadge
import com.example.ui.components.CivicStatusBadge
import com.example.ui.components.StatusTimelineStepper
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
fun IssueDetailScreen(
    issue: CivicIssue?,
    currentRole: UserRole,
    onBack: () -> Unit,
    onUpvoteClick: (CivicIssue) -> Unit,
    onAdminUpdate: (
        issue: CivicIssue,
        newStatus: IssueStatus,
        newDept: Department,
        newPriority: IssuePriority,
        officialResponse: String?,
        officerName: String?
    ) -> Unit
) {
    if (issue == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Issue not found.", color = CivicSlate600)
        }
        return
    }

    val catColor = getCategoryColor(issue.category)
    val catIcon = getCategoryIcon(issue.category)
    val formattedDate = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(Date(issue.reportedTimestamp))

    var isEditingAdmin by remember { mutableStateOf(false) }
    var editStatus by remember { mutableStateOf(issue.status) }
    var editDept by remember { mutableStateOf(issue.assignedDepartment) }
    var editPriority by remember { mutableStateOf(issue.priority) }
    var editResponseText by remember { mutableStateOf(issue.authorityResponse ?: "") }
    var editOfficerName by remember { mutableStateOf(issue.authorityOfficerName ?: "Zonal Officer Priya Verma") }

    var showStatusMenu by remember { mutableStateOf(false) }
    var showDeptMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicSlate100)
    ) {
        // Top Navigation Bar
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = CivicNavyDark,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            ),
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            title = {
                Column {
                    Text(
                        text = "Complaint Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = issue.id,
                        fontSize = 11.sp,
                        color = CivicSlate400
                    )
                }
            },
            actions = {
                if (currentRole == UserRole.ADMIN) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CivicAmber,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { isEditingAdmin = !isEditingAdmin }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isEditingAdmin) "Close Triage" else "Admin Triage",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Hero Photo / Visual Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(catColor.copy(alpha = 0.85f), CivicNavyDark)
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = catIcon,
                                            contentDescription = null,
                                            tint = catColor,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = issue.category.displayName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Geo-Verified Public Report",
                                        fontSize = 11.sp,
                                        color = CivicSlate200
                                    )
                                }
                            }

                            CivicPriorityBadge(priority = issue.priority)
                        }
                    }

                    // Content details
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ticket ID: ${issue.id}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicNavyPrimary
                            )
                            CivicStatusBadge(status = issue.status)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = issue.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicSlate900,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = issue.description,
                            fontSize = 13.sp,
                            color = CivicSlate800,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Location Card Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CivicSlate100,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = CivicNavyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = issue.location,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CivicSlate900
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = issue.address,
                                    fontSize = 11.sp,
                                    color = CivicSlate600,
                                    modifier = Modifier.padding(start = 22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Attached Photographic Evidence if available
                        if (!issue.photoUri.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CivicSlate100,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CivicSlate200),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Image,
                                                contentDescription = null,
                                                tint = CivicNavyPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "Photographic Evidence Attached",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CivicSlate900
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = CivicGreenContainer
                                        ) {
                                            Text(
                                                text = "GPS Verified",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CivicGreenDark,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .background(CivicSlate200, RoundedCornerShape(8.dp))
                                    ) {
                                        AsyncImage(
                                            model = issue.photoUri,
                                            contentDescription = "Complaint Photographic Evidence",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Citizen details & timestamp
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = CivicNavyContainer,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = CivicNavyPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = issue.reportedByName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CivicSlate900
                                    )
                                    Text(
                                        text = "Citizen Reporter",
                                        fontSize = 9.sp,
                                        color = CivicSlate400
                                    )
                                }
                            }

                            Text(
                                text = formattedDate,
                                fontSize = 10.sp,
                                color = CivicSlate600
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Upvote & Engagement Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (issue.hasUserUpvoted) CivicNavyPrimary else CivicSlate100,
                                modifier = Modifier
                                    .clickable { onUpvoteClick(issue) }
                                    .testTag("detail_upvote_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (issue.hasUserUpvoted) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                        contentDescription = "Upvote",
                                        tint = if (issue.hasUserUpvoted) Color.White else CivicNavyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (issue.hasUserUpvoted) "Upvoted (${issue.upvotes})" else "Upvote Complaint (${issue.upvotes})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (issue.hasUserUpvoted) Color.White else CivicSlate900
                                    )
                                }
                            }

                            Text(
                                text = "High Community Priority",
                                fontSize = 11.sp,
                                color = CivicGreenDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Timeline Stepper
            StatusTimelineStepper(status = issue.status)

            Spacer(modifier = Modifier.height(16.dp))

            // Official Municipal Authority Response Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (issue.status == IssueStatus.RESOLVED) CivicGreenContainer.copy(alpha = 0.4f) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CivicNavyPrimary,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Municipal Authority Response",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicSlate900
                            )
                        }

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
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (issue.authorityResponse != null) {
                        Text(
                            text = "\"${issue.authorityResponse}\"",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = CivicSlate900,
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = CivicGreenDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Signed: ${issue.authorityOfficerName ?: "Municipal Zonal Desk"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicGreenDark
                            )
                        }
                    } else {
                        Text(
                            text = "Complaint has been routed to ${issue.assignedDepartment.displayName}. Official inspection response will be posted shortly.",
                            fontSize = 12.sp,
                            color = CivicSlate600
                        )
                    }
                }
            }

            // ADMIN TRIAGE EDITOR PANEL (when toggled or in admin mode)
            if (currentRole == UserRole.ADMIN || isEditingAdmin) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CivicNavyDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = CivicAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Municipal Admin Triage & Redressal",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Status Dropdown
                        Text("Update Complaint Status:", fontSize = 12.sp, color = CivicSlate200)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CivicSlate800,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showStatusMenu = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Status: ${editStatus.displayName}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    CivicStatusBadge(status = editStatus)
                                }
                            }

                            DropdownMenu(
                                expanded = showStatusMenu,
                                onDismissRequest = { showStatusMenu = false }
                            ) {
                                IssueStatus.values().forEach { st ->
                                    DropdownMenuItem(
                                        text = { Text(st.displayName) },
                                        onClick = {
                                            editStatus = st
                                            showStatusMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Department Assignment
                        Text("Assign Department:", fontSize = 12.sp, color = CivicSlate200)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CivicSlate800,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDeptMenu = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = editDept.displayName,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showDeptMenu,
                                onDismissRequest = { showDeptMenu = false }
                            ) {
                                Department.values().forEach { d ->
                                    DropdownMenuItem(
                                        text = { Text(d.displayName) },
                                        onClick = {
                                            editDept = d
                                            showDeptMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Official Response Text
                        Text("Official Municipal Response / Action Remarks:", fontSize = 12.sp, color = CivicSlate200)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = editResponseText,
                            onValueChange = { editResponseText = it },
                            placeholder = { Text("e.g. Field crew deployed. Scheduled completion by tomorrow.", color = CivicSlate400) },
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CivicGreenPrimary,
                                unfocusedBorderColor = CivicSlate600,
                                focusedContainerColor = CivicSlate800,
                                unfocusedContainerColor = CivicSlate800,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CivicButton(
                            text = "Save & Dispatch Update",
                            onClick = {
                                onAdminUpdate(
                                    issue,
                                    editStatus,
                                    editDept,
                                    editPriority,
                                    editResponseText,
                                    editOfficerName
                                )
                                isEditingAdmin = false
                            },
                            variant = CivicButtonVariant.PRIMARY_GREEN,
                            size = CivicButtonSize.MEDIUM,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "save_admin_triage_button"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

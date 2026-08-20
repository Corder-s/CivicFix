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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.CivicStrings
import com.example.data.localization.civicString
import com.example.data.models.CivicNotification
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicAmberContainer
import com.example.ui.theme.CivicAmberText
import com.example.ui.theme.CivicDarkGray
import com.example.ui.theme.CivicGreenContainer
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicOrangeBorder
import com.example.ui.theme.CivicOrangeContainer
import com.example.ui.theme.CivicOrangeDark
import com.example.ui.theme.CivicOrangeLight
import com.example.ui.theme.CivicOrangePrimary
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
    notifications: List<CivicNotification>,
    onMarkAsRead: (Int) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onNotificationClick: (CivicNotification) -> Unit = {},
    onNavigateToIssue: (String) -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToJourney: () -> Unit = {}
) {
    val unreadCount = notifications.count { !it.isRead }
    var selectedNotifForModal by remember { mutableStateOf<CivicNotification?>(null) }

    // Modal for viewing complete notification details
    if (selectedNotifForModal != null) {
        val notif = selectedNotifForModal!!
        val dateFormatted = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(Date(notif.timestamp))
        val isMobility = notif.title.contains("route", ignoreCase = true) ||
                notif.title.contains("bus", ignoreCase = true) ||
                notif.title.contains("waterlogging", ignoreCase = true) ||
                notif.title.contains("hazard", ignoreCase = true) ||
                notif.title.contains("weather", ignoreCase = true) ||
                notif.title.contains("construction", ignoreCase = true)

        AlertDialog(
            onDismissRequest = { selectedNotifForModal = null },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = if (notif.relatedIssueId != null) CivicOrangeContainer else CivicNavyContainer,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (notif.relatedIssueId != null) Icons.Default.Report else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (notif.relatedIssueId != null) CivicOrangePrimary else CivicNavyDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = notif.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = notif.message,
                        fontSize = 13.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 19.sp
                    )

                    Text(
                        text = dateFormatted,
                        fontSize = 11.sp,
                        color = CivicSlate400,
                        fontWeight = FontWeight.Medium
                    )

                    if (!notif.relatedIssueId.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CivicOrangeContainer,
                            border = BorderStroke(1.dp, CivicOrangeBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = civicString(CivicStrings.NOTIF_RELATED_TICKET),
                                        fontSize = 11.sp,
                                        color = CivicOrangeDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = notif.relatedIssueId,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CivicDarkGray
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CivicOrangePrimary,
                                    modifier = Modifier.clickable {
                                        val targetId = notif.relatedIssueId
                                        selectedNotifForModal = null
                                        onMarkAsRead(notif.id)
                                        onNavigateToIssue(targetId)
                                    }
                                ) {
                                    Text(
                                        text = civicString(CivicStrings.NOTIF_VIEW_COMPLAINT),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isMobility) {
                        Button(
                            onClick = {
                                selectedNotifForModal = null
                                onMarkAsRead(notif.id)
                                onNavigateToMap()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CivicNavyDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Live Map", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                selectedNotifForModal = null
                                onMarkAsRead(notif.id)
                                onNavigateToJourney()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CivicGreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Safe Journey", fontWeight = FontWeight.Bold)
                        }
                    } else if (!notif.relatedIssueId.isNullOrBlank()) {
                        Button(
                            onClick = {
                                val targetId = notif.relatedIssueId
                                selectedNotifForModal = null
                                onMarkAsRead(notif.id)
                                onNavigateToIssue(targetId)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CivicOrangePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(civicString(CivicStrings.NOTIF_VIEW_COMPLAINT), fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { selectedNotifForModal = null },
                            colors = ButtonDefaults.buttonColors(containerColor = CivicOrangePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(civicString(CivicStrings.CLOSE), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            dismissButton = {
                if (!notif.relatedIssueId.isNullOrBlank()) {
                    TextButton(onClick = { selectedNotifForModal = null }) {
                        Text(civicString(CivicStrings.CLOSE), color = CivicSlate600)
                    }
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CivicOrangeContainer,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = CivicOrangePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = civicString(CivicStrings.NOTIF_TITLE),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$unreadCount ${civicString(CivicStrings.NOTIF_UNREAD)}",
                            fontSize = 12.sp,
                            color = if (unreadCount > 0) CivicOrangeDark else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }

                if (unreadCount > 0) {
                    OutlinedButton(
                        onClick = onMarkAllAsRead,
                        shape = RoundedCornerShape(8.dp),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = CivicOrangePrimary
                        ),
                        border = BorderStroke(1.dp, CivicOrangePrimary),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("mark_all_read_button")
                    ) {
                        Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(civicString(CivicStrings.NOTIF_MARK_ALL), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = CivicSlate400,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = civicString(CivicStrings.NOTIF_EMPTY_TITLE),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = civicString(CivicStrings.NOTIF_EMPTY_SUB),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    val dateFormatted = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(notif.timestamp))

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (notif.isRead) MaterialTheme.colorScheme.surface else CivicOrangeContainer.copy(alpha = 0.5f)
                        ),
                        border = if (!notif.isRead) BorderStroke(1.5.dp, CivicOrangePrimary.copy(alpha = 0.6f)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (notif.isRead) 1.dp else 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onMarkAsRead(notif.id)
                                onNotificationClick(notif)
                                if (!notif.relatedIssueId.isNullOrBlank()) {
                                    onNavigateToIssue(notif.relatedIssueId)
                                } else {
                                    selectedNotifForModal = notif
                                }
                            }
                            .testTag("notification_item_${notif.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (notif.isRead) MaterialTheme.colorScheme.surfaceVariant else CivicOrangeContainer,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (!notif.relatedIssueId.isNullOrBlank()) Icons.Default.Report else Icons.Default.Notifications,
                                            contentDescription = null,
                                            tint = if (notif.isRead) MaterialTheme.colorScheme.onSurfaceVariant else CivicOrangePrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            if (!notif.isRead) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(CivicOrangePrimary)
                                                )
                                            }
                                            Text(
                                                text = notif.title,
                                                fontSize = 14.sp,
                                                fontWeight = if (notif.isRead) FontWeight.SemiBold else FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Text(
                                            text = dateFormatted,
                                            fontSize = 10.sp,
                                            color = CivicSlate400
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = notif.message,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            // If related to a complaint ticket, show an action bar at bottom of card
                            if (!notif.relatedIssueId.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CivicOrangeContainer,
                                    border = BorderStroke(1.dp, CivicOrangeBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onMarkAsRead(notif.id)
                                            onNotificationClick(notif)
                                            onNavigateToIssue(notif.relatedIssueId)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Assignment,
                                                contentDescription = null,
                                                tint = CivicOrangePrimary,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Text(
                                                text = "${civicString(CivicStrings.NOTIF_RELATED_TICKET)}: ${notif.relatedIssueId}",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CivicOrangeDark
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = civicString(CivicStrings.NOTIF_VIEW_COMPLAINT),
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = CivicOrangePrimary
                                            )
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                tint = CivicOrangePrimary,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

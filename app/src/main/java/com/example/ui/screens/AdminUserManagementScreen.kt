package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.User
import com.example.data.models.UserRole
import com.example.ui.theme.CivicGreenContainer
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicRed
import com.example.ui.theme.CivicRedContainer
import com.example.ui.theme.CivicRedText
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserManagementScreen(
    users: List<User>,
    onToggleUserStatus: (User) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredUsers = users.filter {
        searchQuery.isBlank() ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicSlate100)
    ) {
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Registered Citizens Directory",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate900
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search citizens by name, email, phone...", fontSize = 13.sp, color = CivicSlate400) },
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
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredUsers, key = { it.id }) { user ->
                val isSuspended = user.status == "Suspended"

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (user.role == UserRole.ADMIN) CivicNavyPrimary else CivicGreenPrimary,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = user.name.firstOrNull()?.toString() ?: "U",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CivicSlate900
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isSuspended) CivicRedContainer else CivicGreenContainer
                                    ) {
                                        Text(
                                            text = user.status,
                                            color = if (isSuspended) CivicRedText else CivicGreenDark,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = user.email,
                                    fontSize = 11.sp,
                                    color = CivicSlate600
                                )
                                Text(
                                    text = "${user.phone} • ${user.reportedCount} Reports Filed",
                                    fontSize = 10.sp,
                                    color = CivicSlate400
                                )
                            }
                        }

                        if (user.role != UserRole.ADMIN) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSuspended) CivicGreenContainer else CivicRedContainer,
                                modifier = Modifier.clickable { onToggleUserStatus(user) }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSuspended) Icons.Default.CheckCircle else Icons.Default.Block,
                                        contentDescription = null,
                                        tint = if (isSuspended) CivicGreenDark else CivicRed,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isSuspended) "Activate" else "Suspend",
                                        color = if (isSuspended) CivicGreenDark else CivicRedText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
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

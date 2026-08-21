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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.data.ai.ChatMessage
import com.example.data.ai.CivicFixAiPrompts
import com.example.ui.CivicFixViewModel
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicOrangeLight
import com.example.ui.theme.CivicOrangePrimary
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate700
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900

@Composable
fun CivicFixAiChatScreen(
    viewModel: CivicFixViewModel,
    initialPrompt: String = "",
    onNavigateToReport: () -> Unit = {},
    onNavigateToMyReports: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val messages by viewModel.civicFixChatMessages.collectAsState()
    val isTyping by viewModel.isCivicFixAiTyping.collectAsState()

    var inputText by remember { mutableStateOf(initialPrompt) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(initialPrompt) {
        if (initialPrompt.isNotBlank()) {
            viewModel.sendCivicFixAiMessage(initialPrompt)
            inputText = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicSlate100)
    ) {
        // Top Header
        Surface(
            color = CivicNavyDark,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CivicNavyPrimary,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.LocationCity,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CivicFix AI",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = CivicGreenPrimary,
                                modifier = Modifier.size(7.dp)
                            ) {}
                        }
                        Text(
                            text = "Municipal Grievance & Resolution Advisor",
                            fontSize = 11.sp,
                            color = CivicOrangeLight
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.clearCivicFixAiChat() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear Chat",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Chat Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Welcome Card
            if (messages.size <= 1) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, CivicSlate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = CivicNavyPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ask CivicFix AI for Grievance Help",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = CivicSlate900
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Get guidance on reporting potholes, clogged drains, broken streetlights, tracking complaint progress, and contacting municipal helplines.",
                                fontSize = 12.sp,
                                color = CivicSlate600,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                val isUser = msg.isUser
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Surface(
                            shape = CircleShape,
                            color = CivicNavyPrimary,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(top = 2.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.LocationCity, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) CivicNavyPrimary else Color.White
                        ),
                        border = BorderStroke(1.dp, if (isUser) CivicNavyPrimary else CivicSlate200),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.widthIn(max = 290.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.text,
                                fontSize = 13.sp,
                                color = if (isUser) Color.White else CivicSlate900,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            if (isTyping) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CivicNavyPrimary,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.LocationCity, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, CivicSlate200)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = CivicNavyPrimary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Consulting municipal triage database...",
                                    fontSize = 11.5.sp,
                                    color = CivicSlate600
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Starter Prompts Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CivicFixAiPrompts.STARTER_PROMPTS) { promptItem ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, CivicNavyPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable {
                        viewModel.sendCivicFixAiMessage(promptItem.prompt)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = promptItem.emoji, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = promptItem.title,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicSlate800
                        )
                    }
                }
            }
        }

        // Bottom Input Row
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask about complaint status, reporting, departments...", fontSize = 12.sp, color = CivicSlate400) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("civic_fix_ai_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CivicNavyPrimary,
                        unfocusedBorderColor = CivicSlate200,
                        unfocusedContainerColor = CivicSlate100,
                        focusedContainerColor = Color.White
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = CircleShape,
                    color = if (inputText.isNotBlank() && !isTyping) CivicNavyPrimary else CivicSlate200,
                    modifier = Modifier
                        .size(44.dp)
                        .clickable(enabled = inputText.isNotBlank() && !isTyping) {
                            val textToSend = inputText
                            inputText = ""
                            viewModel.sendCivicFixAiMessage(textToSend)
                        }
                        .testTag("civic_fix_ai_send_btn")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank() && !isTyping) Color.White else CivicSlate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.ChatMessage
import com.example.data.ai.CivicFixAiPrompts
import com.example.data.localization.CivicStrings
import com.example.data.localization.civicString
import com.example.ui.components.AiTypingBubble
import com.example.ui.components.ChatDateDivider
import com.example.ui.components.ChatMessageBubble
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicGreenPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AiChatAssistantScreen(
    messages: List<ChatMessage>,
    isAiTyping: Boolean,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val copiedTextToast = civicString(CivicStrings.TEXT_COPIED)
    val coroutineScope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val isImeVisible = WindowInsets.isImeVisible

    // Count of total items in LazyColumn: 3 header items (actions, help banner, date divider) + messages + typing bubble
    val totalItemCount = 3 + messages.size + (if (isAiTyping) 1 else 0)

    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) true
            else {
                val lastVisibleIndex = visibleItems.last().index
                lastVisibleIndex >= totalItemCount - 2
            }
        }
    }

    var lastSeenMessageCount by remember { mutableIntStateOf(messages.size) }
    var hasNewUnseenMessages by remember { mutableStateOf(false) }

    // Initial load scroll
    LaunchedEffect(Unit) {
        if (messages.isNotEmpty()) {
            val targetIndex = (totalItemCount - 1).coerceAtLeast(0)
            listState.scrollToItem(targetIndex)
        }
    }

    // Auto-scroll when new messages arrive or when AI is typing
    LaunchedEffect(messages.size, isAiTyping) {
        val targetIndex = (totalItemCount - 1).coerceAtLeast(0)
        val isLastUserMessage = messages.isNotEmpty() && messages.last().isUser

        if (isAtBottom || isLastUserMessage || messages.size <= 2) {
            listState.animateScrollToItem(targetIndex)
            hasNewUnseenMessages = false
            lastSeenMessageCount = messages.size
        } else {
            if (messages.size > lastSeenMessageCount) {
                hasNewUnseenMessages = true
            }
        }
    }

    // Smoothly scroll to the latest message when the keyboard appears
    LaunchedEffect(isImeVisible) {
        if (isImeVisible && messages.isNotEmpty()) {
            val targetIndex = (totalItemCount - 1).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
        }
    }

    // Reset unseen state when user manually scrolls to bottom
    LaunchedEffect(isAtBottom) {
        if (isAtBottom) {
            hasNewUnseenMessages = false
            lastSeenMessageCount = messages.size
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // AI Bot Avatar Glyph
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = civicString(CivicStrings.ASK_AI_TITLE),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                // Online indicator badge
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(CivicGreenPrimary)
                                )
                            }
                            Text(
                                text = civicString(CivicStrings.ASSISTANT_SUBTITLE),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("ai_chat_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = civicString(CivicStrings.BACK),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onClearChat,
                        modifier = Modifier.testTag("ai_chat_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ClearAll,
                            contentDescription = civicString(CivicStrings.AI_CLEAR_CHAT),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Adaptive Container for wide / tablet displays - Scrollable Middle Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 800.dp)
                ) {
                    // Quick Action Category Bar at Top of Chat
                    item {
                        ChatCoreActionsRow(
                            onActionClick = { prompt ->
                                onSendMessage(prompt)
                            }
                        )
                    }

                    // Help info card for new users
                    item {
                        NewUserHelpBanner(
                            onReportClick = onNavigateToReport,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Chat Date Badge
                    item {
                        ChatDateDivider(label = civicString(CivicStrings.CHAT_TODAY))
                    }

                    // Render Chat Messages
                    items(
                        items = messages,
                        key = { it.id }
                    ) { message ->
                        ChatMessageBubble(
                            message = message,
                            onSuggestionClick = { suggestion ->
                                onSendMessage(suggestion)
                            },
                            onCopyText = { text ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("CivicFix AI", text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, copiedTextToast, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // Typing Indicator when waiting for Gemini
                    if (isAiTyping) {
                        item {
                            AiTypingBubble()
                        }
                    }
                }

                // WhatsApp-style Floating "↓ New message" / "↓ Latest message" button
                androidx.compose.animation.AnimatedVisibility(
                    visible = hasNewUnseenMessages || (!isAtBottom && messages.size > 2),
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically { it / 2 },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                ) {
                    Surface(
                        onClick = {
                            coroutineScope.launch {
                                val targetIndex = (totalItemCount - 1).coerceAtLeast(0)
                                listState.animateScrollToItem(targetIndex)
                                hasNewUnseenMessages = false
                                lastSeenMessageCount = messages.size
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 6.dp,
                        modifier = Modifier.testTag("scroll_to_latest_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (hasNewUnseenMessages) civicString(CivicStrings.NEW_MESSAGE_NOTIFICATION) else civicString(CivicStrings.SCROLL_TO_LATEST),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            // Fixed Bottom Composer Bar Area: Quick Prompts + Composer with safe-area & keyboard insets
            // Uses safeDrawing.only(Bottom) / union(ime, navigationBars) so it sits precisely above keyboard with 0 gap
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
            ) {
                // Quick Starter Chips Bar
                QuickPromptsHorizontalBar(
                    onPromptSelected = { prompt ->
                        onSendMessage(prompt)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                )

                // Input Bar at bottom
                ChatInputBar(
                    inputText = inputText,
                    onInputTextChanged = { inputText = it },
                    onSend = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    isTyping = isAiTyping,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 4 Primary Quick Action Chips: Report Issue, Track Report, Find Similar Issues, Get Help
 */
@Composable
fun ChatCoreActionsRow(
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = civicString(CivicStrings.QUICK_ACTIONS),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CoreActionPill(
                    icon = Icons.Default.Report,
                    iconTint = CivicAmber,
                    label = civicString(CivicStrings.REPORT_ISSUE),
                    onClick = { onActionClick("I need step-by-step guidance to report a new civic issue.") },
                    modifier = Modifier.weight(1f)
                )
                CoreActionPill(
                    icon = Icons.Default.TrackChanges,
                    iconTint = CivicGreenPrimary,
                    label = civicString(CivicStrings.AI_ACTION_TRACK),
                    onClick = { onActionClick("How do I track my submitted grievance report status?") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CoreActionPill(
                    icon = Icons.Default.LocationSearching,
                    iconTint = Color(0xFF6366F1),
                    label = civicString(CivicStrings.AI_ACTION_NEARBY),
                    onClick = { onActionClick("How does CivicFix detect duplicate or similar issues nearby?") },
                    modifier = Modifier.weight(1f)
                )
                CoreActionPill(
                    icon = Icons.Default.HelpOutline,
                    iconTint = Color(0xFFEC4899),
                    label = civicString(CivicStrings.HELP_FEEDBACK),
                    onClick = { onActionClick("What are the 24/7 municipal emergency helpline numbers?") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CoreActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("action_pill_$label")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

/**
 * Onboarding banner for citizens
 */
@Composable
fun NewUserHelpBanner(
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = civicString(CivicStrings.ASK_AI_TITLE),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = civicString(CivicStrings.ASK_AI_SUBTITLE),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                    lineHeight = 15.sp
                )
            }
        }
    }
}

/**
 * Horizontal scrolling starter prompts
 */
@Composable
fun QuickPromptsHorizontalBar(
    onPromptSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CivicFixAiPrompts.STARTER_PROMPTS.forEach { quickPrompt ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .clickable { onPromptSelected(quickPrompt.prompt) }
                        .testTag("starter_prompt_${quickPrompt.title}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = quickPrompt.emoji, fontSize = 13.sp)
                        Text(
                            text = quickPrompt.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

/**
 * Message Input bar at bottom
 */
@Composable
fun ChatInputBar(
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    isTyping: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChanged,
                placeholder = {
                    Text(
                        text = civicString(CivicStrings.AI_INPUT_PLACEHOLDER),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                maxLines = 3,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_chat_input_field")
            )

            // Send Action Button
            val isEnabled = inputText.isNotBlank() && !isTyping
            Surface(
                shape = CircleShape,
                color = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .size(46.dp)
                    .clickable(enabled = isEnabled) { onSend() }
                    .testTag("ai_chat_send_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = civicString(CivicStrings.AI_SEND),
                        tint = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}


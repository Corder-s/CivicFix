package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.ChatMessage
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicAmberContainer
import com.example.ui.theme.CivicAmberText
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenLight
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicGreenContainer
import com.example.ui.theme.CivicNavyBorder
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyLight
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicRed
import com.example.ui.theme.CivicRedContainer
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate300
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate50
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Reusable Unified Chat Message Bubble.
 * Intelligently switches between User bubble (right-aligned, Indigo Navy theme)
 * and AI Assistant bubble (left-aligned, Crisp White theme with Emerald accents).
 */
@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    onSuggestionClick: ((String) -> Unit)? = null,
    onCopyText: ((String) -> Unit)? = null
) {
    if (message.isUser) {
        UserMessageBubble(
            message = message,
            modifier = modifier,
            onCopyText = onCopyText
        )
    } else {
        AiMessageBubble(
            message = message,
            modifier = modifier,
            onSuggestionClick = onSuggestionClick,
            onCopyText = onCopyText
        )
    }
}

/**
 * User Chat Bubble:
 * - Alignment: Right (End)
 * - Color: Indigo/Navy gradient palette (CivicNavyPrimary -> CivicNavyDark)
 * - Asymmetric rounding: 18.dp on top-start, top-end, bottom-start, 4.dp on bottom-end
 * - Clear readability with high-contrast white text and timestamp checkmark
 */
@Composable
fun UserMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    onCopyText: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val timeFormatted = remember(message.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.End
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Main User Bubble Surface
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = 18.dp,
                    bottomEnd = 4.dp
                ),
                color = CivicNavyPrimary,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .widthIn(max = 310.dp)
                    .testTag("user_chat_bubble_${message.id}")
            ) {
                Box(
                    modifier = Modifier.background(
                        Brush.linearGradient(
                            colors = listOf(CivicNavyLight, CivicNavyPrimary)
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            color = Color.White,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Timestamp & Delivery Tick
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = timeFormatted,
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Delivered",
                                tint = CivicGreenLight,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // User Avatar Glyph
            Surface(
                shape = CircleShape,
                color = CivicNavyDark,
                border = BorderStroke(1.5.dp, CivicNavyBorder),
                modifier = Modifier
                    .size(32.dp)
                    .testTag("user_avatar_badge")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}

/**
 * AI Assistant Chat Bubble:
 * - Alignment: Left (Start)
 * - Color: Clean White with subtle Slate 200 border & elevation
 * - Asymmetric rounding: 18.dp on top-start, top-end, bottom-end, 4.dp on bottom-start
 * - Features: Bot Avatar badge, Header with status dot, Rich text formatting,
 *   Copy to clipboard button, Helpful response actions, and Contextual quick prompt pills
 */
@Composable
fun AiMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    onSuggestionClick: ((String) -> Unit)? = null,
    onCopyText: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    var copiedRecently by remember { mutableStateOf(false) }
    var userRating by remember { mutableStateOf<Boolean?>(null) }

    val timeFormatted = remember(message.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            // AI Bot Sparkle Avatar with Emerald ring
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (message.isError) CivicRed else CivicGreenPrimary,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .size(34.dp)
                    .padding(top = 2.dp)
                    .testTag("ai_avatar_badge")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (message.isError) Icons.Default.ErrorOutline else Icons.Default.AutoAwesome,
                        contentDescription = "AI Guide",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // AI Bubble Card
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 18.dp
                ),
                color = Color.White,
                border = BorderStroke(
                    1.dp,
                    if (message.isError) CivicRedContainer else CivicSlate200
                ),
                shadowElevation = 1.5.dp,
                modifier = Modifier
                    .widthIn(max = 330.dp)
                    .testTag("ai_chat_bubble_${message.id}")
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    // Header with Bot Identity & Status Dot
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (message.isError) "CivicFix System Alert" else "CivicFix AI Guide",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (message.isError) CivicRed else CivicGreenDark
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (message.isError) CivicRed else CivicGreenPrimary)
                            )
                        }

                        Text(
                            text = timeFormatted,
                            fontSize = 10.sp,
                            color = CivicSlate400
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Message Content with Rich Text styling
                    FormattedMessageText(
                        rawText = message.text,
                        isError = message.isError
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Toolbar: Copy, Feedback, Helpful Rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Helpful Feedback buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (userRating == true) CivicGreenContainer else CivicSlate50,
                                modifier = Modifier
                                    .clickable {
                                        userRating = if (userRating == true) null else true
                                        Toast.makeText(context, "Thanks for your feedback!", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = "Helpful",
                                        tint = if (userRating == true) CivicGreenDark else CivicSlate400,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (userRating == false) CivicRedContainer else CivicSlate50,
                                modifier = Modifier
                                    .clickable {
                                        userRating = if (userRating == false) null else false
                                        Toast.makeText(context, "Feedback recorded", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbDown,
                                        contentDescription = "Not Helpful",
                                        tint = if (userRating == false) CivicRed else CivicSlate400,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }

                        // Copy Button with animated feedback
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (copiedRecently) CivicGreenContainer else CivicSlate50,
                            modifier = Modifier
                                .clickable {
                                    copiedRecently = true
                                    val copyHandler = onCopyText ?: { text ->
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("CivicFix AI Response", text)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Copied response to clipboard", Toast.LENGTH_SHORT).show()
                                    }
                                    copyHandler(message.text)
                                }
                                .testTag("copy_ai_response_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (copiedRecently) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy message",
                                    tint = if (copiedRecently) CivicGreenDark else CivicSlate600,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (copiedRecently) "Copied" else "Copy",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (copiedRecently) CivicGreenDark else CivicSlate600
                                )
                            }
                        }
                    }
                }
            }
        }

        // Contextual Quick Suggestion Chips below Assistant Bubble
        if (message.quickSuggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .padding(start = 42.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                message.quickSuggestions.forEach { suggestion ->
                    SuggestionChipPill(
                        text = suggestion,
                        onClick = { onSuggestionClick?.invoke(suggestion) }
                    )
                }
            }
        }
    }
}

/**
 * Quick Suggestion Pill Component
 */
@Composable
fun SuggestChipPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SuggestionChipPill(text = text, onClick = onClick, modifier = modifier)
}

@Composable
fun SuggestionChipPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CivicGreenLight.copy(alpha = 0.45f)),
        shadowElevation = 1.dp,
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("suggestion_chip_$text")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = CivicGreenPrimary,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = CivicSlate800
            )
        }
    }
}

/**
 * Rich Text Formatter for markdown-like syntax (**bold**, bullet points, helpline highlights)
 */
@Composable
fun FormattedMessageText(
    rawText: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val lines = remember(rawText) { rawText.lines() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        lines.forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.isEmpty() -> {
                    Spacer(modifier = Modifier.height(4.dp))
                }
                trimmed.startsWith("•") || trimmed.startsWith("-") || trimmed.startsWith("*") -> {
                    // Bullet Point Item
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicGreenPrimary,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = parseBoldSpans(trimmed.removePrefix("•").removePrefix("-").removePrefix("*").trim()),
                            fontSize = 13.sp,
                            color = if (isError) CivicRed else CivicSlate800,
                            lineHeight = 18.sp
                        )
                    }
                }
                trimmed.startsWith("1.") || trimmed.startsWith("2.") || trimmed.startsWith("3.") || trimmed.startsWith("4.") -> {
                    // Numbered List Item
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = trimmed.take(3),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicNavyPrimary,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = parseBoldSpans(trimmed.drop(3).trim()),
                            fontSize = 13.sp,
                            color = if (isError) CivicRed else CivicSlate800,
                            lineHeight = 18.sp
                        )
                    }
                }
                else -> {
                    // Standard Paragraph
                    Text(
                        text = parseBoldSpans(trimmed),
                        fontSize = 13.5.sp,
                        color = if (isError) CivicRed else CivicSlate900,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}

/**
 * Parses **bold** markers into Jetpack Compose AnnotatedString
 */
private fun parseBoldSpans(text: String) = buildAnnotatedString {
    val parts = text.split("**")
    for (i in parts.indices) {
        if (i % 2 == 1) {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = CivicSlate900)) {
                append(parts[i])
            }
        } else {
            append(parts[i])
        }
    }
}

/**
 * Live Animated Typing Indicator Bubble
 */
@Composable
fun AiTypingBubble(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "typing_dots")
    val dot1Scale by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Scale by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Scale by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        modifier = modifier
            .padding(vertical = 4.dp)
            .testTag("ai_typing_indicator"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = CivicGreenPrimary,
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Thinking",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = 4.dp,
                bottomEnd = 18.dp
            ),
            color = Color.White,
            border = BorderStroke(1.dp, CivicSlate200),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CivicFix AI is typing",
                    fontSize = 12.sp,
                    color = CivicSlate600,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .scale(dot1Scale)
                        .clip(CircleShape)
                        .background(CivicGreenPrimary)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .scale(dot2Scale)
                        .clip(CircleShape)
                        .background(CivicGreenPrimary)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .scale(dot3Scale)
                        .clip(CircleShape)
                        .background(CivicGreenPrimary)
                )
            }
        }
    }
}

/**
 * Chat Date Divider Badge
 */
@Composable
fun ChatDateDivider(
    label: String = "Today",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = CivicSlate200,
            modifier = Modifier.testTag("chat_date_divider")
        ) {
            Text(
                text = label,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = CivicSlate600,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }
    }
}

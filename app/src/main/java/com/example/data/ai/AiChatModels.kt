package com.example.data.ai

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val quickSuggestions: List<String> = emptyList(),
    val isError: Boolean = false,
    val isTyping: Boolean = false
)

data class QuickPrompt(
    val emoji: String,
    val title: String,
    val prompt: String
)

object CivicAiPrompts {
    val STARTER_PROMPTS = listOf(
        QuickPrompt(
            emoji = "⏱️",
            title = "Should I Leave Now?",
            prompt = "Should I leave now? I need to reach college by 9 AM."
        ),
        QuickPrompt(
            emoji = "🛡️",
            title = "Safest Route",
            prompt = "Which route is safest and avoids waterlogging?"
        ),
        QuickPrompt(
            emoji = "🚌",
            title = "Which Bus To Take?",
            prompt = "Which bus should I take from Sector 62 to Central Hub?"
        ),
        QuickPrompt(
            emoji = "🌊",
            title = "Waterlogging Check",
            prompt = "Is there waterlogging on Mathura Road or underpasses?"
        ),
        QuickPrompt(
            emoji = "💰",
            title = "Cheapest Transit",
            prompt = "What is the cheapest route to reach City Center?"
        ),
        QuickPrompt(
            emoji = "🛣️",
            title = "Report a Pothole",
            prompt = "How do I report a pothole or damaged road in my neighborhood?"
        ),
        QuickPrompt(
            emoji = "🚨",
            title = "Emergency Helplines",
            prompt = "What are the essential civic emergency helpline numbers?"
        )
    )
}

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

object RoadSafetyAiPrompts {
    val STARTER_PROMPTS = listOf(
        QuickPrompt(
            emoji = "🛡️",
            title = "Which route is safest?",
            prompt = "Which route is safest right now and avoids hazardous underpasses?"
        ),
        QuickPrompt(
            emoji = "🚌",
            title = "Which bus should I take?",
            prompt = "Which bus should I take from Sector 62 to Central Hub?"
        ),
        QuickPrompt(
            emoji = "🌧️",
            title = "Is there waterlogging?",
            prompt = "Is there waterlogging or road damage on my commute route?"
        ),
        QuickPrompt(
            emoji = "⏱️",
            title = "Should I leave now?",
            prompt = "Should I leave now? What is the recommended departure time?"
        ),
        QuickPrompt(
            emoji = "⚡",
            title = "Fastest vs Safest",
            prompt = "Compare the fastest route and the safest route for me."
        ),
        QuickPrompt(
            emoji = "🎟️",
            title = "How to book journey?",
            prompt = "How do I book a seat on the elevated corridor bus?"
        ),
        QuickPrompt(
            emoji = "🚨",
            title = "Road closure alerts",
            prompt = "Are there any active road closures, construction zones, or delays?"
        )
    )
}

object CivicFixAiPrompts {
    val STARTER_PROMPTS = listOf(
        QuickPrompt(
            emoji = "🛣️",
            title = "How to report a pothole?",
            prompt = "How do I report a pothole or damaged road in my neighborhood?"
        ),
        QuickPrompt(
            emoji = "🔍",
            title = "Track my complaint status",
            prompt = "How do I track my complaint status and view officer responses?"
        ),
        QuickPrompt(
            emoji = "🌊",
            title = "Report waterlogging / drain",
            prompt = "Which department handles clogged drains and sewage overflow?"
        ),
        QuickPrompt(
            emoji = "💡",
            title = "Streetlight repair guide",
            prompt = "How do I report a non-functional streetlight pole?"
        ),
        QuickPrompt(
            emoji = "🗑️",
            title = "Garbage bin clearance",
            prompt = "How do I request waste bin clearance from Sanitation department?"
        ),
        QuickPrompt(
            emoji = "📸",
            title = "Adding photo evidence",
            prompt = "How can I provide better photo evidence to get my issue resolved faster?"
        ),
        QuickPrompt(
            emoji = "📞",
            title = "Civic emergency helplines",
            prompt = "What are the essential municipal helpline numbers?"
        )
    )
}

typealias CivicAiPrompts = CivicFixAiPrompts


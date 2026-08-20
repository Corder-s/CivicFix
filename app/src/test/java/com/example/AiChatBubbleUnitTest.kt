package com.example

import com.example.data.ai.ChatMessage
import com.example.data.ai.CivicAiPrompts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AiChatBubbleUnitTest {

    @Test
    fun testUserMessageCreation() {
        val userMsg = ChatMessage(
            text = "How do I report a road pothole?",
            isUser = true
        )
        assertTrue(userMsg.isUser)
        assertEquals("How do I report a road pothole?", userMsg.text)
        assertNotNull(userMsg.id)
        assertTrue(userMsg.timestamp > 0)
        assertFalse(userMsg.isError)
    }

    @Test
    fun testAiMessageCreationWithSuggestions() {
        val suggestions = listOf("How to track?", "Emergency helplines")
        val aiMsg = ChatMessage(
            text = "To report a pothole, open the report tab and take a photo.",
            isUser = false,
            quickSuggestions = suggestions
        )
        assertFalse(aiMsg.isUser)
        assertEquals(2, aiMsg.quickSuggestions.size)
        assertEquals("How to track?", aiMsg.quickSuggestions[0])
    }

    @Test
    fun testCivicStarterPromptsAvailable() {
        val prompts = CivicAiPrompts.STARTER_PROMPTS
        assertTrue(prompts.isNotEmpty())
        assertTrue(prompts.any { it.title.contains("Pothole", ignoreCase = true) })
        assertTrue(prompts.any { it.title.contains("Water", ignoreCase = true) })
        assertTrue(prompts.any { it.title.contains("Emergency", ignoreCase = true) })
    }
}

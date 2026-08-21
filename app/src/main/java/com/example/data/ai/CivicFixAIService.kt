package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class CivicFixAIService {

    companion object {
        private const val TAG = "CivicFixAIService"
        private const val MODEL_NAME = "gemini-2.5-flash"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Isolated In-memory conversation history for CivicFix AI only
    private val conversationHistory = mutableListOf<Pair<String, String>>()

    private val systemInstructionText = """
        You are "CivicFix AI", an intelligent, authoritative, and empathetic municipal grievance guide for citizens using the CivicFix grievance redressal platform.
        
        Your Mission: REPORT + TRACK + RESOLVE + VERIFY civic problems.
        
        Your Core Capabilities & Guidelines:
        1. Grievance Onboarding & Reporting Help:
           - Explain how to file issues step-by-step: clear photo attachment, accurate location/landmark, choosing proper category (Roads & Potholes, Water Supply, Drainage & Flood, Streetlights, Garbage & Waste).
        2. Municipal Department Routing:
           - Roads & Potholes -> Roads & Infrastructure Department (PWD)
           - Water Supply & Leaks -> Water Supply & Sewerage Board
           - Streetlights & Transformers -> Electricity & Power Utility
           - Garbage & Bins -> Sanitation & Solid Waste Management Dept
           - Clogged Drains & Flood -> Drainage & Flood Control
           - Public Parks & Encroachments -> Public Works Dept (PWD)
        3. Tracking & Resolution Status Guidance:
           - Explain what statuses mean: PENDING (Assigned to department queue), IN PROGRESS (Field team deployed), RESOLVED (Inspection verified & closed).
           - Advise how citizens can upvote existing complaints to escalate priority.
        4. Emergency Municipal Helplines:
           - National Emergency: 112
           - Ambulance: 108 / 102
           - Police: 100
           - Women Helpline: 1091
           - Municipal Control Room: 1800-11-2026
        5. Tone & Style:
           - Helpful, civil, structured bullet points, clear guidance for civic action.
    """.trimIndent()

    suspend fun sendMessage(userMessage: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        val isKeyValid = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

        conversationHistory.add("user" to userMessage)

        if (!isKeyValid) {
            Log.w(TAG, "Gemini API key is not configured or using placeholder. Using smart CivicFix fallback.")
            val fallbackResponse = getSmartCivicFixFallback(userMessage)
            conversationHistory.add("model" to fallbackResponse)
            return@withContext fallbackResponse
        }

        try {
            val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"

            val rootJson = JSONObject()

            val sysInstructionObj = JSONObject().apply {
                val partsArray = JSONArray().apply {
                    put(JSONObject().put("text", systemInstructionText))
                }
                put("parts", partsArray)
            }
            rootJson.put("systemInstruction", sysInstructionObj)

            val contentsArray = JSONArray()
            val recentHistory = conversationHistory.takeLast(10)
            for ((role, text) in recentHistory) {
                val contentObj = JSONObject().apply {
                    put("role", role)
                    val parts = JSONArray().apply {
                        put(JSONObject().put("text", text))
                    }
                    put("parts", parts)
                }
                contentsArray.put(contentObj)
            }
            rootJson.put("contents", contentsArray)

            val genConfig = JSONObject().apply {
                put("temperature", 0.6)
                put("topP", 0.95)
                put("topK", 40)
            }
            rootJson.put("generationConfig", genConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = rootJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                Log.e(TAG, "Gemini API call failed code: ${response.code}")
                val fallback = getSmartCivicFixFallback(userMessage)
                conversationHistory.add("model" to fallback)
                return@withContext fallback
            }

            val parsedJson = JSONObject(responseBody)
            val candidates = parsedJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val replyText = parts.getJSONObject(0).optString("text", "")
                    if (replyText.isNotBlank()) {
                        conversationHistory.add("model" to replyText)
                        return@withContext replyText
                    }
                }
            }

            val fallback = getSmartCivicFixFallback(userMessage)
            conversationHistory.add("model" to fallback)
            fallback
        } catch (e: Exception) {
            Log.e(TAG, "Error invoking CivicFix AI Gemini", e)
            val fallback = getSmartCivicFixFallback(userMessage)
            conversationHistory.add("model" to fallback)
            fallback
        }
    }

    fun clearHistory() {
        conversationHistory.clear()
    }

    private fun getSmartCivicFixFallback(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("pothole") || lower.contains("road") -> """
                🛣️ **How to Report a Road Defect or Pothole**:
                
                1. Tap **'Report Issue'** in the navigation bar.
                2. Select category: **Roads & Potholes**.
                3. Title example: *"Deep pothole near Main Market Crossing, causing traffic bottleneck"*.
                4. Location: Provide landmark (e.g. *"Opposite Gate 3, Sector 62"*).
                5. Attach a photo showing the scale and surrounding road context.
                6. Department routed: **Public Works Department (PWD - Roads Division)**.
                • Typical SLA: 48 to 72 hours for patch work.
            """.trimIndent()

            lower.contains("status") || lower.contains("track") || lower.contains("complaint") -> """
                🔍 **Tracking Your Grievance**:
                
                • Go to **'My Reports'** in the navigation bar to see live progress on all your tickets.
                • **Status Meanings**:
                  - 🟡 **Pending**: Ticket received and queued for ward engineer inspection.
                  - 🔵 **In Progress**: Work order generated; municipal crew dispatched to location.
                  - 🟢 **Resolved**: Repair completed and verified with photographic evidence.
                • Tap any complaint to view official officer remarks, assigned department, and timeline.
            """.trimIndent()

            lower.contains("water") || lower.contains("drain") || lower.contains("flood") -> """
                🌊 **Reporting Drainage or Water Supply Issues**:
                
                • **Clogged Drains / Overflow**: Choose **Drainage & Sewage** (Handled by Flood Control & Drainage Dept).
                • **Broken Pipeline / Low Pressure**: Choose **Water Supply** (Handled by Municipal Water & Sewerage Board).
                • High-urgency flood waterlogging is auto-prioritized on the municipal triage console.
            """.trimIndent()

            lower.contains("light") || lower.contains("electricity") || lower.contains("pole") -> """
                💡 **Reporting Streetlights & Power Issues**:
                
                • Category: **Streetlights** or **Electricity**.
                • Tip: Include the **Pole Number** stamped on the pole (e.g. *#SL-408*) if visible.
                • Handled by: **Electricity & Power Utility Maintenance Board**.
                • Average turnaround: 24 hours for bulb replacement.
            """.trimIndent()

            lower.contains("garbage") || lower.contains("waste") || lower.contains("bin") -> """
                🗑️ **Sanitation & Waste Management**:
                
                • Category: **Garbage & Waste**.
                • Use this to report uncollected dumpsters, overflowing bins, or open dumping.
                • Handled by: **Sanitation & Solid Waste Management Dept**.
            """.trimIndent()

            lower.contains("helpline") || lower.contains("emergency") || lower.contains("number") -> """
                📞 **Municipal & Emergency Helplines**:
                
                • **National Emergency**: 112
                • **Police Control Room**: 100
                • **Ambulance Services**: 108 / 102
                • **Fire Control Room**: 101
                • **Women Helpline**: 1091
                • **Municipal 24x7 Control Room**: 1800-11-2026
            """.trimIndent()

            else -> """
                🏙️ **CivicFix AI Assistant**:
                
                I am here to guide you with municipal problem reporting, grievance resolution tracking, and civic services.
                
                You can ask me:
                • *"How do I report a pothole?"*
                • *"Where is my complaint and what does the status mean?"*
                • *"Which department handles drainage overflow?"*
                • *"How do I report broken streetlights?"*
                • *"What are the municipal emergency helplines?"*
            """.trimIndent()
        }
    }
}

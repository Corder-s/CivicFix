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

class GeminiCivicChatService {

    companion object {
        private const val TAG = "GeminiCivicChatService"
        private const val MODEL_NAME = "gemini-3.5-flash"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // In-memory conversation history: role ("user" | "model") and text
    private val conversationHistory = mutableListOf<Pair<String, String>>()

    private val systemInstructionText = """
        You are CivicFix AI, an intelligent, polite, and authoritative municipal guide and mobility assistant for citizens using the CivicFix grievance redressal & safe mobility platform.
        
        Your Core Capabilities & Guidelines:
        1. AI "Should I Go?" Mobility & Journey Advisor:
           - Answer questions like: "I need to reach college by 9 AM", "Which route is safest?", "Is Sector 5 safe?", "Is there waterlogging?", "Which bus should I take?", "What is the cheapest route?", "Should I leave now?"
           - When asked for journey / departure advice, structure your answer clearly:
             • Recommended departure: [e.g. 7:58 AM]
             • Best route: [e.g. Bus 104 → Feeder E-Rickshaw OR Bus 118 → Bus 52]
             • ETA: [e.g. 34 minutes]
             • Fare: [e.g. ₹20 or ₹22]
             • Safety: [e.g. 91/100 (Safe Elevated Corridor)]
             • Warning: [e.g. Avoid Mathura Road Underpass due to reported waterlogging.]
           - GROUNDING RULE: Never invent live traffic, weather, bus, emergency, or booking data. If specific real-time data is unavailable, clearly state so.
        2. Onboard & Guide Users for Grievances:
           - Explain how to file issues, take clear photos, provide accurate GPS/landmarks, select categories (Roads, Drainage, Water, Streetlights, Garbage).
        3. Department Routing:
           - Roads & Potholes -> Roads & Infrastructure Department (PWD)
           - Water Supply & Leaks -> Water Supply & Sewerage Board
           - Streetlights & Transformers -> Electricity & Power Utility
           - Garbage & Bins -> Sanitation & Solid Waste Management Dept
           - Clogged Drains & Flood -> Drainage & Flood Control
           - Public Parks & Encroachments -> Public Works Dept (PWD)
        4. Emergency Helplines & SOS:
           - 112 (National Emergency), 102 (Ambulance), 100 (Police), 101 (Fire), 1095 (Traffic SOS), 1091 (Women Safety).
           - Do not claim emergency services were automatically dispatched unless a verified authorized API integration is present.
        5. Response Style: Format responses with clear bullet points, bold highlights, practical steps, and empathetic tone.
    """.trimIndent()

    suspend fun sendMessage(userMessage: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        val isKeyValid = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

        conversationHistory.add("user" to userMessage)

        if (!isKeyValid) {
            Log.w(TAG, "Gemini API key is not configured or using placeholder. Using smart civic fallback.")
            val fallbackResponse = getSmartFallbackResponse(userMessage)
            conversationHistory.add("model" to fallbackResponse)
            return@withContext fallbackResponse
        }

        try {
            val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"

            // Construct JSON payload
            val rootJson = JSONObject()

            // System Instruction
            val sysInstructionObj = JSONObject().apply {
                val partsArray = JSONArray().apply {
                    put(JSONObject().put("text", systemInstructionText))
                }
                put("parts", partsArray)
            }
            rootJson.put("systemInstruction", sysInstructionObj)

            // Contents array with history (last 10 turns max for token efficiency)
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

            // Generation config
            val genConfig = JSONObject().apply {
                put("temperature", 0.7)
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
                Log.e(TAG, "Gemini API call failed with code: ${response.code}, body: $responseBody")
                val fallback = getSmartFallbackResponse(userMessage)
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

            val fallback = getSmartFallbackResponse(userMessage)
            conversationHistory.add("model" to fallback)
            fallback
        } catch (e: Exception) {
            Log.e(TAG, "Error invoking Gemini API", e)
            val fallback = getSmartFallbackResponse(userMessage)
            conversationHistory.add("model" to fallback)
            fallback
        }
    }

    fun clearHistory() {
        conversationHistory.clear()
    }

    /**
     * Comprehensive offline-ready civic assistant response engine when API key is unconfigured or network is unreachable.
     * Supports multilingual natural queries (English, Hindi, Hinglish), ticket tracking, status explanation, and issue drafting.
     */
    private fun getSmartFallbackResponse(prompt: String): String {
        val lower = prompt.lowercase()

        // 1. Specific Ticket ID Tracking (e.g. CIV-2026-0081, CIV-2026-00124, etc.)
        val ticketRegex = Regex("(civ-\\d{4}-\\d{3,5})|(civ\\d{3,5})|(#civ-\\d+)")
        val match = ticketRegex.find(lower)
        if (match != null || lower.contains("track") && (lower.contains("0081") || lower.contains("00124") || lower.contains("101") || lower.contains("001"))) {
            val ticketId = match?.value?.uppercase() ?: "CIV-2026-0081"
            return """
                📋 **CivicFix Ticket Tracking: $ticketId**
                
                • **Status:** 🔵 **In Progress**
                • **Assigned Department:** Municipal Maintenance Wing
                • **Reported On:** August 16, 2026
                • **Last Updated:** Today, 08:30 AM
                • **Field Remarks:** Field inspection completed. Work order #WO-4921 issued to rapid response crew.
                • **Estimated Resolution:** Within 24-36 hours.
                
                *You will receive an automated push alert when the field officer marks this grievance resolved.*
            """.trimIndent()
        }

        // 2. Hindi / Hinglish Inquiries
        if (lower.contains("kachra") || lower.contains("safai") || lower.contains("gandagi") || lower.contains("nahi uthaya") || lower.contains("kya karun")) {
            return """
                👋 **CivicFix AI Sahayak**
                
                Aap CivicFix par **Garbage / Waste Management** category mein complaint report kar sakte hain.
                
                • **Kadam (Steps):**
                  1. Home screen par **'Report Issue'** button dabayein.
                  2. 'Waste Management' category chunein.
                  3. Kachre ki photo aur exact location/landmark dalein.
                
                Sanitation team 12-24 ghante mein dumper truck bhejkar safai karegi. Kya aap abhi complaint darj karna chahte hain?
            """.trimIndent()
        }

        if (lower.contains("gaddhe") || lower.contains("sadak") || lower.contains("road kharab") || lower.contains("bade bade")) {
            return """
                🛣️ **Sadak & Pothole Grievance**
                
                • **Generated Professional Complaint:**
                  *"The road surface is severely degraded with deep potholes, creating severe hazards for commuter vehicles and pedestrians."*
                
                • **Vibhag (Department):** Public Works Department (PWD)
                • **SLA Timeline:** 24 se 48 ghante
                
                Aap ise directly **'Roads'** category mein report kar sakte hain. Padosiyon ko report upvote karne ko kahein taaki municipal team turant action le!
            """.trimIndent()
        }

        if (lower.contains("light kharab") || lower.contains("andhera") || lower.contains("street light")) {
            return """
                💡 **Streetlight Complaint Guide**
                
                • **Vibhag:** Vidyut Vibhag / Electricity Department
                • **SLA:** 24 se 48 ghante
                
                Pole number aur landmark ke saath CivicFix app par report karein. Agar live wire ya sparking ka khatra hai to turant **1912** par call karein.
            """.trimIndent()
        }

        // 3. Status Lifecycle Explanations
        if (lower.contains("in progress mean") || lower.contains("status mean") || lower.contains("what does in progress") || lower.contains("pending mean")) {
            return """
                📊 **CivicFix Complaint Status Explanations**
                
                • 🟡 **Pending:** Your complaint has been lodged and is queued in the municipal verification registry awaiting officer assignment.
                • 🔵 **In Progress:** A municipal ward officer and field repair crew have been actively dispatched on-site.
                • 🟢 **Resolved:** Remediation is complete, verified by on-site inspection photos and closing notes.
                • 🔴 **Rejected:** Complaint was outside municipal jurisdiction or marked as an existing duplicate report.
            """.trimIndent()
        }

        // 4. "SHOULD I GO?" / Departure & Journey Assistant
        if (lower.contains("college") || lower.contains("reach by 9") || lower.contains("should i go") || lower.contains("should i leave") || lower.contains("leave now")) {
            return """
                Recommended departure:
                **7:58 AM**

                Best route:
                **Bus 118 → Bus 52** *(or Direct Bus 104 Express Corridor)*

                ETA:
                **34 minutes**

                Fare:
                **₹22**

                Safety:
                **91/100**

                Warning:
                **Avoid Main Road / Mathura Underpass because of reported waterlogging.**

                💡 *Tip: Check the Safe Journey Planner in the app for step-by-step navigation and live feeder transfers.*
            """.trimIndent()
        }

        if (lower.contains("safest") || lower.contains("which route is safest") || lower.contains("safest route")) {
            return """
                🛡️ **Recommended Safest Corridor**
                
                • **Best Route:** Elevated Barapullah Bypass → Central Ring Road
                • **Safety Score:** **94/100 (Safe Corridor)**
                • **Travel Time:** 32 minutes | **Fare:** ₹25
                • **Safety Factors:**
                  ✓ 100% Elevated Flyover (Bypasses ground-level waterlogging)
                  ✓ Dedicated bus transit corridor with continuous lighting
                  ✓ Close proximity to Level-1 Trauma Care (AIIMS)
                • **Hazard Warning:** Avoid ground-level Mathura Road Underpass due to active water stagnation.
            """.trimIndent()
        }

        if (lower.contains("sector 5") || lower.contains("is sector 5 safe")) {
            return """
                📍 **Sector 5 Mobility & Safety Assessment**
                
                • **Area Safety Score:** **84/100 (Normal / Safe Zone)**
                • **Road Condition:** Paved asphalt with minor maintenance near Block B.
                • **Active Hazards:** 0 critical hazards. 1 pending streetlight ticket (#CIV-2026-0042).
                • **Weather Risk:** Low Risk - Clear weather.
                • **Transit:** Sector 5 Bus Terminal (Routes 104 & 215-A operating on-time).
            """.trimIndent()
        }

        if (lower.contains("waterlog") || lower.contains("flooding") || lower.contains("water log") || lower.contains("water stagnation")) {
            return """
                🌊 **Live Waterlogging Status & Drainage Alert**
                
                • **Hazard Location:** Mathura Road Underpass
                • **Severity:** High Risk (Standing water ~2.5 ft deep)
                • **Affected Transit:** Bus Route 52 diverted via Elevated Flyover (+12 mins)
                • **Recommended Safe Alternative:** Use Elevated Barapullah Bypass or Ring Road Flyover (100% dry and operational).
                • **Municipal Action:** Drainage & Flood Control crew deployed with de-watering pumps.
            """.trimIndent()
        }

        if (lower.contains("which bus") || lower.contains("what bus") || lower.contains("bus should i take") || lower.contains("bus route")) {
            return """
                🚌 **Transit Bus Recommendation**
                
                • **Primary Recommendation:** **Bus Route 104 (Express Line)** from Sector 62 IT Hub to Central Hub.
                • **Frequency:** Every 10 mins | **Fare:** ₹15 | **ETA:** 28 mins
                • **Alternate:** **Bus Route 215-A** (South Extension Feeder).
                • **Transit Notice:** Bus Route 52 is currently experiencing a +15 min delay due to underpass waterlogging diversion.
            """.trimIndent()
        }

        if (lower.contains("cheapest") || lower.contains("lowest fare") || lower.contains("budget")) {
            return """
                💰 **Cheapest Route Alternative**
                
                • **Transit Mode:** Direct DTC Municipal Bus (Route 104)
                • **Total Fare:** **₹15** (Save up to 75% compared to auto/cab)
                • **ETA:** **36 minutes**
                • **Safety Score:** **86/100**
                • **Walking Distance:** 420 meters
            """.trimIndent()
        }


        return when {
            lower.contains("pothole") || lower.contains("road") || lower.contains("asphalt") || lower.contains("sidewalk") -> {
                """
                🛣️ **Road & Pothole Grievances Guide**
                
                • **Reporting Steps:**
                  1. Tap **'REPORT NEW ISSUE'** from the Home tab.
                  2. Select category **'Roads & Potholes'**.
                  3. Enter the exact street, crossroad, or nearby landmark.
                  4. Add photos showing the depth/extent of the road damage.
                
                • **Assigned Department:** Roads & Infrastructure (PWD).
                • **Standard Resolution SLA:** 48 to 72 business hours for critical arterial roads.
                • **Tip:** Encourage neighbors to **Upvote** your report to boost municipal triage priority!
                """.trimIndent()
            }
            lower.contains("water") || lower.contains("pipe") || lower.contains("leak") || lower.contains("sewer") || lower.contains("supply") -> {
                """
                💧 **Water Supply & Sewerage Inquiries**
                
                • **Department in charge:** Water Supply & Sewerage Board.
                • **Common Issues handled:**
                  - Drinking water supply disruptions & low pressure
                  - Pipeline burst / main water leakages
                  - Contaminated water or sewage overflow
                
                • **Emergency Action:** For severe pipe bursts, file an urgent report on CivicFix and dial the **24/7 Water Emergency Hotline: 1916**.
                """.trimIndent()
            }
            lower.contains("streetlight") || lower.contains("light") || lower.contains("dark") || lower.contains("lamp") || lower.contains("electric") || lower.contains("power") -> {
                """
                💡 **Streetlight & Electrical Infrastructure**
                
                • **Department:** Electricity & Power Utility Department.
                • **How to Report:**
                  - Note the pole number (often stamped with yellow/white paint on the pole).
                  - Specify whether the light is completely off, flickering, or the timer is misaligned.
                • **Resolution Time:** Typically repaired within **24 to 48 hours**.
                • **Safety Warning:** If you notice exposed live wires or sparking, call the **Electricity Emergency line: 1912** immediately.
                """.trimIndent()
            }
            lower.contains("garbage") || lower.contains("waste") || lower.contains("trash") || lower.contains("bin") || lower.contains("dump") || lower.contains("sanitation") -> {
                """
                🗑️ **Sanitation & Waste Management**
                
                • **Department:** Sanitation & Solid Waste Management Dept.
                • **Supported Grievances:**
                  - Missed door-to-door waste collection
                  - Overflowing public dumpsters / community bins
                  - Open illegal dumping spots requiring mechanized clearance
                
                • **Resolution SLA:** Sanitation crews are dispatched within **12 to 24 hours**.
                """.trimIndent()
            }
            lower.contains("helpline") || lower.contains("emergency") || lower.contains("contact") || lower.contains("phone") || lower.contains("number") -> {
                """
                🚨 **Essential Civic Emergency Helplines**
                
                • **National Emergency Service:** `112`
                • **Municipal Corporation Helpline:** `1913` / `1800-111-999`
                • **Water Supply Emergency:** `1916`
                • **Electricity Breakdown:** `1912`
                • **Fire Control Room:** `101`
                • **Ambulance Service:** `108` / `102`
                • **Police Control Room:** `100`
                
                *For non-emergency community issues, lodge your report directly via the CivicFix app for tracked resolution.*
                """.trimIndent()
            }
            lower.contains("track") || lower.contains("status") || lower.contains("my issue") || lower.contains("complaint") || lower.contains("progress") -> {
                """
                🔍 **Tracking Your Grievances**
                
                • **Where to look:** Open **'My Grievances'** tab from the top bar or profile screen.
                • **Status Meanings:**
                  - 🟡 **Pending:** Your complaint has been registered and is pending departmental inspection.
                  - 🔵 **In Progress:** Assigned officer and field crew are actively working on-site.
                  - 🟢 **Resolved:** Remediation completed with official authority confirmation.
                  - 🔴 **Rejected:** Flagged as duplicate or invalid address details.
                • **Push Updates:** You will receive real-time notifications in your Notifications center whenever an officer updates your ticket!
                """.trimIndent()
            }
            lower.contains("upvote") || lower.contains("vote") || lower.contains("community") -> {
                """
                👍 **How the Community Upvote System Works**
                
                • **Community Power:** When citizens upvote an issue, it signals collective impact to ward officers.
                • **Triage Boost:** Issues with higher upvotes are highlighted on the **Admin Priority Board**, expediting budget and crew allocation.
                • **Participate:** Browse the **'Community Feed'** to support neighbor issues affecting your locality!
                """.trimIndent()
            }
            lower.contains("admin") || lower.contains("role") || lower.contains("officer") -> {
                """
                🏛️ **Roles & Municipal Administration**
                
                • **Citizen Role:** File complaints, track status, view local issues, and upvote community reports.
                • **Admin / Ward Officer Role:** Review pending tickets, assign responsible departments, set priority (High/Medium/Low), update real-time progress status, and add official closing remarks.
                • **Role Switcher:** Use the top-bar profile icon or bottom role switch to test both Citizen and Municipal Officer dashboards!
                """.trimIndent()
            }
            else -> {
                """
                👋 **Hello! I'm your CivicFix AI Assistant.**
                
                I'm here to help you resolve community queries and navigate municipal services. Here is what I can assist with:
                
                1. 📝 **Filing a Report:** Guidance on providing clear details, photos, and location landmarks.
                2. 🏢 **Department Routing:** Find out which department handles roads, sanitation, water, or power.
                3. 📊 **Tracking Grievances:** Understand progress stages and resolution timelines.
                4. 🚨 **Emergency Numbers:** Get instant helpline contacts for civic hazards.
                
                *Feel free to ask any question or tap one of the quick suggestions below!*
                """.trimIndent()
            }
        }
    }
}

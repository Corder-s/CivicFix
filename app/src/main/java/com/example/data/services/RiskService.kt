package com.example.data.services

/**
 * Service abstraction for calculating safety scores (0-100) and risk levels for road hazards.
 */
interface RiskService {
    fun calculateSafetyScore(severity: IncidentSeverity, upvotes: Int, weatherRisk: String): Int
    fun getSafetyLabel(score: Int): Pair<String, String> // e.g. ("CRITICAL HAZARD", "High risk of vehicle damage or accident")
}

class DefaultRiskService : RiskService {
    override fun calculateSafetyScore(severity: IncidentSeverity, upvotes: Int, weatherRisk: String): Int {
        val baseScore = when (severity) {
            IncidentSeverity.CRITICAL -> 20
            IncidentSeverity.HIGH -> 38
            IncidentSeverity.MEDIUM -> 62
            IncidentSeverity.LOW -> 85
        }
        val penalty = (upvotes.coerceAtMost(30) * 0.5).toInt()
        val finalScore = (baseScore - penalty).coerceIn(10, 98)
        return finalScore
    }

    override fun getSafetyLabel(score: Int): Pair<String, String> {
        return when {
            score < 30 -> Pair("CRITICAL DANGER", "Immediate hazard. Avoid this route or proceed with extreme caution.")
            score < 50 -> Pair("HIGH RISK", "Severe road defect. Significant delay and vehicle damage risk.")
            score < 75 -> Pair("MODERATE RISK", "Caution advised. Active road or utility work.")
            else -> Pair("SAFE CORRIDOR", "Normal traffic and paved road conditions.")
        }
    }
}

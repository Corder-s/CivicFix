package com.example.data.services

import com.example.data.models.CivicIssue
import com.example.data.models.IssueCategory
import com.example.data.models.IssueStatus

interface PredictiveRiskService {
    fun getPredictiveHazards(liveIssues: List<CivicIssue>, weatherAlert: WeatherSafetyAlert): List<PredictiveHazard>
    fun getCivicMobilityScores(liveIssues: List<CivicIssue>): List<CivicMobilityScore>
    fun getWardScore(wardCode: String, liveIssues: List<CivicIssue>): CivicMobilityScore
}

class DefaultPredictiveRiskService : PredictiveRiskService {

    override fun getPredictiveHazards(
        liveIssues: List<CivicIssue>,
        weatherAlert: WeatherSafetyAlert
    ): List<PredictiveHazard> {
        val basePredictions = mutableListOf<PredictiveHazard>()

        // 1. Waterlogging Underpass Pattern
        val hasRainfall = weatherAlert.precipitationMm > 5.0
        basePredictions.add(
            PredictiveHazard(
                id = "PRED-01",
                title = "Underpass Flash Flooding Susceptibility",
                hazardType = "Repeated Waterlogging",
                location = "Mathura Road / Minto Underpass",
                probabilityPercent = if (hasRainfall) 92 else 45,
                confidenceLabel = if (hasRainfall) "High Probability (AI Prediction)" else "Moderate Risk",
                triggerFactor = "Precipitation exceeds 10 mm/hr & poor drainage gradient",
                historicalPatternSummary = "Historical log: 6 severe water stagnation events recorded during monsoon precipitation.",
                recommendedMitigation = "AI Advisory: Divert to Elevated Barapullah Bypass or Ring Road Flyover."
            )
        )

        // 2. Recurrent Pothole Clustering
        val roadIssuesCount = liveIssues.count { it.category == IssueCategory.ROADS && it.status != IssueStatus.RESOLVED }
        basePredictions.add(
            PredictiveHazard(
                id = "PRED-02",
                title = "Heavy Axle Road Degradation & Pothole Spikes",
                hazardType = "Pothole Cluster",
                location = "Outer Ring Road Junction (Km 14)",
                probabilityPercent = (70 + (roadIssuesCount * 3)).coerceIn(60, 95),
                confidenceLabel = "High Probability (AI Prediction)",
                triggerFactor = "Commercial freight volume + sub-base asphalt wear",
                historicalPatternSummary = "14 citizen reports logged along this 800m stretch in the past 60 days.",
                recommendedMitigation = "AI Advisory: Maintain lane discipline; reduce speed to 30 km/h."
            )
        )

        // 3. Accident Blackspot Vulnerability
        basePredictions.add(
            PredictiveHazard(
                id = "PRED-03",
                title = "Evening Congestion & Blind Intersection Risk",
                hazardType = "Accident-Prone Hotspot",
                location = "Sector 18 Commercial Curve",
                probabilityPercent = 76,
                confidenceLabel = "Moderate Risk (AI Recommendation)",
                triggerFactor = "High turning conflict index during 18:00 - 20:30 rush",
                historicalPatternSummary = "Multiple low-speed vehicle impacts reported during peak evening merger times.",
                recommendedMitigation = "AI Advisory: Utilize signal-controlled Sector 16 crossover."
            )
        )

        // 4. Low-lying Drainage Runoff
        basePredictions.add(
            PredictiveHazard(
                id = "PRED-04",
                title = "Stormwater Backflow Vulnerability",
                hazardType = "Monsoon Runoff Vulnerability",
                location = "Lajpat Nagar Ring Road Culvert",
                probabilityPercent = if (hasRainfall) 85 else 38,
                confidenceLabel = if (hasRainfall) "High Probability" else "Advisory",
                triggerFactor = "Culvert siltation during heavy storm cloudburst",
                historicalPatternSummary = "3 sewer overflow complaints registered this season.",
                recommendedMitigation = "AI Advisory: Emergency de-watering pump deployed by Municipal Drainage Wing."
            )
        )

        return basePredictions
    }

    override fun getCivicMobilityScores(liveIssues: List<CivicIssue>): List<CivicMobilityScore> {
        val unresolved = liveIssues.filter { it.status != IssueStatus.RESOLVED }

        val ward12Issues = unresolved.count { it.location.contains("Connaught", ignoreCase = true) || it.location.contains("Central", ignoreCase = true) }
        val ward14Issues = unresolved.count { it.location.contains("South", ignoreCase = true) || it.location.contains("Ring", ignoreCase = true) }
        val ward22Issues = unresolved.count { it.location.contains("East", ignoreCase = true) || it.location.contains("Mathura", ignoreCase = true) || it.location.contains("Sector 62", ignoreCase = true) }
        val ward07Issues = unresolved.count { it.location.contains("West", ignoreCase = true) || it.location.contains("Metro", ignoreCase = true) }

        return listOf(
            CivicMobilityScore(
                wardName = "Ward 12 - Central Connaught",
                wardCode = "W12",
                overallScore = (85 - (ward12Issues * 3)).coerceIn(60, 98),
                roadSafetyScore = 88,
                transitAccessScore = 96,
                roadConditionScore = 82,
                weatherRiskScore = 84,
                emergencyAccessScore = 94,
                activeHazardsCount = ward12Issues,
                complaintsTrend = "Decreasing (-22%)",
                primaryStrengths = listOf("Multimodal Metro & Bus Hubs", "24/7 Apex Trauma Care Access", "Rapid Slag Patching"),
                attentionAreas = listOf("Peak evening pedestrian conflicts", "Inner circle parking lane bottlenecks")
            ),
            CivicMobilityScore(
                wardName = "Ward 14 - South Extension & IT Hub",
                wardCode = "W14",
                overallScore = (81 - (ward14Issues * 3)).coerceIn(55, 95),
                roadSafetyScore = 82,
                transitAccessScore = 91,
                roadConditionScore = 67,
                weatherRiskScore = 78,
                emergencyAccessScore = 88,
                activeHazardsCount = ward14Issues,
                complaintsTrend = "Stable (±3%)",
                primaryStrengths = listOf("High-capacity elevated flyovers", "Frequent feeder transit", "Smart traffic lights"),
                attentionAreas = listOf("Potholes near service lanes", "Drainage during cloudbursts")
            ),
            CivicMobilityScore(
                wardName = "Ward 22 - East Expressway & Lowlands",
                wardCode = "W22",
                overallScore = (73 - (ward22Issues * 4)).coerceIn(40, 90),
                roadSafetyScore = 71,
                transitAccessScore = 79,
                roadConditionScore = 64,
                weatherRiskScore = 58,
                emergencyAccessScore = 82,
                activeHazardsCount = ward22Issues,
                complaintsTrend = "High Active Volume (+18%)",
                primaryStrengths = listOf("Signal-free expressway lanes", "Direct regional bus routes"),
                attentionAreas = listOf("Mathura underpass waterlogging", "Active stormwater drain desiltation required")
            ),
            CivicMobilityScore(
                wardName = "Ward 07 - West Industrial & Transit Hub",
                wardCode = "W07",
                overallScore = (79 - (ward07Issues * 3)).coerceIn(50, 94),
                roadSafetyScore = 78,
                transitAccessScore = 86,
                roadConditionScore = 72,
                weatherRiskScore = 76,
                emergencyAccessScore = 84,
                activeHazardsCount = ward07Issues,
                complaintsTrend = "Improving (-11%)",
                primaryStrengths = listOf("Dedicated bus transit corridors", "High streetlight coverage"),
                attentionAreas = listOf("Heavy truck axle road wear", "Junction signal synchronization")
            )
        )
    }

    override fun getWardScore(wardCode: String, liveIssues: List<CivicIssue>): CivicMobilityScore {
        val all = getCivicMobilityScores(liveIssues)
        return all.firstOrNull { it.wardCode.equals(wardCode, ignoreCase = true) }
            ?: all.first()
    }
}

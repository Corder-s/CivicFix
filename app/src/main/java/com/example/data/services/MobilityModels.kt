package com.example.data.services

import androidx.compose.ui.graphics.Color
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.data.models.IssueStatus

/**
 * Mobility Incident and Hazard Types for CivicLive Map
 */
enum class IncidentType(
    val displayName: String,
    val hindiName: String,
    val iconKey: String,
    val defaultColor: Color
) {
    POTHOLE("Pothole", "गड्ढा", "traffic", Color(0xFFE65100)),
    WATERLOGGING("Waterlogging", "जलभराव", "waves", Color(0xFF0288D1)),
    ROAD_DAMAGE("Road Damage", "सड़क क्षति", "construction", Color(0xFFD32F2F)),
    ACCIDENT("Traffic Accident", "दुर्घटना", "car_crash", Color(0xFFC2185B)),
    CONSTRUCTION("Construction Work", "निर्माण कार्य", "engineering", Color(0xFFF57F17)),
    ROAD_CLOSURE("Road Closure", "सड़क बंद", "block", Color(0xFFB71C1C)),
    FALLEN_TREE("Fallen Tree / Debris", "गिरा हुआ पेड़", "park", Color(0xFF388E3C)),
    BROKEN_SIGNAL("Broken Traffic Signal", "खराब सिग्नल", "traffic_light", Color(0xFFE64A19)),
    BROKEN_STREETLIGHT("Broken Streetlight", "खराब स्ट्रीटलाइट", "lightbulb", Color(0xFFFBC02D)),
    WEATHER_HAZARD("Weather Hazard", "मौसम का खतरा", "thunderstorm", Color(0xFF455A64)),
    HIGH_RISK_ROAD("High-Risk Road Corridor", "उच्च जोखिम सड़क", "warning", Color(0xFFBF360C)),
    EMERGENCY_FACILITY("Emergency Center", "आपातकालीन केंद्र", "local_hospital", Color(0xFF1976D2)),
    BUS_STOP("Bus Stop & Route Hub", "बस स्टॉप", "directions_bus", Color(0xFF5E35B1)),
    CIVIC_COMPLAINT("Civic Grievance", "नागरिक शिकायत", "report", Color(0xFF7B1FA2)),
    OTHER("Other Hazard", "अन्य खतरा", "info", Color(0xFF607D8B));

    companion object {
        fun fromCategory(category: IssueCategory): IncidentType {
            return when (category) {
                IssueCategory.ROADS -> POTHOLE
                IssueCategory.WATER -> WATERLOGGING
                IssueCategory.DRAINAGE -> WATERLOGGING
                IssueCategory.ELECTRICITY -> BROKEN_SIGNAL
                IssueCategory.STREETLIGHT -> BROKEN_STREETLIGHT
                IssueCategory.GARBAGE -> CIVIC_COMPLAINT
                IssueCategory.PUBLIC_PROPERTY -> ROAD_DAMAGE
                IssueCategory.OTHER -> OTHER
            }
        }
    }
}

enum class IncidentSeverity(val displayName: String, val level: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    CRITICAL("Critical", 4);

    companion object {
        fun fromPriority(priority: IssuePriority): IncidentSeverity {
            return when (priority) {
                IssuePriority.LOW -> LOW
                IssuePriority.MEDIUM -> MEDIUM
                IssuePriority.HIGH -> HIGH
            }
        }
    }
}

/**
 * Filter layer for CivicLive Map
 */
enum class MapLayerFilter(val labelKey: String, val icon: String) {
    ALL("All", "layers"),
    HAZARDS("Hazards", "warning"),
    POTHOLES("Potholes", "traffic"),
    WATERLOGGING("Waterlogging", "waves"),
    CONSTRUCTION("Construction", "engineering"),
    TRANSIT("Buses & Stops", "directions_bus"),
    EMERGENCY("Emergency", "local_hospital"),
    WEATHER("Weather Risk", "thunderstorm")
}

/**
 * Unified Map Marker representation for CivicLive Map
 */
data class IncidentMarker(
    val id: String,
    val title: String,
    val description: String,
    val type: IncidentType,
    val latitude: Double,
    val longitude: Double,
    val locationName: String,
    val address: String,
    val severity: IncidentSeverity,
    val status: IssueStatus,
    val reportCount: Int = 1,
    val upvotes: Int = 0,
    val hasUserUpvoted: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis(),
    val safetyScore: Int = 50, // 0 (Extremely Hazardous) to 100 (Completely Safe)
    val roadCondition: String = "Normal Asphalt",
    val weatherRisk: String = "Low Risk - Clear Weather",
    val affectedBusRoutes: List<String> = emptyList(),
    val photoUri: String? = null,
    val relatedCivicIssueId: String? = null,
    val isDemonstrationData: Boolean = false
)

/**
 * Bus Route metadata
 */
data class BusRouteInfo(
    val routeNumber: String,
    val routeName: String,
    val origin: String,
    val destination: String,
    val operationalStatus: String, // "On-Time", "Delayed (+15m)", "Diverted"
    val frequencyMinutes: Int,
    val activeHazardsOnRoute: Int,
    val stopsCount: Int,
    val isDelayed: Boolean = false
)

/**
 * Emergency facility metadata
 */
data class EmergencyFacility(
    val id: String,
    val name: String,
    val type: String, // "Hospital / Trauma Center", "Police Station", "Fire Station"
    val address: String,
    val helpline: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val isOpen24Hours: Boolean = true
)

/**
 * Weather & Urban Hydrological Safety Alert
 */
data class WeatherSafetyAlert(
    val headline: String,
    val precipitationMm: Double,
    val windSpeedKmh: Double,
    val temperatureCelsius: Double,
    val waterloggingRisk: String, // "High Risk", "Moderate Risk", "Low"
    val advisoryText: String,
    val affectedZones: List<String>
)

/**
 * Road Risk Segment
 */
data class RoadRiskSegment(
    val roadName: String,
    val zone: String,
    val safetyScore: Int,
    val primaryHazard: String,
    val reportedIncidentsCount: Int,
    val recommendedAction: String
)

/**
 * Travel preferences for AI Safe Journey Planner
 */
enum class TravelPreference(
    val displayName: String,
    val subtitle: String,
    val iconKey: String
) {
    FASTEST("Fastest", "Minimum travel time", "speed"),
    CHEAPEST("Cheapest", "Lowest transit fare", "payments"),
    SAFEST("Safest", "Maximum safety & hazard avoidance", "shield"),
    BEST_OVERALL("Best Overall", "Optimal balance of safety, time & fare", "star")
}

/**
 * Leg types for multimodal transit itinerary
 */
enum class TransitLegType(val displayName: String, val iconKey: String) {
    WALK("Walk", "directions_walk"),
    BUS("Bus", "directions_bus"),
    METRO_FEEDER("Feeder Transit", "electric_rickshaw")
}

/**
 * Individual leg of a multimodal safe journey
 */
data class TransitLeg(
    val legId: String,
    val type: TransitLegType,
    val title: String,
    val instruction: String,
    val durationMinutes: Int,
    val distanceMeters: Int,
    val fareInr: Int? = null,
    val busNumber: String? = null,
    val busRouteName: String? = null,
    val departureLocation: String? = null,
    val arrivalLocation: String? = null,
    val stopsList: List<String> = emptyList(),
    val safetyScore: Int = 85,
    val hazardWarnings: List<String> = emptyList(),
    val isDelayed: Boolean = false,
    val delayMinutes: Int = 0
)

/**
 * Explainable factor breakdown for Route Safety Score
 */
data class SafetyFactorItem(
    val title: String,
    val isPositive: Boolean,
    val description: String,
    val categoryKey: String = "general"
)

/**
 * Complete Route Alternative generated by AI Safe Journey Planner
 */
data class RouteAlternative(
    val id: String,
    val routeName: String,
    val primaryTag: TravelPreference,
    val etaMinutes: Int,
    val fareInr: Int?,
    val distanceKm: Double,
    val walkingDistanceMeters: Int,
    val transfersCount: Int,
    val safetyScore: Int, // 0 to 100
    val legs: List<TransitLeg>,
    val civicHazards: List<String>,
    val weatherRisk: String,
    val recommendationExplanation: String,
    val safetyFactors: List<SafetyFactorItem>,
    val isRecommended: Boolean = false,
    val hasDisruption: Boolean = false,
    val disruptionReason: String? = null,
    val alternativeSuggestion: String? = null,
    val affectedBusRoutes: List<String> = emptyList(),
    val co2SavedKg: Double = 1.2,
    val isDemoData: Boolean = true
)

/**
 * AI Predictive Road Risk Pattern
 */
data class PredictiveHazard(
    val id: String,
    val title: String,
    val hazardType: String, // "Repeated Waterlogging", "Pothole Cluster", "Accident-Prone Hotspot", "Monsoon Runoff Vulnerability"
    val location: String,
    val probabilityPercent: Int, // e.g. 88%
    val confidenceLabel: String = "High Probability", // "High Probability", "Moderate Risk", "Advisory"
    val triggerFactor: String, // e.g. "Triggered during rainfall > 15 mm/h"
    val historicalPatternSummary: String, // e.g. "4 flooding incidents reported in last 3 monsoon storms"
    val recommendedMitigation: String, // e.g. "Take Elevated Corridor bypass; avoid lower slip roads"
    val isAiPrediction: Boolean = true
)

/**
 * Civic Mobility Area / Ward Composite Score
 */
data class CivicMobilityScore(
    val wardName: String,
    val wardCode: String,
    val overallScore: Int, // e.g. 81
    val roadSafetyScore: Int, // e.g. 82
    val transitAccessScore: Int, // e.g. 91
    val roadConditionScore: Int, // e.g. 67
    val weatherRiskScore: Int, // e.g. 78
    val emergencyAccessScore: Int, // e.g. 88
    val activeHazardsCount: Int,
    val complaintsTrend: String = "Decreasing (-14%)",
    val primaryStrengths: List<String> = emptyList(),
    val attentionAreas: List<String> = emptyList()
)

/**
 * Recommended Emergency Route metadata
 */
data class EmergencyRouteInfo(
    val targetFacility: EmergencyFacility,
    val originName: String,
    val estimatedTimeMinutes: Int,
    val distanceKm: Double,
    val roadAccessibility: String, // e.g. "100% Paved & Elevated Corridor"
    val trafficCongestionLevel: String, // "Low Congestion (Priority Corridor)"
    val roadCondition: String, // "Optimal Smooth Asphalt"
    val activeClosuresCount: Int = 0,
    val waterloggingRisk: String = "No Flooding on Corridor",
    val routeSteps: List<String>,
    val safetyExplanation: String
)



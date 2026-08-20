package com.example.data.services

import com.example.data.models.CivicIssue
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.data.models.IssueStatus

/**
 * Service abstraction for road risk corridors, road condition indexing, and safety routing.
 */
interface RouteService {
    fun getHighRiskCorridors(): List<RoadRiskSegment>
    fun getRoadQualityIndex(roadName: String): Int
    fun planSafeJourney(
        origin: String,
        destination: String,
        dateTimeText: String,
        preference: TravelPreference,
        liveCivicIssues: List<CivicIssue>,
        weatherAlert: WeatherSafetyAlert,
        busRoutes: List<BusRouteInfo>
    ): List<RouteAlternative>
    fun getSafetyFactorBreakdown(
        routeTag: TravelPreference,
        hasWaterloggingRisk: Boolean,
        potholeHazardsCount: Int,
        weatherAlert: WeatherSafetyAlert,
        hasBusDisruption: Boolean
    ): List<SafetyFactorItem>
}

class DefaultRouteService : RouteService {

    override fun getHighRiskCorridors(): List<RoadRiskSegment> {
        return listOf(
            RoadRiskSegment(
                roadName = "Outer Ring Road (Sector 18 to 62 Link)",
                zone = "East Corridor",
                safetyScore = 32,
                primaryHazard = "Multiple deep potholes & uneven asphalt joints",
                reportedIncidentsCount = 14,
                recommendedAction = "Reduce speed to 30 km/h or divert via Express Link"
            ),
            RoadRiskSegment(
                roadName = "Mathura Road Underpass Section",
                zone = "Central Arterial",
                safetyScore = 22,
                primaryHazard = "Recurrent waterlogging & poor drainage",
                reportedIncidentsCount = 28,
                recommendedAction = "Avoid during rain; use Ring Road surface route"
            ),
            RoadRiskSegment(
                roadName = "Ashram Chowk Elevated Approach",
                zone = "South Gate",
                safetyScore = 48,
                primaryHazard = "Ongoing lane resurfacing & barricades",
                reportedIncidentsCount = 8,
                recommendedAction = "Expect +15 mins congestion during peak hours"
            )
        )
    }

    override fun getRoadQualityIndex(roadName: String): Int {
        return when {
            roadName.contains("Underpass", ignoreCase = true) -> 35
            roadName.contains("Flyover", ignoreCase = true) -> 55
            roadName.contains("Ring Road", ignoreCase = true) -> 65
            else -> 75
        }
    }

    override fun planSafeJourney(
        origin: String,
        destination: String,
        dateTimeText: String,
        preference: TravelPreference,
        liveCivicIssues: List<CivicIssue>,
        weatherAlert: WeatherSafetyAlert,
        busRoutes: List<BusRouteInfo>
    ): List<RouteAlternative> {
        val cleanOrigin = if (origin.isBlank()) "Home (Sector 62)" else origin.trim()
        val cleanDest = if (destination.isBlank()) "City Center / Connaught Place" else destination.trim()

        // Analyze live civic issues from Room Database for route corridor correlations
        val relevantIssues = liveCivicIssues.filter { it.status != IssueStatus.RESOLVED }
        val waterloggingIssues = relevantIssues.filter {
            it.category == IssueCategory.WATER ||
            it.category == IssueCategory.DRAINAGE ||
            it.title.contains("water", ignoreCase = true) ||
            it.title.contains("drain", ignoreCase = true) ||
            it.location.contains("underpass", ignoreCase = true)
        }
        val roadDamageIssues = relevantIssues.filter {
            it.category == IssueCategory.ROADS ||
            it.category == IssueCategory.PUBLIC_PROPERTY ||
            it.title.contains("pothole", ignoreCase = true) ||
            it.title.contains("crater", ignoreCase = true)
        }

        val hasSevereWaterlogging = waterloggingIssues.isNotEmpty() || weatherAlert.precipitationMm > 20.0
        val potholeCount = roadDamageIssues.size

        // Build 4 dynamic alternatives: FASTEST, CHEAPEST, SAFEST, BEST_OVERALL

        // 1. FASTEST (Express Corridor - Shortest time, but higher exposure to underpass/expressway risks)
        val fastestHazards = mutableListOf<String>()
        var fastestScore = 78
        if (hasSevereWaterlogging) {
            fastestHazards.add("High waterlogging risk near Mathura Road Underpass due to rain")
            fastestScore -= 18
        }
        if (potholeCount > 0) {
            fastestHazards.add("$potholeCount active road/pothole issues reported along express corridor")
            fastestScore -= 8
        }
        val fastestLegs = listOf(
            TransitLeg(
                legId = "f-leg-1",
                type = TransitLegType.WALK,
                title = "Walk to Transit Hub",
                instruction = "Walk 250m to nearest Express Bus Shelter",
                durationMinutes = 4,
                distanceMeters = 250,
                safetyScore = 88
            ),
            TransitLeg(
                legId = "f-leg-2",
                type = TransitLegType.BUS,
                title = "Board Express Bus 118",
                instruction = "Express Route 118 towards Central Hub",
                durationMinutes = 18,
                distanceMeters = 7200,
                fareInr = 20,
                busNumber = "Bus 118",
                busRouteName = "Direct Express Corridor",
                departureLocation = "$cleanOrigin Shelter",
                arrivalLocation = "Central Express Terminal",
                stopsList = listOf("$cleanOrigin Shelter", "Tech Zone Junction", "Highway Flyover", "Central Express Terminal"),
                safetyScore = fastestScore,
                hazardWarnings = if (fastestHazards.isNotEmpty()) listOf("Underpass speed restriction: 30 km/h") else emptyList(),
                isDelayed = hasSevereWaterlogging,
                delayMinutes = if (hasSevereWaterlogging) 6 else 0
            ),
            TransitLeg(
                legId = "f-leg-3",
                type = TransitLegType.WALK,
                title = "Walk to Destination",
                instruction = "Walk 150m to $cleanDest",
                durationMinutes = 5,
                distanceMeters = 150,
                safetyScore = 92
            )
        )

        val fastestRoute = RouteAlternative(
            id = "route_fastest",
            routeName = "Express Corridor via Highway Flyover",
            primaryTag = TravelPreference.FASTEST,
            etaMinutes = 27 + (if (hasSevereWaterlogging) 4 else 0),
            fareInr = 20,
            distanceKm = 7.6,
            walkingDistanceMeters = 400,
            transfersCount = 0,
            safetyScore = fastestScore.coerceIn(40, 95),
            legs = fastestLegs,
            civicHazards = fastestHazards,
            weatherRisk = if (hasSevereWaterlogging) "High Risk - Waterlogging on Underpasses" else "Low Weather Risk",
            recommendationExplanation = if (hasSevereWaterlogging) {
                "Fastest route is available but encounters severe waterlogging advisory near underpass section. Exercise caution."
            } else {
                "Direct express link minimizing total travel time with zero bus transfers."
            },
            safetyFactors = getSafetyFactorBreakdown(
                routeTag = TravelPreference.FASTEST,
                hasWaterloggingRisk = hasSevereWaterlogging,
                potholeHazardsCount = potholeCount,
                weatherAlert = weatherAlert,
                hasBusDisruption = hasSevereWaterlogging
            ),
            isRecommended = preference == TravelPreference.FASTEST && !hasSevereWaterlogging,
            hasDisruption = hasSevereWaterlogging,
            disruptionReason = if (hasSevereWaterlogging) "Waterlogging at Mathura Road Underpass; bus slowdown reported" else null,
            alternativeSuggestion = if (hasSevereWaterlogging) "Consider taking Route B (Safest - Elevated Metro Corridor)" else null,
            affectedBusRoutes = listOf("Bus 118 (Slowdown)"),
            co2SavedKg = 1.1,
            isDemoData = true
        )

        // 2. CHEAPEST (Standard Municipal Bus Route - Budget optimized)
        val cheapestHazards = mutableListOf<String>()
        var cheapestScore = 81
        if (potholeCount > 0) {
            cheapestHazards.add("Minor uneven asphalt along municipal arterial road")
            cheapestScore -= 6
        }
        val cheapestLegs = listOf(
            TransitLeg(
                legId = "c-leg-1",
                type = TransitLegType.WALK,
                title = "Walk to Sector Bus Stop",
                instruction = "Walk 450m to City Bus Stop",
                durationMinutes = 6,
                distanceMeters = 450,
                safetyScore = 85
            ),
            TransitLeg(
                legId = "c-leg-2",
                type = TransitLegType.BUS,
                title = "Board Municipal Bus 104",
                instruction = "City Bus 104 (Non-AC Ordinary)",
                durationMinutes = 24,
                distanceMeters = 8100,
                fareInr = 12,
                busNumber = "Bus 104",
                busRouteName = "Sector Feeder ⇄ Central Bus Terminal",
                departureLocation = "Sector Bus Stop",
                arrivalLocation = "$cleanDest Municipal Stand",
                stopsList = listOf("Sector Bus Stop", "Market Gate", "Hospital Chowk", "Railway Crossing", "$cleanDest Stand"),
                safetyScore = cheapestScore,
                hazardWarnings = emptyList(),
                isDelayed = false
            ),
            TransitLeg(
                legId = "c-leg-3",
                type = TransitLegType.WALK,
                title = "Walk to Final Destination",
                instruction = "Walk 350m to $cleanDest entrance",
                durationMinutes = 5,
                distanceMeters = 350,
                safetyScore = 90
            )
        )

        val cheapestRoute = RouteAlternative(
            id = "route_cheapest",
            routeName = "Municipal Direct via Ring Arterial",
            primaryTag = TravelPreference.CHEAPEST,
            etaMinutes = 35,
            fareInr = 12,
            distanceKm = 8.9,
            walkingDistanceMeters = 800,
            transfersCount = 0,
            safetyScore = cheapestScore.coerceIn(50, 95),
            legs = cheapestLegs,
            civicHazards = cheapestHazards,
            weatherRisk = "Moderate Risk - Standard Drainage",
            recommendationExplanation = "Lowest transit fare at ₹12 with direct municipal bus connection.",
            safetyFactors = getSafetyFactorBreakdown(
                routeTag = TravelPreference.CHEAPEST,
                hasWaterloggingRisk = false,
                potholeHazardsCount = potholeCount,
                weatherAlert = weatherAlert,
                hasBusDisruption = false
            ),
            isRecommended = preference == TravelPreference.CHEAPEST,
            hasDisruption = false,
            affectedBusRoutes = listOf("Bus 104"),
            co2SavedKg = 1.6,
            isDemoData = true
        )

        // 3. SAFEST (Elevated Transit & Well-Lit Main Corridor - Maximum Hazard Avoidance)
        val safestHazards = emptyList<String>()
        val safestScore = 94
        val safestLegs = listOf(
            TransitLeg(
                legId = "s-leg-1",
                type = TransitLegType.WALK,
                title = "Walk via Well-Lit Footpath",
                instruction = "Walk 200m along pedestrian-only sidewalk with CCTV coverage",
                durationMinutes = 3,
                distanceMeters = 200,
                safetyScore = 98
            ),
            TransitLeg(
                legId = "s-leg-2",
                type = TransitLegType.BUS,
                title = "Board Electric Bus 520 (AC Low-Floor)",
                instruction = "Electric Low-Floor Bus 520 via Elevated Flyover Corridor",
                durationMinutes = 14,
                distanceMeters = 5400,
                fareInr = 14,
                busNumber = "Bus 520",
                busRouteName = "Smart Corridor Link",
                departureLocation = "Green Park Transit Station",
                arrivalLocation = "Metro Interchange Hub",
                stopsList = listOf("Green Park Station", "Safdarjung Plaza", "Metro Interchange Hub"),
                safetyScore = 96,
                hazardWarnings = emptyList(),
                isDelayed = false
            ),
            TransitLeg(
                legId = "s-leg-3",
                type = TransitLegType.BUS,
                title = "Transfer to Feeder Bus 52",
                instruction = "Dedicated Transit Corridor Bus 52 to Destination",
                durationMinutes = 11,
                distanceMeters = 3800,
                fareInr = 10,
                busNumber = "Bus 52",
                busRouteName = "University & City Center Shuttle",
                departureLocation = "Metro Interchange Hub",
                arrivalLocation = "$cleanDest North Gate",
                stopsList = listOf("Metro Interchange Hub", "Civic Center", "$cleanDest North Gate"),
                safetyScore = 95,
                hazardWarnings = emptyList(),
                isDelayed = false
            ),
            TransitLeg(
                legId = "s-leg-4",
                type = TransitLegType.WALK,
                title = "Final Walk",
                instruction = "Walk 150m to destination entrance",
                durationMinutes = 4,
                distanceMeters = 150,
                safetyScore = 96
            )
        )

        val safestRoute = RouteAlternative(
            id = "route_safest",
            routeName = "Elevated Flyover & Smart Bus Corridor",
            primaryTag = TravelPreference.SAFEST,
            etaMinutes = 32,
            fareInr = 24,
            distanceKm = 9.5,
            walkingDistanceMeters = 350,
            transfersCount = 1,
            safetyScore = safestScore,
            legs = safestLegs,
            civicHazards = safestHazards,
            weatherRisk = "Zero Risk - 100% Elevated and Storm-Drained Corridor",
            recommendationExplanation = "Avoids all waterlogged underpasses, road construction zones, and pothole clusters. Maximum safety score 94/100.",
            safetyFactors = getSafetyFactorBreakdown(
                routeTag = TravelPreference.SAFEST,
                hasWaterloggingRisk = false,
                potholeHazardsCount = 0,
                weatherAlert = weatherAlert,
                hasBusDisruption = false
            ),
            isRecommended = preference == TravelPreference.SAFEST || hasSevereWaterlogging,
            hasDisruption = false,
            affectedBusRoutes = listOf("Bus 520", "Bus 52"),
            co2SavedKg = 1.8,
            isDemoData = true
        )

        // 4. BEST OVERALL (Optimal Balance of Safety, Speed, and Transit Cost)
        val bestHazards = emptyList<String>()
        val bestScore = 91
        val bestLegs = listOf(
            TransitLeg(
                legId = "b-leg-1",
                type = TransitLegType.WALK,
                title = "Walk to Sector Hub",
                instruction = "Walk 250m to Sector Hub",
                durationMinutes = 4,
                distanceMeters = 250,
                safetyScore = 92
            ),
            TransitLeg(
                legId = "b-leg-2",
                type = TransitLegType.BUS,
                title = "Board Bus 215-A (Smart Feeder)",
                instruction = "Bus 215-A via Ring Road Surface Boulevard",
                durationMinutes = 22,
                distanceMeters = 7800,
                fareInr = 22,
                busNumber = "Bus 215-A",
                busRouteName = "Railway Station ⇄ South Extension",
                departureLocation = "Sector Hub",
                arrivalLocation = "$cleanDest Hub",
                stopsList = listOf("Sector Hub", "Defence Colony", "South Ext Terminal", "$cleanDest Hub"),
                safetyScore = 92,
                hazardWarnings = emptyList(),
                isDelayed = false
            ),
            TransitLeg(
                legId = "b-leg-3",
                type = TransitLegType.WALK,
                title = "Short Walk to Gate",
                instruction = "Walk 200m to $cleanDest main entrance",
                durationMinutes = 5,
                distanceMeters = 200,
                safetyScore = 94
            )
        )

        val bestOverallRoute = RouteAlternative(
            id = "route_best_overall",
            routeName = "Smart Ring Road Corridor (Hybrid Transit)",
            primaryTag = TravelPreference.BEST_OVERALL,
            etaMinutes = 31,
            fareInr = 22,
            distanceKm = 8.2,
            walkingDistanceMeters = 450,
            transfersCount = 0,
            safetyScore = bestScore,
            legs = bestLegs,
            civicHazards = bestHazards,
            weatherRisk = "Low Risk - Modern Surface Drainage",
            recommendationExplanation = "Optimal balance of 31 min travel time, ₹22 fare, and 91/100 safety score with zero transfers.",
            safetyFactors = getSafetyFactorBreakdown(
                routeTag = TravelPreference.BEST_OVERALL,
                hasWaterloggingRisk = false,
                potholeHazardsCount = 0,
                weatherAlert = weatherAlert,
                hasBusDisruption = false
            ),
            isRecommended = (preference == TravelPreference.BEST_OVERALL && !hasSevereWaterlogging) || (!hasSevereWaterlogging && preference != TravelPreference.SAFEST && preference != TravelPreference.CHEAPEST),
            hasDisruption = false,
            affectedBusRoutes = listOf("Bus 215-A"),
            co2SavedKg = 1.4,
            isDemoData = true
        )

        // Return the prioritized list based on user preference
        return when (preference) {
            TravelPreference.FASTEST -> listOf(fastestRoute, bestOverallRoute, safestRoute, cheapestRoute)
            TravelPreference.CHEAPEST -> listOf(cheapestRoute, bestOverallRoute, safestRoute, fastestRoute)
            TravelPreference.SAFEST -> listOf(safestRoute, bestOverallRoute, cheapestRoute, fastestRoute)
            TravelPreference.BEST_OVERALL -> listOf(bestOverallRoute, safestRoute, fastestRoute, cheapestRoute)
        }
    }

    override fun getSafetyFactorBreakdown(
        routeTag: TravelPreference,
        hasWaterloggingRisk: Boolean,
        potholeHazardsCount: Int,
        weatherAlert: WeatherSafetyAlert,
        hasBusDisruption: Boolean
    ): List<SafetyFactorItem> {
        return when (routeTag) {
            TravelPreference.SAFEST -> listOf(
                SafetyFactorItem(
                    title = "Road Condition",
                    isPositive = true,
                    description = "Smooth paved arterial road with active municipal maintenance"
                ),
                SafetyFactorItem(
                    title = "Traffic Density",
                    isPositive = true,
                    description = "Low congestion and dedicated bus rapid transit lane"
                ),
                SafetyFactorItem(
                    title = "Accident & Hazard Risk",
                    isPositive = true,
                    description = "Zero reported accidents or blind spots along this corridor"
                ),
                SafetyFactorItem(
                    title = "Waterlogging & Drainage",
                    isPositive = true,
                    description = "Elevated flyover bypasses all low-lying underpasses completely"
                ),
                SafetyFactorItem(
                    title = "Civic Incidents",
                    isPositive = true,
                    description = "No active citizen complaints reported on this section"
                ),
                SafetyFactorItem(
                    title = "Emergency Accessibility",
                    isPositive = true,
                    description = "Direct proximity to AIIMS / Trauma Center within 4 minutes"
                )
            )

            TravelPreference.FASTEST -> listOf(
                SafetyFactorItem(
                    title = "Traffic & Speed",
                    isPositive = true,
                    description = "High-speed arterial connection with minimal traffic signals"
                ),
                SafetyFactorItem(
                    title = "Road Condition",
                    isPositive = potholeHazardsCount == 0,
                    description = if (potholeHazardsCount == 0) "Even highway surface" else "Uneven joints and $potholeHazardsCount reported potholes"
                ),
                SafetyFactorItem(
                    title = "Waterlogging Risk",
                    isPositive = !hasWaterloggingRisk,
                    description = if (hasWaterloggingRisk) "⚠ High waterlogging risk in Mathura Road underpass after rainfall" else "Clear drainage flow"
                ),
                SafetyFactorItem(
                    title = "Weather Advisory",
                    isPositive = weatherAlert.precipitationMm < 15.0,
                    description = if (weatherAlert.precipitationMm >= 15.0) "⚠ ${weatherAlert.precipitationMm} mm rainfall advisory in effect" else "Dry weather conditions"
                ),
                SafetyFactorItem(
                    title = "Public Transit Status",
                    isPositive = !hasBusDisruption,
                    description = if (hasBusDisruption) "⚠ Bus 118 running with +6m slowdown" else "Transit on schedule"
                ),
                SafetyFactorItem(
                    title = "Emergency Accessibility",
                    isPositive = true,
                    description = "Emergency response corridor reachable within 7 minutes"
                )
            )

            TravelPreference.CHEAPEST -> listOf(
                SafetyFactorItem(
                    title = "Road Condition",
                    isPositive = true,
                    description = "Standard urban municipal street network"
                ),
                SafetyFactorItem(
                    title = "Traffic & Stops",
                    isPositive = true,
                    description = "Multiple passenger boarding stops; moderate stop-and-go speed"
                ),
                SafetyFactorItem(
                    title = "Accident Risk",
                    isPositive = true,
                    description = "Regulated 35 km/h urban speed zone"
                ),
                SafetyFactorItem(
                    title = "Waterlogging",
                    isPositive = true,
                    description = "Surface road avoiding subterranean underpass flooding"
                ),
                SafetyFactorItem(
                    title = "Civic Incidents",
                    isPositive = potholeHazardsCount == 0,
                    description = "1 civic road repair scheduled this week"
                ),
                SafetyFactorItem(
                    title = "Emergency Accessibility",
                    isPositive = true,
                    description = "Local municipal dispensary within 500m"
                )
            )

            TravelPreference.BEST_OVERALL -> listOf(
                SafetyFactorItem(
                    title = "Road Condition",
                    isPositive = true,
                    description = "High-quality asphalt surface on Ring Road Boulevard"
                ),
                SafetyFactorItem(
                    title = "Traffic & Flow",
                    isPositive = true,
                    description = "Synchronized green wave signals ensuring smooth transit"
                ),
                SafetyFactorItem(
                    title = "Accident & Hazard Risk",
                    isPositive = true,
                    description = "Well-lit streetlights and clear median barriers"
                ),
                SafetyFactorItem(
                    title = "Weather & Flood Safety",
                    isPositive = true,
                    description = "Modern storm drainage channels operating at normal capacity"
                ),
                SafetyFactorItem(
                    title = "Civic Reports Sync",
                    isPositive = true,
                    description = "All reported citizen potholes on this section are resolved"
                ),
                SafetyFactorItem(
                    title = "Emergency Proximity",
                    isPositive = true,
                    description = "Trauma & Fire station reachable in under 5 minutes"
                )
            )
        }
    }
}

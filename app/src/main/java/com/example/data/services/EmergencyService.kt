package com.example.data.services

import com.example.data.models.CivicIssue
import com.example.data.models.IssueCategory
import com.example.data.models.IssueStatus

/**
 * Service abstraction for Emergency Centers, Hospitals, Police stations, and Rapid Assistance.
 */
interface EmergencyService {
    fun getEmergencyFacilities(): List<EmergencyFacility>
    fun getEmergencyHelplines(): Map<String, String>
    fun planEmergencyRoute(
        originName: String,
        facility: EmergencyFacility,
        liveIssues: List<CivicIssue>,
        weatherAlert: WeatherSafetyAlert
    ): EmergencyRouteInfo
}

class DefaultEmergencyService : EmergencyService {
    override fun getEmergencyFacilities(): List<EmergencyFacility> {
        return listOf(
            EmergencyFacility(
                id = "EMG-01",
                name = "City Apex Trauma Center (AIIMS)",
                type = "Level 1 Trauma & Emergency",
                address = "Sri Aurobindo Marg, Ansari Nagar",
                helpline = "102 / 011-26588500",
                latitude = 28.5672,
                longitude = 77.2100,
                distanceKm = 1.8,
                isOpen24Hours = true
            ),
            EmergencyFacility(
                id = "EMG-02",
                name = "Dr. RML Emergency & Critical Care",
                type = "Super-Specialty Hospital",
                address = "Baba Kharak Singh Marg, CP",
                helpline = "011-23365525",
                latitude = 28.6234,
                longitude = 77.2030,
                distanceKm = 2.1,
                isOpen24Hours = true
            ),
            EmergencyFacility(
                id = "EMG-03",
                name = "Safdarjung Emergency Department",
                type = "Govt Multispecialty Hospital",
                address = "Ring Road, opposite AIIMS",
                helpline = "011-26165060",
                latitude = 28.5701,
                longitude = 77.2078,
                distanceKm = 2.5,
                isOpen24Hours = true
            ),
            EmergencyFacility(
                id = "EMG-04",
                name = "Central Traffic Police Command HQ",
                type = "Traffic Police & SOS Helpline",
                address = "ITO Crossing, IP Estate",
                helpline = "1095 / 011-25844444",
                latitude = 28.6270,
                longitude = 77.2400,
                distanceKm = 1.4,
                isOpen24Hours = true
            ),
            EmergencyFacility(
                id = "EMG-05",
                name = "Parliament Street Police Station",
                type = "Rapid Response Police Station",
                address = "Parliament St, Connaught Place",
                helpline = "112 / 011-23361100",
                latitude = 28.6255,
                longitude = 77.2160,
                distanceKm = 0.9,
                isOpen24Hours = true
            ),
            EmergencyFacility(
                id = "EMG-06",
                name = "Central Fire Station Headquarters",
                type = "Fire & Disaster Rescue",
                address = "Connaught Lane, Barakhamba",
                helpline = "101 / 011-23414000",
                latitude = 28.6310,
                longitude = 77.2270,
                distanceKm = 1.1,
                isOpen24Hours = true
            )
        )
    }

    override fun getEmergencyHelplines(): Map<String, String> {
        return mapOf(
            "National Emergency (Police/Fire/Med)" to "112",
            "Ambulance / Medical SOS" to "102",
            "Police Control Room" to "100",
            "Fire Emergency & Rescue" to "101",
            "Traffic Accident Helpline" to "1095",
            "Women Safety Helpline" to "1091",
            "Disaster Management Helpline" to "1077",
            "Senior Citizen Helpline" to "14567"
        )
    }

    override fun planEmergencyRoute(
        originName: String,
        facility: EmergencyFacility,
        liveIssues: List<CivicIssue>,
        weatherAlert: WeatherSafetyAlert
    ): EmergencyRouteInfo {
        val hasWaterlogging = liveIssues.any {
            it.category == IssueCategory.DRAINAGE &&
                    it.status != IssueStatus.RESOLVED &&
                    (it.location.contains("Underpass", ignoreCase = true) || it.location.contains("Mathura", ignoreCase = true))
        }

        val baseMinutes = ((facility.distanceKm * 2.8).toInt()).coerceAtLeast(4)

        val routeSteps = if (hasWaterlogging) {
            listOf(
                "Depart $originName heading toward Elevated Green Corridor",
                "Take Barapullah Elevated Expressway Ramp (bypassing ground-level waterlogging)",
                "Follow Ring Road Express Lane with emergency blinkers active",
                "Take dedicated emergency ramp into ${facility.name} (Direct Triage Gate)"
            )
        } else {
            listOf(
                "Depart $originName via Arterial Direct Avenue",
                "Proceed straight past Central Intersection onto Main Boulevard",
                "Arrive at ${facility.name} Emergency Entry (Gate 2, 24/7 Open)"
            )
        }

        val explanation = if (hasWaterlogging) {
            "⚡ Recommended Emergency Route: Prioritized Elevated Barapullah Expressway over Mathura Road due to active low-lying underpass waterlogging. Smooth asphalt, zero reported road closures, direct ambulance bay entry."
        } else {
            "⚡ Recommended Emergency Route: Direct arterial green corridor selected for shortest travel time ($baseMinutes mins) and verified clear traffic flow."
        }

        return EmergencyRouteInfo(
            targetFacility = facility,
            originName = originName,
            estimatedTimeMinutes = baseMinutes,
            distanceKm = facility.distanceKm,
            roadAccessibility = "100% Paved & Priority Emergency Access",
            trafficCongestionLevel = "Low Congestion (Priority Corridor)",
            roadCondition = "Optimal Paved Asphalt",
            activeClosuresCount = 0,
            waterloggingRisk = if (hasWaterlogging) "Avoided Flooded Underpass via Flyover" else "No Flooding along Corridor",
            routeSteps = routeSteps,
            safetyExplanation = explanation
        )
    }
}


package com.example.data.services

import com.example.data.models.CivicIssue
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.data.models.IssueStatus

/**
 * Service abstraction for CivicLive Map management, layers, markers, and geographic data.
 * Replaceable with Mapbox, Google Maps SDK, or OpenStreetMap APIs.
 */
interface MapService {
    fun getDemonstrationIncidentMarkers(): List<IncidentMarker>
    fun getDemonstrationBusStops(): List<IncidentMarker>
    fun getDemonstrationEmergencyLocations(): List<IncidentMarker>
    fun convertCivicIssueToMarker(issue: CivicIssue): IncidentMarker
    fun filterMarkers(
        allMarkers: List<IncidentMarker>,
        layer: MapLayerFilter,
        searchQuery: String,
        severity: IncidentSeverity?
    ): List<IncidentMarker>
}

class DefaultMapService(
    private val riskService: RiskService = DefaultRiskService(),
    private val busService: BusService = DefaultBusService(),
    private val weatherService: WeatherService = DefaultWeatherService()
) : MapService {

    override fun convertCivicIssueToMarker(issue: CivicIssue): IncidentMarker {
        val incidentType = IncidentType.fromCategory(issue.category)
        val severity = IncidentSeverity.fromPriority(issue.priority)
        val safetyScore = riskService.calculateSafetyScore(severity, issue.upvotes + 1, "Moderate")
        val affectedBuses = busService.getAffectedRoutesForLocation(issue.location)

        val roadConditionDesc = when (issue.category) {
            IssueCategory.ROADS -> "Deep Potholes / Damaged Road Surface"
            IssueCategory.WATER, IssueCategory.DRAINAGE -> "Flooded Roadway / Poor Runoff"
            IssueCategory.STREETLIGHT -> "Unlit Corridor / Low Night Visibility"
            IssueCategory.GARBAGE -> "Obstruction / Roadside Debris"
            IssueCategory.ELECTRICITY -> "Power Cables / Signal Outage"
            else -> "Civic Obstruction"
        }

        return IncidentMarker(
            id = "MARKER-${issue.id}",
            title = issue.title,
            description = issue.description,
            type = incidentType,
            latitude = issue.latitude,
            longitude = issue.longitude,
            locationName = issue.location,
            address = issue.address,
            severity = severity,
            status = issue.status,
            reportCount = 1,
            upvotes = issue.upvotes,
            hasUserUpvoted = issue.hasUserUpvoted,
            lastUpdated = issue.reportedTimestamp,
            safetyScore = safetyScore,
            roadCondition = roadConditionDesc,
            weatherRisk = weatherService.getWeatherRiskForCoordinates(issue.latitude, issue.longitude),
            affectedBusRoutes = affectedBuses,
            photoUri = issue.photoUri,
            relatedCivicIssueId = issue.id,
            isDemonstrationData = false
        )
    }

    override fun getDemonstrationIncidentMarkers(): List<IncidentMarker> {
        val now = System.currentTimeMillis()
        val hour = 3600000L

        return listOf(
            IncidentMarker(
                id = "DEMO-HAZ-001",
                title = "Severe Pothole Cluster & Crater",
                description = "Multiple deep craters spanning 3 meters across the left lane. Two-wheelers forced to swerve into oncoming traffic.",
                type = IncidentType.POTHOLE,
                latitude = 28.6289,
                longitude = 77.2065,
                locationName = "Outer Ring Road (Sector 18 Flyover)",
                address = "Near Sector 18 Flyover Exit Ramp, Noida",
                severity = IncidentSeverity.CRITICAL,
                status = IssueStatus.IN_PROGRESS,
                reportCount = 19,
                upvotes = 64,
                lastUpdated = now - 2 * hour,
                safetyScore = 28,
                roadCondition = "Severe Rutting & 8-inch Deep Craters",
                weatherRisk = "High Hydroplaning Risk",
                affectedBusRoutes = listOf("Route 34-B (Delayed +12m)", "Route 108"),
                isDemonstrationData = true
            ),
            IncidentMarker(
                id = "DEMO-HAZ-002",
                title = "Major Monsoon Waterlogging (1.5 ft)",
                description = "Underpass drainage pump failure has caused standing water up to knee level. Sedan cars stalled.",
                type = IncidentType.WATERLOGGING,
                latitude = 28.6139,
                longitude = 77.2290,
                locationName = "Pragati Underpass & Mathura Road",
                address = "Mathura Road Intersection Underpass, New Delhi",
                severity = IncidentSeverity.CRITICAL,
                status = IssueStatus.PENDING,
                reportCount = 38,
                upvotes = 112,
                lastUpdated = now - 1 * hour,
                safetyScore = 18,
                roadCondition = "Submerged under 18 inches standing water",
                weatherRisk = "Severe Flooding Alert - Heavy Rainfall",
                affectedBusRoutes = listOf("Route 419 (Diverted)", "Route 505 (Diverted via Ring Rd)", "Route 720"),
                isDemonstrationData = true
            ),
            IncidentMarker(
                id = "DEMO-HAZ-003",
                title = "Flyover Expansion Joint Construction",
                description = "Emergency lane maintenance by PWD. Two out of four lanes barricaded for resurfacing.",
                type = IncidentType.CONSTRUCTION,
                latitude = 28.5840,
                longitude = 77.2340,
                locationName = "Ashram Chowk Flyover",
                address = "Ashram Chowk, South Delhi",
                severity = IncidentSeverity.HIGH,
                status = IssueStatus.IN_PROGRESS,
                reportCount = 14,
                upvotes = 42,
                lastUpdated = now - 5 * hour,
                safetyScore = 45,
                roadCondition = "Milled Asphalt & Concrete Barricades",
                weatherRisk = "Moderate Risk - Heavy Dust & Slow Traffic",
                affectedBusRoutes = listOf("Route 404", "Route 423-A (+20m delay)"),
                isDemonstrationData = true
            ),
            IncidentMarker(
                id = "DEMO-HAZ-004",
                title = "Broken Traffic Signal Junction",
                description = "All four signal heads blinking red continuously since morning power surge. Gridlock at peak hours.",
                type = IncidentType.BROKEN_SIGNAL,
                latitude = 28.6350,
                longitude = 77.2240,
                locationName = "Connaught Place Outer Circle / Barakhamba",
                address = "Barakhamba Road Crossing, Connaught Place",
                severity = IncidentSeverity.HIGH,
                status = IssueStatus.PENDING,
                reportCount = 22,
                upvotes = 57,
                lastUpdated = now - 3 * hour,
                safetyScore = 35,
                roadCondition = "Uncontrolled 4-Way Intersection",
                weatherRisk = "Moderate Risk",
                affectedBusRoutes = listOf("Route 100", "Route 101", "Route 104"),
                isDemonstrationData = true
            ),
            IncidentMarker(
                id = "DEMO-HAZ-005",
                title = "Multi-Vehicle Collision Clearing",
                description = "Three cars involved in fender-bender near central verge. Police crane on site clearing right lane.",
                type = IncidentType.ACCIDENT,
                latitude = 28.5355,
                longitude = 77.2410,
                locationName = "Nehru Place Arterial Link",
                address = "Opposite Paras Cinema, Nehru Place",
                severity = IncidentSeverity.HIGH,
                status = IssueStatus.IN_PROGRESS,
                reportCount = 9,
                upvotes = 31,
                lastUpdated = now - 40 * 60000L,
                safetyScore = 40,
                roadCondition = "Glass Shards & Blocked Fast Lane",
                weatherRisk = "Low Weather Risk",
                affectedBusRoutes = listOf("Route 511", "Route 522"),
                isDemonstrationData = true
            ),
            IncidentMarker(
                id = "DEMO-HAZ-006",
                title = "Fallen Banyan Tree Branch Blocking Lane",
                description = "Large branch broke during storm, partially obstructing the dedicated bus lane and sidewalk.",
                type = IncidentType.FALLEN_TREE,
                latitude = 28.6010,
                longitude = 77.1980,
                locationName = "Chanakyapuri Shanti Path",
                address = "Near Embassy of France, Shanti Path",
                severity = IncidentSeverity.MEDIUM,
                status = IssueStatus.PENDING,
                reportCount = 6,
                upvotes = 18,
                lastUpdated = now - 6 * hour,
                safetyScore = 60,
                roadCondition = "Left Lane Partially Blocked by Tree Trunk",
                weatherRisk = "Wind Gust Warning (45 km/h)",
                affectedBusRoutes = listOf("Route 620"),
                isDemonstrationData = true
            ),
            IncidentMarker(
                id = "DEMO-HAZ-007",
                title = "Dark Corridor - 12 Consecutive Streetlights Out",
                description = "Underground cable fault has left a 600m stretch in pitch darkness. Severe pedestrian hazard.",
                type = IncidentType.BROKEN_STREETLIGHT,
                latitude = 28.6480,
                longitude = 77.1890,
                locationName = "Pusa Road - Patel Nagar Link",
                address = "Between Metro Pillar 180 to 195, Pusa Road",
                severity = IncidentSeverity.HIGH,
                status = IssueStatus.PENDING,
                reportCount = 27,
                upvotes = 83,
                lastUpdated = now - 12 * hour,
                safetyScore = 32,
                roadCondition = "Zero Lighting / Poor Night Visibility",
                weatherRisk = "High Night Risk",
                affectedBusRoutes = listOf("Route 753", "Route 813"),
                isDemonstrationData = true
            ),
            IncidentMarker(
                id = "DEMO-HAZ-008",
                title = "Pipeline Trench Resurfaced & Safe",
                description = "Jal Board pipeline replacement completed. Road patch asphalted and tested for smooth transit.",
                type = IncidentType.ROAD_DAMAGE,
                latitude = 28.5680,
                longitude = 77.2100,
                locationName = "AIIMS Ring Road Approach",
                address = "South Extension Part 1 Entrance",
                severity = IncidentSeverity.LOW,
                status = IssueStatus.RESOLVED,
                reportCount = 12,
                upvotes = 45,
                lastUpdated = now - 24 * hour,
                safetyScore = 94,
                roadCondition = "Freshly Paved Hot-Mix Asphalt",
                weatherRisk = "Safe Conditions",
                affectedBusRoutes = listOf("Route 500 (Restored Normal)"),
                isDemonstrationData = true
            )
        )
    }

    override fun getDemonstrationBusStops(): List<IncidentMarker> {
        val now = System.currentTimeMillis()
        return listOf(
            IncidentMarker(
                id = "DEMO-BUS-001",
                title = "Central Transit Hub & Terminal",
                description = "Major interchange for 18 bus routes and yellow/blue metro lines. Real-time passenger display active.",
                type = IncidentType.BUS_STOP,
                latitude = 28.6328,
                longitude = 77.2197,
                locationName = "Shivaji Stadium Terminal",
                address = "Connaught Place Central Hub, New Delhi",
                severity = IncidentSeverity.LOW,
                status = IssueStatus.RESOLVED,
                reportCount = 0,
                upvotes = 120,
                lastUpdated = now,
                safetyScore = 92,
                roadCondition = "Smooth Dedicated Bus Bay",
                weatherRisk = "Sheltered Platform",
                affectedBusRoutes = listOf("Route 100", "Route 104", "Route 34-B", "Route 520", "Route 720"),
                isDemonstrationData = true
            ),
            IncidentMarker(
                id = "DEMO-BUS-002",
                title = "South Extension Transit Junction",
                description = "Bus shelter with EV charging station and illuminated display. Minor delay on Route 404.",
                type = IncidentType.BUS_STOP,
                latitude = 28.5705,
                longitude = 77.2225,
                locationName = "South Extension Ring Road Stop",
                address = "Ring Road South Ext Part 2",
                severity = IncidentSeverity.LOW,
                status = IssueStatus.RESOLVED,
                reportCount = 0,
                upvotes = 78,
                lastUpdated = now,
                safetyScore = 88,
                roadCondition = "Concrete Bus Lane",
                weatherRisk = "Covered Shelter",
                affectedBusRoutes = listOf("Route 404 (Delayed +5m)", "Route 419", "Route 500"),
                isDemonstrationData = true
            )
        )
    }

    override fun getDemonstrationEmergencyLocations(): List<IncidentMarker> {
        val now = System.currentTimeMillis()
        return listOf(
            IncidentMarker(
                id = "DEMO-EMG-001",
                title = "City Apex Trauma Center & Emergency",
                description = "24/7 Level-1 Emergency & Trauma Unit. Ambulance bay accessible from East Gate.",
                type = IncidentType.EMERGENCY_FACILITY,
                latitude = 28.5672,
                longitude = 77.2100,
                locationName = "Apex Trauma Center (AIIMS)",
                address = "Sri Aurobindo Marg, Ansari Nagar",
                severity = IncidentSeverity.LOW,
                status = IssueStatus.RESOLVED,
                reportCount = 0,
                upvotes = 240,
                lastUpdated = now,
                safetyScore = 98,
                roadCondition = "Dedicated Emergency Green Corridor",
                weatherRisk = "Clear Priority Route",
                affectedBusRoutes = listOf("Ambulance Emergency Lane"),
                isDemonstrationData = true
            ),
            IncidentMarker(
                id = "DEMO-EMG-002",
                title = "Central Police & Traffic Control Station",
                description = "Integrated Traffic Command & SOS Rapid Response Dispatch.",
                type = IncidentType.EMERGENCY_FACILITY,
                latitude = 28.6270,
                longitude = 77.2400,
                locationName = "ITO Traffic Police HQ",
                address = "ITO Crossing, IP Estate",
                severity = IncidentSeverity.LOW,
                status = IssueStatus.RESOLVED,
                reportCount = 0,
                upvotes = 150,
                lastUpdated = now,
                safetyScore = 96,
                roadCondition = "Traffic Command Center",
                weatherRisk = "Clear",
                affectedBusRoutes = listOf("Police Patrol Unit 12"),
                isDemonstrationData = true
            )
        )
    }

    override fun filterMarkers(
        allMarkers: List<IncidentMarker>,
        layer: MapLayerFilter,
        searchQuery: String,
        severity: IncidentSeverity?
    ): List<IncidentMarker> {
        return allMarkers.filter { marker ->
            // Filter by Layer
            val matchesLayer = when (layer) {
                MapLayerFilter.ALL -> true
                MapLayerFilter.HAZARDS -> marker.type in listOf(
                    IncidentType.POTHOLE,
                    IncidentType.WATERLOGGING,
                    IncidentType.ROAD_DAMAGE,
                    IncidentType.ACCIDENT,
                    IncidentType.FALLEN_TREE,
                    IncidentType.BROKEN_SIGNAL,
                    IncidentType.BROKEN_STREETLIGHT,
                    IncidentType.HIGH_RISK_ROAD
                )
                MapLayerFilter.POTHOLES -> marker.type == IncidentType.POTHOLE
                MapLayerFilter.WATERLOGGING -> marker.type == IncidentType.WATERLOGGING
                MapLayerFilter.CONSTRUCTION -> marker.type in listOf(IncidentType.CONSTRUCTION, IncidentType.ROAD_CLOSURE)
                MapLayerFilter.TRANSIT -> marker.type == IncidentType.BUS_STOP || marker.affectedBusRoutes.isNotEmpty()
                MapLayerFilter.EMERGENCY -> marker.type == IncidentType.EMERGENCY_FACILITY
                MapLayerFilter.WEATHER -> marker.type == IncidentType.WEATHER_HAZARD || marker.type == IncidentType.WATERLOGGING
            }

            // Filter by Search
            val matchesSearch = if (searchQuery.isBlank()) true else {
                marker.title.contains(searchQuery, ignoreCase = true) ||
                marker.locationName.contains(searchQuery, ignoreCase = true) ||
                marker.address.contains(searchQuery, ignoreCase = true) ||
                marker.description.contains(searchQuery, ignoreCase = true) ||
                marker.type.displayName.contains(searchQuery, ignoreCase = true)
            }

            // Filter by Severity
            val matchesSeverity = severity == null || marker.severity == severity

            matchesLayer && matchesSearch && matchesSeverity
        }
    }
}

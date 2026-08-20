package com.example

import com.example.data.models.CivicIssue
import com.example.data.models.Department
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.data.models.IssueStatus
import com.example.data.services.DefaultBusService
import com.example.data.services.DefaultRiskService
import com.example.data.services.DefaultRouteService
import com.example.data.services.DefaultWeatherService
import com.example.data.services.TravelPreference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SafeJourneyPlannerUnitTest {

    private lateinit var busService: DefaultBusService
    private lateinit var weatherService: DefaultWeatherService
    private lateinit var riskService: DefaultRiskService
    private lateinit var routeService: DefaultRouteService

    @Before
    fun setup() {
        busService = DefaultBusService()
        weatherService = DefaultWeatherService()
        riskService = DefaultRiskService()
        routeService = DefaultRouteService()
    }

    @Test
    fun `test journey planner returns 4 multimodal route alternatives`() {
        val issues = emptyList<CivicIssue>()
        val weatherAlert = weatherService.getCurrentWeatherAlert()
        val busRoutes = busService.getActiveBusRoutes()

        val routes = routeService.planSafeJourney(
            origin = "Sector 62 IT Hub",
            destination = "Connaught Place",
            dateTimeText = "Now (14:30)",
            preference = TravelPreference.BEST_OVERALL,
            liveCivicIssues = issues,
            weatherAlert = weatherAlert,
            busRoutes = busRoutes
        )

        assertEquals(4, routes.size)
        assertTrue(routes.any { it.primaryTag == TravelPreference.FASTEST })
        assertTrue(routes.any { it.primaryTag == TravelPreference.CHEAPEST })
        assertTrue(routes.any { it.primaryTag == TravelPreference.SAFEST })
        assertTrue(routes.any { it.primaryTag == TravelPreference.BEST_OVERALL })
    }

    @Test
    fun `test route safety score is high for elevated safe corridor`() {
        val issues = emptyList<CivicIssue>()
        val weatherAlert = weatherService.getCurrentWeatherAlert()
        val busRoutes = busService.getActiveBusRoutes()

        val routes = routeService.planSafeJourney(
            origin = "Sector 62",
            destination = "Central Hub",
            dateTimeText = "Now (14:30)",
            preference = TravelPreference.SAFEST,
            liveCivicIssues = issues,
            weatherAlert = weatherAlert,
            busRoutes = busRoutes
        )

        val safestRoute = routes.first { it.primaryTag == TravelPreference.SAFEST }
        assertTrue("Safest route score should be >= 90", safestRoute.safetyScore >= 90)
        assertTrue("Safest route should have safety factors", safestRoute.safetyFactors.isNotEmpty())
        assertTrue("Safest route should have legs", safestRoute.legs.isNotEmpty())
    }

    @Test
    fun `test civic issues on corridor lower route safety score and add hazard warnings`() {
        val waterloggingIssue = CivicIssue(
            id = "ISSUE-WATER-99",
            title = "Severe Waterlogging at Mathura Road Underpass",
            description = "Flooded underpass with standing water 2.5 feet deep. Traffic stopped.",
            category = IssueCategory.DRAINAGE,
            location = "Mathura Road Underpass",
            address = "Mathura Road",
            status = IssueStatus.PENDING,
            priority = IssuePriority.HIGH,
            assignedDepartment = Department.DRAINAGE_FLOOD,
            reportedByName = "Commuter",
            reportedByEmail = "commuter@test.com",
            reportedTimestamp = System.currentTimeMillis()
        )

        val weatherAlert = weatherService.getCurrentWeatherAlert()
        val busRoutes = busService.getActiveBusRoutes()

        val routes = routeService.planSafeJourney(
            origin = "Sector 62",
            destination = "Central Hub",
            dateTimeText = "Now (14:30)",
            preference = TravelPreference.FASTEST,
            liveCivicIssues = listOf(waterloggingIssue),
            weatherAlert = weatherAlert,
            busRoutes = busRoutes
        )

        val fastestRoute = routes.first { it.primaryTag == TravelPreference.FASTEST }
        assertTrue("Fastest route should have disruption or hazard warning due to underpass waterlogging", fastestRoute.hasDisruption || fastestRoute.civicHazards.isNotEmpty())
    }

    @Test
    fun `test bus service returns valid bus lines and routes`() {
        val routes = busService.getActiveBusRoutes()
        assertTrue(routes.isNotEmpty())
        assertTrue(routes.any { it.routeNumber.contains("104") })
    }

    @Test
    fun `test weather service provides rainfall and waterlogging risk`() {
        val alert = weatherService.getCurrentWeatherAlert()
        assertNotNull(alert)
        assertTrue(alert.precipitationMm > 0)
        assertTrue(alert.waterloggingRisk.isNotBlank())
    }
}

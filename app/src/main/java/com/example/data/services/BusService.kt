package com.example.data.services

/**
 * Service abstraction for Public Transit & Bus network monitoring, disruptions, and affected routes.
 */
interface BusService {
    fun getActiveBusRoutes(): List<BusRouteInfo>
    fun getAffectedRoutesForLocation(location: String): List<String>
}

class DefaultBusService : BusService {
    override fun getActiveBusRoutes(): List<BusRouteInfo> {
        return listOf(
            BusRouteInfo(
                routeNumber = "Route 104",
                routeName = "Central Terminal ⇄ Electronic City",
                origin = "Shivaji Stadium",
                destination = "Sector 62 IT Park",
                operationalStatus = "On-Time",
                frequencyMinutes = 10,
                activeHazardsOnRoute = 1,
                stopsCount = 24,
                isDelayed = false
            ),
            BusRouteInfo(
                routeNumber = "Route 215-A",
                routeName = "Railway Station ⇄ South Extension",
                origin = "New Delhi Rly Stn",
                destination = "South Ext Terminal",
                operationalStatus = "Diverted via Ring Rd",
                frequencyMinutes = 15,
                activeHazardsOnRoute = 2,
                stopsCount = 18,
                isDelayed = true
            ),
            BusRouteInfo(
                routeNumber = "Route 419",
                routeName = "Interstate Bus Terminal ⇄ Nehru Place",
                origin = "Kashmere Gate",
                destination = "Nehru Place Bus Hub",
                operationalStatus = "Delayed (+18m)",
                frequencyMinutes = 12,
                activeHazardsOnRoute = 3,
                stopsCount = 22,
                isDelayed = true
            ),
            BusRouteInfo(
                routeNumber = "Route 520",
                routeName = "Airport Express Feeder ⇄ Civil Lines",
                origin = "T3 Terminal",
                destination = "Civil Lines Hub",
                operationalStatus = "On-Time",
                frequencyMinutes = 8,
                activeHazardsOnRoute = 0,
                stopsCount = 16,
                isDelayed = false
            )
        )
    }

    override fun getAffectedRoutesForLocation(location: String): List<String> {
        return when {
            location.contains("Sector", ignoreCase = true) -> listOf("Route 104", "Route 34-B")
            location.contains("Civil", ignoreCase = true) -> listOf("Route 520", "Route 112")
            location.contains("Mathura", ignoreCase = true) || location.contains("Underpass", ignoreCase = true) -> listOf("Route 419 (Diverted)", "Route 505")
            location.contains("Ring Road", ignoreCase = true) -> listOf("Route 215-A", "Route 404 (+10m delay)")
            else -> listOf("Route 104 (Minor Slowdown)")
        }
    }
}

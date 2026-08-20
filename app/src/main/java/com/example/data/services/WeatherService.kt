package com.example.data.services

/**
 * Service abstraction for meteorological road hazard alerts and monsoon risk tracking.
 */
interface WeatherService {
    fun getCurrentWeatherAlert(): WeatherSafetyAlert
    fun getWeatherRiskForCoordinates(lat: Double, lng: Double): String
}

class DefaultWeatherService : WeatherService {
    override fun getCurrentWeatherAlert(): WeatherSafetyAlert {
        return WeatherSafetyAlert(
            headline = "Monsoon Rain Advisory: Waterlogging in Low-Lying Underpasses",
            precipitationMm = 28.5,
            windSpeedKmh = 32.0,
            temperatureCelsius = 27.5,
            waterloggingRisk = "High Risk",
            advisoryText = "Heavy isolated showers expected. Avoid unpaved road shoulders and subway underpasses.",
            affectedZones = listOf("Central Arterial", "Sector 62 Lowland", "Mathura Corridor")
        )
    }

    override fun getWeatherRiskForCoordinates(lat: Double, lng: Double): String {
        return "Moderate Rain Risk • Waterlogging Potential 65%"
    }
}

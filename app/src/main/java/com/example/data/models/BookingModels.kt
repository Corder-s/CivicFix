package com.example.data.models

import java.util.UUID

enum class BookingStatus(val displayName: String) {
    CONFIRMED("Confirmed"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

data class PassengerInfo(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val seatNumber: String,
    val age: Int = 28,
    val gender: String = "Adult"
)

data class BusTripOption(
    val busNumber: String,
    val routeCode: String,
    val operatorName: String,
    val busType: String, // e.g. "AC Electric Low-Floor", "CNG Express", "Volvo Corridor"
    val departureTime: String,
    val arrivalTime: String,
    val durationText: String,
    val originStop: String,
    val destinationStop: String,
    val baseFarePerPassenger: Double,
    val safetyRating: Int, // 1 to 100
    val availableSeats: Int,
    val isWaterloggingSafe: Boolean = true,
    val liveFrequencyMins: Int = 12
)

data class JourneyBooking(
    val id: String = "CF-MOB-${(1000..9999).random()}",
    val routeId: String,
    val origin: String,
    val destination: String,
    val busNumber: String,
    val busType: String,
    val travelDate: String,
    val departureTime: String,
    val arrivalTime: String,
    val passengerCount: Int,
    val passengers: List<PassengerInfo>,
    val seatNumbers: List<String>,
    val pickupPoint: String,
    val dropPoint: String,
    val baseFare: Double,
    val safetySurcharge: Double = 5.0,
    val taxGst: Double = 3.5,
    val concessionDiscount: Double = 0.0,
    val totalFare: Double,
    val status: BookingStatus = BookingStatus.CONFIRMED,
    val bookingTimestamp: Long = System.currentTimeMillis(),
    val qrCodeData: String = "CIVICFIX-TICKET-${UUID.randomUUID().toString().take(8).uppercase()}",
    val isSimulation: Boolean = true
)

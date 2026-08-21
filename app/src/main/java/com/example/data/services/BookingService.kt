package com.example.data.services

import com.example.data.models.BookingStatus
import com.example.data.models.BusTripOption
import com.example.data.models.JourneyBooking
import com.example.data.models.PassengerInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

interface BookingService {
    val bookings: StateFlow<List<JourneyBooking>>
    fun getAvailableBusTrips(origin: String, destination: String): List<BusTripOption>
    fun createBooking(
        routeId: String,
        origin: String,
        destination: String,
        busOption: BusTripOption,
        travelDate: String,
        departureTime: String,
        passengers: List<String>,
        selectedSeats: List<String>,
        pickupPoint: String,
        dropPoint: String
    ): JourneyBooking
    fun getBookingById(bookingId: String): JourneyBooking?
    fun cancelBooking(bookingId: String): Boolean
}

class DefaultBookingService : BookingService {

    private val initialSampleBookings = listOf(
        JourneyBooking(
            id = "CF-MOB-8942",
            routeId = "route_safest",
            origin = "Sector 62 IT Hub",
            destination = "Connaught Place / City Center",
            busNumber = "BUS-104 Express",
            busType = "Electric AC Low-Floor (Elevated Corridor)",
            travelDate = "Today",
            departureTime = "08:15 AM",
            arrivalTime = "08:52 AM",
            passengerCount = 2,
            passengers = listOf(
                PassengerInfo(name = "Rahul Sharma", seatNumber = "12A"),
                PassengerInfo(name = "Anjali Sharma", seatNumber = "12B")
            ),
            seatNumbers = listOf("12A", "12B"),
            pickupPoint = "Sector 62 Main Gate Bus Bay 3",
            dropPoint = "Super Bazar, Connaught Place Inner Circle",
            baseFare = 40.0,
            safetySurcharge = 5.0,
            taxGst = 3.5,
            concessionDiscount = 0.0,
            totalFare = 48.5,
            status = BookingStatus.CONFIRMED,
            bookingTimestamp = System.currentTimeMillis() - 3600_000 * 2,
            qrCodeData = "CIVICFIX-TICKET-CF8942-VERIFIED"
        )
    )

    private val _bookings = MutableStateFlow<List<JourneyBooking>>(initialSampleBookings)
    override val bookings: StateFlow<List<JourneyBooking>> = _bookings.asStateFlow()

    override fun getAvailableBusTrips(origin: String, destination: String): List<BusTripOption> {
        return listOf(
            BusTripOption(
                busNumber = "BUS-104 Express",
                routeCode = "104E",
                operatorName = "Delhi Integrated Multimodal Transit",
                busType = "Electric AC Low-Floor",
                departureTime = "08:15 AM",
                arrivalTime = "08:52 AM",
                durationText = "37 mins",
                originStop = origin.ifEmpty { "Sector 62 IT Hub" },
                destinationStop = destination.ifEmpty { "Connaught Place" },
                baseFarePerPassenger = 25.0,
                safetyRating = 94,
                availableSeats = 18,
                isWaterloggingSafe = true,
                liveFrequencyMins = 8
            ),
            BusTripOption(
                busNumber = "BUS-52 Blue Line",
                routeCode = "52BL",
                operatorName = "State City Transport Corp",
                busType = "CNG Semi-Low Floor",
                departureTime = "08:25 AM",
                arrivalTime = "09:05 AM",
                durationText = "40 mins",
                originStop = origin.ifEmpty { "Sector 62 IT Hub" },
                destinationStop = destination.ifEmpty { "Connaught Place" },
                baseFarePerPassenger = 18.0,
                safetyRating = 88,
                availableSeats = 26,
                isWaterloggingSafe = true,
                liveFrequencyMins = 12
            ),
            BusTripOption(
                busNumber = "BUS-118 AC Corridor",
                routeCode = "118AC",
                operatorName = "Metro Feeder Express",
                busType = "Premium Electric AC",
                departureTime = "08:35 AM",
                arrivalTime = "09:12 AM",
                durationText = "37 mins",
                originStop = origin.ifEmpty { "Sector 62 IT Hub" },
                destinationStop = destination.ifEmpty { "Connaught Place" },
                baseFarePerPassenger = 30.0,
                safetyRating = 96,
                availableSeats = 14,
                isWaterloggingSafe = true,
                liveFrequencyMins = 10
            ),
            BusTripOption(
                busNumber = "BUS-709 Rapid Transit",
                routeCode = "709RT",
                operatorName = "Urban Smart Transit",
                busType = "EV Clean Mobility",
                departureTime = "08:50 AM",
                arrivalTime = "09:32 AM",
                durationText = "42 mins",
                originStop = origin.ifEmpty { "Sector 62 IT Hub" },
                destinationStop = destination.ifEmpty { "Connaught Place" },
                baseFarePerPassenger = 22.0,
                safetyRating = 91,
                availableSeats = 31,
                isWaterloggingSafe = true,
                liveFrequencyMins = 15
            )
        )
    }

    override fun createBooking(
        routeId: String,
        origin: String,
        destination: String,
        busOption: BusTripOption,
        travelDate: String,
        departureTime: String,
        passengers: List<String>,
        selectedSeats: List<String>,
        pickupPoint: String,
        dropPoint: String
    ): JourneyBooking {
        val count = passengers.size.coerceAtLeast(1)
        val baseFare = busOption.baseFarePerPassenger * count
        val safetySurcharge = 5.0
        val taxGst = (baseFare * 0.05).coerceAtLeast(2.0)
        val totalFare = baseFare + safetySurcharge + taxGst

        val passengerList = passengers.mapIndexed { index, name ->
            val seat = selectedSeats.getOrNull(index) ?: "${(10..24).random()}${"ABCD".random()}"
            PassengerInfo(name = name.ifBlank { "Passenger ${index + 1}" }, seatNumber = seat)
        }

        val booking = JourneyBooking(
            id = "CF-MOB-${(1000..9999).random()}",
            routeId = routeId,
            origin = origin,
            destination = destination,
            busNumber = busOption.busNumber,
            busType = busOption.busType,
            travelDate = travelDate,
            departureTime = departureTime.ifBlank { busOption.departureTime },
            arrivalTime = busOption.arrivalTime,
            passengerCount = count,
            passengers = passengerList,
            seatNumbers = passengerList.map { it.seatNumber },
            pickupPoint = pickupPoint.ifBlank { "${origin} Bus Bay" },
            dropPoint = dropPoint.ifBlank { "${destination} Terminal" },
            baseFare = baseFare,
            safetySurcharge = safetySurcharge,
            taxGst = taxGst,
            concessionDiscount = 0.0,
            totalFare = totalFare,
            status = BookingStatus.CONFIRMED,
            bookingTimestamp = System.currentTimeMillis()
        )

        _bookings.update { listOf(booking) + it }
        return booking
    }

    override fun getBookingById(bookingId: String): JourneyBooking? {
        return _bookings.value.firstOrNull { it.id == bookingId }
    }

    override fun cancelBooking(bookingId: String): Boolean {
        var found = false
        _bookings.update { list ->
            list.map { b ->
                if (b.id == bookingId) {
                    found = true
                    b.copy(status = BookingStatus.CANCELLED)
                } else b
            }
        }
        return found
    }
}

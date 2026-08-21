package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.CivicStrings
import com.example.data.localization.civicString
import com.example.data.models.BusTripOption
import com.example.data.models.JourneyBooking
import com.example.ui.CivicFixViewModel
import com.example.ui.components.CivicButton
import com.example.ui.components.CivicButtonSize
import com.example.ui.components.CivicButtonVariant
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenLight
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyLight
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicOrangeContainer
import com.example.ui.theme.CivicOrangeDark
import com.example.ui.theme.CivicOrangeLight
import com.example.ui.theme.CivicOrangePrimary
import com.example.ui.theme.CivicRed
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate700
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900

@Composable
fun BookJourneyScreen(
    viewModel: CivicFixViewModel,
    initialOrigin: String = "Sector 62 IT Hub",
    initialDestination: String = "Connaught Place / City Center",
    onBookingConfirmed: (String) -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var step by remember { mutableIntStateOf(1) } // 1: Select Bus, 2: Slot & Date, 3: Passengers & Seats, 4: Review, 5: Confirmed Ticket

    val busOptions = remember { viewModel.bookingService.getAvailableBusTrips(initialOrigin, initialDestination) }
    var selectedBus by remember { mutableStateOf(busOptions.firstOrNull() ?: busOptions[0]) }

    var selectedDate by remember { mutableStateOf("Today, 21 Aug") }
    var selectedSlot by remember { mutableStateOf("08:15 AM (Morning Express)") }

    var passengerCount by remember { mutableIntStateOf(1) }
    val passengerNames = remember { mutableStateListOf("Rahul Sharma") }
    val selectedSeats = remember { mutableStateListOf("12A") }

    var confirmedBooking by remember { mutableStateOf<JourneyBooking?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicSlate100)
    ) {
        // Header Bar
        Surface(
            color = CivicNavyDark,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (step > 1 && step < 5) step--
                        else onBack()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (step == 5) "Booking Confirmed" else "Book Safe Bus Journey",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = if (step == 5) "Digital Pass # ${confirmedBooking?.id ?: "CF-MOB"}" else "Step $step of 4 • Safe Multimodal Transit",
                        fontSize = 11.sp,
                        color = CivicOrangeLight
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CivicOrangePrimary.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, CivicOrangePrimary.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = CivicOrangeLight,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SAFETY VERIFIED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CivicOrangeLight
                        )
                    }
                }
            }
        }

        // Stepper Progress Indicator (Steps 1 to 4)
        if (step < 5) {
            Surface(color = Color.White, shadowElevation = 1.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BookingStepPill(number = "1", label = "Bus", isActive = step == 1, isCompleted = step > 1)
                    HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 4.dp), color = CivicSlate200)
                    BookingStepPill(number = "2", label = "Date/Time", isActive = step == 2, isCompleted = step > 2)
                    HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 4.dp), color = CivicSlate200)
                    BookingStepPill(number = "3", label = "Seats", isActive = step == 3, isCompleted = step > 3)
                    HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 4.dp), color = CivicSlate200)
                    BookingStepPill(number = "4", label = "Review", isActive = step == 4, isCompleted = step > 4)
                }
            }
        }

        // Scrollable Body Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            // STEP 1: SELECT BUS
            if (step == 1) {
                Text(
                    text = "SELECT YOUR SAFE BUS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CivicSlate800,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Live scheduled buses with high safety ratings & flood-bypassing routes",
                    fontSize = 11.sp,
                    color = CivicSlate600,
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )

                busOptions.forEach { bus ->
                    val isSelected = bus.busNumber == selectedBus.busNumber
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) CivicOrangeContainer else Color.White),
                        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) CivicOrangePrimary else CivicSlate200),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clickable { selectedBus = bus }
                            .testTag("bus_option_${bus.busNumber}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) CivicOrangePrimary else CivicNavyDark,
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.DirectionsBus,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = bus.busNumber,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            color = CivicSlate900
                                        )
                                        Text(
                                            text = bus.busType,
                                            fontSize = 11.sp,
                                            color = CivicSlate600
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CivicGreenPrimary.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = CivicGreenDark,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${bus.safetyRating}/100 Safe",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 10.5.sp,
                                            color = CivicGreenDark
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Departure / Duration", fontSize = 10.sp, color = CivicSlate400)
                                    Text(
                                        text = "${bus.departureTime} • ${bus.durationText}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CivicSlate800
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "Fare per Seat", fontSize = 10.sp, color = CivicSlate400)
                                    Text(
                                        text = "₹${bus.baseFarePerPassenger.toInt()}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = CivicOrangeDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🟢 ${bus.availableSeats} seats available • Every ${bus.liveFrequencyMins} mins",
                                    fontSize = 11.sp,
                                    color = CivicGreenDark,
                                    fontWeight = FontWeight.Medium
                                )

                                if (isSelected) {
                                    Text(
                                        text = "SELECTED ✓",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CivicOrangeDark
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CivicButton(
                    text = "CONTINUE TO DATE & TIME",
                    onClick = { step = 2 },
                    variant = CivicButtonVariant.PRIMARY_ORANGE,
                    size = CivicButtonSize.LARGE,
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "book_step_1_continue"
                )
            }

            // STEP 2: DATE & TIME SLOTS
            if (step == 2) {
                Text(
                    text = "SELECT DATE & DEPARTURE TIME",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CivicSlate800,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Pick the most convenient travel schedule",
                    fontSize = 11.sp,
                    color = CivicSlate600,
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )

                // Date Pills
                Text(text = "Travel Date", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicSlate700)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Today, 21 Aug", "Tomorrow, 22 Aug", "Sat, 23 Aug").forEach { dateText ->
                        val isDateSelected = selectedDate == dateText
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDateSelected) CivicOrangePrimary else Color.White,
                            border = BorderStroke(1.dp, if (isDateSelected) CivicOrangePrimary else CivicSlate200),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedDate = dateText }
                        ) {
                            Text(
                                text = dateText,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDateSelected) Color.White else CivicSlate800,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Departure Slots", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicSlate700)
                Spacer(modifier = Modifier.height(8.dp))

                val slots = listOf(
                    "08:15 AM (Morning Express)",
                    "08:45 AM (Fast Arterial)",
                    "09:15 AM (Peak Safe Window)",
                    "10:00 AM (Post-Peak Transit)",
                    "05:30 PM (Evening Return Corridor)"
                )

                slots.forEach { slot ->
                    val isSlotSelected = selectedSlot == slot
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSlotSelected) CivicOrangeContainer else Color.White),
                        border = BorderStroke(1.dp, if (isSlotSelected) CivicOrangePrimary else CivicSlate200),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { selectedSlot = slot }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = if (isSlotSelected) CivicOrangePrimary else CivicSlate600,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = slot,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSlotSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = CivicSlate900
                                )
                            }

                            if (isSlotSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = CivicOrangePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { step = 1 },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back", color = CivicSlate800)
                    }

                    CivicButton(
                        text = "CONTINUE TO SEATS",
                        onClick = { step = 3 },
                        variant = CivicButtonVariant.PRIMARY_ORANGE,
                        size = CivicButtonSize.LARGE,
                        modifier = Modifier.weight(2f),
                        testTag = "book_step_2_continue"
                    )
                }
            }

            // STEP 3: PASSENGERS & SEAT SELECTION
            if (step == 3) {
                Text(
                    text = "PASSENGERS & SEAT SELECTION",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CivicSlate800,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Choose number of commuters & seat preferences",
                    fontSize = 11.sp,
                    color = CivicSlate600,
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )

                // Passenger Counter
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CivicSlate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Number of Passengers", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CivicSlate900)
                            Text(text = "Max 6 per booking ticket", fontSize = 11.sp, color = CivicSlate400)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = CivicSlate100,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clickable {
                                        if (passengerCount > 1) {
                                            passengerCount--
                                            if (passengerNames.size > passengerCount) passengerNames.removeAt(passengerNames.lastIndex)
                                            if (selectedSeats.size > passengerCount) selectedSeats.removeAt(selectedSeats.lastIndex)
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = CivicSlate800, modifier = Modifier.size(16.dp))
                                }
                            }

                            Text(
                                text = "$passengerCount",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = CivicSlate900
                            )

                            Surface(
                                shape = CircleShape,
                                color = CivicOrangePrimary,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clickable {
                                        if (passengerCount < 6) {
                                            passengerCount++
                                            passengerNames.add("Passenger $passengerCount")
                                            selectedSeats.add("${(10..24).random()}${"ABCD".random()}")
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Passenger Names
                Text(text = "Passenger Names", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicSlate700)
                Spacer(modifier = Modifier.height(8.dp))

                for (i in 0 until passengerCount) {
                    val currentName = passengerNames.getOrNull(i) ?: "Passenger ${i + 1}"
                    OutlinedTextField(
                        value = currentName,
                        onValueChange = { newName ->
                            if (i < passengerNames.size) {
                                passengerNames[i] = newName
                            } else {
                                passengerNames.add(newName)
                            }
                        },
                        label = { Text("Passenger ${i + 1} Name", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CivicOrangePrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CivicOrangePrimary,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Seat Layout Preview
                Text(text = "Selected Seats", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicSlate700)
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CivicNavyDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🚍 Bus Layout (2x2 Aisle)", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(text = "${selectedBus.busNumber}", fontSize = 11.sp, color = CivicOrangeLight)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedSeats.forEachIndexed { index, seat ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CivicOrangePrimary,
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Text(
                                        text = "Seat $seat (${passengerNames.getOrNull(index)?.take(8) ?: "P${index+1}"})",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { step = 2 },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back", color = CivicSlate800)
                    }

                    CivicButton(
                        text = "REVIEW JOURNEY",
                        onClick = { step = 4 },
                        variant = CivicButtonVariant.PRIMARY_ORANGE,
                        size = CivicButtonSize.LARGE,
                        modifier = Modifier.weight(2f),
                        testTag = "book_step_3_continue"
                    )
                }
            }

            // STEP 4: REVIEW & FARE BREAKDOWN
            if (step == 4) {
                Text(
                    text = "REVIEW JOURNEY & FARE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CivicSlate800,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Confirm itinerary details before generating your digital pass",
                    fontSize = 11.sp,
                    color = CivicSlate600,
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )

                // Journey Summary Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CivicSlate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedBus.busNumber, fontWeight = FontWeight.Black, fontSize = 15.sp, color = CivicSlate900)
                            Surface(shape = RoundedCornerShape(6.dp), color = CivicGreenPrimary.copy(alpha = 0.15f)) {
                                Text(
                                    text = "SAFETY VERIFIED",
                                    color = CivicGreenDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = CivicOrangePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Boarding: $initialOrigin", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = CivicSlate800)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = CivicGreenPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Destination: $initialDestination", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = CivicSlate800)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CivicSlate200)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Travel Date & Slot", fontSize = 10.sp, color = CivicSlate400)
                                Text("$selectedDate • $selectedSlot", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicSlate900)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Seats Assigned", fontSize = 10.sp, color = CivicSlate400)
                                Text(selectedSeats.joinToString(", "), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicOrangeDark)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fare Breakdown Card
                val baseFare = selectedBus.baseFarePerPassenger * passengerCount
                val safetySurcharge = 5.0
                val taxGst = (baseFare * 0.05).coerceAtLeast(2.0)
                val totalAmount = baseFare + safetySurcharge + taxGst

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CivicNavyContainer.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, CivicSlate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "FARE BREAKDOWN",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CivicNavyPrimary,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Base Transit Fare ($passengerCount x ₹${selectedBus.baseFarePerPassenger.toInt()})", fontSize = 12.sp, color = CivicSlate700)
                            Text("₹${baseFare.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicSlate900)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Clean Corridor Safety Surcharge", fontSize = 12.sp, color = CivicSlate700)
                            Text("₹${safetySurcharge.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicSlate900)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("GST & Municipal Transit Cess", fontSize = 12.sp, color = CivicSlate700)
                            Text("₹${String.format("%.1f", taxGst)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicSlate900)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CivicSlate200)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Amount Due", fontSize = 14.sp, fontWeight = FontWeight.Black, color = CivicSlate900)
                            Text("₹${String.format("%.1f", totalAmount)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = CivicOrangeDark)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Realistic Frontend Disclaimer Note
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CivicAmber.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, CivicAmber.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = CivicAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Frontend simulation for seamless mobility demonstration. Instant digital boarding pass will be generated locally.",
                            fontSize = 10.5.sp,
                            color = CivicSlate800,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                CivicButton(
                    text = "CONFIRM & GENERATE DIGITAL PASS",
                    onClick = {
                        val booking = viewModel.bookingService.createBooking(
                            routeId = "route_safest",
                            origin = initialOrigin,
                            destination = initialDestination,
                            busOption = selectedBus,
                            travelDate = selectedDate,
                            departureTime = selectedSlot,
                            passengers = passengerNames.take(passengerCount),
                            selectedSeats = selectedSeats.take(passengerCount),
                            pickupPoint = "$initialOrigin Bus Bay 2",
                            dropPoint = "$initialDestination Terminal"
                        )
                        confirmedBooking = booking
                        step = 5
                        onBookingConfirmed(booking.id)
                    },
                    variant = CivicButtonVariant.PRIMARY_GREEN,
                    size = CivicButtonSize.LARGE,
                    leadingIcon = Icons.Default.ConfirmationNumber,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "confirm_booking_button"
                )
            }

            // STEP 5: BOOKING CONFIRMED & DIGITAL PASS
            if (step == 5 && confirmedBooking != null) {
                val booking = confirmedBooking!!

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, CivicGreenPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Success Header Badge
                        Surface(
                            shape = CircleShape,
                            color = CivicGreenPrimary,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "BOOKING CONFIRMED!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = CivicGreenDark,
                            letterSpacing = 0.5.sp
                        )

                        Text(
                            text = "Ticket Ref: ${booking.id}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicSlate600,
                            fontFamily = FontFamily.Monospace
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = CivicSlate200)

                        // QR Code Graphic Mockup
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CivicSlate100,
                            border = BorderStroke(1.dp, CivicSlate200),
                            modifier = Modifier.size(130.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = "Ticket QR Code",
                                    tint = CivicNavyDark,
                                    modifier = Modifier.size(80.dp)
                                )
                                Text(
                                    text = "SCAN AT BUS ENTRY",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CivicSlate700
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Itinerary Rows
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Bus Service", fontSize = 10.sp, color = CivicSlate400)
                                    Text(booking.busNumber, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CivicSlate900)
                                    Text(booking.busType, fontSize = 10.sp, color = CivicSlate600)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Seats", fontSize = 10.sp, color = CivicSlate400)
                                    Text(booking.seatNumbers.joinToString(", "), fontSize = 14.sp, fontWeight = FontWeight.Black, color = CivicOrangeDark)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Boarding Point", fontSize = 10.sp, color = CivicSlate400)
                                    Text(booking.pickupPoint, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CivicSlate800)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Time", fontSize = 10.sp, color = CivicSlate400)
                                    Text(booking.departureTime, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicSlate900)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Passengers (${booking.passengerCount})", fontSize = 10.sp, color = CivicSlate400)
                                    Text(booking.passengers.joinToString(", ") { it.name }, fontSize = 12.sp, color = CivicSlate800)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Total Paid", fontSize = 10.sp, color = CivicSlate400)
                                    Text("₹${String.format("%.1f", booking.totalFare)}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = CivicGreenDark)
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = CivicSlate200)

                        Text(
                            text = "🛡️ Corridor Flood-Bypassing Verified • Driver Live Connected",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicGreenDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToMap,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View Map", fontSize = 12.sp)
                    }

                    CivicButton(
                        text = "RETURN TO HOME",
                        onClick = onBack,
                        variant = CivicButtonVariant.PRIMARY_ORANGE,
                        size = CivicButtonSize.LARGE,
                        modifier = Modifier.weight(1f),
                        testTag = "return_home_after_booking"
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingStepPill(
    number: String,
    label: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = when {
                isActive -> CivicOrangePrimary
                isCompleted -> CivicGreenPrimary
                else -> CivicSlate200
            },
            modifier = Modifier.size(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = if (isCompleted) "✓" else number,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isActive || isCompleted) Color.White else CivicSlate600
                )
            }
        }
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            color = if (isActive) CivicSlate900 else CivicSlate400
        )
    }
}

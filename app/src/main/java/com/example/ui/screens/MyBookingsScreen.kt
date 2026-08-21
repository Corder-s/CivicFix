package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BookingStatus
import com.example.data.models.JourneyBooking
import com.example.ui.CivicFixViewModel
import com.example.ui.components.CivicButton
import com.example.ui.components.CivicButtonSize
import com.example.ui.components.CivicButtonVariant
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicOrangeContainer
import com.example.ui.theme.CivicOrangeDark
import com.example.ui.theme.CivicOrangeLight
import com.example.ui.theme.CivicOrangePrimary
import com.example.ui.theme.CivicRed
import com.example.ui.theme.CivicRedContainer
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate700
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900

@Composable
fun MyBookingsScreen(
    viewModel: CivicFixViewModel,
    onBookNewJourney: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val bookings by viewModel.bookings.collectAsState()
    var selectedBookingForPass by remember { mutableStateOf<JourneyBooking?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicSlate100)
    ) {
        // Top Bar
        Surface(
            color = CivicNavyDark,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "My Journey Bookings",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Digital Bus Tickets & Boarding Passes",
                            fontSize = 11.sp,
                            color = CivicOrangeLight
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CivicOrangePrimary,
                    modifier = Modifier.clickable { onBookNewJourney() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Book Bus", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // List of bookings or empty state
        if (bookings.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = CivicOrangeContainer,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = CivicOrangePrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No Bookings Found",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate900
                )

                Text(
                    text = "You haven't reserved any safe bus journeys yet. Book a seat to avoid waterlogged routes.",
                    fontSize = 12.sp,
                    color = CivicSlate600,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                CivicButton(
                    text = "BOOK SAFE BUS JOURNEY",
                    onClick = onBookNewJourney,
                    variant = CivicButtonVariant.PRIMARY_ORANGE,
                    size = CivicButtonSize.LARGE,
                    leadingIcon = Icons.Default.DirectionsBus,
                    testTag = "empty_bookings_book_btn"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .testTag("bookings_list"),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "ACTIVE DIGITAL TICKETS (${bookings.count { it.status == BookingStatus.CONFIRMED }})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CivicSlate700,
                        letterSpacing = 0.5.sp
                    )
                }

                items(bookings, key = { it.id }) { booking ->
                    val isCancelled = booking.status == BookingStatus.CANCELLED

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, if (isCancelled) CivicSlate200 else CivicOrangePrimary.copy(alpha = 0.4f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Top Row: Ticket ID & Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isCancelled) CivicSlate200 else CivicOrangePrimary,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.DirectionsBus,
                                                contentDescription = null,
                                                tint = if (isCancelled) CivicSlate600 else Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = booking.id,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CivicSlate900
                                        )
                                        Text(
                                            text = booking.travelDate,
                                            fontSize = 11.sp,
                                            color = CivicSlate600
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isCancelled) CivicRedContainer else CivicGreenPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = booking.status.displayName.uppercase(),
                                        color = if (isCancelled) CivicRed else CivicGreenDark,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Route Points
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = CivicOrangePrimary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${booking.origin} ➔ ${booking.destination}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CivicSlate900
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Details Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Bus & Departure", fontSize = 10.sp, color = CivicSlate400)
                                    Text("${booking.busNumber} • ${booking.departureTime}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CivicSlate800)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Seats & Fare", fontSize = 10.sp, color = CivicSlate400)
                                    Text("${booking.seatNumbers.joinToString(", ")} (₹${booking.totalFare.toInt()})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicOrangeDark)
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CivicSlate200)

                            // Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.QrCode2, contentDescription = null, tint = CivicNavyDark, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "PASS VERIFIED",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CivicNavyDark
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (!isCancelled) {
                                        OutlinedButton(
                                            onClick = { viewModel.bookingService.cancelBooking(booking.id) },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("Cancel", fontSize = 11.sp, color = CivicRed)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = CivicNavyDark,
                                        modifier = Modifier
                                            .clickable { onNavigateToMap() }
                                            .padding(2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Map, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Live Track", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.CivicStrings
import com.example.data.localization.civicString
import com.example.data.services.RouteAlternative
import com.example.data.services.SafetyFactorItem
import com.example.data.services.TransitLeg
import com.example.data.services.TransitLegType
import com.example.data.services.TravelPreference
import com.example.ui.CivicFixViewModel
import com.example.ui.components.CivicButton
import com.example.ui.components.CivicButtonSize
import com.example.ui.components.CivicButtonVariant
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicAmberContainer
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenLight
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyLight
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicOrangeContainer
import com.example.ui.theme.CivicOrangeDark
import com.example.ui.theme.CivicOrangePrimary
import com.example.ui.theme.CivicRedContainer
import com.example.ui.theme.CivicRedDark
import com.example.ui.theme.CivicRedPrimary
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate700
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SafeJourneyPlannerScreen(
    viewModel: CivicFixViewModel,
    onNavigateToMap: () -> Unit = {},
    onBookJourneyClick: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val origin by viewModel.journeyOrigin.collectAsState()
    val destination by viewModel.journeyDestination.collectAsState()
    val dateTime by viewModel.journeyDateTime.collectAsState()
    val selectedPref by viewModel.travelPreference.collectAsState()
    val routes by viewModel.journeyRoutes.collectAsState()
    val selectedRoute by viewModel.selectedRoute.collectAsState()
    val weatherAlert = viewModel.currentWeatherAlert

    var isStopsExpanded by remember { mutableStateOf(false) }

    val quickOrigins = listOf("Sector 62 IT Hub", "Connaught Place", "T3 Airport Terminal", "AIIMS Hospital Hub", "Civil Lines Metro")
    val quickDestinations = listOf("Connaught Place / City Center", "Cyber City", "Nehru Place Bus Hub", "Old Delhi Rly Station", "Noida Sector 18")
    val quickDepartureTimes = listOf("Leave Now", "+15 mins", "+30 mins", "Evening Rush (6:00 PM)")

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("safe_journey_planner_screen"),
        containerColor = CivicSlate100
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Hero Banner
            item {
                JourneyPlannerHeader(
                    weatherAlert = weatherAlert,
                    onNavigateToMap = onNavigateToMap
                )
            }

            // Input Form Card (Origin, Destination, Time)
            item {
                JourneyInputCard(
                    origin = origin,
                    destination = destination,
                    dateTime = dateTime,
                    onOriginChange = { viewModel.setJourneyOrigin(it) },
                    onDestinationChange = { viewModel.setJourneyDestination(it) },
                    onDateTimeChange = { viewModel.setJourneyDateTime(it) },
                    onSwap = { viewModel.swapJourneyEndpoints() },
                    quickOrigins = quickOrigins,
                    quickDestinations = quickDestinations,
                    quickTimes = quickDepartureTimes
                )
            }

            // Travel Preference Tabs
            item {
                TravelPreferenceTabs(
                    selectedPreference = selectedPref,
                    onPreferenceSelected = { viewModel.setTravelPreference(it) }
                )
            }

            // Bus Disruption / Waterlogging Alert Banner (if any route has disruption)
            val disruptedRoute = routes.firstOrNull { it.hasDisruption }
            if (disruptedRoute != null) {
                item {
                    BusDisruptionBanner(
                        disruptionReason = disruptedRoute.disruptionReason ?: "Road hazard detected on corridor",
                        alternativeText = disruptedRoute.alternativeSuggestion ?: "Route B (Elevated Metro Corridor) avoids this hazard.",
                        onSwitchToSafe = { viewModel.rerouteToSafestAlternative() }
                    )
                }
            }

            // Route Alternatives Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AVAILABLE ROUTE ALTERNATIVES",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CivicSlate800,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Dynamically prioritized for ${selectedPref.displayName}",
                            fontSize = 11.sp,
                            color = CivicNavyLight,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, CivicSlate200)
                    ) {
                        Text(
                            text = "${routes.size} Routes",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicNavyDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Route Alternatives Horizontal/Vertical Cards
            items(routes) { route ->
                RouteAlternativeCard(
                    route = route,
                    isSelected = selectedRoute?.id == route.id,
                    onSelect = { viewModel.selectJourneyRoute(route.id) }
                )
            }

            // Detailed Breakdown of the Selected Route
            if (selectedRoute != null) {
                val activeRoute = selectedRoute!!

                item {
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = CivicSlate200
                    )
                }

                // Explainable Safety Score Breakdown ("Why?")
                item {
                    ExplainableSafetyScoreCard(route = activeRoute)
                }

                // Bus Route Integration & Multimodal Itinerary Timeline
                item {
                    TransitItineraryCard(
                        route = activeRoute,
                        isStopsExpanded = isStopsExpanded,
                        onToggleExpandStops = { isStopsExpanded = !isStopsExpanded }
                    )
                }

                // Civic Issue & Weather Route Intelligence Card
                item {
                    RouteIntelligenceCard(
                        route = activeRoute,
                        onNavigateToMap = onNavigateToMap
                    )
                }

                // Floating Action / Bottom Buttons
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CivicButton(
                            text = "BOOK TRANSIT TICKET (QR PASS)",
                            onClick = onBookJourneyClick,
                            variant = CivicButtonVariant.PRIMARY_ORANGE,
                            size = CivicButtonSize.LARGE,
                            leadingIcon = Icons.Default.Payments,
                            modifier = Modifier.fillMaxWidth().testTag("btn_book_transit_ticket")
                        )

                        CivicButton(
                            text = civicString(CivicStrings.VIEW_ON_LIVE_MAP).uppercase(),
                            onClick = onNavigateToMap,
                            variant = CivicButtonVariant.SECONDARY_OUTLINE,
                            size = CivicButtonSize.LARGE,
                            leadingIcon = Icons.Default.Map,
                            modifier = Modifier.fillMaxWidth().testTag("btn_view_on_map")
                        )

                        Text(
                            text = civicString(CivicStrings.DEMO_TRANSIT_DISCLAIMER),
                            fontSize = 11.sp,
                            color = CivicSlate400,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Top Header with Urban Mobility branding & live weather risk telemetry
 */
@Composable
fun JourneyPlannerHeader(
    weatherAlert: com.example.data.services.WeatherSafetyAlert,
    onNavigateToMap: () -> Unit
) {
    Surface(
        color = CivicNavyDark,
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CivicGreenPrimary,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Safe Route",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = civicString(CivicStrings.PLAN_SAFE_JOURNEY),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "AI Multimodal Safety Planner",
                            fontSize = 11.sp,
                            color = CivicGreenLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CivicNavyLight.copy(alpha = 0.3f),
                    border = BorderStroke(1.dp, CivicNavyLight.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { onNavigateToMap() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Map",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Live Map",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Weather & Road Safety Alert Pill
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E3A5F),
                border = BorderStroke(1.dp, Color(0xFF386FA4))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Thunderstorm,
                        contentDescription = "Rain alert",
                        tint = Color(0xFF64B5F6),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rain Alert: ${weatherAlert.precipitationMm} mm • ${weatherAlert.waterloggingRisk}",
                            color = Color(0xFFE3F2FD),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = weatherAlert.advisoryText,
                            color = Color(0xFFB0BEC5),
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Origin, Destination, Departure Time input card
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JourneyInputCard(
    origin: String,
    destination: String,
    dateTime: String,
    onOriginChange: (String) -> Unit,
    onDestinationChange: (String) -> Unit,
    onDateTimeChange: (String) -> Unit,
    onSwap: () -> Unit,
    quickOrigins: List<String>,
    quickDestinations: List<String>,
    quickTimes: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Origin Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = CivicGreenPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Origin",
                            tint = CivicGreenDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = origin,
                    onValueChange = onOriginChange,
                    label = { Text(civicString(CivicStrings.ORIGIN_INPUT_LABEL), fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CivicNavyPrimary,
                        unfocusedBorderColor = CivicSlate200
                    ),
                    trailingIcon = {
                        if (origin.isNotEmpty()) {
                            IconButton(onClick = { onOriginChange("") }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(14.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_journey_origin")
                )
            }

            // Quick Origin Chips
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 42.dp, top = 6.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                quickOrigins.take(3).forEach { place ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (origin == place) CivicGreenPrimary.copy(alpha = 0.15f) else CivicSlate100,
                        border = BorderStroke(1.dp, if (origin == place) CivicGreenPrimary else CivicSlate200),
                        modifier = Modifier.clickable { onOriginChange(place) }
                    ) {
                        Text(
                            text = place,
                            fontSize = 10.sp,
                            fontWeight = if (origin == place) FontWeight.Bold else FontWeight.Medium,
                            color = if (origin == place) CivicGreenDark else CivicSlate700,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Swap Button Divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = CivicSlate200)
                IconButton(
                    onClick = onSwap,
                    modifier = Modifier
                        .size(28.dp)
                        .background(CivicSlate100, CircleShape)
                        .border(1.dp, CivicSlate200, CircleShape)
                        .testTag("btn_swap_endpoints")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap",
                        tint = CivicNavyDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Divider(modifier = Modifier.weight(1f), color = CivicSlate200)
            }

            // Destination Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = CivicRedPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Destination",
                            tint = CivicRedDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = destination,
                    onValueChange = onDestinationChange,
                    label = { Text(civicString(CivicStrings.DESTINATION_INPUT_LABEL), fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CivicNavyPrimary,
                        unfocusedBorderColor = CivicSlate200
                    ),
                    trailingIcon = {
                        if (destination.isNotEmpty()) {
                            IconButton(onClick = { onDestinationChange("") }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(14.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_journey_destination")
                )
            }

            // Quick Destination Chips
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 42.dp, top = 6.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                quickDestinations.take(3).forEach { place ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (destination == place) CivicOrangeContainer else CivicSlate100,
                        border = BorderStroke(1.dp, if (destination == place) CivicOrangePrimary else CivicSlate200),
                        modifier = Modifier.clickable { onDestinationChange(place) }
                    ) {
                        Text(
                            text = place,
                            fontSize = 10.sp,
                            fontWeight = if (destination == place) FontWeight.Bold else FontWeight.Medium,
                            color = if (destination == place) CivicOrangeDark else CivicSlate700,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Departure Time Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "TIME:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CivicSlate400
                )
                quickTimes.forEach { timeStr ->
                    val isSelected = dateTime == timeStr
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) CivicNavyPrimary else CivicSlate100,
                        border = BorderStroke(1.dp, if (isSelected) CivicNavyDark else CivicSlate200),
                        modifier = Modifier.clickable { onDateTimeChange(timeStr) }
                    ) {
                        Text(
                            text = timeStr,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else CivicSlate800,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 4 Travel Preferences Tabs: Fastest, Cheapest, Safest, Best Overall
 */
@Composable
fun TravelPreferenceTabs(
    selectedPreference: TravelPreference,
    onPreferenceSelected: (TravelPreference) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "TRAVEL PRIORITY",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CivicSlate400,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TravelPreference.values().forEach { pref ->
                    val isSelected = selectedPreference == pref
                    val icon: ImageVector = when (pref) {
                        TravelPreference.FASTEST -> Icons.Default.Speed
                        TravelPreference.CHEAPEST -> Icons.Default.Payments
                        TravelPreference.SAFEST -> Icons.Default.Shield
                        TravelPreference.BEST_OVERALL -> Icons.Default.Star
                    }

                    val accentColor = when (pref) {
                        TravelPreference.FASTEST -> CivicOrangePrimary
                        TravelPreference.CHEAPEST -> Color(0xFF00897B)
                        TravelPreference.SAFEST -> CivicGreenPrimary
                        TravelPreference.BEST_OVERALL -> CivicNavyPrimary
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) accentColor.copy(alpha = 0.12f) else CivicSlate100,
                        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) accentColor else CivicSlate200),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onPreferenceSelected(pref) }
                            .testTag("tab_pref_${pref.name.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = pref.displayName,
                                tint = if (isSelected) accentColor else CivicSlate400,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = pref.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CivicSlate900 else CivicSlate700,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Passenger Notification & Bus Disruption Warning Banner
 */
@Composable
fun BusDisruptionBanner(
    disruptionReason: String,
    alternativeText: String,
    onSwitchToSafe: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        border = BorderStroke(1.dp, Color(0xFFFFB74D)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Disruption",
                    tint = CivicOrangeDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CIVIC ISSUE → BUS CORRIDOR DISRUPTION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CivicOrangeDark,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = disruptionReason,
                fontSize = 12.sp,
                color = CivicSlate900,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = alternativeText,
                fontSize = 11.sp,
                color = CivicSlate700,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            CivicButton(
                text = "SWITCH TO SAFEST ALTERNATIVE ROUTE",
                onClick = onSwitchToSafe,
                variant = CivicButtonVariant.PRIMARY_GREEN,
                size = CivicButtonSize.SMALL,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Route Alternative Card
 */
@Composable
fun RouteAlternativeCard(
    route: RouteAlternative,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (isSelected) CivicGreenPrimary else CivicSlate200
    val cardBg = if (isSelected) Color(0xFFF0FDF4) else Color.White

    val scoreColor = when {
        route.safetyScore >= 90 -> CivicGreenDark
        route.safetyScore >= 75 -> CivicNavyDark
        else -> CivicOrangeDark
    }

    val scoreBg = when {
        route.safetyScore >= 90 -> Color(0xFFDCFCE7)
        route.safetyScore >= 75 -> Color(0xFFE0F2FE)
        else -> Color(0xFFFEF3C7)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onSelect() }
            .testTag("route_card_${route.id}"),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(if (isSelected) 3.dp else 1.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Tag, Recommended badge, Safety Score Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (route.primaryTag) {
                            TravelPreference.FASTEST -> CivicOrangeContainer
                            TravelPreference.CHEAPEST -> Color(0xFFE0F2F1)
                            TravelPreference.SAFEST -> Color(0xFFDCFCE7)
                            TravelPreference.BEST_OVERALL -> Color(0xFFEDE9FE)
                        }
                    ) {
                        Text(
                            text = route.primaryTag.name.replace("_", " "),
                            color = when (route.primaryTag) {
                                TravelPreference.FASTEST -> CivicOrangeDark
                                TravelPreference.CHEAPEST -> Color(0xFF00695C)
                                TravelPreference.SAFEST -> CivicGreenDark
                                TravelPreference.BEST_OVERALL -> Color(0xFF5B21B6)
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (route.isRecommended) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CivicGreenPrimary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Recommended",
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "RECOMMENDED",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                // Safety Score Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = scoreBg,
                    border = BorderStroke(1.dp, scoreColor.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Score",
                            tint = scoreColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Safety ${route.safetyScore}/100",
                            color = scoreColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Route Metrics: ETA, Fare, Distance, Walking, Transfers
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${route.etaMinutes} min",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CivicSlate900
                    )
                    Text(
                        text = route.routeName,
                        fontSize = 12.sp,
                        color = CivicSlate700,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (route.fareInr != null) "₹${route.fareInr}" else "Free",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicNavyDark
                    )
                    Text(
                        text = "${route.distanceKm} km • Walk ${route.walkingDistanceMeters}m",
                        fontSize = 11.sp,
                        color = CivicSlate400
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Transit Buses Pills (e.g. Bus 118 -> Bus 52)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = "Bus",
                    tint = CivicNavyLight,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (route.affectedBusRoutes.isNotEmpty()) route.affectedBusRoutes.joinToString(" → ") else "Direct Walk & Transit",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CivicNavyDark
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (route.transfersCount == 0) "Direct" else "${route.transfersCount} transfer",
                    fontSize = 10.sp,
                    color = CivicSlate400
                )
            }

            // Hazard warning if any
            if (route.civicHazards.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Hazard",
                            tint = CivicAmber,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = route.civicHazards.first(),
                            fontSize = 10.sp,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Explainable Route Safety Score Card with factor breakdown ("Why?")
 */
@Composable
fun ExplainableSafetyScoreCard(route: RouteAlternative) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = CivicGreenPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Safety Score",
                                tint = CivicGreenDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Safety Score: ${route.safetyScore}/100",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CivicSlate900
                        )
                        Text(
                            text = if (route.safetyScore >= 90) "Safe Corridor • Zero High Hazards" else "Moderate Risk Corridor",
                            fontSize = 11.sp,
                            color = if (route.safetyScore >= 90) CivicGreenDark else CivicOrangeDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Why?",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CivicSlate800
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Safety Factor Items (Checkmarks and Warnings)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                route.safetyFactors.forEach { factor ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = if (factor.isPositive) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = if (factor.isPositive) "Positive" else "Warning",
                            tint = if (factor.isPositive) CivicGreenPrimary else CivicOrangePrimary,
                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = factor.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicSlate900
                            )
                            Text(
                                text = factor.description,
                                fontSize = 11.sp,
                                color = CivicSlate700
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Summary Explanation Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CivicSlate100,
                border = BorderStroke(1.dp, CivicSlate200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = route.recommendationExplanation,
                    fontSize = 11.sp,
                    color = CivicSlate800,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

/**
 * Transit Itinerary & Bus Step-by-Step Timeline
 */
@Composable
fun TransitItineraryCard(
    route: RouteAlternative,
    isStopsExpanded: Boolean,
    onToggleExpandStops: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = "Transit",
                        tint = CivicNavyDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BUS ROUTE & TRANSIT ITINERARY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CivicSlate800,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "₹${route.fareInr ?: 0} Total",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicNavyDark
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step-by-step Timeline legs
            route.legs.forEachIndexed { index, leg ->
                TransitLegTimelineItem(
                    leg = leg,
                    isLast = index == route.legs.size - 1,
                    isStopsExpanded = isStopsExpanded,
                    onToggleExpandStops = onToggleExpandStops
                )
            }
        }
    }
}

/**
 * Single Leg Item in the vertical timeline
 */
@Composable
fun TransitLegTimelineItem(
    leg: TransitLeg,
    isLast: Boolean,
    isStopsExpanded: Boolean,
    onToggleExpandStops: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Vertical Timeline line and Node
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = when (leg.type) {
                    TransitLegType.WALK -> CivicSlate400
                    TransitLegType.BUS -> CivicNavyPrimary
                    TransitLegType.METRO_FEEDER -> CivicGreenPrimary
                },
                modifier = Modifier.size(22.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (leg.type) {
                            TransitLegType.WALK -> Icons.Default.DirectionsWalk
                            TransitLegType.BUS -> Icons.Default.DirectionsBus
                            TransitLegType.METRO_FEEDER -> Icons.Default.ElectricRickshaw
                        },
                        contentDescription = leg.type.name,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(55.dp)
                        .background(CivicSlate200)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Leg Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = leg.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate900
                )
                Text(
                    text = "${leg.durationMinutes} min • ${leg.distanceMeters}m",
                    fontSize = 11.sp,
                    color = CivicSlate400
                )
            }

            Text(
                text = leg.instruction,
                fontSize = 12.sp,
                color = CivicSlate700,
                modifier = Modifier.padding(top = 2.dp)
            )

            // If Bus Leg, show bus number badge, stops count, and expandable stops
            if (leg.busNumber != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CivicNavyContainer,
                        border = BorderStroke(1.dp, CivicNavyLight.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = leg.busNumber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CivicNavyDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (leg.fareInr != null) {
                        Text(
                            text = "Fare: ₹${leg.fareInr}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CivicGreenDark
                        )
                    }

                    if (leg.stopsList.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CivicSlate100,
                            modifier = Modifier.clickable { onToggleExpandStops() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${leg.stopsList.size} stops",
                                    fontSize = 10.sp,
                                    color = CivicSlate700,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = if (isStopsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand",
                                    tint = CivicSlate700,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                // Expandable list of stops
                if (isStopsExpanded && leg.stopsList.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CivicSlate100,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            leg.stopsList.forEachIndexed { sIdx, stop ->
                                Text(
                                    text = "• $stop",
                                    fontSize = 11.sp,
                                    color = CivicSlate800,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Hazard warning along leg
            if (leg.hazardWarnings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                leg.hazardWarnings.forEach { warning ->
                    Text(
                        text = "⚠ $warning",
                        fontSize = 10.sp,
                        color = CivicOrangeDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Route Intelligence: Real-time correlation with live Room DB civic issues & weather alerts
 */
@Composable
fun RouteIntelligenceCard(
    route: RouteAlternative,
    onNavigateToMap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "CIVIC REPORT & WEATHER INTELLIGENCE",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CivicSlate800,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Weather Impact
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Thunderstorm,
                    contentDescription = "Weather",
                    tint = Color(0xFF0288D1),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Weather Impact:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate900
                    )
                    Text(
                        text = route.weatherRisk,
                        fontSize = 11.sp,
                        color = CivicSlate700
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Civic Hazards on Corridor
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Hazards",
                    tint = if (route.civicHazards.isEmpty()) CivicGreenPrimary else CivicAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Citizen Reported Hazards:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate900
                    )
                    Text(
                        text = if (route.civicHazards.isEmpty()) {
                            "Zero active citizen hazards on this corridor."
                        } else {
                            route.civicHazards.joinToString("\n• ")
                        },
                        fontSize = 11.sp,
                        color = if (route.civicHazards.isEmpty()) CivicGreenDark else CivicSlate700
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Notice",
                        tint = CivicSlate400,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Synchronized in real-time with CivicFix Room Database citizen incident reports.",
                        fontSize = 10.sp,
                        color = CivicSlate700
                    )
                }
            }
        }
    }
}

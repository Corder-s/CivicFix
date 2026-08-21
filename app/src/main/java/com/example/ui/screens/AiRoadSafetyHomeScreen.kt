package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.User
import com.example.data.services.BusRouteInfo
import com.example.data.services.PredictiveHazard
import com.example.data.services.WeatherSafetyAlert
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicAmberContainer
import com.example.ui.theme.CivicCharcoal
import com.example.ui.theme.CivicDarkGray
import com.example.ui.theme.CivicOrangeBorder
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
import com.example.ui.theme.CivicSuccess
import com.example.ui.theme.CivicSuccessContainer

@Composable
fun AiRoadSafetyHomeScreen(
    user: User?,
    origin: String,
    destination: String,
    weatherAlert: WeatherSafetyAlert,
    predictiveHazards: List<PredictiveHazard>,
    activeBusRoutes: List<BusRouteInfo>,
    onPlanJourneyClick: (String, String) -> Unit,
    onOpenMapClick: () -> Unit,
    onReportHazardClick: () -> Unit,
    onEmergencySosClick: () -> Unit,
    onAskAiClick: (String) -> Unit,
    onAlertClick: (String) -> Unit = {},
    onSwitchToCivicFix: () -> Unit = {}
) {
    var searchDestinationText by remember { mutableStateOf(destination.ifEmpty { "Connaught Place, City Center" }) }
    var currentOriginText by remember { mutableStateOf(origin.ifEmpty { "Sector 62 IT Hub, Noida" }) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicSlate100)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // ==========================================
        // 1. HERO SEARCH CARD: "WHERE ARE YOU GOING?"
        // ==========================================
        Card(
            shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = CivicDarkGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                // Header Badge & Slogan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CivicOrangePrimary.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, CivicOrangePrimary.copy(alpha = 0.4f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CivicOrangeLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI ROAD SAFETY ENGINE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = CivicOrangeLight,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CivicSuccess.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, CivicSuccess.copy(alpha = 0.4f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(CivicSuccess, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Live Monitoring",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicSuccess
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Where are you going?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Get from A to B safely, quickly, and affordably with real-time hazard avoidance.",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Interactive Origin & Destination Inputs Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Origin Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = CivicOrangeContainer,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.NearMe,
                                        contentDescription = "From",
                                        tint = CivicOrangePrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "STARTING POINT",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CivicSlate400
                                )
                                Text(
                                    text = currentOriginText,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CivicSlate900,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Divider with dotted indicator
                        Row(
                            modifier = Modifier.padding(start = 15.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(16.dp)
                                    .background(CivicOrangePrimary.copy(alpha = 0.4f))
                            )
                        }

                        // Destination Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = CivicDarkGray,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "To",
                                        tint = CivicOrangePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "DESTINATION",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CivicOrangeDark
                                )
                                OutlinedTextField(
                                    value = searchDestinationText,
                                    onValueChange = { searchDestinationText = it },
                                    placeholder = { Text("Enter destination...", fontSize = 13.5.sp, color = CivicSlate400) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .testTag("home_destination_input")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Destination Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickPlaceChip("🏢 Office / Cyber Hub") { searchDestinationText = "Cyber Hub Phase 2, Gurugram" }
                            QuickPlaceChip("🎓 College / Univ") { searchDestinationText = "Delhi University North Campus" }
                            QuickPlaceChip("🏥 AIIMS Hospital") { searchDestinationText = "AIIMS New Delhi Main Wing" }
                            QuickPlaceChip("✈️ IGI Airport T3") { searchDestinationText = "IGI Airport Terminal 3" }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Primary Action Button
                        Button(
                            onClick = { onPlanJourneyClick(currentOriginText, searchDestinationText) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CivicOrangePrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_plan_safe_journey")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Route, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Plan Safe Journey",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ==========================================
        // 2. LIVE ROAD SAFETY OVERVIEW CARD
        // ==========================================
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, CivicSlate200),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CivicOrangeContainer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Speed, contentDescription = null, tint = CivicOrangePrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Live Road Safety",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicSlate900
                            )
                            Text(
                                text = "City-wide real-time mobility barometer",
                                fontSize = 11.sp,
                                color = CivicSlate600
                            )
                        }
                    }

                    // Score Indicator Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CivicSuccessContainer
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "91/100",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = CivicSuccess
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SAFE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CivicSuccess
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4 Grid Safety Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SafetyMetricBox(
                        title = "Traffic",
                        value = "Moderate",
                        subtitle = "Avg 28 km/h",
                        icon = Icons.Default.Traffic,
                        tint = CivicAmber,
                        modifier = Modifier.weight(1f)
                    )
                    SafetyMetricBox(
                        title = "Weather",
                        value = weatherAlert.waterloggingRisk,
                        subtitle = "${weatherAlert.precipitationMm} mm rain",
                        icon = Icons.Default.WbSunny,
                        tint = CivicOrangePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SafetyMetricBox(
                        title = "Road Condition",
                        value = "Good",
                        subtitle = "96% Clear Roads",
                        icon = Icons.Default.CheckCircle,
                        tint = CivicSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    SafetyMetricBox(
                        title = "Active Hazards",
                        value = "${predictiveHazards.size} Reports",
                        subtitle = "2 Near Your Route",
                        icon = Icons.Default.Warning,
                        tint = CivicRed,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Two-way intelligence note
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CivicOrangeContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = CivicOrangeDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Data continuously synced with 140+ citizen reports from CivicFix.",
                            fontSize = 11.sp,
                            color = CivicSlate800,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 3. EMERGENCY SOS TRIGGER BUTTON
        // ==========================================
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CivicRedContainer),
            border = BorderStroke(1.5.dp, CivicRed.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onEmergencySosClick() }
                .testTag("emergency_sos_banner_button")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = CivicRed,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Sos,
                                contentDescription = "Emergency SOS",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Emergency / SOS Mode",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicRed
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CivicRed
                            ) {
                                Text(
                                    text = "1-TAP",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Find nearest hospital, police & share live location",
                            fontSize = 11.5.sp,
                            color = CivicSlate800
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = CivicRed,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ==========================================
        // 4. QUICK ACTION CARDS GRID
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Quick Actions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CivicSlate900
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionCard(
                    title = "CivicLive Map",
                    subtitle = "Live hazards & buses",
                    icon = Icons.Default.Map,
                    accentColor = CivicOrangePrimary,
                    onClick = onOpenMapClick,
                    modifier = Modifier.weight(1f)
                )
                ActionCard(
                    title = "Plan Journey",
                    subtitle = "Safe multi-modal routes",
                    icon = Icons.Default.Route,
                    accentColor = CivicOrangeDark,
                    onClick = { onPlanJourneyClick(currentOriginText, searchDestinationText) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionCard(
                    title = "Report Hazard",
                    subtitle = "Pothole, flood, lights",
                    icon = Icons.Default.Report,
                    accentColor = CivicRed,
                    onClick = onReportHazardClick,
                    modifier = Modifier.weight(1f)
                )
                ActionCard(
                    title = "AI Road Guide",
                    subtitle = "Instant route advisory",
                    icon = Icons.Default.AutoAwesome,
                    accentColor = Color(0xFF673AB7),
                    onClick = { onAskAiClick("What's the safest route right now?") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ==========================================
        // 5. YOUR ACTIVE / RECOMMENDED ROUTE
        // ==========================================
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, CivicSlate200),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Route (Saved Commute)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate900
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CivicOrangeContainer
                    ) {
                        Text(
                            text = "AI RECOMMENDED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicOrangeDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Route summary box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CivicSlate100,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Home → College Campus",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicSlate900
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "via Blue Line Metro + Route 52 Bus",
                                fontSize = 11.5.sp,
                                color = CivicSlate600
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "31 min • ₹22",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicOrangeDark
                            )
                            Text(
                                text = "🛡️ 91/100 Safe",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CivicSuccess
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = CivicOrangePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Bus 52 arriving at Sector 62 in 4 mins (Low Crowd)",
                        fontSize = 11.5.sp,
                        color = CivicSlate700,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { onPlanJourneyClick("Sector 62 IT Hub", "Delhi University North Campus") },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CivicOrangePrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CivicOrangePrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Detailed Itinerary & Alternatives", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ==========================================
        // 6. LIVE ROAD & TRANSIT ALERTS
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Road & Weather Alerts",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate900
                )
                Text(
                    text = "${predictiveHazards.size + 1} Active",
                    fontSize = 12.sp,
                    color = CivicOrangeDark,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Weather alert card
            AlertItemCard(
                icon = Icons.Default.WaterDrop,
                iconTint = Color(0xFF0288D1),
                title = weatherAlert.headline,
                description = weatherAlert.advisoryText,
                tag = "WEATHER IMPACT",
                tagColor = Color(0xFFE1F5FE),
                tagTextColor = Color(0xFF0288D1),
                onClick = { onAlertClick(weatherAlert.headline) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Predictive hazard cards
            predictiveHazards.take(3).forEach { hazard ->
                val isHighRisk = hazard.probabilityPercent >= 75
                AlertItemCard(
                    icon = if (isHighRisk) Icons.Default.Warning else Icons.Default.Traffic,
                    iconTint = if (isHighRisk) CivicRed else CivicAmber,
                    title = "${hazard.hazardType} at ${hazard.location}",
                    description = "${hazard.triggerFactor} • Suggested: ${hazard.recommendedMitigation}",
                    tag = "${hazard.probabilityPercent}% RISK",
                    tagColor = if (isHighRisk) CivicRedContainer else CivicAmberContainer,
                    tagTextColor = if (isHighRisk) CivicRed else CivicAmber,
                    onClick = { onAlertClick(hazard.location) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 7. TWO-WAY CIVIC INTELLIGENCE BANNER
        // ==========================================
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CivicCharcoal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CivicOrangePrimary
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Connected CivicFix Ecosystem",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "When citizens report potholes or waterlogging on CivicFix, the AI Road Safety engine dynamically redirects thousands of commuters in real-time.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onSwitchToCivicFix,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Switch to CivicFix Grievance Module 🏙️",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickPlaceChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CivicSlate100,
        border = BorderStroke(1.dp, CivicSlate200),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = CivicSlate800,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun SafetyMetricBox(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CivicSlate100,
        border = BorderStroke(1.dp, CivicSlate200),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = CivicSlate600,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CivicSlate900
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = CivicSlate600,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CivicSlate200),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = accentColor.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = CivicSlate900
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = CivicSlate600
            )
        }
    }
}

@Composable
private fun AlertItemCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    tag: String,
    tagColor: Color,
    tagTextColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CivicSlate200),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = iconTint.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = tagColor
                    ) {
                        Text(
                            text = tag,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = tagTextColor,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = CivicSlate600,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

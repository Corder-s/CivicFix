package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.localization.CivicStrings
import com.example.data.localization.civicString
import com.example.data.models.IssueStatus
import com.example.data.services.EmergencyFacility
import com.example.data.services.IncidentMarker
import com.example.data.services.IncidentSeverity
import com.example.data.services.IncidentType
import com.example.data.services.MapLayerFilter
import com.example.ui.CivicFixViewModel
import com.example.ui.components.CivicButton
import com.example.ui.components.CivicButtonSize
import com.example.ui.components.CivicButtonVariant
import com.example.ui.components.EmergencyModeSheet
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenLight
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicOrangeContainer
import com.example.ui.theme.CivicOrangeDark
import com.example.ui.theme.CivicOrangePrimary
import com.example.ui.theme.CivicRed
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900
import kotlin.math.roundToInt

@Composable
fun CivicLiveMapScreen(
    viewModel: CivicFixViewModel,
    onNavigateToReport: () -> Unit,
    onNavigateToIssueDetail: (String) -> Unit,
    onNavigateToJourneyPlanner: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val filteredMarkers by viewModel.filteredMapMarkers.collectAsState()
    val allMarkers by viewModel.allMapMarkers.collectAsState()
    val activeLayer by viewModel.mapLayerFilter.collectAsState()
    val searchQuery by viewModel.mapSearchQuery.collectAsState()
    val selectedMarker by viewModel.selectedMapMarker.collectAsState()
    val isEmergencyModeActive by viewModel.isEmergencyModeActive.collectAsState()
    val selectedEmergencyFacility by viewModel.selectedEmergencyFacility.collectAsState()
    val emergencyRouteInfo by viewModel.emergencyRouteInfo.collectAsState()

    var showDemoNoticeDialog by remember { mutableStateOf(false) }
    var showHelplinesDialog by remember { mutableStateOf(false) }
    var showDetourDialog by remember { mutableStateOf(false) }
    var detourTargetMarker by remember { mutableStateOf<IncidentMarker?>(null) }

    // Emergency Assist Bottom Sheet
    if (isEmergencyModeActive) {
        EmergencyModeSheet(
            facilities = viewModel.emergencyFacilities,
            helplines = viewModel.emergencyHelplines,
            selectedFacility = selectedEmergencyFacility,
            emergencyRouteInfo = emergencyRouteInfo,
            onSelectFacility = { facility -> viewModel.selectEmergencyFacility(facility) },
            onDismiss = { viewModel.deactivateEmergencyMode() }
        )
    }

    // Map Pan & Zoom State
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    // Demo notice modal
    if (showDemoNoticeDialog) {
        AlertDialog(
            onDismissRequest = { showDemoNoticeDialog = false },
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = CivicOrangePrimary) },
            title = { Text(civicString(CivicStrings.MAP_DEMO_DATA_NOTICE), fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "CivicLive Map blends live citizen-reported grievances from your local device database with demonstration simulated urban grid feeds (such as traffic signals, road construction, bus delays, and weather warnings).\n\nWhen you submit a new civic issue report, it automatically appears as a real-time geo-pinned marker on this map.",
                    fontSize = 14.sp,
                    color = CivicSlate600,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showDemoNoticeDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CivicNavyDark)
                ) {
                    Text(civicString(CivicStrings.DONE))
                }
            }
        )
    }

    // Emergency Helplines Modal
    if (showHelplinesDialog) {
        EmergencyHelplinesDialog(
            helplines = viewModel.emergencyHelplines,
            facilities = viewModel.emergencyFacilities,
            onDismiss = { showHelplinesDialog = false }
        )
    }

    // Detour Advisory Modal
    if (showDetourDialog && detourTargetMarker != null) {
        DetourAdvisoryDialog(
            marker = detourTargetMarker!!,
            onDismiss = { showDetourDialog = false },
            onOpenJourneyPlanner = {
                showDetourDialog = false
                viewModel.setJourneyDestination(detourTargetMarker!!.locationName)
                onNavigateToJourneyPlanner()
            }
        )
    }


    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 768.dp

        if (isTablet) {
            // Split-Pane Layout for Tablet & Wide Displays
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Column: Interactive Map Canvas + Controls (60% width)
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                ) {
                    MapCanvasSection(
                        markers = filteredMarkers,
                        selectedMarker = selectedMarker,
                        zoomScale = zoomScale,
                        panOffsetX = panOffsetX,
                        panOffsetY = panOffsetY,
                        onTransform = { panX, panY, zoom ->
                            panOffsetX = (panOffsetX + panX).coerceIn(-400f, 400f)
                            panOffsetY = (panOffsetY + panY).coerceIn(-400f, 400f)
                            zoomScale = (zoomScale * zoom).coerceIn(0.6f, 3.0f)
                        },
                        onResetView = {
                            panOffsetX = 0f
                            panOffsetY = 0f
                            zoomScale = 1.0f
                        },
                        onZoomIn = { zoomScale = (zoomScale * 1.25f).coerceAtMost(3.0f) },
                        onZoomOut = { zoomScale = (zoomScale * 0.8f).coerceAtLeast(0.6f) },
                        onMarkerClick = { marker -> viewModel.selectMapMarker(marker) },
                        onShowDemoInfo = { showDemoNoticeDialog = true },
                        onShowHelplines = { showHelplinesDialog = true }
                    )

                    // Top Filter Header Overlay
                    MapTopOverlayBar(
                        activeLayer = activeLayer,
                        searchQuery = searchQuery,
                        totalIncidents = filteredMarkers.size,
                        onLayerSelected = { viewModel.setMapLayerFilter(it) },
                        onSearchChange = { viewModel.setMapSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter)
                    )
                }

                // Right Column: Active Incident Details / Dossier & Emergency Rail (40% width)
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {
                        if (selectedMarker != null) {
                            IncidentDetailCard(
                                marker = selectedMarker!!,
                                onDismiss = { viewModel.selectMapMarker(null) },
                                onUpvote = { viewModel.toggleUpvoteMapMarker(selectedMarker!!.id) },
                                onViewGrievance = { issueId -> onNavigateToIssueDetail(issueId) },
                                onDetour = {
                                    detourTargetMarker = selectedMarker
                                    showDetourDialog = true
                                },
                                onReportHere = onNavigateToReport
                            )
                        } else {
                            MapWelcomeOverview(
                                totalIncidents = allMarkers.size,
                                weatherAlert = viewModel.currentWeatherAlert,
                                highRiskCorridors = viewModel.highRiskCorridors,
                                onSelectCategory = { viewModel.setMapLayerFilter(it) },
                                onOpenHelplines = { showHelplinesDialog = true }
                            )
                        }
                    }
                }
            }
        } else {
            // Mobile Vertical Layout: Fullscreen Canvas with Top Filter and Floating Bottom Inspection Dossier
            Box(modifier = Modifier.fillMaxSize()) {
                // Interactive Map Canvas
                MapCanvasSection(
                    markers = filteredMarkers,
                    selectedMarker = selectedMarker,
                    zoomScale = zoomScale,
                    panOffsetX = panOffsetX,
                    panOffsetY = panOffsetY,
                    onTransform = { panX, panY, zoom ->
                        panOffsetX = (panOffsetX + panX).coerceIn(-400f, 400f)
                        panOffsetY = (panOffsetY + panY).coerceIn(-400f, 400f)
                        zoomScale = (zoomScale * zoom).coerceIn(0.6f, 3.0f)
                    },
                    onResetView = {
                        panOffsetX = 0f
                        panOffsetY = 0f
                        zoomScale = 1.0f
                    },
                    onZoomIn = { zoomScale = (zoomScale * 1.25f).coerceAtMost(3.0f) },
                    onZoomOut = { zoomScale = (zoomScale * 0.8f).coerceAtLeast(0.6f) },
                    onMarkerClick = { marker -> viewModel.selectMapMarker(marker) },
                    onShowDemoInfo = { showDemoNoticeDialog = true },
                    onShowHelplines = { showHelplinesDialog = true }
                )

                // Top Floating Search & Filter Bar
                MapTopOverlayBar(
                    activeLayer = activeLayer,
                    searchQuery = searchQuery,
                    totalIncidents = filteredMarkers.size,
                    onLayerSelected = { viewModel.setMapLayerFilter(it) },
                    onSearchChange = { viewModel.setMapSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .align(Alignment.TopCenter)
                )

                // Bottom Floating Incident Detail Bottom Sheet
                androidx.compose.animation.AnimatedVisibility(
                    visible = selectedMarker != null,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                ) {
                    selectedMarker?.let { marker ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp,
                            shadowElevation = 12.dp,
                            border = BorderStroke(1.dp, CivicSlate200),
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                IncidentDetailCard(
                                    marker = marker,
                                    onDismiss = { viewModel.selectMapMarker(null) },
                                    onUpvote = { viewModel.toggleUpvoteMapMarker(marker.id) },
                                    onViewGrievance = { issueId -> onNavigateToIssueDetail(issueId) },
                                    onDetour = {
                                        detourTargetMarker = marker
                                        showDetourDialog = true
                                    },
                                    onReportHere = onNavigateToReport
                                )
                            }
                        }
                    }
                }

                // Floating Action Buttons: Plan Safe Journey & Report Hazard (Visible when no marker selected)
                if (selectedMarker == null) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FloatingActionButton(
                            onClick = onNavigateToJourneyPlanner,
                            containerColor = CivicNavyDark,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.testTag("map_plan_journey_fab")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = "Plan Safe Route", tint = CivicGreenLight, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Plan Safe Route", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }
                        }

                        FloatingActionButton(
                            onClick = onNavigateToReport,
                            containerColor = CivicOrangePrimary,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.testTag("map_report_hazard_fab")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Report Hazard", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(civicString(CivicStrings.REPORT_ISSUE), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

            }
        }
    }
}

/**
 * Top Overlay with Search Bar & Layer Chips
 */
@Composable
private fun MapTopOverlayBar(
    activeLayer: MapLayerFilter,
    searchQuery: String,
    totalIncidents: Int,
    onLayerSelected: (MapLayerFilter) -> Unit,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, CivicSlate200),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Row: Title & Live Incident Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CivicOrangePrimary,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                    Column {
                        Text(
                            text = civicString(CivicStrings.CIVIC_LIVE_MAP),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicNavyDark
                        )
                        Text(
                            text = civicString(CivicStrings.MAP_SUBTITLE),
                            fontSize = 11.sp,
                            color = CivicSlate600
                        )
                    }
                }

                // Live Active Count Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CivicOrangeContainer
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(CivicOrangeDark)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$totalIncidents Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicOrangeDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(civicString(CivicStrings.MAP_SEARCH_HINT), fontSize = 13.sp, color = CivicSlate400)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = CivicSlate600, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = CivicSlate600, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CivicNavyDark,
                    unfocusedBorderColor = CivicSlate200,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = CivicSlate100.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("map_search_field")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Scrollable Layer Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MapLayerFilter.entries.forEach { layer ->
                    val isSelected = layer == activeLayer
                    FilterChip(
                        selected = isSelected,
                        onClick = { onLayerSelected(layer) },
                        label = {
                            Text(
                                text = when (layer) {
                                    MapLayerFilter.ALL -> civicString(CivicStrings.MAP_LAYER_ALL)
                                    MapLayerFilter.HAZARDS -> civicString(CivicStrings.MAP_LAYER_HAZARDS)
                                    MapLayerFilter.POTHOLES -> civicString(CivicStrings.MAP_LAYER_POTHOLES)
                                    MapLayerFilter.WATERLOGGING -> civicString(CivicStrings.MAP_LAYER_WATERLOGGING)
                                    MapLayerFilter.CONSTRUCTION -> civicString(CivicStrings.MAP_LAYER_CONSTRUCTION)
                                    MapLayerFilter.TRANSIT -> civicString(CivicStrings.MAP_LAYER_TRANSIT)
                                    MapLayerFilter.EMERGENCY -> civicString(CivicStrings.MAP_LAYER_EMERGENCY)
                                    MapLayerFilter.WEATHER -> civicString(CivicStrings.MAP_LAYER_WEATHER)
                                },
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            val iconVector = when (layer) {
                                MapLayerFilter.ALL -> Icons.Default.Layers
                                MapLayerFilter.HAZARDS -> Icons.Default.Warning
                                MapLayerFilter.POTHOLES -> Icons.Default.Traffic
                                MapLayerFilter.WATERLOGGING -> Icons.Default.Waves
                                MapLayerFilter.CONSTRUCTION -> Icons.Default.Engineering
                                MapLayerFilter.TRANSIT -> Icons.Default.DirectionsBus
                                MapLayerFilter.EMERGENCY -> Icons.Default.LocalHospital
                                MapLayerFilter.WEATHER -> Icons.Default.Thunderstorm
                            }
                            Icon(iconVector, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CivicNavyDark,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White,
                            containerColor = CivicSlate100,
                            labelColor = CivicSlate800
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }
    }
}

/**
 * Interactive Vector / Canvas Urban Grid Map
 */
@Composable
private fun MapCanvasSection(
    markers: List<IncidentMarker>,
    selectedMarker: IncidentMarker?,
    zoomScale: Float,
    panOffsetX: Float,
    panOffsetY: Float,
    onTransform: (panX: Float, panY: Float, zoom: Float) -> Unit,
    onResetView: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onMarkerClick: (IncidentMarker) -> Unit,
    onShowDemoInfo: () -> Unit,
    onShowHelplines: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEBF1F5)) // Clean Map canvas neutral tint
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    onTransform(pan.x, pan.y, zoom)
                }
            }
    ) {
        // Render Urban Geographic Canvas (Roads, Transit Tracks, Waterways, Green Parks)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2 + panOffsetX
            val centerY = height / 2 + panOffsetY

            // 1. Draw River / Water Channel
            val waterPath = Path().apply {
                moveTo(centerX - 350f * zoomScale, centerY + 300f * zoomScale)
                cubicTo(
                    centerX - 100f * zoomScale, centerY + 180f * zoomScale,
                    centerX + 150f * zoomScale, centerY + 360f * zoomScale,
                    centerX + 400f * zoomScale, centerY + 220f * zoomScale
                )
            }
            drawPath(
                path = waterPath,
                color = Color(0xFFB3E5FC),
                style = Stroke(width = 38f * zoomScale)
            )

            // 2. Draw Green Urban Parks / Natural Zones
            drawCircle(
                color = Color(0xFFC8E6C9).copy(alpha = 0.6f),
                radius = 120f * zoomScale,
                center = Offset(centerX - 160f * zoomScale, centerY - 140f * zoomScale)
            )
            drawCircle(
                color = Color(0xFFC8E6C9).copy(alpha = 0.5f),
                radius = 90f * zoomScale,
                center = Offset(centerX + 200f * zoomScale, centerY - 120f * zoomScale)
            )

            // 3. Major Arterial & Ring Roads (Primary Grid)
            // Outer Ring Road (Ellipse)
            drawOval(
                color = Color(0xFFFFFFFF),
                topLeft = Offset(centerX - 280f * zoomScale, centerY - 240f * zoomScale),
                size = androidx.compose.ui.geometry.Size(560f * zoomScale, 480f * zoomScale),
                style = Stroke(width = 18f * zoomScale)
            )
            drawOval(
                color = Color(0xFFFFD54F), // Amber Center Line
                topLeft = Offset(centerX - 280f * zoomScale, centerY - 240f * zoomScale),
                size = androidx.compose.ui.geometry.Size(560f * zoomScale, 480f * zoomScale),
                style = Stroke(
                    width = 2.5f * zoomScale,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
            )

            // Central Arterials (Horizontal & Vertical Crossroads)
            // East-West Expressway
            drawLine(
                color = Color.White,
                start = Offset(centerX - 500f * zoomScale, centerY - 20f * zoomScale),
                end = Offset(centerX + 500f * zoomScale, centerY + 40f * zoomScale),
                strokeWidth = 20f * zoomScale
            )
            // North-South Arterial
            drawLine(
                color = Color.White,
                start = Offset(centerX + 10f * zoomScale, centerY - 450f * zoomScale),
                end = Offset(centerX - 30f * zoomScale, centerY + 450f * zoomScale),
                strokeWidth = 20f * zoomScale
            )

            // Diagonal Secondary Corridors
            drawLine(
                color = Color(0xFFFFFFFF),
                start = Offset(centerX - 350f * zoomScale, centerY - 300f * zoomScale),
                end = Offset(centerX + 350f * zoomScale, centerY + 300f * zoomScale),
                strokeWidth = 12f * zoomScale
            )
            drawLine(
                color = Color(0xFFFFFFFF),
                start = Offset(centerX + 350f * zoomScale, centerY - 280f * zoomScale),
                end = Offset(centerX - 350f * zoomScale, centerY + 280f * zoomScale),
                strokeWidth = 12f * zoomScale
            )

            // 4. Public Transit Dedicated Metro & Bus Line (Dashed Blue Line)
            val metroPath = Path().apply {
                moveTo(centerX - 420f * zoomScale, centerY + 120f * zoomScale)
                lineTo(centerX - 60f * zoomScale, centerY - 100f * zoomScale)
                lineTo(centerX + 340f * zoomScale, centerY - 60f * zoomScale)
            }
            drawPath(
                path = metroPath,
                color = Color(0xFF3F51B5),
                style = Stroke(
                    width = 4.5f * zoomScale,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 12f), 0f)
                )
            )
        }

        // Overlay Map Marker Pins
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val canvasW = maxWidth.value
            val canvasH = maxHeight.value
            val centerX = canvasW / 2 + (panOffsetX / 2.5f)
            val centerY = canvasH / 2 + (panOffsetY / 2.5f)

            // Fixed Reference coordinates for Delhi-NCR Geo anchor (28.6139, 77.2090)
            val refLat = 28.6139
            val refLng = 77.2090
            val coordScale = 2200.0 * zoomScale.toDouble()

            markers.forEachIndexed { index, marker ->
                val dLat = ((marker.latitude - refLat) * coordScale).toFloat()
                val dLng = ((marker.longitude - refLng) * coordScale).toFloat()

                // Position offset relative to center with gentle layout distribution
                val posX = (centerX + dLng + ((index % 4 - 1.5f) * 45f * zoomScale)).coerceIn(24f, (canvasW - 60f).coerceAtLeast(24f))
                val posY = (centerY - dLat + (((index / 4) % 3 - 1f) * 50f * zoomScale)).coerceIn(120f, (canvasH - 120f).coerceAtLeast(120f))

                val isSelected = selectedMarker?.id == marker.id

                MapPinMarker(
                    marker = marker,
                    isSelected = isSelected,
                    onClick = { onMarkerClick(marker) },
                    modifier = Modifier
                        .offset { IntOffset(posX.roundToInt(), posY.roundToInt()) }
                )
            }
        }

        // Floating Map Controls (Zoom +, Zoom -, Recenter, Info)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                onClick = onZoomIn,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, CivicSlate200),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("+", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CivicNavyDark)
                }
            }

            Surface(
                onClick = onZoomOut,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, CivicSlate200),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("−", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CivicNavyDark)
                }
            }

            Surface(
                onClick = onResetView,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, CivicSlate200),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Center", tint = CivicGreenPrimary, modifier = Modifier.size(20.dp))
                }
            }

            Surface(
                onClick = onShowHelplines,
                shape = CircleShape,
                color = CivicRed,
                shadowElevation = 6.dp,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Phone, contentDescription = "SOS Helplines", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Bottom Left: "Demonstration & Local Grid" Pill Indicator
        Surface(
            onClick = onShowDemoInfo,
            shape = RoundedCornerShape(20.dp),
            color = CivicNavyDark.copy(alpha = 0.92f),
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = CivicAmber, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = civicString(CivicStrings.MAP_DEMO_DATA_NOTICE),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Custom Pin Marker with Incident-Specific Icon, Color, and Pulse Animation
 */
@Composable
private fun MapPinMarker(
    marker: IncidentMarker,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pinColor = marker.type.defaultColor

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("map_marker_${marker.id}")
    ) {
        // Selected callout tooltip
        if (isSelected) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CivicNavyDark,
                shadowElevation = 6.dp,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = marker.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Pin Bubble
        Surface(
            shape = CircleShape,
            color = if (isSelected) CivicNavyDark else pinColor,
            border = BorderStroke(
                width = if (isSelected) 3.dp else 2.dp,
                color = if (isSelected) CivicOrangePrimary else Color.White
            ),
            shadowElevation = if (isSelected) 10.dp else 4.dp,
            modifier = Modifier.size(if (isSelected) 38.dp else 30.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                val iconVector: ImageVector = when (marker.type) {
                    IncidentType.POTHOLE -> Icons.Default.Traffic
                    IncidentType.WATERLOGGING -> Icons.Default.Waves
                    IncidentType.ROAD_DAMAGE -> Icons.Default.Build
                    IncidentType.ACCIDENT -> Icons.Default.Report
                    IncidentType.CONSTRUCTION -> Icons.Default.Engineering
                    IncidentType.ROAD_CLOSURE -> Icons.Default.Close
                    IncidentType.FALLEN_TREE -> Icons.Default.Park
                    IncidentType.BROKEN_SIGNAL -> Icons.Default.Traffic
                    IncidentType.BROKEN_STREETLIGHT -> Icons.Default.Lightbulb
                    IncidentType.WEATHER_HAZARD -> Icons.Default.Thunderstorm
                    IncidentType.HIGH_RISK_ROAD -> Icons.Default.Warning
                    IncidentType.EMERGENCY_FACILITY -> Icons.Default.LocalHospital
                    IncidentType.BUS_STOP -> Icons.Default.DirectionsBus
                    IncidentType.CIVIC_COMPLAINT -> Icons.Default.Report
                    IncidentType.OTHER -> Icons.Default.Info
                }
                Icon(
                    imageVector = iconVector,
                    contentDescription = marker.type.displayName,
                    tint = Color.White,
                    modifier = Modifier.size(if (isSelected) 20.dp else 16.dp)
                )
            }
        }

        // Tiny pointer tail
        Box(
            modifier = Modifier
                .size(width = 6.dp, height = 4.dp)
                .background(if (isSelected) CivicNavyDark else pinColor, shape = RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
        )
    }
}

/**
 * Rich Incident Detail Dossier Card
 */
@Composable
private fun IncidentDetailCard(
    marker: IncidentMarker,
    onDismiss: () -> Unit,
    onUpvote: () -> Unit,
    onViewGrievance: (String) -> Unit,
    onDetour: () -> Unit,
    onReportHere: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Top Bar: Category Pill + Close Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = marker.type.defaultColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = marker.type.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = marker.type.defaultColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Severity Pill
                val severityColor = when (marker.severity) {
                    IncidentSeverity.CRITICAL -> CivicRed
                    IncidentSeverity.HIGH -> CivicOrangeDark
                    IncidentSeverity.MEDIUM -> CivicAmber
                    IncidentSeverity.LOW -> CivicGreenDark
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = severityColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${marker.severity.displayName} Severity",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = severityColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = CivicSlate600)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Title
        Text(
            text = marker.title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = CivicNavyDark
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Location & Landmark
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = CivicOrangePrimary, modifier = Modifier.size(16.dp))
            Text(
                text = marker.locationName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = CivicSlate800
            )
        }
        Text(
            text = marker.address,
            fontSize = 12.sp,
            color = CivicSlate600,
            modifier = Modifier.padding(start = 20.dp, top = 2.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        Text(
            text = marker.description,
            fontSize = 13.sp,
            color = CivicSlate800,
            lineHeight = 18.sp
        )

        // Photo if attached
        if (!marker.photoUri.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            AsyncImage(
                model = marker.photoUri,
                contentDescription = "Hazard Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CivicSlate200, RoundedCornerShape(12.dp))
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Key Mobility Metrics Grid (Safety Score, Road Condition, Weather Risk, Bus Impact)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = CivicSlate100,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Safety Score
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = if (marker.safetyScore < 40) CivicRed else CivicGreenPrimary, modifier = Modifier.size(16.dp))
                        Text(civicString(CivicStrings.MAP_SAFETY_SCORE), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicNavyDark)
                    }
                    Text(
                        text = "${marker.safetyScore} / 100",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (marker.safetyScore < 40) CivicRed else if (marker.safetyScore < 70) CivicAmber else CivicGreenDark
                    )
                }

                // Road Condition
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Traffic, contentDescription = null, tint = CivicSlate600, modifier = Modifier.size(16.dp))
                        Text(civicString(CivicStrings.MAP_ROAD_CONDITION), fontSize = 12.sp, color = CivicSlate600)
                    }
                    Text(
                        text = marker.roadCondition,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = CivicSlate900,
                        textAlign = TextAlign.End,
                        modifier = Modifier.widthIn(max = 180.dp)
                    )
                }

                // Weather Risk
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Thunderstorm, contentDescription = null, tint = CivicSlate600, modifier = Modifier.size(16.dp))
                        Text(civicString(CivicStrings.MAP_WEATHER_RISK), fontSize = 12.sp, color = CivicSlate600)
                    }
                    Text(
                        text = marker.weatherRisk,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = CivicSlate900,
                        textAlign = TextAlign.End,
                        modifier = Modifier.widthIn(max = 180.dp)
                    )
                }

                // Affected Bus Routes
                if (marker.affectedBusRoutes.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = Color(0xFF5E35B1), modifier = Modifier.size(16.dp))
                            Text(civicString(CivicStrings.MAP_AFFECTED_BUSES), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5E35B1))
                        }
                        marker.affectedBusRoutes.forEach { route ->
                            Text(
                                text = "• $route",
                                fontSize = 12.sp,
                                color = CivicSlate800,
                                modifier = Modifier.padding(start = 22.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Confirm / Upvote Button
            Button(
                onClick = onUpvote,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (marker.hasUserUpvoted) CivicGreenPrimary else CivicSlate100,
                    contentColor = if (marker.hasUserUpvoted) Color.White else CivicNavyDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("${marker.upvotes} Confirmed", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // Safe Detour Button
            OutlinedButton(
                onClick = onDetour,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CivicOrangeDark),
                border = BorderStroke(1.dp, CivicOrangePrimary),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(civicString(CivicStrings.MAP_AVOID_HAZARD), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // View Linked Grievance if it exists
        if (!marker.relatedCivicIssueId.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            CivicButton(
                text = "${civicString(CivicStrings.MAP_VIEW_INCIDENT_REPORT)} (${marker.relatedCivicIssueId})",
                onClick = { onViewGrievance(marker.relatedCivicIssueId) },
                variant = CivicButtonVariant.SECONDARY,
                size = CivicButtonSize.SMALL,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Overview Welcome Dossier shown on tablet split screen when no single pin is selected
 */
@Composable
private fun MapWelcomeOverview(
    totalIncidents: Int,
    weatherAlert: com.example.data.services.WeatherSafetyAlert,
    highRiskCorridors: List<com.example.data.services.RoadRiskSegment>,
    onSelectCategory: (MapLayerFilter) -> Unit,
    onOpenHelplines: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Urban Mobility Live Dossier",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = CivicNavyDark
        )
        Text(
            text = civicString(CivicStrings.MAP_TAP_MARKER_HINT),
            fontSize = 13.sp,
            color = CivicSlate600,
            lineHeight = 18.sp
        )

        // Weather Alert Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFE1F5FE),
            border = BorderStroke(1.dp, Color(0xFF81D4FA)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Thunderstorm, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(18.dp))
                    Text(weatherAlert.headline, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF01579B))
                }
                Text(weatherAlert.advisoryText, fontSize = 12.sp, color = CivicSlate800)
            }
        }

        // High Risk Road Corridors
        Text(
            text = "High-Risk Road Corridors",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = CivicNavyDark
        )
        highRiskCorridors.forEach { corridor ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CivicSlate100,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(corridor.roadName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CivicNavyDark)
                        Text(corridor.primaryHazard, fontSize = 11.sp, color = CivicSlate600)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (corridor.safetyScore < 40) CivicRed.copy(alpha = 0.15f) else CivicAmber.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${corridor.safetyScore}/100",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (corridor.safetyScore < 40) CivicRed else CivicOrangeDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // SOS Button
        Button(
            onClick = onOpenHelplines,
            colors = ButtonDefaults.buttonColors(containerColor = CivicRed),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Emergency Helplines & SOS", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

/**
 * Emergency Helplines Dialog
 */
@Composable
private fun EmergencyHelplinesDialog(
    helplines: Map<String, String>,
    facilities: List<EmergencyFacility>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.LocalHospital, contentDescription = null, tint = CivicRed)
                Text(civicString(CivicStrings.MAP_EMERGENCY_HELPLINES), fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                helplines.forEach { (name, number) ->
                    Surface(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = CivicSlate100,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CivicNavyDark)
                            Text(number, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CivicRed)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text("Nearby 24/7 Trauma & Emergency Units:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CivicNavyDark)
                facilities.forEach { facility ->
                    Text("• ${facility.name} (${facility.distanceKm} km) - ${facility.helpline}", fontSize = 12.sp, color = CivicSlate800)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CivicNavyDark)
            ) {
                Text(civicString(CivicStrings.CLOSE))
            }
        }
    )
}

/**
 * Detour Advisory Dialog
 */
@Composable
private fun DetourAdvisoryDialog(
    marker: IncidentMarker,
    onDismiss: () -> Unit,
    onOpenJourneyPlanner: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Navigation, contentDescription = null, tint = CivicOrangePrimary) },
        title = { Text(civicString(CivicStrings.MAP_AVOID_HAZARD), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Hazard Zone: ${marker.title} at ${marker.locationName}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicNavyDark
                )
                Text(
                    "Recommended Safe Detour: Take the elevated Outer Ring Road or bypass via Central Avenue. Avoid lower underpass lanes where standing water and construction debris are reported.",
                    fontSize = 13.sp,
                    color = CivicSlate600,
                    lineHeight = 18.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onOpenJourneyPlanner,
                colors = ButtonDefaults.buttonColors(containerColor = CivicGreenPrimary)
            ) {
                Text("Plan Safe Journey", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(civicString(CivicStrings.CLOSE))
            }
        }
    )
}

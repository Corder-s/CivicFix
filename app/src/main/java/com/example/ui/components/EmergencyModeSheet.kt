package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.services.EmergencyFacility
import com.example.data.services.EmergencyRouteInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyModeSheet(
    facilities: List<EmergencyFacility>,
    helplines: Map<String, String>,
    selectedFacility: EmergencyFacility?,
    emergencyRouteInfo: EmergencyRouteInfo?,
    onSelectFacility: (EmergencyFacility) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("emergency_mode_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header Banner with SOS pulsing accent
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD32F2F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = "Emergency Mode Active",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "EMERGENCY ASSIST",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFD32F2F)
                        )
                        Text(
                            text = "Priority Hospital & SOS Emergency Routing",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("emergency_sheet_close_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Emergency Sheet")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mandatory Safety Disclaimer Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Emergency Disclaimer",
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Real-time emergency routing and accredited responder helplines. No automated government dispatch has been made. Call 112 or local responders directly in critical emergencies.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = Color(0xFF5D4037)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fast SOS Helplines Row
            Text(
                text = "⚡ Instant Emergency Helplines (Direct Dial)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val priorityHelplines = listOf(
                    Triple("112", "National SOS", Color(0xFFD32F2F)),
                    Triple("102", "Ambulance", Color(0xFFC2185B)),
                    Triple("100", "Police", Color(0xFF1976D2)),
                    Triple("101", "Fire Rescue", Color(0xFFE65100)),
                    Triple("1095", "Traffic SOS", Color(0xFF455A64))
                )
                items(priorityHelplines) { (number, label, color) ->
                    EmergencyCallChip(
                        number = number,
                        label = label,
                        color = color,
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                            context.startActivity(intent)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Share Location SOS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "🚨 EMERGENCY SOS ALERT: I need immediate assistance. My coordinates: 28.6139° N, 77.2090° E. Please contact emergency services or reach out."
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Broadcast Emergency Location"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_sos_location_button")
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share SOS Location", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nearby Emergency Centers Section
            Text(
                text = "🏥 Nearby Verified Emergency Centers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 340.dp)
            ) {
                items(facilities) { facility ->
                    EmergencyFacilityCard(
                        facility = facility,
                        isSelected = selectedFacility?.id == facility.id,
                        onSelect = { onSelectFacility(facility) },
                        onCall = {
                            val num = facility.helpline.split("/").firstOrNull()?.trim() ?: "112"
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num"))
                            context.startActivity(intent)
                        }
                    )
                }

                // Show Emergency Route Details if calculated
                if (emergencyRouteInfo != null) {
                    item {
                        EmergencyRouteDetailsCard(routeInfo = emergencyRouteInfo)
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun EmergencyCallChip(
    number: String,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = Modifier.testTag("emergency_chip_$number")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(text = number, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = color)
                Text(text = label, style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun EmergencyFacilityCard(
    facility: EmergencyFacility,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onCall: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFFD32F2F) else MaterialTheme.colorScheme.outlineVariant
    val bgColor = if (isSelected) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("emergency_facility_${facility.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1976D2).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (facility.type.contains("Police", ignoreCase = true)) Icons.Default.LocalPolice else Icons.Default.LocalHospital,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = facility.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "${facility.type} • ${facility.distanceKm} km away",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = facility.address,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onCall,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32).copy(alpha = 0.12f))
                    .size(38.dp)
                    .testTag("call_facility_${facility.id}")
            ) {
                Icon(imageVector = Icons.Default.Call, contentDescription = "Call ${facility.name}", tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun EmergencyRouteDetailsCard(routeInfo: EmergencyRouteInfo) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .testTag("emergency_route_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Navigation, contentDescription = null, tint = Color(0xFFD32F2F))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Emergency Priority Route",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "ETA ~${routeInfo.estimatedTimeMinutes} mins",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = routeInfo.safetyExplanation,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Step by step corridor checkpoints
            routeInfo.routeSteps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

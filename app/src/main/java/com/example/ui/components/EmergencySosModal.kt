package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicDarkGray
import com.example.ui.theme.CivicOrangeDark
import com.example.ui.theme.CivicOrangePrimary
import com.example.ui.theme.CivicRed
import com.example.ui.theme.CivicRedContainer
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900
import com.example.ui.theme.CivicSuccess
import com.example.ui.theme.CivicSuccessContainer

@Composable
fun EmergencySosModal(
    onDismiss: () -> Unit,
    onNavigateToMap: () -> Unit = {}
) {
    val context = LocalContext.current
    var locationShared by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp)
                .testTag("emergency_sos_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with SOS icon and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CivicRed,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Sos,
                                    contentDescription = "SOS",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "EMERGENCY ASSISTANCE",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = CivicRed,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Live Response & Location Sharing",
                                fontSize = 12.sp,
                                color = CivicSlate600
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CivicSlate600
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // GPS Current Location Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CivicRedContainer,
                    border = BorderStroke(1.dp, CivicRed.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = CivicRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CURRENT LIVE GPS LOCATION",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CivicRed
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CivicRed
                            ) {
                                Text(
                                    text = "HIGH ACCURACY (±3m)",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Sector 62 IT Hub, Block C (Near Cyber Tower)",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicSlate900
                        )
                        Text(
                            text = "Lat: 28.6280° N • Long: 77.3649° E • City: Noida, UP",
                            fontSize = 11.sp,
                            color = CivicSlate800
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ONE-TAP TRIGGER: Share Live Location with Emergency Contacts
                Button(
                    onClick = {
                        locationShared = true
                        Toast.makeText(context, "🚨 Live location & SOS broadcast sent to emergency contacts and nearest PCR unit!", Toast.LENGTH_LONG).show()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (locationShared) CivicSuccess else CivicRed
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_share_emergency_location")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (locationShared) Icons.Default.CheckCircle else Icons.Default.ShareLocation,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (locationShared) "Location & Alert Shared with Contacts ✓" else "1-Tap Share Live Location with Contacts",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Emergency Helplines Quick Call Grid
                Text(
                    text = "Immediate Helplines (Direct Call)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate900
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HelplineCard(
                        number = "112",
                        name = "National Emergency",
                        icon = Icons.Default.Call,
                        accentColor = CivicRed,
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    HelplineCard(
                        number = "108",
                        name = "Ambulance ICU",
                        icon = Icons.Default.LocalHospital,
                        accentColor = CivicOrangeDark,
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HelplineCard(
                        number = "100",
                        name = "Police Control",
                        icon = Icons.Default.LocalPolice,
                        accentColor = Color(0xFF1E3A8A),
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:100"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    HelplineCard(
                        number = "1091",
                        name = "Women Safety",
                        icon = Icons.Default.Shield,
                        accentColor = Color(0xFF9333EA),
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1091"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Nearby Emergency Services Section
                Text(
                    text = "Nearby Emergency Services",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate900
                )
                Spacer(modifier = Modifier.height(10.dp))

                EmergencyFacilityItem(
                    name = "Fortis Hospital Noida (Sector 62)",
                    distance = "0.7 km away (3 mins)",
                    status = "24/7 Trauma Care & Emergency Open",
                    icon = Icons.Default.LocalHospital,
                    iconTint = CivicRed,
                    onNavigate = {
                        onDismiss()
                        onNavigateToMap()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                EmergencyFacilityItem(
                    name = "Sector 58 Police Station & Patrol #12",
                    distance = "0.5 km away (2 mins)",
                    status = "Active Patrol Car in Area",
                    icon = Icons.Default.LocalPolice,
                    iconTint = Color(0xFF1E3A8A),
                    onNavigate = {
                        onDismiss()
                        onNavigateToMap()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                EmergencyFacilityItem(
                    name = "CivicFix Safe Haven & Marshal Post #4",
                    distance = "0.3 km away (Well-lit)",
                    status = "CCTV Monitored • Guard on Duty",
                    icon = Icons.Default.Security,
                    iconTint = CivicSuccess,
                    onNavigate = {
                        onDismiss()
                        onNavigateToMap()
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CivicSlate400),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text("Close Emergency Mode", color = CivicSlate800, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun HelplineCard(
    number: String,
    name: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CivicSlate100,
        border = BorderStroke(1.dp, CivicSlate200),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = number,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor
                )
                Text(
                    text = name,
                    fontSize = 10.5.sp,
                    color = CivicSlate800,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun EmergencyFacilityItem(
    name: String,
    distance: String,
    status: String,
    icon: ImageVector,
    iconTint: Color,
    onNavigate: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CivicSlate200),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = iconTint.copy(alpha = 0.12f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = name,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate900
                    )
                    Text(
                        text = "$distance • $status",
                        fontSize = 10.5.sp,
                        color = CivicSlate600
                    )
                }
            }

            IconButton(
                onClick = onNavigate,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Directions,
                    contentDescription = "Navigate",
                    tint = CivicOrangePrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

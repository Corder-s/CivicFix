package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.AppLanguage
import com.example.data.models.CivicIssue
import com.example.data.models.User
import com.example.ui.AccessibilitySettings
import com.example.ui.AppLockSettings
import com.example.ui.AppThemeMode
import com.example.ui.CivicFixViewModel
import com.example.ui.NotificationSettings
import com.example.ui.PrivacySettings
import com.example.ui.TextScaleOption
import com.example.ui.components.CivicStatusBadge
import com.example.ui.components.HelpAndFeedbackDialog
import com.example.ui.components.LanguageSelectionDialog
import com.example.ui.components.LogoutConfirmationDialog
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicDarkGray
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicOrangeDark
import com.example.ui.theme.CivicOrangeLight
import com.example.ui.theme.CivicOrangePrimary
import com.example.ui.theme.CivicRed
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * WhatsApp / Instagram Style Profile & Settings Screen
 * All settings, civic records, language switcher, appearance, accessibility, privacy, app lock, and account options arranged in clean list formats.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenProfileScreen(
    viewModel: CivicFixViewModel,
    user: User?,
    userIssues: List<CivicIssue>,
    onSelectIssue: (String) -> Unit,
    onSwitchToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val currentThemeMode by viewModel.themeMode.collectAsState()
    val notificationSettings by viewModel.notificationSettings.collectAsState()
    val textScale by viewModel.textScale.collectAsState()
    val accessibilitySettings by viewModel.accessibilitySettings.collectAsState()
    val privacySettings by viewModel.privacySettings.collectAsState()
    val appLockSettings by viewModel.appLockSettings.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePhoneDialog by remember { mutableStateOf(false) }
    var showChangeEmailDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showTextSizeDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showAccessibilityDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAppLockDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var helpDialogInitialTab by remember { mutableIntStateOf(0) }
    var showLogoutModal by remember { mutableStateOf(false) }
    var showMyIssuesSheet by remember { mutableStateOf(false) }
    var isLoggingOut by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // 1. Edit Profile Dialog
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(user?.name ?: "Rahul Sharma") }
        var editEmail by remember { mutableStateOf(user?.email ?: "rahul.sharma@example.com") }
        var editPhone by remember { mutableStateOf(user?.phone ?: "+91 98765 43210") }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = "Edit Profile",
                    fontWeight = FontWeight.Bold,
                    color = CivicDarkGray
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CivicSlate600) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CivicSlate600) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = CivicSlate600) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            viewModel.updateUserProfile(editName, editEmail, editPhone)
                            showEditProfileDialog = false
                        } else {
                            viewModel.showToast("Name cannot be empty")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivicOrangePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showEditProfileDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. Change Mobile Number Dialog
    if (showChangePhoneDialog) {
        var newPhone by remember { mutableStateOf(user?.phone ?: "+91 98765 43210") }
        var phoneError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showChangePhoneDialog = false },
            title = {
                Text(
                    text = "Change Mobile Number",
                    fontWeight = FontWeight.Bold,
                    color = CivicDarkGray
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter your new 10-digit mobile number for SMS dispatch alerts and verification.",
                        fontSize = 12.5.sp,
                        color = CivicSlate600
                    )
                    OutlinedTextField(
                        value = newPhone,
                        onValueChange = {
                            newPhone = it
                            phoneError = null
                        },
                        label = { Text("Mobile Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = CivicOrangePrimary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    phoneError?.let {
                        Text(text = it, color = CivicRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPhone.trim().length >= 10) {
                            viewModel.updateUserProfile(user?.name ?: "Rahul Sharma", user?.email ?: "citizen@example.com", newPhone.trim())
                            showChangePhoneDialog = false
                        } else {
                            phoneError = "Please enter a valid mobile number."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivicOrangePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Update Number", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showChangePhoneDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // 3. Change Account/Email Dialog
    if (showChangeEmailDialog) {
        var newEmail by remember { mutableStateOf(user?.email ?: "rahul.sharma@example.com") }
        var emailError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showChangeEmailDialog = false },
            title = {
                Text(
                    text = "Change Account / Email",
                    fontWeight = FontWeight.Bold,
                    color = CivicDarkGray
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Update your primary email address for official grievance receipts and account recovery.",
                        fontSize = 12.5.sp,
                        color = CivicSlate600
                    )
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = {
                            newEmail = it
                            emailError = null
                        },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CivicOrangePrimary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    emailError?.let {
                        Text(text = it, color = CivicRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = newEmail.trim()
                        if (trimmed.contains("@") && trimmed.contains(".")) {
                            viewModel.updateUserProfile(user?.name ?: "Rahul Sharma", trimmed, user?.phone ?: "+91 98765 43210")
                            showChangeEmailDialog = false
                        } else {
                            emailError = "Please enter a valid email address."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivicOrangePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Update Email", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showChangeEmailDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // 4. Change Password Dialog
    if (showPasswordDialog) {
        var currentPass by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("") }
        var confirmPass by remember { mutableStateOf("") }
        var passError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = {
                Text(
                    text = "Change Password",
                    fontWeight = FontWeight.Bold,
                    color = CivicDarkGray
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = currentPass,
                        onValueChange = {
                            currentPass = it
                            passError = null
                        },
                        label = { Text("Current Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = {
                            newPass = it
                            passError = null
                        },
                        label = { Text("New Password (min 6 chars)") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = {
                            confirmPass = it
                            passError = null
                        },
                        label = { Text("Confirm New Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    passError?.let {
                        Text(
                            text = it,
                            color = CivicRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentPass.isBlank() || newPass.isBlank()) {
                            passError = "Please fill in all password fields."
                        } else if (newPass.length < 6) {
                            passError = "Password must be at least 6 characters."
                        } else if (newPass != confirmPass) {
                            passError = "New passwords do not match."
                        } else {
                            val success = viewModel.changePassword(currentPass, newPass)
                            if (success) {
                                showPasswordDialog = false
                            } else {
                                passError = "Current password was incorrect."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivicOrangePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Update Password", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showPasswordDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // 5. Appearance / Theme Mode Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = CivicOrangePrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("App Appearance", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppThemeMode.entries.forEach { mode ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (currentThemeMode == mode) CivicOrangeLight else CivicSlate100,
                            border = BorderStroke(1.dp, if (currentThemeMode == mode) CivicOrangePrimary else CivicSlate200),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = currentThemeMode == mode,
                                    onClick = {
                                        viewModel.setThemeMode(mode)
                                        showThemeDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = CivicOrangePrimary)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = mode.displayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = CivicDarkGray
                                    )
                                    Text(
                                        text = when(mode) {
                                            AppThemeMode.SYSTEM -> "Match device system theme automatically"
                                            AppThemeMode.LIGHT -> "High contrast clean daytime interface"
                                            AppThemeMode.DARK -> "Dimmed battery-efficient dark palette"
                                        },
                                        fontSize = 11.sp,
                                        color = CivicSlate600
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close", color = CivicOrangePrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 6. Text Size / Font Scale Dialog
    if (showTextSizeDialog) {
        AlertDialog(
            onDismissRequest = { showTextSizeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FormatSize, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Text Size & Scaling", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Adjust reading typography scale across all transit schedules and civic feeds.",
                        fontSize = 12.sp,
                        color = CivicSlate600
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextScaleOption.entries.forEach { option ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (textScale == option) Color(0xFFEFF6FF) else CivicSlate100,
                            border = BorderStroke(1.dp, if (textScale == option) Color(0xFF2563EB) else CivicSlate200),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setTextScale(option)
                                    showTextSizeDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = textScale == option,
                                    onClick = {
                                        viewModel.setTextScale(option)
                                        showTextSizeDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2563EB))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = option.displayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (14 * option.scaleMultiplier).sp,
                                        color = CivicDarkGray
                                    )
                                    Text(
                                        text = "Sample: Connaught Place • Route 52",
                                        fontSize = (11 * option.scaleMultiplier).sp,
                                        color = CivicSlate600
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTextSizeDialog = false }) {
                    Text("Close", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 7. Granular Notifications Dialog
    if (showNotificationsDialog) {
        var localSettings by remember { mutableStateOf(notificationSettings) }

        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = CivicAmber, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Notification Preferences", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Grievance Status Updates", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("SLA progression & resolution alerts", fontSize = 11.sp, color = CivicSlate400)
                        }
                        Switch(
                            checked = localSettings.issueStatusUpdates,
                            onCheckedChange = { localSettings = localSettings.copy(issueStatusUpdates = it) }
                        )
                    }

                    HorizontalDivider(color = CivicSlate200)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Road Safety & Weather Alerts", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Waterlogging, red alerts, and hazards", fontSize = 11.sp, color = CivicSlate400)
                        }
                        Switch(
                            checked = localSettings.weatherEmergencyAlerts,
                            onCheckedChange = { localSettings = localSettings.copy(weatherEmergencyAlerts = it) }
                        )
                    }

                    HorizontalDivider(color = CivicSlate200)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Transit & Bus Disruptions", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Route detours, delays, and corridor blocks", fontSize = 11.sp, color = CivicSlate400)
                        }
                        Switch(
                            checked = localSettings.transitDisruptions,
                            onCheckedChange = { localSettings = localSettings.copy(transitDisruptions = it) }
                        )
                    }

                    HorizontalDivider(color = CivicSlate200)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Community Upvotes & Activity", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Neighbor confirmations on your reports", fontSize = 11.sp, color = CivicSlate400)
                        }
                        Switch(
                            checked = localSettings.communityActivity,
                            onCheckedChange = { localSettings = localSettings.copy(communityActivity = it) }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateNotificationSettings(localSettings)
                        showNotificationsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivicOrangePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Preferences", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showNotificationsDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // 8. Accessibility Settings Dialog
    if (showAccessibilityDialog) {
        var localAccessibility by remember { mutableStateOf(accessibilitySettings) }

        AlertDialog(
            onDismissRequest = { showAccessibilityDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessibilityNew, contentDescription = null, tint = CivicGreenPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Accessibility Controls", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("High Contrast UI", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Enhances border definitions and solid dark text", fontSize = 11.sp, color = CivicSlate400)
                        }
                        Switch(
                            checked = localAccessibility.highContrast,
                            onCheckedChange = { localAccessibility = localAccessibility.copy(highContrast = it) }
                        )
                    }

                    HorizontalDivider(color = CivicSlate200)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Reduce Animations", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Disables smooth transit transitions and banner pulses", fontSize = 11.sp, color = CivicSlate400)
                        }
                        Switch(
                            checked = localAccessibility.reduceMotion,
                            onCheckedChange = { localAccessibility = localAccessibility.copy(reduceMotion = it) }
                        )
                    }

                    HorizontalDivider(color = CivicSlate200)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Screen Reader Priority Mode", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Expands vocal semantic descriptions for TalkBack", fontSize = 11.sp, color = CivicSlate400)
                        }
                        Switch(
                            checked = localAccessibility.screenReaderOptimized,
                            onCheckedChange = { localAccessibility = localAccessibility.copy(screenReaderOptimized = it) }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateAccessibilitySettings(localAccessibility)
                        showAccessibilityDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivicGreenPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Apply Settings", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAccessibilityDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // 9. Privacy & Permissions Dialog
    if (showPrivacyDialog) {
        var localPrivacy by remember { mutableStateOf(privacySettings) }

        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Privacy & Device Permissions", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Precise GPS Location", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Used for auto-tagging complaint wards & route planner", fontSize = 11.sp, color = CivicSlate400)
                        }
                        Switch(
                            checked = localPrivacy.locationSharing,
                            onCheckedChange = { localPrivacy = localPrivacy.copy(locationSharing = it) }
                        )
                    }

                    HorizontalDivider(color = CivicSlate200)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Camera & Photo Uploads", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Attach evidence photos to municipal grievance tickets", fontSize = 11.sp, color = CivicSlate400)
                        }
                        Switch(
                            checked = localPrivacy.cameraAccess,
                            onCheckedChange = { localPrivacy = localPrivacy.copy(cameraAccess = it) }
                        )
                    }

                    HorizontalDivider(color = CivicSlate200)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Anonymous Reporting Mode", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Hides your name and phone from public feeds", fontSize = 11.sp, color = CivicSlate400)
                        }
                        Switch(
                            checked = localPrivacy.anonymousReporting,
                            onCheckedChange = { localPrivacy = localPrivacy.copy(anonymousReporting = it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.clearAppCache() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear Cache", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.exportUserData() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export Data", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updatePrivacySettings(localPrivacy)
                        showPrivacyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Privacy", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showPrivacyDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // 10. App Lock / Biometrics Dialog
    if (showAppLockDialog) {
        var isLockOn by remember { mutableStateOf(appLockSettings.isEnabled) }
        var pinInput by remember { mutableStateOf(appLockSettings.pinCode ?: "") }
        var pinError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAppLockDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = CivicOrangePrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("App Lock & Biometrics", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enable Screen Lock", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Require PIN or Fingerprint on app open", fontSize = 11.sp, color = CivicSlate400)
                        }
                        Switch(
                            checked = isLockOn,
                            onCheckedChange = { isLockOn = it }
                        )
                    }

                    if (isLockOn) {
                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = {
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    pinInput = it
                                    pinError = null
                                }
                            },
                            label = { Text("Enter 4-digit Security PIN") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CivicOrangePrimary) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        pinError?.let {
                            Text(text = it, color = CivicRed, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isLockOn && pinInput.length < 4) {
                            pinError = "Please enter a complete 4-digit PIN."
                        } else {
                            viewModel.updateAppLock(AppLockSettings(isEnabled = isLockOn, pinCode = if (isLockOn) pinInput else null))
                            showAppLockDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CivicOrangePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Security", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAppLockDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // 11. Language Selector Dialog
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = selectedLanguage,
            onLanguageSelected = {
                viewModel.setLanguage(it)
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // 12. Help & Feedback Dialog
    if (showHelpDialog) {
        HelpAndFeedbackDialog(
            initialTab = helpDialogInitialTab,
            onDismiss = { showHelpDialog = false },
            onSubmitFeedback = { type, subject, message, rating ->
                viewModel.submitFeedback(type, subject, message, rating)
            }
        )
    }

    // 13. My Issues Bottom Sheet
    if (showMyIssuesSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMyIssuesSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "My Reports (${userIssues.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CivicDarkGray
                    )
                    TextButton(onClick = { showMyIssuesSheet = false }) {
                        Text("Done", color = CivicOrangePrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (userIssues.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No civic reports filed yet.",
                            color = CivicSlate400,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.height(380.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(userIssues) { issue ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = CivicSlate100,
                                border = BorderStroke(1.dp, CivicSlate200),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showMyIssuesSheet = false
                                        onSelectIssue(issue.id)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = issue.title,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CivicSlate800
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${issue.category.displayName} • ${issue.address}",
                                            fontSize = 11.5.sp,
                                            color = CivicSlate600
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    CivicStatusBadge(status = issue.status)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // 14. Logout Confirmation Modal
    if (showLogoutModal) {
        LogoutConfirmationDialog(
            onDismiss = { showLogoutModal = false },
            isLoggingOut = isLoggingOut,
            onConfirmLogout = {
                isLoggingOut = true
                coroutineScope.launch {
                    delay(400)
                    isLoggingOut = false
                    showLogoutModal = false
                    viewModel.logout()
                    onLogout()
                }
            }
        )
    }

    // Main WhatsApp / Instagram Style Screen Container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicSlate100)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Instagram / WhatsApp Style Profile Hero Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, CivicSlate200),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with Letter
                Surface(
                    shape = CircleShape,
                    color = CivicDarkGray,
                    border = BorderStroke(2.5.dp, CivicOrangePrimary),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = (user?.name?.firstOrNull() ?: 'R').toString().uppercase(),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user?.name ?: "Rahul Sharma",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate900
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = user?.phone ?: "+91 98765 43210",
                        fontSize = 12.5.sp,
                        color = CivicSlate600
                    )
                    Text(
                        text = user?.email ?: "rahul.sharma@example.com",
                        fontSize = 11.5.sp,
                        color = CivicSlate400
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CivicOrangePrimary.copy(alpha = 0.12f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CivicOrangePrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Verified Citizen Resident",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicOrangePrimary
                            )
                        }
                    }
                }

                // Edit Profile Quick Icon
                IconButton(
                    onClick = { showEditProfileDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CivicSlate100,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = CivicDarkGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // WhatsApp / Instagram Style List Group: Account & Profile Details
        WhatsAppSectionTitle(title = "ACCOUNT & PROFILE")
        WhatsAppCardGroup {
            // 1. Edit Profile
            WhatsAppListItem(
                icon = Icons.Default.Person,
                iconColor = CivicOrangePrimary,
                title = "Edit Profile",
                subtitle = "Update full name and personal information",
                onClick = { showEditProfileDialog = true },
                testTag = "setting_edit_profile_row"
            )

            WhatsAppDivider()

            // 2. Change Mobile Number
            WhatsAppListItem(
                icon = Icons.Default.Phone,
                iconColor = CivicOrangePrimary,
                title = "Change Mobile Number",
                subtitle = "Update registered phone for grievance SMS",
                valueBadge = user?.phone ?: "+91 98765 43210",
                onClick = { showChangePhoneDialog = true },
                testTag = "setting_change_phone_row"
            )

            WhatsAppDivider()

            // 3. Change Account/Email
            WhatsAppListItem(
                icon = Icons.Default.Email,
                iconColor = CivicOrangePrimary,
                title = "Change Account/Email",
                subtitle = "Update your primary login & dispatch email",
                valueBadge = user?.email ?: "citizen@example.com",
                onClick = { showChangeEmailDialog = true },
                testTag = "setting_change_email_row"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // WhatsApp / Instagram Style List Group: Appearance & Display
        WhatsAppSectionTitle(title = "APPEARANCE & DISPLAY")
        WhatsAppCardGroup {
            // 4. App Theme / Appearance
            WhatsAppListItem(
                icon = Icons.Default.DarkMode,
                iconColor = CivicOrangePrimary,
                title = "Appearance",
                subtitle = "Switch between Dark, Light, or System default",
                valueBadge = currentThemeMode.displayName,
                onClick = { showThemeDialog = true },
                testTag = "setting_appearance_row"
            )

            WhatsAppDivider()

            // 5. Text Size / Typography
            WhatsAppListItem(
                icon = Icons.Default.FormatSize,
                iconColor = Color(0xFF2563EB),
                title = "Text Size",
                subtitle = "Adjust readable font scale and typography",
                valueBadge = textScale.displayName,
                onClick = { showTextSizeDialog = true },
                testTag = "setting_text_size_row"
            )

            WhatsAppDivider()

            // 6. App Language
            WhatsAppListItem(
                icon = Icons.Default.Language,
                iconColor = Color(0xFF0284C7),
                title = "App Language",
                subtitle = "Select preferred regional dialect",
                valueBadge = selectedLanguage.englishName,
                onClick = { showLanguageDialog = true },
                testTag = "setting_app_language_row"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // WhatsApp / Instagram Style List Group: Activity & Notifications
        WhatsAppSectionTitle(title = "ACTIVITY & NOTIFICATIONS")
        WhatsAppCardGroup {
            // 7. My Reports
            WhatsAppListItem(
                icon = Icons.Default.Assignment,
                iconColor = CivicOrangePrimary,
                title = "My Reports",
                subtitle = "View and manage filed complaints",
                valueBadge = "${userIssues.size} filed",
                onClick = { showMyIssuesSheet = true },
                testTag = "setting_my_reports_row"
            )

            WhatsAppDivider()

            // 8. Notifications Preferences
            WhatsAppListItem(
                icon = Icons.Default.Notifications,
                iconColor = CivicAmber,
                title = "Notifications",
                subtitle = "Customize status updates & emergency alerts",
                valueBadge = if (notificationSettings.issueStatusUpdates) "Enabled" else "Muted",
                onClick = { showNotificationsDialog = true },
                testTag = "setting_notifications_row"
            )

            WhatsAppDivider()

            // 9. Accessibility
            WhatsAppListItem(
                icon = Icons.Default.AccessibilityNew,
                iconColor = CivicGreenPrimary,
                title = "Accessibility",
                subtitle = "High contrast, screen reader & motion controls",
                onClick = { showAccessibilityDialog = true },
                testTag = "setting_accessibility_row"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // WhatsApp / Instagram Style List Group: Privacy & Security
        WhatsAppSectionTitle(title = "PRIVACY & SECURITY")
        WhatsAppCardGroup {
            // 10. Privacy & Permissions
            WhatsAppListItem(
                icon = Icons.Default.PrivacyTip,
                iconColor = Color(0xFF6366F1),
                title = "Privacy & Permissions",
                subtitle = "GPS location, camera access & data export",
                onClick = { showPrivacyDialog = true },
                testTag = "setting_privacy_row"
            )

            WhatsAppDivider()

            // 11. App Lock & Biometrics
            WhatsAppListItem(
                icon = Icons.Default.Fingerprint,
                iconColor = CivicOrangePrimary,
                title = "App Lock",
                subtitle = "Secure access with 4-digit PIN or biometrics",
                valueBadge = if (appLockSettings.isEnabled) "Locked" else "Off",
                onClick = { showAppLockDialog = true },
                testTag = "setting_app_lock_row"
            )

            WhatsAppDivider()

            // 12. Change Password
            WhatsAppListItem(
                icon = Icons.Default.Lock,
                iconColor = CivicDarkGray,
                title = "Change Password",
                subtitle = "Update your security access password",
                onClick = { showPasswordDialog = true },
                testTag = "setting_change_password_row"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // WhatsApp / Instagram Style List Group: Support & Administration
        WhatsAppSectionTitle(title = "SUPPORT & ADMINISTRATION")
        WhatsAppCardGroup {
            // 13. Help & Feedback
            WhatsAppListItem(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                iconColor = Color(0xFF0284C7),
                title = "Help & Feedback",
                subtitle = "FAQs, SLA resolution times & submit feedback",
                onClick = {
                    helpDialogInitialTab = 0
                    showHelpDialog = true
                },
                testTag = "setting_help_feedback_row"
            )

            WhatsAppDivider()

            // 14. Municipal Admin Switch (Authorized only inside settings)
            WhatsAppListItem(
                icon = Icons.Default.AdminPanelSettings,
                iconColor = CivicAmber,
                title = "Municipal Admin Access",
                subtitle = "Switch to Municipal Officer / Triage console",
                valueBadge = "Officer Console",
                onClick = {
                    onSwitchToAdmin()
                },
                testTag = "setting_switch_to_admin_row"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // WhatsApp / Instagram Style List Group: Logout
        WhatsAppCardGroup {
            // 15. Logout
            WhatsAppListItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                iconColor = CivicRed,
                title = "Logout",
                subtitle = "Sign out from this citizen session",
                titleColor = CivicRed,
                onClick = { showLogoutModal = true },
                testTag = "setting_logout_row"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App Version Footer
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CivicFix • Municipal Grievance Platform",
                fontSize = 11.sp,
                color = CivicSlate400,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Clean Citizen Portal & Fast SLA Dispatch",
                fontSize = 10.sp,
                color = CivicSlate400
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

// ------------------------------------------------------------------------------------------------
// WhatsApp / Instagram Style List Components
// ------------------------------------------------------------------------------------------------

@Composable
private fun WhatsAppSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.ExtraBold,
        color = CivicSlate600,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
    )
}

@Composable
private fun WhatsAppCardGroup(
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CivicSlate200),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            content()
        }
    }
}

@Composable
private fun WhatsAppDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 58.dp),
        color = CivicSlate200.copy(alpha = 0.8f),
        thickness = 0.8.dp
    )
}

@Composable
private fun WhatsAppListItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    titleColor: Color = CivicSlate900,
    valueBadge: String? = null,
    isToggle: Boolean = false,
    toggleState: Boolean = false,
    onToggleChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    testTag: String = ""
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .then(if (testTag.isNotBlank()) Modifier.testTag(testTag) else Modifier)
    ) {
        // WhatsApp / Instagram style colored rounded icon box
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = iconColor.copy(alpha = 0.12f),
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and description
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = CivicSlate400,
                lineHeight = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Trailing element: Value chip / Toggle / Arrow
        if (isToggle && onToggleChange != null) {
            Switch(
                checked = toggleState,
                onCheckedChange = onToggleChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = CivicOrangePrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = CivicSlate200
                ),
                modifier = Modifier.size(width = 44.dp, height = 24.dp)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                valueBadge?.let {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CivicSlate100,
                        border = BorderStroke(1.dp, CivicSlate200)
                    ) {
                        Text(
                            text = it,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicSlate600,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = CivicSlate400,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.AppLanguage
import com.example.data.models.CivicIssue
import com.example.data.models.User
import com.example.ui.AppThemeMode
import com.example.ui.NotificationSettings
import com.example.ui.components.CivicStatusBadge
import com.example.ui.components.HelpAndFeedbackDialog
import com.example.ui.components.LanguageSelectionDialog
import com.example.ui.components.LogoutConfirmationDialog
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicDarkGray
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
 * All settings, civic records, language switcher, and account options arranged in clean list formats.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenProfileScreen(
    user: User?,
    userIssues: List<CivicIssue>,
    selectedLanguage: AppLanguage,
    currentThemeMode: AppThemeMode,
    notificationSettings: NotificationSettings,
    onUpdateProfile: (name: String, email: String, phone: String) -> Unit,
    onChangePassword: (oldPass: String, newPass: String) -> Boolean,
    onUpdateNotifications: (NotificationSettings) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onSelectThemeMode: (AppThemeMode) -> Unit,
    onSubmitFeedback: (type: String, subject: String, message: String, rating: Int) -> Unit,
    onSelectIssue: (String) -> Unit,
    onSwitchToAdmin: () -> Unit,
    onLogout: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePhoneDialog by remember { mutableStateOf(false) }
    var showChangeEmailDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var helpDialogInitialTab by remember { mutableIntStateOf(0) }
    var showLogoutModal by remember { mutableStateOf(false) }
    var showMyIssuesSheet by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
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
                            onUpdateProfile(editName, editEmail, editPhone)
                            showEditProfileDialog = false
                            onShowToast("Profile details updated successfully")
                        } else {
                            onShowToast("Name cannot be empty")
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
                            onUpdateProfile(user?.name ?: "Rahul Sharma", user?.email ?: "citizen@example.com", newPhone.trim())
                            showChangePhoneDialog = false
                            onShowToast("Mobile number updated to $newPhone")
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
                            onUpdateProfile(user?.name ?: "Rahul Sharma", trimmed, user?.phone ?: "+91 98765 43210")
                            showChangeEmailDialog = false
                            onShowToast("Account email updated successfully")
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
                            val success = onChangePassword(currentPass, newPass)
                            if (success) {
                                showPasswordDialog = false
                                onShowToast("Password updated successfully")
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

    // 5. Language Selector Dialog (Shifted directly into settings/profile)
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = selectedLanguage,
            onLanguageSelected = {
                onSelectLanguage(it)
                onShowToast("Language set to ${it.englishName}")
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // 6. Help & Feedback Dialog
    if (showHelpDialog) {
        HelpAndFeedbackDialog(
            initialTab = helpDialogInitialTab,
            onDismiss = { showHelpDialog = false },
            onSubmitFeedback = onSubmitFeedback
        )
    }

    // 7. My Issues Bottom Sheet
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

    // 8. Logout Confirmation Modal
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

        // WhatsApp / Instagram Style List Group: Profile & Account Details
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

        // WhatsApp / Instagram Style List Group: Activity & Preferences
        WhatsAppSectionTitle(title = "ACTIVITY & PREFERENCES")
        WhatsAppCardGroup {
            // 4. My Reports
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

            // 5. Notifications
            WhatsAppListItem(
                icon = Icons.Default.Notifications,
                iconColor = CivicAmber,
                title = "Notifications",
                subtitle = "Resolution updates & official alerts",
                isToggle = true,
                toggleState = notificationSettings.issueStatusUpdates,
                onToggleChange = { checked ->
                    onUpdateNotifications(
                        notificationSettings.copy(
                            issueStatusUpdates = checked,
                            complaintResolution = checked,
                            communityActivity = checked,
                            newAnnouncements = checked
                        )
                    )
                    onShowToast(if (checked) "Push notifications enabled" else "Notifications muted")
                },
                testTag = "setting_notifications_toggle"
            )

            WhatsAppDivider()

            // 6. App Language
            WhatsAppListItem(
                icon = Icons.Default.Language,
                iconColor = Color(0xFF2563EB),
                title = "App Language",
                subtitle = "Select preferred regional dialect",
                valueBadge = selectedLanguage.englishName,
                onClick = { showLanguageDialog = true },
                testTag = "setting_app_language_row"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // WhatsApp / Instagram Style List Group: Support & Security
        WhatsAppSectionTitle(title = "SUPPORT & SECURITY")
        WhatsAppCardGroup {
            // 7. Help & Feedback
            WhatsAppListItem(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                iconColor = Color(0xFF0284C7),
                title = "Help & Feedback",
                subtitle = "FAQs, SLA guide, and direct contact",
                onClick = {
                    helpDialogInitialTab = 0
                    showHelpDialog = true
                },
                testTag = "setting_help_feedback_row"
            )

            WhatsAppDivider()

            // 8. Change Password
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

        // WhatsApp / Instagram Style List Group: Logout
        WhatsAppCardGroup {
            // 9. Logout
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

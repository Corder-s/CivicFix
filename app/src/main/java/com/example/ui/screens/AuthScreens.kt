package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserRole
import com.example.ui.components.AuthButton
import com.example.ui.components.AuthInput
import com.example.ui.components.AuthLayout
import com.example.ui.components.PasswordInput
import com.example.ui.components.SocialDivider
import com.example.ui.components.SocialPillAuthRow
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicAmberContainer
import com.example.ui.theme.CivicAmberText
import com.example.ui.theme.CivicDarkGray
import com.example.ui.theme.CivicGreenContainer
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyBorder
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicOrangeDark
import com.example.ui.theme.CivicOrangeLight
import com.example.ui.theme.CivicOrangePrimary
import com.example.ui.theme.CivicRed
import com.example.ui.theme.CivicRedContainer
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate300
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Aesthetic CivicFix Citizen Login Screen matching the Crextio / Civic reference interface.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: (email: String, role: UserRole) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToAdminLogin: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun validateAndSubmit() {
        emailError = null
        passwordError = null
        generalError = null

        val trimmedEmail = email.trim()
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

        if (trimmedEmail.isEmpty()) {
            emailError = "Please enter your email address."
            return
        } else if (!emailRegex.matches(trimmedEmail)) {
            emailError = "Please enter a valid email address."
            return
        }

        if (password.isEmpty()) {
            passwordError = "Please enter your password."
            return
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters."
            return
        }

        isLoading = true
        coroutineScope.launch {
            delay(500)
            isLoading = false
            onLoginSuccess(trimmedEmail, UserRole.CITIZEN)
        }
    }

    AuthLayout(
        title = "Welcome to CivicFix",
        subtitle = "Sign in to report civic issues and improve your community"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Email Pill Input
            AuthInput(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError != null) emailError = null
                },
                label = "Email",
                placeholder = "citizen@example.com",
                leadingIcon = Icons.Default.Email,
                errorMessage = emailError,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                testTag = "login_email_input"
            )

            // Password Pill Input
            PasswordInput(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError != null) passwordError = null
                },
                label = "Password",
                placeholder = "••••••••••••••••••••",
                errorMessage = passwordError,
                imeAction = ImeAction.Done,
                onImeAction = { validateAndSubmit() },
                testTag = "login_password_input"
            )

            // Remember Me and Forgot Password row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = CivicOrangePrimary,
                            uncheckedColor = CivicSlate400
                        ),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Remember me",
                        fontSize = 12.sp,
                        color = CivicSlate600
                    )
                }

                Text(
                    text = "Forgot password?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CivicOrangePrimary,
                    modifier = Modifier
                        .clickable { onNavigateToForgotPassword() }
                        .testTag("forgot_password_link")
                )
            }

            if (generalError != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CivicRedContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = generalError!!,
                        color = CivicRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Primary Orange Submit Button
            AuthButton(
                text = "Sign In",
                onClick = { validateAndSubmit() },
                isLoading = isLoading,
                loadingText = "Signing in...",
                testTag = "login_submit_button"
            )

            // Apple and Google Pill Social Sign-in Row
            SocialPillAuthRow(
                onAppleClick = {
                    isLoading = true
                    coroutineScope.launch {
                        delay(400)
                        isLoading = false
                        onLoginSuccess("apple.citizen@civicfix.com", UserRole.CITIZEN)
                    }
                },
                onGoogleClick = {
                    isLoading = true
                    coroutineScope.launch {
                        delay(400)
                        isLoading = false
                        onLoginSuccess("google.citizen@civicfix.com", UserRole.CITIZEN)
                    }
                }
            )

            // Portal switch to Admin Login
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CivicNavyContainer,
                border = BorderStroke(1.dp, CivicNavyBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAdminLogin() }
                    .testTag("nav_to_admin_login_card")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = CivicNavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = "Municipal Officer / Admin Portal",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicNavyDark
                            )
                            Text(
                                text = "Authorized municipal command staff",
                                fontSize = 10.sp,
                                color = CivicSlate600
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = CivicNavyDark,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Bottom Footer Row matching reference: "Already have an account? Sign in" & "Terms & Conditions"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Don't have an account? ",
                        fontSize = 11.5.sp,
                        color = CivicSlate600
                    )
                    Text(
                        text = "Sign up",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate900,
                        modifier = Modifier
                            .clickable { onNavigateToRegister() }
                            .testTag("register_navigation_link")
                    )
                }

                Text(
                    text = "Terms & Conditions",
                    fontSize = 11.sp,
                    color = CivicSlate400,
                    modifier = Modifier.clickable { }
                )
            }
        }
    }
}

/**
 * Aesthetic CivicFix Registration Screen directly implementing the Create An Account UI from 1.webp
 */
@Composable
fun RegisterScreen(
    onRegisterSuccess: (name: String, email: String, phone: String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var termsError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun validateAndRegister() {
        nameError = null
        emailError = null
        phoneError = null
        passwordError = null
        termsError = null

        var hasError = false

        if (fullName.trim().length < 2) {
            nameError = "Please enter your full name."
            hasError = true
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (email.trim().isEmpty() || !emailRegex.matches(email.trim())) {
            emailError = "Please enter a valid email address."
            hasError = true
        }

        if (password.length < 6) {
            passwordError = "Password must be at least 6 characters."
            hasError = true
        }

        if (hasError) return

        val finalPhone = if (phone.trim().isNotEmpty()) phone.trim() else "+91 98765 43210"

        isLoading = true
        coroutineScope.launch {
            delay(500)
            isLoading = false
            onRegisterSuccess(fullName.trim(), email.trim(), finalPhone)
        }
    }

    AuthLayout(
        title = "Create an account",
        subtitle = "Sign up and get 30-day free trial",
        onBackClick = onNavigateToLogin
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Full name Pill Input
            AuthInput(
                value = fullName,
                onValueChange = {
                    fullName = it
                    if (nameError != null) nameError = null
                },
                label = "Full name",
                placeholder = "Amélie Laurent",
                leadingIcon = Icons.Default.Person,
                errorMessage = nameError,
                testTag = "register_name_input"
            )

            // Email Pill Input
            AuthInput(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError != null) emailError = null
                },
                label = "Email",
                placeholder = "amélielaurent7622@gmail.com",
                leadingIcon = Icons.Default.Email,
                errorMessage = emailError,
                keyboardType = KeyboardType.Email,
                testTag = "register_email_input"
            )

            // Password Pill Input with Visibility Toggle
            PasswordInput(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError != null) passwordError = null
                },
                label = "Password",
                placeholder = "••••••••••••••••••••",
                errorMessage = passwordError,
                showStrength = true,
                imeAction = ImeAction.Done,
                onImeAction = { validateAndRegister() },
                testTag = "register_password_input"
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Yellow Amber Submit Button
            AuthButton(
                text = "Submit",
                onClick = { validateAndRegister() },
                isLoading = isLoading,
                loadingText = "Creating account...",
                testTag = "register_submit_button"
            )

            // Apple and Google Pill Social Sign-in Row
            SocialPillAuthRow(
                onAppleClick = {
                    isLoading = true
                    coroutineScope.launch {
                        delay(400)
                        isLoading = false
                        onRegisterSuccess("Apple User", "apple.user@civicfix.com", "+91 98765 43210")
                    }
                },
                onGoogleClick = {
                    isLoading = true
                    coroutineScope.launch {
                        delay(400)
                        isLoading = false
                        onRegisterSuccess("Google User", "google.user@civicfix.com", "+91 98765 43210")
                    }
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Bottom Footer Row matching reference: "Already have an account? Sign in" & "Terms & Conditions"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Already have account? ",
                        fontSize = 11.5.sp,
                        color = CivicSlate600
                    )
                    Text(
                        text = "Sign in",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate900,
                        modifier = Modifier
                            .clickable { onNavigateToLogin() }
                            .testTag("login_navigation_link")
                    )
                }

                Text(
                    text = "Terms & Conditions",
                    fontSize = 11.sp,
                    color = CivicSlate400,
                    modifier = Modifier.clickable { }
                )
            }
        }
    }
}

/**
 * Aesthetic CivicFix Forgot Password Screen
 */
@Composable
fun ForgotPasswordScreen(
    onNavigateToLogin: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }

    fun sendResetLink() {
        emailError = null
        val trimmedEmail = email.trim()
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

        if (trimmedEmail.isEmpty()) {
            emailError = "Please enter your email address."
            return
        } else if (!emailRegex.matches(trimmedEmail)) {
            emailError = "Please enter a valid email address."
            return
        }

        isLoading = true
        coroutineScope.launch {
            delay(500)
            isLoading = false
            isSubmitted = true
        }
    }

    AuthLayout(
        title = if (isSubmitted) "Check Your Inbox" else "Forgot password?",
        subtitle = if (isSubmitted) "Instructions dispatched successfully." else "Enter your email address and we'll send a reset link.",
        onBackClick = onNavigateToLogin
    ) {
        AnimatedContent(
            targetState = isSubmitted,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "forgot_password_step"
        ) { submitted ->
            if (submitted) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CivicGreenContainer,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MarkEmailRead,
                                contentDescription = null,
                                tint = CivicGreenDark,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Text(
                        text = "We've sent password reset instructions to your email ($email).",
                        fontSize = 13.sp,
                        color = CivicSlate800,
                        lineHeight = 19.sp
                    )

                    AuthButton(
                        text = "Back to Sign In",
                        onClick = onNavigateToLogin,
                        testTag = "back_to_login_button"
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    AuthInput(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError != null) emailError = null
                        },
                        label = "Email",
                        placeholder = "amélielaurent7622@gmail.com",
                        leadingIcon = Icons.Default.Email,
                        errorMessage = emailError,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done,
                        onImeAction = { sendResetLink() },
                        testTag = "forgot_password_email_input"
                    )

                    AuthButton(
                        text = "Send Reset Link",
                        onClick = { sendResetLink() },
                        isLoading = isLoading,
                        loadingText = "Sending link...",
                        testTag = "send_reset_link_button"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Remembered password? ",
                            fontSize = 12.sp,
                            color = CivicSlate600
                        )
                        Text(
                            text = "Sign in",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CivicNavyPrimary,
                            modifier = Modifier
                                .clickable { onNavigateToLogin() }
                                .testTag("back_to_login_link")
                        )
                    }
                }
            }
        }
    }
}

/**
 * Aesthetic CivicFix Municipal Admin Login Screen
 */
@Composable
fun AdminLoginScreen(
    onAdminLoginSuccess: (email: String) -> Unit,
    onNavigateToCitizenLogin: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var adminEmail by remember { mutableStateOf("admin@civicfix.com") }
    var adminPassword by remember { mutableStateOf("Admin@123") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun validateAndAdminLogin() {
        emailError = null
        passwordError = null
        generalError = null

        val trimmed = adminEmail.trim()
        if (trimmed.isEmpty()) {
            emailError = "Please enter administrator email."
            return
        }
        if (adminPassword.isEmpty()) {
            passwordError = "Please enter administration security key."
            return
        }

        isLoading = true
        coroutineScope.launch {
            delay(500)
            isLoading = false
            onAdminLoginSuccess(trimmed)
        }
    }

    AuthLayout(
        title = "Municipal Command Portal",
        subtitle = "Authorized municipal administration personnel only",
        isAdminTheme = true,
        onBackClick = onNavigateToCitizenLogin
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Authorized Personnel Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = CivicAmberContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = CivicAmberText,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Authorized Personnel — Municipal Command Network",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicAmberText
                    )
                }
            }

            // Admin Email Input
            AuthInput(
                value = adminEmail,
                onValueChange = {
                    adminEmail = it
                    if (emailError != null) emailError = null
                },
                label = "Admin Email",
                placeholder = "admin@civicfix.com",
                leadingIcon = Icons.Default.Email,
                errorMessage = emailError,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                isAdminTheme = true,
                testTag = "admin_email_input"
            )

            // Admin Password
            PasswordInput(
                value = adminPassword,
                onValueChange = {
                    adminPassword = it
                    if (passwordError != null) passwordError = null
                },
                label = "Security Password",
                placeholder = "Enter security password",
                errorMessage = passwordError,
                imeAction = ImeAction.Done,
                onImeAction = { validateAndAdminLogin() },
                isAdminTheme = true,
                testTag = "admin_password_input"
            )

            // Admin Submit Button
            AuthButton(
                text = "Admin Submit",
                onClick = { validateAndAdminLogin() },
                isLoading = isLoading,
                loadingText = "Authenticating...",
                isAdminTheme = true,
                testTag = "admin_submit_button"
            )

            // 1-Click Demo Admin Button
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White,
                border = BorderStroke(1.dp, CivicAmber),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clickable {
                        adminEmail = "admin@civicfix.com"
                        adminPassword = "Admin@123"
                        isLoading = true
                        coroutineScope.launch {
                            delay(350)
                            isLoading = false
                            onAdminLoginSuccess("admin@civicfix.com")
                        }
                    }
                    .testTag("demo_admin_login_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = CivicAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Continue as Demo Admin",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicAmber
                    )
                }
            }

            // Return to Citizen Login
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "← Return to Citizen Portal",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CivicNavyPrimary,
                    modifier = Modifier
                        .clickable { onNavigateToCitizenLogin() }
                        .testTag("return_to_citizen_portal_link")
                )
            }
        }
    }
}

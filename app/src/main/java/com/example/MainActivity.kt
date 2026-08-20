package com.example

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.CivicFixDatabase
import com.example.data.localization.AppLanguage
import com.example.data.localization.CivicStrings
import com.example.data.localization.LocalCivicLanguage
import com.example.data.localization.civicString
import com.example.data.models.UserRole
import com.example.data.repository.CivicFixRepository
import com.example.ui.CivicFixViewModel
import com.example.ui.CivicFixViewModelFactory
import com.example.ui.components.CivicTopBar
import com.example.ui.components.GlobalFloatingAiButton
import com.example.ui.components.LanguageSelectionDialog
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminIssueManagementScreen
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.screens.AdminUserManagementScreen
import com.example.ui.screens.AiChatAssistantScreen
import com.example.ui.screens.CitizenDashboardScreen
import com.example.ui.screens.CitizenProfileScreen
import com.example.ui.screens.CivicLiveMapScreen
import com.example.ui.screens.CommunityIssuesScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.IssueDetailScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MyIssuesScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PublicHomeScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.ReportIssueScreen
import com.example.ui.screens.SafeJourneyPlannerScreen
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicFixTheme
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
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = CivicFixDatabase.getDatabase(applicationContext)
        val repository = CivicFixRepository(database.civicFixDao())
        val sharedPrefs = applicationContext.getSharedPreferences("civicfix_prefs", Context.MODE_PRIVATE)
        val viewModelFactory = CivicFixViewModelFactory(repository, sharedPrefs)

        setContent {
            val viewModel: CivicFixViewModel = viewModel(factory = viewModelFactory)
            val themeMode by viewModel.themeMode.collectAsState()

            CivicFixTheme(themeMode = themeMode) {
                CivicFixApp(viewModel)
            }
        }
    }
}

@Composable
fun CivicFixApp(viewModel: CivicFixViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val issues by viewModel.issues.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val notificationSettings by viewModel.notificationSettings.collectAsState()
    val predictiveHazards by viewModel.predictiveHazards.collectAsState()
    val civicMobilityScores by viewModel.civicMobilityScores.collectAsState()

    var showLanguageModal by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "public_home"

    val isAuthScreen = currentRoute in listOf("login", "register", "forgot_password", "admin_login")
    val isDetailScreen = currentRoute.startsWith("issue_detail") || currentRoute == "ai_chat" || currentRoute == "plan_journey"

    val unreadNotifCount = notifications.count { !it.isRead }

    val navigateToTab: (String) -> Unit = { targetRoute ->
        navController.navigate(targetRoute) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    CompositionLocalProvider(LocalCivicLanguage provides selectedLanguage) {
        if (showLanguageModal) {
            LanguageSelectionDialog(
                currentLanguage = selectedLanguage,
                onLanguageSelected = { lang ->
                    viewModel.setLanguage(lang)
                    Toast.makeText(context, "Language switched to ${lang.englishName}", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showLanguageModal = false }
            )
        }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // Tablet Navigation Rail
            if (isTablet && !isAuthScreen && !isDetailScreen) {
                NavigationRail(
                    containerColor = CivicNavyDark,
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxHeight(),
                    header = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "CivicFix",
                                        tint = CivicGreenLight,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "CivicFix",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    if (currentRole == UserRole.CITIZEN) {
                        NavigationRailItem(
                            selected = currentRoute == "public_home",
                            onClick = { navigateToTab("public_home") },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text(civicString(CivicStrings.HOME), fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = CivicNavyDark,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                                indicatorColor = CivicGreenLight
                            ),
                            modifier = Modifier.testTag("rail_tab_home")
                        )

                        NavigationRailItem(
                            selected = currentRoute == "civic_map",
                            onClick = { navigateToTab("civic_map") },
                            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Map") },
                            label = { Text(civicString(CivicStrings.MAP), fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = CivicNavyDark,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                                indicatorColor = CivicOrangeContainer
                            ),
                            modifier = Modifier.testTag("rail_tab_map")
                        )

                        NavigationRailItem(
                            selected = currentRoute == "community_feed",
                            onClick = { navigateToTab("community_feed") },
                            icon = { Icon(Icons.Default.Explore, contentDescription = "Feed") },
                            label = { Text(civicString(CivicStrings.FEED), fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = CivicNavyDark,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                                indicatorColor = CivicGreenLight
                            ),
                            modifier = Modifier.testTag("rail_tab_feed")
                        )

                        NavigationRailItem(
                            selected = currentRoute == "report_issue",
                            onClick = { navigateToTab("report_issue") },
                            icon = { Icon(Icons.Default.Add, contentDescription = "Report") },
                            label = { Text(civicString(CivicStrings.REPORT_ISSUE), fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = CivicGreenLight,
                                unselectedTextColor = CivicGreenLight,
                                indicatorColor = CivicGreenPrimary
                            ),
                            modifier = Modifier.testTag("rail_tab_report")
                        )

                        NavigationRailItem(
                            selected = currentRoute == "my_issues",
                            onClick = { navigateToTab("my_issues") },
                            icon = { Icon(Icons.Default.Description, contentDescription = "My Tickets") },
                            label = { Text(civicString(CivicStrings.TICKETS), fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = CivicNavyDark,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                                indicatorColor = CivicGreenLight
                            ),
                            modifier = Modifier.testTag("rail_tab_my_issues")
                        )

                        NavigationRailItem(
                            selected = currentRoute == "citizen_profile",
                            onClick = { navigateToTab("citizen_profile") },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text(civicString(CivicStrings.PROFILE), fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = CivicNavyDark,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                                indicatorColor = CivicGreenLight
                            ),
                            modifier = Modifier.testTag("rail_tab_profile")
                        )

                        NavigationRailItem(
                            selected = currentRoute == "ai_chat",
                            onClick = { navigateToTab("ai_chat") },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Guide") },
                            label = { Text(civicString(CivicStrings.AI_GUIDE), fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = CivicNavyDark,
                                selectedTextColor = Color.White,
                                unselectedIconColor = CivicGreenLight,
                                unselectedTextColor = CivicGreenLight,
                                indicatorColor = CivicGreenLight
                            ),
                            modifier = Modifier.testTag("rail_tab_ai_chat")
                        )
                    } else {
                        // Admin Tablet Navigation Items
                        NavigationRailItem(
                            selected = currentRoute == "admin_dashboard",
                            onClick = { navigateToTab("admin_dashboard") },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                            label = { Text(civicString(CivicStrings.OVERVIEW), fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = CivicNavyDark,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                                indicatorColor = CivicAmber
                            ),
                            modifier = Modifier.testTag("rail_tab_admin_dashboard")
                        )

                        NavigationRailItem(
                            selected = currentRoute == "admin_issues",
                            onClick = { navigateToTab("admin_issues") },
                            icon = { Icon(Icons.Default.Report, contentDescription = "Complaints") },
                            label = { Text(civicString(CivicStrings.COMPLAINTS), fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = CivicNavyDark,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                                indicatorColor = CivicAmber
                            ),
                            modifier = Modifier.testTag("rail_tab_admin_issues")
                        )

                        NavigationRailItem(
                            selected = currentRoute == "admin_users",
                            onClick = { navigateToTab("admin_users") },
                            icon = { Icon(Icons.Default.Group, contentDescription = "Citizens") },
                            label = { Text(civicString(CivicStrings.USERS), fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = CivicNavyDark,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                                indicatorColor = CivicAmber
                            ),
                            modifier = Modifier.testTag("rail_tab_admin_users")
                        )

                        NavigationRailItem(
                            selected = currentRoute == "notifications",
                            onClick = { navigateToTab("notifications") },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (unreadNotifCount > 0) {
                                            Badge(containerColor = CivicGreenPrimary) {
                                                Text("$unreadNotifCount")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                                }
                            },
                            label = { Text(civicString(CivicStrings.ALERTS), fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = CivicNavyDark,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                                indicatorColor = CivicAmber
                            ),
                            modifier = Modifier.testTag("rail_tab_notifications")
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Bottom rail role toggle button
                    IconButton(
                        onClick = {
                            viewModel.toggleRole()
                            val newRole = if (currentRole == UserRole.CITIZEN) "Municipal Admin Mode" else "Citizen Mode"
                            Toast.makeText(context, "Switched to $newRole", Toast.LENGTH_SHORT).show()
                            if (currentRole == UserRole.CITIZEN) {
                                navigateToTab("admin_dashboard")
                            } else {
                                navigateToTab("public_home")
                            }
                        },
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .testTag("rail_toggle_role")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Mode",
                            tint = if (currentRole == UserRole.ADMIN) CivicAmber else CivicGreenLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Main Scaffold for Content
            Scaffold(
                topBar = {
                    if (!isAuthScreen && !isDetailScreen) {
                        CivicTopBar(
                            currentRole = currentRole,
                            unreadNotifCount = unreadNotifCount,
                            userName = currentUser?.name,
                            selectedLanguage = selectedLanguage,
                            onLanguageClick = {
                                showLanguageModal = true
                            },
                            onNotificationClick = {
                                navigateToTab("notifications")
                            },
                            onAiChatClick = {
                                navController.navigate("ai_chat")
                            },
                            onToggleRoleClick = {
                                viewModel.toggleRole()
                                val newRole = if (currentRole == UserRole.CITIZEN) "Municipal Admin Mode" else "Citizen Mode"
                                Toast.makeText(context, "Switched to $newRole", Toast.LENGTH_SHORT).show()
                                if (currentRole == UserRole.CITIZEN) {
                                    navigateToTab("admin_dashboard")
                                } else {
                                    navigateToTab("public_home")
                                }
                            }
                        )
                    }
                },
                bottomBar = {
                    // Only show bottom navigation on mobile phones
                    if (!isTablet && !isAuthScreen && !isDetailScreen) {
                        if (currentRole == UserRole.CITIZEN) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp,
                                modifier = Modifier.testTag("citizen_bottom_bar")
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == "public_home",
                                    onClick = { navigateToTab("public_home") },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text(civicString(CivicStrings.HOME), fontSize = 11.sp, fontWeight = if (currentRoute == "public_home") FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag("bottom_tab_home")
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "civic_map",
                                    onClick = { navigateToTab("civic_map") },
                                    icon = { Icon(Icons.Default.LocationOn, contentDescription = "Map") },
                                    label = { Text(civicString(CivicStrings.MAP), fontSize = 11.sp, fontWeight = if (currentRoute == "civic_map") FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CivicOrangeDark,
                                        selectedTextColor = CivicOrangeDark,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = CivicOrangeContainer
                                    ),
                                    modifier = Modifier.testTag("bottom_tab_map")
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "report_issue",
                                    onClick = { navigateToTab("report_issue") },
                                    icon = {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = CivicGreenPrimary,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Add, contentDescription = "Report", tint = Color.White, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    },
                                    label = { Text(civicString(CivicStrings.REPORT_ISSUE), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CivicGreenDark) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CivicGreenPrimary,
                                        selectedTextColor = CivicGreenDark,
                                        unselectedIconColor = CivicGreenPrimary,
                                        unselectedTextColor = CivicGreenDark,
                                        indicatorColor = Color(0xFFDCFCE7)
                                    ),
                                    modifier = Modifier.testTag("bottom_tab_report")
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "community_feed",
                                    onClick = { navigateToTab("community_feed") },
                                    icon = { Icon(Icons.Default.Explore, contentDescription = "Feed") },
                                    label = { Text(civicString(CivicStrings.FEED), fontSize = 11.sp, fontWeight = if (currentRoute == "community_feed") FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag("bottom_tab_feed")
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "citizen_profile",
                                    onClick = { navigateToTab("citizen_profile") },
                                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                    label = { Text(civicString(CivicStrings.PROFILE), fontSize = 11.sp, fontWeight = if (currentRoute == "citizen_profile") FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag("bottom_tab_profile")
                                )
                            }
                        } else {
                            // Admin Mobile Bottom Navigation Bar
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp,
                                modifier = Modifier.testTag("admin_bottom_bar")
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == "admin_dashboard",
                                    onClick = { navigateToTab("admin_dashboard") },
                                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                                    label = { Text(civicString(CivicStrings.OVERVIEW), fontSize = 11.sp, fontWeight = if (currentRoute == "admin_dashboard") FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CivicAmber,
                                        selectedTextColor = CivicAmber,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = Color(0xFFFEF3C7)
                                    ),
                                    modifier = Modifier.testTag("admin_tab_dashboard")
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "admin_issues",
                                    onClick = { navigateToTab("admin_issues") },
                                    icon = { Icon(Icons.Default.Report, contentDescription = "Triage") },
                                    label = { Text(civicString(CivicStrings.COMPLAINTS), fontSize = 11.sp, fontWeight = if (currentRoute == "admin_issues") FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CivicAmber,
                                        selectedTextColor = CivicAmber,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = Color(0xFFFEF3C7)
                                    ),
                                    modifier = Modifier.testTag("admin_tab_issues")
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "admin_users",
                                    onClick = { navigateToTab("admin_users") },
                                    icon = { Icon(Icons.Default.Group, contentDescription = "Users") },
                                    label = { Text(civicString(CivicStrings.USERS), fontSize = 11.sp, fontWeight = if (currentRoute == "admin_users") FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CivicAmber,
                                        selectedTextColor = CivicAmber,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = Color(0xFFFEF3C7)
                                    ),
                                    modifier = Modifier.testTag("admin_tab_users")
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "notifications",
                                    onClick = { navigateToTab("notifications") },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (unreadNotifCount > 0) {
                                                    Badge(containerColor = CivicGreenPrimary) {
                                                        Text("$unreadNotifCount")
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.Notifications, contentDescription = "Notices")
                                        }
                                    },
                                    label = { Text(civicString(CivicStrings.ALERTS), fontSize = 11.sp, fontWeight = if (currentRoute == "notifications") FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CivicAmber,
                                        selectedTextColor = CivicAmber,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = Color(0xFFFEF3C7)
                                    ),
                                    modifier = Modifier.testTag("admin_tab_notifications")
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .widthIn(max = 1100.dp)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "public_home"
                    ) {
                        // Public / Citizen Home
                        composable("public_home") {
                            PublicHomeScreen(
                                issues = issues,
                                onReportClick = { navigateToTab("report_issue") },
                                onExploreClick = { navigateToTab("community_feed") },
                                onCategoryClick = { cat ->
                                    viewModel.setCategoryFilter(cat)
                                    navigateToTab("community_feed")
                                },
                                onIssueClick = { id ->
                                    navController.navigate("issue_detail/$id")
                                },
                                onUpvoteClick = { issue ->
                                    viewModel.toggleUpvote(issue)
                                },
                                onAiHelpClick = {
                                    navController.navigate("ai_chat")
                                },
                                onPlanJourneyClick = {
                                    navigateToTab("plan_journey")
                                }
                            )
                        }

                        // CivicLive Map & Urban Mobility
                        composable("civic_map") {
                            CivicLiveMapScreen(
                                viewModel = viewModel,
                                onNavigateToReport = { navigateToTab("report_issue") },
                                onNavigateToIssueDetail = { id ->
                                    navController.navigate("issue_detail/$id")
                                },
                                onNavigateToJourneyPlanner = {
                                    navigateToTab("plan_journey")
                                }
                            )
                        }

                        // AI Safe Journey Planner
                        composable("plan_journey") {
                            SafeJourneyPlannerScreen(
                                viewModel = viewModel,
                                onNavigateToMap = { navigateToTab("civic_map") },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // Community Issues Feed
                        composable("community_feed") {
                            CommunityIssuesScreen(
                                issues = issues,
                                filterState = filterState,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onCategoryFilterChange = { viewModel.setCategoryFilter(it) },
                                onStatusFilterChange = { viewModel.setStatusFilter(it) },
                                onSortChange = { viewModel.setSortOption(it) },
                                onIssueClick = { id ->
                                    navController.navigate("issue_detail/$id")
                                },
                                onUpvoteClick = { issue ->
                                    viewModel.toggleUpvote(issue)
                                }
                            )
                        }

                        // Report Issue Form
                        composable("report_issue") {
                            ReportIssueScreen(
                                onSubmit = { title, desc, cat, loc, addr, prio, photoUri, callback ->
                                    viewModel.submitIssue(title, desc, cat, loc, addr, prio, photoUri, callback)
                                    Toast.makeText(context, "Complaint filed successfully!", Toast.LENGTH_SHORT).show()
                                },
                                onViewComplaints = {
                                    navigateToTab("my_issues")
                                }
                            )
                        }

                        // Citizen Dashboard
                        composable("citizen_dashboard") {
                            CitizenDashboardScreen(
                                user = currentUser,
                                issues = issues,
                                onReportClick = { navigateToTab("report_issue") },
                                onViewMyIssues = { navigateToTab("my_issues") },
                                onExploreCommunity = { navigateToTab("community_feed") },
                                onIssueClick = { id ->
                                    navController.navigate("issue_detail/$id")
                                },
                                onUpvoteClick = { issue ->
                                    viewModel.toggleUpvote(issue)
                                }
                            )
                        }

                        // My Grievances Screen
                        composable("my_issues") {
                            MyIssuesScreen(
                                user = currentUser,
                                issues = issues,
                                onReportClick = { navigateToTab("report_issue") },
                                onIssueClick = { id ->
                                    navController.navigate("issue_detail/$id")
                                },
                                onUpvoteClick = { issue ->
                                    viewModel.toggleUpvote(issue)
                                }
                            )
                        }

                        // Citizen Profile Screen with full settings & reports history
                        composable("citizen_profile") {
                            val userIssues = issues.filter { 
                                it.reportedByName == (currentUser?.name ?: "Rahul Sharma") || 
                                it.reportedByEmail == (currentUser?.email ?: "rahul.sharma@example.com")
                            }
                            CitizenProfileScreen(
                                user = currentUser,
                                userIssues = userIssues,
                                selectedLanguage = selectedLanguage,
                                currentThemeMode = themeMode,
                                notificationSettings = notificationSettings,
                                onUpdateProfile = { name, email, phone ->
                                    viewModel.updateUserProfile(name, email, phone)
                                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                },
                                onChangePassword = { oldPass, newPass ->
                                    Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                                    true
                                },
                                onUpdateNotifications = { newSettings ->
                                    viewModel.updateNotificationSettings(newSettings)
                                    Toast.makeText(context, "Notification preferences saved", Toast.LENGTH_SHORT).show()
                                },
                                onSelectLanguage = { lang ->
                                    viewModel.setLanguage(lang)
                                    Toast.makeText(context, "Language changed to ${lang.englishName}", Toast.LENGTH_SHORT).show()
                                },
                                onSelectThemeMode = { mode ->
                                    viewModel.setThemeMode(mode)
                                    Toast.makeText(context, "Theme set to ${mode.displayName}", Toast.LENGTH_SHORT).show()
                                },
                                onSubmitFeedback = { type, subject, message, rating ->
                                    viewModel.submitFeedback(type, subject, message, rating)
                                    Toast.makeText(context, "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
                                },
                                onSelectIssue = { id ->
                                    navController.navigate("issue_detail/$id")
                                },
                                onSwitchToAdmin = {
                                    viewModel.switchRole(UserRole.ADMIN)
                                    navigateToTab("admin_dashboard")
                                    Toast.makeText(context, "Switched to Municipal Admin Mode", Toast.LENGTH_SHORT).show()
                                },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("public_home") { inclusive = true }
                                    }
                                },
                                onShowToast = { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        // Admin Dashboard
                        composable("admin_dashboard") {
                            AdminDashboardScreen(
                                adminUser = currentUser,
                                issues = issues,
                                citizens = allUsers,
                                predictiveHazards = predictiveHazards,
                                civicMobilityScores = civicMobilityScores,
                                onManageIssues = { navigateToTab("admin_issues") },
                                onManageUsers = { navigateToTab("admin_users") },
                                onIssueClick = { id ->
                                    navController.navigate("issue_detail/$id")
                                }
                            )
                        }

                        // Admin Issue Management
                        composable("admin_issues") {
                            AdminIssueManagementScreen(
                                issues = issues,
                                onIssueClick = { id ->
                                    navController.navigate("issue_detail/$id")
                                },
                                onAdminUpdate = { issue, newStatus, newDept, newPrio, resp, officer ->
                                    viewModel.adminUpdateIssue(issue, newStatus, newDept, newPrio, resp, officer)
                                    Toast.makeText(context, "Complaint ${issue.id} updated!", Toast.LENGTH_SHORT).show()
                                },
                                onDeleteIssue = { id ->
                                    viewModel.deleteIssue(id)
                                    Toast.makeText(context, "Ticket $id deleted", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        // Admin User Management
                        composable("admin_users") {
                            AdminUserManagementScreen(
                                users = allUsers,
                                onToggleUserStatus = { u ->
                                    viewModel.toggleUserStatus(u)
                                    val newStatus = if (u.status == "Active") "Suspended" else "Active"
                                    Toast.makeText(context, "User ${u.name} is now $newStatus", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        // Notifications Center
                        composable("notifications") {
                            NotificationsScreen(
                                notifications = notifications,
                                onMarkAsRead = { id -> viewModel.markNotificationRead(id) },
                                onMarkAllAsRead = { viewModel.markAllNotificationsRead() },
                                onNavigateToIssue = { issueId ->
                                    navController.navigate("issue_detail/$issueId")
                                },
                                onNavigateToMap = {
                                    navigateToTab("civic_map")
                                },
                                onNavigateToJourney = {
                                    navigateToTab("safe_journey")
                                },
                                onNotificationClick = { notif ->
                                    if (!notif.relatedIssueId.isNullOrBlank()) {
                                        navController.navigate("issue_detail/${notif.relatedIssueId}")
                                    } else if (notif.title.contains("bus", ignoreCase = true) || notif.title.contains("route", ignoreCase = true) || notif.title.contains("journey", ignoreCase = true)) {
                                        navigateToTab("safe_journey")
                                    } else if (notif.title.contains("waterlogging", ignoreCase = true) || notif.title.contains("hazard", ignoreCase = true)) {
                                        navigateToTab("civic_map")
                                    }
                                }
                            )
                        }

                        // Issue Detail Screen
                        composable(
                            route = "issue_detail/{issueId}",
                            arguments = listOf(navArgument("issueId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val issueId = backStackEntry.arguments?.getString("issueId") ?: ""
                            val issue = issues.find { it.id == issueId }

                            IssueDetailScreen(
                                issue = issue,
                                currentRole = currentRole,
                                onBack = { navController.popBackStack() },
                                onUpvoteClick = { iss -> viewModel.toggleUpvote(iss) },
                                onAdminUpdate = { iss, newStatus, newDept, newPrio, resp, officer ->
                                    viewModel.adminUpdateIssue(iss, newStatus, newDept, newPrio, resp, officer)
                                    Toast.makeText(context, "Complaint updated successfully!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        // Login Screen
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = { email, role ->
                                    viewModel.switchRole(role)
                                    if (role == UserRole.ADMIN) {
                                        navController.navigate("admin_dashboard") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate("public_home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                    Toast.makeText(context, "Signed in successfully!", Toast.LENGTH_SHORT).show()
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                },
                                onNavigateToForgotPassword = {
                                    navController.navigate("forgot_password")
                                },
                                onNavigateToAdminLogin = {
                                    navController.navigate("admin_login")
                                }
                            )
                        }

                        // Register Screen
                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = { name, email, phone ->
                                    viewModel.registerUser(name, email, phone)
                                    navController.navigate("public_home") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                    Toast.makeText(context, "Welcome to CivicFix, $name!", Toast.LENGTH_SHORT).show()
                                },
                                onNavigateToLogin = {
                                    navController.navigate("login")
                                }
                            )
                        }

                        // Forgot Password Screen
                        composable("forgot_password") {
                            ForgotPasswordScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login")
                                }
                            )
                        }

                        // Admin Login Screen
                        composable("admin_login") {
                            AdminLoginScreen(
                                onAdminLoginSuccess = { email ->
                                    viewModel.switchRole(UserRole.ADMIN)
                                    navController.navigate("admin_dashboard") {
                                        popUpTo("admin_login") { inclusive = true }
                                    }
                                    Toast.makeText(context, "Authorized admin command session initiated", Toast.LENGTH_SHORT).show()
                                },
                                onNavigateToCitizenLogin = {
                                    navController.navigate("login")
                                }
                            )
                        }

                        // AI Chat Assistant Screen
                        composable("ai_chat") {
                            val chatMessages by viewModel.chatMessages.collectAsState()
                            val isAiTyping by viewModel.isAiTyping.collectAsState()

                            AiChatAssistantScreen(
                                messages = chatMessages,
                                isAiTyping = isAiTyping,
                                onSendMessage = { prompt ->
                                    viewModel.sendAiChatMessage(prompt)
                                },
                                onClearChat = {
                                    viewModel.clearAiChat()
                                    Toast.makeText(context, "Chat history cleared", Toast.LENGTH_SHORT).show()
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onNavigateToReport = {
                                    navigateToTab("report_issue")
                                }
                            )
                        }
                    }

                    // Global CivicFix Floating AI Assistant Button (Positioned at bottom right, strictly above navigation)
                    if (!isAuthScreen && currentRoute != "ai_chat") {
                        GlobalFloatingAiButton(
                            onOpenAiChat = {
                                navController.navigate("ai_chat")
                            },
                            onQuickAction = { prompt ->
                                viewModel.triggerAiQuickAction(prompt)
                                navController.navigate("ai_chat")
                            },
                            bottomPadding = 80.dp,
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }
            }
        }
    }
}
}

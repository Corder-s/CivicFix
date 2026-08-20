package com.example.ui

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ai.ChatMessage
import com.example.data.ai.GeminiCivicChatService
import com.example.data.models.CivicIssue
import com.example.data.models.CivicNotification
import com.example.data.models.Department
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.data.models.IssueStatus
import com.example.data.models.User
import com.example.data.models.UserRole
import com.example.data.repository.CivicFixRepository
import com.example.data.localization.AppLanguage
import com.example.data.localization.CivicStrings
import com.example.data.services.BusRouteInfo
import com.example.data.services.BusService
import com.example.data.services.CivicIssueService
import com.example.data.services.DefaultBusService
import com.example.data.services.DefaultCivicIssueService
import com.example.data.services.DefaultEmergencyService
import com.example.data.services.DefaultMapService
import com.example.data.services.DefaultNotificationService
import com.example.data.services.DefaultRiskService
import com.example.data.services.DefaultRouteService
import com.example.data.services.DefaultWeatherService
import com.example.data.services.EmergencyFacility
import com.example.data.services.EmergencyService
import com.example.data.services.IncidentMarker
import com.example.data.services.IncidentSeverity
import com.example.data.services.IncidentType
import com.example.data.services.MapLayerFilter
import com.example.data.services.MapService
import com.example.data.services.NotificationService
import com.example.data.services.RiskService
import com.example.data.services.RoadRiskSegment
import com.example.data.services.RouteAlternative
import com.example.data.services.RouteService
import com.example.data.services.SafetyFactorItem
import com.example.data.services.TransitLeg
import com.example.data.services.TransitLegType
import com.example.data.services.TravelPreference
import com.example.data.services.WeatherSafetyAlert
import com.example.data.services.WeatherService

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationSettings(
    val issueStatusUpdates: Boolean = true,
    val complaintResolution: Boolean = true,
    val communityActivity: Boolean = true,
    val newAnnouncements: Boolean = true,
    val aiAssistantAlerts: Boolean = true
)

enum class AppThemeMode(val displayName: String) {
    SYSTEM("System Default"),
    LIGHT("Light Mode"),
    DARK("Dark Mode")
}

data class UserFeedback(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: String,
    val subject: String,
    val message: String,
    val rating: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class AuthUiState(
    val currentUser: User? = User(
        id = "USR-001",
        name = "Rahul Sharma",
        email = "rahul.sharma@example.com",
        phone = "+91 98765 43210",
        role = UserRole.CITIZEN,
        status = "Active",
        reportedCount = 4
    ),
    val isLoggedIn: Boolean = true,
    val selectedRole: UserRole = UserRole.CITIZEN
)

data class FilterState(
    val searchQuery: String = "",
    val categoryFilter: IssueCategory? = null,
    val statusFilter: IssueStatus? = null,
    val priorityFilter: IssuePriority? = null,
    val departmentFilter: Department? = null,
    val sortBy: SortOption = SortOption.NEWEST
)

enum class SortOption(val displayName: String) {
    NEWEST("Newest First"),
    MOST_UPVOTES("Most Upvoted"),
    HIGH_PRIORITY("High Priority First"),
    OLDEST("Oldest First")
}

class CivicFixViewModel(
    private val repository: CivicFixRepository,
    private val sharedPrefs: SharedPreferences? = null
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthUiState())
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    val currentUser: StateFlow<User?> = _authState
        .map { it.currentUser }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _authState.value.currentUser)

    val currentRole: StateFlow<UserRole> = _authState
        .map { it.selectedRole }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _authState.value.selectedRole)

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    val allIssues: StateFlow<List<CivicIssue>> = repository.allIssues
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val issues: StateFlow<List<CivicIssue>> = allIssues

    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<CivicNotification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<CivicNotification>> = allNotifications

    private val _selectedIssueId = MutableStateFlow<String?>(null)
    val selectedIssueId: StateFlow<String?> = _selectedIssueId.asStateFlow()

    // Multi-Language State persisted across app launches
    private val _selectedLanguage = MutableStateFlow(
        sharedPrefs?.getString("selected_app_language", AppLanguage.EN.code)?.let { AppLanguage.fromCode(it) } ?: AppLanguage.EN
    )
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    // Theme Mode State (System / Light / Dark)
    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    // Notification Preferences
    private val _notificationSettings = MutableStateFlow(NotificationSettings())
    val notificationSettings: StateFlow<NotificationSettings> = _notificationSettings.asStateFlow()

    // Feedback Store
    private val _feedbackList = MutableStateFlow<List<UserFeedback>>(emptyList())
    val feedbackList: StateFlow<List<UserFeedback>> = _feedbackList.asStateFlow()

    // Clean Service Abstractions for CivicFix Mobility
    val mapService: MapService = DefaultMapService()
    val civicIssueService: CivicIssueService = DefaultCivicIssueService(repository)
    val busService: BusService = DefaultBusService()
    val weatherService: WeatherService = DefaultWeatherService()
    val emergencyService: EmergencyService = DefaultEmergencyService()
    val routeService: RouteService = DefaultRouteService()
    val riskService: RiskService = DefaultRiskService()
    val predictiveRiskService: PredictiveRiskService = DefaultPredictiveRiskService()
    val notificationService: NotificationService = DefaultNotificationService(repository)

    // AI Predictive Road Risk Patterns & Civic Mobility Scores
    val predictiveHazards: StateFlow<List<PredictiveHazard>> = combine(
        repository.allIssues,
        MutableStateFlow(weatherService.getCurrentWeatherAlert())
    ) { issues, weather ->
        predictiveRiskService.getPredictiveHazards(issues, weather)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val civicMobilityScores: StateFlow<List<CivicMobilityScore>> = repository.allIssues.map { issues ->
        predictiveRiskService.getCivicMobilityScores(issues)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedWardCode = MutableStateFlow("W12")
    val selectedWardCode: StateFlow<String> = _selectedWardCode.asStateFlow()

    val selectedWardScore: StateFlow<CivicMobilityScore?> = combine(
        civicMobilityScores,
        _selectedWardCode
    ) { scores, code ->
        scores.firstOrNull { it.wardCode.equals(code, ignoreCase = true) } ?: scores.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun selectWard(wardCode: String) {
        _selectedWardCode.value = wardCode
    }

    // Emergency Mode & Emergency Routing State
    private val _isEmergencyModeActive = MutableStateFlow(false)
    val isEmergencyModeActive: StateFlow<Boolean> = _isEmergencyModeActive.asStateFlow()

    private val _selectedEmergencyFacility = MutableStateFlow<EmergencyFacility?>(null)
    val selectedEmergencyFacility: StateFlow<EmergencyFacility?> = _selectedEmergencyFacility.asStateFlow()

    val emergencyRouteInfo: StateFlow<EmergencyRouteInfo?> = combine(
        _selectedEmergencyFacility,
        _journeyOrigin,
        repository.allIssues
    ) { facility, origin, issues ->
        if (facility == null) null
        else {
            emergencyService.planEmergencyRoute(
                originName = origin.ifEmpty { "Current Live Location" },
                facility = facility,
                liveIssues = issues,
                weatherAlert = weatherService.getCurrentWeatherAlert()
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun activateEmergencyMode() {
        _isEmergencyModeActive.value = true
        if (_selectedEmergencyFacility.value == null) {
            _selectedEmergencyFacility.value = emergencyFacilities.firstOrNull()
        }
    }

    fun deactivateEmergencyMode() {
        _isEmergencyModeActive.value = false
    }

    fun selectEmergencyFacility(facility: EmergencyFacility) {
        _selectedEmergencyFacility.value = facility
    }

    // CivicLive Map State
    private val _mapLayerFilter = MutableStateFlow(MapLayerFilter.ALL)
    val mapLayerFilter: StateFlow<MapLayerFilter> = _mapLayerFilter.asStateFlow()

    private val _mapSearchQuery = MutableStateFlow("")
    val mapSearchQuery: StateFlow<String> = _mapSearchQuery.asStateFlow()

    private val _selectedMapSeverity = MutableStateFlow<IncidentSeverity?>(null)
    val selectedMapSeverity: StateFlow<IncidentSeverity?> = _selectedMapSeverity.asStateFlow()

    private val _selectedMapMarker = MutableStateFlow<IncidentMarker?>(null)
    val selectedMapMarker: StateFlow<IncidentMarker?> = _selectedMapMarker.asStateFlow()

    // Upvoted map marker IDs for immediate UI responsiveness
    private val _upvotedMapMarkerIds = MutableStateFlow<Set<String>>(emptySet())
    val upvotedMapMarkerIds: StateFlow<Set<String>> = _upvotedMapMarkerIds.asStateFlow()

    // Combined Map Markers: Merges live citizen issues from Room database with demo urban grid hazards & transit
    val allMapMarkers: StateFlow<List<IncidentMarker>> = combine(
        repository.allIssues,
        _upvotedMapMarkerIds
    ) { issuesList, upvotedSet ->
        val userMarkers = issuesList.map { issue ->
            val base = mapService.convertCivicIssueToMarker(issue)
            if (upvotedSet.contains(base.id)) {
                base.copy(upvotes = base.upvotes + 1, hasUserUpvoted = true)
            } else base
        }
        val demoIncidents = mapService.getDemonstrationIncidentMarkers().map { marker ->
            if (upvotedSet.contains(marker.id)) {
                marker.copy(upvotes = marker.upvotes + 1, hasUserUpvoted = true)
            } else marker
        }
        val demoBuses = mapService.getDemonstrationBusStops()
        val demoEmergency = mapService.getDemonstrationEmergencyLocations()

        userMarkers + demoIncidents + demoBuses + demoEmergency
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Map Markers based on current layer, query, and severity
    val filteredMapMarkers: StateFlow<List<IncidentMarker>> = combine(
        allMapMarkers,
        _mapLayerFilter,
        _mapSearchQuery,
        _selectedMapSeverity
    ) { markers, layer, query, severity ->
        mapService.filterMarkers(markers, layer, query, severity)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Transit Routes & Alerts
    val activeBusRoutes: List<BusRouteInfo> = busService.getActiveBusRoutes()
    val currentWeatherAlert: WeatherSafetyAlert = weatherService.getCurrentWeatherAlert()
    val emergencyFacilities: List<EmergencyFacility> = emergencyService.getEmergencyFacilities()
    val emergencyHelplines: Map<String, String> = emergencyService.getEmergencyHelplines()
    val highRiskCorridors: List<RoadRiskSegment> = routeService.getHighRiskCorridors()

    // AI Safe Journey Planner State
    private val _journeyOrigin = MutableStateFlow("Sector 62 IT Hub")
    val journeyOrigin: StateFlow<String> = _journeyOrigin.asStateFlow()

    private val _journeyDestination = MutableStateFlow("Connaught Place / City Center")
    val journeyDestination: StateFlow<String> = _journeyDestination.asStateFlow()

    private val _journeyDateTime = MutableStateFlow("Leave Now")
    val journeyDateTime: StateFlow<String> = _journeyDateTime.asStateFlow()

    private val _travelPreference = MutableStateFlow(TravelPreference.BEST_OVERALL)
    val travelPreference: StateFlow<TravelPreference> = _travelPreference.asStateFlow()

    private val _selectedRouteId = MutableStateFlow<String?>("route_best_overall")
    val selectedRouteId: StateFlow<String?> = _selectedRouteId.asStateFlow()

    private val _hasTriggeredDisruptionNotification = MutableStateFlow(false)

    val journeyRoutes: StateFlow<List<RouteAlternative>> = combine(
        _journeyOrigin,
        _journeyDestination,
        _journeyDateTime,
        _travelPreference,
        repository.allIssues
    ) { origin, dest, dt, pref, issuesList ->
        val routes = routeService.planSafeJourney(
            origin = origin,
            destination = dest,
            dateTimeText = dt,
            preference = pref,
            liveCivicIssues = issuesList,
            weatherAlert = currentWeatherAlert,
            busRoutes = activeBusRoutes
        )

        // Automatically trigger passenger notification when a corridor disruption is detected
        val disrupted = routes.firstOrNull { it.hasDisruption }
        if (disrupted != null && !_hasTriggeredDisruptionNotification.value) {
            _hasTriggeredDisruptionNotification.value = true
            viewModelScope.launch {
                repository.postTransitDisruptionAlert(
                    title = "Transit Disruption on Corridor",
                    message = "${disrupted.disruptionReason ?: "Road defect detected"}. Rerouted safe alternative is ready in Safe Journey Planner.",
                    targetRole = UserRole.CITIZEN
                )
            }
        }

        routes
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedRoute: StateFlow<RouteAlternative?> = combine(
        journeyRoutes,
        _selectedRouteId
    ) { routes, id ->
        routes.firstOrNull { it.id == id } ?: routes.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setJourneyOrigin(origin: String) {
        _journeyOrigin.value = origin
    }

    fun setJourneyDestination(dest: String) {
        _journeyDestination.value = dest
    }

    fun setJourneyDateTime(dateTime: String) {
        _journeyDateTime.value = dateTime
    }

    fun setTravelPreference(preference: TravelPreference) {
        _travelPreference.value = preference
        val routes = journeyRoutes.value
        val bestForPref = routes.firstOrNull { it.primaryTag == preference }
        if (bestForPref != null) {
            _selectedRouteId.value = bestForPref.id
        }
    }

    fun selectJourneyRoute(routeId: String) {
        _selectedRouteId.value = routeId
    }

    fun swapJourneyEndpoints() {
        val temp = _journeyOrigin.value
        _journeyOrigin.value = _journeyDestination.value
        _journeyDestination.value = temp
        showToast("Swapped start & destination")
    }

    fun rerouteToSafestAlternative() {
        _travelPreference.value = TravelPreference.SAFEST
        _selectedRouteId.value = "route_safest"
        showToast("Rerouted to Safest corridor (94/100 Safety)")
    }


    fun setMapLayerFilter(layer: MapLayerFilter) {
        _mapLayerFilter.value = layer
    }

    fun setMapSearchQuery(query: String) {
        _mapSearchQuery.value = query
    }

    fun setMapSeverityFilter(severity: IncidentSeverity?) {
        _selectedMapSeverity.value = severity
    }

    fun selectMapMarker(marker: IncidentMarker?) {
        _selectedMapMarker.value = marker
    }

    fun toggleUpvoteMapMarker(markerId: String) {
        val currentSet = _upvotedMapMarkerIds.value
        val isCurrentlyUpvoted = currentSet.contains(markerId)

        if (isCurrentlyUpvoted) {
            _upvotedMapMarkerIds.value = currentSet - markerId
            showToast("Hazard confirmation removed")
        } else {
            _upvotedMapMarkerIds.value = currentSet + markerId
            showToast("Hazard confirmed by you (+1 verified)")
        }

        // If related to a real civic issue in Room, also toggle Room DB
        val marker = allMapMarkers.value.firstOrNull { it.id == markerId }
        val relatedIssueId = marker?.relatedCivicIssueId
        if (relatedIssueId != null) {
            viewModelScope.launch {
                civicIssueService.toggleUpvote(relatedIssueId, isCurrentlyUpvoted)
            }
        }
    }

    fun setLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
        sharedPrefs?.edit()?.putString("selected_app_language", language.code)?.apply()
        showToast("Language changed to ${language.nativeName} (${language.englishName})")
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        showToast("Theme set to ${mode.displayName}")
    }

    fun updateNotificationSettings(settings: NotificationSettings) {
        _notificationSettings.value = settings
        showToast("Notification preferences updated")
    }

    fun updateUserProfile(name: String, email: String, phone: String) {
        val current = _authState.value.currentUser ?: return
        val updated = current.copy(
            name = name.trim().ifBlank { current.name },
            email = email.trim().ifBlank { current.email },
            phone = phone.trim().ifBlank { current.phone }
        )
        _authState.update { it.copy(currentUser = updated) }
        viewModelScope.launch {
            repository.saveUser(updated)
        }
        showToast("Profile updated successfully")
    }

    fun changePassword(oldPass: String, newPass: String): Boolean {
        if (newPass.length < 6) {
            showToast("Password must be at least 6 characters")
            return false
        }
        showToast("Password updated successfully")
        return true
    }

    fun submitFeedback(type: String, subject: String, message: String, rating: Int) {
        val feedback = UserFeedback(
            type = type,
            subject = subject,
            message = message,
            rating = rating
        )
        _feedbackList.update { it + feedback }
        showToast("Thank you! Your feedback has been submitted.")
    }

    // AI Chat Quick Actions
    fun triggerAiQuickAction(actionType: String) {
        when (actionType) {
            "report_issue" -> {
                sendAiChatMessage("I want to report an issue. Guide me step by step on how to file it.")
            }
            "track_report" -> {
                sendAiChatMessage("How can I track the status of my complaint CIV-2026-0081?")
            }
            "find_similar" -> {
                sendAiChatMessage("How does CivicFix detect duplicate issues in my neighborhood?")
            }
            "get_help" -> {
                sendAiChatMessage("Help me understand CivicFix features, departments, and emergency helplines.")
            }
            else -> {
                sendAiChatMessage(actionType)
            }
        }
    }

    // AI Chat Agent State
    private val chatService = GeminiCivicChatService()

    private val initialWelcomeMessage = ChatMessage(
        id = "welcome_01",
        text = "👋 Hello! I am your **CivicFix AI Guide**.\n\nI can help you:\n• File and report civic issues step-by-step\n• Find which municipal department handles your complaint\n• Track grievance resolution timelines & status\n• Provide 24/7 civic emergency helpline numbers\n\nHow can I assist you today?",
        isUser = false,
        quickSuggestions = listOf(
            "How do I report a pothole?",
            "Which dept handles water leaks?",
            "How do I track my complaints?",
            "Emergency helpline numbers"
        )
    )

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(initialWelcomeMessage))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiTyping = MutableStateFlow(false)
    val isAiTyping: StateFlow<Boolean> = _isAiTyping.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Filter updates
    fun setSearchQuery(query: String) {
        _filterState.update { it.copy(searchQuery = query) }
    }

    fun setCategoryFilter(category: IssueCategory?) {
        _filterState.update { it.copy(categoryFilter = category) }
    }

    fun setStatusFilter(status: IssueStatus?) {
        _filterState.update { it.copy(statusFilter = status) }
    }

    fun setPriorityFilter(priority: IssuePriority?) {
        _filterState.update { it.copy(priorityFilter = priority) }
    }

    fun setDepartmentFilter(dept: Department?) {
        _filterState.update { it.copy(departmentFilter = dept) }
    }

    fun setSortBy(sort: SortOption) {
        _filterState.update { it.copy(sortBy = sort) }
    }

    fun setSortOption(sort: SortOption) {
        setSortBy(sort)
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    fun selectIssue(id: String?) {
        _selectedIssueId.value = id
    }

    // Auth actions
    fun loginAsDemoCitizen() {
        _authState.value = AuthUiState(
            currentUser = User(
                id = "USR-001",
                name = "Rahul Sharma",
                email = "rahul.sharma@example.com",
                phone = "+91 98765 43210",
                role = UserRole.CITIZEN,
                status = "Active",
                reportedCount = 4
            ),
            isLoggedIn = true,
            selectedRole = UserRole.CITIZEN
        )
        showToast("Logged in as Citizen Rahul Sharma")
    }

    fun loginAsDemoAdmin() {
        _authState.value = AuthUiState(
            currentUser = User(
                id = "ADM-001",
                name = "Priya Verma (Zonal Officer)",
                email = "admin@citycouncil.gov",
                phone = "+91 94000 11223",
                role = UserRole.ADMIN,
                status = "Active",
                reportedCount = 0
            ),
            isLoggedIn = true,
            selectedRole = UserRole.ADMIN
        )
        showToast("Logged in as Municipal Admin Officer")
    }

    fun switchRole(role: UserRole) {
        if (role == UserRole.ADMIN) {
            loginAsDemoAdmin()
        } else {
            loginAsDemoCitizen()
        }
    }

    fun toggleRole() {
        if (_authState.value.selectedRole == UserRole.ADMIN) {
            switchRole(UserRole.CITIZEN)
        } else {
            switchRole(UserRole.ADMIN)
        }
    }

    fun registerUser(name: String, email: String, phone: String) {
        val newUser = User(
            id = "USR-${(100..999).random()}",
            name = name.ifBlank { "New Citizen" },
            email = email.ifBlank { "citizen@civicfix.org" },
            phone = phone.ifBlank { "+91 98000 00000" },
            role = UserRole.CITIZEN,
            status = "Active",
            joinedTimestamp = System.currentTimeMillis()
        )
        _authState.value = AuthUiState(
            currentUser = newUser,
            isLoggedIn = true,
            selectedRole = UserRole.CITIZEN
        )
        viewModelScope.launch {
            repository.allUsers // Can insert user into DB
        }
    }

    fun loginWithCredentials(email: String, name: String, role: UserRole) {
        _authState.value = AuthUiState(
            currentUser = User(
                id = "USR-${(100..999).random()}",
                name = name.ifBlank { "Citizen User" },
                email = email.ifBlank { "citizen@civicfix.org" },
                phone = "+91 98000 00000",
                role = role,
                status = "Active"
            ),
            isLoggedIn = true,
            selectedRole = role
        )
        showToast("Welcome back, ${_authState.value.currentUser?.name}!")
    }

    fun logout() {
        _authState.value = AuthUiState(
            currentUser = null,
            isLoggedIn = false,
            selectedRole = UserRole.CITIZEN
        )
        showToast("Logged out successfully")
    }

    // Complaint & Issue actions
    fun submitIssue(
        title: String,
        description: String,
        category: IssueCategory,
        location: String,
        address: String,
        priority: IssuePriority,
        photoUri: String? = null,
        onSuccess: (String) -> Unit
    ) {
        reportIssue(title, description, category, location, address, priority, photoUri, onSuccess)
    }

    fun reportIssue(
        title: String,
        description: String,
        category: IssueCategory,
        location: String,
        address: String,
        priority: IssuePriority,
        photoUri: String? = null,
        onSuccess: (String) -> Unit
    ) {
        val user = _authState.value.currentUser
        val reporterName = user?.name ?: "Rahul Sharma"
        val reporterEmail = user?.email ?: "rahul.sharma@example.com"

        viewModelScope.launch {
            val complaintId = repository.reportNewIssue(
                title = title,
                description = description,
                category = category,
                location = location,
                address = address,
                priority = priority,
                reporterName = reporterName,
                reporterEmail = reporterEmail,
                photoUri = photoUri
            )
            showToast("Issue $complaintId submitted successfully!")
            onSuccess(complaintId)
        }
    }

    fun toggleUpvote(issue: CivicIssue) {
        viewModelScope.launch {
            repository.toggleUpvote(issue.id, issue.hasUserUpvoted)
        }
    }

    fun adminUpdateIssue(
        issue: CivicIssue,
        newStatus: IssueStatus,
        newDepartment: Department,
        newPriority: IssuePriority,
        officialResponse: String?,
        officerName: String?
    ) {
        updateIssueByAdmin(issue, newStatus, newDepartment, newPriority, officialResponse, officerName)
    }

    fun updateIssueByAdmin(
        issue: CivicIssue,
        newStatus: IssueStatus,
        newDepartment: Department,
        newPriority: IssuePriority,
        officialResponse: String?,
        officerName: String?
    ) {
        viewModelScope.launch {
            val isResolvedNow = newStatus == IssueStatus.RESOLVED && issue.status != IssueStatus.RESOLVED
            val updated = issue.copy(
                status = newStatus,
                assignedDepartment = newDepartment,
                priority = newPriority,
                authorityResponse = officialResponse?.ifBlank { issue.authorityResponse },
                authorityOfficerName = officerName?.ifBlank { "Municipal Officer" },
                resolutionTimestamp = if (isResolvedNow) System.currentTimeMillis() else issue.resolutionTimestamp
            )
            repository.saveIssue(updated)
            showToast("Complaint ${issue.id} updated to ${newStatus.displayName}")
        }
    }

    fun deleteIssue(id: String) {
        viewModelScope.launch {
            repository.deleteIssue(id)
            showToast("Complaint $id deleted")
        }
    }

    fun toggleUserStatus(user: User) {
        val newStatus = if (user.status == "Active") "Suspended" else "Active"
        viewModelScope.launch {
            repository.updateUserStatus(user.id, newStatus)
            showToast("Citizen ${user.name} status: $newStatus")
        }
    }

    fun markNotificationRead(id: Int) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
            showToast("All notifications marked as read")
        }
    }

    // AI Chat Actions
    fun sendAiChatMessage(prompt: String) {
        val cleanPrompt = prompt.trim()
        if (cleanPrompt.isBlank()) return

        val userMessage = ChatMessage(
            text = cleanPrompt,
            isUser = true
        )
        _chatMessages.update { it + userMessage }
        _isAiTyping.value = true

        viewModelScope.launch {
            try {
                val replyText = chatService.sendMessage(cleanPrompt)
                val aiResponse = ChatMessage(
                    text = replyText,
                    isUser = false,
                    quickSuggestions = getContextualSuggestions(cleanPrompt)
                )
                _chatMessages.update { it + aiResponse }
            } catch (e: Exception) {
                val errorResponse = ChatMessage(
                    text = "I encountered a momentary issue processing your request. Please try again or tap a quick suggestion.",
                    isUser = false,
                    isError = true,
                    quickSuggestions = listOf("How do I report an issue?", "Emergency helpline numbers")
                )
                _chatMessages.update { it + errorResponse }
            } finally {
                _isAiTyping.value = false
            }
        }
    }

    fun clearAiChat() {
        chatService.clearHistory()
        _chatMessages.value = listOf(initialWelcomeMessage)
        showToast("Conversation cleared")
    }

    private fun getContextualSuggestions(prompt: String): List<String> {
        val lower = prompt.lowercase()
        return when {
            lower.contains("college") || lower.contains("leave") || lower.contains("depart") || lower.contains("should i go") -> listOf(
                "Which route is safest?",
                "Is there waterlogging on Mathura Road?",
                "What is the cheapest transit option?"
            )
            lower.contains("waterlog") || lower.contains("flood") -> listOf(
                "Show safest elevated route",
                "Which buses are delayed?"
            )
            lower.contains("bus") -> listOf(
                "Which bus should I take?",
                "Is Bus Route 52 delayed?",
                "Cheapest route to City Center"
            )
            lower.contains("pothole") || lower.contains("road") -> listOf(
                "How do I track my submitted road report?",
                "Which department is PWD?"
            )
            lower.contains("water") -> listOf(
                "What is the water emergency number?",
                "How do I report low water pressure?"
            )
            lower.contains("street") || lower.contains("light") -> listOf(
                "How to find the pole number?",
                "How long does streetlight repair take?"
            )
            lower.contains("garbage") || lower.contains("waste") -> listOf(
                "How do I request a new community bin?",
                "Who is the Sanitation Officer?"
            )
            else -> listOf(
                "Should I leave now?",
                "Which route is safest?",
                "How do I report a new issue?",
                "Emergency civic numbers"
            )
        }
    }
}

class CivicFixViewModelFactory(
    private val repository: CivicFixRepository,
    private val sharedPrefs: SharedPreferences? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CivicFixViewModel::class.java)) {
            return CivicFixViewModel(repository, sharedPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

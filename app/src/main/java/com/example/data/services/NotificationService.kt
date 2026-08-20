package com.example.data.services

import com.example.data.models.CivicNotification
import com.example.data.models.UserRole
import com.example.data.repository.CivicFixRepository
import kotlinx.coroutines.flow.Flow

/**
 * Service abstraction for managing real-time notifications, hazard broadcasts, and alerts.
 */
interface NotificationService {
    val allNotifications: Flow<List<CivicNotification>>
    suspend fun broadcastHazardAlert(marker: IncidentMarker)
}

class DefaultNotificationService(
    private val repository: CivicFixRepository
) : NotificationService {
    override val allNotifications: Flow<List<CivicNotification>> = repository.allNotifications

    override suspend fun broadcastHazardAlert(marker: IncidentMarker) {
        // Can post notification via repository or push backend
    }
}

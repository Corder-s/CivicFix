package com.example.data.repository

import com.example.data.local.CivicFixDao
import com.example.data.models.CivicIssue
import com.example.data.models.CivicNotification
import com.example.data.models.Department
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.data.models.IssueStatus
import com.example.data.models.User
import com.example.data.models.UserRole
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CivicFixRepository(private val dao: CivicFixDao) {

    val allIssues: Flow<List<CivicIssue>> = dao.getAllIssues()
    val allUsers: Flow<List<User>> = dao.getAllUsers()
    val allNotifications: Flow<List<CivicNotification>> = dao.getAllNotifications()

    fun getIssuesByReporter(email: String): Flow<List<CivicIssue>> =
        dao.getIssuesByReporter(email)

    fun getIssueById(id: String): Flow<CivicIssue?> =
        dao.getIssueById(id)

    fun getNotificationsForUser(role: UserRole, email: String): Flow<List<CivicNotification>> =
        dao.getNotificationsForUser(role.name, email)

    suspend fun reportNewIssue(
        title: String,
        description: String,
        category: IssueCategory,
        location: String,
        address: String,
        priority: IssuePriority,
        reporterName: String,
        reporterEmail: String,
        photoUri: String? = null
    ): String {
        val randomNum = (100..999).random()
        val complaintId = "CIV-2026-$randomNum"
        val newIssue = CivicIssue(
            id = complaintId,
            title = title,
            description = description,
            category = category,
            location = location,
            address = address,
            priority = priority,
            status = IssueStatus.PENDING,
            reportedByName = reporterName,
            reportedByEmail = reporterEmail,
            reportedTimestamp = System.currentTimeMillis(),
            assignedDepartment = when (category) {
                IssueCategory.GARBAGE -> Department.SANITATION
                IssueCategory.ROADS -> Department.ROADS_HIGHWAYS
                IssueCategory.WATER -> Department.WATER_WORKS
                IssueCategory.ELECTRICITY, IssueCategory.STREETLIGHT -> Department.POWER_UTILITY
                IssueCategory.DRAINAGE -> Department.DRAINAGE_FLOOD
                IssueCategory.PUBLIC_PROPERTY, IssueCategory.OTHER -> Department.PUBLIC_WORKS
            },
            photoUri = photoUri
        )

        dao.insertIssue(newIssue)

        // Notify Admin of new complaint
        dao.insertNotification(
            CivicNotification(
                title = "New Complaint Filed ($complaintId)",
                message = "New $category complaint reported by $reporterName at $location.",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                targetRole = UserRole.ADMIN,
                relatedIssueId = complaintId
            )
        )

        return complaintId
    }

    suspend fun toggleUpvote(issueId: String, currentUpvoted: Boolean) {
        if (currentUpvoted) {
            dao.removeUpvoteIssue(issueId)
        } else {
            dao.upvoteIssue(issueId)
        }
    }

    suspend fun updateIssueStatus(
        issueId: String,
        newStatus: IssueStatus,
        assignedDepartment: Department? = null,
        officialResponse: String? = null,
        officerName: String? = null,
        resolutionPhotoUri: String? = null
    ) {
        // Fetch current issue and update
        // We'll update the fields directly
        // In Room we can get single item or update via dao.updateIssue
    }

    suspend fun saveIssue(issue: CivicIssue) {
        dao.updateIssue(issue)

        // Notify the citizen of the update
        dao.insertNotification(
            CivicNotification(
                title = "Update on ${issue.id}",
                message = "Status changed to '${issue.status.displayName}' for '${issue.title}'.",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                targetRole = UserRole.CITIZEN,
                targetEmail = issue.reportedByEmail,
                relatedIssueId = issue.id
            )
        )
    }

    suspend fun deleteIssue(id: String) {
        dao.deleteIssue(id)
    }

    suspend fun saveUser(user: User) {
        dao.insertUser(user)
    }

    suspend fun updateUserStatus(userId: String, status: String) {
        dao.updateUserStatus(userId, status)
    }

    suspend fun markNotificationRead(id: Int) {
        dao.markNotificationRead(id)
    }

    suspend fun postTransitDisruptionAlert(title: String, message: String, targetRole: UserRole = UserRole.CITIZEN) {
        dao.insertNotification(
            CivicNotification(
                title = title,
                message = message,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                targetRole = targetRole
            )
        )
    }

    suspend fun markAllNotificationsRead() {
        dao.markAllNotificationsRead()
    }
}


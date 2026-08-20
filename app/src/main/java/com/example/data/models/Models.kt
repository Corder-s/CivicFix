package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class IssueCategory(val displayName: String, val iconName: String) {
    GARBAGE("Garbage", "delete"),
    ROADS("Roads & Potholes", "traffic"),
    WATER("Water Supply", "water_drop"),
    ELECTRICITY("Electricity", "bolt"),
    DRAINAGE("Drainage & Sewage", "waves"),
    STREETLIGHT("Streetlights", "lightbulb"),
    PUBLIC_PROPERTY("Public Property", "account_balance"),
    OTHER("Other Issues", "more_horiz")
}

enum class IssueStatus(val displayName: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved"),
    REJECTED("Rejected")
}

enum class IssuePriority(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class Department(val displayName: String) {
    UNASSIGNED("Unassigned"),
    SANITATION("Sanitation Dept"),
    ROADS_HIGHWAYS("Roads & Infrastructure"),
    WATER_WORKS("Water Supply & Sewerage"),
    POWER_UTILITY("Electricity & Power"),
    DRAINAGE_FLOOD("Drainage & Flood Control"),
    PUBLIC_WORKS("Public Works Dept (PWD)")
}

enum class UserRole {
    CITIZEN,
    ADMIN
}

@Entity(tableName = "civic_issues")
data class CivicIssue(
    @PrimaryKey val id: String, // e.g. CIV-2026-001
    val title: String,
    val description: String,
    val category: IssueCategory,
    val location: String,
    val address: String,
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090,
    val status: IssueStatus = IssueStatus.PENDING,
    val priority: IssuePriority = IssuePriority.MEDIUM,
    val upvotes: Int = 0,
    val hasUserUpvoted: Boolean = false,
    val reportedByName: String,
    val reportedByEmail: String,
    val reportedTimestamp: Long = System.currentTimeMillis(),
    val assignedDepartment: Department = Department.UNASSIGNED,
    val authorityResponse: String? = null,
    val authorityOfficerName: String? = null,
    val resolutionTimestamp: Long? = null,
    val photoUri: String? = null,
    val resolutionPhotoUri: String? = null
)

@Entity(tableName = "app_users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val joinedTimestamp: Long = System.currentTimeMillis(),
    val status: String = "Active", // "Active", "Suspended"
    val reportedCount: Int = 0
)

@Entity(tableName = "app_notifications")
data class CivicNotification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val targetRole: UserRole = UserRole.CITIZEN,
    val targetEmail: String? = null,
    val relatedIssueId: String? = null
)

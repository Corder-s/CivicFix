package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.CivicIssue
import com.example.data.models.CivicNotification
import com.example.data.models.IssueCategory
import com.example.data.models.IssueStatus
import com.example.data.models.User
import kotlinx.coroutines.flow.Flow

@Dao
interface CivicFixDao {
    @Query("SELECT * FROM civic_issues ORDER BY reportedTimestamp DESC")
    fun getAllIssues(): Flow<List<CivicIssue>>

    @Query("SELECT * FROM civic_issues WHERE id = :id")
    fun getIssueById(id: String): Flow<CivicIssue?>

    @Query("SELECT * FROM civic_issues WHERE reportedByEmail = :email ORDER BY reportedTimestamp DESC")
    fun getIssuesByReporter(email: String): Flow<List<CivicIssue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssue(issue: CivicIssue)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssues(issues: List<CivicIssue>)

    @Update
    suspend fun updateIssue(issue: CivicIssue)

    @Query("DELETE FROM civic_issues WHERE id = :id")
    suspend fun deleteIssue(id: String)

    @Query("UPDATE civic_issues SET upvotes = upvotes + 1, hasUserUpvoted = 1 WHERE id = :id AND hasUserUpvoted = 0")
    suspend fun upvoteIssue(id: String)

    @Query("UPDATE civic_issues SET upvotes = upvotes - 1, hasUserUpvoted = 0 WHERE id = :id AND hasUserUpvoted = 1")
    suspend fun removeUpvoteIssue(id: String)

    // User management
    @Query("SELECT * FROM app_users ORDER BY joinedTimestamp DESC")
    fun getAllUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE app_users SET status = :status WHERE id = :userId")
    suspend fun updateUserStatus(userId: String, status: String)

    // Notifications
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<CivicNotification>>

    @Query("SELECT * FROM app_notifications WHERE targetRole = :role OR targetEmail = :email ORDER BY timestamp DESC")
    fun getNotificationsForUser(role: String, email: String): Flow<List<CivicNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: CivicNotification)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<CivicNotification>)

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Int)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()
}

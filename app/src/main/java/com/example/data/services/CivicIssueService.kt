package com.example.data.services

import com.example.data.models.CivicIssue
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.data.repository.CivicFixRepository
import kotlinx.coroutines.flow.Flow

/**
 * Service abstraction for Civic Issue operations and synchronizing with Urban Mobility Map.
 */
interface CivicIssueService {
    val allIssues: Flow<List<CivicIssue>>
    suspend fun createReport(
        title: String,
        description: String,
        category: IssueCategory,
        location: String,
        address: String,
        priority: IssuePriority,
        reporterName: String,
        reporterEmail: String,
        photoUri: String? = null
    ): String
    suspend fun toggleUpvote(issueId: String, currentUpvoted: Boolean)
}

class DefaultCivicIssueService(
    private val repository: CivicFixRepository
) : CivicIssueService {
    override val allIssues: Flow<List<CivicIssue>> = repository.allIssues

    override suspend fun createReport(
        title: String,
        description: String,
        category: IssueCategory,
        location: String,
        address: String,
        priority: IssuePriority,
        reporterName: String,
        reporterEmail: String,
        photoUri: String?
    ): String {
        return repository.reportNewIssue(
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
    }

    override suspend fun toggleUpvote(issueId: String, currentUpvoted: Boolean) {
        repository.toggleUpvote(issueId, currentUpvoted)
    }
}

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CivicIssue
import com.example.data.models.IssueCategory
import com.example.data.models.IssueStatus
import com.example.ui.FilterState
import com.example.ui.SortOption
import com.example.ui.components.IssueCardItem
import com.example.ui.theme.CivicGreenLight
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyLight
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityIssuesScreen(
    issues: List<CivicIssue>,
    filterState: FilterState,
    onSearchChange: (String) -> Unit,
    onCategoryFilterChange: (IssueCategory?) -> Unit,
    onStatusFilterChange: (IssueStatus?) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onIssueClick: (String) -> Unit,
    onUpvoteClick: (CivicIssue) -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }

    // Filter and Sort calculation
    val filteredIssues = issues.filter { issue ->
        val matchesSearch = filterState.searchQuery.isBlank() ||
                issue.title.contains(filterState.searchQuery, ignoreCase = true) ||
                issue.description.contains(filterState.searchQuery, ignoreCase = true) ||
                issue.location.contains(filterState.searchQuery, ignoreCase = true) ||
                issue.id.contains(filterState.searchQuery, ignoreCase = true)

        val matchesCategory = filterState.categoryFilter == null || issue.category == filterState.categoryFilter
        val matchesStatus = filterState.statusFilter == null || issue.status == filterState.statusFilter

        matchesSearch && matchesCategory && matchesStatus
    }.let { list ->
        when (filterState.sortBy) {
            SortOption.NEWEST -> list.sortedByDescending { it.reportedTimestamp }
            SortOption.MOST_UPVOTES -> list.sortedByDescending { it.upvotes }
            SortOption.HIGH_PRIORITY -> list.sortedByDescending { it.priority.ordinal }
            SortOption.OLDEST -> list.sortedBy { it.reportedTimestamp }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicSlate100)
    ) {
        // Top Search & Sort Bar
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = filterState.searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Search complaints, sector, ID...", fontSize = 13.sp, color = CivicSlate400) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = CivicSlate400)
                        },
                        trailingIcon = {
                            if (filterState.searchQuery.isNotBlank()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = CivicSlate400)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CivicNavyPrimary,
                            unfocusedBorderColor = CivicSlate200,
                            focusedContainerColor = CivicSlate100,
                            unfocusedContainerColor = CivicSlate100
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("search_issues_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Sort Button with dropdown
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CivicNavyContainer,
                            modifier = Modifier
                                .height(50.dp)
                                .clickable { showSortMenu = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sort,
                                    contentDescription = "Sort",
                                    tint = CivicNavyPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Sort",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CivicNavyPrimary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option.displayName,
                                            fontWeight = if (filterState.sortBy == option) FontWeight.Bold else FontWeight.Normal,
                                            color = if (filterState.sortBy == option) CivicNavyPrimary else CivicSlate800
                                        )
                                    },
                                    onClick = {
                                        onSortChange(option)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = filterState.categoryFilter == null,
                        onClick = { onCategoryFilterChange(null) },
                        label = { Text("All Categories", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CivicNavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )

                    IssueCategory.values().forEach { category ->
                        FilterChip(
                            selected = filterState.categoryFilter == category,
                            onClick = {
                                onCategoryFilterChange(if (filterState.categoryFilter == category) null else category)
                            },
                            label = { Text(category.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CivicNavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Status Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = filterState.statusFilter == null,
                        onClick = { onStatusFilterChange(null) },
                        label = { Text("All Statuses", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CivicGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )

                    IssueStatus.values().forEach { status ->
                        FilterChip(
                            selected = filterState.statusFilter == status,
                            onClick = {
                                onStatusFilterChange(if (filterState.statusFilter == status) null else status)
                            },
                            label = { Text(status.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CivicGreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Issue List Counter Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredIssues.size} Issues Found",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CivicSlate800
            )
            Text(
                text = "Sorted by ${filterState.sortBy.displayName}",
                fontSize = 11.sp,
                color = CivicSlate600
            )
        }

        if (filteredIssues.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = CivicSlate400,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No civic issues match your filter",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate800
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try clearing search keywords or resetting categories.",
                        fontSize = 12.sp,
                        color = CivicSlate600
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredIssues, key = { it.id }) { issue ->
                    IssueCardItem(
                        issue = issue,
                        onIssueClick = { onIssueClick(issue.id) },
                        onUpvoteClick = { onUpvoteClick(issue) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.CivicIssue
import com.example.data.models.Department
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.data.models.IssueStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CivicFix", appName)
  }

  @Test
  fun `verify CivicIssue creation`() {
    val issue = CivicIssue(
      id = "CIVIC-101",
      title = "Test Pothole",
      description = "Large pothole in road",
      category = IssueCategory.ROADS,
      location = "Main Street",
      address = "123 Main St",
      status = IssueStatus.PENDING,
      priority = IssuePriority.HIGH,
      assignedDepartment = Department.PUBLIC_WORKS,
      reportedByName = "Citizen User",
      reportedByEmail = "citizen@example.com",
      reportedTimestamp = System.currentTimeMillis()
    )
    assertNotNull(issue)
    assertEquals("CIVIC-101", issue.id)
    assertEquals(IssueCategory.ROADS, issue.category)
  }
}

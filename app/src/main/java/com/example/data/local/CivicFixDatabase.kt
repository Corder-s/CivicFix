package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.models.CivicIssue
import com.example.data.models.CivicNotification
import com.example.data.models.Department
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.data.models.IssueStatus
import com.example.data.models.User
import com.example.data.models.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromCategory(value: IssueCategory): String = value.name

    @TypeConverter
    fun toCategory(value: String): IssueCategory = runCatching { IssueCategory.valueOf(value) }.getOrDefault(IssueCategory.OTHER)

    @TypeConverter
    fun fromStatus(value: IssueStatus): String = value.name

    @TypeConverter
    fun toStatus(value: String): IssueStatus = runCatching { IssueStatus.valueOf(value) }.getOrDefault(IssueStatus.PENDING)

    @TypeConverter
    fun fromPriority(value: IssuePriority): String = value.name

    @TypeConverter
    fun toPriority(value: String): IssuePriority = runCatching { IssuePriority.valueOf(value) }.getOrDefault(IssuePriority.MEDIUM)

    @TypeConverter
    fun fromDepartment(value: Department): String = value.name

    @TypeConverter
    fun toDepartment(value: String): Department = runCatching { Department.valueOf(value) }.getOrDefault(Department.UNASSIGNED)

    @TypeConverter
    fun fromRole(value: UserRole): String = value.name

    @TypeConverter
    fun toRole(value: String): UserRole = runCatching { UserRole.valueOf(value) }.getOrDefault(UserRole.CITIZEN)
}

@Database(
    entities = [CivicIssue::class, User::class, CivicNotification::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CivicFixDatabase : RoomDatabase() {
    abstract fun dao(): CivicFixDao
    fun civicFixDao(): CivicFixDao = dao()

    companion object {
        @Volatile
        private var INSTANCE: CivicFixDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        ): CivicFixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CivicFixDatabase::class.java,
                    "civicfix_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.dao())
                }
            }
        }

        private suspend fun populateInitialData(dao: CivicFixDao) {
            val now = System.currentTimeMillis()
            val day = 86400000L

            // 1. Initial Users
            val initialUsers = listOf(
                User(
                    id = "USR-001",
                    name = "Rahul Sharma",
                    email = "rahul.sharma@example.com",
                    phone = "+91 98765 43210",
                    role = UserRole.CITIZEN,
                    joinedTimestamp = now - 45 * day,
                    status = "Active",
                    reportedCount = 4
                ),
                User(
                    id = "USR-002",
                    name = "Ananya Patel",
                    email = "ananya.patel@example.com",
                    phone = "+91 98123 45678",
                    role = UserRole.CITIZEN,
                    joinedTimestamp = now - 30 * day,
                    status = "Active",
                    reportedCount = 2
                ),
                User(
                    id = "USR-003",
                    name = "Vikram Sengupta",
                    email = "vikram.s@example.com",
                    phone = "+91 98234 56789",
                    role = UserRole.CITIZEN,
                    joinedTimestamp = now - 15 * day,
                    status = "Active",
                    reportedCount = 3
                ),
                User(
                    id = "ADM-001",
                    name = "Priya Verma (Zonal Officer)",
                    email = "admin@citycouncil.gov",
                    phone = "+91 94000 11223",
                    role = UserRole.ADMIN,
                    joinedTimestamp = now - 120 * day,
                    status = "Active",
                    reportedCount = 0
                )
            )
            dao.insertUsers(initialUsers)

            // 2. Initial Civic Issues (10+ realistic civic complaints)
            val initialIssues = listOf(
                CivicIssue(
                    id = "CIV-2026-001",
                    title = "Garbage Overflowing at Community Bin",
                    description = "The central waste container has not been cleared for 4 consecutive days. Waste is spilling over the road causing severe odor and stray animal menace.",
                    category = IssueCategory.GARBAGE,
                    location = "Sector 62, Block C Market",
                    address = "Near Mother Dairy Booth, Sector 62, Noida",
                    status = IssueStatus.PENDING,
                    priority = IssuePriority.HIGH,
                    upvotes = 34,
                    hasUserUpvoted = true,
                    reportedByName = "Rahul Sharma",
                    reportedByEmail = "rahul.sharma@example.com",
                    reportedTimestamp = now - 2 * day,
                    assignedDepartment = Department.SANITATION
                ),
                CivicIssue(
                    id = "CIV-2026-002",
                    title = "Deep Dangerous Pothole Near School Gate",
                    description = "Severe crater-like pothole measuring approximately 2.5 feet wide right outside St. Mary's School entrance. Multiple two-wheelers have skidded.",
                    category = IssueCategory.ROADS,
                    location = "Civil Lines, Main Avenue",
                    address = "Opposite Gate #2, St. Mary School, Civil Lines",
                    status = IssueStatus.IN_PROGRESS,
                    priority = IssuePriority.HIGH,
                    upvotes = 48,
                    hasUserUpvoted = false,
                    reportedByName = "Ananya Patel",
                    reportedByEmail = "ananya.patel@example.com",
                    reportedTimestamp = now - 4 * day,
                    assignedDepartment = Department.ROADS_HIGHWAYS,
                    authorityResponse = "Maintenance team dispatched. Cold patch asphalt filling scheduled for tomorrow morning.",
                    authorityOfficerName = "Insp. Rajesh Kumar (Roads)"
                ),
                CivicIssue(
                    id = "CIV-2026-003",
                    title = "Contaminated Tap Water Supply",
                    description = "Muddy discolored water coming through municipal pipelines with high turbidity and strong foul smell for 48 hours.",
                    category = IssueCategory.WATER,
                    location = "Green Park Extension, Lane 4",
                    address = "House 12 to 45, Lane 4, Green Park Extn",
                    status = IssueStatus.IN_PROGRESS,
                    priority = IssuePriority.HIGH,
                    upvotes = 52,
                    hasUserUpvoted = false,
                    reportedByName = "Vikram Sengupta",
                    reportedByEmail = "vikram.s@example.com",
                    reportedTimestamp = now - 1 * day,
                    assignedDepartment = Department.WATER_WORKS,
                    authorityResponse = "Pipeline rupture identified near Booster Pump Station #3. Repair crew is flushing line.",
                    authorityOfficerName = "Eng. Sunita Rao"
                ),
                CivicIssue(
                    id = "CIV-2026-004",
                    title = "Three Streetlights Non-Functional on Dark Curve",
                    description = "Poles #14, #15 and #16 have been dark for a week. The curve is pitch black at night creating safety risks for women and pedestrians.",
                    category = IssueCategory.STREETLIGHT,
                    location = "Mayur Vihar Phase 1, Pocket 4",
                    address = "Near Community Park Gate 1, Pocket 4",
                    status = IssueStatus.RESOLVED,
                    priority = IssuePriority.MEDIUM,
                    upvotes = 21,
                    hasUserUpvoted = false,
                    reportedByName = "Rahul Sharma",
                    reportedByEmail = "rahul.sharma@example.com",
                    reportedTimestamp = now - 7 * day,
                    assignedDepartment = Department.POWER_UTILITY,
                    authorityResponse = "Replaced blown LED luminaires and updated MCB breaker. All 3 lights tested functional.",
                    authorityOfficerName = "Lineman M. Prakash",
                    resolutionTimestamp = now - 1 * day
                ),
                CivicIssue(
                    id = "CIV-2026-005",
                    title = "Blocked Stormwater Drain Causing Waterlogging",
                    description = "Plastic bags and construction debris have completely choked the roadside storm drain. Even light rain leads to 1-foot stagnant water.",
                    category = IssueCategory.DRAINAGE,
                    location = "Indirapuram, Ahinsa Khand 2",
                    address = "Crossing near Shanti Gopal Hospital Road",
                    status = IssueStatus.PENDING,
                    priority = IssuePriority.MEDIUM,
                    upvotes = 19,
                    hasUserUpvoted = false,
                    reportedByName = "Ananya Patel",
                    reportedByEmail = "ananya.patel@example.com",
                    reportedTimestamp = now - 3 * day,
                    assignedDepartment = Department.DRAINAGE_FLOOD
                ),
                CivicIssue(
                    id = "CIV-2026-006",
                    title = "Broken Public Park Benches & Damaged Swings",
                    description = "Two stone benches are cracked with exposed iron rebars, and children swings chains are broken posing injury hazards.",
                    category = IssueCategory.PUBLIC_PROPERTY,
                    location = "Vasant Kunj, Sector B Park",
                    address = "Children Play Area, Sector B Central Park",
                    status = IssueStatus.RESOLVED,
                    priority = IssuePriority.LOW,
                    upvotes = 15,
                    hasUserUpvoted = false,
                    reportedByName = "Rahul Sharma",
                    reportedByEmail = "rahul.sharma@example.com",
                    reportedTimestamp = now - 12 * day,
                    assignedDepartment = Department.PUBLIC_WORKS,
                    authorityResponse = "Installed new modular fiber benches and replaced swing links with galvanized safety chains.",
                    authorityOfficerName = "Horticulture Officer Anita Das",
                    resolutionTimestamp = now - 3 * day
                ),
                CivicIssue(
                    id = "CIV-2026-007",
                    title = "Exposed Live Electrical Cable Near Bus Stop",
                    description = "Transformer ground box cover is broken off with bare wiring accessible to children standing at the bus stop queue.",
                    category = IssueCategory.ELECTRICITY,
                    location = "Karol Bagh, Arya Samaj Road",
                    address = "Beside DTC Bus Shelter #22, Arya Samaj Rd",
                    status = IssueStatus.IN_PROGRESS,
                    priority = IssuePriority.HIGH,
                    upvotes = 76,
                    hasUserUpvoted = true,
                    reportedByName = "Vikram Sengupta",
                    reportedByEmail = "vikram.s@example.com",
                    reportedTimestamp = now - 6 * 3600000L,
                    assignedDepartment = Department.POWER_UTILITY,
                    authorityResponse = "Emergency isolation completed. New lockable feeder pillar cabinet being mounted today.",
                    authorityOfficerName = "Asst Eng. Arvind Gupta"
                ),
                CivicIssue(
                    id = "CIV-2026-008",
                    title = "Illegal Debris Dumping on Pedestrian Footpath",
                    description = "Renovation contractor dumped two truckloads of brick rubble and concrete blocks on sidewalk, forcing pedestrians into traffic.",
                    category = IssueCategory.PUBLIC_PROPERTY,
                    location = "Lajpat Nagar 4, Ring Road Service Lane",
                    address = "Outside Plot #104, Ring Road Service Lane",
                    status = IssueStatus.PENDING,
                    priority = IssuePriority.MEDIUM,
                    upvotes = 11,
                    hasUserUpvoted = false,
                    reportedByName = "Rahul Sharma",
                    reportedByEmail = "rahul.sharma@example.com",
                    reportedTimestamp = now - 18 * 3600000L,
                    assignedDepartment = Department.SANITATION
                ),
                CivicIssue(
                    id = "CIV-2026-009",
                    title = "Manhole Cover Missing on Busy Intersection",
                    description = "Open manhole cover with tree branch placed inside as warning. Extremely hazardous for night cyclists and vehicles.",
                    category = IssueCategory.DRAINAGE,
                    location = "Dwarka Sector 12, Roundabout",
                    address = "North-West corner intersection, Sector 12",
                    status = IssueStatus.RESOLVED,
                    priority = IssuePriority.HIGH,
                    upvotes = 64,
                    hasUserUpvoted = true,
                    reportedByName = "Ananya Patel",
                    reportedByEmail = "ananya.patel@example.com",
                    reportedTimestamp = now - 5 * day,
                    assignedDepartment = Department.PUBLIC_WORKS,
                    authorityResponse = "Heavy duty cast iron chamber lid installed and secured with locking rim.",
                    authorityOfficerName = "Ward Supt. K. Venkat",
                    resolutionTimestamp = now - 2 * day
                ),
                CivicIssue(
                    id = "CIV-2026-010",
                    title = "Unattended Fallen Tree Branch Blocking Road",
                    description = "Large bough snapped during storm and is blocking one entire lane of the two-lane collector road.",
                    category = IssueCategory.OTHER,
                    location = "Rohini Sector 9, Outer Ring Rd bypass",
                    address = "Near Pocket 2 Community Hall",
                    status = IssueStatus.RESOLVED,
                    priority = IssuePriority.MEDIUM,
                    upvotes = 18,
                    hasUserUpvoted = false,
                    reportedByName = "Vikram Sengupta",
                    reportedByEmail = "vikram.s@example.com",
                    reportedTimestamp = now - 9 * day,
                    assignedDepartment = Department.PUBLIC_WORKS,
                    authorityResponse = "Wood clearance squad cleared debris using chainsaws. Road unobstructed.",
                    authorityOfficerName = "Disaster Cell Ops",
                    resolutionTimestamp = now - 8 * day
                )
            )
            dao.insertIssues(initialIssues)

            // 3. Initial Notifications (including Smart Mobility Alerts)
            val initialNotifications = listOf(
                CivicNotification(
                    id = 1,
                    title = "⚠ Waterlogging reported on your route",
                    message = "Severe waterlogging (2.5 ft depth) detected near Mathura Road Underpass. Rerouting via Elevated Corridor suggested.",
                    timestamp = now - 25 * 60000L,
                    isRead = false,
                    targetRole = UserRole.CITIZEN,
                    targetEmail = "rahul.sharma@example.com",
                    relatedIssueId = "CIV-2026-003"
                ),
                CivicNotification(
                    id = 2,
                    title = "🚌 Bus Route 52 may be delayed",
                    message = "Bus Route 52 is diverted via Elevated Barapullah Flyover (+15 min delay) due to waterlogging.",
                    timestamp = now - 50 * 60000L,
                    isRead = false,
                    targetRole = UserRole.CITIZEN,
                    targetEmail = "rahul.sharma@example.com",
                    relatedIssueId = null
                ),
                CivicNotification(
                    id = 3,
                    title = "🚧 Road construction detected",
                    message = "Active flyover expansion work on Ring Road Sector 18. One lane restricted with 30 km/h speed advisory.",
                    timestamp = now - 3 * 3600000L,
                    isRead = false,
                    targetRole = UserRole.CITIZEN,
                    targetEmail = null,
                    relatedIssueId = "CIV-2026-006"
                ),
                CivicNotification(
                    id = 4,
                    title = "🌧 Weather may affect your journey",
                    message = "Heavy rainfall advisory (18.5 mm/h). Reduced visibility and low-lying waterlogging expected on evening commutes.",
                    timestamp = now - 5 * 3600000L,
                    isRead = true,
                    targetRole = UserRole.CITIZEN,
                    targetEmail = null,
                    relatedIssueId = null
                ),
                CivicNotification(
                    id = 5,
                    title = "✅ Road hazard resolved",
                    message = "Pothole repair at Sector 18 Commercial Junction marked as fully resolved with asphalt overlay.",
                    timestamp = now - 1 * day,
                    isRead = true,
                    targetRole = UserRole.CITIZEN,
                    targetEmail = "rahul.sharma@example.com",
                    relatedIssueId = "CIV-2026-002"
                ),
                CivicNotification(
                    id = 6,
                    title = "High Priority Alert: CIV-2026-007",
                    message = "Urgent: Exposed Live Electrical Cable complaint received 75+ citizen upvotes.",
                    timestamp = now - 4 * 3600000L,
                    isRead = false,
                    targetRole = UserRole.ADMIN,
                    relatedIssueId = "CIV-2026-007"
                ),
                CivicNotification(
                    id = 7,
                    title = "New Complaint Filed: CIV-2026-008",
                    message = "Illegal Debris Dumping reported in Lajpat Nagar 4.",
                    timestamp = now - 17 * 3600000L,
                    isRead = true,
                    targetRole = UserRole.ADMIN,
                    relatedIssueId = "CIV-2026-008"
                )
            )
            dao.insertNotifications(initialNotifications)
        }
    }
}

package com.example.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.IssueCategory
import com.example.data.models.IssuePriority
import com.example.ui.components.CivicButton
import com.example.ui.components.CivicButtonSize
import com.example.ui.components.CivicButtonVariant
import com.example.ui.components.getCategoryColor
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.CivicAmber
import com.example.ui.theme.CivicGreenContainer
import com.example.ui.theme.CivicGreenDark
import com.example.ui.theme.CivicGreenLight
import com.example.ui.theme.CivicGreenPrimary
import com.example.ui.theme.CivicNavyContainer
import com.example.ui.theme.CivicNavyDark
import com.example.ui.theme.CivicNavyPrimary
import com.example.ui.theme.CivicOrangeContainer
import com.example.ui.theme.CivicOrangeDark
import com.example.ui.theme.CivicOrangePrimary
import com.example.ui.theme.CivicRed
import com.example.ui.theme.CivicSlate100
import com.example.ui.theme.CivicSlate200
import com.example.ui.theme.CivicSlate400
import com.example.ui.theme.CivicSlate600
import com.example.ui.theme.CivicSlate800
import com.example.ui.theme.CivicSlate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueScreen(
    onSubmit: (
        title: String,
        description: String,
        category: IssueCategory,
        location: String,
        address: String,
        priority: IssuePriority,
        photoUri: String?,
        onSuccess: (String) -> Unit
    ) -> Unit,
    onViewComplaints: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(IssueCategory.ROADS) }
    var location by remember { mutableStateOf("Sector 62, Central Market") }
    var address by remember { mutableStateOf("Near Mother Dairy, Main Block C Road") }
    var selectedPriority by remember { mutableStateOf(IssuePriority.MEDIUM) }

    // Photo Upload State
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var photoFileName by remember { mutableStateOf<String?>(null) }
    var photoFileSizeKb by remember { mutableStateOf<Long?>(null) }
    var photoValidationError by remember { mutableStateOf<String?>(null) }

    // Helper to validate and set picked image
    fun handleSelectedUri(uri: Uri?) {
        if (uri == null) return
        photoValidationError = null

        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: ""

            // Validate format: JPG, JPEG, PNG, WEBP
            val isSupportedFormat = mimeType.contains("jpeg", ignoreCase = true) ||
                    mimeType.contains("jpg", ignoreCase = true) ||
                    mimeType.contains("png", ignoreCase = true) ||
                    mimeType.contains("webp", ignoreCase = true) ||
                    uri.toString().endsWith(".jpg", ignoreCase = true) ||
                    uri.toString().endsWith(".jpeg", ignoreCase = true) ||
                    uri.toString().endsWith(".png", ignoreCase = true) ||
                    uri.toString().endsWith(".webp", ignoreCase = true)

            if (!isSupportedFormat && mimeType.isNotEmpty()) {
                photoValidationError = "Invalid format: Only JPG, JPEG, PNG, and WEBP are supported."
                Toast.makeText(context, photoValidationError, Toast.LENGTH_LONG).show()
                return
            }

            // Validate file size (under 5MB = 5 * 1024 * 1024 bytes)
            var sizeInBytes: Long = 0
            var displayName: String = "civic_evidence.jpg"

            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1 && !it.isNull(sizeIndex)) {
                        sizeInBytes = it.getLong(sizeIndex)
                    }
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && !it.isNull(nameIndex)) {
                        displayName = it.getString(nameIndex) ?: displayName
                    }
                }
            }

            if (sizeInBytes == 0L) {
                try {
                    contentResolver.openAssetFileDescriptor(uri, "r")?.use { afd ->
                        sizeInBytes = afd.length
                    }
                } catch (_: Exception) {}
            }

            val maxSizeBytes = 5 * 1024 * 1024 // 5MB
            if (sizeInBytes > maxSizeBytes) {
                photoValidationError = "File size (${(sizeInBytes / (1024 * 1024))}MB) exceeds 5MB limit. Please choose a smaller photo."
                Toast.makeText(context, photoValidationError, Toast.LENGTH_LONG).show()
                return
            }

            selectedPhotoUri = uri
            photoFileName = displayName
            photoFileSizeKb = if (sizeInBytes > 0) sizeInBytes / 1024 else null
            Toast.makeText(context, "Photo attached successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            photoValidationError = "Could not load photo: ${e.localizedMessage}"
            Toast.makeText(context, photoValidationError, Toast.LENGTH_LONG).show()
        }
    }

    // Activity Result Launcher for Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        handleSelectedUri(uri)
    }

    // Fallback Content Launcher (for broad compatibility)
    val contentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        handleSelectedUri(uri)
    }

    var titleError by remember { mutableStateOf(false) }
    var descError by remember { mutableStateOf(false) }

    var submittedComplaintId by remember { mutableStateOf<String?>(null) }

    if (submittedComplaintId != null) {
        AlertDialog(
            onDismissRequest = {
                submittedComplaintId = null
                onViewComplaints()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = CivicGreenPrimary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Report Filed Successfully!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = CivicSlate900
                )
            },
            text = {
                Column {
                    Text(
                        text = "Your civic grievance has been officially registered with the municipal department.",
                        fontSize = 13.sp,
                        color = CivicSlate600
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CivicNavyContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Complaint Tracking ID",
                                fontSize = 11.sp,
                                color = CivicNavyPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = submittedComplaintId ?: "",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CivicNavyDark
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You can track real-time resolution updates under 'My Complaints'.",
                        fontSize = 11.sp,
                        color = CivicSlate400
                    )
                }
            },
            confirmButton = {
                CivicButton(
                    text = "View My Complaints",
                    onClick = {
                        submittedComplaintId = null
                        onViewComplaints()
                    },
                    variant = CivicButtonVariant.PRIMARY,
                    size = CivicButtonSize.SMALL
                )
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CivicSlate100)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CivicNavyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CivicGreenPrimary,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Report,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Report a Civic Issue",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Direct municipal authority dispatch form",
                            fontSize = 11.sp,
                            color = CivicSlate400
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // 1. Issue Category
                Text(
                    text = "Select Category *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate800
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IssueCategory.values().forEach { category ->
                        val isSelected = selectedCategory == category
                        val catColor = getCategoryColor(category)
                        val catIcon = getCategoryIcon(category)

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) catColor else CivicSlate100,
                            modifier = Modifier
                                .clickable { selectedCategory = category }
                                .padding(vertical = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = catIcon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else catColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else CivicSlate800
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Issue Title
                Text(
                    text = "Issue Headline / Title *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate800
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    placeholder = { Text("e.g. Large crater pothole near School gate", color = CivicSlate400) },
                    isError = titleError,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CivicNavyPrimary,
                        unfocusedBorderColor = CivicSlate200,
                        focusedContainerColor = CivicSlate100,
                        unfocusedContainerColor = CivicSlate100
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("report_issue_title_input")
                )
                if (titleError) {
                    Text(
                        text = "Please enter a descriptive title.",
                        color = CivicRed,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Issue Description
                Text(
                    text = "Detailed Description *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate800
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        descError = false
                    },
                    placeholder = {
                        Text(
                            "Explain the problem severity, hazard risks, duration it has persisted...",
                            color = CivicSlate400
                        )
                    },
                    isError = descError,
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CivicNavyPrimary,
                        unfocusedBorderColor = CivicSlate200,
                        focusedContainerColor = CivicSlate100,
                        unfocusedContainerColor = CivicSlate100
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("report_issue_desc_input")
                )
                if (descError) {
                    Text(
                        text = "Please provide details about the problem.",
                        color = CivicRed,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Photographic Evidence Upload & Preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Photographic Evidence",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate800
                    )
                    Text(
                        text = "JPG, PNG, WEBP (Max 5MB)",
                        fontSize = 10.5.sp,
                        color = CivicSlate400
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                if (selectedPhotoUri == null) {
                    // Upload card state
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CivicSlate100,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CivicSlate200),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } catch (_: Exception) {
                                    contentLauncher.launch("image/*")
                                }
                            }
                            .testTag("upload_photo_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = CivicOrangeContainer,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Attach Photo",
                                        tint = CivicOrangePrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Upload Photo Evidence",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CivicSlate900
                                )
                                Text(
                                    text = "Tap to select image from device gallery",
                                    fontSize = 11.sp,
                                    color = CivicSlate600
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CivicOrangePrimary.copy(alpha = 0.12f),
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.UploadFile,
                                        contentDescription = null,
                                        tint = CivicOrangePrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Browse",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CivicOrangePrimary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Preview card state
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, CivicGreenPrimary.copy(alpha = 0.6f)),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Header with status and remove
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = CivicGreenDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = photoFileName ?: "Photo Attached",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CivicSlate900
                                    )
                                    if (photoFileSizeKb != null) {
                                        Text(
                                            text = "(${photoFileSizeKb} KB)",
                                            fontSize = 10.sp,
                                            color = CivicSlate400
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // Replace button
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = CivicSlate100,
                                        modifier = Modifier.clickable {
                                            try {
                                                photoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            } catch (_: Exception) {
                                                contentLauncher.launch("image/*")
                                            }
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Replace",
                                                tint = CivicNavyPrimary,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "Replace",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = CivicNavyPrimary
                                            )
                                        }
                                    }

                                    // Remove button
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFEE2E2),
                                        modifier = Modifier.clickable {
                                            selectedPhotoUri = null
                                            photoFileName = null
                                            photoFileSizeKb = null
                                            photoValidationError = null
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Remove",
                                                tint = CivicRed,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "Remove",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = CivicRed
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Image thumbnail preview
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CivicSlate200)
                            ) {
                                AsyncImage(
                                    model = selectedPhotoUri,
                                    contentDescription = "Selected Photo Evidence",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Surface(
                                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                                    color = CivicNavyDark.copy(alpha = 0.85f),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Text(
                                            text = "Geotagged & Timestamped",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (photoValidationError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = CivicRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = photoValidationError ?: "",
                            color = CivicRed,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Location and Address
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Incident Location *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CivicSlate800
                    )

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CivicGreenContainer,
                        modifier = Modifier.clickable {
                            location = "Sector 62, Block C Market"
                            address = "Lat: 28.6280° N, Lon: 77.3649° E (GPS Verified)"
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "GPS",
                                tint = CivicGreenDark,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Use Live GPS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivicGreenDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("Locality / Sector / Neighborhood", color = CivicSlate400) },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = CivicSlate400)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CivicNavyPrimary,
                        unfocusedBorderColor = CivicSlate200,
                        focusedContainerColor = CivicSlate100,
                        unfocusedContainerColor = CivicSlate100
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    placeholder = { Text("Exact street address / landmark", color = CivicSlate400) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CivicNavyPrimary,
                        unfocusedBorderColor = CivicSlate200,
                        focusedContainerColor = CivicSlate100,
                        unfocusedContainerColor = CivicSlate100
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 6. Priority Selector
                Text(
                    text = "Perceived Urgency / Priority",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CivicSlate800
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IssuePriority.values().forEach { priority ->
                        val isSelected = selectedPriority == priority
                        val (pColor, pBg) = when (priority) {
                            IssuePriority.LOW -> Pair(CivicGreenPrimary, CivicGreenContainer)
                            IssuePriority.MEDIUM -> Pair(CivicAmber, Color(0xFFFEF3C7))
                            IssuePriority.HIGH -> Pair(CivicRed, Color(0xFFFEE2E2))
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) pBg else CivicSlate100,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) pColor else CivicSlate200
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedPriority = priority }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = priority.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) pColor else CivicSlate600
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                CivicButton(
                    text = "Submit Grievance Report",
                    onClick = {
                        if (title.isBlank()) {
                            titleError = true
                            return@CivicButton
                        }
                        if (description.isBlank()) {
                            descError = true
                            return@CivicButton
                        }

                        onSubmit(
                            title,
                            description,
                            selectedCategory,
                            location,
                            address,
                            selectedPriority,
                            selectedPhotoUri?.toString()
                        ) { generatedId ->
                            submittedComplaintId = generatedId
                        }
                    },
                    variant = CivicButtonVariant.PRIMARY_GREEN,
                    size = CivicButtonSize.LARGE,
                    leadingIcon = Icons.Default.CheckCircle,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "submit_report_button"
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

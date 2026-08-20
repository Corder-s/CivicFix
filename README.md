# CivicFix — AI-Powered Municipal Grievance & Civic Action Platform

**CivicFix** is an intelligent, community-focused civic issue reporting and municipal grievance redressal Android application built with modern Kotlin and Jetpack Compose. It empowers ordinary citizens to report localized public infrastructure defects (potholes, water leaks, broken streetlights, waste accumulation) and gives municipal ward officers an automated triage and resolution pipeline.

---

## 🤖 CivicFix AI — Your Smart Civic Assistant

Integrated throughout the platform is **CivicFix AI**, an AI-powered municipal co-pilot and diagnostic engine designed to bridge the gap between citizens and local authorities.

### Key Capabilities of CivicFix AI

1. **Instant Problem Triage & Diagnosis**:
   - Analyzes natural language problem descriptions in real-time.
   - Identifies the correct municipal department (PWD, Water Board, Electricity Board, Solid Waste Management).
   - Estimates severity, priority, and standard SLA resolution timelines.
2. **1-Tap Auto-Drafting & Filing**:
   - Converts casual or colloquial complaints into structured, professional grievance reports ready for immediate submission.
3. **Multilingual Natural Language Support**:
   - Seamlessly understands and responds in **English**, **Hindi**, and **Hinglish** (e.g., *"Mere ghar ke paas street light kharab hai"* or *"Sadak par bade gaddhe hain"*).
4. **Live Complaint Tracking**:
   - Retrieves real-time status and work order milestones for tickets (e.g. `CIV-2026-0081`, `CIV-2026-00124`).
5. **Status Lifecycle Explanations**:
   - Clearly explains municipal stages (`Pending`, `In Progress`, `Resolved`, `Rejected`) to eliminate citizen confusion.
6. **Community Duplicate Detection**:
   - Flags nearby matching grievances within 300m and encourages community upvoting instead of duplicate submissions.
7. **24/7 Emergency Routing**:
   - Instantly provides critical emergency helplines for urgent hazards (`112` National, `1913` Municipal, `1912` Power Faults, `1916` Water Leaks).

---

## 🌟 Core App Features

### 👤 Citizen Experience
- **Interactive Home Dashboard**:
  - Live municipal statistics (Resolved complaints, Active issues, Avg SLA time).
  - Quick Category grid (Roads, Water, Electricity, Waste, Drainage, Parks).
  - Embedded **CivicFix AI Problem Solver** widget for instant diagnosis.
- **Comprehensive Issue Reporting**:
  - Title, description, category selector, severity indicator.
  - Image attachments with preview and camera simulation.
  - Precise landmark, street, and GPS location tagging.
- **Community Feed & Upvoting**:
  - Browse neighborhood grievances with sorting (Most Upvoted, Newest, Severity).
  - Filter by Category and Resolution Status.
  - Upvote to elevate pressing community issues directly to the Ward Officer triage board.
- **My Grievances & Live Timeline**:
  - Track personal complaints with dynamic progress bars and official officer notes.
- **Notifications Hub**:
  - Automated push alerts when an officer dispatches a field crew or completes a repair.

### 🏛️ Municipal Administration & Ward Officer Portal
- **Officer Triage Board**:
  - Filter complaints by severity, department, and community urgency score.
  - Real-time status management (`Pending` ➔ `In Progress` ➔ `Resolved` ➔ `Rejected`).
  - Assign responsible field teams and append official closure notes with timestamps.
- **Administrative AI Summaries**:
  - Generate high-priority daily complaint summaries and triage recommendations.
- **Citizen Directory & Analytics**:
  - Monitor ward-level activity, resolution rates, and citizen engagement.

### 🔐 Modern Warm Authentication & Identity
- High-contrast warm aesthetic matching modern design standards.
- Seamless role switching between **Citizen** and **Municipal Admin** accounts.
- Social Google & Apple Sign-In support.
- Biometric & session management.

---

## 🏗️ Architecture & Tech Stack

```
CivicFix/
├── app/src/main/java/com/example/
│   ├── MainActivity.kt               # Central Activity, Navigation, & App Shell
│   ├── data/
│   │   ├── ai/                       # Gemini AI Service & Heuristic Fallback Engine
│   │   │   ├── AiChatModels.kt       # Chat models, starter prompts & quick actions
│   │   │   └── GeminiCivicChatService.kt # Gemini REST client & fallback triage
│   │   ├── models/                   # Issue, User, Role, Category & Status Models
│   │   └── repository/               # Centralized InMemory/Persistence Data Store
│   └── ui/
│       ├── components/               # Reusable Material 3 Design System
│       │   ├── AuthComponents.kt     # Warm-styled inputs, social buttons & hero cards
│       │   ├── HomeAiProblemSolver.kt# Embedded AI diagnostic & auto-drafting widget
│       │   ├── CivicButtons.kt       # Accessible custom buttons & badges
│       │   └── IssueCards.kt         # Community & admin grievance cards
│       ├── screens/                  # Feature Screens
│       │   ├── AuthScreens.kt        # Login & Citizen Registration
│       │   ├── PublicHomeScreen.kt   # Community Home & AI Solver
│       │   ├── CommunityIssuesScreen.kt# Search & Upvote Feed
│       │   ├── ReportIssueScreen.kt  # Grievance Creation Form
│       │   ├── IssueDetailScreen.kt  # Full ticket history & actions
│       │   ├── CitizenDashboardScreen.kt # Personal reports & activity
│       │   ├── AdminDashboardScreen.kt# Municipal officer overview & charts
│       │   ├── AdminIssuesScreen.kt  # Officer ticket triage & status updates
│       │   ├── AdminUsersScreen.kt   # Citizen & staff directory
│       │   ├── AiChatAssistantScreen.kt# Full-screen CivicFix AI conversational guide
│       │   └── NotificationsScreen.kt# Real-time municipal alerts
│       └── theme/                    # Material 3 Color Schemes & Typography
```

- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Language**: Kotlin 2.0+
- **Concurrency**: Kotlin Coroutines & Flow
- **AI Integration**: Google Gemini API via REST with high-fidelity offline heuristic fallback
- **Networking**: OkHttp3
- **Responsive Layout**: Adaptive layouts supporting mobile phones, foldables, and tablets

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or later
- JDK 17 or higher
- Android SDK 35 (compileSdk 35, minSdk 24)

### Building the Project
```bash
gradle :app:assembleDebug
```

### Running Tests
```bash
gradle :app:testDebugUnitTest
```

### Configuring the Gemini API Key
To enable cloud Gemini AI generation:
1. Open the **Secrets panel** in Google AI Studio.
2. Add `GEMINI_API_KEY` with your valid Gemini API key.
3. The app will automatically inject it into `BuildConfig.GEMINI_API_KEY`. If not provided, the app runs offline with its built-in knowledge engine.

---

## 📄 License
CivicFix is released under the **MIT License**.

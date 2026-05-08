🤖 SYSTEM CONTEXT: LOCAL-FIRST PERSONAL FINANCE APP (UI-ALIGNED)

Project Objective
Build a privacy-centric, local-first Android application for tracking personal finances. Zero financial data ever leaves the device. The app relies on automatically parsing incoming bank SMS messages and user-uploaded CSV bank statements. It uses a custom-built, deterministic Smart Rules Engine (combining a user memory bank, global keyword dictionaries, and contextual heuristics) to automatically categorize transactions.

Target Architecture & Tech Stack

Platform: Native Android.

UI Framework: Jetpack Compose (Material 3 guidelines). The UI must closely match the provided visual structure (Blue/Purple gradient themes, rounded white cards).

Language: Kotlin.

Local Database: Room (SQLite wrapper).

Architecture Pattern: MVVM (Model-View-ViewModel) with Kotlin Coroutines and Flow for reactive state management. Dependency Injection explicitly excluded to maintain simplicity and momentum.

Background Processing: Android BroadcastReceiver for listening to incoming SMS in real-time.

File Processing: Kotlin standard libraries for parsing local .csv files.

🛑 STRICT CONSTRAINTS (CRITICAL FOR AI AGENT)

NO CLOUD BACKEND: Do not write any code for Retrofit, Ktor client, or HTTP requests to external servers.

NO FIREBASE: Authentication and database must be entirely local.

LOCAL SMS INGESTION: You are explicitly authorized to use READ_SMS and RECEIVE_SMS permissions. The user is sideloading this app for personal use, so Google Play Store policy restrictions regarding SMS permissions do not apply. All SMS parsing and data extraction MUST happen completely locally on the device.

PRIVACY FIRST: All categorization logic and keyword matching must happen entirely on-device. No cloud LLM API calls under any circumstances.

📱 UI/UX & SCREEN IMPLEMENTATION MAP
1. Main Dashboard Screen

Top App Bar: Display a greeting ("Good Evening") and a dropdown for Month/Year selection (e.g., April 2026). The background should use a blue/purple gradient.

Primary Summary Card: Display "Total Monthly Spending" in large font, with a "Remaining Budget" value below it, separated by a horizontal progress bar indicating budget consumption.

Metric Row: Three equal-sized square cards placed horizontally: Income, Expense, and Savings.

Bottom Navigation: Persistent across main screens. Items: Dashboard (Home icon), Transactions (Receipt icon), Insights (Trend chart icon), Settings (Gear icon).

2. Transactions Screen & The Triage UI

Search Bar: A prominent text input field at the top to filter transactions by description.

Filter Chips: A horizontal scrolling row of selectable chips below the search bar.

Grouped List View: Use a LazyColumn to display transactions. Group them by date with sticky headers.

The Triage UI (Highlighting): Visually highlight any transaction marked as "Uncategorized" (e.g., soft red background or warning icon) so the user knows it requires manual attention.

Categorization Bottom Sheet: Clicking an "Uncategorized" transaction opens a Bottom Sheet. The user can select a category from a grid/list and check a box to "Always categorize future transactions from [Merchant] as [Category]".

3. Insights Screen (Local Analytics)

Category Spending Card: A section containing a custom Compose Canvas Donut Chart representing spending distribution. Next to/below the chart, display a list of categories with colored bullet points matching the chart slices, percentages, and total amounts.

Insight Cards: Display dynamically generated text alerts based on local data analysis (e.g., "Food spending 20% higher").

4. Settings Screen

Security Section: Toggles for "Biometric Lock" and "PIN Lock".

Data Section: Clickable row to trigger the local CSV bank statement importer.

Permissions Section: A toggle for "SMS Access" to enable/disable the background SMS receiver.

🗄️ DATA MODELS (ROOM DATABASE SCHEMA)

Entity: Transaction

id: Int (Primary Key, Auto-generate)

rawDescription: String (The parsed SMS merchant name or CSV description)

amount: Double

timestamp: Long (Unix timestamp)

type: String (Enum: CREDIT, DEBIT)

category: String (Populated by the Smart Rules Engine, defaults to "Uncategorized")

source: String (Enum: SMS, CSV, MANUAL)

isManuallyCorrected: Boolean (Default: false)

Entity: MerchantRule (The Memory Bank)

id: Int (Primary Key)

merchantPattern: String (The exact string to match, e.g., "ZEPTO")

category: String (The category the user explicitly mapped to this merchant)

🗺️ IMPLEMENTATION PHASES (FOR AI AGENT)

Phase 1: UI Skeleton & Navigation: Set up Compose project, implement bottom navigation, and build static UI with hardcoded dummy data.

Phase 2: Local Database (Room) & State: Implement Room Database, Transaction Entity, and DAOs. Set up ViewModels and Kotlin Flow.

Phase 3: SMS Ingestion & Parsing: Implement BroadcastReceiver, permissions, and Regex logic to intercept SMS and save parsed output to Room.

Phase 4: CSV Parsing System: Implement custom CSV parsing to ingest multi-line, formatted bank statements into the database.

Phase 5: Smart Rules Engine (Database & Logic): Create the MerchantRule database table. Build the 4-level classification engine (Level 1: User Memory, Level 2: Global Keyword Map, Level 3: Heuristics, Level 4: "Uncategorized" Fallback).

Phase 6: Triage UI & Retroactive Scanning: Build the Categorization Bottom Sheet on the Transactions screen. When a user creates a new rule, trigger a background task to retroactively scan the database and update past "Uncategorized" transactions matching that rule.
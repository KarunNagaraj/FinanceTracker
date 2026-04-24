🤖 SYSTEM CONTEXT: LOCAL-FIRST PERSONAL FINANCE APP (UI-ALIGNED)
Project Objective
Build a privacy-centric, local-first Android application for tracking personal finances. Zero financial data ever leaves the device. The app relies on automatically parsing incoming bank SMS messages and user-uploaded CSV bank statements. It uses an on-device Machine Learning model (TFLite) to categorize transactions based on the parsed SMS text or CSV descriptions.

Target Architecture & Tech Stack

Platform: Native Android.

UI Framework: Jetpack Compose (Material 3 guidelines). The UI must closely match the provided visual structure (Blue/Purple gradient themes, rounded white cards).

Language: Kotlin.

Local Database: Room (SQLite wrapper).

Architecture Pattern: MVVM (Model-View-ViewModel) with Kotlin Coroutines and Flow for reactive state management.

Background Processing: Android BroadcastReceiver for listening to incoming SMS in real-time, and ContentResolver for scanning historical SMS messages. WorkManager for any background parsing tasks.

Machine Learning: TensorFlow Lite (TFLite) for on-device NLP text classification.

File Processing: Kotlin standard libraries or OpenCSV for parsing local .csv files.

🛑 STRICT CONSTRAINTS (CRITICAL FOR AI AGENT)
NO CLOUD BACKEND: Do not write any code for Retrofit, Ktor client, or HTTP requests to external servers.

NO FIREBASE: Authentication and database must be entirely local.

LOCAL SMS INGESTION: You are explicitly authorized to use READ_SMS and RECEIVE_SMS permissions. The user is sideloading this app for personal use, so Google Play Store policy restrictions regarding SMS permissions do not apply. All SMS parsing and data extraction MUST happen completely locally on the device.

PRIVACY FIRST: The TFLite model must be bundled into the APK assets. No cloud LLM API calls under any circumstances.

📱 UI/UX & SCREEN IMPLEMENTATION MAP
1. Main Dashboard Screen

Top App Bar: Display a greeting ("Good Evening") and a dropdown for Month/Year selection (e.g., April 2026). The background should use a blue/purple gradient.

Primary Summary Card: Display "Total Monthly Spending" in large font, with a "Remaining Budget" value below it, separated by a horizontal progress bar indicating budget consumption.

Metric Row: Three equal-sized square cards placed horizontally:

Income (Green text/icon with an upward trend arrow).

Expense (Red text/icon with a downward trend arrow).

Savings (Blue text/icon with a wallet icon).

Category Spending Card: A section containing a Donut Chart representing spending distribution. Next to the chart, display a list of categories (Food, Transport, Shopping) with colored bullet points matching the chart slices and their respective total amounts.

Recent Transactions: A preview list showing the 3 most recent transactions. Include a "View All" text button linking to the Transactions screen.

Bottom Navigation: Persistent across main screens. Items: Dashboard (Home icon), Transactions (Receipt icon), Insights (Trend chart icon), Settings (Gear icon).

2. Transactions Screen

Search Bar: A prominent text input field at the top to filter transactions by description.

Filter Chips: A horizontal scrolling row of selectable chips below the search bar (e.g., "This Month", "Food", "Bills", "Income").

Grouped List View: Use a LazyColumn to display transactions. Group them by date with sticky headers (e.g., "Today", "Yesterday", "This Week").

Transaction Items: Each row must show a circular category icon, the merchant/description, the category name below it, the amount (Green with '+' for credits, Red with '-' for debits), and the time/date.

3. Insights Screen (Local Analytics)

Insight Cards: Display dynamically generated text alerts based on local data analysis. Examples:

Alert (Orange icon): E.g., "Food spending 20% higher".

Tip (Green icon): E.g., "Save ₹2,400 monthly".

Status (Blue icon): E.g., "On track to save ₹47,000".

Projected Month-End Spend: A card calculating estimated total spend based on current daily burn rate, compared against the budget, visualized with a progress bar.

Trend Chart: A line graph showing total spending or savings over a "6 Month Trend".

4. Settings Screen

Security Section: Toggles for "Biometric Lock" and "PIN Lock", plus a clickable row for "Auto-Lock Timer" setting.

Data Section: Clickable rows for "Export CSV", "Backup Data", and "Restore Data". (This handles the local CSV sync functionality).

Permissions Section: A toggle for "SMS Access" (to enable/disable the background SMS receiver and historical read access) and a toggle for "Storage/File Access".

🗄️ DATA MODELS (ROOM DATABASE SCHEMA)
Entity: Transaction

id: Int (Primary Key, Auto-generate)

rawDescription: String (The parsed SMS merchant name, e.g., "Starbucks", or full SMS body if parsing fails)

amount: Double

timestamp: Long (Unix timestamp to handle both Date and Time)

type: String (Enum: CREDIT, DEBIT)

category: String (Populated by TFLite ML model, e.g., "Food", "Transport")

source: String (Enum: SMS, CSV, MANUAL)

isManuallyCorrected: Boolean (Default: false)

Entity: MonthlyBudget

id: Int (Primary Key, Auto-generate)

monthYear: String (e.g., "04-2026")

totalBudgetLimit: Double

incomeTarget: Double

🗺️ IMPLEMENTATION PHASES (FOR AI AGENT)
Phase 1: UI Skeleton & Navigation
Set up the Jetpack Compose project, implement the bottom navigation, and build the static UI for all four screens using hardcoded dummy data matching the visual references. Implement the gradient themes and custom card layouts.

Phase 2: Local Database (Room) & State
Implement the Room Database, Entities, and DAOs. Set up ViewModels for each screen. Ensure the UI reacts to database changes using Kotlin Flow. Replace the dummy data in the UI with data flowing from the local database.

Phase 3: SMS Ingestion & Regex Parsing
Implement runtime permissions requests for READ_SMS and RECEIVE_SMS. Create a BroadcastReceiver to listen for incoming SMS messages. Create a utility using ContentResolver to optionally scan historical SMS messages. Write Regex logic to identify bank transactions, extract the amount, determine CREDIT/DEBIT type, and extract the merchant name. Save these parsed outputs to the Room database.

Phase 4: CSV File System Backup/Restore
Implement the Android Storage Access Framework intent to pick a .csv file via the Settings "Restore/Backup Data" buttons. Write the CSV parsing utility and save the parsed outputs into the Room database.

Phase 5: The ML Integration
Add TFLite dependencies. Load a dummy .tflite text classification model from the assets folder. Intercept the parsed SMS data and CSV data before it hits the database, run the rawDescription through the model to get a category prediction, append it to the transaction, and save it. Build the logic for the "Insights" screen based on the categorized data.
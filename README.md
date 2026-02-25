Goal Management 
GoalSync is a peer-to-peer productivity application built with Jetpack Compose and Firebase. Unlike standard habit trackers, GoalSync is designed for accountability partners. A day is only considered "Complete" when both friends have checked in, fostering true collaboration.

Key Features
Collaborative Milestones: Track progress day-by-day. Progress bars update to 50% when one friend checks in and 100% (Teal/😊) only when both are finished.
Smart Progression: Next-day milestones are generated automatically and strictly once the current day is 100% complete.
Real-Time Chat: Integrated chat tab specific to each goal, allowing partners to motivate each other without leaving the app.
Real-Time Sync: Powered by Firebase Realtime Database; changes made by one user reflect instantly on the partner's screen.

🛠 Tech Stack
UI: Jetpack Compose (Material 3)
Language: Kotlin
Architecture: MVVM (Model-View-ViewModel)
Database: Firebase Realtime Database
Asynchronous Flow: Kotlin Coroutines & StateFlow
Dependency Injection: ViewModelProvider / viewModel()

    
Logic Implementation
1. Milestone Synchronization
We implemented a strict success-callback pattern to ensure milestones behave predictably:
Display: A ValueEventListener monitors the milestones node to keep the UI updated.
Verification: When a user clicks a checkmark, the app confirms the write to Firebase and then checks if the "Next Day" condition is met.
2. Chat Infrastructure
Messages are stored under a unique path to ensure privacy and organization:
users -> {creatorId} -> goals -> {goalId} -> messages
This allows for goal-specific conversations that load instantly as soon as the user switches to the Chat tab.


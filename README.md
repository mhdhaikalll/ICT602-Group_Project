# RoadGuard

**See the hazard before you feel it.**  
A community-driven Android app that lets you snap and report broken roads, then instantly alerts nearby drivers to avoid them.

---

## What it does

RoadGuard turns every driver into a road safety contributor. You snap a photo of a pothole, crack, or missing manhole, mark its location on the map, and submit the report. The app then sends an instant push notification to all users nearby who have opted in, warning them of the danger ahead.

---

## Core Features

- 📸 **Snap & Report** – Take a photo or choose from gallery, select severity, and submit in seconds.
- 📍 **Precise Location** – GPS coordinates automatically captured, adjustable with a map picker.
- 🗺️ **Live Map** – See all reported road hazards around you, color-coded by severity.
- 🔔 **Proximity Alerts** – Get a push notification when a new report appears near your current location.
- ✏️ **Edit Reports** – Update severity, notes, or photo if something changes.
- 📶 **Offline Mode** – Queue reports when offline; sync them automatically when back online.
- 👍 **Community Validation** – Upvote/downvote reports to confirm if the hazard still exists.

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| **Mobile App** | Android (Java + XML), MVVM architecture |
| **Local DB** | SQLite via Room (offline cache & queue) |
| **Cloud DB** | Firebase Firestore |
| **Authentication** | Firebase Auth (email/password, optional Google Sign-In) |
| **Image Storage** | Firebase Cloud Storage |
| **Push Notifications** | Firebase Cloud Messaging (FCM) + Cloud Function trigger |
| **Maps & Location** | Google Maps SDK, Fused Location Provider |
| **Backend Logic** | Firebase Cloud Functions (Node.js) for proximity alert dispatch |

---

## How it works (simplified)

1. User submits a broken road report → data saved to Firestore & Cloud Storage.
2. A Cloud Function triggers on new report creation.
3. The function queries Firestore for nearby users with alerts enabled (using geohash filtering).
4. It sends a push notification via FCM to their devices.
5. Tapping the notification opens the map, centered on the hazard.

---

## Project Structure (simplified)
RoadGuard/
├── app/ # Android application module (Java)
│ ├── src/main/java/com/roadguard/app/
│ │ ├── data/ # Room, Firestore repos
│ │ ├── ui/ # Activities, Fragments, ViewModels
│ │ └── service/ # FCM, Location services
│ └── res/ # XML layouts, drawables
├── functions/ # Firebase Cloud Functions (Node.js)
│ └── index.js # sendProximityAlert trigger
└── README.md

---

## Getting Started (Android)

### Prerequisites
- Android Studio (latest stable)
- Firebase project with `google-services.json` placed in `app/`
- Google Maps API key in `AndroidManifest.xml`
- Firebase CLI for Cloud Functions deployment

### Build & Run
1. Clone the repo: `git clone https://github.com/your-username/roadguard.git`
2. Open the `app` directory in Android Studio.
3. Add your `google-services.json` and Maps API key.
4. Build and run on a device/emulator with Google Play Services.

### Deploy the Cloud Function
```bash
cd functions
npm install
firebase deploy --only functions
```

### Developed By
Haikal [GitHub](https://github.com/mhdhaikalll) | [LinkedIn](https://www.linkedin.com/in/mhdhaikaliman/)
Majdiah [GitHub](#) | [LinkedIn]()
Inas [GitHub](#) | [LinkedIn]()
Qistina [GitHub](#) | [LinkedIn]()
# RoadGuard – Broken Road Alert App (Content & Feature Checklist)

**Tagline:** *See the hazard before you feel it.*  
**Platform:** Android (Java + XML)  
**Backend/Database:** Firebase (Auth, Firestore, Storage, FCM) + local SQLite (Room)  
**Push Notification Logic:** Firebase Cloud Function (Node.js) – triggers on new Firestore report  

---

## 1. App Identity & Visual Theme

- **Name:** RoadGuard
- **Primary Color:** Safety Orange `#FF6B35`
- **Secondary Color:** Amber Yellow `#FFC107`
- **Background:** Asphalt Light `#F5F5F5`
- **Typography:** Roboto (Material Design default)

---

## 2. System Architecture (High‑Level)
┌──────────────────────── Android App (Java) ────────────────────────┐
│ MVVM (ViewModel + LiveData) │
│ Google Maps SDK + Location Services │
│ Room (SQLite) – Offline cache │
│ Firebase SDK (Auth, Firestore, Storage, FCM) │
└──────────────────────────────┬─────────────────────────────────────┘
│
▼
┌────────── Firebase Cloud ─────────────────────────────────────────┐
│ Authentication, Firestore, Cloud Storage, FCM │
│ │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ Cloud Function (Node.js) │ │
│ │ - Trigger: Firestore document create in reports collection │ │
│ │ - Query users for nearby geohash + alertsEnabled │ │
│ │ - Send FCM multicast notification │ │
│ └─────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────┘

---

## 3. Feature Checklist

### 3.1 User Authentication & Profile
- [ ] Sign up with email & password
- [ ] Sign in with email & password
- [ ] Google Sign‑In (optional)
- [ ] Forgot/reset password flow
- [ ] View/Edit profile (display name, email)
- [ ] Statistics screen (reports submitted, verified count)

### 3.2 Location & Alert Preferences
- [ ] Request location permission (foreground)
- [ ] Enable/disable proximity alerts (toggle)
- [ ] Choose alert radius (1, 3, 5 km)
- [ ] Store and update FCM token in Firestore
- [ ] Periodically update user’s coarse location for geohash
- [ ] Show user’s location on map (blue dot)

### 3.3 Report Submission (Broken Road)
- [ ] Open camera from app to capture photo
- [ ] Choose photo from gallery
- [ ] Auto‑compress image before upload
- [ ] Capture current GPS coordinates
- [ ] Map picker to adjust pin if needed
- [ ] Form: severity (low/medium/high), notes (optional)
- [ ] Upload image to Firebase Cloud Storage
- [ ] Save report metadata to Firestore
- [ ] Cache report locally in Room database
- [ ] Confirmation screen after successful submission
- [ ] Offline queue: store pending report & upload when online (WorkManager)

### 3.4 Edit Existing Report
- [ ] List of user’s own reports (My Reports)
- [ ] Tap to view full details
- [ ] Edit severity, notes, replace photo
- [ ] Adjust location if pin was wrong
- [ ] Update Firestore document and local cache
- [ ] (Optional) Re‑trigger alert if severity changed to high

### 3.5 Map View & Nearby Reports
- [ ] Full‑screen Google Map as main screen
- [ ] Markers colored by severity (green/orange/red)
- [ ] Clustering for dense areas
- [ ] Tap marker → bottom sheet with photo, severity, distance, time
- [ ] Bottom sheet: “Navigate” button (opens Google Maps)
- [ ] Bottom sheet: upvote/downvote (mark as “still there” or “fixed”)
- [ ] Real‑time Firestore listener to add new markers instantly
- [ ] First load from local Room cache, then sync with Firestore
- [ ] Filter reports by severity or time range

### 3.6 Push Notification Service (Cloud Function)
- [ ] Firebase project configured for Cloud Functions (Blaze plan required, but free tier covers generous usage)
- [ ] Deploy a `onCreate` trigger for `reports/{reportId}` in Firestore
- [ ] Function computes geohash query bounds (using `geofire-common` library)
- [ ] Queries `users` collection for matching geohash + `alertsEnabled == true`
- [ ] Deduplicates FCM tokens and sends a multicast notification
- [ ] Notification payload includes `reportId`, `latitude`, `longitude`
- [ ] Handles invalid/expired FCM tokens (cleanup logic)
- [ ] Environment configuration via Firebase CLI (`firebase deploy --only functions`)

### 3.7 Notification Handling on Android
- [ ] `FirebaseMessagingService` extended (FCM token update)
- [ ] Show system notification with app icon & tap action
- [ ] Tap notification → open app and centre map on reported location
- [ ] Handle notification when app is in foreground (custom banner)

### 3.8 Offline Support
- [ ] Cache last 200 closest reports in Room
- [ ] Submit reports offline (queued in Room)
- [ ] Use WorkManager to sync pending reports when connectivity returns
- [ ] Show cached map markers when offline

### 3.9 UI/UX & Additional
- [ ] Splash screen with app logo
- [ ] Bottom navigation (Map, Report, Profile)
- [ ] Onboarding screens for first launch
- [ ] Empty states and loading indicators
- [ ] Error handling with user‑friendly messages
- [ ] Material Design components (cards, bottom sheets, FAB)
- [ ] Dark theme support (optional)

---

## 4. Data Models (Firestore)

**`users/{uid}`**
- `email`: string
- `displayName`: string
- `fcmToken`: string
- `alertsEnabled`: boolean
- `notificationRadiusKm`: number
- `geohash`: string
- `lastLatitude`: number
- `lastLongitude`: number

**`reports/{reportId}`**
- `userId`: string
- `imageUrl`: string
- `latitude`: number
- `longitude`: number
- `geohash`: string
- `severity`: string ("low", "medium", "high")
- `notes`: string (optional)
- `timestamp`: timestamp
- `status`: string ("reported", "verified", "fixed")
- `upvotes`: number
- `downvotes`: number

---

## 5. Room Entities (Local SQLite)

- `CachedReport`: same fields as Firestore + `lastSyncTimestamp`
- `PendingReport`: local report awaiting upload (fields like Firestore + `imageLocalPath`)
- `UserProfile`: local copy of user data

---

## 6. Cloud Function Deployment

1. Initialize Firebase in your project folder: `firebase init functions`
2. Choose JavaScript or TypeScript (Node.js environment).
3. Add `geofire-common` dependency: `npm install geofire-common`
4. Implement the `onCreate` trigger (see snippet below).
5. Deploy: `firebase deploy --only functions`
6. Monitor logs: `firebase functions:log`

**Cloud Function skeleton:**
```javascript
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const geofire = require('geofire-common');
admin.initializeApp();

exports.sendProximityAlert = functions.firestore
  .document('reports/{reportId}')
  .onCreate(async (snap, context) => {
    const report = snap.data();
    const radiusMeters = 5000; // or read from app config
    const bounds = geofire.geohashQueryBounds(
      [report.latitude, report.longitude],
      radiusMeters
    );

    const db = admin.firestore();
    const tokens = new Set();

    for (const [start, end] of bounds) {
      const usersSnap = await db.collection('users')
        .where('geohash', '>=', start)
        .where('geohash', '<=', end)
        .where('alertsEnabled', '==', true)
        .get();
      usersSnap.forEach(doc => {
        const token = doc.data().fcmToken;
        if (token) tokens.add(token);
      });
    }

    if (tokens.size === 0) return null;

    const message = {
      notification: {
        title: '⚠️ Road damage reported near you',
        body: `Severity: ${report.severity}. Tap to see details.`
      },
      data: {
        reportId: context.params.reportId,
        latitude: report.latitude.toString(),
        longitude: report.longitude.toString()
      },
      tokens: Array.from(tokens)
    };

    const response = await admin.messaging().sendMulticast(message);
    console.log(`${response.successCount} alerts sent.`);
    // Optional: clean up invalid tokens
    return null;
  });
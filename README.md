# Hakum Auto Care — offline Android customer app

Independent Kotlin / Jetpack Compose customer prototype. Open **this repository folder** in Android Studio. No WebView, Supabase, production credentials, deployment, or runtime internet access. Android manifest deliberately has no INTERNET permission.

## Run in Android Studio

1. Install a current Android Studio supporting Android Gradle Plugin 9.4.1. Use its bundled JDK (JDK 17+; validation here uses bundled JetBrains JDK 25).
2. Clone this repository and open its root folder. Let Gradle sync. Install Android SDK Platform 37 and requested build tools if prompted. Initial Gradle / SDK setup needs internet.
3. Select an API 26+ emulator or Android device and run the `app` configuration.
4. Choose **Explore demo account**, or sign in with `alex@example.test` / `hakumdemo`.
5. Enable airplane mode. The app continues to work. Once dependencies are cached, Gradle can also build with `--offline`.

The demo customer is **Alex Rivera**, with fictional vehicle **DEMO 101**. No actual customer records are used. Sign-up replaces the single local customer sandbox with a new empty customer. The demo-login button restores Alex if another local account is active. Sign-out retains stored records.

## Command line

Set `JAVA_HOME` to your Android Studio JDK and install SDK 37. Android Studio creates `local.properties`; alternatively set `ANDROID_HOME` to your SDK directory.

```sh
./gradlew :app:testDebugUnitTest :app:assembleDebug
./gradlew :app:connectedDebugAndroidTest
./gradlew :app:lintDebug
```

On Windows use `gradlew.bat`. Debug APK: `app/build/outputs/apk/debug/app-debug.apk`. Install with `adb install -r app/build/outputs/apk/debug/app-debug.apk`.

## Try the complete flow

- **Book**: pick branch, saved vehicle, service, date (two days ahead or later), and time; review and confirm. The request persists and appears in Home, Queue, and Your bookings. Prices are explicitly fictional local quotes, not production prices.
- **Me → Profile & settings → Demo Controls**: advance a booking through Booked → Queued → In progress → Final checking → For payment → Completed, or complete and mark paid directly.
- **Rewards**: stamps and points update immediately. Demo Controls can also create a completed paid service, populate five sample visits once, change card capacity (5–50), toggle loyalty/membership settings, inspect ledgers, simulate the birthday, clear history, and reset.
- **Vouchers**: choose a pickup branch, redeem merchandise with points, inspect ready-for-pickup records. Inventory and point debits persist. Birthday wash can be claimed on the Manila birthday, once each year, and used once within seven days. February 29 birthdays match February 28 in non-leap years. The ordinary profile birthday is locked after its first save; only the explicitly labeled demo tool overrides it.
- **Me**: add vehicles, view history, edit name/phone, choose dark/light theme, change local notification preference, or sign out. Notification preference is persisted; this prototype sends no messages.

## Loyalty policy and source fidelity

Reference: `thekirx/AutoDetailingandCarwash`, **main** commit `13664f95215b32ddc9e3daf952ffeaff49fd32da`. This standalone repository contains only the Android project, extracted from `android-customer/` on the original `feature/android-customer-app` branch. The original React/Vite repository remains unchanged.

There are two policies in the source. The JS `loyaltyLogic.js` helpers implement weighted stamps (including category gates, feature flags, optional membership multiplier and card wrap) and spend-based points. Later SQL migrations `20260928160000_detailing_packages_points_reviews.sql` and `20260929090000_visit_stamp.sql` change the POS path to **one stamp per paid visit** and **configured service `points_award` × eligible membership multiplier, floored**. The Android booking lifecycle defaults to that newer SQL behavior. The pure Kotlin engine preserves the JS helper formulas too; Demo Controls explicitly labels the optional helper policies. It never credits the same booking twice, including after reopening the app.

- Default card: **15 slots**, stamp rewards **10 = Free wash**, **15 = Premium detail**, from the seed migration. Both capacity and award flags are stored locally. Reward thresholds are database records, separate from card capacity; a capacity below a threshold means that milestone cannot be shown as earned on that card.
- Visit tiers: Basic; Deluxe at 30; Premium at 70; Platinum at 100, across all cars. Visit tier does not automatically grant a paid membership multiplier. A separate demo membership can enable ×1.5, with a stored end date.
- Visit rewards: 6 / 12 / 18 / 24 / 30 visits, matching `rewardsCatalog.js`.
- All 23 service entries and all 10 merchandise items from the source points table are included. Examples: wash 1, Express 2, Basic 3, Deluxe 4, Classic ceramic 15, Nano Ceramic Tint 10, High Impact Partial PPF 30. Product costs follow the source catalog; local inventory is fictional. The seed history uses the first seven service entries; its starting points equal their catalog awards.
- Stamp redemption debits the required stamp balance. Merchandise redemption debits points and stock atomically. Each seeded reward has one local claim/inventory entitlement; a claimed item cannot be claimed again until reset. This deliberately bounded inventory is for testing repeat-claim prevention; it is not a production inventory system.
- `wrap` follows source modulo behavior; wrap can remove an unclaimed milestone's current stamp balance. Default is off.

## Architecture

`navigation/` uses stable Navigation Compose and native back handling. `ui/` contains auth, home, queue, booking, rewards, vouchers, profile, shared components and themes. `CustomerViewModel` exposes StateFlow. `data/repository/` owns all writes; `data/local/` contains Room and DataStore; `data/model/` contains local entities and snapshots; `domain/loyalty/` is a pure calculation engine; `demo/` owns fictional catalog and tester controls.

Room reads a consistent snapshot under a transaction and invalidates all shared screens on updates. Payment completion stores booking/payment, visit, unique stamp credit, point ledger and balances in one transaction. Claims check persisted history, inventory and balances inside their transaction. Data survives process death; schema v1 is exported in `app/schemas/`. Reset is local and atomic.

Source brand PNGs and Benzin / Gilmer fonts are reused from this repository. WOFF2 fonts were converted to local TTF files for Android. Native navy/paper palettes follow `customer-native.css`; rounded cards, five-column stamp grid, typography and floating dock follow customer components. The app requires no downloaded fonts or images.

## Scope and limitations

This is an offline demonstration, not production authentication or booking confirmation. One local customer sandbox is supported; local password checking is demonstration-only. Branches use source preview locations Bacoor / Batangas. Queue counts describe this customer's simulated bookings; no other customers or wait estimates are fabricated. Birthday voucher consumption and pickup confirmation stand in for staff operations. No SMS, push delivery, payment collection, weather, GPS, referrals, blog/events, maintenance scheduling, or production backend is implemented. The requested customer screens and demo workflows are native and local; the full web operations suite is outside this project.

See `docs/VALIDATION.md` for the actual executed build, tests and emulator checks, and `docs/IMPLEMENTATION_PLAN.md` for source decisions.

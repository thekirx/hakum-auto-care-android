# Source references and local substitutions

Source branch: `main`, commit `13664f95215b32ddc9e3daf952ffeaff49fd32da`.

| Source | Android implementation |
| --- | --- |
| `src/App.jsx`, `CustomerAppFrame.jsx`, `CustomerAccountDock.jsx`, `customerAccountNav.js` | Navigation Compose, five destinations Home / Queue / Book / Rewards / Me, secondary screens with native Back |
| `CustomerSignInPage.jsx`, `CustomerSignUpPage.jsx` | Local sign-in, empty single-customer sign-up sandbox, immediate fictional demo login |
| `CustomerAccountPage.jsx`, `ActiveVisitCard.jsx`, `VisitProgress.jsx`, `CustomerUi.jsx` | Greeting, account overview, active vehicle ticket, queue preview, booking shortcuts, loyalty preview and recent visits |
| `CustomerQueuePage.jsx`, `CustomerVisitPage.jsx` | Branch queue, ticket number, status, native progress rail, separate upcoming bookings |
| `CustomerBookPage.jsx` | Local branch / vehicle / service / date / time / review / confirmation flow; two-day booking lead time |
| `CustomerLoyaltyPage.jsx`, `StampTrack.jsx` | Configurable stamp grid, next milestone, points, visit tier and reward claims |
| `loyaltyLogic.js` | Pure `LoyaltyEngine`: weighted/category stamp helpers, multiplier gates, progress, spend points and modulo wrap |
| `loyaltyPoints.js` | Pure source fallback points-award classifier |
| `rewardsCatalog.js` | All 23 service-point rows, all 10 merchandise costs, 6/12/18/24/30 visit rewards, 30/70/100 tier thresholds |
| `20260721190000_loyalty_membership_smart.sql` | Seed card capacity 15, stamp rewards at 10 and 15 |
| `20260928160000_detailing_packages_points_reviews.sql` | Actual lifecycle uses configured service points rather than spend points |
| `20260929090000_visit_stamp.sql` | Actual lifecycle awards one stamp per paid visit, with a unique persisted visit credit |
| `CustomerVouchersPage.jsx`, `birthdayPerk.js`, `BirthdayGreeting.jsx` | Local birthday pass, Manila birthday matching, annual claim gate, 7-day expiry, one-use redemption and source code alphabet |
| `CustomerMorePage.jsx` | Profile, saved vehicles, visit history, local preferences, theme and sign out |
| `customerPortalClient.js`, `server/customerPortal.mjs` | Replaced with shared Room repository snapshot; no API calls or customer records copied |
| `styles-customer-app.css`, `customer-refinement.css`, `customer-native.css`, customer redesign/page docs | Navy/paper palette, Benzin / Gilmer, branded stamps, cards, progress and floating dock |
| `CustomerReferralPage.jsx`, `ReferralCard.jsx` | Inspected; live referral distribution/qualification omitted from this scoped offline prototype |

Fictional substitutions: Alex Rivera, DEMO 101, service prices, local inventory, transactions and queue activity. Branch names come from `approvalPreview.js`; they do not claim live branch availability. Local sign-up does not implement phone verification or production account recovery. No source environment file or credentials are read by the Android app.

Independent code review found and resolved: incomplete catalog rows, scheduled bookings inflating queue-floor counts, and ineffective helper controls. Tests exercise the queue regression and weighted/category/multiplier configuration. PNGs are cropped to their transparent bounds and resized for Android; fonts are converted from repository WOFF2 to TTF. Original web assets remain untouched.

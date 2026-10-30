# Hakum Android customer implementation plan

Goal: Independent, native offline customer prototype; only android-customer/ changes on feature/android-customer-app.
Spec: User's attached PROJECT brief, 2026-10-30. Source: main, 13664f95215b32ddc9e3daf952ffeaff49fd32da.
Architecture: Compose + Navigation Compose → ViewModel StateFlow → repository → Room. DataStore persists theme, session and notification preferences. No network permission or backend clients.

1. Establish Gradle wrapper and stable dependencies; write unit tests for configurable loyalty calculations, tier gates, expiry, redemption, lifecycle and duplicate credit prevention before implementing engine.
2. Add Room entities, transactional repository, fictional seed, preferences. Atomic completion records unique visit credit, updates booking/payment and both ledgers; atomic claims debit balances/stock and store unique redemption records.
3. Add Hakum fonts/assets, navy/paper themes, floating Home/Queue/Book/Rewards/Me dock, local sign-in and sign-up, home, queue, booking, rewards, vouchers, profile/garage/history/settings and separate Demo Controls. All customer screens observe the repository.
4. Run JVM tests, Android build/lint, Room instrumentation tests, emulator launch and airplane-mode interaction/persistence checks. Produce debug APK and document actual evidence and limitations.

Review focus: insufficient points; repeated/concurrent credit or claim; non-default slot counts; expired/ineligible membership and birthday; empty garage/history. Tests cover engine rules and repository transactions/restart/reset. Screen checks cover navigation, scrolling, light/dark and local actions.

Source rulings: September 28 migration switches points to service points_award; September 29 switches stamps to one per paid visit. Keep JS weighted/spend helpers separately testable. Default actual lifecycle uses newer SQL rules. No live configuration is fetched. Demo prices/stock/customer are fictional; service awards and merchandise costs follow rewardsCatalog.js; slots/milestones use seeded SQL defaults 15 / 10 / 15. Visit tiers do not automatically grant a paid membership multiplier.

Execution record:
- Separate cloned checkout on the requested feature branch; original React/Vite tracked files unchanged.
- Tasks 1–3 implemented. Domain tests first failed against explicit unimplemented engine methods, then passed; queue regression first reproduced and then passed. Source spend-points oracle corrected a mistaken test expectation (25099 minor × 1.5 → 376).
- Transaction tests cover completion, concurrent and repeated redemption, birthday expiry, file database reopen, reset, and meaningful helper weights/categories/multipliers.
- Native UI flow initially hit a transient booking snackbar overlay; synchronization now waits for it to disappear before continuing. Final screen/state behavior is unchanged by that test repair.
- Rulings: latest source SQL controls default earning; legacy JS helpers remain separately available for tests and labeled demo modes. Keep integration local on feature/android-customer-app; no merge or push to main.

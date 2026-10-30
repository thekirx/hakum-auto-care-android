# Executed validation — 30 October 2026 (Asia/Manila)

Final command executed from the Android project using Android Studio's bundled JDK 25:

```sh
./gradlew --offline :app:testDebugUnitTest :app:assembleDebug :app:connectedDebugAndroidTest :app:lintDebug
```

Result: **BUILD SUCCESSFUL in 1m 14s**, exit code 0. 85 tasks: 20 executed, 65 up to date. Gradle 9.6.0, AGP 9.4.1, Kotlin/Compose compiler 2.4.21, Compose BOM 2026.09.00, Room 2.8.5, DataStore 1.3.0, Lifecycle 2.11.0, Coroutines 1.11.0, Navigation Compose 2.10.2. SDK 37, minimum API 26. Emulator: `AnimeReview_API35`, Android 15 / API 35.

| Check | Actual result |
| --- | --- |
| JVM unit suite | **11 tests, 0 failures, 0 errors** |
| Emulator instrumentation | **5 tests, 0 failures, 0 ignored** |
| Debug APK | Generated successfully, package `com.hakum.customer.demo` |
| Lint | Passed; four non-blocking advisories: newer Gradle available, optional legacy backup rules, redundant v26 qualifier, optional monochrome launcher icon |
| Airplane mode | `settings get global airplane_mode_on` = **1** |
| Wi-Fi | `settings get global wifi_on` = **0** |
| App permissions | AAPT dump shows only the app's internal receiver permission; **no INTERNET permission** |
| Five-tab UI | Native test navigated Home, Queue, Book, Rewards, Me |
| Booking | UI selected date/time and confirmed; Room test followed pending through paid completion |
| Immediate loyalty update | UI observed **7 → 8 stamps** after a simulated paid service |
| Duplicate completion | Retrying completion leaves persisted balances unchanged |
| Reward claims | Insufficient balance, unavailable stock and duplicate claims rejected; concurrent attempts produce one success |
| Birthday | Annual repeat rejected; expired voucher rejected; leap-day and source code alphabet tested |
| Persistence | File database closed/reopened with booking retained; manual force-stop/relaunch retained sign-in, theme, notification preference and demo account balances |
| Reset | Repository reset restored seed balances and removed redemption records |
| Helper configuration | Category-gated weight 2 with eligible ×1.5 membership awarded 3 stamps / 3 Express points |
| Scheduled queue regression | Future pending booking does not inflate current floor counts |
| Visual review | Rewards screen inspected on the emulator in both light and dark themes; screenshots below |

The 11 JVM tests cover source stamp gates/quantity/multipliers, spend/catalog points, configurable progress/wrap, tiers, redemption gates, lifecycle/idempotency, scheduled queue counts, service fallback points, birthday matching and code alphabet. The five emulator tests comprise the complete UI flow plus four Room repository tests (including concurrent claims, file reopen and reset).

[Dark Rewards screenshot](screenshots/rewards-dark.png) · [Light Rewards screenshot](screenshots/rewards-light.png)

Empty garage/history/queue states are explicitly implemented; the empty-history branch was reviewed in code rather than separately driven in the final UI test. Only API 35 portrait was exercised; smaller screens, rotation, large accessibility fonts and other Android versions have not been exhaustively tested. This is a practical native recreation, not a pixel-by-pixel certified match. No real backend, payment, notification delivery or production authentication was tested or required.

Earlier failures were addressed: missing test BOM declaration, generated Kotlin newline escaping, scheduled bookings counted on the floor, UI test synchronization with a transient snackbar, and a source-alphabet birthday-code regression. An earlier lint run crashed while source files were being edited; the unchanged final sources passed lint on rerun. No checks were disabled to obtain the final successful result.

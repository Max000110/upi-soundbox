# UPI Soundbox — `rules.md`

**Document Type:** Engineering Rules & AI Agent Contract  
**Project:** UPI Soundbox (Android, local-first)  
**Version:** 1.0  
**Status:** Mandatory  
**Audience:** Human developers, AI coding agents, code-generation systems, reviewers, QA operators, release engineers  
**Authority:** This document is a binding project-level engineering policy. Rules in this document override convenience, speed, generated defaults, and speculative implementation choices.

---

# 1. PURPOSE

This document defines the non-negotiable rules for designing, coding, testing, debugging, reviewing, documenting, and releasing the UPI Soundbox application.

The project is intentionally designed as a **privacy-first, offline-first, event-driven Android application** that reacts to incoming UPI-related notifications and produces local voice announcements.

The application must be built as a reliable software system, not as a quick prototype or a fragile “vibe-coded” application.

The primary objectives of these rules are:

1. Prevent hallucinated APIs, permissions, package names, notification formats, capabilities, and behaviors.
2. Prevent accidental architectural drift.
3. Prevent unnecessary dependencies and hidden network behavior.
4. Make every important claim testable.
5. Make every implementation reproducible.
6. Force evidence-based decisions.
7. Ensure ADB is the primary device-debugging and verification mechanism.
8. Ensure every development phase is tested and explicitly approved before the next phase begins.
9. Protect notification and transaction data from unnecessary exposure.
10. Keep the codebase maintainable by both humans and future AI agents.

---

# 2. RULE AUTHORITY AND PRECEDENCE

When two instructions appear to conflict, apply this precedence order:

```text
1. Android platform / documented platform security constraints
2. Repository architecture.md
3. This rules.md
4. phases.md
5. design.md
6. Feature-specific specifications
7. Explicit human approval in the current development task
8. AI-generated assumptions
```

AI-generated assumptions are always the lowest-confidence source.

A generated answer, code completion, or model memory is **not evidence**.

If a conflict cannot be resolved safely, stop implementation and request clarification rather than guessing.

---

# 3. ABSOLUTE NO-HALLUCINATION POLICY

## 3.1 Core Rule

The AI must never invent technical facts merely to keep implementation moving.

The AI must not fabricate:

- Android APIs
- Android permissions
- Manifest attributes
- service behavior
- lifecycle guarantees
- package names
- UPI application package IDs
- notification payload formats
- notification titles
- notification wording
- undocumented provider behavior
- Play Store rules
- OS restrictions
- vendor-specific behavior
- library APIs
- library versions
- Gradle plugins
- dependency coordinates
- method names
- classes
- configuration flags
- shell commands
- ADB behavior
- device capabilities
- TTS engine capabilities
- database behavior
- security guarantees
- performance numbers

If the AI does not know whether something is supported, it must say so and verify it.

---

## 3.2 Evidence Requirement

Before relying on a technical claim, the AI should prefer the following evidence sources in this order:

```text
1. Official Android documentation
2. Official library documentation / source
3. Official repository source code
4. Device-level observation through ADB
5. Reproducible local test
6. Reliable technical discussion / issue tracker
7. Search results / community posts
8. AI memory
```

The AI must not use level 8 as proof for a critical implementation decision.

---

## 3.3 Unknown-State Rule

When information is unknown, use one of these explicit states:

```text
UNKNOWN
UNVERIFIED
ASSUMPTION
NEEDS DEVICE TEST
NEEDS DOCUMENTATION CHECK
BLOCKED
```

Never silently convert an unknown fact into a confident implementation.

---

## 3.4 Assumption Ledger

Every non-trivial assumption must be recorded during development.

Recommended format:

```text
ASSUMPTION:
The selected UPI application exposes transaction details in the notification text.

CONFIDENCE:
Medium

EVIDENCE:
Observed on test device via adb shell dumpsys notification.

VALIDATION:
Required on target device and target application version.
```

A validated assumption may be promoted to a project decision.

An invalid assumption must be removed or marked obsolete.

---

# 4. NEVER INVENT PAYMENT CONFIRMATION

The application must never claim that money was received based only on an unvalidated guess.

The system must distinguish between:

```text
Notification observed
Parsed transaction
Validated credit transaction
Announceable transaction
```

These are different states.

A notification containing words such as “payment”, “success”, or an amount is not automatically proof of a received payment.

The parser must use provider-specific rules and transaction-type validation.

The application must never pretend to have direct access to bank ledger truth when it does not.

---

# 5. PROVIDER-SPECIFIC DATA RULE

UPI providers are not assumed to use one universal notification schema.

Therefore:

- Never create one giant regex and call it universal.
- Never assume all providers use the same currency syntax.
- Never assume all providers place amount in `EXTRA_TEXT`.
- Never assume the sender name is always available.
- Never assume the app package name remains permanently stable.
- Never assume notification language remains constant.
- Never assume notification wording remains constant across provider versions.

Provider parsers must be version-tolerant and testable.

---

# 6. SOURCE-OF-TRUTH RULE

The architecture document defines system boundaries.

The code defines actual implementation.

The test suite defines verified behavior.

The current device defines observed runtime behavior.

Documentation must never claim a capability that code and tests do not support.

If documentation and code disagree, fix the discrepancy rather than rationalizing it.

---

# 7. ARCHITECTURE RULES

## 7.1 Clean Architecture

Use clear separation between:

```text
Presentation
Domain
Data
Platform / Framework
```

Business logic must not be placed directly in Compose UI code.

Android framework APIs must not leak unnecessarily into pure domain logic.

---

## 7.2 Dependency Direction

Preferred direction:

```text
UI → ViewModel → Use Case → Domain
                         ↓
                    Repository API
                         ↓
                Data / Platform implementation
```

Avoid reversing dependency direction.

---

## 7.3 Module Responsibilities

Each module must have a single clear responsibility.

Examples:

```text
notification      = receives platform notification events
parser            = converts raw notification into structured candidate data
validation        = determines whether a candidate is valid
transaction       = domain representation and business rules
deduplication     = prevents repeat processing
announcement      = creates spoken message
tts               = performs speech output
storage            = persistence
settings           = preferences
ui                 = presentation
```

A module must not silently become a dumping ground for unrelated logic.

---

# 8. AI CODING AGENT OPERATING MODE

Every AI agent working on the repository must operate in the following sequence.

```text
READ
→ UNDERSTAND
→ INSPECT
→ PLAN
→ IMPLEMENT
→ BUILD
→ DEPLOY TO TEST DEVICE
→ TEST
→ INSPECT LOGS
→ FIX
→ RETEST
→ DOCUMENT
```

Do not jump directly from a user request to large-scale code generation without inspecting the repository.

---

# 9. MANDATORY REPOSITORY INSPECTION

Before modifying an existing feature, the AI must inspect:

- architecture.md
- rules.md
- phases.md
- design.md
- memory.md, if present
- relevant source files
- Gradle configuration
- AndroidManifest.xml
- resources
- tests
- current build state
- current git state

Do not overwrite functioning architecture merely because a new code pattern is more familiar.

---

# 10. CHANGE-MINIMIZATION RULE

Prefer the smallest safe change that solves the actual problem.

Do not:

- rewrite unrelated modules
- rename entire packages without need
- migrate frameworks for cosmetic reasons
- replace stable libraries unnecessarily
- regenerate the whole project to fix one bug
- refactor unrelated code during a feature change

Large refactors require explicit justification.

---

# 11. DEPENDENCY RULES

## 11.1 Dependency Philosophy

Every dependency is a maintenance and security surface.

Before adding a dependency, answer:

```text
Why is it needed?
Why can't platform APIs solve this?
Why can't existing project dependencies solve it?
What is its maintenance status?
What permissions / network behavior does it introduce?
What is its license?
What is its transitive dependency impact?
```

If these questions cannot be answered, do not add the dependency.

---

## 11.2 Preferred Technologies

Use the project-approved stack unless a documented exception is approved.

Preferred:

```text
Kotlin
Jetpack Compose
AndroidX
Coroutines
Flow
Room
DataStore
Hilt (where justified)
```

Do not introduce alternate frameworks merely for personal preference.

---

## 11.3 Version Rule

Never invent dependency versions.

Version numbers must come from:

- current project configuration
- official documentation
- verified release information
- an explicitly approved dependency matrix

Do not upgrade a dependency simply because a newer version exists.

---

# 12. NETWORK RULES

The application is local-first.

The default rule is:

```text
NO NETWORK REQUIRED FOR CORE FUNCTIONALITY
```

Do not add:

- analytics endpoints
- remote configuration
- telemetry servers
- remote transaction storage
- ad SDKs
- tracking SDKs
- unnecessary API clients

If a network permission is ever introduced, it requires explicit architectural review and approval.

---

# 13. PRIVACY RULES

Notification content may contain:

- payer name
- UPI identifiers
- transaction identifiers
- amounts
- bank references
- merchant information
- other sensitive financial context

Treat all notification-derived information as sensitive.

Do not expose sensitive information through:

- debug logs
- analytics
- crash reports
- clipboard operations
- external intents
- unnecessary notifications
- screenshots generated by the application
- remote APIs

---

# 14. LOGGING RULES

## 14.1 No Financial Payload Logging

Do not log raw notification bodies in production builds.

Bad:

```text
Payment received from Rahul ₹500
```

Preferred:

```text
Payment notification received: provider=PHONEPE, type=CREDIT, amountPresent=true
```

Even debug builds must minimize sensitive data exposure.

---

## 14.2 Structured Logging

Logs should include:

```text
timestamp
module
event
debug identifier
severity
```

Never include secrets or unnecessary personally identifiable information.

---

# 15. ANDROID PERMISSION RULES

Never request a permission because it “might be useful”.

Every permission must have:

1. a concrete feature requirement,
2. a documented rationale,
3. a platform-compatible implementation,
4. a testing procedure,
5. an explicit privacy impact assessment.

The manifest must contain only required permissions.

Do not claim a permission grants capabilities it does not grant.

---

# 16. NOTIFICATION LISTENER RULES

The notification listener is a platform boundary.

Keep it thin.

It should:

1. receive event,
2. extract safe metadata,
3. pass data into the processing pipeline,
4. return quickly.

Do not perform heavy database work, expensive parsing, or long-running speech synthesis directly inside the listener callback.

---

# 17. NOTIFICATION FILTERING RULES

Process only supported and enabled sources.

Filtering should consider:

- package identity
- enabled provider configuration
- notification category where useful
- content suitability

Do not process every notification indiscriminately.

Do not announce arbitrary notifications as payments.

---

# 18. PARSER RULES

## 18.1 Parser Must Be Deterministic

Same input + same configuration must produce the same result.

---

## 18.2 Parser Output

Prefer a structured result such as:

```text
provider
transactionType
amount
currency
sender
reference
occurredAt
confidence
rawSourceMetadata
```

Only include fields that can actually be derived.

Unknown fields must remain unknown.

---

## 18.3 Regex Rules

Regex is allowed where it is appropriate, but:

- keep patterns provider-specific where possible,
- document each pattern,
- unit test positive and negative cases,
- avoid overly broad patterns,
- avoid accidentally parsing unrelated numbers as payment amounts.

Every amount-extraction pattern must have negative tests.

---

# 19. VALIDATION RULES

Validation must confirm at minimum:

```text
Known / supported provider
Recognized credit transaction
Valid positive amount
Plausible notification structure
```

Validation must reject:

- debit notifications
- promotional messages
- OTP notifications
- account alerts
- balance-only alerts unless explicitly configured
- generic payment-related messages with no amount
- duplicate events according to deduplication rules

---

# 20. MONEY / AMOUNT RULES

Never use binary floating-point as the canonical monetary representation.

Prefer:

```text
Long minor units / smallest currency unit
```

or a precise decimal representation where required by the domain.

Example:

```text
₹10.50 → 1050 minor units
```

Do not rely on floating-point equality for financial values.

Currency formatting must be centralized.

---

# 21. DEDUPLICATION RULES

Duplicate notifications are expected in real systems.

Possible duplicate signals include:

- repeated same text
- same provider
- same amount
- same sender
- same timestamp window
- same transaction reference

The deduplication mechanism must not suppress legitimate separate payments merely because they have the same amount.

Example:

```text
₹100 from Rahul at 10:01:03
₹100 from Rahul at 10:01:45
```

These may be two real payments and must not be blindly collapsed.

---

# 22. ANNOUNCEMENT RULES

Announcements must be:

- short
- clear
- deterministic
- configurable
- localized
- interruptible only according to explicit queue policy

Examples:

```text
Received fifty rupees.
Received five hundred rupees from Rahul.
```

The exact wording must be generated through an announcement/template layer rather than being duplicated across the codebase.

---

# 23. TTS RULES

The TTS subsystem must be treated as an asynchronous service.

It must support:

- initialization failure
- language unavailable
- engine failure
- queue behavior
- interruption policy
- repeated events
- service restart

Never assume a TTS engine is always installed or always supports every language.

Never fake support for a voice or language that the device cannot provide.

---

# 24. AUDIO RULES

Audio behavior must respect Android audio APIs and device state.

Never blindly manipulate system volume.

If the application temporarily changes volume, it must:

1. capture original state,
2. change only the intended stream,
3. restore safely,
4. handle interruption / crash edge cases as far as platform behavior allows.

Do not claim perfect restoration if the platform or another application can legitimately modify the same stream concurrently.

---

# 25. BACKGROUND EXECUTION RULES

Do not use continuous polling to detect payments.

Use event-driven mechanisms.

Avoid:

- infinite loops
- aggressive timers
- unnecessary wake locks
- repeated background work
- battery-draining polling

Background behavior must follow current Android restrictions rather than assumptions from older Android versions.

---

# 26. SERVICE LIFECYCLE RULES

Every Android Service must have a documented purpose.

Document:

- startup trigger
- lifecycle
- foreground requirements if applicable
- shutdown behavior
- restart behavior
- failure behavior
- resource cleanup

Do not call a service “unkillable”, “persistent forever”, or “guaranteed to survive” unless such a guarantee is actually provided by the platform.

---

# 27. DATABASE RULES

Room is the preferred local database where relational persistence is required.

Rules:

- Define explicit entities.
- Define indexes intentionally.
- Define migrations for schema changes.
- Do not delete user data during migration.
- Do not run blocking database operations on the main thread.
- Do not store raw sensitive notification content unless necessary.
- Store only the minimum required data.

---

# 28. DATASTORE RULES

Use DataStore for lightweight preferences and configuration.

Do not use DataStore as an ad-hoc relational database.

Settings must be typed and centrally defined.

No arbitrary string keys scattered across the codebase.

---

# 29. UI RULES

The UI must follow `design.md`.

UI code must:

- be state-driven,
- support loading / empty / error states,
- preserve accessibility,
- avoid unnecessary recomposition,
- avoid business logic inside composables.

Do not implement random visual changes that contradict the approved design system.

---

# 30. GOOGLE STITCH MCP RULES

Google Stitch MCP is the approved design-generation source for UI exploration and design output where available.

Rules:

1. Use Stitch for visual exploration, layouts, components, and screen concepts.
2. Do not invent an unrelated visual system when an approved Stitch output exists.
3. Treat generated visual output as design input, not as authoritative business logic.
4. Validate all generated UI against `design.md`.
5. Generated UI must be adapted to Android/Compose constraints.
6. Never blindly copy unsupported web-only behavior into the Android application.
7. Accessibility and device compatibility take priority over decorative fidelity.

Stitch output does not override:

- Android platform rules,
- security rules,
- architecture.md,
- user privacy requirements.

---

# 31. ADB-FIRST DEBUGGING POLICY

ADB is the mandatory primary mechanism for runtime debugging and device verification.

The AI must be comfortable using and interpreting commands such as:

```text
adb devices
adb shell dumpsys notification
adb logcat
adb shell pm list packages
adb shell pm path <package>
adb shell am start <activity>
adb shell am force-stop <package>
adb shell settings
adb shell cmd
adb bugreport
```

Use only commands appropriate to the target Android version and device.

Never invent an ADB command.

---

# 32. DEBUGGING WORKFLOW

The required debugging model is:

```text
SYMPTOM
↓
OBSERVED BEHAVIOR
↓
REPRODUCTION
↓
EVIDENCE COLLECTION
↓
ROOT CAUSE HYPOTHESIS
↓
VERIFICATION
↓
MINIMAL REMEDIATION
↓
REGRESSION TEST
↓
DOCUMENTATION
```

Do not write “fixed” until verification exists.

---

# 33. ADB EVIDENCE RULE

When debugging a device-specific problem, collect evidence before changing code whenever feasible.

Examples:

- notification not received → inspect `dumpsys notification`
- service not running → inspect process/service state and logcat
- parser wrong → capture actual notification metadata
- TTS silent → inspect engine state, audio state, and logs
- app killed → inspect lifecycle/logcat and device battery restrictions

Do not solve an observed runtime problem entirely from speculation.

---

# 34. TEST DEVICE RULE

Testing must use real Android devices or a verified emulator where platform behavior is representative.

For notification listener and OEM-specific background behavior, real devices are preferred.

Record:

```text
Device
Android version
Build version
UPI app version
Application build
Date/time
Observed behavior
```

---

# 35. UNIT TEST RULES

All deterministic business logic must have unit tests.

Mandatory unit-test targets include:

- amount extraction
- credit detection
- provider detection
- sender extraction
- currency parsing
- validation
- deduplication
- announcement generation
- settings mapping

---

# 36. NEGATIVE TEST RULE

Every positive parser test must have negative or adversarial coverage.

Examples:

Positive:

```text
Payment received ₹500 from Rahul
```

Negative:

```text
Pay ₹500 now
OTP for ₹500
Cashback ₹500 available
Account balance ₹500
You paid ₹500
```

The parser must not confuse these with a received payment.

---

# 37. REGRESSION RULE

Every bug that reaches manual testing should become a regression test when practical.

A bug is not fully closed merely because the immediate code path works.

The prevention mechanism must be captured.

---

# 38. PHASE-GATE RULE

The project uses strict sequential phase gates.

```text
PHASE N IMPLEMENTATION
↓
AUTOMATED TESTS
↓
ADB DEVICE TEST
↓
MANUAL VERIFICATION
↓
BUG FIX LOOP
↓
PHASE REVIEW
↓
APPROVAL
↓
PHASE N+1
```

No phase may be considered complete without approval.

---

# 39. NO-SKIP RULE

The AI must not silently skip a phase because:

- the feature seems simple,
- code already exists,
- an earlier prototype worked,
- a generated implementation appears correct,
- the user is in a hurry.

If a phase cannot be completed, mark it blocked.

---

# 40. ACCEPTANCE CRITERIA RULE

Every feature must have explicit acceptance criteria.

Example:

```text
WHEN a supported UPI application posts a validated credit notification
AND the app is enabled for that provider
THEN exactly one announcement is queued for the matching transaction.
```

Acceptance criteria must be testable.

Avoid vague criteria such as:

```text
“Should work reliably.”
```

Instead define observable conditions.

---

# 41. FAILURE-HANDLING RULE

Every major subsystem must define expected failures.

Minimum failure matrix:

```text
NotificationListener unavailable
Unknown provider
Unknown notification format
Amount not found
Debit transaction
Duplicate transaction
TTS unavailable
TTS language missing
Database unavailable
Service restarted
Permissions missing
Battery restriction active
```

For each case define:

```text
Detection
Fallback
User-visible behavior
Logging
Recovery
```

---

# 42. SAFE FALLBACK RULE

When transaction parsing fails, the default behavior is:

```text
DO NOT ANNOUNCE
```

False positive payment announcements are more dangerous than missed announcements.

Never invent an amount from a nearby unrelated number.

---

# 43. CONCURRENCY RULES

The system may receive multiple notifications close together.

Design for concurrency.

Do not assume one-at-a-time delivery.

The announcement queue must be deterministic under bursts.

Database operations must be thread-safe.

Avoid race conditions in:

- deduplication
- TTS initialization
- settings reads
- transaction writes
- service lifecycle handling

---

# 44. THREADING RULES

Do not block the main thread with:

- database operations
- heavy parsing
- file I/O
- long waits
- network I/O
- TTS initialization where inappropriate

Do not use arbitrary `Thread.sleep()` as a synchronization strategy in production logic.

Use structured concurrency and lifecycle-aware mechanisms.

---

# 45. COROUTINE RULES

Use structured concurrency.

Avoid:

- `GlobalScope`
- unbounded background jobs
- orphan coroutines
- silently swallowed cancellation

Cancellation must be respected.

Coroutine scopes must have clear ownership.

---

# 46. ERROR HANDLING RULES

Never use:

```text
catch (Exception) { }
```

or equivalent silent swallowing unless there is an explicit documented reason.

At minimum:

- log technical failure safely,
- preserve user privacy,
- provide appropriate fallback,
- maintain system state consistency.

Do not show internal stack traces to end users.

---

# 47. EXCEPTION CLASSIFICATION

Distinguish:

```text
Expected failure
Recoverable failure
Non-recoverable failure
Programming bug
Configuration error
Platform limitation
```

Do not treat all exceptions identically.

---

# 48. STATE MANAGEMENT RULES

Important system state must be explicit.

Example states:

```text
DISABLED
STARTING
READY
PROCESSING
SPEAKING
DEGRADED
ERROR
```

Avoid scattered boolean flags that create contradictory states.

---

# 49. SECURITY RULES

Never store:

- UPI PIN
- banking password
- card CVV
- bank credentials
- authentication secrets

The application does not need them for its core notification-announcement function.

---

# 50. TRUST BOUNDARY RULE

UPI notifications are untrusted input.

Treat every notification as attacker-controlled or malformed input.

This means:

- never execute notification text,
- never parse it as code,
- never use it to construct shell commands,
- never use it directly in SQL strings,
- never trust URLs embedded in notification content.

All values must be treated as data.

---

# 51. INPUT SANITIZATION RULE

Normalize notification text before parsing where appropriate.

Examples:

- Unicode normalization
- whitespace normalization
- line break normalization
- safe punctuation handling
- locale-aware number normalization

Do not normalize so aggressively that meaningful transaction information is destroyed.

---

# 52. LOCAL DATA RETENTION RULE

Store only what is needed.

Define retention policy explicitly.

Do not keep unlimited raw notification data.

Where raw notification text is not necessary, prefer structured normalized transaction data.

---

# 53. USER CONTROL RULE

Users must be able to configure which supported providers are enabled.

The application must not silently announce every possible payment-related notification source.

---

# 54. ACCESSIBILITY RULES

The application must support:

- readable font sizes,
- content descriptions where applicable,
- adequate contrast,
- touch targets appropriate for Android,
- screen reader compatibility where practical,
- non-color-only status communication.

Accessibility is not an optional polish step.

---

# 55. UI STATE RULES

Every major screen should define:

```text
Loading
Success / Content
Empty
Error
Disabled / unavailable
```

Do not leave users with a silent blank screen when a state is known.

---

# 56. PERFORMANCE RULES

Do not optimize based on guesses.

Performance claims must be measured.

Track where useful:

- notification-to-processing latency
- processing duration
- TTS queue latency
- memory usage
- startup time
- database operation duration
- battery impact

Never claim a target is met without measurement.

---

# 57. BATTERY RULES

Battery efficiency is a first-class requirement.

Prefer:

```text
event-driven callbacks
short-lived work
bounded queues
lazy initialization
minimal polling
```

Do not keep unnecessary components active.

---

# 58. COMPATIBILITY RULES

Do not assume behavior observed on one Android device applies to all devices.

Compatibility should be validated across representative vendors where relevant.

Potential test families:

```text
Google Pixel
Samsung
Xiaomi / Redmi / POCO
OnePlus
OPPO / Realme
Vivo / iQOO
```

For OEM behavior, record observations rather than making universal claims.

---

# 59. API-LEVEL RULE

Whenever Android behavior depends on API level:

1. identify minimum supported API,
2. identify behavior changes,
3. guard code appropriately,
4. test relevant API levels,
5. document limitations.

Do not rely on outdated assumptions from older Android releases.

---

# 60. MANIFEST RULES

Keep `AndroidManifest.xml` minimal and explicit.

Every component must have a reason to exist.

Document non-obvious manifest declarations.

Do not add exported components casually.

Use explicit export configuration according to the component's actual role.

---

# 61. INTENT SECURITY RULES

For exported activities/services/receivers:

- minimize exported surface,
- validate incoming intents,
- do not trust extras blindly,
- avoid unnecessary implicit intents.

Do not make an internal component exported simply to make testing easier.

---

# 62. STORAGE SECURITY RULES

Prefer application-private storage.

Do not write sensitive transaction data to public external storage without an explicit requirement and security review.

---

# 63. BUILD RULES

A code change is not complete until the project builds successfully.

Minimum workflow:

```text
Clean / appropriate build
Compile
Unit tests
Install on device
Runtime verification
```

Do not claim build success without an actual successful build.

---

# 64. GRADLE RULES

Do not modify Gradle configuration casually.

Any change to:

- plugin versions
- Kotlin version
- Compose BOM
- Android Gradle Plugin
- dependency versions
- compiler configuration

must be justified and tested.

---

# 65. CODE STYLE RULES

Use idiomatic Kotlin.

Prefer:

- immutable values,
- small functions,
- explicit contracts,
- descriptive naming,
- composition over duplication.

Avoid:

- magic numbers,
- hidden mutable global state,
- giant functions,
- duplicated business rules,
- dead code.

---

# 66. NAMING RULES

Names must describe intent.

Bad:

```text
x
temp
data
manager2
util
helper
```

Preferred:

```text
parsedTransaction
announcementQueue
notificationFingerprint
providerParser
```

Generic names such as `Utils` are discouraged unless the scope is genuinely generic and cohesive.

---

# 67. COMMENTS RULES

Comments should explain **why**, not merely repeat **what** the code says.

Bad:

```text
// increment i
 i++
```

Good:

```text
// Keep a short debounce window because some providers post the same event twice.
```

---

# 68. DOCUMENTATION RULES

When behavior changes, update relevant documentation.

At minimum consider:

- architecture.md
- rules.md
- phases.md
- design.md
- memory.md
- feature-specific docs
- test specifications

Do not allow documentation drift.

---

# 69. GIT RULES

Commit messages should describe the change clearly.

Preferred format:

```text
feat(notification): add provider parser
fix(tts): prevent duplicate announcement queueing
refactor(parser): isolate amount extraction
 test( parser ): add debit negative cases
```

Use consistent conventional-style prefixes where the repository adopts them.

---

# 70. COMMIT SCOPE RULE

One commit should ideally represent one coherent change.

Avoid mixing:

```text
feature + unrelated refactor + formatting + dependency upgrade
```

in one opaque commit.

---

# 71. REVIEW RULE

Every meaningful change should answer:

```text
What changed?
Why?
What could break?
What tests were added?
What device testing was performed?
What remains unverified?
```

---

# 72. AI SELF-REVIEW RULE

Before declaring a task complete, the AI must review its own changes for:

- architecture violations
- invented APIs
- unnecessary dependencies
- privacy leakage
- untested edge cases
- nullability issues
- concurrency problems
- lifecycle issues
- logging leakage
- incorrect parser assumptions
- platform-version issues

---

# 73. “DONE” DEFINITION

A task is DONE only when all applicable conditions are true:

```text
Code implemented
Build passes
Unit tests pass
Relevant integration tests pass
ADB test performed
Runtime behavior observed
No critical regression
Documentation updated
Known limitations recorded
Phase approval obtained (where phase gate applies)
```

“Code compiled” is not equivalent to “feature complete”.

---

# 74. NO FALSE CLAIMS RULE

The AI must never say:

```text
Fixed
Tested
Verified
Production-ready
Secure
Compatible
Works everywhere
Works reliably
```

unless evidence supports the statement.

Preferred wording when evidence is incomplete:

```text
Implemented, but device verification is pending.

Build passes; notification behavior is not yet verified on the target UPI app.

Works on the tested device; OEM-wide compatibility remains unverified.
```

---

# 75. BUG REPORT RULE

Bug reports must include:

```text
Environment
Expected behavior
Actual behavior
Reproduction steps
Relevant logs
ADB evidence
Root cause
Fix
Regression test
Remaining risk
```

---

# 76. ROOT-CAUSE RULE

Do not patch symptoms without understanding causality.

Examples:

If TTS does not speak:

Do not immediately increase delay.

First determine whether the failure is:

```text
TTS initialization
language availability
audio focus
service lifecycle
queue handling
volume state
engine failure
```

---

# 77. RETRY RULE

Retries must be bounded.

Never create infinite retries for:

- TTS
- database writes
- service restarts
- initialization

Retry only when the failure is plausibly transient.

---

# 78. TIMEOUT RULE

Every potentially blocking external/platform interaction should have a bounded behavior where appropriate.

Do not wait indefinitely for:

- initialization
- database operations
- IPC calls
- asynchronous state transitions

---

# 79. IDEMPOTENCY RULE

Operations that may execute more than once must be safely repeatable where practical.

Examples:

- initialization
- preference setup
- migration checks
- notification processing

---

# 80. TEST DATA RULE

Use synthetic transaction examples in automated tests.

Do not commit real users’ financial notifications, screenshots, personal identifiers, or transaction IDs into the repository.

---

# 81. SECRETS RULE

Never commit:

- passwords
- API keys
- tokens
- private certificates
- keystores
- signing secrets
- personal credentials

Use secure local configuration mechanisms where secrets are genuinely required.

---

# 82. RELEASE BUILD RULES

Before release:

- remove verbose sensitive logging,
- verify permissions,
- verify exported components,
- run regression tests,
- run device testing,
- verify versioning,
- verify app behavior after process restart,
- verify notification listener setup,
- verify TTS behavior,
- verify storage migrations.

---

# 83. PLAY STORE / DISTRIBUTION RULE

Distribution requirements must be verified against the current official platform policy at release time.

Do not rely on remembered policy text.

The AI must flag policy-sensitive areas rather than inventing compliance conclusions.

---

# 84. THIRD-PARTY APP DEPENDENCY RULE

The core application must not depend on private APIs of UPI providers.

Provider integrations should use observable Android notification behavior and supported platform mechanisms.

Do not reverse-engineer private authentication or transaction APIs for the purpose of core operation.

---

# 85. PACKAGE NAME RULE

Provider package IDs must be verified from observed device state or trusted sources.

Do not hard-code a guessed package identifier and treat it as permanent truth.

Where practical, maintain provider metadata in a centralized registry.

---

# 86. PROVIDER PARSER VERSIONING RULE

Parser behavior can evolve independently.

Keep provider-specific parser changes isolated so one provider update does not break all providers.

Each parser should have:

- test fixtures,
- positive samples,
- negative samples,
- version notes,
- known limitations.

---

# 87. FIXTURE PRIVACY RULE

Real-world notification fixtures must be anonymized before being added to tests.

Example:

```text
Original payer: real person's name
Test payer: TEST_USER
```

Transaction IDs must be synthetic.

---

# 88. OBSERVABILITY RULE

Observability must help answer:

```text
Did notification arrive?
Was provider recognized?
Was transaction parsed?
Why was it rejected?
Was it deduplicated?
Was announcement queued?
Did TTS start?
Did TTS complete?
```

Use safe structured events rather than logging raw financial payloads.

---

# 89. INTERNAL DEBUG MODE

A dedicated debug mode may expose additional diagnostics locally, but it must:

- remain opt-in,
- minimize sensitive output,
- never silently upload data,
- be clearly separated from release logging.

---

# 90. TEST AUTOMATION RULE

Automated tests should be fast and deterministic.

Prefer:

```text
pure unit tests
parser fixture tests
repository tests
integration tests
```

Use device tests where platform behavior cannot be faithfully simulated.

---

# 91. DEVICE TEST MATRIX

At minimum, the project should maintain a test matrix containing:

```text
Android version
Manufacturer
Model
UPI application
UPI application version
Notification permission/configuration
Battery optimization state
TTS engine
Language
Result
```

Do not claim broad OEM support based on a single device.

---

# 92. MANUAL PAYMENT TESTING RULE

For payment-flow validation, only use controlled test transactions appropriate to the environment.

Never trigger real financial operations merely to test a parser when synthetic notification fixtures are sufficient.

---

# 93. LOGCAT RULE

Use focused filters when possible.

Do not collect or publish entire device logs unnecessarily because they can contain unrelated private information.

Sanitize logs before sharing outside the development environment.

---

# 94. CRASH ANALYSIS RULE

When the app crashes:

1. reproduce,
2. capture relevant logcat evidence,
3. identify stack trace,
4. identify component boundary,
5. reproduce with minimal case,
6. fix root cause,
7. add regression coverage.

Do not suppress crashes merely to hide symptoms.

---

# 95. MEMORY LEAK RULE

Services, listeners, TTS engines, and callbacks must not accidentally retain activities or contexts beyond their required lifecycle.

Always release resources appropriately.

---

# 96. RESOURCE CLEANUP RULE

Explicitly clean up:

- TTS engine
- listeners
- callbacks
- coroutine scopes where owned
- database resources where applicable
- temporary buffers

Do not depend solely on garbage collection for lifecycle-sensitive resources.

---

# 97. UI NAVIGATION RULE

Navigation destinations must be explicit.

Do not couple business logic to screen transitions.

Deep links or external intents require security review.

---

# 98. SETTINGS MIGRATION RULE

When a setting changes schema or semantics:

- define migration behavior,
- preserve compatible user choices,
- document breaking changes,
- test old → new settings conversion.

---

# 99. BACKWARD-COMPATIBILITY RULE

When introducing new parser or database behavior, preserve existing supported behavior unless the change is intentional and documented.

Every breaking change requires:

```text
Reason
Impact
Migration
Tests
Documentation
```

---

# 100. FEATURE FLAG RULE

Feature flags are allowed only when they solve a real rollout/testing need.

Do not create permanent undocumented flags.

Each flag must have:

- owner
- purpose
- default
- rollout behavior
- removal condition

---

# 101. CODE GENERATION RULE

Generated code must be reviewed like handwritten code.

AI-generated code is not trusted by default.

Before acceptance:

- verify imports,
- verify APIs,
- verify lifecycle,
- verify concurrency,
- verify security,
- verify tests,
- verify runtime behavior.

---

# 102. LARGE CHANGE RULE

For changes touching multiple modules, the AI must first produce a concise implementation plan describing:

```text
Files to modify
Modules affected
Data flow changes
Tests required
Risk areas
Rollback considerations
```

Implementation should begin only after the plan is internally consistent.

---

# 103. REFACTORING RULE

Refactoring is allowed only when:

- it reduces actual complexity,
- tests protect behavior,
- the scope is understood.

Do not refactor merely because AI prefers another code style.

---

# 104. DEAD CODE RULE

Remove dead code introduced by the change.

Do not keep abandoned implementations “just in case” without a clear reason.

---

# 105. DUPLICATION RULE

Business rules must have one authoritative implementation.

For example, credit detection must not be independently implemented in:

- notification listener,
- UI,
- parser,
- TTS service.

Centralize the rule.

---

# 106. CONFIGURATION RULE

Provider lists, announcement templates, default settings, and thresholds should be centralized.

Avoid magic strings scattered through multiple classes.

---

# 107. LOCALIZATION RULE

Never concatenate user-facing text in random locations.

Use centralized resources/template logic.

Every supported language must have tests for important announcement formats.

---

# 108. NUMBER-TO-SPEECH RULE

Amount-to-words conversion must be deterministic and locale-aware.

Examples that require explicit testing:

```text
₹1
₹10
₹50
₹99
₹100
₹101
₹500
₹999
₹1,000
₹10,000
₹1,00,000
₹10,00,000
₹1.50
```

Do not assume English and Hindi have identical number-word rules.

---

# 109. CURRENCY RULE

Currency symbols and numeric formatting must not be hard-coded into provider parsers unless the provider format genuinely requires it.

Separate:

```text
parsing
currency normalization
display formatting
speech formatting
```

---

# 110. PRECISION RULE

Do not silently round transaction amounts.

If the source notification contains decimal value, preserve precision unless a documented speech/display policy says otherwise.

---

# 111. TIME RULE

Prefer platform-provided notification timestamps where appropriate.

Do not manufacture transaction times from current time unless clearly identified as processing time.

Distinguish:

```text
notificationPostedAt
transactionOccurredAt
processedAt
announcedAt
```

---

# 112. CLOCK RULE

Do not use wall-clock time as the sole source of correctness in deduplication if a stronger transaction/reference identifier exists.

Use monotonic timing mechanisms for in-process elapsed-time behavior when appropriate.

---

# 113. INTERRUPTION RULE

Define behavior when:

- another TTS is speaking,
- music is playing,
- a call arrives,
- multiple payments arrive rapidly,
- the app process restarts.

Do not leave queue behavior implicit.

---

# 114. QUEUE RULE

The TTS queue must have documented semantics:

```text
QUEUE_ALL
REPLACE_PENDING
COALESCE_DUPLICATES
DROP_DUPLICATES
```

Pick behavior deliberately.

Do not implement arbitrary queueing by accident.

---

# 115. USER EXPERIENCE RULE

The application must favor trustworthy information over aggressive announcements.

When confidence is low:

```text
DO NOT SPEAK
```

A missed announcement can be reviewed in history; a false announcement can mislead the user at the point of sale.

---

# 116. TEST RESULT RULE

Every phase test report must distinguish:

```text
PASS
FAIL
BLOCKED
NOT TESTED
NOT APPLICABLE
```

“Probably works” is not a valid status.

---

# 117. APPROVAL RECORD RULE

Each completed phase must record:

```text
Phase
Date
Build version
Device(s)
Tests run
Failures
Fixes
Final result
Approver
Notes
```

---

# 118. MEMORY FILE RULE

`memory.md` is for durable project state.

When a meaningful architectural decision is made, update memory so future sessions do not rediscover the same decision or accidentally reverse it.

Do not use memory.md as an unstructured diary.

---

# 119. DECISION RECORD RULE

Important decisions should include:

```text
Decision
Reason
Alternatives considered
Consequences
Date
```

This is especially important for:

- parser strategy,
- storage choices,
- permission decisions,
- service lifecycle decisions,
- TTS queue policy,
- provider support.

---

# 120. AI CONTEXT RULE

Before implementing a feature, the AI must load the minimum relevant context.

Do not reread the entire repository blindly for every small edit, but do not make architectural changes without enough context.

Context must be sufficient to avoid local optimizations that break system-level invariants.

---

# 121. AI OUTPUT RULE

For non-trivial tasks, the AI's work product should include:

```text
Implementation summary
Files changed
Tests added/updated
ADB verification
Known limitations
Next required approval
```

Do not hide unverified limitations.

---

# 122. STOP CONDITIONS

The AI must STOP rather than continue coding when:

- required information is unavailable,
- an API capability is uncertain and unverified,
- the architecture is contradictory,
- a security boundary is unclear,
- a destructive data migration is required without approval,
- device behavior contradicts documentation and needs investigation,
- tests fail for an unresolved reason,
- a phase gate has not been approved,
- the requested behavior would require unsupported/private behavior.

Stopping is preferable to fabricating a solution.

---

# 123. ESCALATION RULE

When blocked, report:

```text
BLOCKED

Reason:
...

Evidence:
...

What is known:
...

What is unknown:
...

Required decision / test:
...
```

Do not silently invent a workaround.

---

# 124. PROHIBITED DEVELOPMENT PATTERNS

The following patterns are prohibited unless explicitly justified and approved:

- giant singleton application state
- hidden global mutable state
- arbitrary static caches
- polling loops for notification detection
- unrestricted background threads
- silent exception swallowing
- giant regex covering every provider
- raw SQL string concatenation
- logging raw financial notifications
- unnecessary network dependency
- unbounded retries
- hard-coded device-specific hacks without documentation
- undocumented reflection hacks
- private API dependence
- fake compatibility claims
- copy-pasted business logic across modules

---

# 125. DEVICE-SPECIFIC WORKAROUND RULE

A device-specific workaround is permitted only when:

1. the behavior is reproducible,
2. root cause is understood or strongly evidenced,
3. generic implementation fails,
4. workaround is isolated,
5. device / OS conditions are explicit,
6. regression testing exists.

Do not pollute the whole architecture with one-device hacks.

---

# 126. PRODUCTION SAFETY RULE

Before any release candidate is accepted, verify that failure of any single optional subsystem does not silently turn valid payments into misleading announcements.

The application must fail safely.

---

# 127. DATA CONSISTENCY RULE

If a transaction is written to history but announcement fails, history may still retain the transaction with an announcement status such as:

```text
PENDING
SUCCESS
FAILED
SKIPPED
```

Do not imply the user heard an announcement when TTS actually failed.

---

# 128. EVENT MODEL RULE

Prefer immutable event objects at subsystem boundaries.

Once an event enters the domain pipeline, mutation should be minimized.

This improves:

- reproducibility,
- debugging,
- testing,
- auditability.

---

# 129. EVENT ORDER RULE

If event ordering matters, document the ordering guarantee.

Do not assume Android notifications always arrive in perfect transaction order.

The system must tolerate near-simultaneous events.

---

# 130. UNKNOWN PROVIDER RULE

If an unsupported provider posts a payment-like notification:

```text
Ignore by default
Log safe metadata if debugging is enabled
Do not announce
```

The architecture may support a future provider extension without changing existing providers.

---

# 131. PARSER CONFIDENCE RULE

A parser may produce a confidence signal internally, but the system must define how that confidence affects processing.

Do not invent a numeric confidence score merely because it sounds sophisticated.

If confidence is implemented, its semantics must be documented and tested.

---

# 132. TEST FIXTURE NAMING RULE

Fixtures should identify:

```text
provider
scenario
expected result
language
```

Example:

```text
phonepe_credit_en_success.json
phonepe_debit_en_rejected.json
paytm_credit_hi_success.json
unknown_format_rejected.json
```

---

# 133. RELEASE NOTES RULE

Document user-visible changes, compatibility changes, provider parser changes, and known limitations.

Do not claim support for a provider simply because its name appears in configuration.

---

# 134. CHANGE IMPACT RULE

Any change to notification parsing must trigger regression testing for all supported providers.

Any change to TTS queueing must trigger concurrency and rapid-event tests.

Any change to database schema must trigger migration tests.

Any change to permissions must trigger install / upgrade tests.

---

# 135. CROSS-MODULE CONTRACT RULE

When two modules communicate, define an explicit contract.

Avoid passing loosely typed maps when a stable data class or interface is appropriate.

Contracts should be:

- versionable,
- testable,
- understandable.

---

# 136. API WRAPPER RULE

If direct platform APIs are noisy or difficult to test, wrap them behind a small interface.

Examples:

```text
NotificationSource
SpeechEngine
Clock
AudioController
TransactionDao
```

This enables deterministic unit testing.

---

# 137. CLOCK ABSTRACTION RULE

Time-dependent business logic should be testable without waiting in real time.

Where appropriate, use an injectable clock/time source.

Do not hard-code `System.currentTimeMillis()` everywhere in domain logic.

---

# 138. RANDOMNESS RULE

Randomness has no place in payment parsing or transaction validation.

If randomness is ever required for a non-critical UI behavior, it must remain outside financial decision logic.

---

# 139. FORMATTING RULE

Use one canonical formatting layer for:

- currency display
- amount-to-speech
- date/time display
- provider labels

Do not duplicate locale rules in multiple features.

---

# 140. TESTABILITY OVER CLEVERNESS RULE

Prefer explicit, testable implementations over compressed “clever” code.

Readability and determinism are more important than minimal line count.

---

# 141. SIMPLEST-CORRECT-SOLUTION RULE

When multiple implementations satisfy requirements, choose the simplest implementation that:

- preserves architecture,
- passes tests,
- respects platform behavior,
- minimizes dependencies,
- minimizes operational risk.

Do not add complexity without measurable benefit.

---

# 142. SECURITY REVIEW TRIGGER RULE

Security review is mandatory when modifying:

- permissions
- exported components
- external intents
- persistent sensitive storage
- notification access behavior
- network access
- cryptographic code
- third-party SDKs
- authentication mechanisms

---

# 143. PERFORMANCE REVIEW TRIGGER RULE

Performance review is required when changing:

- notification processing pipeline
- database schema/indexing
- TTS queue implementation
- background execution
- startup initialization
- repeated event processing

---

# 144. BATTERY REVIEW TRIGGER RULE

Battery impact must be reconsidered whenever code adds:

- timers
- polling
- wake locks
- long-lived services
- continuous observers
- repeated disk access

---

# 145. FINAL RELEASE GATE

The application must not be labeled Release Candidate until all of the following are satisfied:

```text
[ ] Architecture matches architecture.md
[ ] Rules are satisfied
[ ] All mandatory phases are approved
[ ] Build reproducibly succeeds
[ ] Unit tests pass
[ ] Parser regression suite passes
[ ] Device testing completed
[ ] ADB verification completed
[ ] TTS behavior verified
[ ] Duplicate handling verified
[ ] Unsupported notifications rejected safely
[ ] Sensitive logging reviewed
[ ] Permission set reviewed
[ ] Background behavior reviewed
[ ] Database migrations verified
[ ] Known limitations documented
[ ] No unsupported API assumptions remain
[ ] No unresolved critical bugs remain
```

---

# 146. AI FINAL CHECKLIST

Before the AI says “complete”, it must ask itself:

```text
Did I verify every non-obvious API I used?
Did I invent any provider behavior?
Did I invent any package name?
Did I invent any Android permission?
Did I test the parser with negative cases?
Did I verify notification behavior with ADB?
Did I run the app on a real test environment where necessary?
Did I inspect logs?
Did I avoid leaking financial information?
Did I preserve architecture boundaries?
Did I add unnecessary dependencies?
Did I document assumptions?
Did I update regression tests?
Did I update documentation?
Did I respect the current phase gate?
Did I clearly state anything still unverified?
```

If any answer is “no”, the task is not complete.

---

# 147. GOLDEN RULE

> **Never guess when the system can be inspected. Never claim verification when it was not performed. Never trade reliability for speed. Never trade user privacy for convenience.**

The correct behavior for an AI coding agent is:

```text
Evidence first.
Implementation second.
Verification third.
Claim only what is proven.
```

---

# 148. PROJECT-SPECIFIC SUMMARY

This repository is a real Android application with financial-notification-related behavior.

Therefore the engineering posture is:

```text
STRICT
DETERMINISTIC
PRIVACY-FIRST
LOCAL-FIRST
ADB-VERIFIED
TEST-GATED
EVIDENCE-BASED
NO-HALLUCINATION
```

Any future developer or AI agent inheriting this repository must treat these properties as architectural invariants, not suggestions.

---

# 149. END OF RULES

Changes to these rules must themselves be reviewed and documented.

If a future requirement conflicts with a rule, explicitly record the conflict, decision, rationale, and migration impact instead of silently violating the rule.

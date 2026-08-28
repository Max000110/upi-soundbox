# UPI Voice Soundbox — `phases.md`

**Document Type:** Phase Plan, Delivery Gates, Validation Plan, and Execution Contract  
**Project:** UPI Voice Soundbox — Android local-first application  
**Version:** 1.0  
**Status:** Mandatory project execution document  
**Audience:** Human developer, AI coding agents, QA operators, reviewers, release engineers  
**Companion Documents:** `architecture.md`, `rules.md`, `design.md`, `memory.md`  
**Primary Development Environment:** Android Studio / command-line Android SDK  
**Primary Runtime Verification:** ADB + real Android device  
**Primary UI Design Source:** Google Stitch MCP where available and approved

---

# 0. PURPOSE

This document divides the project into controlled implementation phases.

The purpose is not merely to create a checklist. It defines:

- what must be built,
- what must be verified,
- what evidence must exist,
- what can block progression,
- what constitutes completion,
- what must be tested through ADB,
- what requires manual device validation,
- and when the human owner must approve progression.

The project SHALL be developed sequentially.

A phase is not complete merely because code exists or the application builds.

A phase is complete only when its implementation, automated tests, device tests, diagnostics, documentation, and acceptance criteria have passed.

---

# 1. NON-NEGOTIABLE EXECUTION MODEL

Every phase follows:

```text
PHASE START
    ↓
Read architecture.md
    ↓
Read rules.md
    ↓
Inspect current repository state
    ↓
Define phase scope
    ↓
Implement only phase scope
    ↓
Build
    ↓
Automated tests
    ↓
ADB installation
    ↓
ADB runtime verification
    ↓
Manual device verification
    ↓
Collect evidence
    ↓
Fix defects
    ↓
Regression test
    ↓
Phase review
    ↓
HUMAN APPROVAL
    ↓
Update memory.md
    ↓
Start next phase
```

No phase may bypass the testing gate.

No implementation agent may declare a phase complete without evidence.

No implementation agent may silently advance to the next phase.

---

# 2. PHASE STATUS STATES

Each phase SHALL have exactly one lifecycle state.

```text
NOT_STARTED
    ↓
IN_PROGRESS
    ↓
BLOCKED
    ↓
READY_FOR_TEST
    ↓
UNDER_TEST
    ↓
FAILED
    ↓
FIX_IN_PROGRESS
    ↓
READY_FOR_REVIEW
    ↓
APPROVED
```

Terminal state:

```text
CANCELLED
```

A phase in `BLOCKED`, `FAILED`, or `FIX_IN_PROGRESS` cannot be treated as complete.

---

# 3. APPROVAL GATE

## 3.1 Approval Rule

The next phase may start only when the current phase is explicitly approved.

Acceptable approval examples:

```text
PHASE 01 APPROVED
```

or

```text
Phase 1 testing passed. Proceed.
```

Silence does not count as approval.

Completion of automated tests does not count as approval.

A successful build does not count as approval.

---

# 4. EVIDENCE REQUIREMENT

Every completed phase must produce evidence appropriate to the phase.

Examples:

```text
Build output
Unit-test results
Instrumented-test results
ADB logcat evidence
dumpsys notification evidence
Screen captures
Diagnostic output
Device/version information
Parser fixtures
Regression test cases
```

Sensitive payment payloads must not be copied into public documentation or shared logs unnecessarily.

---

# 5. DEVICE TEST RECORD

For every device validation session, record:

```text
Device model:
Manufacturer:
Android version:
API level:
Build number:
UPI provider:
UPI app version:
Soundbox app version:
Date:
Time:
ADB connection:
Battery optimization state:
Notification access state:
TTS engine:
TTS language:
Audio route:
Result:
Known limitations:
```

Example:

```text
Device model: <device>
Android version: <version>
API: <api>
Provider: PhonePe
Provider version: <version>
App build: debug-<version>
ADB: PASS
Notification access: ON
TTS: READY
Result: PASS
```

---

# 6. COMMON ADB TOOLING

Only use ADB commands verified for the connected device and installed SDK.

Common baseline commands:

```bash
adb devices
adb shell getprop ro.build.version.release
adb shell getprop ro.build.version.sdk
adb shell pm list packages
adb logcat
adb shell dumpsys notification
adb shell dumpsys package <package>
adb shell am force-stop <package>
adb shell am start <activity>
adb install <apk>
adb uninstall <package>
```

Never invent a command.

When a command is uncertain, verify it from official Android/ADB documentation or inspect the available device command set.

---

# 7. GLOBAL DEFINITION OF DONE

A feature is Done only when:

- scope is implemented,
- architecture boundaries are respected,
- code compiles,
- relevant automated tests pass,
- relevant ADB tests pass,
- manual device behavior is verified,
- known failure modes are exercised,
- logs are reviewed,
- no critical regression is observed,
- documentation is updated,
- `memory.md` is updated,
- acceptance criteria are checked,
- and the phase is explicitly approved.

---

# 8. PHASE MAP

```text
PHASE 00 — Research & Environment Baseline
PHASE 01 — Repository & Application Foundation
PHASE 02 — UI Skeleton & Design System
PHASE 03 — Notification Listener Foundation
PHASE 04 — Notification Normalization Pipeline
PHASE 05 — Provider Registry & Parser Contract
PHASE 06 — Payment Detection & Validation
PHASE 07 — Amount / Currency Extraction
PHASE 08 — Deduplication & Event Ordering
PHASE 09 — TTS Engine Foundation
PHASE 10 — Announcement Queue & Audio Policy
PHASE 11 — Local Persistence & Transaction History
PHASE 12 — Settings & Provider Controls
PHASE 13 — Diagnostics & Self-Test System
PHASE 14 — End-to-End Integration
PHASE 15 — Device / OEM Compatibility
PHASE 16 — Reliability, Battery & Lifecycle Hardening
PHASE 17 — Security & Privacy Hardening
PHASE 18 — Regression & Release Candidate
PHASE 19 — Final Acceptance & Release
```

---

# PHASE 00 — RESEARCH & ENVIRONMENT BASELINE

## Objective

Establish a verified technical baseline before writing production feature code.

## Scope

Research and verify:

- target Android SDK strategy,
- minimum supported Android version,
- notification-listener behavior,
- current platform restrictions,
- notification payload fields,
- TTS availability model,
- foreground-service implications if used,
- current Gradle/AGP compatibility,
- device debugging capability,
- actual notification behavior of the selected UPI applications.

## Deliverables

```text
docs/research/
docs/research/android-platform.md
docs/research/provider-observations.md
docs/research/device-matrix.md
```

## Required Decisions

Document:

- minSdk,
- targetSdk,
- compileSdk,
- JDK version,
- Kotlin version,
- Compose baseline,
- Gradle/AGP baseline,
- Room/DataStore strategy,
- notification listener strategy,
- TTS strategy.

Exact versions must be verified rather than invented.

## UPI Provider Research

Create a provider matrix containing:

```text
Provider
Package name
Notification available?
Credit wording observed?
Amount field observed?
Payer field observed?
Localization observed?
Grouped notification?
Known limitations
Last verified date
```

Do not claim provider support until the current installed version is actually observed or independently verified.

## ADB Verification

Confirm:

```bash
adb devices
adb shell getprop ro.build.version.release
adb shell getprop ro.build.version.sdk
```

Inspect notification state:

```bash
adb shell dumpsys notification
```

## Exit Criteria

- target device connected,
- Android version recorded,
- notification access behavior understood,
- initial provider observations recorded,
- no unresolved critical architecture blocker.

## Failure Conditions

Block if:

- device cannot be connected,
- Android test environment is unavailable,
- chosen architecture relies on an unverified platform capability,
- notification access cannot be validated.

## Approval Gate

Human approval required.

---

# PHASE 01 — REPOSITORY & APPLICATION FOUNDATION

## Objective

Create the stable Android project foundation without implementing payment logic.

## Scope

Create:

```text
app/
core/
domain/
data/
notification/
parser/
speech/
storage/
settings/
ui/
diagnostics/
testing/
```

Establish:

- Gradle build,
- Android manifest,
- application class,
- dependency injection baseline if approved,
- Compose baseline,
- test source sets,
- lint/static-analysis baseline.

## Deliverables

The application must:

- compile,
- install,
- launch,
- display placeholder home screen.

No payment detection is implemented yet.

## ADB Tests

```bash
adb install <debug-apk>
adb shell am start -n <package>/<activity>
adb logcat --pid=<pid>
```

Verify:

- no startup crash,
- no fatal exception,
- activity launch successful.

## Automated Tests

At minimum:

- application startup test,
- configuration loading test,
- basic ViewModel test,
- dependency graph creation test where applicable.

## Exit Criteria

```text
Build: PASS
Install: PASS
Launch: PASS
Crash-free startup: PASS
Tests: PASS
```

## Approval Gate

Required.

---

# PHASE 02 — UI SKELETON & DESIGN SYSTEM

## Objective

Implement the application shell based on approved design direction.

## Design Source

Use Google Stitch MCP for visual exploration and screen generation where available.

Stitch is a design-generation aid.

It does not override:

- `architecture.md`,
- `rules.md`,
- platform constraints,
- accessibility,
- privacy requirements.

## Screens

Minimum V1 screens:

```text
Home
Providers
Voice
Diagnostics
History (when persistence is enabled)
About / Privacy
```

## Home Screen

Must expose:

```text
Service status
Notification access status
TTS status
Current language
Test announcement
Last event state
```

## Providers Screen

Must expose:

```text
Provider enabled/disabled
Observed/unsupported state
```

## Voice Screen

Must expose:

```text
Language
Voice
Speech rate
Pitch
Volume policy
Test voice
```

## Diagnostics Screen

Must expose system readiness.

Example:

```text
Notification access     READY
Listener                CONNECTED
TTS                     READY
Language                READY
Audio                   READY
Battery state           UNKNOWN / OK / RESTRICTED
```

## Design Requirements

Must be:

- mobile-first,
- readable,
- high contrast,
- large enough for merchant-style usage,
- accessible,
- consistent with `design.md`.

## Tests

UI tests should cover:

- screen navigation,
- state rendering,
- disabled/ready states,
- configuration controls,
- test announcement trigger.

## ADB Tests

Install the app and verify:

- all screens open,
- rotation/state behavior where supported,
- no runtime exceptions.

## Exit Criteria

- approved Stitch/design direction reflected,
- UI skeleton complete,
- no business logic embedded in composables,
- UI tests pass.

## Approval Gate

Required.

---

# PHASE 03 — NOTIFICATION LISTENER FOUNDATION

## Objective

Establish the Android notification-listener boundary.

## Scope

Implement:

```text
NotificationListenerService
NotificationAccessChecker
ListenerConnectionState
RawNotification model
```

## Service Responsibilities

The listener SHALL:

1. receive notification event,
2. collect required metadata,
3. construct `RawNotification`,
4. dispatch to processing pipeline,
5. return quickly.

The listener SHALL NOT perform:

- heavy database work,
- TTS synthesis,
- complex synchronous parsing,
- blocking operations.

## Captured Fields

Where available:

```text
packageName
notificationKey
postedAt
title
text
bigTitle
bigText
textLines
category
```

Only store minimum required data.

## Notification Access

Implement a settings flow that explains:

```text
Notification access is required to detect supported payment notifications.
```

The app must never describe notification access as direct bank access.

## ADB Validation

Inspect:

```bash
adb shell dumpsys notification
```

Generate test notifications from controlled sources where possible.

Verify:

- listener receives events,
- package name captured,
- title/text captured,
- service remains stable.

## Negative Tests

Verify that:

- unrelated notification does not crash listener,
- empty notification does not crash listener,
- unusually large text does not crash listener,
- grouped notification does not crash listener.

## Exit Criteria

- listener callback verified,
- connection lifecycle verified,
- raw event model verified,
- no payment parsing yet,
- ADB evidence captured.

## Approval Gate

Required.

---

# PHASE 04 — NOTIFICATION NORMALIZATION PIPELINE

## Objective

Create a deterministic normalization layer before provider parsing.

## Scope

Normalize:

- Unicode whitespace,
- line endings,
- currency markers,
- Unicode decimal digits where safe,
- repeated whitespace,
- case for matching,
- field concatenation.

## Canonical Representation

Input:

```text
title
text
bigText
textLines
```

Output:

```text
NormalizedNotification
```

## Requirements

Normalization must not:

- alter semantic meaning,
- remove critical digits,
- silently convert ambiguous values,
- fabricate missing fields.

## Tests

Include:

```text
₹50
Rs 50
Rs. 50
INR 50
50 INR
Unicode spaces
newlines
tabs
Hindi digits where applicable
mixed punctuation
```

## Negative Tests

Must ensure:

```text
₹500 cashback
Balance ₹500
Pay ₹500
OTP ₹500
```

remain distinguishable from credit-payment messages.

## Approval Gate

Required.

---

# PHASE 05 — PROVIDER REGISTRY & PARSER CONTRACT

## Objective

Build the pluggable parser architecture.

## Contract

Use an interface equivalent to:

```kotlin
interface PaymentNotificationParser {
    fun supports(packageName: String): Boolean
    fun parse(notification: RawNotification): ParseResult
}
```

The exact API may vary if architecture changes are documented.

## Provider Registry

Create a registry for:

```text
Paytm
PhonePe
Google Pay
BHIM
Amazon Pay
CRED
Generic UPI fallback
```

Only enable a provider after verified evidence exists.

## Exact Package Matching

Prefer exact verified package identifiers.

Do not use loose substring matching when exact matching is practical.

## Parser Isolation

Each provider parser must:

- be independently testable,
- expose only structured output,
- avoid Android UI access,
- avoid TTS access,
- avoid database coupling.

## Fixtures

Create versioned parser fixtures:

```text
src/test/resources/notifications/
```

Each fixture should record:

```text
provider
appVersion if known
notification sample category
expected parse result
expected confidence
notes
```

Avoid storing unnecessary sensitive real-world data.

## Approval Gate

Required.

---

# PHASE 06 — PAYMENT DETECTION & VALIDATION

## Objective

Distinguish likely incoming-credit payment notifications from unrelated notifications.

## Processing Order

```text
Source filter
    ↓
Provider parser
    ↓
Direction classifier
    ↓
Amount validation
    ↓
Confidence gate
    ↓
PaymentEvent
```

## Credit States

Valid credit may include patterns such as:

```text
received
credited
payment received
money received
received from
```

These are examples, not universal provider rules.

Provider-specific evidence always wins.

## Debit Rejection

Default behavior:

```text
DEBIT → IGNORE
```

Examples:

```text
You paid
₹500 sent
account debited
payment made
```

must not become “money received”.

## Non-payment Rejection

Reject by default:

```text
OTP
cashback
offer
promotion
balance update
bill reminder
generic payment advertisement
```

## Confidence

Use deterministic confidence rules.

Confidence may consider:

```text
Trusted provider source
Explicit credit semantics
Valid monetary amount
Provider-specific structure
Transaction/reference signal
```

Do not use an LLM or remote classifier in the payment path.

## Acceptance Criteria

A valid fixture:

```text
credit + amount + supported provider
```

must produce an announceable candidate.

An invalid fixture:

```text
debit / OTP / offer / balance-only
```

must not.

## Approval Gate

Required.

---

# PHASE 07 — AMOUNT / CURRENCY EXTRACTION

## Objective

Extract money values reliably.

## Canonical Representation

Use integer minor units.

For INR:

```text
₹50.00 → 5000 paise
₹1,000.50 → 100050 paise
```

Do not use `Double` as the canonical money representation.

## Supported Forms

Test:

```text
₹1
₹10
₹99
₹100
₹999
₹1,000
₹10,000
₹1,00,000
₹10,00,000
₹1,000.50
Rs 50
Rs. 50
INR 50
50 INR
50 rupees
```

## Parser Rules

The amount parser must:

- enforce sane syntax,
- validate decimal precision,
- reject malformed amounts,
- avoid extracting unrelated numbers,
- use provider context.

## False Positives

Test against:

```text
UPI reference 123456789
OTP 123456
Account ending 1234
Cashback 50%
Date 12/08/2026
```

## Overflow Handling

Reject values that exceed the supported monetary range rather than overflowing.

## Approval Gate

Required.

---

# PHASE 08 — DEDUPLICATION & EVENT ORDERING

## Objective

Prevent duplicate announcements without suppressing legitimate payments.

## Problem

The same payment can produce:

```text
initial notification
updated notification
grouped notification
reposted notification
```

## Fingerprint

Prefer:

```text
provider
transaction reference
amount
payer
event time
```

Fallback:

```text
provider
amount
normalized payer
normalized content
time bucket
```

## Rules

Never deduplicate solely on:

```text
amount
```

Two legitimate ₹100 payments can happen close together.

## Queue Ordering

Accepted events must be processed FIFO unless an explicit priority policy exists.

## Deduplication Window

A configurable short window may be used.

Default must be justified by testing.

Do not assume one fixed duration is universally correct.

## Tests

Required:

```text
same event repeated → one announcement
same payment with minor notification update → one announcement
two legitimate equal payments → two announcements
different providers, same amount → two announcements
same provider, different references → two announcements
```

## Approval Gate

Required.

---

# PHASE 09 — TTS ENGINE FOUNDATION

## Objective

Create a reliable speech abstraction and Android TTS implementation.

## Interface

Equivalent abstraction:

```kotlin
interface SpeechEngine {
    suspend fun speak(request: SpeechRequest)
    suspend fun stop()
    fun isAvailable(): Boolean
}
```

## Initialization

Must handle:

```text
engine unavailable
initialization failure
language unavailable
voice unavailable
service restart
```

## Languages

Initial target:

```text
English
Hindi
```

Additional languages are not promised until tested.

## User Settings

Support:

```text
language
voice
rate
pitch
volume
```

## Test Announcement

User must be able to manually trigger:

```text
Payment received. Fifty rupees.
```

or the approved configured equivalent.

## Offline Reality

The app must not claim that every TTS engine is fully offline.

The app can avoid network calls itself while an external TTS engine may depend on downloaded voice data.

## Approval Gate

Required.

---

# PHASE 10 — ANNOUNCEMENT QUEUE & AUDIO POLICY

## Objective

Make speech reliable when multiple payments arrive.

## Architecture

```text
PaymentEvent
    ↓
AnnouncementFormatter
    ↓
SpeechRequest
    ↓
FIFO Queue
    ↓
SpeechEngine
```

## Requirements

Exactly one logical speech consumer.

Multiple notification callbacks must not speak concurrently.

## Queue Policies

Define:

```text
FIFO
maximum queue size
duplicate cancellation
failure retry policy
interruption policy
```

## Audio Policy

Possible modes:

```text
Use current volume
Temporary boost
Fixed announcement volume
```

The final set must match approved UI/design.

## Volume Safety

When changing shared system volume:

1. capture relevant state,
2. make minimal change,
3. speak,
4. restore when safe,
5. account for concurrent audio state changes.

Never claim perfect restoration against every external application.

## Tests

Test:

```text
one payment
two rapid payments
five rapid payments
speech engine unavailable
Bluetooth output
phone speaker
volume changed during queue
service restarted during queue
```

## Approval Gate

Required.

---

# PHASE 11 — LOCAL PERSISTENCE & TRANSACTION HISTORY

## Objective

Add optional local history without violating privacy goals.

## Storage Strategy

DataStore:

```text
settings
preferences
provider enablement
```

Room:

```text
transaction history
announcement records
```

Only include Room if history is enabled in V1 scope.

## Transaction Model

Fields should be limited to what the feature needs:

```text
id
provider
amountMinor
currency
payerName
transactionReference
eventTime
confidence
announced
```

Raw notification content should not be stored by default.

## Retention

Provide a bounded retention policy.

Example:

```text
24 hours
7 days
30 days
Never
```

Exact defaults require approval.

## Delete

Provide:

```text
Delete one
Delete all
Automatic retention
```

## Migration

Every schema change must have a migration path.

## Tests

Verify:

- insert,
- query,
- update,
- delete,
- retention,
- migration,
- duplicate safety.

## Privacy Test

Verify that raw financial notification bodies are not accidentally persisted.

## Approval Gate

Required.

---

# PHASE 12 — SETTINGS & PROVIDER CONTROLS

## Objective

Expose safe configuration without allowing inconsistent internal state.

## Settings

Minimum:

```text
Announcement language
Voice
Speech rate
Pitch
Volume
Temporary volume boost
Enabled providers
Announce payer name
Announce provider name
Deduplication window
History retention
Theme
```

Only expose settings that are actually implemented.

## Validation

Settings must have:

- sane lower bound,
- sane upper bound,
- default value,
- persistence,
- migration handling if changed.

## Provider Controls

Display each provider status:

```text
Enabled
Disabled
Not verified
Unsupported on this device
```

Do not label a provider as “supported” unless the current build has verified support.

## Approval Gate

Required.

---

# PHASE 13 — DIAGNOSTICS & SELF-TEST SYSTEM

## Objective

Make failure visible instead of mysterious.

## Diagnostics State

Minimum:

```text
Notification Access
Listener Connection
Last Notification
Last Provider
Parser Result
Amount Present
Confidence
Deduplication Result
Last Speech
TTS State
Language State
Audio Route
Battery Restriction
```

## Sensitive Data Rule

Diagnostics must mask or omit sensitive values when possible.

Example:

```text
Provider: PHONEPE
Credit: YES
Amount present: YES
Amount: [hidden unless diagnostics mode explicitly enables it]
```

Exact diagnostic privacy policy must be approved.

## Self-Test

Create an end-to-end local test path that validates:

```text
Listener capability
Parser capability
TTS capability
Audio capability
Storage capability
```

without requiring a real monetary transaction when avoidable.

## ADB Validation

Capture:

```bash
adb logcat
adb shell dumpsys notification
```

and any relevant package/service state.

## Approval Gate

Required.

---

# PHASE 14 — END-TO-END INTEGRATION

## Objective

Connect every production subsystem.

## Canonical Flow

```text
UPI payment
    ↓
UPI provider notification
    ↓
Android notification framework
    ↓
NotificationListenerService
    ↓
RawNotification
    ↓
Normalizer
    ↓
ProviderParser
    ↓
Validation
    ↓
Amount Extraction
    ↓
Deduplication
    ↓
PaymentEvent
    ↓
History
    ↓
AnnouncementFormatter
    ↓
SpeechQueue
    ↓
TTS
    ↓
Speaker
```

## Test Scenarios

### Scenario A — Valid payment

Expected:

```text
one notification
one PaymentEvent
one database record
one announcement
```

### Scenario B — Duplicate notification

Expected:

```text
one PaymentEvent
one announcement
```

### Scenario C — Debit

Expected:

```text
no announcement
```

### Scenario D — OTP

Expected:

```text
no announcement
```

### Scenario E — TTS unavailable

Expected:

```text
payment can be recorded if history policy allows
speech failure visible in diagnostics
no crash
```

### Scenario F — Notification access disabled

Expected:

```text
service not considered READY
clear setup instruction
```

## Acceptance Criteria

The complete event chain must work on at least one verified device and one verified provider before multi-OEM expansion.

## Approval Gate

Required.

---

# PHASE 15 — DEVICE / OEM COMPATIBILITY

## Objective

Validate behavior across Android manufacturers and background-management environments.

## Target Matrix

Where devices are available, test across:

```text
Google Pixel
Samsung
Xiaomi / Redmi / POCO
OnePlus
Realme
OPPO
Vivo / iQOO
```

These are test targets, not guaranteed support promises.

## Test Categories

```text
notification delivery
listener connection
background survival
battery restrictions
TTS
audio routing
boot behavior
settings persistence
```

## OEM-Specific Rules

Never add manufacturer-specific code without evidence.

For each discovered OEM issue:

```text
Observed behavior
Device
OS
Evidence
Root cause hypothesis
Verification
Workaround
Regression test
```

## Battery Optimization

Do not instruct users to disable every battery feature by default.

Recommend only the minimum required device configuration based on verified behavior.

## Exit Criteria

A compatibility matrix must classify:

```text
PASS
PASS WITH CONFIGURATION
KNOWN LIMITATION
UNVERIFIED
FAIL
```

## Approval Gate

Required.

---

# PHASE 16 — RELIABILITY, BATTERY & LIFECYCLE HARDENING

## Objective

Turn a working prototype into a resilient long-running application.

## Areas

### Listener Lifecycle

Test:

```text
service connect
service disconnect
process restart
app force-stop
device reboot
notification access toggled
```

### TTS Lifecycle

Test:

```text
initialize
speak
stop
restart
language switch
voice switch
engine unavailable
```

### Storage Lifecycle

Test:

```text
app restart
database restart
migration
low-storage conditions where practical
```

### Battery

Verify:

- no polling loop,
- no unnecessary wake lock,
- no continuous CPU activity,
- no needless timers.

## Stress Tests

Generate repeated test events.

Measure:

```text
queue behavior
memory growth
CPU behavior
announcement duplication
crashes
stalls
```

## Stability Target

No known reproducible crash during the defined stress test profile.

Do not claim a universal crash-free guarantee.

## Approval Gate

Required.

---

# PHASE 17 — SECURITY & PRIVACY HARDENING

## Objective

Confirm the final implementation matches the privacy-first architecture.

## Permission Audit

Review AndroidManifest.

Remove any unnecessary permission.

Verify:

```text
notification listener
POST_NOTIFICATIONS only when needed
foreground-service permissions only when justified
```

No unnecessary:

```text
SMS
Contacts
Location
Phone
Microphone
Accessibility
Internet
```

unless separately approved and justified.

## Network Audit

The intended MVP has no application network dependency.

Verify:

- no unexpected HTTP client,
- no analytics SDK,
- no telemetry endpoint,
- no crash uploader,
- no remote configuration.

## Logging Audit

Search source for raw financial payload logging.

Reject patterns that log:

```text
full notification body
full payer identity
full UPI identifier
transaction reference unnecessarily
```

## Storage Audit

Check:

```text
shared preferences
DataStore
Room
cache
logs
```

for accidental sensitive persistence.

## Dependency Audit

Review:

- direct dependencies,
- transitive dependencies,
- licenses,
- permissions,
- network behavior.

## Threat Scenarios

Test or reason through:

```text
fake notification
malformed input
unexpected package
unexpected notification content
duplicate notification
extreme amount
parser confusion
log leakage
```

## Approval Gate

Required.

---

# PHASE 18 — REGRESSION & RELEASE CANDIDATE

## Objective

Freeze feature scope and perform complete regression testing.

## Regression Suite

All previous phase tests must run.

Minimum categories:

```text
build
install
launch
notification access
listener
normalizer
provider parsing
credit validation
amount parsing
deduplication
TTS
queue
audio
history
settings
diagnostics
privacy
OEM behavior
```

## Release Candidate Rules

No new feature may be added during RC except:

- critical bug fix,
- security fix,
- release-blocking compatibility fix.

Any scope change must be documented.

## Build Verification

Verify:

```text
debug build
release build
version code
version name
manifest
permissions
signing configuration
```

Do not claim Play Store readiness without performing the release-specific policy and signing checks required for the actual distribution channel.

## Approval Gate

Required.

---

# PHASE 19 — FINAL ACCEPTANCE & RELEASE

## Objective

Obtain explicit final approval for the first releasable build.

## Final Acceptance Checklist

### Core Behavior

```text
[ ] supported notification received
[ ] credit detected
[ ] amount extracted
[ ] duplicate suppressed correctly
[ ] announcement generated
[ ] TTS speaks
[ ] history works if enabled
```

### Failure Behavior

```text
[ ] debit ignored
[ ] OTP ignored
[ ] promotional notification ignored
[ ] unsupported provider ignored
[ ] missing amount does not speak
[ ] TTS failure does not crash
[ ] listener disconnect visible
```

### Privacy

```text
[ ] no unnecessary network dependency
[ ] no analytics
[ ] no telemetry
[ ] no sensitive debug logs
[ ] no unnecessary permissions
[ ] local history retention verified
```

### Reliability

```text
[ ] restart tested
[ ] listener reconnect tested
[ ] TTS restart tested
[ ] device reboot tested
[ ] settings persist
[ ] database migration tested
```

### ADB

```text
[ ] adb install
[ ] adb launch
[ ] adb logcat review
[ ] dumpsys notification review
[ ] relevant service/package state checked
```

## Final Approval

The build becomes:

```text
RELEASE CANDIDATE
```

only after all release-blocking checks pass.

Final status:

```text
APPROVED FOR RELEASE
```

requires explicit human approval.

---

# 20. BUG SEVERITY MODEL

## P0 — Release Blocking

Examples:

- false payment announcement with meaningful financial impact,
- crash on core payment notification,
- security/privacy leak,
- duplicate announcements causing severe user harm,
- data corruption.

Action:

```text
STOP THE PHASE
FIX IMMEDIATELY
RE-RUN REGRESSION
```

---

## P1 — Critical

Examples:

- supported provider consistently fails,
- TTS unavailable on target environment,
- listener disconnect cannot recover,
- transaction history corrupts.

Must be fixed before phase approval.

---

## P2 — Major

Examples:

- isolated parser failure,
- UI state inconsistency,
- non-critical OEM issue,
- recoverable queue problem.

May block release depending on scope.

---

## P3 — Minor

Examples:

- cosmetic issue,
- copy wording,
- non-blocking UI polish.

May be deferred if documented.

---

# 21. BUG FIX LOOP

Every significant bug follows:

```text
SYMPTOM
    ↓
OBSERVED BEHAVIOR
    ↓
REPRODUCTION
    ↓
EVIDENCE
    ↓
ROOT CAUSE HYPOTHESIS
    ↓
VERIFICATION
    ↓
MINIMAL FIX
    ↓
UNIT TEST
    ↓
ADB TEST
    ↓
REGRESSION
    ↓
DOCUMENT
```

Do not write:

```text
Fixed.
```

unless the bug has been verified after the fix.

---

# 22. AI AGENT PHASE EXECUTION RULES

The AI agent SHALL:

1. Read all governing documents before phase work.
2. Inspect the current codebase.
3. Identify the current phase.
4. Confirm the phase's scope.
5. Avoid implementing future-phase features.
6. State assumptions explicitly.
7. Verify uncertain technical claims.
8. Build after meaningful changes.
9. Test through ADB.
10. Inspect runtime logs.
11. Fix root causes instead of patching symptoms blindly.
12. Update documentation.
13. Update `memory.md`.
14. Report exact status.
15. Stop at the phase gate.

The AI agent MUST NOT:

- skip testing,
- silently advance phases,
- invent APIs,
- invent package IDs,
- invent provider notification formats,
- claim unsupported Android capabilities,
- claim payment settlement verification,
- claim a device is supported without testing,
- mark a phase approved without human approval.

---

# 23. PHASE REPORT TEMPLATE

At the end of every phase, create a report equivalent to:

```text
# Phase <N> Report

Status:
PASS / FAIL / BLOCKED / READY_FOR_REVIEW

Implemented:
- ...

Tests:
- Unit: PASS/FAIL
- Integration: PASS/FAIL
- ADB: PASS/FAIL
- Manual: PASS/FAIL

Device:
- ...

Known Issues:
- ...

Evidence:
- ...

Assumptions:
- ...

Regression:
- PASS/FAIL

Recommendation:
- APPROVE
- DO NOT APPROVE
```

Do not recommend approval if a release-blocking defect remains.

---

# 24. MEMORY UPDATE REQUIREMENT

After every approved phase, update `memory.md` with:

```text
Current phase
Completed phase
Major decisions
Tested devices
Known limitations
Open issues
Next phase
```

Never delete important historical decisions merely because the project moved forward.

---

# 25. ARCHITECTURE CHANGE CONTROL

A phase must stop and request architecture review if implementation requires:

- new external service,
- new major framework,
- new permission,
- network dependency,
- change in persistence model,
- change in notification input architecture,
- AccessibilityService,
- SMS ingestion,
- account system,
- cloud backend,
- major module restructuring.

The agent must not silently alter architecture.

---

# 26. PROVIDER PARSER CHANGE CONTROL

Any provider parser change must include:

```text
Provider
Version observed
Old behavior
New observed behavior
Parser change
Positive fixtures
Negative fixtures
Regression result
```

A provider parser must never be “fixed” from a guessed notification format.

---

# 27. TEST FIXTURE POLICY

Fixtures should be:

- deterministic,
- minimal,
- anonymized,
- reproducible,
- versioned.

Each fixture should specify:

```text
source provider
source package
observed app version
language
raw fields used
expected result
```

Use synthetic or sanitized data where possible.

---

# 28. PERFORMANCE VALIDATION

Measure where relevant:

```text
notification received → parser decision
accepted event → queue insertion
queue insertion → speech start
speech completion
database write time
```

Performance values are targets, not guarantees.

No performance claim may be documented without measurement.

---

# 29. BATTERY VALIDATION

The app must be event-driven.

Check for:

```text
polling loops
repeating timers
unnecessary wake locks
unbounded coroutines
busy waits
CPU spin
```

Observe behavior during idle periods.

The expected architecture is near-zero application activity between events.

---

# 30. ACCESSIBILITY VALIDATION

Check:

```text
content descriptions
touch target size
text scaling
contrast
screen reader semantics
keyboard navigation where applicable
```

A visual design is not considered complete if accessibility behavior is broken.

---

# 31. LOCALIZATION VALIDATION

At minimum:

```text
English
Hindi
```

Test:

- text overflow,
- speech wording,
- number pronunciation,
- currency pronunciation,
- missing language data,
- fallback behavior.

Never promise a language until tested on target TTS engine/device.

---

# 32. AUDIO ROUTING VALIDATION

Test:

```text
phone speaker
Bluetooth output
headset where available
media playback already active
notification sound active
Do Not Disturb variations where relevant
```

Document platform-dependent behavior.

Do not claim that the application can override every Android audio policy.

---

# 33. REBOOT VALIDATION

Where supported by the execution architecture:

1. boot device,
2. verify listener access,
3. verify listener connection,
4. verify TTS readiness,
5. verify test announcement,
6. verify settings persistence.

Record any OEM-specific setup requirement.

---

# 34. UPDATE VALIDATION

Before a release is accepted:

```text
Install previous version
Create settings/history
Install update
Verify migration
Verify settings
Verify history
Verify listener
Verify TTS
```

No update should silently erase user configuration without documented reason.

---

# 35. UNINSTALL / REINSTALL VALIDATION

Verify expected behavior for:

```text
uninstall
reinstall
notification access
settings reset/persistence expectations
local history
```

Document what the platform naturally clears.

Do not promise data restoration without a backup mechanism.

---

# 36. OFFLINE VALIDATION

Disable network connectivity at the device level.

Core path must still work:

```text
notification
→ parser
→ validation
→ dedupe
→ TTS
```

provided the required local TTS voice/engine is available.

The app must not require a backend.

---

# 37. FALSE POSITIVE SAFETY GATE

This is a release-critical area.

The system must be tested against:

```text
payment offer
cashback
request money
send money
debit
account balance
OTP
UPI security alert
promotional notification
bank alert
merchant reminder
```

No false-positive payment announcement should be accepted casually.

A false positive can be more damaging than a missed announcement.

---

# 38. FALSE NEGATIVE POLICY

Some legitimate payment notifications may not be parseable.

When information is insufficient:

```text
DO NOT GUESS
DO NOT SPEAK A PAYMENT
```

Instead:

```text
mark unparsed
record diagnostic state if enabled
add fixture after observation
improve provider parser
regression test
```

---

# 39. TESTING PRIORITY

When test time is limited, prioritize:

```text
1. False-positive prevention
2. Correct amount extraction
3. Duplicate prevention
4. TTS reliability
5. Listener reliability
6. History correctness
7. UI polish
```

Financially misleading announcements are higher priority than cosmetic issues.

---

# 40. RELEASE BLOCKERS

The release must be blocked by:

```text
critical false positives
unverified payment logic
critical notification parsing errors
sensitive data leakage
unexpected network activity
crashes in core flow
irrecoverable TTS failure
data corruption
missing regression coverage for fixed critical bugs
```

---

# 41. PHASE HANDOFF RULE

The output of one phase is the verified input to the next.

Example:

```text
PHASE 03
RawNotification verified
      ↓
PHASE 04
Normalization assumes verified raw model
      ↓
PHASE 05
Parser assumes verified normalized input
```

Do not build downstream features around unverified upstream behavior.

---

# 42. SCOPE CONTROL

Each phase has an explicit scope.

The AI must not use phrases such as:

```text
I also improved...
I redesigned...
I migrated...
I rewrote...
```

for unrelated areas unless the change is explicitly justified.

Large unrelated changes increase regression risk.

---

# 43. EMERGENCY STOP CONDITIONS

Stop implementation immediately if:

- platform behavior contradicts architecture,
- payment data exposure is discovered,
- an unknown permission becomes necessary,
- a dependency introduces unacceptable risk,
- a parser requires guessing,
- notification payload is inaccessible on the target Android version,
- a critical false-positive is observed,
- a core assumption is proven false.

Mark:

```text
BLOCKED
```

Document why.

Do not “work around” a fundamental blocker with undocumented behavior.

---

# 44. DOCUMENTATION SYNCHRONIZATION

Whenever implementation changes architecture:

Update, as applicable:

```text
architecture.md
rules.md
phases.md
design.md
memory.md
```

Do not leave contradictory documentation.

The actual verified system is the source of truth.

---

# 45. FINAL PROJECT STATE MODEL

The project has these major milestones:

```text
RESEARCHED
    ↓
FOUNDATION_READY
    ↓
NOTIFICATION_READY
    ↓
PARSING_READY
    ↓
TTS_READY
    ↓
END_TO_END_READY
    ↓
COMPATIBILITY_VALIDATED
    ↓
HARDENED
    ↓
RELEASE_CANDIDATE
    ↓
APPROVED_FOR_RELEASE
```

---

# 46. PHASE SUMMARY TABLE

| Phase | Name | Main Output | Mandatory Test |
|---|---|---|---|
| 00 | Research | Verified baseline | ADB/environment |
| 01 | Foundation | Buildable app | Build + install |
| 02 | UI | Approved app shell | UI + ADB |
| 03 | Listener | Raw notifications | dumpsys + logcat |
| 04 | Normalization | Canonical event input | unit tests |
| 05 | Parsers | Provider adapters | fixtures |
| 06 | Validation | Credit-only events | positive + negative |
| 07 | Amount | Reliable money parsing | amount matrix |
| 08 | Deduplication | duplicate-safe events | concurrency tests |
| 09 | TTS | Speech engine | device audio |
| 10 | Queue | ordered announcements | burst tests |
| 11 | Storage | optional local history | DB tests |
| 12 | Settings | configurable behavior | persistence tests |
| 13 | Diagnostics | health visibility | self-test + ADB |
| 14 | Integration | complete pipeline | E2E device test |
| 15 | OEM | compatibility matrix | multi-device |
| 16 | Hardening | reliability | stress/restart |
| 17 | Security | privacy baseline | audit |
| 18 | RC | frozen candidate | full regression |
| 19 | Release | approved build | final acceptance |

---

# 47. STRICT COMPLETION STATEMENT

A phase may use this exact completion language:

```text
PHASE <N> READY FOR REVIEW

Implementation: COMPLETE
Automated Tests: PASS
ADB Verification: PASS
Manual Verification: PASS
Known Issues: <NONE / LIST>
Documentation: UPDATED
Regression: PASS
Approval: PENDING HUMAN REVIEW
```

Only after explicit human approval:

```text
PHASE <N> APPROVED
```

Only then may:

```text
PHASE <N+1>
```

begin.

---

# 48. CORE PHILOSOPHY

The project follows one central rule:

> Never guess when the system can be measured.

For this application specifically:

```text
Do not guess notification content.
Do not guess provider behavior.
Do not guess Android behavior.
Do not guess package IDs.
Do not guess permissions.
Do not guess TTS capabilities.
Do not guess that a notification means settlement.
Do not guess that a bug is fixed.
Do not guess that a device is supported.
```

Instead:

```text
Observe
Capture evidence
Model
Implement
Test
Verify
Document
Approve
Proceed
```

---

# 49. FINAL GATE

The project is not considered production-ready merely because:

```text
the APK builds,
the UI looks good,
one payment worked,
or an AI agent claims success.
```

Production readiness requires:

```text
Verified notification pipeline
+
Verified parser behavior
+
False-positive protection
+
Reliable TTS
+
Duplicate protection
+
Lifecycle handling
+
Privacy audit
+
ADB verification
+
Regression coverage
+
Device validation
+
Explicit human approval
```

That is the project execution contract.

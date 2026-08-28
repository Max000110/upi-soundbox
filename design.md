# UPI Voice Soundbox — `design.md`

**Document Type:** Product UI/UX Design System & Screen Specification  
**Project:** UPI Voice Soundbox — Android local-first application  
**Version:** 1.0  
**Status:** Mandatory design baseline  
**Audience:** Product designer, Android developer, AI coding agent, UI engineer, QA, reviewer  
**Design Source:** Human-approved design direction + Google Stitch MCP for exploration and screen generation  
**Implementation Target:** Native Android / Jetpack Compose  
**Visual Direction:** Professional, restrained, trustworthy, utilitarian, merchant-focused  
**Primary Theme:** Light-first  
**Dark Mode:** Supported only if implemented consistently; not the primary visual identity  
**Core Principle:** The application must look like a serious financial utility, not a gaming dashboard, crypto app, neon-tech app, or AI concept UI.

---

# 1. DESIGN OBJECTIVE

The purpose of this document is to define the complete visual and interaction language for UPI Voice Soundbox.

The application is a utility that a user may leave running for long periods while receiving payments.

Therefore the interface must prioritize:

- immediate comprehension,
- trust,
- readability,
- predictable interaction,
- low visual noise,
- accessibility,
- large touch targets,
- clear system status,
- obvious configuration,
- restrained use of color,
- fast recovery when something is not configured.

The UI must feel closer to a professionally designed banking / merchant utility than to a consumer social application.

---

# 2. DESIGN PERSONALITY

The intended visual personality is:

```text
Professional
Calm
Trustworthy
Clean
Operational
Precise
Minimal
Mature
Accessible
Reliable
```

Avoid:

```text
Neon
Cyberpunk
Gaming UI
Glassmorphism-heavy surfaces
Excessive gradients
Glow effects
Excessive shadows
Futuristic HUDs
Crypto-dashboard aesthetics
Over-animated cards
Oversaturated colors
Decorative background graphics
```

The product should look credible even when viewed as a screenshot without context.

---

# 3. CORE DESIGN PRINCIPLES

## DP-01 — Function Before Decoration

Every visual element must communicate:

- status,
- action,
- information,
- hierarchy,
- or feedback.

Remove decorative UI that does not improve comprehension.

---

## DP-02 — Calm Financial Utility

Color should communicate meaning rather than personality.

Green:

```text
success / healthy / ready
```

Amber:

```text
warning / attention
```

Red:

```text
error / blocked
```

Neutral colors:

```text
layout / hierarchy / secondary information
```

The interface must not make every element colorful.

---

## DP-03 — Light First

The primary experience is a clean light interface.

Use:

```text
warm white / neutral white surfaces
dark charcoal text
muted gray secondary text
restrained green success
restrained amber warning
restrained red error
one subtle brand accent
```

The application must NOT use electric blue or neon blue as the dominant brand color.

---

## DP-04 — Information Density Without Clutter

The home screen is operational.

It should tell the user:

```text
Is the soundbox ready?
Is notification access working?
Is TTS ready?
What happened last?
Can I test the speaker?
```

It should not overload the user with technical diagnostics unless they open Diagnostics.

---

## DP-05 — Hierarchy Must Be Obvious

A visual hierarchy should generally follow:

```text
Screen title
↓
Primary status / primary action
↓
Important information
↓
Secondary controls
↓
Supporting information
```

---

# 4. BRAND / VISUAL DIRECTION

## 4.1 Brand Character

The brand should communicate:

```text
Reliable
Local
Private
Merchant-friendly
Professional
Simple
```

Do not make the brand appear:

```text
AI-centric
hacker-centric
crypto-centric
gaming-centric
```

---

## 4.2 Primary Accent

Recommended restrained accent family:

```text
Deep Green / Evergreen
```

Example reference:

```text
#176B57
```

This is a design reference, not a hard implementation requirement.

The exact final token may be refined through Stitch exploration and contrast testing.

---

## 4.3 Success Color

Use a restrained financial green:

```text
#23845A
```

Use primarily for:

- connected state,
- successful payment,
- ready status,
- positive confirmation.

Do not flood the entire screen with green.

---

## 4.4 Warning Color

Use a warm amber:

```text
#A96800
```

Use for:

- action required,
- battery optimization warning,
- incomplete setup,
- degraded state.

---

## 4.5 Error Color

Use a mature red:

```text
#B42318
```

Use for:

- blocking failure,
- permission failure,
- TTS failure,
- parser failure requiring attention.

Avoid bright fluorescent red.

---

## 4.6 Neutral Palette

Recommended:

```text
Text Primary:       #1C1C1B
Text Secondary:     #5F625F
Text Tertiary:      #777A77
Surface:            #FFFFFF
Surface Secondary:  #F7F7F4
Surface Tertiary:   #F1F2EE
Border:             #D9DBD5
Divider:            #E6E7E2
```

These are design reference tokens.

They must be checked for WCAG/Android accessibility requirements before implementation.

---

# 5. COLOR RULES

## 5.1 Color Quantity

Recommended visual ratio:

```text
70–80% neutral surfaces
15–20% typography / structural neutral
5–10% semantic/accent color
```

Do not attempt to make the whole interface visually “exciting”.

The product should feel stable.

---

## 5.2 No Neon Rule

Do not use:

```text
neon cyan
electric blue
purple-blue gradients
glowing green
glowing blue
outer glow
light-beam effects
```

The application is a financial utility.

---

## 5.3 Gradient Rule

Gradients are prohibited by default.

Allowed only when:

- there is a clear approved design reason,
- it does not reduce readability,
- it does not make the product look promotional.

---

# 6. TYPOGRAPHY

## 6.1 Primary Font

Use the Android system typography / Roboto family unless another approved typeface is introduced.

Do not bundle a font only for aesthetic reasons.

---

## 6.2 Type Scale

Suggested hierarchy:

```text
Display:
32sp / bold

Screen Title:
26sp / bold

Section Title:
20sp / semibold

Card Title:
17sp / semibold

Body:
16sp / regular

Secondary:
14sp / regular

Caption:
12sp / medium
```

Exact implementation values may change to match Material 3 conventions and accessibility scaling.

---

## 6.3 Financial Amount Typography

Amounts are important.

Example:

```text
₹500
```

should visually stand out more than:

```text
Received
```

Recommended:

```text
28–36sp
semibold or bold
```

Do not use extremely huge amounts that make the dashboard look like an advertisement.

---

# 7. SPACING SYSTEM

Use a consistent base spacing scale.

Recommended:

```text
4dp
8dp
12dp
16dp
20dp
24dp
32dp
40dp
48dp
```

Default screen horizontal padding:

```text
20dp
```

Primary card internal padding:

```text
16dp
```

Section spacing:

```text
24dp
```

Use consistent spacing instead of manually tuned arbitrary values.

---

# 8. CORNER RADIUS

The visual language should use moderate, professional rounding.

Recommended:

```text
Small controls:      8dp
Buttons:             10–12dp
Cards:               14–16dp
Large containers:    18dp
```

Avoid:

```text
pill-everything
extreme 32dp+ rounding
```

Pills are reserved for:

- status chips,
- compact filters,
- tags.

---

# 9. ELEVATION & SHADOWS

Default surfaces should appear mostly flat.

Use:

```text
subtle elevation
```

rather than strong drop shadows.

Avoid:

```text
large blurred shadows
floating neon cards
multiple nested elevations
```

Cards should feel part of the app surface rather than floating over it.

---

# 10. ICONOGRAPHY

Use one coherent icon family.

Preferred:

```text
Material Symbols / Material Icons
```

Rules:

- same stroke language,
- consistent icon size,
- consistent visual weight,
- no mixing random icon packs.

Standard sizes:

```text
20dp
24dp
28dp
32dp
```

Use 24dp for most standard actions.

---

# 11. NAVIGATION MODEL

Recommended primary navigation:

```text
Home
History
Settings
```

Secondary screens:

```text
Providers
Voice
Diagnostics
Privacy
About
```

Do not overload the bottom navigation with every technical feature.

---

# 12. HOME SCREEN

## 12.1 Purpose

The Home screen answers one question immediately:

> “Is my soundbox ready and what happened recently?”

---

## 12.2 Structure

```text
┌───────────────────────────────────────┐
│ UPI Voice Soundbox                    │
│                                       │
│ Ready                                 │
│ ● Listening for payments              │
│                                       │
│ ┌───────────────────────────────────┐ │
│ │ Today's collection                │ │
│ │ ₹12,450                           │ │
│ │ 28 payments                       │ │
│ └───────────────────────────────────┘ │
│                                       │
│ Last payment                          │
│                                       │
│ ┌───────────────────────────────────┐ │
│ │ ₹500                              │ │
│ │ Received from Rahul              │ │
│ │ PhonePe · 10:42 AM               │ │
│ └───────────────────────────────────┘ │
│                                       │
│ [ Test Announcement ]                 │
│                                       │
│ Setup status                          │
│ Notification Access             ✓     │
│ Voice Engine                     ✓     │
└───────────────────────────────────────┘
```

The exact information set may depend on whether history and collection totals are included in the approved V1 scope.

---

# 13. HOME STATUS COMPONENT

The primary status component is highly important.

States:

```text
READY
ATTENTION
BLOCKED
DISCONNECTED
UNKNOWN
```

---

## READY

Visual:

```text
small green status indicator
dark text
subtle green supporting surface
```

Copy:

```text
Ready
Listening for supported payment notifications
```

Avoid:

```text
LIVE!!!
ACTIVE!!!
PAYMENT RADAR!!!
```

---

## ATTENTION

Example:

```text
Notification access is enabled, but TTS needs setup.
```

Use amber.

---

## BLOCKED

Example:

```text
Notification access is off.
```

Provide direct action:

```text
Open settings
```

---

# 14. PAYMENT SUMMARY CARD

The payment summary should emphasize clarity.

Example:

```text
Today's collection

₹12,450

28 payments
```

Use grouping:

```text
primary value
secondary metadata
```

Avoid:

```text
huge chart dashboards
```

unless analytics become an approved future feature.

---

# 15. LAST PAYMENT CARD

The most recent accepted transaction may be displayed.

Hierarchy:

```text
Amount
↓
Payer (if available)
↓
Provider
↓
Time
```

Example:

```text
₹500
Received from Rahul
PhonePe · 10:42 AM
```

If payer is unavailable:

```text
₹500
Payment received
PhonePe · 10:42 AM
```

Never fabricate payer information.

---

# 16. TEST ANNOUNCEMENT BUTTON

This is a primary diagnostic convenience feature.

Button:

```text
Test Announcement
```

Behavior:

```text
Tap
↓
Generate local test message
↓
Queue speech
↓
Show result
```

Do not require an actual payment to test TTS.

---

# 17. PROVIDERS SCREEN

## Purpose

Allow the user to control which provider sources are processed.

Structure:

```text
Providers

Payment apps
────────────────────────

PhonePe                    ON
Google Pay                 ON
Paytm                      ON
BHIM                       ON
Amazon Pay                 OFF
CRED                       OFF
```

Each row may show:

```text
provider icon
provider name
support status
toggle
```

Do not use unofficial logos or assets without checking licensing/usage requirements.

---

# 18. PROVIDER STATE DESIGN

Possible states:

```text
Enabled
Disabled
Verified
Unverified
Unsupported
Needs configuration
```

Use semantic colors sparingly.

Example:

```text
Verified        neutral/green indicator
Needs setup     amber
Unsupported     gray
Error           red
```

---

# 19. VOICE SETTINGS SCREEN

Sections:

```text
Speech
Voice
Audio
Preview
```

Controls:

```text
Language
Voice
Speech rate
Pitch
Announcement volume
Temporary boost
```

Every adjustable control should display its current value.

---

# 20. VOICE LANGUAGE

Example:

```text
Language

○ English
○ Hindi
```

Do not display languages that cannot actually be provided by the active TTS configuration.

If unavailable:

```text
Hindi voice unavailable on this device.
```

Provide the platform-appropriate setup action where possible.

---

# 21. SPEECH RATE

Use a slider.

Example:

```text
Slower ─────────────●──── Faster
```

Display:

```text
Normal
```

or an appropriate numeric/semantic value.

Avoid tiny precision controls that are difficult to use.

---

# 22. TTS PREVIEW

Provide:

```text
[ Play Sample ]
```

Sample:

```text
Payment received. Five hundred rupees.
```

The sample must use the same actual engine and settings that production announcements use.

---

# 23. AUDIO SETTINGS

Possible options:

```text
Announcement volume
Use current media volume
Temporary volume boost
```

Explain system-volume behavior in plain language.

Avoid technical jargon such as:

```text
STREAM_MUSIC
AudioFocus
AudioManager
```

in the consumer-facing UI.

Those belong in diagnostics/developer documentation.

---

# 24. HISTORY SCREEN

## Purpose

Allow quick review of recent locally stored payment events.

Layout:

```text
History

Today

10:42 AM
₹500
Rahul · PhonePe

10:17 AM
₹100
Payment received · Google Pay

Yesterday

...
```

Use chronological order.

---

# 25. HISTORY FILTERS

Optional filters:

```text
All
PhonePe
Google Pay
Paytm
BHIM
```

Additional filters should only be introduced when they provide real value.

Do not turn a simple transaction list into an enterprise analytics dashboard.

---

# 26. HISTORY PRIVACY

History is sensitive.

The UI must include a clear action:

```text
Delete history
```

Potential confirmation:

```text
Delete all payment history?

This removes locally stored transaction records from this device.
```

Avoid alarming copy.

---

# 27. DIAGNOSTICS SCREEN

The Diagnostics screen is for troubleshooting.

It may be technical.

Structure:

```text
System status

Notification Access       Connected
Listener                  Connected
TTS Engine                Ready
Voice                     English
Audio                     Phone speaker
Battery restriction       None detected

Last notification
Provider                  PhonePe
Parser                    Credit
Amount                    Detected
Announcement              Played
```

Sensitive fields should be masked where practical.

---

# 28. DIAGNOSTIC VISUAL LANGUAGE

Diagnostics should not visually resemble the main dashboard.

Use:

```text
compact rows
status indicators
technical values
monospace only when useful
```

No neon console aesthetic.

Do not create a fake “terminal” UI.

---

# 29. SETTINGS SCREEN

Group settings logically.

```text
General

Appearance
Language
Notifications

Payment apps

Voice & Audio

Voice
Speech rate
Volume
Boost

Privacy

History retention
Delete history

Diagnostics

System status
Troubleshooting
```

Avoid one giant unstructured list.

---

# 30. PRIVACY SCREEN

The privacy screen should explain the architecture honestly.

Example:

```text
Your payment notifications are processed on this device.

The app does not need a cloud account for its core function.

No payment data is intentionally uploaded by the app.
```

Important distinction:

The app reads supported notifications. It does not directly verify bank settlement.

Do not make misleading claims.

---

# 31. ONBOARDING

Onboarding should be short.

Recommended flow:

```text
Welcome
  ↓
Notification access
  ↓
Test voice
  ↓
Choose provider apps
  ↓
Finish
```

Do not create a 10-page onboarding carousel.

---

# 32. SETUP CHECKLIST

A setup card can show:

```text
Setup

✓ App installed
✓ Notification access
✓ TTS ready
○ Provider enabled
○ Test announcement
```

This is more useful than decorative onboarding.

---

# 33. EMPTY STATES

Every list/screen with no data must have a deliberate empty state.

Example History:

```text
No payments yet

When a supported payment notification is detected,
it will appear here.
```

Do not display:

```text
Nothing 😢
```

or playful language.

---

# 34. ERROR STATES

Errors should explain:

```text
What happened
Why it matters
What the user can do
```

Example:

```text
Voice is unavailable

The selected TTS engine cannot provide the chosen language.

[ Check Voice Settings ]
```

Avoid raw stack traces.

---

# 35. SYSTEM STATUS COMPONENTS

Use one reusable status component.

Possible variants:

```text
StatusRow
StatusBadge
ReadinessCard
ErrorCard
ActionCard
```

All statuses should share the same visual semantics.

---

# 36. BUTTON SYSTEM

## Primary

For the most important action:

```text
Filled button
```

Examples:

```text
Test Announcement
Open Notification Settings
```

---

## Secondary

Use:

```text
Outlined button
```

Examples:

```text
Configure voice
View diagnostics
```

---

## Tertiary

Use:

```text
Text button
```

For:

```text
Learn more
View details
```

---

## Destructive

Use restrained red styling.

Example:

```text
Delete all history
```

Do not make destructive actions visually ambiguous.

---

# 37. SWITCHES

Use switches for binary configuration:

```text
PhonePe
Announcements
Payer name
Provider name
```

Do not use switches when an action should execute immediately.

---

# 38. DIALOGS

Dialogs should be used for:

- destructive confirmation,
- important configuration decisions,
- explicit test actions.

Avoid dialogs for routine navigation.

---

# 39. BOTTOM SHEETS

Use sparingly.

Suitable for:

```text
voice selection
provider selection
filter selection
```

Do not use bottom sheets simply because they look modern.

---

# 40. TOAST / SNACKBAR

Use Snackbars for lightweight feedback.

Examples:

```text
Settings saved
History deleted
Test announcement queued
```

Do not display financial transaction amounts in transient UI if they are not necessary.

---

# 41. MOTION DESIGN

Motion should communicate state changes.

Use:

```text
fade
small slide
content transition
progress
```

Avoid:

```text
bounce
elastic transitions
glow
particle effects
screen explosions
```

---

# 42. ANIMATION DURATIONS

Suggested:

```text
micro interaction: 100–150ms
standard transition: 180–250ms
larger transition: 250–350ms
```

Animations must never delay critical actions.

---

# 43. PAYMENT ARRIVAL UX

When a valid payment event is detected while the app is open:

```text
Payment accepted
↓
Subtle visual confirmation
↓
Amount displayed
↓
TTS announcement
```

Do not display excessive animation.

The sound is the primary real-time feedback channel.

---

# 44. REAL-TIME PAYMENT CARD

Optional live event component:

```text
Payment received

₹500

Rahul
PhonePe

Just now
```

Visual priority:

```text
Amount > status > payer/provider > timestamp
```

---

# 45. ACCESSIBILITY

The application must support Android accessibility.

Minimum:

```text
screen reader labels
semantic buttons
sufficient contrast
large touch targets
text scaling
content descriptions where appropriate
```

Target touch size:

```text
48dp minimum where practical
```

Do not encode meaning using color alone.

Example:

Bad:

```text
green = ready
red = failure
```

Better:

```text
✓ Ready
! Attention
× Blocked
```

with color as reinforcement.

---

# 46. TEXT SCALING

The UI must remain usable when Android font scaling increases.

Test at enlarged accessibility font sizes.

Avoid:

- fixed-height text containers,
- clipped labels,
- buttons whose text becomes unreadable,
- cards that collapse.

---

# 47. SCREEN READER ORDER

Semantic order should follow visual hierarchy:

```text
Screen title
Status
Primary action
Primary information
Secondary actions
Supporting details
```

Avoid confusing navigation caused by nested semantics.

---

# 48. ONE-HAND USABILITY

Common actions should be reachable without precision taps.

Important actions:

```text
Test Announcement
Open Settings
Enable provider
```

should not require tiny icon-only buttons.

---

# 49. LANDSCAPE / LARGE SCREEN

The primary target is phone portrait.

However, layouts should degrade gracefully on:

- larger phones,
- tablets,
- landscape where supported.

Avoid extreme stretched layouts.

---

# 50. DARK MODE

Dark mode is optional after the light theme is stable.

If implemented:

Do not simply invert colors.

Create deliberate dark tokens.

Do NOT introduce neon blue/cyan accents just because the background becomes dark.

Dark theme should remain:

```text
professional
low-glare
high-contrast
restrained
```

---

# 51. COMPONENT LIBRARY

Create reusable Compose components.

Suggested:

```text
AppScaffold
ScreenHeader
StatusCard
StatusRow
PrimaryActionButton
SecondaryActionButton
SectionHeader
SettingRow
SettingSwitchRow
SettingSliderRow
ProviderRow
PaymentCard
EmptyState
ErrorState
DiagnosticRow
ConfirmationDialog
```

Do not duplicate near-identical UI code.

---

# 52. DESIGN TOKENS

Centralize design values.

Example conceptual model:

```kotlin
object AppDimens
object AppColors
object AppTypography
object AppShapes
```

Use theme-based tokens where practical.

Do not scatter raw constants throughout composables.

---

# 53. COMPONENT STATES

Every reusable component must define relevant states.

Example button:

```text
enabled
disabled
loading
pressed
```

Provider row:

```text
enabled
disabled
unsupported
loading
error
```

Status card:

```text
ready
warning
error
unknown
```

---

# 54. LOADING STATES

Use subtle loading indicators.

Do not display fake progress.

Bad:

```text
Processing...
```

when nothing is actually asynchronous.

Use loading only when an operation is genuinely in progress.

---

# 55. ERROR RECOVERY UX

Every recoverable error should provide a path forward.

Example:

```text
Notification access is required

Enable notification access to detect supported payment notifications.

[ Open Settings ]
```

Do not merely show:

```text
Error 403
```

---

# 56. PERMISSION EDUCATION

Before navigating to sensitive system settings, explain why.

Example:

```text
Notification Access

This permission lets the app read supported payment notifications
so it can announce them aloud.

The app does not use this access to make payments.
```

The exact wording must remain truthful to implementation and platform behavior.

---

# 57. TRUST DESIGN

The UI should repeatedly reinforce accurate boundaries:

```text
Reads supported notifications
Processes locally
Speaks accepted events
```

Do not claim:

```text
Bank verified
Bank confirmed
100% guaranteed
Impossible to miss
```

unless technically and empirically true.

---

# 58. PRIVACY VISUAL LANGUAGE

Avoid excessive “privacy marketing”.

A professional utility should communicate privacy simply.

Use:

```text
Processed on this device
No account required for core functionality
Local history
```

when actually true.

---

# 59. MICROCOPY

Preferred writing style:

```text
Direct
Neutral
Professional
Human
Short
```

Use:

```text
Notification access is off.
```

not:

```text
Oops! Something went wrong 😢
```

---

# 60. PRODUCT TERMINOLOGY

Use consistent terms.

Preferred:

```text
Payment notification
Payment received
Provider
Announcement
Voice
TTS
Notification access
History
Diagnostics
```

Avoid inconsistent variants such as:

```text
money alert
cash alert
UPI radar
payment detector
sound AI
```

unless branding requires it.

---

# 61. PAYMENT COPY RULES

The app should say:

```text
Payment received.
```

when the event has passed the application's validation rules.

The app must not say:

```text
Bank confirmed payment.
```

unless a future verified bank/API integration actually supports that claim.

---

# 62. NUMBER / CURRENCY PRESENTATION

Visual currency:

```text
₹500
₹1,250
₹1,000.50
```

Voice generation may use natural language.

Examples:

```text
Five hundred rupees
One thousand two hundred fifty rupees
```

The formatter must use deterministic locale-aware rules.

---

# 63. RESPONSIVE LAYOUT RULES

Avoid hardcoded screen sizes.

Use:

```text
responsive width
maximum content width
adaptive padding
FlowRow / Lazy layouts where appropriate
```

The home content should remain centered and readable on larger screens.

---

# 64. SCREEN WIDTH TARGETS

Conceptual breakpoints:

```text
Compact phone
Medium phone
Expanded/tablet
```

Do not design only against one screenshot width.

---

# 65. HOME SCREEN VISUAL PRIORITY

The most important things should be visible without scrolling:

```text
Ready / problem state
Last payment
Test announcement
```

Optional collection statistics may follow depending on V1 scope.

---

# 66. HISTORY VISUAL PRIORITY

Each row:

```text
Amount
Provider / payer
Time
```

Avoid showing internal confidence scores in normal user-facing history.

---

# 67. DIAGNOSTICS VS NORMAL UI

Never expose developer-oriented implementation details in the standard home experience.

Normal user:

```text
Voice ready
```

Diagnostics:

```text
TTS engine initialized
Language status: AVAILABLE
```

This separation keeps the product professional.

---

# 68. GOOGLE STITCH MCP WORKFLOW

Google Stitch MCP is the preferred visual exploration tool for this project.

The design agent should use Stitch for:

```text
screen exploration
layout alternatives
component exploration
design system exploration
visual refinement
```

Workflow:

```text
Requirements
↓
Stitch exploration
↓
Human review
↓
Approved visual direction
↓
design.md alignment
↓
Compose implementation
↓
Android device validation
```

---

# 69. STITCH CONSTRAINTS

Stitch output is not automatically production-ready.

Every generated screen must be checked for:

```text
Android compatibility
Compose implementation feasibility
accessibility
text scaling
touch targets
navigation
state handling
offline behavior
privacy implications
```

The agent must not copy decorative or web-specific patterns blindly.

---

# 70. STITCH NO-HALLUCINATION RULE

The design agent must never claim:

```text
Stitch generated a feature
```

unless it actually generated or exposed that design artifact.

Never invent:

```text
missing screens
component names
tokens
interactions
```

from memory.

---

# 71. DESIGN APPROVAL FLOW

```text
Design Requirement
↓
Stitch Exploration
↓
Variant Review
↓
Human Approval
↓
Design Freeze
↓
Compose Implementation
↓
Device Review
↓
Visual QA
```

After design freeze, major visual changes require explicit approval.

---

# 72. VISUAL QA

Every implemented screen must be checked for:

```text
spacing
alignment
typography
color
icon size
button size
state behavior
text overflow
accessibility
light mode
dark mode if implemented
```

Compare implementation against the approved design.

---

# 73. DEVICE SCREENSHOT QA

Capture screenshots using Android tooling during validation.

Compare:

```text
design
vs
actual device
```

Do not approve based only on emulator rendering if the target is a real phone.

---

# 74. DESIGN REGRESSION

Every significant visual bug should result in:

```text
bug record
design correction
component correction
visual regression check
```

Do not repeatedly patch individual screens when the issue comes from a shared component.

---

# 75. DESIGN SYSTEM GOVERNANCE

Changes to:

```text
colors
typography
spacing
shape
navigation
primary components
```

must be treated as design-system changes.

Do not change individual screens independently in ways that create visual drift.

---

# 76. ANTI-PATTERN CATALOG

Never introduce:

```text
neon blue backgrounds
cyan glowing borders
purple gradient cards
dark cyberpunk dashboard
glass cards everywhere
random emoji icons
giant hero illustrations
fake 3D payment animations
excessive confetti
crypto-style charts
```

Also avoid:

```text
huge empty hero areas
tiny text
tiny buttons
low contrast gray text
excessive rounded pills
unnecessary bottom navigation
```

---

# 77. PROFESSIONALITY TEST

A screen passes the professionality test when:

1. It looks credible without decoration.
2. The primary action is obvious.
3. The status is immediately understandable.
4. The content hierarchy is clear.
5. It works with real data.
6. It remains usable with accessibility text scaling.
7. It does not rely on color alone.
8. It does not resemble a gaming/crypto interface.

---

# 78. PERFORMANCE-AWARE UI

Avoid unnecessarily expensive visuals:

```text
blur
large shadows
complex animations
continuous canvas effects
heavy image backgrounds
```

The application is expected to run for long periods.

UI performance must not compromise the soundbox's background reliability.

---

# 79. OFFLINE UI

All core screens must render without network access.

Do not design:

```text
loading dashboard
waiting for cloud configuration
remote profile dependency
```

for the core experience.

---

# 80. PRIVACY-AWARE SCREEN CAPTURE

Diagnostics and history screens may contain sensitive data.

Do not add automatic screenshot/export/share controls for financial data unless explicitly required.

---

# 81. DELETE CONFIRMATION UX

For destructive actions:

```text
Delete all history?

This action cannot be undone.

[Cancel] [Delete]
```

Use destructive color only on the final action.

---

# 82. FIRST-RUN EXPERIENCE

First launch should detect readiness.

If configuration is incomplete:

```text
Setup needed

Notification access
Voice
Providers

[Complete setup]
```

Do not immediately throw the user into a settings maze.

---

# 83. RETURNING USER EXPERIENCE

After setup:

```text
Ready
Listening for supported payment notifications
```

Do not show the onboarding flow repeatedly unless configuration becomes invalid.

---

# 84. SERVICE HEALTH COMMUNICATION

A background service should have a user-visible health representation without implying impossible guarantees.

Good:

```text
Listening
```

Better when diagnostics are available:

```text
Listener connected
```

Avoid:

```text
Always active forever
Cannot be stopped
100% guaranteed
```

---

# 85. DESIGN TOKEN IMPLEMENTATION GUIDANCE

Recommended architecture:

```text
Theme
 ├── ColorScheme
 ├── Typography
 ├── Shapes
 ├── Dimensions
 └── Component styles
```

Do not create one-off colors inside individual composables.

Bad:

```kotlin
Color(0xFF00FFFF)
```

inside random screens.

Prefer theme tokens.

---

# 86. COMPONENT NAMING

Use names based on function.

Good:

```text
ReadinessCard
PaymentSummaryCard
ProviderRow
VoiceSettingRow
DiagnosticStatusRow
```

Avoid:

```text
CoolCard
ModernBox
FancyButton
GlowContainer
```

Professional component names matter because future AI agents will use them as semantic context.

---

# 87. DESIGN FILE ORGANIZATION

Recommended:

```text
design/
├── tokens.md
├── components.md
├── screens/
│   ├── home.md
│   ├── history.md
│   ├── providers.md
│   ├── voice.md
│   └── diagnostics.md
└── assets/
```

The primary source remains `design.md`.

These files may be created later if the project grows.

---

# 88. SCREEN SPEC TEMPLATE

Every major screen should eventually be documented as:

```text
Purpose
Entry points
Exit points
Layout
Components
States
Actions
Loading state
Empty state
Error state
Accessibility
Responsive behavior
Analytics
Privacy considerations
Acceptance criteria
```

Analytics should remain absent unless explicitly approved.

---

# 89. DESIGN ACCEPTANCE CRITERIA

A screen cannot be marked complete until:

```text
[ ] Layout implemented
[ ] Typography correct
[ ] Theme tokens used
[ ] Components reusable
[ ] States implemented
[ ] Accessibility reviewed
[ ] Text scaling reviewed
[ ] Error state reviewed
[ ] Empty state reviewed
[ ] Real device reviewed
[ ] Screenshot QA completed
```

---

# 90. DESIGN PHASE GATE

Design implementation follows:

```text
Design concept
↓
Stitch exploration
↓
Human approval
↓
Design freeze
↓
Implementation
↓
Visual QA
↓
ADB/device test
↓
Approval
```

No next project phase begins with an unapproved design for a critical user flow.

---

# 91. FUTURE DESIGN EXTENSIONS

Potential future features:

```text
Bluetooth speaker setup
Multiple merchant profiles
Advanced statistics
Export
Backup
Tablet mode
Wear OS companion
Hardware soundbox integration
```

These must not influence V1 visual complexity.

Design V1 for the real current product.

---

# 92. FINAL VISUAL DIRECTION

The final product should visually communicate:

```text
“Professional payment utility running on my phone.”
```

not:

```text
“Cyberpunk AI payment dashboard.”
```

The application should feel:

```text
Clean
White/light
Neutral
Subtle green
Dark charcoal typography
Moderate radius
Minimal shadows
Clear status
Strong accessibility
```

The dominant visual experience should be calm and trustworthy.

---

# 93. FINAL DESIGN RULE

When choosing between:

```text
more visual effects
```

and

```text
more clarity
```

choose clarity.

When choosing between:

```text
more color
```

and

```text
better information hierarchy
```

choose hierarchy.

When choosing between:

```text
a clever interaction
```

and

```text
a predictable interaction
```

choose predictable.

When choosing between:

```text
a visually impressive screen
```

and

```text
a screen that a merchant understands instantly
```

choose the second.

---

# 94. FINAL DESIGN STATEMENT

UPI Voice Soundbox is a financial utility.

Its design must communicate reliability before personality.

The application must look professionally engineered, visually calm, accessible, and trustworthy.

It must avoid neon, gaming, cyberpunk, crypto-dashboard, and excessive “AI” aesthetics.

Google Stitch MCP may be used to explore and refine the visual system, but every generated design must be validated against this specification and then implemented using production-appropriate Android patterns.

The final visual system is:

```text
LIGHT-FIRST
NEUTRAL
MATURE
GREEN-ACCENTED
TYPOGRAPHICALLY CLEAR
ACCESSIBLE
LOW-NOISE
MERCHANT-FOCUSED
PROFESSIONAL
```

This is the visual baseline for the project.

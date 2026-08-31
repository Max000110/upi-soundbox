# UPI Voice Soundbox — Design System Specification

**Design System:** Modern Material 3 Expressive (Design 05)  
**Status:** Approved & Frozen  
**Version:** 2.0.0  
**Last Updated:** 2026-08-31  

---

## 1. DESIGN OVERVIEW & PHILOSOPHY

Design 05 brings modern, expressive Material 3 principles to the UPI Voice Soundbox:
- **Tonal Color Architecture:** Clear visual hierarchy with royal blue (`#1A56DB`), tonal containers (`#E0E7FF`), and dark slate (`#1E293B`).
- **Expressive Shapes:** 24dp rounded corners on primary cards, 20dp on tactile action buttons, and 12dp pill status badges.
- **Glanceability:** Prominent hero container displaying soundbox armed state, today's total revenue, and transaction volume.
- **Air-Gapped Privacy:** Anti-Screen Recording Guard (`FLAG_SECURE`) integrated seamlessly into the Voice & Security settings.

---

## 2. COLOR PALETTE

| Token | Hex Value | Role |
| :--- | :--- | :--- |
| `PrimaryM3` | `#1A56DB` | Primary brand & active actions |
| `PrimaryContainerM3` | `#E0E7FF` | Tonal hero container background |
| `OnPrimaryContainerM3` | `#1E3A8A` | High-contrast text on tonal containers |
| `SecondaryM3` | `#4338CA` | Accent highlights & secondary buttons |
| `BackgroundM3` | `#EDF2F7` | Soft, clean canvas background |
| `SurfacePrimary` | `#FFFFFF` | Card surfaces & navigation bar |
| `TextPrimary` | `#1E293B` | High-contrast headline & title typography |
| `TextSecondary` | `#64748B` | Subtitles, timestamps & metadata |
| `SemanticSuccess` | `#10B981` | Live listener & armed status beacon |
| `SemanticError` | `#EF4444` | Errors & disconnected alerts |

---

## 3. COMPONENT ARCHITECTURE

1. **Expressive Readiness Hero:**
   - Background: `PrimaryContainerM3` (`#E0E7FF`)
   - Corner Radius: `24dp`
   - Content: Status badge ("LIVE & ARMED"), Today's Revenue (`₹...`), Transaction Count.
2. **Tactile Soundbox Action:**
   - 20dp rounded button with `PrimaryM3` (`#1A56DB`), transient audio focus playback.
3. **Transaction Feed Cards:**
   - Surface: Pure white `#FFFFFF` with 20dp rounded corners and subtle elevation.
   - Layout: Payer name, provider pill badge, relative timestamp, and bold currency callout.
4. **Navigation Dock:**
   - Tonal floating pill navigation items with active blue indicator.

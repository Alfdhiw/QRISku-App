---
name: Merchant Voice Utility
colors:
  surface: '#faf8ff'
  surface-dim: '#d2d9f4'
  surface-bright: '#faf8ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f3ff'
  surface-container: '#eaedff'
  surface-container-high: '#e2e7ff'
  surface-container-highest: '#dae2fd'
  on-surface: '#131b2e'
  on-surface-variant: '#434655'
  inverse-surface: '#283044'
  inverse-on-surface: '#eef0ff'
  outline: '#737686'
  outline-variant: '#c3c6d7'
  surface-tint: '#0053db'
  primary: '#004ac6'
  on-primary: '#ffffff'
  primary-container: '#2563eb'
  on-primary-container: '#eeefff'
  inverse-primary: '#b4c5ff'
  secondary: '#516070'
  on-secondary: '#ffffff'
  secondary-container: '#d5e4f8'
  on-secondary-container: '#576676'
  tertiary: '#00632b'
  on-tertiary: '#ffffff'
  tertiary-container: '#117e3b'
  on-tertiary-container: '#c4ffc9'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dbe1ff'
  primary-fixed-dim: '#b4c5ff'
  on-primary-fixed: '#00174b'
  on-primary-fixed-variant: '#003ea8'
  secondary-fixed: '#d5e4f8'
  secondary-fixed-dim: '#b9c8db'
  on-secondary-fixed: '#0e1d2b'
  on-secondary-fixed-variant: '#3a4858'
  tertiary-fixed: '#95f8a7'
  tertiary-fixed-dim: '#79db8d'
  on-tertiary-fixed: '#00210a'
  on-tertiary-fixed-variant: '#005323'
  background: '#faf8ff'
  on-background: '#131b2e'
  surface-variant: '#dae2fd'
typography:
  display-nominal:
    fontFamily: Roboto Flex
    fontSize: 34px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  display-nominal-mobile:
    fontFamily: Roboto Flex
    fontSize: 30px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.02em
  heading-lg:
    fontFamily: Roboto Flex
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  heading-md:
    fontFamily: Roboto Flex
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Roboto Flex
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Roboto Flex
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  button-text:
    fontFamily: Roboto Flex
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: 0.01em
  caption:
    fontFamily: Roboto Flex
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 18px
  label-sm:
    fontFamily: Roboto Flex
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.02em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1rem
  space-xs: 0.5rem
  space-sm: 1rem
  space-md: 1.5rem
  space-lg: 2rem
  space-xl: 2.5rem
---

## Brand & Style
The design system embodies a utility-first, modern minimalist aesthetic engineered specifically for Indonesian MSME (UMKM) merchants across diverse age groups and varying digital literacy levels. The visual philosophy emphasizes trust, clarity, effortless legibility, and high-contrast ergonomics suitable for dynamic physical stall environments (e.g., bustling markets, outdoor stalls under direct sunlight, or dim indoor warungs). 

Key brand characteristics:
- **Pragmatic & Reassuring:** Provides immediate auditory and glanceable visual feedback, removing anxiety around cashless transaction confirmations.
- **Approachable Modernity:** Clean surfaces, generous tap targets, uncluttered layouts, and a deliberate absence of visual noise or confusing decorative abstractions.
- **Auditory-First Synergy:** UI layouts prioritize payment alerts, voice assistant status toggles, and high-impact transaction figures.

## Colors
The color palette relies on high-contrast, functional tones designed to meet and exceed WCAG 2.1 AA accessibility ratios against neutral backdrops.

- **Primary (`#2563EB` / Blue 600):** Drives key calls-to-action, primary state toggles, and listening activity indicators.
- **Primary Pressed / Contrast (`#1D4ED8` / Blue 700):** Applied for pressed states in Jetpack Compose interaction sources, as well as ultra-high-contrast micro-copy.
- **Primary Container / Soft (`#DBEAFE` / Blue 100):** Backdrop for active listener cards, badge chips, and audio testing highlights.
- **Background (`#F8FAFC` / Slate 50):** A cool, glare-free canvas that reduces eye strain throughout long market trading hours.
- **Surface (`#FFFFFF`):** High-clarity elevated containers, bottom sheets, top app bars, and dialogs.
- **Text Primary (`#0F172A` / Slate 900):** Dominant value displays, headings, and critical status messages.
- **Text Secondary (`#475569` / Slate 600):** Time metadata, secondary descriptions, and contextual support labels.
- **Border / Outline (`#E2E8F0` / Slate 200):** Subtle boundary definitions for cards and text inputs to maintain card integrity on varied screen qualities.
- **Semantic Feedback:**
  - **Success (`#15803D` / Green 700):** Service running, listener active, TTS sound operational.
  - **Warning (`#B45309` / Amber 700):** Low device volume alerts, battery optimization cautions.
  - **Error (`#B91C1C` / Red 700):** Notification listener permission missing, TTS engine offline.

## Typography
Typography is centered on **Roboto Flex** (Android System Sans), ensuring zero runtime font packaging overhead while delivering maximum readability on standard Android LCD and OLED displays.

- **Nominal Display Typography:** Always uses tabular figures (`tnum`) to ensure numeric alignment across varying payment totals. Currency prefix ("Rp") is styled alongside numbers without baseline shift to prevent cognitive friction.
- **Hierarchy:** Clear distinction between payment values, merchant notification descriptions, and auxiliary timestamps.
- **Visual Ergonomics:** Line heights are maintained at a minimum of 1.4x for body copy to support aging eyes and rapid glanceability while handling transactions and physical stock.

## Layout & Spacing
The layout follows a strict 8dp baseline grid (8dp, 16dp, 24dp, 32dp, 40dp).

- **Screen Padding:** Standard horizontal margins are 16dp (`space-sm`) on phones, scaling to 24dp on large foldable/tablet screens.
- **Vertical Rhythm:**
  - Compact gap between related micro-elements: 8dp (`space-xs`).
  - Standard card interior padding: 16dp (`space-sm`).
  - Separation between functional sections: 24dp (`space-md`).
  - Top and bottom view clearances: 32dp (`space-lg`).
- **Touch Ergonomics:** All touchable targets strictly observe a minimum physical hit area of 48x48dp, with primary merchant action buttons enforcing an explicit 52dp minimum vertical height.

## Elevation & Depth
Elevation is maintained using a hybrid of tonal layering and soft ambient shadows to keep views crisp under outdoor ambient light:

- **Level 0 (Flat / Canvas):** Surface color `#F8FAFC`, no shadow.
- **Level 1 (Cards & List Items):** Surface `#FFFFFF`, border 1dp solid `#E2E8F0`, ambient drop shadow: 0dp horizontal, 2dp vertical, 8dp blur, color `#0F172A` at 4% opacity.
- **Level 2 (Active Assistant Banner / Modals):** Surface `#FFFFFF` or `#DBEAFE`, ambient drop shadow: 0dp horizontal, 4dp vertical, 16dp blur, color `#0F172A` at 8% opacity.
- **Level 3 (Bottom Navigation & Sheets):** Surface `#FFFFFF`, top stroke 1dp solid `#E2E8F0`, shadow: 0dp horizontal, -4dp vertical, 20dp blur, color `#0F172A` at 6% opacity.

## Shapes
Shapes employ welcoming, soft geometry that creates clear visual affordances without feeling childlike or overly playful:

- **Buttons:** 12dp to 16dp corner radius (`RoundedCornerShape(14.dp)` in Jetpack Compose).
- **Cards & Status Banners:** 16dp to 20dp corner radius (`RoundedCornerShape(18.dp)`).
- **Badges & Chips:** 8dp corner radius for compact tags; pill-shaped (`RoundedCornerShape(50)`) exclusively for real-time status pills (e.g., "Mendengarkan", "Aktif").
- **Bottom Navigation Surface:** 0dp bottom corners, 20dp top-left and top-right corner radius.

## Components

### Buttons
- **Primary Button:** Background `#2563EB`, text `#FFFFFF`, 16sp Semibold. Minimum height 52dp. Corner radius 14dp. Pressed state shifts to `#1D4ED8`. Ripple effect configured via Compose `rememberRipple`.
- **Secondary / Outlined Button:** Background transparent, border 1.5dp solid `#2563EB`, text `#2563EB`. Minimum height 52dp.
- **Critical Action Button:** Background `#B91C1C`, text `#FFFFFF` (used exclusively for permission overrides or critical resets).

### Transaction & Voice Status Cards
- **Voice Monitoring Highlight Card:** Background `#DBEAFE`, border 1.5dp `#2563EB`, radius 18dp, padding 16dp. Houses the master switch, live microphone/notification pulse indicator, and volume status indicator.
- **Transaction History Card:** Background `#FFFFFF`, border 1dp `#E2E8F0`, radius 16dp, padding 16dp. Left-aligned bank/e-wallet icon badge, center column with timestamp and masked buyer name, right-aligned nominal in `#0F172A` (20sp Bold).

### Chips & Status Indicators
- **Status Chip:** Height 32dp, radius 16dp (pill).
  - *Active:* Background `#DCFCE7` (Green 100), text `#15803D` (Green 700), leading 8dp green dot.
  - *Warning:* Background `#FEF3C7` (Amber 100), text `#B45309` (Amber 700), leading 8dp warning icon.
  - *Inactive / Error:* Background `#FEE2E2` (Red 100), text `#B91C1C` (Red 700).

### Lists & Dividers
- Single-line and two-line list rows have a minimum touch height of 56dp.
- Dividers are 1dp `#E2E8F0`, inset by 16dp to align with typography left margins.

### Inputs & Sliders (Audio Settings)
- **Voice Test Input Field:** Surface `#FFFFFF`, border 1.5dp `#E2E8F0`, focus border 2dp `#2563EB`, height 56dp, radius 12dp.
- **Volume & Pitch Sliders:** Active track `#2563EB` (height 6dp), inactive track `#E2E8F0` (height 6dp), thumb `#2563EB` with 24dp hit diameter.

### Bottom Navigation Bar
- Height 64dp + system gesture insets.
- Background `#FFFFFF` with top border 1dp `#E2E8F0`.
- 3 primary destinations: **Beranda** (Home / Voice Monitor), **Riwayat** (Transaction History), and **Pengaturan** (Voice & Permission Settings).
- Active item: `#2563EB` with bold label. Inactive item: `#475569`.


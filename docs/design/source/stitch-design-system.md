---
name: Mzansi Express Food
colors:
  surface: '#f7f9fb'
  surface-dim: '#d8dadc'
  surface-bright: '#f7f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f4f6'
  surface-container: '#eceef0'
  surface-container-high: '#e6e8ea'
  surface-container-highest: '#e0e3e5'
  on-surface: '#191c1e'
  on-surface-variant: '#534434'
  inverse-surface: '#2d3133'
  inverse-on-surface: '#eff1f3'
  outline: '#867461'
  outline-variant: '#d8c3ad'
  surface-tint: '#855300'
  primary: '#855300'
  on-primary: '#ffffff'
  primary-container: '#f59e0b'
  on-primary-container: '#613b00'
  inverse-primary: '#ffb95f'
  secondary: '#545f73'
  on-secondary: '#ffffff'
  secondary-container: '#d5e0f8'
  on-secondary-container: '#586377'
  tertiary: '#006c49'
  on-tertiary: '#ffffff'
  tertiary-container: '#30c88f'
  on-tertiary-container: '#004e34'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffddb8'
  primary-fixed-dim: '#ffb95f'
  on-primary-fixed: '#2a1700'
  on-primary-fixed-variant: '#653e00'
  secondary-fixed: '#d8e3fb'
  secondary-fixed-dim: '#bcc7de'
  on-secondary-fixed: '#111c2d'
  on-secondary-fixed-variant: '#3c475a'
  tertiary-fixed: '#6ffbbe'
  tertiary-fixed-dim: '#4edea3'
  on-tertiary-fixed: '#002113'
  on-tertiary-fixed-variant: '#005236'
  background: '#f7f9fb'
  on-background: '#191c1e'
  surface-variant: '#e0e3e5'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 32px
  headline-md:
    fontFamily: Inter
    fontSize: 22px
    fontWeight: '700'
    lineHeight: 28px
  headline-sm:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
  label-sm:
    fontFamily: Inter
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 14px
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
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system is tailored for an energetic, dependable, fast-paced South African food and essentials on-demand delivery app. Rooted in the visual legacy of iconic local delivery, the aesthetic captures warmth, sunshine, convenience, and urban vibrancy. It follows modern Android Jetpack Compose and Material 3 paradigms, fusing functional utility with welcoming optimism.

### Brand Personality & Emotional Intent
- **Warm & Energetic:** Evoked via high-visibility sun-drenched amber and warm golden tones, sparking hunger and urgency without inducing anxiety.
- **Utilitarian & Grounded:** Anchored by deep slate/charcoal text layers that deliver superior outdoor readability under harsh South African sunlight.
- **Effortless Reliability:** Smooth transitions, glanceable layout hierarchies, high-contrast badges (ETA, rating, delivery fees), and immediate affordances.

### Design Movement
**Modern Material Utility / Clean Tactile Warmth:** Combining Material Design 3 elevation and tonal container principles with soft ambient drop shadows, pill badges, and crisp white cards. Interfaces balance dense culinary metadata (prep time, delivery radius, deals) with breathable, tactile tap targets designed for quick single-handed thumb interactions on mobile devices.

## Colors

The palette is engineered for rapid visual scanning, rich food photography contrast, and compliant Material 3 container-on-surface mappings.

### Color Roles & Semantics
- **Primary (`#F59E0B` - Sun Amber):** Drives key action drivers: primary CTA buttons (e.g., "Add to Cart", "Checkout"), active bottom navigation indicators, promotional banners, and focus rings. High-energy yet balanced against dark typography for AAA contrast compliance.
- **Secondary (`#1E293B` - Deep Slate):** Delivers bedrock legibility for headings, primary text, prominent pricing badges, and selected filter chips. Replaces harsh pitch black with a softer, premium charcoal hue.
- **Tertiary (`#10B981` - Fresh Mint Emerald):** Signals success, operational freshness, active restaurant status ("Open"), high customer ratings (4.5+), and free delivery promotions.
- **Neutral Surface & Background (`#F8FAFC` to `#FFFFFF`):** Base canvas starts with ultra-light slate tint (`#F8FAFC`) to elevate crisp, pure white (`#FFFFFF`) card surfaces and interactive sheets.
- **Muted & Neutral Variant (`#64748B` - Slate Muted):** Used for supporting descriptions, estimated delivery ranges, and subtle borders.
- **Destructive / Urgency (`#EF4444` - Crimson):** Reserved for cart item removal, closed restaurant status, and critical alerts.

## Typography

This design system uses Inter (or system Roboto fallback on native Android) across all scales to maintain clean, neutral legibility alongside vibrant food photography and bold amber surfaces.

### Typographic Hierarchy Guidelines
- **Headlines (`headline-lg` through `headline-sm`):** Set in Semi-Bold (600) and Bold (700) weights with tight letter tracking. Used for restaurant names, category spotlights, and checkout invoice totals.
- **Body (`body-lg` through `body-sm`):** Standard Regular (400) weight with relaxed line-heights to support effortless scanning of dish ingredients, delivery instructions, and dietary declarations.
- **Labels (`label-lg` through `label-sm`):** Elevated to Semi-Bold (600) and Bold (700) for button CTAs, chip text, ETA badges, dietary tag labels, and numeric rating indicators.

## Layout & Spacing

Layout adheres to an 8dp/4dp strict incremental grid model optimized for Android mobile viewport realities (360dp to 412dp standard phone widths).

### Layout Geometry
- **Outer Canvas Padding (`margin`):** 16dp (`1rem`) on standard mobile phone viewports, scaling to 24dp (`1.5rem`) on large foldables and tablet splits.
- **Gutter Space (`gutter`):** 16dp (`1rem`) horizontal space between cards in dual-column category or item grids.
- **Touch Target Standard:** All interactive touch points conform to the minimum Android M3 touch target requirement of 48dp x 48dp, even when visual boundaries (such as a 28dp pill chip) appear smaller.
- **Horizontal Carousels:** Category browsing carousels and top-deal feeds utilize full edge bleed with `16dp` start padding, permitting the trailing card to peak off-screen to signal scrollability.

## Elevation & Depth

This system avoids heavy drop shadows, opting for Material 3 tonal elevation combined with soft, warm-tinted ambient diffusions.

### Elevation Hierarchy
- **Level 0 (Flat / Canvas):** Surface color `#F8FAFC`. Used for global backgrounds and divider lines.
- **Level 1 (Crisp Resting Cards):** Surface color `#FFFFFF` with an ambient shadow: `0px 2px 8px rgba(30, 41, 59, 0.06)` and a subtle 0.5dp border of `rgba(226, 232, 240, 0.8)`. Applied to restaurant cards, menu item containers, and promo panels.
- **Level 2 (Raised Bars & Interactive Overlays):** Surface color `#FFFFFF` with `0px 4px 16px rgba(30, 41, 59, 0.10)`. Used for the top app bar on scroll, sticky search bar containers, and the bottom navigation bar.
- **Level 3 (Modal Sheets & Snackbars):** Bottom sheets, add-to-cart modifier modals, and cart floating bottom anchors use `0px 8px 32px rgba(30, 41, 59, 0.16)`.

## Shapes

The design uses balanced rounded corners (Level 2) to maintain friendly, modern Android tactile softness while retaining compact layout efficiency.

### Shape Scales
- **Base Components (Cards, Text Fields, Dialogs):** 8dp to 16dp corner radius (`rounded-lg`), producing soft and warm structural framing for food photography.
- **Pills (Chips, Badges, Floating Action Bars):** Fully rounded pill geometry (`rounded-full` / 999dp) applied to ETA tags, ratings, filter chips, and primary floating "View Cart" triggers.
- **Bottom Sheets:** Top-left and top-right radii set to 24dp (`rounded-xl`), creating an inviting, thumb-accessible slide-up container.

## Components

### Buttons
- **Primary CTA:** Saturated warm amber background (`#F59E0B`), deep slate text (`#1E293B`), semi-bold label typography, height 48dp to 54dp, fully pill-shaped (`999dp`) or rounded 12dp. Pressed state shifts to `#D97706`.
- **Secondary CTA:** Slate container (`#1E293B`) with white text (`#FFFFFF`) or pure white container with a 1dp border (`#E2E8F0`) for secondary cart management.
- **Floating Cart Button:** Persistent bottom bar or pill floating 16dp above the navigation bar, featuring item quantity, dynamic ETA badge, and total price formatted in ZAR (R).

### Chips & Filter Pills
- **Filter Chips:** Height 36dp with fully rounded pill edges. Inactive state: white background with `#E2E8F0` border and `#1E293B` text. Selected state: deep slate (`#1E293B`) background with white text and active dismissal icon, or warm amber background.
- **Category Icon Chips:** Vertical stack with circular image container (56dp) resting on white surface, paired with `label-md` category labels.

### Badges & Status Indicators
- **ETA Badge:** Compact pill containing clock icon and text (e.g., `25–35 min`), tinted light warm slate (`#F1F5F9`) with `#1E293B` typography.
- **Rating Badge:** High-contrast pill featuring a star glyph with deep slate text and warm amber accent background (`#FEF3C7`), or green accent (`#ECFDF5` / `#059669`) for ratings above 4.5.
- **Status Indicators:** Micro-pills for restaurant availability (`Open` in `#10B981` / `Closed` in `#EF4444`).

### Cards
- **Restaurant Card:** Vertically arranged container with a 16:9 ratio photo, top-right heart/favorite toggle icon, and bottom content bay displaying restaurant logo, title, rating pill, ETA badge, cuisine tags, and delivery fee in Rands.
- **Dish / Menu Card:** Horizontal row layout with title, two-line description, Rand pricing, and a square rounded (80dp) dish thumbnail with a plus (`+`) quick-add action button anchored to the bottom-right.

### Form Inputs & Search Bar
- **M3 Search Bar:** Docked or floating rounded pill bar (height 52dp) with leading magnifying glass icon, placeholder "Search food, groceries, and drinks...", and trailing microphone or filter tuning button. Surface `#FFFFFF` with Level 2 elevation on scroll.
- **Text Fields:** Outlined Material 3 style with rounded 12dp corners, charcoal input text, and warm amber active focus border.

### Navigation
- **Top App Bar:** Clean white surface featuring delivery address dropdown selector ("Deliver to: 12 Kloof St..."), profile avatar, and notification bell.
- **Bottom Navigation Bar:** Native Android navigation rail/bar with 4 destinations (Home, Search, Orders, Account). Active indicators use warm amber pill highlights with deep slate icons.
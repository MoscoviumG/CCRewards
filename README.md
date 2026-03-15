# CCRewards

A US credit card portfolio optimizer for Android. CCRewards helps you track every card you own, find the highest-earning card for each spending category, monitor recurring credits before they expire, and configure point valuations to reflect your personal redemption style.

[![Download APK](https://img.shields.io/badge/Download-APK-brightgreen?logo=android)](https://github.com/MoscoviumG/CCRewards/releases/latest)

> **Requires Android 15 (API 35) or higher.**
> Download the APK, open it on your device, and allow installation from unknown sources when prompted.

---

## Table of Contents

1. [Features Overview](#features-overview)
2. [Supported Cards](#supported-cards)
3. [How to Use](#how-to-use)
   - [My Cards](#my-cards)
   - [Best Card](#best-card)
   - [Credits](#credits)
   - [Settings](#settings)
4. [Tech Stack](#tech-stack)
5. [Project Structure](#project-structure)
6. [Seed Database Developer Guide](#seed-database-developer-guide)
7. [Build Notes](#build-notes)

---

## Features Overview

| Feature | Description |
|---|---|
| **Portfolio Management** | Add, edit, and track every credit card you own |
| **Reward Rate Customization** | Override any card's default earn rate per category |
| **Best Card Recommender** | One-tap answer to "which card should I use here?" |
| **Travel Sub-Category Tiles** | Separate Best Card tiles for each airline and hotel brand |
| **Custom Spend Categories** | Define niche categories (e.g. "Apple.com") with per-card rates |
| **Welcome Bonus Tracking** | Monitor active sign-up bonuses with spend progress and deadlines |
| **Quarterly Rotational Bonuses** | Track time-limited bonus rates (e.g. 5% on gas this quarter) per card |
| **Credits Tracker** | Log and toggle recurring statement credits before they reset |
| **Editable Usage History** | Tap any past usage entry to correct amounts, toggle status, or delete it; add entries for past periods |
| **Custom Benefit Reset Date** | Set a specific calendar date as the annual reset anchor — ideal for free nights and oddly-timed credits |
| **Benefit Reminders** | Daily notifications for credits and welcome bonuses about to expire |
| **Point Valuations** | Per-currency ¢/pt editor powering all effective-return math |
| **Transfer Partner Reference** | In-app lookup of transfer ratios for each transferable currency |
| **Product Change History** | Record card upgrades and downgrades with full account history |
| **Sort My Cards** | Sort your card list by open date, name, annual fee, issuer, or anniversary month |
| **Card Color Customization** | Override the display color strip on any card to your preference |
| **Portfolio Statistics** | At-a-glance total card count, total annual fees, and Chase 5/24 tracker with next drop-to-4 date |
| **Manage Best Card Categories** | Choose which categories appear in the Best Card grid, reorder them via drag-and-drop, and add custom categories |
| **Launch Screen** | Choose which tab the app opens to on launch |
| **Dark / Light / System Theme** | Choose app theme in Settings |
| **Data Export / Import** | Back up all card data to a JSON file; restore it on a new device (Experimental) |

---

## Supported Cards

The app ships with 118 pre-loaded cards (personal + business) across Chase, Amex, Citi, Capital One, Bank of America, US Bank, Wells Fargo, Bilt, Discover, HSBC, Barclays, and more.

For the full catalog — including every card's reward rates, annual fee, and tracked benefits — see **[CARDS.md](CARDS.md)**.

---

## How to Use

### My Cards

The **My Cards** tab is your card portfolio. Every card you own lives here.

---

#### Card List

The main view shows all cards you have added. Each tile displays the card name, last four digits, nickname (if set), annual fee, and issuer. A colored strip along the left edge reflects the card's brand color.

**Filter** — Tap the filter icon (top-right) to narrow the list by:
- **Type** — Personal, Business
- **Issuer** — Chase, Amex, Citi, Capital One, Bank of America, and more
- **Network** — Visa, Mastercard, Amex, Discover
- **Anniversary month** — surface cards whose annual fee renewal is coming up
- **Card age** — new (< 30 days), under 1 year, 1–3 years, 3+ years

An active-filter badge on the button shows how many filters are on. Tap again to clear or adjust them.

**Sort** — Tap the sort icon (top-left, next to the filter) to change the order of the card list. Options:
- **Default** — order cards were added to the portfolio
- **Open date** — oldest to newest by the date the card was opened
- **Alphabetical** — A–Z by card name
- **Annual fee** — lowest to highest
- **Issuer** — grouped alphabetically by issuer name
- **Anniversary month** — grouped by the month the card anniversary falls in

Your sort preference is saved and persists between app restarts.

---

#### Adding a Card

Tap the **+** FAB at the bottom-right. You can add a card two ways:

**From the catalog** — Browse or search the pre-loaded cards. Use the filter button on the Add Card screen to narrow by type, issuer, or network. Tap a card, then fill in:
- *Last four digits* (optional) — displayed throughout the app to distinguish duplicate cards
- *Nickname* (optional) — shown alongside the card name throughout the app
- *Credit limit* (optional)
- *Open date* — defaults to today; used for anniversary-reset benefits and product-change history

You can also set a welcome bonus directly from the add card flow — a **Set Welcome Bonus** option appears at the bottom before you save.

Tap **Add** to save.

**Custom card** — Tap "Create Custom Card" for a card not in the catalog. Enter the name, issuer, annual fee, and the same optional fields above. You can add reward rates and benefits inline before saving.

---

#### Card Detail

Tap any card in the list to open its full profile.

**Header** — Shows the card name, issuer, network, annual fee, credit limit, and open date. A brand-colored strip runs along the top.

Tap **Change Card Color** (below the header) to open a color picker and override the strip color with any of 20 preset card-like colors. Select a swatch to apply it immediately. Tap **Reset to Default** to restore the card's original brand color.

**Reward Rates** — Lists every earn rate on the card (e.g. "Dining: 3x UR Points"). Tap **Edit Reward Rates** to override any rate.

**Welcome Bonus** — Tracks an active sign-up bonus on this card (see [Welcome Bonus](#welcome-bonus)).

**Quarterly Bonuses** — Shows all active rotational bonus rates on this card. Tap **+ Add Quarterly Bonus** to add one.

**Benefits & Credits** — Lists all statement credits and perks. Tap any benefit to view its full usage history and edit usage. Tap **+ Add Benefit** to create a custom benefit.

**Account History** — Records every product change (upgrade/downgrade) made to this account.

**Bottom actions** — **Product Change** to log a card upgrade or downgrade; **Delete Card** to remove the card (with confirmation).

---

#### Editing Reward Rates

Tap **Edit Reward Rates** on the Card Detail screen. Every spend category is listed with an editable rate field and rate-type chips (Points / Miles / Cash Back). Change any value and tap **Save**. Customized rows are highlighted so you can tell them apart from defaults. Tap **Add Category** to add a rate for a category not listed. Tap **Reset Rates** to restore all values to the seed defaults.

---

#### Product Change

If you upgrade or downgrade a card (e.g. Chase Sapphire Preferred → Chase Freedom Unlimited), tap **Product Change**. The app shows all other cards from the same issuer. Tap the target card, confirm, and the app:
- Switches the card's definition (new name, rates, and benefits)
- Preserves the original open date and account history
- Records the change in Account History with today's date

---

#### Welcome Bonus

Each card can have one active welcome bonus tracked at a time. In Card Detail, under **Welcome Bonus**, tap **+ Set Welcome Bonus**. In the bottom sheet, enter:

- **Bonus amount** — points/miles count (e.g. 60,000) or dollar value for cash-back cards (e.g. $200)
- **Spend requirement** — minimum spend to earn the bonus (e.g. $4,000)
- **Deadline** — optional; tap to open a date picker. Bonuses past their deadline are automatically hidden from the Best Card banner
- **Show in Best Card** — toggle whether this bonus appears in the Best Card banner (on by default)

Once saved, three actions are available from Card Detail:
- **Mark Done** — records the bonus as earned; disappears from the Best Card banner but remains visible on Card Detail
- **Edit** — reopens the bottom sheet pre-filled
- **Remove** — deletes the bonus record after a confirmation dialog

---

#### Quarterly Rotational Bonuses

Some cards offer time-limited bonus rates on rotating or user-selected categories (e.g. "5% on gas stations through March 31"). Tap **+ Add Quarterly Bonus** to record one. Fill in:

- **Label** — a short name for the bonus period (e.g. "Q1 2026 Gas")
- **End date** — when the bonus expires; defaults to the end of the current quarter
- **Spend limit** — maximum spend eligible for the bonus rate (e.g. $1,500)
- **Categories** — one or more spend categories with their bonus rate and rate type

Once saved, the bonus appears on Card Detail with a progress bar. Drag the bar to update usage. Tap **Mark Done** when the limit is reached or the period ends.

Active quarterly bonuses are automatically injected into **Best Card** rankings for their categories while live and spend is available.

---

### Best Card

The **Best Card** tab answers *"which card should I swipe right now?"* for every spend category.

---

#### Category Grid

The main view is a 2-column grid of spend category tiles. Each tile shows:
- The spend category name
- The best card in your portfolio for that category
- The effective return percentage (e.g. "3.00%") — or the raw earn rate (e.g. "3x UR") depending on your Settings display preference

The categories shown, their order, and their visibility are all configurable from **Settings → Best Card Categories** (see [Manage Best Card Categories](#manage-best-card-categories)).

---

#### Travel Group

Tapping the **Travel** tile opens a dedicated Travel screen with 19 sub-category tiles:
- **General Travel** — true general travel cards (CSP, CSR, Amex Platinum, etc.)
- **Travel Portal** — booking through card-specific travel portals
- **Airlines** — Alaska, American, Aer Lingus, Air France / KLM, Aeroplan, Allegiant, British Airways, Delta, Iberia, Southwest, Spirit, United
- **Hotels** — Hilton, Hyatt, IHG, Marriott
- **Cruises**

Each sub-category tile shows only cards that earn that brand's currency. General-travel cards do not bleed into brand-specific tiles.

---

#### Welcome Bonus Banner

When you have active, unachieved welcome bonuses, a **Welcome Bonuses** banner appears at the top of the Best Card screen. Each row shows:
- Card name with last four and nickname
- Bonus details — points/miles amount and effective return estimate
- Spending required and a progress bar showing spend-to-goal completion
- Expiry date and spend progress format: `$used / $total`

Tap **Mark Done** on any row to record the bonus as achieved.

---

#### Quarterly Bonus Banner

When you have active quarterly rotational bonuses, a **Quarterly Bonuses** section appears below the welcome bonus banner. Each row shows:
- Card name and bonus label
- Bonus categories and their rates
- A spend progress bar with the `$used / $total` format

Drag the progress bar to update spend. Tap **Mark Done** when the bonus is exhausted or expired.

---

#### Tap a Tile → Category Detail

Tapping any tile (other than Travel) opens a ranked list of all eligible cards for that category, sorted by effective return. Each row shows the card name, effective return %, and an "Owned" chip if the card is in your portfolio.

---

#### Custom Categories

To add a custom spend category (e.g. "Apple.com", "Costco"), go to **Settings → Best Card Categories** and tap the **+** FAB. Enter a name and tap **Create**. The app navigates directly to the Custom Category Detail screen.

**Custom Category Detail** shows every card in your portfolio. For each card you can:
- Enter a custom earn rate for this category
- Leave it blank to fall back to the card's General rate
- Tap **×** to clear a previously entered override

To rename or delete a custom category, use the overflow menu (⋮) on the Custom Category Detail screen.

---

### Credits

The **Credits** tab is your benefit coupon book — every statement credit, free night, and recurring perk across all your cards in one place.

---

#### Benefit List

Benefits are grouped by card and reset period. Each group header shows the card name (with last four digits and nickname) and how many days until its next reset. Each benefit row shows:
- The benefit name and dollar value (e.g. "$300 / yr", "$15 / mo")
- An **anniversary** badge if this benefit resets on the card's open-date anniversary
- A **Used / Not used** toggle switch

Flipping the toggle to **Used** marks the benefit as used for the current period and records today's date. The toggle automatically resets to "Not used" when a new period begins.

**Search** — type in the search bar at the top to filter benefits by name or card.

**Hide Used** — tap the chip to hide all benefits already marked used in the current period.

---

#### Benefit Detail

Tap any benefit row to open its detail view. At the top: the name, description, and value. A **slider** (for monetary benefits) or **toggle** (for non-monetary perks) lets you log usage for the current period.

**Mark as Fully Used** — tap the button to jump the slider to 100% and mark the full amount as used.

**Usage History** — below the usage controls, every past period is listed showing whether the benefit was used, how much was used, and the date it was marked.

**Editing a past entry** — Tap any row in the Usage History list to open an edit dialog:
- For monetary benefits: adjust the dollar amount used (the "fully used" flag updates automatically)
- For non-monetary perks: toggle the used/not-used state
- Tap **Delete** (with a confirmation dialog) to remove the entry entirely
- Changes to the current period instantly sync the slider at the top — no re-entry needed

**Adding a past entry** — Tap **+ Add Past Entry** (next to the Usage History header). A date picker appears; pick any date. The app computes the correct period key from that date (respecting the benefit's reset type and period), then prompts you to enter the usage amount or toggle. This lets you backfill credits you forgot to log.

---

#### Adding and Editing Benefits

From Card Detail, tap **+ Add Benefit** (or tap a benefit row and use the edit icon in the toolbar). Fill in:

- **Name** — what the benefit is (e.g. "Priority Pass Lounge Credit")
- **Description** — optional notes
- **Amount** — dollar value (e.g. $30); enter 0 for non-monetary perks like free nights or lounge visits
- **Reset period** — Monthly, Quarterly, Semi-annual, or Annual
- **Reset type:**
  - *Calendar* — resets on Jan 1 / Apr 1 / Jul 1 / Oct 1, etc.
  - *Anniversary* — resets on the exact same month and day the card was opened
  - *Custom (specific start date)* — resets on a user-chosen month and day each year. Use this for benefits that don't align with either the calendar or the card anniversary. Tap **Pick Date** to choose the month and day (year is ignored). Ideal for free night certificates, which often post a few days after the card anniversary rather than on it exactly.

---

#### Reminders

The app sends a daily notification for:
- **Benefit expiry** — benefits about to reset unused within your configured threshold
- **Welcome bonus deadlines** — unachieved bonuses within N days of their deadline, with remaining spend shown

Configure reminders in **Settings → Notifications**.

---

### Settings

The **Settings** tab controls how the app calculates and displays reward value, manages notifications, and controls the visual theme.

---

#### Portfolio Statistics

At the very top of Settings, a stats panel shows three numbers updated in real time from your card portfolio:

| Stat | What it shows |
|---|---|
| **Total Cards** | Number of cards currently in your portfolio |
| **Total Annual Fees** | Sum of annual fees across all owned cards |
| **5/24 Status** | Number of cards opened in the past 24 months (Chase's unofficial rule-of-thumb: approval is typically declined if this number is 5 or more) |

> **Note:** Product changes (card upgrades/downgrades) do not count toward 5/24, since only new account openings are considered.

When your 5/24 count is 5 or higher, a subtitle appears below showing the estimated date your count will drop back to 4 — i.e., when the oldest qualifying card ages out of the 24-month window. This tells you the earliest you can apply for a Chase card under the 5/24 rule.

---

#### Best Card Categories

**Settings → Best Card Categories** opens a management screen for the category grid in the Best Card tab.

- **Visibility toggle** — the switch on each row shows or hides that category's tile in the Best Card grid. Hidden categories are still computed internally; they simply don't appear in the grid.
- **Reorder** — hold the drag handle (≡) on the right of any row and drag it to a new position. The grid order updates as soon as you release.
- **Delete custom category** — tap the trash icon on any custom category row. Built-in categories cannot be deleted (only hidden).
- **Add custom category** — tap the **+** FAB (bottom-right). Enter a name and tap Create. The app navigates directly to the Custom Category Detail screen to set per-card rates.

Changes take effect the next time the Best Card tab is opened (or immediately on return).

---

#### Launch Screen

**Settings → Appearance → Launch Screen** lets you choose which tab the app opens to when launched from the home screen. Options:
- My Cards
- Best Card
- Credits
- Settings

The selection is saved immediately and persists across app restarts.

---

#### Point Valuations

This is the most important configuration section. Every effective-return percentage shown in Best Card is computed as:

```
Effective Return % = earn rate × (cents per point ÷ 100)
```

For example, a card earning 3x Chase UR with UR valued at 1.0¢/pt returns 3.0%. If you value UR at 1.5¢/pt (by redeeming through transfer partners), that same card returns 4.5%.

The default valuations shipped with the app are conservative 1¢-per-point baselines:

| Currency | Default ¢/pt |
|---|---|
| Chase Ultimate Rewards Points | 1.0¢ |
| Amex Membership Rewards Points | 1.0¢ |
| Capital One Miles | 1.0¢ |
| Citi ThankYou Points | 1.0¢ |
| Bilt Points | 1.0¢ |
| All airline miles (Delta, United, Southwest, AA, Alaska, Avios, Aeroplan, Flying Blue, Free Spirit) | 1.0¢ |
| World of Hyatt Points | 1.0¢ |
| Hilton Honors Points | 0.4¢ |
| Marriott Bonvoy Points | 0.4¢ |
| IHG One Rewards Points | 0.4¢ |
| BofA Points | 1.0¢ |
| Cash Back | 1.0¢ (fixed) |

Tap any currency to open its detail screen. Enter your personal ¢/pt value and tap **Save**. Tap **Reset to Default** to restore the app's baseline. Changes take effect immediately — all Best Card percentages recalculate on the next open.

---

#### Transfer Partners

On a currency's detail screen, below the ¢/pt editor, a **Transfer Partners** section lists all airline and hotel programs the points transfer to, along with the transfer ratio (e.g. "1:1", "1:1.5"). This is a reference guide to help you decide what valuation to enter.

---

#### Appearance

**Theme** — choose between **Light**, **Dark**, or **System default**. The setting is applied immediately and persists across app restarts.

---

#### Display Mode

**Show effective return %** (default ON): Best Card tiles display the computed percentage (e.g. "3.00%") as the primary value, with the raw earn rate (e.g. "3x UR") shown smaller below.

Turn this OFF to flip the display — raw earn rate becomes primary and the effective % is secondary. Useful if you prefer to see "5x UR" rather than "5.00%" at a glance.

---

#### Notifications

**Enable reminders** — master toggle for all benefit expiry and welcome bonus notifications. Toggling on sends an immediate test notification to confirm everything is working.

**Days before reset** — how far in advance to start notifying (options: 1, 3, 5, 7, 14, 30 days). At 7 days (default), you'll be notified starting 7 days before a benefit resets if unused, or 7 days before a welcome bonus deadline if the bonus hasn't been earned.

**Reminder time** — the time of day to send the daily check (default 9:00 AM). The app schedules a WorkManager periodic task aligned to this exact hour and minute.

When a benefit notification fires, it lists the benefit name, its value, and how many days remain. When a welcome bonus notification fires, it shows the remaining spend needed and days left. Tapping either notification opens the app.

---

#### Data Export / Import (Experimental)

Found at the bottom of Settings under the **EXPERIMENTAL** heading.

**Export Data** — Saves all your cards and data to a JSON file using the system file picker. The file contains every table that holds user data: owned cards, benefit usage history, product change records, welcome bonuses, quarterly bonuses, custom categories, free night awards, custom card definitions, customized reward rates, and point/free-night valuations. Seed data (built-in card definitions, standard rates, transfer partners) is not exported since it already exists on any device with the app installed.

**Import Data** — Opens the system file picker to select a previously exported JSON file. A confirmation dialog warns that this will **replace all existing data** and cannot be undone. On confirmation, the import runs in a single transaction: all user data is deleted, then the backup is restored. The app restarts automatically when the import completes.

> **Limitation:** Benefit usage records reference built-in benefit IDs by their database autoincrement value. If the app was updated between export and import and the seed benefit list changed, some usage records may not match their correct benefits. This is noted in the UI subtitle. For best results, export and import on the same app version.

---

## Tech Stack

| Layer | Library / Version |
|---|---|
| Language | Java (no Kotlin) |
| UI | XML layouts, ViewBinding, Material 3 |
| Navigation | Navigation Component 2.8.7 |
| DI | Hilt 2.59.2 (AGP 9 required) |
| Database | Room 2.7.0 |
| Background | WorkManager 2.10.0 |
| Build | AGP 9.0.1, Gradle 9.2.1 |
| Min / Target SDK | 35 / 35 |

---

## Project Structure

```
app/src/main/java/com/example/ccrewards/
├── data/
│   ├── db/               # AppDatabase, DAOs, TypeConverters
│   ├── model/            # Room entities and enums
│   ├── repository/       # CardRepository, BenefitRepository, RewardRateRepository,
│   │                     #   WelcomeBonusRepository, CustomCategoryRepository,
│   │                     #   RotationalBonusRepository
│   └── seed/             # SeedData, DefaultPointValuations, TransferPartnersSeedData
├── di/                   # DatabaseModule (Hilt)
├── ui/
│   ├── mycards/          # My Cards tab — list, detail, add, edit rates,
│   │                     #   product change, add/edit benefit, welcome bonus bottom sheet,
│   │                     #   add rotational bonus
│   ├── bestcard/         # Best Card tab — grid, travel group, category detail,
│   │                     #   custom category detail
│   ├── credits/          # Credits tab — benefit list, benefit detail
│   └── settings/         # Settings tab — valuations list, currency detail,
│                         #   manage categories, main settings
├── worker/               # BenefitReminderWorker, WorkManagerScheduler
├── util/                 # DateUtil, CurrencyUtil, PeriodKeyUtil, CategoryDisplayPrefs
├── MainActivity.java
└── CCRewardsApp.java
```

---

## Seed Database Developer Guide

All seed data lives in three files under `data/seed/`. The app uses a **versioned seed refresh** system: increment `AppDatabase.SEED_VERSION` and existing installs automatically refresh their card catalog on the next launch without losing any user data.

### Making Seed Changes Take Effect

1. Edit the seed file(s) as described below.
2. Open `AppDatabase.java` and increment `SEED_VERSION` by 1:

```java
public static final int SEED_VERSION = 19;  // was 18 → bump to 19
```

3. Build and deploy — no data wipe needed.

On next launch the app detects the version mismatch and refreshes the catalog in the background:

| Table | What changes | What is preserved |
|---|---|---|
| `card_definitions` | All rows upserted | — |
| `reward_rates` | Non-customized rows replaced per card | User-edited rates (`isCustomized = true`) |
| `card_benefits` | Non-custom rows replaced per card | User-added benefits (`isCustom = true`) |
| `point_valuations` | New currencies inserted; `defaultCentsPerPoint` updated | User's `centsPerPoint` edits |
| `transfer_partners` | Fully replaced | — |
| `user_cards` | Untouched | All owned cards, nicknames, limits, dates, custom colors |
| `benefit_usage` | Untouched | All mark-used state and history |
| `product_change_records` | Untouched | Full account history |
| `welcome_bonuses` | Untouched | All bonus data |
| `rotational_bonuses` | Untouched | All quarterly bonus data |

> **Note:** `user_card_choice_categories` is cleared on refresh because reward rate IDs change when rates are re-inserted. Users will be prompted to re-select their choice category (e.g. Bilt Obsidian dining vs. grocery) after the update.

### Clearing the Database (Development Only)

```bash
adb shell pm clear com.example.ccrewards
```

Or on device: **Settings → Apps → CCRewards → Storage → Clear Data**, then relaunch.

---

### Part 1 — Adding a New Card

**File:** `data/seed/SeedData.java`

#### Step 1 — Define a color constant

```java
private static final long MY_BANK_BLUE = argb(255, 0, 84, 166);
private static final long MY_BANK_DARK = argb(255, 0, 40,  90);
```

#### Step 2 — Add the `CardDefinition`

```java
new CardDefinition(
    "my_bank_signature",            // unique snake_case ID — never reuse or change once set
    "My Bank Signature Visa",       // display name
    "My Bank",                      // issuer
    "Visa",                         // network: "Visa" | "Mastercard" | "Amex" | "Discover"
    95,                             // annual fee in whole dollars (0 = no fee)
    false,                          // isCustom — always false for seeded cards
    false,                          // isBusinessCard
    MY_BANK_BLUE,                   // primary card color
    MY_BANK_DARK,                   // secondary card color
    "Chase Ultimate Rewards Points" // rewardCurrencyName — must EXACTLY match a PointValuation name
),
```

**Valid `rewardCurrencyName` values:**

| Name | Default ¢/pt |
|---|---|
| `"Chase Ultimate Rewards Points"` | 1.0¢ |
| `"Amex Membership Rewards Points"` | 1.0¢ |
| `"Capital One Miles"` | 1.0¢ |
| `"Citi ThankYou Points"` | 1.0¢ |
| `"Bilt Points"` | 1.0¢ |
| `"Delta SkyMiles"` | 1.0¢ |
| `"Southwest Rapid Rewards"` | 1.0¢ |
| `"United MileagePlus"` | 1.0¢ |
| `"AAdvantage Miles"` | 1.0¢ |
| `"Avios"` | 1.0¢ |
| `"Aeroplan Miles"` | 1.0¢ |
| `"Atmos/Alaska Rewards Miles"` | 1.0¢ |
| `"Flying Blue Miles"` | 1.0¢ |
| `"Free Spirit Points"` | 1.0¢ |
| `"Hilton Honors Points"` | 0.4¢ |
| `"Marriott Bonvoy Points"` | 0.4¢ |
| `"World of Hyatt Points"` | 1.0¢ |
| `"IHG One Rewards Points"` | 0.4¢ |
| `"BofA Points"` | 1.0¢ |
| `"Cash Back"` | 1.0¢ |

#### Step 3 — Add reward rates

```java
// My Bank Signature Visa
list.add(new RewardRate("my_bank_signature", RewardCategory.DINING,    RateType.POINTS, 4.0));
list.add(new RewardRate("my_bank_signature", RewardCategory.GROCERIES, RateType.POINTS, 4.0));
list.add(new RewardRate("my_bank_signature", RewardCategory.TRAVEL,    RateType.POINTS, 3.0));
list.add(new RewardRate("my_bank_signature", RewardCategory.GENERAL,   RateType.POINTS, 1.0));
```

Always include a `GENERAL` catch-all rate. Every card without an explicit rate for a category falls back to its `GENERAL` rate in the Best Card calculations.

**Valid `RewardCategory` values:**

| Value | Spend Category |
|---|---|
| `GENERAL` | All other purchases (catch-all) |
| `DINING` | Restaurants, food delivery |
| `GROCERIES` | Supermarkets, grocery stores |
| `TRAVEL` | General travel (flights, hotels, transit) |
| `TRAVEL_PORTAL` | Booking through card's own travel portal |
| `TRAVEL_HILTON` | Hilton-brand hotels |
| `TRAVEL_MARRIOTT` | Marriott Bonvoy hotels |
| `TRAVEL_IHG` | IHG hotels |
| `TRAVEL_HYATT` | World of Hyatt hotels |
| `TRAVEL_DELTA` | Delta flights |
| `TRAVEL_UNITED` | United flights |
| `TRAVEL_SOUTHWEST` | Southwest flights |
| `TRAVEL_AA` | American Airlines flights |
| `TRAVEL_AEROPLAN` | Air Canada / Aeroplan flights |
| `TRAVEL_BRITISH_AIRWAYS` | British Airways flights |
| `TRAVEL_AER_LINGUS` | Aer Lingus flights |
| `TRAVEL_IBERIA` | Iberia flights |
| `TRAVEL_AIR_FRANCE_KLM` | Air France / KLM flights |
| `TRAVEL_SPIRIT` | Spirit Airlines flights |
| `TRAVEL_ALLEGIANT` | Allegiant Air flights |
| `TRAVEL_ALASKA` | Alaska Airlines flights |
| `TRAVEL_CRUISES` | Cruise lines |
| `GAS` | Gas stations |
| `ENTERTAINMENT` | Streaming, concerts, events |
| `ONLINE_SHOPPING` | Online retail |
| `RENT_MORTGAGE` | Rent and mortgage payments |

**Valid `RateType` values:**

| Value | Use for |
|---|---|
| `POINTS` | Transferable bank points (UR, MR, TYP, etc.) |
| `MILES` | Airline or hotel miles |
| `CASHBACK` | Flat cash back |
| `BILT_CASH` | Bilt card's cash-back earning leg |

**Choice / rotating category** (e.g. Chase Freedom Flex 5% quarterly):

```java
// All rates in the same group share the same choiceGroupId string
list.add(new RewardRate("chase_freedom_flex", RewardCategory.DINING,          RateType.POINTS, 5.0, true, "cff_rotating", false));
list.add(new RewardRate("chase_freedom_flex", RewardCategory.GAS,             RateType.POINTS, 5.0, true, "cff_rotating", false));
list.add(new RewardRate("chase_freedom_flex", RewardCategory.ONLINE_SHOPPING, RateType.POINTS, 5.0, true, "cff_rotating", false));
```

`isChoiceCategory = true` tells the app the user picks one active category at a time from the group. The user selects their active choice from Card Detail.

---

### Part 2 — Adding a Card Benefit

**File:** `data/seed/SeedData.java`

```java
new CardBenefit(
    "my_bank_signature",               // cardDefinitionId
    "Lounge Access Credit",            // name shown in Credits tab
    "$30/month credit for lounge access", // description (optional)
    3000,                              // amountCents: dollar value × 100
    ResetPeriod.MONTHLY,               // MONTHLY | QUARTERLY | SEMI_ANNUALLY | ANNUALLY
    false                              // isCustom — always false for seeded benefits
),
```

**`amountCents` reference:**

| Dollar value | amountCents |
|---|---|
| $10 | `1000` |
| $15 | `1500` |
| $20 | `2000` |
| $25 | `2500` |
| $50 | `5000` |
| $100 | `10000` |
| $300 | `30000` |
| Non-monetary (free night, bonus miles) | `0` |

> Seeded benefits always use `ResetType.CALENDAR` (the default). Users can change the reset type to `ANNIVERSARY` or `CUSTOM` from the benefit's edit screen after adding the card to their portfolio.

---

### Part 3 — Changing Point Valuations

**File:** `data/seed/DefaultPointValuations.java`

```java
new PointValuation(
    "Chase Ultimate Rewards Points", // must match rewardCurrencyName exactly
    1.0,                             // centsPerPoint: value on a fresh install
    1.0                              // defaultCentsPerPoint: value restored by "Reset to Default"
)
```

To update a valuation, change both numbers and bump `SEED_VERSION`. The refresh logic updates `defaultCentsPerPoint` for all existing installs without touching the user's own `centsPerPoint` edits.

To add a brand-new reward currency:
1. Add the entry to `DefaultPointValuations.java`
2. Optionally add transfer partners in `TransferPartnersSeedData.java`
3. Use the exact same string as `rewardCurrencyName` on any card that earns it

---

## Build Notes

- **Hilt 2.54 is incompatible with AGP 9.0.1** — use Hilt 2.59.2 or later
- Java projects use `annotationProcessor` for both Room and Hilt (not KSP/KAPT)
- Room entities with multiple constructors must annotate all but one with `@Ignore`
- Room migration SQL `DEFAULT` values must be mirrored with `@ColumnInfo(defaultValue = "...")` on the entity field, or Room's schema validation will throw at startup
- Navigation XML `long` arguments require the `L` suffix on default values: `android:defaultValue="-1L"`
- `BenefitReminderWorker` uses `@EntryPoint` + `EntryPointAccessors` for Hilt injection (not `@HiltWorker`) due to WorkManager/Hilt compatibility constraints with this build config
- Notification channel ID must be changed (e.g. `benefit_reminders_v2`) if importance level is updated, since Android ignores importance changes on existing channels
- `CategoryDisplayPrefs` stores Best Card category order and visibility in SharedPreferences using the key format `"B:DINING"` (built-in) and `"C:42"` (custom, by ID)
- The benefit reset type `CUSTOM` stores a month (1–12) and day (1–31) as separate nullable `INTEGER` columns on `card_benefits`; period keys use the `"custom-YYYY-MM-DD"` prefix format, consistent with the `"anniv-"` prefix used for anniversary-based benefits

# CCRewards

A US credit card portfolio optimizer for Android. CCRewards helps you track every card you own, find the highest-earning card for each spending category, monitor recurring credits before they expire, and configure point valuations to reflect your personal redemption style.

[![Download APK](https://img.shields.io/badge/Download-APK-brightgreen?logo=android)](https://github.com/MoscoviumG/CCRewards/releases/latest)

> **Requires Android 15 (API 35) or higher.**
> Download the APK, open it on your device, and allow installation from unknown sources when prompted.

---

## Table of Contents

1. [Features Overview](#features-overview)
2. [Supported Cards](#supported-cards)
3. [Using the App](#using-the-app)
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
| **Benefit Reminders** | Daily notifications for credits and welcome bonuses about to expire |
| **Point Valuations** | Per-currency ¢/pt editor powering all effective-return math |
| **Transfer Partner Reference** | In-app lookup of transfer ratios for each transferable currency |
| **Product Change History** | Record card upgrades and downgrades with full account history |
| **Dark / Light / System Theme** | Choose app theme in Settings |

---

## Supported Cards

The app ships with 109 pre-loaded cards (personal + business) across Chase, Amex, Citi, Capital One, Bank of America, US Bank, Wells Fargo, Bilt, Discover, HSBC, and more.

For the full catalog — including every card's reward rates, annual fee, and tracked benefits — see **[CARDS.md](CARDS.md)**.

---

## Using the App

### My Cards

The **My Cards** tab is your card portfolio. Every card you own lives here.

#### Card List

The main view shows all cards you have added, each tile displaying the card name, last four digits, and nickname (if set). Tap any card to open its detail screen. Use the filter button (top-right) to narrow the list by:

- **Type** — Personal, Business
- **Issuer** — Chase, Amex, Citi, Capital One, Bank of America, and more
- **Network** — Visa, Mastercard, Amex, Discover
- **Anniversary month** — surface cards whose annual fee renewal is coming up
- **Card age** — new (< 30 days), under 1 year, 1–3 years, 3+ years

An active-filter badge on the button shows how many filters are on. Tap the button again to clear or adjust them.

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

#### Card Detail

Tap any card in the list to open its full profile.

**Header** shows the card name, issuer, network, annual fee, credit limit, open date, and nickname. A brand-colored strip runs along the top of the card.

**Reward Rates** lists every earn rate on the card (e.g. "Dining: 3x UR Points", "General: 1.5x UR Points"). Tap **Edit Reward Rates** to override any rate — useful when a card has a limited-time promotion or when you want to model a downgraded base rate.

**Welcome Bonus** tracks an active sign-up bonus on this card (see [Welcome Bonus](#welcome-bonus) below).

**Quarterly Bonuses** shows all active rotational/quarterly bonus rates on this card (see [Quarterly Rotational Bonuses](#quarterly-rotational-bonuses) below). Tap **+ Add Quarterly Bonus** to add one.

**Benefits & Credits** lists all statement credits and perks attached to the card. Tap any benefit to view its full usage history or edit it. Tap **+ Add Benefit** to create a custom benefit (e.g. a free night certificate or a lounge access credit).

**Account History** records every product change (upgrade/downgrade) made to this card's underlying account line.

At the bottom: **Product Change** to log a card upgrade or downgrade, and **Delete Card** (with confirmation) to remove the card from your portfolio entirely.

#### Editing Reward Rates

Tap **Edit Reward Rates** on the Card Detail screen. Every spend category is listed with an editable rate field and rate-type chips (Points / Miles / Cash Back). Change any value and tap **Save**. Customized rates are highlighted so you can tell them apart from defaults at a glance. Tap **Add Category** to add a rate for a category not currently listed. Tap **Reset Rates** to restore all values to the seed defaults.

#### Product Change

If you upgrade or downgrade a card (e.g. Chase Sapphire Preferred → Chase Freedom Unlimited), tap **Product Change**. The app shows all other cards from the same issuer. Tap the target card, confirm, and the app:
- Switches the card's definition (new name, rates, and benefits)
- Preserves the original open date and account history
- Records the change in the Account History section with today's date

#### Welcome Bonus

Each card can have one active welcome bonus tracked at a time. In the Card Detail screen, under **Welcome Bonus**, tap **+ Set Welcome Bonus**. In the bottom sheet that appears, enter:

- **Bonus amount** — points/miles count (e.g. 60,000) or dollar value for cash-back cards (e.g. $200)
- **Spend requirement** — minimum spend to earn the bonus (e.g. $4,000)
- **Deadline** — optional; tap to open a date picker. Bonuses past their deadline are automatically hidden from the Best Card banner
- **Show in Best Card** — toggle whether this bonus appears in the Best Card banner (on by default)

Once saved, the Card Detail screen shows the bonus details. Three actions are available:
- **Mark Done** — records the bonus as earned; it disappears from the Best Card banner but remains visible on the card's detail screen
- **Edit** — reopens the bottom sheet pre-filled with current values
- **Remove** — deletes the bonus record after a confirmation dialog

#### Quarterly Rotational Bonuses

Some cards offer time-limited bonus rates on rotating or user-selected categories (e.g. "5% on gas stations through March 31"). Tap **+ Add Quarterly Bonus** on the Card Detail screen to record one. Fill in:

- **Label** — a short name for the bonus period (e.g. "Q1 2026 Gas")
- **End date** — when the bonus expires; defaults to the end of the current quarter
- **Spend limit** — maximum spend eligible for the bonus rate (e.g. $1,500)
- **Categories** — one or more spend categories with their bonus rate and rate type

Once saved, the bonus appears on the Card Detail screen with a progress bar showing how much of the spend limit has been used. You can drag the bar to update usage. Tap **Mark Done** when the limit is reached or the period ends.

Active quarterly bonuses are automatically injected into the **Best Card** rankings for their categories while the bonus is live and spend is available, so the right card always floats to the top.

---

### Best Card

The **Best Card** tab answers the question *"which card should I swipe right now?"* for every spending category.

#### Category Grid

The main view is a 2-column grid of spend category tiles showing cards from your portfolio. Each tile shows:
- The spend category name
- The best card for that category
- The effective return percentage (e.g. "3.00%") — or the raw earn rate (e.g. "3x UR") depending on your Settings display preference
- For non-cash-back cards, a secondary line with the complementary view

The eight built-in categories are: **General, Dining, Groceries, Travel, Gas, Entertainment, Online Shopping, Rent / Mortgage.**

Custom categories you have created appear below the built-in tiles.

#### Travel Group

Tapping the **Travel** tile opens a dedicated Travel screen with 19 sub-category tiles:
- **General Travel** — true general travel cards (CSP, CSR, Amex Platinum, etc.)
- **Travel Portal** — booking through card-specific travel portals
- **Airlines** — Alaska Airlines, American Airlines, Aer Lingus, Air France / KLM, Aeroplan, Allegiant, British Airways, Delta, Iberia, Southwest, Spirit, United
- **Hotels** — Hilton, Hyatt, IHG, Marriott
- **Cruises**

Each sub-category tile shows only cards that actually earn that brand's currency (e.g. the Hilton tile shows only Hilton co-branded cards). General-travel cards do not bleed into brand-specific tiles.

#### Welcome Bonus Banner

When you have active, unachieved welcome bonuses, a **Welcome Bonuses** banner appears at the top of the Best Card screen. Each row shows:
- The card name (with last four digits and nickname if set)
- Bonus details — points/miles amount, reward currency, and effective return estimate
- Spending required and spend progress
- A progress bar showing spend-to-goal completion
- Expiry date (bottom-left) and spend progress amount (bottom-right, format: `$used / $total`)

Tap **Mark Done** on any row to record the bonus as achieved. The row disappears from the banner.

#### Quarterly Bonus Banner

When you have active quarterly rotational bonuses, a **Quarterly Bonuses** section appears on the Best Card screen below the welcome bonus banner. Each row shows:
- The card name and bonus label
- The bonus categories and their rates
- A progress bar for spend against the limit
- Expiry date (bottom-left) and spend progress amount (bottom-right, format: `$used / $total`)

Drag the progress bar to update how much of the spend limit has been used. Tap **Mark Done** when the bonus is exhausted or expired.

#### Tap a Tile → Category Detail

Tapping any tile (other than Travel, which opens the Travel Group) opens a ranked list of all eligible cards for that category, sorted by effective return. Each row shows the card name (with last four and nickname), effective return %, and an "Owned" chip if the card is in your portfolio.

#### Custom Categories

Tap the **+** FAB (bottom-right) to create a custom spending category. Enter a name (e.g. "Apple.com", "Costco", "Pharmacy") and tap **Create**. The app navigates directly to the Custom Category Detail screen.

**Custom Category Detail** shows every card in your portfolio. For each card you can:
- Enter a custom earn rate for this specific category
- Leave it blank to fall back to the card's General rate
- Tap **×** to clear a previously entered override

The ranked recommendations for the custom tile are computed the same way as built-in categories — using your ¢/pt valuations to convert everything to a comparable effective return.

To rename or delete a custom category, use the overflow menu (⋮) on the Custom Category Detail screen.

---

### Credits

The **Credits** tab is your benefit coupon book — every statement credit, free night, and recurring perk across all your cards in one place.

#### Benefit List

Benefits are grouped by card and reset period. Each group header shows the card name (with last four digits and nickname) and how many days until its next reset. Each benefit row shows:
- The benefit name and dollar value (e.g. "$300 / yr", "$15 / mo")
- An **anniversary** badge if this benefit resets on your card's open-date anniversary rather than the calendar year
- A **Used / Not used** toggle switch

Flipping the toggle to **Used** marks the benefit as used for the current period and records today's date. The toggle automatically resets to "Not used" when the new period begins.

**Search** — type in the search bar at the top to filter benefits by name or card.

**Hide Used** — tap the chip to hide all benefits already marked used in the current period, so you can focus on what's left to claim.

#### Benefit Detail

Tap any benefit row to open its detail view. At the top you'll find the name, description, and value. Below that is a **full usage history** — every period since you added the card, showing whether the benefit was used and, if so, the exact date it was marked used. This is useful for tracking annual credits across multiple years or spotting missed monthly credits.

You can also toggle the used status directly from this screen.

#### Adding Custom Benefits

From Card Detail, tap **+ Add Benefit**. Fill in:
- **Name** — what the benefit is (e.g. "Priority Pass Lounge Credit")
- **Description** — optional notes
- **Amount** — dollar value (e.g. $30); enter 0 for non-monetary perks
- **Reset period** — Monthly, Quarterly, Semi-annual, or Annual
- **Reset type** — *Calendar* (resets Jan 1 / Apr 1 / Jul 1 / Oct 1 etc.) or *Anniversary* (resets on your card's open-date anniversary)

#### Reminders

The app sends a daily notification for:
- **Benefit expiry** — benefits that are about to reset unused within your configured threshold
- **Welcome bonus deadlines** — unachieved bonuses within N days of their deadline, with remaining spend shown

Configure reminders in **Settings → Notifications**.

---

### Settings

The **Settings** tab controls how the app calculates and displays reward value, manages reminder notifications, and controls the app's visual theme.

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

#### Transfer Partners

On a currency's detail screen, below the ¢/pt editor, a **Transfer Partners** section lists all airline and hotel programs the points transfer to, along with the transfer ratio (e.g. "1:1", "1:1.5"). This is a reference guide to help you decide what valuation to enter.

#### Appearance

**Theme** — choose between **Light**, **Dark**, or **System default**. The setting is applied immediately and persists across app restarts.

#### Display Mode

**Show effective return %** (default ON): Best Card tiles display the computed percentage (e.g. "3.00%") as the primary value, with the raw earn rate (e.g. "3x UR") shown smaller below.

Turn this OFF to flip the display — raw earn rate becomes primary and the effective % is secondary. Useful if you prefer to see "5x UR" rather than "5.00%" at a glance.

#### Notifications

**Enable reminders** — master toggle for all benefit expiry and welcome bonus notifications. Toggling on sends an immediate test notification to confirm everything is working.

**Days before reset** — how far in advance to start notifying (options: 1, 3, 5, 7, 14, 30 days). At 7 days (default), you'll be notified starting 7 days before a benefit resets if unused, or 7 days before a welcome bonus deadline if the bonus hasn't been earned.

**Reminder time** — the time of day to send the daily check (default 9:00 AM). The app schedules a WorkManager periodic task aligned to this exact hour and minute.

When a benefit notification fires, it lists the benefit name, its value, and how many days remain. When a welcome bonus notification fires, it shows the remaining spend needed and days left. Tapping either notification opens the app.

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
│   └── settings/         # Settings tab — valuations list, currency detail, main settings
├── worker/               # BenefitReminderWorker, WorkManagerScheduler
├── util/                 # DateUtil, CurrencyUtil, PeriodKeyUtil
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
| `user_cards` | Untouched | All owned cards, nicknames, limits, dates |
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

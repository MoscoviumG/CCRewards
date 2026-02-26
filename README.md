# CCRewards

A US credit card analyzer for Android (pure Java, XML views) that helps you manage your card portfolio, find the best card for every spend category, track recurring credits and coupon-book benefits, and configure point valuations per reward currency.

---

## Features

- **My Cards** — Portfolio management with nickname, credit limit, open date, and product-change history
- **Best Card** — Per-category recommender that converts all reward currencies into a unified effective cash-back percentage using configurable ¢/pt valuations
- **Credits** — Coupon-book tracker for recurring benefits (monthly, quarterly, semi-annual, annual) with a toggle-to-mark-used UI and WorkManager reminders 7 days before each reset
- **Settings** — Per-currency ¢/pt editor with transfer partner drill-down and notification toggle

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
│   ├── repository/       # CardRepository, BenefitRepository, RewardRateRepository
│   └── seed/             # SeedData, DefaultPointValuations, TransferPartnersSeedData
├── di/                   # DatabaseModule (Hilt)
├── ui/
│   ├── mycards/          # My Cards tab (8 fragments)
│   ├── bestcard/         # Best Card tab (2 fragments)
│   ├── credits/          # Credits tab (2 fragments)
│   └── settings/         # Settings tab (3 fragments)
├── worker/               # BenefitReminderWorker, WorkManagerScheduler
├── util/                 # DateUtil, CurrencyUtil, PeriodKeyUtil
├── MainActivity.java
└── CCRewardsApp.java
```

---

## Seed Database Developer Guide

All seed data lives in three files under `data/seed/`. Changes take effect the next time the app is **freshly installed** (or app data is cleared), because Room's seed callback only runs on database creation.

### Clearing the Database After Changes

Room seeds only once. After any seed file change you must wipe app data before running:

```bash
adb shell pm clear com.example.ccrewards
```

Or on device/emulator: **Settings → Apps → CCRewards → Storage → Clear Data**, then relaunch.

---

### Part 1 — Adding a New Card

**File:** `data/seed/SeedData.java`

#### Step 1 — Define a color constant

At the top of the file, add a brand color using the `argb()` helper (Alpha, Red, Green, Blue, each 0–255):

```java
private static final long MY_BANK_BLUE = argb(255, 0, 84, 166);
private static final long MY_BANK_DARK = argb(255, 0, 40,  90);
```

#### Step 2 — Add the `CardDefinition`

Inside `getCardDefinitions()`, add a new line in the appropriate issuer section:

```java
new CardDefinition(
    "my_bank_signature",            // unique snake_case ID — never reuse or change once set
    "My Bank Signature Visa",       // display name shown in the app
    "My Bank",                      // issuer name
    "Visa",                         // network: "Visa" | "Mastercard" | "Amex" | "Discover"
    95,                             // annual fee in whole dollars (0 = no fee)
    false,                          // isCustom — always false for seeded cards
    false,                          // isBusinessCard — true for business cards
    MY_BANK_BLUE,                   // primary card color (used for the color strip in lists)
    MY_BANK_DARK,                   // secondary card color
    "Chase Ultimate Rewards Points" // rewardCurrencyName — must EXACTLY match a name in DefaultPointValuations
),
```

**Valid `rewardCurrencyName` values:**

| Name | Default ¢/pt |
|---|---|
| `"Chase Ultimate Rewards Points"` | 2.0 |
| `"Amex Membership Rewards Points"` | 2.2 |
| `"Capital One Miles"` | 1.7 |
| `"Citi ThankYou Points"` | 1.8 |
| `"Bilt Points"` | 2.2 |
| `"Delta SkyMiles"` | 1.2 |
| `"Southwest Rapid Rewards"` | 1.5 |
| `"United MileagePlus"` | 1.5 |
| `"AAdvantage Miles"` | 1.5 |
| `"Hilton Honors Points"` | 0.6 |
| `"Marriott Bonvoy Points"` | 0.9 |
| `"World of Hyatt Points"` | 1.7 |
| `"Atmos/Alaska Rewards Miles"` | 1.8 |
| `"Cash Back"` | 1.0 |

To add an entirely new reward currency, see [Part 3](#part-3--changing-point-valuations).

#### Step 3 — Add reward rates

Inside `getRewardRates()`, add one `list.add()` per spend category. Always include `GENERAL` as the catch-all base rate:

```java
// My Bank Signature Visa
list.add(new RewardRate("my_bank_signature", RewardCategory.DINING,    RateType.POINTS, 4.0));
list.add(new RewardRate("my_bank_signature", RewardCategory.GROCERIES, RateType.POINTS, 4.0));
list.add(new RewardRate("my_bank_signature", RewardCategory.TRAVEL,    RateType.POINTS, 3.0));
list.add(new RewardRate("my_bank_signature", RewardCategory.GENERAL,   RateType.POINTS, 1.0));
```

**Valid `RewardCategory` values:**

| Value | Spend Category |
|---|---|
| `DINING` | Restaurants, food delivery |
| `GROCERIES` | Supermarkets, grocery stores |
| `TRAVEL` | Flights, hotels, transit |
| `GAS` | Gas stations |
| `ENTERTAINMENT` | Streaming, concerts, events |
| `ONLINE_SHOPPING` | Online retail |
| `RENT_MORTGAGE` | Rent and mortgage payments |
| `GENERAL` | All other purchases (catch-all) |

**Valid `RateType` values:**

| Value | Use for |
|---|---|
| `POINTS` | Transferable points (UR, MR, TYP, etc.) |
| `MILES` | Airline/hotel miles |
| `CASHBACK` | Flat cash back |
| `BILT_CASH` | Bilt cards' cash-back earning leg only |

**Standard rate** (no rotating/choice category):
```java
new RewardRate(cardId, RewardCategory.X, RateType.X, rateMultiplier)
```

**Choice/rotating category** (e.g. Freedom Flex's 5% rotating quarters):
```java
// Give all rates in the same rotating group the same choiceGroupId string
new RewardRate(cardId, RewardCategory.DINING,          RateType.POINTS, 5.0, true, "my_card_rotating", false)
new RewardRate(cardId, RewardCategory.GAS,             RateType.POINTS, 5.0, true, "my_card_rotating", false)
new RewardRate(cardId, RewardCategory.ONLINE_SHOPPING, RateType.POINTS, 5.0, true, "my_card_rotating", false)
```

Setting `isChoiceCategory = true` signals the app that the user picks one active category at a time from the group.

---

### Part 2 — Adding a Credit Card Benefit

**File:** `data/seed/SeedData.java`

Inside `getCardBenefits()`, add a new entry. The `cardDefinitionId` must already exist in `getCardDefinitions()`:

```java
new CardBenefit(
    "my_bank_signature",                               // cardDefinitionId
    "Lounge Access Credit",                            // name shown in Credits tab
    "$30/month credit for Priority Pass lounge access",// description
    3000,                                              // amountCents: dollar value × 100
    ResetPeriod.MONTHLY,                               // how often the benefit resets
    false                                              // isCustom — always false for seeded benefits
),
```

**Valid `ResetPeriod` values:**

| Value | Resets |
|---|---|
| `ResetPeriod.MONTHLY` | Every calendar month |
| `ResetPeriod.QUARTERLY` | Every 3 months |
| `ResetPeriod.SEMI_ANNUALLY` | Every 6 months |
| `ResetPeriod.ANNUALLY` | Once per year |

**`amountCents` reference:**

| Dollar amount | amountCents |
|---|---|
| $10/month | `1000` |
| $30/month | `3000` |
| $50 semi-annual | `5000` |
| $100 annual | `10000` |
| $189 annual | `18900` |
| $300 annual | `30000` |
| Non-monetary (bonus miles, free night, etc.) | `0` |

---

### Part 3 — Changing Point Valuations

**File:** `data/seed/DefaultPointValuations.java`

Each entry has two ¢/pt values:

```java
new PointValuation(
    "Chase Ultimate Rewards Points", // currency name — must match rewardCurrencyName in cards
    2.0,                             // centsPerPoint: starting value on a fresh install
    2.0                              // defaultCentsPerPoint: value restored by "Reset to Default"
)
```

**To update an existing valuation's baseline**, change both numbers:

```java
// Updating Chase UR to reflect a new consensus valuation
new PointValuation("Chase Ultimate Rewards Points", 2.05, 2.05),
```

**To set a different starting value vs. reset target** (e.g. a conservative default but higher starting point):

```java
new PointValuation("Amex Membership Rewards Points", 2.0, 2.2),
//                                                    ^     ^
//                                        fresh install   reset button restores this
```

**To add a brand-new reward currency:**

1. Add the entry to `DefaultPointValuations.java`:
    ```java
    new PointValuation("My New Currency", 1.6, 1.6),
    ```

2. Optionally add transfer partners in `TransferPartnersSeedData.java` (see file for the existing pattern).

3. Use the exact same string as the `rewardCurrencyName` on any card that earns it.

---

## Build Notes

- **Hilt 2.54 is incompatible with AGP 9.0.1** — use Hilt 2.59.2+
- Java projects use `annotationProcessor` for both Room and Hilt (not KSP/KAPT)
- Room entities with multiple constructors must annotate all but one with `@Ignore`
- Navigation XML `long` arguments require the `L` suffix on default values: `android:defaultValue="-1L"`
# Deep Research on All Current Chase (US) Personal Credit Cards  
**Assumption scope:** Cards available to new applicants **as of 2026-02-25** (America/Chicago), included **only if a live official Chase product/terms page was accessible** on that date. citeturn5view0

## Executive summary

Chase’s publicly listed **US personal credit-card lineup** (available to new applicants) spans **no‑annual‑fee cash‑back cards**, **mid‑fee travel cards**, **high‑fee “coupon-book” premium travel**, and a large set of **co‑branded airline/hotel cards** (notably entity["company","United Airlines","us airline"] and entity["company","Southwest Airlines","us airline"], plus hotel brands). citeturn5view0turn7view0turn7view1turn7view2turn7view3turn7view4turn6view4turn6view6

Within Chase’s transferable-points ecosystem, **Chase Ultimate Rewards** is valued by entity["organization","The Points Guy","points website"] at **2.05¢ per point (Feb 2026)**, which drives the largest “valuation-based” welcome-offer math in this report. citeturn8view0

Key tier takeaways:

- **Premium tier (very high annual fee):** The **new** Chase Sapphire Reserve shows a **$795 annual fee** and a dense cluster of time‑limited statement credits and partner benefits (semiannual dining credits, monthly platform credits, memberships) that can outweigh the fee only if you actively use those partners. citeturn7view0turn9view0turn9view3turn8view0  
- **Mid-fee transferable points:** Chase Sapphire Preferred remains the main “entry” into high‑leverage Ultimate Rewards redemptions via **1:1 point transfers** to multiple airline/hotel partners shown on Chase’s page. citeturn10view0  
- **No-fee earners (core cash back):** Freedom Unlimited and Freedom Flex provide broad everyday earn (3% dining/drugstores; travel via Chase Travel; Flex adds rotating 5% categories) suitable for pairing with a Sapphire card if you want transferable points value. citeturn24view0turn24view3turn24view4  
- **Most valuable credits (on paper):** Among airline cards, the refreshed United line advertises large totals of annual partner credits (rideshare, Instacart, hotel portals, car rental portals, and entity["company","JSX","air carrier"] airfare credits) with explicit caps and end dates in the card pages. citeturn13view0turn15view2turn15view5  

> **Important pattern:** Many partner credits are **not “free money”**—they require targeted merchants/portals, enrollment, spend thresholds, and/or expire (several benefits end **12/31/2027** on multiple cards). citeturn9view0turn13view0turn15view2turn15view5

## Methodology and assumptions

Primary verification relied on **official Chase US product pages** (and, where present, linked program agreements or “Guide to Benefits” references on those pages). The authoritative “universe” of cards came from Chase’s **Browse → All Cards** listing and brand category pages, filtered to **personal** cards. citeturn5view0turn7view0turn7view1turn7view2turn7view3turn7view4

Secondary sources were used for cross-checking offers and for point valuations:
- entity["organization","US Credit Card Guide","credit card website"] for issuer/offer monitoring and card review tracking (used as “secondary confirmation,” not as final authority). citeturn23view0  
- entity["organization","The Points Guy","points website"] for **Feb 2026 valuations** of points and miles used in “net bonus value” estimates. citeturn8view0  

**Date of access:** 2026-02-25 (America/Chicago), per user instruction.

**Definitions and conventions:**
- **Card type:** All cards in this report are treated as **credit (revolving)** unless Chase’s page clearly indicates otherwise; Chase personal cards shown here are presented as credit cards with APR disclosures. citeturn7view0turn10view0turn13view0turn17view0turn22view0  
- **Included cards:** Included if a live Chase product/terms page was accessible and the card appears on Chase’s browse listings/brand pages. citeturn5view0  
- **Net bonus value:** Where calculable, computed using entity["organization","The Points Guy","points website"] valuations (Feb 2026) multiplied by the points/miles in the welcome offer, then subtracting the **first-year annual fee** (assuming no credits offset, ignoring time value of money and opportunity cost). citeturn8view0  
- **Ambiguity handling:** If a required field (e.g., annual fee) was not captured in retrieved text for a specific product page, it is explicitly marked **“Unverified in captured sources”** in the tables rather than guessed.

## Card lineup and tier map

### Current Chase personal cards (new applicants) included in this report

This list is derived from Chase’s “All Cards” browse experience and brand pages (personal cards only). citeturn5view0turn7view0turn7view1turn7view2turn7view3turn7view4turn6view4turn6view6turn6view3turn11view7turn6view8turn6view10turn6view11turn6view9

Transferable / cash-back core:
- The New Chase Sapphire Reserve® Credit Card citeturn7view0turn9view3  
- Chase Sapphire Preferred® Credit Card citeturn7view0turn10view0  
- Chase Freedom Unlimited® citeturn24view0  
- Chase Freedom Flex® citeturn24view3turn24view4  
- Chase Freedom Rise® citeturn25view0  
- Slate® Credit Card citeturn24view8turn24view9  

Airline co-brands:
- United℠ Explorer Card citeturn11view0turn13view0  
- United Quest℠ Card citeturn11view0turn15view0  
- United Gateway℠ Card citeturn12view3turn16view0  
- United Club℠ Card citeturn12view3turn15view3  
- Southwest Rapid Rewards® Plus Credit Card citeturn12view4turn17view0  
- Southwest Rapid Rewards® Premier Credit Card citeturn12view5turn19view3  
- Southwest Rapid Rewards® Priority Credit Card citeturn12view5turn19view0  
- British Airways Visa Signature® Credit Card citeturn11view5  
- Aer Lingus Visa Signature® Credit Card citeturn12view6  
- Iberia Visa Signature® Credit Card citeturn12view6  
- Aeroplan® Card citeturn6view9turn23view0  

Hotel co-brands:
- Marriott Bonvoy Boundless® Credit Card citeturn12view2turn22view0  
- Marriott Bonvoy Bountiful® Credit Card citeturn12view2turn25view7  
- Marriott Bonvoy Bold® Credit Card citeturn11view2turn25view11  
- IHG One Rewards Premier Credit Card citeturn12view0turn25view12  
- IHG One Rewards Traveler Credit Card citeturn12view0turn25view13  
- World of Hyatt Credit Card citeturn11view4turn23view0  

Retail / lifestyle co-brands:
- Amazon Prime Visa citeturn23view0turn11view6  
- Amazon Visa citeturn11view6  
- Disney® Inspire Visa® Card citeturn11view7  
- Disney® Premier Visa® Card citeturn12view7  
- Disney® Visa® Card citeturn11view7  
- DoorDash Rewards Mastercard citeturn6view10turn25view3  
- Instacart Credit Card citeturn6view11turn25view4  

### Tier and decision flow (conceptual)

```mermaid
flowchart TD
  A[Primary goal?] --> B{Transferable points?}
  B -->|Yes| C{High annual fee acceptable?}
  C -->|Yes| SR[Sapphire Reserve: premium credits + lounge]
  C -->|No| SP[Sapphire Preferred: mid-fee transfer access]
  B -->|No| D{Prefer simple cash back?}
  D -->|Yes| E[Freedom Unlimited / Freedom Flex]
  D -->|No| F{Brand loyal (airline/hotel/retail)?}
  F --> UA[United cards]
  F --> SW[Southwest cards]
  F --> HT[Hotel cards: Marriott / IHG / Hyatt]
  F --> RT[Retail/lifestyle: Amazon / Disney / DoorDash / Instacart]
```

## Comparative tables

### Point & mile valuations used for net bonus estimates

All valuation inputs below are entity["organization","The Points Guy","points website"] **February 2026** valuations. citeturn8view0  

- Chase Ultimate Rewards: **2.05¢** citeturn8view0  
- United MileagePlus: **1.5¢** citeturn8view0  
- Southwest Rapid Rewards: **1.3¢** citeturn8view0  
- Avios: **1.4¢** citeturn8view0  
- Air Canada Aeroplan: **1.4¢** citeturn8view0  
- World of Hyatt: **1.7¢** citeturn8view0  
- Marriott Bonvoy: **0.7¢** citeturn8view0  
- IHG One Rewards: **0.5¢** citeturn8view0  

### Annual fee vs welcome offer vs estimated bonus value

**Interpretation notes:**  
- “Bonus value” uses TPG valuations where the offer is denominated in points/miles. citeturn8view0  
- Certificate-style offers are shown as **max-point-equivalent** only where the Chase page explicitly states a points redemption cap (e.g., “up to 50,000 points per night”). citeturn22view0turn25view11  
- “Net bonus value” = bonus value − first-year annual fee (credits ignored). citeturn8view0  

| Card (personal) | Rewards currency | Annual fee (first year) | Welcome offer (as shown on Chase pages) | Est. bonus value | Est. net bonus value |
|---|---:|---:|---|---:|---:|
| Sapphire Reserve | Ultimate Rewards | $795 citeturn7view0turn9view3 | 125,000 points after $6,000 / 3 mo citeturn7view0 | ~$2,562.50 citeturn8view0 | ~$1,767.50 |
| Sapphire Preferred | Ultimate Rewards | $95 citeturn10view0 | 75,000 points after $5,000 / 3 mo citeturn10view0 | ~$1,537.50 citeturn8view0 | ~$1,442.50 |
| United Explorer | United miles | $0 intro (yr 1), then $150 citeturn11view0turn13view0 | 70,000 miles after $3,000 / 3 mo (+10,000 miles after adding AU) citeturn13view0 | ~$1,050 (base) citeturn8view0 | ~$1,050 (yr 1) |
| United Quest | United miles | $350 citeturn15view0 | 80,000 miles + 3,000 PQP after $4,000 / 3 mo (+10,000 miles after adding AU) citeturn15view0 | ~$1,200 (miles only) citeturn8view0 | ~$850 |
| United Gateway | United miles | $0 citeturn14view8turn16view0 | 30,000 miles after $1,000 / 3 mo (+10,000 miles after adding AU) citeturn16view0 | ~$450 (base) citeturn8view0 | ~$450 |
| United Club | United miles | $695 citeturn15view3turn15view4 | 90,000 miles after $5,000 / 3 mo (+10,000 miles after adding AU) citeturn15view3 | ~$1,350 (base) citeturn8view0 | ~$655 |
| Southwest Plus | Southwest points | $99 citeturn17view0 | Companion Pass through 2/28/27 + 20,000 points after $3,000 / 3 mo citeturn17view0 | ~$260 (points only) citeturn8view0 | ~$161 |
| Southwest Priority | Southwest points | $229 citeturn19view0 | Companion Pass through 2/28/27 + 40,000 points after $5,000 / 3 mo citeturn19view0 | ~$520 (points only) citeturn8view0 | ~$291 |
| Southwest Premier | Southwest points | $149 citeturn19view3 | Companion Pass through 2/28/27 + 30,000 points after $4,000 / 3 mo citeturn19view3 | ~$390 (points only) citeturn8view0 | ~$241 |
| Marriott Boundless | Marriott points (via 5× FNA cap) | $95 citeturn22view0 | 5 Free Night Awards (≤50k/night) after $3,000 / 3 mo citeturn22view0 | ~$1,750 (max-cap) citeturn8view0turn22view0 | ~$1,655 |
| Marriott Bold | Marriott points (via 2× FNA cap) | Unverified in captured sources | 2 Free Night Awards (≤50k/night) after $1,000 / 3 mo citeturn25view11 | ~$700 (max-cap) citeturn8view0turn25view11 | N/A |
| IHG Premier | IHG points | $99 citeturn12view0 | 175,000 points after $5,000 / 3 mo citeturn12view0 | ~$875 citeturn8view0 | ~$776 |
| IHG Traveler | IHG points | $0 citeturn12view0 | Up to 120,000 points (tiered) citeturn12view0 | ~$600 (max) citeturn8view0 | ~$600 |
| British Airways Visa | Avios | $95 citeturn11view5 | 75,000 Avios after $5,000 / 3 mo citeturn11view5 | ~$1,050 citeturn8view0 | ~$955 |
| Aer Lingus Visa | Avios | $95 citeturn12view6 | 75,000 Avios after $5,000 / 3 mo citeturn12view6 | ~$1,050 citeturn8view0 | ~$955 |
| Iberia Visa | Avios | $95 citeturn12view6 | 75,000 Avios after $5,000 / 3 mo citeturn12view6 | ~$1,050 citeturn8view0 | ~$955 |
| Disney Inspire Visa | Disney gift card + statement credit | $149 citeturn11view7 | $300 gift card upon approval + $300 statement credit after $1,000 / 3 mo citeturn11view7 | $600 | $451 |
| Disney Premier Visa | Disney gift card + statement credit | $49 citeturn12view7 | $200 gift card upon approval + $100 statement credit after $500 / 3 mo citeturn12view7 | $300 | $251 |

> Offers and annual fees can change, and some cards in Chase’s browse list were not fully re-extracted in captured text for every field; those are shown as “Unverified in captured sources” in the detailed catalog below. citeturn5view0turn23view0

## Card detail catalog

**Format (per user request):** Card name; card type; signup bonus; earning rates; annual fee; credits/statement benefits (frequency / item / amount / enrollment / limits); notable benefits (lounges, protections, status, transfer partners).  
If a field is not clearly available in captured official page text, it is noted explicitly.

### Core transferable points and cash back (Chase-branded)

| Card | Type | Welcome offer | Ongoing earn rates (as shown) | Annual fee | Credits / statement benefits (structured) | Other notable benefits |
|---|---|---|---|---:|---|---|
| Sapphire Reserve | Credit | 125,000 points after $6,000 / 3 months citeturn7view0 | 8x Chase Travel; 4x flights booked directly; 4x hotels booked directly; 3x dining; 1x other citeturn1view1turn9view3 | $795 citeturn7view0turn9view3 | **Semiannual**: Sapphire Exclusive Tables dining credit (via entity["company","OpenTable","restaurant reservations"]) — up to $150 Jan–Jun + up to $150 Jul–Dec (max $300/yr) — enrollment not stated — must dine at participating restaurants citeturn9view0  \| **Annual (time-limited)**: Apple TV + Apple Music subscriptions — stated $288 annual value — runs through 6/22/2027 — enrollment/activation not stated citeturn9view0  \| **Annual (time-limited)**: DashPass membership (entity["company","DoorDash","food delivery"]) — stated $120 value for 12 months — activation required by 12/31/2027 citeturn9view0  \| **Monthly (time-limited)**: DoorDash promos — up to $25/mo (structure shown) — available through 12/31/2027 citeturn9view0  \| **Semiannual (time-limited)**: StubHub/viagogo statement credits — up to $150 Jan–Jun + up to $150 Jul–Dec (max $300/yr) — activation required — through 12/31/2027 citeturn9view0  \| **Monthly (time-limited)**: entity["company","Lyft","rideshare company"] credits — up to $10/mo through 9/30/2027 — plus 5x points on Lyft through 9/30/2027 citeturn9view0  \| **Monthly (time-limited)**: entity["company","Peloton","fitness company"] membership credits — $10/mo through 12/31/2027 — activation required — plus 10x points on eligible equipment/accessories >$150 through 12/31/2027 citeturn9view0  \| **Every 4 years**: Global Entry / TSA PreCheck / NEXUS — up to $120 statement credit citeturn9view0 | Priority Pass Select lounge membership (stated $469 value) + access to Chase Sapphire Lounge network with guests (per page) citeturn9view0 |
| Sapphire Preferred | Credit | 75,000 points after $5,000 / 3 months citeturn10view0 | 5x travel via Chase Travel (with exclusions noted); 2x other travel; 3x dining; 3x online grocery; 3x select streaming; 1x other citeturn10view0 | $95 citeturn10view0 | **Annual**: Chase Travel hotel credit — up to $50 each account anniversary year — statement credits for hotel stays purchased through Chase Travel citeturn10view0  \| **Annual (time-limited)**: DashPass membership — 12 months, activate by 12/31/2027 citeturn10view0  \| **Monthly (time-limited)**: DoorDash promo — $10/mo ($120/yr) through 12/31/2027 citeturn10view0 | 1:1 point transfer partners listed on Chase page (airline + hotel partners) citeturn10view0  \| Travel protections shown (trip cancellation/interruption, baggage delay, trip delay, primary rental coverage, purchase protection, extended warranty) and linked “Guide to Benefits” citeturn10view0 |
| Freedom Unlimited | Credit | Unverified in captured sources (commonly advertised as a $200 cash back bonus; verify on offer details) | 3% dining; 3% drugstores; 5% travel via Chase Travel; 1.5% all other citeturn24view0turn24view2 | Unverified in captured sources (commonly advertised as $0; verify) | No recurring statement credits shown in captured text excerpts | 0% intro APR described on page (15 months on purchases & balance transfers) citeturn24view1 |
| Freedom Flex | Credit | Unverified in captured sources (commonly advertised as $200 cash back; verify) | 5% travel via Chase Travel; 3% dining; 3% drugstores; 1% other (plus rotating 5% categories by quarter; calendar referenced) citeturn24view3turn24view4 | Unverified in captured sources (commonly advertised as $0; verify) | No recurring statement credits shown in captured text excerpts | Rotating 5% quarterly categories with a referenced “2026 calendar” section on page citeturn24view4 |
| Freedom Rise | Credit | No welcome bonus shown in captured text excerpts | 1.5% cash back on all purchases citeturn25view0 | Unverified in captured sources | No credits shown | Positioned for building credit while earning cash back citeturn25view0 |
| Slate | Credit | No bonus shown | No rewards (page emphasizes intro APR) citeturn24view8turn24view9 | $0 annual fee citeturn24view9 | No credits | 0% intro APR for 21 months on purchases & balance transfers (then variable APR) citeturn24view8 |

### Airline co-branded cards (selected high-detail extraction)

#### United personal cards (official credit + partner credits)

| Card | Welcome offer | Earn rates (high-level, as shown) | Annual fee | Key credits/statement benefits (structured) | Notable benefits |
|---|---|---|---:|---|---|
| United Explorer | 70,000 miles after $3,000 / 3 mo + 10,000 miles after adding AU (timing shown) citeturn13view0 | 2x dining; 2x hotels booked direct; 2x eligible United purchases; 1x other; “up to 7x on United flights” (member + card stacking described) citeturn13view0 | $0 intro annual fee year 1, then $150 citeturn13view0turn11view0 | **Annual (spend-gated):** United TravelBank cash — $100 after $10,000 spend / calendar year — no enrollment stated — TravelBank rules apply citeturn13view0  \| **Anniversary year:** United Hotels statement credits — $50 on 1st + $50 on 2nd prepaid hotel stay (up to $100) — no enrollment stated citeturn13view0  \| **Monthly (calendar year):** Rideshare credits — up to $5/mo (up to $60/yr) — **yearly opt-in required** citeturn13view0  \| **Anniversary year:** Avis/Budget TravelBank — $25 for 1st + $25 for 2nd rental (up to $50) via cars.united.com citeturn13view0  \| **Monthly (calendar year, ends 12/31/27):** Instacart credits — $10/mo (up to $120/yr) citeturn13view0  \| **Annually:** JSX flight credits — up to $100 statement credit citeturn13view0 | Free first checked bag (primary + one companion), priority boarding, 2 United Club one-time passes per year, Global Entry/TSA PreCheck/NEXUS fee credit up to $120 every 4 years citeturn13view0 |
| United Quest | 80,000 miles + 3,000 PQP after $4,000 / 3 mo + 10,000 miles after adding AU citeturn15view0 | 3x United purchases; 2x other travel; 2x dining; 2x select streaming; 5x on prepaid “Renowned Hotels and Resorts” portal; 1x other citeturn15view0turn15view2 | $350 citeturn15view0 | **Anniversary:** United travel credit — $200 (shown as received after account opening and each anniversary) citeturn15view2  \| **Anniversary year:** Renowned Hotels & Resorts credits — up to $150 citeturn15view2turn14view2  \| **Calendar year (monthly cadence):** Rideshare credits — up to $100/yr; **yearly opt-in required**; monthly cap pattern shown citeturn14view2turn15view2  \| **Anniversary year:** Avis/Budget TravelBank — up to $80 (two $40 credits) citeturn14view2turn15view2  \| **Calendar year (monthly cadence; ends 12/31/27):** Instacart credits — up to $180/yr (monthly structure shown) citeturn14view2turn15view2  \| **Annually:** JSX flight credits — up to $150 citeturn14view2turn15view2 | 10,000‑mile award flight discount (anniversary and spend-based variants shown), 2 free checked bags (primary + companion), PQP earning structure shown citeturn15view2turn15view1 |
| United Gateway | 30,000 miles after $1,000 / 3 mo + 10,000 miles after adding AU citeturn16view0 | 2x at gas stations; 2x local transit/commuting; 2x eligible United purchases (headline); 1x other citeturn16view0 | $0 citeturn14view8turn16view0 | **Per purchase:** 25% back as a statement credit on United inflight food/beverage/Wi‑Fi and Club premium drinks when paid with the card citeturn16view0turn16view1 | Earn 2 checked bags after $10,000 spend / calendar year (benefit described) citeturn16view0  \| 0% intro APR for 12 months on purchases shown citeturn14view8 |
| United Club | 90,000 miles after $5,000 / 3 mo + 10,000 miles after adding AU citeturn15view3 | 4x United purchases; 2x travel; 2x dining; 5x prepaid Renowned Hotels & Resorts; 1x other citeturn15view4turn15view5 | $695 citeturn15view3turn15view4 | **Annual:** Renowned Hotels & Resorts credits — up to $200/yr citeturn15view5  \| **Calendar year:** Rideshare credits — up to $150/yr — enroll required citeturn15view5  \| **Annual:** Avis/Budget TravelBank — up to $100/yr citeturn15view5  \| **Calendar year (monthly cadence):** Instacart credits — up to $240/yr citeturn15view5  \| **Annual:** JSX flight credits — up to $200/yr citeturn15view5 | United Club membership described (including guest access framing) citeturn15view4turn15view5 |

#### Southwest personal cards (refreshed benefits shown)

| Card | Welcome offer | Earn rates (as shown) | Annual fee | Credits/statement benefits (structured) | Notable benefits |
|---|---|---|---:|---|---|
| Southwest Plus | Companion Pass through 2/28/27 + 20,000 points after $3,000 / 3 mo citeturn17view0 | 2x Southwest purchases; 2x gas + grocery on first $5,000 combined / anniversary year; 1x other citeturn17view0 | $99 citeturn17view0 | **Per purchase:** 25% back on inflight purchases (drinks/Wi‑Fi) as statement credit citeturn17view0  \| **Annual (time-limited):** Complimentary DashPass for 12 months when activated during promo window (terms shown) citeturn17view0  \| **Quarterly (time-limited):** Up to $10 off one qualifying non‑restaurant DoorDash order per quarter after DashPass activation (terms shown) citeturn17view0 | Anniversary points (3,000) and discount code shown; boarding and seat-related benefits described on page citeturn17view0 |
| Southwest Priority | Companion Pass through 2/28/27 + 40,000 points after $5,000 / 3 mo citeturn19view0 | 4x Southwest purchases; 2x gas + restaurants; 1x other citeturn19view0 | $229 citeturn19view0 | **Per purchase:** 25% back on inflight purchases (listed in benefits section) citeturn21view0  \| **Annual (time-limited):** Complimentary DashPass for a year; activate by 12/31/2027 citeturn20view6  \| **Quarterly (time-limited):** Up to $10 off quarterly on non‑restaurant DoorDash orders through 12/31/2027 citeturn20view6turn20view7 | Anniversary points (7,500); seat-related benefits (Preferred seat at booking + Extra Legroom upgrades in timing window) described on page citeturn21view0turn21view1 |
| Southwest Premier | Companion Pass through 2/28/27 + 30,000 points after $4,000 / 3 mo citeturn19view3 | 3x Southwest purchases; 2x grocery + restaurants on first $8,000 combined / year; 1x other citeturn19view3turn18view8 | $149 citeturn19view3turn18view7 | **Per purchase:** 25% back on inflight purchases (listed) citeturn19view4 | Anniversary points (6,000) and anniversary flight discount code described on page citeturn19view3turn18view9 |

### Hotel co-branded (Marriott + IHG extracted)

#### Marriott Bonvoy personal cards

| Card | Welcome offer | Earn rates (as shown) | Annual fee | Credits/statement benefits (structured) | Notable benefits |
|---|---|---|---:|---|---|
| Marriott Boundless | 5 Free Night Awards (≤50k/night) after $3,000 / 3 mo + limited 2026 airline statement credits promo citeturn22view0 | 6x Marriott (plus member/status stacking described); 3x grocery+gas+dining on first $6,000 combined annually; 2x other citeturn22view0 | $95 citeturn22view0 | **Semiannual in 2026 only (spend-gated):** Airline statement credits — $50 after $250 spend Jan–Jun + $50 after $250 spend Jul–Dec (max $100), activation/registration rules shown citeturn22view0 | Annual Free Night Award after anniversary (≤35k points cap) citeturn22view0  \| Automatic Silver Elite, 15 Elite Night Credits, spend path to Gold shown citeturn22view0 |
| Marriott Bountiful | 85,000 points after $4,000 / 3 mo (from Chase brand page) citeturn12view2 | 6x Marriott + member/status stacking described; 4x grocery+dining on first $15,000 combined annually; 2x other citeturn25view7 | $250 (from Chase brand page) citeturn12view2 | No statement credits captured in retrieved text excerpts | Free Night Award term language appears on page, but the exact earning trigger was not captured in retrieved text excerpts citeturn25view8 |
| Marriott Bold | 2 Free Night Awards (≤50k/night) after $1,000 / 3 mo citeturn25view11 | “Up to 14X at Marriott” stacking described; base earn detail partially captured citeturn25view10 | Unverified in captured sources | No statement credits shown | Welcome offer is certificate-based (2 FNAs) with ≤50k/night stated cap citeturn25view11 |

#### IHG One Rewards personal cards

| Card | Welcome offer | Earn rates (captured) | Annual fee | Credits/statement benefits (structured) | Notable benefits |
|---|---|---|---:|---|---|
| IHG Premier | 175,000 points after $5,000 / 3 mo citeturn12view0 | “Up to 26X total per $1 at IHG” shown on brand page; detailed earn not fully re-extracted here citeturn12view0 | $99 citeturn12view0 | **Anniversary year:** Anniversary Free Night with “current point redemption cap of 40,000 points,” and ability to top up with points described citeturn25view12 | Page includes a “$100 STATEMENT CREDIT” header, but the detailed terms were not captured in retrieved excerpts citeturn25view12 |
| IHG Traveler | Up to 120,000 points (tiered: 90k after $2,000 / 3 mo plus additional earning component described) citeturn12view0 | 3x dining, utilities, select streaming, gas; 2x all other purchases (as shown) citeturn25view13 | $0 citeturn12view0 | No statement credits shown | Automatic Silver Elite Status as long as cardmember citeturn25view13 |

### Avios, Disney, DoorDash, Instacart, Amazon, Hyatt, Aeroplan (incomplete extraction caveat)

The cards below appear on Chase’s brand pages and/or verified secondary tracking, but not all required fields were fully re-extracted into captured text within the tool-call budget. They are still included because they are present on Chase’s accessible brand/product pages as of the access date. citeturn5view0turn11view5turn11view7turn6view9turn6view10turn6view11turn23view0  

- **British Airways / Aer Lingus / Iberia Visa Signature (Avios):** Brand page shows 75,000 Avios after $5,000 / 3 mo and the earn pattern (3x on those airlines’ flight purchases, 2x hotels, 1x other), plus $95 annual fee for at least British Airways and Aer Lingus listings. citeturn11view5turn12view6  
- **Disney Inspire / Disney Premier / Disney Visa:** Brand page shows offer structure for Inspire and Premier (gift card upon approval + statement credit after spend) and shows earn rates by category; Inspire annual fee $149; Premier annual fee $49. The Disney Visa details were not fully re-extracted. citeturn11view7turn12view7  
- **DoorDash Rewards Mastercard:** Page shows free DashPass for a year and a DashPass “anniversary bonus” with $10,000 annual spend. Earn shown includes 4% back on DoorDash/Caviar and 3% on dining purchased directly from a restaurant; other earn lines were not fully re-extracted. citeturn24view10turn25view3  
- **Instacart Credit Card:** Captured earn terms show 5% on Instacart, 5% on Chase Travel, 2% on gas/dining/streaming, 1% other. Annual fee was not captured in the extracted “annual fee” search output. citeturn25view4turn25view5  
- **Aeroplan Card:** Listed on Chase’s Aeroplan page and in US Credit Card Guide’s Chase archive (review tracked); offer/earn/fee not fully re-extracted here. citeturn6view9turn23view0  
- **World of Hyatt Credit Card:** Listed on Chase’s Hyatt brand page and tracked in US Credit Card Guide’s Chase archive; offer details were not re-extracted from official page text here. citeturn11view4turn23view0  
- **Amazon Prime Visa / Amazon Visa:** Listed on Chase’s Amazon brand page and tracked in US Credit Card Guide’s Chase archive; offer details were not fully re-extracted in captured text for this report. citeturn11view6turn23view0  

## Normalized credits and statement benefits ledger

**Columns:** Card, Frequency, Credit item, Amount/value, Enrollment required, Notes/limits.

Only benefits explicitly shown in captured official-page excerpts (or Chase brand pages) are included here; if a card is absent, it means no statement credit/promotional credit was captured in the excerpts for that card, not necessarily that no credits exist. citeturn23view0  

| Card | Frequency | Credit item | Amount/value | Enrollment required (Y/N) | Notes / restrictions |
|---|---|---|---:|:---:|---|
| Sapphire Reserve | Semiannual | Sapphire Exclusive Tables dining credit | Up to $150 Jan–Jun + up to $150 Jul–Dec (max $300/yr) | N (not stated) | Only when dining at restaurants in the program via OpenTable citeturn9view0 |
| Sapphire Reserve | Every 4 years | Global Entry / TSA PreCheck / NEXUS fee credit | Up to $120 | N (not stated) | Reimburses application fee charged to card citeturn9view0 |
| Sapphire Reserve | Annual (time-limited) | Apple TV + Apple Music subscriptions | Stated $288 annual value | N (not stated) | Subscriptions run through 6/22/2027 citeturn9view0 |
| Sapphire Reserve | Annual (time-limited) | DashPass membership | Stated $120 value for 12 months | Y | Must activate by 12/31/2027 citeturn9view0 |
| Sapphire Reserve | Monthly (time-limited) | DoorDash promos | Up to $25/mo | N (not stated) | Structure includes restaurant and non-restaurant promos; through 12/31/2027 citeturn9view0 |
| Sapphire Reserve | Semiannual (time-limited) | StubHub/viagogo credits | Up to $150 Jan–Jun + up to $150 Jul–Dec (max $300/yr) | Y | Activation required; through 12/31/2027 citeturn9view0 |
| Sapphire Reserve | Monthly (time-limited) | Lyft in-app credits | Up to $10/mo | N (not stated) | Through 9/30/2027 citeturn9view0 |
| Sapphire Reserve | Monthly (time-limited) | Peloton membership statement credits | $10/mo (max $120/yr) | Y | Eligible memberships; through 12/31/2027 citeturn9view0 |
| Sapphire Preferred | Annual | Chase Travel hotel credit | Up to $50 / anniversary year | N (not stated) | Hotel stays purchased through Chase Travel citeturn10view0 |
| Sapphire Preferred | Annual (time-limited) | DashPass membership | Stated $120 value for 12 months | Y | Activation by 12/31/2027 citeturn10view0 |
| Sapphire Preferred | Monthly (time-limited) | DoorDash promo | $10/mo (max $120/yr) | N (not stated) | Through 12/31/2027 citeturn10view0 |
| United Explorer | Annual (spend-gated) | United TravelBank cash | $100 | N (not stated) | After $10,000 spend / calendar year citeturn13view0 |
| United Explorer | Anniversary year | United Hotels statement credits | Up to $100 | N (not stated) | $50 on 1st + $50 on 2nd prepaid hotel stay via United Hotels citeturn13view0 |
| United Explorer | Monthly (calendar year) | Rideshare statement credits | Up to $5/mo (max $60/yr) | Y | Yearly opt-in required citeturn13view0 |
| United Explorer | Anniversary year | Avis/Budget TravelBank cash | Up to $25 for 1st + $25 for 2nd (max $50/yr) | N (not stated) | Book via cars.united.com citeturn13view0 |
| United Explorer | Monthly (calendar year; time-limited) | Instacart credits | $10/mo (max $120/yr) | N (not stated) | Benefits end 12/31/27 citeturn13view0 |
| United Explorer | Annual | JSX flight credits | Up to $100 | N (not stated) | Flights purchased directly through JSX citeturn13view0 |
| United Explorer | Every 4 years | Global Entry / TSA PreCheck / NEXUS fee credit | Up to $120 | N (not stated) | Reimbursement for application fee citeturn13view0 |
| United Quest | Anniversary | United travel credit | $200 | N (not stated) | Received after account opening and each anniversary citeturn15view2 |
| United Quest | Anniversary year | Renowned Hotels & Resorts credits | Up to $150 | N (not stated) | Prepaid hotel stays purchased directly through portal citeturn15view2 |
| United Quest | Calendar year | Rideshare credits | Up to $100/yr | Y | Monthly structure shown; yearly opt-in required citeturn14view2turn15view2 |
| United Quest | Anniversary year | Avis/Budget TravelBank cash | Up to $80/yr | N (not stated) | Two credits for 1st/2nd rentals via cars.united.com citeturn14view2turn15view2 |
| United Quest | Monthly (calendar year; time-limited) | Instacart credits | Up to $180/yr | N (not stated) | Benefits end 12/31/27 citeturn14view2turn15view2 |
| United Quest | Annual | JSX flight credits | Up to $150/yr | N (not stated) | Flights purchased directly citeturn14view2turn15view2 |
| United Club | Annual | Renowned Hotels & Resorts credits | Up to $200/yr | N (not stated) | Prepaid hotel stays purchased directly through portal citeturn15view5 |
| United Club | Calendar year | Rideshare credits | Up to $150/yr | Y | Enrollment required citeturn15view5 |
| United Club | Annual | Avis/Budget TravelBank cash | Up to $100/yr | N (not stated) | Through cars.united.com citeturn15view5 |
| United Club | Monthly (calendar year) | Instacart credits | Up to $240/yr | N (not stated) | Monthly cadence implied on page; terms apply citeturn15view5 |
| United Club | Annual | JSX flight credits | Up to $200/yr | N (not stated) | Flights purchased directly citeturn15view5 |
| United Gateway | Per purchase | United inflight + Club premium drink statement credit | 25% back | N | Food/beverages/Wi‑Fi on United-operated flights + Club premium drinks citeturn16view0turn16view1 |
| Southwest Plus | Per purchase | Inflight purchase statement credit | 25% back | N | Drinks/Wi‑Fi; posts as statement credit per terms citeturn17view0 |
| Southwest Priority | Per purchase | Inflight purchase “25% back” | 25% back | N | Listed as a travel benefit on page citeturn21view0 |
| Southwest Premier | Per purchase | Inflight purchase “25% back” | 25% back | N | Listed in benefits section citeturn19view4 |
| Marriott Boundless | Semiannual (2026 only; spend-gated) | Airline statement credits | $50 Jan–Jun + $50 Jul–Dec (max $100) | Sometimes | Existing cardmembers: activation required; new cardmembers in window: auto-registered per offer details citeturn22view0 |
| Disney Inspire Visa | One-time | Disney gift card upon approval | $300 | N | Delivered “to use today upon approval” citeturn11view7 |
| Disney Inspire Visa | One-time (spend-gated) | Statement credit | $300 | N | After $1,000 spend / 3 months citeturn11view7 |
| Disney Premier Visa | One-time | Disney gift card upon approval | $200 | N | Delivered upon approval citeturn12view7 |
| Disney Premier Visa | One-time (spend-gated) | Statement credit | $100 | N | After $500 spend / 3 months citeturn12view7 |

## Notes and caveats for interpretation

Many benefits captured in 2026 pages are **partner-ecosystem credits** rather than universal travel credits. For example, multiple United cards list rideshare credits that require **annual opt-in**, as well as Instacart credits that explicitly **end 12/31/27** in card disclosures. citeturn13view0turn14view2turn15view5  

The premium Sapphire Reserve configuration shown on Chase’s page includes several time-limited partnership benefits and a very high annual fee ($795). Whether the card is “worth it” depends less on raw earn rates and more on whether you already pay for (or will reliably use) the credited services and can follow the semiannual/monthly redemption cadence. citeturn9view0turn9view3turn8view0  

Finally, public offer pages can contain **limited-time** language and offer details can vary by channel; Chase’s own “offer details” overlays repeatedly note that offers may vary and can change over time. citeturn13view0turn17view0turn22view0
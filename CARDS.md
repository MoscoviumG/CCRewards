# CCRewards — Card Catalog

All cards currently in the seed database. Edit this file and ask Claude to sync changes back to `SeedData.java`.

**Rate types:** `Points` · `Miles` · `%` (cash back) · `Bilt Cash`
**Periods:** `Monthly` · `Quarterly` · `Semi-Annual` · `Annual`
**`[choice]`** — user selects one active category from the group at a time (rotating/choice categories)
**Categories:** `Drugstores` — drugstores & pharmacies · `Transit & Rideshare` — local transit, buses, subway, rideshare, parking, tolls

---

## Quick Reference

| ID | Card Name | Issuer | Fee | Type | Rewards Currency |
|---|---|---|---|---|---|
| `chase_sapphire_preferred` | Chase Sapphire Preferred | Chase | $95 | Personal | Chase Ultimate Rewards Points |
| `chase_sapphire_reserve` | Chase Sapphire Reserve | Chase | $795 | Personal | Chase Ultimate Rewards Points |
| `chase_freedom_unlimited` | Chase Freedom Unlimited | Chase | $0 | Personal | Chase Ultimate Rewards Points |
| `chase_freedom_flex` | Chase Freedom Flex | Chase | $0 | Personal | Chase Ultimate Rewards Points |
| `chase_freedom_rise` | Chase Freedom Rise | Chase | $0 | Personal | Chase Ultimate Rewards Points |
| `amazon_prime_visa` | Amazon Prime Visa | Chase | $0 | Personal | Cash Back |
| `united_explorer` | United Explorer Card | Chase | $150 | Personal | United MileagePlus |
| `united_quest` | United Quest Card | Chase | $350 | Personal | United MileagePlus |
| `united_gateway` | United Gateway Card | Chase | $0 | Personal | United MileagePlus |
| `united_club` | United Club Card | Chase | $695 | Personal | United MileagePlus |
| `southwest_plus` | Southwest Rapid Rewards Plus | Chase | $99 | Personal | Southwest Rapid Rewards |
| `southwest_priority` | Southwest Rapid Rewards Priority | Chase | $229 | Personal | Southwest Rapid Rewards |
| `southwest_premier` | Southwest Rapid Rewards Premier | Chase | $149 | Personal | Southwest Rapid Rewards |
| `marriott_boundless` | Marriott Bonvoy Boundless | Chase | $95 | Personal | Marriott Bonvoy Points |
| `marriott_bountiful` | Marriott Bonvoy Bountiful | Chase | $250 | Personal | Marriott Bonvoy Points |
| `marriott_bold` | Marriott Bonvoy Bold | Chase | $0 | Personal | Marriott Bonvoy Points |
| `chase_ritz_carlton` | Chase Ritz-Carlton Card | Chase | $450 | Personal | Marriott Bonvoy Points |
| `ihg_premier` | IHG One Rewards Premier | Chase | $99 | Personal | IHG One Rewards Points |
| `ihg_traveler` | IHG One Rewards Traveler | Chase | $0 | Personal | IHG One Rewards Points |
| `world_of_hyatt` | World of Hyatt Credit Card | Chase | $95 | Personal | World of Hyatt Points |
| `aeroplan` | Aeroplan Card | Chase | $95 | Personal | Aeroplan Miles |
| `british_airways_visa` | British Airways Visa Signature | Chase | $95 | Personal | Avios |
| `aer_lingus_visa` | Aer Lingus Visa Signature | Chase | $95 | Personal | Avios |
| `iberia_visa` | Iberia Visa Signature | Chase | $95 | Personal | Avios |
| `csr_business` | Chase Sapphire Reserve Business | Chase | $795 | Business | Chase Ultimate Rewards Points |
| `southwest_performance_business` | Southwest Rapid Rewards Performance Business | Chase | $299 | Business | Southwest Rapid Rewards |
| `southwest_premier_business` | Southwest Rapid Rewards Premier Business | Chase | $149 | Business | Southwest Rapid Rewards |
| `ihg_premier_business` | IHG One Rewards Premier Business | Chase | $99 | Business | IHG One Rewards Points |
| `united_business` | United Business Card | Chase | $150 | Business | United MileagePlus |
| `united_club_business` | United Club Business Card | Chase | $695 | Business | United MileagePlus |
| `world_of_hyatt_business` | World of Hyatt Business Credit Card | Chase | $199 | Business | World of Hyatt Points |
| `ink_preferred` | Ink Business Preferred | Chase | $95 | Business | Chase Ultimate Rewards Points |
| `ink_cash` | Ink Business Cash | Chase | $0 | Business | Chase Ultimate Rewards Points |
| `ink_unlimited` | Ink Business Unlimited | Chase | $0 | Business | Chase Ultimate Rewards Points |
| `ink_premier` | Ink Business Premier | Chase | $195 | Business | Cash Back |
| `amazon_business_prime` | Amazon Business Prime Card | Chase | $0 | Business | Cash Back |
| `amex_platinum` | Amex Platinum Card | American Express | $895 | Personal | Amex Membership Rewards Points |
| `amex_gold` | Amex Gold Card | American Express | $325 | Personal | Amex Membership Rewards Points |
| `amex_green` | Amex Green Card | American Express | $150 | Personal | Amex Membership Rewards Points |
| `amex_blue_cash_preferred` | Amex Blue Cash Preferred | American Express | $95 | Personal | Cash Back |
| `amex_blue_cash_everyday` | Amex Blue Cash Everyday | American Express | $0 | Personal | Cash Back |
| `delta_reserve_amex` | Delta SkyMiles Reserve Amex | American Express | $650 | Personal | Delta SkyMiles |
| `delta_platinum_amex` | Delta SkyMiles Platinum Amex | American Express | $350 | Personal | Delta SkyMiles |
| `delta_gold_amex` | Delta SkyMiles Gold Amex | American Express | $150 | Personal | Delta SkyMiles |
| `delta_blue_amex` | Delta SkyMiles Blue Amex | American Express | $0 | Personal | Delta SkyMiles |
| `hilton_aspire_amex` | Hilton Honors Amex Aspire | American Express | $550 | Personal | Hilton Honors Points |
| `hilton_surpass_amex` | Hilton Honors Amex Surpass | American Express | $150 | Personal | Hilton Honors Points |
| `hilton_amex` | Hilton Honors American Express Card | American Express | $0 | Personal | Hilton Honors Points |
| `marriott_brilliant_amex` | Marriott Bonvoy Brilliant Amex | American Express | $650 | Personal | Marriott Bonvoy Points |
| `marriott_bevy_amex` | Marriott Bonvoy Bevy Amex | American Express | $250 | Personal | Marriott Bonvoy Points |
| `amex_business_gold` | Amex Business Gold | American Express | $375 | Business | Amex Membership Rewards Points |
| `amex_business_platinum` | Amex Business Platinum | American Express | $895 | Business | Amex Membership Rewards Points |
| `amex_blue_business_plus` | Amex Blue Business Plus | American Express | $0 | Business | Amex Membership Rewards Points |
| `amex_blue_business_cash` | Amex Blue Business Cash | American Express | $0 | Business | Cash Back |
| `delta_gold_business_amex` | Delta SkyMiles Gold Business Amex | American Express | $150 | Business | Delta SkyMiles |
| `delta_platinum_business_amex` | Delta SkyMiles Platinum Business Amex | American Express | $350 | Business | Delta SkyMiles |
| `delta_reserve_business_amex` | Delta SkyMiles Reserve Business Amex | American Express | $650 | Business | Delta SkyMiles |
| `hilton_business_amex` | Hilton Honors Business Amex | American Express | $195 | Business | Hilton Honors Points |
| `capital_one_venture_x` | Capital One Venture X | Capital One | $395 | Personal | Capital One Miles |
| `capital_one_venture` | Capital One Venture | Capital One | $95 | Personal | Capital One Miles |
| `capital_one_venture_one` | Capital One VentureOne | Capital One | $0 | Personal | Capital One Miles |
| `capital_one_savor` | Capital One Savor | Capital One | $0 | Personal | Capital One Miles |
| `capital_one_quicksilver` | Capital One Quicksilver | Capital One | $0 | Personal | Cash Back |
| `capital_one_quicksilver_one` | Capital One QuicksilverOne | Capital One | $39 | Personal | Cash Back |
| `c1_spark_miles` | Capital One Spark Miles for Business | Capital One | $95 | Business | Capital One Miles |
| `c1_spark_cash_plus` | Capital One Spark Cash Plus | Capital One | $150 | Business | Cash Back |
| `c1_spark_cash_select` | Capital One Spark Cash Select | Capital One | $0 | Business | Cash Back |
| `citi_strata_elite` | Citi Strata Elite | Citi | $595 | Personal | Citi ThankYou Points |
| `citi_strata_premier` | Citi Strata Premier | Citi | $95 | Personal | Citi ThankYou Points |
| `citi_strata` | Citi Strata | Citi | $0 | Personal | Citi ThankYou Points |
| `citi_double_cash` | Citi Double Cash | Citi | $0 | Personal | Cash Back |
| `citi_custom_cash` | Citi Custom Cash | Citi | $0 | Personal | Citi ThankYou Points |
| `costco_citi` | Costco Anywhere Visa by Citi | Citi | $0 | Personal | Cash Back |
| `citi_aadvantage_executive` | Citi AAdvantage Executive | Citi | $595 | Personal | AAdvantage Miles |
| `citi_aadvantage_globe` | Citi AAdvantage Globe | Citi | $350 | Personal | AAdvantage Miles |
| `citi_aadvantage_platinum` | Citi AAdvantage Platinum Select | Citi | $99 | Personal | AAdvantage Miles |
| `citi_aadvantage_mileup` | AAdvantage MileUp | Citi | $0 | Personal | AAdvantage Miles |
| `citi_aadvantage_business` | CitiBusiness AAdvantage Platinum Select | Citi | $99 | Business | AAdvantage Miles |
| `bofa_customized_cash` | BofA Customized Cash Rewards | Bank of America | $0 | Personal | Cash Back |
| `bofa_unlimited_cash` | BofA Unlimited Cash Rewards | Bank of America | $0 | Personal | Cash Back |
| `bofa_premium_rewards` | BofA Premium Rewards | Bank of America | $95 | Personal | BofA Points |
| `bofa_premium_rewards_elite` | BofA Premium Rewards Elite | Bank of America | $550 | Personal | BofA Points |
| `bofa_travel_rewards` | BofA Travel Rewards | Bank of America | $0 | Personal | BofA Points |
| `atmos_ascent` | Atmos Rewards Ascent | Bank of America | $95 | Personal | Atmos/Alaska Rewards Miles |
| `atmos_summit` | Atmos Rewards Summit | Bank of America | $395 | Personal | Atmos/Alaska Rewards Miles |
| `air_france_klm_visa` | Air France KLM Visa Signature | Bank of America | $89 | Personal | Flying Blue Miles |
| `free_spirit_travel_more` | Free Spirit Travel More | Bank of America | $79 | Personal | Free Spirit Points |
| `allways_rewards` | Allways Rewards Visa | Bank of America | $59 | Personal | Cash Back |
| `royal_caribbean_visa` | Royal Caribbean Visa Signature | Bank of America | $0 | Personal | Cash Back |
| `norwegian_cruise_card` | Norwegian Cruise World Mastercard | Bank of America | $0 | Personal | Cash Back |
| `celebrity_cruises_visa` | Celebrity Cruises Visa Signature | Bank of America | $0 | Personal | Cash Back |
| `usbank_cash_plus` | US Bank Cash+ Visa | US Bank | $0 | Personal | Cash Back |
| `usbank_altitude_go` | US Bank Altitude Go | US Bank | $0 | Personal | Cash Back |
| `usbank_altitude_connect` | US Bank Altitude Connect | US Bank | $0 | Personal | Cash Back |
| `usbank_altitude_reserve` | US Bank Altitude Reserve | US Bank | $400 | Personal | Cash Back |
| `usbank_smartly` | US Bank Smartly Visa Signature | US Bank | $0 | Personal | Cash Back |
| `usbank_business_cash_plus` | US Bank Business Cash+ | US Bank | $0 | Business | Cash Back |
| `usbank_business_altitude_power` | US Bank Business Altitude Power | US Bank | $95 | Business | Cash Back |
| `discover_it_cashback` | Discover it Cash Back | Discover | $0 | Personal | Cash Back |
| `discover_it_miles` | Discover it Miles | Discover | $0 | Personal | Cash Back |
| `wf_active_cash` | Wells Fargo Active Cash | Wells Fargo | $0 | Personal | Cash Back |
| `wf_autograph` | Wells Fargo Autograph | Wells Fargo | $0 | Personal | Cash Back |
| `wf_autograph_journey` | Wells Fargo Autograph Journey | Wells Fargo | $95 | Personal | Cash Back |
| `apple_card` | Apple Card | Apple | $0 | Personal | Cash Back |
| `bilt_blue` | Bilt Blue | Bilt | $0 | Personal | Bilt Points |
| `bilt_obsidian` | Bilt Obsidian | Bilt | $95 | Personal | Bilt Points |
| `bilt_palladium` | Bilt Palladium | Bilt | $495 | Personal | Bilt Points |
| `hsbc_premier` | HSBC Premier World Elite Mastercard | HSBC | $0 | Personal | HSBC Rewards Points |
| `hsbc_elite` | HSBC Elite World Elite Mastercard | HSBC | $495 | Personal | HSBC Rewards Points |
| `barclays_jetblue_plus` | JetBlue Plus Card | Barclays | $99 | Personal | JetBlue TrueBlue Points |
| `barclays_jetblue` | JetBlue Card | Barclays | $0 | Personal | JetBlue TrueBlue Points |
| `barclays_hawaiian_airlines` | Hawaiian Airlines World Elite | Barclays | $99 | Personal | Atmos/Alaska Rewards Miles |
| `wyndham_earner_plus` | Wyndham Rewards Earner Plus | Barclays | $75 | Personal | Wyndham Rewards Points |
| `wyndham_earner` | Wyndham Rewards Earner | Barclays | $0 | Personal | Wyndham Rewards Points |
| `frontier_world_mastercard` | Frontier Airlines World Mastercard | Barclays | $79 | Personal | Frontier Miles |
| `barclays_lufthansa` | Lufthansa Miles & More Credit Card | Barclays | $89 | Personal | Miles & More Miles |
| `barclays_emirates_rewards` | Emirates Skywards Rewards Card | Barclays | $99 | Personal | Emirates Skywards Miles |
| `barclays_emirates_premium` | Emirates Skywards Premium Card | Barclays | $499 | Personal | Emirates Skywards Miles |

---

## Chase

### Chase Sapphire Preferred
`chase_sapphire_preferred` · Visa · $95/yr · Personal · Chase Ultimate Rewards Points

| Category | Rate | Type | Notes |
|---|---|---|---|
| Travel Portal | 5x | Points | |
| Dining | 3x | Points | |
| Online Groceries | 3x | Points | Excludes Target, Walmart, and wholesale clubs |
| Entertainment | 3x | Points | Includes select streaming services |
| Travel | 2x | Points | |
| General | 1x | Points | |

| Benefit | Amount | Period |
|---|---|---|
| Hotel Credit | $50 | Annual |

---

### Chase Sapphire Reserve
`chase_sapphire_reserve` · Visa · $795/yr · Personal · Chase Ultimate Rewards Points

| Category | Rate | Type |
|---|---|---|
| Travel Portal | 8x | Points |
| Travel | 4x | Points |
| Dining | 3x | Points |
| General | 1x | Points |

| Benefit | Amount | Period |
|---|---|---|
| Travel Credit | $300 | Annual |
| The Edit Hotel Credit | $250 | Semi-Annual |
| Dining Credit | $150 | Semi-Annual |
| StubHub Credit | $150 | Semi-Annual |
| Apple TV+ / Music Credit | $250 | Annual |
| DoorDash Credit | $25 | Monthly |
| Lyft Credit | $10 | Monthly |
| Peloton Credit | $10 | Monthly |
| Global Entry / TSA PreCheck | $120 | Annual |

---

### Chase Freedom Unlimited
`chase_freedom_unlimited` · Visa · $0/yr · Personal · Chase Ultimate Rewards Points

| Category | Rate | Type |
|---|---|---|
| Travel Portal | 5x | Points |
| Dining | 3x | Points |
| Drugstores | 3x | Points |
| General | 1.5x | Points |

---

### Chase Freedom Flex
`chase_freedom_flex` · Mastercard · $0/yr · Personal · Chase Ultimate Rewards Points

| Category | Rate | Type |
|---|---|---|
| Travel Portal | 5x | Points |
| Dining | 3x | Points |
| Drugstores | 3x | Points |
| General | 1x | Points |

> Rotating 5% quarterly categories: use the Quarterly Bonus feature

---

### Chase Freedom Rise
`chase_freedom_rise` · Visa · $0/yr · Personal · Chase Ultimate Rewards Points

| Category | Rate | Type |
|---|---|---|
| General | 1.5% | Cash Back |

---

### Amazon Prime Visa
`amazon_prime_visa` · Visa · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| Online Shopping | 5% | Cash Back |
| Groceries | 5% | Cash Back |
| Dining | 2% | Cash Back |
| Gas | 2% | Cash Back |
| General | 1% | Cash Back |

---

### United Explorer Card
`united_explorer` · Visa · $150/yr · Personal · United MileagePlus

| Category | Rate | Type | Notes |
|---|---|---|---|
| United (flights) | 2x | Miles | |
| Dining | 2x | Miles | |
| Travel (general) | 2x | Miles | Hotels booked direct |
| General | 1x | Miles | |

| Benefit | Amount | Period |
|---|---|---|
| United Hotels Credit | $100 | Annual |
| Rideshare Credit | $5 | Monthly |
| JSX Flight Credit | $100 | Annual |

---

### United Quest Card
`united_quest` · Visa · $350/yr · Personal · United MileagePlus

| Category | Rate | Type | Notes |
|---|---|---|---|
| United (flights) | 3x | Miles | |
| Travel (general) | 2x | Miles | |
| Dining | 2x | Miles | |
| Entertainment | 2x | Miles | Select streaming |
| General | 1x | Miles | |

| Benefit | Amount | Period |
|---|---|---|
| United Travel Credit | $200 | Annual |
| Renowned Hotels Credit | $150 | Annual |
| JSX Flight Credit | $150 | Annual |

---

### United Gateway Card
`united_gateway` · Visa · $0/yr · Personal · United MileagePlus

| Category | Rate | Type |
|---|---|---|
| United (flights) | 2x | Miles |
| Gas | 2x | Miles |
| Transit & Rideshare | 2x | Miles |
| General | 1x | Miles |

---

### United Club Card
`united_club` · Visa · $695/yr · Personal · United MileagePlus

| Category | Rate | Type |
|---|---|---|
| United (flights) | 4x | Miles |
| Travel (general) | 2x | Miles |
| Dining | 2x | Miles |
| General | 1x | Miles |

| Benefit | Amount | Period |
|---|---|---|
| Renowned Hotels Credit | $200 | Annual |
| Rideshare Credit | $150 | Annual |
| JSX Flight Credit | $200 | Annual |

---

### Southwest Rapid Rewards Plus
`southwest_plus` · Visa · $99/yr · Personal · Southwest Rapid Rewards

| Category | Rate | Type |
|---|---|---|
| Travel (Southwest) | 2x | Miles |
| Gas | 2x | Miles |
| Groceries | 2x | Miles |
| General | 1x | Miles |

---

### Southwest Rapid Rewards Priority
`southwest_priority` · Visa · $229/yr · Personal · Southwest Rapid Rewards

| Category | Rate | Type |
|---|---|---|
| Travel (Southwest) | 4x | Miles |
| Gas | 2x | Miles |
| Dining | 2x | Miles |
| General | 1x | Miles |

| Benefit | Amount | Period |
|---|---|---|
| Anniversary Bonus Points | $0 (7,500 pts) | Anniversary |

---

### Southwest Rapid Rewards Premier
`southwest_premier` · Visa · $149/yr · Personal · Southwest Rapid Rewards

| Category | Rate | Type |
|---|---|---|
| Travel (Southwest) | 3x | Miles |
| Dining | 2x | Miles |
| Groceries | 2x | Miles |
| General | 1x | Miles |

---

### Marriott Bonvoy Boundless
`marriott_boundless` · Visa · $95/yr · Personal · Marriott Bonvoy Points

| Category | Rate | Type |
|---|---|---|
| Marriott Hotels | 6x | Miles |
| Dining | 3x | Miles |
| Groceries | 3x | Miles |
| Gas | 3x | Miles |
| General | 2x | Miles |

---

### Marriott Bonvoy Bountiful
`marriott_bountiful` · Visa · $250/yr · Personal · Marriott Bonvoy Points

| Category | Rate | Type |
|---|---|---|
| Marriott Hotels | 6x | Miles |
| Dining | 4x | Miles |
| Groceries | 4x | Miles |
| General | 2x | Miles |

---

### Marriott Bonvoy Bold
`marriott_bold` · Visa · $0/yr · Personal · Marriott Bonvoy Points

| Category | Rate | Type |
|---|---|---|
| Marriott Hotels | 3x | Miles |
| Dining | 2x | Miles |
| General | 1x | Miles |

---

### Chase Ritz-Carlton Card
`chase_ritz_carlton` · Visa · $450/yr · Personal · Marriott Bonvoy Points

| Category | Rate | Type |
|---|---|---|
| Marriott / Ritz-Carlton Hotels | 6x | Miles |
| General | 2x | Miles |

| Benefit | Amount | Period |
|---|---|---|
| Air / Transportation Credit | $300 | Annual |
| Global Entry / TSA PreCheck | $100 | Anniversary |
| Anniversary Free Night | $0 (85,000 pts) | Anniversary |

---

### IHG One Rewards Premier
`ihg_premier` · Visa · $99/yr · Personal · IHG One Rewards Points

| Category | Rate | Type |
|---|---|---|
| IHG Hotels | 10x | Miles |
| Dining | 5x | Miles |
| Gas | 5x | Miles |
| Groceries | 5x | Miles |
| General | 3x | Miles |

| Benefit | Amount | Period |
|---|---|---|
| Global Entry / TSA PreCheck | $120 | Annual |

---

### IHG One Rewards Traveler
`ihg_traveler` · Visa · $0/yr · Personal · IHG One Rewards Points

| Category | Rate | Type | Notes |
|---|---|---|---|
| Dining | 3x | Miles | |
| Gas | 3x | Miles | |
| Entertainment | 3x | Miles | Select streaming |
| General | 2x | Miles | |

---

### World of Hyatt Credit Card
`world_of_hyatt` · Visa · $95/yr · Personal · World of Hyatt Points

| Category | Rate | Type |
|---|---|---|
| Hyatt Hotels | 4x | Miles |
| Travel (airlines / transit / gyms) | 2x | Miles |
| Dining | 2x | Miles |
| Transit & Rideshare | 2x | Miles |
| General | 1x | Miles |

---

### Aeroplan Card
`aeroplan` · Visa · $95/yr · Personal · Aeroplan Miles

| Category | Rate | Type | Notes |
|---|---|---|---|
| Travel | 3x | Miles | |
| Dining | 1.5x | Miles | |
| Groceries | 1.5x | Miles | |
| Travel (general) | 1.5x | Miles | Direct travel, non-Aeroplan partners |
| General | 1x | Miles | |

---

### British Airways Visa Signature
`british_airways_visa` · Visa · $95/yr · Personal · Avios

| Category | Rate | Type | Notes |
|---|---|---|---|
| Travel | 3x | Miles | |
| Travel (general) | 2x | Miles | Hotels booked direct |
| General | 1x | Miles | |

---

### Aer Lingus Visa Signature
`aer_lingus_visa` · Visa · $95/yr · Personal · Avios

| Category | Rate | Type | Notes |
|---|---|---|---|
| Travel | 3x | Miles | |
| Travel (general) | 2x | Miles | Hotels booked direct |
| General | 1x | Miles | |

---

### Iberia Visa Signature
`iberia_visa` · Visa · $95/yr · Personal · Avios

| Category | Rate | Type | Notes |
|---|---|---|---|
| Travel | 3x | Miles | |
| Travel (general) | 2x | Miles | Hotels booked direct |
| General | 1x | Miles | |

---

### Ink Business Preferred
`ink_preferred` · Visa · $95/yr · Business · Chase Ultimate Rewards Points

| Category | Rate | Type |
|---|---|---|
| Travel | 3x | Points |
| General | 1x | Points |

---

### Ink Business Cash
`ink_cash` · Visa · $0/yr · Business · Chase Ultimate Rewards Points

| Category | Rate | Type |
|---|---|---|
| Online Shopping | 5x | Points |
| Gas | 2x | Points |
| Dining | 2x | Points |
| General | 1x | Points |

---

### Ink Business Unlimited
`ink_unlimited` · Visa · $0/yr · Business · Chase Ultimate Rewards Points

| Category | Rate | Type |
|---|---|---|
| General | 1.5x | Points |

---

### Ink Business Premier
`ink_premier` · Visa · $195/yr · Business · Cash Back

| Category | Rate | Type |
|---|---|---|
| Travel Portal | 5% | Cash Back |
| General | 2% | Cash Back |

---

### Amazon Business Prime Card
`amazon_business_prime` · Visa · $0/yr · Business · Cash Back

| Category | Rate | Type |
|---|---|---|
| Online Shopping | 5% | Cash Back |
| Dining | 2% | Cash Back |
| Gas | 2% | Cash Back |
| General | 1% | Cash Back |

---

### Chase Sapphire Reserve Business
`csr_business` · Visa · $795/yr · Business · Chase Ultimate Rewards Points

| Category | Rate | Type |
|---|---|---|
| Travel Portal | 8x | Points |
| Travel (flights / hotels direct) | 4x | Points |
| General | 1x | Points |

| Benefit | Amount | Period |
|---|---|---|
| Travel Credit | $300 | Anniversary |
| The Edit Hotel Credit | $250 | Semi-Annual |
| DoorDash Credit | $25 | Monthly |
| Lyft Credit | $10 | Monthly |
| ZipRecruiter Credit | $200 | Semi-Annual |
| Google Workspace Credit | $200 | Annual |
| Giftcards.com Credit | $50 | Semi-Annual |

---

### Southwest Rapid Rewards Performance Business
`southwest_performance_business` · Visa · $299/yr · Business · Southwest Rapid Rewards

| Category | Rate | Type |
|---|---|---|
| Travel (Southwest) | 4x | Miles |
| Travel (hotels direct) | 2x | Miles |
| General | 1x | Miles |

---

### Southwest Rapid Rewards Premier Business
`southwest_premier_business` · Visa · $149/yr · Business · Southwest Rapid Rewards

| Category | Rate | Type |
|---|---|---|
| Travel (Southwest) | 3x | Miles |
| Gas | 2x | Miles |
| Dining | 2x | Miles |
| General | 1x | Miles |

---

### IHG One Rewards Premier Business
`ihg_premier_business` · Visa · $99/yr · Business · IHG One Rewards Points

| Category | Rate | Type |
|---|---|---|
| IHG Hotels | 10x | Points |
| Travel | 5x | Points |
| Gas | 5x | Points |
| Dining | 5x | Points |
| General | 3x | Points |

| Benefit | Amount | Period |
|---|---|---|
| United TravelBank Credit | $25 | Semi-Annual |

---

### United Business Card
`united_business` · Visa · $150/yr · Business · United MileagePlus

| Category | Rate | Type |
|---|---|---|
| United (flights) | 2x | Miles |
| Dining | 2x | Miles |
| Gas | 2x | Miles |
| General | 1x | Miles |

| Benefit | Amount | Period |
|---|---|---|
| United Hotels Credit | $50 | Semi-Annual (Anniversary) |
| Avis/Budget Credit | $25 | Semi-Annual (Anniversary) |
| Rideshare Credit | $8 | Monthly |
| Instacart Credit | $10 | Monthly |
| JSX Credit | $100 | Anniversary |

> Rideshare credit is $8/month Jan–Nov, $12 in December = $100/yr total. Yearly opt-in required. Instacart benefit ends 12/31/2027.

---

### United Club Business Card
`united_club_business` · Visa · $695/yr · Business · United MileagePlus

| Category | Rate | Type |
|---|---|---|
| United (flights) | 2x | Miles |
| General | 1.5x | Miles |

| Benefit | Amount | Period |
|---|---|---|
| Renowned Hotels Credit | $200 | Anniversary |
| Avis/Budget Credit | $50 | Semi-Annual (Anniversary) |
| Rideshare Credit | $12 | Monthly |
| Instacart Credit | $20 | Monthly |
| JSX Credit | $200 | Anniversary |

> Rideshare credit is $12/month Jan–Nov, $18 in December = $150/yr total. Yearly opt-in required. Instacart benefit ends 12/31/2027.

---

### World of Hyatt Business Credit Card
`world_of_hyatt_business` · Visa · $199/yr · Business · World of Hyatt Points

| Category | Rate | Type | Notes |
|---|---|---|---|
| Hyatt Hotels | 4x | Points | |
| Dining | 2x | Points | [choice] quarterly choice group |
| Gas | 2x | Points | [choice] quarterly choice group |
| General | 1x | Points | |

> 2x choice categories: user picks 2 per quarter from dining, gas, telecom, car rentals, shipping, airline tickets, social/search advertising. Only dining and gas are modeled in app.

| Benefit | Amount | Period |
|---|---|---|
| Hyatt Credit | $50 | Semi-Annual (Anniversary) |

---

## American Express

### Amex Platinum Card
`amex_platinum` · Amex · $895/yr · Personal · Amex Membership Rewards Points

| Category | Rate | Type |
|---|---|---|
| Travel Portal (Amex Travel) | 5x | Points |
| Travel (flights direct) | 5x | Points |
| General | 1x | Points |

| Benefit | Amount | Period |
|---|---|---|
| Airline Fee Credit | $200 | Annual |
| FHR / The Hotel Collection Credit | $300 | Semi-Annual |
| Resy Credit | $100 | Quarterly |
| Uber Cash | $15 | Monthly |
| Uber One Credit | $120 | Annual |
| Digital Entertainment Credit | $25 | Monthly |
| lululemon Credit | $75 | Quarterly |
| Saks Fifth Avenue Credit | $50 | Semi-Annual |
| Walmart+ Credit | $13 | Monthly |
| Global Entry / TSA PreCheck | $120 | Annual |
| CLEAR Plus Credit | $209 | Annual |
| Oura Ring Credit | $200 | Annual |
| Equinox Credit | $300 | Annual |

---

### Amex Gold Card
`amex_gold` · Amex · $325/yr · Personal · Amex Membership Rewards Points

| Category | Rate | Type | Notes |
|---|---|---|---|
| Dining | 4x | Points | |
| Groceries | 4x | Points | |
| Travel | 3x | Points | |
| Travel Portal | 2x | Points | Prepaid hotels & eligible AmexTravel |
| General | 1x | Points | |

| Benefit | Amount | Period |
|---|---|---|
| Dining Credit | $10 | Monthly |
| Resy Credit | $50 | Semi-Annual |
| Uber Cash | $10 | Monthly |
| Dunkin' Credit | $7 | Monthly |

---

### Amex Green Card
`amex_green` · Amex · $150/yr · Personal · Amex Membership Rewards Points

| Category | Rate | Type |
|---|---|---|
| Travel | 3x | Points |
| Dining | 3x | Points |
| Transit & Rideshare | 3x | Points |
| General | 1x | Points |

| Benefit | Amount | Period |
|---|---|---|
| CLEAR Plus Credit | $209 | Annual |

---

### Amex Blue Cash Preferred
`amex_blue_cash_preferred` · Amex · $95/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| Groceries | 6% | Cash Back |
| Entertainment | 6% | Cash Back |
| Gas | 3% | Cash Back |
| Transit & Rideshare | 3% | Cash Back |
| General | 1% | Cash Back |

| Benefit | Amount | Period |
|---|---|---|
| Disney Streaming Credit | $10 | Monthly |

---

### Amex Blue Cash Everyday
`amex_blue_cash_everyday` · Amex · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| Groceries | 3% | Cash Back |
| Online Shopping | 3% | Cash Back |
| Gas | 3% | Cash Back |
| General | 1% | Cash Back |

| Benefit | Amount | Period |
|---|---|---|
| Disney Streaming Credit | $7 | Monthly |
| Home Chef Credit | $15 | Monthly |

---

### Delta SkyMiles Reserve Amex
`delta_reserve_amex` · Amex · $650/yr · Personal · Delta SkyMiles

| Category | Rate | Type |
|---|---|---|
| Delta (flights) | 3x | Miles |
| General | 1x | Miles |

| Benefit | Amount | Period |
|---|---|---|
| Delta Stays Credit | $200 | Annual |

---

### Delta SkyMiles Platinum Amex
`delta_platinum_amex` · Amex · $350/yr · Personal · Delta SkyMiles

| Category | Rate | Type |
|---|---|---|
| Delta (flights) | 3x | Miles |
| Dining | 2x | Miles |
| Groceries | 2x | Miles |
| General | 1x | Miles |

| Benefit | Amount | Period |
|---|---|---|
| Delta Stays Credit | $150 | Annual |

---

### Delta SkyMiles Gold Amex
`delta_gold_amex` · Amex · $150/yr · Personal · Delta SkyMiles

| Category | Rate | Type |
|---|---|---|
| Delta (flights) | 2x | Miles |
| Dining | 2x | Miles |
| General | 1x | Miles |

| Benefit | Amount | Period |
|---|---|---|
| Delta Stays Credit | $100 | Annual |
| Delta Flight Credit | $200 | Annual |

---

### Delta SkyMiles Blue Amex
`delta_blue_amex` · Amex · $0/yr · Personal · Delta SkyMiles

| Category | Rate | Type |
|---|---|---|
| Delta (flights) | 2x | Miles |
| Dining | 2x | Miles |
| General | 1x | Miles |

---

### Hilton Honors Amex Aspire
`hilton_aspire_amex` · Amex · $550/yr · Personal · Hilton Honors Points

| Category | Rate | Type |
|---|---|---|
| Hilton Hotels | 14x | Points |
| Dining | 7x | Points |
| Travel (flights / car rentals) | 7x | Points |
| General | 3x | Points |

| Benefit | Amount | Period |
|---|---|---|
| Hilton Resort Credit | $200 | Semi-Annual |
| Flight Credit | $50 | Quarterly |
| CLEAR Plus Credit | $189 | Annual |

---

### Hilton Honors Amex Surpass
`hilton_surpass_amex` · Amex · $150/yr · Personal · Hilton Honors Points

| Category | Rate | Type |
|---|---|---|
| Hilton Hotels | 12x | Points |
| Dining | 6x | Points |
| Groceries | 6x | Points |
| Gas | 6x | Points |
| Online Shopping | 4x | Points |
| General | 3x | Points |

| Benefit | Amount | Period |
|---|---|---|
| Hilton Credit | $50 | Quarterly |

---

### Hilton Honors American Express Card
`hilton_amex` · Amex · $0/yr · Personal · Hilton Honors Points

| Category | Rate | Type |
|---|---|---|
| Hilton Hotels | 7x | Points |
| Dining | 5x | Points |
| Groceries | 5x | Points |
| Gas | 5x | Points |
| General | 3x | Points |

---

### Marriott Bonvoy Brilliant Amex
`marriott_brilliant_amex` · Amex · $650/yr · Personal · Marriott Bonvoy Points

| Category | Rate | Type |
|---|---|---|
| Marriott Hotels | 6x | Points |
| Dining | 3x | Points |
| Travel (airlines / taxis) | 3x | Points |
| General | 2x | Points |

| Benefit | Amount | Period |
|---|---|---|
| Brilliant Dining Credit | $300 | Annual |

---

### Marriott Bonvoy Bevy Amex
`marriott_bevy_amex` · Amex · $250/yr · Personal · Marriott Bonvoy Points

| Category | Rate | Type |
|---|---|---|
| Marriott Hotels | 6x | Points |
| Dining | 4x | Points |
| Groceries | 4x | Points |
| General | 2x | Points |

---

### Amex Business Gold
`amex_business_gold` · Amex · $375/yr · Business · Amex Membership Rewards Points

| Category | Rate | Type | Notes |
|---|---|---|---|
| Dining | 4x | Points | [choice] top-2 categories auto-selected |
| Travel | 4x | Points | [choice] top-2 categories auto-selected |
| Online Shopping | 4x | Points | [choice] top-2 categories auto-selected |
| Gas | 4x | Points | [choice] top-2 categories auto-selected |
| General | 1x | Points | |

---

### Amex Business Platinum
`amex_business_platinum` · Amex · $895/yr · Business · Amex Membership Rewards Points

| Category | Rate | Type |
|---|---|---|
| Travel Portal (Amex Travel) | 5x | Points |
| Travel (flights direct) | 5x | Points |
| General | 1x | Points |

| Benefit | Amount | Period |
|---|---|---|
| Airline Fee Credit | $200 | Annual |
| FHR / The Hotel Collection Credit | $300 | Semi-Annual |
| Hilton Credit | $50 | Quarterly |
| Dell Technologies Credit | $150 | Annual |
| Wireless Credit | $10 | Monthly |
| Indeed Credit | $90 | Quarterly |
| Adobe Credit | $150 | Annual |
| Global Entry / TSA PreCheck | $100 | Annual |
| CLEAR Plus Credit | $179 | Annual |

---

### Amex Blue Business Plus
`amex_blue_business_plus` · Amex · $0/yr · Business · Amex Membership Rewards Points

| Category | Rate | Type |
|---|---|---|
| General | 2x | Points |

---

### Amex Blue Business Cash
`amex_blue_business_cash` · Amex · $0/yr · Business · Cash Back

| Category | Rate | Type |
|---|---|---|
| General | 2% | Cash Back |

---

### Delta SkyMiles Gold Business Amex
`delta_gold_business_amex` · Amex · $150/yr · Business · Delta SkyMiles

| Category | Rate | Type |
|---|---|---|
| Delta (flights) | 2x | Miles |
| Dining | 2x | Miles |
| Groceries | 2x | Miles |
| General | 1x | Miles |

---

### Delta SkyMiles Platinum Business Amex
`delta_platinum_business_amex` · Amex · $350/yr · Business · Delta SkyMiles

| Category | Rate | Type |
|---|---|---|
| Delta (flights) | 3x | Miles |
| Dining | 1.5x | Miles |
| General | 1x | Miles |

---

### Delta SkyMiles Reserve Business Amex
`delta_reserve_business_amex` · Amex · $650/yr · Business · Delta SkyMiles

> 1.5x on transit, U.S. shipping, and office supply stores is not modeled (no matching category in app).

| Category | Rate | Type |
|---|---|---|
| Delta (flights) | 3x | Miles |
| General | 1x | Miles |

| Benefit | Amount | Period |
|---|---|---|
| Resy Credit | $20 | Monthly |
| Rideshare Credit | $10 | Monthly |
| Delta Stays Credit | $250 | Annual |

---

### Hilton Honors Business Amex
`hilton_business_amex` · Amex · $195/yr · Business · Hilton Honors Points

| Category | Rate | Type |
|---|---|---|
| Hilton Hotels | 12x | Points |
| Groceries | 5x | Points |
| Gas | 5x | Points |
| Dining | 5x | Points |
| General | 3x | Points |

---

## Capital One

### Capital One Venture X
`capital_one_venture_x` · Visa · $395/yr · Personal · Capital One Miles

| Category | Rate | Type |
|---|---|---|
| Travel Portal | 10x | Miles |
| General | 2x | Miles |

| Benefit | Amount | Period |
|---|---|---|
| Travel Credit | $300 | Annual |
| Anniversary Bonus | $0 (10,000 miles) | Annual |

---

### Capital One Venture
`capital_one_venture` · Visa · $95/yr · Personal · Capital One Miles

| Category | Rate | Type |
|---|---|---|
| Travel Portal | 5x | Miles |
| General | 2x | Miles |

| Benefit | Amount | Period |
|---|---|---|
| Global Entry / TSA PreCheck | $100 | Annual |

---

### Capital One VentureOne
`capital_one_venture_one` · Visa · $0/yr · Personal · Capital One Miles

| Category | Rate | Type |
|---|---|---|
| Travel Portal | 5x | Miles |
| General | 1.25x | Miles |

---

### Capital One Savor
`capital_one_savor` · Mastercard · $0/yr · Personal · Capital One Miles

| Category | Rate | Type |
|---|---|---|
| Travel Portal | 5x | Miles |
| Dining | 3x | Miles |
| Entertainment | 3x | Miles |
| Groceries | 3x | Miles |
| General | 1x | Miles |

---

### Capital One Quicksilver
`capital_one_quicksilver` · Mastercard · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| General | 1.5% | Cash Back |

---

### Capital One QuicksilverOne
`capital_one_quicksilver_one` · Mastercard · $39/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| General | 1.5% | Cash Back |

---

### Capital One Spark Miles for Business
`c1_spark_miles` · Visa · $95/yr · Business · Capital One Miles

| Category | Rate | Type |
|---|---|---|
| Travel Portal | 5x | Miles |
| General | 2x | Miles |

---

### Capital One Spark Cash Plus
`c1_spark_cash_plus` · Mastercard · $150/yr · Business · Cash Back

| Category | Rate | Type |
|---|---|---|
| General | 2% | Cash Back |

---

### Capital One Spark Cash Select
`c1_spark_cash_select` · Mastercard · $0/yr · Business · Cash Back

| Category | Rate | Type |
|---|---|---|
| General | 1.5% | Cash Back |

---

## Citi

### Citi Strata Elite
`citi_strata_elite` · Mastercard · $595/yr · Personal · Citi ThankYou Points

| Category | Rate | Type |
|---|---|---|
| Travel Portal | 12x | Points |
| Travel | 6x | Points |
| Dining | 6x | Points |
| General | 1.5x | Points |

| Benefit | Amount | Period |
|---|---|---|
| Hotel Benefit | $300 | Annual |
| Blacklane Credit | $100 | Semi-Annual |
| Global Entry / TSA PreCheck | $120 | Annual |

---

### Citi Strata Premier
`citi_strata_premier` · Mastercard · $95/yr · Personal · Citi ThankYou Points

| Category | Rate | Type |
|---|---|---|
| Travel Portal | 10x | Points |
| Travel | 3x | Points |
| Dining | 3x | Points |
| Groceries | 3x | Points |
| Gas | 3x | Points |
| General | 1x | Points |

| Benefit | Amount | Period |
|---|---|---|
| Hotel Discount | $100 | Annual |

---

### Citi Strata
`citi_strata` · Mastercard · $0/yr · Personal · Citi ThankYou Points

| Category | Rate | Type | Notes |
|---|---|---|---|
| Travel Portal | 5x | Points | |
| Groceries | 3x | Points | |
| Gas | 3x | Points | |
| Entertainment | 3x | Points | Self-select category |
| Transit & Rideshare | 3x | Points | |
| Dining | 2x | Points | |
| General | 1x | Points | |

---

### Citi Double Cash
`citi_double_cash` · Mastercard · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| General | 2% | Cash Back |

---

### Citi Custom Cash
`citi_custom_cash` · Mastercard · $0/yr · Personal · Citi ThankYou Points

| Category | Rate | Type | Notes |
|---|---|---|---|
| Dining | 5x | Points | [choice] top category each billing cycle |
| Groceries | 5x | Points | [choice] top category each billing cycle |
| Gas | 5x | Points | [choice] top category each billing cycle |
| Travel | 5x | Points | [choice] top category each billing cycle |
| Entertainment | 5x | Points | [choice] top category each billing cycle |
| Online Shopping | 5x | Points | [choice] top category each billing cycle |
| Drugstores | 5x | Points | [choice] top category each billing cycle |
| Transit & Rideshare | 5x | Points | [choice] top category each billing cycle |
| General | 1x | Points | |

---

### Costco Anywhere Visa by Citi
`costco_citi` · Visa · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| Gas | 4% | Cash Back |
| Dining | 3% | Cash Back |
| Travel | 3% | Cash Back |
| General | 1% | Cash Back |

---

### Citi AAdvantage Executive
`citi_aadvantage_executive` · Mastercard · $595/yr · Personal · AAdvantage Miles

| Category | Rate | Type | Notes |
|---|---|---|---|
| Travel Portal | 10x | Miles | AAdvantage Hotels & Cars portal |
| Travel (American Airlines) | 4x | Miles | |
| General | 1x | Miles | |

| Benefit | Amount | Period |
|---|---|---|
| Avis/Budget Rental Credit | $120 | Annual |
| Lyft Credit | $10 | Monthly |
| Grubhub Credit | $10 | Monthly |
| Global Entry / TSA PreCheck | $120 | Annual |

---

### Citi AAdvantage Globe
`citi_aadvantage_globe` · Mastercard · $350/yr · Personal · AAdvantage Miles

| Category | Rate | Type | Notes |
|---|---|---|---|
| Travel Portal | 6x | Miles | AAdvantage Hotels portal |
| Travel (American Airlines) | 3x | Miles | |
| Dining | 2x | Miles | |
| Transit & Rideshare | 2x | Miles | |
| General | 1x | Miles | |

| Benefit | Amount | Period |
|---|---|---|
| Turo Credit | $240 | Annual |
| Inflight Credit | $100 | Annual |
| Splurge Credit | $100 | Annual |
| Global Entry / TSA PreCheck | $120 | Annual |

---

### Citi AAdvantage Platinum Select
`citi_aadvantage_platinum` · Mastercard · $99/yr · Personal · AAdvantage Miles

| Category | Rate | Type |
|---|---|---|
| Travel (American Airlines) | 2x | Miles |
| Dining | 2x | Miles |
| Gas | 2x | Miles |
| General | 1x | Miles |

---

### AAdvantage MileUp
`citi_aadvantage_mileup` · Mastercard · $0/yr · Personal · AAdvantage Miles

| Category | Rate | Type |
|---|---|---|
| Travel (American Airlines) | 2x | Miles |
| Groceries | 2x | Miles |
| General | 1x | Miles |

---

### CitiBusiness AAdvantage Platinum Select
`citi_aadvantage_business` · Mastercard · $99/yr · Business · AAdvantage Miles

| Category | Rate | Type |
|---|---|---|
| Travel (American Airlines) | 2x | Miles |
| Gas | 2x | Miles |
| Dining | 2x | Miles |
| General | 1x | Miles |

---

## Bank of America

### BofA Customized Cash Rewards
`bofa_customized_cash` · Visa · $0/yr · Personal · Cash Back

| Category | Rate | Type | Notes |
|---|---|---|---|
| Dining | 3% | Cash Back | [choice] one category selected by user |
| Travel | 3% | Cash Back | [choice] one category selected by user |
| Gas | 3% | Cash Back | [choice] one category selected by user |
| Online Shopping | 3% | Cash Back | [choice] one category selected by user |
| Entertainment | 3% | Cash Back | [choice] one category selected by user |
| Drugstores | 3% | Cash Back | [choice] one category selected by user |
| Groceries | 2% | Cash Back | |
| General | 1% | Cash Back | |

---

### BofA Unlimited Cash Rewards
`bofa_unlimited_cash` · Visa · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| General | 1.5% | Cash Back |

---

### BofA Premium Rewards
`bofa_premium_rewards` · Visa · $95/yr · Personal · BofA Points

| Category | Rate | Type |
|---|---|---|
| Travel | 2x | Points |
| Dining | 2x | Points |
| General | 1.5x | Points |

| Benefit | Amount | Period |
|---|---|---|
| Airline Incidental Credit | $100 | Annual |

---

### BofA Premium Rewards Elite
`bofa_premium_rewards_elite` · Visa · $550/yr · Personal · BofA Points

| Category | Rate | Type |
|---|---|---|
| Travel | 2x | Points |
| Dining | 2x | Points |
| General | 1.5x | Points |

| Benefit | Amount | Period |
|---|---|---|
| Travel Credit | $300 | Annual |
| Lifestyle Credit | $150 | Annual |

---

### BofA Travel Rewards
`bofa_travel_rewards` · Visa · $0/yr · Personal · BofA Points

| Category | Rate | Type |
|---|---|---|
| Travel Portal | 3x | Points |
| General | 1.5x | Points |

---

### Atmos Rewards Ascent
`atmos_ascent` · Visa · $95/yr · Personal · Atmos/Alaska Rewards Miles

| Category | Rate | Type |
|---|---|---|
| Travel (Alaska) | 3x | Miles |
| Gas | 2x | Miles |
| Entertainment | 2x | Miles |
| Transit & Rideshare | 2x | Miles |
| General | 1x | Miles |

---

### Atmos Rewards Summit
`atmos_summit` · Visa · $395/yr · Personal · Atmos/Alaska Rewards Miles

| Category | Rate | Type |
|---|---|---|
| Travel (Alaska) | 3x | Miles |
| Dining | 3x | Miles |
| General | 1x | Miles |

---

### Air France KLM Visa Signature
`air_france_klm_visa` · Visa · $89/yr · Personal · Flying Blue Miles

| Category | Rate | Type |
|---|---|---|
| Travel | 3x | Miles |
| Dining | 3x | Miles |
| General | 1.5x | Miles |

| Benefit | Amount | Period |
|---|---|---|
| Anniversary Miles | $0 (5,000 miles) | Annual |

---

### Free Spirit Travel More
`free_spirit_travel_more` · Mastercard · $79/yr · Personal · Free Spirit Points

| Category | Rate | Type |
|---|---|---|
| Travel (Spirit) | 3x | Points |
| Dining | 2x | Points |
| Groceries | 2x | Points |
| General | 1x | Points |

| Benefit | Amount | Period |
|---|---|---|
| Companion Flight Voucher | $100 | Annual |

---

### Allways Rewards Visa
`allways_rewards` · Visa · $59/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| Travel (Allegiant) | 3% | Cash Back |
| Dining | 2% | Cash Back |
| General | 1% | Cash Back |

---

### Royal Caribbean Visa Signature
`royal_caribbean_visa` · Visa · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| Travel (Cruises) | 2% | Cash Back |
| General | 1% | Cash Back |

---

### Norwegian Cruise World Mastercard
`norwegian_cruise_card` · Mastercard · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| Travel (Cruises) | 3% | Cash Back |
| Travel (general) | 2% | Cash Back |
| General | 1% | Cash Back |

---

### Celebrity Cruises Visa Signature
`celebrity_cruises_visa` · Visa · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| Travel (Cruises) | 2% | Cash Back |
| General | 1% | Cash Back |

---

## US Bank

### US Bank Cash+ Visa
`usbank_cash_plus` · Visa · $0/yr · Personal · Cash Back

| Category | Rate | Type | Notes |
|---|---|---|---|
| Dining | 5% | Cash Back | [choice] user selects one category |
| Groceries | 5% | Cash Back | [choice] user selects one category |
| Travel | 5% | Cash Back | [choice] user selects one category |
| Entertainment | 5% | Cash Back | [choice] user selects one category |
| Online Shopping | 5% | Cash Back | [choice] user selects one category |
| Gas | 2% | Cash Back | |
| General | 1% | Cash Back | |

---

### US Bank Altitude Go
`usbank_altitude_go` · Visa · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| Dining | 4% | Cash Back |
| Groceries | 2% | Cash Back |
| Gas | 2% | Cash Back |
| General | 1% | Cash Back |

---

### US Bank Altitude Connect
`usbank_altitude_connect` · Visa · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| Travel Portal | 5% | Cash Back |
| Travel | 4% | Cash Back |
| Gas | 4% | Cash Back |
| Entertainment | 2% | Cash Back |
| Dining | 2% | Cash Back |
| Groceries | 2% | Cash Back |
| General | 1% | Cash Back |

| Benefit | Amount | Period |
|---|---|---|
| Global Entry / TSA PreCheck | $100 | Anniversary |

---

### US Bank Altitude Reserve
`usbank_altitude_reserve` · Visa · $400/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| Travel Portal (Altitude Rewards Center) | 5% | Cash Back |
| Travel | 3% | Cash Back |
| General | 1% | Cash Back |

| Benefit | Amount | Period |
|---|---|---|
| Travel Credit | $325 | Annual |

---

### US Bank Smartly Visa Signature
`usbank_smartly` · Visa · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| General | 2% | Cash Back |

---

### US Bank Business Cash+
`usbank_business_cash_plus` · Visa · $0/yr · Business · Cash Back

| Category | Rate | Type | Notes |
|---|---|---|---|
| Dining | 5% | Cash Back | [choice] user selects one category |
| Travel | 5% | Cash Back | [choice] user selects one category |
| Online Shopping | 5% | Cash Back | [choice] user selects one category |
| Gas | 2% | Cash Back | |
| General | 1% | Cash Back | |

---

### US Bank Business Altitude Power
`usbank_business_altitude_power` · Visa · $95/yr · Business · Cash Back

| Category | Rate | Type |
|---|---|---|
| General | 2% | Cash Back |

---

## Discover

### Discover it Cash Back
`discover_it_cashback` · Discover · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| General | 1% | Cash Back |

> Rotating 5% quarterly categories: use the Quarterly Bonus feature

---

### Discover it Miles
`discover_it_miles` · Discover · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| General | 1.5x | Miles |

---

## Wells Fargo

### Wells Fargo Active Cash
`wf_active_cash` · Visa · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| General | 2% | Cash Back |

---

### Wells Fargo Autograph
`wf_autograph` · Visa · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| Travel | 3x | Points |
| Dining | 3x | Points |
| Gas | 3x | Points |
| Entertainment | 3x | Points |
| General | 1x | Points |

---

### Wells Fargo Autograph Journey
`wf_autograph_journey` · Visa · $95/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| Travel | 5x | Points |
| Dining | 3x | Points |
| General | 1x | Points |

| Benefit | Amount | Period |
|---|---|---|
| Airline Credit | $50 | Annual |

---

## HSBC

### HSBC Premier World Elite Mastercard
`hsbc_premier` · Mastercard · $0/yr · Personal · HSBC Rewards Points

| Category | Rate | Type |
|---|---|---|
| Groceries | 3x | Points |
| Gas | 3x | Points |
| Travel | 2x | Points |
| General | 1x | Points |

---

### HSBC Elite World Elite Mastercard
`hsbc_elite` · Mastercard · $495/yr · Personal · HSBC Rewards Points

| Category | Rate | Type |
|---|---|---|
| Travel | 5x | Points |
| Dining | 2x | Points |
| General | 1x | Points |

| Benefit | Amount | Period |
|---|---|---|
| Priceline Credit | $400 | Annual |
| Rideshare Credit | $10 | Monthly |

---

## Other

### Apple Card
`apple_card` · Mastercard · $0/yr · Personal · Cash Back

| Category | Rate | Type |
|---|---|---|
| Online Shopping | 3% | Cash Back |
| General | 2% | Cash Back |

---

### Bilt Blue
`bilt_blue` · Mastercard · $0/yr · Personal · Bilt Points

| Category | Rate | Type |
|---|---|---|
| Rent / Mortgage | 4x | Bilt Cash |
| General | 1x | Points |
| General | 4x | Bilt Cash |

---

### Bilt Obsidian
`bilt_obsidian` · Mastercard · $95/yr · Personal · Bilt Points

| Category | Rate | Type | Notes |
|---|---|---|---|
| Dining | 3x | Points | [choice] dining or groceries |
| Dining | 4x | Bilt Cash | |
| Groceries | 3x | Points | [choice] dining or groceries |
| Groceries | 4x | Bilt Cash | |
| Travel | 2x | Points | |
| Travel | 4x | Bilt Cash | |
| Rent / Mortgage | 1x | Points | |
| Rent / Mortgage | 4x | Bilt Cash | |
| General | 1x | Points | |
| General | 4x | Bilt Cash | |

---

### Bilt Palladium
`bilt_palladium` · Mastercard · $495/yr · Personal · Bilt Points

| Category | Rate | Type |
|---|---|---|
| General | 2x | Points |
| General | 4x | Bilt Cash |
| Rent / Mortgage | 4x | Bilt Cash |

| Benefit | Amount | Period |
|---|---|---|
| Annual Bilt Cash | $200 | Annual |
| Travel Hotel Credit | $200 | Semi-Annual |

---

## Barclays

### JetBlue Plus Card
`barclays_jetblue_plus` · Mastercard · $99/yr · Personal · JetBlue TrueBlue Points

| Category | Rate | Type |
|---|---|---|
| JetBlue | 6x | Miles |
| Dining | 2x | Miles |
| Groceries | 2x | Miles |
| General | 1x | Miles |

---

### JetBlue Card
`barclays_jetblue` · Mastercard · $0/yr · Personal · JetBlue TrueBlue Points

| Category | Rate | Type |
|---|---|---|
| JetBlue | 3x | Miles |
| Dining | 2x | Miles |
| Groceries | 2x | Miles |
| General | 1x | Miles |

---

### Hawaiian Airlines World Elite
`barclays_hawaiian_airlines` · Mastercard · $99/yr · Personal · Atmos/Alaska Rewards Miles

| Category | Rate | Type |
|---|---|---|
| Hawaiian Airlines | 3x | Miles |
| Dining | 2x | Miles |
| Gas | 2x | Miles |
| Groceries | 2x | Miles |
| General | 1x | Miles |

---

### Wyndham Rewards Earner Plus
`wyndham_earner_plus` · Visa · $75/yr · Personal · Wyndham Rewards Points

| Category | Rate | Type |
|---|---|---|
| Wyndham | 6x | Points |
| Gas | 6x | Points |
| Dining | 4x | Points |
| Groceries | 4x | Points |
| General | 1x | Points |

---

### Wyndham Rewards Earner
`wyndham_earner` · Visa · $0/yr · Personal · Wyndham Rewards Points

| Category | Rate | Type |
|---|---|---|
| Wyndham | 5x | Points |
| Gas | 5x | Points |
| Dining | 2x | Points |
| Groceries | 2x | Points |
| General | 1x | Points |

---

### Frontier Airlines World Mastercard
`frontier_world_mastercard` · Mastercard · $79/yr · Personal · Frontier Miles

| Category | Rate | Type |
|---|---|---|
| Frontier | 5x | Miles |
| Dining | 3x | Miles |
| General | 1x | Miles |

---

### Lufthansa Miles & More Credit Card
`barclays_lufthansa` · Mastercard · $89/yr · Personal · Miles & More Miles

| Category | Rate | Type |
|---|---|---|
| Lufthansa | 2x | Miles |
| General | 1x | Miles |

---

### Emirates Skywards Rewards Card
`barclays_emirates_rewards` · Mastercard · $99/yr · Personal · Emirates Skywards Miles

| Category | Rate | Type |
|---|---|---|
| Emirates | 3x | Miles |
| General Travel | 2x | Miles |
| General | 1x | Miles |

---

### Emirates Skywards Premium Card
`barclays_emirates_premium` · Mastercard · $499/yr · Personal · Emirates Skywards Miles

| Category | Rate | Type |
|---|---|---|
| Emirates | 3x | Miles |
| General Travel | 2x | Miles |
| General | 1x | Miles |

| Benefit | Amount | Period |
|---|---|---|
| Global Entry / TSA PreCheck | $100 | Annual (Anniversary) |

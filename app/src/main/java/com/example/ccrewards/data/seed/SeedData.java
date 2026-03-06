package com.example.ccrewards.data.seed;

import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.RateType;
import com.example.ccrewards.data.model.ResetPeriod;
import com.example.ccrewards.data.model.ResetType;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.RewardRate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pre-loaded card catalog (~55 cards, ~300+ RewardRate rows).
 * Card reward rates based on uscreditcardguide.com (Feb 2026).
 */
public class SeedData {

    // ── Seed version helper ────────────────────────────────────────────────────

    /**
     * Returns the IDs of all cards defined in this seed file.
     * Used by the seed-refresh logic to know which cards to update.
     */
    public static List<String> getSeededCardIds() {
        List<String> ids = new ArrayList<>();
        for (CardDefinition card : getCardDefinitions()) {
            ids.add(card.id);
        }
        return ids;
    }

    // ── Color helpers ─────────────────────────────────────────────────────────

    private static long argb(int a, int r, int g, int b) {
        return ((long)(a & 0xFF) << 24) | ((long)(r & 0xFF) << 16) |
               ((long)(g & 0xFF) << 8) | (long)(b & 0xFF);
    }

    private static final long CHASE_BLUE      = argb(255, 0,   114, 206);
    private static final long CHASE_DARK      = argb(255, 0,    60, 113);
    private static final long AMEX_GOLD_C     = argb(255, 183, 147,  74);
    private static final long AMEX_PLAT_C     = argb(255, 193, 193, 193);
    private static final long AMEX_PLAT_D     = argb(255, 120, 120, 120);
    private static final long AMEX_BLUE       = argb(255, 0,   100, 200);
    private static final long AMEX_BLUE_D     = argb(255, 0,    60, 140);
    private static final long AMEX_GREEN_C    = argb(255, 0,   128,  78);
    private static final long AMEX_GREEN_D    = argb(255, 0,    80,  48);
    private static final long AMEX_GOLD_D     = argb(255, 100,  78,  20);
    private static final long C1_RED          = argb(255, 204,   0,   0);
    private static final long C1_DARK         = argb(255, 153,   0,   0);
    private static final long CITI_BLUE       = argb(255, 0,    88, 163);
    private static final long CITI_DARK       = argb(255, 0,    50, 100);
    private static final long BOFA_RED        = argb(255, 214,   0,   0);
    private static final long BOFA_DARK       = argb(255, 140,   0,   0);
    private static final long USB_RED         = argb(255, 208,   0,   0);
    private static final long USB_DARK        = argb(255, 100,   0,   0);
    private static final long DISC_ORANGE     = argb(255, 244, 127,  33);
    private static final long DISC_DARK       = argb(255, 193,  78,   0);
    private static final long WF_RED          = argb(255, 206,  17,  38);
    private static final long WF_DARK         = argb(255, 132,   8,  23);
    private static final long BILT_BLACK      = argb(255,  20,  20,  20);
    private static final long BILT_GOLD       = argb(255, 200, 160,  80);
    private static final long BILT_GRAY       = argb(255,  50,  50,  50);
    private static final long DELTA_RED       = argb(255, 188,   0,  36);
    private static final long DELTA_PURP      = argb(255,  60,  16,  83);
    private static final long HILTON_BLUE     = argb(255,   0,  72, 124);
    private static final long HILTON_LIGHT    = argb(255,   0, 130, 170);
    private static final long AMZN_BLACK      = argb(255,   0,   0,   0);
    private static final long AMZN_ORANGE     = argb(255, 255, 153,   0);
    private static final long UNITED_DARK     = argb(255,   0,  44,  95);
    private static final long UNITED_BLUE     = argb(255,   0, 110, 181);
    private static final long ATMOS_BLUE      = argb(255,   0,  80, 140);
    private static final long ATMOS_DARK      = argb(255,   0,  40,  80);
    private static final long PALLADIUM       = argb(255, 140, 130, 120);
    private static final long APPLE_SILVER    = argb(255, 200, 200, 200);
    private static final long APPLE_DARK      = argb(255, 100, 100, 100);
    private static final long MARRIOTT_BRONZE = argb(255, 152,  96,  40);
    private static final long MARRIOTT_DARK   = argb(255,  80,  40,  10);
    private static final long SW_RED          = argb(255, 220,   0,  50);  // Southwest
    private static final long SW_DARK         = argb(255, 100,   0,  20);
    private static final long IHG_BLUE        = argb(255,   0,  71, 127);  // IHG
    private static final long IHG_TEAL        = argb(255,   0, 163, 173);
    private static final long HYATT_DARK      = argb(255,  24,  24,  24);  // World of Hyatt
    private static final long HYATT_GOLD      = argb(255, 177, 143,  57);
    private static final long AVIOS_NAVY      = argb(255,   0,  37,  84);  // BA/Avios/Iberia/Aer Lingus
    private static final long AVIOS_RED       = argb(255, 218,   2,   2);
    private static final long AC_RED          = argb(255, 180,   0,   0);  // Air Canada / Aeroplan
    private static final long AC_DARK         = argb(255,  25,  25,  25);
    private static final long AA_RED          = argb(255, 180,   0,  30);  // American Airlines
    private static final long AA_SILVER       = argb(255, 160, 160, 170);
    private static final long AF_BLUE         = argb(255,   0,  43, 135);  // Air France KLM
    private static final long AF_RED          = argb(255, 200,   0,  40);
    private static final long FS_YELLOW       = argb(255, 255, 204,   0);  // Free Spirit (Spirit Airlines)
    private static final long FS_BLACK        = argb(255,  10,  10,  10);
    private static final long ALLEGIANT_DARK  = argb(255,  22,  22,  22);  // Allegiant / Allways
    private static final long ALLEGIANT_ORANGE= argb(255, 255, 115,   0);
    private static final long RC_BLUE         = argb(255,   0,  68, 124);  // Royal Caribbean
    private static final long RC_DARK         = argb(255,   0,  30,  70);
    private static final long NCL_BLUE        = argb(255,   0, 101, 163);  // Norwegian Cruise Line
    private static final long NCL_RED         = argb(255, 200,   0,  30);
    private static final long CEL_NAVY        = argb(255,  15,  55, 100);  // Celebrity Cruises
    private static final long CEL_GOLD        = argb(255, 196, 164,  96);
    private static final long RITZ_NAVY       = argb(255,   0,  28,  68);  // Ritz-Carlton
    private static final long RITZ_GOLD       = argb(255, 172, 142,  72);
    private static final long HSBC_RED        = argb(255, 219,   0,  35);  // HSBC
    private static final long HSBC_DARK       = argb(255, 139,   0,   0);

    // ── Card Definitions ──────────────────────────────────────────────────────

    public static List<CardDefinition> getCardDefinitions() {
        return Arrays.asList(
            // Chase Personal — Core (Ultimate Rewards)
            new CardDefinition("chase_sapphire_preferred", "Chase Sapphire Preferred", "Chase", "Visa",  95, false, false, CHASE_BLUE, CHASE_DARK, "Chase Ultimate Rewards Points"),
            new CardDefinition("chase_sapphire_reserve",   "Chase Sapphire Reserve",   "Chase", "Visa", 795, false, false, CHASE_DARK, CHASE_BLUE, "Chase Ultimate Rewards Points"),
            new CardDefinition("chase_freedom_unlimited",  "Chase Freedom Unlimited",  "Chase", "Visa",   0, false, false, CHASE_BLUE, CHASE_DARK, "Chase Ultimate Rewards Points"),
            new CardDefinition("chase_freedom_flex",       "Chase Freedom Flex",       "Chase", "Mastercard", 0, false, false, CHASE_BLUE, CHASE_DARK, "Chase Ultimate Rewards Points"),
            new CardDefinition("chase_freedom_rise",       "Chase Freedom Rise",       "Chase", "Visa",   0, false, false, CHASE_BLUE, CHASE_DARK, "Chase Ultimate Rewards Points"),
            new CardDefinition("amazon_prime_visa",        "Amazon Prime Visa",        "Chase", "Visa",   0, false, false, AMZN_BLACK, AMZN_ORANGE, "Cash Back"),

            // Chase Personal — United Co-Brand
            new CardDefinition("united_explorer", "United Explorer Card",  "Chase", "Visa", 150, false, false, UNITED_DARK, UNITED_BLUE, "United MileagePlus"),
            new CardDefinition("united_quest",    "United Quest Card",     "Chase", "Visa", 350, false, false, UNITED_DARK, UNITED_BLUE, "United MileagePlus"),
            new CardDefinition("united_gateway",  "United Gateway Card",   "Chase", "Visa",   0, false, false, UNITED_BLUE, UNITED_DARK, "United MileagePlus"),
            new CardDefinition("united_club",     "United Club Card",      "Chase", "Visa", 695, false, false, UNITED_DARK, UNITED_BLUE, "United MileagePlus"),

            // Chase Personal — Southwest Co-Brand
            new CardDefinition("southwest_plus",     "Southwest Rapid Rewards Plus",     "Chase", "Visa",  99, false, false, SW_RED, SW_DARK, "Southwest Rapid Rewards"),
            new CardDefinition("southwest_priority", "Southwest Rapid Rewards Priority", "Chase", "Visa", 229, false, false, SW_RED, SW_DARK, "Southwest Rapid Rewards"),
            new CardDefinition("southwest_premier",  "Southwest Rapid Rewards Premier",  "Chase", "Visa", 149, false, false, SW_RED, SW_DARK, "Southwest Rapid Rewards"),

            // Chase Personal — Marriott Co-Brand
            new CardDefinition("marriott_boundless", "Marriott Bonvoy Boundless",  "Chase", "Visa",  95, false, false, MARRIOTT_BRONZE, MARRIOTT_DARK, "Marriott Bonvoy Points"),
            new CardDefinition("marriott_bountiful", "Marriott Bonvoy Bountiful", "Chase", "Visa", 250, false, false, MARRIOTT_BRONZE, MARRIOTT_DARK, "Marriott Bonvoy Points"),
            new CardDefinition("marriott_bold",      "Marriott Bonvoy Bold",      "Chase", "Visa",   0, false, false, MARRIOTT_BRONZE, MARRIOTT_DARK, "Marriott Bonvoy Points"),
            new CardDefinition("chase_ritz_carlton", "Chase Ritz-Carlton Rewards","Chase", "Visa", 450, false, false, RITZ_NAVY, RITZ_GOLD, "Marriott Bonvoy Points"),

            // Chase Personal — IHG Co-Brand
            new CardDefinition("ihg_premier",  "IHG One Rewards Premier",  "Chase", "Visa", 99, false, false, IHG_BLUE, IHG_TEAL, "IHG One Rewards Points"),
            new CardDefinition("ihg_traveler", "IHG One Rewards Traveler", "Chase", "Visa",  0, false, false, IHG_TEAL, IHG_BLUE, "IHG One Rewards Points"),

            // Chase Personal — Other Hotel & Airline Co-Brands
            new CardDefinition("world_of_hyatt",       "World of Hyatt Credit Card",       "Chase", "Visa", 95, false, false, HYATT_DARK, HYATT_GOLD, "World of Hyatt Points"),
            new CardDefinition("aeroplan",              "Aeroplan Card",                     "Chase", "Visa", 95, false, false, AC_RED, AC_DARK, "Aeroplan Miles"),
            new CardDefinition("british_airways_visa",  "British Airways Visa Signature",    "Chase", "Visa", 95, false, false, AVIOS_NAVY, AVIOS_RED, "Avios"),
            new CardDefinition("aer_lingus_visa",       "Aer Lingus Visa Signature",         "Chase", "Visa", 95, false, false, AVIOS_NAVY, AVIOS_RED, "Avios"),
            new CardDefinition("iberia_visa",           "Iberia Visa Signature",             "Chase", "Visa", 95, false, false, AVIOS_NAVY, AVIOS_RED, "Avios"),

            // Amex Personal — Membership Rewards
            new CardDefinition("amex_platinum",            "Amex Platinum Card",           "American Express", "Amex", 895, false, false, AMEX_PLAT_C, AMEX_PLAT_D, "Amex Membership Rewards Points"),
            new CardDefinition("amex_gold",                "Amex Gold Card",               "American Express", "Amex", 325, false, false, AMEX_GOLD_C, AMEX_GOLD_D, "Amex Membership Rewards Points"),
            new CardDefinition("amex_green",               "Amex Green Card",              "American Express", "Amex", 150, false, false, AMEX_GREEN_C, AMEX_GREEN_D, "Amex Membership Rewards Points"),

            // Amex Personal — Cash Back
            new CardDefinition("amex_blue_cash_preferred", "Amex Blue Cash Preferred",     "American Express", "Amex",  95, false, false, AMEX_BLUE, AMEX_BLUE_D, "Cash Back"),
            new CardDefinition("amex_blue_cash_everyday",  "Amex Blue Cash Everyday",      "American Express", "Amex",   0, false, false, AMEX_BLUE, AMEX_BLUE_D, "Cash Back"),

            // Amex Personal — Delta Co-Brand
            new CardDefinition("delta_reserve_amex",       "Delta SkyMiles Reserve Amex",  "American Express", "Amex", 650, false, false, DELTA_PURP, DELTA_RED, "Delta SkyMiles"),
            new CardDefinition("delta_platinum_amex",      "Delta SkyMiles Platinum Amex", "American Express", "Amex", 350, false, false, DELTA_PURP, DELTA_RED, "Delta SkyMiles"),
            new CardDefinition("delta_gold_amex",          "Delta SkyMiles Gold Amex",     "American Express", "Amex", 150, false, false, DELTA_RED, DELTA_PURP, "Delta SkyMiles"),
            new CardDefinition("delta_blue_amex",          "Delta SkyMiles Blue Amex",     "American Express", "Amex",   0, false, false, DELTA_RED, DELTA_PURP, "Delta SkyMiles"),

            // Amex Personal — Hilton Co-Brand
            new CardDefinition("hilton_aspire_amex",       "Hilton Honors Amex Aspire",    "American Express", "Amex", 550, false, false, HILTON_BLUE, HILTON_LIGHT, "Hilton Honors Points"),
            new CardDefinition("hilton_surpass_amex",      "Hilton Honors Amex Surpass",   "American Express", "Amex", 150, false, false, HILTON_BLUE, HILTON_LIGHT, "Hilton Honors Points"),
            new CardDefinition("hilton_amex",              "Hilton Honors American Express Card", "American Express", "Amex",   0, false, false, HILTON_BLUE, HILTON_LIGHT, "Hilton Honors Points"),

            // Amex Personal — Marriott Co-Brand
            new CardDefinition("marriott_brilliant_amex",  "Marriott Bonvoy Brilliant Amex","American Express", "Amex", 650, false, false, MARRIOTT_BRONZE, MARRIOTT_DARK, "Marriott Bonvoy Points"),
            new CardDefinition("marriott_bevy_amex",       "Marriott Bonvoy Bevy Amex",    "American Express", "Amex", 250, false, false, MARRIOTT_BRONZE, MARRIOTT_DARK, "Marriott Bonvoy Points"),

            // Capital One Personal
            new CardDefinition("capital_one_venture_x",    "Capital One Venture X",          "Capital One", "Visa",       395, false, false, C1_DARK, C1_RED,  "Capital One Miles"),
            new CardDefinition("capital_one_venture",      "Capital One Venture",             "Capital One", "Visa",        95, false, false, C1_RED,  C1_DARK, "Capital One Miles"),
            new CardDefinition("capital_one_venture_one",  "Capital One VentureOne",          "Capital One", "Visa",         0, false, false, C1_RED,  C1_DARK, "Capital One Miles"),
            new CardDefinition("capital_one_savor",        "Capital One Savor",               "Capital One", "Mastercard",   0, false, false, C1_RED,  C1_DARK, "Capital One Miles"),
            new CardDefinition("capital_one_quicksilver",  "Capital One Quicksilver",         "Capital One", "Mastercard",   0, false, false, C1_RED,  C1_DARK, "Cash Back"),
            new CardDefinition("capital_one_quicksilver_one","Capital One QuicksilverOne",    "Capital One", "Mastercard",  39, false, false, C1_RED,  C1_DARK, "Cash Back"),

            // Citi Personal — Strata (ThankYou Points)
            new CardDefinition("citi_strata_elite",          "Citi Strata Elite",                "Citi", "Mastercard", 595, false, false, CITI_DARK, CITI_BLUE, "Citi ThankYou Points"),
            new CardDefinition("citi_strata_premier",        "Citi Strata Premier",              "Citi", "Mastercard",  95, false, false, CITI_BLUE, CITI_DARK, "Citi ThankYou Points"),
            new CardDefinition("citi_strata",                "Citi Strata",                      "Citi", "Mastercard",   0, false, false, CITI_BLUE, CITI_DARK, "Citi ThankYou Points"),
            new CardDefinition("citi_double_cash",           "Citi Double Cash",                 "Citi", "Mastercard",   0, false, false, CITI_BLUE, CITI_DARK, "Cash Back"),
            new CardDefinition("citi_custom_cash",           "Citi Custom Cash",                 "Citi", "Mastercard",   0, false, false, CITI_BLUE, CITI_DARK, "Citi ThankYou Points"),
            new CardDefinition("costco_citi",                "Costco Anywhere Visa by Citi",     "Citi", "Visa",         0, false, false, CITI_DARK, CITI_BLUE, "Cash Back"),

            // Citi Personal — AAdvantage (American Airlines Miles)
            new CardDefinition("citi_aadvantage_executive",  "Citi AAdvantage Executive",        "Citi", "Mastercard", 595, false, false, AA_SILVER, AA_RED, "AAdvantage Miles"),
            new CardDefinition("citi_aadvantage_globe",      "Citi AAdvantage Globe",            "Citi", "Mastercard", 350, false, false, AA_SILVER, AA_RED, "AAdvantage Miles"),
            new CardDefinition("citi_aadvantage_platinum",   "Citi AAdvantage Platinum Select",  "Citi", "Mastercard",  99, false, false, CITI_BLUE, CITI_DARK, "AAdvantage Miles"),
            new CardDefinition("citi_aadvantage_mileup",     "AAdvantage MileUp",                "Citi", "Mastercard",   0, false, false, CITI_BLUE, CITI_DARK, "AAdvantage Miles"),

            // Bank of America Personal
            new CardDefinition("bofa_customized_cash",       "BofA Customized Cash Rewards",  "Bank of America", "Visa",   0, false, false, BOFA_RED, BOFA_DARK, "Cash Back"),
            new CardDefinition("bofa_unlimited_cash",        "BofA Unlimited Cash Rewards",   "Bank of America", "Visa",   0, false, false, BOFA_RED, BOFA_DARK, "Cash Back"),
            new CardDefinition("bofa_premium_rewards",       "BofA Premium Rewards",          "Bank of America", "Visa",  95, false, false, BOFA_RED, BOFA_DARK, "BofA Points"),
            new CardDefinition("bofa_premium_rewards_elite", "BofA Premium Rewards Elite",    "Bank of America", "Visa", 550, false, false, BOFA_DARK, BOFA_RED, "BofA Points"),
            new CardDefinition("atmos_ascent",               "Atmos Rewards Ascent",          "Bank of America", "Visa",  95, false, false, ATMOS_BLUE, ATMOS_DARK, "Atmos/Alaska Rewards Miles"),
            new CardDefinition("atmos_summit",               "Atmos Rewards Summit",          "Bank of America", "Visa", 395, false, false, ATMOS_DARK, ATMOS_BLUE, "Atmos/Alaska Rewards Miles"),
            new CardDefinition("bofa_travel_rewards",        "BofA Travel Rewards",           "Bank of America", "Visa",   0, false, false, BOFA_RED, BOFA_DARK, "BofA Points"),
            new CardDefinition("air_france_klm_visa",        "Air France KLM Visa Signature", "Bank of America", "Visa",  89, false, false, AF_BLUE, AF_RED, "Flying Blue Miles"),
            new CardDefinition("free_spirit_travel_more",    "Free Spirit Travel More",       "Bank of America", "Mastercard", 79, false, false, FS_YELLOW, FS_BLACK, "Free Spirit Points"),
            new CardDefinition("allways_rewards",            "Allways Rewards Visa",          "Bank of America", "Visa",  59, false, false, ALLEGIANT_DARK, ALLEGIANT_ORANGE, "Cash Back"),
            new CardDefinition("royal_caribbean_visa",       "Royal Caribbean Visa Signature","Bank of America", "Visa",   0, false, false, RC_BLUE, RC_DARK, "Cash Back"),
            new CardDefinition("norwegian_cruise_card",      "Norwegian Cruise World Mastercard","Bank of America", "Mastercard", 0, false, false, NCL_BLUE, NCL_RED, "Cash Back"),
            new CardDefinition("celebrity_cruises_visa",     "Celebrity Cruises Visa Signature","Bank of America", "Visa",  0, false, false, CEL_NAVY, CEL_GOLD, "Cash Back"),

            // US Bank Personal
            new CardDefinition("usbank_cash_plus",        "US Bank Cash+ Visa",               "US Bank", "Visa",   0, false, false, USB_RED, USB_DARK, "Cash Back"),
            new CardDefinition("usbank_altitude_go",      "US Bank Altitude Go",              "US Bank", "Visa",   0, false, false, USB_RED, USB_DARK, "Cash Back"),
            new CardDefinition("usbank_altitude_connect", "US Bank Altitude Connect",         "US Bank", "Visa",   0, false, false, USB_RED, USB_DARK, "Cash Back"),
            new CardDefinition("usbank_altitude_reserve", "US Bank Altitude Reserve",         "US Bank", "Visa", 400, false, false, USB_DARK, USB_RED, "Cash Back"),
            new CardDefinition("usbank_smartly",          "US Bank Smartly Visa Signature",   "US Bank", "Visa",   0, false, false, USB_RED, USB_DARK, "Cash Back"),

            // HSBC Personal
            new CardDefinition("hsbc_premier", "HSBC Premier Credit Card", "HSBC", "Mastercard",   0, false, false, HSBC_RED, HSBC_DARK, "HSBC Rewards Points"),
            new CardDefinition("hsbc_elite",   "HSBC Elite Credit Card",   "HSBC", "Mastercard", 495, false, false, HSBC_DARK, HSBC_RED, "HSBC Rewards Points"),

            // Discover Personal
            new CardDefinition("discover_it_cashback", "Discover it Cash Back", "Discover", "Discover", 0, false, false, DISC_ORANGE, DISC_DARK, "Cash Back"),
            new CardDefinition("discover_it_miles",    "Discover it Miles",     "Discover", "Discover", 0, false, false, DISC_ORANGE, DISC_DARK, "Cash Back"),

            // Wells Fargo Personal
            new CardDefinition("wf_active_cash",        "Wells Fargo Active Cash",       "Wells Fargo", "Visa",  0, false, false, WF_RED, WF_DARK, "Cash Back"),
            new CardDefinition("wf_autograph",          "Wells Fargo Autograph",         "Wells Fargo", "Visa",  0, false, false, WF_RED, WF_DARK, "Cash Back"),
            new CardDefinition("wf_autograph_journey",  "Wells Fargo Autograph Journey", "Wells Fargo", "Visa", 95, false, false, WF_DARK, WF_RED, "Cash Back"),

            // Other Personal
            new CardDefinition("apple_card",     "Apple Card",     "Apple", "Mastercard",   0, false, false, APPLE_SILVER, APPLE_DARK, "Cash Back"),
            new CardDefinition("bilt_blue",      "Bilt Blue",      "Bilt",  "Mastercard",   0, false, false, BILT_BLACK, BILT_GRAY,  "Bilt Points"),
            new CardDefinition("bilt_obsidian",  "Bilt Obsidian",  "Bilt",  "Mastercard",  95, false, false, BILT_BLACK, BILT_GOLD,  "Bilt Points"),
            new CardDefinition("bilt_palladium", "Bilt Palladium", "Bilt",  "Mastercard", 495, false, false, PALLADIUM,  BILT_BLACK, "Bilt Points"),

            // Chase Business
            new CardDefinition("csr_business",                   "Chase Sapphire Reserve Business",             "Chase", "Visa",  795, false, true, CHASE_DARK,   CHASE_BLUE,   "Chase Ultimate Rewards Points"),
            new CardDefinition("southwest_performance_business", "Southwest Rapid Rewards Performance Business","Chase", "Visa",  299, false, true, SW_RED,       SW_DARK,      "Southwest Rapid Rewards"),
            new CardDefinition("southwest_premier_business",     "Southwest Rapid Rewards Premier Business",    "Chase", "Visa",  149, false, true, SW_RED,       SW_DARK,      "Southwest Rapid Rewards"),
            new CardDefinition("ihg_premier_business",           "IHG One Rewards Premier Business",           "Chase", "Visa",   99, false, true, IHG_BLUE,     IHG_TEAL,     "IHG One Rewards Points"),
            new CardDefinition("united_business",                "United Business Card",                        "Chase", "Visa",  150, false, true, UNITED_DARK,  UNITED_BLUE,  "United MileagePlus"),
            new CardDefinition("united_club_business",           "United Club Business Card",                   "Chase", "Visa",  695, false, true, UNITED_DARK,  UNITED_BLUE,  "United MileagePlus"),
            new CardDefinition("world_of_hyatt_business",        "World of Hyatt Business Credit Card",         "Chase", "Visa",  199, false, true, HYATT_DARK,   HYATT_GOLD,   "World of Hyatt Points"),
            new CardDefinition("ink_preferred",       "Ink Business Preferred",    "Chase", "Visa",   95, false, true, CHASE_BLUE, CHASE_DARK, "Chase Ultimate Rewards Points"),
            new CardDefinition("ink_cash",            "Ink Business Cash",         "Chase", "Visa",    0, false, true, CHASE_BLUE, CHASE_DARK, "Chase Ultimate Rewards Points"),
            new CardDefinition("ink_unlimited",       "Ink Business Unlimited",    "Chase", "Visa",    0, false, true, CHASE_BLUE, CHASE_DARK, "Chase Ultimate Rewards Points"),
            new CardDefinition("ink_premier",         "Ink Business Premier",      "Chase", "Visa",  195, false, true, CHASE_DARK, CHASE_BLUE, "Cash Back"),
            new CardDefinition("amazon_business_prime","Amazon Business Prime Card","Chase", "Visa",    0, false, true, AMZN_BLACK, AMZN_ORANGE, "Cash Back"),

            // Amex Business
            new CardDefinition("amex_business_gold",           "Amex Business Gold",                     "American Express", "Amex", 375, false, true, AMEX_GOLD_C, AMEX_GOLD_D, "Amex Membership Rewards Points"),
            new CardDefinition("amex_business_platinum",       "Amex Business Platinum",                 "American Express", "Amex", 895, false, true, AMEX_PLAT_C, AMEX_PLAT_D, "Amex Membership Rewards Points"),
            new CardDefinition("amex_blue_business_plus",      "Amex Blue Business Plus",                "American Express", "Amex",   0, false, true, AMEX_BLUE, AMEX_BLUE_D, "Amex Membership Rewards Points"),
            new CardDefinition("amex_blue_business_cash",      "Amex Blue Business Cash",                "American Express", "Amex",   0, false, true, AMEX_BLUE, AMEX_BLUE_D, "Cash Back"),
            new CardDefinition("delta_gold_business_amex",     "Delta SkyMiles Gold Business Amex",      "American Express", "Amex", 150, false, true, DELTA_RED, DELTA_PURP, "Delta SkyMiles"),
            new CardDefinition("delta_platinum_business_amex", "Delta SkyMiles Platinum Business Amex",  "American Express", "Amex", 350, false, true, DELTA_PURP, DELTA_RED, "Delta SkyMiles"),
            new CardDefinition("delta_reserve_business_amex",  "Delta SkyMiles Reserve Business Amex",   "American Express", "Amex", 650, false, true, DELTA_PURP, DELTA_RED, "Delta SkyMiles"),
            new CardDefinition("hilton_business_amex",         "Hilton Honors Business Amex",            "American Express", "Amex", 195, false, true, HILTON_BLUE, HILTON_LIGHT, "Hilton Honors Points"),

            // Capital One Business
            new CardDefinition("c1_spark_miles",       "Capital One Spark Miles for Business", "Capital One", "Visa",       95, false, true, C1_RED, C1_DARK, "Capital One Miles"),
            new CardDefinition("c1_spark_cash_plus",   "Capital One Spark Cash Plus",          "Capital One", "Mastercard", 150, false, true, C1_RED, C1_DARK, "Cash Back"),
            new CardDefinition("c1_spark_cash_select", "Capital One Spark Cash Select",        "Capital One", "Mastercard",   0, false, true, C1_RED, C1_DARK, "Cash Back"),

            // Citi Business
            new CardDefinition("citi_aadvantage_business", "CitiBusiness AAdvantage Platinum Select", "Citi", "Mastercard", 99, false, true, CITI_BLUE, CITI_DARK, "AAdvantage Miles"),

            // US Bank Business
            new CardDefinition("usbank_business_cash_plus",      "US Bank Business Cash+",          "US Bank", "Visa",  0, false, true, USB_RED, USB_DARK, "Cash Back"),
            new CardDefinition("usbank_business_altitude_power", "US Bank Business Altitude Power", "US Bank", "Visa", 95, false, true, USB_RED, USB_DARK, "Cash Back")
        );
    }

    // ── Reward Rates ──────────────────────────────────────────────────────────

    public static List<RewardRate> getRewardRates() {
        List<RewardRate> list = new ArrayList<>();

        // Chase Sapphire Preferred
        list.add(new RewardRate("chase_sapphire_preferred", RewardCategory.TRAVEL_PORTAL, RateType.POINTS, 5.0));
        list.add(new RewardRate("chase_sapphire_preferred", RewardCategory.TRAVEL,        RateType.POINTS, 2.0));
        list.add(new RewardRate("chase_sapphire_preferred", RewardCategory.DINING,        RateType.POINTS, 3.0));
        list.add(new RewardRate("chase_sapphire_preferred", RewardCategory.GROCERIES,     RateType.POINTS, 3.0));
        list.add(new RewardRate("chase_sapphire_preferred", RewardCategory.GENERAL,       RateType.POINTS, 1.0));

        // Chase Sapphire Reserve
        list.add(new RewardRate("chase_sapphire_reserve", RewardCategory.TRAVEL_PORTAL, RateType.POINTS, 8.0));
        list.add(new RewardRate("chase_sapphire_reserve", RewardCategory.TRAVEL,        RateType.POINTS, 4.0));
        list.add(new RewardRate("chase_sapphire_reserve", RewardCategory.DINING,        RateType.POINTS, 3.0));
        list.add(new RewardRate("chase_sapphire_reserve", RewardCategory.GENERAL,       RateType.POINTS, 1.0));

        // Chase Freedom Unlimited
        list.add(new RewardRate("chase_freedom_unlimited", RewardCategory.TRAVEL_PORTAL, RateType.POINTS, 5.0));
        list.add(new RewardRate("chase_freedom_unlimited", RewardCategory.DINING,        RateType.POINTS, 3.0));
        list.add(new RewardRate("chase_freedom_unlimited", RewardCategory.GENERAL,       RateType.POINTS, 1.5));

        // Chase Freedom Flex (rotating categories)
        list.add(new RewardRate("chase_freedom_flex", RewardCategory.DINING,          RateType.POINTS, 3.0, true, "freedom_flex_rotating", false));
        list.add(new RewardRate("chase_freedom_flex", RewardCategory.GROCERIES,       RateType.POINTS, 5.0, true, "freedom_flex_rotating", false));
        list.add(new RewardRate("chase_freedom_flex", RewardCategory.GAS,             RateType.POINTS, 5.0, true, "freedom_flex_rotating", false));
        list.add(new RewardRate("chase_freedom_flex", RewardCategory.ENTERTAINMENT,   RateType.POINTS, 5.0, true, "freedom_flex_rotating", false));
        list.add(new RewardRate("chase_freedom_flex", RewardCategory.ONLINE_SHOPPING, RateType.POINTS, 5.0, true, "freedom_flex_rotating", false));
        list.add(new RewardRate("chase_freedom_flex", RewardCategory.TRAVEL_PORTAL,   RateType.POINTS, 5.0));
        list.add(new RewardRate("chase_freedom_flex", RewardCategory.GENERAL,         RateType.POINTS, 1.0));

        // Chase Freedom Rise
        list.add(new RewardRate("chase_freedom_rise", RewardCategory.GENERAL, RateType.CASHBACK, 1.5));

        // Amazon Prime Visa
        list.add(new RewardRate("amazon_prime_visa", RewardCategory.ONLINE_SHOPPING, RateType.CASHBACK, 5.0));
        list.add(new RewardRate("amazon_prime_visa", RewardCategory.GROCERIES,       RateType.CASHBACK, 5.0));
        list.add(new RewardRate("amazon_prime_visa", RewardCategory.DINING,          RateType.CASHBACK, 2.0));
        list.add(new RewardRate("amazon_prime_visa", RewardCategory.GAS,             RateType.CASHBACK, 2.0));
        list.add(new RewardRate("amazon_prime_visa", RewardCategory.GENERAL,         RateType.CASHBACK, 1.0));

        // United Explorer
        list.add(new RewardRate("united_explorer", RewardCategory.TRAVEL_UNITED, RateType.MILES, 2.0));
        list.add(new RewardRate("united_explorer", RewardCategory.DINING,        RateType.MILES, 2.0));
        list.add(new RewardRate("united_explorer", RewardCategory.GENERAL,       RateType.MILES, 1.0));

        // United Quest (3x United, 2x travel/dining, 1x general)
        list.add(new RewardRate("united_quest", RewardCategory.TRAVEL_UNITED, RateType.MILES, 3.0));
        list.add(new RewardRate("united_quest", RewardCategory.TRAVEL,        RateType.MILES, 2.0));
        list.add(new RewardRate("united_quest", RewardCategory.DINING,        RateType.MILES, 2.0));
        list.add(new RewardRate("united_quest", RewardCategory.GENERAL,       RateType.MILES, 1.0));

        // United Gateway (2x United/transit/gas, 1x general)
        list.add(new RewardRate("united_gateway", RewardCategory.TRAVEL_UNITED, RateType.MILES, 2.0));
        list.add(new RewardRate("united_gateway", RewardCategory.GAS,           RateType.MILES, 2.0));
        list.add(new RewardRate("united_gateway", RewardCategory.GENERAL,       RateType.MILES, 1.0));

        // United Club (4x United, 2x travel/dining, 1x general)
        list.add(new RewardRate("united_club", RewardCategory.TRAVEL_UNITED, RateType.MILES, 4.0));
        list.add(new RewardRate("united_club", RewardCategory.TRAVEL,        RateType.MILES, 2.0));
        list.add(new RewardRate("united_club", RewardCategory.DINING,        RateType.MILES, 2.0));
        list.add(new RewardRate("united_club", RewardCategory.GENERAL,       RateType.MILES, 1.0));

        // Southwest Rapid Rewards Plus (2x SW/gas/grocery first $5k, 1x general)
        list.add(new RewardRate("southwest_plus", RewardCategory.TRAVEL_SOUTHWEST, RateType.MILES, 2.0));
        list.add(new RewardRate("southwest_plus", RewardCategory.GAS,              RateType.MILES, 2.0));
        list.add(new RewardRate("southwest_plus", RewardCategory.GROCERIES,        RateType.MILES, 2.0));
        list.add(new RewardRate("southwest_plus", RewardCategory.GENERAL,          RateType.MILES, 1.0));

        // Southwest Rapid Rewards Priority (4x SW, 2x gas/restaurants, 1x general)
        list.add(new RewardRate("southwest_priority", RewardCategory.TRAVEL_SOUTHWEST, RateType.MILES, 4.0));
        list.add(new RewardRate("southwest_priority", RewardCategory.GAS,              RateType.MILES, 2.0));
        list.add(new RewardRate("southwest_priority", RewardCategory.DINING,           RateType.MILES, 2.0));
        list.add(new RewardRate("southwest_priority", RewardCategory.GENERAL,          RateType.MILES, 1.0));

        // Southwest Rapid Rewards Premier (3x SW, 2x grocery/restaurants first $8k, 1x general)
        list.add(new RewardRate("southwest_premier", RewardCategory.TRAVEL_SOUTHWEST, RateType.MILES, 3.0));
        list.add(new RewardRate("southwest_premier", RewardCategory.DINING,           RateType.MILES, 2.0));
        list.add(new RewardRate("southwest_premier", RewardCategory.GROCERIES,        RateType.MILES, 2.0));
        list.add(new RewardRate("southwest_premier", RewardCategory.GENERAL,          RateType.MILES, 1.0));

        // Marriott Bonvoy Boundless (6x Marriott, 3x grocery/gas/dining first $6k, 2x general)
        list.add(new RewardRate("marriott_boundless", RewardCategory.TRAVEL_MARRIOTT, RateType.MILES, 6.0));
        list.add(new RewardRate("marriott_boundless", RewardCategory.DINING,          RateType.MILES, 3.0));
        list.add(new RewardRate("marriott_boundless", RewardCategory.GROCERIES,       RateType.MILES, 3.0));
        list.add(new RewardRate("marriott_boundless", RewardCategory.GAS,             RateType.MILES, 3.0));
        list.add(new RewardRate("marriott_boundless", RewardCategory.GENERAL,         RateType.MILES, 2.0));

        // Marriott Bonvoy Bountiful (6x Marriott, 4x grocery/dining first $15k, 2x general)
        list.add(new RewardRate("marriott_bountiful", RewardCategory.TRAVEL_MARRIOTT, RateType.MILES, 6.0));
        list.add(new RewardRate("marriott_bountiful", RewardCategory.DINING,          RateType.MILES, 4.0));
        list.add(new RewardRate("marriott_bountiful", RewardCategory.GROCERIES,       RateType.MILES, 4.0));
        list.add(new RewardRate("marriott_bountiful", RewardCategory.GENERAL,         RateType.MILES, 2.0));

        // Marriott Bonvoy Bold (3x Marriott, 2x dining, 1x general)
        list.add(new RewardRate("marriott_bold", RewardCategory.TRAVEL_MARRIOTT, RateType.MILES, 3.0));
        list.add(new RewardRate("marriott_bold", RewardCategory.DINING,          RateType.MILES, 2.0));
        list.add(new RewardRate("marriott_bold", RewardCategory.GENERAL,         RateType.MILES, 1.0));

        // Chase Ritz-Carlton (6x Marriott, 2x general)
        list.add(new RewardRate("chase_ritz_carlton", RewardCategory.TRAVEL_MARRIOTT, RateType.MILES, 6.0));
        list.add(new RewardRate("chase_ritz_carlton", RewardCategory.GENERAL,         RateType.MILES, 2.0));

        // IHG One Rewards Premier (10x IHG, 5x dining, 3x gas, 2x general)
        list.add(new RewardRate("ihg_premier", RewardCategory.TRAVEL_IHG, RateType.MILES, 10.0));
        list.add(new RewardRate("ihg_premier", RewardCategory.DINING,    RateType.MILES,  5.0));
        list.add(new RewardRate("ihg_premier", RewardCategory.GAS,       RateType.MILES,  5.0));
        list.add(new RewardRate("ihg_premier", RewardCategory.GROCERIES, RateType.MILES,  5.0));
        list.add(new RewardRate("ihg_premier", RewardCategory.GENERAL,   RateType.MILES,  3.0));

        // IHG One Rewards Traveler (3x dining/gas/streaming, 2x general)
        list.add(new RewardRate("ihg_traveler", RewardCategory.DINING,  RateType.MILES, 3.0));
        list.add(new RewardRate("ihg_traveler", RewardCategory.GAS,     RateType.MILES, 3.0));
        list.add(new RewardRate("ihg_traveler", RewardCategory.GENERAL, RateType.MILES, 2.0));

        // World of Hyatt (4x Hyatt, 2x dining, 2x fitness/transit, 1x general)
        list.add(new RewardRate("world_of_hyatt", RewardCategory.TRAVEL_HYATT, RateType.MILES, 4.0));
        list.add(new RewardRate("world_of_hyatt", RewardCategory.TRAVEL,       RateType.MILES, 2.0));
        list.add(new RewardRate("world_of_hyatt", RewardCategory.DINING,       RateType.MILES, 2.0));
        list.add(new RewardRate("world_of_hyatt", RewardCategory.GENERAL,      RateType.MILES, 1.0));

        // Aeroplan (3x Air Canada/partner airlines, 1.5x grocery/dining/direct travel, 1x general)
        list.add(new RewardRate("aeroplan", RewardCategory.TRAVEL_AEROPLAN, RateType.MILES, 3.0));
        list.add(new RewardRate("aeroplan", RewardCategory.DINING,          RateType.MILES, 1.5));
        list.add(new RewardRate("aeroplan", RewardCategory.GROCERIES,       RateType.MILES, 1.5));
        list.add(new RewardRate("aeroplan", RewardCategory.GENERAL,         RateType.MILES, 1.0));

        // British Airways Visa Signature (3x BA/partner airlines, 2x hotels, 1x general)
        list.add(new RewardRate("british_airways_visa", RewardCategory.TRAVEL_BRITISH_AIRWAYS, RateType.MILES, 3.0));
        list.add(new RewardRate("british_airways_visa", RewardCategory.GENERAL,               RateType.MILES, 1.0));

        // Aer Lingus Visa Signature (3x Aer Lingus/partner airlines, 2x hotels, 1x general)
        list.add(new RewardRate("aer_lingus_visa", RewardCategory.TRAVEL_AER_LINGUS, RateType.MILES, 3.0));
        list.add(new RewardRate("aer_lingus_visa", RewardCategory.GENERAL,           RateType.MILES, 1.0));

        // Iberia Visa Signature (3x Iberia/partner airlines, 2x hotels, 1x general)
        list.add(new RewardRate("iberia_visa", RewardCategory.TRAVEL_IBERIA, RateType.MILES, 3.0));
        list.add(new RewardRate("iberia_visa", RewardCategory.GENERAL,       RateType.MILES, 1.0));

        // Amex Platinum (5X via Amex Travel portal, 5X flights booked direct, 1X general)
        list.add(new RewardRate("amex_platinum", RewardCategory.TRAVEL_PORTAL, RateType.POINTS, 5.0));
        list.add(new RewardRate("amex_platinum", RewardCategory.TRAVEL,        RateType.POINTS, 5.0));
        list.add(new RewardRate("amex_platinum", RewardCategory.GENERAL,       RateType.POINTS, 1.0));

        // Amex Gold (4X dining, 4X U.S. supermarkets, 3X flights, 1X general)
        list.add(new RewardRate("amex_gold", RewardCategory.DINING,    RateType.POINTS, 4.0));
        list.add(new RewardRate("amex_gold", RewardCategory.GROCERIES, RateType.POINTS, 4.0));
        list.add(new RewardRate("amex_gold", RewardCategory.TRAVEL,    RateType.POINTS, 3.0));
        list.add(new RewardRate("amex_gold", RewardCategory.GENERAL,   RateType.POINTS, 1.0));

        // Amex Green (3X travel/transit/dining, 1X general)
        list.add(new RewardRate("amex_green", RewardCategory.TRAVEL,  RateType.POINTS, 3.0));
        list.add(new RewardRate("amex_green", RewardCategory.DINING,  RateType.POINTS, 3.0));
        list.add(new RewardRate("amex_green", RewardCategory.GENERAL, RateType.POINTS, 1.0));

        // Amex Blue Cash Preferred (6% supermarkets/streaming, 3% transit/gas, 1% general)
        list.add(new RewardRate("amex_blue_cash_preferred", RewardCategory.GROCERIES,     RateType.CASHBACK, 6.0));
        list.add(new RewardRate("amex_blue_cash_preferred", RewardCategory.ENTERTAINMENT, RateType.CASHBACK, 6.0));
        list.add(new RewardRate("amex_blue_cash_preferred", RewardCategory.GAS,           RateType.CASHBACK, 3.0));
        list.add(new RewardRate("amex_blue_cash_preferred", RewardCategory.GENERAL,       RateType.CASHBACK, 1.0));

        // Amex Blue Cash Everyday (3% supermarkets/online retail/gas, 1% general)
        list.add(new RewardRate("amex_blue_cash_everyday", RewardCategory.GROCERIES,      RateType.CASHBACK, 3.0));
        list.add(new RewardRate("amex_blue_cash_everyday", RewardCategory.ONLINE_SHOPPING, RateType.CASHBACK, 3.0));
        list.add(new RewardRate("amex_blue_cash_everyday", RewardCategory.GAS,            RateType.CASHBACK, 3.0));
        list.add(new RewardRate("amex_blue_cash_everyday", RewardCategory.GENERAL,        RateType.CASHBACK, 1.0));

        // Delta SkyMiles Reserve Amex (3X Delta, 1X general)
        list.add(new RewardRate("delta_reserve_amex", RewardCategory.TRAVEL_DELTA, RateType.MILES, 3.0));
        list.add(new RewardRate("delta_reserve_amex", RewardCategory.GENERAL,      RateType.MILES, 1.0));

        // Delta SkyMiles Platinum Amex (3X Delta, 2X restaurants, 1X general)
        list.add(new RewardRate("delta_platinum_amex", RewardCategory.TRAVEL_DELTA, RateType.MILES, 3.0));
        list.add(new RewardRate("delta_platinum_amex", RewardCategory.DINING,       RateType.MILES, 2.0));
        list.add(new RewardRate("delta_platinum_amex", RewardCategory.GROCERIES,    RateType.MILES, 2.0));
        list.add(new RewardRate("delta_platinum_amex", RewardCategory.GENERAL,      RateType.MILES, 1.0));

        // Delta SkyMiles Gold Amex (2X restaurants & Delta, 1X general)
        list.add(new RewardRate("delta_gold_amex", RewardCategory.TRAVEL_DELTA, RateType.MILES, 2.0));
        list.add(new RewardRate("delta_gold_amex", RewardCategory.DINING,       RateType.MILES, 2.0));
        list.add(new RewardRate("delta_gold_amex", RewardCategory.GENERAL,      RateType.MILES, 1.0));

        // Delta SkyMiles Blue Amex (2X restaurants & Delta, 1X general)
        list.add(new RewardRate("delta_blue_amex", RewardCategory.TRAVEL_DELTA, RateType.MILES, 2.0));
        list.add(new RewardRate("delta_blue_amex", RewardCategory.DINING,       RateType.MILES, 2.0));
        list.add(new RewardRate("delta_blue_amex", RewardCategory.GENERAL,      RateType.MILES, 1.0));

        // Hilton Honors Amex Aspire (14X Hilton, 7X dining/airfare/car rental, 3X general)
        list.add(new RewardRate("hilton_aspire_amex", RewardCategory.TRAVEL_HILTON, RateType.POINTS, 14.0));
        list.add(new RewardRate("hilton_aspire_amex", RewardCategory.DINING,        RateType.POINTS,  7.0));
        list.add(new RewardRate("hilton_aspire_amex", RewardCategory.TRAVEL,        RateType.POINTS,  7.0));
        list.add(new RewardRate("hilton_aspire_amex", RewardCategory.GENERAL,       RateType.POINTS,  3.0));

        // Hilton Honors Amex Surpass (12X Hilton, 6X dining/supermarkets/gas, 4X online retail, 3X general)
        list.add(new RewardRate("hilton_surpass_amex", RewardCategory.TRAVEL_HILTON,  RateType.POINTS, 12.0));
        list.add(new RewardRate("hilton_surpass_amex", RewardCategory.DINING,         RateType.POINTS,  6.0));
        list.add(new RewardRate("hilton_surpass_amex", RewardCategory.GROCERIES,      RateType.POINTS,  6.0));
        list.add(new RewardRate("hilton_surpass_amex", RewardCategory.GAS,            RateType.POINTS,  6.0));
        list.add(new RewardRate("hilton_surpass_amex", RewardCategory.ONLINE_SHOPPING,RateType.POINTS,  4.0));
        list.add(new RewardRate("hilton_surpass_amex", RewardCategory.GENERAL,        RateType.POINTS,  3.0));

        // Hilton Honors Amex (no fee — 7X Hilton, 5X dining/supermarkets/gas, 3X general)
        list.add(new RewardRate("hilton_amex", RewardCategory.TRAVEL_HILTON, RateType.POINTS, 7.0));
        list.add(new RewardRate("hilton_amex", RewardCategory.DINING,        RateType.POINTS, 5.0));
        list.add(new RewardRate("hilton_amex", RewardCategory.GROCERIES,     RateType.POINTS, 5.0));
        list.add(new RewardRate("hilton_amex", RewardCategory.GAS,           RateType.POINTS, 5.0));
        list.add(new RewardRate("hilton_amex", RewardCategory.GENERAL,       RateType.POINTS, 3.0));

        // Marriott Bonvoy Brilliant Amex (6X Marriott, 3X airfare/dining, 2X general)
        list.add(new RewardRate("marriott_brilliant_amex", RewardCategory.TRAVEL_MARRIOTT, RateType.POINTS, 6.0));
        list.add(new RewardRate("marriott_brilliant_amex", RewardCategory.DINING,          RateType.POINTS, 3.0));
        list.add(new RewardRate("marriott_brilliant_amex", RewardCategory.TRAVEL,          RateType.POINTS, 3.0));
        list.add(new RewardRate("marriott_brilliant_amex", RewardCategory.GROCERIES,       RateType.POINTS, 3.0));
        list.add(new RewardRate("marriott_brilliant_amex", RewardCategory.GENERAL,         RateType.POINTS, 2.0));

        // Marriott Bonvoy Bevy Amex (6X Marriott, 4X dining/supermarkets up to $15k, 2X general)
        list.add(new RewardRate("marriott_bevy_amex", RewardCategory.TRAVEL_MARRIOTT, RateType.POINTS, 6.0));
        list.add(new RewardRate("marriott_bevy_amex", RewardCategory.DINING,          RateType.POINTS, 4.0));
        list.add(new RewardRate("marriott_bevy_amex", RewardCategory.GROCERIES,       RateType.POINTS, 4.0));
        list.add(new RewardRate("marriott_bevy_amex", RewardCategory.GENERAL,         RateType.POINTS, 2.0));

        // Capital One Venture X (10x hotels/car + 5x flights via C1 Travel portal; 2x general)
        list.add(new RewardRate("capital_one_venture_x", RewardCategory.TRAVEL_PORTAL, RateType.MILES, 10.0));
        list.add(new RewardRate("capital_one_venture_x", RewardCategory.GENERAL,       RateType.MILES,  2.0));

        // Capital One Venture (5x hotels/car via C1 Travel portal; 2x general)
        list.add(new RewardRate("capital_one_venture", RewardCategory.TRAVEL_PORTAL, RateType.MILES, 5.0));
        list.add(new RewardRate("capital_one_venture", RewardCategory.GENERAL,       RateType.MILES, 2.0));

        // Capital One Savor
        list.add(new RewardRate("capital_one_savor", RewardCategory.TRAVEL_PORTAL, RateType.MILES, 5.0));
        list.add(new RewardRate("capital_one_savor", RewardCategory.DINING,        RateType.MILES, 3.0));
        list.add(new RewardRate("capital_one_savor", RewardCategory.ENTERTAINMENT, RateType.MILES, 3.0));
        list.add(new RewardRate("capital_one_savor", RewardCategory.GROCERIES,     RateType.MILES, 3.0));
        list.add(new RewardRate("capital_one_savor", RewardCategory.GENERAL,       RateType.MILES, 1.0));

        // Capital One VentureOne
        list.add(new RewardRate("capital_one_venture_one", RewardCategory.TRAVEL_PORTAL, RateType.MILES, 5.0));
        list.add(new RewardRate("capital_one_venture_one", RewardCategory.GENERAL,       RateType.MILES, 1.25));

        // Capital One Quicksilver
        list.add(new RewardRate("capital_one_quicksilver", RewardCategory.GENERAL, RateType.CASHBACK, 1.5));

        // Capital One QuicksilverOne
        list.add(new RewardRate("capital_one_quicksilver_one", RewardCategory.GENERAL, RateType.CASHBACK, 1.5));

        // Citi Strata Elite (12x via Citi Travel portal, 3x hotels/air/dining direct, 1.5x general)
        list.add(new RewardRate("citi_strata_elite", RewardCategory.TRAVEL_PORTAL, RateType.POINTS, 12.0));
        list.add(new RewardRate("citi_strata_elite", RewardCategory.TRAVEL,        RateType.POINTS,  3.0));
        list.add(new RewardRate("citi_strata_elite", RewardCategory.DINING,        RateType.POINTS,  3.0));
        list.add(new RewardRate("citi_strata_elite", RewardCategory.GENERAL,       RateType.POINTS,  1.5));

        // Citi Strata Premier (10x via Citi Travel portal, 3x hotels/air/dining/grocery/gas direct, 1x general)
        list.add(new RewardRate("citi_strata_premier", RewardCategory.TRAVEL_PORTAL, RateType.POINTS, 10.0));
        list.add(new RewardRate("citi_strata_premier", RewardCategory.TRAVEL,        RateType.POINTS,  3.0));
        list.add(new RewardRate("citi_strata_premier", RewardCategory.DINING,        RateType.POINTS,  3.0));
        list.add(new RewardRate("citi_strata_premier", RewardCategory.GROCERIES,     RateType.POINTS,  3.0));
        list.add(new RewardRate("citi_strata_premier", RewardCategory.GAS,           RateType.POINTS,  3.0));
        list.add(new RewardRate("citi_strata_premier", RewardCategory.GENERAL,       RateType.POINTS,  1.0));

        // Citi Strata (5x via Citi Travel portal, 3x grocery/gas/self-select, 2x dining, 1x general)
        list.add(new RewardRate("citi_strata", RewardCategory.TRAVEL_PORTAL,   RateType.POINTS, 5.0));
        list.add(new RewardRate("citi_strata", RewardCategory.GROCERIES,     RateType.POINTS, 3.0));
        list.add(new RewardRate("citi_strata", RewardCategory.GAS,           RateType.POINTS, 3.0));
        list.add(new RewardRate("citi_strata", RewardCategory.ENTERTAINMENT, RateType.POINTS, 3.0));  // self-select category
        list.add(new RewardRate("citi_strata", RewardCategory.DINING,        RateType.POINTS, 2.0));
        list.add(new RewardRate("citi_strata", RewardCategory.GENERAL,       RateType.POINTS, 1.0));

        // Citi Double Cash (2% everywhere = 1% buy + 1% pay)
        list.add(new RewardRate("citi_double_cash", RewardCategory.GENERAL, RateType.CASHBACK, 2.0));

        // Citi Custom Cash (5% on top eligible category each billing cycle, 1% general)
        list.add(new RewardRate("citi_custom_cash", RewardCategory.DINING,          RateType.POINTS, 5.0, true, "citi_custom_cash_choice", false));
        list.add(new RewardRate("citi_custom_cash", RewardCategory.GROCERIES,       RateType.POINTS, 5.0, true, "citi_custom_cash_choice", false));
        list.add(new RewardRate("citi_custom_cash", RewardCategory.GAS,             RateType.POINTS, 5.0, true, "citi_custom_cash_choice", false));
        list.add(new RewardRate("citi_custom_cash", RewardCategory.TRAVEL,          RateType.POINTS, 5.0, true, "citi_custom_cash_choice", false));
        list.add(new RewardRate("citi_custom_cash", RewardCategory.ENTERTAINMENT,   RateType.POINTS, 5.0, true, "citi_custom_cash_choice", false));
        list.add(new RewardRate("citi_custom_cash", RewardCategory.ONLINE_SHOPPING, RateType.POINTS, 5.0, true, "citi_custom_cash_choice", false));
        list.add(new RewardRate("citi_custom_cash", RewardCategory.GENERAL,         RateType.POINTS, 1.0));

        // Costco Anywhere Visa by Citi (4% gas/EV, 3% dining/travel, 1% general)
        list.add(new RewardRate("costco_citi", RewardCategory.GAS,     RateType.CASHBACK, 4.0));
        list.add(new RewardRate("costco_citi", RewardCategory.DINING,  RateType.CASHBACK, 3.0));
        list.add(new RewardRate("costco_citi", RewardCategory.TRAVEL,  RateType.CASHBACK, 3.0));
        list.add(new RewardRate("costco_citi", RewardCategory.GENERAL, RateType.CASHBACK, 1.0));

        // Citi AAdvantage Executive (4x AA, 10x AAdvantage Hotels/Cars, 1x general)
        list.add(new RewardRate("citi_aadvantage_executive", RewardCategory.TRAVEL_AA, RateType.MILES, 4.0));
        list.add(new RewardRate("citi_aadvantage_executive", RewardCategory.GENERAL,   RateType.MILES, 1.0));

        // Citi AAdvantage Globe (3x AA, 6x AAdvantage Hotels, 2x dining/transit, 1x general)
        list.add(new RewardRate("citi_aadvantage_globe", RewardCategory.TRAVEL_AA, RateType.MILES, 3.0));
        list.add(new RewardRate("citi_aadvantage_globe", RewardCategory.DINING,    RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_globe", RewardCategory.GENERAL,   RateType.MILES, 1.0));

        // Citi AAdvantage Platinum Select (2x AA/dining/gas, 1x general)
        list.add(new RewardRate("citi_aadvantage_platinum", RewardCategory.TRAVEL_AA, RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_platinum", RewardCategory.DINING,    RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_platinum", RewardCategory.GAS,       RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_platinum", RewardCategory.GENERAL,   RateType.MILES, 1.0));

        // AAdvantage MileUp (2x AA/grocery, 1x general)
        list.add(new RewardRate("citi_aadvantage_mileup", RewardCategory.TRAVEL_AA, RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_mileup", RewardCategory.GROCERIES,  RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_mileup", RewardCategory.GENERAL,    RateType.MILES, 1.0));

        // BofA Customized Cash Rewards (choice)
        list.add(new RewardRate("bofa_customized_cash", RewardCategory.DINING,          RateType.CASHBACK, 3.0, true, "bofa_custom_cash_choice", false));
        list.add(new RewardRate("bofa_customized_cash", RewardCategory.TRAVEL,          RateType.CASHBACK, 3.0, true, "bofa_custom_cash_choice", false));
        list.add(new RewardRate("bofa_customized_cash", RewardCategory.GAS,             RateType.CASHBACK, 3.0, true, "bofa_custom_cash_choice", false));
        list.add(new RewardRate("bofa_customized_cash", RewardCategory.ONLINE_SHOPPING, RateType.CASHBACK, 3.0, true, "bofa_custom_cash_choice", false));
        list.add(new RewardRate("bofa_customized_cash", RewardCategory.ENTERTAINMENT,   RateType.CASHBACK, 3.0, true, "bofa_custom_cash_choice", false));
        list.add(new RewardRate("bofa_customized_cash", RewardCategory.GROCERIES,       RateType.CASHBACK, 2.0));
        list.add(new RewardRate("bofa_customized_cash", RewardCategory.GENERAL,         RateType.CASHBACK, 1.0));

        // BofA Unlimited Cash Rewards
        list.add(new RewardRate("bofa_unlimited_cash", RewardCategory.GENERAL, RateType.CASHBACK, 1.5));

        // BofA Premium Rewards
        list.add(new RewardRate("bofa_premium_rewards", RewardCategory.TRAVEL,  RateType.POINTS, 2.0));
        list.add(new RewardRate("bofa_premium_rewards", RewardCategory.DINING,  RateType.POINTS, 2.0));
        list.add(new RewardRate("bofa_premium_rewards", RewardCategory.GENERAL, RateType.POINTS, 1.5));

        // BofA Premium Rewards Elite
        list.add(new RewardRate("bofa_premium_rewards_elite", RewardCategory.TRAVEL,  RateType.POINTS, 2.0));
        list.add(new RewardRate("bofa_premium_rewards_elite", RewardCategory.DINING,  RateType.POINTS, 2.0));
        list.add(new RewardRate("bofa_premium_rewards_elite", RewardCategory.GENERAL, RateType.POINTS, 1.5));

        // Atmos Ascent (3x airline/Alaska; 2x gas/EV, cable/streaming, local transit; 1x general)
        list.add(new RewardRate("atmos_ascent", RewardCategory.TRAVEL_ALASKA, RateType.MILES, 3.0));
        list.add(new RewardRate("atmos_ascent", RewardCategory.GAS,           RateType.MILES, 2.0));
        list.add(new RewardRate("atmos_ascent", RewardCategory.ENTERTAINMENT, RateType.MILES, 2.0));
        list.add(new RewardRate("atmos_ascent", RewardCategory.GENERAL,       RateType.MILES, 1.0));

        // Atmos Summit
        list.add(new RewardRate("atmos_summit", RewardCategory.TRAVEL_ALASKA, RateType.MILES, 3.0));
        list.add(new RewardRate("atmos_summit", RewardCategory.DINING,        RateType.MILES, 3.0));
        list.add(new RewardRate("atmos_summit", RewardCategory.GENERAL,       RateType.MILES, 1.0));

        // BofA Travel Rewards (3x BofA Travel Center portal; 1.5x general)
        list.add(new RewardRate("bofa_travel_rewards", RewardCategory.TRAVEL_PORTAL, RateType.POINTS, 3.0));
        list.add(new RewardRate("bofa_travel_rewards", RewardCategory.GENERAL,       RateType.POINTS, 1.5));

        // Air France KLM Visa Signature (3x AF/KLM/SkyTeam; 3x dining; 1.5x general)
        list.add(new RewardRate("air_france_klm_visa", RewardCategory.TRAVEL_AIR_FRANCE_KLM, RateType.MILES, 3.0));
        list.add(new RewardRate("air_france_klm_visa", RewardCategory.DINING,                RateType.MILES, 3.0));
        list.add(new RewardRate("air_france_klm_visa", RewardCategory.GENERAL,               RateType.MILES, 1.5));

        // Free Spirit Travel More (3x Spirit purchases; 2x dining + grocery; 1x general)
        list.add(new RewardRate("free_spirit_travel_more", RewardCategory.TRAVEL_SPIRIT, RateType.POINTS, 3.0));
        list.add(new RewardRate("free_spirit_travel_more", RewardCategory.DINING,        RateType.POINTS, 2.0));
        list.add(new RewardRate("free_spirit_travel_more", RewardCategory.GROCERIES,     RateType.POINTS, 2.0));
        list.add(new RewardRate("free_spirit_travel_more", RewardCategory.GENERAL,       RateType.POINTS, 1.0));

        // Allways Rewards Visa (3x Allegiant purchases; 2x dining; 1x general)
        list.add(new RewardRate("allways_rewards", RewardCategory.TRAVEL_ALLEGIANT, RateType.CASHBACK, 3.0));
        list.add(new RewardRate("allways_rewards", RewardCategory.DINING,           RateType.CASHBACK, 2.0));
        list.add(new RewardRate("allways_rewards", RewardCategory.GENERAL,          RateType.CASHBACK, 1.0));

        // Royal Caribbean Visa Signature (2x RC + sister brands; 1x general)
        list.add(new RewardRate("royal_caribbean_visa", RewardCategory.TRAVEL_CRUISES, RateType.CASHBACK, 2.0));
        list.add(new RewardRate("royal_caribbean_visa", RewardCategory.GENERAL,        RateType.CASHBACK, 1.0));

        // Norwegian Cruise World Mastercard (3x Norwegian; 2x air + hotel; 1x general)
        list.add(new RewardRate("norwegian_cruise_card", RewardCategory.TRAVEL_CRUISES, RateType.CASHBACK, 3.0));
        list.add(new RewardRate("norwegian_cruise_card", RewardCategory.GENERAL,        RateType.CASHBACK, 1.0));

        // Celebrity Cruises Visa Signature (2x Celebrity + sister brands; 1x general)
        list.add(new RewardRate("celebrity_cruises_visa", RewardCategory.TRAVEL_CRUISES, RateType.CASHBACK, 2.0));
        list.add(new RewardRate("celebrity_cruises_visa", RewardCategory.GENERAL,        RateType.CASHBACK, 1.0));

        // US Bank Cash+ (choice 5%)
        list.add(new RewardRate("usbank_cash_plus", RewardCategory.DINING,          RateType.CASHBACK, 5.0, true, "usbank_cash_plus_choice1", false));
        list.add(new RewardRate("usbank_cash_plus", RewardCategory.GROCERIES,       RateType.CASHBACK, 5.0, true, "usbank_cash_plus_choice1", false));
        list.add(new RewardRate("usbank_cash_plus", RewardCategory.TRAVEL,          RateType.CASHBACK, 5.0, true, "usbank_cash_plus_choice1", false));
        list.add(new RewardRate("usbank_cash_plus", RewardCategory.ENTERTAINMENT,   RateType.CASHBACK, 5.0, true, "usbank_cash_plus_choice1", false));
        list.add(new RewardRate("usbank_cash_plus", RewardCategory.ONLINE_SHOPPING, RateType.CASHBACK, 5.0, true, "usbank_cash_plus_choice1", false));
        list.add(new RewardRate("usbank_cash_plus", RewardCategory.GAS,             RateType.CASHBACK, 2.0));
        list.add(new RewardRate("usbank_cash_plus", RewardCategory.GENERAL,         RateType.CASHBACK, 1.0));

        // US Bank Altitude Go
        list.add(new RewardRate("usbank_altitude_go", RewardCategory.DINING,    RateType.CASHBACK, 4.0));
        list.add(new RewardRate("usbank_altitude_go", RewardCategory.GROCERIES, RateType.CASHBACK, 2.0));
        list.add(new RewardRate("usbank_altitude_go", RewardCategory.GAS,       RateType.CASHBACK, 2.0));
        list.add(new RewardRate("usbank_altitude_go", RewardCategory.GENERAL,   RateType.CASHBACK, 1.0));

        // US Bank Altitude Connect
        list.add(new RewardRate("usbank_altitude_connect", RewardCategory.TRAVEL_PORTAL, RateType.CASHBACK, 5.0));
        list.add(new RewardRate("usbank_altitude_connect", RewardCategory.TRAVEL,        RateType.CASHBACK, 4.0));
        list.add(new RewardRate("usbank_altitude_connect", RewardCategory.GAS,           RateType.CASHBACK, 4.0));
        list.add(new RewardRate("usbank_altitude_connect", RewardCategory.DINING,        RateType.CASHBACK, 2.0));
        list.add(new RewardRate("usbank_altitude_connect", RewardCategory.GROCERIES,     RateType.CASHBACK, 2.0));
        list.add(new RewardRate("usbank_altitude_connect", RewardCategory.ENTERTAINMENT, RateType.CASHBACK, 2.0));
        list.add(new RewardRate("usbank_altitude_connect", RewardCategory.GENERAL,       RateType.CASHBACK, 1.0));

        // US Bank Smartly
        list.add(new RewardRate("usbank_smartly", RewardCategory.GENERAL, RateType.CASHBACK, 2.0));

        // US Bank Altitude Reserve
        list.add(new RewardRate("usbank_altitude_reserve", RewardCategory.TRAVEL_PORTAL, RateType.CASHBACK, 5.0));
        list.add(new RewardRate("usbank_altitude_reserve", RewardCategory.TRAVEL,        RateType.CASHBACK, 3.0));
        list.add(new RewardRate("usbank_altitude_reserve", RewardCategory.GENERAL,       RateType.CASHBACK, 1.0));

        // Discover it Cash Back (rotating)
        list.add(new RewardRate("discover_it_cashback", RewardCategory.DINING,          RateType.CASHBACK, 5.0, true, "discover_rotating", false));
        list.add(new RewardRate("discover_it_cashback", RewardCategory.GROCERIES,       RateType.CASHBACK, 5.0, true, "discover_rotating", false));
        list.add(new RewardRate("discover_it_cashback", RewardCategory.GAS,             RateType.CASHBACK, 5.0, true, "discover_rotating", false));
        list.add(new RewardRate("discover_it_cashback", RewardCategory.ENTERTAINMENT,   RateType.CASHBACK, 5.0, true, "discover_rotating", false));
        list.add(new RewardRate("discover_it_cashback", RewardCategory.ONLINE_SHOPPING, RateType.CASHBACK, 5.0, true, "discover_rotating", false));
        list.add(new RewardRate("discover_it_cashback", RewardCategory.GENERAL,         RateType.CASHBACK, 1.0));

        // Discover it Miles
        list.add(new RewardRate("discover_it_miles", RewardCategory.GENERAL, RateType.MILES, 1.5));

        // Wells Fargo Active Cash
        list.add(new RewardRate("wf_active_cash", RewardCategory.GENERAL, RateType.CASHBACK, 2.0));

        // Wells Fargo Autograph
        list.add(new RewardRate("wf_autograph", RewardCategory.TRAVEL,        RateType.POINTS, 3.0));
        list.add(new RewardRate("wf_autograph", RewardCategory.DINING,        RateType.POINTS, 3.0));
        list.add(new RewardRate("wf_autograph", RewardCategory.GAS,           RateType.POINTS, 3.0));
        list.add(new RewardRate("wf_autograph", RewardCategory.ENTERTAINMENT, RateType.POINTS, 3.0));
        list.add(new RewardRate("wf_autograph", RewardCategory.GENERAL,       RateType.POINTS, 1.0));

        // Wells Fargo Autograph Journey
        list.add(new RewardRate("wf_autograph_journey", RewardCategory.TRAVEL,  RateType.POINTS, 5.0));
        list.add(new RewardRate("wf_autograph_journey", RewardCategory.DINING,  RateType.POINTS, 3.0));
        list.add(new RewardRate("wf_autograph_journey", RewardCategory.GENERAL, RateType.POINTS, 1.0));

        // Apple Card
        list.add(new RewardRate("apple_card", RewardCategory.ONLINE_SHOPPING, RateType.CASHBACK, 3.0));
        list.add(new RewardRate("apple_card", RewardCategory.GENERAL,         RateType.CASHBACK, 2.0));

        // Bilt Blue
        list.add(new RewardRate("bilt_blue", RewardCategory.RENT_MORTGAGE, RateType.BILT_CASH, 4.0));
        list.add(new RewardRate("bilt_blue", RewardCategory.GENERAL,       RateType.BILT_CASH, 4.0));
        list.add(new RewardRate("bilt_blue", RewardCategory.GENERAL,       RateType.POINTS,    1.0));

        // Bilt Obsidian (dual-currency)
        list.add(new RewardRate("bilt_obsidian", RewardCategory.DINING,    RateType.POINTS,    3.0, true, "bilt_obsidian_choice", false));
        list.add(new RewardRate("bilt_obsidian", RewardCategory.DINING,    RateType.BILT_CASH, 4.0));
        list.add(new RewardRate("bilt_obsidian", RewardCategory.GROCERIES, RateType.POINTS,    3.0, true, "bilt_obsidian_choice", false));
        list.add(new RewardRate("bilt_obsidian", RewardCategory.GROCERIES, RateType.BILT_CASH, 4.0));
        list.add(new RewardRate("bilt_obsidian", RewardCategory.TRAVEL,    RateType.POINTS,    2.0));
        list.add(new RewardRate("bilt_obsidian", RewardCategory.TRAVEL,    RateType.BILT_CASH, 4.0));
        list.add(new RewardRate("bilt_obsidian", RewardCategory.GENERAL,   RateType.POINTS,    1.0));
        list.add(new RewardRate("bilt_obsidian", RewardCategory.GENERAL,   RateType.BILT_CASH, 4.0));
        list.add(new RewardRate("bilt_obsidian", RewardCategory.RENT_MORTGAGE, RateType.BILT_CASH, 4.0));
        list.add(new RewardRate("bilt_obsidian", RewardCategory.RENT_MORTGAGE, RateType.POINTS,    1.0));

        // Bilt Palladium
        list.add(new RewardRate("bilt_palladium", RewardCategory.GENERAL,       RateType.POINTS,    2.0));
        list.add(new RewardRate("bilt_palladium", RewardCategory.GENERAL,       RateType.BILT_CASH, 4.0));
        list.add(new RewardRate("bilt_palladium", RewardCategory.RENT_MORTGAGE, RateType.BILT_CASH, 4.0));

        // Chase Sapphire Reserve Business (8x portal, 4x travel/hotels direct, 1x general)
        list.add(new RewardRate("csr_business", RewardCategory.TRAVEL_PORTAL, RateType.POINTS, 8.0));
        list.add(new RewardRate("csr_business", RewardCategory.TRAVEL,        RateType.POINTS, 4.0));
        list.add(new RewardRate("csr_business", RewardCategory.GENERAL,       RateType.POINTS, 1.0));

        // Ink Business Preferred
        list.add(new RewardRate("ink_preferred", RewardCategory.TRAVEL,  RateType.POINTS, 3.0));
        list.add(new RewardRate("ink_preferred", RewardCategory.GENERAL, RateType.POINTS, 1.0));

        // Ink Business Cash
        list.add(new RewardRate("ink_cash", RewardCategory.ONLINE_SHOPPING, RateType.POINTS, 5.0));
        list.add(new RewardRate("ink_cash", RewardCategory.GAS,             RateType.POINTS, 2.0));
        list.add(new RewardRate("ink_cash", RewardCategory.DINING,          RateType.POINTS, 2.0));
        list.add(new RewardRate("ink_cash", RewardCategory.GENERAL,         RateType.POINTS, 1.0));

        // Ink Business Unlimited
        list.add(new RewardRate("ink_unlimited", RewardCategory.GENERAL, RateType.POINTS, 1.5));

        // Ink Business Premier (5x via Chase Travel portal; 2x general)
        list.add(new RewardRate("ink_premier", RewardCategory.TRAVEL_PORTAL, RateType.CASHBACK, 5.0));
        list.add(new RewardRate("ink_premier", RewardCategory.GENERAL,       RateType.CASHBACK, 2.0));

        // Amazon Business Prime
        list.add(new RewardRate("amazon_business_prime", RewardCategory.ONLINE_SHOPPING, RateType.CASHBACK, 5.0));
        list.add(new RewardRate("amazon_business_prime", RewardCategory.DINING,          RateType.CASHBACK, 2.0));
        list.add(new RewardRate("amazon_business_prime", RewardCategory.GAS,             RateType.CASHBACK, 2.0));
        list.add(new RewardRate("amazon_business_prime", RewardCategory.GENERAL,         RateType.CASHBACK, 1.0));

        // Southwest Rapid Rewards Performance Business (4x SW, 2x travel/hotels direct, 1x general)
        list.add(new RewardRate("southwest_performance_business", RewardCategory.TRAVEL_SOUTHWEST, RateType.MILES, 4.0));
        list.add(new RewardRate("southwest_performance_business", RewardCategory.TRAVEL,           RateType.MILES, 2.0));
        list.add(new RewardRate("southwest_performance_business", RewardCategory.GENERAL,          RateType.MILES, 1.0));

        // Southwest Rapid Rewards Premier Business (3x SW, 2x gas/dining first $8k, 1x general)
        list.add(new RewardRate("southwest_premier_business", RewardCategory.TRAVEL_SOUTHWEST, RateType.MILES, 3.0));
        list.add(new RewardRate("southwest_premier_business", RewardCategory.GAS,             RateType.MILES, 2.0));
        list.add(new RewardRate("southwest_premier_business", RewardCategory.DINING,          RateType.MILES, 2.0));
        list.add(new RewardRate("southwest_premier_business", RewardCategory.GENERAL,         RateType.MILES, 1.0));

        // IHG One Rewards Premier Business (10x IHG, 5x travel/gas/dining, 3x general)
        list.add(new RewardRate("ihg_premier_business", RewardCategory.TRAVEL_IHG, RateType.POINTS, 10.0));
        list.add(new RewardRate("ihg_premier_business", RewardCategory.TRAVEL,     RateType.POINTS,  5.0));
        list.add(new RewardRate("ihg_premier_business", RewardCategory.GAS,        RateType.POINTS,  5.0));
        list.add(new RewardRate("ihg_premier_business", RewardCategory.DINING,     RateType.POINTS,  5.0));
        list.add(new RewardRate("ihg_premier_business", RewardCategory.GENERAL,    RateType.POINTS,  3.0));

        // United Business Card (2x United/dining/gas, 1x general)
        list.add(new RewardRate("united_business", RewardCategory.TRAVEL_UNITED, RateType.MILES, 2.0));
        list.add(new RewardRate("united_business", RewardCategory.DINING,        RateType.MILES, 2.0));
        list.add(new RewardRate("united_business", RewardCategory.GAS,           RateType.MILES, 2.0));
        list.add(new RewardRate("united_business", RewardCategory.GENERAL,       RateType.MILES, 1.0));

        // United Club Business Card (2x United, 1.5x general)
        list.add(new RewardRate("united_club_business", RewardCategory.TRAVEL_UNITED, RateType.MILES, 2.0));
        list.add(new RewardRate("united_club_business", RewardCategory.GENERAL,       RateType.MILES, 1.5));

        // World of Hyatt Business (4x Hyatt, 2x dining/gas choice, 1x general)
        list.add(new RewardRate("world_of_hyatt_business", RewardCategory.TRAVEL_HYATT, RateType.POINTS, 4.0));
        list.add(new RewardRate("world_of_hyatt_business", RewardCategory.DINING,       RateType.POINTS, 2.0, true, "hyatt_biz_choice", false));
        list.add(new RewardRate("world_of_hyatt_business", RewardCategory.GAS,          RateType.POINTS, 2.0, true, "hyatt_biz_choice", false));
        list.add(new RewardRate("world_of_hyatt_business", RewardCategory.GENERAL,      RateType.POINTS, 1.0));

        // Amex Business Gold (auto top-2 categories — modelled as choice)
        list.add(new RewardRate("amex_business_gold", RewardCategory.DINING,          RateType.POINTS, 4.0, true, "amex_biz_gold_top2", false));
        list.add(new RewardRate("amex_business_gold", RewardCategory.TRAVEL,          RateType.POINTS, 4.0, true, "amex_biz_gold_top2", false));
        list.add(new RewardRate("amex_business_gold", RewardCategory.ONLINE_SHOPPING, RateType.POINTS, 4.0, true, "amex_biz_gold_top2", false));
        list.add(new RewardRate("amex_business_gold", RewardCategory.GAS,             RateType.POINTS, 4.0, true, "amex_biz_gold_top2", false));
        list.add(new RewardRate("amex_business_gold", RewardCategory.GENERAL,         RateType.POINTS, 1.0));

        // Amex Business Platinum (5X via Amex Travel portal, 5X flights booked direct, 1X general)
        list.add(new RewardRate("amex_business_platinum", RewardCategory.TRAVEL_PORTAL, RateType.POINTS, 5.0));
        list.add(new RewardRate("amex_business_platinum", RewardCategory.TRAVEL,        RateType.POINTS, 5.0));
        list.add(new RewardRate("amex_business_platinum", RewardCategory.GENERAL,       RateType.POINTS, 1.0));

        // Amex Blue Business Plus
        list.add(new RewardRate("amex_blue_business_plus", RewardCategory.GENERAL, RateType.POINTS, 2.0));

        // Amex Blue Business Cash
        list.add(new RewardRate("amex_blue_business_cash", RewardCategory.GENERAL, RateType.CASHBACK, 2.0));

        // Delta Gold Business
        list.add(new RewardRate("delta_gold_business_amex", RewardCategory.TRAVEL_DELTA, RateType.MILES, 2.0));
        list.add(new RewardRate("delta_gold_business_amex", RewardCategory.DINING,       RateType.MILES, 2.0));
        list.add(new RewardRate("delta_gold_business_amex", RewardCategory.GROCERIES,    RateType.MILES, 2.0));
        list.add(new RewardRate("delta_gold_business_amex", RewardCategory.GENERAL,      RateType.MILES, 1.0));

        // Delta Platinum Business
        list.add(new RewardRate("delta_platinum_business_amex", RewardCategory.TRAVEL_DELTA, RateType.MILES, 3.0));
        list.add(new RewardRate("delta_platinum_business_amex", RewardCategory.DINING,       RateType.MILES, 1.5));
        list.add(new RewardRate("delta_platinum_business_amex", RewardCategory.GENERAL,      RateType.MILES, 1.0));

        // Delta SkyMiles Reserve Business Amex (3x Delta, 1x general; 1.5x transit/shipping/office supply not modeled)
        list.add(new RewardRate("delta_reserve_business_amex", RewardCategory.TRAVEL_DELTA, RateType.MILES, 3.0));
        list.add(new RewardRate("delta_reserve_business_amex", RewardCategory.GENERAL,      RateType.MILES, 1.0));

        // Hilton Business Amex
        list.add(new RewardRate("hilton_business_amex", RewardCategory.TRAVEL_HILTON, RateType.POINTS, 12.0));
        list.add(new RewardRate("hilton_business_amex", RewardCategory.GROCERIES,     RateType.POINTS,  5.0));
        list.add(new RewardRate("hilton_business_amex", RewardCategory.GAS,           RateType.POINTS,  5.0));
        list.add(new RewardRate("hilton_business_amex", RewardCategory.DINING,        RateType.POINTS,  5.0));
        list.add(new RewardRate("hilton_business_amex", RewardCategory.GENERAL,       RateType.POINTS,  3.0));

        // C1 Spark Miles (5x via C1 Travel portal; 2x general)
        list.add(new RewardRate("c1_spark_miles", RewardCategory.TRAVEL_PORTAL, RateType.MILES, 5.0));
        list.add(new RewardRate("c1_spark_miles", RewardCategory.GENERAL,       RateType.MILES, 2.0));

        // C1 Spark Cash Plus
        list.add(new RewardRate("c1_spark_cash_plus", RewardCategory.GENERAL, RateType.CASHBACK, 2.0));

        // C1 Spark Cash Select
        list.add(new RewardRate("c1_spark_cash_select", RewardCategory.GENERAL, RateType.CASHBACK, 1.5));

        // CitiBusiness AAdvantage
        list.add(new RewardRate("citi_aadvantage_business", RewardCategory.TRAVEL_AA, RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_business", RewardCategory.GAS,       RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_business", RewardCategory.DINING,    RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_business", RewardCategory.GENERAL,   RateType.MILES, 1.0));

        // US Bank Business Cash+
        list.add(new RewardRate("usbank_business_cash_plus", RewardCategory.DINING,          RateType.CASHBACK, 5.0, true, "usbank_biz_cash_plus_choice", false));
        list.add(new RewardRate("usbank_business_cash_plus", RewardCategory.TRAVEL,          RateType.CASHBACK, 5.0, true, "usbank_biz_cash_plus_choice", false));
        list.add(new RewardRate("usbank_business_cash_plus", RewardCategory.ONLINE_SHOPPING, RateType.CASHBACK, 5.0, true, "usbank_biz_cash_plus_choice", false));
        list.add(new RewardRate("usbank_business_cash_plus", RewardCategory.GAS,             RateType.CASHBACK, 2.0));
        list.add(new RewardRate("usbank_business_cash_plus", RewardCategory.GENERAL,         RateType.CASHBACK, 1.0));

        // US Bank Business Altitude Power
        list.add(new RewardRate("usbank_business_altitude_power", RewardCategory.GENERAL, RateType.CASHBACK, 2.0));

        // HSBC Premier (3x groceries/gas, 2x general travel, 1x general)
        list.add(new RewardRate("hsbc_premier", RewardCategory.GROCERIES, RateType.POINTS, 3.0));
        list.add(new RewardRate("hsbc_premier", RewardCategory.GAS,       RateType.POINTS, 3.0));
        list.add(new RewardRate("hsbc_premier", RewardCategory.TRAVEL,    RateType.POINTS, 2.0));
        list.add(new RewardRate("hsbc_premier", RewardCategory.GENERAL,   RateType.POINTS, 1.0));

        // HSBC Elite (5x general travel, 2x dining, 1x general)
        list.add(new RewardRate("hsbc_elite", RewardCategory.TRAVEL,  RateType.POINTS, 5.0));
        list.add(new RewardRate("hsbc_elite", RewardCategory.DINING,  RateType.POINTS, 2.0));
        list.add(new RewardRate("hsbc_elite", RewardCategory.GENERAL, RateType.POINTS, 1.0));

        return list;
    }

    // ── Card Benefits ─────────────────────────────────────────────────────────

    public static List<CardBenefit> getCardBenefits() {
        return Arrays.asList(
            new CardBenefit("chase_sapphire_preferred", "Hotel Credit", "$50 annual hotel credit for hotel stays purchased through Chase Travel. Resets each card anniversary year.", 5000, ResetPeriod.ANNUALLY, false, ResetType.ANNIVERSARY),
            new CardBenefit("chase_sapphire_preferred", "DoorDash Credit", "$10/month ($120/yr) in DashPass credits on restaurant orders. DashPass membership included.", 1000, ResetPeriod.MONTHLY, false),

            new CardBenefit("chase_sapphire_reserve", "Dining Credit", "Up to $150 per period (Jan–Jun + Jul–Dec = $300/yr) at participating Sapphire Exclusive Tables restaurants via OpenTable.", 15000, ResetPeriod.SEMI_ANNUALLY, false),
            new CardBenefit("chase_sapphire_reserve", "The Edit Hotel Credit", "Up to $250 per period (Jan–Jun + Jul–Dec = $500/yr) for prepaid bookings at The Edit luxury hotels via Chase Travel. Two-night minimum required.", 25000, ResetPeriod.SEMI_ANNUALLY, false),
            new CardBenefit("chase_sapphire_reserve", "StubHub Credit", "Up to $150 per period (Jan–Jun + Jul–Dec = $300/yr) on StubHub and viagogo purchases. Activation required. Through 12/31/2027.", 15000, ResetPeriod.SEMI_ANNUALLY, false),
            new CardBenefit("chase_sapphire_reserve", "Apple TV+ / Music Credit", "Up to $20.83/month ($250/yr) for Apple TV+ and Apple Music subscriptions. Through 6/22/2027.", 2083, ResetPeriod.MONTHLY, false),
            new CardBenefit("chase_sapphire_reserve", "DoorDash Credit", "Up to $10/month ($120/yr) in DashPass credits on restaurant orders. DashPass membership included. Through 12/31/2027.", 1000, ResetPeriod.MONTHLY, false),
            new CardBenefit("chase_sapphire_reserve", "Lyft Credit", "Up to $10/month ($120/yr) in Lyft in-app credits. Through 9/30/2027.", 1000, ResetPeriod.MONTHLY, false),

            // United Explorer benefits
            new CardBenefit("united_explorer", "United Hotels Credit", "$50 statement credit per eligible prepaid hotel stay, up to twice per card anniversary year ($100/yr). Booked via United Hotels portal.", 5000, ResetPeriod.SEMI_ANNUALLY, false),
            new CardBenefit("united_explorer", "Rideshare Credit", "Up to $5/month ($60/yr) in statement credits for rideshare purchases. Annual opt-in required each calendar year.", 500, ResetPeriod.MONTHLY, false),
            new CardBenefit("united_explorer", "JSX Flight Credit", "Up to $100/yr in statement credits for flights purchased directly with JSX.", 10000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("united_explorer", "United Club Passes", "Two United Club one-time passes per card anniversary year.", 10000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("united_explorer", "Avis/Budget Credit", "Up to $50/yr in statement credits for prepaid car rentals booked on avis.com or budget.com.", 5000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("united_explorer", "Instacart Credit", "Up to $10/month ($120/yr) in statement credits for Instacart purchases.", 1000, ResetPeriod.MONTHLY, false),

            // United Quest benefits
            new CardBenefit("united_quest", "United Travel Credit", "$125 statement credit for United purchases each card anniversary year.", 12500, ResetPeriod.ANNUALLY, false),
            new CardBenefit("united_quest", "Renowned Hotels Credit", "Up to $150/yr in statement credits for prepaid hotel stays through United's Renowned Hotels & Resorts portal.", 15000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("united_quest", "JSX Flight Credit", "Up to $100/yr in statement credits for flights purchased directly with JSX.", 10000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("united_quest", "Rideshare Credit", "Up to $8/month ($96/yr) in statement credits for rideshare purchases.", 800, ResetPeriod.MONTHLY, false),
            new CardBenefit("united_quest", "Avis/Budget Credit", "Up to $50/yr in statement credits for prepaid car rentals booked on avis.com or budget.com.", 5000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("united_quest", "Instacart Credit", "Up to $15/month ($180/yr) in statement credits for Instacart purchases.", 1500, ResetPeriod.MONTHLY, false),

            // United Club benefits
            new CardBenefit("united_club", "Renowned Hotels Credit", "Up to $200/yr in statement credits for prepaid hotel stays through United's Renowned Hotels & Resorts portal.", 20000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("united_club", "Rideshare Credit", "Up to $150/yr in statement credits for rideshare purchases. Enrollment required.", 15000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("united_club", "JSX Flight Credit", "Up to $200/yr in statement credits for flights purchased directly with JSX.", 20000, ResetPeriod.ANNUALLY, false),

            // World of Hyatt benefits (no free night — removed from Credits tracking)


            // Amex Platinum benefits (current as of Feb 2026)
            new CardBenefit("amex_platinum", "Airline Fee Credit", "Up to $200/yr for airline incidental charges (checked bags, etc.) at one selected airline. Resets each calendar year — not on card anniversary. Enrollment required.", 20000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("amex_platinum", "FHR/THC Hotel Credit", "Up to $300 semi-annually ($600/yr) at Fine Hotels & Resorts or The Hotel Collection, prepaid rate required. FHR = 1 night min; THC = 2 nights min.", 30000, ResetPeriod.SEMI_ANNUALLY, false),
            new CardBenefit("amex_platinum", "Resy Credit", "Up to $100/quarter ($400/yr) at Resy-partnered restaurants. Enrollment required; no reservation needed, just pay with this card.", 10000, ResetPeriod.QUARTERLY, false),
            new CardBenefit("amex_platinum", "Uber Credit", "$15/month Uber Cash ($35 in December) — $200/yr total. Expires each month; requires adding card to Uber account.", 1500, ResetPeriod.MONTHLY, false),
            new CardBenefit("amex_platinum", "Uber One Credit", "Up to $120/yr in statement credits for Uber One auto-renewing membership. Enrollment required.", 12000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("amex_platinum", "Digital Entertainment Credit", "Up to $25/month ($300/yr) at Disney+, Hulu, ESPN+, Peacock, Paramount+, NYT, WSJ, YouTube Premium, or YouTube TV. Enrollment required.", 2500, ResetPeriod.MONTHLY, false),
            new CardBenefit("amex_platinum", "lululemon Credit", "Up to $75/quarter ($300/yr) at U.S. lululemon retail stores and lululemon.com (excluding outlets). Enrollment required.", 7500, ResetPeriod.QUARTERLY, false),
            new CardBenefit("amex_platinum", "Saks Fifth Avenue Credit", "Up to $50 Jan–Jun + $50 Jul–Dec ($100/yr) at Saks Fifth Avenue, online or in-store. No minimum purchase. Enrollment required.", 5000, ResetPeriod.SEMI_ANNUALLY, false),
            new CardBenefit("amex_platinum", "Walmart+ Credit", "$12.95/month statement credit when you pay for your Walmart+ membership with this card.", 1295, ResetPeriod.MONTHLY, false),
            new CardBenefit("amex_platinum", "Oura Ring Credit", "Up to $200/yr when you purchase an Oura Ring at ouraring.com. Enrollment required.", 20000, ResetPeriod.ANNUALLY, false),

            // Amex Gold benefits (current as of Feb 2026)
            new CardBenefit("amex_gold", "Dining Credit", "$10/month ($120/yr) at eligible dining partners (enrollment required)", 1000, ResetPeriod.MONTHLY, false),
            new CardBenefit("amex_gold", "Resy Credit", "$50 semi-annual ($100/yr) at eligible Resy restaurants (enrollment required)", 5000, ResetPeriod.SEMI_ANNUALLY, false),
            new CardBenefit("amex_gold", "Uber Cash", "$10/month ($120/yr) Uber Cash; requires adding card to Uber account", 1000, ResetPeriod.MONTHLY, false),
            new CardBenefit("amex_gold", "Dunkin' Credit", "$7/month statement credit for purchases at Dunkin'.", 700, ResetPeriod.MONTHLY, false),

            // Amex Green benefits (current as of Feb 2026)
            new CardBenefit("amex_green", "CLEAR Plus Credit", "Up to $209/yr toward CLEAR Plus membership (enrollment required)", 20900, ResetPeriod.ANNUALLY, false),

            // Amex Blue Cash Everyday benefits
            new CardBenefit("amex_blue_cash_everyday", "Disney Streaming Credit", "Up to $7/month ($84/yr) for Disney+, Hulu, or ESPN+ (enrollment required)", 700, ResetPeriod.MONTHLY, false),
            new CardBenefit("amex_blue_cash_everyday", "Home Chef Credit", "Up to $15/month ($180/yr) for Home Chef meal kits (enrollment required)", 1500, ResetPeriod.MONTHLY, false),

            // Amex Blue Cash Preferred benefits
            new CardBenefit("amex_blue_cash_preferred", "Disney Streaming Credit", "Up to $10/month ($120/yr) for Disney+, Hulu, or ESPN+ (enrollment required)", 1000, ResetPeriod.MONTHLY, false),

            // Delta SkyMiles Reserve Amex benefits
            new CardBenefit("delta_reserve_amex", "Delta Stays Credit", "Up to $200/yr for eligible prepaid bookings via Delta Stays", 20000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("delta_reserve_amex", "Resy Credit", "Up to $20/month ($240/yr) in statement credits at eligible Resy restaurants. Enrollment required.", 2000, ResetPeriod.MONTHLY, false),
            new CardBenefit("delta_reserve_amex", "Rideshare Credit", "Up to $10/month ($120/yr) in statement credits for rideshare purchases.", 1000, ResetPeriod.MONTHLY, false),

            // Delta SkyMiles Platinum Amex benefits
            new CardBenefit("delta_platinum_amex", "Delta Stays Credit", "Up to $150/yr for eligible prepaid bookings via Delta Stays", 15000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("delta_platinum_amex", "Resy Credit", "Up to $10/month ($120/yr) in statement credits at eligible Resy restaurants. Enrollment required.", 1000, ResetPeriod.MONTHLY, false),
            new CardBenefit("delta_platinum_amex", "Rideshare Credit", "Up to $10/month ($120/yr) in statement credits for rideshare purchases.", 1000, ResetPeriod.MONTHLY, false),

            // Delta SkyMiles Gold Amex benefits
            new CardBenefit("delta_gold_amex", "Delta Stays Credit", "Up to $100/yr for eligible prepaid bookings via Delta Stays", 10000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("delta_gold_amex", "Delta Flight Credit", "$200 Delta eCredit after $10,000 in eligible purchases in a calendar year", 20000, ResetPeriod.ANNUALLY, false),

            // Hilton Honors Amex Aspire benefits
            new CardBenefit("hilton_aspire_amex", "Hilton Resort Credit", "Up to $200 semi-annually ($400/yr) for eligible purchases made directly with participating Hilton Resorts — includes the room rate itself.", 20000, ResetPeriod.SEMI_ANNUALLY, false),
            new CardBenefit("hilton_aspire_amex", "Flight Credit", "Up to $50/quarter ($200/yr) on flights booked directly with airlines or amextravel.com. Covers the ticket itself — not just incidentals. No airline pre-selection required.", 5000, ResetPeriod.QUARTERLY, false),
            new CardBenefit("hilton_aspire_amex", "CLEAR Plus Credit", "Up to $189/yr toward a CLEAR Plus membership (covers full annual fee). Subject to auto-renewal.", 18900, ResetPeriod.ANNUALLY, false),

            // Hilton Honors Amex Surpass benefits
            new CardBenefit("hilton_surpass_amex", "Hilton Credit", "Up to $50/quarter ($200/yr) for eligible Hilton portfolio charges", 5000, ResetPeriod.QUARTERLY, false),

            // Marriott Bonvoy Brilliant Amex benefits
            new CardBenefit("marriott_brilliant_amex", "Brilliant Dining Credit", "Up to $25/month ($300/yr) in statement credits at eligible restaurants worldwide.", 2500, ResetPeriod.MONTHLY, false),

            // Marriott Bonvoy Bevy Amex benefits (no free night — removed from Credits tracking)
            new CardBenefit("capital_one_venture_x",    "Travel Credit", "$300 annual credit via Capital One Travel", 30000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("capital_one_venture_x",    "Anniversary Bonus", "10,000 bonus miles on account anniversary", 0, ResetPeriod.ANNUALLY, false),
            // Citi Strata Elite benefits
            new CardBenefit("citi_strata_elite", "Hotel Benefit", "$300 off a single hotel stay of 2+ nights booked via Citi Travel each year. Applied at booking; excludes taxes and fees.", 30000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("citi_strata_elite", "Splurge Credit", "Up to $200/yr statement credit across eligible partner brands selected from Citi's list. Enrollment required.", 20000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("citi_strata_elite", "Blacklane Credit", "Up to $100 per period (Jan–Jun + Jul–Dec = $200/yr) in statement credits for Blacklane chauffeur service.", 10000, ResetPeriod.SEMI_ANNUALLY, false),
            new CardBenefit("citi_strata_elite", "Admirals Club Passes", "4 American Airlines Admirals Club one-time passes per year (up to $300 value).", 30000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("citi_strata_elite", "Global Entry / TSA PreCheck", "Up to $120 statement credit for Global Entry or TSA PreCheck application fee. Once every 4 years.", 12000, ResetPeriod.ANNUALLY, false, ResetType.ANNIVERSARY),

            // Citi Strata Premier benefits
            new CardBenefit("citi_strata_premier", "Hotel Discount", "$100 off a single hotel stay of $500+ per year booked via Citi Travel. Excludes taxes and fees; applied at booking.", 10000, ResetPeriod.ANNUALLY, false),

            // Citi AAdvantage Executive benefits
            new CardBenefit("citi_aadvantage_executive", "Avis/Budget Rental Credit", "Up to $120/yr in statement credits for eligible prepaid car rentals booked directly on avis.com or budget.com.", 12000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("citi_aadvantage_executive", "Lyft Credit", "Up to $10/month ($120/yr) statement credit after taking 3+ eligible Lyft rides in a calendar month.", 1000, ResetPeriod.MONTHLY, false),
            new CardBenefit("citi_aadvantage_executive", "Grubhub Credit", "Up to $10 per billing statement ($120/yr) in statement credits for eligible Grubhub purchases.", 1000, ResetPeriod.MONTHLY, false),
            new CardBenefit("citi_aadvantage_executive", "Global Entry / TSA PreCheck", "Up to $120 statement credit for Global Entry or TSA PreCheck application fee. Once every 4 years.", 12000, ResetPeriod.ANNUALLY, false),

            // Citi AAdvantage Globe benefits
            new CardBenefit("citi_aadvantage_globe", "Turo Credit", "Up to $240/yr ($30 per eligible trip) in statement credits for Turo car sharing. Must link card to Turo account.", 24000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("citi_aadvantage_globe", "Inflight Credit", "Up to $100/yr in statement credits on inflight purchases made on American Airlines flights.", 10000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("citi_aadvantage_globe", "Splurge Credit", "Up to $100/yr statement credit across up to 2 selected brands from Citi's list of eligible partners.", 10000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("citi_aadvantage_globe", "Global Entry / TSA PreCheck", "Up to $120 statement credit for Global Entry or TSA PreCheck application fee. Once every 4 years.", 12000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("bofa_premium_rewards", "Airline Incidental Credit", "Up to $100/yr in statement credits for airline incidentals (seat upgrades, baggage fees, inflight services, lounge fees). Applied automatically.", 10000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("bofa_premium_rewards_elite","Travel Credit", "$300 annual airline/travel credit", 30000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("bofa_premium_rewards_elite","Lifestyle Credit", "$150 annual lifestyle credit", 15000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("air_france_klm_visa", "Anniversary Miles", "5,000 Flying Blue miles each anniversary year after at least $50 in purchases within the anniversary year.", 0, ResetPeriod.ANNUALLY, false),
            new CardBenefit("free_spirit_travel_more", "Companion Flight Voucher", "$100 companion flight voucher each anniversary year after $5,000 in purchases in the prior anniversary year.", 10000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("usbank_altitude_reserve",  "Travel Credit", "$325 annual travel/dining credit", 32500, ResetPeriod.ANNUALLY, false),
            new CardBenefit("bilt_palladium",           "Annual Bilt Cash", "$200 annual Bilt Cash credit", 20000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("bilt_palladium",           "Travel Hotel Credit", "Up to $200 semi-annually ($400/yr) for eligible hotel stays booked through Bilt Travel.", 20000, ResetPeriod.SEMI_ANNUALLY, false),

            // (Discover it & Chase Freedom Flex quarterly rotating categories
            //  are tracked via the Quarterly Benefit feature, not as fixed credits)

            // Southwest Priority
            new CardBenefit("southwest_priority", "Anniversary Bonus Points", "7,500 bonus Rapid Rewards points each card anniversary year.", 9750, ResetPeriod.ANNUALLY, false, ResetType.ANNIVERSARY),

            // Southwest Premier
            new CardBenefit("southwest_premier", "Anniversary Points", "6,000 bonus Rapid Rewards points each card anniversary year.", 7800, ResetPeriod.ANNUALLY, false, ResetType.ANNIVERSARY),

            // Capital One Venture
            new CardBenefit("capital_one_venture", "Global Entry / TSA PreCheck", "Up to $100 statement credit for Global Entry ($100) or TSA PreCheck (~$85) application fee.", 10000, ResetPeriod.ANNUALLY, false),

            // Amex Business Platinum
            new CardBenefit("amex_business_platinum", "Airline Fee Credit", "Up to $200/yr for incidental charges (e.g. checked bags) at one selected U.S. airline. Resets each calendar year — not on card anniversary, so you can use it twice in your first year. Enrollment required.", 20000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("amex_business_platinum", "FHR/THC Hotel Credit", "Up to $300 semi-annually ($600/yr) at Fine Hotels & Resorts or The Hotel Collection. Prepaid rate required. FHR = 1 night min; THC = 2 nights min.", 30000, ResetPeriod.SEMI_ANNUALLY, false),
            new CardBenefit("amex_business_platinum", "Hilton Credit", "Up to $50/quarter ($200/yr) in statement credits for eligible Hilton portfolio charges. Enrollment requires a Hilton for Business account.", 5000, ResetPeriod.QUARTERLY, false),
            new CardBenefit("amex_business_platinum", "Dell Technologies Credit", "Up to $150/yr in statement credits for U.S. Dell Technologies purchases. No minimum purchase required. Enrollment required.", 15000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("amex_business_platinum", "Wireless Credit", "Up to $10/month ($120/yr) in statement credits for U.S. wireless telephone service purchases. Enrollment required.", 1000, ResetPeriod.MONTHLY, false),
            new CardBenefit("amex_business_platinum", "Indeed Credit", "Up to $90/quarter ($360/yr) in statement credits for Indeed job posting purchases. Enrollment required.", 9000, ResetPeriod.QUARTERLY, false),
            new CardBenefit("amex_business_platinum", "Adobe Credit", "Up to $150/yr in statement credits for an annually prepaid Adobe plan. Enrollment required.", 15000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("amex_business_platinum", "Global Entry / TSA PreCheck", "Up to $100 statement credit for Global Entry ($100) or TSA PreCheck (~$85) application fee.", 10000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("amex_business_platinum", "Clear Credit", "Up to $179/yr statement credit for a CLEAR Plus membership — same as the annual CLEAR fee. Enrollment required.", 17900, ResetPeriod.ANNUALLY, false),

            // Chase Sapphire Reserve Business
            new CardBenefit("csr_business", "Travel Credit", "Up to $300/yr in automatic statement credits for travel purchases (airfare, hotels, taxis, tolls, etc.). Resets each card anniversary year. No enrollment needed.", 30000, ResetPeriod.ANNUALLY, false, ResetType.ANNIVERSARY),
            new CardBenefit("csr_business", "The Edit Hotel Credit", "Up to $250 per period (Jan–Jun + Jul–Dec = $500/yr) for prepaid bookings at The Edit luxury hotels via Chase Travel. Two-night minimum required. Starting 2026, two $250 credits usable anytime throughout the year.", 25000, ResetPeriod.SEMI_ANNUALLY, false),
            new CardBenefit("csr_business", "DoorDash Credit", "Up to $25/month on DoorDash (includes $5 promo on restaurant orders + two $10 promos on groceries/retail). Complimentary DashPass included. Through 12/31/2027.", 2500, ResetPeriod.MONTHLY, false),
            new CardBenefit("csr_business", "Lyft Credit", "Up to $10/month in Lyft in-app credits through 9/30/2027.", 1000, ResetPeriod.MONTHLY, false),
            new CardBenefit("csr_business", "ZipRecruiter Credit", "Up to $200 per period (Jan–Jun + Jul–Dec = $400/yr) in statement credits for ZipRecruiter.", 20000, ResetPeriod.SEMI_ANNUALLY, false),
            new CardBenefit("csr_business", "Google Workspace Credit", "Up to $200/yr in statement credits for purchases made directly on Google Workspace.", 20000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("csr_business", "Giftcards.com Credit", "Up to $50 per period (Jan–Jun + Jul–Dec = $100/yr) in statement credits for gift cards from the curated collection at giftcards.com/reservebusiness.", 5000, ResetPeriod.SEMI_ANNUALLY, false),

            // Southwest Rapid Rewards Performance Business
            // (no tracked statement credits)

            // Southwest Rapid Rewards Premier Business
            // (no tracked statement credits)

            // IHG One Rewards Premier Business
            new CardBenefit("ihg_premier_business", "United TravelBank Credit", "$25 United TravelBank cash deposited on or about January 1 and again on or about July 1 ($50/yr). Usable on united.com.", 2500, ResetPeriod.SEMI_ANNUALLY, false),

            // United Business Card
            new CardBenefit("united_business", "United Hotels Credit", "$50 statement credit for each of your first 2 prepaid hotel stays per anniversary year ($100/yr) booked directly through United Hotels with this card.", 5000, ResetPeriod.SEMI_ANNUALLY, false, ResetType.ANNIVERSARY),
            new CardBenefit("united_business", "Avis/Budget Credit", "$25 United TravelBank cash for each of your first 2 Avis or Budget car rentals per anniversary year ($50/yr) booked directly through cars.united.com.", 2500, ResetPeriod.SEMI_ANNUALLY, false, ResetType.ANNIVERSARY),
            new CardBenefit("united_business", "Rideshare Credit", "Up to $8/month back on rideshare ($12 in December = $100/yr total). Yearly opt-in required.", 800, ResetPeriod.MONTHLY, false),
            new CardBenefit("united_business", "Instacart Credit", "$10/month in Instacart credits ($120/yr) for purchases directly through Instacart. Through 12/31/2027.", 1000, ResetPeriod.MONTHLY, false),
            new CardBenefit("united_business", "JSX Credit", "Up to $100/yr statement credit when booking flights directly with JSX.", 10000, ResetPeriod.ANNUALLY, false, ResetType.ANNIVERSARY),

            // United Club Business Card
            new CardBenefit("united_club_business", "Renowned Hotels Credit", "Up to $200/yr in statement credits for prepaid hotel stays through Renowned Hotels and Resorts with this card.", 20000, ResetPeriod.ANNUALLY, false, ResetType.ANNIVERSARY),
            new CardBenefit("united_club_business", "Avis/Budget Credit", "$50 United TravelBank cash for each of your first 2 Avis or Budget car rentals per anniversary year ($100/yr) booked directly through cars.united.com.", 5000, ResetPeriod.SEMI_ANNUALLY, false, ResetType.ANNIVERSARY),
            new CardBenefit("united_club_business", "Rideshare Credit", "Up to $12/month back on rideshare ($18 in December = $150/yr total). Yearly opt-in required.", 1200, ResetPeriod.MONTHLY, false),
            new CardBenefit("united_club_business", "Instacart Credit", "Two $10/month Instacart credits ($240/yr) for purchases directly through Instacart. Through 12/31/2027.", 2000, ResetPeriod.MONTHLY, false),
            new CardBenefit("united_club_business", "JSX Credit", "Up to $200/yr statement credit when booking flights directly with JSX — a hop-on jet service offering a semi-private flying experience at commercial fares.", 20000, ResetPeriod.ANNUALLY, false, ResetType.ANNIVERSARY),

            // World of Hyatt Business
            new CardBenefit("world_of_hyatt_business", "Hyatt Credit", "Up to $50 twice per cardmember year ($100/yr) in statement credits at Hyatt properties.", 5000, ResetPeriod.SEMI_ANNUALLY, false, ResetType.ANNIVERSARY),

            // Amex Business Gold
            new CardBenefit("amex_business_gold", "Flexible Business Credit", "Up to $20/month ($240/yr) in statement credits at FedEx, Grubhub, or eligible office supply stores. Enrollment required.", 2000, ResetPeriod.MONTHLY, false),
            new CardBenefit("amex_business_gold", "Walmart+ Credit", "$12.95/month statement credit when you pay for your Walmart+ membership with this card.", 1295, ResetPeriod.MONTHLY, false),

            // Delta SkyMiles Reserve Business Amex
            new CardBenefit("delta_reserve_business_amex", "Resy Credit", "Up to $20/month ($240/yr) at Resy-partnered restaurants. No reservation required — just dine at a Resy partner and pay with this card. Enrollment required.", 2000, ResetPeriod.MONTHLY, false),
            new CardBenefit("delta_reserve_business_amex", "Rideshare Credit", "Up to $10/month ($120/yr) in statement credits on rideshare purchases. Enrollment required.", 1000, ResetPeriod.MONTHLY, false),
            new CardBenefit("delta_reserve_business_amex", "Delta Stays Credit", "Up to $250/yr in statement credits for prepaid hotels or vacation rentals booked through Delta Stays on delta.com/stays.", 25000, ResetPeriod.ANNUALLY, false),

            // Hilton Honors Business Amex
            new CardBenefit("hilton_business_amex", "Hilton Credit", "Up to $60/quarter ($240/yr) in statement credits for eligible Hilton portfolio charges.", 6000, ResetPeriod.QUARTERLY, false),

            // Wells Fargo Autograph Journey
            new CardBenefit("wf_autograph_journey",   "Airline Credit", "Up to $50/yr statement credit on airline purchases charged to the card.", 5000, ResetPeriod.ANNUALLY, false),

            // Chase Ritz-Carlton
            new CardBenefit("chase_ritz_carlton", "Air Credit", "$300 annual air credit for airline incidentals or airfare booked through Chase Travel. Resets each calendar year.", 30000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("chase_ritz_carlton", "Global Entry / TSA PreCheck", "Up to $100 statement credit for Global Entry or TSA PreCheck application fee.", 10000, ResetPeriod.ANNUALLY, false, ResetType.ANNIVERSARY),
            new CardBenefit("chase_ritz_carlton", "85K Free Night Award", "One 85,000-point free night award each card anniversary year, redeemable at Marriott Bonvoy properties.", 0, ResetPeriod.ANNUALLY, false, ResetType.ANNIVERSARY),

            // US Bank Altitude Connect
            new CardBenefit("usbank_altitude_connect", "Global Entry / TSA PreCheck", "Up to $100 statement credit for Global Entry or TSA PreCheck application fee, once every 4 years.", 10000, ResetPeriod.ANNUALLY, false, ResetType.ANNIVERSARY),

            // HSBC Elite
            new CardBenefit("hsbc_elite", "Priceline Travel Credit", "Up to $400/yr in statement credits for eligible travel booked through the Priceline platform. Resets each calendar year.", 40000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("hsbc_elite", "Rideshare Credit", "Up to $10/month ($120/yr) in statement credits for eligible rideshare purchases.", 1000, ResetPeriod.MONTHLY, false)
        );
    }
}

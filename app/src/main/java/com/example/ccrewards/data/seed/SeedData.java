package com.example.ccrewards.data.seed;

import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.RateType;
import com.example.ccrewards.data.model.ResetPeriod;
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

    // ── Card Definitions ──────────────────────────────────────────────────────

    public static List<CardDefinition> getCardDefinitions() {
        return Arrays.asList(
            // Chase Personal
            new CardDefinition("chase_sapphire_preferred", "Chase Sapphire Preferred", "Chase", "Visa", 95, false, false, CHASE_BLUE, CHASE_DARK, "Chase Ultimate Rewards Points"),
            new CardDefinition("chase_sapphire_reserve",   "Chase Sapphire Reserve",   "Chase", "Visa", 550, false, false, CHASE_DARK, CHASE_BLUE, "Chase Ultimate Rewards Points"),
            new CardDefinition("chase_freedom_unlimited",  "Chase Freedom Unlimited",  "Chase", "Visa",   0, false, false, CHASE_BLUE, CHASE_DARK, "Chase Ultimate Rewards Points"),
            new CardDefinition("chase_freedom_flex",       "Chase Freedom Flex",       "Chase", "Mastercard", 0, false, false, CHASE_BLUE, CHASE_DARK, "Chase Ultimate Rewards Points"),
            new CardDefinition("chase_freedom_rise",       "Chase Freedom Rise",       "Chase", "Visa",   0, false, false, CHASE_BLUE, CHASE_DARK, "Chase Ultimate Rewards Points"),
            new CardDefinition("amazon_prime_visa",        "Amazon Prime Visa",        "Chase", "Visa",   0, false, false, AMZN_BLACK, AMZN_ORANGE, "Cash Back"),
            new CardDefinition("united_explorer",          "United Explorer Card",     "Chase", "Visa",  95, false, false, UNITED_DARK, UNITED_BLUE, "United MileagePlus"),

            // Amex Personal
            new CardDefinition("amex_gold",                "Amex Gold Card",               "American Express", "Amex", 250, false, false, AMEX_GOLD_C, AMEX_GOLD_D, "Amex Membership Rewards Points"),
            new CardDefinition("amex_platinum",            "Amex Platinum Card",           "American Express", "Amex", 695, false, false, AMEX_PLAT_C, AMEX_PLAT_D, "Amex Membership Rewards Points"),
            new CardDefinition("amex_green",               "Amex Green Card",              "American Express", "Amex", 150, false, false, AMEX_GREEN_C, AMEX_GREEN_D, "Amex Membership Rewards Points"),
            new CardDefinition("amex_blue_cash_preferred", "Amex Blue Cash Preferred",     "American Express", "Amex",  95, false, false, AMEX_BLUE, AMEX_BLUE_D, "Cash Back"),
            new CardDefinition("amex_blue_cash_everyday",  "Amex Blue Cash Everyday",      "American Express", "Amex",   0, false, false, AMEX_BLUE, AMEX_BLUE_D, "Cash Back"),
            new CardDefinition("amex_everyday_preferred",  "Amex EveryDay Preferred",      "American Express", "Amex",  95, false, false, AMEX_BLUE, AMEX_BLUE_D, "Amex Membership Rewards Points"),
            new CardDefinition("amex_everyday",            "Amex EveryDay",                "American Express", "Amex",   0, false, false, AMEX_BLUE, AMEX_BLUE_D, "Amex Membership Rewards Points"),
            new CardDefinition("delta_gold_amex",          "Delta SkyMiles Gold Amex",     "American Express", "Amex", 150, false, false, DELTA_RED, DELTA_PURP, "Delta SkyMiles"),
            new CardDefinition("delta_platinum_amex",      "Delta SkyMiles Platinum Amex", "American Express", "Amex", 350, false, false, DELTA_PURP, DELTA_RED, "Delta SkyMiles"),
            new CardDefinition("hilton_surpass_amex",      "Hilton Honors Amex Surpass",   "American Express", "Amex", 150, false, false, HILTON_BLUE, HILTON_LIGHT, "Hilton Honors Points"),
            new CardDefinition("hilton_aspire_amex",       "Hilton Honors Amex Aspire",    "American Express", "Amex", 550, false, false, HILTON_BLUE, HILTON_LIGHT, "Hilton Honors Points"),

            // Capital One Personal
            new CardDefinition("capital_one_venture_x",  "Capital One Venture X",    "Capital One", "Visa",      395, false, false, C1_DARK, C1_RED,  "Capital One Miles"),
            new CardDefinition("capital_one_venture",    "Capital One Venture",       "Capital One", "Visa",       95, false, false, C1_RED,  C1_DARK, "Capital One Miles"),
            new CardDefinition("capital_one_savor",      "Capital One Savor",         "Capital One", "Mastercard",  0, false, false, C1_RED,  C1_DARK, "Cash Back"),
            new CardDefinition("capital_one_quicksilver","Capital One Quicksilver",   "Capital One", "Mastercard",  0, false, false, C1_RED,  C1_DARK, "Cash Back"),

            // Citi Personal
            new CardDefinition("citi_strata_premier",        "Citi Strata Premier",              "Citi", "Mastercard",  95, false, false, CITI_BLUE, CITI_DARK, "Citi ThankYou Points"),
            new CardDefinition("citi_double_cash",           "Citi Double Cash",                 "Citi", "Mastercard",   0, false, false, CITI_BLUE, CITI_DARK, "Cash Back"),
            new CardDefinition("citi_custom_cash",           "Citi Custom Cash",                 "Citi", "Mastercard",   0, false, false, CITI_BLUE, CITI_DARK, "Citi ThankYou Points"),
            new CardDefinition("citi_rewards_plus",          "Citi Rewards+",                    "Citi", "Mastercard",   0, false, false, CITI_BLUE, CITI_DARK, "Citi ThankYou Points"),
            new CardDefinition("citi_aadvantage_platinum",   "Citi AAdvantage Platinum Select",  "Citi", "Mastercard",  99, false, false, CITI_BLUE, CITI_DARK, "AAdvantage Miles"),

            // Bank of America Personal
            new CardDefinition("bofa_customized_cash",       "BofA Customized Cash Rewards",  "Bank of America", "Visa",   0, false, false, BOFA_RED, BOFA_DARK, "Cash Back"),
            new CardDefinition("bofa_unlimited_cash",        "BofA Unlimited Cash Rewards",   "Bank of America", "Visa",   0, false, false, BOFA_RED, BOFA_DARK, "Cash Back"),
            new CardDefinition("bofa_premium_rewards",       "BofA Premium Rewards",          "Bank of America", "Visa",  95, false, false, BOFA_RED, BOFA_DARK, "Cash Back"),
            new CardDefinition("bofa_premium_rewards_elite", "BofA Premium Rewards Elite",    "Bank of America", "Visa", 550, false, false, BOFA_DARK, BOFA_RED, "Cash Back"),
            new CardDefinition("atmos_ascent",               "Atmos Rewards Ascent",          "Bank of America", "Visa",  95, false, false, ATMOS_BLUE, ATMOS_DARK, "Atmos/Alaska Rewards Miles"),
            new CardDefinition("atmos_summit",               "Atmos Rewards Summit",          "Bank of America", "Visa", 395, false, false, ATMOS_DARK, ATMOS_BLUE, "Atmos/Alaska Rewards Miles"),

            // US Bank Personal
            new CardDefinition("usbank_cash_plus",        "US Bank Cash+ Visa",        "US Bank", "Visa",   0, false, false, USB_RED, USB_DARK, "Cash Back"),
            new CardDefinition("usbank_altitude_go",      "US Bank Altitude Go",       "US Bank", "Visa",   0, false, false, USB_RED, USB_DARK, "Cash Back"),
            new CardDefinition("usbank_altitude_connect", "US Bank Altitude Connect",  "US Bank", "Visa",   0, false, false, USB_RED, USB_DARK, "Cash Back"),
            new CardDefinition("usbank_altitude_reserve", "US Bank Altitude Reserve",  "US Bank", "Visa", 400, false, false, USB_DARK, USB_RED, "Cash Back"),

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
            new CardDefinition("ink_preferred",       "Ink Business Preferred",    "Chase", "Visa",   95, false, true, CHASE_BLUE, CHASE_DARK, "Chase Ultimate Rewards Points"),
            new CardDefinition("ink_cash",            "Ink Business Cash",         "Chase", "Visa",    0, false, true, CHASE_BLUE, CHASE_DARK, "Chase Ultimate Rewards Points"),
            new CardDefinition("ink_unlimited",       "Ink Business Unlimited",    "Chase", "Visa",    0, false, true, CHASE_BLUE, CHASE_DARK, "Chase Ultimate Rewards Points"),
            new CardDefinition("ink_premier",         "Ink Business Premier",      "Chase", "Visa",  195, false, true, CHASE_DARK, CHASE_BLUE, "Cash Back"),
            new CardDefinition("amazon_business_prime","Amazon Business Prime Card","Chase", "Visa",    0, false, true, AMZN_BLACK, AMZN_ORANGE, "Cash Back"),

            // Amex Business
            new CardDefinition("amex_business_gold",           "Amex Business Gold",                     "American Express", "Amex", 375, false, true, AMEX_GOLD_C, AMEX_GOLD_D, "Amex Membership Rewards Points"),
            new CardDefinition("amex_business_platinum",       "Amex Business Platinum",                 "American Express", "Amex", 695, false, true, AMEX_PLAT_C, AMEX_PLAT_D, "Amex Membership Rewards Points"),
            new CardDefinition("amex_blue_business_plus",      "Amex Blue Business Plus",                "American Express", "Amex",   0, false, true, AMEX_BLUE, AMEX_BLUE_D, "Amex Membership Rewards Points"),
            new CardDefinition("amex_blue_business_cash",      "Amex Blue Business Cash",                "American Express", "Amex",   0, false, true, AMEX_BLUE, AMEX_BLUE_D, "Cash Back"),
            new CardDefinition("delta_gold_business_amex",     "Delta SkyMiles Gold Business Amex",      "American Express", "Amex", 150, false, true, DELTA_RED, DELTA_PURP, "Delta SkyMiles"),
            new CardDefinition("delta_platinum_business_amex", "Delta SkyMiles Platinum Business Amex",  "American Express", "Amex", 350, false, true, DELTA_PURP, DELTA_RED, "Delta SkyMiles"),
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
        list.add(new RewardRate("chase_sapphire_preferred", RewardCategory.TRAVEL,    RateType.POINTS, 5.0));
        list.add(new RewardRate("chase_sapphire_preferred", RewardCategory.DINING,    RateType.POINTS, 3.0));
        list.add(new RewardRate("chase_sapphire_preferred", RewardCategory.GROCERIES, RateType.POINTS, 3.0));
        list.add(new RewardRate("chase_sapphire_preferred", RewardCategory.GENERAL,   RateType.POINTS, 1.0));

        // Chase Sapphire Reserve
        list.add(new RewardRate("chase_sapphire_reserve", RewardCategory.TRAVEL,  RateType.POINTS, 10.0));
        list.add(new RewardRate("chase_sapphire_reserve", RewardCategory.DINING,  RateType.POINTS,  3.0));
        list.add(new RewardRate("chase_sapphire_reserve", RewardCategory.GENERAL, RateType.POINTS,  1.0));

        // Chase Freedom Unlimited
        list.add(new RewardRate("chase_freedom_unlimited", RewardCategory.TRAVEL,  RateType.POINTS, 5.0));
        list.add(new RewardRate("chase_freedom_unlimited", RewardCategory.DINING,  RateType.POINTS, 3.0));
        list.add(new RewardRate("chase_freedom_unlimited", RewardCategory.GENERAL, RateType.POINTS, 1.5));

        // Chase Freedom Flex (rotating categories)
        list.add(new RewardRate("chase_freedom_flex", RewardCategory.DINING,          RateType.POINTS, 3.0, true, "freedom_flex_rotating", false));
        list.add(new RewardRate("chase_freedom_flex", RewardCategory.GROCERIES,       RateType.POINTS, 5.0, true, "freedom_flex_rotating", false));
        list.add(new RewardRate("chase_freedom_flex", RewardCategory.GAS,             RateType.POINTS, 5.0, true, "freedom_flex_rotating", false));
        list.add(new RewardRate("chase_freedom_flex", RewardCategory.ENTERTAINMENT,   RateType.POINTS, 5.0, true, "freedom_flex_rotating", false));
        list.add(new RewardRate("chase_freedom_flex", RewardCategory.ONLINE_SHOPPING, RateType.POINTS, 5.0, true, "freedom_flex_rotating", false));
        list.add(new RewardRate("chase_freedom_flex", RewardCategory.TRAVEL,          RateType.POINTS, 5.0));
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
        list.add(new RewardRate("united_explorer", RewardCategory.TRAVEL,  RateType.MILES, 2.0));
        list.add(new RewardRate("united_explorer", RewardCategory.DINING,  RateType.MILES, 2.0));
        list.add(new RewardRate("united_explorer", RewardCategory.GENERAL, RateType.MILES, 1.0));

        // Amex Gold
        list.add(new RewardRate("amex_gold", RewardCategory.DINING,    RateType.POINTS, 4.0));
        list.add(new RewardRate("amex_gold", RewardCategory.GROCERIES, RateType.POINTS, 4.0));
        list.add(new RewardRate("amex_gold", RewardCategory.TRAVEL,    RateType.POINTS, 3.0));
        list.add(new RewardRate("amex_gold", RewardCategory.GENERAL,   RateType.POINTS, 1.0));

        // Amex Platinum
        list.add(new RewardRate("amex_platinum", RewardCategory.TRAVEL,  RateType.POINTS, 5.0));
        list.add(new RewardRate("amex_platinum", RewardCategory.GENERAL, RateType.POINTS, 1.0));

        // Amex Green
        list.add(new RewardRate("amex_green", RewardCategory.TRAVEL,  RateType.POINTS, 3.0));
        list.add(new RewardRate("amex_green", RewardCategory.DINING,  RateType.POINTS, 3.0));
        list.add(new RewardRate("amex_green", RewardCategory.GENERAL, RateType.POINTS, 1.0));

        // Amex Blue Cash Preferred
        list.add(new RewardRate("amex_blue_cash_preferred", RewardCategory.GROCERIES,    RateType.CASHBACK, 6.0));
        list.add(new RewardRate("amex_blue_cash_preferred", RewardCategory.ENTERTAINMENT, RateType.CASHBACK, 6.0));
        list.add(new RewardRate("amex_blue_cash_preferred", RewardCategory.GAS,          RateType.CASHBACK, 3.0));
        list.add(new RewardRate("amex_blue_cash_preferred", RewardCategory.TRAVEL,       RateType.CASHBACK, 3.0));
        list.add(new RewardRate("amex_blue_cash_preferred", RewardCategory.GENERAL,      RateType.CASHBACK, 1.0));

        // Amex Blue Cash Everyday
        list.add(new RewardRate("amex_blue_cash_everyday", RewardCategory.GROCERIES,      RateType.CASHBACK, 3.0));
        list.add(new RewardRate("amex_blue_cash_everyday", RewardCategory.ONLINE_SHOPPING, RateType.CASHBACK, 3.0));
        list.add(new RewardRate("amex_blue_cash_everyday", RewardCategory.GAS,            RateType.CASHBACK, 3.0));
        list.add(new RewardRate("amex_blue_cash_everyday", RewardCategory.GENERAL,        RateType.CASHBACK, 1.0));

        // Amex EveryDay Preferred
        list.add(new RewardRate("amex_everyday_preferred", RewardCategory.GROCERIES, RateType.POINTS, 3.0));
        list.add(new RewardRate("amex_everyday_preferred", RewardCategory.GAS,       RateType.POINTS, 2.0));
        list.add(new RewardRate("amex_everyday_preferred", RewardCategory.GENERAL,   RateType.POINTS, 1.0));

        // Amex EveryDay
        list.add(new RewardRate("amex_everyday", RewardCategory.GROCERIES, RateType.POINTS, 2.0));
        list.add(new RewardRate("amex_everyday", RewardCategory.GENERAL,   RateType.POINTS, 1.0));

        // Delta SkyMiles Gold Amex
        list.add(new RewardRate("delta_gold_amex", RewardCategory.TRAVEL,    RateType.MILES, 2.0));
        list.add(new RewardRate("delta_gold_amex", RewardCategory.DINING,    RateType.MILES, 2.0));
        list.add(new RewardRate("delta_gold_amex", RewardCategory.GROCERIES, RateType.MILES, 2.0));
        list.add(new RewardRate("delta_gold_amex", RewardCategory.GENERAL,   RateType.MILES, 1.0));

        // Delta SkyMiles Platinum Amex
        list.add(new RewardRate("delta_platinum_amex", RewardCategory.TRAVEL,    RateType.MILES, 3.0));
        list.add(new RewardRate("delta_platinum_amex", RewardCategory.DINING,    RateType.MILES, 2.0));
        list.add(new RewardRate("delta_platinum_amex", RewardCategory.GROCERIES, RateType.MILES, 2.0));
        list.add(new RewardRate("delta_platinum_amex", RewardCategory.GENERAL,   RateType.MILES, 1.0));

        // Hilton Honors Amex Surpass
        list.add(new RewardRate("hilton_surpass_amex", RewardCategory.TRAVEL,    RateType.POINTS, 12.0));
        list.add(new RewardRate("hilton_surpass_amex", RewardCategory.GROCERIES, RateType.POINTS,  6.0));
        list.add(new RewardRate("hilton_surpass_amex", RewardCategory.GAS,       RateType.POINTS,  6.0));
        list.add(new RewardRate("hilton_surpass_amex", RewardCategory.DINING,    RateType.POINTS,  6.0));
        list.add(new RewardRate("hilton_surpass_amex", RewardCategory.GENERAL,   RateType.POINTS,  3.0));

        // Hilton Honors Amex Aspire
        list.add(new RewardRate("hilton_aspire_amex", RewardCategory.TRAVEL,  RateType.POINTS, 14.0));
        list.add(new RewardRate("hilton_aspire_amex", RewardCategory.DINING,  RateType.POINTS,  7.0));
        list.add(new RewardRate("hilton_aspire_amex", RewardCategory.GENERAL, RateType.POINTS,  3.0));

        // Capital One Venture X
        list.add(new RewardRate("capital_one_venture_x", RewardCategory.TRAVEL,  RateType.MILES, 10.0));
        list.add(new RewardRate("capital_one_venture_x", RewardCategory.GENERAL, RateType.MILES,  2.0));

        // Capital One Venture
        list.add(new RewardRate("capital_one_venture", RewardCategory.TRAVEL,  RateType.MILES, 5.0));
        list.add(new RewardRate("capital_one_venture", RewardCategory.GENERAL, RateType.MILES, 2.0));

        // Capital One Savor
        list.add(new RewardRate("capital_one_savor", RewardCategory.DINING,         RateType.CASHBACK, 3.0));
        list.add(new RewardRate("capital_one_savor", RewardCategory.ENTERTAINMENT,  RateType.CASHBACK, 3.0));
        list.add(new RewardRate("capital_one_savor", RewardCategory.GROCERIES,      RateType.CASHBACK, 3.0));
        list.add(new RewardRate("capital_one_savor", RewardCategory.GENERAL,        RateType.CASHBACK, 1.0));

        // Capital One Quicksilver
        list.add(new RewardRate("capital_one_quicksilver", RewardCategory.GENERAL, RateType.CASHBACK, 1.5));

        // Citi Strata Premier
        list.add(new RewardRate("citi_strata_premier", RewardCategory.TRAVEL,    RateType.POINTS, 3.0));
        list.add(new RewardRate("citi_strata_premier", RewardCategory.DINING,    RateType.POINTS, 3.0));
        list.add(new RewardRate("citi_strata_premier", RewardCategory.GROCERIES, RateType.POINTS, 3.0));
        list.add(new RewardRate("citi_strata_premier", RewardCategory.GAS,       RateType.POINTS, 3.0));
        list.add(new RewardRate("citi_strata_premier", RewardCategory.GENERAL,   RateType.POINTS, 1.0));

        // Citi Double Cash
        list.add(new RewardRate("citi_double_cash", RewardCategory.GENERAL, RateType.CASHBACK, 2.0));

        // Citi Custom Cash (choice)
        list.add(new RewardRate("citi_custom_cash", RewardCategory.DINING,          RateType.POINTS, 5.0, true, "citi_custom_cash_choice", false));
        list.add(new RewardRate("citi_custom_cash", RewardCategory.GROCERIES,       RateType.POINTS, 5.0, true, "citi_custom_cash_choice", false));
        list.add(new RewardRate("citi_custom_cash", RewardCategory.GAS,             RateType.POINTS, 5.0, true, "citi_custom_cash_choice", false));
        list.add(new RewardRate("citi_custom_cash", RewardCategory.TRAVEL,          RateType.POINTS, 5.0, true, "citi_custom_cash_choice", false));
        list.add(new RewardRate("citi_custom_cash", RewardCategory.ENTERTAINMENT,   RateType.POINTS, 5.0, true, "citi_custom_cash_choice", false));
        list.add(new RewardRate("citi_custom_cash", RewardCategory.ONLINE_SHOPPING, RateType.POINTS, 5.0, true, "citi_custom_cash_choice", false));
        list.add(new RewardRate("citi_custom_cash", RewardCategory.GENERAL,         RateType.POINTS, 1.0));

        // Citi Rewards+
        list.add(new RewardRate("citi_rewards_plus", RewardCategory.GROCERIES, RateType.POINTS, 2.0));
        list.add(new RewardRate("citi_rewards_plus", RewardCategory.GAS,       RateType.POINTS, 2.0));
        list.add(new RewardRate("citi_rewards_plus", RewardCategory.GENERAL,   RateType.POINTS, 1.0));

        // Citi AAdvantage Platinum Select
        list.add(new RewardRate("citi_aadvantage_platinum", RewardCategory.TRAVEL,  RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_platinum", RewardCategory.DINING,  RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_platinum", RewardCategory.GAS,     RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_platinum", RewardCategory.GENERAL, RateType.MILES, 1.0));

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

        // Atmos Ascent
        list.add(new RewardRate("atmos_ascent", RewardCategory.TRAVEL,  RateType.MILES, 3.0));
        list.add(new RewardRate("atmos_ascent", RewardCategory.GAS,     RateType.MILES, 2.0));
        list.add(new RewardRate("atmos_ascent", RewardCategory.GENERAL, RateType.MILES, 1.0));

        // Atmos Summit
        list.add(new RewardRate("atmos_summit", RewardCategory.TRAVEL,  RateType.MILES, 3.0));
        list.add(new RewardRate("atmos_summit", RewardCategory.DINING,  RateType.MILES, 3.0));
        list.add(new RewardRate("atmos_summit", RewardCategory.GENERAL, RateType.MILES, 1.0));

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
        list.add(new RewardRate("usbank_altitude_connect", RewardCategory.TRAVEL,    RateType.CASHBACK, 4.0));
        list.add(new RewardRate("usbank_altitude_connect", RewardCategory.GAS,       RateType.CASHBACK, 4.0));
        list.add(new RewardRate("usbank_altitude_connect", RewardCategory.DINING,    RateType.CASHBACK, 2.0));
        list.add(new RewardRate("usbank_altitude_connect", RewardCategory.GROCERIES, RateType.CASHBACK, 2.0));
        list.add(new RewardRate("usbank_altitude_connect", RewardCategory.GENERAL,   RateType.CASHBACK, 1.0));

        // US Bank Altitude Reserve
        list.add(new RewardRate("usbank_altitude_reserve", RewardCategory.TRAVEL,  RateType.CASHBACK, 5.0));
        list.add(new RewardRate("usbank_altitude_reserve", RewardCategory.DINING,  RateType.CASHBACK, 3.0));
        list.add(new RewardRate("usbank_altitude_reserve", RewardCategory.GENERAL, RateType.CASHBACK, 1.0));

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

        // Ink Business Premier
        list.add(new RewardRate("ink_premier", RewardCategory.TRAVEL,  RateType.CASHBACK, 5.0));
        list.add(new RewardRate("ink_premier", RewardCategory.GENERAL, RateType.CASHBACK, 2.0));

        // Amazon Business Prime
        list.add(new RewardRate("amazon_business_prime", RewardCategory.ONLINE_SHOPPING, RateType.CASHBACK, 5.0));
        list.add(new RewardRate("amazon_business_prime", RewardCategory.DINING,          RateType.CASHBACK, 2.0));
        list.add(new RewardRate("amazon_business_prime", RewardCategory.GAS,             RateType.CASHBACK, 2.0));
        list.add(new RewardRate("amazon_business_prime", RewardCategory.GENERAL,         RateType.CASHBACK, 1.0));

        // Amex Business Gold (auto top-2 categories — modelled as choice)
        list.add(new RewardRate("amex_business_gold", RewardCategory.DINING,          RateType.POINTS, 4.0, true, "amex_biz_gold_top2", false));
        list.add(new RewardRate("amex_business_gold", RewardCategory.TRAVEL,          RateType.POINTS, 4.0, true, "amex_biz_gold_top2", false));
        list.add(new RewardRate("amex_business_gold", RewardCategory.ONLINE_SHOPPING, RateType.POINTS, 4.0, true, "amex_biz_gold_top2", false));
        list.add(new RewardRate("amex_business_gold", RewardCategory.GAS,             RateType.POINTS, 4.0, true, "amex_biz_gold_top2", false));
        list.add(new RewardRate("amex_business_gold", RewardCategory.GENERAL,         RateType.POINTS, 1.0));

        // Amex Business Platinum
        list.add(new RewardRate("amex_business_platinum", RewardCategory.TRAVEL,  RateType.POINTS, 5.0));
        list.add(new RewardRate("amex_business_platinum", RewardCategory.GENERAL, RateType.POINTS, 1.0));

        // Amex Blue Business Plus
        list.add(new RewardRate("amex_blue_business_plus", RewardCategory.GENERAL, RateType.POINTS, 2.0));

        // Amex Blue Business Cash
        list.add(new RewardRate("amex_blue_business_cash", RewardCategory.GENERAL, RateType.CASHBACK, 2.0));

        // Delta Gold Business
        list.add(new RewardRate("delta_gold_business_amex", RewardCategory.TRAVEL,    RateType.MILES, 2.0));
        list.add(new RewardRate("delta_gold_business_amex", RewardCategory.DINING,    RateType.MILES, 2.0));
        list.add(new RewardRate("delta_gold_business_amex", RewardCategory.GROCERIES, RateType.MILES, 2.0));
        list.add(new RewardRate("delta_gold_business_amex", RewardCategory.GENERAL,   RateType.MILES, 1.0));

        // Delta Platinum Business
        list.add(new RewardRate("delta_platinum_business_amex", RewardCategory.TRAVEL,  RateType.MILES, 3.0));
        list.add(new RewardRate("delta_platinum_business_amex", RewardCategory.DINING,  RateType.MILES, 1.5));
        list.add(new RewardRate("delta_platinum_business_amex", RewardCategory.GENERAL, RateType.MILES, 1.0));

        // Hilton Business Amex
        list.add(new RewardRate("hilton_business_amex", RewardCategory.TRAVEL,    RateType.POINTS, 12.0));
        list.add(new RewardRate("hilton_business_amex", RewardCategory.GROCERIES, RateType.POINTS,  5.0));
        list.add(new RewardRate("hilton_business_amex", RewardCategory.GAS,       RateType.POINTS,  5.0));
        list.add(new RewardRate("hilton_business_amex", RewardCategory.DINING,    RateType.POINTS,  5.0));
        list.add(new RewardRate("hilton_business_amex", RewardCategory.GENERAL,   RateType.POINTS,  3.0));

        // C1 Spark Miles
        list.add(new RewardRate("c1_spark_miles", RewardCategory.TRAVEL,  RateType.MILES, 5.0));
        list.add(new RewardRate("c1_spark_miles", RewardCategory.GENERAL, RateType.MILES, 2.0));

        // C1 Spark Cash Plus
        list.add(new RewardRate("c1_spark_cash_plus", RewardCategory.GENERAL, RateType.CASHBACK, 2.0));

        // C1 Spark Cash Select
        list.add(new RewardRate("c1_spark_cash_select", RewardCategory.GENERAL, RateType.CASHBACK, 1.5));

        // CitiBusiness AAdvantage
        list.add(new RewardRate("citi_aadvantage_business", RewardCategory.TRAVEL,  RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_business", RewardCategory.GAS,     RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_business", RewardCategory.DINING,  RateType.MILES, 2.0));
        list.add(new RewardRate("citi_aadvantage_business", RewardCategory.GENERAL, RateType.MILES, 1.0));

        // US Bank Business Cash+
        list.add(new RewardRate("usbank_business_cash_plus", RewardCategory.DINING,          RateType.CASHBACK, 5.0, true, "usbank_biz_cash_plus_choice", false));
        list.add(new RewardRate("usbank_business_cash_plus", RewardCategory.TRAVEL,          RateType.CASHBACK, 5.0, true, "usbank_biz_cash_plus_choice", false));
        list.add(new RewardRate("usbank_business_cash_plus", RewardCategory.ONLINE_SHOPPING, RateType.CASHBACK, 5.0, true, "usbank_biz_cash_plus_choice", false));
        list.add(new RewardRate("usbank_business_cash_plus", RewardCategory.GAS,             RateType.CASHBACK, 2.0));
        list.add(new RewardRate("usbank_business_cash_plus", RewardCategory.GENERAL,         RateType.CASHBACK, 1.0));

        // US Bank Business Altitude Power
        list.add(new RewardRate("usbank_business_altitude_power", RewardCategory.GENERAL, RateType.CASHBACK, 2.0));

        return list;
    }

    // ── Card Benefits ─────────────────────────────────────────────────────────

    public static List<CardBenefit> getCardBenefits() {
        return Arrays.asList(
            new CardBenefit("chase_sapphire_preferred", "Hotel Credit", "$50 annual hotel credit via Chase Travel", 5000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("chase_sapphire_reserve",   "Travel Credit", "$300 annual travel credit", 30000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("chase_sapphire_reserve",   "Global Entry/TSA PreCheck", "$100 credit for Global Entry or $85 for TSA PreCheck", 10000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("amex_gold",                "Dining Credit", "$10/month at Grubhub, The Cheesecake Factory, Goldbelly, Wine.com", 1000, ResetPeriod.MONTHLY, false),
            new CardBenefit("amex_gold",                "Resy Credit", "$50 semi-annual at Resy restaurants", 5000, ResetPeriod.SEMI_ANNUALLY, false),
            new CardBenefit("amex_gold",                "Dunkin' Credit", "$7/month at Dunkin'", 700, ResetPeriod.MONTHLY, false),
            new CardBenefit("amex_platinum",            "Airline Fee Credit", "$200 annual airline fee credit", 20000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("amex_platinum",            "Hotel Credit", "$200 hotel credit via Amex Travel", 20000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("amex_platinum",            "Digital Entertainment Credit", "$20/month digital entertainment credit", 2000, ResetPeriod.MONTHLY, false),
            new CardBenefit("amex_platinum",            "Walmart+ Credit", "$12.95/month Walmart+ membership credit", 1295, ResetPeriod.MONTHLY, false),
            new CardBenefit("amex_platinum",            "Equinox Credit", "$300 annual Equinox credit", 30000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("amex_platinum",            "CLEAR Plus Credit", "$189 annual CLEAR Plus credit", 18900, ResetPeriod.ANNUALLY, false),
            new CardBenefit("amex_platinum",            "Global Entry/TSA PreCheck", "$100 for Global Entry or $85 for TSA PreCheck", 10000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("amex_green",               "CLEAR Plus Credit", "$189 annual CLEAR Plus credit", 18900, ResetPeriod.ANNUALLY, false),
            new CardBenefit("amex_green",               "LoungeBuddy Credit", "$100 annual LoungeBuddy credit", 10000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("capital_one_venture_x",    "Travel Credit", "$300 annual credit via Capital One Travel", 30000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("capital_one_venture_x",    "Anniversary Bonus", "10,000 bonus miles on account anniversary", 0, ResetPeriod.ANNUALLY, false),
            new CardBenefit("citi_strata_premier",      "Hotel Discount", "$100 off a single hotel stay of $500+ per year via Citi Travel", 10000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("bofa_premium_rewards_elite","Travel Credit", "$300 annual airline/travel credit", 30000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("bofa_premium_rewards_elite","Lifestyle Credit", "$150 annual lifestyle credit", 15000, ResetPeriod.ANNUALLY, false),
            new CardBenefit("usbank_altitude_reserve",  "Travel Credit", "$325 annual travel/dining credit", 32500, ResetPeriod.ANNUALLY, false),
            new CardBenefit("bilt_palladium",           "Annual Bilt Cash", "$200 annual Bilt Cash credit", 20000, ResetPeriod.ANNUALLY, false)
        );
    }
}

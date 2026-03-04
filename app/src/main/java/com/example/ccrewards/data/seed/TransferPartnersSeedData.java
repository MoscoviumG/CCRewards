package com.example.ccrewards.data.seed;

import com.example.ccrewards.data.model.TransferPartner;
import com.example.ccrewards.data.model.TransferPartnerType;

import java.util.ArrayList;
import java.util.List;

/** Pre-seeded transfer partner data for all major transferable currencies. */
public class TransferPartnersSeedData {

    private static TransferPartner airline(String currency, String name, int from, double to) {
        return new TransferPartner(currency, name, TransferPartnerType.AIRLINE, from, to, true, null);
    }
    private static TransferPartner airline(String currency, String name, int from, double to, String notes) {
        return new TransferPartner(currency, name, TransferPartnerType.AIRLINE, from, to, true, notes);
    }
    private static TransferPartner hotel(String currency, String name, int from, double to) {
        return new TransferPartner(currency, name, TransferPartnerType.HOTEL, from, to, true, null);
    }

    public static List<TransferPartner> getPartners() {
        List<TransferPartner> list = new ArrayList<>();

        // ── Chase Ultimate Rewards ─────────────────────────────────────────────
        String chase = "Chase Ultimate Rewards Points";
        list.add(airline(chase, "Aer Lingus AerClub", 1, 1.0));
        list.add(airline(chase, "Air Canada Aeroplan", 1, 1.0));
        list.add(airline(chase, "Air France/KLM Flying Blue", 1, 1.0));
        list.add(airline(chase, "British Airways Avios", 1, 1.0));
        list.add(airline(chase, "Emirates Skywards", 1, 1.0));
        list.add(airline(chase, "Iberia Avios", 1, 1.0));
        list.add(airline(chase, "JetBlue TrueBlue", 1, 1.0));
        list.add(airline(chase, "Singapore KrisFlyer", 1, 1.0));
        list.add(airline(chase, "Southwest Rapid Rewards", 1, 1.0));
        list.add(airline(chase, "United MileagePlus", 1, 1.0));
        list.add(airline(chase, "Virgin Atlantic Flying Club", 1, 1.0));
        list.add(hotel(chase, "IHG One Rewards", 1, 1.0));
        list.add(hotel(chase, "Marriott Bonvoy", 1, 1.0));
        list.add(hotel(chase, "World of Hyatt", 1, 1.0));

        // ── Amex Membership Rewards ───────────────────────────────────────────
        String amex = "Amex Membership Rewards Points";
        list.add(airline(amex, "ANA Mileage Club", 1, 1.0, "US excise tax offset fee applies"));
        list.add(airline(amex, "Air Canada Aeroplan", 1, 1.0));
        list.add(airline(amex, "Air France/KLM Flying Blue", 1, 1.0));
        list.add(airline(amex, "Avianca LifeMiles", 1, 1.0));
        list.add(airline(amex, "British Airways Avios", 1, 1.0));
        list.add(airline(amex, "Cathay Pacific Asia Miles", 1, 0.8, "Ratio changing to 1:0.8 from March 2026"));
        list.add(airline(amex, "Delta SkyMiles", 1, 1.0, "US excise tax offset fee applies"));
        list.add(airline(amex, "Emirates Skywards", 1, 1.0));
        list.add(airline(amex, "Etihad Guest", 1, 1.0));
        list.add(airline(amex, "Hawaiian HawaiianMiles", 1, 1.0, "US excise tax offset fee applies"));
        list.add(airline(amex, "Iberia Avios", 1, 1.0));
        list.add(airline(amex, "JetBlue TrueBlue", 1, 0.8, "US excise tax offset fee applies"));
        list.add(airline(amex, "Qantas Frequent Flyer", 1, 1.0));
        list.add(airline(amex, "Singapore KrisFlyer", 1, 1.0));
        list.add(airline(amex, "Virgin Atlantic Flying Club", 1, 1.0));
        list.add(hotel(amex, "Hilton Honors", 1, 2.0));
        list.add(hotel(amex, "Marriott Bonvoy", 1, 1.2));

        // ── Capital One Miles ─────────────────────────────────────────────────
        String c1 = "Capital One Miles";
        list.add(airline(c1, "Aeromexico Club Premier", 1, 1.0));
        list.add(airline(c1, "Air Canada Aeroplan", 1, 1.0));
        list.add(airline(c1, "Air France/KLM Flying Blue", 1, 1.0));
        list.add(airline(c1, "Avianca LifeMiles", 1, 1.0));
        list.add(airline(c1, "British Airways Avios", 1, 1.0));
        list.add(airline(c1, "Emirates Skywards", 2, 1.5));
        list.add(airline(c1, "EVA Air Infinity MileageLands", 2, 1.5));
        list.add(airline(c1, "Japan Airlines Mileage Bank", 2, 1.5));
        list.add(airline(c1, "JetBlue TrueBlue", 5, 3.0));
        list.add(airline(c1, "Singapore KrisFlyer", 1, 1.0));
        list.add(airline(c1, "Turkish Miles&Smiles", 1, 1.0));
        list.add(airline(c1, "Virgin Atlantic Flying Club", 1, 1.0));
        list.add(hotel(c1, "Accor Live Limitless", 2, 1.0));
        list.add(hotel(c1, "Choice Privileges", 1, 1.0));
        list.add(hotel(c1, "I Prefer Hotel Rewards", 1, 2.0));
        list.add(hotel(c1, "Wyndham Rewards", 1, 1.0));

        // ── Citi ThankYou Points ──────────────────────────────────────────────
        String citi = "Citi ThankYou Points";
        list.add(airline(citi, "Aeromexico Club Premier", 1, 1.0));
        list.add(airline(citi, "Avianca LifeMiles", 1, 1.0));
        list.add(airline(citi, "Cathay Pacific Asia Miles", 1, 1.0));
        list.add(airline(citi, "Emirates Skywards", 1, 1.0));
        list.add(airline(citi, "Etihad Guest", 1, 1.0));
        list.add(airline(citi, "EVA Air Infinity MileageLands", 1, 1.0));
        list.add(airline(citi, "Air France/KLM Flying Blue", 1, 1.0));
        list.add(airline(citi, "JetBlue TrueBlue", 1, 1.0));
        list.add(airline(citi, "Qantas Frequent Flyer", 1, 1.0));
        list.add(airline(citi, "Qatar Privilege Club", 1, 1.0));
        list.add(airline(citi, "Singapore KrisFlyer", 1, 1.0));
        list.add(airline(citi, "TAP Air Portugal Miles&Go", 1, 1.0));
        list.add(airline(citi, "Thai Royal Orchid Plus", 1, 1.0));
        list.add(airline(citi, "Turkish Miles&Smiles", 1, 1.0));
        list.add(airline(citi, "Virgin Atlantic Flying Club", 1, 1.0));
        list.add(hotel(citi, "Accor Live Limitless", 1, 2.0));
        list.add(hotel(citi, "Choice Privileges", 1, 2.0));
        list.add(hotel(citi, "Leading Hotels of the World", 1, 2.0));
        list.add(hotel(citi, "Preferred Hotels & Resorts", 1, 2.0));
        list.add(hotel(citi, "Wyndham Rewards", 1, 1.0));

        // ── Bilt Points ───────────────────────────────────────────────────────
        String bilt = "Bilt Points";
        list.add(airline(bilt, "Aer Lingus AerClub", 1, 1.0));
        list.add(airline(bilt, "Air Canada Aeroplan", 1, 1.0));
        list.add(airline(bilt, "Air France/KLM Flying Blue", 1, 1.0));
        list.add(airline(bilt, "Alaska/Atmos Mileage Plan", 1, 1.0));
        list.add(airline(bilt, "American Airlines AAdvantage", 1, 1.0));
        list.add(airline(bilt, "British Airways Avios", 1, 1.0));
        list.add(airline(bilt, "Cathay Pacific Asia Miles", 1, 1.0));
        list.add(airline(bilt, "Emirates Skywards", 1, 1.0));
        list.add(airline(bilt, "Etihad Guest", 1, 1.0));
        list.add(airline(bilt, "Japan Airlines Mileage Bank", 1, 1.0));
        list.add(airline(bilt, "Qatar Privilege Club", 1, 1.0));
        list.add(airline(bilt, "Southwest Rapid Rewards", 1, 1.0));
        list.add(airline(bilt, "Spirit Free Spirit", 1, 1.0));
        list.add(airline(bilt, "TAP Air Portugal Miles&Go", 1, 1.0));
        list.add(airline(bilt, "Turkish Miles&Smiles", 1, 1.0));
        list.add(airline(bilt, "United MileagePlus", 1, 1.0));
        list.add(airline(bilt, "Virgin Atlantic Flying Club", 1, 1.0));
        list.add(hotel(bilt, "Hilton Honors", 1, 1.0));
        list.add(hotel(bilt, "IHG One Rewards", 1, 1.0));
        list.add(hotel(bilt, "Marriott Bonvoy", 2, 1.0));
        list.add(hotel(bilt, "World of Hyatt", 1, 1.0));

        // ── HSBC Rewards Points ───────────────────────────────────────────────
        String hsbc = "HSBC Rewards Points";
        list.add(airline(hsbc, "British Airways Avios", 1, 1.0));
        list.add(airline(hsbc, "Aer Lingus AerClub",   1, 1.0));
        list.add(airline(hsbc, "Iberia Avios",          1, 1.0));
        list.add(airline(hsbc, "Emirates Skywards",     1, 1.0));
        list.add(airline(hsbc, "Singapore KrisFlyer",   1, 1.0));

        return list;
    }
}

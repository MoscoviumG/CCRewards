package com.example.ccrewards.data.db;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.ccrewards.data.db.converters.Converters;
import com.example.ccrewards.data.db.dao.*;
import com.example.ccrewards.data.model.*;
import com.example.ccrewards.data.seed.DefaultPointValuations;
import com.example.ccrewards.data.seed.SeedData;
import com.example.ccrewards.data.seed.TransferPartnersSeedData;

import androidx.room.migration.Migration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

@Database(
    entities = {
        CardDefinition.class,
        RewardRate.class,
        UserCard.class,
        UserCardChoiceCategory.class,
        CardBenefit.class,
        BenefitUsage.class,
        ProductChangeRecord.class,
        PointValuation.class,
        TransferPartner.class,
        CustomCategory.class,
        CustomCategoryRate.class,
        WelcomeBonus.class,
        RotationalBonus.class,
        RotationalBonusCategory.class,
    },
    version = 11,
    exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CardDefinitionDao cardDefinitionDao();
    public abstract RewardRateDao rewardRateDao();
    public abstract UserCardDao userCardDao();
    public abstract UserCardChoiceCategoryDao userCardChoiceCategoryDao();
    public abstract CardBenefitDao cardBenefitDao();
    public abstract BenefitUsageDao benefitUsageDao();
    public abstract ProductChangeRecordDao productChangeRecordDao();
    public abstract PointValuationDao pointValuationDao();
    public abstract TransferPartnerDao transferPartnerDao();
    public abstract CustomCategoryDao customCategoryDao();
    public abstract CustomCategoryRateDao customCategoryRateDao();
    public abstract WelcomeBonusDao welcomeBonusDao();
    public abstract RotationalBonusDao rotationalBonusDao();
    public abstract RotationalBonusCategoryDao rotationalBonusCategoryDao();

    // ── Schema migration ───────────────────────────────────────────────────────

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `custom_categories` " +
                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT NOT NULL, `sortOrder` INTEGER NOT NULL DEFAULT 0)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `custom_category_rates` " +
                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`customCategoryId` INTEGER NOT NULL, `cardDefinitionId` TEXT NOT NULL, " +
                    "`rate` REAL NOT NULL, `rateType` TEXT NOT NULL)");
            database.execSQL("ALTER TABLE `card_benefits` " +
                    "ADD COLUMN `resetType` TEXT NOT NULL DEFAULT 'CALENDAR'");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `welcome_bonuses` (" +
                    "`userCardId` INTEGER PRIMARY KEY NOT NULL, " +
                    "`bonusPoints` INTEGER NOT NULL, " +
                    "`bonusCurrencyName` TEXT NOT NULL, " +
                    "`spendRequirementCents` INTEGER NOT NULL, " +
                    "`deadline` INTEGER, " +
                    "`showInBestCard` INTEGER NOT NULL DEFAULT 1, " +
                    "`achieved` INTEGER NOT NULL DEFAULT 0)");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `custom_category_rates` " +
                    "ADD COLUMN `currencyName` TEXT NOT NULL DEFAULT ''");
        }
    };

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `reward_rates` ADD COLUMN `currencyName` TEXT");
        }
    };

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `benefit_usage` ADD COLUMN `usedCents` INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `rotational_bonuses` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`userCardId` INTEGER NOT NULL, " +
                    "`label` TEXT, " +
                    "`spendLimitCents` INTEGER NOT NULL DEFAULT 150000, " +
                    "`usedCents` INTEGER NOT NULL DEFAULT 0, " +
                    "`endDate` INTEGER, " +
                    "`isFullyUsed` INTEGER NOT NULL DEFAULT 0)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `rotational_bonus_categories` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`rotationalBonusId` INTEGER NOT NULL, " +
                    "`categoryName` TEXT, " +
                    "`rate` REAL NOT NULL DEFAULT 0, " +
                    "`rateType` TEXT, " +
                    "`currencyName` TEXT)");
        }
    };

    public static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `user_cards` ADD COLUMN `lastFour` TEXT");
        }
    };

    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `welcome_bonuses` ADD COLUMN `spendUsedCents` INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `user_cards` ADD COLUMN `customColorPrimary` INTEGER");
        }
    };

    public static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `card_benefits` ADD COLUMN `customResetMonth` INTEGER");
            database.execSQL("ALTER TABLE `card_benefits` ADD COLUMN `customResetDay` INTEGER");
        }
    };

    // ── Versioned seed management ──────────────────────────────────────────────

    /**
     * Increment this whenever seed data changes (card catalog, rates, benefits, valuations).
     * Existing installs will automatically refresh their catalog on next launch while
     * preserving all user data (owned cards, usage history, custom rates, ¢/pt edits).
     */
    public static final int SEED_VERSION = 18;

    private static final String PREF_NAME = "ccrewards_seed_prefs";
    private static final String KEY_SEED_VERSION = "seed_version";

    /**
     * Returns a callback that seeds fresh installs and refreshes stale catalog data on upgrades.
     *
     * <p>User data preserved on refresh:
     * <ul>
     *   <li>user_cards (portfolio, nicknames, limits, open dates)
     *   <li>benefit_usage (mark-used state and history)
     *   <li>product_change_records (account history)
     *   <li>reward_rates where isCustomized=true (user-overridden rates)
     *   <li>point_valuations.centsPerPoint (user-edited ¢/pt values)
     * </ul>
     */
    public static RoomDatabase.Callback buildOpenCallback(Context context, AppDatabase[] dbHolder) {
        return new RoomDatabase.Callback() {
            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
                SharedPreferences prefs =
                        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                int appliedVersion = prefs.getInt(KEY_SEED_VERSION, 0);

                Executors.newSingleThreadExecutor().execute(() -> {
                    AppDatabase database = dbHolder[0];
                    boolean isFreshInstall = database.cardDefinitionDao().count() == 0;

                    if (isFreshInstall) {
                        // First launch: insert all seed data.
                        database.cardDefinitionDao().insertAll(SeedData.getCardDefinitions());
                        database.rewardRateDao().insertAll(SeedData.getRewardRates());
                        database.cardBenefitDao().insertAll(SeedData.getCardBenefits());
                        database.pointValuationDao().insertAll(DefaultPointValuations.getValuations());
                        database.transferPartnerDao().insertAll(TransferPartnersSeedData.getPartners());
                        prefs.edit().putInt(KEY_SEED_VERSION, SEED_VERSION).apply();
                    } else if (appliedVersion < SEED_VERSION) {
                        // Existing install with stale seed: refresh catalog, preserve user data.
                        refreshSeedData(database, prefs);
                    }
                });
            }
        };
    }

    private static void refreshSeedData(AppDatabase database, SharedPreferences prefs) {
        // 1. Upsert all card definitions (INSERT OR REPLACE updates fees/names, inserts new cards).
        database.cardDefinitionDao().insertAll(SeedData.getCardDefinitions());

        // 2. Refresh reward rates per seeded card.
        //    Only non-customized rows are deleted so user edits (isCustomized=true) survive.
        //    Choice-category elections are cleared because their rate IDs have changed.
        List<String> seededCardIds = SeedData.getSeededCardIds();
        List<RewardRate> allSeededRates = SeedData.getRewardRates();
        for (String cardId : seededCardIds) {
            database.rewardRateDao().deleteSeededRatesForCard(cardId);
            List<RewardRate> cardRates = new ArrayList<>();
            for (RewardRate rate : allSeededRates) {
                if (cardId.equals(rate.cardDefinitionId)) cardRates.add(rate);
            }
            if (!cardRates.isEmpty()) {
                database.rewardRateDao().insertAll(cardRates);
            }
        }
        // Stale rate IDs in choice elections — user will re-select after update.
        database.userCardChoiceCategoryDao().deleteAll();

        // 3. Refresh card benefits per seeded card — preserving row IDs.
        //    benefit_usage rows reference benefit IDs; if we delete+reinsert benefits
        //    they get new autoincrement IDs and all "used" marks become orphaned.
        //    Instead: match existing seeded benefits by name, update them in-place
        //    (keeping their ID), insert genuinely new ones, and delete removed ones.
        List<CardBenefit> allSeededBenefits = SeedData.getCardBenefits();
        for (String cardId : seededCardIds) {
            // Map existing seeded benefits: name → row (with its stable ID)
            List<CardBenefit> existing =
                    database.cardBenefitDao().getSeededBenefitsForCardSync(cardId);
            Map<String, Long> existingIdByName = new HashMap<>();
            for (CardBenefit b : existing) {
                existingIdByName.put(b.name, b.id);
            }

            // Collect new seed benefits for this card
            List<CardBenefit> newSeed = new ArrayList<>();
            Set<String> newSeedNames = new HashSet<>();
            for (CardBenefit benefit : allSeededBenefits) {
                if (cardId.equals(benefit.cardDefinitionId)) {
                    newSeed.add(benefit);
                    newSeedNames.add(benefit.name);
                }
            }

            // Delete stale benefits that were removed from the seed
            for (CardBenefit b : existing) {
                if (!newSeedNames.contains(b.name)) {
                    database.cardBenefitDao().delete(b);
                }
            }

            // Upsert: reuse existing ID when name matches (preserves benefit_usage),
            // or let Room auto-generate a new ID for genuinely new benefits.
            for (CardBenefit benefit : newSeed) {
                Long existingId = existingIdByName.get(benefit.name);
                if (existingId != null) {
                    benefit.id = existingId; // keep same ID → usage records remain valid
                }
                database.cardBenefitDao().insert(benefit); // INSERT OR REPLACE
            }
        }

        // 4. Point valuations:
        //    - INSERT IGNORE: inserts new currencies without touching existing rows,
        //      so the user's centsPerPoint edits are never overwritten.
        //    - Update defaultCentsPerPoint for all seeded currencies so the
        //      "Reset to Default" button restores the new baseline.
        List<PointValuation> valuations = DefaultPointValuations.getValuations();
        database.pointValuationDao().insertAll(valuations);
        for (PointValuation v : valuations) {
            database.pointValuationDao().updateDefaultCentsPerPoint(
                    v.rewardCurrencyName, v.defaultCentsPerPoint);
        }

        // 5. Transfer partners: fully replace (no user data stored here).
        database.transferPartnerDao().deleteAll();
        database.transferPartnerDao().insertAll(TransferPartnersSeedData.getPartners());

        prefs.edit().putInt(KEY_SEED_VERSION, SEED_VERSION).apply();
    }
}

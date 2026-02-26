package com.example.ccrewards.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.ccrewards.data.db.converters.Converters;
import com.example.ccrewards.data.db.dao.*;
import com.example.ccrewards.data.model.*;
import com.example.ccrewards.data.seed.DefaultPointValuations;
import com.example.ccrewards.data.seed.SeedData;
import com.example.ccrewards.data.seed.TransferPartnersSeedData;

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
    },
    version = 1,
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

    // ── Seed callback ──────────────────────────────────────────────────────────

    public static RoomDatabase.Callback buildSeedCallback(AppDatabase[] dbHolder) {
        return new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                Executors.newSingleThreadExecutor().execute(() -> {
                    AppDatabase database = dbHolder[0];
                    if (database.cardDefinitionDao().count() == 0) {
                        database.cardDefinitionDao().insertAll(SeedData.getCardDefinitions());
                        database.rewardRateDao().insertAll(SeedData.getRewardRates());
                        database.cardBenefitDao().insertAll(SeedData.getCardBenefits());
                    }
                    if (database.pointValuationDao().count() == 0) {
                        database.pointValuationDao().insertAll(DefaultPointValuations.getValuations());
                    }
                    if (database.transferPartnerDao().count() == 0) {
                        database.transferPartnerDao().insertAll(TransferPartnersSeedData.getPartners());
                    }
                });
            }
        };
    }
}

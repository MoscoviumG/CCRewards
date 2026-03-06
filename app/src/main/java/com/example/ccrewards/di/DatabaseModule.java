package com.example.ccrewards.di;

import android.content.Context;

import androidx.room.Room;

import com.example.ccrewards.data.db.AppDatabase;
import com.example.ccrewards.data.db.dao.*;
import com.example.ccrewards.data.repository.WelcomeBonusRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public static AppDatabase provideDatabase(@ApplicationContext Context context) {
        // Use a holder array so we can reference the DB instance in the callback.
        AppDatabase[] holder = new AppDatabase[1];
        holder[0] = Room.databaseBuilder(context, AppDatabase.class, "ccrewards.db")
                .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3, AppDatabase.MIGRATION_3_4, AppDatabase.MIGRATION_4_5, AppDatabase.MIGRATION_5_6, AppDatabase.MIGRATION_6_7, AppDatabase.MIGRATION_7_8, AppDatabase.MIGRATION_8_9)
                .addCallback(AppDatabase.buildOpenCallback(context, holder))
                .build();
        return holder[0];
    }

    @Provides
    public static CardDefinitionDao provideCardDefinitionDao(AppDatabase db) {
        return db.cardDefinitionDao();
    }

    @Provides
    public static RewardRateDao provideRewardRateDao(AppDatabase db) {
        return db.rewardRateDao();
    }

    @Provides
    public static UserCardDao provideUserCardDao(AppDatabase db) {
        return db.userCardDao();
    }

    @Provides
    public static UserCardChoiceCategoryDao provideChoiceCategoryDao(AppDatabase db) {
        return db.userCardChoiceCategoryDao();
    }

    @Provides
    public static CardBenefitDao provideCardBenefitDao(AppDatabase db) {
        return db.cardBenefitDao();
    }

    @Provides
    public static BenefitUsageDao provideBenefitUsageDao(AppDatabase db) {
        return db.benefitUsageDao();
    }

    @Provides
    public static ProductChangeRecordDao provideProductChangeRecordDao(AppDatabase db) {
        return db.productChangeRecordDao();
    }

    @Provides
    public static PointValuationDao providePointValuationDao(AppDatabase db) {
        return db.pointValuationDao();
    }

    @Provides
    public static TransferPartnerDao provideTransferPartnerDao(AppDatabase db) {
        return db.transferPartnerDao();
    }

    @Provides
    public static CustomCategoryDao provideCustomCategoryDao(AppDatabase db) {
        return db.customCategoryDao();
    }

    @Provides
    public static CustomCategoryRateDao provideCustomCategoryRateDao(AppDatabase db) {
        return db.customCategoryRateDao();
    }

    @Provides
    public static WelcomeBonusDao provideWelcomeBonusDao(AppDatabase db) {
        return db.welcomeBonusDao();
    }

    @Provides
    @Singleton
    public static WelcomeBonusRepository provideWelcomeBonusRepository(WelcomeBonusDao dao) {
        return new WelcomeBonusRepository(dao);
    }

    @Provides
    public static com.example.ccrewards.data.db.dao.RotationalBonusDao provideRotationalBonusDao(AppDatabase db) {
        return db.rotationalBonusDao();
    }

    @Provides
    public static com.example.ccrewards.data.db.dao.RotationalBonusCategoryDao provideRotationalBonusCategoryDao(AppDatabase db) {
        return db.rotationalBonusCategoryDao();
    }
}

package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ccrewards.data.model.BenefitUsage;

import java.util.List;

@Dao
public interface BenefitUsageDao {

    @Query("SELECT * FROM benefit_usage WHERE userCardId = :userCardId")
    LiveData<List<BenefitUsage>> getUsageForCard(long userCardId);

    @Query("SELECT * FROM benefit_usage WHERE userCardId = :userCardId " +
           "AND benefitId = :benefitId AND periodKey = :periodKey")
    BenefitUsage getUsageSync(long userCardId, long benefitId, String periodKey);

    @Query("SELECT * FROM benefit_usage WHERE benefitId = :benefitId ORDER BY periodKey DESC")
    LiveData<List<BenefitUsage>> getUsageHistoryForBenefit(long benefitId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BenefitUsage usage);

    @Update
    void update(BenefitUsage usage);

    @Query("UPDATE benefit_usage SET isUsed = :isUsed " +
           "WHERE userCardId = :userCardId AND benefitId = :benefitId AND periodKey = :periodKey")
    void setUsed(long userCardId, long benefitId, String periodKey, boolean isUsed);
}

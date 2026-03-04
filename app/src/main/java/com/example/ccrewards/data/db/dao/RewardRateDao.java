package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.RewardRate;

import java.util.List;

@Dao
public interface RewardRateDao {

    @Query("SELECT * FROM reward_rates WHERE cardDefinitionId = :cardId ORDER BY category, rateType")
    LiveData<List<RewardRate>> getRatesForCard(String cardId);

    @Query("SELECT * FROM reward_rates WHERE cardDefinitionId = :cardId ORDER BY category, rateType")
    List<RewardRate> getRatesForCardSync(String cardId);

    @Query("SELECT * FROM reward_rates WHERE category = :category ORDER BY rate DESC")
    LiveData<List<RewardRate>> getRatesForCategory(RewardCategory category);

    @Query("SELECT * FROM reward_rates WHERE category = :category ORDER BY rate DESC")
    List<RewardRate> getRatesForCategorySync(RewardCategory category);

    @Query("SELECT * FROM reward_rates WHERE cardDefinitionId = :cardId AND category = :category")
    List<RewardRate> getRatesForCardAndCategory(String cardId, RewardCategory category);

    @Query("SELECT * FROM reward_rates")
    LiveData<List<RewardRate>> getAllRates();

    @Query("SELECT * FROM reward_rates")
    List<RewardRate> getAllRatesSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RewardRate> rates);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RewardRate rate);

    @Update
    void update(RewardRate rate);

    @Delete
    void delete(RewardRate rate);

    @Query("DELETE FROM reward_rates WHERE cardDefinitionId = :cardId AND isCustomized = 1")
    void clearCustomizedRates(String cardId);

    @Query("DELETE FROM reward_rates WHERE cardDefinitionId = :cardId AND isCustomized = 0")
    void deleteSeededRatesForCard(String cardId);

    @Query("DELETE FROM reward_rates WHERE cardDefinitionId = :cardId")
    void deleteAllForCard(String cardId);

    @Query("UPDATE reward_rates SET isCustomized = 0 WHERE cardDefinitionId = :cardId")
    void resetCustomizationsForCard(String cardId);

    @Query("SELECT COUNT(*) FROM reward_rates")
    int count();
}

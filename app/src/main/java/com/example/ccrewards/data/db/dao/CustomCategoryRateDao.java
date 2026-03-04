package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ccrewards.data.model.CustomCategoryRate;

import java.util.List;

@Dao
public interface CustomCategoryRateDao {

    @Query("SELECT * FROM custom_category_rates WHERE customCategoryId = :catId")
    LiveData<List<CustomCategoryRate>> getRatesForCategoryLive(long catId);

    @Query("SELECT * FROM custom_category_rates WHERE customCategoryId = :catId")
    List<CustomCategoryRate> getRatesForCategorySync(long catId);

    @Query("SELECT * FROM custom_category_rates")
    List<CustomCategoryRate> getAllSync();

    @Query("SELECT * FROM custom_category_rates WHERE cardDefinitionId = :cardId")
    List<CustomCategoryRate> getRatesForCardSync(String cardId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(CustomCategoryRate rate);

    @Query("DELETE FROM custom_category_rates WHERE customCategoryId = :catId")
    void deleteRatesForCategory(long catId);

    @Query("DELETE FROM custom_category_rates WHERE customCategoryId = :catId AND cardDefinitionId = :cardId")
    void deleteRateForCard(long catId, String cardId);

    @Query("DELETE FROM custom_category_rates WHERE cardDefinitionId = :cardId")
    void deleteAllForCard(String cardId);
}

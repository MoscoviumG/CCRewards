package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ccrewards.data.model.CardBenefit;

import java.util.List;

@Dao
public interface CardBenefitDao {

    @Query("SELECT * FROM card_benefits WHERE cardDefinitionId = :cardId ORDER BY resetPeriod, name")
    LiveData<List<CardBenefit>> getBenefitsForCard(String cardId);

    @Query("SELECT * FROM card_benefits WHERE id = :id")
    CardBenefit getBenefitByIdSync(long id);

    @Query("SELECT * FROM card_benefits")
    LiveData<List<CardBenefit>> getAllBenefits();

    @Query("SELECT * FROM card_benefits")
    List<CardBenefit> getAllBenefitsSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CardBenefit> benefits);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CardBenefit benefit);

    @Update
    void update(CardBenefit benefit);

    @Delete
    void delete(CardBenefit benefit);

    @Query("SELECT * FROM card_benefits WHERE cardDefinitionId = :cardId AND isCustom = 0")
    List<CardBenefit> getSeededBenefitsForCardSync(String cardId);

    @Query("DELETE FROM card_benefits WHERE cardDefinitionId = :cardId AND isCustom = 0")
    void deleteSeededBenefitsForCard(String cardId);

    @Query("SELECT COUNT(*) FROM card_benefits")
    int count();
}

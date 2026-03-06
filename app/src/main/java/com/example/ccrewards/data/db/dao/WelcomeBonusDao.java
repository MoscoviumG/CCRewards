package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ccrewards.data.model.WelcomeBonus;

import java.util.List;

@Dao
public interface WelcomeBonusDao {

    @Query("SELECT * FROM welcome_bonuses WHERE userCardId = :id")
    LiveData<WelcomeBonus> getByUserCard(long id);

    @Query("SELECT * FROM welcome_bonuses WHERE achieved = 0 AND showInBestCard = 1")
    LiveData<List<WelcomeBonus>> getActiveLive();

    @Query("SELECT * FROM welcome_bonuses WHERE achieved = 0 AND showInBestCard = 1")
    List<WelcomeBonus> getActiveSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(WelcomeBonus wb);

    @Delete
    void delete(WelcomeBonus wb);

    @Query("UPDATE welcome_bonuses SET achieved = 1 WHERE userCardId = :userCardId")
    void markAchieved(long userCardId);

    @Query("UPDATE welcome_bonuses SET spendUsedCents = :cents WHERE userCardId = :userCardId")
    void updateSpendUsed(long userCardId, int cents);
}

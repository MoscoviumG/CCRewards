package com.example.ccrewards.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ccrewards.data.model.AutoBonusCreated;

@Dao
public interface AutoBonusCreatedDao {

    @Query("SELECT COUNT(*) > 0 FROM auto_bonus_created " +
           "WHERE userCardId = :userCardId AND year = :year AND quarter = :quarter")
    boolean exists(long userCardId, int year, int quarter);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(AutoBonusCreated row);
}

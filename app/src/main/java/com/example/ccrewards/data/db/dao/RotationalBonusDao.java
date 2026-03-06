package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ccrewards.data.model.RotationalBonus;

import java.util.List;

@Dao
public interface RotationalBonusDao {

    @Insert
    long insert(RotationalBonus bonus);

    @Update
    void update(RotationalBonus bonus);

    @Delete
    void delete(RotationalBonus bonus);

    @Query("SELECT * FROM rotational_bonuses WHERE id = :id")
    RotationalBonus getByIdSync(long id);

    @Query("SELECT * FROM rotational_bonuses WHERE isFullyUsed = 0 AND (endDate IS NULL OR endDate >= :todayEpoch)")
    LiveData<List<RotationalBonus>> getActiveLive(long todayEpoch);

    @Query("SELECT * FROM rotational_bonuses WHERE isFullyUsed = 0 AND (endDate IS NULL OR endDate >= :todayEpoch)")
    List<RotationalBonus> getActiveSync(long todayEpoch);

    @Query("SELECT * FROM rotational_bonuses WHERE userCardId = :userCardId ORDER BY id DESC")
    LiveData<List<RotationalBonus>> getForUserCard(long userCardId);

    @Query("SELECT * FROM rotational_bonuses WHERE userCardId = :userCardId ORDER BY id DESC")
    List<RotationalBonus> getForUserCardSync(long userCardId);
}

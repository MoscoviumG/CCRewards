package com.example.ccrewards.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ccrewards.data.model.QuarterlyBonusScheduleRow;

import java.util.List;

@Dao
public interface QuarterlyBonusScheduleDao {

    @Query("SELECT * FROM quarterly_bonus_schedule WHERE year = :year AND quarter = :quarter")
    List<QuarterlyBonusScheduleRow> getForYearAndQuarter(int year, int quarter);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<QuarterlyBonusScheduleRow> rows);
}

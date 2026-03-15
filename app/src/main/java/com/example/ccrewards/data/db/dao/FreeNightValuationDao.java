package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ccrewards.data.model.FreeNightValuation;

import java.util.List;

@Dao
public interface FreeNightValuationDao {

    @Query("SELECT * FROM free_night_valuations ORDER BY hotelGroup, limitType, pointsCap, hyattCategory")
    LiveData<List<FreeNightValuation>> getAll();

    @Query("SELECT * FROM free_night_valuations ORDER BY hotelGroup, limitType, pointsCap, hyattCategory")
    List<FreeNightValuation> getAllSync();

    @Query("SELECT * FROM free_night_valuations WHERE typeKey = :typeKey")
    FreeNightValuation getByTypeKeySync(String typeKey);

    /** INSERT OR IGNORE — preserves user edits to valueCents. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<FreeNightValuation> valuations);

    @Update
    void update(FreeNightValuation valuation);

    /** Updates only the defaultValueCents column (called on seed refresh). */
    @Query("UPDATE free_night_valuations SET defaultValueCents = :defaultValueCents WHERE typeKey = :typeKey")
    void updateDefault(String typeKey, int defaultValueCents);
}

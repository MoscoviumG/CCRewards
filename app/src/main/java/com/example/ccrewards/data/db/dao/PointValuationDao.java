package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ccrewards.data.model.PointValuation;

import java.util.List;

@Dao
public interface PointValuationDao {

    @Query("SELECT * FROM point_valuations ORDER BY rewardCurrencyName")
    LiveData<List<PointValuation>> getAllValuations();

    @Query("SELECT * FROM point_valuations WHERE rewardCurrencyName = :currencyName")
    PointValuation getValuationSync(String currencyName);

    @Query("SELECT * FROM point_valuations ORDER BY rewardCurrencyName")
    List<PointValuation> getAllValuationsSync();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<PointValuation> valuations);

    @Update
    void update(PointValuation valuation);

    @Query("UPDATE point_valuations SET centsPerPoint = defaultCentsPerPoint " +
           "WHERE rewardCurrencyName = :currencyName")
    void resetToDefault(String currencyName);

    @Query("SELECT COUNT(*) FROM point_valuations")
    int count();
}

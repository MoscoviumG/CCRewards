package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ccrewards.data.model.ProductChangeRecord;

import java.util.List;

@Dao
public interface ProductChangeRecordDao {

    @Query("SELECT * FROM product_change_records WHERE userCardId = :userCardId ORDER BY changeDate DESC")
    LiveData<List<ProductChangeRecord>> getHistoryForCard(long userCardId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ProductChangeRecord record);

    @Delete
    void delete(ProductChangeRecord record);
}

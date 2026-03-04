package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ccrewards.data.model.CustomCategory;

import java.util.List;

@Dao
public interface CustomCategoryDao {

    @Query("SELECT * FROM custom_categories ORDER BY sortOrder, id")
    LiveData<List<CustomCategory>> getAllLive();

    @Query("SELECT * FROM custom_categories ORDER BY sortOrder, id")
    List<CustomCategory> getAllSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CustomCategory category);

    @Update
    void update(CustomCategory category);

    @Delete
    void delete(CustomCategory category);

    @Query("DELETE FROM custom_categories WHERE id = :id")
    void deleteById(long id);
}

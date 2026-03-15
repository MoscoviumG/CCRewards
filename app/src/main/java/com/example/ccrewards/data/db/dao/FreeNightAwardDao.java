package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ccrewards.data.model.FreeNightAward;

import java.util.List;

@Dao
public interface FreeNightAwardDao {

    @Query("SELECT * FROM free_night_awards WHERE userCardId = :userCardId ORDER BY expirationDate ASC")
    LiveData<List<FreeNightAward>> getForUserCard(long userCardId);

    @Query("SELECT * FROM free_night_awards WHERE userCardId = :userCardId ORDER BY expirationDate ASC")
    List<FreeNightAward> getForUserCardSync(long userCardId);

    @Query("SELECT * FROM free_night_awards WHERE id = :id")
    FreeNightAward getByIdSync(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(FreeNightAward award);

    @Update
    void update(FreeNightAward award);

    @Delete
    void delete(FreeNightAward award);

    @Query("DELETE FROM free_night_awards WHERE userCardId = :userCardId")
    void deleteForUserCard(long userCardId);

    @Query("SELECT * FROM free_night_awards WHERE userCardId IN (:userCardIds) ORDER BY expirationDate ASC")
    List<FreeNightAward> getForUserCardsSync(List<Long> userCardIds);

    @Query("SELECT * FROM free_night_awards WHERE userCardId = :userCardId AND isRecurring = 1")
    List<FreeNightAward> getRecurringForCardSync(long userCardId);
}

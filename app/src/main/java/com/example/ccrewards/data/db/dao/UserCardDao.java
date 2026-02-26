package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.model.relations.UserCardWithDetails;

import java.util.List;

@Dao
public interface UserCardDao {

    @Transaction
    @Query("SELECT * FROM user_cards WHERE closeDate IS NULL ORDER BY sortOrder, openDate DESC")
    LiveData<List<UserCardWithDetails>> getActiveCardsWithDetails();

    @Transaction
    @Query("SELECT * FROM user_cards ORDER BY sortOrder, openDate DESC")
    LiveData<List<UserCardWithDetails>> getAllCardsWithDetails();

    @Transaction
    @Query("SELECT * FROM user_cards WHERE id = :id")
    LiveData<UserCardWithDetails> getCardWithDetails(long id);

    @Query("SELECT * FROM user_cards WHERE id = :id")
    UserCard getCardByIdSync(long id);

    @Query("SELECT DISTINCT cardDefinitionId FROM user_cards WHERE closeDate IS NULL")
    List<String> getActiveCardDefinitionIdsSync();

    @Query("SELECT * FROM user_cards WHERE closeDate IS NULL ORDER BY sortOrder, openDate DESC")
    List<UserCard> getAllActiveUserCardsSync();

    @Query("SELECT MAX(sortOrder) FROM user_cards")
    Integer getMaxSortOrder();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UserCard card);

    @Update
    void update(UserCard card);

    @Delete
    void delete(UserCard card);

    @Query("UPDATE user_cards SET cardDefinitionId = :newCardDefinitionId WHERE id = :userCardId")
    void updateCardDefinition(long userCardId, String newCardDefinitionId);
}

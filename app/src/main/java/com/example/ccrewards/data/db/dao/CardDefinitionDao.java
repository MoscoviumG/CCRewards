package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ccrewards.data.model.CardDefinition;

import java.util.List;

@Dao
public interface CardDefinitionDao {

    @Query("SELECT * FROM card_definitions ORDER BY issuer, displayName")
    LiveData<List<CardDefinition>> getAllCards();

    @Query("SELECT * FROM card_definitions WHERE isBusinessCard = 0 ORDER BY issuer, displayName")
    LiveData<List<CardDefinition>> getPersonalCards();

    @Query("SELECT * FROM card_definitions WHERE isBusinessCard = 1 ORDER BY issuer, displayName")
    LiveData<List<CardDefinition>> getBusinessCards();

    @Query("SELECT * FROM card_definitions WHERE id = :id")
    CardDefinition getCardById(String id);

    @Query("SELECT * FROM card_definitions WHERE issuer = :issuer ORDER BY displayName")
    LiveData<List<CardDefinition>> getCardsByIssuer(String issuer);

    @Query("SELECT * FROM card_definitions WHERE displayName LIKE '%' || :query || '%' " +
           "OR issuer LIKE '%' || :query || '%' ORDER BY issuer, displayName")
    LiveData<List<CardDefinition>> searchCards(String query);

    @Query("SELECT * FROM card_definitions ORDER BY issuer, displayName")
    List<CardDefinition> getAllCardsSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CardDefinition> cards);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CardDefinition card);

    @Update
    void update(CardDefinition card);

    @Delete
    void delete(CardDefinition card);

    @Query("SELECT COUNT(*) FROM card_definitions")
    int count();
}

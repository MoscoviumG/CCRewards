package com.example.ccrewards.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ccrewards.data.model.RotationalBonusCategory;

import java.util.List;

@Dao
public interface RotationalBonusCategoryDao {

    @Insert
    void insert(RotationalBonusCategory cat);

    @Query("SELECT * FROM rotational_bonus_categories WHERE rotationalBonusId = :bonusId")
    List<RotationalBonusCategory> getForBonusSync(long bonusId);

    @Query("DELETE FROM rotational_bonus_categories WHERE rotationalBonusId = :bonusId")
    void deleteForBonus(long bonusId);

    @Query("SELECT * FROM rotational_bonus_categories")
    List<RotationalBonusCategory> getAllSync();

    @Query("SELECT rbc.* FROM rotational_bonus_categories rbc " +
           "INNER JOIN rotational_bonuses rb ON rb.id = rbc.rotationalBonusId " +
           "WHERE rb.userCardId = :userCardId")
    List<RotationalBonusCategory> getForUserCardSync(long userCardId);
}

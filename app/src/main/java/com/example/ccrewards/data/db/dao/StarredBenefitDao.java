package com.example.ccrewards.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ccrewards.data.model.StarredBenefit;

import java.util.List;

@Dao
public interface StarredBenefitDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void star(StarredBenefit starred);

    @Delete
    void unstar(StarredBenefit starred);

    @Query("SELECT COUNT(*) > 0 FROM starred_benefits " +
           "WHERE userCardId = :userCardId AND benefitId = :benefitId")
    boolean isStarred(long userCardId, long benefitId);

    @Query("SELECT benefitId FROM starred_benefits WHERE userCardId = :userCardId")
    List<Long> getStarredBenefitIdsForCard(long userCardId);
}

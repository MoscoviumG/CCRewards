package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ccrewards.data.model.UserCardChoiceCategory;

import java.util.List;

@Dao
public interface UserCardChoiceCategoryDao {

    @Query("SELECT * FROM user_card_choice_categories WHERE userCardId = :userCardId")
    LiveData<List<UserCardChoiceCategory>> getChoicesForCard(long userCardId);

    @Query("SELECT * FROM user_card_choice_categories WHERE userCardId = :userCardId AND choiceGroupId = :groupId")
    UserCardChoiceCategory getChoiceForGroupSync(long userCardId, String groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UserCardChoiceCategory choice);

    @Update
    void update(UserCardChoiceCategory choice);

    @Delete
    void delete(UserCardChoiceCategory choice);

    @Query("DELETE FROM user_card_choice_categories WHERE userCardId = :userCardId")
    void deleteAllForCard(long userCardId);
}

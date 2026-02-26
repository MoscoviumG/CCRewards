package com.example.ccrewards.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "user_card_choice_categories",
    indices = { @Index(value = {"userCardId", "choiceGroupId"}, unique = true) }
)
public class UserCardChoiceCategory {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userCardId;
    public String choiceGroupId;
    public long chosenCategoryId;   // FK → RewardRate.id

    public UserCardChoiceCategory(long userCardId, String choiceGroupId, long chosenCategoryId) {
        this.userCardId = userCardId;
        this.choiceGroupId = choiceGroupId;
        this.chosenCategoryId = chosenCategoryId;
    }
}

package com.example.ccrewards.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "product_change_records")
public class ProductChangeRecord {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userCardId;
    public String fromCardDefinitionId;
    public String toCardDefinitionId;
    public LocalDate changeDate;
    public String notes;

    public ProductChangeRecord(long userCardId, String fromCardDefinitionId,
                               String toCardDefinitionId, LocalDate changeDate, String notes) {
        this.userCardId = userCardId;
        this.fromCardDefinitionId = fromCardDefinitionId;
        this.toCardDefinitionId = toCardDefinitionId;
        this.changeDate = changeDate;
        this.notes = notes;
    }
}

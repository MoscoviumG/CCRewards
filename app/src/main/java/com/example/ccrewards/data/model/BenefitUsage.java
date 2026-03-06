package com.example.ccrewards.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(
    tableName = "benefit_usage",
    indices = { @Index(value = {"userCardId", "benefitId", "periodKey"}, unique = true) }
)
public class BenefitUsage {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userCardId;
    public long benefitId;
    public String periodKey;    // "2026-02", "2026", "2026-Q1", "2026-H1"
    public boolean isUsed;
    public LocalDate usedDate;  // null if not yet used
    public String notes;

    /** How many cents of this benefit have been used in the current period (0 = none). */
    @ColumnInfo(defaultValue = "0")
    public int usedCents = 0;

    public BenefitUsage(long userCardId, long benefitId, String periodKey,
                        boolean isUsed, LocalDate usedDate, String notes) {
        this.userCardId = userCardId;
        this.benefitId = benefitId;
        this.periodKey = periodKey;
        this.isUsed = isUsed;
        this.usedDate = usedDate;
        this.notes = notes;
    }
}

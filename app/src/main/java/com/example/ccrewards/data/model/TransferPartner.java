package com.example.ccrewards.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transfer_partners")
public class TransferPartner {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String rewardCurrencyName;
    public String partnerName;
    public TransferPartnerType partnerType;
    public int ratioFrom;
    public double ratioTo;
    public boolean isInstantTransfer;
    public String notes;

    public TransferPartner(String rewardCurrencyName, String partnerName,
                           TransferPartnerType partnerType,
                           int ratioFrom, double ratioTo,
                           boolean isInstantTransfer, String notes) {
        this.rewardCurrencyName = rewardCurrencyName;
        this.partnerName = partnerName;
        this.partnerType = partnerType;
        this.ratioFrom = ratioFrom;
        this.ratioTo = ratioTo;
        this.isInstantTransfer = isInstantTransfer;
        this.notes = notes;
    }
}

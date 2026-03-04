package com.example.ccrewards.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ccrewards.data.model.TransferPartner;
import com.example.ccrewards.data.model.TransferPartnerType;

import java.util.List;

@Dao
public interface TransferPartnerDao {

    @Query("SELECT * FROM transfer_partners WHERE rewardCurrencyName = :currencyName " +
           "ORDER BY partnerType, partnerName")
    LiveData<List<TransferPartner>> getPartnersForCurrency(String currencyName);

    @Query("SELECT * FROM transfer_partners WHERE rewardCurrencyName = :currencyName " +
           "AND partnerType = :type ORDER BY partnerName")
    LiveData<List<TransferPartner>> getPartnersForCurrencyAndType(String currencyName, TransferPartnerType type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TransferPartner> partners);

    @Query("DELETE FROM transfer_partners")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM transfer_partners")
    int count();
}

package com.example.ccrewards.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.ccrewards.data.db.dao.BenefitUsageDao;
import com.example.ccrewards.data.db.dao.CardBenefitDao;
import com.example.ccrewards.data.db.dao.TransferPartnerDao;
import com.example.ccrewards.data.db.dao.UserCardDao;
import com.example.ccrewards.data.model.BenefitUsage;
import com.example.ccrewards.data.model.BenefitUsageWithAmount;
import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.TransferPartner;
import com.example.ccrewards.data.model.TransferPartnerType;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BenefitRepository {

    private final CardBenefitDao cardBenefitDao;
    private final BenefitUsageDao benefitUsageDao;
    private final TransferPartnerDao transferPartnerDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public BenefitRepository(CardBenefitDao cardBenefitDao,
                              BenefitUsageDao benefitUsageDao,
                              TransferPartnerDao transferPartnerDao) {
        this.cardBenefitDao = cardBenefitDao;
        this.benefitUsageDao = benefitUsageDao;
        this.transferPartnerDao = transferPartnerDao;
    }

    public LiveData<List<CardBenefit>> getBenefitsForCard(String cardId) {
        return cardBenefitDao.getBenefitsForCard(cardId);
    }

    public LiveData<List<CardBenefit>> getAllBenefits() {
        return cardBenefitDao.getAllBenefits();
    }

    public List<CardBenefit> getAllBenefitsSync() {
        return cardBenefitDao.getAllBenefitsSync();
    }

    public CardBenefit getBenefitByIdSync(long id) {
        return cardBenefitDao.getBenefitByIdSync(id);
    }

    public void insertBenefit(CardBenefit benefit, Runnable onComplete) {
        executor.execute(() -> {
            cardBenefitDao.insert(benefit);
            if (onComplete != null) onComplete.run();
        });
    }

    public void updateBenefit(CardBenefit benefit) {
        executor.execute(() -> cardBenefitDao.update(benefit));
    }

    public void deleteBenefit(CardBenefit benefit) {
        executor.execute(() -> cardBenefitDao.delete(benefit));
    }

    public LiveData<List<BenefitUsage>> getUsageForCard(long userCardId) {
        return benefitUsageDao.getUsageForCard(userCardId);
    }

    public LiveData<List<BenefitUsage>> getUsageHistoryForBenefit(long benefitId) {
        return benefitUsageDao.getUsageHistoryForBenefit(benefitId);
    }

    /** Synchronous lookup — call from background thread only. */
    public com.example.ccrewards.data.model.BenefitUsage getUsageSync(
            long userCardId, long benefitId, String periodKey) {
        return benefitUsageDao.getUsageSync(userCardId, benefitId, periodKey);
    }

    public LiveData<List<BenefitUsageWithAmount>> getAllUsedWithAmounts() {
        return benefitUsageDao.getAllUsedWithAmounts();
    }

    public LiveData<List<BenefitUsageWithAmount>> getUsedWithAmountsForCard(long userCardId) {
        return benefitUsageDao.getUsedWithAmountsForCard(userCardId);
    }

    public void setUsed(long userCardId, long benefitId, String periodKey, boolean isUsed) {
        executor.execute(() -> {
            BenefitUsage existing = benefitUsageDao.getUsageSync(userCardId, benefitId, periodKey);
            if (existing != null) {
                existing.isUsed = isUsed;
                benefitUsageDao.update(existing);
            } else {
                BenefitUsage newUsage = new BenefitUsage(userCardId, benefitId, periodKey,
                        isUsed, isUsed ? java.time.LocalDate.now() : null, null);
                benefitUsageDao.insert(newUsage);
            }
        });
    }

    // ── Transfer Partners ─────────────────────────────────────────────────────

    public LiveData<List<TransferPartner>> getPartnersForCurrency(String currencyName) {
        return transferPartnerDao.getPartnersForCurrency(currencyName);
    }

    public LiveData<List<TransferPartner>> getAirlinesForCurrency(String currencyName) {
        return transferPartnerDao.getPartnersForCurrencyAndType(currencyName, TransferPartnerType.AIRLINE);
    }

    public LiveData<List<TransferPartner>> getHotelsForCurrency(String currencyName) {
        return transferPartnerDao.getPartnersForCurrencyAndType(currencyName, TransferPartnerType.HOTEL);
    }
}

package com.example.ccrewards.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.ccrewards.data.db.dao.BenefitUsageDao;
import com.example.ccrewards.data.db.dao.CardBenefitDao;
import com.example.ccrewards.data.db.dao.StarredBenefitDao;
import com.example.ccrewards.data.db.dao.TransferPartnerDao;
import com.example.ccrewards.data.db.dao.UserCardDao;
import com.example.ccrewards.data.model.BenefitUsage;
import com.example.ccrewards.data.model.BenefitUsageWithAmount;
import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.StarredBenefit;
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
    private final StarredBenefitDao starredBenefitDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public BenefitRepository(CardBenefitDao cardBenefitDao,
                              BenefitUsageDao benefitUsageDao,
                              TransferPartnerDao transferPartnerDao,
                              StarredBenefitDao starredBenefitDao) {
        this.cardBenefitDao = cardBenefitDao;
        this.benefitUsageDao = benefitUsageDao;
        this.transferPartnerDao = transferPartnerDao;
        this.starredBenefitDao = starredBenefitDao;
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

    /** Update partial usage amount. isUsed is set to true when usedCents >= amountCents. */
    public void setUsedAmount(long userCardId, long benefitId, String periodKey,
                              int usedCents, int amountCents) {
        executor.execute(() -> {
            boolean isFullyUsed = amountCents > 0 && usedCents >= amountCents;
            BenefitUsage existing = benefitUsageDao.getUsageSync(userCardId, benefitId, periodKey);
            if (existing != null) {
                existing.usedCents = usedCents;
                existing.isUsed = isFullyUsed;
                if (isFullyUsed && existing.usedDate == null) {
                    existing.usedDate = java.time.LocalDate.now();
                }
                benefitUsageDao.update(existing);
            } else {
                BenefitUsage newUsage = new BenefitUsage(userCardId, benefitId, periodKey,
                        isFullyUsed, isFullyUsed ? java.time.LocalDate.now() : null, null);
                newUsage.usedCents = usedCents;
                benefitUsageDao.insert(newUsage);
            }
        });
    }

    public void updateUsage(BenefitUsage usage) {
        executor.execute(() -> benefitUsageDao.update(usage));
    }

    public void deleteUsage(BenefitUsage usage) {
        executor.execute(() -> benefitUsageDao.delete(usage));
    }

    public void setUsed(long userCardId, long benefitId, String periodKey, boolean isUsed) {
        executor.execute(() -> {
            BenefitUsage existing = benefitUsageDao.getUsageSync(userCardId, benefitId, periodKey);
            if (existing != null) {
                existing.isUsed = isUsed;
                if (isUsed) existing.usedCents = 0; // will be overridden by slider when used
                benefitUsageDao.update(existing);
            } else {
                BenefitUsage newUsage = new BenefitUsage(userCardId, benefitId, periodKey,
                        isUsed, isUsed ? java.time.LocalDate.now() : null, null);
                benefitUsageDao.insert(newUsage);
            }
        });
    }

    // ── Starred Benefits ──────────────────────────────────────────────────────

    /** Synchronous — call from background thread only. */
    public boolean isStarred(long userCardId, long benefitId) {
        return starredBenefitDao.isStarred(userCardId, benefitId);
    }

    /** Toggles star state on a background thread, then calls onComplete on that thread. */
    public void toggleStar(long userCardId, long benefitId, Runnable onComplete) {
        executor.execute(() -> {
            StarredBenefit sb = new StarredBenefit(userCardId, benefitId);
            if (starredBenefitDao.isStarred(userCardId, benefitId)) {
                starredBenefitDao.unstar(sb);
            } else {
                starredBenefitDao.star(sb);
            }
            if (onComplete != null) onComplete.run();
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

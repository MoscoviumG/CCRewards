package com.example.ccrewards.data.repository;

import androidx.lifecycle.LiveData;

import com.example.ccrewards.data.db.dao.FreeNightAwardDao;
import com.example.ccrewards.data.db.dao.FreeNightValuationDao;
import com.example.ccrewards.data.model.FreeNightAward;
import com.example.ccrewards.data.model.FreeNightValuation;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FreeNightRepository {

    private final FreeNightAwardDao awardDao;
    private final FreeNightValuationDao valuationDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public FreeNightRepository(FreeNightAwardDao awardDao, FreeNightValuationDao valuationDao) {
        this.awardDao = awardDao;
        this.valuationDao = valuationDao;
    }

    // ── Awards ────────────────────────────────────────────────────────────────

    public LiveData<List<FreeNightAward>> getAwardsForCard(long userCardId) {
        return awardDao.getForUserCard(userCardId);
    }

    public List<FreeNightAward> getAwardsForCardSync(long userCardId) {
        return awardDao.getForUserCardSync(userCardId);
    }

    public FreeNightAward getAwardByIdSync(long awardId) {
        return awardDao.getByIdSync(awardId);
    }

    public void insertAward(FreeNightAward award, Runnable onComplete) {
        executor.execute(() -> {
            awardDao.insert(award);
            if (onComplete != null) onComplete.run();
        });
    }

    /** Synchronous insert — call only from a background thread. */
    public void insertAwardSync(FreeNightAward award) {
        awardDao.insert(award);
    }

    public void markUsed(long awardId, int usedCount) {
        executor.execute(() -> {
            FreeNightAward award = awardDao.getByIdSync(awardId);
            if (award == null) return;
            award.usedCount = Math.min(usedCount, award.totalCount);
            awardDao.update(award);
        });
    }

    public void updateAward(FreeNightAward award) {
        executor.execute(() -> awardDao.update(award));
    }

    public void deleteAward(long awardId) {
        executor.execute(() -> {
            FreeNightAward award = awardDao.getByIdSync(awardId);
            if (award != null) awardDao.delete(award);
        });
    }

    /** Synchronous bulk load across multiple user cards. Call from background thread. */
    public List<FreeNightAward> getAllAwardsForCardsSync(List<Long> userCardIds) {
        if (userCardIds == null || userCardIds.isEmpty()) return new java.util.ArrayList<>();
        return awardDao.getForUserCardsSync(userCardIds);
    }

    /**
     * Called after a card is added. Creates recurring FreeNightAward entries for cards
     * that have annual free night benefits. Must be called from a background thread.
     */
    public void ensureAnnualFreeNightsForCard(long userCardId, String cardDefinitionId,
                                              java.time.LocalDate openDate) {
        List<com.example.ccrewards.data.seed.AnnualFreeNightSeedData.Entry> entries =
                com.example.ccrewards.data.seed.AnnualFreeNightSeedData.getEntries();
        for (com.example.ccrewards.data.seed.AnnualFreeNightSeedData.Entry entry : entries) {
            if (!entry.cardDefinitionId.equals(cardDefinitionId)) continue;
            // Only create if no recurring award of this type already exists for this card
            List<FreeNightAward> existing = awardDao.getRecurringForCardSync(userCardId);
            boolean alreadyExists = false;
            for (FreeNightAward a : existing) {
                if (a.typeKey.equals(entry.typeKey)) { alreadyExists = true; break; }
            }
            if (alreadyExists) continue;

            FreeNightAward award = new FreeNightAward(userCardId, entry.typeKey,
                    entry.label, null, 1, false);
            award.isRecurring = true;
            // Default renewal = card anniversary month/day (or Jan 1 if no open date)
            if (openDate != null) {
                award.renewalMonth = openDate.getMonthValue();
                award.renewalDay   = openDate.getDayOfMonth();
                // Expiration = next occurrence of anniversary
                java.time.LocalDate nextRenewal = openDate.withYear(java.time.LocalDate.now().getYear());
                if (!nextRenewal.isAfter(java.time.LocalDate.now())) {
                    nextRenewal = nextRenewal.plusYears(1);
                }
                award.expirationDate = nextRenewal;
            } else {
                award.renewalMonth = 1;
                award.renewalDay   = 1;
                award.expirationDate = java.time.LocalDate.now().withMonth(1).withDayOfMonth(1).plusYears(1);
            }
            awardDao.insert(award);
        }
    }

    /**
     * For each recurring award, if today >= expirationDate, creates a renewed award.
     * Must be called from a background thread.
     */
    public void refreshRecurringAwardsSync(List<Long> userCardIds) {
        if (userCardIds == null || userCardIds.isEmpty()) return;
        java.time.LocalDate today = java.time.LocalDate.now();
        for (long userCardId : userCardIds) {
            List<FreeNightAward> recurring = awardDao.getRecurringForCardSync(userCardId);
            // Group by typeKey — find most recent per type
            java.util.Map<String, FreeNightAward> latestByType = new java.util.HashMap<>();
            for (FreeNightAward a : recurring) {
                FreeNightAward cur = latestByType.get(a.typeKey);
                if (cur == null || (a.expirationDate != null && cur.expirationDate != null
                        && a.expirationDate.isAfter(cur.expirationDate))) {
                    latestByType.put(a.typeKey, a);
                }
            }
            for (FreeNightAward latest : latestByType.values()) {
                if (latest.expirationDate == null) continue;
                if (!today.isBefore(latest.expirationDate)) {
                    // Need a new award for this year
                    FreeNightAward renewed = new FreeNightAward(latest.userCardId, latest.typeKey,
                            latest.label, null, latest.totalCount, false);
                    renewed.isRecurring = true;
                    renewed.renewalMonth = latest.renewalMonth;
                    renewed.renewalDay   = latest.renewalDay;
                    // Next expiration = next year's renewal date
                    int month = latest.renewalMonth != null ? latest.renewalMonth : 1;
                    int day   = latest.renewalDay   != null ? latest.renewalDay   : 1;
                    java.time.LocalDate nextExp;
                    try {
                        nextExp = today.withMonth(month).withDayOfMonth(day);
                        if (!nextExp.isAfter(today)) nextExp = nextExp.plusYears(1);
                    } catch (Exception e) {
                        nextExp = today.plusYears(1);
                    }
                    renewed.expirationDate = nextExp;
                    awardDao.insert(renewed);
                }
            }
        }
    }

    /**
     * Splits any award with totalCount > 1 into individual single-count awards.
     * This is a one-time migration for legacy data. Call from a background thread.
     */
    public void splitMultiCountAwardsSync(long userCardId) {
        List<FreeNightAward> awards = awardDao.getForUserCardSync(userCardId);
        for (FreeNightAward award : awards) {
            if (award.totalCount <= 1) continue;
            int usedSlots = Math.min(award.usedCount, award.totalCount);
            for (int i = 0; i < award.totalCount; i++) {
                FreeNightAward split = new FreeNightAward(
                        award.userCardId, award.typeKey, award.label,
                        award.expirationDate, 1, award.isFromWelcomeBonus);
                split.isRecurring = award.isRecurring;
                split.renewalMonth = award.renewalMonth;
                split.renewalDay = award.renewalDay;
                split.usedCount = i < usedSlots ? 1 : 0;
                awardDao.insert(split);
            }
            awardDao.delete(award);
        }
    }

    /** Splits multi-count awards for all given cards. Call from a background thread. */
    public void splitMultiCountAwardsSync(List<Long> userCardIds) {
        if (userCardIds == null) return;
        for (long id : userCardIds) splitMultiCountAwardsSync(id);
    }

    /** Toggle used/unused for a free night award. Call from background thread. */
    public void toggleUsedSync(long awardId) {
        FreeNightAward award = awardDao.getByIdSync(awardId);
        if (award == null) return;
        if (award.usedCount >= award.totalCount) {
            award.usedCount = 0;
        } else {
            award.usedCount = award.totalCount;
        }
        awardDao.update(award);
    }

    public void toggleUsed(long awardId, Runnable onComplete) {
        executor.execute(() -> {
            toggleUsedSync(awardId);
            if (onComplete != null) onComplete.run();
        });
    }

    // ── Valuations ────────────────────────────────────────────────────────────

    public LiveData<List<FreeNightValuation>> getAllValuations() {
        return valuationDao.getAll();
    }

    public List<FreeNightValuation> getAllValuationsSync() {
        return valuationDao.getAllSync();
    }

    public FreeNightValuation getValuationSync(String typeKey) {
        return valuationDao.getByTypeKeySync(typeKey);
    }

    public void updateValuation(FreeNightValuation valuation) {
        executor.execute(() -> valuationDao.update(valuation));
    }

    public void resetValuationToDefault(String typeKey) {
        executor.execute(() -> {
            FreeNightValuation val = valuationDao.getByTypeKeySync(typeKey);
            if (val == null) return;
            val.valueCents = val.defaultValueCents;
            valuationDao.update(val);
        });
    }
}

package com.example.ccrewards.data.repository;

import androidx.lifecycle.LiveData;

import com.example.ccrewards.data.db.dao.PointValuationDao;
import com.example.ccrewards.data.db.dao.RewardRateDao;
import com.example.ccrewards.data.model.PointValuation;
import com.example.ccrewards.data.model.RewardRate;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RewardRateRepository {

    private final RewardRateDao rewardRateDao;
    private final PointValuationDao pointValuationDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public RewardRateRepository(RewardRateDao rewardRateDao, PointValuationDao pointValuationDao) {
        this.rewardRateDao = rewardRateDao;
        this.pointValuationDao = pointValuationDao;
    }

    public LiveData<List<RewardRate>> getAllRates() {
        return rewardRateDao.getAllRates();
    }

    public LiveData<List<RewardRate>> getRatesForCard(String cardId) {
        return rewardRateDao.getRatesForCard(cardId);
    }

    public List<RewardRate> getRatesForCardSync(String cardId) {
        return rewardRateDao.getRatesForCardSync(cardId);
    }

    public List<RewardRate> getAllRatesSync() {
        return rewardRateDao.getAllRatesSync();
    }

    public void updateRate(RewardRate rate) {
        executor.execute(() -> rewardRateDao.update(rate));
    }

    public void insertRate(RewardRate rate) {
        executor.execute(() -> rewardRateDao.insert(rate));
    }

    public void resetCustomizations(String cardId) {
        executor.execute(() -> rewardRateDao.resetCustomizationsForCard(cardId));
    }

    // ── Point Valuations ─────────────────────────────────────────────────────

    public LiveData<List<PointValuation>> getAllValuations() {
        return pointValuationDao.getAllValuations();
    }

    public void updateValuation(PointValuation valuation) {
        executor.execute(() -> pointValuationDao.update(valuation));
    }

    public void resetValuationToDefault(String currencyName) {
        executor.execute(() -> pointValuationDao.resetToDefault(currencyName));
    }

    /** Synchronous read for ViewModel computation — call from background thread. */
    public PointValuation getValuationSync(String currencyName) {
        return pointValuationDao.getValuationSync(currencyName);
    }

    public List<PointValuation> getAllValuationsSync() {
        return pointValuationDao.getAllValuationsSync();
    }
}

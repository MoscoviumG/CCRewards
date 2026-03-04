package com.example.ccrewards.data.repository;

import androidx.lifecycle.LiveData;

import com.example.ccrewards.data.db.dao.WelcomeBonusDao;
import com.example.ccrewards.data.model.WelcomeBonus;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WelcomeBonusRepository {

    private final WelcomeBonusDao dao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public WelcomeBonusRepository(WelcomeBonusDao dao) {
        this.dao = dao;
    }

    public LiveData<WelcomeBonus> getLiveForCard(long userCardId) {
        return dao.getByUserCard(userCardId);
    }

    public LiveData<List<WelcomeBonus>> getActiveLive() {
        return dao.getActiveLive();
    }

    /** Sync — call only from a background thread. */
    public List<WelcomeBonus> getActiveSync() {
        return dao.getActiveSync();
    }

    public void upsert(WelcomeBonus wb) {
        executor.execute(() -> dao.upsert(wb));
    }

    public void delete(WelcomeBonus wb) {
        executor.execute(() -> dao.delete(wb));
    }

    public void markAchieved(long userCardId) {
        executor.execute(() -> dao.markAchieved(userCardId));
    }
}

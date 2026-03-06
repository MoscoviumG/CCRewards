package com.example.ccrewards.data.repository;

import androidx.lifecycle.LiveData;

import com.example.ccrewards.data.db.AppDatabase;
import com.example.ccrewards.data.db.dao.RotationalBonusCategoryDao;
import com.example.ccrewards.data.db.dao.RotationalBonusDao;
import com.example.ccrewards.data.model.RotationalBonus;
import com.example.ccrewards.data.model.RotationalBonusCategory;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RotationalBonusRepository {

    private final AppDatabase db;
    private final RotationalBonusDao bonusDao;
    private final RotationalBonusCategoryDao categoryDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public RotationalBonusRepository(AppDatabase db,
                                     RotationalBonusDao bonusDao,
                                     RotationalBonusCategoryDao categoryDao) {
        this.db = db;
        this.bonusDao = bonusDao;
        this.categoryDao = categoryDao;
    }

    public LiveData<List<RotationalBonus>> getActiveLive() {
        return bonusDao.getActiveLive(LocalDate.now().toEpochDay());
    }

    public List<RotationalBonus> getActiveSync() {
        return bonusDao.getActiveSync(LocalDate.now().toEpochDay());
    }

    public LiveData<List<RotationalBonus>> getForUserCard(long userCardId) {
        return bonusDao.getForUserCard(userCardId);
    }

    public List<RotationalBonus> getForUserCardSync(long userCardId) {
        return bonusDao.getForUserCardSync(userCardId);
    }

    public List<RotationalBonusCategory> getCategoriesForBonusSync(long bonusId) {
        return categoryDao.getForBonusSync(bonusId);
    }

    public List<RotationalBonusCategory> getAllCategoriesSync() {
        return categoryDao.getAllSync();
    }

    public List<RotationalBonusCategory> getCategoriesForUserCardSync(long userCardId) {
        return categoryDao.getForUserCardSync(userCardId);
    }

    public void insert(RotationalBonus bonus, List<RotationalBonusCategory> categories,
                       Runnable onComplete) {
        executor.execute(() -> {
            // Run inside a transaction so Room fires the LiveData invalidation only once,
            // after both the bonus row AND all category rows are committed. Without this,
            // the LiveData triggers after the bonus insert but before the categories are
            // written, causing recompute() to see an empty category list.
            db.runInTransaction(() -> {
                long newId = bonusDao.insert(bonus);
                for (RotationalBonusCategory cat : categories) {
                    cat.rotationalBonusId = newId;
                    categoryDao.insert(cat);
                }
            });
            if (onComplete != null) onComplete.run();
        });
    }

    public void updateUsed(long bonusId, int usedCents, int limitCents) {
        executor.execute(() -> {
            RotationalBonus rb = bonusDao.getByIdSync(bonusId);
            if (rb == null) return;
            rb.usedCents = usedCents;
            rb.isFullyUsed = limitCents > 0 && usedCents >= limitCents;
            bonusDao.update(rb);
        });
    }

    public void markFullyUsed(long bonusId) {
        executor.execute(() -> {
            RotationalBonus rb = bonusDao.getByIdSync(bonusId);
            if (rb == null) return;
            rb.isFullyUsed = true;
            rb.usedCents = rb.spendLimitCents;
            bonusDao.update(rb);
        });
    }

    public void delete(long bonusId) {
        executor.execute(() -> {
            RotationalBonus rb = bonusDao.getByIdSync(bonusId);
            if (rb == null) return;
            categoryDao.deleteForBonus(bonusId);
            bonusDao.delete(rb);
        });
    }
}

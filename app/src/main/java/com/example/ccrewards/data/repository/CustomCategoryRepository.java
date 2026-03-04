package com.example.ccrewards.data.repository;

import androidx.lifecycle.LiveData;

import com.example.ccrewards.data.db.dao.CustomCategoryDao;
import com.example.ccrewards.data.db.dao.CustomCategoryRateDao;
import com.example.ccrewards.data.model.CustomCategory;
import com.example.ccrewards.data.model.CustomCategoryRate;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CustomCategoryRepository {

    private final CustomCategoryDao categoryDao;
    private final CustomCategoryRateDao rateDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public CustomCategoryRepository(CustomCategoryDao categoryDao, CustomCategoryRateDao rateDao) {
        this.categoryDao = categoryDao;
        this.rateDao = rateDao;
    }

    public LiveData<List<CustomCategory>> getAllCustomCategoriesLive() {
        return categoryDao.getAllLive();
    }

    public List<CustomCategory> getAllCustomCategoriesSync() {
        return categoryDao.getAllSync();
    }

    public List<CustomCategoryRate> getAllRatesSync() {
        return rateDao.getAllSync();
    }

    public List<CustomCategoryRate> getRatesForCategorySync(long catId) {
        return rateDao.getRatesForCategorySync(catId);
    }

    public List<CustomCategoryRate> getRatesForCardSync(String cardDefinitionId) {
        return rateDao.getRatesForCardSync(cardDefinitionId);
    }

    /** Inserts a new category on a background thread; posts the new id to callback. */
    public void insertCategory(String name, java.util.function.Consumer<Long> onInserted) {
        int sortOrder = 0; // append at end; reordering not supported in v1
        executor.execute(() -> {
            long newId = categoryDao.insert(new CustomCategory(name, sortOrder));
            if (onInserted != null) onInserted.accept(newId);
        });
    }

    public void updateCategory(CustomCategory category) {
        executor.execute(() -> categoryDao.update(category));
    }

    /** Sync — call only from a background thread. */
    public void updateCategorySync(CustomCategory category) {
        categoryDao.update(category);
    }

    public void deleteCategory(long catId) {
        executor.execute(() -> {
            rateDao.deleteRatesForCategory(catId);
            categoryDao.deleteById(catId);
        });
    }

    /** Sync — call only from a background thread. */
    public void deleteCategorySync(long catId) {
        rateDao.deleteRatesForCategory(catId);
        categoryDao.deleteById(catId);
    }

    public void upsertRate(CustomCategoryRate rate) {
        executor.execute(() -> rateDao.upsert(rate));
    }

    /** Sync — call only from a background thread. */
    public void upsertRateSync(CustomCategoryRate rate) {
        rateDao.upsert(rate);
    }

    public void deleteRateForCard(long catId, String cardId) {
        executor.execute(() -> rateDao.deleteRateForCard(catId, cardId));
    }

    /** Sync — call only from a background thread. */
    public void deleteRateForCardSync(long catId, String cardId) {
        rateDao.deleteRateForCard(catId, cardId);
    }

    public void deleteAllRatesForCategory(long catId) {
        executor.execute(() -> rateDao.deleteRatesForCategory(catId));
    }

    /** Sync — call only from a background thread. Removes all custom-category rates for this card. */
    public void deleteAllRatesForCardSync(String cardDefinitionId) {
        rateDao.deleteAllForCard(cardDefinitionId);
    }
}

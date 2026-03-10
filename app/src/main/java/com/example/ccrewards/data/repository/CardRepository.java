package com.example.ccrewards.data.repository;

import androidx.lifecycle.LiveData;

import com.example.ccrewards.data.db.dao.*;
import com.example.ccrewards.data.model.*;
import com.example.ccrewards.data.model.relations.UserCardWithDetails;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CardRepository {

    private final CardDefinitionDao cardDefinitionDao;
    private final RewardRateDao rewardRateDao;
    private final CardBenefitDao cardBenefitDao;
    private final UserCardDao userCardDao;
    private final UserCardChoiceCategoryDao choiceCategoryDao;
    private final ProductChangeRecordDao productChangeRecordDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public CardRepository(CardDefinitionDao cardDefinitionDao,
                          RewardRateDao rewardRateDao,
                          CardBenefitDao cardBenefitDao,
                          UserCardDao userCardDao,
                          UserCardChoiceCategoryDao choiceCategoryDao,
                          ProductChangeRecordDao productChangeRecordDao) {
        this.cardDefinitionDao = cardDefinitionDao;
        this.rewardRateDao = rewardRateDao;
        this.cardBenefitDao = cardBenefitDao;
        this.userCardDao = userCardDao;
        this.choiceCategoryDao = choiceCategoryDao;
        this.productChangeRecordDao = productChangeRecordDao;
    }

    // ── Card Definitions ──────────────────────────────────────────────────────

    public LiveData<List<CardDefinition>> getAllCardDefinitions() {
        return cardDefinitionDao.getAllCards();
    }

    public LiveData<List<CardDefinition>> getPersonalCardDefinitions() {
        return cardDefinitionDao.getPersonalCards();
    }

    public LiveData<List<CardDefinition>> getBusinessCardDefinitions() {
        return cardDefinitionDao.getBusinessCards();
    }

    public List<CardDefinition> getAllCardDefinitionsSync() {
        return cardDefinitionDao.getAllCardsSync();
    }

    public CardDefinition getCardDefinitionSync(String id) {
        return cardDefinitionDao.getCardById(id);
    }

    public List<String> getActiveCardDefinitionIdsSync() {
        return userCardDao.getActiveCardDefinitionIdsSync();
    }

    public List<String> getNonDormantCardDefinitionIdsSync() {
        return userCardDao.getNonDormantCardDefinitionIdsSync();
    }

    public List<com.example.ccrewards.data.model.UserCard> getAllActiveUserCardsSync() {
        return userCardDao.getAllActiveUserCardsSync();
    }

    public List<com.example.ccrewards.data.model.UserCard> getNonDormantActiveUserCardsSync() {
        return userCardDao.getNonDormantActiveUserCardsSync();
    }

    public LiveData<List<CardDefinition>> searchCardDefinitions(String query) {
        return cardDefinitionDao.searchCards(query);
    }

    public LiveData<List<CardDefinition>> getCardsByIssuer(String issuer) {
        return cardDefinitionDao.getCardsByIssuer(issuer);
    }

    public void insertCardDefinition(CardDefinition card) {
        executor.execute(() -> cardDefinitionDao.insert(card));
    }

    public void updateCardDefinition(CardDefinition card) {
        executor.execute(() -> cardDefinitionDao.update(card));
    }

    public void deleteCardDefinition(CardDefinition card) {
        executor.execute(() -> cardDefinitionDao.delete(card));
    }

    // ── Rates & Benefits ──────────────────────────────────────────────────────

    public LiveData<List<RewardRate>> getRatesForCard(String cardDefinitionId) {
        return rewardRateDao.getRatesForCard(cardDefinitionId);
    }

    public LiveData<List<CardBenefit>> getBenefitsForCard(String cardDefinitionId) {
        return cardBenefitDao.getBenefitsForCard(cardDefinitionId);
    }

    // ── User Cards ────────────────────────────────────────────────────────────

    public LiveData<List<UserCardWithDetails>> getActiveUserCards() {
        return userCardDao.getActiveCardsWithDetails();
    }

    public LiveData<List<UserCardWithDetails>> getNonDormantActiveUserCards() {
        return userCardDao.getNonDormantActiveCardsWithDetails();
    }

    public void setDormant(long userCardId, boolean isDormant) {
        executor.execute(() -> userCardDao.setDormant(userCardId, isDormant ? 1 : 0));
    }

    public LiveData<List<UserCardWithDetails>> getAllUserCards() {
        return userCardDao.getAllCardsWithDetails();
    }

    public LiveData<UserCardWithDetails> getUserCardWithDetails(long id) {
        return userCardDao.getCardWithDetails(id);
    }

    /** Synchronous lookup — call from background thread only. */
    public com.example.ccrewards.data.model.UserCard getUserCardByIdSync(long id) {
        return userCardDao.getCardByIdSync(id);
    }

    public void addUserCard(UserCard card, Runnable onComplete) {
        addUserCard(card, ignored -> { if (onComplete != null) onComplete.run(); });
    }

    public void addUserCard(UserCard card, java.util.function.LongConsumer onComplete) {
        executor.execute(() -> {
            Integer maxOrder = userCardDao.getMaxSortOrder();
            card.sortOrder = (maxOrder == null ? 0 : maxOrder + 1);
            long newId = userCardDao.insert(card);
            if (onComplete != null) onComplete.accept(newId);
        });
    }

    public void updateUserCard(UserCard card) {
        executor.execute(() -> userCardDao.update(card));
    }

    public void deleteUserCard(UserCard card) {
        executor.execute(() -> userCardDao.delete(card));
    }

    public void productChangeCard(long userCardId, String fromCardId, String toCardId, ProductChangeRecord record) {
        executor.execute(() -> {
            userCardDao.updateCardDefinition(userCardId, toCardId);
            rewardRateDao.clearCustomizedRates(toCardId);
            productChangeRecordDao.insert(record);
        });
    }

    // ── Product Change History ────────────────────────────────────────────────

    public LiveData<List<ProductChangeRecord>> getProductChangeHistory(long userCardId) {
        return productChangeRecordDao.getHistoryForCard(userCardId);
    }

    public void deleteProductChangeRecord(ProductChangeRecord record) {
        executor.execute(() -> productChangeRecordDao.delete(record));
    }

    public void updateProductChangeRecord(ProductChangeRecord record) {
        executor.execute(() -> productChangeRecordDao.insert(record));
    }

    // ── Choice Categories ─────────────────────────────────────────────────────

    public LiveData<List<UserCardChoiceCategory>> getChoicesForCard(long userCardId) {
        return choiceCategoryDao.getChoicesForCard(userCardId);
    }

    public void saveChoiceCategory(UserCardChoiceCategory choice) {
        executor.execute(() -> choiceCategoryDao.insert(choice));
    }

    public void deleteChoicesForCard(long userCardId) {
        executor.execute(() -> choiceCategoryDao.deleteAllForCard(userCardId));
    }
}
